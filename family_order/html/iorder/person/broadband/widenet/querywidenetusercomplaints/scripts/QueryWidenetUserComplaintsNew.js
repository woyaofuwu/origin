function queryUserComplaints() {
	if (!$.validate.verifyAll()) {
		backPopup('UI-popup');
		return false;
	}
	$.beginPageLoading("正在查询数据...");	
	ajaxSubmit('ConditionPart,AdvanceConditionPart', 'queryUserComplaints','', 'UserComplaintsPart', function(rtnData) { 
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