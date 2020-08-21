
function queryCancelTrade()
{
	if(!$.validate.verifyAll("QueryCondPart"))return false; 
	
	$.beginPageLoading("正在查询数据...");
	//用户优惠查询
	$.ajax.submit('QueryCondPart', 'queryCancelTrade', null, 'cancelInfoPart', function(data){
		$.endPageLoading();
		if(data && data.get("QUERY_CODE") == "N")
		{
			MessageBox.alert("提示",data.get("QUERY_INFO"));
//			$("#CSSUBMIT_BUTTON").attr("disabled",true);
			//关闭提交按钮
			if($.cssubmit){
				$.cssubmit.disabledSubmitBtn(true);
			}
		}else
		{
			//启用提交按钮
			if($.cssubmit){
				$.cssubmit.disabledSubmitBtn(false);
			}
//			$("#CSSUBMIT_BUTTON").attr("disabled",false);
//			$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示",error_info);
    });
}



/**
 * 提交前的校验方法
 * @return
 */
function commitCheck()
{
	MessageBox.confirm("告警提示","您确定取消该工单吗？",function(re){
		if(re=="ok"){
			$.cssubmit.submitTrade();
		}
	});
}