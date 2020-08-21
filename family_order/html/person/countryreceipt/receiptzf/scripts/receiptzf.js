function cancelreceipt(){
	var check = false;
	var printID = "";
	var params = "";
	var checkvalues = $("input[name=checkvalue]");
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			printID = checkvalues[i].value;
			break;
		}
	}
	
	if(!check){
		alert("请先选择记录再作废发票!");
		return false;
	}
	
	$.beginPageLoading("作废受理中...");
	params += "&PRINT_ID="+printID;
	$.ajax.submit('PrintTable', 'submitZFReceipt', params, 'result_Table',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}

function queryZFReceipt(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	//校验起始日期范围
	if($("#cond_SERIAL_NUMBER").val() == "" && $("#cond_TRADE_ID").val() == ""){
		alert("服务号码和业务流水号不能都为空!");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryZFReceipt', null, 'result_Table', function(data){
			if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO').length > 0){
				MessageBox.alert("提示",data.get('ALERT_INFO'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}