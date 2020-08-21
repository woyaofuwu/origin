//成员号码订购集团信息查询成功后调用的方法
function selectOrderMemberInfoAfterAction(data){
  
	if(data == null)
		return;
	var orderInfos = data.get("ORDERED_GROUPINFOS");
	
	//生成成员订购的集团产品列表信息,如果号码只订购了一条集团产品信息，则直接查询这个集团，不在弹出订购的集团列表窗口
	if(orderInfos && orderInfos.length == 1){
		$('#cond_GROUP_SERIAL_NUMBER').val(orderInfos.get(0).get("SERIAL_NUMBER"));
		getGrpInfoBySn();
	}else{
		renderGrpList(orderInfos);
	}
	
}

//查询成员号码订购集团失败后调用的方法
function selectMemberOrderInfoErrAfterAction(){
  	selectGroupBySnErrorAfterAction();
}

//号码订购了多个集团信息时，选择某个集团后触发的事件
function popupGrpSnListAfterAction(grpsn,userId){
	$('#cond_GROUP_SERIAL_NUMBER').val(grpsn);
	getGrpInfoBySn();
}

//集团资料查询成功后调用的方法
function selectGroupBySnAfterAction(data) {

	if(data == null)
		return;
	
	var grpCustInfo = data.get("GRP_CUST_INFO");
	var GrpUserInfo = data.get("GRP_USER_INFO");
	//填充集团客户显示信息
	insertGroupCustInfo(grpCustInfo);
	
	//填充集团用户显示信息
	insertGroupUserInfo(GrpUserInfo);
	
	//填充产品控制信息
	if(!renderProductCtrlInfo($('#GRP_PRODUCT_ID').val(),$('#BUSI_TYPE').val())){
		selectGroupBySnErrorAfterAction();
		return ;
	}
	
	//填充产品信息
	if(!renderProductExplainInfo($('#GRP_PRODUCT_ID').val())){
		selectGroupBySnErrorAfterAction();
		return ;
	}
	
	//刷新成员角色信息
	renderMebRoleBInfos($('#GRP_PRODUCT_ID').val());
	
	//初始付费信息
	renderPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val());
	
	//初始分散账期逻辑
	setDiverTipCtrMb();
	
	//刷新产品信息
	renderProductInfos();
	
	if(GrpUserInfo != null){
		var grpSerialNumber = GrpUserInfo.get('SERIAL_NUMBER');
		if(grpSerialNumber != null && grpSerialNumber != ""){
	   		$.beginPageLoading('正在检验办理权限...');
	   		ajaxSubmit('', 'checkVpmnAllRightCode', '&GrpSerialNumber=' + grpSerialNumber,
			'', function(data) {
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				alert(error_info);
				selectGroupBySnErrorAfterAction();
			});
		}
	}
	
}
//集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {

	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //清空填充的集团用户信息
    clearGroupUserInfo();
    //清空产品控制信息
    cleanProductCtrlInfo();
    //清空产品信息
    cleanProductExplainInfo();
    //清空成员角色信息
    cleanMebRoleBInfo();
    //清空付费信息
    cleanPayPlanSelPart();
    //清空分散账期逻辑
    initDiverTip();
    //重置产品显示信息
    initElement();
    //清空成员三户的相关信息
    selectMemberInfoAfterErrorAction();

}


//成员号码资料查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
  if(data == null)
	return;
	
  	
  var memcustInfo = data.get("MEB_CUST_INFO");
  var memuserInfo = data.get("MEB_USER_INFO");
  var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO");
  
  //号码查询后先验证是否可以受理业务
  if($('#GRP_USER_ID').val() != ''){
	  var checkParam ='&CUST_ID='+$('#CUST_ID').val()+'&PRODUCT_ID='+$("#GRP_PRODUCT_ID").val()+'&USER_ID='+$('#GRP_USER_ID').val() +'&SERIAL_NUMBER='+memuserInfo.get("SERIAL_NUMBER","") +'&IF_ADD_MEB='+$("#IF_ADD_MEB").val()+'&CHK_FLAG=BaseInfo&SPEC_CDTION=SelMemberBySn&TIPS_TYPE=0|1|2' ;
	  if(!ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule','checkSimpeTipsRule',checkParam)) return false;
  }
  insertMemberCustInfo(memcustInfo);
  insertMemberUserInfo(memuserInfo);
  insertUserAcctDayInfo(memuseracctdayInfo);
  
  setDiverTipCtrMb();
  //刷新产品信息
  renderProductInfos();
}

