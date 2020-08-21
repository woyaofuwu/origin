
if (typeof SaleActiveTrade === "undefined") {
    window["SaleActiveTrade"] = function () {};
    var saleActiveTrade = new SaleActiveTrade();
}

(function(){
    $.extend(SaleActiveTrade.prototype, {
        saleActiveData: {},

        checkSaleBook: function (data) {
            var params = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
            $("#NET_ORDER_ID").val("");
            saleActiveTrade.saleActiveData = data;
            $.beginPageLoading("查询终端预约信息。。。");
            ajaxSubmit(null, "checkSaleBook", params, null,
                function (ajaxData) {
                    $.endPageLoading();
                    if (ajaxData && ajaxData.get("AUTH_BOOK_SALE") === 1) {
                        $("#AUTH_SERIAL_NUMBER").attr("disabled", false);
                        popupPage("用户已经预约办理了如下活动，请优先办理该业务", "saleactive.sub.BookListNew", "querySaleBookList", params, null, "c_popup c_popup-half c_popup-half-hasBg");
                    } else {
                        saleActiveTrade.refreshPartAfterAuth(data);
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.alert("提示信息", error_info);
                });
        },

        refreshPartAfterAuth: function (data) {
            if (typeof data === "undefined" || data === null) {
                data = saleActiveTrade.saleActiveData;
            }
            var userInfo = data.get("USER_INFO");
            var acctDayInfo = data.get("ACCTDAY_INFO");
            var userId = userInfo.get("USER_ID");
            var custId = userInfo.get("CUST_ID");
            var acctDay = acctDayInfo.get("ACCT_DAY");
            var firstDate = acctDayInfo.get("FIRST_DATE");
            var nextAcctDay = acctDayInfo.get("NEXT_ACCT_DAY");
            var nextFirstDate = acctDayInfo.get("NEXT_FIRST_DATE");

            $("#custinfo_EPARCHY_CODE").val(userInfo.get("EPARCHY_CODE"));
            saleActive.renderComponent(userId, custId, acctDay, firstDate,
                nextAcctDay, nextFirstDate, $("#LABEL_ID").val());
        },

        onTradeSubmit: function () {
            if (!saleActiveModule.saleActiveSubmitJSCheck()) {
                return false;
            }

            var saleActiveData = saleActiveModule.getSaleActiveSubmitData();
            var params = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
                    + "&SMS_VERI_SUCCESS=" + $("#SMS_VERI_SUCCESS").val()
                    + "&SALEACTIVEDATA=" + saleActiveData.toString();
            var netOrderId = $("#NET_ORDER_ID").val();
            if (netOrderId !== "undefined" && netOrderId !== "") {
                params += "&NET_ORDER_ID=" + netOrderId;
            }
            // 无线固话和铁通营销活动传ORDER_TYPE_CODE
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if (typeof tradeTypeCode !== "undefined" && tradeTypeCode !== null
                    && (tradeTypeCode === "3814" || tradeTypeCode === "3815")) {
                params += "&ORDER_TYPE_CODE=" + tradeTypeCode;
            }
            $.cssubmit.addParam(params);

            var checkParam = "&PACKAGE_ID_A=" + saleActiveData.get("PACKAGE_ID")
                    + "&PRODUCT_ID_A=" + saleActiveData.get("PRODUCT_ID");
            var otherNumber = saleActiveData.get("OTHER_NUMBER", "");
            if (otherNumber !== "") {
                checkParam += "&OTHER_NUMBER=" + otherNumber;
            }
            $.tradeCheck.addParam(checkParam);
            return true;
        },
        
        checkSMSButton: function () {
            var saleActiveData = saleActiveModule.getSaleActiveSubmitData();
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var userId = $("#SALEACTIVE_USER_ID").val();
            var eparchyCode = $("#" + $("#SALEACTIVEMODULE_EPARCHY_CODE_COMPID").val()).val();
            var packageId = saleActiveData.get("PACKAGE_ID");
            var productId = saleActiveData.get("PRODUCT_ID");
            var limitCount = $("#LIMIT_COUNT").val();
            var noticeContent = $("#NOTICE_CONTENT").val();
            var smsCodeFlag = $("#CHECK_SMS_VERICODE").val();
            
            var checkData = $.DataMap();
            checkData.put("SERIAL_NUMBER", serialNumber);
            checkData.put("USER_ID", userId);
            checkData.put("EPARCHY_CODE", eparchyCode);
            checkData.put("LIMIT_COUNT", limitCount);
            checkData.put("SMS_VERI_CODE_TYPE", smsCodeFlag);
            checkData.put("NOTICE_CONTENT", noticeContent);
            
            if (smsCodeFlag === "2") {
                MessageBox.confirm("确定要校验验证码吗?", null,
                    function (btn) {
                        if (btn === "ok") {
                            saleActiveModule.doSMSVeriCode(packageId, productId, checkData);
                        } else {
                            $("#SubmitPart").css("display", "");
                            $("#checkSMSBtn").css("display", "none");
                        }
                    });
            } else if (smsCodeFlag === "1") {
                saleActiveModule.doSMSVeriCode(packageId, productId, checkData);
            }
        },

        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 20160901
         */
        redPackPlaceOrderCall: function () {
            var redPackVal = $("#RED_PACK_VALUE").val();
            var deviceCost = $("#DEVICE_COST").val();
            if (redPackVal !== null && redPackVal > 0) {
                var saleActiveData = saleActiveModule.getSaleActiveSubmitData();
                var outParams = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
                        + "&PACKAGE_ID=" + saleActiveData.get("PACKAGE_ID")
                        + "&PRODUCT_ID=" + saleActiveData.get("PRODUCT_ID")
                        + "&CAMPN_TYPE=" + saleActiveData.get("CAMPN_TYPE")
                        + "&RES_NO=" + saleActiveData.get("SALEGOODS_IMEI")
                        + "&AMT_VAL=" + redPackVal + "&EPARCHY_CODE=" + "0898";
                // 调和包平台接口发送验证码（下订单）
                var finalFee = deviceCost - redPackVal;
                MessageBox.confirm("购机款为（" + deviceCost/100 + "）元，"
                        + "使用电子券金额为（" + redPackVal/100 + "）元，"
                        + "现金支付金额为（" + finalFee/100 + "）元。", null,
                    function (btn) {
                       if (btn === "ok")
                           saleActiveTrade.redPackPlaceOrder(outParams);
                    });
            } else {
                MessageBox.alert("无红包金额！");
                saleActiveTrade.afterRedPackCall();
            }
        },

        redPackPlaceOrder: function (inParams) {
            var userId = $("#SALEACTIVE_USER_ID").val();
            inParams += "&USER_ID=" + userId;
            $.beginPageLoading("和包平台下发验证码。。。");
            ajaxSubmit(null, "redPackPlaceOrder", inParams, null,
                function (ajaxData) {
                    $.endPageLoading();
                    if (ajaxData.get("X_RESULTCODE") === "1") {
                        var redOrderId = ajaxData.get("RED_ORDERID");
                        var redMerId = ajaxData.get("RED_MERID");
                        var serialNumber = ajaxData.get("SERIAL_NUMBER");
                        var productId = ajaxData.get("PRODUCT_ID");
                        var packageId = ajaxData.get("PACKAGE_ID");
                        var amtVal = ajaxData.get("AMT_VAL");
                        var deviceModelCode = ajaxData.get("DEVICE_MODEL_CODE");

                        var popupParam = "&RED_ORDERID=" + redOrderId
                                + "&RED_MERID=" + redMerId
                                + "&USER_ID=" + userId
                                + "&SERIAL_NUMBER=" + serialNumber
                                + "&PRODUCT_ID=" + productId
                                + "&PACKAGE_ID=" + packageId
                                + "&AMT_VAL=" + amtVal
                                + "&DEVICE_MODEL_CODE=" + deviceModelCode;
                        popupPage("请输入红包验证码", "saleactive.sub.RedPackAuthCodeNew", "initParams", popupParam, null, null, saleActiveTrade.afterRedPackCall);
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.alert(error_info);
                });
        },

        afterRedPackCall: function () {
            $("#CSSUBMIT_BUTTON").click();
        },

        setParam: function (netOrderId) {
            $("#NET_ORDER_ID").val(netOrderId);
        }
    });
})();