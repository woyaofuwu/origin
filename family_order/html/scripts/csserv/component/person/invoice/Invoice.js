//全局变量，用于接收数据
var invoiceCode=null;			//发票号码
var flag="0";		        //校验标记 0为需要校验，1为不需要校验
/*发票号弹出框事件*/
function InvoiceAction(obj) {

     if($("#InvoiceSetFormPopup") && $("#InvoiceSetFormPopup").length){
	    $("#InvoiceSetFormPopup").css("display", "block");
	 }
	 if(!$("#InvoiceSetFormPopup") || !$("#InvoiceSetFormPopup").length){
			var html = [];
			html.push('<div id="InvoiceSetFormPopup" style="display:block;" class="c_popup"> ');
			html.push('<div class="c_popupWrapper"><div class="c_popupHeight"></div> ');
			html.push('<div class="c_popupBox" style="width:320px"><div class="c_popupTitle"> ');
			html.push('<div class="text">发票输入</div> ');
			html.push('<div class="fn"><a id="InvoiceSetCloseBtn" href="#nogo" class="close" onclick="onClickInvoiceCancel();"></a></div></div> ');
			html.push('<div class="c_popupContent"><div class="c_popupContentWrapper"> ');
			html.push('<div class="c_form c_form-col-1 c_form-label-8"> ');
			html.push('<ul class="ul"> ');
			
				html.push('<li class="li"><span class="label"><span class="e_required">发票号码：</span></span> ');
				html.push('<span class="e_input"><span> ');
				 if(flag=="1"){
				html.push('<input type="text" id="INVOICE_CODE_OLD" value="" desc="发票号码" nullable="no" datatype="pinteger"  disabled="true"  /> ');
				}
				else 
				html.push('<input type="text" id="INVOICE_CODE_NEW" value="" desc="发票号码" nullable="no" datatype="pinteger" /> ');
				html.push('</span></span></li> ');
			
			
			html.push('</ul></div> ');
			html.push('<div class="c_submit"> ');
			html.push('<button type="button" class="e_button-page-ok" id="PassSubmitBtn" onclick="onClickInvoiceSubmit();"><i></i><span>确定</span></button> ');
			 if(flag=="0"){
			html.push('<button type="button" class="e_button-page-cancel" id="PassCancelBtn" onclick="onClickInvoiceCancel();"><i></i><span>取消</span></button> ');
			html.push('<button type="button" class="e_button-page" id="PassResetBtn" onclick="onClickInvoiceReset();"><i class="e_ico-reset"></i><span>清空</span></button> ');
			}
			html.push('</div> ');
			html.push('</div></div><div class="c_popupBottom"><div></div></div> ');
			html.push('<div class="c_popupShadow"></div></div></div><iframe class="c_popupFrame"></iframe><div class="c_popupCover"></div> ');
			html.push('</div> ');
			
			$(document.body).append(html.join(""));
		}
		if(!obj){
			$("#INVOICE_CODE_OLD").val(invoiceCode);
		}else{
		$("#INVOICE_CODE_OLD").val(obj);
		}
		
      
}

function setInvoiceAttr(Code,tag){
			invoiceCode = Code;
			flag = tag;
			
		}

function getInvoiceAttr(){
			return invoiceCode;
			
		}		

//点击密码设置框提交按钮事件
function onClickInvoiceSubmit(){
if(flag=='0'){
			var param = "&INVOICE_CODE="+$("#INVOICE_CODE_NEW").val();
			$.beginPageLoading("校验发票号码。。。");
			$.httphandler.submit(null, "com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.invoice.Invoice", "checkInvoice", param, 
				function(data){
				invoiceCode=data.get(0).get("INVOICE_CODE");
					$.endPageLoading();
					if(data.get(0).get("RESULT_CODE")=="1")
					{
					 MessageBox.error("错误提示","发票号码"+data.get(0).get("INVOICE_CODE")+data.get(0).get("RESULT_INFO"));
					}
					if(data.get(0).get("RESULT_CODE")=="0"){
					   onClickInvoiceCancel();
					}
				});
          }
   if(flag=='1'){
					   onClickInvoiceCancel();
   }       
			
}

//取消按钮事件
			function onClickInvoiceCancel(){
			$("#INVOICE_CODE_OLD").val("");
			$("#INVOICE_CODE_NEW").val("");
			$("#InvoiceSetFormPopup").css("display", "none");
			}

//重设按钮事件
			function onClickInvoiceReset(){
				$("#INVOICE_CODE_OLD").val("");
			$("#INVOICE_CODE_NEW").val("");
			}




(Wade);