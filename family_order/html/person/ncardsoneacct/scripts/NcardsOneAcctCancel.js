function refreshPartAtferAuth(data){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString(),
			'mainNumInfoPart,secondNumInfoPart', function() {
				
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
	
}

function onTradeSubmit(){
	
	return true;
}