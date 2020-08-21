function initPageParam_110000007030() {
	debugger;
	var methodName = $("#cond_OPER_TYPE").val();
	if(methodName=="ChgUs")
	{
		$("#pam_LBS_PWD").parent().parent().css("display","none");
	}
	
}
//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}

function validateParamPage(methodName) {
	if($("#pam_LBS_PWD").val()==""||$("#pam_LBS_PWD").val()==null){
		$.validate.alerter.one($("#pam_LBS_PWD")[0], "初始密码不能为空！");
		return false;
	}
    return true;
}