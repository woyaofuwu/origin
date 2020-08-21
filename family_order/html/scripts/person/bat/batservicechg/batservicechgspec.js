if(typeof(BatServiceChgSpec)=="undefined"){
	window["BatServiceChgSpec"]=function(){
		
	};
	var batservicechgspec = new BatServiceChgSpec();
}

(function(){
	$.extend(BatServiceChgSpec.prototype,{
	
	getElementAttr: function ()
	{
		var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
		var service_id =  $('#SERVICE_ID1').val();
		var param = "&SERVICE_ID="+service_id+"&BATCH_OPER_TYPE="+batch_oper_type;
		$.beginPageLoading("数据查询中...");
		$.ajax.submit(null,null,param,'QueryListPart',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	},
	
	diabledenddate: function ()
	{
		//'1'如果为删除屏蔽结束时间
		var s1=$("#MODIFY_TAG").val();
		if (s1=="1") {
		
		    $("#servicedate").css("display", "none");
		    $("#START_DATE").val("");
		    $("#END_DATE").val("");
		    $("#START_DATE").attr("nullable","yes");
		    $("#END_DATE").attr("nullable","yes");
		} else if (s1=="0") {
			$("#servicedate").css("display", "block");
			$("#END_DATE").attr("disabled", true);
			$("#START_DATE").attr("disabled", true);
			$("#START_DATE").val($("#START_DATE").attr("startdate"));
		    $("#END_DATE").val($("#END_DATE").attr("enddate"));
		    $("#START_DATE").attr("nullable","yes");
		    $("#END_DATE").attr("nullable","yes");
		}else{
			$("#servicedate").css("display", "block");
			$("#END_DATE").attr("disabled", true);
			$("#START_DATE").attr("disabled", true);
			$("#START_DATE").val("");
		    $("#END_DATE").val("");
		    $("#START_DATE").attr("nullable","yes");
		    $("#END_DATE").attr("nullable","yes");
		}
	}
	
	});
	}
)();
