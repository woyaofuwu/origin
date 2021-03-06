//此JS是对changeproduct.js的扩展,一般适用于本地的特殊处理
if(typeof(ChangeProductExtend)=="undefined"){window["ChangeProductExtend"]=function(){};var changeProductExtend = new ChangeProductExtend();}
(function(){
	$.extend(ChangeProductExtend.prototype,{
		afterSubmitSerialNumber: function(data){
			$("#INVOICE_DATA").val("");
			$("#OLD_VPMN_DISCNT_NAME").val("");
			$("#NEW_VPMN_DISCNT").val("");
			$("#OLD_VPMN_DISCNT_TAG").val("");
			$("#OLD_VPMN_DISCNT").val("");
			$("#VPMN_USER_ID_A").val("");
			$("#VPMN_PRODUCT_ID").val("");
			$("#VIP_CLASS_ID").val("");
			$("#OLD_VPMN_DIV").css("display","none");
			$("#NEW_VPMN_DIV").css("display","none");
			$("#NEW_VPMN_DISCNT").attr("nullable","yes");
			$("#NEW_VPMN_DISCNT").empty();
			$("#BOOKING_DATE").val($("#SYS_DATE").val().substring(0,10));//重置预约时间为系统时间
			changeProductExtend.invoiceCodeInit();
		},
		
		afterLoadChildInfo: function(data){//海南设置VPMN以及押金发票等数据
			var oldVpmnDiscntTag = data.get(0).get("OLD_VPMN_DISCNT_TAG");
			var oldVpmnDiscnt = data.get(0).get("OLD_VPMN_DISCNT");
			var oldVpmnDiscntName = data.get(0).get("OLD_VPMN_DISCNT_NAME");
			var invoiceData = data.get(0).get("INVOICE_DATA");
			var bookingProduct = data.get(0).get("BOOKING_PRODUCT");
			var vpmnUserIdA = data.get(0).get("VPMN_USER_ID_A");
			var vpmnProductId = data.get(0).get("VPMN_PRODUCT_ID");
			var vipClassId = data.get(0).get("VIP_CLASS_ID");
			var nextProductId = data.get(0).get("NEXT_PRODUCT_ID");
			var bookingDatePriv = data.get(0).get("BOOKING_DATE_PRIV");
			
			if(typeof(oldVpmnDiscntTag)!="undefined" && oldVpmnDiscntTag!=null && oldVpmnDiscntTag!=''){
				$("#OLD_VPMN_DISCNT_TAG").val(oldVpmnDiscntTag);
				$("#OLD_VPMN_DIV").css("display","");
			}else{
				$("#OLD_VPMN_DIV").css("display","none");
			}
			if(typeof(oldVpmnDiscnt)!="undefined" && oldVpmnDiscnt!=null && oldVpmnDiscnt!=''){
				$("#OLD_VPMN_DISCNT").val(oldVpmnDiscnt);
			}
			if(typeof(oldVpmnDiscntName)!="undefined" && oldVpmnDiscntName!=null && oldVpmnDiscntName!=''){
				$("#OLD_VPMN_DISCNT_NAME").val(oldVpmnDiscntName);
			}
			if(typeof(invoiceData)!="undefined" && invoiceData!=null && invoiceData!=''){
				$("#INVOICE_DATA").val(invoiceData);
			}
			if(typeof(vpmnUserIdA)!="undefined" && vpmnUserIdA!=null && vpmnUserIdA!=''){
				$("#VPMN_USER_ID_A").val(vpmnUserIdA);
			}
			if(typeof(vpmnProductId)!="undefined" && vpmnProductId!=null && vpmnProductId!=''){
				$("#VPMN_PRODUCT_ID").val(vpmnProductId);
			}
			if(typeof(vipClassId)!="undefined" && vipClassId!=null && vipClassId!=''){
				$("#VIP_CLASS_ID").val(vipClassId);
			}
			if(typeof(bookingProduct)!="undefined" && bookingProduct!=null && bookingProduct!=''){
				if(bookingProduct == "TRUE"){
					$("#BOOKING_DATE").attr("disabled",true);
				}
				if(bookingProduct == "FALSE"){
					$("#BOOKING_DATE").attr("disabled", false);
				}
			}
			var netTypeCode = $("#NET_TYPE_CODE").val();//限制00类型的NET_TYPE_CODE的才能看见可选择框 在有权限情况下
			if(typeof(netTypeCode)!="undefined"&&netTypeCode!=null&&netTypeCode!=''&&netTypeCode == "00"){
				if(typeof(bookingDatePriv)!="undefined"&&bookingDatePriv!=null&&bookingDatePriv!=''&&bookingDatePriv == "TRUE"){
					$('#BOOKING_DATE_FIELD').attr("style", "");
				}else{
					$('#BOOKING_DATE_FIELD').attr("style", "display:none");
				}
				if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
					$('#BOOKING_DATE_FIELD').attr("style", "display:none");
				}
			}else{
				$('#BOOKING_DATE_FIELD').attr("style", "display:none");
			}
		},
		
		afterChangeProduct: function(productId){//获取新VPMN数据
			changeProductExtend.changeProductTipsInfo();
		},
		
		changeProductTipsInfo: function(){//产品变更时相关提示信息
			var newProductId = $("#NEW_PRODUCT_ID").val();
			var userProductId = $("#USER_PRODUCT_ID").val();
			var isRealName = $.auth.getAuthData().get('CUST_INFO').get('IS_REAL_NAME');
			
			var param = "&NEW_PRODUCT_ID="+newProductId+"&USER_PRODUCT_ID="+userProductId+"&IS_REAL_NAME="+isRealName;
			if(typeof(selectedElements.getOtherParam)=="function"){
				param += selectedElements.getOtherParam();
			}
			$.tradeCheck.addParam(param);
			$.tradeCheck.setListener('changeProductTipsInfo');
			$.tradeCheck.checkTrade('0', changeProductExtend.getNewVpmnDiscntBookProductDate);
		},
		
		getNewVpmnDiscntBookProductDate: function(){
			var oldVpmnDiscnt = $("#OLD_VPMN_DISCNT").val();
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var acctDay = $("#ACCT_DAY").val();
			var firstDate = $("#FIRST_DATE").val();
			var newProductId = $("#NEW_PRODUCT_ID").val();
			var userId = $("#USER_ID").val();
			var vpmnUserIdA = $("#VPMN_USER_ID_A").val();
			var vpmnProductId = $("#VPMN_PRODUCT_ID").val();
			var data = "&USER_ID="+userId+"&NEW_PRODUCT_ID="+newProductId+"&EPARCHY_CODE="+eparchyCode+"&OLD_VPMN_DISCNT="+oldVpmnDiscnt+"&ACCT_DAY="+acctDay+"&FIRST_DATE="+firstDate+"&VPMN_USER_ID_A="+vpmnUserIdA+"&VPMN_PRODUCT_ID="+vpmnProductId;
			if(typeof(selectedElements.getOtherParam)=="function"){
				data += selectedElements.getOtherParam();
			}
			$.ajax.submit(null,"getNewVpmnDiscntBookProductDate",data,'newVpmnPart',changeProductExtend.afterGetNewVpmnDiscntBookProductDate);
		},
		
		afterGetNewVpmnDiscntBookProductDate: function(data){
			var newVpmnDiscntTag = data.get(0).get("NEW_VPMN_DISCNT_TAG");
			var bookingProduct = data.get(0).get("BOOKING_PRODUCT");
			var bookDate = data.get(0).get("BOOKING_DATE");
			if(typeof(newVpmnDiscntTag)!="undefined" && newVpmnDiscntTag!=null && newVpmnDiscntTag!='' && newVpmnDiscntTag == 'TRUE'){
				$("#NEW_VPMN_DIV").css("display","");
				$("#NEW_VPMN_DISCNT").attr("nullable","no");
			}else{
				$("#NEW_VPMN_DIV").css("display","none");
				$("#NEW_VPMN_DISCNT").attr("nullable","yes");
			}
			if(typeof(bookingProduct)!="undefined" && bookingProduct!=null && bookingProduct!=''){
				if(bookingProduct == "TRUE"){
					$("#BOOKING_DATE").attr("disabled",true);
					$("#BOOKING_DATE").val(bookDate);
				}
				if(bookingProduct == "FALSE"){
					$("#BOOKING_DATE").attr("disabled", false);
				}
			}
			
			//产品变更，如果选择预约时间 需重新加载数据
			selectedElements.effectBookingDate();
		},
		
		dealInvoice:function(){//发票展示处理,费用处理在java侧
			$("#_INVOICE_CODE").attr("nullable","yes");
			$("#_INVOICE_CODE").attr("disabled",false);
			$("#_INVOICE_CODE_DIV").css("display","none");
			$.printMgr.setPrintParam("SUB_CONTENT", "");
			var fee = $.feeMgr.findFeeInfo("110", "1", "3");
			if(fee!=null && fee.get("PAY") > 0){//应缴费用 需要缴费才判断
				var length = selectedElements.selectedEls.length;
				if(length > 0){
					for(var i=0;i<length;i++){
						var temp = selectedElements.selectedEls.get(i);
						if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("ELEMENT_TYPE_CODE")=="S"){
							var staffId = $("#STAFF_ID").val();
							if(temp.get("MODIFY_TAG")=="0"){
								if(staffId.indexOf('HNYD') == -1){//10086客服客服工号不显示押金发票号码
									changeProductExtend.setInvoiceNullableNo();
									$.printMgr.setPrintParam("SUB_CONTENT", "提示:本押金不作为预存话费使用。为保证您的通话，请及时预存话费。若您手机欠费销号后，中国移动海南公司有权将本押金用于抵扣该手机所有欠费。");
								}
								break;
							}
							if(temp.get("MODIFY_TAG")=="exist" && temp.get("START_DATE") < "1998-09-18"){
								for(var j=0; j<selectedElements.selectedEls.length; j++){
									var temp1 = selectedElements.selectedEls.get(j);
									if(temp1.get("ELEMENT_ID")=="19" && temp1.get("MODIFY_TAG")=="0" && temp1.get("ELEMENT_TYPE_CODE")=="S"){
										if(staffId.indexOf('HNYD')==-1){//10086客服客服工号不显示押金发票号码
											changeProductExtend.setInvoiceNullableNo();
											$.printMgr.setPrintParam("SUB_CONTENT", "提示:本押金不作为预存话费使用。为保证您的通话，请及时预存话费。若您手机欠费销号后，中国移动海南公司有权将本押金用于抵扣该手机所有欠费。");
										}
										break;
									}
								}
								break;
							}
						}
					}
				}
			}else{
				changeProductExtend.invoiceCodeInit();
				var length = selectedElements.selectedEls.length;
				if(length > 0){
					for(var i=0;i<length;i++){
						var temp = selectedElements.selectedEls.get(i);
						if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("ELEMENT_TYPE_CODE")=="S" && temp.get("MODIFY_TAG")=="0"){
							var invoiceNum = changeProductExtend.getInvoiceAlready();
							if(invoiceNum != '0'){//发票号存在
								$("#_INVOICE_CODE").val(invoiceNum);
								$("#_INVOICE_CODE").attr("disabled",true);
								$("#_INVOICE_CODE_DIV").css("display","");
							}
							break;
						}
					}
				}
			}
		},
		
		dealInvoice2:function(){//发票展示处理,费用处理在java侧
			$("#_INVOICE_CODE").attr("nullable","yes");
			$("#_INVOICE_CODE").attr("disabled",false);
			$("#_INVOICE_CODE_DIV").css("display","none");
			$.printMgr.setPrintParam("SUB_CONTENT", "");
			var fee = $.feeMgr.findFeeInfo("3803", "1", "3");
			if(fee!=null && fee.get("PAY") > 0){//应缴费用 需要缴费才判断
				var length = selectedElements.selectedEls.length;
				if(length > 0){
					for(var i=0;i<length;i++){
						var temp = selectedElements.selectedEls.get(i);
						if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("ELEMENT_TYPE_CODE")=="S"){
							var staffId = $("#STAFF_ID").val();
							if(temp.get("MODIFY_TAG")=="0"){
								if(staffId.indexOf('HNYD') == -1){//10086客服客服工号不显示押金发票号码
									changeProductExtend.setInvoiceNullableNo();
									$.printMgr.setPrintParam("SUB_CONTENT", "提示:本押金不作为预存话费使用。为保证您的通话，请及时预存话费。若您固话欠费销号后，中国移动海南公司有权将本押金用于抵扣该固话所有欠费。");
								}
								break;
							}
						}
					}
				}
			}else{
				changeProductExtend.invoiceCodeInit();
				var length = selectedElements.selectedEls.length;
				if(length > 0){
					for(var i=0;i<length;i++){
						var temp = selectedElements.selectedEls.get(i);
						if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("ELEMENT_TYPE_CODE")=="S" && temp.get("MODIFY_TAG")=="0"){
							var invoiceNum = changeProductExtend.getInvoiceAlready();
							if(invoiceNum != '0'){//发票号存在
								$("#_INVOICE_CODE").val(invoiceNum);
								$("#_INVOICE_CODE").attr("disabled",true);
								$("#_INVOICE_CODE_DIV").css("display","");
							}
							break;
						}
					}
				}
			}
		},
		
		invoiceCodeInit:function(){
			$("#_INVOICE_CODE").val("");
			$("#_INVOICE_CODE").attr("nullable","yes");
			$("#_INVOICE_CODE").attr("disabled",false);
			$("#_INVOICE_CODE_DIV").css("display","none");
		},
		
		setInvoiceNullableNo:function(){
			$("#_INVOICE_CODE").attr("disabled",false);
			$("#_INVOICE_CODE").attr("nullable","no");
			$("#_INVOICE_CODE_DIV").css("display","");
		},
		
		getInvoiceAlready: function(){
			var invoiceData = $.DatasetList($("#INVOICE_DATA").val());
			var invoiceNum = "0";
			for(var i=0; i<invoiceData.length; i++){
				if(invoiceData.get(i, "RSRV_NUM1")=="3" && invoiceData.get(i, "RSRV_NUM2")=="80000"){
					invoiceNum = invoiceData.get(i, "SERIAL_NUMBER");
					break;
				}
			}
			return invoiceNum;
		},
		
		afterBookingDate: function(){
			if($("#BOOKING_DATE").val() != ""){
				if(typeof($("#NEW_PRODUCT_ID").val()) != "undefined" && $("#NEW_PRODUCT_ID").val() != null && $("#NEW_PRODUCT_ID").val() != ''){
					var bookingDate = $("#BOOKING_DATE").val();
					var sysDate = $("#SYS_DATE").val();
					if(bookingDate.substring(0,10) == sysDate.substring(0,10))
					{
						$("#OLD_PRODUCT_END_DATE").val(dateUtils.addSeconds(-1,sysDate));
						$("#NEW_PRODUCT_START_DATE").val(sysDate);
					}
					else
					{
						var oldProductEndDate = dateUtils.toString(dateUtils.toDate(dateUtils.addDays(-1, $("#BOOKING_DATE").val())), 'YYYY-MM-DD');
						$("#OLD_PRODUCT_END_DATE").val(oldProductEndDate);
						$("#NEW_PRODUCT_START_DATE").val($("#BOOKING_DATE").val());
					}
				}
				selectedElements.effectBookingDate();
			}
		},
		
		getOtherSubmitParam: function(){
			var data = '';
			if(typeof($("#BOOKING_DATE").val()) != "undefined" && $("#BOOKING_DATE").val() != null && $("#BOOKING_DATE").val() != ''){
				data+= "&BOOKING_DATE="+$("#BOOKING_DATE").val();
			}
			if(typeof($("#OLD_VPMN_DISCNT").val()) != "undefined" && $("#OLD_VPMN_DISCNT").val() != null && $("#OLD_VPMN_DISCNT").val() != ''){
				data+= "&OLD_VPMN_DISCNT="+$("#OLD_VPMN_DISCNT").val();
			}
			if(typeof($("#NEW_VPMN_DISCNT").val()) != "undefined" && $("#NEW_VPMN_DISCNT").val() != null && $("#NEW_VPMN_DISCNT").val() != ''){
				data+= "&NEW_VPMN_DISCNT="+$("#NEW_VPMN_DISCNT").val();
			}
			if(typeof($("#_INVOICE_CODE").val()) != "undefined" && $("#_INVOICE_CODE").val() != null && $("#_INVOICE_CODE").val() != ''){
				data+= "&INVOICE_CODE="+$("#_INVOICE_CODE").val();
			}
			if(typeof($("#_INVOICE_CODE_OLD").val()) != "undefined" && $("#_INVOICE_CODE_OLD").val() != null && $("#_INVOICE_CODE_OLD").val() != ''){
				data+= "&INVOICE_CODE_OLD="+$("#_INVOICE_CODE_OLD").val();
			}
			
			if(typeof($("#TRADE_TYPE_CODE").val()) != "undefined" && $("#TRADE_TYPE_CODE").val() != null && $("#TRADE_TYPE_CODE").val() == '3803'){
				data+= "&ORDER_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			}
			
			return data;
		}
	});
})();