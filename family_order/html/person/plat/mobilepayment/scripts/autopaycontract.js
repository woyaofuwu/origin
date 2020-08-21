 
function afterSubmitSerialNumber(data){
  $("#USER_ID").val(data.get("USER_INFO").get("USER_ID"));
  $("#ROUTE_EPARCHY_CODE").val(data.get("USER_INFO").get("EPARCHY_CODE"));
  $("#PRODUCT_ID").val(data.get("USER_INFO").get("PRODUCT_ID"));
}

function afterClickAffirm()
{
	var operType = $("#cond_PAYOPR").val();
	if("04" == operType)
	{
		$.cssubmit.disabledSubmitBtn(false);
	}else
	{
		window.location.href = window.location.href;
	}
		
	
}

function submitBeforeAction()
{
	if(!$.validate.confirmAll())
	{
		return false;
	} 
	
	
	var param = "SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	param+="&ROUTE_EPARCHY_CODE="+$("#ROUTE_EPARCHY_CODE").val();
	param+="&USER_ID="+$("#USER_ID").val();
	param+="&PRODUCT_ID="+$("#PRODUCT_ID").val();
	$.cssubmit.addParam(param);
	
	return true;
}


function changOperType()
{
	var operType = $("#cond_PAYOPR").val();
	if (operType == "03" || operType == "04") {
        $("#cond_ACCOUNTPERIOD").attr("readOnly",true);
        $("#cond_PAYQUOTA").attr("readOnly",true);
        $("#cond_ACCOUNTPERIOD").attr("nullable","yes");
        $("#cond_PAYQUOTA").attr("nullable","yes");
        $("#cond_PAYTYPE").attr("nullable","yes");
    } else {
    	$("#cond_PAYQUOTA").attr("readOnly",false);
    	$("#cond_ACCOUNTPERIOD").attr("nullable","no");
    	$("#cond_PAYQUOTA").attr("nullable","no");
    	$("#cond_PAYTYPE").attr("nullable","no");
    }
}

 
function changPayType() {
    var payType = $("#cond_PAYTYPE").val();
    if (payType == "") {
        $("#timeTip").css("display","none") ;
        $("#moneyTip").css("display","none");
        $("#quotaTip").css("display","none");
    }
    if (payType == "0") {
    	$("#timeTip").css("display","block");
    	$("#moneyTip").css("display","none");
    	$("#quotaTip").css("display","block");


    }
    if (payType == "1") {
    	$("#timeTip").css("display","none");
    	$("#moneyTip").css("display","block");
    	$("#quotaTip").css("display","block");
    }
}
