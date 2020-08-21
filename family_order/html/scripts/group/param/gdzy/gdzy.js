
function initCrtUs() {
	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs')
	{
		//$('#addBtn').css('display','none');
		//$('#delBtn').css('display','none');
	}
}

function createData() {

	var data = $.table.get("BroadbandTable").getTableData(null, true);
	//if(data != null && data.length > 0){
	//	alert("只能新增一条费项参数,不可再新增!");
	//	return false;
	//}
		
    //项目名称
	var projectName = $("#pam_NOTIN_PROJECT_NAME").val();
    //收费名称
    var feeName = $("#pam_NOTIN_FEE_NAME").val();
    var feeNameV = $("#pam_NOTIN_FEE_NAME option:selected").text();
    //收费金额(元)
    var feeCost = $("#pam_NOTIN_FEE_COST").val();
    //收费截止时间
    var feeEndDate = $("#pam_NOTIN_FEE_END_DATE").val();
    //备注
    var remark = $("#pam_NOTIN_REMARK").val();
    
    //系统当前日期
    var currentDate = $("#pam_NOTIN_CURRENT_DATE").val();
    
    var thisDate = new Date();
    var year = thisDate.getYear().toString();  
    var month = thisDate.getMonth()+1;
    if (month <= 9) {
		month = "0" + month;
	}
	var day = thisDate.getDate();
	if (day <= 9) {
		day = "0" + day;
	}
	var hours = thisDate.getHours();
	if (hours <= 9) {
		hours = "0" + hours;
	}
	var minutes = thisDate.getMinutes();
	if (minutes <= 9) {
		minutes = "0" + minutes;
	}
	var seconds = thisDate.getSeconds();
	if (seconds <= 9) {
		seconds = "0" + seconds;
	}
	
	var today = year + month + day + hours + minutes + seconds;
	
	if(projectName == "")
	{
		alert("请填写项目名称!");
		return false;
	}
	var currLength = getAllLength(projectName);
	if (currLength > 100 && currLength != 0) 
    {
    	alert("项目名称最大长度不能超过100字节,当前输入字符的总字节数为" + currLength + "字节!\n【提示：汉字为两个字节，字母、数字、标点符号等其它为一个字节】");
    	return false;
    }
    
	if(feeName == "")
	{
		alert("请填写收费名称!");
		return false;
	}
	
    if (feeCost == "")
	{
		alert ("请填写收费金额(元)!");
		return false;
	}
	else
	{
		var flag = $.verifylib.checkNumeric(feeCost ,"0.00");
		if(!flag)
		{
			alert("收费金额(元)必须是数字或格式为0.00的数字!");
			return false;
		}
		var floatFlag = parseFloat(feeCost);
		if(floatFlag < 0)
		{
			alert("收费金额(元)不能为负数!");
			return false;
		}
	}
	
	if(feeName == "4")
	{
		if(feeEndDate != "")
		{
			alert("收费名称为其他一次性费用,收费截止时间不需要填写!");
			return false;
		}
	}
	else 
	{
		if(feeEndDate == "")
		{
			alert("请选择收费截止时间!");
			return false;
		}
		var comFlag = dateCompare(currentDate,feeEndDate);
		if(comFlag == false)
		{
			alert("收费截止时间不可早于当前时间!");
			return false;
		}
	}
	
	var compFlag = createDataCompare(data,projectName,feeName);
	if(compFlag)
	{
		alert("项目名称:" + projectName + ",收费名称:" + feeNameV + "已经存在,不可再新增!");
		return false;
	}
	
	$("#pam_NOTIN_OPER_TAG").val(today);
	$("#pam_NOTIN_PROJECT_NAME").val(projectName);
	$("#pam_NOTIN_FEE_NAME").val(feeName);
	$("#pam_NOTIN_FEE_NAME_V").val(feeNameV);
	
	$("#pam_NOTIN_FEE_COST").val(feeCost);
	$("#pam_NOTIN_FEE_END_DATE").val(feeEndDate);
	$("#pam_NOTIN_REMARK").val(remark);
		
	var gdzyData = $.ajax.buildJsonData("broadbandPart");
	$.table.get("BroadbandTable").addRow(gdzyData);
	
	//$("#pam_NOTIN_OPER_TAG").val("");
	//$("#pam_NOTIN_PROJECT_NAME").val("");
	//$("#pam_NOTIN_FEE_NAME").val("");
	//$("#pam_NOTIN_FEE_NAME_V").val("");
	//$("#pam_NOTIN_FEE_COST").val("0");
	//$("#pam_NOTIN_FEE_END_DATE").val("");
	//$("#pam_NOTIN_REMARK").val("");
	//changeBack();
	deleteBack();
}

