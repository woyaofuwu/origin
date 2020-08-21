function queryAll(obj) {
	var serial_number = $("#cond_SERIAL_NUMBER").val();
	var activeType = $("#cond_ACTIVE_TYPE").val();
	if(serial_number == null || serial_number == "") {
		alert ('请输入【被推荐号码】！');
		return false;
	} else if(activeType == null || activeType == "") {
		alert ('请选择【活动类型】！');
		return false;
	} else {
		//ajaxSubmit('QueryCondPart', 'queryInfos', '', 'RefreshPart,refreshHintBar');
		$.beginPageLoading("数据查询中......");
		$.ajax.submit("QueryCondPart", "queryInfos", "", "RefreshPart,refreshHintBar", function(data){
				$.endPageLoading();
			},
			function(error_code, error_info, derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			}
		);
	}
}

