// 设置返回值
function setReturnData(){

	var svcOper = $("#SVC_RADIO").attr('checked');
	var discntOper = $("#DISCNT_RADIO").attr('checked');
	
	if(svcOper == true)
	{
		var svcCode = $("#SVC_CODE").val();
		if(svcCode == null || svcCode == "")
		{
			alert("请选择要操作的服务编码!");
			return false;
		}
	}
	
	if(discntOper == true)
	{
		var discntCode = $("#DISCNT_CODE").val();
		if(discntCode == null || discntCode == "")
		{
			alert("请选择要操作的优惠编码!");
			return false;
		}
	}

	// 设置返回值
	var valueData = $.DataMap();
	if(svcOper == true)//操作服务
	{
		var svcCode = $("#SVC_CODE").val();
		valueData.put("SVC_CODE", svcCode);
		valueData.put("OPER_TYPE", "0");
		parent.$('#POP_CODING_STR').val("服务编码：" + svcCode);
		
		var svcAddCheck = $("#SVCADD_RADIO").attr('checked');
		var svcDelCheck = $("#SVCDEL_RADIO").attr('checked');
		if(svcAddCheck == true)
		{
			var svcAddVal =  $("#SVCADD_RADIO").val();
			valueData.put("SVC_OPER",  svcAddVal);
		}
		if(svcDelCheck == true)
		{
			var svcDelVal =  $("#SVCDEL_RADIO").val();
			valueData.put("SVC_OPER",  svcDelVal);
		}
	}
	
	if(discntOper == true) //操作优惠
	{
		var discntCode = $("#DISCNT_CODE").val();
		valueData.put("DISCNT_CODE", discntCode);
		valueData.put("OPER_TYPE", "1");
		parent.$('#POP_CODING_STR').val("优惠编码：" + discntCode);
		
		var discntAddCheck = $("#DISCNTADD_RADIO").attr('checked');
		var discntDelCheck = $("#DISCNTDEL_RADIO").attr('checked');
		if(discntAddCheck == true)
		{
			var discntAddVal =  $("#DISCNTADD_RADIO").val();
			valueData.put("DISCNT_OPER",  discntAddVal);
		}
		if(discntDelCheck == true)
		{
			var discntDelVal =  $("#DISCNTDEL_RADIO").val();
			valueData.put("DISCNT_OPER",  discntDelVal);
		}
		
	}
	
	parent.$('#CODING_STR').val(valueData);
	
	parent.hiddenPopupPageGrp();
}


function checkRadio(radioId){
	var operValue =$('#'+radioId).val();
	if(operValue == "1")
	{
		$("#SvcPart").css('display','block');
		$("#DiscntPart").css('display','none');
	}
	else if(operValue == "0")
	{
		$("#SvcPart").css('display','none');
		$("#DiscntPart").css('display','block');
	}
}


