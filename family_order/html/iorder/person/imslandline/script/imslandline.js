var PAGE_FEE_LIST = $.DataMap();

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
		$("#BRAND").val(PRODUCT_TYPE_CODE.selectedText);
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

function afterChangeProduct(productId,productName,productDesc,brandCode){
     $("#PRODUCT_ID").val(productId);
     $("#PRODUCT_NAME").val(productName);
     
     $("#PRODUCT_NAME_DISPLAY").html(productName);
     $("#PRODUCT_DESC").html(productDesc);
     
     $("#SELECT_PRODUCT_BTN").attr("disabled", true).hide();  // 隐藏"产品目录"按钮
     $("#CHANGE_PRODUCT_BTN").attr("disabled", false).show(); // 展示"变更"按钮
     $("#PRODUCT_DISPLAY").show();                            // 展示已选产品
     if(productId == "84018059"){
    	 $("#RES_ID_CHECK").show();
     }else{
    	 $("#RES_ID_CHECK").hide();
     }
     
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
			MessageBox.alert('提示',"您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
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
			MessageBox.alert("提示",'该号码已经校验通过，不需要再次校验!');
			return false
		}
	}
	
	$.feeMgr.clearFeeList("6800");
	$.beginPageLoading("校验固话号码...");
	$.ajax.submit('', 'checkFixPhoneNum', "&FIX_NUMBER="+fixNumber+"&CITYCODE_RSRVSTR4="+cityCodeRsrvStr4, '', function(rtnData) {
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){
			if(rtnData.get("RESULT_CODE")=="1"){
				$("#OLD_FIX_NUMBER").val(fixNumber);
				MessageBox.alert("提示",rtnData.get("RESULT_INFO"));
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
				var beautifualTag =rtnData.get("BEAUTIFUAL_TAG");
		    	// 号码需要交纳的预存费用
		        if (rtnData.get("RESERVE_FEE")&&beautifualTag=="1") {
		        	$("#BEAUTIFUAL_TAG").val("1");
		            feeData = $.DataMap();
		            feeData.put("MODE",  "2");
		            feeData.put("CODE", "62"); // 选号预存收入
		            feeData.put("FEE", rtnData.get("RESERVE_FEE"));
		            feeData.put("PAY", rtnData.get("RESERVE_FEE"));
		            feeData.put("TRADE_TYPE_CODE", "6800");
		            feeData.put("SYSCHANGETDFEE", rtnData.get("SYSCHANGETDFEE")); 
		            feeData.put("TDBEAUTIFUALTAG", rtnData.get("TDBEAUTIFUALTAG")); 
		            $.feeMgr.insertFee(feeData);
		            //PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
		            PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.getFeeTrade());
		        }
			}else{
				MessageBox.alert("提示",rtnData.get("RESULT_INFO"));
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

/**
 * 校验终端
 */
function checkTerminal(){
	var productId=$("#PRODUCT_ID").val();
	var packageId=$("#MO_PACKAGE_ID").val();
	var topSetBoxSaleActiveList = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	if(null == productId || productId == ""){
		MessageBox.alert('提示',"请先选择IMS固话产品！");
		return false;
	}
	if(productId == "84018059" && (topSetBoxSaleActiveList == "" || null == topSetBoxSaleActiveList)){
		MessageBox.alert('提示',"营销活动未选，不需要校验串号，只输入即可！");
		return false;
	}
	var resID = $("#RES_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(resID == ""){
		$.TipBox.show(document.getElementById('RES_ID'), "请输入终端串码！", "red");
		return false;
	}
	
	if(resID != null){
		if((resID.length != 11 ) && (resID.length != 0)) {
			MessageBox.alert('提示',"串号必须为11位数字!");
			return false;
		}
	}
   
   if(!$.validate.verifyAll("FixPhonePart"))
	{
		return false;
	}
	var oldResId = $("#OLD_RES_ID").val();
	var resId=$("#RES_ID").val();
	
	if (null != oldResId && '' != oldResId)
	{
		if (oldResId == resId)
		{
			MessageBox.alert("提示",'该串号已经校验通过，不需要再次校验!');
			return false
		}
	}
	$.feeMgr.clearFeeList("6800");
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('', 'checkTerminal', "&RES_ID="+resID+"&SERIAL_NUMBER="+serialNumber, '', function(data) {
				$.endPageLoading();
				if(data!=null&&data.length > 0){
					if(data.get("X_RESULTCODE")=="0"){
						$("#OLD_RES_ID").val(resId);
						var fee = data.get("RES_FEE");
						var feeData = $.DataMap();
						feeData.put("MODE", "0");	 	 	 
						feeData.put("CODE", "60");	 	 	 
						feeData.put("FEE",  fee);	 	 	 
						feeData.put("PAY",  fee);           	 	 	 
						feeData.put("TRADE_TYPE_CODE","6800");                           	 	 	 
						$.feeMgr.insertFee(feeData);
						MessageBox.alert("提示",'OK!');
					}
				}
			}, function(error_code, error_info, derror) {
				$("#OLD_RES_ID").val("");
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/** 用戶认证结束之后执行的js方法 */
function refreshPartAtferAuth(data){
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'widenetInfoPart,FixPhonePart,productDisplayPart',
			function(data) {
				$.endPageLoading();
				var resultCode=data.get("RESULT_CODE");
				if(resultCode=="1"){
					$("#FIX_NUMBER").attr("disabled",false);
					$("#CHECK_BTN").attr("disabled",false);
					$("#RES_ID").attr("disabled",false);
					$("#CHECK_BTN_RES_ID").attr("disabled",false);
					MessageBox.alert('提示',"校验通过，请录入固话号码，点击【校验】按钮。");
					$("#FIX_NUMBER").focus();
					$.cssubmit.disabledSubmitBtn(true,"submitButton");
					setBrandCode();
				}else{
					var resultInfo=data.get("RESULT_INFO");
					MessageBox.alert('提示',"手机号码校验不通过："+resultInfo);
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
    //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
    // 人像比对
    var agentPicIdObj = $("#AGENT_PIC_ID");
    if (agentPicIdObj.val() === "") {
        agentPicIdObj.val("AGENT_PIC_ID_value");
    }

    if(!$.validate.verifyAll('CustInfoFieldPart'))
    {
        return false;
    }

    var birthdayObj = $("#BIRTHDAY");
    if (birthdayObj.val() === "") {
        birthdayObj.val("1900-01-01");
    }
    /**
     * 人像比对
     * AGENT_PIC_ID_value这是界面默认的值
     * 当没有对经办人人像摄像时，就把AGENT_PIC_ID置空
     */
    if (agentPicIdObj.val() === "AGENT_PIC_ID_value") {
        agentPicIdObj.val("");
    }

    var cmpTag = "1";
    $.ajax.submit(null, "isCmpPic", null, null,
        function (ajaxData) {
            var flag = ajaxData.get("CMPTAG");
            if (flag === "0") {
                cmpTag = "0";
            }
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.MessageBox.error(error_code, error_info);
            $.endPageLoading();
        }, {
            "async": false
        });

    if (cmpTag === "0") {
        var picId = $("#PIC_ID").val();
        if (picId != null && picId === "ERROR") { // 客户摄像失败
            MessageBox.error("告警提示", "客户" + $("#PIC_STREAM").val());
            return false;
        }

        var psptTypeCode = $("#PSPT_TYPE_CODE").val();
        var agentPicId = agentPicIdObj.val();
        var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();

        if ((psptTypeCode === "0" || psptTypeCode === "1")
            && picId === "") {
            /**
             * REQ201705270006_关于人像比对业务优化需求
             * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。
             * @author zhuoyingzhi
             * @date 20170620
             */
            var custName = $("#AGENT_CUST_NAME").val();      // 经办人名称
            var psptId = $("#AGENT_PSPT_ID").val();          // 经办人证件号码
            var agentPsptAddr = $("#AGENT_PSPT_ADDR").val(); // 经办人证件地址

            if (agentTypeCode === "" && custName === "" && psptId === ""
                && agentPsptAddr === "") {
                MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                return false;
            }
            if ((agentTypeCode === "0" || agentTypeCode === "1")
                && agentPicId === "") {
                MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                return false;
            }
        }

        if (agentPicId != null && agentPicId === "ERROR") { // 经办人摄像失败
            MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
            return false;
        }

        if ((agentTypeCode === "0" || agentTypeCode === "1")
            && agentPicId === "") { // 经办人未摄像
            MessageBox.error("告警提示", "请进行经办人摄像！");
            return false;
        }

        if (psptTypeCode === "2" && picId === "") { // 未进行客户摄像
            var custPsptId = $("#PSPT_ID").val();   // 客户证件号码
            if (custPsptId !== ""
                && checkCustAge(custPsptId, psptTypeCode)) {
                // 11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）
                MessageBox.error("告警提示", "请进行客户摄像!");
                return false;
            }
        }

        var param = "&PIC_ID=" + picId + "&AGENT_PIC_ID=" + agentPicId;
        $.cssubmit.addParam(param);
    }
    //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx

	var productId=$("#PRODUCT_ID").val();
	var topSetBoxSaleActiveList = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	if(!productId || productId == ""){
		MessageBox.alert('提示',"IMS固话产品不能为空！");
		return false;
	}
	if(productId == "84018059"){
		var feeFlag = checkZnyxFee(productId);
		if(!feeFlag)
		{
			return false ;
		}
		var oldResId = $("#OLD_RES_ID").val();
		var resId=$("#RES_ID").val();
		if(resId == ""){
			MessageBox.alert('提示',"该固话产品为和家固话（智能音箱版）时，请输入音箱串号并校验！");
			return false;
		}else{
			if(resId != oldResId && topSetBoxSaleActiveList == "18059"){
				MessageBox.alert('提示',"串号校验不通过，请重新校验！");
				return false;
			}
		}
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
	var numberFee,totalFee;
	var tradeFeeSubs=$.feeMgr.getFeeTrade().get("X_TRADE_FEESUB");
	
	if(PAGE_FEE_LIST.get("NUMBER_FEE")!=null&&PAGE_FEE_LIST.get("NUMBER_FEE").length > 0){
		for(var i=0;i<tradeFeeSubs.length;i++){
			if(tradeFeeSubs.get(i).get("FEE_TYPE_CODE")=="62"){
				numberFee=tradeFeeSubs.get(i).get("FEE");
				totalFee = parseFloat(topSetBoxSaleActiveFee)/100+parseFloat(numberFee)/100;
			}
		}
	}else{
		totalFee = parseFloat(topSetBoxSaleActiveFee)/100;
	}	
	if (totalFee > 0)
	{
		if ($("#BEAUTIFUAL_TAG").val() == "1") {
			feeFlag = false ;
			var tips = "您正在办理IMS家庭固话吉祥号码开户，后台将为您绑定约定消费的营销活动，总共需要支付："+totalFee
				 	+ "元，家庭IMS固话活动预存："+parseFloat(topSetBoxSaleActiveFee)/100
				 	+ "元，吉祥号码预存："+parseFloat(numberFee)/100+"元，请确认您的余额是否足够？";

			MessageBox.confirm("告警提示",tips,function(re){
				if(re=="ok"){
					feeFlag = checkFeeBeforeSubmit();
					if(!feeFlag)
					{
						return false ;
					}
					
					var data = selectedElements.getSubmitData();
					var param = "&SELECTED_ELEMENTS="+data.toString()+"&PRODUCT_ID="+$("#PRODUCT_ID").val()+"&TOP_SET_BOX_SALE_ACTIVE_FEE="+$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();
				}
			});
		}else{
			feeFlag = false ;
			var tips = "您总共需要转出："+totalFee
				 	+ "元，家庭IMS固话活动预存："+parseFloat(topSetBoxSaleActiveFee)/100
				 	+ "元，请确认您的余额是否足够？";

			MessageBox.confirm("告警提示",tips,function(re){
				if(re=="ok"){
					feeFlag = checkFeeBeforeSubmit();
					if(!feeFlag)
					{
						return false ;
					}
					
					var data = selectedElements.getSubmitData();
					var param = "&SELECTED_ELEMENTS="+data.toString()+"&PRODUCT_ID="+$("#PRODUCT_ID").val()+"&TOP_SET_BOX_SALE_ACTIVE_FEE="+$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();
				}
			});
		}
	}else{
		if(!feeFlag)
		{
			return false ;
		}
		
		var data = selectedElements.getSubmitData();
		var param = "&SELECTED_ELEMENTS="+data.toString()+"&PRODUCT_ID="+$("#PRODUCT_ID").val()+"&TOP_SET_BOX_SALE_ACTIVE_FEE="+$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
		$.cssubmit.addParam(param);
		return true;
	}
				
    
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
				if (topSetBoxSaleActiveId == moSaleActiveDataset.get(i).get('PARA_CODE2'))
				{
					topSetBoxSaleActiveProductId = moSaleActiveDataset.get(i).get("PARA_CODE4");
					topSetBoxSaleActivePackageId = moSaleActiveDataset.get(i).get("PARA_CODE5");
					topSetBoxSaleActiveExplain = moSaleActiveDataset.get(i).get("PARA_CODE24");
					ruleTag=moSaleActiveDataset.get(i).get("PARA_CODE22");
					depProdIds=moSaleActiveDataset.get(i).get("PARA_CODE23");
				}
			}
		}

		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
		var userId =  $.auth.getAuthData().get('USER_INFO').get('USER_ID');
		var inparam = "&ROUTE_EPARCHY_CODE=" + eparchyCode 
					+ "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
					+ "&PRODUCT_ID=" + topSetBoxSaleActiveProductId
					+ "&PACKAGE_ID=" + topSetBoxSaleActivePackageId
					+ "&RULE_TAG=" + ruleTag
					+ "&DEP_PRODUCT_ID=" + depProdIds
					+ "&USER_ID=" + userId;
					
		$.beginPageLoading("IMS营销活动校验中。。。");

		$.ajax.submit(null, 'checkSaleActive', inparam, null,
						function(data) 
						{
							$.endPageLoading();
							$("#MO_PRODUCT_ID").val(topSetBoxSaleActiveProductId);
							$("#MO_PACKAGE_ID").val(topSetBoxSaleActivePackageId);
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val(topSetBoxSaleActiveExplain);
							$("#SALE_ACTIVE_EXPLAIN_DIV").attr("title",topSetBoxSaleActiveExplain);
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

//校验办理智能音箱套餐时，手机号码的余额是否小于9元
function checkZnyxFee(productId)
{   
	var result = false;
	var param = "&PRODUCT_ID="+productId
				+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				
	$.beginPageLoading("校验手机余额。。。");			
	$.ajax.submit(null, 'checkZnyxFee', param, null,
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

//点击展示，隐藏产商品信息按钮
function displayOfferList(obj){
	var div = $('#'+obj);
	if($("#showCheckbox").attr('checked')){
		div.css('display', '');
	}
	else{
		div.css('display', 'none');
	}
}

function setTTtransferValue(){
	var checked = $("#is_TT_TRANSFER")[0].checked;
	if(checked){
		$("#TT_TRANSFER").val("1");
	}else{
		$("#TT_TRANSFER").val("0");
	}
}
