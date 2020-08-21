var wideTable = new Wade.DatasetList();
var fixedTable = new Wade.DatasetList();
var serialTable = new Wade.DatasetList();
function initPageParam_110000006710() {
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["WideTable"] = new Wade.Table("WideTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#WideTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClickWide(WideTable.getSelectedRowData());
	});
	$.each(wideTable,function(index,data) { 
		WideTable.addRow($.parseJSON(data.toString()));
	});
	
	window["FixedTable"] = new Wade.Table("FixedTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#FixedTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClickFixed(FixedTable.getSelectedRowData());
	});
	$.each(fixedTable,function(index,data) { 
		FixedTable.addRow($.parseJSON(data.toString()));
	});
	
	window["SerialTable"] = new Wade.Table("SerialTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#SerialTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClickSerial(SerialTable.getSelectedRowData());
	});
	$.each(serialTable,function(index,data) { 
		SerialTable.addRow($.parseJSON(data.toString()));
	});
	
	
	if(wideTable.length==0)
	{
		$.each(new Wade.DatasetList($("#WIDE_INFO").val()),function(index,data) { 
			WideTable.addRow($.parseJSON(data.toString()));
		});
	}
	if(fixedTable.length==0)
	{
		$.each(new Wade.DatasetList($("#FIXED_INFO").val()),function(index,data) { 
			FixedTable.addRow($.parseJSON(data.toString()));
		});
	}
	if(serialTable.length==0)
	{
		$.each(new Wade.DatasetList($("#SERIAL_INFO").val()),function(index,data) { 
			SerialTable.addRow($.parseJSON(data.toString()));
		});
	}
}	

function createWideData(){
	debugger;
	//宽带号码
	var acctId = $("#pam_NOTIN_WIDE_ACCT_ID").val();
	//宽带时长
	var month = $("#pam_NOTIN_WIDE_MONTH").val();
	//带宽
	var netLine = $("#pam_NOTIN_WIDE_NET_LINE").val();
	
	 //专线宽带
	if (acctId == ""){
		//alert ("请填写宽带号码！");
		$.validate.alerter.one($("#pam_NOTIN_WIDE_ACCT_ID")[0], "请填写宽带账号！");
		return false;
	}
	//专线实例号
    if (month == ""){
    	//alert ("请填写宽带时长！");
		$.validate.alerter.one($("#pam_NOTIN_WIDE_MONTH")[0], "请填写宽带时长！");
		return false;
	}
	//业务标识
	if (netLine == ""){
		//alert ("请填带宽！");
		$.validate.alerter.one($("#pam_NOTIN_WIDE_NET_LINE")[0], "请填写带宽！");
		return false;
	}
	//var pamAttrList = $.table.get("WideTable").getTableData(null,true);
	var pamAttrList = WideTable.getData(true);
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsAcctId = attrs.get("pam_NOTIN_WIDE_ACCT_ID");
			var tag = attrs.get("tag");
			if(acctId == attrsAcctId && tag != 1){
				//alert ("该宽带号码已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_WIDE_ACCT_ID")[0], "该宽带账号已添加请重新选择！");
				return false;
			}
		}
	}
	
	var visplineData = $.ajax.buildJsonData("widePart");
	//$.table.get("WideTable").addRow(visplineData);
	var data = $.DataMap(visplineData);
	WideTable.addRow($.parseJSON(data.toString()));
	wideTable.add(data);
	
	$("#pam_NOTIN_WIDE_ACCT_ID").val("");
	$("#pam_NOTIN_WIDE_MONTH").val("");
	$("#pam_NOTIN_WIDE_NET_LINE").val("");
}
	
function deleteWideData(){
	//$.table.get("WideTable").deleteRow();
	WideTable.deleteRow(WideTable.selected);
}
	
