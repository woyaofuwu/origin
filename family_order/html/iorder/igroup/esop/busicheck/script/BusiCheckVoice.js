function queryBusiInfos(){


	if(!$.validate.verifyAll('queryForm')) return false;

	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryBusiInfos", "", "refreshtable", function(data){
			
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function queryBusiDetail(obj){
	debugger;
	var SUB_IBSYSID = $(obj).attr("subIbsysId");	
	var IBSYSID = $(obj).attr("ibsysId");
	var BUSI_TYPE = $(obj).attr("busiType");
	var CHECK_TYPE = $("#CHECK_TYPE").val();
	$.beginPageLoading("数据查询中......");
	var params = '&IBSYSID='+IBSYSID+'&SUB_IBSYSID='+SUB_IBSYSID+'&BUSI_TYPE='+BUSI_TYPE+'&CHECK_TYPE='+CHECK_TYPE;
	$.ajax.submit("", "queryBusiDetailInfo", params, "showDetailPart,checkDetailList", function(data){
		
		$.endPageLoading();
	},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	$("#showDetailPart").css("display", "");
	$("#checkDetailList").css("display", "");
	//document.getElementById('showDetailPart').style.display='block';
	//document.getElementById('checkRecordPart').style.display='none';
}

function showAddCheckRecordArea(){
	var CHECK_ID = $("#CHECK_ID").val();	
	if(CHECK_ID != undefined){
		MessageBox.alert("错误", "该单已有业务监督检查记录，请点击修改检查记录！");
		return false;
	}
	$("#checkRecordPart").css("display", "");
}

function addOrUpdateCheckRecord(obj){
	debugger;
	var IBSYSID = $("#IBSYSID_Hidden").val();	
	var BUSI_TYPE = $("#BUSI_TYPE").val();	
	var CHECK_ID = $("#CHECK_ID").val();	
	if(CHECK_ID==undefined ||CHECK_ID==""){
		listenerMethod = 'addCheckRecord';
	}else{
		listenerMethod = 'updateCheckRecord';
	}
	var params = '&IBSYSID='+IBSYSID+'&BUSI_TYPE='+BUSI_TYPE+'&CHECK_ID='+CHECK_ID;
	$.beginPageLoading("数据提交中......");
	$.ajax.submit("checkRecordPart", listenerMethod, params, "checkDetailList,checkRecordPart", function(data){
		$.endPageLoading();
		MessageBox.success("成功提示","提交成功！");
	},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	
}
function showUpdateCheckRecordArea(obj){
	debugger;
	//$("#CHECK_TYPE").val($(obj).attr("CHECK_TYPE"));
	$("#CHECK_RESULT").val($(obj).attr("checkresult"));
	$("#REMARK").val($(obj).attr("remark"));
	$("#checkRecordPart").css("display", "");
	
}