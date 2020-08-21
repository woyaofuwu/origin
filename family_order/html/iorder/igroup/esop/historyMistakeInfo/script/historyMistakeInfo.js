function queryWorkformDetail(obj){

	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('AdvanceConditionPart','queryHistoryMistakeInfo',null,'QueryResultPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}