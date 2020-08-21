window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryBOSSInfo() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {// 先校验已配置的校验属性
		return false;
	}
	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryCondPart', 'queryBOSSInfo', null, 'QueryListPart',
		function(data) {
			if (data.get('ALERT_INFO') != '') {
				MessageBox.alert(data.get('ALERT_INFO'));
			}
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
}