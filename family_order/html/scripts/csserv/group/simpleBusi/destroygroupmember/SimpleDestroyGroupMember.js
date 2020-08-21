/* $Id  */

function onSubmitBaseTradeCheck(){
	if(!validate()){
		return false;
	}
	
	return true;
}

function validate() {
	var serialNumber=$("#MEB_SERIAL_NUMBER").val();
	if (serialNumber=="") {
	   alert('尚未查询成员资料，请输入成员手机号码按回车查询！');
	   return false;
	}
	
	var grpuseid=$("#GRP_USER_ID").val();
	if (grpuseid=="") {
	   alert('尚未选择需要变更的集团产品！请在成员订购的集团列表中选择需要变更的集团产品！');
	   return false;
	}
	if(!$.validate.verifyAll('destroyMebPart')){
		return false;
	};
	
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	
	//凭证信息校验 add by chenzg@20180711 REQ201804280001集团合同管理界面优化需求
	if($("#MEB_VOUCHER_FILE_LIST")&&$("#MEB_VOUCHER_FILE_LIST").val()==""){
		alert("请上传凭证附件！");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($("#AUDIT_STAFF_ID")&&$("#AUDIT_STAFF_ID").val()==""){
		alert("请选择稽核人员！");
		mytab.switchTo("凭证信息");
		return false;
	}
	
	var checkParam = '&USER_ID='+grpuseid+'&SERIAL_NUMBER='+$("#cond_SERIAL_NUMBER").val();
	return ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.DestroyGroupMemberRule','checkSimpeTipsRule',checkParam);
	  
}

//集团信息查询成功后调用的方法
function popupGrpSnListAfterAction(data){

	var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    
    //生成三户数据
    if(userInfo != undefined && userInfo != null){
		insertGroupUserInfo(userInfo);
		renderGroupBookingInfo($('#GRP_PRODUCT_ID').val());
	}
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
	if(productExplainInfo != undefined && productExplainInfo != null){
		insertProductExplainInfo(productExplainInfo);
	}
	//加载产品元素信息
	renderProductElementsView($('#MEB_USER_ID').val(),$('#GRP_USER_ID').val(),$('#GRP_PRODUCT_ID').val(),$('#MEB_EPARCHY_CODE').val(),"PRODUCTELEMNET_VIEW");
	//加载成员退订退订归属集团的checkBox信息
	renderExitGrpMemberBox($('#GRP_PRODUCT_ID').val(),$('#MEB_SERIAL_NUMBER').val(),$('#MEB_EPARCHY_CODE').val());
	
	var serialNumber = $('#MEB_SERIAL_NUMBER').val();
	var mebUserId = $('#MEB_USER_ID').val();
	if(serialNumber != null && serialNumber != "" && mebUserId != null && mebUserId != ""){
   		$.beginPageLoading('正在检验...');
   		ajaxSubmit('', 'checkBreTipsHelp', '&MebSerialNumber=' + serialNumber + '&MebUserId=' + mebUserId,
		'', function(data) {
			var result = checkDelVpmnTwo(data);
			if(!result){
				$.endPageLoading();
				clearGrpMemberInfoErrAfterAction();	
				return ;
			}
			$.endPageLoading();
		}, function(error_code, error_info, derror) {
			$.endPageLoading();
			alert(error_info);				
			//popupGrpSnListAfterErrAction();
			//selectMemberInfoErrAfterAction();
			clearGrpMemberInfoErrAfterAction();	
			return ;			
		});
	}
		
}
//集团信息查询失败后调用的方法
function popupGrpSnListAfterErrAction(){
  	
  	//清空集团用户信息
  	clearGroupUserInfo();
  	//清空集团客户信息
	clearGroupCustInfo();
	//清空产品参数信息
	cleanProductExplainInfo();
	//清空成员订购的产品元素信息
	cleanProductElementViewPart('PRODUCTELEMNET_VIEW');
	//清空成员退订退订归属集团的checkBox信息
	cleanExitGrpMemberBoxPart();
}


//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
	if(data == null)
		return;
	cleanProductElementViewPart('PRODUCTELEMNET_VIEW');
	
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	var orderInfos = data.get("ORDERED_GROUPINFOS");
	insertMemberCustInfo(memcustInfo);
	insertMemberUserInfo(memuserInfo);
	//生成成员订购的集团产品列表信息,如果号码只订购了一条集团产品信息，则直接查询这个集团，不在弹出订购的集团列表窗口
	if(orderInfos && orderInfos.length == 1){
		getMenberGroupInfo(orderInfos.get(0).get("SERIAL_NUMBER"),orderInfos.get(0).get("USER_ID"));
	}else{
		renderGrpList(orderInfos);
	}
	
	/*add by chenzg@20180712 免身份校验需要上传凭证信息，*/
	if($("#cond_CHECK_MODE") && $("#cond_CHECK_MODE").val()!="" && $("#cond_CHECK_MODE").val()!="F"){
		$('#voucheInfoPart').css('display','none');
		$('#voucheInfoPart').html('');
	}
}

function checkDelVpmnTwo(data){
	if(data != null){
		//DEL_VPMN_TWO
		var delTwo = data.get("DEL_VPMN_TWO");
		if(delTwo != null && delTwo == "TRUE"){
			var msg = "客户本月办理新增集团次数已达到两次，如取消则当月不能再办理新增集团，是否继续？，点击确定继续办理，点击取消停止办理。 ";
			if(window.confirm(msg))
			{
				return true;
			}
			return false;
		} else if(delTwo != null && delTwo == "FALSE"){
			return true;
		}
	}
	return false;
}

//清理集团、成员信息
function clearGrpMemberInfoErrAfterAction(){

	clearMemberCustInfo();
	clearMemberUserInfo();
  	//清空集团用户信息
  	clearGroupUserInfo();
  	//清空集团客户信息
	clearGroupCustInfo();
	//清空产品参数信息
	cleanProductExplainInfo();
	//清空成员订购的产品元素信息
	cleanProductElementViewPart('PRODUCTELEMNET_VIEW');
	//清空成员退订退订归属集团的checkBox信息
	cleanExitGrpMemberBoxPart();
	
}

//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction(){
  
	clearMemberCustInfo();
	clearMemberUserInfo();
	clearGroupUserInfo();
	clearGroupCustInfo();
	cleanProductElementViewPart('PRODUCTELEMNET_VIEW');
	//清空成员退订退订归属集团的checkBox信息
	cleanExitGrpMemberBoxPart();
}

//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoErrAfterAction();
	
	if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		queryMemberInfo();
		return;
	}else if(state == 'USER_KEY_NULL'){//用户表中没有用户密码
		return true;
	}
}
