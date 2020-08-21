//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var custStr = data.get("CUST_INFO").toString();
	
	var param = "&CUST_INFO="+custStr;
	
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'custInfoPart', function()
	{
		$("#ibsearch").attr("className", "e_ico-search");
		$("#QuerySoft").attr("disabled",false);
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
//提交前校验
function checkBeforeSubmit()
{
	var software = $("#SOFTWARE").val();
	
	if(software == null || software == "")
	{
		alert("请查询并选择要下载的软件后再提交！");
		return false;
	}
	
	if(!$.validate.verifyAll("userInfoPart"))
	{
		return false;
	}
	return true ;
}

function querySoftware()
{
	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	
	param = "&AUTH_SERIAL_NUMBER=" + serialNum;
	
	$.ajax.submit(null, 'getApplicationSoftwareDs', param, 'userInfoPart', function()
	{
		$("#SOFTWARE").attr("disabled",false);
		$("#QuerySoft").attr("disabled",false);
	},	
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
	});
}