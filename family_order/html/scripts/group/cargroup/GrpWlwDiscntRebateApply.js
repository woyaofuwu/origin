
function queryInfo()
{
	/**
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	**/	
	var condEcId = $("#cond_GROUP_SERIAL_NUMBER").val();
	if(condEcId == null || condEcId == "")
	{
		alert("请输入集团产品编码再查询!");
		return false;
	}
	if(!verifyBuziCond())
	{
		return false;
	}
	
	$.beginPageLoading("数据查询中.."); 				
	$.ajax.submit('QueryCondPart,QueryListPart,NavBarPart','queryDiscntApplyInfo',null,
	'QueryListPart,NavBarPart',function(data)
	{		
	   if(!data || data.length<1)
	   {
		   MessageBox.alert("状态","没有查询到数据。");	
	   }
	   $.endPageLoading();
	},	
	function(error_code,error_info)
	{
		alert(error_info);
		$.endPageLoading();					
	});
}

function verifyBuziCond()
{
	var beginDate=$("#cond_START_DATE").val(); 
	var endDate=$("#cond_END_DATE").val(); 
	var d1 = new Date(beginDate.replace(/\-/g, "\/")); 
	var d2 = new Date(endDate.replace(/\-/g, "\/")); 
	
	if(beginDate != "" && endDate !="" && d1 >= d2) 
	{ 
		alert("开始时间不能大于结束时间！"); 
		return false; 
	}
	return true;
}

function showAddDiv()
{
	$("#editInfoPart").css("display","");
}

function hid()
{
	$("#editInfoPart").css("display","none");
}
			
function  submitAdd()
{
	if(!$.validate.verifyAll("editInfoPart"))
	{
		return false;
	}
	
	var data = $.table.get("protectTable").getTableData(null, true);
	if(data != null && data.length <= 0)
	{
		MessageBox.alert("提示","请新增折扣产品信息列表后再提交!");
		return false;
	}
	
	var submitData = $.table.get("protectTable").getTableData(null, false);
	var param = "&DIS_PRODUCTS_INFO=" + submitData.toString();
	
	$.beginPageLoading("数据新增中...");
	$.ajax.submit("editInfoPart", 'onSubmitAdd', param, null, 
	function(data)
	{
		if(data.length>0 && data.get("RESULT_CODE")=="0000")
		{
			alert("添加成功！");
		} 
		else 
		{
			alert("增加失败！");
		}
		$.endPageLoading();
		hid();
		var grouId = $("#EC_ID").val();
		$("#cond_GROUP_SERIAL_NUMBER").val(grouId);
		queryInfo();
	},
	function(error_code,error_info)
	{
		alert(error_info);
		$.endPageLoading();					
		hid();
    });
}
			
//点击表格行，初始化编辑区
function tableRowClick()
{
	var rowData = $.table.get("protectTable").getRowData();
	$("#PROD_ID").val(rowData.get("PROD_ID"));
	$("#PROD_TYPE").val(rowData.get("PROD_TYPE"));
	$("#PROD_TYPE_NAME").val(rowData.get("PROD_TYPE_NAME"));
	$("#DISCNT_RATE").val(rowData.get("DISCNT_RATE"));
	$("#CARD_BIND").val(rowData.get("CARD_BIND"));
	$("#CARD_BIND_NAME").val(rowData.get("CARD_BIND_NAME"));
}


