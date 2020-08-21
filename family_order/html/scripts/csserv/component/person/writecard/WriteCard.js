(function($){ 
	$.extend({simcard:{
		clazz:"com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.SimCardHandler",
		ocx:{},
		mode:null,					//写卡模式
		serialNumber: null,			//单卡号码，或一卡双号主卡号码
		serialNumberB: null,		//一卡双号副卡号码
		beforeWriteSVC:null,		//写卡前服务
		checkWriteSVC:null,			//写卡后确认服务（预制卡需要调用）
		afterWriteSVC:null,			//写卡后服务
		netTypeCode:null,          //区分资源库
		params:null,                //自定义入参
		init:function(){
			$.simcard.ocx.createOcx();		//创建写卡控件

			$.simcard.mode = $("#_CardCmpParam").attr("mode");
			
			//mode(1-2),读卡写卡[1：单卡，2：双卡]
			if($.simcard.mode==1 || $.simcard.mode==2){
				$.simcard.beforeWriteSVC = $("#_CardCmpParam").attr("beforeWriteSVC");
				$.simcard.checkWriteSVC = $("#_CardCmpParam").attr("checkWriteSVC");
				$.simcard.afterWriteSVC = $("#_CardCmpParam").attr("afterWriteSVC");
				
				$("#readCardBtn").bind("click", $.simcard.events.onClickReadCardBtn); 
				$("#writeCardBtn").bind("click", $.simcard.events.onClickWriteCardBtn);
				$("#writeCardBtn").attr("disabled",true);
			}
		},
		
		//===========================读卡=====================================
		//写卡前前校验，判断版本号是否跟资源那边配置一致，以及接入地州等
		checkReadCard:function(){
			//初始化写卡控件，入参true表示需要初始化各项参数
			if(!$.simcard.ocx.initalize(true)) return;
			
			//如果为读卡操作，不需要校验写卡组件DLL版本，仅校验写卡控件OCX版本
			var eparchyCode = $("#_CardCmpParam").attr("eparchyCode");
			var param ="&OCX_VERSION="+$.simcard.ocx.VERSION;
			param += "&EPARCHY_CODE="+eparchyCode;
			
			$.beginPageLoading("校验读卡组件版本。。。");
			$.httphandler.get($.simcard.clazz, "getOcxVersion", param, 
				function(data){
					$.endPageLoading();
					MessageBox.confirm("确认提示","接下来将进行读卡操作，是否继续？", function(btn){
						if(btn != "ok") {
							$.simcard.ocx.finalize();
							return;
						}
						if("0898" != eparchyCode){
							MessageBox.alert("错误提示", "未知的接入方式！");
							$.simcard.ocx.finalize();
							return;
						}
						$.simcard.readSimCard();
					});
				},function(code, info, detail){
					$.endPageLoading();
					$.simcard.ocx.finalize();
					MessageBox.error("错误提示","校验写卡控件版本报错！", null, null, info, detail);
			});	
		},
		//读卡操作，如果是空白卡也一样要回调读卡后action事件
		readSimCard:function(){
			if(!$.simcard.ocx.initalize(true)) return;
			var phoneNum= $.simcard.ocx.getPhoneNums();
			var iccid=$.simcard.ocx.getICCID(); 
			var emptyCardId=$.simcard.ocx.EMPTY_CARD_ID;
			$.simcard.ocx.finalize();
			
			//读卡错误，直接中断
			if(iccid==false || phoneNum==false){
				return;
			}
			if( ( phoneNum == 1 || phoneNum == 10 ) && $.simcard.mode == 2 ){
				MessageBox.alert("读卡提示", "该卡为单号卡，请到换卡界面写卡！");
				return;
			}
			if( (phoneNum == 2 || phoneNum == 20) && $.simcard.mode == 1){
				MessageBox.alert("读卡提示", "该卡为双号卡，请到一卡双号卡写卡界面写卡！");
				return;
			}
			if(iccid.substring(0,5) != "89860"){
				MessageBox.success("读卡提示","空白卡,卡号为："+emptyCardId+"，请执行写卡流程！");
				$("#writeCardBtn").attr("disabled",false);
				var readAfterAction = $("#_CardCmpParam").attr("readAfterAction");
				if(readAfterAction && readAfterAction != ""){
					var m=$.DataMap();
					m.put("PHONE_NUM", phoneNum);
					m.put("EMPTY_CARD_ID", emptyCardId);
					m.put("IS_WRITED", "0");		//是否被写过
					(new Function("var data=arguments[0];"+ readAfterAction + ";"))(m);
				}
				return;
			}
			
			//异地补换卡需要读老卡信息加上这段k3
			var tradeTypeCodesim = $("#_CardCmpParam").attr("tradeTypeCode");
			if(tradeTypeCodesim=="141"){
				MessageBox.success("读卡提示","SIM卡,卡号为："+iccid);
				var readAfterAction = $("#_CardCmpParam").attr("readAfterAction");
				if(readAfterAction && readAfterAction != ""){
					var m=$.DataMap();
					m.put("PHONE_NUM", phoneNum);
					m.put("EMPTY_CARD_ID", emptyCardId);
					m.put("IS_WRITED", "1");		//是否被写过
					m.put("REMOTE_SIM_TAG", "1");		
					m.put("SIM_CARD_NO", iccid);		
					(new Function("var data=arguments[0];"+ readAfterAction + ";"))(m);
				}
				return;
			}
			
			var param="&EPARCHY_CODE="+$("#_CardCmpParam").attr("eparchyCode");
			param+="&SIM_CARD_NO="+iccid;
			param+="&SERIAL_NUMBER="+$.simcard.getSerialNumber();
			param+="&NET_TYPE_CODE="+$.simcard.netTypeCode;
			$.beginPageLoading("校验SIM卡。。。");
			$.httphandler.get($.simcard.clazz, "checkSimCard", param, 
				function(data){
					$.endPageLoading();
					
					$.simcard.afterReadCardAction(phoneNum, iccid, emptyCardId, data);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","校验SIM卡错误！", null, null, info, detail);
			});

		},
		//SIM卡校验后，进行业务自定义逻辑校验，如果没有，继续进行预占
		afterCheckCardAction:function(phoneNum, iccid, emptyCardId, checkData){
			var flag=true;
			var action=$("#_CardCmpParam").attr("readTradeAction"); 
			if(action && action != ""){
				flag=(new Function("var data = arguments[0]; return "+ action + ";"))(checkData);
			}
			if(!flag){
				return;
			}
			
			var param="&EPARCHY_CODE="+$("#_CardCmpParam").attr("eparchyCode");
			param+="&SIM_CARD_NO="+iccid;
			param+="&SERIAL_NUMBER="+$.simcard.getSerialNumber();
			param+="&WRITE_TAG="+checkData.get("WRITE_TAG");
			
			$.beginPageLoading("预占SIM卡。。。");
			$.httphandler.get($.simcard.clazz, "preOccupySimCard", param, 
				function(data){
					$.endPageLoading();
					
					$.simcard.afterReadCardAction(phoneNum, iccid, emptyCardId, data);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","预占SIM卡错误！", null, null, info, detail);
			});
		},
		/**
		 * 预制卡相关资源数据以及IMSI数据从资源那边获取返回
		 * 而非预制卡，直接从卡里面获取
		 */
		afterReadCardAction:function(phoneNum, iccid, emptyCardId, data){
			if(!data){
				data=$.DataMap();
			}
			if(phoneNum == 1 || phoneNum == 10){
				data.put("ICCID", iccid);
				
			}else if(phoneNum == 2 || phoneNum == 20){
				var strICCID=iccid.split("&");
				data.put("ICCID", strICCID[0]);
				data.put("ICCID2", strICCID[1]);
			}else{
				MessageBox.alert("错误提示", "不支持的卡类型！");
				return false;
			}
			data.put("EMPTY_CARD_ID", emptyCardId);
			data.put("PHONE_NUM", phoneNum);
			data.put("IS_WRITED", "1");
			
			var readAfterAction = $("#_CardCmpParam").attr("readAfterAction");
			if(readAfterAction && readAfterAction != ""){
				(new Function("var data=arguments[0];"+ readAfterAction + ";"))(data);
			}
		},
		//===========================写卡=====================================
		getRemoteWriteCardUrl:function(){			
			if($.simcard.ocx.PRE_SIM == "1"){
				$.simcard.checkWriteCard();
				return;
			}
			var strATR = "",strCardType;
			var emptyCardId = $.simcard.ocx.EMPTY_CARD_ID;
			strCardType = emptyCardId.substring(6, 8);
			
			var strATR = $.simcard.ocx.getATR();
			var param = "&ATR="+strATR;
			param += "&CARD_TYPE="+strCardType;			
			param += "&OCX_VERSION="+$.simcard.ocx.VERSION;					

			$.beginPageLoading("校验写卡组件版本。。。");		
			$.httphandler.get($.simcard.clazz, "getRemoteWriteCardUrl", param, function(data){
				$.endPageLoading();
				if(!data) return;
				if(data && data.get("result")!="0"){
					MessageBox.alert("错误提示", data.get("message"));
					$.simcard.ocx.finalize();
					return;
				}
				var xDllName	= data.get("DllName", "");
				var xDllVersion	= data.get("DllVersion", "");
				var xDownUrl	= data.get("DownUrl", "");
				var xAuthKey    = data.get("AuthKey", "");
				//alert("xDllName ="+xDllName +"  xDllVersion= "+xDllVersion+" xDownUrl = "+xDownUrl+" xAuthKey= "+xAuthKey);
				var exists = $.simcard.ocx.checkDll(xDllName);
				if (!exists) {
					MessageBox.alert("信息提示","发现新的写卡组件，请下载安装，否则无法进行写卡操作！");
					   window.open(xDownUrl);
					   return false;
				}
				
				var xStrCoding ="&DLLNAME="+xDllName+"&DLLAUTH="+xAuthKey+"&DLLVER="+xDllVersion;
				$("#_CardCmpParam").attr("writeCardCodeStr", xStrCoding);
				
				$.simcard.checkWriteCard();
				
			},function(code, info, detail){
				$.simcard.ocx.finalize();
				$.endPageLoading();
				MessageBox.error("错误提示","获取写卡控件版本报错！", null, null, info, detail);
			});	
		},
		
		//写卡校验开始
		checkWriteCard:function(){
			var strICCID=null;
			var eparchyCode = $("#_CardCmpParam").attr("eparchyCode");
			MessageBox.confirm("确认提示","接下来将进行写卡操作，是否继续？", function(btn){
				if(btn != "ok") {
					$.simcard.ocx.finalize();
					return;
				}
				if("0898" != eparchyCode){
					MessageBox.alert("错误提示", "未知的接入方式！");
					$.simcard.ocx.finalize();
					return false;
				}
				var sn = $.simcard.getSerialNumber();
				var phoneNum=$.simcard.ocx.getPhoneNums();
				var emptyCardId=$.simcard.ocx.EMPTY_CARD_ID;
				var iccid=$.simcard.ocx.getICCID();
				var strICCID=iccid.split("&");
				//MessageBox.alert("告警提示", "ICCID = "+strICCID[0]+" "+strICCID[1]);
				
				var param = "&EPARCHY_CODE="+eparchyCode;
				param += "&OCX_VERSION="+$.simcard.ocx.VERSION;
				param += "&PHONE_NUM="+phoneNum; 
				param += "&EMPTY_CARD_ID="+emptyCardId; 
				param += "&USIM="+$.simcard.ocx.USIM;
				param += "&IS_NEW="+$.simcard.ocx.PRE_SIM;
				param += "&MODE="+$.simcard.mode;
				param += "&SERIAL_NUMBER="+sn;
				
				if( phoneNum == 1 || phoneNum == 10 ){
					if($.simcard.mode == 2){
						MessageBox.alert("告警提示", "该卡为单号卡，请到换卡界面写卡！");
						$.simcard.ocx.finalize();
						return false;
					}
					
					if(!sn){
						MessageBox.alert("告警提示", "手机号码不能为空！");
						$.simcard.ocx.finalize();
						return false;
					}
					if( iccid.substring(0,5) == "89860" ){
						MessageBox.alert("告警提示", "非空白卡，不能进行写卡，请插入白卡！");
						$.simcard.ocx.finalize();
						return false;
					}
					
				}else if( phoneNum == 2 || phoneNum == 20 ){
					if($.simcard.mode == 1){
						MessageBox.alert("告警提示", "该卡为双号卡，请到一卡双号卡写卡界面写卡！");
						$.simcard.ocx.finalize();
						return false;
					}
					
					if(!sn){
						MessageBox.alert("告警提示", "主卡号码不能为空！");
						$.simcard.ocx.finalize();
						return false;
					}
					var snb = $.simcard.getSerialNumberB();
					if(!snb){
						MessageBox.alert("告警提示", "副卡号码不能为空！");
						$.simcard.ocx.finalize();
						return false;
					}
					if( strICCID[0].substring(0,5) == "89860" || strICCID[1].substring(0,5) == "89860" ){
						MessageBox.alert("告警提示", "非空白卡，不能进行写卡，请插入一卡双号卡白卡！");
						$.simcard.ocx.finalize();
						return false;
					}
					param += "&SERIAL_NUMBERB="+snb;
					
				}else{
					MessageBox.alert("错误提示", "不支持的卡类型！");
					$.simcard.ocx.finalize();
					return false;
				}
				//设置自定义写卡前服务
				if($.simcard.beforeWriteSVC){
					param +="&BEFORE_WRITE_SVC="+$.simcard.beforeWriteSVC;
				}
				//自定义参数
				if($.simcard.params){
					param +="&PARAMS="+$.simcard.params;
				}
				$.beginPageLoading("开始写卡。。。");
				$.httphandler.get($.simcard.clazz, "beforeWriteCard", param, 
					function(data){
						if(!data || !data.length){
							$.endPageLoading();
							MessageBox.alert("告警提示", "写卡前校验错误,不能获取写卡参数串！");
							$.simcard.ocx.finalize();
							return;
						}
						$.simcard.writeSimCard(data, phoneNum, emptyCardId);
					},function(code, info, detail){
						$.endPageLoading();
						$.simcard.ocx.finalize();
						MessageBox.error("错误提示","写卡前校验报错！", null, null, info, detail);
				});	
			});
		},
		//写卡操作，对于预制卡需要多一步确认操作，同时IMSI需要从后台返回，控件暂时不支持，预制卡的IMSI则可以直接读取
		writeSimCard:function(data, phoneNum, emptyCardId){
			//alert(data.toString());
			var resIMSI = data.get("IMSI");
			var iccid= data.get("SIM_CARD_NO");
			var strEncode = data.get("ENCODE_STR");
			if($.simcard.ocx.PRE_SIM==0){
				strEncode += $("#_CardCmpParam").attr("writeCardCodeStr");
			}
			var resultCode=0;	//写卡成功标记
			//写卡操作，预制卡返回写卡结果，非预制卡返回0，错误返回false
			var result = $.simcard.ocx.writeCard(strEncode);
			//alert("result-------"+result);
			if(result === false){
				resultCode=1;
			}
//			alert("写卡结果 = "+resultCode+" 写卡串 = "+strEncode);
			var imsi = resIMSI;
			if($.simcard.ocx.PRE_SIM==0 && resultCode==0){
				imsi = $.simcard.ocx.getIMSI();
				iccid = $.simcard.ocx.getICCID();
			}
			/**
			 * 预制卡写卡成功，需要继续执行checkWriteCard操作，最后才执行afterWriteCard
			 * 非预制卡，不管写卡成功还是失败，直接执行afterWriteCard
			 */
			if($.simcard.ocx.PRE_SIM==0 || ($.simcard.ocx.PRE_SIM==1 && resultCode==1)){
				$.simcard.ocx.finalize();
				$.simcard.afterWriteSimCard(resultCode, phoneNum, iccid, emptyCardId, imsi);
				return;
			}

			//针对预制卡，且写卡成功执行确认操作
			var cardInfo = $.simcard.ocx.getCardInfo();
			if(cardInfo == false){
				$.endPageLoading();
				$.simcard.ocx.finalize();
				return;
			}
			var param ="&CARD_INFO="+cardInfo+"&RESULT_INFO="+result+"&TRADE_ID="+data.get("TRADE_ID");
			//设置自定义写卡后确认服务
			if($.simcard.checkWriteSVC){
				param +="&CHECK_WRITE_SVC="+$.simcard.checkWriteSVC;
			}
			$.httphandler.get($.simcard.clazz, "checkWriteCard", param, function(data){
				//新卡后续处理流程先不考虑一卡双号
				$.simcard.ocx.finalize();
				//alert("新卡写卡后webservice校验结果======="+data.toString());
				if(data.get("RESULT_CODE")!="0"){
					MessageBox.alert("错误提示","写卡结果校验失败！【"+data.get("RESULT_INFO")+"】！");
				}
				$.simcard.afterWriteSimCard(data.get("RESULT_CODE"), phoneNum, iccid, emptyCardId, imsi);
			},function(code, info, detail){
				$.simcard.ocx.finalize();
				$.endPageLoading();
				MessageBox.error("错误提示","写卡校验报错！", null, null, info, detail);
			});	

		},
		/**
		 * 写卡后执行事件，resultCode为写卡结果：0表示写卡成功，1表示写卡失败
		 */
		afterWriteSimCard:function(resultCode, phoneNum, iccid, emptyCardId, imsi){
//			alert("zw进入写卡后执行事件"+iccid+"----"+imsi);
			var data=$.DataMap();
			var param = "&RESULT_CODE="+resultCode;
			param += "&EMPTY_CARD_ID="+emptyCardId;
			param += "&EPARCHY_CODE="+$("#_CardCmpParam").attr("eparchyCode");
			param += "&SERIAL_NUMBER="+$.simcard.getSerialNumber();
			param += "&PHONE_NUM="+phoneNum;
			data.put("EMPTY_CARD_ID", emptyCardId);
			
			if(phoneNum == 1 || phoneNum == 10){	
				param += "&SIM_CARD_NO="+iccid;
				param += "&IMSI="+imsi;
				
				data.put("SIM_CARD_NO", iccid);
				data.put("IMSI", imsi);
				
			}else if(phoneNum == 2 || phoneNum == 20){
				var strICCID=iccid.split("&");
				var strIMSI=imsi.split("&");
				param += "&SIM_CARD_NO="+strICCID[0];
				param += "&SIM_CARD_NO2="+strICCID[1];

				param += "&IMSI="+strIMSI[0];
				param += "&IMSI2="+strIMSI[1];
				
				data.put("ICCID", strICCID[0]);
				data.put("IMSI", strIMSI[0]);
				data.put("ICCID2", strICCID[1]);
				data.put("IMSI2", strIMSI[1]);
				
			}
			data.put("RESULT_CODE", resultCode);
			
			//设置自定义写卡后服务
			if($.simcard.afterWriteSVC){
				param +="&AFTER_WRITE_SVC="+$.simcard.afterWriteSVC;
			}
			$.beginPageLoading("写卡确认。。。");
			$.httphandler.get($.simcard.clazz, "afterWriteCard", param,function(){
				$.endPageLoading();
				if(resultCode==0){
					alert("写卡成功！");
				}
				
				var writeAfterAction = $("#_CardCmpParam").attr("writeAfterAction");
				if(writeAfterAction && writeAfterAction != ""){
					data.put("WRITE_TAG","0");//白卡/成卡标记
					(new Function("var data=arguments[0];"+ writeAfterAction + ";"))(data);
				}
						
			},function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","写卡错误！", null, null, info, detail);
			});
		},
		
		//===========================读卡写卡辅助=====================================
		getSerialNumber:function(){
			if($.simcard.serialNumber){
				return $.simcard.serialNumber;
			}
			var sn = $("#_CardCmpParam").attr("serialNumber");
			if(sn != ""){
				return sn;
			}
			return false;
		},
		getSerialNumberB:function(){
			if($.simcard.serialNumberB){
				return $.simcard.serialNumberB;
			}
			var snbField = $("#_CardCmpParam").attr("snbField");
			if(snbField != "" && $("#"+snbField).val()!= ""){
				return $.trim($("#"+snbField).val());
			}
			return false;
		},
		getParams:function(){
			if($.simcard.params){
				return $.simcard.params;
			}
			return false;
		},
		setSerialNumber:function(sn, snb){
			$.simcard.serialNumber = sn;
			if(snb){
				$.simcard.serialNumberB = snb;
			}
		},
		setSerialNumberB:function(snb){
			$.simcard.serialNumberB = snb;
		},
		setBeforeWriteSVC:function(svc){
			$.simcard.beforeWriteSVC=svc;
		},
		setCheckWriteSVC:function(svc){
			$.simcard.checkWriteSVC=svc;
		},
		setAfterWriteSVC:function(svc){
			$.simcard.afterWriteSVC=svc;
		},
		setNetTypeCode:function(netTypeCode){
			$.simcard.netTypeCode=netTypeCode;
		},
		setParams:function(params){
			$.simcard.params=params;
		},
		
		events:{
			//读/写卡开关
			onClickReadCardBtn:function(){
				var flag=true;
				var readBeforeAction = $("#_CardCmpParam").attr("readBeforeAction");
				if(readBeforeAction && readBeforeAction != ""){
					flag=(new Function("return "+readBeforeAction + ";"))();
				}
				if(!flag) return;
				$.simcard.checkReadCard();
			},
			//读/写卡
			onClickWriteCardBtn:function(){
				//初始化写卡控件，入参true表示需要初始化各项参数
				if(!$.simcard.ocx.initalize(true)) return;
				var flag=true;
				var strICCID=$.simcard.ocx.getICCID();
				if(strICCID.substring(0,5) == "89860" ){
					$.simcard.ocx.finalize();
					MessageBox.alert("错误提示", "非空白卡，不能进行写卡，请插入白卡！");
					return;
				}
				var writeBeforeAction = $("#_CardCmpParam").attr("writeBeforeAction");
				if(writeBeforeAction && writeBeforeAction != ""){
					flag=(new Function("return "+writeBeforeAction + ";"))();
				}
				//如果写卡前校验返回false或者进行异步操作，则不会执行后续写卡，异步里面请使用$.simcard.checkWriteCard()启动
				if(!flag) return;  
				$.simcard.getRemoteWriteCardUrl();
					
			}
		}
	}});
	$.extend($.simcard.ocx, SimCardOcx);
})(Wade);