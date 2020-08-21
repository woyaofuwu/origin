/*!
 * 统一外框菜单打开js
 * auth:xiedx@asiainfo.com
 * date:2013-08-14
 */
(function($) {
	$.menuopener = {

	    openMenu : function(menuId, menuTitle, menuAddr,offerId, noBind) {
		    //M终端mktms005菜单打开薪火联盟应用
		    if ($.os.phone && "mktms005" == menuId) {
			    execute("allianceGame", []);
		    }
		    // PC版本
		    if ($.login) {
			    if ($.login.getAccessNum() && !noBind) {
				    menuAddr += "&ACCESS_NUM=" + $.login.getAccessNum()+"&SERIAL_NUMBER="+$.login.getAccessNum();
			    }
			    if ($.login.getIDS()) {
				    menuAddr += "&IDS=" + $.login.getIDS();
				    if(!noBind){
				    	menuAddr += "&SUBSCRIBER_INS_ID=" + $.login.getIDS().get("SUBSCRIBER_INS_ID");
				    }
			    }
			    menuAddr += "&NGBOSS_RISK_LEVEL=" + $.login.getNgbossRiskLevel();
			    var loginTypeCode = "";
			    if ($.login.isLogin) {
				    loginTypeCode = $.login.getReqData().get("LOGIN_TYPE_CODE");
			    }
			    menuAddr += "&LOGIN_TYPE_CODE=" + loginTypeCode;
		    }

		    //手机版本
		    var frame = document.getElementById('custAuthFrame');
		    if (frame) {
			    var phoneLogin = frame.contentWindow.$.phoneLogin;
			    if (phoneLogin) {
				    if (phoneLogin.getAccessNum()) {
					    menuAddr += "&ACCESS_NUM=" + phoneLogin.getAccessNum();
				    }
				    if (phoneLogin.getIDS()) {
					    menuAddr += "&IDS=" + phoneLogin.getIDS();
					    menuAddr += "&SUBSCRIBER_INS_ID=" + phoneLogin.getIDS().get("SUBSCRIBER_INS_ID");
				    }
				    menuAddr += "&NGBOSS_RISK_LEVEL=" + phoneLogin.getNgbossRiskLevel();
				    var loginTypeCode = "";
				    if (phoneLogin.isLogin) {
					    loginTypeCode = phoneLogin.getReqData().get("LOGIN_TYPE_CODE");
				    }
				    menuAddr += "&LOGIN_TYPE_CODE=" + loginTypeCode;
			    }
		    }

		    // 集团url参数
		    if ($.enterpriseLogin && $.enterpriseLogin.isLogin()) {
			    menuAddr += "&ENTERPRISE_INFO=" + $.enterpriseLogin.getEnterpriseUrlParam();
		    }

		    if (!menuAddr || !$.isString(menuAddr)) {
			    MessageBox.alert("提示信息", "菜单地址不正确！");
		    }

		    //截取url=后面的地址
		    if (menuAddr && menuAddr.lastIndexOf("&url=") != -1) {
			    menuAddr = menuAddr.substr(menuAddr.lastIndexOf("&url=") + 5);
		    }

		    //客服地址处理
		    if (("" + menuAddr).indexOf("/cs/") == 0) {
			    menuAddr = "/aikm/SSOLoginCS?WADE_SID=" + window.WSID + "&Page=" + menuAddr;
		    }

		    //产品管理(计费账务)/酬金 特殊处理
		    if ( menuAddr.indexOf('/bilmanm/bilmanm') > -1 || menuAddr.indexOf('/prodmbil/prodmbil') > -1 || menuAddr.indexOf('/rwdmanm/business') == 0 ) {
			    menuAddr += "&WADE_SID=" + window.WSID;
		    }
		    
		    //计费特殊处理
		    if ( menuAddr.indexOf('/bilmanm/bilmanm') > -1 ){
		    	window.open(menuAddr);
		    	return;
		    }
		    
		    //无纸化特殊处理
		    /*
		    if( menuAddr.indexOf('service=page/person.cs.epaper.EpaperLog') > -1 ){
		    	window.open(menuAddr);
		    	return;
		    }
		    */

		    menuTitle = $.trim(menuTitle);

		    //拼入MENU_ID
		    if (menuId) {
			    menuAddr += "&MENU_ID=" + menuId;
		    }
			
			// 拼入菜单打开时间
			menuAddr += "&menuOpenTime=" + Wade.now();

			var re = false;
		    //跨省工单地址解析
		    if (menuAddr.indexOf('provinceWork') > -1  && menuAddr.indexOf('tag')>-1) {
		    	var params_ =  menuAddr.split("provinceWork");
		    	var tag = params_[params_.length-1];
		    	console.log(menuAddr);
		    	ajaxPost(null,'getProvinceUrl', tag , null,
		     		function(data) {
		     			menuAddr  = data.map.URL;
					},
					function(code, info){
						re = true;
						MessageBox.alert(info);
					},
					{
						async: false
					} );

		    }
		    if(re){
		    	return ;
		    }
		    var menuData = [
		            menuTitle, menuAddr, null, {
			            "menu_id" : menuId,
			            "offer_id" : offerId
		            }
		    ];
		    $.menuopener.openMenuData(menuData);
	    },
	    openMenuData : function(menuData) {
		    $.nav.openByUrl.apply(window, menuData);
	    },
	    handleCSUrl:function(menuAddr){
			 menuAddr = "/aikm/SSOLoginCS?WADE_SID=" + window.WSID + "&Page=" + menuAddr;
			 return menuAddr;
		}
	}
})(Wade);