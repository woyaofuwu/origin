function resetCondition(){
	document.getElementById("cond_SERIAL_NUMBER").value="";
}


function queryEnter(){
	document.getElementById('QUERY_BTN').click();
	return true;
}

//查询订单详细信息
function queryUserImei() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return false;
    }
    $.ajax.submit('QueryCondPart', 'queryUserImei', null, 'QueryListPart', function(data){
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