$(function(){
	$("#mytree").checkBoxAction(function(e, nodeData){
		checkTree(nodeData);
	});
	$("#mybbosstree").checkBoxAction(function(e, nodeData){
		checkBBossTree(nodeData);
	});
	
});

//js生成集团产品开户树
function loadEcUserPackageTree(ecOfferId,userId) {
	var usrSetOfferId = $("#USR_SET_OFFER_ID").val();
	var usrSetUserId = $("#USR_SET_USER_ID").val();
	if(ecOfferId != usrSetOfferId || userId != usrSetUserId){
		$("#USR_SET_OFFER_ID").val(ecOfferId);
		$("#USR_SET_USER_ID").val(userId);
		mytree.empty(true);
		mytree.setParam('EC_OFFER_ID',ecOfferId);
		mytree.setParam('EC_USER_ID',userId);
		mytree.init();
	}
	//将商品上的（待设置）标签隐藏
	$("#li_ec_package> div").eq(0).find(".side").css("display", "none");
}

//js生成集团BBOSS产品开户树
function loadBBossUserPackageTree(ecOfferId,userId) {
	var usrSetOfferId = $("#BBOSS_USR_SET_OFFER_ID").val();
	var usrSetUserId = $("#BBOSS_USR_SET_USER_ID").val();
	if(ecOfferId != usrSetOfferId || userId != usrSetUserId){
		$("#BBOSS_USR_SET_OFFER_ID").val(ecOfferId);
		$("#BBOSS_USR_SET_USER_ID").val(userId);
		mybbosstree.empty(false);
		mybbosstree.setParam('EC_OFFER_ID',ecOfferId);
		mybbosstree.setParam('EC_USER_ID',userId);
		mybbosstree.init();
	}
	//将商品上的（待设置）标签隐藏
	$("#li_pro_package> div").eq(0).find(".side").css("display", "none");
}

//清空集团产品树
function cleanEcUserPackageTree(data) {
	mytree.empty(true);
}
//
function cleanBbossUserPackageTree(data) {
	mybbosstree.empty(true);
}

