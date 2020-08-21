$(function(){
	var flag = $("#FLAG").val();
	var expInfo = $("#EXP_INFO").val();
	if(expInfo != "" && expInfo != null){
		MessageBox.error("错误信息", expInfo, function(btn){
			closeNav();
		});
	}
});

function qrySurveyList(obj){
	//查询条件校验
	var acceptState = $("#cond_ACCEPT_START").val();
	var acceptEnd = $("#cond_ACCEPT_END").val();
	var ibsysId = $("#cond_IBSYSID").val();
	var groupId = $("#cond_GROUP_ID").val();
	var staffId = $("#cond_STAFF_ID").val();
	var auditResult = $("#cond_AUDIT_RESULT").val();
	/*if(ibsysId == "" || ibsysId == null){
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
	}*/
	
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