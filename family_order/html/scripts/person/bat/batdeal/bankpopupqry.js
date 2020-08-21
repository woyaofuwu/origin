function tableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var bankCode = json.get('BANK_CODE','');
	var bank = json.get('BANK','');
	setPopupReturnValue(bankCode,bank);
}

function utableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var pay_name = json.get('PAY_NAME','');
	var pay_mode_code = json.get('PAY_MODE_CODE','');
	var pay_mode = json.get('PAY_MODE','');
	var contract_no = json.get('CONTRACT_NO','');
	var post_code = json.get('POST_CODE','');
	var post_address = json.get('POST_ADDRESS','');
	var bank_code = json.get('BANK_CODE','');
	var bank_name = json.get('BANK','');
	var bank_acct_no = json.get('BANK_ACCT_NO','');
	var returnValue = pay_name + '|' ;
	returnValue += pay_mode_code ;
	returnValue += '|' ;
	returnValue += contract_no ;
	returnValue += '|' ;
	returnValue += post_code ;
	returnValue += '|' ;
	returnValue += post_address ;
	returnValue += '|' ;
	returnValue += bank_code ;
	returnValue += '|' ;
	returnValue += bank_name ;
	returnValue += '|' ;
	returnValue += bank_acct_no ;
	returnValue += '|' ;
	returnValue += pay_mode ;
	setPopupReturnValue(returnValue,'选择用户');
}

function queryBankInfo(){
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryBankInfo', null, 'QueryListPart,TipInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryUsersByBank(){

	var bankCode = $("#cond_BANK_CODE").val();
	if(bankCode == null || bankCode == ""){
		alert('请先选择银行！'+bankCode);
		return false;
	}
	var bankAcctNo = $("#cond_BANK_ACCT_NO").val();
	if(bankAcctNo == null || bankAcctNo == ""){
		alert('银行账号不允许为空！');
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryUsersByBank', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}