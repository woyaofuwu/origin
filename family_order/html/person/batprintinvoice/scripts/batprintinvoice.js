var importLoadData;
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
	    $("#NoPhoneWidePart").css("display", "");
		$("#TradePart").css("display", "none");
		$("#BatchPart").css("display", "none");
		$("#SERIAL_NUMBER").attr("nullable", "no");
		$("#SERIAL_NUMBER").val("");
		$("#BATCH_ID").attr("nullable", "yes");
		$("#BATCH_ID").val("");
		$("#TRADE_ID").attr("nullable", "yes");
		$("#TRADE_ID").val("");
		$("#StaffId").css("display", "");
		$("#StartDate").css("display", "");
		$("#StartDate").attr("nullable", "no");
		$("#EndDate").css("display", "");
		$("#EndDate").attr("nullable", "no");
	}
	if(searchType=="2"){
		$("#SnPart").css("display", "none");
        $("#NoPhoneWidePart").css("display", "none");
		$("#TradePart").css("display", "");
		$("#BatchPart").css("display", "none");
		$("#TRADE_ID").attr("nullable", "no");
		$("#TRADE_ID").val("");	
		$("#SERIAL_NUMBER").attr("nullable", "yes");
		$("#SERIAL_NUMBER").val("");	
		$("#BATCH_ID").attr("nullable", "yes");
		$("#BATCH_ID").val("");
		$("#StaffId").css("display", "");
		$("#StaffId").val("");
		$("#StartDate").css("display", "");
		$("#StartDate").attr("nullable", "no");
		$("#EndDate").css("display", "");
		$("#EndDate").attr("nullable", "no");
	}
	if(searchType=="3"){
		$("#SnPart").css("display", "none");
		$("#TradePart").css("display", "none");
        $("#NoPhoneWidePart").css("display", "none");
		$("#BatchPart").css("display", "");
		$("#SERIAL_NUMBER").attr("nullable", "yes");
		$("#SERIAL_NUMBER").val("");	
		$("#TRADE_ID").attr("nullable", "yes");
		$("#TRADE_ID").val("");
		$("#BATCH_ID").attr("nullable", "no");
		$("#BATCH_ID").val("");
		$("#StaffId").css("display", "none");
		$("#StartDate").css("display", "none");
		$("#StartDate").attr("nullable", "yes");
		$("#EndDate").css("display", "none");
		$("#EndDate").attr("nullable", "yes");
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

//免填单批量打印操作
function printTrade(){
	if(!chkSerialNumber()){
		return ;
	}

	var prtTradeObj = $("input[name=TRADE_ID]:checked");
	if(!prtTradeObj || prtTradeObj.length==0){
		alert("请选择需要批量打印免填单的业务！");
		return;
	}
	var arr=[];
	prtTradeObj.each(function(){
		arr.push($(this).val());
	});
	$("#PRINT_TRADE_ID").val(arr.join(","));
	
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

//电子工单补打
function printEdocInfo(){	
	var searchType=$("#SEARCH_TYPE").val();
	//REQ201811130004优化物联网卡相关界面及功能——BOSS侧
	//根据批次号补打工单 
	if(searchType=="3"){		
		var prtTradeObj = $("input[name=TRADE_ID]:checked");
		if(!prtTradeObj || prtTradeObj.length!=1){
			alert("请选择需要补打电子工单的业务！");
			return;
		}
		var batchId = prtTradeObj.val();
		var param = "&BATCH_ID=" + batchId;
		$.ajax.submit(null,'importData',param,'',function(data){
				printEDoc(data);
		    	$.endPageLoading();
		}, function(error_code, error_info) {
	        $.endPageLoading();
	        alert(error_info);
	    });
	}else{
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
	        printInfo.put("USER_ID", notePrintData.get("USER_ID"));
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
}

//REQ201811130004优化物联网卡相关界面及功能——BOSS侧 wuhao5
function printEDoc(data){
    var detail="";     
    detail +="##"+"受理工号:"+data.get("TRADE_STAFF_ID", "");
    detail +="##"+"受理业务时间:"+data.get("ACCEPT_DATE", "");
    detail +="##"+"受理业务类型:"+data.get("TRADE_TYPE_NAME", "");
		
	var edocXml=[];
	edocXml.push('<?xml version="1.0" encoding="utf-8" ?>');
	edocXml.push('<IN>');
	edocXml.push('	<op_code>'+"10"+'</op_code>');
	edocXml.push('	<billid>'+data.get("BATCH_ID","")+'</billid>');//批次号
	
	edocXml.push('	<work_name>'+data.get("TRADE_STAFF_NAME", "")+'</work_name>');//工号
	edocXml.push('	<work_no>'+data.get("TRADE_STAFF_ID", "")+'</work_no>');
	edocXml.push('	<org_info>'+data.get("ORG_INFO", "")+'</org_info>');//部门编码(组织机构编码)
	edocXml.push('	<org_name>'+data.get("ORG_NAME", "")+'</org_name>');//部门名称				
	edocXml.push('	<phone>'+data.get("BATCH_ID","")+'</phone>');
	edocXml.push('	<serv_id>'+data.get("USER_ID", "")+'</serv_id>');
	edocXml.push('	<op_time>'+data.get("ACCEPT_DATE", "")+'</op_time>');//受理时间
	edocXml.push('	<batsup_tag>'+"1"+'</batsup_tag>');//批量业务补打标识
	/*获取照片标志信息*/
	//摄像标识
	edocXml.push('	<pic_tag>'+data.get("PIC_ID", "")+'</pic_tag>');
	
	edocXml.push('	<busi_list>');
	edocXml.push('		<busi_info>');
	edocXml.push('			<op_code>'+"10"+'</op_code>');//业务类型
	edocXml.push('			<sys_accept>'+data.get("BATCH_ID","")+'</sys_accept>');//批次号
	edocXml.push('			<busi_detail>'+detail+'</busi_detail>');
	edocXml.push('		</busi_info>');
	edocXml.push('	</busi_list>');

	edocXml.push('	<verify_mode>'+data.get("VERIFY_MODE", "")+'</verify_mode>');
	edocXml.push('	<id_card>'+data.get("ID_CARD","")+'</id_card>');
	edocXml.push('	<cust_name>'+data.get("CUST_NAME","")+'</cust_name>');
	edocXml.push('	<brand_name>'+data.get("PRODUCT_NAME","")+'</brand_name>');
	edocXml.push('	<copy_flag></copy_flag>');
	edocXml.push('	<agm_flag></agm_flag>');

	edocXml.push('</IN>');
	console.log(edocXml);
	var edocStr = edocXml.join("");
    
	//启动电子工单打印
	MakeBillActiveX.MainBuildBill(edocStr);
	//$.printMgr.setElcNoteData(edocStr);
	//$.printMgr.getElecAcceptBill(edocStr);
	edocStr=null;
	detail=null;
	edocXml=null;
	$.printMgr.edocPrintLock=false;		//关闭打印锁
	$.endPageLoading();	
	
	var param = "&BATCH_ID=" + data.get("BATCH_ID","");
	$.ajax.submit(null,'getTradeBatPicIdSyn',param,'',function(){
    	$.endPageLoading();
	}, function(error_code, error_info) {
	    $.endPageLoading();
	    alert(error_info);
	});
}
