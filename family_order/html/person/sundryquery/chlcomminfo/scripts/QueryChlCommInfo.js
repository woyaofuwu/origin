

/***
 * 提交前校验并提交
 */
 function queryChlCommInfo(obj){
 	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryChlCommInfo', null, 'QueryListPart', function(data){
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
 