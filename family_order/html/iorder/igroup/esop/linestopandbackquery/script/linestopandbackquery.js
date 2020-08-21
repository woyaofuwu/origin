function qryLineInfos(obj){
	var param = '';
	
	var changMode = $("#CHANGEMODE").val();
	if(isBlank(changMode)){
		alert("请先选择查询类型！")
		return false;
	}
	
	if(isBlank($("#cond_GROUP_ID").val()) && isBlank($("#cond_SERIAL_NUMBER").val()) && isBlank($("#cond_PRODUCTNO").val())){
		alert("请至少输入一个查询条件!");
		return false;
	}
	$.beginPageLoading('正在查询专线信息...');
	$.ajax.submit('qryInfo','qryLineInfos',null,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function isBlank(obj){
	if(obj == undefined ||obj==null||obj==''){
		return true;
	}
	return false;
}
