function submitReqParam()
{
	
	var param = "&ID_VALUE=" + $("#ID_VALUE").val()
				+"&IDENT_CODE=" + $("#IDENT_CODE").val();
	$.ajax.submit(null, 'onTradeSubmit', param, '', function(data)
	{
		if("0000" == data.get("BIZ_ORDER_RESULT")){
			alert("登出成功！");
		}
	},	
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
	});
}