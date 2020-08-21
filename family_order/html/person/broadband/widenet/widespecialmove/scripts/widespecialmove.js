
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&AUTH_SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val(), 'CustInfoPart,WideInfoPart', function(data){
		    $("#SubmitPart").removeClass("e_dis");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


//业务提交
function onTradeSubmit(){
    var serialNumber = $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
    var mainSerialNumber = $("#MAIN_SERIAL_NUMBER").val();
    if(serialNumber == mainSerialNumber){
    	alert("移机账号不能和主号码一致！");
    	return false;
    }
    var prewideType = $("#PREWIDE_TYPE").val();
    param = param + '&MAIN_SERIAL_NUMBER='+mainSerialNumber+'&PREWIDE_TYPE='+prewideType;
	$.ajax.submit('AuthPart,chPWDInfoPart,inputPWDInfoPart', 'onTradeSubmit', param, null, function(data){
		$.endPageLoading();
		$.message.showSucTradeMessage(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//清除主账号号码
function removeNumber(){ 
	$("#MAIN_SERIAL_NUMBER").val('');
	$("#PREWIDE_TYPE").val('');
}

function qureyMphoneByMove(){                       
	var obj = $("#MAIN_SERIAL_NUMBER").val();
	var type = $("#PREWIDE_TYPE").val();
	if(type==null||type =="")
	{
	  alert("请先选择账号类型");
	  $("#MAIN_SERIAL_NUMBER").val('');
	  return false;
	}
	if(obj==null||obj =="")
	{
	  alert("请输入手机号");
	  return false;
	}
	$.ajax.submit('', 'queryMainSn',  '&SERIAL_NUMBER_A='+trim(obj)+'&PREWIDE_TYPE='+trim(type), null, function(data){
		$.endPageLoading();
		$.message.showSucTradeMessage(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function enterQureyMphoneMove()
{
	if (event.keyCode == 13)
	{
		var obj = $("#MAIN_SERIAL_NUMBER").val();
		var type = $("#PREWIDE_TYPE").val();
		if(type != "" && obj !="")
		{
			qureyMphoneByMove();
		}

    }
}