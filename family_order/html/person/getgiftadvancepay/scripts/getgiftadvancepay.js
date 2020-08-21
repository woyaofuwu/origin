var printData = null;
var loadTradeInfo=function(data){
	var param = "&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&OPEN_DATE="+data.get("USER_INFO").get("OPEN_DATE");
	param += "&ACCT_TAG="+data.get("USER_INFO").get("ACCT_TAG");
	param += "&BRAND_CODE="+data.get("USER_INFO").get("BRAND_CODE");
	param += "&EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
	$.beginPageLoading("校验业务。。。");
	$.ajax.submit("", "checkAcceptTrade", param, "", function(rsData){
		$.endPageLoading();
		printData = rsData.get("PRINT_DATA");
		alert(printData);
		$("#PAY_MONEY").attr("disabled", false);
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","校验业务受理限制。", $.auth.reflushPage, null, info, detail);
	});
};
var submitBeforeCheck=function(){
	if($("#PAY_MONEY").val() == ""){
		alert("请选择预存金额!");
		return false;
	}
	data=$.auth.getAuthData();
	var param = "&EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	
	$.cssubmit.addParam(param);
	
	//打印自定义信息,SUB_CONTENT跟模板里自定义变量对应
	$.printMgr.setPrintParam("SUB_CONTENT","尊敬的客户，感谢您参加预存话费送礼品活动，参加活动预存的话费不作退款处理。");
	return true;
};

$(document).ready(function(){
	$("#PAY_MONEY").bind("change", function(){
		var fee = 0;
		if($(this).val() == "1"){
			fee = 5000;
		}else if($(this).val() == "2"){
			fee = 10000;
		}
		var data = $.DataMap();
		data.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
		data.put("MODE", "2");
		data.put("CODE", "51");
		data.put("FEE", fee);
		
		$.feeMgr.clearFeeList();
		$.feeMgr.insertFee(data);
	});
	$("#PAY_MONEY").attr("disabled", true);
});
