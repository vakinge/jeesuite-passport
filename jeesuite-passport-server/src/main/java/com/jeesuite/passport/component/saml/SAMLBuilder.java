package com.jeesuite.passport.component.saml;

import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jeesuite.common.util.ResourceUtils;


/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2018年5月22日
 */
public class SAMLBuilder {

	public static final String NAMEID_FORMAT = NameIDType.UNSPECIFIED;
	private static final XMLObjectBuilderFactory builderFactory;
	private static EntityDescriptor entityDescriptor;
	
	private static String entityId;
	private static String baseUrl;
	static {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		Security.addProvider(new BouncyCastleProvider());
		builderFactory = Configuration.getBuilderFactory();
		entityId = ResourceUtils.getProperty("saml.idp.entity-id");
		baseUrl = ResourceUtils.getAndValidateProperty("server.base-url");
	}
	
	@SuppressWarnings({ "unchecked" })
	public static <T> T buildSAMLObject(final Class<T> objectClass, QName qName) {
		return (T) builderFactory.getBuilder(qName).buildObject(qName);
	}
	
	public static EntityDescriptor buildIDPEntityDescriptor(){
		if (entityDescriptor != null){
			entityDescriptor.setID(randomSAMLId());
		    entityDescriptor.setValidUntil(new DateTime().plusMillis(86400000));			
			return entityDescriptor;
		}
		try {
			entityDescriptor = buildSAMLObject(EntityDescriptor.class, EntityDescriptor.DEFAULT_ELEMENT_NAME);
		    entityDescriptor.setEntityID(entityId);
		    entityDescriptor.setID(randomSAMLId());
		    entityDescriptor.setValidUntil(new DateTime().plusMillis(86400000));

		    Signature signature = buildSAMLObject(Signature.class, Signature.DEFAULT_ELEMENT_NAME);

		    Credential credential = JKSKeyManager.getSigningCredential();
		    signature.setSigningCredential(credential);
		    signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		    signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

		    entityDescriptor.setSignature(signature);

		    Configuration.getMarshallerFactory().getMarshaller(entityDescriptor).marshall(entityDescriptor);
		    Signer.signObject(signature);

		    IDPSSODescriptor idpssoDescriptor = buildSAMLObject(IDPSSODescriptor.class, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

		    NameIDFormat nameIDFormat = buildSAMLObject(NameIDFormat.class, NameIDFormat.DEFAULT_ELEMENT_NAME);
		    nameIDFormat.setFormat(NAMEID_FORMAT);
		    idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

		    idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

		    SingleSignOnService singleSignOnService = buildSAMLObject(SingleSignOnService.class, SingleSignOnService.DEFAULT_ELEMENT_NAME);
		    singleSignOnService.setLocation(baseUrl + "/saml2/login");
		    singleSignOnService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

		    idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

		    X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
		    keyInfoGeneratorFactory.setEmitEntityCertificate(true);
		    KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();
		    //
		    KeyDescriptor encKeyDescriptor = buildSAMLObject(KeyDescriptor.class, KeyDescriptor.DEFAULT_ELEMENT_NAME);
		    encKeyDescriptor.setUse(UsageType.SIGNING);
		    encKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
		    idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);
		    //
		    encKeyDescriptor = buildSAMLObject(KeyDescriptor.class, KeyDescriptor.DEFAULT_ELEMENT_NAME);
		    encKeyDescriptor.setUse(UsageType.ENCRYPTION);
		    encKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
		    idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);

		    entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);
		    
