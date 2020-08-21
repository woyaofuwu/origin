// 查询方式改变
// 注销选择的记录
function onSubmitBaseTradeCheck(){
	
	var tbodyList = $("#myTBody input:checked");
	
	var checkValue = "";
	
	tbodyList.each(function(){
		checkValue += $(this).val() + "#";
	});
	
	
	if(checkValue == ""){
		alert("请选择要注销的记录！");
		return false;	
	}
	
	checkValue = checkValue.substring(0, checkValue.length - 1)
	
	$.cssubmit.setParam("CHECKVALUE_LIST", checkValue);
	
	var actionFlag = $("#ACTION_FLAG").val();
	if(actionFlag == ""){
		alert("请选择失效方式！");
		return false;	
	}
	$.cssubmit.setParam("ACTION_FLAG", actionFlag);
	
	var smsFlag = $("#SMS_FLAG").attr("checked");
	if(smsFlag){
		$.cssubmit.setParam("SMS_FLAG", "1");
	}else{
		$.cssubmit.setParam("SMS_FLAG", "0");
	}
	return true;
}