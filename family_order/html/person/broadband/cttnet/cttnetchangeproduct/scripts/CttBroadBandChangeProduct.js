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
		afterSelectAction:function(package){
			var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var userProductId = $("#USER_PRODUCT_ID").val();
			var userId= $("#USER_ID").val();
			var araeCode = "";
			var tradeTypeCode = "9725";
			var param ="&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+tradeTypeCode+"&ARAE_CODE="+araeCode;
//			pkgElementList.renderComponent(package,eparchyCode,param);
		},
		getPackages: function(data){
			var prodId = $("#PRODUCT_ID").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var userId= $("#USER_ID").val();
			var param="&USER_ID="+userId;
			var userProductID = $("#USER_PRODUCT_ID").val();
			
			if(userProductID!=prodId){
				//判断。军区产品和其他产品不能互换
				if(userProductID == "50001004"){//军区产品编码
					alert("军区产品不允许变更为其它产品!");
					$("#PRODUCT_ID").val(userProductID);
					prodId = userProductID;
				}
				
				if(prodId == "50001004"){
					alert("其它产品不允许变更为军区产品!");
					$("#PRODUCT_ID").val(userProductID);
					prodId = userProductID;
				}
			
				param+="&USER_PRODUCT_ID="+userProductID+"&NEW_PRODUCT_ID="+prodId;
			}
			$("#NEW_PRODUCT_ID").val(prodId);
			offerList.renderComponent(prodId,eparchyCode);
			selectedElements.renderComponent(param,eparchyCode);
		},
		
		afterSubmitSerialNumber: function(data){
			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			$("#USER_PRODUCT_ID").val(userProductId);
			$("#USER_ID").val(userId);
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#NEW_PRODUCT_ID").val("");
			$("#NEXT_PRODUCT_ID").val("");
			$("#NEW_PRODUCT_START_DATE").val("");
			$("#OLD_PRODUCT_END_DATE").val("");
			
			$("#PROD_SPEC_TYPE").attr("disabled",false);
			
			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			$.ajax.submit(null,"loadChildInfo","&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode,null,changeProduct.afterLoadChildInfo);
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
			offerList.renderComponent("",eparchyCode);
		},
		
		afterLoadChildInfo: function(data){
			var userProductArea = $("#USER_PRODUCT_NAME");
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			var userBrandArea = $("#USER_BRAND_NAME");
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			userProductArea.empty();
			nextProductArea.empty();
			userBrandArea.empty();
			nextBrandArea.empty();
			var userProductId = data.get(0).get("USER_PRODUCT_ID");
			var nextProductId = data.get(0).get("NEXT_PRODUCT_ID");
			var nextProductStartDate = data.get(0).get("NEXT_PRODUCT_START_DATE");
			selectedElements.setEnvProductId(userProductId,nextProductId,nextProductStartDate);
			$("#NEXT_PRODUCT_ID").val(nextProductId);
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				$('#productSelectBtn').attr("disabled",true);
			}
			var eparchyCode = data.get(0).get("EPARCHY_CODE");
			$.insertHtml('beforeend',userProductArea,data.get(0).get("USER_PRODUCT_NAME"));
			$.insertHtml('beforeend',userBrandArea,data.get(0).get("USER_BRAND_NAME"));
			$.insertHtml('beforeend',nextProductArea,data.get(0).get("NEXT_PRODUCT_NAME"));
			$.insertHtml('beforeend',nextBrandArea,data.get(0).get("NEXT_BRAND_NAME"));
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				//packageList.renderComponent(nextProductId,eparchyCode);
			}
			else{
				//packageList.renderComponent(userProductId,eparchyCode);
			}
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			//var nextProductArea = $("#NEXT_PRODUCT_NAME");
			//nextProductArea.empty();
			//var nextBrandArea = $("#NEXT_BRAND_NAME");
			//nextBrandArea.empty();
			//$.insertHtml('beforeend',nextProductArea,productName);
			//$.insertHtml('beforeend',nextBrandArea,brandName);
			
			if(typeof(productId)!="undefined" || productId!=null || productId!=''){
				productId = $("#PRODUCT_ID").val();
			}
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());

			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			$("#NEW_PRODUCT_ID").val(productId);
			//var param = "&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
			//param += "&PRODUCT_ID="+productId;
			//param += "&SEARCH_TYPE=2";
			//$.Search.get("elementSearch").setParams(param);
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
		
		deleteElementCheckBox:function(elCheckBox){
			var length = selectedElements.selectedEls.length;
			if(length > 0){
				alert(elCheckBox);
				//var index = elCheckBox.parentNode.parentNode;
				//var index2  = elCheckBox.parentNode.parentNode.index;
					
			}
		
		},
		
		submit: function(){
			var canSubmit = selectedElements.checkForcePackage();
			if(!canSubmit){
				return false;
			}
			
			var productId = $("#PRODUCT_ID").val();
			var data = selectedElements.getSubmitData();
			var checkElement = selectedElements.selectedEls;
			var length = checkElement.length;
			var svcName = "";
			var discntName = "";
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
			if(data&&data.length>0){
				var param = "&CHECK_SELECTED_ELEMENTS="+checkElement.toString()+"&SELECTED_ELEMENTS="+data.toString()+"&NEW_PRODUCT_ID="+productId+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
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
		},
		
		displaySwitch:function(btn,o) {
			var button = $(btn);
			var div = $('#'+o);
			if (div.css('display') != "none")
			{
				div.css('display', 'none');
				button.children("i").attr('className', 'e_ico-unfold'); 
				button.children("span:first").text("显示客户信息");
			}
			else {
				div.css('display', '');
				button.children("i").attr('className', 'e_ico-fold'); 
				button.children("span:first").text("隐藏客户信息");
			}
		}					
	});
})();

