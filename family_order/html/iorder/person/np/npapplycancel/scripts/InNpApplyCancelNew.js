window.onload = function () {
    $("#SERIAL_NUMBER").focus();
}

function queryNpCancel() {
	if (!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
		return false;
	}

	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryPart', 'queryNpApplyTradeInfo', '', 'netNpPart', function(data) {
		$.endPageLoading();
		if (data.get('ALERT_INFO') != '') {
			MessageBox.alert("错误提示", data.get('ALERT_INFO'));
			$.cssubmit.disabledSubmitBtn(true);// 禁用提交按钮
		} else {
			$.cssubmit.disabledSubmitBtn(false);// 放开提交按钮
		}
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!");
	});
}

function checkBeforeSubmit() {
	var datas = tradeTable.getData(true);
	if (datas.length > 0) {
		var param = "&TRADE_ID=" + datas.get(0).get("TRADE_ID");
		$.cssubmit.addParam(param);
		return true;
	}
	return false;
}