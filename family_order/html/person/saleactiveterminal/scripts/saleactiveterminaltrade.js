(function($){
    if(typeof($.SaleActiveTrade=="undefined")){
        $.SaleActiveTrade={
            saleActiveData:{},
            checkSaleBook:function(data){
                debugger;
                var sn=$("#AUTH_SERIAL_NUMBER").val();
                $.SaleActiveTrade.saleActiveData = data;
                $.beginPageLoading("查询终端预约信息。。。");
                ajaxSubmit('', 'checkSaleBook', "&SERIAL_NUMBER="+sn, '', 
                   function(ajaxdata){
                      debugger;
                      $.endPageLoading();	
                      if(ajaxdata && ajaxdata.get("AUTH_BOOK_SALE")==1){
                         $("#AUTH_SERIAL_NUMBER").attr("disabled", false);
                         $.popupPage("saleactive.sub.BookList", "querySaleBookList", "&SERIAL_NUMBER=" + sn, "用户已经预约办理了如下活动，请优先办理该业务！", "640", "250");
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
	           saleactive.readerComponent(data.get('USER_INFO').get('USER_ID'), acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"),labelId);
            },
            onTradeSubmit:function(){
               if(!saleactiveModule.saleactiveSubmitJSCheck()) return false;
			   var saleactiveData = saleactiveModule.getSaleActiveSubmitData();
			   var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
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

