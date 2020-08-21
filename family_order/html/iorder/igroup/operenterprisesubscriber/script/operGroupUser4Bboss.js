var count=new Wade.DataMap();
var grpItempopId="";
var grpItemareaId="";
var grpItempagePop="";
//删除可选子销售品信息(入参：可选子销售品编码，主销售品编码, 子商品序列)
function deleteBbossOptionalOffer(optOfferId, offerId, offerIndex)
{
	MessageBox.confirm("提示信息", "是否删除当前子商品？", function(btn){
		if(btn == "cancel")
		{
			return;
		}
		
		//将子销售品的选择标记取消
		$("#"+optOfferId).attr("checked", false);
		$("#"+optOfferId).attr("disabled", false);
		
		//删除数据结构
		var offerData = PageData.getData($("#class_"+offerId));
		var offers = offerData.get("SUBOFFERS");
		if(typeof(offers) == "undefined" || typeof(offers) != "object" || offers.length == 0){
			//动力100子销售品未选择实例前无数据结构
			return;
		}
		for(var i = offers.length; i > 0; i--)
		{
			var offer = offers.get(i-1);
			if(offer.get("OFFER_ID") == optOfferId && typeof(offer.get("OFFER_INS_ID")) == "undefined" && offer.get("OFFER_INDEX") == offerIndex)
			{
				offers.removeAt(i-1);
				$("#div_"+optOfferId+"_"+offerIndex).remove();
			}
			else if(offer.get("OFFER_ID") == optOfferId && typeof(offer.get("OFFER_INS_ID")) != "undefined" && offer.get("OFFER_INDEX") == offerIndex)
			{
				offer.put("OPER_CODE", ACTION_DELETE);
				offer = deleteSubOfferChild(offer);
				$("#div_"+optOfferId+"_"+offerIndex).remove();
			}
		}
		if(offers.length == 0)
		{
			offerData.remove(offers);
		}
		PageData.setData($("#class_"+offerId), offerData);

	});
}
function deleteBbossOptionalOffer1(optOfferId, offerId, offerIndex)
{
	if(!window.confirm("是否删除当前子商品？")){
		return false;
	}
	
	//将子销售品的选择标记取消
	$("#"+optOfferId).attr("checked", false);
	$("#"+optOfferId).attr("disabled", false);
	
	//判断子销售品是否有实例，有则隐藏，没有则直接删除
	var hasInst = false;
	var offerInsId = $("#oo_"+offerId+"_OPTOFFER_ID").attr("OFFER_INS_ID");
	if(offerInsId == "" || typeof(offerInsId) =="undefined")
	{//没有实例
		$("#div_"+optOfferId+offerIndex).remove();
	}
	else
	{
		$("#div_"+optOfferId+offerIndex).css("display", "none");
		hasInst = true;
	}
	//删除数据结构
	var offerData = PageData.getData($("#class_"+offerId));
	var offers = offerData.get("SUBOFFERS");
	if(typeof(offers) == "undefined" || typeof(offers) != "object" || offers.length == 0){
		//动力100子销售品未选择实例前无数据结构
		return;
	}
	for(var i = offers.length; i > 0; i--)
	{
		var offer = offers.get(i-1);
		if(offer.get("OFFER_ID") == optOfferId && offer.get("OFFER_INDEX") == offerIndex)
		{
			if(hasInst)
			{
				offer.put("OPER_CODE", "2");
			}
			else
			{
				offers.removeAt(i-1);
			}
		}
	}
	if(offers.length == 0)
	{
		offerData.remove(offers);
	}
	PageData.setData($("#class_"+offerId), offerData);
}

function dealProdOperCodeByMerchOperCode(merchOperCode, prodOperCode, offerId) {
	var retCode = merchOperCode;
	var ispre = $("#IS_PRE").val();
    //预受理
    if("true" == ispre&& "10"==merchOperCode){
    	return merchOperCode;
    }
	switch(merchOperCode)
	{
	// 新增产品订购
	case "1":
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getProdPreInfo',
				'&OFFER_ID=' + offerId, function(d) {
			retCode = d.map.result;
		}, function(e, i) {
			MessageBox.alert("操作失败");
			$(e).select();
			retCode = "-1";
		},{async:false});
		break;
	case "7":
		// 新增产品订购
		if(prodOperCode == "0") {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getProdPreInfo',
					'&OFFER_ID=' + offerId, function(d) {
				retCode = d.map.result;
			}, function(e, i) {
				MessageBox.alert("操作失败");
				$(e).select();
				retCode = "-1";
			},{async:false});
			break;
		}
		// 注销产品
		else if(prodOperCode == "1") {
			retCode = "2";
			break;
		}
		retCode = "7";
		break;
	// 冷冻期恢复商品订购
	case "11":
		retCode = "12";
		break;
	// 预取消商品订购
	case "10":
		retCode = "11";
		return ;
	default:
		retCode = merchOperCode;
	}
	return retCode;
}

function bbossProductPause(subOfferId, OfferId, subOfferIndex) {

	MessageBox.confirm("确认暂停", "确定暂停子商品吗？<br> 暂停后，将停止提供服务并不再计费。", function(btn){
		if("ok" == btn) {
			var OfferInfo = PageData.getData($("#class_" + OfferId));
			var subOfferInfos = OfferInfo.get("SUBOFFERS");
			for(var i=0;i < subOfferInfos.length;i++) {
				var productId = subOfferInfos.get(i).get("OFFER_ID");
				if(subOfferId == productId) {
					//产品操作类型
					var operCode = subOfferInfos.get(i).get("OPER_CODE");
					if("3" == operCode){
					// 将产品操作类型改为暂停，前后台转换的时候会将这个操作类型传后台，后台会跳过判断是否变更了资费属性的判断
					subOfferInfos.get(i).put("OPER_CODE", ACTION_PASTE);
					//修改暂停按钮
					$("#div_"+subOfferId+"_"+subOfferIndex+" span").attr("class","e_ico-play");
					}
/*					else if(ACTION_PASTE == operCode){
						//数据还原 3 状态
					subOfferInfos.get(i).put("OPER_CODE", "3");
					$("#div_"+subOfferId+"_"+subOfferIndex+" span").attr("class","e_ico-pause");
					}*/
					break;
				}
			}
			PageData.setData($("#class_"+OfferId), OfferInfo);
			openEnterpriseOfferPopupItem(subOfferId, OfferId, false, subOfferIndex);
		}
	});
}

