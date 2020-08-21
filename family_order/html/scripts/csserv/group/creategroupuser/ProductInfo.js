function initProductInfo() {
	var effectNow = $('#EFFECT_NOW').attr('checked');
	if(effectNow == true){
		selectedElements.isEffectNow = effectNow
	}
	// 初始化资源信息
	initGroupSerialNumber();
}

function changePayFeeMode() {

}


/**
 * 作用:套餐折扣率校验
 */
function checkShuangKuaParam(itemIndex, attrcodein) {

	if ($.validate.verifyAll('elementPanel')) {
		var tempElement = selectedElements.selectedEls.get(itemIndex);
		var attrs = tempElement.get("ATTR_PARAM");
		var length = attrs.length;
		var isUpdate = false;
		var newAttrValue;
		for ( var i = 0; i < length; i++) {
			var attr = attrs.get(i);
			var attrCode = attr.get("ATTR_CODE");
			if (attrCode == attrcodein) {
				newAttrValue = $("#" + attrCode).val();
				break;
			}
		}
		if (newAttrValue == null || newAttrValue == "") {
			return true;
		}
		if (g_IsDigit(newAttrValue) == false || newAttrValue * 1 > 100
				|| newAttrValue * 1 < 0) {
			// 套餐折扣不正确
			MessageBox.alert("","套餐折扣的不正确,折扣应为0-100的整数！");
			return false;
		}
		return true;
	}
}

/**
 * 下一步 校验
 * 
 * @return {Boolean}
 */
