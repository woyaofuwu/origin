
//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data)
{

	if(data == null)
	{
		return;
	}
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO");
	var orderInfos = data.get("ORDERED_GROUPINFOS");
	
	//生成成员客户信息
	insertMemberCustInfo(memcustInfo);
	//生成成员用户信息
	insertMemberUserInfo(memuserInfo);
	//生成成员的账期信息
	//insertUserAcctDayInfo(memuseracctdayInfo);
	//生成成员订购的集团产品列表信息,如果号码只订购了一条集团产品信息，则直接查询这个集团，不在弹出订购的集团列表窗口
	if(orderInfos && orderInfos.length == 1)
	{
		var s_Num = orderInfos.get(0).get("SERIAL_NUMBER");
		var s_userId = orderInfos.get(0).get("USER_ID");
		getMenberGroupInfo(s_Num,s_userId);
	}
	else
	{
		renderGrpList(orderInfos);
	}
	
}


//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data)
{
	selectMemberInfoErrAfterAction();
	
	if(state == 'USER_CUSTOM')
	{//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		queryMemberInfo();
		return;
	}
	else if(state == 'USER_KEY_NULL')
	{//用户表中没有用户密码
		return true;
	}
}

//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction()
{
  initAllPage();
}

//成员号码订购集团失败后调用的方法
function initAllPage()
{
  
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

function initElement()
{
	//清空待选区信息
  	offerList.renderComponent();
	//清空已选区信息
	$('#SelectSvcUl').html('');
	$('#SelectDiscntTable').html('');
	selectedElements.initSelectedElements(null);
	$('#SELECTED_ELEMENTS').val('');
}


//集团信息查询成功后调用的方法
function popupGrpSnListAfterAction(data)
{
	if(data == null)
	{
		return;
	}
	var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");
    
    //生成集团用户信息
    insertGroupUserInfo(userInfo);
    
    //生成集团客户信息
    insertGroupCustInfo(groupInfo);
    
    //填充产品控制信息
    if(!renderProductCtrlInfo($('#GRP_PRODUCT_ID').val(),$('#BUSI_TYPE').val()))
    {
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
function popupGrpSnListAfterErrAction()
{
	initAllPage();
}

function productTabSwitchAction(ptitle,title)
{
	
	if ($('#elementPanel').length != 0  && $('#elementPanel').css('display') =='block')
	{
		$('#elementPanel').css('display','none');
	}
	
	return true;
} 

//刷新产品相关信息
function renderProductInfos()
{
	cleanProductParamsTab();
	var productId = $('#GRP_PRODUCT_ID').val();
	var mebUserId = $('#MEB_USER_ID').val();
	var grpUserId = $('#GRP_USER_ID').val();
		
	if(productId != ''  && mebUserId!= '' )
	{
		//初始产品元素信息
		renderProductElements();
		//初始参数信息
		renderProductParams();
		//先把资源信息删掉
		cleanResPart();		
		//初始化资源信息
		renderResList();
	}
}

//隐藏参数标签页 -- 不会刷新只能出此下策
function cleanProductParamsTab()
{
  
	$('li[idx=1]').css('display','none');
	$('#prama').html();
  
}

function renderProductParamsTab(productId,busiType,selfParam)
{
  
	$('li[idx=1]').css('display','');
	$.ajax.submit(null,null,'&IS_RENDER=true&PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType+selfParam,'productParamPart');
  
}

//刷新产品元素信息
function renderProductElements()
{
  var productId = $('#GRP_PRODUCT_ID').val();
  var grpUserId = $('#GRP_USER_ID').val();
  var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();
  var mebUserId = $('#MEB_USER_ID').val();
  if(productId != '' && mebEparchyCode != '' && mebUserId!= '' )
  {
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


//刷新产品参数信息
function renderProductParams()
{
	var productParamPage = getProductParamPage();
	if(productParamPage=='')
	{
		//隐藏参数标签页
		cleanProductParamsTab();
	}
	else
	{
		var productId = $('#GRP_PRODUCT_ID').val();
		var busiType = $('#BUSI_TYPE').val();
		//各产品需要的参数
		var selfparam = '&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&MEB_USER_ID='+$('#MEB_USER_ID').val()+'&MEB_EPARCHY_CODE='+$('#MEB_EPARCHY_CODE').val()+'&MEB_SERIAL_NUMBER='+$('#MEB_SERIAL_NUMBER').val();
		selfparam += '&CUST_ID='+$('#CUST_ID').val()+'&GROUP_ID='+$('#GROUP_ID').val()+'&MEB_CUST_ID='+$('#MEB_CUST_ID').val();
		renderProductParamsTab(productId,busiType,selfparam);
	}
}

//刷新资源信息
function renderResList()
{
	
	var grpUserId = $('#GRP_USER_ID').val();
	var mebUserId = $('#MEB_USER_ID').val();
	var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();

	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.resview.ResViewHttpHandler','qryResInfosByMebUserIdGrpUserIdProductId','&GRP_USER_ID='+grpUserId+'&MEB_USER_ID='+mebUserId+'&MEB_EPARCHY_CODE='+mebEparchyCode,
	function(data)
	{
		if(data == null)
		{
			return ;
		}
			
		initResList(data);
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		cleanResPart();
		$.endPageLoading();
		
    },{async:false});
}

//产品已选区加载完成后，将业务类型赋给组件，后续添加时会使用到业务类型做元素互斥等验证
function afterRenderActionChgMb()
{
	$("#SELECTED_TRADE_TYPE_CODE").val($('#PRODUCTCTRL_TRADE_TYPE_CODE').val());
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

//下账期生效的checkbox点击后触发的方法,需要重新加载产品信息，因为预约后需要重算所有的元素时间，所以暂时重刷，效率慢了些，元素时间的准确率有保证
function checkBookingBoxAction()
{
	clickBookingBox();
	var ifcheck = getBookingCheck();
	if(ifcheck)
	{
		$('#PRODUCT_PRE_DATE').val($('#USER_ACCTDAY_FIRST_DAY_NEXTACCT').val());
		$('#PRODUCT_PRE_END_DATE').val($('#USER_ACCTDAY_LAST_DAY_THISACCT').val());
	}
	else
	{
		$('#PRODUCT_PRE_DATE').val('');
		$('#PRODUCT_PRE_END_DATE').val('');
	}
	
	renderProductInfos();
}

/**产品拼串*/
function setData(obj)
{
	return productInfoNextCheck();
}

function productInfoNextCheck() 
{
	var grpUserId=$("#GRP_USER_ID").val();   
	if (grpUserId=="")
	{
	   alert('尚未查询客户资料，请输入成员服务号码按回车查询!');
	   return false;
	}
	
	var meb_userid=$("#MEB_USER_ID").val(); 
	if (meb_userid == "") 
	{
	   alert('请先输入成员服务号码查询资料后，再进行此操作！');
	   return false;
	}
	
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true')
	{
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	
	if (typeof (validateParamPage) == "function")
	{
		mytab.switchTo("产品参数信息");
		var result = $.validate.verifyAll("prama")
		if(!result)
		{
			return false;
		}
		
		if (!validateParamPage("ChgMb")) 
		{
			return false;
		}
	}
	
	if($('#MEB_VOUCHER_FILE_LIST')&&$('#MEB_VOUCHER_FILE_LIST').val() == ""){
		alert("请上传凭证信息！");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($('#AUDIT_STAFF_ID')&&$('#AUDIT_STAFF_ID').val() == ""){
		alert("请选择稽核员！");
		mytab.switchTo("凭证信息");
		return false;
	}
	
	mytab.switchTo("产品信息");
		//校验必选包
	if (!selectedElements.checkForcePackage()) 
	{
		return false;
	}
	
	var tempElements = selectedElements.selectedEls;
    if(!tempElements)
    {
   		return false;
   	}
    for (var i=0;i<tempElements.length ;i++ )
    {
    	if(tempElements.get(i,"MODIFY_TAG")=='0'&&tempElements.get(i,"ATTR_PARAM_TYPE")=='9'
    		&&tempElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
	 	{
			alert(tempElements.get(i,"ELEMENT_NAME")+",缺少服务参数，请补全相关服务参数信息！"); 
			$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click(); 
			return false ; 
      	}
    }
    
    if(!$.validate.verifyAll("productInfoPart"))
    {
		return false;
	}
	
	var submitData = selectedElements.getSubmitData();
	if (!submitData) 
	{
		return false;
	}
	
	if (submitData == "" || submitData == "[]")
	{
		alert("未修改任何参数,不能提交!");
		return false;
	}
	
	var checkWlw = checkDiscntAttrForWlw(submitData,tempElements);
	if(!checkWlw){
		return false;
	}
	
	$("#SELECTED_ELEMENTS").val(submitData);
	
	commSubmit();
	return true;
}

function commSubmit() 
{
	var groupId = $("#GROUP_ID").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#GRP_PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var custId = $("#CUST_ID").val();
	var userEparchyCode = $('#MEB_EPARCHY_CODE').val();
	var grpSn = $('#GRP_SN').val();
	var userIdB = $('#MEB_USER_ID').val();
	var serialNumberB = $('#MEB_SERIAL_NUMBER').val();
	
	if (elements == "" ) 
	{
		return false;
	}
	var idata = $.DatasetList(elements);
	var info = $.DataMap();
	info.put("ELEMENT_INFO", idata);
	info.put("GROUP_ID", groupId);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", custId);
	info.put('GRP_SN', grpSn);
	info.put('USER_EPARCHY_CODE',userEparchyCode);
	info.put('MEBUSER_ID_B',userIdB);
	info.put('MEBSERIAL_NUMBER_B',serialNumberB);
	info.put('NEED_RULE',true);
	//add by chenzg@20180706 REQ201804280001集团合同管理界面优化需求
	if($('#MEB_VOUCHER_FILE_LIST')&&$('#MEB_VOUCHER_FILE_LIST').val() != ""){
		info.put('MEB_VOUCHER_FILE_LIST',$('#MEB_VOUCHER_FILE_LIST').val());
	}
	if($('#AUDIT_STAFF_ID')&&$('#AUDIT_STAFF_ID').val() != ""){
		info.put('AUDIT_STAFF_ID',$('#AUDIT_STAFF_ID').val());
	}
	//alert(info);
	
 	parent.$('#POP_CODING_STR').val(productName);
 	parent.$('#CODING_STR').val(info);
 	
 	parent.hiddenPopupPageGrp();
}

function checkDiscntAttrForWlw(data,data2){
	debugger;
	
	if(data2 != null)
	{
		var length = data2.length;
		
		//服务策略的拦截校验
		for (var i=0;i<length ;i++ )
    	{
    		var allSelectedElements = data2.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2"))
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var scStartDateTime = "";
				var scEndDateTime = "";
				if(elementId == "99011005" || elementId == "99011012" || elementId == "99011021" ||
					elementId == "99011022" || elementId == "99011028" || elementId == "99011029" ||
					elementId == "99011024" || elementId == "99011025")
				{
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "ServiceCode"){
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceCodeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "OperType")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									operTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceBillingType")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceBillingTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceUsageState")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceUsageStateValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceStartDateTime")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									scStartDateTime = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceEndDateTime")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									scEndDateTime = oneAttrValue;
								}
							}
						}
					}
					
					if(serviceCodeValue != "" || operTypeValue != "" || 
						serviceBillingTypeValue!="" || serviceUsageStateValue !="" || scStartDateTime != "" ||
						scEndDateTime != "")
					{
						if(!(serviceCodeValue != "" && operTypeValue != "" 
							&& serviceBillingTypeValue != "" && serviceUsageStateValue !=""))
						{
							alert("请把该服务" + elementId + "策略的属性值填写完,订购业务唯一代码、操作类型、计费方式、配额状态!");
							return false;
						}
					}
					
				}
				
			}
    	}
    	
		for (var i=0;i<length ;i++ )
    	{
    		var allSelectedElements = data2.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist"))
			{
				if(elementId == "99011022" || elementId ==  "99011028"
					|| elementId == "99011021" || elementId == "99011029"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "APNNAME"){
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("APNNAME不能为空,请填写!");
									return false;
								}
							}
						}
					}
				}
			}
    	}
    	
    	
    	///定向专线的apnname校验-重写
		for(var i=0;i<length;i++){
			var allSelectedElements = data2.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			var packageId = allSelectedElements.get("PACKAGE_ID");
		
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
					|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");//优惠的apnname
							if(oneAttrCode == "APNNAME")
							{//--1--
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("APNNAME不能为空,请填写!");
									return false;
								}
								
								if(packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){//定向优惠包
									var breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(var k=0;k<length;k++){
										var allSelectedElementSVC = data2.get(k);
										var elementTypeSVC = allSelectedElementSVC.get("ELEMENT_TYPE_CODE");
										var elementIdSVC = allSelectedElementSVC.get("ELEMENT_ID");
										var stateSVC = allSelectedElementSVC.get("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011021" || elementIdSVC == "99011029")
											{
												var attrSvcParams = allSelectedElementSVC.get("ATTR_PARAM");
												if(attrSvcParams != null && attrSvcParams != ""){
													var sizeSVC = attrSvcParams.length;
													for(var h=0;h<sizeSVC;h++){
														var oneAttrSvc = attrSvcParams.get(h);
														var oneAttrCodeSvc = oneAttrSvc.get("ATTR_CODE");
														var oneAttrValueSvc = oneAttrSvc.get("ATTR_VALUE");
														if(oneAttrCodeSvc == "APNNAME"){
															if(oneAttrValueSvc == oneAttrValue){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									debugger;
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										alert("定向流量包下的优惠的APNNAME与对应的服务的APNNAME不一样!请核实重新填写!");
										return false;
									}
								
								}
								else if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"){//通用优惠包
									var breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(var k=0;k<length;k++){
										var allSelectedElementSVC = data2.get(k);
										var elementTypeSVC = allSelectedElementSVC.get("ELEMENT_TYPE_CODE");
										var elementIdSVC = allSelectedElementSVC.get("ELEMENT_ID");
										var stateSVC = allSelectedElementSVC.get("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011022" || elementIdSVC == "99011028")
											{
												var attrSvcParams = allSelectedElementSVC.get("ATTR_PARAM");
												if(attrSvcParams != null && attrSvcParams != ""){
													var sizeSVC = attrSvcParams.length;
													for(var h=0;h<sizeSVC;h++){
														var oneAttrSvc = attrSvcParams.get(h);
														var oneAttrCodeSvc = oneAttrSvc.get("ATTR_CODE");
														var oneAttrValueSvc = oneAttrSvc.get("ATTR_VALUE");
														if(oneAttrCodeSvc == "APNNAME"){
															if(oneAttrValueSvc == oneAttrValue){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									debugger;
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										alert("通用流量包下的优惠的APNNAME与对应的服务的APNNAME不一样!请核实重新填写!");
										return false;
									}
								}
								
								
							}//--1--
						}
					}
				}
			}
			
		}
	
	}
	
	
			
	if(data != null){
		var discount = false;//true:折扣率低于6折
		var length = data.length;
		
		for(var i=0;i<length;i++){
			var allSelectedElements = data.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var packageId = allSelectedElements.get("PACKAGE_ID");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			if(elementType == "D" && (state == "0" || state == "2")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
				|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){ //先找折扣率
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "Discount"){
								discount = false;
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("优惠的折扣率不能为空,请填写!");
									return false;
								} else {
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 1){
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
								}
							}
							//-------add by chenzg@20180116---REQ201711150003关于新增物联卡本省折扣需求---begin---------
							/*本省折扣率校验*/
							else if(oneAttrCode == "20171211"){
								//本省折扣率有值才做校验，没值则不做校验
								if($.trim(oneAttrValue).length != 0){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("本省折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 0){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("本省折扣率必须是0到100之间的整数!");
										return false;
									}
									//当本省折扣内填写折扣后，原有折扣窗口默认100
									setAttrVale(attrParams, "Discount", "100");
									//当本省折扣低于6折时，判断审批工单号是否填写
									if(oneAttrValue<60){
										var audiInfo = getAttrVale(attrParams, "AudiInfo0898");
										if(audiInfo==null || $.trim(audiInfo) == ""){
											if(mytab != null){
												mytab.switchTo("产品信息");
											}
											alert("本省折扣低于6折，请填写审批工单号及名称信息！");
											return false;
										}
									}
								}
								
							}
							//-------add by chenzg@20180116---REQ201711150003关于新增物联卡本省折扣需求---end-----------
						}
						
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "APNNAME"){//appname的校验
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("APNNAME不能为空,请填写!");
									return false;
								}
							} else if(oneAttrCode == "Discount"){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("优惠的折扣率不能为空,请填写!");
									return false;
								} else {
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 1){
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
								}
							} else if(oneAttrCode == "PromiseUseMonths"){//在网时间校验
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("在网时间必须是整数");
										return false;
									}
									//var values = parseInt(oneAttrValue)%24;
									var values = parseInt(oneAttrValue);
									if(values < 24){
										alert("在网时间必须是大于等于24的整数!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										alert("折扣率低于6折!在网时间必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "CanShare"){//是否共享校验
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("是否可共享不能为空,请选择!");
									return false;
								}
							} else if(oneAttrCode == "MinimumOfYear"){//年承诺收入（元）
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("年承诺收入(元)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 50000){
										alert("年承诺收入(元)必须是大于等于50000!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										alert("折扣率低于6折!年承诺收入(元)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "BatchAccounts"){//入网用户数(张)
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("入网用户数(张)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 50000){
										alert("入网用户数(张)必须是大于等于50000!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										alert("折扣率低于6折!入网用户数(张)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									if(discount == true){
										alert("折扣率低于6折!审批文号必须填写");
										return false;
									}
								}
							}
						
						}
					
					}
				}
			}
		}
	}
	
	//==start
	if(data2 != null){
		var length = data2.length;
		var groupType = $('#GROUP_TYPE').val(); //集团企业类型
		var userFlag = "2";//用户类别
		var userAreaFlag = "3";
		
		for(var i=0;i<length;i++){//先查找个人智能网语音通信服务,取相应的属性值
			var allSelectedElements = data2.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist")
				&& elementId == "99011019")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "UserFlag" && oneAttrValue != null && oneAttrValue != ""){
							userFlag = oneAttrValue;
						} else if(oneAttrCode == "userAreaFlag" && oneAttrValue != null && oneAttrValue != ""){
							userAreaFlag = oneAttrValue;
						}
					}
				}
			}
		}
		
		for(var i=0;i<length;i++){//对个人智能网语音通信服务的一些属性限制
			var allSelectedElements = data2.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist")
				&& elementId == "99011019")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "IDDFlag" && userFlag == "1" && oneAttrValue == ""){
							if(mytab != null){
								mytab.switchTo("产品信息");
							}
							alert("用户类别为企业客户成员时,企业客户成员国际长途权限必选,不能为空!");
							return false;
						}else if(oneAttrCode == "AutoForwardFlag"){
							if(userFlag == "1" && oneAttrValue == ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("用户类别为企业客户成员时,是否支持自动前转必选,不能为空!");
								return false;
							}
							if(groupType == "2" && oneAttrValue == "1" && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,是否支持自动前转只能选择不支持选项!");
								return false;
							}
						}else if(oneAttrCode == "RoamingFlag"  && userFlag == "1" && oneAttrValue == ""){ 
							if(mytab != null){
								mytab.switchTo("产品信息");
							}
							alert("用户类别为企业客户成员时,国际漫游权限必选,不能为空!");
							return false;
						} else if(oneAttrCode == "UserClass"){
							if(userFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("用户类别为企业客户成员时,通话阀值的级别必填,不能为空!");
								return false;
							}
							if(oneAttrValue != null && oneAttrValue != ""){
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("通话阀值的级别必须是整数!");
									return false;
								}
								var values = parseInt(oneAttrValue);
								if(values <= 0){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("通话阀值的级别必须是大于0!请重新填写");
									return false;
								}
							}
						} else if(oneAttrCode == "LockFlag"){
							if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,被叫闭锁不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userShutFlag"){
							if(groupType == "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是普通企业,客户成员闭锁功能标识不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userWhiteNumFlag"){
							if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,白名单功能状态不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userAreaOperType"){
							if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项不是多省漫游,限制区域操作类型不能选择!");
								return false;
							} else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项是多省漫游,限制区域操作类型不能为空!");
								return false;
							}
						} else if(oneAttrCode == "userArea"){
							if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项不是多省漫游,限制区域列表不能填写!");
								return false;
							}else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项是多省漫游,限制区域列表不能为空");
								return false;
							}
						}
						
					}
				}
			}
		}
		
	}
	//end
	
	return true;
}

/**
 * 修改属性值
 * @param attrParams
 * @param attrCode
 * @param attrVal
 * @return
 */
function setAttrVale(attrParams, attrCode, attrVal){
	//alert("setAttrVale attrParams="+attrParams);
	for(var i=0;i<attrParams.length;i++){
		var param = attrParams.get(i);
		var oneAttrCode = param.get("ATTR_CODE");
		var oneAttrValue = param.get("ATTR_VALUE");
		if(oneAttrCode == attrCode){
			param.put("ATTR_VALUE",attrVal);
		}
	}
}
/**
 * 取属性值
 * @param attrParams
 * @param attrCode
 * @return
 */
function getAttrVale(attrParams, attrCode){
	var attrVal = "";
	//alert("getAttrVale attrParams="+attrParams);
	for(var i=0;i<attrParams.length;i++){
		var param = attrParams.get(i);
		var oneAttrCode = param.get("ATTR_CODE");
		var oneAttrValue = param.get("ATTR_VALUE");
		if(oneAttrCode == attrCode){
			attrVal = oneAttrValue;
			break;
		}
	}
	
	return attrVal;
}
