function queryIbTradeForm(){
	if(!$.validate.verifyAll("queryInfoPart")){
		return false;
	}
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('queryInfoPart', 'queryIbTradeInfo', null, 'IbTradeInfoPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });	
}
function origdomaindetail(data){
	var td = $.table.get("ibTradeTable").getRowData("REQTIME",1); //获取第一行的REQTIME值
	var jsondata = eval('('+td+')');//转json对象
	var strreqdate = jsondata.REQTIME.replace(/-/g,"/");//正则表达式替换-为/；
	var reqdate =  new Date(strreqdate); //字符串转成时间
	var nowdate = new Date(); //获取当前时间
	var timeapart = nowdate.getTime() - reqdate.getTime();
	var dayapart = Math.floor(timeapart/(24*3600*1000)); //计算相差天数
	if(dayapart > 13)
	{
		alert("交易流水对应的请求时间距离今天的日期只能在14天内，超过不能查看其对应的日志！")
	}
	else
	{		
		$.ajax.submit('queryInfoPart', 'queryIbTradelogInfo', 'TRANSIDO='+data, 'logInfoPart', function(data){
			if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
			else
			{
				popupDiv('logpopup',800,'日志库');
			}
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });	
	}	
}