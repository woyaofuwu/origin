function refreshPartAtferAuth(data)
{
	$.beginPageLoading("业务加载中。。。");
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(), 'ECardGprsBindinfo,part2', function(){
		$("#SubmitPart").removeClass("e_dis");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$("#CSRESET_BUTTON").click();
		$.endPageLoading();
		alert(error_info);
    });
}

function checkenumberEcard()
{
	$.tradeCheck.setListener('checkPhoneEcard');
	var eNumber = $("#cond_ENUMBER").val();
	if(eNumber == null || eNumber=="")
	{
		alert("请输入随e行手机号！");
		return false;
	}
	var checkFlag =  $.verifylib.checkMbphone(eNumber);
	if(checkFlag == false){
		alert("随e行号码格式不正确！");
		return false;
	}
	$.beginPageLoading("正在校验数据...");
	
	 $.ajax.submit('', 'checkEcardPhone', '&SERIAL_NUMBER='+eNumber, '', function(data){
	 	
	 	if(data.get("TIPS_TYPE")==1){
	 		MessageBox.confirm("提示信息",data.get("TIPS_INFO"),function(btn){
			if(btn=="ok"){
				 $.ajax.submit('', 'checkPhoneEcard', '&SERIAL_NUMBER='+eNumber, 'MultiSNOneCardPart2', function(){
				    $( "#checkEnumberFlag" ).val("1");
				       $.endPageLoading();
					},
					function(error_code,error_info){
						$.endPageLoading();
						alert(error_info);
				    }); 
			}
		 });
	 	}else{
	 		 $.ajax.submit('', 'checkPhoneEcard', '&SERIAL_NUMBER='+eNumber, 'MultiSNOneCardPart2', function(){
				    $( "#checkEnumberFlag" ).val("1");
				       $.endPageLoading();
					},
					function(error_code,error_info){
						$.endPageLoading();
						alert(error_info);
				    }); 
	 	}
	    $.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });  
	
     
}

function submitDepts(obj)
{
	if (!verifyAll('area')) return false;	
	var eNumber = $("#cond_ENUMBER").val();
	if(eNumber==null || eNumber=="")
	{
		//alert("随e行手机号不能为空！");
		return false;
	}
	return true;
} 
  
function clickOK()  
{		
	
	 if($( "#checkEnumberFlag" ).val() != 1){
	 	alert( "请校验随E行号码！" );
	 	return false;
	 }
	if(  $( "#cond_ECUST_NAME" ).val() == "" )
	{
		alert( "随e行校验未通过" );
		$("#cond_ENUMBER").focus();
		return false;
	}
	if(  $( "#cond_ENUMBER" ).val() == "" )
	{
		alert( "请输入随e行手机号!" );
		$("#cond_ENUMBER").focus();
		return false;
	}
	return true;
}
        