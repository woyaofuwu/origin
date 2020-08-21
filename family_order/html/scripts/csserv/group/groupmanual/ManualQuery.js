function getSelectValue() {
	// 查询条件校验
	if (!$.validate.verifyAll('QueryCondPart'))
		return false;

	var eos = new Wade.DataMap($("#EOS").val());
	$.beginPageLoading('数据查询中...');
	ajaxSubmit('QueryCondPart,infonav', 'queryFileInfo', '&EOS=' + eos,
			'QueryListPart,hintPart', function(data) {
				$("#hintPart").attr('style', 'display:;');
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
	return true;
}

function deleteFile() {

	if (!queryBox(this, 'trades')) {
		return;
	}

	var eos = new Wade.DataMap($('#EOS').val());

	MessageBox.confirm('提示信息', '确定要删除文件吗', function(btn) {

		if ('ok' == btn) {
			$.beginPageLoading('删除文件中...');
			var check = $("input[name='trades']:checked");
			var param = '';

			for ( var i = 0; i < check.length; i++) {
				param += check[i].value + ',';
			}

			ajaxSubmit('QueryCondPart,infonav', 'deleteFile', '&param=' + param
					+ '&EOS=' + eos, 'QueryListPart', function(data) {
						$.endPageLoading();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});

		}

	});
}

function sendUploadEnd() {
	MessageBox.confirm('提示信息', '确定要发起ESOP流程受理吗', function(btn) {

		if ('ok' == btn) {

			var eos = new Wade.DataMap($('#EOS').val());
			$.beginPageLoading('正在发起ESOP流程受理流程...');
			ajaxSubmit(this, 'sendEsopMsg', '&EOS=' + eos, null,
					function(data) {

						var result = data.get("RESULT");

						if ('SUCCESS' == result) {
							MessageBox.success('提示信息', 'ESOP流程受理成功！');
						}
						$.endPageLoading();
					}, function(error_code, error_info, derr) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derr);
					});

		}

	});
}