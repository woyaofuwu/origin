
var gElems = $.DatasetList();

if(typeof(SaleActiveModule)=="undefined"){
	window["SaleActiveModule"]=function(){};
	var saleactiveModule = new SaleActiveModule();
}

(function(){
	$.extend(SaleActiveModule.prototype,{
		readerComponent: function(packageId, productId, campnType, newImei, deviceModelCode){	
			if($('#'+$('#SALEACTIVEMODULE_EPARCHY_CODE_COMPID').val()).val() == ''){
				alert('请先输入号码');
				return false;
			}
			var needCheck = $('#SALEACTIVE_NEED_CHECK').val();
			saleactiveModule.clearSaleActive();
			var userId = $('#SALEACTIVE_USER_ID').val();
			var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
			var eparchyCode = $('#'+$('#SALEACTIVEMODULE_EPARCHY_CODE_COMPID').val()).val();
			var param = '';
			param += "&EPARCHY_CODE=" + eparchyCode;
			param += '&USER_ID=' + userId;
			param += '&SERIAL_NUMBER='+serialNumber;
			
			if(typeof(packageId) != "undefined") param += '&PACKAGE_ID='+packageId;
			if(typeof(productId) != "undefined") param += '&PRODUCT_ID='+productId;
			if(typeof(campnType) != "undefined") param += '&CAMPN_TYPE='+campnType;
			if(typeof(newImei) != "undefined") param += '&NEW_IMEI='+newImei;
			if(typeof(deviceModelCode) != "undefined") param += '&DEVICE_MODEL_CODE='+deviceModelCode;
			if(needCheck == "true"){
				param += '&SPEC_TAG=checkByPackage';
				$.beginPageLoading("规则校验中。。。");		
				ajaxSubmit(null, null, param, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), 
					function(data) {
						$.endPageLoading(); 
						var tigTag= data.get("IS_FTTH_USER");
					 
						if(tigTag=="true"){
							alert("提醒：FTTH用户开户完成后或者参加宽带1+活动后，请在“FTTH光猫申请”界面申领光猫。");
						}
						
						var confirmSet = data.get("TIPS_TYPE_CHOICE");
						var warnSet = data.get("TIPS_TYPE_TIP");
						if(confirmSet && confirmSet.length>0){
							var flag = true;
							confirmSet.each(function(item, index, totalCount){
								if(!window.confirm(item.get("TIPS_INFO"))){
									flag = false;
									return false;
								}
							});
							if(!flag) return false;
						}
						if(warnSet && warnSet.length>0){
							warnSet.each(function(item, index, totalCount){
								window.alert(item.get("TIPS_INFO"));
							});
						}
						var checkData = new Wade.DataMap();
						checkData.put("USER_ID",              userId);
						checkData.put("SERIAL_NUMBER",        serialNumber);
						checkData.put("EPARCHY_CODE",         eparchyCode);
						checkData.put("LIMIT_COUNT",          data.get("LIMIT_COUNT"));
						checkData.put("SMS_VERI_CODE_TYPE",   data.get("SMS_VERI_CODE_TYPE","0"));
						checkData.put("NOTICE_CONTENT",       data.get("NOTICE_CONTENT"));
						checkData.put("BIND_SERIAL_NUMBER_B", data.get("BIND_SERIAL_NUMBER_B","false"));
						saleactiveModule.doCheckActionLinkedAsync(packageId, productId, campnType, newImei, checkData);
					}, 
					saleactiveModule.activeCheckFail);
			}else{
				saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
			}
		},
		doCheckActionLinkedAsync:function(packageId, productId, campnType, newImei, data){
		   var checkSmsVeriCode    = data.get("SMS_VERI_CODE_TYPE","0");
		   var isBindSerialNumberB = data.get("BIND_SERIAL_NUMBER_B","false");
		   /**
	         * REQ201511300032 业务问题优化-营销活动受理验证码下发
	         */
		   $("#submitDiv").css("display","");
		   $("#checkSMSBtn").css("display","none");		   
		   $("#CHECK_SMS_VERICODE").val(checkSmsVeriCode); //要清空，不然换包的时候会按原来的值
		   $("#LIMIT_COUNT").val(data.get("LIMIT_COUNT"));
		   $("#NOTICE_CONTENT").val(data.get("NOTICE_CONTENT"));
		   
		   
		   if(checkSmsVeriCode==="2" || checkSmsVeriCode==="1"){
			   $("#submitDiv").css("display","none");
			   $("#checkSMSBtn").css("display","");
			   saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
		      //saleactiveModule.checkSmsVariCode(packageId, productId, campnType, newImei, data);
		   }else if(isBindSerialNumberB === "true"){
		      saleactiveModule.checkBindSerialNumberB(packageId, productId, campnType, newImei, data);
		   }else{
		      saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
		   }
		},
		checkSmsVariCode:function(packageId, productId, campnType, newImei, data){
		    var checkSmsVeriCode = data.get("SMS_VERI_CODE_TYPE","0");
		    if("2" === checkSmsVeriCode){
			   if(window.confirm("确定要校验验证码吗?")){
				  saleactiveModule.doSmsVariCode(packageId, productId, campnType, newImei, data);
			   }else{
			      saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
			   }
		    }
		    else if("1" === checkSmsVeriCode){
		    	saleactiveModule.doSmsVariCode(packageId, productId, campnType, newImei, data);
		    }
		},
		doSmsVariCode:function(packageId, productId, campnType, newImei, data){
		    var param = "&USER_ID=" + data.get("USER_ID");
		    param += "&SERIAL_NUMBER=" + data.get("SERIAL_NUMBER");
		    param += "&PRODUCT_ID=" + productId;
		    param += "&PACKAGE_ID=" + packageId;
		    param += "&LIMIT_COUNT="+data.get("LIMIT_COUNT");
		    param += "&NOTICE_CONTENT=" + data.get("NOTICE_CONTENT");
		    param += "&EPARCHY_CODE=" + data.get("EPARCHY_CODE");
		    param += "&SPEC_TAG=SEND_VERI_CODE_SMS";
		    ajaxSubmit(null, null, param, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), function(ajaxData){
			   var checkCount = ajaxData.get('CHECK_COUNT');	
 	           var smsCode    = ajaxData.get('SMS_CODE');
 	           MessageBox.prompt("校验验证码第"+checkCount+"次","请输入验证码:" ,function(btn, codeNumber){
 	              debugger;
			      if(btn==="ok"){
				     if(codeNumber==null || codeNumber.length==0){alert('请输入校验号码！');return false;}
				     if(smsCode!=codeNumber){alert('验证码输入错误请重新验证！');return false;}
				     var params = '&PRODUCT_ID=' + productId + '&SERIAL_NUMBER=' + data.get("SERIAL_NUMBER");
				     params += "&USER_ID="+data.get("USER_ID") + "&EPARCHY_CODE="+data.get("EPARCHY_CODE");
				     params += "&SMS_CODE="+ smsCode +'&SPEC_TAG=UPD_VERI_CODE_OK';
				     ajaxSubmit(null, null, params, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), 
				         function(ajaxReturnData){
					    	 /**
						     * REQ201511300032 业务问题优化-营销活动受理验证码下发
						     */
					    	 alert("验证码正确！请提交。");
					    	 $("#submitDiv").css("display","");
							   $("#checkSMSBtn").css("display","none");
							   $("#SMS_VERI_SUCCESS").val("1"); // 短信验证码验证成功
				            //saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
				         },
				         function(error_code,error_info){
						   $("#SMS_VERI_SUCCESS").val("0");
		                    alert(error_info);
                         }
                     );
			      }
 	           });
		   },function(error_code,error_info){alert(error_info);} );
		},
		checkBindSerialNumberB:function(packageId, productId, campnType, newImei, data){
		   MessageBox.prompt("校验绑定号码","请输入服务号码：",function(btn, bindSerialNumber, data){
		       if(btn=="yes"){
                  if(bindSerialNumber==null || bindSerialNumber.length==0){alert('请输入校验号码！');return false;}
	              if(serialNumber==bindSerialNumber){alert('校验号码不能与办理号码一致！');return false;}
	              var params = '&PRODUCT_ID=' + productId + '&PACKAGE_ID=' +packageId  + "&EPARCHY_CODE="+data.get("EPARCHY_CODE");
				  params += '&BIND_SERIAL_NUMBER=' + bindSerialNumber +'&SPEC_TAG=CHECK_BIND_SN';
				  ajaxSubmit(null, null, params, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), 
				     function(ajaxData){
				        saleactiveModule.drawSaleActive(packageId, productId, campnType, newImei);
				     },
				     function(error_code,error_info){
		                alert(error_info);
                     }
                  );
	           }
		   })
		},
		drawSaleActive : function(packageId, productId, campnType, newImei){
			saleactive.openDetailPage();
			var param = '';
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVEMODULE_EPARCHY_CODE_COMPID').val()).val();
			param += "&SALEACTIVEMODULE_EPARCHY_CODE_COMPID=" + $('#SALEACTIVEMODULE_EPARCHY_CODE_COMPID').val();
			if(typeof(packageId) != "undefined") param += '&PACKAGE_ID='+packageId;
			if(typeof(productId) != "undefined") param += '&PRODUCT_ID='+productId;
			if(typeof(campnType) != "undefined") param += '&CAMPN_TYPE='+campnType;
			if(typeof(newImei) != "undefined") param += '&NEW_IMEI='+newImei;
			param += '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
			param += '&NET_ORDER_ID='+$("#NET_ORDER_ID").val();
			$.beginPageLoading("营销活动展现中。。。");		
			ajaxSubmit(null, null, param, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), saleactiveModule.afterDrawSaleActive,
			function(errorcode, errorinfo){
				$.endPageLoading();
				$.showErrorMessage('活动查询失败',errorinfo);
			});
		},
		activeCheckFail: function(rscode, rsinfo){
			$.endPageLoading();
			$.showErrorMessage('活动校验不通过',rsinfo);
		},
		afterDrawSaleActive: function(ajaxDataset){
			$.endPageLoading();
			var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
			var afterChoicePackageEvent = $('#AFTER_CHOICEPACKAGE_EVENT').val();
			/**
             * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
             * chenxy3 20160901 
             * 
             * */
			var redPackVal=$("#RED_PACK_VALUE").val(); 
            if(redPackVal!=null && redPackVal>0){
            	$("#AuthCodeBtn").css("display","");
				$("#submitDiv").css("display","none");
            }else{
            	$("#AuthCodeBtn").css("display","none");
				//$("#submitDiv").css("display","");
            }
			if(afterChoicePackageEvent != '')
			{
				try{
					new Function(afterChoicePackageEvent+"();")();
				}catch(e){
					alert('自定义JS方法' + afterChoicePackageEvent + '执行出错！');
					return;
				}
			}
		},
		clearSaleActive: function() {
			var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
			$('#'+componentId+'_PART').html('');
			$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
			var gElems = $.DatasetList();
		},
		
		/**
		 * 提交时获取营销活动相关的数据
		 */
		getSaleActiveSubmitData: function(){
			var saleactiveData = new Wade.DataMap();
			saleactiveData.put("PRODUCT_ID", $("#SALEACTIVE_PRODUCT_ID").val());
			saleactiveData.put("PACKAGE_ID", $("#SALEACTIVE_PACKAGE_ID").val());
			saleactiveData.put("SALEGOODS_IMEI", $("#SALEGOODS_NEW_IMEI").val());
			saleactiveData.put("SALE_STAFF_ID", $("#SALE_STAFF_ID").val());
			saleactiveData.put("CAMPN_TYPE", $("#SALEACTIVE_CAMPN_TYPE").val());
			saleactiveData.put("START_DATE", $("#SALEACTIVE_START_DATE").val());
			saleactiveData.put("END_DATE", $("#SALEACTIVE_END_DATE").val());
			saleactiveData.put("BOOK_DATE", $("#SALEACTIVE_BOOK_DATE").val());
			saleactiveData.put("ONNET_START_DATE", $("#SALEACTIVE_ONNET_START_DATE").val());
			saleactiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").val());
			//IPHONE6活动处理 20141022
 			saleactiveData.put("IPHONE6_IMEI", $("#IPHONE6_IMEI").val());
 			
 			//冼乃捷20150515 针对营销活动发票模版改造
 			saleactiveData.put("ALL_MONEY_NAME", $("#ALL_MONEY_NAME").val());
 			
 			//REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
 			saleactiveData.put("GIFT_CODE", $("#GIFT_CODE").val());
 			
			if(gElems.length > 0){
				saleactiveData.put("SELECTED_ELEMENTS", gElems);
			}
			return saleactiveData;
		},
		checkMinElementLimit:function(limitType, minElem){
		    if(minElem!=-1){
			   var elemNum = 0;
			   if(limitType == ''){
			      elemNum = gElems.length;
			      if(elemNum < minElem) {
				     alert('所有元素最小选择数为：' + minElem + '！所有当前元素选择数为：' + elemNum);
				     return false;
			      }
				  return true;
			   }else if(limitType == 'D'){
			      for(var i = 0; i < gElems.length; i++){
				     var elem = gElems.get(i);
				     if(elem.get('ELEMENT_TYPE_CODE') == 'D' && !elem.get("DISABLED")){
					   elemNum++;
				     }
			      }	
			      if(elemNum < minElem){
				     alert('优惠元素最小选择数为：' + minElem + '！优惠当前元素选择数为：' + elemNum);
				     return false;
			      }
				  return true;
			   }else if(limitType == 'S'){
			      for(var i = 0; i < gElems.length; i++){
				     var elem = gElems.get(i);
				     if(elem.get('ELEMENT_TYPE_CODE') == 'S' && !elem.get("DISABLED")){
					    elemNum++;
				     }
			      }
			      if(elemNum < minElem) {
				     alert('服务元素最小选择数为：' + minElem + '！服务当前元素选择数为：' + elemNum);
				     return false;
			      } 
				  return true;
			   }
			   return true;
			}
			return true;
		},
		checkMaxElementLimit:function(limitType, maxElem){
		    debugger;
		    if(maxElem != -1){
		       var elemNum = 0;
		       if(limitType == ''){	
			      elemNum = gElems.length;
			      if(elemNum > maxElem) {
				     alert('所有元素最大选择数为：' + maxElem + '！所有当前元素选择数为：' + elemNum);
				     return false;
			      }
			      return true;
		       }else if(limitType == 'D'){
				   for(var i = 0; i < gElems.length; i++){
					  var elem = gElems.get(i);
					  if(elem.get('ELEMENT_TYPE_CODE') == 'D' && !elem.get("DISABLED")){
						  elemNum++;
					  }
				   }		
				   if(elemNum > maxElem){
					  alert('优惠可选元素最大选择数为：' + maxElem + '！当前优惠可选元素选择数为：' + elemNum);
					  return false;
				   }
				   return true;
			    }else if(limitType == 'S'){
				   for(var i = 0; i < gElems.length; i++){
					 var elem = gElems.get(i);
					 if(elem.get('ELEMENT_TYPE_CODE') == 'S' && !elem.get("DISABLED")){
						elemNum++;
					 }
				   }
				   if(elemNum > maxElem) {
					  alert('服务元素最大选择数为：' + maxElem + '！当前服务元素选择数为：' + elemNum);
					  return false;
				   }
				   return true;
			    }
			    return true;
			}
		    return true;
		},
		elemLimitNumberCheck:function(){
		   var limitType = $("#SALEACTIVE_PGK_LIMIT_TYPE").val();
		   var minElem = parseInt($("#SALEACTIVE_PGK_ELEMENT_MIN").val());
		   var maxElem = parseInt($("#SALEACTIVE_PGK_ELEMENT_MAX").val());
		   if(!saleactiveModule.checkMinElementLimit(limitType, minElem)) return false;
		   if(!saleactiveModule.checkMaxElementLimit(limitType, maxElem)) return false;
		   return true;
		},
		combineElemLimitNumberCheck:function(){
		   var elemNum = 0;
		   for(var j = 0; j < gElems.length; j++) {
			  var elem = gElems.get(j);
			  if(elem.get('ELEMENT_TYPE_CODE') == "K") {
				 elemNum++;
			  }
		   }
		   //if(elemNum>0){
		      var limitMin = $("#SALEACTIVE_COMBINE_MIN").val();
		      if(limitMin){
		         var limitMinNum = parseInt(limitMin);
		         if(limitMinNum!=-1){
				   if(elemNum<limitMinNum){
					  alert('组合包元素最小选择数为：' + limitMinNum + '！当前元素选择数为：' + elemNum);
					  return false;
				   }
			     }
		      }
		      var limitMax = $("#SALEACTIVE_COMBINE_MAX").val();
		      if(limitMax){
		         var limitMaxNum = parseInt(limitMax);
		         if(limitMaxNum!=-1){
				    if(elemNum>limitMaxNum){
					   alert('组合包元素最大选择数为：' + limitMaxNum + '！当前元素选择数为：' + elemNum);
					   return false;
				    }
			     }
		      }
		   //}
	       return true;
		},
		/**
		 * 提交时的营销活动JS规则校验
		 */
		saleactiveSubmitJSCheck: function(){
			if($("#SALEACTIVE_PACKAGE_ID").length == 0 || $("#SALEACTIVE_PACKAGE_ID").val() == ''){
				alert('请先选择一个活动');
				return false;
			}
			
			if(!saleactiveModule.elemLimitNumberCheck()) return false;
			
			if(!saleactiveModule.combineElemLimitNumberCheck()) return false;
			
			var goods = saleactiveModule.spGetGoods();
			if (goods.length>0) {
				for(var i=0;i<goods.length;i++){
					var good = goods.get(i);
					if (good.get("HAS_CHECKED") != "TRUE") {
						alert("请先校验资源!");
						return false;
					}else if (good.get("RES_CODE") == "-1") {
						alert("资源数据丢失,请重新输入检验!");
						good.put("HAS_CHECKED","FALSE") // 设置实物为未校验状态
						var elemKey = good.get("ELEM_KEY");
						var elemKeys = elemKey.split("_");
						var ResTextId=$(elemKeys[0]+"_"+elemKeys[1]+"_RES_CODE");
						ResTextId.attr('disabled', false);
						return false;
					}
				}
			}
		    return true;
		},
		showAttr: function(eventObj){
			var obj = $(eventObj);
			var element = saleactiveModule.spGetElem(obj.attr("checkboxId"));
			if(element == null) 
			{
				return;
			}
			
			var params = "&ELEMENT_ID="+obj.attr("elementId")+"&ELEMENT_TYPE_CODE="+obj.attr("elementTypeCode")+"&ITEM_INDEX="+obj.attr("checkboxId");
			$.ajax.submit(null,null,params,'saleElementAttr', function(){saleactiveModule.afterShowAttr(eventObj)});
		},
		afterShowAttr: function(eventObj){
			var obj = $(eventObj);
			var element = saleactiveModule.spGetElem(obj.attr("checkboxId"));
			var attrs = element.get("ATTR_PARAM");
			if(typeof(attrs) != 'undefined' && attrs.length > 0)
			{
				var length = attrs.length;
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					$("#"+attrCode).val(attrValue);
				}
				
			}
			var topAdd = 0;
			var scroll =  $("div[class=m_wrapper]:first");
			if(scroll.length>0){
				topAdd = scroll.attr("scrollTop");
			}
			var o = $(eventObj).offset();
			$("#elementPanel").css("top", (o.top+obj.height()+topAdd) + "px");
			$("#elementPanel").css("left", (o.left+obj.width()-$("#elementPanel").width()) + "px");
			$("#elementPanel").css("display","");
		},
		hideAttr: function(){
			$("#elementPanel").css("display","none");
		},
		confirmAttr: function(itemIndex) {
			var element = saleactiveModule.spGetElem(itemIndex);
			var attrs = $.DatasetList();
			$("#elementPanel select").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attrs.add(attr);
			});
			$("#elementPanel input").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attrs.add(attr);
			});
			element.put("ATTR_PARAM", attrs);
			$("#elementPanel").css("display","none");
		},
		showDateChoice: function(eventObj){
			var obj = $(eventObj);
			
			var params = "&ITEM_INDEX="+obj.attr("checkboxId");
			var elem = this.spGetElem(obj.attr("checkboxId"));
			if(elem == null)
			{
				alert('该元素还没有被选上');
				return;
			}
			var editMode = obj.attr('editMode');
			if(editMode == '1')
			{
				var checkObj = $('#'+obj.attr('checkboxId'));
				var nowDay = checkObj.attr('nowDay');
				var nextAcctDay = checkObj.attr('nextAcctDay');
				params += '&NOW_DAY=' + nowDay;
				params += '&NEXT_ACCT_DAY=' + nextAcctDay;
			}
			else if(editMode == '2')
			{
				params += '&END_DATE=' + elem.get('END_DATE');
			}
			params += '&EDIT_MODE=' + editMode;
			
			$.ajax.submit(null,null,params,'saleElementDate',function(){saleactiveModule.afterShowDateChoice(eventObj)});
		},
		afterShowDateChoice: function(eventObj){
			var obj = $(eventObj);
			var topAdd = 0;
			var scroll =  $("div[class=m_wrapper]:first");
			if(scroll.length>0){
				topAdd = scroll.attr("scrollTop");
			}
			var o = $(eventObj).offset();
			$("#elementDatePanel").css("top", (o.top+obj.height()+topAdd) + "px");
			$("#elementDatePanel").css("left", (o.left+obj.width()-$("#elementDatePanel").width()) + "px");
			$("#elementDatePanel").css("display","");
		},
		confirmDateChoice: function(itemIndex){
			var choiceInfo = elementDateChoice.getChoiceInfo();
			var element = saleactiveModule.spGetElem(itemIndex);
			
			if(choiceInfo.get('START_DATE') != '')
			{
				var startDate = choiceInfo.get('START_DATE');
				element.put("START_DATE", startDate);
				$('#'+itemIndex+'_START_DATE').html(startDate);
			}
			if(choiceInfo.get('END_DATE') != '')
			{
				var endDate = choiceInfo.get('END_DATE');
				if(choiceInfo.get('END_DATE').length == 10)
				{
					endDate += ' 23:59:59';
				}
				element.put("END_DATE", endDate);
				$('#'+itemIndex+'_END_DATE').html(endDate);
			}
			
			$("#elementDatePanel").css("display","none");
		},
		disabledDepositPlusItem: function(cbid, flag) {
			var checkboxObj = $("#"+cbid);
			var giftUseTag = checkboxObj.attr('gift_use_tag');
			var depositType = checkboxObj.attr('deposit_type');
			if(depositType == '2')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_FEE";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
			if(giftUseTag == '1')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_DEPOSIT_USER_ID";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
		},
		/**
		 * 包展现时字段拼装已选上的元素
		 */
		spAutoAddCheckedElems: function(){
			gElems = $.DatasetList();
			$('#detailTitleName').text($('#SALEACTIVE_PACKAGE_NAME').val());
		
			// 优惠
			$("#SaleDiscntTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 服务
			$("#SaleServiceTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//预存赠送
			$("#SaleDepositTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//积分
			$("#SaleScoreTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//信用度
			$("#SaleCreditTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//平台业务
			$("#SalePlatSvcTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 实物
			$("#SaleGoodsTable input[type=checkbox]").each(function()
			{
				saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});
			//组合包
			$("#SaleCombineTable input[type=checkbox]").each(function()
			{
				saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});
		},
		spCheckBoxOnclickAction: function(cbId){
			var cb = $("#"+cbId);
			if (cb.attr("checked")) {
				saleactiveModule.spDecodeElem(cbId);
			} else {
				saleactiveModule.hideAttr();
				saleactiveModule.spDelElemById(cbId);
			}
		},
		/*拼装元素串*/
		spDecodeElem: function(cbId) {
			var param = new Wade.DataMap();
			var cb = $("#"+cbId);
			saleactiveModule.spDecodePubParam(param, cb);
			var elemType = cb.attr("element_type_code");
			if (elemType == 'S') {
				saleactiveModule.spDecodeServiceParam(param, cb);
			} else if (elemType == 'D') {
				saleactiveModule.spDecodeDiscntParam(param, cb);
			} else if (elemType == 'R') {
			} else if (elemType == 'Z') {
				saleactiveModule.spDecodePlatsvcParam(param, cb);
			} else if (elemType == 'A') {
				saleactiveModule.spDecodeDepositParam(param, cb);
			} else if (elemType == 'G') {
				saleactiveModule.spDecodeGoodsParam(param,cb);
			} else if (elemType == 'J') {
				saleactiveModule.spDecodeScoreParam(param, cb);
			} else if (elemType == 'C') {
				saleactiveModule.spDecodeCreditParam(param, cb);
			} else if (elemType == "K") {
			    saleactiveModule.spDecodeCombineParam(param, cb);
			}
		},
		
		/** 拼各类元素共有的参数 */
 		spDecodePubParam: function(param, elem) {
	
			param.put("ELEM_KEY", elem.attr("id"));
			
			param.put("ELEMENT_ID", elem.attr("element_id"));
			param.put("ELEMENT_TYPE_CODE", elem.attr("element_type_code"));
			param.put("MODIFY_TAG", "0");
			param.put("FORCE_TAG", elem.attr("forceTag"));
			param.put("DISABLED", elem.attr("disabled"));
			if(typeof(elem.attr('fee')) != 'undefined')
			{
				param.put("FEE", elem.attr("fee"));
				param.put("FEE_MODE", elem.attr("fee_mode"));
				param.put("FEE_TYPE_CODE", elem.attr("fee_type_code"));
				param.put("PAY_MODE", elem.attr("pay_mode"));
				param.put("IN_DEPOSIT_CODE", elem.attr('in_deposit_code'));
				param.put("OUT_DEPOSIT_CODE", elem.attr('out_deposit_code'));
				param.put('PAYMENT_ID', elem.attr('fee_type_code'));
			}
		},
		
		/*通过checkId删除一个元素*/
		spDelElemById: function(cbId) {
			var cb = $('#'+cbId);
			gElems.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					gElems.removeAt(index);
					saleactiveModule.spDelFeeItem(item);
					if(item.get('ELEMENT_TYPE_CODE') == 'A')
					{
						saleactiveModule.disabledDepositPlusItem(item.get("ELEM_KEY"), true);
					}
				}
			});
		},
		/*服务拼串*/
		spDecodeServiceParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.decodeAttrParam(param, elem)
			saleactiveModule.spAddElem(param);
		},
		/*优惠拼串*/
		spDecodeDiscntParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.decodeAttrParam(param, elem)
			saleactiveModule.spAddElem(param);
		},
		/*A元素拼串*/
		spDecodeDepositParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			param.put("SERIAL_NUMBER_B", elem.attr('serial_number_b'));
			param.put("GIFT_USER_ID", elem.attr('user_id_a'));
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		
			saleactiveModule.disabledDepositPlusItem(param.get('ELEM_KEY'), false);
		},
		/*平台业务拼串*/
		spDecodePlatsvcParam: function(param, elem) {
			param.put("ELEMENT_ID", elem.attr('element_id'));
			saleactiveModule.spAddElem(param);
		},
		
		/*实物拼串*/
		spDecodeGoodsParam: function(param,elem) {
			param.put("HAS_CHECKED",elem.attr("has_check"));
			if("S"==elem.attr("res_type_code"))
			{
			   var selectResCode = $("#SELECT_GOODS_"+elem.attr("index")+"_"+elem.attr("element_id")).val();
			   param.put("RES_CODE", selectResCode);
			}
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		
		/**构建积分数据 */
 		spDecodeScoreParam: function(param, elem) {
			param.put("SCORE_VALUE", $("#"+elem.attr('input_id')).val());//从积分输入框中取值
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		/**构建信用度数据 */
		spDecodeCreditParam: function(param, elem) {
			param.put("CREDIT_VALUE", elem.attr('credit_value'));
			param.put("CREDIT_GIFT_MONTHS", elem.attr('credit_gift_months'));
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		
		spDecodeCombineParam: function(param, elem){
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		/*添加元素费用*/
		spAddFeeItem: function(param) {
			var fee = param.get("FEE", ""); 
			var feeMode = param.get("FEE_MODE", ""); 
			var feeTypeCode = param.get("FEE_TYPE_CODE", ""); 
			var payMode = param.get("PAY_MODE", "");
			var elemType = param.get("ELEMENT_TYPE_CODE", "");
			if (fee == "" || feeMode == "" || feeTypeCode == "") {
				return false;
			}
			if (payMode == "1") {
				return true; 
			}
			//A元素的ELEMENT_ID需要记录到表里，后续要根据ELEMENT_ID找到ACTION_CODE(A_DISCNT_CODE)传到账务
			if(elemType == 'A')
			{
				saleactiveModule.insertFee($("#TRADE_TYPE_CODE").val(), param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('FEE'), param.get('ELEMENT_ID'));
			}
			else
			{
				saleactiveModule.insertFee($("#TRADE_TYPE_CODE").val(), param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('FEE'));
			}
		},
		/*构建元素的属性值*/
		decodeAttrParam: function(param, elem) {
			var attrInitParam = $("#"+$(elem).attr("id")+'_ATTR').val();
			if(typeof(attrInitParam) != 'undefined' && attrInitParam != '')
			{
				var attrs = $.DatasetList(attrInitParam);
				param.put("ATTR_PARAM", attrs);
			}
		},
		/*删除元素的费用*/
		spDelFeeItem: function(param) {
			if(param.get('ELEMENT_TYPE_CODE') == 'A')
			{
				$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(), param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('ELEMENT_ID'));
			}
			else
			{
				$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(), param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'));
			}
		},
		
		spAddElem: function(elem) {
			gElems.each(function(item, index, totalcount) {
						if (item.get("ELEM_KEY") == elem.get("ELEM_KEY")) {
							gElems.removeAt(index);
						}
					});
			gElems.add(elem);
		},
		spDelElem: function(elem) {
			gElems.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == elem.get("ELEM_KEY")) {
					gElems.removeAt(index);
					saleactiveModule.spDelFeeItem(item);
				}
			});
		},
		insertFee: function(tradeTypeCode, feeMode, feeTypeCode, fee, elementId) {
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
			feeData.put("MODE", feeMode);
			feeData.put("CODE", feeTypeCode);
			feeData.put("FEE", fee);
			if(typeof(elementId) != "undefined" && elementId != "")
			{
				feeData.put('ELEMENT_ID', elementId);
			}
			$.feeMgr.insertFee(feeData);
		},
		spCheckResInfo: function(obj){
			var button = $(obj);
			var inpt = $("#GOODS_"+button.attr("index")+"_RES_CODE");
			if(inpt.val() == ''){
				alert('不能为空');return;
			}
			if(inpt.attr("enterstafftag")=="1"){
				var saleStaff = $("#GOODS_"+button.attr("index")+"_STAFF_ID");
				if(saleStaff.val()==''){
					alert('请输入促销员工');return;
				}
			}
			
			// 设置实物为未校验
			saleactiveModule.spSetResCheckState("FALSE",inpt.attr("goodsId"));
			
			if($(inpt).val().length > 20){
				alert('串码最多20位,您输入的终端串码过长,请确认后输入!');return;
			}

			var checkResParam = '&RES_TYPE_CODE='+inpt.attr('resTypeCode')+"&RES_ID="+inpt.attr('resId')+"&RES_NO="+inpt.val();
			checkResParam += '&PRODUCT_ID='+$("#SALEACTIVE_PRODUCT_ID").val();
			checkResParam += '&PACKAGE_ID='+$("#SALEACTIVE_PACKAGE_ID").val();
			if($("#SALEACTIVE_OTHER_NUMBER").length > 0)
			{
				checkResParam += '&OTHERNET_TAG='+$("#SALEACTIVE_OTHERNET_TAG").val();
				checkResParam += '&OTHER_NUMBER='+$("#SALEACTIVE_OTHER_NUMBER").val();
			}
			checkResParam += '&SPEC_TAG=checkResInfo';
			checkResParam += "&EPARCHY_CODE=" + $('#SALEGOODS_EPARCHY_CODE').val();
			checkResParam += "&RES_CHECK="+inpt.attr('resCheck');
			checkResParam += "&SALE_STAFF_ID="+$("#GOODS_"+button.attr("index")+"_STAFF_ID").val();
			
			$.beginPageLoading("终端预占中。。。");	
				
			ajaxSubmit(null, null, checkResParam, $('#SALEGOODS_COMPONENT_ID').val(), 
				   function(d){
				      saleactiveModule.spAfterCheckResInfo(d, inpt);
				   },
			       function(errorcode, errorinfo){
				      $.endPageLoading();
				      $.showErrorMessage('终端预占失败',errorinfo);
				   }
			);
		},
		spSetResCheckState: function(state,goodsId){
			var goods = saleactiveModule.spGetGoodByGoodId(goodsId);
			if (goods) {
				goods.put("HAS_CHECKED", state);
				saleactiveModule.spAddElem(goods);
			}
		},
		spGetGoodByGoodId: function(goodsId){
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY").indexOf("GOODS")>=0&&item.get("ELEMENT_ID")==goodsId) {
					return item;
				}
			}
			return null;
		},
		
		spGetGoods: function(goodsId){
			var result = new $.DatasetList();
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY").indexOf("GOODS")>=0) {
					result.add(item);
				}
			}
			return result;
		},
		spAfterCheckResInfo: function(ajaxDataset, obj){
			$.endPageLoading();
			if (ajaxDataset.length <= 0) {
				alert("资源校验失败!");
				return false;
			}
			var tgoods = saleactiveModule.spGetGoodByGoodId(obj.attr("goodsId"));
			tgoods.put("HAS_CHECKED", "TRUE"); // 设置实物为已校验状态
			tgoods.put("RES_CODE", $(obj).val()); // 资源号码
			tgoods.put("SALE_STAFF_ID", $("#GOODS_"+obj.attr("index")+"_STAFF_ID"));
			saleactiveModule.spAddElem(tgoods);
			obj.attr("disabled","true");
			alert("资源校验成功");
		},
		selectGiftGoods : function(obj){
		    var jqueryObj = $(obj);
		    var tgoods = saleactiveModule.spGetGoodByGoodId(jqueryObj.attr("goodsId"));
		    if(tgoods){
		       tgoods.put("RES_CODE", jqueryObj.val());
		    }
		},
		/*根据checkId获取一个元素*/
		spGetElem: function(cbId) {
			var cb = $('#'+cbId);
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					return item;
				}
			}
			return null;
		},
		checkDepositGiftUser: function(obj){
			obj = $(obj);
			$("#"+obj.attr('chkboxid')).attr('user_id_a', '');
			$("#"+obj.attr('chkboxid')).attr('serial_number_b', '');
			var elem = saleactiveModule.spGetElem(obj.attr('chkboxid'));
			if(elem != null){
				elem.put('GIFT_USER_ID', '');
				elem.put('SERIAL_NUMBER_B', '');
			}
			
			if (obj.val() == ''){
				return false;
			}
			
			var userId = $('#SALEACTIVE_USER_ID').val();
			var param = '&USER_ID='+userId;
			param += '&GIFT_SERIAL_NUMBER='+obj.val();
			param += '&PRODUCT_ID='+$("#SALEACTIVE_PRODUCT_ID").val();
			param += "&PACKAGE_ID="+$("#SALEACTIVE_PACKAGE_ID").val();
			param += '&SPEC_TAG=checkDepositGiftUser';
			param += "&EPARCHY_CODE=" + $('#SALEDEPOSIT_EPARCHY_CODE').val();
			ajaxSubmit(null, null, param, $('#SALEDEPOSIT_COMPONENT_ID').val(),
				function(d) {saleactiveModule.afterCheckDepositGiftUser(d, obj)}, 
				function(rscode, rsinfo) {saleactiveModule.checkDepositGiftUserFail(rscode, rsinfo, obj)});
		},
		
	
		
		afterCheckDepositGiftUser: function(ajaxData, obj){
			var elem = saleactiveModule.spGetElem(obj.attr('chkboxid'));
			if(elem != null)
			{
			    var confirmSet = ajaxData.get("TIPS_TYPE_CHOICE");
				var warnSet = ajaxData.get("TIPS_TYPE_TIP");
				if(confirmSet && confirmSet.length>0){
					var flag = true;
					confirmSet.each(function(item, index, totalCount){
						if(!window.confirm(item.get("TIPS_INFO"))){
							flag = false;
							return false;
						}
					});
					if(!flag) return false;
				}
				if(warnSet && warnSet.length>0){
					warnSet.each(function(item, index, totalCount){
						window.alert(item.get("TIPS_INFO"));
					});
				}
				debugger;
				elem.put('GIFT_USER_ID', ajaxData.get(0).get('GIFT_USER_ID'));
				elem.put('SERIAL_NUMBER_B', obj.val());
				elem.put('GIFT_START_DATE', ajaxData.get(0).get('GIFT_START_DATE'));
				elem.put('GIFT_END_DATE', ajaxData.get(0).get('GIFT_END_DATE'));
				alert('赠送号码校验成功');
			}
		},
		checkDepositGiftUserFail: function(rscode, rsinfo, obj){
			obj.val('');	
			var elem = saleactiveModule.spGetElem(obj.attr('chkboxid'));
			elem.put("SERIAL_NUMBER_B", "");
			elem.put("GIFT_USER_ID", "");
			elem.put('GIFT_START_DATE',"");
			elem.put('GIFT_END_DATE',"");
			alert('赠送号码校验失败：'+rsinfo);
		},
		checkDepositMoney: function(obj){
			obj = $(obj);
			
			var oldValue = obj.attr('oldValue')/100;
			var feeMode = obj.attr('feeMode');
			var feeTypeCode = obj.attr('feeTypeCode');
			var elementId = obj.attr('elementId');
			if(obj.val() == ""){ 
				alert("预存不能为空");
				obj.val(oldValue);
				return false;
			}
			
			var deposit_high_value = obj.attr('deposit_high_value');
			var deposit_low_value  = obj.attr('deposit_low_value');
			var idepositfee		   = parseInt(obj.val())*100;
			
			if (idepositfee < parseInt(deposit_low_value) || idepositfee > parseInt(deposit_high_value)) {
				alert('输入的预存款必须在'+parseInt(deposit_low_value)/100 + '元和' + parseInt(deposit_high_value)/100 + '元之间');
				obj.val(oldValue);
				return false;
			}
			$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(), feeMode, feeTypeCode, elementId);
			
			saleactiveModule.insertFee($("#TRADE_TYPE_CODE").val(), feeMode, feeTypeCode, idepositfee, elementId);
			
			obj.attr('oldValue', parseInt(obj.val())*100);
			
			var checkboxId = obj.attr('chkboxid');
			var checkboxObj = $('#'+checkboxId);
			checkboxObj.attr('fee', parseInt(obj.val())*100);
			checkboxObj.attr('fee_mode', feeMode);
			checkboxObj.attr('fee_type_code', feeTypeCode);
			checkboxObj.attr('pay_mode', '0');
			
			var elem = saleactiveModule.spGetElem(obj.attr('chkboxid'));
			if(elem != null){
				elem.put('FEE', parseInt(obj.val())*100);
				elem.put('FEE_MODE', feeMode);
				elem.put('FEE_TYPE_CODE', feeTypeCode);
				elem.put('PAY_MODE', '0');
			}
			
			return true;
		},
		/** 积分修改 回车响应*/
		spScoreChanged: function(obj) {
			obj = $(obj);
			var checkboxObj = $("#"+obj.attr("cbId"));
			if (checkboxObj.attr("checked")) {
				saleactiveModule.spDelElemById(obj.attr("cbId"));
				saleactiveModule.spDecodeElem(obj.attr("cbId"));
			}
		}
	});
})();
	