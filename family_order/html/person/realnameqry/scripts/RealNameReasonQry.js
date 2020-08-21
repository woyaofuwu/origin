function queryListInfos()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'queryRealNameReasonList', "", 'ResultPart,RealNamePart', function(data){
			$.endPageLoading();
			$("#ResultPart").css("display",""); 
			$("#USER_ID").val(data.get("USER_ID"));
			var qryFlag=data.get("USER_EXISE");
			if(qryFlag=="FALSE"){
				alert("根据号码查询不到用户数据，请重新输入号码查询");
				$("#cond_SERIAL_NUMBER").focus();
			}else{
				var isRealName=data.get("IS_REAL_NAME");
				if(isRealName=="TRUE"){
					$("#ResultPart").css("display","none"); 
					alert("用户已经实名制"); 
				}else{
					var listExist=data.get("REALNAME_LIST");
					if(listExist=="NOT"){
						$("#ResultPart").css("display","none"); 
						alert("用户资料异常或新增非实名用户待次日查询!");
					}
				}
			}
		},function(code, info, detail){ 
			MessageBox.error("错误提示","查询错误！", null, null, info, detail);
			$.endPageLoading(); 
		});
	}
}

function reset(){ 
	$("#cond_SERIAL_NUMBER").val("");
	$("#cond_SERIAL_NUMBER").focus();
}
  