//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,topSetBoxInfoPart',
			function(data) {
			//根据需求编号REQ201709080021 这里去掉只有交了押金的才显示退还机顶盒的检验
				//var isHasFee=data.get("IS_HAS_FEE");
				//if(isHasFee=="1"){		//显示是否退还机顶盒
					$("#isBackSet").css("display","block");
				//}
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
//	if($("#RES_ID").val()!=$("#RES_NO").val()){
//		alert("终端串不一致，请重新校验！");
//		return false;
//	}
//	if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
//		return false;
//	}
//	var isReturnTopsetbox=$("IS_RETURN_TOPSETBOX");
//	if(!isReturnTopsetbox||isReturnTopsetbox==""){
//		alert("请选择是否退还机顶盒！");
//		return false;
//	}
//	var isHasFee=$("#IS_HAS_FEE").val();
//	if(isHasFee=="1"){
		if(!$.validate.verifyAll("submitPart")||!$.validate.verifyAll("submitPart")) {
			return false;
		}
//	}
	return true;
}