function query(){
debugger;
	var operType = $('#OPER_TYPE').val();
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","query", "&OPER_TYPE="+operType,"QueryResultPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}