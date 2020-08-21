function queryRightDeal(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	var params = $("#SERIAL_NUMBER").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryRightDealInfo',params, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			MessageBox.alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
