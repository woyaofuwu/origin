var merchOffer = new Object();// Object.create(null);

function init() {
	//$("#showCartBtn").css("left", 50);

	initComponent();// 初始化组建信息
	//initPopuActive();//营销活动搜索组件

	$("#tabset1").afterSwitchAction(function(e, idx) {
//		resetAuthParams();
		suggest.hide();
//		setAuthParam();
		
		var newContent = $(".content[idx=" + idx + "]", e.target);
		var componentId = newContent.attr("componentId");
		var component = window[componentId];

		if (!checkAuth() && componentId != "fusion") {
			return;
		}
		var initFn = newContent.attr("initFn");
		var fn = component[initFn];
		if (typeof (fn) == "function") {
			fn.call(component);
		}
		
		var searchAfterAction = newContent.attr("searchAfterAction");
		setSuggestParam(newContent, newContent.attr("componentId"), searchAfterAction);
//		if (searchAfterAction && searchAfterAction.length > 0) {
//			suggest.show();
//			if (typeof (component[searchAfterAction]) == "function") {
//				suggest.setShowAction(function(ajaxData) {
//					component[searchAfterAction].call(component, ajaxData);
//				});
//			} else if (typeof (window[searchAfterAction]) == "function") {
//				suggest.setShowAction(function(ajaxData) {
//					if (component.hideError) {
//						component.hideError();
//					}
//					window["searchAfterAction"](component, ajaxData);
//				});
//			}
//			suggest.setParam("TYPE", component.type);
//			if (newContent.attr("componentId") == "fusion") {
//				suggest.setParam("EPARCHY_CODE", $("#STAFF_EPARCHY_CODE").val());
//			} else {
//				suggest.setParam("EPARCHY_CODE", merchOffer["EPARCHY_CODE"]?merchOffer["EPARCHY_CODE"]:$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"));
//			}
//		} else {
//			suggest.hide();
//		}
	});

	initTab();
}

//营销活动搜索组件初始化,
function initPopuActive() {
	window["mypop1"] = new Wade.Popup("mypop1",{
		visible:false,
		mask:true
	});
	
	window["offerAttrPop"] = new Wade.Popup("offerAttrPop",{
		visible:false,
		mask:true
	});//duhj
	
}

function initComponent() {
	typeof (product) != "undefined" && (product.showError("请先验证号码！") || product.bind({
		"order" : changeoffer.orderAction,
		"open" : changeoffer.openPage,
		"add" : changeoffer.addShoppingCart,
		"initDataParam" : changeoffer.initDataParam,
		"refreshOfferParam" : changeoffer.initDataParam
	}) || (product.extendFn("refresh", function() {
		$.auth.reloadAuth("110", "", "(function(d){changeoffer.refreshPartAfterAuth(d,product);}(data));");
	})));
	typeof (discnt) != "undefined" && (discnt.showError("请先验证号码！") || discnt.bind({
		"order" : changeoffer.orderAction,
		"add" : changeoffer.addShoppingCart,
		"initDataParam" : changeoffer.initDataParam,
		"refreshOfferParam" : changeoffer.initDataParam
	}) || (discnt.extendFn("refresh", function() {
		$.auth.reloadAuth("110", "", "(function(d){changeoffer.refreshPartAfterAuth(d,discnt);}(data));");
	})));
	typeof (service) != "undefined" && (service.showError("请先验证号码！") || service.bind({
		"order" : changeoffer.orderAction,
		"add" : changeoffer.addShoppingCart,
		"initDataParam" : changeoffer.initDataParam,
		"refreshOfferParam" : changeoffer.initDataParam
	}) || (service.extendFn("refresh", function() {
		$.auth.reloadAuth("110", "", "(function(d){changeoffer.refreshPartAfterAuth(d,service);}(data));");
	})));
	typeof (platservice) != "undefined" && (platservice.showError("请先验证号码！")||platservice.bind({
		"order" : changeoffer.orderAction,
		"add" : changeoffer.addShoppingCart,
		"initDataParam" : changeoffer.initDataParam,
		"refreshOfferParam" : changeoffer.initDataParam
	}) || (platservice.extendFn("refresh", function() {
		$.auth.reloadAuth("3700", "", "(function(d){changeoffer.refreshPartAfterAuth(d,platservice);}(data));");
	})));
	
	typeof (hotoffer) != "undefined" && (hotoffer.showError("请先验证号码！") || hotoffer.bind({
		"order" : changeoffer.orderAction,
		"open" : changeoffer.openPage,
		"add" : changeoffer.addShoppingCart,
		"initDataParam" : changeoffer.initDataParam,
		"refreshOfferParam" : changeoffer.initDataParam
	}) || (hotoffer.extendFn("refresh", function() {
		$.auth.reloadAuth("110", "", "(function(d){changeoffer.refreshPartAfterAuth(d,hotoffer);}(data));");
	})));



	window["suggest"] = new Suggest("suggest");
	suggest.setParam("EPARCHY_CODE", $("#STAFF_EPARCHY_CODE").val());

}

