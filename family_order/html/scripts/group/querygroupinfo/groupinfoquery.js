 var choose = null;
 /** 
  * add by lixiuyu 2009-8-13
 */
 function GroupPRBTQuery() {  
	var snA=$('#cond_SERIAL_NUMBER_B').val();
	var snB=$('#cond_SERIAL_NUMBER').val();
	if (snA=='' && snB==''){
		alert('集团服务号和手机号码不能同时为空!');
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart','queryInfos', '','refreshtable,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }    
  
 function checkPersonalbookQueryType(){ 
 	var queryType = $('#cond_QUERY_TYPE').val(); 
 	if(queryType == '0' || queryType == '1')
 		 $('#GroupPart').css("display","none");
 	if(queryType == '2')
 		 $('#GroupPart').css("display","block");

 }
 
 function groupPersonBookQuery() {
	if(!$.validate.verifyAll('QueryCondPart')) return false;
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart','queryPersonBookInfo', '','refreshtable,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }  
 
 function groupProductInfoQuery() {
	if(!$.validate.verifyAll('QueryCondPart')) return false;
	if($('#POP_cond_GROUP_ID').val()==""){
		alert('集团客户编码不能为空！');
		return false;
	}
	$('#RefreshPart').html('');
	$('#RefreshPart1').html(''); 
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart','queryInfos', '','RefreshPart,RefreshPart1', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }  
 
 function groupVPMNQuery() {
		if(!$.validate.verifyAll('QueryCondPart')) return false;
		$('#refreshtable').html('');
		$.beginPageLoading();
		$.ajax.submit('QueryCondPart','queryInfos', '','refreshtable,refreshHintBar', function(data){
			$.endPageLoading(); 
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	 }  
 function RemoveSixVpmnMebQuery() {
		if(!$.validate.verifyAll('QueryCondPart')) return false;
		$('#refreshtable').html('');
		$.beginPageLoading();
		$.ajax.submit('QueryCondPart','queryInfos', '','refreshtable,refreshHintBar', function(data){
			$.endPageLoading(); 
			initParentVpmnQuery();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	 }  
 /**
  * 
  * add by jch 2009.8.3
 */
function GroupProductQueryO() {  
	choose=$('#cond_QueryMode').val(); 
	if (choose=='0'){ 
		$('#QueryTypeOne').css('display','');
		$('#QueryTypeTwo').css('display','none');
		$('#QueryTypeThree').css('display','none'); 
		$('#cond_BIZ_CODE').attr('nullable','no'); 
		$('#cond_BIZ_ATTR').attr('nullable','yes');
		$('#cond_BIZ_ATTR').val(''); 
		$('#cond_SERIAL_NUMBER').attr('nullable','yes');
		$('#cond_SERIAL_NUMBER').val(''); 
	}else if (choose=='1'){ 
		$('#QueryTypeOne').css('display','none');
		$('#QueryTypeTwo').css('display','block');
		$('#QueryTypeThree').css('display','none'); 
		$('#cond_BIZ_CODE').attr('nullable','yes');
		$('#cond_BIZ_CODE').val(''); 
		$('#cond_BIZ_ATTR').attr('nullable','no'); 
		$('#cond_SERIAL_NUMBER').attr('nullable','yes');
		$('#cond_SERIAL_NUMBER').val(''); 
	}else if (choose=='2'){  
		$('#QueryTypeOne').css('display','none');
		$('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display',''); 
	   $('#cond_BIZ_CODE').attr('nullable','yes');
	   $('#cond_BIZ_CODE').val(''); 
	   $('#cond_BIZ_ATTR').attr('nullable','yes');
	   $('#cond_BIZ_ATTR').val(''); 
 	   $('#cond_SERIAL_NUMBER').attr('nullable','no'); 
	}
 }            
 /** 
  * add by wusf 2009.8.3
 */
 function changeQueryType() {
	choose=$('#QueryType').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	}
 }
 
  /** 
  * add by wusf 2009-9-9
 */
 function changeNetQueryType() {
	choose=$('#QueryType').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#cond_GROUP_ID').val('');
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#cond_PRODUCT_ID').val('');
	}
 }
 /**  
  * add by lixiuyu 2009-8-12
 */
 function IPLaterPayQuery() {
	var choose=$('#cond_QueryMode').val();
		
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#cond_SERIAL_NUMBER_A').attr('nullable','no');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER').val('');
	}else if (choose=='1'){ 
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#cond_SERIAL_NUMBER_A').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','no');
 	   $('#cond_SERIAL_NUMBER_A').val('');
	}
 }  
 
 /** 
  * add by lixiuyu 2009-8-13
 */
 function GroupLBSQuery() {
	var snA=$('#cond_SERIAL_NUMBER_B').val();
	var snB=$('#cond_SERIAL_NUMBER').val();
	if (snA=='' && snB==''){

		alert('\u4EA7\u54C1\u7F16\u7801\u548C\u624B\u673A\u53F7\u7801\u4E0D\u80FD\u540C\u65F6\u4E3A\u7A7A\u0021');
		return false;
	}
 } 
 

 /** 
  * add by lixiuyu 2009-8-12
 */
 function changeQueryTypeVPMN() {
	var choose=$('#cond_QueryModeVPMN').val();
	if (choose=='0'){     
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#cond_VPN_NO').attr('nullable','no');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER').val('');
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#cond_VPN_NO').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','no');
	   $('#cond_VPN_NO').val('');
	} 
 }   
 
 
  function clickcheck()
{
     var effectNow =$('#effectNow').val();
     if(effectNow=='on'){
       getElement(flag).value=1;
     }
} 
  /** 
  * add by wusf 2009.8.3
 */
 function changeThreeQueryType() {
	choose=$('#QueryType').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');



	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	}else if (choose=='2'){ 
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	}
 }      



    /**
  * 
  * add by jch 2009.8.3
 */
 function GroupBBossTradeQuery() {
 	
	choose=$('#cond_QueryMode').val();
    //alert(choose);
   
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
			 
	   $('#cond_GROUP_ID').attr('nullable','no');
 	   
	   $('#cond_CUST_NAME').attr('nullable','yes');
 	   $('#cond_CUST_NAME').val('');

 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');
 	   
 	   
 	   
 	   
	
	}else if (choose=='1'){ 
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
      
	   $('#cond_GROUP_ID').attr('nullable','yes');
 	   $('#cond_GROUP_ID').val('');
 	   
	   $('#cond_CUST_NAME').attr('nullable','no');
 	
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');

	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');

	    $('#cond_GROUP_ID').attr('nullable','yes');
 	    $('#cond_GROUP_ID').val('');
 	   
	   $('#cond_CUST_NAME').attr('nullable','yes');
       $('#cond_CUST_NAME').val('');

 	
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','no');
 	   	
	}
 }      
 
  /**
  * 
  * add by jch 2009.8.3
 */
 function GroupBBossBizMebQuery() {
	choose=$('#cond_QueryMode').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	
	   $('#cond_SERIAL_NUMBER').attr('nullable','no');
	     
	   $('#cond_GROUP_ID').attr('nullable','yes');
 	   $('#cond_GROUP_ID').val('');
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');
 	   
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   
	   
	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	   $('#cond_SERIAL_NUMBER').val('');
	     
	   $('#cond_GROUP_ID').attr('nullable','no');
 	 
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');
	   

	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	   
	    
	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	   $('#cond_SERIAL_NUMBER').val('');
	     
	   $('#cond_GROUP_ID').attr('nullable','yes');
	    $('#cond_GROUP_ID').val('');
 	 
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','no');
 	  
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');
	 
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	   
	   
	    $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	    $('#cond_SERIAL_NUMBER').val('');
	     
	    $('#cond_GROUP_ID').attr('nullable','yes');
	    $('#cond_GROUP_ID').val('');
 	 
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	  
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','no');
 	 
	 }
 }      
 
  /**
  * 
  * add by jch 2009.8.3
 */
 function GroupBBossBizEcQuery() {
	choose=$('#cond_QueryMode').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   
	   
	    $('#cond_GROUP_ID').attr('nullable','no');
	    
	     $('#cond_CUST_NAME').attr('nullable','yes'); 
	     $('#cond_CUST_NAME').val('');
	     
	     $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes'); 
	     $('#cond_EC_SERIAL_NUMBER').val('');
	     
	     $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes'); 
	     $('#cond_PRODUCT_OFFER_ID').val('');
	   
	   
	
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   
	      $('#cond_GROUP_ID').attr('nullable','yes');
	      $('#cond_GROUP_ID').val('');
	    
	     $('#cond_CUST_NAME').attr('nullable','no'); 
	     
	     
	     $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes'); 
	     $('#cond_EC_SERIAL_NUMBER').val('');
	     
	     $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes'); 
	     $('#cond_PRODUCT_OFFER_ID').val('');

	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	   
	     $('#cond_GROUP_ID').attr('nullable','yes');
	      $('#cond_CUST_NAME').val('');
	    
	     $('#cond_CUST_NAME').attr('nullable','yes');
	     $('#cond_CUST_NAME').val(''); 
	     
	     
	     $('#cond_EC_SERIAL_NUMBER').attr('nullable','no'); 
	     
	     
	     $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes'); 
	     $('#cond_PRODUCT_OFFER_ID').val('');
	 
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	   
	      $('#cond_GROUP_ID').attr('nullable','yes');
	      $('#cond_CUST_NAME').val('');
	    
	     $('#cond_CUST_NAME').attr('nullable','yes');
	     $('#cond_CUST_NAME').val(''); 
	     
	     
	     $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes'); 
	     $('#cond_EC_SERIAL_NUMBER').val('');
	     
	     
	     $('#cond_PRODUCT_OFFER_ID').attr('nullable','no'); 
	  
	 }
 }      
     
     
 /**
  * 
  * add by jch 2009.8.3
 */
 function GroupBbossBizProdDgQuery() {
	choose=$('#cond_QueryMode').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	
	  
	      $('#cond_PRODUCT_OFFER_ID').attr('nullable','no');
	        
	     $('#cond_GROUP_ID').attr('nullable','yes');
	     $('#cond_GROUP_ID').val('');	
	    
	      $('#cond_CUST_NAME').attr('nullable','yes');
	     $('#cond_CUST_NAME').val('');	
	     
	       $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	     $('#cond_SERIAL_NUMBER').val('');	
	
	}else if (choose=='1'){ 
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   
	   
	   
	     $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
	     $('#cond_PRODUCT_OFFER_ID').val('');	
	        
	      $('#cond_GROUP_ID').attr('nullable','no');
	   
	    
	      $('#cond_CUST_NAME').attr('nullable','yes');
	      $('#cond_CUST_NAME').val('');	
	     
	      $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	      $('#cond_SERIAL_NUMBER').val('');	
	     
	     

	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	 
	 
	 	      $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
	        $('#cond_PRODUCT_OFFER_ID').val('');	
	        
	     $('#cond_GROUP_ID').attr('nullable','yes');
	      $('#cond_GROUP_ID').val('');	
	   
	    
	      $('#cond_CUST_NAME').attr('nullable','no');
	     
	     
	       $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	     $('#cond_SERIAL_NUMBER').val('');	
	     
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	    
	      $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
	        $('#cond_PRODUCT_OFFER_ID').val('');	
	        
	     $('#cond_GROUP_ID').attr('nullable','yes');
	      $('#cond_GROUP_ID').val('');	
	   
	    
	      $('#cond_CUST_NAME').attr('nullable','yes');
	      $('#cond_SERIAL_NUMBER').val('');	
	     
	     
	       $('#cond_SERIAL_NUMBER').attr('nullable','no');
	     
	 }
 }      

     
 
 
  /**
  * 
  * add by jch 2009.8.3
 */
 function GroupBBossMebQuery() {
	choose=$('#cond_QueryMode').val();

	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	   
	   $('#cond_SERIAL_NUMBER').attr('nullable','no');
	   
	   
	   $('#cond_GROUP_ID').attr('nullable','yes');
 	   $('#cond_GROUP_ID').val('');
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');	   
 	   
	   
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	   
	    $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	    $('#cond_SERIAL_NUMBER').val('');
	   
	    $('#cond_GROUP_ID').attr('nullable','no');
 	  
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');   
	   
	   
	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	   
	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	   $('#cond_SERIAL_NUMBER').val('');
	   
	    $('#cond_GROUP_ID').attr('nullable','yes');
 	   $('#cond_GROUP_ID').val('');
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','no');
 	  
 	   
 	   
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');
	   
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	   $('#QueryTypeFive').css('display','none');
	   
	    $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	    $('#cond_SERIAL_NUMBER').val('');
	   
	    $('#cond_GROUP_ID').attr('nullable','yes');
 	    $('#cond_GROUP_ID').val('');
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','no');
 	   
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_OFFER_ID').val('');

	 }else
		 if(choose=='4')
	 {
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','');
	   
	    $('#cond_SERIAL_NUMBER').attr('nullable','yes');
	    $('#cond_SERIAL_NUMBER').val('');
	   
	    $('#cond_GROUP_ID').attr('nullable','yes');
 	    $('#cond_GROUP_ID').val('');
 	   
 	   $('#cond_EC_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_EC_SERIAL_NUMBER').val('');
 	   
 	   
 	   $('#cond_PRODUCT_ORDER_ID').attr('nullable','yes');
 	   $('#cond_PRODUCT_ORDER_ID').val('');
 	   
 	   
 	   $('#cond_PRODUCT_OFFER_ID').attr('nullable','no');
 	   
	 }
 }      
 
 

  /** 
  * add by jch 2009.8.3
 */
 function changeFiveQueryType() {
	choose=$('#QueryType').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','none');
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	   $('#QueryTypeFive').css('display','none');

	 }else
		 if(choose=='4')
	 {
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	   $('#QueryTypeFive').css('display','');
	 }
 }      

 /** 
  * add by jch 2009.8.3
 */
 function changeFourQueryType() {
	choose=$('#QueryType').val();
	if (choose=='0'){ 
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');
	
	}else if (choose=='1'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','none');

	}else if (choose=='2'){  
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','');
	   $('#QueryTypeFour').css('display','none');
	 
	}else if(choose=='3')
	 {
	  $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','none');
	   $('#QueryTypeThree').css('display','none');
	   $('#QueryTypeFour').css('display','');
	 }
 }      

 
 function getColsValue(obj)
{   
    var val = window.event.srcElement.parentElement;
    var USER_ID;
		if(val.tagName=='TR'){
			var enuma = val.childNodes;
			 USER_ID = enuma[6].innerText;
		}
	
	ajaxDirect('group.querygroupinfo.GroupNetworkQuery', 'queryByUserid', '&USER_ID='+USER_ID, 'GroupProductPart',false,setAttrProduct)
	
}
 function setAttrProduct(){
       
       for(a=0;ajaxDataset.getCount()>a ; a++){
        var elementData= this.ajaxDataset.get(a);
        var ATTR_VALUE = elementData.get("ATTR_VALUE");
        var ATTR_LABLE = elementData.get("ATTR_LABLE");
        if(null==ATTR_VALUE && ''==ATTR_VALUE && null==ATTR_VALUE && ''==ATTR_VALUE){
          return true;
        }else{
        if(a==0){
   	    $('#DBEdit1').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit1').value=ATTR_VALUE;
   	    }else if(a==1){
   	    $('#DBEdit2').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit2').value=ATTR_VALUE;}
   	    else if(a==2){
   	    $('#DBEdit3').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit3').value=ATTR_VALUE;
   	    }
   	    else if(a==3){
   	    $('#DBEdit4').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit4').value=ATTR_VALUE;}
   	    else if(a==4){
   	    $('#DBEdit5').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit5').value=ATTR_VALUE;}
   	    else if(a==5){
   	    $('#DBEdit6').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit6').value=ATTR_VALUE;}
   	     else if(a==6){  	   
   	    $('#DBEdit7').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit7').value=ATTR_VALUE;}
   	     else if(a==7){  	   
   	    $('#DBEdit8').innerHTML=ATTR_LABLE+': ';
   	    $('#cond_DBEdit8').value=ATTR_VALUE;}
   	   }
   	    
       } return true;
}
  
function printtable(){
		Wade.print.printRaw("printTable");
}
  
 /**
  * changeVpmnTypeQuery
  * add by meig 2009.8.17
 */
 function changeVpmnTypeQuery() {
	choose=$('#cond_QueryType').val();
	if (choose=='0'){
	   $('#QueryTypeOne').css('display','');
	   $('#QueryTypeTwo').css('display','none');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','no');
	   $('#cond_SERIAL_NUMBER_A').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER_A').val('');
	}else if (choose=='1'){
	   $('#QueryTypeOne').css('display','none');
	   $('#QueryTypeTwo').css('display','');
	   $('#cond_SERIAL_NUMBER_A').attr('nullable','no');
 	   $('#cond_SERIAL_NUMBER').attr('nullable','yes');
 	   $('#cond_SERIAL_NUMBER').val('');
	}
}

function initParentVpmnQuery(){
	$('#discntPart').css('display','none');
}

function queryDiscntInfo(obj){
	$('#discntPart').css('display','');
	ajaxDirect(this,'queryDiscntInfos', '&USER_ID='+obj, 'discntPart',null,null);
	
}


function getGroupProductinfo(obj){
	parent.getElement("cond_GROUP_ID").value = obj.getAttribute('groupId');
	try {
	   parent.getElement("cond_SERIAL_NUMBER").value = obj.getAttribute('serialNumber');
	} catch(e) {
	}
	cancel(true);
}




function aftercheckGroupInfo(){
var flag = this.ajaxDataset.get(0, "ISEXIST");
var groupid = this.ajaxDataset.get(0, "GROUP_ID");
if(flag == 'EXISTS')
	popupDialog('group.querygroupinfo.QueryGroupProductinfo', 'queryGroupInfo', '&cond_GROUP_ID='+$('#cond_GROUP_ID').value+'&refresh='+true, '集团产品订购信息', '600', '240');
else
	alert('根据集团边编码[' + groupId	+ ']查找集团产品信息不存在!');

}
function exportBeforeAction(domid) { 
//	alert("点击[导出]按钮时执行的JS方法exportBeforeAction,在这里可以用JS动态设置传到后台的参数"); 
	var navbar =$("#"+domid);  
	if($('#dataCount').val()==''||$('#dataCount').val()=='0'){
		alert('没数据！');
		return false;
	}
	if($('#cond_QUERY_TYPE').val()=='0'){
		return true;
	}
//	if(confirm('数据量大会导致导出失败，是否只导出当前页数据？')) 
//	{ 
//		$.Export.get("exportFile").setParams("&isCurPage=true"); 
//		return true;
//	} 
//	else 
//	{ 
//		alert('取消导出！');
//		return false; 
//	} 
	
	return true; 
}
function exportBeforeAction2(domid) {  
	var navbar =$("#"+domid);  
	if($('#dataCount').val()==''||$('#dataCount').val()=='0'){
		alert('没数据！');
		return false;
	} 
//	if(confirm('数据量大会导致导出失败，是否只导出当前页数据？')) 
//	{ 
//		$.Export.get("exportFile").setParams("&isCurPage=true"); 
//		return true;
//	} 
//	else 
//	{ 
//		alert('取消导出！');
//		return false; 
//	} 
	
	return true; 
}