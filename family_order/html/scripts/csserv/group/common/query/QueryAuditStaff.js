function qryAuditStaffInfos() {
	var staffId = $('#con_STAFF_ID').val();
	var staffName = $('#con_STAFF_NAME').val();
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "qryAuditStaffInfos", "", "refreshHintBar,RefreshPart", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}