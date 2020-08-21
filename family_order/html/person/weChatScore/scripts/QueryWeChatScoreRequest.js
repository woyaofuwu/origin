function sel_bef_chkSearchForm(){
	//查询条件校验
	if(!($.validate.verifyAll("QueryCondPart")&&checkDateRange())) {//先校验已配置的校验属性
		return false;
	}
	
    var cheackbox = $("#cond_NORMAL_USER_CHECK").attr('checked');
    var sn = $("#cond_SERIAL_NUMBER").val();
    var params = "&SERIAL_NUMBER="+ sn;
    
    if(cheackbox)	//在网用户
    {
    	queryRequest();
    }else	//不在网用户，就弹出手机号的（在网和不在网）用户信息
    {
    	popupPage("wechatscore.QueryWeChatScoreRequest.SelectUserInfoForRealName", 'getCheckUserInfo', params, '选择用户信息', '700', '700');
    }
}

function queryRequest(){
	if($.validate.verifyAll("cond")){
		$.beginPageLoading("查询中...");
		$.ajax.submit("cond","qryWechatScoreRequest",null,'weChatScore', afterQuery);
	}
}

//核对是否是同一个月
function checkDateRange(){
	var startDate = $("#cond_BEGIN_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	
	var beginYear=startDate.substr(0,4);
	var beginMonth=startDate.substr(5,2);
	
	var endYear=endDate.substr(0,4);
	var endMonth=endDate.substr(5,2);
	
	
	if(!(beginYear==endYear&&beginMonth==endMonth)){
		alert( "查询日期必须是同一个月！");
		return false;
	}
	
	return true;
}

function afterQuery(data){
	$.endPageLoading();
	if(data&&data.length>0){
		$("#TipInfoPart").css("display","none");
	}
	else{
		$("#TipInfoPart").css("display","");
	}
}