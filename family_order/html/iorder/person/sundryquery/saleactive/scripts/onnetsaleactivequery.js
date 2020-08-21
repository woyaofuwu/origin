function refreshPartAfterAuth(data) {
    var userInfo = data.get("USER_INFO");
    var param = "&USER_ID=" + userInfo.get("USER_ID")
            + "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE")
            + "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER");
    $.ajax.submit(null, "loadBaseTradeInfo", param, "saleActivePart,saleActiveDetailPart,userOperPart",
        function () {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

function getSelectedData(obj) {
    if (obj && typeof obj !== "undefined") {
        return saleActiveTable.getRowData(obj.attr("rowIndex") - 1);
    } else {
        if ($("#saleActiveTable tbody .on").length === 1) {
            return saleActiveTable.getSelectedRowData();
        } else {
            return new $.DataMap();
        }
    }
}

function selectSaleActive(obj) {
    var $obj = $(obj);
    var authData = $.auth.getAuthData();
    var userInfo = authData.get("USER_INFO");
    var serialNumber = userInfo.get("SERIAL_NUMBER");
    $.feeMgr.clearFeeList("237");

    var saleActiveInfo = getSelectedData($obj);
    var packageId = saleActiveInfo.get("PACKAGE_ID");
    var productId = saleActiveInfo.get("PRODUCT_ID");
    var productName = saleActiveInfo.get("PRODUCT_NAME");
    var relationTradeId = saleActiveInfo.get("RELATION_TRADE_ID");

    var resultTipInfo = "";
    var flag = saleActiveInfo.get("PRODT_FLAG");
    if (flag === "0") {
        resultTipInfo = "您没有产品【'" + productId + "|" + productName + "'】终止权限！";
        MessageBox.alert(resultTipInfo, null,
            function () {
                $obj.removeClass("on");
            });
        return;
    }

    var param = "&SERIAL_NUMBER=" + serialNumber + "&PRODUCT_ID=" + productId
            + "&PACKAGE_ID=" + packageId
            + "&RELATION_TRADE_ID=" + relationTradeId
            + "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE");
    $.beginPageLoading("活动校验中......");
    $.ajax.submit(null, "checkSaleActive", param, null,
        function (ajaxData) {
            // 应蔡世泳要求，去掉费用的处理
            /*var deposit = data.get(0,"DEPOSIT_MONEY");
            insertFee("237", "2", "102", deposit);
            var present = ajaxData.get(0,"PRESENT_MONEY");
            insertFee("237", "0", "601", present);*/
            param += "&USER_ID=" + userInfo.get("USER_ID");
            $.ajax.submit(null, "loadActiveDetailInfo", param, "saleActiveDetailPart,userOperPart",
                function (detailData) {
                    // 获取应收违约金
                    var refundData = detailData.get("REFUND_MONEY");
                    var resultTip = refundData.get("RESULT_TIP");
                    if (resultTip !== "0") {
                        resultTipInfo = "【" + refundData.get("RESULT_TIP_INFO") + "】，请手工计算违约金！";
                        MessageBox.alert(resultTipInfo);
                    }
                    var refundMoney = refundData.get("REFUND_MONEY");
                    $("#RETURNFEE").val(refundMoney);
                    $("#TRUE_RETURNFEE").val(refundMoney);

                    $("#saleActiveDetailPart").css("display", "");
                    $.endPageLoading();

                    var isNeedWarm = ajaxData.get(0, "IS_NEED_WARM");
                    if (isNeedWarm === "1") {
                        var packageName = saleActiveInfo.get("PACKAGE_NAME");
                        resultTipInfo = "取消活动【" + productName + "(" + packageName + ")】后，将不能再参加！";
                        MessageBox.alert("告警提示", resultTipInfo);
                    }

                    var alertInfo = detailData.get("ALERT_INFO");
                    if (alertInfo.length > 0) {
                        MessageBox.alert(alertInfo);
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error(error_info);
                });
        },
        function (error_code, error_info) {
            $obj.attr("disabled", true).removeClass("on");
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

function insertFee(tradeTypeCode, feeMode, feeTypeCode, fee) {
    var feeData = new $.DataMap();
    feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
    feeData.put("MODE", feeMode);
    feeData.put("CODE", feeTypeCode);
    feeData.put("FEE", fee);
    $.feeMgr.insertFee(feeData);
}

function addFeeList(obj) {
    var fee = $(obj).val();
    if (fee === "") {
        return false;
    }
    if (!$.isNumeric(fee)) {
        MessageBox.alert("费用输入非法！");
        return false;
    }

    var yearFee = fee * 100;
    $.feeMgr.removeFee("237", "0", "602");
    insertFee("237", "0", "602", yearFee);
}


