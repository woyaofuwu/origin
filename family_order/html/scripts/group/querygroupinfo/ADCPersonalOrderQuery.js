function changeQueryType(){
	var conQueryType = $("#cond_QueryType").val();
	
	if(conQueryType == "0")
	{
		$("#usersn").css("display", "");
		$("#ecSn").css("display", "none");
		$("#bizCode").css("display", "none");
		
		$('#cond_BIZ_CODE').val("");
		$('#cond_EC_SERIAL_NUMBER').val("");

	}
	if(conQueryType == "1")
	{
		$("#bizCode").css("display", "");
		$("#usersn").css("display", "none");
		$("#ecSn").css("display", "none");
		
		$('#cond_SERIAL_NUMBER').val("");
		$('#cond_EC_SERIAL_NUMBER').val("");
	}
	if(conQueryType == "2")
	{
		$("#ecSn").css("display", "");
		$("#bizCode").css("display", "none");
		$("#usersn").css("display", "none");
		
		$('#cond_SERIAL_NUMBER').val("");
		$('#cond_BIZ_CODE').val("");
	}
		
}

function queryInfos(){
	
	var sn = $('#cond_SERIAL_NUMBER').val();
	var bizCode = $('#cond_BIZ_CODE').val();
	var ecSn = $('#cond_EC_SERIAL_NUMBER').val();
	
	
	var queryway = $('#cond_QueryType').val();
	if(queryway == 0 && sn == ''){
		alert('个人手机号码不能为空!');
		return false;
	}
	if(queryway == 1 && bizCode == ''){
		alert('集团端口（服务代码）不能为空!');
		return false;
	}
	if(queryway == 2 && ecSn == ''){
		alert('集团用户手机号码不能为空!');
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

