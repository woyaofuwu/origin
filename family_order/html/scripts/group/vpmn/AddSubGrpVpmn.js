// 查询母VPMN信息
function qryParentVpmn(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryParentVpmn", null, "parentVpmnPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearParentVpmnForm();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 查询子VPMN信息
function qrySubVpmn(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	if(!$.validate.verifyField($("#SUB_SERIAL_NUMBER"))) return false;
	
	var parentSerialNumber = $("#cond_SERIAL_NUMBER").val();
	var subSerialNumber = $("#SUB_SERIAL_NUMBER").val();
	
	if(parentSerialNumber == subSerialNumber){
		alert("子VPMN编码与母VPMN编码相同, 请重新输入!");
		return false;
	}
	
	var param = "&PARENT_SERIAL_NUMBER=" + parentSerialNumber + "&SUB_SERIAL_NUMBER=" + subSerialNumber;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit(null, "qrySubVpmn", param, "subVpmnPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearSubVpmnForm();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 业务受理前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#PARENT_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#HIDDEN_SUB_SERIAL_NUMBER"))) return false;
	
	if($("#cond_SERIAL_NUMBER").val() != $("#PARENT_SERIAL_NUMBER").val()){
		alert("母VPMN编码与输入的VPMN编码不一致, 请重新输入!");
		return false;
	}
	
	if($("#SUB_SERIAL_NUMBER").val() != $("#HIDDEN_SUB_SERIAL_NUMBER").val()){
		alert("子VPMN编码与输入的VPMN编码不一致, 请重新输入!");
		return false;
	}
	
	if($("#PARENT_SERIAL_NUMBER").val() == $("#HIDDEN_SUB_SERIAL_NUMBER").val()){
		alert("子VPMN编码与母VPMN编码相同, 请重新输入!");
		return false;
	}
	
	return true;
}

// 清空母VPMN信息
function clearParentVpmnForm(){
	
	$("#PARENT_SERIAL_NUMBER").val("");
	$("#PARENT_CUST_NAME").val("");
	$("#PARENT_PRODUCT_NAME").val("");
	$("#PARENT_GROUP_CONTACT_PHONE").val("");
	$("#PARENT_CUST_ID").val("");
	$("#PARENT_USER_ID").val("");
}

// 清子VPMN信息
function clearSubVpmnForm(){
	
	$("#SUB_SERIAL_NUMBER").val("");
	$("#SUB_CUST_NAME").val("");
	$("#SUB_PRODUCT_NAME").val("");
	$("#SUB_GROUP_CONTACT_PHONE").val("");
	$("#REMARK").val("");
	$("#SUB_CUST_ID").val("");
	$("#SUB_USER_ID").val("");
	$("#HIDDEN_SUB_SERIAL_NUMBER").val("");
}


