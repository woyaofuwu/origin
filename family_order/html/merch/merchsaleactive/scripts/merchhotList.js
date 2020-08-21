
function   checkByPackageId(obj) {
            var $obj = $(obj);
            var packageId = $obj.attr("packageId");
            var packageName = $obj.attr("packageName");
            var productId = $obj.attr("productId");
            var campnType = $obj.attr("campnType");
            var userId = $("#SALEACTIVE_USER_ID").val();
            var custId = $("#SALEACTIVE_CUST_ID").val();
            var errorFlag = $("#SALEACTIVE_ERROR_FLAG").val();
            var serialNumber =$("#SALE_SERIAL_NUMBER").val();
            var eparchyCode =$("#SALE_EPARCHY_CODE").val();
            
            if (packageId !== "") {
                var params = "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&SERIAL_NUMBER=" + serialNumber
                        + "&USER_ID=" + userId + "&CUST_ID=" + custId + "&SPEC_TAG=" + "checkByPkgId"+ "&EPARCHY_CODE=" + eparchyCode
                        + "&CHECK_TYPE=" + "CHECKPACK";
                $.beginPageLoading("营销包校验中。。。");
              ajaxSubmit(null, "checkHotByPkgIg", params, null,
                    function () {
                        $("#SALE_PACKAGE").val(packageName);
                        $("#SALE_PACKAGE_ID").val(packageId);
                        if (errorFlag === "1") {
                            selectSaleActiveAction(obj);
                        } else {
                            checkPackNeedOldCustSn(productId, packageId, obj, serialNumber);
                        }
                        $.endPageLoading();
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.error("营销包校验失败", error_info);
                        afterCheckPopProductSnFailure();//duhj
                    });
            }
        };
        
        function popupHotCheckPackOldCustSn () {
            var el = "oldCustSnForPack",
                $obj = $("#" + el),
                serialNumber = $("#SALE_SERIAL_NUMBER").val();;

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&PACKAGE_ID=" + $("#SALE_PACKAGE_ID").val()
                    + "&CHECK_SERIAL_NUMBER=" + $obj.val()
                    + "&CHECK_TYPE=" + "CHECKSN";

            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var checkSnTag = ajaxData.get("SN_HAVE_PRODUCT");
                    var checkSnUse = ajaxData.get("SN_HAVE_USE");
                    var checkSnMsg = ajaxData.get("SN_MSG");

                    if (checkSnTag === "N") {
                        $.validate.alerter.one($obj[0], "输入的校验用户手机号未办理指定活动，请输入其他号码！");
                    } else if (checkSnTag === "K") {
                        $.validate.alerter.one($obj[0], checkSnMsg);
                    } else {
                        if (checkSnUse === "Y") {
                            $.validate.alerter.one($obj[0], "该号码已经被其他号码校验过，请输入新号码校验！");
                        } else if (checkSnUse === "Z") {
                            $.validate.alerter.one($obj[0], "办理用户证件和校验用户证件号码不一致，不能办理！");
                        } else {
                            MessageBox.success("成功登记校验用户手机号！");
                            afterPopupPackCheckSuccess();
                        }
                    }
                }, function (error_code, error_info) {
                    MessageBox.error(error_info);
                    //afterPopupPackCheckFailure();
                });
        };
        
        function afterPopupPackCheckSuccess() {
        	$("#checkPackOldCustSn").css("display", "none"); 
        	$("#sendSMSCodePopup").css("display", "none"); 
        	$("#hotSaleActiveListScroller").css("display", "");        	
            selectSaleActiveAction();
        };
        
        
        /**
         * 判断产品包是否需要录入老用户手机号
         * zhangxing3
         */
        function checkPackNeedOldCustSn(productId, packageId, obj, serialNumber) {
            var params = "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&SERIAL_NUMBER=" + serialNumber
                    + "&CHECK_TYPE=" + "CHECKPACK";
            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var ifNeed = ajaxData.get("IF_NEED");
                    if (ifNeed === "Y") {
                        //showPopup("checkPackOldCustSnPopup", "checkPackOldCustSnItem", true);
                    	$("#checkPackOldCustSn").css("display", ""); 
                    	$("#hotSaleActiveListScroller").css("display", "none");        	

                    } else if(ifNeed==="SIM"){
                    	//showPopup("sendSMSCodePopup", "sendSMSCodeItem", true);//点击红包雨营销包时，弹出校验码的输入框--wangsc10--20190311
                    	$("#sendSMSCodePopup").css("display", ""); 
                    	$("#hotSaleActiveListScroller").css("display", "none");        	

                    }else{
                    	selectSaleActiveAction(obj);
                    }
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    //afterPopupPackCheckFailure();
                })
        };
        
        //点击校验码弹出框确定按钮--wangsc10-20190311--start
        function popupqueryProdRedMoney(obj) {
            var el = "RED_SMS_CODE",
            $obj = $("#" + el);
	        if ($obj.val() === null || "" === $obj.val()) {
	            $.validate.alerter.one($obj[0], "验证码不能为空！");
	            return;
	        }
//	        var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
//	                + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
//	                + "&PACKAGE_ID=" + $("#SALE_PACKAGE_ID").val()
//	                + "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
//	                + "&SMS_CODE=" + $obj.val();
//	
//	        ajaxSubmit(null, "queryProdRedMoney", params, null,
//	            function (ajaxData) {
//	        		var RED_CODE = ajaxData.get("RED_CODE");
//	        		var RED_YE = ajaxData.get("RED_YE");
//	        		if (RED_CODE === "1") {
//                        $.validate.alerter.one($obj[0], "红包余额"+RED_YE+"不足！");
//                    }else{
//                    	$("#SMS_CODE").val(ajaxData.get("SMS_CODE"));
//                    	hidePopup("sendSMSCodePopup");
//                    	saleActiveList.afterPopupPackCheckSuccess();
//                    }
//	            }, function (error_code, error_info) {
//	                MessageBox.error(error_info);
//	                saleActiveList.afterPopupPackCheckFailure();
//	            });
	        $("#SMS_CODE").val($obj.val());
        	//hidePopup("sendSMSCodePopup");
        	saleActiveList.afterPopupPackCheckSuccess();
	    };
	    //end
        
	    //热门营销活动只针对YX02,YX04,参数简化下
        function selectSaleActiveAction(obj){
            var packageId, productId, campnType,packageName,
            deviceModelCode = $("#DEVICE_MODEL_CODE").val();
            if (obj) {
                var obj = $(obj);
                packageId = obj.attr("packageId");
                productId = obj.attr("productId");
                campnType = obj.attr("campnType");
                packageName = obj.attr("packageName");
            } else {
                packageId = $("#SALE_PACKAGE_ID").val();
                productId = $("#SALE_PRODUCT_ID").val();
                campnType = $("#SALE_CAMPN_TYPE").val();
                packageName = $("#SALE_PACKAGE").val();
            }
            
	var codeGoodsInfo = "";
	var newImei = "";
	var deviceModelCode = "";
	var codeAllMoneyName = "";
	if (isTeminalCampnType(campnType)) {
		var deviceBrand = $("#DEVICE_BRAND").val();
		var deviceModel = $("#DEVICE_MODEL").val();
		var goodsInfo = "品牌：" + deviceBrand + "  电池：" + $("#BATTERY").val() + "  颜色：" + $("#COLOR").val() + "  型号：" + deviceModel;
		var allMoneyName = deviceBrand + deviceModel + "手机款";
		codeGoodsInfo = encodeURIComponent(goodsInfo);
		newImei = $("#SALEACTIVE_IMEI").val();
		deviceModelCode = $("#DEVICE_MODEL_CODE").val();
		codeAllMoneyName = encodeURIComponent(allMoneyName);
	}
                             
			var activeType = $("#ACTIVE_TYPE").val();			
			var smsCode=$("#SMS_CODE").val();//红包验证码						
			var saleUserId = $("#SALEACTIVE_USER_ID").val();
			var saleSerialNumber = $("#SALE_SERIAL_NUMBER").val();
			var eparchyCode =$("#SALE_EPARCHY_CODE").val();
			var needCheck = $("#SALEACTIVE_NEED_CHECK").val();
			var saleStaffId = $("#SALE_STAFF_ID").val();
			var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
			var needCheck = $("#SALEACTIVE_NEED_CHECK").val();
			var param="&SALE_PACKAGE_ID="+packageId+"&SALE_PRODUCT_ID="+productId+"&SALE_CAMPN_TYPE="+campnType+"&ACTIVE_TYPE="+activeType+"&TRADE_TYPE_CODE="+tradeTypeCode+"&AUTO_AUTH=true";
			if(typeof(saleUserId)!="undefined") param+="&SALEACTIVE_USER_ID="+saleUserId;
			if(typeof(saleSerialNumber)!="undefined") param+="&SALE_SERIAL_NUMBER="+saleSerialNumber;
			if(typeof(saleSerialNumber)!="undefined") param+="&SERIAL_NUMBER="+saleSerialNumber;
			if(typeof(eparchyCode)!="undefined") param+="&SALE_EPARCHY_CODE="+eparchyCode;
			if(typeof(needCheck)!="undefined") param+="&SALEACTIVE_NEED_CHECK="+needCheck;
			if(typeof(saleStaffId)!="undefined") param+="&SALE_STAFF_ID="+saleStaffId;
			if(typeof(smsCode)!="undefined") param+="&SMS_CODE="+smsCode;
			if(typeof(needCheck)!="undefined") param+="&SALEACTIVE_NEED_CHECK="+needCheck;
			if(typeof(newImei)!="undefined" && newImei !="") param+="&NEW_IMEI="+newImei;
			if(typeof(deviceModelCode)!="undefined" && deviceModelCode !="") param+="&DEVICE_MODE_CODE="+deviceModelCode;
			if(typeof(codeGoodsInfo)!="undefined" && codeGoodsInfo !="") param+="&GOODS_INFO="+codeGoodsInfo;
			if(typeof(codeAllMoneyName)!="undefined") param+="&ALL_MONEY_NAME="+codeAllMoneyName;

			 //openNav(packageName, "merch.MarketingActivityDetail", "onInitTrade", param);
			 //此处营销包名称直接写为营销活动详情页面，这样只能打开一个详情页面，操作完一个再去操作另外一个，防止打开多个详情页，对时间有影响
			 openNav("营销活动详情页面", "merch.MarketingActivityDetail", "onInitTrade", param);
		}
        
        
        function popupCheckProdOldCustSn() {
            var el = "oldCustSnForProd",
                $obj = $("#" + el),
                serialNumber = $("#SALE_SERIAL_NUMBER").val();

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&CHECK_SERIAL_NUMBER=" + $obj.val();

            var sss=$("#IF_NEW_USER_ACTIVE").val();
            if ($("#IF_NEW_USER_ACTIVE").val() === "NEW") {
                params += "&AUTH_SERIAL_NUMBER=" + serialNumber
                        + "&CHECK_TYPE=" + "NEW_USER_ACTIVE";
                ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                    function (ajaxData) {
                        /**
                         * 新号码校验：
                         * 1、是否办理过该活动                                 SN_HAVE_PRODUCT   Y:办理过  N:没办理过
                         * 2、0存折不能大于0                                  SN_FEE_TYPE       Y:大于0   N:不大于0
                         * 3、不能是多终端共享业务数据                          SHARE_INFO_TYPE   Y:属于    N:不属于
                         * 4、不能统一付费业务数据                             RELATION_UU_TYPE  Y:属于    N:不属于
                         * 5、与新号码的身份证是否相同                          PSPT_ID_SAME      Y:相同    N:不相同
                         * 6、新号码存在红海行动营销活动，不能再办理感恩大派送活动 PSPT_ID_SAME      Y:相同    N：不相同
                         */
                        var open48Tag = ajaxData.get("OPEN_48_HOUR"),
                            haveTag = ajaxData.get("SN_HAVE_PRODUCT"),
                            shareTag = ajaxData.get("SHARE_INFO_TYPE"),
                            relaTag = ajaxData.get("RELATION_UU_TYPE"),
                            psptIdTag = ajaxData.get("PSPT_ID_SAME"),
                            feeTag = ajaxData.get("SN_FEE_TYPE"),
                            activeTag = ajaxData.get("HAVE_ACTIVE"),
						    mainCardTag=ajaxData.get("MAIN_CARD"),
						    familyTag=ajaxData.get("FAMILY_CARD"),
						    widenetTag=ajaxData.get("WIDENET_TYPE");
                            familyOldTag=ajaxData.get("FAMILY_CARD_OLD");
                            widenetOldTag=ajaxData.get("WIDENET_TYPE_OLD");

                        if (open48Tag == "N") {
                            $.validate.alerter.one($obj[0], "输入的用户手机号不属于7天内开户的号码，不允许办理该业务！");
                            return;
                        }
                        if(mainCardTag == "N"){
		                	$.validate.alerter.one($obj[0],"输入的号码对应的老号码没有作为主卡与新号码办统一付费业务  ，不能办理该活动！");
		            		   return;
			        	}
			        	if(familyTag == "N"){
			        		$.validate.alerter.one($obj[0],"输入的号码没有办理亲亲网业务  ，不能办理该活动！");
		            	 	   return;
			        	}
			        	if(widenetTag == "N"){
			        		$.validate.alerter.one($obj[0],"输入的号码没有办理宽带业务(或预约)  ，不能办理该活动！");
		            		   return;
			        	}
			        	if(familyOldTag == "N"){
			        		$.validate.alerter.one($obj[0],"老号码没有办理亲亲网业务  ，不能办理该活动！");
		            	 	   return;
			        	}
			        	if(widenetOldTag == "N"){
			        		$.validate.alerter.one($obj[0],"老号码没有办理宽带业务(或预约)  ，不能办理该活动！");
		            		   return;
			        	}
                        if (haveTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码已经办理过该活动，不能再次办理！");
                            return;
                        }
                        if (shareTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码办理过多终端共享业务，不能办理该活动！");
                            return;
                        }
                        if (relaTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码办理过统一付费业务，不能办理该活动！");
                            return;
                        }
                        if (psptIdTag === "N") {
                            $.validate.alerter.one($obj[0], "输入的号码与主号码的身份证不一致，不能办理该活动！");
                            return;
                        }
                        if (feeTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码预存款大于0，不能办理该活动！");
                            return;
                        }
                        if (activeTag === "Y") {
                            $.validate.alerter.one($obj[0], "输入的号码存在红海行动营销活动，不能办理该活动！");
                            return;
                        }
                        MessageBox.success("校验通过！");
                        afterPopupProdCheckSuccess();
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        afterPopupProdCheckFailure(true);
                    });
            } else {
                params += "&CHECK_TYPE=" + "CHECKSN";
                ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                    function (ajaxData) {
                        var checkSnTag = ajaxData.get("SN_HAVE_PRODUCT"),
                            checkSnUse = ajaxData.get("SN_HAVE_USE");
                        if (checkSnTag === "N") {
                            $.validate.alerter.one($obj[0], "输入的老用户手机号未办理指定活动，请输入其他号码！");
                        } else {
                            if (checkSnUse === "Y") {
                                $.validate.alerter.one($obj[0], "该号码已经被其他号码校验过，请输入新号码校验！");
                            } else if (checkSnUse === "Z") {
                                $.validate.alerter.one($obj[0], "老用户证件和新用户证件号码不一致，不能办理！");
                            } else {
                                MessageBox.success("成功登记老用户手机号！");
                                afterPopupProdCheckSuccess();
                            }
                        }
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        afterPopupProdCheckFailure(true);
                    });
            }
        };
        
        
        function afterPopupProdCheckSuccess() {       	
        	$("#hotSaleActiveListScroller").css("display", "");
        	$("#checkProdOldCustSnItemHot").css("display", "none");        	
        };
        
        function afterPopupProdCheckFailure() {       	
        	$("#checkProdOldCustSnItemHot").css("display", "");
        	$("#hotSaleActiveListScroller").css("display", "none");        	
        };
        
        // 根据营销包名称关键字过滤
        function filterByPackageName() {
            var text = $("#SEARCH_CONTENT").val();
            var packageList = $("#saleActiveListUl > li");
            if (packageList !== null) {
                packageList.css("display", "");
                packageList.each(function () {
                    if ($(this).attr("packageName").indexOf(text) === -1) {
                        $(this).css("display", "none");
                    }
                });
            }
        };
        
        function backcheckPackOldCustSn(){
        	$("#hotSaleActiveListScroller").css("display", "");        	       	
        	$("#checkPackOldCustSn").css("display", "none");    
        	
        };
        
        //校验活动产品老用户手机号成功后，点击营销包检验失败，返回检验号码页面
        function afterCheckPopProductSnFailure(){
        	var isNeed=$("#IF_NEED").val();
        	if("Y"==isNeed){
         		$("#failCheckPro").unbind("click");//解除组件中绑定的事件
        		$("#failCheckPro").bind("click",function(){
        			onChangeCheck();
        		});        	

        	}
        };
        
        
        function onChangeCheck(){
        	$("#checkProdOldCustSnItemHot").css("display", "");  
        	$("#hotSaleActiveListScroller").css("display", "none");        	       	
        };
        
        function isTeminalCampnType(campnType){
			   var terminalCampnTypes = "YX03|YX08|YX09|YX07|YX06";
			   if(campnType!=""&&terminalCampnTypes.indexOf(campnType)>-1){
			      return true;
			   }
			   return false;

		};

        

        