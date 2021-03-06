$(function () {
    var tradeTypeCodeCurrent = $("#TRADE_TYPE_CODE_CURRENT").val();
    var checkedItem = $(".STOP_AND_OPEN_MENU_ITEM[valueCode='" + tradeTypeCodeCurrent + "']");
    if (checkedItem.length > 0) {
        checkedItem.addClass("checked");
    }
});

/**
 * 点击下拉框切换页面函数
 *
 * @param obj
 * @returns
 */
function clickOper(obj) {
    var tradeTypeCode = $(obj).attr("valueCode");
    setRedirectUrl(tradeTypeCode);
}

function setRedirectUrl(tradeTypeCode) {
    $.beginPageLoading("页面加载...");
    var redirectUrl = "";
    if (tradeTypeCode == "603") { // 宽带报停（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.WidenetStopNew", "onInitTrade",
            "&WIDE_TYPE=WIDE&TAG=0&TRADE_TYPE_CODE=603");
    }
    if (tradeTypeCode == "604") { // 宽带报开（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.WidenetOpenNew", "onInitTrade",
            "&WIDE_TYPE=WIDE&TAG=1&TRADE_TYPE_CODE=604");
    }
    if (tradeTypeCode == "632") { // 校园宽带报停（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.WidenetStopNew", "onInitTrade",
            "&WIDE_TYPE=SCHOOL&TAG=0&TRADE_TYPE_CODE=632");
    }
    if (tradeTypeCode == "633") { // 校园宽带报开（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.WidenetOpenNew", "onInitTrade",
            "&WIDE_TYPE=SCHOOL&TAG=1&TRADE_TYPE_CODE=633");
    }
    if (tradeTypeCode == "671") { // 宽带局方停机（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.OfficeStopWidenetNew", "",
            "&TRADE_TYPE_CODE=671");
    }
    if (tradeTypeCode == "672") { // 宽带局方开机（新）
        redirectUrl = $.redirect.buildUrl("broadband.widenet.changesvcstate.OfficeOpenWidenetNew", "",
            "&TRADE_TYPE_CODE=672");
    }
    if (redirectUrl != "") {
        redirectNavByUrl("停开机业务受理（新）", redirectUrl, "");
    }
    $.endPageLoading();
}