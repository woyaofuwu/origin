$(document).ready(function(){
	 $("#OLD_CARD_NO").attr("disabled",true);
	 $("#check_button").attr("disabled",true);
	 $("#cardInfoPart").addClass("e_dis");
});
function refreshPartAtferAuth(data) {
		$("#OLD_CARD_NO").attr("disabled",false);
		$("#check_button").attr("disabled",false);
		$("#cardInfoPart").removeClass("e_dis");
}

//先查询该旧实体卡的状态，如果未锁定或者未邦定就将该卡锁定
function lockEntityCard(obj,flag){
	$.beginPageLoading();
	var oldCardNo = $("#OLD_CARD_NO").val();
	var checkResultCode = $("#CHECK_RESULT");
	if(oldCardNo == "" || oldCardNo == null){
		alert("旧实体卡号不能为空!");
		$.endPageLoading();
		return false;
	}
	if(checkResultCode.val()=="1"&&$("#CHECKED_OLD_CARD_NO").val()==$("#OLD_CARD_NO").val())
	{
		if(flag==1)
			alert("输入的实体卡号码和刚才校验的号码一致！");
		$.endPageLoading();
		return false;
	}
	$.ajax.submit('','checkOldEntityCard','&OLD_ENTITY_CARD_NO='+$("#OLD_CARD_NO").val(),'',
			function(data){
				
				var resultCode = data.get("X_RESULTCODE");
				var rspType = data.get("X_RSPTYPE");
				var rspCode = data.get("X_RSPCODE");
				var cardStatus = data.get("CARDSTATUS");
				var rspDesc = data.get("X_RSPDESC");
				if(rspType=="0" && rspCode=="0000"){
					if(cardStatus=="05"){
						alert("该实体卡已经被锁定，不能再办理锁定操作!");
						$.endPageLoading();
						return false;
					}else if(cardStatus=="04"){
						alert("该实体卡已经绑定了，不能再办理锁定操作!");
						$.endPageLoading();
						return false;
					}else{
						$.ajax.submit('','LockEntityCard','&OLD_CARD_NO='+$("#OLD_CARD_NO").val(),'',
							function(datas){
							var rspType1 = datas.get("X_RSPTYPE");
							var rspCode1 = datas.get("X_RSPCODE");
							if(rspType1=="0" && rspCode1=="0000"){
								$("#CHECK_RESULT").val("1");
								$("#CHECKED_OLD_CARD_NO").val($("#OLD_CARD_NO").val());
								alert("实体卡锁定成功！");
							}
							else{
								var rspDesc1 =datas.get("X_RSPDESC");
								$("#CHECK_RESULT").val("0");
								alert("实体卡锁定失败: "+rspDesc1);
							}
							$.endPageLoading();
						}, function(error_code, error_info) {
							$.endPageLoading();
						});
					}
				}
				else{
					alert("实体卡状态查询失败: "+rspDesc);
					$("#CHECK_RESULT").val("0");
					$.endPageLoading();
				}
	}, function(error_code, error_info) {
		alert(error_info);
		$.endPageLoading();
	});
}
function checkNewEntityCard(){
	
	$.beginPageLoading();
	if($("#CHECK_RESULT").val()!="1"){alert("旧实体卡号未锁定，业务无法继续!");$.endPageLoading();return false;}
	var oldCardNo = $("#OLD_CARD_NO").val();
	var newCardNo = $("#NEW_CARD_NO").val();
	var checkResultCode = $("#CHECK_RESULT");
	if(oldCardNo == "" || oldCardNo == null){
		alert("旧实体卡号不能为空!");
		$.endPageLoading();
		return false;
	}
	if(NEW_CARD_NO == "" || NEW_CARD_NO == null){
		alert("新实体卡号不能为空!");
		$.endPageLoading();
		return false;
	}
	
	$.ajax.submit('','checkNewEntityCard','&OLD_ENTITY_CARD_NO='+$("#OLD_CARD_NO").val()+"&NEW_ENTITY_CARD_NO="+newCardNo,'',
			function(data){
				$("#CHECK_NEW_CARD_RESULT").val("1");
				$.endPageLoading();
			}, 
			function(error_code, error_info) {
				alert(error_info);
				$("#CHECK_NEW_CARD_RESULT").val("0");
				$.endPageLoading();
	});
	
}
function submitTrade(){
	if(!$.validate.verifyAll("cardInfoPart"))return false;
	if($("#CHECK_RESULT").val()!="1"){alert("旧实体卡号未锁定，业务无法继续!");return false;}
	if($("#CHECK_NEW_CARD_RESULT").val()!="1"){alert("新实体卡未通过校验，业务无法继续!");return false;}
	if($("#OLD_CARD_NO").val()==$("#NEW_CARD_NO").val()){
		alert("新实体卡号与旧实体卡号号码相同，业务无法继续");
		return false;
	}
	return true;
}