function deleteData()
{
	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs')
	{
		var rowData = $.table.get("BroadbandTable").getRowData();
		if(rowData != null && rowData != "")
		{
			var operTag = rowData.get("pam_NOTIN_OPER_TAG");
			var feeName = rowData.get("pam_NOTIN_FEE_NAME");
			if(feeName == "4")
			{
				var oldPamAttr = $("#pam_NOTIN_OLD_AttrGdzy").val();
				if (oldPamAttr != "" && oldPamAttr != "[]")
				{
					var oldValueSet = new Wade.DatasetList(oldPamAttr);
    				for (var i=0;i<oldValueSet.length;i++)
    				{
    					var oldValueColumn = oldValueSet.get(i);
    					var oldOperTag = oldValueColumn.get("pam_NOTIN_OPER_TAG");
    					if(oldOperTag == operTag)
    					{
    						alert("收费名称为其他一次性费用不能删除!");
    						deleteBack();
    						return false;
    					}
    				}
				}
			}
		}
	}
	
	$.table.get("BroadbandTable").deleteRow();
	//changeBack();
	deleteBack();
}

function checkNum(obj){
	if(!/^(?:[1-9]\d*|0)$/.test(obj)){
		return false;
	}
	return true;
}

function updateData() {
    //项目名称
	var projectName = $("#pam_NOTIN_PROJECT_NAME").val();
    //收费名称
    var feeName = $("#pam_NOTIN_FEE_NAME").val();
    var feeNameV = $("#pam_NOTIN_FEE_NAME option:selected").text();
    //收费金额(元)
    var feeCost = $("#pam_NOTIN_FEE_COST").val();
    //收费截止时间
    var feeEndDate = $("#pam_NOTIN_FEE_END_DATE").val();
    //备注
    var remark = $("#pam_NOTIN_REMARK").val();
	
	var operTag = $("#pam_NOTIN_OPER_TAG").val();
	
	if(operTag == "" || operTag == null)
	{
		alert("请重新选择要修改的行数据后,再点击修改按钮!");
		return false;
	}
	
	//系统当前日期
    var currentDate = $("#pam_NOTIN_CURRENT_DATE").val();
    
	if(projectName == "")
	{
		alert("请填写项目名称!");
		return false;
	}
	var currLength = getAllLength(projectName);
	if (currLength > 100 && currLength != 0) 
    {
    	alert("项目名称最大长度不能超过100字节,当前输入字符的总字节数为" + currLength + "字节!\n【提示：汉字为两个字节，字母、数字、标点符号等其它为一个字节】");
    	return false;
    }
    
	if(feeName == "")
	{
		alert("请填写收费名称!");
		return false;
	}
	
    if (feeCost == "")
    {
		alert ("收费金额(元)!");
		return false;
	}
	else
	{
		var flag = $.verifylib.checkNumeric(feeCost ,"0.00");
		if(!flag)
		{
			alert("收费金额(元)必须是数字或格式为0.00的数字");
			return false;
		}
		var floatFlag = parseFloat(feeCost);
		if(floatFlag < 0)
		{
			alert("收费金额(元)不能为负数!");
			return false;
		}
	}
	
	if(feeName == "4")
	{
		if(feeEndDate != "")
		{
			alert("收费名称为其他一次性费用,收费截止时间不需要填写!");
			return false;
		}
	}
	else 
	{
		if(feeEndDate == "")
		{
			alert("请选择收费截止时间!");
			return false;
		}
		
		//当收费截止时间被修改过,则在做一下比较
		/*
		var compareFlag = false;
		var oldParamAttr = $("#pam_NOTIN_OLD_AttrGdzy").val();
		if (oldParamAttr != "" && oldParamAttr != "[]")
		{
			var oldValueSet = new Wade.DatasetList(oldParamAttr);
			for (var j=0;j<oldValueSet.length;j++)
			{
				var oldValueColumn = oldValueSet.get(j);
				var oldOperTag = oldValueColumn.get('pam_NOTIN_OPER_TAG');
				var oldFeeEndDate = oldValueColumn.get('pam_NOTIN_FEE_END_DATE');
				if(operTag == oldOperTag && feeEndDate != oldFeeEndDate 
					&& feeEndDate != "" && oldFeeEndDate != "" )
				{
					compareFlag = true;
					break;
				}
			}
		}
		if(compareFlag == true)
		{
			var comFlag = dateCompare(currentDate,feeEndDate);
			if(comFlag == false)
			{
				alert("收费截止时间不可早于当前时间!");
				return false;
			}
		}
		*/
		var comFlag = dateCompare(currentDate,feeEndDate);
		if(comFlag == false)
		{
			alert("收费截止时间不可早于当前时间!");
			return false;
		}
	}
	
	var updateFlag = updateDataCommpare(operTag,projectName,feeName);
	if(updateFlag)
	{
		alert("项目名称:" + projectName + ",收费名称:" + feeNameV + "已经存在,请重新修改!");
		return false;
	}
	
	$("#pam_NOTIN_PROJECT_NAME").val(projectName);
	$("#pam_NOTIN_FEE_NAME").val(feeName);
	$("#pam_NOTIN_FEE_NAME_V").val(feeNameV);
	$("#pam_NOTIN_FEE_COST").val(feeCost);
	$("#pam_NOTIN_FEE_END_DATE").val(feeEndDate);
	$("#pam_NOTIN_REMARK").val(remark);
	
	//获取编辑区的数据
	var vispbroadbandData = $.ajax.buildJsonData("broadbandPart");
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("BroadbandTable").updateRow(vispbroadbandData);
	
	//$("#pam_NOTIN_OPER_TAG").val("");
	//$("#pam_NOTIN_PROJECT_NAME").val("");
	//$("#pam_NOTIN_FEE_NAME").val("");
	//$("#pam_NOTIN_FEE_NAME_V").val("");
	//$("#pam_NOTIN_FEE_COST").val("0");
	//$("#pam_NOTIN_FEE_END_DATE").val("");
	//$("#pam_NOTIN_REMARK").val("");
	//changeBack();
	deleteBack();
}

