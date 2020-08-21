if(typeof(EnterpriseGroupOffers) == "undefined")
{
	window["EnterpriseGroupOffers"] = function(id){
		this.id = id;
	};
};

(function(){
	$.extend(EnterpriseGroupOffers.prototype, {
		initGroupOffers : function(obj){
			var self = this;
			var offerId = $("#prodDiv_OFFER_ID").val();
			var brand = $("#cond_EC_BRAND").val();
			var operType = $("#cond_OPER_TYPE").val();
			var isNeedFetchDis = $("#cond_IS_NEED_FETCH_DIS").val();
			var merchOperCode = $("#OPERTYPE").val();
			
			//BBOSS商品注销时不可操作资费
			if("BOSG" == brand && operType == "DstUs"){		        
				MessageBox.alert("该产品操作下不允许操作资费！");
		        return;				
			}
			
			//修改商品属性时，不可操作资费
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
			if(merchOperCode == "3"||merchOperCode == "4"){
				if ("BOSG" == brand && operType == "ChgUs"){
	                var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	                var oprCode = curOfferData.get("OPER_CODE");
	                
	                if (ACTION_PREDESTROY == oprCode || ACTION_PREDSTBACK == oprCode){
	                    MessageBox.alert("该产品操作下不允许操作资费！");
	                    return;
					}
				}
			}
			//BBOSS产品预受理无法订购资费
            if ("BOSG" == brand && operType == "CrtUs") {
                var curOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
                if ("10" == curOfferData.get("MERCHP_OPER_TYPE")) {
                    MessageBox.alert("该产品操作下不允许操作资费！");
                    return;
                }
            }
            //成员操作，定制情况需要加载已定制的包和元素		
            var ecUserId = $("#cond_EC_USER_ID").val();
			var ecOfferId = $("#cond_OFFER_ID").val();
			var param="&BRAND_CODE="+brand+"&OFFER_ID="+offerId+"&EC_USER_ID="+ecUserId+"&EC_OFFER_ID="+ecOfferId+"&OPER_TYPE="+operType+"&IS_NEED_FETCH_DIS="+isNeedFetchDis;
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "",param , "GroupOffersPart", function(groupOfferList){
				
				//按数据库配置加载商品组列表，设置选中状态
				for(var i = 0, sizeI = groupOfferList.length; i < sizeI; i++)
				{
					var group = groupOfferList.get(i);
					var groupSelectFlag = group.get("SELECT_FLAG");
					var offerList = group.get("GROUP_COM_REL_LIST"); //组内商品
					for(var j = 0, sizeJ = offerList.length; j < sizeJ; j++)
					{
						var offer = offerList.get(j);
						var offerId = offer.get("OFFER_ID");
						var offerSelectFlag = offer.get("SELECT_FLAG");
						if(groupSelectFlag == "0" && (offerSelectFlag == "0"))
						{//必选组的必选商品
							$("#grpOffer_"+offerId).attr("checked" , true);
							$("#grpOffer_"+offerId).attr("disabled", true);
							
						}
						else if(groupSelectFlag == "0" && (offerSelectFlag == "1"))
						{//必选组的可选(默认选中)元素设置为默认选中
							$("#grpOffer_"+offerId).attr("checked" , true);
						}
						else if(groupSelectFlag != "0" && (offerSelectFlag == "0"||offerSelectFlag == "1") && (operType == "CrtUs" || operType == "CrtMb"))
						{//非必选组的必选元素和可选(默认选中)都设置为默认选中
							$("#grpOffer_"+offerId).attr("checked" , true);
						}
					}
				}
				
				//将已选择的定价计划设置为选中状态
				self.setGroupOfferCheckedByOfferData();

				$.endPageLoading();
//				$("#batchOperPrice").attr("checked", false);
				forwardPopup(obj, self.id);
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		},
		setGroupOfferCheckedByOfferData : function(){
			
			var key = "SUBOFFERS";//处理子商品信息
			
			var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
			var subOfferDataset = offerData.get(key);
			if(typeof(subOfferDataset) == "undefined" || subOfferDataset.length == 0)
			{
				return ;
			}
			for(var i = 0; i < subOfferDataset.length; i++)
			{
				var subOfferData = subOfferDataset.get(i);
				var offerId = subOfferData.get("OFFER_ID");
				
				var lastDayThisMonth = getLastDayOfCurrentMonth();
				var expireDate = subOfferData.get("END_DATE");
				if(expireDate && expireDate.length > 10)
				{
					expireDate = expireDate.substring(0, 10);
				}
				
				if(subOfferData.get("OPER_CODE") != ACTION_DELETE && lastDayThisMonth != expireDate)
				{
					$("#grpOffer_"+offerId).attr("checked" , true);
					$("#grpOffer_"+offerId).attr("disabled", true);
				}
				
//				if(subOfferData.get("OPER_CODE") != ACTION_DELETE)
//				{
//					$("#grpOffer_"+offerId).attr("checked" , true);
//					$("#grpOffer_"+offerId).attr("disabled", true);
//				}
			}
		},
		submitGroupOffers : function(el){
			var self = this;
			if(!self.checkGroupOfferSelNum())
			{
				return ;
			}

			var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
			var subOffers = offerData.get("SUBOFFERS");
			if(typeof(subOffers) == "undefined" || subOffers.length == 0)
			{
				subOffers = new Wade.DatasetList();
				offerData.put("SUBOFFERS", subOffers);
			}
			
			var groupData = new Wade.DataMap(); //存放选择的组信息及组内商品
			var selOfferId = "";
			$("input[name=selGroupOffer]:checked").each(function(){
				var groupId = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("id");
				var offerCode = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("OFFER_CODE");
				selOfferId = selOfferId + "@" + $(this).attr("OFFER_ID") + "#" + groupId+ "#" + offerCode;
				if(!groupData.containsKey(groupId))
				{
					var maxNum = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("MAX_NUM");
					var minNum = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("MIN_NUM");
					var selectFlag = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("SELECT_FLAG");
					var limitType = $(this).closest("div [class*='c_list c_list-border c_list-line']").attr("LIMIT_TYPE");

					var groupInfo = new Wade.DataMap();
					groupInfo.put("OFFER_CODE", offerCode);//包归属产品
					groupInfo.put("GROUP_ID", groupId);
					groupInfo.put("GROUP_NAME", $("#"+groupId).parent().find(".text").html());
					groupInfo.put("MUST_SEL_OFFER", $("#MUST_SEL_OFFER_"+groupId).val());
					groupInfo.put("MAX_NUM", maxNum);
					groupInfo.put("MIN_NUM", minNum);
					groupInfo.put("LIMIT_TYPE", limitType);
					groupInfo.put("SELECT_FLAG", selectFlag);
					
					var selOfferList = new Wade.DatasetList();
					var selOffer = new Wade.DataMap();
					selOffer.put("OFFER_ID", $(this).attr("OFFER_ID"));
					selOffer.put("OFFER_TYPE", $(this).attr("OFFER_TYPE"));
					selOffer.put("SELECT_FLAG", $(this).attr("SELECT_FLAG"));
					selOfferList.add(selOffer);
					
					groupInfo.put("SEL_OFFER", selOfferList);
					
					groupData.put(groupId, groupInfo);
				}
				else
				{
					var groupInfo = groupData.get(groupId);
					var selOfferList = groupInfo.get("SEL_OFFER", new Wade.DatasetList());
					var selOffer = new Wade.DataMap();
					selOffer.put("OFFER_ID", $(this).attr("OFFER_ID"));
					selOffer.put("OFFER_TYPE", $(this).attr("OFFER_TYPE"));
					selOffer.put("SELECT_FLAG", $(this).attr("SELECT_FLAG"));
					selOfferList.add(selOffer);
					groupInfo.put("SEL_OFFER", selOfferList);
					
					groupData.put(groupId, groupInfo);
					
				}
				PageData.setData($(".e_SetSelGroupOfferPart"), groupData);
			});
			
			if(selOfferId != "")
			{
				var mainOfferId = $("#prodDiv_OFFER_ID").val(); //取当前设置的商品标识
				var operType = $("#cond_OPER_TYPE").val();
				var brand = $("#cond_EC_BRAND").val();
				var param = "&SEL_OFFER_IDS="+selOfferId+"&OPER_TYPE="+operType+"&BRAND="+brand+"&MAIN_OFFER_ID="+mainOfferId;

				var effectNow = $("#cond_EFFECT_NOW").val();//是否立即生效 只对订购有效，1立即、0下月初
				if(effectNow){
					param += "&EFFECT_NOW="+effectNow;
				}
				if(typeof(subOffers) != "undefined" && subOffers.length > 0)
				{
					param += "&USERELEMETS="+subOffers;
				}
				$.beginPageLoading("数据加载中......");
				$.ajax.submit("", "", param, "GroupOffersPart", function(selOfferDataset){
					$.endPageLoading();
					for(var i = 0, sizeI = selOfferDataset.length; i < sizeI; i++)
					{
						var selOfferData = selOfferDataset.get(i);
						var offer_id = selOfferData.get("OFFER_ID");
						var offer_type = selOfferData.get("OFFER_TYPE");
						var validDate = selOfferData.get("START_DATE");
						var expireDate = selOfferData.get("END_DATE");
						var operCode = ACTION_CREATE;
						
						var isChangeTime = $("#"+offer_id+"_flag").val();
						selOfferData.put("IS_DISCOUNT_TIME_CHANGE",isChangeTime);
						var addHtml = true;
						var addData = true;
						for(var j = 0, sizeJ = subOffers.length; j < sizeJ; j++)
						{
							var subOffer = subOffers.get(j);
							if(subOffer.get("OFFER_ID") == offer_id && subOffer.get("OPER_CODE") == "1")
							{//原来已经删除的实例
								subOffer.put("OPER_CODE", ACTION_EXITS);
								operCode = ACTION_EXITS;
								
								if("D" == offer_type)
								{
									validDate = subOffer.get("START_DATE");
									expireDate = subOffer.get("OLD_END_DATE");
									
									subOffer.put("END_DATE", expireDate);
									
									selOfferData.put("P_OFFER_INDEX", subOffer.get("P_OFFER_INDEX"));
									
									
								}
								//将删除列表中的资费商品删除
								$("#del_"+offer_id).remove();
								addHtml = true;
								addData = false;
								break;
							}
							else if(subOffer.get("OFFER_ID") == offer_id && subOffer.get("OPER_CODE") != "1")
							{
								addHtml = false;
								addData = false;
								break;
							}
						}
						if(addHtml)
						{
							if("D" == offer_type)
							{//资费  remark:此处判断是否添加html时，不需要比对P_OFFER_INDEX，因为商品组内同一个资费只有一个
								self.addPriceOfferHtml(selOfferData, validDate, expireDate, operCode);
								if($("#PriceOfferPart").css("display") == "none")
								{
									$("#PriceOfferPart").css("display", "");
								}
							}
							else
							{//服务
								self.addServiceOfferHtml(selOfferData);
								if($("#ServiceOfferListPart").css("display") == "none")
								{
									$("#ServiceOfferListPart").css("display", "");
									
								}
							}
						}
						if(addData)
						{
							subOffers.add(selOfferData);
						}
					}
					PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
					
					//手动刷新scroller组件
					OfferSetupScrollPart.refresh();
					
					//关闭新增组内商品区域
					backPopup(el);
				},
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    });
			}
			else
			{
				//关闭新增组内商品区域
				backPopup(el);
			}
		},
		addPriceOfferHtml : function(offerData, validDate, expireDate, operCode){
			var parent_offer_id =  $("#prodDiv_OFFER_ID");
			var offer_id = offerData.get("OFFER_ID");
			var param = "PARENT_OFFER_ID="+parent_offer_id;
			param = param + "&OFFER_ID=" + offer_id;
			param = param +"&ACTION=isNeedDiscountChange";
			var offer_code = offerData.get("OFFER_CODE");
			var offer_name = offerData.get("OFFER_NAME");
			var offer_type = offerData.get("OFFER_TYPE");
//			var validDate = offerData.get("START_DATE");
//			var expireDate = offerData.get("END_DATE");
			var pOfferIndex = offerData.get("P_OFFER_INDEX");
			var repeatOrder = offerData.get("REPEAT_ORDER");
			var renewTag = offerData.get("RENEW_TAG");  //续订标记
			var hasPriceCha = offerData.get("HAS_PRICE_CHA");
			var description = offerData.get("DESCRIPTION");
			var isChangeTime = offerData.get("IS_DISCOUNT_TIME_CHANGE");
			if(hasPriceCha == "true")
			{
				var groupOfferHtml = "<li class='link' offerIndex='"+pOfferIndex+"' id='li_"+offer_id+"_"+pOfferIndex+"'><div class='main'>"
				+ "<div class='title'>" 
				+ "【" + offer_code + "】<span class='e_blue' tip='"+description+"'>【详情】</span>" 
				+ "<input type='hidden' name='pp_PRICE_PLAN_ID' value='" + offer_id + "'/>"
				+ "<input type='hidden' name='pcha_" + offer_id + "_"+pOfferIndex+"' id='pcha_" + offer_id + "_"+pOfferIndex+"' value=''/>"
				+ "</div>"
				+ "<div class='content content-auto'>"
				+ offer_name + "<br/>"
				+ "生效时间：<span id='valid_"+offer_id+"_"+pOfferIndex+"' >" + validDate.substring(0, 10) +"</span>"
            
					if(operCode == ACTION_CREATE&&isChangeTime=='1')
					{
						groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='BEGIN'>[修改]</a>";
					}
					
					groupOfferHtml = groupOfferHtml +"<br/>";
					groupOfferHtml = groupOfferHtml + "失效时间：<span id='expire_"+offer_id+"_"+pOfferIndex+"' >" + expireDate.substring(0, 10) +"</span>";
					if(operCode == ACTION_CREATE)
					{
						groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='CANCEL'>[修改]</a>";
					}
					groupOfferHtml = groupOfferHtml + "</div>"
					+ "</div>"
					+ "<div class='side' tag='0' OFFER_ID='" + offer_id + "' ontap='priceOfferCha.initPriceOfferCha(this);'><span class='e_tag e_tag-red'>待设置</span>"
					+  "<input type='hidden'  name='expire_time' value='expire_"+offer_id+"_"+pOfferIndex+"'  desc='资费失效时间' /></div>"
					+ "<input type='hidden'  id='isRemindTime_"+offer_id+"_"+pOfferIndex+"' value='"+isChangeTime+"'  desc='是否需要提示' />"
					+ "<div class='more'></div>"
					+ "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
					
					if(repeatOrder == "true")
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
					else if(renewTag == "true")
					{
						groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' RENEW_TAG='true' ontap='repeatOrderPriceOffer(this)'></span></div>";
					}
					groupOfferHtml += "</li>";
					$("#priceOfferUL").append(groupOfferHtml);
				
			}
			else
			{
				var groupOfferHtml = "<li offerIndex='"+pOfferIndex+"' id='li_"+offer_id+"_"+pOfferIndex+"'><div class='main'>"
				+ "<div class='title'>" 
				+ "【" + offer_code + "】<span class='e_blue' tip='"+description+"'>【详情】</span>" 
				+ "<input type='hidden' name='pp_PRICE_PLAN_ID' value='" + offer_id + "'/>"
				+ "<input type='hidden' name='pcha_" + offer_id + "' id='pcha_" + offer_id + "' value=''/>"
				+ "</div>"
				+ "<div class='content content-auto'>"
				+ offer_name + "<br/>"
				+ "生效时间：<span id='valid_"+offer_id+"_"+pOfferIndex+"' >" + validDate.substring(0, 10) +"</span><br/>"
				
				if(operCode == ACTION_CREATE&&isChangeTime=='1')
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='BEGIN'>[修改]</a>";
				}
				
				groupOfferHtml = groupOfferHtml +"<br/>";
				
				groupOfferHtml =groupOfferHtml + "失效时间：<span id='expire_"+offer_id+"_"+pOfferIndex+"' >" + expireDate.substring(0, 10) +"</span>";
				if(operCode == ACTION_CREATE)
				{
					groupOfferHtml = groupOfferHtml + "<a ontap='showDateField(this);' name='CANCEL'>[修改]</a>";
				}
				groupOfferHtml = groupOfferHtml + "</div>"
				+ "</div>"
				+ "<div class='fn'><span class='e_ico-delete' ontap='deletePriceOffer(this);'></span></div>";
		
				if(repeatOrder == "true")
				{
					groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' ontap='repeatOrderPriceOffer(this)'></span></div>";
				}
				else if(renewTag == "true")
				{
					groupOfferHtml += "<div class='fn' name ='repeatPriceOffer'><span class='e_ico-add' OFFER_ID='"+offer_id+"' RENEW_TAG='true' ontap='repeatOrderPriceOffer(this)'></span></div>";
				}
				groupOfferHtml += "</li>";
				$("#priceOfferUL").append(groupOfferHtml);
			}
		},
		addServiceOfferHtml : function(offerData){
			var offer_id = offerData.get("OFFER_ID");
			var offer_name = offerData.get("OFFER_NAME");
			var serviceOfferHtml = "<li id='li_"+offer_id+"'><div class='main'>"
				+ "<div class='title'>"
				+ offer_name
				+ "</div>"
				+ "</div>"
				+ "<div class='fn'>"
				+ "<span class='fn e_ico-delete' ontap='deleteServiceOffer(this);'></span>"
				+ "</div>"
				+ "<span value='' id='SUBOFFER_DATA_"
				+ offer_id
				+ "' class='e_SubOfferPart' desc='子商品数据结构' style='display:none' name='SUBOFFER_DATA'>"
				+ offerData.toString()
				+ "</span>"
				+ "<input type='hidden' name='selServiceOffer' id='selServiceOffer_" + offer_id + "' value='" + offer_id +"'>"
				+ "<div class='c_line'></div></li>";
			$("#serviceOfferUL").append(serviceOfferHtml);
		},
		checkGroupOfferSelNum : function(){
			//校验商品组内选择商品数
			var flag = true;
			var errorInfo = "";
			$("div[id=GroupOffersPart] div[class*='c_list c_list-line']").each(function(){
				var maxNum = $(this).attr("MAX_NUM"); //组内选择商品最大数
				var minNum = $(this).attr("MIN_NUM"); //组内选择商品最小数
				var groupSelectFlag = $(this).attr("SELECT_FLAG"); //商品组必选标记
				var limitType = $(this).attr("LIMIT_TYPE"); //商品组必选标记
				var groupId = $(this).attr("id"); //商品组id
				var selNum = 0; //已选择的子商品个数
				var selCheckNum = 0; //已选择的需要校验的子商品个数，对应pm_group的limit_type
				var selOfferIds = ""; //已选择的子商品id
				var typeName="";
				if("D"==limitType){
					typeName="资费类";
				}else if("S"==limitType){
					typeName="服务类";
				}
				$("#"+groupId+" input[type=checkbox]").each(function(){
					if($(this).attr("checked"))
					{
						var offerType = $(this).attr("OFFER_TYPE");
						if(limitType && limitType==offerType){
							selCheckNum++;
						}else if(!limitType){
							selCheckNum++;
						}
						selNum++;
						if(selNum == 1)
						{
							selOfferIds = selOfferIds + $(this).attr("OFFER_ID");
						}
						else
						{
							selOfferIds = selOfferIds + "@" + $(this).attr("OFFER_ID");
						}
					}
				});
				var groupName = $("#"+groupId).parent().find(".text").html();
				if(groupSelectFlag == "0")
				{//商品组必选
					if(selCheckNum > maxNum && maxNum > 0)
					{//包内最大订购数小于0的不做校验
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
						flag = false;
						return ;
					}
					else if(selCheckNum < minNum && maxNum > 0)
					{
						errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
						flag = false;
						return ;
					}else if(selCheckNum == 0)
					{
						errorInfo = "商品组["+groupName+"]是必选的，请至少选择一个组内元素！";
						flag = false;
						return ;
					}
				}
				else
				{//商品组非必选
					var mustSelectOffers = $("#MUST_SEL_OFFER_"+groupId).val();
					if(mustSelectOffers)
					{//非必选组存在必选子商品
						if(selNum > 0)
						{
							var mustSelectOfferArr = mustSelectOffers.split("@");
							var selOfferIdArr = selOfferIds.split("@");
							for(var i = 0, sizeI = mustSelectOfferArr.length; i < sizeI; i++)
							{
								var selFlag = false; //组内必选商品是否被选中
								for(var j = 0, sizeJ = selOfferIdArr.length; j < sizeJ; j++)
								{
									if(mustSelectOfferArr[i] == selOfferIdArr[j])
									{
										selFlag = true;
										break;
									}
								}
								if(!selFlag)
								{
									var name = $("#grpOffer_"+mustSelectOfferArr[i]).attr("OFFER_NAME");
									errorInfo = "您已选择["+groupName+"]组内商品["+name+"]是必选商品，选择了该组的商品必须同时订购["+name+"]商品！";
									flag = false;
									return ;
								}
							}
							//非必选组必须要在选择组内商品的情况下才校验元素个数
							if(selCheckNum > maxNum && maxNum > 0)
							{//包内最大订购数小于0的不做校验
								errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
								flag = false;
								return ;
							}
							else if(selCheckNum < minNum && maxNum > 0)
							{
								errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
								flag = false;
								return ;
							}
						}
						
					}
					else
					{
						if(selCheckNum > maxNum && maxNum > 0)
						{//包内最大订购数小于0的不做校验
							errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能多于"+maxNum+"个！";
							flag = false;
							return ;
						}
						else if(selCheckNum > 0 && selCheckNum < minNum && maxNum > 0)
						{
							errorInfo = "您已选择["+groupName+"]组内商品，该组内"+typeName+"商品选择个数不能少于"+minNum+"个！";
							flag = false;
							return ;
						}
					}
				}
			});
			if(!flag)
			{
				MessageBox.alert("提示信息", errorInfo);
				return false;
			}
			return true;
		},
		showOffers : function(id){
			var groupId = id.split("_")[1];
			var reduceId = "unfold_" + groupId;
			$("#"+id).css("display", "none");
			$("#"+reduceId).css("display", "");
			$("#"+groupId).css("display", "");
		},
		hideOffers : function(id){
			var groupId = id.split("_")[1];
			var addId = "fold_" + groupId;
			$("#"+id).css("display", "none");
			$("#"+addId).css("display", "");
			$("#"+groupId).css("display", "none");
		}
	});
})();


