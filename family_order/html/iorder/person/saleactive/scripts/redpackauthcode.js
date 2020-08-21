// 云支付短信验证码支付（校验验证码成功后支付）
function checkAuthCode() {
    if (!$.validate.verifyAll("CodePart")) {
        return;
    }
    $.beginPageLoading("校验验证码...");
    $.ajax.submit("CodePart", "redPackOrderConfirm", null, null,
        function (data) {
            $.endPageLoading();
            if (data.get("X_RESULTCODE") === "1") {
                hidePopup(this);
            } else {
                var $redPackCode = $("#REDPACK_CODE");
                $.validate.alerter.one($redPackCode[0], "您输入的验证码不正确，请输入正确的验证码...");
                $redPackCode.val("").focus();
            }
        }, function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

// 重发验证码，要求有原来下单时候的ORDER_ID
function resendAuthCode() {
    $.beginPageLoading("重发验证码...");
    $.ajax.submit("CodePart", "resendAuthCode", null, null,
        function (ajaxData) {
            $.endPageLoading();
            if (ajaxData.get("X_RESULTCODE") === "1") {
                $.validate.alerter.one($("#resendCodeBtn")[0], "验证码重发成功，请注意查收。");
            }
        }, function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}