function validateParamPage(methodName) {
	if(methodName=='CrtUs'||methodName=='ChgUs'){
		var managerNo = getElementValue("pam_CUST_MANAGER");
		if(managerNo == null || managerNo == "")	{
			alert("\u5BA2\u6237\u7ECF\u7406\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
			return false;
		}
	}
	
	return true;
}