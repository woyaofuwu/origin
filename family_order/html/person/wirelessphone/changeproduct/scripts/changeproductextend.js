//此JS是对changeproduct.js的扩展,一般适用于本地的特殊处理
if(typeof(ChangeProductExtend)=="undefined"){window["ChangeProductExtend"]=function(){};var changeProductExtend = new ChangeProductExtend();}
(function(){
	$.extend(ChangeProductExtend.prototype,{
		afterSubmitSerialNumber: function(data){
			$("#INVOICE_DATA").val("");
			$("#INVOICE_CODE").val("");
			$("#OLD_VPMN_DISCNT_NAME").val("");
			$("#NEW_VPMN_DISCNT").val("");
			$("#OLD_VPMN_DISCNT_TAG").val("");
			$("#OLD_VPMN_DISCNT").val("");
			$("#VPMN_USER_ID_A").val("");
			$("#VPMN_PRODUCT_ID").val("");
			$("#VIP_CLASS_ID").val("");
			$("#BOOKING_DATE").val($("#SYS_DATE").val());//重置预约时间为系统时间
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
			
			if(typeof(oldVpmnDiscntTag)!="undefined" && oldVpmnDiscntTag!=null && oldVpmnDiscntTag!=''){
				$("#OLD_VPMN_DISCNT_TAG").val(oldVpmnDiscntTag);
				$("#OLD_VPMN_DIV").css("display","block");
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
		},
		
		afterChangeProduct: function(productId){//获取新VPMN数据
			changeProductExtend.changeProductTipsInfo();
		},
		
		changeProductTipsInfo: function(){//产品变更时相关提示信息
			var newProductId = $("#NEW_PRODUCT_ID").val();
			var userProductId = $("#USER_PRODUCT_ID").val();
			var isRealName = $.auth.getAuthData().get('CUST_INFO').get('IS_REAL_NAME');
			
			var param = "&NEW_PRODUCT_ID="+newProductId+"&USER_PRODUCT_ID="+userProductId+"&IS_REAL_NAME="+isRealName;
			
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
			$.ajax.submit(null,"getNewVpmnDiscntBookProductDate","&USER_ID="+userId+"&NEW_PRODUCT_ID="+newProductId+"&EPARCHY_CODE="+eparchyCode+"&OLD_VPMN_DISCNT="+oldVpmnDiscnt+"&ACCT_DAY="+acctDay+"&FIRST_DATE="+firstDate+"&VPMN_USER_ID_A="+vpmnUserIdA+"&VPMN_PRODUCT_ID="+vpmnProductId,'newVpmnPart',changeProductExtend.afterGetNewVpmnDiscntBookProductDate);
		},
		
		afterGetNewVpmnDiscntBookProductDate: function(data){
			var newVpmnDiscntTag = data.get(0).get("NEW_VPMN_DISCNT_TAG");
			var bookingProduct = data.get(0).get("BOOKING_PRODUCT");
			var bookDate = data.get(0).get("BOOKING_DATE");
			if(typeof(newVpmnDiscntTag)!="undefined" && newVpmnDiscntTag!=null && newVpmnDiscntTag!='' && newVpmnDiscntTag == 'TRUE'){
				$("#NEW_VPMN_DIV").css("display","block");
				$("#NEW_VPMN_DISCNT").attr("nullable","no");
			}
			if(typeof(bookingProduct)!="undefined" && bookingProduct!=null && bookingProduct!=''){
				if(bookingProduct == "TRUE"){
					$("#BOOKING_DATE").attr("disabled",true);
					$("#BOOKING_DATE").val(bookDate);
				}
				if(bookingProduct == "FALSE"){
					$("#BOOKING_DATE").attr("disabled", false);
					//$("#BOOKING_DATE").val(bookDate);
				}
			}
		},
		
		dealInvoice:function(){
			var staffId = $("#STAFF_ID").val();
			var length = selectedElements.selectedEls.length;
			var invoiceNum = changeProductExtend.getInvoiceAlready();
			
			if(length > 0){
				for(var i=0;i<length;i++){
					var temp = selectedElements.selectedEls.get(i);
					if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="0" && temp.get("ELEMENT_TYPE_CODE")=="S"){
						if(invoiceNum != '0'){//发票号存在
							Invoice.existsInvoicePopupOpen(invoiceNum);
							//设置INVOICE组件费用参数
							//Invoice.setFeeParams("9733", temp.get("PRODUCT_ID"), temp.get("PACKAGE_ID"), "15", "S", "3", $("#EPARCHY_CODE").val(), $("#VIP_CLASS_ID").val());
							//changeProductExtend.addFeeList();//费用处理
						}else{//10086客服客服工号不显示押金发票号码
							if(staffId.indexOf('HNYD') == -1){
								Invoice.invoicePopupOpen();
								//设置INVOICE组件费用参数
								//Invoice.setFeeParams("9733", temp.get("PRODUCT_ID"), temp.get("PACKAGE_ID"), "15", "S", "3", $("#USER_EPARCHY_CODE").val(), $("#VIP_CLASS_ID").val());
								//此处未添加费用 疑问
								//Invoice.setDealFeeTag(false);
							}
						}
						break;
					}
					
					if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="EXIST" && temp.get("START_DATE") < "1998-09-18" && temp.get("ELEMENT_TYPE_CODE")=="S"){
						//changeProductExtend.delFeeList();
						for(var j=0; j<selectedElements.selectedEls.length; j++){
							var temp1 = selectedElements.selectedEls.get(j);
							if(temp1.get("ELEMENT_ID")=="19" && temp1.get("MODIFY_TAG")=="0" && temp1.get("ELEMENT_TYPE_CODE")=="S"){
								//changeProductExtend.addFeeList();//费用处理
								if(staffId.indexOf('HNYD')==-1){//10086客服客服工号不显示押金发票号码
									Invoice.setOtherSvcId("19");
									Invoice.invoicePopupOpen();
									//设置INVOICE组件费用参数
									//Invoice.setFeeParams("9733", temp.get("PRODUCT_ID"), temp.get("PACKAGE_ID"), "15", "S", "3", $("#EPARCHY_CODE").val(), $("#VIP_CLASS_ID").val());
									//Invoice.setDealFeeTag(true);
								}
								break;
							}
						}
						break;
					}
				}
			}
		},
		
		setInvoiceInfo: function(){//海南处理押金发票  
			$("#INVOICE_CODE_DIV").css("display","none");
			$("#INVOICE_CODE").attr("nullable","yes");
			var length = selectedElements.selectedEls.length;
			var invoiceNum = changeProductExtend.getInvoiceAlready();
			var staffId = $("#STAFF_ID").val();
			for(var i=0;i<length;i++){
				var temp = selectedElements.selectedEls.get(i);
				if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="0" && temp.get("ELEMENT_TYPE_CODE")=="S")
				{
					$("#INVOICE_CODE_DIV").css("display","block");
					$("#INVOICE_CODE").attr("nullable","no");
					
					if(invoiceNum != '0'){
						$("#INVOICE_CODE").val(invoiceNum);
						$("#INVOICE_CODE").attr("disabled",true);
						$("#INVOICE_CODE_DIV").css("display","block");
						
						changeProductExtend.delFeeList();
					}
					else//10086客服客服工号不显示押金发票号码
					{
						if(staffId.indexOf('HNYD')!=-1){
							$("#INVOICE_CODE_DIV").css("display","none");
						}
					}
					return;
				}
				
				if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="EXIST" && temp.get("START_DATE") < "1998-09-18" && temp.get("ELEMENT_TYPE_CODE")=="S")
				{
					changeProductExtend.delFeeList();
					
					for(var j=0; j<selectedElements.selectedEls.length; j++){
						var temp1 = selectedElements.selectedEls.get(j);
						if(temp1.get("ELEMENT_ID")=="19" && temp1.get("MODIFY_TAG")=="0" && temp1.get("ELEMENT_TYPE_CODE")=="S")
						{
							//费用处理
							changeProductExtend.addFeeList();
							
							if(staffId.indexOf('HNYD')==-1)
							{
								$("#INVOICE_CODE_DIV").css("display","block");
							}
							
							$("#INVOICE_CODE").attr("nullable","no");
							
							return;
						}
					}						
				}
			}
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
				var oldProductEndDate = dateUtils.toString(dateUtils.toDate(dateUtils.addDays(-1, $("#BOOKING_DATE").val())), 'YYYY-MM-DD');
				//$("#OLD_PRODUCT_END_DATE").val(oldProductEndDate);
			}
		},
		
		addFeeList: function(){
			var vipClass = $("#VIP_CLASS_ID").val();
			var fee = "80000";
			if(vipClass !=null && vipClass !='' && vipClass != ""){
				if(vipClass == "2" || vipClass == "1"){
					fee = "50000";
				}else{
					fee = "0";
				}
			}
		
			var feeData = $.DataMap();
			feeData.put("MODE", "1");
			feeData.put("CODE", "3");
			feeData.put("FEE",  fee);
			feeData.put("PAY",  fee);
			feeData.put("TRADE_TYPE_CODE","9733");				
			$.feeMgr.insertFee(feeData);
		},
		
		delFeeList: function(){
			$.feeMgr.removeFee("9733", "1", "3");
			/*---modify by chenzg@20140106---新增电子渠道办理国际漫游业务(REQ201309030003)---begin---*/
			$.feeMgr.removeFee("9733", "1", "5");//国际业务专款押金 疑问 TRADE_FEE_TYPE=5
			/*---modify by chenzg@20140106---新增电子渠道办理国际漫游业务(REQ201309030003)---end-----*/
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
			return data;
		}
	});
})();