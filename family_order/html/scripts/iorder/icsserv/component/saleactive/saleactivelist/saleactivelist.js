
if (typeof SaleActiveList === "undefined") {
    window["SaleActiveList"] = function () {};
    var saleActiveList = new SaleActiveList();
}
 
(function() {
    $.extend(SaleActiveList.prototype, {
        packageList: null,

        renderComponent: function (params) {
            var popupId = $("#SALEACTIVELIST_POPUP_ID").val();
            var componentId = $("#SALEACTIVELIST_COMPONENT_ID").val();
            var eparchyCodeCompId = $("#SALEACTIVELIST_EPARCHY_CODE_COMPID").val();

            saleActiveList.clearSaleActiveList();
            saleActiveModule.clearSaleActiveModule();

            params += "&EPARCHY_CODE=" + $("#" + eparchyCodeCompId).val()
                    + "&SALEACTIVELIST_EPARCHY_CODE_COMPID=" + eparchyCodeCompId;
            $.beginPageLoading("营销活动列表查询中。。。");
            ajaxSubmit(null, null, params, componentId,
                function (ajaxData) {
                    $.endPageLoading();
                    saleActiveList.packageList = $("#saleActiveListUl > li");
                    showPopup(popupId, componentId, true);
                    var allFail = ajaxData.get("ALL_FAIL");
                    if (allFail === "true") {
                        MessageBox.alert("用户不满足参与该活动的条件，详细原因请点击相应营销包！");
                    } else {
                        // 老用戶免费领取4G手机需求 chenxy3
                        saleActiveList.checkProdNeedOldCustSn($("#SALE_PRODUCT_ID").val());
                    }
                    // iPhone6活动特殊处理 20141030
                    var iPhone6XResultCode = ajaxData.get("IPHONE6_X_RESULTCODE");
                    if (typeof iPhone6XResultCode !== "undefined" && iPhone6XResultCode === "-1") {
                        $("#IPHONE6_IMEI").val("");
                        MessageBox.alert(ajaxData.get("IPHONE6_X_RESULTINFO"));
                    }
                    var goodsInfo = ajaxData.get("GOODS_INFO");
                    if (typeof goodsInfo !== "undefined") {
                        saleActive.showGoodsInfo(goodsInfo);
                    } else {
                        saleActive.hideGoodsInfo();
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("活动列表查询失败", error_info);
                    saleActive.saleActiveFromWelcomePageURLEnd();
                    saleActive.saleActiveFromHotRecommListEnd();
                });
        },

        checkByPackageId: function (obj) {
            var $obj = $(obj);
            var packageId = $obj.attr("packageId");
            var packageName = $obj.attr("packageName");
            var productId = $obj.attr("productId");
            var campnType = $obj.attr("campnType");
            var userId = $("#SALEACTIVELIST_USER_ID").val();
            var custId = $("#SALEACTIVELIST_CUST_ID").val();
            var errorFlag = $("#SALEACTIVE_ERROR_FLAG").val();
            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
            
            if (packageId !== "") {
                var params = "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&SERIAL_NUMBER=" + serialNumber
                        + "&USER_ID=" + userId + "&CUST_ID=" + custId + "&SPEC_TAG=" + "checkByPkgId"
                        + "&CHECK_TYPE=" + "CHECKPACK";
                $.beginPageLoading("营销包校验中。。。");
                ajaxSubmit(null, null, params, null,
                    function () {
                        $("#SALE_PACKAGE").html(packageName);
                        $("#SALE_PACKAGE_ID").val(packageId);
                        hidePopup("saleActiveListPopup");
                        if (errorFlag === "1") {
                            saleActiveList.selectSaleActiveAction(obj);
                        } else {
                            saleActiveList.checkPackNeedOldCustSn(productId, packageId, obj, serialNumber);
                        }
                        $.endPageLoading();
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("营销包校验失败", error_info);
                    });
            }
        },

        selectSaleActiveAction: function (o) {
            var packageId, productId, campnType,
                newImei = $("#SALEACTIVE_IMEI").val(),
                deviceModelCode = $("#DEVICE_MODEL_CODE").val();
            if (o) {
                var $obj = $(o);
                packageId = $obj.attr("packageId");
                productId = $obj.attr("productId");
                campnType = $obj.attr("campnType");
            } else {
                packageId = $("#SALE_PACKAGE_ID").val();
                productId = $("#SALE_PRODUCT_ID").val();
                campnType = $("#SALE_CAMPN_TYPE").val();
            }

            saleActiveModule.renderComponent(packageId, productId, campnType, newImei, deviceModelCode);
        },

        /**
         * 老用户免费领取4G手机业务
         * 判断产品是否需要录入老用户手机号
         * chenxy3 2015-09-21
         */
        checkProdNeedOldCustSn: function (productId) {
            var params = "&PRODUCT_ID=" + productId
                    + "&CHECK_TYPE=" + "CHECKPROD";
            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
	            	var creditPurchases = ajaxData.get("CREDIT_PURCHASES");
	            	if (creditPurchases === "1") {
            		 $("#CREDIT_PURCHASES").val("1") ;
     				 $("#CREDIT_PURCHASES_FLG").val("1") ;
     				 $("#CREDIT_PURCHASES").attr("checked",true);
	            	}else{
			    $("#CREDIT_PURCHASES").val("0") ;
     				 $("#CREDIT_PURCHASES_FLG").val("0") ;
     				 $("#CREDIT_PURCHASES").attr("checked",false);
			}
                    var ifNeed = ajaxData.get("IF_NEED");
                    if (ifNeed === "Y") {
                        $("#IF_NEW_USER_ACTIVE").val(ajaxData.get("IF_NEW_USER_ACTIVE"));
                        forwardPopup("saleActiveListPopup","checkProdOldCustSnItem");
                        saleActiveList.disableSaleActiveListAfterPopupProdCheck();
                        return;
                    }
                    saleActiveList.autoSelectPackageFromSaleActiveList();
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    saleActiveList.afterPopupProdCheckFailure(false);
                    saleActive.saleActiveFromWelcomePageURLEnd();
                    saleActive.saleActiveFromHotRecommListEnd();
                });
        },

        popupCheckProdOldCustSn: function () {
            var el = "oldCustSnForProd",
                $obj = $("#" + el),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&CHECK_SERIAL_NUMBER=" + $obj.val();

            if ($("#IF_NEW_USER_ACTIVE").val() === "NEW") {
                params += "&AUTH_SERIAL_NUMBER=" + serialNumber
                        + "&CHECK_TYPE=" + "NEW_USER_ACTIVE";
                ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                    function (ajaxData) {
                        /**
                         * 新号码校验：
                         * 1、是否办理过该活动                                 SN_HAVE_PRODUCT   Y:办理过  N:没办理过
                         * 2、0存折不能大于0                                  SN_FEE_TYPE       Y:大于0   N:不大于0
                         * 3、不能是多终端共享业务数据                          SHARE_INFO_TYPE   Y:属于    N:不属于
                         * 4、不能统一付费业务数据                             RELATION_UU_TYPE  Y:属于    N:不属于
                         * 5、与新号码的身份证是否相同                          PSPT_ID_SAME      Y:相同    N:不相同
                         * 6、新号码存在红海行动营销活动，不能再办理感恩大派送活动 PSPT_ID_SAME      Y:相同    N：不相同
                         */
                        var open48Tag = ajaxData.get("OPEN_48_HOUR"),
                            haveTag = ajaxData.get("SN_HAVE_PRODUCT"),
                            shareTag = ajaxData.get("SHARE_INFO_TYPE"),
                            relaTag = ajaxData.get("RELATION_UU_TYPE"),
                            psptIdTag = ajaxData.get("PSPT_ID_SAME"),
                            feeTag = ajaxData.get("SN_FEE_TYPE"),
                            activeTag = ajaxData.get("HAVE_ACTIVE"),
						    mainCardTag=ajaxData.get("MAIN_CARD"),
						    familyTag=ajaxData.get("FAMILY_CARD"),
						    widenetTag=ajaxData.get("WIDENET_TYPE");
                            familyOldTag=ajaxData.get("FAMILY_CARD_OLD");
                            widenetOldTag=ajaxData.get("WIDENET_TYPE_OLD");
                            discntTag=ajaxData.get("HAVE_DISCNT");

                        if (open48Tag == "N") {
                            $.validate.alerter.one($obj[0], "输入的用户手机号不属于7天内开户的号码，不允许办理该业务！");
                            return;
                        }
                        if(mainCardTag == "N"){
		                	$.validate.alerter.one($obj[0],"输入的号码对应的老号码没有作为主卡与新号码办统一付费业务  ，不能办理该活动！");
		            		   return;
			        	}
			        	if(familyTag == "N"){
			        		$.validate.alerter.one($obj[0],"输入的号码没有办理亲亲网业务  ，不能办理该活动！");
		            	 	   return;
			        	}
			        	if(widenetTag == "N"){
			        		$.validate.alerter.one($obj[0],"输入的号码没有办理宽带业务(或预约)  ，不能办理该活动！");
		            		   return;
			        	}
			        	if(familyOldTag == "N"){
			        		$.validate.alerter.one($obj[0],"老号码没有办理亲亲网业务  ，不能办理该活动！");
		            	 	   return;
			        	}
			        	if(widenetOldTag == "N"){
			        		$.validate.alerter.one($obj[0],"老号码没有办理宽带业务(或预约)  ，不能办理该活动！");
		            		   return;
			        	}
                        if (haveTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码已经办理过该活动，不能再次办理！");
                            return;
                        }
                        if (shareTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码办理过多终端共享业务，不能办理该活动！");
                            return;
                        }
                        if (relaTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码办理过统一付费业务，不能办理该活动！");
                            return;
                        }
                        if (psptIdTag === "N") {
                            $.validate.alerter.one($obj[0], "输入的号码与主号码的身份证不一致，不能办理该活动！");
                            return;
                        }
                        if (feeTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码预存款大于0，不能办理该活动！");
                            return;
                        }
                        if (activeTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码存在红海行动营销活动，不能办理该活动！");
                            return;
                        }
			if (discntTag === "Y"){
                        	$.validate.alerter.one($obj[0], "输入的号码存在自由选8元套餐优惠,不能办理该活动！");
                            return;
                        }
                        MessageBox.success("校验通过！");
                        saleActiveList.afterPopupProdCheckSuccess();
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        saleActiveList.afterPopupProdCheckFailure(true);
                    });
            } else {
                params += "&CHECK_TYPE=" + "CHECKSN";
                ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                    function (ajaxData) {
                        var checkSnTag = ajaxData.get("SN_HAVE_PRODUCT"),
                            checkSnUse = ajaxData.get("SN_HAVE_USE");
                        if (checkSnTag === "N") {
                            $.validate.alerter.one($obj[0], "输入的老用户手机号未办理指定活动，请输入其他号码！");
                        } else {
                            if (checkSnUse === "Y") {
                                $.validate.alerter.one($obj[0], "该号码已经被其他号码校验过，请输入新号码校验！");
                            } else if (checkSnUse === "Z") {
                                $.validate.alerter.one($obj[0], "老用户证件和新用户证件号码不一致，不能办理！");
                            } else {
                                MessageBox.success("成功登记老用户手机号！");
                                saleActiveList.afterPopupProdCheckSuccess();
                            }
                        }
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        saleActiveList.afterPopupProdCheckFailure(true);
                    });
            }
        },

        /**
         * 判断产品包是否需要录入老用户手机号
         * zhangxing3
         */
        checkPackNeedOldCustSn: function (productId, packageId, obj, serialNumber) {
            var params = "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&SERIAL_NUMBER=" + serialNumber
                    + "&CHECK_TYPE=" + "CHECKPACK";
            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var ifNeed = ajaxData.get("IF_NEED");
                    if (ifNeed === "Y") {
                        showPopup("checkPackOldCustSnPopup", "checkPackOldCustSnItem", true);
                    } else if(ifNeed==="SIM"){
                    	showPopup("sendSMSCodePopup", "sendSMSCodeItem", true);//点击红包雨营销包时，弹出校验码的输入框--wangsc10--20190311
                    }else{
                    	saleActiveList.selectSaleActiveAction(obj);
                    }
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    saleActiveList.afterPopupPackCheckFailure();
                })
        },
        
        popupCheckPackOldCustSn: function () {
            var el = "oldCustSnForPack",
                $obj = $("#" + el),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&PACKAGE_ID=" + $("#SALE_PACKAGE_ID").val()
                    + "&CHECK_SERIAL_NUMBER=" + $obj.val()
                    + "&CHECK_TYPE=" + "CHECKSN";

            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var checkSnTag = ajaxData.get("SN_HAVE_PRODUCT");
                    var checkSnUse = ajaxData.get("SN_HAVE_USE");
                    var checkSnMsg = ajaxData.get("SN_MSG");

                    if (checkSnTag === "N") {
                        $.validate.alerter.one($obj[0], "输入的校验用户手机号未办理指定活动，请输入其他号码！");
                    } else if (checkSnTag === "K") {
                        $.validate.alerter.one($obj[0], checkSnMsg);
                    } else {
                        if (checkSnUse === "Y") {
                            $.validate.alerter.one($obj[0], "该号码已经被其他号码校验过，请输入新号码校验！");
                        } else if (checkSnUse === "Z") {
                            $.validate.alerter.one($obj[0], "办理用户证件和校验用户证件号码不一致，不能办理！");
                        } else {
                            MessageBox.success("成功登记校验用户手机号！");
                            saleActiveList.afterPopupPackCheckSuccess();
                        }
                    }
                }, function (error_code, error_info) {
                    MessageBox.error(error_info);
                    saleActiveList.afterPopupPackCheckFailure();
                });
        },
        
        //点击校验码弹出框确定按钮--wangsc10-20190311--start
        popupqueryProdRedMoney: function (obj) {
            var el = "RED_SMS_CODE",
            $obj = $("#" + el);
	        if ($obj.val() === null || "" === $obj.val()) {
	            $.validate.alerter.one($obj[0], "验证码不能为空！");
	            return;
	        }
//	        var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
//	                + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
//	                + "&PACKAGE_ID=" + $("#SALE_PACKAGE_ID").val()
//	                + "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
//	                + "&SMS_CODE=" + $obj.val();
//	
//	        ajaxSubmit(null, "queryProdRedMoney", params, null,
//	            function (ajaxData) {
//	        		var RED_CODE = ajaxData.get("RED_CODE");
//	        		var RED_YE = ajaxData.get("RED_YE");
//	        		if (RED_CODE === "1") {
//                        $.validate.alerter.one($obj[0], "红包余额"+RED_YE+"不足！");
//                    }else{
//                    	$("#SMS_CODE").val(ajaxData.get("SMS_CODE"));
//                    	hidePopup("sendSMSCodePopup");
//                    	saleActiveList.afterPopupPackCheckSuccess();
//                    }
//	            }, function (error_code, error_info) {
//	                MessageBox.error(error_info);
//	                saleActiveList.afterPopupPackCheckFailure();
//	            });
	        $("#SMS_CODE").val($obj.val());
        	hidePopup("sendSMSCodePopup");
        	saleActiveList.afterPopupPackCheckSuccess();
	    },
	    //end
	    
        disableSaleActiveListAfterPopupProdCheck: function () {
            var $ul = $("#saleActiveListUl");
            if ($.browser.msie) {
                $ul.css("display", "none");
            } else {
                $ul.addClass("e_dis");
            }
        },

        afterPopupProdCheckSuccess: function () {
            backPopup(this);
            var $ul = $("#saleActiveListUl");
            if ($.browser.msie) {
                $ul.css("display", "");
            } else {
                $ul.removeClass("e_dis");
            }
            saleActiveList.autoSelectPackageFromSaleActiveList();
            if($.os.phone){//对于手机端返回营销活动列表
				backPopup("saleActiveListPopup");
			}
        },

        afterPopupProdCheckFailure: function (hasTwoLayers) {
            if (hasTwoLayers) backPopup(this);
            hidePopup("saleActiveListPopup");
            $("#SalePackageBtn").unbind("tap");
        },

        afterPopupPackCheckSuccess: function () {
            hidePopup(this);
            saleActiveList.selectSaleActiveAction();
        },

        afterPopupPackCheckFailure: function () {
            hidePopup(this);
            $("#SalePackageBtn").unbind("tap");
        },

        clearSaleActiveList: function () {
            $("#saleActiveListUl").html("");
            $("#SALE_PACKAGE").html("");
            $("#SALE_PACKAGE_ID").val("");
        },

        // 从活动列表自动选择目标活动
        autoSelectPackageFromSaleActiveList: function () {
            var packageId;
            if (saleActive.fromWelcomePageURL()) { // 首页"热门活动"和"推荐活动"跳转流程
                packageId = $("#PACKAGE_ID_FROM_URL").val();
                saleActiveList.packageList.each(function () {
                    if ($(this).attr("packageId") === packageId) {
                        $(this).trigger("tap");
                        saleActive.saleActiveFromWelcomePageURLEnd();
                        return false;
                    }
                });
            } else if (saleActive.fromHotRecommList()) { // 页面"热门活动"和"推荐活动"跳转流程
                packageId = $("#PACKAGE_ID_FROM_HOT_RECOMM").val();
                saleActiveList.packageList.each(function () {
                    if ($(this).attr("packageId") === packageId) {
                        $(this).trigger("tap");
                        saleActive.saleActiveFromHotRecommListEnd();
                        return false;
                    }
                });
            }
        },

        // 根据营销包名称关键字过滤
        filterByPackageName: function () {
            var text = $("#SEARCH_CONTENT").val();
            if (saleActiveList.packageList !== null) {
                saleActiveList.packageList.css("display", "");
                saleActiveList.packageList.each(function () {
                    if ($(this).attr("packageName").indexOf(text) === -1) {
                        $(this).css("display", "none");
                    }
                });
            }
        }
    });
})();