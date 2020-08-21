function queryAirlinesWhite() {
    $.beginPageLoading("数据查询中..");
    $.ajax.submit('QueryViewPart', 'queryAirlinesWhite', '', 'resultsPart', function (data) {
            $.endPageLoading();
            if (data.get('NUM') <= 0) {
                MessageBox.alert("提示信息", "查询无数据!");
            }
            hidePopup('mypop', 'UI-search');
        },
        function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", "加载业务数据!", $.auth.reflushPage, null, info, detail);
        });
}

function createAirlinesWhite() {
    if (!$.validate.verifyAll()) {
        return false;
    }
    $.beginPageLoading("正在新增白名单..");
    $.ajax.submit('CreatetUserPart', 'createAirlinesWhite', '', 'resultsPart,CreatetUserPart', function (data) {
            $.endPageLoading();
            var result = data.get('X_RESULTCODE');
            if (result == '0') {
                MessageBox.success("员工信息新增成功！");
                hidePopup('mypop', 'UI-create');
                queryAirlinesWhite();
            } else {
                MessageBox.alert("提示信息", data.get('X_RESULTINFO'));
            }
        },
        function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", "加载业务数据!", $.auth.reflushPage, null, info, detail);
        });
}

function deleteAirlinesWhite(serialNumber) {
    MessageBox.confirm("提示信息", "是否删除员工号码" + serialNumber + "?(删除将退订员工订购的专属国漫资费产品)", function (btn) {
        if (btn == "ok") {
            var param = '&SERIAL_NUMBER=' + serialNumber;
            $.beginPageLoading("正在删除..");
            $.ajax.submit(null, 'deleteAirlinesWhite', param, 'resultsPart', function (data) {
                    var result = data.get('X_RESULTCODE');
                    if (result == '0') {
                        MessageBox.success("员工信息删除成功！");
                    }
                    else {
                        MessageBox.alert("提示信息", "员工信息删除失败！");
                    }
                    queryAirlinesWhite();
                },
                function (error_code, error_info, derror) {
                    $.endPageLoading();
                    MessageBox.error(error_code, error_info, derror);
                });
        } else {
            return;
        }
    }, {ok: "确定", cancel: "取消"});
}

//批量导入
function batImportCaseSn() {
    var importFile = $("#FILE_ID").val();
    if (importFile == "") {
        MessageBox.alert("提示", "请选择需要导入的文件！");
        return;
    }
    $.beginPageLoading("Import...");
    var param = "&FILE_ID=" + importFile + "&operTag=" + $("#operTag").val();
    $.ajax.submit(null, "batImportAirList", param, "", function (data) {
        $.endPageLoading();
        var msg = data.get("RESULT_MSG");
        MessageBox.success("提示：", msg, function (btn) {
        });
        queryAirlinesWhite();
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
