/**
 * 统一订购查询
 */
function queryUnionOrderInfos() {
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('QueryCondPart,navt', 'queryUnionOrderInfos', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 统一订购信息处理
 */
function dealUnionOrder() {
	var data = $.table.get("UnionOrderTable").getCheckedRowDatas();
	
	if(data.length == 0) {
		MessageBox.alert('提示信息', "请选择需要处理的订购日志！");
	} else {
		$.popupDiv("popup_deal_div","","统一订购日志处理");
	}
}

/**
 * 保存已处理统一订购信息
 */
function saveDealOrderInfo() {
	if($.validate.verifyAll("popup_deal_div")){
		var dealInfo = $("#dealInfo").val();
		
		if(dealInfo.length > 100){
			MessageBox.alert('提示信息', "处理意见长度不能大于100！");
			return;
		}
		
		var data = getUnionOrderCheckStr();
		
		$.beginPageLoading("提交中...");
	    $.ajax.submit('QueryCondPart,DealInfoPart', 'updateUnionOrderInfos', 'DEAL_INFO='+dealInfo+'&DEAL_LIST='+data, '', function() {
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

/**
 * 展示订购详细信息
 */
function showOrderDetailInfo(obj) {
	var sessionId = $(obj).attr('sessionId');
	var orderId = $(obj).attr('orderId');
	
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('QueryCondPart', 'showOrderDetailInfo', 'sessionId='+sessionId+'&orderId='+orderId, 'OrderDetailPart', function() {
    	$.endPageLoading();
    	$.popupDiv("order_detail_div",800,"订购详细信息");
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

/**
 * 获取已选中的订购信息
 */
function getUnionOrderCheckStr(){
	var s = document.getElementsByName("SessionOrder");
	var s2 = "";
	for( var i = 0; i < s.length; i++ ){
		if (s[i].checked){
			s2 += s[i].value+";";
		}
	}
	s2 = s2.substr(0,s2.length-1);
	return s2;
}