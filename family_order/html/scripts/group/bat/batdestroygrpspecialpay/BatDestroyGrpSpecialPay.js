// 设置返回值
function setReturnData(){

	var queryType = $("#cond_QueryType").val();
	if(queryType == null || queryType == "")
	{
		alert("请选择查询方式!");
		return false;
	}

	var actionFlag = $("#ACTION_FLAG").val();
	if(actionFlag == null || actionFlag == "")
	{
		alert("请选择失效方式!");
		return false;
	}
							
	// 设置返回值
	var valueData = $.DataMap();
	
	valueData.put("QUERY_TYPE", queryType);
	valueData.put("ACTION_FLAG", actionFlag);
	
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
	
	var showStr = "查询方式：" + queryType + " 失效方式:" + actionFlag;
	parent.$('#POP_CODING_STR').val(showStr);
	parent.$('#CODING_STR').val(valueData);
	
	parent.hiddenPopupPageGrp();
}


