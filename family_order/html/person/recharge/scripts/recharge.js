function enable()
{
	$("#submitButton").attr("disabled",false);
}

function onTradeSubmit()
{
	if($.validate.verifyAll("card_pw_div"))
	{
		var param = 'SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		$.beginPageLoading("充值中...");
		$.ajax.submit('card_pw_div', 'onTradeSubmit', param, '', submitSuccess, submitError);
	}
}
function submitSuccess(data)
{
	$.endPageLoading();
	var retCode = data.get('RESULT_CODE');
	var retInfo = data.get('RESULT_INFO');
	if(retCode == '0000')
	{
		$.showSucMessage(retInfo);
		reset();
	}
	else
	{
		MessageBox.error('充值失败',retInfo,function(){
			reset();
		});
	}
}
function submitError(code,info)
{
	$.endPageLoading();
	MessageBox.error('充值失败',info,function(){
		reset();
	});
}

function reset()
{
	$.redirect.toPage('recharge.RechargeByCard');
}

function onTradeSubmit4E()
{
	if($.validate.verifyAll("card_pw_div"))
	{
		var param = 'SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		$.beginPageLoading("充值中...");
		$.ajax.submit('card_pw_div', 'onTradeSubmit', param, '', submitSuccess4E, submitError);
	}
}

function submitSuccess4E(data)
{
	$.endPageLoading();
	var retCode = data.get('RESULT_CODE');
	var retInfo = data.get('RESULT_INFO');
	if(retCode == '0000')
	{
		$.showSucMessage(retInfo);
	}
	else
	{
		MessageBox.error('充值失败',retInfo,function(){
		});
	}
}