function qryRatioList(){
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryRatioList", null, "hintPart,ratioPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}