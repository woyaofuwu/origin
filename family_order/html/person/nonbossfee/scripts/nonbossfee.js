$(document).ready(function(){
	 
	$("#FEE_AMOUNT").bind("click", statAmount);
	$("#addFeeBtn").bind("click", verifyNonBossFee)
	$("#clearListBtn").bind("click", clearFeelist);
	//$("#PAY_NAME").bind("change", popupPayNamList);
	//$("#PayNameRemarkBtn").bind("click", popupPayNamList);
	$("#SearchBtn").bind("click", queryNonBossFeeLog);
	$("#CancelBtn").bind("click", cancelNonBossFeeLog);
	$("#PrintTicketBtn").bind("click", rePrintNonBossFee);
	$("#resetBtn").bind("click", resetButton); 
	$("#FEE_PAY_MODE").val("3"); 
	if($("#COST_CENTER").val()!=""&&$("#COST_CENTER").val()!='HNSJ'){
		$("#COST_CENTER").attr("disabled",true);
	}
});
function reset(){
	$("#cond_FEE_NAME").val("");
	$("#cond_COST_CENTER").val("");
	$("#cond_TAX_TYPE").val("");
	$("#cond_PAY_NAME").val("");
	$("#cond_INVOICE_PRINT").val("");
	$("#cond_START_DATE").val("");
	$("#cond_END_DATE").val("");
	$("#cond_FUZZY_QUERY").val("");
	$("#cond_FUZZY_COMMENT").val("");
}

function resetButton(){ 
	$("#TAX_TYPE").attr("disabled",false);
	$("#TAX_RATE").attr("disabled",false);
	$("#INVOICE_TYPE").attr("disabled",false);
	$("#FEE_CHECKBOX").attr("disabled",false);
	$("#TAX_CHECKBOX").attr("disabled",false);
	$("#TAX_TYPE").val("");
	$("#TAX_RATE").val("");
	$("#INVOICE_TYPE").val("");
}

function statAmount(){
	var feeNum=$.trim($("#FEE_NUM").val());
	var feePrice=$.trim($("#FEE_PRICE").val());
	if(feeNum=="" || feePrice==""){		//单位或数量其中有一个为空就不再做等式校验
		return;
	}
	//当等式不成立时再做有效性校验
	if(!$.isNumeric(feeNum)) {
		alert("输入数量不合法！");
		return;
	}
	if(!$.isNumeric(feePrice)) {
		alert("输入单价不合法！");
		return;
	}
	feeNum=Math.round(feeNum);
	feePrice=Math.round(feePrice*100)/100
	$("#FEE_NUM").val(feeNum);
	$("#FEE_PRICE").val(feePrice);
	var amount = parseFloat(feeNum)*parseFloat(feePrice);
	if(amount!= 0){
		$("#FEE_AMOUNT").val(Math.round(amount*100)/100);
	}

}

function verifyNonBossFee(){
	var feeName=$("#FEE_NAME").val();
	if(!feeName || feeName==""){
		alert("请选择项目名称!");
		return false;
	} 
	if($("#FEE").val()==""||$("#FEE_NOTAX").val()==""||$("#TAX_PRICE").val()==""){
		alert("金额、不含税价款不允许为空，请输入并计算税款后再保存！");
		return false;
	}
	if(!$.validate.verifyAll("AddFeeListPart"))
	{ 
		return false;
	}
	addFeeList();
}

