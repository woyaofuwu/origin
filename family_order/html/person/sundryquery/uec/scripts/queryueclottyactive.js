
//查询订单详细信息
function querylottys() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return false;
    }
    $.beginPageLoading("数据加载中！");
    $.ajax.submit('QueryCondPart,navt', 'querylottys', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

function activechange(obj) {
	
	$.beginPageLoading("数据加载中！");
    $.ajax.submit('QueryCondPart,navt', 'queryPrizes', '&ACTIVITY_NUMBER=' + obj.value, 'PrizePart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}