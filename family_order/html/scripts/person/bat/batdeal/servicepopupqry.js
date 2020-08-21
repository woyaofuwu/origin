function tableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var serviceId = json.get('SERVICE_ID','');
	var serviceName = json.get('SERVICE_NAME','');
	setPopupReturnValue(serviceId,serviceName);
}

function queryServiceInfo(){
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryServiceInfo', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
