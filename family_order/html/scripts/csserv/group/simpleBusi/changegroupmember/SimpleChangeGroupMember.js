//集团信息查询成功后调用的方法
function popupGrpSnListAfterAction(data){

	if(data == null)
		return;
	var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    
    //生成集团用户信息
    insertGroupUserInfo(userInfo);
    
    //生成集团客户信息
    insertGroupCustInfo(groupInfo);
    
    //填充产品控制信息
    if(!renderProductCtrlInfo($('#GRP_PRODUCT_ID').val(),$('#BUSI_TYPE').val())){
    	popupGrpSnListAfterErrAction();
    	return ;
    }
    
    //生成预约box信息
	renderGroupBookingInfo($('#GRP_PRODUCT_ID').val());
	
	//生成产品描述信息
	insertProductExplainInfo(productExplainInfo);
	
	//刷新产品信息
    renderProductInfos();
    
    //刷新角色信息
    renderMebRoleBInfoByMebUserId($('#MEB_USER_ID').val(),$('#GRP_USER_ID').val(),$('#MEB_EPARCHY_CODE').val());
    
}


//集团信息查询失败后调用的方法
function popupGrpSnListAfterErrAction(){
  	initAllPage();
}


//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
	if(data == null)
		return;
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO");
	var orderInfos = data.get("ORDERED_GROUPINFOS");
	
	//生成成员客户信息
	insertMemberCustInfo(memcustInfo);
	//生成成员用户信息
	insertMemberUserInfo(memuserInfo);
	//生成成员的账期信息
	insertUserAcctDayInfo(memuseracctdayInfo);
	//生成成员订购的集团产品列表信息,如果号码只订购了一条集团产品信息，则直接查询这个集团，不在弹出订购的集团列表窗口
	if(orderInfos && orderInfos.length == 1){
		getMenberGroupInfo(orderInfos.get(0).get("SERIAL_NUMBER"),orderInfos.get(0).get("USER_ID"));
	}else{
		renderGrpList(orderInfos);
	}
	
}

//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction(){
  	initAllPage();
}

//成员号码订购集团失败后调用的方法
function initAllPage(){
  
  	//清空成员客户信息
	clearMemberCustInfo();
	//清空成员用户信息
	clearMemberUserInfo();
	//清空集团用户信息
	clearGroupUserInfo();
	//清空集团客户信息
	clearGroupCustInfo();
	//清空预约box信息
	initHiddenGroupBookingPart();
	//清空角色信息
	cleanMebRoleBInfo();
	//清空产品控制信息
    cleanProductCtrlInfo();
    //清空产品参数信息
	cleanProductExplainInfo();
	//清空资源信息
	cleanResPart();
	//重置产品显示信息
	initElement()
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
        
  		var data="&USER_ID="+mebUserId+"&PRODUCT_ID="+productId+"&USER_EPARCHY_CODE="+mebEparchyCode+"&MEB_USER_ID="+mebUserId;
  		//已选元素初始化
		selectedElements.renderComponent(data,mebEparchyCode);
		
  }
  
}

//产品已选区加载完成后，将业务类型赋给组件，后续添加时会使用到业务类型做元素互斥等验证
function afterRenderActionChgMb(){
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

//刷新资源信息
function renderResList(){
	
	var grpUserId = $('#GRP_USER_ID').val();
	var mebUserId = $('#MEB_USER_ID').val();
	var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();

	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.resview.ResViewHttpHandler','qryResInfosByMebUserIdGrpUserIdProductId','&GRP_USER_ID='+grpUserId+'&MEB_USER_ID='+mebUserId+'&MEB_EPARCHY_CODE='+mebEparchyCode,
	function(data){
		if(data == null)
			return ;
			
		initResList(data);
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		cleanResPart();
		$.endPageLoading();
		
    },{async:false});
}

//隐藏参数标签页 -- 不会刷新只能出此下策
function cleanProductParamsTab(){
  
	$('li[idx=1]').css('display','none');
	$('#prama').html();
  
}

