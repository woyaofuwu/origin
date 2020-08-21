$(function () {
    var tradeTypeCodeFirst = $("#tradeTypeCodeFirst").val();
    if (tradeTypeCodeFirst != null && tradeTypeCodeFirst != "") {
        setRedirectUrl(tradeTypeCodeFirst);
    }
});

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