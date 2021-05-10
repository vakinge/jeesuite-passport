package com.jeesuite.passport.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.component.saml.SAMLBuilder;
import com.jeesuite.passport.component.saml.SAMLPrincipal;
import com.jeesuite.passport.dao.entity.UserPrincipalEntity;
import com.jeesuite.passport.dto.AuthUserDetails;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;

@Controller
@RequestMapping(value = "/auth/saml2")
public class SamlLoginController extends BaseLoginController {

	static Pattern chineseCharPattern = Pattern.compile("[\u4e00-\u9fa5]+");
	
	@GetMapping("/login")
	public String toLoginPage(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserSession session = SecurityDelegating.getCurrentSession();
		String SAMLRequest = request.getParameter("SAMLRequest");
		if(session == null || session.isAnonymous()){
			model.addAttribute("SAMLRequest", SAMLRequest);
			return "saml/login";
		}
		
		AuthnRequest samlRequestObject = processRequest(SAMLRequest, request);
		
		UserPrincipalEntity userPrincipal = userService.findUserById(session.getUserId());
		String outSAMLResponse = prepareResponse(samlRequestObject,userPrincipal.getMobile(),userPrincipal.getRealname());
		String base64Encoded = Base64.encodeBytes(outSAMLResponse.getBytes(), Base64.DONT_BREAK_LINES);
		model.addAttribute("spConsumerUrl", samlRequestObject.getAssertionConsumerServiceURL());
		model.addAttribute("SAMLResponse", base64Encoded);
		model.addAttribute("RelayState", samlRequestObject.getID());
		
		return "saml/redirect";
	}

	private AuthnRequest processRequest(String SAMLRequest, HttpServletRequest request)
			throws ParserConfigurationException, UnmarshallingException, SAXException, IOException {
		byte[] decodedSAMLRequestBytes = Base64.decode(SAMLRequest);
		SAMLRequest = new String(decodedSAMLRequestBytes, "UTF-8");
		if (request.getHeader("Accept-Encoding").contains("deflate")) {
			try {
				Inflater inflater = new Inflater(true);
				inflater.setInput(decodedSAMLRequestBytes);
				byte[] xmlMessageBytes = new byte[5000];
				int resultLength = inflater.inflate(xmlMessageBytes);
				inflater.end();
				SAMLRequest = new String(xmlMessageBytes, 0, resultLength, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ByteArrayInputStream is = new ByteArrayInputStream(SAMLRequest.getBytes("UTF-8"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(is);
		Element element = document.getDocumentElement();
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
		XMLObject responseXmlObj = unmarshaller.unmarshall(element);
		AuthnRequest samlRequestObject = (AuthnRequest) responseXmlObj;
		SAMLRequest = samlRequestObject.toString();
		return samlRequestObject;
	}

	@PostMapping("/login")
	public String login2(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String SAMLRequest = request.getParameter("SAMLRequest");
		if(StringUtils.isBlank(SAMLRequest)){
			model.addAttribute(AppConstants.ERROR, "SAMLRequest不能为空");
			return "error";
		}
		AuthnRequest samlRequestObject = processRequest(SAMLRequest, request);

		AuthUserDetails userDetails = validateUserFromRequestParam(request);
	
		if(StringUtils.isBlank(userDetails.getMobile())){
			model.addAttribute(AppConstants.ERROR, "该账号未登记手机号码");
			return "error";
		}
		
		String outSAMLResponse = prepareResponse(samlRequestObject,userDetails.getMobile(),userDetails.getRealname());
		String base64Encoded = Base64.encodeBytes(outSAMLResponse.getBytes(), Base64.DONT_BREAK_LINES);
		model.addAttribute("spConsumerUrl", samlRequestObject.getAssertionConsumerServiceURL());
		model.addAttribute("SAMLResponse", base64Encoded);
		model.addAttribute("RelayState", samlRequestObject.getID());
		return "saml/redirect";
	}

	public String prepareResponse(AuthnRequest authRequest,String realName,String mobile) throws Exception {

		if(StringUtils.isAnyBlank(realName,mobile)) {
			throw new JeesuiteBaseException("手机或姓名为空");
		}
		
		SAMLPrincipal principal = new SAMLPrincipal();
		principal.setNameID(mobile);
		principal.setRequestID(authRequest.getID());
		principal.setAssertionConsumerServiceURL(authRequest.getAssertionConsumerServiceURL());
		principal.setServiceProviderEntityID(authRequest.getIssuer().getValue());
		
		String firstName = realName.substring(0, 1);
		String lastName = realName.substring(1);
		principal.getAttributes().put("FirstName", Collections.singletonList(firstName));
		principal.getAttributes().put("LastName", Collections.singletonList(lastName));

		return SAMLBuilder.buildResponse(principal);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata", produces = "application/xml")
	public @ResponseBody String metadata(HttpServletRequest request) throws Exception {
		EntityDescriptor entityDescriptor = SAMLBuilder.buildIDPEntityDescriptor();
		return writeEntityDescriptor(entityDescriptor);
	}

	private String writeEntityDescriptor(EntityDescriptor entityDescriptor)
			throws ParserConfigurationException, MarshallingException, TransformerException {
		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(entityDescriptor);
		Element element = marshaller.marshall(entityDescriptor);
		return XMLHelper.nodeToString(element);
	}



}
