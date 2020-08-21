/* $Id  */

function onSubmitBaseTradeCheck(){
	if(!validate()){
		return false;
	}
	
	return true;
}

function validate() {
    
	var sn = $('#cond_GROUP_SERIAL_NUMBER').val();
	if (sn == '') {
		alert('请输入集团服务号码后查询！');
		return false;
	}
	var userId = $('#GRP_USER_ID').val();
	if (userId == '') {
		alert('请选择需要办理业务的用户！');
		return false;
	}
	if (!$.validate.verifyAll('destroyUserPart')) {
		return false;
	};
	return true;
	//$.beginPageLoading('业务验证中....');
    //var checkParam = '&CUST_ID='+$('#CUST_ID').val() +'&USER_ID='+userId +'&PRODUCT_ID='+$("#GRP_PRODUCT_ID").val()  +'&IF_BOOKING='+$("#IF_BOOKING").val()+'&EPARCHY_CODE='+$('#GRP_USER_EPARCHYCODE').val() ;
	//return ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.DestroyGroupUserRule','checkSimpleTipsRule',checkParam);
}

//集团信息查询成功后调用的方法
function selectGroupBySnAfterAction(data){

	var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    var acctInfo =  data.get("GRP_ACCT_INFO");
   	insertGroupUserInfo(userInfo);
	renderGroupBookingInfo($('#GRP_PRODUCT_ID').val());
	insertGroupCustInfo(groupInfo);
	insertGroupAcctInfo(acctInfo);
	renderProductExplainInfo($('#GRP_PRODUCT_ID').val());
	//加载产品元素信息
	renderProductElementsView($('#GRP_USER_ID').val(),'-1',$('#GRP_PRODUCT_ID').val(),'',"PRODUCTELEMNET_VIEW");
	$.beginPageLoading('业务验证中....');
    var checkParam = '&CUST_ID='+$('#CUST_ID').val() +'&USER_ID='+$('#GRP_USER_ID').val() +'&PRODUCT_ID='+$("#GRP_PRODUCT_ID").val()  +'&IF_BOOKING='+$("#IF_BOOKING").val()+'&EPARCHY_CODE='+$('#GRP_USER_EPARCHYCODE').val() ;
	var result =  ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.DestroyGroupUserRule','checkSimpleTipsRule',checkParam);
	$.endPageLoading();
	return result;
	
}
//集团信息查询失败后调用的方法
function selectGroupBySnErrorAfterAction(){
  	
  	//清空集团用户信息
  	clearGroupUserInfo();
  	//清空集团客户信息
	clearGroupCustInfo();
	//清空集团账户信息
	clearGroupAcctInfo();
	//清空产品参数信息
	cleanProductExplainInfo();
	//清空预约信息
	initHiddenGroupBookingPart();
	//清空成员订购的产品元素信息
	cleanProductElementViewPart('PRODUCTELEMNET_VIEW');
	
}

