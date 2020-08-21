function queryInfo(){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('QueryCondPart', 'queryInfo', '', 'results', function(data){
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

function checkOrder(){
	 var flag=false;
	 var  radioObj=document.getElementsByName("orderid");
	 for(var i=0; i<radioObj.length;i++){   
		 if(radioObj[i].checked){
	  			flag=true;
	  	}
	 }
	 if(!flag){
	  	alert('请选择执行的子订单！');
	  	return false;
	 }
	 return flag;
}
function rowClickShowInfo(){
	var data = $.table.get("resultsTable").getRowData()
	var oId = data.get("OID");
	$.beginPageLoading("信息查询中..");
    $.ajax.submit('', 'queryOrderProductInfo', '&OID='+oId, 'prodList', function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
		var table = $.table.get("prodTable");//获取 JavaScript 包装的 Table 对象
		table.cleanRows();//清空表格的内容
    });
    return true;
} 