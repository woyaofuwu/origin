//用戶认证结束之后执行的js方法
function qryGrpUser()
 {
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryproductForm", "loadPageInfo", null, 'topSetBoxInfoPart', 
		function(data){
			$("#cond_USER_ID_A").val(data.get("cond_USER_ID_A"));
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


function onSubmitBaseTradeCheck(){
	if("" == $("#cond_USER_ID_A").val()) 
	{
		alert("请查询后提交！");
		return false;
	}
	return true;
}