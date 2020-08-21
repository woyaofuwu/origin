//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var custStr = data.get("CUST_INFO").toString();
	
	var param = "&CUST_INFO="+custStr;
	
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'custInfoPart', function()
	{
		$("#bsearch").attr("disabled",false);
		$("#ibsearch").attr("className", "e_ico-search");
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function getFirmwareReturnDs()
{
	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	
	param = "&AUTH_SERIAL_NUMBER=" + serialNum;
	
	$.ajax.submit(null, 'getFirmwareReturnDs', param, 'rollBackBagIdPart', function()
	{
		$("#EXECUTEMODE").attr("disabled",false);
	    $("#ROLLBACKBAGID").attr("disabled",false);
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
	
	var rollbackbagid = $("#ROLLBACKBAGID");
	var executemode  = $("#EXECUTEMODE");
	
	if(!$.validate.verifyField(rollbackbagid[0]) || !$.validate.verifyField(executemode[0])){
		return false;
     }
	
	return true ;
}