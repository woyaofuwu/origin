(function($){
	$.extend($.BmFrame,{
		initPage:function(){
		    
        },
        dealChangeDisplay:function(){
        	var dealState = $("#DEAL_STATE").val();
        	if(dealState=="1"){
        		$("#PART_DEAL_DESC").css("display","");
        		$("#REMARK").val("");
        		$("#PART_CANCEL_REASON").css("display","none");
        		$("#CANCEL_REASON").val("");
        	}else if(dealState=="2"){
        		$("#PART_DEAL_DESC").css("display","none");
        		$("#REMARK").val("");
        		$("#PART_CANCEL_REASON").css("display","");
        		$("#CANCEL_REASON").val("");
        	}else{
        		$("#PART_DEAL_DESC").css("display","");
        		$("#REMARK").val("");
        		$("#PART_CANCEL_REASON").css("display","");
        		$("#CANCEL_REASON").val("");
        	}
        },
		gBmFrame_getAllSubmitPageData:function(){
			var objSubmitData = new $.BmFrame.SubmitData();
			var dealState = $("#DEAL_STATE").val();
			var remark = $("#REMARK").val();
			var reasonOne = $("#CANCEL_REASON_ONE").val();
			var reasonTwo = $("#CANCEL_REASON_TWO").val();
			if (null == remark || '' == remark){
				alert("请填写处理描述!");
				return false;
			}else{
				if(remark.length > 10){
					alert("处理描述:最大长度为10");
					return false;
				}
			}
			if(dealState == "2"){
				if(null == reasonOne || '' == reasonOne){
					alert("请选择撤单原因!");
					return false;
				}else if(null == reasonTwo || '' == reasonTwo){
					alert("请选择二级原因!");
					return false;
				}
			}
	    	var tradeInfo = new $.DataMap();
	    	tradeInfo.put("TRADE_ID",$("#TRADE_ID").val());
	    	tradeInfo.put("SERIAL_NUMBER",$("#SERIAL_NUMBER").val());
	    	tradeInfo.put("SUBSCRIBE_TYPE",$("#SUBSCRIBE_TYPE").val());
	    	tradeInfo.put("PROD_SPEC_ID",$("#PROD_SPEC_ID").val());
	    	tradeInfo.put("OPER_FEE",$("#backOperFee").val());
	    	tradeInfo.put("FOREGIFT",$("#backForeGift").val());
	    	tradeInfo.put("ADVANCE_PAY",$("#backAdvancePay").val());
	    	tradeInfo.put("REMARK",$("#REMARK").val());
	    	tradeInfo.put("CANCEL_REASON_ONE",$("#CANCEL_REASON_ONE").val());
	    	tradeInfo.put("CANCEL_REASON_TWO",$("#CANCEL_REASON_TWO").val());
	    	tradeInfo.put("STAFF_PHONE",$("#STAFF_PHONE").val());
	    	tradeInfo.put("STAFF_CITY_CODE",$("#STAFF_CITY_CODE").val());
	    	var attrData = new $.BmFrame.AttrData(tradeInfo);
	    	objSubmitData.setAttrData(attrData);
	    	//objNodeInfo.addJSONObjectStr("TRADE_INFO", tradeInfo);
	    	return objSubmitData;
	    },
	    gBmFrame_SuccessFunc:function(data){
	    	$.endPageLoading();
	    	MessageBox.success("成功提示","业务取消成功!",function(btn){
	    		$.setReturnValue('',true);
	    	});
	    }
	});
	$($.BmFrame.initPage);  //页面加载完成之后才执行
})(Wade);

function changeCancelReason(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('PART_CANCEL_REASON', 'getCancelReasonTwo', null, 'cancelReasonSecond',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}