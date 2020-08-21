/**
 * 添加二维
 * @author dengshu
 * @date   2014年5月21日-下午4:53:25
 */

function reset(){
	$('#submit_part :input').val('');
}

/**
 * 保存二维码信息
 */
function  saveTwoDimCode(){
	//基本信息校验
	if(!$.validate.verifyAll('submit_part')) {
		return false;
	}
	var title = $.nav.getTitle();
	
	var submit_data = $.table.get("paramTable").getTableData();
	var tableIsNull = true;
	//新增数据判断是否为空
	if(title.indexOf('修改')==-1){
		tableIsNull= submit_data.length<1;
	}else{
	//修改数据判断是否为空
		tableIsNull = !checkParamTableNotNull();
	}
	
	if(tableIsNull){
		MessageBox.alert('提示','没有添加任何元素数据！');
		return false;
	}else{
		$("#edit_table").val(submit_data.toString());
	}
	$.beginPageLoading("正在生成二维码图片...");
	$.ajax.submit('submit_part', 'saveTwoDimCode', null, null, function(data){
		//窗口方式关闭窗口
		MessageBox.alert('提示','二维码生成成功！');
		$.endPageLoading();
		$.nav.switchByTitle('二维码管理');
		$.nav.getContentWindowByTitle('二维码管理').$("#queryBtn").click();
		$.nav.closeByTitle(title);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});

}

/**
 * 检查编辑表格是否有数据
 * false  为空
 * true  有数据
 */
function checkParamTableNotNull(){
	var tableJQ = $("#paramTable tbody tr");
	//是否有数据
	var dataFlag = false;
	for(var i=0;i<tableJQ.length;i++){
		var trDom = tableJQ.get(i);
		//删除
		if(trDom.getAttribute("status")!=1&&trDom.style.display!='none'){
			dataFlag = true;
			break;
		}
	}
	return dataFlag;
}

/**
 * 审批二维码提交
 * @param dealType
 * @returns {Boolean}
 */
function doApproval(dealType){
	if(!$.validate.verifyAll('submit_part')) {
		return false;
	}
	$.beginPageLoading("正在处理数据...");
	$.ajax.submit('submit_part', 'approvalTwoDimCode', 'STATE='+dealType, null, function(data){
		//窗口方式关闭窗口
		MessageBox.alert('提示','操作成功！');
		$.endPageLoading();
		$.nav.switchByTitle('二维码管理');
		$.nav.getContentWindowByTitle('二维码管理').$("#queryBtn").click();
		$.nav.closeByTitle('审批二维码');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

//点击表格行，初始化编辑区
function tableRowClick(){
	var rowData = $.table.get("paramTable").getRowData();
	for(var i = 0;i< rowData.keys.length;i++){
		var colId = rowData.keys[i];
		var jqObj = $('#'+colId);
		if(jqObj!=null){
			jqObj.val(rowData.map[colId]);
		}
	}
}

/** 新增一条记录，对应表格新增按钮 */
function addProductInfo() {
	/* 校验所有的输入框 */
	if(!$.validate.verifyAll('editPart')) {
		return false;
	}
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	var elementTypeName = $("#ELEMENT_TYPE")[0].options[$("#ELEMENT_TYPE")[0].selectedIndex].text;
	editData["ELEMENT_TYPE_NAME"]=elementTypeName;
	var table_data = $.table.get("paramTable").getTableData();
	for(var i = 0;i< table_data.length;i++){
		var rowData=table_data.get(i);
		var newId=editData["ELEMENT_ID"];
		var newType=editData["ELEMENT_TYPE"];
		var oldId=rowData.get("ELEMENT_ID");
		var oldType=rowData.get("ELEMENT_TYPE");
		if(newId==oldId && newType==oldType){
			alert("不允许添加重复元素！");
			return false;
		}
	}
	/* 新增表格行 */
	$.table.get("paramTable").addRow(editData);
}

/** 修改一条记录，对应表格修改按钮 */
function editProductInfo() {
	/* 校验所有的输入框 */
	if(!$.validate.verifyAll('editPart')) {
		return false;
	}
	var editData = $.ajax.buildJsonData("editPart");
	var elementTypeName = $("#ELEMENT_TYPE")[0].options[$("#ELEMENT_TYPE")[0].selectedIndex].text;
	editData['ELEMENT_TYPE_NAME']=elementTypeName;
	
	/* 修改表格行 */
	$.table.get("paramTable").updateRow(editData);
}

/** 删除一条记录，对应表格删除按钮 */
function deleteProductInfo() {
	$.table.get("paramTable").deleteRow();
}	



/*修改结束日期，开始日期提前一个月*/
function chgEndDateSynStartDate(endObj, startId){
	var endDate = endObj.value;
	if(endObj.value == '' || !verifyField(endObj)){//仅在格式满足的情况下才执行该操作
		endObj.select();
		return;
	}
	if($("#"+startId).val()!=''){
		return;
	}
	var dateArr = endDate.split("-");
	
	var edate = new Date(dateArr[0], parseInt(dateArr[1]-1), dateArr[2]);
	var sdate = new Date(edate.getTime()-(1000*60*60*24*30));
	//月份补0
	var month =(sdate.getMonth()+1);
	if((sdate.getMonth()+1)<10){
		month = '0'+(sdate.getMonth()+1);
	}
	var date = edate.getDate();
	if(edate.getDate()<10){
		date = '0'+edate.getDate();
	}
	sdateStr = sdate.getFullYear()+"-"+month+"-"+date;
	$("#"+startId).val( sdateStr);
}

