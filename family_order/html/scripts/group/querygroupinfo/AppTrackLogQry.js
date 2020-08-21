function AppTrackLogQuery() {
	
	if(!$.validate.verifyAll('queryForm')) return false;
	
	$.beginPageLoading();
	$.ajax.submit('queryForm','qryAppTrackLogs', '','refreshtable,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}  

function deleteAppTrackLogs() {
	
	if(!$.validate.verifyAll('queryForm')) return false;
	
	MessageBox.confirm("提示信息","确定删除跟踪日志吗?",function(btn){
		if(btn=='ok'){
			
		    $.beginPageLoading();
			$.ajax.submit('queryForm','delAppTrackLogs', '','refreshtable,refreshHintBar', function(data){
				$.endPageLoading(); 
				$.showSucMessage("跟踪日志删除成功!","");
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}
	});
	
}
