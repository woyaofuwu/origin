
/**
 * 
 */
function queryData(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryChangeCustInfo', '', 'refresh_table', function(data){
		$.endPageLoading();
		var type = $("#cond_SELECT_TRADE_TYPE_CODE").val();
		var title = "";
		if(type == "100") {
			title = "用户可以过户！";
		}else if(type == "192"){
			title = "用户可以立即销户！";
		}

		if(data){
			var resultcode = data.get("X_RESULTCODE");
			if(resultcode == '0001'){
				if(type == "100") {
					title = "用户不可以过户，详见限制编码和限制说明！";
				}else if(type == "192"){
					title = "用户不可以立即销户，详见限制编码和限制说明！";
				}
				$("#tableInfoArea").css("display","");
				//MessageBox.error(title,title,null,null,null);
			}else if(resultcode == 0){
//					MessageBox.success(title,title,null,null,null);
					$("#tableInfoArea").css("display","");
					MessageBox.success("提示",title);
				}else
				{
					title = "错误";
					errInfo = "代码异常";
//					MessageBox.error(title,errInfo,null,null,null);
					MessageBox.error(title,errInfo);
				}
				
				
			}
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}	
	