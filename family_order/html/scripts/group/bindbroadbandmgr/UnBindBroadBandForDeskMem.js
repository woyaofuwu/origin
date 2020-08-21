function qryImsPhoneNumInfo(){

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryImsPhoneNumInfo", null, 'imsPart', 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearImsForm();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


// 清除数据
function clearImsForm(){
	$("#IMS_CUST_ID").html('');
	$("#IMS_USER_ID").html('');
	$("#IMS_CUST_NAME").html('');
	$("#IMS_EPARCHY_NAME").html('');
	$("#IMS_PSPT_TYPE_NAME").html('');
	$("#IMS_PSPT_ID").html('');
	$("#USER_ID").val("");
	$("#SERIAL_NUMBER").val("");
	$("#cond_SERIAL_NUMBER").val("");
	$("#KD_USER_ID").html('');
	$("#FTTH_ACOUNT").html('');
	$("#KD_SERIAL_NUMBER").val("");
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	var ftthAccount = $("#KD_SERIAL_NUMBER").val();
	if(ftthAccount == null || ftthAccount == "")
	{
		alert("该IMS固话号码无绑定的宽带!请确认清楚后再操作!或者回车查询后再操作!");
		return false;
	}
	
	var condSerialNumber = $("#cond_SERIAL_NUMBER").val();
	var serialNumber = $("#SERIAL_NUMBER").val()
	if(condSerialNumber != serialNumber)
	{
		alert("输入号码[" + condSerialNumber + "]与办理业务号码[" + serialNumber + "]不一致, 请重新输入! ");
		return false;
	}
	
	return true;
}