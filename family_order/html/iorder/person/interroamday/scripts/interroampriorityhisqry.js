function checkSerialNumber()
{
	var serialNumber = $("#SERIAL_NUMBER").val();
	var len = serialNumber.length;
	var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;

	if (len !== 11 || !myreg.test(serialNumber)) {
		$("#SERIAL_NUMBER").val("");
		MessageBox.alert("输入的手机号码不对，请重新输入！");
		return;
	}
	$.beginPageLoading("手机号码信息查询中...");
	$.ajax.submit('', 'checkSerialNumber', '&SERIAL_NUMBER='+serialNumber, 'PriorityInfoPart', function(data){
		var resultCode = data.get("X_RESULTCODE");
		if(resultCode != '0')
		{
			$.MessageBox.alert("错误提示", data.get("X_RESULTINFO"));
			$("#SERIAL_NUMBER").val("");
			$("#SN_SUCCESS_LABEL").css("display", "none");
			return;
		}
		$("#SN_SUCCESS_LABEL").css("display", "");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		$.MessageBox.alert("查询结果", error_info);
		$("#SN_SUCCESS_LABEL").css("display", "none");
	});
}

function interRoamPriorityHisQry()
{
	if (!verifyAll('PriorityOrderPart')){
		return false;
	}
	
	var beginTimsi = $("#BEGIN_TIMSI").val();
	var endTimsi = $("#END_TIMSI").val();
	
	var beginTime = Date.parse(new Date(beginTimsi.replace(/-/g,"/")));
	var endTime = Date.parse(new Date(endTimsi.replace(/-/g,"/")));
	
	if(beginTime >= endTime)
	{
		alert("开始时间必须小于结束时间");
		return;
	}
	
	$.beginPageLoading("查询中...");
	$.ajax.submit('PriorityOrderPart', 'interRoamPriorityHisQry', '', 'PriorityInfoPart', function(data){
		$.endPageLoading();
		var resultCode = data.get("X_RESULTCODE");
		if(resultCode != '0')
		{
			$.MessageBox.alert("错误提示", data.get("X_RESULTINFO"));
			return;
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		$.MessageBox.alert("查询结果", error_info);
	});
}