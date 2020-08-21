function queryVpmnInfo(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false; 
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryVpmnInfo", null, 'parentVpmnPart', 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearParentForm();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 清除数据
function clearParentForm(){
	$("#USER_ID").val("");
	$("#SERIAL_NUMBER").val("");
	$("#JURISTIC_NAME").val("");
	$("#CUST_NAME").val("");
	$("#GROUP_ADDR").val("");
	$("#GROUP_CONTACT_PHONE").val("");
	$("#POST_CODE").val("");
	$("#PSPT_TYPE").val("");
	$("#PSPT_ID").val("");
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyAll("outSerialNumberPart")) return false; 
	var condSerialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var serialNumber = $("#SERIAL_NUMBER").val()
	
	if(condSerialNumber != serialNumber){
		alert("输入号码[" + condSerialNumber + "]与办理业务号码[" + serialNumber + "]不一致, 请重新输入! ");
		return false;
	}
	
	return true;
}