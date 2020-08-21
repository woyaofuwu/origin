(function($){
	$.extend({userCheck:{
		clazz: "com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.usercheck.UserCheckHandler",
		userMap:null,				//存放查询的用户资料
		init:function(fieldName){
			var snObj = $("#"+fieldName);
			var handlerObj = $("#POP_"+fieldName);
			//设置号码输入框校验属性
			if(handlerObj.attr("authType")=="00"){
				//如果是手机服务号码则设置数据限制类型，如果是其他，比如宽度，则取消
				//snObj.attr("datatype", "mbphone");numchar
				//因要增加和校园异网号码转换为虚拟号码 by update zhuweijun
				snObj.attr("datatype", "numchar");
			}
			snObj.attr("nullable", "no");
			if(!snObj.attr("desc") || snObj.attr("desc")==""){
				if(handlerObj.attr("desc") == ""){
					snObj.attr("desc", "用户号码");
				}else{
					snObj.attr("desc", handlerObj.attr("desc"));	
				}
			}
			var className = handlerObj.attr("classHandler");
			if(className && className!=""){
				$.userCheck.clazz = className;
			}
			
			//如果绑定事件属性为true，则绑定回车事件，否则不需要绑定 
			var bindEvent = handlerObj.attr("bindEvent")=="true"? true : false;
			if(bindEvent){
				//回车事件
				snObj.bind("keydown", function(e){
					$.userCheck.events.onUserCheckInput(e, fieldName);
				});
			}
			 
			/**失去焦点事件
			snObj.bind("blur", function(e){
				$.userCheck.checkUser(fieldName);
			}); */
		},
		
		chkBeforeAction:function(fieldName){
			var flag = true;
			var action=$("#POP_"+fieldName).attr("beforeAction"); 
			if(action && action != ""){
				try{
					flag = (new Function("return "+ action + ";"))();
				}catch(e){
					MessageBox.error("错误提示","加载beforeAction错误，请检查后重试！");
					return false;
				}
			}
			return flag;
		},
		
		//校验手机号码是否正确
		checkSnValid:function(fieldName){
			var snObj=$("#"+fieldName);
			snObj.val($.trim(snObj.val()));
			if(!$.validate.verifyField(snObj[0])){
				return false;
			}
			return true;
		},
		
		/**
		* 认证用户
		* 外部调用接口，启动服务号码校验
		*/
		checkUser:function(fieldName){
			//查询按钮事件
			if(!$.userCheck.checkSnValid(fieldName)){
				return;
			} 
			if(!$.userCheck.chkBeforeAction(fieldName)){
				return;
			}
			$.userCheck.queryUser(fieldName);
		},
		
		//认证校验之前动作,确认用户是否存在
		queryUser:function(fieldName){
			var handlerObj = $("#POP_"+fieldName);
			var param = "&SERIAL_NUMBER="+$("#"+fieldName).val();
			param += "&IS_LOCAL="+handlerObj.attr("isLocal");
			param += "&AUTH_TYPE="+handlerObj.attr("authType");
			param += "&EPARCHY_CODE="+handlerObj.attr("tradeEparchyCode");
			$.beginPageLoading("核对号码。。。");
			$.httphandler.submit(null, $.userCheck.clazz, "queryUser", param, 
				function(data){
					$.endPageLoading();
					$.userCheck.callBackQryUser(data, fieldName);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","核对号码报错！", function(){$.userCheck.exceptEvent(fieldName, 'USER_ERR');}, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "核对号码超时");
			});	

		},
		//查询业务类型数据及其他认证需要的数据，判断是否弹出校验认证框
		callBackQryUser:function(data, fieldName){ 
			var authSn = $("#"+fieldName);
			var handlerObj = $("#POP_"+fieldName);
			var sn = $.xss($.trim(authSn.val()));
			var callPhone = $.userCheck.getCallCenterSn();
			if(data && data.get("RESULT_CODE") == "1"){
				MessageBox.alert("告警提示", "查询不到"+authSn.attr("desc")+"["+sn+"]的用户资料！",function(){$.userCheck.exceptEvent(fieldName,"USER_NOT");});
				return;
			}else if(data && data.get("RESULT_CODE") == "2"){
				MessageBox.alert("告警提示", authSn.attr("desc")+"["+sn+"]为异地号码！",function(){$.userCheck.exceptEvent(fieldName, "USER_YD");});
				return ;
			}else if(data && data.get("RESULT_CODE") == "3"){
				$.userCheck.fireAction(fieldName, data.get("USER_INFO"));
				return;
			}else if(data && data.get("RESULT_CODE") != "0"){
				$.userCheck.exceptEvent(fieldName, "USER_CUSTOM", data);
				return;
			}
			var userInfo = data.get("USER_INFO");
			var isAuth = handlerObj.attr("isAuth");
			var cacheSn = handlerObj.attr("cacheSn"); 
			//如果只进行用户查询，则回调用户自定义事件后返回
			if(!isAuth || isAuth=="false"){
				$.userCheck.fireAction(fieldName, userInfo);
				return ;
			}
			//如果开启缓存服务号码开关，则判断号码是否已经记录过，如果有，则不需要再次鉴权
			if(cacheSn && cacheSn=="true"){
				if(top["AUTH_VALID_SN"] && top["AUTH_VALID_SN"]==sn){
					$.userCheck.fireAction(fieldName, userInfo);
					return ;					
				}
				top["AUTH_VALID_SN"]=null;
			}
			//判断客服接入号
			if(callPhone&&sn){
				var callParams = callPhone.split("JHJ");
				if (callParams.length == 4 && ((callParams[1] != null && callParams[1].indexOf(sn) > -1) 
	        			|| (callParams[2] != null && callParams[2].indexOf(sn) > -1))) {
					top["AUTH_VALID_SN"] = sn;
					top["AUTH_VALID_CHECKMODE"] = "F";
					$.userCheck.fireAction(fieldName, userInfo);
					return;
				}
			}
			//在需要鉴权时候，保存好用户资料，便于后面回调需要
			if(!$.userCheck.userMap) {
				$.userCheck.userMap = $.DataMap();
			}
			$.userCheck.userMap.put(fieldName, userInfo);
			
			var noUserPasswd="false", checkTag="11000";
			
			if (userInfo && userInfo.get("USER_PASSWD")==""){
				if(!$.userCheck.exceptEvent(fieldName, "USER_KEY_NULL", data)){
					return ;
				}
				alert("该号码用户尚未设置密码,请使用客户证件方式进行身份校验!");
				//设置用户密码为空
				noUserPasswd=true;
			}
			if(handlerObj.attr("checkTag")){
				checkTag = handlerObj.attr("checkTag");
			}
			handlerObj.attr("authUser", userInfo.get("USER_ID"));
			handlerObj.attr("noUserPasswd", noUserPasswd);
			
			var param = "&HANDLER=POP_"+fieldName;
			param += "&SERIAL_NUMBER="+sn;
			param += "&IDENTITY_CHECK_TAG="+checkTag;
			param += "&NO_USER_PASSWD="+noUserPasswd;
			param += "&DISABLED_AUTH=false";
			param += "&USER_CHECK=true";
			
			handlerObj.attr("authParams", param);
			//弹出密码认证窗口
			$.userCheck.popAuthCheck(fieldName);
		},
		//弹出认证窗口
		popAuthCheck:function(fieldName){
			
			var param = $("#POP_"+fieldName).attr("authParams") + '&NewDate=' + new Date();
			popupPage("身份校验", "components.auth.AuthCheckNew", "init", param, '', 'c_popup c_popup-half c_popup-half-hasBg', $.userCheck.onUserCheck);
		},
		//身份校验
		onUserCheck:function(){
			// AuthCheckNew.js返回值，用于记录校验的手机号码的id。需要在调用userCheck组件的html增加隐藏域POP_FIELD_NAME。
			var fieldName = $("#POP_FIELD_NAME").val().substring(4); 
			var handlerObj = $("#POP_"+fieldName);
			//客服方式认证，返回数据未1
			if(handlerObj.val() == "1"){
				var cacheSn = handlerObj.attr("cacheSn"); 
				if(cacheSn && cacheSn=="true"){
					top["AUTH_VALID_SN"]=$.trim($("#"+fieldName).val());
					top["AUTH_VALID_CHECKMODE"]="1";
				}
				$.userCheck.fireAction(fieldName);
				return;
			}
			var param = "";	
			//鉴权认证公共入参
			param += "&SERIAL_NUMBER="+$("#"+fieldName).val();
			param += "&NO_USER_PASSWD="+handlerObj.attr("noUserPasswd");
			param += "&USER_ID="+handlerObj.attr("authUser");

			/**
			 * 鉴权认证密码框返回入参
			 * [CHECK_MODE,PSPT_TYPE_CODE,USER_PASSWD,PSPT_ID,SIM_NO,VIP_ID,OWNER_PSPT_ID,IVR_PASS_SUCC,DISABLED_AUTH]
			 */
			param += handlerObj.val();
			$.beginPageLoading("认证校验。。。");
			$.httphandler.submit(null, $.userCheck.clazz, "checkUser", param, 
				function(data){
					$.endPageLoading();
					$.userCheck.afterUserCheck(data, fieldName);
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","认证校验报错！",function(){$.userCheck.exceptEvent(fieldName, "USER_AUTH");}, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","认证校验超时！");
			});
		},
		afterUserCheck:function(data, fieldName){
			if (data.get("RESULT_CODE") !="0"){
				MessageBox.alert("告警提示", data.get("RESULT_INFO"), function(){
					if (data.get("IS_CLOSE") != "1"){
						$.userCheck.popAuthCheck(fieldName);
					}
				});
			}else{
				//如果需要缓存服务号码，则记录下来
				var handlerObj = $("#POP_"+fieldName);
				var cacheSn = handlerObj.attr("cacheSn"); 
				if(cacheSn && cacheSn=="true"){
					top["AUTH_VALID_SN"]=$.trim($("#"+fieldName).val());
					top["AUTH_VALID_CHECKMODE"]=data.get("CHECK_MODE");
				}
				$.userCheck.fireAction(fieldName);
			}
		},
		
		// 获取客服接入号码
		getCallCenterSn:function(){
			var callPhone = null;
			var frame=top.document.getElementById("callcenter"); 
			if (frame && frame.contentWindow && frame.contentWindow.document) { //老版客服
				var callObj = frame.contentWindow.document.getElementById("CALL_PHONE");
				if(callObj){
					callPhone = callObj.value;
				}
			}else if(typeof(eval(window.top.getCustorInfo))=="function"&&typeof(eval(window.top.AiSoftPhone.isTalking))=="function"){ //新版客服
				//获取流水号+主叫号码+被叫号码+受理号码
				callPhone = window.top.getSubscriberInfo().getCALL_SERIAL_NO() 
					+ "JHJ" + window.top.getSubscriberInfo().getCALLERNO_AUTH() 
					+ "JHJ" + window.top.getSubscriberInfo().getCALLEDNO_AUTH() 
					+ "JHJ" + window.top.getSubscriberInfo().getBILL_ID();
			}
			return callPhone;
		},
		
		//加载业务校验后事件
		fireAction:function(fieldName, userInfo){
			//回调业务数据加载服务
			var action=$("#POP_"+fieldName).attr("tradeAction"); 
			if(action && action != ""){
				if(!userInfo && $.userCheck.userMap){
					userInfo = $.userCheck.userMap.get(fieldName);
				}
				try{
					(new Function("var data = arguments[0];"+ action + ";"))(userInfo);
				}catch(e){
					MessageBox.error("错误提示","加载业务校验后信息错误，请检查后重试！");
					return;
				}
			}
			//清理认证框数据，避免提交到后台覆盖业务数据
			$("#POP_"+fieldName).val("");
		},
		//执行例外回调事件
		exceptEvent:function(fieldName, state, data){
			var flag=true;
			var action=$("#POP_"+fieldName).attr("exceptAction"); 
			if(action && action != ""){
				try{
					flag=(new Function("var state = arguments[0]; data = arguments[1]; return "+ action + ";"))(state, data);
				}catch(e){
					MessageBox.error("错误提示","回调自定义错误事件有问题，请检查后重试！");
					return false;
				}
			}
			return flag;
		},
		events:{
			//号码框事件
			onUserCheckInput:function(e, fieldName){
				if(e.keyCode==13 || e.keyCode==108){
					$.userCheck.checkUser(fieldName);
				}
				return true;
			}
		}
	}});
})(Wade);