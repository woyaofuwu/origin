//成员号码信息查询
function queryMemberInfo(){
	$("#IF_QUERY").val("true");
	//查询条件校验
	if(!$.validate.verifyAll("QryMebInfoPart")) {
		return false;
	}
	
	//ims成员号码信息查询
	$.ajax.submit('QryMebInfoPart', 'queryMemberInfo', null, 'MebCustInfoPart,MebUserInfoPart,MebAcctInfoPart', function(data){
		$.endPageLoading();		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
//鉴权密码重置提交验证
function onSubmitBaseTradeCheck(){
	if(!$.validate.verifyAll("QryMebInfoPart")) {
		return false;
	}
	if(!$.validate.verifyAll("NewPasswordPart")) {
		return false;
	}
	var flag = $("#IF_QUERY").val();
	if(flag == "false"){
		alert("请先查询用户信息后再提交！");
		return false;
	}
	
	var userPassWord = $("#USER_PASSWD2").val();
	
	var regAll = /(?!^(\d+|[a-z]+|[A-Z]+|[\.\+\-\=\[\{|\/\*:?<"~(&^!@#$%)_`\\}\];',>]+)$)^[\w\.\+\-\=\[\{|\/\*:?<"~(&^!@#$%)_`\\}\];',>]+$/;
	var regReuslt = regAll.test(userPassWord);
	if(!regReuslt)
	{
		alert("密码必须包含大写字母、小写字母、数字、特殊字符中的任意两项!");
		return false;
	}

	return true;
}

function judgePsw(){
	if(!$.validate.verifyAll("NewPasswordPart")) {
		return false;
	}
	var password1 = $("#USER_PASSWD1").val();
	var password2 = $("#USER_PASSWD2").val();
    if(password1 != password2){
    	alert("前后密码不一致，请重新输入!");
    	$("#USER_PASSWD2").val("");
    	return false;
    }else{
    	return true;
    }
}