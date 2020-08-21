
// 根据成员服务号码查询成员开户信息
function queryMemberInfo() 
{ 
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) 
		return false;	
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	$.beginPageLoading("正在查询...");
    $.ajax.submit(this, "queryMemberInfo", "&SERIAL_NUMBER="+serialNumber, "memCustInfo,memUserInfo,memAcctInfo,removeInfo", 
    		function(data){ 
				$.endPageLoading();
			},
			function(error_code,error_info,detail)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,detail);
			}
	);
}

// 记住销户原因
function reasonChange(obj)
{ 
   var oReason = $("#REMOVE_REASON"); 
   oReason.val(obj.options[obj.selectedIndex].text);  
}

// 提交前校验
function onSubmitBaseTradeCheck()
{
	
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) 
		return false;	
	if(!$.validate.verifyField($("#REMOVE_REASON_CODE"))) 
		return false;
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	var serialNumberBak = $("#SERIAL_NUMBER_BAK").val();
	var remark = $("#REMARK").val();
	
	if(!(serialNumber == serialNumberBak))
	{
		MessageBox.alert("提示信息","用户服务号码异常,请重新输入查询！");
		return false;
	}		
	if(remark.length > 100)
	{
    	MessageBox.alert("提示信息","输入备注的值不能越界！");
    	return false;
    }	
    
	
    // 设置提交数据(SERIAL_NUMBER,REMOVE_REASON,REMOVE_REASON_CODE,REMARK,USER_EPARCHY_CODE)
    $.cssubmit.setParam("SERIAL_NUMBER", $("#SERIAL_NUMBER").val());
    $.cssubmit.setParam("REMOVE_REASON", $("#REMOVE_REASON").val());
    $.cssubmit.setParam("REMOVE_REASON_CODE", $("#REMOVE_REASON_CODE").val());
    $.cssubmit.setParam("REMARK", $("#REMARK").val());
    $.cssubmit.setParam("USER_EPARCHY_CODE", $("#USER_EPARCHY_CODE").val());
    
	return true; 	
}
