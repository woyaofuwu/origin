$(function(){
	debugger;
	
	$("#myExport").beforeAction(function(e){
		if ($("#tableBody").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
	
});

function queryBusiInfos(obj){

	debugger;
	//查询条件校验
	var cityCode = $("#cond_CITY_CODE").val();
	var ibsysId = $("#cond_IBSYSID").val();
	var custName = $("#cond_CUST_NAME").val();
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","queryBusiInfos", "&CITY_CODE="+cityCode+"&IBSYSID="+ibsysId+"&CUST_NAME="+custName,'QueryResultPart', function(data){
			$.endPageLoading();
			hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function querybusiAudit(obj){
	var bpmTempletId = $(obj).attr("BPM_TEMPLET_ID");
	var ibsysId = $(obj).attr("IBSYSID");
	var flag = $(obj).attr("FLAG");
	openNav("业务详情", "igroup.busiCheck400.busiDetail400", "queryData",  "&FLAG="+flag+"&IBSYSID="+ibsysId+"&BPM_TEMPLET_ID="+bpmTempletId, "/order/iorder");

	
}