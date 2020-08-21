(function(){
	if(window.createTDUser == undefined){
		function isNull(str){
			if(str==undefined || str==null || str=="") {
				return true;
			}
			return false;
		}
		function showLayer(optionID) {
			$("#" + optionID).css("display","");
		}
		function hideLayer(optionID) {
			$("#" + optionID).css("display","none");
		}
		window.createTDUser={
			init:function(){
			    debugger;
				$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
				$.custInfo.pushWidget(-1,"REMARK_PART");
				$.developStaff.init("10"); 
		    },
		    setRealNameValue:function(){
		    	var checked = $("#other_REAL_NAME")[0].checked;
		    	if(checked){
		    		$("#REAL_NAME").val("1");
		    	}else{
		    		$("#REAL_NAME").val("0");
		    	}
		    },
			checkSerialNumber:function(){
				//如果开户号码为空，或格式不正确，则返回
				if(!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
					return false;
				}
				$("#CHECK_RESULT_CODE").val("-1");	//设置号码未校验
			    var serialNumber = $("#SERIAL_NUMBER").val();
			    $.custInfo.setSerialNumber(serialNumber);
			    if(serialNumber.length != 11){
			    	alert("无线固话号码必须为11位");
			    	return false;
			    }
			    var param = '&SERIAL_NUMBER=' + serialNumber;
			    $.beginPageLoading("新开户号码校验中......");
			    $.ajax.submit(null,'checkSerialNumber',param,'CardPwdPart',function(data){
		    		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
			    	$("#CHECK_RESULT_CODE").val("0");//号码校验通过
			    	$.endPageLoading();
			    	//预配，预开处理密码因子和SIM卡信息
			    	var simCardNo =data.get(0).get("SIM_CARD_NO");
			    	if(!isNull(simCardNo)) {
			    		$("#SIM_CARD_INPUT").addClass("e_elements-success");
				    	$("#CHECK_RESULT_CODE").val("1");	//SIM卡校验通过
				    	//设置SIM卡返回信息
				    	$("#SIM_CARD_NO").val(simCardNo);
				    	$("#SIM_CARD_NO").attr("disabled", true);
				    	$("#IMSI").val(data.get(0).get("IMSI"));
				    	$("#KI").val(data.get(0).get("KI"));
				    	$("#RES_KIND_CODE").val(data.get(0).get("RES_KIND_CODE"));
				    	$("#RES_TYPE_CODE").val(data.get(0).get("RES_TYPE_CODE"));
				    	//密码因子和SIM卡信息
				    	createTDUser.processCardPwdCode();
			    	}
			    },function(error_code,error_info,derror){
					$.endPageLoading();
					$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
		    },
		    processCardPwdCode:function(){
		    	debugger;
		    	var cardPwd = $("#CARD_PASSWD").val();
		    	var passCode = $("#PASSCODE").val(); 
		    	if(!isNull(cardPwd)&&!isNull(passCode)){
		    		if(confirm('该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？')){
						$("#DEFAULT_PWD_FLAG").val("1");
						hideLayer("PasswdPart");
					}else{
					    $("#DEFAULT_PWD_FLAG").val("0");
						showLayer("PasswdPart");
					    }
		    	}else{
		    		$("#DEFAULT_PWD_FLAG").val("0");
					showLayer("PasswdPart");
		    	}
		    },
		    checkSimCardNo:function(){
		    	if($("#CHECK_RESULT_CODE").val()!="0"){
		    		alert("号码校验未通过！");
		    		return false;
		    	}
				if(!$.validate.verifyField($("#SIM_CARD_NO")[0])) {
					return false;
				}
			    var simCardNo = $("#SIM_CARD_NO").val();
			    var serialNumber = $("#SERIAL_NUMBER").val();
			    var param = '&SIM_CARD_NO=' + simCardNo + '&SERIAL_NUMBER=' + serialNumber;
			    $.beginPageLoading("SIM卡校验中......");
			    $.ajax.submit(null,'checkSimCardNo',param,'CardPwdPart',function(data){
		    		$("#SIM_CARD_INPUT").addClass("e_elements-success");
			    	$("#CHECK_RESULT_CODE").val("1");	//SIM卡校验通过
			    	//设置SIM卡返回信息
			    	$("#IMSI").val(data.get(0).get("IMSI"));
			    	$("#KI").val(data.get(0).get("KI"));
			    	$("#RES_KIND_CODE").val(data.get(0).get("RES_KIND_CODE"));
			    	$("#RES_TYPE_CODE").val(data.get(0).get("RES_TYPE_CODE"));
			    	$.endPageLoading();
			    	//处理密码卡
			    	createTDUser.processCardPwdCode();
			    },function(error_code,error_info,derror){
					$.endPageLoading();
					$("#SIM_CARD_INPUT").addClass("e_elements-error");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
		    },
		    changeSearchType:function(eventObj){
		    	var searchType = eventObj.value;
				var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
				param += "&SEARCH_TYPE="+searchType;
				if(searchType == "2"){
					param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
				}
				$.Search.get("productSearch").setParams(param);
		    },
		    searchOptionEnter:function(){
		    	var searchType = $("#productSearchType").val();
				var searchLi = $("#Ul_Search_productSearch li[class=focus]");
				if(searchType == "1"){
					//产品搜索
					var productId = searchLi.attr("PRODUCT_ID");
					var productName = searchLi.attr("PRODUCT_NAME");
					var brandCode = searchLi.attr("BRAND_CODE");
					var brandName = searchLi.attr("BRAND");
					createTDUser.afterChangeProduct(productId,productName,brandCode,brandName);
				}
				else if(searchType == "2"){
					//元素搜索
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
		    },
		    setBrandCode:function(){
		    	if($("#PRODUCT_TYPE_CODE").val()!=""){
		    	    $("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		    		createTDUser.initProduct();
		    	}else{
		    		$("#BRAND").val('');
		    	}
		    },
		    initProduct:function(){
		    	offerList.renderComponent("",$("#EPARCHY_CODE").val());
				selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
				$("#PRODUCT_NAME").val('');
		    },
		    checkBeforeProduct:function(){
		    	var checkResultCode = $("#CHECK_RESULT_CODE").val();
		    	if(checkResultCode=="-1"){
		    		alert("新开户号码校验未通过！");
		    		$("#SERIAL_NUMBER").focus();
		    		return false;
		    	}
		        if(!verifyAll('BaseInfoPart') || !verifyAll('CustInfoPart') || !verifyAll('AcctInfoPart')
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart'))
		        {
		    	   return false;
		        }
		        if($("#DEFAULT_PWD_FLAG").val()!="1"){
		    	    if(!verifyAll('PasswdPart')) {
		    		   return false;
		    	   } 
		        }
		        ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
		    },
		    afterChangeProduct:function(productId,productName,brandCode,brandName){
		    	$.feeMgr.clearFeeList("3820");
		        var feeData = $.DataMap();
		        $("#PRODUCT_ID").val(productId);
		        $("#PRODUCT_NAME").val(productName);
		        var param = "&NEW_PRODUCT_ID="+productId;
		        offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
				selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
				var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
				var length = selectedElements.selectedEls.length;
				for(var i=0;i<length;i++){
					var temp = selectedElements.selectedEls.get(i);
					if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="0" && temp.get("ELEMENT_TYPE_CODE")=="S")
					{
					  inparam+="&ELEMENT_ID=15";
					}
				}
			    $.beginPageLoading("产品费用查询中......");
				$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
					$.endPageLoading();
				    $.cssubmit.disabledSubmitBtn(false);
				  	for(var i = 0; i < data.getCount(); i++) {
				  	     var data0 = data.get(i);
					     if(data0){
								feeData.clear();
								feeData.put("MODE", data0.get("FEE_MODE"));
								feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
								feeData.put("FEE",  data0.get("FEE"));
								feeData.put("PAY",  data0.get("FEE"));		
								feeData.put("TRADE_TYPE_CODE","3820");							
								$.feeMgr.insertFee(feeData);			
							}
				  	}
				
			    },
			    function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
		    },
		    disableElements:function(data){
		    	if(data){
		    	    var temp = data.get(0);
		    	    if(data.get(0).get("NEW_PRODUCT_START_DATE")){
		    			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
		    		}
		    	}
		    },
		    passwdbeforeAction:function(){
		    	if($("#PSPT_TYPE_CODE").val()==""){
		    	      alert("证件类型不能为空！");
		    	      return false;
		    	}
		    	if($("#PSPT_ID").val()==""){
		    	      alert("证件号码不能为空！");
		    	      return false;
		    	}
		    	if($("#BIRTHDAY").val()==""){
		    	      alert("出生日期不能为空！");
		    	      return false;
		    	}
	    	    //将值赋给组件处理
	    	    var psptId =$("#PSPT_ID").val();
	    	    var serialNumber = $("#SERIAL_NUMBER").val();
	    	    $.password.setPasswordAttr(psptId, serialNumber);
	    	    return true ;
		    },
		    passwdafterAction:function(data){
		    	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
		    },
		    onSubmit:function(){
		    	if(!verifyAll('BaseInfoPart') || !verifyAll('CustInfoPart') || !verifyAll('AcctInfoPart')
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart'))
		        {
		    	   return false;
		        }
		    	if($("#DEFAULT_PWD_FLAG").val()!="1"){
		    	    if(!verifyAll('PasswdPart')) {
		    		   return false;
		    	   } 
		        }
		    	var data = selectedElements.getSubmitData();
		        var param = "&SELECTED_ELEMENTS="+data.toString();
		        $.cssubmit.addParam(param);
		        return true;
		    }
		}
	}
	$(createTDUser.init);
})();