//校验选择的定价计划不能超过定价计划组的最大订购数
function checkPricePlanGroupSelNum()
{
	var flag = true;
	var groupName = "";
	var maxSelNum = 0;
	var minSelNum = 0;
	$("div[id=PricePlanListPart] div[class*='c_list c_list-border c_list-line']").each(function(){
		var maxNum = $(this).attr("MAX_CARDINALITY");
		var minNum = $(this).attr("MIN_CARDINALITY");
		var groupId = $(this).attr("id");

		var selNum = 0;
		$("#"+groupId+" input[type=checkbox]").each(function(){
			if($(this).attr("checked"))
			{
				selNum++;
			}
		});
		if(selNum > maxNum && maxNum > 0)
		{//包内最大订购数小于0的不做校验
			maxSelNum = maxNum;
			groupName = $("#"+groupId).parent().find(".text").html();
			flag = false;
			return;
		}
	});
	if(!flag)
	{
		MessageBox.alert("提示信息", groupName+"组内选择的定价计划个数不能大于"+maxSelNum);
		return false;
	}
	return true;
}

//批量全选或反选定价计划
function batchOperPricePlan(el)
{
	$("input[name='selPricePlan']").each(function(){
		if(!$(this).attr("disabled"))
		{
			$(this).attr("checked", $(el).attr("checked"));
		}
	});
}

//function showPricePlans(id)
//{
//	var groupId = id.split("_")[1];
//	var reduceId = "unfold_" + groupId;
//	$("#"+id).css("display", "none");
//	$("#"+reduceId).css("display", "");
//	$("#"+groupId).css("display", "");
//}
//
//function hidePricePlans(id)
//{
//	var groupId = id.split("_")[1];
//	var addId = "fold_" + groupId;
//	$("#"+id).css("display", "none");
//	$("#"+addId).css("display", "");
//	$("#"+groupId).css("display", "none");
//}
