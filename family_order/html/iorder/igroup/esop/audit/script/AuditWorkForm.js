function queryBusiInfos(){
	if(!$.validate.verifyAll('queryForm')) return false;
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryWorkForm", "", "refreshtable,attachInfoPart", function(data){
			
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function submitInfos(obj){
	var table = getTables();
	$.beginPageLoading("数据提交中......"); 
	$.ajax.submit("checkRecordPart", "submits", "&ROWDATAS="+table, "refreshtable,checkRecordPart", function(data){
			$.endPageLoading();
			MessageBox.success("成功提示","提交成功！");
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function submitAuditInfo(obj){
	if(!$.validate.verifyAll("checkRecordPart")){
		return false;
	}
	
	$.beginPageLoading("数据提交中......");
	$.ajax.submit("checkRecordPart", "submits", null, "checkRecordPart", function(data){
			$.endPageLoading();
			MessageBox.confirm("提示", "提交成功！",function(){
				closeNav();
			});
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function getTables(){
	var chk = document.getElementsByName("TRADES");
	var tables = new Wade.DatasetList();
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				var table = new Wade.DataMap();
				table.put("USER_ID",chk[i].getAttribute("userid"));
				table.put("IBSYSID",chk[i].getAttribute("ibsysid"));
				table.put("AUDIT_TYPE",chk[i].getAttribute("audittype"));
				table.put("TRADE_ID",chk[i].getAttribute("tradeid"));
				tables.add(table);
            }
       	}
		
	}
	return tables;
}


function showAuditStaff(){
	ajaxSubmit("",'qryStaffinfo',null,'auditParts',function(data){
		showPopup("popup08", "auditPopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function auditFormQuery(){
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("auditForm",'qryStaffinfo',null,'auditParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function setReturnValue(el){
	var staffId = $(el).attr("staff_id");
    var staffPhone =  $(el).attr("staff_phone");
	$("#pattr_AUDITSTAFF").val(staffId);
	$("#pattr_AUDITPHONE").val(staffPhone);
	backPopup("popup08", "auditPopupItem", true);
}


function queryAllDataline(el){
	var ibsysId = $(el).attr("IBSYSID");
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("",'queryAllDataline',"&IBSYSID="+ibsysId,'dataLineInfoPart',function(data){
		showPopup("popup09", "datalinePopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}
function queryAllDatalineInfo(el){
	debugger;
	var userId = $(el).attr("USER_ID");
	var productId =  $("#PRODUCT_ID").val();
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("",'queryAllDataline',"&USER_ID="+userId+"&PRODUCT_ID="+productId,'dataLineInfoPart',function(data){
		showPopup("popup09", "datalinePopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function submitHAuditInfo(obj){
	if(!$.validate.verifyAll("checkRecordPart")){
		return false;
	}
	var groupName=$("#GROUPNME").val();
	var lineType=$("#LINE_TYPE").val();
	var discnt=$("#DISCNT").val();
	var width=$("#WIDTH").val();
	if(groupName == "" || groupName == null) 
	{
	  $.validate.alerter.one($("#GROUPNME")[0], "稽核信息必选!\n");
	  return false;
	}
	if(lineType == "" || lineType == null) 
	{
	  $.validate.alerter.one($("#LINE_TYPE")[0], "稽核信息必选!\n");
	  return false;
	}
	if(discnt == "" || discnt == null) 
	{
	  $.validate.alerter.one($("#DISCNT")[0], "稽核信息必选!\n");
	  return false;
	}
	if(width == "" || width == null) 
	{
		$.validate.alerter.one($("#WIDTH")[0], "稽核信息必选!\n");
		return false;
	}
	var IBSYSID  = $("#IBSYSID").val();
	var NODE_ID  = $("#NODE_ID").val();
	var BUSIFORM_NODE_ID  = $("#BUSIFORM_NODE_ID").val();
	var BPM_TEMPLET_ID  = $("#BPM_TEMPLET_ID").val();
	var BUSI_CODE  = $("#BUSI_CODE").val();
	var BUSI_TYPE  = $("#BUSI_TYPE").val();
	var MAIN_IBSYSID  = $("#MAIN_IBSYSID").val();
	var RECORD_NUM  = $("#RECORD_NUM").val();
	
	$.beginPageLoading("数据提交中......");
	var param = '&IBSYSID=' + IBSYSID+'&NODE_ID=' + NODE_ID+'&BUSIFORM_NODE_ID=' + BUSIFORM_NODE_ID+'&BPM_TEMPLET_ID=' + BPM_TEMPLET_ID
	+'&BUSI_CODE=' + BUSI_CODE+'&BUSI_TYPE=' + BUSI_TYPE+'&MAIN_IBSYSID=' + MAIN_IBSYSID + '&RECORD_NUM' + RECORD_NUM;
	$.ajax.submit("checkRecordPart", "submits", param,"checkRecordPart", function(data){
			$.endPageLoading();
			MessageBox.success("操作成功！", "稽核工单号："+data.get("IBSYSID"),function(){
				closeNav();
			});
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


$(function(){
	  
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
				if(containSpecial(fileNameArr[i])){
					MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
					return false; 
				}
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "P");
				fileList.add(data);
			}
		}
		$("#AUDIT_FILE_LIST").val(fileList.toString());
/*		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);*/
		$("#AUDIT_FILE_NAME").val(obj.NAME);
		$("#AUDIT_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
});

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

