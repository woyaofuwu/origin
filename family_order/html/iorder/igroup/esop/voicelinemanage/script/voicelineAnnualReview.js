function queryAll(){
	
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('queryForm','queryByCondition',null,'refreshtable',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function addOrUpdateCheckRecord(){
	
	$.beginPageLoading('数据提交中...');
	$.ajax.submit('checkRecordPart','submitInfos',null,'refreshtable',function(data){
		$.endPageLoading();
		MessageBox.success("成功提示","提交成功！");
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
