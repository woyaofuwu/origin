function qrySmsTrade() {
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	var ACCESS_NO = $("#cond_ACCESS_NO").val();
	var SERIAL_NO = $("#cond_SERIAL_NO").val();
	if (ACCESS_NO == "" && SERIAL_NO == "" ) {
		MessageBox.alert("至少输入一个查询条件！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	// 用户可返销订单查询
	ajaxSubmit('QueryCondPart', 'querySmsTrade', null, 'TradeInfoPart',
			function(data) {
				$.endPageLoading();
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", "查询失败！</br>" + info, function(btn) {
				}, null, detail);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}