function tableRowClick(){

	deleteBack();
	
	//获取选择行的数据
	var rowData = $.table.get("BroadbandTable").getRowData();
	 
	var choose = rowData.get("pam_NOTIN_FEE_NAME");
	if(choose == "4")
	{  
		$("#pam_NOTIN_FEE_END_DATE").val("");
		$("#pam_NOTIN_FEE_END_DATE").attr("disabled", true);
		
		$("#spanFeeDate").attr("className","");
	}
	else
	{ 
		$("#pam_NOTIN_FEE_END_DATE").attr("disabled", false);
		$("#spanFeeDate").attr("className","e_required");
	} 
	
	var METHOD_NAME = $('#pam_NOTIN_METHOD_NAME').val();
	if(METHOD_NAME=='ChgUs')
	{
		var operTag = rowData.get("pam_NOTIN_OPER_TAG");
		var projectName = rowData.get("pam_NOTIN_PROJECT_NAME");
		var feeName = rowData.get("pam_NOTIN_FEE_NAME");
		var oldPamAttr = $("#pam_NOTIN_OLD_AttrGdzy").val();
		if (oldPamAttr != "" && oldPamAttr != "[]")
		{
			var oldValueSet = new Wade.DatasetList(oldPamAttr);
			for (var i=0;i<oldValueSet.length;i++)
			{
				var oldValueColumn = oldValueSet.get(i);
				var oldOperTag = oldValueColumn.get("pam_NOTIN_OPER_TAG");
				var oldProjectName = oldValueColumn.get("pam_NOTIN_PROJECT_NAME");
				var oldFeeName = oldValueColumn.get("pam_NOTIN_FEE_NAME");
				if(operTag == oldOperTag && projectName == oldProjectName && feeName == oldFeeName)
				{
					$("#pam_NOTIN_PROJECT_NAME").attr("disabled", true);
					$("#pam_NOTIN_FEE_NAME").attr("disabled", true);
				}
			}
		}
	}
	
	$("#pam_NOTIN_OPER_TAG").val(rowData.get("pam_NOTIN_OPER_TAG"));
	$("#pam_NOTIN_PROJECT_NAME").val(rowData.get("pam_NOTIN_PROJECT_NAME"));
	$("#pam_NOTIN_FEE_NAME").val(rowData.get("pam_NOTIN_FEE_NAME"));
	$("#pam_NOTIN_FEE_NAME_V").val(rowData.get("pam_NOTIN_FEE_NAME_V"));
	$("#pam_NOTIN_FEE_COST").val(rowData.get("pam_NOTIN_FEE_COST")!="" ? rowData.get("pam_NOTIN_FEE_COST") : "0");
	$("#pam_NOTIN_FEE_END_DATE").val(rowData.get("pam_NOTIN_FEE_END_DATE"));
	$("#pam_NOTIN_REMARK").val(rowData.get("pam_NOTIN_REMARK"));
}


function getAllLength(str) 
{
	var length = 0;
	if (typeof (str) == "string" && str != "") 
	{
		for (var i = 0; i < str.length; i++) 
		{
			if (str.charCodeAt(i) > 255) 
			{
				length += 2;
			} 
			else 
			{
				length++;
			}
		}
	}
	return length;
}


