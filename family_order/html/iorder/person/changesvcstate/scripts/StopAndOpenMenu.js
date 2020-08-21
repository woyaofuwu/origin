$(function () {
    var tradeTypeCodeFirst = $("#tradeTypeCodeFirst").val();
    if (tradeTypeCodeFirst != null && tradeTypeCodeFirst != "") {
        setRedirectUrl(tradeTypeCodeFirst);
    }
});

function setRedirectUrl(tradeTypeCode) {
    $.beginPageLoading("页面加载...");
    var redirectUrl = "";
    if (tradeTypeCode == "131") { // 报停（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.StopMobileNew", "onInitTrade",
            "&authType=00&TRADE_TYPE_CODE=131");
    }
    if (tradeTypeCode == "133") { // 报开（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OpenMobileNew", "onInitTrade",
            "&authType=00&TRADE_TYPE_CODE=133");
    }
    if (tradeTypeCode == "132") { // 挂失（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.LoseMobileNew", "onInitTrade",
            "&authType=00&TRADE_TYPE_CODE=132");
    }
    if (tradeTypeCode == "136") { // 局方停机（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeStopMobileNew", "onInitTrade",
            "&authType=00&TRADE_TYPE_CODE=136");
    }
    if (tradeTypeCode == "126") { // 局方开机（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeOpenMobileNew", "onInitTrade",
            "&authType=00&TRADE_TYPE_CODE=126");
    }
    if (tradeTypeCode == "492") { // 大客户担保开机（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.VipAssureOpenNew", "onInitTrade", "&TRADE_TYPE_CODE=492");
    }
    if (tradeTypeCode == "497") { // 紧急开机（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.EmergencyOpenNew", "onInitTrade", "&TRADE_TYPE_CODE=497");
    }
    if (tradeTypeCode == "496") { // 客户担保开机（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.GuaranteeOpenNew", "onInitTrade", "&TRADE_TYPE_CODE=496");
    }
    if (tradeTypeCode == "3801") { // 无线固话报停（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.StopMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3801");
    }
    if (tradeTypeCode == "3802") { // 无线固话报开（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OpenMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3802");
    }

    if (tradeTypeCode == "3808") { // 无线固话局方报停（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeStopWirelessMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3808");
    }
    if (tradeTypeCode == "3809") { // 无线固话局方报开（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeOpenWirelessMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3809");
    }
    if (redirectUrl != "") {
        redirectNavByUrl("停开机业务受理（新）", redirectUrl, "");
    }
    $.endPageLoading();
}