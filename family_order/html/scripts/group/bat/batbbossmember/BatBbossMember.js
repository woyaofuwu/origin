// 查询集团客户信息(成功)
function selectGroupAfterAction(data){
	
	// 加载客户信息
	insertGroupCustInfo(data);
	
    // 加载集团产品树
    loadGroupProductTree(data);
}

// 查询集团客户信息(失败)
function selectGroupErrorAfterAction(){
	
	// 清空填充的集团客户信息内容
    clearGroupCustInfo();
    
    // 清空集团产品树信息
    cleanGroupProductTree();
}

// 查询产品信息
function queryProduct(note){
	
	var noteId = note.id;
	
	// 通过号码查询的节点不需要再重新刷新用户信息
    if(noteId == "USER_NODE_TREE"){
    	 return true;
    }
    
    var productId = note.value;
    var custId = $("#CUST_ID").val();
    
    var ifCheck = true;
    
    $.ajax.submit(null, "qryUserProduct", "&PRODUCT_ID=" + productId + "&CUST_ID=" + custId, "userProductPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			ifCheck = false;
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
    
    return ifCheck;
}

// 查询资费信息
function queryDiscnt(obj){
	
	var batchOperType = $("#BATCH_OPER_TYPE").val();
	
	var userId = obj.attr("USER_ID");
	var productId = obj.val();
	
	var param = "&BATCH_OPER_TYPE=" + batchOperType + "&USER_ID=" + userId + "&PRODUCT_ID=" + productId;
	
	$.ajax.submit(null, "qryUserDiscnt", param, "hintPart,userDiscntPart,paramPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			ifCheck = false;
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

// 设置返回值
function setReturnData(){
	
	var groupId = $("#GROUP_ID").val();
	
	if(groupId == null || groupId == ""){
		alert("集团客户编码不能够为空!");
		return;
	}
	
	// 获取选择的产品
	var checkProductObj = $("input:radio[name='userProductCheck']:checked");
	
	var productId = checkProductObj.val();
	
	if(productId == null || productId == ""){
		alert("请选择办理的产品信息!");
		return;
	}
	
	// 判断是否支持
	var canAccept = $("#CAN_ACCEPT").val();
	
	if(canAccept != "true"){
	
		alert("该产品不支持此批量类型！");
		return;
	}
	
	// 获取选择的资费
	var discntObj = $("input:checkbox[name='userDiscntCheck']");
	
	var checkDiscntObj = $("input:checkbox[name='userDiscntCheck']:checked");
	
	if(discntObj.length > 0 && checkDiscntObj.length == 0){
		
		alert("至少选择一种优惠!");
		return;
	}
	
	var discntList = $.DatasetList();
	
	for(var i = 0, row = checkDiscntObj.length; i < row; i++){
		
		discntList.add(checkDiscntObj[i].value);
	}
	
	// 设置返回值
	var valueData = $.DataMap();
	
	valueData.put("X_SUBTRANS_CODE", "ITF_CRM_TcsGrpIntf");
	valueData.put("X_TRANS_CODE", "GrpBat");
	valueData.put("GROUP_ID", groupId);
	valueData.put("PRODUCT_ID", productId);
	valueData.put("USER_ID", checkProductObj.attr("USER_ID"));
	valueData.put("BBOSS_DISCNTS", discntList);
	
	//$.setReturnValue({'POP_CODING_STR': "集团编码：" + groupId}, false);
 	//$.setReturnValue({'CODING_STR': valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("集团编码：" + groupId);
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}