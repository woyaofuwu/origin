function queryWidenetConsts(){
	//查询条件校验
	if (!$.validate.verifyAll("ConditionPart")) {
		return false;
	}

	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if ($.trim(startDate).length > 7) {
		startDate = startDate.substring(0, 7);
	}
	if ($.trim(endDate).length > 7) {
		endDate = endDate.substring(0, 7);
	}
	if (startDate != endDate) {
		MessageBox.alert("提示信息", "开始时间和结束时间必须在同一个月内!");
		return false;
	}
	$.beginPageLoading("数据查询中...");
	ajaxSubmit('ConditionPart', 'queryWidenetConsts', null, 'QueryListPart', function(rtnData) { 
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

//稽核通过
function checkDealPassOK()
{
	var param = '';
	var data = UnfinishTrade.getCheckedRowsData("viceCheckBox");
	if (data == null || data.length == 0) {
		MessageBox.alert("提示信息", "您没有进行任何操作，不能提交！");
		return false;
	}
	param += '&NUMBER_LIST=' + data;
	
	$.beginPageLoading("业务受理中...");
	ajaxSubmit('QueryListPart,RemarkPart', 'updateConstsPass', param, 'QueryListPart', function(rtnData) {
		MessageBox.success("成功提示", "操作成功！", function(btn) {
			if (btn == "ok") {
				window.location.href = window.location.href;
			}
		});
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "操作超时!", function(btn) {
		});
	});
}

//稽核不通过
function checkDealPassFall()
{
	var param = '';
	var data = UnfinishTrade.getCheckedRowsData("viceCheckBox");
	if (data == null || data.length == 0) {
		MessageBox.alert("提示信息", "您没有进行任何操作，不能提交！");
		return false;
	}
	param += '&NUMBER_LIST=' + data;
	
	$.beginPageLoading("业务受理中...");
	ajaxSubmit('QueryListPart,RemarkPart', 'updateConstsNoPass', param, 'QueryListPart', function(rtnData) {
		MessageBox.success("成功提示", "操作成功！", function(btn) {
			if (btn == "ok") {
				window.location.href = window.location.href;
			}
		});
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "操作超时!", function(btn) {
		});
	});
}