
function validateParamPage(methodName) {
	if(methodName=='CrtUs'||methodName=='ChgUs') {
	    var ip_address = $("#pam_NOTIN_MGR_PHONE").val();
	    if(!$.verifylib.checkMbphone(ip_address)){
	    	//alert("电话号码格式不正确请重新输入！");
	    	$.validate.alerter.one($("#pam_NOTIN_MGR_PHONE")[0], "电话号码格式不正确请重新输入！");
	        return false;
	    }
	}
	
    return true;
}
//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}

