//播记录查询
function queryRecord() 
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//播记录查询
	$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function queryAbnormal()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//播记录查询
	$.ajax.submit('QueryRecordPart,recordNav', 'queryAbnormal', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//办理校验
function checkAndSubmit()
{
	//查询条件校验
	if(!$.validate.verifyAll("blackListProcessPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('blackListProcessPart', 'submitProcess', null, 'blackListProcessPart', function(data)
	{
		
		if(data.get("RESULT_CODE")!='1'){
			MessageBox.alert("提示","操作成功！",function(btn){
					if(btn=="ok")
					{
						window.location.href = window.location.href;
					}
				});
		}else{
			MessageBox.error("提示","操作失败！",null,null,data.get("RESULT_INFO")==null?"":data.get("RESULT_INFO"));
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//受理类型动态刷新
function changeOperaType()
{
		//查询条件校验
	var serialNum = $('#cond_PHONE_NUM').val();
	
	if(serialNum == "")
	{
		alert("手机号码不能为空！");
		window.location.href = window.location.href;
		return false;
	}
	$.beginPageLoading("正在加载数据...");
	$.ajax.submit('QueryRecordPart', 'operaType', null, 'QueryRecordPart',function()
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//轨迹查询
function queryAction() 
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//轨迹查询
	$.ajax.submit('QueryRecordPart,recordNav,TradeIdPart', 'queryBaseBehavior', null, 'QueryListPart,recordNav,HiddenPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

/*
 * 特殊校验  电子邮件和手机号码同时校验
 */
function specCheck(){
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	var paraCode1=$("#cond_PARA_CODE1").val();
	if($.verifylib.checkEmail(paraCode1)||$.verifylib.checkMbphone(paraCode1)){
		//查询条件校验
		$.beginPageLoading("正在查询数据...");
		//播记录查询
		$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
		{
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });
	}else {
		return false;
	}
}
/*
 * 特殊校验 电子邮箱
 */
function specSubmit(){
	if(!$.validate.verifyAll("blackListProcessPart"))
	{
		return false;
	}
	var paraCode1=$("#cond_SUBMIT_MODE1").val();
	if($.verifylib.checkEmail(paraCode1)||$.verifylib.checkMbphone(paraCode1)){
		//查询条件校验
		$.beginPageLoading("业务受理中...");
		
		$.ajax.submit('blackListProcessPart', 'submitProcess', null, 'blackListProcessPart', function(data)
		{
			if(data.get("RESULT_CODE")!='1'){
				MessageBox.alert("提示","操作成功！",function(btn){
						if(btn=="ok")
						{
							window.location.href = window.location.href;
						}
					},null,data.get("RESULT_INFO")==null?"":data.get("RESULT_INFO"));
			}else{
				MessageBox.error("提示","操作失败！",null,null,data.get("RESULT_INFO")==null?"":data.get("RESULT_INFO"));
			}
			
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });
	}else {
		return false;
		
	}
}

function checkChanged(obj){
	if(0==getCheckedBoxNum('saleActives_checked')){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
	}
	else if(1==getCheckedBoxNum('saleActives_checked')){
		$("#CSSUBMIT_BUTTON").attr("disabled", '');
		$("#CSSUBMIT_BUTTON").attr("class", 'e_button-page-ok');
		$("#SubmitPart").attr("class", " ");
		$("#cond_SUBMIT_MODE2").val(obj.val());
	}
	else{
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		alert("温馨提示：目前只支持一次同步退订一个！");
	}
}

function checkDate(month){
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	var startDate=$("#cond_START_DATE").val();
	var endDate=$("#cond_END_DATE").val();
	var start=new Date(startDate);
	var end=new Date(endDate);
	start.setMonth(start.getMonth()+month);
	if(start<end){
		alert("时间跨度不能超过"+month+"个月！");
		return false;
	}else{
		$.beginPageLoading("正在查询数据...");
		//播记录查询
		$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
		{
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });
	}
}
