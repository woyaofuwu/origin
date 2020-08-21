if (typeof ChangeProduct === "undefined") {
    window["ChangeProduct"] = function () {
    };
    var changeProduct = new ChangeProduct();
}
(function () {
    $.extend(ChangeProduct.prototype, {
        afterSubmitSerialNumber: function (data) {
            var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
            var userId = data.get("USER_INFO").get("USER_ID");
            var custId = data.get("CUST_INFO").get("CUST_ID");
            var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
            var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");

            $("#PSPT_TYPE_CODE").val(data.get("CUST_INFO").get("PSPT_TYPE_CODE"));
            $("#SERIAL_NUMBER").val(serialNumber);
            $("#USER_PRODUCT_ID").val(userProductId);
            $("#USER_ID").val(userId);
            $("#USER_EPARCHY_CODE").val(eparchyCode);
            $("#NEXT_PRODUCT_ID").val("");
            $("#NEW_PRODUCT_ID").val("");
            $("#NEW_PRODUCT_NAME").val("");
            $("#NEW_PRODUCT_START_DATE").val("");
            $("#OLD_PRODUCT_END_DATE").val("");
            $("#PRODUCT_SELECT_BTN").css("display", "");
            $("#PRODUCT_SEARCH_TEXT").attr("disabled", false);

            //$("#SHAREMEAL_NUMBERS").val("");
            //$("#SHAREMEAL_FLAG").val(0);
            //$("#SHAREMEAL_LI").css("display", "none");
            //$("#SHAREMEAL_CHECK_BTN").attr("disabled", true);

            // 海南添加
            if (typeof changeProductExtend !== "undefined"
                    && changeProductExtend.afterSubmitSerialNumber) {
                changeProductExtend.afterSubmitSerialNumber();
            }

            var acctDayInfo = data.get("ACCTDAY_INFO");
            selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),
                    acctDayInfo.get("FIRST_DATE"),
                    acctDayInfo.get("NEXT_ACCT_DAY"),
                    acctDayInfo.get("NEXT_FIRST_DATE"));
            var params = "&USER_ID=" + userId + "&CUST_ID=" + custId
                    + "&ROUTE_EPARCHY_CODE=" + eparchyCode;
            $.ajax.submit(null, "loadChildInfo", params, null, changeProduct.afterLoadChildInfo);
            selectedElements.renderComponent("&USER_ID=" + userId, eparchyCode);
        },

        afterLoadChildInfo: function (data) {
            var userProductNameDiv = $("#USER_PRODUCT_NAME");
            var userProductDescDiv = $("#USER_PRODUCT_DESC");
            var nextProductNameDiv = $("#NEXT_PRODUCT_NAME");
            var nextProductDescDiv = $("#NEXT_PRODUCT_DESC");
            userProductNameDiv.empty();
            userProductDescDiv.empty();
            nextProductNameDiv.empty();
            nextProductDescDiv.empty();

            var rtnData = data.get(0);
            var userProductId = rtnData.get("USER_PRODUCT_ID");
            var nextProductId = rtnData.get("NEXT_PRODUCT_ID");
            var eparchyCode = rtnData.get("EPARCHY_CODE");
            var userProductLi = $("#USER_PRODUCT_LI");
            var nextProductLi = $("#NEXT_PRODUCT_LI");

            var userProductDesc = rtnData.get("USER_PRODUCT_DESC");
            userProductNameDiv.html(rtnData.get("USER_PRODUCT_NAME"));
            userProductDescDiv.html(userProductDesc);
            userProductDescDiv.attr("title", userProductDesc);
            userProductLi.css("display", "");

            if (typeof nextProductId !== "undefined" && nextProductId) {
                $("#NEXT_PRODUCT_ID").val(nextProductId);

                var nextProductDesc = rtnData.get("NEXT_PRODUCT_DESC");
                nextProductNameDiv.html(rtnData.get("NEXT_PRODUCT_NAME"));
                nextProductDescDiv.html(nextProductDesc);
                nextProductDescDiv.attr("title", nextProductDesc);
                nextProductLi.css("display", "");
                if ($.os.phone) {// 手机客户端
                    $("#PRODUCT_DISPLAY_DIV").css("display", "");
                } else { // 非手机客户端
                    $("#PRODUCT_DISPLAY_DIV").addClass("c_list-col-2").css("display", "");
                }
                $("#PRODUCT_SELECT_BTN").css("display", "none");
                $("#PRODUCT_SELECT_BTN_PHONE").css("display", "none");
                $("#DELETE_PRODUCT_BTN").css("display", "none");
                $("#PRODUCT_SEARCH_TEXT").attr("disabled", true);
                offerList.renderComponent(nextProductId, eparchyCode);
            } else {
                nextProductLi.css("display", "none");
                $("#PRODUCT_DISPLAY_DIV").removeClass("c_list-col-2").css("display", "");
                $("#PRODUCT_SELECT_BTN").css("display", "");
                offerList.renderComponent(userProductId, eparchyCode);
            }

            // 海南添加
            if (typeof changeProductExtend !== "undefined"
                    && changeProductExtend.afterLoadChildInfo) {
                changeProductExtend.afterLoadChildInfo(data);
            }

            // 从首页"热门套餐"和"推荐套餐"跳转套餐载入
            var $assignedFlag = $("#PRODUCT_ASSIGNED"),
                productId = $("#PRODUCT_ID_FROM_URL").val();
            if ($assignedFlag.val() === "0" && productId !== "") {
                var param = "&NEXT_PRODUCT_ID=" + productId
                        + "&USER_PRODUCT_ID=" + userProductId;
                $.ajax.submit(null, "queryProductInfoFromURL", param, null,
                    function (ajaxData) {
                        var productName = ajaxData.get("OFFER_NAME"),
                            productDesc = ajaxData.get("DESCRIPTION");
                        changeProduct.afterChangeProduct(productId, productName, productDesc);
                        $assignedFlag.val("1");
                    },
                    function (error_code, error_info) {
                        MessageBox.error("错误提示", error_info);
                    });
            }
        },

        afterChangeProduct: function (newProductId, newProductName, newProductDesc) {
            var nextProductNameDiv = $("#NEXT_PRODUCT_NAME");
            var nextProductDescDiv = $("#NEXT_PRODUCT_DESC");
            nextProductNameDiv.empty();
            nextProductDescDiv.empty();
            nextProductNameDiv.html(newProductName);
            nextProductDescDiv.html(newProductDesc);
            nextProductDescDiv.attr("title", newProductDesc);
            $("#NEW_PRODUCT_ID").val(newProductId);
            $("#NEW_PRODUCT_NAME").val(newProductName);
            $("#NEXT_PRODUCT_LI").css("display", "");
            if (!$.os.phone) // 非手机客户端
                $("#PRODUCT_DISPLAY_DIV").addClass("c_list-col-2");
            $("#DELETE_PRODUCT_BTN").css("display", "");

            /**
             * 产品变更前增加权益提醒
             * @param 前台增加确认提醒
             * @author liwei29
             */

            $.ajax.submit(null, 'getWelfareTipinfo', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(), null, function(rtnData) {
                $.endPageLoading();
                if(rtnData!=null&&rtnData.length > 0){
                    var content1 = rtnData.get("TIP_INFO").toString();
                    if(content1 != null) {
                        MessageBox.confirm("确认提示", content1,
                            function (btn) {
                                if (btn === "ok") {
                                    confirmMessageBeforeSubmit(userProductId, newProductId);
                                }
                            });
                        return;
                    }
                }
            }, function(error_code, error_info,detail) {
                $.endPageLoading();
                MessageBox.error("错误提示", error_info, null, null, null, detail);
            });
            //产品变更前增加权益提醒 end
            
            //REQ201910240002 融合套餐与宽带速率适配系统提醒功能—BOSS侧 start
            var params = "&NEW_PRODUCT_ID=" + newProductId + "&SRC_PAGE=" + "productChange"
            + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()+ "&USER_PRODUCT_ID=" + $("#USER_PRODUCT_ID").val();
		    $.ajax.submit(null, "getNewProductTips", params, null,
		        function (rtnData) {
		            if (rtnData != null && rtnData.length > 0) {
		            	if("0000"==rtnData.get("X_RESULTCODE")){
		            		MessageBox.alert(rtnData.get("X_RESULTINFO"));
		            	}
		            }
		        },
		        function (error_code, error_info, detail) {
		            MessageBox.error("错误提示", error_info, null, null, null, detail);
		        });
            //REQ201910240002 融合套餐与宽带速率适配系统提醒功能—BOSS侧 end

            var userProductId = $("#USER_PRODUCT_ID").val();
            var userEparchyCode = $("#USER_EPARCHY_CODE").val();
            var data = "&USER_ID=" + $("#USER_ID").val()
                    + "&USER_PRODUCT_ID=" + userProductId
                    + "&NEW_PRODUCT_ID=" + newProductId;
            offerList.renderComponent(newProductId, userEparchyCode);
            selectedElements.renderComponent(data, userEparchyCode);

//            $("#SHAREMEAL_NUMBERS").val("");
//            $("#SHAREMEAL_FLAG").val(0);
//            if ("80003014" === newProductId) {
//                $("#OTHER_DISPLAY_DIV").css("display", "");
//                $("#SHAREMEAL_LI").css("display", "");
//                $("#SHAREMEAL_CHECK_BTN").attr("disabled", false);
//            } else {
//                $("#SHAREMEAL_LI").css("display", "none");
//                $("#SHAREMEAL_CHECK_BTN").attr("disabled", true);
//            }
//
            if ("80003014" === userProductId) {
                MessageBox.alert("您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。");
            }

            // 海南添加
            if (typeof changeProductExtend !== "undefined"
                    && changeProductExtend.afterChangeProduct) {
                changeProductExtend.afterChangeProduct();
            }
        },

        cleanupSelectedProduct: function () {
            var nextProductNameDiv = $("#NEXT_PRODUCT_NAME");
            var nextProductDescDiv = $("#NEXT_PRODUCT_DESC");
            nextProductNameDiv.empty();
            nextProductDescDiv.empty();
            nextProductDescDiv.attr("title", "");
            $("#NEW_PRODUCT_ID").val("");
            $("#NEW_PRODUCT_NAME").val("");
            $("#NEXT_PRODUCT_LI").css("display", "none");
            $("#PRODUCT_DISPLAY_DIV").removeClass("c_list-col-2");

            var userProductId = $("#USER_PRODUCT_ID").val();
            var userEparchyCode = $("#USER_EPARCHY_CODE").val();
            var inParam = "&USER_ID=" + $("#USER_ID").val();
            offerList.renderComponent(userProductId, userEparchyCode);
            selectedElements.renderComponent(inParam, userEparchyCode);
        },

        afterRenderSelectedElements: function (data) {
            if (data) {
                var temp = data.get(0);

                if (temp.get("OLD_PRODUCT_END_DATE")) {
                    $("#OLD_PRODUCT_END_DATE").val(temp.get("OLD_PRODUCT_END_DATE"));
                }
                if (temp.get("NEW_PRODUCT_START_DATE")) {
                    $("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
                }
                if (temp.get("EFFECT_NOW_DISABLED") === "false") {
                    $("#EFFECT_NOW").attr("disabled", "");
                } else {
                    $("#EFFECT_NOW").attr("disabled", true);
                }
                if (temp.get("EFFECT_NOW_CHECKED") === "true") {
                    $("#EFFECT_NOW").attr("checked", true).trigger("click");
                } else {
                    $("#EFFECT_NOW").attr("checked", "");
                }
            }
        },

        checkSharePhone: function () {
            var shareMealLi = $("#SHAREMEAL_LI").css("display");

            if (shareMealLi !== "none") {
                var shareMealNumbers = $("#SHAREMEAL_NUMBERS").val().trim();
                if (shareMealNumbers.indexOf(" ") === -1) {
                    MessageBox.alert("请按格式正确填写共享副号！");
                    return;
                }
                var shareMealOneValue = shareMealNumbers.split(" ", 2)[0];
                var shareMealTwoValue = shareMealNumbers.split(" ", 2)[1];
                
                if ("" === shareMealOneValue || null == shareMealOneValue) {
                    MessageBox.alert("请正确填写共享副号一");
                    return;
                }
                if ("" === shareMealTwoValue || null == shareMealTwoValue) {
                    MessageBox.alert("请正确填写共享副号二");
                    return;
                }
                if (shareMealOneValue === shareMealTwoValue) {
                    MessageBox.alert("填写共享副号不能一样");
                    return;
                }
            }

            var params = "&NEW_PRODUCT_ID=" + $("#NEW_PRODUCT_ID").val()
                    + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
                    + "&SHAREMEALONE=" + shareMealOneValue
                    + "&SHAREMEALTWO=" + shareMealTwoValue
                    + "&BOOKING_DATE=" + $("#BOOKING_DATE").val();
            $.beginPageLoading("校验共享号码...");
            $.ajax.submit(null, "checkShareMealPhoneNum", params, null,
                function (rtnData) {
                    $.endPageLoading();
                    if (rtnData != null && rtnData.length > 0) {
                        $("#SHAREMEAL_FLAG").val("1");
                        $("#SHAREMEALONE_NAME").val(shareMealOneValue);
                        $("#SHAREMEALTWO_NAME").val(shareMealTwoValue);
                        MessageBox.success("校验通过！");
                    }
                },
                function (error_code, error_info, detail) {
                    $.endPageLoading();
                    $("#SHAREMEAL_FLAG").val("0");
                    MessageBox.error("错误提示", error_info, null, null, null, detail);
                });
        },

        submit: function () {
//            var shareMealLi = $("#SHAREMEAL_LI").css("display");
//
//            if (shareMealLi !== "none") {
//                var shareMealNumbers = $("#SHAREMEAL_NUMBERS").val();
//                if (shareMealNumbers.indexOf(",") === -1) {
//                    MessageBox.alert("请按格式正确填写共享副号！");
//                    return;
//                }
//                var shareMealOneValue = $("#SHAREMEALONE_NAME").val();
//                var shareMealTwoValue = $("#SHAREMEALTWO_NAME").val();
//
//                if ("" === shareMealOneValue) {
//                    MessageBox.alert("请正确填写共享副号一");
//                    return;
//                }
//                if ("" === shareMealTwoValue) {
//                    MessageBox.alert("请正确填写共享副号二");
//                    return;
//                }
//                if (shareMealOneValue === shareMealTwoValue) {
//                    MessageBox.alert("填写共享副号不能一样");
//                    return;
//                }
//                if ("0" === $("#SHAREMEAL_FLAG").val()) {
//                    MessageBox.alert("请点击校验通过！");
//                    return;
//                }
//            }

            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            //BUG20190217084859  无线固话办理国际长途无法打发票问题 
            if(tradeTypeCode != "undefined" && tradeTypeCode !='' && tradeTypeCode != null && tradeTypeCode == '3803'){
				//发票处理
				changeProductExtend.dealInvoice2();
			}
            if (tradeTypeCode !== "undefined" && tradeTypeCode !== ""
                    && tradeTypeCode != null && tradeTypeCode === "110") {
                // 校验
                if (!changeProduct.confirmSubmit()) {
                    return false;
                }
                               
                //REQ201811300035 主套餐变更办理界面增加宽带权益判断提醒
                var paramDisct=null;
                var paramContent="";
                $.ajax.submit(null, 'getDisctTipsInfo', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(), null, function(paraData) { 
   					$.endPageLoading();
   					if(paraData!=null&&paraData.length > 0){
   						paramDisct=	paraData;   	
   						var submitData = selectedElements.getSubmitData();
   						submitData.each(function (item) {
   		                    var elementId = item.get("ELEMENT_ID");
   		                    if(paramDisct!=null){
   		                    	for(var i = 0; i < paramDisct.length; i++){
   		                    		if(paramDisct.get(i).get("PARA_CODE1")==elementId){
   		                    			paramContent=paramDisct.get(i).get("PARA_CODE20");
   		                    			break;
   		                    		}                  		
   		                    	}
   		                    }
   		                });
   						
   					}
   				}, function(error_code, error_info,detail) {
   					$.endPageLoading();
   				    MessageBox.error("错误提示", error_info, null, null, null, detail);
   				},{async:false});
                

                var submitData = selectedElements.getSubmitData();

                var size = submitData.length;
                var userProductId = $("#USER_PRODUCT_ID").val();
                var newProductId = $("#NEW_PRODUCT_ID").val();
                submitData.each(function (item) {
                    var productId = item.get("PRODUCT_ID");
                    var elementType = item.get("ELEMENT_TYPE_CODE");
                    if (userProductId === productId && elementType === "D") {
                        size--;
                    }               
                });
                                

                // 取消GPRS的时候，二次确认后，如果用户确认取消，则系统同时取消VOLTE服务。
                if (submitData && submitData.length > 0) {
                    var bResult22 = true;
                    var bResult190 = true;
                    for (var i = 0; i < submitData.length; i++) {
                        var item = submitData.get(i);
                        var strElementId = item.get("ELEMENT_ID");
                        var strElementTypeCode = item.get("ELEMENT_TYPE_CODE");
                        var strModifyTag = item.get("MODIFY_TAG");
                        if (strElementId === "22" && strElementTypeCode === "S"
                                && strModifyTag === "1") {
                            bResult22 = false;
                        }
                        if (strElementId === "190" && strElementTypeCode === "S"
                                && strModifyTag === "1") {
                            bResult190 = false
                        }
                    }

                    if (!bResult22 && bResult190) {
                        if (selectedElements.checkIsExist("190", "S")) {
                            var msg = "办理取消手机上网功能，则系统同时取消VOLTE服务，是否继续？";
                            MessageBox.confirm(msg, null,
                                function (btn) {
                                    if (btn === "ok") {
                                        confirmMessageBeforeSubmit(userProductId, newProductId);
                                    }
                                });
                            return;
                        }
                    }
                    
                    
                }
                
   			 var content1 = "是否要终止宽带1+活动？";
   			 $.ajax.submit(null, 'getCancelActiveInfos', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val(), null, function(rtnData) { 
   					$.endPageLoading();
   					if(rtnData!=null&&rtnData.length > 0){
   							if(newProductId==rtnData.get("PARAM_CODE"))
   							{
	   							 MessageBox.confirm(content1, null,
	   	                                function (btn) {
	   	                                    if (btn === "ok") {
	   	                                    	var	content2 = "是否立即终止？";
	   	                                    	MessageBox.confirm(content2, null,
	   	     	   	                                function (btn) {
	   	     	   	                                    if (btn === "ok") {
			   	     	   	                                $("#WDACTICE_ENDFLAG").val("Y");
			   	     	   	                                changeProduct.confirmSubmit()
			   	     	   	                                confirmMessageBeforeSubmit(userProductId, newProductId);
	   	     	   	                                    }else{
			   	     	   	                                $("#WDACTICE_ENDFLAG").val("N");
			   	     	   	                                changeProduct.confirmSubmit()
			   	     	   	                                confirmMessageBeforeSubmit(userProductId, newProductId);
	   	     	   	                                    }
	   	     	   	                                });
	   	                                    }
	   	                                });
   							}
   					}
   				}, function(error_code, error_info,detail) {
   					$.endPageLoading();
   				    MessageBox.error("错误提示", error_info, null, null, null, detail);
   				});

   			    var flag = false;
                $.ajax.submit(null, 'checkProPrice', "&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()
                    +"&SELECTED_ELEMENTS=" + selectedElements.getSubmitData().toString(), null, function(rtnData) {
                    $.endPageLoading();
                    if(rtnData!=null&&rtnData.length > 0){
                        var data = rtnData.get(0);
                        if(data != null && data.get("flag") == "1") {
                            flag = true;
                        }
                    }
                }, function(error_code, error_info,detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", error_info, null, null, null, detail);
                },{async:false});

                if(flag) {
                    MessageBox.confirm("请注意客户准备办理套餐降档业务，请重点做好客户挽留，根据弹窗提示引导客户办理更适合的套餐", null,
                        function (btn) {
                            if (btn === "ok") {
                                confirmMessageBeforeSubmit(userProductId, newProductId);
                            }
                        });
                    return;
                }
                
                confirmMessageBeforeSubmit(userProductId, newProductId);

                function confirmMessageBeforeSubmit(userProductId, newProductId) {
                    // 提示确认
                    var content = "";
                    var productName = $("#NEW_PRODUCT_NAME").val();
                    var bookingDate = $("#BOOKING_DATE").val();
                    var sysDate = $("#SYS_DATE").val();
                    if (bookingDate !== "undefined" && bookingDate !== ""
                            && sysDate !== "undefined" && sysDate !== "") {
                        if (bookingDate.substring(0, 10) === sysDate.substring(0, 10)) {
                            if (newProductId == null || newProductId === ""
                                    || userProductId === newProductId) {
                                content = "本次将受理【立即】产品变更业务，是否继续？"+paramContent;
                            } else {
                                content = "本次将受理【立即】【" + productName
                                        + "】产品变更业务，是否继续？"+paramContent;
                            }
                        } else {
                            if (newProductId == null || newProductId === ""
                                    || userProductId === newProductId) {
                                content = "本次将受理【预约】产品变更业务，是否继续？"+paramContent;
                            } else {
                                content = "本次将受理【预约"
                                        + bookingDate.substring(0, 10) + "】【"
                                        + productName + "】产品变更业务，是否继续？"+paramContent;
                            }
                        }
                    }
                    MessageBox.confirm("确认提示", content,
                        function (btn) {
                            if (btn === "ok") {
                                $.cssubmit.submitTrade(); // 提交台账
                            }else{
                            	$("#WDACTICE_ENDFLAG").val("");
                            }
                        });
                }
            } else {
                return changeProduct.confirmSubmit();
            }
        },

        confirmSubmit: function () {
            // 重置校验监听方法
            $.tradeCheck.setListener("checkBeforeTrade");

            if (!$.validate.verifyAll()) {
                return false;
            }
            if (!selectedElements.checkForcePackage()) {
                return false;
            }

            var data = selectedElements.getSubmitData();
            if (data && data.length > 0) {
                var param = "&SELECTED_ELEMENTS=" + data.toString()
                        + "&NEW_PRODUCT_ID=" + $("#NEW_PRODUCT_ID").val()
                        + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val();
                if ($("#EFFECT_NOW").attr("checked")) {
                    param += "&EFFECT_NOW=" + "1";
                }

                param += "&REMARK=" + $("#REMARK").val()
                        //+ "&SHAREMEALONE=" + $("#SHAREMEALONE_NAME").val()
                        //+ "&SHAREMEALTWO=" + $("#SHAREMEALTWO_NAME").val()
                        ;

                if (typeof changeProductExtend.getOtherSubmitParam === "function") {
                    param += changeProductExtend.getOtherSubmitParam();
                }
                if($("#WDACTICE_ENDFLAG").val()!= "undefined" && $("#WDACTICE_ENDFLAG").val() !=''&&$("#WDACTICE_ENDFLAG").val()!=null){
					param+="&WDACTICE_ENDFLAG="+$("#WDACTICE_ENDFLAG").val();
				}

                $.cssubmit.addParam(param);
                return true;
            } else {
                MessageBox.alert("您没有进行任何操作，不能提交");
                return false;
            }
        },

        beforeSubmitSerialNumber: function () {
            $.tradeCheck.addParam(null);
            $.tradeCheck.setListener("checkBeforeTrade");
        },

        effecNowEvent: function (o) {
            if (o.checked) {
                selectedElements.effectNow();
            } else {
                selectedElements.unEffectNow();
            }
        }
    });
})();

