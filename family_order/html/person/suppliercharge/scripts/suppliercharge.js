var shoppers = null;
var fees = null;
//设置选择的供应商编码
var setSerialNumber=function(){
	var factoryCode=$("#FACTORY_CODE").val();
	if(!factoryCode || factoryCode=="") return;
	if(!shoppers){
		MessageBox.error("错误提示","供应商数据错误!");
		return;
	}
	shoppers.each(function(item,index,totalcount){
		if(item.get("CORP_NO") == factoryCode){
			$("#AUTH_SERIAL_NUMBER").val(item.get("RSRV_STR1"));
			return false;
		}
	});
};

var setFeeHideField=function(fee){
	if(!fees){
		return;
	}
	fees.each(function(item,index,totalcount){
		if(item.get("BALANCE") == fee){
			$("#YEAR").val(item.get("YEAR"));
			$("#ACCEPT_MONTH").val(item.get("ACCEPT_MONTH"));
			$("#RSRV_STR3").val(item.get("RSRV_STR3"));
			
			return false;
		}
	});
};

var queryFeeInfo=function(){
	var chnlId = $("#CHNL_ID").val();
	var factoryCode = $("#FACTORY_CODE").val();
	if(!chnlId || chnlId==""){
		alert("请选择卖场！");
		return;
	}
	if(!factoryCode || factoryCode==""){
		alert("请选择供应商！");
		return ;
	}
	$.feeMgr.clearFeeList();
	//启动Auth认证，加载供应商三户资料
	$("#AUTH_SERIAL_NUMBER").removeAttr("datatype");
	$.auth.autoAuth();
};

var loadTradeInfo=function(data){
	var chnlId = $("#CHNL_ID").val();
	var factoryCode = $("#FACTORY_CODE").val();
	var param = "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE");
	param += "&CHNL_ID="+chnlId;
	param += "&FACTORY_CODE="+factoryCode;
	
	$.beginPageLoading("加载供应商费用数据。。。");
	$.ajax.submit("", "loadTradeInfo", param, "SupplierFeePart", function(dataset){
		$.endPageLoading();
		fees = dataset;
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","加载供应商费用数据错误!", null, null, info, detail);
	});
};

var addFeeList=function(){
	var fee=$.trim($("#FEE").val()); 
	if(!fee || fee == "" || !$.isNumeric(fee)){
		$.feeMgr.clearFeeList();
		return;
	}
	setFeeHideField(fee);
	var yearFee = parseFloat(fee)*100;
	var feeObj =$.DataMap();
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	feeObj.put("MODE","0"); 
	feeObj.put("CODE","440");
	feeObj.put("FEE",yearFee);
	
	$.feeMgr.clearFeeList(); 
	$.feeMgr.insertFee(feeObj);
};

var submitBeforeCheck=function(){
	var feeList = $.feeMgr.getFeeList();
	if(feeList == null || feeList.length==0){
		alert("请选择费用！");
		return false;
	}
	if(!fees){
		alert("该供应商已缴费！");
		return false;
	}
	var authData=$.auth.getAuthData();
	var param = "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&EPARCHY_CODE="+authData.get("USER_INFO").get("EPARCHY_CODE");
	param += "&CHNL_ID="+$("#CHNL_ID").val();
	param += "&FACTORY_CODE="+$("#FACTORY_CODE").val();
	param += "&YEAR="+$("#YEAR").val();
	param += "&ACCEPT_MONTH="+$("#ACCEPT_MONTH").val();
	param += "&FEE_AMOUNT="+$("#FEE").val();
	param += "&RSRV_STR3="+$("#RSRV_STR3").val();
	param += "&CORP_NAME="+$("#CHNL_ID").find("option:selected").text();
	$.cssubmit.addParam(param);
	
	//$.printMgr.setPrintParam("FEE_TYPE", "管理费");
	$.cssubmit.bindCallBackEvent(supplierChargeCallBack);
	return true;
};

var supplierChargeCallBack=function(data){
	if(data && data.get("TRADE_ID") == null && data.get("TRADE_ID") ==""){
		$.cssubmit.showMessage("error", "业务受理提示", "供应商缴费失败", false);
		return;
	}
	$.printMgr.bindPrintEvent(printReceipt);
	$.cssubmit.showMessage("success", "业务受理提示", "供应商缴费成功!", true);
};

var printReceipt=function(data){
	var param = "&TRADE_ID="+data.get("TRADE_ID");
	param += "&FEE_AMOUNT="+data.get("FEE_AMOUNT");
	param += "&FEE_NAME="+"卖场管理费";  
	param += "&RECEIPT_NAME="+"供应商缴费收据";
	param += "&FEE_MODE="+"2";//押金
	param += "&TRADE_TYPE_CODE="+data.get("TRADE_TYPE_CODE"); 
	param += "&CHNL_ID="+data.get("CHNL_ID");
	param += "&ACCEPT_MONTH="+data.get("ACCEPT_MONTH");
	param += "&YEAR="+data.get("YEAR");
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("", "printHandGathering", param, null, 
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
};

$(document).ready(function(){
	//$.feeMgr.setTestTag(true);
	$.beginPageLoading("加载供应商数据。。。");
	$.ajax.submit("", "onInitTrade", null, "SupplierPart", function(dataset){
		$.endPageLoading();
		shoppers = dataset;
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","加载供应商缴费数据错误!", $.auth.reflushPage, null, info, detail);
	});

});