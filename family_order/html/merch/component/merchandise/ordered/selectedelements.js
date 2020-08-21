!function() {
	if (typeof (SelectedElements) == "undefined") {
		SelectedElements = function(id, n) {
			return new SelectedElements.fn.init(id, n);
		};
		SelectedElements.fn = SelectedElements.prototype = {
			TIME_SEPARATOR : " ~ ",
			HANDLER : "com.asiainfo.veris.crm.iorder.web.merch.component.merchandise.ItemHandler",
			init : function(id, n) {
				this.id = id;
				this.selectDiscntUl = this.find("#SelectDiscntUl");
				this.selectSvcUl = this.find("#SelectSvcUl");
				if ($.isObject(n))
					$.extend(this, n)
				this.initEnv();
				return this;
			},
			initEnv : function() {
				this.isEffectNow = false;
				this.userProductId = null;
				this.nextProductId = null;
				this.nextProductStartDate = null;
				this.eparchyCode = null;
				this.userId = null;
				this.newProductId = null;
				this.selectedEls = $.DatasetList();
				this.bindObj = $.DataMap();
			},
			clearCache : function() {
				this.initEnv();
				var bindObj = this.getBindIdObject("item");
				if (bindObj)
					bindObj.mainProductId = null;
			},
			setEnv : function(userId, routeEparchyCode, tradeTypeCode, userProductId, nextProductId, nextProductStartDate) {
				this.userId = userId;
				this.eparchyCode = routeEparchyCode;
				this.userProductId = userProductId;
				this.nextProductId = nextProductId;
				this.nextProductStartDate = nextProductStartDate;
				this.tradeTypeCode = tradeTypeCode;
			},
			setNewProduct : function(productId) {
				this.newProductId = productId;
			},
			resetComponent : function() {
				this.clearAllElementFee();
				$.ajax.submit(null, null, null, this.id);
				// this.clearCache();
			},
			renderComponent : function(selectElements) {
				$.beginPageLoading("已选区加载中。。。。。");
				var data = "&USER_ID=" + this.userId;
				if (this.newProductId) {
					data += "&NEW_PRODUCT_ID=" + this.newProductId;
				}
				if (this.userProductId || this.nextProductId) {
					data += "&USER_PRODUCT_ID=" + (this.nextProductId ? this.nextProductId : this.userProductId);
				}

				this.clearAllElementFee();
				data += "&EPARCHY_CODE=" + this.eparchyCode + "&TRADE_TYPE_CODE=" + this.tradeTypeCode + "&CALL_SVC=" + this.renderCallSvc;
				if (selectElements && selectElements.length > 0) {
					data += "&SELECT_ITEM_LIST=" + selectElements.toString();
				}
				data += this.getOtherParam("render");
				var that = this;

				if (typeof (getSpecialAreaParam) == "function") {
					data += getSpecialAreaParam();
				}
				if(typeof($("#BOOKING_DATE").val()) != "undefined" && $("#BOOKING_DATE").val() != null && $("#BOOKING_DATE").val() != ''){
					data+= "&BOOKING_DATE="+$("#BOOKING_DATE").val();
				}
				hhSubmit(null, this.HANDLER, "render", data, function(ajaxData) {
					that.initSelectedElements(ajaxData);
				}, function(errorCode, errorInfo) {
					that.errProcess(errorCode, errorInfo);
				});
			},
			effectBookingDate:function(){
				this.isEffectNow = false;
				var length = this.selectedEls.length;
				var tempEls = new $.DatasetList();
				var bookingDate = $("#BOOKING_DATE").val();
				
				for(var i=0;i<length;i++){
					var temp = this.selectedEls.get(i);
					if(temp.get("MODIFY_TAG")=="0" || temp.get("MODIFY_TAG")=="0_1"){
						//temp.put("START_DATE", bookingDate);
						tempEls.add(temp);
					}else if(temp.get("MODIFY_TAG")=="1"){
						tempEls.add(temp);
					}
				}
				
				if(tempEls.length > 0)
				{
					var params = "&ELEMENTS="+tempEls.toString()+"&EPARCHY_CODE="+this.eparchyCode;
					if($("#basicStartDateControlId").val()!=""){
						params+="&BASIC_START_DATE="+$("#"+$("#basicStartDateControlId").val()).val();
					}
					if($("#basicCancelDateControlId").val()!=""){
						params+="&BASIC_CANCEL_DATE="+$("#"+$("#basicCancelDateControlId").val()).val();
					}
					if(this.isEffectNow){
						params+="&EFFECT_NOW=true";
					}
					if(typeof(selectedElements.getOtherParam)=="function"){
						params += selectedElements.getOtherParam();
					}
					if(this.userProductId!=null){
						params+="&USER_PRODUCT_ID="+this.userProductId;
					}
					if(this.nextProductId!=null){
						params+="&NEXT_PRODUCT_ID="+this.nextProductId;
					}
					if(this.nextProductStartDate!=null){
						params+="&NEXT_PRODUCT_START_DATE="+this.nextProductStartDate;
					}
					if(typeof($("#NEW_PRODUCT_ID").val()) !="undefined" && $("#NEW_PRODUCT_ID").val() != null && $("#NEW_PRODUCT_ID").val() != ''){
						params+="&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val();
					}
					params+="&SELECTED_ELEMENTS="+encodeURIComponent(this.selectedEls.toString())+"&USER_ID="+this.userId+"&CALL_SVC=" + this.callAddElementSvc + "&TRADE_TYPE_CODE="+this.tradeTypeCode;
					params+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
					var that = this;
					$.beginPageLoading("已选区加载中。。。。。");
					hhSubmit(null,this.HANDLER,"dispatch", params,  function(ajaxData) {
						that.afterEffectBookingDate(ajaxData);
					}, function(errorCode, errorInfo) {
						that.errProcess(errorCode, errorInfo);
					});
				}
			},
			afterEffectBookingDate:function(data){
				if(data.length > 0){
//					this.draw(data, true);
					for(var i=0; i<data.length; i++){
						var element = data.get(i);
						var temp = selectedElements.selectedEls.get(element.get("ITEM_INDEX"));
						temp.put("START_DATE",element.get("START_DATE"));
						temp.put("END_DATE",element.get("END_DATE"));
						temp.put("EFFECT_NOW_START_DATE",element.get("EFFECT_NOW_START_DATE"));
						temp.put("EFFECT_NOW_END_DATE",element.get("EFFECT_NOW_END_DATE"));
						temp.put("OLD_EFFECT_NOW_START_DATE",element.get("OLD_EFFECT_NOW_START_DATE"));
						temp.put("OLD_EFFECT_NOW_END_DATE",element.get("OLD_EFFECT_NOW_END_DATE"));
						if(element.get("ELEMENT_TYPE_CODE")=="D"){
							selectedElements.setStartDateView(element.get("ITEM_INDEX"),temp.get("START_DATE"));
							selectedElements.setEndDateView(element.get("ITEM_INDEX"),temp.get("END_DATE"));
						}	
					}
				}
				$.endPageLoading();
			},
			setStartDateView : function(itemIndex,startDate) {
				var el = this.selectedEls.get(itemIndex);
			/*	if (startDate.substring(0, 10) == (el.get("EFFECT_NOW_START_DATE").substring(0, 10))) {
					el.put("OLD_EFFECT_NOW_START_DATE", el.get("START_DATE"));
					el.put("OLD_EFFECT_NOW_END_DATE", el.get("END_DATE"));
					el.put("START_DATE", el.get("EFFECT_NOW_START_DATE"));
					el.put("END_DATE", el.get("EFFECT_NOW_END_DATE"));
				} else if (startDate.substring(0, 10) == el.get("OLD_EFFECT_NOW_START_DATE").substring(0, 10)) {
					el.put("START_DATE", el.get("OLD_EFFECT_NOW_START_DATE"));
					el.put("END_DATE", el.get("OLD_EFFECT_NOW_END_DATE"));
				}*/
				
				if (el.get("SELF_END_DATE") != "true") {
					this.find("#" + itemIndex + "_END_DATE").html(el.get("END_DATE").substring(0, 10));
				}
				this.find("#" + itemIndex + "_CHOICE_START_DATE_SPAN").children("span").html(startDate.substring(0, 10));
				this.find("#" + itemIndex + "_START_DATE").html(startDate.substring(0, 10));
			},
			setEndDateView : function(itemIndex,endDate) {
				var el = this.selectedEls.get(itemIndex);
				var startDate = el.get("START_DATE");
				this.find("#" + itemIndex + "_END_DATE").html(endDate.substring(0, 10));
				el.put("END_DATE", endDate.substring(0, 10) + " 23:59:59");
			},
			initSelectedElements : function(data) {
				this.selectDiscntUl.empty();
				this.selectSvcUl.empty();
				if (data && data.length > 0) {
					var elements = data.get(0).get("SELECTED_ELEMENTS");
					if (elements && elements.length > 0) {
						this.selectedEls = elements;
						this.initFee(elements);
						this.draw(elements, true);
					}
				}
				if (!this.selectedEls || this.selectedEls.length == 0) {
					this.selectedEls = new $.DatasetList();
				}
				this.afterAction("AFTER_RENDER_ACTION", data);
				this.afterAction("ELEMENT_EXTEND_ACTION");

				this.hiddenAttr();

				$.endPageLoading();
			},

			initFee : function(elements) {
				var length = elements.length;
				for (var i = 0; i < length; i++) {
					var element = elements.get(i);
					if (element.get("MODIFY_TAG") == "0") {
						var feeData = element.get("FEE_DATA");
						if (feeData != null && typeof (feeData) != "undefined" && feeData.length > 0) {
							var feeSize = feeData.length;
							for (var j = 0; j < feeSize; j++) {
								var fee = feeData.get(j);
								$.feeMgr.insertFee(fee);
							}
						}
					}
				}
			},

			clearAllElementFee : function() {
				var length = this.selectedEls.length;
				for (var i = 0; i < length; i++) {
					var el = this.selectedEls.get(i);
					if (el.get("MODIFY_TAG") == "0") {
						var feeData = el.get("FEE_DATA");
						if (feeData != null && typeof (feeData) != "undefined" && feeData.length > 0) {
							var feeSize = feeData.length;
							for (var j = 0; j < feeSize; j++) {
								var fee = feeData.get(j);
								$.feeMgr.deleteFee(fee);
							}
						}
					}
				}
			},

			checkBoxAction : function(elCheckBox) {
				var itemIndex = $(elCheckBox).attr("itemIndex");
				var el = this.selectedEls.get(itemIndex);
				var checked = $(elCheckBox).attr("checked");
				if (checked) {
					var modifyTimeObj = this.find("#" + itemIndex + "_MODIFYTIME");
					if (modifyTimeObj) {
						modifyTimeObj.css("display", "none");
					}
					
					var obj = this.find("#" + itemIndex + "_ATTRPARAM");
					if (obj) {
						obj.css("display", "");
					}
					if (el.get("MODIFY_TAG") == "1") {
						$(elCheckBox).parent().parent().removeClass("e_delete");

						if (el.get("MODIFY_ATTR") == "true") {
							el.put("MODIFY_TAG", "2");
						} else {
							el.put("MODIFY_TAG", "exist");
						}

						el.put("START_DATE", el.get("OLD_START_DATE"));
						el.put("END_DATE", el.get("OLD_END_DATE"));
						if (el.get("ELEMENT_TYPE_CODE") == "D") {
							this.resetDate(el);
						}
					} else if (el.get("MODIFY_TAG") == "0_1") {
						// 新增过，又删除的，再新增
						el.put("MODIFY_TAG", "0");
						var feeData = el.get("FEE_DATA");
						if (feeData != null && typeof (feeData) != "undefined" && feeData.length > 0) {
							var feeSize = feeData.length;
							for (var j = 0; j < feeSize; j++) {
								var fee = feeData.get(j);
								$.feeMgr.insertFee(fee);
							}
						}
						this.afterAction("AFTER_CHECK_BOX_ACTION", el);

						var bindObj = this.getBindIdObject("item");
						if (bindObj)
							bindObj.afterOrder(el.get("ELEMENT_ID"), el.get("ELEMENT_TYPE_CODE"));
					}
					this.afterAction("ELEMENT_EXTEND_ACTION");

				} else {
					var modifyTimeObj = this.find("#" + itemIndex + "_MODIFYTIME");
					if (modifyTimeObj) {
						modifyTimeObj.css("display", "");
					}
					
					var obj = this.find("#" + itemIndex + "_ATTRPARAM");
					if (obj) {
						obj.css("display", "none");
					}
					if (el.get("MODIFY_TAG") == "0") {
						// 新增的,删除掉
						el.put("MODIFY_TAG", "0_1");
						var feeData = el.get("FEE_DATA");
						if (feeData != null && typeof (feeData) != "undefined" && feeData.length > 0) {
							var feeSize = feeData.length;
							for (var j = 0; j < feeSize; j++) {
								var fee = feeData.get(j);
								$.feeMgr.deleteFee(fee);
							}
						}
						this.afterAction("ELEMENT_EXTEND_ACTION");
						this.afterAction("AFTER_CHECK_BOX_ACTION", el);

						var bindObj = this.getBindIdObject("item");
						if (bindObj)
							bindObj.afterOrder(el.get("ELEMENT_ID"), el.get("ELEMENT_TYPE_CODE"));
					} else if (el.get("MODIFY_TAG") == "exist" || el.get("MODIFY_TAG") == "2") {
						// 已有的，删除掉
						// 表示是用户原有的元素
						$(elCheckBox).parent().parent().addClass("e_delete");

						this.hiddenAttr();
						el.put("MODIFY_TAG", "1");// 删除

						el.put("OLD_START_DATE", el.get("START_DATE"));
						el.put("OLD_END_DATE", el.get("END_DATE"));

						var tempEls = new $.DatasetList();
						tempEls.add(el);

						var params = this.prepareDealElementsParam(tempEls);
						params += this.getOtherParam("check");

						if (typeof (getSpecialAreaParam) == "function") {
							params += getSpecialAreaParam();
						}
						
						$.beginPageLoading("已选区加载中。。。。。");
						var that = this;

						hhSubmit(null, this.HANDLER, "dispatch", params, function(data) {
							that.afterCheckBoxAction(data, elCheckBox);
						}, function(errorCode, errorInfo) {
							that.errProcessReverse(errorCode, errorInfo, elCheckBox)
						});
					}
				}
			},

			afterCheckBoxAction : function(data, elCheckBox) {
				var element = data.get(0);
				if (element.get("ERROR_INFO")) {
					var result = window.confirm(element.get("ERROR_INFO").replace(/<br>/ig, "\n")
							+ "\n点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素\n点击“取消”按钮取消本次操作");
					if (!result) {
						elCheckBox.click();
						$.endPageLoading();
						return;
					}
				}

				var temp = this.selectedEls.get(element.get("ITEM_INDEX"));
				temp.put("END_DATE", element.get("END_DATE"));
				temp.put("EFFECT_NOW_START_DATE", element.get("EFFECT_NOW_START_DATE"));
				temp.put("EFFECT_NOW_END_DATE", element.get("EFFECT_NOW_END_DATE"));
				temp.put("OLD_EFFECT_NOW_START_DATE", element.get("OLD_EFFECT_NOW_START_DATE"));
				temp.put("OLD_EFFECT_NOW_END_DATE", element.get("OLD_EFFECT_NOW_END_DATE"));
				if (element.get("ELEMENT_TYPE_CODE") == "D") {
					this.find("#" + element.get("ITEM_INDEX") + "_END_DATE").html(temp.get("END_DATE").substring(0, 10));
				}

				this.afterAction("ELEMENT_EXTEND_ACTION");
				$.endPageLoading();
			},

			showAttr : function(obj) {
				var itemIndex = $(obj).attr("itemIndex");
				var elementId = $(obj).attr("elementId");
				var elementType = $(obj).attr("elementType");
				var params = "&EPARCHY_CODE=" + this.eparchyCode;
				params += this.getOtherParam("showattr");
				if ("Z" == elementType) {
					params += "&DISPLAY_CONDITION=06";
				}
				var tempElement = this.selectedEls.get(itemIndex);

				var bindObj = this.getBindIdObject("attr");
				if (bindObj)
					bindObj.renderComponent(params, tempElement, itemIndex, elementId, elementType);
			},
			checkAndConfirmAttr : function(itemIndex) {
				if ($.validate.verifyAll('elementPanel')) {
					var tempElement = this.selectedEls.get(itemIndex);
					var attrs = tempElement.get("ATTR_PARAM");
					var length = attrs.length;
					var isUpdate = false;
					var bindObj = this.getBindIdObject("attr");
					$.beginPageLoading("属性信息校验中。。。");
					var that = this;
					var error_num = 0;
					for (var i = 0; i < length; i++) {
						var attr = attrs.get(i);
						var attrCode = attr.get("ATTR_CODE");
						var attrValue = attr.get("ATTR_VALUE");
						if (bindObj) {
							var newAttrValue = $("#" + bindObj.id).find("#ATTR" + attrCode).val();
							if (attrValue != newAttrValue) {
								var params = "&ELEMENT_ID=" + tempElement.get("ELEMENT_ID") + "&EPARCHY_CODE=" + this.eparchyCode + "&ATTR_CODE="
										+ attrCode + "&ATTR_VALUE=" + newAttrValue + "&OLD_ATTR_VALUE=" + attrValue;
								params += this.getOtherParam("checkAndConfirmAttr");

								if (typeof (getSpecialAreaParam) == "function") {
									params += getSpecialAreaParam();
								}

								hhSubmit(null, this.HANDLER, "checkAttr", params, function(ajaxData) {
									if (ajaxData && ajaxData.length > 0) {
										var result = ajaxData.get(0);
										var result_code = result.get("X_RESULTCODE");
										var result_info = result.get("X_RESULTINFO");
										var result_OLD_ATTR_VALUE = result.get("OLD_ATTR_VALUE");
										var result_ATTR_CODE = result.get("ATTR_CODE");
										if (result_code != "0") {
											$("#" + bindObj.id).find("#ATTR" + attrCode).val(result_OLD_ATTR_VALUE)
											alert("元素属性校验>>" + result_info);
											$.endPageLoading();
											$(this.elementPanel).css("display", "block");
										}
										if (i == length) {
											that.confirmAttr(itemIndex);
										}
									}
								}, function(error_code, error_info) {
									$.endPageLoading();
									alert("属性校验出错，请与管理员联系");
									return;
								});
							}
						}
					}
					$.endPageLoading();
				}
				return true;
			},
			confirmAttr : function(itemIndex) {
				var tempElement = this.selectedEls.get(itemIndex);
				var attrs = tempElement.get("ATTR_PARAM");
				var length = attrs.length;
				var isUpdate = false;
				for (var i = 0; i < length; i++) {
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					var bindObj = this.getBindIdObject("attr");
					if (bindObj) {
						var newAttrValue = $("#" + bindObj.id).find("#ATTR" + attrCode).val();
						if (attrValue != newAttrValue) {
							if (typeof (_customVerifyConfirmAttr) == "function") {// 自定义效验,懒得加属性了
								var result = _customVerifyConfirmAttr(tempElement, attr, newAttrValue);
								if (result) {
									return;
								}
							}
							attr.put("ATTR_VALUE", newAttrValue);
							isUpdate = true;
						}
					}
				}
				if (isUpdate) {
					if (tempElement.get("MODIFY_TAG") != "0") {
						tempElement.put("MODIFY_TAG", "2");
						tempElement.put("MODIFY_ATTR", "true");
					}
				}

			},
			hiddenAttr : function() {
				var bindObj = this.getBindIdObject("attr");
				if (bindObj)
					bindObj.hide();
			},

			toggleSelectElementPart : function(obj) {
				var o = $(obj);
				o.parent().parent().parent().next().toggle();
				o.toggleClass("e_ico-unfold e_ico-fold");
			},

			renderItemList : function() {
				var productId = this.newProductId ? this.newProductId : (this.nextProductId ? this.nextProductId : this.userProductId);
				var bindObj = this.getBindIdObject("item");
				if (bindObj) {
					var param = "&SELECTED_ELEMENTS=" + encodeURIComponent(this.selectedEls.toString());
					param += this.getOtherParam("renderItem");
					bindObj.renderComponent(productId, this.eparchyCode, param);
				}
			},

			addElements : function(elementIds) {
				$.beginPageLoading("已选区加载中。。。。。");
				var params = this.prepareDealElementsParam(elementIds);
				params += this.getOtherParam("addElements");
				
				if (typeof (getSpecialAreaParam) == "function") {
					params += getSpecialAreaParam();
				}
				
				var that = this;
				hhSubmit(null, this.HANDLER, "dispatch", params, function(data) {// dealElement
					that.afterAddElements(data);
				}, function(errorCode, errorInfo) {
					that.errProcess(errorCode, errorInfo)
				});
			},
			afterAddElements : function(data) {
				var length = data.length;
				if (length > 0) {
					var info = data.get(0);
					if (info.get("ERROR_INFO")) {
						var result = window.confirm(info.get("ERROR_INFO").replace(/<br>/ig, "\n")
								+ "\n点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素\n点击“取消”按钮取消本次操作");
						if (!result) {
							$.endPageLoading();
							return;
						}
					}
					if (info.get("CONFIRM_INFO")) {
						var result = window.confirm(info.get("CONFIRM_INFO"));
						if (!result) {
							$.endPageLoading();
							return;
						}
					}
					var bindObj = this.getBindIdObject("item");
					var els = $.DatasetList();
					for (var i = 0; i < length; i++) {
						var el = data.get(i);
						if (el.get("MODIFY_TAG") == "0") {
							var itemIndex = this.selectedEls.length;
							el.put("ITEM_INDEX", itemIndex);
							this.selectedEls.add(el);
							els.add(el);
							if (bindObj)
								bindObj.afterOrder(el.get("ELEMENT_ID"), el.get("ELEMENT_TYPE_CODE"));
							if (el.get("CHOICE_START_DATE") == "true") {
								var isConfirm = window.confirm(el.get("ELEMENT_NAME") + "可以选择立即生效，点击“确定”按钮选择立即生效\n点击“取消”按钮采用默认生效方式");
								if (isConfirm) {
									this.find("#" + el.get("ITEM_INDEX") + "_CHOICE_START_DATE").val(el.get("EFFECT_NOW_START_DATE"));
									this.changeStartDate(this.find("#" + el.get("ITEM_INDEX") + "_CHOICE_START_DATE"));
								}
							}
						}
					}
					this.draw(els);
					this.initFee(data);
					$.endPageLoading();
					this.afterAction("ELEMENT_EXTEND_ACTION");
					this.afterAction("AFTER_ADD_ELEMENTS_ACTION", data);
				} else {
					$.endPageLoading();
				}
			},
			draw : function(els, isrender) {
				var discntHtml = "";
				var svcHtml = "";
				for (var i = 0; i < els.length; i++) {
					var el = els.get(i);
					var elementId = el.get("ELEMENT_ID");
					var elementType = el.get("ELEMENT_TYPE_CODE");
					var state = el.get("MODIFY_TAG");
					var itemIndex = el.get("ITEM_INDEX");
					var shoppingTag = el.get("SHOPPING_TAG");

					var isCanChangeDate = el.get("CHOICE_START_DATE") == "true" || el.get("SELF_END_DATE") == "true" || el.get("BOOKING_DATE_PRIV") == "TRUE";

					var elSelectTag = "", elementName = "", elementDesc = "";
					if (isrender) {
						elSelectTag = ("1_1" == el.get("PACKAGE_FORCE_TAG") + "_" + el.get("ELEMENT_FORCE_TAG") || el.get("DISABLED")) ? "0" : "";
						elementName = el.get("ELEMENT_NAME");
						elementDesc = el.get("ELEMENT_DESC", el.get("DESCRIPTION", ""));
					} else {
						var bindObj = this.getBindIdObject("item");
						var item = bindObj.getItem(elementId, elementType);
						elSelectTag = item.get("SELECT_FLAG");
						elementName = item.get("OFFER_NAME");
						elementDesc = item.get("DESCRIPTION");// el.put("ELEMENT_NAME",elementName);
					}

					var style = state == "0" ? "e_green" : "";
					var disabled = elSelectTag == "0" ? 'disabled="true"' : '';

					var endFormat = "yyyy-MM-dd";
					var startDate = el.get("START_DATE").substring(0, 10);
					if ("6" == el.get("START_UNIT")) {
						startDate = el.get("START_DATE");
					}
					var endDate = el.get("END_DATE").substring(0, 10);
					if ("6" == el.get("END_UNIT")) {
						endDate = el.get("END_DATE");
						endFormat = "yyyy-MM-dd HH:mm:ss";
					}

					var li = "";
					li += '<li class="link" title="' + elementDesc + '">';
					li += '<div class="content ' + (state == "1" ? 'e_delete' : '') + '">';
					li += '<div class="main">';
					li += '<div class="title ' + style + '">' + "【" + elementId + "】" + elementName + '</div>';
					if (elementType == "D") {
						li += '<span id="' + itemIndex + '_START_DATE" itemindex="' + itemIndex + '">' + el.get("START_DATE").substr(0, 10) + '</span>'
								+ this.TIME_SEPARATOR + '<span id="' + itemIndex + '_END_DATE" itemindex="' + itemIndex + '">' + el.get("END_DATE").substr(0, 10)
								+ '</span>';
						var timeObj = this.getBindIdObject("time");
						if (isCanChangeDate && timeObj && !shoppingTag) {
							li += '<a href="#nogo" style="display:none" id="' + itemIndex + '_MODIFYTIME" name="' + itemIndex + '_MODIFYTIME" class="e_ico-edit" onclick="'
								+ this.id + '.modifyTime(' + itemIndex + ')"></a>';
//							li += '<span class="e_ico-unfold" onclick="' + this.id + '.showDataPart(' + itemIndex + ')"></span>';
						}
					}
					li += '</div>';
					
					if(shoppingTag) {
						li += '<div class="statu statu-right statu-blue">购物车</div>';
					}else {
						if (el.containsKey("ATTR_PARAM")) {
							li += '<div class="fn">';
							li += '<span class="e_ico-unfold" id="' + itemIndex + '_ATTRPARAM" name="' + itemIndex + '_ATTRPARAM" itemIndex="'
									+ itemIndex + '" elementId="' + elementId + '" elementType="' + elementType + '" onclick="' + this.id
									+ '.showAttr(this)" ></span>';
							li += '</div>';
						}
						
						li += '<div class="fn">';
						li += '<input name="' + (elementType == "D" ? "SELECTED_DISCNT_CHECKBOX" : "SELECTED_SVC_CHECKBOX")
								+ '" class="e_checkbox" checked="true" type="checkbox" value="' + elementId + '" check="checked" itemIndex="' + itemIndex
								+ '" onclick="' + this.id + '.checkBoxAction(this)" ' + disabled + '/>';
						li += '</div>';
					}

					li += '</div>';

					if (isCanChangeDate && elementType == "D") {
						li += '<div class="sub" style="display:none" id="DATE_PART_' + itemIndex + '" itemIndex="' + itemIndex
								+ '"><div class="main"><div class="c_form c_form-col-2 c_form-label-4"><ul>';
						if (isCanChangeDate) {
							li += '<li>';
							li += '<div class="label">开始日期：</div>';
							li += '<div class="value">';
							li += '<span class="e_select" id="' + itemIndex + '_CHOICE_START_DATE_SPAN" itemIndex="' + itemIndex + '" onclick="'
									+ this.id + '.showChoice;artDate(this);">';
							li += '<span>' + startDate + '</span>';
							li += '<input type="hidden" id="' + itemIndex + '_CHOICE_START_DATE" itemIndex="' + itemIndex + '" value="' + startDate
									+ '" nullable="no" >';
							li += '</div>';
							li += '</li>';
							this.initChoiceStartDate(itemIndex + "_CHOICE_START_DATE_SPAN", itemIndex, el);
						}
						if (isCanChangeDate) {
							li += '<li>';
							li += '<div class="label">结束日期：</div>';
							li += '<div class="value">';
							li += '<input type="text" itemIndex="' + itemIndex + '" desc="结束日期" time="false" datatype="date" format="' + endFormat
									+ '" nullable="no" type="text" value="' + endDate + '" onblur="' + this.id + '.changeEndDate(this)" />';
							li += '</div>';
							li += '</li>';
						}
						li += '</ul></div></div></div>';
					}
					li += '</li>\r\n';
					if ("D" == elementType) {
						discntHtml += li;
					} else {
						svcHtml += li;
					}
				}
				if (discntHtml != "") {
					this.selectDiscntUl.prepend(discntHtml);
				}
				if (svcHtml != "") {
					this.selectSvcUl.prepend(svcHtml);
				}
			},

			toggleElement : function(elementId, elementType) {
				var checkbox = null;
				if ("D" == elementType) {
					checkbox = this.find("input:checkbox[name=SELECTED_DISCNT_CHECKBOX][value=" + elementId + "]:first");
				} else {
					checkbox = this.find("input:checkbox[name=SELECTED_SVC_CHECKBOX][value=" + elementId + "]:first");
				}
				if (checkbox.attr("checked") == "true" || checkbox.attr("checked") == true) {
					checkbox.attr("checked", false);
					this.checkBoxAction(checkbox);
				} else {
					checkbox.attr("checked", true);
					this.checkBoxAction(checkbox);
				}
			},

			getSubmitData : function() {
				var length = this.selectedEls.length;
				var submitData = $.DatasetList();
				for (var i = 0; i < length; i++) {
					var temp = this.selectedEls.get(i);
					if (temp.get("MODIFY_TAG") == "0" || temp.get("MODIFY_TAG") == "1" || temp.get("MODIFY_TAG") == "2") {
						if(!temp.get("SHOPPING_TAG")) {
							var data = new $.DataMap();
							data.put("ELEMENT_ID", temp.get("ELEMENT_ID"));
							data.put("ELEMENT_TYPE_CODE", temp.get("ELEMENT_TYPE_CODE"));
							data.put("PRODUCT_ID", temp.get("PRODUCT_ID"));
							data.put("PACKAGE_ID", temp.get("PACKAGE_ID"));
							if (temp.get("ATTR_PARAM") != null && temp.get("ATTR_PARAM").length > 0) {
								data.put("ATTR_PARAM", temp.get("ATTR_PARAM"));
							}
							data.put("MODIFY_TAG", temp.get("MODIFY_TAG"));
							data.put("START_DATE", temp.get("START_DATE"));
							data.put("END_DATE", temp.get("END_DATE"));
							data.put("INST_ID", temp.get("INST_ID"));
							submitData.add(data);
						}
					}
				}
				return submitData;
			},

			showChoiceStartDate : function(obj) {
				var float = this.find("#" + obj.id + "_float");
				if (float.length > 0) {
					this.resetStartDatePart(obj);
				}
			},
			initChoiceStartDate : function(objId, itemIndex, el) {
				var id = objId + "_float";
				var effectStartDate = el.get("EFFECT_NOW_START_DATE");
				var startDate = el.get("START_DATE");
				var html = [];
				html.push('<div id="' + id + '" class="c_float" >');
				html.push('<div class="bg"></div>');
				html.push('<div class="content" ><div class="c_scrollContent"><div class="c_list c_list-pc-s c_list-phone-line "><ul>');
				html.push('<li idx="' + itemIndex + '" val="' + effectStartDate + '" onclick="' + this.id + '.choiceStartDate(this)">');
				html.push('<div class="main">' + effectStartDate + '</div>');
				html.push('</li>');
				html.push('<li idx="' + itemIndex + '" val="' + startDate + '" onclick="' + this.id + '.choiceStartDate(this)">');
				html.push('<div class="main">' + startDate + '</div>');
				html.push('</li>');
				html.push('</ul></div></div></div>');
				$("#" + this.id).append(html.join(""));
				var that = this;
				$(document.body).bind("click", function(n) {
					if (n && n.target) {
						var a = n.target;
						if (a.id != objId) {
							that.find("#" + id).css("display", "none");
						}
					}
				});
			},
			resetStartDatePart : function(obj) {
				var offset = $(obj).offset();
				var scorll = $(document).find(".c_scroll:first");
				var top = offset.top + $(obj).outerHeight();
				if (scorll && scorll.length > 0) {
					top += scorll.scrollTop();
				}
				var left = offset.left;
				var width = $(obj).parent().width();
				var float = this.find("#" + obj.id + "_float");
				float.css("top", top + "px");
				float.css("left", left + "px");
				float.css("width", width + "px");
				float.css("display", "block");
			},
			choiceStartDate : function(obj) {
				var idx = $(obj).attr("idx");
				var val = $(obj).attr("val");
				var value = this.find("#" + idx + "_CHOICE_START_DATE").val().substring(0, 10);
				if (value != val.substring(0, 10)) {
					this.find("#" + idx + "_CHOICE_START_DATE").val(val);
					this.changeStartDate(this.find("#" + idx + "_CHOICE_START_DATE"));
				}
			},
			showDataPart : function(itemIndex) {
				$(event.target).toggleClass("e_ico-unfold e_ico-fold");
				var part = this.find("#DATE_PART_" + itemIndex);
				part.toggle();
			},
			changeStartDate : function(eventObj) {
				var obj = $(eventObj);
				var itemIndex = obj.attr("itemIndex");
				var el = this.selectedEls.get(itemIndex);
				var value = obj.val();
				if (value.substring(0, 10) == (el.get("EFFECT_NOW_START_DATE").substring(0, 10))) {
					el.put("OLD_EFFECT_NOW_START_DATE", el.get("START_DATE"));
					el.put("OLD_EFFECT_NOW_END_DATE", el.get("END_DATE"));
					el.put("START_DATE", el.get("EFFECT_NOW_START_DATE"));
					el.put("END_DATE", el.get("EFFECT_NOW_END_DATE"));
				} else if (value.substring(0, 10) == el.get("OLD_EFFECT_NOW_START_DATE").substring(0, 10)) {
					el.put("START_DATE", el.get("OLD_EFFECT_NOW_START_DATE"));
					el.put("END_DATE", el.get("OLD_EFFECT_NOW_END_DATE"));
				}
				if (el.get("SELF_END_DATE") != "true") {
					this.find("#" + itemIndex + "_END_DATE").html(el.get("END_DATE"));
				}
				this.find("#" + itemIndex + "_CHOICE_START_DATE_SPAN").children("span").html(value);
				this.find("#" + itemIndex + "_START_DATE").html(value);
			},
			changeEndDate : function(eventObj) {
				var obj = $(eventObj);
				var val = obj.val();
				var itemIndex = obj.attr("itemIndex");
				var el = this.selectedEls.get(itemIndex);
				var format = obj.attr("format");
				if (format == null || format == "") {
					format = "yyyy-MM-dd";
				}
				var isDate = this.isDate(val);
				if (!isDate) {
					alert("输入有误，请重新输入");
					if (format == 'yyyy-MM-dd') {
						obj.val(el.get("END_DATE").substring(0, 10));
					} else {
						obj.val(el.get("END_DATE"));
					}
					return false;
				}
				var startDate = el.get("START_DATE");
				if (startDate > val) {
					alert("结束时间不能小于开始时间");
					obj.val(el.get("END_DATE").substring(0, 10));
					return;
				}
				this.find("#" + itemIndex + "_END_DATE").html(val + " 23:59:59");
				el.put("END_DATE", val + " 23:59:59");
			},
			isDate : function(dateValue) {
				var regex = new RegExp(
						"^(?:(?:([0-9]{4}(-|\/)(?:(?:0?[1,3-9]|1[0-2])(-|\/)(?:29|30)|((?:0?[13578]|1[02])(-|\/)31)))|([0-9]{4}(-|\/)(?:0?[1-9]|1[0-2])(-|\/)(?:0?[1-9]|1\\d|2[0-8]))|(((?:(\\d\\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))(-|\/)0?2(-|\/)29))))$");
				if (!regex.test(dateValue)) {
					return false;
				}
				return true;
			},
			resetDate : function(element) {
				this.find("#" + element.get("ITEM_INDEX") + "_START_DATE").html(element.get("START_DATE"));
				this.find("#" + element.get("ITEM_INDEX") + "_END_DATE").html(element.get("END_DATE"));
			},

			getBindIdObject : function(key) {
				if (!this.bindObj.containsKey(key)) {
					if ("attr" == key) {
						var bindId = this.find("#attrComponent").val();
						if (bindId) {
							var obj = window[bindId];
							this.bindObj.put("attr", obj);
						}
					}
					if ("item" == key) {
						var bindId = this.find("#itemComponent").val();
						if (bindId) {
							var obj = window[bindId];
							this.bindObj.put("item", obj);
						}
					}
					if ("time" == key) {
						var bindId = this.find("#timeComponent").val();
						if (bindId) {
							var obj = window[bindId];
							this.bindObj.put("time", obj);
						}
					}
				}
				return this.bindObj.get(key);
			},
			getOtherParam : function(type) {
				if (typeof (getOtherParam) == 'function') {
					var data = getOtherParam.call(this, type);
					if (data) {
						return data;
					}
				}
				return "";
			},
			hasElement : function(elementId, elementType) {
				var ele = this.getElement(elementId, elementType);
				if (ele != null) {
					return true;
				}
				return false;
			},
			getElement : function(elementId, elementType) {
				if (this.selectedEls) {
					for (var i = 0; i < this.selectedEls.length; i++) {
						var ele = this.selectedEls.get(i);
						if (elementType == ele.get("ELEMENT_TYPE_CODE") && elementId == ele.get("ELEMENT_ID")) {
							return ele;
						}
					}
				}
				return null;
			},
			prepareDealElementsParam : function(elementIds) {
				var params = "&ELEMENTS=" + encodeURIComponent(elementIds.toString()) + "&EPARCHY_CODE=" + this.eparchyCode + "&SELECTED_ELEMENTS="
						+ encodeURIComponent(this.selectedEls.toString()) + "&USER_ID=" + this.userId + "&TRADE_TYPE_CODE=" + this.tradeTypeCode;
				var basicStartDateControlId = this.find("#basicStartDateControlId").val();
				if (basicStartDateControlId != "") {
					params += "&BASIC_START_DATE=" + $("#" + basicStartDateControlId).val();
				}
				var basicCancelDateControlId = this.find("#basicCancelDateControlId").val();
				if (basicCancelDateControlId != "") {
					params += "&BASIC_CANCEL_DATE=" + $("#" + basicCancelDateControlId).val();
				}
				if (this.isEffectNow) {
					params += "&EFFECT_NOW=true";
				}
				if (this.userProductId != null) {
					params += "&USER_PRODUCT_ID=" + this.userProductId;
				}
				if (this.nextProductId != null) {
					params += "&NEXT_PRODUCT_ID=" + this.nextProductId;
				}
				if (this.nextProductStartDate != null) {
					params += "&NEXT_PRODUCT_START_DATE=" + this.nextProductStartDate;
				}
				params += "&CALL_SVC=" + this.callAddElementSvc;
				return params;
			},
			checkForcePackageBylab : function() {
				var bindObj = this.getBindIdObject("item");
				if (bindObj) {
					if ((this.newProductId != null && this.newProductId == bindObj.mainProductId)
							|| (this.newProductId == null && this.userProductId == bindObj.mainProductId)) {
						var packages = bindObj.getGroups();
						for (var i = 0; i < packages.length; i++) {
							var thePackage = packages.get(i);
							var selectFLag = thePackage.get("SELECT_FLAG");
							var groupId = thePackage.get("LABEL_ID");
							if (selectFLag == "0") {
								var isHas = false;
								for (var j = 0; j < this.selectedEls.length; j++) {
									var temp = this.selectedEls.get(j);
									if ((temp.get("MODIFY_TAG") == "0" || temp.get("MODIFY_TAG") == "exist" || temp.get("MODIFY_TAG") == "2")
											&& (temp.get("PACKAGE_ID") == groupId || temp.get("NEW_PACKAGE_ID") == groupId)) {
										isHas = true;
										break;
									}
								}
								if (!isHas) {
									alert("包" + thePackage.get("LABEL_NAME") + "是必选包，必须添加该包下的至少一个元素");
									return false;
								}
							}
						}
					}
				}
				return true;
			},

			errProcess : function(errorCode, errorinfo) {
				this.hiddenAttr();
				alert(errorinfo);
				$.endPageLoading();
			},
			errProcessReverse : function(errorCode, errorInfo, elCheckBox) {
				this.hiddenAttr();
				alert(errorInfo);
				elCheckBox.click();
				$.endPageLoading();
			},

			effectNow : function() {
				this.isEffectNow = true;
				var length = this.selectedEls.length;
				for (var i = 0; i < length; i++) {
					var temp = this.selectedEls.get(i);
					if (temp.get("MODIFY_TAG") == "0" || temp.get("MODIFY_TAG") == "1" || temp.get("MODIFY_TAG") == "0_1") {
						if ((temp.get("START_DATE").substring(0, 10) == temp.get("EFFECT_NOW_START_DATE").substring(0, 10))
								&& temp.get("MODIFY_TAG") == "0") {
							continue;
						}
						if ((temp.get("END_DATE").substring(0, 10) == temp.get("EFFECT_NOW_END_DATE").substring(0, 10))
								&& temp.get("MODIFY_TAG") == "1") {
							continue;
						}
						temp.put("OLD_EFFECT_NOW_START_DATE", temp.get("START_DATE"));
						temp.put("START_DATE", temp.get("EFFECT_NOW_START_DATE"));
						temp.put("OLD_EFFECT_NOW_END_DATE", temp.get("END_DATE"));
						temp.put("END_DATE", temp.get("EFFECT_NOW_END_DATE"));
						if (temp.get("ELEMENT_TYPE_CODE") == "D") {
							this.resetDate(temp);
						}
					}
				}
			},
			unEffectNow : function() {
				this.isEffectNow = false;
				var length = this.selectedEls.length;
				for (var i = 0; i < length; i++) {
					var temp = this.selectedEls.get(i);
					if (temp.get("MODIFY_TAG") == "0" || temp.get("MODIFY_TAG") == "1" || temp.get("MODIFY_TAG") == "0_1") {
						if ((temp.get("OLD_EFFECT_NOW_START_DATE") == null || temp.get("START_DATE").substring(0, 10) == temp.get(
								"OLD_EFFECT_NOW_START_DATE").substring(0, 10))
								&& temp.get("MODIFY_TAG") == "0") {
							continue;
						}
						if ((temp.get("OLD_EFFECT_NOW_END_DATE") == null || temp.get("END_DATE").substring(0, 10) == temp.get(
								"OLD_EFFECT_NOW_END_DATE").substring(0, 10))
								&& temp.get("MODIFY_TAG") == "1") {
							continue;
						}
						temp.put("START_DATE", temp.get("OLD_EFFECT_NOW_START_DATE"));
						temp.put("END_DATE", temp.get("OLD_EFFECT_NOW_END_DATE"));
						if (temp.get("ELEMENT_TYPE_CODE") == "D") {
							this.resetDate(temp);
						}
					}
				}
			},

			find : function(name) {
				return $("#" + this.id).find(name);
			},
			afterAction : function(objId, data) {
				var action = this.find("#" + objId).val();
				if (action != "" && action != "undefined") {
					var reg = /\(data\)/;
					if (!reg.test(action) && data) {
						eval(action + "(data)");
					} else {
						eval(action);
					}
				}
			},
			checkIsExist: function(elementId,elementType){
				var length = this.selectedEls.length;
				for(var i=0;i<length;i++){
					var selectedEl = this.selectedEls.get(i);
					if(selectedEl.get("ELEMENT_ID")==elementId&&selectedEl.get("ELEMENT_TYPE_CODE")==elementType){
						return true;
					}
				}
				return false;
			},
			modifyTime : function(itemIndex) {
				var offer = this.selectedEls.get(itemIndex);
				if (offer && offer != null) {
					var bindObj = this.getBindIdObject("time");
					if(bindObj) {
						var that = this;
						bindObj.render(offer, function() {
							var itemIndex = offer.get("ITEM_INDEX");
							that.find("#" + itemIndex + "_START_DATE").html(offer.get("START_DATE").substring(0, 10));
							that.find("#" + itemIndex + "_END_DATE").html(offer.get("END_DATE").substring(0, 10));
//							$(obj).siblings("[name=" + offerCode + "_" + offerType + "_START_DATE" + "]").html(offer.get("START_DATE").substring(0, 10));
//							$(obj).siblings("[name=" + offerCode + "_" + offerType + "_END_DATE" + "]").html(offer.get("END_DATE").substring(0, 10));
						});
					}
				}
			}
		};
		SelectedElements.fn.init.prototype = SelectedElements.prototype;
	}
}();
