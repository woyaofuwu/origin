function queryBatDealBySN(){
	if($("#cond_SERIAL_NUMBER").val()==null||$("#cond_SERIAL_NUMBER").val()==""){
		alert('服务号码不能为空！');
		return false;
	}
	if(!$.validate.verifyAll("QueryCondPart")){
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryBatDealBySN', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//id:domid
function exportBeforeAction(domid) {
	
	var msisdn = $("#cond_SERIAL_NUMBER").val();
	var startDate = $("#cond_START_TIME").val();
	var endDate = $("#cond_END_TIME").val();
	var params =  "&SERIAL_NUMBER=" + msisdn;
	params = params + "&START_TIME=" + startDate;
	params = params + "&END_TIME=" + endDate;
	$.Export.get(domid).setParams(params);
	return true;
}
