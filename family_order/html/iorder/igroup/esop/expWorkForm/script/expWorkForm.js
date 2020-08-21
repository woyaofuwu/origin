$(function(){
	var flag = $("#FLAG").val();
	if("TRUE"==flag){
		$("#queryForm").css("display","none");
		$("#CheckPart").css("display","none");
		$("#OpenSubmit").css("display","none");
	}else{
		$("#queryForm").css("display","");
		$("#CheckPart").css("display","");
		$("#OpenSubmit").css("display","");
	}
	
	
});


function initPage(){
debugger;
	var busiFromId = "";
	var bisn = $('#cond_BI_SN').val();
	$("#BUSIFORM_ID").val("");
	$("#NODE_NAME").val("");
	$("#ACCEPT_DATE").val("");
	$("#BUSIFORM_NODE_ID").val("");
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("","initPage", "&BI_SN="+bisn+"&BUSIFORM_ID="+busiFromId,"QueryResultPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function expSubscribeInfo(obj){
	var busiFormId = $(obj).attr("BUSIFORM_ID");
	var state = $(obj).attr("STATE");
	var nodeName = $(obj).attr("NODE_NAME");
	var acceptDate = $(obj).attr("ACCEPT_DATE");
	var busiFormNodeId = $(obj).attr("BUSIFORM_NODE_ID");
	$('#cond_BUSIFORM_ID').val(busiFormId);
	$("#BUSIFORM_ID").val(busiFormId);
	$("#NODE_NAME").val(nodeName);
	$("#ACCEPT_DATE").val(acceptDate);
	$("#BUSIFORM_NODE_ID").val(busiFormNodeId);
	if("0"==state){
		$("#STATE").val("未完成");
	}else if("9"==state){
		$("#STATE").val("已完成");
	}else if("4"==state){
		$("#STATE").val("已撤单");
	}
}

function submitState(){
	var busiFormNodeId = $("#BUSIFORM_NODE_ID").val();
	var busiFormId = $("#BUSIFORM_ID").val();
	var state = "M";
	  if(""==busiFormId||undefined==busiFormId||null==busiFormId){
		  $.validate.alerter.one($("#BUSIFORM_ID")[0], "未点击表格的业务单号获取信息，请获取业务单号信息！");
	        return false;
	  }
	$.ajax.submit("","submit", "&STATE="+state+"&BUSIFORM_ID="+busiFormId+"&BUSIFORM_NODE_ID="+busiFormNodeId,null, function(data){
		$.endPageLoading();
		MessageBox.success("提交", "操作成功！",function(){
			initPage();
   		});
	},
	function(error_code,error_info, derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	}
);
}