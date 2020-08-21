
function addInfo()
{  
	var noticeContent = $("#NOTICE_CONTENT").val();
	if(noticeContent.length == 0 ){
		alert("【提示内容】不能为空~");
		return false;
	}else if(noticeContent.length > 500){
		alert("【提示内容】不能超过500个字符~");
		return false;
	}	
	
	var remark = $("#REMARK").val();
	if(remark.length > 100){
		alert("【备注】不能超过100个字符~");
		return false;
	}
	
	var editTable = new Array();
	editTable["ENABLE_TAG_BOX_TEXT"] =$("#ENABLE_TAG").find("option:selected").text();
	
	var noticeContent = $("#NOTICE_CONTENT").val();
	if(noticeContent.length > 20){
		noticeContent = noticeContent.substring(0,19) + "...";
	}
	
	if(remark.length > 20){
		remark = remark.substring(0,19) + "...";
	}
	noticeContent = $.xss(noticeContent);
	remark = $.xss(remark);
	
	editTable["OTICE_CONTENT1"] =noticeContent;
	editTable["REMARK1"] =remark;
	editTable["NOTICE_CONTENT"] =$.xss($("#NOTICE_CONTENT").val());
	editTable["REMARK"] =$.xss($("#REMARK").val()); 
	$.table.get("InfomationTable").addRow(editTable);	
}

function delInfo()
{
	var tab = $.table.get("InfomationTable");
	tab.deleteRow();
}

function onTradeSubmit()
{
	var editTable = $.table.get("InfomationTable").getTableData();
	
	if(editTable.length==0){
		alert("没有数据可以提交");
		return false;
	}
	
	var param = "&editTable="+editTable;
	$.cssubmit.addParam(param);
	
	return true;
}

function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'QueryInfoPart', function(data){
	
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}








 