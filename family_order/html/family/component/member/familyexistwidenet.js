function WidenetExsit(tplObj) {
	Role.apply(this, [ "2", tplObj.type, tplObj.partId ]);
	var tpl = tplObj.tpl;
	var mainSn = tplObj.sn;//当前办理的角色手机号码，上一级传过来
	var eparchyCode = tplObj.eparchyCode;
	var type = tplObj.type;
	var partId = tplObj.partId;
	var isFmMgr = tplObj.isFmMgr;
	this.type = type;
	this.sn = tplObj.oldWideSn;//当前角色的宽带号码
	this.mainSn = mainSn;
	this.partId = partId;
	this.eparchyCode = eparchyCode;
	this.isNeedSelectOffer = true;
	this.share = false;
	this.isFmMgr = isFmMgr;//管理员标志  true:管理员  false:非管理员
	this.initDomTree(tpl);// 加载模板，画出宽带页面
	this.initinfos(type);
	this.bindEvents();

}

WidenetExsit.tpl = 'family/component/tpl/familyexistwide.tpl';

WidenetExsit.prototype = {

	// 初始化方法
	initinfos : function(type) {
		var that = this;
		that.find("input[name=WIDE_ACCT_ID]").val(that.sn);// 目前都是一个手机下一条宽带
		hhSubmit(null, window.constdata.HANDLER, "roleWideInit", "&OPEN_TYPE=" + type + "&WIDE_SERIAL_NUMBER=" + that.sn + "&SERIAL_NUMBER=" + that.mainSn, function(ajaxData) {
			var wideUserId = ajaxData.get("WIDE_USER_ID");
			var detailAddress = ajaxData.get("DETAIL_ADDRESS");
			var standAddress = ajaxData.get("STAND_ADDRESS");
			var standAddressCode = ajaxData.get("STAND_ADDRESS_CODE");
			var wideProductId = ajaxData.get("USER_WIDE_PRODUCT_ID");
			var wideProductName = "【" + wideProductId + "】" + ajaxData.get("USER_WIDE_PRODUCT_NAME");
			var wideProductType = ajaxData.get("USER_WIDE_PRODUCT_TYPE");
			var wideProductTypeName = ajaxData.get("USER_WIDE_PRODUCT_TYPE_NAME");
			var userWideProductMode = ajaxData.get("USER_WIDE_PRODUCT_MODE");
			var userWideAreaCode = ajaxData.get("USER_WIDE_AREA_CODE");
			that.find("input[name=USER_WIDE_USER_ID]").val(wideUserId);
			that.find("input[name=STAND_ADDRESS]").val(standAddress);
			that.find("input[name=STAND_ADDRESS_CODE]").val(standAddressCode);
			that.find("input[name=USER_WIDE_PRODUCT_ID]").val(wideProductId);
			that.find("input[name=OFFER_NAME]").val(wideProductName);
			that.find("input[name=USER_WIDE_PRODUCT_TYPE]").val(wideProductType);
			that.find("input[name=USER_WIDE_PRODUCT_TYPE_NAME]").val(wideProductTypeName);
			that.find("input[name=USER_WIDE_PRODUCT_MODE]").val(userWideProductMode);
			that.find("input[name=USER_WIDE_AREA_CODE]").val(userWideAreaCode);
			that.find("input[name=DETAIL_ADDRESS]").val(detailAddress);
			that.find("input[name=DETAIL_ADDRESS]").trigger("input");			
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});
	},

	// 绑定事件
	bindEvents : function() {
		var that = this;
		this.find("input[name=DETAIL_ADDRESS]").bind("input propertychange", function() {
			that.showOfferinit(that);
		})
	},
	
	showOfferinit : function(that) {
		OfferPop.initSelectedOffers(that);// 宽带商品必选元素
	},
	
	checkAddress : function() {
		var standAddress = this.find("input[name=STAND_ADDRESS]").val();
		if (!standAddress) {
			MessageBox.alert("提示信息", "请先选择宽带标准地址！");
			return false;
		}
		return true;
	},

	// 宽带商品根据所选地址过滤
	getFilterOfferParam : function() {
		var that = this;
		var param = $.DataMap();
		param.put("WIDE_PRODUCT_TYPE", that.find("input[name=USER_WIDE_PRODUCT_TYPE]").val());
		param.put("PRODUCT_MODE", that.find("input[name=USER_WIDE_PRODUCT_MODE]").val());
		return param;
	},

	// 提交数据组装
	getImportData : function() {
		var that = this;
		var roleType = that.find("input[name=ROLE_TYPE]").val();
		var param = $.DataMap();
		var elements = that.getOffers();
		// 公共参数
		param.put("ROLE_CODE", "2");
		param.put("ROLE_TYPE", roleType);
		param.put("USER_PRODUCT_ID", that.find("input[name=USER_WIDE_PRODUCT_ID]").val());//宽带现有产品id
		param.put("WIDE_USER_ID", that.find("input[name=USER_WIDE_USER_ID]").val());
		param.put("WIDE_TYPE", that.find("input[name=USER_WIDE_PRODUCT_TYPE]").val());

		return param;
	},

	checkSubmitData : function(data) {
		var that = this;
		var roleType = that.find("input[name=ROLE_TYPE]").val();
		var wideaid = that.find("input[name=WIDE_ACCT_ID]").val();
		var serialNum = that.find("input[name=SERIAL_NUMBER]").val();

		var wideProductId = that.getWideProductId();
		if (wideProductId == "") {
			MessageBox.alert("提示信息", "请先选择宽带产品！");
			return false;
		}
		
		// 固话和tv检验
		var subRoles = that.getSubRoles();
		if (subRoles != null && subRoles.length > 0) {
			for (var i = 0; i < subRoles.length; i++) {
				var subRole = subRoles[i];
				if (!subRole.isEmpty) {
					var imsAndTvCheckResult = subRole.checkSubmitData();
					if (!imsAndTvCheckResult) {
						return false;
					}
				}
			}
		}

		// 魔百和调测费费用检验
		if (!that.checkModelFee(that)) {
			return false;
		}
		;

		return true;
	},

	getSn : function() {
		return this.sn;
	},

	getEparchyCode : function() {
		return this.eparchyCode;
	},

	getRefreshTplData : function() {
		var that = this;
		// 填充模板数据
		var btnStrs = 'x';
		var wideTitle = "已有";
		var roleBtnStr = $.DatasetList();
		// 已有宽带，需要检验宽带下有没有固话
		hhSubmit(null, window.constdata.HANDLER, "checkIsHasChildrenRole", "&SERIAL_NUMBER=" + this.mainSn + "&ROLE_CODE=" + this.roleCode, function(ajaxData) {
			var ishasIms = ajaxData.get("IS_HAS_IMS");
			var ishasTv = ajaxData.get("IS_HAS_TV");
			if ("Y" == ishasIms) {// 有固话
				roleBtnStr.add('3_OLD');
			} else {
				if (that.isFmMgr) {
					roleBtnStr.add('3_NEW');
				}
			}

			if ("Y" == ishasTv) {// 有TV
				roleBtnStr.add('4_OLD');
			} else {
				if (that.isFmMgr) {
					roleBtnStr.add('4_NEW');
				}
			}
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		}, {
			async : false
		});

		btnStrs += OfferPop.getExistOffersRoleTypeStrs(roleBtnStr); // 家庭商品过滤角色
		var data = {
			title : wideTitle,
			sn : this.mainSn,
			isLtIE9 : window.constdata.isLtIE9,
			changeOffer : '商品选择',
			btnStrs : btnStrs,
			roletype : this.type
		};

		return data;
	},

	createSubRole : function(roleCode, roleType) {
		var that = this;
		if (!roleCheck.checkRoleCountLimit(roleCode)) {
			return false;
		}

		// 每个手机成员下检验固话和tv数量，暂时都是办理一个
		if (that.checkRoleRule(roleCode, roleType)) {
			var roleTitle = window.constdata.busiTitle[roleCode];
			MessageBox.alert(that.sn + "已添加" + roleTitle + "成员,不能重复添加！");
			return null;
		}

		//已有宽带,新开固话和TV，规则检验     已有固话和TVpop弹出框，选择完对应号码后会检验
		if (roleType == "NEW" && !that.checkIsCanAddImsAndTv(roleCode, roleType)) {//新增固话和tv规则检验
			return false;
		}

		var wideProductType = that.find("input[name=USER_WIDE_PRODUCT_TYPE]").val();
		var areaCode = that.find("input[name=USER_WIDE_AREA_CODE]").val();
//		var wideProductId = that.find("input[name=USER_WIDE_PRODUCT_ID]").val(); 已有宽带当前宽带产品id

		if (roleCode == "4") {// TV		 已有Tv会在tv页面创建后，选择产品会弹出已有TVpop框
			var wideProductId = that.getWideProductId();//已有宽带需要变更的新宽带产品
			if (wideProductId == "") {
				MessageBox.alert("提示信息", "请先选择宽带产品！");
				return false;
			}

			var newTv = common.createRole(TopsetBox, {
				partId : that.partId,
				sn : that.mainSn,
				eparchyCode : that.eparchyCode,
				roleType : roleType,
				wideProductType : wideProductType,
				wideProductId : wideProductId
			});
			return newTv;
		} else if (roleCode == "3") {// 固话
			//
			if ('3' != wideProductType && '5' != wideProductType) {
				MessageBox.alert("提示", "只有FTTH宽带才能办理固话业务，新选择的地址不支持将取消当前所选的固话业务！");
				return null;
			}

			if (roleType == "NEW") {
				// 测试用
				var newIms = common.createRole(Familynewims, {
					partId : that.partId,
					sn : that.mainSn,
					eparchyCode : that.eparchyCode,
					type : roleType,
					areaCode : areaCode
				});
				return newIms;
			} else if (roleType == "OLD") {
				selectExistRole.showPop(that, roleCode, roleType);//已有固话pop框
			}

		}

	},

	// 检验角色
	checkRoleRule : function(roleCode, roleType) {
		var subRoles = this.getSubRoles();
		if (subRoles != null && subRoles.length > 0) {
			for (var i = 0; i < subRoles.length; i++) {
				var subRole = subRoles[i];
				if (!subRole.isEmpty) {
					var tempRoleCode = subRole.roleCode;
					if (tempRoleCode == roleCode) {
						return true;
					}

				}
			}
		}

		return false;
	},

	checkModelFee : function(that) {
		var topSetBoxSaleActiveFee2 = 0;// 魔百和调测费
		var subRoles = that.getSubRoles();
		if (subRoles != null && subRoles.length > 0) {
			for (var i = 0; i < subRoles.length; i++) {
				var subRole = subRoles[i];
				if (!subRole.isEmpty && subRole.roleCode == "4") {
					var subRoleTopSetFee = subRole.getTopSetActiveFee();
					if (null == subRoleTopSetFee || '' == subRoleTopSetFee) {
						subRoleTopSetFee = '0';
					}
					topSetBoxSaleActiveFee2 += parseFloat(subRoleTopSetFee);
				}
			}
		}

		var feeFlag = true;
		var totalFee = parseFloat(topSetBoxSaleActiveFee2) / 100;

		if (totalFee > 0) {
			feeFlag = false;
			var tips = "您总共需要从现金存折中转出：" + totalFee + "元,其中魔百和调测费：" + parseFloat(topSetBoxSaleActiveFee2) / 100 + "元";

			// 宽带付费方式 A：先装后付，P：立即付费
			var widePayMode = that.find("input[name=WIDENET_PAY_MODE]").val();

			if ("A" == widePayMode) {
				tips = "本次家庭宽带开户总费用为：" + totalFee + "元,其中魔百和调测费：" + parseFloat(topSetBoxSaleActiveFee2) / 100 + "元，费用将在施工完成后收取";
			}

			if ("A" == widePayMode) {
				feeFlag = true;
			} else {
				feeFlag = that.checkFeeBeforeSubmit(topSetBoxSaleActiveFee2);
			}

		}
		return feeFlag;
	},

	// 提交前费用校验
	checkFeeBeforeSubmit : function(topSetBoxSaleActiveFee2) {
		var that = this;
		var result = false;

		// 后台余额校验
		var param = "&TOPSETBOX_SALE_ACTIVE_FEE2=" + topSetBoxSaleActiveFee2 + "&SERIAL_NUMBER=" + that.mainSn;
		$.beginPageLoading("提交前费用校验中。。。");
		hhSubmit(null, window.constdata.HANDLER, "checkFeeBeforeSubmit", param, function(data) {
			$.endPageLoading();
			var resultCode = data.get("X_RESULTCODE");
			if (resultCode == '0')
				result = true;
		}, function(error_code, error_info) {
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
			result = false;
		}, {
			async : false
		});

		return result;
	},

	checkIsCanAddImsAndTv : function(roleCode, roleType) {
		$.beginPageLoading("规则检验中...");
		var that = this;
		var flag = false;
		var param = "&SERIAL_NUMBER=" + that.mainSn + "&ROLE_CODE=" + roleCode + "&ROLE_TYPE=" + roleType + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
		hhSubmit(null, constdata.HANDLER, "checkAddChilrenRole", param, function(data) {
			flag = true;
			$.endPageLoading();
		}, function(error_code, error_info) {
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		}, {
			async : false
		});

		return flag;
	},
	
	//获取需要变更的宽带产品id,有可能不需要变更
	getWideProductId : function() {
		var that = this;
		var wideNewProductId = "";
		var elements = that.getOffers();
		var length = elements.length;
		for (var i = 0; i < length; i++) {
			var selectedEl = elements.get(i);
			var elementTypeCode = selectedEl.get("ELEMENT_TYPE_CODE");
			var elementId = selectedEl.get("ELEMENT_ID");
			if (elementTypeCode == "P") {
				wideNewProductId = elementId;
				break;
			}
		}
		return wideNewProductId;
	}

}
