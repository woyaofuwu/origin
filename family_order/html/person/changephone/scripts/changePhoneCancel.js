function refreshAtferAuth(data){
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+(data.get("USER_INFO")).toString(), 'ChangePhoneInfoPart', function(date){

	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		alert(error_info);
    });
}