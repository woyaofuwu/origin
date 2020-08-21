(function($) {
	if (typeof ($.creditPurchasesSaleActiveTrade == "undefined")) {
		
		$.creditPurchasesSaleActiveTrade = {
				
				USER_INFO: $.DataMap(),
				
				/**
				 * auth查询后自定义查询
				 * @param data
				 * @returns
				 */
				refreshPartAtferAuth : function(data) {
					
					var userInfo = data.get("USER_INFO");
					USER_INFO = data.get("USER_INFO");
					var userId = userInfo.get("USER_ID");
					
					
					var param = "&ROUTE_EPARCHY_CODE="
						+ data.get("USER_INFO").get("EPARCHY_CODE")
						+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val()
					    +"&USER_ID="+userId;
					$.beginPageLoading("营销方案列表查询中。。。");
					$.ajax.submit('AuthPart', 'loadChildInfo', param, 'saleActiveInfoPart,saleActiveInfoPart2,saleActiveInfoPart3',
							function(data) 
							{
						            $.endPageLoading();
								    //$.creditPurchasesSaleActiveTrade.clearSaleActive();
								   // $.creditPurchasesSaleActiveTrade.queryAllAvailabelProducts();
								
							}, function(error_code, error_info)
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
					});
					
				},
				
				clearSaleActive: function () {
					$.creditPurchasesSaleActiveTrade.setQueryValidSaleActiveFlag("0");
		        },
		        
		        // 设置查询用户当前全部营销活动完成标记
		        setQueryValidSaleActiveFlag: function (v) {
		            $("#QUERY_VALID_SALE_FINISHED").val(v);
		        },
		        
		        cheackProduct : function(data) {
		        	// 如果没有选择活动类型，直接选择活动方案，需要将该方案对应的类型编码传入活动类型下拉框
		        	var title =$("#PRODUCT_ID_float li.on").attr("title");
                    var $campnType = $("#CAMPN_TYPE"); 
		            if("PRODUCT_ID2"==data.id){
		        		title = $("#PRODUCT_ID2_float li.on").attr("title");
		        		 $campnType = $("#CAMPN_TYPE2"); 
		        	}else if("PRODUCT_ID3"==data.id){
		        		title = $("#PRODUCT_ID3_float li.on").attr("title");
		        		 $campnType = $("#CAMPN_TYPE3"); 
		        	}
		            //var title = $("#PRODUCT_ID_float li.on").attr("title");
		           // var $campnType = $("#SALE_CAMPN_TYPE"); 
		            if (title) {
		                 $campnType.val(title);
		            } 
		            if ($.creditPurchasesSaleActiveTrade.isTerminalCampnType(title)) {
		            	if("PRODUCT_ID2"==data.id){
		            		$("#SALEGOODS_IMEI2").attr("readonly", false);
		            	}else if("PRODUCT_ID3"==data.id){
		            		$("#SALEGOODS_IMEI3").attr("readonly", false);
		            	}
		            }else{
		            	if("PRODUCT_ID"!=data.id){
		            		$.creditPurchasesSaleActiveTrade.queryPackages2(data);
		            	}
		            	if("PRODUCT_ID2"==data.id){
		            		$("#SALEGOODS_IMEI2").attr("readonly", true);
		            	}else if("PRODUCT_ID3"==data.id){
		            		$("#SALEGOODS_IMEI3").attr("readonly", true);
		            	}
		            }
					
                    var userId = USER_INFO.get("USER_ID");
					
					var param = "&ROUTE_EPARCHY_CODE="
						+ USER_INFO.get("EPARCHY_CODE")
						+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val()
					    +"&USER_ID="+userId;

					$.ajax.submit('AuthPart', 'cheackProduct', param, null,
							function(data) 
							{
								
							}, function(error_code, error_info)
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
					});
					
				},
				
				configSaleActives : function() {
					
					var areaParam = "salePackageInfoPart";
			        var campnType = $("#CAMPN_TYPE").val(),
			            productId = $("#PRODUCT_ID").val(),
			            packageId = $("#PACKAGE_ID").val(),
		                $IMEI = $("#SALEGOODS_IMEI"),
		                IMEI = $IMEI.val();
			        
			        
		            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
		            var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
		            var custId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");
		           

		            var params =  "&SERIAL_NUMBER=" + serialNumber + "&NEW_IMEI=" + IMEI
		                    + "&PRODUCT_ID=" + productId+"&ROUTE_EPARCHY_CODE="+ USER_INFO.get("EPARCHY_CODE")
		                    + "&SALEACTIVE_USER_ID=" + userId + "&SALEACTIVE_CUST_ID=" + custId+ "&PACKAGE_ID=" + packageId
		                    + "&CAMPN_TYPE=" + campnType+ "&SPEC_TAG=" + "renderByActiveQry";
		            $.ajax.submit('AuthPart', 'configSaleActives', params, null,
							function(data) 
							{
		            	       var downMoney= data.get("PARA_CODE9");
		            	       if(""!=downMoney){
		            	    	   $("#DOWN_MONEY").val(downMoney);
		            	       }else{
		            	    	   $("#DOWN_MONEY").val(0);
		            	       }
		            	       
		            	       var commparaPackages= data.get("PARA_CODE17");
		            	       if(""!=commparaPackages){
		            	    	   $("#COMMPARA_PACKAGES").val(commparaPackages);
		            	       }else{
		            	    	   $("#COMMPARA_PACKAGES").val("-1");
		            	       }
		            	
							}, function(error_code, error_info)
							{
								$.MessageBox.error(error_code, error_info);
					});
					
				},
		        
		        queryAllAvailabelProducts : function() {
					
					var userId = USER_INFO.get("USER_ID");
					
					var param = "&ROUTE_EPARCHY_CODE="
						+ USER_INFO.get("EPARCHY_CODE")
						+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val()
					    +"&USER_ID="+userId;

					$.beginPageLoading("业务资料查询。。。");
					$.ajax.submit('AuthPart', 'queryAllProducts', param, 'saleActiveInfoPart,saleActiveInfoPart2,saleActiveInfoPart3',
							function(data) 
							{
						        $.endPageLoading();
							}, function(error_code, error_info)
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
					});
					
				},
				
				changeCampnType : function(data) {
					var areaParam = "salePackageInfoPart";
					var campnType = $("#CAMPN_TYPE").val();
					if("CAMPN_TYPE2"==data.id){
                	    areaParam = "salePackageInfoPart2";
                	    campnType = $("#CAMPN_TYPE2").val();
					}else if("CAMPN_TYPE3"==data.id){
                        areaParam = "salePackageInfoPart3";
                        campnType = $("#CAMPN_TYPE3").val();
					}
					
					var userId = USER_INFO.get("USER_ID");
					var param = "&ROUTE_EPARCHY_CODE="
						+ USER_INFO.get("EPARCHY_CODE")
						+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val()
					    +"&USER_ID="+userId+"&LABEL_ID="+campnType;

					$.beginPageLoading("业务资料查询。。。");
					$.ajax.submit('AuthPart', 'queryAllProducts', param, areaParam,
							function(data) 
							{
								
							}, function(error_code, error_info)
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
					});
					
				},
				
				queryPackages2: function (data) {
					 var areaParam = "salePackageInfoPart2";
					 var productId = $("#PRODUCT_ID2").val();
					 var campnType = $("#CAMPN_TYPE2").val();
					 if("PRODUCT_ID3"==data.id){
						 areaParam = "salePackageInfoPart3";
						 productId = $("#PRODUCT_ID3").val();
						 campnType = $("#CAMPN_TYPE3").val();
					 }
					 var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
			         var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
			         var custId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");
			         
					 var params =  "&SERIAL_NUMBER=" + serialNumber
	                    + "&PRODUCT_ID=" + productId+"&ROUTE_EPARCHY_CODE="+ USER_INFO.get("EPARCHY_CODE")
	                    + "&SALEACTIVE_USER_ID=" + userId + "&SALEACTIVE_CUST_ID=" + custId
	                    + "&CAMPN_TYPE=" + campnType+ "&SPEC_TAG=" + "renderByActiveQry";
			            $.beginPageLoading("营销包校验中。。。");
			            $.ajax.submit('AuthPart', 'renderByActiveQry', params, areaParam,
								function(data) 
								{
			            	        $.endPageLoading();
								}, function(error_code, error_info)
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
						});
					 
				 },
				 
				 isTerminalCampnType: function (campnType) {
		            var terminalCampnType = "YX03|YX06|YX07|YX08|YX09";
		            return campnType !== "" && terminalCampnType.indexOf(campnType) > -1;
		         },
				
				 queryPackages: function (data) {
					    var areaParam = "salePackageInfoPart";
				        var campnType = $("#CAMPN_TYPE").val(),
				            productId = $("#PRODUCT_ID").val(),
			                $IMEI = $("#SALEGOODS_IMEI"),
			                IMEI = $IMEI.val();
				        
				        if("SALEGOODS_IMEI2"==data.id){
                    	    campnType = $("#CAMPN_TYPE2").val(),
							productId = $("#PRODUCT_ID2").val(),
							$IMEI = $("#SALEGOODS_IMEI2"),
							IMEI = $IMEI.val();
                    	    areaParam = "salePackageInfoPart2";
						}else if("SALEGOODS_IMEI3"==data.id){
                            campnType = $("#CAMPN_TYPE3").val(),
							productId = $("#PRODUCT_ID3").val(),
							$IMEI = $("#SALEGOODS_IMEI3"),
							IMEI = $IMEI.val();
                            areaParam = "salePackageInfoPart3";
						}
				        
			            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
			            var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
			            var custId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");
			           

			            var params =  "&SERIAL_NUMBER=" + serialNumber + "&NEW_IMEI=" + IMEI
			                    + "&PRODUCT_ID=" + productId+"&ROUTE_EPARCHY_CODE="+ USER_INFO.get("EPARCHY_CODE")
			                    + "&SALEACTIVE_USER_ID=" + userId + "&SALEACTIVE_CUST_ID=" + custId
			                    + "&CAMPN_TYPE=" + campnType+ "&SPEC_TAG=" + "renderByActiveQry";
			            $.beginPageLoading("营销包校验中。。。");
			            $.ajax.submit('AuthPart', 'renderByActiveQry', params, areaParam,
								function(data) 
								{
			            	        $.endPageLoading();
								}, function(error_code, error_info)
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
						});
			        },
			        
			    checkSubmitBefore: function (data) {
			    	 var IMEI = $("#SALEGOODS_IMEI").val();
			    	 var IMEI2 = $("#SALEGOODS_IMEI2").val();
			    	 var IMEI3 = $("#SALEGOODS_IMEI3").val();
			    	 if(IMEI==IMEI2||(IMEI2==IMEI3&&IMEI2!=""&&IMEI3!="")||IMEI==IMEI3){
			    		 MessageBox.alert("告警提示", "终端串码有重复！");
			    		 return false;
			    	 }
			    	 
			    	 var pakage = $("#PACKAGE_ID").val();
			    	 var pakage2 = $("#PACKAGE_ID2").val();
			    	 var pakage3 = $("#PACKAGE_ID3").val();
			    	 if(""==pakage){
			    		 MessageBox.alert("告警提示", "营销活动1必须办理！");
			    		 return false;
			    	 }
//			    	 if(""!=pakage2&&pakage!=pakage2){
//			    		 MessageBox.alert("告警提示", "营销活动1与营销活动2的营销包必须一致！");
//			    		 return false;
//			    	 }
//			    	 if(""!=pakage3&&pakage!=pakage3){
//			    		 MessageBox.alert("告警提示", "营销活动1与营销活动3的营销包必须一致！");
//			    		 return false;
//			    	 }
			    	 var packages = $("#COMMPARA_PACKAGES").val();
			    	 if("-1"!=packages){
			    	 	 if(pakage2==""){
			    	 		pakage2=-1;
			    	 	 }
			    		 if(packages.indexOf(pakage2)==-1){
			    			 MessageBox.alert("告警提示", "办理活动1必须再受理"+packages+"活动2!");
				    		 return false;
			    		 }
			    	 }
                    // 费用信息确认后返回true，可以继续办理业务
                    if (this.getFee(pakage2,pakage3)){

                        return this.showFeeInfo();
                    }else {
                        MessageBox.alert("告警提示", "无法获取费用信息！");
                        return false;
                    }
		        },

		        getFee:function (PACKAGE_ID2,PACKAGE_ID3) {// 获取费用信息
					var flag = false;
                    var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                    var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
                    var custId = $.auth.getAuthData().get("CUST_INFO").get("CUST_ID");

                    var params =  "&SERIAL_NUMBER=" + serialNumber+"&PACKAGE_ID2=" + PACKAGE_ID2 + "&PACKAGE_ID3=" + PACKAGE_ID3 +
                    "&ROUTE_EPARCHY_CODE="+ USER_INFO.get("EPARCHY_CODE")
                    + "&SALEACTIVE_USER_ID=" + userId + "&SALEACTIVE_CUST_ID=" + custId;

                    $.ajax.submit('', 'checktradeFeeInfo', params, '',
                        function(data)
                        {
                            $.endPageLoading();
                            var packge2Fee = data.get("PACKAGE2_FEE");
                            var packge3Fee = data.get("PACKAGE3_FEE");
                            if (packge2Fee){
                                $("#PACKAGE2_FEE").val(packge2Fee.get("PACKAGE_FEE"));
                                // $("#PACKAGE2_FEE_TYPE_CODE").val(packge2Fee.get("PACKAGE_FEE_TYPE_CODE"));
							}
                            if (packge3Fee){
                                $("#PACKAGE3_FEE").val(packge3Fee.get("PACKAGE_FEE"));
                                // $("#PACKAGE3_FEE_TYPE_CODE").val(packge3Fee.get("PACKAGE_FEE_TYPE_CODE"));
                        	}
                            flag = true;

                        }, function(error_code, error_info)
                        {
                            $.endPageLoading();
                            $.MessageBox.error(error_code, error_info);
                        },
                        {async:false});
                    return flag;
                },
				getPackgeName:function (id) {

                    var packageName = $("#"+id).find("span").html();
					if (!packageName){
                        packageName = "";
					}
                    return packageName;
                },
				showFeeInfo: function () {
                    // 费用信息提示--- 开始
                    var flag = false;
                    var package2Fee = $("#PACKAGE2_FEE").val();
                    var package3Fee = $("#PACKAGE3_FEE").val();
                    var package2PayFee = parseInt(package2Fee);
                    var package3PayFee = parseInt(package3Fee);
                    var totalPayFee = package2PayFee+package3PayFee;
                    var msg = "<br\/>";
                    msg +=  this.getPackgeName("PACKAGE_ID2_span") + "收取" +  ( Math.abs(package2PayFee)/100 ) + "元<br\/>";
                    msg +=  this.getPackgeName("PACKAGE_ID3_span") + "收取" +  ( Math.abs(package3PayFee)/100 ) + "元<br\/>";
                    msg += "--------------------<br\/>";
                    msg += "合计" + (parseInt(totalPayFee/100)) + "元<br\/>";
                    msg += "是否继续?";

                    MessageBox.confirm("费用提示", "请确认费用明细"+msg, function(btn){
                        if(btn == "ok"){
                            $.cssubmit.submitTrade();//提交台账
                        }
                    });
                    // 费用信息提示--- 结束
					return false;
                }
		}
	}
})(Wade);


