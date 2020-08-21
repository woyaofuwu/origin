//提交校验
function checkBeforeSubmit()
{  
     if(!$.validate.verifyField($("#cond_SERIAL_NUMBER")[0]))
     {
        return false;
     }
     return true;
}


function queryUserReserveCombo()
{
	if(checkBeforeSubmit())
	{
		$.ajax.submit('QueryCondPart', 'queryUserReserveCombo', null, 'ResultDataPart', function(data){
			if(data.get('ALERT_INFO') != '')
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

}

function myTabSwitchAction(ptitle,title){
	return true;
}

