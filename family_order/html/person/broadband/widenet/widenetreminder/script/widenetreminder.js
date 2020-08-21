function queryTradeList(data)
{
	$.ajax.submit('ConditionPart', 'queryTradeList', '', 'QueryListPart', function(returnData){
		
	},	
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$.endPageLoading();
		alert(error_info);
    });
}


/*业务提交*/
function onTradeSubmit()
{	
	var checkedDatas = new Wade.DatasetList();//拼提交到后台的副设备记录
	var isChecked = false;
	
	$("input[name='TRADE_ID']:checkbox:checked").each(function(){
	   if(this.checked) {
		   //拼提交到后台的副设备记录		   
		   var data =  new Wade.DataMap();
		   data.put("TRADE_ID", $(this).val());
		   data.put("SERIAL_NUMBER", $(this).attr("serial_number"));
		   checkedDatas.add(data);
		   
		   isChecked = true;
	   }
	});
	
	if(!isChecked) 
	{
		alert("请先选择订单后再提交！");
		return false;
	}

	//前台提示
	if(!confirm( "请确认是否进行宽带业务催单操作？" )) {
		return false;
	}
	var param = "&TRADE_LIST=" + checkedDatas.toString() ;

	$.beginPageLoading("业务提交中。。。");
	
	$.ajax.submit(this, 'submitTrade', param, '',
			function(data) 
			{
				$.endPageLoading();
				
		    	MessageBox.success("成功提示","宽带催单成功!",function(btn){
		    		$.redirect.toPage("order","broadband.widenet.WidenetReminder","",'');
		    	});
				
			}, function(error_code, error_info)
			{
				$.endPageLoading();
				$.MessageBox.error(error_code, error_info);
			});

	return true;
}