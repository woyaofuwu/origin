// 查询集团用户信息
function qryGrpUser(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryGrpUser", null, 'custPart,userPart,acctPart,remarkPart', 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearCustForm();
			clearUserForm();
			clearAcctForm();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyAll("remarkPart")) return false;
	
	if($("#cond_SERIAL_NUMBER").val() != $("#SERIAL_NUMBER").val()){
		alert("注销服务号码与输入服务号码不一致");
		return false;
	}
	
	if($("#AUDIT_STAFF_ID") && $("#AUDIT_STAFF_ID").val()==""){
		alert("请选择稽核人员！");
		return false;
	}
	
	return true;
}

// 清空集团客户信息
function clearCustForm(){
	
	$("#CUST_NAME").val("");
	$("#PSPT_TYPE").val("");
	$("#PSPT_ID").val("");
	$("#POST_CODE").val("");
	$("#GROUP_CONTACT_PHONE").val("");
	$("#POST_ADDRESS").val("");
}

//  清空集团用户信息
function clearUserForm(){
	
	$("#JURISTIC_TYPE_CODE").val("");
	$("#PRODUCT_NAME").val("");
	$("#BRAND").val("");
	$("#OPEN_DATE").val("");
}

// 清空集团账户信息
function clearAcctForm(){
	
	$("#PAY_NAME").val("");
	$("#PAY_MODE").val("");
	$("#BANK").val("");
	$("#BANK_ACCT_NO").val("");
	$("#OWE_FEE").val("");
	$("#LEFT_MONEY").val("");
	$("#FOREGIFT").val("");
	$("#REMOVE_REASON").val("");
	$("#REMARK").val("");
}