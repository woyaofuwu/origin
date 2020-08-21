 function setReturnData(){
    var isCallOutChecked = $("#CALL_OUT").attr("checked");
    var isCallInChecked = $("#CALL_IN").attr("checked");
    
    var callOutTag = $("#CALL_OUT_TAG").val();
    var callInTag = $("#CALL_IN_TAG").val();
    
    var callOutRight = $("#CALL_OUT_RIGHT").val();
    var callInRight = $("#CALL_IN_RIGHT").val();
    
    // 是否选择成员服务
    if((isCallOutChecked == false) && (isCallInChecked == false)){
    	alert("请选择成员服务！");
    	return false;
    }
    
    // 是否选择处理方式
    if((callOutTag == null || callOutTag == "") && (callInTag == null || callInTag == "")){
    	alert("请选择处理方式！");
    	return false;
    }  
    
    
    if((isCallOutChecked == true) && (callOutTag == null || callOutTag == "")){
    	$("#CALL_OUT_TAG").focus();
    	alert("请选择限制呼叫VPMN外电话处理方式！"); // 主叫
    	return false;
    }
    
    if(callOutTag != null && callOutTag != "0"){
    	if((isCallOutChecked == true) && (callOutRight == null || callOutRight == "")){
    		$("#CALL_OUT_RIGHT").focus();
    		alert("请选择限制呼叫VPMN外电话主叫权限！");
    		return false;
    	}
    }
    
    // 限制接听VPMN电话
    if((isCallInChecked == true) && (callInTag == null || callInTag == "")){
    	$("#CALL_IN_TAG").focus();
    	alert("请选择限制接听VPMN外电话处理方式！"); // 被叫
    	return false;
    }
    
    if(callInTag != null && callInTag != "0"){
    	if((isCallInChecked == true) && (callInRight == null || callInRight == "")){
    		$("#CALL_IN_RIGHT").focus();
    		alert("请选择限制呼叫VPMN外电话被叫权限！");
    		return false;
   		}
    }
    
   var callOut = "";
   var callIn = "";
    
    // 1-表示限制呼叫VPMN外电话 2-表示限制接听VPMN外电话 0-不做处理
    if(isCallOutChecked == true){
    	callOut = "1";
    }else{
    	callOut = "0";
    }
    
    if(isCallInChecked == true){
    	callIn = "2";
    }else{
    	callIn = "0";
    }
   
    if(callOutTag == null || callOutTag == ""){
    	callOutTag = "3";
    }
    
    if(callInTag == null || callInTag == ""){
    	callInTag = "3";
    }
    
    
    if(callOutRight == null || callOutRight == ""){
    	callOutRight = "3";
    }
    
    if(callInRight == null || callInRight == ""){
    	callInRight = "3";
    }
    
    // 设置返回值
    var valueData = $.DataMap();    
    valueData.put("CALL_OUT", callOut);
    valueData.put("CALL_IN", callIn);
    valueData.put("CALL_OUT_TAG", callOutTag);
    valueData.put("CALL_IN_TAG",callInTag);
    valueData.put("CALL_OUT_RIGHT",callOutRight);
    valueData.put("CALL_IN_RIGHT",callInRight);
    //add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
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
	//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
    
    //$.setReturnValue({"POP_CODING_STR": "批量条件"}, false);
 	//$.setReturnValue({"CODING_STR": valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("批量条件");
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
 	
}

function checkCallOut(){
	
 	if($("#CALL_OUT").attr("checked")){
 		$('#LI_CALL_OUT_TAG').css("display", "");
 		$('#LI_CALL_OUT_RIGHT').css("display", "");
 	}else{
 		$('#LI_CALL_OUT_TAG').css("display", "none");
 		$('#LI_CALL_OUT_RIGHT').css("display", "none");
 	}
 }
 
function checkCallIn(){
    var callin = $('#pam_CALL_IN').val();
    
 	if($("#CALL_IN").attr("checked")){
 		$('#LI_CALL_IN_TAG').css("display", "");
 		$('#LI_CALL_IN_RIGHT').css("display", "");
 	}else{
 		$('#LI_CALL_IN_TAG').css("display", "none");
 		$('#LI_CALL_IN_RIGHT').css("display", "none");
 	}
}

function changeCallOutTag(){
	
	var callOutTag = $("#CALL_OUT_TAG").val();
	
    if(callOutTag == null || callOutTag == "" || callOutTag == "0"){
    	$('#LI_CALL_OUT_RIGHT').css("display", "none");
    } else {
    	$('#LI_CALL_OUT_RIGHT').css("display", "");
    }
}

function changeCallInTag(){
	
	var callInTag = $("#CALL_IN_TAG").val();

    if(callInTag == null || callInTag == "" || callInTag == "0"){
    	$('#LI_CALL_IN_RIGHT').css("display", "none");
    } else {
    	$('#LI_CALL_IN_RIGHT').css("display", "");
    }
}
