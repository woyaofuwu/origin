//查询集团客户信息
function getGroupInfo(){	
	//集团客户编码校验
	if(!$.validate.verifyAll("GroupCondPart")) {
		return false;
	}
	
	//集团客户信息查询
	$.ajax.submit('GroupCondPart', 'queryVocieInfo', null, 'GantPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}