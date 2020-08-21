//此JS是对createpersonuser.js的扩展,一般适用于本地的特殊处理
if(typeof(CreatePersonUserExtend)=="undefined"){window["CreatePersonUserExtend"]=function(){};var createPersonUserExtend = new CreatePersonUserExtend();}
(function(){
	$.extend(CreatePersonUserExtend.prototype,{
		
		afterChangeProduct: function(param){
			createPersonUserExtend.createPersonUserTipsInfo(param);
		},
		
		createPersonUserTipsInfo: function(param){//选择产品后的相关提示信息
			$.ajax.submit(null, 'createPersonUserTipsInfo', param, null,
				function(data) 
				{
					$.endPageLoading();
					var resultCode = data.get("resultCode");
					var resultInfo = data.get("resultInfo");

					var walerTipCode = data.get("walerTipCode");
					var walerTipInfo = data.get("walerTipInfo");
			
					if(resultCode == "0"){

						 MessageBox.alert('提示',resultInfo);
					}

					else if(walerTipCode == "0"){
						MessageBox.alert('提示',walerTipInfo);
					}
				}, function(error_code, error_info) 
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
					result = false ;
				}
			);
		}
		
	});
})();