//查询集团客户信息
function getGroupInfo() {	
	//EC编码校验
	if(!$.validate.verifyAll("ImsUserPart")) {
		return false;
	}
	
	//EC编码查询
	$.ajax.submit('ImsUserPart', 'getGroupBaseInfo', '', 'GroupInfoPart,ImsGrpPasswordPart', function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){   	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function queryGrpCustInfo(){
	//成员号码校验
	if(!$.validate.verifyAll("ImsMebPart")) {
		return false;
	}
	
	//成员密码查询
	$.ajax.submit('ImsMebPart', 'getGrpImsInfoByMebNumber', '', 'GroupInfoPart,ImsGrpPasswordPart', function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){   	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}


