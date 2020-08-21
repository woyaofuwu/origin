//查询
function queryInterface() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryWidenetPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中...");
	ajaxSubmit('QueryWidenetPart', 'getTradeInterface', null, 'QueryListPart',
			function(data) {
				$.endPageLoading();
				if (data.get("ALERT_CODE") != null) {
					$("#TipInfoPart").css("display", "block");
				} else {
					$("#TipInfoPart").css("display", "none");
				}
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!");
			});
}
// 重调
function restartInterface() {
	var boxObj = $(":radio:checked");
	var size = boxObj.length;
	if (size < 1) {
		MessageBox.alert("请选择需要调城市热点接口的工单！");
		return false;
	}
	var tradeId = $(boxObj[0]).val();
	$.beginPageLoading("接口调用中...");
	ajaxSubmit(null, 'restartInterface', "&TRADE_ID=" + tradeId, 'QueryListPart', function(data) {
				$.endPageLoading();
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!");
			});
}

function execInterface() {
	$.beginPageLoading("批量调用中...");
	ajaxSubmit(null, 'execInterface', null, 'QueryListPart', function(data) {
		$.endPageLoading();
		if (data.get("ALERT_CODE") != null) {
			$("#TipInfoPart").css("display", "block");
		} else {
			$("#TipInfoPart").css("display", "none");
		}
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!");
	});
}