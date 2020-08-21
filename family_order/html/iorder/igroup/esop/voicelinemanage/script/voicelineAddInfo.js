$(function(){
	$("#upload").select(function(){
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
				fileList.add(data);
			}
		}
		$("#COMMON_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#COMMON_FILE").val(obj.NAME);
		$("#COMMON_FILE_ID").val(obj.ID);
		
		hidePopup("popup07");
	});
	
	$("#upload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#COMMON_FILE").val("");
		$("#COMMON_FILE_ID").val("");
		
		$("#COMMON_FILE_LIST").text("");
	});
});

function verifyUploadFile(obj){
	if(!$.validate.verifyAll("custInfoPart")){
		return false;
	}
	if(!$.validate.verifyAll("conditionInfoPart")){
		return false;
	}
	debugger;
	/*var uploadFile= new Wade.DatasetList();
	uploadFile=	$("#COMMON_FILE_LIST").text();*/
	var uploadFile=	 $("#COMMON_FILE_ID").val().split(","); 
	
	if(uploadFile.length<3){
		alert("请继续上传附件，《鉴权记录表》、《拨测报告》、《合同附件》必须上传！");
		return false;
	}
	$.ajax.submit("custInfoPart,conditionInfoPart,fileInfoPart", "submitInfos", null, "custInfoPart,conditionInfoPart,fileInfoPart", function(data){
		$.endPageLoading();
		MessageBox.success("成功提示","提交成功！");
	},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	
}