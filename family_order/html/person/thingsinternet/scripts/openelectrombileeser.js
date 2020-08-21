function refreshPartAtferAuth(data)
{
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'EditPart',loadInfoSucc,loadInfoError);
}

function loadInfoSucc()
{
	$('#OPERATION_TYPE_CODE').attr('disabled', false);
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	if(!submitCheck())
	{
		return false;
	}
	
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&OPERATION_TYPE_CODE='+$('#OPERATION_TYPE_CODE').val();
	param += '&IS_PAY_FEE='+$('#IS_PAY_FEE').val();
	param += '&DISCNT_CODE='+$('#DISCNT_CODE').val();
	param += '&SERVICE_ID='+$('#SERVICE_ID').val();
	param += '&OLD_SERIAL_NUMBER='+$('#OLD_SERIAL_NUMBER').val();
	
	$.cssubmit.addParam(param);
	return true;
}

function changeOperType()
{
	$('#SERVICE_ID').attr('disabled', true);
	$('#DISCNT_CODE').attr('disabled', true);
	$('#IS_PAY_FEE').attr('disabled', true);
	$('#OLD_SERIAL_NUMBER').attr('disabled', true);
	
	clearData();
	
	var operType = $('#OPERATION_TYPE_CODE').val();
	if(operType == '1')
	{
		if($('#SVC_NAME').text() != '')
		{
			alert('已订购'+$('#SVC_NAME').text()+'服务，不能再次订购');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
		
		$('#SERVICE_ID').attr('disabled', false);
		$('#DISCNT_CODE').attr('disabled', false);
		$('#IS_PAY_FEE').attr('disabled', false);
		$('#OLD_SERIAL_NUMBER').attr('disabled', false);
	}
	else if(operType == '2')
	{
		if($('#SVC_NAME').text() == '')
		{
			alert('没有订购过，请先订购');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
	}
	else if(operType == '3')
	{
		if($('#SVC_NAME').text() == '')
		{
			alert('没有订购过，请先订购');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
		if($('#SVC_STATE').val() != 'A')
		{
			alert('用户服务状态不为正常状态，不能暂停');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
	}
	else if(operType == '4')
	{
		if($('#SVC_NAME').text() == '')
		{
			alert('没有订购过，请先订购');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
		if($('#SVC_STATE').val() != 'N')
		{
			alert('用户服务状态不为暂停状态，不能恢复');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
		$('#DISCNT_CODE').attr('disabled', false);
	}
	else if(operType == '5')
	{
		if($('#SVC_NAME').text() == '')
		{
			alert('没有订购过，请先订购');
			$('#OPERATION_TYPE_CODE').val('');
			return;
		}
		$('#IS_PAY_FEE').attr('disabled', false);
		$('#OLD_SERIAL_NUMBER').attr('disabled', false);
	}
}

function submitCheck()
{
	var operType = $('#OPERATION_TYPE_CODE').val();
	if(operType == '')
	{
		alert('操作类型必填');
		return false;
	}
	if(operType == '1')
	{		
		if($('#SERVICE_ID').val() == '')
		{
			alert('服务名称必填');
			return false;
		}
		if($('#DISCNT_CODE').val() == '')
		{
			alert('优惠名称必填');
			return false;
		}
		if($('#IS_PAY_FEE').val() == '')
		{
			alert('是否主号付费必填');
			return false;
		}
		if($('#OLD_SERIAL_NUMBER').val() == '')
		{
			alert('主号码必填');
			return false;
		}
		if($('#AUTH_FLAG').val() == '')
		{
			alert('主号码需验证');
			return false;
		}
	}
	else if(operType == '2')
	{
		
	}
	else if(operType == '3')
	{
		
	}
	else if(operType == '4')
	{
		if($('#DISCNT_CODE').val() == '')
		{
			alert('优惠名称必填');
			return false;
		}
	}
	else if(operType == '5')
	{
		if($('#IS_PAY_FEE').val() == '')
		{
			alert('是否主号付费必填');
			return false;
		}
		if($('#OLD_SERIAL_NUMBER').val() == '')
		{
			alert('主号码');
			return false;
		}
		if($('#AUTH_FLAG').val() == '')
		{
			alert('主号码需验证');
			return false;
		}
	}
	
	return true;
}

function clearData()
{
	$('#SERVICE_ID').val('');
	$('#DISCNT_CODE').val('');
	$('#IS_PAY_FEE').val('');
	$('#OLD_SERIAL_NUMBER').val('');
	$('#AUTH_FLAG').val('');
	$('#POP_OLD_SERIAL_NUMBER').attr('isAuth', '');
}

function changeIsPayFee()
{
	$('#POP_OLD_SERIAL_NUMBER').attr('isAuth', '');
	var isPayFee = $('#IS_PAY_FEE').val();
	if(isPayFee == '1')
	{
		$('#POP_OLD_SERIAL_NUMBER').attr('isAuth', 'true');
		$('#AUTH_FLAG').val('');
	}
}

function changeMainSn()
{
	$('#AUTH_FLAG').val('');
}

function changeAuthFlag()
{
	$('#AUTH_FLAG').val('true');
}

function exceptAction(state, data){
	if(state=="USER_KEY_NULL"){
		alert("该用户没有设置密码！");
		changeAuthFlag();
		return false;
	}
	return true;
}