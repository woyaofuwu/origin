/**批量预开户选产品,根据操作员eparch_code**/
function checkBeforeProduct()
{  
	var batchOperType = $('#cond_BATCH_OPER_TYPE').val();
	var tt = $("#EPARCHY_CODE").val();
	var productTypeCode = $("#PRODUCT_TYPE_CODE").find('option:selected').val();
	var productTypeName = $("#PRODUCT_TYPE_CODE").find('option:selected').text();
	var PRE_FLAG = $("#PRE_FLAG").val();
	var param = "&PRODUCT_TYPE_CODE="+productTypeCode;
	param += "&PRODUCT_TYPE_NAME="+productTypeName;
	param += getOtherParam();
	if("1" == PRE_FLAG || "6" == PRE_FLAG || "9" == PRE_FLAG){
		$.ajax.submit(null,'loadPersonProductsTreeForBatCreateuser',param,'',function(data){
			$.endPageLoading();
			var productIds = data.get("PRODUCT_IDS");
			if( batchOperType == 'CREATEPREUSER_M2M' ){
				ProductSelect.popupProductSelectYW(productTypeCode, productTypeName, tt ,'',productIds);
			}else{
				ProductSelect.popupProductSelect(productTypeCode, tt ,'','');
			}
			
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}else{
		ProductSelect.popupProductSelect(productTypeCode, tt ,'','');
	}
}

function afterChangeProduct(productId,productName,brandCode,brandName)
{
	$("#NEW_PRODUCT_ID").val(productId);
	$("#BRAND").val(brandName);
	$("#PRODUCT_NAME").val(productName);
	//$("#CUST_NAME").val($("#PRODUCT_TYPE_CODE :selected").text());
	if("MOSP" == brandCode){
		$("#CUST_NAME").val("和多号虚拟副号");    //和多号平台要求客户名称填充成“和多号虚拟副号”  add tanjl
	}
	var param = "&NEW_PRODUCT_ID="+productId+"&BATCH_OPER_TYPE=" + $("#cond_BATCH_OPER_TYPE").val();
	offerList.renderComponent($("#NEW_PRODUCT_ID").val(),$("#EPARCHY_CODE").val(),param);
	//pkgElementList.initElementList(null);
	selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
}

function setBrandCode(){
	if($("#PRODUCT_TYPE_CODE").val()!=""){
	    $("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
	    initProduct();
	}else{
		$("#BRAND").val('');
	}	
}

/*初始化产品*/
function initProduct(){
	var param = "&BATCH_OPER_TYPE=" + $("#cond_BATCH_OPER_TYPE").val();
	offerList.renderComponent("",$("#EPARCHY_CODE").val(),param);
	//pkgElementList.initElementList(null);
	selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
	$("#PRODUCT_NAME").val('');
}

function disableElements(data){
   if(data){
     var temp = data.get(0);
     if(data.get(0).get("NEW_PRODUCT_START_DATE")){
		$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
	 }
   }
}

function productTypeChange(){
	setBrandCode();
}
