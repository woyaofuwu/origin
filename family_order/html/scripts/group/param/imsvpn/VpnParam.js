function init() 
{
	var methodName = $('#METHOD_NAME').val();
	var netThypeCode =  $("#NET_TYPE_CODE").val();
	if(netThypeCode == "05")
	{
		$('#pam_OuterCallPart').css('display','');
		$("#outerCall").attr("class", "e_required");
	} 
	else if(netThypeCode == "00") 
	{
		$('#pam_OuterCallPart').css('display','none');
		$("#outerCall").attr("class", "");
	}

}
function chkPacDigitshead() 
{
	var pac = $.trim($("#pam_GRP_PAC_DIGITSHEAD").val());
	
	if(pac == "") 
	{
		alert ("\u51fa\u7fa4\u5b57\u51a0\u4e0d\u80fd\u4e3a\u7a7a\uff01");
		return false;
	}
	if(pac != "1" && pac != "9") 
	{
		alert ("\u51fa\u7fa4\u5b57\u51a0\u5fc5\u987b\u662f\u0031\u6216\u0039\uff01");
		return false;
	}
	$("#pam_GRP_PAC_DIGITSHEAD").val(pac);
	return true;
}

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
	if (methodName=='CrtUs'||methodName=='ChgUs')
	{
		return chkPacDigitshead();
	}
    if (methodName=='CrtMb' || methodName=='ChgMb')
    {
    	var check_box_serv = "0";  //默认没选中短号服务
    	var selectElements = selectedElements.selectedEls;
		for (var i = 0; i < selectElements.length; i++) 
		{
			var modify_tag = selectElements.get(i, "MODIFY_TAG");  // 0 表示元素选中   
			var element_id = selectElements.get(i, "ELEMENT_ID");
			if((modify_tag == "0" || modify_tag == "exist")&& element_id == "861")   //861短号服务
			{
				check_box_serv = "1";  //选中短号服务
				break;
			}	
		}		
		var shortNumber = $("#pam_OLD_SHORT_CODE").val();
		var shortNumberInput = $("#pam_SHORT_CODE").val();
		if (check_box_serv == "1")
		{
			if(shortNumberInput.length == 0) 
			{
				alert("\u60a8\u9009\u62e9\u4e86\u77ed\u53f7\u670d\u52a1\uff0c\u8bf7\u8f93\u5165\u77ed\u53f7\u7801");
				return false;
			} 
			if (shortNumber) 
			{
				if (shortNumber != shortNumberInput)
				 {
					alert("\u8bf7\u5148\u9a8c\u8bc1\u77ed\u53f7\u7801\uff01");
					return false;
				}
			} 
			else 
			{ 
				if (shortNumberInput.length > 0) 
				{
					alert("\u8bf7\u5148\u9a8c\u8bc1\u77ed\u53f7\u7801\uff01");
					return false;
				} 
				else 
				{
					return true;
				}
			}
		}
		else 
		{
			clearShortRes();
			if (shortNumberInput.length > 0) 
			{
				alert("\u6ca1\u6709\u9009\u62e9\u77ed\u53f7\u670d\u52a1\uff0c\u4e0d\u80fd\u586b\u5199\u77ed\u53f7");
				$("#pam_SHORT_CODE").val("");
				$("#pam_SHORT_CODE").attr("nullable","yes");
				return false;
			}
		}
		//00 or 05 discnt
		var netThypeCode =  $("#NET_TYPE_CODE").val();
		var imsdiscnt00 = $("#IMS_VPN_DISCNT_00").val();
		var imsdiscnt05 = $("#IMS_VPN_DISCNT_05").val();
		if(netThypeCode == '00')
		{ 
    		for (var i=0;i<selectElements.getCount();i++) 
    		{ 
				var elementData = selectElements.get(i); 
   	 			//EXIST DEL ADD
   	 			var modify_tag = elementData.get("MODIFY_TAG",""); 
   	 			var elementType = elementData.get("ELEMENT_TYPE_CODE", ""); 
   	 			var elementId = elementData.get("ELEMENT_ID",""); 
	 			var imsdiscnt05list = imsdiscnt05.split(',');
	 			for(var k = 0; k < imsdiscnt05list.length; k++)
	 			{
	 				if(elementType=="D" && elementId == imsdiscnt05list[k] && modify_tag =='0') 
	 				{
	 			    	alert('\u624b\u673a\u53f7\uff0c\u4e0d\u80fd\u5bf9\u0049\u004d\u0053\u56fa\u8bdd\u8d44\u8d39\u505a\u64cd\u4f5c\uff0c\u8d44\u8d39\u7f16\u7801\uff1a\u3010'+imsdiscnt05list[k]+'\u3011');
	 			        return false;
	 			   	}	
	 			}	   	    		
			}
		}
		else if(netThypeCode == '05')
		{     
			for (var i=0;i<selectElements.getCount();i++) 
    		{ 
				var elementData = selectElements.get(i); 
   	 			var modify_tag = elementData.get("MODIFY_TAG",""); 
   	 			var elementType = elementData.get("ELEMENT_TYPE_CODE", ""); 
   	 			var elementId = elementData.get("ELEMENT_ID",""); 
	 			var imsdiscnt00list = imsdiscnt00.split(',');
	 			for(var k=0;k<imsdiscnt00list.length;k++)
	 			{
	 				if(elementType=="D" && elementId == imsdiscnt00list[k] && modify_tag =='0') 
	 				{
	 			    	alert('\u0049\u004d\u0053\u56fa\u8bdd\u53f7\uff0c\u4e0d\u80fd\u5bf9\u624b\u673a\u8d44\u8d39\u505a\u64cd\u4f5c\uff0c\u8d44\u8d39\u7f16\u7801\uff1a\u3010'+imsdiscnt00list[k]+'\u3011');
	 			        return false;
	 				}	
				}	   		   	    		
			}
			var length = selectedElements.selectedEls.length;
			for(var i=0;i<length;i++)
			{
				var allSelectedElements = selectedElements.selectedEls.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG"); 
				if(state == "0" && elementType == "S" && elementId == "862") 
				{
					if(mytab != null)
					{
					 	mytab.switchTo("产品信息");
					}
					alert("固话号码不能选择862服务!");
					return false;
				}
			}

		}      
		if(netThypeCode == '05')
		{ 
			var outerCall = $("#pam_OuterCall").val();
			if(outerCall == null || outerCall == "")
			{
				//网外呼叫权限不能为空
				alert("\u7f51\u5916\u547c\u53eb\u6743\u9650\u4e0d\u80fd\u4e3a\u7a7a!");
				return false;
			}
		}
		                   
    }
    return true;
}

