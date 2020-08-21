function queryBadMessage(obj) {
	if (!verifyField($('#cond_MSISDN')))
		return false;
	var startDate = $('#cond_START_DATE').val();
	var endDate = $('#cond_END_DATE').val()
	if ((!startDate == ""
			&& (endDate == "" || endDate == null) || ((startDate == "" || startDate == null) && !endDate == ""))) {
		alert("查询时间输入不全,请输入起始时间和结束时间！");
		return false;
	}
	if (!startDate == "" && !endDate == "") {
		if (startDate > $('#cond_END_DATE').val()) {
			alert("举报开始时间不能大于举报结束时间！");
			return false;
		}
	}

	$.ajax.submit('QueryCondPart', 'queryBadMessage', null, 'QueryListPart',
			function(data) {
				if (data.get('ALERT_INFO') != '') {
					alert(data.get('ALERT_INFO'));
				}
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});

}