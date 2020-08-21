/*
 * 发票打印后的相关动作
 */
function printreceipt(){
	var check = false;
	var checkvalues = $("input[name=checkvalue]");
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			break;
		}
	}
	
	if(!check){
		alert("请先选择记录后再打印发票!");
		return false;
	}
	
	$.cssubmit.bindCallBackEvent(function(data){
		$.printMgr.bindPrintEvent(printTrade);
		$.cssubmit.showMessage("success", "处理成功", "请点击【打印】按钮补打发票！", true);
	});
	
	return true;
}

/**
 * 打印发票回调方法
 * @param data
 */
function printTrade(tradeData){
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("result_Table", "printTrade", null, null, 
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

var needCheckFlag = true;

function queryTradeReceipt(){
	//查询条件校验
	if($("#cond_SERIAL_NUMBER").val() == "" && $("#cond_TRADE_ID").val() == ""){
		alert("服务号码和业务流水号不能都为空!");
		return false;
	}
	
	if($("#cond_ACCEPT_TIME").val() == ""){
		alert("受理时间不能为空!");
		return false;
	}
	
	if($("#cond_SERIAL_NUMBER").val() != "" && needCheckFlag){
		alert("手机号码必须通过验证后才能进行查询操作!");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryTradeReceipt', null, 'result_Table', function(data){
			if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO').length > 0){
				MessageBox.alert("提示",data.get('ALERT_INFO'));
			}
			
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}

/**
 * 手机号码验证
 */
function checkSerailNumber(){
	var sn = $("#cond_SERIAL_NUMBER").val();
	if(sn==null || sn==""){
		alert("请输入服务号码！");
		return false;
	}

	$.userCheck.checkUser("cond_SERIAL_NUMBER");//身份校验
}

function setNeedCheck(){
	needCheckFlag = true;
}

function checkFinish(){
	needCheckFlag = false;
}