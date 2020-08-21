$(document).ready(function(){
	//$.feeMgr.setPosParam("1101", "00000000000", "0898");
	
	$("#FEE_PAY_MODE").find("option[value=1]").attr("selected", true);
	
	/**不采用费用展示框展示
	$("#FEE_AMOUNT").bind("keydown", function(){
		$("#addBtn").attr("disabled", false);
	});
	$("#addBtn").bind("click", function(){
		if(!$.validate.verifyAll("HandGatheringFeePart")){
			return false;
		}
		var feeAmount = $("#FEE_AMOUNT").val();
		if(!$.isNumeric(feeAmount)){
			alert($("#FEE_AMOUNT").attr("desc")+"不合法！");
			$("#FEE_AMOUNT").focus();
			return false;
		}
		$("#addBtn").attr("disabled", true);
		
		addFeeList(feeAmount);
	});*/
});

var addFeeList=function(val){
	var feeStr = $.trim(val);
	if(feeStr==""){
		$.feeMgr.clearFeeList();
		return;
	}
	var fee = Math.round(parseFloat(feeStr)*100);
	var feeObj =$.DataMap();
	feeObj.put("TRADE_TYPE_CODE", "1101");
	feeObj.put("MODE","0"); 
	feeObj.put("CODE","440");
	feeObj.put("FEE",fee);
	
	if($.feeMgr.clearFeeList()){
		$.feeMgr.insertFee(feeObj);
	}
};

var submitBeforeCheck=function(){
	if(!$.validate.verifyAll("HandGatheringFeePart")){
		$("#addBtn").attr("disabled", false);
		return false;
	}
	var payFlag = $("#PAY_FLAG").val();
	var feeAmount = $("#FEE_AMOUNT").val();
	if(!$.isNumeric(feeAmount)){
		alert($("#FEE_AMOUNT").attr("desc")+"不合法！");
		$("#FEE_AMOUNT").focus();
		return false;
	}
	/**
	var feeList = $.feeMgr.getFeeList();
	if(feeList == null || feeList.length==0){
		alert("请先填写相关字段并点击\"确定\"按钮，在提交！");
		$("#addBtn").attr("disabled", false);
		return false;
	}**/
	
	var fee = Math.round(parseFloat(feeAmount)*100);
	if(payFlag == '0') {		
		$.cssubmit.bindCallBackEvent(handGatheringFeeCallBack);
		$.cssubmit.addParam("&FEE_TOTAL="+fee);
		$("#PAY_FLAG").val("");
		$.cssubmit.submitTrade();
	}else {	
		if(!window.confirm("确定手工补录收款吗？")){
			return false;
		}
		$.beginPageLoading("正在支付...");
		var feeName = $("#FEE_NAME").val();
		var feeAmount = $("#FEE_AMOUNT").val();
		if(parseFloat(feeAmount) > 0) {
			var param = "&FEE_AMOUNT="+feeAmount
						+"&FEE_NAME="+feeName;
			ajaxSubmit('', 'createPaySubmit', param,'', 
			function(paydata){
				var orderId = paydata.get(0).get("ORDER_ID");
				var peerOrderId = paydata.get(0).get("PEER_ORDER_ID");
				var param ="&ORDER_ID="+orderId+"&PEER_ORDER_ID="+peerOrderId;
				var popupId= $.popupPage("pay.order.PayMain","queryOrderInfo",param+'&PARENT_EVENT_ID=_UN_PAY_FEE_ID','支付收银',800,400,'_UN_PAY_FEE_ID','',subsys_cfg.payment,false,false);
				$('#_UN_PAY_FEE_ID').val(popupId);
			},
			function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
		    },function(){
				$.cssubmit.showMessage("success", title, content + "。。", false);
				$.endPageLoading();
			});	
			$.endPageLoading();
			return false;
		}else {
			$.cssubmit.bindCallBackEvent(handGatheringFeeCallBack);
			$.cssubmit.addParam("&FEE_TOTAL="+fee);
			$("#PAY_FLAG").val("");
			return true;
		}
	}
};

var handGatheringFeeCallBack=function(data){
	if(data && data.get("RESULT_CODE") != "0"){
		$.cssubmit.showMessage("error", "业务受理提示", "手工补录收款失败，原因:"+data.get("RESULT_INFO"), false);
		return;
	}
	$.printMgr.bindPrintEvent(printHandGatheringFee);
	$.cssubmit.showMessage("success", "业务受理提示", "手工补录收费成功!", true);
};

/**
 * 调用支付中心回调
 */
function updatePayState(result){
	
	if(typeof(result) !="undefined"){
		var payresultMap = $.DataMap(result);
		var state = payresultMap.get("STATE");
		if(state =="2"){
			console.log("支付中心回调");
			$("#PAY_FLAG").val("0");			
			$("#CSSUBMIT_BUTTON").trigger("click");
			return true;
		}else{
			console.log("支付失败");
			return false;
		}	
	}
}


var printHandGatheringFee=function(data){
	var param = "&TRADE_ID="+data.get("TRADE_ID");
	param += "&FEE_AMOUNT="+data.get("FEE_AMOUNT");
	param += "&FEE_NAME="+data.get("FEE_NAME");
	param += "&FEE_REASON="+data.get("FEE_REASON");
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
