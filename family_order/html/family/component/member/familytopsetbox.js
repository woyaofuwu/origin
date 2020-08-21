function TopsetBox(tplObj) {
    Role.apply(this, [ "4", tplObj.roleType, tplObj.partId ]);
    var tpl = tplObj.tpl;
    var sn = tplObj.sn;
    var eparchyCode = tplObj.eparchyCode;
    var roleCode = '4';
    var roleType = tplObj.roleType;
    var partId = tplObj.partId;
    var topSetBoxAreaId = 'TOP_SET_BOX_AREA_ID_'+this.uniqueId;
    var wideProductType = tplObj.wideProductType;
    var wideProductId = tplObj.wideProductId;
    this.sn = sn;
    this.mainSn = sn;
    this.eparchyCode = eparchyCode;
    this.roleCode = roleCode;
    this.roleType = roleType;
    this.partId = partId;
    this.wideProductType = wideProductType;
    this.wideProductId = wideProductId;
    this.topSetBoxAreaId = topSetBoxAreaId;
    this.isNeedSelectOffer = true;
    // 没有用户资料的角色，代付和共享都设置为false
    this.familyPay = true;
    this.share = false;
    this.initDomTree(tpl);
    this.initinfos();
}

TopsetBox.tpl = 'family/component/tpl/familytopsetbox.tpl';
TopsetBox.prototype = {
    getImportData : function () {
    	var _this = this;

    	var roleType = _this.roleType;

    	var data = $.DataMap();
        if (!_this.checkSubmitData()) {
            return false;
        }

        data.put("SERIAL_NUMBER", _this.sn);
        data.put("ROLE_CODE", _this.roleCode);
        data.put("ROLE_TYPE", _this.roleType);

        //新装魔百和
        if("NEW" == _this.roleType){
        	data.put("PRODUCT_ID",_this.find("input[id=TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val());
        	data.put("BASE_PACKAGES",_this.find("input[id=TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"]").val());
        	data.put("PLATSVC_PACKAGES",_this.find("input[id=TOP_SET_BOX_PLATSVC_PACKAGES_"+_this.uniqueId+"]").val());
        	data.put("OPTION_PACKAGES",_this.find("input[id=TOP_SET_BOX_OPTION_PACKAGES_"+_this.uniqueId+"]").val());
        	data.put("MO_PRODUCT_ID2",_this.find("input[id=MO_PRODUCT_ID2_"+_this.uniqueId+"]").val());
        	data.put("MO_PACKAGE_ID2",_this.find("input[id=MO_PACKAGE_ID2_"+_this.uniqueId+"]").val());
        	data.put("TOP_SET_BOX_SALE_ACTIVE_ID2",_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId+"]").val());
        	data.put("TOP_SET_BOX_SALE_ACTIVE_FEE2",_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val());
        }
        if("OLD" == _this.roleType){

        	data.put("OLD_PRODUCT_ID",_this.find("input[id=OLD_TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val());
        	data.put("OLD_BASE_PACKAGES",_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"]").val());
        	data.put("OLD_RES_NO",_this.find("input[id=OLD_TOP_SET_BOX_RES_NO_"+_this.uniqueId+"]").val());

        }
        return data;
    },

    //校验魔百和提交数据
    checkSubmitData : function () {
    	var _this = this;

    	if("NEW" == _this.roleType){

    		var productId=_this.find("input[id=TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val();
        	if(!productId || productId == ""){
        		MessageBox.alert("提示", _this.sn+":魔百和产品类型不能为空！");
        		return false;
        	}

        	var basePackages=_this.find("input[id=TOP_SET_BOX_PLATSVC_PACKAGES_"+_this.uniqueId+"]").val();
        	if(!basePackages || basePackages == ""){
        		MessageBox.alert("提示", _this.sn+":必选基础包不能为空！");
        		return false;
        	}

        	var platsvcPackages=_this.find("input[id=TOP_SET_BOX_PLATSVC_PACKAGES_"+_this.uniqueId+"]").val();
        	if(!platsvcPackages || platsvcPackages == ""){
        		MessageBox.alert("提示", "必选优惠包不能为空！");
        		return false;
        	}

        	var saleActiveId2=_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId+"]").val();
        	if(!saleActiveId2 || saleActiveId2 == ""){
        		MessageBox.alert("提示", "魔百和调测费不能为空！");
        		return false;
        	}
    	}

    	if("OLD" == _this.roleType){

    		var productId=_this.find("input[id=OLD_TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val();
        	if(!productId || productId == ""){
        		MessageBox.alert("提示", _this.sn+":魔百和产品类型不能为空！");
        		return false;
        	}

        	var basePackages=_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"]").val();
        	if(!basePackages || basePackages == ""){
        		MessageBox.alert("提示", _this.sn+":必选基础包不能为空！");
        		return false;
        	}

        	var resNo=_this.find("input[id=OLD_TOP_SET_BOX_RES_NO_"+_this.uniqueId+"]").val();
        	if(!resNo || resNo == ""){
        		MessageBox.alert("提示", _this.sn+":终端编码不能为空！");
        		return false;
        	}

    	}


    	return true;
    },
    getRefreshTplData : function () {
        //填充模板数据
        var btnStrs = 'x';
        var data = {
            title : '魔百和',
            serialNum : '服务号码',
            sn : this.sn,
            changeOffer : '商品选择',
            btnStrs : btnStrs
        };
        return data;
    },
    //页面初始化方法
    initinfos: function (){
    	var _this = this;
    	if(_this.roleType == "NEW")
    	{
    		_this.find("div[name=NEW_TOP_SET_BOX]").css("display", "");
    		_this.find("div[name=OLD_TOP_SET_BOX]").css("display", "none");

    		var param = "&OPER_FLAG=roleTvInit"
    				  + "&ROLE_TYPE="+ _this.roleType
    				  + "&SERIAL_NUMBER="+ _this.sn
    				  +	"&EPARCHY_CODE="+ _this.eparchyCode
    				  +	"&ROUTE_EPARCHY_CODE="+ _this.eparchyCode;
    		hhSubmit(null, window.constdata.HANDLER, "familyTvOper",param, function(ajaxData) {
				if (ajaxData && ajaxData.length > 0)
				{
					// 魔百和产品类型
					$.Select.after("selectTvProductTypeCon_" +_this.uniqueId,
					{
						id : "TOP_SET_BOX_PRODUCT_ID_" +_this.uniqueId,
						name : "TOP_SET_BOX_PRODUCT_ID_" +_this.uniqueId,
						style : "width: 100%",
						textField : "PRODUCT_NAME",
						valueField : "PRODUCT_ID",
						addDefault : true,
						defaultText : "--请选择--",
					},
					ajaxData.get("PRODUCT_INFO_SET"));
					_this.find("input[id=TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").bind("change", function() {
						var productId = _this.find("input[id=TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val();
						_this.queryTvPackages(productId);
					});

					//必选优惠包
		    		$.Select.append("selectTvBasePackageCon_" +_this.uniqueId,
					{
						id : "TOP_SET_BOX_BASE_PACKAGES_" +_this.uniqueId,
						name : "TOP_SET_BOX_BASE_PACKAGES_" +_this.uniqueId,
						style : "width: 100%",
						textField : "SERVICE_NAME",
						valueField : "SERVICE_ID",
						addDefault : true,
						defaultText : "--请选择--"
					},
					$.DatasetList());

		    		//可选优惠包
		    		$.Select.after("selectTvOptionPackageCon_" +_this.uniqueId,
		    		{
		    				id : "TOP_SET_BOX_OPTION_PACKAGES_" +_this.uniqueId,
		    				name : "TOP_SET_BOX_OPTION_PACKAGES_" +_this.uniqueId,
		    				style : "width: 100%",
		    				textField : "SERVICE_NAME",
		    				valueField : "SERVICE_ID",
		    				addDefault : true,
		    				defaultText : "--请选择--"
		    		},
		    		$.DatasetList());

		    		//必选优惠包
		    		$.Select.after("selectTvPlatsvcPackagesCon_" +_this.uniqueId,
		    		{
		    				id : "TOP_SET_BOX_PLATSVC_PACKAGES_" +_this.uniqueId,
		    				name : "TOP_SET_BOX_PLATSVC_PACKAGES_" +_this.uniqueId,
		    				style : "width: 100%",
		    				textField : "SERVICE_NAME",
		    				valueField : "SERVICE_ID",
		    				addDefault : true,
		    				defaultText : "--请选择--"
		    		},
		    		$.DatasetList());

		    		//魔百和调测费活动
		    		$.Select.after("selectTvSaleActivesCon_" +_this.uniqueId,
		    		{
		    				id : "TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId,
		    				name : "TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId,
		    				style : "width: 100%",
		    				textField : "PARA_CODE3",
		    				valueField : "PARA_CODE2",
		    				addDefault : true,
		    				defaultText : "--请选择--"
		    		},
		    		$.DatasetList());
				}
    		}, function(error_code, error_info) {
    			$.endPageLoading();
    			$.MessageBox.error(error_code, error_info);
    		});
    	}

    	//存量魔百盒初始化
    	else if(_this.roleType == "OLD")
    	{
    		_this.find("div[name=NEW_TOP_SET_BOX]").css("display", "none");
    		_this.find("div[name=OLD_TOP_SET_BOX]").css("display", "");
    		//魔百和信息
    		var param = "&OPER_FLAG=roleTvInit"
				  + "&ROLE_TYPE="+ _this.roleType
				  + "&SERIAL_NUMBER="+ _this.sn
				  +	"&EPARCHY_CODE="+ _this.eparchyCode
				  +	"&ROUTE_EPARCHY_CODE="+ _this.eparchyCode;
    		hhSubmit(null, window.constdata.HANDLER, "familyTvOper",param, function(ajaxData) {

    			// 魔百和产品类型
				$.Select.after("selectOldTvProductTypeCon_" +_this.uniqueId,
				{
					id : "OLD_TOP_SET_BOX_PRODUCT_ID_" +_this.uniqueId,
					name : "OLD_TOP_SET_BOX_PRODUCT_ID_" +_this.uniqueId,
					style : "width: 100%",
					textField : "PRODUCT_NAME",
					valueField : "PRODUCT_ID",
					addDefault : true,
    				defaultText : "--请选择--"
				},
				ajaxData.get("OLD_PRODUCT_INFO_SET"));
				_this.find("input[id=OLD_TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").bind("change", function() {
					var productId = _this.find("input[id=OLD_TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val();
					_this.queryTopsetBoxInfo(productId);
				});
    		},function(error_code, error_info) {
    			$.endPageLoading();
    			$.MessageBox.error(error_code, error_info);
    		});

    	}
    },

    //查询魔百和基础优惠包和可选优惠包start
    queryTvPackages:function (productId){
    	var _this = this;

    	_this.find("span[id=TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"_span]").remove();
    	_this.find("span[id=TOP_SET_BOX_OPTION_PACKAGES_"+_this.uniqueId+"_span]").remove();
    	_this.find("span[id=TOP_SET_BOX_PLATSVC_PACKAGES_"+_this.uniqueId+"_span]").remove();
    	_this.find("span[id=TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId+"_span]").remove();
		window["TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId] = null;
		window["TOP_SET_BOX_OPTION_PACKAGES_"+_this.uniqueId] = null;
		window["TOP_SET_BOX_PLATSVC_PACKAGES_"+_this.uniqueId] = null;
		window["TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId] = null;

		_this.find("input[id=SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('0');
		_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('');
		_this.find("textarea[id=TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_"+_this.uniqueId+"]").val('');

    	if(!productId){
    		return $.DatasetList();
    	}

    	$.beginLoading('基础优惠包和可选优惠包查询中...', _this.topSetBoxAreaId);

    	var param = "&OPER_FLAG=qryPackagesByPID"
    			   +"&PRODUCT_ID="+productId
    			   +"&WIDE_PRODUCT_TYPE="+_this.wideProductType
    			   +"&WIDE_PRODUCT_ID="+_this.wideProductId
    			   +"&SERIAL_NUMBER="+_this.sn;
    	hhSubmit(null, window.constdata.HANDLER,"familyTvOper",param, function(ajaxData){
    		$.endLoading(_this.topSetBoxAreaId);

    		if ('-1' == ajaxData.get('resultIPTVCode')){
    			$.MessageBox.error("拦截提示:", ajaxData.get('resultIPTVInfo'));
    			_this.find("input[id=TOP_SET_BOX_PRODUCT_ID_"+_this.uniqueId+"]").val('');
    		}

    		//必选优惠包
    		$.Select.append("selectTvBasePackageCon_" +_this.uniqueId,
			{
				id : "TOP_SET_BOX_BASE_PACKAGES_" +_this.uniqueId,
				name : "TOP_SET_BOX_BASE_PACKAGES_" +_this.uniqueId,
				style : "width: 100%",
				textField : "SERVICE_NAME",
				valueField : "SERVICE_ID",
				addDefault : true,
				defaultText : "--请选择--"
			},
			ajaxData.get("B_P"));

    		//可选优惠包
    		$.Select.after("selectTvOptionPackageCon_" +_this.uniqueId,
    		{
    				id : "TOP_SET_BOX_OPTION_PACKAGES_" +_this.uniqueId,
    				name : "TOP_SET_BOX_OPTION_PACKAGES_" +_this.uniqueId,
    				style : "width: 100%",
    				textField : "SERVICE_NAME",
    				valueField : "SERVICE_ID",
    				addDefault : true,
    				defaultText : "--请选择--"
    		},
    		ajaxData.get("O_P"));

    		//必选优惠包
    		$.Select.after("selectTvPlatsvcPackagesCon_" +_this.uniqueId,
    		{
    				id : "TOP_SET_BOX_PLATSVC_PACKAGES_" +_this.uniqueId,
    				name : "TOP_SET_BOX_PLATSVC_PACKAGES_" +_this.uniqueId,
    				style : "width: 100%",
    				textField : "SERVICE_NAME",
    				valueField : "SERVICE_ID",
    				addDefault : true,
    				defaultText : "--请选择--"
    		},
    		ajaxData.get("P_P"));

    		//魔百和调测费活动
    		$.Select.after("selectTvSaleActivesCon_" +_this.uniqueId,
    		{
    				id : "TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId,
    				name : "TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId,
    				style : "width: 100%",
    				textField : "PARA_CODE3",
    				valueField : "PARA_CODE2",
    				addDefault : true,
    				defaultText : "--请选择--"
    		},
    		ajaxData.get("TOP_SET_BOX_SALE_ACTIVE_LIST2"));

    		_this.find("input[id=MO_SALEACTIVE_LIST2_"+_this.uniqueId+"]").val(ajaxData.get('TOP_SET_BOX_SALE_ACTIVE_LIST2'));

    		_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId+"]").bind("change", function() {
				_this.changeTvSaleActiveId2();
			});


    	}, function(error_code, error_info) {
			$.$.endLoading(_this.topSetBoxAreaId);
			$.MessageBox.error(error_code, error_info);
		});
    },
    //查询魔百和基础优惠包和可选优惠包end

    //根据魔百和id查询调测费详情描述start
    changeTvSaleActiveId2:function (){
    	var _this = this;
    	_this.find("input[id=SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('0');
    	_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('');
    	_this.find("textarea[id=TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_"+_this.uniqueId+"]").val('');

    	var topSetBoxSaleActiveId = _this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId+"]").val();
    	var moSaleActiveList = _this.find("input[id=MO_SALEACTIVE_LIST2_" +_this.uniqueId+"]").val();

    	if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId){
    		var topSetBoxSaleActiveProductId = '';//魔百和营销方案ID
    		var topSetBoxSaleActivePackageId = '';//魔百和营销包ID
    		var topSetBoxSaleActiveExplain = '';//魔百和营销活动描述
    		var ruleTag=''; //依赖宽带产品的规则标记
    		var depProdIds='';//依赖宽带产品串

    		if (null != moSaleActiveList)
    		{
    			var moSaleActiveDataset = $.DatasetList(moSaleActiveList);
    			for (var i = 0; i < moSaleActiveDataset.length; i++)
    			{
    				if (topSetBoxSaleActiveId == moSaleActiveDataset.get([i],"PARA_CODE2"))
    				{
    					topSetBoxSaleActiveProductId = moSaleActiveDataset.get([i],"PARA_CODE4");
    					topSetBoxSaleActivePackageId = moSaleActiveDataset.get([i],"PARA_CODE5");
    					topSetBoxSaleActiveExplain = moSaleActiveDataset.get([i],"PARA_CODE24");
    					ruleTag=moSaleActiveDataset.get([i],"PARA_CODE22");
    					depProdIds=moSaleActiveDataset.get([i],"PARA_CODE23");
    				}
    			}
    		}

    		var eparchyCode = _this.eparchyCode;
    		var param = "&OPER_FLAG=checkSaleActive"
    					+ "&ROUTE_EPARCHY_CODE=" + eparchyCode
    					+ "&SERIAL_NUMBER=" +_this.sn
    					+ "&PRODUCT_ID=" + topSetBoxSaleActiveProductId
    					+ "&PACKAGE_ID=" + topSetBoxSaleActivePackageId
    					+ "&RULE_TAG=" + ruleTag
    					+ "&DEP_PRODUCT_ID=" + depProdIds;

    		$.beginLoading('魔百和调测费活动校验中...', _this.topSetBoxAreaId);


    		hhSubmit(null, window.constdata.HANDLER,"familyTvOper",param, function(ajaxData){

    				$.endLoading(_this.topSetBoxAreaId);

    				_this.find("input[id=MO_PRODUCT_ID2_" +_this.uniqueId+"]").val(topSetBoxSaleActiveProductId);
    				_this.find("input[id=MO_PACKAGE_ID2_" +_this.uniqueId+"]").val(topSetBoxSaleActivePackageId);
    				_this.find("textarea[id=TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_" +_this.uniqueId+"]").val(topSetBoxSaleActiveExplain);

    				//营销活动费用需要重新取产品模型配置
    				_this.checkTopBoxSetSaleActiveFee2(topSetBoxSaleActiveProductId,topSetBoxSaleActivePackageId);

    			},
    			function(error_code, error_info)
    			{
    				_this.find("input[id=MO_PRODUCT_ID2_" +_this.uniqueId+"]").val('');
    				_this.find("input[id=MO_PACKAGE_ID2_" +_this.uniqueId+"]").val('');
    				_this.find("textarea[id=TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_" +_this.uniqueId+"]").val('');
    				_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_" +_this.uniqueId+"]").val('');
    				_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('');
    				_this.find("input[id=SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('0');

    				$.endLoading(_this.topSetBoxAreaId);
    				$.MessageBox.error(error_code, error_info);
    			});
    	}

    },
    //根据魔百和id查询调测费详情描述end

    //获取产品模型配置start
    checkTopBoxSetSaleActiveFee2:function(productId,packageId){
    	var _this = this;

    	//费用清零
    	_this.find("input[id=SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('');

    	$.beginLoading('魔百和调测费活动校验中...', _this.topSetBoxAreaId);

    	var param = "&OPER_FLAG=queryCheckSaleActiveFee"
    			  + "&SERIAL_NUMBER="+ _this.sn
    			  + "&PRODUCT_ID=" + productId
    			  + "&PACKAGE_ID=" + packageId
    			  + "&ACTIVE_FLAG=2";

    	hhSubmit(null, window.constdata.HANDLER,"familyTvOper",param, function(ajaxData){

    		$.endLoading(_this.topSetBoxAreaId);

    		var fee = ajaxData.get("SALE_ACTIVE_FEE");
    		_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val(fee);
    		_this.find("input[id=SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val(fee/100);
    	},
    	function(error_code, error_info){

    		_this.find("textarea[id=TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_"+_this.uniqueId+"]").val('');
    		_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_ID2_"+_this.uniqueId+"]").val('');
    		_this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val('');

    		$.endLoading(_this.topSetBoxAreaId);
			$.MessageBox.error(error_code, error_info);
    	});
    },
    //获取产品模型配置end

    afterInitDom:function(){
    	var _this = this;
    	OfferPop.initSelectedOffers(_this);
    },

    //组织提交数据start
    getSubmitData : function() {

    },
    //组织提交数据end

    queryTopsetBoxInfo:function (productId){
    	var _this=this;

		_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"]").val("");
		_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_NAME_"+_this.uniqueId+"]").val("");
		_this.find("input[id=OLD_TOP_SET_BOX_RES_NO_"+_this.uniqueId+"]").val("");

    	if(!productId){
    		return $.DatasetList();
    	}

    	selectExistTopsetBox.showPop(_this, productId);

    },

    //提供给当前宽带角色，宽带下的魔百和调测费
    getTopSetActiveFee: function () {
    	var _this=this;
        var topSetBoxSaleActiveFee2 = _this.find("input[id=TOP_SET_BOX_SALE_ACTIVE_FEE2_"+_this.uniqueId+"]").val();
        return topSetBoxSaleActiveFee2;
    },

    checkIsCanAddOldTv : function(resNo) {
    	var _this = this;
    	$.beginPageLoading('魔百和规则检验中...', _this.topSetBoxAreaId);
		var _this = this;
		var flag = false;
		var param = "&RES_NO=" + resNo
				  + "&SERIAL_NUMBER=" + _this.sn
				  + "&ROLE_CODE=" + _this.roleCode
				  + "&ROLE_TYPE=" + _this.roleType
				  + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
		hhSubmit(null, constdata.HANDLER, "checkAddChilrenRole", param, function(data) {
			$.endLoading(_this.topSetBoxAreaId);
			flag = true;
		}, function(error_code, error_info) {
			$.endLoading(_this.topSetBoxAreaId);
			$.MessageBox.error(error_code, error_info);
		}, {
			async : false
		});

		return flag;
	}
};

