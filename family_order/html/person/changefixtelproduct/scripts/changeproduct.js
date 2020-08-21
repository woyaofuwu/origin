if(typeof(ChangeProduct)=="undefined"){window["ChangeProduct"]=function(){};var changeProduct = new ChangeProduct();}
(function(){
	$.extend(ChangeProduct.prototype,{
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
			$('#productSelectBtn').attr("disabled",false);
			$("#productSearch").attr("disabled",false);
			$.Search.get("productSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+userProductId+"&SEARCH_TYPE=1");
			$.Search.get("elementSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+userProductId+"&SEARCH_TYPE=2");
			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			$.ajax.submit(null,"loadChildInfo","&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode,null,changeProduct.afterLoadChildInfo);
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
//			pkgElementList.initElementList(null);
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
				offerList.renderComponent(nextProductId,eparchyCode);
				$.Search.get("elementSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+nextProductId+"&SEARCH_TYPE=2");
				$("#productSearch").attr("disabled",true);
			}
			else{
				offerList.renderComponent(userProductId,eparchyCode);
			}
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			nextProductArea.empty();
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			nextBrandArea.empty();
			$.insertHtml('beforeend',nextProductArea,productName);
			$.insertHtml('beforeend',nextBrandArea,brandName);
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());
//			pkgElementList.initElementList(null);
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			$("#NEW_PRODUCT_ID").val(productId);
			var param = "&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
			param += "&PRODUCT_ID="+productId;
			param += "&SEARCH_TYPE=2";
			$.Search.get("elementSearch").setParams(param);
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
			
			var data = selectedElements.getSubmitData();
			if(data&&data.length>0){
				var param = "&SELECTED_ELEMENTS="+data.toString()+"&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				if($("#EFFECT_NOW").attr("checked")){
					param+="&EFFECT_NOW=1";
				}
				param+="&GGCARD_NO="+$("#GGCARD_NO").val()+"&REMARK="+$("#REMARK").val();
				$.cssubmit.addParam(param);
				return true;
			}
			else{
				alert("您没有进行任何操作，不能提交");
			}
		},
		
		searchProductOptionEnter: function(data){
			var searchLi = $("#Ul_Search_productSearch li[class=focus]");
			//产品搜索
			var productId = searchLi.attr("PRODUCT_ID");
			var productName = searchLi.attr("PRODUCT_NAME");
			var brandCode = searchLi.attr("BRAND_CODE");
			var brandName = searchLi.attr("BRAND");
			changeProduct.afterChangeProduct(productId,productName,brandCode,brandName);
			$("#Div_Search_productSearch").css("visibility","hidden");
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

