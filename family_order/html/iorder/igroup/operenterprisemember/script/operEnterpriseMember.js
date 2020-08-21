var ACTION_CREATE = "0";
var ACTION_DELETE = "1";
var ACTION_UPDATE = "2";
var ACTION_EXITS = "3";

//为便于成员操作时，将未区分集团还是成员操作逻辑执行
var ACTION_PASTE = "5"; // 暂停  
var ACTION_CONTINUE = "6"; // 恢复
var ACTION_PREDESTROY = "7"; // 预取消
var ACTION_PREDSTBACK = "8"; // 冷冻期恢复
/**
 * 页面提交
 */
function submitAll(el) 
{
	
	if(!checkBeforeSubmit())
	{
		return false;
	}
	
	//成员角色
	assembleRoleInfo();
	
	//付费类型
	assemblePayPlan();
	
	//是否立即生效
	assembleEffectNow();
	
	//获取附件信息
	assembleFile();
	
	//备注
	assembleRemark();
	
	PageData.submit();
	
}

/**
 * 备注
 */
function assembleRemark() {
	var commInfo = pageData.getCommonInfo();
	commInfo.put("REMARK", $("#cond_REMARK").val());
	pageData.setCommonInfo(commInfo);
}

function checkBeforeSubmit()
{
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	if(offerData.length == 0)
	{
		MessageBox.alert("提示信息", "请选择商品！");
		return false;
	}
	
	var groupCustInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if (groupCustInfo) {
		var custId = groupCustInfo.get("CUST_ID");
		var pCustId = $("#cond_CUST_ID").val();
		if (custId && custId != pCustId) {
			$.MessageBox.error("错误提示", "进行操作的集团客户和验证的集团客户不一致，请重新操作！");
		}
	} else {
		$.MessageBox.error("错误提示", "读取客户信息失败！");
	}
	
	var operType = $("#cond_OPER_TYPE").val();
	
	if(operType != "DstMb" && $("#cond_REL_SUBSCRIBER_ROLE").val() == "")
	{
		MessageBox.alert("提示信息", "请选择成员角色！");
		return false;
	}
	
	if(operType == "DstMb")
	{
		var remark = $("#cond_REMARK").val();
		if(remark == "")
		{
			$.validate.alerter.one($("#cond_REMARK")[0],"注销时备注不可为空！");
			return false;
		}
	}
	
	if(operType == "ChgMb")
	{
		var backUpData = PageData.getData($("#class_OfferDataBackup"));
		var offerData = PageData.getData($(".e_SelectOfferPart"));
		var changeSpecOrSubFlag=false;//是否变更商品特征以及子商品标记（false表示没有变更操作，true表示至少变更一个地方）
		
		var proParamCha = offerData.get("IS_CHA_VALUE_CHANGE");
		if(typeof(proParamCha) == "undefined"){
			MessageBox.alert("提示信息", "如果不需要修改订购信息,请先点开商品订购服务优惠的弹出按钮,如果配有商品特征,请点开商品特征后点击确定！");
			return false;
		}
		
        if(offerData.get("IS_CHA_VALUE_CHANGE")!='true'){//先过滤掉商品特征是否变更
			var subOffersList=offerData.get("SUBOFFERS");
			if(subOffersList){
                for(var i=0;i<subOffersList.length;i++){
                	var subOfferData=subOffersList.get(i);
					if(subOfferData.get("OPER_CODE")!='3'){//如果有一个子商品变更了，则认定可以通过变更(包括变更服务属性或者新增删除服务)
                        changeSpecOrSubFlag=true;
                        break;
					}
                }
			}
		}else{
            changeSpecOrSubFlag=true;
		}
		if(!changeSpecOrSubFlag){//主商品和子商品信息没有变更，比较成员角色是否变更
            var newRole = $("#cond_REL_SUBSCRIBER_ROLE").val();
            var oldRole = $("#old_REL_SUBSCRIBER_ROLE").val();
            if(newRole == oldRole){
                MessageBox.alert("提示信息", "您没有变更任何信息，无法提交！");
                return false;
            }
		}

	}
	
	var mainOfferLis = $("#mainOfferPart").find("div[class=side]");
	for(var i = 0; i < mainOfferLis.length; i++)
	{
		if($(mainOfferLis[i]).css("display") != "none")
		{
			MessageBox.alert("提示信息", "请先完成商品设置再提交！");
			return false;
		}
	}
	
	var switchOn = $("#prodDiv_SWITCH").val();
	if(switchOn == "on")
	{//商品设置区域打开时，不允许受理提交
		MessageBox.alert("提示信息", "请先提交右侧区域的商品设置！");
		return false;
	}

	var auditFlag = $("#auditFlag").val();
	if(auditFlag == "true")
	{
		var auditStaffId = $("#AUDIT_STAFF_ID").text();
		if(auditStaffId==null||auditStaffId==''){
			MessageBox.alert("提示信息", "请选择稽核人员！");
			return false;
		}
		var fileList = $("#MEB_VOUCHER_FILE_LIST").val();
		if(fileList==null||fileList==''){
			MessageBox.alert("提示信息", "请上传凭证信息！");
			return false;
		}
	}
	
	
	
	return true;
}

