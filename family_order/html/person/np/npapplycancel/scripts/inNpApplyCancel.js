function queryNpCancel()
{
	if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))
    {
        return false;
    }
    
    $.ajax.submit('QueryPart', 'queryNpApplyTradeInfo', '', 'netNpPart', function(data){
    	if(data.get('ALERT_INFO') != '')
		{
    		$("#netNpPart  tbody").empty();
			alert(data.get('ALERT_INFO'));
			$.cssubmit.disabledSubmitBtn(true);//放开提交按钮
		}
    	else
    	{
    		$.cssubmit.disabledSubmitBtn(false);//放开提交按钮

    	} 

	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function checkBeforeSubmit(){
	var datas = getAllTableData("tradeTable",null);
	if(datas.length>0){
		var param = "&TRADE_ID="+datas.get(0).get("TRADE_ID");
		$.cssubmit.addParam(param);
		return true;
	}
	return false;
}