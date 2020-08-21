
//动力100子销售品订购初始化
function initOutOffer(){
	var initOfferId = $("#INIT_OFFER_ID").val();
	if(typeof(initOfferId) == "undefined" || "" == initOfferId){
		return;
	}
	var initOfferName = $("#INIT_OFFER_NAME").val();
	var initOfferCode = $("#INIT_OFFER_CODE").val();
	chooseOffer(initOfferId, initOfferCode, "P", initOfferName, "", "CrtUs");
	$("#IS_HAVE_INIT_OFFER").val("true");
	
}

function addChildOfferIns(obj){
	//获取当前产品区域的offerId
	var curOfferId = $("#cond_OFFER_ID").val();
	var subscriberInsId = obj.getAttribute("subscriberInsId");
	var offerInstId = obj.getAttribute("offerInstId");
	var offerName = obj.getAttribute("offerName");
	var offerCode = obj.getAttribute("offerCode");
	var offerId = $("#OPER_CHILD_OFFER").val();
	var selectFlag = $("#divmain_"+offerId).attr("SELECT_FLAG");
	var oldInsId = $("#ps_"+offerId+"_SUB_INS_ID").val();
	var accessNum =obj.getAttribute("accessNum");
	//一、已选择实例，不用处理
	if(subscriberInsId == oldInsId){
		return;
	}		
	//二、删掉原来选择的实例
	if(typeof(oldInsId)!="undefined" && null != oldInsId && "" !=oldInsId){
		$("#div_ins_"+offerId).remove();
		
		var offerData = PageData.getData($("#class_"+curOfferId));
		var childSubs = offerData.get("POWER100_PRODUCT_INFO");
		for(var i = 0; i < childSubs.length; i++)
		{
			var childSub = childSubs.get(i);
			if(childSub.get("OFFER_ID") == offerId)
			{
				childSubs.removeAt(i);
				i--;
			}
		}
		if(childSubs.length == 0)
		{
			offerData.remove(childSubs);
		}
		PageData.setData($("#class_"+curOfferId), offerData);
		
	}

	//三、加入新选择的实例
	var offerInstHtml = "<div class='sub' id='div_ins_" + offerId + "' ><div class='main' id='div_ins_" + offerId + "'>" 
	+ "<div class='title'></div>" 
	+ "<div class='content'>" + "【" + accessNum + "】" + offerName + "</div>"
	+ "<input type='hidden' id='ps_"+offerId+"_SUB_INS_ID' value='" + subscriberInsId + "'/></div></div>";

	$("#div_"+offerId).append(offerInstHtml);
	//手动刷新scroller组件
	editMainScroll.refresh();

	var offerData = PageData.getData($("#class_"+curOfferId));
	var optOfferDataset = offerData.get("POWER100_PRODUCT_INFO"); //可选子销售品(取已存在的数据)
	if(typeof(optOfferDataset) == "undefined")
	{
		optOfferDataset = new Wade.DatasetList();
	}
	
	var optOfferData = new Wade.DataMap();
	optOfferData.put("OPER_CODE", "0");
	optOfferData.put("OFFER_ID", offerId);
 	optOfferData.put("OFFER_CODE", offerCode);
	optOfferData.put("OFFER_INS_ID", offerInstId);
	optOfferData.put("OFFER_NAME", offerName);
	optOfferData.put("USER_ID", subscriberInsId);
	optOfferData.put("SELECT_FLAG", selectFlag);
	
	optOfferDataset.add(optOfferData);
	offerData.put("POWER100_PRODUCT_INFO", optOfferDataset);
	PageData.setData($("#class_"+curOfferId), offerData);
}

function operNewOffer(obj){
	var offerId = obj.getAttribute("OFFER_ID");
	var offerCode = obj.getAttribute("OFFER_CODE");
	var offerName = obj.getAttribute("OFFER_NAME");

	openNav(offerName+'业务受理', 'igroup.operenterprisesubscriber','initial','&OUT_OFFER_ID='+offerId+'&OUT_OFFER_CODE='+offerCode+'&OUT_OFFER_NAME='+encodeURIComponent(offerName));
}

//删除子销售品实例信息(入参：子销售品实例编码，主销售品编码)
function deleteOptionalChildOffer(obj)
{
	if($("#prodDiv_SWITCH").val() == "on")
	{
		MessageBox.alert("提示信息", "请点击【确定】或【取消】按钮确认商品设置！");
		return false;
	}
	if(!window.confirm("是否删除当前子商品？")){
		return false;
	}
	
    var offerId=obj.getAttribute("OFFER_ID");
	$("#"+offerId).attr("checked", false);
	$("#"+offerId).attr("disabled", false);
	
	//手动刷新scroller组件
	editMainScroll.refresh();

	//删除数据结构
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	var childSubs = offerData.get("POWER100_PRODUCT_INFO");

	if(childSubs){
		for(var i = 0, size = childSubs.length; i < size; i++)
		{
			var sub = childSubs.get(i);
			if(sub.get("OFFER_ID") == offerId)
			{
				childSubs.removeAt(i);//新增的子商品直接删掉就可以了
				break;
			}
		}
		if(childSubs.length == 0)
		{
			offerData.remove(childSubs);
		}
	}
	$("#div_"+offerId).remove();

	PageData.setData($(".e_SelectOfferPart"),offerData); 
}

