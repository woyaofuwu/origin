/**
 * 云支付短信验证码支付（校验验证码成功后支付）
 * */
function checkAuthCode(){
	var checkTag=true; 
	if(!$.validate.verifyAll("CodePart")) {
		checkTag=false;
		return false;
	}
	if(checkTag){
		var authCode=$("#REDPACK_CODE").val();
		var redOrderId=$("#RED_ORDERID").val();
		var redMerId=$("#RED_MERID").val();
		var userId=$("#USER_ID").val();
		var packId=$("#PACKAGE_ID").val();
		
		 var amtVal=$("#AMT_VAL").val();
    	 var deviceModelCode=$("#DEVICE_MODEL_CODE").val();
    	 var serialNum=$("#SERIAL_NUMBER").val();
		
		$.beginPageLoading("校验验证码...");
		$.ajax.submit('', 'redPackOrderConfirm', "&REDPACK_CODE="+authCode+"&RED_ORDERID="+redOrderId+"&RED_MERID="+redMerId+"&USER_ID="+userId+"&PACKAGE_ID="+packId+"&AMT_VAL="+amtVal+"&DEVICE_MODEL_CODE="+deviceModelCode+"&SERIAL_NUMBER="+serialNum, '', function(data){
			$.endPageLoading();
			var rtnFlag=data.get("X_RESULTCODE");
			if(rtnFlag=="1"){
				$.closePopupPage(true);
			}else{
				alert("您输入的验证码不正确，请输入正确的验证码...");
				$("#REDPACK_CODE").val("");
				$("#REDPACK_CODE").focus();
			}
		},function(error_code,error_info){
			$.endPageLoading();
            alert(error_info);
        });
	}
}
/**
 * 重发验证码，要求有原来下单时候的order_id
 * */
function resendAuthCode(){ 
	$.beginPageLoading("重发验证码...");
	var redOrderId=$("#RED_ORDERID").val();
	var redMerId=$("#RED_MERID").val();
	
	var serialNum=$("#SERIAL_NUMBER").val();
	
	$.ajax.submit('', 'resendAuthCode', "&RED_ORDERID="+redOrderId+"&RED_MERID="+redMerId+"&SERIAL_NUMBER="+serialNum, '', function(data){
		$.endPageLoading();
		var rtnFlag=data.get("X_RESULTCODE");
		if(rtnFlag=="1"){
			alert("验证码重发成功，请注意查收。");
		}
	},function(error_code,error_info){
		$.endPageLoading();
        alert(error_info);
    });	 
}