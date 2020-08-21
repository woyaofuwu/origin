function checkBeforeData(data)
{
	$("#REMARK").attr("disabled",false);
	document.getElementById("op0").selected = "selected";
	setPwdTrDisplay("none");
	$("#PASSWD_TYPE").attr("disabled",false);
	$("#REMARK").val('');
	$('#USER_PASSWD1').val('');
	$('#USER_PASSWD2').val('');
	
}
//page值说明 :1=修改密码 2=新增密码 3=随机密码  4=取消密码
function showMe(page){
    
    if (page==5) //选择重置 则不能点击设置密码按钮
    {
    	setPwdTrDisplay("none");
    }else                      //选择修改和新增密码 则能点击设置密码按钮
    { 
    	setPwdTrDisplay("block");
    }
    
    
    var  oldNetPsw= $.auth.getAuthData().get('USER_INFO').get('RSRV_STR5');
    if( oldNetPsw != ""){			//说明有原密码
    	if(page == 2){    //原密码存在 不能选择新增
    		alert("用户密码已存在,请选择[修改密码]或[重置密码]！");
    		document.getElementById("op1").selected = "selected";
    		return ;
    	}
    }else{     
    	if(page == 1  || page == 4 || page == 5){   //原密码不存在 只能选择新增 2
    		alert("用户密码不存在,请选择[新增密码]！");
    		document.getElementById("op2").selected = "selected";
    		setPwdTrDisplay("block");
    		return;
    	}
    }
}
//设置密码输入框隐藏或显示
function setPwdTrDisplay(value){
	$("#user_pwd1_tr").css("display", value);
	$("#user_pwd2_tr").css("display", value);
}

function validate(){
	var npwd1 = $('#USER_PASSWD1').val();
	var npwd2 = $('#USER_PASSWD2').val();
	//判断两次输入密码是否一致
	if(npwd1 != npwd2){
		alert("两次输入密码不一致，请重新输入！");
		$('#USER_PASSWD1').val('');
		$('#USER_PASSWD2').val('');
		return false;
	}''
	//判断密码长度是否在6至16之间
	if(npwd1.length < 6 || npwd1.length > 16){
		alert("互联网密码位数至少为6，最长16位！");
		 $('#USER_PASSWD1').val('');
		 $('#USER_PASSWD2').val('');
		return false;
	}
	//校验密码：不能包含某些特殊字符
	var patrn = /[%#&\+]+/; 	
	if (patrn.test(npwd1)){
		alert("互联密码不能包含以下特殊字符：%#&+ ！");
		$('#USER_PASSWD1').val('');
		$('#USER_PASSWD2').val('');
		return false;
	}
	//判断互联网密码必须包含数字和字母
	//校验密码：只能输入6-20个字母、数字、下划线
	var patrn = /(\d)+/; 	
	if (!patrn.test(npwd1)){
		alert("互联密码必须包含数字！");
		$('#USER_PASSWD1').val('');
		$('#USER_PASSWD2').val('');
		return false;
	}
	var patrn = /([a-z|A-Z])+/; 	
	if (!patrn.test(npwd1)){
		alert("互联密码必须包含字母！");
		$('#USER_PASSWD1').val('');
		$('#USER_PASSWD2').val('');
		return false;
	}
	
	return true;
}

function checkBeforeSubmit(){
	var pwdType= $('#PASSWD_TYPE').val(); //密码设置操作;
	//修改或者新增密码的时候要比较两次密码输入是否一致
	if(pwdType == "1" || pwdType == "2"){
		return validate();
	}else if(pwdType=="0" || pwdType==""){
		alert("请选择操作类型！");
		return false;
	}
	return true;
}


