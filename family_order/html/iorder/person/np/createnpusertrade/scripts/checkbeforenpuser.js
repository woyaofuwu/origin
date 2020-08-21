// 检查号码
function checkMphone() {
    var serialNumber = $("#SERIAL_NUMBER").val();
    var len = serialNumber.length;
//    var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
    
    if (len !== 11 || !/^[0-9]+$/.test(serialNumber)) {
    	checkSerialNumberFailed();
    	$("#SERIAL_NUMBER").val("");
        MessageBox.alert("输入的手机号码不对，请重新输入！");
        return;
    }
    $.custInfo.setSerialNumber(serialNumber);
    checkSerialNumberSucceed();
}

// 校验手机号码失败
function checkSerialNumberFailed() {
    $("#CHECK_SN_BTN").css("display", "");
    $("#SN_ERROR_LABEL").css("display", "");
    $("#SN_SUCCESS_LABEL").css("display", "none");
}

// 校验手机号码成功
function checkSerialNumberSucceed() {
    $("#CHECK_SN_BTN").css("display", "none");
    $("#SN_SUCCESS_LABEL").css("display", "");
    $("#SN_ERROR_LABEL").css("display", "none");
    $.custInfo.setDefaultValueAfterSerialNumberCheck();
}

//校验是否大于或者小于当前时间
function verifyDate(eventObj,compareType) {
	var authCode = $("#AUTH_CODE").val();
	if(authCode == ''){
		MessageBox.alert("提示","请先输入授权码");
		return;
	}

	var myreg=/^\d{6}$/;
	if(!myreg.test(authCode))
	{
		MessageBox.alert("提示","授权码必须为6位纯数字");
		return;
	}

	var expired = $("#AUTH_CODE_EXPIRED").val();
	var expiredTime = Date.parse(new Date(expired.replace(/-/g,"/")));
	var currentDate = Date.parse(new Date());
	var usedTime = expiredTime - currentDate;  //两个时间戳相差的毫秒数
	var minutes=Math.floor(usedTime/(60*1000));
	
	if(compareType == "less") {
        if(expiredTime <=currentDate) {
           MessageBox.alert("您选择的授权码有效期不能小于当前日期！");
            $("#AUTH_CODE_EXPIRED").val("");
        }
        else{
        	//计算相差分钟数
        	if(minutes<15){
        		MessageBox.confirm("提示信息",'授权码即将到期，后续生效可能会失败，建议协助用户重新申请新的授权码',function(btn){
 				    if(btn == "cancel"){
 				    	 $("#AUTH_CODE_EXPIRED").val("");
 				    	return false;
 				    }
 			   },{ok:"继续查验",cancel:"取消查验"});
        	}
        }
	}
}

function onTradeSubmit(){
	if (!$.validate.verifyAll()) {
        return false;
    }
	 $.beginPageLoading("业务受理中...");
		$.ajax.submit('BaseInfoPart,CustInfoPart,HiddenPart', 'onTradeSubmit','', '', function(result){
			if(result.get("RESULT_CODE") =="0"){
				MessageBox.success("成功提示","业务受理成功！");
			}else{
				MessageBox.alert("提示","受理失败");
			}
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			MessageBox.error(error_info);
	    });
}