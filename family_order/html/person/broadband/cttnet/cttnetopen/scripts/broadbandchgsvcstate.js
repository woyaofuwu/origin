

/**
 * 认证结束之后的时间
 * @param data
 * @return
 */
function refreshPartAtferAuth(data)
{
	//$("#IS_PRE_CHECKBOX").attr("disabled","");
	
	$.beginPageLoading("正在校验数据...");
	var param = "&SERIAL_NUMBER="+(data.get("USER_INFO")).get("SERIAL_NUMBER");
	 $.ajax.submit('AuthPart', 'checkBroadbandOpenFree', param, '', function(data){
		 if(data){
		 	var feeData = $.DataMap();
			feeData.clear();
			if(data.get("FEE_MODE") && data.get("FEE_TYPE_CODE") && data.get("FEE")){
				feeData.put("MODE", data.get("FEE_MODE"));
				feeData.put("CODE", data.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data.get("FEE"));
				feeData.put("PAY",  data.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","9717");		
				$.feeMgr.insertFee(feeData);
			}			
		}
	    $.endPageLoading();
	},
	function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	 });  
}


/**
 * 点击预约时间勾选框时间
 * @return
 */
function clickPreDate(obj)
{
	if(obj.checked)
	{
		$("#PRE_BUSI_DATE").attr("disabled","");
	}else
	{
		$("#PRE_BUSI_DATE").attr("disabled","true");
		$("#PRE_BUSI_DATE").val('');
	}
}

/**
 * 校验选择的预约时间
 * @return
 */
function checkPreBuisDate()
{
	var sysDate =$("#SYS_DATE").val();
	var selDate =$("#PRE_BUSI_DATE").val();
	if(!comparaDate(sysDate,selDate))
	{
		alert("预约时间必须大于当前时间！");
		$("#PRE_BUSI_DATE").val('');
	}
}

/**
 * 比较日期
 * @param sysDate
 * @param selDate
 * @return
 */
function comparaDate(sysDate,selDate)
{
	var sysDateArray = sysDate.split("-");
	var selDateArray = selDate.split("-");
	
	if(selDateArray[0]<sysDateArray[0])//比较年份
	{
		return false;
	}else if (selDateArray[0] == sysDateArray[0])
	{
		if(selDateArray[1]<sysDateArray[1])//比较月份
		{
			return false;
		}else if (selDateArray[1] == sysDateArray[1])
		{
			if(selDateArray[2]<=sysDateArray[2])//比较日期
			{
				return false;
			}
		}
	}
	return true;
}

function beforeCommitCheck()
{
/*
	if(document.getElementById("IS_PRE_CHECKBOX").checked)
	{
		if($("#PRE_BUSI_DATE").val() == "")
		{
			alert("请选择预约时间！");
			return false;
		}
	}*/
	
	return true;
}