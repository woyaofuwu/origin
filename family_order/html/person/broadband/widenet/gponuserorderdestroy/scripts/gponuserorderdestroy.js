function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE");
	param += "&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");

    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'BusiInfoPart,wideInfoPart,destroyInfoPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

//提交校验
function onSubmit()
{
	var hiddenState = $("#HIDDEN_STATE").val();
	if(hiddenState!="")//已预约的
	{
		alert("已预约过拆机！");
		return false;
	}

	return true;
}
