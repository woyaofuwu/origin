function myTabSwitchAction(ptitle, title) {
		if(title=='客户接触')
		{
			 $.ajax.submit('QueryCondPart', 'qryTHCustomerContactInfo', '', 'refreshArea2', function(data){
					redirect(trade_type);
					$.endPageLoading();
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			  });
		}
		if(title=='相关工单')
		{
			 $.ajax.submit('QueryCondPart', 'qryTHRelaTradeInfo', '', 'refreshArea3', function(data){
					redirect(trade_type);
					$.endPageLoading();
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
			  });			
		}
		return true;
}
   
  /**
   *控制页面跳转
   */
   function contorlRedirect(cust_id,user_id,trade_flag,trade_id,trade_type){	
	 var serial_number = $("#SERIAL_NUMBER").val();
     var param = 'TRADE_ID='+trade_id+'&TRADE_FLAG='+trade_flag+'&CUST_ID='+cust_id+'&USER_ID='+user_id+'&TRADE_TYPE_CODE='+trade_type+'&SERIAL_NUMBER='+serial_number;
	 $.ajax.submit('', 'initSubmit', param, 'mytabPart', function(data){
			redirect(trade_type);
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	  });

   }

