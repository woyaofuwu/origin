/**
 * 初始化页面参数
 * */
function initBatchId(){
	$.beginPageLoading("正在进行中...");
	$.ajax.submit("BatScoreDonate","getBatchId",'','NewBatchId',function(data){
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
	$("#importKey").attr("disabled",false);
	$.beginPageLoading("刷新中...");
	$.ajax.submit('BatScoreDonate','init','','SubmitCondPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}



function importBatData(){
	
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("导入中...");
	$.ajax.submit('SubmitCondPart','importBatData','','',function(data){
		alert('导入成功！');
		//resetPage();
		$("#importKey").attr("disabled",true);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function checkAndSubmit(){
	
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("数据已提交！积分转赠处理中...请耐心等待");
	$.ajax.submit('SubmitCondPart','dealSubmit','','',function(data){
		alert('提交成功！');
		var batch_id = $("#BATCH_ID").val();
		$("#cond_BATCH_ID").val(batch_id);
		queryImportData()
		resetPage();

		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryImportData()
{
	 if(!$.validate.verifyAll("QueryPart")){
		 return false;
	 }
    $.ajax.submit('QueryPart', 'queryImportData', null, 'ImportDataPart',function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}