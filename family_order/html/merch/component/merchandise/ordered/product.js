!function() {
	if (typeof (UserProduct) == "undefined") {
		UserProduct = function(id) {
			return new UserProduct.fn.init(id);
		};
		UserProduct.fn = UserProduct.prototype = {
			TIME_SEPARATOR : " ~ ",
			init : function(id) {
				this.clean();
				this.id = id;
			},
			getMainUl : function() {
				if (this.mainUl) {
					return this.mainUl;
				}
				this.mainUl = $("#" + this.id).find("#mainUl");
				return this.mainUl;
			},
			getListDiv : function() {
				if (this.listDiv) {
					return this.listDiv;
				}
				this.listDiv = $("#" + this.id).find(".c_list");
				return this.listDiv;
			},
			renderProduct : function(product, nextProduct) {
				var flag = false;
				if (product) {
					this.product = product;
					this.showProductContent("oldMain", product.get("PRODUCT_ID"), product.get("PRODUCT_NAME"), product.get("PRODUCT_DESC"), product
							.get("START_DATE"), product.get("END_DATE"));
				}
				if (nextProduct) {
					flag = true;
					this.nextProduct = nextProduct;
					this.showProductContent("newMain", nextProduct.get("PRODUCT_ID"), nextProduct.get("PRODUCT_NAME"), nextProduct
							.get("PRODUCT_DESC"), nextProduct.get("START_DATE"), nextProduct.get("END_DATE"));
				}

				if (flag) {
					this.resetBtnStyle("none", "none");
				} else {
					this.resetBtnStyle("", "none");
				}
			},
			changeProduct : function(newProductId, newProductName, newProductDesc, startDate, endDate) {
				this.showProductContent("newMain", newProductId, newProductName, newProductDesc, startDate, endDate);
				this.newProductId = newProductId;
				this.resetBtnStyle("none", "");
				this.modifyOldProductEndDate(startDate);
			},
			resetProduct : function(resetAction) {
				var mainUl = this.getMainUl();
				mainUl.find("#newMain").css("display", "none");
				mainUl.find("#oldMain").css("display", "");
				var part = mainUl.find("#newMain");
				part.find("div[name=productName]").html("");
				part.find("div[name=productDesc]").html("");
				part.find("div[name=productTime]").html("");
				this.getListDiv().removeClass("c_list-col-2");
				this.newProductId = null;
				this.resetBtnStyle("", "none");
				if (resetAction) {
					eval(resetAction);
				}
				this.modifyOldProductEndDate();
			},
			modifyOldProductEndDate : function(startDate) {
				var endDate = "";
				if (startDate) {
					if (dateUtils) {
						endDate = dateUtils.addSeconds(-1, startDate);
					}
				} else {
					endDate = this.product.get("END_DATE");
				}
				if (endDate != "") {
					endDate = this.product.get("START_DATE").substring(0,10) + this.TIME_SEPARATOR + endDate.substring(0,10);
					var part = this.getMainUl().find("#oldMain");
					part.find("div[name=productTime]").html(endDate);
				}
			},
			showProductContent : function(partId, productId, productName, productDesc, startDate, endDate) {
				var mainUl = this.getMainUl();
				var part = mainUl.find("#" + partId);
				var time = "";
				if (productId == "") {
					productName = "", productDesc = "", time = "";
				} else {
					productName = "[" + productId + "]" + productName, time = startDate.substring(0, 10) + this.TIME_SEPARATOR + endDate.substring(0, 10);
				}
				if (partId == "newMain") {
					productName = '<span class="e_green">变更为： </span> ' + productName;
					time = '<span class="e_blue">生效时间</span> ' + time;
				}

				part.find("div[name=productName]").html(productName);
				var productDescObj = part.find("div[name=productDesc]");
				productDescObj.html(productDesc);
				productDescObj.attr("title", productDesc);
				part.find("div[name=productTime]").html(time);
				part.css("display", "");
				if ("oldMain" == partId) {
					mainUl.find("#newMain").css("display", "none");
					this.getListDiv().removeClass("c_list-col-2");
				} else {
					// mainUl.find("#oldMain").css("display", "none");
					this.getListDiv().addClass("c_list-col-2");
				}
			},
			disableEffectNow : function(flag) {
				$("#" + this.id).find("#EFFECT_NOW").attr("disabled", flag);
			},
			disable : function(flag) {
				$("#" + this.id).find("#changeButton").attr("disabled", flag);
			},
			disableResetBtn : function(flag) {
				$("#" + this.id).find("#resetButton").attr("disabled", flag);
			},
			resetBtnStyle : function(changeFlag, resetFlag) {
				var mainUl = this.getMainUl();
				mainUl.find("#changeButton").css("display", changeFlag);
				mainUl.find("#resetButton").css("display", resetFlag);
			},
			displayEffectNow : function(flag) {
				$("#" + this.id).find("#EFFECT_NOW").parents(".fn").css("display", flag);
			},
			clean : function() {
				this.clearDiv();
				this.id = null;
				this.mainUl = null;
				this.listDiv = null;
				this.product = null;
				this.nextProduct = null;
				this.newProductId = null;
			},
			clearDiv : function() {
				var mainUl = this.getMainUl();
				if (mainUl) {
					mainUl.find("div.main").find("div").html("");
					this.resetBtnStyle("", "none");
				}
			},
			effectProduct : function(effect, startDate, endDate) {
				var part = this.getMainUl().find("#newMain");
				var time = startDate + this.TIME_SEPARATOR + endDate;
				
				if (effect) {
					time = '<span class="e_blue">立即生效</span> ' + time;
				} else {
					time = '<span class="e_blue">预约生效</span> ' + time;
				}
				part.find("div[name=productTime]").html(time);

			},
			effectOldProduct : function(effect, startDate, endDate) {
				var part = this.getMainUl().find("#oldMain");
				var time = startDate + this.TIME_SEPARATOR + endDate;
				part.find("div[name=productTime]").html(time);
			},
			showBookDate : function(sysdate) {
				$("#" + this.id).find("#BOOKING_DATE").val(sysdate);
			}
		};
		
		UserProduct.fn.init.prototype = UserProduct.prototype;
	}
}();
