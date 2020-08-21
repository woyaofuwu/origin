function queryWidePreRegInfo(){
	if(!$.validate.verifyAll("WidePreRegConditionPart")){return false;}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('WidePreRegConditionPart', 'queryWidePreRegInfo','', 'WidePreRegInfoPart', function(rtnData) {
			$("#notifySmsBtn").css('display', 'none');
			var regStatus = $("#cond_REG_STATUS option:selected").val();
			if(regStatus == "4"){
				if(rtnData.get("RESULT_CODE") == "0"){
					$("#notifySmsBtn").css('display', '');
				}
			}
			$.endPageLoading();
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			$("#notifySmsBtn").css('display', 'none');
			MessageBox.error("错误提示", error_info, null, null, detail);
		}); 
}

function preregInfoSubmit(){
	var table = $.table.get("widePreRegTable");
	var paramList = new Wade.DatasetList();
	var tableList = table.getCheckedRowDatas();
	if(tableList.length == "0"){
		alert("请选择修改的数据");
		return;
	}
	tableList.each(function(item,index,totalcount){
		if(totalcount==0){
			alert("请选择修改的数据");
			return;
		}
		if(item.get("REG_STATUS","") != ""){
			paramList.add(item);
		}
	});
	if(paramList.length == "0"){
		alert("您没有做任何修改，请修改后提交!");
		return;
	}
	$.beginPageLoading("业务受理中...");
	$.ajax.submit('', 'saveContent','&PARAM_INFO='+paramList.toString(), '', function(rtnData) { 
			MessageBox.success("成功提示","业务受理成功！");
			$.endPageLoading();
			queryWidePreRegInfo();
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, detail);
		}); 
	
}


function notifySms(){
	var table = $.table.get("widePreRegTable");
	var tableList = table.getCheckedRowDatas();
	if(tableList.length == "0"){
		alert("请选择需要下发短信的用户");
		return;
	}
	$.beginPageLoading("下发短信中...");
	$.ajax.submit('', 'notifySms','&PARAM_INFO='+tableList.toString(), '', function(rtnData) { 
			MessageBox.success("成功提示","下发短信成功，修改登记状态！");
			$.endPageLoading();
			queryWidePreRegInfo();
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, detail);
		}); 
}

function queryCollectInfo(){
	if(!$.validate.verifyAll("WidePreRegCollectConditionPart")){return false;}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('WidePreRegCollectConditionPart', 'queryCollectInfo','', 'WidePreRegCollectInfoPart', function(rtnData) { 
			$.endPageLoading();
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, detail);
		}); 
}

function changeOperQryTrade(){
	var operType = $("#WIDE_PRE_REG_QRY_TYPE option:selected").val();
	$("#widePreRegQry").attr("class","");
	$("#widePreRegCollectQry").attr("class","e_hideX");
	var collectTable = $.table.get("widePreRegCollectTable");
	collectTable.cleanRows();
	var table = $.table.get("widePreRegTable");
	table.cleanRows();
	if(operType == 2){
		$("#widePreRegQry").attr("class","e_hideX");
		$("#widePreRegCollectQry").attr("class","");
	}
}


