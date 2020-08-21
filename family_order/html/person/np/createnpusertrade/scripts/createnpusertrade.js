$(document).ready(function() {
	$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
	$("#SERIAL_NUMBER").unbind();
	$("#SERIAL_NUMBER").bind("keypress",
	function(event) {
		//回车事件
		if (event.keyCode == 13 || event.keyCode == 108) {
			checkMphone(1);
		}
		
	});
	$("#SERIAL_NUMBER").bind("blur",
	function() {
		checkMphone()
	});
	
	$("#checkSnBtn").unbind();
	$("#checkSnBtn").bind("click",
	function() {
		checkMphone()
	});
	
	//网络类型 
//	$("#NETWORK_TYPE").bind("change",
//			function() {
//				checkNetWorkType();
//	});
	
	
	
	
	$("#HOME_OPERATOR").bind("change",
	function() {
		setNetWorkType();
    });
	
	$.custInfo.setRealName(true);//设置启用实名制
	$("#IS_REAL_NAME").attr("checked",$.custInfo.isRealName);
	$("#IS_REAL_NAME").attr("disabled",$.custInfo.isRealName);
		
	$("#NP_BACK").bind("click",function() {
		checkSerialNumber();
	});
	
	//网络类型只有“GSM”与CDMA参数可选 －－此问题是客户看页面说出来的 
    $("#NETWORK_TYPE option[title!='GSM'][title!='CDMA'][title!='--请选择--']").remove();
	
    
    $("#chkRead").unbind();
    $("#chkRead").bind("click",
    		function() {
    	          showWriteCardBtn()
    		});
    $("#checkSimCardNo").unbind();
    $("#checkSimCardNo").bind("click",
    		function() {
    	checkSimCardNo()
    		});
    
    //先隐藏写卡，和校验 按钮
   // $("#writeCardBtn").css("display","none");
    //$("#checkSimCardNo").css("display","none");
	 $("#bankAgreementNoLi").empty();//银行协议号去掉

})


function setNetWorkType(){
	var home_operator = $("#HOME_OPERATOR").val();//归属运营商:001中国电信002中国移动003中国联通
	 //CDMA==1,CDMA2000=2,GSM=3,TD-SCDMA=4
	if(home_operator == "001")
	{
		$("#NETWORK_TYPE").val(1);
		
	}
	
	if(home_operator == "003")
	{
		$("#NETWORK_TYPE").val(3);
	}
}


/**
 * 勾上显示写卡，去勾显示读卡 不使用
 */
function showWriteCardBtn(){
	if($("#chkRead").attr("checked")){
		$("#writeCardBtn").css("display","");
		$("#readCardBtn").css("display","none");
	}else{
		
		$("#writeCardBtn").css("display","none");
		$("#readCardBtn").css("display","");
	}
}

/**
 * 读卡前的动作
 * @returns {Boolean}
 */
function beforeReadCard(){
	
		var sn = $("#SERIAL_NUMBER").val();
		$.simcard.setSerialNumber(sn);
		return true;
}



/**
 * 读卡后的动作
 */
function afterReadCard(data){
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		$("#SIM_CARD_NO").val(data.get("SIM_CARD_NO"));
		checkSimCardNo(data);
	}else{
		$("#SIM_CARD_NO").val(data.get("SIM_CARD_NO"));
	}
}

function beforeWriteCard(data){
	var sn = $("#SERIAL_NUMBER").val();
	$.simcard.setSerialNumber(sn);
	return true;
}
/**
 * 写卡之后的动作
 * @param data
 */
function afterWriteCard(data){
//	alert(data.toString());
	if(data.get("RESULT_CODE")=="0"){
		//checkSimCardNo(data);
		$.simcard.readSimCard();
	}
}


//页面关闭时调用释放资源流程
function onClose(){
	
} 
var tablecacheData = new Wade.DataMap();

/**
 * 校验网络类型
 */
