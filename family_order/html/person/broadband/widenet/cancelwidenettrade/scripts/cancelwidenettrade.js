(function($){
	$.extend($.BmFrame,{
		initPage:function(){
		    $("#queryBtn").bind("click", $.BmFrame.queryUserCancelTrade);
		    var tradeTable = $.tableManager.get("CancelTradeTable");
		    tradeTable.setAllSelectCheckBoxSts(false);
        },
        test:function(aColName,aId){
        	//alert("aaaaaaaaaa===="+aColName+"bbbbbbbbbbbbbb==="+aId);
        	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
        	params += "&TRADE_ID="+aId;
        	params += "&HIS_FLAG=false";
        	$.popupDialog("broadband.widenet.TradeFrame","initFrame",params,"","800","400","personserv");
        },
        queryUserCancelTrade:function(){
 		   debugger;
 		   if(!$.validate.verifyAll()){return false;}
 		   $.BmFrame.setWideAcctType(); //获取并设置宽带账号类型(主账号、子账号、普通账号)
 		   $.BmFrame.refreshTable();//刷新表格获取可取消业务列表
 		   $.BmFrame.clearFeeInfo();
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
		gBmFrame_getAllSubmitPageData:function(){
			var objSubmitData = new $.BmFrame.SubmitData();
	    	//var objNodeInfo = objSubmitData.addNewNodeInfo("","");
	    	var tradeInfos = $.BmFrame.buildJsonData(true);
	    	if(!tradeInfos || tradeInfos.length <= 0){
	    		alert("您没有选择可取消的业务，请选择后提交!");
	    		return false;
	    	}
	    	var tradeInfo = tradeInfos.get(0);
	    	tradeInfo.put("SERIAL_NUMBER",$("#SERIAL_NUMBER").val());
	    	tradeInfo.put("OPER_FEE",$("#backOperFee").val());
	    	tradeInfo.put("FOREGIFT",$("#backForeGift").val());
	    	tradeInfo.put("ADVANCE_PAY",$("#backAdvancePay").val());
	    	tradeInfo.put("REMARK",$("#REMARK").val());
	    	var attrData = new $.BmFrame.AttrData(tradeInfo);
	    	objSubmitData.setAttrData(attrData);
	    	//objNodeInfo.addJSONObjectStr("TRADE_INFO", tradeInfo);
	    	return objSubmitData;
	    },
	    gBmFrame_SuccessFunc:function(data){
	    	$.endPageLoading();
	    	MessageBox.success("成功提示","业务取消成功!",function(btn){
	    		$.redirect.toPage("personserv","broadband.widenet.CancelWidenetTrade","onInitTrade",'');
	    		debugger;
	    	});
	    },
	    buildJsonData:function(isOnlySendModifyData){
	    	var tradeTable = $.tableManager.get("CancelTradeTable");
		    return tradeTable.toJsonArray(false);
	    }
	});
	$($.BmFrame.initPage);  //页面加载完成之后才执行
})(Wade);
