/**
 * 固话号码后查询获取页面用户信息
 */
function afterSubmitSerialNumber(data){
	$.ajax.submit('AuthPart', 'loadTradeInfo', "&ACCT_ID="+data.get("ACCT_INFO").get("ACCT_ID"), 'acctDisnctPart', function(data){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}