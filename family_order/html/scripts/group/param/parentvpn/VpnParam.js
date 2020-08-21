// 创建服务号码
function createSerialNumber(){
	
	if(!$.validate.verifyField($("#pam_WORK_TYPE_CODE"))) return false;
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	// 删除服务号码
	if(serialNumber != null && serialNumber != ""){
		var delData = $.DataMap();
		delData.put("RES_TYPE_CODE", "L");
		delData.put("RES_CODE",serialNumber);
		deleteRes(delData);
		
		$("#SERIAL_NUMBER").val("");
		$("#HIDDEN_SERIAL_NUMBER").val("");
	}
	
	// 生成服务号码
	$.beginPageLoading();
	var param = "&WORK_TYPE_CODE=" + $("#pam_WORK_TYPE_CODE").val() + "&GRP_USER_EPARCHYCODE=" + $("#GRP_USER_EPARCHYCODE").val()
				+ "&METHOD_NAME=createSerialNumber&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.parentvpn.UserParamInfo";

	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param, sucCreateSerialNumber, errCreateSerialNumber);
}

// 创建服务号码成功
function sucCreateSerialNumber(data){
	$.endPageLoading();
	
	var vpnNo = data.get("VPN_NO");
	
	var addData = $.DataMap();
	addData.put("RES_TYPE_CODE", "L");
	addData.put("RES_CODE", vpnNo);
	addData.put("CHECKED", "true");
	addData.put("DISABLED", "true");
	insertRes(addData);
	
	$("#SERIAL_NUMBER").val(vpnNo);
	$("#HIDDEN_SERIAL_NUMBER").val(vpnNo);
}

// 创建服务号码失败
function errCreateSerialNumber(error_code, error_info, derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code, error_info, derror);
}