/**
 * 设置集团客户
 */
function saveEcSubscriberInfo()
{
	var subsId = $("#cond_EC_USER_ID").val();
	var accessNum = $("#cond_EC_SERIAL_NUMBER").val();
	
	var ecSubscriber = new $.DataMap();
	ecSubscriber.put("USER_ID", subsId);
	ecSubscriber.put("SERIAL_NUMBER", accessNum);
	
	pageData.setSubscriber(ecSubscriber);
}

/**
 * 设置成员用户信息
 * @param data
 */
function addMemSubscriber(data)
{
	var accessNum = data.get("SERIAL_NUMBER");
	var subsId = data.get("USER_ID");
	
	var memSubscriber = new $.DataMap();
	memSubscriber.put("SERIAL_NUMBER", accessNum);
	memSubscriber.put("USER_ID", subsId);
	memSubscriber.put("EPARCHY_CODE", data.get("EPARCHY_CODE"));
	pageData.setMemSubscriber(memSubscriber);
	
}


/**
 * 生效方式变更
 * @param 
 */
function changeEffectTag()
{
	if(!isSwitchOff())//商品设置区域不关，不让变更生失效方式
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		//把已变更的值改回去
		var effectNow = $("#EFFECT_NOW").val();
		if("1" == effectNow){
			$("#EFFECT_NOW").val("0");
		}else{
			$("#EFFECT_NOW").val("1");
		}
		return ;
	}
	//更改了是否生效，标记一下
	$("#IS_CHANGE_EFFECT").val("true");
	// 主体商品
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	
	//取页面立即生效标记
	var effectNow = $("#EFFECT_NOW").val();
	
	var validDate = getFirstDayOfNextMonth();
	if(effectNow == "0")
	{   //下月生效
		validDate = getFirstDayOfNextMonth();
	}else{
		validDate = getNowSysTime();
	}
	var subOffers = mainOffer.get("SUBOFFERS");
	if(subOffers)
	{
		for(var i = 0, sizeI = subOffers.length; i < sizeI; i++)
		{
			var subOffer = subOffers.get(i);
			if(subOffer.get("OFFER_TYPE") == "P")
			{
				var subChildOffers = subOffer.get("SUBOFFERS");
				if(!subChildOffers)
				{
					continue;
				}
				for(var j = 0, sizeJ = subChildOffers.length; j < sizeJ; j++)
				{
					var subChildOffer = subChildOffers.get(j);
					if(subChildOffer.get("OFFER_TYPE") != "D")
					{
						continue;
					}
					subChildOffer.put("START_DATE", validDate);
				}
			}
			else if(subOffer.get("OFFER_TYPE") == "D")
			{
				subOffer.put("START_DATE", validDate);
			}
		}
	}
	

	PageData.setData($(".e_SelectOfferPart"),mainOffer);
}
/**
 * 成员角色信息
 */
function assembleRoleInfo()
{
	if($("#cond_OPER_TYPE").val() == "DstMb")
	{
		return ;
	}
	var roleCodeB = $("#cond_REL_SUBSCRIBER_ROLE").val();
	var commonData = pageData.getCommonInfo();
	commonData.put("ROLE_CODE_B", roleCodeB);
	pageData.setCommonInfo(commonData);
}

/**
 * 付费类型
 */
function assemblePayPlan()
{
	if($("#cond_OPER_TYPE").val() != "CrtMb")
	{
		return ;
	}
	
	var payPlanCode = $("#PAY_PLAN_SEL_PLAN_TYPE").val();
	var feeType = $("#cond_FEE_TYPE").val();
	var limitType = $("#cond_LIMIT_TYPE").val();
	var limit = $("#cond_LIMIT").val();
	var commonData = pageData.getCommonInfo();
	commonData.put("PLAN_TYPE_CODE", payPlanCode);
	commonData.put("FEE_TYPE", feeType);
	commonData.put("LIMIT_TYPE", limitType);
	commonData.put("LIMIT", limit);
	pageData.setCommonInfo(commonData);
}

/**
 * 资费类商品是否立即生效
 */
function assembleEffectNow()
{
	if($("#cond_OPER_TYPE").val() != "CrtMb")
	{
		return ;
	}
	
	var effectNow = $("#EFFECT_NOW").val();
	var effect=false;
	if("1"==effectNow){
		effect=true;
	}
	
	var commonData = pageData.getCommonInfo();
	commonData.put("EFFECT_NOW", effect);
	pageData.setCommonInfo(commonData);
}

/**
* 合同信息
*/
function assembleFile()
{
	
	var fileList = $("#MEB_FILE_LIST").val(); 
	var fileShow = $("#MEB_FILE_SHOW").val();
	var commonData = pageData.getCommonInfo();
	commonData.put("MEB_FILE_LIST", fileList);
	commonData.put("MEB_FILE_SHOW", fileShow);
	pageData.setCommonInfo(commonData);
}



/**
 * 重新验证手机号码，需要刷新目录
 * @param subInfo
 */