/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 选择APN类型处理
 * @return
 */
function apnTypeChanged() 
{
	var apnType = $("#APNTYPE").val();
	$("#COMMONAPN").val("");	
	$("#APNNAME").val("");			
	$("#LOWPOWERMODE").val("");
	$("#RAUTAUTIMER").val("");
	$("#APNNAME").attr("readonly", false);
	$("#LOWPOWERMODE").attr("disabled", false);
	//通用APN
	if ("0" == apnType) 
	{
		$("#COMMONAPN").attr("nullable", "yes");
		$.each($("#COMMONAPN").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "");
		});
		//通用APN类型，APNNAME只能选，不能填
		$("#APNNAME").attr("readonly", true);	
	} 
	//专用APN
	else if("1" == apnType)
	{
		$("#COMMONAPN").attr("nullable", "yes");
		$.each($("#COMMONAPN").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "display:none");
		});
		//专用APN类型，APNNAME只能填，不能选
		$("#APNNAME").attr("readonly", false);	
	}
}

/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 选择通用APN名称时处理
 * @return
 */
function commonApnSelected() 
{
	var commonApnName = $("#COMMONAPN").val();
	if ("" == commonApnName || null == commonApnName) 
	{
		return;
	}
	var apnType = $("#APNTYPE").val();
	if ("" == apnType || null == apnType) 
	{
		alert("请先选择APN类型");
		$("#COMMONAPN").val("");
		return;
	}
	// 将值写入APNNAME属性，
	$("#APNNAME").val(commonApnName);
	$.beginPageLoading();
	//查询通用apn模板
	var param = '&APNNAME='
			  + commonApnName
			  + '&METHOD_NAME=queryCommonApnTemplate&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.gfff.CreateWlwGroupMemberP';
	Wade.httphandler.submit('',
							'com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam',
							'productParamInvoker', param, afterQueryCommonApnTemplate, errFun);

}

