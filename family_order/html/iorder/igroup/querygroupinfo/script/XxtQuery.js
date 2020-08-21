function query(){

	var snA = $('#cond_SERIAL_NUMBER_A').val();
	var snB = $('#cond_SERIAL_NUMBER_B').val();
	if (snA=='' && snB==''){
		//alert('请输入查询条件!');
		MessageBox.alert("本地号码和异地号码不能同时为空!");
		return false;
	}

	if(!$.validate.verifyAll('queryForm')) return false;

	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryInfos", "", "refreshtable", function(data){
			
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}