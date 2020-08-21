/**
 * 查询集团业务稽核工单信息
 * @return
 */
function queryGrpAuditInfos(){
		//查询条件校验
	if(!$.validate.verifyAll("ConditionPart")) {
		return false;
	}
	
	/*if($("#GROUP_ID").val()=="" && $("#CUST_NAME").val()==""){
		alert("请输入集团客户编码或名称！");
		return false;
	}*/
		
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if($.trim(startDate).length > 7){
		startDate = startDate.substring(0,7);
	}
	if($.trim(endDate).length > 7){
		endDate = endDate.substring(0,7);
	}
	if(startDate != endDate){
		alert("开始时间和结束时间必须在同一个月内!");
		return false;
	}
	
	var auditstartDate = $("#AUDIT_START_DATE").val();
	var auditendDate = $("#AUDIT_END_DATE").val();
	if($.trim(auditstartDate).length > 7){
		auditstartDate = auditstartDate.substring(0,7);
	}
	if($.trim(auditendDate).length > 7){
		auditendDate = auditendDate.substring(0,7);
	}
	if(auditstartDate != auditendDate){
		alert("开始时间和结束时间必须在同一个月内!");
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart', 'queryGrpAuditInfos', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/**
 * 提交稽核处理
 * @return
 */
function submitAudit() {
	var isBizStaff = $("#IS_BIZSTAFF").val();
	var isAuditStaff = $("#IS_AUDITSTAFF").val();
	if(isAuditStaff=="false"){
		alert("您不是稽核员，不能稽核工单！");
		return false;
	}
	if (!queryBox(this, "viceCheckBox")) {
		return;
	}
	var auditState = $("#AUDIT_STATE").val();
	var auditDesc = $("#AUDIT_DESC").val();
	if(auditState==""){
		alert("请选择稽核结果！");
		return false;
	}
	if($.trim(auditDesc)==""){
		alert("请填写备注！");
		return false;
	}

	MessageBox.confirm("提示信息", "确定要提交稽核吗？", function(btn) {
		if ("ok" == btn) {
			$.beginPageLoading("提交稽核处理中...");
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var param = new Wade.DataMap();
				param.put("AUDIT_ID", checkedObj.val());
				param.put("ACCEPT_MONTH", checkedObj.attr("acceptMonth"));
				param.put("BIZ_TYPE", checkedObj.attr("biz_type"));
				param.put("IN_STAFF_ID", checkedObj.attr("inStaffId"))
				param.put("STATE", auditState);
				param.put("AUDIT_DESC", auditDesc);
				params.add(param);
			}
			ajaxSubmit("ConditionPart,LogNav,QueryListPart", "submitAudit",
					"&AUDIT_INFOS=" + params.toString(), "QueryListPart", function(data) {
						$("#AUDIT_STATE").val("");
						$("#AUDIT_DESC").val("");
						$.endPageLoading();
						alert("提交稽核处理成功！");
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});

		}

	});
}

/**
 *批量转派人员
 * @return
 */
function submitTransfer(){
	var isBizStaff = $("#IS_BIZSTAFF").val();
	var isAuditStaff = $("#IS_AUDITSTAFF").val();
	if(isAuditStaff=="false"){
		alert("您不是稽核员，不能转派工单！");
		return false;
	}
	var auditStaffId = $("#AUDIT_STAFF_ID").val();
	if(auditStaffId==""){
		alert("转派人员时，请选择稽核人员！");
		return false;
	}
	MessageBox.confirm("提示信息", "确定要转派给"+auditStaffId+"吗？", function(btn) {
		
		if ("ok" == btn) {
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			if(check.length<=0){
				alert("请选择转派工单！");
				return false;
			}
			
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var param = new Wade.DataMap();
				if(checkedObj.attr("inStaffId")==auditStaffId){
					alert("转派人员时，转派人员不能同办理人员相同！");
					return false;
				}
				param.put("AUDIT_ID", checkedObj.val());
				param.put("ACCEPT_MONTH", checkedObj.attr("acceptMonth"));
				param.put("BIZ_TYPE", checkedObj.attr("biz_type"));
				
				param.put("TRANSFER_AUDIT_STAFF_ID", auditStaffId);
				
				params.add(param);
			}
			$.beginPageLoading("转派中...");
			ajaxSubmit("ConditionPart,LogNav,QueryListPart", "submitTransfer",
					"&AUDIT_INFOS=" + params.toString(), "QueryListPart", function(data) {
						$("#AUDIT_STATE").val("");
						$("#AUDIT_DESC").val("");
						$.endPageLoading();
						alert("转派成功！");
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});
		}
		
	});
	
}
