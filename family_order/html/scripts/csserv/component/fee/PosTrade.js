(function($){
	$.extend({posTrade:{
		POS_PAY:"00",						//消费
		POS_CANCEL:"01",					//撤销
		POS_BACK:"02",						//退货
		POS_BALANCE:"03",					//查余额
		POS_SIGNIN:"05",					//签到
		
		init:function(){
			//刷卡消费
			$("#PayBtn").bind("click", $.posTrade.payFee);
			//刷卡撤销
			$("#BackBtn").bind("click", $.posTrade.backFee);
			//查询余额
			$("#QryFeeBtn").bind("click", $.posTrade.queryFee);
			//签到
			$("#SignInBtn").bind("click", $.posTrade.signIn);
			
			//设置刷卡金额
			$("#AMOUNT").bind("blur", $.posTrade.verifyFee);
			
			//提交POS刷卡窗口
			$("#SubmitPosBtn").bind("click", $.posTrade.submitPosPaying);
			//关闭POS刷卡窗口
			$("#CancelPosBtn").bind("click", $.posTrade.cancelPosPaying);
		},
		//加载POS刷卡日志
		loadPosLogs:function(){
			$.beginPageLoading("加载POS日志。。。");
			$.ajax.submit(null, "queryPosLog", "&PRE_TRADE_ID="+$("#PRE_TRADE_ID").val(), "PosListPart", function(data){
				$.endPageLoading();
				//更新已刷卡次数
				$("#POS_COUNT").val(data.get("POS_COUNT"));
				
			},function(code, info, detail){
				$.endPageLoading();
				alert("加载POS日志错误："+info);
			});
		},
		
		//POS刷卡消费
		payFee:function(){
			var posCount=parseInt($("#POS_COUNT").val());
			var posCountLimit=parseInt($("#POS_COUNT_LIMIT").val());
			if(posCountLimit>0 && posCount==posCountLimit){
				alert("一笔业务的刷卡次数不能超过" + posCountLimit + "次!");
				return;
			}
			var amount=$("#AMOUNT").val();
			var allAmount=$("#ALL_AMOUNT").val();
			var curAmount=$("#CUR_AMOUNT").val();
			
			if(!$.isNumeric(amount)){
				alert("刷卡金额不合法！");
				return;
			}
			if(parseFloat(amount)<0){
				alert("刷卡金额必须大于0！");
				return;
			}
			//如果校验通过，则将输入值进行四舍五入
			var fee=Math.round(parseFloat(amount)*100);
			$("#AMOUNT").val(fee/100);
			amount=$("#AMOUNT").val();
			
			if(parseFloat(amount)+parseFloat(curAmount)>parseFloat(allAmount)){
				alert("累计刷卡金额不能大于POS刷卡金额：" + allAmount + "元"); //刷卡金额不能大于有线POS金额
				$("#AMOUNT").val(parseFloat(allAmount)-parseFloat(curAmount));
				return;
			}
			
			var reqStr=$.posTrade.getPosReqStr($.posTrade.POS_PAY, fee);
			if(!reqStr) return ;
			var respStr=$.posTrade.posTrans(reqStr);
			var respData=$.posTrade.parsePosResp(respStr);
			var param = "&TRANS_TYPE=S";	 //S为消费类型
			param +="&USER_ID="+$("#USER_ID").val();
			param +="&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(); 
			param +="&TRADE_ID="+$("#PRE_TRADE_ID").val();
			param +="&S_REQ="+reqStr;
			param +="&S_RESP="+respStr;
			
			//连接POS返回参数
			respData.eachKey(function(key,index,totalcount){
				param += "&"+key+"="+respData.get(key);
			});
			if(respData.get("RESP")=="00"){
				param += "&STATUS=0";			//刷卡成功
				
				$.beginPageLoading("记录POS日志。。。");
				$.ajax.submit(null, "recordPosLog", param, null, function(data){
					$.endPageLoading();
					if (data && data.get("RESULT_CODE") == "0") {
						if (fee != data.get("AMOUNT")) {
							alert("银联POS缴费金额不一致，开始自动撤消...");
							$.fee.cancelFee(respData.get("CERT_NO"), respData.get("AMOUNT"), data.get("POS_TRADE_ID"));
							return;
						}
						alert("该笔银联POS缴费成功！");
						
						$.posTrade.statAmount(amount, 1);
						$.posTrade.loadPosLogs();
						
					} else {
						alert("POS缴费日志记录登记失败,开始自动撤消...");
						$.posTrade.cancelFee(respData.get("CERT_NO"), respData.get("AMOUNT"));
						return;
					}
					
				},function(code, info, detail){
					$.endPageLoading();
					alert("记录银联POS刷卡日志错误："+info+"\n点击确定自动撤销！");
					$.posTrade.cancelFee(respData.get("CERT_NO"), respData.get("AMOUNT"));
				});
			}else{
				param += "&STATUS=1";			//刷卡失败
				if (respStr.getByteLength() != 148) {
					alert("银联pos缴费失败！");
					return;
				}
				alert("银联pos缴费失败，请重试，或者选择其他交易方式！\n错误码：" + respData.get("RESP") + ",错误信息：" + respData.get("RESP_INFO"));
				
				$.ajax.submit(null, "recordPosLog", param, null,function(data){
					$.endPageLoading();
				},function(code, info, detail){
					$.endPageLoading();
				});
			}
		},
		//POS刷卡失败撤销
		cancelFee:function(certNo, amount, oldPosTradeId){
			var reqStr=$.posTrade.getPosReqStr($.posTrade.POS_CANCEL, amount, null, null, certNo);
			if(!reqStr) return ;
			var respStr=$.posTrade.posTrans(reqStr);
			var respData=$.posTrade.parsePosResp(respStr);
			var param = "&TRANS_TYPE=A";	 //A为撤销类型
			param +="&USER_ID="+$("#USER_ID").val();
			param +="&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(); 
			param +="&TRADE_ID="+$("#PRE_TRADE_ID").val();
			param +="&S_REQ="+reqStr;
			param +="&S_RESP="+respStr;
			if(oldPosTradeId){
				param +="&OLD_POS_TRADE_ID="+oldPosTradeId;
			}
			
			//连接POS返回参数
			respData.eachKey(function(key,index,totalcount){
				param += "&"+key+"="+respData.get(key);
			});
			if(respData.get("RESP")=="00"){
				param += "&STATUS=0";			//撤销成功
				$.beginPageLoading("记录POS撤销日志。。。");
				$.ajax.submit(null, "recordPosLog", param, null, function(data){
					$.endPageLoading();
					if (data && data.get("RESULT_CODE") == "0") {
						alert("银联POS撤销成功！");
						
						var fee=parseFloat(amount)/100;
						$.posTrade.statAmount(fee, -1);
						
						$.posTrade.loadPosLogs();
					}
					
				},function(code, info, detail){
					$.endPageLoading();
					alert("记录POS撤销日志错误："+info);
				});
			}else{
				param += "&STATUS=1";			//撤销失败
				if (respStr.getByteLength() != 148) {
					alert("银联POS撤销失败！POS机返回数据 错误！");
					return;
				}
				alert("银联POS撤销失败！\n错误码：" + respData.get("RESP") + ",错误信息：" + respData.get("RESP_INFO"));
				$.ajax.submit(null, "recordPosLog", param, null,function(data){
					$.endPageLoading();
				},function(code, info, detail){
					$.endPageLoading();
				});
			}
		},
		//选择POS刷卡记录
		selectPosLog:function(){
			$("#BackBtn").css("display", "");
		},
		//当日撤销，POS成功刷卡撤销
		backFee:function(){
			var selectedPos=$("input[name=posTrade]:checked")
			if(!selectedPos || !selectedPos.length){
				alert("请选择需要撤销的POS刷卡记录！");
				return ;
			}
			var posTradeId=selectedPos.val();
			var certNo=selectedPos.parent().parent().find("td[name=posCertNo]").html();
			var amount=selectedPos.parent().parent().find("td[name=posAmount]").html();
			$.posTrade.cancelFee(certNo, amount, posTradeId);
		},
		
		//POS查询余额
		queryFee:function() {
			var reqStr=$.posTrade.getPosReqStr($.posTrade.POS_BALANCE);
			if(!reqStr) return ;
			var respStr=$.posTrade.posTrans(reqStr);
			var respData=$.posTrade.parsePosResp(respStr);
			if (respData.get("RESP") == "00") {
				alert("查询余额成功!");
				return; 
			}else{
				if (respStr.getByteLength() == 148) {
					alert("银联pos查询余额失败，请重试！错误码：" + respData.get("RESP") + ",错误信息：" + respData.get("RESP_INFO"));
					return false;
				} else {
					alert("银联pos查询余额失败！pos机返回数据错误！");
				}
			}
		},
		//POS签到
		signIn:function(){
			var reqStr=$.posTrade.getPosReqStr($.posTrade.POS_SIGNIN);
			if(!reqStr) return ;
			var respStr=$.posTrade.posTrans(reqStr);
			var respData=$.posTrade.parsePosResp(respStr);
			if (respData.get("RESP") == "00") {
				alert("签到成功!");
				return; 
			}else{
				if (respStr.getByteLength() == 148) {
					alert("银联pos签到失败，请重试！错误码：" + respData.get("RESP") + ",错误信息：" + respData.get("RESP_INFO"));
					return false;
				} else {
					alert("银联pos签到失败！pos机返回数据错误！");
				}
			}
		},
		//提交POS刷卡
		submitPosPaying:function(){
			var allAmount=$("#ALL_AMOUNT").val();
			var curAmount=$("#CUR_AMOUNT").val();
			if(parseFloat(curAmount) != parseFloat(allAmount)){
				alert("总金额和银联POS刷卡金额不一致！");
				return;
			}
			parent.$.fee.closePosTrade(parseFloat(curAmount)*100);
		},
		//取消POS刷卡
		cancelPosPaying:function(){
			var posCount=$("#POS_COUNT").val();
			if(parseInt(posCount)>0){
				alert("如果业务不提交或修改其它缴费方式，请把POS刷卡的"+posCount+ "笔费用退掉！");
			}
			var curAmount=$("#CUR_AMOUNT").val();
			parent.$.fee.closePosTrade(parseFloat(curAmount)*100);
		},
		//统计刷卡金额：如果是缴费，mark=1；如果是退费，mark=-1
		statAmount:function(fee, mark){
			var allAmount=$("#ALL_AMOUNT").val();
			var curAmount=$("#CUR_AMOUNT").val();
			
			var newAmount=Math.round((parseFloat(fee)*mark + parseFloat(curAmount))*100)/100;
			$("#CUR_AMOUNT").val(newAmount);
			var amount=Math.round((parseFloat(allAmount)-parseFloat(newAmount))*100)/100;
			$("#AMOUNT").val(amount);
		},
		//支付金额设置
		verifyFee:function(){
			var val=$.trim($(this).val());
			if(!$.isNumeric(val)){
				alert("告警提示","输入金额不合法！");
				return false;
			}
			//进行四舍五入处理
			var fee = parseFloat(val)*100;
			$(this).val(Math.round(fee)/100);
		},
		//解析POS返回的相应字符串
		parsePosResp:function (respStr) {
			var data=$.DataMap();
			data.put("RESP", respStr.subCharStr(0, 2));				//返回码:00 表示成功，其它表示失败
			data.put("BANK_CODE", respStr.subCharStr(2, 4));		//银行行号: 4	发卡行代码
			data.put("CARD_NO", respStr.subCharStr(6, 20));			//卡号 20	卡号（屏蔽部分，保留前6后4）
			data.put("CERT_NO", respStr.subCharStr(26, 6));			//凭证号
			data.put("AMOUNT", respStr.subCharStr(32, 12));			//金额
			data.put("RESP_INFO", respStr.subCharStr(44, 40));		//交易说明	ANS	40	中文解释
			data.put("MERCH_ID", respStr.subCharStr(84, 15));		//商户号			
			data.put("POS_ID", respStr.subCharStr(99, 8));			//终端POS机号			
			data.put("BATCH_NO", respStr.subCharStr(107, 6));		//批次号			
			data.put("TRANS_DATE", respStr.subCharStr(113, 4));		//交易日期			
			data.put("TRANS_TIME", respStr.subCharStr(117, 6));		//交易时间			
			data.put("REF_NO", respStr.subCharStr(123, 12));		//交易参考号			
			data.put("AUTH_NO", respStr.subCharStr(135, 6));		//授权号			
			data.put("LIQUIDATION", respStr.subCharStr(141, 4));	//清算日期					
			data.put("LRC", respStr.subCharStr(145, 3));			//LRC校验	ANS	3	三位数字，应该和请求一致
			return data;
		},
		
		/**
		 * 拼接POS交易入参数据
		 */
		getPosReqStr:function(tradeType, amount, tradeTime, refNo, certNo){
			var posNo="00000000";
			var staffId=($("#OPER_STAFF_ID").val()).fillRightChar(" ", 8);	//不足8位右边不空格
			if(!amount) {
				amount="000000000000"; 
			}else{
				amount= new String(amount).fillLeftChar("0", 12);				//费用不足12为左边补0				
			}
			//获取3位随机LRC码
			var lrc=Math.floor(Math.random()*899+100);
			
			//原交易日期 yyyymmdd格式，退货时用，其他交易空格
			if(tradeType==$.posTrade.POS_BACK){
				if(!tradeTime || tradeTime.length!=8){
					alert("原交易日期错误!");
					return false;
				}
				if(!refNo || refNo.length!=12){
					alert("原交易参考号错误!");
					return false;
				}
			}else{
				tradeTime="".fillRightChar(" ", 8);		//原交易日期:非退货时，补足8位空格
				refNo = "".fillRightChar(" ", 12);		//原交易参考号:非退货时，补足12位空格
			}
			
			//原凭证号:非撤消时，补足6位空格
			if(tradeType==$.posTrade.POS_CANCEL){
				if(!certNo || certNo.length!=6){
					alert("原凭证号错误!");
					return false;
				}
			}else{
				certNo = "".fillRightChar(" ", 6);		
			}

			return posNo+staffId+tradeType+amount+tradeTime+refNo+certNo+lrc;
		},
		
		posTrans:function(req){
			PosDeviceOcx.bankrequest = req;
			PosDeviceOcx.trans();
			return PosDeviceOcx.BankResponse;
		}
	}});
	
})(Wade);

