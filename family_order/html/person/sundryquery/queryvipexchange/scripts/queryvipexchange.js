function resetCondition(){
	document.getElementById("cond_SERIAL_NUMBER").value="";
	document.getElementById("cond_UPDATE_TIME").value="";
}


function queryEnter(){
	document.getElementById('QUERY_BTN').click();
	return true;
}

//查询订单详细信息
function queryexchange() {
    if (!$.validate.verifyAll("QueryCondPart")) {
        return false;
    }
    $.beginPageLoading("数据加载中！");
    $.ajax.submit('QueryCondPart,navt', 'queryVipExchange', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}