function queryWorkformDetail(obj){

	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('QueryCondPart,AdvanceConditionPart','queryWorkformDetail',null,'QueryResultPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
//根据业务类型，查询流程信息等
function queryDatelineCodeType(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var groupId = $(obj).attr("GROUP_ID");
	var productName = $(obj).attr("PRODUCT_NAME");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
	var templetName = $(obj).attr("TEMPLET_NAME");
	var urgencyLevel = $(obj).attr("URGENCY_LEVEL");
	var title = $(obj).attr("TITLE");
	var bpmtempletId = $(obj).attr("BPM_TEMPLET_ID");
	var isFinish = $(obj).attr("IS_FINISH");
	var busiFormId = $(obj).attr("BUSIFORM_ID");
	
	openNav("业务详情", "igroup.myWorkForm.Summarize", "queryData", "&BUSIFORM_ID="+busiFormId+"&IBSYSID="+ibsysId+"&GROUP_ID="+groupId+"&PRODUCT_NAME="+productName+"&DEAL_STAFF_ID="+dealStaffId+"&TEMPLET_NAME="+templetName+"&URGENCY_LEVEL="+urgencyLevel+"&TITLE="+title+"&BPM_TEMPLET_ID="+bpmtempletId+"&IS_FINISH="+isFinish, "/order/iorder");
}
//根据订单号暂时专线信息及资管EOMS流程
function queryDatelineAttr(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var nodeId = $(obj).attr("NODE_ID");
	var productId = $(obj).attr("PRODUCT_ID");
	if("7011"==productId||"7012"==productId)
	{
		openNav("专线详情", "igroup.myWorkForm.QueryDatelineAttr", "queryData", "&IBSYSID="+ibsysId+"&NODE_ID="+nodeId, "/order/iorder");
	}else{
		MessageBox.error("提示信息", "该工单不属于互联网或数据专线产品，无法查看资管处理状态！");
		return false;
	}
	
}
//查询曾经节点处理人
function queryOnceTemplet(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var dealstaffName = $(obj).attr("DEAL_STAFF_NAME");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
	var acceptTime = $(obj).attr("ACCEPT_TIME");
	openNav("曾经节点处理详情", "igroup.myWorkForm.ViewHisStaff", "queryData", "&IBSYSID="+ibsysId+"&DEAL_STAFF_NAME="+dealstaffName+"&DEAL_STAFF_ID="+dealStaffId+"&ACCEPT_TIME="+acceptTime, "/order/iorder");
}
//调用撤单接口
function queryDatelineRemoveOne(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
	openNav("订单撤销", "igroup.myWorkForm.DatelineRemoveOne", "queryData", "&IBSYSID="+ibsysId+"&DEAL_STAFF_ID="+dealStaffId, "/order/iorder");
}


//产品类型和产品选择的联动
function refreshProduct(data){
	debugger;
	var product_type = data.value;
	if(product_type=='1'){
		$("#cond_PRODUCT_NO").attr('disabled','true');
		$("#cond_PRODUCT_NO").val("");
	}else{
		$("#cond_PRODUCT_NO").attr('disabled','');
	}
	// 提交
	$.ajax.submit(null, 'queryProduct', 'PRODUCT_TYPE=' + product_type,
			'QueryProductList', function(data) {
		
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				MessageBox.alert("",error_info);
			});
}

