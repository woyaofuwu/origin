/**
 * 校验HTV SN编码
 * @param btn
 */
function checkRecycledTVBoxSN(btn)
{
	var hitvSN = $("#UN_HITV_SN").val();
	//判断账户规则
	if(hitvSN=="")
	{
		alert("TV+业务终端编号不能为空！");
		return false;
	}
	//TV+编码重复校验
	if (!tvBoxSnRepeatCheck())
	{
		return false;
	}
	
	$("#isCheckSuccess").attr('innerText','是');
	//$("#isCheckSuccess").val("on");
	$("#UN_HITV_SN").attr('oldValue',$("#UN_HITV_SN").val());
	
	alert("TV+终端SN码校验通过!");
	addRecyledTVBOXInfo();
		
}


/**
 * HITV号码重复校验
 * @returns {Boolean}
 */
function tvBoxSnRepeatCheck()
{
	var hitvInfo= RECYCLED_TVBOX_TABLE.getData(true, "col_RECYCLED_HITV_SN");
	
	var hitvSN = $("#UN_HITV_SN").val();
	
	if(hitvInfo != '[]')
	{
		
		for (i = 0; i<hitvInfo.length; i++)
			
		{
			if (hitvSN == hitvInfo.get(i).get("col_RECYCLED_HITV_SN"))
			{
				alert('该TV+终端SN码已经存在！');
				
				$("#UN_HITV_SN").val('');
				
				return false;
			}
		}
	}
	
	return true;
}




/**
 * 增加TV+编码
 * @returns {Boolean}
 */
function addRecyledTVBOXInfo()
{
	var hitvSN = $("#UN_HITV_SN").val();
	 
	if ('' == hitvSN)
	{
		alert("TV+终端SN码不能为空！");
		
		return false;
	} 
	
	if (hitvSN != $("#UN_HITV_SN").attr('oldValue'))
	{
		alert("您所输入的TV+终端SN码【"+hitvSN+"】和校验通过TV+终端SN码【"+$('#UN_HITV_SN').attr('oldValue')+"】不一致，请重新校验！");
		
		return false;
	}
	
	var hitvSN = $("#UN_HITV_SN").val();
	
	var data = $.DataMap();
	data["col_RECYCLED_HITV_SN"] = hitvSN;
	data["col_IS_CHECK"] = "是"; 
	data["col_CHECKBOX"] = '<input type="checkbox" name="checkBoxIndex1" id="checkBoxIndex1">';
	
	RECYCLED_TVBOX_TABLE.addRow(data);
	
	//新增后对输入数据进行重置
	$("#UN_HITV_SN").val("");
	//$("#HITV_RESULT_CODE").val("");
	$("#isCheckSuccess").attr('innerText','否');
}




/**
 * 删除选中的TV+号码
 * @param row
 */
function deleteRecyledTVBOXInfo()
{
 	//标识是否有选中数据
 	var flag = false;
	
	$("#recycledTVBoxTableInfos input[name=checkBoxIndex1]").each(function() {
		   if(this.checked)
		   {
			   flag =  true;
			   this.click();
			   RECYCLED_TVBOX_TABLE.deleteRow(RECYCLED_TVBOX_TABLE.selected);
			   $("#isCheckSuccess").attr('innerText','否');
			   $("#isCheckSuccess").val("off");
		   }
		});
	
	if (!flag)
	{
		alert("请选中需要删除的TV+终端编码！");
	}
}


/**
 * 获取checkbox 被选中部分
 */
function getCheckedInfos(){
	debugger;
	var data="";
    var cells = $("input[name=checkBoxIndex]");
	for ( var i = 0; i < cells.length; i++) {
		if (cells[i].checked) {
			var jsonData = hitvInfoTable.getRowData(i);
			if("是"==jsonData.get("CANCLE_TAG")){
				data += cells[i].value+"#"+ 1+ ",";
			}
		}else{
			var jsonData = hitvInfoTable.getRowData(i);
			if("否"==jsonData.get("CANCLE_TAG")){
				data += cells[i].value+"#"+ 0+ ",";
			}
		}
	}
	return data;
}



/**
 *                                       
 * 将表格中的TV+编码 组装到 HITVINFOS
 */
function getHITVInfo()
{
	
	//校验通过的要回收的魔百盒串码
	var hitvInfo= RECYCLED_TVBOX_TABLE.getData(true,"col_RECYCLED_HITV_SN");
	
	var hitvs = "";
	
	if(hitvInfo != '[]')
	{
		for (i = 0; i<hitvInfo.length; i++)
			
		{
			if (null != hitvs && '' != hitvs)
			{
				hitvs += ',';
			}
			
			hitvs += hitvInfo.get(i).get('col_RECYCLED_HITV_SN')
			
		}
		$('#HITVINFOS').val(hitvs);
	}
	else
	{
		//先新增后删除的情况需要将存在该字段的数据清空
		$('#HITVINFOS').val('');
	}
}



