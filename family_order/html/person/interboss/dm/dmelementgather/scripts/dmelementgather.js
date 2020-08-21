//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	
	var param = "&USER_INFO="+user_info;
	
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'userInfoPart,hiddenPart', function()
	{ },
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function sendHttpRequest()
{
	
	var flag = false;
	$("#CSSUBMIT_BUTTON").attr("disabled",true);
	$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok e_dis");
	var phone = $("#AUTH_SERIAL_NUMBER").val();
	var imei = $("#comminfo_IMEI_NUM").val();
	var inmodecode = $("#comminfo_INMODECODE").val(); 
		
	//$.beginPageLoading("参数采集命令发送中...");//无效  需在 $.ajax.check 前加alert  才可以 起到罩子的作用
	if((phone!=null && phone!="")||(imei!=null && imei!="")||(inmodecode!=null && inmodecode!=""))
	{
		//校验需要同步
		var rs1 = $.ajax.check(null, 'sendHttpGather', '&PHONE='+phone+'&IMEI='+imei+'&INMODECODE='+inmodecode, null, null);
	    var operateId = rs1.rscode();
	    if(operateId && operateId.length>0)
	    {
	    	$("#comminfo_OPERATEID").val(operateId);
			alert("参数采集命令发送成功，开始记录日志!"); 
			flag = true;
	    }
	}
	else{
		alert("请输入手机号码或者IMEI号码");
	}
	//$.endPageLoading();
	return flag;
	
}


