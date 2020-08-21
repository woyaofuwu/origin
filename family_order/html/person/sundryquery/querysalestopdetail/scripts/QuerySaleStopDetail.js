function query()
{
	 if(!verifyAll('QueryCondPart'))
	 {
	    	return false;
	 }
	$.ajax.submit('QueryCondPart', 'queryDetailInfo', null, 'ResultDataPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function exportData() {
	$.beginPageLoading("导出中...");
	
	$.ajax.submit('QueryCondPart','exportExcel',null,'',function(data){
		if (data && data.get("url")) {
			window.open(data.get("url"),"_self");
		}
		$.endPageLoading();
	},function(e,i){
		$.showErrorMessage("导出失败");
		$.endPageLoading();
	});
}

