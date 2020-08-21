function queryUserPolicyInfo() {
	if (!$.validate.verifyAll()) {
		return false;
	}
	var params = $.ajax.buildPostData("ConditionPart");
	$.beginPageLoading("物联网用户策略信息查询。。。");
	$.ajax.submit('', 'queryUserPolicyInfo', params, 'mpPolicyInfo,refreshHintBar',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}