function updateWideData(){
	//宽带号码
	var acctId = $("#pam_NOTIN_WIDE_ACCT_ID").val();
	//宽带时长
	var month = $("#pam_NOTIN_WIDE_MONTH").val();
	//带宽
	var netLine = $("#pam_NOTIN_WIDE_NET_LINE").val();
	
	 //专线宽带
	if (acctId == ""){
		//alert ("请填写宽带号码！");
		$.validate.alerter.one($("#pam_NOTIN_WIDE_ACCT_ID")[0], "请填写宽带账号！");
		return false;
	}
	//专线实例号
    if (month == ""){
		//alert ("请填写宽带时长！");
    	$.validate.alerter.one($("#pam_NOTIN_WIDE_MONTH")[0], "请填写宽带时长！");
		return false;
	}
	//业务标识
	if (netLine == ""){
		//alert ("请填带宽！");
		$.validate.alerter.one($("#pam_NOTIN_WIDE_NET_LINE")[0], "请填写带宽！");
		return false;
	}
	//var pamAttrList = $.table.get("WideTable").getTableData(null,true);
	var pamAttrList = WideTable.getData(true);
	
	//var rowData = $.table.get("WideTable").getRowData();
    var rowData = WideTable.getRowData(WideTable.selected);


	var rowAcctId = rowData.get("pam_NOTIN_WIDE_ACCT_ID");
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrAcctId = attrs.get("pam_NOTIN_WIDE_ACCT_ID");
			var tag = attrs.get("tag");
			if(rowAcctId == acctId){
				break ;
			}
			if(acctId == attrAcctId && tag != 1){
				//alert ("该专线已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_WIDE_ACCT_ID")[0], "该专线已添加请重新选择！");
				return false;
			}
		}
	}
	
	var visplineData = $.ajax.buildJsonData("widePart");
	//$.table.get("WideTable").updateRow(visplineData);
	var data = $.DataMap(visplineData);
	WideTable.updateRow($.parseJSON(data.toString()),WideTable.selected);
	
	$("#pam_NOTIN_WIDE_ACCT_ID").val("");
	$("#pam_NOTIN_WIDE_MONTH").val("");
	$("#pam_NOTIN_WIDE_NET_LINE").val("");

}
	
function tableRowClickWide(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("WideTable").getRowData();
	 $("#pam_NOTIN_WIDE_ACCT_ID").val(rowData.get("pam_NOTIN_WIDE_ACCT_ID"));
	 $("#pam_NOTIN_WIDE_MONTH").val(rowData.get("pam_NOTIN_WIDE_MONTH"));
	 $("#pam_NOTIN_WIDE_NET_LINE").val(rowData.get("pam_NOTIN_WIDE_NET_LINE"));
}
	
	
	
	
	
	
	
	
function createFixedData(){
	//固话号码
	var phone = $("#pam_NOTIN_FIXED_PHONE").val();
	//金额
	var money = $("#pam_NOTIN_FIXED_MONEY").val();
	
	 //专线宽带
	if (phone == ""){
		//alert ("请填写固话号码！");
		$.validate.alerter.one($("#pam_NOTIN_FIXED_PHONE")[0], "请填写固话号码！");
		return false;
	}
	//金额
    if (money == ""){
		//alert ("请填写金额！");
		$.validate.alerter.one($("#pam_NOTIN_FIXED_MONEY")[0], "请填写金额！");
		return false;
	}
	
	//var pamAttrListFixed = $.table.get("FixedTable").getTableData(null,true);
    var pamAttrListFixed = FixedTable.getData(true);
	
	if("" != pamAttrListFixed){
		for(var i=0; i<pamAttrListFixed.length ;i++){
			var fixedlist = pamAttrListFixed.get(i);
			var attrPhone = fixedlist.get("pam_NOTIN_FIXED_PHONE");
			var tag = fixedlist.get("tag");
			if(phone == attrPhone && tag != 1){
				//alert ("该固话号码已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_FIXED_PHONE")[0], "该固话号码已添加请重新选择！");
				return false;
			}
		}
	}
	
	var fixedData = $.ajax.buildJsonData("fixedPart");
	//$.table.get("FixedTable").addRow(fixedData);
	var data = $.DataMap(fixedData);
	FixedTable.addRow($.parseJSON(data.toString()));
	fixedTable.add(data);
	
	$("#pam_NOTIN_FIXED_PHONE").val("");
	$("#pam_NOTIN_FIXED_MONEY").val("");
}

function deleteFixedData(){
	//$.table.get("FixedTable").deleteRow();
	FixedTable.deleteRow(FixedTable.selected);
}
	
