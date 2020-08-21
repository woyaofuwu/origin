function queryGroupInfo(el, e)
{
	if (e.keyCode != 13 && e.keyCode != 108) 
	{
		return ;
	}
	
	if(!$.validate.verifyField("cond_GROUP_ID_INPUT"))
	{
		return ;
	}

	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryCustGroupByGroupId", "GROUP_ID="+$("#cond_GROUP_ID_INPUT").val(), "groupBasePart,moreCustPart", function(data){
		$.endPageLoading();
		el.blur();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeOperType()
{
	$("#cond_OFFER_CODE").val("");
	$("#cond_OFFER_NAME").val("");
	$("#OFFER_CODE_LI").css("display", "none");
	$("#ParamPart").css("display", "none");
}

function chooseOfferAfterAction(offerCode, offerName, userId)
{
	var param = "&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val();
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryOffer", param, "OfferPart,ParamPart", function(data){
		$.endPageLoading();
//		$("#cond_OFFER_CODE").val(offerCode);
//		$("#cond_OFFER_NAME").val(offerName);
		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function datalineOperTypeOnChange()
{
	var datalineOperType = $("#DATALINE_OPER_TYPE").val();
	if(datalineOperType == 0)
	{//新增-新增集团产品用户
		$("#DATALINE_SERIAL_NUMBER_LI").css("display", "none");
		$("#DATALINE_SERIAL_NUMBER").attr("nullable", "yes");
		$("#DATALINE_SERIAL_NUMBER").val("");
	}
	else
	{//已有-在已有集团产品用户下新增专线
		$("#DATALINE_SERIAL_NUMBER_LI").css("display", "");
		$("#DATALINE_SERIAL_NUMBER").attr("nullable", "no");
	}
}

/**************************商品特征-开始*****************************/
function initOfferCha(offerChaHiddenId)
{
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); //将属性要保存到对应隐藏域的id存到隐藏域中
	var subOfferCode = "";
	var userId = "";
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	
	if(showParamPart == "2")
	{
		var datalinOperType = $("#DATALINE_OPER_TYPE").val();
		if(datalinOperType == "1"){
			serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
		}
		var serialSEQ = $("#DATALINE_MAX_INDEX").val();
		var serialNO = $("#serialNO").val();
	}
	if(brandCode == "BOSG")
	{
		var offerDataHiddenId = "OFFER_DATA_" + offerChaHiddenId.substring(10);
		var offerData = new Wade.DataMap($("#"+offerDataHiddenId).text());
		userId = offerData.get("USER_ID");//产品USER_ID
		subOfferCode = offerData.get("OFFER_CODE");
		offerId = offerData.get("OFFER_ID");
		
	}
	
	var param = "&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		showPopup("popup02", "offerChaPopupItem", true);
		
		var grpItems = data.map.result;
		if ("BOSG" == brandCode && grpItems)
		{
			for (var i=0;i<grpItems.length;i++)
			{
				var prodChaSpecInfo = grpItems.get(i);
				var elementId = prodChaSpecInfo.get("CHA_SPEC_ID");
				var regx = /[0-9]+/m;
				var code = elementId.match(regx);
				if (code != null){
					elementId = code[code.length-1];
				}
				var elementValue = prodChaSpecInfo.get("CHA_VALUE");
				var grpItemId = "AREA_ID_" + prodChaSpecInfo.get("GROUP_ATTR");
				if (grpItemId)
				{
					dealGrpItemInit(grpItemId,elementId,elementValue);
				}
			}
		}
		
		var offerChaSpecStr = $("#"+offerChaHiddenId).val();
		if(offerChaSpecStr != "")
		{
			var offerChaSpecs = new Wade.DatasetList(offerChaSpecStr);
			for(var i = 0; i < offerChaSpecs.length; i++)
			{
				var offerChaSpec = offerChaSpecs.get(i);
				var attrCode = offerChaSpec.get("ATTR_CODE");
				var attrValue = offerChaSpec.get("ATTR_VALUE");
				var grpItemId = offerChaSpec.get("GROUP_ATTR");
				if (grpItemId)
				{
//					dealGrpItemInit(grpItemId,elementId,elementValue);
				}
				else
				{
					var inputType = $("#offerChaPopupItem input[name="+attrCode+"]").attr("type");
					if("BOSG" == brandCode)
					{
						inputType= $("#offerChaPopupItem input[element_id="+attrCode+"]").attr("type");
					}
					if(inputType == "checkbox" || inputType == "radio")
					{
						if(attrValue == "1")
						{
							$("#offerChaPopupItem input[name="+attrCode+"]").attr("checked", "true");
						}
						else
						{
							$("#offerChaPopupItem input[name="+attrCode+"]").removeAttr("checked");
						}
					}
					else
					{	
						if("BOSG" == brandCode)
						{
							$("#offerChaPopupItem input[element_id="+attrCode+"]").val(attrValue);
						}
						else
						{
							$("#offerChaPopupItem input[id="+attrCode+"]").val(attrValue);
						}
						
					}
					
					// 如果是simpleupload组件还需要初始化几个值
//					if (elementValue.length>0 && "simpleupload" == $("#offerChaPopupItem input[element_id=" + elementId + "]").attr("x-wade-uicomponent")) {
//						var uploadId = $("#offerChaPopupItem input[element_id=" + elementId + "]").attr("id");
//						$("#" + uploadId + "_name").val(elementValue);
//						$("#" + uploadId + "_btn_close").attr("style","display:");
//						$("#" + uploadId + "_btn_download").attr("style","display:");
//					}
				}
			}
			
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function submitOfferCha()
{debugger;
	var chaSpecObjs = $("#offerChaPopupItem input");
	var brand = $("#cond_BRAND_CODE").val();
	var offerChaSpecDataset = new Wade.DatasetList();

	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if("BOSG"==brand&&isBbossNull(chaSpecCode))
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
			chaSpecCode = chaSpecObjs[i].name;
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
		}
		
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("ATTR_CODE", chaSpecCode);
		
		if("BOSG"==brand)
		{
			var elementId = chaSpecObjs[i].getAttribute("element_id");
			if(!elementId)
			{
				continue;
			}
			offerChaSpecData.put("ATTR_CODE", elementId);
		}
		offerChaSpecDataset.add(offerChaSpecData);
	}
	var offerChaHiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
	$("#"+offerChaHiddenId).val(offerChaSpecDataset);
	
	$("#"+offerChaHiddenId).closest("li").find(".side").html("<span class='e_tag e_tag-green'>已设置</span>");
	
	hidePopup(document.getElementById("offerChaPopupItem"));
	submitOfferChaAfterAction(offerChaSpecDataset);
	
	var offerchalist = new Wade.DatasetList($("#offerchalist").val());
	offerchalist.add(offerChaSpecDataset);
	$("#offerchalist").val(offerchalist);
}

function initPageParamCommon()
{
	var curOfferId = $("#prodDiv_OFFER_ID").val();
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

function submitOfferChaAfterAction(offerChaSpecs)
{
	var submitFuncNameOfferId = "afterSubmitOfferCha_" + $("#cond_OFFER_ID").val();
	var submitFuncName = "afterSubmitOfferCha";
	if($.isFunction(window[submitFuncNameOfferId]))
	{
		window[submitFuncNameOfferId](offerChaSpecs);
	}
	else if($.isFunction(window[submitFuncName])){
		window[submitFuncName](offerChaSpecs);
	}
}

/**
 * bboss把前台没显示属性过滤
 * @param arg
 * @returns {Boolean}
 */
function isBbossNull(arg)
{
    if(!arg && arg!==0 && typeof arg!=="boolean")
    {
        return true ;
    }
    //不显示的属性不提交到后台
    if($("#"+arg).attr("style")=="display:none;")
    {
        return true ;
    }

    //去掉select为none的,兼容ie8的情况
    if($("#"+arg+"_span").children.length>0&&($("#"+arg+"_span").attr("style")=="display:none;" || $("#"+arg+"_span").attr("style")=="DISPLAY: none")){
        return true ;
    }
    return false;
}
/**************************商品特征-结束*****************************/

//提交
function submitApply()
{
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == "")
	{
		$.validate.alerter.one(document.getElementById("cond_GROUP_ID"), "请输入集团编码，然后回车，查询集团信息！");
		return false;
	}
	var offerCode = $("#cond_OFFER_CODE").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one(document.getElementById("cond_OFFER_CODE"), "请选择产品！");
		return false;
	}
	var operType = $("#cond_OPER_TYPE").val();//20:新增,21:资源变更,22:资费变更,23:删除,
	var operCode = transOperCode(operType);//0:新增,1:删除,2:变更
		
	
	var custData = new Wade.DataMap();
	custData.put("GROUP_ID", groupId);
	custData.put("CUST_NAME", $("#cond_CUST_NAME").val());
	
	var offerData = new Wade.DataMap();
	offerData.put("OFFER_CODE", offerCode);
	offerData.put("OFFER_TYPE", "P");
	offerData.put("OFFER_ID", $("#cond_OFFER_ID").val());
	offerData.put("OFFER_NAME", $("#cond_OFFER_NAME").val());
	offerData.put("OPER_CODE", operCode);
	
	var commData = new Wade.DataMap();
	var otherList = new Wade.DatasetList();
	
	var showParamPart = $("#SHOW_PARAM_PART").val();
	if(showParamPart == "1")
	{//标准产品参数
		var standardOfferCha = saveStandardOfferCha();
		offerData.put("OFFER_CHA", standardOfferCha);
	}
	else if(showParamPart == "2")
	{//普通专线产品
		var datalineOfferCha = saveDataLineOfferCha(operCode);
		offerData.put("SUB_OFFER", datalineOfferCha);
		commData.put("DATALINE_OPER_TYPE", $("#DATALINE_OPER_TYPE").val());
		commData.put("DATALINE_SERIAL_NUMBER", $("#DATALINE_SERIAL_NUMBER").val());
	    
		var isCheckTag = $("#check_tag").val();
		if(""!=isCheckTag&&isCheckTag!=null){
			var otherData = new Wade.DataMap();
			otherData.put("ATTR_CODE","isCheckTag");
			otherData.put("ATTR_NAME","是否免勘查");
			otherData.put("ATTR_VALUE",isCheckTag);
			otherData.put("RECODR_NUM","-1");
			otherList.add(otherData);
		}
	}
	else if(showParamPart == "3")
	{//Bboss产品
		var bbossOfferCha = saveBbossOfferCha();
		if("1"==bbossOfferCha)
		{
			MessageBox.alert("提示信息", "请完成产品参数设置再提交！");
			return false;
		}
		offerData.put("SUB_OFFER", bbossOfferCha);
	}
	
	commData.put("OFFER_ID", $("#cond_OFFER_ID").val());
	
	var submitParam = new Wade.DataMap();
	submitParam.put("CUST_DATA", custData);
	submitParam.put("OFFER_DATA", offerData);
	submitParam.put("BUSI_SPEC_RELE", new Wade.DataMap($("#BUSI_SPEC_RELE").text()));
	submitParam.put("NODE_TEMPLETE", new Wade.DataMap($("#NODE_TEMPLETE").text()));
	submitParam.put("COMMON_DATA", commData);
	submitParam.put("OTHER_LIST",otherList);
	
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();

		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ASSIGN_URL").split("?");
					openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]);
				}
				if("ok" == btn){
					window.location.reload();
				}
			}, {"ext1" : "指派"});
		}
		else
		{
			MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ok" == btn){
					window.location.reload();
				}
			});
		}
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

