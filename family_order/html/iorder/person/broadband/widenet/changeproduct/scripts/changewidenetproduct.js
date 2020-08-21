

if(typeof(ChangeWidenetProduct)=="undefined"){window["ChangeWidenetProduct"]=function(){};var changeWidenetProduct = new ChangeWidenetProduct();}
(function(){
	$.extend(ChangeWidenetProduct.prototype,{
		afterSubmitSerialNumber: function(data){

			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			/*
			var openDate = data.get("USER_INFO").get("OPEN_DATE");
			var custName = data.get("CUST_INFO").get("CUST_NAME");
			alert(custName);
			$("#CUST_NAME").val(custName);
			$("#OPEN_DATE").val(openDate);
			*/
	
			$("#USER_PRODUCT_ID").val(userProductId);
			$("#USER_ID").val(userId);
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#NEW_PRODUCT_ID").val("");
			$("#NEXT_PRODUCT_ID").val("");
			$("#NEW_PRODUCT_START_DATE").val("");
			$("#OLD_PRODUCT_END_DATE").val("");
			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			$.ajax.submit(null,"loadChildInfo","&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val(),'productTypePart',changeWidenetProduct.afterLoadChildInfo);
			
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
			offerList.renderComponent(userProductId,eparchyCode);		},
		
		afterLoadChildInfo: function(data){
			
			var userProductArea = $("#USER_PRODUCT_NAME");
			var userProductDateArea = $("#USER_PRODUCT_DATE");
			
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			var nextProductDateArea = $("#NEXT_PRODUCT_DATE");
			
			userProductArea.empty();
			nextProductArea.empty();
			userProductDateArea.empty();
			nextProductDateArea.empty();
			
			var userProductId = data.get(0).get("USER_PRODUCT_ID");
			var userProductName = data.get(0).get("USER_PRODUCT_NAME");
			var userProductStartDate = data.get(0).get("USER_PRODUCT_START_DATE");
			var userProductEndDate = data.get(0).get("USER_PRODUCT_END_DATE");
			
			var nextProductId = data.get(0).get("NEXT_PRODUCT_ID");
			var nextProductName = data.get(0).get("NEXT_PRODUCT_NAME");
			var nextProductStartDate = data.get(0).get("NEXT_PRODUCT_START_DATE");
			var nextProductEndDate = data.get(0).get("NEXT_PRODUCT_END_DATE");
			
			selectedElements.setEnvProductId(userProductId,nextProductId,nextProductStartDate);
			$("#NEXT_PRODUCT_ID").val(nextProductId);
			$("#PRODUCT_ID").val(userProductId);
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				$('#productSelectBtn').attr("disabled",true);
			}
			var eparchyCode = data.get(0).get("EPARCHY_CODE");
			
			if(typeof(userProductName)!="undefined"&&userProductName!=null&&userProductName!=''){
				$("#OLD_PRODUCT").css("display","")
				$.insertHtml('beforeend',userProductArea,userProductName);
				$.insertHtml('beforeend',userProductDateArea,userProductStartDate+" ~ "+userProductEndDate);
			}else{
				$("#OLD_PRODUCT").css("display","none")
			}
			
			if(typeof(nextProductName)!="undefined"&&nextProductName!=null&&nextProductName!=''){
				$("#NEW_PRODUCT").css("display","")
				$.insertHtml('beforeend',nextProductArea,nextProductName);
				$.insertHtml('beforeend',nextProductDateArea,nextProductStartDate+" ~ "+nextProductEndDate);
			}else{
				$("#NEW_PRODUCT_ITEM").css("display","none")
			}
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				offerList.renderComponent(nextProductId,eparchyCode);
			}
			else{
				offerList.renderComponent(userProductId,eparchyCode);
			}
			$("#UCAViewPart").css("display","");
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
		    var productId = $("#PRODUCT_ID").val();
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			$("#NEW_PRODUCT_ID").val(productId);
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			
			if($("#USER_PRODUCT_ID").val() != productId){
				$("#NEW_PRODUCT").css("display","");
				var productName = PRODUCT_ID.selectedText;
				var nextProductArea = $("#NEXT_PRODUCT_NAME");
				nextProductArea.empty();
				$.insertHtml('beforeend',nextProductArea,productName);
				
			}else{
				$("#NEW_PRODUCT").css("display","none");
				var nextProductArea = $("#NEXT_PRODUCT_NAME");
				nextProductArea.empty();
				var nextProductDateArea = $("#NEXT_PRODUCT_DATE");
				nextProductDateArea.empty();
			}
		},
		
		searchElementOptionEnter:function(){
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
				MessageBox.alert("提示","您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
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
		
		afterRenderSelectedElements: function(data){
			if(data){
				var temp = data.get(0);
				if(temp.get("OLD_PRODUCT_END_DATE")){
					$("#OLD_PRODUCT_END_DATE").val(temp.get("OLD_PRODUCT_END_DATE"));
				}
				if(data.get(0).get("NEW_PRODUCT_START_DATE")){
					$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
					var nextProductDateArea = $("#NEXT_PRODUCT_DATE");
					nextProductDateArea.empty();
					$.insertHtml('beforeend',nextProductDateArea,$("#NEW_PRODUCT_START_DATE").val());
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
				$.cssubmit.addParam(param);
				return true;
			}
			else{
				MessageBox.alert("提示","您没有进行任何操作，不能提交");
			}
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

