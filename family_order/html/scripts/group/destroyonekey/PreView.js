function validate()
{
	if(!$.validate.verifyAll("previewInfoPart")) return false;
	
	//add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
	if($("#MEB_VOUCHER_FILE_LIST")&&$("#MEB_VOUCHER_FILE_LIST").val() == ""){
		alert("请上传凭证信息！");
		return false;
	}
	if($("#AUDIT_STAFF_ID")&&$("#AUDIT_STAFF_ID").val() == ""){
		alert("请选择稽核员！");
		return false;
	}
	
	var mebCount = $("#MEB_COUNT").val();
	
	if(!confirm("还有[" + mebCount + "]位成员使用了集团产品,是否确认注销!"))
	{
		return false;
	}
	
	return true;
}