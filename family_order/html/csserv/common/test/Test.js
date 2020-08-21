$(function(){
	$.resizeHeight(); 
});

function insertFee(){
	var feeListTable = $.tableManager.get("feeListTable");
	var newRowIndex = feeListTable.newRow();
	feeListTable.setValue(newRowIndex,"TRADE_TYPE_CODE","240");
	feeListTable.setValue(newRowIndex,"TRADE_TYPE_NAME","活动受理");
	feeListTable.setValue(newRowIndex,"SERIAL_NUMBER","13975841640");
	feeListTable.setValue(newRowIndex,"ELEMENT_ID","12");
	feeListTable.setValue(newRowIndex,"ELEMENT_NAME","呼叫转移");
	feeListTable.setValue(newRowIndex,"FEE_MODE","0");
	feeListTable.setValue(newRowIndex,"FEE_MODE_NAME","营业费用");
	feeListTable.setValue(newRowIndex,"FEE_TYPE_CODE","1111");
	feeListTable.setValue(newRowIndex,"FEE_TYPE_NAME","呼转订购手续费");
	feeListTable.setValue(newRowIndex,"OLD_FEE","3000");
	feeListTable.setValue(newRowIndex,"FEE","2400");
	feeListTable.setValue(newRowIndex,"FREE_FEE","600");
	$.resizeHeight(); 
}