var familyEntrance = {
    // 初始化
    init: function () {
        familyEntrance.mainSn = familyEntrance.getTouchFrameSn();
        if (!familyEntrance.mainSn) {
            return;
        }
        familyEntrance.initEntrance(familyEntrance.mainSn);
    },
    // 获取外框登录号码
    getTouchFrameSn: function () {
        if (top.document.getElementById("LOGIN_NUM")) {
            var mainSn = $(top.document.getElementById("LOGIN_NUM")).val();
            if (!mainSn) {
                MessageBox.alert("告警提示", "受理号码不能为空！");
                return "";
            }
            return mainSn;
        } else {
            MessageBox.alert("告警提示", "请从外框登录后办理此业务！");
            return "";
        }
    },
    initEntrance: function (sn) {
        var mainUserId = $("#MAIN_USER_ID").val();
        if (!mainUserId) {
            var param = "&SERIAL_NUMBER=" + sn;
            $.beginPageLoading("加载用户资料...");
            $.ajax.submit("", "loadUserInfo", param, "hiddenPart", function (ajaxData) {
                familyEntrance.afterLoadData(ajaxData);
                $.endPageLoading();
            }, function (code, info, detail) {
                $.endPageLoading();
                MessageBox.alert("错误提示", "加载元素信息错误！", null, null, info, detail);
            });
        }
    },
    afterLoadData: function (data) {
        if (typeof (fmyOffer) != "undefined") {
            fmyOffer.setEnv($("#EPARCHY_CODE").val());
            var param = {
                "open": familyEntrance.openFamilyPage,
                "initDataParam": familyEntrance.refreshOfferParam,
                "refreshOfferParam": familyEntrance.refreshOfferParam
            }
            fmyOffer.bind(param);
            window["suggest"] = new Suggest("suggest");
            suggest.setParam("EPARCHY_CODE", $("#EPARCHY_CODE").val());
            suggest.setParam("TYPE", fmyOffer.type);
            suggest.setParam("SERIAL_NUMBER", $("#FAMILY_SERIAL_NUMBER").val());
            suggest.setParam("USER_ID", $("#FAMILY_USER_ID").val());
            suggest.setParam("TRADE_TYPE_CODE", constdata.tradeType.ACCEPT);
            suggest.setShowAction(function (ajaxData) {
                fmyOffer["afterSearchRefreshOfferList"].call(fmyOffer, ajaxData);
            });
            fmyOffer.initData();
        }
    },
    refreshOfferParam: function () {
        var param = "";
        var isFusion = $("#IS_FAMILY_USER").val();
        if (isFusion == "true") {
            return "&SERIAL_NUMBER=" + $("#FAMILY_SN").val() + "&USER_ID=" + $("#FAMILY_USER_ID").val() + "&TRADE_TYPE_CODE=" + constdata.tradeType.CHANGE;
        }
        return param;
    },
    openFamilyPage: function (offer) {
        var offerName = offer.get("OFFER_NAME");
        var offerCode = offer.get("OFFER_CODE");
        var isFusion = $("#IS_FAMILY_USER").val();
        var param = "&SERIAL_NUMBER=" + $("#MAIN_SERIAL_NUMBER").val();
        var page = "family.Accept";
        var initFn = "";
        if (isFusion == "true") {
            param += "&AUTO_AUTH=true" + "&NEW_PRODUCT_ID=" + offerCode;
            page = 'family.ProductChange';
        } else {
            param += "&PRODUCT_ID=" + offerCode;
        }
        openNav(offerName, page, initFn, param, "/order/iorder");
    }
}