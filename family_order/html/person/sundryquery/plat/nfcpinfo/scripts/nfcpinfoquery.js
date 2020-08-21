function refreshPartAtferAuth(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryRecordPart', 'loadChildInfo', null, 'custInfoPart,downInfoPart', 
	function(datas){
		$.endPageLoading();
		if(datas.get("X_RSPCODE")=="0000" && datas.get("X_RSPTYPE")=="0" ){

		}else{
			content = datas.get("X_RSPDESC");
			$.cssubmit.showMessage("error", "查询失败", "错误信息："+content);
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}