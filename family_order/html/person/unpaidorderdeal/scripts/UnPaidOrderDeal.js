var gloabObj = null;
$(document).ready(function(){
	qryUnpaidTrade();
});
function qryUnpaidTrade()
{
	if(!$.validate.verifyAll("QueryCondPart")){
		return false; 
	}
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	if (startDate>endDate){
		alert("开始时间不能大于结束时间！请重新输入！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户可返销订单查询
	$.ajax.submit('QueryCondPart', 'queryUnpaidTrade', null, 'TradeInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function doPayTrade(obj)
{
	$.beginPageLoading("正在支付...");
	var orderId = $(obj).attr("orderId");
	var tradeId = $(obj).attr("tradeId");
	var param ="&ORDER_ID="+orderId;
	param+="&TRADE_ID="+tradeId;
	param+="&SERIAL_NUMBER="+$(obj).attr("serialNumber");
	param+="&USER_ID="+$(obj).attr("userId");
	param+="&TRADE_EPARCHY_CODE="+$(obj).attr("eparchyCode");
	param+="&TRADE_TYPE_CODE="+$(obj).attr("tradeTypeCode");
	gloabObj = obj;
	ajaxSubmit('', 'createPaySubmit', param,'', 
				function(paydata){
					$(obj).blur();
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
}

function updatePayState(result){
	
	if(typeof(result) !="undefined"){
		var payresultMap = $.DataMap(result);
		var state = payresultMap.get("STATE");
		if(state =="2"){
			var obj =gloabObj;
			var param = "&ORDER_ID="+$(obj).attr("orderId");
			param+="&TRADE_ID="+$(obj).attr("tradeId");
			param+="&SERIAL_NUMBER="+$(obj).attr("serialNumber");
			param+="&TRADE_EPARCHY_CODE="+$(obj).attr("eparchyCode");
			param+="&PAY_DETAIL="+payresultMap.get("PAY_DETAIL");
			if($(obj).attr("isGroup") == "GRPORDER"){
				param += "&IS_GROUP=TRUE";
			}else{
				param += "&IS_GROUP=FALSE";
			}
				
			ajaxSubmit('', 'payTrade', param,'', 
					function(data){
						if(data && data.get("RESULT")=="SUCCESS"){
							doPrintReceipt(obj);
							gloabObj =null;
						}
						$.endPageLoading();
					},
					function(error_code,error_info){
						$.MessageBox.error(error_code,error_info);
						$.endPageLoading();
				    },{async:false});
		}else{
			alert("支付失败");
		}	
	}
}

function doPrintReceipt(obj){
	$.beginPageLoading("获取打印数据。。。");
	
	var orderId = $(obj).attr("orderId");
	var tradeId = $(obj).attr("tradeId");
	var tradeData = $.DataMap();
	tradeData.put("ORDER_ID",orderId);
	tradeData.put("DB_SOURCE", $(obj).attr("eparchyCode"));
	tradeData.put("TRADE_ID", tradeId);
	tradeData.put("USER_ID", $(obj).attr("userId"));
	tradeData.put("TRADE_TYPE_CODE", $(obj).attr("tradeTypeCode"));
	tradeData.put("EPARCHY_CODE", $(obj).attr("eparchyCode"));
	tradeData.put("FEE_STATE", $(obj).attr("feeState"));
	var title = "未打印操作";
	var content = "点【打印票据】打印免填单和发票。";
	$(obj).blur();
	$.cssubmit.tradeData = tradeData;
	$.cssubmit.loadPrintTradeData(tradeData, title, content);
}

function doPrintNote(obj){
	
	var orderId = $(obj).attr("orderId");
	var tradeId = $(obj).attr("tradeId");
	var param ="&ORDER_ID="+orderId;
	param+="&TRADE_ID="+tradeId;
	param+="&SERIAL_NUMBER="+$(obj).attr("serialNumber");
	param+="&USER_ID="+$(obj).attr("userId");
	param+="&TRADE_EPARCHY_CODE="+$(obj).attr("eparchyCode");
	param+="&TRADE_TYPE_CODE="+$(obj).attr("tradeTypeCode");
	param+="&TRADE_STAFF_ID="+$(obj).attr("tradeStaffId");
	param+="&ACCEPT_TIME="+$(obj).attr("acceptDate");
	
	//$.beginPageLoading("获取打印数据。。。");
	ajaxSubmit('', 'printTradeNote', param,'',function(printInfos){
		if(printInfos && printInfos.length){
			$.endPageLoading();
			$.printMgr.setPrintData(printInfos);
			$.printMgr.printReceipt();
		}
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取打印数据错误！", null, null, info, detail);
	},{async:false});
	
}

function doCancelTrade(obj){
	$.beginPageLoading("正在取消订单。。。");
	var orderId = $(obj).attr("orderId");
	var tradeId = $(obj).attr("tradeId");
	var param ="&ORDER_ID="+orderId;
	param+="&TRADE_ID="+tradeId;
	param+="&SERIAL_NUMBER="+$(obj).attr("serialNumber");
	param+="&USER_ID="+$(obj).attr("userId");
	param+="&TRADE_EPARCHY_CODE="+$(obj).attr("eparchyCode");
	param+="&TRADE_TYPE_CODE="+$(obj).attr("tradeTypeCode");
	param+="&TRADE_STAFF_ID="+$(obj).attr("tradeStaffId");
	param+="&ACCEPT_TIME="+$(obj).attr("acceptDate");
	
	ajaxSubmit('', 'cancelTrade', param,'',function(resInfo){
		if(resInfo && resInfo.get(0).get("RESULT_CODE")=="1"){
			$(obj).parent().find("button").attr("disabled",true).addClass("e_dis");
			alert("取消订单成功！");
		}else{
			alert("取消订单失败！");
		}
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","取消订单错误！", null, null, info, detail);
	},{async:false});
	$.endPageLoading();
}