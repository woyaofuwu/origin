
function query(){
	
	if(!$.validate.verifyAll('queryForm')) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryInfos", "", "refreshtable,hintPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}