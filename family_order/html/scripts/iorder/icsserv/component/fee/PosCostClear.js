$(document).ready(function(){
	//当日撤销
	$("#TodayBackBtn").bind("click", "A", cancelPosFee);
	//隔日退货
	$("#BackBtn").bind("click", "T", cancelPosFee);
	//手工调账
	$("#ManualBtn").bind("click", "M", cancelPosFee);
	//签到
	$("#SignInBtn").bind("click", $.posTrade.signIn);
	
	//退费原因
	$("#CANCEL_REASON").bind("change", changeQueryType);
	
	//查询POS刷卡信息
	$("#queryBtn").bind("click", queryPosLog);
	//选择刷卡流水
	$("#PosListPart input[name=posTrade]").bind("click", selectPosTrade);
	
});

//选择退费原因事件
function changeQueryType(){
	var cancelReason=$(this).val();
	if(cancelReason=="1"){
		$("#SnPart").css("display", "");
		$("#TradePart").css("display", "none");
		$("#SERIAL_NUMBER").attr("nullable", "no");
		$("#TRADE_ID").attr("nullable", "yes");
	}else{
		$("#SnPart").css("display", "none");
		$("#TradePart").css("display", "");
		$("#SERIAL_NUMBER").attr("nullable", "yes");
		$("#TRADE_ID").attr("nullable", "no");
	}
}

//点击查询事件
function queryPosLog(){ 
	if (!$.validate.verifyAll("SearchPosPart")){
		return false; 
	}
	$("#TodayBackBtn,#BackBtn,#ManualBtn").css("display", "none");
	$.beginPageLoading("加载刷卡记录。。。");
	$.ajax.submit("SearchPosPart", "queryPosCost", null, "PosListPart",function(data){
		$.endPageLoading();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","加载刷卡记录错误！", null, null, info, detail);
	});
	return true;
}

//选择刷卡流水
function selectPosTrade(obj){
	var posObj=$(obj);
	var canCancel=posObj.attr("canCancel");
	var todayPay=parseInt(posObj.attr("todayPay"));
	if(canCancel=="0"){
		MessageBox.alert("告警提示", "该笔费用今天可能清退过，一天内不允许重复清退！", function(){
			posObj.attr("checked", false);
		});
		return;
	}
	if(todayPay == 0){
		$("#TodayBackBtn").css("display", "");
		$("#BackBtn,#ManualBtn").css("display", "none");
		$("#TodayBackBtn").attr("disabled", false).removeClass("e_dis");
	}else if(todayPay<=30){
		$("#BackBtn").css("display", "");
		$("#TodayBackBtn,#ManualBtn").css("display", "none");	
		$("#BackBtn").attr("disabled", false).removeClass("e_dis");
	}else if(todayPay>30){
		$("#ManualBtn").css("display", "");
		$("#TodayBackBtn,#BackBtn").css("display", "none");
		$("#ManualBtn").attr("disabled", false).removeClass("e_dis");
		
	}else{
		MessageBox.alert("告警提示", "数据错误，请点击查询后重试！");
	}
}

var posReceipt = $.DataMap();

function cancelPosFee(evt){
	var transType=evt.data;
	var posTradeObj=$("#PosListPart input[name=posTrade]:checked");
	if(!posTradeObj || posTradeObj.length!=1){
		MessageBox.alert("告警提示", "请选择需要退费的POS刷卡记录！");
		return;
	}
	var amount=posTradeObj.attr("amount");
	var certNo=posTradeObj.attr("certNo");
	var refNo=posTradeObj.attr("refNo");
	var tradeTime=posTradeObj.attr("tradeTime");
	//手工调账
	if(transType=="M"){
		var param="";
		
		$.beginPageLoading("加载POS手工调账。。。");
		$.ajax.submit(null, "getPosReceipt", "&POS_TRADE_ID="+posTradeObj.val(), null, function(data){
			$.endPageLoading();
			
			posReceipt = data;
			data.eachKey(function(key, idex, total){
				if(key=="TEMP_PATH" || key=="TEMP_PATH"){
					return true;
				}
				param += "&"+key+"="+data.get(key);
			});
			$.popupPage("components.fee.PosReceipt", "showPosReceipt", param, "特约商户调帐申请单", "650", "1000", "POS_RECEIPT");
			
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示", "加载POS手工调账错误。", null, null, info, detail);
		});
	}
	//当日撤销A, 隔日退货T
	else
	{
		var reqStr="";
		if(transType=="A"){
			reqStr=$.posTrade.getPosReqStr($.posTrade.POS_CANCEL, amount, null, null, certNo);
		}else if(transType=="T"){
			reqStr=$.posTrade.getPosReqStr($.posTrade.POS_BACK, amount, tradeTime, refNo, null);
		}
		recordPosLos(posTradeObj, transType, reqStr);
	}
}

