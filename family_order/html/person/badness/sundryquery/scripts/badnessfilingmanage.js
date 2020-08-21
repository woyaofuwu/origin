function queryBadnessInfo() {
	if(!verifyAll('badInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('badInfoPart,badInfoNav,dealReamrkPart', 'queryBadInfos', null, 'badInfoTablePart,TipInfoPart', function(data){
     	$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		$.endPageLoading();
		if(data.get("ALERT_INFO") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function dealRemark() {
	var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	
	if(!verifyAll('dealReamrkPart')) {
	   return false;
   	}
   	
	var remark = $("#cond_DEAL_REMARK_MAKEUP").val();
	var param = "&BADNESS_TABLE=" + data + "&DEAL_REMARK_MAKEUP=" + remark;
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('dealReamrkPart', 'updateBadInfos', param, null, function(data){
		$.endPageLoading();
		$.showSucMessage("归档成功!");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function archRemark() {
	/*var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}*/
	
	MessageBox.prompt('归档意见', '<span class="e_required">请输入归档意见说明：</span>', function(btn, val){showRemarkText(btn, val);});
}

function showRemarkText(btn,text)
{
	if(btn=='ok' && getTextLength(text)>2)
	{
		alert('归档意见说明最大长度为2000个字符或1000个汉字');
		return false;
	}
	if(btn=='ok' && text!="")
	{
		//ajaxSubmit(this,'updateBadInfos','&DEAL_REMARK_MAKEUP='+text,'refreshList');
		//self.afterAction="alert('归档成功')";
		alert('归档成功');
	}
	if(btn=='ok' && text=="") {
		alert('归档意见说明不能为空');
	}
}

function getTextLength(v)
{
	var len=0;
	for(i=0;i<v.length;i++)
	{
	    if(v.charCodeAt(i)>256)
	     	len+=2;
	    else
	     	len++;
	}
	return len;
}

function exportBeforeAction(obj) {
	var data = $.table.get("badTable").getTableData("INFO_RECV_ID", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}