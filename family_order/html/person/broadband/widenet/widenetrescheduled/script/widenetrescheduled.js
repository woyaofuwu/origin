
//查询服务号
function queryWideNetInfo(data)
{	
	var serail_number = $("#SERIAL_NUMBER").val();
	if(serail_number == null || serail_number == ""){
		$.redirect.toPage("order","broadband.widenet.widenetrescheduled.WidenetRescheduled","",'');//重新加载该页面
		return false;
	}
	
	
	$.beginPageLoading("业务资料查询。。。");
	
	$.ajax.submit('ConditionPart', 'queryWideNetInfo', '', 'WideNetPart,updateTimePart', function(returnData){
		$.endPageLoading();
		$("#SUGGEST_DATE").attr("disabled",false);
	},	
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$.endPageLoading();
		alert(error_info);
    });
}

//提交按钮（修改预约时间）
function onTradeSubmit(){
	
	if(!$.validate.verifyAll("ConditionPart")) {
		return false;
	}
	
	$.beginPageLoading("业务提交中。。。");
	
	$.ajax.submit('ConditionPart,updateTimePart', 'onTradeSubmit', '', '', function(returnData){
		
		$.endPageLoading();
		var info = returnData.get(0).get("X_RESULTINFO");
		if (info == null || info == "ok")
		{
			info = "预约施工时间修改成功!";
		}
		
		MessageBox.success("成功提示",info,function(btn){
			//页面跳转
    		$.redirect.toPage("order","broadband.widenet.widenetrescheduled.WidenetRescheduled","",'');
    	});
	},	
	function(error_code,error_info){
		$.endPageLoading();		
		/*$("#WIDE_TYPE").val("");
		$("#STAND_ADDRESS").val("");
		$("#DETAIL_ADDRESS").val("");
		$("#SUGGEST_DATE").val("");
		$("#REASON").val("");
		$("#SUGGEST_DATE").attr("disabled",true);
		$("#CSSUBMIT_BUTTON").attr("disabled", true);*/
		$.redirect.toPage("order","broadband.widenet.widenetrescheduled.WidenetRescheduled","",'');
		alert(error_info);
    });
}

function afterSelSuggestDate(obj)
{
	var suggerDate = $("#SUGGEST_DATE").val();
	
	//判断预约选择的时间是否在工作时间范围内，每天8点-16点为工作时间
	var strHour = suggerDate.substr(11,2);
	var h = Number(strHour);
	if(h < 8 || h >= 16)
	{
		alert('预约施工时间为每天8：00--16：00,请重新选择时间!');
		$("#SUGGEST_DATE").val('');
		return ;
	}
	$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
	
}

