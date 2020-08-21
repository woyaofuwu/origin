/* auth查询后自定义查询 */
function refreshPartAtferAuth(data) {
    var param = "&ROUTE_EPARCHY_CODE=" + data.get("USER_INFO").get("EPARCHY_CODE") + "&USER_ID=" + data.get("USER_INFO").get("USER_ID") + "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val() + "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
    $.beginPageLoading("宽带资料查询。。。");
    $.ajax.submit(this, 'loadChildInfo', param, 'widePart', function (data) {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
}
