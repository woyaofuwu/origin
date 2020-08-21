function queryData(){
	var mos=/^1[3|4|5|8][0-9]\d{4,8}$/;
	var tel=$("#tel").val();
	var tradeDate=$("#tradeDate").val();
	//正则表达式验证手机号码的有效性
	if(tel !=""){
		if(!mos.test(tel)){
			MessageBox.alert("提示信息","请输入正确的手机号码",null,null,null);	
			return;
			
		}
	}
	
		if(tel=="" && tradeDate==""){
			MessageBox.alert("提示信息","请输入手机号码或交易日期",null,null,null);	
		return;
	}
		$.beginPageLoading("查询中...");
		$.ajax.submit("submitInfo","queryData",null,"tmallTablePart",function(data){
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