function queryWorkform(){
	debugger;
	var ibsysId = $('#IBSYSID').val();
	var confIbsysid = $('#RSRV_STR5').val();
	var groupId = $('#GROUP_ID').val();
	if (ibsysId=='' && confIbsysid =='' && groupId == ''){
		MessageBox.alert("请输入查询条件!");
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryWorkform", "&IBSYSID="+ibsysId+"&RSRV_STR5="+confIbsysid+"&GROUP_ID="+groupId, "queryPart",function(data){
		$.endPageLoading();
	},null);
}

function release(obj){
	debugger;
	var ibsysid = $(obj).attr("ibsysid");
	var rsrv_str5 = $(obj).attr("rsrv_str5");
	var group_id = $(obj).attr("group_id");
	var history_tag = $(obj).attr("history_tag");
	MessageBox.alert("请撤单进行解绑勘察单!");
//	ajaxSubmit("", 'release', '&IBSYSID='+ibsysid+'&RSRV_STR5='+rsrv_str5+'&GROUP_ID='+group_id+'&HISTORY_TAG='+history_tag, 'queryPart',function(data){
//		queryWorkform();
//	},null);
}