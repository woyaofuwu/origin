function validateParamPage(methodName) {
	if(methodName=='CrtMb'||methodName=='ChgMb') {
	    var ip_address = $("#pam_NOTIN_IP_ADDRESS").val();
	    if(!$.verifylib.checkIp(ip_address)){
	    	$.validate.alerter.one($("#pam_NOTIN_IP_ADDRESS")[0], "IP地址格式不正确请重新输入！");
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