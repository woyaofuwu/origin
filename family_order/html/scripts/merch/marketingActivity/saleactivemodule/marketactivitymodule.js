
var COMBINE = "K";
var SERVICE = "S";
var DISCNT  = "D";
var PLATSVC = "Z";
var DEPOSIT = "A";
var GOODS   = "G";
var CREDIT  = "C";
var SCORE   = "J";
var gElems = $.DatasetList();

if(typeof(MarketActivityModule)=="undefined"){
	window["MarketActivityModule"]=function(){
		this.packageId = "";
		this.packageName = "";
		this.userId = "";
	};
	var marketActivityModule = new MarketActivityModule();
}

(function(){
	$.extend(MarketActivityModule.prototype, {
        clearSaleActiveModule: function() {
			var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
			$('#'+componentId+'_PART').html('');
			//TODO  现在业务类型合并了，如果界面不合并，那么根据业务类型删除费用时就会有问题！
			$.feeMgr.clearFeeList("240");
			var gElems = $.DatasetList();

        },

        readerComponent: function (packageId, productId, campnType, newImei, deviceModelCode) {
			if($('#SALE_EPARCHY_CODE').val() == ''){
				alert('请先输入号码！');
				return false;
			}


            marketActivityModule.clearSaleActiveModule();
			var eparchyCode = $('#SALE_EPARCHY_CODE').val();
            var needCheck = $("#SALEACTIVE_NEED_CHECK").val();
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var userId = $("#SALEACTIVE_USER_ID").val();
            
			this.packageId = packageId;
			var goodsInfo = $.params.get("GOODS_INFO");
			var terminalDetailInfo = $.params.get("TERMINAL_DETAIL_INFO");
			var allMoneyName = $.params.get("ALL_MONEY_NAME");
			$("#GOODS_INFO").val(goodsInfo);
			$("#TERMINAL_DETAIL_INFO").val(terminalDetailInfo);
			$("#ALL_MONEY_NAME").val(allMoneyName);
			
			this.packageName = $("#SALE_PACKAGE_NAME").val();
			this.userId = userId;

            
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

						marketActivityModule.showOrHideGoodInfo();//duhj
						//$.cssubmit.disabledSubmitBtn(false);

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
                            marketActivityModule.doCheckActionLinkedAsync(packageId, productId, campnType, newImei, checkData);
                        }
                    },
                    marketActivityModule.activeCheckFail
//                    function (error_code, error_info) {
//                        $.endPageLoading();
//                        //MessageBox.error("活动校验不通过", error_info);
//						MessageBox.alert("活动校验不通过", function(btn){closeNav();}, null, error_info);//duhj
//
//                    }
                    );
            } else {
                marketActivityModule.drawSaleActive(packageId, productId ,campnType, newImei);
            }
        },


        doCheckActionLinkedAsync: function (packageId, productId, campnType, newImei, data) {
            //var $submitPart = $("#SubmitPart");
            var $submitPart = $("#MerchSubmitPart");
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
                marketActivityModule.drawSaleActive(packageId, productId, campnType, newImei);
            } else if (isBindSerialNumberB === "true") {
                marketActivityModule.checkBindSerialNumberB(packageId, productId,campnType, newImei, data);
            } else  {
                marketActivityModule.drawSaleActive(packageId, productId, campnType, newImei);
            }
        },

        drawSaleActive: function (packageId, productId, campnType, newImei) {
        	var smsCode = $("#SMS_CODE").val();//和包平台短信验证码-wangsc10-20190410
			marketActivityModule.openDetailPage();
			var params = '';
			var userId = $('#SALEACTIVE_USER_ID').val();
			params += "&EPARCHY_CODE=" + $('#SALE_EPARCHY_CODE').val();
			params += "&SALEACTIVEMODULE_EPARCHY_CODE_COMPID=" + $('#SALEACTIVE_EPARCHY_CODE_COMPID').val();
			params += "&USER_ID="+userId;
			params += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			params += "&NET_ORDER_ID="+$("#NET_ORDER_ID").val();
			params += "&SMS_CODE="+smsCode;

        	        	
            var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
            if (typeof packageId !== "undefined") params += "&PACKAGE_ID=" + packageId;
            if (typeof productId !== "undefined") params += "&PRODUCT_ID=" + productId;
            if (typeof campnType !== "undefined") params += "&CAMPN_TYPE=" + campnType;
            if (typeof newImei !== "undefined") params += "&NEW_IMEI=" + newImei;

            $.beginPageLoading("营销活动展现中。。。");
            ajaxSubmit(null, null, params, componentId,
                function (ajaxData) {
                    marketActivityModule.afterDrawSaleActive(ajaxData);
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    //MessageBox.error("活动查询失败", error_info);
    				MessageBox.alert("错误提示", "活动查询失败:"+error_info, function(btn){closeNav();});

                });
        },

        afterDrawSaleActive: function (data) {
            $.endPageLoading();

            // REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 20160901
            var redPackVal = $("#RED_PACK_VALUE").val();
            if (redPackVal !== null && redPackVal > 0) {
                $("#AuthCodeBtn").css("display", "");
                $("#MerchSubmitPart").css("display", "none");
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
                    hidePopup("checkBindSerialNumberBPopup");
                    marketActivityModule.drawSaleActive(packageId, productId, splitParams[2], splitParams[3]);
                },
                function (error_code, error_info) {
					MessageBox.alert("错误提示","业务规则报错!",'', null, error_info);

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
                   //MessageBox.error(error_info);
					MessageBox.alert("错误提示","业务规则报错!",'', null, error_info);

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
                    $("#MerchSubmitPart").css("display", "");
                    $("#checkSMSBtn").css("display", "none");
                    $("#SMS_VERI_SUCCESS").val("1"); // 短信验证码验证成功
                    hidePopup("checkSMSCodePopup");
                },
                function (error_code, error_info) {
                    //MessageBox.error(error_info);
                    $("#SMS_VERI_SUCCESS").val("0");
					MessageBox.alert("错误提示","业务规则报错!",'', null, error_info);

                });
        },

        // 提交时获取营销活动相关的数据
        getSaleActiveSubmitData: function () {
            var saleActiveData = new $.DataMap();
            saleActiveData.put("PRODUCT_ID", $("#SALEACTIVE_PRODUCT_ID").val());
            saleActiveData.put("PACKAGE_ID", $("#SALEACTIVE_PACKAGE_ID").val());
            saleActiveData.put("SALEGOODS_IMEI", $("#SALEGOODS_NEW_IMEI").val());
            saleActiveData.put("SALE_STAFF_ID", $("#SALE_STAFF_ID").val());
            saleActiveData.put("CAMPN_TYPE", $("#SALEACTIVE_CAMPN_TYPE").val());
            saleActiveData.put("START_DATE", $("#SALEACTIVE_START_DATE").val());
            saleActiveData.put("END_DATE", $("#SALEACTIVE_END_DATE").val());
            saleActiveData.put("BOOK_DATE", $("#SALEACTIVE_BOOK_DATE").val());
            saleActiveData.put("ONNET_START_DATE", $("#SALEACTIVE_ONNET_START_DATE").val());
            saleActiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").val());
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
            saleActiveData.put("REL_PACKAGE_ID", $("#REL_PACKAGE_ID").val());//存放所依赖顺延的营销活动
            saleActiveData.put("NET_PACKAGE_ID", $("#NET_PACKAGE_ID").val());//存放在网时间所依赖顺延的营销活动

            return saleActiveData;
        },
        
        // 提交时的营销活动JS规则校验
        saleactiveSubmitJSCheck: function () {
            var packageObj = $("#SALEACTIVE_PACKAGE_ID");
            if (packageObj.length === 0 || packageObj.val() === "") {
                MessageBox.alert("请先选择一个活动");
                return false;
            }
            if (!marketActivityModule.elemLimitNumberCheck())
                return false;
            if (!marketActivityModule.combineElemLimitNumberCheck())
                return false;
            
            var goods = marketActivityModule.spGetGoods();                        
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
            var checkMinElem = (minLimit === -1) ? true : marketActivityModule.compareElemNumWithLimit(limitType, minLimit, null);
            var checkMaxElem = (maxLimit === -1) ? true : marketActivityModule.compareElemNumWithLimit(limitType, null, maxLimit);
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
			$('#detailTitleName').text($('#SALEACTIVE_PACKAGE_NAME').val());
		
			// 优惠
			$("#SaleDiscntTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 服务
			$("#SaleServiceTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//预存赠送
			$("#SaleDepositTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//积分
			$("#SaleScoreTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//信用度
			$("#SaleCreditTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//平台业务
			$("#SalePlatSvcTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 实物
			$("#SaleGoodsTable input[type=checkbox]").each(function()
			{
				marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});
			
			$("#SaleCombineTable input[type=checkbox]").each(function()
			{
				marketActivityModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});

        },
        
        spCheckBoxOnclickAction: function (id) {
            var checkBox = $("#" + id);
            if (checkBox.attr("checked")) {
                marketActivityModule.spDecodeElem(id);
            } else {
                marketActivityModule.hideAttr();
                marketActivityModule.spDelElemById(id);
            }

        },

        // 拼装元素串
        spDecodeElem: function (id) {
            var param = $.DataMap();
            var checkBox = $("#" + id);
            //var elemType = checkBox.attr("elementTypeCode");
			var elemType = checkBox.attr("element_type_code");

            marketActivityModule.spDecodePubParam(param, checkBox);
            switch (elemType) {
                case COMBINE:
                    marketActivityModule.spDecodeCombineParam(param);
                    break;
                case SERVICE:
                    marketActivityModule.spDecodeServiceParam(param, checkBox);
                    break;
                case DISCNT:
                    marketActivityModule.spDecodeDiscntParam(param, checkBox);
                    break;
                case PLATSVC:
                    marketActivityModule.spDecodePlatsvcParam(param, checkBox);
                    break;
                case DEPOSIT:
                    marketActivityModule.spDecodeDepositParam(param, checkBox);
                    break;
                case GOODS:
                    marketActivityModule.spDecodeGoodsParam(param, checkBox);
                    break;
                case CREDIT:
                    marketActivityModule.spDecodeCreditParam(param, checkBox);
                    break;
                case SCORE:
                    marketActivityModule.spDecodeScoreParam(param, checkBox);
                    break;
                default:
                    return;
            }
            marketActivityModule.spAddElem(param);
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

		/*通过checkId删除一个元素*/
		spDelElemById: function(id) {
			var cb = $('#'+id);
			gElems.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					gElems.removeAt(index);
					marketActivityModule.spDelFeeItem(item);
					if(item.get('ELEMENT_TYPE_CODE') == 'A')
					{
						marketActivityModule.disabledDepositPlusItem(item.get("ELEM_KEY"), true);
					}
				}
			});
		},
        
        // 拼各类元素共有的参数
        spDecodePubParam: function (param, elem) {

       	
            param.put("ELEM_KEY", elem.attr("id"));//duhj
            param.put("ELEMENT_ID", elem.attr("element_id"));
            param.put("ELEMENT_TYPE_CODE", elem.attr("element_type_code"));
            param.put("MODIFY_TAG", "0");
            param.put("FORCE_TAG", elem.attr("forceTag"));
            param.put("DISABLED", elem.attr("disabled"));

            var fee = elem.attr("fee");
            if (typeof fee !== "undefined") {
                var feeTypeCode = elem.attr("fee_type_code");
                param.put("FEE", fee);
                param.put("FEE_MODE", elem.attr("fee_mode"));
                param.put("FEE_TYPE_CODE", feeTypeCode);
                param.put("PAY_MODE", elem.attr("pay_mode"));
                param.put("IN_DEPOSIT_CODE", elem.attr("in_deposit_code"));
                param.put("OUT_DEPOSIT_CODE", elem.attr("out_deposit_code"));
                param.put("PAYMENT_ID", feeTypeCode);
            }
        },

        // 组合包拼串
        spDecodeCombineParam: function (param) {
            marketActivityModule.spAddFeeItem(param);
        },

        // 服务拼串
        spDecodeServiceParam: function (param, elem) {
            param.put("START_DATE", elem.attr("start_date"));
            param.put("END_DATE", elem.attr("end_date"));
            marketActivityModule.decodeAttrParam(param, elem);

        },

        // 优惠拼串
        spDecodeDiscntParam: function (param, elem) {
            param.put("START_DATE", elem.attr("start_date"));
            param.put("END_DATE", elem.attr("end_date"));
            marketActivityModule.decodeAttrParam(param, elem);
        },

        // 平台业务拼串
        spDecodePlatsvcParam: function (param, elem) {
			param.put("ELEMENT_ID", elem.attr('element_id'));
        },

        // 预存拼串
        spDecodeDepositParam: function (param, elem) {
//            param.put("START_DATE", elem.attr("startDate"));
//            param.put("END_DATE", elem.attr("endDate"));
//            param.put("SERIAL_NUMBER_B", elem.attr("serialNumberB"));
//            param.put("GIFT_USER_ID", elem.attr('userIdA'));            
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			param.put("SERIAL_NUMBER_B", elem.attr('serial_number_b'));
			param.put("GIFT_USER_ID", elem.attr('user_id_a'));
            marketActivityModule.spAddFeeItem(param);
            marketActivityModule.disabledDepositPlusItem(param.get('ELEM_KEY'), false);

        },

		disabledDepositPlusItem: function(cbid, flag) {
			var checkboxObj = $("#"+cbid);
			var giftUseTag = checkboxObj.attr('gift_use_tag');
			var depositType = checkboxObj.attr('deposit_type');
			if(depositType == '2')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_FEE";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
			if(giftUseTag == '1')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_DEPOSIT_USER_ID";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
		},
        // 实物拼串
        spDecodeGoodsParam: function (param, elem) {
            param.put("HAS_CHECKED", elem.attr("has_check"));
            if (elem.attr("resTypeCode") === "S") {
                var resCode = $("#SELECT_GOODS_" + elem.attr("index") + "_"
                        + elem.attr("element_id")).val();
                param.put("RES_CODE", resCode);
            }
            marketActivityModule.spAddFeeItem(param);
        },

        // 信用度拼串
        spDecodeCreditParam: function (param, elem) {        	
			param.put("CREDIT_VALUE", elem.attr('credit_value'));
			param.put("CREDIT_GIFT_MONTHS", elem.attr('credit_gift_months'));
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
            marketActivityModule.spAddFeeItem(param);
        },
        
        // 积分拼串
        spDecodeScoreParam: function (param, elem) {
			param.put("SCORE_VALUE", elem.attr('score_value'));
            //param.put("SCORE_VALUE", $("#" + elem.attr("inputId")).val());
            marketActivityModule.spAddFeeItem(param);
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
                marketActivityModule.insertFee(tradeTypeCode, feeMode, feeTypeCode, fee, elementId);
            else
                marketActivityModule.insertFee(tradeTypeCode, feeMode, feeTypeCode, fee);
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
            //$.feeMgr.removeFee(this.packageId, this.packageName, "240", feeMode, feeTypeCode, elemId, this.userId);

            else
                $.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode);
            //$.feeMgr.removeFee(this.packageId,this.packageName, "240", feeMode, feeTypeCode, null, this.userId);
        },

        insertFee: function (tradeTypeCode, feeMode, feeTypeCode, fee, elementId) {
            var feeData = $.DataMap();
			feeData.put("MERCH_ID", this.packageId);//duhj
			feeData.put("MERCH_NAME", this.packageName);//duhj

            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
            feeData.put("MODE", feeMode);
            feeData.put("CODE", feeTypeCode);
            feeData.put("FEE", fee);
            if (typeof elementId !== "undefined" && elementId !== "") {
                feeData.put("ELEMENT_ID", elementId);
            }
            //$.feeMgr.insertFee(feeData);duhj
			// 为了防止费用配置没加载完就去校验
			setTimeout(function(){$.feeMgr.insertFee(feeData);}, 300);

        },

        // 构建元素属性值
        decodeAttrParam: function (param, elem) {
            var attrParam = $("#" + elem.attr("id") + "_ATTR").val();
            if (typeof attrParam !== "undefined" && attrParam !== "") {
                param.put("ATTR_PARAM", $.DatasetList(attrParam));
            }
        },

		checkDepositGiftUser: function(obj){
			obj = $(obj);
			$("#"+obj.attr('chkboxid')).attr('user_id_a', '');
			$("#"+obj.attr('chkboxid')).attr('serial_number_b', '');
			var elem = marketActivityModule.spGetElem(obj.attr('chkboxid'));
			if(elem != null){
				elem.put('GIFT_USER_ID', '');
				elem.put('SERIAL_NUMBER_B', '');
			}
			
			if (obj.val() == ''){
				return false;
			}
			
			var userId = $('#SALEACTIVE_USER_ID').val();
			var param = '&USER_ID='+userId;
			param += '&GIFT_SERIAL_NUMBER='+obj.val();
			param += '&PRODUCT_ID='+$("#SALEACTIVE_PRODUCT_ID").val();
			param += "&PACKAGE_ID="+$("#SALEACTIVE_PACKAGE_ID").val();
			param += '&SPEC_TAG=checkDepositGiftUser';
			param += "&EPARCHY_CODE=" + $('#SALEDEPOSIT_EPARCHY_CODE').val();
			ajaxSubmit(null, null, param, $('#SALEDEPOSIT_COMPONENT_ID').val(),
				function(d) {marketActivityModule.afterCheckDepositGiftUser(d, obj)}, 
				function(rscode, rsinfo) {marketActivityModule.checkDepositGiftUserFail(rscode, rsinfo, obj)});
		},

		afterCheckDepositGiftUser: function(ajaxData, obj){
			var elem = marketActivityModule.spGetElem(obj.attr('chkboxid'));
			if(elem != null)
			{
			    var confirmSet = ajaxData.get("TIPS_TYPE_CHOICE");
				var warnSet = ajaxData.get("TIPS_TYPE_TIP");
				if(confirmSet && confirmSet.length>0){
					var flag = true;
					confirmSet.each(function(item, index, totalCount){
						if(!window.confirm(item.get("TIPS_INFO"))){
							flag = false;
							return false;
						}
					});
					if(!flag) return false;
				}
				if(warnSet && warnSet.length>0){
					warnSet.each(function(item, index, totalCount){
						window.alert(item.get("TIPS_INFO"));
					});
				}
				debugger;
				elem.put('GIFT_USER_ID', ajaxData.get(0).get('GIFT_USER_ID'));
				elem.put('SERIAL_NUMBER_B', obj.val());
				elem.put('GIFT_START_DATE', ajaxData.get(0).get('GIFT_START_DATE'));
				elem.put('GIFT_END_DATE', ajaxData.get(0).get('GIFT_END_DATE'));
				alert('赠送号码校验成功');
			}
		},

		checkDepositGiftUserFail: function(rscode, rsinfo, obj){
			obj.val('');	
			var elem = marketActivityModule.spGetElem(obj.attr('chkboxid'));
			elem.put("SERIAL_NUMBER_B", "");
			elem.put("GIFT_USER_ID", "");
			elem.put('GIFT_START_DATE',"");
			elem.put('GIFT_END_DATE',"");
			alert('赠送号码校验失败：'+rsinfo);
		},

//        // 根据checkBoxId获取元素
//        spGetElem: function (id) {
//            gElems.each(function (item) {
//                if (item.get("ELEM_KEY") === id) return item;
//            });
//            return null;
//        },
		/*根据checkId获取一个元素*/
		spGetElem: function(cbId) {
			var cb = $('#'+cbId);
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					return item;
				}
			}
			return null;
		},

        selectGiftGoods: function (obj) {
            var $obj = $(obj);
            var saleGoods = marketActivityModule.spGetGoodsByGoodsId($obj.attr("goodsId"));
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
            marketActivityModule.spSetResCheckState("FALSE", input.attr("goodsId"));

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
                    marketActivityModule.spAfterCheckResInfo(ajaxData, input);
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("终端预占失败", error_info);
                });
        },

        spSetResCheckState: function (state, goodsId) {
            var saleGoods = marketActivityModule.spGetGoodsByGoodsId(goodsId);
            if (saleGoods) {
                saleGoods.put("HAS_CHECKED", state);
                marketActivityModule.spAddElem(saleGoods);
            }
        },

        spGetGoodsByGoodsId: function (goodsId) {
            var rt = null;
            gElems.each(function (item) {
                if (item.get("ELEM_KEY").indexOf("GOODS") >= 0
                        && item.get("ELEMENT_ID") === goodsId) {
                    rt = item;
                    return false; // break the gElems.each() loop
                }
            });
            return rt;
        },

        spAfterCheckResInfo: function (data, obj) {
            $.endPageLoading();
            if (data.length <= 0) {
                MessageBox.error("资源校验失败！");
                return;
            }

            var saleGoods = marketActivityModule.spGetGoodsByGoodsId(obj.attr("goodsId"));
            saleGoods.put("HAS_CHECKED", "TRUE");
            saleGoods.put("RES_CODE", obj.val());
            saleGoods.put("SALE_STAFF_ID", $("#GOODS_" + obj.attr("index") + "_STAFF_ID").val());
            marketActivityModule.spAddElem(saleGoods);
            obj.attr("disabled", true);
            MessageBox.success("资源校验成功！");
        },

        showAttr: function (obj) {
            var $obj = $(obj);
            var itemIndex = $obj.attr("checkboxId");
            var element = marketActivityModule.spGetElem(itemIndex);
            if (element === null) {
                return;
            }

            var params = "&ELEMENT_ID=" + $obj.attr("elementId")
                    + "&ELEMENT_TYPE_CODE=" + $obj.attr("elementTypeCode")
                    + "&ITEM_INDEX=" + itemIndex;
            $.ajax.submit(null, null, params, "saleElementAttr",
                function () {
                    marketActivityModule.afterShowAttr(obj);
                });
        },

        afterShowAttr: function (obj) {
            var $obj = $(obj);
            var element = marketActivityModule.spGetElem($obj.attr("checkboxId"));
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
			//$("#saleFeeItemPanel").css("display","");

        },

        hideAttr: function () {
            $("#elementPanel").css("display", "none");//duhj
			//$("#saleFeeItemPanel").css("display","none");/sx

        },

        confirmAttr: function (itemIndex) {
            var element = marketActivityModule.spGetElem(itemIndex);
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
        },
        
		showOrHideGoodInfo : function() {
			
			var goodsInfo = $("#GOODS_INFO").val();
			//var viceImeiNo = $("#VICE_IMEI_NO").val();
			var newImei = $("#NEW_IMEI").val();

			if ((typeof(goodsInfo) == "undefined" || goodsInfo == "")) {
				$('#showGoodsInfo').css('display', 'none');
			} else {
				$('#showGoodsInfo').css('display', '');
			}

			if (typeof(goodsInfo) != "undefined" && goodsInfo != "") {
				$('#GoodsInfoPart').css('display', '');
			} else {
				$('#GoodsInfoPart').css('display', 'none');
			}
			if (typeof(newImei) != "undefined" && newImei != "") {
				$('#imeiQuery').css('display', '');
			} else {
				$('#imeiQuery').css('display', 'none');
			}
		},
		
		openDetailPage: function()
		{
			$("#detailTitle").css("display","");
			$("#detailTitle").attr("class","on");
			$("#detailContent").css("display","");
			$("#listContent").css("display","none");
			$("#listTitle").attr("class","");
		},
		activeCheckFail: function(rscode, rsinfo){
			$.endPageLoading();
			MessageBox.alert("错误提示", "业务规则报错！", function(btn){closeNav();}, null, "活动校验不通过:"+rsinfo);
		}
        
    });
})();
