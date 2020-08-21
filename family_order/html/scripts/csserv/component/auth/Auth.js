(function($){
	$.extend({auth:{
		componentId:"AuthPart",
		popupHandler:"POP_AUTH_PARAMS",
		cacheData:{},			//存放校验号码，USER_ID,三户资料
		userListSvc:"",			//查询多用户服务，如果为空，默认取缺省CS.UserListSVC.queryUserList
		init:function(){
			$.auth.bindEvents(); 
			var snObj = $("#AUTH_SERIAL_NUMBER");
			snObj.focus();
			//保证光标位于服务号码之后
			if(snObj.val()!= "") {
				snObj.val($.trim(snObj.val()));
			}else if(top["ESCAPE_AUTH_SN"]){
				snObj.val(top["ESCAPE_AUTH_SN"]);
			}
			
			var btnObj = $("#AUTH_SUBMIT_BTN");
			$.auth.componentId=btnObj.attr("componentId");
			var disabledInput = btnObj.attr("disabledInput");
			if(disabledInput && disabledInput=="true"){
				snObj.attr("disabled",true);
			}
			
			/**
			* 如果是客服或设置为自动认证
			* 则在打开页面时候，自动发起认证校验
			* 前提条件是必须传入服务号码
			*/
			var inModeCode = btnObj.attr("inModeCode");
			var autoAuth = btnObj.attr("autoAuth");
			if(inModeCode && inModeCode == "1"){
				var sn = null;
				var callPhone = $.auth.getCallCenterSn();
				//在客服接入时候先保存接续页面的主叫号码或被叫号码
				$("#AGENT_CALL_PHONE").val(callPhone);
				if(callPhone){
					var callParams = callPhone.split("JHJ");
					if(callParams && callParams.length==4){
						sn=callParams[3];
					}
				}
				//autoAuth有可能为空，在客服接入时候也需要启用自动认证
				if(sn && autoAuth!="false"){
					snObj.val(sn);
					$.auth.autoAuth();
					return;
				}
			}
			
			/**
			 * 正常情况下，autoAuth=="true"才启用自动认证
			 * 设置免认证号码情况下，如果autoAuth为空时候，表示第一次加载页面，需要启用自动认证
			 * 如果加载报错以后，重载页面autoAuth=false,这个时候不需要进行自动认证
			 */
			if(autoAuth && autoAuth=="true" || (autoAuth=="" && top["ESCAPE_AUTH_SN"])){
				$.auth.autoAuth();
				return;
			}
		},
		
		/**
		 * IVR回调方法，该方法名由客服那边命名定义
		 */
		validateIvrUserPasswd:function(flag){
			var authCheckWin= $("iframe[name^=wade_popup_frame"+$.auth.popupHandler+"]");
			if(authCheckWin && authCheckWin.length && authCheckWin[0].contentWindow){
				authCheckWin[0].contentWindow.ValidateUserPasswdCallBack(flag);
			}
		},
		
		//绑定事件
		bindEvents:function(){
			//号码输入框
			$("#AUTH_SERIAL_NUMBER").bind("keydown",$.auth.events.onSerialNumberInputKeyDown);
			//粘贴号码
			$("#PASTE_SN_BTN").bind("click",$.auth.events.onPasteSnClick);
			//查询按钮
			$("#AUTH_SUBMIT_BTN").bind("click",$.auth.events.onBtnSubmitClick); 
		},
		
		//判断是否不需要认证手机号码或者已经通过验证的手机号码
		cancelAuth:function(sn){
			if(top["ESCAPE_AUTH_SN"]){
				if(top["ESCAPE_AUTH_SN"]==sn
					|| $("#AUTH_SUBMIT_BTN").attr("authType")== "00"){
					return true;
				}else{
					top["ESCAPE_AUTH_SN"]=null;
				}
			}
			return (top["AUTH_VALID_SN"]==sn && top["AUTH_VALID_CHECKMODE"]!="F");
		},
		
		//校验手机号码是否正确
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
		},
		/**
		 * 获取客服接入号码
		 */
		getCallCenterSn:function(){
			var callPhone = null;
			var frame=top.document.getElementById("callcenter"); 
			var rhkfframe=window.top.document.getElementById("public_iframe");
			if (frame && frame.contentWindow && frame.contentWindow.document) { //老版客服
				var callObj = frame.contentWindow.document.getElementById("CALL_PHONE");
				if(callObj){
					callPhone = callObj.value;
				}
			}else if(rhkfframe && rhkfframe.contentWindow && rhkfframe.contentWindow.document){	//融合客服
				callPhone = rhkfframe.contentWindow.document.getElementById("callId").value
					+ "JHJ" + rhkfframe.contentWindow.document.getElementById("callerNo").value
					+ "JHJ" + rhkfframe.contentWindow.document.getElementById("calledNo").value
					+ "JHJ" + rhkfframe.contentWindow.document.getElementById("serviceNo").value;
			}else if(typeof(eval(window.top.getCustorInfo))=="function"){ //新版客服
				//获取流水号+主叫号码+被叫号码+受理号码
				callPhone = window.top.getSubscriberInfo().getCALL_SERIAL_NO() 
					+ "JHJ" + window.top.getSubscriberInfo().getCALLERNO_AUTH() 
					+ "JHJ" + window.top.getSubscriberInfo().getCALLEDNO_AUTH() 
					+ "JHJ" + window.top.getSubscriberInfo().getBILL_ID();
			}
			return callPhone;
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
				if(param.indexOf("SERIAL_NUMBER") == -1 && $("#AUTH_SERIAL_NUMBER").length){
					param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				}
				param += "&AUTO_AUTH=false";		//重新刷新以后，禁用之前逻辑中的自动刷新

				window.location.href = url+"?"+param;
			}
		},
		//外部调用接口，启动AUTH认证
		autoAuth:function(){
			var autoAuthSn = $("#AUTH_SERIAL_NUMBER").val();
			//如果没有输入号码，则取消自动认证
			if(autoAuthSn == "") return;

			$.auth.authStart();
		},
		//设置免除认证号码
		escapeAuth:function(serialNumber){
			top["ESCAPE_AUTH_SN"]= serialNumber;
		},
		cancelEscapeAuth:function(serialNumber){
			top["ESCAPE_AUTH_SN"]= null;
		},
		
		setCacheSn:function(sn, checkMode){
			top["AUTH_VALID_SN"] = sn;
			top["AUTH_VALID_CHECKMODE"] = checkMode;
		},
		clearCacheSn:function(){
			$.auth.setCacheSn(null, null);
		},
		//启动认证
		authStart:function(){
			if(!$.auth.authSnValid()){
				return ;
			}
			//如果手机号码不同，则需要清理之前可能存在的费用数据
			var sn = $("#AUTH_SERIAL_NUMBER").val();
			if($.feeMgr){
				if(!$.feeMgr.clearFeeList()) return;
			}
			//禁用输入框
			$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
			if ($("#AUTH_SUBMIT_BTN").attr("moreUser") == "true"){
				$.auth.checkMoreUser();
			}else{
				var awayCheck=""; 
				var groupPack=$("#GROUP_PRODUCT_PACK").val();
				var tradeTypecode=$("#TRADE_TYPE_CODE").val();
				if("327" == tradeTypecode){
					if(null == groupPack || ""== groupPack){
						alert('集团产品包不能为空，请选择集团产品包！');
					    return false;
					}

				}
				
				var param = '&TRADE_TYPE_CODE=' + tradeTypecode + '&SERIAL_NUMBER=' + sn + '&AWAY=1'; 
				$.beginPageLoading("查询用户。。。");
				$.ajax.submit(null, "awayCheck", param, "",function(data){
			    	 awayCheck=data.get("AWAYCHECK");
			    	 if($.auth.cancelAuth(sn)&&(awayCheck==null||awayCheck!="1")){
							$.auth.cacheData["AUTH_USER_ID"]=null;
							$.auth.loadTradeData();
							return false;
					 }else{
						//清空之前认证记录数据
						$.auth.clearCacheSn();
						$.auth.beforeAuthCheck();
					 }
				}, function(error_code,error_info,detail){
					$.endPageLoading();
					MessageBox.error("错误提示", error_info, null, null, null, detail);
			    }); 
			}
		},
		
		//核对多用户
		checkMoreUser:function(){
			//ACTION 入参在组件内部区分调用逻辑
			var param = "&ACTION=AUTH_MOREUSER";
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			
			$.beginPageLoading("查询用户。。。");
			//通过刷新组件，在组件内部判断多用户
			ajaxSubmit(null, null, param, $.auth.componentId, 
				function(data){ 
			  		$.endPageLoading();	
			  		if(data && data.get("RESULT_CODE")==0){
			  			//多用户选择
			  			$("#AUTH_SERIAL_NUMBER").attr("disabled", false);
			  			$("#AUTH_SUBMIT_BTN").blur();
			  			$.popupPage("components.auth.UserList", "queryUserList", 
			  							"&cond_SERIAL_NUMBER=" +$("#AUTH_SERIAL_NUMBER").val()+"&cond_USER_LIST_SVC="+$.auth.userListSvc, "用户选择", "640", "250", "SELECTED_AUTH_USER");
			  			
			  		}else{
			  			MessageBox.alert("告警提示","不存在该号码的用户，请重新输入！",$.auth.reflushPage);
			  		}
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","查询用户报错！",$.auth.reflushPage, null, info, detail);
			});
		},
		
		//选择多用户后点击事件
		afterSelectMoreUser:function(){
			$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
			var selectedUserId = $("#SELECTED_AUTH_USER").val();
			if (selectedUserId != ""){
				$.auth.beforeAuthCheck(selectedUserId);
			}else{
				$.auth.beforeAuthCheck();
			}
		},
		//认证校验之前动作,确认用户是否存在
		beforeAuthCheck:function(userId){

			var param = "&ACTION=AUTH_BEFORE";
			var authSn = $("#AUTH_SERIAL_NUMBER").val();
			var callSn = $("#AGENT_CALL_PHONE").val();
			
			param += "&SERIAL_NUMBER="+authSn;
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&NET_TYPE_CODE=00";
			param += "&USER_CAN_BE_NULL="+$("#AUTH_SUBMIT_BTN").attr("userCanBeNull");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			if (userId){
				param += "&USER_ID=" + userId;
			}
			if (callSn){
				param += "&AGENT_CALL_PHONE="+callSn;
			}

			$.beginPageLoading("查询用户。。。");
			//通过刷新组件，在组件内部做校验前准备
			ajaxSubmit(null, null, param, $.auth.componentId, $.auth.callBackBeforeAuthCheck,
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","查询用户报错！",$.auth.reflushPage, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "查询用户超时");
			});	

		},
		//认证校验查询用户,确认用户是否存在，并弹出认证框
		queryPopAuth:function(userId){
			var param = "&ACTION=AUTH_BEFORE";
			var authSn = $("#AUTH_SERIAL_NUMBER").val();
			var callSn = $("#AGENT_CALL_PHONE").val();
			param += "&SERIAL_NUMBER="+authSn;
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&NET_TYPE_CODE=00";
			param += "&USER_CAN_BE_NULL="+$("#AUTH_SUBMIT_BTN").attr("userCanBeNull");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			if (userId){
				param += "&USER_ID=" + userId;
			}
			if (callSn){
				param += "&AGENT_CALL_PHONE="+callSn;
			}
			$.beginPageLoading("查询用户。。。");
			//通过刷新组件，在组件内部做校验前准备
			ajaxSubmit(null, null, param, $.auth.componentId, $.auth.popAuthCheck(),
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","查询用户报错！",$.auth.reflushPage, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "查询用户超时");
			});	
		},
		//查询业务类型数据及其他认证需要的数据，判断是否弹出校验认证框
		callBackBeforeAuthCheck:function(data){ 
			$.endPageLoading();	
			if($.auth.cacheData["AUTH_CURRENT_SN"] 
				&& $.auth.cacheData["AUTH_CURRENT_SN"]!=$("#AUTH_SERIAL_NUMBER").val()){
				$("#AUTH_SUBMIT_BTN").attr("authCount", "0");		//如果鉴权号码跟输入号码不同，则重置认证错误次数
			}
			var userInfo = data.get("USER_INFO");
			if(userInfo){
				$.auth.cacheData["AUTH_USER_ID"] = userInfo.get("USER_ID");	//记录好认证USER_ID
				$.auth.cacheData["AUTH_CURRENT_SN"] = userInfo.get("SERIAL_NUMBER");
			}
			//如果不需要认证，直接去加载三户信息  
			if(data.get("AUTH_STATE") == "1" ){
				$.auth.setCacheSn($("#AUTH_SERIAL_NUMBER").val(), "F");
				$.auth.loadTradeData();
				return;
			}
			//记录认证方式
			var checkTag = data.get("AUTH_IDENTITY_CHECK_TAG");
			$("#AUTH_SUBMIT_BTN").attr("checkTag", checkTag);
			if (userInfo && userInfo.get("USER_PASSWD")==""){
				//设置用户密码为空
				$("#AUTH_SUBMIT_BTN").attr("noUserPasswd", "true");
				MessageBox.alert("告警提示","该用户尚未设置密码,请使用客户证件方式进行身份校验！", function(){
					//如果用户密码为空，且只有密码认证唯一方式，则允许用户不需要校验
					if (checkTag=="01000"){
						$.auth.setCacheSn($("#AUTH_SERIAL_NUMBER").val(), "F");
						$.auth.loadTradeData();
						return;
					}
					$.auth.popAuthCheck();
				});
			}else{
				$.auth.popAuthCheck();
			}
		},
		//弹出认证窗口
		popAuthCheck:function(){
			var param = "&HANDLER="+$.auth.popupHandler;
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&IDENTITY_CHECK_TAG="+$("#AUTH_SUBMIT_BTN").attr("checkTag");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&NO_USER_PASSWD="+$("#AUTH_SUBMIT_BTN").attr("noUserPasswd");
			
			//弹出密码认证窗口
			$.popupPage("components.auth.AuthCheck", "init", param, "身份校验", "500", "135", $.auth.popupHandler);	
			//解除输入框禁用，否则点击取消时候，没法输入
			$("#AUTH_SERIAL_NUMBER").attr("disabled", false);	
		},
		
		onAuthCheck:function(){
			//继续禁用
			$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
			//如果是客服接入，校验通过以后，直接加载三户数据，校验方式设置为密码认证
			var authCheckValue=$("#"+$.auth.popupHandler).val();
			if(authCheckValue == "1"){
				$.auth.setCacheSn($("#AUTH_SERIAL_NUMBER").val(), "1");
				$.auth.loadTradeData();
				return;
			}
			var param = "";	
			//鉴权认证公共入参
			param += "&ACTION=AUTH_CHECK";
			param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&NO_USER_PASSWD="+$("#AUTH_SUBMIT_BTN").attr("noUserPasswd");
			param += "&DISABLED_AUTH="+$("#AUTH_SUBMIT_BTN").attr("disabledAuth");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			if ($.auth.cacheData["AUTH_USER_ID"]){
				param += "&USER_ID="+$.auth.cacheData["AUTH_USER_ID"];		//用户USER_ID
			}

			/**
			 * 鉴权认证密码框返回入参
			 * [CHECK_MODE,PSPT_TYPE_CODE,USER_PASSWD,PSPT_ID,SIM_NO,VIP_ID,OWNER_PSPT_ID,IVR_PASS_SUCC,DISABLED_AUTH]
			 */
			param += $("#"+$.auth.popupHandler).val();
			 
			//更改不需要认证标识
			var authParams = $.params.load(authCheckValue);
			if(authParams.get("DISABLED_AUTH")){
				$("#AUTH_SUBMIT_BTN").attr("disabledAuth", authParams.get("DISABLED_AUTH"));
			}
			var checkMode = authParams.get("CHECK_MODE");
			if(checkMode && (checkMode=="1" || checkMode=="2" || checkMode=="4")){
				$("#AUTH_SUBMIT_BTN").attr("userPasswd", authParams.get("USER_PASSWD"));
			}
			
			$.beginPageLoading("认证校验。。。");
			ajaxSubmit(null, null, param, $.auth.componentId, $.auth.afterAuthCheck,	
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","认证校验报错！",$.auth.reflushPage, null, info, detail);
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
				//REQ201506020023 证件办理业务触发完善客户信息 CHENXY3
				if(data.get("RESULT_CODE") =="2"){
					MessageBox.alert("提示",data.get("RESULT_INFO"));
				}else{
					$("#AUTH_SUBMIT_BTN").attr("authCount", authCount);
					MessageBox.alert("告警提示", "第"+authCount+"次身份校验："+data.get("RESULT_INFO"), function(){
						if (data.get("IS_CLOSE") != "1" && authCount<3){
							$.auth.popAuthCheck();
						}else{
							//如果校验错误锁定以后，刷新页面
							$.auth.reflushPage();
						}	
					});
					return;
				}
			}
			//如果校验成功，则清空认证次数
			$("#AUTH_SUBMIT_BTN").attr("authCount", "0");	
			//记录认证的号码
			$.auth.setCacheSn($("#AUTH_SERIAL_NUMBER").val(), data.get("CHECK_MODE"));
			//设置补换卡的值 //cxy 补换卡类型
			if($("#REMOTECARD_TYPE").val()==""){
				$("#REMOTECARD_TYPE").val(data.get("REMOTECARD_TYPE"));
			}
			
			/**
			 * REQ201705270006_关于人像比对业务优化需求
			 * @author zhuoyingzhi
			 * @date 20170626
			 */
			if($("#AUTH_CHECK_PSPT_TYPE_CODE").val()==""){
				$("#AUTH_CHECK_PSPT_TYPE_CODE").val(data.get("AUTH_CHECK_PSPT_TYPE_CODE"));
			}
			
			if($("#AUTH_CHECK_PSPT_ID").val()==""){
				$("#AUTH_CHECK_PSPT_ID").val(data.get("AUTH_CHECK_PSPT_ID"));
			}
			if($("#AUTH_CHECK_CUSTINFO_CUST_NAME").val()==""){
				$("#AUTH_CHECK_CUSTINFO_CUST_NAME").val(data.get("AUTH_CHECK_CUSTINFO_CUST_NAME"));
			}
			if($("#FRONTBASE64").val()==""){
				$("#FRONTBASE64").val(data.get("FRONTBASE64"));
			}
			/*******************end*********************************************/			
			//查询三户
			$.auth.loadTradeData();		
		},
		//加载三户资料
		loadTradeData:function(){
			var sn=$("#AUTH_SERIAL_NUMBER").val();
			var param = "&ACTION=AUTH_DATA";
			param += "&SERIAL_NUMBER="+sn;
			param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			param += "&USER_CAN_BE_NULL="+$("#AUTH_SUBMIT_BTN").attr("userCanBeNull");
			param += "&AUTH_TYPE="+$("#AUTH_SUBMIT_BTN").attr("authType");
			
			if ($.auth.cacheData["AUTH_USER_ID"]){
				param += "&USER_ID=" + $.auth.cacheData["AUTH_USER_ID"];
			}
			var authCheckValue=$("#"+$.auth.popupHandler).val();
			//authCheckValue=1;
			if(authCheckValue == "1"){
				param += "&AUTH_CHECK_VALUE_TAG=IVR001";
			}else
				param += "&AUTH_CHECK_VALUE_TAG=NGBOSS";
			//如果是经过认证方式认证，则需要设置是否模糊化标记
			if(top["AUTH_VALID_SN"] && (!top["ESCAPE_AUTH_SN"] || top["ESCAPE_AUTH_SN"]!=sn)
					&& (top["AUTH_VALID_CHECKMODE"] && top["AUTH_VALID_CHECKMODE"]!="F") 
					&& $("#TRADE_TYPE_CODE").val() != "276" ){
				param += "&X_DATA_NOT_FUZZY=true";
			}
			$.beginPageLoading("加载数据。。。");
			ajaxSubmit(null, null, param, $.auth.componentId, 
				function(ucaData){
				 	$.getScript("scripts/csserv/common/des/des.js",function(){  
					$.endPageLoading();
					//保存好用户信息
					if(!$.auth.cacheData["AUTH_DATA"]) $.auth.cacheData["AUTH_DATA"]=$.DataMap();
					ucaData.eachKey(function(key,index,totalcount){
						$.auth.cacheData["AUTH_DATA"].put(key, ucaData.get(key));
					});
					if(top["AUTH_VALID_SN"]){
						$.auth.cacheData["AUTH_DATA"].put("CHECK_MODE", top["AUTH_VALID_CHECKMODE"]);
					}
					
					//增加简单密码校验提示，因为不需要发送短信提醒，后台简单密码校验屏蔽
					if($.auth.cacheData["AUTH_DATA"].containsKey("CHECK_MODE")){
						var checkMode = $.auth.cacheData["AUTH_DATA"].get("CHECK_MODE");
						var userPasswd = $("#AUTH_SUBMIT_BTN").attr("userPasswd");
						var sn=$("#AUTH_SERIAL_NUMBER").val();
						var psptId=ucaData.get("CUST_INFO").get("PSPT_ID");
						if((checkMode=="1" || checkMode=="2" || checkMode=="4") && userPasswd && sn && psptId){
							userPasswd=new String(userPasswd.substring(0,userPasswd.length-4));		//转成字符串,兼容IE8及以后浏览器BUG
//							var authParams = $.params.load($("#"+$.auth.popupHandler).val());
//							var passWordAfter=authParams.get("USER_PASSWD");
//							passWordAfter=passWordAfter.substring(0,(passWordAfter.length-4));
						
							var firstKey="c";
							var secondKey="x";
							var thirdKey="y";
							userPasswd=strDec(userPasswd,firstKey,secondKey,thirdKey);
						 
							$.auth.isSimplePasswd(userPasswd, sn, psptId,function () {
								MessageBox.alert("告警提示", "设置的服务密码较为简单，为保护客户个人信息安全，请建议客户修改！", 
								function(){
//									$.auth.ruleAction(ucaData);
								});
//								return;
							});
						}
					}
					//规则校验
					$.auth.ruleAction(ucaData);					
					});
				},
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","加载数据报错！",$.auth.reflushPage, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","加载数据超时！");
			});
			
		},
		//启动业务规则校验
		ruleAction:function(obj){
			if(typeof(obj) == "undefined"){
				obj = $.auth.cacheData["AUTH_DATA"];
			}
			
			//判断是否有业务规则校验前自定义规则事件处理		
			var beforeAction=$("#AUTH_SUBMIT_BTN").attr("beforeAction"); 
			if(beforeAction && beforeAction != ""){
				(new Function("var data = arguments[0];"+ beforeAction + ";"))(obj);
			}

			//调用业务规则校验
			if($.tradeCheck && typeof($.tradeCheck.checkTrade)){
				$.tradeCheck.checkTrade(0 , $.auth.fireAction);
			}else{
				$.auth.fireAction(obj);
			}
		},
		
		//加载业务受理准备数据
		fireAction:function(obj){
			if(typeof(obj) == "undefined"){
				obj = $.auth.cacheData["AUTH_DATA"];
			}

			var disabledInput = $("#AUTH_SUBMIT_BTN").attr("disabledInput");
			//更新认证和提交组件相关数据和控件的状态
			if(disabledInput && disabledInput=="false"){
				$("#AUTH_SERIAL_NUMBER").attr("disabled", false);
			}

			//刷新三户展示区域
			if($("#UCAViewPart") && $("#UCAViewPart").length){
				$.ajax.submit(null, "setUCAViewInfos", "&UCAInfoParam="+obj, "UCAViewPart");
			}
			
			//回调业务数据加载服务
			var action=$("#AUTH_SUBMIT_BTN").attr("tradeAction"); 
			if(action && action != ""){
				try{
					(new Function("var data = arguments[0];"+ action + ";"))(obj);
				}catch(e){
					MessageBox.error("错误提示","加载业务受理信息错误，请检查后重试！",$.auth.reflushPage);
					return;
				}
			}
			
			//加载费用
			if($.feeMgr){
				var userId="",productId="-1",sn=$("#AUTH_SERIAL_NUMBER").val(),eparchyCode=null,vipClassId=null;
				if(obj && obj.get("USER_INFO")){
					userId = obj.get("USER_INFO").get("USER_ID");
					sn = obj.get("USER_INFO").get("SERIAL_NUMBER");
					productId = obj.get("USER_INFO").get("PRODUCT_ID");				
					eparchyCode = obj.get("USER_INFO").get("EPARCHY_CODE");				
				}
				if(obj && obj.get("VIP_INFO")){
					vipClassId = obj.get("VIP_INFO").get("VIP_CLASS_ID");				
				}
				$.feeMgr.loadTradeFee($("#TRADE_TYPE_CODE").val(),productId,eparchyCode, vipClassId);
				
				//设置POS机信息
				$.feeMgr.setPosParam($("#TRADE_TYPE_CODE").val(), sn, eparchyCode, userId);
			}
			
			//客户信息
			if(top.showCustInfo && typeof(top.showCustInfo)=="function"){
				var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				param += "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
				if(obj && obj.get("USER_INFO")){
					param +="&USER_ID="+obj.get("USER_INFO").get("USER_ID");
					param +="&PRODUCT_ID="+obj.get("USER_INFO").get("PRODUCT_ID");
					param +="&BRAND_CODE="+obj.get("USER_INFO").get("BRAND_CODE");	
					param +="&EPARCHY_CODE="+obj.get("USER_INFO").get("EPARCHY_CODE");
				};
				$.ajax.submit("", "getHintInfo", param,"",function(data){
						if(!data || (data && data.get("RESULT_CODE")!="0")){
							top.clearCustInfo();
							return ;
						}
						var map=$.DataMap();
						var custName = obj.get("CUST_INFO").get("ORIGIN_CUST_NAME");
						var custNameFazzy = custName.substring(0,1);
						for(var i=0;i<custName.length-1;i++)
						{
							custNameFazzy += "*"; 
						}
						map.put("CUST_NAME", custNameFazzy);
						map.put("PRODCUT_NAME", data.get("PRODCUT_NAME"));
						map.put("HINT_INFO", data.get("HINT_INFO1", "")+data.get("HINT_INFO2", ""));
						top.showCustInfo(map.toString());
						
					},function(code, info, detail){
						$.endPageLoading();
						MessageBox.error("错误提示","加载客户信息错误！", null, null, info, detail);
				});
			}
			
			if(obj.get("CRM_REALTIMEMARKETING_WEBSWITCH")=="1"){
				//营销推荐信息
				if(top.triggerPushInfos && typeof(top.triggerPushInfos)=="function") {
					var hintInfo = $.DataMap(obj.get("USER_INFO").toString());
					hintInfo.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
					var param="&HINT_INFO="+hintInfo.toString();
				/*	$.ajax.submit("", "checkPushInfo", param,"",function(resultData){
							if(!resultData || (resultData && resultData.get("PUSH_FLAG")!="1")){
								top.$.sidebar.hideSide(true);
								return;
							}
							top.triggerPushInfos(param,"baseinfo");
							
						},function(code, info, detail){
							$.endPageLoading();
							MessageBox.error("错误提示","加载新业务推荐信息错误！", null, null, info, detail);
					});*/
					
					$.auth.loadRealTimeMarketingData(obj);
				}	
			}
				
			
			//发展员工初始化
			if($.developStaff && typeof($.developStaff.init)){
				$.developStaff.init();
			}
			//启用提交按钮
			if($.cssubmit){
				$.cssubmit.disabledSubmitBtn(false);
			}
			//清空认证框设置的参数值
			$("#"+$.auth.popupHandler).val("");
			$.auth.cacheData["AUTH_USER_ID"]=null;
			//设置业务受理地州
			if(obj && obj.get("USER_INFO")){
				$("#TRADE_EPARCHY_NAME").text(obj.get("USER_INFO").get("EPARCHY_NAME"));
			}
		},
		
		loadRealTimeMarketingData:function(obj){
			//获取实时营销推荐信息	
			var hintInfo = $.DataMap(obj.get("USER_INFO").toString());
			hintInfo.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
			var param="&HINT_INFO="+hintInfo.toString();
			$.ajax.submit("", "newcheckPushInfo", param,"",function(resultData){
					if(!resultData || (resultData && resultData.get("PUSH_FLAG")!="1")){
						//top.$.sidebar.hideSide(true);
						return;
					}
					//top.triggerPushInfos(param,"baseinfo");
					
				},function(code, info, detail){
					$.endPageLoading();
					//MessageBox.error("错误提示","加载新业务推荐信息错误！", null, null, info, detail);
			});
		},
		
		//获取三户信息等
		getAuthData: function(){
			return $.auth.cacheData["AUTH_DATA"];
		},
		//禁用号码输入框 
		disabledAuthInput:function(flag){
			$("#AUTH_SERIAL_NUMBER").attr("disabled", flag);
		},
		//设置查询多用户列表服务
		setUserListSvc:function(svcName){
			this.userListSvc = svcName;
		},
		//简单密码校验   
		isSimplePasswd:function(userPasswd, serialNumber, psptId,returnFunction){
			var param = "&ACTION=AUTH_CHECKPSW";
			param += "&PassWork="+userPasswd;
			return ajaxSubmit(null, null, param, $.auth.componentId,
				function(data){
					if(data.get("RESULT_CODE")!="0"){
						returnFunction(); 
					}else{
						if($.toollib.isSerialCode(userPasswd)){
							returnFunction();
						}
						if($.toollib.isRepeatCode(userPasswd)){
							returnFunction();
						}
						if(psptId.indexOf(userPasswd)>-1 || serialNumber.indexOf(userPasswd)>-1){
							returnFunction();
						}
						if($.toollib.isSubRingCode(serialNumber, userPasswd, 3)){
							returnFunction();
						}
						if($.toollib.getRepeatCount(userPasswd)<=3){
							returnFunction();
						}
						if($.toollib.isHalfSame(userPasswd)){
							returnFunction();
						}
						if($.toollib.isAllParity(userPasswd)){
							returnFunction();
						}
						if($.toollib.isArithmetic(userPasswd)){
							returnFunction();
						}
						return false;
					}
				},	
				function(code, info, detail){return false;},function(){return false;}
			);
		},
		events:{
			//号码框事件
			onSerialNumberInputKeyDown:function(e){
				if(e.keyCode==13 || e.keyCode==108){
					//回车事件
					$.auth.authStart();
					return false;
				}
				return true;
			},
			//点击查询按钮事件
			onBtnSubmitClick:function(){
				//查询按钮事件
				$.auth.authStart();
			},
			//点击验证按钮事件
			onBtnValideClick:function(){
				if(!$.auth.authSnValid()){
					return ;
				}
				//$.auth.popAuthCheck();
				$.auth.queryPopAuth();//先查用户资料再弹出验证框
			},
			//粘贴服务号码
			onPasteSnClick:function(){
				if(!top["AUTH_VALID_SN"]){
					MessageBox.alert("告警提示","没有可粘贴的服务号码！");
					return;
				}
				$("#AUTH_SERIAL_NUMBER").val(top["AUTH_VALID_SN"]);
			}
		}
	}});
	//页面初始化
	$($.auth.init);
})(Wade);

window.ValidateUserPasswdCallBack=$.auth.validateIvrUserPasswd;