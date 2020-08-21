(function($){
	$.extend({simcardcheck:{
		clazz:"com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.SimCardHandler",
		serialNumber:null,
				
		//初始化SIM卡校验
		init:function(fieldId){
			var oThis=this;
			var obj = $("#"+fieldId);
			var handlerObj = $("#SOURCE_"+fieldId);
			
			if(handlerObj.attr("serialNumber")){
				oThis.serialNumber=handlerObj.attr("serialNumber");
			}
			obj.attr("nullable", "no");
			//回车事件
			obj.bind("keydown", function(e){
				oThis.events.onKeydowSimCard(e, fieldId);
			}); 
		},
		setSerialNumber:function(sn){
			this.serialNumber=sn;
		},
		//SIM卡校验前动作，主要是校验
		checkSimCard:function(fieldId){
			var flag=false;
			var action=$("#SOURCE_"+fieldId).attr("beforeAction"); 
			if(action && action != ""){
				try{
					flag=(new Function("return "+ action + ";"))();
				}catch(e){
					MessageBox.error("错误提示","执行SIM卡校验前事件有问题，请检查后重试！");
					return;
				}
			}
			if(!flag){
				return;
			}
			$.simcardcheck.doSimCard(fieldId);
		},
		//校验SIM卡号
		doSimCard:function(fieldId){
			var oThis=this;
			var obj=$("#"+fieldId);
			obj.val($.trim(obj.val()));
			if(!$.validate.verifyField(obj[0])){
				return;
			}
			var simPattern = /^89860[A-Za-z0-9]{15}$/;
			if(!simPattern.exec(obj.val())) {
				MessageBox.alert("告警提示", "SIM格式错误！", function(){
					obj.focus();
				});
				return ;
			}
			if(!this.serialNumber){
				MessageBox.alert("告警提示", "请设置手机号码！");
				return ;
			}
			var handlerObj = $("#SOURCE_"+fieldId);
			var param = "&SIM_CARD_NO="+$("#"+fieldId).val();
			param += "&SERIAL_NUMBER="+this.serialNumber;
			$.beginPageLoading("校验SIM卡。。。");
			$.httphandler.submit(null, $.simcardcheck.clazz, "checkSimCard", param, 
				function(data){
					$.endPageLoading();
					oThis.callBackSimCard(fieldId, data);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","校验SIM卡报错！", null, null, info, detail);
			});	
			
		},
		//校验后回调动作,需要返回True or  False
		callBackCheckSimCard:function(fieldId, checkData){
			var flag=true;
			var oThis=this;
			var action=$("#SOURCE_"+fieldId).attr("tradeAction"); 
			if(action && action != ""){
				flag=(new Function("var data = arguments[0]; return "+ action + ";"))(checkData);
			}
			if(!flag){
				return;
			}
			var param = "&SIM_CARD_NO="+$("#"+fieldId).val();
			param += "&SERIAL_NUMBER="+this.serialNumber;
			param+="&WRITE_TAG="+checkData.get("WRITE_TAG");
			
			$.beginPageLoading("预占SIM卡。。。");
			$.httphandler.submit(null, $.simcardcheck.clazz, "preOccupySimCard", param, 
				function(data){
					$.endPageLoading();
					oThis.callBackSimCard(fieldId, data);
					
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","预占SIM卡报错！", function(){oThis.exceptEvent(fieldId);}, null, info, detail);
			});	
		},
		
		//预占后回调动作
		callBackSimCard:function(fieldId, data){
			var action=$("#SOURCE_"+fieldId).attr("afterAction"); 
			if(action && action != ""){
				(new Function("var data = arguments[0];"+ action + ";"))(data);
			}
		},
		
		//预占报错点击确定后，执行例外回调事件
		exceptEvent:function(fieldId){
			var action=$("#SOURCE_"+fieldId).attr("exceptAction"); 
			if(action && action != ""){
				try{
					(new Function( action + ";"))();
				}catch(e){
					MessageBox.error("错误提示","执行校验错误事件有问题，请检查后重试！");
					return;
				}
			}
		},
		
		events:{
			onKeydowSimCard:function(e, fieldId){
				if(e.keyCode==13 || e.keyCode==108){
					//回车事件
					$.simcardcheck.checkSimCard(fieldId);
					return false;
				}
				return true;
			}
			
		}
	}});
	
})(Wade);