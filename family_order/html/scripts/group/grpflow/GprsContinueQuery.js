// 查询费用信息
function qryGrpsContinueLogs()
{
	if(!$.validate.verifyAll("queryForm")) return false;
	var groupId = $("#cond_GROUP_ID").val();
	var staffId = $("#cond_STAFF_ID").val();
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	if( (groupId == null || $.trim(groupId) == "") 
			&& (staffId == null || $.trim(staffId) == "")
			&& (startDate == null || $.trim(startDate) == "")
			&& (endDate == null || $.trim(endDate) == "")
			){
		alert("请至少输入一个查询条件!");
		return false;
	}
	if( ($.trim(startDate)!="" && $.trim(endDate) == "")
			|| ($.trim(endDate)!="" && $.trim(startDate) == "")
			){
		alert("请同时输入开始时间,结束时间再做查询!");
		return false;
	}
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryGrpsContinueLogs", null, "QueryResultPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}