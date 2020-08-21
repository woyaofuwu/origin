if(typeof(UserPlatSvcsList)=="undefined"){window["UserPlatSvcsList"]=function(){};var userPlatSvcsList = new UserPlatSvcsList();}
(function(){
	$.extend(UserPlatSvcsList.prototype,{
		selectedElements: new $.DatasetList(),
		allCancels: new $.DataMap(),
		allSwitch: new $.DataMap(),
		eparchyCode:null,
		operCode: "",
		userId:"",
		renderComponent: function(userId,eparchyCode){
			var data="&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&DISABLE_OPERATION="+$("#disableOperation").val();
			this.eparchyCode = eparchyCode;
			this.userId = userId;
			$.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterRender);
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
					$("#"+i+"_ATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),true,"");});
				}
			}
			else{
				userPlatSvcsList.selectedElements = new $.DatasetList();
			}
		},
		
		//清理
		clearCache: function(data){
		   userPlatSvcsList.selectedElements.clear();
		   userPlatSvcsList.allCancels.clear();
		   userPlatSvcsList.allSwitch.clear();
		   userPlatSvcsList.eparchyCode = null;
		   userPlatSvcsList.operCode = "";
		   userPlatSvcsList.userId = "";
		},
		
		findElement: function(serviceId){
			if(this.selectedElements!=null){
				var size = this.selectedElements.length;
				for(var i=0;i<size;i++){
					var data = this.selectedElements.get(i);
					if(data.get("SERVICE_ID")==serviceId){
						return data;
					}
				}
			}
			return null;
		},
		
		addElement: function(serviceId){
			var data = "&SERVICE_ID="+serviceId+"&IS_ELEMENT=true&USER_ID="+this.userId+"&ROUTE_EPARCHY_CODE="+this.eparchyCode;
			$.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterAddElement);
		},
		
		addKeyElement: function(serviceId){
			var data = "&SERVICE_ID="+serviceId+"&IS_ELEMENT=true&USER_ID="+this.userId+"&ROUTE_EPARCHY_CODE="+this.eparchyCode;
			$.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterAddKeyElement);
		},
		
		afterAddElement: function(data){
			if(data){
				var drawTbody = $("#userPlatSvcTable");
				var html=[];
				var itemIndex = userPlatSvcsList.selectedElements.length;
				html.push('<tr class="new">');
				html.push('<td class="e_center"><input type="checkbox" class="e_checkbox" itemIndex="'+itemIndex+'" name="'+itemIndex+'_USERCHECKBOX" id="'+itemIndex+'_USERCHECKBOX" checked onclick="userPlatSvcsList.checkBoxAction(this)"/></td>');
				html.push('<td class="edit">');
				html.push('<span class="e_select"><span><span>');
				html.push('<select onchange="userPlatSvcsList.changeOrderGiftOper(this,'+itemIndex+')">');
				var supportOpers = data.get('SUPPORT_OPERS');
				for(var i=0;i<supportOpers.length;i++){
					var oper = supportOpers.get(i);
					html.push('<option value="'+oper.get("OPER_CODE")+'">'+oper.get("OPER_NAME")+'</option>');
				}
				html.push('</select>');
				html.push('</span></span></span>');
				html.push('</td>');
				html.push('<td>')
				if(data.get("ATTR_PARAM")&&data.get("ATTR_PARAM").length>0){
					html.push('<a href="#nogo" elementId="'+data.get("SERVICE_ID")+'" itemIndex="'+itemIndex+'" id="'+itemIndex+'_ATTRPARAM" name="'+itemIndex+'_ATTRPARAM" elementType="Z" class="select"></a>');
				}
				html.push(data.get("SERVICE_NAME")+'</td>');
				html.push('<td>'+data.get("BIZ_STATE")+'</td>');
				html.push('<td id="'+itemIndex+'_PRICE" name="'+itemIndex+'_PRICE">'+data.get("PRICE")+'</td>');
				html.push('<td>'+data.get("BILL_TYPE")+'</td>');
				html.push('<td>'+data.get("START_DATE")+'</td>');
				html.push('<td>'+data.get("END_DATE")+'</td>');
				html.push('<td></td>');
				html.push('<td>'+data.get("BIZ_TYPE_NAME")+'</td>');
				html.push('<td>'+data.get("BIZ_CODE")+'</td>');
				html.push('<td>'+data.get("SP_CODE")+'</td>');
				html.push('<td>'+data.get("SP_NAME")+'</td>');
				drawTbody.prepend(html.join(""));
				
				//$.insertHtml('afterbegin',drawTbody,html.join(""));
				$("#"+itemIndex+"_ATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),false,'06');});
				var map = new $.DataMap();
				map.put("SERVICE_ID",data.get("SERVICE_ID"));
				map.put("BIZ_STATE_CODE","A");
				map.put("MODIFY_TAG","0");
				map.put("OPER_CODE","06");
				if(data.get("ATTR_PARAM")){
					map.put("ATTR_PARAM",data.get("ATTR_PARAM"));
				}
				userPlatSvcsList.selectedElements.add(map);
			}
		},
		
		afterAddKeyElement: function(data){
			if(data){
				var drawTbody = $("#userPlatSvcTable");
				var html=[];
				var itemIndex = userPlatSvcsList.selectedElements.length;
				html.push('<tr class="new">');
				html.push('<td class="e_center"><input type="checkbox" class="e_checkbox" itemIndex="'+itemIndex+'" name="'+itemIndex+'_USERCHECKBOX" id="'+itemIndex+'_USERCHECKBOX" checked onclick="userPlatSvcsList.checkBoxAction(this)"/></td>');
				html.push('<td class="edit">');
				html.push('<span class="e_select"><span><span>');
				html.push('<select onchange="userPlatSvcsList.changeOrderGiftOper(this,'+itemIndex+')">');
				var supportOpers = data.get('SUPPORT_OPERS');
				for(var i=0;i<supportOpers.length;i++){
					var oper = supportOpers.get(i);
					html.push('<option value="'+oper.get("OPER_CODE")+'">'+oper.get("OPER_NAME")+'</option>');
				}
				html.push('</select>');
				html.push('</span></span></span>');
				html.push('</td>');
				/*html.push('<td>')
				if(data.get("ATTR_PARAM")&&data.get("ATTR_PARAM").length>0){
					html.push('<a href="#nogo" elementId="'+data.get("SERVICE_ID")+'" itemIndex="'+itemIndex+'" id="'+itemIndex+'_ATTRPARAM" name="'+itemIndex+'_ATTRPARAM" elementType="Z" class="select"></a>');
				}*/
				html.push('<td>'+data.get("SERVICE_NAME")+'</td>');
				html.push('<td>'+data.get("BIZ_STATE")+'</td>');
				html.push('<td>'+data.get("PRICE")+'</td>');
				html.push('<td>'+data.get("BILL_TYPE")+'</td>');
				html.push('<td>'+data.get("START_DATE")+'</td>');
				html.push('<td>'+data.get("END_DATE")+'</td>');
				html.push('<td></td>');
				html.push('<td>'+data.get("BIZ_TYPE_NAME")+'</td>');
				html.push('<td>'+data.get("BIZ_CODE")+'</td>');
				html.push('<td>'+data.get("SP_CODE")+'</td>');
				html.push('<td>'+data.get("SP_NAME")+'</td>');
				drawTbody.prepend(html.join(""));
				
				//$.insertHtml('afterbegin',drawTbody,html.join(""));
				//$("#"+itemIndex+"_ATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),false,'06');});
				var map = new $.DataMap();
				map.put("SERVICE_ID",data.get("SERVICE_ID"));
				map.put("BIZ_STATE_CODE","A");
				map.put("MODIFY_TAG","0");
				map.put("OPER_CODE","06");
				if(data.get("ATTR_PARAM")){
					//固定特级会员属性
					var mapattr = new $.DataMap();
					var attr=new $.DatasetList();
					mapattr.put("ATTR_VALUE","3");
					mapattr.put("ATTR_CODE","302");
					attr.add(mapattr)
					map.put("ATTR_PARAM",attr);
				}
				userPlatSvcsList.selectedElements.add(map);
			}
		},
		
		checkBoxAction: function(eventObj){
			var element = this.selectedElements.get($(eventObj).attr("itemIndex"))
			if(eventObj.checked){
				if(element.get("MODIFY_TAG")=="0_1"){
					element.put("MODIFY_TAG","0");
				}
				else{
					var operObj = $("#"+$(eventObj).attr("itemIndex")+"_OPERCODE");
					operObj.attr("disabled",false);
					element.put("OPER_CODE",operObj.attr("value")); //fix bug 默认开通的业务，暂停时，也变成了退订
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
					var operObj = $("#"+$(eventObj).attr("itemIndex")+"_OPERCODE");
					operObj.attr("disabled",true);
					operObj.val(operObj.attr("initValue"));
				}
			}
		},
		
		changeOperCode: function(eventObj){
			var itemIndex = $(eventObj).attr("itemIndex");
			var element = this.selectedElements.get($(eventObj).attr("itemIndex"));
			if(eventObj.value!="02"&&eventObj.value!="07"){
				element.put("MODIFY_TAG","2");
			}
			else{
				element.put("MODIFY_TAG","1");
			}
			element.put("OPER_CODE",eventObj.value);
			
			if(eventObj.value=="03" || eventObj.value=="08" ||eventObj.value=="10"||eventObj.value=="11"||eventObj.value=="12"||eventObj.value=="17"||eventObj.value=="18"){
				$("#"+itemIndex+"_ATTRPARAM").unbind("click");
				$("#"+itemIndex+"_ATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),false,eventObj.value);});
				$("#"+itemIndex+"_ATTRPARAM").trigger("click");
				 
			}
			else{
				$("#"+itemIndex+"_ATTRPARAM").unbind("click");
				$("#"+itemIndex+"_ATTRPARAM").bind("click",function(){userPlatSvcsList.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'),true,"");});
				$("#elementPanel").css("display","none");
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
				var serialNumber = $("#SERIAL_NUMBER").val();
				
				if($.trim(giftSerialNumber) == '')
				{
					MessageBox.alert("提示","赠送号码不能为空");
					return false;
				}
				
				if(giftSerialNumber == serialNumber)
				{
					MessageBox.alert("提示","赠送号码不能是本人的号码");
					return false;
				}
				
				var giftStartDate = $("#GIFT_START_DATE").val();
				var giftEndDate = $("#GIFT_END_DATE").val();
				var giftItemIndex = $("#GIFT_ITEM_INDEX").val();
				if(giftEndDate<=giftStartDate){
					MessageBox.alert("提示","赠送结束时间不能小于赠送开始时间");
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
			var itemIndex = eventObj.getAttribute("itemIndex");
			var tempElement = userPlatSvcsList.selectedElements.get(itemIndex);
			
			if(!isView)
			{
				tempElement.put("IS_WRITE_ATTR",false); //需要填写完整ATTR，提交前通过该字段校验
			}
			
			
			userPlatSvcsList.operCode = operCode;
			var params = "&ELEMENT_ID="+elementId+"&ELEMENT_TYPE_CODE="+elementType+"&ITEM_INDEX="+eventObj.getAttribute("itemIndex")+"&EPARCHY_CODE="+this.eparchyCode+"&USER_ID="+this.userId;
			if(operCode!=null&&typeof(operCode)!="undefined"){
				params+="&DISPLAY_CONDITION="+operCode;
			}
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
					$("#"+attrCode).val(attrValue);
				}
				if(isView){
					$("#"+attrCode).attr("disabled",true);
				}
				if($("#"+attrCode).attr("onchange")){
					$("#"+attrCode).trigger("onchange");
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
			
			var obj = $(eventObj);
			var o = $(eventObj).offset();	
			var topAdd = 0;
			var scroll =  $("div[class=m_wrapper]:first");
			if(scroll.length>0){
				topAdd = scroll.attr("scrollTop");
			}
			$("#elementPanel").css("top", (o.top+obj.height()+topAdd) + "px");
			$("#elementPanel").css("left", (o.left+obj.width()-$("#elementPanel").width()) + "px");
			$("#elementPanel").css("display","");

		},
		
		confirmAttr: function(itemIndex){
			var tempElement = this.selectedElements.get(itemIndex);
			if($.validate.verifyAll('elementPanel')){		
				var attrs = tempElement.get("ATTR_PARAM");
				var length = attrs.length;
				var isUpdate = false;
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					var newAttrValue = $("#"+attrCode).val();
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
				$("#elementPanel").css("display","none");
				
			}
		},
		changePsptType: function(psptTypeCode){
			var result;
			if(psptTypeCode=="0"||psptTypeCode=="1"){//本外地身份证
				result="00";
//			}else if(psptTypeCode=="2"){//VIP卡
//				result="01";
			}else if(psptTypeCode=="2"){//户口本
				result="02";
			}else if(psptTypeCode=="3"){//军人证
				result="03";
//			}else if(psptTypeCode=="2"){//警察证
//				result="04";
			}else if(psptTypeCode=="O"){//港澳居民往来内地通行证
				result="05";
			}else if(psptTypeCode=="N"){//台湾居民来往大陆通行证
				result="06";
			}else if(psptTypeCode=="A"){//护照
				result="07";
			}else if(psptTypeCode=="E"){//单位营业执照（组织机构代码证）副本原件
				result="08";
//			}else if(psptTypeCode=="E"){//加盖公章的营业执照（组织机构代码证）复印件
//				result="09";
			}else{//其他
				result="99";
			}
			return result;
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

if (typeof(SelectedElements) == "undefined") {window["SelectedElements"]=function(){};var selectedElements = new SelectedElements();}
(function(){
	$.extend(SelectedElements.prototype,{
		confirmAttr: function(itemIndex){
			userPlatSvcsList.confirmAttr(itemIndex);
		}
	});
	
	$.extend(SelectedElements.prototype,{
		cancelElementAttr: function(itemIndex){
			$("#elementPanel").css("display","none");
			$("#elementPanelUL").html();
		}
	});
})();

