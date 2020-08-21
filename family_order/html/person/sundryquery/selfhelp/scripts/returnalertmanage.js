
function queryalert() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return false;
    }
    $.beginPageLoading("数据加载中！");
    $.ajax.submit('QueryCondPart,navt', 'queryReturnAlert', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}