function queryInfo(){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('QueryCondPart', 'queryReturnOrderInfo', '', 'results', function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
		var table = $.table.get("resultsTable");//获取 JavaScript 包装的 Table 对象
		table.cleanRows();//清空表格的内容
    });
    return true;
}

function checkBack(){
	 var flag=false;
	 var  tableObj=document.getElementsByName('inforecvid');
	 for(var i=0; i<tableObj.length;i++){   
		 if(tableObj[i].checked){
	  			flag=true;
	  	}
	 }
	 if(!flag){
	  	$.showErrorMessage("请选择反馈的订单！");
	  	return false;
	 }
	 
	 var rspResult = $('#RSP_RESULT').val(); 
	 var refundFee = $('#REFUND_FEE').val(); 
	 if(!$.validate.verifyAll("subPart")) {
		return false;
	 }
	 if(rspResult == "01" && (refundFee == "" || refundFee == undefined)){
		 $.showErrorMessage("同意部分退款,退款金额不能为空！");
		 return false;
	 }
	 $.beginPageLoading("数据提交中..");
     $.ajax.submit('results', 'feedbackReturnOrder', '', 'results', function(data){
     	$.showSucMessage("反馈处理成功")
		$.endPageLoading();
	 },
	
	 function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
     });
	 return flag;
}
function showSubPage(param){
	$.popupDialog("electricBusiness.QueryReturnSubOrder", "queryReturnSubOrder", param, "子订单信息", 900, 310,"");
}