$(document).ready(function(){
	//调用验证是否免人像比对和身份证可手动输入权限
	kqbkDataRight();
});
function inputPasswd1(){
	var userPasswd="";
	$("#PASSWORD").val("");
	try{
		userPasswd=document.getElementById("LittleKey").Init();
		if(userPasswd==""){
			alert("密码为空，请重新输入密码！");
			return;
		}
		if(!$.isNumeric(userPasswd)){
			alert(userPasswd);
			return;
		}
	}catch(e){
		alert("小键盘输入控件加载失败!");
		return;
	}
	$("#PASSWORD").val(userPasswd);
}

function inputPasswd2(){
	var userPasswd="";
	$("NEW_PASSWD_AGAIN").val("");
	try{
		userPasswd=document.getElementById("LittleKey").Init();
		if(userPasswd==""){
			alert("密码为空，请重新输入密码！");
			return;
		}
		if(!$.isNumeric(userPasswd)){
			alert(userPasswd);
			return;
		}
	}catch(e){
		alert("小键盘输入控件加载失败!");
		return;
	}
	$("#NEW_PASSWD_AGAIN").val(userPasswd);
}
//扫描读取身份证信息
function clickScanPspt(){
	getMsgByEFormKQBK("IDCARDNUM","CUST_NAME",null,null,null,null,null,null);
	if('' != $('#IDCARDNUM').val()){
		$('#IDCARDNUM').attr('disabled', true);
	}
	if('' != $('#CUST_NAME').val()){
		$('#CUST_NAME').attr('disabled', true);
	}
}

function identification(picid,picstream){
	var custName,psptId,psptType,fornt,desc;
 
	custName = $("#CUST_NAME").val();
	psptId = $("#IDCARDNUM").val();
	psptType = "0";//默认身份证
	fornt = $("#FRONTBASE64").val();
	desc = "请输入客户证件号码！";

/*	if( psptType == ""){
		alert("请选择证件类型！");
		return false;
	}*/
	if( psptId == ""){
		alert(desc);
		return false;
	}
	var blackTradeType="149";
	var sn = "";

	sn = $("#MOBILENUM").val();
	
	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>'+'</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+sn+'</phone>');				
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>'+'</op_time>');
	
	bossOriginalXml.push('	<busi_list>');
	bossOriginalXml.push('		<busi_info>');
	bossOriginalXml.push('			<op_code>'+'</op_code>');
	bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
	bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
	bossOriginalXml.push('		</busi_info>');
	bossOriginalXml.push('	</busi_list>');

	bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
	bossOriginalXml.push('	<id_card>'+'</id_card>');
	bossOriginalXml.push('	<cust_name>'+'</cust_name>');
	bossOriginalXml.push('	<copy_flag></copy_flag>');
	bossOriginalXml.push('	<agm_flag></agm_flag>');
	bossOriginalXml.push('</req>');
	var bossOriginaStr = bossOriginalXml.join("");
	 
	//调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	//获取保存结果
	var result = makeActiveX.IdentificationInfo.result;			
	//获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	if(picID != ''){
		alert("人像摄像成功");
	}else{
		alert("人像摄像失败");
		return false;
	}			
	//获取照片流
	var picStream = makeActiveX.IdentificationInfo.pic_stream;
	picStream = escape (encodeURIComponent(picStream));
	if("0" == result){	
		//alert("picid:"+picid);
		$("#"+picid).val(picID);	
		$("#"+picstream).val(picStream);
		var param = "&BLACK_TRADE_TYPE="+blackTradeType;
		param+="&CERT_ID="+psptId;
		param+="&CERT_NAME="+custName;
		param+="&CERT_TYPE="+psptType;
		param+="&SERIAL_NUMBER="+sn;
		param+="&FRONTBASE64="+escape (encodeURIComponent(fornt));
		param+="&PIC_STREAM="+picStream;
		
		$.beginPageLoading("正在进行人像比对。。。");		 
		$.ajax.submit(null, "cmpPicInfo", param, '',
				function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")== "0"){
						MessageBox.success("成功提示","人像比对成功", null, null, null);		
						$("#custinfo_PhoneFlag").val("1");//比对验证成功
						return true;
					}else if(data && data.get("X_RESULTCODE")== "1"){
						$("#"+picid).val("ERROR");
						$("#"+picstream).val(data.get("X_RESULTINFO"));
						$("#custinfo_PhoneFlag").val("0");//比对验证失败
						MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);		
						return false;
					}else if (data && data.get("X_RESULTCODE") == "3") {
                        MessageBox.confirm("提示", "该身份证在公安部人像库未留存头像，请现场进行人工核验！", function (btn) {
                            if (btn == 'cancel') {
                                $.cssubmit.closeMessage(true);
                            }
                        }, {'ok': "核验通过", 'cancel': '核验不通过'});
                    }
				},function(code, info, detail){
					$.endPageLoading();
					$("#"+picid).val("ERROR");
					$("#"+picstream).val("人像比对信息错误，请重新拍摄！");
					$("#custinfo_PhoneFlag").val("0");//比对验证失败
					MessageBox.error("错误提示","人像比对信息错误，请重新拍摄！",null, null, null, null);
				},function(){
					$.endPageLoading();
					$("#"+picid).val("ERROR");
					$("#"+picstream).val("人像比对失败，请重新拍摄");
					$("#custinfo_PhoneFlag").val("0");//比对验证失败
					MessageBox.alert("告警提示", "人像比对失败，请重新拍摄");
			});
	}else{
		MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
	}
}

