
function qryFeeRegCTT()
{	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	var start = startDate.substring(0,4)+startDate.substring(5,7)+startDate.substring(8,10);
	var end = endDate.substring(0,4)+endDate.substring(5,7)+endDate.substring(8,10);
	if(startDate == ""){
		alert("起始日期不能为空！");
		return false;
	}
	if(endDate == ""){
		alert("终止日期不能为空！");
		return false;
	}
	if( start > end){
		alert("起始日期不能大于终止日期！");
		return false;
	}
	
	$.ajax.submit('QueryPart', 'qryFeeRegCTT', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
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

function qryFeeRegMCtt()
{	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	var start = startDate.substring(0,4)+startDate.substring(5,7)+startDate.substring(8,10);
	var end = endDate.substring(0,4)+endDate.substring(5,7)+endDate.substring(8,10);
	if(startDate == ""){
		alert("起始日期不能为空！");
		return false;
	}
	if(endDate == ""){
		alert("终止日期不能为空！");
		return false;
	}
	if( start > end){
		alert("起始日期不能大于终止日期！");
		return false;
	}
	$.ajax.submit('QueryPart', 'qryFeeRegMCtt', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
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

function qryBankFeeRegCTT()
{	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	var start = startDate.substring(0,4)+startDate.substring(5,7)+startDate.substring(8,10);
	var end = endDate.substring(0,4)+endDate.substring(5,7)+endDate.substring(8,10);
	if(startDate == ""){
		alert("起始日期不能为空！");
		return false;
	}
	if(endDate == ""){
		alert("终止日期不能为空！");
		return false;
	}
	if( start > end){
		alert("起始日期不能大于终止日期！");
		return false;
	}
	$.ajax.submit('QueryPart', 'qryBankFeeRegCTT', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
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
function qryBankFeeRegMCtt()
{	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	var start = startDate.substring(0,4)+startDate.substring(5,7)+startDate.substring(8,10);
	var end = endDate.substring(0,4)+endDate.substring(5,7)+endDate.substring(8,10);
	if(startDate == ""){
		alert("起始日期不能为空！");
		return false;
	}
	if(endDate == ""){
		alert("终止日期不能为空！");
		return false;
	}
	if( start > end){
		alert("起始日期不能大于终止日期！");
		return false;
	}

	$.ajax.submit('QueryPart', 'qryBankFeeRegMCtt', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
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