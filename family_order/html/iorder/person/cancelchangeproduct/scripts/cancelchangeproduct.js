$(function () {
    var $serialNumber = $("#cond_SERIAL_NUMBER");
    var inModeCode = $serialNumber.attr("inModeCode");
    if (inModeCode === "1"
            && typeof eval(window.top.getCustorInfo) === "function") {
        var sn = window.top.getCustorInfo();
        $serialNumber.val(sn);
    }
});

function queryCancelTrade() {
    if (!$.validate.verifyAll("QueryCondPart"))
        return false;

    $.beginPageLoading("正在查询数据...");
    $.ajax.submit("QueryCondPart", "queryCancelTrade", null, "TradeInfoPart",
        function (ajaxData) {
            $.endPageLoading();
            if (ajaxData && ajaxData.get("QUERY_CODE") === "N") {
                MessageBox.alert(ajaxData.get("QUERY_INFO"));
                $.cssubmit.disabledSubmitBtn(true);
            } else {
                $.cssubmit.disabledSubmitBtn(false);
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

// 订单详细信息弹出页面
function popupTradeInfoPage(obj) {
    var index = obj.rowIndex - 1;
    var eparchyCode = cancelTradeTable.getRowData(index).get("EPARCHY_CODE"); // 用户归属地市，用该编码作为服务的路由
    var tradeId = cancelTradeTable.getRowData(index).get("TRADE_ID");
    var inParam = "&TRADE_ID=" + tradeId + "&EPARCHY_CODE=" + eparchyCode;
    popupPage("工单信息", "cancelchangeproduct.TradeInfoNew", "initQueryTrade", inParam, null, "full");
}


// 提交前的校验方法
function commitCheck() {
    var cancelTradeInfo = new $.DataMap();
    var checkedRadio = $("input[name=cancelTradeRadio]:checked", "#cancelTradeTable");
    if (checkedRadio.length === 1) {
        cancelTradeInfo = cancelTradeTable.getRowData(checkedRadio.attr("index"));
    } else {
        MessageBox.alert("请先选择需要取消的业务！");
        return false;
    }

    var tradeId = cancelTradeInfo.get("TRADE_ID");
    var serialNumber = $("#cond_SERIAL_NUMBER").val();
    var allFee = parseFloat(cancelTradeInfo.get("OPER_FEE"))
            + parseFloat(cancelTradeInfo.get("FOREGIFT"))
            + parseFloat(cancelTradeInfo.get("ADVANCE_PAY"));
    var tipFlag = cancelTradeInfo.get("TIPFLAG");
    MessageBox.confirm("取消该业务共需向用户清退费用【" + allFee + "】元！", null,
        function (btn) {
            if ("ok" === btn) {
                var remarks = $("#REMARKS").val();
                var invoiceNo = $("#INVOICE_NO").val();
                var param = "&TRADE_ID=" + tradeId + "&REMARKS=" + remarks
                        + "&INVOICE_NO=" + invoiceNo
                        + "&SERIAL_NUMBER=" + serialNumber;
                if ("Y" === tipFlag) {
                    MessageBox.confirm("您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。", null,
                        function (btn) {
                            if ("ok" === btn) {
                                $.cssubmit.addParam(param);
                                $.cssubmit.submitTrade();
                            }
                        });
                } else {
                    $.cssubmit.addParam(param);
                    $.cssubmit.submitTrade();
                }
            }
        });
    return false;
}