if(typeof(BatDiscntChg)=="undefined"){
	window["BatDiscntChg"]=function(){
		
	};
	var batdiscntchg = new BatDiscntChg();
}

(function(){
	$.extend(BatDiscntChg.prototype,{

	getElementAttr: function ()
	{
		var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
		var discnt_code =  $('#DISCNT_CODE').val();
		var param = "&DISCNT_CODE="+discnt_code+"&BATCH_OPER_TYPE="+batch_oper_type;
		$.beginPageLoading("数据查询中...");
		$.ajax.submit(null,'initBatPopuPages',param,'QueryListPart',function(data){
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
		    $("#END_DATE").attr("disabled", true);
		    $("#START_DATE").attr("disabled", true);
		    $("#START_DATE").val("");
		    $("#END_DATE").val($("#END_DATE").attr("enddate"));
		} else if (s1=="0") {
			$("#END_DATE").attr("disabled", true);
			$("#START_DATE").attr("disabled", false);
			$("#END_DATE").val("");
			$("#START_DATE").val($("#START_DATE").attr("startdate"));
		}
	}

	});
	}
)();