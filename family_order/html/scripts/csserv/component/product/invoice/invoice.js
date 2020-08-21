if(typeof(invoice)=="undefined"){window["invoice"]=function(){};var Invoice = new invoice();}
(function(){
	$.extend(invoice.prototype,{
		_invoiceCode:null,//发票号
		_checkTag:false,//校验状态
		_otherSvcId:null,//处理其他的服务ID

		init:function(){
			Invoice._invoiceCode = null;
			Invoice._checkTag = false;
			$("#_INVOICE_CODE").attr("nullable","yes");
			$("#_INVOICE_CODE").val("");
			$("#_INVOICE_CODE_OLD").val("");
		},
		
		//设置已经存在的发票号 产品变更使用
		existsInvoicePopupOpen: function(invoiceCode){
			if(invoiceCode !='undefined' && invoiceCode !=null && invoiceCode !=''){//不等于空，直接弹出
				$("#_INVOICE_CODE").val(invoiceCode);
				$("#_INVOICE_CODE_OLD").val(invoiceCode);
				$("#_INVOICE_CODE").attr("disabled", true);
				Invoice._invoiceCode = invoiceCode;
				Invoice._checkTag = true;
				$("#_INVOICE_CODE").attr("nullable", "yes");
				$("#InvoicePopup").css("display","");
				return;
			}
		},
		
		//打开弹出框
		invoicePopupOpen: function(){
			if($("#_INVOICE_CODE").val().length <=0 && Invoice._checkTag == false){//当发票号存在时候且校验通过时候,不弹出
				var domObj = Invoice.getDomObj();
				if(domObj && domObj.checked)
				{
					Invoice.init();
					$("#_INVOICE_CODE").attr("nullable", "no");
					$("#InvoicePopup").css("display","");
				}
			}
		},
		
		//设置处理其他服务ID
		setOtherSvcId: function(otherSvcId){
			Invoice._otherSvcId = otherSvcId;
		},
		
		getSvcId: function(){
			var svcId = $("#SVC_ID").val();
			if(Invoice._otherSvcId != null && Invoice._otherSvcId != ''){
				svcId = Invoice._otherSvcId;
			}
			return svcId;
		},
		
		//点击取消按钮
		invoicePppupCancel: function(){
			$("#InvoicePopup").css("display","none");
			var domObj = Invoice.getDomObj();
			if(domObj && domObj.checked){
				domObj.checked = false;
				selectedElements.checkBoxAction(domObj);
				Invoice.init();
			}
		},
		
		//返回某个元素dom对象
		getDomObj:function()
		{
			return $("input[name='SELECTED_SVC_CHECKBOX'][value='"+Invoice.getSvcId()+"']").get(0);
		},
		
		//点击确定事件
		confirmAction: function(){
			if(Invoice._checkTag){
				$("#InvoicePopup").css("display","none");
				return;
			}else{
				var invoiceCode = $("#_INVOICE_CODE").val();
				if(invoiceCode == '' || invoiceCode == null){
					alert("发票号不能为空!");
					return false;
				}else{
					if(!$.validate.verifyField($("#_INVOICE_CODE"))){
						return false;
					}else{
						$.beginPageLoading("发票号校验中......");
						var params = "&INVOICE_CODE="+invoiceCode+"&CALL_SVC="+$("#CHECK_SVC").val();
						hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.invoice.InvoiceElementHandler","invoiceCheck", params, Invoice.afterConfirm, Invoice.errProcessReverse);
						$.endPageLoading();
					}
				}
			}
		},
		
		afterConfirm: function(data){
			if(data){
				var resultCode = data.get(0).get("RESULT_CODE");
				var resultInfo = data.get(0).get("RESULT_INFO");
				if(resultCode != "undefined" && resultCode != ''){
					if(resultCode !=0){
						alert(resultInfo);
						$("#_INVOICE_CODE").val("");
					}else{
						Invoice._checkTag = true;
						$("#InvoicePopup").css("display","none");

						//处理绑定事件,用于取消已经绑定的值
						var domObj = $("input[name='SELECTED_SVC_CHECKBOX'][value='"+Invoice.getSvcId()+"']");
						domObj.unbind("click", Invoice.init);//Invoice.init不能带括号否则报js错
						domObj.bind("click", Invoice.init);
					}
				}
			}
		},
		
		//处理错误
		errProcessReverse:function(errorCode, errorInfo){
			Invoice.invoicePppupCancel();
			$.MessageBox.error(errorCode, errorInfo);
			$.endPageLoading();
		}
	});
})();