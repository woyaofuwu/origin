function refreshPartAtferAuth(){
	var params ="&SERIAL_NUMBER="+ $("#AUTH_SERIAL_NUMBER").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryRightUseInfo',params, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(code, ALERT_INFO, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", ALERT_INFO, function(btn) {
		}, null, detail);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!", function(btn) {
		});
	});
}