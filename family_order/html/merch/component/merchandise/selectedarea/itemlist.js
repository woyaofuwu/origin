!function() {
	if (typeof (ItemList) == "undefined") {
		ItemList = function(id) {
			return new ItemList.fn.init(id);
		};
		ItemList.foldTags = function(obj) {
			$(obj).parent().prev().toggleClass('option-fold');
			if ($(obj).data("fold") == "true") {
				$(obj).children().html("更多");
				$(obj).data("fold", "false");
			} else {
				$(obj).children().html("收起");
				$(obj).data("fold", "true");
			}
		};
		ItemList.fn = ItemList.prototype = {
			HANDLER : "com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ItemHandler",
			init : function(id) {
				this.id = id;
				this.mainProductId = null;
				this.eparchyCode = null;
				this.labels = null;
				this.offers = null;
				this.addTipCount = 0;
				var that = this;
				this.pagingComponent = new PagingComponent(this.find("#pagingComponent")[0], function(e) {
					that.drawOffers(e);
				});
				this.bindEvent();
				return this;
			},
			bindEvent : function() {
				var that = this;
				this.find("#searchInput").bind("input propertychange", function() {
					that.searchItem(this);
				});
				$(document.body).bind("click", function(n) {
					if (n && n.target) {
						var a = n.target;
						var parentId = a.parentNode.id;
						if (a.id != "searchInput" && a.id != "searchBtn" && parentId != "searchBtn") {
							var n = $(a).parents("#searchResult_float");
							if (!(n && n.length > 0)) {
								that.hideSearchItemListPart();
							}
						}
					}
				});
				this.find("#itemListArea").bind("click", function(n) {
					var obj = n.target;
					var cls = obj.className;
					if (cls == "e_ico-unfold" || cls == "e_ico-fold") {
						that.toggleDesc(obj);
					} else if (obj.nodeName == "BUTTON") {
						that.order(obj);
					}
					return false;
				});
				this.find("#labels").bind("click", function(n) {
					var obj = n.target;
					if (obj.nodeName == "LI") {
						that.switchLabel(obj);
					}
				});
				this.find("#searchResult").bind("click", function(n) {
					var obj = $(n.target).parent("li");
					var offerCode = obj.attr("offerCode");
					var offerType = obj.attr("offerType");
					that.choiceElement(offerCode, offerType);
				});
			},
			renderComponent : function(productId, routeEparchyCode, param) {
//				var productId="10007614";
				if (this.mainProductId == productId) {
					showPopup(this.id + "Pop", "popup-ItemList");
					return false;
				}
				if (productId) {
					this.eparchyCode = routeEparchyCode;
					param += "&PRODUCT_ID=" + productId + "&EPARCHY_CODE=" + routeEparchyCode;
					if (typeof (getSpecialAreaParam) == "function") {
						param += getSpecialAreaParam();
					}
					$.beginLoading("加载中。。。。。");
					var that = this;
					hhSubmit(null, this.HANDLER, "queryProductChangeListTagsAndoffers", param, function(data) {
						// that.getBindIdObject().getBindIdObject("item").mainProductId
						// = productId;
						that.mainProductId = productId;
						that.afterRender(data);
					}, function(error_code, error_info) {
						alert(error_info);
						$.endLoading();
					});
				} else {
					$.ajax.submit(null, null, param, this.id, null, null);
				}
			},
			afterRender : function(data) {
				$.endLoading();
				if (!data || data.length == 0) {
					alert("无权限办理，商品下线或配置错误!");
					return false;
				}
				this.labels = data.get("LABELS");
				this.drawLabels();
				this.offers = data.get("OFFERS");
				showPopup(this.id + "Pop", "popup-ItemList");
				this.switchLabel(this.find("li[labelId=-1][labelType=-1]")[0]);
			},
			drawLabels : function() {
				var drawArea = this.find("#labels");
				drawArea.empty();
				var html = "";
				var labels = this.labels;
				for (var i = 0; i < labels.length; i++) {
					var label = labels.get(i);
					var labelId = label.get("LABEL_ID");
					var labelType = label.get("LABEL_TYPE");
					var selectFlag = label.get("SELECT_FLAG");
					var labelName = label.get("LABEL_NAME");
					html += "<li labelId='" + labelId + "' labelType='" + labelType + "' selectFlag='" + selectFlag + "' >";
					html += labelName;
					html += "</li>";
				}
				drawArea.append(html);
				var btn = this.find("#labelsArea").find("button");
				btn.data("fold", "false");
				btn.children("span").html("更多");
			},

			switchLabel : function(obj) {
				if ($(obj).attr("class") == "on") {
					return;
				}
				this.switchLabelClass(obj);
				var afterAction = this.find("#SWITCH_LABEL_ACTION").val();
				if (afterAction && afterAction != "") {
					eval(afterAction + "(obj)");
				} else {
					this.renderItemList(obj);
				}
			},
			switchLabelClass : function(obj) {
				$(obj).siblings().attr("class", "");
				$(obj).attr("class", "on");
			},
			toggleDesc : function(obj) {
				var o = $(obj);
				o.parents("li").find("div.content.content-auto").toggle();
				o.toggleClass("e_ico-unfold e_ico-fold");
			},
			renderItemList : function(obj) {
				var afterAction = this.find("#SWITCH_LABEL_AFTER_ACTION").val();
				if (afterAction && afterAction != "") {
					eval(afterAction + "(obj)");
				} else {
					var labelId = $(obj).attr("labelId");
					var labelType = $(obj).attr("labelType");// -1:全量，1:group，2:catalog，3：tag
					var offers = this.getOffersByLabel(labelId, labelType);
					this.pagingComponent.setDataSource(offers);
				}
			},
			getOffersByLabel : function(labelId, labelType) {
				if (labelType == "-1") {
					return this.offers;
				} else {
					var result = $.DatasetList();
					var bindObj = this.getBindIdObject();
					for (var i = 0; i < this.offers.length; i++) {
						var offer = this.offers.get(i);
						if (bindObj) {
							var ele = bindObj.getElement(offer.get("OFFER_CODE"), offer.get("OFFER_TYPE"));
							if (ele != null && (ele.get("MODIFY_TAG") == "1" || ele.get("MODIFY_TAG") == "2" || ele.get("MODIFY_TAG") == "exist"))
								continue;
						}
					/*	if (labelType == "1" && offer.get("GROUP_ID") == labelId || labelType == "2" && offer.get("CATEGORY_ID") == labelId
								|| labelType == "3" && offer.get("TAG_ID") == labelId) {
							result.add(offer);
						}*/
						/*海南沿用老接口数据不支持修改 guohuan*/
						if (offer.get("LABEL_ID") == labelId){
							result.add(offer);
						}
					}
				}
				return result;
			},
			drawOffers : function(offers) {
				var drawArea = this.find("#itemListArea");
				drawArea.empty("");
				var html = '';

				for (var i = 0; i < offers.length; i++) {
					var offer = offers.get(i);
					var offerCode = offer.get("OFFER_CODE");
					var offerName = offer.get("OFFER_NAME");
					var offerType = offer.get("OFFER_TYPE");
					var offerDesc = offer.get("DESCRIPTION");
					var orderMode = offer.get("ORDER_MODE");
					var ordered = offer.get("ORDERED");
					var tagValue = offer.get("TAG_VALUE");
					
					var className=null;
					if(orderMode!="R"&&orderMode!="C"&&selectedElements.checkIsExist(offerCode,offerType)){
	                    className="e_dis";
	                }

					html += '<li title="'+offerDesc+'"'+(className!=null?(' class="'+className+'"'):'')+' id="PELI_'+offerType+"_"+offerCode+'">';
					html += '<div class="main">';
					html += '<div class="title">';
					html += '[' + offerCode + ']' + offerName;
					html += '</div>';
					html += '<div class="content content-auto" style="display:none">';
					html += offerDesc;
					html += '</div>';
					html += '</div>';

					html += '<div class="side" >';
					if (tagValue == "HOT") {
						html += '<span class="e_tag e_tag-green">hot</span>';
					} else if (tagValue == "NEW") {
						html += '<span class="e_tag e_tag-orange">new</span>';
					}
					html += '</div>';

					html += '<div class="fn"><span class="e_ico-unfold"></span></div>';
					html += '<div class="side">';
					if (ordered == true) {
						html += '<button class="' + (orderMode == 'R' ? 'e_button-orange' : 'e_button-navy') + ' e_button-r" offertype="' + offerType
								+ '" offercode="' + offerCode + '" orderMode="' + orderMode + '" >退订</button>';
					} else {
						var disabled = this.checkReorder(offer);
						html += '<button class="' + (orderMode == 'R' ? 'e_button-orange' : (!disabled ? 'e_button-navy' : 'e_button-blue'))
								+ ' e_button-r" offertype="' + offerType + '" offercode="' + offerCode + '" orderMode="' + orderMode + '" '
								+ (!disabled ? 'disable=true' : '') + ' >订购</button>';

					}
					html += '</div>';
					html += '</li>\r\n';
				}
				drawArea.append(html);
			},
			checkReorder : function(offer) {
				var orderMode = offer.get("ORDER_MODE");
				if (orderMode == "R") {
					var offerCode = offer.get("OFFER_CODE");
					var offerType = offer.get("OFFER_TYPE");
					var reOrderSize = offer.get("REORDER_SIZE");
					var bindObj = this.getBindIdObject();
					if (bindObj) {
						var count = 0;
						var eles = bindObj.selectedEls;
						for (var i = 0; i < eles.length; i++) {
							var ele = eles.get(i);
							if (offerCode == ele.get("ELEMENT_ID") && offerType == ele.get("ELEMENT_TYPE_CODE")) {
								count++;
							}
						}
						if (count >= reOrderSize) {
							return false;
						}
					}
				}
				return true;
			},
			order : function(obj) {
				var orderMode = $(obj).attr("orderMode");
				var offerCode = $(obj).attr("offerCode");
				var offerType = $(obj).attr("offerType");
				var item = this.getItem(offerCode, offerType);
				var ordered = item.get("ORDERED");
				if (orderMode == "R") {
					this.showAddTip(obj, "+1");
				} else {
					if (ordered == true || ordered == false) {
						var bindObj = this.getBindIdObject();
						if (bindObj)
							bindObj.toggleElement(offerCode, offerType);
						return;
					}
				}
				this.addElement(offerCode, offerType);
			},
			afterOrder : function(offerCode, offerType) {
				var item = this.getItem(offerCode, offerType);
				if (item != null) {
					var obj = this.find("button[offerCode=" + offerCode + "][offerType=" + offerType + "]");
					var orderMode = $(obj).attr("orderMode");
					if (orderMode != "R") {
						var ordered = item.get("ORDERED");
						if (!ordered) {
							$(obj).html(" 退订");
							item.put("ORDERED", true);
						} else {
							$(obj).html("订购");
							item.put("ORDERED", false);
						}
						$(obj).toggleClass("e_button-blue e_button-navy");
					} else {
						if (!this.checkReorder(item)) {
							$(obj).attr("disabled", true);
							$(obj).toggleClass("e_button-navy e_button-orange");
						}
					}
				}
			},
			addElement : function(offerCode, offerType) {
				var tempEl = this.getItem(offerCode, offerType);
				var bindObj = this.getBindIdObject();
				if (bindObj) {
					if (tempEl.get("ORDER_MODE") != "R") {
						var selectElem = bindObj.getElement(offerCode, offerType);
						if (tempEl.get("ORDER_MODE") == "C") { // 如果是可续订的
							if (selectElem != null) {
								if (selectElem != null && dateUtils.toDate(selectElem.get("END_DATE")) > dateUtils.toDate(dateUtils.lastDayOfMonth())) {// 如果结束时间大于本月最后一天不能订
									alert("您所选择的元素" + tempEl.get("OFFER_NAME") + "结束时间大于本月最后一天 不能续订！");
									return false;
								}
							}
						} else {
							if (selectElem != null) {
								alert("您所选择的元素" + tempEl.get("OFFER_NAME") + "已经存在于已选区，不能重复添加");
								return false;
							}
						}
					} else {
						if (!this.checkReorder(tempEl)) {
							alert("您所选择的元素" + tempEl.get("OFFER_NAME") + "可重复订购次数已达上限，不能继续添加");
							return false;
						}
					}
				}
				var elementIds = $.DatasetList();
				var selected = $.DataMap();
				selected.put("PRODUCT_ID", this.mainProductId);
				selected.put("PACKAGE_ID", tempEl.get("GROUP_ID", "-1"));
				selected.put("ELEMENT_ID", offerCode);
				selected.put("ELEMENT_TYPE_CODE", offerType);
				selected.put("MODIFY_TAG", "0");
				elementIds.add(selected);

				if (typeof (_confirmPackageOperation) == "function") {// 自定义效验,懒得加属性了
					var result = _confirmPackageOperation(elementIds);
					if (result) {
						return;
					}
				}
				if (bindObj)
					bindObj.addElements(elementIds);
			},

			showAddTip : function(o, str) {
				var tipLeft = $(o).offset().left + $(o).width() * 0.25;
				var tipTop = $(o).offset().top - $(o).height() / 1.2;
				var tipCount = this.addTipCount;
				var tempHTML = '	<div class="c_floattip c_floattip-green e_size-xs" style="z-index:2000; left:' + tipLeft + 'px; top:' + tipTop
						+ 'px;" id="floatTip' + tipCount + '">';
				tempHTML += '		<div class="content"><strong>' + str + '</strong></div>';
				tempHTML += '	</div>';
				$("#" + this.id).append(tempHTML);
				var tip = this.find("#floatTip" + tipCount);
				this.addTipCount++
				var initOpacity = 1;
				var x = setInterval(function() {
					tipTop -= 3, initOpacity -= 0.1;
					tip.css({
						"top" : tipTop,
						"opacity" : initOpacity,
						"filter" : "alpha(opacity=" + (initOpacity * 100) + ")"
					});
					if (initOpacity <= 0) {
						clearInterval(x);
						tip.remove();
					}
				}, 60);
			},

			searchItem : function(obj) {
				var value = obj ? $(obj).val() : this.find("#searchInput").val();
				if (value.length < 2) {
					this.hideSearchItemListPart();
					return false;
				}
				var search = new Search(value);
				var searchResult = new Array();
				for (var i = 0; i < this.offers.length; i++) {
					if (searchResult.length >= 10) {
						break;
					}
					var offer = this.offers.get(i);
					var offerCode = offer.get("OFFER_CODE");
					var offerName = offer.get("OFFER_NAME");
					if (search.test(offerCode) || search.test(offerName) || search.testAcronym(offerName)) {
						searchResult[searchResult.length] = i;
					}
				}
				if (searchResult.length > 0) {
					this.createSearchItemList(searchResult);
					this.showSearchItemListPart();
				}
			},
			createSearchItemList : function(searchResult) {
				var drawArea = this.find("#searchResult");
				drawArea.empty();
				if (searchResult.length > 0) {
					var html = [];
					for (var i = 0; i < searchResult.length; i++) {
						var position = searchResult[i];
						var offer = this.offers.get(position);
						var offerCode = offer.get("OFFER_CODE");
						var offerName = offer.get("OFFER_NAME");
						var offerType = offer.get("OFFER_TYPE");
						html.push('<li class="link" offerCode="' + offerCode + '" offerType="' + offerType + '" >');
						html.push('<div class="main  e_size-m" title="' + offer.get("DESCRIPTION") + '" >');
						html.push('[' + offerCode + ']' + offerName);
						html.push('</div>');
						html.push('</li>');
					}
					drawArea.append(html.join("\n"));
				}
			},
			showSearchItemListPart : function() {
				var searchFloat = this.find("#searchResult_float");
				if (searchFloat.find("li").length == 0 || searchFloat.hasClass("c_float-show")) {
					return false;
				}

				var obj = this.find(".e_mix,.e_mix-search");
				var offset = obj.offset();
				var left = offset.left;
				var top = offset.top + obj.height();
				searchFloat.css({
					"left" : left,
					"top" : top
				});
				searchFloat.addClass("c_float-show");
			},
			hideSearchItemListPart : function() {
				this.find("#searchResult_float").removeClass("c_float-show");
			},
			choiceElement : function(offerCode, offerType) {
				this.addElement(offerCode, offerType);
				this.hideSearchItemListPart();
			},

			getBindIdObject : function() {
				if (this.bindObj) {
					return this.bindObj;
				}
				var bindId = this.find("#bindedComponent").val();
				this.bindObj = window[bindId];
				return this.bindObj;
			},
			getGroups : function() {
				var result = $.DatasetList();
				if (this.labels) {
					for (var i = 0; i < this.labels.length; i++) {
						var label = this.labels.get(i);
						if (label.get("LABEL_TYPE") == "1") {
							result.add(label);
						}
					}
				}
				return result;
			},
			getItem : function(offerCode, offerType) {
				if (this.offers) {
					for (var i = 0; i < this.offers.length; i++) {
						var offer = this.offers.get(i);
						if (offer.get("OFFER_CODE") == offerCode && offer.get("OFFER_TYPE") == offerType) {
							return offer;
						}
					}
				}
				return null;
			},
			find : function(name) {
				return $("#" + this.id).find(name);
			}
		};
		ItemList.fn.init.prototype = ItemList.prototype;
	}
}();
