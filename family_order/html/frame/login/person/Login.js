(function($){
	$.extend({login:{
		componentId:"nonePart",
		isLogin:false,
		cacheData:{},
		prefix:"/order",
		init:function(){
			$.login.bindEvents();
			$.login.initLoginTypelist();
		},
		bindEvents:function(){
			$("#LOGIN_BTN").tap($.login.loginConfirm); 
			$("#logoutButton").tap($.login.logout);
			//$("#shopCart").tap($.login.openShopCart);
			$("#broadbandOpen").tap($.login.broadbandOpen);
			$("#custCentre").tap($.login.openCustCentre);
			$("#creatUserButton").tap($.login.creatUserButton);
			$("#cust_info, #personalDetail").each(function() {
		        $(this).bind("mouseover", function() {
		        	$("#personalDetail").addClass('more-show');
		        });
		        $(this).bind("mouseout", function() {
		        	$("#personalDetail").removeClass('more-show');
		        });
		    });
			$("#REFRESH_BTN").tap($.login.refreshScaInfo);
			$.login.bindEnterEvent();
		},
		bindEnterEvent:function(){
			$("#LoginPart input").bind("keydown", function(e){
				if(e.keyCode == 13 || e.keyCode == 108){
					//回车事件
					$("#LOGIN_BTN").focus();
					$.login.loginConfirm();
					return false;
				}
			});
		},
		ctrlLayer:function(){
			$("body").unbind("click",$.login.closeLoginTypeOption);
			var display = $("#LoginTypeOption").css("display");
			if(display==="none"){
				$("#LoginTypeOption").css("display","");
				$("#mainCover").css("display","");
			}else{
				$("#LoginTypeOption").css("display","none");
				$("#mainCover").css("display","none");
			}
			$("body").bind("click",$.login.closeLoginTypeOption);
		},
		closeLoginTypeOption:function(){
			var display = $("#LoginTypeOption").css("display");
			if(display==="" || display=="block"){
				$("#LoginTypeOption").css("display","none");
			}
		},
		loginConfirm:function(){
			if(!$.login.checkData()){
				return ;
			};
			//缓存登录前数据
			$.login.cacheData["REQ_DATA"]=$.DataMap();
			$.login.cacheData["REQ_DATA"].put("LOGIN_TYPE_CODE",$("#LOGIN_TYPE_CODE").attr("loginTypeCode"));
			$.login.cacheData["REQ_DATA"].put("LOGIN_TYPE_NAME",$("#LOGIN_TYPE_CODE").val());
			$.login.cacheData["REQ_DATA"].put("LOGIN_NUM",$.trim($("#LOGIN_NUM").val()));
			$.login.cacheData["REQ_DATA"].put("LOGIN_VAL",$.trim($("#LOGIN_VAL").val()));
			$.login.loadLoginData();
		},
		
		creatUserButton:function(){
			//debugger;
			var href = $.login.prefix+"/iorder?service=page/createpersonuser.CreatePersonUserNew&listener=onInitTrade";
			openNavByUrl('个人用户开户', href);
		},
		broadbandOpen:function(){
			var href = $.login.prefix+"?service=page/broadband.BroadBandCreate&listener=initPage&cond_CREATE_TYPE=PERSONSERV";
			openNavByUrl('宽带开户', href);
		},
		openCustCentre:function(){
			//debugger;
			var param = "" ;
			if($.login.isLogin){
				param += "&ACCESS_NUM="+$.login.getAccessNum()+"&SUBSCRIBER_INS_ID="+$.login.getIDS().get("SUBSCRIBER_INS_ID");
			}
			var href = $.login.prefix+"/order?service=page/userview.User360View"+param;
			openNavByUrl('客户资料综合查询', href);
		},
		openShopCart:function(){
			//debugger;
			if($.login.isLogin){
				var cartNum = $("#CART_NUM").html();
				if("0" == cartNum) return;
				var href = $.login.prefix+"?service=page/oc.person.shopcart.ShopCart&listener=onInitBusi&ACCESS_NUM="+$.login.getAccessNum();
				openNavByUrl('购物车', href);
			}
		},
		loadLoginData:function(){
			var param= "&ACTION=login&LOGIN_TYPE_CODE="+$("#LOGIN_TYPE_CODE").attr("loginTypeCode")+"&LOGIN_NUM="+$.trim($("#LOGIN_NUM").val())+"&LOGIN_VAL="+$.trim($("#LOGIN_VAL").val());
			//$.beginPageLoading("服务号码登录中。。。");
			  $("#LOGIN_BTN").attr("disabled", true);
		        $("#LOGIN_BTN").text("认证中...");
			$.ajax.submit(null, null, param, $.login.componentId, 
				function(data){
//					$.endPageLoading();
					$("#LOGIN_BTN").attr("disabled", false);
		            $("#LOGIN_BTN").text("登录");
		            
					if(data.get("VERIFY_RESULT")){
						var verifyResult = data.get("VERIFY_RESULT").get(0);
						var resultCode = verifyResult.get("RESULT_CODE");
						if(resultCode == "1"){
							MessageBox.alert("告警提示",verifyResult.get("RESULT_INFO"));
							return false;
						}
					}
					$.login.isLogin=true;
					$.login.showInfo(data);
					$.login.refreshActivePage();
				 
					var hintInfo = data.get("SUB_INFO").get(0);
					hintInfo.put("CUST_ID",data.get("CUST_ID"));
					
					$.login.showAcctInfo(hintInfo.get("SERIAL_NUMBER"),hintInfo.get("USER_ID"),hintInfo.get("REMOVE_TAG"));
					$.HintInfo.renderComponent("&HINT_INFO="+hintInfo.toString());
				},
				function(code, info, detail){
				//	$.endPageLoading();
					$("#LOGIN_BTN").attr("disabled", false);
		            $("#LOGIN_BTN").text("登录");
					MessageBox.error("错误提示",info,null, null, detail);
				},function(){
					//$.endPageLoading();
					$("#LOGIN_BTN").attr("disabled", false);
		            $("#LOGIN_BTN").text("登录");
					MessageBox.alert("告警提示","加载数据超时！");
			});
		},
		
         
		refreshActivePage:function(){
			var activeTitle = getNavTitle();
			if(activeTitle){
				var win = getNavContentByTitle(activeTitle);
				var href = win.location.href;
				var newUrl = $.login.getNewUrl(href);
				$.navtabset.closeAll();
				if("购物车" == activeTitle){
					this.openShopCart();
				}else{
					openNavByUrl(activeTitle,newUrl);
				}
			}else{
				$.navtabset.closeAll();
			}
		},
		getNewUrl:function(href){
			//debugger;
			var prefixUrl = href.substring(0,href.indexOf("?")+1);
			var tailUrl = href.substring(href.indexOf("?")+1);
			var paramArray = tailUrl.split("&");
			var paramData = $.DataMap();
			for(var i=0;i< paramArray.length;i++){
				var pa = paramArray[i].split("=");
				paramData.put(pa[0],pa[1]);
			}
			//注销刷新参数置空
			var accessNum = $.login.getAccessNum();
			var ids = $.login.getIDS();
			if(!accessNum){
				accessNum = "";
			}
			if(!ids){
				ids = "";
			}
			//设置新的IDS和号码参数
			paramData.put("ACCESS_NUM",accessNum);
			paramData.put("SERIAL_NUMBER",accessNum);
			paramData.put("LOGIN_TYPE_CODE",$.login.cacheData["REQ_DATA"].get("LOGIN_TYPE_CODE"));
			paramData.put("IDS",ids);
//			paramData.put("NGBOSS_RISK_LEVEL",$.login.getNgbossRiskLevel());
			var newUrl = "";
			paramData.eachKey(function(key){
				newUrl += "&"+key+"="+paramData.get(key);
			});
			return prefixUrl+newUrl.substring(1);
		},
		initLoginTypelist : function() {
			var options = "";
			$.DatasetList($("#LoginTypeList").val()).each(
				function(item, index, totalcount) {
					options = options + "<li val='"+item.get("DATA_ID")+"' onclick='$.login.setLoginTypeCode(this)' " ;
					//默认P方式加上样式
					if(index == 0){
						options = options +" class='on'";
					}
					options += ">"+item.get("DATA_NAME")+"</li>";
			});
			$("#LoginTypeOption ul").html(options);
		},
		checkData:function(){
			if(!$.validate.verifyAll('LoginPart')){
				return false;
			}
			return true;
		},
		getIDS:function(){
			return $.login.cacheData["IDS"];
		},
		getAccessNum:function(){
			return $.login.cacheData["ACCESS_NUM"];
		},
		getReqData:function(){
			return $.login.cacheData["REQ_DATA"];
		},
		
		//展示帐务相关信息，星级，余额，信用度等
		showAcctInfo:function(serialNumber,userId,removeTag){
			var param= "&ACTION=loginExtend&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&REMOVE_TAG="+removeTag;
			 
			$.ajax.submit(null, null, param, $.login.componentId, function(data){
				var subInfo = data.get("SUB_INFO");
				if(!subInfo){
					subInfo = data.get("SUB_DESTORY_INFO")
				}
				if(subInfo){
					var acctInfo = subInfo.get(0);
					acctInfo.eachKey(function(key){
						if("BALANCE"==key){
							var balance = parseFloat(acctInfo.get(key))/100;
							balance = balance.toFixed(2);
							acctInfo.put("BALANCE",balance);
						}
						if("REAL_TIME_FEE"==key){
							var realTimeFee = parseFloat(acctInfo.get(key))/100;
							realTimeFee = realTimeFee.toFixed(2);
							acctInfo.put("REAL_TIME_FEE",realTimeFee);
						}
						$("#S_"+key).html(acctInfo.get(key));
					});
					 
				 
					//客户星级 class="level level-5"
					var starLevel = acctInfo.get("STAR_LEVEL");
					$("#STAR_LEVEL").attr("class","");
					if(parseInt(starLevel)==0){
						$("#STAR_LEVEL").addClass("level");
					}else if(parseInt(starLevel)==6){
						$("#STAR_LEVEL").addClass("level level-gold");
					}else if(parseInt(starLevel)==7){
						$("#STAR_LEVEL").addClass("level level-diamond");
					}else{
						$("#STAR_LEVEL").addClass("level level-"+starLevel);
					}
					 
				}
			},function(error,info){
				MessageBox.error(error,info);
			});
			
			
			
		},
		
		showInfo:function(data){
			$("#login,#creatUserButton,#broadbandOpen").css("display","none");
			$("#logoutButton,#cust_info,#custCentre").css("display","");
			$("#LOGIN_TYPE_NAME").html($.login.getReqData().get("LOGIN_TYPE_NAME"));
			//缓存IDS
			$.login.cacheData["IDS"]=$.DataMap();
			$.login.cacheData["IDS"].put("CUST_ID",data.get("CUST_ID"));
			$.login.cacheData["IDS"].put("PARTY_ID",data.get("CB_PARTY").get(0).get("PARTY_ID"));
			//参与人
			$("#S_PARTY_NAME").html(data.get("CB_PARTY").get(0).get("PARTY_NAME"));
			
			//密码登录需要展示用户信息
			var subInfo = data.get("SUB_INFO");
			if(!subInfo){
				subInfo = data.get("SUB_DESTORY_INFO")
			}
			if(subInfo){
				var acctInfo = subInfo.get(0);
				 
				var accessNum = acctInfo.get("ACCESS_NUM");
				$("#S_ACCESS_NUM").html(accessNum);
				var mainOffer = acctInfo.get("MAIN_OFFER");
				$("#S_MAIN_OFFER").html(mainOffer);
				//宽带用户号
				var wideNetAccessNum = acctInfo.get("WIDE_NET_ACCESS_NUM");
				if(wideNetAccessNum){
					accessNum = wideNetAccessNum;
				}
				//缓存认证号码
				$.login.cacheData["ACCESS_NUM"] = accessNum;
				$.login.cacheData["IDS"].put("ACC_ID",acctInfo.get("ACC_ID"));
				$.login.cacheData["IDS"].put("SUBSCRIBER_INS_ID",acctInfo.get("SUBSCRIBER_INS_ID"));
				//停开机设置  class="statu-on"
				var prodSta = acctInfo.get("PROD_STA");
				var prodStaName = acctInfo.get("PROD_STA_NAME");
				$("#STA_SET").attr("title",prodStaName);
				$("#STA_SET").attr("class","statu-link");
				var removeTag = data.get("REMOVE_TAG");
				if(removeTag&&removeTag=="0"){
					if(prodSta && prodSta=="0"){
						$("#STA_SET").addClass("statu-on");
					}else{
						$("#STA_SET").addClass("statu-off");
					}
				}else{
					$("#STA_SET").addClass("statu-destroy");
				}
				 
				//4G用户
				var is4GUser = acctInfo.get("IS_4GUSER");
				$("#IS_4GUSER").attr("class","is4g-off");
				if(is4GUser&&is4GUser=="Y"){
					$("#IS_4GUSER").attr("class","is4g-on");
				}
			}
			//购物车数量
			$("#CART_NUM").html(data.get("CART_NUM"));
			var cartNum = data.get("CART_NUM");
			if(parseInt(cartNum)>0){
				$("#CART_NUM").css("display","");
			}
			$.login.transDigitalStyle();
			$.login.processEvent();
		},
		setLoginTypeCode:function(obj){
			this.resetLoginArea();
			//设置样式
			$("#LoginTypeOption li").removeClass("on");
			$(obj).addClass("on");
			var loginTypeCode = $(obj).attr("val");
			 
			//证件号码
			if(loginTypeCode == "0"){
				var html = "";
				html=html+"<div class=\"input\"><input type=\"text\"    id=\"LOGIN_VAL\" placeholder=\"证件号码\" nullable=\"no\" desc=\"证件号码\"/></div><button type=\"button\" ontap=\"$.login.readCard()\">读取</button>";
				$("#LOGIN_VLI").html(html);
				//$("#PSPT_TYPE_LI").attr("style","display:block");
			}
			else if(loginTypeCode == "1"){
				
				$("#LOGIN_VLI").html(" <div class=\"input\"><input type=\"password\" id=\"LOGIN_VAL\" autocomplete=\"off\" placeholder=\"服务密码\" maxlength=\"6\" nullable=\"no\" desc=\"服务密码\"/></div>");
			}
			else if(loginTypeCode =="7" || loginTypeCode =="6")
			{
				$("#LOGIN_VLI").html("<div class=\"input\"><input id=\"LOGIN_VAL\" type=\"text\" value=\"\" placeholder=\"验证码\" maxlength=\"6\" nullable=\"no\" desc=\"验证码\"/></div><button type=\"button\" ontap=\"$.login.sendVerifyCode()\">获取</button>");
			}else if(loginTypeCode == "5"){
				//VIP卡号校验
			} 
			
			if(loginTypeCode!="0"){
				$.login.hidePsptTypeCode();
			}
			
			$("#LOGIN_TYPE_CODE").attr("loginTypeCode",loginTypeCode);
			$("#LOGIN_TYPE_CODE").val($(obj).html());
			$.login.ctrlLayer();
			$.login.bindEnterEvent();
		},
		
		hidePsptTypeCode:function(){
			$("#PSPT_TYPE_LI").attr("style","display:none");
		},
		
		readCard:function(){
			  var ocx = new ActiveXObject("WadeMutiIdCard.MutiIdCard");
		        // 读取二代证芯片信息 obj为JSON格式对象,键名参考规范文档IDINFO的定义
		      var cardInfo = eval(ocx.GetCardInfo());
		      var strPsptId = cardInfo.number;//证件号码
		      
		      $("#LOGIN_VAL").val(strPsptId);
		      $.login.loginConfirm();
		},
		resetLoginArea:function(){
			$("#LOGIN_TYPE_CODE").attr("loginTypeCode","1");
			$("#LOGIN_TYPE_CODE").val("服务密码");
			$("#LoginTypeOption li").removeClass("on");
			$("#LoginTypeOption li[val=1]").addClass("on");
			var defaultLoginLi = "<div class=\"input\"><input type=\"password\" id=\"LOGIN_VAL\" placeholder=\"密码\" autocomplete=\"off\" maxlength=\"6\" nullable=\"no\" desc=\"密码\"/></div>";
			$("#LOGIN_VLI").html(defaultLoginLi);
			$("#LOGIN_NUM").attr("placeholder","手机号码");
			$("#LOGIN_NUM").attr("desc","手机号码");
//			$("#LOGIN_NUM").attr("datatype","mbphone");
			$.login.bindEnterEvent();
		},
		logout:function(){
			var param= "&ACTION=logout&ACCESS_NUM="+$.login.getAccessNum();
			$.beginPageLoading("服务号码注销中。。。");
			$.ajax.submit(null, null, param, $.login.componentId, 
				function(data){
					$.endPageLoading();
					$.login.isLogin=false;
					$.login.cacheData={};
					$.login.subscrberFeeInfo={};
					$.login.resetLoginArea();
					$("#login,#creatUserButton,#broadbandOpen").css("display","");
					$("#logoutButton,#cust_info,#fee_info,#custCentre,#widenet_fee_info").css("display","none");
					$("#SUB_LIST ul").remove();
					$("#DESTROY_SUB_LIST ul").remove();
					$("#CART_NUM").html("0");
					$("#CART_NUM").css("display","none");
//					sendSn("");
					$.navtabset.closeAll();
					$.HintInfo.clear();
				},
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示",info,null, null, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","加载数据超时！");
			});
		},
		sendVerifyCode:function(){
			if($.trim($("#LOGIN_NUM").val())==""||!$.verifylib.checkMbphone($.trim($("#LOGIN_NUM").val()))){
				MessageBox.alert("告警提示","手机号码非法！");
				return false;
			}
			var param= "&LOGIN_NUM="+$.trim($("#LOGIN_NUM").val())+"&LOGIN_TYPE_CODE="+$("#LOGIN_TYPE_CODE").attr("loginTypeCode")+"&ACTION=sendVerifyCode";
			$.beginPageLoading("发送验证码中。。。");
			$.ajax.submit(null, null, param, $.login.componentId, 
				function(data){
					$.endPageLoading();
					MessageBox.success("成功提示","验证码发送成功！");
				},
				function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示",info,null, null, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","加载数据超时！");
			});
		},
		transDigitalStyle:function(){
			var balance = $("#S_BALANCE").html();
			var realTimeFee = $("#S_REAL_TIME_FEE").html();
			var flow = $("#S_FLOW").html();
			if(balance){
				$("#S_BALANCE").html("<span class=\"num\">"+balance.substring(0,balance.indexOf("."))+"<span>"+balance.substring(balance.indexOf("."))+"</span></span>");
			}
			if(realTimeFee){
				$("#S_REAL_TIME_FEE").html("<span class=\"num\">"+realTimeFee.substring(0,realTimeFee.indexOf("."))+"<span>"+realTimeFee.substring(realTimeFee.indexOf("."))+"</span></span>");
			}
			if(flow){
				$("#S_FLOW").html("<span class=\"num\">"+flow.substring(0,flow.indexOf("."))+"<span>"+flow.substring(flow.indexOf("."))+"</span></span>");
			}
		},
		getNgbossRiskLevel:function(){
			/*if($.login.isLogin){
				return $.login.ngbossRiskLevel[$.login.getReqData().get("LOGIN_TYPE_CODE")]
			}*/
			return "0";
		},
		refreshScaInfo:function(){
			$("#REFRESH_BTN").attr("class","refresh refresh-loading");
			var param= "&ACTION=login&LOGIN_TYPE_CODE="+$.login.getReqData().get("LOGIN_TYPE_CODE")+"&LOGIN_NUM="+$.login.getReqData().get("LOGIN_NUM")+"&LOGIN_VAL="+$.login.getReqData().get("LOGIN_VAL");
			$.ajax.submit(null, null, param, $.login.componentId, 
				function(data){
					$("#REFRESH_BTN").attr("class","refresh");
					$.login.showInfo(data);
					
					var hintInfo = data.get("SUB_INFO").get(0);
					hintInfo.put("CUST_ID",data.get("CUST_ID"));
					
					$.login.showAcctInfo(hintInfo.get("SERIAL_NUMBER"),hintInfo.get("USER_ID"),hintInfo.get("REMOVE_TAG"));
					$.HintInfo.renderComponent("&HINT_INFO="+hintInfo.toString());
			},
				function(code, info, detail){
					$.endPageLoading();
					$("#REFRESH_BTN").attr("class","refresh");
					MessageBox.error("错误提示",info,null, null, detail);
				},function(){
					$("#REFRESH_BTN").attr("class","refresh");
					$.endPageLoading();
					MessageBox.alert("告警提示","加载数据超时！");
			});
		},
		 
        msgdispy:function(returncode,returnmsg){
			var idctl = document.getElementById("idctl");
            var idenNr = idctl.IDNo.trim(); // 身份证号
            try{
	            if(idenNr == undefined || idenNr == null || idenNr == ""){
	            	MessageBox.alert("提示信息", "身份证号码信息获取为空！");
	            	return;
	            }                       
	            $("#LOGIN_VAL").val(idenNr);
	        }catch (e){
	        	MessageBox.alert("提示信息", "身份证信息解析出错！");
	        }
		},
		processEvent:function(){
			var loginTypeCode = $.login.getReqData().get("LOGIN_TYPE_CODE");
			if(loginTypeCode){
				/*var param = "&ACCESS_NUM=" + $.login.getAccessNum()+"&IDS=" + $.login.getIDS()+"&SUBSCRIBER_INS_ID=" + $.login.getIDS().get("SUBSCRIBER_INS_ID")+"&NGBOSS_RISK_LEVEL=" + $.login.getNgbossRiskLevel()+"&LOGIN_TYPE_CODE=" + loginTypeCode;
				$("#STA_SET").unbind("tap");
				$("#chargeFee").unbind("tap");*/
				if(loginTypeCode.indexOf("W")>-1){
					/*$("#widenet_fee_info").css("display","");
					$("#STA_SET").tap(function(){
						var href = $.login.prefix+"?service=page/oc.person.cs.WideNetStatusChange&listener=onInitBusi"+param;
						openNavByUrl('宽带暂停恢复', href);
					});
					$("#widenetChargeFee").tap(function(){
						var href = "/customeraccount/customeraccount?service=page/cs.core.payment.BroadBandPayment"+param;
						openNavByUrl('有线宽带收费', href);
					});*/
				}else{
					$("#fee_info").css("display","");
					/*$("#STA_SET").tap(function(){
						var href = $.login.prefix+"?service=page/oc.person.changeprodstatus&listener=onInitBusi"+param;
						openNavByUrl('停开机业务受理', href);
					});
					$("#chargeFee").tap(function(){
						var href = "/customeraccount/customeraccount?service=page/cs.core.payment.Payment"+param;
						openNavByUrl('话费收取', href);
					});*/
				}
			}
		}
	}})
	$.login.init();
	
})(Wade);