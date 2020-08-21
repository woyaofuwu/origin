!function() {
	if (typeof (ProductSelect) == "undefined") {
		ProductSelect = function(id, n) {
			return new ProductSelect.fn.init(id, n);
		};
		ProductSelect.fn = ProductSelect.prototype = {
			HANDLER : "com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ItemHandler",
			init : function(id, n) {
				this.id = id;
				this.currentProduct = null;
				this.monitor = null;
				this.eparchyCode = null;
				this.products = null;
				this.serviceName = null;
				var that = this;
				if ($.isObject(n))
					$.extend(this, n)
				this.pagingComponent = new PagingComponent(this.find("#pagingComponent")[0], function(e) {
					that.drawProduct(e);
				});
				this.bindEvent();
				return this;
			},

			bindEvent : function() {
				var that = this;
				this.find("#searchInput").bind("input propertychange", function() {
					that.searchProduct(this);
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
				this.find("#productArea").bind("click", function(n) {
					var obj = n.target;
					var cls = obj.className;
					if (cls == "e_ico-unfold" || cls == "e_ico-fold") {
						that.toggleProductDesc(obj);
					} else if (obj.nodeName == "DIV" && (cls == "title" || cls == "main")) {
						var li = $(obj).parents("li");
						that.selectProduct(li.attr("offerCode"), li.attr("offerName"));
					} else if (obj.nodeName == "LI") {
						that.selectProduct(obj.getAttribute("offerCode"), obj.getAttribute("offerName"));
					}
					return false;
				});
				this.find("#labels").bind("click", function(n) {
					var obj = n.target;
					if (obj.nodeName == "LI") {
						that.filter(obj);
					}
				});
				this.find("#searchResult").bind("click", function(n) {
					var obj = $(n.target).parent("li");
					var offerCode = obj.attr("offerCode");
					var offerName = obj.attr("offerName");
					that.choiceProduct(offerCode, offerName);
				});
			},

			popupProductSelect : function(productTypeCode, eparchyCode, userProductId, monitor, assignProductIds) {
				var param = "";
				param += "&USER_PRODUCT_ID=" + userProductId;
				param += "&PRODUCT_TYPE_CODE=" + productTypeCode;
				param += "&EPARCHY_CODE=" + eparchyCode;
				if (assignProductIds) {
					param += "&ASSIGN_PRODUCTIDS=" + assignProductIds + "&PARENT_ID=0000";
				}
				if (this.serviceName) {
					param += "&SERVICE_NAME=" + this.serviceName;
				}
				if (typeof (getOtherParam) == "function") {
					var otherParam = getOtherParam("ProductSelect");
					param += otherParam;
				}
				if (typeof (getSpecialAreaParam) == "function") {
					param += getSpecialAreaParam();
				}
				if (this.monitor == monitor) {
					showPopup(this.id + "Pop", "popup-mainProduct");
				} else {
					this.eparchyCode = eparchyCode;
					var that = this;
					$.beginPageLoading("产品加载中。。。");
					hhSubmit(null, this.HANDLER, "queryCompOfferLabels", param, function(data) {
						$.endPageLoading();
						that.afterRender(data);
						if (monitor) {
							that.monitor = monitor;
						}
					}, function(error_code, error_info) {
						$.endPageLoading();
						alert(error_info);
					});
				}
			},

			afterRender : function(data) {
				$.endLoading();
				if (!data || data.length == 0) {
					alert("无权限办理，商品下线或配置错误!");
					return false;
				}
				this.drawLabels(data.get("LABELS"));
				this.products = data.get("OFFERS");
				showPopup(this.id + "Pop", "popup-mainProduct");
				this.filter(this.find("li[labelId=-1][labelType=-1]")[0]);
			},

			drawLabels : function(labels) {
				var drawArea = this.find("#labels");
				drawArea.empty();
				var html = "";
				for (var i = 0; i < labels.length; i++) {
					var label = labels.get(i);
					var labelId = label.get("LABEL_ID");
					var labelType = label.get("LABEL_TYPE");
					var selectFlag = label.get("SELECT_FLAG");
					var labelName = label.get("LABEL_NAME");
					var style = i > 3 ? "display:none" : "";
					html += "<li labelId='" + labelId + "' labelType='" + labelType + "' style='" + style + "' >";
					html += labelName;
					html += "</li>";
				}
				drawArea.append(html);
			},

			toggleProductDesc : function(obj) {
				var o = $(obj);
				o.parent().parent().find("div.content.content-auto").toggle();
				o.toggleClass("e_ico-unfold e_ico-fold");
			},

			foldTags : function(obj) {
				if ($(obj).data("fold") == "true") {
					$(obj).parent().prev().find("li:gt(3)").css("display", "none");
					$(obj).children().html("更多");
					$(obj).data("fold", "false");
				} else {
					$(obj).parent().prev().find("li").show();
					$(obj).children().html("收起");
					$(obj).data("fold", "true");
				}
			},

			filter : function(obj) {
				this.switchTagClass(obj);
				var labelId = $(obj).attr("labelId");
				var labelType = $(obj).attr("labelType");
				var products = this.getProducts(labelId, labelType);
				this.pagingComponent.setDataSource(products);
			},

			getProducts : function(labelId, labelType) {
				if (labelId == -1) {
					return this.products;
				} else {
					var rst = new $.DatasetList();
					for (var i = 0; i < this.products.length; i++) {
						var product = this.products.get(i);
						if (labelType == "2") {
							var catalogId = product.get("CATALOG_ID");
							if (catalogId == labelId) {
								rst.add(product);
							}
						} else if (labelType == "3") {
							var tagId = product.get("TAG_ID");
							if (tagId == labelId) {
								rst.add(product);
							}
						}
					}
					return rst;
				}
			},

			switchTagClass : function(obj) {
				var parent = $(obj).parent();
				var children = parent.children();
				var labelId = $(obj).attr("labelId");
				for (var i = 0; i < children.length; i++) {
					var child = $(children[i]);
					var tempLabelId = child.attr("labelId");
					if (tempLabelId == labelId) {
						child.attr("class", "on");
					} else {
						child.attr("class", "");
					}
				}
			},

			drawProduct : function(products) {
				var drawArea = this.find("#productArea");
				drawArea.empty();
				var html = [];
				for (var i = 0; i < products.length; i++) {
					var product = products.get(i);
					var productId = product.get("OFFER_CODE");
					var productName = product.get("OFFER_NAME");
					var brandCode = product.get("BRAND_CODE");
					var brandName = product.get("BRAND_NAME");
					var productDesc = product.get("DESCRIPTION");
					var productPrice = product.get("PRICE", "");
					var tagValue = product.get("TAG_VALUE");

					html.push('<li class="link" offerCode="' + productId + '" offerName="' + productName + '" >');
					html.push('<div class="main">');
					html.push('<div class="title">' + '[' + productId + ']' + productName + '</div>');
					html.push('<div class="content content-auto" style="display:none;">' + productDesc + '</div>');
					html.push('</div>');

					html.push('<div class="side">' + productPrice);
					if (tagValue == "HOT") {
						html.push('<span class="e_tag e_tag-green">hot</span>');
					} else if (tagValue == "NEW") {
						html.push('<span class="e_tag e_tag-orange">new</span>');
					}
					html.push('</div>');

					html.push('<div class="fn"><span class="e_ico-unfold"></span></div>');
					// html.push('<div class="fn"><span
					// class="e_ico-cart"></span></div>');
					html.push("</li>");
				}
				var ss = html.join("");
				drawArea.append(ss);
			},

			selectProduct : function(productId, productName) {
				this.find("#pop-itemList div.back").html(productName);
				this.currentProduct = this.getProductById(productId);

				if (!this.showGroup) {
					this.confirmAction();
				} else if (this.currentProduct && this.currentProduct.containsKey("GROUP")) {
					if (this.currentProduct.get("GROUP").length > 0) {
						this.afterRenderGroupItemList(this.currentProduct.get("GROUP"));
						forwardPopup(this.id + "Pop", "pop-itemList");
					} else {
						this.confirmAction();
					}
				} else {
					var that = this;
					var param = "&RENDER_PRODUCT_ITEM_LIST=true&PRODUCT_ID=" + productId + "&EPARCHY_CODE=" + this.eparchyCode;
					if (typeof (getOtherParam) == "function") {
						var otherParam = getOtherParam("selectProduct");
						param += otherParam;
					}
					if (typeof (getSpecialAreaParam) == "function") {
						param += getSpecialAreaParam();
					}
					hhSubmit(null, this.HANDLER, "queryCompGroupOffers", param, function(data) {
						$.endLoading();
						that.currentProduct.put("GROUP", data);
						if (data && data.length > 0) {
							that.afterRenderGroupItemList(data);
							forwardPopup(that.id + "Pop", "pop-itemList");
						} else {
							that.confirmAction();
						}
					}, function(error_code, error_info) {
						$.endPageLoading();
						alert(error_info);
					});
				}
			},

			getProductById : function(productId) {
				var productData = null;
				for (var i = 0; i < this.products.length; i++) {
					var product = this.products.get(i);
					var offerCode = product.get("OFFER_CODE");
					if (productId == offerCode) {
						productData = product;
						break;
					}
				}
				return productData;
			},

			afterRenderGroupItemList : function(data) {
				var drawArea = this.find("#productItemListArea");
				drawArea.empty();
				var html = [];
				var groupList = data;
				for (var i = 0; i < groupList.length; i++) {
					var group = groupList.get(i);
					var selectFlag = group.get("SELECT_FLAG");
					html.push('<div class="c_title">');
					html.push('<div class="text">');
					html.push(group.get("GROUP_NAME"));
					if (selectFlag == "0") {
						html.push('<span class="e_red">（必选）</span>');
					} else {
						html.push('<span>（可选）</span>');
					}
					html.push('</div></div>');
					html.push(this.getGroupItemListHtml(group.get("GROUP_COM_REL")).join(""));
				}
				drawArea.append(html.join(""));
			},

			getGroupItemListHtml : function(offerList) {
				var html = [];
				if (offerList && offerList.length > 0) {
					html.push('<div class="c_list c_list-s c_list-line c_list-border c_list-col-2"><ul>');
					for (var i = 0; i < offerList.length; i++) {
						var offer = offerList.get(i);
						var offerCode = offer.get("OFFER_CODE");
						var offerType = offer.get("OFFER_TYPE");
						var groupId = offer.get("GROUP_ID");
						var productDesc = offer.get("DESCRIPTION");

						var selectFlag = offer.get("SELECT_FLAG", "0");
						html.push('<li class="link" title="' + productDesc + '">');
						html.push('<div class="main">');
						html.push(offer.get("OFFER_NAME"));
						html.push('</div>');
						html.push('<div class="fn"><input type="checkbox" ' + (selectFlag == '2' ? '' : 'checked="true"') + ' '
								+ (selectFlag == '0' ? 'disabled="true"' : '') + ' offerCode="' + offerCode + '"  offerType="' + offerType
								+ '" groupId="' + groupId + '"></div>')
						html.push('</li>');
					}
					html.push('</ul></div>');
				}
				return html;
			},

			confirmAction : function(obj) {
				var selectElements = null;
				if (obj) {
					selectElements = this.getSelectGroupItemList();
					if (!this.checkGroupLimit(selectElements)) {
						return;
					}
				}
				var afterAction = this.find("#AFTER_ACTION").val();
				if (afterAction != "" && afterAction != "undefined") {
					var product = this.currentProduct;
					var productId = product.get("OFFER_CODE");
					var productName = product.get("OFFER_NAME");
					var brandCode = product.get("BRAND_CODE");
					var brandName = product.get("BRAND_NAME");
					var productDesc = product.get("DESCRIPTION");
					eval(afterAction);
				}
				hidePopup(this.id + "Pop");
			},

			getSelectGroupItemList : function() {
				var selectElements = $.DatasetList();
				var checkBoxs = this.find("#productItemListArea").find(":checked");
				if (checkBoxs) {
					for (var i = 0; i < checkBoxs.length; i++) {
						var checkBox = $(checkBoxs[i]);
						var offerCode = checkBox.attr("offerCode");
						var offerType = checkBox.attr("offerType");
						var groupId = checkBox.attr("groupId");
						var item = $.DataMap();
						item.put("ELEMENT_ID", offerCode);
						item.put("ELEMENT_TYPE_CODE", offerType);
						item.put("PACKAGE_ID", groupId);
						item.put("PRODUCT_ID", this.currentProduct.get("OFFER_CODE"));
						selectElements.add(item);
					}
				}
				return selectElements;
			},

			checkGroupLimit : function(selectElements) {
				var result = $.DatasetList();
				var groups = this.currentProduct.get("GROUP");
				var err = "";
				for (var i = 0; i < groups.length; i++) {
					var group = groups.get(i);
					var err1 = this.checkForceGroup(group, selectElements);
					err1 != "" ? err += err1 + "\n" : "";
					var err2 = this.checkMinMax(group, selectElements);
					err2 != "" ? err += err2 + "\n" : "";
				}
				if (err != "") {
					MessageBox.alert("提示信息", err);
					return false;
				}
				return true;
			},
			checkForceGroup : function(group, selectElements) {
				var groupId = group.get("GROUP_ID");
				var selectFlag = group.get("SELECT_FLAG");
				var groupName = group.get("GROUP_NAME");

				if (selectFlag == "0") {
					var flag = false;
					for (var k = 0; k < selectElements.length; k++) {
						var ele = selectElements.get(k);
						if (groupId == ele.get("PACKAGE_ID")) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						return groupName + "是必选的，请至少选择一个商品";
					}
				}
				return "";
			},
			checkMinMax : function(group, selectElements) {
				var min = group.get("MIN_NUM");
				var max = group.get("MAX_NUM");
				var groupName = group.get("GROUP_NAME");
				var groupId = group.get("GROUP_ID");

				var count = 0;
				for (var k = 0; k < selectElements.length; k++) {
					var ele = selectElements.get(k);
					if (groupId == ele.get("PACKAGE_ID")) {
						count++;
					}
				}
				if (count > 0) {
					if (max != -1 || min != -1) {
						var err = groupName + ",";
						var flag = false;
						if (max != -1 && count > max) {
							err += "最多选择[" + max + "]个,";
							flag = true;
						}
						if (min != -1 && count < min) {
							err += "最少选择[" + min + "]个,";
							flag = true;
						}
						if (flag) {
							err += "当前选择[" + count + "]个";
							return err;
						}
					}
				}
				return "";
			},

			cancelAction : function() {
				backPopup(this.id + "Pop");
				this.currentProduct = null;
			},
			searchProduct : function(obj) {
				var value = obj ? $(obj).val() : this.find("#searchInput").val();
				if (value.length < 2) {
					this.hideSearchItemListPart();
					return false;
				}
				var search = new Search(value);
				var searchResult = new Array();
				for (var i = 0; i < this.products.length; i++) {
					if (searchResult.length >= 10) {
						break;
					}
					var offer = this.products.get(i);
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
						var offer = this.products.get(position);
						var offerCode = offer.get("OFFER_CODE");
						var offerName = offer.get("OFFER_NAME");
						html.push('<li class="link" offerCode="' + offerCode + '" offerName="' + offerName + '">');
						html.push('<div class="main e_size-m" title="' + offer.get("DESCRIPTION") + '" >');
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
			choiceProduct : function(offerCode, offerType) {
				this.selectProduct(offerCode, offerType);
				this.hideSearchItemListPart();
			},

			find : function(name) {
				return $("#" + this.id).find(name);
			}
		};
		ProductSelect.fn.init.prototype = ProductSelect.prototype;
	}
}();
