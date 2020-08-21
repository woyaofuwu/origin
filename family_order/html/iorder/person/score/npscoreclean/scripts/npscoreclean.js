function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'enquiry,hidePart', function(){
		$("#comminfo_REMARK").attr('disabled',false);
		// $("#comminfo_DONATE_SCORE").attr('disabled',true);
		// $("#objinfo_OBJ_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		// $("#SOURCE_SERIAL_NUMBER").val($("#AUTH_SERIAL_NUMBER").val());
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}

//业务提交
function onTradeSubmit()
{
	$.cssubmit.submitTrade();
}

