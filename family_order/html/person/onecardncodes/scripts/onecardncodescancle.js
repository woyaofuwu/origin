function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,userInfoPart_newSim', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}