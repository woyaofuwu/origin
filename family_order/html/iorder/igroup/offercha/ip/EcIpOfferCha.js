/*function validateParamPage(methodName) {
	if(methodName == 'CrtUs')
	{
		var pwd = $("#pam_PASSWORD").val();
		var repwd = $("#pam_REPASSWORD").val();
		if(pwd != null){
			if((pwd.length != 6 ) && (pwd.length != 0)) {
				alert("密码必须为六位数字!");
				return false;
			}
			if(pwd != repwd){
				alert("两次密码输入错误!");
				return false;
			}
			if(getSequenceCount(pwd) > 3 || checkPwd(pwd)){
				alert("您输入的密码过于简单!");
				return false;
			}
		}
	}
	return true;
}*/

function getSequenceCount(str)
{
	var iLen = str.length;
	if (iLen < 1) return 0;
	if (iLen == 1) return 1;
	var iResult = 1;
	var iCount = 0;
	var n2 = 0;
	var n1 = 0;

	n1 = parseInt(str.substring(0,1));
	iCount = 1;
	for (var i = 1; i < iLen ; i++)
	{
		n2 = parseInt(str.substring(i,i+1));
		if (n1 + 1 == n2)
		{
			iCount++;
			if ( iCount > iResult)
			{
				iResult = iCount;
			}
		}
		else
		{
			iCount = 1;
		}
		n1 = n2;
	}
	n1 = parseInt(str.substring(0,1));
	iCount = 1;
	for (var i = 1; i < iLen ; i++)
	{
		n2 = parseInt(str.substring(i,i+1));
		if (n1 == n2 +1)
		{	
			iCount++;
			if ( iCount > iResult)
			{
				iResult = iCount;
			}
		}
		else
		{
			iCount = 1;
		}
		n1 = n2;
	}
	return iResult;
}

function checkPwd(str)
{
	var iCount = 0;
	for(var i = 1;i <= 6; i++)
	{
		if(str.substring(i,i+1) == str.substring(i-1,i)) 
		iCount++;
	}
	if(iCount == 5) return true;
	return false;
}

//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	
	var pwd = $("#pam_PASSWORD").val();
	var repwd = $("#pam_REPASSWORD").val();
	if(pwd != null){
		if((pwd.length != 6 ) && (pwd.length != 0)) {
			$.validate.alerter.one($("#pam_PASSWORD")[0], "密码必须为六位数字!\n");
			return false;
		}
		if(pwd != repwd){
			$.validate.alerter.one($("#pam_REPASSWORD")[0], "两次密码输入错误!\n");
			return false;
		}
		if(getSequenceCount(pwd) > 3 || checkPwd(pwd)){
			$.validate.alerter.one($("#pam_PASSWORD")[0], "您输入的密码过于简单!\n");
			return false;
		}
	}

	backPopup(obj);
}