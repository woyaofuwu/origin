function refreshPartAtferAuth(data)
{
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, '',loadInfoSucc,loadInfoError);
}

function loadInfoSucc(ajaxData)
{
	$('#HAVE_NUM').val(ajaxData.get('HAVE_NUM'));
	$('#ACCEPT_NUM').val('');
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	var acceptNum = $('#ACCEPT_NUM').val();
	if(acceptNum == '' )
	{
		alert('参加活动次数必填');
		return false;
	}
	if(!$.verifylib.checkPInteger(acceptNum) || acceptNum <= 0)
	{
		alert("参加活动次数必须为自然数");
		return false;
	}
	if(confirm("确定参加活动" + acceptNum + "次吗？"))
	{
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&ACCEPT_NUM='+acceptNum;
		param += '&REMARK='+$('#REMARK').val();
		
		$.cssubmit.addParam(param);
		return true;
	}
	
	
}