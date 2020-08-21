// 查询子VPMN信息
function qrySubVpmn(){

	var serialNumber = $("#SUB_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"子VPMN编码必须填写，请填写后再进行查询！");
		return false;
	}
	$.beginPageLoading("查询中，请稍后...");
	$.ajax.submit('', 'qrySubVpmn','&SERIAL_NUMBER='+serialNumber,"subVpmnPart,parentVpmnPart", function(data){
		$("#cond_SERIAL_NUMBER").val($("#PARENT_SERIAL_NUMBER").val());
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

// 业务受理前校验
function onSubmitBaseTradeCheck(){
	
	var subSerialNumber = $("#SUB_SERIAL_NUMBER").val();
	var hiddenSubSerialNumber = $("#HIDDEN_SUB_SERIAL_NUMBER").val();
	
	if(""==subSerialNumber || undefined ==subSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"子VPMN编码必须填写，请填写后再进行提交！");
		return false;
	}
	if(""==hiddenSubSerialNumber || undefined ==hiddenSubSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"子VPMN编码未进行查询，请进行查询后再进行提交！");
		return false;
	}
	
	if(subSerialNumber != hiddenSubSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"输入子VPMN号码[" + subSerilNumber + "]与办理业务号码[" + hiddenSubSerialNumber + "]不一致,请输入原子VPMN受理号码，或重新查询!");
		return false;
	}
	
	return true;
}


