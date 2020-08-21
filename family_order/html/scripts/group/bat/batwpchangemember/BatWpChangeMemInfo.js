// 设置返回值
function setReturnData(){
	
	var group_id = $("#GROUP_ID").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var cust_id = $("#CUST_ID").val();
	var user_eparchy_code = $('#USER_EPARCHY_CODE').val();
	var grp_sn = $('#GRP_SN').val();
	var oper_type = "";	//变更方式
	
	//处理ELEMENT_INFO
	var disParam = $("#DIS_PARAM").val();
	var effectType = $("#EFFECT_TYPE").val();
	var infoList = $.DatasetList();
	var info = $.DataMap();
	info.put("MODIFY_TAG","2");
	info.put("REMARK",effectType);		//生效方式
	info.put("ELEMENT_TYPE_CODE","D");
	//处理Attr
	var attrList = $.DatasetList();
	var attr = $.DataMap();
	attr.put("ATTR_VALUE",disParam);
	attr.put("ATTR_CODE","114485");

	attrList.add(attr);
	info.put("ATTR_PARAM",attrList);
	//处理生失效时间
	var date = new Date();
	var time = '';
	date.setTime(date.getTime());
	if(effectType == 0){
		//计算立即生效时间
		time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+1),2) + "-" + PrefixInteger(date.getDate(),2);
		time += " "+PrefixInteger(date.getHours(),2)+":"+PrefixInteger(date.getMinutes(),2)+":"+PrefixInteger(date.getSeconds(),2);
		info.put("START_DATE",time);
		info.put("END_DATE","2050-12-31 23:59:59");
	}else{
		//计算下月生效时间
		if(date.getMonth()+2 > '12'){
			  time = (date.getFullYear()+1)+"-01-01";
		  }else{
			  time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+2),2) + "-01";
		  }
		time += " 00:00:00";
		info.put("START_DATE",time);
		info.put("END_DATE","2050-12-31 23:59:59");
	}
	
	infoList.add(info);
	// 设置返回值
	var valueData = $.DataMap();
	
	var operTypeName = "修改";

	// 设置返回值
	valueData.put("ELEMENT_INFO", infoList);
	valueData.put("GROUP_ID", group_id);
	valueData.put("PRODUCT_ID",grpProductId);
	valueData.put("USER_ID", grpUserID);
	valueData.put("CUST_ID", cust_id);
	valueData.put('OPER_TYPE', oper_type);
	valueData.put('MEB_FILE_SHOW', "true");
	
	//valueData.put("disParam", $("#DIS_PARAM").val());
	//valueData.put("effectType", $("#EFFECT_TYPE").val());

	//处理稽核
	if($('#MEB_VOUCHER_FILE_LIST')){
		var mebVoucherFileList = $('#MEB_VOUCHER_FILE_LIST').val();
		if( mebVoucherFileList== ""){
			alert("请上传凭证附件！");
			return false;
		}else{
			valueData.put('MEB_VOUCHER_FILE_LIST', mebVoucherFileList);
		}		
	}
	if($('#AUDIT_STAFF_ID')){
		var auditStaffId = $('#AUDIT_STAFF_ID').val();
		if( auditStaffId== ""){
			alert("请选择稽核员！");
			return false;
		}else{
			valueData.put('AUDIT_STAFF_ID', auditStaffId);
		}		
	}
 	
 	parent.$('#POP_CODING_STR').val(productName);
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}

function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea',function(data){
		
		if(data.get("EFFECT_NOW")=='true')
		{
			selectedElements.isEffectNow = true;
		}
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function errorAction() {
	clearGroupCustInfo();
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
		
		//var productId = $("#PRODUCT_ID").val();
		//var userId = $("#GRP_USER_ID").val();
		//var groupId = $('#POP_cond_GROUP_ID').val();
		//$.beginPageLoading('正在查询产品包元素中...');
		//$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GROUP_ID='+groupId,'ProductPackagePart,GroupUserPart,RolePart',function(data){
		//	
		//	renderPayPlanSel(productId,userId);
		//	
		//	$.endPageLoading();
		//},function(error_code,error_info,derror){
		//	$.endPageLoading();
		//	showDetailErrorInfo(error_code,error_info,derror);
		//});
	}
}


function setHiddenData(Arry) {
	$("#PRODUCT_ID").val(Arry[0]);
	$("#GRP_PRODUCT_NAME").val(Arry[1]);
	$("#GRP_SN").val(Arry[2]);
	$("#GRP_USER_ID").val(Arry[3]);
}

function PrefixInteger(num, length) {
	return (Array(length).join('0') + num).slice(-length);
}