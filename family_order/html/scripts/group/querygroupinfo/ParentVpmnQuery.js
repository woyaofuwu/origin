function changeQueryType(){
	var condQueryType = $("#cond_QueryType").val();
	
	if(condQueryType == "0"){
		$("#parentSerialNumber").css("display", "");
		$("#subSerialNumber").css("display", "none");
		
		$("#cond_PARENT_SERIAL_NUMBER").attr("nullable", "no");
		$("#cond_SUB_SERIAL_NUMBER").attr("nullable", "yes");
		
		$("cond_SUB_SERIAL_NUMBER").val("");
	} else {
		$("#parentSerialNumber").css("display", "none");
		$("#subSerialNumber").css("display", "");
		
		$("#cond_PARENT_SERIAL_NUMBER").attr("nullable", "yes");
		$("#cond_SUB_SERIAL_NUMBER").attr("nullable", "no");
		
		$("cond_PARENT_SERIAL_NUMBER").val("");
	}
}

function queryVpmn(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryVpmnList", null, "vpmnPart,hintPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}