function getGroupInfo()
{
    var group = $("#cond_GROUP_ID").val;
    
    if(!$.validate.verifyField($("#cond_GROUP_ID"))) 
    {
       return false;
    }  
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit('queryFrom','getGroupBaseInfo','&cond_GROUP_ID='+group,'GroupInfoPart,TradeListPart',
	function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
	);
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	var userId = $("#USER_ID").val();
	if(userId == null || userId == ""){
		//请选择集团产品!
		alert("\u8bf7\u9009\u62e9\u96c6\u56e2\u4ea7\u54c1!");
		return false;
	}
	
	var wordsDes = $("#WORDS_DES").val();
	if(wordsDes == null || wordsDes == ""){
		//欢迎词描述不能为空!
		alert("\u6b22\u8fce\u8bcd\u63cf\u8ff0\u4e0d\u80fd\u4e3a\u7a7a!");
		return false;
	}
	
	if(wordsDes.length > 10){
		//欢迎词描述长度不能超过10!
		alert("\u6b22\u8fce\u8bcd\u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc710!");
		return false;
	}
	
	var grpFileList = $("#GRP_FILE_LIST").val();
	if(grpFileList == null || grpFileList == ""){
		//请上传集团铃音文件
		alert("\u8bf7\u4e0a\u4f20\u96c6\u56e2\u94c3\u97f3\u6587\u4ef6!");
		return false;
	}
	
	if(!$.validate.verifyAll("TradeListPart")) return false;
	
	if(!$.validate.verifyAll("GrpFilePart")) return false;
		
	return true;
}