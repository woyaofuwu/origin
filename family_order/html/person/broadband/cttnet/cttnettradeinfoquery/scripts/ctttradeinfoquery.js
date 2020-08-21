
function queryInfos()
{
	var tradeId = $("#TRADE_ID").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
	var staffId = $("#STAFF_ID").val();
	var departId = $("#DEPART_ID").val();
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	
	if(startDate==""){
		alert("起始时间不能为空！");
		return false;
	}

	$.ajax.submit('', 'getTradeInfo', "&TRADE_ID="+tradeId+"&SERIAL_NUMBER="+serialNumber+"&STAFF_ID="+staffId+'&DEPART_ID='+departId+'&START_DATE='+
			startDate+'&END_DATE='+endDate, 'TradePart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}