(function($){
	$.extend({uca:{
		cacheData:{},			//存放校验号码，USER_ID,三户资料
		popupHandler:"POP_AUTH_PARAMS",
		
		loadUcaData: function(){
			if(!$.uca.authSnValid()){
				return ;
			}
			$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
			
			$.uca.beforeAuthCheck();
		},
		
		//认证校验之前动作,确认用户是否存在
		beforeAuthCheck:function(){
			var param = "&ACTION=AUTH_BEFORE";
			var authSn = $("#AUTH_SERIAL_NUMBER").val();
			
			param += "&SERIAL_NUMBER="+authSn;
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&NET_TYPE_CODE=00";
			param += "&USER_CAN_BE_NULL="+$("#AUTH_SUBMIT_BTN").attr("userCanBeNull");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			param += "&AGENT_TYPE="+$("#AUTH_SUBMIT_BTN").attr("agentType");

			$.beginPageLoading("查询用户。。。");
			//通过刷新组件，在组件内部做校验前准备
			ajaxSubmit(null, null, param, $.auth.componentId, $.uca.callBackBeforeAuthCheck,
				function(code, info, detail){
					$.endPageLoading();
					//MessageBox.alert("错误提示","查询用户报错！",$.uca.reflushPage, null, info, detail);
					$.auth.closePage("错误信息", info?info:"查询用户报错！", "0");
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "查询用户超时");
			});	

		},
		
		//查询业务类型数据及其他认证需要的数据，判断是否弹出校验认证框
		callBackBeforeAuthCheck:function(data){ 
			$.endPageLoading();	
			if($.uca.cacheData["AUTH_CURRENT_SN"] 
				&& $.uca.cacheData["AUTH_CURRENT_SN"]!=$("#AUTH_SERIAL_NUMBER").val()){
				$("#AUTH_SUBMIT_BTN").attr("authCount", "0");		//如果鉴权号码跟输入号码不同，则重置认证错误次数
			}
			var userInfo = data.get("USER_INFO");
			if(userInfo){
				$.uca.cacheData["AUTH_USER_ID"] = userInfo.get("USER_ID");	//记录好认证USER_ID
				$.uca.cacheData["AUTH_CURRENT_SN"] = userInfo.get("SERIAL_NUMBER");
			}
			//如果不需要认证，直接去加载三户信息
			if(data.get("AUTH_STATE") == "1"){
				$.uca.loadTradeData();
				return;
			}
			//记录认证方式
			var checkTag = data.get("AUTH_IDENTITY_CHECK_TAG");
			$("#AUTH_SUBMIT_BTN").attr("checkTag", checkTag);
			$("#AUTH_SUBMIT_BTN").attr("noUserPasswd", "false");
			if (userInfo && userInfo.get("USER_PASSWD")==""){
				//设置用户密码为空
				$("#AUTH_SUBMIT_BTN").attr("noUserPasswd", "true");
				MessageBox.alert("告警提示","该用户尚未设置密码,请使用客户证件方式进行身份校验！", function(){
					//如果用户密码为空，且只有密码认证唯一方式，则允许用户不需要校验
					if (checkTag=="01000"){
						$.uca.loadTradeData();
						return;
					}
					$.uca.popAuthCheck();
				});
			}else{
				$.uca.popAuthCheck();
			}
		},
		//弹出认证窗口
		popAuthCheck:function(){
			var param = "&HANDLER="+$.uca.popupHandler;
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&IDENTITY_CHECK_TAG="+$("#AUTH_SUBMIT_BTN").attr("checkTag");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&NO_USER_PASSWD="+$("#AUTH_SUBMIT_BTN").attr("noUserPasswd");
			param += "&AGENT_TYPE="+$("#AUTH_SUBMIT_BTN").attr("agentType");
			param += "&CUST_CALL_CHECK="+$("#AUTH_SUBMIT_BTN").attr("custCallCheck");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			
			//弹出密码认证窗口
			popupPage("身份校验", "components.auth.AuthCheckNew", "init", param, null, "c_popup c_popup-half c_popup-half-hasBg", $.uca.onAuthCheck, null);
			//解除输入框禁用，否则点击取消时候，没法输入
			$("#AUTH_SERIAL_NUMBER").attr("disabled", false);	
		},
		
		onAuthCheck:function(){
			//继续禁用
			$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
			var param = "";	
			//鉴权认证公共入参
			param += "&ACTION=AUTH_CHECK";
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&NO_USER_PASSWD="+$("#AUTH_SUBMIT_BTN").attr("noUserPasswd");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			if ($.uca.cacheData["AUTH_USER_ID"]){
				param += "&USER_ID="+$.uca.cacheData["AUTH_USER_ID"];		//用户USER_ID
			}

			/**
			 * 鉴权认证密码框返回入参
			 * [CHECK_MODE,PSPT_TYPE_CODE,USER_PASSWD,PSPT_ID,SIM_NO,VIP_ID,OWNER_PSPT_ID,IVR_PASS_SUCC,DISABLED_AUTH,SERIAL_NUMBER1,SERIAL_NUMBER2,SERIAL_NUMBER3,CUST_NAME]
			 */
			var checkData = $("#"+$.uca.popupHandler).val();
			param += checkData;
			
			//更改不需要认证标识
			var authParams = $.params.load(checkData);
			if(authParams.get("DISABLED_AUTH")){
				$("#AUTH_SUBMIT_BTN").attr("disabledAuth", authParams.get("DISABLED_AUTH"));
			}
			var checkMode = authParams.get("CHECK_MODE");
			if(checkMode && (checkMode=="1" || checkMode=="2" || checkMode=="4")){
				$("#AUTH_SUBMIT_BTN").attr("userPasswd", authParams.get("PASSWORD"));
			}
			
			$.beginPageLoading("认证校验。。。");
			ajaxSubmit(null, null, param, $.auth.componentId, $.uca.afterAuthCheck,	
				function(code, info, detail){
					$.endPageLoading();
					//MessageBox.alert("错误提示","认证校验报错！",$.auth.reflushPage, null, info, detail);
					$.auth.closePage("错误信息", "认证校验报错！", "0");
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","认证校验超时！");
			});
		},
		afterAuthCheck:function(data){
			$.endPageLoading();
			var authCount = parseInt($("#AUTH_SUBMIT_BTN").attr("authCount"));
			authCount++;
			if (data.get("RESULT_CODE") !="0"){
				$("#AUTH_SUBMIT_BTN").attr("authCount", authCount);
				if(authCount<3){
					MessageBox.alert("告警提示", "第"+authCount+"次身份校验："+data.get("RESULT_INFO"), function(){
						if (data.get("IS_CLOSE") != "1"){
							$.uca.popAuthCheck();
						}else{
							//如果校验错误锁定以后，刷新页面
							//$.auth.reflushPage();
							$.auth.closePage("错误信息", "密码校验错误达到最大次数！", "1");
						}	
					});
				}else{
					//MessageBox.alert("告警提示", "三次身份校验失败!");
					//$.auth.reflushPage();
					$.auth.closePage("告警提示", "三次身份校验失败！", "1");
				}	
				return;
			}
			//如果校验成功，则清空认证次数
			$("#AUTH_SUBMIT_BTN").attr("authCount", "0");		
			if(!$.uca.cacheData["AUTH_DATA"]) $.uca.cacheData["AUTH_DATA"]=$.DataMap();
			//如果通过认证，则记录认证校验数据
			if(data.containsKey("CHECK_MODE")){
				$.uca.cacheData["AUTH_DATA"].put("CHECK_MODE", data.get("CHECK_MODE"));
				$.uca.cacheData["AUTH_DATA"].put("CHECK_DESC", data.get("CHECK_MODE_DESC"));
			}
			$.uca.loadTradeData();				
		},
		//加载三户资料
		loadTradeData:function(){
			var param = "&ACTION=AUTH_DATA";
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&USER_CAN_BE_NULL="+$("#AUTH_SUBMIT_BTN").attr("userCanBeNull");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			param += "&MEB_USER_ID="+$("#AUTH_SUBMIT_BTN").attr("mebUserId");
			param += "&ROLE="+$("#AUTH_SUBMIT_BTN").attr("role");
			
			if ($.uca.cacheData["AUTH_USER_ID"]){
				param += "&USER_ID=" + $.uca.cacheData["AUTH_USER_ID"];
			}
			
			$.beginPageLoading("加载数据。。。");
			ajaxSubmit(null, null, param, $.auth.componentId, 
				function(ucaData){
					$.endPageLoading();
					//保存好用户信息
					if(!$.uca.cacheData["AUTH_DATA"]) $.uca.cacheData["AUTH_DATA"]=$.DataMap();
					ucaData.eachKey(function(key,index,totalcount){
						$.uca.cacheData["AUTH_DATA"].put(key, ucaData.get(key));
					});
					
					var userInfo = ucaData.get("USER_INFO");
					if(userInfo){
						$("#TRADE_EPARCHY_NAME").text(userInfo.get("EPARCHY_NAME"));
					}
					
					//记录认证的号码
					$.uca.cacheData["AUTH_VALID_SN"] = $("#AUTH_SERIAL_NUMBER").val();
					
					//增加简单密码校验提示，因为不需要发送短信提醒，后台简单密码校验屏蔽
					if($.uca.cacheData["AUTH_DATA"].containsKey("CHECK_MODE")){
						var checkMode = $.uca.cacheData["AUTH_DATA"].get("CHECK_MODE");
						var userPasswd = $("#AUTH_SUBMIT_BTN").attr("userPasswd");
						if(checkMode=="1" || checkMode=="2" || checkMode=="4"){
							if($.uca.isSimplePasswd( 
									$("#AUTH_SUBMIT_BTN").attr("userPasswd"), 
										$("#AUTH_SERIAL_NUMBER").val(), 
											ucaData.get("CUST_INFO").get("PSPT_ID"))){
								MessageBox.alert("告警提示", "设置的服务密码较为简单，为保护客户个人信息安全，请建议客户修改！", 
								function(){
									$.auth.ruleAction(ucaData);
								});
								return;
							}
						}
					}	
					//规则校验
					$.auth.ruleAction(ucaData);		
				},
				function(code, info, detail){
					$.endPageLoading();
					//MessageBox.alert("错误提示","加载数据报错！",$.auth.reflushPage, null, info, detail);
					$.auth.closePage("错误提示", info?info:"加载数据报错！", "0");
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","加载数据超时！");
			});
		},
		
		//简单密码校验
		isSimplePasswd:function(userPasswd, serialNumber, psptId){
			if($.toollib.isSerialCode(userPasswd)){
				return true;
			}
			if($.toollib.isRepeatCode(userPasswd)){
				return true;
			}
			if(psptId.indexOf(userPasswd)>-1 || serialNumber.indexOf(userPasswd)>-1){
				return true;
			}
			if($.toollib.isSubRingCode(serialNumber, userPasswd, 3)){
				return true;
			}
			if($.toollib.getRepeatCount(userPasswd)<=3){
				return true;
			}
			if($.toollib.isHalfSame(userPasswd)){
				return true;
			}
			if($.toollib.isAllParity(userPasswd)){
				return true;
			}
			return false;
		},
		
		authSnValid:function(){
			var snObj=$("#AUTH_SERIAL_NUMBER");
			snObj.val($.trim(snObj.val()));
			//如果认证类型不是个人服务号码，则取消手机号码格式校验
			if($("#AUTH_SUBMIT_BTN").attr("authType")!= "00"){
				snObj.removeAttr("datatype");
			}
			if(!$.validate.verifyField(snObj[0])){
				return false;
			}
			return true;
		}
	}});	
})(Wade);
