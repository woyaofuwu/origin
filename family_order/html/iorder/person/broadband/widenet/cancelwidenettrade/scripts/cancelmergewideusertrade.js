(function($){
	$.extend($.BmFrame,{
		initPage:function()
		{
        },
        queryTradeInfo:function(aColName,aId){
        	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
        	params += "&TRADE_ID="+aId;
        	params += "&HIS_FLAG=false";
        	popupPage("基本信息","broadband.widenet.TradeInfoNew","queryTradeInfo",params,"iorder","c_popup c_popup-half  c_popup-half-hasBg",null,null);
        },
 	    refreshTable:function(){
  		   	var params = $.ajax.buildPostData("ConditionPart");
	    	$.beginPageLoading('Loading.....');
	    	
	    	ajaxSubmit(null,'queryCancelTradeList',params,'QueryListPart',function(rtnData){
	    		if(rtnData!=null&&rtnData.length > 0){
	    			var fanxiBox = $("[name='monitorids']").get(0).click();
					$.endPageLoading();
				}else{
					$.endPageLoading();
					MessageBox.alert("提示","查询用户可取消订单无数据!");
					return false; 
				}
	    	},function(code, info, detail){
	    		$.endPageLoading();
	    		MessageBox.error("错误提示","表格刷新失败！",null, null, info, detail);	
	    	},{async:false});
  		   
 	    },
 	    setWideAcctType:function(){
 	    	$.beginPageLoading('Loading.....');
	    	var params = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
	    	ajaxSubmit(null,"queryWideAcctType",params,"",function(data){
	    		$.endPageLoading();
	    		var isMasterAcct = data.get(0,"isMasterAcct");
	    		$("#acctType_value").html('<span id="acctType" name="acctType">'+isMasterAcct+'</span>');
	    	},function(code, info, detail){
	    		$.endPageLoading();
	    		MessageBox.error("错误提示","处理状态刷新失败！",null, null, info, detail);	
	    	});
 	    },
 	    selTrade:function(obj){ //勾选或去勾选可取消业务时触发
    		var fanxiBox = $("[name='monitorids']");
 	    	if(obj.checked || obj.checked=='checked'){
                fanxiBox.removeAttr("checked");
//              这里需注意jquery1.6以后必须用prop()方法
//              $(obj).prop("checked", true);
                $(obj).attr("checked",true);
 	    		$.beginPageLoading('Loading.....');
 	    		var tradeInfos = $.BmFrame.buildJsonData();
 		    	var tradeInfo = tradeInfos.get(0);
 		    	//tradeInfo.put("SERIAL_NUMBER",$("#SERIAL_NUMBER").val());
 		    	var params = "&TRADE_ID="+tradeInfo.get("TRADE_ID")+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
 		    	ajaxSubmit(null,"queryTradeBackFee",params,"",function(data){
 		    		$.endPageLoading();
 	 		    	$("#backOperFee_value").html('<span id="backOperFee" name="backOperFee">'+data.get(0,"backOperFee")+'</span>');
 	 		    	$("#backForeGift_value").html('<span id="backForeGift" name="backForeGift">'+data.get(0,"backForeGift")+'</span>');
 	 		    	$("#backAdvancePay_value").html('<span id="backAdvancePay" name="backAdvancePay">'+data.get(0,"backAdvancePay")+'</span>');
 	 		    	$("#backFee_value").html('<span id="backFee" name="backFee">'+data.get(0,"backFee")+'</span>');
 	 	    		
 		    	},function(code, info, detail){
 		    		$.endPageLoading();
 		    		MessageBox.error("错误提示","处理状态刷新失败！",null, null, info, detail);	
 		    	});
 	    	}else{
 	    		$.BmFrame.clearFeeInfo();
 	    	}
 	    	
 	    	
 	    },
 	    clearFeeInfo:function(){
	    	$("#backOperFee_value").html('<span id="backOperFee" name="backOperFee">0</span>');
	    	$("#backForeGift_value").html('<span id="backForeGift" name="backForeGift">0</span>');
	    	$("#backAdvancePay_value").html('<span id="backAdvancePay" name="backAdvancePay">0</span>');
	    	$("#backFee_value").html('<span id="backFee" name="backFee">0</span>');
 	    },
	    buildJsonData:function(){
		    return CancelTradeTable.getCheckedRowsData("monitorids");
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


