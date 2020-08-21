function queryInfo()
{
	var orderId = $('#ORDER_ID').val();
	var routeEparchyCode = $('#ROUTE_EPARCHY_CODE').val();
	var content = $('#CONTENT').val();
	
	var param = "";
	param += '&ORDER_ID='+orderId;
	param += '&ROUTE_EPARCHY_CODE='+routeEparchyCode;
	param += '&TEMPLATE_CONTENT='+content;
	$.ajax.submit(null,'queryInfo',param,null, function(d){
		$('#RESULT').val(d.get('RESULT'));
	});
}