function initTab() {
	var activeIdx = getInitActiveIdx();
	if (activeIdx == tabset1.activeIdx) {
		var content = tabset1.contents[activeIdx];
		if (content) {
			var component = window[content.getAttribute("componentId")];
			var searchAfterAction = content.getAttribute("searchAfterAction");
			if (component) {
				suggest.setParam("TYPE", component.type);
				if (typeof (component[searchAfterAction]) == "function") {
					suggest.setShowAction(function(ajaxData) {
						component[searchAfterAction].call(component, ajaxData);
					});
				} else if (typeof (window[searchAfterAction]) == "function") {
					suggest.setShowAction(function(ajaxData) {
						if (component.hideError) {
							component.hideError();
						}
						window["searchAfterAction"](component, ajaxData);
					});
				}
			}
		} else {
			suggest.hide();
		}
	}
}

function getInitActiveIdx() {
	var activeIdx = tabset1.activeIdx;
	var param = location.search.substr(1);
	var reg = new RegExp("(^|&)init_component=([^&]*)(&|$)");
	var initArr = param.match(reg);
	if (initArr != null) {
		var paramComponentId = initArr[2];
		tabset1.contents.forEach(function(value, index, array) {
			if (paramComponentId == value.getAttribute("componentId")) {
				activeIdx = index;
			}
		});
	}
	return activeIdx;
}

function refreshPartAfterAuth(data) {
	var activeIdx = getInitActiveIdx();
	var content = tabset1.contents[activeIdx];
	var componentId = content.getAttribute("componentId");

	if (activeIdx != tabset1.activeIdx) {
		tabset1.switchTo(activeIdx);
	} else if (typeof (product) != "undefined") {
		var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
		var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
		var userId = data.get("USER_INFO").get("USER_ID");

		var param = "&USER_ID=" + userId + "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER") + "&EPARCHY_CODE=" + eparchyCode
				+ "&PRODUCT_ID=" + userProductId + "&ROUTE_EPARCHY_CODE=" + eparchyCode;

		$.beginPageLoading("加载中。。。");
		$.ajax.submit('', 'loadChildInfo', param, '', function(ajaxData) {
			$.endPageLoading();

			product.setEnv(eparchyCode, userProductId);
			service.setEnv(eparchyCode, userProductId);
			discnt.setEnv(eparchyCode, userProductId);
			platservice.setEnv(eparchyCode, userProductId);
			hotoffer.setEnv(eparchyCode, userProductId);

			suggest.setParam("USER_ID", userId);
			suggest.setParam("SERIAL_NUMBER", data.get("USER_INFO").get("SERIAL_NUMBER"));
			suggest.setParam("EPARCHY_CODE", eparchyCode);
			suggest.setParam("USER_PRODUCT_ID", userProductId);

			merchOffer["USER_PRODUCT_ID"] = userProductId;
			merchOffer["EPARCHY_CODE"] = eparchyCode;
			
			
			if (ajaxData.containsKey("NEXT_PRODUCT")) {
				merchOffer["NEXT_PRODUCT_ID"] = ajaxData.get("NEXT_PRODUCT").get("PRODUCT_ID");
				merchOffer["NEXT_PRODUCT_START_DATE"] = ajaxData.get("NEXT_PRODUCT").get("START_DATE");
				if("product" == componentId) {
					product.showError("您已预约产品变更，不能再次变更！");
				}
			} 
//			else {
//				var component = window[componentId];
//				if(component.initData) {
//					component.initData()
//				}
//			}
			
			//执行initFn方法
			var component = window[componentId];
			var fn = component[content.getAttribute("initFn")];
			if (typeof (fn) == "function") {
				$.auth.setRuleActionFlag("1");
				fn.call(component);
			}
			setSuggestParam(content, componentId, content.getAttribute("searchAfterAction"));
			
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
			product.showError(error_info);
		});
	}
};

function getOtherParam(type) {
	var authData = $.auth.getAuthData();
	if (("refreshOffer" == type || "initData" == type) && authData) {
		var userInfo = authData.get("USER_INFO");
		return "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER") + "&USER_ID=" + userInfo.get("USER_ID");
	}
	return "";
}

function checkAuth() {
	var authData = $.auth.getAuthData();
	if (authData) {
		var userInfo = authData.get("USER_INFO");
		return userInfo && userInfo.length > 0;
	}
	return false;
}

