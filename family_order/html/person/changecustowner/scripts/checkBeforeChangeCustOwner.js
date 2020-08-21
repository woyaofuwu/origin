
/**
 * 
 */
function queryData(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryChangeCustInfo', '', 'refresh_table', function(data){
		$.endPageLoading();
		var title = "用户可以过户！";
			if(data){
				var resultcode = data.get("X_RESULTCODE");
				if(resultcode == '0001'){
					title = "用户不可以过户，详见限制编码和限制说明！";
					//MessageBox.error(title,title,null,null,null);
				}else if(resultcode == 0){
					MessageBox.success(title,title,null,null,null);
				}else
				{
					title = "错误";
					errInfo = "代码异常";
					MessageBox.error(title,errInfo,null,null,null);
				}
				
				
			}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}	
	
