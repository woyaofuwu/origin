(function($){
	$.extend($.BmFrame,{
		initPage:function()
		{
		    var tradeTable = $.tableManager.get("CancelTradeTable");
		    tradeTable.setAllSelectCheckBoxSts(false);
        },
        showTradeInfo:function(aColName,aId){
        	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
        	params += "&TRADE_ID="+aId;
        	params += "&HIS_FLAG=false";
        	$.popupDialog("broadband.widenet.TradeFrame","initFrame",params,"","800","400","order");
        },
 	    refreshTable:function(){
 	       var tradeTable = $.tableManager.get("CancelTradeTable");
  		   var params = $.ajax.buildPostData("ConditionPart");
  		   params += "&ROUTE_EPARCHY_CODE=0898";
  		   tradeTable.refresh(params);  //同步刷新
  		   tradeTable.setAllSelectCheckBoxSts(false);
  		   if(tradeTable.count() <=1){
  			   alert("查询用户可取消订单无数据!");
  		   }else{
  			   tradeTable.rowSelected(1,true);
  		   }
 	    },
 	    selTrade:function(rowIndex,isSelect){ //勾选或去勾选可取消业务时触发
 	    	debugger;
 	    	if(isSelect){
 	    		$.BmFrame.unselOtherTrade(rowIndex); //去勾选其它可取消业务
 	    		$.beginPageLoading('Loading.....');
 	    		var tradeInfos = $.BmFrame.buildJsonData(false);
 		    	var tradeInfo = tradeInfos.get(0);
 		    	
 		    	var params = "&TRADE_ID="+tradeInfo.get("TRADE_ID")+"&SERIAL_NUMBER="+tradeInfo.get("SERIAL_NUMBER")+"&TRADE_TYPE_CODE="+tradeInfo.get("TRADE_TYPE_CODE");
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

