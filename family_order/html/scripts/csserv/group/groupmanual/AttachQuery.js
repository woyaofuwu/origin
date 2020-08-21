function getSelectValue() {
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}

	$.beginPageLoading("数据查询中...");
	ajaxSubmit("QueryCondPart,infonav", "queryFileInfo", null,
			"QueryListPart,hintPart", function(data) {
				$("#hintPart").attr("style", "display:;");
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
	return true;
}

function deleteFile() {

	if (!queryBox(this, "trades")) {
		return;
	}

	MessageBox.confirm("提示信息", "确定要删除文件吗", function(btn) {

		if ("ok" == btn) {
			$.beginPageLoading("删除文件中...");
			var check = $("input[name='trades']:checked");

			var param = "";

			for ( var i = 0; i < check.length; i++) {
				param += check[i].value + ",";
			}

			ajaxSubmit("QueryCondPart,infonav,QueryListPart", "deleteFile",
					"&param=" + param, "QueryListPart", function(data) {
						$.endPageLoading();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});

		}

	});
}