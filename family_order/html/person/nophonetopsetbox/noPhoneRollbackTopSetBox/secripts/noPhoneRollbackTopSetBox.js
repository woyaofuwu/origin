//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,topSetBoxInfoPart',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/**
 * 业务提交校验
 */
//function submitBeforeCheck()
//{
//	if($("#RES_ID").val()!=$("#RES_NO").val()){
//		alert("终端串不一致，请重新校验！");
//		return false;
//	}
//	if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
//		return false;
//	}
//	return true;
//}