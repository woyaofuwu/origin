$(document).ready(function(){
});

function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString() + "&ACCT_INFO="
			+ (data.get("ACCT_INFO")).toString(),
			'BankBindPart', function() {
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}

function tableRowClick() {
	// 获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#BANK_NAME").val(rowData.get("TBL_BANK_NAME"));
	$("#BANK_CARD_NO").val(rowData.get("TBL_BANK_CARD_NO"));
}

/**
 * 提交结果
 * @param obj
 * @returns {Boolean}
 */
function submitDepts(obj) {
	
	var bank_card = $('#BANK_CARD_NO').val();
	if(bank_card==""){
		alert("银联卡不能为空！");
		return false;
	}
	
	return true;
}
