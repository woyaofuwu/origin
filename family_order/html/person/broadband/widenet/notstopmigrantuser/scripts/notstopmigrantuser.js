function regNotStop(obj)
{
	var user=$.auth.getAuthData().get("USER_INFO");
	var serialNumber = user.get("SERIAL_NUMBER");
	var userId = user.get("USER_ID");
	var eparchyCode = user.get("EPARCHY_CODE");
	var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;

	$.beginPageLoading("正在提交数据...");
	$.ajax.submit('AuthPart', 'onTradeSubmit', param, 'MigrantUserState', function(data){
		$.endPageLoading();
		var title = "业务受理成功";
		if(data){
			$("#REG_BUTTON").attr("disabled", true);
			var dataInsertResult = data.get("DATA_INSERT_RESULT");
			if(dataInsertResult=="true"){
				MessageBox.success(title,title,null,null,null);
			}else{
				title = "业务受理失败";
				MessageBox.error(title,title,null,null,null);
			}
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	   });
}

﻿//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var userId = data.get("USER_INFO").get("USER_ID");
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;

	$.ajax.submit('AuthPart', 'queryInfo', param, 'MigrantUserState', function(data){
	 	if(data){
			var regButtonState = data.get("REG_BUTTON_STATE");
			if(regButtonState=="0"){
				$("#REG_BUTTON").attr("disabled", false);
			}else{
				$("#REG_BUTTON").attr("disabled", true);
			}
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}