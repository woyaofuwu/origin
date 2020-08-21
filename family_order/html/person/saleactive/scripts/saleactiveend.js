//重新计算违约金
var selectActiveHtmlElement;
function checkBackTerm(obj){
	if(obj.checked){
		selectActive(selectActiveHtmlElement,'1');
	}else{
		selectActive(selectActiveHtmlElement,'0');
	}
}
function refreshPartAtferAuth(data){
    var userInfo = data.get("USER_INFO");
    var param = "&USER_ID="+userInfo.get("USER_ID")+"&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('', 'loadBaseTradeInfo', param, 'activatePart,activateDetailPart,userOperPart', 
	function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function getSelectedData(){
    var dataset = new $.DatasetList();
    var tableObj = $.table.get("activeInfoTable");
    var tbodyObj = $("tbody", tableObj.getTable()[0]);
    var size = tableObj.tabHeadSize;
    if(tbodyObj){
       $("tr", tbodyObj[0]).each(function(index, item){
           var checked = $("input[name=saleactive]", item).attr("checked");
           if(checked){
               var rowData = tableObj.getRowData(null, index+size);
               if(rowData){dataset.add(rowData);}
           }
       });
    }
    return dataset;
}

function selectActive(obj,backTerm){
   selectActiveHtmlElement=obj;
   if(typeof(backTerm)=='undefined'){
	   backTerm="0";
   }
   var authData = $.auth.getAuthData();
   var userInfo = authData.get("USER_INFO");
   var serialNumber = userInfo.get("SERIAL_NUMBER");
   $.feeMgr.clearFeeList("237");
   var selectedDataset = getSelectedData();
   var productId = selectedDataset.get(0).get("PRODUCT_ID");
   var packageId= selectedDataset.get(0).get("PACKAGE_ID");
   var productName=selectedDataset.get(0).get("PRODUCT_NAME");
   var flag= selectedDataset.get(0).get("PRODT_FLAG");
   if(flag!=null && flag == "0"  ){
	   alert("您没有产品【'"+productId+"|"+productName+"'】终止权限！");
	   $('input:radio[name=saleactive]').attr("checked",false);
	   return;
   } 
   if ("66000602" == productId)//宽带候鸟营销活动，不能提前终止！
   {
	   	var resultTipInfo="根据业务部门规定，该活动不能提前终止，如有疑问请咨询业务部门活动负责人！ ";
   		alert(resultTipInfo);
   		$('input:radio[name=saleactive]').attr("checked",false);
   		return;
   }
   var relationTradeId= selectedDataset.get(0).get("RELATION_TRADE_ID");
   var param = "&SERIAL_NUMBER="+serialNumber+"&PRODUCT_ID="+productId+"&PACKAGE_ID="+packageId+"&RELATION_TRADE_ID="+relationTradeId +"&PRODUCT_MODE="+selectedDataset.get(0).get("PRODUCT_MODE");
   param+="&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
   $.beginPageLoading("活动校验中........");
   $.ajax.submit('', 'checkSaleActive', param, '', 
	  function(ajaxDataset){
	     debugger;
	     $("#IS_RETURN").attr("checked", false)
		 $("#IS_RETURN").attr("disabled", true);
	     
		 //应蔡世泳要求，去掉费用的处理
		 //var deposit = data.get(0,"DEPOSIT_MONEY");
		 //insertFee("237", "2", "102", deposit);
		 //var present = ajaxDataset.get(0,"PRESENT_MONEY");
		 //insertFee("237", "0", "601", present);
		 param +="&USER_ID="+userInfo.get("USER_ID")+"&BACK_TERM="+backTerm;
		 $.ajax.submit('', 'loadActiveDetailInfo', param, 'activateDetailPart,userOperPart', 
		    function(dataset){
			  //加退还摄像头显示
		      var backTermShow = ajaxDataset.get(0,"BACK_TERM_SHOW");
		      if(backTermShow=="1"){
		    	  $("#BACK_TERM_LI").css("display", "block");
		      }else{
		    	  $("#BACK_TERM_LI").css("display", "none");
		      }
		      if(backTerm=='0'){
		    	  $('#BACK_TERM').attr("checked",false);
		      }else{
		    	  $('#BACK_TERM').attr("checked",true);
		      }
		      $('#BACK_TERM').val(backTerm);
		      
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
		    	  var refundactiveFunc=dataset.get("REFUND_ACTIVE_FUNC");
		    	  if(refundactiveFunc=="1")
		    	  {
			    	  var refundFunc=dataset.get("REFUND_PRICE_FUNC");
				      if(refundFunc=="1"){
				    	  $("#TRUE_RETURNFEE_COST").attr("disabled",false);
				    	  $("#TRUE_RETURNFEE_PRICE").attr("disabled",false);
				      }
			      }else{
			    	var resultTipInfo="根据业务部门规定，该活动不能提前终止，如有疑问请咨询业务部门活动负责人！ 或者申请：营销活动终止无配置活动终止权限（REFUNDACTIVEFUNC）再处理。";
			    	alert(resultTipInfo);
			    	$('input:radio[name=saleactive]').attr("checked",false);
			      }
		      }else{
		    	  var refundFunc=dataset.get("REFUND_PRICE_FUNC");
			      if(refundFunc=="1"){
			    	  $("#TRUE_RETURNFEE_COST").attr("disabled",false);
			    	  $("#TRUE_RETURNFEE_PRICE").attr("disabled",false);
			      }
		      }
		      var refundMoney=dataset.get("REFUND_MONEY").get("REFUND_MONEY");
		      $("#RETURNFEE").val(refundMoney);
		      $("#TRUE_RETURNFEE").val(refundMoney);
		      
		      var refundMoneyPrice=dataset.get("REFUND_MONEY").get("REFUND_PRICE");
		      $("#TRUE_RETURNFEE_PRICE").val(refundMoneyPrice);
		      var refundMoneyCost=dataset.get("REFUND_MONEY").get("REFUND_COST");
		      $("#TRUE_RETURNFEE_COST").val(refundMoneyCost);
		      
			  $("#activateDetailDiv").attr("style","block");
			  $.endPageLoading();
			  
			 var isNeedWarm = ajaxDataset.get(0,"IS_NEED_WARM");
		     if(isNeedWarm=="1"){
		    	 var packageName=selectedDataset.get(0).get("PACKAGE_NAME");
		    	 MessageBox.alert("告警提示", "取消活动【"+productName+"("+packageName+")】后，将不能再参加!"); 
		     }
		     
		     var alertInfo = dataset.get("ALERT_INFO");
		     if(alertInfo.length > 0)
		     {
		     	alert(alertInfo);
		     }

		    },
		    function(error_code,error_info){
			   $.endPageLoading();
			   alert(error_info);
	        }
	     );
	     
	  },
	  function(error_code,error_info){
	     var jqueryObj = $(obj);
	     jqueryObj.attr("disabled", true);
	     jqueryObj.attr("checked", false);
		 $.endPageLoading();
		 alert(error_info);
      }
    );
}

