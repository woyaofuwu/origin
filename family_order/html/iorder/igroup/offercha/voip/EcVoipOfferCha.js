var number = 1;
var voipLineTable=new Wade.DatasetList(); 
var zjTable = new Wade.DatasetList();
function initPageParam_110000007010() {
	/*initCrtUs();*/
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["VoipLineTable"] = new Wade.Table("VoipLineTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#VoipLineTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(VoipLineTable.getSelectedRowData());
	});
	$.each(voipLineTable,function(index,data) { 
		VoipLineTable.addRow($.parseJSON(data.toString()));
	});
	
	window["ZJTable"] = new Wade.Table("ZJTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZJTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClickZJ(ZJTable.getSelectedRowData());
	});
	$.each(zjTable,function(index,data) { 
		ZJTable.addRow($.parseJSON(data.toString()));
	});
	
	if($("#VISP_INFO").val()!=""&&voipLineTable.length==0)
	{
		$.each(new Wade.DatasetList($("#VISP_INFO").val()),function(index,data) { 
			VoipLineTable.addRow($.parseJSON(data.toString()));
		});
	}
	
	if($("#VISP_INFO_ZJ").val()!=""&&zjTable.length==0)
	{
		$.each(new Wade.DatasetList($("#VISP_INFO_ZJ").val()),function(index,data) { 
			ZJTable.addRow($.parseJSON(data.toString()));
		});
	}
}

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
    //var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
	debugger;
    var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE").text();
    
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
		//alert ("\u0020\u0020\u8BF7\u9009\u62E9\u4E13\u7F51");
		$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请选择专网");
		return false;
	}
    
    //专线宽带
    if (lineBroad == ""){
		//alert ("请填写专线宽带！");
		$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "请填写专线宽带！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			//alert("专线宽带必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线宽带必须是整数");
			return false;
		}
	}
	//专线价格
	if (linePrice == ""){
		//alert ("请填写专线价格！");
		$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "请填写专线价格！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(linePrice);
		if(!flag){
			//alert("专线价格必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "专线价格必须是整数");
			return false;
		}
	}

	//安装调试费
	if (installCost == ""){
		//alert ("请填写安装调试费！");
		$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "请填写安装调试费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(installCost);
		if(!flag){
			//alert("安装调试费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_INSTALLATION_COST")[0], "安装调试费必须是整数");
			return false;
		}
	}
	 //专线一次性通信服务费
	if (oneCost == ""){
		//alert ("请填写专线一次性通信服务费！");
		$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "请填写专线一次性通信服务费！");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(oneCost);
		if(!flag){
			//alert("专线一次性通信服务费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "专线一次性通信服务费必须是整数");
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
	
    //var pamAttrList = $.table.get("VoipLineTable").getTableData(null,true);
    var pamAttrList = VoipLineTable.getData(true);
	//var rowData = $.table.get("VoipLineTable").getRowData();
    var rowData = VoipLineTable.getRowData(VoipLineTable.selected);
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
				//alert ("该专线已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "该专线已添加请重新选择！");
				return false;
			}
		}
	}
	
	
	/*$("#pam_NOTIN_LINE_NUMBER_CODE").val(numberCode);
	$("#pam_NOTIN_LINE_NUMBER").val(lineNumber);
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);
	$("#pam_NOTIN_PRODUCT_NUMBER").val(lineInstance);*/

	//获取编辑区的数据
	var datalineData = $.ajax.buildJsonData("voipLinePart");
	
	//往表格里添加一行并将编辑区数据绑定上
	//$.table.get("VoipLineTable").updateRow(datalineData);
	var voiplineData = $.DataMap(datalineData);
	voiplineData.put("#pam_NOTIN_LINE_NUMBER_CODE",numberCode);
	voiplineData.put("#pam_NOTIN_LINE_NUMBER",lineNumber);
	voiplineData.put("#pam_NOTIN_LINE_INSTANCENUMBER",lineInstance);
	voiplineData.put("#pam_NOTIN_PRODUCT_NUMBER",lineInstance);
	VoipLineTable.updateRow($.parseJSON(voiplineData.toString()),VoipLineTable.selected);
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val("");
	$("#pam_NOTIN_PRODUCT_NUMBER").val("");
	$("#pam_NOTIN_INSTALLATION_COST").val("");
	$("#pam_NOTIN_ONE_COST").val("");
	$("#pam_LINE_TRADE_NAME").val("");
}


