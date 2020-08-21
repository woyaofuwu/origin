function changeQueryType(){
	var conQueryType = $("#cond_QueryType").val();
	
	if(conQueryType == "0")
	{
		$("#snA").css("display", "");
		$("#snB").css("display", "none");

		$('#cond_SERIAL_NUMBER').val('');
	}
	if(conQueryType == "1")
	{
		$("#snB").css("display", "");
		$("#snA").css("display", "none");
		
		$('#cond_SERIAL_NUMBER_A').val('');
	}
		
}

function queryInfos(){
	
	var snA = $('#cond_SERIAL_NUMBER_A').val();
	var snB = $('#cond_SERIAL_NUMBER').val();
	
	var queryway = $('#cond_QueryType').val();
	if(queryway == 0 && snA == ''){
		alert('集团编码不能为空!');
		return false;
	}
	if(queryway == 1 && snB == ''){
		alert('固定号码不能为空!');
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