function updateFixedData(){
	//固话号码
	var phone = $("#pam_NOTIN_FIXED_PHONE").val();
	//金额
	var money = $("#pam_NOTIN_FIXED_MONEY").val();
	
	 //专线宽带
	if (phone == ""){
		//alert ("请填写固话号码！");
		$.validate.alerter.one($("#pam_NOTIN_FIXED_PHONE")[0], "请填写固话号码！");
		return false;
	}
	//金额
    if (money == ""){
		//alert ("请填写金额！");
		$.validate.alerter.one($("#pam_NOTIN_FIXED_MONEY")[0], "请填写金额！");
		return false;
	}
	
	
	//var pamAttrList = $.table.get("FixedTable").getTableData(null,true);
	var pamAttrList = FixedTable.getData(true);
	
	//var rowData = $.table.get("FixedTable").getRowData();
	var rowData = FixedTable.getRowData(FixedTable.selected);

	var rowAcctId = rowData.get("pam_NOTIN_FIXED_PHONE");
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrAcctId = attrs.get("pam_NOTIN_FIXED_PHONE");
			var tag = attrs.get("tag");
			if(rowAcctId == phone){
				break ;
			}
			if(phone == attrAcctId && tag != 1){
				//alert ("该固话号码已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_FIXED_PHONE")[0], "该固话号码已添加请重新选择！");
				return false;
			}
		}
	}
	
	var visplineData = $.ajax.buildJsonData("fixedPart");
	//$.table.get("FixedTable").updateRow(visplineData);
	var data = $.DataMap(visplineData);
	FixedTable.updateRow($.parseJSON(data.toString()),FixedTable.selected);
	
	$("#pam_NOTIN_FIXED_PHONE").val("");
	$("#pam_NOTIN_FIXED_MONEY").val("");

}


function tableRowClickFixed(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("FixedTable").getRowData();
	 $("#pam_NOTIN_FIXED_PHONE").val(rowData.get("pam_NOTIN_FIXED_PHONE"));
	 $("#pam_NOTIN_FIXED_MONEY").val(rowData.get("pam_NOTIN_FIXED_MONEY"));
}






function createSerialData(){
	//固话号码
	var phone = $("#pam_NOTIN_SERIAL_PHONE").val();
	//金额
	var money = $("#pam_NOTIN_SERIAL_MONEY").val();
	
	 //专线宽带
	if (phone == ""){
		//alert ("请填写固话号码！");
		$.validate.alerter.one($("#pam_NOTIN_SERIAL_PHONE")[0], "请填写手机号码！");
		return false;
	}
	//金额
    if (money == ""){
		//alert ("请填写金额！");
		$.validate.alerter.one($("#pam_NOTIN_SERIAL_MONEY")[0], "请填写金额！");
		return false;
	}
	
	//var pamAttrList = $.table.get("SerialTable").getTableData(null,true);
    var pamAttrList = SerialTable.getData(true);

	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsAcctId = attrs.get("pam_NOTIN_SERIAL_PHONE");
			var tag = attrs.get("tag");
			if(phone == attrsAcctId && tag != 1){
				//alert ("该固话号码已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_SERIAL_PHONE")[0], "该固话号码已添加请重新选择！");
				return false;
			}
		}
	}
	
	var visplineData = $.ajax.buildJsonData("serialPart");
	//$.table.get("SerialTable").addRow(visplineData);
	var data = $.DataMap(visplineData);
	SerialTable.addRow($.parseJSON(data.toString()));
	serialTable.add(data);
	
	$("#pam_NOTIN_SERIAL_PHONE").val("");
	$("#pam_NOTIN_SERIAL_MONEY").val("");
}

function deleteSerialData(){
	//$.table.get("SerialTable").deleteRow();
	SerialTable.deleteRow(SerialTable.selected);
}

