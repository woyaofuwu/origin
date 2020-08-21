if(typeof(SaleActiveFilter)=="undefined"){
	window["SaleActiveFilter"]=function(){};
	var saleActiveFilter = new SaleActiveFilter();
}

(function(){
	$.extend(SaleActiveFilter.prototype,{
		queryProductByType: function(obj){
			obj = $(obj);
			if(obj.attr("catalogId") != ""){
				var serialNumber = saleActiveFilter.getSerialNum();
				var param = "&CAMPN_TYPE="+obj.attr("catalogId")+"&SPEC_TAG=refreshProduct"+"&SERIAL_NUMBER="+serialNumber;
				param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
				param += "&USER_ID=" + $('#SALEACTIVE_USER_ID').val();

				if(typeof($('#ACTIVE_FLAG').val()) != 'undefined') param += "&CAMPN_TYPE_FLAG_YD="+$('#ACTIVE_FLAG').val();
				$.beginPageLoading("检索中...");
				ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val()+"Part", function(d){
				    marketActivityList.clearSaleActiveList();
				    //duhj
//					$("#activeTags").empty();
//					
				    var tipflag = "";//duhj
				    var tipinfo = "";//duhj
//
//				    var count="0";
//				    var html="";
//				    d.each(function(item){
//				    	//duhj
////						html += "<li catalogId='" + item.get("CATALOG_ID") + "' catalogName='" + item.get("CATALOG_NAME") + "' ontap='saleActiveFilter.switchLowerLevelLabel(this);' >";
////						html += item.get("CATALOG_NAME");
//						html += "<li catalogId='" + item.get("PRODUCT_ID") + "' catalogName='" + item.get("PRODUCT_NAME") + "' ontap='saleActiveFilter.switchLowerLevelLabel(this);' >";
//						html += item.get("PRODUCT_NAME");
//						html += "</li>";
//				    });
//				    $("#activeTags").append(html);
				    
					$.endPageLoading();
					
					//海南营销方案太多，需要添加搜索框
                    d.each(function (product) {
                        tipFlag = product.get("TIP_FLAG");
                        tipInfo = product.get("TIP_INFO");
                        var productDesc = product.get("UP_CATALOG_NAME") + " | " + product.get("PRODUCT_NAME"),
                            productTitle = product.get("PRODUCT_NAME") + " | " + product.get("UP_CATALOG_ID");
                        product.put("PRODUCT_NAME", productDesc);
                        product.put("PRODUCT_TITLE", productTitle);
                    });

                    $("#SALE_PRODUCT_SELECT_CONTAINER").empty();
                    if (window["SALE_PRODUCT_ID_SEARCH"]) {
                        window["SALE_PRODUCT_ID_SEARCH"].destroy();
                        window["SALE_PRODUCT_ID_SEARCH"] = null;
                    }
                    $.Select.append(
                        "SALE_PRODUCT_SELECT_CONTAINER", {
                            name: "SALE_PRODUCT_ID_SEARCH",
                            textField: "PRODUCT_NAME",
                            valueField: "PRODUCT_ID",
                            titleField: "PRODUCT_TITLE",
                            inputable: true,
                            disabled: false,
                            nullable: "no",
                            desc: "营销方案",
                            optionWidth: 35,
                            style: "width:5rem", 
                        }, d
                    );
                    $("#SALE_PRODUCT_ID_SEARCH").bind("change", saleActiveFilter.checkByProductSearch);

					
					//终端类活动，需要显示串码区域和工号区域
					var prodouctStr = $("#SALE_PRODUCT_ID").val();
					if(saleActiveFilter.isTeminalCampnType(obj.attr("catalogId"))){
					    $('#imeiQuery').css('display', '');
					    $('#saleStaffId').css('display', '');
					    //IPHONE6 相关处理
					    $('#isIphone6').css('display', 'none');
					    $('#iphone6Imei').css('display', 'none');
					    $("#IS_IPHONE6").val("");
					    $("#IPHONE6_IMEI").val("");

					}
					else
					{
					    $('#imeiQuery').css('display', 'none');
					    $('#saleStaffId').css('display', 'none');
					    $("#NEW_IMEI").val("");
					    $('#SALE_STAFF_ID').val("");
					     //IPHONE6 相关处理
					    $('#isIphone6').css('display', 'none');
					    $('#iphone6Imei').css('display', 'none');
					    $("#IS_IPHONE6").val("");
					    $("#IPHONE6_IMEI").val("");
					    if(saleActiveFilter.isiPhone6CampnType(obj.attr("catalogId"))){
					    	$('#isIphone6').css('display', '');
					    	$('#iphone6Imei').css('display', '');
						}

					}
					
                    if (tipFlag === "1") {
                        MessageBox.alert(tipInfo);
                    }

				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					MessageBox.error("营销产品列表查询失败", errorinfo, function(btn){});
				});				
			}
		},
		
		 //IPHONE6 活动大类校验
		isiPhone6CampnType:function(campnType){
            var iPhone6CampnType = "YX11";
            return campnType !== "" && iPhone6CampnType.indexOf(campnType) > -1;
		},

		isTeminalCampnType:function(campnType){
			   var terminalCampnTypes = "YX03|YX08|YX09|YX07|YX06";
			   if(campnType!=""&&terminalCampnTypes.indexOf(campnType)>-1){
			      return true;
			   }
			   return false;

		},
		checkByProductSearch: function(){
			var productId = $("#SALE_PRODUCT_ID_SEARCH").val();//搜索过来的营销方案
			$("#SALE_PRODUCT_ID").val(productId);//搜索框的产品id赋值给页面隐藏的产品id,后面都会用到sale_product_id
		    $('#SEARCH_CONTENT').val('');//每次切换方案清空搜索的营销包				
			saleActiveFilter.checkByProduct();
		},
		checkByProduct: function(){
			var productId = $("#SALE_PRODUCT_ID").val();
						
			var userId = $('#SALEACTIVE_USER_ID').val();
			var custId = $('#SALEACTIVE_CUST_ID').val();			
			if(productId != ""){
				var param = "&PRODUCT_ID=" + productId;
				param += "&USER_ID=" + userId;
				param += "&CUST_ID=" + custId;
				param += "&SPEC_TAG=" + "checkByProduct";
				$.beginPageLoading("营销产品校验中。。。");
				ajaxSubmit(null,null,param,'', function(d){
					$.endPageLoading();	
					//saleactive.queryPackages();
					saleActiveFilter.giftSaleactive();//REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销产品校验失败',errorinfo);
				});				
			}
		},
		giftSaleactive: function(){
			var productId = $("#SALE_PRODUCT_ID").val();
			var param = "&PRODUCT_ID=" + productId;
			param += "&SPEC_TAG=" + "giftSaleactive";
			$.beginPageLoading("特殊活动校验中。。。");
				ajaxSubmit(null,null,param,'', function(d){
					$.endPageLoading();
					if(d.get("productAttr")==1)//是礼品码的活动
					{
                        $("#GiftCodePart").css("display", "");
                        $("#IS_GIFT_ACTIVE").val("1");
					}
					else
					{
                        $("#GiftCodePart").css("display", "none");
                        $("#IS_GIFT_ACTIVE").val("0");
					}
					saleActiveFilter.queryPackages();
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('特殊活动校验失败',errorinfo);
					return;
				});	
		},

		
		queryPackages: function(pagesize,current){
			$('#saleactiveListPart').css('display', '');
			var imei = $("#NEW_IMEI").val();
			var saleStaffId = $('#SALE_STAFF_ID').val();
			var productId = $("#SALE_PRODUCT_ID").val();
			var searchContent = $('#SEARCH_CONTENT').val();
			if(productId == '' && imei == '' && searchContent == '')
			{
				alert('未输入查询的条件！');return;
			}
			var campnType = $("#SALE_CAMPN_TYPE").val();
			if(saleActiveFilter.isTeminalCampnType(campnType)){
			   $('#imeiQuery').css('display', '');
			   $('#saleStaffId').css('display', '');
			   if(imei==''){
			      alert("请输入终端串码查询可办理的营销包");return;
			   }
			}
            // iPhone6 相关处理
            var isiPhone6 = "",
                $iPhone6IMEI = $("#IPHONE6_IMEI"),
                iPhone6IMEI = $iPhone6IMEI.val();
            if (saleActiveFilter.isiPhone6CampnType(campnType)) {
                if (iPhone6IMEI !== "" && iPhone6IMEI !== "undefined") {
                    isiPhone6 = "1";
                } else {
                    isiPhone6 = "0";
                }
            } else {
                $("#IsiPhone6Part").css("display", "none");
                $("#iPhone6IMEIPart").css("display", "none");
                $iPhone6IMEI.val("");
            }
			var netOrderId = $('#NET_ORDER_ID').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
			var saleActiveProductId = $("#SALE_PRODUCT_ID").val();
			var saleActiveUserId = $('#SALEACTIVE_USER_ID').val();
			var saleActiveCustId = $('#SALEACTIVE_CUST_ID').val();	
			
			var isGiftActive = $('#IS_GIFT_ACTIVE').val();
			var giftCode = $('#GIFT_CODE').val();
			
            var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent +"&IS_IPHONE6="+isiPhone6+"&IPHONE6_IMEI="+iPhone6IMEI;
			param += "&SALEACTIVE_USER_ID="+saleActiveUserId+"&SALEACTIVE_CUST_ID="+saleActiveCustId;
			param += "&IS_GIFT_ACTIVE="+isGiftActive+"&GIFT_CODE="+giftCode;  
			marketActivityList.readerComponent(param);
		},
		
		queryPackagesByImei:function(){
		    var imei = $('#NEW_IMEI').val();
		    if(imei==''){
			   alert("请输入终端串码查询可办理的营销包");
			   return;
			}
			var saleStaffId = $('#SALE_STAFF_ID').val();
			if(saleStaffId==''){
			   alert("请输入促销员工");
			   return;
			}
			var netOrderId = $('#NET_ORDER_ID').val();
			var campnType = $('#SALE_CAMPN_TYPE').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
			var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent;
			marketActivityList.readerComponent(param);
		},
		getSerialNum: function()
		{	var serialNumber ;
			var needUserId = $('#SALEACTIVE_NEED_USER_ID').val();
			if(needUserId == 'false')
			{
				serialNumber= $("#SERIAL_NUMBER").val();
			}
			else
			{
				serialNumber = $("#SALE_SERIAL_NUMBER").val();
			}
			
			return serialNumber;
		},
		readerComponent: function(userId, custId, acctDay, firstDate, nextAcctDay, nextFirstDay, labelType){
			var param = "";
			if(typeof(userId) != 'undefined') param += "&USER_ID="+userId;
	        if(typeof custId !== "undefined") param += "&CUST_ID=" + custId;
			if(typeof(acctDay) != 'undefined') param += "&ACCT_DAY="+acctDay;
			if(typeof(firstDate) != 'undefined') param += "&FIRST_DATE="+firstDate;
			if(typeof(nextAcctDay) != 'undefined') param += "&NEXT_ACCT_DAY="+nextAcctDay;
			if(typeof(nextFirstDay) != 'undefined') param += "&NEXT_FIRST_DAY="+nextFirstDay;
			var serialNumber = saleActiveFilter.getSerialNum();
			param += '&NEED_USER_ID='+$('#SALEACTIVE_NEED_USER_ID').val()+'&SERIAL_NUMBER='+serialNumber;
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			param += "&LABEL_ID=" + labelType;
			param += "&ACTIVE_TYPE="+$('#ACTIVE_TYPE').val();
			if(typeof($('#ACTIVE_FLAG').val()) != 'undefined') param += "&ACTIVE_FLAG="+$('#ACTIVE_FLAG').val();
			$.beginPageLoading("选项加载中...");
			ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val()+"Part",function(data){
				var activeType = $("#ACTIVE_TYPE").val();
				var labelId = $("#LABEL_ID").val();
				var orderMode = activeType+"_"+labelId;
				var obj = $("#"+orderMode);
				obj.siblings().attr("class", "");
				obj.attr("class", "on");
				$.endPageLoading();
			},
			function(errorcode, errorinfo){
				$.endPageLoading();
				MessageBox.error("选项加载失败", errorinfo, function(btn){});
			});
		},
		search: function(){
			var productId = $("#Ul_Search_saleproductsearch li[class=focus]").attr("PRODUCT_ID");
			var productName = $("#Ul_Search_saleproductsearch li[class=focus]").attr("PRODUCT_NAME");
			$('#saleproductsearch').val(productName);
			$('#SALE_PRODUCT_ID').val(productId);
			$("#Div_Search_saleproductsearch").css("visibility","hidden");
			saleActiveFilter.queryPackages();
		},
		setAcctDayInfo: function(acctDay, firstDate, nextAcctDay, nextFirstDate) {
			$('#SALEACTIVE_ACCT_DAY').val(acctDay);
			$('#SALEACTIVE_FIRST_DATE').val(firstDate);
			$('#SALEACTIVE_NEXT_ACCT_DAY').val(nextAcctDay);
			$('#SALEACTIVE_NEXT_FIRST_DATE').val(nextFirstDate);
		},
		openChoicePage: function(){
			$("#detailTitle").attr("class","");
			$("#detailContent").css("display","none");
			$("#listContent").css("display","");
			$("#listTitle").attr("class","on");
		},
		openDetailPage: function(){
			$("#detailTitle").css("display","");
			$("#detailTitle").attr("class","on");
			$("#detailContent").css("display","");
			$("#listContent").css("display","none");
			$("#listTitle").attr("class","");
		},
		showHotSale: function(){
			$('#saleactiveListPart').css('display', 'none');
		},
		showGoodsInfo: function(goodsInfo){
		    var showInfo ;
	        var showInfo = "品牌：" + goodsInfo.get("DEVICE_BRAND")+"  "
		     + "电池：" + goodsInfo.get("BATTERY")+"  "
		     + "颜色：" + goodsInfo.get("COLOR")+"  "
		     + "型号：" + goodsInfo.get("DEVICE_MODEL");
		    
	        $('#GOODS_INFO').val(showInfo);
	        var allMoneyName = goodsInfo.get("DEVICE_BRAND") + goodsInfo.get("DEVICE_MODEL") + "手机款";
	        $('#ALL_MONEY_NAME').val(allMoneyName);

		},
		hiddenGoodsInfo: function(){
		    $('#GOODS_INFO').val("");
		    $('#TERMINAL_DETAIL_INFO').val("");
		    $('#ALL_MONEY_NAME').val("");
		},
		switchUpLabel : function(obj){
			var orderMode = $(obj).attr("id");
			
			var subStr = orderMode.split("_");
			
			$("#ACTIVE_TYPE").val(subStr[0]);
			$("#LABEL_ID").val(subStr[1]);
			
			if(subStr[0] == "WD"){
				$("#AUTH_SUBMIT_BTN").attr("authType", "04");			
			}else{
				$("#AUTH_SUBMIT_BTN").attr("authType", "00");
			}
			
			//$.auth.autoAuth();
			saleActiveFilter.refreshMarketPartAtferAuth();
		},
		switchLabel : function(obj)
		{			
			this.switchLabelClass(obj);
			$('#SALE_PRODUCT_ID').val('');//每次切换标签前清理产品id
		    $('#SEARCH_CONTENT').val('');//每次切换标签前清空搜索的营销包			
			var campType = $(obj).attr("catalogId");			
			$("#SALE_CAMPN_TYPE").val(campType);
			
			this.queryProductByType(obj);
		},
		switchLowerLevelLabel : function(obj)
		{
			this.switchLabelClass(obj);
			
			var productId = $(obj).attr("catalogId");
			
			$("#SALE_PRODUCT_ID").val(productId);
			$("#SALE_PRODUCT_ID_SEARCH").val(productId);//点击标签给搜索框赋值

			saleActiveFilter.checkByProduct();

		},
		switchLabelClass : function(obj)
		{
			$(obj).siblings().attr("class", "");
			$(obj).attr("class", "on");
		},
		filterFold : function(divName,obj)
		{
			var mode = $(obj).attr("mode");
			if("fold" == mode)
			{
				// 收起时点击
				$('#'+divName+'').removeClass('option-fold').addClass('option-unfold');
				$(obj).attr("mode","unfold");
				$(obj).children("span").removeClass().addClass("e_ico-fold");
				$(obj).children("span").html("收起");
			}
			else
			{
				//展开时点击
				$('#'+divName+'').removeClass('option-unfold').addClass('option-fold');
				$(obj).attr("mode","fold");
				$(obj).children("span").removeClass().addClass("e_ico-unfold");
				$(obj).children("span").html("更多");
			}
		},
		choiceActiveKind : function(obj){
			
			$("#AUTH_SUBMIT_BTN").click();
		},
		setAuthDatas : function(){
			var actionParams = "";
			if($.auth && $.auth.getAuthData()){
				var userInfo = $.auth.getAuthData().get("USER_INFO");
				var custInfo = $.auth.getAuthData().get("CUST_INFO");
				var acctInfo = $.auth.getAuthData().get("ACCT_INFO");
				var acctDayInfo = $.auth.getAuthData().get("ACCTDAY_INFO");
				if(userInfo && userInfo.length>0){
					userInfo.eachKey(function(key, index, totalCount){
						actionParams += "|"+key+"="+ userInfo.get(key);
					});					
				}
				
				if(custInfo && custInfo.length>0){
					actionParams += "|CUST_TYPE="+ custInfo.get("CUST_TYPE");
					actionParams += "|IS_REAL_NAME="+ custInfo.get("IS_REAL_NAME");	
					actionParams += "|PSPT_TYPE_CODE="+ custInfo.get("PSPT_TYPE_CODE");
				}
				
				if(acctInfo && acctInfo.length>0){
					actionParams += "|ACCT_ID="+ acctInfo.get("ACCT_ID");
				}
				if(acctDayInfo && acctDayInfo.length>0){
					acctDayInfo.eachKey(function(key, index, totalCount){
						actionParams += "|"+key+"="+ acctDayInfo.get(key);
					});					
				}
			}
			actionParams = encodeURIComponent(actionParams);
			return actionParams;
		},
		chgAuthParam : function(){
			$("#TRADE_TYPE_CODE").val("240");
			$("#AUTH_SUBMIT_BTN").attr("tradeTypeCode", "240");
			$("#TRADE_TYPE_CODE").attr("orderTypeCode", "240");
			$("#AUTH_SUBMIT_BTN").attr("beforeAction", "setRuleParam()");
			//$("#AUTH_SUBMIT_BTN").attr("tradeAction", "refreshMarketPartAtferAuth(data)");
			$("#AUTH_SUBMIT_BTN").attr("tradeAction", "saleActiveFilter.checkSaleBook(data)");

			$.auth.autoAuth();
			

		},
        chooseNp: function(){
        	var checkNP=$("#CREDIT_PURCHASES").attr("checked");
        	if(checkNP){
        		$("#CREDIT_PURCHASES").val("1");
        	}else{
        		$("#CREDIT_PURCHASES").val("0");
        	}
        },
        
        checkSaleBook:function (data) {
            var params = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
            params += "&SPEC_TAG=checkSaleBook";

            $("#NET_ORDER_ID").val("");
            $.beginPageLoading("查询终端预约信息。。。");
            ajaxSubmit(null, null, params,$('#SALEACTIVE_COMPONENT_ID').val()+"Part",
                function (ajaxData) {
                    $.endPageLoading();
                    if (ajaxData && ajaxData.get("AUTH_BOOK_SALE") == 1) {
                        popupPage("用户已经预约办理了如下活动，请优先办理该业务", "saleactive.sub.MerchBookListNew", "querySaleBookList", params, null, "c_popup c_popup-half c_popup-half-hasBg");
                    } else {
                        //saleActiveFilter.refreshMarketPartAtferAuth(data);
                    	saleActiveFilter.queryValidSaleActives();
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.alert("提示信息", error_info);
                });
        },
        setParam: function (netOrderId) {
            $("#NET_ORDER_ID").val(netOrderId);
        },
        refreshMarketPartAtferAuth: function(data) {
            if (typeof data === "undefined" || data === null) {
			    data = $.auth.getAuthData();
            }
        	var eparchyCode = data.get('USER_INFO').get('EPARCHY_CODE');
        	var userId = data.get('USER_INFO').get('USER_ID');
            var custId = data.get('USER_INFO').get("CUST_ID");
            var acctDayInfo = data.get("ACCTDAY_INFO");
            var acctDay = acctDayInfo.get("ACCT_DAY");
            var firstDate = acctDayInfo.get("FIRST_DATE");
            var nextAcctDay = acctDayInfo.get("NEXT_ACCT_DAY");
            var nextFirstDate = acctDayInfo.get("NEXT_FIRST_DATE");

        	$('#custinfo_EPARCHY_CODE').val(eparchyCode);
        	var labelId = $('#LABEL_ID').val();
        	saleActiveFilter.readerComponent(userId, custId, acctDay, firstDate,
                    nextAcctDay, nextFirstDate, labelId);
        },
        
        // 查询用户当前全部营销活动
        queryValidSaleActives: function () {
            if (!$.auth.getAuthData()) {
                $.validate.alerter.one($("#AUTH_SERIAL_NUMBER")[0], "请先查询号码!");
                return;
            }

            var queryFlag = $("#QUERY_VALID_SALE_FINISHED").val(),
                userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID"),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"),
                eparchyCode = $("#" + $("#SALEACTIVE_EPARCHY_CODE_COMPID").val()).val();
            if (userId !== "") {
                var params = "&USER_ID=" + userId
                        + "&SERIAL_NUMBER=" + serialNumber
                        + "&EPARCHY_CODE=" + eparchyCode
                        + "&SPEC_TAG=" + "queryValidSaleActives";
                $.beginPageLoading("用户已有营销活动查询中。。。");
                ajaxSubmit(null, null, params, "ValidSaleArea",
                    function () {
                        $.endPageLoading();
                        saleActiveFilter.toggleArea("#ShowValidSaleArea", "#HideValidSaleArea", "#ValidSaleArea");
//                        saleActive.setQueryValidSaleActiveFlag("1");
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("用户已有营销活动查询失败", error_info);
                    });
            } else {
            	saleActiveFilter.toggleArea("#ShowValidSaleArea", "#HideValidSaleArea", "#ValidSaleArea");
            }
        },
        
        // 显示/隐藏开关按钮事件
        toggleArea: function (btn, paired, area) {
	        if ($(area + ":visible").length === 1) {
	            $(area).css("display", "none");
            } else if ($(area + ":hidden").length === 1) {
	            $(area).css("display", "");
            }
            $(btn).css("display", "none");
	        $(paired).css("display", "");
        },
        
        //duhj
		queryHotPackages: function(pagesize,current){
        	hidePopup("mypopHot");			
			var imei = $("#NEW_IMEI_HOT").val();
			var saleStaffId = $('#SALE_STAFF_ID_HOT').val();
			var productId = $("#HOT_PRODUCT_ID").val();
			var searchContent = $('#SEARCH_CONTENT_HOT').val();
			if(productId == '' && imei == '' && searchContent == '')
			{
				alert('未输入查询的条件！');return;
			}
			var campnType = $("#HOT_CAMPN_TYPE").val();
			if(saleActiveFilter.isTeminalCampnType(campnType)){
			   $('#imeiQuery').css('display', '');
			   $('#saleStaffId').css('display', '');
			   if(imei==''){
			      alert("请输入终端串码查询可办理的营销包");return;
			   }
			}

			var netOrderId = $('#NET_ORDER_ID').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
			var saleActiveProductId = $("#HOT_PRODUCT_ID").val();
			var saleActiveUserId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
			var saleActiveCustId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");			
        	var eparchyCode = $.auth.getAuthData().get('USER_INFO').get('EPARCHY_CODE');
			var isGiftActive = $('#IS_GIFT_ACTIVE').val();
			var giftCode = $('#GIFT_CODE').val();
			
            var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent;
			param += "&SALEACTIVE_USER_ID="+saleActiveUserId+"&SALEACTIVE_CUST_ID="+saleActiveCustId;
			param += "&IS_GIFT_ACTIVE="+isGiftActive+"&GIFT_CODE="+giftCode;  
			param += "&SALE_EPARCHY_CODE="+eparchyCode;  
			param += "&EPARCHY_CODE="+eparchyCode;  
			
            popupPage("热门营销活动列表", "saleactive.sub.MerchHotList", "queryHotSaleList", param, null, "c_popup c_popup-half c_popup-half-hasBg");

		}

		
	});
}
)();