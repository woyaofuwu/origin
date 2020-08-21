$(function () {
    var tradeTypeCodeFirst = $("#tradeTypeCodeFirst").val();
    if (tradeTypeCodeFirst != null && tradeTypeCodeFirst != "") {
        setRedirectUrl(tradeTypeCodeFirst);
    }
});

function setRedirectUrl(tradeTypeCode) {
    $.beginPageLoading("页面加载...");
    var redirectUrl = "";

    if (tradeTypeCode == "3801") { // 无线固话报停（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.StopMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3801&TYPE_CODE=TD");
    }
    if (tradeTypeCode == "3802") { // 无线固话报开（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OpenMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3802&TYPE_CODE=TD");
    }

    if (tradeTypeCode == "3808") { // 无线固话局方报停（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeStopWirelessMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3808&TYPE_CODE=TD");
    }
    if (tradeTypeCode == "3809") { // 无线固话局方报开（新）
        redirectUrl = $.redirect.buildUrl("person.changesvcstate.OfficeOpenWirelessMobileNew", "onInitTrade",
            "&authType=18&TRADE_TYPE_CODE=3809&TYPE_CODE=TD");
    }
    if (redirectUrl != "") {
        redirectNavByUrl("无线固话停开机类", redirectUrl, "");
    }
    $.endPageLoading();
}