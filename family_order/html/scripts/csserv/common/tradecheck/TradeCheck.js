(function($){
	$.extend({tradeCheck:{
		dataPart : null,							//需要提交的业务数据区块
		listener : "checkBeforeTrade",				//侦听方法
		dynamicParams : null,						//动态设置参数
		dynamicParamData :$.DataMap(),
		
		//设置单个参数
		setParam : function(key, value){
			(this.dynamicParamData).put(key, value);
		},
		//设置参数,每次设置，都会覆盖之前参数
		addParam : function(paramStr){
			this.dynamicParams = paramStr;
		},
		//清除所有动态参数
		clearParam:function(){
			this.dynamicParams = null;
			this.dynamicParamData.clear();
		},
		
		setDataPart:function(areaids){
			this.dataPart = areaids ;
		},
		
		setListener:function(listener){
			this.listener = listener ;
		},
		
		//业务规则校验动作
		checkTrade:function(tag, func){
			var my = this;
			var entrancePart;
			if(!tag) tag = 0;
			
			//规则校验公用参数
			var actionParams = "&X_CHOICE_TAG="+tag;
			
			if($("#TRADE_TYPE_CODE") && $("#TRADE_TYPE_CODE").length){
				actionParams += "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val();
				actionParams += "&ORDER_TYPE_CODE=" + $("#TRADE_TYPE_CODE").attr("orderTypeCode");
			}
			if($.auth && $.auth.getAuthData()){
				var userInfo = $.auth.getAuthData().get("USER_INFO");
				var custInfo = $.auth.getAuthData().get("CUST_INFO");
				var acctInfo = $.auth.getAuthData().get("ACCT_INFO");
				var acctDayInfo = $.auth.getAuthData().get("ACCTDAY_INFO");
				if(userInfo && userInfo.length>0){
					userInfo.eachKey(function(key, index, totalCount){
						actionParams += "&"+key+"="+ userInfo.get(key);
					});					
				}
				
				//2014-5-21 LIUKE ADD
				if(custInfo && custInfo.length>0){
					actionParams += "&CUST_NAME="+ custInfo.get("ORIGIN_CUST_NAME");	
					actionParams += "&CUST_TYPE="+ custInfo.get("CUST_TYPE");	
					actionParams += "&IS_REAL_NAME="+ custInfo.get("IS_REAL_NAME");	
					actionParams += "&PSPT_TYPE_CODE="+ custInfo.get("PSPT_TYPE_CODE");	
					actionParams += "&PSPT_ID="+ custInfo.get("ORIGIN_PSPT_ID");	
				}
				
				if(acctInfo && acctInfo.length>0){
					actionParams += "&ACCT_ID="+ acctInfo.get("ACCT_ID");
				}
				if(acctDayInfo && acctDayInfo.length>0){
					acctDayInfo.eachKey(function(key, index, totalCount){
						actionParams += "&"+key+"="+ acctDayInfo.get(key);
					});					
				}
			}
			//如果是业务受理提交前校验，判断是否存在费用信息，同时将费用加入进来
			if(tag == 1 && $.feeMgr){
				actionParams += "&X_TRADE_FEESUB="+($.feeMgr.getFeeList()).toString();
				actionParams += "&X_TRADE_PAYMONEY="+($.feeMgr.getPayModeList()).toString();
			}
			/**
			 * 链接用户特定参数
			 * 加载动态字符串参数
			 */
			if(this.dynamicParams && (this.dynamicParams).length>0){
				actionParams += this.dynamicParams;
			}
			//加载动态键值对参数
			if(this.dynamicParamData && (this.dynamicParamData).length>0){
				(this.dynamicParamData).eachKey(function(key,index,totalcount){
					actionParams += "&"+key+"="+(my.dynamicParamData).get(key);
				});
			}
			
			$.beginPageLoading("业务规则校验。。。");
			//通过刷新组件，在组件内部做校验前准备
			$.ajax.submit(this.dataPart, this.listener, actionParams, null,
				function(data){
					$.endPageLoading();
					my.clearParam();
					my.dataPart = null;
					my.showRuleResult(data, tag, func);
				},function(code, info, detail){
					$.endPageLoading();
					my.clearParam();
					my.dataPart = null;
					MessageBox.error("错误提示","业务规则校验报错！",$.tradeCheck.reflushPage, null, info, detail);
				},function(){
					$.endPageLoading();
					my.clearParam();
					my.dataPart = null;
					MessageBox.alert("告警提示","业务规则校验超时!");
			});	
		},
		
		//规则信息提示
		showRuleResult:function(data, tag, func){
			var errorSet = data.get("TIPS_TYPE_ERROR");
			var confirmSet = data.get("TIPS_TYPE_CHOICE");
			var warnSet = data.get("TIPS_TYPE_TIP");
			//错误提示
			if(tag==0 && errorSet && errorSet.length>0){
				//中断错误提示，直接返回
				MessageBox.error("错误提示","业务规则报错!", $.tradeCheck.reflushPage, null, errorSet.get(0, "TIPS_INFO"));
				return ;
			}
			//确认提示
			if(tag==0 && confirmSet && confirmSet.length>0){
				$.tradeCheck.showConfirm(confirmSet, warnSet, tag, func);
				return;
			}
			//告警提示
			if(tag==0 && warnSet && warnSet.length>0){
				$.tradeCheck.showAlert(warnSet, func);
				return;
			}
			if(func) func();
		},
		
		showConfirm:function(confirmSet, warnSet, tag, func){
			if(!confirmSet || confirmSet.length == 0){
				$.tradeCheck.showAlert(warnSet, func);
				return;
			}
			var item = confirmSet.get(0);
			var content = item.get("TIPS_INFO");
			confirmSet.remove(item);
			MessageBox.confirm("确认提示", "业务规则提示！", 
				function(btn){
					if(btn == "ok"){
						$.tradeCheck.showConfirm(confirmSet, warnSet, tag, func);
					}else{
						if(tag==0) $.tradeCheck.reflushPage();
					}
			}, null, content);	
		},
		
		showAlert:function(warnSet, func){
			if(!warnSet || warnSet.length == 0){
				if(func) func();
				return;
			}
			var item = warnSet.get(0);
			var content = item.get("TIPS_INFO");
			warnSet.remove(item);
			MessageBox.alert("告警提示", "业务规则提示！", 
				function(){
					$.tradeCheck.showAlert(warnSet, func);
			}, null, content);	
		},
		
		//刷新页面
		reflushPage:function(){
			var href = window.location.href;
			if(href){
				if(href.lastIndexOf("#nogo") == href.length-5){
					href = href.substring(0, href.length-5);
				}
				var url = href.substring(0, href.indexOf("?"));
				var reqParam = href.substr(href.indexOf("?")+1);
				
				var paramObj = $.params.load(reqParam);
				paramObj.remove("DISABLED_AUTH");
				paramObj.remove("AUTO_AUTH");
				paramObj.remove("SERIAL_NUMBER");
				var param = paramObj.toString();
				if(param.indexOf("SERIAL_NUMBER") == -1 && $("#AUTH_SERIAL_NUMBER").length){
					param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				}
				param += "&AUTO_AUTH=false";
				window.location.href = url+"?"+param;
			}
		}
		
	}});
	
})(Wade);
