var offerSimpleCha;

$(function(){
	offerSimpleCha = new OfferSimpleCha('offerSimpleChaPopupItem');

});

function changePackage(){
	var packageIdSel = $("#packageSelects").val();
	packageIdSel = packageIdSel.split(',');//0、PACKAGE_ID 1、MIN_NUM 2、MAX_NUM 3、FORCE_TAG

	var param = "PACKAGE_ID="+packageIdSel[0]+"&ACTION=getChildOffers";
	$.beginPageLoading("商品目录数据查询中...");
	$.ajax.submit("", "", param, "elementPart", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

function checkElement()
{
	var packageIdSel = $("#packageSelects").val();
	var offerId = $("#elementSelects").val();
	
	if(!offerId){//如果没有获取到元素，提示
		MessageBox.alert("提示","请选择您要添加的资费或服务");
		return false;
	}
	packageIdSel = packageIdSel.split(',');//0、PACKAGE_ID 1、MIN_NUM 2、MAX_NUM 3、FORCE_TAG
	
	var elements = document.getElementsByName("SELECT_OFFER_ID");
	if(typeof(elements)=="undefined" || 0 == elements.length){
		return true;
	}
	
	var num = 0;
	for(var j = 0; j < elements.length; j++)
	{
		var selectedOfferId = elements[j].value;
		var selectedElementId = elements[j].getAttribute("elementId");
		var selectedElementName = elements[j].getAttribute("elementName");
		var selectedPakcageId = elements[j].getAttribute("packageId"); 
		if(selectedOfferId==offerId){
			MessageBox.alert("提示","已存在["+selectedElementId+"_"+selectedElementName+"],请勿重复添加！");
			return false;
		}
		//如果包元素最大值大于0，并且
		if(packageIdSel[2] > 0 && $("#checkelement_"+selectedOfferId).attr("checked") && selectedPakcageId == packageIdSel[0]){
			num = num+1;
			if(num >= packageIdSel[2]){
				MessageBox.alert("提示","["+packageIdSel[0]+"]"+packageSelects.spanEl.innerText+"最多只能添加"+packageIdSel[2]+"个元素！");
				return false;
			}
		}
	}
	return true;
}

function addElement(){
	if(!checkElement()){
		return;
	}
	var packageIdSel = $("#packageSelects").val();
	var offerId = $("#elementSelects").val();
	var mainOfferId = $("#MAIN_OFFER_ID").val();
	packageIdSel = packageIdSel.split(',');//0、PACKAGE_ID 1、MIN_NUM 2、MAX_NUM 3、FORCE_TAG
	var param = "PACKAGE_ID="+packageIdSel[0]+"&OFFER_ID="+offerId+"&MAIN_OFFER_ID="+mainOfferId+"&ACTION=getChildOfferDetail";
	$.beginPageLoading("数据加载中...");
	$.ajax.submit("", "", param, "refreshPart", function(data){
		var elementId = data.get("OFFER_CODE");
		var elementName=data.get("OFFER_NAME");
		var startDate=data.get("START_DATE");
		var endDate=data.get("END_DATE");
		var extFlag = data.get("EXT_FLAG");

		var groupOfferHtml = "<li id='li_"+offerId+"'>"
		
		+ "<div class='fn'>"
		+ "<input type='checkbox' id='checkelement_"+offerId+"' name='checkelement_"+offerId+"' offer_id= '" + offerId + "'"
		+ "checked='true' ontap='clickElement(this);'/>"
		+ "</div>"
		+ "<div class='main'>"
		+ "<div class='title'>【" + elementId + "】"+elementName+ "</div>"
		+ "<input type='hidden' id='Attr_"+offerId+"' value='' />"
		+ "<div class='content'>"
		+ startDate+ "~"+endDate + "</div>"
		+ "<input type='hidden' name='SELECT_OFFER_ID' value='" + offerId + "' elementId='" + elementId + "' elementName='" + elementName +"' packageId='" + packageIdSel[0] + "'/>"
		+ "</div>";
		
		if(extFlag && extFlag == "true")
		{
			groupOfferHtml += "<div class='side' tag='0' OFFER_ID='" + offerId + "' USER_ID='' OFFER_INS_ID='' ontap='offerSimpleCha.initOfferSimpleCha(this);'><span class='e_tag e_tag-red'>待设置</span></div>"
			+"<div class='more'></div>";
		}
		groupOfferHtml += "<div id='OFFER_DATA_"+offerId+"' style='display:none'>"+data.toString()+"</div>";
		groupOfferHtml += "</li>";
		$("#selectedUL").append(groupOfferHtml);
		
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

function clickElement(obj){
	var offerId=obj.attr("offer_id");
	var offerData = new Wade.DataMap($("#OFFER_DATA_"+offerId).text());
	var operCode=offerData.get("OPER_CODE");
	//处理元素状态
	if(obj.checked){
		if("1"==operCode){
			
		}
	}else{
		
	}
}
function getSelectedElements(){
	var selectedElement = new Wade.DatasetList(); //资费类商品特征

	var elements = document.getElementsByName("SELECT_OFFER_ID");
	if(typeof(elements)=="undefined" || 0 == elements.length){
		return;
	}
	//变更，删除的情况后续处理
	var num = 0;
	for(var j = 0; j < elements.length; j++)
	{
		var offerId = elements[j].value;
		if($("#checkelement_"+offerId).attr("checked")){
			var offerData = new Wade.DataMap($("#OFFER_DATA_"+offerId).text());
			var offerAttr = $("#Attr_"+offerId).val();
			var offerSimpleChaList = new Wade.DatasetList(offerAttr); //资费类商品特征
			offerData.put("ATTR",offerSimpleChaList);
			selectedElement.add(offerData);
		}
	}
	alert(selectedElement);
	return selectedElement;
}