/**
*当输入的数量单价的时候,校验金额是否相符
*/
function checkAmount(){
	var feeNum=$.trim($("#FEE_NUM").val());
	var feePrice=$.trim($("#FEE_PRICE").val());
	var amount=$.trim($("#FEE_AMOUNT").val());

	if(amount==""){
		alert('金额不能为空');
		return false;
	}
	if (!$.isNumeric(amount)) {
		alert("输入金额不合法！");
		return false;
	}
	if(feeNum=="" || feePrice==""){		//单位或数量其中有一个为空就不再做等式校验
		return true;
	}else{
		//当等式不成立时再做有效性校验
		if(!$.isNumeric(feeNum)) {
			alert("输入数量不合法！");
			return false;
		}
		if(!$.isNumeric(feePrice)) {
			alert("输入单价不合法！");
			return false;
		}
		feeNum=Math.round(feeNum);
		feePrice=Math.round(feePrice*100)/100
		$("#FEE_NUM").val(feeNum);
		$("#FEE_PRICE").val(feePrice);
		if(parseFloat(feeNum)*parseFloat(feePrice) != parseFloat(amount)){
			alert("单价的数量乘积与金额不符!");
			return false;
		}
	}
	return true;
}

var feeIdx=0;
var feeList=$.DatasetList();
var feeTotal=0;
function addFeeList() {
	/*
	if (feeAmount == "") {
		return false;
	}

	
	var fee = parseFloat(feeAmount)*100;
	fee=Math.round(fee);
	*/
	var fee = $("#FEE").val();
	var feeCode=$("#FEE_NAME").val();
	var feeObj = $.DataMap();
	feeObj.put("FEE_IDX", feeIdx);
	feeObj.put("FEE_NAME", feeCode); 
	var FEE_NAME_DESC=$("#FEE_NAME").find("option[value="+feeCode+"]").text();
	FEE_NAME_DESC=FEE_NAME_DESC.substring(FEE_NAME_DESC.indexOf("|")+1);
	feeObj.put("FEE_NAME_DESC", FEE_NAME_DESC);
	feeObj.put("COST_CENTER", $("#COST_CENTER").val());
	var COST_CENTER_DESC=$("#COST_CENTER").find("option[value="+$("#COST_CENTER").val()+"]").text(); 
	COST_CENTER_DESC=COST_CENTER_DESC.substring(COST_CENTER_DESC.indexOf("|")+1);
	feeObj.put("COST_CENTER_DESC", COST_CENTER_DESC);
	feeObj.put("INVOICE_TYPE", $("#INVOICE_TYPE").val());
	var INVOICE_TYPE_DESC=$("#INVOICE_TYPE").find("option[value="+$("#INVOICE_TYPE").val()+"]").text();
	INVOICE_TYPE_DESC=INVOICE_TYPE_DESC.substring(INVOICE_TYPE_DESC.indexOf("|")+1);
	feeObj.put("INVOICE_TYPE_DESC", INVOICE_TYPE_DESC);
	feeObj.put("TAX_TYPE", $("#TAX_TYPE").val());
	var TAX_TYPE_DESC=$("#TAX_TYPE").find("option[value="+$("#TAX_TYPE").val()+"]").text();
	TAX_TYPE_DESC=TAX_TYPE_DESC.substring(TAX_TYPE_DESC.indexOf("|")+1);
	feeObj.put("TAX_TYPE_DESC", TAX_TYPE_DESC);
	feeObj.put("TAX_RATE", $("#TAX_RATE").val());
	feeObj.put("FEE", fee);
	feeObj.put("FEE_NOTAX", $("#FEE_NOTAX").val());
	feeObj.put("TAX_PRICE", $("#TAX_PRICE").val());
	feeObj.put("FEE_REMARK", $("#FEE_REMARK").val());
	feeObj.put("ADD_REMARK", $("#ADD_REMARK").val());
	
	feeObj.put("PAY_NAME_REMARK", $("#PAY_NAME_REMARK").val());
	var PAY_NAME_REMARK_DESC=$("#PAY_NAME_REMARK").find("option[value="+$("#PAY_NAME_REMARK").val()+"]").text(); 
	PAY_NAME_REMARK_DESC=PAY_NAME_REMARK_DESC.substring(PAY_NAME_REMARK_DESC.indexOf("|")+1);
	feeObj.put("PAY_NAME_REMARK_DESC", PAY_NAME_REMARK_DESC);
	feeObj.put("PAY_NAME", $("#PAY_NAME").val());
	var PAY_NAME_DESC=$("#PAY_NAME").find("option[value="+$("#PAY_NAME").val()+"]").text(); 
	PAY_NAME_DESC=PAY_NAME_DESC.substring(PAY_NAME_DESC.indexOf("|")+1);
	feeObj.put("PAY_NAME_DESC", PAY_NAME_DESC);
	feeObj.put("FEE_PAY_MODE", $("#FEE_PAY_MODE").val());
	var FEE_PAY_MODE_DESC=$("#FEE_PAY_MODE").find("option[value="+$("#FEE_PAY_MODE").val()+"]").text(); 
	FEE_PAY_MODE_DESC=FEE_PAY_MODE_DESC.substring(FEE_PAY_MODE_DESC.indexOf("|")+1);
	feeObj.put("FEE_PAY_MODE_DESC", FEE_PAY_MODE_DESC);
	
	if($("#ONLY_PRINT").attr("checked")==true){
		feeObj.put("ONLY_PRINT", "√");
	}else{
		feeObj.put("ONLY_PRINT", "");
	}
	
	feeTotal += parseFloat(fee); 
	feeList.add(feeObj);
	feeIdx++;
	
	renderFeeList(feeObj);
}

