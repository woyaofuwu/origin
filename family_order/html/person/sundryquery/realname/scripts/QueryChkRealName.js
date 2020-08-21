/**
 * 
 */
function queryRealNameInfo(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
//	var cheackbox = $("#NORMAL_USER_CHECK").attr('checked');
//	var isCheck="1";	//默认是选择了的
//	if(!cheackbox){
//		isCheck="0";	//没有选择
//	}
//	var userId=$("#cond_USER_ID").val();
//	
//	var param="&IS_CHECK="+isCheck+"&USER_ID="+userId;
//	
	$.ajax.submit('QueryCondPart', 'queryRealNameInfo', null, 'QueryListPart', function(data){
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


function sel_bef_chkSearchForm(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
    var cheackbox = $("#cond_NORMAL_USER_CHECK").attr('checked');
    var sn = $("#cond_SERIAL_NUMBER").val();
    var params = "&SERIAL_NUMBER="+ sn;
//    $("#sel_user_id").val("");
    
    if(cheackbox)	//在网用户
    {
    	queryRealNameInfo();
    }else	//不在网用户，就弹出手机号的（在网和不在网）用户信息
    {
    	popupPage("realname.sub.SelectUserInfoForRealName", 'getCheckUserInfo', params, '选择用户信息', '700', '700');
    }
}