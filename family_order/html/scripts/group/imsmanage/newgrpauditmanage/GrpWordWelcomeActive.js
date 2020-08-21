//查询集团客户信息
function getGroupInfo(){	
	//集团客户编码校验
	if(!$.validate.verifyAll("GroupCondPart")) {
		return false;
	}
	
	//集团客户信息查询
	$.ajax.submit('GroupCondPart', 'queryVocieInfo', null, 'GantPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function updateGrpWord(){
	
	/**
	var rowData = $.table.get("FeeTable").getRowData();
	var groupId = rowData.get("GROUP_ID");
	var userId = rowData.get("USER_ID");
	var fileId = rowData.get("FILE_ID");
	var fileName = rowData.get("FILE_NAME");
	var vpnNo = rowData.get("VPN_NO");
	*/
	
	var groupId = $("#GROUP_ID").val();
	var userId = $("#USER_ID").val();
	var fileId = $("#FILE_ID").val();
	var fileName =$("#FILE_NAME").val();
	var vpnNo = $("#VPN_NO").val();
	var wordsDes = $("#WORDS_DES").val();
	
	if(fileName == null || fileName == ""){
		alert("文件名为空!请选择您要操作的数据!");
		return false;
	}
	
	if(fileId == null || fileId == ""){
		alert("文件号为空!请选择您要操作的数据!");
		return false;
	}
	
	if(vpnNo == null || vpnNo == ""){
		alert("获取BGname集团编号为空!请选择您要操作的数据!");
		return false;
	}
	
	var params = "&GROUP_ID=" + groupId + "&USER_ID=" + userId + "&FILE_ID=" + fileId +	"&FILE_NAME=" 
		+ fileName + "&VPN_NO=" + vpnNo + "&WORDS_DES=" + wordsDes;
	
	$.beginPageLoading("正在提交中......");
	$.ajax.submit(null, "sendGrpWordMessage", params, "GantPart,GrpConditionInfoPart,CondGroupPart,GroupInfoPart,feePart", 
		function(data){
			$.endPageLoading();
			$.showSucMessage("审核欢迎词业务成功!");
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function activeGrpWord(){
	
	/*
	var rowData = $.table.get("FeeTable").getRowData();
	
	var groupId = rowData.get("GROUP_ID");
	var userId = rowData.get("USER_ID");
	var fileId = rowData.get("FILE_ID");
	var fileName = rowData.get("FILE_NAME");
	var vpnNo = rowData.get("VPN_NO");
	*/
	
	var groupId = $("#GROUP_ID").val();
	var userId = $("#USER_ID").val();
	var fileId = $("#FILE_ID").val();
	var fileName =$("#FILE_NAME").val();
	var vpnNo = $("#VPN_NO").val();
	var wordsDes = $("#WORDS_DES").val();
	
	if(fileName == null || fileName == ""){
		alert("文件名为空!请选择您要操作的数据!");
		return false;
	}
	
	if(fileId == null || fileId == ""){
		alert("文件号为空!请选择您要操作的数据!");
		return false;
	}
	
	if(vpnNo == null || vpnNo == ""){
		alert("获取BGname集团编号为空!请选择您要操作的数据!");
		return false;
	}
	
	var params = "&GROUP_ID=" + groupId + "&USER_ID=" + userId + "&FILE_ID=" + fileId +	"&FILE_NAME="
		 + fileName + "&VPN_NO=" + vpnNo + "&WORDS_DES=" + wordsDes;
	
	$.beginPageLoading("正在提交中......");
	$.ajax.submit(null, "activeGrpWordMessage", params, "GantPart,GrpConditionInfoPart,CondGroupPart,GroupInfoPart,feePart", 
		function(data){
			$.endPageLoading();
			$.showSucMessage("激活欢迎词业务成功!");
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function tableRowClick(){
	var rowData = $.table.get("FeeTable").getRowData();
	if(rowData != null && rowData.length > 0){
		$("#GROUP_ID").val(rowData.get("GROUP_ID"));
		$("#USER_ID").val(rowData.get("USER_ID"));
		$("#FILE_ID").val(rowData.get("FILE_ID"));
		$("#FILE_NAME").val(rowData.get("FILE_NAME"));
		$("#VPN_NO").val(rowData.get("VPN_NO"));
		$("#WORDS_DES").val(rowData.get("WORDS_DES"));
	}
}

function deleteGrpWord(){
	$("#GROUP_ID").val("");
	$("#USER_ID").val("");
	$("#FILE_ID").val("");
	$("#FILE_NAME").val("");
	$("#VPN_NO").val("");
	$("#WORDS_DES").val("");
}

