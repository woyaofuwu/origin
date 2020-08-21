function queryBatDealBySN(){
	var cond_SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var cond_BATCH_ID = $("#cond_BATCH_ID").val();
	if(cond_SERIAL_NUMBER == '' && cond_BATCH_ID == ''){
		alert('服务号码和批次号不能同时为空');
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryBatDealBySN', null, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function exportBeforeAction(domid) {
	var msisdn = $("#cond_SERIAL_NUMBER").val();
	var batchId = $("#cond_BATCH_ID").val();
	var startDate = $("#cond_START_TIME").val();
	var endDate = $("#cond_END_TIME").val();
	var params =  "&SERIAL_NUMBER=" + msisdn;
	params = params + "&BATCH_ID=" + batchId;
	params = params + "&START_TIME=" + startDate;
	params = params + "&END_TIME=" + endDate;
	
	$.Export.get(domid).setParams(params);
	return true;
}

