//通过集团号码查询集团资料成功后调用的方法
function selectGroupBySnAfterAction(data)
{  
	$("#cond_SERIAL_NUMBER").attr('disabled',false);
	//获取三户资料
    var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var productCtrlInfo = data.get("PRODUCT_CTRL_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");  
    //初始集团用户资料
    if(userInfo != undefined && userInfo != null){
		insertGrpUserList(userInfo);
		insertSelGroupUserInfo(userInfo);
	}
	//初始集团资料信息
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	} 
	
	//查询集团VPN信息
	qryVpnInfo();
} 

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();   
}

function qryVpnInfo(){
	$.beginPageLoading();
	$.ajax.submit("GrpUserInfoPart", "qryVPMNScpInfo", null, "VpmnPart", 
		function(data){
			$.endPageLoading(); 
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function onSubmitBaseTradeCheck(){
//	checkStaffRight();  // j2ee 权限判断移到java代码中去
	var oldScpCode = $('#OLD_SCP_CODE').val(); 
	var newScpCode = $('#SCP_CODE').val();  
	if(newScpCode == ''){
		var message ='SCP代码不能为空，请选择SCP代码！' ;
		$.showWarnMessage('提示',message);  
		return false;
	}
	if(oldScpCode==newScpCode){
		var message ='请选择下拉框修改SCP代码！' ;
		$.showWarnMessage('提示',message);  
		return false;
	}
	return true;
}

/////////////////////////////////
function qryClick(){  
	var serNum = $('#cond_SERIAL_NUMBER').val(); 
	if(serNum.trim() != "") {
		ajaxDirect4CS('', 'qryChgVPMNScp', '&cond_SERIAL_NUMBER='+serNum, 'refreshPart', false, '');
	}else {
		alert('请输入VPMN编号！');
		return false; 
	} 
}
// j2ee 权限判断移到java代码中去
function checkStaffRight() {
	var count = $("#COUNT").val();
	
	if(count>2 && !hasPriv("SYS119")) { 
		alert("本VPMN集团的成员数大于2，你没有权限变更本集团的SCP");
		return false;
	}
	else {
		return true;
	}
}