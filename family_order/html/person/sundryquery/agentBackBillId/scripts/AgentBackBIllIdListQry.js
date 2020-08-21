function queryAgentBackBillIdList(){
	
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('QueryCondPart', 'qryAgentBackBillIdListByCond', '', 'results', function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
		var table = $.table.get("resultsTable");//获取 JavaScript 包装的 Table 对象
		table.cleanRows();//清空表格的内容
    });
    return true;
}