//保存标准产品参数
function saveStandardOfferCha()
{
	var offerChaList = new Wade.DatasetList();
	var offerChaStr = $("#OFFER_CHA_"+$("#cond_OFFER_ID").val()).val();
	var offerChas = new Wade.DatasetList(offerChaStr);
	offerChaList.add(offerChas);
	return offerChaList;
}

//保存普通专线产品参数
function saveDataLineOfferCha(operCode)
{
	var offerChaList = new Wade.DatasetList();
	var mebOfferCode = $("#MEB_OFFER_CODE").val();
	
	
	$("#DATALINE_PARAM_UL").find("input:hidden[id*=OFFER_CHA_]").each(function(){
		var offerChaStr = new Wade.DatasetList($(this).val());
		var MebOfferCha  = new Wade.DataMap();
		MebOfferCha.put("OFFER_CODE",mebOfferCode);
		MebOfferCha.put("OFFER_CHA",offerChaStr);
		MebOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
		MebOfferCha.put("OFFER_TYPE","P");
		MebOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
		MebOfferCha.put("OPER_CODE", operCode);
		
		offerChaList.add(MebOfferCha);
	});
	
	return offerChaList;
}

//保存Bboss产品参数	SUBOFFER:[{OFFER_CODE:	OFFER_NAME:	OFFER_TYPE:	OFFER_ID: OPER_CODE: OFFER_CHA:[{},{}]}]
function saveBbossOfferCha()
{
	var offerChaList = new Wade.DatasetList();
	// 获得指定区域的checkbox
	var chks = $("#BBOSS_PRODUCT_UL [type=checkbox]");
	{
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)//获取选中的列表
			{
				var offerCodeId = $(chks[j]).closest("li").attr("id");
				var chkRed = $("#"+offerCodeId).find(".e_tag-red");
				if(chkRed.length>0)
				{
					return "1";
				}
				
				var offerDataOfferCodeId = "OFFER_DATA_"+offerCodeId;
				var offerChaOfferCodeId = "OFFER_CHA_"+offerCodeId;
				
				var offerDataStr = $("#"+offerDataOfferCodeId).text();
				var offerChaStr = $("#"+offerChaOfferCodeId).val();
				
				var offerDataset = new Wade.DataMap(offerDataStr);
				var offerChaset = new Wade.DatasetList(offerChaStr);
				
				//1.产品基本信息
				var offerData = new Wade.DataMap();
				offerData.put("OFFER_CODE", offerDataset.get("OFFER_CODE"));
				offerData.put("OFFER_TYPE", offerDataset.get("OFFER_TYPE"));
				offerData.put("OFFER_ID",   offerDataset.get("OFFER_ID"));
				offerData.put("OFFER_NAME", offerDataset.get("OFFER_NAME"));
				
				//2.产品参数信息
				var offerChaSpecList = new Wade.DatasetList();
				var offerCha = new Wade.DataMap();
				for(var i = 0; i < offerChaset.length; i++)
				{
					var temp = new Wade.DataMap(); 
					var offerChaSpec = offerChaset.get(i);
					var attrCode = offerChaSpec.get("ATTR_CODE");
					var attrValue = offerChaSpec.get("ATTR_VALUE");
					var attrName = offerChaSpec.get("ATTR_NAME");
			  		 if(""==attrValue||attrValue==null)
			  		 {
			  			continue;
			  		 }
			  		 if(""==attrCode||attrCode==null)
			  		 {
			  			continue;
			  		 }
			  		temp.put("ATTR_CODE",attrCode);
			  		temp.put("ATTR_VALUE",attrValue);
			  		temp.put("ATTR_NAME",attrName);
			  		offerChaSpecList.add(offerChaSpec);
			  	}
				offerData.put("OFFER_CHA",offerChaSpecList);
				
			  	offerChaList.add(offerData);
			}
		}
	}
	return offerChaList;
}

function transOperCode(operType)
{
	var operCode = "2";//变更
	if("20" == operType)
	{
		operCode = "0";//新增
	}
	else if("23" == operType)
	{
		operCode = "1";//删除
	}
	else
	{
		operCode = "2";//资源变更
	}
	return operCode;
}
