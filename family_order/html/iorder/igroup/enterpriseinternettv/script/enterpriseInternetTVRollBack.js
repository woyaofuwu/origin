function queryGroupUser()
{
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"集团机顶盒编码必须填写，请填写后再进行查询！");
		return false;
	}
	$.beginPageLoading("查询中，请稍后...");
	$.ajax.submit('', 'loadPageInfo','&SERIAL_NUMBER='+serialNumber,'topSetBoxInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function onSubmitBaseTradeCheck(){
	if("" == $("#cond_USER_ID_A").val()) 
	{
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"请查询后再进行查询！");
		return false;
	}
	return true;
}