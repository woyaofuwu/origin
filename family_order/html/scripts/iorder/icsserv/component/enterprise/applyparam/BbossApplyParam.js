//选择产品
function checkedBbossProduct(el)
{
	if($(el).attr("checked"))
	{
		var operType = $("#cond_OPER_TYPE").val();//20:新增,21:资源变更,22:资费变更,23:删除,
		if("20"==operType)//20:新增
		{
			$(el).closest("li").children(".side").html("<span class='e_tag e_tag-red'>待设置</span>");
			$(el).closest("li").children(".side").attr("ontap", "initBbossProductParam(this);");
		}
		else if ("21"==operType)//21:资源变更
		{
			$(el).closest("li").children(".side").html("<span class='e_tag e_tag-green'>待修改</span>");
			$(el).closest("li").children(".side").attr("ontap", "initBbossProductParam(this);");
		}
		else if ("22"==operType)//22:资费变更
		{
			MessageBox.alert("提示信息", "暂时不支持修改资费信息，请重新选择操作方式！");
		}
		else if ("23"==operType)//23:删除
		{
			
		}
	}
	else
	{
		$(el).closest("li").children(".side").html("");
		$(el).closest("li").children(".side").attr("ontap", "");
	}
}

//新增bboss产品参数
function initBbossProductParam(el)
{
	var offerChaHiddenId = "OFFER_CHA_" + $(el).closest("li").attr("id");
	
	initOfferCha(offerChaHiddenId);
}

//新增bboss专线
function addBbossDataLine(el)
{
	var liObjId = $(el).closest("li").attr("id");
	var offerDataHiddenId = "OFFER_DATA_" + liObjId;
	var offerData = new Wade.DataMap($("#"+offerDataHiddenId).text());
	
	loadBbossDataLineHtml(offerData);

	if($("#BBOSS_PRODUCT_UL").children("li").length > 1)
	{
		$("#BBOSS_PRODUCT_UL").find(".e_dis").removeClass("e_dis");
	}
}

//新增bboss专线html
function loadBbossDataLineHtml(offerData)
{
	var offerCode = offerData.get("OFFER_CODE");
	var offerName = offerData.get("OFFER_NAME");
	var maxIndex = $("#BBOSS_PRODUCT_MAX_INDEX").val();
	
	var liHtml = "<li id='"+offerCode+"_"+maxIndex+"'>";
	liHtml += "<div class='fn'><input name='PRODUCT_TAG' id='PRODUCT_"+maxIndex+"' type='checkbox' ontap='checkedBbossProduct(this);' checked='checked'></div>";
	liHtml += "<div class='main'>";
	liHtml += "<div class='title'>【"+offerCode+"】"+offerName+"</div>";
	liHtml += "</div>";
	liHtml += "<div class='side link' ontap='initBbossProductParam(this)'><span class='e_tag e_tag-red'>待设置</span></div>";
	liHtml += "<div class='more'></div>";
	liHtml += "<div class='fn'><span class='e_ico-add' ontap='addBbossDataLine(this);'></span></div>";
	liHtml += "<div class='fn'><span class='e_ico-delete' ontap='deleteBbossDataLine(this);'></span></div>";
	liHtml += "<div id='OFFER_DATA_"+offerCode+"_"+maxIndex+"' style='display:none;'>"+offerData.toString()+"</div>";
	liHtml += "<input type='hidden' name='OFFER_CHA_"+offerCode+"_"+maxIndex+"' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value=''/>";
	liHtml += "</li>";
	$("#BBOSS_PRODUCT_UL").append(liHtml);
	
	maxIndex++;
	$("#BBOSS_PRODUCT_MAX_INDEX").val(maxIndex);
}

//删除bboss专线产品
function deleteBbossDataLine(el)
{
	var liObjId = $(el).closest("li").attr("id");
	$("#"+liObjId).remove();
	
	if($("#BBOSS_PRODUCT_UL").children("li").length == 1)
	{
//		var lastLiObjId = $("#BBOSS_PRODUCT_UL").children("li").attr("id");
		$("#BBOSS_PRODUCT_UL").find(".e_ico-delete").addClass("e_dis");
	}
}

