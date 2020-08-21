//导入系统文件
function pccImport(){
	//查询条件校验
	if(!$.validate.verifyAll("UserPccImportPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('UserPccImportPart', 'onTradeSubmit', null, 'UserPccImportPart', function(data){
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
		alert("###"+url+"###"+name);
		$.popupDialog('userpcc.ComDownExport', 'downMSG', '&URL='+encodeURIComponent(url)+'&NAME='+encodeURIComponent(name), '错误信息附件下载列表', '800', '400',null);
	}
	else if(result=="S")
	{
		$.showSucMessage("操作成功","批量导入信息已入库！",this);
	}
		
}

function addInfo()
{  
//	var param = "&X_CODING_STR="+$("#X_CODING_STR").val();
//	
//	$.ajax.submit('', 'checkNums', param, 'QueryInfoPart', function(data){
	var obj = $("#SERIAL_NUMBER");
	if(!$.verifylib.checkMbphone(obj.val())){
		alert(obj.attr("title") + "必须为手机号码!");
		obj.focus();
		return false;
	}
	
	var serviceEdit = new Array();
	serviceEdit["SERIAL_NUMBER"] =$("#SERIAL_NUMBER").val();
	$.table.get("serialTable").addRow(serviceEdit);
	obj.val('');
//	},
//	function(error_code,error_info){
//		$.endPageLoading();
//		alert(error_info);
//    });
	
	 
}

function delInfo()
{  
	var tab = $.table.get("serialTable");
	tab.deleteRow();
}

function onTradeSubmit()
{
	var serialData = $.table.get("serialTable").getTableData();
	
	if(serialData.length==0){
		alert("没有数据可以提交");
		return false;
	}
	
	var param = "&serialData="+serialData;
	$.cssubmit.addParam(param);
	
	return true;
}

function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'QueryInfoPart', function(data){
		
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}








 