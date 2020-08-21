window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryData() {
	if (!$.validate.verifyAll("queryPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	ajaxSubmit('queryPart', 'queryNpOutInfo', '', 'QueryResultPart', function(data) {
		$.endPageLoading();
		var resultInfo = "用户可以申请携出！";
		if (data) {
			var resultcode = data.get(0).get("X_RESULTCODE");
			if (resultcode == 0) {
				MessageBox.success("成功提示", resultInfo);
				return;
			} else if (resultcode == '0001') {
				resultInfo = "用户不可以申请携出，详见限制编码和限制说明！";
			} else if (resultcode == '0002') {
				resultInfo = "用户不满足携出条件！";
			} else {
				resultInfo = data.get(0).get("X_RESULTINFO");
			}
			MessageBox.error("错误提示", resultInfo);
		}
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!");
	});
}
