//打开商品特征区域
function openOfferChaPopupItem(el, ecMebType)
{
	var offerId = $(el).attr("OFFER_ID");
	var offerData = getOfferData(offerId, ecMebType);
	if("D" == offerData.get("OFFER_TYPE") && offerData.get("PRICE_CHA_TYPE") == "OFFER")
	{//资费属性组件
		openPriceChaPopupItem(el, ecMebType);
	}
	else
	{//打开产品属性or服务属性设置
		openProdSvcOfferChaPopupItem(el, ecMebType);
	}
}

//打开产品属性or服务属性设置
function openProdSvcOfferChaPopupItem(el, ecMebType)
{
	var offerId = $(el).attr("OFFER_ID");
	var offerCode = $(el).attr("OFFER_CODE");
	$("#SET_OFFERCHA_OFFER_ID").val(offerId);
	$("#SET_OFFERCHA_ECMEB_TYPE").val(ecMebType);
	
	var offerData = getOfferData(offerId, ecMebType);
	
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
    var param = "";
//    if (ecMebType === "MEB" && !ecOfferData.get("OFFER_ID")) {
//        var ecOfferId = $("#CHILD_OFFER_BBOSS_MEB_PRODUCT_LIST_UL input:hidden").first().attr("ec_product_offer_id");
//        var ecOfferCode = $("#CHILD_BBOSS_EC_PRODUCT_OFFER_ID_"+ecOfferId).attr("offer_code");
//        ecOfferData.put("OFFER_ID", ecOfferId);
//        ecOfferData.put("OFFER_CODE", ecOfferCode);
//        param = "&BRAND_CODE=BOSG";
//    }
    param += "&EC_OFFER_ID="+ecOfferData.get("OFFER_ID")+"&EC_OFFER_CODE="+ecOfferData.get("OFFER_CODE")+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&OPER_CODE="+offerData.get("OPER_CODE")+"&EC_MEB_TYPE="+ecMebType+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	if(ecMebType == "MEB")
	{
		param = param + "&MEB_OFFER_ID="+$("#CHILD_MEB_OFFER_ID").val();
	}
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "initOfferCha", param, "OfferChaPart", function(data){
		$.endPageLoading();
		var offerChaList = offerData.get("OFFER_CHA_SPECS");
		if(offerChaList)
		{
			for(var i = 0, size = offerChaList.length; i < size; i++)
			{
				var offerChaSpec = offerChaList.get(i);
				var attrCode = offerChaSpec.get("ATTR_CODE");
				var attrValue = offerChaSpec.get("ATTR_VALUE");
				var grpItemId = offerChaSpec.get("ATTR_GROUP");
				if (grpItemId)
				{
					pointGrpItemInfos2(offerChaSpec);
				}
				else
				{
					var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
					var inputName ="";
					if("120000008172"==offerId){
						inputName = attrCode
					}else{
						inputName = "pam_" + attrCode;
					}
					if("BOSG" == ecOfferData.get("BRAND_CODE"))
					{
						inputName = attrCode;
						$("#OfferChaPart input[element_id="+attrCode+"]").val(attrValue);
					}
					
					var inputType = $("#OfferChaPart input[name="+inputName+"]").attr("type");
					if(inputType == "checkbox" || inputType == "radio")
					{
						if(attrValue == "1")
						{
							$("#OfferChaPart input[name="+inputName+"]").attr("checked", "true");
						}
						else
						{
							$("#OfferChaPart input[name="+inputName+"]").removeAttr("checked");
						}
					}
					else
					{
						$("#OfferChaPart input[id="+inputName+"]").val(attrValue);
					}
				}
			}
			initOfferChaSpec(offerChaList);
		}
		else {
			
			//加载电子合同中的信息
			var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
			var ecOfferId = ecOfferData.get("OFFER_ID");
			var eleOfferData = new Wade.DataMap($("#AGREEMENT_ELEMENT_DATA_"+ecOfferId).text());
			if(eleOfferData && ecMebType =="MEB")
			{	
				var mebEleData =eleOfferData.get("MEB_OFFER");
				if(mebEleData)
				{
					for (var i = 0; i < mebEleData.length; i++)
					{
						var offerChaList = mebEleData[i].get("OFFER_CHA_SPECS");
						initChaSpec(offerChaList);
					}
				}
			}
			else if(eleOfferData && ecMebType =="EC")
			{	
				var ecEleData =eleOfferData.get("EC_OFFER");
				if(ecEleData)
				{
					for (var i = 0; i < ecEleData.length; i++) {
						var offerChaList = ecEleData[i].get("OFFER_CHA_SPECS");
						initChaSpec(offerChaList);
					}
				}
			}
		}
		showPopup("popup", "OfferChaPopupItem", true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

//提交商品特征
function submitOfferCha()
{
	if(!$.validate.verifyAll("OfferChaPart"))
	{
		return false;
	}
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	var brandCode = ecOfferData.get("BRAND_CODE");
	
	var offerId = $("#SET_OFFERCHA_OFFER_ID").val();
	var ecMebType = $("#SET_OFFERCHA_ECMEB_TYPE").val();
    // 成员的情况，是取不到集团的信息的,且只有bboss需要区分
    if (ecMebType === "MEB" && !brandCode) {
        var bbossTag = $("#CHILD_OFFER_BBOSS_MEB_PRODUCT_DIV input:hidden");
        if (bbossTag.length > 0) {
            brandCode = bbossTag.attr("brand_code");
        }
    }

    var offerData = getOfferData(offerId, ecMebType);
	var operCode = offerData.get("OPER_CODE");
	
	var isChaValueChg = false;
	var isChaDataChg = false;
	var offerChaSpecDataset = new Wade.DatasetList();
	
	var chaSpecObjs = $("#OfferChaPart input");
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		var elemetnId = chaSpecObjs[i].getAttribute("element_id");
		var simpleupload = chaSpecObjs[i].getAttribute("x-wade-uicomponent");
		var chaSpecGroupId = chaSpecObjs[i].getAttribute("attr_group");
		
		//剔除隐藏的上传主键
		if("BOSG" == brandCode && removeUpload(elemetnId, simpleupload, chaSpecObjs[i]))
		{
			continue;
		}
		if("BOSG" == brandCode && isBbossNull(chaSpecCode))
		{
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
			if(chaValue == 0)
			{
				continue;
			}
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
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
		if(brandCode == "BOSG")
		{
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
		if("BOSG" == brandCode)
		{
			offerChaSpecData.put("ATTR_CODE", chaSpecObjs[i].getAttribute("element_id"));
		}
		//20190320 zhanghy9 bboss属性组
		if(chaSpecGroupId != "" && chaSpecGroupId != null)
		{
		    offerChaSpecData.put("ATTR_GROUP", chaSpecGroupId);
		    offerChaSpecData.put("OPER_CODE", chaSpecObjs[i].getAttribute("oper_code"));
		}
		offerChaSpecDataset.add(offerChaSpecData);
	}
	var grpItemEleObjs = $("#OfferChaPart textarea[isgrpitem='true']");
	if (grpItemEleObjs && grpItemEleObjs.length>0){
		for (var i = 0; i< grpItemEleObjs.length; i++){
			var grpMapObj = new Wade.DataMap($(grpItemEleObjs[i]).text());
			var paramData = grpMapObj.get("ELEMENT_DATAS");
			for (var j = 0;j < paramData.items.length; j++){
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
	//将产品特征规格放到数据对象中 
	if(offerChaSpecDataset.length != 0)
	{
		var offerData = getOfferData(offerId, ecMebType);
		offerData.put("OFFER_CHA_SPECS", offerChaSpecDataset);
		offerData.put("IS_CHA_VALUE_CHANGE", isChaValueChg);//判断当前商品属性是否变更标记
		if(isChaValueChg == true)
		{
			offerData.put("OPER_CODE", ACTION_UPDATE);
		}
		
		//保存数据
		updateOfferData(offerId, ecMebType, offerData);
	}

	$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE="+ecMebType+"][OFFER_ID="+offerId+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
	return true;
}

function initPageParamCommon()
{
	var curOfferId = $("#SET_OFFERCHA_OFFER_ID").val();
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

function pointGrpItemInfos2(offerChaSpec){

	var attrCode = offerChaSpec.get("ATTR_CODE");
	var grpItempopId = "GRPITEM";
	if("999033719"==attrCode.split("_")[0]){
		grpItempopId+="999033719";
	}else{
		grpItempopId+="999033720";
	}
	var operCode = offerChaSpec.get("OPER_CODE");
	var putPoint = $("span[element_id="+grpItempopId+"]").parent().parent().parent().parent();
	pointGrpItemInfo("B"+attrCode.split("_")[0],offerChaSpec,putPoint,grpItempopId,operCode);
}

function pointGrpItemInfo(key,element,putPoint,grpItempopId,operCode){
	var infoItem;
	var count = element.get("ATTR_GROUP");
	if(typeof($("#BSEL"+key.split("B")[1]))!="undefined"){
		infoItem = $("#BSEL"+key.split("B")[1]);
	}else if(typeof($("#"+key))!="undefined"){
		infoItem = $("#"+key);
	}
	var page = putPoint.parent().find("div[area='"+grpItempopId+"'][group_id='"+count+"']");
	var pageBody = "";
	if(page.html()==null||page.html()==""){
		pageBody+= "<div class=\"c_list c_list-form\" group_id=\""+count+"\" area=\""+grpItempopId+"\"><ul>";
		pageBody+="<li class=\"link\">	<div class=\"label\">";
	}else{
		pageBody+="<li style=\"display:none;\" class=\"link\">	<div class=\"label\">";
	}	
    pageBody+=infoItem.attr("desc")+"</div><div class=\"value\"><div class=\"e_mix\">";
    pageBody+="<input x-wade-uicomponent=\"text\" ";
    pageBody+="value=\""+""+element.get("ATTR_VALUE")+"\" ";
    pageBody+="type=\"text\" ";
    if(element.get("OLD_ATTR_VALUE")!=null){
		pageBody+="old_attr_value=\""+""+element.get("OLD_ATTR_VALUE")+"\" ";
    }else{
    	pageBody+="old_attr_value=\""+""+element.get("ATTR_VALUE")+"\" ";
    }
    pageBody+="old_attr_value=\""+""+element.get("ATTR_VALUE")+"\" ";
    pageBody+="oper_code=\""+operCode+"\" ";
    pageBody+="name=\""+""+infoItem.attr("name")+"\" ";
    pageBody+="id=\""+""+infoItem.attr("id")+"_"+count+"\" ";
    pageBody+="autocomplete=\""+""+infoItem.attr("autocomplete")+"\" ";
    pageBody+="element_id=\""+""+infoItem.attr("element_id")+"_"+count+"\" ";
    pageBody+="desc=\""+""+infoItem.attr("desc")+"\" ";
    if(operCode!=ACTION_DELETE){
    	 pageBody+="sytle=\"text-decoration:line-through\" ";
    }
    pageBody+="attr_group=\""+count+"\" readonly > ";
    if(operCode!=ACTION_DELETE){
    	pageBody+="<span class='e_ico-edit e_ico-pic-xxxs' ontap=\"changeGrpItemInfos(this)\" tip=\"进行修改\"></span>";	
    	pageBody+="<span class=\"e_ico-delete e_ico-pic-xxxs\" tip=\"去除条目\" ontap=\"deleteGrpItem(this);\"></span></div></div></li>";
	}else{
		pageBody+="<span class=\"e_ico-cancel e_ico-pic-xxxs\" tip=\"取消删除\" ontap=\"undeleteGrpItem(this);\"></span></div></div></li>";
	}
    if(page.html()==null||page.html()==""){
		pageBody+="</ul></div>";
		putPoint.after(pageBody);
	}else{
		page.children().children("li:last-child").after(pageBody);
	}
}

function initChaSpec (offerChaList)
{
	if(offerChaList)
	{
		for(var i = 0, size = offerChaList.length; i < size; i++)
		{
			var offerChaSpec = offerChaList.get(i);
			var attrCode = offerChaSpec.get("ATTR_CODE");
			var attrValue = offerChaSpec.get("ATTR_VALUE");
			var grpItemId = offerChaSpec.get("ATTR_GROUP");
			if (grpItemId)
			{
				pointGrpItemInfos2(offerChaSpec);
			}
			else
			{
				var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
				var inputName = "pam_" + attrCode;
				if("BOSG" == ecOfferData.get("BRAND_CODE"))
				{
					inputName = attrCode;
					$("#OfferChaPart input[element_id="+attrCode+"]").val(attrValue);
				}
				
				var inputType = $("#OfferChaPart input[name="+inputName+"]").attr("type");
				if(inputType == "checkbox" || inputType == "radio")
				{
					if(attrValue == "1")
					{
						$("#OfferChaPart input[name="+inputName+"]").attr("checked", "true");
					}
					else
					{
						$("#OfferChaPart input[name="+inputName+"]").removeAttr("checked");
					}
				}
				else
				{
					$("#OfferChaPart input[id="+inputName+"]").val(attrValue);
				}
			}
		}
		initOfferChaSpec(offerChaList);
	}
	
}