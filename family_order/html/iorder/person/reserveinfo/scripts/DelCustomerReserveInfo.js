function queryCustomerReserveInfo() {
	if($("#SERIAL_NUMBER").val() == ''){
		MessageBox.alert("提示信息","服务号码不能为空！");
		return;
	}
    $.beginPageLoading("数据查询中..");
    $.ajax.submit('QueryCondPart', 'queryCustomerReserveInfo', '', 'resultsPart', function (data) {
            $.endPageLoading();
            if (data.get('NUM') <= 0) {
                MessageBox.alert("提示信息", "查询无数据!");
            }
            hidePopup('mypop', 'UI-search');
        },
        function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", "加载业务数据!", null, null, info, detail);
        });
}

function deleteCustomerReserveInfo(serialNumber) {
    MessageBox.confirm("提示信息", "是否删除客户预约下单信息?（服务号码：" + serialNumber + "）", function (btn) {
        if (btn == "ok") {
            var param = '&SERIAL_NUMBER=' + serialNumber;
            $.beginPageLoading("正在删除..");
            $.ajax.submit(null, 'deleteCustomerReserveInfo', param, 'resultsPart', function (data) {
	            var result = data.get('X_RESULTCODE');
	            if (result == '0000') {
	                MessageBox.success("客户预留信息删除成功！");
	                queryCustomerReserveInfo();
	            }
	            else {
	                MessageBox.alert("提示信息", data.get('X_RESULTINFO'));
	                queryCustomerReserveInfo();
	            }
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
