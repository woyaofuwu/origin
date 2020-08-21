function queryTrade()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	//效验是否跨月
	if(!checkQryDate())
	{
		return false;
	}
	
	$.beginPageLoading("查询中。。。");
	$.ajax.submit('QueryCondPart', 'queryTrade', '', 'QueryListPart', 
		function(ajaxData){
			$.endPageLoading();
		},
		function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","签收失败！", null, null, info, detail)
		});
}

function dealHdfkActiveTrade(index, dealStateCode)
{
	var rowIndex = parseInt(index) + 1;
	var table = $.table.get("SaleActiveTable");
	var rowData = table.getRowData(null, rowIndex);
	var operName = dealStateCode=='1' ? '撤销' : '签收';
	if(confirm('您确认要['+operName+']该订单？')){
		var param = '&SERIAL_NUMBER=' + rowData.get('SERIAL_NUMBER');
		param += '&RELATION_TRADE_ID=' + rowData.get('RELATION_TRADE_ID');
		param += '&OPER_TYPE_CODE=' + dealStateCode;
		param += '&INDEX='+index;
		
		var loadingDesc = operName + "中。。。";
		$.beginPageLoading(loadingDesc);
		$.ajax.submit(null, 'dealHdfkActiveTrade', param, null, sucFunc, 
			function(code, info){
				$.endPageLoading();
				alert(info);
			});
	}	
}

function sucFunc(ajaxData)
{
	$.endPageLoading();
	alert('处理成功');
	var index = ajaxData.get('INDEX');
	var operTypeCodeDesc = ajaxData.get('OPER_TYPE_CODE') == '1' ? '已撤销' : '已签收';
	
	$('#DEAL_STATE_CODE_'+index).html(operTypeCodeDesc);
	$('#OPER_TD_'+index).html('');
}

function checkQryDate()
{
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	
	var beginYear=startDate.substr(0,4);
	var beginMonth=startDate.substr(5,2);
	
	var endYear=endDate.substr(0,4);
	var endMonth=endDate.substr(5,2);
	
	if(startDate == '')
	{
		alert( "请选择开始时间！");
		return false;
	}
	
	if(endDate == '')
	{
		alert( "请选择结束时间！");
		return false;
	}
	
	if(!(beginYear==endYear&&beginMonth==endMonth)){
		alert( "查询日期必须是同一个月，不支持跨月查询！");
		return false;
	}
	
	return true;
}