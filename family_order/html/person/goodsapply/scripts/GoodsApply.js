function refreshPartAtferAuth()
{
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) return false;
	
	var hsn = $("#SERIAL_NUMBER").val();
	$("#HID_SERIAL_NUM").val(hsn);
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'queryUserScoreGoodsList', "&HID_SERIAL_NUM="+hsn, 'ResultPart', function(){
		$.endPageLoading();
	});
}

function tableRowClick(){

}

function queryListInfo(hsn)
{
	$.beginPageLoading("刷新数据...");
	$.ajax.submit('', 'queryUserScoreGoodsList', "&HID_SERIAL_NUM="+hsn, 'ResultPart', function(){
		$.endPageLoading();
	});
}

function getGift(){
	 
	var logObj=$("#ResultPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择要领取的礼品！");
		return false;
	}
	var checkTag=true;
	logObj.each(function(){ 
		var state=$(this).attr("state");//状态为2全部领取完成  3已返销'不能领取
		if(state == "2" ){
			alert("礼品已经“全部领取完成”，无法领取！");
			checkTag=false;
			return false;
		}else if(state=="3"){
			alert("礼品“已返销”，无法领取！");
			checkTag=false;
			return false;
		}
		
		var remainNum=$(this).attr("remainNum");//剩余数量=0不允许领取
		if(remainNum=="0"){
			alert("可领礼品剩余数量为0，无法领取！");
			checkTag=false;
			return false;
		}
		var dateState=$(this).attr("dateState");//过期不允许领取
		if(dateState=="EXP"){
			alert("该礼品领取期限已过！");
			checkTag=false;
			return false;
		} 
	});
	if(checkTag==true){
		MessageBox.prompt("验证","请输入验证码",function(btn, quanNum){
			if(btn==="ok"){
				if(quanNum==null || quanNum.length==0){
					alert('请输入验证码！' );
					getGift();
					return;
				}else{
					if(quanNum.length!=4){
						alert('输入内容非4位，请输入4位数字验证码！' );
						getGift();
						return;
					}
					if(isNaN(quanNum)){
						alert('输入内容不是数字，请输入4位数字验证码！' );
						getGift();
						return;
					}
					var s=$.DatasetList();
					logObj.each(function(){
						
						var quancodes=$(this).attr("quancode");
						var getQuans=$(this).attr("getQuan");
						if(getQuans!=null && getQuans.indexOf(quanNum)>-1){
							alert('您输入的验证码已经领取过，请重新输入！' );
							getGift();
							return;
						}
						if(quancodes.indexOf(quanNum)>-1){
							var userId=$(this).attr("userId");
							var m=$.DataMap(); 
							m.put("STATE", $(this).attr("state"));
							m.put("TRADE_ID", $(this).attr("tradeId"));
							m.put("USER_ID", userId);
							m.put("ACCEPT_MONTH", $(this).attr("acceptMon"));
							m.put("RULE_ID", $(this).attr("ruleID"));
							m.put("RULE_NAME", $(this).attr("ruleName"));
							m.put("REMAIN_NUM", $(this).attr("remainNum"));
							m.put("GET_NUM", $(this).attr("getNum"));
							m.put("QUANCODE", $(this).attr("quancode"));
							m.put("GET_QUANCODE", $(this).attr("getQuan"));
							m.put("USER_QUANCODE", quanNum); 
							m.put("SERIAL_NUMBER", $(this).attr("serialNum")); 
							s.add(m);
							$.beginPageLoading("领取礼品。。。");
							$.ajax.submit("", "exchangeGoods", "&GOODS_INFOS="+s.toString(), "ResultPart",function(data){
								$.endPageLoading();
								if(data && data.get("RESULT_CODE")=="1"){
									var ruleName=data.get("RULE_NAME")
									$.showSucMessage("提示","【"+ruleName+"】领取成功!",'ResultPart'); 
									$.endPageLoading();
									queryListInfo($(this).attr("serialNum"));
								}
							},function(code, info, detail){
								$.endPageLoading();
								MessageBox.error("错误提示","礼品领取错误！", null, null, info, detail);
							});	
						}else{
							alert('您输入的验证码不正确，请重新输入！' );
							getGift();
							return;
						}
					}); 
				}
			}
		}); 	
	}
}

function resendSMS(){ 
	var logObj=$("#ResultPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择要领取的礼品！");
		return false;
	}
	var checkTag=true;
	logObj.each(function(){ 
		var state=$(this).attr("state");//状态为2全部领取完成  3已返销'不能领取
		if(state == "2" ){
			alert("礼品已经“全部领取完成”，无须重发短信！");
			checkTag=false;
			return false;
		}else if(state=="3"){
			alert("礼品“已返销”，无法重发短信！");
			checkTag=false;
			return false;
		}
		
		var remainNum=$(this).attr("remainNum");//剩余数量=0不允许领取
		if(remainNum=="0"){
			alert("可领礼品剩余数量为0，无须重发短信！");
			checkTag=false;
			return false;
		}
		var dateState=$(this).attr("dateState");//过期不允许领取
		if(dateState=="EXP"){
			alert("该礼品领取期限已过，无法重发短信！");
			checkTag=false;
			return false;
		} 
	});
	if(checkTag==true){
		logObj.each(function(){
			var param ="&TRADE_ID="+$(this).attr("tradeId")+"&RULE_ID="+$(this).attr("ruleID")+"&SERIAL_NUMBER="+$(this).attr("serialNum");
			popupPage('goodsapply.AffirmNumber', 'initHidden', param, '', '300', '300');
		}); 
	}
}

function submitResendNO(){
	var checkTag=true; 
	if(!$.validate.verifyAll("SnPart")) {
		checkTag=false;
		return false;
	}
	
	var snA=$("#SERIAL_NUMBER").val();
	var snB1=$("#SERIAL_NUMBER_A").val();
	var snB=$("#SERIAL_NUMBER_B").val();
	if(snB1 != snB){
		alert("两次输入的手机号不一致，请重新输入");
		checkTag=false;
		return false;
	}
	if(checkTag){
		var param="&RULE_ID="+$("#RULE_ID").val()+"&TRADE_ID="+$("#TRADE_ID").val()+"&SERIAL_NUMBER="+snA+"&SERIAL_NUMBER_B="+snB+"&FROM_TYPE=PAGE";
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('', 'onTradeSubmit', param, '', function(data){
			$.endPageLoading();
			var flag=data.get("X_RESULTCODE");
			if(flag=="0"){
				alert("短信发送成功，请注意查收。"); 
			}else{
				alert(data.get("X_RESULTINFO"));
			}
			$.closePopupPage(true,null,null,null,null,true);
		}); 
	}
}