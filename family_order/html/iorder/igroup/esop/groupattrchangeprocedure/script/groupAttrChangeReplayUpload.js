$(function(){
	//过户依据
	$("#ATTR_CHG_FILE").afterAction(function(e, file){
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#C_FILE_LIST_ATTACH").text(data.toString());
		var ATTR_FILE=file.name + "|" + file.fileId;
		$("#C_FILE_LIST").text(ATTR_FILE);
	});

//业务协议
$("#fileupload").select(function(){
	var fileList = new Wade.DatasetList();

	var obj = this.val();
	var fileIdArr = obj.ID.split(",");
	var fileNameArr = obj.NAME.split(",");
	
	for(var i = 0, size = fileIdArr.length; i < size; i++)
	{
		if(fileIdArr[i] != "")
		{
			var data = new Wade.DataMap();
			data.put("FILE_ID", fileIdArr[i]);
			data.put("FILE_NAME", fileNameArr[i]);
			data.put("ATTACH_TYPE", "P");
			fileList.add(data);
		}
	}
	$("#ATTACH_FILE_LIST").text(fileList.toString());
	
	$("#upfileText").text(obj.NAME);
	$("#upfileValue").val(obj.ID);
	
	$("#ATTACH_FILE_NAME").val(obj.NAME);
	$("#ATTACH_FILE_ID").val(obj.ID);
	BUSI_FILE=fileList.toString();
	if(null==BUSI_FILE||0<=BUSI_FILE.size)
	{
		MessageBox.alert("提示信息","请上传业务协议！");
		return;
	}
	hidePopup("upfilegroup");
});

//绑定上传组件清除按钮事件
$("#fileupload").clear(function(){
	$("#upfileText").text("");
	$("#upfileValue").val("");
});
});

function saveAttachInfo(attach){
	//其他附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST_ATTACH").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	return attachList;	
}


function submitApply(){
	var confirmGrpAttrs = $("#CONFIRM_GRP_ATTRS").val();
	var commData = $("#Comm_Data").text();
	var attach = new Wade.DatasetList();
//	var commDataMap = new Wade.DataMap(commData);
	attach = saveAttachInfo(attach); //获取附件信息
	 $.beginPageLoading();
	 ajaxSubmit("","submitChange", '&CONFIRM_GRP_ATTRS=' + confirmGrpAttrs+"&ATTACH_LIST="+attach+"&Comm_Data="+commData, null, function(data){
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