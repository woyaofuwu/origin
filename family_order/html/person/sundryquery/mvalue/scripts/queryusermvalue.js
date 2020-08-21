//提交校验
function checkBeforeSubmit()
{  
	//查询条件校验
	 if(!$.validate.verifyAll("QueryMvaluePart")) {
		return false;
	 }
     //if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))
     //{
       // return false;
    // }
     return true;
}

function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		queryUserMvalue();
	}
}

function queryUserMvalue(obj)
{
	if(checkBeforeSubmit())
	{
		$.ajax.submit('QueryMvaluePart', 'queryUserMvalue', null, 'userInfoPart,buttonPart', function(data){
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

}

/* check cycle */
function checkcycle(){
	var startCycleId = document.getElementById("START_CYCLE_ID").value;
	var endCycleId = document.getElementById("END_CYCLE_ID").value;
	if(startCycleId > endCycleId ){
		alert("开始帐期不能大于结束帐期!");
	}
}

