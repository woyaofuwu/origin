$(function(){
	debugger;
	$("#myExport").beforeAction(function(e){
		if ($("#tableBody").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
//	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
//	if(groupInfo!=undefined && groupInfo!=null){
//		 $("#POP_cond_GROUP_ID").val(groupInfo.get("GROUP_ID"));
//		/*$("#cond_GROUP_NAME").val(groupInfo.get("GROUP_NAME"));*/
//	}		
});

function querySurveyList(obj){

	debugger;
	//查询条件校验
	var acceptState = $("#ACCEPT_START").val();
	var acceptEnd = $("#ACCEPT_END").val();
	var ibsysId = $("#IBSYSID").val();
	var subTypeOpen = $("#SUB_TYPE_OPEN").val();
	if(acceptState == "" || acceptState == null) 
	{
	  $.validate.alerter.one($("#ACCEPT_START")[0], "开始时间必须填写!\n");
	  return false;
	}
	if(acceptEnd == "" || acceptEnd == null) 
	{
	  $.validate.alerter.one($("#ACCEPT_END")[0], "结束时间必须填写!\n");
	  return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","querySurveyList", "&ACCEPT_START="+acceptState+"&ACCEPT_END="+acceptEnd+"&IBSYSID="+ibsysId+"&SUB_TYPE_OPEN="+subTypeOpen,'QueryResultPart', function(data){
			$.endPageLoading();
			hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}
function qrySurveyList(obj){

	debugger;
	//查询条件校验
	var acceptState = $("#cond_ACCEPT_START").val();
	var acceptEnd = $("#cond_ACCEPT_END").val();
	var ibsysId = $("#cond_IBSYSID").val();
	var groupId = $("#cond_GROUPID").val();
	var staffId = $("#cond_STAFFID").val();
	var auditResult = $("#cond_AUDIT_RESULT").val();
	if(ibsysId == "" || ibsysId == null){
		if(acceptState == "" || acceptState == null) 
		{
		  $.validate.alerter.one($("#cond_ACCEPT_START")[0], "开始时间必须填写!\n");
		  return false;
		}
		if(acceptEnd == "" || acceptEnd == null) 
		{
		  $.validate.alerter.one($("#cond_ACCEPT_END")[0], "结束时间必须填写!\n");
		  return false;
		}
	}
	
	
	$.beginPageLoading("数据查询中......");
	var params = "&cond_ACCEPT_START="+acceptState+"&cond_ACCEPT_END="+acceptEnd+"&cond_IBSYSID="+ibsysId+"&cond_GROUP_ID="
		+groupId+"&cond_STAFF_ID="+staffId+"&cond_AUDIT_RESULT="+auditResult;
	$.ajax.submit("","querySurveyList", params,'QueryResultPart', function(data){
			$.endPageLoading();
			hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}