function checkNetWorkType(){
	var home_operator = $("#HOME_OPERATOR").val();//归属运营商:001中国电信002中国移动003中国联通
	var network_type = $("#NETWORK_TYPE").val();//CDMA==1,CDMA2000=2,GSM=3,TD-SCDMA=4
	if(home_operator == "001" && (network_type!=1 && network_type!=2))
	{
		
		MessageBox.alert("提示","中国电信运商网络只能为 CDMA或 CDMA2000!",function(){
			$("#NETWORK_TYPE").val("");
		},null,null);
	}
	
	if(home_operator == "003" && network_type!=3 )
	{
		
		MessageBox.alert("提示","中国联通运商网络只能为 GSM或 WCDMA!",function(){
			$("#NETWORK_TYPE").val("");
		},null,null);
	}
}




/**
 * 检查号码，查询出产品类型
 * @param flag
 * @returns {Boolean}
 */
function checkMphone() {
	var serial_number = $("#SERIAL_NUMBER").val();
	var len = serial_number.length;
	
	if ( (len != 11) || !/^[0-9]+$/.test(serial_number)) {
			alert("输入的手机号码不对，请重新输入！");
			return;
	}
	
	var reg = /(^14[5|7]\d{8})|(^13[4|5|6|7|8|9]\d{8})|(^15[0|1|2|7|8|9]\d{8})|(^18[2|3|7|8]\d{8})$/;

    if(reg.test(serial_number))
    {
    	$("#NP_BACK").attr("disabled",false);
    }else{
    	$("#NP_BACK").attr("checked",false);
    	$("#NP_BACK").attr("disabled",true);
    	
    }	
    
	
	var npBack = "0";
	if($("#NP_BACK").attr("checked")){
		npBack = "1";
	}
	var key = serial_number+"_isChecked_"+npBack;

	if(tablecacheData.get(key)!=1){
		checkSerialNumber();
	}
}


/**
 * 向后台发起请求较验号码
 */
