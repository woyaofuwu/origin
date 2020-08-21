function validateExchangeTeleSn(obj)
{  
	if($.validate.verifyField(obj))
	{ 		
		var param = "&SERIAL_NUMBER=" + obj.val();
		$.beginPageLoading();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.supertele.ProxyParamHandler", "queryExchangeUserInfo",param, function(data){
			$.endPageLoading();
			sucValidateExchangeTeleSn(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			errValidateExchangeTeleSn(error_code,error_info,derror);
	    });
	}
}

function sucValidateExchangeTeleSn(data)
{  
	$.endPageLoading();
	$("#pam_E_CUST_NAME").val(data.get("CUST_NAME"));
	$("#pam_E_CUST_ID").val(data.get("CUST_ID")); 
	$("#pam_E_USER_ID").val(data.get("USER_ID"));
	$("#pam_E_EPARCHY_CODE").val(data.get("EPARCHY_CODE")); 
	$("#pam_E_CITY_CODE").val(data.get("CITY_CODE")); 
	$("#pam_E_PRODUCT_ID").val(data.get("PRODUCT_ID")); 
	$("#pam_E_BRAND_CODE").val(data.get("BRAND_CODE")); 
	$("#custServNumber").val(data.get("SERIAL_NUMBER")); 
	
	$("#USER_TYPE").val(data.get("USER_TYPE")); 
	$("#PSPT_TYPE").val(data.get("PSPT_TYPE_NAME"));  
	$("#PSPT_ID").val(data.get("PSPT_ID"));
	$("#OPEN_DATE").val(data.get("OPEN_DATE")); 
	$("#DEVELOP_NO").val(data.get("DEVELOP_NO")); 
    $("#PRODUCT_NAME").val(data.get("PRODUCT_NAME")); 
    $("#BRAND_NAME").val(data.get("BRAND_NAME")); 
    
    if(data.get("FLAG") == "1")
    {
    	$("#pam_EXCHANGE_SHORT_SN").val(data.get("EXCHANGE_SHORT_SN"));
    	$("#pam_EXCHANGE_SHORT_SN").attr("disabled",true);
    }
}

function errValidateExchangeTeleSn(error_code,error_info,derror)
{
	$.endPageLoading();
    showDetailErrorInfo(error_code,error_info,derror);
}

function shortNumValidateSuperTeleAdmin(obj)
{
	if($.validate.verifyField(obj))
	{		
		var param = "&SHORT_CODE=" + obj.val();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.supertele.ProxyParamHandler", "shortNumValidateSuperTeleAdmin",param, function(data){
			$.endPageLoading();
			sucShortNumRes(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			errShortNumRes(error_code,error_info,derror);
	    });
	}
}

function sucShortNumRes(data)
{
	var flag = data.get("FLAG");
	if(flag == 'true')
	{
		var shortNum =  $("#adminShortNum").val();
	   	var newShort =  $("#pam_EXCHANGE_SHORT_SN").val();
		$("#adminShortNum").val(newShort);
		MessageBox.success("成功信息", "短号验证通过,短号可以使用!");
	}
	else
	{
		$.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN")[0], data.get("ERROR_MESSAGE"));
		$("#pam_SHORT_CODE").focus();
		return false;
	}
}

function errShortNumRes(error_code,error_info,derror)
{
    showDetailErrorInfo(error_code,error_info,derror);
}

function vpmnValidateSuperTeleMember(obj)
{
	if($.validate.verifyField(obj))
	{ 		
		var param = "&VPMN_SN=" + obj.val();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.supertele.ProxyParamHandler", "vpmnNumValidateSuperTele",param, function(data){
			$.endPageLoading();
			sucVpmnValidate(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			errShortNumRes(error_code,error_info,derror);
	    });
	}
}


function sucVpmnValidate(data)
{  
	var flag = data.get("FLAG");
	var msg = data.get("ERROR_MESSAGE");
    if(flag == "0")
    {
    	$.validate.alerter.one($("#pam_VPMN_SN")[0], msg);
    	$("#pam_VPMN_SN").focus();
    	return false;
    }
    if(flag == "1")
    {
    	$.validate.alerter.one($("#pam_VPMN_SN")[0], msg);
    	return false;
    }
}