//成员资料查询失败后调用的方法
function selectMemberInfoAfterErrorAction() {

	//重置产品显示信息
    initElement();
	clearMemberCustInfo();
	clearMemberUserInfo();
	clearUserAcctDayInfo();
}

//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoAfterErrorAction();
	
	if(state=='USER_NOT'){
		if($('#cond_SERIAL_NUMBER').val().indexOf('0898')=='0' ){
			if(confirm("服务号码["+$('#cond_SERIAL_NUMBER').val()+"]为非移动号码，是否需要新增客户核心资料")){
				$("#GROUP_AUTH_FLAG").val("true");
				$("#MEB_SERIAL_NUMBER").val($('#cond_SERIAL_NUMBER').val());
				$("#MEB_EPARCHY_CODE").val('0898');
				$("#MEB_PRODUCT_ID").val('4444');
				$("#IF_ADD_MEB").val("true");
				return ;
			} 
		}
	}else if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		getMemInfo();
		return;
	}else if(state == 'USER_KEY_NULL'){//用户表中没有用户密码
		return true;
	}
}

//刷新产品元素信息
function renderProductElements(){
  
  var productId = $('#GRP_PRODUCT_ID').val();
  var grpUserId = $('#GRP_USER_ID').val();
  var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();
  var mebUserId = $('#MEB_USER_ID').val();
  if(productId != '' && mebEparchyCode != '' && mebUserId!= '' ){
  		//待选区元素初始化
    	var svcParam="&USER_EPARCHY_CODE="+mebEparchyCode+'&GRP_USER_ID='+grpUserId;
  		offerList.renderComponent(productId,mebEparchyCode,svcParam);
  		offerList.currentGroup='';
        var acctDay = $("#USER_ACCTDAY_ACCT_DAY").val();
        var firstDate = $("#USER_ACCTDAY_FIRST_DATE").val();
        var nextAcctDay = $("#USER_ACCTDAY_NEXT_ACCT_DAY").val();
        var nextFirstDate = $("#USER_ACCTDAY_NEXT_FIRST_DATE").val();
        selectedElements.setAcctDayInfo(acctDay,firstDate,nextAcctDay,nextFirstDate);
        
  		var data="&USER_ID="+mebUserId+"&PRODUCT_ID="+productId+"&USER_EPARCHY_CODE="+mebEparchyCode;
  		//已选元素初始化
		selectedElements.renderComponent(data,mebEparchyCode);
		
  }
  
}

//产品已选区加载完成后，将业务类型赋给组件，后续添加时会使用到业务类型做元素互斥等验证
function afterRenderActionCrtMb(){
	$("#SELECTED_TRADE_TYPE_CODE").val($('#PRODUCTCTRL_TRADE_TYPE_CODE').val());
}

//刷新产品参数信息
function renderProductParams(){
	var productParamPage = getProductParamPage();
	if(productParamPage==''){
		//隐藏参数标签页
		cleanProductParamsTab();
	}else{
		var productId = $('#GRP_PRODUCT_ID').val();
		var busiType = $('#BUSI_TYPE').val();
		//各产品需要的参数
		var selfparam = '&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&MEB_USER_ID='+$('#MEB_USER_ID').val()+'&MEB_EPARCHY_CODE='+$('#MEB_EPARCHY_CODE').val()+'&MEB_SERIAL_NUMBER='+$('#MEB_SERIAL_NUMBER').val();
		selfparam += '&CUST_ID='+$('#CUST_ID').val()+'&GROUP_ID='+$('#GROUP_ID').val()+'&MEB_CUST_ID='+$('#MEB_CUST_ID').val();
		renderProductParamsTab(productId,busiType,selfparam);
		
	}
  
}