function checkSerialNumber(){

	var serial_number = $("#SERIAL_NUMBER").val();
	if(serial_number == ""){
		return ;
	}
	var npBack = "0";
	if($("#NP_BACK").attr("checked")){
		npBack = "1";
	}
	$("#NETWORK_TYPE").val("");
	$("#HOME_OPERATOR").val("");
	$.beginPageLoading("号码校验中......");
	var param = "&SERIAL_NUMBER=" + serial_number+"&NP_BACK="+npBack;
	$.ajax.submit(null, 'checkSerialNumber', param, 'CheckSerialNumberHidePart,ProductTypePart,TradeInfoHidePart',
	function(data) {
		var rObj = data.get(0).get("checkSerialNumber");
		var val = rObj.get("HOME_OPERATOR");
		var message = rObj.get("MESSAGE");
		var np_b_can_restore = rObj.get("NP_B_CAN_RESTORE");
		
		var key = serial_number+"_isChecked_"+npBack;
		if (message != "" && typeof(message) != "undefined") {
			//alert(message); //(7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户
			MessageBox.alert("提示",message,null,null,null);
			
		}
		//selectedOption("HOME_OPERATOR", val);//设置归属运营商
		if(1==np_b_can_restore){
			//MessageBox.alert("提示","该号码可以进行复机，可以选择到【携入-复机】界面进行复机。",null,null,null); 携入-复机功能做废
		}
		$.custInfo.setSerialNumber($("#SERIAL_NUMBER").val());
		
		tablecacheData.put(key,"1");//1较验通过	
		$.endPageLoading();
		/**
		 * REQ201602290007 关于入网业务人证一致性核验提醒的需求
		 * chenxy3 2016-03-08
		 * */
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit(null,'checkNeedBeforeCheck','','',
				function(data){ 
					var flag=data.get("PARA_CODE1");
					if(flag=="1"){ 
						var param ="&TRADE_ID=10"+"&EPARCHY_CODE=0898"+"&TRADE_TYPE_CODE=40";
						popupPage('beforecheck.BeforeCheck','init',param,'业务检查','680','400',null,null,null,null,false);
					}
				  	$.endPageLoading();
				},function(error_code,error_info){
					$.MessageBox.error(error_code,error_info);
					$.endPageLoading();
				});
	},
	function(error_code, error_info) {
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * MessageBox.MessageBox.confirm(title,msg,fn,buttons,showMinMax)
 * 处理MessageBox.confirm
 * @param btn
 * @returns {Boolean}
 */
function dealMsgBox(btn)
{
	if(btn=='ok')
	{
	    
	}
	else if(btn=='cancel')
	{
		
	}
}

/**
 * 选中select
 * @param select名称
 * @param val 值 
 */
function selectedOption(name, val) {

	var srtval = val.substring(2);
	$("#" + name + " option[value=" + val + "]").attr("selected", true);
	$("#NETWORK_TYPE option[value=" + srtval + "]").attr("selected", true);
	
}

/**
 * 
 */
function checkBeforeProduct() {
	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	//	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	//	var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	//	if(checkResultCode=="-1"){
	//		alert("新开户号码校验未通过！");
	//		$("#SERIAL_NUMBER").focus();
	//		return false;
	//	}
	//	if(checkResultCode=="0"){
	//		alert("SIM卡号校验未通过！");
	//		$("#SIM_CARD_NO").focus();
	//		return false;
	//	}
	//	if(checkPsptCode != "1"){
	//	   alert("证件号码校验未通过！");
	//	   	$("#PSPT_ID").focus();
	//	   return false;
	//	}
	// if(!verifyAll('BaseInfoPart'))
	//   {
	//	   return false;
	//   }
	var eparchy_code = $("#EPARCHY_CODE").val();
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(), eparchy_code, '');
}

/**
 * 选完产品后的动作
 * @param productId
 * @param productName
 * @param brandCode
 * @param brandName
 */
function afterChangeProduct(productId, productName, brandCode, brandName) {
	$("#PRODUCT_ID").val(productId);
	$("#PRODUCT_NAME").val(productName);
	var eparchy_code = $("#EPARCHY_CODE").val();
	var param = "&NEW_PRODUCT_ID=" + productId;
	offerList.renderComponent($("#PRODUCT_ID").val(), eparchy_code);
//	pkgElementList.initElementList(null);
	
	selectedElements.renderComponent(param, eparchy_code);
	$.cssubmit.disabledSubmitBtn(false);
	$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
	
	 $.feeMgr.clearFeeList("40");
     $.feeMgr.insertFee(tablecacheData.get("SIMCARD_FEE"));
	
	var feeData = $.DataMap();
	var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+eparchy_code;
	$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
	    $.cssubmit.disabledSubmitBtn(false);
	  	for(var i = 0; i < data.getCount(); i++) {
	  	     var data0 = data.get(i);
		     if(data0){
		    	 //应乃捷要求，携入申请开户不需要费用
//						feeData.clear();
//						feeData.put("MODE", data0.get("FEE_MODE"));
//						feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
//						feeData.put("FEE",  data0.get("FEE"));
//						feeData.put("PAY",  data0.get("FEE"));		
//						feeData.put("TRADE_TYPE_CODE","40");							
//						$.feeMgr.insertFee(feeData);			
				}
	  	}

       },
       function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		});

}

/**
 * 产品搜索
 * @returns {Boolean}
 */

function changeSearchType(eventObj){
	var searchType = eventObj.value;
	var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
	param += "&SEARCH_TYPE="+searchType;
	if(searchType == "2"){
		param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
	}
	$.Search.get("productSearch").setParams(param);
}


