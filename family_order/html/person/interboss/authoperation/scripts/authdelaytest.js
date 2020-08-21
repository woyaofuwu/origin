function submitReqParam()
{
	
	var param = "&ID_VALUE=" + $("#ID_VALUE").val()
				+"&IDENT_CODE=" + $("#IDENT_CODE").val()
				+"&EFFECTIVE_TIME=" + $("#EFFECTIVE_TIME").val();
	$.ajax.submit(null, 'onTradeSubmit', param, '', function(data)
	{
		if("0000" == data.get("BIZ_ORDER_RESULT")){
			alert("延时成功！");
		} else {
			alert("延时失败！归属省返回错误：" + data.get("RESULT_DESC"));
		}
	},	
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
	});
}