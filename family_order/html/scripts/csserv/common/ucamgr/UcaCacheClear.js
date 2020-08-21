function clearUcaInfo()
{
	if(!$.validate.verifyField($("#SERIAL_NUMBER")))
	{
		alert('存在必填信息为空的情况！');
		return false;
	}	 
	
	$.beginPageLoading();
	$.ajax.submit("queryForm","clear", "", "refreshtable", function(data){
		
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

function changeClearType() 
{
	var choose=$('#CLEAR_TYPE').val();
   	if (choose=="0") //按服务号码
   	{
   		$("#ConditionLabel").html("服务号码：");
   		$("#ConditionLabel").attr("class","e_required");
   	}
   	else if (choose=="1") //按账户编码
   	{
   		$("#ConditionLabel").html("账户编码：");
   		$("#ConditionLabel").attr("class","e_required");
   	}
   	else if (choose=="2") //按用户编码
   	{
      	$("#ConditionLabel").html("用户编码：");
      	$("#ConditionLabel").attr("class","e_required");
   	}
   	else if (choose=="3") //按客户标识
   	{
      	$("#ConditionLabel").html("客户标识：");
      	$("#ConditionLabel").attr("class","e_required");
   	}
   	else if (choose=="4") //按客户编码
   	{
      	$("#ConditionLabel").html("客户编码：");
      	$("#ConditionLabel").attr("class","e_required");
   	}
}
			
		