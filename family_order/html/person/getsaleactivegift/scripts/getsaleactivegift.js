function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'UserGiftInfoPart', 
	function()
	{
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



/*提交*/
function onTradeSubmit()
{
	if(!$.validate.confirmAll()) return false;
	return true;
}