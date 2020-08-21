if (typeof (ZiYouBusiDynamicPage) == "undefined") {
	window["ZiYouBusiDynamicPage"] = function() {
	};
	var ziYouBusiDynamicPage = new ZiYouBusiDynamicPage();
}
(function() {
	
	$.extend(ZiYouBusiDynamicPage.prototype, {
		renderComponent : function(param, asyncFlag) {// 支持同步，解决动态刷新
			if (asyncFlag == false || asyncFlag == "false") {
				asyncFlag = false;
			} else {
				asyncFlag = true;
			}
			$.beginPageLoading("玩命加载中。。。");   
			$.ajax.submit(null, null, param, "DynamicQryBlackList", function() {
				
				var accessNum=$("#ACCESS_NUM").val();
				var pagetype = $("#TRADE_TYPE_CODE").val();//业务类型
				if((pagetype!='1')&&(pagetype!='63')&&(pagetype!='')&&(pagetype!=undefined)){//手机阅读-书籍信息查询，不将号码显示在书项名称上面
					$("#QRYINFO0").val(accessNum);
				}
				showPopup('qryPopup','qryPopupItem');
				$.endPageLoading();
				
			}, null, {
				async : asyncFlag
			});

		},
		changepage : function(){
			var pagetype = $("#SEL").val();
			$("#TRADE_TYPE_CODE").val(pagetype);
			var pagetype = $("#TRADE_TYPE_CODE").val();
			var routeeparchy = $("#ROUTE_EPARCHY_CODE").val();
			var svcParam = '&REFRESH=1&LV=1&ROUTE_EPARCHY_CODE=' + routeeparchy + '&TRADE_TYPE_CODE='+pagetype;
			this.renderComponent(svcParam,true);
		}
		
	});
})();
function initCsPage(access_number){
	
	var pagetype = $("#TRADE_TYPE_CODE").val();
	if((pagetype!='1')&&(pagetype!='63')){//手机阅读-书籍信息查询，不将号码显示在书项名称上面
		$("#QRYINFO0").val(access_number);
		showPopup('qryPopup','qryPopupItem');
	}
} 
$(window).load(function(){
	ziYouBusiDynamicPage.changepage();
})