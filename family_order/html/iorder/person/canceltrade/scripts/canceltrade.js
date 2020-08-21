// 用户可返销订单查询
function queryCancelTrade() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return;
    }

    var startDate = $("#cond_START_DATE").val();
    var endDate = $("#cond_END_DATE").val();
    if (startDate > endDate) {
        MessageBox.alert("开始时间不能大于结束时间！请重新输入！");
        return;
    }

    $.beginPageLoading("正在查询数据...");
    $.ajax.submit("QueryCondPart", "queryCancelTrade", null, "TradeInfoPart",
        function (data) {
            $.endPageLoading();
            if (data && data.get("QUERY_CODE") === "N") {
                MessageBox.alert(data.get("QUERY_INFO"));
                $.cssubmit.disabledSubmitBtn(true);
            } else {
                $.cssubmit.disabledSubmitBtn(false);
            }
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });
}

// 订单详细信息弹出页面
function popupTradeInfoPage(obj) {
    var index = obj.rowIndex - 1; // 当前操作行
    var eparchyCode = cancelTradeTable.getRowData(index).get("col_EPARCHY_CODE"); // 用户归属地市，用该编码作为服务的路由
    var tradeId = cancelTradeTable.getRowData(index).get("col_TRADE_ID");
    var tradeTypeCode = cancelTradeTable.getRowData(index).get("col_TRADE_TYPE_CODE");
    var inParam = "&TRADE_ID=" + tradeId + "&EPARCHY_CODE=" + eparchyCode
            + "&TRADE_TYPE_CODE=" + tradeTypeCode;
    popupPage("工单信息", "canceltrade.TradeInfoNew", "initQueryTrade", inParam, null, "full");
}

// 提交前的校验方法
var checkedTradeIdList = ""; // 票据检查通过流水列表
var inCheckTradeIdList = ""; // 票据待检查流水列表

function commitCheck() {
    // 金库验证
    var returnFlag = false;
    var tag = $("#4APARIS_TAG").val();
    if (tag === "0") {
        $.beginPageLoading("正进行金库认证...");
        $.treasury.auth("SALESERV_4A_CancelTrade",
            function (ret) {
                $.endPageLoading();
                if (true === ret) {
                    MessageBox.alert("认证成功");
                    $("#4APARIS_TAG").val("1");
                } else {
                    MessageBox.alert("认证失败");
                    $("#4APARIS_TAG").val("0");
                }
            });
        return returnFlag;
    } else {
        returnFlag = commitData();
        if (returnFlag && checkedTradeIdList !== inCheckTradeIdList) {
            $.beginPageLoading("正在检查票据信息...");
            $.ajax.submit(null, "ticketCancelCheck", $.cssubmit.dynamicParams, null,
                function (ajaxData) {
                    $.endPageLoading();
                    var ticketInfo = ajaxData.get(0).get("TICKET_INFO");
                    if (ticketInfo.length > 0) {
                        MessageBox.confirm("请回收之前打印的票据：" + "\r\n" + ticketInfo + "是否继续进行返销操作?", null,
                            function (btn) {
                                if (btn === "ok") {
                                    checkedTradeIdList = inCheckTradeIdList;
                                    $("#CSSUBMIT_BUTTON").click();
                                }
                            });
                    } else {
                        MessageBox.alert("没有票据需要进行回收，请继续进行返销操作!", null,
                            function () {
                                checkedTradeIdList = inCheckTradeIdList;
                                $("#CSSUBMIT_BUTTON").click();
                            });
                    }
                },
                function (error_code, error_info, derror) {
                    $.endPageLoading();
                    showDetailErrorInfo(error_code, error_info, derror);
                });
            return false;
        }
        return returnFlag;
    }
}

function commitData() {
    var checkedRows = $("input[name=cancelTradeBox]:checked", "#cancelTradeTable");
    if (checkedRows.length === 0) {
        MessageBox.alert("请选择需要执行返销的工单！");
        return false;
    }
    var tradeIdList = "";
    checkedRows.each(function () {
        tradeIdList += $(this).attr("tradeId") + ",";
    });

    // 获得当前选择数据
    var pass = true;
    var checkedData = cancelTradeTable.getCheckedRowsData("cancelTradeBox");
    if (checkedData == null || checkedData.length === 0) {
        MessageBox.alert("请选择需要执行返销的工单！");
        return false;
    } else {
        checkedData.each(function (tempData) {
            if (tempData) {
                var cancelTag = tempData.get("col_CANCEL_TAG");
                if (cancelTag === "-1") {
                    var tradeId = tempData.get("col_TRADE_ID");
                    var msg = "该订单[" + tradeId + "]不允许返销，请重新选择需要执行返销的工单！";
                    MessageBox.alert(msg);
                    pass = false;
                    return false;
                }
            }
        });
    }

    if (pass === true) {
        inCheckTradeIdList = tradeIdList;
        var remarks = $("#REMARKS").val();
        var invoiceNo = $("#INVOICE_NO").val();
        var param = "TRADEID_LIST=" + tradeIdList + "&REMARKS=" + remarks
                + "&INVOICE_NO=" + invoiceNo;
        $.cssubmit.addParam(param);
        return true;
    } else {
        return false;
    }
}

function CreateControl(DivID) {
    document.getElementById(DivID).innerHTML = "<OBJECT id='JunAct' classid='CLSID:BAB91EC4-6964-452C-8500-8BA8F1050903' width='0' height='0' style='display:none;'></OBJECT>";
}