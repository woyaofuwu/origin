// 设置返回值
function setReturnData()
{
	var opercode = $('#BIND_TAG').val();
   	if(opercode == "")
   	{
		alert("请选择操作类型!");
    	return false;
    }

	// 设置返回值
	var valueData = $.DataMap();
	valueData.put("BIND_TAG",  opercode);
	parent.$('#POP_CODING_STR').val("操作类型:" + opercode);
	parent.$('#CODING_STR').val(valueData);
	parent.hiddenPopupPageGrp();
}

