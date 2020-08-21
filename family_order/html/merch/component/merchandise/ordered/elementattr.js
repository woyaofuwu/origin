!function() {
	if (typeof (ElementAttr) == "undefined") {
		ElementAttr = function(id, scrollId) {
			return new ElementAttr.fn.init(id, scrollId);
		};
		ElementAttr.fn = ElementAttr.prototype = {
			init : function(id, scrollId) {
				this.id = id;
				this.smartFloat(scrollId);
				if (window.Tabset && $.isFunction(window.Tabset) && window.Tabset.prototype._init) {
					this.tabsetHeight = $("#" + this.id).parents().find("div.tab").height();
				}
			},
			smartFloat : function(scrollId) {
				var that = this;
				$(document).ready(function() {
					var oldScrollsTop = 0;
					var scrollObj = null;
					if (scrollId) {
						scrollObj = $("#" + scrollId);
					} else {
						scrollObj = $(that).parents(".c_scroll");
					}
					if (scrollObj.length > 0) {
						$(scrollObj).scroll(function() {
							var obj = $("#" + that.id).find("#elementAttrFloat");
							var scrolls = $(this).scrollTop();
							if (obj.is(":visible")) {
								var top = obj.css("top").replace("px", "") * 1;// 原来高度
								if (!isNaN(top)) {
									var offset = oldScrollsTop - scrolls;
									top += offset;
									obj.css("top", top);
								}
							}
							oldScrollsTop = scrolls;
						});
					}
				});
			},
			renderComponent : function(params, tempElement, itemIndex, elementId, elementType) {
				var obj = arguments.callee.caller.arguments[0] || event.srcElement;

				if (this.isChangeElemnt(itemIndex, elementId, elementType)) {
					this.afterShowAttr(this.data, tempElement, obj);
				} else {
					this.clearCache();
					params += "&ELEMENT_ID=" + elementId + "&ELEMENT_TYPE_CODE=" + elementType + "&ITEM_INDEX=" + itemIndex;
					if (typeof (getSpecialAreaParam) == "function") {
						params += getSpecialAreaParam();
					}
					var that = this;
					$.ajax.submit(null, null, params, this.id, function(data) {
						that.setCurrentElementInfo(itemIndex, elementId, elementType, data);
						that.afterShowAttr(data, tempElement, obj)
					});
				}
			},
			isChangeElemnt : function(itemIndex, elementId, elementType) {
				if (this.itemIndex == itemIndex && this.elementId == elementId && this.elementType == elementType) {
					return true;
				}
				return false;
			},
			setCurrentElementInfo : function(itemIndex, elementId, elementType, data) {
				this.itemIndex = itemIndex;
				this.elementId = elementId;
				this.elementType = elementType;
				this.data = data;
			},
			clearCache : function() {
				this.itemIndex = null;
				this.elementId = null;
				this.elementType = null;
				this.data = null;
			},
			afterShowAttr : function(data, tempElement, obj) {
				if (tempElement) {
					if (data && data.length > 0) {
						// 只有属性类型为9时才会执行弹出自定义窗口设置属性
						var productId = tempElement.get("PRODUCT_ID");
						for (var i = 0; i < data.length; i++) {
							var popupAttr = data.get(i);
							if (popupAttr.get("PRODUCT_ID") == productId) {
								var param = "&ELEMENT_ID=" + tempElement.get("ELEMENT_ID") + "&ELEMENT_TYPE_CODE="
										+ tempElement.get("ELEMENT_TYPE_CODE") + "&PRODUCT_ID=" + tempElement.get("PRODUCT_ID") + "&PACKAGE_ID="
										+ tempElement.get("PACKAGE_ID") + "&ITEM_INDEX=" + this.itemIndex;
								popupPage(popupAttr.get("ATTR_FIELD_CODE"), popupAttr.get("ATTR_FIELD_NAME"), param, popupAttr.get("TITLE"),
										popupAttr.get("WIDTH"), popupAttr.get("HEIGHT"));
								return;
							}
						}
					}
					var attrs = tempElement.get("ATTR_PARAM");
					if (attrs != null) {
						var length = attrs.length;
						for (var i = 0; i < length; i++) {
							var attr = attrs.get(i);
							var attrCode = attr.get("ATTR_CODE");
							var attrValue = attr.get("ATTR_VALUE");
							if (attrValue) {
								var attrObj = this.find("#ATTR" + attrCode);
								attrObj.val(attrValue);
								if (attrObj.attr("type") == "radio" || attrObj.attr("type") == "checkbox") {
									if (attrValue != "" && attrObj.val() == attrValue) {
										attrObj.attr("checked", true);
										attrObj.click();
									}
								}
							}
						}
					}
				}
				this.resetLayout(obj);
			},
			resetLayout : function(eventObj) {
				var obj = $(eventObj).parents("li");
				var o = obj.offset();
				var top = o.top + obj.height();
//				var header = $("#" + this.id).parent("body").children("div.c_header");
//				if (header.length > 0) {
//					top -= header.height();
//				}
//				if (this.tabsetHeight) {
//					top -= this.tabsetHeight;
//				}
//				var htmlHieght = $(document.body).height();
//				var elementAttrHiegth = this.find("#elementAttrFloat").height();
//				var bottomVal = htmlHieght - (top + elementAttrHiegth);
//				if (bottomVal < 0) {
//					top = o.top - elementAttrHiegth;
//				}
				var left = o.left;

				this.find("#elementAttrFloat").css("width", obj.width());
				this.find("#elementAttrFloat").css("top", top);
				this.find("#elementAttrFloat").css("left", left);
				this.find("#elementAttrFloat").css("position", "relative");
				this.find("#elementAttrFloat").addClass("c_float-show");

			},
			radioClick : function(eventObj) {
				var obj = $(eventObj);
				var name = obj.attr("name");
				var attrs = this.find("input[name=" + name + "]");
				var length = attrs.length;
				obj.val(obj.attr("attrValue"));
				for (var i = 0; i < length; i++) {
					var attr = $(attrs[i]);
					if (obj.attr("id") == attr.attr("id")) {
						continue;
					} else {
						attr.val("");
					}
				}
			},
			exeSelfFunc : function(funcStr, obj) {
				try {
					var fn = null;
					if (typeof (funcStr) == "function") {
						fn = funcStr
					} else if (typeof (funcStr) == "string") {
						fn = eval(funcStr);
					}
					if (fn && typeof (fn) == "function") {
						fn(obj);
					}
				} catch (e) {
				}
			},
			cancelAction : function(obj) {
				var cancelHandler = $(obj).attr("cancelHandler");
				if (cancelHandler && cancelHandler != "") {
					eval(cancelHandler);
				}
				this.hide();
			},
			hide : function() {
				this.find("#elementAttrFloat").removeClass('c_float-show');
			},
			confirmAction : function(obj) {
				if ($.validate.verifyAll(this.id + '_elementPanel')) {
					var confirmHandler = $(obj).attr("confirmHandler");
					if (confirmHandler && confirmHandler != "") {
						eval(confirmHandler);
					} else {
						var itemIndex = $(obj).attr("itemIndex");
						var bindObj = this.getBindIdObject();
						if (bindObj) {
							bindObj.confirmAttr(itemIndex);
						}
					}
					this.hide();
				}
			},
			getBindIdObject : function() {
				if (this.bindObj) {
					return this.bindObj;
				}
				var bindId = this.find("#bindedComponent").val();
				this.bindObj = window[bindId];
				return this.bindObj;
			},
			find : function(name) {
				return $("#" + this.id).find(name);
			}
		};
		ElementAttr.fn.init.prototype = ElementAttr.prototype;
	}
}();