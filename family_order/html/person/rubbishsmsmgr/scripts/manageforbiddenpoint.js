function deleteData(){
	var data = $.table.get("BadnessTradeTable").getCheckedRowDatas();//获取选择中的数据
	
	var hasDis = true;
	if(data.length > 0) {
		var id = "";
		var operType = "";
		for(var i=0;i<data.length;i++) {
			if(id!="") {
				id += ",";
			}
			id += data.get(i).get("INFO_RECV_ID");
			operType = data.get(i).get("OPERATE_TYPE");
			if(operType == '2') {
				//alert(operType);
				hasDis = false;
			}
		}
		//alert(hasDis);
		if(!hasDis) {
			alert('选择的待处理数据中已有无效数据!');
		}
		
		var param = "&REVC_IDS=" + id;
		$.beginPageLoading("数据处理中..");
		$.ajax.submit('QueryCondPart', 'disableData', param, 'QueryListPart',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}else {
		alert('请选择待处理的数据!');
	}
	/*var boxObj=document.getElementsByName("chkbox");
	var ids = "";
	for(i=boxObj.length;i>0;i--)
	{ 
		if(boxObj[i-1].checked==true)
		{
			
				var tdObj=boxObj[i-1].parentNode.parentNode;//单元格，行
				
				var rowValue = boxObj[i-1].value;
				var addFlag = true;
				for(var j=0;j<tdObj.childNodes.length;j++){
					//过滤无效数据
					if(tdObj.childNodes[j].innerHTML!=null){
						var statusStr=tdObj.childNodes[j].innerHTML;
						if(statusStr=='无效'){
							addFlag = false;
							break ;
						}
					}
				}
				if(!addFlag){
					continue;
				}
				if(ids!=""){
					ids+=",";
				}
				ids+=rowValue;
		}
	}
	//提交
	if(ids.length>0){
		var params =  "ids="+ids;
		$.beginPageLoading("数据处理中..");
		$.ajax.submit('QueryCondPart', 'disableData', params, 'QueryListPart',function(data){
			if(data.length<1 ){
				MessageBox.alert("状态","没有查询到任何数据。");
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}else{
		alert("请选择需要操作的有效 数据");
	}*/
}



//举报处理查询
function queryForbiddenList(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryForbiddenList', null, 'QueryListPart',function(data){
		if(data.get("ALERT_INFO")!=null) {
			MessageBox.alert("状态","没有查询到任何数据。");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