function updateSerialData(){
	//固话号码
	var phone = $("#pam_NOTIN_SERIAL_PHONE").val();
	//金额
	var money = $("#pam_NOTIN_SERIAL_MONEY").val();
	
	 //专线宽带
	if (phone == ""){
		//alert ("请填写固话号码！");
		$.validate.alerter.one($("#pam_NOTIN_SERIAL_PHONE")[0], "请填写手机号码！");
		return false;
	}
	//金额
    if (money == ""){
		//alert ("请填写金额！");
		$.validate.alerter.one($("#pam_NOTIN_SERIAL_MONEY")[0], "请填写金额！");
		return false;
	}
	
	
	//var pamAttrListSerial = $.table.get("SerialTable").getTableData(null,true);
    var pamAttrListSerial = SerialTable.getData(true);

	//var rowData = $.table.get("SerialTable").getRowData();
    var rowData = SerialTable.getRowData(SerialTable.selected);

	var rowAcctId = rowData.get("pam_NOTIN_SERIAL_PHONE");
	
	if("" != pamAttrListSerial){
		for(var i=0; i<pamAttrListSerial.length ;i++){
			var attrsSerial = pamAttrListSerial.get(i);
			var attrAcctId = attrsSerial.get("pam_NOTIN_SERIAL_PHONE");
			var tag = attrsSerial.get("tag");
			if(rowAcctId == phone){
				break ;
			}
			if(phone == attrAcctId && tag != 1){
				//alert ("该固话号码已添加请重新选择！");
				$.validate.alerter.one($("#pam_NOTIN_SERIAL_PHONE")[0], "该手机号码已添加请重新选择！");
				return false;
			}
		}
	}
	
	var visplineData = $.ajax.buildJsonData("serialPart");
	//$.table.get("SerialTable").updateRow(visplineData);
	var data = $.DataMap(visplineData);
	SerialTable.updateRow($.parseJSON(data.toString()),SerialTable.selected);
	
	$("#pam_NOTIN_SERIAL_PHONE").val("");
	$("#pam_NOTIN_SERIAL_MONEY").val("");

}
	
	
function tableRowClickSerial(rowData){
	//获取选择行的数据
	 //var rowData = $.table.get("SerialTable").getRowData();
	 $("#pam_NOTIN_SERIAL_PHONE").val(rowData.get("pam_NOTIN_SERIAL_PHONE"));
	 $("#pam_NOTIN_SERIAL_MONEY").val(rowData.get("pam_NOTIN_SERIAL_MONEY"));
}
	

function showLayer(obj){
	/*var sbtitle = document.getElementById(obj);
	if(sbtitle){
   		if(sbtitle.style.display=='block'){
   			sbtitle.style.display='none';
  		}else{
   			sbtitle.style.display='block';
   		}
	}*/
	$("#"+obj).css("display","block"); 
}
function hideLayer(obj){
	/*var sbtitle = document.getElementById(obj);
	if(sbtitle){
   		if(sbtitle.style.display=='none'){
   			sbtitle.style.display='block';
  		}else{
   			sbtitle.style.display='none';
   		}
	}*/
	$("#"+obj).css("display","none");
}
	
	
	
	
function validateParamPage(methodName) {
	/*var acctId = $("#pam_NOTIN_WIDE_ACCT_ID").val();
	var month = $("#pam_NOTIN_WIDE_MONTH").val();
	var netLine = $("#pam_NOTIN_WIDE_NET_LINE").val();
	if($("#wideinfo").css("display")=="none"){
		if(acctId == ""){
			$.validate.alerter.one($("#wideInfoShower")[0], "请填写宽带号码！");
			return false;
		}
		if(month == ""){
			$.validate.alerter.one($("#wideInfoShower")[0], "请填写宽带时长！");
			return false;
		}
		if(netLine == ""){
			$.validate.alerter.one($("#wideInfoShower")[0], "请填写带宽！");
			return false;
		}
	}else{
		if (acctId == ""){
			$.validate.alerter.one($("#pam_NOTIN_WIDE_ACCT_ID")[0], "请填写宽带号码！");
			return false;
		}
	    if (month == ""){
			$.validate.alerter.one($("#pam_NOTIN_WIDE_MONTH")[0], "请填写宽带时长！");
			return false;
		}
		if (netLine == ""){
			$.validate.alerter.one($("#pam_NOTIN_WIDE_NET_LINE")[0], "请填写带宽！");
			return false;
		}
	}
	
	var fixedPhone = $("#pam_NOTIN_FIXED_PHONE").val();
	var fixedMoney = $("#pam_NOTIN_FIXED_MONEY").val();
	if($("#fixedinfo").css("display")=="none"){
		if(fixedPhone == ""){
			$.validate.alerter.one($("#fixedInfoShower")[0], "请填写固话号码！");
			return false;
		}
		if(fixedMoney == ""){
			$.validate.alerter.one($("#fixedInfoShower")[0], "请填写金额！");
			return false;
		}
	}else{
		if (fixedPhone == ""){
			$.validate.alerter.one($("#pam_NOTIN_FIXED_PHONE")[0], "请填写固话号码！");
			return false;
		}
		//金额
	    if (fixedMoney == ""){
			$.validate.alerter.one($("#pam_NOTIN_FIXED_MONEY")[0], "请填写金额！");
			return false;
		}
	}
	
	var serialPhone = $("#pam_NOTIN_SERIAL_PHONE").val();
	var serialMoney = $("#pam_NOTIN_SERIAL_MONEY").val();
	if($("#serialinfo").css("display")=="none"){
		if(serialPhone == ""){
			$.validate.alerter.one($("#serialInfoShower")[0], "请填写手机号码！");
			return false;
		}
		if(serialMoney == ""){
			$.validate.alerter.one($("#serialInfoShower")[0], "请填写金额！");
			return false;
		}
	}else{
		if (serialPhone == ""){
			//alert ("请填写固话号码！");
			$.validate.alerter.one($("#pam_NOTIN_SERIAL_PHONE")[0], "请填写手机号码！");
			return false;
		}
		//金额
	    if (serialMoney == ""){
			$.validate.alerter.one($("#pam_NOTIN_SERIAL_MONEY")[0], "请填写金额！");
			return false;
		}
	}*/
	var wideAttr = WideTable.getData(true);
	var fixedAttr = FixedTable.getData(true);
	var serialAttr = SerialTable.getData(true);
	
	var oldWideAttr = $("#pam_NOTIN_OLD_WideData").val();
	var oldFixedAttr = $("#pam_NOTIN_OLD_FixedData").val();
	var oldSerialAttr = $("#pam_NOTIN_OLD_SerialData").val();
	
	
	var wideList = new Wade.DatasetList();
	var fixedList = new Wade.DatasetList();
	var serialList = new Wade.DatasetList();
	
	var wideList = compareDatasetWide(wideAttr,oldWideAttr);
	$("#pam_NOTIN_WideData").val(wideList);
	
	var fixedList = compareDatasetFixed(fixedAttr,oldFixedAttr);
	$("#pam_NOTIN_FixedData").val(fixedList);
	
	var serialList = compareDatasetSerial(serialAttr,oldSerialAttr);
	$("#pam_NOTIN_SerialData").val(serialList);
	
	//去除必填验证
	$("#pam_NOTIN_WIDE_ACCT_ID").attr("nullable", "yes");
	$("#pam_NOTIN_WIDE_MONTH").attr("nullable", "yes");
	$("#pam_NOTIN_WIDE_NET_LINE").attr("nullable", "yes");
	$("#pam_NOTIN_FIXED_PHONE").attr("nullable", "yes");
	$("#pam_NOTIN_FIXED_MONEY").attr("nullable", "yes");
	$("#pam_NOTIN_SERIAL_PHONE").attr("nullable", "yes");
	$("#pam_NOTIN_SERIAL_MONEY").attr("nullable", "yes");
	
    return true;
}

