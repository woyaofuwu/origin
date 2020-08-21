function importDelayData(){
	if(!$.validate.verifyAll("SubmitCondPart")) { 
		return false;
	}
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importOpenUserData','','',function(data){
		alert('导入成功！');
		$.endPageLoading();  
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}