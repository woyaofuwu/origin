if(typeof(PriceOfferCha) == "undefined")
{
	window["PriceOfferCha"] = function(id){
		this.id = id;
	};
};

(function(){
	$.extend(PriceOfferCha.prototype, {
		initPriceOfferCha : function(el){
			var self = this;
			var brand = $("#cond_EC_BRAND").val();
			var operType = $("#cond_OPER_TYPE").val();
			var merchOperCode = $("#OPERTYPE").val();
			if("BOSG" == brand && operType == "DstUs"){		        
				MessageBox.alert("该产品操作下不允许操作资费！");
		        return;				
			}
			if("BOSG" == brand && operType == "ChgUs"){
				if(merchOperCode==""||merchOperCode==undefined){					
		            MessageBox.alert("请先设置商品特征！");
		            return;
				}
				if(merchOperCode == "9" || merchOperCode == "3" || merchOperCode == "4"){
		            MessageBox.alert("该产品操作下不允许操作资费！");
		            return;
				}
			}
			
			var offerId = $(el).attr("OFFER_ID");
			var param = "&OFFER_ID=" + offerId;
			
			var pOfferIndex = $(el).closest("li").attr("offerIndex");
			
			var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
			if(offerData.length>0){
				var subOfferDataset = offerData.get("SUBOFFERS");
				for(var i = 0, size = subOfferDataset.length; i < size; i++)
				{
					var subOfferData = subOfferDataset.get(i);
					if(offerId == subOfferData.get("OFFER_ID") && pOfferIndex == subOfferData.get("P_OFFER_INDEX") && subOfferData.get("OPER_CODE") != ACTION_CREATE)
					{
						param = param + "&OFFER_INS_ID=" + subOfferData.get("OFFER_INS_ID") + "&USER_ID=" + subOfferData.get("USER_ID"); 
						break;
					}
				}
			}
			var operType = $("#cond_OPER_TYPE").val();
			if(operType == "CrtUs" || operType == "ChgUs")
			{
				param = param + "&IS_MEB=false";
			}
			else
			{
				param = param + "&IS_MEB=true";
			}
			
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "", param, "PriceOfferChaListPart", function(data){
				
				$("#pricecha_OFFER_ID").val(offerId);
				$("#pricecha_OFFER_INDEX").val(pOfferIndex);
				
				self.initPriceOfferChaVal(offerId, pOfferIndex);
				
				initPriceOfferChaSpec(offerId, pOfferIndex);
				
				$.endPageLoading();
				forwardPopup(el, self.id);
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		},
		initPriceOfferChaVal : function(offerId, pOfferIndex){
			debugger;
			var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
			if(priceOfferChaStr == "")
			{
				return ;
			}
			var priceOfferChaList = new Wade.DatasetList(priceOfferChaStr);
			for(var i = 0, size = priceOfferChaList.length; i < size; i++)
			{
				var priceOfferCha = priceOfferChaList.get(i);
				var chaSpecCode = priceOfferCha.get("CHA_SPEC_CODE");
				if(!chaSpecCode){
					chaSpecCode = priceOfferCha.get("ATTR_CODE");
				}
				var chaValue = priceOfferCha.get("CHA_VALUE");
				if(!chaValue){
					chaValue = priceOfferCha.get("ATTR_VALUE");
				}
				var el = $("div[id=PriceOfferChaListPart] input[name="+chaSpecCode+"]");
				if("checkbox" == el.attr("type"))
				{
					if(chaValue == "1")
					{
						el.attr("checked", true);
					}
					else
					{
						el.attr("checked", false);
					}
				}
				else
				{
					el.val(chaValue);
				}
			}
		},
		submitPriceOfferCha : function(){
			if(!$.validate.verifyAll("PriceOfferChaListPart"))
			{
				return ;
			}
			
			this.calculateParam(); //自动计算总价

			var offerId = $("#pricecha_OFFER_ID").val();
			var pOfferIndex = $("#pricecha_OFFER_INDEX").val();
			var expireTime = $("#expire_"+offerId+"_"+pOfferIndex).text();
			var isRemindTime = $("#isRemindTime_"+offerId+"_"+pOfferIndex).val();
			if(isRemindTime=='1'){
				if(expireTime == '2050-12-31'){
					alert("结束日期为2050年12月31日，请确认是否修改");
				}
				if('130010010208'==offerId||'130010010209'==offerId||'130010010210'==offerId||'130010010211'==offerId||'130010010212'==offerId){
					alert("折扣套餐包的结束时间默认是12个月,结束日期为"+expireTime+"，请确认是否修改");
				}
				var now = new Date();
				var startDate = $("#valid_"+offerId+"_"+pOfferIndex).text();
		        if(startDate < now.format('yyyy-MM-dd')){
		        	MessageBox.alert("开始时间不能小于当前时间");
		        	return false;
		        }
			}
			
			var topOfferId = $("#prodDiv_OFFER_ID").val();
			var offerChaIsChg = false; // 资费类商品特征是否被修改(针对实例数据)
			var offerChaIsTrue = 0; //BBOSS资费属性校验是否通过统计
			var priceOfferChaList = new Wade.DatasetList(); //资费类商品特征
			$("div[id=PriceOfferChaListPart] input").each(function(){
				var chaSpecCode = $(this).attr("id");
				var chaValue = $(this).attr("value");
				if($(this).attr("type") == "checkbox")
				{
					chaValue = $(this).attr("checked") ? "1" : "0";
				}
				var chaSpecId = $(this).attr("CHA_SPEC_ID");
				if(!chaSpecId)
				{
					return true; //相当于 continue
				}
				//只要是chaSpecCode=ApprovalNum 才去检验审批文号是否只填了数字或字母
				if("ApprovalNum" == chaSpecCode){
				  if(!checkshenpiwenhao(chaSpecCode,chaValue,topOfferId)){
					  offerChaIsTrue++;
					  return false;
				  }		
				}
				if(!checkBBossPriceOfferCha(chaSpecCode,chaValue)){
					offerChaIsTrue++;
					return false;
				}
				if(!checkJtclbDiscnt(chaSpecCode,chaValue,offerId)){
					return false;
				}
				//MAS 资费属性校验
					if(!checkNewXYZMessageYearParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZMessageMonthParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZMessageYearParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZMessageMonthSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZMessageYearSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZ_MMSMonthParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZ_MMSYearParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZ_MMSMonthSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkXYZ_MMSYearSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkNewXYZMessageYearSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkNewXYZ_MMSYearParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
					if(!checkNewXYZ_MMSYearSpecialParam(chaSpecCode,chaValue,offerId)){
						offerChaIsTrue++;
						return false;
					}
				
				
				var priceOfferChaData = new Wade.DataMap();
				priceOfferChaData.put("ATTR_VALUE", chaValue);
				priceOfferChaData.put("ATTR_CODE", chaSpecCode);
				
				//判断资费类商品特征实例是否变更
				priceOfferChaData.put("OLD_VALUE", $(this).attr("oldValue"));
				if(chaValue != $(this).attr("oldValue"))
				{
					offerChaIsChg = true;
				}
				
				if(!setPriceOfferChaSpecValue(offerId,chaSpecCode,chaValue,priceOfferChaData)){
					offerChaIsTrue++;
					return false;
				}
				
			priceOfferChaList.add(priceOfferChaData);
			});
			
			if(offerChaIsTrue > 0 )
			{
				return false;
			}
			var self = this;
/*			var param = "&OFFER_ID=" + $("#cond_OFFER_ID").val() + "&PRICE_OFFER_CHA_LIST=" + priceOfferChaList.toString();
			$.httphandler.post("com.asiainfo.veris.crm.order.web.enterprise.cs.handler.priceoffercha.PriceOfferChaChkHandler", "validatePriceOfferCha", param, function(data){
				
				if(data && data.length > 0)
				{
					var err_message = data.get("err_message");
					var priceOfferId = data.get("priceOfferId");
					
					if(err_message != null && err_message != "" && typeof(err_message) != "undefined")
					{
			 			$.validate.alerter.one($("#"+priceOfferId)[0], err_message);
			 			return;
					}
				}
				
			}, 
			function(error_code, error_info){
				MessageBox.error(error_code, error_info);
			});*/

			//将保存的值设置到页面隐藏域中
			$("#pcha_"+offerId+"_"+pOfferIndex).val(priceOfferChaList);
			
			if(priceOfferChaList.length > 0)
			{//将数据保存到offerData中
				var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
				var subOfferDataset = offerData.get("SUBOFFERS");
				if(typeof(subOfferDataset) == "undefined" && subOfferDataset.length == 0)
				{
					return ;
				}
				for(var i = 0, size = subOfferDataset.length; i < size; i++)
				{
					var subOffer = subOfferDataset.get(i);
					if(offerId != subOffer.get("OFFER_ID") || pOfferIndex != subOffer.get("P_OFFER_INDEX"))
					{
						continue;
					}
					if(subOffer.get("OPER_CODE") != ACTION_CREATE && offerChaIsChg)
					{//子商品不是新增状态且商品特征发生变更
						subOffer.put("OPER_CODE", ACTION_UPDATE);
					}
					subOffer.put("OFFER_CHA_SPECS", priceOfferChaList);
					break;
				}
				//保存数据
				PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
			}
			
			//隐藏资费类商品特征"待设置"标签
			self.hidePriceOfferChaTag(offerId, pOfferIndex);
			
			//关闭资费类商品特征popup
			backPopup(document.getElementById(self.id));
			
		},
		calculateParam : function(){
			var self = this;
			$("div[id=PriceOfferChaListPart] input[calculate_formula]").each(function(){
				var formula = $(this).attr("calculate_formula");
				var regExp = /([0-9]+)(.{1})([A-Za-z0-9]+)(.{1})([A-Za-z0-9]+)/;
				var arr = formula.match(regExp);
				if(arr && arr.length == 6)
				{//arr格式：100*PRICE*COUNT,100,*,PRICE,*,COUNT
					var value1 = arr[1];
					var oper1 = arr[2];
					var value2 = self.getParamValue(arr[3]);
					var oper2 = arr[4];
					var value3 = self.getParamValue(arr[5]);
					var result0 = eval(value1 + oper1 + value2);
					var result = eval(result0 + oper2 + value3);
					
					$(this).attr("value", result);
				}
			});
		},
		getParamValue : function(fieldName){
			var paramValue = $("div[id=PriceOfferChaListPart] input[name="+fieldName+"]").val();
			if(!paramValue)
			{
				var desc = $("div[id=PriceOfferChaListPart] input[name="+fieldName+"]").attr("desc");
				MessageBox.alert("提示信息", "["+desc+"]不能为空！");
			}
			return paramValue;
		},
		hidePriceOfferChaTag : function(priceOfferId, pOfferIndex){
			//隐藏资费类商品特征"待设置"标签 显示"已设置"标签
			var liName = "li_" + priceOfferId+"_"+pOfferIndex;
			$("ul[id=priceOfferUL] li").each(function(){
				if($(this).attr("id") == liName)
				{
//					$(this).children().eq("1").css("display", "none");
					$(this).children().eq("1").attr("tag", "1"); //tag=1，已设置
					$(this).children().eq("1").html("<span class='e_tag e_tag-green'>已设置</span>");
				}
			});
		}
	});
})();

function setCalendar(el)
{
	var calendarName = $(el).attr("desc");
	$("#calendarEle").html(calendarName);
	popupDateSelect(el, "calendarItem");
}

function checkBBossPriceOfferCha(chaSpecCode, chaValue)
{
	//和对讲产品特殊处理
	var filter = /^\d+(\.\d+)?$/;
	if ("00039801"==chaSpecCode&&!filter.test(chaValue)) {
        MessageBox.alert("资费值提示", "和对讲资费详情"+chaSpecCode+"应为数字");
	    return false;
	 }
	if("00039801"==chaSpecCode&&chaValue<4.2){
        MessageBox.alert("资费值提示", "和对讲资费详情"+chaSpecCode+"价格不能低于4.2元/月/人");
		return false;
	}
	if ("000117601"==chaSpecCode&&!filter.test(chaValue)) {
        MessageBox.alert("资费值提示", "和对讲(功能费)资费详情"+chaSpecCode+"应为数字");
	    return false;
	 }
	if("000117601"==chaSpecCode&&chaValue<6){
        MessageBox.alert("资费值提示", "和对讲(功能费)资费详情"+chaSpecCode+"价格不能低于6元/月/人");
		return false;
	 }
	
	//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw start
	var singleAttrValue = 0;//定义一个单个属性值变量
	if(chaSpecCode == "100005")
	{
		singleAttrValue = chaValue;
		var reg = /^\d+$/;
		if(!reg.test(singleAttrValue))
		{
			MessageBox.alert('保底比例_资费属性必须为正整数!');
			return false;
		}

		if(parseInt(singleAttrValue) > 100)
		{
			MessageBox.alert('保底比例_资费属性不能大于100，请重新输入!');
			return false ;
		}
	}
	if(chaSpecCode == "100099")
	{
		var val100001 = $("#100001").val();
		val100001 = val100001.replace("Mb","");
		var val100005 = $("#100005").val();
		var val100099 = val100001+"||"+val100005;
		$("#100099").val(val100099);
	}
	
	//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw end
    
	if("000151401"==chaSpecCode&&chaValue<=0){
        MessageBox.alert("资费值提示", "千里眼(摄像头功能费)资费详情"+chaSpecCode+"价格不能等于或低于0元/月/路");
        return false;
    }
	return true;
	
}
function  checkJtclbDiscnt(chaSpecCode,chaValue,offerid){
	if(!("130084011444"==offerid||"130084011443"==offerid)){
		return true;
	}
	if("18605"!=chaSpecCode){
		return true;
	}

	//折扣率校验
	if(!$.verifylib.checkPInteger(chaValue)){
		MessageBox.alert("提示信息", "折扣率请填写>=60的整数！");
		return false;
	}else{
		if(!(chaValue>=60 && chaValue<=100)){
			MessageBox.alert("提示信息", "该优惠要求折扣必须在[60-100]之间！");
			return false;
		}
	}
	return true;
}

//------集团短彩信XYZ资费套餐需求验证以下部分暂时写在这里 下个省优化会搬动以下JS(liuxx3 2014 10-11)------------

/**
 * 作用:选择XYZ资费短信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthParam(chaSpecCode,chaValue,offerId){
	if("130009900101" != offerId ){
		return true;
	}
	var xyzCost = $("#30011118").val();
	var xyzItem = $("#30011116").val();
	var xyzExcessCost = $("#30011117").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 200)
	{
		if(Number(xyzItem) < 3000)
		{
			var xyzWithin = Number(xyzCost)/Number(xyzItem);
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需少于3000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>200||Number(xyzCost)==200)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>3000||Number(xyzItem)==3000)&& Number(xyzItem)<16000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.07){
				MessageBox.alert("","您输入的超出费用不得低于0.07元/条 输入有误 请重新输入！");
				return false;
			}
			return true;

		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于3000条小于16000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>16000||Number(xyzItem)==16000)&& Number(xyzItem)<50000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于16000条小于50000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>50000||Number(xyzItem)==50000)&& Number(xyzItem)<100000){
			if(xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于50000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>5500||Number(xyzCost)==5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)){
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条 输入有误 请重新输入！");
			return false;
		}
	}
}


/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZMessageYearParam(chaSpecCode,chaValue,offerId){
	if("130009900102" != offerId){
		return true;
	}
	var xyzCost = $("#30011108").val();
	var xyzItem = $("#30011106").val();
	var xyzExcessCost = $("#30011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900103" != offerId){
		return true;
	}

	var xyzCost = $("#30011338").val();
	var xyzItem = $("#30011336").val();
	var xyzExcessCost = $("#30011337").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.04)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.04元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.04)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.04元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageYearSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900104" != offerId){
		return true;
	}
	
	var xyzCost = $("#30011328").val();
	var xyzItem = $("#30011326").val();
	var xyzExcessCost = $("#30011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}


/**
 * 作用:选择XYZ资费彩信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthParam(chaSpecCode,chaValue,offerId){
	if("130009900201" != offerId){
		return true;
	}
	var xyzCost = $("#30011138").val();
	var xyzItem = $("#30011136").val();
	var xyzExcessCost = $("#30011137").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 400)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem) < 2000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.3){
				MessageBox.alert("","您输入的超出费用不得低于0.3元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
				MessageBox.alert("","您输入的赠送条数需小于2000条 输入有误 请重新输入！");
				return false;
		}

	}

	if((Number(xyzCost)>400||Number(xyzCost)==400)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>2000||Number(xyzItem)==2000)&& Number(xyzItem)<5000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于2000条小于5000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<15000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于15000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>15000||Number(xyzItem)==15000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于15000条小于25000条 输入有误 请重新输入！");
			return false;
		}
	}

	if(Number(xyzCost)>5000||Number(xyzCost)==5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>25000||Number(xyzItem)==25000)
		{
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条 输入有误 请重新输入！");
			return false;
		}

	}
}


/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZ_MMSYearParam(chaSpecCode,chaValue,offerId){
	if("130009900202" != offerId){
		return true;
	}
	var xyzCost = $("#30011128").val();
	var xyzItem = $("#30011126").val();
	var xyzExcessCost = $("#30011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900203" != offerId && "130009900211" != offerId && "130009900213" != offerId){
		return true;
	}
	var xyzCost = $("#30011238").val();
	var xyzItem = $("#30011236").val();
	var xyzExcessCost = $("#30011237").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if("130009900211" == offerId || "130009900213" == offerId){
		if(xyzWithin < 0.084)
		{
			MessageBox.alert("","您输入的功能费用与赠送条数价格需大于等于0.084元/条 输入有误 请重新输入！");
			return false;
		}
		if(Number(xyzExcessCost)<0.084)
		{
			MessageBox.alert("","您输入的超出费用不得低于0.084 输入有误 请重新输入！");
			return false;
		}
		return true;
	}
	if(xyzWithin < 0.12)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.12元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.12)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.12元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSYearSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900204" != offerId){
		return true;
	}
	var xyzCost = $("#30011228").val();
	var xyzItem = $("#30011226").val();
	var xyzExcessCost = $("#30011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZMessageYearParam(chaSpecCode,chaValue,offerId){
	if("130009900105" != offerId){
		return true;
	}
	var xyzCost = $("#40011108").val();
	var xyzItem = $("#40011106").val();
	var xyzExcessCost = $("#40011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2016 --01 --09
 */
function checkNewXYZMessageYearSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900106" != offerId){
		return true;
	}
	var xyzCost = $("#40011328").val();
	var xyzItem = $("#40011326").val();
	var xyzExcessCost = $("#40011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.04)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.04元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.04)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.04元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZ_MMSYearParam(chaSpecCode,chaValue,offerId){
	if("130009900107" != offerId){
		return true;
	}
	var xyzCost = $("#40011128").val();
	var xyzItem = $("#40011126").val();
	var xyzExcessCost = $("#40011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkNewXYZ_MMSYearSpecialParam(chaSpecCode,chaValue,offerId){
	if("130009900108" != offerId && "130009900111" != offerId && "130009900113" != offerId){
		return true;
	}
	var xyzCost = $("#40011228").val();
	var xyzItem = $("#40011226").val();
	var xyzExcessCost = $("#40011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if("130009900111" == offerId || "130009900113" == offerId){
		if(xyzWithin < 0.084)
		{
			MessageBox.alert("","您输入的功能费用与赠送条数价格需大于等于0.084元/条 输入有误 请重新输入！");
			return false;
		}
		if(Number(xyzExcessCost)<0.084)
		{
			MessageBox.alert("","您输入的超出费用不得低于0.084 输入有误 请重新输入！");
			return false;
		}
		return true;
	}

	if(xyzWithin < 0.12)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.12元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.12)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.12元 输入有误 请重新输入！");
		return false;
	}
	return true;
}
//-------------------------集团短彩信XYZ资费套餐需求END(liuxx3 2014-10-11)------------------------------------

