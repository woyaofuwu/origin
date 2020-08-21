//截取员工号码
function inputCtrlForStaff(obj){
	var staffId=$("#STAFF_ID").val();
	var staffId = $.trim(staffId);
	if(staffId.length>8){
		staffId = staffId.substring(0,8);
	}
	$("#STAFF_ID").val(staffId.toUpperCase());
} 

function chkInput(makeData){
	var sn = $("#SERIAL_NUMBER").val();
 	var start = $("#START_DATE").val();
 	var end = $("#END_DATE").val();
 	if (!$.validate.verifyAll("SearchCondPart")){
		return false; 
	}
 	var day=makeData?1:31;
	//校验起始日期范围
	if(dateDiff(start, end)>day){
		alert("开始时间和结束时间跨度不能超过"+day+"天");
		return false;
	}
	return true;
}

//点击查询确认事件
function chkSearchForm(){
	//校验起始日期范围
	if(!chkInput()){
		return;
	}
	
	$.beginPageLoading("查询打印业务数据。。。");
	$.ajax.submit("SearchCondPart", "queryPrintInfo", null, "SearchResultPart",function(data){
		$.endPageLoading();
		if(!$("#printTable tbody tr") || $("#printTable tbody tr").length==0){
			$("#tipInfo").html("获取批量免填单打印信息无数据!");
			$("#tipInfo").css("display", "");
		}else{
			$("#tipInfo").css("display", "none");
		}
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","查询打印业务数据错误！", null, null, info, detail);
	});
}

function changeQueryCond(){
	var searchType=$("#SEARCH_TYPE").val();
	if(searchType=="1"){
		$("#SnPart").css("display", "");
		$("#TradePart").css("display", "none");
		$("#SERIAL_NUMBER").attr("nullable", "no");
		$("#TRADE_ID").attr("nullable", "yes");
		$("#TRADE_ID").val("");
	}else{
		$("#SnPart").css("display", "none");
		$("#TradePart").css("display", "");
		$("#SERIAL_NUMBER").attr("nullable", "yes");
		$("#TRADE_ID").attr("nullable", "no");
		$("#SERIAL_NUMBER").val("");
	}
}

function makeEdocInfo(){
	if(!chkInput(true)){
		return false;
	}
	$.beginPageLoading("生成电子工单数据。。。");
	$.ajax.submit("SearchCondPart", "createPrintInfo", null, "SearchResultPart",function(data){
		$.endPageLoading();
		if(data && data.get("RESULT_CODE") == "1"){
			MessageBox.alert("告警提示","没有找到可生成的电子工单记录！");
		}
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","生成电子工单数据错误！", null, null, info, detail);
	});
}

function makePrintInfo(){
	if(!chkInput(true)){
		return false;
	}
	$.beginPageLoading("生成免填单数据。。。");
	$.ajax.submit("SearchCondPart", "createPrintInfo", null, "SearchResultPart",function(data){
		$.endPageLoading();
		if(data && data.get("RESULT_CODE") == "1"){
			MessageBox.alert("告警提示","没有找到可生成的免填单记录！");
		}
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","生成免填单数据错误！", null, null, info, detail);
	});
}

function dateDiff(start,end){
	var day = 0;
	var startDt = new Date(Date.parse(start.replace(/-/g,   "/")));
	var endDt = new Date(Date.parse(end.replace(/-/g,   "/")));
	day = (endDt.getTime()-startDt.getTime()) / (1000*60*60*24) ;
	return day;
}

function checkExport(){
	if(!chkInput()){
		return false;
	}
	if(!chkSerialNumber()){
		return ;
	}
	return confirm("确定导出免填单批量打印数据吗?");
}

function chkSerialNumber(){
	var sn=$.trim($("#SERIAL_NUMBER").val());
	var curSn = $.trim($("#CUR_SERIAL_NUMBER").val());

	if(curSn == "") curSn=sn;

	if( curSn != sn){
		alert("输入的服务号码与查询到的服务号码信息不一致，请重新查询打印数据！");
		return false;
	}
	return true;
}

