var tmpServiceID="";
//入参：当前商品编码，当前商品归属的顶层商品编码，是否主商品，商品层级
function openEnterpriseOfferPopupItem(offerId, topOfferId, isMainOffer, offerIndex)
{
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
	
	// 取当前区域所属的销售品
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var curOfferIndex = $("#prodDiv_OFFER_INDEX").val();
	if(offerId == topOfferId){
		//设置商品特征规格时不要显示产品定制信息
		$("#proPackagePart").css("display","none");

	}
	
	var merchpOperType = hasMerchOptCode(isMainOffer, offerIndex, topOfferId, offerId);
	if ("-1" == merchpOperType ) {
		return ;
	}
	//获取需要设置的商品对象
	var offerData = getOfferData(topOfferId, offerId, offerIndex);
	if ("BOSG" == PageData.getData($("#class_" + topOfferId)).get("BRAND_CODE"))
	{
        offerData.put("MERCHP_OPER_TYPE",merchpOperType);
	}
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	
	//将商品设置区域开关打开(目的：设置完商品后，必须点确定才能将该区域关闭)
	$("#prodDiv_SWITCH").val("on");
	
	//设置当前设置商品的操作类型
	$("#prodDiv_OFFER_OPER_CODE").val(offerData.get("OPER_CODE"));
	
	//如果重新设置了生效方式，那么也需要重新加载商品设置区域
	var isChangeEffect = $("#IS_CHANGE_EFFECT").val();
	if(curOfferId != offerId || "true"==isChangeEffect)
	{
		$("#IS_CHANGE_EFFECT").val("false");
		
		//将offerId设置到产品区域的隐藏域中
		$("#prodDiv_OFFER_ID").val(offerId);
		$("#prodDiv_TOP_OFFER_ID").val(topOfferId);
		$("#prodDiv_OFFER_INDEX").val(offerIndex);

		if (isMainOffer)
		{
			$("#enterpriseOfferPopupItem").attr("isMainOffer", isMainOffer);
		} 
		else 
		{
			$("#enterpriseOfferPopupItem").removeAttr("isMainOffer");
		}
		
		initEnterpriseOfferPopupItem(offerId, merchpOperType);
	}
	else 
	{
		if(curOfferIndex == offerIndex)
		{
			showPopup('popup', 'enterpriseOfferPopupItem');
		}
		else
		{
			//将offerId设置到产品区域的隐藏域中
			$("#prodDiv_OFFER_ID").val(offerId);
			$("#prodDiv_TOP_OFFER_ID").val(topOfferId);
			$("#prodDiv_OFFER_INDEX").val(offerIndex);

			if (isMainOffer)
			{
				$("#enterpriseOfferPopupItem").attr("isMainOffer", isMainOffer);
			}
			else
			{
				$("#enterpriseOfferPopupItem").removeAttr("isMainOffer");
			}
			
			initEnterpriseOfferPopupItem(offerId, merchpOperType);
		}
	}
}

//入参：主商品
function openEcPackagePopupItem(memOfferId)
{
	if(!isSwitchOff())
	{//如果上一次打开商品设置区域没有提交，则本次不让打开
		return ;
	}
    var offerId = $("#cond_OFFER_ID").val();//集团商品
	//获取需要设置的商品对象
	var offerData = getOfferData(offerId, offerId, "");
	
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	
	//将商品设置区域开关打开(目的：设置完商品后，必须点确定才能将该区域关闭)
	$("#prodDiv_SWITCH").val("on");
	
	var curOfferIndex = $("#prodDiv_OFFER_INDEX").val();
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	if(curOfferId == offerId && curOfferIndex == "1")
	{
		showPopup('popup', 'enterpriseOfferPopupItem');
		return;
	}
	
	$("#prodDiv_OFFER_INDEX").val("1");//主商品设置定制页
	//将offerId设置到产品区域的隐藏域中
	$("#prodDiv_OFFER_ID").val(offerId);
	$("#prodDiv_TOP_OFFER_ID").val(offerId);

	$("#enterpriseOfferPopupItem").attr("isMainOffer", true);
	
	//查询成员商品下元素
	initEnterpriseOfferPopupItem(memOfferId, "");
}

function initEnterpriseOfferPopupItem(offerId, merchpOperType)
{
	var operType = $("#cond_OPER_TYPE").val();
	var isBatch = $("#cond_IS_BATCH").val(); //是否批量
	var ecOfferId = $("#cond_OFFER_ID").val(); //集团商品标识
	//顶层商品编码
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();	
	//当前设置的商品的编码
	var curOfferId = $("#prodDiv_OFFER_ID").val();	
	//顶层商品数据对象
	var offerData = PageData.getData($("#class_"+topOfferId));
	
	var mainOfferBrand = PageData.getData($(".e_SelectOfferPart")).get("BRAND_CODE");

	var merchPuserId = "";
	var tempOfferId = "";
	if(topOfferId!=curOfferId && mainOfferBrand == "BOSG"){
		 var subOffes = offerData.get("SUBOFFERS");
		 if(subOffes){
			for(var i = 0, size = subOffes.length; i < size; i++)
			{
				var subOfferData = subOffes.get(i);
				tempOfferId = subOfferData.get("OFFER_ID");
				if(curOfferId == tempOfferId){
					
					merchPuserId = subOfferData.get("USER_ID");
				}				
					
			}
			 
		 }
		 
	}
	if(merchPuserId == "undefined" || merchPuserId ==undefined){
		
		merchPuserId = "";
	}
	//BBOSS定制信息重进页面，待做后续处理。。。
	var openData = PageData.getData($("#cond_OPEN_TAG"));
	var openTag = "";
	var strId = curOfferId+"";
	if(openData){
		 openTag = openData.get(strId);
	}
	
	var param ="OPEN_TAG="+openTag+"&USER_ID="+merchPuserId+"&OFFER_ID="+offerId+"&EC_OFFER_ID="+ecOfferId+"&OPER_TYPE="+operType+"&MERCHP_OPER_TYPE="+merchpOperType;
	
	var effectNow = $("#cond_EFFECT_NOW").val();//是否立即生效 只对订购有效，1立即、0下月初
	if(effectNow){
		param += "&EFFECT_NOW="+effectNow;
	}
	if(isBatch == "true")
	{
		param += "&IS_BATCH=true";
	}
	
	// bboss成员新增变更，根据选择的集团子商品筛选出对应的成员子商品	
	param += "&BRAND_CODE=" + mainOfferBrand;

	var isMainOffer = $("#enterpriseOfferPopupItem").attr("isMainOffer");
	if(mainOfferBrand == "BOSG" && isMainOffer == "true" && (operType == "CrtMb" || operType == "ChgMb"))
	{
		var bbossSubOfferId = $("#cond_BBOSS_SUB_OFFER_ID").val();
		var ecSubInsId = $("#cond_EC_USER_ID").val();

		param += "&BBOSS_SUB_OFFER_ID="+bbossSubOfferId;
		param += "&BBOSS_EC_USER_ID="+ecSubInsId;
	}
	else if((mainOfferBrand == "ADCG" || mainOfferBrand == "MASG") && (operType == "CrtMb" || operType == "ChgMb"))
	{
		var ecSubInsId = $("#cond_EC_USER_ID").val();
		param += "&EC_USER_ID="+ecSubInsId;
	}
	
	//初始化必选产品规格、定价计划和可选子销售品
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "OfferPart,proPackagePart", function(data){

		if(PageData.getData($(".e_SetSelGroupOfferPart")).length == 0)
		{
			var selectGroupOffer = data.get("SELECT_GROUP_OFFER");
			PageData.setData($(".e_SetSelGroupOfferPart"), selectGroupOffer);
		}
		
		var  offerId = data.get("OFFER_ID");
		var ecOfferId = data.get("EC_OFFER_ID");
		//设置当前商品包含的商品组
		$("#prodDiv_GROUP_IDS").val(data.get("GROUP_IDS"));
		
		//BBOSS定制信息处理
		if(offerId != ecOfferId){
			var useTag = data.get("USE_TAG");
			var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
			var curOfferCode = curOfferData.get("OFFER_CODE");
			if(useTag == "1")
			{		
				var forcePkg = data.get("FORCE_PACKAGES");
				PageData.setData($("#FORCE_PACKAGE_LIST"), forcePkg);
				
				$("#proPackagePart").css("display","");
				var packageMap  = new  Wade.DataMap();

				var userPackages = data.get("USER_PACKAGES");				
				var offerDataInfo = PageData.getData($(".e_SelectOfferPart"));
				
				var oldPackageInfo = offerDataInfo.get("GRP_PACKAGE_INFO");
				if(oldPackageInfo){
					oldPackageInfo.put(curOfferCode+"",userPackages);
					offerDataInfo.put("GRP_PACKAGE_INFO",oldPackageInfo);
				}else{
					
					packageMap.put(curOfferCode+"",userPackages);
					offerDataInfo.put("GRP_PACKAGE_INFO",packageMap);
				}

				
				PageData.setData($(".e_SelectOfferPart"),offerDataInfo);
				
			}
			else
			{
				$("#proPackagePart").css("display","none");
			}
		}
		
		
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		var brand = offerData.get("BRAND_CODE");

		//加载商品设置区域的商品特征
		var offerChaSpecs = offerData.get("OFFER_CHA_SPECS");
		if(offerChaSpecs)
		{
			var offerId = offerData.get("OFFER_ID");
			$("#cha_"+offerId).val(offerChaSpecs);
		}
		
//		// 将当前产品操作类型存入
//		$("#SelectProductSpecPart").attr("MERCHP_OPER_TYPE", merchpOperType);
	
		var subOffers = new Wade.DatasetList();
		if("DLBG" == brand){//是否动力100
			offerKey = "POWER100_PRODUCT_INFO";
			var power100Offers = offerData.get("POWER100_PRODUCT_INFO");
			var dlSubOffers = offerData.get("SUBOFFERS");
			var subMap = initDl100SubOfferDatas(power100Offers, dlSubOffers, data.get("SUB_OFFERS"), data.get("IS_SHOW_ADDOFFER_BTN"));
			var newPowerSubs = subMap.get("POWER100_PRODUCT_INFO");
			var newSubs = subMap.get("SUBOFFERS");

			if(typeof(newPowerSubs) != "undefined" && newPowerSubs.length > 0)
			{
				offerData.put("POWER100_PRODUCT_INFO", newPowerSubs);
				for(var a=0;a<newPowerSubs.length;a++){
					subOffers.add(newPowerSubs.get(a));//将动力100子商品和资费服务合到一起做商品设置初始化
				}
			}else{
				subOffers = new Wade.DatasetList();
			}
			if(typeof(newSubs) != "undefined" && newSubs.length > 0)
			{
				offerData.put("SUBOFFERS", newSubs);
				for(var b=0;b<newSubs.length;b++){
					subOffers.add(newSubs.get(b));//将动力100子商品和资费服务合到一起做商品设置初始化
				}
			}

		}else{
			var offerKey = "SUBOFFERS";
			
			subOffers = offerData.get(offerKey);
			subOffers = initSubOfferDatas(subOffers, data.get("SUB_OFFERS"), brand, data.get("IS_SHOW_ADDOFFER_BTN"));
			if(typeof(subOffers) != "undefined" && subOffers.length > 0)
			{
				offerData.put(offerKey, subOffers);
			}
		}

		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
		
		//加载商品设置区域的资费类子商品
		loadPriceOfferBySubOfferDatas(subOffers);
		
		//加载商品设置区域的服务类子商品
		loadServiceOfferBySubOfferDatas(subOffers);
		
		//加载商品设置区域的产品类子商品
		loadProductOfferBySubOfferDatas(subOffers);
		
		//根据是否配置商品特征，来判断是否显示商品特征配置按钮
		isShowOfferChaSpecTag(data);
		
		//是否显示资费商品特征'待设置'标签
		isShowPriceOfferChaTag(subOffers);
		
		//如果serviceOfferUL中间没有子元素，则将ServiceOfferListPart隐藏
		hideServiceOfferPart();
		
		//如果priceOfferUL中间没有子元素，则将PriceOfferPart隐藏
		hidePriceOfferPart();
		
		hideProductOfferPart();
		
		//根据之前选择的操作类型，加载子商品，以免误操作导致选择的操作类型与实际操作不符，导致考核
		isBBOSSDealSubOffers(isMainOffer,mainOfferBrand);
		
		showPopup('popup', 'enterpriseOfferPopupItem', true);
		$.endPageLoading();
		
	},
	function(error_code,error_info,derror){
		$("#prodDiv_OFFER_ID").val("");
		$("#prodDiv_TOP_OFFER_ID").val("");
		$("#prodDiv_OFFER_INDEX").val("");
		$("#prodDiv_OFFER_OPER_CODE").val("");
		$("#prodDiv_SWITCH").val("off");
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//加载商品设置区域资费商品
function loadPriceOfferBySubOfferDatas(subOffers)
{
	if(typeof(subOffers) == "undefined")
	{
		return ;
	}

	//把页面加载的html先移除调，用数据结构中的data重新生成html
	$("ul[id=priceOfferUL] li").each(function(){
		$(this).remove();
	});

	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	var operTyep = $("#cond_OPER_TYPE").val();
	
	//bboss资费变更时，必选包内的必选元素不可被删除
	var mustSelOfferArr= new Array();
	var brand = $("#cond_EC_BRAND").val();
	if(curOfferId!=topOfferId && brand=="BOSG"&&operTyep=="ChgUs"){		
		var groupIds = $("#prodDiv_GROUP_IDS").val();//当前产品已选商品组
		var curGroupIds = groupIds.split("@");
		var groupOfferDatas = PageData.getData($(".e_SetSelGroupOfferPart"));//当前产品商品组信息
		var mustSelOffer = "";
		if(groupOfferDatas){
			for(var Isize=0; Isize<curGroupIds.length; Isize++){
				var selGroupId =  curGroupIds[Isize];
				var curOfferGroupData = groupOfferDatas.get(selGroupId);
				if(curOfferGroupData){
					mustSelOffer = mustSelOffer+"@"+curOfferGroupData.get("MUST_SEL_OFFER");
				}	
			}
	
		}
		if(mustSelOffer!="undefined"&&mustSelOffer!=""){
			mustSelOfferArr = mustSelOffer.split("@");
		}
	}


	var priceOfferList = getSubOffersByOfferType(subOffers, "D");
	
	
	for(var i = 0, sizeI = priceOfferList.length; i < sizeI; i++)
	{
		var priceOffer = priceOfferList.get(i);
		var offer_id = priceOffer.get("OFFER_ID");
		var offer_code = priceOffer.get("OFFER_CODE");
		var offer_name = priceOffer.get("OFFER_NAME");
		var validDate = priceOffer.get("START_DATE");
		var expireDate = priceOffer.get("END_DATE");
		var operCode = priceOffer.get("OPER_CODE");
		var hasPriceCha = priceOffer.get("HAS_PRICE_CHA");
		var repeatOrder = priceOffer.get("REPEAT_ORDER"); //是否可重复订购标记
		var forceTag = priceOffer.get("FORCE_TAG"); //标识商品构成关系或者必选包的必选元素
		var isNeedChange = priceOffer.get("IS_DISCOUNT_TIME_CHANGE");
		//bboss资费变更时，必选包内的必选元素不可被删除
		if("BOSG" == brand && operTyep=="ChgUs"){
			for(var j=0; j<mustSelOfferArr.length; j++){
				if(offer_id == mustSelOfferArr[j] && mustSelOfferArr[j]!=""){
					forceTag = "true";
				}
			}
		}

		var renewTag = priceOffer.get("RENEW_TAG"); //续订标记
		var pOfferIndex = priceOffer.get("P_OFFER_INDEX"); 
		var description = priceOffer.get("DESCRIPTION");
		var isExist = false; //标识商品设置区域是否存在相同的资费商品

		if(operCode == ACTION_DELETE)
		{
			//如果是已经删除的实例，则做为已删除数据展示
			movePriceOffer2DelList(priceOffer);			
			continue;
		}
		
//		if(!isExist)
//		{
			if(hasPriceCha == "true")
			{
				
				var groupOfferHtml = "<li class='link' id='li_"+offer_id+"_"+pOfferIndex+"' offerIndex='"+pOfferIndex+"'><div class='main'>"
				+ "<div class='title'>" 
				+ "【" + offer_code + "】<span class='e_blue' tip='"+description+"'>【详情】</span>" 
				+ "<input type='hidden' name='pp_PRICE_PLAN_ID' value='" + offer_id + "'/>"
				+ "<input type='hidden' name='pcha_" + offer_id + "_"+pOfferIndex+"' id='pcha_" + offer_id + "_"+pOfferIndex+"' value=''/>"
				+ "</div>"
				+ "<div class='content content-auto'>"
				+ offer_name + "<br/>"
				+ "生效时间：<span id='valid_"+offer_id+"_"+pOfferIndex+"' >" + validDate.substring(0, 10) +"</span>"
				
				if(operCode == ACTION_CREATE&&isNeedChange=='1')
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='BEGIN'>[修改]</a>";
				}
				
				groupOfferHtml = groupOfferHtml +"<br/>";
				groupOfferHtml = groupOfferHtml + "失效时间：<span id='expire_"+offer_id+"_"+pOfferIndex+"' >" + expireDate.substring(0, 10) +"</span>";
				if(operCode == ACTION_CREATE)
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='CANCEL'>[修改]</a>";
				}
				groupOfferHtml = groupOfferHtml + "</div>"
				+ "</div>"
				+ "<div class='side' tag='0' OFFER_ID='" + offer_id + "' ontap='priceOfferCha.initPriceOfferCha(this);'><span class='e_tag e_tag-red'>待设置</span></div>"
				+ "<div class='more'></div>";
				
				if(repeatOrder == "true" || renewTag == "true")
				{
					groupOfferHtml += "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
					
					if(renewTag == "true")
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' RENEW_TAG='true' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
					else
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
				}
				else
				{
					if(forceTag != "true")
					{//不是构成关系或者必选包的必选元素
						groupOfferHtml += "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
					}
					else
					{
						groupOfferHtml += "<div class='fn'><span class='fn e_dis e_ico-delete' ></span></div>";
					}
					
				}
				groupOfferHtml += "</li>";
				$("#priceOfferUL").append(groupOfferHtml);
			}
			else
			{
				var groupOfferHtml = "<li id='li_"+offer_id+"_"+pOfferIndex+"' offerIndex='"+pOfferIndex+"'><div class='main'>"
				+ "<div class='title'>" 
				+ "【" + offer_code + "】<span class='e_blue' tip='"+description+"'>【详情】</span>" 
				+ "<input type='hidden' name='pp_PRICE_PLAN_ID' value='" + offer_id + "'/>"
				+ "<input type='hidden' name='pcha_" + offer_id + "' id='pcha_" + offer_id + "' value=''/>"
				+ "</div>"
				+ "<div class='content content-auto'>"
				+ offer_name + "<br/>"
				+ "生效时间：<span id='valid_"+offer_id+"_"+pOfferIndex+"' >" + validDate.substring(0, 10) +"</span>"
				
				if(operCode == ACTION_CREATE&&isNeedChange=='1')
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='BEGIN'>[修改]</a>";
				}
				
				groupOfferHtml  = groupOfferHtml +"<br/>";
				
				groupOfferHtml = groupOfferHtml + "失效时间：<span id='expire_"+offer_id+"_"+pOfferIndex+"' >" + expireDate.substring(0, 10) +"</span>";
				if(operCode == ACTION_CREATE)
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='CANCEL'>[修改]</a>";
				}
				groupOfferHtml = groupOfferHtml + "</div>"
				+ "</div>";
				
				if(repeatOrder == "true" || renewTag == "true")
				{
					groupOfferHtml += "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
					
					if(renewTag == "true")
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' RENEW_TAG='true' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
					else
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
				}
				else
				{
					if(forceTag != "true")
					{//不是构成关系或者必选包的必选元素
						groupOfferHtml += "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
					}
					else
					{
						groupOfferHtml += "<div class='fn'><span class='fn e_dis e_ico-delete' ></span></div>";
					}
					
				}
				groupOfferHtml += "</li>";
				$("#priceOfferUL").append(groupOfferHtml);
			}

			var priceChas = priceOffer.get("OFFER_CHA_SPECS");
			if(typeof(priceChas) != "undefined")
			{
				$("#pcha_"+offer_id+"_"+pOfferIndex).val(priceChas);
				
				priceOfferCha.hidePriceOfferChaTag(offer_id, pOfferIndex);
			}