//20190614yuanza REQ201904080043
function  checkshenpiwenhao(attrCode,attrValue,OfferId){
	if (("110020161123" == OfferId || "110020171215" == OfferId ||"110020161125" == OfferId || "110020005014" == OfferId)
			&& "ApprovalNum" == attrCode){
		var grep = /^[A-Za-z0-9]+$/;
	   	if (!grep.test(attrValue)&& attrValue != ""){
	   		MessageBox.alert("","审批文号只能填写数字或英文字母,请重新输入！");
	   		return false;
	   	}
	   	return true;
	} else{
		return true;
	}
		//end
}
var min154485forall = 0;
function initPriceOfferChaSpec(offerId,pOfferIndex)
{
	debugger;
	if(offerId != "" && ("130010010201" == offerId || "130010010202" == offerId))
	{
		var chaObject144485 = $("div[id=PriceOfferChaListPart] input[name=144485]");//分钟数
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		var chaObject124485 = $("div[id=PriceOfferChaListPart] input[name=124485]");//流量
		var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
		//初始化的时候是空的
		if(priceOfferChaStr == "")
		{
			if(chaObject144485)
			{
				var attrValue = chaObject144485.val()/60;//集团套餐分钟数，秒转为分
				$("div[id=PriceOfferChaListPart] input[name=144485]").val(attrValue);
				
				chaObject144485.change(function(){
					calcFunFee(offerId);
				});
			}
			if(chaObject154485)
			{
				var attrValue = chaObject154485.val()/100;//集团套餐功能费，分转为元
				$("div[id=PriceOfferChaListPart] input[name=154485]").val(attrValue);
			}
			if(chaObject124485)
			{
				var attrValue = (chaObject124485.val()/1024/1024)+"M";//集团套餐流量数，字节转为M
				$("div[id=PriceOfferChaListPart] input[name=124485]").val(attrValue);
				chaObject124485.change(function(){
					calcFunFee(offerId);
				});
			}
		}
		else 
		{
			
			var priceOfferChaList = new Wade.DatasetList(priceOfferChaStr);
			for(var i = 0, size = priceOfferChaList.length; i < size; i++)
			{
				var priceOfferCha = priceOfferChaList.get(i);
				var chaSpecCode = priceOfferCha.get("ATTR_CODE");
				var chaValue = priceOfferCha.get("ATTR_VALUE");
				if("144485" == chaSpecCode && chaObject144485){
					chaObject144485.val(chaValue/60);//集团套餐分钟数，秒转为分
				}else if("154485" == chaSpecCode && chaObject154485){
					chaObject154485.val(chaValue/100);//集团套餐功能费，分转为元
				}else if("124485" == chaSpecCode && chaObject124485){
					chaObject124485.val((chaValue/1024/1024)+"M");//集团套餐流量数，字节转为M
				}
			}
			
			if(chaObject144485)
			{
				chaObject144485.change(function(){
					calcFunFee(offerId);
				});
			}
			if(chaObject124485)
			{
				chaObject124485.change(function(){
					calcFunFee(offerId);
				});
			}
		}
		
		if(chaObject154485){
			chaObject154485.attr("disabled", "true");	//功能费不可编辑
		}
	}
	var min154485 = 0;
	//REQ202006010005  关于优化工作手机语音包和流量包的需求
	if(offerId != "" && ("130010010203" == offerId||"130010010204" == offerId||"130010010208" == offerId||"130010010209" == offerId))
	{
		var chaObject144485 = $("div[id=PriceOfferChaListPart] input[name=144485]");//分钟数
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		var chaObject124485 = $("div[id=PriceOfferChaListPart] input[name=124485]");//流量
		var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
		//初始化的时候是空的
		if(priceOfferChaStr == "")
		{
			if(chaObject144485)
			{
				var attrValue = chaObject144485.val()/60;//集团套餐分钟数，秒转为分
				$("div[id=PriceOfferChaListPart] input[name=144485]").val(attrValue);
				
				chaObject144485.change(function(){
					calcFunFee(offerId);
					min154485 = chaObject154485.val();
					min154485forall = chaObject154485.val();
				});
			}
			if(chaObject154485)
			{
				var attrValue = chaObject154485.val()/100;//集团套餐功能费，分转为元
				$("div[id=PriceOfferChaListPart] input[name=154485]").val(attrValue);
				chaObject154485.change(function(){
					var checkval = chaObject154485.val();
					if(checkval < min154485){
						var checkInfo = "功能费需大于等于"+min154485;
						alert(checkInfo);
						return false;
					}
				});
				
			}
			if(chaObject124485)
			{
				var attrValue = (chaObject124485.val()/1024/1024/1024)+"G";//集团套餐流量数，字节转为G
				$("div[id=PriceOfferChaListPart] input[name=124485]").val(attrValue);
				chaObject124485.change(function(){
					calcFunFee(offerId);
					min154485 = chaObject154485.val();
					min154485forall = chaObject154485.val();
				});
			}
		}
		else 
		{   
			var priceOfferChaList = new Wade.DatasetList(priceOfferChaStr);
			for(var i = 0, size = priceOfferChaList.length; i < size; i++)
			{
				var priceOfferCha = priceOfferChaList.get(i);
				var chaSpecCode = priceOfferCha.get("ATTR_CODE");
				var chaValue = priceOfferCha.get("ATTR_VALUE");
				if("144485" == chaSpecCode && chaObject144485){
					chaObject144485.val(chaValue/60);//集团套餐分钟数，秒转为分
				}else if("154485" == chaSpecCode && chaObject154485){
					chaObject154485.val(chaValue/100);//集团套餐功能费，分转为元
				}else if("124485" == chaSpecCode && chaObject124485){
					chaObject124485.val((chaValue/1024/1024/1024)+"G");//集团套餐流量数，字节转为G
				}
			}
			
			if(chaObject144485)
			{
				chaObject144485.change(function(){
					calcFunFee(offerId);
					min154485 = chaObject154485.val();
					min154485forall = chaObject154485.val();
				});
			}
			if(chaObject124485)
			{
				chaObject124485.change(function(){
					calcFunFee(offerId);
					min154485 = chaObject154485.val();
					min154485forall = chaObject154485.val();
				});
			}
		}
		
		if(chaObject154485){
			chaObject154485.change(function(){
				var checkval = chaObject154485.val();
				if(checkval < min154485){
					var checkInfo = "功能费需大于等于"+min154485;
					alert(checkInfo);
					return false;
				}
			});
		}
	}
	
	//REQ201908130005  MDM手机管控资费套餐
	if(offerId != "" && "110084071842" == offerId)
	{
		var chaObject254485 = $("div[id=PriceOfferChaListPart] input[name=254485]");//功能费
		var chaObject214485 = $("div[id=PriceOfferChaListPart] input[name=214485]");//折扣
		var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
		//初始化的时候是空的
		if(priceOfferChaStr == "")
		{
			 
			if(chaObject254485)
			{
				var attrValue = chaObject254485.val()/100;//集团套餐功能费，分转为元
				$("div[id=PriceOfferChaListPart] input[name=254485]").val(attrValue);
			}
			if(chaObject214485)
			{
				//var attrValue = (chaObject124485.val()/1024/1024)+"M";//集团套餐流量数，字节转为M
				//$("div[id=PriceOfferChaListPart] input[name=124485]").val(attrValue);
				chaObject214485.change(function(){
					calcFunFeeMDM(offerId);
				});
			}
		}
		else 
		{
			
			var priceOfferChaList = new Wade.DatasetList(priceOfferChaStr);
			for(var i = 0, size = priceOfferChaList.length; i < size; i++)
			{
				var priceOfferCha = priceOfferChaList.get(i);
				var chaSpecCode = priceOfferCha.get("ATTR_CODE");
				var chaValue = priceOfferCha.get("ATTR_VALUE");
				if("254485" == chaSpecCode && chaObject254485){
					chaObject254485.val(chaValue/100);//集团套餐功能费，分转为元
				} 
			}
			  
			if(chaObject214485)
			{
				chaObject214485.change(function(){
					calcFunFeeMDM(offerId);
				});
			}
		}
		
		if(chaObject254485){
			chaObject254485.attr("disabled", "true");	//功能费不可编辑
		}
	}
	
	//IDC峰值计费套餐与IDC95法则计费套餐：宽带种类属性换算Mb显示
	if(offerId != "" && ("130084012040" == offerId || "130084012041" == offerId))
	{
		var chaObject61301 = $("div[id=PriceOfferChaListPart] input[name=61301]");//宽带种类（Mb）
		
		var priceOfferChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
		//初始化的时候是空的
		if(priceOfferChaStr == "")
		{
			if(chaObject61301)
			{
				var attrValue = chaObject61301.val()*1024;//gb转换mb显示
				
				chaObject61301.val(attrValue);//gb转换mb显示
				
			}
			
		}
		else 
		{
			
			var priceOfferChaList = new Wade.DatasetList(priceOfferChaStr);
			for(var i = 0, size = priceOfferChaList.length; i < size; i++)
			{
				var priceOfferCha = priceOfferChaList.get(i);
				var chaSpecCode = priceOfferCha.get("ATTR_CODE");
				var chaValue = priceOfferCha.get("ATTR_VALUE");
				if("61301" == chaSpecCode && chaObject61301){
					
					chaObject61301.val(chaValue);//gb转换mb显示
					
				}
			}
			
		}
				
	}
	
}


