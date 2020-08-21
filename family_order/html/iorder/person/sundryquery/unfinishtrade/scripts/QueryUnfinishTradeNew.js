/**
 * 
 */
function getUnfinishTrade(obj) {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {// 先校验已配置的校验属性
		return false;
	}
	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryCondPart', 'queryUnfinishTrade',
			null, 'QueryListPart', function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != '') {
					MessageBox.alert(data.get('ALERT_INFO'));
				}
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("警告提示", "查询超时");
			});
}

function inputCtrlForStaff(obj) {
	staffId = $(obj).val().toUpperCase();
	if (staffId.length > 8) {
		obj.value = staffId.substring(0, 8);
	} else {
		obj.value = staffId;
	}
}

/*REQ201707210024 未完工单界面功能优化*/
//显示Layer
function displayLayer(obj) {
    var params = "&ORDER_ID=" + $(obj).attr("orderid") + "&TRADE_ID=" + $(obj).attr("tradeid")
        + "&SERIAL_NUMBER=" + $(obj).attr("serialnumber") + "&TRADE_TYPE_CODE=" + $(obj).attr("tradetypecode");

    $.beginPageLoading();
    ajaxSubmit(this, "queryUnfinishTradeTrace", params, "table4TradeTrace,table4PFTrace", function (data) {
		/*if(data.get('ALERT_INFO') !=''){
		 alert(data.get('ALERT_INFO'));
		 }*/
        $.endPageLoading();
    }, function (code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function () {
        $.endPageLoading();
        MessageBox.alert("警告提示", "查询超时");
    });
}