function searchOptionEnter() {
	var searchType = $("#productSearchType").val();
	var searchLi = $("#Ul_Search_productSearch li[class=focus]");
	if (searchType == "1") {
		//产品搜索
		var productId = searchLi.attr("PRODUCT_ID");
		var productName = searchLi.attr("PRODUCT_NAME");
		var brandCode = searchLi.attr("BRAND_CODE");
		var brandName = searchLi.attr("BRAND");
		afterChangeProduct(productId, productName, brandCode, brandName);
	} else if (searchType == "2") {
		//元素搜索
		var reOrder = searchLi.attr("REORDER");
		var elementId = searchLi.attr("ELEMENT_ID");
		var elementName = searchLi.attr("ELEMENT_NAME");
		var productId = searchLi.attr("PRODUCT_ID");
		var packageId = searchLi.attr("PACKAGE_ID");
		var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
		var forceTag = searchLi.attr("FORCE_TAG");

		if (reOrder != "R" && selectedElements.checkIsExist(elementId, elementTypeCode)) {
			alert("您所选择的元素" + elementName + "已经存在于已选区，不能重复添加");
			return false;
		}
		var elementIds = $.DatasetList();
		var selected = $.DataMap();
		selected.put("PRODUCT_ID", productId);
		selected.put("PACKAGE_ID", packageId);
		selected.put("ELEMENT_ID", elementId);
		selected.put("ELEMENT_TYPE_CODE", elementTypeCode);
		selected.put("MODIFY_TAG", "0");
		selected.put("ELEMENT_NAME", elementName);
		selected.put("FORCE_TAG", forceTag);
		selected.put("REORDER", reOrder);
		elementIds.add(selected);
		if (selectedElements.addElements) {
			selectedElements.addElements(elementIds);
		}
	}
	$("#Div_Search_productSearch").css("visibility", "hidden");
}

/**
 * 设置品牌
 */
function setBrandCode() {
	if ($("#PRODUCT_TYPE_CODE").val() != "") {
		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	} else {
		$("#BRAND").val('');
	}
}

/**
 * 初始化产品产
 */
function initProduct() {
	var eparchy_code = $("#EPARCHY_CODE").val();
	//packageList.renderComponent("",$("#EPARCHY_CODE").val());
	offerList.renderComponent("", eparchy_code);
//	pkgElementList.initElementList(null);
	//selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", eparchy_code);
	$("#PRODUCT_NAME").val('');
}

function disableElements(data) {
	if ($("#B_REOPEN_TAG").val() == '1') {
		selectedElements.disableAll();
	} else {
		if (data) {
			var temp = data.get(0);
			if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
				$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
			}
		}
	}
}












//检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByPspt(){
    var custName = $("#CUST_NAME").val();
    var psptId = $("#PSPT_ID").val();
    if(custName == "" || psptId == ""){
        return false;
    }
   var eparchy_code = $("#EPARCHY_CODE").val();
   $.beginPageLoading("新开户实名制校验中......");
   $.ajax.submit(null,'checkRealNameLimitByPspt','&custName='+custName+'&psptId='+psptId+'&EPARCHY_CODE='+eparchy_code,'',function(data){
  	
	    if(data.get(0).get("CODE") == '0')
	    {
	       return true;
	    }else{
	        alert(data.get(0).get("MSG"));
	        return false;
	    }
	     $.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
    });
    return true;
}



	 

function onSubmit(){
	var phone = $("#PHONE").val();
	if(phone.length >= 15){
		alert("联系电话不能大于等于15位！");
		$("#PHONE").focus();
		 return false;
	}
	var serial_number = $("#SERIAL_NUMBER").val();
	 if(!$.validate.verifyAll()){
		 return false;
	 }
	 
	 var isRealName = "1";
	 if(!$.custInfo.isRealName)
	 {
		 isRealName = "0";
	 }
	 
	 var npBack = "0";
		if($("#NP_BACK").attr("checked")){
			npBack = "1";
		}
	var key = serial_number+"_isChecked_"+npBack;
	if(tablecacheData.get(key)!="1"){
		 alert("号码校验未通过,请选校验号码！");
		 return false;
	}
	var simCardNo = $("#SIM_CARD_NO").val();
	var simKey = simCardNo+"_isChecked";
	if(tablecacheData.get(simKey)!="1"){
		 alert("sim卡校验未通过,请选校验im卡！");
		 return false;
	}
	
	var _productName = $("#PRODUCT_NAME").val();
	if(_productName ==""){
		alert("请选择产品！");
		 return false;
	}
	
	var _home_operator = $("#HOME_OPERATOR").val();
	if(_home_operator ==""){
		alert("归属运营商不能为空！");
		 return false;
	}
	
	var _network_type = $("#NETWORK_TYPE").val();
	if(_network_type ==""){
		alert("网络类型 不能为空！");
		 return false;
	}
	
	var canSubmit = selectedElements.checkForcePackage();
	if(!canSubmit){
		return false;
	}
	var resStr = tablecacheData.get(simCardNo).toString();
	var data = selectedElements.getSubmitData();
	var invoiceNo = $("#_INVOICE_CODE").val()
	var param = "&SELECTED_ELEMENTS=" + data.toString() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val()+"&IS_REAL_NAME="+isRealName+"&INVOICE_NO="+invoiceNo+"&NP_BACK="+npBack+"&RES_INFO_DATA="+resStr;
	$.cssubmit.addParam(param);
	return true;
	
}








