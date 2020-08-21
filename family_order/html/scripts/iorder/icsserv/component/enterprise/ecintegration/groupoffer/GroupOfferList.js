//打开商品组商品列表
function openGroupOfferListPopupItem(el, ecMebType)
{
	var ecProductId = $(el).attr("EC_PRODUCT_OFFER_ID");
	var offerId = $(el).attr("OFFER_ID");
	$("#SET_GROUPOFFER_OFFER_ID").val(offerId);
	$("#SET_GROUPOFFER_ECMEB_TYPE").val(ecMebType);
	
//	var param = "&OFFER_ID="+offerId+"&ACTION=queryGroupOffersByOfferId";
	
	var param = "&OFFER_ID="+offerId+"&ecMebType="+ecMebType+"&ACTION=queryGroupOffersByOfferId";
	if(ecProductId)
	{
		param += "&EC_PRODUCT_OFFER_ID="+ecProductId;
	}
	if(ecMebType && ecMebType=="MEB"){
		param += buildDZSelElement(ecProductId);
	}
	//esp产品处理
	var dealtype = $(el).attr("Deal_Type");
	if(dealtype=="esp"){
		if (!checkEspMustSet(offerId)){
			return;
		}
		param += buildEspElement(offerId);
	}
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "GroupOffersPart", function(groupOfferList){
		
		//按数据库配置加载商品组列表，设置选中状态
		for(var i = 0, sizeI = groupOfferList.length; i < sizeI; i++)
		{
			var group = groupOfferList.get(i);
			var groupId = group.get("GROUP_ID");
			var groupSelectFlag = group.get("SELECT_FLAG");
			var offerList = group.get("GROUP_COM_REL_LIST"); //组内商品
			for(var j = 0, sizeJ = offerList.length; j < sizeJ; j++)
			{
				var offer = offerList.get(j);
				var offerId = offer.get("OFFER_ID");
				var offerSelectFlag = offer.get("SELECT_FLAG");
				if(groupSelectFlag == "0" && offerSelectFlag == "0")
				{//必选组的必选商品
					$("#grpOffer_"+groupId+"_"+offerId).attr("checked" , true);
					$("#grpOffer_"+groupId+"_"+offerId).attr("disabled", true);
				}
			}
		}
		
		//将已选择的定价计划设置为选中状态
		if(ecMebType == "DZ_MEB")
		{
			setGrpPackageOfferChecked(ecMebType);
		}
		else
		{
			setGroupOfferCheckedByOfferData(ecMebType);
		}

		$.endPageLoading();
		showPopup("popup", "GroupOfferListPopupItem", true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//组装定制选择的元素信息(modify)
function buildDZSelElement(ecProductId)
{
	var selDZElements = "";
	
	var grpPackageList = new Wade.DatasetList($("#DZ_MEB_OFFER_DATA").text());
	if(grpPackageList == null || grpPackageList.length == 0)
	{//BBOSS成员需要从隐藏域获取
		var ecBBossOfferData = new Wade.DataMap($("#CHILD_BBOSS_EC_PRODUCT_OFFER_DATA_"+ecProductId).text());
		grpPackageList = ecBBossOfferData.get("GRP_PACKAGE_INFO");
	}
	
	if(grpPackageList && grpPackageList.length > 0)
	{
		grpPackageList.each(function(item,index,totalcount){
			if(index == 0)
				selDZElements += item.get("ELEMENT_ID")+"@"+item.get("ELEMENT_TYPE_CODE");
			else
				selDZElements += ","+item.get("ELEMENT_ID")+"@"+item.get("ELEMENT_TYPE_CODE");
		});
	}

	return "&DZ_MEB_ELEMENTS="+selDZElements;
}

//组装ESP选择的元素
function buildEspElement(offerId)
{
	//云酒馆
	if(offerId == "110000921015"){
		var version = $("#pam_ESP_GROUP_VERSION").val();
		return "&ESP_OFFER_VERSION="+version;
	}
	return "";
}

//校验ESP必填产品参数是否已经设置
function checkEspMustSet(offerId)
{
	//云酒馆
	if(offerId == "110000921015"){
		var version = $("#pam_ESP_GROUP_VERSION").val();
		if (!version){
			MessageBox.alert("提示信息", "请先设置集团商品【云酒馆（产品）】的属性！");
			return false;
		}
	}
	return true;
}

//将已选择的定价计划设置为选中状态
function setGroupOfferCheckedByOfferData(ecMebType)
{
	var offerData = new Wade.DataMap($("#"+ecMebType+"_OFFER_DATA").text());
	var subOfferDataset = offerData.get("SUBOFFERS");
	if(typeof(subOfferDataset) == "undefined" || subOfferDataset.length == 0)
	{
		return ;
	}
	for(var i = 0, size = subOfferDataset.length; i < size; i++)
	{
		var subOfferData = subOfferDataset.get(i);
		var offerId = subOfferData.get("OFFER_ID");
		var groupId = subOfferData.get("GROUP_ID");
		var lastDayThisMonth = getLastDayOfCurrentMonth();
		var expireDate = subOfferData.get("END_DATE");
		if(expireDate && expireDate.length > 10)
		{
			expireDate = expireDate.substring(0, 10);
		}
		
		if(subOfferData.get("OPER_CODE") != ACTION_DELETE && lastDayThisMonth != expireDate)
		{
			$("#grpOffer_"+groupId+"_"+offerId).attr("checked" , true);
			$("#grpOffer_"+groupId+"_"+offerId).attr("disabled", true);
		}
		
	}
}

//将已选择的集团商品设置为选中状态
function setGrpPackageOfferChecked(ecMebType)
{
	var grpPackageList = new Wade.DatasetList($("#"+ecMebType+"_OFFER_DATA").text());
	if(grpPackageList && grpPackageList.length > 0)
	{
		for(var i = 0, size = grpPackageList.length; i < size; i++)
		{
			var groupId = grpPackageList.get(i).get("PACKAGE_ID");
			var offerId = grpPackageList.get(i).get("ELE_OFFER_ID");
			$("#grpOffer_"+groupId+"_"+offerId).attr("checked" , true);
			$("#grpOffer_"+groupId+"_"+offerId).attr("disabled", true);
		}
	}
}

function submitGrpPackages(el)
{
	var type = $("#SET_GROUPOFFER_ECMEB_TYPE").val();
	var grpPackageList = new Wade.DatasetList($("#"+type+"_OFFER_DATA").text());
	$("input[name=selGroupOffer]:checked").each(function(){
		if($("#DZ_MEB_"+$(this).attr("OFFER_ID")).length == 0)
		{
			var grpPackage = new Wade.DataMap();
			grpPackage.put("PRODUCT_ID", $(this).closest("div [class*='c_list c_list-line']").attr("OFFER_CODE"));
			grpPackage.put("PACKAGE_ID", $(this).closest("div [class*='c_list c_list-line']").attr("id"));
			grpPackage.put("ELEMENT_ID", $(this).attr("OFFER_CODE"));
			grpPackage.put("ELEMENT_NAME", $(this).attr("OFFER_NAME"));
			grpPackage.put("ELEMENT_TYPE_CODE", $(this).attr("OFFER_TYPE"));
			grpPackage.put("ELE_OFFER_ID", $(this).attr("OFFER_ID"));
			grpPackage.put("MODIFY_TAG", "0");
			
			grpPackageList.add(grpPackage);
			
			//回写html
			addGrpPackageHtml(grpPackage, type);
		}
		
	});
	
	//将定制信息数据设置回隐藏域
	$("#"+type+"_OFFER_DATA").text(grpPackageList.toString());
	
	//手动刷新scroller组件
	//ChildOfferSetupScroll.refresh();
	
	//关闭新增组内商品区域
	backPopup(el);
}

function submitGroupOffers(el)
{
	var ecMebType = $("#SET_GROUPOFFER_ECMEB_TYPE").val();
	if(ecMebType == "DZ_MEB")
	{//定制信息处理逻辑不同（定制不需要做元素个数校验，不需要计算元素生失效时间）
		submitGrpPackages(el);
		return ;
	}
	
	if(!checkGroupOfferSelNum())
	{
		return ;
	}
	
	if(!checkGroupOfferEspSelNum())
	{//esp产品某些元素需要进行互斥校验
		return ;
	}
	
	var offerId = $("#SET_GROUPOFFER_OFFER_ID").val();
	var ecMebType = $("#SET_GROUPOFFER_ECMEB_TYPE").val();

	var offerData = getOfferData(offerId, ecMebType);
	var subOffers = offerData.get("SUBOFFERS");
	if(typeof(subOffers) == "undefined" || subOffers.length == 0)
	{
		subOffers = new Wade.DatasetList();
		offerData.put("SUBOFFERS", subOffers);
	}
	//是否已经设置过包信息了，打个标记，免得再次加载包时将已经勾选掉的元素再次勾上
//	offerData.put("IS_HAVE_SET_GROUP", "true");
	var groupData = new Wade.DataMap();//存放选择的组信息及组内商品

	var selOfferId = "";
	$("input[name=selGroupOffer]:checked").each(function(){
		var groupId = $(this).closest("div [class*='c_list c_list-line']").attr("id");
		var offerCode = $(this).closest("div [class*='c_list c_list-line']").attr("OFFER_CODE");
		selOfferId = selOfferId + "@" + $(this).attr("OFFER_ID") + "#" + groupId+ "#" + offerCode;
		if(!groupData.containsKey(groupId))
		{
			var maxNum = $(this).closest("div [class*='c_list c_list-line']").attr("MAX_NUM");
			var minNum = $(this).closest("div [class*='c_list c_list-line']").attr("MIN_NUM");
			var selectFlag = $(this).closest("div [class*='c_list c_list-line']").attr("SELECT_FLAG");
			var limitType = $(this).closest("div [class*='c_list c_list-line']").attr("LIMIT_TYPE");

			var groupInfo = new Wade.DataMap();
			groupInfo.put("OFFER_CODE", offerCode);//包归属产品
			groupInfo.put("GROUP_ID", groupId);
			groupInfo.put("GROUP_NAME", $("#"+groupId).parent().find(".text").html());
			groupInfo.put("MUST_SEL_OFFER", $("#MUST_SEL_OFFER_"+groupId).val());
			groupInfo.put("MAX_NUM", maxNum);
			groupInfo.put("MIN_NUM", minNum);
			groupInfo.put("LIMIT_TYPE", limitType);
			groupInfo.put("SELECT_FLAG", selectFlag);
			
			var selOfferList = new Wade.DatasetList();
			var selOffer = new Wade.DataMap();
			selOffer.put("OFFER_ID", $(this).attr("OFFER_ID"));
			selOffer.put("OFFER_TYPE", $(this).attr("OFFER_TYPE"));
			selOffer.put("SELECT_FLAG", $(this).attr("SELECT_FLAG"));
			selOfferList.add(selOffer);
			
			groupInfo.put("SEL_OFFER", selOfferList);
			
			groupData.put(groupId, groupInfo);
		}
		else
		{
			var groupInfo = groupData.get(groupId);
			var selOfferList = groupInfo.get("SEL_OFFER", new Wade.DatasetList());
			
			var selOffer = new Wade.DataMap();
			selOffer.put("OFFER_ID", $(this).attr("OFFER_ID"));
			selOffer.put("OFFER_TYPE", $(this).attr("OFFER_TYPE"));
			selOffer.put("SELECT_FLAG", $(this).attr("SELECT_FLAG"));
			selOfferList.add(selOffer);
			groupInfo.put("SEL_OFFER", selOfferList);
			
			groupData.put(groupId, groupInfo);
		}
		$("#"+ecMebType+"_SelGroupOffer").text(groupData.toString());
	});
	
	if(selOfferId != "")
	{
		var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
		var mainOfferOperCode = ecOfferData.get("OPER_CODE");
		var brandCode = ecOfferData.get("BRAND_CODE");
		var param = "&SEL_OFFER_IDS="+selOfferId+"&MAIN_OPER_CODE="+mainOfferOperCode+"&EC_MEB_TYPE="+ecMebType+"&BRAND_CODE="+brandCode+"&EFFECT_NOW="+$("#EFFECT_NOW").val()+"&ACTION=buildSelGroupOfferData"
		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "", param, "GroupOffersPart", function(selOfferDataset){
			$.endPageLoading();
			for(var i = 0, sizeI = selOfferDataset.length; i < sizeI; i++)
			{
				var selOfferData = selOfferDataset.get(i);
				var offer_id = selOfferData.get("OFFER_ID");
				var offer_type = selOfferData.get("OFFER_TYPE");
				var validDate = selOfferData.get("START_DATE");
				var expireDate = selOfferData.get("END_DATE");
				var operCode = ACTION_CREATE;
				
				var addHtml = true;
				var addData = true;
				for(var j = 0, sizeJ = subOffers.length; j < sizeJ; j++)
				{
					var subOffer = subOffers.get(j);
					if(subOffer.get("OFFER_ID") == offer_id && subOffer.get("OPER_CODE") == "1")
					{//原来已经删除的实例
						subOffer.put("OPER_CODE", ACTION_EXITS);
						operCode = ACTION_EXITS;
						
						//将删除列表中的资费商品删除
//						$("#del_"+offer_id).remove();
						
						validDate = subOffer.get("START_DATE");
						expireDate = subOffer.get("OLD_END_DATE");
						subOffer.put("END_DATE", expireDate);
						
						if("D" == offer_type)
						{
							if($("#delPriceOffer").children().length == 0)
							{
								$("#PriceDelOfferPart").css("display", "none");
							}
						}else{
							if($("#delServiceOffer").children().length == 0)
							{
								$("#ServiceDelOfferListPart").css("display", "none");
							}
						}

						addHtml = true;
						addData = false;
						break;
					}
					else if(subOffer.get("OFFER_ID") == offer_id && subOffer.get("OPER_CODE") != "1")
					{
						addHtml = false;
						addData = false;
						break;
					}

				}
				if(addHtml)
				{
					if("D" == offer_type)
					{//资费  remark:此处判断是否添加html时，不需要比对P_OFFER_INDEX，因为商品组内同一个资费只有一个
						addSubOfferHtml(selOfferData, validDate, expireDate, operCode, null);
						if($("#PriceOfferPart").css("display") == "none")
						{
							$("#PriceOfferPart").css("display", "");
						}
					}
					else
					{//服务
						addSubOfferHtml(selOfferData, validDate, expireDate, operCode, null);
						if($("#ServiceOfferListPart").css("display") == "none")
						{
							$("#ServiceOfferListPart").css("display", "");
							
						}
					}
				}
				if(addData)
				{
					subOffers.add(selOfferData);
				}
			}
			updateOfferData(offerId, ecMebType, offerData);
			
			//手动刷新scroller组件
			//ChildOfferSetupScroll.refresh();
			
			//关闭新增组内商品区域
			backPopup(el);
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}
	else
	{
		//关闭新增组内商品区域
		backPopup(el);
	}
}

function addSubOfferHtml(selOfferData, validDate, expireDate, operCode, ecMebType)
{
	if(ecMebType == null)
	{
		ecMebType = $("#SET_GROUPOFFER_ECMEB_TYPE").val();
	}
	
	var htmlStr = "<li>";
	htmlStr += "<div class='main'>";
	htmlStr += "<div class='title' tip='"+selOfferData.get("OFFER_NAME")+"'>【" + selOfferData.get("OFFER_CODE") + "】" + selOfferData.get("OFFER_NAME") + "</div>";
	htmlStr += "<div class='content content-auto'>" + validDate.substring(0,10) + "~" + expireDate.substring(0,10) + "</div>";
	htmlStr += "</div>";
	if(selOfferData.get("HAS_OFFER_CHA") == "true")
	{
		var offerChaList = selOfferData.get("OFFER_CHA_SPECS");
		if(offerChaList && offerChaList.length > 0)
		{
			htmlStr += "<div class='side' EC_MEB_TYPE='"+ecMebType+"' OFFER_ID="+selOfferData.get("OFFER_ID")+" OFFER_CODE="+selOfferData.get("OFFER_CODE")+" ontap='openOfferChaPopupItem(this, &#39;"+ecMebType+"&#39;);'><span class='e_tag e_tag-green'>已设置</span></div>";
			htmlStr += "<div class='more'></div>";
		}
		else
		{
			htmlStr += "<div class='side' EC_MEB_TYPE='"+ecMebType+"' OFFER_ID="+selOfferData.get("OFFER_ID")+" OFFER_CODE="+selOfferData.get("OFFER_CODE")+" ontap='openOfferChaPopupItem(this, &#39;"+ecMebType+"&#39;);'><span class='e_tag e_tag-red'>待设置</span></div>";
			htmlStr += "<div class='more'></div>";
		}
	}
	if(selOfferData.get("SELECT_FLAG") == "0")
	{
		htmlStr += "<div class='fn'>";
		htmlStr += "<span class='e_ico-delete e_dis'></span>";
		htmlStr += "</div>";
	}
	else
	{
		htmlStr += "<div class='fn' OFFER_ID="+selOfferData.get("OFFER_ID")+" ontap='deleteSubOffer(this, &#39;"+ecMebType+"&#39;);'>";
		htmlStr += "<span class='e_ico-delete'></span>";
		htmlStr += "</div>";
	}
	htmlStr += "</li>";
	
	if(ecMebType == "EC")
	{
		$("#childEcOfferSubList").append(htmlStr);
	}
	else if(ecMebType == "MEB")
	{
		$("#childMebOfferSubList").append(htmlStr);
	}
}

function addGrpPackageHtml(grpPackage, ecMebType)
{
	var htmlStr = "<li OFFER_ID='"+grpPackage.get("ELE_OFFER_ID")+"' id='"+ecMebType+"_"+grpPackage.get("ELE_OFFER_ID")+"'>";
	htmlStr += "<div class='main'>";
	htmlStr += "<div class='title' tip='"+grpPackage.get("ELEMENT_NAME")+"'>";	
	htmlStr += "【" + grpPackage.get("ELEMENT_ID") + "】" + grpPackage.get("ELEMENT_NAME") + "</div>";
	htmlStr += "</div>";
	if(grpPackage.get("SELECT_FLAG") == "0")
	{
		htmlStr += "<div class='fn'>";
		htmlStr += "<span class='e_ico-delete e_dis'></span>";
		htmlStr += "</div>";
	}
	else
	{
		htmlStr += "<div class='fn' OFFER_ID="+grpPackage.get("ELE_OFFER_ID")+" ontap='deleteGrpPackage(this);'>";
		htmlStr += "<span class='e_ico-delete'></span>";
		htmlStr += "</div>";
	}
	htmlStr += "</li>\r\n";
	$("#DZ_childMebOfferSubList").append(htmlStr);
	
}

function checkGroupOfferSelNum()
{
	//校验商品组内选择商品数
	var flag = true;
	var errorInfo = "";
	$("div[id=GroupOffersPart] div[class*='c_list c_list-line']").each(function(){
		var maxNum = $(this).attr("MAX_NUM"); //组内选择商品最大数
		var minNum = $(this).attr("MIN_NUM"); //组内选择商品最小数
		var groupSelectFlag = $(this).attr("SELECT_FLAG"); //商品组必选标记
		var limitType = $(this).attr("LIMIT_TYPE"); //商品组必选标记
		var groupId = $(this).attr("id"); //商品组id
		var selNum = 0; //已选择的子商品个数
		var selCheckNum = 0; //已选择的需要校验的子商品个数，对应pm_group的limit_type
		var selOfferIds = ""; //已选择的子商品id
		var typeName="";
		if("D"==limitType){
			typeName="资费类";
		}else if("S"==limitType){
			typeName="服务类";
		}
		$("#"+groupId+" input[type=checkbox]").each(function(){
			if($(this).attr("checked"))
			{
				var offerType = $(this).attr("OFFER_TYPE");
				if(limitType && limitType==offerType){
					selCheckNum++;
				}else if(!limitType){
					selCheckNum++;
				}
				selNum++;
				if(selNum == 1)
				{
					selOfferIds = selOfferIds + $(this).attr("OFFER_ID");
				}
				else
				{
					selOfferIds = selOfferIds + "@" + $(this).attr("OFFER_ID");
				}
			}
		});
		var groupName = $("#"+groupId).parent().find(".text").html();
		if(groupSelectFlag == "0")
		{//商品组必选
			if(selCheckNum > maxNum && maxNum > 0)
			{//包内最大订购数小于0的不做校验
				errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
				flag = false;
				return ;
			}
			else if(selCheckNum < minNum && minNum > 0)
			{
				errorInfo = "["+groupName+"]为必选组商品组，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
				flag = false;
				return ;
			}
		}
		else
		{//商品组非必选
			var mustSelectOffers = $("#MUST_SEL_OFFER_"+groupId).val();
			if(mustSelectOffers)
			{//非必选组存在必选子商品
				if(selNum > 0)
				{
					var mustSelectOfferArr = mustSelectOffers.split("@");
					var selOfferIdArr = selOfferIds.split("@");
					for(var i = 0, sizeI = mustSelectOfferArr.length; i < sizeI; i++)
					{
						var selFlag = false; //组内必选商品是否被选中
						for(var j = 0, sizeJ = selOfferIdArr.length; j < sizeJ; j++)
						{
							if(mustSelectOfferArr[i] == selOfferIdArr[j])
							{
								selFlag = true;
								break;
							}
						}
						if(!selFlag)
						{
							var name = $("#grpOffer_"+groupId+"_"+mustSelectOfferArr[i]).attr("OFFER_NAME");
							errorInfo = "您已选择["+groupName+"]组内商品["+name+"]是必选商品，选择了该组的商品必须同时订购["+name+"]商品！";
							flag = false;
							return ;
						}
					}
					//非必选组必须要在选择组内商品的情况下才校验元素个数
					if(selCheckNum > maxNum && maxNum > 0)
					{//包内最大订购数小于0的不做校验
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
						flag = false;
						return ;
					}
					else if(selCheckNum < minNum && minNum > 0)
					{
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
						flag = false;
						return ;
					}
				}
				
			}
			else
			{
				if(selCheckNum > maxNum && maxNum > 0)
				{//包内最大订购数小于0的不做校验
					errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
					flag = false;
					return ;
				}
				else if(selCheckNum > 0 && selCheckNum < minNum && maxNum > 0)
				{
					errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
					flag = false;
					return ;
				}
			}
		}
	});
	if(!flag)
	{
		MessageBox.alert("提示信息", errorInfo);
		return false;
	}
	return true;
}

function checkGroupOfferEspSelNum(){
		//校验商品组内选择商品数
		var flag = true;
		var errorInfo = "";
		$("div[id=GroupOffersPart] div[class*='c_list c_list-line']").each(function(){
			var maxNum = $(this).attr("MAX_NUM"); //组内选择商品最大数
			var minNum = $(this).attr("MIN_NUM"); //组内选择商品最小数
			var groupSelectFlag = $(this).attr("SELECT_FLAG"); //商品组必选标记
			var limitType = $(this).attr("LIMIT_TYPE"); //商品组必选标记
			var groupId = $(this).attr("id"); //商品组id
			var selNum = 0; //已选择的子商品个数
			var selCheckNum = 0; //已选择的需要校验的子商品个数，对应pm_group的limit_type
			var selOfferIds = ""; //已选择的子商品id
			var typeName="";
			var offerId = $(this).attr("offer_id");
			if("D"==limitType){
				typeName="资费类";
			}else if("S"==limitType){
				typeName="服务类";
			}
			$("#"+groupId+" input[type=checkbox]").each(function(){
				if($(this).attr("checked"))
				{
					var offerType = $(this).attr("OFFER_TYPE");
					if(limitType && limitType==offerType){
						selCheckNum++;
					}else if(!limitType){
						selCheckNum++;
					}
					selNum++;
					if(selNum == 1)
					{
						selOfferIds = selOfferIds + $(this).attr("OFFER_ID");
					}
					else
					{
						selOfferIds = selOfferIds + "@" + $(this).attr("OFFER_ID");
					}
				}
			});
			var groupName = $("#"+groupId).parent().find(".text").html();
			//云酒馆 互斥校验
			if(offerId == "110000921015"){
				var selOfferIdArr = selOfferIds.split("@");
				if (selOfferIdArr.contains("130392101503")){
					if (selOfferIdArr.contains("130392101504")){
						var name03 = $("#grpOffer_"+groupId+"_130392101503").attr("OFFER_NAME");
						var name04 = $("#grpOffer_"+groupId+"_130392101504").attr("OFFER_NAME");
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+name03+"商品与"+name04+"商品，"+"只能选择其中1个！";
						flag = false;
						return;
					}
					if (selOfferIdArr.contains("130392101505")){
						var name03 = $("#grpOffer_"+groupId+"_130392101503").attr("OFFER_NAME");
						var name05 = $("#grpOffer_"+groupId+"_130392101505").attr("OFFER_NAME");
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+name03+"商品与"+name05+"商品，"+"只能选择其中1个！";
						flag = false;
						return;
					}
				}
				else if (selOfferIdArr.contains("130392101504")){
					if (selOfferIdArr.contains("130392101505")){
						var name04 = $("#grpOffer_"+groupId+"_130392101504").attr("OFFER_NAME");
						var name05 = $("#grpOffer_"+groupId+"_130392101505").attr("OFFER_NAME");
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+name04+"商品与"+name05+"商品，"+"只能选择其中1个！";
						flag = false;
						return;
					}
				}
			}
		});
		if(!flag)
		{
			MessageBox.alert("提示信息", errorInfo);
			return false;
		}
		return true;
}

function showOffers(id)
{
	var groupId = id.split("_")[1];
	var reduceId = "unfold_" + groupId;
	$("#"+id).css("display", "none");
	$("#"+reduceId).css("display", "");
	$("#"+groupId).css("display", "");
}

function hideOffers(id)
{
	var groupId = id.split("_")[1];
	var addId = "fold_" + groupId;
	$("#"+id).css("display", "none");
	$("#"+addId).css("display", "");
	$("#"+groupId).css("display", "none");
}