(function($){
    if(typeof($.SaleActiveTrade=="undefined")){
        $.SaleActiveTrade={
            saleActiveData:{},
            checkSaleBook:function(data){
                debugger;
                var sn=$("#AUTH_SERIAL_NUMBER").val();
                $("#NET_ORDER_ID").val("");
                $.SaleActiveTrade.saleActiveData = data;
                $.beginPageLoading("查询终端预约信息。。。");
                ajaxSubmit('', 'checkSaleBook', "&SERIAL_NUMBER="+sn, '', 
                   function(ajaxdata){
                      debugger;
                      $.endPageLoading();	
                      if(ajaxdata && ajaxdata.get("AUTH_BOOK_SALE")==1){
                         $("#AUTH_SERIAL_NUMBER").attr("disabled", false);
                         $.popupPage("saleactive.sub.BookList", "querySaleBookList", "&SERIAL_NUMBER=" + sn, "用户已经预约办理了如下活动，请优先办理该业务！", "640", "250",
                                     null, null, null, false, false);
                      }else{
                         $.SaleActiveTrade.refreshPartAtferAuth(data);
                      }
                   },
                   function(error_code,error_info){
                      $.endPageLoading();
		              alert(error_info);
                  });
            },
            setParam:function(netOrderId){
                $("#NET_ORDER_ID").val(netOrderId);
            },
            refreshPartAtferAuth:function(data){
               if(data==null || typeof(data)=="undefined"){
                  data = $.SaleActiveTrade.saleActiveData
               };
               saleactiveModule.clearSaleActive();
	           var eparchyCode = data.get('USER_INFO').get('EPARCHY_CODE');
	           $('#custinfo_EPARCHY_CODE').val(eparchyCode);
	           var acctDayInfo = data.get("ACCTDAY_INFO");
	           var labelId = $('#LABEL_ID').val();
//	           saleactive.readerComponent(data.get('USER_INFO').get('USER_ID'), acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"),labelId);
	           saleactive.readerComponent(data.get('USER_INFO').get('USER_ID'), acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"),labelId,data.get('USER_INFO').get('CUST_ID'));
            },
            onTradeSubmit:function(){
               if(!saleactiveModule.saleactiveSubmitJSCheck()) return false;  
			   var saleactiveData = saleactiveModule.getSaleActiveSubmitData();
			   var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val() + "&SMS_VERI_SUCCESS=" + $("#SMS_VERI_SUCCESS").val();
			   param += '&SALEACITVEDATA='+saleactiveData.toString();
			   
			   var netOrderId = $("#NET_ORDER_ID").val();
			   if(netOrderId!="undefined" && ""!=netOrderId){
			      param +='&NET_ORDER_ID='+netOrderId;
			   }
			   if(typeof($("#TRADE_TYPE_CODE").val()) != "undefined" && $("#TRADE_TYPE_CODE").val() != null && $("#TRADE_TYPE_CODE").val() == '3814'){
				   param+= "&ORDER_TYPE_CODE="+$("#TRADE_TYPE_CODE").val(); //无线固话营销活动传ORDER_TYPE_CODE
				}
			   if(typeof($("#TRADE_TYPE_CODE").val()) != "undefined" && $("#TRADE_TYPE_CODE").val() != null && $("#TRADE_TYPE_CODE").val() == '3815'){
				   param+= "&ORDER_TYPE_CODE="+$("#TRADE_TYPE_CODE").val(); //铁通营销活动传ORDER_TYPE_CODE
				}
			   $.cssubmit.addParam(param);
			   var checkParam = "";
			   checkParam += '&PACKAGE_ID_A='+saleactiveData.get('PACKAGE_ID');
			   checkParam += '&PRODUCT_ID_A='+saleactiveData.get('PRODUCT_ID');
			   if(saleactiveData.get('OTHER_NUMBER','') != ''){
				  checkParam += '&OTHER_NUMBER=' + saleactiveData.get('OTHER_NUMBER','');
			   }
			   $.tradeCheck.addParam(checkParam);
			   return true;
            },
            /**
             * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
             * chenxy3 20160901 
             * 
             * */
			redPackPlaceOrderCall:function(){ 
               var redPackVal=$("#RED_PACK_VALUE").val(); 
               var deviceCost=$("#DEVICE_COST").val(); 
               if(redPackVal!=null && redPackVal>0){
            	   var saleactiveData1 = saleactiveModule.getSaleActiveSubmitData(); 
 				   var callParam="SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&AMT_VAL="+redPackVal
 				   +"&PRODUCT_ID="+saleactiveData1.get('PRODUCT_ID')+"&PACKAGE_ID="+saleactiveData1.get('PACKAGE_ID')+"&CAMPN_TYPE="+saleactiveData1.get('CAMPN_TYPE')
 				   +"&EPARCHY_CODE=0898&RES_NO="+saleactiveData1.get('SALEGOODS_IMEI');
 				   //调和包平台接口发送验证码（下订单）
 				   var finalFee=deviceCost-redPackVal;
 				   if(window.confirm("购机款为（"+deviceCost/100+"）元，使用电子券金额为（"+redPackVal/100+"）元，现金支付金额为（"+finalFee/100+"）元。")==true){
 					  $.SaleActiveTrade.redPackPlaceOrder(callParam,redPackVal); 
 				   }
 				   
               }else{
            	   alert("无红包金额！");
            	   $("#CSSUBMIT_BUTTON").click();
               }
			},
			popupHandler:"RED_PACK_PARAMS",
            redPackPlaceOrder:function(callParam,redPackVal){ 
				var userId = $('#SALEACTIVE_USER_ID').val();
            		$.beginPageLoading("和包平台下发验证码。。。");
             		ajaxSubmit('', 'redPackPlaceOrder', callParam+"&USER_ID="+userId, '',function(ajaxdata){ 
                         $.endPageLoading();	
                         if(ajaxdata.get("X_RESULTCODE")=="1"){ 	
                        	 var redOrderId=ajaxdata.get("RED_ORDERID");
                        	 var redMerId=ajaxdata.get("RED_MERID");
                        	 var sn=ajaxdata.get("SERIAL_NUMBER"); 
                        	 var prodId=ajaxdata.get("PRODUCT_ID");
                        	 var packId=ajaxdata.get("PACKAGE_ID");
                        	 
                        	 var amtVal=ajaxdata.get("AMT_VAL");
                        	 var deviceModelCode=ajaxdata.get("DEVICE_MODEL_CODE");
                        	 var serialNum=ajaxdata.get("SERIAL_NUMBER");
                        	 
                        	 var inparam="&RED_ORDERID="+redOrderId+"&RED_MERID="+redMerId+"&USER_ID="+userId+"&SERIAL_NUMBER="+sn+"&PRODUCT_ID="+prodId+"&PACKAGE_ID="+packId+"&AMT_VAL="+amtVal+"&DEVICE_MODEL_CODE="+deviceModelCode;
                        	 var pageResult=$.popupPage("saleactive.RedPackAuthCode", "initParams",inparam,  "请输入红包验证码！", "640", "500",$.SaleActiveTrade.popupHandler);
                        	 
                        	 
                         }
                    },
                    function(error_code,error_info){
                          $.endPageLoading();
          		          alert(error_info); 
                    });  
            },
            afterRedPackCall:function(){
            	$("#CSSUBMIT_BUTTON").click();
            },
            checkSMSBotton:function(){
            	var saleactiveData = saleactiveModule.getSaleActiveSubmitData();
            	var packageId=saleactiveData.get('PACKAGE_ID');
 			    var productId=saleactiveData.get('PRODUCT_ID');
 			    var userId = $('#SALEACTIVE_USER_ID').val();
 				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
 				var eparchyCode = $('#'+$('#SALEACTIVEMODULE_EPARCHY_CODE_COMPID').val()).val();
 				var limitCount=$("#LIMIT_COUNT").val();
 				var noticeContent=$("#NOTICE_CONTENT").val();
 				var smsCodeFlag=$('#CHECK_SMS_VERICODE').val();
 				var checkData = new Wade.DataMap();
				checkData.put("USER_ID",              userId);
				checkData.put("SERIAL_NUMBER",        serialNumber);
				checkData.put("EPARCHY_CODE",         eparchyCode);
				checkData.put("LIMIT_COUNT",          limitCount);
				checkData.put("SMS_VERI_CODE_TYPE",   smsCodeFlag);
				checkData.put("NOTICE_CONTENT",       noticeContent);  
 			    
// 			    alert("PACKAGE_ID:"+packageId+"####$PRODUCT_ID:"+productId+"$$$$smsCodeFlag=="+smsCodeFlag
// 			    		+"%%%%limitCount="+limitCount+"&&&&&noticeContent="+noticeContent); 
 			    if("2" === smsCodeFlag){
 				   if(window.confirm("确定要校验验证码吗?")){
 					  saleactiveModule.doSmsVariCode(packageId, productId, null, null, checkData);
 				   }else{
 					  $("#submitDiv").css("display","");
					   $("#checkSMSBtn").css("display","none");
 				   }
 			    }
 			    else if("1" === smsCodeFlag){
 			    	saleactiveModule.doSmsVariCode(packageId, productId, null, null, checkData);
 			    }
            },
            displaySwitch:function(btn, o){
               var button = $(btn);
	           var div = $('#'+o);
	           if (div.css('display') != "none")
	           {
				  div.css('display', 'none');
				  button.children("i").attr('className', 'e_ico-unfold'); 
				  button.children("span:first").text("展示客户信息");
	           }else {
		          div.css('display', '');
		          button.children("i").attr('className', 'e_ico-fold'); 
		          button.children("span:first").text("不展示客户信息");
	           }
            }
        }
    }
})(Wade);

