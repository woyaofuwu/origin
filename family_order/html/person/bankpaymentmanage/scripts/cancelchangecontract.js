function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', '' ,
			'busiInfoPart,busiInfoPart2', function() {
				$("#PREPAY_TAG").val(data.get("USER_INFO").get("PREPAY_TAG"));
				payTypeChange($("#PAY_TYPE").val());
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}

function checkRadio(radioId){
	
	var write_type =$('#'+radioId).val();
	if(write_type == "1"){
		$("#PAY_TYPE").attr("disabled",false);
		$("#RECH_THRESHOLD").attr("disabled",false);
		$("#RECH_AMOUNT").attr("disabled",false);
		$("#REMARK").attr("disabled",false);
	}else if(write_type == "0"){
		$("#PAY_TYPE").attr("disabled",true);
		$("#RECH_THRESHOLD").attr("disabled",true);
		$("#RECH_AMOUNT").attr("disabled",true);
		$("#REMARK").attr("disabled",true);
	}
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

function checkBeforeSubmit(){

	if(!$.validate.confirmAll("cancelChgALLPart")) return false;
	
	if($("#OPER_RADIO").attr("checked") && $("#PAY_TYPE").val() == $("#OLD_PAY_TYPE").val() 
			&& $("#RECH_THRESHOLD").val() ==$("#OLD_RECH_THRESHOLD").val()
			&& $("#RECH_AMOUNT").val() ==$("#OLD_RECH_AMOUNT").val()){
		alert("没有做任何修改，无法提交！");
		return false;
	}
	
	var param ="&OPER_RADIO_VALUE="+($("#OPER_RADIO").attr("checked")?"1":"0")
	
	$.cssubmit.addParam(param);
 	
 	return true; 
}