//		}
	}
}

//加载商品设置区域的服务类子商品
function loadServiceOfferBySubOfferDatas(subOffers)
{
	if(typeof(subOffers) == "undefined")
	{
		return ;
	}
	
	var serviceOfferList = getSubOffersByOfferType(subOffers, "S");
	
	var offerObj = $("input[name=selServiceOffer]");
	for(var i = 0, sizeI = serviceOfferList.length; i < sizeI; i++)
	{
		var serivceOfferData = serviceOfferList.get(i);
		var serivceOfferId = serivceOfferData.get("OFFER_ID");
		var serivceOfferName = serivceOfferData.get("OFFER_NAME");
		var operCode = serivceOfferData.get("OPER_CODE");
		var isExist = false;
		for(var j = 0, sizeJ = offerObj.length; j < sizeJ; j++)
		{
			if(serivceOfferId == offerObj[j].value)
			{
				isExist = true;
				break;
			}
		}
		
		if(operCode == ACTION_DELETE)
		{//如果是已经删除的实例，则做为已删除数据展示
			moveServiceOffer2DelList(serivceOfferData);	
			continue;
		}
		
		if(!isExist)
		{
			var serviceOfferHtml = "<li id='li_"+serivceOfferId+"'><div class='main'>"
				+ "<div class='title'>"
				+ serivceOfferName
				+ "</div>"
				+ "</div>"
				+ "<div class='fn'>"
				+ "<span class='fn e_ico-delete' ontap='deleteServiceOffer(this);'></span>"
				+ "</div>"
				+ "<span value='' id='SUBOFFER_DATA_"
				+ serivceOfferId
				+ "' class='e_SubOfferPart' desc='子商品数据结构' style='display:none' name='SUBOFFER_DATA'>"
				+ serivceOfferData.toString()
				+ "</span>"
				+ "<input type='hidden' name='selServiceOffer' id='selServiceOffer_" + serivceOfferId + "' value='" + serivceOfferId +"'>"
				+ "<div class='c_line'></div></li>";
			$("#serviceOfferUL").append(serviceOfferHtml);
			
		}
	}
}

//加载商品设置区域的产品类子商品
function loadProductOfferBySubOfferDatas(subOffers)
{
	if(typeof(subOffers) == "undefined")
	{
		return ;
	}
	
	var productOfferList = getSubOffersByOfferType(subOffers, "P");
	
	var offerObj = $("input[name=selProductOffer]");
	
	var brand = $("#cond_EC_BRAND").val();
	var operType = $("#cond_OPER_TYPE").val();
	if("BOSG" == brand && operType == "DstUs"){		        
		$("input[name=selProductOffer]").attr("disabled","true");			
	}
	
	for(var i = 0, sizeI = productOfferList.length; i < sizeI; i++)
	{
		var productOfferData = productOfferList.get(i);
		var productOfferId = productOfferData.get("OFFER_ID");
		var productOfferIndex = productOfferData.get("OFFER_INDEX");
		for(var j = 0, sizeJ = offerObj.length; j < sizeJ; j++)
		{
			var selObjId = offerObj[j].getAttribute("id");
			var offerObjId = selObjId.split("_");
			if(productOfferId == offerObjId[0] && productOfferIndex == offerObjId[1])
			{
				$("#"+selObjId).attr("checked", true);
				break;
			}
		}
	}
	
}

//勾选子商品事件
function checkedSubOffer(obj)
{
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var brand = offerData.get("BRAND_CODE");
	var key = "SUBOFFERS";//处理子商品信息
	if("DLBG" == brand){
		key = "POWER100_PRODUCT_INFO";//动力100子商品放在这个结构里
	}

	var optOfferDataset = offerData.get(key);
	if(typeof(optOfferDataset) == "undefined" || optOfferDataset.length == 0)
	{
		optOfferDataset = new Wade.DatasetList();
		offerData.put(key, optOfferDataset);
	}
	if(obj.checked)
	{
		var obj_offerId = obj.getAttribute("OFFER_ID");
		var addData = true;
		for(var i = 0; i < optOfferDataset.length; i++)
		{
			var optOfferData = optOfferDataset.get(i);
			var optOfferInsId = optOfferData.get("OFFER_ID");
			if(optOfferData.get("OFFER_ID") == obj_offerId)
			{
				addData = false;
				if(optOfferData.get("OPER_CODE") == ACTION_DELETE && typeof(optOfferInsId) != "undefined")
				{//已删除的商品实例数据
					optOfferData.put("OPER_CODE", ACTION_EXITS); // 3-不变
//					optOfferData = recoverSubOfferChild(optOfferData);
				}
			}
		}
		if(addData)
		{
			var optOfferData = PageData.getData($("#SUBOFFER_DATA_"+obj_offerId));
			optOfferData.put("OFFER_INDEX", "0");
			optOfferData.removeKey("SELECT_FLAG");
			optOfferDataset.add(optOfferData);
			
		}
		//保存商品数据
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	}
	else
	{
		var obj_offerId = obj.getAttribute("OFFER_ID");
		for(var i = 0; i < optOfferDataset.length; i++)
		{
			var optOfferData = optOfferDataset.get(i);
			var optOfferInsId = optOfferData.get("OFFER_INS_ID");
			if(optOfferData.get("OFFER_ID") == obj_offerId)
			{
				if(optOfferData.get("OPER_CODE") != ACTION_DELETE && typeof(optOfferInsId) != "undefined")
				{//已存在的商品实例数据
					optOfferData.put("OPER_CODE", ACTION_DELETE); // 1-删除
//					optOfferData = deleteSubOfferChild(optOfferData);
				}
				else if(typeof(optOfferInsId) == "undefined")
				{//非实例数据，直接删除
					optOfferDataset.remove(optOfferData);
				}
			}
		}
		//保存商品数据
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	}
}

//删除资费商品
function deletePriceOffer(el)
{
	var brand = $("#cond_EC_BRAND").val();
	var operType = $("#cond_OPER_TYPE").val();
	
	if("BOSG" == brand && operType == "DstUs"){		        
		MessageBox.alert("该产品操作下不允许操作资费！");
        return;				
	}
	
	if ("BOSG" == brand && operType == "ChgUs"){
		var operType = $("#OPERTYPE").val();
		if(operType=="" || operType==undefined){					
            MessageBox.alert("请先设置商品特征！");
            return;
		}
		if(operType == "9" || operType == "3" || operType == "4"){
            MessageBox.alert("该产品操作下不允许操作资费！");
            return;
		}				
        var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
        var oprCode = curOfferData.get("OPER_CODE");
        if (ACTION_PREDESTROY == oprCode || ACTION_PREDSTBACK == oprCode){
            MessageBox.alert("该产品操作下不允许操作资费！");
            return;
		}
	}
	$li = $(el).closest("li");
	var priceOfferId = $li.attr("id").split("_")[1];
	var pOfferIndex = $li.attr("offerIndex");
	
	var key = "SUBOFFERS";//处理子商品信息

	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOffers = offerData.get(key);
	
	var priceOfferLength = $("li[id^=li_"+priceOfferId+"]").length;
	var expireYearMonth = $("#expire_"+priceOfferId+"_"+pOfferIndex).text().substring(0, 8);
	var currentYearMonth = getNowSysTime().substring(0,8);
	if(subOffers)
	{
		for(var i = 0, size = subOffers.length; i < size; i++)
		{
			var priceOfferData = subOffers.get(i);
			if(priceOfferData.get("OFFER_ID") == priceOfferId && priceOfferData.get("P_OFFER_INDEX") == pOfferIndex)
			{
				if(priceOfferLength == 1 && priceOfferData.get("FORCE_TAG") == "true")
				{//如果当前资费类商品只有一条，且当前商品是商品构成关系或者必选组的必选商品
					MessageBox.alert("提示信息", "当前资费类商品是必选商品，不能删除！");
					return false;
				}
				if(priceOfferData.get("OPER_CODE") != ACTION_CREATE && expireYearMonth == getNowSysTime().substring(0,8) && priceOfferId != "130092303004") 
				{//如果当前商品是已订购实例，且资费商品当月失效
					MessageBox.alert("提示信息", "当前资费类商品本月失效，不能删除！");
					return false;
				}

				if(priceOfferData.get("OPER_CODE") != ACTION_CREATE && "CANCEL_MODE4"==priceOfferData.get("CANCEL_END_DATE"))
				{//cancel_mode==4的情况，不到期不可删除
					var expireDate = priceOfferData.get("END_DATE");
					MessageBox.alert("提示信息", "当前资费将在"+expireDate+"到期结束，现在无法取消该优惠！");
					return false;
				}
			}
			
		}
	}
	
	MessageBox.confirm("提示信息", "是否删除当前资费类商品？", function(btn){
		if(btn == "cancel")
		{
			return;
		}
		priceOfferId = $li.attr("id").split("_")[1];
		offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		subOffers = offerData.get(key);
		pOfferIndex = $li.attr("offerIndex");
		if(typeof(subOffers) == "undefined" || subOffers.length == 0)
		{
			$("#li_"+priceOfferId+"_"+pOfferIndex).remove();
			return ;
		}
		
		for(var i = 0, size = subOffers.length; i < size; i++)
		{
			var priceOfferData = subOffers.get(i);
			if(priceOfferData.get("OFFER_ID") == priceOfferId && priceOfferData.get("OFFER_TYPE") == "D" && priceOfferData.get("P_OFFER_INDEX") == pOfferIndex)
			{
				var operCode = priceOfferData.get("OPER_CODE");
				if(operCode == ACTION_CREATE)
				{//不是实例数据，直接删除
					subOffers.remove(priceOfferData);
					$("#li_"+priceOfferId+"_"+pOfferIndex).remove();   
					break;
				}
				else
				{//实例数据，设置子商品数据对象OPER_CODE=1
					
					priceOfferData.put("OPER_CODE", ACTION_DELETE); //1-删除
					
					var expireDate = priceOfferData.get("END_DATE");
					var cancelExpireDate = priceOfferData.get("CANCEL_END_DATE");
					
					priceOfferData.put("OLD_END_DATE", expireDate); 
					priceOfferData.put("END_DATE", cancelExpireDate);
					
					var priceSubOffers = priceOfferData.get("SUBOFFERS");
					if(typeof(priceSubOffers) != "undefined")
					{
						for(var j = 0, sizeJ = priceSubOffers.length; j < sizeJ; j++)
						{
							var priceSubOfferData = priceSubOffers.get(j);
							priceSubOfferData.put("OPER_CODE", ACTION_DELETE); //1-删除
						}
						
					}
					$("#li_"+priceOfferId+"_"+pOfferIndex).remove();
					//将删除的资费商品移到删除列表中
					movePriceOffer2DelList(priceOfferData);
					
					break;
				}
			}
		}
		
		if($("li[id^=li_"+priceOfferId+"]").length == 0)
		{//不存在id以li_XXX开头的li元素，说明资费类商品id为XXX的商品已全部删除  或者  删除的是续订的资费(只能续订一次)
			deleteSelGroupOfferData(priceOfferId);
			
			//同步将商品组内选中的商品
			if($("#grpOffer_"+priceOfferId).length > 0)
			{
				$("#grpOffer_"+priceOfferId).attr("checked", false);
				$("#grpOffer_"+priceOfferId).attr("disabled", false);
			}
		}
		
		if($(el).parent().next().children().attr("RENEW_TAG") == "true")
		{
			if($("#priceOfferUL").find("li[id^=li_"+priceOfferId+"]").length == 1)
			{
				//同步将商品组内选中的商品
				if($("#grpOffer_"+priceOfferId).length > 0)
				{
					$("#grpOffer_"+priceOfferId).attr("checked", false);
					$("#grpOffer_"+priceOfferId).attr("disabled", false);
				}
			}
		}
		
		
		
		hidePriceOfferPart();
		
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	});
	
}

//删除服务商品
function deleteServiceOffer(el)
{
	$li = $(el).closest("li");
	tmpServiceID = $li.attr("id").split("_")[1];
	MessageBox.confirm("提示信息", "是否删除当前服务类商品？", function(btn){
		if(btn == "cancel")
		{
			return;
		}
		
		var  serviceOfferId =tmpServiceID;
		var key = "SUBOFFERS";//处理子商品信息
		
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		var subOffers = offerData.get(key);
		if(typeof(subOffers) == "undefined" || subOffers.length == 0)
		{
			$("#li_"+serviceOfferId).remove();
			return ;
		}
		
		for(var i = 0, size = subOffers.length; i < size; i++)
		{
			var serviceOfferData = subOffers.get(i);
			if(serviceOfferData.get("OFFER_ID") == serviceOfferId && serviceOfferData.get("OFFER_TYPE") == "S")
			{
				var offerInsId = serviceOfferData.get("OFFER_INS_ID");
				if(typeof(offerInsId) == "undefined" || offerInsId == null || offerInsId == "")
				{//不是实例数据，直接删除
					subOffers.remove(serviceOfferData);
					$("#li_"+serviceOfferId).remove();   
					break;
				}
				else
				{//实例数据，设置子商品数据对象OPER_CODE=1
					serviceOfferData.put("OPER_CODE", ACTION_DELETE); //1-删除
					var canCelEndDate = serviceOfferData.get("CANCEL_END_DATE");
					if(canCelEndDate!=null&&canCelEndDate!=""){
						serviceOfferData.put("END_DATE",canCelEndDate);
					}
					var serviceSubOffers = serviceOfferData.get("SUBOFFERS");
					if(typeof(serviceSubOffers) != "undefined")
					{
						for(var j = 0, sizeJ = serviceSubOffers.length; j < sizeJ; j++)
						{
							var serviceSubOfferData = serviceSubOffers.get(j);
							serviceSubOfferData.put("OPER_CODE", ACTION_DELETE); //1-删除
						}
						
					}
					$("#li_"+serviceOfferId).remove();
					//将删除的资费商品移到删除列表中
					moveServiceOffer2DelList(serviceOfferData);
					
					break;
				}
			}
		}
		
		//同步取消商品组内对应商品的选中状态
		if($("#grpOffer_"+serviceOfferId).length > 0)
		{
			$("#grpOffer_"+serviceOfferId).attr("checked", false);
			$("#grpOffer_"+serviceOfferId).attr("disabled", false);
		}
		
		deleteSelGroupOfferData(serviceOfferId);
		
		hideServiceOfferPart();
		
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	});
}

