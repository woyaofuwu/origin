window.onload = function () {
    $("#SERIAL_NUMBER").focus();
}

function querySaleActive()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryCondPart', 'querySaleActive', '', 'QueryResultPart,ExportPart',
		function(ajaxData){
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", "查询失败！</br>" + info);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!");
		});
}

function myTabSwitchAction(ptitle, title)
{
	return true;
}

function showDetail(obj)
{
	var param = "&PACKAGE_ID=" + $(obj).attr("packageId");
	param += '&USER_ID=' + $(obj).attr("userId");
	param += '&SERIAL_NUMBER=' + $(obj).attr("serialNumber");
	param += '&RELATION_TRADE_ID=' + $(obj).attr("relationTradeId");
	param += '&INST_ID=' + $(obj).attr("instId");
	
	
	$.beginPageLoading("查看活动细节...");
	ajaxSubmit(null, 'showDetail', param, 'SaleActiveDetailPart', 
		function(ajaxData){
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", "查询失败！</br>" + info);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!");
		});
}

function changeDetailShow(value) {
	$("#detail1").css("display", "none");
	$("#detail2").css("display", "none");
	$("#detail3").css("display", "none");
	if (value == 1) {
		$("#detail1").css("display", "block");
	} else if (value == 2) {
		$("#detail2").css("display", "block");
	} else if (value == 3) {
		$("#detail3").css("display", "block");
	}
}