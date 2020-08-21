function queryPresentInfos()
{	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if(startDate==""){
		alert("起始日期不能为空！");
		return false;
	}
	if(endDate==""){
		alert("终止日期不能为空！");
		return false;
	}
	if(endDate < startDate){
		alert("结束时间不能小于开始时间!");
		return false;
	}

	$.ajax.submit('QueryPart', 'queryPresentInfos', null, 'ResultDataPart', function(data){
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
