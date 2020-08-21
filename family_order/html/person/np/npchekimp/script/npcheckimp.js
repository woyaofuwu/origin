
function queryImportData()
{
	 if(!$.validate.verifyAll("QueryPart")){
		 return false;
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
	$.popupPage('npcheckimp.EditData', '', '&refresh=true', '新增', '800', '600');
}


function editTask()
{	
	//editPart
	 if(!$.validate.verifyAll("editPart")){
		 return false;
	 }
	 
	 if($('#cond_SERIAL_NUMBER').val()==""){
		 alert('手机号码不能为空');
		 return;
	 }
	 if($('#cond_CUST_NAME').val()==""){
		 alert('客户名称不能为空');
		 return;
	 }
	 if($('#cond_PSPT_ID').val()==""){
		 alert('证件号码不能为空');
		 return;
	 }
	 
	 
	$.beginPageLoading("数据保存中...");
	
	$.ajax.submit('editPart', 'modifyData', null, 'editPart', function(data)
    {
		alert("提交成功！");
		setPopupReturnValue('','',true);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
 