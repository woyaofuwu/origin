//清空页面显示的集团账户信息内容
function clearGroupAcctInfo(){
	
	$('#SPAN_GROUPACCTINFO_PAY_NAME').html('');
	$('#SPAN_GROUPACCTINFO_ACCT_ID').html('');
	$('#SPAN_GROUPACCTINFO_RSRV_STR3').html('');
	$('#SPAN_GROUPACCTINFO_PAY_MODE_NAME').html('');
	$('#SPAN_GROUPACCTINFO_BANK').html('');
	$('#SPAN_GROUPACCTINFO_BANK_ACCT_NO').html('');
	
	$('#GRP_ACCT_ID').val('');
}

//生成页面上的集团账户信息内容
function insertGroupAcctInfo(data){

	$('#SPAN_GROUPACCTINFO_PAY_NAME').html(data.get('PAY_NAME'));
	$('#SPAN_GROUPACCTINFO_ACCT_ID').html(data.get('ACCT_ID'));
	$('#SPAN_GROUPACCTINFO_RSRV_STR3').html(data.get('RSRV_STR3'));
	$('#SPAN_GROUPACCTINFO_PAY_MODE_NAME').html(data.get('PAY_MODE_NAME'));
	$('#SPAN_GROUPACCTINFO_BANK').html(data.get('BANK'));
	$('#SPAN_GROUPACCTINFO_BANK_ACCT_NO').html(data.get('BANK_ACCT_NO'));
	$('#GRP_ACCT_ID').val(data.get('ACCT_ID'));
}