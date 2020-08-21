

//查询
function getBatReplySmsQuery(){
	if($("#cond_SERIAL_NUMBER").val()==null||$("#cond_SERIAL_NUMBER").val()==""){
		alert('服务号码不能为空！');
		return false;
	}
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'batreplysmsquery', null, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}



