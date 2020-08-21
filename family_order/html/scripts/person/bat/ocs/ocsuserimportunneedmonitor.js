/**
 * 初始化页面参数
 * */
function initBatchId(){
	$.beginPageLoading("正在进行中...");
	$.ajax.submit('',"getBatchId",'','NewBatchId',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * 初始化页面参数
 * */
function resetPage(){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','','SubmitCondPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * 导入数据
 * */
function importOcsNoNeedMonitorData(){
	/*
	if(!$.validate.verifyAll("SubmitCondPart")){
		return false;
	}
	*/
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importOcsNoNeedMonitorData','','',function(data){
		alert('导入成功！');
		resetPage();
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}