function renderFeeList(feeObj){
	var curIdx=$("#FeeListPart tr").length;
	var feeArr = [];
	feeArr.push('<tr feeIdx="'+feeObj.get("FEE_IDX")+'">');
	feeArr.push('<td name="SEQ" class="e_center">'+curIdx+'</td>');
	feeArr.push('<td>'+feeObj.get("FEE_NAME_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("COST_CENTER_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("INVOICE_TYPE_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("TAX_TYPE_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("TAX_RATE")+'</td>');
	feeArr.push('<td>'+feeObj.get("FEE")+'</td>');
	feeArr.push('<td>'+feeObj.get("FEE_NOTAX")+'</td>');
	feeArr.push('<td>'+feeObj.get("TAX_PRICE")+'</td>');
	
	feeArr.push('<td>'+feeObj.get("PAY_NAME_REMARK_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("PAY_NAME_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("FEE_PAY_MODE_DESC")+'</td>');
	feeArr.push('<td>'+feeObj.get("ONLY_PRINT")+'</td>');
	
	feeArr.push('<td>'+feeObj.get("FEE_REMARK")+'</td>');
	feeArr.push('<td>'+feeObj.get("ADD_REMARK")+'</td>');
	feeArr.push('<td class="e_center"><a href="javascript:void(0)" onclick="removeFee(this)">删除</a></td>');
	feeArr.push('</tr>');
	$("#FeeListPart").append(feeArr.join(""));
	$("#FEE_TOTAL").val(feeTotal);
	feeArr=null;
}

function removeFee(obj){
	var feeTr=$(obj).parent().parent();
	var feeIdx = feeTr.attr("feeIdx");
	feeTr.remove();
	feeList.each(function(item,index,totalcount){
		if(item.get("FEE_IDX") == feeIdx){
			feeTotal -= item.get("FEE");
			feeList.removeAt(index);
			return false;
		}
	});
	$("#FeeListPart tr").each(function(i, el){
		$(el).find("td[name=SEQ]").html(i);
	});
	$("#FEE_TOTAL").val(feeTotal);
}

function clearFeelist(){
	feeTotal=0;
	feeIdx=0;
	feeList=$.DatasetList();
	$("#FeeListPart").empty();
	$("#FEE_TOTAL").val(feeTotal);
}

function setPayNameRemark(){
	
}
/*
function popupPayNamList(){
	var payName=$("#PAY_NAME").val();
	if(!payName || payName==""){
		alert("请选择用户类型！");
		return;
	}
	//弹出窗口
	$.popupPage("nonbossfee.PayNameList", "queryPayNameRemark", "&PAY_NAME="+payName, "用户全称列表", "350", "250", "PAY_NAME_REMARK");
}
*/ 

function submitBeforeCheck(){
	//if(!$.validate.verifyAll($("#PayMoneyPart")[0])){
	//	return false;
	//}
	if(feeList.length <1){
		alert("列表无数据，请编辑并点击\"暂存\"按钮保存数据到列表中再提交！");
		return false;
	} 
	if(!confirm("确定提交吗?")){ 
		return false;
	}
	 
	var feeInfo="&FEE_INFO="+feeList.toString();
	feeInfo=feeInfo.replace(/%/g,"%25"); 
	$.cssubmit.addParam(feeInfo);
	/*
	$.cssubmit.bindCallBackEvent(function(data){
		$.printMgr.bindPrintEvent(printNonBossFeeTrade);
		$.cssubmit.showMessage("success", "业务受理成功", "请点击【打印】按钮打印发票！", true);
	});
	*/
	return true;
	 
}

/**
 * 作废按钮
 * */
function cencelInvoice(cenType){
	 
	var logObj=$("#NonBossFeeResultPart").find("input[name=CHECKBOX_LIST]:checked");
	var typeName="";
	var tiType = "";
	if(!logObj || !logObj.length){
		if(cenType=="ZF"){
			alert("请选择需要做废发票的记录！");
		} 
		return;
	}else if(logObj.length>1){
		alert("只允许选择单条记录，如果存在合打票会一并作废！");
		return;
	}
	var printTag,operMon,printTime,operDate;
	var logIds="";
	var isPrintTag=false;
	var isZf=true; 
	var ticketId="";
	var PAY_NAME_DES="";
	var toDay=new Date();
	var toMonth=toDay.getMonth()+1;
	var s=$.DatasetList();
	logObj.each(function(idx, el){
		logId=$(this).val();
		printTag=$(this).attr("printTag");
		operMon=$(this).attr("operMon");
		printTime=$(this).attr("printTime");
		operDate=$(this).attr("operDate"); 
		ticketId=$(this).attr("ticketId"); 
		PAY_NAME_DES=$(this).attr("payNameDes");
		tiType=$(this).attr("ticketType"); 
		if(tiType=="0"){
			ticketId="非增值税票无票号记录";
		}
		if(printTag!="1"){
			logIds=logIds+logId+",";
			isPrintTag=true;
			return false;
		}else{
			var printMon=printTime.substring(6,7);
			
			if(cenType=="ZF"){
				typeName="作废"; 
				if(toMonth!=printMon){
					isZf=false;
					return false;
				}
			} 
		}
		var m=$.DataMap();
		m.put("LOG_ID", logId);
		m.put("TRADE_ID", $(this).attr("tradeId"));
		m.put("TRADE_STAFF_ID", $(this).attr("staffId"));
		m.put("CENCEL_TYPE",cenType); 
		m.put("PAY_NAME_DES",PAY_NAME_DES);
		m.put("TI_TYPE",tiType); 
		s.add(m);
	});
	
	if(isPrintTag){
		alert("【流水号："+logId+"】的记录打印状态不是“已打印”，不允许作废或者冲红！");
		return;
	}
	if(!isZf){
		alert("【流水号："+logId+"】的打票月份不在当前月份，不允许作废！请冲红！");
		return;
	} 
//	if(window.confirm("确认【"+typeName+"】以下票号："+ticketId)==true){
	if(window.confirm("确认是否【"+typeName+"】？")==true){
		$.beginPageLoading("开始作废。。。");
		$.ajax.submit("", "cencelInvoice", "&NON_BOSS_INFO="+s.toString(), "", 
			function(cencelDataset){ 
				$.endPageLoading(); 
				if(tiType=="0"){
					$.showSucMessage("【非增值税发票】作废数据标记成功，请到税务系统做相关的操作。","",'NonBossFeeResultPart');
				}else{
					$.showSucMessage("【增值税发票】作废成功，请到税务系统做相关的操作。。","",'NonBossFeeResultPart');
				}
				queryNonBossFeeLog();
			},
			function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","加载作废数据错误！", null, null, info, detail);
			},function(){
				$.endPageLoading();
				MessageBox.alert("告警提示","加载作废数据超时！");
		});	
	}
}

/**
 * 冲红按钮
 * */
function cencelInvoiceCH(cenType){ 
	var logObj=$("#NonBossFeeResultPart").find("input[name=CHECKBOX_LIST]:checked");
	var typeName="";
	var tiType = "";
	if(!logObj || !logObj.length){
		alert("请选择需要冲红发票的记录！"); 
		return;
	}else if(logObj.length>1){
		alert("只允许选择单条记录，如果存在合打票会一并冲红处理！");
		return;
	}
	var printTag,operMon,printTime,operDate;
	var logIds="";
	var isPrintTag=false; 
	var isCh=false;
	var ticketId=""; 
	var s=$.DatasetList();
	var toDay=new Date();
	var toMonth=toDay.getMonth()+1;
	logObj.each(function(idx, el){
		logId=$(this).val();
		printTag=$(this).attr("printTag");
		operMon=$(this).attr("operMon");
		printTime=$(this).attr("printTime");
		operDate=$(this).attr("operDate"); 
		ticketId=$(this).attr("ticketId");  
		tiType=$(this).attr("ticketType"); 
		if(tiType=="0"){
			ticketId="非增值税票无票号记录";
		}
		if(printTag!="1"){
			logIds=logIds+logId+",";
			isPrintTag=true;
			return false;
		}else{
			var printMon=printTime.substring(6,7);
			typeName="冲红";
			if(toMonth==printMon){
				isCh=true;
				return false;
			}
			
		}
		var m=$.DataMap();
		m.put("LOG_ID", logId);
		m.put("TRADE_ID", $(this).attr("tradeId"));
		m.put("TRADE_STAFF_ID", $(this).attr("staffId"));
		m.put("CENCEL_TYPE",cenType);  
		m.put("TI_TYPE",tiType);  
		s.add(m);
	});
	
	if(isPrintTag){
		alert("【流水号："+logId+"】的记录打印状态不是“已打印”，不允许作废或者冲红！");
		return;
	} 
	if(isCh){
		alert("【流水号："+logId+"】的打票月份为当前月，不允许冲红！请作废！");
		return;
	}
//	if(window.confirm("确认【"+typeName+"】以下票号："+ticketId)==true){
	if(window.confirm("确认是否【"+typeName+"】？")==true){
		$.beginPageLoading("开始冲红。。。");
		$.ajax.submit("", "cencelInvoice", "&NON_BOSS_INFO="+s.toString(), "", 
			function(cencelDataset){ 
				$.endPageLoading(); 
				if(tiType=="0"){
					$.showSucMessage("【非增值税发票】冲红数据已经标记，请到税务系统办理相关票的操作。","",'NonBossFeeResultPart');
				}else{
					$.showSucMessage("【增值税发票】冲红数据已经标记，请到税务系统办理相关票的操作。","",'NonBossFeeResultPart');
					
					/**
					MessageBox.confirm("确认提示", "处理成功，是否打印冲红发票？", function(btn){
						if(btn == "ok"){
							chRePrintNonBossFee(cencelDataset);
						}else{
							queryNonBossFeeLog();
						}
					}, null, null);	
					*/
				}
				queryNonBossFeeLog();
			},
			function(code, info, detail){
				$.endPageLoading(); 
				MessageBox.error("错误提示","加载冲红数据错误！", null, null, info, detail);
			},function(){
				$.endPageLoading();
				MessageBox.alert("告警提示","加载冲红数据超时！");
		});	
	}
}

/**
 * 冲红重新打印
 * */
function chRePrintNonBossFee(cencelDataset){ 
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("", "rePrintNonBossFee", "&NON_BOSS_INFO="+cencelDataset.toString(), "", 
		function(printDataset){ 
			$.endPageLoading();
			//设置打印数据
			$.printMgr.setPrintData(printDataset); 
			//启动打印
			$.printMgr.printReceipt();
			//刷新列表
			queryNonBossFeeLog();
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
 * 查询记录打印发票按钮
 */
function rePrintNonBossFee(){
	var logObj=$("#NonBossFeeResultPart").find("input[name=CHECKBOX_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择需要打印发票的记录！");
		return;
	} 
	var s=$.DatasetList();
	var logId,printTag,payName,taxRate,isPrint=false,samePayName=false,refundTag,ifrefund,isSameRate=false;
	var PAY_NAME_DES="";
	var invType = $("#INVOICE_TYPE").val();
	
	if(invType==null||invType==""){
		alert("请选择需要打印发票类型（增值税发票 | 非增值税发票）！");
		$("#INVOICE_TYPE").focus();
		return;
	}
	
	logObj.each(function(idx, el){
		logId=$(this).val();
		printTag=$(this).attr("printTag");
		refundTag=$(this).attr("refundTag");
		 
		if(printTag=="1"){
			isPrint=true;
			return false;
		}
		if(refundTag!="0"){
			ifrefund=true;
			return false;
		}
		
		if(idx==0){
			payName=$(this).attr("payName");
			taxRate=$(this).attr("taxRate");
		} 
		if(payName!=$(this).attr("payName")){
			//alert(payName+"|||"+$(this).attr("payName"));
			samePayName=true;
			return false;
		}else{
			if(invType=="1"&&taxRate!=$(this).attr("taxRate")){
				isSameRate=true;
				return false;
			}else{
				PAY_NAME_DES=$(this).attr("payNameDes");
			}
		}
		
		var m=$.DataMap();
		m.put("LOG_ID", logId);
		m.put("TRADE_STAFF_ID", $(this).attr("staffId"));
		m.put("PAY_NAME_DES",PAY_NAME_DES);
		m.put("INVOICE_TYPE",invType);
		m.put("PAY_NAME",payName);
		s.add(m);
	});
	//如果有已经打印过的记录，则直接返回不执行后续操作
	if(isPrint){
		alert("【流水号："+logId+"】的记录已打印过发票，不能重复打印！");
		return;
	}
	if(samePayName){
		alert("不符合同一用户，无法打印！");
		return;
	} 
	if(isSameRate){
		alert("不符合同一用户且税率相同，无法打印！");
		return;
	}  
	if(ifrefund){
		alert("【流水号："+logId+"】的记录已返销，不允许打印！");
		return;
	}
	if(window.confirm("确认是否【打印】？")==true){
		$.beginPageLoading("加载打印数据。。。");
		$.ajax.submit("", "rePrintNonBossFee", "&NON_BOSS_INFO="+s.toString(), "", 
		function(printDataset){ 
			$.endPageLoading();
			/**
			 * 暂时屏蔽，2015-06-15
			if(printDataset!=null&&printDataset.length > 0){
				//设置打印数据
				$.printMgr.setPrintData(printDataset); 
				//启动打印
				$.printMgr.printReceipt();
				
				queryNonBossFeeLog();
			}else{
				$.showSucMessage("非增值税发票标记成功!请记得打印发票","");
				queryNonBossFeeLog();
			}
			*/
			var invName="";
			if(invType=="0"){
				invName="非增值税";
			}else{
				invName="增值税";
			}
			
			$.showSucMessage(invName+"发票标记成功!请记得打印发票","");
			queryNonBossFeeLog();
		},
		function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载打印数据错误！", null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示","加载打印数据超时！");
		});	
	}
}

/**
 * 打印发票回调方法
 * @param data
 */
function printNonBossFeeTrade(tradeData){
	var param="&NONBOSSFEE_TRADE_ID="+tradeData.get("PRINT_TRADE_ID");
	param+="&FEE_INFO="+feeList.toString();
	
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("NonBossFeeCondPart,NonBossFeeResultPart", "printNonBossFeeTrade", param, null, 
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

function queryNonBossFeeLog(){
	
	if($("#cond_FUZZY_COMMENT").val()!= "" && $("#cond_FUZZY_QUERY").val() == ""){
		alert("填写了模糊查询内容，需要选择模糊查询条件！");
		$("#cond_FUZZY_QUERY").focus();
		return;
	}
	$.beginPageLoading("查询非出账业务收费补录信息。。。");
	$.ajax.submit("NonBossFeeCondPart,NonBossFeeResultPart", "queryNonBossFeeLog", "", "NonBossFeeResultPart",function(data){
		$.endPageLoading();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","查询非出账业务收费补录信息错误！", null, null, info, detail);
	});	
}

function exportNonBossFeeLog(){
	$.beginPageLoading("导出非出账业务收费补录信息。。。");
	$.ajax.submit("NonBossFeeCondPart,NonBossFeeResultPart", "queryNonBossFeeLog", "", "",function(data){
		$.endPageLoading();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","导出非出账业务收费补录信息错误！", null, null, info, detail);
	});	
}

function checkExport(){
	return confirm("确定导出吗?");
}

function cancelNonBossFeeLog(){
	 
	var logObj=$("#NonBossFeeResultPart").find("input[name=CHECKBOX_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择需要返销的记录！");
		return;
	}
	if(!confirm("确 认 【 返 销 】 ？")){
		return ;
	}else{
		var errNum=0;
		var err=$.DataMap();
		logObj.each(function(){
			//alert($(this).val()+"|||"+$(this).attr("refundTag")+"|||"+$(this).attr("rsrvstr6"));
			var logid=$(this).val();
			var refundtag=$(this).attr("refundTag");
			var ifprint=$(this).attr("printTag");
			if(refundtag!=0 || ifprint=="1"){
				errNum=errNum+1;
				$(this).attr("checked",false);
				err.put("LOG_ID:"+errNum,$(this).val());
			}
		});
		if(errNum>0){
			alert("存在已经返销或者已打印发票的记录，不允许办理返销："+err.toString());
			return;
		}
	}
	var s=$.DatasetList();
	logObj.each(function(){
		var m=$.DataMap();
		m.put("LOG_ID", $(this).val());
		m.put("TRADE_ID", $(this).attr("tradeId"));
		m.put("TRADE_STAFF_ID", $(this).attr("staffId"));
		s.add(m);
	});

	$.beginPageLoading("非出账业务收费补录返销。。。");
	$.ajax.submit("", "cancelNonBossFeeLog", "&NON_BOSS_INFO="+s.toString(), "",function(data){
		$.endPageLoading();
		if(data && data.get("RESULT_CODE")=="0"){
			$.showSucMessage("返销提示","选定业务返销成功!",'NonBossFeeResultPart');
			queryNonBossFeeLog();
		}
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","非出账业务收费补录返销错误！", null, null, info, detail);
	});	
}

function autoselpayname(){
	$.beginPageLoading("获取用户类型。。。");
	var param = '&DATA_NAME='+$("#PAY_NAME_REMARK").val();  
	$.ajax.submit("", "getPayUserName", param, "",function(data){ 
		$.endPageLoading();
		$("#PAY_NAME").val(data.get("PARAM_NAME"));
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取用户类型错误！", null, null, info, detail);
	});	
}

function selectFeeName(){
	$.beginPageLoading("获取费用项目对应关系。。。");
	var param = "&DATA_NAME="+$("#FEE_NAME").val()+"&VALID_FLAG=1";  
	$.ajax.submit("", "getFeeTypeRelation", param, "",function(data){ 
		$("#TAX_TYPE").val(data.get("TAX_TYPE"));
		$("#TAX_RATE").val(data.get("TAX_RATE"));
		$("#INVOICE_TYPE").val(data.get("INVOICE_TYPE"));
		$("#TAX_TYPE").attr("disabled",true);
		$("#TAX_RATE").attr("disabled",true);
		$("#INVOICE_TYPE").attr("disabled",true);
		$("#FEE_REMARK").val(data.get("REMARK"));
		
		$("#FEE_CHECKBOX").attr("checked",false);
		$("#FEE_CHECKBOX").attr("disabled",false);
		if($("#TAX_TYPE").val()=="2"){
			$("#TAX_CHECKBOX").attr("checked",false);
			$("#TAX_CHECKBOX").attr("disabled",true);
		}else{
			$("#TAX_CHECKBOX").attr("checked",false);
			$("#TAX_CHECKBOX").attr("disabled",false);
		}
		$("#FEE").val("");
		$("#FEE_NOTAX").val("");
		$("#TAX_PRICE").val("");
		$.endPageLoading();
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取费用项目对应关系！", null, null, info, detail);
	});	
}


function selectCheckbox(whichone){
	if(whichone=="FEE"){  
		if($("#FEE_CHECKBOX").attr("checked")==true){
			$("#TAX_CHECKBOX").attr("checked",false);
			$("#TAX_CHECKBOX").attr("disabled",true);
			$("#FEE").attr("disabled",false);
			$("#FEE").focus();
		}else{
			$("#FEE").attr("disabled",true);
			if($("#TAX_TYPE").val()!="2"){
				$("#TAX_CHECKBOX").attr("disabled",false);
			}
		}
	}else if(whichone=="NOTAX"){ 
		if($("#TAX_CHECKBOX").attr("checked")==true){ 
			$("#FEE_CHECKBOX").attr("checked",false);
			$("#FEE_CHECKBOX").attr("disabled",true);
			$("#FEE_NOTAX").attr("disabled",false);
			$("#FEE_NOTAX").focus();
		}else{
			$("#FEE_NOTAX").attr("disabled",true);
			$("#FEE_CHECKBOX").attr("disabled",false);
		}
	}
} 

function countPrice(feeType){
	var taxRate=$("#TAX_RATE").val();
	if(feeType=='FEE'){
		//计算不含税价款
		//可录入负值；
		//如《业务参数》应税类型为增值税且税率为3%，则为［“金额”-round（“金额”/（1+3%）×2%，2）］；
		//应税类型为增值税且税率不为3%，则为 “金额”/（1+税率）；
		//否则等于“金额”
		//alert($("#TAX_TYPE").val()+"||||"+Math.round(2.498*100)/100+"|||"+2.443.toFixed(2));
		 
		$("#FEE").val(Math.round($("#FEE").val()*100)/100) ;
		fee=$("#FEE").val();
		 
		if($("#TAX_TYPE").val()=="2"  ){
			$("#FEE_NOTAX").val(Math.round((fee-fee/(1+0.03)*0.02)*100)/100);
		}else if($("#TAX_TYPE").val()=="1"){
			$("#FEE_NOTAX").val(Math.round(fee/(1+taxRate/100)*100)/100);
		}else{
			$("#FEE_NOTAX").val(fee-Math.round((fee*taxRate/100)*100)/100);
		}
		
	}else if(feeType=='FEE_NOTAX'){
		//计算“金额”
		//如《业务参数》应税类型为增值税，则为 “不含税价款”×（1+税率），否则等于“不含税金额”）     
		$("#FEE_NOTAX").val(Math.round($("#FEE_NOTAX").val()*100)/100) 
		var feeNotax=$("#FEE_NOTAX").val();
		if($("#TAX_TYPE").val()=="1"){
			$("#FEE").val(Math.round(feeNotax*(1+taxRate/100)*100)/100);
		}else{
			$("#FEE").val(Math.round(feeNotax/(1-taxRate/100)*100)/100);
		}
	}
	$("#TAX_PRICE").val(Math.round(($("#FEE").val()-$("#FEE_NOTAX").val())*100)/100);
}