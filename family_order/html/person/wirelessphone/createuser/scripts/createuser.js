(function(){
	if(window.createUser == undefined){
		
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
		
		window.createUser={
			init:function(){
			    debugger;
				$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
				$.custInfo.pushWidget(-1,"REMARK_PART");
				$.developStaff.init("10"); 
		    },
		    selectDevice:function(){
		    	var deviceKind = $("#SEL_DEVICE_KIND").val();
		    	//test-
		    	$.popupPage('brm.materialChoose', 'queryMaterialList', '&RES_TYPE_CODE=W&refresh=true&RES_KIND_CODE='+deviceKind, '选择设备', '620', '400','CHOOSEDEVICE','',subsys_cfg.pbossintf);
		    	//dev-$.popupPage('brm.materialChoose', 'queryMaterialList', '&RES_TYPE_CODE=W&refresh=true&RES_KIND_CODE='+deviceKind, '选择设备', '620', '400','CHOOSEDEVICE','','pbossintf');
		    },
		    afterSelectDevice:function(){
		    	debugger;
		    	var deviceKindCode = $("#SEL_DEVICE_KIND").val();
		    	var deviceKind = $("#SEL_DEVICE_KIND").text();
		    	var useTypeCode = $("#USE_TYPE_CODE").val();
		    	var useType = $("#USE_TYPE_CODE").text();
		    	//界面返回值
		    	var saleTypeDesc = $("#SALETYPE_DESC").val();
		    	var saleTypeCode = $("#SALETYPE_CODE").val();
		    	var salePrice    = $("#SALEPRICE").val();
		    	var materialCode = $("#MATERIAL_CODE").val(); //物品编号
		    	if(isNull(materialCode)){
		    		alert("串号不能为空");
		    		return false;
		    	}
		    	var deviceTable = $.tableManager.get("DeviceList");
		    	//不能重复添加
		    	if(deviceTable){
		    		for(var i=0;i<deviceTable.count();i++){
		    			var mCode = deviceTable.getValue(i,"MATERIAL_CODE");
		    			if(mCode == materialCode){
		    				alert("该串号【"+materialCode+"】已经添加");
		    				return false;
		    			}
		    		}
		    	}
		    	var index = deviceTable.newRow();
		    	deviceTable.setAllSelectCheckBoxSts(false);
		    	deviceTable.rowSelected(index,true);
		    	deviceTable.setValue(index,"MATERIAL_CODE", materialCode);
		    	deviceTable.setValue(index,"DEVICE_KIND_CODE", deviceKindCode);
		    	deviceTable.setValue(index,"DEVICE_KIND", deviceKind);
		    	deviceTable.setValue(index,"SALE_TYPE_CODE", saleTypeCode);
		    	deviceTable.setValue(index,"SALE_TYPE", saleTypeDesc);
		    	deviceTable.setValue(index,"SALE_PRICE", salePrice/100);
		    	deviceTable.setValue(index,"USE_TYPE_CODE", useTypeCode);
		    	deviceTable.setValue(index,"USE_TYPE", useType);
		    	var deviceStr = deviceTable.toJsonArray(false);
		    	$("#Device_Str").val(deviceStr);
		    	//费用FEE_TYPE_CODE='9' 暂时不用 待确认
		    },
		    deleteDevice:function(rowIndex,isSelect){
		    	if(!isSelect){
		    		var deviceTable = $.tableManager.get("DeviceList");
		    		deviceTable.deleteRow(rowIndex);
		    		var deviceStr = deviceTable.toJsonArray(false);
		    		$("#Device_Str").val(deviceStr);
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
			    var param = '&SERIAL_NUMBER=' + serialNumber;
			    $.beginPageLoading("新开户号码校验中......");
			    $.ajax.submit(null,'checkSerialNumber',param,'',function(data){
			    	var data0 = data.get(0);
			    	var simCardNo =data0.get("SIM_CARD_NO");
			    	var serialNumberBind =data0.get("SERIAL_NUMBER_BIND");
			    	if(!isNull(simCardNo) && !isNull(serialNumberBind)){
			    		$("#SERIAL_NUMBER_BIND").val(serialNumberBind);
			    		$("#SIM_CARD_NO").val(simCardNo);
			    		$("#IMSI").val(data0.get("IMSI"));
			    		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
			    	}
			    	$("#CHECK_RESULT_CODE").val("0");//号码校验通过
			    	$.endPageLoading();
			    },function(error_code,error_info,derror){
					$.endPageLoading();
					$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
		    },
		    modifyFee:function(){
				var tempFee = $("#TEMP_PAY_MONEY").val();
				if(!$.toollib.isNumber(tempFee)){
					alert("缴款金额必须为整数！");
					$("#TEMP_PAY_MONEY").val("");
					return false;
				}
				//清理费用
				$.feeMgr.clearFeeList("9721","2");
				//新增修改费用
				var feeData = $.DataMap();
				feeData.put("MODE", "2");
				feeData.put("CODE", "0");
				feeData.put("FEE",  tempFee*100);
				feeData.put("PAY",  tempFee*100);		
				feeData.put("TRADE_TYPE_CODE","9721");							
				$.feeMgr.insertFee(feeData);
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
					afterChangeProduct(productId,productName,brandCode,brandName);
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
		    		createUser.initProduct();
		    	}else{
		    		$("#BRAND").val('');
		    	}
		    },
		    initProduct:function(){
		    	offerList.renderComponent("",$("#EPARCHY_CODE").val());
//	            pkgElementList.initElementList(null);
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
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart') || !verifyAll('PasswdPart'))
		        {
		    	   return false;
		        }
		        ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
		    },
		    afterChangeProduct:function(productId,productName,brandCode,brandName){
		    	$.feeMgr.clearFeeList("9721");
		        var feeData = $.DataMap();
		        $("#PRODUCT_ID").val(productId);
		        $("#PRODUCT_NAME").val(productName);
		        var param = "&NEW_PRODUCT_ID="+productId;
		        offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
//		        pkgElementList.initElementList(null);
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
				$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
				    $.cssubmit.disabledSubmitBtn(false);
				    //实际只有一条费用记录
				  	for(var i = 0; i < data.getCount(); i++) {
				  	     var data0 = data.get(i);
					     if(data0){
								feeData.clear();
								feeData.put("MODE", data0.get("FEE_MODE"));
								feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
								feeData.put("FEE",  data0.get("FEE"));
								feeData.put("PAY",  data0.get("FEE"));		
								feeData.put("TRADE_TYPE_CODE","9721");							
								$.feeMgr.insertFee(feeData);
								$("#TEMP_PAY_MONEY").val(data0.get("FEE")/100);
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
		    checkGiftDevice:function(obj){
		    	var isChecked = $(obj).attr("checked");
		    	if(isChecked){
		    		showLayer("DeviceListPart");
		    	}else{
		    		hideLayer("DeviceListPart");
		    	}
		    },
		    onSubmit:function(){
		    	if(!verifyAll('BaseInfoPart') || !verifyAll('CustInfoPart') || !verifyAll('AcctInfoPart')
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart') || !verifyAll('PasswdPart'))
		        {
		    	   return false;
		        }
		    	var data = selectedElements.getSubmitData();
		        var param = "&SELECTED_ELEMENTS="+data.toString();
		        $.cssubmit.addParam(param);
		        return true;
		    }
		}
	}
	$(createUser.init);
})();