function errFun(error_code,error_info)
{
	$.endPageLoading();
	alert(error_info);
}

/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 根据模板更改LOWPOWERMODE和RAUTAUTIMER的值
 */
function afterQueryCommonApnTemplate(data)
{
	$.endPageLoading();
	var lowPowerMode = data.get("LOWPOWERMODE");
	$("#LOWPOWERMODE").val(lowPowerMode);
	$("#LOWPOWERMODE").attr("disabled", true);	//通用APN对应的LOWPOWERMODE模式是固定的，不可选择
	var nullable = data.get("RAUTAUTIMER");
	$("#RAUTAUTIMER").attr("nullable", nullable);
	//必填则显示
	if("no" == nullable)
	{
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "");
		});
	}
	//非必填则不显示
	else{
		$("#RAUTAUTIMER").val("");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "display:none");
		});
	}
}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 专用APN省电模式选择处理
 * @return
 */
function lowPowerModeChanged()
{
	var lowPowerMode = $("#LOWPOWERMODE").val();
	//alert("getAttrVale LOWPOWERMODE="+lowPowerMode);
	if("PSM"==lowPowerMode || "BOTH"==lowPowerMode)
	{
		$("#RAUTAUTIMER").attr("nullable", "no");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "");
		});
	}
	else
	{
		$("#RAUTAUTIMER").attr("nullable", "yes");
		$("#RAUTAUTIMER").val("");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj)
		{
			$(obj).attr("style", "display:none");
		});
	}
}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 属性框填值后【确认】按钮触发事件
 * @return
 */
