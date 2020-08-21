function queryPresentScore()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryPart")) {
		return false;
	}
	$.ajax.submit('QueryPart', 'queryPresentScore', null, 'PresentScorPart,buttonPart', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
   	    });
}   

