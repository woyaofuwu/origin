// 查询子VPMN信息
function qrySubVpmn(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qrySubVpmn", null, "subVpmnPart,parentVpmnPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			
			// 清空数据
			clearSubVpmnForm();
			clearSubVpmnForm();
			
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 业务受理前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#SUB_SERIAL_NUMBER"))) return false;
	
	if($("#cond_SERIAL_NUMBER").val() != $("#SUB_SERIAL_NUMBER").val()){
		alert("子VPMN编码与输入的VPMN编码不一致, 请重新输入!");
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
	$("#SUB_CUST_ID").val("");
	$("#SUB_USER_ID").val("");
}


