function query(){
debugger;
	var busiFromId = $('#OPER_TYPE').val();
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","query", "&BUSIFORM_ID="+busiFromId,"QueryResultPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}