var ACTION_CREATE = "0";
var ACTION_DELETE = "1";
var ACTION_UPDATE = "2";
var ACTION_EXITS = "3";
var ACTION_PASTE = "5"; // 暂停
var ACTION_CONTINUE = "6"; // 恢复
var ACTION_PREDESTROY = "7"; // 预取消
var ACTION_PREDSTBACK = "8"; // 冷冻期恢复

function submitAll(el) {
	
	if(!checkBeforeSubmit())
	{
		return false;
	}
	
	//受理初始化费用信息
    var  operType=$("#cond_OPER_TYPE").val();
	if(operType == "CrtUs"){
		 
		initDefaultFee();
	}
	
	//检查费用
	if($.feeMgr.checkFee(PageData.submit,cancleButtom)){
		PageData.submit();
	}
}


function cancleButtom(){
	return false;
}
function checkBeforeSubmit()
{
	// 登陆校验
	if (!$.enterpriseLogin.isLogin()) {
		MessageBox.error("错误信息", "请先验证的集团客户！");
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
	
	var offerId = $("#cond_OFFER_ID").val();
	if(offerId == null || offerId == "")
	{
		MessageBox.error("错误信息", "请选择商品！");
		return false;
	}
	
	if(!checkDL100()){
		return false;
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
	
	var brand=$("#cond_EC_BRAND").val();
	if("BOSG"==brand&&!checkBBossEnterPrise()){
		return false;
	}
	var offerCode=$("#cond_OFFER_CODE").val();
	if("8000"==offerCode&&!checkVPMNEnterPrise()){
		return false;
	}
	
	if(!isAccessNumSuccess())
	{
		MessageBox.error("错误信息", "请校验服务号码！");
		return false;
	}
	var operType = $("#cond_OPER_TYPE").val();
	if(operType == "DstUs")
	{
		if("BOSG"==brand){
			var contactorInfo = $("#CONTACTOR_INFOS").val();
			if(contactorInfo == "")
			{
				MessageBox.alert("提示信息", "BBOSS集团产品注销时,请先打开商品特征页面填写联系人信息！");
				return false;
			}
		}

		var removeReason = $("#cond_REMOVE_REASON").val();
		if(removeReason == "")
		{
			MessageBox.error("错误信息", "请选择销户原因！");
			return false;
		}
		var remark = $("#cond_REMARK").val();
		if(remark == "")
		{
			$.validate.alerter.one($("#cond_REMARK")[0],"注销时备注不可为空！");
			return false;
		}
		
	}
	else if(operType == "ChgUs")
	{
		var backUpData = PageData.getData($("#class_OfferDataBackup"));//老数据
		var offerData = PageData.getData($(".e_SelectOfferPart"));//新数据
		
		var isChange = false;//用于判断数据是否做出了变更 默认未作出修改

		if("BOSG"==brand){
			if(backUpData.toString() == offerData.toString()){
				MessageBox.alert("提示信息", "您没有变更任何信息，无法提交！");
				return false;
			}
		}else{
		//判断集团服务优惠产品特征参数是否有改变
		var proParamCha = offerData.get("IS_CHA_VALUE_CHANGE");
		if(typeof(proParamCha) == "undefined"){
			MessageBox.alert("提示信息", "如果不需要修改订购信息,请先点开商品订购服务优惠的弹出按钮,如果配有商品特征,请点开商品特征后点击确定！");
			return false;
		}

		if(proParamCha!='true'){ //如果产品参数未做修改,则判断服务优惠是否做出了修改
		  var suboffers = offerData.get("SUBOFFERS");
		  if(suboffers){
		    for(var j=0;j<suboffers.length;j++){
		    var suboffer = suboffers.get(j);
		    var operCode = suboffer.get("OPER_CODE");
		       if(operCode!='3'){
		         isChange = true;
		         break;
		       }
		    }
		  }
		}else{
		  isChange = true;
		}
		
		//判断集团定制信息是否有改变
		if(!isChange){
		  var grpPackageInfo = offerData.get('GRP_PACKAGE_INFO');
		  if(grpPackageInfo){
		     for(var k=0;k<grpPackageInfo.length;k++){
		    	 var grppackage = grpPackageInfo.get(k);
		         if(grppackage.get('MODIFY_TAG')!='EXIST'){
		            isChange = true;
		            break;
		         }
		     }
		  }
		}
		
		if(!isChange)
		{
			MessageBox.alert("提示信息", "您没有变更任何信息，无法提交！");
			return false;
		}
		}
	}
	else if(operType == "CrtUs")
	{
		//校验付费类型
		var payPlanValue = $("#payPlanValue").val();
		if(payPlanValue == "")
		{
			MessageBox.alert("提示信息", "请选择付费类型！");
			return false;
		}
		
		//校验缴费方式:0-后付费；1-预付费
		var payType = $("#cond_PAY_TYPE").val();
		if(payType == "")
		{
			MessageBox.alert("提示信息", "请选择缴费方式！");
			return false;
		}

	    //校验缴费周期(后付费需要填写缴费周期)
		if(payType == "0")
		{
			var payCycle = $("#cond_PAY_CYCLE").val();
			if(payCycle == "")
			{
				MessageBox.alert("提示信息", "请选择缴费周期！");
				return false;
			}
			
			if(payCycle > 6)
			{
				//MessageBox.alert("提示信息", "缴费周期不能超过6个月！");
				//return false;
			}
		}

		//校验账户
		var acctDeal = $("#cond_ACCT_DEAL").val();
		if(acctDeal == 1)
		{
			var acctId = $("#ACCT_COMBINE_ID").html();
			if(!acctId)
			{
				MessageBox.alert("提示信息", "账户操作类型是合户，请选择一个付费账户！");
				return false;
			}
		}
		else
		{
			var acctId = $("#ACCT_COMBINE_ID").html();
			if(!acctId)
			{
				MessageBox.alert("提示信息", "账户操作类型是开户，请新增一个付费账户！");
				return false;
			}
		}

	}
	
	if(operType == "CrtUs"||operType == "ChgUs"){
		if($.trim($("#i_contractSelPart .value").html()) == ""){
			MessageBox.alert("提示信息", "订购该产品需要合同，请选择相应合同！");
			return false;
		}else{
			var isfound = false;
			var contractInfo = pageData.getSubscriber();
			var offerIds = contractInfo.get("OFFER_IDS").split(",");
			var offerCode = $("#cond_OFFER_CODE").val();
			for(var i=0;i<offerIds.length;i++){
				if(offerCode == offerIds[i]){
					isfound = true;
				}
			}
			if(!isfound){
				MessageBox.alert("提示信息", "所选择的产品在合同中没有找到，请确认是否选择正确的合同！");
				return false;
			}
			
			var contractDay = contractInfo.get("CONTRACT_END_DATE");
			var now = new Date();
			var year  = now.getFullYear();
			var month = now.getMonth() + 1;
			var day   = now.getDate();
			var hour  = now.getHours();
			var min   = now.getMinutes();
			var sec   = now.getSeconds();
			month = (parseInt(month) < 10) ? ("0" + month) : (month);
			day   = (parseInt(day)   < 10) ? ("0" + day )  : (day);
			hour  = (parseInt(hour)  < 10) ? ("0" + hour)  : (hour);
			min   = (parseInt(min)   < 10) ? ("0" + min)   : (min);
			sec   = (parseInt(sec)   < 10) ? ("0" + sec)   : (sec);
			var toDay = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
		  	if(toDay > contractDay) 
		  	{
		  		MessageBox.alert("提示信息","该集团产品合同已过期，请续约后再操作！");
		       return false;
		  	}
		}
	}
	//校验定制信息
	if(operType == "CrtUs"||operType == "ChgUs"){
		 if(!checkUserGrpPkgForceInfo()){
			 return false;
		 };
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
		/*var fileList = $("#MEB_VOUCHER_FILE_LIST").val();
		if(fileList==null||fileList==''){
			MessageBox.alert("提示信息", "请上传凭证信息！");
			return false;
		}*/
	}
	
	return true;
}

/**
 * 初始化默认费用
 */
function initDefaultFee(){
	var offerId=$("#cond_OFFER_ID").val();
	if("110000006200"==offerId||"110010005743"==offerId){
		return ;
	}
	ajaxSubmit("", "initDefaultFee", "&OFFER_ID="+offerId,null, function(data){
		var grpFeeList=data.get("GRP_FEE_LIST");
		var list = $.DatasetList(grpFeeList);
		var tradeTypeCode ="";
		if(list.length > 0)
		{
			if($.feeMgr)
			{
				$.feeMgr.clearFee();
			}
			
			for(var i = 0; i < list.length; i++)
			{
				var feeMap = $.DataMap();
				tradeTypeCode=list[i].get("map")["map"]["TRADE_TYPE_CODE"];
				var elementid=list[i].get("map")["map"]["ELEMENT_ID"];
				feeMap.put("TRADE_TYPE_CODE",tradeTypeCode );
				if(""!=elementid&&"null"!=elementid&&"undefined"!=elementid){
				feeMap.put("ELEMENT_ID",elementid);
				}
				feeMap.put("MODE", list[i].get("map")["map"]["FEE_MODE"]);
				feeMap.put("CODE", list[i].get("map")["map"]["FEE_TYPE_CODE"]);
				feeMap.put("FEE", list[i].get("map")["map"]["FEE"]);
				$.feeMgr.insertFee(feeMap);
			}
			
			// 设置POS参数
			var serialNumber = $('#cond_SERIAL_NUMBER').val();
			var eparchyCode = $('#EPARCHY_CODE').val();
			var userId = $('#USER_ID').val();
			
			$.feeMgr.setPosParam(tradeTypeCode, serialNumber, '898', null);
		}
	},function(error_code,error_info,derror){
		
	  showDetailErrorInfo(error_code,error_info,derror);
			
	},{async:false});
	return 1;
}

/**
 * 账户新增回调
 */
function accountPopupItemCallback(data) {
	var acctInfo = data.get("ACCT_INFO");
	pageData.setAccountInfo(acctInfo);
	
	// 改变选择框样式
	if (data) {
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_COMBINE_ID">'+acctInfo.get("ACCT_NAME")+'</span>');
	}
}
/**
 * 合同组件回调
 */
function contractPopupItemCallback(data) {
	var contractInfo = data.get("CONTRACT_INFO");

	// 改变选择框样式
	if (data) {
		$("#i_contractSelPart .label").text("合同名称");
		$("#i_contractSelPart .value").html('<span class="text">'+contractInfo.get("CONTRACT_NAME")+'</span>');
		$("#i_contractSelPart .more").remove();
	}
	var data = new Wade.DataMap();
	data.put("CONTRACT_ID", contractInfo.get("CONTRACT_ID"));
	
	//取合同信息的生失效时间和对应的产品用于提交判断
	data.put("OFFER_IDS", contractInfo.get("OFFER_IDS"));
	data.put("CONTRACT_END_DATE", contractInfo.get("CONTRACT_END_DATE"));
	data.put("CONTRACT_WRITE_DATE", contractInfo.get("CONTRACT_WRITE_DATE"));
	
	pageData.setSubscriber(data);

}
/**
 * 用户信息拼装
 */
function saveEcSubscriberInfo() {
	var data = pageData.getSubscriber();
	if (!data) {
		data = new Wade.DataMap();
	}
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var uid = mainOffer.get("USER_ID");
	
	if (uid) {
		data.put("USER_ID", uid);
	}
	data.put("SERIAL_NUMBER", $("#cond_SERIAL_NUMBER").val());
	data.put("USER_DIFF_CODE", $("#USER_DIFF_CODE").val());

	pageData.setSubscriber(data);
}
/**
 * 账户信息拼装
 */
function saveEcAcctInfo() {
	
	var acctDeal = $("#cond_ACCT_DEAL").val();
	
	if(acctDeal == 1)
	{//账户合户
		var acctId = $("#ACCT_COMBINE_ID").html();
		var acctInfo = new Wade.DataMap();
		acctInfo.put("ACCT_ID", acctId);
		acctInfo.put("OPER_CODE", ACTION_EXITS);
		
		pageData.setAccountInfo(acctInfo);
	}
	else
	{//账户新增
		var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
		var acctInfo = pageData.getAccountInfo();
		
		if (acctInfo && acctInfo.length > 0) {
			
		} else {
			var data = new $.DataMap();
			acctInfo = new $.DataMap();
			acctInfo.put("ACCT_NAME", createAcctName());
			acctInfo.put("ACCT_TYPE", "0");
			
			data.put("ACCT_INFO", acctInfo);
			accountPopupItemCallback(data);
		}
	}
	
}
/**
 * 商品拼装
 */
function saveOffers() 
{
	var list = new Wade.DatasetList();
	// 主体商品
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	
	list.add(mainOffer);
	
	// 附加商品
	$(".e_SelectAttachOfferPart").each(function(){
		var data = PageData.getData($(this));
		if (data) {
			list.add(data);
		}
	});
	pageData.setOffers(list);
}

/**
 * 公共信息
 */
function saveCommonInfo() 
{
	// 资源
	assembleResource();
	
	// 注销原因
	assembleRemoveReason();
	
	// 备注
	assembleRemark();
	
	// 缴费信息
	assemblePayInfo();
	
	//付费计划
	assemblePayplan();
	
	// ESOP信息
	assembleEsopInfo();
}

/**
 * esop信息
 */
function assembleEsopInfo()
{
	//判断是否来自esop
	var esopStr = $("#e_ESOP_INFO").text();
	if(esopStr && esopStr != "")
	{
		var commInfo = pageData.getCommonInfo();
		var esopInfo = new $.DataMap(esopStr);
		commInfo.put("ESOP_INFO", esopInfo);
		pageData.setCommonInfo(commInfo);
	}
	else
	{
		return;
	}
}

/**
 * 资源信息
 */
function assembleResource() {
	var commInfo = pageData.getCommonInfo();
	var resInfos = commInfo.get("RES_INFO");
	if (resInfos && resInfos.length > 0) {
		var flag = false;
		for(var i=0;i<resInfos.length;i++){
			var item = resInfos.get(i);
			if(item.get("RES_TYPE_CODE") == "L"){
				flag = true;
			}
		}
		if(flag == false){
			var resInfo = new $.DataMap();
			resInfo.put("RES_CODE", $("#cond_SERIAL_NUMBER").val());
			resInfo.put("RES_TYPE_CODE", "L");
			resInfos.add(resInfo);
		}
	}else{
		resInfos = new $.DatasetList();
		var resInfo = new $.DataMap();
		resInfo.put("RES_CODE", $("#cond_SERIAL_NUMBER").val());
		resInfo.put("RES_TYPE_CODE", "L");
		resInfos.add(resInfo);
	}
	commInfo.put("RES_INFO", resInfos);
	pageData.setCommonInfo(commInfo);
}

/**
 * 缴费信息
 */
function assemblePayInfo() {
	var commInfo = pageData.getCommonInfo();
	
	var payInfo = new $.DataMap();
	payInfo.put("PAY_TYPE", $("#cond_PAY_TYPE").val());
	if($("#cond_PAY_TYPE").val() == "1")
	{//1-预付费
		payInfo.put("PAY_CYCLE", "0");
	}
	else
	{
		payInfo.put("PAY_CYCLE", $("#cond_PAY_CYCLE").val());
	}
	payInfo.put("PRO_START_DATE", $("#cond_PRO_START_DATE").val());
	payInfo.put("PRO_END_DATE", $("#cond_PRO_END_DATE").val());
	
	commInfo.put("PAY_CYCLE_INFO", payInfo);
	pageData.setCommonInfo(commInfo);
}
/**
 * 邮寄信息
 */
function assemblePostInfo() {
	var commInfo = pageData.getCommonInfo();
	
	var postInfo = new $.DataMap();
	postInfo.put("POST_TAG", $("#post_POST_TAG").val());
	postInfo.put("POST_NAME", $("#post_POST_NAME").val());
	postInfo.put("POST_ADDRESS", $("#post_POST_ADDRESS").val());
	postInfo.put("POST_CODE", $("post_POST_CODE").val());
	postInfo.put("EMAIL", $("#post_EMAIL").val());
	postInfo.put("FAX_NBR", $("#post_FAX_NBR").val());

	var postContent = $("#post_POST_CONTENT").val();
	var postCyc = $("#post_POST_CYC").val();
	var postTypeset = $("#post_POST_TYPESET").val();
	
    if (!postContent || ""==postContent)
    {
    	postContent = "0";
    }
    if (!postCyc || ""==postCyc)
    {
    	postCyc = "0";
    } 
    if (!postTypeset || ""==postTypeset)
    {
    	postTypeset = "0";
    }
	postInfo.put("POST_CONTENT", postContent);
	postInfo.put("POST_CYC", postCyc);
	postInfo.put("POST_TYPESET", postTypeset);
	
	var askPostInfo = new $.DataMap();
	askPostInfo.put("RSRV_STR1", $("askPrint_RSRV_STR1").val());
	askPostInfo.put("RSRV_STR2", $("askPrint_RSRV_STR2").val());
	askPostInfo.put("RSRV_STR3", $("askPrint_RSRV_STR3").val());
	askPostInfo.put("RSRV_STR4", $("askPrint_RSRV_STR4").val());
	askPostInfo.put("RSRV_STR5", $("askPrint_RSRV_STR5").val());
	askPostInfo.put("RSRV_STR6", $("askPrint_RSRV_STR6").val());
	askPostInfo.put("OLD_ASKPRINT_INFO", $("askPrint_OLD_ASKPRINT_INFO").val());

	var postInfos = new $.DatasetList();
	postInfos.add(postInfo);
	commInfo.put("POST_INFO", postInfos);
	commInfo.put("ASK_INFO", askPostInfo);
	pageData.setCommonInfo(commInfo);
}

/**
 * 付费计划
 */
function assemblePayplan()
{
	var payPlanValue = $("#payPlanValue").val();
	
	var commInfo = pageData.getCommonInfo();
	commInfo.put("PAY_PLAN_INFO", payPlanValue);
	
	//从这里加一个effect_now字段
	var effectNow = $("#EFFECT_NOW").val();
	if(effectNow ==1){
		commInfo.put("EFFECT_NOW",true);
	}else{
		commInfo.put("EFFECT_NOW",false);
	}
	
	pageData.setCommonInfo(commInfo);
}

/**
 * 注销原因
 */
function assembleRemoveReason() 
{
	var commInfo = pageData.getCommonInfo();

	var removeReason = new $.DataMap();
	removeReason.put("REASON_CODE", $("#cond_REMOVE_REASON").val());
	removeReason.put("REASON_DESC", cond_REMOVE_REASON.selectedText);
	
	commInfo.put("REMOVE_REASON", removeReason);
	pageData.setCommonInfo(commInfo);
}

/**
 * 备注
 */
function assembleRemark() {
	var commInfo = pageData.getCommonInfo();
	commInfo.put("REMARK", $("#cond_REMARK").val());
	pageData.setCommonInfo(commInfo);
}

//初始化商品目录组件
function initOfferCategoryPopupItem()
{
	var groupInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if(typeof(groupInfo) == "undefined" || groupInfo == null || groupInfo == "")
	{
		MessageBox.alert("提示信息", "请先认证政企客户信息！");
		return false;
	}
	
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	
//	//判断是否有固定受理的销售品，有的话不让选择其他销售品
	var isHaveInitOffer = $("#IS_HAVE_INIT_OFFER").val();
	if("true" == isHaveInitOffer){
		return;
	}
	enterpriseCatalog.setOfferListDivSize();
	showPopup('popup', enterpriseCatalog.id, true);
}
//初始化商品目录组件
function initEcUserPackagePopupItem()
{
	var groupInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if(typeof(groupInfo) == "undefined" || groupInfo == null || groupInfo == "")
	{
		MessageBox.alert("提示信息", "请先认证政企客户信息！");
		return false;
	}
	
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	var offerId = $("#cond_OFFER_ID").val();
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var userId = mainOffer.get("USER_ID");
	if(!userId){//如果不存在，默认塞个空
		userId = "";
	}
	loadEcUserPackageTree(offerId,userId);
	showPopup('popup', 'userPckageTreePop', true);
}

//初始化BBOSS商品目录组件
function initProUserPackagePopupItem()
{
	var groupInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if(typeof(groupInfo) == "undefined" || groupInfo == null || groupInfo == "")
	{
		MessageBox.alert("提示信息", "请先认证政企客户信息！");
		return false;
	}
	
/*	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}*/
	var offerId = $("#proPackage").attr("proId");
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var userId = mainOffer.get("USER_ID");
	if(!userId){//如果不存在，默认塞个空
		userId = "";
	}
	loadBBossUserPackageTree(offerId,userId);
	showPopup('bbossUserPckageTreePop', 'PckageHome');
}



//初始化crm受理时页面展示元素
function initCrm()
{
	var esopStr = $("#e_ESOP_INFO").text();//此值不为空说明是ESOP跳转过来的
	if(esopStr != "")
	{
		$("#offerChoiceButton").attr("style","display:none;");
		
		var esopInfo = new $.DataMap(esopStr);
		var operType = esopInfo.get("OPER_TYPE");
		var offerId = esopInfo.get("OFFER_ID");
		
		if(operType == "CrtUs")
		{
			var contractStr = $("#e_ESOP_CONTRACT_INFO").text();
			if(contractStr && contractStr != "")
			{
				var contractInfo = new $.DataMap(contractStr);
				var commInfo = pageData.getCommonInfo();
				commInfo.put("CONTRACT_INFO", contractInfo);
				pageData.setCommonInfo(commInfo);
				$("#i_contractSelPart").attr("style","display:none;");
				$("#i_contractSelPart").removeClass("required");
			}
			
			showOpenInfo();
		}
		else if(operType == "ChgUs")
		{
			showChangeInfo();
		}else if(operType == "DstUs"){
			showLogoutInfo();
			//隐藏一键注销按钮
			$("#SHOW_DESTROY_ONE_KEY").css("display", "none");
		}

		var esopOffer = $("#ESOP_OFFER_DATA").text();
		if(esopOffer)
		{
			$("#class_"+offerId).text(esopOffer);
		}
		
		//是否有定制，控制区域是否显示  
		//一定要放到这句$("#class_"+offerId).text(esopOffer);后面，要不然默认定制信息被覆盖了
		showUserPackageInfo(operType);
		
		return ;
	}
	
}

//是否有定制，控制区域是否显示
function showUserPackageInfo(operType)
{
	//是否有定制，控制区域是否显示
	var useTag = $("#USE_TAG").val();
	if(useTag == "1" && operType != "DstUs")
	{
		$("#ecPackagePart").css("display","");
		var userPackages = $("#USER_PACKAGES").text();
		var offerData = PageData.getData($(".e_SelectOfferPart"));
		offerData.put("GRP_PACKAGE_INFO",$.DatasetList(userPackages));
		PageData.setData($(".e_SelectOfferPart"),offerData);
	}
	else
	{
		$("#ecPackagePart").css("display","none");
	}

	//判断服务号码是否可编辑
	var ifResCode = $("#cond_IF_RES_CODE").val();
	if(ifResCode == "1")
	{
		$("#cond_SERIAL_NUMBER_INPUT").attr("disabled", false);
	}
	else
	{
		$("#cond_SERIAL_NUMBER_INPUT").attr("disabled", true);
	}
}

//从商品搜索选择商品
function chooseOfferFromSearch(offerId, parentOfferId, offerName)
{
	chooseOffer(offerId, offerName, "", "CrtUs");
}

function chooseOfferInfo(el, operType) {
	var offerId = $(el).attr('OFFER_ID');
	var offerCode = $(el).attr('OFFER_CODE');
	var offerType = $(el).attr('OFFER_TYPE');
	var offerName = $(el).attr('OFFER_NAME');
	var userId = $(el).attr('USER_ID');
	chooseOffer(offerId, offerCode, offerType, offerName, userId, operType);
	
}
//销售品列表选择销售品(入参：销售品编码，销售品名称，主销售品实例id，操作类型)
function chooseOffer(offerId, offerCode, offerType, offerName, userId, operType)
{	
	//设置销售品隐藏域
	$("#cond_OFFER_CODE").val(offerCode);
	$("#cond_OFFER_ID").val(offerId);
	$("#cond_OPER_TYPE").val(operType);
	
	//清空销售品设置组件的缓存数据
	$("#prodDiv_OFFER_ID").val("");
	$("#prodDiv_TOP_OFFER_ID").val("");
	$("#prodDiv_OFFER_DATA").val("");
	$("#prodDiv_OFFER_INDEX").val("");
	
	$("#class_OfferDataBackup").val("");
	$("#class_SELECT_GROUP_OFFER").val("");
	//bboss重新选择商品时，清空缓存数据
	$("#OPERTYPE").val("");
	$("#FORCE_PACKAGE_LIST").val("");
    //联系人缓存数据初始化，避免数据重复记录
    var tempList = new Wade.DatasetList();    
    CONTACTOR_INFOS = tempList;	
	
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var groupId = groupInfo.get("GROUP_ID");
	var custId = custInfo.get("CUST_ID");
	var classId = groupInfo.get("CLASS_ID");
	$("#cond_CUST_ID").val(custId);
	
	if(operType == "CrtUs")
	{//集团销售品开户
		//加载必选销售品信息
		initRequriedOffer(offerId, offerCode, groupId,custId,classId);
	}
	else if(operType == "ChgUs" || operType == "DstUs")
	{//集团销售品变更、注销
		
		initOpenedOffer(userId, offerId, custId);
	}
	
}

//加载必选销售品信息
function initRequriedOffer(offerId, offerCode, groupId,custId,classId)
{
	$.beginPageLoading("数据加载中......");//,DLchildOfferPart,SelectAttachOfferPart,accessNumInfo,AcctDealPart

	$.ajax.submit("", "queryOfferInfo", "&OFFER_ID="+offerId+"&GROUP_ID="+groupId+"&CUST_ID="+custId+"&ClASS_ID="+classId, "mainOfferPart,OtherInfo,ecPackagePart,postInfoMgrPart", function(data){

		$.endPageLoading();

		var payCycle = data.get("PAY_CYCLE");
		var payCycleLi = $("#cond_PAY_CYCLE").closest("li");
		if(payCycle == "0")
		{
			payCycleLi.css("display", "");
		}
		else
		{
			payCycleLi.css("display", "none");
		}
		
		var offer = data.get("OFFER_DATA");

		var offerData = new Wade.DataMap();
		offerData.put("OFFER_ID", offerId);
		offerData.put("OFFER_CODE", offerCode);
		offerData.put("OFFER_NAME", offer.get("OFFER_NAME"));
		offerData.put("BRAND_CODE", offer.get("BRAND_CODE"));
		offerData.put("OPER_CODE", ACTION_CREATE);
		PageData.setData($("#class_"+offerId), offerData);
		
		$("#cond_EC_BRAND").val(offer.get("BRAND_CODE"));
		
		//是否有定制，控制区域是否显示
		var useTag = data.get("USE_TAG");
		if(useTag == "1")
		{
			$("#ecPackagePart").css("display","");
			var userPackages = data.get("USER_PACKAGES");
			
			var offerData = PageData.getData($(".e_SelectOfferPart"));
			offerData.put("GRP_PACKAGE_INFO",userPackages);
			PageData.setData($(".e_SelectOfferPart"),offerData);
		}
		else
		{
			$("#ecPackagePart").css("display","none");
		}
		
		// 显示集团受理需要展示的页面元素
		showOpenInfo();
		
		//手动刷新scroller组件
		editMainScroll.refresh();
		
		//关闭集团目录popupItem
		enterpriseCatalog.closeEcCataPopupItem();
		
		//所有产品合同都是必填,无需进行判断,尤其这逻辑不符合海南环境
		//initRequriedContract(offerCode);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		if(error_info.indexOf("CRM_ORDER_125") != -1 || error_code == "CRM_ORDER_125"){
			var message = "系统不支持该产品进行集团新增操作";
			MessageBox.alert("提示信息", message);
		}else{
			showDetailErrorInfo(error_code,error_info,derror);
		}
    });
}

//加载已订购的销售品信息
function initOpenedOffer(userId, offerId,custId)
{
	var operType = $("#cond_OPER_TYPE").val();
	$.beginPageLoading("数据加载中......");//mainOfferPart,DLchildOfferPart,SelectAttachOfferPart,accessNumInfo

	$.ajax.submit("", "queryInsOfferInfo", "USER_ID="+userId+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&CUST_ID="+custId, "mainOfferPart,OtherInfo,ecPackagePart,DLchildOfferPart", function(data){
		$.endPageLoading();
		//将数据对象设置到页面上
		var dataset = data.get("OFFER_DATAS");
		$("#DLchildOfferPart").css("display","none");//DLBG默认是不显示
		for(var i = 0, size = dataset.length; i < size; i++)
		{
			var offer = dataset.get(i);
			var offerId = offer.get("OFFER_ID");
			var offerData = offer.get("OFFER_DATA");
			PageData.setData($("#class_"+offerId), offerData);
			
			$("#cond_EC_BRAND").val(offerData.get("BRAND_CODE"));
			
			if(offerData.get("BRAND_CODE") == "BOSG")
			{
				// 变更的时候不需要初始化删除按钮，disabled掉
				//$("#optOffer_"+offerId+" [class='fn']").attr("class", "fn e_dis");
			}else if(offerData.get("BRAND_CODE") == "DLBG")
			{
				$("#DLchildOfferPart").css("display","");
			}
			//缓存一份原始数据对象
			PageData.setData($("#class_OfferDataBackup"), offerData);
		}
		
		var selGroupOfferData = data.get("SELECT_GROUP_OFFER");
		PageData.setData($("#class_SELECT_GROUP_OFFER"), selGroupOfferData);
		
		$("#cond_SERIAL_NUMBER_INPUT").attr("disabled", true);
		$("#checkPic").addClass("e_dis");
		$("#checkPic").removeAttr("ontap");

		//是否有定制，控制区域是否显示
		var useTag = data.get("USE_TAG");
		if(useTag == "1" && operType != "DstUs")
		{
			$("#ecPackagePart").css("display","");
			var userPackages = data.get("USER_PACKAGES");
			
			var offerData = PageData.getData($(".e_SelectOfferPart"));
			offerData.put("GRP_PACKAGE_INFO",userPackages);
			PageData.setData($(".e_SelectOfferPart"),offerData);
		}
		else
		{
			$("#ecPackagePart").css("display","none");
		}
		$("#mainOfferPart").css("display","");
		
		if(operType == "ChgUs")
		{
			showChangeInfo();
		}
		else if(operType == "DstUs")
		{
			showLogoutInfo();
			
			//一键注销按钮展示控制
			var showDesOneKey = data.get("SHOW_DESTROY_ONE_KEY");
//			if(showDesOneKey == "yes")
//			{
//				$("#SHOW_DESTROY_ONE_KEY").css("display", "");
//			}
//			else
//			{
//				$("#SHOW_DESTROY_ONE_KEY").css("display", "none");
//			}
			$("#SHOW_DESTROY_ONE_KEY").css("display", "none");
		}
		//手动刷新scroller组件
		editMainScroll.refresh();
		
		//关闭集团目录popupItem(M端需要)
		enterpriseCatalog.closeEcCataPopupItem();
		
		//esop初始化数据
		var esopOfferStr = $("#ESOP_OFFER_DATA").text();
		if(esopOfferStr != "")
		{
			var esopOffer = new $.DataMap(esopOfferStr);
			var esopOfferId = esopOffer.get("OFFER_ID");
			$("#class_"+esopOfferId).text(esopOfferStr);
		}
	},
	function(error_code,error_info,derror){
		debugger;
		$.endPageLoading();
		if(error_info.indexOf("CRM_ORDER_125") != -1 || error_code == "CRM_ORDER_125"){
			var message = "";
			if(operType == "ChgUs"){
				message = "系统不支持该产品进行集团变更操作";
			}else if(operType == "DstUs"){
				message = "系统不支持该产品进行集团注销操作";
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
		if(typeof(offers) == "undefined" || typeof(offers) != "object" || offers.length == 0){
			//动力100子销售品未选择实例前无数据结构
			return;
		}
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
		
		if($("#optOffer_"+offerId).children().length == 0)
		{
			$("#sub_"+offerId).remove();
		}
		
		if(offers.length == 0)
		{
			offerData.remove(offers);
		}
		PageData.setData($("#class_"+offerId), offerData);

	});
}


function deleteMoreSubOffers(flag, optOfferId, offerId)
{
	MessageBox.confirm("提示信息", "是否删除当前子商品？", function(btn){
		if(btn == "cancel")
		{
			return;
		}
		
		//将子销售品的选择标记取消
		$("#"+optOfferId).attr("checked", false);
		$("#"+optOfferId).attr("disabled", false);
		
		//判断子销售品是否有实例，有则隐藏，没有则直接删除
		var hasInst = false;
		var offerInsId = $("#oo_"+optOfferId+flag+"_OPTOFFER_ID").attr("OFFER_INS_ID");
		if(offerInsId == ""|| offerInsId =="null" )
		{//没有实例
			$("#div_"+optOfferId+flag).remove();
		}
		else
		{
			$("#div_"+optOfferId+flag).css("display", "none");
			hasInst = true;
		}
		//删除数据结构
		var offerData = GrpOperPageData.getData($("#class_"+offerId));
		var offers = offerData.get("SUBOFFERS");
		if(typeof(offers) == "undefined" || typeof(offers) != "object" || offers.length == 0){
			//动力100子销售品未选择实例前无数据结构
			return;
		}
		for(var i = offers.length; i > 0; i--)
		{
			var offer = offers.get(i-1);
			if(offer.get("OFFER_ID") == optOfferId)
			{
				if(hasInst)
				{
					offer.put("OPER_CODE", ACTION_DELETE);
				}
				else
				{
					offers.removeAt(i-1);
				}
			}
		}
		if(offers.length == 0)
		{
			offerData.remove(offers);
		}
		GrpOperPageData.setData($("#class_"+offerId), offerData);

	});
}

//服务号码校验
function checkAccesssNum()
{
	var resTypeCode = $("#cond_RES_TYPE_CODE").val();
	var accessNumInput = $("#cond_SERIAL_NUMBER_INPUT").val();
	var accessNum = $("#cond_SERIAL_NUMBER").val();
	var isSuc = $("#cond_SERIAL_NUMBER_SUCCESS").val();
	var CHECK_SUCCESS = "0";
	var CHECK_FAIL = "1";
	
	if(isSuc=="true" && accessNumInput == accessNum)
	{
		MessageBox.alert("提示信息", "服务号码已校验成功,请勿重复校验！");
		return false;
	}
	var mainOfferCode = $("#cond_OFFER_CODE").val();
	
	$.beginPageLoading("服务校验中，请稍后......");
	$.ajax.submit("", "checkSerialNumber", "PRODUCT_ID="+mainOfferCode+"&SERIAL_NUMBER="+accessNumInput+"&RES_TYPE_CODE="+resTypeCode, "", function(data){
		$.endPageLoading();
		var retCode = data.get("X_RESULTCODE", "-1")//返回编码
		var retDesc = data.get("X_RESULTINFO", "");//返回结果描述
		
		if (CHECK_SUCCESS == retCode) {//校验成功
			var commInfo = pageData.getCommonInfo();
			var resInfo = commInfo.get("RES_INFO");
			if (isNull(resInfo)) {
				resInfo = $.DataMap();
			}
			var resInfos = $.DatasetList();
			resInfo.put("RES_CODE",accessNumInput);
			resInfo.put("RES_TYPE_CODE",resTypeCode);
			resInfo.put("MODIFY_TAG","0");
			resInfos.add(resInfo);
			commInfo.put("RES_INFO",resInfos);
			pageData.setCommonInfo(commInfo);
			
			$("#cond_SERIAL_NUMBER").val(accessNumInput);
			$("#cond_SERIAL_NUMBER_SUCCESS").val(true);
			MessageBox.success("成功信息", retDesc);
		} else if(CHECK_FAIL == retCode) {//校验失败
			
			$("#cond_SERIAL_NUMBER").val("");
			$("#cond_SERIAL_NUMBER_SUCCESS").val(false);
			MessageBox.error("错误信息", retDesc);
		} else {
			MessageBox.error("错误信息", "号码校验发生系统异常，请联系管理员！");
		}
		
	},
	function(error_code,error_info,derror){
		$("#cond_SERIAL_NUMBER_SUCCESS").val(false);  //修改服务号码校验标记
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//集团新增提交时校验服务号码是否验证成功
function isAccessNumSuccess()
{
	var operType = $("#cond_OPER_TYPE").val();
	var isSuc = $("#cond_SERIAL_NUMBER_SUCCESS").val();
	var accessNumInput = $("#cond_SERIAL_NUMBER_INPUT").val();
	var accessNum = $("#cond_SERIAL_NUMBER").val();
	if(operType == "CrtUs")
	{
		if(isSuc == "true" && accessNumInput == accessNum)
		{
			return true;
		}
		return false;
	}
	return true;
}

//显示注销信息
function showLogoutInfo()
{
	$("#OpenSubmit").css("display","none");
	$("#ChgSubmit").css("display","none");
	$("#DelSubmit").css("display","");
	
	$("#accessNumUL").css("display", "");
	$("#acctContractUL").css("display", "none");
	$("#removeUL").css("display", "");
	
	$("#mainOfferPart").css("display","");
	
	$("#remarkInfo").addClass("required");
	$("#removeInfo").addClass("required");
	$("#accessNumPart").removeClass("required");
	$("#postDiv").css("display", "none");

}

//显示变更信息
function showChangeInfo()
{
	$("#OpenSubmit").css("display","none");
	$("#ChgSubmit").css("display","");
	$("#DelSubmit").css("display","none");
	
	$("#accessNumUL").css("display", "");
	
	$("#acctContractUL").css("display", "");
	$("#i_contractSelPart").css("display", "");
	$("#i_contractSelPart").addClass("required");

	$("#i_acctDealPart").css("display", "none");
	$("#i_acctSelPart").css("display", "none");
	$("#i_payPlanPart").css("display", "none");
	
	$("#removeUL").css("display", "none");
	
	$("#mainOfferPart").css("display","");
	
	$("#removeInfo").removeClass("required");
	$("#accessNumPart").removeClass("required");
}

//显示开户信息
function showOpenInfo()
{
	$("#OpenSubmit").css("display","");
	$("#ChgSubmit").css("display","none");
	$("#DelSubmit").css("display","none");
	
	$("#accessNumUL").css("display", "");
	$("#acctContractUL").css("display", "");
	$("#i_contractSelPart").addClass("required");
	
	$("#cond_ACCT_DEAL").css("display","");
	$("#cond_ACCT_DEAL_span").css("display","");
	$("#removeUL").css("display", "none");
	
	$("#mainOfferPart").css("display","");
	
	$("#removeInfo").removeClass("required");
	$("#accessNumPart").addClass("required");
	
	$("#DLchildOfferPart").css("display","none");
	$("#SelectAttachOfferPart").css("display","none");

	var acctDeal = $("#cond_ACCT_DEAL").val();
	if(acctDeal == 1)
	{
		$("#i_acctSelPart").css("display","none");
		$("#i_acctCombPart").css("display","");
	}
	else
	{
		$("#i_acctSelPart").css("display","");
		$("#i_acctCombPart").css("display","none");
	}
	//vpn特殊处理 add by zhouchao begin
	var offerId = $("#cond_OFFER_ID").val();
	if("110000008018" == offerId){
		$("#centrexVpnPart").css("display","");
	}else{
		$("#centrexVpnPart").css("display","none");
	}
	//vpn特殊处理 add by zhouchao end
}

//获取第二天日期
function getNextDate()
{
	var currentDate = new Date();
	currentDate = currentDate.setDate(currentDate.getDate() + 1);
	currentDate = new Date(currentDate);
	var fullYear = currentDate.getFullYear();
	var month = currentDate.getMonth() + 1;
	var day = currentDate.getDate();
	
	return fullYear+"-"+month+"-"+day;
}

//加载合同组件
function initContractPopupItem()
{
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	queryContractInfo();
}

//加载付费模式组件
function initPayPlanItem()
{
	queryPayPlanInfo();

	//修改确定按钮样式
	$("#payPlanSubmitBtn").removeClass("e_button-green");
	$("#payPlanSubmitBtn").addClass("e_button-blue");
}

//加载更多信息区域
function initMoreInfoPopupItem()
{
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if(typeof(custInfo) == "undefined" || custInfo == null || custInfo == "")
	{
		return false;
	}
	var regionId = custInfo.get("REGION_ID");
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryAreaInfo", "AREA_CODE="+regionId, "AreaRefreshPart", function(data){
		$.endPageLoading();
		showPopup('popup', 'moreInfoPopupItem');
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//查询发展人
function initDeprtId()
{
	var area_code = $("#cond_AREA_CODE").val();

	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryOrganizeInfo", "AREA_CODE="+area_code, "OrgRefreshPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function changeCHNL(el)
{
	var chnlName = $("#cond_DELEGATER_text").text();
	var delegaterId = $(el).val();
	var arr = delegaterId.split("|");
	$("#cond_CHNL_ID").val(arr[0]);
	$("#cond_CHNL_CODE").val(arr[1]);
	
}

function clearTips()
{
	var accessNumInput = $("#cond_SERIAL_NUMBER_INPUT").val();
	var tips = $("#cond_TIPS").val();
	if(accessNumInput == tips)
	{
		$("#cond_SERIAL_NUMBER_INPUT").val("");
	}
}

function showTips()
{
	var accessNumInput = $("#cond_SERIAL_NUMBER_INPUT").val();
	if(accessNumInput == "")
	{
		var tips = $("#cond_TIPS").val();
		$("#cond_SERIAL_NUMBER_INPUT").val(tips);
	}
}



/************BBOSS产品开始***********************/

// 上面的日历js可删除，可用下面的，弹出日期可用 popupDateSelect 方法
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
//$(function(){
//	$("#calendar").each(function(){
//		var calendarId = $(this).attr("id"),
//		calendar = eval(calendarId),
//		item = $(this).closest(".c_popupItem"),
//		back = item.find(".back")[0];
//		$("#"+calendarId).select(function(e){
//			var fmt = activeDateField.attr("format");
//			activeDateField.val(Date.parseFrom(this.val()).format(fmt));
//			var $Id = e.target.id;
//			if($Id != 'calendar_btn_clear' && $Id != 'calendar_btn_nmfday' && $Id != 'calendar_btn_today') {
//				backPopup(back);
//			}	
//		});
//		$("#"+calendarId).clear(function(e){
//			activeDateField.val("");
//		});
//	});
//});
//(function(c){
//	var o = c.prototype;
//	o.format = function(fmt) {
//	    var o = {
//            "M+": this.getMonth() + 1,
//            "d+": this.getDate(),
//            "h+": this.getHours(),
//            "m+": this.getMinutes(),
//            "s+": this.getSeconds(),
//            "q+": Math.floor((this.getMonth() + 3) / 3),
//            "S": this.getMilliseconds()
//        };
//        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, 
//        		(this.getFullYear() + "").substr(4 - RegExp.$1.length));
//        for (var k in o)
//        if (new RegExp("(" + k + ")").test(fmt)) 
//        	fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) 
//        			: (("00" + o[k]).substr(("" + o[k]).length)));
//        return fmt;
//    };
//    
//    c.parseFrom = function(dateStr) {
//    	var date = eval('new Date('+dateStr.replace(/\d+(?=-[^-]+$)/,
//	    function(a){return parseInt(a,10)-1;}).match(/\d+/g)+')');
//	    return date;
//    };
//})(Date);

function showAcctAddPopup(el) {
	
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	
	var offerCode=$("#cond_OFFER_CODE").val();
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId= custInfo.get("CUST_ID");
	if("9898"==offerCode||"7345"==offerCode || "7349"==offerCode){
		openNav("集团账户管理", "igroup.creategroupacct", "queryAcctInfoList", "&PRODUCT_ID="+offerCode+"&CUST_ID="+custId, "/order/iorder");
		$("#cond_ACCT_DEAL").val("1");
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}else{
		showPopup('popup', 'accountPopupItem', true);
		accountPopupItem.showAddPopup();
		var data = {};
		data["ACCT_NAME"] = createAcctName();
		accountPopupItem.fillAcctPopup(data);
	}
	
	
}

//生成账户名称：集团名称_商品名称
function createAcctName()
{
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var acctName = groupInfo.get("CUST_NAME");
	if(!acctName){
		acctName = groupInfo.get("GROUP_NAME");
	}
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}
	
//	var offerId = $("#cond_OFFER_ID").val();
//	var offerData = PageData.getData($("#class_"+offerId));
//	if(offerData)
//	{
//		var offerName = offerData.get("OFFER_NAME");
//		acctName = acctName + "_" + offerName;
//		if(acctName.length > 100)
//		{
//			acctName = acctName.substring(0, 99);
//		}
//	}

	return acctName;
}

//合户操作：显示账户列表
function showAcctCombPopup(el)
{
	
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryEcAccountList", "CUST_ID="+custId, "ecAccountListPart", function(data){
		$.endPageLoading();
		
		var acctId = $("#ACCT_COMBINE_ID").html();
		if(acctId)
		{
			$("#"+acctId).addClass("checked");
		}
		
		showPopup('popup', 'chooseEcAccount', true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	

}

function queryGroupInfos(obj)
{
	var groupValue = $("#cond_GROUP_VALUE").val();
	var qryType = $("#acctCustQueryType").val();
	
	if(groupValue == "")
	{
		$.MessageBox.alert("提示", "请填写客户查询条件！");	
		return false;
	}
	var param = "&QUERY_TYPE="+qryType+"&VALUE="+groupValue;
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryEcAccountList", param, "ecAccountListPart", function(data){
		$.endPageLoading();
		
		var acctId = $("#ACCT_COMBINE_ID").html();
		if(acctId)
		{
			$("#"+acctId).addClass("checked");
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//合户操作：选择付费账户
function selectAccount1(el)
{
	$("#ecAccountUL li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	
	var acctId = $(el).attr("id");
	var acctName = $(el).children().find("div[class=title]").html();
	
	var html = "<span class='text'>"+acctName+"</span>";
	html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
	$("#i_acctCombPart .value").html(html);
	
	hidePopup('popup');
}

//合户操作：选择付费账户
function selectAccount(el)
{
	$("#ecAccountUL li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	
	var acctId = $(el).attr("id");
	var acctName = $(el).children().find("div[class=title]").html();
	
	var html = "<span class='text'>"+acctName+"</span>";
	html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
	$("#i_acctCombPart .value").html(html);
	
	hidePopup('popup');
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

function payCycleInfoChange(el, event) {
	var li = $("#"+el.id).closest("li");
	var nextLi = li.next("li");

	var value = el.value;
	if (value == "1") {
		nextLi.css("display", "none");
	} else if (value == "0") {
		nextLi.css("display", "");
	}
}

function acctDealChange(el)
{
	var acctDeal = el.value;
	if(acctDeal == "1")
	{
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}
	else
	{
		$("#i_acctCombPart").css("display", "none");
		$("#i_acctSelPart").css("display", "");
	}
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
function changePlanMode() {
	var planModeCode = $("#acct_PLAN_MODE_CODE");
	var message = "";
	if(planModeCode.val() == "1"){
		message = "【完全统付】付费模式是集团单位为单位成员缴纳所有通信费用的付费模式。单位成员共用一个账户，账户内金额由所有成员共同使用。请业务受理人员在办理【完全统付】付费模式前与客户解释和确认关于账户金额的使用规则，避免业务风险。";
	}else if(planModeCode.val() == "2"){
		message = "【限定金额】是集团单位只为单位成员缴纳固定金额通信费用的付费模式。";
	}else if(planModeCode.val() == "3"){
		message = "【限定账目项】是集团单位只为单位成员缴纳指定账目项通信费用的付费模式。";
	}else if(planModeCode.val() == "4"){
		message = "【既限金额，又限账目项】是集团单位只为单位成员缴纳指定账目项中固定金额通信费用的付费模式。";
	}
	if(message){
		MessageBox.confirm("请注意：",message,
			function(btn){
				if(btn!="ok"){
					$("#acct_PLAN_MODE_CODE").val('');
					$.endPageLoading();
				}
			},
			function(error_code,error_info){
				$.endPageLoading();
		    });
	}
}
 
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


function onlyNumber(e){
	
	var id = $(e).attr("id");
	if (id == 'pam_TIME_HOUR') {
		var hour = $("#pam_TIME_HOUR").val();
		if (isNaN(hour)) {
			$.validate.alerter.one($("#pam_TIME_HOUR")[0], "小时输入必须为数字，范围为[00~23],请重新输入。");
			$("#pam_TIME_HOUR").val("");
			return false;
		}
		if (hour.length < 2 && !isNull(hour)) {
			$.validate.alerter.one($("#pam_TIME_HOUR")[0], "小时输入必须为两位数字，范围为[00~23],请重新输入。");
			$("#pam_TIME_HOUR").val("");
			return false;
		}
		if (hour >= 24) {
			$.validate.alerter.one($("#pam_TIME_HOUR")[0], "小时输入范围为[00~23],请重新输入。");
			$("#pam_TIME_HOUR").val("");
			return false;
		}
	}
	
	if (id == 'pam_TIME_MIN') {
		var min = $("#pam_TIME_MIN").val();
		if (isNaN(min)) { 
			$.validate.alerter.one($("#pam_TIME_MIN")[0], "分钟输入必须为数字，范围为[00~59],请重新输入。");
			$("#pam_TIME_MIN").val("");
			return false;
		}
		if (min.length < 2 && !isNull(min)) {
			$.validate.alerter.one($("#pam_TIME_MIN")[0], "分钟输入必须为两位数字，范围为[00~59],请重新输入。");
			$("#pam_TIME_MIN").val("");
			return false;
		}
		if (min >= 60) {
			$.validate.alerter.one($("#pam_TIME_MIN")[0], "分钟输入范围为[00~59],请重新输入。");
			$("#pam_TIME_MIN").val("");
			return false;
		}
	}
	if (id == 'pam_TIME_SEC') {
		var sec = $("#pam_TIME_SEC").val();
		if (isNaN(sec)) {
			$.validate.alerter.one($("#pam_TIME_SEC")[0], "秒输入必须为数字，范围为[00~59],请重新输入。");
			$("#pam_TIME_SEC").val("");
			return false;
		}
		if (sec.length < 2 && !isNull(sec)) {
			$.validate.alerter.one($("#pam_TIME_SEC")[0], "秒输入必须为两位数字，范围为[00~59],请重新输入。");
			$("#pam_TIME_SEC").val("");
			return false;
		}
		if (sec >= 60) {
			$.validate.alerter.one($("#pam_TIME_SEC")[0], "秒输入范围为[00~59],请重新输入。");
			$("#pam_TIME_SEC").val("");
			return false;
		}
	}
}

//初始化合同的必填
function initRequriedContract(offerCode)
{
	$.ajax.submit("", "queryNeddContract", "OFFER_CODE="+offerCode, null, function(data){
		if(data.get("NeedContract") == true || data.get("NeedContract") == 'true'){
			if(!$("#i_contractSelPart").hasClass("required")){
				$("#i_contractSelPart").addClass("required");
			}
		}else{
			if($("#i_contractSelPart").hasClass("required")){
				$("#i_contractSelPart").removeClass("required");
			}
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

/**
 * 字符串是否是HHmmss格式
 * @param dateString
 * @returns
 */
function isHHmmss(dateString){
	if(dateString.trim()=="")
		return true;
	var regEx = /^[0-2][0-4]([0-5]\d){2}$/;
	var flag = regEx.test(dateString); 
	return flag;
}

function checkPayCyclePriv(el)
{
	var payCycle=el.value;
	var privflag;
	$.httphandler.post("com.asiainfo.veris.crm.order.web.enterprise.cs.operenterprisesubscriber.CheckPayCyclePrivHandler", "CheckPayCyclePriv", "PAY_CYCLE="+payCycle+"&CHOOSE_OFFER_ID="+$("#cond_OFFER_ID").val(), 
	function(data){       
		if(data && data.length > 0){
			privflag = data.get("privflag");
			if(payCycle>6){
				if(privflag=="0"){
					MessageBox.alert("提示信息", "无权限员工,缴费周期不能选择超过6个月！");	
					$("#OpenSubmit").css("display","none");
				    return false;
				}else{
					$("#OpenSubmit").css("display","");
				}
			}else{
				$("#OpenSubmit").css("display","");
			}
		}
	}, 
	function(error_code, error_info){
		MessageBox.error(error_code, error_info);
	},{
		async: false
	});
}


//判断是否必选包已经选上
function checkUserGrpPkgForceInfo(){
	var forcePackageStr = $("#FORCE_PACKAGE_LIST").val();
	var forcePkgList= $.DatasetList(forcePackageStr);
	
	var grpPackageList = PageData.getData($(".e_SelectOfferPart")).get("GRP_PACKAGE_INFO");
	var result =true;
	//判断新增的元素
	forcePkgList.each(function(item, index, totalcount){
	
		var existForcePkg = false;
		
		grpPackageList.each(function(obj){
			    var packageId="";
				 packageId = obj.get("PACKAGE_ID");
				 if(packageId=="" || typeof(packageId) == "undefined"){//如果取不到，可能定制信息存在多条数据且第一条不是资费是服务 update by zhuwj
					 packageId = obj.get(1).get("PACKAGE_ID");
				 }
			
			if( packageId==item.get("PACKAGE_ID")){
				existForcePkg=true;
				return false;
			}
		});
		if(!existForcePkg){
			MessageBox.alert("操作提示","集团定制信息中包"+item.get("PACKAGE_NAME")+"是必选包，必须选择包下的至少一个元素");
			result = false;
			return false;
		}
		
	});
	
	return result;
	
}

function checkVPMNEnterPrise(){
	var operType = $("#cond_OPER_TYPE").val();
	if("CrtUs" ==operType){
		var offerData = PageData.getData($(".e_SelectOfferPart"));
		 var grpPackageInfo = offerData.get('GRP_PACKAGE_INFO');
		  if(grpPackageInfo){
		     for(var k=0;k<grpPackageInfo.length;k++){
		    	 var grppackage = grpPackageInfo.get(k);
		         var elementId=grppackage.get('ELEMENT_ID');
		         var elementType=grppackage.get('ELEMENT_TYPE');
		         if(elementType=="D" && (elementId =='99720501' ||elementId =='99720502' ||elementId =='99720503' ||elementId =='99720504' ||elementId =='99720505')){
		        	 	MessageBox.alert("操作提示",'本集团不能订购跨省V网优惠['+elementId+']！如需订购，请对TD_S_COMMPARA表配置！');
						return false;	
					}  
		     }
		  }
	}
	return true;
}

/**
 * @author chenhh6
 */
function CheckDepartCode(){
	var departCode = $("#pam_DEPART_ID").val();
	$.beginPageLoading("服务校验中，请稍后......");
	$.ajax.submit("", "checkDepartCode", "DEPART_ID="+departCode, "", function(data){
		$.endPageLoading();
		var retCode = data.get("X_RESULTCODE", "")//返回编码
		var retDesc = data.get("X_RESULTINFO", "");//返回结果描述
		if ('0' == retCode) {//校验成功
			MessageBox.success("代理商校验成功", retDesc);
		} else if('-1' == retCode) {//校验失败
			MessageBox.error("错误信息", retDesc);
			$("#pam_DEPART_ID").val("");
		} else {
			MessageBox.error("错误信息", "号码校验发生系统异常，请联系管理员！");
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
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
	/*pageData.setAuditInfo(auditIdInfo);*/
	
}



function selectProductAuditPopupItem(obj,auditNo,auditName){
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryAuditInfo", "&STAFF_ID="+auditNo+"&STAFF_NAME="+auditName, "productAuditPopupItem", function(data){

		$.endPageLoading();
	
	/*	// 显示集团受理需要展示的页面元素
		showOpenInfo();*/
		
		//手动刷新scroller组件
		editMainScroll.refresh();
		forwardPopup(obj, 'productAuditPopupItem');
		/*
		//关闭集团目录popupItem
		enterpriseCatalog.closeEcCataPopupItem();*/
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		
    });
	
	
	
	
}
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
	/*var  auditId= $(el).children().find("div[class=title]").text();*/
	
	var html = "<span class='text' >"+auditId+"</span>";
	html += "<span id='AUDIT_STAFF_ID' style='display:none'>"+auditId+"</span>";
	$("#i_auditSelPart .value").html(html);
	
	hidePopup('popup');
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