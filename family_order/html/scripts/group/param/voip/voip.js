var number = 1;
function initCrtUs() {
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == false) {
		$('#EFFECT_NOW').attr('checked',true);
		selectedElements.isEffectNow = true;
	}
	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs'){
	  	var chgFlag = $("#pam_NOTIN_CHANGE_DISABLED").val();
		if (chgFlag != null && chgFlag != "" && chgFlag == "true") {
			$("#pam_NOTIN_LINE_PRICE").attr("disabled", true);
			$("#pam_NOTIN_INSTALLATION_COST").attr("disabled", true);
			$("#pam_NOTIN_ONE_COST").attr("disabled", true);
		}
	}
}

function updateData() {
	//专线
    var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
    
    var lineNumberCode = $("#pam_NOTIN_LINE_NUMBER_CODE").val();
    
    var numberCode = Number(lineNumberCode);
    
    //专线宽带
    var lineBroad = $("#pam_NOTIN_LINE_BROADBAND").val();
    //专线实例号
    var lineInstance = $("#pam_NOTIN_LINE_INSTANCENUMBER").val();
    //业务标识
    var productNo = $("#pam_NOTIN_PRODUCT_NUMBER").val();
    //专线价格
    var linePrice = $("#pam_NOTIN_LINE_PRICE").val();
    
    
    //安装调试费
    var installCost = $("#pam_NOTIN_INSTALLATION_COST").val();
    // 专线一次性通信服务费
    var oneCost = $("#pam_NOTIN_ONE_COST").val();
    
    //专线
    if (lineNumberCode == ""){
		alert ("\u0020\u0020\u8BF7\u9009\u62E9\u4E13\u7F51");
		return false;
	}
    
    //专线宽带
    if (lineBroad == ""){
		alert ("请填写专线宽带！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			alert("专线宽带必须是整数");
			return false;
		}
	}
	//专线价格
	if (linePrice == ""){
		alert ("请填写专线价格！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(linePrice);
		if(!flag){
			alert("专线价格必须是整数");
			return false;
		}
	}

	//安装调试费
	if (installCost == ""){
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
	if (oneCost == ""){
		alert ("请填写专线一次性通信服务费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(oneCost);
		if(!flag){
			alert("专线一次性通信服务费必须是整数");
			return false;
		}
	}
    
    if(linePrice != "0" || installCost != "0" || oneCost != "0"){
		var hitInfo = "您录入的专线价格不是0元，提交后将开始按照录入的价格计费。是否继续保存修改?";
		var choose = confirm(hitInfo);
        if (choose == false) {
        	return false;
        }
	}
	
    var pamAttrList = $.table.get("VoipLineTable").getTableData(null,true);
	var rowData = $.table.get("VoipLineTable").getRowData();
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
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);
	$("#pam_NOTIN_PRODUCT_NUMBER").val(lineInstance);

	//获取编辑区的数据
	var datalineData = $.ajax.buildJsonData("voipLinePart");
	
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("VoipLineTable").updateRow(datalineData);
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val("");
	$("#pam_NOTIN_PRODUCT_NUMBER").val("");
	$("#pam_NOTIN_INSTALLATION_COST").val("");
	$("#pam_NOTIN_ONE_COST").val("");
	$("#pam_LINE_TRADE_NAME").val("");
}


function tableRowClick(){
	//获取选择行的数据
	 var rowData = $.table.get("VoipLineTable").getRowData();
	 $("#pam_NOTIN_LINE_NUMBER_CODE").val(rowData.get("pam_NOTIN_LINE_NUMBER_CODE"));
	 $("#pam_NOTIN_LINE_NUMBER").val(rowData.get("pam_NOTIN_LINE_NUMBER"));
	 $("#pam_NOTIN_LINE_BROADBAND").val(rowData.get("pam_NOTIN_LINE_BROADBAND"));
	 $("#pam_NOTIN_LINE_PRICE").val(rowData.get("pam_NOTIN_LINE_PRICE"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(rowData.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 $("#pam_NOTIN_PRODUCT_NUMBER").val(rowData.get("pam_NOTIN_PRODUCT_NUMBER"));
	 $("#pam_NOTIN_INSTALLATION_COST").val(rowData.get("pam_NOTIN_INSTALLATION_COST"));
	 $("#pam_NOTIN_ONE_COST").val(rowData.get("pam_NOTIN_ONE_COST"));
	 $("#pam_LINE_TRADE_NAME").val(rowData.get("pam_LINE_TRADE_NAME"));
}

function tableRowClickZJ(){
	 var rowData = $.table.get("ZJTable").getRowData();
	 $("#pam_NOTIN_ZJ_NUMBER").val(rowData.get("pam_NOTIN_ZJ_NUMBER"));
	 $("#pam_NOTIN_ZJ_TYPE").val(rowData.get("pam_NOTIN_ZJ_TYPE"));
	 $("#pam_NOTIN_TYPE_NAME").val(rowData.get("pam_NOTIN_TYPE_NAME"));
	 $("#pam_NOTIN_SUPER_NUMBER").val(rowData.get("pam_NOTIN_SUPER_NUMBER"));
	 $("#pam_NOTIN_OLD_ZJ_NUMBER").val(rowData.get("pam_NOTIN_OLD_ZJ_NUMBER"));
}


function createZJ(){
    var zjType = $("#pam_NOTIN_ZJ_TYPE option:selected").text();
	$("#pam_NOTIN_TYPE_NAME").val(zjType);
	
	var zjNumber = $("#pam_NOTIN_ZJ_NUMBER").val();
	
	var rowData = $.table.get("ZJTable").getRowData();
    var rowZjNumber = rowData.get("pam_NOTIN_ZJ_NUMBER");
    var zjType = $("#pam_NOTIN_ZJ_TYPE").val();
	
	if(zjType == ""){
		alert ("请选择类型！");
		return false;
	}
    
    var pamZJlist  = $.table.get("ZJTable").getTableData(null,true);
    
	for(var i=0; i<pamZJlist.length ;i++){
		var attrs = pamZJlist.get(i);
		var attrsZjNumCode = attrs.get("pam_NOTIN_ZJ_NUMBER");
		var tag = attrs.get("tag");
		if(zjNumber == rowZjNumber){
			break ;
		}
		if(zjNumber == attrsZjNumCode && tag != 1){
			alert ("该中继号码已存在请重新添加！");
			return false;
		}
	}
	$("#pam_NOTIN_OLD_ZJ_NUMBER").val(zjNumber);
	var visplineData = $.ajax.buildJsonData("zjNumber");
	$.table.get("ZJTable").addRow(visplineData);
}

function updateZJ(){
 	var zjType = $("#pam_NOTIN_ZJ_TYPE option:selected").text();
	$("#pam_NOTIN_TYPE_NAME").val(zjType);
	
	var zjNumber = $("#pam_NOTIN_ZJ_NUMBER").val();
	var zjOldNumber = $("#pam_NOTIN_OLD_ZJ_NUMBER").val();
	var zjType = $("#pam_NOTIN_ZJ_TYPE").val();
	
	if(zjType == ""){
		alert ("请选择类型！");
		return false;
	}
		
	var rowData = $.table.get("ZJTable").getRowData();
    var rowZjNumber = rowData.get("pam_NOTIN_ZJ_NUMBER");
    
    var pamZJlist  = $.table.get("ZJTable").getTableData(null,true);
    
	for(var i=0; i<pamZJlist.length ;i++){
		var attrs = pamZJlist.get(i);
		var attrsZjNumCode = attrs.get("pam_NOTIN_ZJ_NUMBER");
		var tag = attrs.get("tag");
		if(zjNumber == rowZjNumber){
			break ;
		}
		if(zjNumber == attrsZjNumCode && tag != 1){
			alert ("该中继号码已存在请重新添加！");
			return false;
		}
	}
	
	//获取编辑区的数据
	$("#pam_NOTIN_OLD_ZJ_NUMBER").val(zjOldNumber);
	var datalineData = $.ajax.buildJsonData("zjNumber");
	
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("ZJTable").updateRow(datalineData);

}

function deleteZJ(){
	$.table.get("ZJTable").deleteRow();
}




function validateParamPage(methodName) {
	var pamAttr = $.table.get("VoipLineTable").getTableData(null, true);
	
	
	var pamAttrzj = $.table.get("ZJTable").getTableData(null, true);
	var oldPamAttrzj = $("#pam_NOTIN_OLD_ZJ_ATTR").val();
	
	if (pamAttrzj == "" || pamAttrzj == "[]"){
		alert ("请填写中继信息！");
		return false;
	}
	
	var attrListzj = new Wade.DatasetList();
	attrListzj = compareDatasetzj(pamAttrzj,oldPamAttrzj);
	
	$("#pam_NOTIN_AttrInternet").val(pamAttr);
	$("#pam_NOTIN_ZJ_ATTR").val(attrListzj);	
	
    return true;
}


function compareDatasetzj(strNewValue,strOldValue){
		if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue == "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('pam_NOTIN_OLD_ZJ_NUMBER') == newValueColumn.get('pam_NOTIN_OLD_ZJ_NUMBER')){
	        	if(newValueColumn.get('tag') == "2"){
	        		isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_NOTIN_OLD_ZJ_NUMBER') == newValueColumn.get('pam_NOTIN_OLD_ZJ_NUMBER'))
            {
            	isfound = "true";
            	break;
            }
        }
        if (isfound == "false") {
            oldValueColumn.put("tag","1");
            resultSet.add(oldValueColumn);
        }
   }
   
   return resultSet.toString();
}
