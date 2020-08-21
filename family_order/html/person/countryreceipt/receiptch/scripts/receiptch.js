
function chreceipt(){
	var check = false;
	var checkvalues = $("input[name=checkvalue]");
	for(var i=0;i<checkvalues.length;++i){
		if(checkvalues[i].checked == true){
			check = true;
			break;
		}
	}
	
	if(!check){
		alert("请先选择记录再冲红发票!");
		return false;
	}
	
	$.cssubmit.bindCallBackEvent(function(data){
		$.printMgr.bindPrintEvent(printTrade);
		$.cssubmit.showMessage("success", "处理成功", "请点击【打印】按钮打印冲红发票！", true);
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

function queryCHReceipt(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	//校验起始日期范围
	if($("#cond_SERIAL_NUMBER").val() == "" && $("#cond_TRADE_ID").val() == ""){
		alert("服务号码和业务流水号不能都为空!");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryCHReceipt', null, 'result_Table', function(data){
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