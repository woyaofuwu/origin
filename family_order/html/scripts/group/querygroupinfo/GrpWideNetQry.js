
function query(){
	
	if(!$.validate.verifyAll('QueryFormPart')) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryFormPart", "queryInfos", "", "grpcustRefreshPart,refreshtable,hintPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

