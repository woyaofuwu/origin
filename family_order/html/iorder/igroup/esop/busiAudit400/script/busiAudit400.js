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