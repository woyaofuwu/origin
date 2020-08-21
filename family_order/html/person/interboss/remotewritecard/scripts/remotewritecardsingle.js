$(document).ready(function(){

	$('#submit').attr("disabled","true");
	if($("#ROUTETYPE").val()=="01") 
	{	
		$('#MOBILENUM').attr("disabled","");
		$('#ID_ITEM_RANGE').attr("disabled","");
		$('#IDCARDNUM').attr("disabled","");
		if($("IDTYPE").value=="01" ) {
			$("#ID_ITEM_RANGE").val($('#MOBILENUM').val());
		}
	}
	//调用验证是否免人像比对和身份证可手动输入权限
	kqbkDataRight();
});

/*************************************公用方法 开始************************************/



/*去掉空格*/
function trim(str) {
	return str.replace(/^\s+|\s+$/,'');
}

/*检查非空*/
function isNull(str) {
	if(str==undefined || str==null || str=="") {
		return true;
	}
	return false;
}


/*************************************公用方法 结束************************************/

function checkUserNum(){
	var param = "&MOBILENUM=" + $("#MOBILENUM").val() + "&PSPT_ID=" + $("#IDCARDNUM").val() +"&CUST_NAME=" + $("#CUST_NAME").val()+"&USER_PASSWD=" + $("#USER_PASSWD").val();
	
		if("1"!=$('#queryCardTag').val()){
			alert("请先进行卡查询");
			return false;
		}
		if(!$.validate.verifyAll('CustCondPart')) {
			return false;
		}
		if("2"==$("#FRIENDCOUNTS").val()){
			var phone4 = $("#PHONE4").val();
			var phone5 = $("#PHONE5").val();
			var friend2RequiredCheck = "" == phone4 || "" == phone5;
			if(friend2RequiredCheck){
				alert("请输入好友号码");
				return false;
			}
			param = param+"&NUMBER_CHECK="+phone4+"|"+phone5;
		}
		if("3"==$("#FRIENDCOUNTS").val()){
			var phone1 = $("#PHONE1").val();
			var phone2 = $("#PHONE2").val();
			var phone3 = $("#PHONE3").val();
			var friend3RequiredCheck = "" == phone1 || "" == phone2 || "" == phone3;
			if(friend3RequiredCheck){
				alert("请输入好友号码");
				return false;
			}
			param = param+"&NUMBER_CHECK="+phone1+"|"+phone2+"|"+phone3;
		}
	
	if("2"==$('#RECARD_TYPE_K').val()){
		var simNo=$("#SIM_CARD_NO").val();//sim卡
		var messageCheck=$("#AUTH_NUMBER_K").val();//验证码
		if(""==simNo||simNo==null){
			alert("请先读卡再鉴权！");
			return false;
		}
		if(""==trim(messageCheck)){
			alert("请输入验证码！");
			return false;
		}
		param=param+"&MESSAGE_CHECK="+messageCheck+"&ICC_ID="+simNo+"&CHANGE_CARD_TAG="+$('#RECARD_TYPE_K').val();
	}
	kqbkDataRight();
	var PhoneFlag = $("#custinfo_PhoneFlag").val();
	if(PhoneFlag!="1"){//不是验证通过
		alert("身份证人像比对未通过，不能进行鉴权操作！");
		return false;
	}
	$.beginPageLoading("鉴权中...");
	$.ajax.submit('', 'openResultAuth',param , '', function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
			/*if(data.get("USER_STATE")!=null&&"00"!=data.get("USER_STATE")){
				alert("用户状态异常，不可办理！");
				return false;
			}*/
			if("1" == data.get("BUS_STATE")){
				alert("校验成功！");
				$("#IDENT_CODE").val(data.get("IDENT_CODE"));
				$("#custinfo_RemoteVerifyFlag").val('1');//鉴权标记
				if("2"==$("#FRIENDCOUNTS").val()||"3"==$("#FRIENDCOUNTS").val()){
					$("#PHONECHECK").val('1');
				}
			}else{
				$("#custinfo_RemoteVerifyFlag").val('0');//鉴权标记
				alert("不可办理！"+data.get("REASON"));
				return false;
			}
			$("#AUTH_CUST_INFO").val(data.toString());
		}else{
			$("#custinfo_RemoteVerifyFlag").val('0');//鉴权标记
			alert("校验失败！"+data.get("REASON"));
			return false;
		}
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/**
 * 卡查询
 * @returns {Boolean}
 */
function qryCardType(){
	if(!$.validate.verifyAll('AddrCondPart')) {
		return false;
	}
	var carTag=$("#RECARD_TYPE_K").val();
	if(carTag==null||""==carTag){
		alert("请选择补卡/换卡");
		return false;
	}
	if(11!= $("#MOBILENUM").val().length){
		alert("手机号码必须为11位数字");
		return false;
	}
	var param = "&SERIAL_NUMBER="+$("#MOBILENUM").val()+"&CHANGE_CARD_TAG="+carTag;
	//window.location.reload();
	$.beginPageLoading("查询中...");
	$.ajax.submit('AddrCondPart', 'qryCardType',param , 'AddrCondPart,simcardInfoPart,CustCondPart', function(data){
		$.endPageLoading();
		$("#RECARD_TYPE_K").val(carTag);
		var numCount=data.get("NUM_COUNT");
		/*var isReadonly=data.get("READONLY");
		if("1"==isReadonly){
			$("#SIM_CARD_NO").attr('readonly',false);
			$("#IMSI").attr('readonly',false);
		}*/
//		if("0" != data.get("CARD_TYPE")){
//			alert("您的号卡不是标准实体卡，无法办理！");
//			return false;
//		}
		if("1" == data.get("result")){
			alert("本省号码无法办理异地业务！");
			return false;
		}
		if("0" != data.get('IS_SHIMING')){
			MessageBox.alert('重要提示','未实名用户无法办理跨区补卡业务！');
			return false;
		}
		if("2" == data.get('CARD_RETN')){
			var cardType = data.get("CARD_NAME");
			MessageBox.alert('重要提示','该号码原卡类型为'+cardType+'，1、原NFC卡内公交卡应用、银行卡应用（电子现金）的余额无法使用”；2、如为一号终端业务，补卡后，仅支持一个终端好用（可以放卡的终端）；3、如为一卡双号卡，补卡/换卡后，仅一个号码可以正常使用。相应的提示内容体现到电子免填单中。');
		}else if("1" == data.get('CARD_RETN')){
			var cardType = data.get("CARD_NAME");
			MessageBox.alert('重要提示','该号码原卡类型为'+cardType+'，1、原NFC卡内公交卡应用、银行卡应用（电子现金）的余额无法使用”；2、如为一号终端业务，补卡后，仅支持一个终端好用（可以放卡的终端）；3、如为一卡双号卡，补卡/换卡后，仅一个号码可以正常使用。相应的提示内容体现到电子免填单中。');
		}
		if('0'==data.get('IS_JXH')){
			$('#ISJXH').val('是');
			if("2"==numCount){
				$('#BeautifulFlag').val('1');
				$("#PHONECHECK").val('0');
				$("#FRIENDCOUNTS").val(numCount);
				$("#NUMBERCOUNT2").val(numCount);
				$("#PhoneCheckTitle").css("display","");
				$("#TWONUMBER").css("display","");
			}else if("3"==numCount){
				$('#BeautifulFlag').val('1');
				$("#PHONECHECK").val('0');
				$("#FRIENDCOUNTS").val(numCount);
				$("#NUMBERCOUNT3").val(numCount);
				$("#PhoneCheckTitle").css("display","");
				$("#THREENUMBER").css("display","");
			}else{
				$("#FRIENDCOUNTS").val(numCount);
				$("#PHONECHECK").val('1');
				$('#BeautifulFlag').val('0');
			}
		}else{
			$("#FRIENDCOUNTS").val('0');
			$("#PHONECHECK").val('1');
			$('#BeautifulFlag').val('0');
			$('#ISJXH').val('否');
		}
		$('#tips_info').css('display','none');
		if("2"==carTag){
			$('#auth_number1').css('display','');
			$('#recard_auth_button').css('display','');
			$('#tips_info').css('display','');
			$("#custinfo_PhoneFlag").val("1");//免人像比对和证件扫描
			$('#authQueryButton').css('display','none');
			$('#checkMode1').css('display','none');
			$('#USER_PASSWD').attr('nullable','yes');
			$("#SIM_CARD_NO").attr('readonly',false);
		}
		$('#queryCardTag').val('1');
		$('#IDCARDNUM').attr('readonly', false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function queryCustInfo(){
	
	if(!$.validate.verifyAll('AddrCondPart')) {
		return false;
	}
	var carTag=$("#RECARD_TYPE_K").val();
	if(carTag==null||""==carTag){
		alert("请选择补卡/换卡");
		return false;
	}
	if(!$.validate.verifyAll('CustCondPart')) {
		return false;
	}
	
	if("01" == $("#ROUTETYPE").val()) {
		var mobileNum = $("#MOBILENUM").val();
		if(11!= mobileNum.length){
			alert("手机号码必须为11位数字");
			return false;
		}
	}
	
	if("" == trim($("#IDCARDTYPE").val()) || "" == trim($("#USER_PASSWD").val())) {
		alert("请输入验证证件类型和服务密码");
		return false;
	}
	if("" == trim($("#IDCARDNUM").val()) || "" == trim($("#USER_PASSWD").val()) ) {
		alert("请输入验证证件信息和服务密码");
		return false;
	}
	
	
	
	kqbkDataRight();
	var PhoneFlag = $("#custinfo_PhoneFlag").val();
	if(PhoneFlag!="1"){//不是验证通过
		alert("身份证人像比对未通过，不能进行查询操作！");
		return false;
	}
	
	$("#custinfo_CheckPhone").val($("#MOBILENUM").val());
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AddrCondPart,CustCondPart', 'queryCustInfo', '&ID_VALUE='+$("#MOBILENUM").val(), 'AddrCondPart,custInfoPart,PhoneCheckPart', function(data){
		$("#RECARD_TYPE_K").val(carTag);
		if("0" != data.get('IS_SHIMING')){
			$.endPageLoading();
			$("#custinfo_RemoteVerifyFlag").val('0');
			MessageBox.alert('重要提示','未实名用户无法办理跨区补卡业务！');
			return false;
		}
		if("2" == data.get('CARD_RETN')){
			$.endPageLoading();
			$("#custinfo_RemoteVerifyFlag").val('0');
			var cardType = data.get("CARD_NAME");
			MessageBox.alert('重要提示','该号码原卡类型为'+cardType+'，跨区补卡仅提供补换标准实体卡。如需补换原卡类型，请客户自行联系号码归属省处理！');
			return false;
		}else if("1" == data.get('CARD_RETN')){
			$.endPageLoading();
			var cardType = data.get("CARD_NAME");
			MessageBox.alert('重要提示','该号码原卡类型为'+cardType+'，跨区补卡仅提供补换标准实体卡。如需补换原卡类型，请客户自行联系号码归属省处理！');
		}
		if('0'==data.get('IS_JXH')){
			$('#ISJXH').val('是');
			$('#BeautifulFlag').val('1');
			if("2"!=carTag){
				$("#PHONECHECK").val('0');
				$('#PhoneCheckPart').css('display','');
				$('#PhoneCheckTitle').css('display','');
			}else{
				//carTag==2时为换卡，换卡不需要好友验证
				$("#PHONECHECK").val('1');
				$('#PhoneCheckPart').css('display','none');
				$('#PhoneCheckTitle').css('display','none');
			}
			
		}else{
			$("#PHONECHECK").val('1');
			$('#BeautifulFlag').val('0');
			$('#ISJXH').val('否');
			$('#PhoneCheckPart').css('display','none');
			$('#PhoneCheckTitle').css('display','none');
		}
		$("#AUTH_CUST_INFO").val(data.toString());
		//$("#cond_SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
		//$.feeMgr.setPosParam("141", $("#MOBILENUM").val(), "");
		$.endPageLoading();
		if(data.get("USER_STATE_CODESET")=="00"){
			$("#custinfo_RemoteVerifyFlag").val('1');					
		}else{
			$("#custinfo_RemoteVerifyFlag").val('0');
			alert("非正常在网用户,不能办理此业务！");
			return false;				
		}
		
		if("2"==carTag){
			$('#auth_number1').css('display','');
			$('#recard_auth_button').css('display','');
			$('#tips_info').css('display','');
		}
		/*if(data.get("IDCARDNUM")==trim($("#IDCARDNUM").val())){ 
			
		}else{
			$("#custinfo_RemoteVerifyFlag").val('0');
			alert("扫描证件号码和归属省证件号码不一致，不能办理此业务！");
			return false;
		}*/
	},
	function(error_code,error_info)
	{
		$('#PhoneCheckPart').css('display','none');
		$('#PhoneCheckTitle').css('display','none');
		$.endPageLoading();
		alert(error_info);
    });
//	$.beginPageLoading("正进行金库认证..");
//	$.treasury.auth('IBS9236', $("#MOBILENUM").val(), function(ret){
//		$.endPageLoading();
//		if(true === ret){
//			alert("认证成功！");
//		}else{
//			alert("认证失败！");
//		}
//});
}

function onTradeSubmit() {
	
	checkRealName();
	
	//$.cssubmit.setParam("PAY", $.feeMgr.getTotalFee());
	//$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
	//屏蔽打印功能，提示用户去另一界面打印发票并收取费用。
	//$.cssubmit.bindCallBackEvent(tradeCallBack2);		//设置提交业务后回调事件
	
	return true;
}

function tradeCallBack(data){
	var content;
	if (data!=null) {	
		content = "客户订单标识："+data.get(0).get("ORDER_ID")+"<br/>点【确定】继续业务受理。";
	}else{
		alert('打印数据为空!');
		return;
	}
	//tradePrint(data);
	$.cssubmit.showMessage("success", "业务受理成功", content, true);
	$.printMgr.bindPrintEvent(tradePrint);
//	$.printMgr.bindPrintEvent(printTrade);
}

function tradeCallBack2(data){
	var content;
	if (data!=null) {
	
		content = "业务受理流水标识："+data.get(0).get("TRADE_ID")+"<br/>请到【跨区补卡打印】页面收取费用并打印票据。";
	}else{
		alert('打印数据为空!');
		return;
	}
	//tradePrint(data);
	$.cssubmit.showMessage("success", "业务受理成功", content, false);
	//$.printMgr.bindPrintEvent(tradePrint);
//	$.printMgr.bindPrintEvent(printTrade);

}

/**
 * 打印发票回调方法
 * @param data
 */
function printTrade(tradeData){
	var serialNumber = tradeData.get(0).get("SERIAL_NUMBER");
	var params = "&TRADE_ID=" + tradeData.get(0).get("TRADE_ID");
	
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit(null, "printTrade", params, null, 
		function(printDataset){
			$.endPageLoading();
			//设置打印数据
			$.printMgr.setPrintData(printDataset);
			//启动打印
			$.printMgr.printReceipt();
		},
		function(code, info, detail){
			$.endPageLoading();
			MessageBox.alert("错误提示","加载打印数据错误！", null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示","加载打印数据超时！");
	});	
}

//打印免填单
function tradePrint(datas){
		var serialNumber = datas.get(0).get("SERIAL_NUMBER");
		var custName = $("#CUST_NAME").val();
		var psptType = $("#IDCARDTYPE").val();
		var psptId = $("#IDCARDNUM").val();
		var verifyType = "";
		var reason = $("#REASON").val();
		var params = "&SERIAL_NUMBER=" + serialNumber;
		if("0" == $("#VERIFY_TYPE").val()){
			verifyType = "证件校验";
		}else if("1" == $("#VERIFY_TYPE").val()){
			verifyType = "密码校验";
		}
		params += "&CUST_NAME=" + custName;
		params += "&TRADE_ID=" + datas.get(0).get("TRADE_ID");
		params += "&ID_CARD_TYPE=" + psptType;
		params += "&IDCARDNUM=" + psptId;
		params += "&TRADE_TYPE_CODE=" + "141";
		params += "&VERIFY_TYPE=" + verifyType;
		params += "&ACCEPT_DATE=" + datas.get(0).get("ACCEPT_DATE");
		params += "&SIM_CARD_NO=" + datas.get(0).get("SIM_CARD_NO");
		params += "&TRADE_DETAIL=" + "异地补卡";
		params += "&CHECK_DESC=" + verifyType;
		params += "&EMPTY_CARD_ID=" + datas.get(0).get("EMPTY_CARD_ID");
		
		$.beginPageLoading("加载打印数据。。。");
		$.ajax.submit(null, "loadPrintData", params, null, 
			function(data){
				$.endPageLoading();
				//设置打印数据
				$.printMgr.setPrintData(data.get("PRINT_DATA"));
				//启动打印
				$.printMgr.printReceipt();
				
				$.endPageLoading();
			},
			function(errorcode, errorinfo){
				$.endPageLoading();
				MessageBox.alert("信息提示",errorinfo);
			},function(){
				$.endPageLoading();
				alert("加载打印数据超时");
		});	
	}

function changeValueBySn() {
	var routeType = $("#ROUTETYPE").val();
	var serialNumber = $("#MOBILENUM").val();
	var idType = $("#IDTYPE").val();
	if("01" == routeType && "01" == idType) {
		$("#ID_ITEM_RANGE").val(serialNumber);
	}
	$("#USER_PASSWD").val('');
	$("#IDCARDNUM").val('');
	$("#PIC_ID").val('');
	$("#PIC_STREAM").val('');
	$("#BACKBASE64").val('');
	$("#FRONTBASE64").val('');
	$("#custinfo_RemoteVerifyFlag").val('0');//变换号码后，设置鉴权为不通过
	
}
function before(){
	if (!checkRealName()) {
		return false;
	}
	$.simcard.setSerialNumber($("#MOBILENUM").val());
	return true;
}

function checkRealName(){
	if("1"!=$('#queryCardTag').val()){
		alert("请先进行卡查询");
		return false;
	}
	if(!$.validate.verifyAll('AddrCondPart')) {
		return false;
	}
	
		if(!$.validate.verifyAll('CustCondPart')) {
			return false;
		}
	
	if("1"==$('#BeautifulFlag').val()){
		if("1" != $("#PHONECHECK").val()){
			alert("好友号码验证未通过！");
			return false;
		}
	}
	return true;
}

function checkVerify() {
	var verifyType = $("#VERIFY_TYPE").val();
	if("0"==verifyType) {//证件校验
		//$('#pwdocx').css("display","none");//客服密码
		$('#userpasswd').css("display","none");
		$('#idcardtype').css("display","");
		$('#idcardnum').css("display","");
		
		$('#IDCARDTYPE').attr("nullable","no");
		$('#IDCARDNUM').attr("nullable","no");
		$('#USER_PASSWD').attr("nullable","yes");
		$('#USER_PASSWD').attr("datatype","text");
		$('#IDCARDNUM').val("");
		$('#USER_PASSWD').val("");
	}else if("1"==verifyType) {//密码校验
		//$('#pwdocx').css("display","");//客服密码
		$('#idcardtype').css("display","none");
		$('#idcardnum').css("display","none");
		$('#userpasswd').css("display","");
		
		$('#USER_PASSWD').attr("nullable","no");
		$('#USER_PASSWD').attr("datatype","pinteger");
		$('#IDCARDTYPE').attr("nullable","yes");
		$('#IDCARDNUM').attr("nullable","yes");
		$('#IDCARDNUM').val("");
		$('#USER_PASSWD').val("");
	}
}
function afterReadCard(data){
	
	if(data.get("REMOTE_SIM_TAG")&&"1"==data.get("REMOTE_SIM_TAG")){
		$("#SIM_CARD_NO").val(data.get("SIM_CARD_NO"));
		return;
	}
	
	var emptyCardId = data.get("EMPTY_CARD_ID");
/*	if( 20 == emptyCardId.length){
	MessageBox.alert("错误提示","异地写卡暂时不支持预制卡写卡，请更换卡片！");
	}*/
	var sn = $("#MOBILENUM").val();
	var simCardNo =  data.get("SIM_CARD_NO");
 
	$("#EMPTY_CARD_ID").val(emptyCardId);
	$("#SIM_CARD_NO").val(simCardNo);
	
	//alert("读取卡费"+emptyCardId);
	//获取卡费
	//测试环境 feemgr.js报错，可能是需要连pos,联调中先屏蔽掉，也不知道陕西跨区补卡是否需要钱。
	
	$.feeMgr.clearFeeList();
	$("#FEE").val("");
	var level = $("#LEVEL").val();
	var isSuperStrLevel = false;
	if (level=="04" || level=="05" || level=="06" || level=="07") {
		alert("该客户为四星级或四星级以上客户,费用减免！");
		isSuperStrLevel = true;
	}
	var serviceFeeObj = new Wade.DataMap();
	serviceFeeObj.put("TRADE_TYPE_CODE", "149");
	serviceFeeObj.put("MODE", "0"); 
	serviceFeeObj.put("CODE", "0"); 
	serviceFeeObj.put("FEE", "1000");  
	if (!isSuperStrLevel) {
		//serviceFeeObj.put("PAY", "0");  
		$.feeMgr.insertFee(serviceFeeObj);
	}
	 
	/*
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'getDevicePrice', "&EMPTY_CARD_ID="+emptyCardId+"&SERIAL_NUMBER="+sn, '', function(data){
	   	    alert("data="+data);
		//设置卡费信息，是否免费等
			if(data.get("FEE")){
				$.endPageLoading();
				alert("卡费 ："+data.get("FEE"));
				var obj = new Wade.DataMap();
				if(data.get("FEE")){
				obj.put("TRADE_TYPE_CODE", "141");
				obj.put("MODE", "0"); 
				obj.put("CODE", "10"); 
				obj.put("FEE", data.get("FEE"));  
				$.feeMgr.insertFee(obj);
				$("#FEE").val(data.get("FEE"));
			}
			$.endPageLoading();
		}
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
    	$("#BRAND_CODE").val('111111111');
	//end
	$.endPageLoading();*/
}   

function beforeWriteCard(){
	if(!checkRealName()){
		return false;
	}
//	checkRealName();
	if($("#custinfo_RemoteVerifyFlag").val()!="1"){//鉴权失败
		alert("该号码写卡鉴权未成功，请重新鉴权！");
		return false;	
	}
	var sn = $("#MOBILENUM").val();
	var emptyCardId = $("#EMPTY_CARD_ID").val();
	var simCardNo = $("#SIM_CARD_NO").val(); 
	var identCode = $("#IDENT_CODE").val(); 	
	var changeCardTag = $("#RECARD_TYPE_K").val();	
	 //alert(sn+" --- "+emptyCardId+" --- "+simCardNo);
	//获取写卡信息
	$.beginPageLoading("获取异地写卡数据中...");
	$.ajax.submit('', 'applyRemoteWrite', "&EMPTY_CARD_ID="+emptyCardId+"&MOBILENUM="+sn+"&SIM_CARD_NO="+simCardNo+"&IDENT_CODE="+identCode+"&CHANGE_CARD_TAG="+changeCardTag, '', function(data){
		//将写卡信息保存起来，供写卡服务 组装写卡指令
		var speSimInfo=$.DataMap();
		$("#IMSI").val(data.get("IMSI"));
		$("#ICCID").val(data.get("ICCID"));
		$("#PIN1").val(data.get("PIN1"));
		$("#PIN2").val(data.get("PIN2"));
		$("#PUK1").val(data.get("PUK1"));
		$("#PUK2").val(data.get("PUK2"));
		$("#SMSP").val(data.get("SMSP"));
		$("#ReqSeq").val(data.get("ReqSeq"));

		var params =data.toString();
		
		$("#SIM_CARD_NO").val(data.get("ICCID"));
		$("#NEW_SIM_CARD").val(data.get("ICCID"));
		$("#CSSubmitID").val("1");
		$.endPageLoading();
		$.simcard.setSerialNumber($("#MOBILENUM").val());
		$.simcard.setParams(params);
		$.simcard.checkWriteCard();
		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
	//end
}
function afterWriteCard(data){
	var simCardNo = data.get("SIM_CARD_NO");
	var imsi = data.get("IMSI");
	var emptycardid = data.get("EMPTY_CARD_ID");
	$("#SPROVINCE_CODE").html($("#COP_SI_PROV_CODE_NAME").val());
	$("#IDVALUE").html($("#IDVALUES").val());
	$("#SCUST_NAME").html($("#CUST_NAME").html());
	$("#IMSI").html(imsi);
	$("#SIM_CARD_NO").html(simCardNo);
	$("#NEW_SIM_CARD").val(simCardNo);
	$("#EMPTY_CARD_ID").val(emptycardid);
	$("#CSSubmitID").val("1");
	$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
}

function beforeActiveCard(){
	//卡片激活之前校验，确定各个要素到位
	if(!$.validate.verifyAll('AddrCondPart')) {
	return false;
	}
		if(!$.validate.verifyAll('CustCondPart')) {
			return false;
		}

	var isCSSubmitID = $("#CSSubmitID").val();
	if(isCSSubmitID != "1"){
		MessageBox.alert("错误提示", "请先进行读写卡片,再进行激活提交！");
		return false;
	}	
	//end
}
function read(data){
	//alert("空卡序列号："+data.toString());
}

function beforeReadCard(){
	if(!checkRealName()){
		return false;
	}
	//验证 界面身份证和归属省身份证，是否一致	
	var sn = $("#MOBILENUM").val();
	$.simcard.setSerialNumber(sn);
	//alert("手机号码："+sn);
	return true;
}

//获取实名制流水
function  getTradeSend(){
	var transactionId = $("#TRANSACTION_ID").val();
	if(!isNull(transactionId)){
		return ;
	}
	$.beginPageLoading("获取实名制流水......");
	$.ajax.submit(null,'getTradeSend',null,null,function(data){
		$("#TRANSACTION_ID").val(data.get("TRANSACTION_ID"));//实名制流水
		$("#REALNAME_SEQ").val(data.get("TRANSACTION_ID"));//实名制流水
		
		var tradeData = $("#TRADE_DATA").val();
		if(tradeData){
			var tradeDataMap = $.DataMap(tradeData);
			tradeDataMap.put("TRANSACTION_ID",data.get("TRANSACTION_ID"));
			tradeDataMap.put("REALNAME_FLAG",$("#IS_REAL").val());
			$("#TRADE_DATA").val(tradeDataMap.toString());
		}
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	alert(error_info);
	//showDetailErrorInfo(error_code,error_info,derror);
    });
}

function clickScanPspt()
{
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
	if( custName == ""){
		alert("请输入客户姓名！");
		return false;
	}
	$("#HIDDEN_CUST_NAME").val(custName);
	
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
					}
                    else if (data && data.get("X_RESULTCODE") == "3") {
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

/*密码组件后赋值*/
function inputPassWD(){
	var userPasswd="";
	$("#USER_PASSWD").val("");
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
	$("#USER_PASSWD").val(userPasswd);
	
}
//下发短信验证码
function simpleCardNotice(){
	var sn=$('#MOBILENUM').val();
	var param="&MOBILENUM="+sn;
	$.beginPageLoading("短信验证码下发中......");
	$.ajax.submit(null,'simpleCardNotice',param,null,function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
			alert("短信下发成功，请注意查收！");
		}
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	alert(error_info);
    });
}
