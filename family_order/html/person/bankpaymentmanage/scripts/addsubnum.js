function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', '' ,
			'busiInfoPart', function() {
				$("#SubmitPart").addClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",true);
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}
function checkSubNo(){
	
	if(!$.validate.confirmAll("busiInfoPart")) return false;
	
	var subNumber = $("#SUB_NUMBER").val();
	$.beginPageLoading("正在校验副号码...");
	$.ajax.submit('AuthPart', 'checkSubNum', '&SUB_NUMBER='+subNumber,
			'', function() {
				$("#SubmitPart").removeClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",false);
				$("#CHECKED_SUB_NUMBER").val(subNumber);
				$.endPageLoading();
			}, function(error_code,error_info){
				$.endPageLoading();
				$("#CHECKED_SUB_NUMBER").val("");
				alert(error_info);
		    });
}

function checkBeforeSubmit(){
	if($("#CHECKED_SUB_NUMBER").val() !=  $("#SUB_NUMBER").val()){
		alert("输入的副号码与校验通过的副号码不一致，请重新校验！");
		return false;
	}
	if(!$.validate.confirmAll("addSubNumALLPart")) return false;
 	
 	return true; 
}