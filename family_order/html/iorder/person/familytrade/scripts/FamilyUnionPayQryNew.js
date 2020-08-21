function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var params = "&USER_ID=" + (data.get("USER_INFO")).get("USER_ID");
	ajaxSubmit('', 'loadChildInfo', params, 'memberInfoPart,familyMemberInfoPart', function() {
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