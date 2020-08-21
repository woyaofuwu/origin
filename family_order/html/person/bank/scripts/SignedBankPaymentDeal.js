function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var param = "&USER_INFO="+user_info;
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'QueryPart,QueryTwoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
 

