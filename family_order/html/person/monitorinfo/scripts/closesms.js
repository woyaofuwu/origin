function refreshPartAtferAuth(data) {
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'TipInfoPart', function(data){
		$.endPageLoading();
		if(data.get('TIP_CODE') != null) {
			$("#TipInfoPart").css("display","block");
		}
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

function onTradeSubmit() {
	var authData = $.auth.getAuthData();
	var remark = $("#REMARK").val();
	var param = "";
	
	param += "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&REMARK=" + remark;
	
	$.cssubmit.addParam(param);
	return true;
}