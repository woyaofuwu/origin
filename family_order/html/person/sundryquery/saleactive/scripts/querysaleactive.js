function querySaleActive()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.beginPageLoading("查询营销活动。。。");
	$.ajax.submit('QueryCondPart', 'querySaleActive', '', 'QueryListPart,ExportPart', 
		function(ajaxData){
			$.endPageLoading();
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function myTabSwitchAction(ptitle, title)
{
	return true;
}

function showDetail()
{
	var table = $.table.get("SaleActiveTable");
	var rowData = table.getRowData();
	
	var param = "&PACKAGE_ID=" + rowData.get('PACKAGE_ID');
	param += '&USER_ID=' + rowData.get('USER_ID');
	param += '&SERIAL_NUMBER=' + rowData.get('SERIAL_NUMBER');
	param += '&RELATION_TRADE_ID=' + rowData.get('RELATION_TRADE_ID');
	param += '&INST_ID=' + rowData.get('INST_ID');
	
	
	$.beginPageLoading("查看活动细节。。。");
	$.ajax.submit(null, 'showDetail', param, 'SaleActiveDetailPart', 
		function(ajaxData){
			$.endPageLoading();
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

