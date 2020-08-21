function refreshPartAtferAuth(data){
	userInfo = data.get("USER_INFO");
	$("#NOPHONE_SERIAL_NUMBER").val(data.get("USER_INFO").get("SERIAL_NUMBER"));
	 
	$("#FTTHBusiModemManage").attr("class","e_hideX");
	$("#FTTHModemManage").attr("class","");
	$.beginPageLoading("校验手机号码...");
	$.ajax.submit('', 'checkAuthSerialNum', "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'FixPhonePart', function(rtnData) { 
		$.endPageLoading();
		var resultCode=rtnData.get("RESULT_CODE");
		if(resultCode=="1"){
			$("#FIX_NUMBER").attr("disabled",false);
			$("#CHECK_BTN").attr("disabled",false); 
			alert("校验通过，请录入固话号码，点击【校验】按钮。");
			$("#FIX_NUMBER").focus();
		}else{
			var resultInfo=rtnData.get("RESULT_INFO");
			alert("手机号码校验不通过："+resultInfo);
			return;
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 

	$.endPageLoading();
}

/**
 * 控制基本信息显示\隐藏
 * @param btn
 * @param o
 */
function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}


function checkFixPhone(){
	if(!$.validate.verifyAll("FixPhonePart"))
	{
		return false;
	}
	var fixNumber=$("#FIX_NUMBER").val();
	$.beginPageLoading("校验固话号码...");
	$.ajax.submit('', 'checkFixPhoneNum', "&FIX_NUMBER="+fixNumber, '', function(rtnData) { 
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){
			if(rtnData.get("RESULT_CODE")=="1"){
				$("#CHECK_RESULT_TAG").val("1");
				$("#CHECK_RESULT").val(rtnData.get("RESULT_INFO"));
			}else{
				$("#CHECK_RESULT_TAG").val("-1");
				$("#CHECK_RESULT").val(rtnData.get("RESULT_INFO"));
			} 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 

	$.endPageLoading();
}


/**
 * 提交
 * */
function onTradeSubmit()
{
	if($("#CHECK_RESULT_TAG").val()!="1"){
		alert("固话号码校验不通过，请校验通过后再提交。");
		return false;
	}
	var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")+"&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	$.cssubmit.addParam(param);
	return true;
}