function calcFunFee(offerId)
{
	debugger;
	//分钟数对应的功能费
	if("130010010201" == offerId)
	{
		var chaObject144485 = $("div[id=PriceOfferChaListPart] input[name=144485]");//分钟数
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject144485)
		{
			var oneAttrValue = chaObject144485.val();//分钟数
			var flag = $.verifylib.checkPInteger(oneAttrValue);
			if(!flag || parseInt(oneAttrValue,10)<100)
			{
				var checkInfo = "分钟数校验：分钟数必须是大于0的整数，最低100分钟起步!";
				alert(checkInfo);
				return false;
			}
			var funFee = 0;
			//100-500 0.07
			if(oneAttrValue<=500){
				funFee = 0.07 * oneAttrValue;
			}
			//501-1000 0.05 
			else if(oneAttrValue<=1000 && oneAttrValue>500){
				funFee = 0.07 * 500 + 0.05*(oneAttrValue - 500)
			}
			//1000分钟以上 0.03 
			else if(oneAttrValue>1000){
				funFee = 0.07 * 500 + 0.05*500 + 0.03*(oneAttrValue - 1000)
			}
			if(chaObject154485){
				chaObject154485.val(Math.round(funFee));//功能费：元四舍五入后转为分
			}
			//chaObject144485.val(oneAttrValue*60);//分钟数分转为秒
			chaObject144485.val(oneAttrValue);//分钟数分转为秒
		}
	} else if("130010010202" == offerId) {//流量对应的功能费
		var chaObject124485 = $("div[id=PriceOfferChaListPart] input[name=124485]");//流量
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject124485)
		{
			var oneAttrValue = chaObject124485.val();//流量
			var gprsNum = oneAttrValue.substring(0, oneAttrValue.length-1);	//流量值
			var unit = oneAttrValue.substring(oneAttrValue.length-1, oneAttrValue.length);				//流量单位
			if((unit=="M" || unit=="G")&&($.verifylib.checkNumberRange(gprsNum, 0, 99999))){
				if(unit=="M" && parseFloat(gprsNum,10)<500){
					var checkInfo = "流量校验：流量最低500M起步！";
					alert(checkInfo);
					return false;
				}
				if(unit=="G" && parseFloat(gprsNum,10)<(500/1024)){
					var checkInfo = "流量校验：流量最低500M起步！";
					alert(checkInfo);
					return false;
				}
				if(unit=="G"){
					gprsNum = Math.round(1024 * gprsNum);	//G转为M
				}
			}else{
				var checkInfo = "流量校验：流量请输入大于0的数字并以单位M或G结尾，比如：700M,2G等！";
				alert(checkInfo);
				return false;
			}
			
			/* REQ201908160015 优化工作手机流量包资费需求
			 现需求的价格
		    0-1GB             4元/GB，0.004元/MB（满1G按4元计算，超出部分未满1G的，按0.004元/MB）
		    1GB以上-10GB      4元/GB，0.004元/MB （满1G按4元计算，超出部分未满1G的，按0.004元/MB）
		    10GB以上-50GB     3元/GB，0.003元/MB （满1G按3元计算，超出部分未满1G的，按0.003元/MB）
		    50GB以上          2元/GB，0.002元/MB （满1G按2元计算，超出部分未满1G的，按0.002元/MB）
		    以上是按阶梯式费用计算，计算出来的费用总和保留到小数点后两位，其余舍去（不需要4舍5入）
		    注意：1GB=1024MB 
		    parseInt 取整 ； % 取余
		    */
			
			var funFee = 0;
			//500M-1G  4元/GB，0.004元/MB 
			if(gprsNum<=1024){
				funFee =  parseInt(gprsNum/1024) * 4 + 0.004 * (gprsNum%1024);
			}
			//1G-10G   4元/GB，0.004元/MB 
			else if(gprsNum>1024 && gprsNum<=1024*10 ){
				funFee = 1 * 4 + 4 * parseInt((gprsNum-1024)/1024) + 0.004 * (gprsNum%1024);
			}
			//10GB以上-50GB  3元/GB，0.003元/MB 
			else if(gprsNum>1024*10 && gprsNum<=1024*50){
				funFee = 10 * 4 + 3 * parseInt((gprsNum-10*1024)/1024) + 0.003 * (gprsNum%1024);
			}
			//50GB以上     2元/GB，0.002元/MB    
			else if(gprsNum>1024*50){
				funFee = 10 * 4 + 40 * 3 + 2 * parseInt((gprsNum-50*1024)/1024) + 0.002 * (gprsNum%1024);
			}
			if(chaObject154485){
				//chaObject154485.val(Math.round(funFee)*100);//功能费：元四舍五入后转为分
				chaObject154485.val(parseInt(funFee*100)/100); //功能费：元保留2位小数点 
			}
			//chaObject124485.val(gprsNum*1024*1024);//流量数M转为B(字节)
			//chaObject124485.val(gprsNum);//流量数M转为B(字节)
		}
	} else if("130010010203" == offerId||"130010010208" == offerId) {//流量对应的功能费
		var chaObject144485 = $("div[id=PriceOfferChaListPart] input[name=144485]");//分钟数
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject144485)
		{
			var oneAttrValue = chaObject144485.val();//分钟数
			var flag = $.verifylib.checkPInteger(oneAttrValue);
			if(!flag || parseInt(oneAttrValue,10)<100)
			{
				var checkInfo = "分钟数校验：分钟数必须是大于0的整数!";
				alert(checkInfo);
				return false;
			}
			
			//0-1000分钟（含），0.06元/分钟；1000分钟以上，0.05元/分钟； 
			var funFee = 0;
			//0-1000分钟（含），0.06元/分钟
			if(oneAttrValue<=1000){
				funFee = 0.06 * oneAttrValue;
			}
			//1000分钟以上，0.05元/分钟；
			else if(oneAttrValue>1000){
				funFee = 0.06 * 1000 + 0.05*(oneAttrValue - 1000)
			}
			if(chaObject154485){
				chaObject154485.val(Math.round(funFee));//功能费：元四舍五入后转为分
			}
			//chaObject144485.val(oneAttrValue*60);//分钟数分转为秒
			chaObject144485.val(oneAttrValue);//分钟数分转为秒
		}
	} else if("130010010204" == offerId||"130010010209" == offerId) {//流量对应的功能费
		var chaObject124485 = $("div[id=PriceOfferChaListPart] input[name=124485]");//流量
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject124485)
		{
			var oneAttrValue = chaObject124485.val();//流量
			var gprsNum = oneAttrValue.substring(0, oneAttrValue.length-1);	//流量值
			var flag = checkDecimalOnePoint(gprsNum);
			var unit = oneAttrValue.substring(oneAttrValue.length-1, oneAttrValue.length);				//流量单位
			if(unit=="M"||unit!="G"){
					var checkInfo = "流量校验：流量请输入大于0的数字并G结尾，比如：1G,2G等！";
					alert(checkInfo);
					return false;
				
			}else{
					if(!flag){
						var checkInfo = "流量校验：流量输入大于0的数字，若输入小数，请保留一位小数输入，比如0.5G,1.5G等";
						alert(checkInfo);
						return false;
				     }
					
			}
			
			/* REQ202006010005  关于优化工作手机语音包和流量包的需求
			 现需求的价格
		   政企流量包0-2GB（含），3元/GB；2-20GB（含），2元/GB；20-40GB（含），1.5元/GB；40-50GB（含），1元/GB；50-200GB（含），0.4元/GB 
		    注意：1GB=1024MB 
		    parseInt 取整 ； % 取余
		    */
			
			var funFee = 0;
			//0-2GB（含），3元/GB
			if(gprsNum<=2&&gprsNum>0){
				funFee =  gprsNum * 3;
			}
			//2-20GB（含），2元/GB
			else if(gprsNum>2 && gprsNum<=20 ){
				funFee = 2 * 3 + 2 * (gprsNum-2);
			}
			//20-40GB（含），1.5元/GB
			else if(gprsNum>20 && gprsNum<=40){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (gprsNum-20);
			}
			//40-50GB（含），1元/GB   
			else if(gprsNum>40 && gprsNum<=50){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (40-20) + 1 * (gprsNum-40);
			}
			//50-200GB（含），0.4元/GB
			else if(gprsNum>50 && gprsNum<=200){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (40-20) + 1 * (50-40) + 0.4 * (gprsNum-50);
			}
			if(chaObject154485){
				//chaObject154485.val(Math.round(funFee)*100);//功能费：元四舍五入后转为分
				chaObject154485.val(parseInt(funFee*100)/100); //功能费：元保留2位小数点 
			}
			//chaObject124485.val(gprsNum*1024*1024);//流量数M转为B(字节)
			//chaObject124485.val(gprsNum);//流量数M转为B(字节)
		}
	}
	return true;
}


