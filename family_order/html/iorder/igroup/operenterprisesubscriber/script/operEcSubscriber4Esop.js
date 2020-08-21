function initEsop()
{
	if($("#IS_ESOP").val() != "true")
	{
		return ;
	}

	var operType = $("#cond_OPER_TYPE").val();

	if(operType == "CrtUs")
	{
		showOpenInfo();
		
		if($("#ESOP_IF_RES_CODE").val() == "YES")
		{
			$("#cond_SERIAL_NUMBER_INPUT").attr("disabled", false);
			$("#checkPic").removeClass("e_dis");
			$("#checkPic").attr("ontap", "checkAccesssNum();");
		}
		else
		{
			$("#cond_SERIAL_NUMBER_INPUT").attr("disabled", true);
			$("#checkPic").addClass("e_dis");
			$("#checkPic").removeAttr("ontap");
		}
	}
	else if(operType == "ChgUs")
	{
		showChangeInfo();
	}
	else
	{
		showLogoutInfo();
	}
	
	//是否有附加销售品，控制附加销售品区域是否显示
	if($("#ESOP_HAS_ATTACH_OFFER").val() == "true" && operType != "DstUs")
	{
		$("#AttachOfferPart").css("display","");
	}
	else
	{
		$("#AttachOfferPart").css("display","none");
	}
	
	//判断当前登陆集团与选择变更或注销的商品归属的集团是否一致，不一致则刷新登陆集团
	var groupId = $("#esop_GROUP_ID").val();
	var custId = $("#cond_CUST_ID").val();
	var groupCustInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	if (groupCustInfo) 
	{
		var loginCustId = groupCustInfo.get("CUST_ID");
		if(loginCustId != custId)
		{
			$.enterpriseLogin.refreshGroupInfo(groupId);
		}
	}
	else
	{//如果没有获取到登陆信息，则用esop传过来的集团登陆
		$.enterpriseLogin.refreshGroupInfo(groupId);
	}
	
	var esopOfferDataStr = $("#ESOP_OFFER_DATA").html();
	var offerData = new Wade.DataMap(esopOfferDataStr);
	PageData.setData($(".e_SelectOfferPart"), offerData);
	
	var offerId = offerData.get("OFFER_ID");
	if(operType != "DstUs")
	{
		initProductPopupItem(offerId,offerId,true,0);
	}
}