function compareDatasetWide(strNewValue,strOldValue){
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
	        if(oldValueColumn.get('pam_NOTIN_WIDE_ACCT_ID') == newValueColumn.get('pam_NOTIN_WIDE_ACCT_ID')){
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_WIDE_MONTH') == newValueColumn.get('pam_NOTIN_WIDE_MONTH')&&
	        		oldValueColumn.get('pam_NOTIN_WIDE_NET_LINE') == newValueColumn.get('pam_NOTIN_WIDE_NET_LINE')){
	        		
	        		newValueColumn.put("tag","");
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        	isfound = "true";
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
            if(oldValueColumn.get('pam_NOTIN_WIDE_ACCT_ID') == newValueColumn.get('pam_NOTIN_WIDE_ACCT_ID'))
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
	
function compareDatasetFixed(strNewValue,strOldValue){
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
	        if(oldValueColumn.get('pam_NOTIN_FIXED_PHONE') == newValueColumn.get('pam_NOTIN_FIXED_PHONE')){
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_FIXED_MONEY') == newValueColumn.get('pam_NOTIN_FIXED_MONEY')){
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
            if(oldValueColumn.get('pam_NOTIN_FIXED_PHONE') == newValueColumn.get('pam_NOTIN_FIXED_PHONE'))
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
	
	
	
function compareDatasetSerial(strNewValue,strOldValue){
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
	        if(oldValueColumn.get('pam_NOTIN_SERIAL_PHONE') == newValueColumn.get('pam_NOTIN_SERIAL_PHONE')){
	        	/*if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}*/
	        	if(oldValueColumn.get('pam_NOTIN_SERIAL_MONEY') == newValueColumn.get('pam_NOTIN_SERIAL_MONEY')){
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
            if(oldValueColumn.get('pam_NOTIN_SERIAL_PHONE') == newValueColumn.get('pam_NOTIN_SERIAL_PHONE'))
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
	
	
	wideTable = null;
	fixedTable = null;
	serialTable = null;
	wideTable = WideTable.getData();
	fixedTable = FixedTable.getData();
	serialTable = SerialTable.getData();
	backPopup(obj);
}