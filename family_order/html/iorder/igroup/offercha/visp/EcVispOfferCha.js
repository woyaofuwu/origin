var number = 1;
var table = new Wade.DatasetList();
function initPageParam_110000007013() {
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["VispLineTable"] = new Wade.Table("VispLineTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#VispLineTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(VispLineTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		VispLineTable.addRow($.parseJSON(data.toString()));
	});
	
	if($("#pam_NOTIN_OLD_AttrInternet").val()!=""&&table.length==0)
	{
		$.each(new Wade.DatasetList($("#pam_NOTIN_OLD_AttrInternet").val()),function(index,data) { 
			VispLineTable.addRow($.parseJSON(data.toString()));
		});
	}
	
	$("#VispLineTable tbody tr").attr("class","");//去掉背景色
}

function createData() {
	//专线
    //var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
    debugger;
    var lineNumberCode = $("#pam_NOTIN_LINE_NUMBER_CODE").val();
    
    var lineNumber=lineNumberCode;
    
    var numberCode = Number(lineNumberCode)+1;
    //专线宽带
    var lineBroad = $("#pam_NOTIN_LINE_BROADBAND").val();
    //专线价格
    var linePrice = $("#pam_NOTIN_LINE_PRICE").val();
    
    var maxnumberline = $("#pam_NOTIN_MAX_NUMBER_LINE").val();
    
    var numA = maxnumberline.substring(maxnumberline.length-3);
	var numB = Number(numA) + Number(number);
 
	if(Number(numB) >= 1000){
		//alert("\u5df2\u7ecf\u8d85\u8fc7\u4e13\u7ebf\u5e8f\u5217\u6761\u65701000\u6761");
		$.validate.alerter.one($("#VispLineTable")[0], "已经超过专线序列条数1000条");
		return false;
	}
	var indexA = "0000" + numB;
	var indexB = indexA.substring(indexA.length-3);
	var lineInstance = maxnumberline.substring(0,maxnumberline.length-3) + indexB;
	
    if (lineNumberCode == ""){
		//alert ("\u0020\u0020\u8BF7\u9009\u62E9\u4E13\u7F51");
    	$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请选择专网");
		return false;
	}
    if (lineBroad == ""){
		//alert ("\u8BF7\u8F93\u5165\u4E13\u7EBF\u5E26\u5BBD\uFF08\u5146\uFF09");
    	$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "请输入专线带宽（兆）");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			//alert("专线宽带必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线宽带必须是整数");
			return false;
		}
	}
	
	if (lineBroad == "0"){
		//alert ("\u4E13\u7EBF\u5E26\u5BBD\uFF08\u5146\uFF09\u4E0D\u80FD\u4E3A\u0030");
		$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线带宽（兆）不能为0");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			//alert("专线宽带必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线宽带必须是整数");
			return false;
		}
	}
	
	if (linePrice == ""){
		//alert ("\u8BF7\u8F93\u5165\u4E13\u7EBF\u4EF7\u683C\uFF08\u5143\uFF09");
		$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "请输入专线价格（元）");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(linePrice);
		if(!flag){
			//alert("专线价格必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "专线价格必须是整数");
			return false;
		}
	}
	
	if (linePrice % 10 != 0){
		//alert ("\u4E13\u7EBF\u4EF7\u683C\u5FC5\u987B\u662F\u0031\u0030\u7684\u500D\u6570");
		$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "专线价格必须是10的倍数");
		return false;
	}
	
	//var pamAttrList = $.table.get("VispLineTable").getTableData(null,true);
	var pamAttrList = VispLineTable.getData(true);
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_NOTIN_LINE_NUMBER_CODE");
			var attrsNum = attrs.get("pam_NOTIN_LINE_NUMBER");
			var tag = attrs.get("tag");
			if(numberCode == attrsNumCode && tag != 1){
				//alert ("关键字段'专线'已经存在同样的值"+attrsNum);
				$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "关键字段'专线'已经存在同样的值"+attrsNum);
				return false;
			}
		}
	}
	
	//$("#pam_NOTIN_LINE_NUMBER_CODE").val(numberCode);
	/*$("#pam_NOTIN_LINE_NUMBER").val(lineNumber);
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);*/
	
	//var visplineData = $.ajax.buildJsonData("vispLinePart");
	//$.table.get("VispLineTable").addRow(visplineData);
	var visplineData=new Wade.DataMap();
	visplineData.put("pam_NOTIN_LINE_NUMBER",$("#pam_NOTIN_LINE_NUMBER_CODE").text());
	visplineData.put("pam_NOTIN_LINE_NUMBER_CODE",numberCode);
	visplineData.put("pam_NOTIN_LINE_BROADBAND",lineBroad);
	visplineData.put("pam_NOTIN_LINE_PRICE",linePrice);
	visplineData.put("pam_NOTIN_LINE_INSTANCENUMBER",lineInstance);
	table.add(visplineData);
	
	VispLineTable.addRow($.parseJSON(visplineData.toString()));
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	number++;
	
	$("#VispLineTable tbody tr").attr("class","");//去掉背景色
}

