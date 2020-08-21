function queryPopuTaskInfo(){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	if($("#QUERY_PARAM").val()==null||$("#QUERY_PARAM").val()==""){
		alert('任务编号或号码不能为空！');
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryPopuTaskInfo', '', 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function tableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var dBatchTaskId = json.get('BATCH_TASK_ID','');
	var dBatchTaskName = json.get('BATCH_TASK_NAME','');
	setPopupReturnValue(dBatchTaskId,dBatchTaskName);
}