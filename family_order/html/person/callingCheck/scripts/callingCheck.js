//提交按钮
function onTradeSubmit()
{
   
   var check_number1=$('#CHECK_NUMBER1').val();
   if(check_number1 == ''){
	   alert("核验主叫号码1 :不能为空");
	   return false; 
   }else{
	   if(check_number1.length != 11 ||!$.verifylib.checkMbphone(check_number1)){
		   alert("核验主叫号码1 :手机号码格式不对");
		   return false;
	   }
   }
   
   var check_number2=$('#CHECK_NUMBER2').val();
   if(check_number2 != ''){
	   if(check_number2.length != 11 ||!$.verifylib.checkMbphone(check_number2)){
		   alert("核验主叫号码2 :手机号码格式不对");
		   return false;
	   }
   }
   
   var check_number3=$('#CHECK_NUMBER3').val();
   if(check_number3 != ''){
	   if(check_number3.length != 11 ||!$.verifylib.checkMbphone(check_number3)){
		   alert("核验主叫号码3:手机号码格式不对");
		   return false;
	   }
   }
   
   if(fun_is_same(check_number1,check_number2,check_number3)){
	   //存在相同的
	   $.showErrorMessage("核验主叫号码存在相同的");
	   return false;
   }
   
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();//手机号码
	$.beginPageLoading("业务处理中..");
    $.ajax.submit('AuthPart,callingCheckInfoPart', 'onTradeSubmit', param, null, function(data){
		$.endPageLoading();
		var checkFlag=data.get("checkFlag");
	   if(checkFlag == '0'){
		   $.showSucMessage("通过");
		   //resetPage('AuthPart,UCAViewPart,callingCheckInfoPart');
		   $.cssubmit.disabledButton(false,$("#CSSUBMIT_BUTTON"));
	   }else if(checkFlag == '3'){
		   //单日核验已经超过三次.
		   $.showErrorMessage(data.get("CHECK_RESULT"));
	   }else{
		   $.showErrorMessage("不通过");
	   }
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
		$.cssubmit.disabledButton(true,$("#CSSUBMIT_BUTTON"));
   });
}
/**
 * 初始化页面参数
 * */
function resetPage(jwcidMark){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','',jwcidMark,function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function refreshPartAtferAuthEmergency(data)
{
	 var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userId = data.get("USER_INFO").get("USER_ID");
	 var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	 //给页面的userId赋值
	 $("#USER_ID").val(userId);
}

/**
 * 
 * 移动去掉右边空格
 * @param id
 */
function fun_rtrim(id){ //删除右边的空格
	var str=$("#"+id).val();
	if(str != ''){
		$("#"+id).val(str.replace(/(\s*$)/g,""));
	}
}

/**
 * 判断是否有相同存在
 * @param check_number1
 * @param check_number2
 * @param check_number3
 * @returns {Boolean}
 */
function  fun_is_same(check_number1,check_number2,check_number3){
	//
	if(check_number2 != ''){
		if(check_number1 == check_number2){
			//存在相同代码
			return true;
		}
	}
	if(check_number3 != ''){
		if(check_number1 == check_number3){
			//存在相同代码
			return true;
		}
	}
	if(check_number2 != ''&&check_number3 != ''){
		if(check_number2 == check_number3){
			//存在相同代码
			return true;
		}
	}
	return false;
}