function clearShortRes() 
{
	var shortNumber = $("#pam_OLD_SHORT_CODE").val();
	var shortNumberInput = $("#pam_SHORT_CODE").val();
	var del = $.DataMap();
	del.put("RES_TYPE_CODE", "S");
	del.put("RES_CODE", shortNumber);
	deleteRes(del);
}

function validateImsShortNum(obj) 
{
    var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格
    var user_id = $("#GRP_USER_ID").val();
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
    if (verifyField(obj))
    {
    	if(shortNumber == shortCode )
    	{
			alert("短号未修改,无需校验!");
			$("#pam_SHORT_CODE").focus();
			return false;
		}
		
    	var param = '&SHORT_CODE='+obj.val()+'&USER_ID='+user_id+'&MEB_EPARCHY_CODE='+meb_eparchy_code+'&METHOD_NAME=validchk&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.imsvpn.CentrexMemParamInfo';
        Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, afterValidateShortNum,err);
    }
}

function afterValidateShortNum (data) 
{
	var tempElements = selectedElements.selectedEls;
	for (var i = 0; i < tempElements.length; i++) 
	{
		var modify_tag = tempElements.get(i, "MODIFY_TAG");  // 0 表示元素选中  
		var element_id = tempElements.get(i, "ELEMENT_ID");
		if(modify_tag != "0" && element_id == "816")   //816短号服务
		{
			alert("\u6ca1\u6709\u9009\u62e9\u77ed\u53f7\u670d\u52a1\uff0c\u4e0d\u80fd\u586b\u5199\u77ed\u53f7");
			$("#pam_SHORT_CODE").val("");
			$("#pam_SHORT_CODE").attr("disabled","true");
      		return false;
		}	
	}		
		
    var shortCode = data.get("SHORT_CODE");
    var result = data.get("RESULT"); 
    if (result=='false') 
    {
        alert(data.get("ERROR_MESSAGE"));
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
