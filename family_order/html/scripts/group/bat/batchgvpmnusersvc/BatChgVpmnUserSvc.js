// 查询VPMN用户信息
function qryVpmnUser(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryVpmnUser", null, "queryForm", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
    );
}

// 导出校验
function exportCheck(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	return true;
}

// 设置返回值
function setReturnData(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	if(serialNumber == null || serialNumber == ""){
		alert("集团服务号码不能为空!");
		return false;
	}
	
	var operType = $("#OPER_TYPE").val();
	
	// 设置返回值
	var valueData = $.DataMap();
	valueData.put("X_SUBTRANS_CODE", "ITF_CRM_TcsGrpIntf");
	valueData.put("X_TRANS_CODE", "GrpBat");
	valueData.put("IN_MODE_CODE", "v");
	valueData.put("OPER_TYPE", operType);
	//add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
	if($('#MEB_VOUCHER_FILE_LIST')){
		var mebVoucherFileList = $('#MEB_VOUCHER_FILE_LIST').val();
		if( mebVoucherFileList== ""){
			alert("请上传凭证附件！");
			return false;
		}else{
			valueData.put('MEB_VOUCHER_FILE_LIST', mebVoucherFileList);
		}		
	}
	if($('#AUDIT_STAFF_ID')){
		var auditStaffId = $('#AUDIT_STAFF_ID').val();
		if( auditStaffId== ""){
			alert("请选择稽核员！");
			return false;
		}else{
			valueData.put('AUDIT_STAFF_ID', auditStaffId);
		}		
	}
	//alert(valueData);
	//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
	
	//$.setReturnValue({'POP_CODING_STR': "操作类型:[" + $("#OPER_TYPE").text() + "]"}, false);
 	//$.setReturnValue({'CODING_STR': valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("操作类型:[" + $("#OPER_TYPE").text() + "]");
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}
