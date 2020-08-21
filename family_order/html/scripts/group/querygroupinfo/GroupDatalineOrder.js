 
 function qryClick(){	
 	//var ibSysId = $("#cond_IB_SYS_ID").val();
 	//var groupId = $("#cond_GROUP_ID").val();
 	//var state = $("#cond_ENET_INFO_QUERY_STATE").val();
 	//var startDate = $("#cond_START_DATE").val();
 	//var endDate = $("#cond_END_DATE").val();
    
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos','','groupNetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }
 

function tableRowClick() {
	var ibsysid = $("#pam_NOTIN_IBSYSID").val();
	var groupId = $("#pam_NOTIN_GROUP_ID").val();
	var state = $("#pam_NOTIN_STATE").val();

}

function dealDataline(){
	var items = getCheckedValues("itemcodes");
	var itemsNum = getCheckedBoxNum("itemcodes");
	
	$.beginPageLoading(itemsNum+"笔在途工单处理中......");
	$.ajax.submit('','dealDatalineOrder','&ITEMS='+items+"&ITEMSNUM="+itemsNum
	,'refreshHintBar,groupNetInfo', function(data){
		successMessage(data);
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function successMessage(result){
	MessageBox.alert("在途工单处理完成:","业务流水号:"+result.get("ORDER_ID"),function(btn){
	});
}



