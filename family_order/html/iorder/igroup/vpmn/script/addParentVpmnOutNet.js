function qryParentVpmn(){

	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"VPMN编码必须填写，请填写后再进行查询！");
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


// 提交前校验
function onSubmitBaseTradeCheck(){
	
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"VPMN编码查询，请填写后进行查询再提交！");
		return false;
	}
	
	var outSerialNumber = $("#OUT_SERIAL_NUMBER").val();
	if(""==outSerialNumber || undefined ==outSerialNumber){
		$.validate.alerter.one($("#OUT_SERIAL_NUMBER")[0],"外网号码未填写，请填写后再提交！");
		return false;
	}
	var serialNumber1 = $("#SERIAL_NUMBER").val();
	
	if(serialNumber1 != serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"输入号码[" + serialNumber + "]与办理业务号码[" + serialNumber1 + "]不一致,请输入原受理号码，或重新查询!");
		return false;
	}
	
	return true;
}