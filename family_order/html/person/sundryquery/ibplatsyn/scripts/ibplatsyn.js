function reset(){
	$('#submit_part :input').val('');
}

function query(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryIbPlatSytInfo', null, 'ResultDataPart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}