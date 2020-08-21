//打开资费类商品属性设置
function openPriceChaPopupItem(el, ecMebType)
{
	var offerId = $(el).attr("OFFER_ID");
	$("#SET_PRICE_OFFERCHA_OFFER_ID").val(offerId);
	$("#SET_PRICE_OFFERCHA_ECMEB_TYPE").val(ecMebType);
	
	var offerData = getOfferData(offerId, ecMebType);
	
	var param = "&OFFER_ID="+offerId+"&EC_MEB_TYPE="+ecMebType+"&ACTION=queryPriceOfferChaList";
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "PriceOfferChaListPart", function(data){
		
		var offerChaList = offerData.get("OFFER_CHA_SPECS");
		if(offerChaList && offerChaList.length > 0)
		{
			initPriceOfferChaVal(offerChaList);
		}
		else
		{
			//加载电子合同中的信息
			var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
			var ecOfferId = ecOfferData.get("OFFER_ID");
			var eleOfferData = new Wade.DataMap($("#AGREEMENT_ELEMENT_DATA_"+ecOfferId).text());
			if(eleOfferData && ecMebType =="MEB")
			{
				var ecOfferData =eleOfferData.get("MEB_OFFER");
				
				if(ecOfferData)
				{
					for (var i = 0; i < ecOfferData.length; i++) {
						var offerchaSpecList = ecOfferData[i].get("OFFER_CHA_SPECS");
						
						if(offerchaSpecList && offerchaSpecList.length > 0)
						{
							initPriceOfferChaVal(offerchaSpecList);
						}
						
					}
				}
			}
			else if(eleOfferData && ecMebType =="EC")
			{
				var ecOfferData =eleOfferData.get("EC_OFFER");
				
				if(ecOfferData)
				{
					for (var i = 0; i < ecOfferData.length; i++) {
						var offerchaSpecList = ecOfferData[i].get("OFFER_CHA_SPECS");
					
						if(offerchaSpecList && offerchaSpecList.length > 0)
						{
							initPriceOfferChaVal(offerchaSpecList);
						}
					}
				}
			}
		}
		
		$.endPageLoading();
		showPopup("popup", "PriceChaPopupItem", true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function initPriceOfferChaVal(offerChaList)
{
	for(var i = 0, size = offerChaList.length; i < size; i++)
	{
		var priceOfferCha = offerChaList.get(i);
		var chaSpecCode = priceOfferCha.get("ATTR_CODE");
		var chaValue = priceOfferCha.get("ATTR_VALUE");
		var el = $("div[id=PriceOfferChaListPart] input[name="+chaSpecCode+"]");
		if("checkbox" == el.attr("type"))
		{
			if(chaValue == "1")
			{
				el.attr("checked", true);
			}
			else
			{
				el.attr("checked", false);
			}
		}
		else
		{
			el.val(chaValue);

			/*
            * 多媒体桌面电话-自定义费用套餐设置 ：
            *     资费特征-超出后的国内通信费折扣率 初始化时：
            *        select下拉框值设置成功。但是text未设置，特殊处理;
            */
            if(chaSpecCode == "20000001")
            {
                var pattern = new RegExp("^[5-9]*$");    //  折扣率 只能为 5-10的整数
                if(pattern.test(chaValue))
                {
                    $("#20000001_span span:first").text(chaValue+"折");
                }
                else
                {
                    $("#20000001_span span:first").text("10折");
                    el.val("10");  //  如果是非 5-10 的数字，默认是10；
                }
            }
		}
	}
}

function submitPriceOfferCha()
{ 
	if(!$.validate.verifyAll("PriceOfferChaListPart"))
	{
		return ;
	}

	var offerId = $("#SET_PRICE_OFFERCHA_OFFER_ID").val();
	var ecMebType = $("#SET_PRICE_OFFERCHA_ECMEB_TYPE").val();
	
	var offerChaIsChg = false; // 资费类商品特征是否被修改(针对实例数据)
	
	var priceOfferChaList = new Wade.DatasetList(); //资费类商品特征
	$("div[id=PriceOfferChaListPart] input").each(function(){
		var chaSpecCode = $(this).attr("id");
		var chaValue = $(this).attr("value");
		if($(this).attr("type") == "checkbox")
		{
			chaValue = $(this).attr("checked") ? "1" : "0";
		}
		var chaSpecId = $(this).attr("CHA_SPEC_ID");
		if(!chaSpecId)
		{
			return true; //相当于 continue
		}
		
		var priceOfferChaData = new Wade.DataMap();
		priceOfferChaData.put("ATTR_VALUE", chaValue);
		priceOfferChaData.put("ATTR_CODE", chaSpecCode);
		
		//判断资费类商品特征实例是否变更
		priceOfferChaData.put("OLD_VALUE", $(this).attr("oldValue"));
		if(chaValue != $(this).attr("oldValue"))
		{
			offerChaIsChg = true;
		}
		priceOfferChaList.add(priceOfferChaData);
	});
	
	if(priceOfferChaList.length > 0)
	{//将数据保存到offerData中
		var offerData = getOfferData(offerId, ecMebType);
		
		var funcName = "submitOfferSimpleChaAfterAction_" + offerId;
		if($.isFunction(window[funcName]))
		{
			window[funcName](priceOfferChaList, offerId, offerData);
		}

		if(offerData.get("OPER_CODE") != ACTION_CREATE && offerChaIsChg)
		{//子商品不是新增状态且商品特征发生变更
			offerData.put("OPER_CODE", ACTION_UPDATE);
		}
		offerData.put("OFFER_CHA_SPECS", priceOfferChaList);
		updateOfferData(offerId, ecMebType, offerData);
	}
	
	//隐藏资费类商品特征"待设置"标签
	$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE="+ecMebType+"][OFFER_ID="+offerId+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
	
	//关闭资费类商品特征popup
	backPopup(document.getElementById("PriceChaPopupItem"));
}

function calEndTime(dateTime, monthNum, isChk15)
{
	var currentDate = new Date(dateTime.replace(/\-/g, "\/"));
	
	var day = currentDate.getDate();
	
	currentDate.setMonth(currentDate.getMonth() + parseInt(monthNum));
	currentDate.setDate(1); //取当月第一天
	currentDate.setSeconds(currentDate.getSeconds() - 1);
	
	var fullYear = currentDate.getFullYear();
	var month = currentDate.getMonth() + 1;
	month = month > 9 ? month : "0"+month;
	day = currentDate.getDate() > 9 ? currentDate.getDate() : "0"+currentDate.getDate();
	return fullYear + "-" + month + "-" + day + " " + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
}

function getSystime(){
	
	var now=new Date();
	var year=now.getFullYear();
	var month=now.getMonth() + 1;
	var day=now.getDate();
	var hours=now.getHours();
	var minutes=now.getMinutes();
	var seconds=now.getSeconds(); 
	if (month < 10)
	{
		month = "0" + month;
	}
	if (day < 10)
	{
		day = "0" + day;
	}
	if (hours < 10)
	{
		hours = "0" + hours;
	}
	if (minutes < 10)
	{
		minutes = "0" + minutes;
	}
	if (seconds < 10)
	{
		seconds = "0" + seconds;
	}
	var time = year+"-" +month +"-" + day+" "+hours+":"+minutes+":"+seconds+"";
	return time;

}

//获取下月第一天
function getFirstDayOfNextMonth()
{
	var currentDate = new Date();
	var date = new Date(currentDate.getFullYear(), currentDate.getMonth()+2, 0);
	var fullYear = date.getFullYear();
	var month = date.getMonth() + 1;
	month = month > 9 ? month : "0"+month;
	return fullYear + "-" + month + "-01";
}