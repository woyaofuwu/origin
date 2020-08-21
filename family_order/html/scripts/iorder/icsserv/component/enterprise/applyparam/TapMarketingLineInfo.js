function initPageParam() {
	debugger;
	
} 

//提交
function checkSub(obj)
{
	var lineName = $("#NOTIN_LINENAME").val();
	var bandwIdth = $("#NOTIN_BANDWIDTH").val();
	var rsrv = $("#NOTIN_RSRV_STR2").val();
	var opponent = $("#NOTIN_OPPONENTMARKETINGCONTENT").val();
	var cust = $("#NOTIN_CUSTRENEWCONTENT").val();
	var custName = $("#NOTIN_CUST_NAME").val();
	$("#NOTIN_LINENAME").val($.xss(lineName));
	$("#NOTIN_BANDWIDTH").val($.xss(bandwIdth));
	$("#NOTIN_RSRV_STR2").val($.xss(rsrv));
	$("#NOTIN_OPPONENTMARKETINGCONTENT").val($.xss(opponent));
	$("#NOTIN_CUSTRENEWCONTENT").val($.xss(cust));
	$("#NOTIN_CUST_NAME").val($.xss(custName));
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}
