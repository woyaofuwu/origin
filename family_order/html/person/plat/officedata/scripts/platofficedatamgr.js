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


function querySPBiz()
{
	$.beginPageLoading("Loading...");
	
	ajaxSubmit('QueryCondPart', 'querySPBiz', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}



function querySPInfo()
{
	$.beginPageLoading("Loading...");
	
	ajaxSubmit('QueryCondPart', 'querySPInfo', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}



function deleteSPInfo()
{
	var flag = false;
	
	var bizCodes = '';
	 $.each
		(
			$("input[name='BIZ_CODES']"), function (index,obj)
			{
				if(obj.checked)
				{
					bizCodes = bizCodes+obj.value+',';
					flag = true;
				}
			}
		);
	 
	 bizCodes = bizCodes.substr(0, bizCodes.length-1);
	 
	 if(!flag)
	 {
		 MessageBox.alert("提示","请先选择要删除的企业信息！");
		 return;
	 }
	 
	 MessageBox.confirm("提示","确定删除SP信息?",function (btn){
		 if(btn !="ok")
		 {
			 return;
		 }
		 
		 ajaxSubmit('QueryCondPart', 'delSPInfo', '&BIZ_CODES='+bizCodes, 'QueryListPart', function(data){
				MessageBox.alert("提示","删除成功");
			},
			function(error_code,error_info){
				$.endPageLoading();
				MessageBox.error("错误",error_info);
			}
			 );
		 
	 });
	 
   
	
}



function deleteSPBiz()
{
	var flag = false;
	var bizCodes = '';
	 $.each
		(
			$("input[name='BIZ_CODES']"), function (index,obj)
			{
				if(obj.checked)
				{
					bizCodes = bizCodes+obj.value+',';
					flag = true;
				}
			}
		);
	 
	 bizCodes = bizCodes.substr(0, bizCodes.length-1);
	 
	 if(!flag)
	 {
		 MessageBox.alert("提示","请先选择要删除的SP服务信息！");
		 return;
	 }
	 
	 MessageBox.confirm("提示","确定删除SP业务信息?",function(btn){
		 if(btn != "ok")
		 {
			 return;
		 }
		 
		 ajaxSubmit('QueryCondPart', 'delSPBiz', '&BIZ_CODES='+bizCodes, 'QueryListPart', function(data){
				MessageBox.alert("提示","删除成功");
			},
			function(error_code,error_info){
				$.endPageLoading();
				MessageBox.error("错误",error_info);
			}
			);
	});

	
}


function querySPBizBat()
{
	$.beginPageLoading("Loading...");
	
	ajaxSubmit('QueryCondPart', 'querySPBizBat', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}



function querySPInfoBat()
{
	$.beginPageLoading("Loading...");
	
	ajaxSubmit('QueryCondPart', 'querySPInfoBat', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}

function importSPInfoBat()
{
	if($("#FILE_ID").val() == '')
	{
		MessageBox.alert("提示","请选择要导入的文件");
		return;
	}
	
	$.beginPageLoading("Import...");
	ajaxSubmit('DataImportPart', 'importSPInfo', null, null, function(data){
		$.endPageLoading();
		$.showSucMessage("导入成功!");
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}


function importSPBizBat()
{
	if($("#FILE_ID").val() == '')
	{
		MessageBox.alert("提示","请选择要导入的文件");
		return;
	}
	$.beginPageLoading("Import...");
	ajaxSubmit('DataImportPart', 'importSPBiz', null, null, function(data){
		$.endPageLoading();
		$.showSucMessage("导入成功!");
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}




function querySPInfoBatDetail()
{
	$.beginPageLoading("Loading...");
	ajaxSubmit('QueryCondPart', 'querySPInfoBatDetail', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}


function querySPBizBatDetail()
{
	$.beginPageLoading("Loading...");
	ajaxSubmit('QueryCondPart', 'querySPBizBatDetail', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	}
	 );
}