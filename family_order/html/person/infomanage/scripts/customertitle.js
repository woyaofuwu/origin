
//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var cust_info = data.get("CUST_INFO").toString();
	
	var param = "&USER_INFO="+user_info+"&CUST_INFO="+cust_info;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'QueryPartTwo,custInfoPart,CustInfoTwoPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
