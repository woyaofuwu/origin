// 查询VPMN信息
function qryVpmn(){

	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryVpmn", null, "vpmnPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 设置返回数据
function setReturnData(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	if(!$.validate.verifyAll("vpmnPart")) return false;
	
	var condSerialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	if(condSerialNumber != serialNumber){
		alert("服务号码未校验，请先校验！");
		return false;
	}
	
	// 设置返回值
	var valueData = $.DataMap();
	
	valueData.put("SERIAL_NUMBER", serialNumber);
	valueData.put("VPN_NAME", $("#VPN_NAME").val());
	valueData.put("GROUP_ADDR", $("#GROUP_ADDR").val());
	valueData.put("GROUP_CONTACT_PHONE", $("#GROUP_CONTACT_PHONE").val());
	
	//$.setReturnValue({'POP_CODING_STR': "VPMN编码：" + serialNumber}, false);
 	//$.setReturnValue({'CODING_STR': valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("VPMN编码：" + serialNumber);
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}