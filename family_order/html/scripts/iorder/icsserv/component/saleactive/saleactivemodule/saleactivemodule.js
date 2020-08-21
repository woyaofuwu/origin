
var COMBINE = "K";
var SERVICE = "S";
var DISCNT  = "D";
var PLATSVC = "Z";
var DEPOSIT = "A";
var GOODS   = "G";
var CREDIT  = "C";
var SCORE   = "J";
var gElems = $.DatasetList();

if (typeof SaleActiveModule === "undefined") {
	window["SaleActiveModule"] = function () {};
	var saleActiveModule = new SaleActiveModule();
}

(function(){
	$.extend(SaleActiveModule.prototype, {
        clearSaleActiveModule: function() {
            var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
            $('#' + componentId + "Body").html("");
            $.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
            gElems = $.DatasetList();
        },

        renderComponent: function (packageId, productId, campnType, newImei, deviceModelCode) {
            var eparchyCodeCompId = $("#SALEACTIVEMODULE_EPARCHY_CODE_COMPID").val();
            var eparchyCode = $("#" + eparchyCodeCompId).val();
            if (eparchyCode === "") {
                MessageBox.alert("请先输入号码");
                return;
            }

            saleActiveModule.clearSaleActiveModule();

            var needCheck = $("#SALEACTIVE_NEED_CHECK").val();
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var userId = $("#SALEACTIVE_USER_ID").val();
            var params = "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId
                    + "&EPARCHY_CODE=" + eparchyCode;
            if (typeof packageId !== "undefined") params += "&PACKAGE_ID=" + packageId;
            if (typeof productId !== "undefined") params += "&PRODUCT_ID=" + productId;
            if (typeof campnType !== "undefined") params += "&CAMPN_TYPE=" + campnType;
            if (typeof newImei !== "undefined") params += "&NEW_IMEI=" + newImei;
            if (typeof deviceModelCode !== "undefined") params += "&DEVICE_MODEL_CODE=" + deviceModelCode;

            if (needCheck === "true") {
                params += "&SPEC_TAG=" + "checkByPackage";
                $.beginPageLoading("规则校验中。。。");
                ajaxSubmit(null, null, params, $("#SALEACTIVEMODULE_COMPONENT_ID").val(),
                    function (ajaxData) {
                        $.endPageLoading();
                        var tigTag = ajaxData.get("IS_FTTH_USER");
                        if (tigTag === "true") {
                            MessageBox.alert("提醒：FTTH用户开户完成后或者参加宽带1+活动后，请在“FTTH光猫申请”界面申领光猫。")
                        }

                        var flag = true;
                        var confirmSet = ajaxData.get("TIPS_TYPE_CHOICE");
                        if (confirmSet && confirmSet.length > 0) {
                            flag = false;
                            confirmSet.each(function (item) {
                                MessageBox.confirm(item.get("TIPS_INFO"), null,
                                    function (btn) {
                                        if (btn === "ok") {
                                            afterConfirmMessageAction(packageId, productId, campnType, newImei, ajaxData);
                                        }
                                    });
                            });
                        }
                        if (flag) {
                            afterConfirmMessageAction(packageId, productId, campnType, newImei, ajaxData);
                        }

                        function afterConfirmMessageAction(packageId, productId, campnType, newImei, data) {
                            var warnSet = data.get("TIPS_TYPE_TIP");
                            if (warnSet && warnSet.length > 0) {
                                warnSet.each(function (item) {
                                    MessageBox.alert(item.get("TIPS_INFO"));
                                });
                            }

                            var checkData = new $.DataMap();
                            checkData.put("SERIAL_NUMBER", serialNumber);
                            checkData.put("USER_ID", userId);
                            checkData.put("EPARCHY_CODE", eparchyCode);
                            checkData.put("LIMIT_COUNT", data.get("LIMIT_COUNT"));
                            checkData.put("SMS_VERI_CODE_TYPE", data.get("SMS_VERI_CODE_TYPE", "0"));
                            checkData.put("NOTICE_CONTENT", data.get("NOTICE_CONTENT"));
                            checkData.put("BIND_SERIAL_NUMBER_B", data.get("BIND_SERIAL_NUMBER_B", "false"));
                            saleActiveModule.doCheckActionLinkedAsync(packageId, productId, campnType, newImei, checkData);
                        }
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("活动校验不通过", error_info);
                    });
            } else {
                saleActiveModule.drawSaleActive(packageId, productId ,campnType, newImei);
            }
        },


        doCheckActionLinkedAsync: function (packageId, productId, campnType, newImei, data) {
            var $submitPart = $("#SubmitPart");
            var $checkSMSBtn = $("#checkSMSBtn");
            var checkSmsVeriCode = data.get("SMS_VERI_CODE_TYPE");
            var isBindSerialNumberB = data.get("BIND_SERIAL_NUMBER_B");

            // REQ201511300032 业务问题优化-营销活动受理验证码下发
            $submitPart.css("display", "");
            $checkSMSBtn.css("display", "none");
            $("#CHECK_SMS_VERICODE").val(checkSmsVeriCode);
            $("#LIMIT_COUNT").val(data.get("LIMIT_COUNT"));
            $("#NOTICE_CONTENT").val(data.get("NOTICE_CONTENT"));

            if (checkSmsVeriCode === "1" || checkSmsVeriCode === "2") {
                $submitPart.css("display", "none");
                $checkSMSBtn.css("display", "");
                saleActiveModule.drawSaleActive(packageId, productId, campnType, newImei);
            } else if (isBindSerialNumberB === "true") {
                saleActiveModule.checkBindSerialNumberB(packageId, productId,campnType, newImei, data);
            } else  {
                saleActiveModule.drawSaleActive(packageId, productId, campnType, newImei);
            }
        },

        drawSaleActive: function (packageId, productId, campnType, newImei) {
        	var smsCode = $("#SMS_CODE").val();//和包平台短信验证码-wangsc10-20190410
            var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
            $("#" + componentId).css("display", "");

            var eparchyCodeCompId = $("#SALEACTIVEMODULE_EPARCHY_CODE_COMPID").val();
            var params = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
                    + "&NET_ORDER_ID=" + $("#NET_ORDER_ID").val()
                    + "&EPARCHY_CODE=" + $("#" + eparchyCodeCompId).val()
                    + "&SALEACTIVEMODULE_EPARCHY_CODE_COMPID=" + eparchyCodeCompId
            		+ "&SMS_CODE=" + smsCode;
            if (typeof packageId !== "undefined") params += "&PACKAGE_ID=" + packageId;
            if (typeof productId !== "undefined") params += "&PRODUCT_ID=" + productId;
            if (typeof campnType !== "undefined") params += "&CAMPN_TYPE=" + campnType;
            if (typeof newImei !== "undefined") params += "&NEW_IMEI=" + newImei;

            $.beginPageLoading("营销活动展现中。。。");
            ajaxSubmit(null, null, params, componentId,
                function (ajaxData) {
                    saleActiveModule.afterDrawSaleActive(ajaxData);
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("活动查询失败", error_info);
                });
        },

        afterDrawSaleActive: function (data) {
            $.endPageLoading();

            // REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 20160901
            var redPackVal = $("#RED_PACK_VALUE").val();
            if (redPackVal !== null && redPackVal > 0) {
                $("#AuthCodeBtn").css("display", "");
                $("#SubmitPart").css("display", "none");
            } else {
                $("#AuthCodeBtn").css("display", "none");
            }

            var afterFunc = $("#AFTER_SELECTPACKAGE_EVENT").val();
            if (afterFunc && afterFunc !== "") {
                try {
                    (new Function("var data = arguments[0];" + afterFunc + ";"))(data);
                } catch (e) {
                    MessageBox.error("自定义JS方法" + afterFunc + "执行出错！");
                }
            }
        },

        checkBindSerialNumberB: function (packageId, productId, campnType, newImei, data) {
            var params = packageId + "&" + productId + "&" + campnType + "&"
                    + newImei + "&" + data.get("EPARCHY_CODE");
            $("#CHECK_BIND_SERIAL_NUMBER_B_PARAMS").val(params);
            showPopup("checkBindSerialNumberBPopup", "checkBindSerialNumberBItem", true);
        },

        popupCheckBindSerialNumberB: function () {
            var el = "bindSerialNumberB",
                $obj = $("#" + el),
                serialNumber = $("#AUTH_SERIAL_NUMBER").val();

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var splitParams = $("#CHECK_BIND_SERIAL_NUMBER_B_PARAMS").val().split("&"),
                packageId = splitParams[0],
                productId = splitParams[1],
                ajaxParams = "&PACKAGE_ID=" + packageId
                    + "&PRODUCT_ID=" + productId
                    + "&EPARCHY_CODE=" + splitParams[4]
                    + "&BIND_SERIAL_NUMBER=" + $obj.val()
                    + "&SPEC_TAG=" + "CHECK_BIND_SN";
            ajaxSubmit(null, null, ajaxParams, $("#SALEACTIVEMODULE_COMPONENT_ID").val(),
                function () {
                    saleActiveModule.drawSaleActive(packageId, productId, splitParams[2], splitParams[3]);
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                }
            );
        },

        doSMSVeriCode: function (packageId, productId, data) {
            var serialNumber = data.get("SERIAL_NUMBER"),
                userId = data.get("USER_ID"),
                eparchyCode = data.get("EPARCHY_CODE"),
                params = "&SERIAL_NUMBER=" + serialNumber
                    + "&USER_ID=" + userId + "&PACKAGE_ID=" + packageId
                    + "&PRODUCT_ID=" + productId
                    + "&LIMIT_COUNT=" + data.get("LIMIT_COUNT")
                    + "&NOTICE_CONTENT=" + data.get("NOTICE_CONTENT")
                    + "&EPARCHY_CODE=" + eparchyCode
                    + "&SPEC_TAG=" + "SEND_VERI_CODE_SMS";
            ajaxSubmit(null, null, params, $("#SALEACTIVEMODULE_COMPONENT_ID").val(),
                function (ajaxData) {
                    $("#SMS_CHECKED_COUNT").html(ajaxData.get("CHECK_COUNT"));
                    $("#SMS_RECV").val(ajaxData.get("SMS_CODE"));
                    var SMSParams = productId + "&" + serialNumber + "&"
                            + userId + "&" + eparchyCode;
                    $("#CHECK_SMS_CODE_PARAMS").val(SMSParams);
                    showPopup("checkSMSCodePopup", "checkSMSCodeItem", true);
                },
                function (error_code, error_info) {
                   MessageBox.error(error_info);
                });
        },

        popupCheckSMSCode: function () {
            var el = "SMS_SEND",
                $obj = $("#" + el),
                SMSCodeRecv = $("#SMS_RECV").val();

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() !== SMSCodeRecv) {
                $.validate.alerter.one($obj[0], "验证码输入错误请重新验证！");
                return;
            }

            var splitParams = $("#CHECK_SMS_CODE_PARAMS").val().split("&"),
                ajaxParams = "&PRODUCT_ID=" + splitParams[0]
                    + "&SERIAL_NUMBER=" + splitParams[1]
                    + "&USER_ID=" + splitParams[2]
                    + "&EPARCHY_CODE=" + splitParams[3]
                    + "&SMS_CODE=" + $obj.val()
                    + "&SPEC_TAG=" + "UPD_VERI_CODE_OK";
            ajaxSubmit(null, null, ajaxParams, $("#SALEACTIVEMODULE_COMPONENT_ID").val(),
                function () {
                    // REQ201511300032 业务问题优化-营销活动受理验证码下发
                    MessageBox.success("验证码正确！请提交。");
                    $("#SubmitPart").css("display", "");
                    $("#checkSMSBtn").css("display", "none");
                    $("#SMS_VERI_SUCCESS").val("1"); // 短信验证码验证成功
                    hidePopup("checkSMSCodePopup");
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    $("#SMS_VERI_SUCCESS").val("0");
                });
        },

        // 提交时获取营销活动相关的数据
        getSaleActiveSubmitData: function () {
            var saleActiveData = new $.DataMap();
            saleActiveData.put("CAMPN_TYPE", $("#SALEACTIVE_CAMPN_TYPE").val());
            saleActiveData.put("PRODUCT_ID", $("#SALEACTIVE_PRODUCT_ID").val());
            saleActiveData.put("PACKAGE_ID", $("#SALEACTIVE_PACKAGE_ID").val());
            saleActiveData.put("START_DATE", $("#SALEACTIVE_START_DATE").html());
            saleActiveData.put("END_DATE", $("#SALEACTIVE_END_DATE").html());
            saleActiveData.put("BOOK_DATE", $("#SALEACTIVE_BOOK_DATE").val());
            saleActiveData.put("SALEGOODS_IMEI", $("#SALEGOODS_NEW_IMEI").val());
            saleActiveData.put("SALE_STAFF_ID", $("#SALE_STAFF_ID").val());
            saleActiveData.put("ONNET_START_DATE", $("#SALEACTIVE_ONNET_START_DATE").html());
            saleActiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").html());
            saleActiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").html());

            // iPhone6活动处理 20141022
            saleActiveData.put("IPHONE6_IMEI", $("#IPHONE6_IMEI").val());

            // 针对营销活动发票模版改造 冼乃捷20150515
            saleActiveData.put("ALL_MONEY_NAME", $("#ALL_MONEY_NAME").val());

            // REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
            saleActiveData.put("GIFT_CODE", $("#GIFT_CODE").val());

            //购机活动.可选信用购机标记
            saleActiveData.put("CREDIT_PURCHASES", $("#CREDIT_PURCHASES").val());

            if (gElems.length > 0) {
                saleActiveData.put("SELECTED_ELEMENTS", gElems);
            }
            return saleActiveData;
        },

        // 提交时的营销活动JS规则校验
        saleActiveSubmitJSCheck: function () {
            var packageObj = $("#SALEACTIVE_PACKAGE_ID");
            if (packageObj.length === 0 || packageObj.val() === "") {
                MessageBox.alert("请先选择一个活动");
                return false;
            }
            if (!saleActiveModule.elemLimitNumberCheck())
                return false;
            if (!saleActiveModule.combineElemLimitNumberCheck())
                return false;
            
            var goods = saleActiveModule.spGetGoods();
            if (goods.length > 0) {
                for (var i = 0; i < goods.length; i++) {
                    var item = goods.get(i);
                    if (item.get("HAS_CHECKED") !== "TRUE") {
                        MessageBox.alert("请先校验资源！");
                        return false;
                    } else if (item.get("RES_CODE") === "-1") {
                        MessageBox.alert("资源数据丢失，请重新输入校验！");
                        item.put("HAS_CHECKED", "FALSE"); // 设置实物为未校验状态
                        var elemKeyArr = item.get("ELEM_KEY").split("_");
                        var ResTextId = $(elemKeyArr[0] + "_" + elemKeyArr[1]
                                + "_RES_CODE");
                        ResTextId.attr("disabled", false);
                        return false;
                    }
                }
            }
            return true;
        },

        elemLimitNumberCheck: function () {
            var limitType = $("#SALEACTIVE_PKG_LIMIT_TYPE").val();
            var minLimit = parseInt($("#SALEACTIVE_PKG_ELEMENT_MIN").val());
            var maxLimit = parseInt($("#SALEACTIVE_PKG_ELEMENT_MAX").val());
            var checkMinElem = (minLimit === -1) ? true : saleActiveModule.compareElemNumWithLimit(limitType, minLimit, null);
            var checkMaxElem = (maxLimit === -1) ? true : saleActiveModule.compareElemNumWithLimit(limitType, null, maxLimit);
            return checkMinElem && checkMaxElem;
        },

        compareElemNumWithLimit: function (elemType, minLimit, maxLimit) {
            var elemNum = 0;
            var elemTypeStr = "";
            switch (elemType) {
                case DISCNT:
                    elemTypeStr = "优惠";
                    break;
                case SERVICE:
                    elemTypeStr = "服务";
                    break;
                default:
                    elemTypeStr = "所有";
            }

            // 统计元素选择数
            if (elemType === "") {
                elemNum = gElems.length;
            } else {
                gElems.each(function (elem) {
                    if (elem.get("ELEMENT_TYPE_CODE") === elemType
                            && !elem.get("DISABLED"))
                        elemNum++;
                });
            }

            if (maxLimit === null && elemNum < minLimit) {
                MessageBox.alert(elemTypeStr + "元素最小选择数为：" + maxLimit
                    + "！当前" + elemTypeStr + "元素选择数为：" + elemNum);
                return false;
            } else if (minLimit === null && elemNum > maxLimit) {
                MessageBox.alert(elemTypeStr + "元素最大选择数为：" + maxLimit
                        + "！当前" + elemTypeStr + "元素选择数为：" + elemNum);
                return false;
            } else {
                return true;
            }
        },

        combineElemLimitNumberCheck: function () {
            var elemNum = 0;
            var minElem = $("#SALEACTIVE_COMBINE_MIN").val();
            var maxElem = $("#SALEACTIVE_COMBINE_MAX").val();

            gElems.each(function (elem) {
                if (elem.get("ELEMENT_TYPE_CODE") === COMBINE)
                    elemNum++;
            });
            if (minElem) {
                var minElemNum = parseInt(minElem);
                if (minElemNum !== -1 && elemNum < minElemNum) {
                    MessageBox.alert("组合包元素最小选择数为：" + minElemNum
                            + "！当前元素选择数为：" + elemNum + "！");
                    return false;
                }
            }
            if (maxElem) {
                var maxElemNum = parseInt(maxElem);
                if (maxElemNum !== -1 && elemNum > maxElemNum) {
                    MessageBox.alert("组合包元素最大选择数为：" + minElemNum
                        + "！当前元素选择数为：" + elemNum + "！");
                    return false;
                }
            }
            return true;
        },

        spGetGoods: function () {
            var r = $.DatasetList();
            gElems.each(function (item) {
                if (item.get("ELEM_KEY").indexOf("GOODS") >= 0) r.add(item);
            });
            return r;
        },

        // 营销活动包展现时自动拼装已选上的元素
        spAutoAddCheckedElems: function () {
            gElems = $.DatasetList();

            // 组合包
            $("#SaleCombineDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 服务
            $("#SaleServiceDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 优惠
            $("#SaleDiscntDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 平台服务
            $("#SalePlatsvcDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 预存
            $("#SaleDepositDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 实物
            $("#SaleGoodsDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 信用度
            $("#SaleCreditDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });
            // 积分
            $("#SaleScoreDiv input[type=checkbox]").each(function () {
               if (this.checked)
                   saleActiveModule.spCheckBoxOnclickAction(this.id);
            });

        },
        
        spCheckBoxOnclickAction: function (id) {
            var checkBox = $("#" + id);
            if (checkBox.attr("checked")) {
                saleActiveModule.spDecodeElem(id);
            } else {
                saleActiveModule.hideAttr();
                saleActiveModule.spDelElemById(id);
            }
        },

        // 拼装元素串
        spDecodeElem: function (id) {
            var param = $.DataMap();
            var checkBox = $("#" + id);
            var elemType = checkBox.attr("elementTypeCode");

            saleActiveModule.spDecodePubParam(param, checkBox);
            switch (elemType) {
                case COMBINE:
                    saleActiveModule.spDecodeCombineParam(param);
                    break;
                case SERVICE:
                    saleActiveModule.spDecodeServiceParam(param, checkBox);
                    break;
                case DISCNT:
                    saleActiveModule.spDecodeDiscntParam(param, checkBox);
                    break;
                case PLATSVC:
                    saleActiveModule.spDecodePlatsvcParam(param, checkBox);
                    break;
                case DEPOSIT:
                    saleActiveModule.spDecodeDepositParam(param, checkBox);
                    break;
                case GOODS:
                    saleActiveModule.spDecodeGoodsParam(param, checkBox);
                    break;
                case CREDIT:
                    saleActiveModule.spDecodeCreditParam(param, checkBox);
                    break;
                case SCORE:
                    saleActiveModule.spDecodeScoreParam(param, checkBox);
                    break;
                default:
                    return;
            }
            saleActiveModule.spAddElem(param);
        },

        // 新增元素
        spAddElem: function (elem) {
            gElems.each(function (item, index) {
                if (item.get("ELEM_KEY") === elem.get("ELEM_KEY")) {
                    gElems.removeAt(index);
                }
            });
            gElems.add(elem);
        },

        // 通过checkBoxId删除元素
        spDelElemById: function (id) {
            gElems.each(function (item, index) {
                if (item.get("ELEM_KEY") === id) {
                    gElems.removeAt(index);
                    saleActiveModule.spDelFeeItem(item);

                    if (item.get("ELEMENT_TYPE_CODE") === "A")
                        saleActiveModule.disableDepositPlusItem(id, true);
                }
            });
        },

        // 拼各类元素共有的参数
        spDecodePubParam: function (param, elem) {
            param.put("ELEM_KEY", elem.attr("id"));
            param.put("ELEMENT_ID", elem.attr("elementId"));
            param.put("ELEMENT_TYPE_CODE", elem.attr("elementTypeCode"));
            param.put("MODIFY_TAG", "0");
            param.put("FORCE_TAG", elem.attr("forceTag"));
            param.put("DISABLED", elem.attr("disabled"));

            var fee = elem.attr("fee");
            if (typeof fee !== "undefined") {
                var feeTypeCode = elem.attr("feeTypeCode");
                param.put("FEE", fee);
                param.put("FEE_MODE", elem.attr("feeMode"));
                param.put("FEE_TYPE_CODE", feeTypeCode);
                param.put("PAY_MODE", elem.attr("payMode"));
                param.put("IN_DEPOSIT_CODE", elem.attr("inDepositCode"));
                param.put("OUT_DEPOSIT_CODE", elem.attr("outDepositCode"));
                param.put("PAYMENT_ID", feeTypeCode);
            }
        },

        // 组合包拼串
        spDecodeCombineParam: function (param) {
            saleActiveModule.spAddFeeItem(param);
        },

        // 服务拼串
        spDecodeServiceParam: function (param, elem) {
            param.put("START_DATE", elem.attr("startDate"));
            param.put("END_DATE", elem.attr("endDate"));
            saleActiveModule.decodeAttrParam(param, elem);
        },

        // 优惠拼串
        spDecodeDiscntParam: function (param, elem) {
            param.put("START_DATE", elem.attr("startDate"));
            param.put("END_DATE", elem.attr("endDate"));
            saleActiveModule.decodeAttrParam(param, elem);
        },

        // 平台业务拼串
        spDecodePlatsvcParam: function (param, elem) {
            param.put("ELEMENT_ID", elem.attr("elementId"));
        },

        // 预存拼串
        spDecodeDepositParam: function (param, elem) {
            param.put("START_DATE", elem.attr("startDate"));
            param.put("END_DATE", elem.attr("endDate"));
            param.put("SERIAL_NUMBER_B", elem.attr("serialNumberB"));
            param.put("GIFT_USER_ID", elem.attr('userIdA'));
            saleActiveModule.spAddFeeItem(param);

            saleActiveModule.disableDepositPlusItem(param.get('ELEM_KEY'), false);
        },

        disableDepositPlusItem: function (id, flag) {
            var checkBox = $("#" + id);
            var giftUseTag = checkBox.attr("giftUseTag");
            if (giftUseTag === "1") {
                var plusId = "DEPOSIT_" + checkBox.attr("index") + "_DEPOSIT_USER_ID";
                if (plusId !== null && plusId !== "") {
                    var plusObj = $("#" + plusId);
                    if (plusObj.length > 0)
                        plusObj.attr("disabled", flag);
                }
            }
        },

        // 实物拼串
        spDecodeGoodsParam: function (param, elem) {
            param.put("HAS_CHECKED", elem.attr("hasCheck"));
            if (elem.attr("resTypeCode") === "S") {
                var resCode = $("#SELECT_GOODS_" + elem.attr("index") + "_"
                        + elem.attr("elementId")).val();
                param.put("RES_CODE", resCode);
            }
            saleActiveModule.spAddFeeItem(param);
        },

        // 信用度拼串
        spDecodeCreditParam: function (param, elem) {
            param.put("CREDIT_VALUE", elem.attr("creditValue"));
            param.put("CREDIT_GIFT_MONTHS", elem.attr("creditGiftMonths"));
            param.put("START_DATE", elem.attr("startDate"));
            param.put("END_DATE", elem.attr("endDate"));
            saleActiveModule.spAddFeeItem(param);
        },
        
        // 积分拼串
        spDecodeScoreParam: function (param, elem) {
            param.put("SCORE_VALUE", $("#" + elem.attr("inputId")).val());
            saleActiveModule.spAddFeeItem(param);
        },

        // 添加元素费用
        spAddFeeItem: function (param) {
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            var fee = param.get("FEE", "");
            var feeMode = param.get("FEE_MODE", "");
            var feeTypeCode = param.get("FEE_TYPE_CODE", "");
            var payMode = param.get("PAY_MODE", "");
            var elemType = param.get("ELEMENT_TYPE_CODE", "");
            var elementId = param.get("ELEMENT_ID");

            if (fee === "" || feeMode === "" || feeTypeCode === "") return;
            if (payMode === "1") return;

            // 预存元素的ELEMENT_ID需要记录到表里，后续要根据ELEMENT_ID找到ACTION_CODE(A_DISCNT_CODE)传到账务
            if (elemType === DEPOSIT)
                saleActiveModule.insertFee(tradeTypeCode, feeMode, feeTypeCode, fee, elementId);
            else
                saleActiveModule.insertFee(tradeTypeCode, feeMode, feeTypeCode, fee);
        },

        // 删除元素费用
        spDelFeeItem: function (param) {
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            var feeMode = param.get("FEE_MODE");
            var feeTypeCode = param.get("FEE_TYPE_CODE");
            var elementId = param.get("ELEMENT_ID");
            var elemType = param.get("ELEMENT_TYPE_CODE");

            if (elemType === DEPOSIT)
                $.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode, elementId);
            else
                $.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode);
        },

        insertFee: function (tradeTypeCode, feeMode, feeTypeCode, fee, elementId) {
            var feeData = $.DataMap();
            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
            feeData.put("MODE", feeMode);
            feeData.put("CODE", feeTypeCode);
            feeData.put("FEE", fee);
            if (typeof elementId !== "undefined" && elementId !== "") {
                feeData.put("ELEMENT_ID", elementId);
            }
            $.feeMgr.insertFee(feeData);
        },

        // 构建元素属性值
        decodeAttrParam: function (param, elem) {
            var attrParam = $("#" + elem.attr("id") + "_ATTR").val();
            if (typeof attrParam !== "undefined" && attrParam !== "") {
                param.put("ATTR_PARAM", $.DatasetList(attrParam));
            }
        },

        checkDepositGiftUser: function (obj) {
            var $obj;
            if (obj.type === "button") {
                $obj = obj.prev();
            } else {
                $obj = $(obj);
            }
            var id = $obj.attr("chkboxid");
            $("#" + id).attr({
                userIdA: "",
                serialNumberB: ""
            });
            var elem = saleActiveModule.spGetElem(id);
            if (elem !== null) {
                elem.put("GIFT_USER_ID", "");
                elem.put("SERIAL_NUMBER_B", "");
            }
            var giftSerialNumber = $obj.val();
            if (giftSerialNumber === "")
                return false;

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&GIFT_SERIAL_NUMBER=" + giftSerialNumber
                    + "&PRODUCT_ID=" + $("#SALEACTIVE_PRODUCT_ID").val()
                    + "&PACKAGE_ID=" + $("#SALEACTIVE_PACKAGE_ID").val()
                    + "&EPARCHY_CODE=" + $("#SALEDEPOSIT_EPARCHY_CODE").val()
                    + "&SPEC_TAG=" + "checkDepositGiftUser";
            ajaxSubmit(null, null, params, $("#SALEDEPOSIT_COMPONENT_ID").val(),
                function (ajaxData) {
                    saleActiveModule.afterCheckDepositGiftUser(ajaxData.get(0), $obj);
                },
                function (error_code, error_info) {
                    saleActiveModule.checkDepositGiftUserFail(error_code, error_info, $obj);
                });
        },

        afterCheckDepositGiftUser: function (data, obj) {
            var elem = saleActiveModule.spGetElem(obj.attr("chkboxid"));
            if (elem !== null) {
                var flag = true;
                var confirmSet = data.get("TIPS_TYPE_CHOICE");
                if (confirmSet && confirmSet.length > 0) {
                    flag = false;
                    confirmSet.each(function (item) {
                        MessageBox.confirm(item.get("TIPS_INFO"), null,
                            function (btn) {
                                if (btn === "ok") {
                                    afterConfirmMessageAction(data, obj);
                                }
                            });
                    });
                }
                if (flag) {
                    afterConfirmMessageAction(data, obj);
                }
            }

            function afterConfirmMessageAction(data, obj) {
                var warnSet = data.get("TIPS_TYPE_TIP");
                if (warnSet && warnSet.length > 0) {
                    warnSet.each(function (item) {
                        MessageBox.alert(item.get("TIPS_INFO"));
                    });
                }
                elem.put("GIFT_USER_ID", data.get("GIFT_USER_ID"));
                elem.put("SERIAL_NUMBER_B", obj.val());
                elem.put("GIFT_START_DATE", data.get("GIFT_START_DATE"));
                elem.put("GIFT_END_DATE", data.get("GIFT_END_DATE"));
                MessageBox.success("赠送号码校验成功！");
            }
        },

        checkDepositGiftUserFail: function (code, info, obj) {
            obj.val("");
            var elem = saleactiveModule.spGetElem(obj.attr("chkboxid"));
            elem.put("SERIAL_NUMBER_B", "");
            elem.put("GIFT_USER_ID", "");
            elem.put("GIFT_START_DATE", "");
            elem.put("GIFT_END_DATE", "");
            MessageBox.error("赠送号码校验失败：" + info);
        },

        // 根据checkBoxId获取元素
        spGetElem: function (id) {
            var elem = null;
            gElems.each(function (item) {
                if (item.get("ELEM_KEY") === id) {
                    elem = item;
                    return false;
                }
            });
            return elem;
        },

        selectGiftGoods: function (obj) {
            var $obj = $(obj);
            var saleGoods = saleActiveModule.spGetGoodsByGoodsId($obj.attr("goodsId"));
            if (saleGoods)
                saleGoods.put("RES_CODE", $obj.val());
        },

        spCheckResInfo: function (obj) {
            var index = $(obj).attr("index");
            var input = $("#GOODS_" + index + "_RES_CODE");
            var resCode = input.val();
            var saleStaffId = $("GOODS_" + index + "_STAFF_ID").val();

            if (resCode === "") {
                MessageBox.alert("终端串码不能为空！");
                return;
            }
            if (resCode.length > 20) {
                MessageBox.alert("终端串码最多20位，您输入的串码过长，请确认后输入！");
                return;
            }
            if (input.attr("enterStaffTag") === "1" && saleStaffId === "") {
                MessageBox.alert("请输入促销员工！");
                return;
            }

            // 设置实物为未校验
            saleActiveModule.spSetResCheckState("FALSE", input.attr("goodsId"));

            var params = "&RES_TYPE_CODE=" + input.attr("resTypeCode")
                    + "&RES_ID=" + input.attr("resId")
                    + "&RES_NO=" + resCode
                    + "&RES_CHECK=" + input.attr("resCheck")
                    + "&SALE_STAFF_ID=" + saleStaffId
                    + "&PRODUCT_ID=" + $("#SALEACTIVE_PRODUCT_ID").val()
                    + "&PACKAGE_ID=" + $("#SALEACTIVE_PACKAGE_ID").val()
                    + "&EPARCHY_CODE=" + $('#SALEGOODS_EPARCHY_CODE').val()
                    + "&SPEC_TAG=" + "checkResInfo";
            $.beginPageLoading("终端预占中。。。");
            ajaxSubmit(null, null, params, $("#SALEGOODS_COMPONENT_ID").val(),
                function (ajaxData) {
                    saleActiveModule.spAfterCheckResInfo(ajaxData, input);
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("终端预占失败", error_info);
                });
        },

        spSetResCheckState: function (state, goodsId) {
            var saleGoods = saleActiveModule.spGetGoodsByGoodsId(goodsId);
            if (saleGoods) {
                saleGoods.put("HAS_CHECKED", state);
                saleActiveModule.spAddElem(saleGoods);
            }
        },

        spGetGoodsByGoodsId: function (goodsId) {
            var goods = null;
            gElems.each(function (item) {
                if (item.get("ELEM_KEY").indexOf("GOODS") >= 0
                        && item.get("ELEMENT_ID") === goodsId) {
                    goods = item;
                    return false;
                }
            });
            return goods;
        },

        spAfterCheckResInfo: function (data, obj) {
            $.endPageLoading();
            if (data.length <= 0) {
                MessageBox.error("资源校验失败！");
                return;
            }

            var saleGoods = saleActiveModule.spGetGoodsByGoodsId(obj.attr("goodsId"));
            saleGoods.put("HAS_CHECKED", "TRUE");
            saleGoods.put("RES_CODE", obj.val());
            saleGoods.put("SALE_STAFF_ID", $("#GOODS_" + obj.attr("index") + "_STAFF_ID").val());
            saleActiveModule.spAddElem(saleGoods);
            obj.attr("disabled", true);
            MessageBox.success("资源校验成功！");
        },

        showAttr: function (obj) {
            var $obj = $(obj);
            var itemIndex = $obj.attr("checkboxId");
            var element = saleActiveModule.spGetElem(itemIndex);
            if (element === null) {
                return;
            }

            var params = "&ELEMENT_ID=" + $obj.attr("elementId")
                    + "&ELEMENT_TYPE_CODE=" + $obj.attr("elementTypeCode")
                    + "&ITEM_INDEX=" + itemIndex;
            $.ajax.submit(null, null, params, "saleElementAttr",
                function () {
                    saleActiveModule.afterShowAttr(obj);
                });
        },

        afterShowAttr: function (obj) {
            var $obj = $(obj);
            var element = saleActiveModule.spGetElem($obj.attr("checkboxId"));
            var attrs = element.get("ATTR_PARAM");
            if (typeof attrs !== "undefined" && attrs.length > 0) {
                attrs.each(function (attr) {
                    var attrCode = attr.get("ATTR_CODE");
                    var attrValue = attr.get("ATTR_VALUE");
                    $("#" + attrCode).val(attrValue);
                });
            }
            /* by huangls5
            var topAdd = 0;
            var scroll = $("div[class=m_wrapper]:first");
            if (scroll.length > 0) {
                topAdd = scroll.attr("scrollTop");
            }
            var o = $(obj).offset();
            $("#elementPanel").css("top", (o.top+obj.height()+topAdd) + "px");
            $("#elementPanel").css("left", (o.left+obj.width()-$("#elementPanel").width()) + "px");*/

            $("#elementPanel").css("display","");
        },

        hideAttr: function () {
            $("#elementPanel").css("display", "none");
        },

        confirmAttr: function (itemIndex) {
            var element = saleactiveModule.spGetElem(itemIndex);
            var attrs = $.DatasetList();
            $("#elementPanel select").each(function () { // by huangls5
                var attr = $.DataMap();
                attr.put("ATTR_CODE", this.id);
                attr.put("ATTR_VALUE", this.value);
                attrs.add(attr);
            });
            $("#elementPanel input").each(function () {
                var attr = $.DataMap();
                attr.put("ATTR_CODE", this.id);
                attr.put("ATTR_VALUE", this.value);
                attrs.add(attr);
            });
            element.put("ATTR_PARAM", attrs);
            $("#elementPanel").css("display", "none");
        }
    });
})();

/* by huangls5
showDateChoice: function(eventObj){
    var obj = $(eventObj);

    var params = "&ITEM_INDEX="+obj.attr("checkboxId");
    var elem = this.spGetElem(obj.attr("checkboxId"));
    if(elem == null)
    {
        alert('该元素还没有被选上');
        return;
    }
    var editMode = obj.attr('editMode');
    if(editMode == '1')
    {
        var checkObj = $('#'+obj.attr('checkboxId'));
        var nowDay = checkObj.attr('nowDay');
        var nextAcctDay = checkObj.attr('nextAcctDay');
        params += '&NOW_DAY=' + nowDay;
        params += '&NEXT_ACCT_DAY=' + nextAcctDay;
    }
    else if(editMode == '2')
    {
        params += '&END_DATE=' + elem.get('END_DATE');
    }
    params += '&EDIT_MODE=' + editMode;

    $.ajax.submit(null,null,params,'saleElementDate',function(){saleactiveModule.afterShowDateChoice(eventObj)});
},
afterShowDateChoice: function(eventObj){
    var obj = $(eventObj);
    var topAdd = 0;
    var scroll =  $("div[class=m_wrapper]:first");
    if(scroll.length>0){
        topAdd = scroll.attr("scrollTop");
    }
    var o = $(eventObj).offset();
    $("#elementDatePanel").css("top", (o.top+obj.height()+topAdd) + "px");
    $("#elementDatePanel").css("left", (o.left+obj.width()-$("#elementDatePanel").width()) + "px");
    $("#elementDatePanel").css("display","");
},
confirmDateChoice: function(itemIndex){
    var choiceInfo = elementDateChoice.getChoiceInfo();
    var element = saleactiveModule.spGetElem(itemIndex);

    if(choiceInfo.get('START_DATE') != '')
    {
        var startDate = choiceInfo.get('START_DATE');
        element.put("START_DATE", startDate);
        $('#'+itemIndex+'_START_DATE').html(startDate);
    }
    if(choiceInfo.get('END_DATE') != '')
    {
        var endDate = choiceInfo.get('END_DATE');
        if(choiceInfo.get('END_DATE').length == 10)
        {
            endDate += ' 23:59:59';
        }
        element.put("END_DATE", endDate);
        $('#'+itemIndex+'_END_DATE').html(endDate);
    }

    $("#elementDatePanel").css("display","none");
},
}*/