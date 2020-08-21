function validateImsShortNum(obj) {
    var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格
    var user_id = $("#cond_EC_USER_ID").val();
    var cust_id = $("#CUST_ID").val();
    var meb_eparchy_code = $("#cond_USER_EPARCHY_CODE").val();
	if(shortCode == ""){
		$.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号码不能为空！\n");
		return false;
	}
	if(shortCode != shortCodeTmp)
    {
		$.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号['+shortCode+']含有空格，请去掉!\n");
    	return false;
    }
   
	var shortNumber = $("#pam_SHORT_NUMBER_PARAM_INPUT").val();
    if (verifyField(obj))
    {
    	if(shortNumber == shortCode )
    	{
    		$.validate.alerter.one($("#pam_SHORT_CODE")[0], "短号未修改,无需校验!");
			return false;
		}
		
    	var param = '&SHORT_CODE='+obj.val()+'&USER_ID='+user_id+'&CUST_ID='+cust_id+'&MEB_EPARCHY_CODE='+meb_eparchy_code;
        $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "validchk", param, afterValidateShortNum, err);

    }
}

function afterValidateShortNum (data) {
    var shortCode = data.get("SHORT_CODE");
    var result = data.get("AJAX_DATA").get("RESULT");
    if (result == "false") 
    {
    	$.validate.alerter.one($("#pam_SHORT_CODE")[0], data.get("AJAX_DATA").get("ERROR_MESSAGE"));
      	return false;
    }
    else 
    {
        displayShort(shortCode);
    }
}

function err(error_code,error_info)
{
	$.validate.alerter.one($("#pam_SHORT_CODE")[0], error_info);
}

function displayShort(shortCode) 
{
    var shortNumber = $("#pam_SHORT_NUMBER_PARAM_INPUT").val();
    var shortNumberInput = $("#pam_SHORT_CODE").val();
    if (shortNumber != "" && shortNumber != null)	
    {
        if (shortNumber != shortNumberInput) 
        {
            var del = new Wade.DataMap();
            del.put("RES_TYPE_CODE","S");
            del.put("RES_CODE",shortNumber);
            deleteRes(del);
        }
        else
        {
			return true;
		}
    }
    var obj =new Wade.DataMap();
    obj.put("RES_TYPE_CODE","S");
    obj.put("RES_CODE",shortCode);
    obj.put("CHECKED","true");
    obj.put("DISABLED","true");
   
    $("#pam_SHORT_NUMBER_PARAM_INPUT").val(shortCode);
    $.validate.alerter.one($("#pam_SHORT_CODE")[0], "\u9a8c\u8bc1\u901a\u8fc7\uff0c\u5f55\u5165\u7684\u77ed\u53f7\u7801\u53ef\u4ee5\u4f7f\u7528\uff01",'green');
	return true;
}


function changeVpnScareTag(){
	var tag1 = $("#IF_PROV_VPN") ;
	var tag2 = $("#IF_PROV_PAY");
	var tag3 = $("#IF_PROV_RESULT");
	var tag4 = $("#IF_PROV_S");
	var tag5 = $("#IF_PROV_ITEM");
	var tag6 = $("#IF_PROV_S_ID");
	var provGroupID = $("#pam_PROV_GROUPID");
	var vpn_scare_code = $("#pam_VPN_SCARE_CODE").val();
		
	if(vpn_scare_code== '2'){
		tag1.css("display","");
		tag5.css("display","");
		provGroupID.attr("nullable", "no");

	}else{
		tag1.css("display","none");
		tag5.css("display","none");
		provGroupID.attr("nullable", "yes");
	}
	showTag();
}

function showTag()
{
	var checkboxs = $("input[name=itemcodes]");
	for(var i = 0; i < checkboxs.length; i++) 
	{
		if(checkboxs[i].getAttribute('chk') == "1") 
		{
			checkboxs[i].checked = true;
		}			
	}
}

function checkProvInfo() {

	var provInfo = $("#pam_VPN_SCARE_CODE").val();

	if(provInfo=='2'){
	
		var s = document.getElementsByName("itemcodes");
		var s2 = "";
		for( var i = 0; i < s.length; i++ )	{
			if ( s[i].checked ){
				s2 += s[i].value+'|';
			}
		}

		if(s2.length<1){
			MessageBox.alert("","请选择省代码！");
			return "false";
		}
	}
}


