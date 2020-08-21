function queryUserComplaints(){
	var cityCode = $('#cond_CITY_CODE').val();
	var ibsysId = $('#cond_SUBSCRIBE_ID').val();
	var busiType = "FOURMANAGE";	
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit(null,'queryBusiWorkformInfos','&BUSI_TYPE='+busiType+'&CITY_CODE='+cityCode+'&IBSYSID='+ibsysId,'QueryResultPart',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function queryBusiWorkformReview(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var subibsysId = $(obj).attr("SUB_IBSYSID");
	openNav("400业务订单复核流详情", "igroup.busiWorkform400.BusiWorkform400ReviewDetail", "queryData", "&IBSYSID="+ibsysId+"&SUB_IBSYSID="+subibsysId, "/order/iorder");


}