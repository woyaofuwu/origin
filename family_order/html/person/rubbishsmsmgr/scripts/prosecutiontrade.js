/**
function refreshPartAtferAuth(data) {
	$.ajax.submit('', 'loadChildInfo', "&CUST_INFO="+data.get("CUST_INFO").toString(), 'CustInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}
*/

function onTradeSubmit() {
	if(!verifyAll('dealInfoPart')) {
	   return false;
   	}
   	
   	var param = "";
   	var authData = $.auth.getAuthData();
   	param += "&SERIAL_NUMBER=" + authData.get("USER_INFO").get("SERIAL_NUMBER");
   	param += "&PROSECUTEE_NUMBER=" + $("#cond_PROSECUTEE_NUMBER").val();
   	param += "&SMS_CONTENT=" + $("#cond_SMS_CONTENT").val();
   	//alert(param);
	$.cssubmit.addParam(param);
	
	return true;
}