//将删除的子商品从已选择的组商品数据结构中删除
function deleteSelGroupOfferData(offerId)
{
	var delGroupId = "";
	var groupOfferData = PageData.getData($(".e_SetSelGroupOfferPart"));
	groupOfferData.each(function(group){
		var selOffers = group.get("SEL_OFFER", new Wade.DatasetList());
		for(var i = selOffers.length; i > 0; i--)
		{
			var selOffer = selOffers.get(i-1);
			if(offerId == selOffer.get("OFFER_ID"))
			{
				selOffers.remove(selOffer);
				delGroupId = group.get("GROUP_ID");
				break;
			}
		}
	});
	if(delGroupId != "")
	{
		var groupInfo = groupOfferData.get(delGroupId);
		if(groupInfo.get("SEL_OFFER").length == 0 && groupInfo.get("SELECT_FLAG") != "0")
		{//当组内选择的商品个数等于0且商品组不是必选，则将商品组删除
			groupOfferData.removeKey(delGroupId);
		}
		PageData.setData($(".e_SetSelGroupOfferPart"), groupOfferData);
	}
}

//重复订购叠加包资费(及资费续订)
function repeatOrderPriceOffer(el)
{
	var priceOfferId = $(el).attr("OFFER_ID");
	var pOfferIndex = $(el).closest("li").attr("offerIndex");
	
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOfferDataset = offerData.get("SUBOFFERS");
	if(!subOfferDataset)
	{
		return ;
	}
	
	if($(el).attr("RENEW_TAG") == "true")
	{
//		if($("#priceOfferUL .e_ico-add").attr("OFFER_ID", priceOfferId).attr("RENEW_TAG", "true").length > 1)
		if($("#priceOfferUL").find("li[id^=li_"+priceOfferId+"]").length > 1)
		{
			MessageBox.alert("提示信息", "当前资费类商品只能续订一次！");
			return false;
		}
	}
	
	var priceOfferRepeat = new Wade.DataMap();
	for(var i = 0, size = subOfferDataset.length; i < size; i++)
	{
		var subOfferData = subOfferDataset.get(i);
		if(priceOfferId == subOfferData.get("OFFER_ID") && pOfferIndex == subOfferData.get("P_OFFER_INDEX"))
		{//复制新对象
			priceOfferRepeat.put("OFFER_ID", subOfferData.get("OFFER_ID"));
			priceOfferRepeat.put("OFFER_NAME", subOfferData.get("OFFER_NAME"));
			priceOfferRepeat.put("IS_SHOW_SET_TAG", subOfferData.get("IS_SHOW_SET_TAG"));
			priceOfferRepeat.put("FORCE_TAG", subOfferData.get("FORCE_TAG"));
			priceOfferRepeat.put("BRAND_CODE", subOfferData.get("BRAND_CODE"));
			priceOfferRepeat.put("OFFER_TYPE", subOfferData.get("OFFER_TYPE"));
			priceOfferRepeat.put("GROUP_ID", subOfferData.get("GROUP_ID"));
			priceOfferRepeat.put("HAS_PRICE_CHA", subOfferData.get("HAS_PRICE_CHA"));
			priceOfferRepeat.put("REPEAT_ORDER", subOfferData.get("REPEAT_ORDER"));
			priceOfferRepeat.put("RENEW_TAG", subOfferData.get("RENEW_TAG"));
			priceOfferRepeat.put("DESCRIPTION", subOfferData.get("DESCRIPTION"));
			priceOfferRepeat.put("OFFER_INDEX", subOfferData.get("OFFER_INDEX"));
			
			if(subOfferData.get("OPER_CODE") == ACTION_CREATE)
			{
				priceOfferRepeat.put("START_DATE", subOfferData.get("START_DATE"));
				priceOfferRepeat.put("END_DATE", subOfferData.get("END_DATE"));
			}
			else
			{
				priceOfferRepeat.put("START_DATE", subOfferData.get("REPEAT_START_DATE"));
				priceOfferRepeat.put("END_DATE", subOfferData.get("REPEAT_END_DATE"));
			}
			
			//处理组内选择元素列表
			if($(el).attr("RENEW_TAG") == "true")
			{
//				$("#class_SELECT_GROUP_OFFER").val();
				if($("#class_SELECT_GROUP_OFFER").val())
				{
					var selGroupOfferData = new Wade.DataMap($("#class_SELECT_GROUP_OFFER").val());
					var data = selGroupOfferData.get(subOfferData.get("GROUP_ID"));
					if(data)
					{
						var selOfferList = data.get("SEL_OFFER", new Wade.DatasetList());
						var flag = true;
						for(var j = 0, sizeJ = selOfferList.length; j < sizeJ; j++)
						{
							if(selOfferList.get(j).get("OFFER_ID") == subOfferData.get("OFFER_ID"))
							{
								flag = false;
								break;
							}
						}
						if(flag)
						{
							var selOffer = new Wade.DataMap();
							selOffer.put("OFFER_ID", subOfferData.get("OFFER_ID"));
							selOffer.put("OFFER_TYPE", subOfferData.get("OFFER_TYPE"));
							selOffer.put("SELECT_FLAG", "2"); //默认2，不存在必选元素
							selOfferList.add(selOffer);
							data.put("SEL_OFFER",selOfferList);
						}
					}
					$("#class_SELECT_GROUP_OFFER").val(selGroupOfferData.toString());
				}
				
			}
			break;
		}
	}
	if(!priceOfferRepeat)
	{
		return ;
	}
	//将数据结构处理成新增状态
	priceOfferRepeat.put("P_OFFER_INDEX", getMaxPOfferIndex());
	priceOfferRepeat.put("OPER_CODE", ACTION_CREATE);
	
	subOfferDataset.add(priceOfferRepeat);
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	
	//生成html
	enterpriseGroupOffers.addPriceOfferHtml(priceOfferRepeat, priceOfferRepeat.get("START_DATE"), priceOfferRepeat.get("END_DATE"), ACTION_CREATE);
}

//将删除的资费商品移到删除列表中
function movePriceOffer2DelList(priceOfferData)
{
	var expireDate = priceOfferData.get("END_DATE");
	
	var offerId = priceOfferData.get("OFFER_ID");
	var offerCode = priceOfferData.get("OFFER_CODE");
	var offerName = priceOfferData.get("OFFER_NAME");
	var validDate = priceOfferData.get("START_DATE");
	var delPriceOfferHtml = "<li id='del_"+offerId+"'><div class='main e_delete'>";
	delPriceOfferHtml += "<div class='title'>【" + offerCode + "】</div>";
	delPriceOfferHtml += "<div class='content content-auto'>" + offerName;
	delPriceOfferHtml += "<br>生效时间：" + validDate.substring(0, 10);
	delPriceOfferHtml += "<br>失效时间：" + expireDate.substring(0, 10) + "</div>";
	delPriceOfferHtml += "</div>";
	delPriceOfferHtml += "<div class='fn'>";
	delPriceOfferHtml += "<span class='e_ico-delete e_dis'></span>";
	delPriceOfferHtml += "</div></li>";
	
	$("#delPriceOffer").append(delPriceOfferHtml);
	if($("#PriceDelOfferPart").css("display") == "none")
	{
		$("#PriceDelOfferPart").css("display", "");
		
	}
}

//将删除的服务类商品移到删除列表中
function moveServiceOffer2DelList(serviceOfferData)
{
	var offerId = serviceOfferData.get("OFFER_ID");
	var offerName = serviceOfferData.get("OFFER_NAME");
	var delServiceOfferHtml = "<li id='del_"+offerId+"'><div class='main e_delete'>";
	delServiceOfferHtml += "<div class='title'>" + offerName + "</div>";
	delServiceOfferHtml += "</div>";
	delServiceOfferHtml += "<div class='fn'>";
	delServiceOfferHtml += "<span class='e_ico-delete e_dis'></span>";
	delServiceOfferHtml += "</div></li>";
	
	$("#delServiceOffer").append(delServiceOfferHtml);
	if($("#ServiceDelOfferListPart").css("display") == "none")
	{
		$("#ServiceDelOfferListPart").css("display", "");
		
	}
}

//初始化子商品对象(入参：页面上子商品对象，商品构成关系中的子商品列表，是否显示新增子商品按钮)
function initSubOfferDatas(subOffers, optionalOffers, brand, isShowAddOfferBtn)
{
	if(typeof(subOffers) == "undefined")
	{
		subOffers = new Wade.DatasetList();
	}
	//将必选子销售品设置为选中状态且不可编辑，同时将subOffers没有 且已选中状态的子商品放到subOffers中，OPER_CODE=0
	if(optionalOffers.length != 0)
	{
		subOffers = this.setSubOfferData(subOffers, optionalOffers);
		
		var productOfferList = getSubOffersByOfferType(subOffers, "P");
		//bboss产品重复订购处理
		if(brand == "BOSG"){
			//将可以重复订购的产品类商品id放到map中
			var repeatOrderOfferIdData = new Wade.DataMap();
			$("#productOfferUL li").each(function(){
				var offerId = $(this).attr("offerId");
				if($(this).attr("repeatOrder") == "true" && !repeatOrderOfferIdData.containsKey(offerId))
				{
					repeatOrderOfferIdData.put(offerId, true);
				}
			});
			
			if(repeatOrderOfferIdData.length > 0 && productOfferList.length > 0)
			{
				loadRepeatProductOffer(productOfferList, repeatOrderOfferIdData);
			}
			
			var operType = $("#cond_OPER_TYPE").val();

			for(var i = 0, size = optionalOffers.length; i < size; i++)
			{
				var offerObj = optionalOffers.get(i);
				var opOfferId = offerObj.get("OFFER_ID");
				var isSelect = false;
				//必选子商品默认选中
				if(offerObj.get("SELECT_FLAG") == "0")
				{
					$("#"+opOfferId + "_" + offerObj.get("OFFER_INDEX")).attr("checked" , true);
					$("#"+opOfferId + "_" + offerObj.get("OFFER_INDEX")).attr("disabled", true);
					isSelect = true;
				}
				//非必选子商品暂不作处理。。。
				else if(offerObj.get("SELECT_FLAG") == "1" && (operType == "CrtUs" || operType == "CrtMb") )
				{
					//$("#"+opOfferId + "_" + offerObj.get("OFFER_INDEX")).attr("checked" , true);
					//isSelect = true;
				}


				var hasOpOffer = false;
				for(var j = 0; j < subOffers.length; j++)
				{
					var subOfferData = subOffers.get(j);
					if(subOfferData.get("OFFER_ID") == opOfferId)
					{
						hasOpOffer = true;
					}
				}
				if(isSelect && !hasOpOffer)
				{//子商品状态为选中，且子商品对象列表中没有当前商品
//					subOfferData = PageData.getData($("#SUBOFFER_DATA_"+offerObj.get("OFFER_ID")));
					var offerIndex = offerObj.get("OFFER_INDEX");
					if(typeof(offerIndex) == "undefined"){
						offerObj.put("OFFER_INDEX", "0");
					}else{
						offerObj.put("OFFER_INDEX", offerIndex);
					}
					offerObj.removeKey("SELECT_FLAG");
					subOffers.add(offerObj);
				}
			}
			
		}else{
			var operType = $("#cond_OPER_TYPE").val();
			$("#OptionalOfferPart").css("display", "");
			for(var i = 0, size = optionalOffers.length; i < size; i++)
			{
				var offerObj = optionalOffers.get(i);
				var opOfferId = offerObj.get("OFFER_ID");
				var isSelect = false;
				if(offerObj.get("SELECT_FLAG") == "0")
				{
					$("#"+opOfferId).attr("checked" , true);
					$("#"+opOfferId).attr("disabled", true);
					isSelect = true;
				}
				else if(offerObj.get("SELECT_FLAG") == "1" && (operType == "CrtUs" || operType == "CrtMb") )
				{
					$("#"+opOfferId).attr("checked" , true);
					isSelect = true;
				}

				var hasOpOffer = false;
				for(var j = 0; j < subOffers.length; j++)
				{
					var subOfferData = subOffers.get(j);
					if(subOfferData.get("OFFER_ID") == opOfferId)
					{
						hasOpOffer = true;
					}
				}
				if(isSelect && !hasOpOffer)
				{//子商品状态为选中，且子商品对象列表中没有当前商品
//					subOfferData = PageData.getData($("#SUBOFFER_DATA_"+offerObj.get("OFFER_ID")));
					offerObj.put("OFFER_INDEX", "0");
					offerObj.removeKey("SELECT_FLAG");
					subOffers.add(offerObj);
				}
			}
		}
		
	}
	else
	{//隐藏子销售品区域
		if(isShowAddOfferBtn == "true")
		{
			$("#OptionalOfferPart").css("display", "");
		}
		else
		{
			$("#OptionalOfferPart").css("display", "none");
		}
	}
	return subOffers;
}

//初始化子商品对象(入参：页面上子商品对象，商品构成关系中的子商品列表，是否显示新增子商品按钮)
function initDl100SubOfferDatas(powerSubOffers,subOffers, optionalOffers, isShowAddOfferBtn)
{
	if(typeof(powerSubOffers) == "undefined")
	{
		powerSubOffers = new Wade.DatasetList();
	}
	if(typeof(subOffers) == "undefined")
	{
		subOffers = new Wade.DatasetList();
	}
	//将必选子销售品设置为选中状态且不可编辑，同时将subOffers没有 且已选中状态的子商品放到subOffers中，OPER_CODE=0
	if(optionalOffers.length != 0)
	{
		subOffers = this.setSubOfferData(subOffers, optionalOffers);
		
		var operType = $("#cond_OPER_TYPE").val();

		$("#OptionalOfferPart").css("display", "");
		for(var i = 0, size = optionalOffers.length; i < size; i++)
		{
			var offerObj = optionalOffers.get(i);
			var opOfferId = offerObj.get("OFFER_ID");
			var offerType = offerObj.get("OFFER_TYPE");
			var isSelect = false;
			if(offerObj.get("SELECT_FLAG") == "0")
			{
				$("#"+opOfferId).attr("checked" , true);
				$("#"+opOfferId).attr("disabled", true);
				isSelect = true;
			}
			else if(offerObj.get("SELECT_FLAG") == "1" && (operType == "CrtUs" || operType == "CrtMb") )
			{
				$("#"+opOfferId).attr("checked" , true);
				isSelect = true;
			}

			var hasOpOffer = false;
			for(var j = 0; j < powerSubOffers.length; j++)
			{
				var powerOfferData = powerSubOffers.get(j);
				if(powerOfferData.get("OFFER_ID") == opOfferId)
				{
					hasOpOffer = true;
				}
			}
			if(!hasOpOffer){
				for(var j = 0; j < subOffers.length; j++)
				{
					var subOfferData = subOffers.get(j);
					if(subOfferData.get("OFFER_ID") == opOfferId)
					{
						hasOpOffer = true;
					}
				}
			}

			if(isSelect && !hasOpOffer)
			{//子商品状态为选中，且子商品对象列表中没有当前商品
				offerObj.put("OFFER_INDEX", "0");
				offerObj.removeKey("SELECT_FLAG");
				if("D" == offerType || "S" == offerType){//资费服务塞到子商品结构中，其他塞到动力100结构中
					subOffers.add(offerObj);
				}else{
					powerSubOffers.add(offerObj);
				}
			}
		}
	}
	else
	{//隐藏子销售品区域
		if(isShowAddOfferBtn == "true")
		{
			$("#OptionalOfferPart").css("display", "");
		}
		else
		{
			$("#OptionalOfferPart").css("display", "none");
		}
	}
	var subMap = new Wade.DataMap();
	subMap.put("POWER100_PRODUCT_INFO",powerSubOffers);
	subMap.put("SUBOFFERS",subOffers);
	return subMap;
}

