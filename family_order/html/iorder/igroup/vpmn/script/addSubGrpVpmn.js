// 查询母VPMN信息
function qryParentVpmn(){

	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"母VPMN编码必须填写，请填写后再进行查询！");
		return false;
	}
	$.beginPageLoading("查询中，请稍后...");
	$.ajax.submit('', 'qryParentVpmn','&SERIAL_NUMBER='+serialNumber,"parentVpmnPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

// 查询子VPMN信息
function qrySubVpmn(){
	
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var subSerialNumber = $("#SUB_SERIAL_NUMBER").val();
	var parentSerialNumber = $("#PARENT_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"母VPMN编码必须填写，请填写后再进行查询！");
		return false;
	}
	if(""==parentSerialNumber || undefined ==parentSerialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"母VPMN编码未进行查询，请进行查询后在进行子VPMN编码查询！");
		return false;
	}
	if(""==subSerialNumber || undefined ==subSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"子VPMN编码必须填写，请填写后再进行查询！");
		return false;
	}
	if(serialNumber==subSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"子VPMN编码与母VPMN编码相同, 请重新输入！");
		return false;
	}
	
	$.beginPageLoading("查询中，请稍后...");
	$.ajax.submit('', 'qrySubVpmn','&SERIAL_NUMBER=' + serialNumber + '&SUB_SERIAL_NUMBER=' + subSerialNumber,"subVpmnPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

// 业务受理前校验
function onSubmitBaseTradeCheck(){
	
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var subSerialNumber = $("#SUB_SERIAL_NUMBER").val();
	var parentSerialNumber = $("#PARENT_SERIAL_NUMBER").val();
	var hiddenSubSerialNumber = $("#HIDDEN_SUB_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"母VPMN编码必须填写，请填写后再进行提交！");
		return false;
	}
	if(""==parentSerialNumber || undefined ==parentSerialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"母VPMN编码未进行查询，请进行查询后再进行提交！");
		return false;
	}
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
	
	if(parentSerialNumber != serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"输入母VPMN号码[" + serialNumber + "]与办理业务号码[" + parentSerialNumber + "]不一致,请输入原母VPNM受理号码，或重新查询!");
		return false;
	}
	
	if(parentSerialNumber == hiddenSubSerialNumber){
		$.validate.alerter.one($("#SUB_SERIAL_NUMBER")[0],"输入子VPMN受理号码[" + hiddenSubSerialNumber + "]与母VPMN办理号码[" + parentSerialNumber + "]相同,请输入重新输入或重新查询!");
		return false;
	}
	return true;
}



