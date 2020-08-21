function queryBusiInfos(){
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryBusiWorkformInfos", "", "refreshtable", function(data){
			
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function queryBusiWorkformReview(obj){
	var IBSYSID =$(obj).attr("ibsysid");
	var SUB_IBSYSID = $(obj).attr("subibsyid");
	var params = '&IBSYSID='+IBSYSID+'&SUB_IBSYSID='+SUB_IBSYSID;
	$("#cond_IBSYSID").val(IBSYSID);
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryBusiWorkformReviewDetailInfos", params, "QryResultPart", function(data){
			$.endPageLoading();
			showPopup('voicePopup','voicePopupItem',true);
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function queryBusiWorkformReviewIbsysId(){
	debugger;
	var IBSYSID =$("#cond_IBSYSID").val();
	var params = '&IBSYSID='+IBSYSID;
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "queryBusiWorkformReviewDetailInfos", params, "QryResultPart", function(data){
			$.endPageLoading();
			showPopup('voicePopup','voicePopupItem',true);
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}