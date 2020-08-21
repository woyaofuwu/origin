function queryMemberInfoListener(listenerName) {
	var snvalue = $('#'+$('#SELECTMEMBERINFO_SN_NAME').val()).val();
	var limitType = $('#SELECTMEMBERINFO_LIMITTYPE').val();
  	var limitProducts = $('#SELECTMEMBERINFO_LIMITPRODUCTS').val();
	if (snvalue == ""){
		$.showWarnMessage('请输入服务号码！','服务号码内容不能为空，请输入11位的手机号码！');
		return false;
	}else{
		$.beginPageLoading();
		var param = '&cond_SERIAL_NUMBER='+snvalue+'&RELATION_CODE='+$("#mebRelationTypePart input:checked").val();
		if(limitType != null && limitType != ''){
			param += '&cond_LIMIT_TYPE='+limitType;
		}
		if(limitProducts != null && limitProducts != ''){
			param += '&cond_LIMIT_PRODUCTS='+limitProducts;
		}
		param +='&cond_JUDGE_USERSTATE='+getSeleMebOrderJudgeUserStateTag();
				
		var refreshpart = $('#SELECTMEMBERINFO_REFRESH_PART').val();
		$.ajax.submit('',listenerName,param,refreshpart,
		function(data){
    		$.endPageLoading();  
    		var resultcode = data.get('X_RESULTCODE','0');
    		if(resultcode!='0'){
	    		$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    		}
    		var succesMethod = $('#SELECTMEMBERINFO_SUCCESS_JSMETHOD').val();
    		if(succesMethod != ''){
    			eval(succesMethod); 
    		} 
		},
		function(error_code,error_info,detailError){
			var errorJsMethod = $('#SELECTMEMBERINFO_ERROR_JSMETHOD').val();
			if(errorJsMethod != ''){
				eval(errorJsMethod);
			}
		
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,detailError);
	    });
	}

}

function queryMemberInfoHttpHandler() {
	var snvalue = $('#'+$('#SELECTMEMBERINFO_SN_NAME').val()).val();
	var limitType = $('#SELECTMEMBERINFO_LIMITTYPE').val();
  	var limitProducts = $('#SELECTMEMBERINFO_LIMITPRODUCTS').val();
	if (snvalue == ""){
		$.showWarnMessage('请输入服务号码！','服务号码内容不能为空，请输入11位的手机号码！');
		return false;
	}else{
		$.beginPageLoading();
		var param = '&cond_SERIAL_NUMBER='+snvalue+'&RELATION_CODE='+$("#mebRelationTypePart input:checked").val();
		if(limitType != null && limitType != ''){
			param += '&cond_LIMIT_TYPE='+limitType;
		}
		if(limitProducts != null && limitProducts != ''){
			param += '&cond_LIMIT_PRODUCTS='+limitProducts;
		}
		param +='&cond_JUDGE_USERSTATE='+getSeleMebOrderJudgeUserStateTag();
		
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmeborderinfo.SelectMebOrderInfoHttpHandler','queryMemberInfo',param,
		function(data){
    		 $.endPageLoading();
    		var resultcode = data.get('X_RESULTCODE','0');
    		if(resultcode!='0'){
	    		$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    		}
    		var succesMethod = $('#SELECTMEMBERINFO_SUCCESS_JSMETHOD').val();
    		if(succesMethod != ''){
    			eval(succesMethod); 
    		}
    		
    	
		},
		function(error_code,error_info,detailError){
			var errorJsMethod = $('#SELECTMEMBERINFO_ERROR_JSMETHOD').val();
			if(errorJsMethod != ''){
				eval(errorJsMethod);
			}
		
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,detailError);
	    });
	}

}

function queryMemberInfo() {
	var listenerName = $("#SELECTMEMBERINFO_LISTENER_NAME").val();
	if(listenerName == null || listenerName ==''){
		queryMemberInfoHttpHandler();
	}else{
		queryMemberInfoListener(listenerName);
	}
}

function queryMemberInfoForAuth() {
	var authParam  = $('#POP_'+$('#SELECTMEMBERINFO_SN_NAME').val()).val();
	$("#cond_CHECK_MODE").val(top["AUTH_VALID_CHECKMODE"]);
	$("#GROUP_AUTH_FLAG").val("true");
	queryMemberInfo();
}

function getCheckMode(authParams,keyStr){
	if(authParams==null || authParams == ''){
		return "";
	}
	
	var authParamArray = authParams.split("&");
	for(var i=0;i<authParamArray.length;i++){
		if(authParamArray[i].indexOf(keyStr)>=0){
			return authParamArray[i].split("=")[1];
		}
	}
	
}

function groupAuthStart(){
	if($.userCheck.checkSnValid($('#SELECTMEMBERINFO_SN_NAME').val())){
		$.userCheck.queryUser($('#SELECTMEMBERINFO_SN_NAME').val());
	}

}

function getSeleMebOrderJudgeUserStateTag(){
	var stateTag = $('#SELECTMEMBERINFO_JUDGE_USER_STATE').val();
	if(stateTag == null){
		stateTag ="";
	}
	return stateTag;
}