//是否已经选择子商品（如果已经选择过子商品，则再打开商品设置区域时，selectFlag=1的子商品不再默认选中，即只有第一次打开商品设置区域时，将selectFlag=1的子商品默认选中）
function hasSelectSubOffer()
{
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	var offerId = $("#prodDiv_OFFER_ID").val();
	var offerIndex = $("#prodDiv_OFFER_INDEX").val();
	
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	if(topOfferId == offerId)
	{
		var subOffers = offerData.get("SUBOFFERS");
		if(subOffers)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	else
	{
		var curOfferData = getOfferData(topOfferId, offerId, offerIndex); //取预览区域的offerdata
		var subOffers = curOfferData.get("SUBOFFERS");
		if(subOffers)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

//设置子商品数据结构(包括必选元素结构)到对应隐藏域中(入参：页面上子商品对象，商品构成关系中的子商品列表)
function setSubOfferData(subOffers, optionalOffers)
{
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	var currentOfferId = $("#prodDiv_OFFER_ID").val();
	var isEsop = $("#ESOP_TAG").val();

	if(topOfferId == currentOfferId)
	{
		// 修改预览区域子商品<待设置>标签显示标记 --开始 
		var offerData = PageData.getData($(".e_SelectOfferPart"));
		var brand = offerData.get("BRAND_CODE");
		var subOfferIdList = new Wade.DatasetList();
		$("ul[id=optOffer_"+currentOfferId+"] li").each(function(){
			if($(this).find("div[class=side]").css("display") != "block")
			{
				var liId = $(this).attr("id");
				var subOfferId = liId.substring(4);
				subOfferIdList.add(subOfferId);
			}
		});
		for(var i = 0; i < subOfferIdList.length; i++)
		{
			var subOfferId = subOfferIdList.get(i);
			for(var j = 0; j < subOffers.length; j++)
			{
				var subOffer = subOffers.get(j);
				var offerId = subOffer.get("OFFER_ID");
				//bboss产品重复订购改造
				if(subOfferId.indexOf("_") > 0 && brand == "BOSG")
				{
					var offerIndex = subOffer.get("OFFER_INDEX");
					offerId = offerId + "_" + offerIndex;
				}
				if(!isEsop)//如果是esop,不修改子商品“待设置”标记
				{
					if(subOfferId == offerId)
					{
						subOffer.put("IS_SHOW_SET_TAG", false);
					}
				}
			}
			
		}
	}
	// 修改预览区域子商品<待设置>标签显示标记 --结束	
	var selectSubData = PageData.getData($("#class_"+topOfferId));
	var selectSubOffersData = selectSubData.get("SUBOFFERS");
	for(var i = 0, size = optionalOffers.length; i < size; i++)
	{
		var offerObj = optionalOffers.get(i);
		var optOfferId = offerObj.get("OFFER_ID");
		if(brand == "BOSG"){
			var optOfferIndex = offerObj.get("OFFER_INDEX");
			$("#SUBOFFER_DATA_"+optOfferId+"_"+optOfferIndex).text(offerObj.toString());
			
			if(offerObj.get("OFFER_TYPE") == "P" && offerObj.get("REPEAT_ORDER") != "true" )
			{
				
				for(var j = 0, sizeJ = subOffers.length; j < sizeJ; j++)
				{
					var subOffer = subOffers.get(j);
					if(optOfferId == subOffer.get("OFFER_ID"))
					{//不能重复订购的商品只判断OFFER_ID相等即可
						subOffer.put("OFFER_INDEX", optOfferIndex);
						break;
					}
				}
			}
			//class_offerId
			if(offerObj.get("OFFER_TYPE") == "P" && offerObj.get("REPEAT_ORDER") != "true" && typeof(selectSubOffersData) != "undefined")
			{
				
				for(var x = 0; x < selectSubOffersData.length; x++)
				{
					var selectSubOffer = selectSubOffersData.get(x);
					if(optOfferId == selectSubOffer.get("OFFER_ID"))
					{//不能重复订购的商品只判断OFFER_ID相等即可
						selectSubOffer.put("OFFER_INDEX", optOfferIndex);
						//PageData.getData($("#class_"+topOfferId)).get("SUBOFFERS").get(x).put("OFFER_INDEX", optOfferIndex);
						break;
					}
				}
			}
			
			PageData.setData($("#class_"+topOfferId), selectSubData);

		}else{
			
			$("#SUBOFFER_DATA_"+optOfferId).text(offerObj.toString());

		}
		
	}
	return subOffers;
}

//根据商品对象设置预览区域的展示
function dealPreviewByOfferData(curOfferData)
{
	var curOfferId = curOfferData.get("OFFER_ID");
//	var curProdDataset = curOfferData.get("PRODS");
	var operType = $("#cond_OPER_TYPE").val();

	//将商品上的（待设置）标签隐藏
	$("#li_"+curOfferId+" > div").eq(0).find(".side").css("display", "none");
	
	//根据商品设置区域的定价计划对象设置预览区域定价计划展示
//	dealPricePlanPreviewByPricePlanData(curOfferData.get("PRICE_PLANS"), curOfferId);
	
	//BBoss成员没有成员商品，按照正常商品展示
	var operTypeTemp = operType.substring(3);
	//根据商品设置区域的子商品对象设置预览区域子商品展示
	if(curOfferData.get("BRAND_CODE") == "BOSG" && operTypeTemp != "Mb")
	{
		dealBbossOptOfferPreviewByOptOfferData(curOfferData.get("SUBOFFERS"), curOfferId);
	}
	else 
	{
		dealSubOfferPreviewBySubOfferData(curOfferData.get("SUBOFFERS"), curOfferId);
	}

}

//处理商品预览区域子商品(资费和服务)的展示
function dealSubOfferPreviewBySubOfferData(subOfferDataset, topOfferId)
{
	if(!subOfferDataset)
	{
		return;
	}
	var priceOfferDataset = new Wade.DatasetList();
	var serviceOfferDataset = new Wade.DatasetList();
	for(var i = 0, size = subOfferDataset.length; i < size; i++)
	{
		var subOfferData = subOfferDataset.get(i);
		if(subOfferData.get("OFFER_TYPE") == "D")
		{
			priceOfferDataset.add(subOfferData);
		}
		else
		{//OFFER_TYPE=S/P的放在一起展示
			serviceOfferDataset.add(subOfferData);
		}
	}
	
	//处理服务类商品和产品类商品
	dealOptOfferPreviewByOptOfferData(serviceOfferDataset, topOfferId);
	
	//处理资费类商品
	dealPriceOfferPreviewByPriceOfferData(priceOfferDataset, topOfferId);
}

//动力100子商品
function dealDLOptOfferPreviewByOptOfferData(curOptionOfferDataset, topOfferId)
{
	var preViewDLOptOfferIds = $("input[name=child_OFFER_ID]");

	for(var j = 0; j < preViewDLOptOfferIds.length; j++)
	{
		var isDel = true;//是否删除
		for(var a = 0; a < curOptionOfferDataset.length; a++){
			if(preViewDLOptOfferIds[j].value == curOptionOfferDataset.get(a).get("OFFER_ID")){
				if(curOptionOfferDataset.get(a).get("OPER_CODE") != ACTION_DELETE){
					isDel = false;
				}
				break;
			}
		}
		if(isDel){
			var state = preViewDLOptOfferIds[j].getAttribute("state");
			if("exist" == state){
				$("#checkDl100_"+preViewDLOptOfferIds[j].value).attr("checked" , false);
			}else{
				$("#div_"+preViewDLOptOfferIds[j].value).remove();
			}
		}
	}
	var offers = ""; //待查询实例的商品
	var offercodes = "";
	for(var i = 0; i < curOptionOfferDataset.length; i++)
	{
		var curOptOfferData = curOptionOfferDataset.get(i);
		var curOfferId = curOptOfferData.get("OFFER_ID");
		var curOfferCode = curOptOfferData.get("OFFER_CODE");
		var curOfferName = curOptOfferData.get("OFFER_NAME");
		var curOfferInsId = curOptOfferData.get("OFFER_INS_ID");
		var selOffer = $("#div_"+curOfferId);
		if(selOffer.length == 0)
		{
			offers+=curOfferId+"@";
			offercodes+=curOfferCode+"@";
			var selectFlag = PageData.getData($("#SUBOFFER_DATA_"+curOfferId)).get("SELECT_FLAG");
			var dlOptOfferHtml = "<li class='link' id='div_"+curOfferId+"'><div class='content'><div class='main' jwcid='@Any' name='divmain_" + curOfferId + "' id='divmain_" + curOfferId + "' OFFER_ID='"+ curOfferId +"' OFFER_CODE='"+curOfferCode+"' SELECT_FLAG='"+ selectFlag +"'>"
				+ curOfferName 
				+ "<input type='hidden' name='child_OFFER_ID' value='" + curOfferId + "' offerName='" + curOfferName + "'/>"
				+ "</div>"
			if(selectFlag != "0")
			{
				dlOptOfferHtml += "<div class='fn'><span class='e_ico-close' OFFER_ID='" + curOfferId + "' ontap='deleteOptionalChildOffer(this);'></span></div>";
			}

			dlOptOfferHtml += "<div class='fn' id='refresh_"+ curOfferId+"'><span class='e_ico-refresh' OFFER_ID='"+ curOfferId +"' OFFER_NAME='"+ curOfferName +"' OFFER_CODE='"+curOfferCode+"' ontap='refreshDLChildOffer(this);'></span></div>"
			+ "<div class='fn' id='add_"+ curOfferId+"'><span class='e_ico-add' OFFER_ID='"+ curOfferId +"' OFFER_NAME='"+ curOfferName +"' OFFER_CODE='"+curOfferCode+"' ontap='operNewOffer(this);'></span></div></div></li>";
			$("#childUL").append(dlOptOfferHtml);
		}
	}
	
	if(offers.length > 0){
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		var custId = custInfo.get("CUST_ID");

		//此处根据cust_id和offer_id查询一下集团是否已订购
		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "queryOfferInstInfos", "OFFER_ID="+offers+"&OFFER_CODE="+offercodes+"&CUST_ID="+custId, "", function(data){
			var insOffers = data.get("INS_OFFERS");
			if(insOffers){
				
				for(var i = 0, size = insOffers.length; i < size; i++)
				{
					var offerId = insOffers.get(i).get("OFFER_ID");
					var offerName = insOffers.get(i).get("OFFER_NAME");
					var insSize = insOffers.get(i).get("INS_SIZE");
					$("#refresh_"+offerId).remove();
					if(insSize>1){//如果存在多实例的情况，可支持选择
						$("#add_"+offerId).children().attr("ontap","selectDLChildOffer(this)");
						continue;
					}
					$("#add_"+offerId).remove();
					var accessNum =insOffers.get(i).get("SERIAL_NUMBER");
					var subscriberInsId =insOffers.get(i).get("USER_ID");
					//三、加入新选择的实例
					var offerInstHtml =  "<div class='sub' id='div_ins_"+offerId+"'><div class='main'>" 
					+ "<div class='title'></div>" 
					+ "<div class='content'>" + "【" + accessNum + "】" + offerName + "</div>"
					+ "<input type='hidden' id='ps_"+offerId+"_SUB_INS_ID' value='" + subscriberInsId + "'/></div></div>";
					$("#div_"+offerId).append(offerInstHtml);
		
					var offerData = PageData.getData($("#class_"+topOfferId));
					var optOfferDataset = offerData.get("POWER100_PRODUCT_INFO"); //可选子销售品(取已存在的数据)
					if(typeof(optOfferDataset) == "undefined")
					{
						optOfferDataset = new Wade.DatasetList();
					}
					for(var j = 0, sizej = optOfferDataset.length; j < sizej; j++)
					{
						var optOfferData = optOfferDataset.get(j);
						if(optOfferData.get("OFFER_ID") == offerId)
						{
							optOfferData.put("OPER_CODE", "0");
							optOfferData.put("OFFER_ID", offerId);
							optOfferData.put("OFFER_NAME", offerName);
							optOfferData.put("USER_ID", subscriberInsId);
							optOfferData.put("OFFER_INS_ID", insOffers.get(i).get("OFFER_INS_ID"));
							var selFlag = $("#"+offerId).attr("SELECT_FLAG");
							optOfferData.put("SELECT_FLAG", selFlag);
							break;
						}
					}

					offerData.put("POWER100_PRODUCT_INFO", optOfferDataset);
					PageData.setData($("#class_"+topOfferId), offerData);
					
				}
			}
			$.endPageLoading();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });

	}

	
	$("#DLchildOfferPart").css("display", "");
}

//根据子商品数据结构加载商品预览区域
function dealOptOfferPreviewByOptOfferData(curOptionOfferDataset, topOfferId)
{
	var preViewOptOfferName = "oo_" + topOfferId + "_OPTOFFER_ID";
	var preViewOptOfferIds = $("input[name="+preViewOptOfferName+"]");
	
	for(var j = 0; j < preViewOptOfferIds.length; j++)
	{
		$("#div_"+preViewOptOfferIds[j].value).remove();
	}
	
	if($("#optOffer_"+topOfferId).children().length == 0)
	{
		$("#sub_"+topOfferId).remove();
	}

	if(typeof(curOptionOfferDataset) != "undefined")
	{
		for(var i = 0; i < curOptionOfferDataset.length; i++)
		{
			var curOptOfferData = curOptionOfferDataset.get(i);
			var curOfferId = curOptOfferData.get("OFFER_ID");
			var curOfferName = curOptOfferData.get("OFFER_NAME");
			var isShowTag = curOptOfferData.get("IS_SHOW_SET_TAG");
			var subOfferIndex = curOptOfferData.get("OFFER_INDEX");
			
			if(curOptOfferData.get("OPER_CODE") != ACTION_DELETE)
			{
				if($("#sub_"+topOfferId).length == 0)
				{
					var subHtml = "<div class='sub' id='sub_"+topOfferId+"'><div class='main' >" 
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>" 
						+ "<ul id='optOffer_" + topOfferId + "'>";
						+ "</ul></div></div></div>";
					
					$("#li_"+topOfferId).append(subHtml);
						
				}
				
				var optionalOfferHtml = "<li id='div_"+curOfferId+"'><div class='group link' ontap='openEnterpriseOfferPopupItem("+curOfferId+","+topOfferId+",false,"+subOfferIndex+");'>" 
					+ "<div class='main'>" + curOfferName + "</div>";
				if(isShowTag == "true")
				{
					optionalOfferHtml += "<div class='side'><span class='e_tag e_tag-red'>待设置</span></div>";
				}
				optionalOfferHtml +=  "<div class='more'></div>" 
					+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "'/>" 
					+ "</div></li>";
				
//				if($("#"+curOfferId).attr("SELECT_FLAG") != "0")
//				{
//					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteOptionalOffer(" + curOfferId + "," + topOfferId + ");'></span></div>";
//				}
//				else
//				{
//					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='fn e_dis e_ico-delete' /></div>";
//				}
				$("#optOffer_"+topOfferId).append(optionalOfferHtml);
				
			}
		}
	}
}

//处理商品预览区域资费商品展示
function dealPriceOfferPreviewByPriceOfferData(priceOfferDataset, topOfferId)
{
	var preViewPriceOfferName = "pp_" + topOfferId + "_PRICE_OFFER_ID";
	var preViewPriceOfferIds = $("input[name="+preViewPriceOfferName+"]");
	
	for(var j = 0; j < preViewPriceOfferIds.length; j++)
	{
		$("#div_"+preViewPriceOfferIds[j].value).remove();
	}

	if(typeof(priceOfferDataset) != "undefined")
	{
		for(var i = 0; i < priceOfferDataset.length; i++)
		{
			var priceOfferData = priceOfferDataset.get(i);
			var offerId = priceOfferData.get("OFFER_ID");
			var offerName = priceOfferData.get("OFFER_NAME");
			if(priceOfferData.get("OPER_CODE") != ACTION_DELETE)
			{
				var priceOfferHtml = "<div id='div_"+offerId+"'>【" + priceOfferData.get("OFFER_CODE") + "】" + offerName 
					+ "<input type='hidden' name='pp_"+topOfferId+"_PRICE_OFFER_ID' value='" + offerId + "'/></div>";
				
				$("#priceOffer_"+topOfferId).append(priceOfferHtml);
			}
		}
	}
}

//是否显示商品'待设置'标签
function isShowOfferChaSpecTag(data)
{
	var hasOfferChaSpecs = data.get("HAS_OFFER_CHA_SPECS");
	
	// 判断是否含有静态参数页面
	if (isHasStaticParamPage()) 
	{
		hasCha = "true";
		hasOfferChaSpecs = "true";
	}

	var operCode = $("#cond_OPER_TYPE").val();
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var brand = mainOffer.get("BRAND_CODE");
	var bbossMerchPage = false;
	//判断是否含有BBOSS参数页面
	if(judgeBbossBiz(brand,operCode)) {
        hasOfferChaSpecs = "true";
	}
	
	if(hasOfferChaSpecs == "true")
	{
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		var offerId = offerData.get("OFFER_ID");
		var offerChaSpecStr = $("#cha_"+offerId).val();
		var esopOfferCha = offerData.get("ESOP_OFFER_CHA");
		var offerOperCode = offerData.get("OPER_CODE"); //当前商品的操作编码
		if(((offerChaSpecStr != "" || (offerOperCode != ACTION_CREATE && offerOperCode != ACTION_PASTE && offerOperCode != ACTION_CONTINUE ))) && esopOfferCha != "true" || offerData.get("MERCHINFO"))
		{//如果设置过产品特征或者有产品特征实例 且不是esop过来的数据 则隐藏待设置标签
			$("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq(1).css("display", "none");			
		}
	}
	else
	{
		$("ul[id=offerChaSpecUL] li:nth-child(1)").removeClass("link");
		$("ul[id=offerChaSpecUL] li:nth-child(1)").attr("ontap", "");
		$("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq(1).css("display", "none");
		$("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq(2).css("display", "none");
	}
	
}

//是否显示资费商品特征'待设置'标签
function isShowPriceOfferChaTag(subOffers)
{
	if(typeof(subOffers) == "undefined" || subOffers.length == 0)
	{
		return ;
	}
	var priceOfferList = getSubOffersByOfferType(subOffers, "D");
	for(var i = 0; i < priceOfferList.length; i++)
	{
		var priceOffer = priceOfferList.get(i);
		var offerId = priceOffer.get("OFFER_ID");
		var operCode = priceOffer.get("OPER_CODE");
		var hasPriceCha = priceOffer.get("HAS_PRICE_CHA");
		var pOfferIndex = priceOffer.get("P_OFFER_INDEX");
		var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
		if(hasPriceCha == "true" && (priceOfferChaStr != "" || operCode != ACTION_CREATE))
		{
			priceOfferCha.hidePriceOfferChaTag(offerId, pOfferIndex);
		}
	}
}

//隐藏服务类商品区域
function hideServiceOfferPart()
{
	
	if($("#serviceOfferUL").children().length == 0)
	{
		$("#ServiceOfferListPart").css("display", "none");
		
	}
	if($("#delServiceOffer").children().length == 0)
	{
		$("#ServiceDelOfferListPart").css("display", "none");
		
	}
	
}

//隐藏资费类商品区域
function hidePriceOfferPart()
{
	
	if($("#priceOfferUL").children().length == 0)
	{
		$("#PriceOfferPart").css("display", "none");
		
	}
	if($("#delPriceOffer").children().length == 0)
	{
		$("#PriceDelOfferPart").css("display", "none");
		
		
	}
	
}

//隐藏产品类子商品区域
function hideProductOfferPart()
{

	if($("#productOfferUL").children().length == 0)
	{
		$("#productOfferListPart").css("display", "none");
		
	}
}


function judgeBbossBiz(brand, operCode) {
    // if！BBOSS return
    if("BOSG" != brand || "DstMb" ==operCode) {
        $("#BbossOfferParam").css("display", "none");
        return false;
    }

    var offerId = $("#prodDiv_OFFER_ID").val();
    var configFlag = false;
    var param = "OPER_TYPE=" + operCode + "&OFFER_ID=" + offerId;
    $.beginPageLoading("数据加载中......");
    $.ajax.submit("", "getBbossMerchPageConfig", param, "", function(data){
            $.endPageLoading();
            var ret = JSON.parse(data);
            var flag = ret.hasMerchPage;
            if("false" == flag) {
                return ;
            }
            configFlag = true;
            $("#BbossOfferParam").css("display", "");
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        },{async:false});

    return configFlag;
}

//显示日期组件
function showDateField(el)
{
	var type= $(el).attr('name');
	//获取正在操作
	operObj=el;
	if(type=='BEGIN'){
	forwardPopup(el, 'calendarItem0');
	setCalendar1Value('calendar0');
	var $li = $(el).closest("li");
	var offerId = $li.attr("id").split("_")[1];
	var pOfferIndex = $li.attr("offerIndex");
	
	$("#BEGIN_OFFER_ID").val(offerId);
	$("#BEGIN_OFFER_INDEX").val(pOfferIndex);
	}
	
	if(type=='CANCEL'){
		forwardPopup(el, 'calendarItem1');
		setCalendar1Value('calendar1');
		var $li = $(el).closest("li");
		var offerId = $li.attr("id").split("_")[1];
		var pOfferIndex = $li.attr("offerIndex");
		
		$("#CAL_OFFER_ID").val(offerId);
		$("#CAL_OFFER_INDEX").val(pOfferIndex);
		}
	
	

}

//修改资费类商品失效时间
function changePriceOfferExpiredDate(date)
{
	var offerId = $("#CAL_OFFER_ID").val();
	var pOfferIndex = $("#CAL_OFFER_INDEX").val();
	
//	date = date + " 23:59:59";
	//修改页面显示日期
	$("#expire_"+offerId+"_"+pOfferIndex).text(date);
	
	//修改商品数据结构日期
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOffers = offerData.get("SUBOFFERS");
	if(!subOffers)
	{
		return ;
	}
	for(var i = 0, size = subOffers.length; i < size; i++)
	{
		var subOfferData = subOffers.get(i);
		if(subOfferData.get("OFFER_ID") == offerId && subOfferData.get("P_OFFER_INDEX") == pOfferIndex)
		{
			subOfferData.put("END_DATE", date);
			break;
		}
	}
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}


//修改资费类商品生效时间
function changePriceOfferValidDate(date)
{
	var offerId = $("#BEGIN_OFFER_ID").val();
	var pOfferIndex = $("#BEGIN_OFFER_INDEX").val();
	
//	date = date + " 23:59:59";
	//修改页面显示日期
	$("#valid_"+offerId+"_"+pOfferIndex).text(date);
	
	//修改商品数据结构日期
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOffers = offerData.get("SUBOFFERS");
	if(!subOffers)
	{
		return ;
	}
	for(var i = 0, size = subOffers.length; i < size; i++)
	{
		var subOfferData = subOffers.get(i);
		if(subOfferData.get("OFFER_ID") == offerId && subOfferData.get("P_OFFER_INDEX") == pOfferIndex)
		{
			subOfferData.put("START_DATE", date);
			break;
		}
	}
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}



//加载产品规格特征区域
function initOfferChaSpecPopupItem(obj)
{
	//设置显示区域
	var operType = $("#cond_OPER_TYPE").val();
	
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var brand = mainOffer.get("BRAND_CODE");
	var topOfferId = mainOffer.get("OFFER_ID");
	var ecOfferId = $("#cond_OFFER_ID").val();
	var ecOfferCode = $("#cond_OFFER_CODE").val();
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var serialNumber = $("#cond_VALID_SERIAL_NUMBER").val();
	var curOfferType = PageData.getData($("#prodDiv_OFFER_DATA")).get("OFFER_TYPE");
	var curOfferCode = PageData.getData($("#prodDiv_OFFER_DATA")).get("OFFER_CODE");
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	
	var groupId = groupInfo.get("GROUP_ID");
	var custId = custInfo.get("CUST_ID");
	var ecUserId =  $("#cond_EC_USER_ID").val();
	// 判断是否为BBOSS业务的商品页面
	var isBBossBiz = judgeBbossBiz(brand, operType);
	if(isBBossBiz) {
		showBbossMerchPage(obj, operType, topOfferId);
		return;
	}
	else {
		hideBbossMerchPage();
	}

	var showSubPopup = function() {
		$("#proParamPage").css("display", "");
		$("#dynamicOfferParam").css("display", "none");

		forwardPopup(obj, 'productChaSpecPopupItem');
	};

	var methodName = "";
	var param = "OFFER_CODE="+ecOfferCode+"&GROUP_ID="+groupId+"&OFFER_ID="+ecOfferId+"&SUB_OFFER_ID="+curOfferId+"&SUB_OFFER_TYPE="+curOfferType+"&SUB_OFFER_CODE="+curOfferCode+"&BRAND_CODE="+brand+"&CUST_ID="+custId+"&EC_USER_ID="+ecUserId+"&MEB_SERIAL_NUMBER="+serialNumber;
	if(operType == "CrtUs" || operType == "CrtMb")
	{
		methodName = "initOfferChaSpecByOfferId";
		param += "&OPER_CODE="+ACTION_CREATE;
		if(operType == "CrtMb"){
			var subInsId = $("#cond_USER_ID").val();
			var userEparchyCode = $("#cond_USER_EPARCHY_CODE").val();
			param = param +"&USER_ID="+subInsId+"&USER_EPARCHY_CODE="+userEparchyCode;
		}
	}
	else if(operType == "ChgUs" || operType == "ChgMb")
	{
		var offerIndex = $("#prodDiv_OFFER_INDEX").val();
		
		var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
		if(curOfferData.get("OPER_CODE") == ACTION_CREATE)
		{//当前产品规格为新增状态
			methodName = "initOfferChaSpecByOfferId";
			param += "&OPER_CODE="+ACTION_CREATE;
		}
		else
		{
			var subInsId = operType == "ChgUs" ? curOfferData.get("USER_ID") : $("#cond_USER_ID").val();
			var offerInsId = curOfferData.get("OFFER_INS_ID");
			methodName = "initOfferChaSpecByOfferId";
			param += "&USER_ID="+subInsId+"&OFFER_INS_ID="+offerInsId+"&OPER_CODE="+ACTION_UPDATE;
		}
		
		if("ChgMb" == operType)
		{
			var userEparchyCode = $("#cond_USER_EPARCHY_CODE").val();
			param += "&USER_EPARCHY_CODE="+userEparchyCode;
		}
	}
	else if(operType == "DstUs" && "BOSG" == brand)
	{
		var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
		var subInsId = curOfferData.get("USER_ID");
		var offerInsId = curOfferData.get("OFFER_INS_ID");


		methodName = "initOfferChaSpecByOfferId";
		param += "&USER_ID="+subInsId+"&OFFER_INS_ID="+offerInsId+"&OPER_CODE="+ACTION_DELETE;
		
	}

	//判断是否有静态参数页面
	var isStaticPage = isHasStaticParamPage();
	if(isStaticPage) {
		param = param +"&OPER_TYPE="+operType;
		var pageParam = getStaticPageParam(ecOfferId);
		pageParam.param = param;
		showStaticPage(obj, pageParam);
		return;
	}
	else {
		hideStaticPage();
	}
	
	
	if("BOSG" == brand && (operType == "CrtUs" || operType == "ChgUs"))
	{
		var offerIndex = "";
		var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
		offerIndex = curOfferData.get("OFFER_INDEX");
		var merchpOperType = $("#div_"+curOfferId+"_"+offerIndex).children()[0].getAttribute("prodOperType");
		param += "&MERCHP_OPER_TYPE="+merchpOperType;
	}
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", methodName, param, "proParamPage,PopPart,proParamPagePop", function(data){
			$.endPageLoading();
			checkparamopertype();
			checkparamopertypeByUsr();
			var grpItems = data.map.result;
			var offerChaSpecStr = $("#cha_"+curOfferId).val();
			if ("BOSG" == brand && grpItems){
				if(offerChaSpecStr==null||offerChaSpecStr==""){
					for (var i=0;i<grpItems.length;i++){
						
						pointGrpItemInfos2(grpItems.get(i));
							
						
					}
				}

			}

			showSubPopup();
			
			if(offerChaSpecStr != "")
			{
				var offerChaSpecs = new Wade.DatasetList(offerChaSpecStr);
				for(var i = 0; i < offerChaSpecs.length; i++)
				{
					var offerChaSpec = offerChaSpecs.get(i);
					var attrCode = offerChaSpec.get("ATTR_CODE");
					var attrValue = offerChaSpec.get("ATTR_VALUE");
					var grpItemId = offerChaSpec.get("ATTR_GROUP");
					if (grpItemId)
					{
						pointGrpItemInfos2(offerChaSpec);
//						dealGrpItemInit(grpItemId,elementId,elementValue);
					}
					else
					{
						var inputName = "pam_" + attrCode;
						if("BOSG" == brand)
						{
							inputName = attrCode;
							$("#proParamPage input[element_id="+attrCode+"]").val(attrValue);
						}
						
						var inputType = $("#proParamPage input[name="+inputName+"]").attr("type");
						if(inputType == "checkbox" || inputType == "radio")
						{
							if(attrValue == "1")
							{
								$("#proParamPage input[name="+inputName+"]").attr("checked", "true");
							}
							else
							{
								$("#proParamPage input[name="+inputName+"]").removeAttr("checked");
							}
						}
						else
						{
							$("#proParamPage input[id="+inputName+"]").val(attrValue);
							if (attrValue.length>0 && "simpleupload" == $("#proParamPage input[element_id=" + attrCode + "]").attr("x-wade-uicomponent")) {
								var uploadId = $("#proParamPage input[element_id=" + attrCode + "]").attr("id");
								$("#" + uploadId + "_name").val(attrValue);
								$("span[class=e_ico-close]").attr("style","display:");
								$("span[class=e_ico-download]").attr("style","display:");

							}
						}
					}
				}
				
				initOfferChaSpec(offerChaSpecs);

			}
			
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});

}

//提交产品特征规格
function submitOfferCha()
{
	//判断是否必填项是否填完
	var isBacth = $("#cond_BAT_TAG").val();
	if(!isBacth){
		if(!$.validate.verifyAll("proParamPage")){
			return false;
		}
	}
	var isChaValueChg = false;
	var isChaDataChg = false;
	var offerId = $("#prodDiv_OFFER_ID").val();
	
	var brand = PageData.getData($(".e_SelectOfferPart")).get("BRAND_CODE");
	
	//获取当前商品操作类型：新增or变更
	var operCode = PageData.getData($("#prodDiv_OFFER_DATA")).get("OPER_CODE");
	
	var offerChaSpecDataset = new Wade.DatasetList(); //产品规格特征

	var val_1101584033 = "";//本异网是否统一套餐
	var val_1101574037 = "";//异网套餐费（元/月）
	var val_1101574038 = "";//异网套餐包含条数
	var val_1101574039 = "";//异网套餐外资费标准（元/条）

	var chaSpecObjs = $("#proParamPage input");
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		var elemetnId = chaSpecObjs[i].getAttribute("element_id");
		var simpleupload = chaSpecObjs[i].getAttribute("x-wade-uicomponent");
		var chaSpecGroupId = chaSpecObjs[i].getAttribute("attr_group");
		
		//剔除隐藏的上传主键
		if("BOSG"==brand&&removeUpload(elemetnId,simpleupload,chaSpecObjs[i])){
			continue;
		}
		
		if("BOSG"==brand&&isBbossNull(chaSpecCode)){
			if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		else if (!chaSpecCode)
		{
			if(chaSpecObjs[i].type != "checkbox" && chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
		{
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			if(chaValue==0)
				continue;
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
		}
		var chaSpecId = chaSpecObjs[i].getAttribute("element_id");
		if(chaSpecId == "" || chaSpecId == null)
		{
			continue;
		}
		if(chaValue == "" || chaValue == null)
		{
			continue;
		}

		if(operCode != ACTION_CREATE && operCode != "5" && operCode != "6")
		{
			
			var oldValue = chaSpecObjs[i].getAttribute("old_value");
			if(chaValue != oldValue)
			{
				isChaValueChg = true;
				isChaDataChg = true;
			}
			else
			{
				isChaDataChg = false;
			}
		}
		
		
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
		if(chaSpecCode.indexOf("pam_") == 0)
		{
			var prodChaValCode = chaSpecCode.substring(4);
			offerChaSpecData.put("ATTR_CODE", prodChaValCode);
		}
		else
		{
			if(chaSpecObjs[i].type == "radio" || chaSpecObjs[i].type == "checkbox")
			{//针对些单选框的name没有pam的情况
				offerChaSpecData.put("ATTR_CODE", chaSpecObjs[i].name.substring(4));
			}
			else
			{
				offerChaSpecData.put("ATTR_CODE", chaSpecCode);
			}
		}

		if(brand == "BOSG"){
			var chaSpecId = chaSpecObjs[i].getAttribute("element_id");
			if(chaSpecId == "" || chaSpecId == null)
			{
				continue;
			}
			offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
            offerChaSpecData.put("ATTR_CODE", chaSpecId);
		}

		if(operCode != ACTION_CREATE && isChaDataChg == true)
		{
			offerChaSpecData.put("OLD_ATTR_VALUE", chaSpecObjs[i].getAttribute("old_value"));
			if(chaSpecGroupId != "" && chaSpecGroupId != null){
				if(chaSpecObjs[i].getAttribute("oper_code")!=null&&chaSpecObjs[i].getAttribute("oper_code")!=ACTION_EXITS){
					offerChaSpecData.put("OPER_CODE", chaSpecObjs[i].getAttribute("oper_code"));
				}
			}else{
			
				offerChaSpecData.put("OPER_CODE", ACTION_UPDATE);
			}

		}
		if("BOSG"==brand){
			offerChaSpecData.put("ATTR_CODE", chaSpecObjs[i].getAttribute("element_id"));
		}
		//bboss属性组
		if(chaSpecGroupId != "" && chaSpecGroupId != null){
		    offerChaSpecData.put("ATTR_GROUP", chaSpecGroupId);
		    offerChaSpecData.put("OPER_CODE", chaSpecObjs[i].getAttribute("oper_code"));

		}
		offerChaSpecDataset.add(offerChaSpecData);

		var val_elementId = chaSpecObjs[i].getAttribute("element_id");
		if("1101574033"==val_elementId){
			val_1101584033 = chaSpecObjs[i].value;
		}
		if("1101574037"==val_elementId){
			val_1101574037 = chaSpecObjs[i].value;
		}
		if("1101574038"==val_elementId){
			val_1101574038 = chaSpecObjs[i].value;
		}
		if("1101574039"==val_elementId){
			val_1101574039 = chaSpecObjs[i].value;
		}
	}
	if(2==val_1101584033){//本异网是否统一套餐'值为否
		if(val_1101574037==""||val_1101574038==""||val_1101574039==""){
			MessageBox.alert("提示信息", "'本异网是否统一套餐'为否时，请填写异网套餐费（元/月）/异网套餐包含条数/异网套餐外资费标准（元/条）");
			return false;
		}
	}
	var grpItemEleObjs =$("#proParamPage textarea[isgrpitem='true']");
	if (grpItemEleObjs && grpItemEleObjs.length>0){
		for (var i = 0;i<grpItemEleObjs.length;i++){
			var grpMapObj = new Wade.DataMap($(grpItemEleObjs[i]).text());
			var paramData = grpMapObj.get("ELEMENT_DATAS");
			for (var j =0;j<paramData.items.length;j++){
				var offerChaSpecData = new Wade.DataMap();
				var grpItem = $(grpItemEleObjs[i]).attr("id").substring(8);
				offerChaSpecData.put("CHA_SPEC_ID", paramData.keys[j]);
				offerChaSpecData.put("CHA_SPEC_CODE", paramData.keys[j]);
				offerChaSpecData.put("CHA_VALUE", paramData.items[j]);
				offerChaSpecData.put("GROUP_ATTR", grpItem);
				offerChaSpecDataset.add(offerChaSpecData);
			}
		}
	}
	
//	alert(offerChaSpecDataset);
	
	$("#cha_"+offerId).val(offerChaSpecDataset);
	var merchOperType = $("#OPERTYPE").val();
	//将产品特征规格放到数据对象中 
	if(offerChaSpecDataset.length != 0)
	{
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		offerData.put("OFFER_CHA_SPECS", offerChaSpecDataset);
		offerData.put("IS_CHA_VALUE_CHANGE", isChaValueChg);//判断当前商品属性是否变更标记
		if(isChaValueChg == true && (merchOperType!="3"||merchOperType!="4"))
		{
			offerData.put("OPER_CODE", ACTION_UPDATE);
		}
		
		var esopOfferCha = offerData.get("ESOP_OFFER_CHA");
		if(!esopOfferCha)
		{
			offerData.removeKey("ESOP_OFFER_CHA");
		}

		//保存数据
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	}
	
	$("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq("1").css("display", "none");
	
	return true;
}

//提交商品设置区域数据
function submitSubOfferData(el)
{
	if(!checkSelOfferNum())
	{
		return ;
	}

	//顶层商品编码
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	
	//当前设置的商品的编码
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	
	//顶层商品数据对象
	var offerData = PageData.getData($("#class_"+topOfferId));
	
	if("DLBG" == offerData.get("BRAND_CODE")){//动力100单独处理
		submitDL100SubOfferData(el);
		return;
	}

	//当前设置的商品的数据对象
	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	
	//当前设置的商品序号
	var curOfferIndex = $("#prodDiv_OFFER_INDEX").val();	
	var operType = $("#cond_OPER_TYPE").val();
	var brand = offerData.get("BRAND_CODE");
	var isOfferChaSpecPartExist = $("#OfferChaSpecPart").length > 0;
	var isBatDeal = $("#cond_BAT_TAG").val();
	//var merchOperType = offerData.get("MERCHINFO").get("OPERTYPE");
	
	//ADC校讯通校验
	if("ADCG" == brand && operType == "ChgMb"){
		
		if(topOfferId ==curOfferId && "110000915001"==topOfferId && !validateDiscntPage()){
			return;
		}
	}
	
	//bboss定制信息必选包检验
	if("BOSG" == brand && (operType == "CrtUs"||operType == "ChgUs") && topOfferId!=curOfferId){
		var forcePkgInfoList = $("#FORCE_PACKAGE_LIST").val();
		var forcePkgInfos =  new Wade.DatasetList(forcePkgInfoList);

		if(forcePkgInfos&&forcePkgInfos.length>0){
			var curOfferCode = curOfferData.get("OFFER_CODE");
			var pkgInfos = offerData.get("GRP_PACKAGE_INFO").get(curOfferCode);
		
			for(var i=0; i<forcePkgInfos.length; i++){
				var existForcePkg = false;
				var forcePackageId = forcePkgInfos.get(i).get("PACKAGE_ID");
				if(!pkgInfos){
					
					MessageBox.alert("提示信息", "集团定制信息中包"+forcePkgInfos.get(i).get("PACKAGE_NAME")+"是必选包，必须选择包下的至少一个元素");
					return false;

				}
				for(var j=0; j<pkgInfos.length; j++){
					var selectPackageId = pkgInfos.get(j).get("PACKAGE_ID");
					if(operType == "ChgUs"){
						var modifyTag = pkgInfos.get(j).get("MODIFY_TAG");
						if(forcePackageId == selectPackageId && modifyTag!="1"){
							existForcePkg=true;
						}
					}else{
						
						if(forcePackageId == selectPackageId){
							existForcePkg=true;

							break;
						}
					}
					
				}
				if(!existForcePkg){
					MessageBox.alert("提示信息","集团定制信息中包"+forcePkgInfos.get(i).get("PACKAGE_NAME")+"是必选包，必须选择包下的至少一个元素");
					return false;
				}
				
			}
			
		}
	}
	//BBOSS本地商品资费变更 商品资费变更  
	if("BOSG" == brand && operType == "ChgUs"&&typeof(offerData.get("MERCHINFO")) != "undefined") {

		if(!paramSpelValidate())
		{
			return;
		}
	}
	
	if("BOSG" == brand && operType == "ChgMb") {
		var mebOperType = $("#BM710000000734").val();
		if(mebOperType == "" || mebOperType== undefined)
		{
			MessageBox.alert("提示信息", "成员变更操作时请点击商品特征选择成员操作类型！");
			return false;

		}

	}
	if(isOfferChaSpecPartExist && (operType == "ChgUs" || operType == "ChgMb" ) && (brand!="BOSG"))
	{
		if(operType == "ChgMb" && ("110000574401" ==topOfferId||"110000601301" ==curOfferId||"110000840201" ==curOfferId)){			
		
		}
		else
		{		
			var offerChaSpecss = curOfferData.get("OFFER_CHA_SPECS");			
			if(typeof(offerChaSpecss) == "undefined" || offerChaSpecss.length == 0)
			{
				MessageBox.alert("提示信息", "如无需变更,请先打开产品特征信息后点击确定！");
				return false;
			}		
		}
	}
	
	if(isOfferChaSpecPartExist && (operType == "CrtUs" || operType == "CrtMb" || (brand == "BOSG" && operType == "ChgUs")))
	{
		var offerChaSpecs = curOfferData.get("OFFER_CHA_SPECS");
		// bboss业务商品参数在特殊结构中
		var merchInfo = offerData.get("MERCHINFO");
		if("BOSG" == brand && curOfferId == topOfferId && operType!="CrtMb" )
		{
			if(!merchInfo) 
			{
				MessageBox.alert("提示信息", "请先设置产品特征信息！");
				return false;
			}
		}
		else
		{
			var setProdChaSpecTag = $("ul[id=offerChaSpecUL] li:nth-child(1)").children().eq(1).css("display");
			if(setProdChaSpecTag != "none")
			{
				if(typeof(offerChaSpecs) == "undefined" || offerChaSpecs.length == 0)
				{
					MessageBox.alert("提示信息", "请先设置产品特征信息！");
					return false;
				}
			}
		}
	}
	
	var priceChaFlag = true;
	$("#priceOfferUL").find("div[class=side]").each(function(){
		if($(this).css("display") == "block" && $(this).attr("tag") == "0")
		{
			priceChaFlag =  false;
		}
	});
	if(!priceChaFlag)
	{
		MessageBox.alert("提示信息", "请设置资费商品特征！");
		return false;
	}
	
	//设置数据对象
	if(topOfferId == curOfferId)
	{
		var curOfferChaDataset = curOfferData.get("OFFER_CHA_SPECS");
		offerData.put("IS_CHA_VALUE_CHANGE", curOfferData.get("IS_CHA_VALUE_CHANGE"));//属性是否变更
		offerData.put("OPER_CODE", curOfferData.get("OPER_CODE"));
		if(typeof(curOfferChaDataset) != "undefined" && curOfferChaDataset.length > 0)
		{
			offerData.put("OFFER_CHA_SPECS", curOfferChaDataset);
		}
		
		var curOptionOfferDataset = curOfferData.get("SUBOFFERS");
		//oper_code  0 增加, 3 修改 , 1 取消
		if(curOptionOfferDataset!=null&&typeof(curOptionOfferDataset)!="undefined"&&curOptionOfferDataset.length>0){
			for(var i=0;i<curOptionOfferDataset.length;i++){
				var temp=curOptionOfferDataset.get(i);
		        if(temp.get("OPER_CODE")=="0"){
		    			if(temp.get("OFFER_CODE")==84017249){
		    	        	alert("如选择办理达量限速功能，使用超过套餐内的流量，则将进行限速。");
		    	        }
		        }
			}
		}
		if(typeof(curOptionOfferDataset) != "undefined" && curOptionOfferDataset.length > 0)
		{
			offerData.put("SUBOFFERS", curOptionOfferDataset);
		}
		else
		{
			offerData.removeKey("SUBOFFERS");
		}
		
		//根据商品对象设置预览区域的展示
		dealPreviewByOfferData(curOfferData);
	}
	else
	{
		var curOfferOperCode = curOfferData.get("OPER_CODE");
		if((operType == "ChgUs" || operType == "ChgMb") && (curOfferOperCode == ACTION_UPDATE || curOfferOperCode == ACTION_EXITS))
		{
			checkCurOfferOperCode(curOfferData);
		}
		
		var subOfferDataset = offerData.get("SUBOFFERS");
		for(var i = 0; i < subOfferDataset.length; i++)
		{
			var subOfferData = subOfferDataset.get(i);
			if(subOfferData.get("OFFER_ID") == curOfferId && subOfferData.get("OFFER_INDEX") == curOfferIndex)
			{
				var curOfferChaDataset = curOfferData.get("OFFER_CHA_SPECS");
				subOfferData.put("IS_CHA_VALUE_CHANGE", curOfferData.get("IS_CHA_VALUE_CHANGE"));//属性是否变更
				subOfferData.put("OPER_CODE", curOfferData.get("OPER_CODE"));
				if(typeof(curOfferChaDataset) != "undefined" && curOfferChaDataset.length > 0)
				{
					subOfferData.put("OFFER_CHA_SPECS", curOfferChaDataset);
				}
				
				var curOptionOfferDataset = curOfferData.get("SUBOFFERS");
				if(typeof(curOptionOfferDataset) != "undefined" && curOptionOfferDataset.length > 0)
				{
					subOfferData.put("SUBOFFERS", curOptionOfferDataset);
				}
				
				if("BOSG" == brand)
				{
					$("#div_"+curOfferId+"_"+curOfferIndex+" > div").eq(0).find(".side").css("display", "none");
				}
				else
				{
					$("#div_"+curOfferId+" > div").eq(0).find(".side").css("display", "none");
				}
				
			}
		}
	}
	//保存数据对象
	PageData.setData($("#class_"+topOfferId), offerData);
	
	//将商品设置区域开关关闭
	$("#prodDiv_SWITCH").val("off");
	
	//设置定制信息再次打开
	var openData = PageData.getData($("#cond_OPEN_TAG"));
	if(curOfferId!=topOfferId){
		if(openData){			
			openData.put(curOfferId+"","IsOpen");
			$("#cond_OPEN_TAG").val(openData);		
		}else{
			var newOpenData = new Wade.DataMap();
			newOpenData.put(curOfferId+"","IsOpen");
			//设置再次打开
			$("#cond_OPEN_TAG").val(newOpenData);
		}
	}


	if(window.offerProductCallback)
	{
		window.offerProductCallback.call(this, offerData);
	}
	
	//手动刷新scroller组件
	editMainScroll.refresh();
	
	hidePopup(el);
}

//修改当前商品的OPER_CODE
function checkCurOfferOperCode(curOfferData)
{
	
	if(curOfferData.get("IS_CHA_VALUE_CHANGE") == "true")
	{

		curOfferData.put("OPER_CODE", ACTION_UPDATE);
		
	}
	else
	{
		var subOffers = curOfferData.get("SUBOFFERS");
		if(subOffers)
		{//有子商品
			var isChangeSubOffer = false;
			for(var i = 0, size = subOffers.length; i < size; i++)
			{
				var subOfferOperCode = subOffers.get(i).get("OPER_CODE");
				if(subOfferOperCode != ACTION_EXITS)
				{
					isChangeSubOffer = true;
					break;
				}
			}
			if(isChangeSubOffer == true)
			{
				curOfferData.put("OPER_CODE", ACTION_UPDATE);
			}
			else
			{
				curOfferData.put("OPER_CODE", ACTION_EXITS);
			}
		}
		else
		{//没有子商品
			curOfferData.put("OPER_CODE", ACTION_EXITS);
		}
	}
}

function submitDL100SubOfferData(el){
	//顶层商品编码
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	
	//当前设置的商品的编码
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	
	//顶层商品数据对象
	var offerData = PageData.getData($("#class_"+topOfferId));
	
	//当前设置的商品的数据对象
	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));

	var curOptionDl100OfferDataset = curOfferData.get("POWER100_PRODUCT_INFO");
	if(typeof(curOptionDl100OfferDataset) != "undefined" && curOptionDl100OfferDataset.length > 0)
	{
		offerData.put("POWER100_PRODUCT_INFO", curOptionDl100OfferDataset);
	}
	else
	{
		offerData.removeKey("POWER100_PRODUCT_INFO");
	}
	
	var curOptionOfferDataset = curOfferData.get("SUBOFFERS");
	if(typeof(curOptionOfferDataset) != "undefined" && curOptionOfferDataset.length > 0)
	{
		offerData.put("SUBOFFERS", curOptionOfferDataset);
	}
	else
	{
		offerData.removeKey("SUBOFFERS");
	}
	
	//根据商品对象设置预览区域的展示
	dealPreviewByOfferData(curOfferData);
	
	$("#li_"+curOfferId+" > div").eq(0).find(".side").css("display", "none");
	//根据商品对象设置预览区域的展示
	dealDLOptOfferPreviewByOptOfferData(curOptionDl100OfferDataset,curOfferId);
	
	//保存数据对象
	PageData.setData($("#class_"+topOfferId), offerData);
	//将商品设置区域开关关闭
	$("#prodDiv_SWITCH").val("off");
	if(window.offerProductCallback)
	{
		window.offerProductCallback.call(this, offerData);
	}
	
	//手动刷新scroller组件
	editMainScroll.refresh();
	
	hidePopup(el);
	
	return;
}

//根据商品对象设置预览区域的展示
function dealEcPackagePreviewByOfferData(curOfferData)
{
	
	var topOfferId = curOfferData.get("OFFER_ID");

	//将商品上的（待设置）标签隐藏
	$("#li_ec_package> div").eq(0).find(".side").css("display", "none");

	var preViewOptOfferIds = $("input[name=oo_ec_package_OPTOFFER_ID]");
	
	for(var j = 0; j < preViewOptOfferIds.length; j++)
	{
		$("#div_"+preViewOptOfferIds[j].value).remove();
	}
	
	if($("#optOffer_ec_package").children().length == 0)
	{
		$("#sub_ec_package").remove();
	}
	var curOptionOfferDataset = curOfferData.get("EC_PACKAGE_INFO");
	if(typeof(curOptionOfferDataset) != "undefined")
	{
		for(var i = 0; i < curOptionOfferDataset.length; i++)
		{
			var curOptOfferData = curOptionOfferDataset.get(i);
			var curOfferId = curOptOfferData.get("OFFER_ID");
			var curOfferName = curOptOfferData.get("OFFER_NAME");
			var isShowTag = curOptOfferData.get("IS_SHOW_SET_TAG");
			var subOfferIndex = curOptOfferData.get("OFFER_INDEX");
			
			if(curOptOfferData.get("OPER_CODE") != ACTION_DELETE)
			{
				if($("#sub_ec_package").length == 0)
				{
					var subHtml = "<div class='sub' id='sub_ec_package'><div class='main' >" 
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>" 
						+ "<ul id='optOffer_ec_package'>";
						+ "</ul></div></div>";
					
					$("#li_ec_package").append(subHtml);	
				}
				
				var optionalOfferHtml = "<li id='div_"+curOfferId+"'><div class='group link' ontap='openEnterpriseOfferPopupItem("+curOfferId+","+topOfferId+",false,"+subOfferIndex+");'>" 
					+ "<div class='main'>" + curOfferName + "</div>";
				if(isShowTag == "true")
				{
					optionalOfferHtml += "<div class='side'><span class='e_tag e_tag-red'>待设置</span></div>";
				}
				optionalOfferHtml +=  "<div class='more'></div>" 
					+ "<input type='hidden' name='oo_ec_package_OPTOFFER_ID' value='" + curOfferId + "'/>" 
					+ "</div>";
				
				if($("#"+curOfferId).attr("SELECT_FLAG") != "0")
				{
					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteOptionalOffer(" + curOfferId + "," + topOfferId + ");'></span></div>";
				}
				else
				{
					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='fn e_dis e_ico-delete' /></div>";
				}
				optionalOfferHtml=optionalOfferHtml + "</li>";
				$("#optOffer_ec_package").append(optionalOfferHtml);
				
			}
		}
	}

}

//校验选择的组内商品是否满足商品组最大最小限制
function checkSelOfferNum()
{
	var flag = true;
	//顶层商品编码
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();	
	//顶层商品数据对象
	var offerData = PageData.getData($("#class_"+topOfferId));
	var groupIds = $("#prodDiv_GROUP_IDS").val();
	var brand = offerData.get("BRAND_CODE");
	var operType = $("#cond_OPER_TYPE").val();
	 if ("BOSG" == brand && operType == "DstUs") {
		 return true;
	 }
	
	 if ("BOSG" == brand && operType == "CrtUs") {
         var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
         
    	var  subOffer = curOfferData.get("SUBOFFERS");
    	if(!subOffer){
    			
    		MessageBox.alert("提示信息", "请选择产品！");
    		return false;

    	}
    		    
         if ("10" == curOfferData.get("MERCHP_OPER_TYPE")) {
             return true;
         }
     }
	if(!groupIds)
	{//当前商品没有商品组，则不校验
		return true;
	}
	//bboss暂停或恢复时，不检验
	var merchOperType = $("#OPERTYPE").val();
	if(merchOperType == "3" || merchOperType == "4")
	{
		return true;

	}
	var checkGroupDataset = new Wade.DatasetList();
	var groupOfferData = PageData.getData($(".e_SetSelGroupOfferPart"));
	var groupIdArr = groupIds.split("@");
	//bboss产品商品组内不存在任何资费时不校验，如农信通商品下的产品
	if ("BOSG" == brand && operType == "CrtUs" && topOfferId=="110000009982") {
		if(groupOfferData.length==1 && groupIdArr.length==1){
			var data = groupOfferData.get(groupIdArr[0]);
			var selOffers = data.get("SEL_OFFER", new Wade.DatasetList());
			if(selOffers.length==0){
				return true;
			}
		}
	}
	for(var i = 0, size = groupIdArr.length; i < size; i++)
	{
		var data = groupOfferData.get(groupIdArr[i]);
		if(data)
		{
			checkGroupDataset.add(data);
		}
	}
	var offerIds = new Array();
	for(var i = 0, size = checkGroupDataset.length; i < size; i++)
	{
		var group = checkGroupDataset.get(i);

		var selOffers = group.get("SEL_OFFER", new Wade.DatasetList());
		var selNum = selOffers.length;
		var selectFlag = group.get("SELECT_FLAG");
		var maxNum = group.get("MAX_NUM");
		var minNum = group.get("MIN_NUM");
		var mustSelOffer = group.get("MUST_SEL_OFFER");
		var groupName = group.get("GROUP_NAME");
		var limitType = group.get("LIMIT_TYPE");
		var typeName="";
		if("D"==limitType){
			typeName="资费类";
		}else if("S"==limitType){
			typeName="服务类";
		}
		var selCheckNum = 0; //已选择的需要校验的子商品个数，对应pm_group的limit_type
		var hasSvc ="0";
		for(var a = 0, sizea = selOffers.length; a < sizea; a++)
		{
			if(limitType && limitType == selOffers.get(a).get("OFFER_TYPE"))
			{
				selCheckNum++;
			}else if(!limitType){
				selCheckNum++;
			}
			//融合vpmn业务校验
			if(operType == "CrtMb" || operType == "ChgMb"){
				var elementData = selOffers.get(a);
				var elementType = elementData.get("OFFER_TYPE");
	 			var elementId = elementData.get("OFFER_ID");
	    		if(elementType=="S" && elementId == "120000000861") {
	 				hasSvc ="1";
	 				
	 			}
			}
			offerIds.push(selOffers.get(a).get("OFFER_ID"));
		}
		//融合vpmn业务校验
		if(group.get("GROUP_ID") == "33001971"){
			var shortNumberInput = $('#pam_SHORT_CODE').val();
			if(hasSvc =="1" && shortNumberInput == ""){
				MessageBox.alert("提示信息","短号服务与短号必须同时存在，请填写短号！");
				return false;
			}
			if(hasSvc =="0" && shortNumberInput != ""){
				MessageBox.alert("提示信息","没有选择短号服务，请选择短号服务！");
				return false;
			}
		}
		
		if(selCheckNum < parseInt(minNum) && parseInt(minNum) > 0)
		{//包内最大订购数小于0的不做校验
			MessageBox.alert("提示信息", "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！");
			flag = false;
			return false;
		}
		
		if(selectFlag == 0 && selNum == 0)
		{
			MessageBox.alert("提示信息", "商品组["+groupName+"]是必选的，请至少选择一个组内元素！");
			flag = false;
			return false;
		}
		
		if(selCheckNum > 0 && selCheckNum > parseInt(maxNum) && parseInt(maxNum) > 0)
		{
			MessageBox.alert("提示信息", "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！");
			flag = false;
			return false;
		}
		
		if(mustSelOffer)
		{
			var mustSelOfferArr = mustSelOffer.split("@");
			for(var z = 0, sizeI = mustSelOfferArr.length; z < sizeI; z++)
			{
				var selFlag = false; //组内必选商品是否被选中
				for(var j = 0, sizeJ = selOffers.length; j < sizeJ; j++)
				{
					if(mustSelOfferArr[z] == selOffers.get(j).get("OFFER_ID"))
					{
						selFlag = true;
						break;
					}
				}
				if(!selFlag)
				{
					MessageBox.alert("提示信息", "您已选择["+groupName+"]组内商品"+mustSelOfferArr[z]+"是必选商品，选择了该组的商品必须同时订购"+mustSelOfferArr[z]+"商品！");
					flag = false;
					return false;
				}
			}
		}

	}
	if(offerIds.indexOf("120000017000")>-1){
		//非彩信资费
		var  mmsPostages = [
			"130000002500","130000002501","130000002502","130000002503",
			"130000002504","130000002507","130000003303","130000003304",
			"130000906040","130000906041","130000906042","130000906068",
			"130000906069","130000906070","130000906071","130000906120",
			"130000906121","130000906122","130009900101","130009900102",
			"130009900103","130009900104","130009900105","130009900106",
			"130009900205","130009900206","130009950103","130009950106"
		];
		var len = offerIds.length;
		for(var i = 0; i < len; i++) {
			if(mmsPostages.indexOf(offerIds[i])>-1)	{
				MessageBox.alert("提示信息", "WAPPUSH服务类只能办理彩信资费！");
				flag = false;
				return false;
			}
		}
	}
	return flag;
}

//取消本次商品设置
function cancleSubOfferPopupItem(el)
{
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	var offerId = $("#prodDiv_OFFER_ID").val();
	var offerIndex = $("#prodDiv_OFFER_INDEX").val();
	var offerData = getOfferData(topOfferId, offerId, offerIndex);
	var offerDataStr = offerData.toString();
	var curOfferData = PageData.getData($(".e_SetOfferPart"));
	var curOfferDataStr = curOfferData.toString();
	if(offerDataStr == curOfferDataStr)
	{//如果offerdata相等，则商品设置区域没有任何操作，可以直接关闭
		$("#prodDiv_SWITCH").val("off");
		$("#prodDiv_USETAG").val("0");
		$("#prodDiv_OFFER_ID").val("");
		$("#prodDiv_TOP_OFFER_ID").val("");
		$("#prodDiv_OFFER_INDEX").val("");
		PageData.setData($(".e_SetOfferPart"), new Wade.DataMap());
		hidePopup(el);
	}
	else
	{
		MessageBox.confirm("提示信息", "是否确定取消商品设置？取消后，本次设置的商品信息将无法保存。", function(btn){
			if(btn == "cancel")
			{
				return;
			}
			
			$("#prodDiv_SWITCH").val("off");
			$("#prodDiv_USETAG").val("0");
			$("#prodDiv_OFFER_ID").val("");
			$("#prodDiv_TOP_OFFER_ID").val("");
			$("#prodDiv_OFFER_INDEX").val("");
			PageData.setData($(".e_SetOfferPart"), new Wade.DataMap());
			hidePopup(el);
		});
	}
	
}

//获取商品对象，不带SUBOFFERS结构(入参：顶层商品编码，当前商品编码)
function getOfferData(topOfferId, offerId, offerIndex)
{
	if(topOfferId == offerId)
	{
		var offerData = PageData.getData($("#class_"+offerId));

		return offerData;
	}
	else if(topOfferId != offerId && offerIndex != 0)
	{
		var offerData = PageData.getData($("#class_"+topOfferId));
		var subOffers = offerData.get("SUBOFFERS");
		for(var i = 0; i < subOffers.length; i++)
		{
			var subOffer = subOffers.get(i);
			if(subOffer.get("OFFER_INDEX") == offerIndex && subOffer.get("OFFER_ID") == offerId)
			{
				return subOffer;
			}
		}
		return new Wade.DataMap();
	}
	else
	{
		var offerData = PageData.getData($("#class_"+topOfferId));
		var subOffers = offerData.get("SUBOFFERS");
		for(var i = 0; i < subOffers.length; i++)
		{
			var subOffer = subOffers.get(i);
			if(subOffer.get("OFFER_ID") == offerId)
			{
				return subOffer;
			}
		}
		return new Wade.DataMap();
	}
}

//根据商品类型获取子商品列表
function getSubOffersByOfferType(subOfferList, offerType)
{
	var subOffers = new Wade.DatasetList();
	for(var i = 0, size = subOfferList.length; i < size; i++)
	{
		var subOffer = subOfferList.get(i);
		if(offerType == subOffer.get("OFFER_TYPE"))
		{
			subOffers.add(subOffer);
		}
	}
	return subOffers;
}

function initPageParamCommon()
{
//	var prodSpecId = $("ul[id=offerChaSpecUL]").children().eq(0).attr("PROD_SPEC_ID");
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var checkfunctionNameOfferId = "initPageParam_" + curOfferId;
	var checkfunctionName = "initPageParam";
	if($.isFunction(window[checkfunctionNameOfferId]))
	{
		window[checkfunctionNameOfferId]();
	}
	else if($.isFunction(window[checkfunctionName])){
		window[checkfunctionName]();
	}
}

function initOfferChaSpec(offerChaSpecs)
{
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var checkfunctionNameOfferId = "initProdChaSpec_" + curOfferId;
	var checkfunctionName = "initProdChaSpec";
	if($.isFunction(window[checkfunctionNameOfferId]))
	{
		window[checkfunctionNameOfferId](offerChaSpecs);
	}
	else if($.isFunction(window[checkfunctionName])){
		if(typeof(offerChaSpecs) != "undefined"){
			window[checkfunctionName](offerChaSpecs);
		}
	}
}


function isSwitchOff()
{
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	var offerId = $("#prodDiv_OFFER_ID").val();
	var offerIndex = $("#prodDiv_OFFER_INDEX").val();
	var offerData = getOfferData(topOfferId, offerId, offerIndex);
	var offerDataStr = offerData.toString();
	var curOfferData = PageData.getData($(".e_SetOfferPart"));
	var curOfferDataStr = curOfferData.toString();
	var showtag=offerData.get("IS_SHOW_SET_TAG");

	if(offerDataStr == curOfferDataStr)
	{//如果offerdata相等，则商品设置区域没有任何操作，可以直接关闭
		$("#prodDiv_SWITCH").val("off");
		$("#prodDiv_USETAG").val("0");
	}
	
	if($("#prodDiv_SWITCH").val() == "off")
	{
		return true;
	}
	else
	{
		MessageBox.alert("提示信息", "请点击【确定】或【取消】按钮确认商品设置！");
		return false;
	}
	return true;
}

//检查level=1的popupGroup中是否有已经打开的popupItem
function checkPopupClose()
{
//	$(".c_popupGroup").each(function(){
//		alert($(this).attr("level"));
//	});
	
//	var flag = false;
//	$("div[class='c_popupGroup'][level=1][id!='CalendarPart']").children().each(function(){
//		if($(this).attr("visible") == true)
//		{
//			flag = true;
//		}
//	});
//	alert(flag);
}
/**
获取bboss成员产品的offer_inst_id
**/
function  getBBossMerchpMebSubofferInstId(subOfferDataset){
    if(subOfferDataset.length<=0){
		return "";
	}
	for(var i=0;i<subOfferDataset.length;i++){
		var suboffer=subOfferDataset.get(i);
		var offerType=suboffer.get("OFFER_TYPE");
		if("P"==offerType){
			var offerInstId=suboffer.get("OFFER_INS_ID");
			return offerInstId;
		
		}
	}

}

function  hasMerchOptCode(isMainOffer, offerIndex, topOfferId, offerId) {
    var merchpOperType ="";
    if(!isMainOffer && "BOSG" == PageData.getData($("#class_" + topOfferId)).get("BRAND_CODE")) {
        if("CrtUs" == $("#cond_OPER_TYPE").val() || "ChgUs" == $("#cond_OPER_TYPE").val()) {
            // 主界面初始化的时候不会加上index
            if($("#div_"+offerId).children().length != 0){
                if(!$("#div_"+offerId).children()[0].getAttribute("prodOperType")) {
                    MessageBox.alert("请先填写商品信息，再填写子商品信息。");
                    return "-1";
                }
            }

            merchpOperType = $("#div_"+offerId + "_" + offerIndex).children()[0].getAttribute("prodOperType");
            if(!merchpOperType) {
                MessageBox.alert("请先填写商品信息，再填写子商品信息。");
                return "-1";
            }
        }
    }
    return merchpOperType;
}

//根据之前选择的操作类型，加载子商品，以免误操作导致选择的操作类型与实际操作不符，导致考核
function isBBOSSDealSubOffers(isMainOffer,mainOfferBrand) {
	//如果还没有选择bboss的商品操作类型，返回
	//如果不是bboss业务，返回
	var merchOperCode = $("#OPERTYPE").val();
	if(!merchOperCode || "BOSG" != mainOfferBrand){
		return;
	}
	//如果是子商品设置，返回(只有bboss主商品需要设置)
	if(!isMainOffer){
		return;
	}

    var operCode = $("#cond_OPER_TYPE").val();
    // 成员业务暂时不做处理
    if (operCode == "CrtMb" || operCode == "ChgMb" || operCode == "DstMb") {
        return;
    }
	//如果管理节点，返回
	var flowMainId = $("#FLOW_MAIN_ID").val();
	if(flowMainId){
		return;
	}
	dealProdListByMerchOperCode();
}

/**
 * bboss把前台没显示属性过滤
 * @param arg
 * @returns {Boolean}
 */
function isBbossNull(arg) {

    if(!arg && arg!==0 && typeof arg!=="boolean")
    {
        return true ;
    }
    //不显示的属性不提交到后台
    if($("#"+arg).attr("style")=="display:none;" || $("#"+arg).attr("style")=="display: none;")
    {
        return true ;
    }    

    //去掉select为none的,兼容ie8的情况
    if($("#"+arg+"_span").children.length>0&&($("#"+arg+"_span").attr("style")=="display:none;" || $("#"+arg+"_span").attr("style")=="DISPLAY: none")){
        return true ;
    }
    //去掉select为none的,兼容ie8的情况
    if($("#"+arg+"_span").children.length>0&&($("#"+arg+"_span").attr("style")=="display: none;" || $("#"+arg+"_span").attr("style")=="DISPLAY: none")){
        return true ;
    }

    return false;
}

//剔除BBOSS产品属性是上传主键并且隐藏的数据
function removeUpload(elemetnId,simpleupload,chaSpecObj){
	
	if(!elemetnId && elemetnId!==0 && typeof elemetnId!=="boolean")
    {
          return true;
    }
	
	if("simpleupload"==simpleupload){
    	var style = chaSpecObj.parentNode.style.display;
    	if("none"==style){
    		return true;
    	} 
    }
}

function submitBbossOfferChaSpec(el){
    if (!$.validate.verifyAll("productChaSpecPopupItem")) {
        return false;
    }
    return submitOfferChaSpec(el);
    
}

/**
 * 是否含有静态参数页面
 * @returns {Boolean}
 */
function isHasStaticParamPage() {
  var offerId = $("#cond_OFFER_ID").val();
  var staticPageParm = getStaticPageParam(offerId);
  if (staticPageParm && staticPageParm.subpage) {
    return true;
  }
  return false;
}

var _cachedStaticPageParam = {};

/**
 * 获取静态页面参数
 * @param offerId
 * @returns
 */
function getStaticPageParam() {
	var mainOffer = PageData.getData($(".e_SelectOfferPart"));
	var mainOfferId = $("#prodDiv_OFFER_ID").val(); //mainOffer.get("OFFER_ID");
	
	var operType = $("#cond_OPER_TYPE").val();
	// 缓存操作 
	var urlParams = "";
	if (mainOfferId) {
		urlParams += "&OFFER_ID="+mainOfferId;
	}
	if("110000008018" == $("#cond_OFFER_ID").val()){
		if("CrtUs" == operType){
			if("2" == $("#cond_IF_CENTRETYPE").val() || 2 == $("#cond_IF_CENTRETYPE").val()){
				operType = operType + "Cent";
			}
		}else if("ChgUs"==operType){
			var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
			var subInsId = curOfferData.get("USER_ID");
			urlParams += "&USER_ID="+subInsId;
		}else if("CrtMb"==operType || "ChgMb"==operType){
			urlParams += "&USER_ID="+$("#cond_EC_USER_ID").val();
		}
	}
	if (operType) {
		urlParams += "&OPER_TYPE="+operType;
	}
	urlParams += "&GROUP_ID="+$.enterpriseLogin.getInfo().get("GROUP_ID");
	urlParams += "&MEB_SERIAL_NUMBER="+$.enterpriseLogin.getInfo().get("GROUP_ID");
	var cahce = _cachedStaticPageParam[mainOfferId];
	if ( cahce!=undefined) {
		var n = 0;
        for(var i in _cachedStaticPageParam) {n++;}
        if (n > 16) {
        	_cachedStaticPageParam = {};
        }
        if(urlParams == cahce.urlParams){
        	return cahce;
        }
	}
	// 先取后台获取页面配置
	var pageParam;
	$.beginPageLoading();
	$.ajax.submit("", "queryProdParam", urlParams, "", function(json) {
		$.endPageLoading();
		var dynamicPage=json.get("subpage")
		if(dynamicPage!=""&&dynamicPage!=undefined){
			pageParam = JSON.parse(json);
			pageParam.urlParams = urlParams;
			_cachedStaticPageParam[mainOfferId] = pageParam;
		}
	}, function(error_code,error_info,derror) {
		$.endPageLoading();
    },{async:false});
  
	return pageParam;
}

/**
 * 显示静态参数
 * @param el
 * @param pageParam
 * @returns
 */
function showStaticPage(el, pageParam) {
	dynamicOfferParam.request(pageParam,function(){
		$("#proParamPage").css("display", "none");
	    $("#dynamicOfferParam").css("display", "");
	    forwardPopup(el, 'productChaSpecPopupItem');
	});
}

/**
 * 隐藏静态参数
 * @returns
 */
function hideStaticPage(){
	$("#dynamicOfferParam").css("display", "none");
}

//获取当月最后一天
function getLastDayOfCurrentMonth()
{
	var currentDate = new Date();
	var lastDate = new Date(currentDate.getFullYear(), currentDate.getMonth()+1, 0);
	var fullYear = lastDate.getFullYear();
	var month = lastDate.getMonth() + 1;
	month = month > 9 ? month : "0"+month;
	var day = lastDate.getDate();
	return fullYear + "-" + month + "-" + day;
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

//获取当前系统时间，格式yyyy-MM-dd HH:MM:SS
function getNowSysTime() 
{
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9)
    {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) 
    {
        strDate = "0" + strDate;
    }
    var hour = date.getHours();
    if (hour >= 0 && hour <= 9) 
    {
    	hour = "0" + hour;
    }
    var minutes = date.getMinutes();
    if (minutes >= 0 && minutes <= 9) 
    {
    	minutes = "0" + minutes;
    }
    var seconds = date.getSeconds();
    if (seconds >= 0 && seconds <= 9) 
    {
    	seconds = "0" + seconds;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + hour + seperator2 + minutes
            + seperator2 + seconds;
    return currentdate;
}

function getMaxPOfferIndex()
{
	var offerIndexArr = new Array();
	$("ul[id=priceOfferUL] li").each(function(){
		offerIndexArr.push($(this).attr("offerIndex"));
	});
	if(offerIndexArr.length == 0)
	{
		return 1;
	}
	else
	{
		var maxOfferIndex = Math.max.apply(Math, offerIndexArr);
		
		return maxOfferIndex + 1;
	}
}


function addMoreSubOffers(e){
	
	var offer_id = $(e).attr("OFFER_ID");
	var offer_name =  $(e).attr("OFFER_NAME");
	var brand = $(e).attr("BRAND_CODE");
	var select_flag = $(e).attr("SELECT_FLAG");
	
	var optionalOfferUL =  $("li","#productOfferUL");
	var i = optionalOfferUL.length;
	var offerIndex = offer_id.toString()+i;
	var id = offer_id +"_"+i;
	var optionOfferHtml = "<div class='main'>"
		+ "<div class='title'>" 
		+ offer_name
		+ "</div></div>"
		+ "<div class='fn'>"
		+ "<input class='e_checkbox' type='checkbox' disabled='true' checked ='checked' name='selProductOffer'" 
		+ " id=' "+ id + "' OFFER_ID ='"+offer_id 
		+ "' OFFER_NAME='"+ offer_name + "' BRAND='"+ brand + "' SELECT_FLAG='" + select_flag + "' />"
		+ "</div>"
		+ "<div class='fn'>" 
		+ "<span class='e_ico-delete' ontap='delMoreSubOffers(this)' desc='删除产品' flag='" +i + "' OFFER_INDEX='"+ offerIndex 
		+ "' OFFER_ID ='"+offer_id+"' OFFER_NAME='"+ offer_name + "' BRAND='"+ brand + "' SELECT_FLAG='" + select_flag + "' />"
		+ "</div>"
		+ "<div class='c_line'></div>";
	$("#productOfferUL").append({tag:"li",id:"add_"+id,html:optionOfferHtml});
	
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOffers = offerData.get("SUBOFFERS");	
	
//	var subOfferData = new Wade.DataMap();
//	subOfferData.put("OFFER_ID", offer_id);
//	subOfferData.put("OFFER_NAME", offer_name);
//	subOfferData.put("OFFER_INDEX", offerIndex);
//	subOfferData.put("BRAND_CODE", brand);
//	subOfferData.put("OPER_CODE", ACTION_CREATE);
	
	var subOfferData = PageData.getData($("#SUBOFFER_DATA_"+offer_id));
	subOfferData.put("OFFER_INDEX", offerIndex);
	subOfferData.removeKey("SELECT_FLAG");
//	subOfferData.removeKey("IS_SHOW_SET_TAG");
	subOffers.add(subOfferData);
	
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}

function delMoreSubOffers(e)
{
	var flag = $(e).attr("flag");
	var offerId = $(e).attr("OFFER_ID");
	var offerIndex = $(e).attr("OFFER_INDEX");
	var topOfferId = $("#prodDiv_TOP_OFFER_ID").val();
	
	$("#add_"+offerId +"_"+flag).remove(); //新增子商品删除
	
	//删除数据结构
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var offers = offerData.get("SUBOFFERS");
	
	for(var i = offers.length; i > 0; i--)
	{
		var offer = offers.get(i-1);
		if(offer.get("OFFER_INDEX") == offerIndex)
		{
			offers.removeAt(i-1);
		}
	}
	if(offers.length == 0)
	{
		offerData.remove(offers);
	}
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}
//勾选产品类子商品事件
function checkedProductOffer(obj)
{
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var brand = offerData.get("BRAND_CODE");
	var key = "SUBOFFERS";//处理子商品信息
	if("DLBG" == brand){
		key = "POWER100_PRODUCT_INFO";//动力100子商品放在这个结构里
	}
	
	var subOfferDataset = offerData.get(key);
	if(typeof(subOfferDataset) == "undefined" || subOfferDataset.length == 0)
	{
		subOfferDataset = new Wade.DatasetList();
		offerData.put(key, subOfferDataset);
	}
	
	var isChecked = obj.checked;
	var productOfferId = $(obj).closest("li").attr("offerId");
	var productOfferIndex = $(obj).closest("li").attr("offerIndex");
	
	if(isChecked)
	{
		var addData = true;
		for(var i = subOfferDataset.length; i > 0; i--)
		{
			var subOfferData = subOfferDataset.get(i-1);
			var subOfferId = subOfferData.get("OFFER_ID");
			var subOfferIndex = subOfferData.get("OFFER_INDEX");
			var subOfferInsId = subOfferData.get("OFFER_INS_ID");
			if(productOfferId == subOfferId && productOfferIndex == subOfferIndex)
			{
				if(subOfferData.get("OPER_CODE") == ACTION_DELETE && typeof(subOfferInsId) != "undefined")
				{//已删除的商品实例数据
					subOfferData.put("OPER_CODE", ACTION_EXITS); // 3-不变
					addData = false;
				}
			}
		}
		if(addData)
		{
			var subOffer = PageData.getData($("#SUBOFFER_DATA_"+productOfferId+"_"+productOfferIndex));
			subOffer.removeKey("SELECT_FLAG");
			subOfferDataset.add(subOffer);
			
			//$.feeMgr
			if(subOffer.get("OPER_CODE")==ACTION_CREATE){
				var feeData = subOffer.get("FEE_DATA");
				if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
					var feeSize = feeData.length;
					for(var j=0;j<feeSize;j++){
						var fee = feeData.get(j);	
						var tradeTypeCode = fee.get("TRADE_TYPE_CODE").toString();
						var feeValue = fee.get("FEE").toString();
						if("0"==feeValue && (tradeTypeCode=="4690" || tradeTypeCode =="4691" || tradeTypeCode == "4693")){
	    					var remark =subOffer.get("OFFER_NAME").toString();
	    					var money = prompt(remark+"应缴额度为(单位：分):","0");
	    					fee.put("FEE",money);
						}
						$.feeMgr.insertFee(fee);
					}
				}
			}
		}
	}
	else
	{
		for(var i = subOfferDataset.length; i > 0; i--)
		{
			var subOfferData = subOfferDataset.get(i-1);
			var subOfferId = subOfferData.get("OFFER_ID");
			var subOfferIndex = subOfferData.get("OFFER_INDEX");
			var subOfferInsId = subOfferData.get("OFFER_INS_ID");
			if(productOfferId == subOfferId && productOfferIndex == subOfferIndex)
			{
				if(typeof(subOfferInsId) == "undefined")
				{//非实例数据，直接删除
					//$.feeMgr
					var feeData = subOfferData.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);	
	    					$.feeMgr.deleteFee(fee);
	    				}
	    			}
	    			subOfferDataset.remove(subOfferData);
				}
				else
				{//实例数据，修改状态
					subOfferData.put("OPER_CODE", ACTION_DELETE); // 1-删除
				}
			}
		}
	}
	//保存商品数据
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}

//产品重复订购
function repeatOrderProductOffer(el)
{
	var brand = $("#cond_EC_BRAND").val();
	var operType = $("#cond_OPER_TYPE").val();
	
	if("BOSG" == brand && operType == "DstUs"){		        
		MessageBox.alert("该产品操作下不允许新增产品！");
        return;				
	}
	
	var offerId = $(el).closest("li").attr("offerId");
	var offerIndex = $(el).closest("li").attr("offerIndex");
	
	//获取被复制的产品类商品数据对象
	var subOfferData = new Wade.DataMap($("#SUBOFFER_DATA_"+offerId+"_"+offerIndex).text());
	
	//获取产品类商品最大序号
	var newOfferIndex = getMaxOfferIndex("productOfferUL");
	subOfferData.put("OFFER_INDEX", newOfferIndex);
	subOfferData.removeKey("SELECT_FLAG");
	
	loadRepeatProductOfferHtml(subOfferData);
	
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var subOffers = offerData.get("SUBOFFERS");
	subOffers.add(subOfferData);
	PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
}

//加载重复订购的产品类商品html
function loadRepeatProductOfferHtml(productOffer)
{
	var newOfferIndex = productOffer.get("OFFER_INDEX");
	
	var productOfferHtml = "<li offerId="+productOffer.get("OFFER_ID")+" offerIndex="+newOfferIndex+" repeatOrder="+productOffer.get("REPEAT_ORDER")+">";
	productOfferHtml += "<div class='main'>";
	productOfferHtml += "<div class='title'>" + productOffer.get("OFFER_NAME") + "</div>";
	productOfferHtml += "</div>";
	productOfferHtml += "<div class='fn'>";
	productOfferHtml += "<input name='selProductOffer' id='"+productOffer.get("OFFER_ID")+"_"+newOfferIndex+"' type='checkbox' class='e_checkbox' checked='checked' ontap='checkedProductOffer(this);'/>";
	productOfferHtml += "</div>";
	productOfferHtml += "<div class='fn'>";
	productOfferHtml += "<span desc='新增产品' class='e_ico-add' ontap='repeatOrderProductOffer(this)'></span>";
	productOfferHtml += "</div>";
	productOfferHtml += "<span id='SUBOFFER_DATA_"+productOffer.get("OFFER_ID")+"_"+newOfferIndex+"' class='e_SubOfferPart' desc='子商品数据结构' style='display:none' name='SUBOFFER_DATA'>";
	productOfferHtml += productOffer;
	productOfferHtml += "</span>";
	productOfferHtml += "<div class='c_line'></div>";
	productOfferHtml += "</li>";
	$("#productOfferUL").append(productOfferHtml);
}

function getMaxOfferIndex(ulObjId)
{
	var offerIndexArr = new Array();
	$("ul[id="+ulObjId+"] li").each(function(){
		offerIndexArr.push($(this).attr("offerIndex"));
	});
	if(offerIndexArr.length == 0)
	{
		return 1;
	}
	else
	{
		var maxOfferIndex = Math.max.apply(Math, offerIndexArr);
		
		return maxOfferIndex + 1;
	}
}

//加载商品设置区域重复订购的产品类商品
function loadRepeatProductOffer(productOfferList, repeatOrderOfferIdData)
{
	repeatOrderOfferIdData.eachKey(function(item){
		//获取隐藏域的offerdata
		var subOfferData = new Wade.DataMap($("#productOfferUL").find("span[id*=SUBOFFER_DATA_"+item+"_]").text());
		if(subOfferData.length > 0)
		{
			var subOfferId = subOfferData.get("OFFER_ID");
			var subOfferIndex = subOfferData.get("OFFER_INDEX");
			var isDelHtml = false;
			for(var i = 0, size = productOfferList.length; i < size; i++)
			{
				var productOffer = productOfferList.get(i);
				if(item == productOffer.get("OFFER_ID"))
				{//根据已选择的产品类商品重新加载商品设置区域的产品列表
					subOfferData.put("OFFER_INDEX", productOffer.get("OFFER_INDEX"));
					loadRepeatProductOfferHtml(subOfferData);
					isDelHtml = true;
				}
			}
			if(isDelHtml)
			{//删除原来的html
				$("#li_"+subOfferId+"_"+subOfferIndex).remove();
			}
		}
		
	});
}

//用于区分白名单功能状态和个人通话阀值是否立即生效是否修改
var changeA = 0;
var changeB = 0;

//校验不同操作状态下的应该携带的入参--成员商品受理
function checkparamopertype(){
	var offercode = $("#prodDiv_OFFER_OPER_CODE").val();

	var proParamPagecom = $("#proParamPage li").length;
	var offerId = $("#prodDiv_OFFER_ID").val();
	
if(offerId == "120099011019"){
	if(offercode == "0"){
		if(proParamPagecom !=0){
			$("#proParamPage li").each(function(){
				var idval = $(this).find("input").attr("id");
               if(idval == "pam_userWhiteNumOperType"||idval == "pam_userWhiteNum"||idval == "pam_userAreaOperType"||idval == "pam_userArea")
               {
            	  
            	   $(this).css("display","none");
               }
		});
			
		}
		
	}
	else if(offercode == "3"){
		if(proParamPagecom !=0){
			$("#proParamPage li").each(function(){
				var idval = $(this).find("input").attr("id");
               if(idval == "pam_userAreaOperType"||idval == "pam_userArea")
               {
            	   $(this).css("display","none");
               }
		});
			
		}
		
		$("#pam_userAreaFlag").change(function(){
			var userAreaFlag = $("#pam_userAreaFlag").val();
			console.log("ddd："+userAreaFlag)
			if(userAreaFlag =="1"){
				if(proParamPagecom !=0){
					$("#proParamPage li").each(function(){
						var idval = $(this).find("input").attr("id");
		               if(idval == "pam_userAreaOperType"||idval == "pam_userArea")
		               {
		            	   $(this).css("display","");
		               }
				});	
				}
			}else{
				if(proParamPagecom !=0){
					$("#proParamPage li").each(function(){
						var idval = $(this).find("input").attr("id");
		               if(idval == "pam_userAreaOperType"||idval == "pam_userArea")
		               {   $("#pam_userAreaOperType").val("");
		                   $("#pam_userArea").val("");
		            	   $(this).css("display","none");
		               }
				});	
				}
			}
		});

		changeA = 0;
		changeB = 0;
		$("#pam_userClassFlag").change(function(){
			changeB = 1;
		});
		$("#pam_userWhiteNumFlag").change(function(){
			changeA = 1;
	});
	}
  }
}
var UserClassOperTypeFrist = $("#UserClassOperType").attr("old_value");
//校验不同操作状态下的应该携带的入参--商品受理业务
function checkparamopertypeByUsr(){
	debugger;
	
	var offercode = $("#prodDiv_OFFER_OPER_CODE").val();

	var proParamPagecom = $("#proParamPage li").length;
	var offerId = $("#prodDiv_OFFER_ID").val();

 if(offerId == "120099011015"){
	if(offercode == "0"){
		if(proParamPagecom !=0){
			$("#proParamPage li").each(function(){
				var idval = $(this).find("input").attr("id");
               if(idval == "UserClassOperType"||idval == "UserClass"||idval == "UserAccount"||idval == "BlackNumOperType"||idval == "BlackNum"||idval == "SysSpecialNumOperType"||idval == "SysSpecialNum")
               {
            	   $(this).css("display","none");
               }
		});
			$("#GroupAccount").val("0");
		}
	}else if(offercode == "3"){
		UserClassOperTypeFrist = $("#UserClassOperType").attr("old_value");
	}
 }
}

//校验资费不允许在产品信息页面取消
function validateDiscntPage()
{
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var offers = offerData.get("SUBOFFERS");
	for (var i = 0; i < offers.length; i++)
	{
		if(offers.get(i, "OPER_CODE")=="1" && offers.get(i, "OFFER_CODE")!="5911" && offers.get(i, "OFFER_CODE")!="5912")
	 	 {
			MessageBox.error("资费还有学生关联，请通过和校园成员参数页面退订资费！");
	 		return false;
	 		break;
	 	 }

	}
	return true;
}
