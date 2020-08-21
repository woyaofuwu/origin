//办理校验
function checkAndSubmit()
{
	
	//查询条件校验
	if(!$.validate.verifyAll("SubmitPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	
	$.ajax.submit('SubmitPart', 'transaction', null, '', function(data)
	{
		$("#SMSBOMB_BUSINESS_TYPE").val("");
		$("#SERIAL_NO").val("");
		$("#ACCESS_NO").val("");
		$("#EXPIRE_DATE").val("");
		MessageBox.success("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//查询
function queryRecord()
{
	
	if($("#cond_SERIAL_NO").val()==""&&$("#cond_ACCESS_NO").val()==""){
		alert("至少填写一个查询条件");
		return false;
	}
	//查询条件校验
	if(!$.validate.verifyAll("QueryPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('QueryPart', 'queryInfo', null, 'ListPart', function(data)
	{
		$.endPageLoading();
		if(data == null || data.getCount()<=0)
		{
			MessageBox.alert("提示","无查询数据",function(btn){
				
			});

		}
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		MessageBox.error("","信息查询失败",null,null,error_info);

  });
}
/*
 * 操作类型
 */
function qryTypeChange()
{
	var selector = $('#SMSBOMB_BUSINESS_TYPE').val();
	if(selector=='2'){
		 $("#endtime").css('display','none');
			$("#EXPIRE_DATE").val("");
			$("#EXPIRE_DATE").attr("nullable","yes");
	}else if(selector=='1'){
		$("#EXPIRE_DATE").attr("nullable","yes");
		 $("#endtime").css('display','');
	}else{
		 $("#endtime").css('display','');
		 $("#EXPIRE_DATE").attr("nullable","no");
	}
}
/*
 * 取消按钮置空
 */
function canCel(){
	$("#SMSBOMB_BUSINESS_TYPE").val("");
	$("#SERIAL_NO").val("");
	$("#ACCESS_NO").val("");
	$("#EXPIRE_DATE").val("");
	
}
