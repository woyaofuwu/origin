function validateParamPage(methodName) 
{
	if(methodName == 'CrtUs'||methodName == 'ChgUs')
	{		
		var pwd = $("#pam_PASSWORD").val();
		var repwd = $("#pam_REPASSWORD").val();
		if(pwd != null)
		{
			if((pwd.length < 6) && (pwd.length != 0)) 
			{
				alert("\u5bc6\u7801\u5fc5\u987b\u4e3a\u516d\u4f4d\u6570\u5b57!");
				return false;
			}
			if(pwd != repwd)
			{
				alert("\u4e24\u6b21\u5bc6\u7801\u8f93\u5165\u9519\u8bef!");
				return false;
			}
			if(getSequenceCount(pwd)>3 || checkPwd(pwd))
			{
				alert("\u60a8\u8f93\u5165\u7684\u5bc6\u7801\u8fc7\u4e8e\u7b80\u5355!");
				return false;
			}
		}
	}
	
	if(methodName=='CrtUs')
	{
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
			alert("\u8BF7\u5148\u9A8C\u8BC1\u7BA1\u7406\u5458\u7684\u77ED\u53F7\u7801");
			return false;
		}

		var custServNumber = $("#custServNumber").val(); 
		var inputCustServNumber = $("#pam_EXCHANGETELE_SN").val(); 
		if(custServNumber)
		{
			if(custServNumber != inputCustServNumber)
			{
				alert("\u8BF7\u5148\u9A8C\u8BC1\u603B\u673A\u4FE1\u606F\uFF01");
				return false;
			}
		}
		else
		{
			alert("\u8BF7\u5148\u9A8C\u8BC1\u603B\u673A\u4FE1\u606F\uFF01");
			return false;
		}
	}
	return true;
}

function validateExchangeTeleSn(obj)
{  
	if($.validate.verifyField(obj))
	{ 		
		var param = "&SERIAL_NUMBER=" + obj.val() + "&METHOD_NAME=queryExchangeUserInfo&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.supertele.UserParamInfo";
		$.beginPageLoading();
		Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam","productParamInvoker", param , sucValidateExchangeTeleSn, errValidateExchangeTeleSn);
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
		var param = "&SHORT_CODE=" + obj.val() + "&METHOD_NAME=shortNumValidateSuperTeleAdmin&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.supertele.UserParamInfo";
	   	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param,  sucShortNumRes, errShortNumRes);
	}
}

function sucShortNumRes(data)
{
	var flag = data.get("FLAG");
	if(flag == 'true')
	{
		var shortNum =  $("#adminShortNum").val();
	   	var newShort =  $("#pam_EXCHANGE_SHORT_SN").val();
	    if (shortNum != "" && shortNum != null)
	    {
	    	if(shortNum != newShort)
	    	{
		    	var del = $.DataMap();
				del.put("RES_TYPE_CODE","S");
				del.put("RES_CODE",shortNum);
				deleteRes(del);
			}
	    }	    
	    var obj = $.DataMap();
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",newShort);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
		$("#adminShortNum").val(newShort);
		alert("短号验证通过,短号可以使用!");
	}
	else
	{
		alert(data.get("ERROR_MESSAGE"));
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
		var param = "&VPMN_SN=" + obj.val() + "&METHOD_NAME=vpmnNumValidateSuperTele&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.supertele.UserParamInfo";
		Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam","productParamInvoker", param , sucVpmnValidate, errShortNumRes);
	}
}


function sucVpmnValidate(data)
{  
	var flag = data.get("FLAG");
	var msg = data.get("ERROR_MESSAGE");
    if(flag == "0")
    {
    	alert(msg);
    	$("#pam_VPMN_SN").focus();
    	return false;
    }
    if(flag == "1")
    {
    	alert(msg);
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
		var param = "&SHORT_NUM=" + obj.val() + "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&METHOD_NAME=shortNumValidateSuperTeleMember&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.supertele.MebParamInfo";
		$.beginPageLoading();
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, sucValidateMemberShortNum, errValidateMemberShortNum);
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
	    	if(hiddenShortCode != pamShortCode){
		    	var del = $.DataMap();
				del.put("RES_TYPE_CODE","S");
				del.put("RES_CODE",hiddenShortCode);
				deleteRes(del);
			}else{
				return true;
			}
	    }
	    
	    var obj = $.DataMap();
		obj.put("RES_TYPE_CODE", "S");
		obj.put("RES_CODE", pamShortCode);
		obj.put("CHECKED", "true");
		obj.put("DISABLED", "true");
		insertRes(obj);
		$("#hidden_SHORT_CODE").val(pamShortCode);
		$.showSucMessage("\u77ED\u53F7\u7801\u9A8C\u8BC1\u6210\u529F\uFF01");
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
