function resetUrl()
{
	var href = window.location.href;
	if(href){
		if(href.lastIndexOf("#nogo") == href.length-5){
			href = href.substring(0, href.length-5);
		}
	}
	window.location.href = href;
}


function queryPlatOrderRelationBat()
{
	$.beginPageLoading("Loading...");
	
	ajaxSubmit('QueryCondPart', 'queryPlatOrderRelationBat', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}

function importPlatOrderRelation()
{
	if($("#FILE_ID").val() == '')
	{
		MessageBox.alert("提示","请选择要导入的文件");
		return;
	}
	
	$.beginPageLoading("Import...");
	ajaxSubmit('DataImportPart', 'importPlatOrderRelation', null, null, function(data){
		$.endPageLoading();
		$.showSucMessage("导入成功!");
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}

function runBatDeal(){
	var importid = getCheckedValues("trades");
   if(null == importid || "" == importid) {
   		alert("请选择需要启动的批量任务！");
   }else {
   		$.ajax.submit('QueryCondPart','startBatDeals','&IMPORT_IDS='+importid,'QueryListPart',function(data){
   		
		$.endPageLoading();
		$.showSucMessage(data.map.result,"");
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
   }
}
