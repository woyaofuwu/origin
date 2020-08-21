function queryUserCancelTrade() {
	
	if(!$.validate.verifyAll('queryForm')) return false;
	
	$.beginPageLoading();
	$.ajax.submit('queryForm','queryUserCancelTrade', '','groupCancelbook,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
} 

function cancelTradeSubmit(){
	if(!$.validate.verifyAll('queryForm')) return false;
	
	$.beginPageLoading();
	$.ajax.submit('queryForm','cancelTradeSubmit', '','cancelBookTradeTable,refreshHintBar', function(data){
		$.endPageLoading(); 
		successMessage(data);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

}

function successMessage(data){ 
	$.showSucMessage(data.get("DATAINFO"));
}


function onclickTradeId() {
	var selectTagObj = $("input[name='CANCEL_TRADE_ID']");
	var cancel_trade = "";
	for(var i = 0;i < selectTagObj.length; i++)
	{
		if(selectTagObj[i].checked)
		{
			cancel_trade = $(selectTagObj[i]).val();
		}
	}
	
	var arr_cancel_trade = cancel_trade.split(";");
	
	$("#pam_NOTIN_TRADE_ID").val(arr_cancel_trade[0]);
	$("#pam_NOTIN_FOREGIFT").val(parseFloat(arr_cancel_trade[2]));
	$("#pam_NOTIN_ALL_FEE").val(parseFloat(arr_cancel_trade[1])+parseFloat(arr_cancel_trade[2])+parseFloat(arr_cancel_trade[3]));
	$("#pam_NOTIN_SERIAL_NUMBER").val(arr_cancel_trade[4]);
	$("#pam_NOTIN_USER_ID").val(arr_cancel_trade[5]);
	
}