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
		$("#userProductInfo").val('');
		$("#userProductInfo").attr('disabled', 'true');
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

/**产品拼串*/
function setData(obj){
	if($("#userProductInfo").val()=='') {
		alert('请选择集团产品!');
		return false;
	}
	
	var tempElements = '[]';
	$("#SELECTED_ELEMENTS").val(tempElements);
	commSubmit();	
	return true;
}

function commSubmit() {
	var group_id = $("#GROUP_ID").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var cust_id = $("#CUST_ID").val();
	var user_eparchy_code = $('#USER_EPARCHY_CODE').val();
	var grp_sn = $('#GRP_SN').val();
	
	if ( elements == "" ) 
		return false;    
	var idata = $.DatasetList(elements);
	var info = $.DataMap();
	info.put("ELEMENT_INFO", idata);
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", cust_id);
	info.put('GRP_SN', grp_sn);
	info.put('USER_EPARCHY_CODE',user_eparchy_code);
	
	//add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
	if($('#MEB_VOUCHER_FILE_LIST')){
		var mebVoucherFileList = $('#MEB_VOUCHER_FILE_LIST').val();
		if( mebVoucherFileList== ""){
			alert("请上传凭证附件！");
			return false;
		}else{
			info.put('MEB_VOUCHER_FILE_LIST', mebVoucherFileList);
		}		
	}
	if($('#AUDIT_STAFF_ID')){
		var auditStaffId = $('#AUDIT_STAFF_ID').val();
		if( auditStaffId== ""){
			alert("请选择稽核员！");
			return false;
		}else{
			info.put('AUDIT_STAFF_ID', auditStaffId);
		}		
	}
	//alert(info);
	//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
	
 	parent.$('#POP_CODING_STR').val(productName);
 	parent.$('#CODING_STR').val(info);
 	
 	parent.hiddenPopupPageGrp();
}

function errorAction() {
	clearGroupCustInfo();
}

function addChoiceArea (str) {
	$('#'+str).click();
}

/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var eparchyCode=$("#MEB_EPARCHY_CODE").val();
	        var param="&MEB_EPARCHY_CODE="+eparchyCode;
	        return param;
	       }
	});
})();
