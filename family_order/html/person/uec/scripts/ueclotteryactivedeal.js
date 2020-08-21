function refreshPartAtferAuth(data)
{
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, '',loadInfoSucc,loadInfoError);
}

function loadInfoSucc(ajaxData)
{
	var size = ajaxData.length;
	var data = new $.DataMap();
	if(size == 1)
	{
		data = ajaxData.get(0);
	}
	else 
	{
		var sn = $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
		var rtDataStr = $.popupDialog("uec.UECLotteryChooseRecord","queryUserLotteryInfos",'&SERIAL_NUMBER='+sn,null,"500","360");
		data = new $.DataMap(rtDataStr);
	}
	setLotteryInfo(data);
}

function setLotteryInfo(data)
{
	$('#ACTIVITY_NAME').val(data.get('ACTIVITY_NAME'));
	$('#ACCEPT_DATE').val(data.get('ACCEPT_DATE'));
	$('#PRIZE_TYPE_NAME').val(data.get('PRIZE_TYPE_NAME'));
	$('#EXEC_FLAG_TRANS').val(data.get('EXEC_FLAG_TRANS'));
	$('#EXEC_TIME').val(data.get('EXEC_TIME'));
	$('#TRADE_ID').val(data.get('TRADE_ID'));
	
	var arrExecName = data.get('PRIZE_TYPE_NAME').split('|');
		
	$("#EXEC_NAME").empty();
	$("#EXEC_NAME").css('width', '100%');
	$("#EXEC_NAME").css('width', '');
	for(var i = 0, size = arrExecName.length; i < size; i++)
	{
		$("#EXEC_NAME").append("<option title=\"" + arrExecName[i] + "\"" + "value=\"" + arrExecName[i] + "\">" + arrExecName[i] + "</option>");	
	}
}

function setLotteryInfobySelect()
{
	var table = $.table.get("UecLotteryTable");
	var rowData = table.getRowData();
	$.closeDialog(rowData.toString());
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	if(!$.validate.verifyAll("TradeInfoPart")) {//先校验已配置的校验属性
		return false;
	}
	
	var execName = $('#EXEC_NAME').val();
	
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&EXEC_NAME='+execName;
	param += '&TRADE_ID='+$('#TRADE_ID').val();;
	
	$.cssubmit.addParam(param);
	return true;
	
}