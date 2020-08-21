//xunyl 创建  此文件专门用于集团BBOSS产品的属性初始化校验

/******************************************统一接口*************************************/
/**
 * @description 集团BBOSS产品的属性变更统一接口
 * @author xunyl
 * @date 2014-06-25
 */
 function initParamUnit(){
	// 1- 获取所有产品参数对象
	var attrParams = $('[id =PRODUCT_PARAM_CODE]');
	
	// 2- 遍历产品参数对象，执行初参数始化方法
	attrParams.each(function() {
		// 获取输入框对象
		var paramCode = $.attr(this, "value");
		var inputObj = $("#input_" + paramCode);
		// 获取调用的js方法名
		var methodName = inputObj.attr('initMethodName');
		// 判断方法名是否存在，如果不存在则直接返回
		if (methodName != null && methodName != "undefined"){
			// 获取商产品信息
			var productGoodInfos =new Wade.DataMap($("#productGoodInfos").val());
			// 调用相应的js方法，将属性编号，参数既有值和变更后的值传入js方法中
			return window[methodName](productGoodInfos);
		}
	});
	return true;
 }
 

/** **************************************400商品************************************ */
/**
 * @description 400商品初始化校验
 * @author xunyl
 * @date 2014-06-25
 */
 function business400_init(productGoodInfos){
    // 1- 400号码一致性校验
    check400Num(productGoodInfos);
 }

/**
 * @description 400商品中除400语音产品外其它产品的400号码必须与400语音产品中的400号保持一致
 * @author xunyl
 * @date 2014-06-25
 */
 function check400Num(productGoodInfos){
	// 1- 获取当前的产品编号(全网产品编号)，如果为400语音产品，直接退出
	var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
 	var allNetProductId = productParamInfoList.get(0,"PRODUCT_ID");
	if(allNetProductId == "411501"){
		return false;
	}
	
	// 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接推出
	var productOpType = $("#productOperType").val();
	if(productOpType != "1" && productOpType != "10"){
		return false;
	}
	
	// 3- 获取400语音产品对应的省内产品编号
	var proProductId = "";
	$.ajax.submit(
				'','getProProductId','&ALLNET_PRODUCT_ID=411501', '', 
				function(data){proProductId = data.map.result;}, 
				function(error_code, error_info){}, 
				{async : false}
	);
	
	// 4- 获取商品操作类型，根据商品操作类型通过不同途径获取400号码
	var number = "";
	var operType = $.getSrcWindow().$("#operType").val();
	if(operType=="1"){
		number = get400NumForGrpOpen(productGoodInfos,proProductId);
	}else if(operType=="7"){
		number = get400NumForGrpChg(productGoodInfos,proProductId);
	}
		
	// 5- 如果400语音产品未订购，提示用户先订购400语音产品，再订购该产品
	if(number == ""){
		alert("400语音产品还未订购，请先订购400语音产品，否则该页面将无法提交！");
		return false;
	}
	
	// 6- 根据产品编号，给对应的400号码赋初始值
	if("411502" == allNetProductId){
		$("#input_4115027011").val(number);
		changeValue($('#input_4115027011')[0]);
	}else if("411503" == allNetProductId){
		$("#input_4115037011").val(number);
		changeValue($('#input_4115037011')[0]);
	}else if("411504" == allNetProductId){
		$("#input_4115047011").val(number);
		changeValue($('#input_4115047011')[0]);
	}else if("411506" == allNetProductId){  
		$("#input_4115067011").val(number);
		changeValue($('#input_4115067011')[0]);
		$("#input_4115061009").val('10657'+number);
		changeValue($('#input_4115061009')[0]);
		$("#input_4115062009").val('10657'+number);
		changeValue($('#input_4115062009')[0]);
	}else if("411508" == allNetProductId){  
		$("#input_4115087011").val(number);
		changeValue($('#input_4115087011')[0]);
		$("#input_4115081009").val('10657'+number);
		changeValue($('#input_4115081009')[0]);
		$("#input_4115082009").val('10657'+number);
		changeValue($('#input_4115082009')[0]);
	}else if("411509" == allNetProductId){  
		$("#input_4115097011").val(number);
		changeValue($('#input_4115097011')[0]);
		$("#input_4115091009").val('10657'+number);
		changeValue($('#input_4115091009')[0]);
		$("#input_4115092009").val('10657'+number);
		changeValue($('#input_4115092009')[0]);
	}else if("411510" == allNetProductId){
		$("#input_4115107011").val(number);
		changeValue($('#input_4115107011')[0]);
	}else if("411511" == allNetProductId){
		$("#input_4115111001").val(number);
		changeValue($('#input_4115111001')[0]);
	}	
	return true;
}

