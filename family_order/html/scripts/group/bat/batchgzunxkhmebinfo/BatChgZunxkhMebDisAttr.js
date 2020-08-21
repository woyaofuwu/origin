// 设置返回值
function setReturnData(){

	var discntCode = $("#DISCNT_CODE").val();
	if(discntCode == null || discntCode == "")
	{
		alert("请选择要操作的优惠编码!");
		return false;
	}

	var operFee = $("#operFee").val();
	if(operFee == null || operFee == "")
	{
		alert("请填写功能费(元)!");
		return false;
	}
	
	if(!checkMoney(operFee))
	{
		alert("请输入正确的金额格式!");
		$("#operFee").val('');
		return false;
	}
	
	var operDiscount = $("#operDiscount").val();
	if(operDiscount == null || operDiscount == "")
	{
		alert("请填写折扣(%)!");
		return false;
	}
	
	var flag = $.verifylib.checkPInteger(operDiscount);
	if(!flag)
	{
		alert("折扣(%)必须是整数!");
		$("#operDiscount").val('');
		return false;
	}
	
	if(operDiscount > 100 || operDiscount < 1)
	{
		alert("折扣(%)必须是1到100之间的整数!");
		$("#operDiscount").val('');
		return false
	}
										
	// 设置返回值
	var valueData = $.DataMap();
	
	valueData.put("DISCNT_CODE", discntCode);
	valueData.put("OPER_FEE", operFee);
	valueData.put("OPER_DISCOUNT", operDiscount);

	var showStr = "优惠编码：" + discntCode + " 功能费(元):" + operFee + " 折扣(%):" + operDiscount;
	parent.$('#POP_CODING_STR').val(showStr);
	parent.$('#CODING_STR').val(valueData);
	
	parent.hiddenPopupPageGrp();
}

/**
用途：检查输入字符串是否符合金额格式 
格式定义为整数
*/
function checkMoney(obj){
	//var regu = "^[0-9]*(\.[0-9]{1,2})?$"; 
	var regu = "^[0-9]*$"; 
	var reg = new RegExp(regu); 
	if (reg.test(obj)) { 
		return true; 
	} else { 
		return false; 
	} 
}


function checkRadio(radioId){
	var operValue =$('#'+radioId).val();
	if(operValue == "1")
	{
		$("#SvcPart").css('display','block');
		$("#DiscntPart").css('display','none');
	}
	else if(operValue == "0")
	{
		$("#SvcPart").css('display','none');
		$("#DiscntPart").css('display','block');
	}
}


