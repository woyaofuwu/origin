(function($){
	$.extend({HintInfo:{
		renderComponent:function(param){
			
			$.ajax.submit(null,null,param,"HintListPart",function(data){
				
			},function(code,info){
				MessageBox.alert("提示信息",info);
			});
		},
		clear:function(){
			var param = "&ACTION=clear";
			$.HintInfo.renderComponent(param);
		}
	}})
	
	window.triggerPushInfos=$.HintInfo.renderComponent;
})(Wade);