/**
 * @description 获取当前产品的产品编号(BBOSS侧产品编号)
 * @author xunyl
 * @date 2014-06-26
 */
 function getMerchpProductId(){
 	// 1- 获取初始化产品参数对象
	var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
    var allNetProductId = productParamInfoList.get(0, "PRODUCT_ID");
 	
 	// 3- 返回产品编号
 	return allNetProductId;
 }
 
 /**
	 * @description 商品变更阶段获取400语音产品中400号码的值
	 * @author xunyl
	 * @date 2014-06-27
	 */
  function get400NumForGrpChg(productGoodInfos,proProductId){
  	// 1- 定义400号码
  	var number = "";
  	  	
  	// 2- 获取商品对象中的产品信息列表
  	var productInfoList = new Wade.DatasetList(productGoodInfos.get("PRODUCT_INFO_LIST").toString());
  	if(productInfoList.length == 0){
  		return number;
  	}
  	
  	// 3- 遍历产品信息列表，获取400语音产品对应的用户编号
  	var userId = "";
  	for(var i = 0;i < productInfoList.length; i++){
  		var productInfo = productInfoList.get(i);
  		var productId = productInfo.get("PRODUCT_ID");
  		if(productId == proProductId){
  			userId = productInfo.get("USER_ID");
  			break;
  		}
  	}
  	
  	// 4- 根据400语音用户编号获取400号码
  	$.ajax.submit(
				'','get400NumByUserIdAttrCode','&USER_ID='+userId+'&ATTR_CODE=4115017001', '', 
				function(data){number = data.map.result;}, 
				function(error_code, error_info){}, 
				{async : false}
	);
  	
  	// 5- 返回400号码
  	return number;
  }
 
  /**
	 * @description 商品新增阶段获取400语音产品中400号码的值
	 * @author xunyl
	 * @date 2014-06-27
	 */
  function get400NumForGrpOpen(productGoodInfos,proProductId){
  	// 1- 定义400号码
  	var number = "";
  	
  	// 2- 获取商产品对象中的产品参数对象
	var productParamMap = new Wade.DataMap();
	if (productGoodInfos.get("PRODUCT_PARAM") != null) {
		productParamMap = productGoodInfos.get("PRODUCT_PARAM");
	}
	
	// 3- 获取400语音产品对应的产品参数
	var productParamInfo = productParamMap.get(proProductId+"_1");
	if(productParamInfo == null || productParamInfo=="undefined"){
		return number;
	}
	
	// 4- 从400语音产品对应的产品参数中遍历找出400号码值
	productParamInfo.eachKey(
		function(key, index, totalCount){
			var paramInfo = productParamInfo.get(key);
			var paramCode = paramInfo.get("ATTR_CODE");
			if(paramCode == "4115017001"){
				number = paramInfo.get("ATTR_VALUE");
			}
		}
	);
	
	// 5- 返回400号码
	return number;
  }

  /** **************************************省行业网关云MAS商品***************************************** */
  /*
    	 * 省行业网关云MAS初始化入口 @author chenkh @param productGoodInfos
    	 */
  function IAGWCloudMAS_init(productGoodInfos) {
	    var allNetProductId = getMerchpProductId();
	    setServCode1();//add by songxw 省行业网关短流程云MAS

	    // 1- 局数据产品和全网短流程基本接入号和企业代码校验
	    checkBizInCode(productGoodInfos, allNetProductId);

	    var operType = $.getSrcWindow().$("#operType").val();
	    if (operType != "1" && operType != "7") {
	        return false;
	    }
	    // 2- 服务代码一致性校验
	    checkServiceCode(productGoodInfos, allNetProductId);
	    // 3- 企业代码一致性校验
	    checkEnterpriseCode(productGoodInfos,allNetProductId);
	    // 4-端口速率一致性校验
	    checkPortRate(productGoodInfos, allNetProductId);

	}
  
  
  function dealbaseinCodeputAfter(data, allNetProductId) {
	    var ecBaseInCode = data.get("EC_BASE_IN_CODE", "");
	    var ecBaseInCodea = data.get("EC_BASE_IN_CODE_A", "");
	    if (ecBaseInCode == '' || ecBaseInCode == '[]') {
	        alert('该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
	        return;
	    }
	    if ("110164" == allNetProductId) {
	        if (ecBaseInCodea == '01') {
	            $("#input_1101644005").val(ecBaseInCode);
	            changeValue($('#input_1101644005')[0]);
	            $('#input_1101644005').attr('disabled', true);

	        } else {
	            $("#input_1101644007").val(ecBaseInCode);
	            changeValue($('#input_1101644007')[0]);
	            $('#input_1101644007').attr('disabled', true);

	        }

	        $("#input_1101644002").val(data.get("SP_CODE", ""));
	        changeValue($('#input_1101644002')[0]);
	        $('#input_1101644002').attr('disabled', true);
	    } else if ("110163" == allNetProductId) {
	        if (ecBaseInCodea == '01') {
	            $("#input_1101631009").val(ecBaseInCode);
	            changeValue($('#input_1101631009')[0]);
	            $('#input_1101631009').attr('disabled', true);

	        } else {
	            $("#input_1101631019").val(ecBaseInCode);
	            changeValue($('#input_1101631019')[0]);
	            $('#input_1101631019').attr('disabled', true);

	        }

	        $("#input_1101630004").val(data.get("SP_CODE", ""));
	        changeValue($('#input_1101630004')[0]);
	        $('#input_1101630004').attr('disabled', true);

	    } else if (allNetProductId == "110157" || allNetProductId == "110158") {
	        $("#input_" + allNetProductId + "4002").val(ecBaseInCode);
	        changeValue($("#input_" + allNetProductId + "4002")[0]);
	        $("#input_" + allNetProductId + "4002").attr('disabled', true);
	        // 服务代码为1065096开头，系统默认“白名单”；为1065097开头，系统默认“黑名单”。不允许修改
	        if ("110157" == allNetProductId) {
	            if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509622")||ecBaseInCode.substring(0, 9) == "106509652"
	            	||ecBaseInCode.substring(0, 9) == "106509022"||ecBaseInCode.substring(0, 9) == "106509052") {
	                $('#input_1101574011').val("2");
	                $('#input_1101574011').change();
	                $('#input_1101574011').attr("disabled", true);
	                $('#PARAM_NAME_1101574012').removeClass("e_required");
	                $('#input_1101574012').attr("nullable", "yes");

	            } else if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509722"||ecBaseInCode.substring(0, 9) == "106509752"||ecBaseInCode.substring(0, 9) == "106509122"||ecBaseInCode.substring(0, 9) == "106509152")) {
	                $('#input_1101574011').val("0");
	                $('#input_1101574011').change();
	                $('#input_1101574011').attr("disabled", true);
	                $('#PARAM_NAME_1101574012').attr("class", "e_required");
	                $('#input_1101574012').attr("nullable", "no");
	            } else {
	                alert('省行业网关短流程服务代码必须以白名单1065096-XY-ABC，1065090-XY-ABC或者黑名单1065097-XY-ABC，1065091-XY-ABC(xy的校验规则请参照枚举对应关系)开头!');
	            }
	        }

	    }
	    return;
	}
  function checkBizInCode(productGoodInfos, allNetProductId) {

	    if (allNetProductId == "110164" || allNetProductId == "110163") {

	        // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接推出
	        var productOpType = $("#productOperType").val();
	        if (productOpType != "1") {
	            return false;
	        }
	        // 获取短信基本接入号和企业代码
	        var group_id = $.getSrcWindow().$("#GROUP_ID").val(); // 从父页面获取CUST_ID
	        var incodea = "01";
	        var bizTypeCode = "001";
	        $.ajax.submit(this, 'getMasEcCodeListByA', '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + group_id + '&BIZ_TYPE_CODE=' + bizTypeCode, null,
	        function(data) {
	            dealbaseinCodeputAfter(data, allNetProductId);
	        });
	    } else if (allNetProductId == "110157" || allNetProductId == "110158" || allNetProductId == "110159") {
	        var productOpType = $("#productOperType").val();
	        if (productOpType != "1") {
	            return false;
	        }
	        // 获取短信基本接入号和企业代码
	        var group_id = $.getSrcWindow().$("#GROUP_ID").val(); // 从父页面获取CUST_ID
	        var incodea = allNetProductId == "110159" ? "02": "01";
	        var bizTypeCode = allNetProductId == "110159" ? "002": "001";;
	        $.ajax.submit(this, 'getMasEcCodeListByA', '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + group_id + '&BIZ_TYPE_CODE=' + bizTypeCode, null,
	        function(data) {
	            dealbaseinCodeputAfter(data, allNetProductId);
	        });

	    }

	}
  
  function checkServiceCode(productGoodInfos, allNetProductId) {
	    // 1- 调用产品一致性检查，取得主产品中主属性
	    var baseProduct = "";
	    var baseincode = ""; //短信基本接入号属性编码
	    if ("110154" == allNetProductId || "110155" == allNetProductId) {
	        baseProduct = "110163";
	        baseincode = "1101631009";
	    } else if ("110156" == allNetProductId) {
	        baseProduct = "110163";
	        baseincode = "1101631019";
	    } else if ("110160" == allNetProductId || "110161" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644005";
	    } else if ("110162" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644007";
	    } else {
	        return;

	    }
	    var attrValue = checkAttrSync(productGoodInfos, baseProduct, baseincode);
	    // 3- 对其他产品进行赋值
	    if ("110154" == allNetProductId) {
	        $("#input_1101544006").val(attrValue);
	        changeValue($('#input_1101544006')[0]);
	        $('#input_1101544006').attr('disabled', true);
	    } else if ("110155" == allNetProductId) {
	        $("#input_1101554006").val(attrValue);
	        changeValue($('#input_1101554006')[0]);
	        $('#input_1101554006').attr('disabled', true);
	    } else if ("110156" == allNetProductId) {
	        $("#input_1101564006").val(attrValue);
	        changeValue($('#input_1101564006')[0]);
	        $('#input_1101564006').attr('disabled', true);
	    } else if ("110160" == allNetProductId) {
	        $("#input_1101604002").val(attrValue);
	        changeValue($('#input_1101604002')[0]);
	        $('#input_1101604002').attr('disabled', true);
	    } else if ("110161" == allNetProductId) {
	        $("#input_1101614002").val(attrValue);
	        changeValue($('#input_1101614002')[0]);
	        $('#input_1101614002').attr('disabled', true);
	    } else if ("110162" == allNetProductId) {
	        $("#input_1101624002").val(attrValue);
	        changeValue($('#input_1101624002')[0]);
	        $('#input_1101624002').attr('disabled', true);
	    }
	}

  function getMasBaseProduct(productGoodInfos) {
	    var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
	    var allNetProductId = productParamInfoList.get(0, "PRODUCT_ID");
	    var baseProduct = "";
	    if ("110154" == allNetProductId || "110155" == allNetProductId || "110156" == allNetProductId) {
	        baseProduct = "110163";
	    } else if ("110160" == allNetProductId || "110161" == allNetProductId || "110162" == allNetProductId) {
	        baseProduct = "110164";
	    }
	    return baseProduct;

	}

  function checkEnterpriseCode(productGoodInfos,allNetProductId) {
	    var attrValue = "";
	    var baseProduct = getMasBaseProduct();
	    // 1- 调用产品一致性检查，取得主产品中主属性
	    if ("110163" == baseProduct) {
	        attrValue = checkAttrSync(productGoodInfos, "110163", "1101630004");
	    }
	    else if("110164" == baseProduct){
	    	attrValue = checkAttrSync(productGoodInfos, "110164", "1101644002");
	    }
	    // 2- 如果返回值为空，直接返回false
	    else {
	        return;
	    }
	    // 3- 对其他产品进行赋值
	    if ("110154" == allNetProductId) {
	        $("#input_1101544001").val(attrValue);
	        changeValue($('#input_1101544001')[0]);
	    } else if ("110155" == allNetProductId) {
	        $("#input_1101554001").val(attrValue);
	        changeValue($('#input_1101554001')[0]);
	    } else if ("110156" == allNetProductId) {
	        $("#input_1101564001").val(attrValue);
	        changeValue($('#input_1101564001')[0]);
	    }
	    else if ("110160" == allNetProductId) {
	    	//省行业网关短流程云MAS需求中企业代码写死故注掉
	        //$("#input_1101604037").val(attrValue);
	        //changeValue($('#input_1101604037')[0]);
	    }
	    else if ("110161" == allNetProductId) {
	        $("#input_1101614037").val(attrValue);
	        changeValue($('#input_1101614037')[0]);
	    }
	    else if ("110162" == allNetProductId) {
	    	//省行业网关短流程云MAS需求中企业代码写死故注掉
	        //$("#input_1101624037").val(attrValue);
	        //changeValue($('#input_1101624037')[0]);
	    }
	}

  function checkPortRate(productGoodInfos, allNetProductId) {
	    var baseProduct = "";
	    var baseincode = ""; //短信基本接入号属性编码
	    if ("110160" == allNetProductId || "110161" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644009";
	    } else if ("110162" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644010";
	    } else {
	        return;

	    }
	    var attrValue = checkAttrSync(productGoodInfos, baseProduct, baseincode);
	    // 3- 对其他产品进行赋值
	    if ("110160" == allNetProductId) {
	        $("#input_1101604005").val(attrValue);
	        changeValue($('#input_1101604005')[0]);
	    } else if ("110161" == allNetProductId) {
	        $("#input_1101614005").val(attrValue);
	        changeValue($('#input_1101614005')[0]);
	    } else if ("110162" == allNetProductId) {
	        $("#input_1101624005").val(attrValue);
	        changeValue($('#input_1101624005')[0]);
	    }
	}
  /**
  	 * 产品一致性检查
  	 * 
  	 * @author chenkh
  	 * @param productGoodInfos
  	 * @param mainMerchId
  	 * @param mainAttrCode
  	 * @returns 如果为空，则表示取值失败，上级函数返回false，否则返回需要取得的主属性值。
  	 */
  function checkAttrSync(productGoodInfos, mainMerchId, attrCode) {
	    // 1- 获取当前的产品编号(全网产品编号)，如果为主产品，直接退出
	    var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
	    var allNetProductId = productParamInfoList.get(0, "PRODUCT_ID");
	    if (allNetProductId == mainMerchId) {
	        return "";
	    }

	    // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接推出
	    var productOpType = $("#productOperType").val();
	    if (productOpType != "1") {
	        return "";
	    }

	    // 3- 获取主产品对应的省内产品编号
	    var proProductId = "";
	    $.ajax.submit('', 'getProProductId', '&ALLNET_PRODUCT_ID=' + mainMerchId, '',
	    function(data) {
	        proProductId = data.map.result;
	    },
	    function(error_code, error_info) {},
	    {
	        async: false
	    });

	    // 4- 获取商品操作类型，根据商品操作类型通过不同途径获取主产品属性
	    var attrvalue = "";
	    var operType = $.getSrcWindow().$("#operType").val();
	    if (operType == "1") {
	        attrvalue = getAttrValueForGrpOpen(productGoodInfos, proProductId, attrCode);
	    } else if (operType == "7") {
	        attrvalue = getAttrValueForGrpChg(productGoodInfos, proProductId, attrCode);
	    }

	    // 5- 如果主产品未订购，提示用户先订购主产品，再订购该产品
	    if (attrvalue == "") {
	        alert("商品主产品还未订购，请先订购商品主产品，否则该页面将无法提交！");
	        return "";
	    }
	    return attrvalue;
	}
  /**
	 * @param productGoodInfos
	 * @param proProductId
	 * @param mainAttrCode
	 *            主属性编号
	 * @returns {String} 主属性值
	 */
  function getAttrValueForGrpChg(productGoodInfos, proProductId, attrCode) {
	    // 1- 定义返回值
	    var attrValue = "";

	    // 2- 获取商品对象中的产品信息列表
	    var productInfoList = new Wade.DatasetList(productGoodInfos.get("PRODUCT_INFO_LIST").toString());
	    if (productInfoList.length == 0) {
	        return number;
	    }

	    // 3- 遍历产品信息列表，获取主产品对应的用户编号
	    var userId = "";
	    for (var i = 0; i < productInfoList.length; i++) {
	        var productInfo = productInfoList.get(i);
	        var productId = productInfo.get("PRODUCT_ID");
	        if (productId == proProductId) {
	            userId = productInfo.get("USER_ID");
	            break;
	        }
	    }

	    // 4- 根据主产品用户编号获取主属性
	    $.ajax.submit('', 'get400NumByUserIdAttrCode', '&USER_ID=' + userId + '&ATTR_CODE=' + attrCode, '',
	    function(data) {
	        attrValue = data.map.result;
	    },
	    function(error_code, error_info) {},
	    {
	        async: false
	    });

	    // 5- 返回主属性值
	    return attrValue;
	}

  /**
	 * @param productGoodInfos
	 * @param proProductId
	 * @param attrCode
	 *            主属性编号
	 * @returns {String} 主属性值
	 */
  function getAttrValueForGrpOpen(productGoodInfos, proProductId, attrCode) {
	    var attrValue = "";

	    // 2- 获取商产品对象中的产品参数对象
	    var productParamMap = new Wade.DataMap();
	    if (productGoodInfos.get("PRODUCT_PARAM") != null) {
	        productParamMap = productGoodInfos.get("PRODUCT_PARAM");
	    }

	    // 3- 获取产品对应的产品参数
	    var productParamInfo = productParamMap.get(proProductId + "_1");
	    if (productParamInfo == null || productParamInfo == "undefined") {
	        return attrValue;
	    }

	    // 4- 从对应的产品参数中遍历找出对应的属性值
	    productParamInfo.eachKey(function(key, index, totalCount) {
	        var paramInfo = productParamInfo.get(key);
	        var paramCode = paramInfo.get("ATTR_CODE");
	        if (paramCode == attrCode) {
	            attrValue = paramInfo.get("ATTR_VALUE");
	        }
	    });

	    // 5- 返回属性值
	    return attrValue;
	}
	/**
		 * @param null
		 * @returns void 设置省行业网关短流程云MAS服务代码
		 */
	function setServCode1(){
		  // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接退出
		  var productOpType = $("#productOperType").val();
		  if(productOpType != "1" && productOpType != "10"){
			  return false;
		  }
		  // 1- 获取当前的产品编号(全网产品编号)
		  var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
		  var allNetProductId = productParamInfoList.get(0,"PRODUCT_ID");
		  var servCode = getRandom1();
		  if(allNetProductId == "110159"){
			  //设置服务代码
			  $("#input_1101594002").val(servCode);
		      changeValue($('#input_1101594002')[0]);
		      //设置业务代码
		      /* var servCodeTemp = servCode.substring(0,9);
		    	var bizCode = "4444444444";
		    	if(servCodeTemp == "106509652"){
					bizCode = "6666666666";
				}else if(servCodeTemp == "106509022"){
					bizCode = "7777777777";
				}else if(servCodeTemp == "106509622"){
					bizCode = "4444444444";
				}else{
					bizCode = "1111111111";
				}*/
		      	var bizCode = "6666666666";
				$('#input_1101594010').val(bizCode);
				changeValue($('#input_1101594010')[0]);
		  }
	}
	
	/**
		 * @param null
		 * @returns {String} 省行业网关短流程云MAS服务代码固定5位+3位随机数
		 */
	function getRandom1(){
		  var rand = "";
		  for(var i = 0; i < 6; i++){
		      var r = Math.floor(Math.random() * 10);
		      rand += r;
		  }
	
		  /*var arr = ["106509652","106509022","106509622","106509052"];
		  var index = Math.floor((Math.random()*arr.length));
	
		  var servCode = arr[index]+rand;*/
		  var servCode = "106509652"+rand;
		  $.ajax.submit(this, 'getMasServCodeByParam', '&SERV_CODE=' + servCode, null,
		  function(data){
			    if(data.result == "1"){
			    	getRandom1();
			    }
		  });
		  return servCode;
	}
  