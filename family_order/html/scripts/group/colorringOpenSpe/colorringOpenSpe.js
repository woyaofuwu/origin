function getMemUserInfo()
{
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryMemberInfo", null, "custPart,userPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );	   
}

// 提交前校验
function onSubmitBaseTradeCheck()
{
    if(!$.validate.verifyField($("#END_DATE"))) return false;
    
    return true;

}
