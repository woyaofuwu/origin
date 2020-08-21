// 查询方法
function qryInfoList(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryInfoList", null, "infoPart,hintPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 注销选择的记录
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	var checkValue = "";
	
	var tbodyList = $("#myGrpTBody input:checked");
	tbodyList.each(function(){
		checkValue += $(this).val() + "#";
	});
	
	if(checkValue == ""){
		alert("请选择要注销的记录！");
		return false;	
	}
	
	checkValue = checkValue.substring(0, checkValue.length - 1)
	$.cssubmit.setParam("CHECKVALUE_LIST", checkValue);
	
	//add by wangyf6 at 20160809
	if($('#MEB_FILE_SHOW').val()=="true"){
		var mebFileList = $('#MEB_FILE_LIST').val();
		if(mebFileList != null && mebFileList != ""){
			$.cssubmit.setParam("MEB_FILE_SHOW", "true");
			$.cssubmit.setParam("MEB_FILE_LIST", mebFileList);
		}
	}
	
	//add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
	if($("#MEB_VOUCHER_FILE_LIST")){
		var voucherFileList = $("#MEB_VOUCHER_FILE_LIST").val();
		if(voucherFileList==""){
			alert("请上传凭证信息！");
			return false;
		}else{
			$.cssubmit.setParam("MEB_VOUCHER_FILE_LIST", voucherFileList);
		}		
	}
	if($("#AUDIT_STAFF_ID")){
		var auditStaffId = $("#AUDIT_STAFF_ID").val();
		if(auditStaffId==""){
			alert("请选择稽核员！");
			return false;
		}else{
			$.cssubmit.setParam("AUDIT_STAFF_ID", auditStaffId);
		}		
	}
	
	var actionFlag = "";
	actionFlag = $("#ACTION_FLAG").val();
	if(actionFlag == ""){
		alert("请选择失效方式！");
		return false;	
	}
	
	$.cssubmit.setParam("ACTION_FLAG", actionFlag);
	$.cssubmit.setParam("SERIAL_NUMBER", $("#cond_SERIAL_NUMBER").val());
	
	return true;
}