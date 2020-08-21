(function($){
	$.extend($.BmFrame,{
		initPage:function()
		{
		    var tradeTable = $.tableManager.get("CancelTradeTable");
		    tradeTable.setAllSelectCheckBoxSts(false);
        },
        test:function(aColName,aId){
        	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
        	params += "&TRADE_ID="+aId;
        	params += "&HIS_FLAG=false";
        	$.popupDialog("imslandline.TradeFrame","initFrame",params,"","800","400","order");
        },
 	    refreshTable:function(){
 	       var tradeTable = $.tableManager.get("CancelTradeTable");
  		   var params = $.ajax.buildPostData("ConditionPart");
  		   tradeTable.refresh(params);  //同步刷新
  		   tradeTable.setAllSelectCheckBoxSts(false);
  		   if(tradeTable.count() <=1){
  			   alert("查询用户可取消订单无数据!");
  		   }else{
  			   tradeTable.rowSelected(1,true);
  		   }
 	    },
 	    setWideAcctType:function(){
 	    	$.beginPageLoading('Loading.....');
	    	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
	    	ajaxSubmit(null,"queryWideAcctType",params,"",function(data){
	    		$.endPageLoading();
	    		var isMasterAcct = data.get(0,"isMasterAcct");
	    		$("#acctType").val(isMasterAcct);
	    	},function(code, info, detail){
	    		$.endPageLoading();
	    		MessageBox.error("错误提示","处理状态刷新失败！",null, null, info, detail);	
	    	});
 	    },
 	    selTrade:function(rowIndex,isSelect){ //勾选或去勾选可取消业务时触发
 	    	debugger;
 	    	if(isSelect){
 	    		$.BmFrame.unselOtherTrade(rowIndex); //去勾选其它可取消业务
 	    		$.beginPageLoading('Loading.....');
 	    		var tradeInfos = $.BmFrame.buildJsonData(false);
 		    	var tradeInfo = tradeInfos.get(0);
 		    	//tradeInfo.put("SERIAL_NUMBER",$("#SERIAL_NUMBER").val());
 		    	var params = "&TRADE_ID="+tradeInfo.get("TRADE_ID")+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
 		    	ajaxSubmit(null,"queryTradeBackFee",params,"",function(data){
 		    		$.endPageLoading();
 		    		$("#backOperFee").val(data.get(0,"backOperFee"));
 	 	    		$("#backForeGift").val(data.get(0,"backForeGift"));
 	 	    		$("#backAdvancePay").val(data.get(0,"backAdvancePay"));
 	 	    		$("#backFee").val(data.get(0,"backFee"));
 		    	},function(code, info, detail){
 		    		$.endPageLoading();
 		    		MessageBox.error("错误提示","处理状态刷新失败！",null, null, info, detail);	
 		    	});
 	    	}else{
 	    		$.BmFrame.clearFeeInfo();
 	    	}
 	    },
 	    clearFeeInfo:function(){
 	    	$("#backOperFee").val("0");
	    	$("#backForeGift").val("0");
	    	$("#backAdvancePay").val("0");
	    	$("#backFee").val("0");
 	    },
 	    unselOtherTrade:function(rowIndex){
 	    	var tradeTable = $.tableManager.get("CancelTradeTable");
 	    	for(var i = 1; i < tradeTable.count(); i++){
 	    		if(tradeTable.isSelected(i) && i != rowIndex){
 	    			tradeTable.rowSelected(i,false);
 	    		}
 	    	}
 	    },
	    buildJsonData:function(isOnlySendModifyData){
	    	var tradeTable = $.tableManager.get("CancelTradeTable");
		    return tradeTable.toJsonArray(false);
	    }
	});
	$($.BmFrame.initPage);  //页面加载完成之后才执行
})(Wade);



/**
 * REQ201609190029_优化家庭宽带装机退单分类内容
 * @author zhuoyingzhi
 * 20161014
 */
function changeSecondLevelType(){
	$.beginPageLoading("载入中......");
	//获取一级
	var cancel_reason=$('#CANCEL_REASON').val();
    $.ajax.submit('cancelReasonPart', 'qrySecondLevelTypeInfo', null, 'cancelReasonSecondLevel',function(data){
    		$.endPageLoading();
    	},
	    function(error_code,error_info,detail){
    		$.endPageLoading();
    		MessageBox.error("错误提示", error_info, null, null, null, detail);
   });
}
