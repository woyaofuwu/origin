//历史机卡配对关系查询
function queryHCard()
{
	//查询条件校验
	if(!$.validate.verifyAll("DMBusiHCardQuryPart"))
	{
		return false;
	}
	
	if($("#cond_PHONENUM").val()=="" && $("#cond_IMEINUM").val()=="")
	{
		MessageBox.alert("提示","手机号码和IMEI号不能同时为空。");
		return false;
	}
	if($("#cond_PHONENUM").val()!="" && $("#cond_IMEINUM").val()!="")
	{
		MessageBox.alert("提示","手机号码和IMEI号只能输入一个。");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('DMBusiHCardQuryPart', 'queryHCard', null, 'QueryListPart', function(data)
	{
		if(data.length<1)
		{
			MessageBox.alert("提示","没有符合查询条件的信息数据！");
		}
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//最新机卡配对关系查询
function queryDMBusiCard()
{
	//查询条件校验
	if(!$.validate.verifyAll("DMBusiCardSelPart"))
	{
		return false;
	}
	
	if($("#cond_PHONENUM").val()=="" && $("#cond_IMEINUM").val()=="")
	{
		MessageBox.alert("提示","手机号码和IMEI号不能同时为空。");
		return false;
	}
	if($("#cond_PHONENUM").val()!="" && $("#cond_IMEINUM").val()!="")
	{
		MessageBox.alert("提示","手机号码和IMEI号只能输入一个。");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('DMBusiCardSelPart', 'queryDMBusiCard', null, 'QueryListPart', function(data)
	{
		if(data.length<1)
		{
			MessageBox.alert("提示","没有符合查询条件的信息数据！");
		}
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//终端静态信息查询
function queryStaticInfo()
{
	//查询条件校验
	if(!$.validate.verifyAll("StaticInfoQuryPart"))
	{
		return false;
	}
	
	if($("#cond_TERMID").val()=="")
	{
		MessageBox.alert("提示","终端型号标识不能为空。");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('StaticInfoQuryPart', 'queryStaticInfo', null, 'QueryListPart', function(data)
	{
		if(data.length<1)
		{
			MessageBox.alert("提示","没有符合查询条件的信息数据！");
		}
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
//DM业务查询
function queryDMBusi()
{
	//查询条件校验
	if(!$.validate.verifyAll("DMBusiPart"))
	{
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('DMBusiPart', 'queryDMBusi', null, 'QueryListPart,QueryListSubPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function queryDMBusi_Sub(obj){
	
	var pa_operateid = obj.getAttribute("operid");
	var pa_apply_type =   obj.getAttribute("apply_type");
	
	openNav('DM业务数据关联查询','sundryquery.interboss.dmbusisel.DMBusiSel', 'queryDMBusi_Sub', '&PA_OPERATEID='+pa_operateid+'&PA_APPLY_TYPE='+pa_apply_type);
	
}

//DM业务操作取消
function afterTrade(rowIndex)
{
	var selTable = $.table.get("DMBusiSelTable");
	var rowValue = selTable.getRowData(null,rowIndex);
	var cancel_tag = rowValue.get("CANCEL_TAG");
	var cancel_time =rowValue.get("CANCEL_TIME");
	if(cancel_tag =='取消' && cancel_time !=''){
		alert("已经取消,不能再进行取消操作！");
		return false;
	}
	
	MessageBox.confirm("确认信息","确定取消该笔业务吗？", function(btn)
	{
		if(btn=="ok")
		{
			var operid = rowValue.get("OPERATEID");
			$.beginPageLoading("取消该笔业务中...");
			$.ajax.submit(this, 'submitTrade', '&OPERATE_ID='+operid, null, function(ajaxDataset){afterAjax(ajaxDataset)});
		}
	});
}

function afterAjax(ajaxDataset)
{
	if(ajaxDataset.get(0).get("X_RESULTCODE") == 0)
	{
		MessageBox.confirm("确认信息","操作成功！<br/>点击【确定】返回查看已取消数据。", function(btn)
		{
			var operid = ajaxDataset.get(0).get("OPERATEID");
			
			if(btn=="ok")
			{
				$.beginPageLoading("正在查询数据...");	
	
				$.ajax.submit(null, 'queryDMBusi_Sub', '&PA_OPERATEID='+operid, 'QueryListPart,QueryListSubPart', function(data)
				{
					$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
    			});
			}
	    });
	}
	$.endPageLoading();
}