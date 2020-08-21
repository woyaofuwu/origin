function changequerytype(querytype){
	if (querytype=="1"){
	    $("#cond_UPT_START").val('');
	    $("#cond_UPT_END").val('');
	    $("#cond_IS_LIMIT").val('');
		$("#cond_UPT_START").attr('disabled',true);
		$("#cond_UPT_END").attr('disabled',true);
		$("#cond_IS_LIMIT").attr('disabled',true);
		//$("#cond_LIMIT_QUERY_TYPE").attr('disabled',true);
		
	}else {
		$("#cond_UPT_START").val('');
	    $("#cond_UPT_END").val('');
	    $("#cond_IS_LIMIT").val('');
		$("#cond_UPT_START").attr('disabled',false);
		$("#cond_UPT_END").attr('disabled',false);
		$("#cond_IS_LIMIT").attr('disabled',false);
		
	}
	
}

function queryDayData()
{
	 if(!$.validate.verifyAll("DayQueryPart")){
		 return false;
	 }
	 
	 $.beginPageLoading("正在查询...");
	 $.ajax.submit('DayQueryPart', 'queryDayData', null, 'DayDataPart',function(data){
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}

function queryImportData()
{
	 if(!$.validate.verifyAll("QueryPart")){
		 return false;
	 }
	 if($('#cond_SERIAL_NUMBER').val()==""&&$('#cond_UPT_START').val()==""&&$('#cond_UPT_END').val()==""&&$('#cond_IS_LIMIT').val()==""){
		 alert('请填写至少一个查询条件！');
		 return;
	 }
	$.beginPageLoading("正在查询...");
    $.ajax.submit('QueryPart', 'queryImportData', null, 'ImportDataPart',function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function  modifyData(){
	var table = $.table.get("DeptTable");
	if(table.getSelected('serialNumber')==null){
		MessageBox.alert('请选择数据','请选择数据后再做操作');
		return;
	}
	var serialNumber = table.getSelected('serialNumber').html();
	var isLimit = table.getSelected('rsrvValue').html();
	var param = '&SERIAL_NUMBER='+encodeURI(encodeURI(serialNumber))+'&IS_LIMIT='+encodeURI(encodeURI(isLimit));
	$.popupPage('gprslimit.queryFreeGprs', 'initEditPage', '&refresh=true'+param, '日免费流量查询', '800', '600');
	
}


function gprsLimit()
{	
	$.beginPageLoading("处理中...");
	
	$.ajax.submit('DayQueryPart', 'submitGprsLimit', null, 'DayQueryPart', function(data)
    {
		alert("该用户流量限速成功，如需查看请重新查询！");
		setPopupReturnValue('','',true);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function gprsNotLimit()
{	
	$.beginPageLoading("处理中...");
	
	$.ajax.submit('DayQueryPart', 'submitGprsNotLimit', null, 'DayQueryPart', function(data)
    {
		alert("该用户解除限速成功，如需查看请重新查询！");
		setPopupReturnValue('','',true);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
 