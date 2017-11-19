//拓展方法
;(function($) {
	$.extend($,{
		success : function(msg){
		  $.tip({content:msg, icon:'success'});
	    },
	    alert : function(msg){
	    	$.tip({content:msg, icon:'alert'});
	    },
		error : function(msg){
	    	$.tip({content:msg, icon:'error'});
	    }
	});
})(jQuery);

/**
 * 页面初始化通用方法
 */
$(document).ready(function(){
	$.commons.init();
}); 
;(function($){
	$.commons = {
			init:function(){
				$.commons.initAjaxFormSubmit();
				$.commons.initCalendar();
				$.commons.initUploadify();
				//ajax全局设置
				$.ajaxSetup({
				   cache: false,
				   global: true,
				   type: "POST",
				   dataType: "json"
				});
			},
			//初始化日历
            initCalendar: function(){},
			initUploadify: function(){
				$("*[data-component='uploadify']").each(function(i){
					var $this = $(this),$preview = $('img.J_preview',$this),
					     $fileInput = $('.J_upload',$this),
					     $valout = $('input.J_val',$this),
					     uploadUrl = $this.attr('data-uri');
					$fileInput.ajaxUploader({
				        action_url: uploadUrl,
				        input_name: 'img',
				        onSubmit: function(id, fileName){},
				        onFinish: function(fileName){
						  $preview.attr('src',APP.root+'/attachs/'+fileName);
						  $preview.attr('data-bimg',APP.root+'/attachs/'+fileName);
						  $valout.val(fileName);
				        }
				    });
				});
			},
			initAjaxFormSubmit:function(){
				var $this = $(this),$form = $this.parent(),callback = $this.attr('onSuccessCallback'),jumpUrl = $this.attr('onSuccessJumpurl');
				while(!$form.is('form')){
					$form = $form.parent();
				}
				var params = {};
				var dataArrays = $form.serializeArray();
				if(dataArrays){
					$.each( dataArrays, function(i, field){
						if(field.value && field.value != ''){					
							if(params[field.name]){
								params[field.name] = params[field.name] + ',' + field.value;
							}else{					
								params[field.name] = field.value;
							}
						}
					});
				}
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
				        	 $.success(data.msg || '操作成功');
				             data = data.data;
				             if(callback != undefined){
							    eval(callback+"(data)");
				             }
				             if(jumpUrl){
				            	 setTimeout(function(){window.location.href = jumpUrl;},500);
							 }
				          }else{
				        	 $this.removeAttr('disabled');
				        	 $.error(data.msg);
				          }
				        },
					error: function(xhr, type){
						$this.removeAttr('disabled');
						$.error('系统错误');
					}
				});
			}
	};
})(jQuery);

;(function($) {
	$.merchant = $.merchant || {version : "v1.0.0"};
	$.extend($.merchant, {
		isLogin : function() {},
		tuan:{}
	});
})(jQuery);


