function refreshPartAtferAuth(data)
{
	 $("#PASSWORD_UNLOCK_TYPE").attr("disabled",false);
	 $("#PASSWORD_PSPT_ID").val('');
	 
	
}

function onChangeUnLockType(){
	var unLockType =  $('#PASSWORD_UNLOCK_TYPE').val();
	if(unLockType=='2'){
		 $("#VlidDataPart").css("display", "");
		// $("#psptTypeCodeDIV").css("display", "");
		 $("#psptIdDIV").css("display", "");
	}else if(unLockType==''){
		$("#VlidDataPart").css("display", "none");
		 $("#psptTypeCodeDIV").css("display", "none");
		 $("#psptIdDIV").css("display", "none");
	}
}

function checkBeforeSubmit(){
	if(!$.validate.verifyField($("#PASSWORD_UNLOCK_TYPE")[0])){
			return false;
	}
	var unLockType =  $('#PASSWORD_UNLOCK_TYPE').val();
	if(unLockType=='2'){
//		if($("#PASSWORD_PSPT_TYPE_CODE").val()==""){
//			alert("证件类型不能为空！");
//			$("#PASSWORD_PSPT_TYPE_CODE").focus();
//			return false;
//		}
		var psptId = $("#PASSWORD_PSPT_ID").val();
		if(psptId == ""){
			alert("证件号码不能为空！");
			$("#PASSWORD_PSPT_ID").focus();
			return false;
		}
		var oriPsptId = $.auth.getAuthData().get('CUST_INFO').get('ORIGIN_PSPT_ID');
		if(psptId!=oriPsptId){
			alert("输入证件号不正确！");
			return false;
		}		
	}
	return true;
}