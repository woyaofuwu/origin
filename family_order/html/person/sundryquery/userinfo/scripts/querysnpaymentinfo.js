function querySnPaymentInfo(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('QueryCondPart', 'querySnPaymentInfo', null, 'ResultDataPart,TipInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}