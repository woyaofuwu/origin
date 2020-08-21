$(function(){
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "P");
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#ATTACH_FILE_NAME").val(obj.NAME);
		$("#ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#ATTACH_FILE_NAME").val("");
		$("#ATTACH_FILE_ID").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
});

function queryAllMarketing(){
	debugger;
	var ibsysidMarketing = $('#cond_IBSYSID_MARKETING').val();
	var resultCode = $('#cond_RESULT_CODE').val();
	var marketingEndDate = $('#cond_MARKETING_END_DATE').val();
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryAllMarketing", "&IBSYSID_MARKETING="+ibsysidMarketing+"&RESULT_CODE="+resultCode+"&MARKETING_END_DATE="+marketingEndDate, "ResultPart",function(data){
		$.endPageLoading();
	},null);
}

function submitInfos(){
	debugger;
	if (!$.validate.verifyAll("writeForm")) {
		return false;
	}
	var rowData = MarketingHandleTable.getCheckedRowsData("ckline");
	if(rowData==null||rowData==""){
		$.validate.alerter.one(rowData,"请先选择相关营销活动订单!");
		return false;
	}
	var ibsysidMarketing = rowData.get(0).get("IBSYSID_MARKETING");
	var handleOption = $('#msubmit_HANDLE_OPINION').val();
	var audit = $('#msubmit_IBSYSID_AUDIT').val();
	var handleInfo = $('#msubmit_HANDLE_INFO').val();
	var fileName = $("#ATTACH_FILE_NAME").val();
	
	var submitParam = new Wade.DataMap();
	submitParam.put("IBSYSID_MARKETING",ibsysidMarketing);
	submitParam.put("HANDLE_OPINION",handleOption);
	submitParam.put("IBSYSID_AUDIT",audit);
	submitParam.put("HANDLE_INFO",handleInfo);
	submitParam.put("COMMON_FILE",fileName);
	submitParam.put("ATTACH_LIST",saveAttach());
	
	$.beginPageLoading("数据处理中......");
	$.ajax.submit("", "submitInfos", "&SUBMIT_PARAM="+submitParam.toString(), "ResultPart",function(data){
		$.endPageLoading();
		MessageBox.confirm("提示", "提交成功！", function(btn) {
			window.reloadNav();
		});
	},null);
}

function saveAttach()
{
	debugger;
	//其他附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	return attachList;
}

function queryAllMarketingforAudit(){
	debugger;
	var ibsysidMarketing = $('#cond_IBSYSID_MARKETING').val();
	var resultCode = $('#cond_RESULT_CODE').val();
	var marketingEndDate = $('#cond_MARKETING_END_DATE').val();
	var acceptDate = $('#cond_ACCEPT_DATE').val();
	var customno = $('#cond_CUSTOMNO').val();
	var marketingIsSucc = $('#cond_MARKETING_IS_SUCC').val();
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryAllMarketing", "&IBSYSID_MARKETING="+ibsysidMarketing+"&RESULT_CODE="+resultCode+"&MARKETING_END_DATE="+marketingEndDate+"&ACCEPT_DATE="+acceptDate+"&CUSTOMNO="+customno+"&MARKETING_IS_SUCC="+marketingIsSucc, "ResultPart",function(data){
		$.endPageLoading();
	},null);
}

function submitInfosForAudit(){
	debugger;
	if (!$.validate.verifyAll("writeForm")) {
		return false;
	}
	var rowData = MarketingHandleTable.getCheckedRowsData("ckline");
	if(rowData==null||rowData==""){
		$.validate.alerter.one(rowData,"请先选择相关营销活动订单!");
		return false;
	}
	var ibsysidMarketing = rowData.get(0).get("IBSYSID_MARKETING");
	var auditOption = $('#msubmit_AUDIT_OPINION').val();
	var resultInfo = $('#msubmit_RESULT_INFO').val();
	
	$.beginPageLoading("数据处理中......");
	$.ajax.submit("", "submitInfosForAudit", "&IBSYSID_MARKETING="+ibsysidMarketing+"&RESULT_CODE="+auditOption+"&RESULT_INFO="+resultInfo, "ResultPart",function(data){
		$.endPageLoading();
		MessageBox.confirm("提示", "提交成功！", function(btn) {
			window.reloadNav();
		});
	},null);
}