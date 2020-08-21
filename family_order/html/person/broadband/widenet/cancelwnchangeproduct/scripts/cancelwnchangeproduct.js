
function queryCancelTrade()
{
	if(!$.validate.verifyAll("QueryCondPart"))return false; 
	
	$.beginPageLoading("正在查询数据...");
	//用户优惠查询
	$.ajax.submit('QueryCondPart', 'queryCancelTrade', null, 'cancelInfoPart', function(data){
		$.endPageLoading();
		if(data && data.get("QUERY_CODE") == "N")
		{
			alert(data.get("QUERY_INFO"));
			$("#CSSUBMIT_BUTTON").attr("disabled",true);
		}else
		{
			$("#CSSUBMIT_BUTTON").attr("disabled",false);
			$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



/**
 * 提交前的校验方法
 * @return
 */
function commitCheck()
{
	
	if(confirm('您确定取消该工单吗？'))
	{
		return true;
	}
	return false;
}