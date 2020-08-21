function queryRemindOrder(){
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var param = "&SERIAL_NUMBER=" + serialNumber;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'queryRemindOrder',param , 'TableInfoPart', function(data){
		if(data && data.length) {
			//MessageBox.success("提示信息","查询成功！");
        } else {
        	MessageBox.success("提示信息","无数据！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