//校验小数点只能保留后1位
function checkDecimalOnePoint(num){
	var point = /^\d+(\.\d{0,1})?$/;
	if(!point.test(num)){
		return false;
	}else{
		return true;
	}
}



//REQ201908130005  MDM手机管控资费套餐
function calcFunFeeMDM(offerId)
{
	debugger;
	//折扣对应的功能费
	if("110084071842" == offerId)
	{
		var chaObject214485 = $("div[id=PriceOfferChaListPart] input[name=214485]");//折扣
		var chaObject254485 = $("div[id=PriceOfferChaListPart] input[name=254485]");//功能费
		if(chaObject214485)
		{
			var oneAttrValue = chaObject214485.val();//折扣
			var flag = $.verifylib.checkPInteger(oneAttrValue);
			if(!flag ||  parseInt(oneAttrValue,10)< 0 || parseInt(oneAttrValue,10) > 100  )
			{
				var checkInfo = "折扣校验：折扣必须是大于0小于100的整数!";
				alert(checkInfo);
				return false;
			}
			//var funFee = chaObject254485.val();//功能费;
			//var defaultValue = chaObject254485.attr('defaultValue');
			var defaultFee = 15;//默认套餐资费15元，如果修改配置，这里也要做相应的修改
			chaObject254485.val(Math.round(defaultFee*oneAttrValue/100));//功能费：元四舍五入
		}
	}
	
	return true;
}

