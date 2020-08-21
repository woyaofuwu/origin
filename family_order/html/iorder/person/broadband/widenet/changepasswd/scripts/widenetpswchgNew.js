
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");

    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit(this, 'loadChildInfo', param, 'wideInfoPart', function(data){
		    $("#SubmitPart").removeClass("e_dis");
		    if("634"==$("#TRADE_TYPE_CODE").val()){	//校园宽带密码变更
			    $("#QueryType").attr("disabled",false);
			    $("#PasswordSetPart").attr("renderBtn",true);
		    }
		    if("607"==$("#TRADE_TYPE_CODE").val()){	//宽带密码变更
		    	$("#QueryType").val("3");	//设置默认为随机密码
		    	$("#op3").addClass("e_segmentOn");
		    }
		    $.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
}

	//page值说明 :1=修改密码 2=新增密码 3=随机密码  4=取消密码 (5=重置密码 HXYD-YZ-REQ)
function showMe(page){
	$.password._clearPassword();
	$("#NEW_PASSWORD2").val("");//清空新密码
    if (page==3) //选择随机密码 则不能点击设置密码按钮
    {
	     $("#PasswdSetBtn").attr("disabled",true);
	     $.cssubmit.disabledSubmitBtn(false);
    }else{//选择修改密码 则能点击设置密码按钮
	     $("#PasswdSetBtn").attr("disabled",false);
    }
    
    var oldPasswd = $.auth.getAuthData().get('USER_INFO').get('USER_PASSWD');
    if( oldPasswd == ""){//没有原密码
    	if(page == 1){   //原密码不存在 只能选择随机 3  
    		MessageBox.alert("提示", "用户密码不存在,请选择随机密码!");
    		$("#op3").attr("selected","selected");
			$("#PasswdSetBtn").attr("disabled",true);
    		return;
         }
     }    
}
//点击“密码设置”按钮组件时执行
function beforeEvent(){
   $.password._clearPassword();
   $("#NEW_PASSWORD2").val("");//清空新密码
   var t_psptId = $.auth.getAuthData().get('CUST_INFO').get('ORIGIN_PSPT_ID');
   var t_serialNumber =  $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
   var t_userId =  $.auth.getAuthData().get('USER_INFO').get('USER_ID');
   $.password.setPasswordAttr({
      psptId:t_psptId, 
      serialNumber:t_serialNumber,
      userId:t_userId     
   });
   return true;
}
//密码设置完成后的回调方法
function afterEvent(data){
	$("#NEW_PASSWORD2").val(data.get("NEW_PASSWORD"));
}

//提交校验
function checkBeforeSubmit()
{  
   var newPasswd=$("#NEW_PASSWORD2").val();
   var queryType=$("#QueryType").val();
   if(queryType=="1"){
	   if(newPasswd==""||newPasswd==null){
		   MessageBox.alert("提示", "用户新密码为空,请设置!");
	      return false;
	   }
   }
	
	var param = "&QUERY_TYPE="+queryType+"&NEW_PASSWORD2="+newPasswd;

	$.cssubmit.addParam(param) 
	return true;	
}
