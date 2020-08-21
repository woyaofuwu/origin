//梦网包月类业务暂停恢复订购查询
function queryDreamNet(){
	
	$("#UPDATE_BTN").attr('disabled',true);
	$.beginPageLoading("努力加载中...");
    ajaxSubmit('ParamsPart', 'queryDreamNet', null, 'QueryListPart', function(data) {
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
//批量导入
function batImportCaseSn() {
	
    var importFile = $("#FILE_ID").val();
    var status = $('input:radio:checked').val();
    if (importFile == "") {
        MessageBox.alert("提示", "请选择需要导入的文件！");
        return;
    }
    if (status == "" || status == undefined) {
        MessageBox.alert("提示", "请选择暂停或恢复！");
        return;
    }
    $.beginPageLoading("Import...");
    var param = "&params=" + importFile+","+status;
    $.ajax.submit(null, "batImportDreamNetList", param, "", function (data) {
        $.endPageLoading();
        var msg = data.get("RESULT_MSG");
        MessageBox.success("提示：", msg, function (btn) {
        });
       
    }, function (error_code, error_info) {
        $.endPageLoading();
        var tip_info = "文件导入失败！";
        var new_error_info = (function () {
            if (error_info.indexOf(tip_info) == 1) {
                return error_info.substring(tip_info.length + 1, error_info.length - 1);
            } else {
                tip_info = "";
                return error_info;
            }
        })();
        MessageBox.error("错误提示", tip_info, null, null, new_error_info);
    });
}


