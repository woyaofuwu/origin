function initPageParam_110000008050() {
	debugger;
	var methodName = $("#cond_OPER_TYPE").val();
	if(methodName=="ChgUs")
	{
		$("#pam_WORK_TYPE_CODE").attr("disabled","true");
		$("#pam_SCP_CODE").attr("disabled","true");
		$("#pam_VPN_SCARE_CODE").attr("disabled","true");
		$("#pam_MAX_INNER_NUM").attr("disabled","true");
	}else if(methodName=="CrtUs")
	{
		$("#pam_CALL_AREA_TYPE").val("4");
	}
	
}
// 创建服务号码
function createserialnumber(){
	
	if(!$.validate.verifyField($("#pam_WORK_TYPE_CODE"))) return false;
	
	var serialNumber = $("#cond_SERIAL_NUMBER_INPUT").val();
	
	// 删除服务号码
	if(serialNumber != null && serialNumber != ""){
		$("#cond_SERIAL_NUMBER_INPUT").val("");
		$("#cond_SERIAL_NUMBER").val("");
	}
	var work_type_code = $('#pam_WORK_TYPE_CODE').val();
	var userEpachyCode = $.enterpriseLogin.getInfo().get("CUST_INFO").get("EPARCHY_CODE");
	// 生成服务号码
	$.beginPageLoading();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.parentvpn.ParentvpnHandler", "createSerialNumber",'&WORK_TYPE_CODE='+work_type_code+'&GRP_USER_EPARCHY_CODE='+userEpachyCode, function(data){
		sucCreateSerialNumber(data);
		},    
		function(error_code,error_info,derror){
		errCreateSerialNumber(error_code,error_info,derror);
    });
}

// 创建服务号码成功
function sucCreateSerialNumber(data){
	$.endPageLoading();
	
	var vpnNo = data.get("VPN_NO");
	$("#cond_SERIAL_NUMBER_INPUT").val(vpnNo);
	$("#cond_SERIAL_NUMBER").val(vpnNo);
}

// 创建服务号码失败
function errCreateSerialNumber(error_code, error_info, derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code, error_info, derror);
}

//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}