function printBefore() {
	if(!chkSerialNumber()){
		return false;
	}

/*	var prtTradeObj = $("input[name=TRADE_ID]:checked");
	if(!prtTradeObj || prtTradeObj.length==0){
		alert("请选择需要批量打印免填单的业务！");
		return;
	}
	var arr=[];
	prtTradeObj.each(function(){
		arr.push($(this).val());
	});*/
	
	
	var arr=[];
	var check = false;
	var checkvalues = $("input[name=checkvalue]");
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			var checkArr  = checkvalues[i].value.split(",");
			var tradeId = checkArr[0];
			var isPrinted = checkArr[1];
			//arr.push(checkvalues[i].value);
			arr.push(tradeId);
			$("#PRINTED_TAG").val(isPrinted);			
			break;
		}
	}
	if(!check){
		alert("请先选择记录后再打印!");
		return false;
	}	

	$("#PRINT_TRADE_ID").val(arr.join(","));
	return true;
}
//免填单批量打印操作
function printTrade(){
	if (!printBefore()) {
		return;
	}	
	
	$.beginPageLoading("获取打印数据。。。");
	$.ajax.submit("SearchCondPart", "printTrade", null, null,function(printInfos){
		$.endPageLoading();
		$.printMgr.setPrintData(printInfos);
		$.printMgr.printReceipt();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取打印数据错误！", null, null, info, detail);
	});
}

/*
 * 发票打印后的相关动作
 */
function printreceipt(){
	if (!printBefore()) {
		return;
	}	
	if ("1"==$("#PRINTED_TAG").val()) {
		alert("该发票已打印，请选择其他流水打印发票！");
		return;
	}

	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("SearchCondPart", "printReceiptTrade", null, null, 
		function(printDataset){
			$.endPageLoading();
			//设置打印数据			
			$.printMgr.setPrintData(printDataset);
			//启动打印前，判断一把确保该发票之前没打印过
			if (chkInvoicePrinted()) {
				//启动打印
				$.printMgr.printReceipt();
			}
		},
		function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载打印数据错误！", null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示","加载打印数据超时！");
	});
	
	return true;
}

//电子工单补打
function printEdocInfo(){
	if(!chkSerialNumber()){
		return ;
	}

	var prtTradeObj = $("input[name=TRADE_ID]:checked");
	if(!prtTradeObj || prtTradeObj.length!=1){
		alert("请选择需要补打电子工单的业务！");
		return;
	}
	var tradeId=prtTradeObj.val();
	$.beginPageLoading("获取打印数据。。。");
	$.ajax.submit("", "printEdoc", "&TRADE_ID="+tradeId, null,function(data){
		$.endPageLoading();
		
		if(data.get("RESULT_CODE")!="0"){
			MessageBox.alert("告警提示","没有找到电子工单数据！");
			return;
		}
		var notePrintData=data.get("CNOTE_DATA");
		
		if(!notePrintData || !notePrintData.length){
			alert("没有找到电子工单数据！");
			return;
		}
		//由于历史原因，系统换行符号以~区分，而电子工单需要两个 
		notePrintData.put("RECEIPT_INFO1", (notePrintData.get("RECEIPT_INFO1","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO2", (notePrintData.get("RECEIPT_INFO2","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO3", (notePrintData.get("RECEIPT_INFO3","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO4", (notePrintData.get("RECEIPT_INFO4","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO5", (notePrintData.get("RECEIPT_INFO5","")).replace(/\~/g,"~~"));

		$.printMgr.params.put("EPARCHY_CODE", $("#TRADE_EPARCHY_CODE").val());
		
		var printInfos=$.DatasetList();
		var printInfo=$.DataMap();
		printInfo.put("TRADE_ID", tradeId);
		printInfo.put("TYPE", "P0003");
		printInfo.put("EPARCHY_CODE", $("#TRADE_EPARCHY_CODE").val());
		printInfos.add(printInfo);
		
		//设置打印数据，设置成标准打印格式，以期后面整个走打印流程
		$.printMgr.setPrintData(printInfos);
		
		//启动电子工单打印
		$.printMgr.setElcNoteData(notePrintData);
		$.printMgr.getElecAcceptBill(notePrintData);
		
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取打印数据错误！", null, null, info, detail);
	});
}

function chkInvoicePrinted(){
	var infos = $.printMgr.getPrintData();
	if(!infos || infos.length == 0){
		MessageBox.alert("告警提示", "系统检索不到打印数据！");
		return false;
	}
	
	infos.each(function(info, idx, total){
		//判断是否已经打印，如果已经打印，则不打印
		if(info.containsKey("IS_INVOICE_PRINTED") && info.get("IS_INVOICE_PRINTED")=="1"){
			alert("该发票已打印，请选择其他流水打印发票！");
			return false;
		}		
	});
	return true;
}
