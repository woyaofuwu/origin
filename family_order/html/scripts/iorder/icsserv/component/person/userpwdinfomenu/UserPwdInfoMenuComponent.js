$(function () {
    var tradeTypeCodeCurrent = $("#TRADE_TYPE_CODE_CURRENT").val();
    var checkedItem = $(".USER_PWD_INFO_MENU_ITEM[valueCode='" + tradeTypeCodeCurrent + "']");
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

    if (tradeTypeCode == "71") { // 用户密码变更
        redirectUrl = $.redirect.buildUrl("changepassword.ModifyUserPwdInfoNew","init","TRADE_TYPE_CODE=71");
    }
    if (tradeTypeCode == "73") { // 用户密码重置
        redirectUrl = $.redirect.buildUrl("changepassword.ResetUserPwdInfoNew","init","TRADE_TYPE_CODE=73");
    }
    if (tradeTypeCode == "77") { // 用户密码解锁
        redirectUrl = $.redirect.buildUrl("changepassword.UnLockUserPwdNew","init","TRADE_TYPE_CODE=77");
    }

    if (redirectUrl != "") {
        redirectNavByUrl("用户密码管理", redirectUrl, "");
    }
    $.endPageLoading();
}