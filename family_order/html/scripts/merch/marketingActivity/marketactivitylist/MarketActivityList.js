if(typeof(MarketActivityList)=="undefined"){
	window["MarketActivityList"]=function(){};
	
	var marketActivityList = new MarketActivityList();
}

(function(){
	$.extend(MarketActivityList.prototype,{
		
		readerComponent: function(param){
			$('#SALEACTIVE_LIST_DETAIL').html('');
			var needCheck = $('#SALEACTIVE_NEED_CHECK').val();
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			param += "&SALEACTIVELIST_EPARCHY_CODE_COMPID=" + $('#SALEACTIVE_EPARCHY_CODE_COMPID').val();
			param += '&NEED_CHECK='+needCheck;
			marketActivityList.queryPackageList(param);
		},
		checkSaleProductResultDeal: function(data,param)
		{
			var confirmSet = data.get("TIPS_TYPE_CHOICE");
			var warnSet = data.get("TIPS_TYPE_TIP");
			if(confirmSet && confirmSet.length>0){
				var flag = true;
				confirmSet.each(function(item, index, totalCount){
					if(!window.confirm(item.get("TIPS_INFO"))){
						flag = false;
						return false;
					}
				});
				if(!flag) return false;
			}
			if(warnSet && warnSet.length>0){
				warnSet.each(function(item, index, totalCount){
					window.alert(item.get("TIPS_INFO"));
				});
			}
			marketActivityList.queryPackageList(param);
		},
		queryPackageList: function(param)
		{
			$.beginLoading("活动查询中...");
	    	ajaxSubmit(null,null,param,$('#SALEACTIVELIST_COMPONENT_ID').val(), function(data){
				$.endLoading();
				if(data && data.length > 0){
					var noRecords = data.get("NO_RECORDS");
					if("1" == noRecords){
						$("#queryError").css("display", "");
						$("#SALEACTIVE_LIST_DETAIL").css("display","none");
						$("#SEARCH_CONTENT").val("");
					}else{
						$("#queryError").css("display", "none");
						$("#SALEACTIVE_LIST_DETAIL").css("display","");
						hidePopup('mypop1','UI-search');
						
		                var allFail = data.get("ALL_FAIL");
		                if (allFail === "true") {
		                    MessageBox.alert("用户不满足参与该活动的条件，详细原因请点击相应营销包！");
		                } else {
		                    // 老用戶免费领取4G手机需求 chenxy3
		                    marketActivityList.checkProdNeedOldCustSn($("#SALE_PRODUCT_ID").val());//duhj add  这里需要测试
		                }

					}
				}
				marketActivityList.goPage(1);
				
                var iPhone6XResultCode = data.get("IPHONE6_X_RESULTCODE");
                if (typeof iPhone6XResultCode !== "undefined" && iPhone6XResultCode === "-1") {
                    $("#IPHONE6_IMEI").val("");
                    MessageBox.alert(data.get("IPHONE6_X_RESULTINFO"));
                }

				//展示终端信息
			    var goodsInfo = data.get("GOODS_INFO");
			    if(typeof(goodsInfo)!="undefined"){
			       saleActiveFilter.showGoodsInfo(goodsInfo);
			    }else{
			       saleActiveFilter.hiddenGoodsInfo();
			    }
				$.endLoading();
			},
			function(errorcode, errorinfo){
				$.endLoading();
				MessageBox.error("活动列表查询失败", errorinfo, function(btn){});
			});
		},
		
        /**
         * 老用户免费领取4G手机业务
         * 判断产品是否需要录入老用户手机号
         * chenxy3 2015-09-21
         */
        checkProdNeedOldCustSn: function (productId) {
            var params = "&PRODUCT_ID=" + productId
                    + "&CHECK_TYPE=" + "CHECKPROD";
            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var ifNeed = ajaxData.get("IF_NEED");
                    if (ifNeed === "Y") {
                        $("#IF_NEW_USER_ACTIVE").val(ajaxData.get("IF_NEW_USER_ACTIVE"));
                        showPopup("saleActiveListPopup", "checkProdOldCustSnItem", true);
                        marketActivityList.disableSaleActiveListAfterPopupProdCheck();
                        return;
                    }
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    marketActivityList.afterPopupProdCheckFailure(false);
                });
        },
        disableSaleActiveListAfterPopupProdCheck: function () {
            //var $ul = $("#saleActiveListUl");
            var $ul = $("#SaleActiveListTable_Body");

            if ($.browser.msie) {
                $ul.css("display", "none");
            } else {
                $ul.addClass("e_dis");
            }
        },
		selectSaleActiveAction: function(obj){
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
			var imei = $("#NEW_IMEI").val();
			//$('#TEMP_PACKAGE_ID').val(packageId);
			//$('#TEMP_CAMPN_TYPE').val(campnType);
			// 中文的话要加密下
			var goodsInfo = $("#GOODS_INFO").val();
			var codeGoodsInfo = encodeURIComponent(goodsInfo);			
			var terminalDetailInfo = $("#TERMINAL_DETAIL_INFO").val();
			var codeTerminalDetailInfo = encodeURIComponent(terminalDetailInfo);			
			var activeType = $("#ACTIVE_TYPE").val();			
			var netOrderId=$("#NET_ORDER_ID").val();
			var creditPurchases=$("#CREDIT_PURCHASES").val();
			var iphone6Imei=$("#IPHONE6_IMEI").val();
			var giftCode=$("#GIFT_CODE").val();
			var smsCode=$("#SMS_CODE").val();//红包验证码
						
			var allMoneyName=$("#ALL_MONEY_NAME").val();
			var codeAllMoneyName = encodeURIComponent(allMoneyName);
			var newImei = $("#SALEACTIVE_IMEI").val();
			var deviceModelCode = $("#DEVICE_MODEL_CODE").val();
			var saleUserId = $("#SALEACTIVE_USER_ID").val();
			var saleSerialNumber = $("#SALE_SERIAL_NUMBER").val();
			var eparchyCode = $("#custinfo_EPARCHY_CODE").val();
			var needCheck = $("#SALEACTIVE_NEED_CHECK").val();
			var queryMode = $("#SALEACTIVE_QUERY_MODE").val();
			var activeType = $("#ACTIVE_TYPE").val();
			var afterEvent = $("#AFTER_CHOICEPACKAGE_EVENT").val();
			var saleActiveEparchyCodeCompid = $("#SALEACTIVE_EPARCHY_CODE_COMPID").val();
			var saleStaffId = $("#SALE_STAFF_ID").val();
			var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
			var param="&SALE_PACKAGE_ID="+packageId+"&SALE_PRODUCT_ID="+productId+"&SALE_CAMPN_TYPE="+campnType+"&ACTIVE_TYPE="+activeType+"&TRADE_TYPE_CODE="+tradeTypeCode+"&AUTO_AUTH=true";
			if(typeof(newImei)!="undefined") param+="&NEW_IMEI="+newImei;
			if(typeof(deviceModelCode)!="undefined") param+="&DEVICE_MODE_CODE="+deviceModelCode;
			if(typeof(saleUserId)!="undefined") param+="&SALEACTIVE_USER_ID="+saleUserId;
			if(typeof(saleSerialNumber)!="undefined") param+="&SALE_SERIAL_NUMBER="+saleSerialNumber;
			if(typeof(saleSerialNumber)!="undefined") param+="&SERIAL_NUMBER="+saleSerialNumber;
			if(typeof(eparchyCode)!="undefined") param+="&SALE_EPARCHY_CODE="+eparchyCode;
			if(typeof(needCheck)!="undefined") param+="&SALEACTIVE_NEED_CHECK="+needCheck;
			if(typeof(afterEvent)!="undefined") param+="&AFTER_CHOICEPACKAGE_EVENT="+afterEvent;
			if(typeof(saleActiveEparchyCodeCompid)!="undefined") param+="&SALEACTIVE_EPARCHY_CODE_COMPID="+saleActiveEparchyCodeCompid;
			if(typeof(saleStaffId)!="undefined") param+="&SALE_STAFF_ID="+saleStaffId;
			if(typeof(codeGoodsInfo)!="undefined") param+="&GOODS_INFO="+codeGoodsInfo;
			if(typeof(codeTerminalDetailInfo)!="undefined") param+="&TERMINAL_DETAIL_INFO="+codeTerminalDetailInfo;
			if(typeof(netOrderId)!="undefined") param+="&NET_ORDER_ID="+netOrderId;
			if(typeof(creditPurchases)!="undefined") param+="&CREDIT_PURCHASES="+creditPurchases;
			if(typeof(codeAllMoneyName)!="undefined") param+="&ALL_MONEY_NAME="+codeAllMoneyName;
			if(typeof(iphone6Imei)!="undefined") param+="&IPHONE6_IMEI="+iphone6Imei;
			if(typeof(giftCode)!="undefined") param+="&GIFT_CODE="+giftCode;
			if(typeof(smsCode)!="undefined") param+="&SMS_CODE="+smsCode;

//			 openNav(packageName, "merch.MarketingActivityDetail", "onInitTrade", param);
			//此处营销包名称直接写为营销活动详情页面，这样只能打开一个详情页面，操作完一个再去操作另外一个，防止打开多个详情页，对时间有影响
			 openNav("营销活动详情页面", "merch.MarketingActivityDetail", "onInitTrade", param);

		},
		
		checkByPackageId: function(obj){
			var JQryobj = $(obj);
			JQryobj.parent().siblings().attr("class", "");
			JQryobj.parent().attr("class","on");

			var packageId = JQryobj.attr('packageId');
			var productId = JQryobj.attr('productId');
			var campnType = JQryobj.attr('campnType');
			var packageName = JQryobj.attr('packageName');
			var userId = $('#SALEACTIVELIST_USER_ID').val();
			var custId = $('#SALEACTIVELIST_CUST_ID').val();
			var errorFlag = JQryobj.attr('errorFlag');//duhj $('#SALEACTIVE_ERROR_FLAG').val();
			if(packageId != ""){
				var param = "&PRODUCT_ID=" + productId;
				param += "&USER_ID=" + userId;
				param += "&CUST_ID=" + custId;
				param += "&SPEC_TAG=" + "checkByPkgId";
				param += "&PACKAGE_ID=" + packageId;
				param += "&CAMPN_TYPE=" + campnType;
				param += "&CHECK_TYPE=CHECKPACK";
				var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
				$.beginPageLoading("营销包校验中。。。");
				ajaxSubmit(null,null,param,null, function(d){
                    $("#SALE_PACKAGE_ID").val(packageId);
                    $("#SALE_PACKAGE").val(packageName);
				    if(1==errorFlag)
				    {
				    	marketActivityList.selectSaleActiveAction(obj);
				    }else{
					    marketActivityList.checkPackNeedOldCustSn(productId, packageId, obj, serialNumber);
				    }
				  $.endLoading();
					
				},
				function(errorcode, errorinfo){
				    $.endLoading();
					MessageBox.error("营销包校验中失败", errorinfo, function(btn){});

				});				
			}
		},
        /**
         * 判断产品包是否需要录入老用户手机号
         * zhangxing3
         */
        checkPackNeedOldCustSn: function (productId, packageId, obj, serialNumber) {
            var params = "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&SERIAL_NUMBER=" + serialNumber
                    + "&CHECK_TYPE=" + "CHECKPACK";
            ajaxSubmit(null, "checkProdNeedOldCustSn", params, null,
                function (ajaxData) {
                    var ifNeed = ajaxData.get("IF_NEED");
                    if (ifNeed === "Y") {
                        showPopup("checkPackOldCustSnPopup", "checkPackOldCustSnItem", true);
                    } else if(ifNeed==="SIM"){
                    	showPopup("sendSMSCodePopup", "sendSMSCodeItem", true);//点击红包雨营销包时，弹出校验码的输入框--wangsc10--20190311
                    }else{
                    	marketActivityList.selectSaleActiveAction(obj);
                    }
                },
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    marketActivityList.afterPopupPackCheckFailure();
                })
        },

		changeColor:function(e,flag)
		{
			obj = $(e);
			var packageDesc = obj.attr('packageDesc');
			e.style.cursor="hand";
			if(flag==0){
				e.style.color="red";
				e.title = packageDesc;
			}
			else{
				e.style.color="black";
			}
		},
		clearSaleActiveList:function(){
		    $("#SaleActiveListTable_Body").html("");
            $("#SALE_PACKAGE_ID").val("");
            $("#SALE_PACKAGE").val("");
		},

        afterPopupPackCheckFailure: function () {
            hidePopup(this);
        },
        popupCheckPackOldCustSn: function () {
            var el = "oldCustSnForPack",
                $obj = $("#" + el),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");

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
                            marketActivityList.afterPopupPackCheckSuccess();
                        }
                    }
                }, function (error_code, error_info) {
                    MessageBox.error(error_info);
                    marketActivityList.afterPopupPackCheckFailure();
                });
        },
		goPage:function(currentPage)
		{
		    var itable = $("#SaleActiveListTable_Body li");
		    
		    var num = itable.length;
		    
		    var totalPage = 0;
		  
		    var pageSize = 21;
		     
		    if(num / pageSize > parseInt(num / pageSize))
		    {
		    	totalPage = parseInt(num / pageSize) + 1;
		    }
		    else
		    {
		        totalPage = parseInt(num / pageSize);
		    }     
		    
		    var startRow = (currentPage - 1) * pageSize + 1;
		    var endRow = currentPage * pageSize;
		    
		    endRow = (endRow > num) ? num : endRow;
		    
		    for(var i = 0; i < num; i++)
		    {  
                var irow = itable[i];
                
                if(i >= (startRow - 1) && i < endRow)
                {  
                    $(irow).show();
                }
                else
                {  
                    $(irow).hide();
                }  
            }
		    
		    $("#currentPageNum").html(currentPage);
		    $("#totalPageNum").html(totalPage);
		    
		    if(currentPage > 1)
		    {
		    	var prePage = currentPage - 1;
		    	$("#firstPage").removeClass("e_dis");
		    	$("#prePage").removeClass("e_dis");
		        $("#firstPage").attr("ontap", "marketActivityList.goPage(1)")
		        $("#prePage").attr("ontap", "marketActivityList.goPage("+prePage+")");
		    }else{
		    	$("#firstPage").addClass("e_dis");
		    	$("#prePage").addClass("e_dis");
		        $("#firstPage").attr("ontap", "")
		        $("#prePage").attr("ontap", "")
		    }  
		  
		    if(currentPage < totalPage){
		    	var nextPage = currentPage + 1;
		    	$("#nextPage").removeClass("e_dis");
		    	$("#lastPage").removeClass("e_dis");
		        $("#nextPage").attr("ontap", "marketActivityList.goPage("+nextPage+")");
		        $("#lastPage").attr("ontap", "marketActivityList.goPage("+totalPage+")");
		    }else{  
		    	$("#nextPage").addClass("e_dis");
		    	$("#lastPage").addClass("e_dis");
		        $("#nextPage").attr("ontap", "");
		        $("#lastPage").attr("ontap", "");
		    }     
		},
		jumpPage:function()
		{  
		    var num = parseInt($("#jumpPageNum").val());
		    
		    if(num < -1 || num > marketActivityList.totalPage){
		    	alert("输入页数超出范围！");
		    	return true;
		    }
		    
		    if(num != marketActivityList.currentPage)
		    {  
		        marketActivityList.goPage(num);
		    }  
		},
        popupCheckProdOldCustSn: function () {
            var el = "oldCustSnForProd",
                $obj = $("#" + el),
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");

            if (!$.validate.verifyField(el)) return;
            if ($obj.val() === serialNumber) {
                $.validate.alerter.one($obj[0], "校验号码不能与办理号码一致！");
                return;
            }

            var params = "&USER_ID=" + $("#SALEACTIVE_USER_ID").val()
                    + "&PRODUCT_ID=" + $("#SALE_PRODUCT_ID").val()
                    + "&CHECK_SERIAL_NUMBER=" + $obj.val();

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
                        marketActivityList.afterPopupProdCheckSuccess();
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        marketActivityList.afterPopupProdCheckFailure(true);
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
                                marketActivityList.afterPopupProdCheckSuccess();
                            }
                        }
                    }, function (error_code, error_info) {
                        MessageBox.error(error_info);
                        marketActivityList.afterPopupProdCheckFailure(true);
                    });
            }
        },
        //点击校验码弹出框确定按钮--wangsc10-20190311--start
        popupqueryProdRedMoney: function (obj) {
            var el = "RED_SMS_CODE",
            $obj = $("#" + el);
	        if ($obj.val() === null || "" === $obj.val()) {
	            $.validate.alerter.one($obj[0], "验证码不能为空！");
	            return;
	        }
	        //online 2019/4/11注掉了 保持与online一致 duhj
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
//                    	 marketActivityList.afterPopupPackCheckSuccess();
//                    }
//	            }, function (error_code, error_info) {
//	                MessageBox.error(error_info);
//	                marketActivityList.afterPopupPackCheckFailure();
//	            });
	        $("#SMS_CODE").val($obj.val());
        	hidePopup("sendSMSCodePopup");
        	saleActiveList.afterPopupPackCheckSuccess();
	    },
	    //end
        afterPopupProdCheckSuccess: function () {
            hidePopup("saleActiveListPopup", "checkProdOldCustSnItem");
            var $ul = $("#SaleActiveListTable_Body");

            if ($.browser.msie) {
                $ul.css("display", "");
            } else {
                $ul.removeClass("e_dis");
            }
        },
        
        afterPopupProdCheckFailure: function (hasTwoLayers) {
            hidePopup("saleActiveListPopup");
		    marketActivityList.clearSaleActiveList();
        },
        afterPopupPackCheckSuccess: function () {
            hidePopup(this);
            marketActivityList.selectSaleActiveAction();
        }
	    
		
	});
}
)();