function refreshEnterpriseCatalog(subInfo)
{
	if(!memberSubInsIdCache)
	{
		memberSubInsIdCache = subInfo.get("USER_ID");
//		initCrm();
		return ;
	}
	
	showOpenInfo();
	$("#mainOfferPart").css("display", "none");
	$("#AttachOfferPart").css("display", "none");
	
	var param = "";
	if($("#kfFlag").val() == "true")
	{
		param += "&KF_FLAG=true";
	}
	
	$.beginPageLoading();
	$.ajax.submit("", "", param, "OfferTypePart,OfferListPart,mainOfferPart,AttachOfferPart", function(data){
		$.endPageLoading();
		
//		initCrm();
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function initCrm()
{
	var catalogTypeObj = document.getElementById("10014"); //默认选中10014-已订购商品
	enterpriseCatalog.chooseOfferCategory(catalogTypeObj);
	enterpriseCatalog.setOfferListDivSize();
	showPopup('popup', 'offerPopupItem');
	$("#prodDiv_SWITCH").val("off");
}



//初始化商品目录组件
function initOfferCategoryPopupItem()
{	
	var isValid = $("#cond_IS_VALIDATE_SUCCESS").val();
	var accessNum = $("#cond_SERIAL_NUMBER_INPUT").val(); //页面输入框的号码
	var validAccessNum = $("#cond_VALID_SERIAL_NUMBER").val(); //已经验证成功的号码
	if(isValid == "false" || accessNum != validAccessNum)
	{
		MessageBox.error("错误信息", "请先验证成员服务号码！");
		return false;
	}
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	
	enterpriseCatalog.setOfferListDivSize();
	showPopup('popup', enterpriseCatalog.id, true);
}

function chooseOfferInfo(userId, operType)
{	
	var cataDataStr = $("#CATADATA_"+userId).text();
	var cataData = new Wade.DataMap(cataDataStr);
	
	var offerId = cataData.get("OFFER_ID");                    //商品编码(集团主商品)
	var offerCode = cataData.get("OFFER_CODE"); 
	var ecSubscriberInsId = cataData.get("USER_ID"); //集团用户标识
	var ecAccessNum = cataData.get("SERIAL_NUMBER");              //集团服务号码
	var ecBrand = cataData.get("BRAND_CODE");                       //集团商品品牌
	var custId = cataData.get("CUST_ID");                      //选择商品的集团标识
	var groupId = cataData.get("GROUP_ID");                    //选择商品的集团编码
	var groupName = cataData.get("GROUP_NAME");                //选择商品的集团名称
	var isBatch = cataData.get("IS_BATCH");                    //是否批量业务【true或者false字符串】
	var batchType = cataData.get("BATCH_OPER_TYPE", "");       //批量操作类型
	
	if(ecBrand == "BOSG")
	{//bboss子商品编码
		var subOfferId = cataData.get("SUB_OFFER_ID");
		$("#cond_BBOSS_SUB_OFFER_ID").val(subOfferId);
	}
	if (operType == "DstMb" && ecBrand == 'WLWG'){
		MessageBox.alert("提示信息" ,"注销集团成员之后若该集团为空集团，将无法新增集团成员！");
	}
	//设置销售品隐藏域
	$("#cond_OFFER_ID").val(offerId);
	$("#cond_OFFER_CODE").val(offerCode);
	$("#cond_OPER_TYPE").val(operType);
	$("#cond_EC_USER_ID").val(ecSubscriberInsId);
	$("#cond_EC_SERIAL_NUMBER").val(ecAccessNum);
	$("#cond_EC_BRAND").val(ecBrand);
	
	//清空销售品设置组件的缓存数据
	$("#prodDiv_OFFER_ID").val("");
	$("#prodDiv_TOP_OFFER_ID").val("");
	$("#prodDiv_OFFER_LEVEL").val("");
	
	if($.enterpriseLogin && $.enterpriseLogin.isLogin())
	{
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		cachedCustId = custInfo.get("CUST_ID");

		$("#cond_CUST_ID").val(cachedCustId);
	}

	if(operType == "CrtMb")
	{//成员商品开户

		//加载必选销售品信息
		initRequriedOffer(offerId, "CrtMb", isBatch, cachedCustId);
		
	}
	else if(operType == "ChgMb" || operType == "DstMb")
	{//成员变更、注销
		
		//判断当前登陆集团与选择变更或注销的商品归属的集团是否一致，不一致则刷新登陆集团
		if(cachedCustId != custId)
		{
			if($("#kfFlag").val() == "true")
			{
				$.enterpriseLogin.refreshGroupInfo(groupId);
				$("#cond_CUST_ID").val(custId);
				cachedCustId = custId;

				initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
			}
			else
			{
				MessageBox.alert("提示信息", "您正在处理<span class='e_red'>【"+groupId+" : "+groupName+"】</span>的业务，认证信息会随之变化！", function(btn){
					$.enterpriseLogin.refreshGroupInfo(groupId);
					$("#cond_CUST_ID").val(custId);
					cachedCustId = custId;

					if(isBatch == 'true')
					{
						initRequriedOffer(offerId, operType, isBatch, custId);
					}
					else
					{
						initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
					}
				});
			}
			
		}
		else
		{
			if(isBatch == 'true')
			{
				initRequriedOffer(offerId, operType, isBatch, custId);
			}
			else
			{
				initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
			}
		}
	}
}

//加载必选销售品信息
function initRequriedOffer(offerId, batOperType, isBatch, custId)
{
	var ecSubscriberInsId = $("#cond_EC_USER_ID").val();
	var mebSubscriberInsId = $("#cond_USER_ID").val();
	var mebSn = $("#cond_VALID_SERIAL_NUMBER").val();
	var ifCentreType = $("#IF_CENTRETYPE").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brand = $("#cond_EC_BRAND").val();
	var checkMode = $("#cond_CHECK_MODE").val();
	
	
	var params = "&OFFER_ID="+offerId;
	params += "&BAT_OPER_TYPE="+batOperType;
	params += "&CUST_ID="+custId;
	params += "&EC_USER_ID="+ecSubscriberInsId;
	params += "&MEB_USER_ID="+mebSubscriberInsId;
	params += "&MEB_SERIAL_NUMBER="+mebSn;
	params += "&BRAND_CODE="+brand;
	params += "&OPER_TYPE="+operType;
	params += "&cond_CHECK_MODE="+checkMode;

	//bboss产品加集团商品用户id
	var merchEcSubscriberInsId = $("#cond_EC_MERCH_USER_ID").val();
	if(merchEcSubscriberInsId!=""&&merchEcSubscriberInsId!="undefined"){
		params += "&EC_MERCH_USER_ID="+merchEcSubscriberInsId;
	}
	
	if (isBatch == "true")
	{
		params += "&IS_BATCH=true";
	}
	if (ifCentreType)
	{
		params += "&IF_CENTRETYPE="+ifCentreType;
	}

	$.beginPageLoading("数据加载中......");//SelectAttachOfferPart,
	$.ajax.submit("", "queryOfferInfo", params, "mainOfferPart,OtherInfo", function(data){
		$.endPageLoading();
		var offer = data.get("OFFER_DATA");

		var offerData = new Wade.DataMap();
		offerData.put("OFFER_ID", offer.get("OFFER_ID"));
		offerData.put("OFFER_CODE", offer.get("OFFER_CODE"));
		offerData.put("OFFER_NAME", offer.get("OFFER_NAME"));
		offerData.put("BRAND_CODE", offer.get("BRAND_CODE"));

		if(batOperType == "DstMb")
		{
			offerData.put("OPER_CODE", ACTION_DELETE);
			offerData.put("E_USER_ID", offer.get("E_USER_ID"));
		}
		else if(batOperType == "ChgMb")
		{
			offerData.put("OPER_CODE", ACTION_UPDATE);
			offerData.put("E_USER_ID", offer.get("E_USER_ID"));
		}
		else
		{
			offerData.put("OPER_CODE", ACTION_CREATE);
		}
		PageData.setData($("#class_"+offer.get("OFFER_ID")), offerData);
		
/*		//设置资费类商品立即生效标记
		var effectFlag = data.get("IsNextMonthEffect");
		if(effectFlag == "true")
		{//下月生效，且不可编辑
			$("#cond_EFFECT_NOW").val("0");
			$("#cond_EFFECT_NOW").attr("disabled", true);
		}
		else
		{
			$("#cond_EFFECT_NOW").val("1");
			$("#cond_EFFECT_NOW").attr("disabled", false);
		}*/
		
		//是否显示待设置(集团定制特殊处理)
		if(data.get("IS_SHOW_SET_TAG") == "false")
		{
			$("#li_"+offer.get("OFFER_ID")).find(".side").css("display", "none");
		}
		
		//处理批量金库认证
		if (isBatch == "true")
		{
			var cashboxParam = data.get("CASHBOX_PARAM");
			$("#cond_CASHBOX_SWITCH").val(cashboxParam.get("CASHBOX_SWITCH"));
			$("#cond_RIGHT_CODE").val(cashboxParam.get("RIGHT_CODE"));
			$("#cond_NEED_CASHBOX").val(cashboxParam.get("NEED_CASHBOX"));
		}
		$("#TRADE_TYPE_CODE").val(offer.get("TRADE_TYPE_CODE"));
		// 显示成员新增页面元素
		showOpenInfo();
		
		//手动刷新scroller组件
		editMainScroll.refresh();
		
		//关闭集团目录popupItem
		enterpriseCatalog.closeEcCataPopupItem();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		if(error_info.indexOf("CRM_ORDER_125") != -1 || error_code == "CRM_ORDER_125"){
			var message = "";
			if(batOperType == "CrtMb")
			{
				message = isBatch == "true" ? "系统不支持该产品进行批量成员新增操作":"系统不支持该产品进行成员新增操作";
			}else if(batOperType == "ChgMb"){
				message = isBatch == "true" ? "系统不支持该产品进行批量成员变更操作":"系统不支持该产品进行成员变更操作";
			}else if(batOperType == "DstMb"){
				message = isBatch == "true" ? "系统不支持该产品进行批量成员注销操作":"系统不支持该产品进行成员注销操作";
			}
			MessageBox.alert("提示信息", message);
		}else{
			showDetailErrorInfo(error_code,error_info,derror);
		}
    });
}


//加载成员已订购的销售品信息(入参：集团用户id，集团商品标识)
function initOpenedOfferForMeb(ecSubscriberInsId, ecOfferId, custId)
{
	var subOfferId = $("#cond_BBOSS_SUB_OFFER_ID").val(); //bboss成员产品id
	var mebSubscriberInsId = $("#cond_USER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brand = $("#cond_EC_BRAND").val();
	var mebSn = $("#cond_VALID_SERIAL_NUMBER").val();
	var mebEparchyCode = $("#cond_USER_EPARCHY_CODE").val();
	var checkMode = $("#cond_CHECK_MODE").val();

	var params = "&OFFER_ID="+ecOfferId;
	params += "&SUB_OFFER_ID="+subOfferId;
	params += "&EC_USER_ID="+ecSubscriberInsId;
	params += "&MEB_USER_ID="+mebSubscriberInsId;
	params += "&OPER_TYPE="+operType;
	params += "&CUST_ID="+custId;
	params += "&BRAND_CODE="+brand;
	params += "&MEB_SERIAL_NUMBER="+mebSn;
	params += "&MEB_EPARCHY_CODE="+mebEparchyCode;
	params += "&cond_CHECK_MODE="+checkMode;


	var batType = parent.$("#BATCH_OPER_TYPE").val();
	if (batType) {
		params += "&BATCH_TYPE="+batType;
	}

	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryInsOfferInfoForMeb", params, "mainOfferPart,OtherInfo", function(data){
		$.endPageLoading();
		//将数据对象设置到页面上
		var dataset = data.get("OFFER_DATAS");
		for(var i = 0, size = dataset.length; i < size; i++)
		{
			var offer = dataset.get(i);
			var offerId = offer.get("OFFER_ID");
			var offerData = offer.get("OFFER_DATA");
			PageData.setData($("#class_"+offerId), offerData);
			
			//缓存一份原始数据对象
			PageData.setData($("#class_OfferDataBackup"), offerData);
			
			$("#TRADE_TYPE_CODE").val(offerData.get("TRADE_TYPE_CODE"));

		}
		
		var selGroupOfferData = data.get("SELECT_GROUP_OFFER");
		PageData.setData($("#class_SELECT_GROUP_OFFER"), selGroupOfferData);
		
		if(operType == "ChgMb")
		{
			showChangeInfo();
		}
		else if(operType == "DstMb")
		{
			showLogoutInfo();
		}
		//手动刷新scroller组件
		editMainScroll.refresh();
		
		//关闭集团目录popupItem
		enterpriseCatalog.closeEcCataPopupItem();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		if(error_info.indexOf("CRM_ORDER_125") != -1 || error_code == "CRM_ORDER_125"){
			var message = "";
			if(operType == "ChgMb"){
				message = "系统不支持该产品进行成员变更操作";
			}else if(operType == "DstMb"){
				message = "系统不支持该产品进行成员注销操作";
			}
			MessageBox.alert("提示信息", message);
		}else{
			showDetailErrorInfo(error_code,error_info,derror);
		}
    });
}

//删除可选子销售品信息(入参：可选子销售品编码，主销售品编码)
function deleteOptionalOffer(optOfferId, offerId)
{
	MessageBox.confirm("提示信息", "是否删除当前子商品？", function(btn){
		if(btn == "cancel")
		{
			return;
		}
		
		//将子销售品的选择标记取消
		$("#"+optOfferId).attr("checked", false);
		$("#"+optOfferId).attr("disabled", false);
		
		//删除数据结构
		var offerData = PageData.getData($("#class_"+offerId));
		var offers = offerData.get("SUBOFFERS");
		for(var i = offers.length; i > 0; i--)
		{
			var offer = offers.get(i-1);
			if(offer.get("OFFER_ID") == optOfferId && typeof(offer.get("OFFER_INS_ID")) == "undefined")
			{
				offers.removeAt(i-1);
				$("#div_"+optOfferId).remove();
			}
			else if(offer.get("OFFER_ID") == optOfferId && typeof(offer.get("OFFER_INS_ID")) != "undefined")
			{
				offer.put("OPER_CODE", ACTION_DELETE);
				offer = deleteSubOfferChild(offer);
				$("#div_"+optOfferId).remove();
			}
		}
		if(offers.length == 0)
		{
			offerData.remove(offers);
		}
		PageData.setData($("#class_"+offerId), offerData);

	});
}

function showFeeLimitInfo()
{
	var payType = $("#cond_PAY_TYPE").val();
	var isFixedPay = $("#cond_ISFIXEDPAY").val();
	if(payType == "G" && isFixedPay == "Y")
	{
		$("#FEE_LIMIT_PART").css("display", "");
	}
	else
	{
		$("#FEE_LIMIT_PART").css("display", "none");
	}
}

function feeTypeChg()
{
	var feeType = $("#cond_FEE_TYPE").val();
	if(feeType == "1")
	{
		$("#cond_LIMIT_TYPE").attr("disabled", true);
		$("#cond_LIMIT").attr("disabled", true);
	}
	else if(feeType == "2")
	{
		$("#cond_LIMIT_TYPE").attr("disabled", false);
		$("#cond_LIMIT").attr("disabled", false);
	}
}

//显示注销信息
function showLogoutInfo()
{
	$("#OpenSubmit").css("display","none");
	$("#ChgSubmit").css("display","none");
	$("#DelSubmit").css("display","");
	$("#roleInfoPart").css("display","none");
	
	var offerCode=$("#cond_OFFER_CODE").val();
	if("8000"!=offerCode){
		$("#remarkInfo").addClass("required");
	}
	$("#mainOfferPart").css("display","");
}

//显示变更信息
function showChangeInfo()
{
	$("#OpenSubmit").css("display","none");
	$("#ChgSubmit").css("display","");
	$("#DelSubmit").css("display","none");
	
	$("#roleInfoPart").css("display","");
	
	$("#mainOfferPart").css("display","");
}

//显示开户信息
function showOpenInfo()
{
	$("#OpenSubmit").css("display","");
	$("#ChgSubmit").css("display","none");
	$("#DelSubmit").css("display","none");
	
	$("#mainOfferPart").css("display","");
}

//校验费用类别、限定方式、限定值
function checkFeeLimitInfo()
{
	var feeType = $("#cond_FEE_TYPE").val();
	var limitType = $("#cond_LIMIT_TYPE").val();
   	var limit = $("#cond_LIMIT").val();
   	if("2" == feeType && "0" == limitType)
   	{
   		MessageBox.alert("提示信息", "当选择部分付费时,须选择限定方式为：金额或者比例！");
        return false;
   	}
  	
  	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;

    if (!reg.test(limit) && limit !='')
    {
    	MessageBox.alert("提示信息", "限定值必须为数字,请重新输入！");
        return false;
    }
    if (parseInt(limit) < 0 && limit !='')
    {
    	MessageBox.alert("提示信息", "限定值不能小于0，请重新输入！");
        return false;
    }
    if(limitType == "2")
    {
    	var reg = /^[1-9]{1}[0-9]{0,1}$/; //不包括0和100
    	if(!reg.test(limit))
    	{
    		MessageBox.alert("提示信息", "比例必须是1--99的整数");
			return false;
		}
	}
	if(limitType == "1")
	{
	 	var index = limit.indexOf("0");  
        var length = limit.length;  
        if(limit == "0" || limit == "0." || limit == "0.0" || limit == "0.00")
    	{
    		limit == "0";
    	}
        if(index == 0 && length > 1)
        {/*0开头的数字串*/  
            var reg = /^[0]{1}[.]{1}[0-9]{1,2}$/;  
            if(!reg.test(limit))
            {
            	MessageBox.alert("提示信息", "请输入有效的金额！"); 
                return false;  
            }
        }
        else
        {/*非0开头的数字*/  
            var reg = /^[1-9]{1}[0-9]{0,10}[.]{0,1}[0-9]{0,2}$/;  
            if(!reg.test(limit))
            {  
            	MessageBox.alert("提示信息", "请输入有效的金额！"); 
                return false;  
            }
        }
          
		if(parseInt(limit) > 500000 && limit !='')
		{
			MessageBox.alert("提示信息", "限定方式为金额时，限定值不能大于500000，请重新输入！");
			return false;
		}
		
		if(limitType == '1' && parseFloat(limit) < 0.01 && limit != '')
		{
			MessageBox.alert("提示信息", "限定方式为金额时，限定值不能小于0.01，请重新输入！");
			return false;
		}
	}
	return true;
}

/**
 * 控制el元素下后面size个li元素的显隐
 * @param el
 * @param size
 */
function toggleLi(el, size) {
	if (!size) {
		return;
	}
	var ul = $(el).closest("ul");
	var li = $(el).closest("li");
	
	for (var i = 0; i < size; i++) {
		li = li.next("li");
		var dis = li.css("display");
		if (dis == "none") {
			li.css("display", "");
		} else {
			li.css("display", "none");
		}
	}
	//手动刷新scroller组件
	editMainScroll.refresh();
}


//上面的日历js可删除，可用下面的，弹出日期可用 popupDateSelect 方法
//var activeDateField;
//function popupDateSelect(el, popupItem) {
//	var item = $("#"+popupItem);
//	var popupGroup = item.closest(".c_popupGroup");
//	var popup = item.closest(".c_popup");
//	var cal = eval(item.find(".c_calendar").attr("id"));
//	
//	var timeout = 0;
//	if (item.hasClass("c_popupItem-show")) {
//		item.removeClass("c_popupItem-show");
//		timeout = 450;
//	}
//	
//	setTimeout(function(){
//		if (popupGroup.attr("level") > 1) {
//			forwardPopup(el, popupItem);
//		} else {
//			showPopup(popup.attr("id"), popupItem);
//		}
//		var inputEle = $(el).find("input");
//		activeDateField = (inputEle.val() == undefined) ? $(el) : inputEle;
//		cal.val(activeDateField.val());
//	}, timeout);
//	
//}

/** 此段代码没有直接引用ecbusipage.js是因为成员商品受理可以不依赖集团登陆  开始 **/
var cachedCustId;
//页面激活时触发该方法，用于校验页面激活前后，登陆的政企客户是否发生改变
function onActive()
{
	var operType = $("#cond_OPER_TYPE").val();
	//未登录，但是之前有登录或者已经选择了操作，刷新
	if((!$.enterpriseLogin || !$.enterpriseLogin.isLogin()) && (operType || cachedCustId))
	{
		location.reload();
	}else if($.enterpriseLogin && $.enterpriseLogin.isLogin() && cachedCustId){//登陆了，并且老客户ID不是空，判断一下是否有更改
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		var loginCustId = custInfo.get("CUST_ID");
		if (loginCustId != cachedCustId) {
			cachedCustId = loginCustId;
			$.enterpriseLogin.refreshActiveNav();
		} 
	}
}

$.enterpriseLogin = (function() {
	var login, count = 0;
	if($.os.phone)
	{
		var obj = window;
		while(true)
  		{
  			var frame = obj.document.getElementById("custAuthFrame");	
   			if(frame != null &&  frame)
   			{
				login = frame.contentWindow.ecLogin;
				break;
   			}
   			obj = obj.parent;
   			count ++;
   			if (count > 2) {
				break;
			}
  		}
	}else{
		var scope = window;
		while (!(login = scope.parent.$.enterpriseLogin)) {
			scope = scope.parent;
			count++;
			if (count > 2) {
				break;
			}
		}
	}
	return login;
})();

$(function(){
	if($.enterpriseLogin && $.enterpriseLogin.isLogin()){
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		var loginCustId = custInfo.get("CUST_ID");
		cachedCustId = loginCustId;
		$("#cond_CUST_ID").val(loginCustId);
	}

});

/** 此段代码没有直接引用ecbusipage.js是因为成员商品受理可以不依赖集团登陆  结束 **/

/**
 * 非空判断:【空：true】【非空：false】
 * @param {} e
 */
 
function isNull(arg) {
	return !arg && arg!==0 && typeof arg!=="boolean"?true:false;
}


(function ($) {
    $.fn.extend({
        changeSelectData: function ($val_col,$name_col,dataList) {
        	var length = dataList.length;
        	
			if(length == 0) {
				$(this).empty();
				$(this).html();
				return false;
			}
			
			if (isNull($val_col)) {
				$val_col = $name_col;
			}
			
			if (isNull($name_col)) {
				$name_col = $val_col;
			}
			
			if (isNull($val_col) && isNull($name_col)) {
				alert("[CODE_NAME],[CODE_VALUE]未指定");
				return false;
			}
			
			var _html = "<li class='link' idx='0' title='--请选择--' val=''><div class='main'>--请选择--</div></li>";
			var index = 0;
			dataList.each(function(_val){
				var title = _val.get($name_col);
				var val = _val.get($val_col);
				
				if (isNull(title) || isNull(val)) {
					_html = _html + "";
				} else {
					_html = _html + "<li class='link' idx='" + (index + 1) +"' title='" + title + "' val='" + val +"'><div class='main'>" + title + "</div></li>";
					index ++;
				}
			})
			
			$(this).empty();
			$(this).html(_html);
        	
			if (_html == "<li class='link' idx='0' title='--请选择--' val=''><div class='main'>--请选择--</div></li>") {
				return false;
			}
			return true;
        }
    
    });
    
    $.fn.extend({
    	changeSegmentData : function (componentName,$name_col,$val_col,dataList) {
    		
    		var length = dataList.length;
    		
    		if(length == 0) {
    			$(this).find("span").remove();
    			return false;
    		}
			
			if (isNull($val_col)) {
				$val_col = $name_col;
			}
			
			if (isNull($name_col)) {
				$name_col = $val_col;
			}
			
			if (isNull($val_col) && isNull($name_col)) {
				alert("[CODE_NAME],[CODE_VALUE]未指定");
				return false;
			}
    		var _html = "";
    		var index = 0;
    		
    		$(this).find("span").remove();
    		dataList.each(function(_val){
    			var title = _val.get($name_col);
    			var val = _val.get($val_col);
    			
    			if (!isNull(title) && !isNull(val)) {
    				componentName.append(title,val);
    			} 
    		})
    		
    		return true;
    	}
    });
})(Wade);

//用了搜索框后的查询逻辑
function chooseOfferInfo2(el, operType)
{	
	
	var offerId = $(el).attr("OFFER_ID");                    //商品编码(集团主商品)
	var offerCode = $(el).attr("OFFER_CODE"); 
	var ecSubscriberInsId = $(el).attr("USER_ID"); //集团用户标识
	var ecAccessNum = $(el).attr("SERIAL_NUMBER");              //集团服务号码
	var ecBrand = $(el).attr("BRAND_CODE");                       //集团商品品牌
	var custId = $(el).attr("CUST_ID");                      //选择商品的集团标识
	var groupId = $(el).attr("GROUP_ID");                    //选择商品的集团编码
	var groupName = $(el).attr("GROUP_NAME");                //选择商品的集团名称
	var isBatch = $(el).attr("IS_BATCH");                    //是否批量业务【true或者false字符串】
	var batchType = $(el).attr("BATCH_OPER_TYPE", "");       //批量操作类型
	
	if(ecBrand == "BOSG")
	{//bboss子商品编码
		var subOfferId = $(el).attr("SUB_OFFER_ID");
		$("#cond_BBOSS_SUB_OFFER_ID").val(subOfferId);
	}
	
	//设置销售品隐藏域
	$("#cond_OFFER_ID").val(offerId);
	$("#cond_OFFER_CODE").val(offerCode);
	$("#cond_OPER_TYPE").val(operType);
	$("#cond_EC_USER_ID").val(ecSubscriberInsId);
	$("#cond_EC_SERIAL_NUMBER").val(ecAccessNum);
	$("#cond_EC_BRAND").val(ecBrand);
	
	//清空销售品设置组件的缓存数据
	$("#prodDiv_OFFER_ID").val("");
	$("#prodDiv_TOP_OFFER_ID").val("");
	$("#prodDiv_OFFER_LEVEL").val("");
	
	if($.enterpriseLogin && $.enterpriseLogin.isLogin())
	{
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		cachedCustId = custInfo.get("CUST_ID");

		$("#cond_CUST_ID").val(cachedCustId);
	}

	if(operType == "CrtMb")
	{//成员商品开户

		//加载必选销售品信息
		initRequriedOffer(offerId, "CrtMb", isBatch, cachedCustId);
		
	}
	else if(operType == "ChgMb" || operType == "DstMb")
	{//成员变更、注销
		
		//判断当前登陆集团与选择变更或注销的商品归属的集团是否一致，不一致则刷新登陆集团
		if(cachedCustId != custId)
		{
			if($("#kfFlag").val() == "true")
			{
				$.enterpriseLogin.refreshGroupInfo(groupId);
				$("#cond_CUST_ID").val(custId);
				cachedCustId = custId;

				initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
			}
			else
			{
				MessageBox.alert("提示信息", "您正在处理<span class='e_red'>【"+groupId+" : "+groupName+"】</span>的业务，认证信息会随之变化！", function(btn){
					$.enterpriseLogin.refreshGroupInfo(groupId);
					$("#cond_CUST_ID").val(custId);
					cachedCustId = custId;

					if(isBatch == 'true')
					{
						initRequriedOffer(offerId, operType, isBatch, custId);
					}
					else
					{
						initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
					}
				});
			}
			
		}
		else
		{
			if(isBatch == 'true')
			{
				initRequriedOffer(offerId, operType, isBatch, custId);
			}
			else
			{
				initOpenedOfferForMeb(ecSubscriberInsId, offerId, custId);
			}
		}
	}
}


/*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
function showAuditAddPopup(el) {
	showPopup('popup', 'auditPopupItem', true);
}
//稽核操作：选择稽核员
function selectAudit(el)
{
	$("#ecAccountULAudit li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	var auditId = $(el).attr("id");
	var html = "<span class='text' >"+auditId+"</span>";
	html += "<span id='AUDIT_STAFF_ID' style='display:none'>"+auditId+"</span>";
	$("#i_auditSelPart .value").html(html);
	
	hidePopup('popup');
}
/**
 * 稽核信息拼装
 */
function saveAuditInfo() {
	var auditId = $("#AUDIT_STAFF_ID").html();
	var auditIdInfo = new Wade.DataMap();
	auditIdInfo.put("AUDIT_STAFF_ID", auditId);
	var fileList = $("#MEB_VOUCHER_FILE_LIST").val();
	if(fileList!=null&&fileList!=''){
		auditIdInfo.put("MEB_VOUCHER_FILE_LIST", fileList);
	}
	return auditIdInfo;
}



function selectProductAuditPopupItem(obj,auditNo,auditName){
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryAuditInfo", "&STAFF_ID="+auditNo+"&STAFF_NAME="+auditName, "productAuditPopupItem", function(data){

		$.endPageLoading();
		//手动刷新scroller组件
		editMainScroll.refresh();
		forwardPopup(obj, 'productAuditPopupItem');
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		
    });
}


//绑定上传组件清除按钮事件
function clearUpFile(){
	$("#UP_FILE_TEXT").val("");
	$("#MEB_VOUCHER_FILE_LIST").val("");
}
//多文件上传,绑定上传组件确定按钮事件
function okUpFile(){
	var obj = $("#FILE_UPLOAD").val();
	$("#UP_FILE_TEXT").val(obj.NAME);
	$("#UP_FILE_TEXT").attr('tip',obj.NAME);
	$("#MEB_VOUCHER_FILE_LIST").val(obj.ID);
	hidePopup('popup','UI-popup-upload');
}

/*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/