function createRebate(obj)
{
	/* 校验所有的输入框 */
	if(!checkEditPart()) 
	{
		return false;
	}
	
	var data = $.table.get("protectTable").getTableData(null, true);
	
	var editData = $.ajax.buildJsonData("ProtectEditPart");
	editData['PROD_ID'] = $("#PROD_ID").val();
	var prodTypeName = $("#PROD_TYPE option:selected").text();
	editData['PROD_TYPE'] = $("#PROD_TYPE").val();
	editData['PROD_TYPE_NAME'] = prodTypeName;
	editData['DISCNT_RATE'] = $("#DISCNT_RATE").val();
	var cardBindName = $("#CARD_BIND option:selected").text();
	editData['CARD_BIND'] = $("#CARD_BIND").val();
	editData['CARD_BIND_NAME'] = cardBindName;

	if($.table.get("protectTable").isPrimary("PROD_ID,DISCNT_RATE", editData))
	{
		MessageBox.alert("提示","该PBOSS产品编码,折后价已经存在,请重新输入!");
		return false;
	}
			
	/* 新增表格行 */
	$.table.get("protectTable").addRow(editData,null,null,null,true);
	
	var data = $.table.get("protectTable").getTableData(null, true);
	
	$("#bcreate").attr("disabled",false);	
	$("#bupdate").attr("disabled",false);	
	$("#bdelete").attr("disabled",false);	
	$("#PROD_ID").val("");
	$("#PROD_TYPE").val("");
	$("#DISCNT_RATE").val("");
	$("#CARD_BIND").val("");
}

function updateRebate(obj)
{
	var rowData = $.table.get("protectTable").getRowData();
	if (rowData.length == 0) 
	{
		MessageBox.alert("提示","请您选择记录后再进行修改操作!");
		return false;
	}
			
	/* 校验所有的输入框 */
	if (!checkEditPart())
	{
	 	return false;	
	}
	
	var editData = $.ajax.buildJsonData("ProtectEditPart");
	editData['PROD_ID'] = $("#PROD_ID").val();
	var prodTypeName = $("#PROD_TYPE option:selected").text();
	editData['PROD_TYPE'] = $("#PROD_TYPE").val();
	editData['PROD_TYPE_NAME'] = prodTypeName;
	editData['DISCNT_RATE'] = $("#DISCNT_RATE").val();
	var cardBindName = $("#CARD_BIND option:selected").text();
	editData['CARD_BIND'] = $("#CARD_BIND").val();
	editData['CARD_BIND_NAME'] = cardBindName;
	
	$.table.get("protectTable").updateRow(editData,null,null,true);
	$("#PROD_ID").val("");
	$("#PROD_TYPE").val("");
	$("#DISCNT_RATE").val("");
	$("#CARD_BIND").val("");
}

function deleteRebate(obj)
{
	var rowData = $.table.get("protectTable").getRowData();
	if (rowData.length == 0) 
	{
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
	$("#PROD_ID").val("");
	$("#PROD_TYPE").val("");
	$("#DISCNT_RATE").val("");
	$("#CARD_BIND").val("");
}

function checkEditPart()
{
	var prodId = $("#PROD_ID").val();
	if(prodId == null || prodId == "")
	{
		alert("请填写PBOSS产品编码!");
		return false;
	}
	
	var prodType = $("#PROD_TYPE").val();
	if(prodType == null || prodType == "")
	{
		alert("请选择产品类型!");
		return false;
	}
	
	var discntRate = $("#DISCNT_RATE").val();
	if(discntRate == null || discntRate == "")
	{
		alert("请填写折后价!");
		return false;
	}
	var flag = $.verifylib.checkNumeric(discntRate ,"0.00");
	if(!flag){
		alert("折后价必须是数字或格式为0.00的数字!");
		return false;
	}
	var floatFlag = parseFloat(discntRate);
	if(floatFlag < 0)
	{
		alert("折后价不能为负数!");
		return false;
	}
	var dotLength = discntRate.indexOf(".");
	if(dotLength > 0)
	{
		var tempRate = discntRate.substring(dotLength + 1,discntRate.length);
		if(tempRate.length != 2)
		{
			alert("折后价必须精确到小数点后两位!");
			return false;
		}
	}

	var cardBind = $("#CARD_BIND").val();
	if(cardBind == null || cardBind == "")
	{
		alert("请选择机卡绑定!");
		return false;
	}
	
	return true;
}


			