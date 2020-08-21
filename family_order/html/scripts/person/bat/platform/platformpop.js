function tableRowDBClick(){
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var serviceId = json.get('SERVICE_ID','');
	var serviceName = json.get('SERVICE_NAME','');
	setPopupReturnValue(serviceId,serviceName);
}

function queryServiceInfoForPlat(){
	var bizTypeCode = $("#PARAM_BIZ_TYPE_CODE").val();
	if(!$.validate.verifyAll())
	{
		return;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryServiceInfoForPlat', '', 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