function dateCompare(currentDate, selDate) 
{
	var curDateArr = currentDate.split("-");
	var selDateArr = selDate.split("-");
	if(curDateArr.length == 3 && selDateArr.length == 3)
	{
		var curTime = new Date(curDateArr[0], curDateArr[1], curDateArr[2]);
		var selTime = new Date(selDateArr[0], selDateArr[1], selDateArr[2]);
		//alert("curTime=" + curTime);
		//alert("selTime=" + selTime);
		if(curTime.getTime() >= selTime.getTime()) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	return true;
}

function getNewData(){
	var data = $.table.get("BroadbandTable").getTableData(null, true);
	//alert("获取全表数据:"+data);
}

function getOldData(){
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrGdzy").val();
	//alert(oldPamAttr.toString());
}


function validateParamPage(methodName) {
	var pamAttr = $.table.get("BroadbandTable").getTableData(null, true);
	var oldPamAttr = $("#pam_NOTIN_OLD_AttrGdzy").val();
	if (pamAttr == "" || pamAttr == "[]"){
		alert ("请增加一条费项信息!");
		return false;
	}
	
	var attrList = new Wade.DatasetList();
	var attrList = compareDataset(pamAttr,oldPamAttr);
	
	$("#pam_NOTIN_AttrGdzy").val(attrList);
	
    return true;
}

function compareDataset(strNewValue,strOldValue){
	debugger;
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue == "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    //查找不变和变更行
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG')){//可能不变，可能变更
	        	if(newValueColumn.get('tag') == "2"){
	        		var isfound = "true";
					resultSet.add(newValueColumn);	        	
	        	}
	        }
	    }
	    if(isfound == "false"){
       		resultSet.add(newValueColumn);
	    }
	}
	
	//查找删除的行
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        var isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_NOTIN_OPER_TAG') == newValueColumn.get('pam_NOTIN_OPER_TAG'))
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

function createDataCompare(newValueSet,newProjectName,newFeeName)
{
    if (newValueSet == ""){
        newValueSet == "[]";
    }
    var isfound = false;
    //查找项目名称、收费名称是否存在
    for (var i=0;i<newValueSet.length;i++)
    {
    	var newValueColumn = newValueSet.get(i);
    	var oldProjectName = newValueColumn.get('pam_NOTIN_PROJECT_NAME');
    	var oldFeeName = newValueColumn.get('pam_NOTIN_FEE_NAME');
    	if(newProjectName == oldProjectName && newFeeName == oldFeeName)
    	{
    		isfound = true;
    		break;
    	}
	}
	
   return isfound;
}

/**
*存在返回true,否则false
*/
function updateDataCommpare(notinOperTag,newProjectName,newFeeName)
{
	var dataSet = $.table.get("BroadbandTable").getTableData(null, true);
	if (dataSet == ""){
        dataSet == "[]";
    }
    var isfound = false;
    //从列表中查找项目名称、收费名称是否存在,剔除掉当前修改的项notinOperTag
    for (var i=0;i<dataSet.length;i++)
    {
    	var valueColumn = dataSet.get(i);
    	var oldNotinOperTag = valueColumn.get('pam_NOTIN_OPER_TAG');
    	var oldProjectName = valueColumn.get('pam_NOTIN_PROJECT_NAME');
    	var oldFeeName = valueColumn.get('pam_NOTIN_FEE_NAME');
    	if(newProjectName == oldProjectName && newFeeName == oldFeeName && notinOperTag != oldNotinOperTag)
    	{
    		isfound = true;
    		break;
    	}
	}
	return isfound;
}

function changeFeeName() 
{
	var choose = $("#pam_NOTIN_FEE_NAME").val();
	if(choose == "4")
	{  
		$("#pam_NOTIN_FEE_END_DATE").val("");
		$("#pam_NOTIN_FEE_END_DATE").attr("disabled", true);
		$("#spanFeeDate").attr("className","");
	}
	else
	{ 
		$("#pam_NOTIN_FEE_END_DATE").attr("disabled", false);
		$("#spanFeeDate").attr("className","e_required");
	}   
}

function changeBack()
{
	$("#pam_NOTIN_PROJECT_NAME").attr("disabled", false);
	$("#pam_NOTIN_FEE_NAME").attr("disabled", false);
	$("#pam_NOTIN_FEE_END_DATE").attr("disabled", false);
	$("#spanFeeDate").attr("className","e_required");
}

function deleteBack()
{
	$("#pam_NOTIN_PROJECT_NAME").attr("disabled", false);
	$("#pam_NOTIN_FEE_NAME").attr("disabled", false);
	$("#pam_NOTIN_FEE_END_DATE").attr("disabled", false);
	$("#spanFeeDate").attr("className","e_required");
	$("#pam_NOTIN_OPER_TAG").val("");
	$("#pam_NOTIN_PROJECT_NAME").val("");
	$("#pam_NOTIN_FEE_NAME").val("");
	$("#pam_NOTIN_FEE_NAME_V").val("");
	$("#pam_NOTIN_FEE_COST").val("0");
	$("#pam_NOTIN_FEE_END_DATE").val("");
	$("#pam_NOTIN_REMARK").val("");
}


function cleanData()
{
	deleteBack();
}