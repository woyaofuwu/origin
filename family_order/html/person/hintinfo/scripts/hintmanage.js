function queryDiscntInfo()
{
	//查询条件校验
//	if(!$.validate.verifyAll("QueryCondPart")) {
//		return false;
//	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryDiscntInfo', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
 
//导入系统文件
function importDiscntExplain(){
	//查询条件校验
	if(!$.validate.verifyAll("DiscntImportPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('DiscntImportPart', 'importDiscnt', null, 'DiscntImportPart', function(data){
		$.endPageLoading(); 
		afterDo(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
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


function openImportDiscnt(){ 
	
	popupPage('hintinfo.DiscntImport', null,
			null,
			'优惠描述信息导入','600','400');
}

function openUpdateDiscnt(discntCode)
{ 
	openNav('修改优惠描述信息','hintinfo.UpdateDiscnt', 'queryUpDiscntInfo', '&DISCNT_CODE='+discntCode);
}

