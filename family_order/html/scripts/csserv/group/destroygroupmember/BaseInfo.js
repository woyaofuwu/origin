/* $Id */
$(function(){
	var inModeCode = $("#IN_MODE_CODE").val();
	if(inModeCode == 1 && typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		$("#cond_SERIAL_NUMBER").val(sn);
	}
})
function isSelectedProducts() {
   var serialNumber=$("#MEB_SERIAL_NUMBER").val();
   var condserialNumber=$("#cond_SERIAL_NUMBER").val();
   if (serialNumber=="") {
	   alert('尚未查询成员资料，请输入成员手机号码按回车查询！');
	   return false;
	}
	if(condserialNumber != serialNumber){
   		alert('成员服务号码和显示的成员信息号码不一致，不能办理业务！');
   		return false;
   	}
	var grpUserId=$("#GRP_USER_ID").val();
	if (grpUserId == "") {
	   alert('尚未选择需要退订的集团产品！请在成员订购的集团列表中选择需要退订的产品！');
	   return false;
	}
	var grpWLWG=$("#GRP_BRAND_CODE").val();
	if ( grpWLWG == 'WLWG'){
		alert('注销集团成员之后若该集团为空集团，将无法新增集团成员！');
	}
	$.beginPageLoading('业务验证中....');
	var checkParam = '&USER_ID='+grpUserId+'&SERIAL_NUMBER='+$("#cond_SERIAL_NUMBER").val();
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.DestroyGroupMemberRule','checkBaseInfoRule',checkParam);
	return false;
}

//集团信息查询成功后调用的方法
function popupGrpSnListAfterAction(data){

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
	
}
//集团信息查询失败后调用的方法
function popupGrpSnListAfterErrAction(){
  	clearGroupUserInfo();
	clearGroupCustInfo();
	initHiddenGroupBookingPart();
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
	initHiddenGroupBookingPart();
}
