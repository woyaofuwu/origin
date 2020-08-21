
/**
 * 导入
 * @returns {Boolean}
 */
function importQueryData(){

	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	
	$.ajax.submit('SubmitQueryInfo','importQueryDataInfo','','SvcStateList',function(data){
		$.endPageLoading();
		var msg=data.get(0).get("msg");
		   $.showSucMessage(msg);
		   resetPage('SubmitQueryInfo');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


   
   
   
   
   