function refreshPartAtferAuth(data)
{
	$("#QUERY_BTN").attr("disabled", false);
	var psptTypeCode = data.get("CUST_INFO").get("PSPT_TYPE_CODE");
	var psptId = data.get("CUST_INFO").get("ORIGIN_PSPT_ID");
	$("#PSPT_TYPE_CODE").val(psptTypeCode);
	$("#PSPT_ID").val(psptId);
	var table = $.table.get("QueryListTable");
	table.cleanRows();	
}

function queryListByUsrpid(){
	if (!$.validate.verifyAll()){
		return false;
	}
	var param ="&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryListByUsrpid', param, 'QueryListPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		
    });
}