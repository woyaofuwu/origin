function queryThisImportInfo() {
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "queryThisImportInfo", "", "RefreshPart", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
//			$.showWarnMessage(error_code,error_info);
		}
	);
}