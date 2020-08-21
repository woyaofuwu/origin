var CONFIRM_FILE;


$(function(){
	//过户依据
	$("#ATTR_CHG_FILE").afterAction(function(e, file){
		CONFIRM_FILE=file.name + "|" + file.fileId;
		
	});
});

function submitApply(){
	var confirmGrpAttrs = $("#PROCEDURE_GRP_ATTRS").val();
	var IBSYSID  = $("#IBSYSID").val();
	var NODE_ID  = $("#NODE_ID").val();
	var BUSIFORM_NODE_ID  = $("#BUSIFORM_NODE_ID").val();
	var BPM_TEMPLET_ID  = $("#BPM_TEMPLET_ID").val();
	var BUSI_CODE  = $("#BUSI_CODE").val();
	var BUSI_TYPE  = $("#BUSI_TYPE").val();
	 $.beginPageLoading();
	 ajaxSubmit("","submitChange", '&PROCEDURE_GRP_ATTRS=' + confirmGrpAttrs+'&CONFIRM_FILE=' + CONFIRM_FILE+ '&IBSYSID=' + IBSYSID+'&NODE_ID=' + NODE_ID+'&BUSIFORM_NODE_ID=' + BUSIFORM_NODE_ID+'&BPM_TEMPLET_ID=' + BPM_TEMPLET_ID+'&BUSI_CODE=' + BUSI_CODE+'&BUSI_TYPE=' + BUSI_TYPE, null, function(data){
			$.endPageLoading();	
			if(data.get("ASSIGN_FLAG") == "true")
			{
				MessageBox.success("审批成功", "定单号："+data.get("IBSYSID"), function(btn){
					if("ext1" == btn){
						debugger;
						var urlArr = data.get("ASSIGN_URL").split("?");
						var pageName = getNavTitle();
						openNav('指派', urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
						closeNavByTitle(pageName);
					}
					if("ok" == btn){
						closeNav();
					}
				}, {"ext1" : "指派"});
			}else if(data.get("ALERT_FLAG")== "true"){
				MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
					if("ext1" == btn){
						var urlArr = data.get("ALERT_URL").split("?");
						var ALERT_NAME = data.get("ALERT_NAME");
						var pageName = getNavTitle();
						openNav(ALERT_NAME, urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
						closeNavByTitle(pageName);
					}
					if("ok" == btn){
						closeNav();
					}
				}, {"ext1" : "下一步"});
			}
			else
			{
				MessageBox.success("审批成功", "定单号："+data.get("IBSYSID"), function(btn){
					if("ok" == btn){
						closeNav();
					}
				});
			}
			
		}, function(error_code, error_info) {
			MessageBox.error(error_code, error_info);
			$.endPageLoading();
		});
}