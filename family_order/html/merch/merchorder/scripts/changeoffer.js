if (typeof ChangeOffer === "undefined") {
    window["ChangeOffer"] = function () {
    };
    var changeoffer = new ChangeOffer();
}
(function () {
    $.extend(ChangeOffer.prototype, {
    	initDataParam : function() {
    		var authData = $.auth.getAuthData();
    		if (authData) {
    			var userInfo = authData.get("USER_INFO");
    			return "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER") + "&USER_ID=" + userInfo.get("USER_ID") + "&TRADE_TYPE_CODE=110";
    		}
    		return "";
    	},
    	refreshPartAfterAuth : function(data, obj) {
    		var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
    		var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
    		var userId = data.get("USER_INFO").get("USER_ID");

    		var param = "&USER_ID=" + userId + "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER") + "&EPARCHY_CODE=" + eparchyCode
    				+ "&PRODUCT_ID=" + userProductId + "&ROUTE_EPARCHY_CODE=" + eparchyCode;

    		if ((data.get("USER_INFO").get("ACCT_TAG") != "0") && (data.get("USER_INFO").get("USER_TYPE_CODE") != "B")) {
    			MessageBox.alert("激活状态校验", "此号码未激活不能进行产品变更");
    			return false;
    		}
    		$.beginPageLoading("加载中。。。");
    		$.ajax.submit('', 'loadChildInfo', param, '', function(ajaxData) {
    			$.endPageLoading();

    			suggest.setParam("USER_ID", userId)
    			suggest.setParam("SERIAL_NUMBER", data.get("USER_INFO").get("SERIAL_NUMBER"));
    			suggest.setParam("EPARCHY_CODE", eparchyCode);
    			suggest.setParam("USER_PRODUCT_ID", ajaxData.containsKey("NEXT_PRODUCT") ? ajaxData.get("NEXT_PRODUCT").get("PRODUCT_ID") : userProductId);
    			suggest.clear();
    			
    			merchOffer["USER_PRODUCT_ID"] = userProductId;
    			merchOffer["EPARCHY_CODE"] = eparchyCode;

    			if (obj.id == "product") {
    				if (ajaxData.containsKey("NEXT_PRODUCT")) {
    					merchOffer["NEXT_PRODUCT_ID"] = ajaxData.get("NEXT_PRODUCT").get("PRODUCT_ID");
    					merchOffer["NEXT_PRODUCT_START_DATE"] = ajaxData.get("NEXT_PRODUCT").get("START_DATE");
    					product.showError("您已预约产品变更，不能再次变更！");
    				}
    			}
    			obj.setEnv(eparchyCode, userProductId);
    			obj.clearSelectTags();
    			obj.initData();

    		}, function(error_code, error_info) {
    			$.endPageLoading();
    			MessageBox.error("错误提示", error_info);
    			obj.showError(error_info);
    		});
    	},

    	renderMyOfferList : function() {
    		$.auth.reloadAuth("110", "", "changeoffer.loadChildInfo(data)");
    	},
    	
    	renderUserPlatSvc : function() {
    		$.auth.reloadAuth("3700", "", "changeoffer.loadPlatSvcInfo(data)");
    	},
    	
    	loadPlatSvcInfo : function(data) {
    		$.beginPageLoading("加载中。。。");
    		var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
    		var platFrame = document.getElementById("platFrame").contentWindow;
    		if(platFrame && platFrame.document) {
    			var frameState = platFrame.document.readyState;
    			if(!frameState || frameState!="complete")
    			{
    				var timmer = setInterval(function(){
    					var state = platFrame.document.readyState;
    					if(state && state=="complete") {
    						clearInterval(timmer);
//    						console.info("state:" + state);
//    	        			var authSerialNumber = platFrame.document.getElementById("AUTH_SERIAL_NUMBER").value;
//    	        			if(!authSerialNumber || authSerialNumber == "")
//    	        			{
//    	        				platFrame.document.getElementById("AUTH_SERIAL_NUMBER").value = serialNumber;
//    	        			}
    	        			var platAuthBtnObj = platFrame.document.getElementById("AUTH_SUBMIT_BTN");
    	            		platAuthBtnObj.click();
    	        		}
    				}, 1000);
    			}else {
//        			var authSerialNumber = platFrame.document.getElementById("AUTH_SERIAL_NUMBER").value;
//        			if(!authSerialNumber || authSerialNumber == "")
//        			{
//        				platFrame.document.getElementById("AUTH_SERIAL_NUMBER").value = serialNumber;
//        			}
        			var platAuthBtnObj = platFrame.document.getElementById("AUTH_SUBMIT_BTN");
            		platAuthBtnObj.click();
    			}
    		}
    	},
    	
    	
    	loadChildInfo : function(data) {
    		// 海南添加
//            if (typeof changeProductExtend !== "undefined"
//                    && changeProductExtend.afterSubmitSerialNumber) {
//                changeProductExtend.afterSubmitSerialNumber();
//            }
    		
    		var userInfo = data.get("USER_INFO");
    		var userId = userInfo.get("USER_ID");
    		var eparchyCode = userInfo.get("EPARCHY_CODE");
    		var userProductId = userInfo.get("PRODUCT_ID");
    		var param = "&USER_ID=" + userId + "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER") + "&PRODUCT_ID=" + userProductId;
    		$.beginPageLoading("加载中。。。");
    		$.ajax.submit('', 'loadChildInfo', param, '', function(ajaxData) {
    			myProduct.displayEffectNow("none");
    			myProduct.renderProduct(ajaxData.get("USER_PRODUCT"), ajaxData.get("NEXT_PRODUCT"), ajaxData.get("SYS_TIME"));
    			myProduct.resetBtnStyle("none", "none");
    			
//    			var product = ajaxData.get("USER_PRODUCT");
//    			var productName = "[" + product.get("PRODUCT_ID") + "]" + product.get("PRODUCT_NAME")
//    			var productDesc = product.get("PRODUCT_DESC")
//    			var productTime = product.get("START_DATE").substring(0, 10) + " ~ " + product.get("END_DATE").substring(0, 10);
//    			$("#productName").html(productName);
//    			$("#productDesc").html(productDesc);
//    			$("#productDesc").attr("title", productDesc);
//    			$("#productTime").html(productTime);
    			
    			var nextProduct = ajaxData.get("NEXT_PRODUCT");
    			var nextProductId = null;
    			var nextProductStartDate = null;
    			if (nextProduct) {
    				nextProductId = nextProduct.get("PRODUCT_ID");
    				nextProductStartDate = nextProduct.get("START_DATE");
    			}
    			myOfferList.setEnv(userId, eparchyCode, "110", userProductId, nextProductId, nextProductStartDate);
    			myOfferList.renderComponent();
    			
    			userPlatSvcsList.clearCache();
    			userPlatSvcsList.renderComponent(userId, eparchyCode);
    			
    			// 海南添加
//                if (typeof changeProductExtend !== "undefined"
//                        && changeProductExtend.afterLoadChildInfo) {
//                    changeProductExtend.afterLoadChildInfo(ajaxData);
//                }
                
//    			$("#USER_EPARCHY_CODE").val(eparchyCode);
//    			$("#NEW_PRODUCT_START_DATE").val("");
//                $("#OLD_PRODUCT_END_DATE").val("");
//                
//    			var acctDayInfo = data.get("ACCTDAY_INFO");
//                selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),
//                        acctDayInfo.get("FIRST_DATE"),
//                        acctDayInfo.get("NEXT_ACCT_DAY"),
//                        acctDayInfo.get("NEXT_FIRST_DATE"));
//                
//    			selectedElements.renderComponent("&USER_ID=" + userId, eparchyCode);
    		}, function(error_code, error_info) {
    			$.endPageLoading();
    			MessageBox.error("错误提示", error_info);
    		});
    	},

    	cancelOffers : function(type) {
    		var offerdata = myOfferList.getSubmitData();
    		var platdata = userPlatSvcsList.getOperElements();
    		if (offerdata.length == 0 && platdata.length == 0) {
    			MessageBox.alert("提示","您没有进行任何操作，不能提交");
    			return false;
    		}
    		var param = "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") + "&SELECTED_ELEMENTS=" + offerdata.toString()
    				+ "&PLAT_SELECTED_ELEMENTS=" + platdata.toString()
    				+ "&USER_ID=" + $.auth.getAuthData().get("USER_INFO").get("USER_ID");
    		$.cssubmit.addParam(param);
    		$.cssubmit.setParam("REG_SVC", "SS.MerchChangeProductSVC.tradeReg");
    		$.cssubmit.setParam("REMARK", $("#REMARK").val());
    		if (type == "1") {
    			$("#CSSUBMIT_BUTTON").click();
    		} else {
    			$("#ADD_SHOPPING_CART").click();
    		}
    	},
    	
    	openPage : function(offer) {
    		if(offer.get("OFFER_TYPE")=="K"){ 
    			//热门商品标签页打开的时候是按110鉴权的,热门点击营销方案需要再去用240去鉴权下
    			var user=$.auth.getAuthData().get("USER_INFO");
    			    user.put("TRADE_TYPE_CODE","240");                    
		        $.ajax.submit('', 'checkBeforeTrade', user, '', function(ajaxData) {
			    var errorSet = ajaxData.get("TIPS_TYPE_ERROR");
			    //告警提示
			    if(errorSet && errorSet.length>0)
			    {
				    errorSet.each(function(item, index, totalCount)
				    {
					    window.alert(item.get("TIPS_INFO"));
				    });
				    return ;
			    }
			    
				var campnType =offer.get("CAMPN_TYPE");
				var hotProductId =offer.get("OFFER_CODE");
				
				//现网YX10,YX11产商品都没有配置,应该都不会出现这种,最好加一下
                if(changeoffer.isiPhone6CampnType(campnType)){ 
            		MessageBox.alert("提示","IPHONE6合约送费活动需要输入IMEI再去选择营销方案,热门商品配置不符合要求,需要去营销活动标签页去办理！");
            		return false;
                }
            	
                if(changeoffer.isTerminalCampnType(campnType)){                	
                	showPopup('mypopHot', 'hot-search');
                	//页面加热门产品id
                	$("#HOT_PRODUCT_ID").val(hotProductId);
                	$("#HOT_CAMPN_TYPE").val(campnType);
                }else{
					changeoffer.openSaleHotList(offer);
                }  
				//changeoffer.openSaleHotList(offer);
                			    
		       }, function(error_code, error_info) {
			        $.endPageLoading();
			       MessageBox.error("错误提示", error_info);
		         });   			    			        
    		}else{
        		var offerName = offer.get("OFFER_NAME");
        		var offerCode = offer.get("OFFER_CODE");
        		var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
        		var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
        		var param = "&SERIAL_NUMBER=" + serialNumber + "&USER_ID" + userId +"&NEW_PRODUCT_ID=" + offerCode
        				+ "&NEW_PRODUCT_NAME=" + offerCode + "&AUTO_AUTH=true";
        		
        		$.beginPageLoading("加载中。。。");
        		$.ajax.submit('', 'checkShoppingCartForProduct', param, '', function(ajaxData) {
    				$.endPageLoading();
    				openNav(offerName, 'merch.MerchChangeProduct', 'init', param);
    			}, function(error_code, error_info) {
    				$.endPageLoading();
    				MessageBox.error("错误提示", error_info);
    			});
    		}
    	},
        isTerminalCampnType: function (campnType) {
            var terminalCampnType = "YX03|YX06|YX07|YX08|YX09";
            return campnType !== "" && terminalCampnType.indexOf(campnType) > -1;
        },
        isiPhone6CampnType: function (campnType) {
            var iPhone6CampnType = "YX11";
            return campnType !== "" && iPhone6CampnType.indexOf(campnType) > -1;
        },
    	addShoppingCart : function(offer) {
    		if(offer.get("OFFER_TYPE")=="Z"){//duhj  获取属性信息 ,第一次加载商品的时候不去查询属性信息，避免循环调用产商品接口，耗费时间太长，异步加载属性信息   			
    			changeoffer.addPlatCart(offer);
    		}else{
        		var offerName = offer.get("OFFER_NAME");
        		var tips = "是否将商品【" + offerName + "】加入购物车？是的话请点击确定。";  
        		if("P" == offer.get("OFFER_TYPE")) {
        			tips += "<br/>此次加入购物车的商品【" + offerName + "】结算后将立即生效，如需选择预约时间，请进入商品详情界面选择预约时间后再加购物车！";
        		}
        		MessageBox.confirm("提示信息", tips, function(btn) {
        			if(btn == "ok") {
            			if (offer.containsKey("ATTR_PARAM")) {
            				offerAttr.render(offer, merchOffer["EPARCHY_CODE"], false, function() {
            					changeoffer.addShoppingCartConfirm(offer);
            				});
    	    			} else {
    	    				changeoffer.addShoppingCartConfirm(offer);
    	    			}
        			}
        		});
    		}
    		
//    		$("#PELI_" + offer.get("OFFER_TYPE") + "_" + offer.get("OFFER_CODE")).attr("className","gray");
//    		$("#PELI_" + offer.get("OFFER_TYPE") + "_" + offer.get("OFFER_CODE")).append("<div class='statu statu-right statu-blue'>购物车</div>");
//    		$("#PELI_" + offer.get("OFFER_TYPE") + "_" + offer.get("OFFER_CODE")).find(".fn").remove();
//    		$("#PELI_" + offer.get("OFFER_TYPE") + "_" + offer.get("OFFER_CODE")).find(".main").attr("handle", "");
    	},
    	
    	addShoppingCartConfirm : function(offer) {
    		$.feeMgr.clearFeeList("110");
    		var offerType = offer.get("OFFER_TYPE");
			// 校验购物车
			var param = "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") + "&OFFER="
					+ encodeURIComponent(offer.toString());
			$.beginPageLoading("加载中。。。");
			$.ajax.submit('', 'addShoppingCartConfirm', param, '', function(ajaxData) {
				$.endPageLoading();
				if (ajaxData && ajaxData.get("IS_CONFIRM", "false") == "true") {
//					var context = "购物车中有预约产品变更，本次加入的商品，在新产品下不存在，不能订购";
					var context = ajaxData.get("CONFIRM_MESSAGE");
					if(""!=context && null!= context){
						MessageBox.error("错误提示", context);
						return false;
					}
				}
				
				var feeData = ajaxData.get("FEE_DATA");
    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
    				var feeSize = feeData.length;
    				for(var j=0;j<feeSize;j++){
    					var fee = feeData.get(j);
    					$.feeMgr.insertFee(fee);
    				}
    			}
				
				changeoffer.setOrderParam(offer, 0);
			}, function(error_code, error_info) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info);
			});
    	},
    	
    	orderAction : function(offer) {
    		if(offer.get("OFFER_TYPE")=="Z"){//duhj  获取属性信息 ,第一次加载商品的时候不去查询属性信息，避免循环调用产商品接口，耗费时间太长，异步加载属性信息   			
    			changeoffer.addPlatOrder(offer);
    		}else{
        		var offerName = offer.get("OFFER_NAME");
        		var tips = "是否要快捷订购【" + offerName + "】？是的话请点击确定。";
        		if("P" == offer.get("OFFER_TYPE")) {
        			tips += "<br/>此次订购的商品【" + offerName + "】将立即生效，如需选择预约时间，请进入商品详情界面选择预约时间后再提交！";
        		}
        		MessageBox.confirm("提示信息", tips, function(btn) {
        			if(btn == "ok") {
        				if (offer.containsKey("ATTR_PARAM")) {
            				offerAttr.render(offer, merchOffer["EPARCHY_CODE"], false, function() {
            					changeoffer.setOrderParam(offer, 1);
            				});
            			} else {
            				changeoffer.setOrderParam(offer, 1);
            			}
        			}
        		});	
    		}

    	},
    	
    	setOrderParam : function(offer, type) {
    		var offerType = offer.get("OFFER_TYPE");
    		var offerCode = offer.get("OFFER_CODE");
    		var groupId = offer.get("GROUP_ID");
    		var productId = offer.get("PRODUCT_ID");
    		var userInfo = $.auth.getAuthData().get("USER_INFO");
    		var bookingDate = "";
    		$.cssubmit.setParam("SERIAL_NUMBER", userInfo.get("SERIAL_NUMBER"));
    		$.cssubmit.setParam("USER_ID", userInfo.get("USER_ID"));
    		if(offer.get("START_DATE") && offer.get("START_DATE")!="") {
    			$.cssubmit.setParam("BOOKING_DATE", offer.get("START_DATE"));
    			bookingDate =  offer.get("START_DATE");
    		}
    		if(offerType=="Z"){
        		$.cssubmit.setParam("REG_SVC", "SS.MerchSinglePlatSVC.tradeReg");
				var selectedElements = $.DatasetList();
				selectedElements.add(offer);
//				console.log(selectedElements.toString());
				$.cssubmit.setParam("SELECTED_ELEMENTS", encodeURIComponent(selectedElements.toString()));
				$.cssubmit.setParam("REMARK", $("#REMARK").val());
				changeoffer.submit(type);

    		}else{
        		$.cssubmit.setParam("REG_SVC", "SS.MerchChangeProductSVC.tradeReg");
        		if (offerType != "P") {
        			var param = "&OFFER_CODE=" + offerCode + "&OFFER_TYPE=" + offerType + "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER") + "&USER_ID="
        					+ userInfo.get("USER_ID") + "&GROUP_ID=" + groupId +"&PRODUCT_ID=" + productId + "&BOOKING_DATE=" + bookingDate;
        			$.beginPageLoading("加载中。。。");
        			$.ajax.submit('', 'getOrderAntinomyOffers', param, '', function(ajaxData) {
        				$.endPageLoading();
        				var selectedElements = $.DatasetList();
        				if (ajaxData && ajaxData.length > 0) {
        					var tips = ajaxData.get(0).get("DEL_TIPS");
        					if (tips && !confirm(tips)) {
        						$.feeMgr.clearFeeList("110");
        						return;
        					}
        					ajaxData.each(function(item) {
        						selectedElements.add(item);
        					});
        				}
        				selectedElements.add(offer);
//        				console.log(selectedElements.toString());
        				$.cssubmit.setParam("SELECTED_ELEMENTS", encodeURIComponent(selectedElements.toString()));
        				$.cssubmit.setParam("REMARK", $("#REMARK").val());
        				changeoffer.submit(type);
        			}, function(error_code, error_info) {
        				$.endPageLoading();
        				MessageBox.error("错误提示", error_info);
        			});
        			
        		} else {
        			var selectedElements = $.DatasetList();
        			var data = encodeURIComponent(selectedElements.toString());
        			var newProductId = offerCode;
        			if (data && data.length > 0 || newProductId){
        				var param = "&SELECTED_ELEMENTS=" + data.toString() + "&NEW_PRODUCT_ID=" + newProductId + "&SERIAL_NUMBER="
        				+ $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") + "&NEW_PRODUCT_NAME=" + offer.get("OFFER_NAME")
        				+"&USER_PRODUCT_ID="+userInfo.get("PRODUCT_ID")+ "&USER_ID=" +userInfo.get("USER_ID");
        				$.beginPageLoading("正在校验中···");
        				$.ajax.submit(null, "checkElementParamIntegrality", param, null, function(indata){
        					$.endPageLoading();
        					var error = indata.get("ERROR_INFO");
        					var openOrderDetail = indata.get("OPEN_ORDER");
        					if (error && error != null && error != '') {
//        						MessageBox.confirm("提交确认",error,function(btn){
//        							if(btn == 'ok'){
//        								merchOffer["NEW_PRODUCT_ID"] = offerCode;
//        								$.cssubmit.setParam("NEW_PRODUCT_ID", offerCode);
//        								$.cssubmit.setParam("REMARK", $("#REMARK").val());
//        								changeoffer.submit(type);
//        							}
//        						});
        						//快捷订购 海南特殊业务逻辑处理 ：统一在详情页处理 guohuan
        						if ("0" == openOrderDetail){
	        						MessageBox.confirm("提示信息", error, function(btn) {
	        							if(btn == 'ok') {
	        								changeoffer.openPage(offer);
	        							}
	        						})
        						}else{
        							MessageBox.confirm("提示信息", error, function(btn) {
	        							if(btn == 'ok') {
	        								merchOffer["NEW_PRODUCT_ID"] = offerCode;
	                						$.cssubmit.setParam("NEW_PRODUCT_ID", offerCode);
	                						$.cssubmit.setParam("REMARK", $("#REMARK").val());
	                						changeoffer.submit(type);
	        							}
	        						})
        						}
        					}
        					else{
        						merchOffer["NEW_PRODUCT_ID"] = offerCode;
        						$.cssubmit.setParam("NEW_PRODUCT_ID", offerCode);
        						$.cssubmit.setParam("REMARK", $("#REMARK").val());
        						changeoffer.submit(type);
        					}
        				}, function(error_code, error_info) {
        					MessageBox.error("错误", error_info);
        					$.endPageLoading();
        					return false;
        				});
        			}
        		}	
    		}

    	},
    	
    	submit : function(type) {
    		if (type == 0) {
    			$("#ADD_SHOPPING_CART").click();
    		} else {
    			$("#CSSUBMIT_BUTTON").click();
    		}
    	},
    	
    	addPlatCart :function(offer){
    		changeoffer.checkPlatOffer(offer);
    		var userId=$.auth.getAuthData().get("USER_INFO").get("USER_ID");
    		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
			var param = "&OFFER_CODE=" + offer.get("OFFER_CODE") + "&OFFER_TYPE=" + offer.get("OFFER_TYPE")+"&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode;
    		$.ajax.submit('', 'addPlatAttr', param, '', function(ajaxData) {
				if (ajaxData && ajaxData.length > 0) {
	    			offer.put("ATTR_PARAM",ajaxData);
				}
				changeoffer.addPlatShoppingCart(offer);
    		}, function(error_code, error_info) {
    			$.endPageLoading();
    			MessageBox.error("错误提示", error_info);
    		});
    		
    	},
    	addPlatShoppingCart : function(offer) {    			
        		var offerName = offer.get("OFFER_NAME");
        		var tips = "是否将商品【" + offerName + "】加入购物车？是的话请点击确定。";    		
        		MessageBox.confirm("提示信息", tips, function(btn) {
        			if(btn == "ok") {
            			if (offer.containsKey("ATTR_PARAM")) {
            				offerAttr.render(offer, merchOffer["EPARCHY_CODE"],false, function() {
            					changeoffer.addShoppingCartConfirm(offer);
            				});
    	    			} else {
    	    				changeoffer.addShoppingCartConfirm(offer);
    	    			}
        			}
        		});	
    	},
    	
    	addPlatOrder :function(offer){
    		changeoffer.checkPlatOffer(offer);
    		var userId=$.auth.getAuthData().get("USER_INFO").get("USER_ID");
    		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
			var param = "&OFFER_CODE=" + offer.get("OFFER_CODE") + "&OFFER_TYPE=" + offer.get("OFFER_TYPE")+"&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode;
    		$.ajax.submit('', 'addPlatAttr', param, '', function(ajaxData) {
				if (ajaxData && ajaxData.length > 0) {
	    			offer.put("ATTR_PARAM",ajaxData);
				}
				changeoffer.addPlatOrderNow(offer);
    		}, function(error_code, error_info) {
    			$.endPageLoading();
    			MessageBox.error("错误提示", error_info);
    		});
    		
    	},
    	addPlatOrderNow:function(offer){    		
    		var offerName = offer.get("OFFER_NAME");
    		var tips = "是否要快捷订购【" + offerName + "】？是的话请点击确定。";    		
    		MessageBox.confirm("提示信息", tips, function(btn) {
    			if(btn == "ok") {
    				if (offer.containsKey("ATTR_PARAM")) {
        				offerAttr.render(offer, merchOffer["EPARCHY_CODE"], false,function() {
        					changeoffer.setOrderParam(offer, 1);
        				});
        			} else {
        				changeoffer.setOrderParam(offer, 1);
        			}
    			}
    		});
    		
    	},
    	checkPlatOffer:function(offer){ 		
    		var serviceId=offer.get("OFFER_CODE");
            if (serviceId == '98001901' || serviceId == '80002009' || serviceId == '55687442'
                || serviceId == '55627768'){
                MessageBox.alert("提示","此用户可办理相应福包业务，可通过产品变更界面进行办理。");
            }
    		
    	},
    	
    	openSaleHotList:function(offer){
            var productId = offer.get("OFFER_CODE");//"69900321";//
            var campnType =offer.get("CAMPN_TYPE");//"YX02";
            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
            var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
            var custId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");
        	var eparchyCode = $.auth.getAuthData().get('USER_INFO').get('EPARCHY_CODE');

//        	if ("_YX03_YX08_YX09_YX07_YX11_".indexOf(campnType) != -1)
//            {
//        		MessageBox.alert("提示","终端类营销活动请去营销活动标签页去办理！");
//        		return false;
//            } 
        	
            var params = "&SERIAL_NUMBER=" + serialNumber
                    + "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + productId
                    + "&SALEACTIVE_CUST_ID=" + custId
                    + "&SALE_EPARCHY_CODE=" + eparchyCode
                    + "&SALEACTIVE_USER_ID=" + userId;

            popupPage("热门营销活动列表", "saleactive.sub.MerchHotList", "queryHotSaleList", params, null, "c_popup c_popup-half c_popup-half-hasBg");
    	}
    	
    	
    });
})();