function checkBBossTree(nodeData){
	var userSetOfferId = $("#BBOSS_USR_SET_OFFER_ID").val();
	var id = nodeData.id;
	var groupid = nodeData.groupid;
	var dataid = nodeData.dataid;
	if("ELEMENT"!=groupid){
		return;
	}
	var dealType = "";
	if(nodeData.checked=="true"){
		//原来是checked的，本次去掉勾选
		dealType = "1";//删除
		
		$("#div_"+dataid).remove();
		if($("#optOffer_pro_package").children().length == 0)
		{
			$("#sub_pro_package").remove();
		}
		dealBBossEcUserPackagedata(dataid,dealType,"");
	}else{
		//原来没有checked，本次checked
		dealType = "0";//新增
		var existState = dealBBossEcUserPackagedata(dataid,dealType,nodeData.value);
		if("" == existState || "1" == existState){//如果不存在，或者原状态为删除，需要添加展示
			if($("#sub_pro_package").length == 0)
			{
				var subHtml = "<div class='sub' id='sub_pro_package'><div class='main' >" 
					+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>" 
					+ "<ul id='optOffer_pro_package'>";
					+ "</ul></div></div>";
				
				$("#li_pro_package").append(subHtml);	
			}
			
			var optionalOfferHtml = "<li id='div_"+dataid+"'><div class='group link'>" 
			+ "<div class='main'>" + "["+nodeData.id+"]"+nodeData.text + "</div>"
/*			+ "<input type='hidden' name='oo_ec_package_OPTOFFER_ID' value='" + id + "'/>" 
*/			+ "</div>";
		
			optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteBBossUserPackage(&#39;" + dataid +"&#39;);'></span></div>";
	
			optionalOfferHtml=optionalOfferHtml + "</li>";
			$("#optOffer_pro_package").append(optionalOfferHtml);
		}
	}
	$("#prodDiv_SWITCH").val("off");

	return true;
}

function checkTree(nodeData){
	var userSetOfferId = $("#USR_SET_OFFER_ID").val();
	var id = nodeData.id;
	var groupid = nodeData.groupid;
	var dataid = nodeData.dataid;
	if("ELEMENT"!=groupid){
		return;
	}
	var dealType = "";
	if(nodeData.checked=="true"){
		//原来是checked的，本次去掉勾选
		dealType = "1";//删除
		
		$("#div_"+dataid).remove();
		if($("#optOffer_ec_package").children().length == 0)
		{
			$("#sub_ec_package").remove();
		}
		dealEcUserPackagedata(dataid,dealType,"");
	}else{
		//原来没有checked，本次checked
		dealType = "0";//新增
		var existState = dealEcUserPackagedata(dataid,dealType,nodeData.value);
		if("" == existState || "1" == existState){//如果不存在，或者原状态为删除，需要添加展示
			if($("#sub_ec_package").length == 0)
			{
				var subHtml = "<div class='sub' id='sub_ec_package'><div class='main' >" 
					+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>" 
					+ "<ul id='optOffer_ec_package'>";
					+ "</ul></div></div>";
				
				$("#li_ec_package").append(subHtml);	
			}
			
			var optionalOfferHtml = "<li id='div_"+dataid+"'><div class='group link'>" 
			+ "<div class='main'>" + "["+nodeData.id+"]"+nodeData.text + "</div>"
/*			+ "<input type='hidden' name='oo_ec_package_OPTOFFER_ID' value='" + id + "'/>" 
*/			+ "</div>";
		
			optionalOfferHtml = optionalOfferHtml + "<div class='fn'><span class='e_ico-delete' ontap='deleteUserPackage(&#39;" + dataid +"&#39;);'></span></div>";
	
			optionalOfferHtml=optionalOfferHtml + "</li>";
			$("#optOffer_ec_package").append(optionalOfferHtml);
		}
	}
	
	return true;
}

//处理bboss定制信息
function dealBBossEcUserPackagedata(dataid,dealType,elementTypeCode){
	var element = dataid.split("●");
	//nodeData.checked取到的是当前操作前的值	
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	var userPackages = offerData.get("GRP_PACKAGE_INFO");
	var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var offerCode = curOfferData.get("OFFER_CODE");
	
	var userPackageList = userPackages.get(offerCode);
	
	if(!userPackageList){
		userPackageList = new Wade.DatasetList();
	}
	
	//var userPackages = PageData.getDataList($("#class_ec_package"));
	var ecOperType = $("#cond_OPER_TYPE").val();
	var existState = "";//获取已存在的元素当前状态
	if("0"==dealType && "CrtUs" == ecOperType){
		
		//当前元素已经第一次选择了就不能再添加
		for(var i=0;i<userPackageList.length;i++){
			var oldGrpPackage =userPackageList.get(i);
			var oldElementId=oldGrpPackage.get("ELEMENT_ID");
			if(element[3]==oldElementId){
				existState="3";
				return existState;
			}
		}
		//checked 并且 集团产品订购的情况，直接添加
		var newGrpPackage = new Wade.DataMap();
		newGrpPackage.put("PRODUCT_ID",element[1]);
		newGrpPackage.put("PACKAGE_ID",element[2]);
		newGrpPackage.put("ELEMENT_ID",element[3]);
		newGrpPackage.put("ELEMENT_TYPE_CODE",elementTypeCode);
		newGrpPackage.put("MODIFY_TAG","0");
		userPackageList.add(newGrpPackage);
		userPackages.put(offerCode+"",userPackageList);
		
	}else{
		var exist=false;
		for(var i = 0, sizei = userPackageList.length; i < sizei; i++)
		{
			var userPackage = userPackageList.get(i);
			if(userPackage.get("ELEMENT_ID") == element[3] 
				&& userPackage.get("PACKAGE_ID") == element[2])
			{
				existState = userPackage.get("MODIFY_TAG");
				
				if("1"==dealType && "CrtUs" == ecOperType){
					//nochecked+集团产品新增，直接移除
					userPackageList.remove(userPackage);
				}else if("1"==dealType && "ChgUs" == ecOperType){
					//nochecked+集团产品变更，更改状态为删除
					userPackage.put("MODIFY_TAG","1");//删除
				}else if("0"==dealType && "ChgUs" == ecOperType){
					//checked+集团产品变更，更改状态为不变
					userPackage.put("MODIFY_TAG","EXIST");//不变
				}
				exist=true;
				break;
			}
		}
		//不存在，则新增
		if(!exist && "0"==dealType){
			//集团产品变更的情况，新增定制
			var newGrpPackage = new Wade.DataMap();
			newGrpPackage.put("PRODUCT_ID",element[1]);
			newGrpPackage.put("PACKAGE_ID",element[2]);
			newGrpPackage.put("ELEMENT_ID",element[3]);
			newGrpPackage.put("ELEMENT_TYPE_CODE",elementTypeCode);
			newGrpPackage.put("MODIFY_TAG","0");
			userPackageList.add(newGrpPackage);
			userPackages.put(offerCode+"",userPackageList);
		}
	}

	//保存数据对象
	offerData.put("GRP_PACKAGE_INFO",userPackages);
	PageData.setData($(".e_SelectOfferPart"),offerData);
	return existState;
}
function dealEcUserPackagedata(dataid,dealType,elementTypeCode){
	var element = dataid.split("●");
	//nodeData.checked取到的是当前操作前的值	
	var offerData = PageData.getData($(".e_SelectOfferPart"));
	var userPackages = offerData.get("GRP_PACKAGE_INFO");
	if(!userPackages){
		userPackages = new Wade.DatasetList();
	}
	//var userPackages = PageData.getDataList($("#class_ec_package"));
	var ecOperType = $("#cond_OPER_TYPE").val();
	var existState = "";//获取已存在的元素当前状态
	
	
	
	if("0"==dealType && "CrtUs" == ecOperType){
		
		//当前元素已经第一次选择了就不能再添加
		for(var i=0;i<userPackages.length;i++){
			var oldGrpPackage =userPackages.get(i);
			var oldElementId=oldGrpPackage.get("ELEMENT_ID");
			if(element[3]==oldElementId){
				existState="3";
				return existState;
			}
		}
		//checked 并且 集团产品订购的情况，直接添加
		var newGrpPackage = new Wade.DataMap();
		newGrpPackage.put("PRODUCT_ID",element[1]);
		newGrpPackage.put("PACKAGE_ID",element[2]);
		newGrpPackage.put("ELEMENT_ID",element[3]);
		newGrpPackage.put("ELEMENT_TYPE_CODE",elementTypeCode);
		newGrpPackage.put("MODIFY_TAG","0");
		userPackages.add(newGrpPackage);
	}else{
		var exist=false;
		for(var i = 0, sizei = userPackages.length; i < sizei; i++)
		{
			var userPackage = userPackages.get(i);
			if(userPackage.get("ELEMENT_ID") == element[3] 
				&& userPackage.get("PACKAGE_ID") == element[2])
			{
				existState = userPackage.get("MODIFY_TAG");
				
				if("1"==dealType && "CrtUs" == ecOperType){
					//nochecked+集团产品新增，直接移除
					userPackages.remove(userPackage);
				}else if("1"==dealType && "ChgUs" == ecOperType){
					//nochecked+集团产品变更，更改状态为删除
					userPackage.put("MODIFY_TAG","1");//删除
				}else if("0"==dealType && "ChgUs" == ecOperType){
					//checked+集团产品变更，更改状态为不变
					userPackage.put("MODIFY_TAG","EXIST");//不变
				}
				exist=true;
				break;
			}
		}
		//不存在，则新增
		if(!exist && "0"==dealType){
			//集团产品变更的情况，新增定制
			var newGrpPackage = new Wade.DataMap();
			newGrpPackage.put("PRODUCT_ID",element[1]);
			newGrpPackage.put("PACKAGE_ID",element[2]);
			newGrpPackage.put("ELEMENT_ID",element[3]);
			newGrpPackage.put("ELEMENT_TYPE_CODE",elementTypeCode);
			newGrpPackage.put("MODIFY_TAG","0");
			userPackages.add(newGrpPackage);
		}
	}

	//保存数据对象
	offerData.put("GRP_PACKAGE_INFO",userPackages);
	PageData.setData($(".e_SelectOfferPart"),offerData);
	return existState;
}
function deleteUserPackage(dataid){

	$("#div_"+dataid).remove();
	
	if($("#optOffer_ec_package").children().length == 0)
	{
		$("#sub_ec_package").remove();
	}
	
	dealEcUserPackagedata(dataid,"1","");
	
	var nodedata = mytree._getNodeDataByDataId(dataid);
	//如果nodedata存在
	if(nodedata){
		nodedata.checked="";
		
		$("#mytree○"+dataid).find("input[name=mytree]").attr("checked", "");
	}
}

function deleteBBossUserPackage(dataid){

	$("#div_"+dataid).remove();
	
	if($("#optOffer_pro_package").children().length == 0)
	{
		$("#sub_pro_package").remove();
	}
	
	dealBBossEcUserPackagedata(dataid,"1","");
	
	var nodedata = mybbosstree._getNodeDataByDataId(dataid);
	//如果nodedata存在
	if(nodedata){
		nodedata.checked="";
		
		$("#mybbosstree○"+dataid).find("input[name=mybbosstree]").attr("checked", "");
	}
}
//取消
function cancelPckInfo(){
	
	hidePopup("bbossUserPckageTreePop","PckageHome");
}
//确认
function submitPkgInfo(){
	
	hidePopup("bbossUserPckageTreePop","PckageHome");
}