//截取字符
String.prototype.subCharStr = function (start, len){
	var str = "";
	var idx = 0;
	for(var i=0;i<this.length;i++){		
		if(idx>=start+len){
			break;
		}else if(idx>=start){
			str += this.charAt(i);
		}
		//中文占两位
		if(parseInt(this.charCodeAt(i))>127){
			idx+=2;   
		}else{
			idx++;
		}	
	 }
	 return	str;
}

//补齐字符串左侧串，char为需要补全的字符，len为字符串最终的长度
String.prototype.fillLeftChar = function (char, len){
	var str=""+this;
	if(this.length>len){
		str=this.substr(this.length-len);
	}else if(this.length<len){
		for(var i=0, size=len-this.length; i<size; i++){
			str = char+str;
		}
	}
	return str;
}
//补齐字符串右侧串，char为需要补全的字符，len为字符串最终的长度
String.prototype.fillRightChar = function (char, len){
	var str=""+this;
	if(this.length>len){
		str=this.substr(0, len);
	}else if(this.length<len){
		for(var i=0, size=len-this.length; i<size; i++){
			str += char;
		}
	}
	return str;
}

String.prototype.getByteLength = function (){
	var idx = 0;
	for(var i=0;i<this.length;i++){		
	   if(parseInt(this.charCodeAt(i))>127){
		   idx+=2;   //中文占两位
	   }else{
		   idx++;
	   }
	}
	return idx;
}