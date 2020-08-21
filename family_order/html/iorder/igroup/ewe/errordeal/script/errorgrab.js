function qryNodeInfos()
{
	var busiformNodeId = $("#BUSIFORM_NODE_ID").val();
	if(busiformNodeId == "")
	{
		MessageBox.alert("BUSIFORM_NODE_ID不能为空！");
		return;
	}
	ajaxSubmit('', 'queryFlow', '&BUSIFORM_NODE_ID='+busiformNodeId, 'QryCondPart', function(data) {
	}, function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示",info,null, null, detail);
	});
}

function submit()
{
	var busiformNodeId = $("#BUSIFORM_NODE_ID").val();
	var nodeId = $("#NODE_ID").val();
	
	if(busiformNodeId == "")
	{
		MessageBox.alert("BUSIFORM_NODE_ID不能为空！");
		return;
	}
	
	if(nodeId == "")
	{
		MessageBox.alert("NODE_ID不能为空！");
		return;
	}
		
	$.beginPageLoading("处理中...");
	ajaxSubmit('', 'submit', '&BUSIFORM_NODE_ID='+busiformNodeId+'&NODE_ID='+nodeId, '', function(data) {
		$.endPageLoading();
		MessageBox.alert("处理成功！");
	}, function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示",info,null, null, detail);
	});
}
