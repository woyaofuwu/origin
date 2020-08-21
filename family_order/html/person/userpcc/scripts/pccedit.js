
function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit(null, 'loadChildInfo',  "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(),
	'EditListPart,hiddenPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
} 

function tablePccEditClick()
{
	var rowData = $.table.get("PccEditTable").getRowData();
	var serialNumber = rowData.get("SERIAL_NUMBER");
	var serialNumberA = rowData.get("SERIAL_NUMBER_A");
	var IMSI = rowData.get("IMSI"); 
	var billType = rowData.get("BILL_TYPE");
	var userGrade = rowData.get("USER_GRADE");
	var userStatus = rowData.get("USER_STATUS"); 
	var userBillcycledate = rowData.get("USER_BILLCYCLEDATE");
	var operCode = rowData.get("OPER_CODE");
	
	$("#SERIAL_NUMBER").val(serialNumber);
	$("#SERIAL_NUMBER_A").val(serialNumberA);
	$("#USER_GRADE").val(userGrade); 
	$("#USER_STATUS").val(userStatus);
	$("#BILL_TYPE").val(billType);
	$("#USER_BILLCYCLEDATE").val(userBillcycledate); 
	$("#OPER_CODE").val(operCode);
}


function checkFinal()
{
	var serialNumber = $("#SERIAL_NUMBER").val();
	var serialNumberA = $("#SERIAL_NUMBER_A").val();
	var billType = $("#BILL_TYPE").val();
	var userGrade = $("#USER_GRADE").val(); 
	var userStatus = $("#USER_STATUS").val();
	var userBillcycledate = $("#USER_BILLCYCLEDATE").val(); 
	var operCode = $("#OPER_CODE").val();
	
	if(null == serialNumber || serialNumber.length == 0 ){
		alert("【用户号码】不能为空");
		return false;
	}
	
	if(null == serialNumberA || serialNumberA.length == 0 ){
		alert("【短信接收号码】不能为空");
		return false;
	}
	
	if(null == billType || billType.length == 0 ){
		alert("【计费方式】不能为空");
		return false;
	}
	
	if(null == userGrade || userGrade.length == 0 ){
		alert("【用户等级】不能为空");
		return false;
	}
	
	if(null == userStatus || userStatus.length == 0 ){
		alert("【用户配额状态】不能为空");
		return false;
	}
	
	if(null == userBillcycledate || userBillcycledate.length == 0 ){
		alert("【用户起帐日期】不能为空");
		return false;
	}

	if(null == operCode || operCode.length == 0 ){
		alert("【操作】不能为空");
		return false;
	}
	
	return true;
}
    
   














