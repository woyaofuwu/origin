function changeQueryType(){
	var conQueryType = $("#cond_QueryType").val();
	
	if(conQueryType == "0")
	{
		$("#usersn").css("display", "");
		$("#ecSn").css("display", "none");
		
		$('#cond_EC_SERIAL_NUMBER').val("");

	}
	if(conQueryType == "1")
	{
		$("#usersn").css("display", "none");
		$("#ecSn").css("display", "");
		
		$('#cond_SERIAL_NUMBER').val("");
	}
		
}

function queryInfos(){
	
	var sn = $('#cond_SERIAL_NUMBER').val();
	var ecSn = $('#cond_EC_SERIAL_NUMBER').val();
	var start_time = $('#cond_START_TIME').val();
	var end_time = $('#cond_END_TIME').val();
	
	
	var queryway = $('#cond_QueryType').val();
	if(queryway == 0 && sn == ''){
		alert('个人手机号码不能为空!');
		return false;
	}
	if(queryway == 1 && ecSn == ''){
		alert('集团用户手机号码不能为空!');
		return false;
	}
	if(start_time == ''){
		alert('开始时间不能为空!');
		return false;
	}
	if(end_time == ''){
		alert('结束时间不能为空!');
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryInfos", "", "refreshtable,hintPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