/*备注信息特殊字符校验*/
function checkRemark() {
	var remark = $("#REMARK").val();
	if(remark=="") {
		return true;
	}
	
	var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~!@#￥……&*()——|{}【】‘;:”“'。,、?]");
	if(pattern.test(remark)){
		alert("备注信息不充许包含特殊字符！！！");
		return false;
	}
	
	return true;	
}





/*密码组件前校验*/
function PasswdbeforeAction() {
	 if($("#PSPT_TYPE_CODE").val()==""){
      alert("证件类型不能为空！");
      return false;
    }
     if($("#PSPT_ID").val()==""){
      alert("证件号码不能为空！");
      return false;
    }
    
     //if($("#BIRTHDAY").val()==""){  
    	// $("#BIRTHDAY").val("1900-01-01");
      //alert("出生日期不能为空！");
      //return false;
    //}
    //将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) {

$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

//校验sim卡
function checkSimCardNo(data){
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo == ""){
		alert("sim卡号不能为空！");
		return false;
	}
	
	if(simCardNo.length<20){
		alert("输入的SIM卡长度不正确，请重新输入！");
		return false;
	}
	
   var eparchy_code = $("#EPARCHY_CODE").val();	
 
   var serial_number = $("#SERIAL_NUMBER").val();
	var npBack = "0";
	if($("#NP_BACK").attr("checked")){
		npBack = "1";
	}
	var key = serial_number+"_isChecked_"+npBack;
	if(tablecacheData.get(key)!="1"){
		 alert("号码校验未通过,请选校验号码！");
		 return false;
	}
   
	var simKey = simCardNo+"_isChecked";
	if(tablecacheData.get(simKey)=="1"){
		 alert("sim卡["+simCardNo+"]校验已通过！");
		 return false;
	}
	var emptyCardId = "";
		
	//if(data){
//		emptyCardId = data.get("EMPTY_CARD_ID");
//		if(emptyCardId == "" || emptyCardId == null){
//			alert("携号转网必须是空白卡！");
//			return false;
//		}
//	}
	
   $.beginPageLoading("SIM卡校验中......");
   $.ajax.submit(null,'checkSimCardNo','&SIM_CARD_NO='+simCardNo+'&ROUTE_EPARCHY_CODE='+eparchy_code+'&SERIAL_NUMBER='+serial_number+'&EMPTY_CARD_ID='+emptyCardId,'',function(data){
  	      //alert(data);
	     if(data){
	    	 var key = simCardNo+"_isChecked";
		    	 tablecacheData.put(key,"1");//1较验通过
		    	 tablecacheData.put(simCardNo,data.get(0).get("RES_INFO_DATA"));
		    	 
	    	     alert("sim卡校验通过！"); 
	    	 var mainRsrvStr8 = data.get(0).get("MAIN_RSRV_STR8");
	    	 var feeMode = data.get(0).get("FEE_MODE");
	    	 var feeTypeCode = data.get(0).get("FEE_TYPE_CODE");
	    	 var fee = data.get(0).get("FEE");
	    	 if(fee !=null && fee!=""){
	    		 var obj = new Wade.DataMap();
				 obj.put("TRADE_TYPE_CODE", "40");
				 obj.put("MODE", feeMode); 
				 obj.put("CODE", feeTypeCode); 
				 obj.put("FEE", fee);
				 $.feeMgr.removeFee("40",feeMode,feeTypeCode);
				 $.feeMgr.insertFee(obj);
				 tablecacheData.put("SIMCARD_FEE",obj);
	    	 }
	    	 
	     }
	     $.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
    });
}