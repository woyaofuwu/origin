$(window).load(function() {
	$("#SERIAL_NUMBER").focus();
	$("#SERIAL_NUMBER").bind("keydown", onSerialNumberInputKeyDown);
});

function onSerialNumberInputKeyDown(e) {
	if (e.keyCode == 13 || e.keyCode == 108) {
		queryReject();
	}
}

/**
 * 拒收挂机提醒短信查询
 * @author zhouwu
 * @date 2014年6月18日 14:17:08 
 */
function queryReject(){
	if($.validate.verifyAll("query_div")){
	debugger;
		var serialNumber = $("#SERIAL_NUMBER").val();
		
		//此校验方法不是很准确
		if(!$.verifylib.checkMbphone(serialNumber)){
			alert('服务号码不是手机号，请重新输入！');
			$('#SERIAL_NUMBER').val('');
			return;
		}
		
		$.beginPageLoading("查询中...");

		ajaxSubmit('query_div', 'queryReject', null, 'viceInfopart', function(rtnData) {
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
	}
}