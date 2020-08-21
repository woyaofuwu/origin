var CONFIRM_FILE;


$(function(){
	//过户依据
	$("#ATTR_CHG_FILE").afterAction(function(e, file){
		CONFIRM_FILE=file.name + "|" + file.fileId;
		
	});
});

function submitApply(){
	var confirmGrpAttrs = $("#CONFIRM_GRP_ATTRS").val();
	if(!$.validate.verifyAll("CustAuditPart")){
		return false;
	}
	
	var comData;
	if($("#EOS_COMMON_DATA").text()){
		comData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	}else{
		MessageBox.alert("错误", "未获取到流程公共信息！");
		return false;
	}
	
	 $.beginPageLoading();
	 ajaxSubmit("CustAuditPart","submitChange", '&CONFIRM_GRP_ATTRS=' + confirmGrpAttrs+'&CONFIRM_FILE=' + CONFIRM_FILE+'&COMMON_DATA=' + comData.toString() , null, function(data){
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
				MessageBox.success("审批成功", "", function(btn){
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