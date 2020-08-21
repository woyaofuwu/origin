$(document).ready(function () {
    welfareFrame.init();
    welfareFrame.reload();
});
var wf = {
    print: function () {
        debugger;
        $.beginPageLoading("获取打印数据。。。");
        var param = "&ORDER_ID=" + parent.$.cssubmit.tradeData.get("ORDER_ID");
        param += "&USER_ID=" + parent.$.cssubmit.tradeData.get("USER_ID");
        param += "&ORDER_TYPE_CODE=" + parent.$.cssubmit.tradeData.get("ORDER_TYPE_CODE");
        param += "&TRADE_ID=" + parent.$.cssubmit.tradeData.get("TRADE_ID");
        param += "&TRADE_TYPE_CODE=" + parent.$.cssubmit.tradeData.get("TRADE_TYPE_CODE");
        param += "&EPARCHY_CODE=" + parent.$.cssubmit.tradeData.get("EPARCHY_CODE");

        // TODO 拿到权益中心受理页面上的权益订单流水 start
        var flowId = $("#WELFARE_FLOW_ID").val();
        // TODO 拿到权益中心受理页面上的权益订单流水 end

        if (flowId) {
            param += "&WELFARE_FLOW_ID=" + flowId;
        }
        $.ajax.submit("", "getPrintData", param, null, function (prtdata) {
            $.endPageLoading();
            console.info(prtdata.toString());
            //设置打印数据
            if (prtdata.containsKey("PRINT_DATA")) {
                //设置打印数据
                parent.$.printMgr.setPrintData(prtdata.get("PRINT_DATA"));
                //公共参数数据
                parent.$.printMgr.params.put("ORDER_ID", parent.$.cssubmit.tradeData.get("ORDER_ID"));
                parent.$.printMgr.params.put("EPARCHY_CODE", parent.$.cssubmit.tradeData.get("EPARCHY_CODE"));
                parent.$.printMgr.resetPrintFlag();	//重置打印轮询标记
                parent.$.printMgr.printReceipt();		//启动缓存打印数据
            }
        }, function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", "获取打印数据错误！", null, null, info, detail);
        });
    }
};