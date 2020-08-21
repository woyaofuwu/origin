$(function (){
	exportButtonDisable();
})

function reset(){
	$('#submit_part :input').val('');
}

function query(){
	//查询条件校验
	if(!verifyAll()) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryGreenCellInfo', null, 'result_Table', function(data){
			if($('#DeptTable tbody tr').length>0){
				//使用导出
				exportButtonEnable();
			}else{
				//禁用导出按钮
				exportButtonDisable();
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}