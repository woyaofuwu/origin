function validateParamPage(methodName) 
{
	if(methodName=='CrtUs')
	{
		var shortNumber = $("#SHORT_NUMBER_CODE_INPUT").val();
		var shortNumberInput = $("#pam_SERV_CODE").val();
		if(shortNumber)	
		{
			if(shortNumber != shortNumberInput) 
			{
				alert("\u8BF7\u5148\u9A8C\u8BC1\u670D\u52A1\u4EE3\u7801\uFF01");
				return false;
			}
		}
		else 
		{
			alert("\u8BF7\u5148\u9A8C\u8BC1\u670D\u52A1\u4EE3\u7801\uFF01");
			return false;
		}
	}
	else if(methodName=='ChgUs')
	{
		var operCode = $("#pam_OPER_CODE").val();
	   	if(operCode == "")
	   	{
	       	alert("\u8BF7\u9009\u62E9\u64CD\u4F5C\u7C7B\u578B\uFF01");
	       	return false;
	   	} 
	}
	return true;  
}

function initCrtUs() 
{
	var methodname = $("#METHOD_NAME").val();	
	var operpart = $("#OperCodePart");
	var prodtype = $("#ProductTypePart");
	var loginname = $("#LoginNamePart");
	var tmppwd = $("#TmpPwdPart");
	if(methodname == "CrtUs")
	{
		operpart.css("display","none");
		$("#pam_BIZ_NAME").attr("disabled","true");
		$("#pam_BILLING_TYPE").attr("disabled","true");
		$("#pam_ACCESS_MODE").attr("disabled","true");
	    
	    var param = "&METHOD_NAME=getBizCodeTail&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.tisms.UserParamInfo";
		$.beginPageLoading();
		Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param, suc, err);
	}
	else if(methodname == "ChgUs")
	{
		$("#validButton").css("display","none");
		prodtype.css("display","none");
		$("#pam_PRODUCT_TYPE").attr("nullable","yes");
		loginname.css("display","none");
		tmppwd.css("display","none");
		$("#pam_SERV_CODE").attr("disabled","true");
		
	    statetodisable();
	}	
}
function suc(data)
{ 
	$.endPageLoading();
		
	var strSvcCodeTail= data.get("strSvcCodeTail");		
	$("#pam_SERV_CODE").val($("#pam_SERV_CODE").val()+strSvcCodeTail);
}

function err(error_code,error_info,derror)
{
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
}

function checkservcode()
{   
	var servCode = $("#pam_SERV_CODE");	
	
	if(!$.validate.verifyField(servCode)) return false;
	
	var param = "&SERV_CODE=" + servCode.val() +"&METHOD_NAME=checkValidServCode&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.tisms.UserParamInfo";
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param, afterValidchk, err);
}


function afterValidchk(data) 
{
	var servCode = data.get("SERV_CODE"); 
	var result = data.get("RESULT"); 
	if(result=='false') 
	{		
		alert(data.get("ERROR_MESSAGE"));
		$("#SHORT_NUMBER_CODE_INPUT").val("");	
      	$("#pam_SERV_CODE").focus();
      	return false;
	}
	else 
	{
		$("#SHORT_NUMBER_CODE_INPUT").val(servCode);	
		alert("\u9A8C\u8BC1\u6210\u529F\uFF01");
	}
}

function  actionOnChgOpertype()
{ 
	var opervalue = $("#pam_OPER_CODE").val();		
    if(opervalue == "71" || opervalue == "72")
    {    
        statetodisable();            
    }
    else if(opervalue == "05")
    { 
         statetoundisable();
    }    
}

function statetodisable()
{      
	$("#pam_BILLING_TYPE").attr("disabled","true");
	$("#pam_BIZ_CODE").attr("disabled","true");
	$("#pam_SERV_CODE").attr("disabled","true");
	$("#pam_ACCESS_MODE").attr("disabled","true");
	$("#pam_BIZ_ATTR").attr("disabled","true");
	$("#pam_BIZ_STATUS").attr("disabled","true");
	$("#pam_PRICE").attr("disabled","true");
	$("#pam_BILLING_MODE").attr("disabled","true");
	$("#pam_MAX_ITEM_PRE_DAY").attr("disabled","true");
	$("#pam_BILLING_MODE").attr("disabled","true");
	$("#pam_IS_TEXT_ECGN").attr("disabled","true");
	$("#pam_MAX_ITEM_PRE_MON").attr("disabled","true");
	$("#pam_DEFAULT_ECGN_LANG").attr("disabled","true");
	$("#pam_TEXT_ECGN_ZH").attr("disabled","true");
	$("#pam_TEXT_ECGN_EN").attr("disabled","true");
	$("#pam_ADMIN_NUM").attr("disabled","true");
	$("#pam_BIZ_NAME").attr("disabled","true");
	$("#pam_BIZ_PRI").attr("disabled","true");
	$("#pam_CS_URL").attr("disabled","true");
	$("#pam_USAGE_DESC").attr("disabled","true");
	$("#pam_PRE_CHARGE").attr("disabled","true");
	$("#pam_BIZ_TYPE_CODE").attr("disabled","true");  
	$("#pam_INTRO_URL").attr("disabled","true");   
}

function statetoundisable()
{   
	$("#pam_BILLING_TYPE").attr("disabled","");
	$("#pam_OPER_CODE").attr("disabled","");
	$("#pam_ACCESS_MODE").attr("disabled","");
	$("#pam_BIZ_STATUS").attr("disabled","");
	$("#pam_PRICE").attr("disabled","");
	$("#pam_BILLING_MODE").attr("disabled","");
	$("#pam_USAGE_DESC").attr("disabled","");
	$("#pam_INTRO_URL").attr("disabled","");
	$("#pam_MAX_ITEM_PRE_DAY").attr("disabled","");
	$("#pam_IS_TEXT_ECGN").attr("disabled","");
	$("#pam_MAX_ITEM_PRE_MON").attr("disabled","");
	$("#pam_DEFAULT_ECGN_LANG").attr("disabled","");
	$("#pam_TEXT_ECGN_ZH").attr("disabled","");
	$("#pam_TEXT_ECGN_EN").attr("disabled","");
	$("#pam_ADMIN_NUM").attr("disabled","");
	$("#pam_BIZ_TYPE_CODE").attr("disabled","");
	$("#pam_BIZ_PRI").attr("disabled","");
	$("#pam_CS_URL").attr("disabled","");
	$("#pam_PRE_CHARGE").attr("disabled","");    
}