function productInfoNextCheck()
{

	if(!isCheckedSerialNumber()){return false;}
	var title=mytab.getCurrentTitle();

	mytab.switchTo("产品信息");
	if(!selectedElements.checkForcePackage())
		return false;
	var tempElements = selectedElements.selectedEls;
	if(!tempElements) return false;
	for (var i=0;i<tempElements.length ;i++ )
	   {
	  	 if(tempElements.get(i,"MODIFY_TAG")=='0'&&tempElements.get(i,"ATTR_PARAM_TYPE")=='9'&&tempElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
		 {
			alert(tempElements.get(i,"ELEMENT_NAME")+",缺少服务参数，请补全相关服务参数信息！");
			$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click();
			return false ;
	      }

	   }

	if(typeof(commparaGrpPackageElements)=="function"){
		mytab.switchTo("定制信息");
		//判断是否必选包已经定制上
		if(!checkUserGrpPkgForce()) return false;
		//生成定制信息
		commparaGrpPackageElements();
	}


	mytab.switchTo("账户信息");
	if(!$.validate.verifyAll('acctInfo')) return false;
	if(!checkAcctInfo()){return false;}
	if(!comparaPayPlans()){return false;}


	if($('#pramaPage')){
		mytab.switchTo("产品参数信息");
		if(!$.validate.verifyAll('prama')) return false;
		if(typeof(validateParamPage)=="function"){
			try{
				if (!validateParamPage('CrtUs')) return false;
			}
			catch(e){
				alert('产品参数页面处理出错！');
				return false;
			}

		}
	}
	//add by chenzg@20180711
	if($("#AUDIT_STAFF_ID")&&$("#AUDIT_STAFF_ID").val()==""){
		alert("请选择稽核员！");
		mytab.switchTo("稽核信息");
		return false;
	}
	
	//规则验证
	mytab.switchTo(title);
	$.beginPageLoading('业务验证中....');
	var paramrule  ='&SERIAL_NUMBER='+$("#SERIAL_NUMBER").val()+'&CUST_ID='+$("#CUST_ID").val()+'&PRODUCT_ID='+$("#PRODUCT_ID").val()+'&ALL_SELECTED_ELEMENTS='+tempElements+'&EPARCHY_CODE='+$("#GRP_USER_EPARCHYCODE").val()+'&BUSI_TYPE=CrtUs'+'&ACCT_ID='+$("#acct_ACCT_ID").val();
	//power100
	var power100Info= $('#POWER100_PRODUCT_INFO').val();
	var power100Listsize=0;
	if(power100Info.length>0){
		paramrule  =paramrule +'&POWER100_PRODUCT_INFO='+power100Info;
	}
	var grppackagelist  =  $("#SELECTED_GRPPACKAGE_LIST").val();
	if(typeof(grppackagelist) != 'undefined'){
		paramrule = paramrule +'&SELECTED_GRPPACKAGE_LIST='+grppackagelist;
	}
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupUserRule','checkProductInfoRule',paramrule);

	return false;


}

//业务规则验证成功后，需要把选择的产品元素保存到缓存中
function pageFlowCheckAfterAction(){

	var submitData = selectedElements.getSubmitData();
	if(!submitData)
		return false;
    $('#SELECTED_ELEMENTS').val(submitData);
	//将页面中的元素数据保存到缓存中
	var selectparam  = '&SELECTED_ELEMENTS='+submitData+'&ID='+$("#CUST_ID").val()+'&PRODUCT_ID='+$("#PRODUCT_ID").val()+'&TRADE_TYPE_CODE='+$("#TRADE_TYPE_CODE").val();
	var grppackagelist  =  $("#SELECTED_GRPPACKAGE_LIST").val();
	if(typeof(grppackagelist) != 'undefined'){
		selectparam = selectparam +'&SELECTED_GRPPACKAGE_LIST='+grppackagelist;
	}
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupCacheUtilHttpHandler','saveSelectElementsCache',selectparam,'','',{async:false});

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





function productTabSwitchAction(ptitle,title){

	if ($('#elementPanel').length != 0  && $('#elementPanel').css('display') =='block'){
		$('#elementPanel').css('display','none');
	}

	return true;
}

//------集团短彩信XYZ资费套餐需求验证以下部分暂时写在这里 下个省优化会搬动以下JS(liuxx3 2014 10-11)------------

/**
 * 作用:选择XYZ资费短信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthParam(){
	var xyzCost = $("#30011118").val();
	var xyzItem = $("#30011116").val();
	var xyzExcessCost = $("#30011117").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 200)
	{
		if(Number(xyzItem) < 3000)
		{
			var xyzWithin = Number(xyzCost)/Number(xyzItem);
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需少于3000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>200||Number(xyzCost)==200)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>3000||Number(xyzItem)==3000)&& Number(xyzItem)<16000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.07){
				MessageBox.alert("","您输入的超出费用不得低于0.07元/条 输入有误 请重新输入！");
				return false;
			}
			return true;

		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于3000条小于16000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>16000||Number(xyzItem)==16000)&& Number(xyzItem)<50000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于16000条小于50000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>50000||Number(xyzItem)==50000)&& Number(xyzItem)<100000){
			if(xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于50000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>5500||Number(xyzCost)==5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)){
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条 输入有误 请重新输入！");
			return false;
		}
	}
}


/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZMessageYearParam(){
	var xyzCost = $("#30011108").val();
	var xyzItem = $("#30011106").val();
	var xyzExcessCost = $("#30011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthSpecialParam(){

	var xyzCost = $("#30011338").val();
	var xyzItem = $("#30011336").val();
	var xyzExcessCost = $("#30011337").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageYearSpecialParam(){

	var xyzCost = $("#30011328").val();
	var xyzItem = $("#30011326").val();
	var xyzExcessCost = $("#30011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}


/**
 * 作用:选择XYZ资费彩信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthParam(){
	var xyzCost = $("#30011138").val();
	var xyzItem = $("#30011136").val();
	var xyzExcessCost = $("#30011137").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 400)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem) < 2000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.3){
				MessageBox.alert("","您输入的超出费用不得低于0.3元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
				MessageBox.alert("","您输入的赠送条数需小于2000条 输入有误 请重新输入！");
				return false;
		}

	}

	if((Number(xyzCost)>400||Number(xyzCost)==400)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>2000||Number(xyzItem)==2000)&& Number(xyzItem)<5000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于2000条小于5000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<15000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于15000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>15000||Number(xyzItem)==15000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于15000条小于25000条 输入有误 请重新输入！");
			return false;
		}
	}

	if(Number(xyzCost)>5000||Number(xyzCost)==5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>25000||Number(xyzItem)==25000)
		{
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条 输入有误 请重新输入！");
			return false;
		}

	}
}


/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZ_MMSYearParam(){
	var xyzCost = $("#30011128").val();
	var xyzItem = $("#30011126").val();
	var xyzExcessCost = $("#30011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthSpecialParam(){

	var xyzCost = $("#30011238").val();
	var xyzItem = $("#30011236").val();
	var xyzExcessCost = $("#30011237").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSYearSpecialParam(){

	var xyzCost = $("#30011228").val();
	var xyzItem = $("#30011226").val();
	var xyzExcessCost = $("#30011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZMessageYearParam(){
	var xyzCost = $("#40011108").val();
	var xyzItem = $("#40011106").val();
	var xyzExcessCost = $("#40011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2016 --01 --09
 */
function checkNewXYZMessageYearSpecialParam(){

	var xyzCost = $("#40011328").val();
	var xyzItem = $("#40011326").val();
	var xyzExcessCost = $("#40011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZ_MMSYearParam(){
	var xyzCost = $("#40011128").val();
	var xyzItem = $("#40011126").val();
	var xyzExcessCost = $("#40011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkNewXYZ_MMSYearSpecialParam(){

	var xyzCost = $("#40011228").val();
	var xyzItem = $("#40011226").val();
	var xyzExcessCost = $("#40011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}
//-------------------------集团短彩信XYZ资费套餐需求END(liuxx3 2014-10-11)------------------------------------

function getOtherParam()
{
    var grpProductId=$("#PRODUCT_ID").val();
    
    var param="&PRODUCT_ID="+grpProductId;
    return param;
}