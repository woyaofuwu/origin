// 集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	
	// 填充集团客户显示信息
	insertGroupCustInfo(data);
}

// 集团资料查询失败后调用的方法
function selectGroupErrorAfterAction(data){

	//清空填充的集团客户信息内容
    clearGroupCustInfo();
}

// 查询费用信息
function queryFeeList(){
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	var groupId = $("#POP_cond_GROUP_ID").val();
	
	if(groupId == null || groupId == ""){
		alert("集团客户编码不能为空");
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "qryOneFeeList", null, "feePart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}