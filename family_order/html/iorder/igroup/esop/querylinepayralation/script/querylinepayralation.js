function qryAll(obj){
	
	var param = false;
	if($("#cond_ACCT_ID").val()){
		param = true;
	}
	if($("#cond_SERIAL_NUMBER_A").val()){
		param = true;
	}
	if($("#cond_GROUP_ID").val()){
		param = true;
	}
	if($("#cond_SERIAL_NUMBER").val()){
		param = true;
	}
	if($("#cond_PRODUCT_NO").val()){
		param = true;
	}
	if(!param){
		MessageBox.alert("错误", "请至少输入一个查询条件！");
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