function changeValueBySn() {
	var routeType = $("#ROUTETYPE").val();
	var serialNumber = $("#MOBILENUM").val();
	var idType = $("#IDTYPE").val();
	if("01" == routeType && "01" == idType) {
		$("#IDVALUE").val(serialNumber);
	}
}

function changeValueByValue() {
	var routeType = $("#ROUTETYPE").val();
	var serialNumber = $("#IDVALUE").val();
	var idType = $("#IDTYPE").val();
	if("01" == routeType && "01" == idType) {
		$("#MOBILENUM").val(serialNumber);
	}
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

function queryCustInfo() {
	if(!verifyAll("CustCondPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('AddrCondPart,CustCondPart', 'getCustInfo', null, 'CustInfoPart,MobileTradePart,hiddenPart', function(data) {
		// $("#UserCheckFlag").val('1');
		// $("#IDENT_CODE").val(data.get("IDENT_CODE"));
		// $.cssubmit.disabledSubmitBtn(false);
		// $.endPageLoading();
		if("1" == data.get("RESULT")){
			if("1" == data.get("BUS_STATE")){
				alert("校验成功！");
				$("#IDENT_CODE").val(data.get("IDENT_CODE"));
				$("#UserCheckFlag").val('1');
				$("#BRAND_CODE").val(data.get("BRAND_CODE"));
				$.cssubmit.disabledSubmitBtn(false);
			}else{
				alert("不可办理！"+data.get("REASON"));
				$("#IDENT_CODE").val(data.get("IDENT_CODE"));
				$("#UserCheckFlag").val('1');
			}
		}else {
            alert("校验失败！");
            $("#UserCheckFlag").val('0');
            $("#IDENT_CODE").val('');
        }
        $.endPageLoading();

	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}
 
function onTradeSubmit() {
 	if(!verifyAll("MobileTradePart")) {
		return false;
	}
	
	var userCheck = $("#UserCheckFlag").val();
	if("1" != userCheck){
		alert("请进行用户身份及好友号码验证！");
		return false;
	}
	
	$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
	return true;
}

function tradeCallBack(data){
	var content;
	if (data!=null) {
		content = "业务受理成功！<br/>点【确定】继续业务受理。";
	}else{
		alert('打印数据为空!');
		return;
	}
	$.cssubmit.showMessage("success", "业务受理成功", content, true);
	
//	$.printMgr.bindPrintEvent(tradePrint);	
}

function tradePrint(datas){
//alert(datas);
	var eparchyCode = "898";
	var serialNumber = datas.get(0).get("SERIAL_NUMBER");
	var custName = $("#CUST_NAME_BAK").val();
	var psptType = $("#IDCARDTYPE_BAK").val();
	var psptId = $("#IDCARDNUM_BAK").val();
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	var verifyType = $("#VERIFY_TYPE").val();
	var reason = $("#REASON").val();
	var params = "&SERIAL_NUMBER=" + serialNumber;
	params += "&CUST_NAME=" + custName;
	params += "&TRADE_ID=" + datas.get(0).get("TRADE_ID");
	params += "&ID_CARD_TYPE=" + psptType;
	params += "&IDCARDNUM=" + psptId;
	params += "&TRADE_TYPE_CODE=" + tradeTypeCode;
	params += "&VERIFY_TYPE=" + verifyType;
	params += "&REASON=" + reason;
	params += "&ACCEPT_DATE=" + datas.get(0).get("ACCEPT_DATE");
	//alert(params);
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit(null, "loadPrintData", params, null, 
		function(data){
			$.endPageLoading();
			//设置打印数据
			$.printMgr.setPrintData(data.get("PRINT_DATA"));
			$.printMgr.params.put("TYPE", "P0003");
			$.printMgr.params.put("NAME", "P0003");
			$.printMgr.params.put("PRT_TYPE", "0");
			$.printMgr.params.put("EPARCHY_CODE", eparchyCode);
			//启动打印
			$.printMgr.printReceipt();
		},
		function(errorcode, errorinfo){
			$.endPageLoading();
			MessageBox.alert("信息提示",errorinfo);
		},function(){
			$.endPageLoading();
			alert("加载打印数据超时");
	});	
}
