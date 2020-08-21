function refreshPartAtferAuth(data)
{
$.ajax.submit('', 'loadChildInfo', "SERIAL_NUMBER="+ $("#AUTH_SERIAL_NUMBER").val()+"&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), '', function(){

	},
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$.endPageLoading();
		alert(error_info);
    });
}


function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	$.cssubmit.addParam(param);

	return true;
}
