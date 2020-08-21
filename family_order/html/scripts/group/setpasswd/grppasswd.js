/** 
 * 当输入新密码时的一次校验
 */
function checkPassword1()
{	
	var pass1 = $("#NEW_PASSWD").val();
   	var pass1Lenth = pass1.length;
   	var patrn = /^([+-]?)(\d+)$/;
   	
   	if(!$.validate.verifyField($("#NEW_PASSWD"))) 
   	{
   		$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
   		return false;
   	}
   	
   	if(pass1Lenth != 6)
   	{
    	alert("输入密码长度不等于6,请重新输入！");
    	$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
	  	return false;	  
   	}
  	var isNumber =  patrn.test(pass1);
  	if(isNumber == false)
  	{
    	alert("输入密码必须全为数字！"); 
    	$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
      	return false;	
  	}
    
    var count=0;
	var count1=0;
	var count2=0;
	
	for(var i=0; i<pass1.length-1; i++)
	{
		if(pass1.charAt(i)*1 == pass1.charAt(i+1))
		{
			count++;
		}
		else if(parseInt(pass1.charAt(i))+1==parseInt(pass1.charAt(i+1)))
		{
			count1++;	
		}
		else if(parseInt(pass1.charAt(i))-1==parseInt(pass1.charAt(i+1)))
		{
			count2++;
		}
	}

	if(count == pass1.length-1 || count1 == pass1.length-1 || count2 == pass1.length-1)
	{
		alert("新服务密码过于简单，请重新输入");
		$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();					
		return false;
	}	
}

/** 
 * 设置完密码点击确定后，进行密码校验且将值返回父页面
 */
function checkPassword()
{
	if(!$.validate.verifyAll('PasswdPart')) return false;
	
	var pass1 = $("#NEW_PASSWD").val();
	var pass2 = $("#RE_NEW_PASSWD").val();
	var passwd;	   
	if (pass1 != pass2)
	{
		alert("前后两次输入密码不一致!");
		return false;
    }
    else
    {
        $.setReturnValue(['pam_TEMP_PWD',pass2],true);
    }
}