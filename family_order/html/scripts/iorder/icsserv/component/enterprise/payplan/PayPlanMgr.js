//展开信息
function toggle(thisT) {
	if($($(thisT).children()).attr("class")=="e_ico-unfold"){
		$($(thisT).parent().next()).css("display","");
		$($(thisT).children()).removeClass("e_ico-unfold");
		$($(thisT).children()).addClass("e_ico-fold");
	}
	else{
		$($(thisT).parent().next()).css("display","none");
		$($(thisT).children()).removeClass("e_ico-fold");
		$($(thisT).children()).addClass("e_ico-unfold");
	}
}


//查询合同信息
function queryPayPlanInfo()
{
	//校验用户信息和付费计划
	var offerId  = $("#cond_OFFER_CODE").val();
	var subscriberInsId  = $("#cond_SUBSCRIBER_INS_ID").val();
	var param = "&subscriberInsId="+subscriberInsId+"&offerId="+offerId;
	if(!offerId)
	{
		MessageBox.error("错误信息", "请先选择用户商品信息！");
		return false;
	}
	if(!subscriberInsId)
	{
		param="&offerId="+offerId;
	}
	
	if($("cond_VPN_BIZ_TYPE").val() == "2")
	{//取集团商品业务受理界面隐藏域，默认为0
		param += "&ATTR_OBJ='2'";
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "", param, "payPlanPart1", function(data){
		$.endPageLoading();
		
		var payplanValue = $("#payPlanValue").val();
		if(payplanValue)
		{
			var payplanArr = payplanValue.split(",");
			for(var i = 0, size = payplanArr.length; i < size; i++)
			{
				$(":checkbox[checkparamvalue="+payplanArr[i]+"]").attr("checked", true);
			}
		}
		
		showPopup('popup', 'payPlansInfos', true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//查询付费类型for政企一单清
function queryPayPlanInfoForYDQ()
{
	//校验用户信息和付费计划
	var offerCode  = $("#CHILD_EC_OFFER_CODE").val();
//	var subscriberInsId  = $("#cond_SUBSCRIBER_INS_ID").val();
	var param = "&offerId="+offerCode;
	if(!offerCode)
	{
		MessageBox.error("错误信息", "请先选择用户商品信息！");
		return false;
	}
//	if(!subscriberInsId)
//	{
//		param="&offerId="+offerId;
//	}
	
	if($("cond_VPN_BIZ_TYPE").val() == "2")
	{//取集团商品业务受理界面隐藏域，默认为0
		param += "&ATTR_OBJ='2'";
	}
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "", param, "payPlanPart1", function(data){
		$.endPageLoading();
		
		var payplanValue = $("#payPlanValue").val();
		if(payplanValue)
		{
			var payplanArr = payplanValue.split(",");
			for(var i = 0, size = payplanArr.length; i < size; i++)
			{
				$(":checkbox[checkparamvalue="+payplanArr[i]+"]").attr("checked", true);
			}
		}
		
		showPopup('popup', 'payPlansInfos', true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function cancelInfo(){
	hidePopup("popup",'payPlansInfos');
}




function dealInfo(){
	var paramValue = "";
	var paramText = "";
    $("input[name=checkId]:checked").each(function(){
    	 paramValue+=$.attr(this,"checkParamValue")+",";
    	 paramText+=$.attr(this,"checkParamText")+",";
    });
    if(paramValue.length!=0){
    	paramValue =paramValue.substring(0,paramValue.length-1);
    }
    if(paramText!=0){
    	paramText =paramText.substring(0,paramText.length-1);
    }
    $("#payPlanValue").val(paramValue);
    $("#payPlanTexts").html(paramText);
    
    if($.isFunction(window["payplanMgrCallback"]))
	{
		window["payplanMgrCallback"](paramValue);
	}
    
	hidePopup("popup",'payPlansInfos');
}

