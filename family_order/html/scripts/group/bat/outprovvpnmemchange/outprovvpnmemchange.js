// 查询VPMN信息
function getVpmnInfo(){

	if(!$.validate.verifyAll("queryForm")) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryVpmnInfo", null, "vpmnPart,productInfoArea", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 设置返回数据
function setReturnData(){
	debugger;
	if(!$.validate.verifyAll("queryForm")) return false;
	
	if(!$.validate.verifyAll("vpmnPart")) return false;
	
	var condSerialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	
	if(condSerialNumber != serialNumber){
		alert("服务号码未校验，请先校验！");
		return false;
	}
	
	var discntCode = $("#DISCNT_CODE").val();
	if(discntCode=="" || discntCode==null){
    	alert("请选择优惠");
    	return false; 
    }
    
    var operType = $("#OPER_TYPE").val();
	if(operType=="" || operType==null){
    	alert("请选择优惠变更方式");
    	return false; 
    }
	
 	$.ajax.submit(this, "validchkVpmn", "&DISCNT_CODE="+discntCode+"&SERIAL_NUMBER="+serialNumber, null, 
		function(data){
			// 设置返回值
			var returnStr = $.DataMap();
			debugger;
			returnStr.put("X_SUBTRANS_CODE", "ITF_CRM_TcsGrpIntf"); 
    		returnStr.put("X_TRANS_CODE", "GrpBat"); 
    
			returnStr.put("SERIAL_NUMBER", serialNumber);
			returnStr.put("VPN_NAME", $("#VPN_NAME").val());
			returnStr.put("GROUP_ADDR", $("#GROUP_ADDR").val());
			returnStr.put("GROUP_CONTACT_PHONE", $("#GROUP_CONTACT_PHONE").val());
			returnStr.put("USER_ID", $("#cond_USER_ID").val()); 
		    returnStr.put("VPN_NO", serialNumber); 
		    returnStr.put("PRODUCT_ID", $("#cond_PRODUCT_ID").val()); //8000
		    returnStr.put('DISCNT_CODE',discntCode);
		    returnStr.put("OPER_TYPE", operType); 
		    
		    var operTypeName = "";
		    if(operType == 0){
		        operTypeName = "新增";
		    }else if(operType == 2){
		        operTypeName = "修改";
		    }
    
		    //add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
			if($('#MEB_VOUCHER_FILE_LIST')){
				var mebVoucherFileList = $('#MEB_VOUCHER_FILE_LIST').val();
				if( mebVoucherFileList== ""){
					alert("请上传凭证附件！");
					return false;
				}else{
					returnStr.put('MEB_VOUCHER_FILE_LIST', mebVoucherFileList);
				}		
			}
			if($('#AUDIT_STAFF_ID')){
				var auditStaffId = $('#AUDIT_STAFF_ID').val();
				if( auditStaffId== ""){
					alert("请选择稽核员！");
					return false;
				}else{
					returnStr.put('AUDIT_STAFF_ID', auditStaffId);
				}		
			}
			//alert(returnStr);
			//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
			
		    //$.setReturnValue({'POP_CODING_STR': "集团VPMN编码:[" + serialNumber + "] 优惠编码:[" + discntCode + "] 处理方式:[" + operTypeName + "]"}, false);
 			//$.setReturnValue({'CODING_STR': returnStr}, true);
 			
 			parent.$('#POP_CODING_STR').val("集团VPMN编码:[" + serialNumber + "] 优惠编码:[" + discntCode + "] 处理方式:[" + operTypeName + "]");
			parent.$('#CODING_STR').val(returnStr);
 	
			parent.hiddenPopupPageGrp();
 			
		},
		function(error_code,error_info, derror){
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
    
}