function qryIpMemberList()
{
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryIPMenberNumber", null, "memberPart,operForm", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function onSubmitBaseTradeCheck()
{
    if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
    
    return true;

}