//清空页面显示的集团客户信息内容
function clearGroupUserInfo(){
	
	$('#SPAN_GROUPUSERINFO_SN').html('');
	$('#SPAN_GROUPUSERINFO_USER_ID').html('');
	$('#SPAN_GROUPUSERINFO_OPEN_DATE').html('');
	$('#SPAN_GROUPUSERINFO_PRODUCT_ID').html('');
	$('#SPAN_GROUPUSERINFO_PRODUCT_NAME').html('');
	$('#SPAN_GROUPUSERINFO_BRAND_NAME').html('');
	
	$('#GRP_SN').val('');
	$('#GRP_USER_ID').val('');
	$('#GRP_BRAND_CODE').val('');
	$('#GRP_PRODUCT_ID').val('');
	$('#GRP_PRODUCT_NAME').val('');
	$('#GRP_USER_EPARCHYCODE').val('');
}

//生成页面上的集团客户信息内容
function insertGroupUserInfo(data){

	$('#SPAN_GROUPUSERINFO_SN').html(data.get('SERIAL_NUMBER'));
	$('#SPAN_GROUPUSERINFO_USER_ID').html(data.get('USER_ID'));
	$('#SPAN_GROUPUSERINFO_OPEN_DATE').html(data.get('OPEN_DATE'));
	$('#SPAN_GROUPUSERINFO_PRODUCT_ID').html(data.get('PRODUCT_ID'));
	$('#SPAN_GROUPUSERINFO_PRODUCT_NAME').html(data.get('PRODUCT_NAME'));
	$('#SPAN_GROUPUSERINFO_BRAND_NAME').html(data.get('BRAND_NAME'));
	
	$('#GRP_SN').val(data.get('SERIAL_NUMBER'));
	$('#GRP_USER_ID').val(data.get('USER_ID'));
	$('#GRP_BRAND_CODE').val(data.get('BRAND_CODE'));
	$('#GRP_PRODUCT_ID').val(data.get('PRODUCT_ID'));
	$('#GRP_PRODUCT_NAME').val(data.get('PRODUCT_NAME'));
	$('#GRP_USER_EPARCHYCODE').val(data.get('EPARCHY_CODE'));
}
