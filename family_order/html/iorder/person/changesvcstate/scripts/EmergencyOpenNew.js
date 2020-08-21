function refreshPartAtferAuthEmergency(data) {
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var userId = data.get("USER_INFO").get("USER_ID");
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var param = "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId + "&EPARCHY_CODE=" + eparchyCode;
	$.ajax.submit('', 'loadChildInfo', param, 'busiInfoPart', function() {
		$.endPageLoading();
		if ($("#OPEN_HOURS").val() == "") {
			alert("无法获取紧急开机时间，请确认用户的信用等级是否正确！");
			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		} else {
			$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		}
	}, function(error_code, error_info, detail) {
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}