function tableRowClick(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("VoipLineTable").getRowData();
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

function tableRowClickZJ(rowData){
	 //var rowData = $.table.get("ZJTable").getRowData();
	 $("#pam_NOTIN_ZJ_NUMBER").val(rowData.get("pam_NOTIN_ZJ_NUMBER"));
	 $("#pam_NOTIN_ZJ_TYPE").val(rowData.get("pam_NOTIN_ZJ_TYPE"));
	 $("#pam_NOTIN_TYPE_NAME").val(rowData.get("pam_NOTIN_TYPE_NAME"));
	 $("#pam_NOTIN_SUPER_NUMBER").val(rowData.get("pam_NOTIN_SUPER_NUMBER"));
	 $("#pam_NOTIN_OLD_ZJ_NUMBER").val(rowData.get("pam_NOTIN_OLD_ZJ_NUMBER"));
}


function createZJ(){
	debugger;
    //var zjType = $("#pam_NOTIN_ZJ_TYPE option:selected").text();
	var zjType = $("#pam_NOTIN_ZJ_TYPE").text();
	$("#pam_NOTIN_TYPE_NAME").val(zjType);
	
	var zjNumber = $("#pam_NOTIN_ZJ_NUMBER").val();
	
	//var rowData = $.table.get("ZJTable").getRowData();
	var rowData = ZJTable.getData();
    var rowZjNumber = rowData.get("pam_NOTIN_ZJ_NUMBER");
    var zjType = $("#pam_NOTIN_ZJ_TYPE").val();
	
	if(zjType == ""){
		//alert ("请选择类型！");
		$.validate.alerter.one($("#pam_NOTIN_ZJ_TYPE")[0], "请选择类型！");
		return false;
	}
    
    //var pamZJlist  = $.table.get("ZJTable").getTableData(null,true);
	var pamZJlist  = ZJTable.getData(true);
    
	for(var i=0; i<pamZJlist.length ;i++){
		var attrs = pamZJlist.get(i);
		var attrsZjNumCode = attrs.get("pam_NOTIN_ZJ_NUMBER");
		var tag = attrs.get("tag");
		if(zjNumber == rowZjNumber){
			break ;
		}
		if(zjNumber == attrsZjNumCode && tag != 1){
			//alert ("该中继号码已存在请重新添加！");
			$.validate.alerter.one($("#pam_NOTIN_ZJ_TYPE")[0], "该中继号码已存在请重新添加！");
			return false;
		}
	}
	$("#pam_NOTIN_OLD_ZJ_NUMBER").val(zjNumber);
	var visplineData = $.ajax.buildJsonData("zjNumber");
	//$.table.get("ZJTable").addRow(visplineData);
	var data = $.DataMap(visplineData);
	data.put("pam_NOTIN_TYPE_NAME",$("#pam_NOTIN_ZJ_TYPE").text());
	ZJTable.addRow($.parseJSON(data.toString()));
}

function updateZJ(){
 	//var zjType = $("#pam_NOTIN_ZJ_TYPE option:selected").text();
	var zjType = $("#pam_NOTIN_ZJ_TYPE").text();
	$("#pam_NOTIN_TYPE_NAME").val(zjType);
	
	var zjNumber = $("#pam_NOTIN_ZJ_NUMBER").val();
	var zjOldNumber = $("#pam_NOTIN_OLD_ZJ_NUMBER").val();
	var zjType = $("#pam_NOTIN_ZJ_TYPE").val();
	
	if(zjType == ""){
		//alert ("请选择类型！");
		$.validate.alerter.one($("#pam_NOTIN_ZJ_TYPE")[0], "请选择类型！");
		return false;
	}
		
	//var rowData = $.table.get("ZJTable").getRowData();
	var rowData = ZJTable.getRowData(ZJTable.selected);
    var rowZjNumber = rowData.get("pam_NOTIN_ZJ_NUMBER");
    
    //var pamZJlist  = $.table.get("ZJTable").getTableData(null,true);
    var pamZJlist  = ZJTable.getData(true);
    
	for(var i=0; i<pamZJlist.length ;i++){
		var attrs = pamZJlist.get(i);
		var attrsZjNumCode = attrs.get("pam_NOTIN_ZJ_NUMBER");
		var tag = attrs.get("tag");
		if(zjNumber == rowZjNumber){
			break ;
		}
		if(zjNumber == attrsZjNumCode && tag != 1){
			//alert ("该中继号码已存在请重新添加！");
			$.validate.alerter.one($("#pam_NOTIN_ZJ_TYPE")[0], "该中继号码已存在请重新添加！");
			return false;
		}
	}
	
	//获取编辑区的数据
	$("#pam_NOTIN_OLD_ZJ_NUMBER").val(zjOldNumber);
	var datalineData = $.ajax.buildJsonData("zjNumber");
	
	//往表格里添加一行并将编辑区数据绑定上
	//$.table.get("ZJTable").updateRow(datalineData);
	var data = $.DataMap(datalineData);
	data.put("pam_NOTIN_TYPE_NAME",$("#pam_NOTIN_ZJ_TYPE").text());
	ZJTable.updateRow($.parseJSON(data.toString()),ZJTable.selected);

}

function deleteZJ(){
	//$.table.get("ZJTable").deleteRow();
	ZJTable.deleteRow(ZJTable.selected);
}




function validateParamPage(methodName) {
	//var pamAttr = $.table.get("VoipLineTable").getTableData(null, true);
	var pamAttr = VoipLineTable.getData(true);
	
	
	//var pamAttrzj = $.table.get("ZJTable").getTableData(null, true);
	var pamAttrzj = ZJTable.getData(true);
	var oldPamAttrzj = $("#pam_NOTIN_OLD_ZJ_ATTR").val();
	
	if (pamAttrzj == "" || pamAttrzj == "[]"){
		//alert ("请填写中继信息！");
		$.validate.alerter.one($("#pam_NOTIN_ZJ_NUMBER")[0], "请填写中继信息！");
		return false;
	}
	
	var attrListzj = new Wade.DatasetList();
	attrListzj = compareDatasetzj(pamAttrzj,oldPamAttrzj);
	
	$("#pam_NOTIN_AttrInternet").val(pamAttr.toString());
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
//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	
	if(!submitOfferCha())
		return false; 
	
	voipLineTable=null;
	voipLineTable=VoipLineTable.getData(true);
	zjTable = null;
	zjTable = ZJTable.getData(true);
	backPopup(obj);
}