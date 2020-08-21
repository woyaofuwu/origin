/*$Id:$*/
//面初始化

function myTabSwitchAction(ptitle, title) {
   
	return true;
}


function showDIV(op){
	if(op==31){
		$("#only").css('display','none');
		$("#many").css('display','block'); 
	}
	if(op==32){
		$("#only").css('display','block');
		$("#many").css('display','none'); 
	}
}

function batEditData()
{   
	$.beginPageLoading("数据正在导入...");
	$.ajax.submit("only", "batEditGardenInfo", null, "BatSubmitPart2", 
			function(data){
	          $.endPageLoading();
	          $.showSucMessage(data.get("result"), '', '');
	          $("#FILE_PATH").val("");
			}, 
			function(error_code, error_info){
				$.endPageLoading();
				alert(error_info);
	});	
}

function batAddData()
{   
	$.beginPageLoading("数据正在导入...");
	$.ajax.submit("many", "batGardenInfo", null, "BatSubmitPart", 
			function(data){
	          $.endPageLoading();
	          $.showSucMessage(data.get("result"), '', '');
	          $("#FILE_PATH").val("");
			}, 
			function(error_code, error_info){
				$.endPageLoading();
				alert(error_info);
	});	
}

function batDelData()
{   
	$.beginPageLoading("数据正在导入...");
	$.ajax.submit("many", "batGardenInfo", null, "BatSubmitPart", 
			function(data){
	          $.endPageLoading();
	          $.showSucMessage(data.get("result"), '', '');
	          $("#FILE_PATH").val("");
			}, 
			function(error_code, error_info){
				$.endPageLoading();
				alert(error_info);
	});	
}

function checkSelectMesInfo(obj){
	if(obj){
		var check = obj.checked;
		if(check == true){
			obj.value = "1";
		} else {
			obj.value = "0";
		}
	}
}

function qryGardenDeviceInfo(){
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit("QueryCondPart", "qryGardenDeviceInfo", null, "QueryCondPart,ratioPart,editForm", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function tableRatioClick()
{
	var rowData = $.table.get("ratioTable").getRowData();
	var userId = rowData.get("USER_ID");
	var groupId = rowData.get("GROUP_ID");
	var custName = rowData.get("CUST_NAME");
	var serialNumber = rowData.get("SERIAL_NUMBER");
	var startDate = rowData.get("START_DATE");
	var endDate = rowData.get("END_DATE");
	
	$("#E_USER_ID").val(userId);
	$("#E_GROUP_ID").val(groupId);
	$("#E_CUST_NAME").val(custName);
	$("#E_SERIAL_NUMBER").val(serialNumber);
	$("#E_START_DATE").val(startDate);
	$("#E_END_DATE").val(endDate);
}
 
