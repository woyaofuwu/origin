$(function () {
    // 加载查询数据
    queryServiceStateInfo();
});

// 点击导入Excel文件
function importExcel() {
    // 获取表单数据
    var importFile = $("#FILE_ID").val();
    var operate = $('input:radio:checked').val();
    var baseServiceID = $("#baseService").val();
    var baseServiceName = $("#baseService").text();
    if (importFile == "") {
        MessageBox.alert("提示", "请选择需要导入的文件！");
        return;
    }
    if (baseServiceID == "" || baseServiceID == null) {
        MessageBox.alert("提示", "请选择基础功能服务！");
        return;
    }
    // 开始加载
    $.beginPageLoading("导入中...");
    var param = "&importFile=" + importFile+"&operate="+operate+"&baseServiceID="+baseServiceID+"&baseServiceName="+baseServiceName;
    // 调用后台的ajax请求，将基础功能服务维护内容传入到后台处理
    $.ajax.submit(null, "importExcel", param, "", function (data) {
        // 结束加载
        $.endPageLoading();
        var msg = data.get("RESULT_MSG");
        MessageBox.success("提示：", msg, function (btn) {
        });
        // 导入成功后默认点击查询全部接口表数据。
        queryServiceStateInfo();
    }, function (error_code, error_info) {
        // 结束加载
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
// 查询基础功能服务信息数据
function queryServiceStateInfo() {
    ajaxSubmit('QueryCond', 'queryBaseService', null, 'QueryList', function(data) {
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
// 提交组件提交前校验
function onTradeSubmit() {
    var param = "&SERIAL_NUMBER=";
    $.cssubmit.addParam(param);
    return true;
}
