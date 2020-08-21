function shortNumValidateSuperTeleMember(obj)
{
	if($.validate.verifyField(obj))
	{
		if($("#pam_hidden_SHORT_CODE").val() == $("#pam_SHORT_CODE").val() ){
			MessageBox.alert("","短号未修改,无需校验!");
			$("#pam_SHORT_CODE").focus();
			return;
		}
		
		var param = "&SHORT_NUM=" + obj.val() + "&USER_ID_A=" + $("#cond_EC_USER_ID").val();
		$.beginPageLoading();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "shortNumValidateSuperTeleMember", param, sucShortNumValidateSuperTeleMember, errShortNumValidateSuperTeleMember);
	}
}

function sucShortNumValidateSuperTeleMember(data)
{
	$.endPageLoading();
	var result = data.get("AJAX_DATA").get("FLAG");
	if(result == "true")
	{
		var pamShortCode =  $("#pam_SHORT_CODE").val();
		$("#pam_hidden_SHORT_CODE").val(pamShortCode);
		$.validate.alerter.one($("#pam_SHORT_CODE")[0], "\u77ED\u53F7\u7801\u9A8C\u8BC1\u6210\u529F\uFF01","green");
		return true;
	}
	else
	{
		$.validate.alerter.one($("#pam_SHORT_CODE")[0], data.get("AJAX_DATA").get("ERROR_MESSAGE"));
		$("#pam_SHORT_CODE").focus();
		return false;
	}
}

function errShortNumValidateSuperTeleMember(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
}

function isSetSuperTelOper(obj){
	var roleCodeB = $("#ROLE_CODE_B").val();
	
	if(obj.attr("checked") && "3" == roleCodeB){
		$("#pam_SUPERTELNUMBER").attr("disabled", true);
		$("#pam_OPERATORPRIONTY").attr("disabled", true);
	}
	
	if(obj.attr("checked")){
		var param = "&CUST_ID=" + $("#cond_CUST_ID").val() + "&GRP_USER_ID=" + $("#cond_EC_USER_ID").val() + "&MEB_USER_ID=" + $("#cond_USER_ID").val() + 
					"&MEB_EPARCHY_CODE=" + $("#cond_USER_EPARCHY_CODE").val() ;
		$.beginPageLoading();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "checkSuperTelOper", param, sucIsSetSuperTelOper, errIsSetSuperTelOper);
	}else{
		
		$("#pam_SUPERTELNUMBER").val("");
		
		$("#pam_SUPERTELNUMBER").attr("disabled", true);
		$("#pam_OPERATORPRIONTY").attr("disabled", true);
		
		$("#span_SUPERTELNUMBER").attr("class", "");
		$("#span_OPERATORPRIONTY").attr("class", "");
		
		$("#pam_SUPERTELNUMBER").attr("nullable", "yes");
		$("#pam_OPERATORPRIONTY").attr("nullable", "yes");
	}
}

function sucIsSetSuperTelOper(data){
	$.endPageLoading();
	
	var roleCodeB = $("#IMSROLE_CODE_B").val();
	var obj = $("input[type='checkbox']");
	
	if("true" != data.get("AJAX_DATA").get("RESULT")){
		$.validate.alerter.one($("#pam_IS_SUPERTELOPER")[0], data.get("AJAX_DATA").get("ERROR_MESSAGE"));
		obj.attr("checked",false);
	}else{
		if(obj.attr("checked") && "3" != roleCodeB){
			$("#pam_SUPERTELNUMBER").attr("disabled", false);
			$("#pam_OPERATORPRIONTY").attr("disabled", false);
			
			$("#span_SUPERTELNUMBER").attr("class", "link required");
			$("#span_OPERATORPRIONTY").attr("class", "link required");
			
			$("#pam_SUPERTELNUMBER").attr("nullable", "no");
			$("#pam_OPERATORPRIONTY").attr("nullable", "no");
		}
	}
}

function errIsSetSuperTelOper(error_code,error_info,derror){
    showDetailErrorInfo(error_code,error_info,derror);
}