function getSequenceCount(str)
{
	var iLen = str.length;
	if (iLen < 1) return 0;
	if (iLen == 1) return 1;
	var iResult = 1;
	var iCount = 0;
	var n2 = 0;
	var n1 = 0;

	n1 = parseInt(str.substring(0,1));
	iCount = 1;
	for (var i = 1; i < iLen ; i++)
	{
		n2 = parseInt(str.substring(i,i+1));
		if (n1 + 1 == n2)
		{
			iCount++;
			if ( iCount > iResult)
			{
				iResult = iCount;
			}
		}
		else
		{
			iCount = 1;
		}
		n1 = n2;
	}
	n1 = parseInt(str.substring(0,1));
	iCount = 1;
	for (var i = 1; i < iLen ; i++)
	{
		n2 = parseInt(str.substring(i,i+1));
		if (n1 == n2 +1)
		{	
			iCount++;
			if ( iCount > iResult)
			{
				iResult = iCount;
			}
		}
		else
		{
			iCount = 1;
		}
		n1 = n2;
	}
	return iResult;
}

function checkPwd(str)
{
	var iCount = 0;
	for(var i = 1;i <= 6; i++)
	{
		if(str.substring(i,i+1) == str.substring(i-1,i)) 
		iCount++;
	}
	if(iCount == 5) return true;
	return false;
}

function shortNumValidateSuperTeleMember(obj)
{
	if($.validate.verifyField(obj))
	{
		var param = "&SHORT_NUM=" + obj.val() + "&GRP_USER_ID=" + $("#GRP_USER_ID").val();
		$.beginPageLoading();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.supertele.ProxyParamHandler", "shortNumValidateSuperTeleMember",param, function(data){
			$.endPageLoading();
			sucValidateMemberShortNum(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			errValidateMemberShortNum(error_code,error_info,derror);
	    });
	}
}
function sucValidateMemberShortNum(data)
{
	$.endPageLoading();
	if(data.get("FLAG"))
	{
	   var hiddenShortCode = $("#hidden_SHORT_CODE").val();
	   var pamShortCode = $("#pam_SHORT_CODE").val();
	    if (hiddenShortCode != null && hiddenShortCode != ""){
	    	if(hiddenShortCode == pamShortCode){
				return true;
			}
	    }
		$("#hidden_SHORT_CODE").val(pamShortCode);
		$.showSucMessage("短号码验证成功！");
		return true;
	}
	else
	{
		$("#SHORT_NUMBER_PARAM_INPUT").val("");
		$("#memberShortNum").val("");
		$.showWarnMessage(data.get("ERROR_MESSAGE"));
		return false;
	}
}

function errValidateMemberShortNum(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
}

//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	
	var pwd = $("#pam_PASSWORD").val();
	var repwd = $("#pam_REPASSWORD").val();
	if(pwd != null && pwd !='')
	{
		if((pwd.length < 6) && (pwd.length != 0)) 
		{
			$.validate.alerter.one($("#pam_PASSWORD")[0], "密码必须为六位数字!\n");
			return false;
		}
		if(pwd != repwd)
		{
			$.validate.alerter.one($("#pam_REPASSWORD")[0], "两次密码输入错误!\n");
			return false;
		}
		if(getSequenceCount(pwd)>3 || checkPwd(pwd))
		{
			$.validate.alerter.one($("#pam_PASSWORD")[0], "您输入的密码过于简单!\n");
			return false;
		}
	}
	
	var custServNumber = $("#custServNumber").val(); 
	var inputCustServNumber = $("#pam_EXCHANGETELE_SN").val(); 
	if(custServNumber)
	{
		if(custServNumber != inputCustServNumber)
		{
			$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], "请先验证总机信息！\n");
			return false;
		}
	}
	else
	{
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], "请先验证总机信息！\n");
		return false;
	}

	var shortNum = $("#adminShortNum").val();
	var adminShortCode = $("#pam_ADMIN_SHORT_CODE").val();
	if(shortNum)
	{
		if(shortNum == adminShortCode)
		{
		}
	}
	else
	{
		$.validate.alerter.one($("#pam_ADMIN_SHORT_CODE")[0], "请先验证管理员的短号码！\n");
		return false;
	}

	

	backPopup(obj);
}
