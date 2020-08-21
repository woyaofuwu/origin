// 播记录查询
function queryRecord() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryRecordPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	// 播记录查询
	$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null,
			'HiddenPart,QueryListPart', function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}