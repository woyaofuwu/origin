function queryData(){
	
	var tradeDate=$("#tradeDate").val();
	
	if(tradeDate==""){
			MessageBox.alert("提示信息","请输入对账日期",null,null,null);	
		return;
	}
		$.beginPageLoading("查询中...");
		$.ajax.submit("submitInfo","queryData",null,"tyzfTablePart",function(data){
			$.endPageLoading();
			if(data.get("CODE")!=null){
				 MessageBox.alert("提示信息","未找到记录！",null,null,null);	
			}
		},
		function(error_code,error_info,detail){
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, null, detail);
	    });
}