function insertFee(tradeTypeCode, feeMode, feeTypeCode, fee) {
	var feeData = new $.DataMap();
	feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
	feeData.put("MODE", feeMode);
	feeData.put("CODE", feeTypeCode);
	feeData.put("FEE", fee);
	$.feeMgr.insertFee(feeData);
}

function addFeeList(obj){
    debugger;
    var jqueryObj = $(obj);
	if(jqueryObj.val()==""){
	   return false;
	}
	if(!$.isNumeric(jqueryObj.val())){
	   alert("费用输入非法！");return false;
	}
	var fee = jqueryObj.val();
	var yearfee = fee*100;
	$.feeMgr.removeFee("237", "0", "602");
	insertFee("237", "0", "602", yearfee);
}

function addFee(obj){
	var price = $("#TRUE_RETURNFEE_PRICE").val();
	var cost = $("#TRUE_RETURNFEE_COST").val();
	var total = 0;
	//total = (parseInt(price*100)+parseInt(cost*100))/100;
	total = parseFloat(price)+parseFloat(cost);
	total = total.toFixed(2);
	
	//$("#RETURNFEE").val(total);
    $("#TRUE_RETURNFEE").val(total);
}

function checkSubmitBefore(){
	/**
	 * REQ201805240036 终止营销活动收取违约金操作界面优化需求 by mengqx 2018-6-28
	 * @return
	 */
	var fee = $("#TRUE_RETURNFEE").val();
	MessageBox.confirm("费用提示","本次最终收取费用共"+ fee +"元，如金额无误请点击确认！",function(bt){
		if(bt=='ok'){
			debugger;
			   var selectedActives = getSelectedData();
			   if(selectedActives.length===0){
			      alert("请选择结束的活动后提交！");return false;
			   }
			   
			   if(!$.validate.verifyAll("userOperPart")) {
						return false;
			   }
			   //加是否退还摄像头标识
			   var backTerm="";
			   if( $("#BACK_TERM_LI").css("display")=='block'){
				   backTerm= $('#BACK_TERM').val();
			   }
			   
			   var authData = $.auth.getAuthData();
			   var userInfo = authData.get("USER_INFO");
			   var param = '&SERIAL_NUMBER='+userInfo.get("SERIAL_NUMBER");
			   param += "&PRODUCT_ID="+selectedActives.get(0).get("PRODUCT_ID");
			   param += "&PACKAGE_ID="+selectedActives.get(0).get("PACKAGE_ID");
			   param += "&RELATION_TRADE_ID="+selectedActives.get(0).get("RELATION_TRADE_ID");
			   param += "&CAMPN_TYPE="+selectedActives.get(0).get("CAMPN_TYPE");
			   param += "&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
			   param += "&END_DATE_VALUE="+$("#END_DATE_VALUE").val();
			   param += "&REMARK="+$("#REMARK").val();
			   param += "&IS_RETURN="+$("#IS_RETURN").val();
			   param += "&RETURNFEE="+$("#TRUE_RETURNFEE").val();    
			   param += "&YSRETURNFEE="+$("#RETURNFEE").val();
			   param += "&TRUE_RETURNFEE_COST="+$("#TRUE_RETURNFEE_COST").val(); 
			   param += "&TRUE_RETURNFEE_PRICE="+$("#TRUE_RETURNFEE_PRICE").val(); 
			   param += "&RSRVSTR6="+$("#RSRVSTR6").val();
			   param += "&BACK_TERM="+backTerm;
			   $.cssubmit.addParam(param);
			   $.cssubmit.submitTrade();//提交台账
		}
	}); 
}

$(document).ready(function(){
   $.table.fn.selectedRow = function (c, f){
        var e = $.table.fn.get(c);
	    var b = e.getTable().attr("selected");
	    if (b == null || b == "-1") {
		    //a(f).attr("class", "on");
	    } else {
		    var d = e.getSelected();
		    if (d && d.length) {
			    if (d.attr("raw_class")) {
				    d.attr("class", d.attr("raw_class"));
			    } else {
				    d.attr("class", "");
			    }
		    }
		    //a(f).attr("class", "on");
	    }
	    e.getTable().attr("selected", f.rowIndex);
   };
});
