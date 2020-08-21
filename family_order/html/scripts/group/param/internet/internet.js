var number = 1;
function initCrtUs() {
	
	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs'){
	  	var chgFlag = $("#pam_NOTIN_CHANGE_DISABLED").val();
		if (chgFlag != null && chgFlag != "" && chgFlag == "true") {
			$("#pam_NOTIN_LINE_PRICE").attr("disabled", true);
			$("#pam_NOTIN_IP_PRICE").attr("disabled", true);
			$("#pam_NOTIN_INSTALLATION_COST").attr("disabled", true);
			$("#pam_NOTIN_ONE_COST").attr("disabled", true);
			$("#pam_NOTIN_SOFTWARE_PRICE").attr("disabled", true);
			$("#pam_NOTIN_NET_PRICE").attr("disabled", true);
		}
	}
}

function validateParamPage(methodName) {
	var pamAttr = $.table.get("InternetTable").getTableData(null, true);
	$("#pam_NOTIN_AttrInternet").val(pamAttr);
    return true;
}

function updateData() {
	//专线
    var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
    var lineNumberCode = $("#pam_NOTIN_LINE_NUMBER_CODE").val();
    var numberCode = Number(lineNumberCode);
    
    //专线宽带
    var lineBroad = $("#pam_NOTIN_LINE_BROADBAND").val();
    //专线实例号
//    var lineInstance = $("#pam_NOTIN_LINE_INSTANCENUMBER").val();
    //业务标识
//    var productNo = $("#pam_NOTIN_PRODUCT_NUMBER").val();
    //专线价格
    var linePrice = $("#pam_NOTIN_LINE_PRICE").val();
    //IP地址使用费
    var ipPrice = $("#pam_NOTIN_IP_PRICE").val();
    //安装调试费
    var installCost = $("#pam_NOTIN_INSTALLATION_COST").val();
    // 专线一次性通信服务费
    var oneCost = $("#pam_NOTIN_ONE_COST").val();
   
    
    //专线宽带
    if (lineNumberCode == "" || lineNumberCode == null){
		alert ("请选择专线！");
		return false;
	}
    
    //专线宽带
    if (lineBroad == ""){
		alert ("请填写专线宽带！");
		return false;
	}
	
	//专线价格
	if (linePrice == ""){
		alert ("请填写专线价格！");
		return false;
	}
	//IP地址使用费
	if (ipPrice == ""){
		alert ("请填写IP地址使用费！");
		return false;
	}
	//安装调试费
	if (installCost == "" || installCost == null ){
		alert ("请填写安装调试费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(installCost);
		if(!flag){
			alert("安装调试费必须是整数");
			return false;
		}
	}
	//专线一次性通信服务费
	if (oneCost == "" || oneCost == null ){
		alert ("请填写一次性通信服务费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(oneCost);
		if(!flag){
			alert("一次性通信服务费必须是整数");
			return false;
		}
	}
	
	if(linePrice != "0" || ipPrice != "0" || installCost != "0" || oneCost != "0"){
		var hitInfo = "您录入的专线价格不是0元，提交后将开始按照录入的价格计费。是否继续保存修改?";
		var choose = confirm(hitInfo);
        if (choose == false) {
        	return false;
        }
	}
	
	var pamAttrList = $.table.get("InternetTable").getTableData(null,true);
   	var rowData = $.table.get("InternetTable").getRowData();
    var rowNumber = rowData.get("pam_NOTIN_LINE_NUMBER_CODE");
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_NOTIN_LINE_NUMBER_CODE");
			var tag = attrs.get("tag");
			if(rowNumber == numberCode){
				break ;
			}
			if(numberCode == attrsNumCode && tag != 1){
				alert ("该专线已添加请重新选择！");
				return false;
			}
		}
	}
    
    
	$("#pam_NOTIN_LINE_NUMBER_CODE").val(numberCode);
	$("#pam_NOTIN_LINE_NUMBER").val(lineNumber);
	//$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);
	//获取编辑区的数据
	var visplineData = $.ajax.buildJsonData("internetPart");
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("InternetTable").updateRow(visplineData);
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	$("#pam_NOTIN_IP_PRICE").val("");
	$("#pam_NOTIN_INSTALLATION_COST").val("");
	$("#pam_NOTIN_ONE_COST").val("");
	$("#pam_LINE_TRADE_NAME").val("");
}


function tableRowClick(){
	//获取选择行的数据
	 var rowData = $.table.get("InternetTable").getRowData();
	 $("#pam_NOTIN_LINE_NUMBER_CODE").val(rowData.get("pam_NOTIN_LINE_NUMBER_CODE"));
	 $("#pam_NOTIN_LINE_NUMBER").val(rowData.get("pam_NOTIN_LINE_NUMBER"));
	 $("#pam_NOTIN_LINE_BROADBAND").val(rowData.get("pam_NOTIN_LINE_BROADBAND"));
	 $("#pam_NOTIN_LINE_PRICE").val(rowData.get("pam_NOTIN_LINE_PRICE"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(rowData.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 $("#pam_NOTIN_PRODUCT_NUMBER").val(rowData.get("pam_NOTIN_PRODUCT_NUMBER"));
	 $("#pam_NOTIN_IP_PRICE").val(rowData.get("pam_NOTIN_IP_PRICE"));
	 $("#pam_NOTIN_INSTALLATION_COST").val(rowData.get("pam_NOTIN_INSTALLATION_COST"));
	 $("#pam_NOTIN_ONE_COST").val(rowData.get("pam_NOTIN_ONE_COST"));
	 $("#pam_LINE_TRADE_NAME").val(rowData.get("pam_LINE_TRADE_NAME"));
	 $("#pam_NOTIN_SOFTWARE_PRICE").val(rowData.get("pam_NOTIN_SOFTWARE_PRICE"));
	 $("#pam_NOTIN_NET_PRICE").val(rowData.get("pam_NOTIN_NET_PRICE"));
}


