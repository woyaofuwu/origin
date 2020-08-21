
function  hasMerchOptCode(isMainOffer, offerIndex, topOfferId, offerId) {
    var merchpOperType ="";
    if(!isMainOffer && "BOSG" == PageData.getData($("#class_" + topOfferId)).get("BRAND_CODE")) {
        if("ecCrt" == $("#cond_OPER_TYPE").val() || "ecChg" == $("#cond_OPER_TYPE").val()) {
            // 主界面初始化的时候不会加上index
            if($("#div_"+offerId).children().length != 0){
                if(!$("#div_"+offerId).children()[0].getAttribute("prodOperType")) {
                    MessageBox.alert("请先填写商品信息，再填写子商品信息。");
                    return "-1";
                }
            }

            merchpOperType = $("#div_"+offerId + offerIndex).children()[0].getAttribute("prodOperType");
            if(!merchpOperType) {
                MessageBox.alert("请先填写商品信息，再填写子商品信息。");
                return "-1";
            }
        }
    }
    return merchpOperType;
}

function hideBbossMerchPage() {
    $("#proParamPage").css("display", "");
    $("#dynamicOfferParam").css("display", "");
    $("#BbossOfferParam").css("display", "none");
}

function showBbossMerchPage(el,operCode, topOfferId,ecUserId) {
    var merchInfo = PageData.getData($("#class_"+topOfferId)).get("MERCHINFO");
    if(merchInfo){
        $("#proParamPage").css("display", "none");
        $("#dynamicOfferParam").css("display", "none");
        $("#BbossOfferParam").css("display", "");
        initBBossOfferParam();
        forwardPopup(el, 'productChaSpecPopupItem');
    }
    else {
        // 刷新加载商品参数页面
        var offerId = $("#cond_OFFER_ID").val();
        var isBatch = $("#cond_IS_BATCH").val();

        
        
        var subInstId = "";
        
        subInstId = PageData.getData($("#class_" + offerId)).get("USER_ID")
        if(operCode == "memChg")
    	{
        	//此段逻辑待确认
//        	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
//    		var curProdData = curOfferData.get("PRODS").get(0);
//    		if(curProdData.get("PROD_SPEC_ID") == prodId)
//    		{
//    			subInstId = curProdData.get("USER_ID");
//    		}
    	}
        
        if(typeof(subInstId) == "undefined") {
            subInstId =$("#cond_USER_ID").val();;
        }
        var param ="&OPER_TYPE=" + operCode + "&OFFER_ID=" + offerId + "&SUB_INST_ID=" + subInstId+"&EC_USER_ID="+ecUserId+"&IS_BATCH="+isBatch;
        $.beginPageLoading("数据加载中......");
        $.ajax.submit("", "", param, "productChaSpecPopupItem", function(data){
                $.endPageLoading();
            },
            function(error_code,error_info,derror){
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
            },{async:false});
        initBBossOfferParam();
        $("#proParamPage").css("display", "none");
        $("#dynamicOfferParam").css("display", "none");
        $("#BbossOfferParam").css("display", "");
        forwardPopup(el, 'productChaSpecPopupItem');
    }
}

//提交BBOSS商品特征规格(成员)
function submitBBOSSChaSpec()
{
    var merchInfo = new Wade.DataMap();

    //1-组装商品 信息
    merchInfo.put("OPERTYPE",$("#OPERTYPE").val());
    merchInfo.put("MEB_TYPE",$("#MEB_TYPE").val());
   // alert(merchInfo);
    //2加入列表
    var offerId = $("#prodDiv_TOP_OFFER_ID").val();
    var offerData = PageData.getData($("#class_"+offerId));
    offerData.put("MERCHINFO",merchInfo);
    PageData.setData($("#class_"+offerId), offerData);
}
/**
 * 交验成员变更是否设置商品信息
 */
