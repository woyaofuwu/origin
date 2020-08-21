 
function myTabSwitchAction(ptitle, title) {

		return true;
}
  
  /**
   * 监听是否启用时间查询条件
   */
   function timeCheck(){
	var objCheck = $('#TIME_CHECK')[0].checked;
	if(objCheck)
	{
		$("#START_DATE").attr("disabled", false);
		$("#END_DATE").attr("disabled", false);
	}else
	{
		$("#START_DATE").attr("disabled", true);
		$("#END_DATE").attr("disabled", true);
	}
   }
  /**
   * 控制查询预约复选框选定时禁用其他所有条件
   * @param action 动作类型(init/check)
   */
  function bookCheck(){
		//如果选定，则将临时字段(正式字段_T)的值赋给正式字段
		var is_check = $('#BOOK_CHECK')[0].checked;
			if(is_check){
				$('#TIME_CHECK').attr('checked',false);
				$("#TRADE_TYPE_CODE").val('');
				$("#TIME_CHECK").attr("disabled", true);
				$("#TRADE_TYPE_CODE").attr("disabled", true);
				$("#START_DATE").attr("disabled", true);
				$("#END_DATE").attr("disabled", true);
			}else{
				$('#TIME_CHECK').attr('checked',true);
				$("#TIME_CHECK").attr("disabled", false);
				$("#TRADE_TYPE_CODE").attr("disabled", false);
				$("#START_DATE").attr("disabled", false);
				$("#END_DATE").attr("disabled", false);
			}
		
	 }
	 
	 
//业务历史综合查询
  function thQuery() {
	  
	//查询条件校验
//		if(!$.validate.verifyAll("QueryCondPart")) {
//			return false;
//		}
		$.beginPageLoading("数据查询中..");
		$.ajax.submit('QueryCondPart,QueryCondPart1,ThInfonav', 'queryInfo', null, 'QueryListPart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
  }
  
    function redirect(trade_type)
    {
    		 
	   if(trade_type == '110' || trade_type == '111' || trade_type == '112')
	   {
	   	     mytab.switchTo("产品变化");
	   }else{
	  		 mytab.switchTo("受理信息");
	  }
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

    function popupPagePlat(obj)
    {
    	var param = 'SERVICE_ID='+$(obj).attr('service_id')+'&TRADE_ID='+$(obj).attr('trade_id');
	 	$.ajax.submit('commInfo', 'queryPlatAttrInfo', param, 'popupPart,popupResult', function(data){
			$('#popupPart').css('display','');
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    }); 
    }