function check154485Fee(offerId){
	debugger;
	if("130010010203" == offerId ){
		var chaObject144485 = $("div[id=PriceOfferChaListPart] input[name=144485]");//分钟数
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject144485)
		{
			var oneAttrValue = chaObject144485.val();//分钟数
			
			//0-1000分钟（含），0.06元/分钟；1000分钟以上，0.05元/分钟； 
			var funFee = 0;
			//0-1000分钟（含），0.06元/分钟
			if(oneAttrValue<=1000){
				funFee = 0.06 * oneAttrValue;
			}
			//1000分钟以上，0.05元/分钟；
			else if(oneAttrValue>1000){
				funFee = 0.06 * 1000 + 0.05*(oneAttrValue - 1000)
			}
			if(chaObject154485){
				var min154485 = chaObject154485.val();
				if(min154485 < funFee){
					var checkInfo = "功能费校验：功能费必须大于等于"+funFee;
					alert(checkInfo);
					return false;
				}
			}
			
		}
	}else if("130010010204" == offerId){
		var chaObject124485 = $("div[id=PriceOfferChaListPart] input[name=124485]");//流量
		var chaObject154485 = $("div[id=PriceOfferChaListPart] input[name=154485]");//功能费
		if(chaObject124485)
		{
			var oneAttrValue = chaObject124485.val();//流量
			var gprsNum = oneAttrValue.substring(0, oneAttrValue.length-1);	//流量值
			var unit = oneAttrValue.substring(oneAttrValue.length-1, oneAttrValue.length);				//流量单位
			
			
			/* REQ202006010005  关于优化工作手机语音包和流量包的需求
			 现需求的价格
		   政企流量包0-2GB（含），3元/GB；2-20GB（含），2元/GB；20-40GB（含），1.5元/GB；40-50GB（含），1元/GB；50-200GB（含），0.4元/GB 
		    注意：1GB=1024MB 
		    parseInt 取整 ； % 取余
		    */
			
			var funFee = 0;
			//0-2GB（含），3元/GB
			if(gprsNum<=2&&gprsNum>0){
				funFee =  gprsNum * 3;
			}
			//2-20GB（含），2元/GB
			else if(gprsNum>2 && gprsNum<=20 ){
				funFee = 2 * 3 + 2 * (gprsNum-2);
			}
			//20-40GB（含），1.5元/GB
			else if(gprsNum>20 && gprsNum<=40){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (gprsNum-20);
			}
			//40-50GB（含），1元/GB   
			else if(gprsNum>40 && gprsNum<=50){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (40-20) + 1 * (gprsNum-40);
			}
			//50-200GB（含），0.4元/GB
			else if(gprsNum>50 && gprsNum<=200){
				funFee = 2 * 3 + 2 * (20-2) + 1.5 * (40-20) + 1 * (50-40) + 0.4 * (gprsNum-50);
			}
			if(chaObject154485){
				var min154485 = chaObject154485.val();
				if(min154485 < funFee){
					var checkInfo = "功能费校验：功能费必须大于等于"+funFee;
					alert(checkInfo);
					return false;
				}
			}
			
		}
	}
	return true;
	
}

