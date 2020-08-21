function queryOcsDealInfo(){
	if(!$.validate.verifyAll("QueryCondPart")){
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryOcsDealInfo', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function exportBeforeAction(domid) {
	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	var batchid = $("#BATCH_ID").val();
	var msisdn = $("#SERIAL_NUMBER").val();
	var params =  "&BATCH_ID=" + batchid;
	var params =  "&SERIAL_NUMBER=" + msisdn;
	params = params + "&START_DATE=" + startDate;
	params = params + "&END_DATE=" + endDate;
	$.Export.get(domid).setParams(params);
	return true;
}