function resetAuthParams() {

	var tradeTypeCode = $("#AUTH_SUBMIT_BTN").attr("tradeTypeCode");
	if (tradeTypeCode != "110") {
		$("#TRADE_TYPE_CODE").val("110");
		$("#AUTH_SUBMIT_BTN").attr("tradeTypeCode", "110");
		$("#TRADE_TYPE_CODE").attr("orderTypeCode", "110");
		$("#AUTH_SUBMIT_BTN").attr("beforeAction", "");
		$("#AUTH_SUBMIT_BTN").attr("tradeAction", "refreshPartAfterAuth(data)");
	}
}

function affirmAction(type) {
	if (type == "submit") {

	} else {

	}
	$("#REMARK").val("");
}

/** *****购物车********* */
function openShopCartPage() {
	closeNavByTitle("购物车");
	var authData = $.auth.getAuthData();
	var param = "&AUTO_AUTH=true&DISABLED_AUTH=true&SERIAL_NUMBER=";
	if (authData) {
		var serialNum = authData.get('USER_INFO').get('SERIAL_NUMBER');
		param += serialNum;
	}
	openNav("购物车", "merch.merchShoppingCart", "", param, '/order/iorder');
}
/** *****融合业务********* */
function openFusionPage(offer) {
	var offerName = offer.get("OFFER_NAME");
	var offerCode = offer.get("OFFER_CODE");
	var authData = $.auth.getAuthData()
	var param = "&FLG_PZ=1&OPEN_TYPE=1&OFFER_CODE=" + offerCode;
	if (authData && authData.length > 0) {
		var userInfo = authData.get("USER_INFO");
		param += "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	}
	openNav(offerName, 'fusion.FusionAccept', 'init', param);
}

function getSpecialAreaParam() {
	var content = tabset1.contents[tabset1.activeIdx];
	if (content.getAttribute("componentId") == "fusion") {
		return "&USER_CITY_CODE=" + $("#STAFF_CITY_CODE").val();
	} else {
		return "&USER_CITY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("CITY_CODE");
	}
}

function setSuggestParam(content, componentId, searchAfterAction) {
	var component = window[componentId];
	
	if (searchAfterAction && searchAfterAction.length > 0) {
		suggest.show();
		if (typeof (component[searchAfterAction]) == "function") {
			suggest.setShowAction(function(ajaxData) {
				component[searchAfterAction].call(component, ajaxData);
			});
		} else if (typeof (window[searchAfterAction]) == "function") {
			suggest.setShowAction(function(ajaxData) {
				if (component.hideError) {
					component.hideError();
				}
				window["searchAfterAction"](component, ajaxData);
			});
		}
		suggest.setParam("TYPE", component.type);
		if ("fusion" == componentId) {
			suggest.setParam("EPARCHY_CODE", $("#STAFF_EPARCHY_CODE").val());
		} else {
			suggest.setParam("EPARCHY_CODE", merchOffer["EPARCHY_CODE"]?merchOffer["EPARCHY_CODE"]:$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"));
		}
	} else {
		suggest.hide();
	}
}

function setAuthParam(componentId){
	if("saleActiveFilter" == componentId) {//营销活动
		$("#TRADE_TYPE_CODE").val("240");
		$("#AUTH_SUBMIT_BTN").attr("tradeTypeCode", "240");
		$("#TRADE_TYPE_CODE").attr("orderTypeCode", "240");
		$("#AUTH_SUBMIT_BTN").attr("beforeAction", "setRuleParam()");
		$("#AUTH_SUBMIT_BTN").attr("tradeAction", "refreshMarketPartAtferAuth(data)");
	}else if("userPlatSvcsList" == componentId) {//平台业务
		$("#TRADE_TYPE_CODE").val("3700");
		$("#AUTH_SUBMIT_BTN").attr("tradeTypeCode", "3700");
		$("#TRADE_TYPE_CODE").attr("orderTypeCode", "3700");
//		$("#AUTH_SUBMIT_BTN").attr("beforeAction", "setRuleParam()");
//		$("#AUTH_SUBMIT_BTN").attr("tradeAction", "refreshMarketPartAtferAuth(data)");
	}else if("userPlatSvcsList" == componentId) {//产品变更
		$("#TRADE_TYPE_CODE").val("110");
		$("#AUTH_SUBMIT_BTN").attr("tradeTypeCode", "110");
		$("#TRADE_TYPE_CODE").attr("orderTypeCode", "110");
//		$("#AUTH_SUBMIT_BTN").attr("beforeAction", "setRuleParam()");
//		$("#AUTH_SUBMIT_BTN").attr("tradeAction", "refreshMarketPartAtferAuth(data)");
	}
}


