function queryDatas()
{
	$.ajax.submit('QueryCondPart,payRelationNav', 'queryDatas', null, 'QueryListPart',null,null);
}

function setRecordInfo(obj)
{
	var element = null;
	var elements = document.getElementsByName('records');
	for(var i=0;i<elements.length;i++)
	{
		if(elements.item(i).checked)
		{
			element = elements.item(i).value;
			break;
		}
	}
	
	if(element == null || "" == element) {
		alert('请选择一条记录');
		return ;
	}
	
	var arr_element = element.split(";");
	var filename= arr_element[0];
	var datatype= arr_element[1];
	var oprnumb= arr_element[2];
	var dealflag= arr_element[3];
	
	var param = "&DATA_TYPE="+ datatype;
	param += "&OPRNUMB="+ oprnumb;
	param += "&FILE_NAME="+ filename;
	
	//如果是1，则调用ajax进行产品配置反馈
	if('1' == obj)
	{
	    if('1' == dealflag)
	    {
	        MessageBox.alert('提示','该条记录的处理状态为已配置,不需要再进行配置反馈');
	    }
	    else
	    {
	        $.ajax.submit(this, 'configFeedback', param, 'QueryListPart',
		        function(data){
						if(data.get('ALERT_INFO') != '')
						{
							MessageBox.alert("提示",data.get('ALERT_INFO'));
							$.endPageLoading();
						}
						$.ajax.submit(this, "queryDatas",null ,'QueryListPart',null,null);
					},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			    });
	    }
	}
	//如果是2，则调用ajax进行产品配置延期申请
	else if('2' == obj)
	{
		if('1' == dealflag)
	    {
	        MessageBox.alert('提示','该条记录的处理状态为已配置,不需要进行延期申请');
	    }
	    else
	    {
	        $.ajax.submit(this, 'applyForExtension', param, null,
		        function(data){
						if(data.get('ALERT_INFO') != '')
						{
							MessageBox.alert("提示",data.get('ALERT_INFO'));
							$.endPageLoading();
						}
					},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			    });
	    }
	}
	//如果是3，则调用ajax进行产品催办反馈
	else if('3' == obj)
	{
		if('1' == dealflag)
	    {
	        MessageBox.alert('提示','该条记录的处理状态为已配置,不需要进行催办反馈');
	    }
	    else
	    {
	        $.ajax.submit(this, 'feedBackForReminder', param, null,
		        function(data){
						if(data.get('ALERT_INFO') != '')
						{
							MessageBox.alert("提示",data.get('ALERT_INFO'));
							$.endPageLoading();
						}
					},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			    });
	    }
	}
}

function feedbackSucc(flag)
{
     var baseDataset=this.ajaxDataset;
     if('undefined' != baseDataset && null != baseDataset)
     {
	     var baseData=baseDataset.get(0);
	     if('undefined' != baseData && null != baseData)
	     {
		     var succFlag = baseData.get('feedbackFlag');
			 if('1' == flag && '0' == succFlag)
			 {
			    alert('产品配置反馈成功');
			 }
			 else if('2' == flag && '0' == succFlag)
			 {
			    alert('产品配置延期申请成功');
			 }
			  $.ajax.submit(this, "queryDatas",null ,'QueryListPart',true,null);
	     }
     }
}

