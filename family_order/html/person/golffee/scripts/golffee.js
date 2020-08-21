
var loadTradeInfo=function(data){
	var param = "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME");
	param += "&CITY_CODE="+data.get("CUST_INFO").get("CITY_CODE");
	if(data.get("VIP_INFO")){
		param += "&VIP_TYPE_CODE="+data.get("VIP_INFO").get("VIP_TYPE_CODE");
		param += "&VIP_CLASS_ID="+data.get("VIP_INFO").get("VIP_CLASS_ID");
		param += "&CUST_MANAGER_ID="+data.get("VIP_INFO").get("CUST_MANAGER_ID");
	}
	
	$.beginPageLoading("加载业务数据。。。");
	$.ajax.submit("", "loadTradeInfo", param, "GolfFeePart", function(data){
		$.endPageLoading();
		$("#YEAR_FEE").attr("disabled", false);
		$("#addFeeBtn").bind("click", addFee);
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","加载业务数据错误!", $.auth.reflushPage, null, info, detail);
	});
};

var addFee=function(){
	var fee = $.trim($("#YEAR_FEE").val());
	if(fee == "") {
		alert("请输入年费用！");
		return false;
	}
	if(!$.isNumeric(fee)){
		alert("年费用数字不合法！");
		return false;
	}

	var yearFee = Math.round(parseFloat(fee)*100);
	if(yearFee<0){
		alert("年费用不能小于0！");
		return false;
	}
	$("#YEAR_FEE").val(yearFee/100);
	var feeObj =$.DataMap();
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	feeObj.put("MODE", "0");//预存类
	feeObj.put("CODE",  "440");
	feeObj.put("FEE",  yearFee);
	$.feeMgr.clearFeeList();
	$.feeMgr.insertFee(feeObj);
};

var submitBeforeCheck=function(){
	var feeList = $.feeMgr.getFeeList();
	if(feeList == null || feeList.length==0){
		alert("请输入年费！");
		$("#YEAR_FEE").focus();
		return false;
	}
	return true;
};