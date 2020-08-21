function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'MultiSNOneCardHiddenPart,MultiSNOneCardPart2', function(){
		$("#OPERATION_TYPE").attr("disabled", false);
		$("#DEPUTY_LIST").attr("disabled", false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function resetvalue(obj){
	
	if($("#OPERATION_TYPE").val() =="01"){
		$("#part3").attr("style","display:none");
	}else{
		$("#part3").attr("style","display:block");
	}

 }	
	
function checkNumber(){
        var SERIAL_NUMBER_C= $("#AUTH_SERIAL_NUMBER").val();
        var ser_number=$("#SERIAL_NUMBER_O").val();
        alert(SERIAL_NUMBER_C+"  "+ser_number);
        if(SERIAL_NUMBER_C!=ser_number){
        	$.ajax.submit('MultiSNOneCardPart3', 'checkNumber', '&NEW_SERIAL_NUMBER='+ser_number, 'userInfoPart_newSim', function(){
        		$("#OPERATION_TYPE").attr("disabled", false);
        		$("#DEPUTY_LIST").attr("disabled", false);
        	},
        	function(error_code,error_info){
        		$.endPageLoading();
        		alert(error_info);
            });
		    return true;

        }else{
	         alert("新号码和原号码不能相同!");
        	 return false;
        }
  }
  
  function checkInfo(){
  	 var data=this.ajaxDataset;
   if(data && data.get(0) != null){	
  	    var resultinfo= data.get(0).get("RESULT_CODE");
        var info= data.get(0).get("RESULT_INFO");
    	if(resultinfo=="0"){
		  alert('错误提示!'+info);
		  return false;    	
      }
 }	
}
	
		