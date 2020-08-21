var maxVal='30000';

function validateParamPage(methodName) 
{
	if (methodName=='CrtUs')
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
	}
	if(methodName == "CrtUs" || methodName == "ChgUs") 
	{
		var userNum = $("#pam_BGUserNum").val();
		var fixUserNum = $("#pam_BGFixUserNum").val();
		var mobUserNum = $("#pam_BGMobileUserNum").val();
		var num = parseInt(fixUserNum)+parseInt(mobUserNum);
		if(userNum<num) 
		{
			alert ("\u56FA\u5B9A\u96C6\u56E2\u6210\u5458\u6570\u4E0E\u79FB\u52A8\u96C6\u56E2\u6210\u5458\u6570\u4E4B\u548C\u4E0D\u80FD\u5927\u4E8E\u96C6\u56E2\u6210\u5458\u6570\uFF0C\u8BF7\u91CD\u65B0\u586B\u5199\uFF01");
			return false;
		}
	}
	else if(methodName == "CrtMb") 
	{
    	var old_short_code = $("#pam_OLD_SHORT_CODE").val(); 
    	var short_code = $("#pam_SHORT_CODE").val(); 

		if(old_short_code)
		{
			if(old_short_code != short_code)
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
    return true;
}

function init()
{
	var methodname =$("#METHOD_NAME").val(); 
	var vpnno =$("#pam_VPN_NO");
	if (methodname == "ChgUs") 
	{
		vpnno.attr("disabled","true");
		vpnno.attr("nullable","no");
	}else if(methodname == "CrtUs"){
		checkRule800109();  //判断签约2年且5个固话用户
	}
	
	//集团产品新增 多媒体桌面电话受理增加优惠立即生效 add by caolei 
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == false) {
		$('#EFFECT_NOW').attr('checked',true);
		selectedElements.isEffectNow = true;
	}
	 
}

function validateShortNum(obj) 
{
    var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格
    var user_id_a = $("#GRP_USER_ID").val();
    
    var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();

    if(shortCode == "") 
    {
    	alert('短号码不能为空！');
    	$("#pam_SHORT_CODE").focus();
    	return false;
    }
    if(shortCode != shortCodeTmp)
    {
    	alert('短号['+shortCode+']含有空格，请去掉!');
    	return false;
    }
    
	var shortNumber = $("#pam_OLD_SHORT_CODE").val();
	if ($.validate.verifyField(obj))
    {
    	if(shortNumber == shortCode )
    	{
			alert("短号未修改,无需校验!");
			$("#pam_SHORT_CODE").focus();
			return false;
		}
		
		var param = '&SHORT_CODE='+obj.val()+'&USER_ID_A='+user_id_a+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&METHOD_NAME=validchk&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.desktoptel.MebParamInfo';					   
        Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, afterValidateShortNum,err);           																																						   
	}

}

function afterValidateShortNum(data) 
{
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");
	
	var error_msg = data.get('ERROR_MESSAGE');
    if (result=='false') 
    {
		alert(error_msg);
        $("#pam_SHORT_CODE").val("");
        $("#pam_SHORT_CODE").focus();
        return false;
    }
    else 
    {
        displayShort(shortCode);
    }
}

function err(error_code,error_info)
{
	alert(error_info);
}
    
function displayShort(shortCode) 
{
	var shortNumber = $("#pam_OLD_SHORT_CODE").val();
    var shortNumberInput = $("#pam_SHORT_CODE").val();
    if (shortNumber != "" && shortNumber != null)	
    {
        if (shortNumber != shortNumberInput) 
        {
            var del = $.DataMap();
            del.put("RES_TYPE_CODE","S");
            del.put("RES_CODE",shortNumber);
            deleteRes(del);
        }
        else
        {
			return true;
		}
    }
    var obj = $.DataMap();
    obj.put("RES_TYPE_CODE","S");
    obj.put("RES_CODE",shortCode);
    obj.put("CHECKED","true");
    obj.put("DISABLED","true");
    insertRes(obj);
    $("#pam_OLD_SHORT_CODE").val(shortCode);
    alert("\u9a8c\u8bc1\u901a\u8fc7\uff0c\u5f55\u5165\u7684\u77ed\u53f7\u7801\u53ef\u4ee5\u4f7f\u7528\uff01");
    return true;
}

