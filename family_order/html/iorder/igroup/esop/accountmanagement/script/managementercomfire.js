//绑定上传完成后的回调事件
$(function(){
	$("#simpleupload").afterAction(function(e, file){
		if(containSpecial(file.name)){
			MessageBox.alert("错误", "【"+file.name+"】文件名称包含特殊字符，请修改后再上传！");
			$("#simpleupload").val();
			return false; 
		}
		
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#uploadDate").text(data.toString());
		//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
	});
});

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

//提交方法
function submit(){
	
	var nodeId = $("#NODE_ID").val();
	
	var otherList = new Wade.DatasetList();
	
	var attachList = new Wade.DatasetList();
	
	if(nodeId=='mangerConfirm'){
		if(!$("#CHANGE_RESULT").val()){
			alert("请选择调整结果！");
			return false;
		}else{
			otherList = saveOtherList();
		}
	}else if(nodeId=='mangerModify'){
		if(!$("#uploadDate").text()){
			$.validate.alerter.one($("#simpleupload")[0], "请上传审批附件！");
			//alert("请先上传附件！");
			return false;
		}else{
			var attachList = savaAttachList();
		}
	}
	
	var submitParam = new Wade.DataMap();
	submitParam.put("OTHER_LIST",otherList);
	submitParam.put("COMMON_DATA",saveCommonDate());
	submitParam.put("ATTACH_LIST",attachList);
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?"); 
					var pageName = getNavTitle(); 
					openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}else if(data.get("ALERT_FLAG")== "true"){
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ALERT_URL").split("?");
					var ALERT_NAME = data.get("ALERT_NAME");
					var pageName = getNavTitle();
					openNav(ALERT_NAME, urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "下一步"});
		}
		else
		{
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
	
}

function saveOtherList(){
	var otherList = new Wade.DatasetList();
	var otherData = new Wade.DataMap();
	otherData.put("ATTR_CODE","CHANGE_RESULT");
	otherData.put("ATTR_NAME","是否调整完毕");
	otherData.put("ATTR_VALUE",$("#CHANGE_RESULT").val());
	otherData.put("RECORD_NUM","0");
	otherList.add(otherData);
	return otherList;
}

function saveCommonDate(){
	var commonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!commonData.get("CUST_NAME"))
	{
		commonData.put("CUST_NAME", $("#CUST_NAME").text());
	}
	return commonData;
}

function savaAttachList(){
	var attachList = new Wade.DatasetList();
	var attachData = new Wade.DataMap($("#uploadDate").text());
	attachList.add(attachData);
	return attachList;
}