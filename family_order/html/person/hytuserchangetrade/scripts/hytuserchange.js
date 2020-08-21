function refreshPartAtferAuth(data)
{ 	 
	var userId=data.get("USER_INFO").get("USER_ID"); 
	$.ajax.submit(this, 'checkIsHYTUser', '&USER_ID=' + userId, 'distinctInfoPart',
			function(data) {
				if (data.get("CODE") !='0000') {
					MessageBox.alert("告警提示","非海洋通用户不能办理该业务");
					$.cssubmit.disabledSubmitBtn(true);
				}else{
					$("#SHIP_ID").val(data.get("SHIP_ID"));
					$("#IS_SHIP_OWNER").find("option[value="+data.get("IS_SHIP_OWNER")+"]").attr("selected", true);
					
					appendDiscnt(data);
					$.cssubmit.disabledSubmitBtn(false);
				}
				$.endPageLoading();
			}, 
			function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			}
		);
	
}
function appendDiscnt(data){
	var list = data.get("DISCNT_LIST");
	$("#discntList").empty();
	 for(var j=0; j<list.length; j++){
		 
	     var textHtml = "<br/><li class = 'li col-1'><span class='label'><span>套餐编码：</span></span><span><input jwcid='@TextField'  " +
	     		" disabled='true' value="+list.get(j).get("DISCNT_CODE")+"></span></li>"+"<li class = 'li col-1'>"+
	 			"<span class='label'><span>套餐名称：</span></span>"+
				"<span><input jwcid='@TextField'   disabled='true' value="+list.get(j).get("DISCNT_NAME")+"></span></li>"+
				"<li class = 'li col-1'><span class='label'><span>开始时间：</span></span><span>"+
				"<input jwcid='@TextField'    disabled='true' value="+list.get(j).get("START_DATE")+"/></span></li>"+
				"<li class = 'li col-1'><span class='label'><span>结束时间：</span></span><span>"+
				"<input jwcid='@TextField'    disabled='true' value="+list.get(j).get("END_DATE")+"/></span></li>";
	     $.insertHtml('beforeend',$("#discntList") ,textHtml);
	    }
	
	
	
}



function checkValidDiscnt(){
	var isShipOwner = $("#IS_SHIP_OWNER").val();
	var shipId = $("#SHIP_ID").val();
	var discntCode = $("#DISCNT_CODE").val();
	if(shipId==""){
		MessageBox.alert("告警提示","船只编号为必填参数！");
		return ;
	}
	$.beginPageLoading("办理套餐校验中...");
	$.ajax.submit(this, 'checkValidDiscnt', '&IS_SHIP_OWNER=' + isShipOwner+'&SHIP_ID=' + shipId+'&DISCNT_CODE=' + discntCode, '',
		function(data) {


			if (data.get("CODE") !='0000') {
				MessageBox.alert("告警提示",data.get("MSG"));
				$.cssubmit.disabledSubmitBtn(true);
			}else{
				$("#IS_OWNER_DISCNT").val(data.get("IS_OWNER_DISCNT"));
				$("#DISCNT_NAME").val($("#DISCNT_CODE").find("option:selected").text());
				$.cssubmit.disabledSubmitBtn(false);
			}
			$.endPageLoading();
		}, 
		function(error_code, error_info, derror) {
			$.endPageLoading();
			
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	
}










function checkHSerialNumber() {
	var serialNumber = $("#SERIAL_NUMBER").val();
	var oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	
	if (null == serialNumber || '' == serialNumber)
	{
		alert("请先录入异网号码再进行校验！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	
	//已经校验过则不需要再次校验
	if (serialNumber == oldSerialNumber)
	{
		return;
	}
	
	$("#CHECK_RESULT_CODE").val("0");		//设置号码未校验
	$.custInfo.setSerialNumber(serialNumber);	//针对实名制，需要传递手机给组件

	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");
	$.beginPageLoading("异网号码校验中...");
	
	$.ajax.submit(this, 'checkHSerialNumber', '&SERIAL_NUMBER=' + serialNumber+'&OLD_SERIAL_NUMBER=' + oldSerialNumber, 'ProductInfoPart',
		function(data) {
			$("#CHECK_RESULT_CODE").val("1");		//设置号码校验通过
			$("#OLD_SERIAL_NUMBER").val(serialNumber);
			$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
			$.cssubmit.disabledSubmitBtn(false);

		 	var feeData = $.DataMap();
			feeData.clear();
			if (data.get("FEE_MODE") && data.get("FEE_TYPE_CODE") && data.get("FEE")) {
				feeData.put("MODE", data.get("FEE_MODE"));
				feeData.put("CODE", data.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data.get("FEE"));
				feeData.put("PAY",  data.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","7510");		
				$.feeMgr.insertFee(feeData);
			}
			$.endPageLoading();
		}, 
		function(error_code, error_info, derror) {
			$.endPageLoading();
			$("#CHECK_RESULT_CODE").val("0");		//设置号码未校验
			$("#OLD_SERIAL_NUMBER").val(serialNumber);
			$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

function changeProductType() {
	
	setBrandCode();
	
	var productType = $("#PRODUCT_TYPE_CODE").val();

	$.beginPageLoading("产品信息查询...");

	$.ajax.submit(this, 'getProductInfoByProductType', '&PRODUCT_TYPE='
			+ productType, 'productPart', function(data) {
		initProduct();
		$.endPageLoading();
	}, function(error_code, error_info) {
		$.endPageLoading();
		$.MessageBox.error(error_code, error_info);
	});
}

/** 设置用户产品品牌 */
function setBrandCode() {
	if ($("#PRODUCT_TYPE_CODE").val() != "") {
		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	} else {
		$("#BRAND").val('');
	}
}

function changeProduct() {
//	$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	var productId = $("#PRODUCT_ID").val();
	var eparchyCode = $("#EPARCHY_CODE").val();
	var param = "&NEW_PRODUCT_ID=" + productId;

	offerList.renderComponent(productId, eparchyCode);
	selectedElements.renderComponent(param, eparchyCode);
}

/** 产品初始化 */
function initProduct() {
	offerList.renderComponent("", $("#EPARCHY_CODE").val());
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", $("#EPARCHY_CODE").val());
}

function checkBeforeProduct(){
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
}

/* 密码组件前校验 */
function PasswdbeforeAction() {
	if($("#PSPT_TYPE_CODE").val() == "") {
		alert("证件类型不能为空！");
		return false;
	}
	if($("#PSPT_ID").val() == "") {
		alert("证件号码不能为空！");
		return false;
    }
     
    // 将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/* 密码组件后赋值 */
function PasswdafterAction(data) {
	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

function disableElements(data) {
	if (data) {
		var temp = data.get(0);
		if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
		}
	}
}

function onSubmit() {
	
	
	return true;
}
