function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'interRoamQuery',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'QueryListPart', function(data1){
		$.endPageLoading();
	},
	function(code, info, detail){
		
		$.endPageLoading();
		MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, detail);
		//alert(error_info);
    });
}