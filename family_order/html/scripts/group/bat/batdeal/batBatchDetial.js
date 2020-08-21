function qryBatDealByCondition () {
	if(!$.validate.verifyAll("condForm")) {
		return false;
	}
	
	$.ajax.submit('condForm','batchDetialQuery','','GantPart,condForm',function (data) {
		$.endPageLoading();
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
}

function qryBatVPMNDealByCondition() {
	if(!$.validate.verifyAll("condForm")) {
		return false;
	}
	
	$.ajax.submit('condForm','batchDetialQueryVPMN','','GantPart,condForm',function (data) {
		$.endPageLoading();
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
}

function dealStateToRun(vpmnFlag) {
	$.beginPageLoading("错单重跑加载中...");
	$.ajax.submit('condForm','batchDetialErrorToRun','&vpmnFlag='+vpmnFlag,'GantPart,condForm',function (data) {
		$.endPageLoading();
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
}

function onclickArr(deal_desc,deal_desc_a,deal_id,deal_id2){
	var aa = $("#"+deal_id).val();
	if(aa==null || aa==""){
		$("#"+deal_id).val("show");
		$("#"+deal_id).html(deal_desc);
		$("#"+deal_id2).html("关闭详细错误信息");
	}else{
		$("#"+deal_id).val("");
		$("#"+deal_id).html(deal_desc_a);
		$("#"+deal_id2).html("查看详细错误信息");
	}
	
}