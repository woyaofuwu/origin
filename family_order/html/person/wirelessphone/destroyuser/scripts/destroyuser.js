//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	 var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userId = data.get("USER_INFO").get("USER_ID");
	 var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	 var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;
	 
	 $.ajax.submit('AuthPart', 'setPageCustInfo', param, 'BusiInfoPart,HiddenPart,DestroyInfoPart', function(data){
			//MessageBox.confirm("提示",data.get("message"),afterTrade);
			$("#REMOVE_REASON_CODE").attr("disabled", false);
			$("#REMARK").attr("disabled", false);
			$("#QUERY_BTN").attr("disabled", false);
			$("#QUERY_REST").attr("disabled", false);
			$("#QUERY_BTN").attr("className", "e_button-page-ok");
			$("#QUERY_REST").attr("className", "e_button-page");
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}

//业务提交
function submitBeforeCheck()
{
	if(!$.validate.verifyAll("DestroyInfoPart")) {
		return false;
	}
	return true;
}