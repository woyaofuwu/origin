
function queryCustRights(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	var PARA_CODE2 = $("#PARA_CODE2").val();
	var USER_INFO_CLASS = $("#USER_INFO_CLASS").val();
	//var params = "&PARA_CODE2=" + PARA_CODE2+"&USER_INFO_CLASS=" + USER_INFO_CLASS;
	var params=PARA_CODE2+USER_INFO_CLASS;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryRealNameInfo',params, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			MessageBox.alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


