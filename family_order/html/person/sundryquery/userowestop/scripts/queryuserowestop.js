/***
 * 提交欠费停拆机用户查询前做校验
 */
function checkUserOweStop(obj){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	var psptKind = $("#cond_PSPT_KIND").val();
	var psptId = $("#cond_PSPT_ID").val();
	var custName =$("#cond_CUST_NAME").val();
	//如果只填写证件号码，但没选择证件类型，则提示不通过
	if(psptId.length > 0&& psptKind.length == 0){
		alert( "请选择【证件类型】~");
		return false;
	}
	//如果证件号不为空，且证件类型也不为空则通过
	if(psptId.length > 0&& psptKind.length > 0){
        $.ajax.submit('QueryCondPart', 'queryUserOweStop', null, 'QueryListPart', function(data){
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
		return true;
	}
	//如果客户名称不为空，则通过
	if(custName.length > 0){
        $.ajax.submit('QueryCondPart', 'queryUserOweStop', null, 'QueryListPart', function(data){
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
		return true;
	}
	//证件号码和客户名称不能同时为空
	if(psptKind.length>0&&(psptId.length==0&&custName.length==0)){
		alert( "【证件号码】和【客户名称】不能同时为空~");
		return false;
	}
	
	alert( "请输入查询条件~");
	return false;
}

//查询
function queryUserOweStop()
{
		
		
		
}

function resetCondition(){
	document.getElementById("cond_CUST_NAME").value="";
	document.getElementById("cond_PSPT_ID").value="";
	var psptKind = getElement("cond_PSPT_KIND");
    psptKind.selectedIndex = 0;
}