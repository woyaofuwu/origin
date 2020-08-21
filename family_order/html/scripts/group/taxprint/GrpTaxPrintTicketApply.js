// 查询受理日志信息
function qryTaxLog()
{
	if(!$.validate.verifyAll("queryForm")) return false;
	
	var groupId = $("#POP_cond_GROUP_ID").val();
	
	if(groupId == null || groupId == "")
	{
		alert("集团客户编码不能为空!");
		return false;
	}
	
	$.beginPageLoading("数据查询中......");
	
	$.ajax.submit("queryForm", "qryTaxLog", null, "taxLogPart", 
		function(data)
		{
			$.endPageLoading();
		},
		function(error_code,error_info, derror)
		{
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
    );
}

// 查询客户资料失败后的方法
function selectGroupErrorAfterAction()
{
	$("#cond_CUST_NAME").val("");
	$("#cond_CUST_ID").val("");
	
	$("#taxLogBody").html("");
}

// 查询客户资料成功后的方法
function selectGroupAfterAction(data)
{
	var groupId = data.get("GROUP_ID");
	var custName = data.get("CUST_NAME")
	var custId = data.get("CUST_ID")
	
	$.beginPageLoading();
	$.ajax.submit(null, "qryCustTaxApply", "GROUP_ID=" + groupId, "taxApplyPart", 
		function(data)
		{
			$("#cond_CUST_NAME").val(custName);
			$("#cond_CUST_ID").val(custId);
			$.endPageLoading();
		},
		function(error_code,error_info, derror)
		{
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
    );
}

// 弹出详细信息
function showTaxDetail(tradeId)
{
	$.beginPageLoading();
	$.ajax.submit(null, "qryTaxDetail", "cond_TRADE_ID=" + tradeId, null, 
		function(data)
		{
			// 展示详细信息
			displayTaxDetail(data);
			$.endPageLoading();
		},
		function(error_code,error_info, derror)
		{
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
    );
}

// 展示展示信息
function displayTaxDetail(taxDetailList)
{
	$("#taxDetailBody").html(""); // 清空数据
	
	taxDetailList.each(function(item,idx)
	{
		$("#taxDetailBody").prepend(buildTaxDetailHtml(item));
			
	});
	
	$("#taxDetail").css("display", "");
}

// 隐藏展示信息
function hiddenTaxDetail(taxDetailList)
{
	$("#taxDetail").css("display", "none");
}

function buildTaxDetailHtml(taxDetail)
{
	var feeitemName = taxDetail.get("FEEITEM_NAME");
	var goodsName = taxDetail.get("GOODS_NAME");
	var unit = taxDetail.get("UNIT");
	var count = taxDetail.get("COUNT");
	var price = taxDetail.get("PRICE");
	var salePrice = taxDetail.get("SALE_PRICE");
	var rate = taxDetail.get("RATE");
	
	var html = "";
	
	html += "<tr>";
	html += "<td class='e_center'><span class='e_required'>" + feeitemName + "</span></td>";
	html += "<td>" + goodsName + "</td>";
	html += "<td>" + unit + "</td>";
	html += "<td>" + count + "</td>";
	html += "<td>" + price + "</td>";
	html += "<td>" + salePrice + "</td>";
	html += "<td>" + rate + "</td>";
	html += "</tr>";
	
	return html;
}

// 业务受理前规则校验
function onSubmitBaseTradeCheck()
{
	
	if(!$.validate.verifyAll("queryForm")) return false;
	
	var groupId = $("#POP_cond_GROUP_ID").val();
	
	if(groupId == null || groupId == "")
	{
		alert("集团客户编码不能为空!");
		return false;
	}
	
	if(!$.validate.verifyAll("taxApplyPart")) return false;
	
	if(!$.validate.verifyAll("nextUserPart")) return false;
	
	var selectTagObj = $("input[name='myRadio']");
	
	// 申请单号
	var tradeId = "";
	
	for(var i = 0;i < selectTagObj.length; i++)
	{
		if(selectTagObj[i].checked)
		{
			
			tradeId = $(selectTagObj[i]).val();
		}
	}
	
	if(tradeId == null || tradeId == "")
	{
    	alert("请选择增值税业务信息!");
    	return false;
    }
	
	$.cssubmit.setParam("cond_GROUP_ID", groupId);
	$.cssubmit.setParam("cond_TRADE_ID", tradeId);
	
	return true;
}