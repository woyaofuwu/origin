function initPageParam_110000008060() {
	debugger;
	var methodName = $("#cond_OPER_TYPE").val();
	if(methodName=="ChgUs")
	{
		$("#pam_WORK_TYPE_CODE").attr("disabled","true");
		$("#pam_SHORT_CODE_LEN").attr("disabled","true");
		$("#pam_MAX_USERS").attr("disabled","true");
		$("#pam_MAX_TELPHONIST_NUM").attr("readonly","readonly");
		$("#pam_MAX_LINKMAN_NUM").attr("disabled","true");
	}else if(methodName=="CrtUs")
	{
		$("#pam_CALL_AREA_TYPE").val("4");
	}
}

function createserialnumber() {
	debugger;
	var work_type_code = $('#pam_WORK_TYPE_CODE').val();
	
	if(null == work_type_code || "" == work_type_code || work_type_code =="-----选择-----"){
		$.validate.alerter.one($("#pam_WORK_TYPE_CODE")[0], "VPMN集团类型不能为空！");
		return false;
	} 
	
	var userEpachyCode = $.enterpriseLogin.getInfo().get("CUST_INFO").get("EPARCHY_CODE");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.jwtvpn.JwtvpnHandler", "createSerialNumber",'&WORK_TYPE_CODE='+work_type_code+'&GRP_USER_EPARCHY_CODE='+userEpachyCode, function(data){
		$.endPageLoading();
		afterValidchk(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function afterValidchk(data) {
	$.endPageLoading();
	
	var vpnNo = data.get("VPN_NO");
	
	$("#cond_SERIAL_NUMBER_INPUT").val(vpnNo);
	$("#cond_SERIAL_NUMBER").val(vpnNo);
}

//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}
