if(typeof(UserPlatSvcsList)=="undefined"){window["UserPlatSvcsList"]=function(){};var userPlatSvcsList = new UserPlatSvcsList();}
(function(){
	$.extend(UserPlatSvcsList.prototype,{
		selectedElements: new $.DatasetList(),
		allCancels: new $.DataMap(),
		allSwitch: new $.DataMap(),
		eparchyCode:null,
		operCode: "",
		userId:"",
		attrVisable:false,
		renderComponent: function(userId,eparchyCode){
			$.beginPageLoading("平台服务加载中。。。。。");
			var data="&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode;
			this.eparchyCode = eparchyCode;
			this.userId = userId;
			$.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterRender,function(error_code,error_info){
                $.endPageLoading();
				MessageBox.alert(error_info);
            },{async:false});
		},
		
		afterRender: function(data){
			if(data&&data.length>0){
				userPlatSvcsList.selectedElements = data;
				for(var i=0;i<data.length;i++){
					//1是
					var isInternetTvSvc=data.get(i).get("INTERNET_TV_SVC");
					if(isInternetTvSvc=="1"){
						$("#"+i+"_USERCHECKBOX").attr("disabled",true);
					}
					$("#"+i+"_OPERCODE").attr("disabled",true);
					$("#"+i+"_PLATATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),true,"");});
				}
			}
			else{
				userPlatSvcsList.selectedElements = new $.DatasetList();
			}
			$.endPageLoading();
		},
		
		//清理
		clearCache: function(data){
		   userPlatSvcsList.selectedElements.clear();
		   userPlatSvcsList.allCancels.clear();
		   userPlatSvcsList.allSwitch.clear();
		   userPlatSvcsList.eparchyCode = null;
		   userPlatSvcsList.operCode = "";
		   userPlatSvcsList.userId = "";
		   userPlatSvcsList.attrVisable = false;
		},
		
		checkBoxAction: function(eventObj){
			var itemIndex = $(eventObj).attr("itemIndex");
			var element = this.selectedElements.get(itemIndex)
			if(eventObj.checked){
				if(element.get("MODIFY_TAG")=="0_1"){
					element.put("MODIFY_TAG","0");
				}
				else{
					var operObj = $("#"+itemIndex+"_OPERCODE");
					operObj.attr("disabled",false);
					element.put("OPER_CODE",operObj.attr("initValue"));
					element.put("MODIFY_TAG","1");
				}
			}
			else{
				element.put("IS_WRITE_ATTR",true); //checkbox取消，则attr不需要填写.
				
				if(element.get("MODIFY_TAG")=="0"){
					//新增服务
					element.put("MODIFY_TAG","0_1");
				}
				else{
					element.put("MODIFY_TAG","exist");
					element.remove("OPER_CODE");
					var operObj = $("#"+itemIndex+"_OPERCODE");
					operObj.attr("disabled",true);
					operObj.val(operObj.attr("initValue"));
				}
				
				$("#"+itemIndex+"_PLATATTRPARAM").unbind("click");
				$("#"+itemIndex+"_PLATATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),true,"");});
			}
		},
		
		changeOperCode: function(eventObj){
			var itemIndex = $("#"+$(eventObj).attr("el").id).attr("itemIndex");
			var element = this.selectedElements.get(itemIndex);
			if(eventObj.value!="02"&&eventObj.value!="07"){
				element.put("MODIFY_TAG","2");
			}
			else{
				element.put("MODIFY_TAG","1");
			}
			element.put("OPER_CODE",eventObj.value);
			
			if(eventObj.value=="03" || eventObj.value=="08" ||eventObj.value=="10"||eventObj.value=="11"||eventObj.value=="12"||eventObj.value=="17"||eventObj.value=="18"){
				$("#"+itemIndex+"_PLATATTRPARAM").unbind("click");
				$("#"+itemIndex+"_PLATATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),false,eventObj.value);});
				$("#"+itemIndex+"_PLATATTRPARAM").trigger("click");
			}
			else{
				$("#"+itemIndex+"_PLATATTRPARAM").unbind("click");
				$("#"+itemIndex+"_PLATATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),true,"");});
				hidePopup('offerAttrPop');
			}
		},
		
		changeOrderGiftOper: function(eventObj,itemIndex){
			var oper = eventObj.value;
			var element = this.selectedElements.get(itemIndex);
			element.put("OPER_CODE",oper);
			if(oper=="GIFT"){
				var giftSerialNumber = element.get("GIFT_SERIAL_NUMBER");
				var giftStartDate = element.get("GIFT_START_DATE");
				var giftEndDate = element.get("GIFT_END_DATE");
				if(giftSerialNumber!="undefined"&&giftSerialNumber){
					$("#GIFT_SERIAL_NUMBER").val(giftSerialNumber);
				}
				else{
					$("#GIFT_SERIAL_NUMBER").val("");
				}
				$("#GIFT_START_DATE").val(giftStartDate);
				if(giftEndDate!="undefined"&&giftEndDate){
					$("#GIFT_END_DATE").val(giftEndDate);
				}
				else{
					$("#GIFT_END_DATE").val("");
				}
				
				$("#GIFT_ITEM_INDEX").val(itemIndex);
				var obj = $(eventObj);
				var o = $(eventObj).offset();	
				$("#giftPanel").css("top", (o.top+obj.height()) + "px");
				$("#giftPanel").css("left", (o.left+obj.width()-$("#giftPanel").width()+100) + "px");
				$("#giftPanel").css("display","");
			}
			else{
				$("#giftPanel").css("display","none");
			}
		},
		
		confirmGift:function(){
			if($.validate.verifyAll('giftPanel')){
				var giftSerialNumber = $("#GIFT_SERIAL_NUMBER").val();
				var giftStartDate = $("#GIFT_START_DATE").val();
				var giftEndDate = $("#GIFT_END_DATE").val();
				var giftItemIndex = $("#GIFT_ITEM_INDEX").val();
				if(giftEndDate<=giftStartDate){
					alert("赠送结束时间不能小于赠送开始时间");
					return false;
				}
				var itemIndex = $("#GIFT_ITEM_INDEX").val();
				var element = this.selectedElements.get(itemIndex);
				element.put("GIFT_SERIAL_NUMBER",giftSerialNumber);
				element.put("GIFT_START_DATE",giftStartDate);
				element.put("GIFT_END_DATE",giftEndDate);
				$("#giftPanel").css("display","none");
			}
		},
		
		showAttr: function(eventObj,elementId,elementType,isView,operCode){
		    userPlatSvcsList.attrVisable = true;
		    
		    var itemIndex = eventObj.getAttribute("itemIndex");
			var tempElement = userPlatSvcsList.selectedElements.get(itemIndex);
		    if(!isView)
			{
				tempElement.put("IS_WRITE_ATTR",false); //需要填写完整ATTR，提交前通过该字段校验
			}
		    
			userPlatSvcsList.operCode = operCode;
			var params = /**"&ELEMENT_ID="+elementId+"&ELEMENT_TYPE_CODE="+elementType+"&ITEM_INDEX="+eventObj.getAttribute("itemIndex")+*/"&EPARCHY_CODE="+this.eparchyCode+"&USER_ID="+this.userId;
			if(operCode!=null&&typeof(operCode)!="undefined"){
				params+="&DISPLAY_CONDITION="+operCode;
			}
			
			tempElement.put("OFFER_TYPE", "Z");
			tempElement.put("OFFER_CODE", tempElement.get("SERVICE_ID"));

			$.ajax.submit(null,null,params,$("#ELEMENTATTR_COMPONENT_ID").val(),function(data){userPlatSvcsList.afterShowAttr(data,eventObj,elementId,elementType,isView)});
		},
		
		afterShowAttr: function(data,eventObj,elementId,elementType,isView){
			var itemIndex = eventObj.getAttribute("itemIndex");
			//设置回填值
			var tempElement = userPlatSvcsList.selectedElements.get(itemIndex);
			var attrs = tempElement.get("ATTR_PARAM");
			var length = attrs.length;
			for(var i=0;i<length;i++){
				var attr = attrs.get(i);
				var attrCode = attr.get("ATTR_CODE");
				var attrValue = attr.get("ATTR_VALUE");
				if(attrValue){
					$("#ATTR"+attrCode).val(attrValue);
				}
				if(isView){
					$("#ATTR"+attrCode).attr("disabled",true);
				}
				if($("#ATTR"+attrCode).attr("onchange")){
					$("#ATTR"+attrCode).trigger("onchange");
				}
			}
			
			//手机支付特殊处理，默认填写证件号码
			if(elementId == '99081371')
			{
			   var psptType =  $("#CARDTYPE").val();
			   var psptNO = $("#CARDNUM").val();
			   if(psptType == '')
			   {
			     $("#CARDNUM").val($("#PSPT_ID").val());
			   }
			   if(psptNO == '')
			   {
			      $("#CARDTYPE").val('00');//默认取身份证
			   }
			}
			
			$("#attrTitle").html(tempElement.get("SP_NAME"));
//            $("#attrPanel").attr("style","display:");
            $("#giftPanel").attr("style","display:none");
            
            offerAttr.render(tempElement, this.eparchyCode, isView, function() {
            	userPlatSvcsList.confirmAttr(itemIndex);
			});
		},
		
		confirmAttr: function(itemIndex){
			var tempElement = this.selectedElements.get(itemIndex);
			if($.validate.verifyAll('offerAttrPanel')){		
				var attrs = tempElement.get("ATTR_PARAM");
				var length = attrs.length;
				var isUpdate = false;
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					var newAttrValue = $("#ATTR"+attrCode).val();
					if($.trim(attrValue)!= $.trim(newAttrValue)){
						attr.put("ATTR_VALUE",newAttrValue);
						isUpdate = true;
					}
					if(tempElement.get("SERVICE_ID") == "98001901"){
						//var tr = $("#userPlatSvcTable").find("tr").eq(itemIndex);
						//var td = tr.find("td").eq(4);
						var td = $("#"+itemIndex+"_PRICE");
						if(newAttrValue == '1'){
							td.html("0.0");
						}else if(newAttrValue == '2'){
							td.html("5.0");
						}else if(newAttrValue == '3'){
							td.html("6.0");
						}				
					}
				}
				if(isUpdate&&tempElement.get("MODIFY_TAG")!="0"&&tempElement.get("MODIFY_TAG")!="1"){
					tempElement.put("MODIFY_TAG","2");
				}
				
				if(tempElement.get("SERVICE_ID") == '99166951'){//手机支付需要校验客户实名制信息
					//auth查询结果
					var isRealName =  $("#IS_REAL_NAME").val();
					var psptTypeCode = $("#PSPT_TYPE_CODE").val();
					var custName = $("#CUST_NAME").val();
					var psptId = $("#PSPT_ID").val();
					if(isRealName != '1'){
						MessageBox.alert("提示","非实名制用户不能办理手机支付业务");
						return false;
					}
					for(var i=0;i<length;i++){
						var attr = attrs.get(i);
						var attrCode = attr.get("ATTR_CODE");
						var attrValue = attr.get("ATTR_VALUE");
						if($.trim(attrCode)=='101'&&$.trim(attrValue)!= $.trim(custName)){//客户姓名
							MessageBox.alert("提示","客户姓名与登记信息不符");
							return false;
						}
						if($.trim(attrCode)=='103'&&$.trim(attrValue)!= userPlatSvcsList.changePsptType($.trim(psptTypeCode))){//证件类型
							MessageBox.alert("提示","证件类型与登记信息不符");
							return false;
						}
						if($.trim(attrCode)=='104'&&$.trim(attrValue)!= $.trim(psptId)){//证件号码
							MessageBox.alert("提示","证件号码与登记信息不符");
							return false;
						}
					}
				}					
				tempElement.put("IS_WRITE_ATTR",true); //已经填写完整ATTR
			}
		},
		
		addAllCancel: function(eventObj){
			var obj = $(eventObj);
			if(eventObj.checked){
				if(obj.val()=="SP"){
					$("#ALL_CANCEL_SP").attr("disabled","");
					this.allCancels.put(obj.val(),$("#ALL_CANCEL_SP").val());
				}
				else{
					this.allCancels.put(obj.val(),obj.val());
				}
			}
			else{
				this.allCancels.removeKey(obj.val());
				if(obj.val()=="SP"){
					$("#ALL_CANCEL_SP").attr("disabled","true");
				}
			}
		},
		
		addAllSwitch: function(eventObj){
			var obj = $(eventObj);
			var temp = new $.DataMap();
			if(eventObj.checked){
				temp.put("SERVICE_ID",obj.val());
				if(obj.attr("isClose") == "false"){
					temp.put("OPER_CODE","91");
				}
				else{
					temp.put("OPER_CODE","90");
				}
				this.allSwitch.put(obj.val(),temp);
			}
			else{
				this.allSwitch.removeKey(obj.val());
			}
		},
		
		changeSpAllCancel: function(eventObj){
			var obj = $(eventObj);
			if(this.allCancels.get("SP")){
				this.allCancels.put("SP",obj.val());
			}
		},
		
		getAllCancelSpCode: function(){
			var result = new $.DataMap();
			var size = this.selectedElements.length;
			for(var i=0;i<size;i++){
				var element = this.selectedElements.get(i);
				if(element.get("SP_CODE")&&element.get("ORG_DOMAIN")=="DSMP"){
					result.put(element.get("SP_CODE"),element.get("SP_NAME"));
				}
			}
			return result;
		},
		
		isExist: function(temp,flag){
			var size = this.selectedElements.length;
			for(var i=0;i<size;i++){
				var element = this.selectedElements.get(i);
				if(flag=="1"&&element.get("BIZ_TYPE_CODE")==temp){
					return true;
				}
				else if(flag=="2"&&element.get("ORG_DOMAIN")==temp){
					return true;
				}
			}
			return false;
		},
		
		getOperElements: function(eventObj){
			var length = this.selectedElements.length;
			var submitElements = new $.DatasetList();
			for(var i=0;i<length;i++){
				var element = this.selectedElements.get(i);
				if(element.get("MODIFY_TAG")=="0"||element.get("MODIFY_TAG")=="1"||element.get("MODIFY_TAG")=="2"){
					if(element.get("OPER_CODE") == "06"){
						element.removeKey("GIFT_SERIAL_NUMBER");
						element.removeKey("GIFT_START_DATE");
						element.removeKey("GIFT_END_DATE");
					}
					element.removeKey("ORG_DOMAIN");
					element.removeKey("SP_CODE");
					element.removeKey("SP_NAME");
					//element.removeKey("BIZ_TYPE_CODE");
					submitElements.add(element);
				}
			}
			return submitElements;
		}
	});
})();

//﻿if(typeof(SelectedElements)=="undefined"){window["SelectedElements"]=function(){};var selectedElements = new SelectedElements();}
//(function(){
//	$.extend(SelectedElements.prototype,{
//		confirmAttr: function(itemIndex){
//			userPlatSvcsList.confirmAttr(itemIndex);
//		}
//	});
//})();
//
