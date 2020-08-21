 
function afterSubmitSerialNumber(data){
	 var serial_number =data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userStr = data.get("USER_INFO").toString();
	
	 var custStr = data.get("CUST_INFO").toString();
	 var acctStr = data.get("ACCT_INFO").toString();
	 var param = "&USER_INFO="+userStr+"&CUST_INFO="+custStr+"&ACCT_INFO="+acctStr+"&SERIAL_NUMBER="+serial_number;
	 $.beginPageLoading("业务受理中。。。");
	$.ajax.submit('AuthPart', 'queryInfos', param, 'QueryListPart', function(data){
		$.endPageLoading();
			if(data && data instanceof $.DatasetList && data.length>0){
					$.msg.showSucTrade(data);
				}else{
					$.msg.showSucTrade();
				}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



