
/**产品拼串*/
function setData(obj){
	commSubmit();	
	return true;
}

function commSubmit() {
	var markFlag = $('input:radio[name="MARK_FLAG"]:checked').val();
	var POP_CODING_STR = "1"==markFlag ? "新增欠费不截止代付关系" : "删除欠费不截止代付关系";
	var info = $.DataMap();
	info.put("MARK_FLAG", markFlag);
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
	//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
	
 	parent.$('#POP_CODING_STR').val(POP_CODING_STR);
 	parent.$('#CODING_STR').val(info);
 	
 	parent.hiddenPopupPageGrp();
}

