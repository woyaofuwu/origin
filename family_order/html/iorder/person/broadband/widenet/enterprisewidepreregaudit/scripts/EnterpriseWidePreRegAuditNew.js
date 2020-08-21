function queryWidePreRegInfo(){
	if(!$.validate.verifyAll("WidePreRegConditionPart")){return false;}
	backPopup('UI-popup');
	$.beginPageLoading("正在查询数据...");
	ajaxSubmit('WidePreRegConditionPart', 'queryWidePreRegInfo','', 'WidePreRegInfoPart', function(rtnData) {
			$("#notifySmsBtn").css('display', 'none');
			var regStatus = $("#cond_REG_STATUS").val();
			if(regStatus == "4"){
				if(rtnData.get("RESULT_CODE") == "0"){
					$("#notifySmsBtn").css('display', '');
				}
			}
			$.endPageLoading();
		}, function(code, info, detail) {
			$.endPageLoading();
			$("#notifySmsBtn").css('display', 'none');
			MessageBox.error("错误提示", info, function(btn) {
			}, null, detail);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
}