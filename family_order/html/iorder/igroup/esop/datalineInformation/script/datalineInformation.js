$(function(){
	debugger;
	var newValue = $("#ATTR_NEW_VALUE").val();
	var length = $("#LENGTH").val();
	var datatypeA = $("#DEAL_TYPE_A").val();
	var lengthsFalse = $("#LENGTHS_FALSE").val();
	if("2"!=newValue){
		 $("#barchFile").css("display","none");
		 $("#closeSheet").css("display","none");
	}
	if("true"==length){
		 $("#barchRestartSend").css("display","");
		 $("#renewSheet").css("display","none");
		 if("34"==datatypeA){
			 $("#barchRestartSend").css("display","none");
			 $("#renewSheet").css("display","");
		 }
		 if("false"==lengthsFalse){
			 $("#barchRestartSend").css("display","none");
			 $("#renewSheet").css("display","");
		 }
	}
	if("false"==length){
		 $("#barchRestartSend").css("display","none");
		 $("#renewSheet").css("display","");
	}

});

function barchRestartSend(){
	debugger;
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	var dataset = new Wade.DatasetList();
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选需要批量重派的专线!");
		return false;
	}
	//判断批量驳回需要选择全部的驳回单
	var lineLength = rowDatas.length;
	var lengthsNum = $("#LENGTHS_SUM").val();
	if( lengthsNum != lineLength ){
		$.validate.alerter.one(rowDatas,"您未全部勾选【OSS工单驳回】状态的专线，提示：【一单多线驳回单只能一次全部重派!】");
		return false;
	}
	
	var busiFormId = $("#BUSIFORM_ID").val();
	var busiFormNodeId = $("#BUSIFORM_NODE_ID").val();
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	var tableList = myTable.getData(true);
	for(var i=0;i<tableList.length;i++){
		var data = tableList.get(i);
		var paramValue = data.get("PARAMVALUE");
		var productNo = data.get("PRODUCT_NO");
		if("P"== paramValue){
			dataset.add(data);
		}else{
			 $.validate.alerter.one(rowDatas,"您选择的专线实列号【 "+productNo+" 】不是驳回重派单，不能重派!");
			  return false;
		}		
	}
	if(""!= dataset){
		
		var  pram = "&BUSIFORM_ID="+busiFormId+"&BUSIFORM_NODE_ID="+busiFormNodeId+"&LENGTHS_SUM="+lengthsNum+"&BPM_TEMPLET_ID="+bpmTempletId;
		//openNav("重派批量操作", "","igroup.datalineInformation.BatchRestartSend=querydataLineInfoList"+pram, "", "", "");
		//var url = $.redirect.buildUrl("/order/iorder", "igroup.datalineInformation.BatchRestartSend", "querydataLineInfoList", );
		//redirectNavByUrl("重派批量操作", url, "");
		openNav("重派批量操作", "igroup.datalineInformation.BatchRestartSend", "querydataLineInfoList",pram, "/order/iorder");
	}
	

}

function renewSheet(){
	debugger;
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选需要重派的专线!");
		return false;
	}
	
	var lineLength = rowDatas.length;
	var datatypeA = $("#DEAL_TYPE_A").val();
	if("34"==datatypeA){
		if( 1 < lineLength ){
			$.validate.alerter.one(rowDatas,"拆机单不能批量重派，只能选择一条，请发起单条重派!");
			return false;
		}
	}
	
	var busiFormId = $("#BUSIFORM_ID").val();
	var busiFormNodeId = $("#BUSIFORM_NODE_ID").val();
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	var dealType = rowDatas.get(0).get("DEAL_TYPE_1");
	var serialNo = rowDatas.get(0).get("SERIALNO");
	var ibsysId =  rowDatas.get(0).get("IBSYSID");//工单流水号
	var productNo = rowDatas.get(0).get("PRODUCT_NO");//专线实列号
	var productId = rowDatas.get(0).get("PRODUCT_ID");//产品ID
	var dealType = rowDatas.get(0).get("DEAL_TYPE_1");//操作状态
	var nodeId = rowDatas.get(0).get("NODE_ID");
	var groupId = rowDatas.get(0).get("EOMS_CUSTOMNO");
	var paramValue = rowDatas.get(0).get("PARAMVALUE");
	var recordNum = rowDatas.get(0).get("RECORD_NUM");
	if("P"== paramValue){
	openNav("重派操作", "igroup.datalineInformation.RenewWorkSheetEdite", "queryData", "&BUSIFORM_ID="+busiFormId+"&BUSIFORM_NODE_ID="+busiFormNodeId+"&SERIALNO="+serialNo+"&IBSYSID="+ibsysId+"&PRODUCT_NO="+productNo+"&PRODUCT_ID="+productId+"&DEAL_TYPE="+dealType+"&NODE_ID="+nodeId+"&GROUP_ID="+groupId+"&RECORD_NUM="+recordNum+"&BPM_TEMPLET_ID="+bpmTempletId, "/order/iorder");
	}else{
		 $.validate.alerter.one(rowDatas,"您选择的专线实列号【 "+productNo+" 】不是驳回重派单，不能重派!");
		  return false;
	}	
}

