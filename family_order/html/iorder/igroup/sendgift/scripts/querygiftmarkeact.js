function queryInfoList()
{
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('QueryCondPart', 'queryInfoList', "", 'QueryResultPart', function(){
			$.endPageLoading();
		});
	}
}

function reset(){
	$("#cond_START_DATE").val('');
	$("#cond_END_DATE").val('');
}