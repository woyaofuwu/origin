$(document).ready(function(){
	$('#submit').attr("disabled","true");
});
function queryCustInfo(){
	
	if(!$.validate.verifyAll("queryCondPart")){
		return false;
	}
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AddrCondPart,CustCondPart', 'queryCustInfo', null, 'custInfoPart', function(data){
		alert("跨区补卡业务办理要求，首先需在异地写卡（客户资料查询）界面验证客户有效身份证号码或服务密码，然后在此界面验证客户另一项信息后（两次验证不可同时进行）方可办理，请确认。");
		
		$("#cond_SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		$.feeMgr.setPosParam("141", $("#MOBILENUM").val(), "0898");

		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
//function writeCardActive(){
//	if(!$.validate.verifyAll("queryCondPart")){
//		return false;
//	}
//	$.beginPageLoading("数据处理中...");
//	$.ajax.submit('AddrCondPart,CustCondPart,custInfoPart', 'writeCardActive', null, 'custInfoPart', function(data){
//		$.endPageLoading();
//		$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
//	},
//	function(error_code,error_info)
//	{
//		$.endPageLoading();
//		alert(error_info);
//    });
//}
function checkId(){
	var idType = $("#IDCARDTYPE").val(); 
	/*
	 * 1	VIP卡
	0	身份证
	A	护照
	C	军官证
	K	武装警察身份证
	Z	其他证件 
	 * */
	var highPriv = "";
	if(idType == "0"){
		$("#IDCARDNUM").val("");
		$.beginPageLoading("载入中..");
	    $.ajax.submit('', 'getPriv', '', '',function(data){
			$.endPageLoading();
			$("#PsptScanBtn").attr("disabled",false);
			highPriv = data.get("HIGH_PRIV"); 
			$("#IDCARDNUM").attr("disabled",true);
			if(highPriv!=null && highPriv == "1"){
				$("#IDCARDNUM").attr("disabled",false);
			}
			
		}, 
		function(error_code,error_info,detail){
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, null, detail);
	   });
	}else{
		$("#IDCARDNUM").val("");
		$("#PsptScanBtn").attr("disabled",true);
		$("#IDCARDNUM").attr("disabled",false);
	}
}

function onTradeSubmit() {
	$.cssubmit.setParam("PAY", $.feeMgr.getTotalFee());
	$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
	
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
	$.cssubmit.showMessage("success", "业务受理成功", content, true);
	
	$.printMgr.bindPrintEvent(tradePrint);
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
			MessageBox.error("错误提示","加载打印数据错误！", null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示","加载打印数据超时！");
	});	
}

//打印免填单
function tradePrint(datas){
		var eparchyCode = "0898";
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
		$("#IDVALUE").val(serialNumber);
	}
}
function before(){
	$.simcard.setSerialNumber($("#MOBILENUM").val());
	//alert("主号码  = "+$("#MOBILENUM").val());
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
	var emptyCardId = data.get("EMPTY_CARD_ID");
	var sn = $("#MOBILENUM").val();
	//alert("读取卡费"+emptyCardId);
	//获取卡费
	$.feeMgr.clearFeeList();
	$("#FEE").val("");
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'getDevicePrice', "&EMPTY_CARD_ID="+emptyCardId+"&SERIAL_NUMBER="+sn, '', function(data){
		//设置卡费信息，是否免费等
		$.endPageLoading();
		//alert("卡费 ："+data.get("FEE"));
		var obj = new Wade.DataMap();
		obj.put("TRADE_TYPE_CODE", "141");
		obj.put("MODE", "0"); 
		obj.put("CODE", "10"); 
		obj.put("FEE", data.get("FEE"));  
		$.feeMgr.insertFee(obj);
		$("#FEE").val(data.get("FEE"));
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
	//end
}

function beforeWriteCard(){
	//获取写卡信息
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'getSimCardInfo', null, '', function(data){
		//将写卡信息提供至写卡组件
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
	//end
}
function afterWriteCard(data){
	//alert(data.toString());
	var simCardNo = data.get("SIM_CARD_NO");
	var imsi = data.get("IMSI");
	var emptycardid = data.get("EMPTY_CARD_ID");
	//alert(simCardNo+"  "+imsi+" "+emptycardid);
	$("#NEW_SIM_CARD").val(simCardNo);
	$("#NEW_IMSI").val(imsi);
	$("#EMPTY_CARD_ID").val(emptycardid);

}
//function afterWriteCard(data){
//	//判断写卡是否成功，如果不成功，则提示进行释放白卡资源的操作（SIM卡回收再利用）。
//	
//	
//	
//	//写卡信息回传
//	$.beginPageLoading("数据处理中...");
//	$.ajax.submit('', 'writeCardResultback', null, '', function(data){
//		
//		//提示进行提交操作，对SIM卡进行激活
//		$.endPageLoading();
//	},
//	function(error_code,error_info)
//	{
//		$.endPageLoading();
//		alert(error_info);
//    });
//	//end
//}

function beforeActiveCard(){
	//卡片激活之前校验，确定各个要素到位
	
	//end
}
function read(data){
	//alert("空卡序列号："+data.toString());
}

function beforeReadCard(){
	var sn = $("#MOBILENUM").val();
	$.simcard.setSerialNumber(sn);
	//alert("手机号码："+sn);
	return true;
}

