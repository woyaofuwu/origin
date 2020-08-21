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

function qryKdPhoneNumInfo(){

	if(!$.validate.verifyField($("#cond_KD_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("kdQueryForm", "qryKdPhoneNumInfo", null, 'kdQueryForm,kdPart', 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			clearKdForm();
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
}


// 清除数据
function clearKdForm(){
	$("#cond_KD_SERIAL_NUMBER").val("");
	$("#FTTH_ACOUNT").html('');
	$("#PRODUCT_NAME").html('');
	$("#STAND_ADDRESS").html('');
	$("#CONTACT").html('');
	$("#CONTACT_PHONE").html('');
	$("#KD_USER_ID").val("");
	$("#KD_SERIAL_NUMBER").val("");
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	if(!$.validate.verifyField($("#cond_KD_SERIAL_NUMBER"))) return false;
	
	var condSerialNumber = $("#cond_SERIAL_NUMBER").val();
	var serialNumber = $("#SERIAL_NUMBER").val()
	if(condSerialNumber != serialNumber)
	{
		alert("输入号码[" + condSerialNumber + "]与办理业务号码[" + serialNumber + "]不一致, 请重新输入! ");
		return false;
	}
	
	var condKdSerialNumber = $("#cond_KD_SERIAL_NUMBER").val();
	var kdSerialNumber = $("#KD_SERIAL_NUMBER").val()
	if(condKdSerialNumber != kdSerialNumber)
	{
		alert("输入宽带账号[" + condKdSerialNumber + "]与办理业务宽带账号[" + kdSerialNumber + "]不一致, 请重新输入! ");
		return false;
	}
	
	return true;
}