function confirmNbiot1218301Svc(itemIndex)
{
	var apnType = $("#APNTYPE").val();
	var apnName = $("#APNNAME").val();
	if(apnType==null || apnType=="")
	{
		alert("请先选择APN类型,再做修改,若不修改则点【取消】！");
		return false;
	}
	//专用APN名称不能与通用APN名称一致
	var comApnNames = "cmnbiot,cmnbiot1,cmnbiot2,cmnbiot3,cmnbiot4,cmnbiot5,cmnbiot6,";
	if("1" == apnType)
	{
		if(comApnNames.indexOf(apnName+",")!=-1)
		{
			alert("专用APN名称不能与通用APN名称["+comApnNames+"]一致！");
			return false;
		}
	}
	var lowPowerMode = $("#LOWPOWERMODE").val();
	var rauTauTimer = $("#RAUTAUTIMER").val();
	if(rauTauTimer=="" && ("PSM"==lowPowerMode || "BOTH"==lowPowerMode))
	{
		alert("低电耗模式为[PSM,PSM+eDRX]需要填写[RAU/TAU定时器]参数值!");
		return false;
	}
	else if(rauTauTimer!="" && ("PSM"!=lowPowerMode && "BOTH"!=lowPowerMode))
	{
		alert("低电耗模式不为[PSM,PSM+eDRX]不需要填写[RAU/TAU定时器]参数值!");
		$("#RAUTAUTIMER").val("");
		return false;
	}
	
	//这两个属性不需要传值到后台生成台帐
	$("#COMMONAPN").val("");
	$("#APNTYPE").val("");
	selectedElements.confirmAttr(itemIndex);
}