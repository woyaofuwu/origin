function init() {
	var activeType = $("#cond_ACTIVE_TYPE").val();
	if(activeType == '1'){
		$("#SaleCheckPart").css("display", "");
		$("#FeeCheckPart").css("display", "none");
		$("#BothWebActivePart").css("display", "none");
	}if(activeType == '2'){
		$("#SaleCheckPart").css("display", "none");
		$("#FeeCheckPart").css("display", "");
		$("#BothWebActivePart").css("display", "none");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_SERIAL_NUMBER_A").val("");
		$("#cond_SERIAL_NUMBER_BOTH").val("");
	}
	if(activeType == '9'){
		$("#SaleCheckPart").css("display", "none");
		$("#FeeCheckPart").css("display", "none");
		$("#BothWebActivePart").css("display", "");
		$("#cond_SERIAL_NUMBER").val("");
		$("#cond_SERIAL_NUMBER_A").val("");
		$("#cond_SERIAL_NUMBER_BOTH").val("");
	}		
}

/**
 * Ajax获取活动名称
 */
function queryCampnNames(obj) {
	if(obj.value == '1') {
		$("#SaleCheckPart").css("display", "");
		$("#FeeCheckPart").css("display", "none");
		$("#BothWebActivePart").css("display", "none");
	}else if(obj.value == '2'){
		$("#SaleCheckPart").css("display", "none");
		$("#FeeCheckPart").css("display", "");
		$("#BothWebActivePart").css("display", "none");
	} else if(obj.value == '9'){
		$("#SaleCheckPart").css("display", "none");
		$("#FeeCheckPart").css("display", "none");
		$("#BothWebActivePart").css("display", "");
	} else {
		$("#SaleCheckPart").css("display", "none");
		$("#FeeCheckPart").css("display", "none");
		$("#BothWebActivePart").css("display", "none");
	}
	$("#cond_SERIAL_NUMBER_IN").val("");
	$("#cond_SERIAL_NUMBER").val("");
	$("#cond_SERIAL_NUMBER_A").val("");
	$("#cond_SERIAL_NUMBER_BOTH").val("");
	$("#CUST_NAME").val("");
	$("#POST_CODE").val("");
	$("#PHONE").val("");
	$("#HOME_ADDRESS").val("");
	$("#BRAND").val("");
	$("#CITY_NAME").val("");
	$("#PRODUCT_NAME").val("");
	$("#OPEN_DATE").val("");
	$("#CUST_NAME_A").val("");
	$("#cond_SERIAL_NUMBER_B").val("");
	$("#IS_CHK_SERIAL_NUMBER").val("0");
	$("#cond_SERIAL_NUMBER_C").val("");
	$("#IS_CHK_SERIAL_NUMBER_C").val("0");
}

function checkUuInfo(){	
	$("#CUST_NAME").val("");
	$("#POST_CODE").val("");
	$("#PHONE").val("");
	$("#HOME_ADDRESS").val("");
	$("#BRAND").val("");
	$("#CITY_NAME").val("");
	$("#PRODUCT_NAME").val("");
	$("#OPEN_DATE").val("");
	$("#CUST_NAME_A").val("");
	
	var group = $("#cond_SERIAL_NUMBER_IN").val();
	var activeType = $("#cond_ACTIVE_TYPE").val();
	if (group == ""){
		alert ('请输入【被推荐号码】！');
		return false;
	}
	else {
		$.beginPageLoading("数据查询中......");
		$.ajax.submit(this, "checkUuInfo", "&cond_SERIAL_NUMBER_IN="+group+"&cond_ACTIVE_TYPE="+activeType, "groupCustinfoPage,groupVpnPart,groupUserinfoPage", function(data){
				$.endPageLoading();
				ajaxSubmitButton();
			},
			function(error_code, error_info, derror){
				$.endPageLoading();
//				showDetailErrorInfo(error_code,error_info,derror);
				$.showWarnMessage(error_code,error_info);
			}
		);
	}
}

