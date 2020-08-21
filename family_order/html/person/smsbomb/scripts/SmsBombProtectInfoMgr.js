
function queryUserInfo()
{
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(serialNumber == null || serialNumber == "")
	{
		MessageBox.alert("提示","手机号码不能为空!");
		return false;
	}
	$("#PROTECT_NUM").val("");
	$("#EXPIRE_DATE").val("");
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&SERIAL_NUMBER=" + serialNumber, 
		'protectPart,ProtectEditPart,whitePart,WhiteEditPart', 
		function(data) {
			if(data != null && data.getCount() > 0)
			{
				$("#PROTECT_NUM").val(data.get("SERIAL_NUMBER"));
			}
			else 
			{
				$("#PROTECT_NUM").val(serialNumber);
			}
			$.endPageLoading();
		}, 
		function(error_code, error_info,detail)
		{
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		});
}


//点击表格行，初始化编辑区
function tableRowClick()
{
	var rowData = $.table.get("protectTable").getRowData();
	$("#PROTECT_NUM").val(rowData.get("PROTECT_NUM"));
	$("#EXPIRE_DATE").val(rowData.get("EXPIRE_DATE"));
}

function createPhone(obj)
{
	/* 校验所有的输入框 */
	if(!checkEditPart()) 
	{
		return false;
	}
	
	var data = $.table.get("protectTable").getTableData(null, true);
	if(data != null && data.length > 0)
	{
		//alert("保护名单列表只能新增一条记录!");
		MessageBox.alert("提示","保护名单列表只能新增一条记录!");
		return false;
	}
	
	var editData = $.ajax.buildJsonData("ProtectEditPart");
	editData['PROTECT_NUM'] = $("#PROTECT_NUM").val();
	editData['EXPIRE_DATE'] = $("#EXPIRE_DATE").val();
		
	/* 新增表格行 */
	$.table.get("protectTable").addRow(editData,null,null,null,true);
	
	$("#bcreate").attr("disabled",false);	
	$("#bupdate").attr("disabled",false);	
	$("#bdelete").attr("disabled",false);	

	$("#EXPIRE_DATE").val("");
}

function updatePhone(obj)
{
	var rowData = $.table.get("protectTable").getRowData();
	if (rowData.length == 0) 
	{
		//alert("请您选择记录后再进行修改操作!");
		MessageBox.alert("提示","请您选择记录后再进行修改操作!");
		return false;
	}
			
	/* 校验所有的输入框 */
	if (!checkEditPart())
	{
	 	return false;	
	}
	
	var editData = $.ajax.buildJsonData("ProtectEditPart");
	editData['PROTECT_NUM']=$("#PROTECT_NUM").val();
	editData['EXPIRE_DATE']=$("#EXPIRE_DATE").val();
	
	$.table.get("protectTable").updateRow(editData,null,null,true);
	$("#EXPIRE_DATE").val("");
}

function deletePhone(obj)
{
	var rowData = $.table.get("protectTable").getRowData();
	if (rowData.length == 0) 
	{
		//alert("请您选择记录后再进行删除操作！");
		MessageBox.alert("提示","请您选择记录后再进行删除操作!");
		return false;
	}
	$.table.get("protectTable").deleteRow();
	var phoneTable = $.table.get("protectTable").getTableData(null, true);

	var size =0;
	phoneTable.each(function(item,index,totalCount)
	{
		if(item.get("tag") !="1")
		{
			size++;
		}
	});
	
	$("#bcreate").attr("disabled",false);	
	$("#bupdate").attr("disabled",false);	
	$("#bdelete").attr("disabled",false);	
	$("#EXPIRE_DATE").val("");
}


function checkEditPart(){
	if(!$.validate.verifyAll("ProtectEditPart")) {
		return false;
	}
	return true;
}

