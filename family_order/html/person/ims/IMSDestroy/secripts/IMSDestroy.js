

//选择拆机方式后触发
function changedestorytype()
{
	var fangshi = $("#DESTORYTYPE option:selected").val();
	if (fangshi=="1")
	{
		//立即拆机
		$("#TRADE_TYPE_CODE").val("6805");
		$("#li_destorytime").css("display","none");
		$("#div_predestory").css("display","none");
	}else if (fangshi=="2")
	{
		//预约拆机
		$("#TRADE_TYPE_CODE").val("6807");
		//显示日期选择框
		$("#li_destorytime").css("display","block");
		$("#div_predestory").css("display","block");
	}else
	{
		MessageBox.alert("告警提示","请选择拆机方式！");
	}
}

/**
校验预约拆机时间
*/
function checkDestroyTime(obj)
{
	var destroyTime = obj.value;
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	
	if(destroyTime == null || destroyTime == '')
	{
		MessageBox.alert("提示","请选择预约拆机时间!");
		return false ;
	}
	
	var param = "&DESTROY_TIME=" + destroyTime + "&SERIAL_NUMBER=" + serialNumber + "&TRADE_TYPE_CODE=1605" ;
	$.ajax.submit(null,"checkDestroyTime",param,'',
			function(data){
			$.endPageLoading();
			var resultCode = data.get('X_RESULTCODE');
			$("#CHECK_DESTROY_TIME").val(resultCode);
			if(resultCode != '0')
			{
				var resultInfo = data.get('X_RESULTINFO');
				MessageBox.alert("提示","校验失败:" + data.get('X_RESULTINFO'));
			}
		},
		function(error_code, error_info) 
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
			$("#CHECK_DESTROY_TIME").val("-1");
		}
	);
}


/** 用戶认证结束之后执行的js方法 */
function refreshPartAtferAuth(data){
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'IMSInfoPart',
		function(data) {
			$.endPageLoading();
			$.cssubmit.disabledSubmitBtn(false,"submitButton");
		}, 
		function(error_code, error_info) 
		{
			$.endPageLoading();
			$.cssubmit.disabledSubmitBtn(true,"submitButton");
			$.MessageBox.error(error_code, error_info);
		}
	);
}

function submitBeforeCheck(data){

	if(!$.validate.verifyAll("AuthPart") || !$.validate.verifyAll("IMSInfoPart")) {
		return false;
	}
	
    var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()
    			+"&IMS_NUMBER="+$("#IMS_NUMBER").val()
    			+"&REMOVE_REASON="+$("#REMARK").val();
    $.cssubmit.addParam(param);
    
	return true;
}

