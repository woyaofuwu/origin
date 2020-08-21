(function($){
	$.extend({
	//提交模块	
	cssubmit:{
		componentId : "MerchSubmitPart" ,			//组件ID
		repeatFlag: false,						//防止重复提交
		dynamicParams : null,					//动态设置参数
		dynamicParamData :$.DataMap(),
		callBackEvent:null,						//提交回调事件
		customizeBtns:null,						//自定义按钮
		tradeData: null,						//业务执行返回结果
		commData:$.DataMap(),							//公用参数
		//----------购物车
		clickButton:null,
		//----------购物车
		
		//绑定业务提交事件
		init:function(){
			$.cssubmit.dynamicParams = "";
			$.cssubmit.clickButton = "submit";
			//----------购物车
			$("#CSSUBMIT_BUTTON").bind("click", "submit", $.cssubmit.bindFucntion);
			$("#ADD_SHOPPING_CART").bind("click", "addShoppingCart", $.cssubmit.bindFucntion);
			$("#CSRESET_BUTTON").bind("click", $.cssubmit.resetTrade);
			//是否禁用提交按钮
			var disabled = $("#CSSUBMIT_BUTTON").attr("disabledBtn") == "true"? true : false;
			$.cssubmit.disabledSubmitBtn(disabled,"submitButton");
			disabled = $("#ADD_SHOPPING_CART").attr("disabledBtn") == "true"? true : false;
			$.cssubmit.disabledSubmitBtn(disabled,"shoppingCartButton");
			//----------购物车
		},
		
		//----------购物车
		bindFucntion:function(buttonType){
			var beforeAction = $("#CSSUBMIT_BUTTON").attr("beforeAction");
			//beforeAction动作如果涉及到后台服务调用，必须是同步方法,否则手动启动业务提交
			$.cssubmit.clickButton = buttonType.data;
			if(beforeAction && beforeAction != ""){
				var validFlag =  (new Function("return " + beforeAction + ";"))();
				//如果返回false，停止业务提交
				if(typeof(validFlag) == "undefined" || !validFlag){
					return false;
				}
			}
			$.cssubmit.submitTrade();
		},
		//----------购物车
				
		//设置单个参数
		setParam : function(key, value){
			($.cssubmit.dynamicParamData).put(key, value);
		},
		
		//设置参数,每次设置，都会覆盖之前参数
		addParam : function(paramStr){
			$.cssubmit.dynamicParams = paramStr;
		},
		//清除所有动态参数
		clearParam:function(){
			$.cssubmit.dynamicParams = null;
			$.cssubmit.dynamicParamData.clear();
		},
		//重置业务
		resetTrade : function(){
			var resetAction=$("#CSRESET_BUTTON").attr("resetAction"); 
			if(!resetAction || resetAction.length == 0){
				var href = window.location.href;
				if(href){
					if(href.lastIndexOf("#nogo") == href.length-5){
						href = href.substring(0, href.length-5);
					}
					window.location.href = href;
				}
			}else{
				(new Function(resetAction + ";"))();
			}
		},
		
		/**
		* 集团业务页面流提交
		*/
		flowSubmit : function(){
			var flowSubmitData = window.getFlow().getSubmitData();
			$.cssubmit.addParam(flowSubmitData);
			$.cssubmit.bindCallBackEvent($.cssubmit.callBack.grpCallBack);
			$("#CSSUBMIT_BUTTON").trigger("click");
		},

		//业务提交规则校验
		submitTrade : function(){
			if($.auth && $.auth.getAuthData()){
				var user=$.auth.getAuthData().get("USER_INFO");
				if(user && user.get("SERIAL_NUMBER")){
					if(user.get("SERIAL_NUMBER") != $.trim($("#AUTH_SERIAL_NUMBER").val())){
						//MessageBox.alert("告警提示", "认证校验的服务号码与当前输入的服务号码不一致，请确认");
						//return ;
					}
				}
			}			
			
			var cancelRule = $("#CSSUBMIT_BUTTON").attr("cancelRule");
			var isGrp = $("#CSSUBMIT_BUTTON").attr("isGrp");
			if(!cancelRule || cancelRule=="") 	cancelRule = "false";
			if(!isGrp || isGrp=="") 	isGrp = "false";
			//调用业务规则校验
			if($.tradeCheck && cancelRule == "false" && isGrp=="false"){
				$.tradeCheck.checkTrade(1 , $.cssubmit.checkFee);
			}else{
				//如果没有导入业务规则校验，默认为不需要执行
				$.cssubmit.checkFee();
			}
		},
		//核对费用
		checkFee:function(){
			//----------购物车
			var buttonType = $.cssubmit.clickButton;
			if($.feeMgr&&buttonType!="addShoppingCart"){
				//返回true则自动提交业务
				if($.feeMgr.checkFee($.cssubmit.registerTrade, $.cssubmit.cancelAction)){
					$.cssubmit.registerTrade();
				}
			}else if($.feeMgr&&buttonType=="addShoppingCart"){
				//营销活动有费用的加购物车，没有检验会造成缺少费用参数，导致后台报错
//				$.cssubmit.registerTrade();
				if($.feeMgr.checkFee($.cssubmit.registerTrade, $.cssubmit.cancelAction)){
					$.cssubmit.registerTrade();
				}				
			}//--------购物车
			else{
				$.cssubmit.registerTrade();
			}
		},
		//业务登记
		registerTrade : function(noFeeFlag){
			var submitFalg = true;
			var area = $("#CSSUBMIT_BUTTON").attr("area");
			var callBean = $("#CSSUBMIT_BUTTON").attr("callBean");
			var listener = $("#CSSUBMIT_BUTTON").attr("listener");
			var params = $("#CSSUBMIT_BUTTON").attr("params");
			var refreshPart = $("#CSSUBMIT_BUTTON").attr("refreshPart");
			
			//禁用提交按钮
			$.cssubmit.disabledSubmitBtn(true);
						
			if(!area || area=="") 			area = "MerchAuthPart,AuthPart";
			if(!callBean || callBean=="") 	submitFalg = false;
			if(!listener || listener=="") 	listener = "onTradeSubmit";
			if(!refreshPart) 	refreshPart = null;
			if(!params) 		params = "";
			
			//----------购物车
			var submitType = $.cssubmit.clickButton;
			$.cssubmit.setParam("SUBMIT_TYPE",submitType);
			$.cssubmit.setParam("SUBMIT_SOURCE","CRM_PAGE");
			//------------购物车
			
			//加载动态字符串参数
			if($.cssubmit.dynamicParams && ($.cssubmit.dynamicParams).length>0){
				params += $.cssubmit.dynamicParams;
			}
			//加载动态键值对参数
			if($.cssubmit.dynamicParamData && ($.cssubmit.dynamicParamData).length>0){
				($.cssubmit.dynamicParamData).eachKey(function(key,index,totalcount){
					params += "&"+key+"="+($.cssubmit.dynamicParamData).get(key);
				});
			}
			
			//设置认证方式.如果无认证组件设置CHECK_MODE=Z，如果免认证则CHECK_MODE=F,需要更新台账表PROCESS_TAG_SET第20位
			var checkMode="Z";
			if($.auth && $.auth.getAuthData()){
				var authData = $.auth.getAuthData();
				if(authData && authData.containsKey("CHECK_MODE")){
					checkMode= authData.get("CHECK_MODE");
				}else{
					checkMode="F";
				}
				params += "&CHECK_MODE="+checkMode;
				//增加客服参数传值
				if($("#AUTH_SUBMIT_BTN").attr("opCode")!=""){
					params += "&OP_CODE="+$("#AUTH_SUBMIT_BTN").attr("opCode");
				}
			}
			if(top.document.getElementById("staffId") && top.$){
				params += "&NGBOSS_STAFF_ID="+top.$("#staffId").val();
			}
			
			if($.cssubmit.repeatFlag){
				MessageBox.alert("告警提示", "业务已经在受理中，请不要重复提交业务！");
				return ;
			}
			//防止重复提交
			$.cssubmit.repeatFlag = true;
			
			//------购物车
			if(submitType=="submit") {
			   $.beginPageLoading("业务受理中。。。");
			}else if(submitType=="addShoppingCart") {
			   $.beginPageLoading("加入购物车过程中。。。");
			}
			//--------购物车
			
			if(!submitFalg){
				//普通方式提交
				$.ajax.submit(area, listener, params,refreshPart,
					$.cssubmit.callBack.beforePay,
						$.cssubmit.callBack.errorFunc,
							$.cssubmit.callBack.timeoutFunc);				
			}else{
				//特殊方式提交
				hhSubmit(area, callBean, listener, params,
					$.cssubmit.callBack.beforePay,
						$.cssubmit.callBack.errorFunc,
							$.cssubmit.callBack.timeoutFunc);				
			}
		},
		//设定提交成功回调事件
		bindCallBackEvent:function(func){
			$.cssubmit.callBackEvent = func;
		},
		//打印调用事件
		printTrade:function(){
			//默认执行方法
			if($.cssubmit.tradeData && $.printMgr){
				$.printMgr.printTrade($.cssubmit.tradeData);
			}
		},
		

		//弹出电子发票开具推送信息设置的对话框
		openKJConf:function(){
			//弹出设置页面
			var userId=null;
			var eparchyCode=null;
			if($.auth && $.auth.getAuthData()){
				var user=$.auth.getAuthData().get("USER_INFO");
				if(user){
					userId=user.get("USER_ID");
					eparchyCode=user.get("EPARCHY_CODE"); //取用户路由，有可能是异地受理
				}
			} else if ($.cssubmit.tradeData && $.cssubmit.tradeData.containsKey("USER_ID")) {
				userId=$.cssubmit.tradeData.get("USER_ID");
				eparchyCode=$.cssubmit.tradeData.get("EPARCHY_CODE"); //取用户路由，有可能是异地受理
			}				
			if (!userId || userId==null || userId.length==0) {
				MessageBox.alert(111)
				MessageBox.alert("告警提示","未获取到用户信息！");
				return ;
			}
			var tradetypecode=$.cssubmit.commData.get("TRADE_TYPE_CODE");
			
			if (!eparchyCode || eparchyCode.length<2){
				var dataTmp = null;
				if($.cssubmit.tradeData)
				{
				if($.cssubmit.tradeData instanceof $.DatasetList){
					dataTmp = $.cssubmit.tradeData.get(0);
				}else if($.cssubmit.tradeData instanceof $.DataMap){
					dataTmp = $.cssubmit.tradeData;
				}				
				if(dataTmp.containsKey("DB_SOURCE")){
					eparchyCode = dataTmp.get("DB_SOURCE");
				}
				}
			}
			
			var eparchyCodeparamStr = "";
			if (eparchyCode && eparchyCode.length>0) {
				eparchyCodeparamStr = "&EPARCHY_CODE="+eparchyCode+"&ROUTE_EPARCHY_CODE="+eparchyCode;
			}
			var param = '&USER_ID=' +userId+eparchyCodeparamStr+'&TRADE_TYPE_CODE='+tradetypecode+'&NEW_DATE='+new Date();
			//popupPage('changeuserinfo.ModifyEPostInfo', 'onInitTrade',param , '电子发票打印设置', 1000, 1380);
			popupPage("电子发票打印设置", "changeuserinfo.ModifyEPostInfoNew", "onInitTrade", param, "/order/iorder", "c_popup c_popup-full");
			//openNav("开具设置", "changeuserinfo.ModifyEPostInfo", "onInitTrade", "&USER_ID=" +userId+"&EPARCHY_CODE="+eparchyCode+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+tradetypecode);		
		},
		//集团弹出设置页面	
		openGrpKJConf:function(){
			
			if ($.cssubmit.commData && $.cssubmit.commData.containsKey("Send_GROUP_ID")) {
				sendGroupId=$.cssubmit.commData.get("Send_GROUP_ID");
				
			}		
			if (!sendGroupId || sendGroupId==null || sendGroupId.length==0) {
				MessageBox.alert("告警提示","未获取到集团编码！");
				return ;
			}
			var param = '&GROUP_ID=' +sendGroupId;
			//popupPage('', '',param , '电子发票打印设置', 870,1380);
			popupDialog("电子发票打印设置", "group.creategroupacct.SetGrpElecInvoice", "initial", param, subsys_cfg.order,"870px","1380px");
			//openNav("开具设置", "group.creategroupacct.SetGrpElecInvoice", "initial", "&GROUP_ID=" +sendGroupId);		
		},
		
		//获取业务类型是否支持开具电子发票推送信息设置
		getTradeTypeKjConf: function(tradeTypeCode){				
				var param = "&ACTION=GET_TRADE_TYPE_KJ_CONF";
				param += "&TRADE_TYPE_CODE="+tradeTypeCode;
				$.beginPageLoading("加载业务类型是否支持开具电子发票推送信息设置数据。。。");			
				ajaxSubmit(null, null, param, $.cssubmit.componentId, 
							function(data){
								$.endPageLoading();
								if(data){
									$.cssubmit.commData.put("CAN_SET_EPRINTCEPT_CONF", data.get("CAN_SET_EPRINTCEPT_CONF"));
									$.cssubmit.commData.put("TRADE_TYPE_CODE", tradeTypeCode);
								}
							},
							function(code, info, detail){
								$.endPageLoading();
								MessageBox.error("错误提示","加载业务类型是否支持开具电子发票推送信息设置数据报错！", null, null, info, detail);
							},/*function(){
								$.endPageLoading();
								MessageBox.alert("错误提示","加载业务类型是否支持开具电子发票推送信息设置数据超时！");
							}, */{
								"async" : false  //必须第七个参数
							});	
						
		},
		
		//回调方法
		callBack:{
			//支付前校验
			beforePay:function(data){
				$.cssubmit.tradeData = data;
				var tradeData;
				if(data instanceof $.DatasetList){
					tradeData = data.get(0);
				}else if(data instanceof $.DataMap){
					tradeData = data;
				}
				
				//购物车不需要弹出支付中心的界面
				var submitType = $.cssubmit.clickButton;
				if(submitType=="addShoppingCart"){
					$.cssubmit.callBack.successFunc(data);
					return;
				}
				
				// 集团有些业务前台有费用信息 但是后台处理了只入了挂账信息， 这里先提前校验下，没有入fee_sub表就不跳转支付
				var orderId = tradeData.get("ORDER_ID");
				if(typeof(orderId) !="undefined" && orderId !="" && !isNaN(orderId)){
					var checkParam = "&ACTION=CHECKHASFEE";
					checkParam += "&ORDER_ID="+tradeData.get("ORDER_ID");
					ajaxSubmit(null, null, checkParam, $.cssubmit.componentId, 
							function(ajaxData){
										if(ajaxData && ajaxData.get("ORDER_HAS_FEE")=="true")//有入费用表
										{
											$.cssubmit.callBack.payFunc(data);
										}else{
											$.cssubmit.callBack.successFunc(data);
											return;
										}
											
									},function(code, info, detail){
										$.endPageLoading();
										MessageBox.error("错误提示","获取费用信息异常！", null, null, info, detail);
					},{"async":false});
				}else{
					$.cssubmit.callBack.successFunc(data);
				}
			},
			//支付
			payFunc:function(data){
				var tradeData;
				if(data instanceof $.DatasetList){
					tradeData = data.get(0);
				}else if(data instanceof $.DataMap){
					tradeData = data;
				}	
				$.beginPageLoading("正在跳转支付。。。");
				var param = "&ACTION=PAYMENT";
				
				if($.auth && $.auth.getAuthData()){
						var user=$.auth.getAuthData().get("USER_INFO");
						if(user && user.get("SERIAL_NUMBER")){
							tradeData.put("ROUTE_EPARCHY_CODE",user.get("EPARCHY_CODE"));
							tradeData.put("SERIAL_NUMBER",user.get("SERIAL_NUMBER"));
						}
				}else if(tradeData.get("SERIAL_NUMBER")){
						//
					tradeData.put("SERIAL_NUMBER",tradeData.get("SERIAL_NUMBER"));
				}else{//开户无auth组件，只能从页面上取
					tradeData.put("SERIAL_NUMBER",$("#SERIAL_NUMBER").val());
				}
				tradeData.eachKey(function(key,index,totalcount){
					if(key !="EPOST_DATA" && key !="PRINT_INFO")// 这种数据没必要传到后台
					{
						param += "&"+key+"="+tradeData.get(key);
					}
				});
				ajaxSubmit(null, null, param, $.cssubmit.componentId, 
						function(paydata){
							var orderId = paydata.get("ORDER_ID");
							var peerOrderId = paydata.get("PEER_ORDER_ID");
							var param ="&ORDER_ID="+orderId+"&PEER_ORDER_ID="+peerOrderId;
							//var returnVal = window.showModalDialog(openUrl,'','dialogWidth=800px;dialogHeight=400px;location:no;');
							//var popupId= $.popupPage("pay.order.PayMain","queryOrderInfo",param+'&PARENT_EVENT_ID=_PAY_FEE_ID','支付收银',800,400,'_PAY_FEE_ID','',subsys_cfg.payment,false,false);
                            $.endPageLoading();
							var popupId= popupPage("支付收银", "pay.order.PayMain","queryOrderInfo", param+'&PARENT_EVENT_ID=_PAY_FEE_ID', subsys_cfg.payment, "c_popup c_popup-full", null, null);
							$('#_PAY_FEE_ID').val(popupId);
						},
						function(code, info, detail){
							$.endPageLoading();
							MessageBox.error("支付失败3","请通过未支付订单管理界面重新支付。",null, null, info, detail);
						},{async:false});	
			},
			//支付成功后处理
			payBackFunc:function(payData){
				if(typeof(payData) !="undefined"){
					var payresultMap = $.DataMap(payData);
					var state = payresultMap.get("STATE");
					if(state =="2"){
						$.beginPageLoading("支付成功，正在修改订单状态。。。");
						var tradeData ;
						var data = $.cssubmit.tradeData;
						if(data instanceof $.DatasetList){
							tradeData = data.get(0);
						}else if(data instanceof $.DataMap){
							tradeData = data;
						}	
						var param = "&ACTION=PAY_BACK";
						tradeData.eachKey(function(key,index,totalcount){
							if(key !="EPOST_DATA" && key !="PRINT_INFO")// 这种数据没必要传到后台
							{
								param += "&"+key+"="+tradeData.get(key);
							}
						});
						param += "&PAY_DETAIL="+payresultMap.get("PAY_DETAIL");
						if($("#CSSUBMIT_BUTTON").attr("isGrp")=="true"){
							param += "&IS_GROUP=TRUE";
						}else{
							param += "&IS_GROUP=FALSE";
						}
						var tradeTypeCode="";
						if($.auth){
							tradeTypeCode= $("#TRADE_TYPE_CODE").val();
						}else if( tradeData && tradeData.containsKey("ORDER_TYPE_CODE") ){
							tradeTypeCode = tradeData.get("ORDER_TYPE_CODE");
						}else if( tradeData && tradeData.containsKey("TRADE_TYPE_CODE") ){
							tradeTypeCode = tradeData.get("TRADE_TYPE_CODE");
						}
						param += "&TRADE_TYPE_CODE="+tradeTypeCode;
						ajaxSubmit(null, null, param, $.cssubmit.componentId,
								function(result){
									if(result && result.get("RESULT")=="SUCCESS"){
										$.cssubmit.callBack.successFunc(tradeData);
									}else{
										MessageBox.alert("支付成功后修改订单失败！");
									}
								},
								function(code, info, detail){
									$.endPageLoading();
									if($.auth){
										MessageBox.error("错误提示",$.auth.reflushPage, null, info, detail);
									}
									else{
										MessageBox.error("错误提示",null, null, info, detail);
									}
						});	
					}else{
						$.endPageLoading();
						MessageBox.error("支付失败","请通过未支付订单管理界面重新支付。",null, null, null, null);
					}			
				}else{
						$.endPageLoading();
						MessageBox.error("支付失败2","请通过未支付订单管理界面重新支付。",null, null, null, null);
				}
			},
			//提交成功回调
			successFunc:function(data){
				$.cssubmit.tradeData = data;		//保存业务受理返回结果
								
				$.cssubmit.clearParam();
				$.cssubmit.repeatFlag = false;
				window.onClose = function(){
					if($.cssubmit){
						if(!$.cssubmit.hasPrintAll()){
							MessageBox.alert("您还有票据未打印，请打印完后办理其他业务!");
							return false;
						}
					}	
					if(top.clearFee && top.getFee){
						if($.feeMgr){
							// 取当前费用信息
							$.feeMgr.cacheFee = top.getFee();
							//费用缓冲好以后，修改为非激活状态
							$.feeMgr.activeFlag = false;
						}
						// 清空费用组件
						top.clearFee();
					}
								
				};
				window.onUnActive = function(){
					
					if($.cssubmit){
						if(!$.cssubmit.hasPrintAll()){
							MessageBox.alert("您还有票据未打印，请打印完后办理其他业务!");
							return false;
						}
					}
					
					if(top.clearFee && top.getFee){
						if($.feeMgr){
							// 取当前费用信息
							$.feeMgr.cacheFee = top.getFee();
							//费用缓冲好以后，修改为非激活状态
							$.feeMgr.activeFlag = false;
						}
						// 清空费用组件
						top.clearFee();
					}
				};
				if(!$.cssubmit.commData) $.cssubmit.commData=$.DataMap();
				
				//如果是集团提交，直接按照给定的方式去回调，不需要走后面逻辑
				if($("#CSSUBMIT_BUTTON").attr("isGrp")=="true"){
					$.endPageLoading();
					$.cssubmit.callBack.grpCallBack(data);
					return;
				}
				//如果有设置回调事件，则执行自定义事件，转为人工控制
				if($.cssubmit.callBackEvent){
					$.endPageLoading();
					$.cssubmit.callBackEvent(data);
					return ;
				}
				
				var isPrint = false;			//是否显示打印按钮	
				var isTicketPrint = false;		//是否打印票据
				var isPrivPrint = true;			//权限打印标识(控制业务受理单)
				//如果没有回调事件，继续按照默认进行业务受理信息展示
				var title = "业务受理成功";
				//---------购物车
				if($.cssubmit.clickButton=="addShoppingCart"){
					$.endPageLoading();
					title = "成功加入购物车";
					$.cssubmit.showMessage("success", title, content, false);
					return;
				}
				//---------购物车
				var content = "点【确定】继续业务受理。";
				var tradeData;
				if(data instanceof $.DatasetList){
					tradeData = data.get(0);
				}else if(data instanceof $.DataMap){
					tradeData = data;
				}	
				if(tradeData && tradeData.containsKey("ORDER_ID")){
					content = "客户订单标识：" + tradeData.get("ORDER_ID") + "<br/>点【确定】继续业务受理。";
				}
				//如果打印权限控制
				if($("#CSSUBMIT_BUTTON").attr("isPrint") == "false"){
					$.endPageLoading();
					$.cssubmit.showMessage("success", title, content, false);
					return ;
				}
				//如果有自定义打印事件，不加载打印数据
				if($.printMgr && $.printMgr.printEvent){
					$.endPageLoading();
					if($.os.phone) {
						MessageBox.alert("手机版本："+content);
                        $.cssubmit.showMessage("success", title, content + ".", false);
					} else {
                        $.cssubmit.showMessage("success", title, content + ".", true);
                    }
					return ;
				}
				var tradeTypeCode="";
				if($.auth){
					tradeTypeCode= $("#TRADE_TYPE_CODE").val();
				}else if( tradeData && tradeData.containsKey("ORDER_TYPE_CODE") ){
					tradeTypeCode = tradeData.get("ORDER_TYPE_CODE");
				}else if( tradeData && tradeData.containsKey("TRADE_TYPE_CODE") ){
					tradeTypeCode = tradeData.get("TRADE_TYPE_CODE");
				}
				tradeData.put("TRADE_TYPE_CODE", tradeTypeCode);
				
				$.cssubmit.loadPrintTradeData(tradeData, title, content);		
			},
			errorFunc:function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","业务受理失败！", $.cssubmit.cancelAction, null, info, detail);	
			},
			timeoutFunc:function(){
				$.endPageLoading();
				MessageBox.alert("告警提示", "业务提交超时！", $.cssubmit.cancelAction);
				return false;
			},
			
			// 集团业务回调函数
			grpCallBack : function(data){
				var isPrint = false;// 打印标识
					
				//如果没有回调事件，继续按照默认进行业务受理信息展示
				var content = "点【确定】继续业务受理。"
				
				var tradeData;
				if(data instanceof $.DatasetList && data.length > 0)
				{
					tradeData = data.get(0);
				}
				else if(data instanceof $.DataMap){
					tradeData = data;
				}
				if(tradeData) {
					orderId = tradeData.get("ORDER_ID") + "";
					content = "业务订单号：" + orderId + "<br/>" + "点【确定】继续业务受理。";
					
					var printInfos=tradeData;
					if(printInfos && printInfos.containsKey("PRINT_INFO")){
						isPrint = true;
						$.printMgr.setPrintData(printInfos.get("PRINT_INFO"));
					}
					var bothPrint = $("#CSSUBMIT_BUTTON").attr("bothPrint")=="true"? true : false;
					var edocPrint = $("#CSSUBMIT_BUTTON").attr("edocPrint")=="true"? true : false;
					if(isPrint && (bothPrint || edocPrint) && printInfos.containsKey("CNOTE_DATA")){
						$.cssubmit.confElecAcceptBill();						//生成电子工单按钮
						$.printMgr.setElcNoteData(printInfos.get("CNOTE_DATA"));	//设置电子工单数据
					}
					//获取groupId
					if(printInfos.containsKey("GROUP_ID")){
						groupId=printInfos.get("GROUP_ID");
						$.cssubmit.commData.put("Send_GROUP_ID", groupId);
						//是否打印集团电子发票标识
						if(printInfos){						
							if((printInfos.get("CUST_TYPE") =="GRP")){
								$.printMgr.userRecptSvcConf.custType = "GRP";
							}
							else{
								$.printMgr.userRecptSvcConf.custType = "MEB";
							}
							if( printInfos.containsKey("EPOST_DATA")){//电子发票推送信息
								if(printInfos.get("EPOST_DATA").containsKey("IS_ERECEPT")){
									$.printMgr.userRecptSvcConf.sendWay = printInfos.get("EPOST_DATA").get("RECEIVER_SENDWAY");
									$.printMgr.userRecptSvcConf.receiverMobile = printInfos.get("EPOST_DATA").get("RECEIVER_MOBILE");
									$.printMgr.userRecptSvcConf.receiverEmail = printInfos.get("EPOST_DATA").get("RECEIVER_EMAIL");
									$.printMgr.userRecptSvcConf.hadGot = true;
									$.printMgr.userRecptSvcConf.isERecept = "TRUE";
								}
							}
						}
					}
					
				}
				$.cssubmit.showMessage("success", "业务受理成功", content, isPrint);
			}
		},
		loadPrintTradeData: function(tradeData, title, content){
			//登记成功以后，可能返回的地州编码键值是DB_SOURCE，这里做一下特殊处理
			if(tradeData.containsKey("DB_SOURCE")){
				tradeData.put("EPARCHY_CODE", tradeData.get("DB_SOURCE"));
				tradeData.removeKey("DB_SOURCE");
			}
			//拼接打印查询入参
			var param = "&ACTION=PRINT";
			tradeData.eachKey(function(key,index,totalcount){
				param += "&"+key+"="+tradeData.get(key);
			});
			
			if($.printMgr.printParam && $.printMgr.printParam.length){
				param += "&PRINT_PARAMS="+encodeURIComponent($.printMgr.printParam.toString());
			}
			if(top.document.getElementById("staffId") && top.$){
				param += "&NGBOSS_STAFF_ID="+top.$("#staffId").val();
			}
			//发票打印
			var isTicketPrint=false;
			if($.feeMgr && $.feeMgr.getTotalFee()!=0){
				param += "&PRINT_TICKET=1";
				isTicketPrint=true;
			}else{
				if(tradeData.get("FEE_STATE") =="1"){
					param += "&PRINT_TICKET=1";
					isTicketPrint=true;
				}
			}
			//电子工单打印
			var bothPrint = $("#CSSUBMIT_BUTTON").attr("bothPrint")=="true"? true : false;
			var edocPrint = $("#CSSUBMIT_BUTTON").attr("edocPrint")=="true"? true : false;
			
			if(edocPrint || bothPrint){
				param += "&PRINT_EDOCUMENT=1";
			}
			var isPrint = false;
			ajaxSubmit(null, null, param, $.cssubmit.componentId, 
				function(prtdata){
					$.endPageLoading();
					
					if(prtdata && prtdata.containsKey("PRINT_TAG")){
						isPrint = (prtdata.get("PRINT_TAG")== "1")?true:false;
					}
					var tradePrint = true;
					if(isPrint){
						//拥有电子工单权限且有电子工单数据才显示打印电子工单按钮
						if((bothPrint || edocPrint) && prtdata.containsKey("CNOTE_DATA")){
							$.cssubmit.confElecAcceptBill();						//生成电子工单按钮
							$.printMgr.setElcNoteData(prtdata.get("CNOTE_DATA"));	//设置电子工单数据
						}
						//如果仅仅是只显示电子工单按钮，则关闭打印按钮
						if(edocPrint && !bothPrint){
							tradePrint = false;
						}
					}
					
					//设置打印数据
					if(prtdata.containsKey("PRINT_DATA")){
						//设置打印数据
						$.printMgr.setPrintData(prtdata.get("PRINT_DATA"));
						//公共参数数据
						$.printMgr.params.put("ORDER_ID", tradeData.get("ORDER_ID"));
						$.printMgr.params.put("EPARCHY_CODE", tradeData.get("EPARCHY_CODE"));
					}
					if(isPrint){
						//有免打印权限不展示取消按钮 !closeNoPrint &&
					    var closeNoPrint = $("#CSSUBMIT_BUTTON").attr("closeNoPrint")=="true"? true : false;
						var mustPrintTag = $("#CSSUBMIT_BUTTON").attr("mustPrintTag")=="true"? true : false;
						if(mustPrintTag){
							if(!closeNoPrint && $.cssubmit.hasDoc()){//有免填单打印才生成取消按钮
						    	//$.cssubmit.addCancelOrderButton();
						    	title = "业务受理单据打印";
						    }
						}
					}
						
					//显示业务受理成功提示框
					if($.os.phone) {
                        $.cssubmit.showMessage("success", title, content,false);
					}
					else {
                        $.cssubmit.showMessage("success", title, content, (isPrint&&tradePrint || isTicketPrint));
                    }
				},
				function(code, info, detail){
					//记录错误信息
					var param1 = "&ACTION=ERROR_LOG";
					param1 += "&ERROR_CODE="+code;
					param1 += "&ERROR_INFO="+info;
					param1 += "&ERROR_DETAIL="+detail;
					tradeData.eachKey(function(key,index,totalcount){
						param1 += "&"+key+"="+tradeData.get(key);
					});
					ajaxSubmit(null, null, param1, $.cssubmit.componentId, 
							function(data){
								$.endPageLoading();
								if(data && data.containsKey("PRINT_TAG")){
									isPrint = (data.get("PRINT_TAG")== "1")?true:false;
								}
								if(isPrint){
									content = content + "<br/>" + "打印单据解析失败，请通过发票补打和业务受理单补打界面进行补打单据，后台维护人员将尽快解决单据解析失败的问题。";
								}
								$.cssubmit.showMessage("success", title, content, false);
							},
							function(code, info, detail){
								$.endPageLoading();
								$.cssubmit.showMessage("success", title, content + "。。。", false);
							},function(){
								$.endPageLoading();
								$.cssubmit.showMessage("success", title, content + "。。。", false);
							});
				},function(){
					$.endPageLoading();
					$.cssubmit.showMessage("success", title, content + "。。", false);
			});	
		},
		
		//电子工单按钮配置
		confElecAcceptBill:function(){
			$.cssubmit.bindCustomizeBtn({
	 			"name":"打印电子工单",
	 			"icon":"print",
	 			"fn":$.printMgr.loadElecAcceptBill
		 	});
		},
		addCancelOrderButton:function(){
			$.cssubmit.bindCustomizeBtn({
	 			"name":"取消",
	 			"icon":"cancel",
	 			"fn":$.cssubmit.noPrintCancelOrder
		 	});
		},
		noPrintCancelOrder:function(){
			var data = $.cssubmit.tradeData;
			var tradeData;
			if(data instanceof $.DatasetList){
				tradeData = data.get(0);
			}else if(data instanceof $.DataMap){
				tradeData = data;
			}	
			var param = "&ACTION=CANCEL_ORDER";
			tradeData.eachKey(function(key,index,totalcount){
				param += "&"+key+"="+tradeData.get(key);
			});
			ajaxSubmit(null, null, param, $.cssubmit.componentId, 
				function(result){
				 	if(result && result.length && result.get("RESULT_CODE")=='1'){
				 		MessageBox.alert("提示","取消订单成功！",function(btn){
							if(btn=="ok")
							{
								$.cssubmit.closeMessage(true,true);
							}
						});
				 	}else{
				 		MessageBox.alert("取消订单失败！");
				 	}
				},
				function(error_code,error_info,derror)
				{
					$.MessageBox.error(error_code,error_info);
					$.endPageLoading();
				});	
		},
		noPrintExcuteOrder:function(){
			var data = $.cssubmit.tradeData;
			var tradeData;
			if(data instanceof $.DatasetList){
				tradeData = data.get(0);
			}else if(data instanceof $.DataMap){
				tradeData = data;
			}	
			var param = "&ACTION=NO_PRINT_EXCUTE";
			tradeData.eachKey(function(key,index,totalcount){
				param += "&"+key+"="+tradeData.get(key);
			});
			ajaxSubmit(null, null, param, $.cssubmit.componentId, 
				function(paydata){
				},
				function(error_code,error_info,derror)
				{
					$.MessageBox.error(error_code,error_info);
					$.endPageLoading();
				});	
		},
		
		//取消动作,解除提交禁用或配置参数
		cancelAction:function(){
			$.cssubmit.clearParam();
			$.cssubmit.disabledSubmitBtn(false);
			$.cssubmit.repeatFlag = false;
			
			// 页面流按钮
			if($("#bnext").length){
				$("#bnext").removeAttr("disabled");
			}
		},
		
		//禁用提交按钮
		//--------购物车
		disabledSubmitBtn:function(flag,buttonType){
		    if(buttonType){
			    var button = $("#CSSUBMIT_BUTTON");
			    if(buttonType=="shoppingCartButton"){
			       button = $("#ADD_SHOPPING_CART");
			    }
				$.cssubmit.disabledButton(flag,button);
			}else{
			    $.cssubmit.disabledButton(flag,$("#CSSUBMIT_BUTTON"));
			    if($("#ADD_SHOPPING_CART")){
			       $.cssubmit.disabledButton(flag,$("#ADD_SHOPPING_CART"));
			    }
			}
		},
		disabledButton:function(flag,button){
		   if(!button.length){
	          return;
		   }
		   if(flag == true){
			  button.attr("disabled", true).addClass("e_dis");
		   }else{
			  button.attr("disabled", false).removeClass("e_dis");
		   }
		},
		//------购物车
		//取消打印
		disabledPrint:function(flag){
			if(!$("#CSSUBMIT_BUTTON").length){
				return ;
			}
			if(flag == true){
				$("#CSSUBMIT_BUTTON").attr("isPrint", "false");
			}else{
				$("#CSSUBMIT_BUTTON").attr("isPrint", "true");
			}
		},
		
		//业务弹出框按钮事件
		closeMessage : function(succFlag,checkPrintFlag) {
			
			if(!succFlag){
				$.cssubmit.cancelAction();
				return;
			}
			var isGroup = false;
			if($("#CSSUBMIT_BUTTON").attr("isGrp")=="true"){
				isGroup = true;
			}
			var closeNoPrint = $("#CSSUBMIT_BUTTON").attr("closeNoPrint")=="true"? true : false;
			var mustPrintTag = $("#CSSUBMIT_BUTTON").attr("mustPrintTag")=="true"? true : false;
			if(typeof(checkPrintFlag) =="undefined"){checkPrintFlag=false;}
			//此地 先判断必须打印开关有没有开，再判断业务是否可以不打印
			if($.printMgr){
				var infos = $.printMgr.getPrintData();//有打印数据才判断
				if(infos && infos.length){
					if(mustPrintTag && !isGroup){
						if(!checkPrintFlag){
							if(!closeNoPrint){
								if(!$.cssubmit.hasPrintAllDoc()){
									MessageBox.alert("提示","您还有票据未打印，请打印完后办理其他业务!！");
									return;
								}
							}else{
								var infos = $.printMgr.getPrintData();
								if(infos && infos.length){
									$.cssubmit.noPrintExcuteOrder();
								}
							}
						}
					}
				}
			}
			if($("#SUBMIT_MSG_PANEL").length){
				$("#SUBMIT_MSG_PANEL").remove();
			}
			
			//清除费用数据
			if($.feeMgr){
				$.feeMgr.clearFee();
			}
			
			var buttonType = $.cssubmit.clickButton;
			var affirmAction = $("#CSSUBMIT_BUTTON").attr("affirmAction");
			//如果有确认动作，则执行确认事件，否则刷新
			if(affirmAction && affirmAction != "" && "addShoppingCart"==buttonType){
//				(new Function("return " + affirmAction + ";"))();
				(new Function("var type = arguments[0];return " + affirmAction + ";"))(buttonType);
			}else{
				var href = window.location.href;
				if(href){
					if(href.lastIndexOf("#nogo") == href.length-5){
						href = href.substring(0, href.length-5);
					}
					var url = href.substring(0, href.indexOf("?"));
					var reqParam = href.substr(href.indexOf("?")+1);
					
					var paramObj = $.params.load(reqParam);
					paramObj.remove("SERIAL_NUMBER");
					paramObj.remove("DISABLED_AUTH");
					paramObj.remove("AUTO_AUTH");
					//去掉从首页推荐，或者热点过来的参数
                    paramObj.remove("isHot");
                    paramObj.remove("isRec");
                    paramObj.remove("offerCode");
					var param = paramObj.toString();
					param += "&AUTO_AUTH=false";   ////受理成功以后，返回页面界面，禁用之前逻辑中的自动刷新
					window.location.href = url+"?"+param;
															
				}
				
				//详情页面受理成功，点击确定按钮关闭详情页面
				if(affirmAction && affirmAction != ""){
					(new Function("var type = arguments[0];return " + affirmAction + ";"))(buttonType);
				}
			}
		},
		/**
		 * 是否打印了全部免填单
		 */
		hasPrintAllDoc:function(){
			var finishPrinted = false;
			var data = $.cssubmit.tradeData;
			var tradeData;
			if(data instanceof $.DatasetList){
				tradeData = data.get(0);
			}else if(data instanceof $.DataMap){
				tradeData = data;
			}
			if($.printMgr){
				var infos = $.printMgr.getPrintData();
				if(!infos || infos.length == 0){
					return true;
				}
				var param = "&ACTION=HAS_PRINT_ALL";
				tradeData.eachKey(function(key,index,totalcount){
					param += "&"+key+"="+tradeData.get(key);
				});
				ajaxSubmit(null, null, param, $.cssubmit.componentId, 
					function(printdata){
						if(printdata && printdata.get("PRINT_ALL")=="true"){
							finishPrinted = true;
						}
					},
					function(error_code,error_info,derror)
					{
						$.MessageBox.error(error_code,error_info);
						$.endPageLoading();
					},{"async" : false});
			}	
		    return 	finishPrinted;
		},
		/**
		 * 判断本次业务是否有免填单
		 */
		hasDoc:function(){
			var hasDoc = false;
			if($.printMgr){
				var infos = $.printMgr.getPrintData();
				if(!infos || infos.length == 0){
					return false;
				}
				infos.each(function(info, idx, total){
					var printType = info.get("TYPE");
					if( printType.indexOf("03")>-1){
						hasDoc = true;
					}
				});
			}
			return hasDoc;
		},
		/**判断是否已经打印了所有的必须强制打印的发票，收据，免填单
		 * 
		 */
		hasPrintAll : function() {
			
			if(!$("#CSSUBMIT_BUTTON").length){
				return true ;
			}
			var finishPrinted = true;
			var ticketFlag = false;  //是否需要打印发票
			var recceiptFlag = false;//是否需要打印收据
			var docFlag = false;//是否需要打印免填单
			var edocFlag = false;//是否需要打印电子工单
			
			var hasPrintTicket =false;  //是否打印了发票
			var hasPrintreceipt =false; //是否打印了收据
			var hasPrintDoc = false;    //是否打印了免填单
			var hasPrintEdoc = false;   //是否打印了电子工单
			
			if($.printMgr){
				var infos = $.printMgr.getPrintData();
				if(!infos || infos.length == 0){
					return true;
				}
				
				infos.each(function(info, idx, total){
					$.printMgr.printIndex = idx;		//记录当前打印类型

					var printType = info.get("TYPE");
					if( printType.indexOf("01")>-1){
						ticketFlag = true;
						//判断是否已经打印发票
						if(info.containsKey("PRINTED") && info.get("PRINTED")=="1"){
							hasPrintTicket = true;
						}
					}
					if( printType.indexOf("02")>-1){
						recceiptFlag = true;
						//判断是否已经打印收据
						if(info.containsKey("PRINTED") && info.get("PRINTED")=="1"){
							hasPrintreceipt = true;
						}
					}
					if( printType.indexOf("03")>-1){
						docFlag=true;
						//判断是否已经打印免填单,包括电子工单，共用一个标志
						if(info.containsKey("PRINTED_DOC") && (info.get("PRINTED_DOC")=="1"||info.get("PRINTED_DOC")=="2")){
							hasPrintDoc = true;
						}
					}
				});
				
				 if(ticketFlag)	{
					 finishPrinted = finishPrinted && hasPrintTicket;
				 }
				 if(recceiptFlag) {
					 finishPrinted =  finishPrinted && hasPrintreceipt;
				 }
				 if(docFlag || edocFlag){
					 finishPrinted = finishPrinted && hasPrintDoc;
				 }
					 
				  $.printMgr.setFinishPrinted(finishPrinted);
			}
		    return 	finishPrinted;
		},
		
		

		/**提示信息
		showTmpMessage : function(result, title, content, isPrint) {
			if (!result) {
				result = "error";
			}
			if(!isPrint){
				isPrint = false;
			}
			content = content.replace(/\n/ig, "<br/>");
			if(result == "success"){
				var buttons = null;
				if(isPrint) buttons = {"ext0": "打印,print"};
				MessageBox.success("成功提示", title, function(btn){
					//点击确定按钮
					if(btn == "ok"){
						$.cssubmit.closeMessage(true);
					}else if(btn == "ext0"){
					//点击打印按钮
						$.cssubmit.printTrade();
						return false;
					}
				},buttons, content);
			}else{
				MessageBox.error("错误提示", title, function(btn){
					$.cssubmit.closeMessage();
				} ,null, content);
			}
		},*/
		
		
		
		/**
		* 自定义业务受理成功窗口按钮
		* 参数要求Json对象或数组 {} or []
		* {"name":"打印", "icon":"print", "fn": func}
		*  name		按钮名字
		*  icon		按钮icon,参考ECL中icon，如e_ico-print 只需传入print
		*  fn		按钮点击按钮触发事件，该函数有一个入参，即为业务受理成功后返回对象
		*  如果有多个按钮，以上面为原子结构，组织成数组传入
		*/
		bindCustomizeBtn:function(customizeBtns){
			var btns = [];
			if(customizeBtns instanceof Array){
				btns = customizeBtns;
			}else{
				btns.push(customizeBtns);
			}
			if(!$.cssubmit.customizeBtns){
				$.cssubmit.customizeBtns = [];
			}
			$.cssubmit.customizeBtns = $.cssubmit.customizeBtns.concat(btns);
			
			if('继续' == customizeBtns.name){
				$.printMgr.getPrintData().clear();
			}
			
		},
		//自定义按钮执行方法
		customizeBtnEvent:function(idx){
			var btnObj = $.cssubmit.customizeBtns[idx];
			if(btnObj["fn"]){
				btnObj["fn"]($.cssubmit.tradeData);
			}
		},
		
		//提示信息
		showMessage : function(result, title, content, isPrint) {
			if (!result) {
				result = "error";
			}
			if(!isPrint){
				isPrint = false;
			}
			var judgeTrade = ("error" == result);
			var msgPanel = $("#SUBMIT_MSG_PANEL");
			if (!msgPanel.length) {
				var submitWidth = "42";
				if($.os.phone){
					submitWidth = "22";
				}
				var html = [];
				
				html.push('<div class="c_msg c_msg-phone-v c_msg-h c_msg-full" id="SUBMIT_MSG_PANEL">')
				html.push('<div class="wrapper" style="width:'+submitWidth+'em;">');
				html.push('<div class="emote"></div>');
				
				html.push('<div class="info">');
				
				html.push('<div class="text">');
				html.push('<div class="title e_green">'+title+'</div>');
				html.push('<div class="content">'+(content ? ("" + content).replace(/\n/ig, "<br />") : "")+'</div>');
				html.push('<div class="c_space"></div>');
				html.push('</div>');
				
				html.push('<div class="fn" id="SUBMIT_MSG_BTN">');
				
				if($.cssubmit.clickButton=="addShoppingCart"){
					var btnClass = "e_button-r e_button-green";
					var btnText = "继续办理业务";
					var btnIco = "e_ico-ok";
					if(judgeTrade){
						btnClass = "e_button-r e_button-navy";
						btnText = "关闭";
						btnIco = "e_ico-close";
					}
					
					html.push('<button type="button" class="'+btnClass+'" ontap="javascript:$.cssubmit.closeMessage('+(judgeTrade ? "" : "true")+');" >');
					html.push('<span class="'+btnIco+'"></span><span>'+btnText+'</span>');
					html.push('</button>');
					html.push('<button type="button" class="e_button-r e_button-blue"  ontap="javascript:$.cssubmit.goShopping();" ><span class="e_ico-cart"></span><span>去结算</span></button>');
				}else{
					var btnClass = "e_button-r e_button-green";
					var btnText = "确定";
					var btnIco = "e_ico-ok";
					if(judgeTrade){
						btnClass = "e_button-r e_button-navy";
						btnText = "关闭";
						btnIco = "e_ico-close";
					}
					html.push('<button type="button" class="'+btnClass+'" ontap="javascript:$.cssubmit.closeMessage('+(judgeTrade ? "" : "true")+');" >');
					html.push('<span class="'+btnIco+'"></span><span>'+btnText+'</span>');
					html.push('</button>');
				}
				if(isPrint){
					html.push('<button type="button" class="e_button-r e_button-blue" ontap="$.cssubmit.printTrade();" ><span class="e_ico-print"></span><span>打印</span></button>');
				}
				
				if($.cssubmit.customizeBtns){
					var name,icon;
					var btns = $.cssubmit.customizeBtns;
					for(var i=0; i<btns.length; i++){
						if(!btns[i]["name"]) continue;
						name = btns[i]["name"];
						//icon = btns[i]["icon"]=""?"":"e_ico-"+btns[i]["icon"];
						html.push('<button type="button" class="e_button-r e_button-blue" ontap="$.cssubmit.customizeBtnEvent('+i+');"><span class=""></span><span>'+name+'</span></button>')
					}
				}
				

				//显示开具设置按钮
				//start 添加开具电子发票推送信息设置按钮
				//debugger;
				try{					
					if($.cssubmit.tradeData instanceof $.DatasetList){
						$.cssubmit.tradeData = $.cssubmit.tradeData.get(0);
					}else if($.cssubmit.tradeData instanceof $.DataMap){
						$.cssubmit.tradeData = $.cssubmit.tradeData;
					}
				var tradeTypeCode;
				if($.auth){
					tradeTypeCode= $("#TRADE_TYPE_CODE").val();
				}else if( $.cssubmit.tradeData && $.cssubmit.tradeData.containsKey("ORDER_TYPE_CODE") ){
					tradeTypeCode = $.cssubmit.tradeData.get("ORDER_TYPE_CODE");
				}else if( $.cssubmit.tradeData && $.cssubmit.tradeData.containsKey("TRADE_TYPE_CODE") ){
					tradeTypeCode = $.cssubmit.tradeData.get("TRADE_TYPE_CODE");
				}
				
				if (tradeTypeCode) {
					$.cssubmit.getTradeTypeKjConf(tradeTypeCode);
					if ($.cssubmit.commData.get("CAN_SET_EPRINTCEPT_CONF") && "TRUE"==$.cssubmit.commData.get("CAN_SET_EPRINTCEPT_CONF")){
						if($.printMgr.userRecptSvcConf.custType != "GRP"){
							html.push('<button type="button" class="e_button-r e_button-blue" ontap="$.cssubmit.openKJConf();" ><span class="e_ico-print"></span><span>电子发票打印设置</span></button>');
						}else{							
							html.push('<button type="button" class="e_button-r e_button-blue" ontap="$.cssubmit.openGrpKJConf();" ><span class="e_ico-print"></span><span>电子发票打印设置</span></button>');
						}
					}						
				}
				}catch(e){MessageBox.alert(e);}
				//end 添加开具推送信息配置按钮		
				
				html.push('</div>');
				html.push('</div>');
				html.push('</div>');
				html.push('</div>');
				
				$(document.body).append(html.join(""));
				$("#SUBMIT_MSG_PANEL").css("z-index",$.zIndexer.get("SUBMIT_MSG_PANEL"));
				msgArr=null;
			}
			msgPanel = null;
		},
		//-----购物车
		goShopping : function(){
			var param = "&AUTO_AUTH=true&DISABLED_AUTH=true&SERIAL_NUMBER=";
			  if($.auth && $.auth.getAuthData()){
				 var user=$.auth.getAuthData().get("USER_INFO");
				 if(user && user.get("SERIAL_NUMBER")){
				    param += user.get("SERIAL_NUMBER");
				    param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
				    param += "&USER_ID="+user.get("USER_ID");
				 }
			  }
//			  openNav("购物车", "shopping.ShoppingCart","", param);
			  closeNavByTitle("购物车");
			  openNav("购物车", "merch.merchShoppingCart","", param, '/order/iorder');
			}
		//-----购物车
		
	}});
	
	//执行初始化方法，将事件绑定到提交按钮上
	$($.cssubmit.init);
	window.flowSubmit = $.cssubmit.flowSubmit;
})(Wade);
	
//关闭tab窗口时触发
function onClose(current){ 
	
	if($.cssubmit){
		if(!$.cssubmit.hasPrintAll()){
			MessageBox.alert("您还有票据未打印，请打印完后退出!");
			return false;
		}
	}
	
	if(top.clearFee && current==true){
		// 清空费用组件
		top.clearFee();
	}
	if($.feeMgr){
		$.feeMgr.cacheFee = null;
		$.feeMgr.cacheFeeList = null;
	}
	
}

//tab窗口变为非当前窗口时触发
function onUnActive(){
	
	if($.cssubmit){
		if(!$.cssubmit.hasPrintAll()){
			MessageBox.alert("您还有票据未打印，请打印完后办理其他业务!");
			return false;
		}
		
	}
	
	if(top.clearFee && top.getFee){
		if($.feeMgr){
			// 取当前费用信息
			$.feeMgr.cacheFee = top.getFee();
			//费用缓冲好以后，修改为非激活状态
			$.feeMgr.activeFlag = false;
		}
		// 清空费用组件
		top.clearFee();
	}
	
}
