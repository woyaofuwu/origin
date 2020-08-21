function refreshPartAtferAuth(data)
{
	var params = "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()
				+"&ACCT_INFO="+data.get("ACCT_INFO").toString()+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
				+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	$.beginPageLoading("宽带资料查询中......");
	$.ajax.submit(this, 'qryBroadBandUser', params, 'UserInfoArea', 
	function(dataset)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}
function onTradeSubmit()
{
	var accessAcct = $("#ACCESS_ACCT").val();
	if(null == accessAcct || "" == accessAcct)
	{
		alert("请先输入宽带号码进行查询！");
		return false;
	}
	
	var bookDestroyDate = $("#BOOK_DESTROY_DATE").val();
	var remark = $("#REMARK").val();
	var param = "&SERIAL_NUMBER="+accessAcct+"&BOOK_DESTROY_DATE="+bookDestroyDate+"&REMARK="+remark;
	$.cssubmit.addParam(param);
	if(confirm("确认对宽带用户【"+accessAcct+"】进行宽带拆机操作？"))
	{
		return true;
	}
	else
	{
		return false;
	}
}