
var idStrData = new Wade.DataMap();

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
	$.ajax.submit('QueryCondPart,QueryListPart,NavBarPart',
	'queryRebateApproveInfo',null,'QueryListPart,NavBarPart',
	function(data)
	{		
	   if(!data || data.length<1)
	   {
		   MessageBox.alert("状态","没有查询到数据。");	
	   }
	   $.endPageLoading();
	},	
	function(error_code,error_info){
		alert(error_info);
		$.endPageLoading();					
	});
}

function verifyBuziCond()
{
	var beginDate = $("#cond_START_DATE").val(); 
	var endDate = $("#cond_END_DATE").val(); 
	var d1 = new Date(beginDate.replace(/\-/g, "\/")); 
	var d2 = new Date(endDate.replace(/\-/g, "\/")); 

	if(beginDate !="" && endDate !="" && d1 >= d2) 
	{ 
		alert("开始时间不能大于结束时间!"); 
		return false; 
	}
	return true;
}

function showApproveDiv()
{
	if(!checkSelectedRecord())
	{
		return false;
	}
	show();
}
			
function checkSelectedRecord()
{
	idStrData.clear();
	
	var flag = true;
	$("input[name='chkbox']").each(function()
	{
		if($(this).attr("checked")==true && $(this).val() && $(this).val().length > 0)
		{
			if($(this).attr("approveresl") != '9')
			{
				alert("选择的审批记录中不可含有通过或不通过的审批结果！");
				flag = false;
			}
			idStrData.put($(this).val(),$(this).val());
		}
	});
	
	if(flag && !idStrData.length > 0)
	{
		alert("请选择一条或多条待处理的记录进行审批!");
		flag = false;
	}
	return flag;
}

function show()
{
	$("#editInfoPart").css("display","");
}

function hid()
{
	$("#editInfoPart").css("display","none");
}

function submitApprove()
{
	if(!$.validate.verifyAll("editInfoPart")) 
	{
		return false;
	}
	var rsltState = $("#RSLT_STATE").val();
	if(rsltState == null || rsltState == "")
	{
		alert("请选择修改审批状态!");
		return false;
	}
	if(rsltState == "9" )
	{
		alert("尚未修改审批状态，请修改审批状态后再提交！");
		return false;
	}
		
	$.beginPageLoading("审批中...");
	$.ajax.submit("editInfoPart", 'onSubmitApprove', "&OPR_SEQS=" + toIdStr(), null, function(data)
    {
		if(data.length>0 && data.get("RESULT_CODE")=="0000")
		{
			alert("审批成功！");
		} else {
			alert("审批失败！");
		}
		$.endPageLoading();
		hid();
		queryInfo();
	},
	function(error_code,error_info)
	{
		alert(error_info);
		$.endPageLoading();					
		hid();
    });
}
			
function toIdStr()
{
	var ids="";
	if(idStrData && idStrData.length > 0)
	{
		(idStrData).eachKey(function(key,index,totalcount){
			ids += "," + key;
		});
	}
	if(ids.indexOf(",") > -1 )
	{
		ids = ids.substring(1);
	}
	return ids;
}


