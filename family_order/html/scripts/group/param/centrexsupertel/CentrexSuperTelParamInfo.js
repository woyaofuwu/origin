function init()
{
	var tableedit = $.table.get("ZyhtTable");
	$("#ROLE_CODE_B").attr("disabled", true);
	isSetSuperTelOper($("#pam_IS_SUPERTELOPER"));
}

function validateParamPage(methodName) 
{
	if(methodName == "CrtUs")
	{		
		var vpn_infos = $("#VPN_INFOS").val();
	    var vpn_no = $("#pam_VPN_NO").val();
	    if(vpn_infos != "" && vpn_infos != null)
	    {
	   		if(vpn_no == "")
	   		{
	   			alert ("\u8BF7\u9009\u62E9\u0056\u0050\u004E\u7F16\u7801\uFF01");
				return false;
	   		}
	    }
	    
	    var super_number = $("#pam_SUPERNUMBER").val();
	    if(super_number == "")
   		{
   			alert ("\u8BF7\u81F3\u5C11\u65B0\u589E\u4E00\u4E2A\u603B\u673A\u4FE1\u606F!");
			return false;
   		}
	}
	else if(methodName == "CrtMb")
	{
		
		if($("#hidden_SHORT_CODE").val())
		{
			if($("#hidden_SHORT_CODE").val() != $("#pam_SHORT_CODE").val())
			{
				alert("\u8BF7\u5148\u9A8C\u8BC1\u6210\u5458\u77ED\u53F7\uFF01");
				$("#pam_SHORT_CODE").focus();
				return false;
			}
		}
		else
		{
			alert("\u8BF7\u5148\u9A8C\u8BC1\u6210\u5458\u77ED\u53F7\uFF01");
			$("#pam_SHORT_CODE").focus();
			return false;
		}
	}
	else if(methodName == "ChgMb")
	{
		
	    var hiddenShortCode = $("#hidden_SHORT_CODE").val();
		var pamShortCode = $("#pam_SHORT_CODE").val();
		
	  	if(hiddenShortCode)
	  	{
			if(hiddenShortCode != pamShortCode) 
			{
				alert("\u8BF7\u5148\u9A8C\u8BC1\u77ED\u53F7\u7801\uFF01");
				return false;
			}
		}
		else if(pamShortCode != null && pamShortCode != "")
		{
			alert("\u8BF7\u5148\u9A8C\u8BC1\u77ED\u53F7\u7801\uFF01");
			return false;
		}
	}
	return true;
}

function shortNumValidateSuperTeleMember(obj)
{
	if($.validate.verifyField(obj))
	{
		if($("#hidden_SHORT_CODE").val() == $("#pam_SHORT_CODE").val() ){
			alert("短号未修改,无需校验!");
			$("#pam_SHORT_CODE").focus();
			return;
		}
		
		var param = "&SHORT_NUM=" + obj.val() + "&USER_ID_A=" + $("#GRP_USER_ID").val() + 
		            "&METHOD_NAME=shortNumValidateSuperTeleMember&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.centrexsupertel.CentrexSuperTelMemParamInfo";
		$.beginPageLoading();
		Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param, sucShortNumValidateSuperTeleMember, errShortNumValidateSuperTeleMember);
		var param = "&SHORT_NUM=" + obj.val() + "&USER_ID_A=" + $("#GRP_USER_ID").val();
	}
}

