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

function queryElecAgreementList(obj){

	debugger;
	//查询条件校验
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	 
	if(startDate == "" || startDate == null) 
	{
	  $.validate.alerter.one($("#START_DATE")[0], "开始时间必须填写!\n");
	  return false;
	}
	if(endDate == "" || endDate == null) 
	{
	  $.validate.alerter.one($("#END_DATE")[0], "结束时间必须填写!\n");
	  return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","queryElecAgreementList", "&START_DATE="+startDate+"&END_DATE="+endDate,'QueryResultPart', function(data){
			$.endPageLoading();
			hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}