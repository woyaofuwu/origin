function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', '' ,
			'busiInfoPart', function() {

				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}


function checkBeforeSubmit(){

	if(!$.validate.confirmAll("SignContractALLPart")) return false;
	if($("#PAY_TYPE").val()=="1"){
		var rechThreshold =$("#RECH_THRESHOLD").val();
		var rechAmount =$("#RECH_AMOUNT").val();
	}
 	
 	return true; 
}


function payTypeChange(payType){
	if(payType == "1"){
		$("#autoFZ").css("display","");
		$("#RECH_THRESHOLD").attr("nullable","no");
		$("#autoED").css("display","");
		$("#RECH_AMOUNT").attr("nullable","no");
	}else if(payType == "0"){
		$("#autoFZ").css("display","none");
		$("#RECH_THRESHOLD").attr("nullable","yes");
		$("#autoED").css("display","none");
		$("#RECH_AMOUNT").attr("nullable","yes");
	}
}