//是否免人像比对和身份证可手动输入权限
function kqbkDataRight(){

	$.ajax.submit(null,'kqbkDataRight','','',
			function(data){ 
		var flag=data.get("TAG");
		
		if(flag=="1"){ 
			$("#custinfo_PhoneFlag").val("1");//有免人像比对和身份证可手动输入权限
			$('#IDCARDNUM').attr("readonly",false);
			$("#PIC_ID").val("111111111111111111111111");					
		}
		$.endPageLoading();
	},function(error_code,error_info){
		$.MessageBox.error(error_code,error_info);
		$.endPageLoading();
	},{
		"async" : false
	});
		
}

function checkUserNum(){
	if("1"==$("#RemoteFlag").val()){
		alert("本省号码无法办理异地业务！");
		return false;
	}
	var custName = $("#CUST_NAME").val();
	var psptId = $("#IDCARDNUM").val();
	if(!$.validate.verifyAll('psptCheckPart')) {
		return false;
	}
	if(!$.validate.verifyAll('PhoneCheckPart')) {
		return false;
	}
	var param = "&PSPT_ID="+psptId;
	if("2"==$("#FRIENDCOUNTS").val()){
		var phone4 = $("#PHONE4").val();
		var phone5 = $("#PHONE5").val();
		param=param + "&NUMBER_CHECK="+phone4+"|"+phone5;
	}
	if("3"==$("#FRIENDCOUNTS").val()){
		var phone1 = $("#PHONE1").val();
		var phone2 = $("#PHONE2").val();
		var phone3 = $("#PHONE3").val();
		param=param + "&NUMBER_CHECK="+phone1+"|"+phone2+"|"+phone3;
	}
	var PhoneFlag = $("#custinfo_PhoneFlag").val();
	if(PhoneFlag!="1"){//不是验证通过
		alert("身份证人像比对未通过，不能进行鉴权操作！");
		return false;
	}
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('psptCheckPart,QueryPart', 'openResultAuth',param , '', function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
			if(data.get("USER_STATE")!=null&&"00"!=data.get("USER_STATE")&&"02"!=data.get("USER_STATE")){
				alert("用户状态异常，不可办理！");
				$("#UserCheckFlag").val('0');
				return false;
			}
			if("1" == data.get("BUS_STATE")){
				alert("校验成功！");
				$("#IDENT_CODE").val(data.get("IDENT_CODE"));
				$("#UserCheckFlag").val('1');
				$("#BRAND_CODE").val(data.get("BRAND_CODE"));
			}else{
				alert("不可办理！"+data.get("REASON"));
				$("#IDENT_CODE").val(data.get("IDENT_CODE"));
				$("#UserCheckFlag").val('0');
			}
		}else{
			alert("校验失败！"+data.get("REASON"));
			$("#UserCheckFlag").val('0');
			$("#IDENT_CODE").val('');
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function qryCardType(){
	if(11!= $("#MOBILENUM").val().length){
		alert("手机号码必须为11位数字");
		return false;
	}
	var param = "&SERIAL_NUMBER="+$("#MOBILENUM").val();
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('QueryPart', 'qryCardType',param , 'PhoneCheckPart', function(data){
		$.endPageLoading();
		var numCount=data.get("NUM_COUNT");
//		if("0" != data.get("CARD_TYPE")){
//			alert("您的号卡不是标准实体卡，无法办理！");
//			return false;
//		}
		if("1" == data.get("result")){
			alert("本省号码无法办理异地业务！");
			$("#RemoteFlag").val('1');
			return false;
		}
		if("0" != data.get('IS_SHIMING')){
			MessageBox.alert('重要提示','未实名用户无法办理跨区密码重置业务！');
			return false;
		}
		
		if("2"==numCount){
			$("#FRIENDCOUNTS").val(numCount);
			$("#CHECKFRIENDPART").css("display","");
			$("#TWONUMBER").css("display","");
			$("#PHONE4").attr('nullable','no');
			$("#PHONE5").attr('nullable','no');
		}else if("3"==numCount){
			$("#FRIENDCOUNTS").val(numCount);
			$("#CHECKFRIENDPART").css("display","");
			$("#THREENUMBER").css("display","");
			$("#PHONE1").attr('nullable','no');
			$("#PHONE2").attr('nullable','no');
			$("#PHONE3").attr('nullable','no');
		}else{
			$("#FRIENDCOUNTS").val(numCount);
		}
		
		
		$("#RemoteFlag").val('0');
		if("0"==data.get("IS_JXH")){
			$("#BeautifulFlag").val('1');
			$("#ISJXH").val('是');
//			$.beginPageLoading("您的号码是吉祥号码，正进行金库认证..");
//			$.treasury.auth('IBS9201', $("#MOBILENUM").val(), function(ret){
//				$.endPageLoading();
//				if(true === ret){
//					alert("认证成功！");
//				}else{
//					alert("认证失败！");
//				}
//		});
		}else{
			$("#BeautifulFlag").val('0');
			$("#ISJXH").val('否');
		}
		$.cssubmit.disabledSubmitBtn(false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//清空输入框
function cleanInput(){
	$("#PASSWORD").val("");
	$("#NEW_PASSWD_AGAIN").val("");
	
}
/**
 * 提交时校验
 * @return
 */
function checkBeforeSubmit()
{  
	if("1"==$("#RemoteFlag").val()){
		alert("本省号码无法办理异地业务！");
		cleanInput();
		return false;
	}
	var newPasswd=trim($("#PASSWORD").val());
	var newPasswdAgain=trim($("#NEW_PASSWD_AGAIN").val());
	if(newPasswd==""){
		alert("新密码不能为空！");
		cleanInput();
		return false;
	}
	if(newPasswdAgain==""){
		alert("重复密码不能为空！");
		cleanInput();
		return false;
	}
	//var reg = /^\d{6}$/;
	//if (!reg.test(newPasswd)) {
	//	alert("密码必须为6位数字！");
	//	return false;
	//}
	if(newPasswd!=newPasswdAgain){
		alert("新密码与重复密码不同，请重新输入！");
		cleanInput();
		return false;
	}
	var userCheck = $("#UserCheckFlag").val();
	if("1" != userCheck){
		alert("请进行鉴权！");
		return false;
	}
    return true;
}

//业务提交
function onTradeSubmit(){
	//提交前校验
	if(!checkBeforeSubmit()) {
		return false;
	}
	$.ajax.submit('inputPWDInfoPart,psptCheckPart,QueryPart', 'onTradeSubmit', '', null, function(data){
		$.endPageLoading();
		return true;
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
//	$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
	return true;
}
