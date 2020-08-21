function refreshPartAtferAuth(data)
{	
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	param += '&DEAL_FLAG=1' ;
	$.ajax.submit(null, 'queryLotteryInfo', param, 'lotteryInfoPart',loadInfoSucc,loadInfoError);
	
}
function loadInfoSucc(ajaxData)
{
	$("#qryBTN").attr("disabled",null);
	$.cssubmit.disabledSubmitBtn(false);
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function brushLotteryPart(){
	
	var month = $("#MONTH").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(month==null || month=="")
	{
		alert("请输入账期！");
		return false;
	}
	if(serialNumber==null || serialNumber=="")
	{
		alert("请输入手机号码！");
		return false;
	}
	$("#PRIZE_TYPE_CODE").val("");
	$("#ACCEPT_DATE").val("");
	$("#EXEC_FLAG").val("");
	$("#EXEC_TIME").val("");
	$("#REVC1").val("");
	
	$.cssubmit.disabledSubmitBtn(true);
	$("#moneyPart").css("display","none");
	
	$.beginPageLoading("中奖信息查询...");
	var param = '&MONTH='+month+'&SERIAL_NUMBER='+serialNumber;
	$.ajax.submit(null, 'queryLotteryInfo', param, 'lotteryInfoPart',
	function(){
		$.endPageLoading();
		$("#qryBTN").attr("disabled",null);
		$.cssubmit.disabledSubmitBtn(false);
	},
	function(code,info){
		$.endPageLoading();
		$.showErrorMessage("错误",info);
	});
}

function onTradeSubmit()
{
	
	var MONTH = $("#MONTH").val();
	var REMARK = $("#REMARK").val();
	if(MONTH==null || MONTH=="")
	{
		alert("请输入账期！");
		return false;
	}
	if(REMARK==null || REMARK=="")
	{
		alert("请输入备注！");
		return false;
	}
	
	var param = '&MONTH='+MONTH;
	param += '&REMARK='+REMARK;
	param += '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	
	$.cssubmit.addParam(param);
	return true;
}



//有奖查询活动部分
function queryLotteryInfo()
{
	var month = $('#cond_MONTH').val();
	var serialNumber = $('#cond_SERIAL_NUMBER').val();
	var prize = $('#cond_PRIZE_TYPE_CODE').val();
	if(month==null || month=="")
	{
		alert("请输入账期！");
		return false;
	}
	
	if(serialNumber == "" && prize == "") {
		alert("用户号码或者获奖情况必须选择一个！");
		return false;
	}

     $.beginPageLoading("数据加载中！");
	    $.ajax.submit('QueryCondPart,navt', 'queryLotteryInfo', '', 'QueryListPart', function() {
	    	$.endPageLoading();
	    }, function(error_code, error_info) {
	        $.endPageLoading();
	        alert(error_info);
	    });
}

