if(typeof(ChangeProduct)=="undefined"){window["ChangeProduct"]=function(){};var changeProduct = new ChangeProduct();}
(function(){
	$.extend(ChangeProduct.prototype,{
		
		getProductBySpec: function(data){
			var prodSpec = $("#PROD_SPEC_TYPE").val();
			$.beginPageLoading("产品加载中.....");
			$.ajax.submit(this, 'getProductBySpec', "&PROD_SPEC_TYPE=" + prodSpec, 'productListPart', function() {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});
			
		},
		getPackages: function(data){
			var prodId = $("#PRODUCT_ID").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			
			offerList.renderComponent(prodId,eparchyCode);
		},
		
		
		afterSubmitSerialNumber: function(data){
			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#USER_PRODUCT_ID").val(userProductId);
			$("#USER_ID").val(userId);


			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			var loadParam = "&USER_ID="+userId+"&SERIAL_NUMBER="+serialNumber+"&ROUTE_EPARCHY_CODE="+eparchyCode;
			$.ajax.submit(null,"loadChildInfo",loadParam,'cttNetViewPart',function() {
				var param="&USER_ID="+userId;
				selectedElements.renderComponent(param,eparchyCode);
				offerList.renderComponent(userProductId,eparchyCode);
			
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
				
			});
			
			
			
		},
		
		afterLoadChildInfo: function(data){
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			if(typeof(productId)!="undefined" || productId!=null || productId!=''){
				productId = $("#PRODUCT_ID").val();
			}
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			$("#NEW_PRODUCT_ID").val(productId);
		},
		
		afterRenderSelectedElements: function(data){
			if(data){
				var temp = data.get(0);
				if(temp.get("OLD_PRODUCT_END_DATE")){
					$("#OLD_PRODUCT_END_DATE").val(temp.get("OLD_PRODUCT_END_DATE"));
				}
				if(data.get(0).get("NEW_PRODUCT_START_DATE")){
					$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
				}
				if(temp.get("EFFECT_NOW_DISABLED")=="false"){
					$("#EFFECT_NOW").attr("disabled","");
				}
				else{
					$("#EFFECT_NOW").attr("disabled",true);
				}
				if(temp.get("EFFECT_NOW_CHECKED")=="true"){
					$("#EFFECT_NOW").attr("checked",true);
					$("#EFFECT_NOW").trigger("click");
				}
				else{
					$("#EFFECT_NOW").attr("checked","");
				}
			}
		},
		
		
		
		submit: function(){
			var canSubmit = selectedElements.checkForcePackage();
			if(!canSubmit){
				return false;
			}
			
			var checkElement = selectedElements.selectedEls;
			var length = checkElement.length;
			var svcCount = 0;
			var discntCount = 0;
			for(var i=0;i<length;i++){
				var element = checkElement.get(i);
				var elementType = element.get("ELEMENT_TYPE_CODE");
				var elementId = element.get("ELEMENT_ID");
				var modifyTag = element.get("MODIFY_TAG");
				if(element.get("MODIFY_TAG")=="0" && "D" == elementType){
					discntCount++;
				}
				if("501" != elementId && "S" == elementType && element.get("MODIFY_TAG")!="1"&& element.get("MODIFY_TAG")!="0_1"){
					svcCount++;
					
				}
			}
			if(discntCount < 1){
				alert("请选择一个优惠!");
				return false;		
			}
			if(discntCount > 1){
				alert("只能选择一个优惠!");
				return false;		
			}
			if(svcCount < 1){
				alert("请选择一个服务!");
				return false;		
			}
			
			if(svcCount > 1){
				alert("只能选择一个可选服务!");
				return false;		
			}
			
			
			
			var productId = $("#PRODUCT_ID").val();
			var data = selectedElements.getSubmitData();
			if(data&&data.length>0){
				var param = "&SELECTED_ELEMENTS="+data.toString()+"&NEW_PRODUCT_ID="+productId+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				if($("#EFFECT_NOW").attr("checked")){
					param+="&EFFECT_NOW=1";
				}
				param+="&REMARK="+$("#REMARK").val();
				$.cssubmit.addParam(param);
				return true;
			}
			else{
				alert("您没有进行任何操作，不能提交");
			}  
		},
		
		
		searchElementOptionEnter:function(data){
			//元素搜索
			var searchLi = $("#Ul_Search_elementSearch li[class=focus]");
			var reOrder = searchLi.attr("REORDER");
			var elementId = searchLi.attr("ELEMENT_ID");
			var elementName = searchLi.attr("ELEMENT_NAME");
			var productId = searchLi.attr("PRODUCT_ID");
			var packageId = searchLi.attr("PACKAGE_ID");
			var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
			var forceTag = searchLi.attr("FORCE_TAG");
			
			if(reOrder!="R"&&reOrder!="C"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
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
			$("#Div_Search_elementSearch").css("visibility","hidden");
		}
						
	});
})();










