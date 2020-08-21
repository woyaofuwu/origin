function queryUserComplaints(){
	var ibsysId = $('#cond_SUBSCRIBE_ID').val();
	var busiType = "FOURMANAGE";	
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit(null,'queryData','&BUSI_TYPE='+busiType+'&IBSYSID='+ibsysId,'QueryResultPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}