// 查询集团客户信息(成功)
function selectGroupAfterAction(data){
	
	// 加载客户信息
	insertGroupCustInfo(data);
}

// 查询集团客户信息(失败)
function selectGroupErrorAfterAction(){
	
	// 清空填充的集团客户信息内容
    clearGroupCustInfo();
}


// 设置返回值
function setReturnData(){
	
	var groupId = $("#GROUP_ID").val();
	
	if(groupId == null || groupId == ""){
		alert("集团客户编码不能够为空!");
		return;
	}
	
	// 设置返回值
	var valueData = $.DataMap();
	
	valueData.put("GROUP_ID", groupId);
	
	//$.setReturnValue({'POP_CODING_STR': "集团编码：" + groupId}, false);
 	//$.setReturnValue({'CODING_STR': valueData.toString()}, true);
 	
 	parent.$('#POP_CODING_STR').val("集团编码：" + groupId);
	parent.$('#CODING_STR').val(valueData);
 	
	parent.hiddenPopupPageGrp();
}