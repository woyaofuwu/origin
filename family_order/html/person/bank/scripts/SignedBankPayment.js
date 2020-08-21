function queryBankInfo()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'loadChildInfo', null, 'bankListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
 

