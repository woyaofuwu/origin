function queryInfos(obj)
{
	if(!$.validate.verifyAll("CondPart")){
		return false;
	}
	var beginDate = $("#cond_BEGIN_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	var replyState = $("#cond_REPLY_STATE").val();
	
	var data = "&cond_BEGIN_DATE=" + beginDate + "&cond_END_DATE=" + endDate + "&cond_REPLY_STATE=" + replyState ;
	
	$.beginPageLoading('正在查询业务信息...');
	$.ajax.submit(null,'queryInfos',data,'qryPart',function(data){
		$.endPageLoading();
		hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function queryBulletinInfo(obj)
{
	var ibsysid = $(obj).attr('ibsysid');
	var subIbsysid = $(obj).attr('subIbsysid');
	var recordNum = $(obj).attr('recordNum');
	var groupSeq = $(obj).attr("groupSeq");

	var data = "&IBSYSID=" + ibsysid + "&RECORD_NUM=" + recordNum + "&SUB_IBSYSID=" + subIbsysid + "&GROUP_SEQ=" + groupSeq;
	
	$.beginPageLoading('正在查询业务信息...');
	$.ajax.submit(null,'queryBulletinInfos',data,'qryBulletinItem',function(data){
		$.endPageLoading();
		showPopup('bulletinPopup','qryBulletinItem', true);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}