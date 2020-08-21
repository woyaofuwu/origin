
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
		return;
	}
	
	//填充产品信息
	if(!renderProductExplainInfo($('#GRP_PRODUCT_ID').val())){
		selectGroupBySnErrorAfterAction();
		return;
	}
	
	//刷新合同信息
	renderGrpContractInfos($('#CUST_ID').val(),$('#GRP_USER_ID').val(),$('#CUST_NAME').val(),$('#GROUP_ID').val());
	
	//刷新产品信息
	renderProductInfos();
	
	
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
    //重置产品显示信息
    initElement();

}

//刷新产品元素信息
function renderProductElements(){
  
  var productId = $('#GRP_PRODUCT_ID').val();
  var grpUserId = $('#GRP_USER_ID').val();
  var eparchCode = $('#GRP_USER_EPARCHYCODE').val();
  if(productId != ''){
  		//待选区初始化
  		offerList.renderComponent(productId,eparchCode);

  		var data="&USER_ID="+grpUserId+"&PRODUCT_ID="+productId+"&USER_EPARCHY_CODE="+eparchCode;
  		//已选元素初始化
		selectedElements.renderComponent(data,eparchCode);
		
  }
  
}

//产品已选区加载完成后，将业务类型赋给组件，后续添加时会使用到业务类型做元素互斥等验证
function afterRenderActionChgUs(){
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
		var selfparam = '&USER_ID='+$('#GRP_USER_ID').val()+'&EPARCHY_CODE='+$('#GRP_USER_EPARCHYCODE').val()+'&USER_EPARCHY_CODE='+$('#GRP_USER_EPARCHYCODE').val();
		selfparam += '&GRP_SN='+$('#GRP_SN').val()+'&GROUP_ID='+$('#GROUP_ID').val();
		renderProductParamsTab(productId,busiType,selfparam);
		
	}
  
}

//刷新集团定制信息
function renderUserGrpPackageHtml(){
	var useTag = getProductExplainUseTag();
	if(useTag !='1'){
		cleanUseGrpPackageTab();
	}else{
		renderUseGrpPackageTab($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_EPARCHYCODE').val(),$('#GRP_USER_ID').val());
	}
  
}

function cleanUseGrpPackageTab(){
  
	$('li[idx=1]').css('display','none');
	$('#grppackage').html();
  
}

function renderUseGrpPackageTab(productId,eparchyCode,userId){
  
	$('li[idx=1]').css('display','');
	renderUserGrpPackage(productId,eparchyCode,userId);
}


//隐藏参数标签页 -- 不会刷新只能出此下策
function cleanProductParamsTab(){
  
	$('li[idx=2]').css('display','none');
	$('#prama').html();
  
}

function renderProductParamsTab(productId,busiType,selfParam){
  
	$('li[idx=2]').css('display','');
	$.ajax.submit(null,null,'&IS_RENDER=true&PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType+selfParam,'productParamPart');
  
}


//刷新产品相关信息
function renderProductInfos(){
	cleanProductParamsTab();
	var productId = $('#GRP_PRODUCT_ID').val();
	var grpUserId = $('#GRP_USER_ID').val();
	if(productId != '' ){
		//初始产品元素信息
		renderProductElements();
		//初始参数信息
		renderProductParams();
		//初始定制信息
		renderUserGrpPackageHtml();
		
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
	return productInfoNextCheck();
}
function productInfoNextCheck() {

	var grpUserId=$("#GRP_USER_ID").val();   
	if (grpUserId=="") {
	   alert('尚未查询客户资料，请输入集团服务编码按回车查询!');
	   
	   return false;
	}
	
	var selectProduct=$('#GRP_PRODUCT_ID').val();
	 if (selectProduct=='') {
	   alert('请先选择需要办理的集团产品！');
	   return false;
	 }
   	
   	//校验是否在选择正确的合同信息
   	
    if(!checkHasProductContract(selectProduct)){
    	mytab.switchTo("合同信息");
    	return false;
    };
	
	if (typeof (validateParamPage) == "function") {
		mytab.switchTo("产品参数信息");
		var result = $.validate.verifyAll("prama")
		if(!result){
			return false;
		}
		
		if (!validateParamPage("ChgUs")) {
			return false;
		}
	}
	
	if(getProductExplainUseTag() == '1' && typeof(commparaGrpPackageElements)=="function"){
		mytab.switchTo("定制信息");
		var result = $.validate.verifyAll("grppackage")
		if(!result){
			return false;
		}
		commparaGrpPackageElements();
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
	
	return true;
}
 

/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var custId=$("#CUST_ID").val();
	        var groupId=$("#GROUP_ID").val();
	        var eparchyCode=$("#GRP_USER_EPARCHYCODE").val();
	        var param="&CUST_ID="+custId+"&GROUP_ID="+groupId+"&GRP_USER_EPARCHYCODE="+eparchyCode;
	        return param;
	       }
	});
})();

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

//点击分页标签触发的事件，如果没有选择集团用户，则不显示标签
function myTabSwithcAction(ptitle,title){
	if($('#GRP_USER_ID').val() == ''){
		return false;
	}else{
		return true;
	}
}

