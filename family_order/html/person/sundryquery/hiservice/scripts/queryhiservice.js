
//查询交换机信息
function queryHIService() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return false;
    }
    $.beginPageLoading("数据加载中！");
    $.ajax.submit('QueryCondPart,navt', 'queryHIService', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}