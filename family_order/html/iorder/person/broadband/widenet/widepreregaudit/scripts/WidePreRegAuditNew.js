function queryWidePreRegInfo(){
	if(!$.validate.verifyAll("WidePreRegConditionPart")){return false;}
	backPopup('UI-popup');
	$.beginPageLoading("正在查询数据...");
	ajaxSubmit('WidePreRegConditionPart', 'queryWidePreRegInfo','', 'WidePreRegInfoPart', function(rtnData) {
			$("#notifySmsBtn").css('display', 'none');
			var regStatus = $("#cond_REG_STATUS").val();
			if(regStatus == "4"){
				if(rtnData.get("RESULT_CODE") == "0"){
					$("#notifySmsBtn").css('display', '');
				}
			}
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			$("#notifySmsBtn").css('display', 'none');
			MessageBox.error("错误提示", info, function(btn) {
			}, null, detail);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
}

function preregInfoSubmit(){
	var paramList = new Wade.DatasetList();
	var tableList = widePreRegTable.getCheckedRowsData("monitorids");
	if(tableList == null || tableList.length == 0){
		MessageBox.alert("请选择修改的数据");
		return;
	}
	tableList.each(function(item,index,totalcount){
		if(totalcount==0){
			MessageBox.alert("请选择修改的数据");
			return;
		}
		if($("#PRE_REG_STATUS_" + index,"").val() != item.get("ORG_REG_STATUS","")){
			item.put("REG_STATUS", $("#PRE_REG_STATUS_" + index,"").val());
			paramList.add(item);
		}
	});
	if(paramList.length == 0){
		MessageBox.alert("您没有做任何修改，请修改后提交!");
		return;
	}
	$.beginPageLoading("业务受理中...");
	ajaxSubmit('', 'saveContent','&PARAM_INFO='+paramList.toString(), '', function(rtnData) { 
			MessageBox.success("成功提示","业务受理成功！");
			$.endPageLoading();
			queryWidePreRegInfo();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info, function(btn) {
			}, null, detail);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "受理超时!", function(btn) {
			});
		});
}

function notifySms(){
	var tableList = widePreRegTable.getCheckedRowsData("monitorids");
	if(tableList == null || tableList.length == 0){
		MessageBox.alert("请选择需要下发短信的用户");
		return;
	}
	$.beginPageLoading("下发短信中...");
	ajaxSubmit('', 'notifySms','&PARAM_INFO='+tableList.toString(), '', function(rtnData) { 
			MessageBox.success("成功提示","下发短信成功，修改登记状态！");
			$.endPageLoading();
			queryWidePreRegInfo();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info, function(btn) {
			}, null, detail);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
}

function queryCollectInfo(){
	if(!$.validate.verifyAll("WidePreRegCollectConditionPart")){return false;}
	backPopup('UI-popup');
	$.beginPageLoading("正在查询数据...");	
	ajaxSubmit('WidePreRegCollectConditionPart', 'queryCollectInfo','', 'WidePreRegCollectInfoPart', function(rtnData) { 
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info, function(btn) {
			}, null, detail);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
}

function changeOperQryTrade(){
	$("#fmymembers").html("");
	$("#widePreRegTBody").html("");
	var operType = $("#WIDE_PRE_REG_QRY_TYPE").val();
	if(operType == 1) {
		widePreRegTable.adjust();
		
		$("#widePreRegQry").css('display','');
		$("#widePreRegCollectQry").css('display','none');
	} else if(operType == 2){
		window["collect_REG_STATUS"].remove("1");
		window["collect_REG_STATUS"].remove("5");
		window["collect_REG_STATUS"].remove("6");
		
		$("#widePreRegQry").css('display','none');
		$("#widePreRegCollectQry").css('display','');
		widePreRegCollectTable.adjust();
	}
}