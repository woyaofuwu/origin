window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryUserTradeScore() {
	// 页面加载时根据传的分页参数进行查询
	var olcomnav_current = 1;// 当前显示第1页
	var olcomnav_pagesize = 10;// 每页显示条数
	var serial_number = $('#SERIAL_NUMBER').val();
	var accept_start = $('#ACCEPT_START').val();
	var accept_end = $('#ACCEPT_END').val();
	var params = '&SERIAL_NUMBER=' + serial_number + '&ACCEPT_START='
			+ accept_start + '&ACCEPT_END=' + accept_end
			+ '&pagin=olcomnav&olcomnav_count=0&snInfo_current='
			+ olcomnav_current + '&olcomnav_pagesize=' + olcomnav_pagesize
			+ '&olcomnav_needcount=false';

	ajaxSubmit('', 'queryUserTradeScore', params, 'QueryListPart', function(data) {
		if (data.get('ALERT_INFO') != '') {
			MessageBox.alert(data.get('ALERT_INFO'));
		}
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!", function(btn) {
		});
	});
}

// 提交校验
function checkBeforeSubmit() {
	if (!$.validate.verifyField($("#cond_SERIAL_NUMBER")[0])) {
		backPopup('UI-popup');
		return false;
	}
	backPopup('UI-popup');
	refreshPartAtferAuth();
}

function refreshPartAtferAuth() {
	$.beginPageLoading("努力加载中...");
	ajaxSubmit('QueryCondPart,AdvanceQueryCondPart', 'queryUserTradeScore', null, 'QueryResultPart,ExportPart',
		function(data) {
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

function popupInfo(obj) {
	var params = '&TRADE_ID=' + $(obj).attr('title') + '&SERIAL_NUMBER='
			+ $('#SERIAL_NUMBER').val() + "&NEW_DATE=" + new Date();
	popupPage('明细项及转预存受益号码信息', 'sundryquery.usertradescore.PopupUserTradeScoreNew', 'init', params);
}

function queryDetailItem() {
	// 页面加载时根据传的分页参数进行查询
	var serial_number = $('#SERIAL_NUMBER').val();
	var trade_id = $('#TRADE_ID').val();
	var params = '&SERIAL_NUMBER=' + serial_number + '&TRADE_ID=' + trade_id;

	ajaxSubmit('', 'queryDetailItem', params, 'detailInfoPart', function() {
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!", function(btn) {
		});
	});
}

function querySnInfo() {
	// 页面加载时根据传的分页参数进行查询
	var serial_number = $('#SERIAL_NUMBER').val();
	var trade_id = $('#TRADE_ID').val();
	var params = '&SERIAL_NUMBER=' + serial_number + '&TRADE_ID=' + trade_id;
	
	ajaxSubmit('', 'querySnInfo', params, 'NumberPart', function() {
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!", function(btn) {
		});
	});
}