//check已订购子商品信息
function checkOptionalChildOffer(obj)
{
    var offerId=obj.getAttribute("OFFER_ID");
	if($("#prodDiv_SWITCH").val() == "on")
	{
		$("#checkDl100_"+offerId).attr("checked" , !obj.checked);
		MessageBox.alert("提示信息", "请点击【确定】或【取消】按钮确认商品设置！");
		return false;
	}
	if(!obj.checked){
		if(!window.confirm("是否删除当前子商品？")){
			return false;
		}
	}

	//手动刷新scroller组件
	editMainScroll.refresh();

	//删除数据结构
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	var childSubs = offerData.get("POWER100_PRODUCT_INFO");
	for(var i = 0, size = childSubs.length; i < size; i++)
	{
		var sub = childSubs.get(i);
		if(sub.get("OFFER_ID") == offerId)
		{
			if(obj.checked){
				sub.put("OPER_CODE","EXISTS");
				$("#"+offerId).attr("checked", true);
				
			}else{
				sub.put("OPER_CODE","1");
				$("#"+offerId).attr("checked", false);
				
			}

			break;
		}
	}

	PageData.setData($(".e_SelectOfferPart"),offerData); 
}

function refreshDLChildOffer(obj){
	var offerId = obj.getAttribute("OFFER_ID");
    var offer_code = obj.getAttribute("OFFER_CODE");
	var offerName = obj.getAttribute("OFFER_NAME");

	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");
	//此处根据cust_id和offer_id查询一下集团是否已订购
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryOfferInstInfos", "OFFER_ID="+offerId+"&OFFER_CODE="+offer_code+"&CUST_ID="+custId, "", function(data){
		var insOffers = data.get("INS_OFFERS");
		if(insOffers){
			for(var i = 0, size = insOffers.length; i < size; i++)
			{
				var accessNum =insOffers.get(i).get("SERIAL_NUMBER");
				var subscriberInsId =insOffers.get(i).get("USER_ID");
				//三、加入新选择的实例
				var offerInstHtml =  "<div class='sub' id='div_ins_"+offerId+"'><div class='main'>" 
				+ "<div class='title'></div>" 
				+ "<div class='content'>" + "【" + accessNum + "】" + offerName + "</div>"
				+ "<input type='hidden' id='ps_"+offerId+"_SUB_INS_ID' value='" + subscriberInsId + "'/></div></div>";
				$("#div_"+offerId).append(offerInstHtml);
	
				var offerData = PageData.getData($(".e_SelectOfferPart"));
				var optOfferDataset = offerData.get("POWER100_PRODUCT_INFO"); //可选子销售品(取已存在的数据)
				if(typeof(optOfferDataset) == "undefined")
				{
					optOfferDataset = new Wade.DatasetList();
				}
				var optOfferData = new Wade.DataMap();
				optOfferData.put("OPER_CODE", "0");
				optOfferData.put("OFFER_ID", offerId);
				optOfferData.put("OFFER_CODE", offer_code);
				optOfferData.put("OFFER_NAME", offerName);
				optOfferData.put("USER_ID", subscriberInsId);
				optOfferData.put("OFFER_INS_ID", insOffers.get(i).get("OFFER_INS_ID"));
				var selFlag = $("#"+offerId).attr("SELECT_FLAG");
				optOfferData.put("SELECT_FLAG", selFlag);
				
				optOfferDataset.add(optOfferData);
				offerData.put("POWER100_PRODUCT_INFO", optOfferDataset);
				PageData.setData($(".e_SelectOfferPart"),offerData); 
				$("#add_"+offerId).remove();
				$("#refresh_"+offerId).remove();
			}
		}
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function checkDL100(){
	var aoOfferIds = document.getElementsByName("child_OFFER_ID");
	if(typeof(aoOfferIds)=="undefined" || 0 == aoOfferIds.length){
		return true;
	}
	for(var j = 0; j < aoOfferIds.length; j++)
	{
		var offerId = aoOfferIds[j].value;
		var offerName = aoOfferIds[j].getAttribute("offerName");
		var subscriberInsId = $("#ps_"+offerId+"_SUB_INS_ID").val();
		if(typeof(subscriberInsId)=="undefined" || null == subscriberInsId || "" ==subscriberInsId)
		{
			MessageBox.alert("提示信息", "请选择"+offerName+"商品实例！");
			return false;
		}
	}
	//删除新增的子商品数据对象
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	var childSubs = offerData.get("SUBOFFERS");
	if(childSubs){
		for(var i = 0; i < childSubs.length; i++)
		{
			var childSub = childSubs.get(i);
			var offerType = childSub.get("OFFER_TYPE");//只移除产品，服务资费类子商品要保留（产品类子商品已经放到POWER100_PRODUCT_INFO中了）
			if("P" == offerType)
			{
				childSubs.removeAt(i--);
			}
		}
		if(childSubs.length == 0)
		{
			offerData.remove(childSubs);
		}
	}

	PageData.setData($(".e_SelectOfferPart"),offerData); 
	//删除新增的产品类子商品数据对象
	return true;
	
}

function selectDLChildOffer(obj){
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");
    var offerId = obj.getAttribute("OFFER_ID");
    var offer_code = obj.getAttribute("OFFER_CODE");
    var offerName = obj.getAttribute("OFFER_NAME");
	//此处根据cust_id和offer_id查询一下集团是否已订购
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryMainOfferInstancesByCustIdOfferId", "OFFER_ID="+offerId+"&OFFER_CODE="+offer_code+"&CUST_ID="+custId, "chooseChildOfferPart", function(data){
		$("#OPER_CHILD_OFFER").val(offerId);
		$("#OPER_CHILD_OFFER_NAME").val(offerName);
		var subInsId = $("#ps_"+offerId+"_SUB_INS_ID").val();
		var offerObj = $("input[name=CHILD_RADIO]");

		for(var i = 0; i < offerObj.length; i++)
		{
			var preSubInsId = offerObj[i].getAttribute("subscriberInsId");
			if(subInsId == preSubInsId)
			{
				$("#"+preSubInsId).attr("checked", true);
				break;
			}
		}
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

    showPopup('popup', 'chooseChildOffer');
}
