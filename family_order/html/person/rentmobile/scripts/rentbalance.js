
function refreshPartAtferAuth(data) {
	//alert(data.get("TRADE_TYPE_CODE"));
	resetArea("contentPart", true);
	$.ajax.submit("", "loadChildInfo", "&USER_ID=" + data.get("USER_INFO").get("USER_ID").toString() + "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER").toString(), "ResInfoPart,rentMobileInfoPart", function (data) {
		$.endPageLoading();
	}, function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}
function onTradeSubmit() {
	/**
	if($("#Jsubmit").name != 1){
		alert('请先计算押金!');
		return false;
	}else {
		return true;
	}
	**/
	addBusiFee();
	var authData = $.auth.getAuthData();
	var param = "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&RENT_MODE_CODE="+$('#RENT_MODE_CODE').val();
	$.cssubmit.addParam(param);
	
	/**
	$.cssubmit.bindCallBackEvent(function reprintCallBack(data) {
		if(data && data.length>0) {
		alert(data);
			var content = "租金结算完成，如需退租，请到退租界面办理退租！<br/>客户订单标识：" + data.get(0).get("ORDER_ID") + "<br/>点【确定】继续业务受理。";
			$.cssubmit.showMessage("success", "业务受理成功", content, true);
			
			$.printMgr.bindPrintEvent(printTrade);
		}
	});		//设置提交业务后回调事件
	*/
	
	return true;
}

/**
 * 打印发票回调方法
 * @param data
 */
function printTrade(tradeData){
	$.beginPageLoading("加载打印数据。。。");
	var params = "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	params += "&TRADE_ID=" + tradeData.get(0).get("TRADE_ID");
	params += "&ROUTE_EPARCHY_CODE=" + tradeData.get(0).get("DB_SOURCE");
	params += "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val();
	$.ajax.submit(null, "loadPrintData", params, null, 
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

/**
*增加营业费用
*/
function addBusiFee() {
	var rentStartDate = $("#RENT_START_DATE").val();
	var rentModeCode = $("#RENT_MODE_CODE").val();
	var StartDate = rentStartDate;
	var rentDays = daysBetween(getNowDate(), rentStartDate);
	var months = 0;
	if(rentModeCode == '1') {
		//删除老费用
		$.feeMgr.removeFee('249','0','160');
		//新增费用
		var rentMoney = rentDays * 30;
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "249");
		feeData.put("MODE", "0");
		feeData.put("CODE", "160");
		feeData.put("FEE", rentMoney * 100);
		$.feeMgr.insertFee(feeData);
		//alert(feeData);
	}else if (rentModeCode == '0') {
		//删除老费用
		$.feeMgr.removeFee('249','0','160');
		//新增费用
		months = Math.ceil(rentDays / 15);
		var rentMoney = months * 250;
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "249");
		feeData.put("MODE", "0");
		feeData.put("CODE", "160");
		feeData.put("FEE", rentMoney * 100);
		$.feeMgr.insertFee(feeData);
		//alert(feeData);
	} else {
		alert("未知租金方式，请与管理员联系!");
		return false;
	}
}

/*比较两日期相关天数*/
function daysBetween(DateOne, DateTwo) {
	var OneMonth = DateOne.substring(5, DateOne.lastIndexOf("-"));
	var OneDay = DateOne.substring(DateOne.length, DateOne.lastIndexOf("-") + 1);
	var OneYear = DateOne.substring(0, DateOne.indexOf("-"));
	var TwoMonth = DateTwo.substring(5, DateTwo.lastIndexOf("-"));
	var TwoDay = DateTwo.substring(DateTwo.length, DateTwo.lastIndexOf("-") + 1);
	var TwoYear = DateTwo.substring(0, DateTwo.indexOf("-"));
	var cha = ((Date.parse(OneMonth + "/" + OneDay + "/" + OneYear) - Date.parse(TwoMonth + "/" + TwoDay + "/" + TwoYear)) / 86400000);
	return Math.ceil(Math.abs(cha));
}
/*当前时间YYYY-MM-DD*/
function getNowDate() {
	var now = new Date();
	y = now.getFullYear();
	m = now.getMonth() + 1;
	d = now.getDate();
	m = m < 10 ? "0" + m : m;
	d = d < 10 ? "0" + d : d;
	return y + "-" + m + "-" + d;
}

