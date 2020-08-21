/**
 * 自助终端资料查询
 */
function queryTerminals() {
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('QueryCondPart,navt', 'queryTerminals', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 自助终端资料新增
 */
function addTerminal() {
	reset();
	$.popupDiv("DealInfoPart","","新增自助终端设备");
}

/**
 * 自助终端资料删除
 */
function delTerminal() {
	var data = $.table.get("TerminalInfoTable").getCheckedRowDatas();
	
	if(data.length == 0) {
		MessageBox.alert('提示信息', "请选择需要删除的自助终端！");
		return;
	}
	
	for(var i = 0,size = data.length; i < size; i++){
		var removeTag = data.get(i).get(0);
		if(removeTag == "注销"){
			MessageBox.alert('提示信息', "已经注销的终端不能被删除！");
			return;
		}
	}
	
	MessageBox.confirm("提示信息","确定要删除吗？",function(btn){
		if(btn == "ok") {
			var data = getUnionOrderCheckStr();
			$.beginPageLoading("删除中...");
		    $.ajax.submit('QueryCondPart', 'deleteTerminal', 'DEAL_LIST='+data, '', function() {
		    	$.endPageLoading();
		    	MessageBox.alert('提示信息', '删除成功！',function(){
		    		queryTerminals();
		    	});
		    }, function(error_code, error_info) {
		        $.endPageLoading();
		        alert(error_info);
		    });
		}
	});
}

/**
 * 保存自助终端
 */
function saveTerminal() {
	if($.validate.verifyAll("DealInfoPart")){
		var data = getUnionOrderCheckStr();
		var deviceNumber = $('#cond_DEVICE_NUMBER').val();
		
		$.beginPageLoading("保存中...");
	    $.ajax.submit('DealInfoPart', 'saveTerminal', '', '', function() {
	    	$.endPageLoading();
	    	MessageBox.alert('提示信息', '新增成功！设备资产编码：['+deviceNumber+']',function(){
	    		$.closePopupDiv("DealInfoPart");
	    	});
	    }, function(error_code, error_info) {
	        $.endPageLoading();
	        alert(error_info);
	    });
	}
}

/**
 * 获取已选中的终端信息
 */
function getUnionOrderCheckStr(){
	var s = document.getElementsByName("terminalList");
	var s2 = "";
	for( var i = 0; i < s.length; i++ ){
		if (s[i].checked){
			s2 += s[i].value+";";
		}
	}
	s2 = s2.substr(0,s2.length-1);
	return s2;
}

function reset(){
	resetArea("DealInfoPart",true);
}

//改变查询条件
function changeQueryCondition(){
	var queryMode = $("#cond_QUERY_MODE").val();
	
	//如果是渠道编码
	if(queryMode == "0"){
		$("#DEPART_CODE").css("display", "");
		$("#DEVICE_STAFF_ID").css("display", "none");
		$("#DEVICE_NUMBER").css("display", "none");
	}
	//如果是自助终端工号
	if(queryMode == "1"){
		$("#DEVICE_STAFF_ID").css("display", "");
		$("#DEPART_CODE").css("display", "none");
		$("#DEVICE_NUMBER").css("display", "none");
	}
	//如果是设备资产编码
	if(queryMode == "2"){
		$("#DEVICE_NUMBER").css("display", "");
		$("#DEPART_CODE").css("display", "none");
		$("#DEVICE_STAFF_ID").css("display", "none");
	}
}

//查询终端信息
function queryTerminal() {
	var queryMode = $("#cond_QUERY_MODE").val();
	if(queryMode == ""){
		alert("请选择查询条件！");
		return;
	}
	if(queryMode == "0"){
		var departCode = $("#cond_DEPART_CODE").val();
		if(departCode == ""){
			alert("渠道编码不能为空！");
			return;
		}
	}
	if(queryMode == "1"){
		var departCode = $("#cond_DEVICE_STAFF_ID").val();
		if(departCode == ""){
			alert("自助终端工号不能为空！");
			return;
		}
	}
	if(queryMode == "2"){
		var departCode = $("#cond_DEVICE_NUMBER").val();
		if(departCode == ""){
			alert("设备资产编码不能为空！");
			return;
		}
	}
	
	$.beginPageLoading("查询中...");
    $.ajax.submit('QueryCondPart,navt', 'queryTerminal', '', 'QueryListPart', function() {
    	$.endPageLoading();
    	$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");;
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
        $("#CSSUBMIT_BUTTON").attr("disabled",true);
    });
}

//缴费提交
function onTradeSubmit(){
	var data = $.table.get("TerminalInfoTable").getCheckedRowDatas();
	var list = $.table.get("TerminalInfoTable").getTableData(null,true);
	if(list.length == 0){
		alert("请先查询出结果再提交！");
		return;
	}
	if(data.length == 0) {
		alert("请选择一条记录！");
		return;
	}
	if(data.length > 1) {
		alert("只能选择一条记录，请确认！");
		return;
	}
	
	if($.validate.verifyAll("FeeInfoPart")){
		var totalFee = $("#cond_TOTAL_FEE").val();
		if(totalFee <= 0){
			alert("缴费金额必须大于零！");
			return;
		}
		var device_number2 = data.get(0).get("DEVICE_NUMBER2");
		var device_id = data.get(0).get("DEVICE_ID");
		var balance = data.get(0).get("BALANCE")=="0.0"?"0":data.get(0).get("BALANCE");
		var recv_fee = data.get(0).get("RECV_FEE")=="0.0"?"0":data.get(0).get("RECV_FEE");
		var param = "cond_DEVICE_NUMBER2="+device_number2+"&cond_DEVICE_ID="+device_id
			+"&cond_BALANCE="+balance+"&cond_RECV_FEE="+recv_fee;
		$.cssubmit.addParam(param);
		$.cssubmit.bindCallBackEvent(tradeCallBack);		//设置提交业务后回调事件
		return true;
	}
}

function tradeCallBack(data){
	var content;
	if (data!=null) {
		content = "客户订单标识："+data.get("ORDER_ID")+"<br/>点【确定】继续业务受理。";
	}else{
		alert('业务受理失败!');
		return;
	}
	$.cssubmit.showMessage("success", "业务受理成功", content, true);
	
	$.printMgr.bindPrintEvent(tradePrint);
}

//打印
function tradePrint(data){
	var params = "TRADE_ID=" + data.get("TRADE_ID");
	params += "&FEE_AMOUNT=" + data.get("TOTAL_FEE");
	
	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit(null, "loadPrintData", params, null, 
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
