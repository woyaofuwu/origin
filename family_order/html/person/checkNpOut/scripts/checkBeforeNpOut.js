function queryData(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryNpOutInfo', '', 'refresh_table', function(data){
		$.endPageLoading();
		var title = "用户可以申请携出！";
			if(data){
				var resultcode = data.get(0).get("X_RESULTCODE");
				if(resultcode == '0001'){
					title = "用户不可以申请携出，详见限制编码和限制说明！";
					MessageBox.error(title,title,null,null,null);
				}else if(resultcode == '0002'){
					title = "用户不满足携出条件！";
					MessageBox.error(title,title,null,null,null);
				}else if(resultcode == 0){
					MessageBox.success(title,title,null,null,null);
				}else
				{
					title = "错误";
					errInfo = data.get(0).get("X_RESULTINFO");
					alert(errInfo);
					MessageBox.error(title,errInfo,null,null,null);
				}
				
				
			}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}	
	
