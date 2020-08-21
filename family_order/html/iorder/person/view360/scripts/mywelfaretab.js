function welfareHisQueryButton() {
	var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val();
	$.beginPageLoading("数据查询中。。。");
    $.ajax.submit("initParamPart", "queryInfo", param, "welfareHisTablePart",
        function (ajaxData) {
            $.endPageLoading();
            var alertInfo = ajaxData.get("ALERT_INFO");
            if ("" !== alertInfo)
                MessageBox.alert(alertInfo);
	
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
    
	/*loadTabInfo("welfareHisTablePart", "initParamPart");*/
	$("#tableViewButton").css("display", "none");
    $("#welfareHisTablePart").css("display", "");
    $("#tradeHisListPart").addClass("e_show-phone");
}
//"我的业务历史"页面从表格中选择业务台账
function selectTradeFromTable(el) {
    $("#tableViewButton").css("display", ""); // 显示"返回表格试图"按钮
    $("#welfareHisTablePart").css("display", "none");
    $("#tradeHisListPart").removeClass("e_show-phone");
    orderId = $(el).attr("orderId");
    var params = "&ORDER_ID="+orderId;
    ajaxSubmit(null,'queryDetailInfo',params,'mainTradeContent',function(data){
    	
		}, function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
    
}
function tableViewButton() {
	$("#tableViewButton").css("display", "none");
    $("#welfareHisTablePart").css("display", "");
    $("#tradeHisListPart").addClass("e_show-phone");
}