if(typeof(ChangeProduct)=="undefined"){window["ChangeProduct"]=function(){};var changeProduct = new ChangeProduct();}
(function(){
	$.extend(ChangeProduct.prototype,{
		afterSubmitSerialNumber: function(data){
			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var custId = data.get("CUST_INFO").get("CUST_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			
			
			$("#PSPT_TYPE_CODE").val(data.get("CUST_INFO").get("PSPT_TYPE_CODE"));
		   
			
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
			
			$("#SHAREMEALONE_NAME").val("");
			$("#SHAREMEALTWO_NAME").val("");
			$("#SHAREMEAL_NAME").val(0);
			$("#SHAREMEALONE").css("display","none");
			$("#SHAREMEALTWO").css("display","none");
			$("#SHAREMEAL_PHONE_CHECK").css("display","none");
			$("#CHECK_BTN").attr("disabled",true);
			
			
			//海南添加
			if(typeof(changeProductExtend) != "undefined" && changeProductExtend.afterSubmitSerialNumber)
			{
				changeProductExtend.afterSubmitSerialNumber(data);
			}
			
			$.Search.get("productSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+userProductId+"&SEARCH_TYPE=1");
			$.Search.get("elementSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+userProductId+"&SEARCH_TYPE=2");
			//selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			$.ajax.submit(null,"loadChildInfo","&USER_ID="+userId+"&CUST_ID="+custId+"&ROUTE_EPARCHY_CODE="+eparchyCode,null,changeProduct.afterLoadChildInfo);
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
			//pkgElementList.initElementList(null);
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
			//selectedElements.setEnvProductId(userProductId,nextProductId,nextProductStartDate);
			$("#NEXT_PRODUCT_ID").val(nextProductId);
			
			var eparchyCode = data.get(0).get("EPARCHY_CODE");
			$("#USER_PRODUCT_NAME").val(data.get(0).get("USER_PRODUCT_NAME"));
			$.insertHtml('beforeend',userProductArea,data.get(0).get("USER_PRODUCT_NAME"));
			$.insertHtml('beforeend',userBrandArea,data.get(0).get("USER_BRAND_NAME"));
			$.insertHtml('beforeend',nextProductArea,data.get(0).get("NEXT_PRODUCT_NAME"));
			$.insertHtml('beforeend',nextBrandArea,data.get(0).get("NEXT_BRAND_NAME"));
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				offerList.renderComponent(nextProductId,eparchyCode);
				$.Search.get("elementSearch").setParams("&EPARCHY_CODE="+eparchyCode+"&PRODUCT_ID="+nextProductId+"&SEARCH_TYPE=2");
				$("#productSearch").attr("disabled",true);
				$('#productSelectBtn').attr("disabled",true);
			}
			else{
				offerList.renderComponent(userProductId,eparchyCode);
			}
			
			//海南添加
			if(typeof(changeProductExtend) != "undefined" && changeProductExtend.afterLoadChildInfo)
			{
				changeProductExtend.afterLoadChildInfo(data);
			}
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			nextProductArea.empty();
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			nextBrandArea.empty();
			$.insertHtml('beforeend',nextProductArea,productName);
			$.insertHtml('beforeend',nextBrandArea,brandName);
			$("#NEW_PRODUCT_NAME").val(productName);
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			$("#NEW_PRODUCT_ID").val(productId);
			var param = "&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
			param += "&PRODUCT_ID="+productId;
			param += "&SEARCH_TYPE=2";
			$.Search.get("elementSearch").setParams(param);
			
			if("80003014"==productId)
			{
				$("#SHAREMEALONE_NAME").val("");
				$("#SHAREMEALTWO_NAME").val("");
				$("#SHAREMEAL_NAME").val(0);
				$("#SHAREMEALONE").css("display","");
				$("#SHAREMEALTWO").css("display","");
				$("#SHAREMEAL_PHONE_CHECK").css("display","");
				$("#CHECK_BTN").attr("disabled",false);
			}else{
				$("#SHAREMEALONE_NAME").val("");
				$("#SHAREMEALTWO_NAME").val("");
				$("#SHAREMEAL_NAME").val(0);
				$("#SHAREMEALONE").css("display","none");
				$("#SHAREMEALTWO").css("display","none");
				$("#SHAREMEAL_PHONE_CHECK").css("display","none");
				$("#CHECK_BTN").attr("disabled",true);
			}
			
			if("80003014"==$("#USER_PRODUCT_ID").val())
			{
				alert("您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。");
			}
			
			//海南添加
			if(typeof(changeProductExtend) != "undefined" && changeProductExtend.afterChangeProduct)
			{
				changeProductExtend.afterChangeProduct(productId);
			}
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
		
		checkSharePhone: function(){
			var shareMealOne =  $("#SHAREMEALONE").css("display");
			var shareMealTwo =  $("#SHAREMEALTWO").css("display");
			
			if(shareMealOne != "undefined" && shareMealOne != "none")
			{
				var shareMealOneValue = $("#SHAREMEALONE_NAME").val();
				var shareMealTwoValue = $("#SHAREMEALTWO_NAME").val();

				if('' == shareMealOneValue || null == shareMealOneValue)
				{
					alert("请正确填写共享副号一");
					return;
				}
				if('' == shareMealTwoValue || null == shareMealTwoValue)
				{
					alert("请正确填写共享副号二");
					return;
				}
				if(shareMealOneValue == shareMealTwoValue)
				{
					alert("填写共享副号不能一样");
					return;
				}
			}
			
			$.beginPageLoading("校验共享号码...");
			$.ajax.submit('', 'checkShareMealPhoneNum', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&SHAREMEALTWO="+shareMealTwoValue+"&SHAREMEALONE="+shareMealOneValue+"&BOOKING_DATE="+$("#BOOKING_DATE").val(), '', function(rtnData) { 
				$.endPageLoading();
				if(rtnData!=null&&rtnData.length > 0){
					$("#SHAREMEAL_NAME").val(1);
					alert("校验通过");
				}
			}, function(error_code, error_info,detail) {
				$("#SHAREMEAL_NAME").val(0);
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, null, null, null, detail);
			}); 

			$.endPageLoading();
		},
		
		
		submit: function(){
			
			var shareMealOne =  $("#SHAREMEALONE").css("display");
			var shareMealTwo =  $("#SHAREMEALTWO").css("display");
			
			if(shareMealOne != "undefined" && shareMealOne != "none")
			{
				var shareMealOneValue = $("#SHAREMEALONE_NAME").val();
				var shareMealTwoValue = $("#SHAREMEALTWO_NAME").val();

				if('' == shareMealOneValue || null == shareMealOneValue)
				{
					alert("请正确填写共享副号一");
					return;
				}
				if('' == shareMealTwoValue || null == shareMealTwoValue)
				{
					alert("请正确填写共享副号二");
					return;
				}
				if(shareMealOneValue == shareMealTwoValue)
				{
					alert("填写共享副号不能一样");
					return;
				}
				if(0 == $("#SHAREMEAL_NAME").val())
				{
					alert("请点击校验通过！");
					return;
				}
			}
			
			var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
			if(tradeTypeCode != "undefined" && tradeTypeCode !='' && tradeTypeCode != null && tradeTypeCode == '3803'){
				//发票处理
				changeProductExtend.dealInvoice2();
			}
			if(tradeTypeCode != "undefined" && tradeTypeCode !='' && tradeTypeCode != null && tradeTypeCode == '110'){
				//发票处理
				changeProductExtend.dealInvoice();
				
				//校验
				if(!changeProduct.confirmSubmit()){
					return false;
				}
				
				var data1 = selectedElements.getSubmitData();
								
				var size = data1.length ;
				//var size1 = 0 ;
				var userpid = $("#USER_PRODUCT_ID").val();
				var newuserpid = $("#NEW_PRODUCT_ID").val();
				data1.each(function(item){
					var productid = item.get("PRODUCT_ID") ;
					var elementtype = item.get("ELEMENT_TYPE_CODE");
					//var newproductid = item.get("NEW_PRODUCT_ID");
					if((userpid==productid) && (elementtype=="D"))
					{
						size = size -1 ; 
					}
				});
				
				//取消GPRS的时候，二次确认后，如果用户确认取消，则系统同时取消VOLTE服务。
				if(data1&&data1.length>0){
					
					var bResult22 = true;
					var bResult190 = true;
					for(var i=0; i < data1.length; i++)
					{
						var item = data1.get(i);
						var strElementId = item.get("ELEMENT_ID") ;
						//alert(strElementId);
						var strElementTypeCode = item.get("ELEMENT_TYPE_CODE");
						var strModifyTag = item.get("MODIFY_TAG");
						if(strElementId=="22" && strElementTypeCode=="S" && strModifyTag == "1")
						{
							bResult22 = false;
						}
						if(strElementId=="190" && strElementTypeCode=="S" && strModifyTag == "1")
						{
							bResult190 = false
						}
					}
				
					if(!bResult22 && bResult190)
					{
						if(selectedElements.checkIsExist("190", "S"))
						{
							var content = "办理取消手机上网功能，则系统同时取消VOLTE服务，是否继续？";
							if(!window.confirm(content))
							{
								return;
		   					}
						}
					}
				}
				
				//提示确认
				var content = "";
				var productname = $("#NEW_PRODUCT_NAME").val();
				var bookingDate = $("#BOOKING_DATE").val();
				var sysDate = $("#SYS_DATE").val();
				if(bookingDate != "undefined" && bookingDate !='' && sysDate != "undefined" && sysDate !=''){
					if(bookingDate.substring(0,10) == sysDate.substring(0,10)){
						if(newuserpid==null || newuserpid=="" || userpid==newuserpid)
						{
							content = "本次将受理【立即】产品变更业务，是否继续？";
						}
						else
						{										
							//content = "本次将受理【立即】产品变更业务，是否继续？";
							//if(size==1)
							//{							
								content = "本次将受理【立即】【"+ productname +"】产品变更业务，是否继续？";
							//}
						}
						
					}else{
						if(newuserpid==null || newuserpid=="" || userpid==newuserpid)
						{
							content = "本次将受理【预约】产品变更业务，是否继续？";
						}
						else
						{	
							//content = "本次将受理【预约"+ bookingDate.substring(0,10) + "】产品变更业务，是否继续？";
							//if(size==1)
							//{
								content = "本次将受理【预约"+ bookingDate.substring(0,10) + "】【"+ productname +"】产品变更业务，是否继续？";
							//}
														
						}		
					}
				}
				MessageBox.confirm("确认提示", "业务提交提示！", 
				function(btn){
					if(btn == "ok"){
						$.cssubmit.submitTrade();//提交台账
					}
				}, null, content);
			}else{
				return changeProduct.confirmSubmit();
			}
		},
		
		confirmSubmit: function(){
			//重置校验监听方法
			$.tradeCheck.setListener('checkBeforeTrade');
			
			if(!$.validate.verifyAll())
   			{
	   			return false;
   			}
		
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
				
				param+="&REMARK="+$("#REMARK").val();
				param+="&SHAREMEALONE="+$("#SHAREMEALONE_NAME").val();
				param+="&SHAREMEALTWO="+$("#SHAREMEALTWO_NAME").val();
				
				if(typeof(changeProductExtend.getOtherSubmitParam)=="function"){
					param += changeProductExtend.getOtherSubmitParam();
				}
				
				$.cssubmit.addParam(param);
				return true;
			}
			else{
				alert("您没有进行任何操作，不能提交");
				return false;
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
		},
		
		beforeSubmitSerialNumber:function(data){
			$.tradeCheck.addParam(null);
			$.tradeCheck.setListener("checkBeforeTrade");
		}				
	});
})();

