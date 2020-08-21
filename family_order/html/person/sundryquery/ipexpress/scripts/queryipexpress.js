//IP直通车查询

function queryIPExpress(){

    var  serialnumber = $("#cond_SERIAL_NUMBER")
	if(!$.validate.verifyField(serialnumber[0])){
			return false;
	}
	$.beginPageLoading("数据查询中..");

	//IP直通车查询
	$.ajax.submit('QueryIPExpressCondPart,paginNav','queryIPExpress', null, 'QueryIPExpressListPart', function(){
	  $.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}