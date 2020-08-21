//按工号查询 
function queryOpenAccountInfo(){
	
	$("#UPDATE_BTN").attr('disabled',true);
	$.beginPageLoading("努力加载中...");
    ajaxSubmit('ParamsPart', 'queryOpenAccountInfo', null, 'QueryListPart', function(data) {
    	$.endPageLoading();
		if (data.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(data.get('ALERT_INFO'));
		}
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function() {
    	$.endPageLoading();
		MessageBox.alert("警告提示", "查询超时");
	});
    
}