function check800109Param(itemIndex){ 
	var tempElement = selectedElements.selectedEls.get(itemIndex);
	var attrs = tempElement.get("ATTR_PARAM");
	var length = attrs.length;
	var elementId = tempElement.get("ELEMENT_ID");
	var monthFuncFee = 0; //月功能费
	var monthCallFee = 0 ; //月通信费 
	var discount = 10 ; //折扣
	
	for(var i=0;i<length;i++){
		var attr = attrs.get(i);
		var attrCode = attr.get("ATTR_CODE");
		var attrValue = attr.get("ATTR_VALUE");
		var newAttrValue = $("#"+attrCode).val();
		
		if(elementId == "800109")
		{
			//通信费(Y)
			if(attrCode == "20000000")
			{
				monthCallFee = newAttrValue;
			}
			//月功能费(X)
			if(attrCode == "20000002")
			{
				monthFuncFee = newAttrValue;
			}
			//折扣(Z)
			if(attrCode == "20000001")
			{
				discount = newAttrValue;
			}
		}
	} 
 
	for(var i=0;i<length;i++){
		debugger;
		var attr = attrs.get(i);
		var attrCode = attr.get("ATTR_CODE");
		var attrValue = attr.get("ATTR_VALUE");
		var newAttrValue = $("#"+attrCode).val();
		
		if(elementId == "800109")
		{
			//通信费(Y)
			if(attrCode == "20000000")
			{
				if(monthCallFee!='0' && !$.verifylib.checkNature(monthCallFee))
				{
					alert('通信费必须为正整数!');
					$("#"+attrCode).val(attrValue);
					return false;
				}
			}
			//月功能费(X)
			if(attrCode == "20000002")
			{
				if(!$.verifylib.checkNature(monthFuncFee))
				{
					alert('月功能费必须为正整数!');
					$("#"+attrCode).val(attrValue);
					return false;
				}
				//从配置中取月功能费最大值
				getMaxMonthFuncFee();
				
				if(maxVal!=-1 && parseInt(monthFuncFee) > parseInt(maxVal))
				{
					alert('月功能费不能大于'+maxVal+'元，请重新输入!');
					$("#"+attrCode).val(attrValue);
					return false ;
				}
			}
			
			
			if(parseInt(monthCallFee) > 2 * parseInt(monthFuncFee))
			{
				alert('通信费不能大于月功能费的2倍，请重新输入!');
				$("#"+attrCode).val(attrValue);
				return false ;
			}
			
		}
		
	} 
	 
}
function getMaxMonthFuncFee() {  
	 Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
	    		'&METHOD_NAME=getMaxMonthFuncFee&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.desktoptel.UserParamInfo',getMaxMonthFuncFeeAfter,errafterCheck,
				{async:false});

}

function getMaxMonthFuncFeeAfter(data){
	$.endPageLoading();
	var result = data.get("RESULT"); 
	if(result==true||result=='true'){    
		maxVal =data.get("MAX_VAL");
		
	}else{   
	   var message=  '获取月功能费最大值出错！' ;//data.get("ERROR_MESSAGE");
	   $.showWarnMessage(message);  
	}
}

function checkRule800109() {   
	 Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
	    		'&SELE_CONTRACTPRODUCT_CONTRACT_ID='+$('#SELE_CONTRACTPRODUCT_CONTRACT_ID').val()+'&METHOD_NAME=checkRule800109&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.desktoptel.UserParamInfo',chkRule800109After,errafterCheck);

}

function chkRule800109After(data){
	$.endPageLoading();
	var result = data.get("RESULT"); 
	if(result==false||result=='false'){   
		$("#Y_DIS").val('true');
	} 
}

function errafterCheck(e,i)
{   
	$.endPageLoading();
    $.showErrorMessage(e+":"+i);
}

 
 