function bbossProductRecovery(subOfferId, OfferId, subOfferIndex) {
	MessageBox.confirm("确认恢复", "确定恢复子商品吗？<br> 恢复后，将重新提供服务并开始计费。", function(btn){
		if("ok" == btn) {
			var OfferInfo = PageData.getData($("#class_" + OfferId));
			var subOfferInfos = OfferInfo.get("SUBOFFERS");
			for(var i=0;i < subOfferInfos.length;i++) {
				var productId = subOfferInfos.get(i).get("OFFER_ID");
				if(subOfferId == productId) {
					//产品操作类型
					var operCode = subOfferInfos.get(i).get("OPER_CODE");
					if("3" == operCode){
					// 将产品操作类型改为恢复，前后台转换的时候会将这个操作类型传后台，后台会跳过判断是否变更了资费属性的判断
					subOfferInfos.get(i).put("OPER_CODE", ACTION_CONTINUE);
					$("#div_"+subOfferId+"_"+subOfferIndex+" span").attr("class","e_ico-pause");
					}
/*					else if(ACTION_CONTINUE = operCode){
						//数据还原 3 状态
						subOfferInfos.get(i).put("OPER_CODE", "3");
						$("#div_"+subOfferId+"_"+subOfferIndex+" span").attr("class","e_ico-play");
					}*/
					break;
				}
			}
			PageData.setData($("#class_"+OfferId), OfferInfo);
			openEnterpriseOfferPopupItem(subOfferId, OfferId, false, subOfferIndex);
		}
	});
}

function bbossProductPreDst(subOfferId, OfferId, subOfferIndex) {
	MessageBox.confirm("确认预取消", "确定预取消子商品吗？<br> 预取消后，将停止服务并停止计费。", function(btn){
		if("ok" == btn) {
			var OfferInfo = PageData.getData($("#class_" + OfferId));
			var subOfferInfos = OfferInfo.get("SUBOFFERS");
			for(var i=0;i < subOfferInfos.length;i++) {
				var productId = subOfferInfos.get(i).get("OFFER_ID");
				if(subOfferId == productId) {
					// 将产品操作类型改为预取消，前后台转换的时候会将这个操作类型传后台，后台会跳过判断是否变更了资费属性的判断
					subOfferInfos.get(i).put("OPER_CODE", ACTION_PREDESTROY);
					break;
				}
			}
			PageData.setData($("#class_"+OfferId), OfferInfo);
			openEnterpriseOfferPopupItem(subOfferId, OfferId, false, subOfferIndex);
		}
	});
}


function bbossProductPreDstBack(subOfferId, OfferId, subOfferIndex) {
	MessageBox.confirm("确认冷冻期恢复", "确定恢复冷冻期子商品吗？<br> 冷冻期恢复后，将重新提供服务并开始重新计费。", function(btn){
		if("ok" == btn) {
			var OfferInfo = PageData.getData($("#class_" + OfferId));
			var subOfferInfos = OfferInfo.get("SUBOFFERS");
			for(var i=0;i < subOfferInfos.length;i++) {
				var productId = subOfferInfos.get(i).get("OFFER_ID");
				if(subOfferId == productId) {
					// 将产品操作类型改为冷冻期恢复，前后台转换的时候会将这个操作类型传后台，后台会跳过判断是否变更了资费属性的判断
					subOfferInfos.get(i).put("OPER_CODE", ACTION_PREDSTBACK);
					break;
				}
			}
			PageData.setData($("#class_"+OfferId), OfferInfo);
			openEnterpriseOfferPopupItem(subOfferId, OfferId, false, subOfferIndex);
		}
	});
}

function hideBbossMerchPage() {
    $("#proParamPage").attr("style", "display:");
    $("#dynamicOfferParam").attr("style", "display:");
    $("#BbossOfferParam").attr("style", "display:none");
}

