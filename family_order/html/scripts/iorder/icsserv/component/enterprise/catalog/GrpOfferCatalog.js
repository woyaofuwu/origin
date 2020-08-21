var catalogOfferListCacheData = new Wade.DataMap();

//初始化商品目录
function initOfferCatalog()
{
	if(20 == $("#cond_OPER_TYPE").val())
	{
		$("#OfferSearchDiv").css("display", "");
	}
	else
	{
		$("#OfferSearchDiv").css("display", "none");
	}
	var param = "CUST_ID="+$("#cond_CUST_ID").val()+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&ACTION=INIT_CATALOG";
	$.beginPageLoading("商品目录数据查询中...");
	$.ajax.submit("", "", param, "CatalogPart,OfferListPart", function(data){
		$.endPageLoading();
		if(data != null)
		{
			catalogOfferListCacheData = data;
		}
		$("#offerchalist").val("");
		showPopup("popup02", "offerCatalogPopupItem", true);
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

//选择商品目录，加载商品列表
function chooseOfferCatalog(catalogId)
{
	$("li[name=CATALOG_ID][class='on']").attr("class", "off");
	$("#"+catalogId).attr("class", "on");
	
	var offerList = catalogOfferListCacheData.get(catalogId);
	if(offerList)
	{
		loadOfferListByCacheData(offerList);
	}
	else
	{
		var param = "CATALOG_ID="+catalogId+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&ACTION=LOAD_OFFER_LIST";
		$.beginPageLoading("数据查询中...");
		$.ajax.submit("", "", param, "OfferListPart", function(data){
			$.endPageLoading();
			if(data)
			{
				catalogOfferListCacheData.put(catalogId, data);
			}
		}, 
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}
}

function loadOfferListByCacheData(offerList)
{
	$("#OFFER_LIST_UL").empty(); //删除子元素li
	
	for(var i = 0, size = offerList.length; i < size; i++)
	{
		var offer = offerList.get(i);
		var liHtml = "<li>";
		liHtml += "<div class='main'>";
		liHtml += offer.get("OFFER_NAME");
		if($("#cond_OPER_TYPE").val() != "20")
		{
			liHtml += "<div class='c_space'></div><div class='content'>服务号码：" + offer.get("SERIAL_NUMBER") + "</div>";
		}
		liHtml += "</div>";
		liHtml += "<div class='side'><button class='e_blue e_button-r' OFFER_CODE='"+offer.get("OFFER_CODE")+"' OFFER_NAME='"+offer.get("OFFER_NAME")+"' USER_ID='"+offer.get("USER_ID")+"' ontap='chooseOffer(this);'>选择</button></div>";
		liHtml += "</li>";
		$("#OFFER_LIST_UL").append(liHtml);
	}
}

function chooseOffer(el)
{
	var offerCode = $(el).attr("OFFER_CODE");
	var offerName = $(el).attr("OFFER_NAME");
	var userId = $(el).attr("USER_ID");
	
	if($.isFunction(window["chooseOfferAfterAction"]))
	{
		window["chooseOfferAfterAction"](offerCode, offerName, userId);
	}
	
	hidePopup(el);
}

function chooseOfferFromSearch(offerCode, offerName)
{
	if($.isFunction(window["chooseOfferAfterAction"]))
	{
		window["chooseOfferAfterAction"](offerCode, offerName, "");
	}
	
	hidePopup("popup02");
}
