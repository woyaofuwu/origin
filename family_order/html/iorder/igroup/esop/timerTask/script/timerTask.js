function timerFullReviewTask(){
	var ibsysId = $('#cond_IBSYSID').val();
	$.beginPageLoading('正在发起复核...');
	$.ajax.submit(null,'timerFullReviewTask','&IBSYSID='+ibsysId,'',function(data){
		$.endPageLoading();
		MessageBox.success("复核成功", "定单号："+data.get("IBSYSID"), function(btn){
			if("ok" == btn){
				closeNav();
			}
		});
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}