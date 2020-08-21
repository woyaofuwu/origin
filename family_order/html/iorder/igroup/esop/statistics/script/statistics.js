function qryAll(obj){
	
	$.beginPageLoading("数据查询中，请稍后...");
	$.ajax.submit('qryInfo','qryInfos',null,'queryPart',function(data){
		$.endPageLoading();
		hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}