function printPosReceipt(){
	var posTradeObj=$("#PosListPart input[name=posTrade]:checked");
	if(!posTradeObj || posTradeObj.length!=1){
		MessageBox.alert("告警提示", "请选择需要退费的POS刷卡记录！");
		return;
	}
	var param = "&TRANS_TYPE=M";	 //撤销类型
	param +="&USER_ID="+posTradeObj.attr("userId");
	param +="&SERIAL_NUMBER="+posTradeObj.attr("serialNumber"); 
	param +="&TRADE_ID="+posTradeObj.attr("tradeId");
	param +="&OLD_POS_TRADE_ID="+posTradeObj.val();
	$.beginPageLoading("更新POS手工调账状态。。。");
	$.ajax.submit(null, "recordPosLog", param, null, function(data){
		$.endPageLoading();
		if (data && data.get("RESULT_CODE") == "0") {
			$.printMgr.startupPrint(posReceipt);
			$("#ManualBtn").attr("disabled", true).addClass("e_dis");
		}
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示", "更新POS手工调账状态错误。", null, null, info, detail);
	});
}

//POS撤销/退货
function recordPosLos(posTradeObj, transType, reqStr){
	if(!reqStr) return ;
	var transTypeStr="",btn="";
	if(transType=="A"){
		transTypeStr="撤销";
		btn="TodayBackBtn";
	}else{
		transTypeStr="退货";
		btn="BackBtn";
	}	
	var respStr=$.posTrade.posTrans(reqStr);
	var respData=$.posTrade.parsePosResp(respStr);
	var param = "&TRANS_TYPE="+transType;	 //撤销类型
	param +="&USER_ID="+posTradeObj.attr("userId");
	param +="&SERIAL_NUMBER="+posTradeObj.attr("serialNumber"); 
	param +="&TRADE_ID="+posTradeObj.attr("tradeId");
	param +="&OLD_POS_TRADE_ID="+posTradeObj.val();
	param +="&S_REQ="+reqStr;
	param +="&S_RESP="+respStr;
	
	//连接POS返回参数
	respData.eachKey(function(key,index,totalcount){
		param += "&"+key+"="+respData.get(key);
	});
	if(respData.get("RESP")=="00"){
		param += "&STATUS=0";			//撤销成功
		$.beginPageLoading("记录POS"+transTypeStr+"日志。。。");
		$.ajax.submit(null, "recordPosLog", param, null, function(data){
			$.endPageLoading();
			if (data && data.get("RESULT_CODE") == "0") {
				$("#"+btn).attr("disabled", true).addClass("e_dis");
				MessageBox.success("成功提示", "银联POS"+transTypeStr+"成功！", function(){
					$("#queryBtn").trigger("click");	//退费成功以后，重新刷新加载
				});
			}
			
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示", "记录POS"+transTypeStr+"日志错误。", null, null, info, detail);
		});
	}else{
		param += "&STATUS=1";			//撤销失败
		if (respStr.getByteLength() != 148) {
			MessageBox.alert("告警提示", "银联POS"+transTypeStr+"失败！POS机返回数据 错误！");
			return;
		}
		MessageBox.alert("错误提示", "银联POS"+transTypeStr+"失败！", function(){
			$.ajax.submit(null, "recordPosLog", param, null,function(data){
				$.endPageLoading();
			},function(code, info, detail){
				$.endPageLoading();
			});
			
		}, null, "错误码：" + respData.get("RESP") + "<br/>错误信息：" + respData.get("RESP_INFO"));
	}
}