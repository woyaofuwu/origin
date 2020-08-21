function refreshPartAtferAuth(data)
{
	var params = "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()
				+"&ACCT_INFO="+data.get("ACCT_INFO").toString()+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
				+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	$.beginPageLoading("宽带资料查询中......");
	$.ajax.submit(this, 'qryBroadBandUser', params, 'UserInfoArea,chPWDInfoPart', 
	function(dataset)
	{
		//$("#pwdInfo_PASSWD_TYPE").attr("disabled",null);
		//initModifyUserPswInfo();
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

//显示修改密码弹出框
	function pwdModify(){
		$("#pwdModify").css("display","");
		if($("#pwdInfo_PASSWD_TYPE").val()=="1"){
			$("#OLDLI").css("display","");
		}else if($("#pwdInfo_PASSWD_TYPE").val()=="2"){
			$("#OLDLI").css("display","none");
		}else if($("#pwdInfo_PASSWD_TYPE").val()=="5"){
			$("#OLDLI").css("display","none");
		}
	}
	
	
	
/*设置用户密码*/
function setUserPasswd(newPasswd,newPasswdAgain){
	
	//查询条件校验
	if(!$.validate.verifyAll("inputPWDInfoPart")) {
		cleanInput("0");
		return false;
	}
	
	if($('#'+newPasswd).val()==""){
		alert("新密码不能为空！");
		cleanInput("2");
		return false;
	}
	if($('#'+newPasswdAgain).val()==""){
		alert("重复密码不能为空！");
		cleanInput("2");
		return false;
	}
	if($('#'+newPasswd).val().length!=6){
		alert("密码长度不够6位，请重新输入！");
		cleanInput("2");
		return false;
	}
	if($('#'+newPasswd).val()=='123456'||$('#'+newPasswd).val()=='654321'){
		alert("密码过于简单，请重新输入！");
		cleanInput("2");
		return false;
	}
	var flag = true;
	var passwd=$('#'+newPasswd).val().charAt(0);
	for(var i=0; i<$('#'+newPasswd).val().length; i++) {
		if(passwd!=$('#'+newPasswd).val().charAt(i)) {
				flag=false;
			}
	}
	if (flag) {
		alert('新密码过于简单，请重新输入！');
		cleanInput("2");
		return false;
	}	
	if($('#'+newPasswd).val()!=$('#'+newPasswdAgain).val()){
		alert("新密码与重复密码不同，请重新输入！");
		cleanInput("2");
		return false;
	}
	try{
		var psptTypeCode  = $('#PSPT_TYPE_CODE').val();
		var psptCode  = $('#PSPT_ID').val();
		var serialNumber = $('#SERIAL_NUMBER').val();
		var info = isSimplePwd(serialNumber,psptTypeCode,psptCode,$('#'+newPasswd).val());
		
		if ( info ){
		
			var alertInfo = '尊敬的客户为了保护您的个人信息安全，建议不要将规律数字、生日、证件号码或电话号码等作为服务密码，请重新输入！';
			cleanInput("2");
			alert(alertInfo);
			
			return false;
		}			
	}catch(err){
		var temperr = err;
	}
	$("#pwdInfo_NEW_PASSWD").val($('#'+newPasswd).val());
	
	
	if($("#pwdInfo_PASSWD_TYPE").val()=="1"){
	
		var param = "";
		var passwdType = $("#pwdInfo_PASSWD_TYPE").val();
		var userPasswd = $("#USER_PWD_HIDDEN").val()
		param = "&PASSWD_TYPE="+passwdType+"&USER_PASSWORD="+userPasswd+"&USER_ID="+$("#USER_ID").val();
		//进行ajax密码校验
		$.ajax.submit('inputPWDInfoPart,AuthPart', 'checkPwd', param, '', function(data){
			//校验失败，清空输入框
			if(data.get(0).get("X_RESULT_CODE")=="0"){
				$.showSucMessage("校验成功。","","")
				$("#SubmitPart").removeClass("e_dis");
				$("#CSSUBMIT_BUTTON").attr("disabled",null);
				hideLayer('pwdModify');
			}else{
				alert(data.get(0).get("X_RESULT_INFO"));
				cleanInput("0");
				
			}
			
			$.endPageLoading();
		},
			
		
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}else{
		hideLayer('pwdModify');
		$("#SubmitPart").removeClass("e_dis");
		$("#CSSUBMIT_BUTTON").attr("disabled",null);
	}
}
//清空输入框
function cleanInput(type){
	$("#SubmitPart").addClass("e_dis");
	$("#CSSUBMIT_BUTTON").attr("disabled","true");
	if(type=="0"){
		$("#OLD_PASSWD").val("");
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
	}else if(type=="1"){
		$("#OLD_PASSWD").val("");
	}else if(type=="2"){
		$("#NEW_PASSWD").val("");
		$("#NEW_PASSWD_AGAIN").val("");
	}
	
}
//校验是否为弱密码
function isSimplePwd(serialNumber,psptTypeCode,psptCode,password)
{
	var isSimple = false;
	if (serialNumber != null && serialNumber.indexOf(password) > -1)
	{
		isSimple = true;
	}

	if (psptTypeCode == '0' && psptCode != null && (psptCode.indexOf(password) > -1))
	{
		isSimple = true;
	}

	if (password != null && checkRepeatNumber(password))
	{
		isSimple = true;
	}

	return isSimple;
}
function checkRepeatNumber(password) {

	var temp = password.split('');

	var b = '';
	
	for ( var i = 0; i < temp.length; i++){
		
		var t = temp[i] + '';
		
		if (b.indexOf(t) < 0){
			b = b + '' + t
		}
	}

	if ( b.length < 3 ){
		return true;
	}
	
	
	if (temp[0] == temp[1] && temp[2] == temp[3] && temp[4] == temp[5]){
		return true;
	}

	var a1 = parseInt(temp[1]+"") - parseInt(temp[0]+"") ;
	var a2 = parseInt(temp[2]+"") - parseInt(temp[1]+"") ;
	
	var a3 = parseInt(temp[4]+"") - parseInt(temp[3]+"") ;
	var a4 = parseInt(temp[5]+"") - parseInt(temp[4]+"") ;

	if (a1 == a2 && a3 == a4){
		return true;
	}
	
	return false;
}

function initModifyUserPswInfo(){
	var pswHidden = $("#USER_PWD_HIDDEN");
	
	 $("#pwdInfo_PASSWD_TYPE").attr("disabled",null);
	if( pswHidden.val() != ""){	
		showMe("1");
		document.getElementById("op1").selected="selected";
	}else{
		showMe("2");
		document.getElementById("op2").selected="selected";
	}
}	

  //page值说明 :1=修改密码 2=新增密码 3=随机密码  4=取消密码 (5=重置密码 HXYD-YZ-REQ-20100917-033 增加)
	function showMe(page){
	    cleanInput("0");
	    if (page==3 || page==4 ) //选择取消和随机密码 则不能点击设置密码按钮
	    {
	     $("#setPWDIV").addClass("e_dis");
	     $("#setPWD").attr("disabled","true");

	     $("#SubmitPart").removeClass("e_dis");
	     $("#CSSUBMIT_BUTTON").attr("disabled",null);
	    }else                      //选择修改和新增密码 则能点击设置密码按钮
	    { 
	     $("#setPWDIV").removeClass("e_dis");
	      $("#setPWD").attr("disabled",null);
	    }
	    var pswHidden = $("#USER_PWD_HIDDEN");
	    	    
	    if( pswHidden.val() != ""){			//说明有原密码
	    	
	    	if(page == 2){    //原密码存在 不能选择新增
	    		alert("用户密码已存在,请选择修改密码或取消密码！");
	    		document.getElementById("op1").selected="selected";
	    		return ;
	    	}
	    	if (page == 1) { //修改密码
	    		
	    	}
	    	if (page == 5){ //重置密码
	    		
	   		}
	    }
	    else{     
	    	if(page == 1  || page == 4 || page == 5){   //原密码不存在 只能选择新增 2和随机 3  // modify by songzy@20110121
	    		alert("用户密码不存在,请选择新增密码或随机密码!");
	    		document.getElementById("op2").selected="selected";
				$("#setPWDIV").removeClass("e_dis");
				$("#setPWD").attr("disabled",null);
	    		return;
	    	}
	    }

	};
	//显示修改密码弹出框
	function pwdModify(){
		$("#pwdModify").css("display","");
		if($("#pwdInfo_PASSWD_TYPE").val()=="1"){
			$("#OLDLI").css("display","");
		}else if($("#pwdInfo_PASSWD_TYPE").val()=="2"){
			$("#OLDLI").css("display","none");
		}else if($("#pwdInfo_PASSWD_TYPE").val()=="5"){
			$("#OLDLI").css("display","none");
		}
	}
	
	function showLayer(optionID) {
		$('#'+optionID).css("display","block");
//	document.getElementById(optionID).style.display = "block";
	}
	//隐藏修改密码框
	function hideLayer(optionID) {
		$('#'+optionID).css("display","none");
//		document.getElementById(optionID).style.display = "none";
	}
	
function checkBeforeSubmit()
{  
	
	var param = "&PASSWD_TYPE="+$("#pwdInfo_PASSWD_TYPE").val();
	$.cssubmit.addParam(param) 
	return true;	
}