function refreshPartAtferAuth(data)
{
	var userId = $.auth.getAuthData().get('USER_INFO').get('USER_ID');
	var eparchyCode = $.auth.getAuthData().get('USER_INFO').get('EPARCHY_CODE');
	
	$.ajax.submit('', 'getUserSpecDiscntList', "&EPARCHY_CODE="+eparchyCode+"&USER_ID="+userId, 'chooseDiscntPart,userDiscntPart', 
	function(data)
	{
		$("#SYS_DATE").val(data.get('SYS_DATE'));
		$("#END_DATE").val(data.get('END_DATE'));
		$("#LAST_DAY_THIS_MONTH").val(data.get('LAST_DAY_THIS_MONTH'));
		$("#FIRST_DAY_NEXT_MONTH").val(data.get('FIRST_DAY_NEXT_MONTH'));
		$.endPageLoading();
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

function addDiscnt()
{
	//是否为空校验
	var chooseDiscnt = $("#CHOOSE_DISCNT").val();
	
	if(chooseDiscnt == null || chooseDiscnt == '')
	{
		$.MessageBox.alert("提示","请选择优惠!");
		return;
	}
	
	//是否已存在择校验
	var table = $.table.get("discntTable");
	
	//得到全table数据,包含未改变的数据，第一个参数传null,如需要单独列传列名，如下
	var discntList = table.getTableData("DISCNT_CODE",true);

	if(discntList.length > 0)
	{
		for(var i=0; i<discntList.length; i++)
		{
			var colDiscnt = discntList.get(i).get('DISCNT_CODE');
			
			if(colDiscnt == chooseDiscnt)
			{
				$.MessageBox.alert("提示","优惠已存在!");
				return;
			}
		}
	}
	
	//添加
	var rowAdd = {};
	rowAdd["DISCNT_CODE"] = chooseDiscnt;
	rowAdd["DISCNT_NAME"] = $("#CHOOSE_DISCNT")[0].options($("#CHOOSE_DISCNT")[0].selectedIndex).text;//$("#CHOOSE_DISCNT option:selected")[0].text; 这种也可以
	rowAdd["START_DATE"] = $("#FIRST_DAY_NEXT_MONTH").val();
	rowAdd["END_DATE"] = $("#END_DATE").val();
	
	var newrow = table.addRow(rowAdd);
}

function delDiscnt()
{
	MessageBox.confirm("提示", "确认删除？", 
	function(btn)
	{
		if(btn == "ok")
		{
			$.table.get("discntTable").deleteRow(null,true);
		}
		else
		{
			return;
		}
	}, null, null);
}

function onTradeSubmit()
{
	if(!$.validate.verifyAll())
	{
		return false;
	}
	
	var discntList = $.table.get("discntTable").getTableData();

	if(discntList.length <=0)
	{
		$.MessageBox.alert("提示","未进行任何操作!");
		
		return false;
	}
	
	var param = "&DISCNT_LIST="+discntList+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	
	$.cssubmit.addParam(param);
	
	return true;
}