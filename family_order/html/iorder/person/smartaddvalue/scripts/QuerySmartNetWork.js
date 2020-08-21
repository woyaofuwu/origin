
// 按手机号码查询
function querySmartNetWork() {
	$.beginPageLoading("数据加载中...");
    ajaxSubmit('QueryCondPart', 'querySmartNetwork', null, 'QueryListPart', function(data) {
    	$.endPageLoading();
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function() {
    	$.endPageLoading();
		MessageBox.alert("警告提示", "查询超时");
	}); 
}

 