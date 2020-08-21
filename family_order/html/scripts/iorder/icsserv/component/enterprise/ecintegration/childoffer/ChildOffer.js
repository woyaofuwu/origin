//打开子群组商品设置组件
function openChildOfferPopupItem(el)
{
	if(!$("#cond_GROUP_ID").val())
	{
		MessageBox.alert("提示信息", "请先进行客户信息设置或者在左侧认证集团信息！");
		return false;
	}
	var brandCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("BRAND_CODE");
	if(brandCode == "BOSG")
	{
		openChildOfferBbossPopupItem(el);
	}
	else if(brandCode == "ESPG")
	{
		openChildOfferEspgPopupItem(el);
	}
	else
	{
		openChildOfferOtherPopupItem(el);
	}
}

function openChildOfferPopupItem4AddMeb(el)
{
	if(!$("#cond_GROUP_ID").val())
	{
		MessageBox.alert("提示信息", "请先进行客户信息设置或者在左侧认证集团信息！");
		return false;
	}
	var brandCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("BRAND_CODE");
	if(brandCode == "BOSG")
	{
		openChildOfferBbossPopupItem4AddMeb(el);
	}
	else if(brandCode == "ESPG")
	{
		openChildOfferEspgPopupItem4AddMeb(el);
	}
	else
	{
		openChildOfferOtherPopupItem4AddMeb(el);
	}
}

//关闭子群组商品设置组件
function closeChildOfferPopupItem()
{
	hidePopup("popup03");
}

