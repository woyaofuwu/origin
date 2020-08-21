function qryAll(obj){
	
	if(!$("#cond_BEGIN_DATE").val()){
		alert("请选择开始时间！");
		return false;
	}
	if(!$("#cond_END_DATE").val()){
		alert("请选择结束时间！");
		return false;
	}
	if(!$("#cond_SUB_TYPE_CODE").val()){
		alert("请选择工单类型！");
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