function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}
function deleteData(){
	//$.table.get("VispLineTable").deleteRow();
	VispLineTable.deleteRow(VispLineTable.selected);
}

function getNewData(){
	//var data = $.table.get("VispLineTable").getTableData(null, true);
	var data = VispLineTable.getData(true);
	alert("获取全表数据:"+data);
}

function getOldData(){
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	alert(oldPamAttr.toString());
}


function validateParamPage(methodName) {
	//var pamAttr = $.table.get("VispLineTable").getTableData(null, true);
	var pamAttr = VispLineTable.getData();
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrInternet").val();
	if(methodName=="CrtUs"){
		if (pamAttr == "" || pamAttr == "[]"){
			//alert ("\u8BF7\u589E\u52A0\u4E13\u7EBF\uFF01");
			$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请增加专线！");
			return false;
		}
	}
	
	var attrList = new Wade.DatasetList();
	attrList = compareDataset(pamAttr,oldPamAttr);
	
	$("#pam_NOTIN_AttrInternet").val(attrList.toString());
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").attr("nullable", "yes");
	$("#pam_NOTIN_LINE_BROADBAND").attr("nullable", "yes");
	$("#pam_NOTIN_LINE_PRICE").attr("nullable", "yes");
	
    return true;
}

function updateData() {
	//专线
    //var lineNumber = $("#pam_NOTIN_LINE_NUMBER_CODE option:selected").text();
    
    var lineNumberCode = $("#pam_NOTIN_LINE_NUMBER_CODE").val();
    
    var lineNumber=lineNumberCode;
    
    var numberCode = Number(lineNumberCode)+1;
    //专线宽带
    var lineBroad = $("#pam_NOTIN_LINE_BROADBAND").val();
    //专线价格
    var linePrice = $("#pam_NOTIN_LINE_PRICE").val();
    
    var lineInstance = $("#pam_NOTIN_LINE_INSTANCENUMBER").val();
    
    
    if (lineNumberCode == ""){
		//alert ("\u0020\u0020\u8BF7\u9009\u62E9\u4E13\u7F51");
    	$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请选择专网");
		return false;
	}
	
    if (lineBroad == ""){
		//alert ("\u8BF7\u8F93\u5165\u4E13\u7EBF\u5E26\u5BBD\uFF08\u5146\uFF09");
		$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "请输入专线带宽（兆）");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			//alert("专线宽带必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线宽带必须是整数");
			return false;
		}
	}
	
	if (lineBroad == "0"){
		//alert ("\u4E13\u7EBF\u5E26\u5BBD\uFF08\u5146\uFF09\u4E0D\u80FD\u4E3A\u0030");
		$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线带宽（兆）不能为0");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(lineBroad);
		if(!flag){
			//alert("专线宽带必须是整数");
			$.validate.alerter.one($("#pam_NOTIN_LINE_BROADBAND")[0], "专线宽带必须是整数");
			return false;
		}
	}
	
	if (linePrice == ""){
		//alert ("\u8BF7\u8F93\u5165\u4E13\u7EBF\u4EF7\u683C\uFF08\u5143\uFF09");
		$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "请输入专线价格（元）");
		return false;
	}else{
		var flag = $.verifylib.checkPInteger(linePrice);
		if(!flag){
			alert("专线价格必须是整数");
			return false;
		}
	}
	
	if (linePrice % 10 != 0){
		//alert ("\u4E13\u7EBF\u4EF7\u683C\u5FC5\u987B\u662F\u0031\u0030\u7684\u500D\u6570");
		$.validate.alerter.one($("#pam_NOTIN_LINE_PRICE")[0], "专线价格必须是10的倍数");
		return false;
	}

	var checkedData =VispLineTable.getData().get(0);
	if(checkedData.get("pam_NOTIN_LINE_NUMBER_CODE")==($("#pam_NOTIN_LINE_NUMBER_CODE").val()-0+1+"")&&
		checkedData.get("pam_NOTIN_LINE_BROADBAND")==$("#pam_NOTIN_LINE_BROADBAND").val()&&
		checkedData.get("pam_NOTIN_LINE_PRICE")==$("#pam_NOTIN_LINE_PRICE").val()){
		$.validate.alerter.one($("#pam_NOTIN_LINE_NUMBER_CODE")[0], "请修改！");
		return false;
	}
	
	
	
	
    //var pamAttrList = $.table.get("VispLineTable").getTableData(null,true);
    var pamAttrList = VispLineTable.getData(true);
    
   	//var rowData = $.table.get("VispLineTable").getRowData();
    //var rowData = VispLineTable.getRowData();
    var rowData =VispLineTable.getSelectedRowData();
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
    
	//$("#pam_NOTIN_LINE_NUMBER_CODE").val(numberCode);
	/*$("#pam_NOTIN_LINE_NUMBER").val(lineNumber);
	$("#pam_NOTIN_LINE_INSTANCENUMBER").val(lineInstance);*/
	//获取编辑区的数据
	//var visplineData = $.ajax.buildJsonData("vispLinePart");
	//往表格里添加一行并将编辑区数据绑定上
	//$.table.get("VispLineTable").updateRow(visplineData);
	var visplineData=new Wade.DataMap();
	visplineData.put("pam_NOTIN_LINE_NUMBER",$("#pam_NOTIN_LINE_NUMBER_CODE").text());
	visplineData.put("pam_NOTIN_LINE_NUMBER_CODE",numberCode);
	visplineData.put("pam_NOTIN_LINE_BROADBAND",lineBroad);
	visplineData.put("pam_NOTIN_LINE_PRICE",linePrice);
	visplineData.put("pam_NOTIN_LINE_INSTANCENUMBER",lineInstance);
	VispLineTable.updateRow($.parseJSON(visplineData.toString()),VispLineTable.selected);
	
	$("#pam_NOTIN_LINE_NUMBER_CODE").val("");
	$("#pam_NOTIN_LINE_BROADBAND").val("");
	$("#pam_NOTIN_LINE_PRICE").val("");
	
}

