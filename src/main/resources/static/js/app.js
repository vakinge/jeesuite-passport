$(document).ready(function(){
	$('.date-picker').datetimepicker({
		  format: 'yyyy-mm-dd'
	});
	
	$('body').on('click','.J_ajaxSubmit',function(){
		var $this = $(this),$form = $this.parent(),callback = $this.attr('onSuccessCallback'),jumpUrl = $this.attr('onSuccessJumpurl');
		while(!$form.is('form')){
			$form = $form.parent();
		}
//		//验证
//		if(!$form.doFormValidator()){
//		  return;
//		}
		var params = oneplatform.serializeJson($form);
		$this.attr('disabled',true);
		var loading = layer.load();
		var requestURI = $form.attr('action');
		$.ajax({
			dataType:"json",
		    type: "POST",
	        url: requestURI,
	        contentType: "application/json",
	        data:JSON.stringify(params) ,
			complete: function(){layer.close(loading);},
			success: function(data){
				if(data.code==401){top.location.href = "/login.html";return;}
		        if(data.code==200){
		        	 oneplatform.success(data.msg || '操作成功');
		             data = data.data;
		             if(callback != undefined){
					    eval(callback+"(data)");
		             } else if(jumpUrl){
		            	 setTimeout(function(){window.location.href = jumpUrl;},500);
					 }else{		
						 parent.window.location.reload();
					 }
		             setTimeout(function(){parent.layer.closeAll();},1000);
		          }else{
		        	 $this.removeAttr('disabled');
		        	 oneplatform.error(data.msg);
		          }
		        },
			error: function(xhr, type){
				$this.removeAttr('disabled');
				oneplatform.error('系统错误');
			}
		});
	});
});


;(function($) {
	$.user = $.user || {version : "v1.0.0"};
	$.extend($.user, {
	  init: function(){},
	  isLogin : function() {},
	  
	});
})(jQuery);

