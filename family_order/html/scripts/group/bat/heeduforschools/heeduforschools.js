function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function refreshProduct(){
	var productVal = $("#userProductInfo").val();
	if("" == productVal || null == productVal || undefined == productVal) {
		alert('请选择集团产品!');
		return false;
	}else {
		var productNameStr = $("#userProductInfo :selected").text();
		var productNameArry = productNameStr.split("|");
		setHiddenData(productNameArry);
	}
}

function setHiddenData(Arry) {
	$("#PRODUCT_ID").val(Arry[0]);
	$("#GRP_PRODUCT_NAME").val(Arry[1]);
	$("#GRP_SN").val(Arry[2]);
	$("#GRP_USER_ID").val(Arry[3]);
}

function commSubmit() {
	if(!$.validate.verifyAll("scrollPart")) {
		return false;
	}
	var group_id = $("#GROUP_ID").val();
	var effectNow = $("#userInfoEffectNow").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var cust_id = $("#CUST_ID").val();
	var oper_code = $("#OPERCODE").val();
	var grp_sn = $('#GRP_SN').val();
	var info = $.DataMap();
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", cust_id);
	info.put("OPER_CODE", oper_code);
	info.put('GRP_SERIAL_NUMBER', grp_sn);
    parent.$('#POP_CODING_STR').val(group_id+productName);
    parent.$('#CODING_STR').val(info);

    parent.hiddenPopupPageGrp();
}

function errorAction() {
	clearGroupCustInfo();
}