function tableRowClick(data){
	//获取选择行的数据
	 //var rowData = $.table.get("VispLineTable").getRowData();
	 $("#pam_NOTIN_LINE_NUMBER_CODE").val(Number(data.get("pam_NOTIN_LINE_NUMBER_CODE"))-1+"");
	 $("#pam_NOTIN_LINE_NUMBER").val(data.get("pam_NOTIN_LINE_NUMBER"));
	 $("#pam_NOTIN_LINE_BROADBAND").val(data.get("pam_NOTIN_LINE_BROADBAND"));
	 $("#pam_NOTIN_LINE_PRICE").val(data.get("pam_NOTIN_LINE_PRICE"));
	 $("#pam_NOTIN_LINE_INSTANCENUMBER").val(data.get("pam_NOTIN_LINE_INSTANCENUMBER"));
	 
}



function compareDataset(strNewValue,strOldValue){
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue = "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('pam_NOTIN_LINE_NUMBER_CODE') == newValueColumn.get('pam_NOTIN_LINE_NUMBER_CODE')){
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_LINE_BROADBAND') == newValueColumn.get('pam_NOTIN_LINE_BROADBAND')&&
	        		oldValueColumn.get('pam_NOTIN_LINE_PRICE') == newValueColumn.get('pam_NOTIN_LINE_PRICE')){
	        		
	        		newValueColumn.put("tag","");
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        	var isfound = "true";
				resultSet.add(newValueColumn);
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        var isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_NOTIN_LINE_NUMBER_CODE') == newValueColumn.get('pam_NOTIN_LINE_NUMBER_CODE'))
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
	
	table = null;
	table = VispLineTable.getData(true);
	backPopup(obj);
}

