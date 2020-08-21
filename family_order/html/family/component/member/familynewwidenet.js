function WidenetNew(tplObj) {
	Role.apply(this, [ "2", tplObj.type, tplObj.partId ]);
	var tpl = tplObj.tpl;
	var mainSn = tplObj.sn;// 当前办理的角色手机号码，上一级传过来
	var eparchyCode = tplObj.eparchyCode;
	var type = tplObj.type;
	var partId = tplObj.partId;
	var isFmMgr = tplObj.isFmMgr;
	this.type = type;
	this.sn = "";// 当前角色的宽带号码
	this.mainSn = mainSn;
	this.partId = partId;
	this.eparchyCode = eparchyCode;
	this.isNeedSelectOffer = true;
	this.share = false;
	this.isFmMgr = isFmMgr;//管理员标志  true:管理员  false:非管理员
	this.initDomTree(tpl);// 加载模板，画出宽带页面
	this.initOtherInfos(this.uniqueId, type);
	this.initinfos(type, mainSn, this.uniqueId);
	this.bindEvents();
	this.SALE_ACTIVE_LIST2 = $.DatasetList();// 光猫调测费数据源

}

WidenetNew.tpl = 'family/component/tpl/familynewwidenet.tpl';

WidenetNew.prototype = {
	initOtherInfos : function(idx, type) {
		var that = this;
		// 初始化日期组件
		window["SUGGEST_DATE_" + that.uniqueId] = new Wade.DateField("SUGGEST_DATE_" + that.uniqueId, {
			dropDown : true,
			now : new Date(),
			value : new Date(),
			format : "yyyy-MM-dd HH:mm:ss",
			useTime : false,
			useLunar : false
		});

		that.find("li[name=userproduct]").css('display', 'none');
		that.find("li[name=userproductType]").css('display', 'none');

	},
	// 初始化方法
	initinfos : function(type, sn, idx) {
		var that = this;
		that.sn = "KD_" + that.mainSn;
		that.find("input[name=WIDE_ACCT_ID]").val(that.sn);// 目前都是一个手机下一条宽带,后续根据业务发展，可能一个手机多条宽带，加载完宽带子页面，再回填宽带账号
		// 地区
		hhSubmit(null, window.constdata.HANDLER, "roleWideInit", "&SERIAL_NUMBER=" + sn + "&OPEN_TYPE=" + type, function(ajaxData) {
			if (ajaxData && ajaxData.length > 0) {
				// 地区
				$.Select.after(
				// 对应元素，在 el 元素下生成下拉框，el 可以为元素 id，或者原生 dom对象
				"mywideAreaCode_" + idx,
				// 参数设置
				{
					id : "mywideArea_" + idx,
					name : "AREA_CODE",
					style : "width: 100%",
					textField : "DATA_NAME",
					valueField : "DATA_ID",
					addDefault : true,
					defaultText : "--请选择--"
				},
				// 数据源，可以为 JSON 数组，或 JS 的 DatasetLsit 对象
				ajaxData.get("WIDE_AREA_CODE"));

				// 支付模式
				$.Select.after("mySelectWidePayMode_" + idx, {
					id : "mywidePayMode_" + idx,
					name : "WIDENET_PAY_MODE",
					style : "width: 100%",
					textField : "DATA_NAME",
					valueField : "DATA_ID",
					addDefault : true,
					defaultText : "--请选择--"
				}, ajaxData.get("WIDENET_PAY_MODE_LIST"));

				// 开户方式
				$.Select.after("myWideUserOpenStyle_" + idx, {
					id : "mywideOpenStyle_" + idx,
					name : "HGS_WIDE",
					style : "width: 100%",
					textField : "DATA_NAME",
					valueField : "DATA_ID",
					addDefault : true,
					defaultText : "--请选择--"
				}, ajaxData.get("MERGE_WIDE_LIST"));

				// 宽带产品类型
				$.Select.after("mySelectWideProductType_" + idx, {
					id : "mywideProductType_" + idx,
					name : "WIDE_PRODUCT_TYPE",
					style : "width: 100%",
					textField : "DATA_NAME",
					valueField : "DATA_ID",
					addDefault : true,
					value : "3", // 可选设置，下拉框默认值，必须是数据源中能匹配到的值
					disabled : true,// 可选设置，设置下拉框是否禁用，默认为false
					nullable : "no" // 可选设置，下拉框表单校验是否可以为空属性

				}, ajaxData.get("WIDE_PRODUCT_TYPE"));

				// 光猫调测费加载
				$.Select.append("mySelectModelActive_" + idx, {
					id : "SALE_ACTIVE_ID2_" + idx,
					name : "SALE_ACTIVE_ID2",
					textField : "PARA_CODE3",
					valueField : "PARA_CODE2",
					addDefault : true,
					defaultText : "--请选择--",
					optionWidth : 40,
					style : "width: 100%"
				}, ajaxData.get("MODEL_ACTIVE_SALE"));
				SALE_ACTIVE_LIST2 = ajaxData.get("MODEL_ACTIVE_SALE");
				// 预约时间段
				that.find("input[name=SUGGEST_DATE_MAX]").val(ajaxData.get("SUGGEST_DATE_MAX"));
				that.find("input[name=SUGGEST_DATE_MIN]").val(ajaxData.get("SUGGEST_DATE_MIN"));
			}

		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});
	},

	// 绑定事件
	bindEvents : function() {
		var that = this;
		this.find("button[name=showAddrPopup]").bind("click", function() {
			if (that.checkRoleRule("4", "NEW")) {
				MessageBox.alert("提示信息", "已添加TV+成员,不可变更地址！");
				return false;
			}
			if (that.checkRoleRule("3", "NEW")) {
				MessageBox.alert("提示信息", "已添加固话成员,不可变更地址！");
				return false;
			}
			that.addressSelect(that);
		});

		//树状地址
		this.find("button[name=showTreeAddrPopup]").bind("click", function() {
			if (that.checkRoleRule("4", "NEW")) {
				MessageBox.alert("提示信息", "已添加TV+成员,不可变更地址！");
				return false;
			}
			if (that.checkRoleRule("3", "NEW")) {
				MessageBox.alert("提示信息", "已添加固话成员,不可变更地址！");
				return false;
			}
			that.addressTreeSelect(that);
		});
		// 预约时间时间
		this.find("input[name=SUGGEST_DATE]").bind("afterAction", function() {
			that.afterSelSuggestDate(that);
		});

		// 调测费活动校验
		this.find("#mySelectModelActive_" + this.uniqueId).bind("change", function() {
			that.checkModelActive(that);
		});

		// 报装地址检验
		this.find("input[name=DETAIL_ADDRESS]").bind("change", function() {
			that.checkDetailAddress(this);
		});
		
		// 联系人检验
		this.find("input[name=CONTACT_PHONE]").bind("keyup", function() {
			that.checkExpression(this);
		});
		
		// 联系电话检验
		this.find("input[name=PHONE]").bind("keyup", function() {
			that.checkExpression(this);
		});

	},

	checkAddress : function() {
		var standAddress = this.find("input[name=STAND_ADDRESS]").val();
		if (!standAddress) {
			MessageBox.alert("提示信息", "请先选择宽带标准地址！");
			return false;
		}
		return true;
	},
	
	checkDetailAddress : function(textbox) {
		var pattern=new RegExp("[`~!#$^&*()=|{}':;',\\[\\].<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？‘'@%]");
		var textboxvalue = textbox.value;
		var rs="";
		for(var i=0;i<textboxvalue.length;i++){
			rs=rs+textboxvalue.substr(i,1).replace(pattern,'');
		}		
		textbox.value=rs;
	},

	
	checkExpression : function(textbox) {
		textbox.value = textbox.value.replace(/[^\d]/g,'');
	},
	
	// 宽带商品根据所选地址过滤
	getFilterOfferParam : function() {
		var that = this;
		var param = $.DataMap();
		param.put("WIDE_PRODUCT_TYPE", that.find("input[name=WIDE_PRODUCT_TYPE]").val());
		return param;
	},

	addressSelect : function(that) {
		addressAdepter.popupPageAddressPage(that,"0");
	},

	addressTreeSelect : function(that) {
		addressAdepter.popupPageAddressPage(that,"1");
	},	
	
	// 选择预约时间后校验
	afterSelSuggestDate : function(that) {
		var suggerDate = that.find("input[name=SUGGEST_DATE]").val();
		var min = that.find("input[name=SUGGEST_DATE_MIN]").val();
		var chooseDate = new Date(Date.parse(suggerDate));
		var minDate = new Date(Date.parse(min));
		if (chooseDate < minDate) {
			$.MessageBox.alert('提示', '预约受理时间不能小于' + min + '!');
			that.find("input[name=SUGGEST_DATE]").val('');
			return;
		}

		// 判断预约选择的时间是否在工作时间范围内，每天8点-16点为工作时间
		var strHour = suggerDate.substr(11, 2);
		var h = Number(strHour);
		if (h < 8 || h >= 16) {
			$.MessageBox.alert('提示', '预约施工时间为每天8：00--16：00,请重新选择时间!');
			that.find("input[name=SUGGEST_DATE]").val('');
			return;
		}
	},

	// 光猫营销活动活动检验
	checkModelActive : function(that) {
		var saleActiveId = that.find("input[name=SALE_ACTIVE_ID2]").val();// 光猫调测费
		if (null != saleActiveId && '' != saleActiveId) {
			// //产品组件已选取元素列表
			var discntIds = '';
			var serviceIds = '';

			// 营销活动类型
			var saleActiveCampnType = '';

			// 营销方案ID
			var saleActiveProductId = '';

			// 营销包ID
			var saleActivePackageId = '';

			// 营销活动描述
			var saleActiveExplain = '';

			// 营销活动预存费用
			var saleActiveFee = '';

			if (null != SALE_ACTIVE_LIST2) {
				for (var i = 0; i < SALE_ACTIVE_LIST2.length; i++) {
					if (saleActiveId == SALE_ACTIVE_LIST2.get(i).get('PARA_CODE2')) {
						// 营销活动类型
						saleActiveCampnType = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE6");

						// 营销方案ID
						saleActiveProductId = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE4");

						// 营销包ID
						saleActivePackageId = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE5");

						// 营销活动描述
						saleActiveExplain = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE24");

						// 营销活动预存费用
						saleActiveFee = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE7");

						break;
					}
				}
			}

			var para_code1 = $("#WIDE_PRODUCT_ID").val();// 宽带产品id
			var eparchyCode = this.eparchyCode;
			var inparam = "&TRADE_TYPE_CODE=" + "600" + "&ROUTE_EPARCHY_CODE=" + eparchyCode + "&DISCNT_IDS=" + discntIds + "&WIDE_USER_SELECTED_SERVICEIDS=" + serviceIds
					+ "&SERIAL_NUMBER=" + this.mainSn + "&CAMPN_TYPE=" + saleActiveCampnType + "&PRODUCT_ID=" + saleActiveProductId + "&PACKAGE_ID=" + saleActivePackageId
					+ "&PARA_CODE1=" + para_code1;
			$.beginPageLoading("调试费活动校验中。。。");
			hhSubmit(null, window.constdata.HANDLER, "checkSaleActive", inparam, function(data) {
				$.endPageLoading();
				that.find("input[name=SALE_ACTIVE_EXPLAIN2]").val(saleActiveExplain);
				// 宽带营销活动预存
				// 营销活动费用需要重新取产品模型配置
				that.checkWideNetSaleActiveFee2(saleActiveCampnType, saleActiveProductId, saleActivePackageId);
			}, function(error_code, error_info) {
				that.find("input[name=SALE_ACTIVE_EXPLAIN2]").val('');
				that.find("input[name=SALE_ACTIVE_ID2]").val('');
				that.find("input[name=SALE_ACTIVE_FEE2]").val('');
				$.endPageLoading();
				alert(error_info);
			});

		}

	},

	// 光猫调测费费用检验
	checkWideNetSaleActiveFee2 : function(campnType, productId, packageId) {
		var that = this;
		$.beginPageLoading("调测费活动费用校验中。。。");
		var serialNumber = this.mainSn;

		var param = "&SERIAL_NUMBER=" + serialNumber + "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=1";
		hhSubmit(null, window.constdata.HANDLER, "queryCheckSaleActiveFee", param, function(data) {
			$.endPageLoading();
			// 设置营销活动费用
			var fee = data.get("SALE_ACTIVE_FEE");
			that.find("input[name=SALE_ACTIVE_FEE2]").val(fee);
		}, function(error_code, error_info) {
			that.find("input[name=SALE_ACTIVE_EXPLAIN2]").val('');
			that.find("input[name=SALE_ACTIVE_ID2]").val('');
			that.find("input[name=SALE_ACTIVE_FEE2]").val('');
			$.endPageLoading();
			alert(error_info);
		});

	},

	// 提交数据组装
	getImportData : function() {
		var that = this;
		var roleType = that.find("input[name=ROLE_TYPE]").val();
		var param = $.DataMap();
		// 公共参数
		param.put("ROLE_CODE", "2");
		param.put("ROLE_TYPE", roleType);
		param.put("CONTACT", that.find("input[name=CONTACT]").val());// 联系人
		param.put("CONTACT_PHONE", that.find("input[name=CONTACT_PHONE]").val());// 联系人电话
		param.put("DETAIL_ADDRESS", that.find("input[name=DETAIL_ADDRESS]").val());// 报装地址
		param.put("DEVICE_ID", that.find("input[name=DEVICE_ID]").val());// 设备ID
		param.put("FLOOR_AND_ROOM_NUM", that.find("input[name=FLOOR_AND_ROOM_NUM]").val());// 楼层和房号
		param.put("FLOOR_AND_ROOM_NUM_FLAG", that.find("input[name=FLOOR_AND_ROOM_NUM_FLAG]").val());// 楼层和房号
		param.put("HGS_WIDE", that.find("input[name=HGS_WIDE]").val());// 开户方式
		param.put("IS_NEED_MODEM", that.find("input[name=IS_NEED_MODEM]").val());// 是否需要光猫标志(移动FTTH和铁通FTTH需要申请光猫)   1：需要  0：不需要 
//		param.put("IS_WIDEBING", "0");// 是否迁移客户，铁通，暂时不要,默认给0，可以删掉
//		param.put("MODEM_DEPOSIT", that.find("input[name=MODEM_DEPOSIT]").val());// 光猫押金，调测费后没有押金了，默认0  可以删掉
		param.put("MODEM_STYLE", that.find("input[name=MODEM_STYLE]").val());// 是否需要光猫标志(移动FTTH和铁通FTTH需要申请光猫)   0：需要  为空：不需要 
		param.put("OPEN_TYPE", that.find("input[name=OPEN_TYPE]").val());// PBOSS那边传过来的宽带类型
		param.put("PHONE", that.find("input[name=PHONE]").val());// 联系电话
//		param.put("SALE_ACTIVE_EXPLAIN2", that.find("input[name=SALE_ACTIVE_EXPLAIN2]").val());// 光猫调测描述
		param.put("SALE_ACTIVE_FEE2", that.find("input[name=SALE_ACTIVE_FEE2]").val());// 光猫调测费费用
		param.put("SALE_ACTIVE_ID2", that.find("input[name=SALE_ACTIVE_ID2]").val());// 光猫调测费ID
		param.put("STAND_ADDRESS", that.find("input[name=STAND_ADDRESS]").val());// 标准地址
		param.put("STAND_ADDRESS_CODE", that.find("input[name=STAND_ADDRESS_CODE]").val());// 标准地址CODE
		param.put("SUGGEST_DATE", that.find("input[name=SUGGEST_DATE]").val());// 预约时间
		param.put("WIDENET_PAY_MODE", that.find("input[name=WIDENET_PAY_MODE]").val());// 支付模式  A:先装后付P:立即支付
		param.put("WIDE_PRODUCT_TYPE", that.find("input[name=WIDE_PRODUCT_TYPE]").val());//宽带类型
		param.put("AREA_CODE", that.find("input[name=AREA_CODE]").val());

		return param;
	},

	checkSubmitData : function(data) {
		var that = this;
		var roleType = that.find("input[name=ROLE_TYPE]").val();
		var wideaid = that.find("input[name=WIDE_ACCT_ID]").val();
		var serialNum = that.find("input[name=SERIAL_NUMBER]").val();
		var wideProductType = that.find("input[name=WIDE_PRODUCT_TYPE]").val();
		var isBusinessWide = that.find("input[name=IS_BUSINESS_WIDE]").val();
		var isNeedModem = that.find("input[name=IS_NEED_MODEM]").val();
		var wideProductId = that.getWideProductId();
		if (wideProductId == "") {
			MessageBox.alert("提示信息", "请先选择宽带产品！");
			return false;
		}
		
		// 移动FTTH和铁通FTTH需要申请光猫
		if (('3' == wideProductType || '5' == wideProductType) && '0' != isNeedModem && 'Y' != isBusinessWide ) {
			var modemStyle = that.find("input[name=SALE_ACTIVE_ID2]").val();

			if (null == modemStyle || '' == modemStyle) {
				MessageBox.alert("告警提示", "FTTH宽带必须办理调试费活动！");

				return false;
			}
		}

		var standAddress = that.find("input[name=STAND_ADDRESS]").val();
		if (standAddress == null || undefined == standAddress || standAddress == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的标准地址不能为空，请重新选择！");
			return false;
		}

		var widenetPay = that.find("input[name=WIDENET_PAY_MODE]").val();
		if (widenetPay == null || undefined == widenetPay || widenetPay == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的支付模式不能为空，请重新选择！");
			return false;
		}

		var detailAddress = that.find("input[name=DETAIL_ADDRESS]").val();
		if (detailAddress == null || undefined == detailAddress || detailAddress == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的报装地址不能为空，请重新选择！");
			return false;
		}

		var floorNum = that.find("input[name=FLOOR_AND_ROOM_NUM]").val();
		if (floorNum == null || undefined == floorNum || floorNum == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的楼层和房号不能为空，请重新选择！");
			return false;
		}

		var contactPhone = that.find("input[name=CONTACT_PHONE]").val();
		if (contactPhone == null || undefined == contactPhone || contactPhone == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的联系人电话不能为空，请重新选择！");
			return false;
		}

		var contactPerson = that.find("input[name=CONTACT]").val();
		if (contactPerson == null || undefined == contactPerson || contactPerson == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的联系人不能为空，请重新选择！");
			return false;
		}

		var phone = that.find("input[name=PHONE]").val();
		if (phone == null || undefined == phone || phone == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的联系电话不能为空，请重新选择！");
			return false;
		}

		var openStyle = that.find("input[name=HGS_WIDE]").val();
		if (openStyle == null || undefined == openStyle || openStyle == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的开户方式不能为空，请重新选择！");
			return false;
		}

		var openAreaCode = that.find("input[name=AREA_CODE]").val();
		if (openAreaCode == null || undefined == openAreaCode || openAreaCode == "") {
			MessageBox.alert("提示信息", "宽带[" + wideaid + "]的地区不能为空，请重新选择！");
			return false;
		}

		if (roleType == "NEW") {
			var re = /^[0-9]+.?[0-9]*$/; // 判断字符串是否为数字
			if (!re.test(phone)) {
				MessageBox.alert("提示信息", "联系电话只能是数字，请重新选择！");
				var phone = that.find("input[name=CONTACT_PHONE]").val("");
				return false;
			}
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

		// 光猫调测费费用检验
		if (!that.checkModelFee(that)) {
			return false;
		}

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
		var wideTitle = "新开";
		if (that.isFmMgr) {//只有管理员身份才可以新开
			btnStrs += OfferPop.getExistOffersRoleTypeStrs([ '3_NEW', '4_NEW' ]); // 家庭商品过滤角色
		}

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
		var roleTitle = window.constdata.busiTitle[roleCode];

		//非管理员新开按钮已经隐藏掉了，再加下检验
		if (roleType == "NEW" && !that.isFmMgr) {
			MessageBox.alert("提示信息", this.mainSn + "不是管理员号码，不允许新开" + roleTitle);
			return false;
		}

		if (that.find("input[name=STAND_ADDRESS]").val() == "") {
			MessageBox.alert("提示信息", "请先选择宽带标准地址！");
			return false;
		}

		if (!roleCheck.checkRoleCountLimit(roleCode)) {
			return false;
		}

		// 每个手机成员下检验固话和tv数量，暂时都是办理一个
		if (that.checkRoleRule(roleCode, roleType)) {
			MessageBox.alert(that.sn + "已添加" + roleTitle + "成员,不能重复添加！");
			return false;
		}

		//新开宽带
		if (!this.checkIsCanAddImsAndTv(roleCode, roleType)) {//新增固话和tv规则检验
			return false;
		}

		var wideProductType = that.find("input[name=WIDE_PRODUCT_TYPE]").val();
		var areaCode = that.find("input[name=AREA_CODE]").val();
		if (roleCode == "4") {// TV
			var wideProductId = that.getWideProductId();
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
			var newIms = common.createRole(Familynewims, {
				partId : that.partId,
				sn : that.mainSn,
				eparchyCode : that.eparchyCode,
				type : roleType,
				areaCode : areaCode
			});
			return newIms;

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

	setAddrPopupReturnValue : function(obj) {
		var that = this;
		that.find("input[name=DEVICE_ID]").val(obj.DEVICE_ID);
		that.find("input[name=OPEN_TYPE]").val(obj.OPEN_TYPE);
		that.find("input[name=STAND_ADDRESS]").val(obj.STAND_ADDRESS);
		that.find("input[name=INSTALL_ADDR]").val(obj.reginalName);
		that.find("input[name=FLOOR_AND_ROOM_NUM]").val(obj.FLOOR_AND_ROOM_NUM);// 楼层和房号
		that.find("input[name=ADDRESS_BUILDING_NUM]").val(obj.ADDRESS_BUILDING_NUM);
		that.find("input[name=AREA_CODE]").val(obj.AREA_CODE);// 地区
		that.afterSetDetailAddress(obj);// 原宽带地址选完后回调方法
	},

	afterSetDetailAddress : function(obj) {
		var that = this;
		that.afterFloorAndRoomNum(obj.FLOOR_AND_ROOM_NUM);
		// 默认将标准地址放置到安装地址处
//		that.find("input[name=DETAIL_ADDRESS]").trigger("input");
		that.find("input[name=DETAIL_ADDRESS]").val(obj.STAND_ADDRESS);

		// PBOSS那边传过来的宽带类型,1-移动FTTH、2-铁通FTTH、3-移动FTTB、4-铁通FTTB、5-铁通ADSL
		var openType = obj.OPEN_TYPE;

		// 1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：海南铁通FTTH，6：海南铁通FTTB
		var wideProductType = "1";

		if ('FTTH' == openType) {
			wideProductType = '3';
		} else if ('TTFTTH' == openType) {
			wideProductType = '5';
		} else if ('GPON' == openType) {
			wideProductType = "1";
		} else if ('TTFTTB' == openType) {
			wideProductType = "6";
		} else if ('TTADSL' == openType) {
			wideProductType = "2";
		}

		// 宽带产品类型
		that.find("input[name=WIDE_PRODUCT_TYPE]").val(wideProductType);

		// 刷新宽带产品，
		// 只有有移动FTTH和铁通FTTH需要申请光猫
		if ('3' != wideProductType && '5' != wideProductType) {
			that.find("li[name=modemDiv]").css("display", "none");
			that.find("li[name=modelSaleExplain]").css("display", "none");
			that.find("input[name=MODEM_STYLE]").val('');
			that.find("input[name=MODEM_DEPOSIT]").val('0');
			that.find("input[name=SALE_ACTIVE_FEE2]").val('');
		} else {
			that.find("li[name=modemDiv]").css('display', '');
			that.find("li[name=modelSaleExplain]").css('display', '');
			that.find("input[name=MODEM_STYLE]").val('0');
		}

		// ADSL和商务宽带不能办理魔百和业务

		that.showOfferinit(that);//宽带商品初始化

	},

	afterFloorAndRoomNum : function(floor_and_room_num) {
		var that = this;
		// 获取楼层和房号
		if (floor_and_room_num != '') {
			// 有值
			that.find("input[name=FLOOR_AND_ROOM_NUM]").attr("disabled", true);
			// 给楼层隐藏赋值 1
			that.find("input[name=FLOOR_AND_ROOM_NUM_FLAG]").val('1');
		} else {
			// 没有值
			that.find("input[name=FLOOR_AND_ROOM_NUM]").val('');
			that.find("input[name=FLOOR_AND_ROOM_NUM]").attr("disabled", false);
			that.find("input[name=FLOOR_AND_ROOM_NUM_FLAG]").val('0');
		}
	},

	showOfferinit : function(that) {
		OfferPop.initSelectedOffers(that);// 宽带商品必选元素
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

		var saleActiveFee2 = that.find("input[name=SALE_ACTIVE_FEE2]").val();// 光猫调测费
		if (null == saleActiveFee2 || '' == saleActiveFee2) {
			saleActiveFee2 = '0';
		}

		var feeFlag = true;
		var totalFee = parseFloat(saleActiveFee2) / 100 + parseFloat(topSetBoxSaleActiveFee2) / 100;

		if (totalFee > 0) {
			feeFlag = false;
			var tips = "您总共需要从现金存折中转出：" + totalFee + "元,其中，宽带调测费：" + parseFloat(saleActiveFee2) / 100 + "元，魔百和调测费：" + parseFloat(topSetBoxSaleActiveFee2) / 100 + "元";

			// 宽带付费方式 A：先装后付，P：立即付费
			var widePayMode = that.find("input[name=WIDENET_PAY_MODE]").val();

			if ("A" == widePayMode) {
				tips = "本次家庭宽带开户总费用为：" + totalFee + "元,其中宽带调测费：" + parseFloat(saleActiveFee2) / 100 + "元，魔百和调测费：" + parseFloat(topSetBoxSaleActiveFee2) / 100 + "元，费用将在施工完成后收取";
			}

			if ("A" == widePayMode) {
				feeFlag = true;
			} else {
				feeFlag = that.checkFeeBeforeSubmit(saleActiveFee2, topSetBoxSaleActiveFee2);
			}

		}
		return feeFlag;
	},

	// 提交前费用校验
	checkFeeBeforeSubmit : function(saleActiveFee2, topSetBoxSaleActiveFee2) {
		var that = this;
		var result = false;

		// 后台余额校验
		var param = "&SALE_ACTIVE_FEE2=" + saleActiveFee2 + "&TOPSETBOX_SALE_ACTIVE_FEE2=" + topSetBoxSaleActiveFee2 + "&SERIAL_NUMBER=" + that.mainSn;
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
	},

	checkIsCanAddImsAndTv : function(roleCode, roleType) {
		$.beginPageLoading("规则检验中...");
		var that = this;
		var flag = false;
		var param = "&SERIAL_NUMBER=" + that.mainSn + "&ROLE_CODE=" + roleCode + "&ROLE_TYPE=" + roleType + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
		hhSubmit(null, constdata.HANDLER, "checkAddChilrenRole", param, function(data) {
			$.endPageLoading();
			flag = true;
		}, function(error_code, error_info) {
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		}, {
			async : false
		});

		return flag;
	}

}
