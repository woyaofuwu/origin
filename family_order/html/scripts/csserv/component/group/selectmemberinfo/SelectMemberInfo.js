function getMemInfo()
{
	
	var mebsn = $('#'+$('#SN_MEB_SN_NAME').val()).val();
	if (mebsn == ""){
		$.showWarnMessage('请输入正确的服务号码！','服务号码信息不能为空');
		return false;
	}else{
		$.beginPageLoading();
		
  		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmemberinfo.SelectMemberInfoHttpHandler','queryMemberInfo','&cond_SERIAL_NUMBER='+mebsn+'&cond_JUDGE_USERSTATE='+getJudgeUserStateTag(),
    	function(data){
    		$.endPageLoading();
    		var resultcode = data.get('X_RESULTCODE','0');
	    	if(resultcode!='0'){
	    	
    			var aftererrorAction = $("#SN_MEB_AFTER_ERROR_ACTION").val();
			    if (aftererrorAction != '') {
			        eval(""+aftererrorAction);
			    }
    			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    			
	    	}else{
	    	
		    	var afterAction = $("#SN_MEB_AFTER_ACTION").val();
			    if (afterAction != '') {
			        eval(""+afterAction);
			    }
	    	
	    	}
	    	
    	},
		function(error_code,error_info,derror){
		
			var aftererrorAction = $("#SN_MEB_AFTER_ERROR_ACTION").val();
		    if (aftererrorAction != '') {
		        eval(""+aftererrorAction);
		    }
		    $.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
			
			
	    });
	}
	
}

function getMemInfoForAuth(){
	var authParam  = $('#POP_'+$('#SN_MEB_SN_NAME').val()).val();
	$("#cond_CHECK_MODE").val(top["AUTH_VALID_CHECKMODE"]);
	$("#GROUP_AUTH_FLAG").val("true");
	getMemInfo();
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
	if($.userCheck.checkSnValid($('#SN_MEB_SN_NAME').val())){
		$.userCheck.queryUser($('#SN_MEB_SN_NAME').val());
	}

}

function cleanMebAuthState(){
	$("#GROUP_AUTH_FLAG").val("false");
	$("#IF_ADD_MEB").val("false");
}
//是否选择新增成员三户资料
function ifAddMebUca(){
	return $("#IF_ADD_MEB").val();
}

function getJudgeUserStateTag(){
	var stateTag = $('#SN_MEB_JUDGE_USER_STATE').val();
	return stateTag;
}

function  setJudgeUserStateTag(judgeUserTag){
	$('#SN_MEB_JUDGE_USER_STATE').val(judgeUserTag);
}

function initSelectMemberInfo(){
	$('#'+$('#SN_MEB_SN_NAME').val()).val('');
	cleanMebAuthState();
}