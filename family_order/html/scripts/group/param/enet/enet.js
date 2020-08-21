function validateParamPage(methodName) {
	if(methodName=='CrtMb'||methodName=='ChgMb') {
	    var ip_address = $("#pam_NOTIN_IP_ADDRESS").val();
	    if(!$.verifylib.checkIp(ip_address)){
	    	alert("IP地址格式不正确请重新输入！");
	        return false;
	    }
	}
    return true;
}