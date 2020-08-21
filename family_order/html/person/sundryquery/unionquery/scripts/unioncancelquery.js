/**
 * 统一订购查询
 */
function queryUnionDetailInfos() {
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('QueryCondPart,navt', 'queryUnionDetailInfos', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 展示会话详细信息
 */
function showSessionDetailInfo(obj) {
	var sessionId = $(obj).attr('content');
	
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('', 'showSessionDetailInfo', 'sessionId='+sessionId, 'SessionDetailPart', function() {
    	$.endPageLoading();
    	$.popupDiv("session_detail_div",850,"会话详细信息");
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 展示退订详细信息
 */
function showCancelDetailInfo(obj) {
	var sessionId = $(obj).attr('sessionId');
	var orderId = $(obj).attr('orderId');
	
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('', 'showCancelDetailInfo', 'sessionId='+sessionId+'&orderId='+orderId, 'CancelDetailPart', function() {
    	$.endPageLoading();
    	$.popupDiv("cancel_detail_div",750,"退订业务详细信息");
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 统一订购信息处理
 */
function dealUnionCancel(obj) {
	var sessionId = $(obj).attr('sessionId');
	
	$("#sessionId").val(sessionId);
	
	$.popupDiv("popup_deal_div","","处理退订信息");
}

/**
 * 保存已处理统一订购信息
 */
function saveDealCancelInfo() {
	if($.validate.verifyAll("popup_deal_div")){
		var dealInfo = $("#dealInfo").val();
		
		if(dealInfo.length > 100){
			MessageBox.alert('提示信息', "处理意见长度不能大于100！");
			return;
		}
		
		$.beginPageLoading("提交中...");
	    $.ajax.submit('DealInfoPart', 'updateUnionCancelInfos', '', '', function() {
	    	$.endPageLoading();
	    	MessageBox.alert('提示信息', '提交成功！',function(){
	    		$.closePopupDiv("popup_deal_div");
	    	});
	    }, function(error_code, error_info) {
	        $.endPageLoading();
	        alert(error_info);
	    });
	}
}