function checkBbossMerChInfo(){
    var merchId = $("#prodDiv_TOP_OFFER_ID").val();
    var merchInfo = PageData.getData($("#class_"+merchId)).get("MERCHINFO");
    if(merchInfo==null){
    	MessageBox.alert("提示信息", "请设置商品操作类型！");
        return false;
    }else{
    	 return true;
   }
}
function dealBbossOptOfferPreviewByOptOfferData(curOptionOfferDataset, topOfferId)
{
    var preViewOptOfferName = "oo_" + topOfferId + "_OPTOFFER_ID";
    var preViewOptOfferIds = $("input[name="+preViewOptOfferName+"]");

    if("memDel" != $("#cond_OPER_TYPE").val()) {
        // 取得商品id，进而取得pageData
        var merchId = $("#prodDiv_TOP_OFFER_ID").val();
        var merchInfo = PageData.getData($("#class_"+merchId)).get("MERCHINFO");
        var curMerchOperCode = PageData.getData($("#class_"+merchId)).get("OPER_CODE");
    }

    for(var j = 0; j < preViewOptOfferIds.length; j++)
    {
        $("#div_"+preViewOptOfferIds[j].value).remove();
    }

    if(typeof(curOptionOfferDataset) != "undefined")
    {
        for(var i = 0; i < curOptionOfferDataset.length; i++)
        {
            var curOptOfferData = curOptionOfferDataset.get(i);
            var curOfferId = curOptOfferData.get("OFFER_ID");
            var curOfferName = curOptOfferData.get("OFFER_NAME");
            var curOfferIndex = curOptOfferData.get("OFFER_INDEX");
			var isShowTag = curOptOfferData.get("IS_SHOW_SET_TAG");


            if("memCrt" != $("#cond_OPER_TYPE").val() && "memChg" != $("#cond_OPER_TYPE").val() && "memDel" != $("#cond_OPER_TYPE").val()) {
                // 将商品的操作码转为子商品的操作码
                var prodOperType = dealProdOperCodeByMerchOperCode(curMerchOperCode, curOptOfferData.get("OPER_CODE"), curOfferId);

                // 产品暂停  curOfferId:子商品   topOfferId：商品
                if("3" == prodOperType || "4" == prodOperType){
                    if($("#sub_"+topOfferId).length == 0)
                    {
                        var subHtml = "<div class='sub' id='sub_"+topOfferId+"'><div class='main' >"
                            + "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
                            + "<ul id='optOffer_" + topOfferId + "'>"
                            + "</ul></div></div>";

                        $("#li_"+topOfferId).append(subHtml);

                    }

                    var optionalOfferHtml ="<li id='div_"+curOfferId+curOfferIndex+"'>";
                    if("3" == prodOperType) {
                        optionalOfferHtml += "<div class='group link' ontap='bbossProductPause("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
                            + "<div class='main'>"
                            + "<span class='e_ico-pause'></span>"
                            +"	" + curOfferName + "</div>"
                            + "<div class='more'></div>"
                            + "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + curOfferIndex + "'/>"
                            + "</div>";
                    }
                    if("4" == prodOperType) {
                        optionalOfferHtml += "<div class='group link' ontap='bbossProductRecovery("+curOfferId+","+topOfferId+","+curOfferIndex+");' prodOperType="+prodOperType+">"
                            + "<div class='main'>"
                            + "<span class='e_ico-play'></span>"
                            +"	" + curOfferName + "</div>"
                            + "<div class='more'></div>"
                            + "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + curOfferIndex + "'/>"
                            + "</div>";
                    }
                    optionalOfferHtml += "</li>";
                    $("#optOffer_"+topOfferId).append(optionalOfferHtml);
                    continue;
                }
            }
            if (curMerchOperCode == "7" && curOptOfferData.get("OPER_CODE") == ACTION_DELETE) {
                if ($("#sub_" + topOfferId).length == 0) {
                    var subHtml = "<div class='sub' id='sub_"+topOfferId+"'><div class='main' >"
                        + "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
                        + "<ul id='optOffer_" + topOfferId + "'>"
                        + "</ul></div></div>";

                    $("#li_" + topOfferId).append(subHtml);
                }

                var optionalOfferHtml = "<li id='div_"+curOfferId + curOfferIndex+"'><div class='group link' ontap='openEnterpriseOfferPopupItem(" + curOfferId + "," + topOfferId + ",false," + curOfferIndex + ");' prodOperType=" + prodOperType + ">"
                    + "<div class='main'>"
                    + "<span class='e_ico-delete'></span>"
                    + "	" + curOfferName + "</div>"
                    + "<div class='more'></div>"
                    + "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + curOfferIndex + "'/>"
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

//                if($("#"+curOfferId).attr("SELECT_FLAG") != "0" && curOfferIndex =="0")
//                {
//                    optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteBbossOptionalOffer(" + curOfferId + "," + topOfferId +","+ curOfferIndex+");'></span></div>";
//                }
//                else
//                {
//                    optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='fn e_dis e_ico-delete' /></div>";
//                }
                $("#optOffer_"+topOfferId).append(optionalOfferHtml);
            }
//			if(curOptOfferData.get("OPER_CODE") != ACTION_DELETE)
//			{
//				var optionalOfferHtml = "<div class='main' id='div_" + curOfferId + "' prodOperType="+prodOperType+">"
//					+ "<div class='title'></div>"
//					+ "<div class='content'><span class='e_ico-tree'>" + curOfferName + "</span></div>"
//					+ "<input type='hidden' name='oo_" + topOfferId + "_OPTOFFER_ID' value='" + curOfferId + curOfferIndex+"'/>"
//					+ "</div>"
//					+ "<div class='side' ontap='openEnterpriseOfferPopupItem("+curOfferId+","+topOfferId+",false,"+curOfferIndex+");'><span class='e_tag e_tag-red'>设置子商品</span></div>";
//
//				if($("#"+curOfferId).attr("SELECT_FLAG") != "0" && curOfferIndex =="0")
//				{
//					optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteBbossOptionalOffer(" + curOfferId + "," + topOfferId +","+ curOfferIndex+");'></span></div>";
//				}
//
//				$("#li_"+topOfferId).append({tag:"div",class:"sub",id:"div_"+curOfferId+curOfferIndex,html:optionalOfferHtml});
//			}
        }
    }

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