$(function(){
	$("#myExport2").beforeAction(function(e){
		return confirm('是否导入?');
	});
//	
	$("#myExport2").afterAction(function(e, status){
		debugger;
		if('ok' == status){
			$.beginPageLoading("正在处理...");
			qryMobileCloudInfos("mypop");
			$.endPageLoading();
			MessageBox.success("导入成功！", "页面已自动刷新!",function(){
			});
		}
	});

});

function qryMobileCloudInfos(obj){

	var param = '';

	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('qryInfo','qryMobileCloudInfos',null,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function isBlank(obj){
	if(obj == undefined ||obj==null||obj==''){
		return true;
	}
	return false;
}
