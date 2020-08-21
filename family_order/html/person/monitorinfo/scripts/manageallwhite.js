function refreshPartAtferAuth(data) {
	$.ajax.submit("", "loadChildInfo", "&USER_INFO=" + data.get("USER_INFO").toString(), null, function (data) {
		$.endPageLoading();
	}, function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

function onTradeSubmit() {
	var authData = $.auth.getAuthData();
	var serialNumber = authData.get("USER_INFO").get("SERIAL_NUMBER");
	var userId = authData.get("USER_INFO").get("USER_ID");
	var remark = $("#REMARK").val();
	
	$.beginPageLoading("业务受理中..");
	$.ajax.submit("", "onTradeSubmit", "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId + "&REMARK=" + remark, null, function (data) {
		$.endPageLoading();
		$.auth.reflushPage();
		$.showSucMessage("新增短信白名单成功!");
	}, function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}