function checkAndSubmit()
{
	var serialNum = $("#cond_SERIAL_NUMBER").val();
	var protectNum = $("#PROTECT_NUM").val();
	if(serialNum == null || serialNum == "")
	{
		MessageBox.alert("提示","请您输入手机号码查询后,再进行操作!");
		return false;
	}
	if(protectNum == null || protectNum == "")
	{
		MessageBox.alert("提示","请您输入手机号码查询后,再进行操作!");
		return false;
	}
	if(serialNum != protectNum)
	{
		MessageBox.alert("提示","您输入的手机号码和被保护号码不一致,请您重新后查询再操作!");
		return false;
	}
	
	var data = $.table.get("protectTable").getTableData(null, true);
	if(data != null && data.length <= 0)
	{
		MessageBox.alert("提示","请新增保护名单列表后再提交!");
		return false;
	}
	
	var submitData = $.table.get("protectTable").getTableData(null, false);
	var size =0;
	var operType ="-1";
	submitData.each(function(item,index,totalCount)
	{
		if(item.get("tag") =="0")
		{
			size++; 
			operType = "0";
		}
		else if(item.get("tag")=="1")
		{
			size++; 
			operType = "1";
		}
		else if(item.get("tag")=="2")
		{
			size++;
			operType = "2";
		}
	});
	
	var whiteData = $.table.get("whiteTable").getTableData(null, false);
	var subSize = 0;
	whiteData.each(function(item,index,totalCount)
	{
		if(item.get("tag") =="0")
		{
			subSize++; 
		}
		else if(item.get("tag")=="1")
		{
			subSize++; 
		}
	});
	
	if(size < 1)
	{
		if(subSize < 1)
		{
			MessageBox.alert("提示","没有数据可以提交!");
			return false;
		} 
		else 
		{
			operType = "2";
		}
	}
	
	var param = "&PROTECT_DATASET=" + submitData.toString() 
		+ "&PROTECT_OPERTYPE=" + operType + "&PROTECT_DATASUB=" + whiteData.toString() ;
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('QueryUserPart,protectPart,ProtectEditPart', 
		'transAction', param, 
		'QueryUserPart,protectPart,ProtectEditPart,whitePart,WhiteEditPart', 
		function(data)
		{
			MessageBox.success("提示","操作成功！",function(btn)
			{
				if(btn=="ok")
				{
					window.location.href = window.location.href;
				}
			});
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
   		});
}


//点击表格行，初始化编辑区
function tableRowClick2()
{
	var rowData = $.table.get("whiteTable").getRowData();
	$("#WHITE_NUM").val(rowData.get("WHITE_NUM"));
	$("#WHITE_TYPE").val(rowData.get("WHITE_TYPE"));
	$("#WHITE_TYPE_NAME").val(rowData.get("WHITE_TYPE_NAME"));
}

function createWhitePhone(obj)
{
	/* 校验所有的输入框 */
	if(!checkWhiteEditPart()) 
	{
		return false;
	}
	
	var data = $.table.get("whiteTable").getTableData(null, true);
	if(data != null && data.length > 10)
	{
		//alert("保护名单列表只能新增一条记录!");
		MessageBox.alert("提示","白名单列表只能新增十条记录!");
		return false;
	}
	var whiteTypeName = $("#WHITE_TYPE option:selected").text();
	var editData = $.ajax.buildJsonData("WhiteEditPart");
	editData['WHITE_NUM'] = $("#WHITE_NUM").val();
	editData['WHITE_TYPE'] = $("#WHITE_TYPE").val();
	editData['WHITE_TYPE_NAME'] = whiteTypeName;
	
	if($.table.get("whiteTable").isPrimary("WHITE_NUM", editData)){
		//alert("该号码已经存在,请重新输入!");
		MessageBox.alert("提示","该号码已经存在,请重新输入!");
		return false;
	}
	
	/* 新增表格行 */
	$.table.get("whiteTable").addRow(editData,null,null,null,true);
	
	$("#bcreateWhite").attr("disabled",false);	
	$("#bdeleteWhite").attr("disabled",false);	

	$("#WHITE_NUM").val("");
	$("#WHITE_TYPE").val("");
}

function deleteWhitePhone(obj)
{
	var rowData = $.table.get("whiteTable").getRowData();
	if (rowData.length == 0) 
	{
		//alert("请您选择记录后再进行删除操作！");
		MessageBox.alert("提示","请您选择记录后再进行删除操作!");
		return false;
	}
	$.table.get("whiteTable").deleteRow();
	var phoneTable = $.table.get("whiteTable").getTableData(null, true);

	var size =0;
	phoneTable.each(function(item,index,totalCount)
	{
		if(item.get("tag") !="1")
		{
			size++;
		}
	});
	
	$("#bcreateWhite").attr("disabled",false);	
	$("#bdeleteWhite").attr("disabled",false);	
	$("#WHITE_NUM").val("");
	$("#WHITE_TYPE").val("");
}

function checkWhiteEditPart(){
	if(!$.validate.verifyAll("WhiteEditPart")) {
		return false;
	}
	//alert($("#WHITE_TYPE").val());
	var whiteType = $("#WHITE_TYPE").val();
	return true;
}


