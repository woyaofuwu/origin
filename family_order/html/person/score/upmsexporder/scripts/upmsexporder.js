
function queryExpOrder()
{	
	if($("#SERIAL_NUMBER").val() == ''){
		alert("服务号码不能为空！");
		return;
	}
	$.ajax.submit('QueryPart', 'queryExpOrder', null, 'ResultDataPart', function(data){
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
