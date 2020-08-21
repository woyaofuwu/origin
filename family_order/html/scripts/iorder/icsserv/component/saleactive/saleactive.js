
if (typeof SaleActive === "undefined") {
	window["SaleActive"] = function () {};
	var saleActive = new SaleActive();
}

(function(){
	$.extend(SaleActive.prototype, {
        clearSaleActive: function () {
            saleActive.setQueryValidSaleActiveFlag("0");
            saleActive.setQueryHotRecommSaleActiveFlag("0")
        },

	    renderComponent: function (userId, custId, acctDay, firstDate, nextAcctDay, nextFirstDate, labelId) {
	        var params = "";
	        if (typeof userId !== "undefined") params += "&USER_ID=" + userId;
	        if (typeof custId !== "undefined") params += "&CUST_ID=" + custId;
	        if (typeof acctDay !== "undefined") params += "&ACCT_DAY=" + acctDay;
	        if (typeof firstDate !== "undefined") params += "&FIRST_DATE=" + firstDate;
	        if (typeof nextAcctDay !== "undefined") params += "&NEXT_ACCT_DAY=" + nextAcctDay;
	        if (typeof nextFirstDate !== "undefined") params += "&NEXT_FIRST_DATE=" + nextFirstDate;

	        params += "&NEED_USER_ID=" + $("#SALEACTIVE_NEED_USER_ID").val() + "&LABEL_ID=" + labelId
                    + "&EPARCHY_CODE=" + $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val()
                    + "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val()
                    + "&MUL_CHANNEL_TAG="+$("#MUL_CHANNEL_TAG").val();
	        ajaxSubmit(null, null, params, $("#SALEACTIVE_COMPONENT_ID").val(),
                function () {
                    $("#SalePackageBtn").tap(function () { // 绑定营销包右侧弹窗事件
                        showPopup($("#SALEACTIVELIST_POPUP_ID").val(), $("#SALEACTIVELIST_COMPONENT_ID").val());
                    });
                    saleActive.clearSaleActive();
                    saleActive.queryAllAvailabelProducts();
                });
        },

        queryAllAvailabelProducts: function () {
            var params = "&LABEL_ID=" + $("#LABEL_ID").val()
                    + "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&EPARCHY_CODE=" + $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val()
                    + "&SPEC_TAG=" + "queryAllProducts"
                    + "&MUL_CHANNEL_TAG="+$("#MUL_CHANNEL_TAG").val();
            saleActive.queryProductAjaxSubmit(params, "1");
        },

        queryProductByType: function (obj) {
            var campnType = $(obj).val();
            if (campnType !== "") {
                var params = "&CAMPN_TYPE=" + campnType + "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                        + "&EPARCHY_CODE=" + $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val()
                        + "&SPEC_TAG=" + "refreshProduct"
                        + "&MUL_CHANNEL_TAG="+$("#MUL_CHANNEL_TAG").val();
                saleActive.queryProductAjaxSubmit(params);
            }
        },

        queryProductAjaxSubmit: function (params, passTip) {
            $.beginPageLoading("营销产品列表查询中。。。");
            ajaxSubmit(null, null, params, $("#SALEACTIVE_COMPONENT_ID").val(),
                function (productList) {
                    var tipFlag = "",
                        tipInfo = "";
                    productList.each(function (product) {
                        tipFlag = product.get("TIP_FLAG");
                        tipInfo = product.get("TIP_INFO");
                        var productDesc = product.get("UP_CATALOG_NAME") + " | " + product.get("PRODUCT_NAME"),
                            productTitle = product.get("PRODUCT_NAME") + " | " + product.get("UP_CATALOG_ID");
                        product.put("PRODUCT_NAME", productDesc);
                        product.put("PRODUCT_TITLE", productTitle);
                    });

                    $("#SALE_PRODUCT_SELECT_CONTAINER").empty();
                    if (window["SALE_PRODUCT_ID"]) {
                        window["SALE_PRODUCT_ID"].destroy();
                        window["SALE_PRODUCT_ID"] = null;
                    }
                    $.Select.append(
                        "SALE_PRODUCT_SELECT_CONTAINER", {
                            name: "SALE_PRODUCT_ID",
                            textField: "PRODUCT_NAME",
                            valueField: "PRODUCT_ID",
                            titleField: "PRODUCT_TITLE",
                            inputable: true,
                            disabled: false,
                            nullable: "no",
                            desc: "营销方案"
                        }, productList
                    );
                    $("#SALE_PRODUCT_ID").bind("change", saleActive.checkByProduct);

                    saleActiveList.clearSaleActiveList();
                    saleActiveModule.clearSaleActiveModule();
                    $.endPageLoading();

                    var campnType = $("#SALE_CAMPN_TYPE").val(),
                        $IMEIQuery = $("#IMEIQueryPart"),
                        $saleStaffId = $("#SaleStaffIdPart"),
                        $isiPhone6 = $("#IsiPhone6Part"),
                        $iPhone6IMEI = $("#iPhone6IMEIPart");

                    // iPhone6 相关处理
                    $isiPhone6.css("display", "none");
                    $iPhone6IMEI.css("display", "none");
                    $("#IPHONE6_IMEI").val("");

                    if (saleActive.isTerminalCampnType(campnType)) {
                        $IMEIQuery.css("display", "").addClass("required");
                        $("#SalePackageBtn").tap(saleActive.queryPackages); // 绑定营销包右侧弹窗事件
                    } else {
                        $IMEIQuery.css("display", "none").removeClass("required");
                        $saleStaffId.css("display", "none");
                        $("#NEW_IMEI").val("");
                        $("#SALE_STAFF_ID").val("");

                        // iPhone6 相关处理
                        if (saleActive.isiPhone6CampnType(campnType)) {
                            $isiPhone6.css("display", "");
                            $iPhone6IMEI.css("display", "");
                        }
                    }

                    if (passTip !== "1" && tipFlag === "1") {
                        MessageBox.alert(tipInfo);
                    }

                    saleActive.autoSelectProductFromSaleAcitveProducts();
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("营销产品列表查询失败", error_info);
                });
        },

        isTerminalCampnType: function (campnType) {
            var terminalCampnType = "YX03|YX06|YX07|YX08|YX09";
            return campnType !== "" && terminalCampnType.indexOf(campnType) > -1;
        },

        isiPhone6CampnType: function (campnType) {
            var iPhone6CampnType = "YX11";
            return campnType !== "" && iPhone6CampnType.indexOf(campnType) > -1;
        },
        

        checkCreditPurchases: function () {
            var productId = $("#SALE_PRODUCT_ID").val();
            var params = "&PRODUCT_ID=" + productId  + "&SPEC_TAG=" + "checkCreditPurchases";

            $.beginPageLoading("信用购机校验中。。");
            ajaxSubmit(null, null, params, null,
                    function (data) {
                		$.endPageLoading();
            			if(data.get("CREDIT_PURCHASES")==='1'){
                            $("#CREDIT_PURCHASES_PART").css("display", "");
            				$("#CREDIT_PURCHASES").val("1") ;
            			}else{
                            $("#CREDIT_PURCHASES_PART").css("display", "none");
            				$("#CREDIT_PURCHASES").val('0') ;
            			}
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                    });
            
        },
        chooseNp: function(){
        	var val = $("#CREDIT_PURCHASES_FLG").val();
        	if('1'==val){
        		$("#CREDIT_PURCHASES").attr("checked",true);
        		MessageBox.alert("该活动只能进行信用购机！");
        		return;
        	}
        	
        	var checkNP=$("#CREDIT_PURCHASES").attr("checked");
        	if(checkNP){
        		$("#CREDIT_PURCHASES").val("1");
        	}else{
        		$("#CREDIT_PURCHASES").val("0");
        	}
        },
        checkByProduct: function () {
            var productId = $("#SALE_PRODUCT_ID").val(),
                userId = $("#SALEACTIVE_USER_ID").val(),
                custId = $("#SALEACTIVE_CUST_ID").val();

            saleActiveList.clearSaleActiveList();
            saleActiveModule.clearSaleActiveModule();

            if (productId !== "") {
                var params = "&PRODUCT_ID=" + productId + "&USER_ID=" + userId
                        + "&CUST_ID=" + custId + "&SPEC_TAG=" + "checkByProduct";
                $.beginPageLoading("营销产品校验中。。。");
                ajaxSubmit(null, null, params, null,
                    function () {
                        $.endPageLoading();
                        // REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
                        saleActive.giftSaleActive();
                        //信用购机
                        //saleActive.checkCreditPurchases();

                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("营销产品校验失败", error_info);
                        saleActive.saleActiveFromWelcomePageURLEnd();
                        saleActive.saleActiveFromHotRecommListEnd();
                    });
            }
        },
        
        giftSaleActive: function () {
            var params = "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&SPEC_TAG=" + "giftSaleActive";
            $.beginPageLoading("特殊活动校验中。。。");
            ajaxSubmit(null, null, params, null,
                function (ajaxData) {
                    $.endPageLoading();
                    if (ajaxData.get("ProductAttr") === "1") { // 礼品码活动
                        $("#GiftCodePart").css("display", "");
                        $("#IS_GIFT_ACTIVE").val("1");
                    } else {
                        $("#GiftCodePart").css("display", "none");
                        $("#IS_GIFT_ACTIVE").val("0");
                    }
                    saleActive.queryPackages();
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error("特殊活动校验失败", error_info);
                    saleActive.saleActiveFromWelcomePageURLEnd();
                    saleActive.saleActiveFromHotRecommListEnd();
                });
        },

        queryPackages: function () {
	        var $campnType = $("#SALE_CAMPN_TYPE"),
	            productId = $("#SALE_PRODUCT_ID").val(),
                $searchKeyword = $("#SEARCH_KEYWORD"),
                searchKeyword = $searchKeyword.val(),
	            $saleStaffId = $("#SALE_STAFF_ID"),
                saleStaffId = $saleStaffId.val(),
                $IMEI = $("#NEW_IMEI"),
                IMEI = $IMEI.val();

            // 如果没有选择活动类型，直接选择活动方案，需要将该方案对应的类型编码传入活动类型下拉框
            var title = $("#SALE_PRODUCT_ID_float li.on").attr("title"),
                campnType;
            if (title) {
                campnType = title.slice(title.lastIndexOf("|") + 2);
                if ($campnType.val() === "" || $campnType.val() !== campnType) {
                    $campnType.val(campnType);
                }
            } else {
                campnType = $campnType.val();
            }

            if (productId === "" && IMEI === "" && searchKeyword === "") {
                $.validate.alerter.one($searchKeyword[0], "未输入查询的条件！");
                return;
            }
            if (saleActive.isTerminalCampnType(campnType)) {
                $("#IMEIQueryPart").css("display", "").addClass("required");
                $("#SalePackageBtn").tap(saleActive.queryPackages); // 绑定营销包右侧弹窗事件
                if (IMEI === "") {
                    $.validate.alerter.one($IMEI[0], "请输入终端串码查询可办理的营销包");
                    return;
                }
            } else {
                $("#SaleStaffIdPart").css("display", "none");
                $("#IMEIQueryPart").css("display", "none").removeClass("required");
                $saleStaffId.val("");
                $IMEI.val("");
            }
            // iPhone6 相关处理
            var isiPhone6 = "",
                $iPhone6IMEI = $("#IPHONE6_IMEI"),
                iPhone6IMEI = $iPhone6IMEI.val();
            if (saleActive.isiPhone6CampnType(campnType)) {
                if (iPhone6IMEI !== "" && iPhone6IMEI !== "undefined") {
                    isiPhone6 = "1";
                } else {
                    isiPhone6 = "0";
                }
            } else {
                $("#IsiPhone6Part").css("display", "none");
                $("#iPhone6IMEIPart").css("display", "none");
                $iPhone6IMEI.val("");
            }

            var netOrderId = $("#NET_ORDER_ID").val();
            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
            var userId = $("#SALEACTIVE_USER_ID").val();
            var custId = $("#SALEACTIVE_CUST_ID").val();
            var isGiftActive = $("#IS_GIFT_ACTIVE").val();
            var giftCode = $("#GIFT_CODE").val();

            var params = "&NET_ORDER_ID=" + netOrderId + "&SERIAL_NUMBER=" + serialNumber + "&NEW_IMEI=" + IMEI
                    + "&SALE_STAFF_ID=" + saleStaffId + "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + productId
                    + "&SEARCH_CONTENT=" + searchKeyword + "&IS_IPHONE6=" + isiPhone6 + "&IPHONE6_IMEI=" + iPhone6IMEI
                    + "&SALEACTIVE_USER_ID=" + userId + "&SALEACTIVE_CUST_ID=" + custId
                    + "&IS_GIFT_ACTIVE=" + isGiftActive + "&GIFT_CODE=" + giftCode
                    + "&SPEC_TAG=" + "renderByActiveQry"
                    + "&MUL_CHANNEL_TAG="+$("#MUL_CHANNEL_TAG").val();
            saleActiveList.renderComponent(params);
        },

        showGoodsInfo: function (goodsInfo) {
	        var deviceBrand = goodsInfo.get("DEVICE_BRAND");
	        var deviceModel = goodsInfo.get("DEVICE_MODEL");
            /*var showInfo = "品牌：" + deviceBrand
                    + "  电池：" + goodsInfo.get("BATTERY")
                    + "  颜色：" + goodsInfo.get("COLOR")
                    + "  型号：" + deviceModel;*/
            var showInfo = "零售价：" + goodsInfo.get("SALE_PRICE")/100
                    + "  品牌：" + deviceBrand
                    + "  型号：" + deviceModel
                    + "  资源编码：" + goodsInfo.get("DEVICE_MODEL_CODE");
            $("#GOODS_INFO").html(showInfo);
            $("#GoodsInfoPart").css("display", "");
            var allMoneyName = deviceBrand + deviceModel + "手机款";
            $("#ALL_MONEY_NAME").val(allMoneyName);
        },

        hideGoodsInfo: function () {
            $("#GOODS_INFO").val("");
            $("#GoodsInfoPart").css("display", "none");
        },

        // 判断是否从首页"热门活动"和"推荐活动"跳转进入
        fromWelcomePageURL: function () {
            var assignedFlag = $("#SALEACTIVE_ASSIGNED").val(),
                campnType = $("#CAMPN_TYPE_FROM_URL").val(),
                productId = $("#PRODUCT_ID_FROM_URL").val(),
                packageId = $("#PACKAGE_ID_FROM_URL").val();
            return (assignedFlag === "0" && campnType !== "" && productId !== "" && packageId !== "")
        },

        // 从活动产品列表自动选择目标产品
        autoSelectProductFromSaleAcitveProducts: function () {
            var $saleProductId = $("#SALE_PRODUCT_ID"),
                productId;
            if (saleActive.fromWelcomePageURL()) { // 首页"热门活动"和"推荐活动"跳转流程
                productId = $("#PRODUCT_ID_FROM_URL").val();
                $saleProductId.val(productId);
                $saleProductId.trigger("change");
            } else if (saleActive.fromHotRecommList()) { // 页面"热门活动"和"推荐活动"跳转流程
	            productId = $("#PRODUCT_ID_FROM_HOT_RECOMM").val();
                $saleProductId.val(productId);
                $saleProductId.trigger("change");
            }
        },

        // 首页"热门活动"和"推荐活动"跳转流程结束
        saleActiveFromWelcomePageURLEnd: function () {
            $("#SALEACTIVE_ASSIGNED").val("1");
        },

        // 查询用户当前全部营销活动
        queryValidSaleActives: function () {
            if (!$.auth.getAuthData()) {
                $.validate.alerter.one($("#AUTH_SERIAL_NUMBER")[0], "请先查询号码!");
                return;
            }

            var queryFlag = $("#QUERY_VALID_SALE_FINISHED").val(),
                userId = $("#SALEACTIVE_USER_ID").val(),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"),
                eparchyCode = $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val();
            if (queryFlag === "0" && userId !== "") {
                var params = "&USER_ID=" + userId
                        + "&SERIAL_NUMBER=" + serialNumber
                        + "&EPARCHY_CODE=" + eparchyCode
                        + "&SPEC_TAG=" + "queryValidSaleActives";
                $.beginPageLoading("用户已有营销活动查询中。。。");
                ajaxSubmit(null, null, params, "ValidSaleArea",
                    function () {
                        $.endPageLoading();
                        saleActive.toggleArea("#ShowValidSaleArea", "#HideValidSaleArea", "#ValidSaleArea");
                        saleActive.setQueryValidSaleActiveFlag("1");
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("用户已有营销活动查询失败", error_info);
                    });
            } else {
                saleActive.toggleArea("#ShowValidSaleArea", "#HideValidSaleArea", "#ValidSaleArea");
            }
        },

        // 设置查询用户当前全部营销活动完成标记
        setQueryValidSaleActiveFlag: function (v) {
            $("#QUERY_VALID_SALE_FINISHED").val(v);
        },

        // 查询页面"热门活动"和"推荐活动"
        queryHotRecommSaleActives: function () {
            if (!$.auth.getAuthData()) {
                $.validate.alerter.one($("#AUTH_SERIAL_NUMBER")[0], "请先查询号码!");
                return;
            }

            var queryFlag = $("#QUERY_HOT_RECOMM_SALE_FINISHED").val(),
                userId = $("#SALEACTIVE_USER_ID").val(),
                eparchyCode = $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val();
            if (queryFlag === "0" && userId !== "") {
                var params = "&EPARCHY_CODE=" + eparchyCode
                        + "&SPEC_TAG=" + "queryHotRecommSaleActives";
                $.beginPageLoading("热门和推荐营销活动查询中。。。");
                ajaxSubmit(null, null, params, "HotRecommArea",
                    function () {
                        $.endPageLoading();
                        saleActive.toggleArea("#ShowHotRecommArea", "#HideHotRecommArea", "#HotRecommArea");
                        saleActive.setQueryHotRecommSaleActiveFlag("1");
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("热门和推荐营销活动查询失败", error_info);
                    });
            } else {
                saleActive.toggleArea("#ShowHotRecommArea", "#HideHotRecommArea", "#HotRecommArea");
            }
        },

        // 设置查询页面"热门活动"和"推荐活动"完成标记
        setQueryHotRecommSaleActiveFlag: function (v) {
            $("#QUERY_HOT_RECOMM_SALE_FINISHED").val(v);
        },

        // 显示/隐藏开关按钮事件
        toggleArea: function (btn, paired, area) {
	        if ($(area + ":visible").length === 1) {
	            $(area).css("display", "none");
            } else if ($(area + ":hidden").length === 1) {
	            $(area).css("display", "");
            }
            $(btn).css("display", "none");
	        $(paired).css("display", "");
        },

        // 页面"热门活动"和"推荐活动"点击事件
        queryPackageFromHotRecommList: function (o) {
            $("#PRODUCT_ID_FROM_HOT_RECOMM").val($(o).attr("productId"));
            $("#PACKAGE_ID_FROM_HOT_RECOMM").val($(o).attr("packageId"));
            saleActive.queryAllAvailabelProducts();
        },

        // 判断是否从页面"热门活动"和"推荐活动"点击进入
        fromHotRecommList: function () {
            var productId = $("#PRODUCT_ID_FROM_HOT_RECOMM").val(),
                packageId = $("#PACKAGE_ID_FROM_HOT_RECOMM").val();
            return productId !== "" && packageId !== "";
        },

        // 页面"热门活动"和"推荐活动"跳转流程结束
        saleActiveFromHotRecommListEnd: function () {
            $("#PRODUCT_ID_FROM_HOT_RECOMM").val("");
            $("#PACKAGE_ID_FROM_HOT_RECOMM").val("");
        }

		/* by huangls5
		queryPackage: function(obj){ // 热门活动点击触发事件
			obj = $(obj);
			var value = obj.attr('package');
			if(value == '') return;
			var productId = value.split("|")[0];
			var packageId = value.split("|")[1];
			var campnType = value.split("|")[2];
			var imei = $("#NEW_IMEI").val();
			if(saleactive.isTeminalCampnType(campnType)){
			   if(imei==''){
			      alert("请输入终端串码查询可办理的营销包");return;
			   }
			   var saleStaffId = $('#SALE_STAFF_ID').val();
			   if(saleStaffId==''){
			      alert("请输入促销员工");return;
			   }
			   var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
			   var param = '&SERIAL_NUMBER='+serialNumber;
			   param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			   param += "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&CAMPN_TYPE=" + campnType;
			   param += "&NEW_IMEI=" + imei + "&SPEC_TAG=checkShotCutActive";
			   ajaxSubmit(null,null,param);
			   saleactiveModule.readerComponent(packageId, productId, campnType, imei);
			} else {
			   saleactiveModule.readerComponent(packageId, productId, campnType);
			}
		},*/
	});
})();