function sucShortNumValidateSuperTeleMember(data)
{
	$.endPageLoading();
	
	if(data.get("FLAG"))
	{
	   var hiddenShortCode =  $("#hidden_SHORT_CODE").val();
	   var pamShortCode =  $("#pam_SHORT_CODE").val();
	    if (hiddenShortCode != "" && hiddenShortCode != null){
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
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",pamShortCode);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
		$("#hidden_SHORT_CODE").val(pamShortCode);
		alert("\u77ED\u53F7\u7801\u9A8C\u8BC1\u6210\u529F\uFF01");
		return true;
	}
	else
	{
		alert(data.get("ERROR_MESSAGE"));
		$("#pam_SHORT_CODE").focus();
		return false;
	}
}

function errShortNumValidateSuperTeleMember(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
}


function setPamSuperNumber() 
{
	var data = $.table.get("ZyhtTable").getTableData();
	$("#pam_SUPERNUMBER").val(data.toString());	
}

function addSuperTel()
{
    //效验表单
    if(!verifySupTelListTable()) return false;   
    var pamSerialNumber = $("#pam_EXCHANGETELE_SN").val(); 
    var serialNumber = $("#EXCHANGETELE_SN").val(); 
	if (pamSerialNumber != serialNumber)
	{
		alert("请验证总机号码！");
		return false;
	}
	//新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
	if (!$.table.get("ZyhtTable").isPrimary('EXCHANGETELE_SN', superTelEdit))
	{
		$.table.get("ZyhtTable").addRow(superTelEdit);
		setPamSuperNumber();
		resetArea("partSuperTelInfo");
	}
	else
	{
		alert("关键字段\"总机号码\"已经存在相同的记录\"" + $("#pam_EXCHANGETELE_SN").val() + "\"");
		return false;
	}
}

function delSuperTel()
{
    $.table.get("ZyhtTable").deleteRow();
    setPamSuperNumber();
}

function updateSuperTel()
{
 	//效验表单
    if(!verifySupTelListTable()) return false;  
    var serialNumber = $("#pam_EXCHANGETELE_SN").val();   
    if(serialNumber == "")
    {
		alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
    } 	
    var oldSerialNumber = $("#OLD_EXCHANGETELE_SN").val();   
    if(serialNumber != oldSerialNumber){
    	alert("总机号码不可变更，新的总机号码请点击【新增】操作！");
    }
	
	//新增表格行数据，并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
	$.table.get("ZyhtTable").updateRow(superTelEdit);
	setPamSuperNumber();
}

function tableRowClick() 
{
	//获取选择行的数据
	 var rowData = $.table.get("ZyhtTable").getRowData();
	 $("#pam_EXCHANGETELE_SN").val(rowData.get("EXCHANGETELE_SN"));
	 $("#OLD_EXCHANGETELE_SN").val(rowData.get("EXCHANGETELE_SN"));
	 $("#EXCHANGETELE_SN").val(rowData.get("EXCHANGETELE_SN"));
	 $("#E_CUST_NAME").val(rowData.get("E_CUST_NAME"));
	 $("#E_CUST_ID").val(rowData.get("E_CUST_ID"));
	 $("#E_USER_ID").val(rowData.get("E_USER_ID"));
	 $("#E_EPARCHY_CODE").val(rowData.get("E_EPARCHY_CODE"));
	 $("#E_EPARCHY_NAME").val(rowData.get("E_EPARCHY_NAME"));
	 $("#MAXWAITINGLENGTH").val(rowData.get("MAXWAITINGLENGTH"));
	 $("#CALLCENTERTYPE").val(rowData.get("CALLCENTERTYPE"));
	 $("#callCenterTypeName").val(rowData.get("callCenterTypeName"));
	 $("#CALLCENTERSHOW").val(rowData.get("CALLCENTERSHOW"));
	 $("#callCenterShowName").val(rowData.get("callCenterShowName"));
	 $("#CORP_REGCODE").val(rowData.get("CORP_REGCODE"));
	 $("#CORP_DEREGCODE").val(rowData.get("CORP_DEREGCODE"));
}
function tableRowDBClick() 
{
    var rowData = $.table.get("ZyhtTable").getRowData();
}

function validateSuperTeleInfo()
{
	var serialNumber = $('#pam_EXCHANGETELE_SN').val();
	
	if(serialNumber == null || serialNumber.length == 0)
	{
		alert("总机号码不能为空!");
		$('#pam_EXCHANGETELE_SN').focus();
		return false;
	}
	
	if(!$.validate.verifyField($("#pam_EXCHANGETELE_SN"))) return false;
		
	var param = "&SERIAL_NUMBER=" + serialNumber + "&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.centrexsupertel.CentrexSuperTelUserParamInfo&METHOD_NAME=checkSuperTeleInfo";
	$.beginPageLoading();
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam", "productParamInvoker", param, sucValidateSuperTelInfo, errValidateSuperTelInfo);	 
}

function sucValidateSuperTelInfo(data)
{ 
	$.endPageLoading();
	$("#pam_EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
	$("#EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
    $("#E_CUST_NAME").val(data.get("CUST_NAME"));
    $("#E_CUST_ID").val(data.get("CUST_ID"));
    $("#E_USER_ID").val(data.get("USER_ID"));
    $("#E_EPARCHY_CODE").val(data.get("EPARCHY_CODE"));
    $("#E_EPARCHY_NAME").val(data.get("EPARCHY_NAME"));
}

function errValidateSuperTelInfo(error_code,error_info,derror)
{
	$.endPageLoading();
	$("#EXCHANGETELE_SN").val("");
    $("#E_CUST_NAME").val("");
    $("#E_CUST_ID").val("");
    $("#E_USER_ID").val("");
    $("#E_EPARCHY_CODE").val("");
	showDetailErrorInfo(error_code,error_info,derror);
}


function setDefaultNumber()
{
	var exchangeNum = $("#EXCHANGETELE_SN").val();
	if ("" == exchangeNum || null == exchangeNum)
	{
	   alert("\u8bf7\u5728\u5217\u8868\u4e2d\u9009\u62e9\u4e00\u6761\u8bbe\u4e3a\u9ed8\u8ba4\u7684\u547c\u51fa\u663e\u793a\u53f7\u7801!");
	   return;
	}
	var defNumObj = $("#pam_EC_DEFAULT_NUMBER");
	var defNum = defNumObj.val();
    if("" != defNum && null != defNum && defNum != exchangeNum)
    {
    	alert("\u547c\u51fa\u9ed8\u8ba4\u663e\u793a\u53f7\u7801\u7531["+defNum+"]\u53d8\u66f4\u4e3a["+exchangeNum+"]");
    }
    defNumObj.val(exchangeNum);
}

function isSetSuperTelOper(obj){
	var roleCodeB = $("#IMSROLE_CODE_B").val();

	if(obj.attr("checked") && "3" == roleCodeB){
		$("#pam_SUPERTELNUMBER").attr("disabled", false);
		$("#pam_OPERATORPRIONTY").attr("disabled", false);
	}
	
	if(obj.attr("checked")){
		var param = "&CUST_ID=" + $("#CUST_ID").val() + "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&MEB_USER_ID=" + $("#MEB_USER_ID").val() + 
					"&MEB_EPARCHY_CODE=" + $("#MEB_EPARCHY_CODE").val() + 
		            "&METHOD_NAME=checkSuperTelOper&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.centrexsupertel.CentrexSuperTelMemParamInfo";
		$.beginPageLoading();
		Wade.httphandler.submit(null, "com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam","productParamInvoker", param , sucIsSetSuperTelOper, errIsSetSuperTelOper);
	}else{

		$("#pam_SUPERTELNUMBER").attr("disabled", true);
		$("#pam_OPERATORPRIONTY").attr("disabled", true);
		
		$("#span_SUPERTELNUMBER").attr("class", "");
		$("#span_OPERATORPRIONTY").attr("class", "");
		
		$("#pam_SUPERTELNUMBER").attr("nullable", "yes");
		$("#pam_OPERATORPRIONTY").attr("nullable", "yes");
		$("#ROLE_CODE_B").val("1");
	}
}

function sucIsSetSuperTelOper(data){
	$.endPageLoading();
	
	var roleCodeB = $("#IMSROLE_CODE_B").val();
	var obj = $("#pam_IS_SUPERTELOPER");
	
	if("true" != data.get("RESULT")){
		$.showWarnMessage(data.get("ERROR_MESSAGE"));
		obj.attr("checked",false);
		$("#pam_SUPERTELNUMBER").attr("disabled", true);
		$("#pam_OPERATORPRIONTY").attr("disabled", true);
		
		$("#span_SUPERTELNUMBER").attr("class", "");
		$("#span_OPERATORPRIONTY").attr("class", "");
		
		$("#pam_SUPERTELNUMBER").attr("nullable", "yes");
		$("#pam_OPERATORPRIONTY").attr("nullable", "yes");
		
		$("#ROLE_CODE_B").val("1");
	}else{
		if(obj.attr("checked")){
			$("#pam_SUPERTELNUMBER").attr("disabled", false);
			$("#pam_OPERATORPRIONTY").attr("disabled", false);
			
			$("#span_SUPERTELNUMBER").attr("class", "e_required");
			$("#span_OPERATORPRIONTY").attr("class", "e_required");
			
			$("#pam_SUPERTELNUMBER").attr("nullable", "no");
			$("#pam_OPERATORPRIONTY").attr("nullable", "no");
			
			$("#ROLE_CODE_B").val("3");
		}
	}
}

function errIsSetSuperTelOper(error_code,error_info,derror){
	$.endPageLoading();
    showDetailErrorInfo(error_code,error_info,derror);
}

function verifySupTelListTable()
{
	if(!checkFiled("pam_EXCHANGETELE_SN")) return false;
	if(!checkFiled("EXCHANGETELE_SN")) return false;
	if(!checkFiled("E_CUST_NAME")) return false;
	if(!checkFiled("E_CUST_ID")) return false;
	if(!checkFiled("E_USER_ID")) return false;
	if(!checkFiled("E_EPARCHY_CODE")) return false;
	if(!checkFiled("MAXWAITINGLENGTH")) return false;
	if(!checkFiled("CALLCENTERTYPE")) return false;
	if(!checkFiled("CALLCENTERSHOW")) return false;
	if(!checkFiled("CORP_REGCODE")) return false;
	if(!checkFiled("CORP_DEREGCODE")) return false;
	
	return true;
}

function checkFiled(obj)
{
	var keyValue = $("#" + obj).val();	
	
	if(null == keyValue || "" == keyValue)
	{
	    alert($("#" + obj).attr("desc") + '\u4e0d\u80fd\u4e3a\u7a7a');
	    $("#" + obj).focus()
	 	return false;
	}
	
	return $.validate.verifyField($("#" + obj));
	
	return true;
}

function setCallCenterTypeName()
{
   $("#callCenterTypeName").val($('#CALLCENTERTYPE option:selected').text()); 
}

function setCallCenterShowName()
{  
  $("#callCenterShowName").val($('#CALLCENTERSHOW option:selected').text()); 
}


