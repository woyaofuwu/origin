function queryProductInfos() {
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId && groupId != "")
	{
		$.beginPageLoading('正在查询业务信息...');
		ajaxSubmit(this,'ProductInfoPart',"&GROUP_ID="+groupId,"queryLineInfo",function(data){
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});	
	}
}

function addSN(sn) {
	$("#ACCEPT_PHONE_CODE").val(sn);
};

function submit(){
	if(!$.validate.verifyAll("contentItem")){
		return false;
	}
	var dataStr = "";
	
	dataStr = dataStr + "&ACCEPT_PHONE_CODE=" + $("#ACCEPT_PHONE_CODE").val();
	dataStr = dataStr + "&LINK_PHONE_CODE=" + $("#LINK_PHONE_CODE").val();
	dataStr = dataStr + "&LINK_NAME=" + $("#LINK_NAME").val();
	dataStr = dataStr + "&WORKFORM_TITLE=" + $("#WORKFORM_TITLE").val();
	dataStr = dataStr + "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val();
	dataStr = dataStr + "&EXPLAIN_CONTENT=" + $("#EXPLAIN_CONTENT").val();
	dataStr = dataStr + "&DEAL_CLASS_CODE=" + $("#DEAL_CLASS_CODE").val();
	dataStr = dataStr + "&ACCEPT_EPARCHY_CODE=" + $("#ACCEPT_EPARCHY_CODE").val();
	dataStr = dataStr + "&SP_CODE=" + $("#SP_CODE").val();
	dataStr = dataStr + "&SP_NAME=" + $("SP_NAME").val();
	dataStr = dataStr + "&GLOBAL_OR_LOCAL=" + $("#GLOBAL_OR_LOCAL").val();
	dataStr = dataStr + "&CONTENT=" + $("#CONTENT").val();
	
	$.beginPageLoading('正在查询业务信息...');
	ajaxSubmit(this,'',dataStr,"submit",function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});	
}