function showBbossMerchPage(el, operCode, topOfferId) {
    $("#BbossOfferParam").attr("style", "display:");
	var merchInfo = PageData.getData($("#class_"+topOfferId)).get("MERCHINFO");
	if(merchInfo){
        $("#proParamPage").attr("style", "display:none");
        $("#dynamicOfferParam").attr("style", "display:none");
        $("#BbossOfferParam").attr("style", "display:");
		initBBossOfferParam();
		forwardPopup(el, 'productChaSpecPopupItem');
	}
	else {
		// 刷新加载商品参数页面
		var offerId = $("#cond_OFFER_ID").val();
		var subInstId = PageData.getData($("#class_" + offerId)).get("USER_ID");
		if(typeof(subInstId) == "undefined") {
			subInstId = "";
		}
		var mainOfferProds = PageData.getData($(".e_SelectOfferPart")).get("PRODS");
	    var isEsop= $("#IS_ESOP").val();
		var param = "&OPER_TYPE=" + operCode + "&OFFER_ID=" + offerId + "&SUB_INST_ID=" + subInstId+"&PRODS="+mainOfferProds+"&IS_ESOP="+isEsop;
		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "", param, "productChaSpecPopupItem", function(data){
                $("#proParamPage").attr("style", "display:none");
                $("#dynamicOfferParam").attr("style", "display:none");
                initBBossOfferParam();
                forwardPopup(el, 'productChaSpecPopupItem');
				$.endPageLoading();
			},
			function(error_code,error_info,derror){
                $("#proParamPage").css("display", "");
                $("#dynamicOfferParam").css("display", "");
                $("#BbossOfferParam").css("display", "none");
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
	}
}

//提交BBOSS商品特征规格
function submitBBOSSChaSpec()
{
	// 如果是再次打开，并修改了商品操作类型，则需要将产商品备份中的数据覆盖提交的产品数据。
	var backUpInfo = $("#class_OfferDataBackup").text();
	// 先清空提交数据的数据，再赋值。
	var offerId = $("#cond_OFFER_ID").val();
	if (backUpInfo) {
		$("#class_" + offerId).text("");
		$("#class_" + offerId).text(backUpInfo);
	}
	// 如果是变更的时候且商品操作类型选择的是取消商品订购
	var operType = $("#cond_OPER_TYPE").val();
	var merchOperCode = $("#OPERTYPE").val();
	if ( "ChgUs" == operType ) {
		$("#ChgSubmit").children().text("变更");
		if ( "2" == merchOperCode ) {
			$("#ChgSubmit").children().text("注销");
		}
	}

	var merchInfo = new Wade.DataMap();
	//1-组装合同部分信息
	merchInfo.put("AUDITOR_INFOS", AUDITOR_INFOS);//审批人信息
	merchInfo.put("CONTACTOR_INFOS", CONTACTOR_INFOS);//联系人信息
	merchInfo.put("ATT_INFOS", ATT_INFOS);//合同信息

	//2-组装商品 信息
	merchInfo.put("OPERTYPE",$("#OPERTYPE").val());
	merchInfo.put("BIZ_MODE",$("#BIZ_MODE").val());
	merchInfo.put("PAY_MODE",$("#PAY_MODE").val());
	merchInfo.put("BUS_NEED_DEGREE",$("#BUS_NEED_DEGREE").val());
	//3加入列表
	var offerData = PageData.getData($("#class_"+offerId));
	offerData.put("MERCHINFO",merchInfo);
	PageData.setData($("#class_"+offerId), offerData);
}

//过滤资费类商品单独处理
function filterPriceOffer(curOptionOfferDataset, priceOfferDataset)
{
	for(var i = curOptionOfferDataset.length; i > 0; i--)
	{
		var subOfferData = curOptionOfferDataset.get(i-1);
		if(subOfferData.get("OFFER_TYPE") == "D")
		{
			priceOfferDataset.add(subOfferData);
//			curOptionOfferDataset.removeAt(i-1);
		}
	}
}

function dealBbossOptOfferPreviewByOptOfferData(curOptionOfferDataset, topOfferId)
{
	var preViewOptOfferName = "oo_" + topOfferId + "_OPTOFFER_ID";
	var preViewOptOfferIds = $("input[name="+preViewOptOfferName+"]");

	// 取得商品id，进而取得pageData
	var merchId = $("#prodDiv_TOP_OFFER_ID").val();
	var merchInfo = PageData.getData($("#class_"+merchId)).get("MERCHINFO");
	var curMerchOperCode = "";
	if(merchInfo){
		curMerchOperCode = merchInfo.get("OPERTYPE");
	}

	for(var j = 0; j < preViewOptOfferIds.length; j++)
	{
		$("#div_"+preViewOptOfferIds[j].value).remove();
	}

	if(typeof(curOptionOfferDataset) != "undefined")
	{
		var priceOfferDataset = new Wade.DatasetList();
		filterPriceOffer(curOptionOfferDataset, priceOfferDataset);
		
		//处理资费类商品
		dealPriceOfferPreviewByPriceOfferData(priceOfferDataset, topOfferId);
		
		for(var i = 0; i < curOptionOfferDataset.length; i++)
		{
			var curOptOfferData = curOptionOfferDataset.get(i);
			if("D" == curOptOfferData.get("OFFER_TYPE"))
			{//过滤资费
				continue;
			}
			var curOfferId = curOptOfferData.get("OFFER_ID");
			var curOfferName = curOptOfferData.get("OFFER_NAME");
			var curOfferIndex = curOptOfferData.get("OFFER_INDEX");
			var isShowTag = curOptOfferData.get("IS_SHOW_SET_TAG");

			if("CrtMb" != $("#cond_OPER_TYPE").val() && "ChgMb" != $("#cond_OPER_TYPE").val() && "DstMb" != $("#cond_OPER_TYPE").val()) {
				// 将商品的操作码转为子商品的操作码
				var prodOperType = dealProdOperCodeByMerchOperCode(curMerchOperCode, curOptOfferData.get("OPER_CODE"), curOfferId);

				// 产品暂停  curOfferId:子商品   topOfferId：商品
				if(("3" == prodOperType || "4" == prodOperType || "11" == prodOperType || "12" == prodOperType)
					&& curOptOfferData.get("OPER_CODE") != ACTION_DELETE){
					if($("#sub_"+topOfferId).length == 0)
					{
						var subHtml = "<div class='sub' id='sub_"+topOfferId+"'><div class='main' >"
							+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
							+ "<ul id='optOffer_" + topOfferId + "'>"
							+ "</ul></div></div>";

						$("#li_"+topOfferId).append(subHtml);

					}

					var optionalOfferHtml ="<li id='div_"+curOfferId+"_"+curOfferIndex+"'>";
					if("3" == prodOperType) {
						optionalOfferHtml +="<div class='group link' ontap='bbossProductPause("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
							+ "<div class='main'>"
							+ "<span class='e_ico-pause'></span>"
							+"	" + curOfferName + "</div>"
							+ "<div class='more'></div>"
							+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
							+ "</div>";
					}
					if("4" == prodOperType) {
						optionalOfferHtml += "<div class='group link' ontap='bbossProductRecovery("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
							+ "<div class='main'>"
							+ "<span class='e_ico-play'></span>"
							+"	" + curOfferName + "</div>"
							+ "<div class='more'></div>"
							+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
							+ "</div>";
					}
					if ("11" == prodOperType) {
						optionalOfferHtml += "<div class='group link' ontap='bbossProductPreDst("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
							+ "<div class='main'>"
							+ "<span class='e_ico-play'></span>"
							+"	" + curOfferName + "</div>"
							+ "<div class='more'></div>"
							+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
							+ "</div>";
					}
					if ("12" == prodOperType) {
						optionalOfferHtml += "<div class='group link' ontap='bbossProductPreDstBack("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
							+ "<div class='main'>"
							+ "<span class='e_ico-play'></span>"
							+"	" + curOfferName + "</div>"
							+ "<div class='more'></div>"
							+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
							+ "</div>";
					}
					optionalOfferHtml += "</li>";
					$("#optOffer_"+topOfferId).append(optionalOfferHtml);
					continue;
				}
			}
			if (curMerchOperCode == "7" && curOptOfferData.get("OPER_CODE") == ACTION_DELETE) {
				if ($("#sub_" + topOfferId).length == 0) {
					var subHtml = "<div class='sub' id='sub_'"+topOfferId+"'><div class='main' >"
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
						+ "<ul id='optOffer_" + topOfferId + "'>"
						+ "</ul></div></div></div>";

					$("#li_" + topOfferId).append(subHtml);
				}

				var optionalOfferHtml = "<li id='div_"+curOfferId+"_"+curOfferIndex+"'><div class='group link' ontap='openEnterpriseOfferPopupItem(" + curOfferId + "," + topOfferId + ",false," + curOfferIndex + ");' prodOperType=" + prodOperType + ">"
					+ "<div class='main'>"
					+ "<span class='e_ico-delete'></span>"
					+ "	" + curOfferName + "</div>"
					+ "<div class='more'></div>"
					+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
					+ "</div></li>";

				$("#optOffer_" + topOfferId).append(optionalOfferHtml);
				continue;
			}
			if (curMerchOperCode == "" && curOptOfferData.get("OPER_CODE") == ACTION_DELETE) {
				if ($("#sub_" + topOfferId).length == 0) {
					var subHtml = "<div class='sub' id='sub_'"+topOfferId+"'><div class='main' >"
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
						+ "<ul id='optOffer_" + topOfferId + "'>"
						+ "</ul></div></div></div>";

					$("#li_" + topOfferId).append(subHtml);
				}

				var optionalOfferHtml = "<li id='div_"+curOfferId+"_"+curOfferIndex+"'><div class='group link' ontap='openEnterpriseOfferPopupItem(" + curOfferId + "," + topOfferId + ",false," + curOfferIndex + ");' prodOperType=" + prodOperType + ">"
					+ "<div class='main'>"
					+ "<span class='e_ico-delete'></span>"
					+ "	" + curOfferName + "</div>"
					+ "<div class='more'></div>"
					+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex + "'/>"
					+ "</div></li>";

				$("#optOffer_" + topOfferId).append(optionalOfferHtml);
				continue;
			}
			if(curOptOfferData.get("OPER_CODE") != ACTION_DELETE)
			{
				if($("#sub_"+topOfferId).length == 0)
				{
					var subHtml = "<div class='sub' id='sub_"+topOfferId+"'><div class='main' >"
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
						+ "<ul id='optOffer_" + topOfferId + "'>"
						+ "</ul></div></div>";

					$("#li_"+topOfferId).append(subHtml);
				}

				var optionalOfferHtml = "<li id='div_"+curOfferId+"_"+curOfferIndex+"'><div class='group link' ontap='openEnterpriseOfferPopupItem("+curOfferId+","+topOfferId+",false,"+curOfferIndex+");' prodOperType="+prodOperType+">"
					+ "<div class='main'>" + curOfferName + "</div>";
				if(isShowTag == "true")
				{
					optionalOfferHtml += "<div class='side'><span class='e_tag e_tag-red'>待设置</span></div>";
				}
				optionalOfferHtml +=  "<div class='more'></div>" 
					+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + "_" + curOfferIndex+"'/>"
					+ "</div></li>";

//				if($("#"+curOfferId).attr("SELECT_FLAG") != "0" && curOfferIndex =="0")
//				{
//					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteBbossOptionalOffer(" + curOfferId + "," + topOfferId +","+ curOfferIndex+");'></span></div>";
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

function bbossBizVerify(operType) {
	var offerId = $("#cond_OFFER_ID").val();
	var offerInfo = PageData.getData($("#class_" + offerId));
	// 取不到数据，直接返回（防止特殊情况）不是bboss业务直接返回
	if (!offerInfo || "BOSG" != offerInfo.get("BRAND")) {
		return true;
	}
	// 取得产品操作类型
	var merchpOperType = $("#OfferChaSpecPart").attr("MERCHP_OPER_TYPE");
	// 商品的情况返回
	if(!merchpOperType) {
		return true;
	}
	// 为预受理的情况
	if (operType == "CrtUs" && "10" == merchpOperType) {
		MessageBox.alert("BBOSS产品预受理的时候不需要填写付费计划！");
		//return false;
	}

	if (operType == "ChgUs" && ("55" != merchpOperType && "5" != merchpOperType)) {
		MessageBox.alert("BBOSS产品变更非资费变更的情况不需要填写付费计划！");
		return false;
	}
	return true;
}

function addGrpItem(partId,parentPartId){
	 grpItempopId = partId;
	 grpItemareaId = parentPartId;
	var grpItemPart = $("#proParamPagePop input,#proParamPagePop textarea");

	for(var i = 0 ; i < grpItemPart.length; i ++)
	{
		var grpItemPartName = $(grpItemPart[i]).attr("name");
		var tempUpload = $("#"+grpItemPartName+"_name");
		$("#"+grpItemPartName+"_name").val("");
		$("#"+grpItemPartName).val("");
		$("#"+grpItemPartName+"_btn_close").attr("style","display:none;");
		$("#"+grpItemPartName+"_btn_download").attr("style","display:none;");
		
	}
	if(parentPartId=='GRPITEM99903001'&&partId=='GRPITEM999033719'){
		showPopup('UI-popup','UI-popup-query',true);
	}
	if(parentPartId=='GRPITEM99903002'&&partId=='GRPITEM999033720'){
		showPopup('UI-popup','UI-popup-query2',true);
	}
	if(parentPartId=='GRPITEM500050101'&&partId=='GRPITEM50005010006'){
		showPopup('UI-popup','UI-popup-query2',true);
	}
	
	//业务状态修改
	isChangeGroupValue=false;
	
}
function showGrpItemPop(popLevel) {
	$("#popup").attr("class", "c_popup c_popup-half c_popup-show c_popup-level-"+popLevel);
	$("#"+grpItempagePop).attr("class", "c_popup c_popup-half c_popup-show c_popup-level-1");
	$("#"+grpItempopId).attr("class", "c_popupItem c_popupItem-show");
}

function addGrpItemInfos(el) {
	// 进行判断是否是修改
	if(isChangeGroupValue){
		saveChangeGrpItemInfo(el);
		return;
	}
	var grpItemInfos = $(el).parent().parent().parent().parent().parent().find("input");//$("#"+grpItempopId+" input,#"+grpItempopId+" textarea");
    var putPoint = $("span[element_id="+grpItempopId+"]").parent().parent().parent().parent();
    var count = 0;
	$("input[element_id^='"+grpItemInfos[0].getAttribute("element_id")+"'][attr_group]").each(function(){
		if(count<$(this).attr("attr_group")){
			count = $(this).attr("attr_group");
		}
	});
	count++;
    //拼接条码
    var pageBody = "<div class=\"c_list c_list-form\" group_id=\""+count+"\" area=\""+grpItempopId+"\"><ul>";
    pageBody+=putAttrGroup(grpItemInfos,count,"0");
	putPoint.after(pageBody);
	hidePopup('UI-popup');
	
	
	// 1 读取列表数据
/*	var elementList = $(el).parents("div[class*='c_scroll']").find('input');
	// 2 找到原数据位置
	var putPoint = $("div[group_id=\""+elementList[0].getAttribute("attr_group")+"\"][area*=\"GRPITEM"+elementList[0].getAttribute("element_id")+"\"");
	// 3 进行比较来判断业务类型 并进行插值
	for (var i = 0; i < elementList.length; i++) {
		var element = putPoint.find("input[element_id*=\""+elementList[i].getAttribute("element_id")+"\"]");
		if(element.val()!=null&&elementList[i]!=null){
			if(element.val()!=elementList[i].getAttribute("value")){
				element.attr("value",elementList[i].getAttribute("value"));
				element.attr({
					oper_code: ACTION_UPDATE
				});
			}
		}
	}*/
}

function changeGrpItemInfos(el){
	// 1 找到位置
	var grpItemInfos = $(el).parent().parent().parent().parent().parent().find("input");//$("#"+grpItempopId+" input,#"+grpItempopId+" textarea");
	var popupList = $("div[class='c_popup']");
    var putPoint = $("div[class='c_popup']").find("#"+grpItemInfos[0].getAttribute("element_id").split("_")[0]);
	// 2 读取数据 通过elementid写入数据 带入组号
	for (var i = 0; i < grpItemInfos.length; i++) {
		var element = popupList.find('input[element_id='+grpItemInfos[i].getAttribute("element_id").split("_")[0]+']');
		element.val(grpItemInfos[i].getAttribute("value"));
		element.attr("attr_group",grpItemInfos[i].getAttribute("attr_group"));
	}
	// 3 弹出框
	var area = $(el).parents("div[area]").attr("area");
	if(area=='GRPITEM999033719'){
		showPopup('UI-popup','UI-popup-query',true);
	}
	if(area=='GRPITEM999033720'){
		showPopup('UI-popup','UI-popup-query2',true);
	}
	if(area=='GRPITEM50005010006'){
		showPopup('UI-popup','UI-popup-query2',true);

	}
	// 4 业务控制
	isChangeGroupValue = true;
	
}

function saveChangeGrpItemInfo(el){
	// 1 读取列表数据
	var elementList = $(el).parents("div[class*='c_scroll']").find('input');
	// 2 找到原数据位置
	var putPoint = $("div[group_id=\""+elementList[0].getAttribute("attr_group")+"\"][area*=\"GRPITEM"+elementList[0].getAttribute("element_id")+"\"");
	// 3 进行比较来判断业务类型 并进行插值
	for (var i = 0; i < elementList.length; i++) {
        var elementType = elementList[i].getAttribute("x-wade-uicomponent");
        if(elementType == "textfield" || elementType == "simpleupload")
        {
            var elementId = elementList[i].getAttribute("element_id");
            elementVal = $("#input_B"+elementId).val();
        }else if(elementType == "select")
        {
        	elementVal = elementList[i].getAttribute("value");
        }else{
        	       	
        }
		var element = putPoint.find("input[element_id*=\""+elementList[i].getAttribute("element_id")+"\"]");
		if(element.val()!=null&&elementList[i]!=null){
			if(element.val()!=elementVal){
				element.attr("value",elementVal);
				element.attr({
					oper_code: ACTION_UPDATE
				});
			}
		}
	}
	// 4 关闭弹出框
	hidePopup('UI-popup');
	// 5 业务控制
	isChangeGroupValue = false;
};

function pointGrpItemInfos(infos){
	infos.eachKey(function(key,item,index,totalcount){
		var grpItempopId = "GRPITEM";
		if("999033719"==key.split("B")[1]){
			grpItempopId+="999033719";
		}else if("999033720"==key.split("B")[1]){
			grpItempopId+="999033720";
		}else{
			grpItempopId+="50005010006";
		}
		var putPoint = $("span[element_id="+grpItempopId+"]").parent().parent().parent().parent();
		var operCode = ACTION_EXITS;
		for (var i = 0; i < item.length; i++) {
			pointGrpItemInfo(key,item[i],putPoint,grpItempopId,operCode);
		}
	});
}

function pointGrpItemInfos2(offerChaSpec){

	var attrCode = offerChaSpec.get("ATTR_CODE");
	var grpItempopId = "GRPITEM";
	if("999033719"==attrCode){
		grpItempopId+="999033719";
	}else if("999033720"==attrCode){
		grpItempopId+="999033720";
	}else{
		grpItempopId+="50005010006";

	}
	var operCode = offerChaSpec.get("OPER_CODE");
	var putPoint = $("span[element_id="+grpItempopId+"]").parent().parent().parent().parent();
	pointGrpItemInfo(attrCode,offerChaSpec,putPoint,grpItempopId,operCode);
}

/**
 * @param key
 * @param element
 * @param putPoint
 * @param grpItempopId
 * @param operCode
 */
function pointGrpItemInfo(key,element,putPoint,grpItempopId,operCode){
	var count = element.get("ATTR_GROUP");
	var page = putPoint.parent().find("div[area='"+grpItempopId+"'][group_id='"+count+"']");
	var elementData = $("input[element_id*='"+key+"']");

/*	if(typeof($("#BSEL"+key.split("B")[0]))!="undefined"){
		infoItem = $("#"+key);
		//infoItem = $("#BSEL"+key.split("B")[1]);
	}else if(typeof($("#"+key))!="undefined"){
		infoItem = $("#"+key);
	}*/
	var pageBody = "";
	if(page.html()==null||page.html()==""){
		pageBody+= "<div class=\"c_list c_list-form\" group_id=\""+count+"\" area=\""+grpItempopId+"\"><ul>";
		pageBody+="<li class=\"link\">	<div class=\"label\">";
	}else{
		pageBody+="<li style=\"display:none;\" class=\"link\">	<div class=\"label\">";
	}	
    pageBody+=elementData.attr("desc")+"</div><div class=\"value\"><div class=\"e_mix\">";
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
    pageBody+="name=\""+""+elementData.attr("name")+"_"+count+"\" ";
    pageBody+="id=\""+""+elementData.attr("id")+"_"+count+"\" ";
    pageBody+="autocomplete=\""+""+elementData.attr("autocomplete")+"\" ";
    pageBody+="element_id=\""+""+elementData.attr("element_id")+"\" ";
    pageBody+="desc=\""+""+elementData.attr("desc")+"\" ";
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

function hideGrpPopItem(popLevel){
	$("#"+grpItempopId).attr("class", "c_popupItem c_popupItem");
	$("#"+grpItempagePop).attr("class", "c_popup c_popup-half c_popup-level-1");
	$("#popup").attr("class", "c_popup c_popup-half c_popup-show c_popup-level-"+popLevel);
}

function divStr(param_data,groupId,tempCount,element_id)
{
	var d_c_s = $("<li class='link'>");
	var d_d_s = $("<div class='main'>");
	d_c_s.append(d_d_s);
	d_d_s.append("属性组" + groupId);
	var d_d_e = $("</div>");
	d_c_s.append(d_d_e);

	var d_e_s = $("<div class='more'></div>");
	var d_f_s = $("<div class='fn' ontap='showGrpItemPopPage(\"areaId_A"+element_id+"_"+tempCount+"\");'><span class='e_ico-search'></span></div>");
	var d_g_s = $("<div class='fn' ontap='deleteGrpItem(this);'><span class='e_ico-delete'></span></div>");

	d_c_s.append(d_e_s);
	d_c_s.append(d_f_s);
	d_c_s.append(d_g_s);

	var d_h_s = $("<textarea style='display:none;' id='areaId_A"+element_id+"_"+tempCount+"' name='areaId_A"+element_id+"_"+tempCount+"' isGrpItem='true'>");
	var d_h_e = $("</textarea>");

	d_c_s.append(d_h_s);
	d_h_s.append(param_data.toString());
	d_c_s.append(d_h_e);

	var d_c_e = $("</li>");
	d_c_s.append(d_c_e);

	return d_c_s;
}

function putAttrGroup(grpItemInfos,count,opercode){
	
	var pageBody = "";
    var elementVal="";
	for (var i=0; i<grpItemInfos.length;i++){
        if(i==0){
            pageBody+="<li class=\"link\">	<div class=\"label\">";
        }else{
            pageBody+="<li style=\"display:none;\" class=\"link\">	<div class=\"label\">";
        }
        var elementType = grpItemInfos[i].getAttribute("x-wade-uicomponent");
        if(elementType == "textfield" || elementType == "simpleupload")
        {
            var elementId = grpItemInfos[i].getAttribute("element_id");
            elementVal = $("#input_B"+elementId).val();
        }else if(elementType == "select")
        {
        	elementVal = grpItemInfos[i].getAttribute("value");
        }else{
        	       	
        }
                
        pageBody+=grpItemInfos[i].getAttribute("desc")+"</div><div class=\"value\"><div class=\"e_mix\">";
        pageBody+="<input x-wade-uicomponent=\"text\" ";
        pageBody+="value=\""+""+elementVal+"\" ";
        pageBody+="oper_code=\""+opercode+"\" ";
        pageBody+="type=\"text\" ";
        pageBody+="name=\""+""+grpItemInfos[i].getAttribute("name")+count+"\" ";
        pageBody+="id=\""+""+grpItemInfos[i].getAttribute("id")+"_"+count+"\" ";
        pageBody+="element_id=\""+""+grpItemInfos[i].getAttribute("element_id")+"\" ";
        if(opercode!=ACTION_DELETE){
        	 pageBody+="sytle=\"text-decoration:line-through\" ";
        }
        pageBody+="desc=\""+grpItemInfos[i].getAttribute("desc")+"\" ";
        pageBody+="attr_group=\""+count+"\"  readonly > ";
	    if(opercode!=ACTION_DELETE){
	    	pageBody+="<span class='e_ico-edit e_ico-pic-xxxs' ontap=\"changeGrpItemInfos(this)\" tip=\"进行修改\"></span>";	
	    	pageBody+="<span class=\"e_ico-delete e_ico-pic-xxxs\" tip=\"去除条目\" ontap=\"deleteGrpItem(this);\"></span></div></div></li>";
		}else{
			pageBody+="<span class=\"e_ico-cancel e_ico-pic-xxxs\" tip=\"取消删除\" ontap=\"undeleteGrpItem(this);\"></span></div></div></li>";
		}
    }
    pageBody+="</ul></div>";
    return pageBody;
}

function showGrpItemPopPage(dataAreaId){
	var butAdd = $("button[name='Button_Add']");
	var butView = $("button[name='Button_View']");
	for(var i =0;i< butAdd.length;i++){
	    $(butAdd[i]).attr("style","display:none;");
	    $(butView[i]).attr("style","");
	}
	var initData = new Wade.DataMap($("#"+dataAreaId).text());
	initGrpItemPopup(initData);
	showPopup(grpItempagePop,grpItempopId);
}

function initGrpItemPopup(initData){
	grpItempagePop = initData.get("GRPITEM_PAGE_POP");
	grpItempopId = initData.get("GRPITEM_POP_ID");
	var popItem = $("#PopPart input").each(function(){
		var grpItemEleId = $(this).attr("element_id");
		var elementDatas = initData.get("ELEMENT_DATAS");
		this.value=elementDatas.get(grpItemEleId);
		$(this).val(elementDatas.get(grpItemEleId));
		$(this).attr("value", elementDatas.get(grpItemEleId));
		if(!elementDatas.get(grpItemEleId)){
			$(this).val("");
		}
		if ("select" == $(this).attr("x-wade-uicomponent")){
			var tempId = $(this).attr("id");
			//解决联动下拉框显示问题
			var changeMethod = $(this).attr("changemethod");
			if (changeMethod && changeMethod.length >0){
				onValueChangeUnit(this);
			}
		}
	});
}

function dealGrpItemInit(grpItems,chaCode,chaValue) {
	var spiltNum = grpItems.lastIndexOf("_");
	var grpItemId = grpItems.substring(0,spiltNum);
	var grpId = grpItems.substring(spiltNum+1);

	var areaObj = $("#"+grpItemId+"_List");
	var initDataObj = $("#areaId_A"+grpItemId+"_"+grpId);
	// 有ul表格，但没有li行
	if(areaObj.length>0 && initDataObj.length<=0)
	{
		var a = areaObj.children();
		var param_data = new Wade.DataMap();
		var element_data = new Wade.DataMap();
		// var popInfo = $("#"+grpItemId+" li").attr("ontap").split("'");
		// param_data.put("GRPITEM_POP_ID", popInfo[1]);
		// param_data.put("GRPITEM_PAGE_POP", popInfo[5]);
		// element_data.put(chaCode,chaValue);
		// param_data.put("ELEMENT_DATAS", element_data);
		var d_c_s = divStr(param_data,Number(grpId),grpId, grpItemId);
		a.append(d_c_s);
	}
	// ul、li都有
	else if (areaObj.length>0 && initDataObj.length>0){
		var initData = new Wade.DataMap(initDataObj.text());
		var elementData = initData.get("ELEMENT_DATAS");
		elementData.put(chaCode,chaValue);
		initDataObj.text(initData.toString());
	}
	//ul、li都没有
	else
	{
		var a = areaObj.children();
		var a = $("#"+grpItemId);
		var d_a_s = $("<div class='c_list c_list-form' id='"+grpItemId+"_List'>");
		var d_b_s = $("<ul>");
		d_a_s.append(d_b_s);
		var param_data = new Wade.DataMap();
		var element_data = new Wade.DataMap();
		// var popInfo = $("#"+grpItemId+" li").attr("ontap").split("'");
		// param_data.put("GRPITEM_POP_ID", popInfo[1]);
		// param_data.put("GRPITEM_PAGE_POP", popInfo[5]);
		// element_data.put(chaCode,chaValue);
		// param_data.put("ELEMENT_DATAS", element_data);
		var d_c_s = divStr(param_data,Number(grpId),grpId,grpItemId);
		d_b_s.append(d_c_s);

		a.after(d_a_s);
	}
}

function deleteGrpItem(obj)
{
	var operCode = PageData.getData($("#prodDiv_OFFER_DATA")).get("OPER_CODE");
	if(ACTION_CREATE == operCode){
		$(obj).parent().parent().parent().parent().parent().remove();
		return true;
	}else{
		var body = $(obj).parent().parent().parent().parent().parent();
		var title = body.find("input:visible");
		var all = body.find('input');
		if(ACTION_CREATE == title.attr("oper_code")){
			$(obj).parent().parent().parent().parent().parent().remove();
			return true;
		}else{
			$(obj).prev().remove();
			$(obj).attr({'class':'e_ico-cancel e_ico-pic-xxxs','tip':'取消删除','ontap':'undeleteGrpItem(this)'});
			title.attr("style","text-decoration:line-through");
			body.attr("tip","已经删除");
			for (var i = all.length - 1; i >= 0; i--) {
				all[i].setAttribute("oper_code",ACTION_DELETE);
			}
		}
	}
	
	// for(var i = i ; i <= count ; i++){
	// 	divStr(param_data,i,i,element_id);
	// }
	
}

function undeleteGrpItem(obj){
	var body = $(obj).parent().parent().parent().parent().parent();
	var title = body.find("input:visible");
	var all = body.find('input');
	$(obj).attr({'class':'e_ico-delete e_ico-pic-xxxs','tip':'取消删除','ontap':'deleteGrpItem(this)'});
	$(obj).prev().after("<span class='e_ico-edit e_ico-pic-red e_ico-pic-xxxs'  ontap=\"changeGrpItemInfos(this)\" tip=\"进行修改\"></span>");
	title.removeAttr("style");
	body.removeAttr("tip");
	for (var i = all.length - 1; i >= 0; i--) {
		all[i].setAttribute("oper_code",ACTION_EXITS);
	}
}

function viewGrpItemInfos(obj){
	hideGrpPopItem("2");
}

/**
 * 校验变更是否暂停产品
 */
function checkChgPause(subOfferDataset){
	var flag=false;

	if(subOfferDataset.length<0||subOfferDataset.length==0){
		MessageBox.alert("提示信息", "请先选择需要暂停的子产品！");
		return flag;
	}
	
	for(var i=subOfferDataset.length;i>0;i--){
		var subOfferData = subOfferDataset.get(i-1);
		var offerType= subOfferData.get("OFFER_TYPE");
		var operCode=subOfferData.get("OPER_CODE");
		if("P"==offerType&&("5"==operCode)){
			flag= true;
			continue;
		}
		if("P"==offerType&&("5"!=operCode)){
			subOfferDataset.remove(subOfferData);
			continue;
		}
		
	}
	if(!flag){
		MessageBox.alert("提示信息", "请先选择需要暂停的子产品!");
	}else{
		var offerPart=PageData.getData($(".e_SelectOfferPart"));
		offerPart.put("SUBOFFERS",subOfferDataset);
		PageData.setData($(".e_SelectOfferPart"), offerPart);
	}

	return  flag;

	
	
}
function undeleteGrpItem(obj){
	var body = $(obj).parent().parent().parent().parent().parent();
	var title = body.find("input:visible");
	var all = body.find('input');
	$(obj).attr({'class':'e_ico-delete e_ico-pic-xxxs','tip':'取消删除','ontap':'deleteGrpItem(this)'});
	$(obj).prev().after("<span class='e_ico-edit e_ico-pic-red e_ico-pic-xxxs'  ontap=\"changeGrpItemInfos(this)\" tip=\"进行修改\"></span>");
	title.removeAttr("style");
	body.removeAttr("tip");
	for (var i = all.length - 1; i >= 0; i--) {
		all[i].setAttribute("oper_code",ACTION_EXITS);
	}
}

function viewGrpItemInfos(obj){
	hideGrpPopItem("2");
}
/**
 * 校验变更是否恢复产品
 */
function checkChgContinue(subOfferDataset){
	
	var flag=false;
	if(subOfferDataset.length<0||subOfferDataset.length==0){
		MessageBox.alert("提示信息", "请先选择需要恢复的子产品！");
		return flag;
	}
	for(var i=subOfferDataset.length;i>0;i--){
		var subOfferData = subOfferDataset.get(i-1);
		var offerType= subOfferData.get("OFFER_TYPE");
		var operCode=subOfferData.get("OPER_CODE");
		if("P"==offerType&&("6"==operCode)){
			flag= true;
			continue;
		}
		if("P"==offerType&&("6"!=operCode)){
			subOfferDataset.remove(subOfferData);
			continue;
		}
		
	}
	if(!flag){
		
		MessageBox.alert("提示信息", "请先选择需要恢复的子产品!");
	}else{
		var offerPart=PageData.getData($(".e_SelectOfferPart"));
		offerPart.put("SUBOFFERS",subOfferDataset);
		PageData.setData($(".e_SelectOfferPart"), offerPart);
	}
	
	return  flag;
	
}
/**
 * 校验变更是否产品属性
 */
function checkChgSpecChas(subOfferDataset){

	var flag=false;
	
	if(subOfferDataset.length<0||subOfferDataset.length==0){
		MessageBox.alert("提示信息", "请先选择需要修改属性的子产品！");
		return flag;
	}
	for(var i=subOfferDataset.length;i>0;i--){
		var subOfferData = subOfferDataset.get(i-1);
		var offerType= subOfferData.get("OFFER_TYPE");
		//非产品不校验
		if("P"!=offerType){
			continue;
		}
		var  chaSpecs=subOfferData.get("OFFER_CHA_SPECS");
		var ischangechavaue=subOfferData.get("IS_CHA_VALUE_CHANGE");
		if(typeof(chaSpecs) == "undefined" || typeof(chaSpecs) != "object" || chaSpecs.length == 0){
			//subOfferDataset.remove(subOfferData);
			continue;
		}
		if("true"==ischangechavaue){
			flag= true;
			continue;
		}
		
	}
	if(!flag){
		MessageBox.alert("提示信息", "请先选择需要修改属性的子产品重新操作!");
	}else{
		var offerPart=PageData.getData($(".e_SelectOfferPart"));
		offerPart.put("SUBOFFERS",subOfferDataset);
		PageData.setData($(".e_SelectOfferPart"), offerPart);
	}
	return  flag;
	
}

/**
 * 校验组成关系变更
 */
function checkChgModifyGroup(subOfferDataset){

	var flag=false;
	
	if(subOfferDataset.length<0||subOfferDataset.length==0){
		MessageBox.alert("提示信息", "请先选择需要新增或删除的子产品！");
		return flag;
	}
	for(var i=subOfferDataset.length;i>0;i--){
		var subOfferData = subOfferDataset.get(i-1);
		var offerType= subOfferData.get("OFFER_TYPE");
		var operCode= subOfferData.get("OPER_CODE");
		
		//非产品不校验
		if("P"!=offerType){
			continue;
		}
		if(operCode=="1"||operCode=="0"){
		   flag=true;
		   continue;
		}else{
		 subOfferDataset.remove(subOfferData);
		 continue;
		}
		
	}
	if(!flag){
		MessageBox.alert("提示信息", "请先选择需要子产品重新操作!");
	}else{
		var offerPart=PageData.getData($(".e_SelectOfferPart"));
		offerPart.put("SUBOFFERS",subOfferDataset);
		PageData.setData($(".e_SelectOfferPart"), offerPart);
	}
	return  flag;
	
}

/**
 * 校验资费变更
 */
function checkChgPrice(subOfferDataset, merchOperType){

	
	if(subOfferDataset.length<0||subOfferDataset.length==0){
		MessageBox.alert("提示信息", "请先选择需要变更资费子产品！");
		return false;
	}
	for(var i=subOfferDataset.length;i>0;i--){
		var hasPrice=false;
		var subOfferData = subOfferDataset.get(i-1);
		var offerType= subOfferData.get("OFFER_TYPE");
		var operCode= subOfferData.get("OPER_CODE");
		var suboffers=subOfferData.get("SUBOFFERS");//元素产品
		
		//非产品不校验
		if("P"!=offerType){
			continue;
		}
		if(!suboffers){			
			MessageBox.alert("提示信息", "您选择的产品未定购基础服务，数据异常！");
			return false;
		}
		if(suboffers.length<0||suboffers.length==0){
			 subOfferDataset.remove(subOfferData);
			 continue;
		}
		for(var j=0;j<suboffers.length;j++){
			var subOffer=suboffers.get(j);
			var subofferType=subOffer.get("OFFER_TYPE");
			var opercode=subOffer.get("OPER_CODE");
			if("D"!=subofferType){
				continue;
			}
			
			//即存在新增删除资费操作
			if("2"!=opercode){
				hasPrice=true;
				break;
			}
			//变更了资费看是否变更资费参数
			if("2"==opercode){
				//取出资费参数
				var attrparams=subOffer.get("OFFER_CHA_SPECS");
				hasPrice=checkBBossPriceCha(attrparams);
			}
		}
		if(!hasPrice){
			if(merchOperType == "55"){
				continue;
			}else{
				subOfferDataset.remove(subOfferData);
				continue;
			}

		}
		
	}
	
	var localDis = 0;

	for(var i=0; i<subOfferDataset.length; i++ ){
		var tempData = subOfferDataset.get(i);
		var offerType= tempData.get("OFFER_TYPE");
		var operCode= tempData.get("OPER_CODE");
		var suboffers= tempData.get("SUBOFFERS");//元素产品
		
		//非产品不校验
		if("P"!=offerType){
			continue;
		}else{
			localDis++;
		}
	}
	
	if(localDis<1){
		MessageBox.alert("提示信息", "未变更任何产品资费或商品资费！");
		return  false;

	}
	//重新设置子产品列表
	var offerPart=PageData.getData($(".e_SelectOfferPart"));
	offerPart.put("SUBOFFERS",subOfferDataset);
	PageData.setData($(".e_SelectOfferPart"), offerPart);
	return  true;
}
/**
 *  校验bboss资费参数是否变更
 */
function checkBBossPriceCha(offerchas){
	var changeCha=false;
	if(typeof(offerchas) == "undefined" || typeof(offerchas) != "object" || offerchas.length == 0){
		return changeCha;
	}
	for(var i=0;i<offerchas.length;i++){
		var offercha=offerchas.get(i);
		var operCode=offercha.get("OPER_CODE");
		if("3"!=operCode){
			changeCha=true;
		}
		
	}
	return changeCha;
}
/**
 *  校验bboss集团提交数据
 */
function checkBBossEnterPrise(){
	var operType = $("#cond_OPER_TYPE").val();
	var flag=false;
	if(operType == "ChgUs"){
		var subOfferDataset=PageData.getData($(".e_SelectOfferPart")).get("SUBOFFERS");
		var merchOperType=PageData.getData($(".e_SelectOfferPart")).get("MERCHINFO").get("OPERTYPE");

		//暂停
		if("3"==merchOperType){
			flag=checkChgPause(subOfferDataset);
			return flag;
			
		}else 	if("4"==merchOperType){//恢复
			
			flag=checkChgContinue(subOfferDataset);
			return flag;
		}else if("9"==merchOperType){//变更属性
			flag=checkChgSpecChas(subOfferDataset);
			return flag;
		}else if("7"==merchOperType){//变更组成关系
			flag=checkChgModifyGroup(subOfferDataset);
			return flag;
		}else if("5"==merchOperType||"55"==merchOperType){//变更资费
			flag=checkChgPrice(subOfferDataset, merchOperType);
			return flag;
		}
		
		
	}
	return true;
	
}

/*
 *集团BBOSS某些产品的特殊校验  
 */
function paramSpelValidate()
{
	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	
	var operType = curOfferData.get("MERCHP_OPER_TYPE");
	//剔除没有修改的资费
	var subOffers = curOfferData.get("SUBOFFERS");
	
	var productElements = new Wade.DatasetList();
	
	for(var i=0;i<subOffers.length;i++){
		var subOffer = subOffers.get(i);
		var operCode = subOffer.get("OPER_CODE");
		if("3" != operCode){
			productElements.add(subOffer);
		}
	}
	
   if(operType=='5')
   {      
          //未变更任何资费，不准提交
          if(productElements==null || productElements=='' || productElements.length==0)
          {				
        	 MessageBox.alert("未变更任何资费!");
             return false;
          }
          var merDisCount=0;
          var locDisCount=0;
          for(var i=0;i<productElements.length;i++)
          {
              var elementData = productElements.get(i);
			 var elementTypeCode = elementData.get("OFFER_TYPE");
			 var elementId = elementData.get("OFFER_CODE");
			 if(elementTypeCode == 'D'){
			    //查询是否是商品资费变更
			   
		             Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify','qryProductToMerch',
					'&ELEMENT_ID='+elementId,
					function(d){
							var param = d.map.result;
							if(param=="true")
							{
							     merDisCount++;
							}else
							{
							    locDisCount++;  
							}
				
					},function(e,i)
					{
						MessageBox.alert("操作失败");
						result=false;
					},
					{async:false}); 
			 }     
          }
          if(merDisCount==0)
          { 
             MessageBox.alert("商品资费变更未变更商品资费!");
          }
          if(locDisCount>0)
          { 
             MessageBox.alert("商品资费变更不能变更商品本地资费!");
             return false;
          }
          
   }
    // 本地资费变更
   if(operType=='55')
   {
          
          var merDisCount=0;
          var locDisCount=0;
          var subOfferDataSet = PageData.getData($(".e_SelectOfferPart"));
          var pkgInfos = subOfferDataSet.get("GRP_PACKAGE_INFO");
      	  var offerCode = curOfferData.get("OFFER_CODE");
      	  
      	  var pkgCount = 0;
          if(pkgInfos){
        	  var proPkgInfos = pkgInfos.get(offerCode);
        	  if(proPkgInfos){
            	  for(var i=0; i<proPkgInfos.length; i++){
            		  var proPkgInfo = proPkgInfos.get(i);
            		  var modifyTag = proPkgInfo.get("MODIFY_TAG");
            		  if(modifyTag != "EXIST"){
            			  pkgCount++;
            		  }
            	  }
        	  }
        	  
          }
          for(var i=0;i<productElements.length;i++)
          {
             var elementData = productElements.get(i);
			 var elementTypeCode = elementData.get("OFFER_TYPE");
			 var elementId = elementData.get("OFFER_CODE");
			 if(elementTypeCode == 'D'){
			    //查询是否是商品资费变更
			   
		             Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify','qryProductToMerch',
					'&ELEMENT_ID='+elementId,
					function(d){
							var param = d.map.result;
							if(param=="true")
							{
							     merDisCount++;
							}else
							{
							    locDisCount++;  
							}
				
					},function(e,i)
					{ 
						MessageBox.alert("操作失败");
						return false;

					},
					{async:false}); 
			 }     
          }
          if(locDisCount==0 && pkgCount==0)
          { 
             MessageBox.alert("商品本地资费变更未变更商品本地资费!");

          }
          if(merDisCount>0)
          { 
             MessageBox.alert("商品本地资费变更不能变更商品资费!");
             return false;
          }
   }
   return true;
}