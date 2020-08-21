(function($){
	$.extend({authNoTrade:{
		popupHandler:"POP_AUTH_PARAMS",
		cacheData:{},			//存放校验号码，USER_ID,三户资料
		
		init:function(){
			//查询按钮
			$("#AUTH_SUBMIT_BTN").bind("click",$.authNoTrade.beforeQueryUserCancelTrade);
			$("#CANCEL_CSSUBMIT_BUTTON").bind("click",$.authNoTrade.submitCancelTrade); 
		},
		
		beforeQueryUserCancelTrade:function(){
			
 		   if(!$.validate.verifyAll('ConditionPart'))
 		   {
 			   return false;
 		   }
 		   
 		   $.beginPageLoading("认证初始化。。。");
 		   $.ajax.submit(null, "isNeedAuth", null, null,
				function(data){
					$.endPageLoading();
					var isNeedAuth = data.get("IS_NEED_AUTH");
					if("isNeedAuth" == "YES")
					{
						//鉴权认证
 		   				$.authNoTrade.popAuthCheck();
					}
					else
					{
						//无需认证
						$.authNoTrade.queryUserCancelTrade();
					}
				},
				function(error_code, error_info){
					$.endPageLoading();
					MessageBox.alert("错误提示","错误代码:" + error_code + ",错误信息:" + error_info);
				},
				function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","认证校验超时！");
				}
			);
 	    },
		
		queryUserCancelTrade:function(){
			$.BmFrame.setWideAcctType(); //获取并设置宽带账号类型(主账号、子账号、普通账号)
 		   	$.BmFrame.refreshTable();//刷新表格获取可取消业务列表
 		   	$.BmFrame.clearFeeInfo();
 		   	
 		   	$("#SERIAL_NUMBER").attr("disabled", false);
		},
		
		//弹出认证窗口
		popAuthCheck:function(){
			$("#SERIAL_NUMBER").attr("disabled", true);	
			var param = "&HANDLER="+$.authNoTrade.popupHandler;
			param += "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
			param += "&IDENTITY_CHECK_TAG=11000";
			
			//弹出密码认证窗口
			$.popupPage("components.auth.AuthCheckNoTrade", "init", param, "身份校验", "500", "135", $.authNoTrade.popupHandler);
			$("#SERIAL_NUMBER").attr("disabled", false);	
		},

		onAuthCheck:function(){
			//继续禁用
			$("#SERIAL_NUMBER").attr("disabled", true);
			//如果是客服接入，校验通过以后，直接加载三户数据，校验方式设置为密码认证
			var authCheckValue=$("#"+$.authNoTrade.popupHandler).val();
			if(authCheckValue == "1"){
				$.authNoTrade.setCacheSn($("#SERIAL_NUMBER").val(), "1");
				//$.auth.loadTradeData();
				return;
			}
			var param = "";	
			//鉴权认证公共入参
			param += "&ACTION=AUTH_CHECK";
			param += "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			if ($.authNoTrade.cacheData["AUTH_USER_ID"]){
				param += "&USER_ID="+$.authNoTrade.cacheData["AUTH_USER_ID"];		//用户USER_ID
			}

			/**
			 * 鉴权认证密码框返回入参
			 * [CHECK_MODE,PSPT_TYPE_CODE,USER_PASSWD,PSPT_ID,SIM_NO,VIP_ID,OWNER_PSPT_ID,IVR_PASS_SUCC,DISABLED_AUTH]
			 */
			param += $("#"+$.authNoTrade.popupHandler).val();

			$.beginPageLoading("认证校验。。。");
			$.ajax.post(null, "authCheck", param, null, $.authNoTrade.afterAuthCheck,	
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","认证校验报错！",$.authNoTrade.reflushPage, null, info, detail);
				},
				function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","认证校验超时！");
				}
			);
		},
		
		afterAuthCheck:function(data){
			$.endPageLoading();
			var authCount = parseInt($("#AUTH_SUBMIT_BTN").attr("authCount"));
			authCount++;
			if (data.get("RESULT_CODE") !="0"){
				//REQ201506020023 证件办理业务触发完善客户信息 CHENXY3
				if(data.get("RESULT_CODE") =="2"){
					MessageBox.alert("提示",data.get("RESULT_INFO"));
				}else{
					$("#AUTH_SUBMIT_BTN").attr("authCount", authCount);
					MessageBox.alert("告警提示", "第"+authCount+"次身份校验："+data.get("RESULT_INFO"), function(){
						if (data.get("IS_CLOSE") != "1" && authCount<3){
							$.authNoTrade.popAuthCheck();
						}else{
							//如果校验错误锁定以后，刷新页面
							$.authNoTrade.reflushPage();
						}	
					});
					return;
				}
			}
			//如果校验成功，则清空认证次数
			$("#AUTH_SUBMIT_BTN").attr("authCount", "0");	
		
			debugger;
			$.authNoTrade.queryUserCancelTrade();
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
				paramObj.remove("SERIAL_NUMBER");
				paramObj.remove("AUTO_AUTH");
				var param = paramObj.toString();
				if(param.indexOf("SERIAL_NUMBER") == -1 && $("#SERIAL_NUMBER").length){
					param += "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				}
				param += "&AUTO_AUTH=false";		//重新刷新以后，禁用之前逻辑中的自动刷新

				window.location.href = url+"?"+param;
			}
		},
		submitCancelTrade:function()
		{
			var tradeInfos = $.BmFrame.buildJsonData();
	    	if(!tradeInfos || tradeInfos.length <= 0){
	    		MessageBox.alert("提示","您没有选择可取消的业务，请选择后提交!");
	    		return false;
	    	}
	    	
	    	if (null == $("#CANCEL_REASON").val() || '' == $("#CANCEL_REASON").val())
	    	{
	    		MessageBox.alert("提示","撤单原因不能为空!");
	    		return false;
	    	}
	    	
	    	//如果选择其他，备注为必填
	    	if ('4' == $("#CANCEL_REASON").val())
	    	{
	    		if (null == $("#REMARK").val() || '' == $("#REMARK").val())
	    		{
	    			MessageBox.alert("提示","请填写取消备注信息!");
		    		return false;
	    		}
	    	}
	    	/**
	    	 * REQ201609190029_优化家庭宽带装机退单分类内容
	    	 * @author zhuoyingzhi
	    	 * <br/>
	    	 * 二级原因
	    	 */
	    	var cancel_reason_second_level=$("#CANCEL_REASON_SECOND_LEVEL").val();
	    	if(null ==cancel_reason_second_level ||'' == cancel_reason_second_level ){
	    		MessageBox.alert("提示","二级原因不能为空!");
	    		return false;
	    	}
	    	//如果选择其他原因,二级原因再选择   其他 ,备注为必填
	    	if ('1019092' == cancel_reason_second_level )
	    	{
	    		if (null == $("#REMARK").val() || '' == $("#REMARK").val())
	    		{
	    			MessageBox.alert("提示","请填写取消备注信息!");
		    		return false;
	    		}
	    	}
	    	if($("#REMARK").val()){
	    		  var remark=$("#REMARK").val();
	    		  if(remark != ''){
	    			  if(remark.length > 10){
	    				  MessageBox.alert("提示","取消备注:最大长度为10");
	    				  return false;
	    			  }
	    		  }
	    	}
	    	
	    	MessageBox.confirm("告警提示","系统将提交受理数据信息，你确认要继续吗？",function(re){
				if(re=="ok"){
					var tradeInfo = tradeInfos.get(0);
			    	
			    	var param = "&TRADE_ID="+tradeInfo.get("TRADE_ID")+"&TRADE_TYPE_CODE="+tradeInfo.get("TRADE_TYPE_CODE")
			    	+"&ACCEPT_MONTH="+tradeInfo.get("ACCEPT_MONTH")+"&OPER_FEE="+$("#backOperFee").text()
			    	+"&FOREGIFT="+$("#backForeGift").text()+"&ADVANCE_PAY="+$("#backAdvancePay").text()
			    	+"&FEE_STATE="+tradeInfo.get("FEE_STATE")+"&FEE_STAFF_ID="+tradeInfo.get("FEE_STAFF_ID")
			    	+"&TRADE_STAFF_ID="+tradeInfo.get("TRADE_STAFF_ID")+"&TRADE_EPARCHY_CODE="+tradeInfo.get("TRADE_EPARCHY_CODE")
			    	+"&TRADE_DEPART_ID="+tradeInfo.get("TRADE_DEPART_ID")+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()
			    	+"&CANCEL_REASON="+$("#CANCEL_REASON").val()+"&REMARK="+$("#REMARK").val()
			    	+"&CANCEL_REASON_ONE="+$("#CANCEL_REASON").val()//撤单原因
			    	+"&CANCEL_REASON_TWO="+cancel_reason_second_level;//二级原因
			    	
			    	$.beginPageLoading("业务提交中。。。");
			    	
			    	$.ajax.submit(this, 'submitCancelTrade', param, '',
							function(data) 
							{
								$.endPageLoading();
								
								$.authNoTrade.afterSubmit();
								
							}, function(error_code, error_info)
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				}
			});
	    },
	    afterSubmit:function(data){
	    	$.endPageLoading();
	    	MessageBox.success("成功提示","业务取消成功!",function(btn){
	    		$.redirect.toPage("iorder","imslandline.CancelIMSLandLineNew","onInitTrade",'');
	    		debugger;
	    	});
	    }
	}});
	//页面初始化
	$($.authNoTrade.init);
})(Wade);