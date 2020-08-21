function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_ID="+(data.get("USER_INFO")).get("USER_ID"),
			'memberInfoPart,familyMemberInfoPart', function() {
					$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				alert(error_info);
			});
}