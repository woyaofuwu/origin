//导入系统文件
function pccImport(){
	//查询条件校验
	if(!$.validate.verifyAll("UserPccImportPart")) {
		return false;
	}
	if(confirm('确定要导入用户动态策略计费控制信息导入吗？'))
	{
		$.beginPageLoading("数据导入中..");
		$.ajax.submit('UserPccImportPart', 'onTradeSubmit', null, 'UserPccImportPart', function(data){
			$.endPageLoading(); 
			afterDo(data);
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	
}

function afterDo(ajaxDataset){
	var result = ajaxDataset.get("MSG_TYPE");
	if(result=="F"){
		var url = ajaxDataset.get("MSG");
		var name = ajaxDataset.get("NAME");
		$.popupDialog('userpcc.ComDownExport', 'downMSG', '&URL='+encodeURIComponent(url)+'&NAME='+encodeURIComponent(name), '错误信息附件下载列表', '800', '400',null);
	}
	else if(result=="S")
	{
		$.showSucMessage("操作成功","批量导入信息已入库！",this);
	}
		
}
 