		    return entityDescriptor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Issuer buildIssuer(String issuingEntityName) {
		Issuer issuer = buildSAMLObject(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue(issuingEntityName);
		issuer.setFormat(NameIDType.ENTITY);
		return issuer;
	}

	private static Subject buildSubject(String subjectNameId, String subjectNameIdType, String recipient,
			String inResponseTo) {
		NameID nameID = buildSAMLObject(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
		nameID.setValue(subjectNameId);
		nameID.setFormat(subjectNameIdType);

		Subject subject = buildSAMLObject(Subject.class, Subject.DEFAULT_ELEMENT_NAME);
		subject.setNameID(nameID);

		SubjectConfirmation subjectConfirmation = buildSAMLObject(SubjectConfirmation.class,
				SubjectConfirmation.DEFAULT_ELEMENT_NAME);
		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

		SubjectConfirmationData subjectConfirmationData = buildSAMLObject(SubjectConfirmationData.class,
				SubjectConfirmationData.DEFAULT_ELEMENT_NAME);

		subjectConfirmationData.setRecipient(recipient);
		subjectConfirmationData.setInResponseTo(inResponseTo);
		subjectConfirmationData.setNotOnOrAfter(new DateTime().plusMinutes(8 * 60));
		// subjectConfirmationData.setAddress(recipient);

		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

		subject.getSubjectConfirmations().add(subjectConfirmation);

		return subject;
	}

	public static Status buildStatus(String value) {
		Status status = buildSAMLObject(Status.class, Status.DEFAULT_ELEMENT_NAME);
		StatusCode statusCode = buildSAMLObject(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
		statusCode.setValue(value);
		status.setStatusCode(statusCode);
		return status;
	}

	public static Status buildStatus(String value, String subStatus, String message) {
		Status status = buildStatus(value);

		StatusCode subStatusCode = buildSAMLObject(StatusCode.class, StatusCode.DEFAULT_ELEMENT_NAME);
		subStatusCode.setValue(subStatus);
		status.getStatusCode().setStatusCode(subStatusCode);

		StatusMessage statusMessage = buildSAMLObject(StatusMessage.class, StatusMessage.DEFAULT_ELEMENT_NAME);
		statusMessage.setMessage(message);
		status.setStatusMessage(statusMessage);

		return status;
	}

	public static Assertion buildAssertion(SAMLPrincipal principal, Status status, String entityId) throws ConfigurationException {
		Assertion assertion = buildSAMLObject(Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);

		if (status.getStatusCode().getValue().equals(StatusCode.SUCCESS_URI)) {
			Subject subject = buildSubject(principal.getNameID(), principal.getNameIDType(),
					principal.getAssertionConsumerServiceURL(), principal.getRequestID());
			assertion.setSubject(subject);
		}

		Issuer issuer = buildIssuer(entityId);

		Audience audience = buildSAMLObject(Audience.class, Audience.DEFAULT_ELEMENT_NAME);
		audience.setAudienceURI(principal.getServiceProviderEntityID());
		AudienceRestriction audienceRestriction = buildSAMLObject(AudienceRestriction.class,
				AudienceRestriction.DEFAULT_ELEMENT_NAME);
		audienceRestriction.getAudiences().add(audience);

		Conditions conditions = buildSAMLObject(Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
		conditions.getAudienceRestrictions().add(audienceRestriction);
		assertion.setConditions(conditions);

		AuthnStatement authnStatement = buildAuthnStatement(new DateTime(), entityId);

		assertion.setIssuer(issuer);
		assertion.getAuthnStatements().add(authnStatement);
		assertion.setID(randomSAMLId());
		assertion.setIssueInstant(new DateTime());
		
		if (principal.getAttributes() != null) {
			AttributeStatement attrStatement = buildSAMLObject(AttributeStatement.class, AttributeStatement.DEFAULT_ELEMENT_NAME);
			Set<String> keySet = principal.getAttributes().keySet();
			for (String key : keySet) {
				Attribute attr = buildStringAttribute(key, principal.getAttributes().get(key));
				attrStatement.getAttributes().add(attr);
			}
			assertion.getAttributeStatements().add(attrStatement);
		}

		return assertion;
	}

	public static void signAssertion(SignableXMLObject signableXMLObject, Credential signingCredential)
			throws MarshallingException, SignatureException {
		Signature signature = buildSAMLObject(Signature.class, Signature.DEFAULT_ELEMENT_NAME);

		signature.setSigningCredential(signingCredential);
		signature.setSignatureAlgorithm(
				Configuration.getGlobalSecurityConfiguration().getSignatureAlgorithmURI(signingCredential));
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

		signableXMLObject.setSignature(signature);

		Configuration.getMarshallerFactory().getMarshaller(signableXMLObject).marshall(signableXMLObject);
		Signer.signObject(signature);
	}

	public static Optional<String> getStringValueFromXMLObject(XMLObject xmlObj) {
		if (xmlObj instanceof XSString) {
			return Optional.ofNullable(((XSString) xmlObj).getValue());
		} else if (xmlObj instanceof XSAny) {
			XSAny xsAny = (XSAny) xmlObj;
			String textContent = xsAny.getTextContent();
			if (StringUtils.hasText(textContent)) {
				return Optional.of(textContent);
			}
			List<XMLObject> unknownXMLObjects = xsAny.getUnknownXMLObjects();
			if (!CollectionUtils.isEmpty(unknownXMLObjects)) {
				XMLObject xmlObject = unknownXMLObjects.get(0);
				if (xmlObject instanceof NameID) {
					NameID nameID = (NameID) xmlObject;
					return Optional.of(nameID.getValue());
				}
			}
		}
		return Optional.empty();
	}

	public static String randomSAMLId() {
		return "_" + UUID.randomUUID().toString();
	}

	private static AuthnStatement buildAuthnStatement(DateTime authnInstant, String entityID) {
		AuthnContextClassRef authnContextClassRef = buildSAMLObject(AuthnContextClassRef.class,
				AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);

		AuthenticatingAuthority authenticatingAuthority = buildSAMLObject(AuthenticatingAuthority.class,
				AuthenticatingAuthority.DEFAULT_ELEMENT_NAME);
		authenticatingAuthority.setURI(entityID);

		AuthnContext authnContext = buildSAMLObject(AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
		authnContext.setAuthnContextClassRef(authnContextClassRef);
		authnContext.getAuthenticatingAuthorities().add(authenticatingAuthority);

		AuthnStatement authnStatement = buildSAMLObject(AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
		authnStatement.setAuthnContext(authnContext);

		authnStatement.setAuthnInstant(authnInstant);

		return authnStatement;

	}
	
	public static Attribute buildStringAttribute(String name, List<String> values)
			throws ConfigurationException {
		SAMLObjectBuilder attrBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
		Attribute attr = (Attribute) attrBuilder.buildObject();
		attr.setName(name);

		// Set custom Attributes
		XMLObjectBuilder stringBuilder = builderFactory.getBuilder(XSString.TYPE_NAME);
		XSString attrValue;
		for (String value : values) {
			attrValue = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,XSString.TYPE_NAME);
			attrValue.setValue(value);
			attr.getAttributeValues().add(attrValue);
		}

		return attr;
	}


	public static String buildResponse(SAMLPrincipal principal) throws MarshallingException, SignatureException, ConfigurationException {
		Status status = buildStatus(StatusCode.SUCCESS_URI);

		String entityId = SAMLPrincipal.IDP_ENTITY_ID;
		Credential signingCredential = JKSKeyManager.getSigningCredential();

		Response authResponse = buildSAMLObject(Response.class, Response.DEFAULT_ELEMENT_NAME);
		Issuer issuer = buildIssuer(entityId);

		authResponse.setIssuer(issuer);
		authResponse.setID(SAMLBuilder.randomSAMLId());
		authResponse.setIssueInstant(new DateTime());
		authResponse.setInResponseTo(principal.getRequestID());

		Assertion assertion = buildAssertion(principal, status, entityId);
		signAssertion(assertion, signingCredential);

		authResponse.getAssertions().add(assertion);
		authResponse.setDestination(principal.getAssertionConsumerServiceURL());

		authResponse.setStatus(status);

		ResponseMarshaller marshaller = new ResponseMarshaller();
		Element plain = marshaller.marshall(authResponse);
		String samlResponse = XMLHelper.nodeToString(plain);
//		System.out.println("----------");
//		System.out.println(samlResponse);
//		System.out.println("----------");

		return samlResponse;
	}

}
