function submit(){
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	var table = document.getElementsByName("productNo").length;
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选需要提交中继号的专线，请勾选所有的专线！");
		return false;
	}
	var lineLength = rowDatas.length;
	if( table != lineLength ){
		$.validate.alerter.one(rowDatas,"您没有选择所有的专线填写中继号，请重新选择！");
		return false;
	}
	var dealType = rowDatas.get(0).get("DEAL_TYPE_1");
	var serialNo = rowDatas.get(0).get("SERIALNO");
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var productNo = rowData.get("PRODUCT_NO");
		var zjId = i+"ZJ";
		var zj = document.getElementById(zjId).value;
		if(""== zj || zj==null){
			$.validate.alerter.one(rowDatas,"该专线实例号："+productNo+"没有填写中继号，请重新填写！");
			return false;
		}
		rowData.put("ZJ",zj);
	}
	$.beginPageLoading("数据提交中，请稍后...");
	/*$.ajax.submit("", "submitZj", "&SUBMIT_PARAM="+rowDatas.toString(), "QueryListPart", function(data){
		$.endPageLoading();
		if(resultCode =='0'){
			 MessageBox.success("操作成功！");
		 }
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);*/
	
	$.ajax.submit("", "submitZj", "&SUBMIT_PARAM="+rowDatas.toString(), "QueryListPart", function(data){
		$.endPageLoading();
		errorInfo = data.get("X_RESULTCODE");
		if("0" == errorInfo){
			MessageBox.alert("提示信息",data.get("X_RESULTINFO"));
			return false;
		}else{
			MessageBox.success("操作成功！");
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	}
	);
	
	
}