function renderProductParamsTab(productId,busiType,selfParam){
  
	$('li[idx=1]').css('display','');
	$.ajax.submit(null,null,'&IS_RENDER=true&PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType+selfParam,'productParamPart');
  
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
		//先把资源信息删掉
		cleanResPart();		
		//初始化资源信息
		renderResList();
		/*通过身份校验办理时不显示凭证tag页,免身份校验办理时需要显示*/
		if($("#cond_CHECK_MODE") && $("#cond_CHECK_MODE").val()!="" && $("#cond_CHECK_MODE").val()!="F"){
			$('li[idx=3]').css('display','none');
			$('#voucher').html('');
		}
	}
}

//清空产品信息
function cleanProductInfos(){

}

//点击左侧包之后,执行的自定义方法
function pkgListAfterSelectAction(pkg) {
	var selfParam = "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&PRODUCT_ID=" + $("#GRP_PRODUCT_ID").val();
	var eparchyCode = $("#MEB_EPARCHY_CODE").val();
	pkgElementList.renderComponent(pkg, eparchyCode, selfParam);
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
	return productInfoNextCheck();
}
function productInfoNextCheck() {
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
		
		if (!validateParamPage("ChgMb")) {
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
    if(!tempElements)
   		return false;
    for (var i=0;i<tempElements.length ;i++ )
    {
     if(tempElements.get(i,"MODIFY_TAG")=='0'&&tempElements.get(i,"ATTR_PARAM_TYPE")=='9'&&tempElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
	 {
		alert(tempElements.get(i,"ELEMENT_NAME")+",缺少服务参数，请补全相关服务参数信息！"); 
		$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click(); 
		return false ; 
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
	
	return true;
}
 
//下账期生效的checkbox点击后触发的方法,需要重新加载产品信息，因为预约后需要重算所有的元素时间，所以暂时重刷，效率慢了些，元素时间的准确率有保证
function checkBookingBoxAction(){
	clickBookingBox();
	var ifcheck = getBookingCheck();
	if(ifcheck){
		$('#PRODUCT_PRE_DATE').val($('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val());
		$('#PRODUCT_PRE_END_DATE').val($('#USER_ACCTDAY_LAST_DAY_THISACCT').val());
	}else{
		$('#PRODUCT_PRE_DATE').val('');
		$('#PRODUCT_PRE_END_DATE').val('');
	}
	
	renderProductInfos();
}

//VPMN集团成员注销
function destroyGrpMember(){
	
	var grpUserId = $('#GRP_USER_ID').val();
	var grpProductId= $('#GRP_PRODUCT_ID').val();
	var grpBrandCode = $('#GRP_BRAND_CODE').val();
	var parmRemark = $('#parm_REMARK').val();	
	var condSerialNumber = $('#cond_SERIAL_NUMBER').val();
	var parmJoinIn = $('#parm_JOIN_IN').val();
	var ifBooking = $('#ifBooking').val();
	var param = '&GRP_USER_ID='+grpUserId+'&GRP_PRODUCT_ID='+grpProductId+'&GRP_BRAND_CODE='+grpBrandCode+'&parm_REMARK='+parmRemark+'&cond_SERIAL_NUMBER='
	+ condSerialNumber + '&parm_JOIN_IN=' + parmJoinIn + '&ifBooking=' + ifBooking;
	
	$.beginPageLoading("正在注销VPMN集团成员...");
	
	if(!productInfoNextCheck())
	{
		$.endPageLoading();
		return false;
	}
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.destroyvpmngroupmember.DestroyGrpMebFlowMainHttpHandler','submit', param,
	function(data){
		if(data == null)
			return ;
		$.endPageLoading();
		alert("VPMN成员注销成功!");
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		alert(error_info);
		
    },{async:false});
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

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#GRP_PRODUCT_ID").val(); 
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId;
    return param;
} 