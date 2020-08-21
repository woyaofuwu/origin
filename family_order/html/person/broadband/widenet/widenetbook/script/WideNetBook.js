/**
 * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
 * 新增显示押金金额
 * */
function refreshPartAtferAuth(data)
{  
/*	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'checkFTTHdeposit', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), '', function(rtnData) { 
			if(rtnData!=null&&rtnData.length > 0){
					$.endPageLoading();
					var rtnCode=rtnData.get("DEPOSIT");
					$("#DEPOSIT").val(rtnCode);
				}else{
					$.endPageLoading();
					alert("程序出错，未找到数据！");
					return false; 
				}
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			}); */
	
	$('#validCode').val("");
	//校验通过
	 $("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
}

//提交按钮
function onTradeSubmit()
{
   //验证码
   var validCode=$('#validCode').val();
   
   //校验通过后会把验证码设置到validCode2中
   var validCode2=$('#validCode2').val();
   
   if(validCode == '')
   {
	   alert("验证码不能为空");
	   return false;
   }
   
   if(validCode2 == '')
   {
	   alert("验证码没有校验通过，请点击进行校验！");
	   return false;
   }
   
   if(validCode != validCode2)
   {
	   alert("校验通过的验证码与提交的验证码不一致，请重新进行校验！");
	   return false;
   }
   
   
	var param = '&serial_number='+$("#AUTH_SERIAL_NUMBER").val();//手机号码
	param += '&remark='+$('#remark').val();//备注
	param += '&validCode='+$('#validCode').val();//验证码 
	$.cssubmit.addParam(param);
	return true;
}

function  checkWidenetBook(){
	//验证码
    var validCode=$('#validCode').val();
    var serial_number=$("#AUTH_SERIAL_NUMBER").val();
	$.ajax.submit('', 'checkValidCodeNum', "&validCode="+validCode+"&serial_number="+serial_number, '', function(rtnData) {
		if(rtnData!=null){
			  $.endPageLoading();
			   if(rtnData.get("stauts") ==0){
				   
				   alert("验证码校验通过！");
					//校验通过
//				   $("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
				   
				   $("#validCode2").val(validCode);
					return true;
			   }else{
//				   $("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
				   //MessageBox.error("错误提示", rtnData.get("msg"), $.auth.reflushPage, null, null, null);
				   alert(rtnData.get("msg"));
				   return false;
			   }
			}else{
				$.endPageLoading();
//				$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
				alert("程序出错，未找到数据！");
				return false; 
			}
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
//			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
			return false;
		}); 
	
}


