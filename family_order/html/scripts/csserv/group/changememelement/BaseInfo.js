/* $Id  */

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
	if(!$.validate.verifyAll('baseInfoPart')){
		return false;
	};
	
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	
	$.beginPageLoading('业务验证中....');
	var checkParam ='&USER_ID='+grpuseid +'&USER_ID_B='+$("#MEB_USER_ID").val()+'&SERIAL_NUMBER='+serialNumber ;
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ChangeMemElementRule','checkBaseInfoRule',checkParam);
	
  	return false;    
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
}
//集团信息查询失败后调用的方法
function popupGrpSnListAfterErrAction(){
  	clearGroupUserInfo();
	clearGroupCustInfo();
	cleanProductExplainInfo();
}


//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
	if(data == null)
		return;
	
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	var orderInfos = data.get("ORDERED_GROUPINFOS");
	insertMemberCustInfo(memcustInfo);
	insertMemberUserInfo(memuserInfo);
	renderGrpList(orderInfos);
}

//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction(){
  
  clearMemberCustInfo();
  clearMemberUserInfo();
  clearGroupUserInfo();
  clearGroupCustInfo();
  
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
