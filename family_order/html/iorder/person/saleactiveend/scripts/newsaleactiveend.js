/** 查询后的校验 */
function refreshPartAtferAuth(data){
    var userInfo = data.get("USER_INFO");
    var param = "&USER_ID="+userInfo.get("USER_ID")+"&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('', 'loadBaseTradeInfo', param, 'activatePart,activateDetailPart,userOperPart', 
	function(data){
		$("#activateDetailDiv").css("display","none");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("错误提示",error_info);
    });
}

/**
 * 控制基本信息显示\隐藏
 * @param btn
 * @param o
 */
function displaySwitch(btn, o) {
	var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
		  div.css('display', 'none');
		  button.empty();
		  button.html('<span class="e_ico-unfold"></span>展示客户信息'); 
    }else {
       div.css('display', '');
       button.empty();
	   button.html('<span class="e_ico-fold"></span>隐藏客户信息'); 
    }
}

/** 选中某个营销活动 */
function selectActive(obj){
   var authData = $.auth.getAuthData();
   var userInfo = authData.get("USER_INFO");
   var serialNumber = userInfo.get("SERIAL_NUMBER");
   $.feeMgr.clearFeeList("237");
   var selectedDataset = getSelectedData();
   
   
   var productId = selectedDataset.get(0).get("PRODUCT_ID");
   var packageId = selectedDataset.get(0).get("PACKAGE_ID");
   var productName = selectedDataset.get(0).get("PRODUCT_NAME");
   
   var flag = selectedDataset.get(0).get("PRODT_FLAG");//0-记录为不在1530，即不允许终止；1-在1530，但不符合约定月份；2-即在1530，又符合约定月份
   if(flag != null && flag == "0"){
	   MessageBox.alert("提示","[" + productId + "|" + productName + "]不允许终止！");
		$('input:radio[name=saleactive]').attr("checked",false);
		return;
   }
   if(flag != null && flag == "1"){
		var checkMonth = selectedDataset.get(0).get("CHECK_MONTH");
		MessageBox.alert("提示","[" + productId + "|" + productName + "]未达到约定月份数" + checkMonth + "！不允许终止");
		$('input:radio[name=saleactive]').attr("checked",false);
		return;
   }
   
   var relationTradeId= selectedDataset.get(0).get("RELATION_TRADE_ID");
   var param = "&SERIAL_NUMBER=" + serialNumber + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&RELATION_TRADE_ID=" + relationTradeId;
   param += "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE");
   $.beginPageLoading("活动校验中........");
   
   $.ajax.submit('', 'checkSaleActive', param, '', 
	  function(ajaxDataset){
	     $("#IS_RETURN").attr("checked", false)
		 $("#IS_RETURN").attr("disabled", true);
		 //应蔡世泳要求，去掉费用的处理
		 //var deposit = data.get(0,"DEPOSIT_MONEY");
		 //insertFee("237", "2", "102", deposit);
		 //var present = ajaxDataset.get(0,"PRESENT_MONEY");
		 //insertFee("237", "0", "601", present);
		 param +="&USER_ID="+userInfo.get("USER_ID");
		 $.ajax.submit('', 'loadActiveDetailInfo', param, 'activateDetailPart,userOperPart', 
		    function(dataset){
		      if(dataset&&dataset.length>0){
		    	 var saleGoods=dataset.get("SALE_GOODS");
		         for(var i=0; i<saleGoods.length; i++) {
		            var resTypeCode = saleGoods.get(i,'RES_TYPE_CODE');
		            if(resTypeCode=='4'){
		               $("#IS_RETURN").val("0");
		               //$("#IS_RETURN").attr("disabled", false);
		               break;
		            }
		         } 
		      }
		      
		      //获取应收违约金
		      var resultTip=dataset.get("REFUND_MONEY").get("RESULT_TIP");
		      if(resultTip!="0"){
		    	  var resultTipInfo="【"+dataset.get("REFUND_MONEY").get("RESULT_TIP_INFO")+"】，请手工计算违约金！";
		    	  MessageBox.alert("提示",resultTipInfo);
		      }
		      var refundMoney=dataset.get("REFUND_MONEY").get("REFUND_MONEY");
		      $("#RETURNFEE").val(refundMoney);
		      $("#TRUE_RETURNFEE").val(refundMoney);
		      
		      
			  $("#activateDetailDiv").css("display","block");
			  $.endPageLoading();
			  
			 var isNeedWarm = ajaxDataset.get(0,"IS_NEED_WARM");
		     if(isNeedWarm=="1"){
		    	 var packageName=selectedDataset.get(0).get("PACKAGE_NAME");
		    	 MessageBox.alert("告警提示", "取消活动【"+productName+"("+packageName+")】后，将不能再参加!"); 
		     }
		     
		     var alertInfo = dataset.get("ALERT_INFO");
		     if(alertInfo.length > 0)
		     {
		    	 MessageBox.alert("提示",alertInfo);
		     }
		     
		    },
		    function(error_code,error_info){
			   $.endPageLoading();
			   MessageBox.alert("错误提示",error_info);
	        }
	     );
	     
	  },
	  function(error_code,error_info){
	     var jqueryObj = $(obj);
	     jqueryObj.attr("disabled", true);
	     jqueryObj.attr("checked", false);
		 $.endPageLoading();
		 MessageBox.alert("错误提示",error_info);
      }
    );
}

/** 获取营销活动信息 */
function getSelectedData(){
	var dataset = activeInfoTable.getCheckedRowsData("saleactive");
	if(dataset == null){
		dataset = new $.DatasetList();
	}
    return dataset;
}

/** 提交前校验 */
function checkSubmitBefore(){
   var selectedActives = getSelectedData();
   if(selectedActives.length===0){
	   MessageBox.alert("提示","请选择结束的活动后提交！");return false;
   }
   
   if(!$.validate.verifyAll("userOperPart")) {
			return false;
   }
   
   var authData = $.auth.getAuthData();
   var userInfo = authData.get("USER_INFO");
   var param = '&SERIAL_NUMBER='+userInfo.get("SERIAL_NUMBER");
   param += "&PRODUCT_ID="+selectedActives.get(0).get("PRODUCT_ID");
   param += "&PACKAGE_ID="+selectedActives.get(0).get("PACKAGE_ID");
   param += "&RELATION_TRADE_ID="+selectedActives.get(0).get("RELATION_TRADE_ID");
   param += "&CAMPN_TYPE="+selectedActives.get(0).get("CAMPN_TYPE");
   param += "&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
   param += "&REMARK="+$("#REMARK").val();
   param += "&IS_RETURN="+$("#IS_RETURN").val();
   param += "&RETURNFEE="+$("#TRUE_RETURNFEE").val();    
   $.cssubmit.addParam(param);
   return true;
}

/** 调用处被注释了，已不用 */
function insertFee(tradeTypeCode, feeMode, feeTypeCode, fee) {
	var feeData = new $.DataMap();
	feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
	feeData.put("MODE", feeMode);
	feeData.put("CODE", feeTypeCode);
	feeData.put("FEE", fee);
	$.feeMgr.insertFee(feeData);
}

/** 不知道干嘛用的，没地方调用 */
function addFeeList(obj){
    var jqueryObj = $(obj);
	if(jqueryObj.val()==""){
	   return false;
	}
	if(!$.isNumeric(jqueryObj.val())){
	   MessageBox.alert("提示","费用输入非法！");return false;
	}
	var fee = jqueryObj.val();
	var yearfee = fee*100;
	$.feeMgr.removeFee("237", "0", "602");
	insertFee("237", "0", "602", yearfee);
}

