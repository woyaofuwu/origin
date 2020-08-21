function queryUserFreeze(){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryUserFreeze', null, 'ResultDataPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkEditPart(){
	if(!$.validate.verifyAll("EditPart")) {
		return false;
	}
	return true;
}

function createPhone(obj){
	/* 校验所有的输入框 */
	if (!checkEditPart()) return false;
	$.ajax.submit('EditPart', 'createPhone', null, 'TipInfoPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
	
	
//	var phoneTable = $.table.get("phoneTable").getTableData(null, true);
//	var editData = $.ajax.buildJsonData("editPart");
//	editData['SERIAL_NUMBER']=$("#SERIAL_NUMBER").val();
//	editData['NUMBER_TYPE']=$("#NUMBER_TYPE").val();
//	$.table.get("phoneTable").addRow(editData);/* 新增表格行 */
}

function deletePhone(obj){
	var rowData = $.table.get("phoneTable").getRowData();
	if (rowData.length == 0) {
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	$.table.get("phoneTable").deleteRow();
	var phoneTable = $.table.get("phoneTable").getTableData(null, true);
	var size =0;
	phoneTable.each(function(item,index,totalCount){

		if(item.get("tag") !="1"){
			size++;
		}
	});
}

function deleteAllPhone(obj){
	var length = $.table.get("phoneTable").getTableData(null, true).length;
	for(var i=0;i<length;i++){
		$.table.fn.selectedRow('phoneTable', $("#phoneTable")[0].rows[i + 1]);
		$.table.get("phoneTable").deleteRow();
	}
}
