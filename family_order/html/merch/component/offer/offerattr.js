if (typeof (OfferAttr) == "undefined") {
	OfferAttr = function(id, n) {
		return new OfferAttr.fn.init(id, n);
	};
	OfferAttr.fn = {
		init : function(id, n) {
			this.id = id;
			this.dom = $("#" + this.id);
		},
	};
	OfferAttr.prototype = {
		render : function(offer, eparchyCode, isView, fn) {
			
//			window["offerAttrPop"] = new Wade.Popup("offerAttrPop",{
//				visible:false,
//				mask:true
//			});//duhj
			
//			window[this.id + "Pop"] = new Wade.Popup(this.id + "Pop",{
//				visible:false,
//				mask:true
//			});//duhj
			
			this.offer = offer;
			this.afterConfirmAction = fn;
			var elementId = offer.get("OFFER_CODE");
			var elementType = offer.get("OFFER_TYPE");
			var params = "&OFFER_CODE=" + elementId + "&OFFER_TYPE=" + elementType + "&EPARCHY_CODE=" + eparchyCode;
			$.beginPageLoading("加载中。。。");
			var that = this;
			$.ajax.submit(null, null, params, this.id + "Panel", function(ajaxData) {
				$.endPageLoading();
				var offerName = offer.get("OFFER_NAME") ? offer.get("OFFER_NAME") : "";
				var offerDesc = offer.get("DESCRIPTION") ? offer.get("DESCRIPTION") : "";
				that.find("[name=offerName]").html(offerName);
				that.find("[name=offerDesc]").html(offerDesc);
				
				var startDate = offer.get("START_DATE");
				if (startDate) {
					that.find("li[name=dateLi]").show();
					that.find("[name=startDate]").html(offer.get("START_DATE").substring(0, 10));
					var end = that.find("[name=endDate]").html(offer.get("END_DATE").substring(0, 10));
					var isCanChangeDate = offer.get("CHOICE_START_DATE") == "true" || offer.get("SELF_END_DATE") == "true"
							|| offer.get("FLIP_END_DATE") == "true";
					if (isCanChangeDate) {
						end.after('<a href="#nogo"  class="e_ico-edit" ontap="' + that.id + '.modifyTime()"></a>');
					}
				} else {
					that.find("li[name=dateLi]").hide();
				}

				that.afterShowAttr(ajaxData, isView);
			});
			
			showPopup(this.id + "Pop", this.id + "Item");
		},
		afterShowAttr : function(data, isView, obj) {
			var offer = this.offer;
			if (offer) {
				if (data && data.length > 0) {
					// 只有属性类型为9时才会执行弹出自定义窗口设置属性
					var productId = offer.get("PRODUCT_ID");
					for (var i = 0; i < data.length; i++) {
						var popupAttr = data.get(i);
						if (popupAttr.get("PRODUCT_ID") == productId) {
							var param = "&ELEMENT_ID=" + offer.get("OFFER_CODE") + "&ELEMENT_TYPE_CODE=" + offer.get("OFFER_TYPE") + "&PRODUCT_ID="
									+ offer.get("PRODUCT_ID") + "&PACKAGE_ID=" + offer.get("GROUP_ID") + "&ITEM_INDEX=" + this.itemIndex;
							popupPage(popupAttr.get("ATTR_FIELD_CODE"), popupAttr.get("ATTR_FIELD_NAME"), param, popupAttr.get("TITLE"), popupAttr
									.get("WIDTH"), popupAttr.get("HEIGHT"));
							return;
						}
					}
				}
				var attrs = offer.get("ATTR_PARAM");
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
						
						if(isView){
							var attrObj = this.find("#ATTR" + attrCode);
							attrObj.attr("disabled",true);
						}
					}
				}
				
				if(isView) {
					$("#offerAttrSubmit").addClass("e_hide");
				}else {
					$("#offerAttrSubmit").removeClass("e_hide");
				}
			}
		},
		find : function(name) {
			return this.dom.find(name);
		},
		confirmAttr : function() {
			var offer = this.offer;
			if($.validate.verifyAll(this.id + 'Panel')){
				var attrs = offer.get("ATTR_PARAM");
				var length = attrs.length;
				var isUpdate = false;
				for (var i = 0; i < length; i++) {
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					var newAttrValue = this.find("#ATTR" + attrCode).val();
					if (attrValue != newAttrValue) {
						attr.put("ATTR_VALUE", newAttrValue);
						isUpdate = true;
					}
				}
				 if (isUpdate) {
					 if(isUpdate&&offer.get("MODIFY_TAG")!="0"&&offer.get("MODIFY_TAG")!="1") {
						 offer.put("MODIFY_TAG","2");
					 }
				 }
				hidePopup(this.id + 'Pop');
				if (typeof (this.afterConfirmAction) == "function") {
					this.afterConfirmAction.call(Object);
				}
				this.afterConfirmAction = null;
			}
		},
		modifyTime : function() {
			var offer = this.offer;
			if (offer != null) {
				var that = this;
				modifyTime.render(offer, function() {
					that.find("[name=startDate]").html(offer.get("START_DATE"));
					that.find("[name=endDate]").html(offer.get("END_DATE"));
				});
			}
		}
	};

	OfferAttr.fn.init.prototype = OfferAttr.prototype;

}