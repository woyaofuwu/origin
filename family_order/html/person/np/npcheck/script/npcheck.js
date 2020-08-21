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

function onTradeSubmit(form,boxName)
{
	if(!$.validate.verifyBox(form,boxName,'请选中要操作的数据!'))
	{
		return false;
	} 
	var boxList = $("*[name=" + boxName +"]");
	var idata=new Wade.DatasetList();
	for (var i = 0; i < boxList.length; i++){
		if (boxList[i].checked) {
			var sn = boxList[i].value;
			var data=new Wade.DataMap();
			data.put("SERIAL_NUMBER",sn);
			idata.add(data);
		}
	}
	var tempS = idata.toString();
	var param = '&CHECK_DATAS='+tempS;
	$.beginPageLoading("正在处理...");
    $.ajax.submit('QueryPart', 'submitNpCheck', param, 'ImportDataPart',function(data){
    	alert("审核通过！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function importBatData(){
	
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("批量审核中...");
	$.ajax.submit('SubmitCondPart','importBatData','','',function(data){
		alert('处理成功！');
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
 