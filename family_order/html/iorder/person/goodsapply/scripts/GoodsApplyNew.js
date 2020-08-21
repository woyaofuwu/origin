function refreshPartAtferAuth()
{
	if(!$.validate.verifyField($("#SERIAL_NUMBER"))) return false;
	
	var hsn = $("#SERIAL_NUMBER").val();
	$("#HID_SERIAL_NUM").val(hsn);
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'queryUserScoreGoodsList', "&HID_SERIAL_NUM="+hsn, 'ResultPart', function(){
		$("#getGiftBtn").attr('disabled' ,false);
		$("#msgResendBtn").attr('disabled' ,false);
		sortedTable.adjust();
		$.endPageLoading();
	});
}

  function queryListInfo(hsn)
{
	$.beginPageLoading("刷新数据...");
	$.ajax.submit('', 'queryUserScoreGoodsList', "&HID_SERIAL_NUM="+hsn, 'ResultPart', function(){
		sortedTable.adjust();
		$.endPageLoading();
	});
}

function getGift(){
	 
	var logObj=$("#ResultPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		MessageBox.alert("提示", "请选择要领取的礼品！");
		return false;
	}
	var checkTag=true;
	logObj.each(function(){ 
		var state=$(this).attr("state");//状态为2全部领取完成  3已返销'不能领取
		if(state == "2" ){
			MessageBox.alert("提示", "礼品已经“全部领取完成”，无法领取！");
			checkTag=false;
			return false;
		}else if(state=="3"){
			MessageBox.alert("提示", "礼品“已返销”，无法领取！");
			checkTag=false;
			return false;
		}
		
		var remainNum=$(this).attr("remainNum");//剩余数量=0不允许领取
		if(remainNum=="0"){
			MessageBox.alert("提示", "可领礼品剩余数量为0，无法领取！");
			checkTag=false;
			return false;
		}
		var dateState=$(this).attr("dateState");//过期不允许领取
		if(dateState=="EXP"){
			MessageBox.alert("提示", "该礼品领取期限已过！");
			checkTag=false;
			return false;
		} 
	});
	if(checkTag==true){
		
		showPopup("myPopup","myPopup_item",true);
	}
}

/**
 * 检查验证码
 */
function checkVerificationCode(){
	var quanNum = $("#VERIFICATION_CODE").val();
	if(quanNum==null || quanNum.length==0){
		MessageBox.alert("提示", "请输入验证码！");
		getGift();
		return;
	}else{
		if(quanNum.length!=4){
			MessageBox.alert("提示","输入内容非4位，请输入4位数字验证码！" );
			getGift();
			return;
		}
		if(isNaN(quanNum)){
			MessageBox.alert("提示","输入内容不是数字，请输入4位数字验证码！" );
			getGift();
			return;
		}
		var s=$.DatasetList();
			
		var radioList = $("*[name='RADIO_LIST']");
		var m=$.DataMap(); 
		for(var i=0;i<radioList.length;i++){
			if(radioList[i].checked){
				var quancodes=radioList[i].getAttribute("quancode");
				var getQuans=radioList[i].getAttribute("getQuan");
				if(getQuans!=null && getQuans.indexOf(quanNum)>-1){
					MessageBox.alert("提示","您输入的验证码已经领取过，请重新输入！");
					getGift();
					return;
				}
				if(quancodes.indexOf(quanNum)>-1){
	//				var t1 = radioList[i].getAttribute("tradeId");
					var userId=radioList[i].getAttribute("userId");
					m.put("STATE", radioList[i].getAttribute("state"));
					m.put("TRADE_ID", radioList[i].getAttribute("tradeId"));
					m.put("USER_ID", userId);
					m.put("ACCEPT_MONTH", radioList[i].getAttribute("acceptMon"));
					m.put("RULE_ID", radioList[i].getAttribute("ruleID"));
					m.put("RULE_NAME", radioList[i].getAttribute("ruleName"));
					m.put("REMAIN_NUM", radioList[i].getAttribute("remainNum"));
					m.put("GET_NUM", radioList[i].getAttribute("getNum"));
					m.put("QUANCODE", radioList[i].getAttribute("quancode"));
					m.put("GET_QUANCODE", radioList[i].getAttribute("getQuan"));
					m.put("USER_QUANCODE", quanNum); 
					m.put("SERIAL_NUMBER", radioList[i].getAttribute("serialNum")); 
					s.add(m);
				$.beginPageLoading("领取礼品。。。");
				$.ajax.submit("", "exchangeGoods", "&GOODS_INFOS="+s.toString(), "ResultPart",function(data){
					$.endPageLoading();
					if(data && data.get("RESULT_CODE")=="1"){
						var ruleName=data.get("RULE_NAME");
						MessageBox.success("提示","【"+ruleName+"】领取成功!",'ResultPart'); 
						$.endPageLoading();
						var HID_SERIAL_NUM = $("#SERIAL_NUMBER").val();
						
						hidePopup('myPopup');
						queryListInfo(HID_SERIAL_NUM);
					}
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","礼品领取错误！", null, null, info, detail);
				});	
			}else{
				MessageBox.alert("提示", "您输入的验证码不正确，请重新输入！" );
				getGift();
				return;
			}
	}
}
}}

function resendSMS(){ 
	var logObj=$("#ResultPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		MessageBox.alert("提示", "请选择礼品！");
		return false;
	}
	var checkTag=true;
	logObj.each(function(){ 
		var state=$(this).attr("state");//状态为2全部领取完成  3已返销'不能领取
		if(state == "2" ){
			MessageBox.alert("提示", "礼品已经“全部领取完成”，无须重发短信！");
			checkTag=false;
			return false;
		}else if(state=="3"){
			MessageBox.alert("提示", "礼品“已返销”，无法重发短信！");
			checkTag=false;
			return false;
		}
		
		var remainNum=$(this).attr("remainNum");//剩余数量=0不允许领取
		if(remainNum=="0"){
			MessageBox.alert("提示", "可领礼品剩余数量为0，无须重发短信！");
			checkTag=false;
			return false;
		}
		var dateState=$(this).attr("dateState");//过期不允许领取
		if(dateState=="EXP"){
			MessageBox.alert("提示", "该礼品领取期限已过，无法重发短信！");
			checkTag=false;
			return false;
		} 
	});
	if(checkTag==true){
		logObj.each(function(){
			var param ="&TRADE_ID="+$(this).attr("tradeId")+"&RULE_ID="+$(this).attr("ruleID")+"&SERIAL_NUMBER="+$(this).attr("serialNum");
			popupPage('短信重发','goodsapply.AffirmNumberNew','initHidden',param,'','c_popup c_popup-half c_popup-half-hasBg','','');
		}); 
	}
}

/**
 * 短信重发
 * @returns {Boolean}
 */

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
		$.TipBox.show(document.getElementById('SERIAL_NUMBER_B'), "两次输入的手机号不一致，请重新输入", "red");
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
				MessageBox.success("提示", "短信发送成功，请注意查收。", function(){
					hidePopup(this);
				});
			}else{
				MessageBox.error("提示", data.get("X_RESULTINFO"), "");
			}
		}); 
	}
}