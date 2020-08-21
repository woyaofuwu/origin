var PAGE_FEE_LIST = $.DataMap();

function refreshPartAtferAuth(data) {
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var userId = data.get("USER_INFO").get("USER_ID");
	var netTypeCoce = data.get("USER_INFO").get("NET_TYPE_CODE");
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var param = "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId
			+ "&NET_TYPE_CODE=" + netTypeCoce + "&EPARCHY_CODE=" + eparchyCode;
	$.ajax.submit('AuthPart', 'loadChildInfo', param,'TELLEPHONEInfo,ProductTypePart,userResInfosPart,hiddenPart,BusiInfoPart',
			function(data) {
				if (data && data.length > 0) {
					var disableRestoreFlag = data.get("DISABLE_RESTORE");
					var msg = data.get("MESSAGE_CONTENT");
					if(disableRestoreFlag==1){
						$.endPageLoading();
						MessageBox.alert("",msg, $.auth.reflushPage);
					}else{
						if(msg!=null && msg!="")
						{
							alert(msg);
						}
					}
					
					var resReason = data.get("RESET_REASON");
					if(resReason!=null && resReason!="")
					{
						alert(resReason);
					}
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});

}


function myTabSwitchAction(v, v2) {

	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	if (v != title) {
		objTabset.switchTo(v);
	}
	if (v2 != title) {
		objTabset.switchTo(v2);
	}

}

//添加新的一行资源记录
function addResRow(resTypeCode,resNo,startDate)
{
	var resBody = getElement("userResTable");
	var oRow1=resBody.insertRow(resBody.rows.length);
	// 获取表格的行集合。
	var aRows=resBody.rows;
	// 获取第一行的单元格集合。
	var aCells=oRow1.cells;
	// 在第一行中插入两个单元格。
	var oCell1_1=aRows(oRow1.rowIndex-1).insertCell(aCells.length);
	var oCell1_2=aRows(oRow1.rowIndex-1).insertCell(aCells.length);
	var oCell1_3=aRows(oRow1.rowIndex-1).insertCell(aCells.length);
	var oCell1_4=aRows(oRow1.rowIndex-1).insertCell(aCells.length);
	if(resTypeCode==0)
	{
		oCell1_1.innerHTML="新手机号码";
		oCell1_2.innerHTML=resNo;
		$("#HAVE_NEW_SERIAL_NUMBER").value="1";
	}
	if(resTypeCode==1)
	{
		oCell1_1.innerHTML="新SIM卡号";
		oCell1_2.innerHTML=resNo;
		$("#HAVE_NEW_SIM_CARD_NO").value="1";
	}
	oCell1_3.innerHTML=startDate;
	oCell1_4.innerHTML="2050-12-31 23:59:59";
}

$(document).ready(function() {

	// $("#AUTH_SUBMIT_BTN").unbind();
	// $("#AUTH_SUBMIT_BTN").bind("click",
	// function() {
	// queryDestroyedUserInfo();
	// });
});

//点击表格中某资源信息
function tableRowClick(obj) {
//	checkDisabledOper($("#checkResBt"),1); //资源校验按钮不可操作
	
	var rowIndex = obj.rowIndex;//当前操作行
	var resTableObj = $.table.get("userResTable");//获取 资源表格对象
    var json = resTableObj.getRowData(null,rowIndex); //获得当前行数据
    var res_type_code = json.get("col_RES_TYPE_CODE");//当前行资源类型
    var res_code = json.get("col_RES_CODE");//当前行资源编码
    
    $("#rowIndex").val(rowIndex);//保存选择 index  后续【资源校验后】 时需要
    $("#RES_INFO").val(json.get("col_IMSI"));
    $("#RES_CODE").val(json.get("col_RES_CODE"));
    $("#RES_TYPE_CODE").val(json.get("col_RES_TYPE_CODE"));
	$("#START_DATE").val(json.get("col_START_DATE"));
	$("#END_DATE").val(json.get("col_END_DATE"));
	
//	checkDisabledOper($("#checkResBt"),2);
	return;
}

//资源校验 按钮事件
function checkRes() 
{
	var newResCode =  $('#RES_CODE').val();//输入的资源编码
	if(newResCode==null || newResCode=="")
	{
		alert('请输入新资源号码再进行校验！');
		return false;
	}
	
	var newSimCard = $('#NEW_SIM_CARD_NO').val();//用户上一次新换的sim卡
    var newPhone = $('#NEW_PHONE_NO').val();//用户上一次新换的号码
    var resType = $('#RES_TYPE_CODE').val();//当前表格选择行的资源类型编码
    
    if(resType == "")
    {
      alert("无法获取到当前校验资源的类型，请重新点击资源信息表格中需要更换的数据！");
      return false;
    }
    
    var oldResCode = $('#col_OLD_RES_CODE').val(); //用户原来的资源号
	if(resType == "0")
	{
		if((newPhone=="" && newResCode==oldResCode) || newResCode==newPhone)
		{
			alert('请输入新手机号码再进行校验！');
			return false;
		}		
		if(!isTel(newResCode))
		{
			alert('手机号码格式不正确，请重新输入！');
			return false;
		}		
	}else if(resType == "1")
	{
		if((newSimCard=="" && newResCode==oldResCode) || newResCode==newSimCard)
		{
			alert('请输入新的SIM再进行校验！');
			return false;
		}		
	}
	
	var param = '&RES_TYPE_CODE='+resType+'&RES_CODE='+newResCode+'&OLD_RES_CODE='+oldResCode
				+'&NEW_SIM_CARD_NO='+newSimCard +'&NEW_PHONE_NO='+newPhone
				+'&PRODUCT_ID=' + $('#PRODUCT_ID').val()
				+ '&USER_INFO='+ $.auth.getAuthData().get("USER_INFO");
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit(null, 'checkNewResource',param, null, function(data){
		afterCheckNewRes(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}

//资源校验后
function afterCheckNewRes(data)
{
	if(data.get("RESULT_CODE")=="N")
	{
		alert("新资源校验失败！");
		return false;
		$("#CAN_USE").val("1");
	}
	
	var resType = $('#RES_TYPE_CODE').val();//当前操作的资源类型
	var newResCode = data.get("RES_CODE");
	var imsi = data.get("IMSI");
	var ki = data.get("KI");
	var serialNumber = data.get("SERIAL_NUMBER");
	var startDate = data.get("START_DATE");
	var endDate =  data.get("END_DATE");
/*	if((imsi==null || imsi=="") && (serialNumber==null || serialNumber==""))
	{
		alert('该资源不存在或者已经被占用，请输入新资源号！');
		return false;
	}else*/
	{
/*		if(resType == 0)//手机号码
		{
			var rsrvtag3 =  data.get("RSRV_TAG3");
			alert('新服务号码和用户原SIM卡不匹配，请换卡！');
			$('#SIM_CHECK_TAG').val(false);
		}*/
		if(resType == "0")
		{
			$('#NEW_PHONE_NO').val(newResCode);
			$('#PHONE_CHECK_TAG').val(data.get("PHONE_CHECK_TAG"));
			alert('校验通过！');
		}else if(resType == "1")
		{	
			$('#NEW_SIM_CARD_NO').val(newResCode);
			$('#FREE_SIMCARD_FEE_TAG').val(data.get("FREE_SIMCARD_FEE_TAG"));
			$('#SIM_CHECK_TAG').val(data.get("SIM_CHECK_TAG"));
			alert('校验通过！'+data.get("TIPS_INFO"));
		}
		
		$('#START_DATE').val(startDate);
		$('#END_DATE').val(endDate);
		$('#OPC_VALUE').val(data.get("OPC_VALUE"));
		
		//删除费用
		$.feeMgr.removeFee("310","0","10"); 
		
		//获取资源费用
		var thefee =  data.get("DEVICE_PRICE");
		if(thefee && thefee!=null && thefee!="" && parseInt(thefee)) {
			var feeData =  $.DataMap();
			feeData.put("MODE","0");
			feeData.put("CODE","10");
			feeData.put("FEE",thefee);
			feeData.put("TRADE_TYPE_CODE","310");				
			//$.feeMgr.insertFee(feeData);//新增费用
		}
		

		 //修改选择行的数据
		 var tableObj = $.table.get("userResTable");
		 var nowIndex =$("#rowIndex").val();
		 var rowEdit = $.ajax.buildJsonData("userResInfosPart");
		 rowEdit["col_RES_CODE"]=$('#RES_CODE').val();
		 rowEdit["col_START_DATE"]=startDate;
		 rowEdit["col_END_DATE"]=endDate;
		 rowEdit["col_IMSI"]=imsi;
		 rowEdit["col_KI"]=ki;
		 var updaterow = tableObj.updateRow(rowEdit);
		 
		 var thesubmit = $('#CSSUBMIT_BUTTON');
		 if(thesubmit != null && thesubmit != "")
		 {
			thesubmit.attr("disabled",false);
		    thesubmit.attr("className","e_button-page-ok");
		 }
	}
}

//提交 台账
function submitBeforeAction()
{
	var phoneCheckTag = $('#PHONE_CHECK_TAG').val();
	var simCheckTag = $('#SIM_CHECK_TAG').val();
	if(phoneCheckTag=="0")
	{
		alert("服务号码校验不通过，请先校验服务号码！");
		return false;
	}
	if(simCheckTag=="0"){
		alert("sim卡校验不通过，请先校验sim卡资源 ！");
		return false;
	}
	var data = selectedElements.getSubmitData();
	if(data==null || data=="" || data.length==0){
		alert("请选择产品！");
		return false;
	}
	var param = "&SELECTED_ELEMENTS=" + data.toString() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val();
    
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
	var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	param = param + "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId
		+ "&EPARCHY_CODE=" + eparchyCode;
	var data = $.table.get("userResTable").getTableData(null,true);//获取整个表格数据
	$("#X_CODING_STR").val(data) ;		
	$.cssubmit.addParam(param);
	return true;
}


/******************************工具方法*****************************************/
//设置某个原始的状态
function checkDisabledOper(obj,tag){
	if(tag == true){
		obj.attr("disabled",true);
		obj.attr("className","e_button-right e_dis");
	}
	if(tag == false){
		obj.attr("disabled",false);
		obj.attr("className","e_button-right");
	}
}

function clearTableData(obj)
{
//	var nodes = obj.childNodes;
//	var count = nodes.length;
//	for(var i=0;i<count;i++)
//	{
//		obj.removeChild(nodes(0));
//	}
}

function isTel(str){
    var reg=/^([0-9]|[\-])+$/g ;
    if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
     return false;
    }
    else{
      return reg.exec(str);
    }
}


//===================产品组件脚本开始================================================
/**
 * 选中select
 * 
 * @param select名称
 * @param val
 *            值
 */
function selectedOption(name, val) {

	$("#" + name + " option[value=" + val + "]").attr("selected", true);
}

function checkBeforeProduct() {

	var eparchy_code = $.auth.getAuthData().get("USER_INFO")
			.get("EPARCHY_CODE");
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),
			eparchy_code, '');
}

/**
 * 选完产品后的动作
 * 
 * @param productId
 * @param productName
 * @param brandCode
 * @param brandName
 */
function afterChangeProduct(productId, productName, brandCode, brandName) {
    $.feeMgr.clearFeeList("310");
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
    var feeData = $.DataMap();
    	feeData.clear();
		feeData.put("MODE", "2");
		feeData.put("CODE", "0");
		feeData.put("FEE",  "0");
		feeData.put("PAY",  "0");		
		feeData.put("TRADE_TYPE_CODE","310");
		
		$.feeMgr.insertFee(feeData);	
		
		/*feeData.clear();
		feeData.put("MODE", "2");
		feeData.put("CODE", "2");
		feeData.put("FEE",  "0");
		feeData.put("PAY",  "0");		
		feeData.put("TRADE_TYPE_CODE","10");
		$.feeMgr.insertFee(feeData);*/
     $("#PRODUCT_ID").val(productId);
     $("#PRODUCT_NAME").val(productName);
      var param = "&NEW_PRODUCT_ID="+productId;
      var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
      offerList.renderComponent($("#PRODUCT_ID").val(), eparchyCode);
//      pkgElementList.initElementList(null);
		selectedElements.renderComponent(param, eparchyCode);
		//变换产品后，重置搜索组件 
		var setData = "&EPARCHY_CODE="+eparchyCode;
		setData += "&PRODUCT_ID="+productId;
		setData += "&SEARCH_TYPE=2";
		$.Search.get("productSearch").setParams(setData);
		
		var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode 
			+ "&EPARCHY_CODE="+ eparchyCode;
		$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
		    $.cssubmit.disabledSubmitBtn(false);
		  	for(var i = 0,count=data.getCount(); i < count; i++) {
		  	     var data = data.get(i);
		  	     if(data){
							feeData.clear();
							feeData.put("MODE", data.get("FEE_MODE"));
							feeData.put("CODE", data.get("FEE_TYPE_CODE"));
							feeData.put("FEE",  data.get("FEE"));
							feeData.put("PAY",  data.get("FEE"));		
							feeData.put("TRADE_TYPE_CODE","310");							
							$.feeMgr.insertFee(feeData);			
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
 * 
 * @returns {Boolean}
 */
function searchOptionEnter() {
	var searchType = $("#productSearchType").val();
	var searchLi = $("#Ul_Search_productSearch li[class=focus]");
	if (searchType == "1") {
		// 产品搜索
		var productId = searchLi.attr("PRODUCT_ID");
		var productName = searchLi.attr("PRODUCT_NAME");
		var brandCode = searchLi.attr("BRAND_CODE");
		var brandName = searchLi.attr("BRAND");
		afterChangeProduct(productId, productName, brandCode, brandName);
	} else if (searchType == "2") {
		// 元素搜索
		var reOrder = searchLi.attr("REORDER");
		var elementId = searchLi.attr("ELEMENT_ID");
		var elementName = searchLi.attr("ELEMENT_NAME");
		var productId = searchLi.attr("PRODUCT_ID");
		var packageId = searchLi.attr("PACKAGE_ID");
		var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
		var forceTag = searchLi.attr("FORCE_TAG");

		if (reOrder != "R"
				&& selectedElements.checkIsExist(elementId, elementTypeCode)) {
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
	var eparchy_code = $.auth.getAuthData().get("USER_INFO")
			.get("EPARCHY_CODE");
	// packageList.renderComponent("",$("#EPARCHY_CODE").val());
	offerList.renderComponent("", eparchy_code);
//	pkgElementList.initElementList(null);
	// selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
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
				$("#NEW_PRODUCT_START_DATE").val(
						temp.get("NEW_PRODUCT_START_DATE"));
			}
		}
	}
}
// ===================产品组件脚本结束================================================