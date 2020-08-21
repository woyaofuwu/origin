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
	if (!queryBox(this, "viceCheckBox")) {
		return;
	}
	var remark = $("#REAUDIT_DESC").val();
	var fileList = $("#REAUDIT_FILE_LIST").val();
	var fileListDesc = $("#REAUDIT_FILE_LIST_DESC").val();
	if($.trim(remark)==""){
		alert("请填写整改意见！");
		return false;
	}

	MessageBox.confirm("提示信息", "确定要提交整改吗？", function(btn) {
		if ("ok" == btn) {
			$.beginPageLoading("提交处理中...");
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var tradetypecode = checkedObj.attr("tradetypecode");
				if(tradetypecode == "8981" || tradetypecode == "8982"){
					$.endPageLoading();
					alert("SA分配业务整改在原界面重新分配或者修改，不能在此整改SA分配业务！");
					return false;
				}
				var param = new Wade.DataMap();
				param.put("AUDIT_ID", checkedObj.val());
				param.put("ACCEPT_MONTH", checkedObj.attr("acceptMonth"));
				param.put("BIZ_TYPE", checkedObj.attr("biz_type"));
				param.put("IN_STAFF_ID", checkedObj.attr("inStaffId"))
				param.put("STATE", "3");	//状态改为已整改
				param.put("REAUDIT_DESC", remark);
				param.put("REAUDIT_FILE_LIST",fileList);//附件
				param.put("REAUDIT_FILE_LIST_DESC",fileListDesc);//附件说明
				params.add(param);
			}
			ajaxSubmit("ConditionPart,LogNav,QueryListPart", "submitReAudit",
					"&AUDIT_INFOS=" + params.toString(), "QueryListPart", function(data) {
						$("#REAUDIT_DESC").val("");
						$.endPageLoading();
						alert("提交稽核整改处理成功！");
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});

		}

	});
}