//隐藏参数标签页 -- 不会刷新只能出此下策
function cleanProductParamsTab(){
  
	$('li[idx=1]').css('display','none');
	$('#prama').html();
  
}

function renderProductParamsTab(productId,busiType,selfParam){
  
	$('li[idx=1]').css('display','');
	$.ajax.submit(null,null,'&IS_RENDER=true&PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType+selfParam,'productParamPart,resPart');
  
}


//刷新产品相关信息
function renderProductInfos(){
	cleanProductParamsTab();
	var productId = $('#GRP_PRODUCT_ID').val();
	var mebUserId = $('#MEB_USER_ID').val();
	var grpUserId = $('#GRP_USER_ID').val();
	if(productId != ''  && mebUserId!= '' ){
		//初始产品元素信息
		renderProductElements();
		//初始参数信息
		renderProductParams();
		//初始化凭证tab页信息 add by chenzg@20180711
		/*通过身份校验办理时不显示凭证tag页,免身份校验办理时需要显示*/
		//alert($("#cond_CHECK_MODE").val());
		if($("#cond_CHECK_MODE") && $("#cond_CHECK_MODE").val()!="" && $("#cond_CHECK_MODE").val()!="F"){
			$('li[idx=3]').css('display','none');
			$('#voucher').html('');
		}
	}
}

function initElement(){
	//清空待选区信息
  	offerList.renderComponent();
	//清空已选区信息
	$('#SelectSvcUl').html('');
	$('#SelectDiscntTable').html('');
	selectedElements.initSelectedElements(null);
	$('#SELECTED_ELEMENTS').val('');
}

function onSubmitBaseTradeCheck(){
	return productInfoNextCheck()
}
function productInfoNextCheck() {
	var vpmnShortCode=$("#SHORT_CODE").val(); 
	if (vpmnShortCode=="") {
	   alert('VPMN成员新增，短号不能为空!'); 
	   return false;
	}
	var grpUserId=$("#GRP_USER_ID").val();   
	if (grpUserId=="") {
	   alert('尚未查询客户资料，请输入集团服务编码按回车查询!');
	   
	   return false;
	}
	
	var meb_userid=$("#MEB_USER_ID").val(); 
	if (meb_userid == "") {
	   alert('请先输入成员服务号码查询资料后，再进行此操作！');
	   return false;
	}
	
	//判断是否新增成员时选择了新增网外号码的三户资料
	if(!judgeAddMebUca()){
		return false;
	}
	
	if(!checkPayPlan()){
		return false;
	}
	
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	
	if (typeof (validateParamPage) == "function") {
		mytab.switchTo("产品参数信息");
		var result = $.validate.verifyAll("prama")
		if(!result){
			return false;
		}
		
		if (!validateParamPage("CrtMb")) {
			return false;
		}
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
	
	mytab.switchTo("产品信息");
		//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
	}
	
	var tempElements = selectedElements.selectedEls;
	if (!tempElements) {
		return false;
	}
	for (var i = 0; i < tempElements.length; i++) {
		if (tempElements.get(i, "MODIFY_TAG") == "0" && tempElements.get(i, "ATTR_PARAM_TYPE") == "9" && tempElements.get(i, "ATTR_PARAM").get(0, "PARAM_VERIFY_SUCC") == "false") {
			alert(tempElements.get(i, "ELEMENT_NAME") + ",\u7f3a\u5c11\u670d\u52a1\u53c2\u6570\uff0c\u8bf7\u8865\u5168\u76f8\u5173\u670d\u52a1\u53c2\u6570\u4fe1\u606f\uff01");
			$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click(); 
			return false;
		}
	}
	
	if(!$.validate.verifyAll("productInfoPart")){
		return false;
	}
	
	var submitData = selectedElements.getSubmitData();
	if (!submitData) {
		return false;
	}
	$("#SELECTED_ELEMENTS").val(submitData);
	
	if(!judeAcctDays($('#USER_ACCTDAY_DISTRIBUTION').val(),$('#PRODUCT_NATURETAG').val(),$('#PRODUCT_IMMEDI_TAG').val(),$('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val(),$('#PAY_PLAN_SEL_PLAN_TYPE').val())){
		return false;
	}
	return true;
	
}
 
//成员新增时使用的分散提示信息
function setDiverTipCtrMb(){
	setDiverTip($('#USER_ACCTDAY_DISTRIBUTION').val(),$('#PRODUCT_NATURETAG').val(),$('#PRODUCT_IMMEDI_TAG').val(),$('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val(),$('#PAY_PLAN_SEL_PLAN_TYPE').val(),$('#PRODUCTCTRL_BOOKING_FLAG').val());
	var ifcheck = getBookingCheck();
	if(ifcheck){
		$('#PRODUCT_PRE_DATE').val($('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val());
	}else{
		$('#PRODUCT_PRE_DATE').val('');
	}
}

//判断产品是否支持成员新增时同时新增三户资料
function judgeAddMebUca(){
	var ifAddMeb =ifAddMebUca();//新增三户资料
	var AddMebUcaFlag  =getAddMebUcaFlag();//是否支持新增三户资料
	if(ifAddMeb=='true' && AddMebUcaFlag != 'true'){
		alert('产品不支持成员订购时新增客户核心资料！请先在集团成员用户开户界面补录号码的三户资料');
		return false;
	}
	return true;
}

//下账期生效的checkbox点击后触发的方法,需要重新加载产品信息，因为预约后需要重算所有的元素时间，所以暂时重刷，效率慢了些，元素时间的准确率有保证
function checkBookingBoxAction(){
	clickBookingBox();
	var ifcheck = getBookingCheck();
	if(ifcheck){
		$('#PRODUCT_PRE_DATE').val($('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val());
	}else{
		$('#PRODUCT_PRE_DATE').val('');
	}
	
	renderProductInfos();
}


/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var eparchyCode=$("#MEB_EPARCHY_CODE").val();
	        var param="&MEB_EPARCHY_CODE="+eparchyCode;
	        return param;
	       }
	});
})();

function goOnDo(){
    if($("#SUBMIT_MSG_PANEL").length){
		$("#SUBMIT_MSG_PANEL").remove();
	}
	//清除费用数据
	if($.feeMgr){
		$.feeMgr.clearFee();
	}
    //清空分散账期逻辑
    initDiverTip();
    
    //清空成员三户的相关信息
    selectMemberInfoAfterErrorAction();
    //清空服务号码查询组件信息
    initSelectMemberInfo();
    
    $.cssubmit.cancelAction();
    
    bindGoOnDoAction();
}


//点击分页标签触发的事件，如果没有选择集团产品，则不显示标签内容
function myTabSwithcAction(ptitle,title){
	if( $('#GRP_SN').val() == ''){
		alert('请先查询集团信息!');
		$('#cond_GROUP_SERIAL_NUMBER').focus();
		return false;
	}else if($('#MEB_USER_ID').val() == '' ){
		alert('请先查询成员服务号码信息!');
		$('#cond_SERIAL_NUMBER').focus();
		return false;
	}else{
		return true;
	}
}

function bindGoOnDoAction(){
	
	$.cssubmit.customizeBtns = null;//清空缓存自定义按钮
	
	$.cssubmit.bindCustomizeBtn({
			"name":"继续",
			"icon":"e_button-page-ok",
			"fn":goOnDo
 	});
}

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#GRP_PRODUCT_ID").val(); 
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId;
    return param;
} 
		