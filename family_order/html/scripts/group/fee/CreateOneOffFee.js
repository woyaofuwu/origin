// 集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	
	// 填充集团客户显示信息
	insertGroupCustInfo(data);
	
	// 查询费用信息
	qryGrpOneOffFee();
}

// 集团资料查询失败后调用的方法
function selectGroupErrorAfterAction(data){

	// 清空填充的集团客户信息内容
    clearGroupCustInfo();
}

// 查询费用信息
function qryGrpOneOffFee(){
	
	var groupId = $("#POP_cond_GROUP_ID").val();
	
	if(groupId == null || groupId == ""){
		alert("集团客户编码不能为空");
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit(null, "qryOneFeeList", "&GROUP_ID=" + groupId, "feeListPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			// 清空填充的集团客户信息内容
			clearGroupCustInfo();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 新增费用
function addFee(){

	if(!$.validate.verifyAll("feePart")) return false;
	
	$("#RSRV_STR2_NAME").val($('#RSRV_STR2 option:selected').text()); 
	
	$("#RSRV_STR3_NAME").val($('#RSRV_STR3 option:selected').text()); 
	
	$("#FEE_TYPE_NAME").val($('#FEE_TYPE_CODE option:selected').text());
	
	$("#RSRV_STR5_NAME").val($('#RSRV_STR5 option:selected').text()); 
	
	var feePartData = $.ajax.buildJsonData("feePart");
	
	$.table.get("FeeTable").addRow(feePartData);
}

// 修改费用
function updateFee(){
	
	if(!$.validate.verifyAll("feePart")) return false;
	
	$("#RSRV_STR2_NAME").val($('#RSRV_STR2 option:selected').text()); 
	
	$("#RSRV_STR3_NAME").val($('#RSRV_STR3 option:selected').text()); 
	
	$("#FEE_TYPE_NAME").val($('#FEE_TYPE_CODE option:selected').text());
	
	$("#RSRV_STR5_NAME").val($('#RSRV_STR5 option:selected').text());  
	
	var feePartData = $.ajax.buildJsonData("feePart");
	
	$.table.get("FeeTable").updateRow(feePartData);
}

// 删除费用
function deleteFee(){
	
	$.table.get("FeeTable").deleteRow();
}

function tableRowClick(){
	
	var rowData = $.table.get("FeeTable").getRowData();
	
	$("#RSRV_STR1").val(rowData.get("RSRV_STR1"));
	$("#RSRV_STR2").val(rowData.get("RSRV_STR2"));
	$("#RSRV_STR2_NAME").val(rowData.get("RSRV_STR2_NAME"));
	$("#RSRV_STR3").val(rowData.get("RSRV_STR3"));
	$("#RSRV_STR3_NAME").val(rowData.get("RSRV_STR3_NAME"));
	$("#RSRV_STR4").val(rowData.get("RSRV_STR4"));
	$("#FEE_TYPE_CODE").val(rowData.get("FEE_TYPE_CODE"));
	$("#FEE_TYPE_NAME").val(rowData.get("FEE_TYPE_NAME"));
	$("#RSRV_STR5").val(rowData.get("RSRV_STR5"));
	$("#RSRV_STR5_NAME").val(rowData.get("RSRV_STR5_NAME"));
	$("#FEE").val(rowData.get("FEE"));
}

// 业务受理前的校验
function onSubmitBaseTradeCheck(){
	
	var custId = $("#CUST_ID").val();
	
	if(custId == null || custId == ""){
		alert("集团客户编码不能为空");
		return false;
	}
	
	var feeData = $.table.get("FeeTable").getTableData()
	
	if(feeData == null || feeData == "" || feeData == "[]"){
		alert("集团一次性费用信息不能为空");
		return false;
	}
	
	if (!window.confirm("确定提交吗？")) return false;
	
	$.cssubmit.setParam("CUST_ID", custId);
	$.cssubmit.setParam("FEE_SUB_LIST", feeData);
	
	return true;
}