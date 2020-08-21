//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	 var userStr = data.get("USER_INFO").toString();
	 var custStr = data.get("CUST_INFO").toString();
	 var acctStr = data.get("ACCT_INFO").toString();
	 var param = "&USER_INFO="+userStr+"&CUST_INFO="+custStr+"&ACCT_INFO="+acctStr;
	 
	 $.ajax.submit('AuthPart', 'setPageCustInfo', param, 'CustInfoPart,DestroyInfoPart', function(data){
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
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck()
{
	if(!$.validate.verifyAll("DestroyInfoPart")) 
	{
		return false;
	}
	return true;
}
