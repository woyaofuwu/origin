// 查询集团彩铃用户信息
function qryColorRingUser(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryColorRingUser", null, "payPlanPart", 
		function(data){
			$.endPageLoading();
			// 填充集团客户信息
			insertGroupCustInfo(data);
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			// 清空集团客户信息
			clearGroupCustInfo();
			showDetailErrorInfo(error_code, error_info, derror);
		}
    );
}

// 设置返回值
function setReturnData(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#PLAN_TYPE_CODE"))) return false;
	
	// 设置返回值
	var valueData = $.DataMap();
	
	// 设置返回值
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var palnTypeCode = $("#PLAN_TYPE_CODE").val();
	
	valueData.put("SERIAL_NUMBER", serialNumber);
	valueData.put("PLAN_TYPE", palnTypeCode);
	
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
	
	//$.setReturnValue({'POP_CODING_STR': "集团彩铃服务号码：" + serialNumber + "；付费方式：" + $("#PLAN_TYPE_CODE").text()}, false);
 	//$.setReturnValue({'CODING_STR': valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("集团彩铃服务号码：" + serialNumber + "；付费方式：" + $("#PLAN_TYPE_CODE").text());
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}


