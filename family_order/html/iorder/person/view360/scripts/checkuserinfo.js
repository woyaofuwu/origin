
// 跳转到父页面"首页"标签
function redirectToHomeTab(o) {
    parent.$("#SERIAL_NUMBER").val($("#SERIAL_NUMBER").val());
    parent.$("#USER_ID").val($(o).attr("userid"));
    parent.$("#CUST_ID").val($(o).attr("custid"));
    parent.$("#CUST_NAME").val($(o).val());
    parent.$("#NORMAL_USER").val($(o).attr("normalusercheck"));

    hidePopup(this);
    parent.updateTouchFrameLoginInfo();
    parent.setParamsForAcctPages();
    parent.queryCRMInfo();
    parent.queryAcctInfo();
    parent.retInfo();
}