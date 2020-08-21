
function init()
{

}

function queryGrpCustInfo(){
	if(!$.validate.verifyAll("AddDelPart")) {
		return false;
	}
	$.beginPageLoading("查询中...");
	$.ajax.submit('AddDelPart', 'getGrpCustInfos', null, 'grpcustRefreshPart', 
	function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		clearGrpCautionFee();
		showDetailErrorInfo(error_code,error_info,derror);
    });
  
}


function onSubmitBaseTradeCheck()
{
	var grpNumber = $("#cond_SERIAL_NUMBER").val();
	if(grpNumber == null || grpNumber == "")
	{
		alert("请输入集团产品服务编码!");
		return false;
	}
	var grpSn = $("#GRP_SN").val();
	if(grpSn == null || grpSn == "")
	{
		alert("请输入集团产品服务编码查询后再新增!");
		return false;
	}
	
	if(grpNumber != grpSn)
	{
		alert("您输入的集团产品服务编码有变化,请重新输入查询后再新增!");
		return false;
	}
	
	var auditOrder = $("#AUDIT_ORDER").val();
	if(auditOrder == null || auditOrder == "")
	{
		alert("请输入审批工单号!");
		return false;
	}
	
	var depositFee = $("#DEPOSIT_FEE").val();
	if(depositFee == null || depositFee == "")
	{
		alert("请填写保证金额!");
		return false;
	}
	else 
	{		
		var flag = $.verifylib.checkNumeric(depositFee ,"0.00");
		if(!flag){
			alert("保证金额必须是数字或格式为0.00的数字");
			return false;
		}
		
		var dotLength = depositFee.indexOf(".");
		if(dotLength > 0)
		{
			if(dotLength > 8)
			{
				alert("保证金金额的整位数不能超过8位,请重新输入!");
				return false;
			}
		}
		else 
		{
			if(depositFee.length > 8)
			{
				alert("保证金金额的位数不能超过8位,请重新输入!");
				return false;
			}
		}
		var floatFee = parseFloat(depositFee) * 100;
		if(floatFee == 0)
		{
			alert("保证金额不能是零!");
			return false;
		}
		if(floatFee > 2147483648)
		{
			alert("保证金额不能大于2147483648!");
			return false;
		}
	}
	
	var depositTypeV = $("#DEPOSIT_TYPE").val();
	if(depositTypeV == null || depositTypeV == "")
	{
		alert("请选择保证金子项!");
		return false;
	}
	
	return true;
}

function clearGrpCautionFee()
{
	//清空页面显示的集团客户信息内容
	clearGroupCustInfo();
	//清空页面显示的集团客户信息内容
	clearGroupUserInfo();
	
	$("#AUDIT_ORDER").val("");
	$("#DEPOSIT_FEE").val("");
	$("#DEPOSIT_TYPE").val("");
}

function clearGrpCautionFeeAll()
{
	//清空页面显示的集团客户信息内容
	clearGroupCustInfo();
	//清空页面显示的集团客户信息内容
	clearGroupUserInfo();
	$("#cond_SERIAL_NUMBER").val("");
	$("#AUDIT_ORDER").val("");
	$("#DEPOSIT_FEE").val("");
	$("#DEPOSIT_TYPE").val("");
}