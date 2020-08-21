(function() {
	$.extend({
		itemcollect : {
			tab : {
				currentTitle : "choose",
				switchTitle : function(e) {
					if(!itemList.mainProductId){
						return ;
					}
					var obj = $(e.currentTarget);

					var id = obj.attr("tab-it-col-li");

					if (this.currentTitle != id) {
						this.chooseTitleClass(obj, id);
						this.currentTitle = id;
					} else {
						return false;
					}
				},
				resetItemListHeight : function(isShow) {
					var div = $("#OFFERLIST_DRAWAREA").parent();
					if (!this.offerset)
						this.offerset = div.parent().height() - div.outerHeight() - div.next().outerHeight();
					if (isShow) {
						div.height(div.height() - this.offerset);
						$("#LABEL_PART").css("display", "");
					} else {
						div.height(div.height() + this.offerset);
						$("#LABEL_PART").css("display", "none");
					}
				},
				chooseTitleClass : function(obj, id) {
					$(obj).siblings().attr("class", "");
					$(obj).attr("class", "on");
					if (id == "choose") {// 带选区
						itemList.iscollect = false;
						this.resetItemListHeight(true);
						if (itemList.currentLabel) {
							var labelId = itemList.currentLabel.labelId;
							var labelType = itemList.currentLabel.labelType;
							var labelObj = $("li[labelId=" + labelId + "][labelType=" + labelType + "]");
							itemList.switchLabel(labelObj);
						} else {
							var drawArea = $("#OFFERLIST_DRAWAREA");
							drawArea.empty();
						}
					} else {// 收藏区
						itemList.iscollect = true;
						this.resetItemListHeight(false);
						var label = itemList.labelMaps.get("C_C");
						if (!label) {
							label = new Label();
							label.labelId = "C"
							label.labelName = "收藏";
							label.labelType = "C";
							label.offerList = [];
							itemList.labelMaps.put("C_C", label);
						}
						if (label.offerList.length > 0) {// 暂时不考虑对于没有收藏过的用户查询多次的情况
							label.draw();
						} else {
							$("#OFFERLIST_DRAWAREA").empty();
							var params = "&PRODUCT_ID=" + itemList.mainProductId + "&EPARCHY_CODE=" + itemList.eparchyCode;
							hhSubmit(null, itemList.page, "queryCollectOffers", params, function(datas) {
								itemList.refreshElementList(datas, label);
							}, function(error_code, error_info) {
								$.endPageLoading();
								alert(error_info);
							});
						}
					}
				}
			},
			createItemCollectContextMenu : function() {
				var html = [];
				html.push('<div id="itemCollect_contextMenu" class="c_contextMenu" style="z-index: 9999; left: 0px; top: 0px;display:none;">');
				html.push('<div class="content">');
				html.push('<div id="itemCollect_contextMenu_content" class="wrapper">');
				html.push('<ul group="itemCollect">');
				html.push('<li id="collect-li"><a href="javascript:void(0)" id="collect" title="收藏" utitle="收藏">收藏</a></li>');
				html.push('<li id="cancelCollect-li"><a href="javascript:void(0)" id="cancelCollect" title="取消收藏" utitle="取消收藏">取消收藏</a></li>');
				html.push('</ul></div></div>');
				html.push('<div class="shadow"></div>');
				html.push('</div>');
				$(document.body).append(html.join(''));
				$("#collect").bind("click", function() {
					$.itemcollect.collect();
				});
				$("#cancelCollect").bind("click", function() {
					$.itemcollect.cancelCollect();
				});
			},
			showContextMenu : function(obj) {
				var t = event;
				var a = $("#itemCollect_contextMenu");
				if (!a || a.length < 1) {
					this.createItemCollectContextMenu();
				}
				var o = $(obj).find("input[name=elementCheckBox]");
				a.data("ELEMENT_ID", o.val());
				a.data("ELEMENT_TYPE_CODE", o.attr("elementtype"));
				var d = $(document.body).width(),
				p = $(document.body).height(), 
				c = a.width(), 
				u = a.height(),
				v = t.clientX, 
				f = t.clientY;
				c > d && (c = 160), 
				d - c < t.clientX && (v = t.clientX - c),
				p - u < t.clientY && (f = t.clientY - u);
				
				var left = v + "px";
				var top = f + "px";
				if (this.tab.currentTitle == "choose") {
					$("#collect-li").css("display", "");
					$("#cancelCollect-li").css("display", "none");
				} else {
					$("#collect-li").css("display", "none");
					$("#cancelCollect-li").css("display", "");
				}
				a.css({
					"left" : left,
					"top" : top,
					"z-index" : "9999",
					"display" : ""
				});
				return false;
			},
			hideContextMenu : function(t) {
				$("#itemCollect_contextMenu").css({
					"left" : "-1000px",
					"top" : "-1000px",
					"z-index" : "-1000",
					"display" : "none"
				});
				return false;
			},
			collect : function() {
				var obj = $("#itemCollect_contextMenu");
				var eleId = $(obj).data("ELEMENT_ID");
				var eleType = $(obj).data("ELEMENT_TYPE_CODE");
				var params = "&ELEMENT_ID=" + eleId + "&ELEMENT_TYPE_CODE=" + eleType + "&EPARCHY_CODE=" + itemList.eparchyCode;
				hhSubmit(null, itemList.page, "collectOffer", params, function(data) {
					if (data.get("RESULT_CODE") == "1") {
						alert("收藏成功");
					} else {
						alert("收藏失败");
					}
					$.itemcollect.hideContextMenu();
					$.itemcollect.cleanOfferList();
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
				});
			},
			cancelCollect : function() {
				var obj = $("#itemCollect_contextMenu");
				var eleId = $(obj).data("ELEMENT_ID");
				var eleType = $(obj).data("ELEMENT_TYPE_CODE");
				var params = "&ELEMENT_ID=" + eleId + "&ELEMENT_TYPE_CODE=" + eleType + "&EPARCHY_CODE=" + itemList.eparchyCode;
				hhSubmit(null, itemList.page, "cancelCollectOffer", params, function(data) {
					if (data.get("RESULT_CODE") == "1") {
						alert("取消收藏成功");
					} else {
						alert("取消收藏失败");
						return false;
					}
					$.itemcollect.hideContextMenu();
					$("#PELI_" + eleType + "_" + eleId).remove();
					var label = itemList.labelMaps.get("C_C");
					if (label && label.offerList.length > 0) {// l.splice
						for (i = 0; i < label.offerList.length; i++) {
							var offer = label.offerList[i];
							if (eleId == offer.elementId && eleType == offer.elementType) {
								label.offerList.splice(i, 1);
								return;
							}
						}
					}
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
				});
			},
			cleanOfferList : function() {// 新增收藏或者取消收藏后清空保存数据
				var label = itemList.labelMaps.get("C_C");
				if (label) {
					label.offerList = [];
				}
			}
		}
	});
	$(document).ready(function() {
		$(document.body).bind("contextmenu", function(e) {
			// window.event.returnValue=false;
			return false;
		});
		$(document).bind("click", function() {
			try {
				$.itemcollect.hideContextMenu();
			} catch (a) {
				return;
			}
		});
		$("li[tab-it-col-li]").bind("click", function(event) {
			$.itemcollect.tab.switchTitle(event);
		});
	});
})();
