// 查询方式改变
function changeQueryType(){

	var condQueryType = $("#cond_QueryType").val();
	
	// 按集团客户编码查询
	if("0" == condQueryType){
		
		$("#groupId").css("display", "");
		$("#cond_GROUP_ID").attr("nullable", "no");
		
		$("#cond_SERIAL_NUMBER").val("");
		$("#serialNumber").css("display", "none");
		$("#cond_SERIAL_NUMBER").attr("nullable", "yes");
		
		$("#SubmitPart").css("display", "");
		$("#endType").css("display", "none");
	}
	// 按成员服务号码查询
	else if("1" == condQueryType){
	
		$("#cond_GROUP_ID").val("");
		$("#groupId").css("display", "none");
		$("#cond_GROUP_ID").attr("nullable", "yes");
		
		$("#serialNumber").css("display", "");
		$("#cond_SERIAL_NUMBER").attr("nullable", "no");
		
		$("#SubmitPart").css("display", "");
		$("#endType").css("display", "");
	}
}

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
	
	if("1" == $("#cond_QueryType").val()) {
		var tbodyList = $("#myTBody input:checked");
		
		tbodyList.each(function(){
			checkValue += $(this).val() + "#";
		});
	
	}else if("0" == $("#cond_QueryType").val()){
	
		var tbodyList = $("#myGrpTBody input:checked");
		
		tbodyList.each(function(){
			checkValue += $(this).val() + "#";
		});
		
	}
	
	if(checkValue == ""){
		alert("请选择要注销的记录！");
		return false;	
	}
	
	checkValue = checkValue.substring(0, checkValue.length - 1);
	$.cssubmit.setParam("CHECKVALUE_LIST", checkValue);
	
	if("1" == $("#cond_QueryType").val())
	{
		$.cssubmit.setParam("SERIAL_NUMBER", $("#cond_SERIAL_NUMBER").val());
	}
	
	var actionFlag = "";
	if("1" == $("#cond_QueryType").val()) {
	
		actionFlag = $("#ACTION_FLAG").val();
		if(actionFlag == ""){
			alert("请选择失效方式！");
			return false;	
		}	
	
	}else if("0" == $("#cond_QueryType").val()){
	
		actionFlag = "2";
		
	}
	
	$.cssubmit.setParam("ACTION_FLAG", actionFlag);
	
	var smsFlag = $("#SMS_FLAG").attr("checked");
	if(smsFlag){
		$.cssubmit.setParam("SMS_FLAG", "1");
	}else{
		$.cssubmit.setParam("SMS_FLAG", "0");
	}
	
	//add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
	if($("#MEB_VOUCHER_FILE_LIST")){
		var voucherFileList = $("#MEB_VOUCHER_FILE_LIST").val();
		if(voucherFileList==""){
			alert("请上传凭证信息!");
			return false;
		}else{
			$.cssubmit.setParam("MEB_VOUCHER_FILE_LIST", voucherFileList);
		}
	}
	if($("#AUDIT_STAFF_ID")){
		var auditStaffId = $("#AUDIT_STAFF_ID").val();
		if(auditStaffId==""){
			alert("请选择稽核员!");
			return false;
		}else{
			$.cssubmit.setParam("AUDIT_STAFF_ID", auditStaffId);
		}
	}
	
	var hasGpwpUsr = false;
	var arr1 = checkValue.split("#");
	for(var i=0;i<arr1.length;i++){
		var row = arr1[i];
		var colArr = row.split(",");
		if(colArr[4]=='1'){
			hasGpwpUsr = true;
			break;
		}
	}
	if(hasGpwpUsr){
		if("1" == $("#cond_QueryType").val()){
			alert("该成员为集团工作手机产品成员，请确认是否需要取消成员及工作手机折扣套餐，如需取消，请在“集团产品成员注销”界面注销！");
		}else{
			if(confirm("成员已订购集团工作手机产品(具体看备注列)，请确认是否需要取消成员及工作手机折扣套餐，如需取消，请在“集团产品成员注销”界面注销！")){
				return true;
			}else{
				return false;
			}
		}
	}
	
	return true;
}

