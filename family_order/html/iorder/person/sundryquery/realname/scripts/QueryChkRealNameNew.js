window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}
function sel_bef_chkSearchForm() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {// 先校验已配置的校验属性
		return false;
	}

	var checkbox = $("#cond_NORMAL_USER_CHECK").attr('checked');
	var sn = $("#cond_SERIAL_NUMBER").val();
	var params = "&SERIAL_NUMBER=" + sn + "&NEW_DATE=" + new Date();

	if (checkbox) { // 在网用户
		queryRealNameInfo();
	} else {// 不在网用户，就弹出手机号的（在网和不在网）用户信息
		popupDialog('选择用户信息', 'realname.sub.SelectUserInfoForRealNameNew', 'getCheckUserInfo', params, null, "600px", "300px");
	}
}

function queryRealNameInfo() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {// 先校验已配置的校验属性
		return false;
	}
	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryCondPart', 'queryRealNameInfo', null, 'QueryListPart,ExportPart',
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