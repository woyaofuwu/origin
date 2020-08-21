var number = 1;
var table=new Wade.DatasetList(); 
function initPageParam_110000007012() {
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["DataLineTable"] = new Wade.Table("DataLineTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#DataLineTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(DataLineTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		DataLineTable.addRow($.parseJSON(data.toString()));
	});
	
	if($("#VISP_INFO").val()!=""&&table.length==0)
	{
		$.each(new Wade.DatasetList($("#VISP_INFO").val()),function(index,data) { 
			DataLineTable.addRow($.parseJSON(data.toString()));
		});
	}
	
	$("#DataLineTable tbody tr").attr("class","");//去掉背景色
}

/*function initCrtUs() {

	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs'){
	  	var chgFlag = $("#pam_NOTIN_CHANGE_DISABLED").val();
		if (chgFlag != null && chgFlag != "" && chgFlag == "true") {
			$("#pam_NOTIN_LINE_PRICE").attr("disabled", true);
			$("#pam_NOTIN_INSTALLATION_COST").attr("disabled", true);
			$("#pam_NOTIN_ONE_COST").attr("disabled", true);
		}
	}
	
}*/

function validateParamPage(methodName) {
	var pamAttr = DataLineTable.getData(true);
	$("#pam_NOTIN_AttrInternet").val(pamAttr);
    return true;
}


function updateData() {
	//专线
    //var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
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
    
    //集团客户所在市县
    //var groupCity = $("#pam_NOTIN_GROUP_CITY option:selected").text();
    var groupCity = $("#pam_NOTIN_GROUP_CITY").text();
    var groupCityValue = $("#pam_NOTIN_GROUP_CITY").val();
    //A端所在市县
    //var aCity = $("#pam_NOTIN_A_CITY option:selected").text();
    var aCity = $("#pam_NOTIN_A_CITY").text();
    var aCityValue = $("#pam_NOTIN_A_CITY").val();
    
    //Z端所在市县
    //var zCity = $("#pam_NOTIN_Z_CITY option:selected").text();
    var zCity = $("#pam_NOTIN_Z_CITY").text();
    var zCityValue = $("#pam_NOTIN_Z_CITY").val();
    
    
    //集团所在市县分成比例
    var groupPercent = $("#pam_NOTIN_GROUP_PERCENT").val();
    //A端所在市县分成比例
    var aPercent = $("#pam_NOTIN_A_PERCENT").val();
    //Z端所在市县分成比例
    var zPercent = $("#pam_NOTIN_Z_PERCENT").val();
    
    
    //安装调试费
    var installCost = $("#pam_NOTIN_INSTALLATION_COST").val();
    // 专线一次性通信服务费
    var oneCost = $("#pam_NOTIN_ONE_COST").val();
    
     //专线宽带
    if (lineNumberCode == "" || lineNumberCode == null){
		//alert ("请选择专线！");
		$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请选择专线！");
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
	
	if(aCityValue == ""){
		//alert ("请选择A端所在市县！");
		$.validate.alerter.one($("#pam_NOTIN_A_CITY")[0], "请选择A端所在市县！");
		return false;
	}
	if(zCityValue == ""){
		//alert ("请选择Z端所在市县！");
		$.validate.alerter.one($("#pam_NOTIN_Z_CITY")[0], "请选择Z端所在市县！");
		return false;
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
			//alert("一次性通信服务费必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_ONE_COST")[0], "一次性通信服务费必须是整数");
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
	
	//var pamAttrList = $.table.get("DataLineTable").getTableData(null,true);
	var pamAttrList = DataLineTable.getData(true);
	//var rowData = $.table.get("DataLineTable").getRowData();
	var rowData = DataLineTable.getRowData(DataLineTable.selected);
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
	
	/*$("#pam_NOTIN_LINE_NUMBER").val(lineNumber);
	$("#pam_NOTIN_LINE_NUMBER_CODE").val(numberCode);
	$("#pam_NOTIN_GROUP_CITY_NAME").val(groupCity);
	$("#pam_NOTIN_GROUP_CITY").val(groupCityValue);
	$("#pam_NOTIN_A_CITY_NAME").val(aCity);
	$("#pam_NOTIN_A_CITY").val(aCityValue);
	$("#pam_NOTIN_Z_CITY_NAME").val(zCity);
	$("#pam_NOTIN_Z_CITY").val(zCityValue);*/
	
	var datalineData = $.ajax.buildJsonData("dataLinePart");
	//$.table.get("DataLineTable").updateRow(datalineData);
	var data = $.DataMap(datalineData);
	data.put("pam_NOTIN_LINE_NUMBER",lineNumber);
	data.put("pam_NOTIN_LINE_NUMBER_CODE",numberCode);
	data.put("pam_NOTIN_GROUP_CITY_NAME",groupCity);
	data.put("pam_NOTIN_GROUP_CITY",groupCityValue);
	data.put("pam_NOTIN_A_CITY_NAME",aCity);
	data.put("pam_NOTIN_A_CITY",aCityValue);
	data.put("pam_NOTIN_Z_CITY_NAME",zCity);
	data.put("pam_NOTIN_Z_CITY",zCityValue);
	DataLineTable.updateRow($.parseJSON(data.toString()),DataLineTable.selected);
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val("");
	$("#pam_NOTIN_PRODUCT_NUMBER").val("");
	$("#pam_NOTIN_A_CITY").val("");
	$("#pam_NOTIN_Z_CITY").val("");
	$("#pam_NOTIN_INSTALLATION_COST").val("");
	$("#pam_NOTIN_ONE_COST").val("");
	$("#pam_LINE_TRADE_NAME").val("");

}

function tableRowClick(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("DataLineTable").getRowData();
	 $("#pam_NOTIN_LINE_NUMBER_CODE").val(rowData.get("pam_NOTIN_LINE_NUMBER_CODE"));
	 $("#pam_NOTIN_LINE_BROADBAND").val(rowData.get("pam_NOTIN_LINE_BROADBAND"));
	 $("#pam_NOTIN_LINE_PRICE").val(rowData.get("pam_NOTIN_LINE_PRICE"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(rowData.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 $("#pam_NOTIN_PRODUCT_NUMBER").val(rowData.get("pam_NOTIN_PRODUCT_NUMBER"));
	 $("#pam_NOTIN_A_CITY").val(rowData.get("pam_NOTIN_A_CITY"));
	 $("#pam_NOTIN_Z_CITY").val(rowData.get("pam_NOTIN_Z_CITY"));
	 $("#pam_NOTIN_INSTALLATION_COST").val(rowData.get("pam_NOTIN_INSTALLATION_COST"));
	 $("#pam_NOTIN_ONE_COST").val(rowData.get("pam_NOTIN_ONE_COST"));
	 $("#pam_LINE_TRADE_NAME").val(rowData.get("pam_LINE_TRADE_NAME"));
}

//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	
	if(!submitOfferCha())
		return false; 
	
	table=null;
	table=DataLineTable.getData(true);
	backPopup(obj);
}


