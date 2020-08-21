function qryAll(obj){
	if(!$("#cond_BEGIN_DATE").val()){
		alert("请选择开始时间！");
		return false;
	}
	if(!$("#cond_END_DATE").val()){
		alert("请选择结束时间！");
		return false;
	}
	$.beginPageLoading("数据查询中，请稍后...");
	$.ajax.submit('qryInfo','qryInfos',null,'queryPart',function(data){
		$.endPageLoading();
		hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function qryDetailInfos(obj){
	var td = $(obj);
	var sub_type_code = td.attr("sub_type_code");
	if(!sub_type_code){
		alert("未获取到流程ID！");
		return false;
	}
	var start_date = td.attr("start_date");
	if(!start_date){
		alert("未获取到开始时间！");
		return false;
	}
	var end_date = td.attr("end_date");
	if(!end_date){
		alert("未获取到结束时间！");
		return false;
	}
	var query_type = td.attr("query_type");
	if(!query_type){
		alert("未获取到查询类型！");
		return false;
	}
	
	$.beginPageLoading("数据查询中，请稍后...");
	$.ajax.submit(null,'qryDetailInfos','&BEGIN_DATE='+start_date+'&END_DATE='+end_date+'&SUB_TYPE_CODE='+sub_type_code+"&QUERY_TYPE="+query_type,'detailInfo,detailCond',function(data){
		$.endPageLoading();
		showPopup('qryPopup1','qryOrderInfos');
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}