function openChildOfferOtherPopupItem(el)
{
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	var param = "&GROUP_ID="+$("#cond_GROUP_ID").val()+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&EFFECT_NOW="+$("#EFFECT_NOW").val()+"&ACTION=queryOfferForCreate";
	var offerData = new Wade.DataMap($("#CHILD_OFFER_DATA_"+offerId).text());
	if(offerData && offerData.length > 0)
	{
		param += "&HAS_OFFER_DATA=true";
		param += "&AGREEMENT_ELEMENT_LIST="+"";//第二次打开不再加载附件中的元素
	}
	else
	{//如果已经完成过一次商品设置，则不再传入附件中的元素
		var eleOfferData = new Wade.DataMap($("#AGREEMENT_ELEMENT_DATA_"+offerId).text());
		param += "&AGREEMENT_ELEMENT_LIST="+eleOfferData.toString();
	}
	var widePhoneFalg = $("#oattr_WIDE_FLAG").val();
	if("true"!=widePhoneFalg&&"2222"==offerCode&&""==widePhoneFalg){
		$.validate.alerter.one($("#oattr_WIDE_PHONE")[0], "因该集团未订购商务宽带及IMS专线，请输入宽带号码进行查询！");
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "CHILD_OFFER_SETUP_ITEM", function(data){
		$.endPageLoading();
		if(offerData && offerData.length > 0)
		{
			initChildOfferPageByOfferData(offerData);
		}
		var ifResCode = data.get("IF_RES_CODE");
		if(ifResCode == "1")
		{
			$("#CHILD_EC_SERIAL_NUMBER_INPUT").attr("disabled", false);
		}else{
			$("#CHILD_EC_SERIAL_NUMBER_INPUT").attr("disabled", true);
		}
		showPopup("popup03", "CHILD_OFFER_SETUP_ITEM", true);
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function openChildOfferEspgPopupItem(el)
{
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	var param = "&GROUP_ID="+$("#cond_GROUP_ID").val()+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&EFFECT_NOW="+$("#EFFECT_NOW").val()+"&ACTION=queryOfferEspgForCreate";
	var offerData = new Wade.DataMap($("#CHILD_OFFER_DATA_"+offerId).text());
	if(offerData && offerData.length > 0)
	{
		param += "&HAS_OFFER_DATA=true";
		param += "&AGREEMENT_ELEMENT_LIST="+"";//第二次打开不再加载附件中的元素
	}
	else
	{//如果已经完成过一次商品设置，则不再传入附件中的元素
		var eleOfferData = new Wade.DataMap($("#AGREEMENT_ELEMENT_DATA_"+offerId).text());
		param += "&AGREEMENT_ELEMENT_LIST="+eleOfferData.toString();
	}
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "CHILD_OFFER_SETUP_ITEM", function(data){
		$.endPageLoading();
		if(offerData && offerData.length > 0)
		{
			initChildOfferPageByOfferData(offerData);
		}
		$("#CHILD_OFFER_SUBMIT").attr("ontap", "saveChildOfferEspgData(this)");
		showPopup("popup03", "CHILD_OFFER_SETUP_ITEM", true);
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function openChildOfferOther4BbossProduct(el)
{
	var ecMebType = $(el).attr("EC_MEB_TYPE");
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_BBOSS_"+ecMebType+"_PRODUCT_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_BBOSS_"+ecMebType+"_PRODUCT_OFFER_ID_]").attr("OFFER_CODE")
	var param = "&GROUP_ID="+$("#cond_GROUP_ID").val()+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&EFFECT_NOW="+$("#EFFECT_NOW").val();
	if(ecMebType == "MEB")
	{
		var ecProdOfferId = $("#CHILD_BBOSS_MEB_PRODUCT_OFFER_ID_"+offerId).attr("EC_PRODUCT_OFFER_ID");
		param = param +"&EC_PRODUCT_OFFER_ID="+ecProdOfferId+"&ACTION=queryBbossMebOfferForCreate";
	}
	else
	{
		param = param +"&ACTION=queryBbossEcOfferForCreate";
	}

	var offerData = new Wade.DataMap($("#CHILD_BBOSS_"+ecMebType+"_PRODUCT_OFFER_DATA_"+offerId).text());
	if(offerData && offerData.length > 0)
	{
		param += "&HAS_OFFER_DATA=true";
	}
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "CHILD_OFFER_SETUP_ITEM", function(data){
		$.endPageLoading();
		if(offerData && offerData.length > 0)
		{
			initBbossProductPageByOfferData(offerData, ecMebType);
		}
		//如果是bboss业务，则重新设置提交事件
		if(ecMebType == "MEB")
		{
			$("#CHILD_OFFER_SUBMIT").attr("ontap", "saveBbossMebProductOfferData(this);");
		}
		else
		{
			$("#CHILD_OFFER_SUBMIT").attr("ontap", "saveBbossProductOfferData(this);");
		}
		showPopup("popup02", "CHILD_OFFER_SETUP_ITEM", true);
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function openChildOfferEspgPopupItem4AddMeb(el)
{
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	var userId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("USER_ID");
	var serialNumber = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("SERIAL_NUMBER");
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	var param = "&GROUP_ID="+$("#cond_GROUP_ID").val()+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&USER_ID="+userId
		+"&SERIAL_NUMBER="+serialNumber+"&BPM_TEMPLET_ID="+bpmTempletId+"&ACTION=queryOfferEspgForAddMeb";
	
	if($("#oattr_OPER_TYPE").val())
	{//变更主页面商品的操作类型
		param += "&CHILD_OFFER_OPER_TYPE="+$("#oattr_OPER_TYPE").val();
	}
	if("DelMeb"==$("#oattr_OPER_TYPE").val()){
		var offerData = new Wade.DatasetList($("#CHILD_DELMEB_DATA_"+offerId).text());
		param += "&CHILD_DELMEB_DATA="+offerData;
	}
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "CHILD_OFFER_SETUP_ITEM", function(data){
		$.endPageLoading();
		
		var offerName = $(el).closest("li").find(".title").text();
		$("#ChildOfferSetupHead").find(".back").text(offerName);
		$("#EC_INS_OFFER_NAME").text(offerName);
		
		var offerData = new Wade.DataMap($("#CHILD_OFFER_DATA_"+offerId).text());
		if(offerData && offerData.length > 0)
		{
			initChildOfferPageByOfferData(offerData);
		}
		
		if($.isFunction(window["initMemberListByOperType"]))
		{
			window["initMemberListByOperType"]();
		}
		//待修改
		$("#CHILD_OFFER_SUBMIT").attr("ontap", "saveChildOfferEspgData(this)");
		showPopup("popup03", "CHILD_OFFER_SETUP_ITEM", true);
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//打开子群组商品设置，只设置成员商品信息
function openChildOfferOtherPopupItem4AddMeb(el)
{
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	var userId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("USER_ID");
	var serialNumber = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("SERIAL_NUMBER");
	var param = "&GROUP_ID="+$("#cond_GROUP_ID").val()+"&OFFER_ID="+offerId+"&OFFER_CODE="+offerCode+"&USER_ID="+userId+"&SERIAL_NUMBER="+serialNumber+"&ACTION=queryOfferForAddMeb";
	
	if($("#oattr_OPER_TYPE").val())
	{//变更主页面商品的操作类型
		param += "&CHILD_OFFER_OPER_TYPE="+$("#oattr_OPER_TYPE").val();
	}
	
	if("DelMeb"==$("#oattr_OPER_TYPE").val()){
		var offerData = new Wade.DatasetList($("#CHILD_DELMEB_DATA_"+offerId).text());
		param += "&CHILD_DELMEB_DATA="+offerData;
	}
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "", param, "CHILD_OFFER_SETUP_ITEM", function(data){
		$.endPageLoading();
		
		var offerName = $(el).closest("li").find(".title").text();
		$("#ChildOfferSetupHead").find(".back").text(offerName);
		$("#EC_INS_OFFER_NAME").text(offerName);
		
		var offerData = new Wade.DataMap($("#CHILD_OFFER_DATA_"+offerId).text());
		if(offerData && offerData.length > 0)
		{
			initChildOfferPageByOfferData(offerData);
		}
		
		if($.isFunction(window["initMemberListByOperType"]))
		{
			window["initMemberListByOperType"]();
		}
		
		showPopup("popup03", "CHILD_OFFER_SETUP_ITEM", true);
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//根据商品数据初始化子群组商品页面
function initChildOfferPageByOfferData(offerData)
{
	console.log(offerData.toString());
	var operCode = offerData.get("OPER_CODE");//集团商品操作类型
	
	//1、初始化子群组集团商品
	var ecOfferData = offerData.get("EC_OFFER");
	if(ecOfferData && ecOfferData.length > 0 && operCode == "0")
	{
		initEcChildOfferPart(ecOfferData);
	}
	
	//2、初始化子群组集团商品公共信息
	var ecCommonData = offerData.get("EC_COMMON_INFO");
	if(ecCommonData && ecCommonData.length > 0 && operCode == "0")
	{
		initEcCommonInfoPart(ecCommonData);
	}
	
	//3、初始化子群组成员商品
	var mebOfferData = offerData.get("MEB_OFFER");
	if(mebOfferData && mebOfferData.length > 0)
	{
		initMebChildOfferPart(mebOfferData);
	}
	
	//4、初始化子群组成员商品公共信息
	var mebCommonData = offerData.get("MEB_COMMON_INFO");
	if(mebCommonData && mebCommonData.length > 0)
	{
		initMebCommonInfoPart(mebCommonData);
	}
	
	//5、初始化成员列表
	var mebFile = offerData.get("MEB_FILE");
	if(mebFile && mebFile.length > 0)
	{
		$("#MEMBER_FILE_INFO").text(mebFile.toString());
	}
	var mebList = offerData.get("MEB_LIST");
	if(mebList && mebList.length > 0)
	{
		if (ecOfferData.get("OFFER_CODE") == "380700") {
			initTVMebListPart(mebList);
		} else if (ecOfferData.get("OFFER_CODE") == "380300") {
			initWifiMebListPart(mebList);
		} else if(ecOfferData.get("OFFER_CODE") != "7341"){
			initMebListPart(mebList);
		}
	}

	//6、初始化子群组商品选择的商品组内的商品
	initEcMebSelGroupOfferData(ecOfferData.get("OFFER_ID"));
	
	if(ecOfferData.get("BRAND_CODE") == "ESPG"){
		if (offerData.get("ORDER_SATFF_INFO")){
			//ESP产品 初始化ESP订购人员
			initOrderStaffData(offerData.get("ORDER_SATFF_INFO"));
		}
			
	}
	
	
}

//根据商品数据初始化子群组商品页面
function initBbossProductPageByOfferData(offerData, ecMebType)
{
	if(ecMebType == "MEB")
	{
		initMebChildOfferPart(offerData);
	}
	else
	{
		initEcChildOfferPart(offerData);
	}
	
	var offerId = offerData.get("OFFER_ID");
	var childOfferSelGroupOfferData = new Wade.DataMap($("#CHILD_BBOSS_"+ecMebType+"_PRODUCT_OFFER_SelGroupOffer_"+offerId).text());
	if(childOfferSelGroupOfferData && childOfferSelGroupOfferData.length > 0)
	{
		var ecSelGroupOffer = childOfferSelGroupOfferData.get("EC_SelGroupOffer");
		if(ecSelGroupOffer && ecSelGroupOffer.length > 0)
		{
			$("#EC_SelGroupOffer").text(ecSelGroupOffer.toString());
		}
		var mebSelGroupOffer = childOfferSelGroupOfferData.get("MEB_SelGroupOffer");
		if(mebSelGroupOffer && mebSelGroupOffer.length > 0)
		{
			$("#MEB_SelGroupOffer").text(mebSelGroupOffer.toString());
		}
	}
}


//初始化子群组集团商品
function initEcChildOfferPart(ecOfferData)
{
	$("#EC_OFFER_DATA").text(ecOfferData.toString());
	
	var offerId = ecOfferData.get("OFFER_ID");
	var hasOfferCha = ecOfferData.get("HAS_OFFER_CHA");
	var offerChaList = ecOfferData.get("OFFER_CHA_SPECS");
	if(hasOfferCha == "true" && offerChaList && offerChaList.length > 0)
	{
	    //如果数据结构中有属性，则将"待设置"修改为"已设置"
		$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE=EC][OFFER_ID="+offerId+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
	}
	
	var subOfferList = ecOfferData.get("SUBOFFERS");
	if(subOfferList && subOfferList.length > 0)
	{
		for(var i = 0, size = subOfferList.length; i < size; i++)
		{
			var subOffer = subOfferList.get(i);
			var selectFlag = subOffer.get("SELECT_FLAG");
			var subOfferChaList = subOffer.get("OFFER_CHA_SPECS");
			if(selectFlag == "0")
			{
				if(subOfferChaList && subOfferChaList.length > 0)
				{
					$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE=EC][OFFER_ID="+subOffer.get("OFFER_ID")+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
				}
			}
			else
			{
				if(subOffer.get("OPER_CODE") != ACTION_DELETE)
				{
					addSubOfferHtml(subOffer, subOffer.get("START_DATE"), subOffer.get("END_DATE"), subOffer.get("OPER_CODE"), "EC");
				}
			}
		}
	}

	//云酒馆、 云WiFi无需定制   &&   和商务TV无需定制
	if(offerId == "110000380300"  ||  offerId == "110000380700" || offerId == "110000921015")
	{
		return;
	}

	//定制初始化
	var grpPackageList = ecOfferData.get("GRP_PACKAGE_INFO");

	if(grpPackageList && grpPackageList.length > 0)
	{
        $("#DZ_MEB_OFFER_DATA").text(grpPackageList.toString());
        for(var i = 0, size = grpPackageList.length; i < size; i++)
        {
            var grpPackage = grpPackageList.get(i);
            if(grpPackage.get("ELE_OFFER_ID") && $("#DZ_MEB_+"+grpPackage.get("ELE_OFFER_ID")).length == 0)
            {
                //如果是构成关系，ELE_OFFER_ID值为空
                addGrpPackageHtml(grpPackageList.get(i), "DZ_MEB");
            }
        }
    }
}

//初始化子群组集团商品公共信息
function initEcCommonInfoPart(ecCommonData)
{
	var resList = ecCommonData.get("RES_INFO");
	var res = resList.get(0);
	var serialNumber = res.get("SERIAL_NUMBER");
	$("#CHILD_EC_SERIAL_NUMBER_INPUT").val(serialNumber);
	$("#CHILD_EC_REAL_SERIAL_NUMBER").val(serialNumber);
	$("#CHILD_EC_RES_TYPE_CODE").val(res.get("RES_TYPE_CODE"));
	$("#CHILD_EC_IF_RES_CODE").val(res.get("IF_RES_CODE"));
	$("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val("true");
	
	var acctInfo = ecCommonData.get("ACCT_INFO");
	if(acctInfo && acctInfo.length > 0)
	{
        //初始化账户信息
        initEcChildAccount(acctInfo);
    }

    var payPlanInfo = ecCommonData.get("PAY_PLAN_INFO");
    if(payPlanInfo && payPlanInfo.length > 0)
    {
        //初始化付费类型
        initEcChildPayplan(payPlanInfo);
    }
}

//初始化付费类型
function initEcChildPayplan(payplan)
{
	var payplanName = "";
	if(payplan.indexOf("P") > -1)
	{
		payplanName = "个人付费";
		var selHtml = "<li class='link' idx='1' title='个人付费' val='P'><div class='main'>个人付费</div></li>";
		$("#CHILD_MEB_PAY_PLAN_float").find("ul").append(selHtml);
	}
	if(payplan.indexOf("G") > -1)
	{
		var payplanNameG= "集团付";
		if(""!=payplanName){
			payplanName = payplanName+","+payplanNameG;	
		}else{
			payplanName = payplanNameG;
		}
		
		var selHtml = "<li class='link' idx='2' title='集团付' val='G'><div class='main'>集团付</div></li>";
		$("#CHILD_MEB_PAY_PLAN_float").find("ul").append(selHtml);
	}
	
	
	
	if(payplan)
	{
		$("#payPlanValue").val(payplan);
		
		var payplanHtml = "<span class='text'>"+payplanName+"</span>";
	     $("#payPlanTexts").html(payplanHtml);
	}
}

//初始化账户信息
function initEcChildAccount(account)
{
	$("#cond_ACCT_DEAL").val(account.get("ACCT_DEAL"));
	$("#CHILD_EC_ACCOUNT_DATA").text(account.toString());
	if(account.get("ACCT_DEAL") == "1")
	{//合户
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
		
		var html = "<span class='text'>"+account.get("ACCT_NAME")+"</span>";
		html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+account.get("ACCT_ID")+"</span>";
		$("#i_acctCombPart .value").html(html);
	}
	else
	{
		$("#i_acctCombPart").css("display", "none");
		$("#i_acctSelPart").css("display", "");
		
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_COMBINE_ID">'+account.get("ACCT_NAME")+'</span>');
	}
}

//初始化子群组成员商品
function initMebChildOfferPart(mebOfferData)
{
	$("#MEB_OFFER_DATA").text(mebOfferData.toString());
	
	var offerId = mebOfferData.get("OFFER_ID");
	var hasOfferCha = mebOfferData.get("HAS_OFFER_CHA");
	var offerChaList = mebOfferData.get("OFFER_CHA_SPECS");
	if(hasOfferCha == "true" && offerChaList && offerChaList.length > 0)
	{//如果数据结构中有属性，则将"待设置"修改为"已设置"
		$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE=MEB][OFFER_ID="+offerId+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
	}
	
	var subOfferList = mebOfferData.get("SUBOFFERS");
	if(subOfferList && subOfferList.length > 0)
	{
		for(var i = 0, size = subOfferList.length; i < size; i++)
		{
			var subOffer = subOfferList.get(i);
			var selectFlag = subOffer.get("SELECT_FLAG");
			var subOfferChaList = subOffer.get("OFFER_CHA_SPECS");
			if(selectFlag == "0")
			{
				if(subOfferChaList && subOfferChaList.length > 0)
				{
					$("#CHILD_OFFER_SETUP_ITEM").find("div.side[EC_MEB_TYPE=MEB][OFFER_ID="+subOffer.get("OFFER_ID")+"]").html("<span class='e_tag e_tag-green'>已设置</span>");
				}
			}
			else
			{
				if(subOffer.get("OPER_CODE") != ACTION_DELETE)
				{
					addSubOfferHtml(subOffer, subOffer.get("START_DATE"), subOffer.get("END_DATE"), subOffer.get("OPER_CODE"), "MEB");
				}
			}
		}
	}
}

//初始化子群组成员商品公共信息
function initMebCommonInfoPart(mebCommonData)
{
	//成员角色
	var roleCodeB = mebCommonData.get("ROLE_CODE_B");
	$("#CHILD_MEB_REL_SUBSCRIBER_ROLE").val(roleCodeB);
	//成员付费类型
	var planTypeCode = mebCommonData.get("PLAN_TYPE_CODE");
	$("#CHILD_MEB_PAY_PLAN").val(planTypeCode);
	
	
}

//初始化成员列表
function initMebListPart(mebList)
{
	for(var i = 0, size = mebList.length; i < size; i++)
	{
//		var sn = mebList.get(i).get("SERIAL_NUMBER");
		drawMemberListHtml(mebList.get(i));
	}
}
//初始化和商务TV成员列表
function initTVMebListPart(mebList)
{
	for(var i = 0, size = mebList.length; i < size; i++)
	{
		drawTVMemberListHtml(mebList.get(i));
	}
}
//初始化云WiFi成员列表
function initWifiMebListPart(mebList)
{
	for(var i = 0, size = mebList.length; i < size; i++)
	{
		drawWifiMemberListHtml(mebList.get(i));
	}
}
//初始化ESP订购人员信息
function initOrderStaffData(staffInfo)
{
	$("#attr_ORDERSTAFF").val(staffInfo.get("ORDER_STAFF_ID"));
	$("#attr_ORDERSTAFFID").val(staffInfo.get("ORDER_STAFF_ID"));
	$("#attr_ORDERPHONE").val(staffInfo.get("ORDER_STAFF_PHONE"));
}

//初始化子群组商品选择的商品组内的商品
function initEcMebSelGroupOfferData(offerId)
{
	var childOfferSelGroupOfferData = new Wade.DataMap($("#CHILD_OFFER_SelGroupOffer_"+offerId).text());
	if(childOfferSelGroupOfferData && childOfferSelGroupOfferData.length > 0)
	{
		var ecSelGroupOffer = childOfferSelGroupOfferData.get("EC_SelGroupOffer");
		if(ecSelGroupOffer && ecSelGroupOffer.length > 0)
		{
			$("#EC_SelGroupOffer").text(ecSelGroupOffer.toString());
		}
		var mebSelGroupOffer = childOfferSelGroupOfferData.get("MEB_SelGroupOffer");
		if(mebSelGroupOffer && mebSelGroupOffer.length > 0)
		{
			$("#MEB_SelGroupOffer").text(mebSelGroupOffer.toString());
		}
	}	
}

//服务号码校验
function checkAccesssNum()
{
	var resTypeCode = $("#CHILD_EC_RES_TYPE_CODE").val();
	var accessNumInput = $("#CHILD_EC_SERIAL_NUMBER_INPUT").val();
	var accessNum = $("#CHILD_EC_REAL_SERIAL_NUMBER").val();
	var isSuc = $("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val();
	var CHECK_SUCCESS = "0";
	var CHECK_FAIL = "-1";
	
	if(isSuc=="true" && accessNumInput == accessNum)
	{
		MessageBox.alert("提示信息", "服务号码已校验成功,请勿重复校验！");
		return false;
	}
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	var mainOfferCode = ecOfferData.get("OFFER_CODE");
	
	var param = "PRODUCT_ID="+mainOfferCode+"&SERIAL_NUMBER="+accessNumInput+"&RES_TYPE_CODE="+resTypeCode+"&ACTION=checkSerialNumber";
	$.beginPageLoading("服务号码校验中，请稍后......");
	$.ajax.submit("", "", param, "ChildOfferSerialNumberPart", function(data){
		$.endPageLoading();
		var retCode = data.get("X_RESULTCODE", "-1")//返回编码
		var retDesc = data.get("X_RESULTINFO", "");//返回结果描述
		
		if (CHECK_SUCCESS == retCode) {//校验成功
			$("#CHILD_EC_REAL_SERIAL_NUMBER").val(accessNumInput);
			$("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val(true);
			MessageBox.success("成功信息", retDesc);
		} else if(CHECK_FAIL == retCode) {//校验失败
			
			$("#CHILD_EC_REAL_SERIAL_NUMBER").val("");
			$("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val(false);
			MessageBox.error("错误信息", retDesc);
		} else {
			MessageBox.error("错误信息", "号码校验发生系统异常，请联系管理员！");
		}
		$("#CHILD_EC_IF_RES_CODE").val(data.get("IF_RES_CODE"));
		$("#CHILD_EC_RES_TYPE_CODE").val(data.get("RES_TYPE_CODE"));
		$("#CHILD_EC_SERIAL_NUMBER_INPUT").val(accessNumInput);
		
	},
	function(error_code,error_info,derror){
		$("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val(false);  //修改服务号码校验标记
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//删除定制信息
function deleteGrpPackage(el)
{
	var offerId = $(el).attr("OFFER_ID");
	
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	
	//校验该定制商品成员有没有选择，如果已选，则不允许删除
	var mebSelFlag = false;
	var mebOfferData = new Wade.DataMap($("#MEB_OFFER_DATA").text());
	if(ecOfferData.get("BRAND") == "BOSG")
	{//如果是bboss产品需要判断成员商品数据结构
		var mebOfferId = $("#CHILD_OFFER_BBOSS_MEB_PRODUCT_LIST_UL input:hidden[EC_PRODUCT_OFFER_ID="+ecOfferData.get("OFFER_ID")+"]").val();
		mebOfferData = new Wade.DataMap($("#CHILD_BBOSS_MEB_PRODUCT_OFFER_DATA_"+mebOfferId).text());
	}
	var mebSubOffers = mebOfferData.get("SUBOFFERS");
	if(mebSubOffers && mebSubOffers.length > 0)
	{
		for(var i = 0, size = mebSubOffers.length; i < size; i++)
		{
			if(offerId == mebSubOffers.get(i).get("OFFER_ID"))
			{
				mebSelFlag = true;
				break;
			}
		}
		if(mebSelFlag == true)
		{
			MessageBox.alert("提示信息", "成员已经订购，不能删除！如需删除，请先删除对应的成员子商品！");
			return ;
		}
	}
	
	//删除数据结构
	var grpPackageList = new Wade.DatasetList($("#DZ_MEB_OFFER_DATA").text());
	for(var i = 0, size = grpPackageList.length; i < size; i++)
	{
		if(offerId == grpPackageList.get(i).get("ELE_OFFER_ID"))
		{
			grpPackageList.removeAt(i);
			$("#DZ_MEB_OFFER_DATA").text(grpPackageList.toString())
			break;
		}
	}
	
	//删除页面元素
	$(el).closest("li").remove();
}

//删除子商品
function deleteSubOffer(el, ecMebType)
{
	var offerId = $(el).attr("OFFER_ID");
	var offerData = getOfferData(offerId, ecMebType);
	if(offerData)
	{
		var offerType = offerData.get("OFFER_TYPE");
		if(offerType == "D")
		{
			var expireYearMonth = offerData.get("END_DATE").substring(0, 8);
			if(offerData.get("OPER_CODE") != ACTION_CREATE && expireYearMonth == getNowSysTime().substring(0,8))
			{//如果当前商品是已订购实例，且资费商品当月失效
				MessageBox.alert("提示信息", "当前资费类商品本月失效，不能删除！");
				return false;
			}

			if(offerData.get("OPER_CODE") != ACTION_CREATE && "CANCEL_MODE4" == offerData.get("CANCEL_END_DATE"))
			{//cancel_mode==4的情况，不到期不可删除
				var expireDate = offerData.get("END_DATE");
				MessageBox.alert("提示信息", "当前资费将在"+expireDate+"到期结束，现在无法取消该优惠！");
				return false;
			}
		}
		MessageBox.confirm("提示信息", "是否删除当前商品【"+offerData.get("OFFER_CODE")+"】"+offerData.get("OFFER_NAME")+"？", function(btn){
			if(btn == "cancel")
			{
				return;
			}
			
			if(offerData.get("OPER_CODE") == ACTION_CREATE)
			{
				$(el).closest("li").remove(); //删除页面显示的元素
				updateOfferData(offerId, ecMebType, null); //删除数据
			}
			else
			{
				offerData.put("OPER_CODE", ACTION_DELETE);
				offerData.put("OLD_END_DATE", offerData.get("END_DATE")); 
				offerData.put("END_DATE", offerData.get("CANCEL_END_DATE"));
				updateOfferData(offerId, ecMebType, offerData); //更新数据
				
				$(el).closest("li").remove(); //删除页面显示的元素
				
//				moveSubOffer2DelList(ecMebType, offerData); //将删除的资料数据放到删除列表中展示
			}
			
			deleteSelGroupOfferData(offerId, ecMebType);
			
//			hideServiceOfferPart();
			
		});
	}
	else
	{
		$(el).closest("li").remove(); //删除页面显示的元素
	}
}

//将删除的子商品从已选择的组商品数据结构中删除
function deleteSelGroupOfferData(offerId, ecMebType)
{
	var delGroupId = "";
	var groupOfferData = new Wade.DataMap($("#"+ecMebType+"_SelGroupOffer").text());
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
		$("#"+ecMebType+"_SelGroupOffer").text(groupOfferData.toString())
	}
}

//保存子群组商品数据
function saveChildOfferData(el)
{
	var hasEcIns = $("#HAS_EC_INS_OFFER").val();
	if(!checkBeforeSaveData(hasEcIns))
	{
		return false;
	}
	
	var childOfferData = new Wade.DataMap();
	
	if("true" != hasEcIns){
		childOfferData.put("EC_COMMON_INFO", saveEcChildOfferCommonInfo());
		childOfferData.put("OPER_CODE", "0");
	}else{
		childOfferData.put("OPER_CODE", "2");
	}

	var ecChildOfferId = $("#CHILD_EC_OFFER_ID").val();
	

	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	if(!ecOfferData.get("SERIAL_NUMBER"))
	{
		ecOfferData.put("SERIAL_NUMBER", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
	}
	
	//定制
	ecOfferData.put("GRP_PACKAGE_INFO", new Wade.DatasetList($("#DZ_MEB_OFFER_DATA").text()));
	
	childOfferData.put("EC_OFFER", ecOfferData);
	
	var mebOfferData = new Wade.DataMap($("#MEB_OFFER_DATA").text());
	if(mebOfferData.length > 0)
	{
		if(!mebOfferData.get("EC_SERIAL_NUMBER"))
		{
			mebOfferData.put("EC_SERIAL_NUMBER", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
		}
		childOfferData.put("MEB_OFFER", mebOfferData);
		childOfferData.put("MEB_COMMON_INFO", saveMebChildOfferCommonInfo());
		
		//获取成员列表
		var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
		if(mebList && mebList.length > 0)
		{
			childOfferData.put("MEB_LIST", mebList);
			childOfferData.put("MEB_FILE", new Wade.DataMap($("#MEMBER_FILE_INFO").text()));
		}
		
	}
	
	var delMebList = new Wade.DatasetList($("#SUCCESS_DELMBR_LIST").text());
	var operType = $("#oattr_OPER_TYPE").val();
	if (delMebList.length <= 0 && "DelMeb" == operType) {
		MessageBox.alert("提示信息", "您没有进行删除成员操作，不能确定！");
		return false;
	}
	
	$("#CHILD_OFFER_DATA_"+ecChildOfferId).text(childOfferData.toString());
	$("#CHILD_OFFER_DATA_"+ecChildOfferId).closest("li").find("div.side").html("<span class='e_tag e_tag-green'>已设置</span>");
	$("#CHILD_DELMEB_DATA_"+ecChildOfferId).text(delMebList.toString());
	var childOfferSelGroupOfferData = new Wade.DataMap();
	var ecSelGroupOfferData = new Wade.DataMap($("#EC_SelGroupOffer").text());
	if(ecSelGroupOfferData && ecSelGroupOfferData.length > 0)
	{
		childOfferSelGroupOfferData.put("EC_SelGroupOffer", ecSelGroupOfferData);
	}
	
	var mebSelGroupOfferData = new Wade.DataMap($("#MEB_SelGroupOffer").text());
	if(mebSelGroupOfferData && mebSelGroupOfferData.length > 0)
	{
		childOfferSelGroupOfferData.put("MEB_SelGroupOffer", mebSelGroupOfferData);
	}
	$("#CHILD_OFFER_SelGroupOffer_"+ecChildOfferId).text(childOfferSelGroupOfferData.toString());
	
	if($.isFunction(window["childOfferAfterAction"]))
	{
		window["childOfferAfterAction"](childOfferData);
	}
	hidePopup(el);
}

function saveChildOfferEspgData(el) {
	var hasEcIns = $("#HAS_INS_ESPG_OFFER").val();
	
    //1、校验商品特征是否设置完成
	if(!checkOfferChaIsSet())
	{
		return false;
	}

    var childOfferData = new Wade.DataMap();
    var ecChildOfferId = $("#CHILD_EC_OFFER_ID").val();
    var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	
    childOfferData.put("EC_OFFER", ecOfferData);
    
    //保存esp订购人员
    var orderStaffId = $("#attr_ORDERSTAFFID").val();
    var orderStaffPhone = $("#attr_ORDERPHONE").val();
    var orderStaffInfo = new Wade.DataMap();
    if(orderStaffId && orderStaffId.length > 0){
    	orderStaffInfo.put("ORDER_STAFF_ID",orderStaffId);
    }
    if(orderStaffPhone && orderStaffPhone.length > 0){
    	orderStaffInfo.put("ORDER_STAFF_PHONE",orderStaffPhone);
    }
    childOfferData.put("ORDER_SATFF_INFO", orderStaffInfo);
    
    //开通和变更
    if("true" != hasEcIns){
    	//保存公共信息
        var ecCommonInfo = new Wade.DataMap();
    	//保存资源信息
    	var res = new Wade.DataMap();
    	res.put("SERIAL_NUMBER", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
    	res.put("RES_CODE", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
    	res.put("RES_TYPE_CODE", $("#CHILD_EC_RES_TYPE_CODE").val());
    	res.put("IF_RES_CODE", $("#CHILD_EC_IF_RES_CODE").val());
    	var resList = new Wade.DatasetList();
    	resList.add(res);
    	ecCommonInfo.put("RES_INFO", resList);
    	childOfferData.put("EC_COMMON_INFO", ecCommonInfo);
    	//开通为0
		childOfferData.put("OPER_CODE", "0");
		
		//获取成员列表
	    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
	    if (mebList && mebList.length > 0) {
	        childOfferData.put("MEB_LIST", mebList);
	        childOfferData.put("MEB_FILE", new Wade.DataMap($("#MEMBER_FILE_INFO").text()));
	    }
	}else{
		var operType = $("#oattr_OPER_TYPE").val();
		//变更为2
		childOfferData.put("OPER_CODE", "2");
		if (operType == "AddMeb"){
			//获取成员列表
		    var mebList = new Wade.DatasetList($("#SUCCESS_MEMBER_LIST").text());
		    if (mebList && mebList.length > 0) {
		        childOfferData.put("MEB_LIST", mebList);
		        childOfferData.put("MEB_FILE", new Wade.DataMap($("#MEMBER_FILE_INFO").text()));
		    }
		} else if (operType == "DelMeb") {
			var delMebList = new Wade.DatasetList($("#SUCCESS_DELMBR_LIST").text());
//			if (delMebList.length > 0){
//				//追加删除数据
//				if ($("#CHILD_DELMEB_DATA_"+ecChildOfferId).text() != "" && $("#CHILD_DELMEB_DATA_"+ecChildOfferId).text() != "[]"){
//					var oldMebList = new Wade.DatasetList($("#CHILD_DELMEB_DATA_"+ecChildOfferId).text());
//					//遍历多列数据集中的数据列
//					delMebList.each(function(item,index,totalcount){
//						oldMebList.add(item);
//					});
//					$("#CHILD_DELMEB_DATA_"+ecChildOfferId).text(oldMebList.toString());
//				}else{
//					$("#CHILD_DELMEB_DATA_"+ecChildOfferId).text(delMebList.toString());
//				}
//			}
			if (delMebList.length <= 0 && "DelMeb" == operType) {
				MessageBox.alert("提示信息", "您没有进行删除成员操作，不能确定！");
				return false;
			}
			$("#CHILD_DELMEB_DATA_"+ecChildOfferId).text(delMebList.toString());
		}
		
	}
	//childOfferData.put("OPER_CODE", "0");
	
	if(!ecOfferData.get("SERIAL_NUMBER"))
	{
		ecOfferData.put("SERIAL_NUMBER", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
	}
	
    var mebOfferData = new Wade.DataMap($("#MEB_OFFER_DATA").text());
    
    $("#CHILD_OFFER_DATA_" + ecChildOfferId).text(childOfferData.toString());
    $("#CHILD_OFFER_DATA_" + ecChildOfferId).closest("li").find("div.side").html("<span class='e_tag e_tag-green'>已设置</span>");

    hidePopup(el);
}

//保存bboss集团产品数据
function saveBbossProductOfferData(el)
{
	if(!checkBeforeSaveData4Bboss())
	{
		return false;
	}
	var ecChildOfferId = $("#CHILD_EC_OFFER_ID").val();
	
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	
	$("#CHILD_BBOSS_EC_PRODUCT_OFFER_DATA_"+ecChildOfferId).text(ecOfferData.toString());
	$("#CHILD_BBOSS_EC_PRODUCT_OFFER_DATA_"+ecChildOfferId).closest("li").find("div.side").html("<span class='e_tag e_tag-green'>已设置</span>");

	var childOfferSelGroupOfferData = new Wade.DataMap();
	var ecSelGroupOfferData = new Wade.DataMap($("#EC_SelGroupOffer").text());
	if(ecSelGroupOfferData && ecSelGroupOfferData.length > 0)
	{
		childOfferSelGroupOfferData.put("EC_SelGroupOffer", ecSelGroupOfferData);
	}
	$("#CHILD_BBOSS_EC_PRODUCT_OFFER_SelGroupOffer_"+ecChildOfferId).text(childOfferSelGroupOfferData.toString());
	
	hidePopup(el);
}

//保存bboss成员产品信息
function saveBbossMebProductOfferData(el)
{
	if(!checkBeforeSaveData4BbossMeb())
	{
		return false;
	}
	var mebChildOfferId = $("#CHILD_MEB_OFFER_ID").val();
	
	var mebOfferData = new Wade.DataMap($("#MEB_OFFER_DATA").text());
	var ecOfferId = $("#CHILD_OFFER_BBOSS_MEB_PRODUCT_LIST_UL input:hidden").first().attr("ec_product_offer_id");
    mebOfferData.put("EC_OFFER_ID", ecOfferId);
	
	$("#CHILD_BBOSS_MEB_PRODUCT_OFFER_DATA_"+mebChildOfferId).text(mebOfferData.toString());
	$("#CHILD_BBOSS_MEB_PRODUCT_OFFER_DATA_"+mebChildOfferId).closest("li").find("div.side").html("<span class='e_tag e_tag-green'>已设置</span>");

	var childOfferSelGroupOfferData = new Wade.DataMap();
	var mebSelGroupOfferData = new Wade.DataMap($("#MEB_SelGroupOffer").text());
	if(mebSelGroupOfferData && mebSelGroupOfferData.length > 0)
	{
		childOfferSelGroupOfferData.put("MEB_SelGroupOffer", mebSelGroupOfferData);
	}
	$("#CHILD_BBOSS_MEB_PRODUCT_OFFER_SelGroupOffer_"+mebChildOfferId).text(childOfferSelGroupOfferData.toString());
	
	hidePopup(el);
}

//保存子群组集团商品公共信息
function saveEcChildOfferCommonInfo()
{
	var ecCommonInfo = new Wade.DataMap();
	
	//保存资源信息
	var res = new Wade.DataMap();
	res.put("SERIAL_NUMBER", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
	res.put("RES_CODE", $("#CHILD_EC_REAL_SERIAL_NUMBER").val());
	res.put("RES_TYPE_CODE", $("#CHILD_EC_RES_TYPE_CODE").val());
	res.put("IF_RES_CODE", $("#CHILD_EC_IF_RES_CODE").val());
	var resList = new Wade.DatasetList();
	resList.add(res);
	ecCommonInfo.put("RES_INFO", resList);
	
	//保存账户信息
	var acctInfo = new Wade.DataMap($("#CHILD_EC_ACCOUNT_DATA").text());
	acctInfo.put("ACCT_DEAL", $("#cond_ACCT_DEAL").val());
	ecCommonInfo.put("ACCT_INFO", acctInfo);
	
	//保存付费类型
	ecCommonInfo.put("PAY_PLAN_INFO", $("#payPlanValue").val());
	
	return ecCommonInfo;
}

//保存子群组成员商品公共信息
function saveMebChildOfferCommonInfo()
{  
	var mebCommonInfo = new Wade.DataMap();
	
	mebCommonInfo.put("ROLE_CODE_B", $("#CHILD_MEB_REL_SUBSCRIBER_ROLE").val());  // 成员角色
	var  planTypeCode = $("#CHILD_MEB_PAY_PLAN").val();
	if(""==planTypeCode||null==planTypeCode||undefined ==planTypeCode||"null"==planTypeCode){
		planTypeCode = $("#CHILD_MEB_PAY_PLAN").text();
		if("集团付"==planTypeCode||"集团付费"==planTypeCode){
			planTypeCode = "G";
		}else if("个人付费"==planTypeCode){
			planTypeCode = "P";
		}
		mebCommonInfo.put("PLAN_TYPE_CODE", planTypeCode);     // 付费方式
	}else{
		mebCommonInfo.put("PLAN_TYPE_CODE", $("#CHILD_MEB_PAY_PLAN").val());     // 付费方式
	}

	mebCommonInfo.put("EFFECT_NOW", $("#CHILD_MEB_EFFECT_NOW").val());   // 生效方式  ：  0-下月；1-立即

	return mebCommonInfo;
}

//保存数据前校验
function checkBeforeSaveData(hasEcIns)
{
	if("true" != hasEcIns){
		//1、校验集团服务号码
		if($("#CHILD_EC_SERIAL_NUMBER_SUCCESS").val() == "false")
		{
			MessageBox.alert("提示信息", "请先验证服务号码！");
			return false;
		}
		
		//2、校验账户
		if(!$("#CHILD_EC_ACCOUNT_DATA").text())
		{
			MessageBox.alert("提示信息", "请先录入账户信息！");
			return false;
		}
		
		//3、校验付费类型
		if(!$("#payPlanValue").val())
		{
			MessageBox.alert("提示信息", "请先选择付费类型！");
			return false;
		}
		
		//3、校验商品组内选择的商品
		if(!checkSelOfferNum("EC"))
		{
			return false;
		}
	}

	//2、校验商品特征是否设置完成
	if(!checkOfferChaIsSet())
	{
		return false;
	}

	//  宽带没有成员信息，不需要校验
    if($("#childMebOfferBasePart").length > 0)
    {
        if(!$.validate.verifyField("CHILD_MEB_PAY_PLAN"))
        {
            return false;
        }
    }

	if(!checkSelOfferNum("MEB"))
	{
		return false;
	}
	return true;
}

//保存数据前校验
function checkBeforeSaveData4Bboss()
{
	//1、校验商品特征是否设置完成
	if(!checkOfferChaIsSet())
	{
		return false;
	}
	
	//2、校验商品组内选择的商品
	if(!checkSelOfferNum("EC"))
	{
		return false;
	}
	return true;
}

function checkBeforeSaveData4BbossMeb()
{
	//1、校验商品特征是否设置完成
	if(!checkOfferChaIsSet())
	{
		return false;
	}
	
	//2、校验商品组内选择的商品
	if(!checkSelOfferNum("MEB"))
	{
		return false;
	}
	return true;
}

//校验商品特征是否设置完成
function checkOfferChaIsSet()
{
	//1、校验集团主商品
	if($("#childEcOfferBasePart").find("span[class*=e_tag-red]").length > 0)
	{
		var offerName = $("#childEcOfferBasePart").find("div.main").text();
		MessageBox.alert("提示信息", "请先设置集团商品【"+offerName+"】的属性！");
		return false;
	}
	
	//2、校验集团子商品
	var ecSubOfferName = "";
	$("#childEcOfferSubList").find("span[class*=e_tag-red]").each(function(){
		ecSubOfferName = ecSubOfferName + "【" + $(this).closest("li").find("div.title").text() + "】";
	});
	if(ecSubOfferName != "")
	{
		MessageBox.alert("提示信息", "请先设置集团商品子商品"+ecSubOfferName+"的属性！");
		return false;
	}
	
	//3、校验成员商品
	if($("#childMebOfferBasePart").find("span[class*=e_tag-red]").length > 0)
	{
		var offerName = $("#childMebOfferBasePart").find("div.main").text();
		MessageBox.alert("提示信息", "请先设置成员商品【"+offerName+"】的属性！");
		return false;
	}
	
	//4、校验成员子商品
	var mebSubOfferName = "";
	$("#childMebOfferSubPart").find("span[class*=e_tag-red]").each(function(){
		mebSubOfferName = mebSubOfferName + "【" + $(this).closest("li").find("div.title").text() + "】";
	});
	if(mebSubOfferName != "")
	{
		MessageBox.alert("提示信息", "请先设置成员商品子商品"+mebSubOfferName+"的属性！");
		return false;
	}
	
	//校验ESP订购人员
	var orderStaffPhone =$("#attr_ORDERPHONE").val(); 
	var orderStaffPID =$("#attr_ORDERSTAFFID").val(); 
	if(orderStaffPhone =="" || orderStaffPID == ""){
		$.validate.alerter.one($("#attr_ORDERSTAFF")[0], "您未选择ESP订购员工，请选择!");
		return false;
	}
	return true;
}

//校验选择的组内商品是否满足商品组最大最小限制
function checkSelOfferNum(ecMebType)
{
	var text = "集团子商品";
	var offerId = $("#CHILD_"+ecMebType+"_OFFER_ID").val();
	if("MEB" == ecMebType)
	{
		text = "成员子商品";
	}
	var flag = true;
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA").text());
	var brand = ecOfferData.get("BRAND_CODE");
//	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
//	var curMrchpOperType = curOfferData.get("MERCHP_OPER_TYPE");
//	if ("BOSG" == brand) 
//	{
//		if ("10" == curMrchpOperType||"2" == curMrchpOperType||"9" == curMrchpOperType)//预受理不校验，集团退订不校验，集团变更属性不校验
//		{
//			return true;
//		}
//	}

	var checkGroupDataset = new Wade.DatasetList();
	var groupOfferData = new Wade.DataMap($("#"+ecMebType+"_SelGroupOffer").text());
	if(!groupOfferData)
	{
		return true;
	}
	
	groupOfferData.each(function(item,index,totalcount){
		checkGroupDataset.add(item);
	});
	
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
		for(var a = 0, sizea = selOffers.length; a < sizea; a++)
		{
			if(limitType && limitType == selOffers.get(a).get("OFFER_TYPE"))
			{
				selCheckNum++;
			}else if(!limitType){
				selCheckNum++;
			}
		}
		
		if(selectFlag == 0 && selNum == 0)
		{
			MessageBox.alert("提示信息", text+"商品组["+groupName+"]是必选的，请至少选择一个组内元素！");
			flag = false;
			return false;
		}
		
		if(selCheckNum < parseInt(minNum) && parseInt(minNum) > 0)
		{//包内最大订购数小于0的不做校验
			MessageBox.alert("提示信息", "您已选择"+text+"["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！");
			flag = false;
			return false;
		}
		
		if(selCheckNum > 0 && selCheckNum > parseInt(maxNum) && parseInt(maxNum) > 0)
		{
			MessageBox.alert("提示信息", "您已选择"+text+"["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！");
			flag = false;
			return false;
		}
		
		if(mustSelOffer)
		{
			var mustSelOfferArr = mustSelOffer.split("@");
			for(var i = 0, sizeI = mustSelOfferArr.length; i < sizeI; i++)
			{
				var selFlag = false; //组内必选商品是否被选中
				for(var j = 0, sizeJ = selOffers.length; j < sizeJ; j++)
				{
					if(mustSelOfferArr[i] == selOffers.get(j).get("OFFER_ID"))
					{
						selFlag = true;
						break;
					}
				}
				if(!selFlag)
				{
					MessageBox.alert("提示信息", "您已选择"+text+"["+groupName+"]组内商品"+mustSelOfferArr[i]+"是必选商品，选择了该组的商品必须同时订购"+mustSelOfferArr[i]+"商品！");
					flag = false;
					return false;
				}
			}
		}

	}
	return flag;
}

//获取订购的子商品商品组id和商品id（格式：GROUP_ID#OFFER_ID@GROUP_ID#OFFER_ID@...）
function getSubOfferGroupIdOfferId(subOfferList)
{
	var groupIdOfferId = "";
	for(var i = 0, size = subOfferList.length; i < size; i++)
	{
		var subOffer = subOfferList.get(i);
		if(subOffer.get("GROUP_ID"))
		{//只记录有商品组的数据
			groupIdOfferId = groupIdOfferId + "@" + subOffer.get("GROUP_ID") + "#" + subOffer.get("OFFER_ID");
		}
	}
	if(groupIdOfferId.length > 1)
	{
		groupIdOfferId = groupIdOfferId.substring(1);
	}
	return groupIdOfferId;
}

//获取指定商品数据结构（type=EC/MEB）
function getOfferData(offerId, type)
{
	if(type != "EC" && type != "MEB")
	{
		MessageBox.alert("提示信息", "传入获取商品数据类型有误，类型只能是EC或者MEB！");
		return null;
	}
	
	//EC_OFFER_DATA存放集团商品数据，MEB_OFFER_DATA存放成员商品数据
	var offerData = new Wade.DataMap($("#"+type+"_OFFER_DATA").text());
	if(offerId == offerData.get("OFFER_ID"))
	{
		return offerData;
	}
	else
	{
		var subOfferList = offerData.get("SUBOFFERS");
		if(!subOfferList)
		{
			return null;
		}
		for(var i = 0, sizeI = subOfferList.length; i < sizeI; i++)
		{
			if(offerId == subOfferList.get(i).get("OFFER_ID"))
			{
				return subOfferList.get(i);
			}
			else
			{
				var subOfferList2 = subOfferList.get(i).get("SUBOFFERS");
				if(!subOfferList2)
				{
					continue;
				}
				else
				{
					for(var j = 0, sizeJ = subOfferList2.length; j < sizeJ; j++)
					{
						if(offerId == subOfferList2.get(j).get("OFFER_ID"))
						{
							return subOfferList2.get(j);
						}
					}
				}
			}
		}
		return null;
	}
}

//更新指定商品数据结构（type=EC/MEB）当传入更新对象updOfferData为空，则做数据删除
function updateOfferData(offerId, type, updOfferData)
{
//	if(!updOfferData || updOfferData.length == 0)
//	{
//		return ;
//	}
	if(type != "EC" && type != "MEB")
	{
		MessageBox.alert("提示信息", "传入获取商品数据类型有误，类型只能是EC或者MEB！");
		return ;
	}
	//EC_OFFER_DATA存放集团商品数据，MEB_OFFER_DATA存放成员商品数据
	var offerData = new Wade.DataMap($("#"+type+"_OFFER_DATA").text());
	if(offerId == offerData.get("OFFER_ID"))
	{
		$("#"+type+"_OFFER_DATA").text(updOfferData.toString());
		return ;
	}
	else
	{
		var subOfferList = offerData.get("SUBOFFERS");
		if(!subOfferList)
		{
			return ;
		}
		for(var i = 0, sizeI = subOfferList.length; i < sizeI; i++)
		{
			if(offerId == subOfferList.get(i).get("OFFER_ID"))
			{
				subOfferList.removeAt(i);
				if(updOfferData)
				{
					subOfferList.add(updOfferData);
				}
				$("#"+type+"_OFFER_DATA").text(offerData.toString());
				return ;
			}
			else
			{
				var subOfferList2 = subOfferList.get(i).get("SUBOFFERS");
				if(!subOfferList2)
				{
					continue;
				}
				else
				{
					for(var j = 0, sizeJ = subOfferList2.length; j < sizeJ; j++)
					{
						if(offerId == subOfferList2.get(j).get("OFFER_ID"))
						{
							subOfferList2.removeAt(i);
							if(updOfferData)
							{
								subOfferList2.add(updOfferData);
							}
							$("#"+type+"_OFFER_DATA").text(offerData.toString());
							return ;
						}
					}
				}
			}
		}
	}
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


function acctDealChange(el)
{
	var acctDeal = el.value;
	if(acctDeal == "1")
	{
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}
	else
	{
		$("#i_acctCombPart").css("display", "none");
		$("#i_acctSelPart").css("display", "");
	}
}

function showOrderStaff(){
	debugger;
	var param = "&ACTION=qryOrderStaffinfo";
    ajaxSubmit("",'',param,'orderParts',function(data){
        showPopup("popup06", "orderPopupItem", true);
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}

function setReturnValueOrder(el){
	debugger;
	var staffId = $(el).attr("staff_id");
    var staffPhone =  $(el).attr("staff_phone");
	$("#attr_ORDERSTAFF").val(staffId);
	$("#attr_ORDERSTAFFID").val(staffId);
	$("#attr_ORDERPHONE").val(staffPhone);
	backPopup("popup06", "orderPopupItem", true);
}

function orderFormQuery(){
	debugger;
	$.beginPageLoading("数据查询中...");
	var param = "&ACTION=qryOrderStaffinfo";
	ajaxSubmit("orderForm",'',param,'orderParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