function checkVpmnAndUuInfo() {	
	$("#CUST_NAME").val("");
	$("#POST_CODE").val("");
	$("#PHONE").val("");
	$("#HOME_ADDRESS").val("");
	$("#BRAND").val("");
	$("#CITY_NAME").val("");
	$("#PRODUCT_NAME").val("");
	$("#OPEN_DATE").val("");
	$("#CUST_NAME_A").val("");
	
	var group = $("#cond_SERIAL_NUMBER_BOTH").val();
	var activeType = $("#cond_ACTIVE_TYPE").val();
	if (group == "") {
		alert ('请输入【被推荐号码】！');
		return false;
	} else {
		$.beginPageLoading("数据查询中......");
		$.ajax.submit(this, "checkVpmnAndUuInfo", "&cond_SERIAL_NUMBER_BOTH="+group+"&cond_ACTIVE_TYPE="+activeType, "groupCustinfoPage,groupVpnPart,groupUserinfoPage", function(data){
				$.endPageLoading();
				ajaxSubmitButton();
			},
			function(error_code, error_info, derror){
				$.endPageLoading();
//				showDetailErrorInfo(error_code,error_info,derror);
				$.showWarnMessage(error_code,error_info);
			}
		);
	}
}


function ajaxSubmitButton() {
	var custNameA = $("#CUST_NAME_A").val();
	if(custNameA == '') {
		//$("#bsubmitDiv").css("display","none");	
	} else {
		//$("#bsubmitDiv").css("display","");	
		//$("#bsubmit").attr("disabledBtn","false");	
	}
}

function checkVpmn(){
	var activeType = $("#cond_ACTIVE_TYPE").val();
	if(activeType == "2") 
	{
		var group = $("#cond_SERIAL_NUMBER_IN").val();
		var serialnumberB = $("#cond_SERIAL_NUMBER_B").val();
//		getElement('CUST_NAME_A').value = '';
		if (serialnumberB != ""){
			$.beginPageLoading("数据检查中......");
			$.ajax.submit(this, "checkVpnInfo", "&cond_SERIAL_NUMBER_IN="+group+"&cond_SERIAL_NUMBER_A="+serialnumberB+"&cond_ACTIVE_TYPE="+activeType, "FeeCheckPart", function(data){
					$.endPageLoading();
					$("#IS_CHK_SERIAL_NUMBER").val("1");
					$.showSucMessage("【推荐号码】可以使用","","");
				},
				function(error_code, error_info, derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				}
			);
		}
	}
	else if(activeType == "9") 
	{
		var group = $("#cond_SERIAL_NUMBER_BOTH").val();
		var serialnumberC = $("#cond_SERIAL_NUMBER_C").val();
		if (serialnumberC == ""){
			alert ('请输入【推荐号码】！');
			return false;
		}
		else {
			$.beginPageLoading("数据检查中......");
			$.ajax.submit(this, "checkVpnInfo", "&cond_SERIAL_NUMBER_BOTH="+group+"&cond_SERIAL_NUMBER_A="+serialnumberC+"&cond_ACTIVE_TYPE="+activeType, "BothWebActivePart", function(data){
					$.endPageLoading();
					$("#IS_CHK_SERIAL_NUMBER_C").val("1");
					$.showSucMessage("【推荐号码】可以使用","","");
				},
				function(error_code, error_info, derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				}
			);
		}
	}
}

function onSubmitBaseTradeCheck() {
	var serialnumberB = $("#cond_SERIAL_NUMBER_B").val();
	var isChkSN = $("#IS_CHK_SERIAL_NUMBER").val();
	var serialnumberC = $("#cond_SERIAL_NUMBER_C").val();
	var isChkSNC = $("#IS_CHK_SERIAL_NUMBER_C").val();
	var activeType = $("#cond_ACTIVE_TYPE").val();
	var custNameA = $("#CUST_NAME_A").val();
	if(activeType == "2") {
		var serial_number_in = $("#cond_SERIAL_NUMBER_IN").val();
		if(serial_number_in == "") {
			alert("请输入【被推荐号码】");
			return false;
		} else if(custNameA == "") {
			alert("请校验【被推荐号码】是否可用！");
			return false;
		} else if(isChkSN != "1" && serialnumberB != "") {
			alert("请校验【推荐号码】是否可用！");
			return false;
		}
	} else if(activeType == "9") {
		var serial_number_both = $("#cond_SERIAL_NUMBER_BOTH").val();
		if(serial_number_both == "") {
			alert("请输入【被推荐号码】");
			return false;
		} else if(custNameA == "") {
			alert("请校验【被推荐号码】是否可用！");
			return false;
		} else if(serialnumberC == "") {
			alert("请输入【推荐号码】");
			return false;
		} else if(isChkSNC != "1") {
			alert("请校验【推荐号码】是否可用！");
			return false;
		}
	}
	return true;
}
