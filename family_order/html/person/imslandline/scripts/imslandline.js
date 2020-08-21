function displaySwitch(btn, o){
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}

function changeSearchType(eventObj) {
	var searchType = eventObj.value;
	var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
	param += "&SEARCH_TYPE="+searchType;
	if(searchType == "2"){
		param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
	}
	$.Search.get("productSearch").setParams(param);
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
	offerList.renderComponent("",eparchy_code);
    selectedElements.renderComponent("",eparchy_code);
    $("#productSelectBtn").attr("disabled", false);
	$("#PRODUCT_NAME").val('');
}

function checkBeforeProduct(){
	if(!verifyAll('AuthPart') || !verifyAll("widenetInfoPart"))
	return false;
   
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
}

function afterChangeProduct(productId,productName,brandCode,brandName){
     $("#PRODUCT_ID").val(productId);
     $("#PRODUCT_NAME").val(productName);
     var param = "&NEW_PRODUCT_ID="+productId;
     offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
     selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
     
     queryPackages();
}

function searchOptionEnter(){
	var searchType = $("#productSearchType").val();
	var searchLi = $("#Ul_Search_productSearch li[class=focus]");
	
	if(searchType == "1"){
		// 产品搜索
		var productId = searchLi.attr("PRODUCT_ID");
		var productName = searchLi.attr("PRODUCT_NAME");
		var brandCode = searchLi.attr("BRAND_CODE");
		var brandName = searchLi.attr("BRAND");
		afterChangeProduct(productId,productName,brandCode,brandName);
	}
	else if(searchType == "2"){
		// 元素搜索
		var reOrder = searchLi.attr("REORDER");
		var elementId = searchLi.attr("ELEMENT_ID");
		var elementName = searchLi.attr("ELEMENT_NAME");
		var productId = searchLi.attr("PRODUCT_ID");
		var packageId = searchLi.attr("PACKAGE_ID");
		var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
		var forceTag = searchLi.attr("FORCE_TAG");
		
		if(reOrder!="R"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
			alert("您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
			return false;
		}
		var elementIds = $.DatasetList();
		var selected = $.DataMap();
		selected.put("PRODUCT_ID",productId);
		selected.put("PACKAGE_ID",packageId);
		selected.put("ELEMENT_ID",elementId);
		selected.put("ELEMENT_TYPE_CODE",elementTypeCode);
		selected.put("MODIFY_TAG","0");
		selected.put("ELEMENT_NAME",elementName);
		selected.put("FORCE_TAG",forceTag);
		selected.put("REORDER",reOrder);
		elementIds.add(selected);
		if(selectedElements.addElements){
			selectedElements.addElements(elementIds);
		}
	}
	$("#Div_Search_productSearch").css("visibility","hidden");
}

function disableElements(data){
	 if($("#B_REOPEN_TAG").val()=='1'){
	    selectedElements.disableAll();
	 }else{
	   if(data){
	     var temp = data.get(0);
	     if(data.get(0).get("NEW_PRODUCT_START_DATE")){
				$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
		 }
	   }
	 }
}

function checkFixPhone(){
	if(!$.validate.verifyAll("FixPhonePart"))
	{
		return false;
	}
	var oldFixNumber = $("#OLD_FIX_NUMBER").val();
	var fixNumber=$("#FIX_NUMBER").val();
	var cityCodeRsrvStr4=$("#RSRV_STR4").val();
	
	if (null != oldFixNumber && '' != oldFixNumber)
	{
		if (oldFixNumber == fixNumber)
		{
			alert('该号码已经校验通过，不需要再次校验!');
			return false
		}
	}
	
	$.beginPageLoading("校验固话号码...");
	$.ajax.submit('', 'checkFixPhoneNum', "&FIX_NUMBER="+fixNumber+"&CITYCODE_RSRVSTR4="+cityCodeRsrvStr4, '', function(rtnData) {
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){
			if(rtnData.get("RESULT_CODE")=="1"){
				$("#OLD_FIX_NUMBER").val(fixNumber);
				alert(rtnData.get("RESULT_INFO"));
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
			}else{
				alert(rtnData.get("RESULT_INFO"));
				$("#FIX_NUMBER").val("");
				$("#OLD_FIX_NUMBER").val("");
			} 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		$.MessageBox.error(error_code, error_info);
		$("#FIX_NUMBER").val("");
		$("#OLD_FIX_NUMBER").val("");
	}); 

	$.endPageLoading();
}

/** 用戶认证结束之后执行的js方法 */
function refreshPartAtferAuth(data){
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,FixPhonePart,ProductTypePart',
			function(data) {
				$.endPageLoading();
				var resultCode=data.get("RESULT_CODE");
				if(resultCode=="1"){
					$("#FIX_NUMBER").attr("disabled",false);
					$("#CHECK_BTN").attr("disabled",false); 
					alert("校验通过，请录入固话号码，点击【校验】按钮。");
					$("#FIX_NUMBER").focus();
					$.cssubmit.disabledSubmitBtn(true,"submitButton");
				}else{
					var resultInfo=data.get("RESULT_INFO");
					alert("手机号码校验不通过："+resultInfo);
					return;
				}
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true,"submitButton");
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/** 业务提交校验 */
function submitBeforeCheck(){
	var productId=$("#PRODUCT_ID").val();
	if(!productId || productId == ""){
		alert("IMS固话产品不能为空！");
		return false;
	}
	
	if(!selectedElements.checkForcePackage())
		return false;

	if(!$.validate.verifyAll("widenetInfoPart") || !$.validate.verifyAll("FixPhonePart")) {
		return false;
	}
	
	//开始费用校验
	var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();//营销活动预存 单位：分
	
	if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
	{
		topSetBoxSaleActiveFee = '0';
	}
	
	var feeFlag = true;
	var totalFee = parseFloat(topSetBoxSaleActiveFee)/100;
				
	if (totalFee > 0)
	{
		feeFlag = false ;
		var tips = "您总共需要转出："+totalFee
			 	+ "元，家庭IMS固话活动预存："+parseFloat(topSetBoxSaleActiveFee)/100
			 	+ "元，请确认您的余额是否足够？";

		if(window.confirm(tips))
		{
			//后台余额校验
			feeFlag = checkFeeBeforeSubmit();
		}
		else
		{
			return false;
		}
	}
				
	if(!feeFlag)
	{
		return false ;
	}
	
	var data = selectedElements.getSubmitData();
    var param = "&SELECTED_ELEMENTS="+data.toString()+"&PRODUCT_ID="+$("#PRODUCT_ID").val()+"&TOP_SET_BOX_SALE_ACTIVE_FEE="+$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
    $.cssubmit.addParam(param);
    
	return true;
}


function queryPackages(){
	var productId = $("#PRODUCT_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	var param = "&PRODUCT_ID="+productId+"&SERIAL_NUMBER="+serialNumber+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId;

	$.beginPageLoading("IMS营销活动查询中......");
	$.ajax.submit(null, 'queryDiscntPackagesByPID', param, 'saleActivePart',
			function(data) {
				//将魔百和营销活动信息传出
				$("#MO_SALEACTIVE_LIST").val(data.get('TOP_SET_BOX_SALE_ACTIVE_LIST'));
				
				$("#MO_PRODUCT_ID").val('');
				$("#MO_PACKAGE_ID").val('');
				$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');//描述置为空
				
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
			
	//每次重新选择魔百和产品，都将营销活动的预存置为空
	$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
}

function changeTopSetBoxSaleActive(){

	var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	var moSaleActiveList = $("#MO_SALEACTIVE_LIST").val();
	
	//如果选中的营销活动有值
	if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
	{
		var topSetBoxSaleActiveProductId = '';//魔百和营销方案ID
		var topSetBoxSaleActivePackageId = '';//魔百和营销包ID
		var topSetBoxSaleActiveExplain = '';//魔百和营销活动描述
		var ruleTag=''; //依赖宽带产品的规则标记
		var depProdIds='';//依赖宽带产品串

		if (null != moSaleActiveList)
		{
			var moSaleActiveDataset = $.DatasetList(moSaleActiveList);
			for (var i = 0; i < moSaleActiveDataset.length; i++)
			{
				if (topSetBoxSaleActiveId == moSaleActiveDataset.items[i].get('PARA_CODE2'))
				{
					topSetBoxSaleActiveProductId = moSaleActiveDataset.items[i].get("PARA_CODE4");
					topSetBoxSaleActivePackageId = moSaleActiveDataset.items[i].get("PARA_CODE5");
					topSetBoxSaleActiveExplain = moSaleActiveDataset.items[i].get("PARA_CODE24");
					ruleTag=moSaleActiveDataset.items[i].get("PARA_CODE22");
					depProdIds=moSaleActiveDataset.items[i].get("PARA_CODE23");
				}
			}
		}

		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
		var inparam = "&ROUTE_EPARCHY_CODE=" + eparchyCode 
					+ "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
					+ "&PRODUCT_ID=" + topSetBoxSaleActiveProductId
					+ "&PACKAGE_ID=" + topSetBoxSaleActivePackageId
					+ "&RULE_TAG=" + ruleTag
					+ "&DEP_PRODUCT_ID=" + depProdIds;
					
		$.beginPageLoading("IMS营销活动校验中。。。");

		$.ajax.submit(null, 'checkSaleActive', inparam, null,
						function(data) 
						{
							$.endPageLoading();
							$("#MO_PRODUCT_ID").val(topSetBoxSaleActiveProductId);
							$("#MO_PACKAGE_ID").val(topSetBoxSaleActivePackageId);
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val(topSetBoxSaleActiveExplain);
							$("#TOP_SET_BOX_DEPOSIT").val("0");
							
							//营销活动费用需要重新取产品模型配置
					 		checkTopBoxSetSaleActiveFee(topSetBoxSaleActiveProductId,topSetBoxSaleActivePackageId);
							
						}, 
						function(error_code, error_info) 
						{
							$("#MO_PRODUCT_ID").val('');
							$("#MO_PACKAGE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
							$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
								
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						}
					);
	}
	else
	{
		$("#MO_PRODUCT_ID").val('');
		$("#MO_PACKAGE_ID").val('');
		$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');//描述置为空
		$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');//如果不选营销活动，则将活动预存值置为空
		$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
	}
}

function checkTopBoxSetSaleActiveFee(productId,packageId)
{
	$.beginPageLoading("魔百和宽带营销活动费用校验中。。。");
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var param = "&SERIAL_NUMBER="+ serialNumber + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=2";
				
	$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(fee);//很重要，将活动下的元素费用传出
					}, function(error_code, error_info) 
					{
						$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
								
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
}

/** 提交前费用校验 */
function checkFeeBeforeSubmit()
{
	var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();//单位：分
	var result = false;
					
	if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
	{
		topSetBoxSaleActiveFee = '0';
	}
				
	var param = "&TOPSETBOX_SALE_ACTIVE_FEE=" + topSetBoxSaleActiveFee
			  + "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				
	$.beginPageLoading("提交前费用校验中。。。");			
	$.ajax.submit(null, 'checkFeeBeforeSubmit', param, null,
					function(data) 
					{
						$.endPageLoading();
						var resultCode = data.get("X_RESULTCODE");
						if(resultCode == '0')
							result = true;
					}, function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
						result = false ;
					},
					{async:false}
				);
				
	return result;
}