function setPriceOfferChaSpecValue(offerId,chaSpecCode,chaValue,priceOfferChaData)
{
	debugger;
	if("130010010201" == offerId || "130010010202" == offerId) {
		if("154485" == chaSpecCode || "144485" == chaSpecCode || "124485" == chaSpecCode){
			if("154485" == chaSpecCode){
				chaValue = chaValue*100;		//集团套餐功能费，元转为分
				priceOfferChaData.put("ATTR_VALUE",chaValue);
			}else if("144485" == chaSpecCode){
				var flag = $.verifylib.checkPInteger(chaValue);
				if(!flag || parseInt(chaValue,10)<100){
					var checkInfo = "分钟数校验：分钟数必须是大于0的整数，最低100分钟起步!";
					alert(checkInfo);
					return false;
				}
				chaValue = chaValue*60;		//集团套餐分钟数，分转为秒
				priceOfferChaData.put("ATTR_VALUE",chaValue);
				
			}else if("124485" == chaSpecCode){
				var dotLength = chaValue.indexOf("M");
				if(chaValue.indexOf("M") > 0){
					chaValue = chaValue.substring(0, chaValue.length-1);	//流量值
					if(parseFloat(chaValue,10)<500){
						var checkInfo = "流量校验：流量最低500M起步!!";
						alert(checkInfo);
						return false;
					}
					chaValue = chaValue*1024*1024;//集团套餐流量数，M转为字节
				}else if(chaValue.indexOf("G") > 0){
					chaValue = chaValue.substring(0, chaValue.length-1);	//流量值
					if(parseFloat(chaValue,10)<(500/1024)){
						var checkInfo = "流量校验：流量最低500M起步!!";
						alert(checkInfo);
						return false;
					}
					chaValue = chaValue*1024*1024*1024;//集团套餐流量数，G转为字节
				} else {
					var checkInfo = "流量校验：流量请输入大于0的数字并以单位M或G结尾，比如：700M,2G等!!";
					alert(checkInfo);
					return false;
				}
				priceOfferChaData.put("ATTR_VALUE",chaValue);
			}
		}
	}
	
		if("130010010203" == offerId||"130010010204" == offerId||"130010010208" == offerId||"130010010209" == offerId) {
		if("154485" == chaSpecCode || "144485" == chaSpecCode || "124485" == chaSpecCode){
			if("154485" == chaSpecCode){
				if(!check154485Fee(offerId)){
					return false;
				}
//				if(chaValue < min154485forall){
//					var checkInfo = "功能费校验：功能费必须大于等于"+min154485forall;
//					alert(checkInfo);
//					return false;
//				}
				chaValue = chaValue*100;		//集团套餐功能费，元转为分
				priceOfferChaData.put("ATTR_VALUE",chaValue);
			}else if("144485" == chaSpecCode){
				var flag = $.verifylib.checkPInteger(chaValue);
				if(!flag || parseInt(chaValue,10)<100){
					var checkInfo = "分钟数校验：分钟数必须是大于0的整数，最低100分钟起步!";
					alert(checkInfo);
					return false;
				}
				chaValue = chaValue*60;		//集团套餐分钟数，分转为秒
				priceOfferChaData.put("ATTR_VALUE",chaValue);
				
			}else if("124485" == chaSpecCode){
				var dotLength = chaValue.indexOf("M");
				if(chaValue.indexOf("M") > 0){
						var checkInfo = "流量校验：政企流量套餐单位应该以G结尾";
						alert(checkInfo);
						return false;
				}else if(chaValue.indexOf("G") > 0){
					chaValue = chaValue.substring(0, chaValue.length-1);	//流量值
					var flag = checkDecimalOnePoint(chaValue);
					if(chaValue < 0){
						var checkInfo = "流量校验：流量请输入大于0的数字";
						alert(checkInfo);
						return false;
					}else if(!flag){
						var checkInfo = "流量校验：流量请输入大于0的数字，若有小数，请保留一位小数输入";
						alert(checkInfo);
						return false;
					}
					chaValue = chaValue*1024*1024*1024;//集团套餐流量数，G转为字节
				} else {
					var checkInfo = "流量校验：流量请输入大于0的数字并以单位G结尾，比如：1G,2G等!!";
					alert(checkInfo);
					return false;
				}
				priceOfferChaData.put("ATTR_VALUE",chaValue);
			}
		}
	}
	
	if( "130010010205" == offerId||"130010010206" == offerId||"130010010207" == offerId){
		if("154485" == chaSpecCode){
			chaValue = chaValue*100;		//集团套餐功能费，元转为分
			priceOfferChaData.put("ATTR_VALUE",chaValue);
		}
	}
	//REQ201908130005  MDM手机管控资费套餐
	if( "110084071842" == offerId){
		if("254485" == chaSpecCode){
			chaValue = chaValue*100;		//集团套餐功能费，元转为分
			priceOfferChaData.put("ATTR_VALUE",chaValue);
		}
	}
	
	//START ADD BY XUZH5 REQ202004210014  关于行业应用卡资费折扣底线变更的需求   
	if("7050"==chaSpecCode){
		var flag = $.verifylib.checkPInteger(chaValue);
		if(!flag){
			alert("折扣率必须是整数");
			return false;
		}
		if(chaValue>100 ||chaValue<40){
			alert("折扣率只能在40到100之间!");
			return false;
			}
	}
	//END ADD BY XUZH5 REQ202004210014  关于行业应用卡资费折扣底线变更的需求  
	
	return true;
}