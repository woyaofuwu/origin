function queryInfoList()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'queryInfoList', "", 'ResultPart', function(){
			$.endPageLoading();
		});
	}
}

function checkData (){
	var serialNum=$("#cond_SERIAL_NUMBER").val();  
}

function reset(){
	$("#cond_SERIAL_NUMBER").val('');
	$("#cond_START_DATE").val('');
	$("#cond_END_DATE").val('');
	$("#AGENT_DEPART_ID1").val(''); 
	$("#cond_STAFF_ID").val(''); 
}