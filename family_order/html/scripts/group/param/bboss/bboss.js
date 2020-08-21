/*???
 * 比较两个dataset
 * str为需要比较的字段，以逗号分隔
 * 返回一个比较后的dataset
 * key唯一区分这个数据的,key可以是由多个字段组成的，以逗号分隔
 * str除了key以外的其他值
 */

function compareDataset(newDs, oldDs, key, str) {
	var resultDs = new Wade.DatasetList();
	var ss = str.split(",");
	var keyCols = key.split(",");
	// newDs和oldDs比较
	for (var i = 0; i < newDs.length; i++) {
		var newData = newDs.get(i);
		newData.put("STATE", "ADD");// 默认是新增的
		for (var j = 0; j < oldDs.length; j++) {
			var oldData = oldDs.get(j);
			var isKeyFind = "false";// 是否在两个数据中找到了相同的key
			for (var k = 0; k < keyCols.length; k++) {
				if (newData.get(keyCols[k]) == oldData.get(keyCols[k])
						&& newData.get("ATTR_GROUP") == oldData
								.get("ATTR_GROUP")) {
					isKeyFind = "true";
					break;// 如果key中有一个字段不相等，就不用比较了
				} else {
					isKeyFind = "false";
				}
			}

			if (isKeyFind == "true")// 如果两个参数的key相同
			{
				newData.put("STATE", "EXIST");// 置为存在状态
				var isModif = "false";// 是否被修改了
				for (var k = 0; k < ss.length; k++) {
					isModif = "false";// 是否被修改了
					if (newData.get(ss[k]) == oldData.get(ss[k])
							&& oldData.get("OLD_PARAM_VALUE") != null
							&& oldData.get("OLD_PARAM_VALUE") != "") {
						continue;// 如果两个字段相等，继续比较下一个字段
					} else {
						// 判断是否有数据库的值，没有则不算是修改
						if (oldData.get("OLD_PARAM_VALUE") != null
								&& oldData.get("OLD_PARAM_VALUE") != "") {
							newData.put("PARAM_OLD_VALUE", oldData.get(ss[k]));
							isModif = "true";
							break;
						} else {
							newData.put("STATE", "ADD");// 有修改，且数据库中没有，则为新增
						}
					}
				}
				if (isModif == "true")// 如果被修改了，就要保存成一条删除，一条新增
				{
					oldData.put("STATE", "DEL");// 将老的数据置为删除状态
					newData.put("STATE", "ADD");// 将新数据置为新增状态
					resultDs.add(oldData); // 将删除的老数据也保存
				}
			}
		}
		resultDs.add(newData);
	}

	// 老数据和新数据进行比较
	for (var i = 0; i < oldDs.length; i++) {
		var isfound = "false";// 是否找到了这个参数
		var oldData = oldDs.get(i);
		for (var j = 0; j < newDs.length; j++) {
			var newData = newDs.get(j);
			if (oldData.get(key) == newData.get(key)
					&& oldData.get("ATTR_GROUP") == newData.get("ATTR_GROUP"))// 比较老参数在新参数列表中是否有
			{
				isfound = "true";
				break;
			}
		}
		if (isfound == "false")// 如果新参数中没有，就要删除老的
		{
			oldData.put("STATE", "DEL");
			resultDs.add(oldData);
		}
	}
	return resultDs;
}

/**
 * @description 将BBOSS产品的资费信息和产品参数信息放到指定的控件中
 * @author xunyl
 * @date 2014-06-24
 */
function setProductParams() {
	// 1- 获取商产品信息
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());

	// 2- 获取产品序列号(产品ID+下标INDEX)
	var PRODUCT_INDEX = $("#PRODUCT_INDEX").val();
	var productIndex = $("#PRODUCT_NUMBER").val() + '_' + PRODUCT_INDEX;

	// 3- 将产品信息对象添加至商产品信息中
	setProductInfo(productGoodInfos, productIndex)

	// 4- 将产品属性对象添加至商产品信息(当产品操作类型不为修改产品资费时添加)
	if ($("#productOperType").val() != '5') 
	{
		setProductParamInfo(productGoodInfos, productIndex);
	}

	// 5- 将发送与落地方信息添加商品信息中
	var goodInfo = productGoodInfos.get("GOOD_INFO");
	goodInfo.put("LOCATION", "SEND");
	productGoodInfos.put("GOOD_INFO", goodInfo);

	// 6- 将资费与服务信息添加至商产品信息(当产品操作类型不为修改产品属性时添加)
	if ($("#productOperType").val() != '9') 
	{
		setElementInfo(productGoodInfos, productIndex)
	}

	// 7- 将集团定制信息添加到商产品信息
	var grpPackageInfo = new Wade.DataMap();
	if (productGoodInfos.get("GRP_PACKAGE_INFO") != null) 
	{
		grpPackageInfo = productGoodInfos.get("GRP_PACKAGE_INFO");
	}
	grpPackageInfo.put(productIndex, $("#SELECTED_GRPPACKAGE_LIST").val());
	productGoodInfos.put("GRP_PACKAGE_INFO", grpPackageInfo);

	// 8- IS_REOPEN作为标记用，true表示参数页面已经开启过，false表示参数页面初次加载
	var IS_REOPEN = new Wade.DataMap();
	if (productGoodInfos.get("IS_REOPEN") != null) {
		IS_REOPEN = productGoodInfos.get("IS_REOPEN");
	}
	IS_REOPEN.put(productIndex, true);
	productGoodInfos.put("IS_REOPEN", IS_REOPEN);
	

	// 9- 将商产品信息保存至主页面(bboss.html)中
	$.setReturnValue({'productGoodInfos' : productGoodInfos}, true);
}

/**
 * @description 设置产品信息
 * @author xunyl
 * @date 2013-07-02
 */
function setProductInfo(productGoodInfos, productIndex) {
	// 1- 获取老产品集
	var productInfoList = new Wade.DatasetList();
	if (null != productGoodInfos.get("PRODUCT_INFO_LIST")) {
		productInfoList = new Wade.DatasetList(productGoodInfos.get("PRODUCT_INFO_LIST").toString());
	}

	// 2- 定义产品对象
	var productInfo = new Wade.DataMap();

	// 3- 拼装产品对象
	var PRODUCT_INDEX = $("#PRODUCT_INDEX").val();
	productInfo.put("PRODUCT_INDEX", PRODUCT_INDEX);
	productInfo.put("ISEXIST", $("#isExist" + productIndex).val());// 产品受理时为空
	productInfo.put("PRODUCT_ID", $("#PRODUCT_NUMBER").val());
	productInfo.put("PRODUCT_OPER_CODE", $("#productOperType").val());// 产品操作类型
	productInfo.put("USER_ID", $("#GRP_USER_ID").val());// 子产品集团用户ID

	// 4- 检查当前产品信息是否存在于老产品集中（如果存在则先删除老产品信息，再添加新产品信息）
	for (var i = 0; i < productInfoList.length; i++) {
		if ((productInfo.get("PRODUCT_ID") == productInfoList.get(i,"PRODUCT_ID"))
				&& productInfo.get("PRODUCT_INDEX") == productInfoList.get(i,"PRODUCT_INDEX")) {
			productInfoList.removeAt(i);
			break;
		}
	}
	productInfoList.add(productInfo);

	// 5- 将产品信息保存至商产品信息中
	productGoodInfos.put("PRODUCT_INFO_LIST", productInfoList);
}

/**
 * @description 拼装产品属性信息
 * @auhtor xunyl
 * @date 2013-07-02
 */
function setProductParamInfo(productGoodInfos, productIndex) {
	// 1- 获取所有参数对象
	var attrParams = $('[id =PRODUCT_PARAM_CODE]');

	// 2- 定义新参数集
	var newParamObj = new Wade.DatasetList();

	// 3- 循环参数对象
	attrParams.each(function() {
		// 3-1 获取属性编号
		var paramCode = $.attr(this, "value");

		// 3-2 循环新参数集，查看当前属性编号是否存在于新参数集中
		var isFind = false;
		for (var i = 0; i < newParamObj.length; i++) {
			var tmpData = newParamObj.get(i);
			if (tmpData.get("ATTR_CODE") == paramCode)// 有重复的
			{
				isFind = true;
				break;
			}
		}

		// 3-3 如果新参数集中没有，则需要将该属性编号对应的所有属性添加进产品参数集(注意属性组情况)
		if (isFind == false) {
			var paramValueId = "input_" + paramCode;
			var groupAttrId = "GROUPATTR_FLAG_" + paramCode;
			var paramValues = $('[id=' + paramValueId + ']');
			var attrGroupFlags = $('[id=' + groupAttrId + ']');
			paramValues.each(function(index, item, totalcount) {
				// 新产品参数对象
				var newParamData = new Wade.DataMap();
				newParamData.put("ATTR_CODE", paramCode);
				newParamData.put("ATTR_NAME", $("#PARAM_NAME_" + paramCode).text());
				newParamData.put("ATTR_VALUE", $.attr(paramValues.get(index),	"value"));
				newParamData.put("ATTR_GROUP", $.attr(attrGroupFlags.get(index), "value"));
				var attr_value = $.attr(paramValues.get(index), "value");
				// 拼写属性的前后缀
				var front_part = $("#FRONT_PART_" + paramCode).text();
				var after_part = $("#AFTER_PART_" + paramCode).text()
				if (attr_value != "") {
					attr_value = front_part + attr_value + after_part;
					newParamData.put("ATTR_VALUE", attr_value);
					newParamObj.add(newParamData);
				}
			});
		}
	});

	// 4- 获取所有的老参数对象
	var oldDataset = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
	// 因为升级后集团受理与变更的参数页面合并了，而OLD_PRODUCT_PARAMS元素仅供变更使用，因此受理时赋空值
	if (null == $("#GRP_USER_ID").val() || "" == $("#GRP_USER_ID").val()) {
		oldDataset = new Wade.DatasetList("");
	}

	// 5- 定义老参数集
	var oldParamObj = new Wade.DatasetList();// 老产品参数对象集合

	// 6- 循环老参数对象，将老参数添加至老参数集
	for (var i = 0; i < oldDataset.length; i++) {
		var oldParamData = new Wade.DataMap();
		oldParamData.put("ATTR_CODE", oldDataset.get(i, "ATTR_CODE"));
		oldParamData.put("ATTR_NAME", oldDataset.get(i, "ATTR_NAME"));
		oldParamData.put("ATTR_VALUE", oldDataset.get(i, "ATTR_VALUE"));
		oldParamData.put("OLD_PARAM_VALUE", oldDataset
				.get(i, "OLD_PARAM_VALUE"));
		if (oldDataset.get(i, "ATTR_GROUP") != undefined) {
			oldParamData.put("ATTR_GROUP", oldDataset.get(i, "ATTR_GROUP"));
		} else {
			oldParamData.put("ATTR_GROUP", "");
		}
		oldParamObj.add(oldParamData);
	}

	// 7- 比较新老参数集,确定参数状态
	var tmpDs = compareDataset(newParamObj, oldParamObj, "ATTR_CODE", "ATTR_VALUE");

	// 8- 将产品参数对象保存进商产品信息中(productGoodInfos)
	var productParam = new Wade.DataMap();
	for (var i = 0; i < tmpDs.length; i++) {
		var tmpData = tmpDs.get(i);
		productParam.add("pro" + i, tmpData);
	}
	var PRODUCT_PARAM = new Wade.DataMap();
	if (productGoodInfos.get("PRODUCT_PARAM") != null) {
		PRODUCT_PARAM = productGoodInfos.get("PRODUCT_PARAM");
	}
	PRODUCT_PARAM.put(productIndex, productParam);
	productGoodInfos.put("PRODUCT_PARAM", PRODUCT_PARAM);
}

/**
 * @description 设置产品的资费与服务信息
 * @author xunyl
 * @date 2013-07-02
 */
function setElementInfo(productGoodInfos, productIndex) {
	// 1- 将产品资费与服务的缓存信息添加至商产品信息中（资费与服务信息包括缓存信息和提交信息，缓存信息供参数页面再次被访问使用，提交信息供后台入库用）
	var tempProductElements = new Wade.DatasetList(selectedElements.selectedEls.toString());

	var length = tempProductElements.length;
	for (var i = 0; i < length; i++) {
		var temp = tempProductElements.get(i);
		if (temp.get("ATTR_PARAM") == null
				|| temp.get("ATTR_PARAM").length == 0) {
			temp.removeKey("ATTR_PARAM");
		}
	}

	var TEMP_PRODUCTS_ELEMENT = new Wade.DataMap();
	if (productGoodInfos.get("TEMP_PRODUCTS_ELEMENT") != null) {
		TEMP_PRODUCTS_ELEMENT = productGoodInfos.get("TEMP_PRODUCTS_ELEMENT");
	}
	TEMP_PRODUCTS_ELEMENT.put(productIndex, tempProductElements);
	productGoodInfos.put("TEMP_PRODUCTS_ELEMENT", TEMP_PRODUCTS_ELEMENT);

	// 2- 将产品资费与服务的提交信息添加至商产品信息中
	var productElements = selectedElements.getSubmitData();
	var PRODUCTS_ELEMENT = new Wade.DataMap();
	if (productGoodInfos.get("PRODUCTS_ELEMENT") != null) {
		PRODUCTS_ELEMENT = productGoodInfos.get("PRODUCTS_ELEMENT");
	}
	PRODUCTS_ELEMENT.put(productIndex, productElements);
	productGoodInfos.put("PRODUCTS_ELEMENT", PRODUCTS_ELEMENT);
}

/**
 * @description 设置成员的参数
 * @author xunyl
 * @date 2014-06-24
 */

function setMemberParams() {

	// BBOSS商产品对象
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());

	// 产品属性参数
	var newParamObj = new Wade.DatasetList();// 新产品参数对象集合
	var oldParamObj = new Wade.DatasetList();// 老产品参数对象集合
	var attrParams = $('span[id=PRODUCT_PARAM_CODE]');// 获取页面上所有参数对象
	attrParams.each(function() {
		var code = $.attr(this, "value");
		// 新产品参数对象ddd
		var newParamData = new Wade.DataMap();
		newParamData.put("ATTR_CODE", code);
		newParamData.put("ATTR_NAME", $("#ATTR_NAME_" + code).text());
		newParamData.put("ATTR_VALUE", $("#input_" + code).val());
		if ($("#input_" + code).val() != "") {
			newParamObj.add(newParamData);
		}
	});
	var oldDataset = new Wade.DatasetList($("#OLD_MEMBER_PARAMS").val());// 成员新增没值，变更才有值
	for (var i = 0; i < oldDataset.length; i++) {
		var oldParamData = new Wade.DataMap();
		oldParamData.put("ATTR_CODE", oldDataset.get(i, "ATTR_CODE"));
		oldParamData.put("ATTR_NAME", oldDataset.get(i, "ATTR_NAME"));
		oldParamData.put("ATTR_VALUE", oldDataset.get(i, "ATTR_VALUE"));
		oldParamData.put("OLD_PARAM_VALUE", oldDataset
				.get(i, "OLD_PARAM_VALUE"));
		oldParamObj.add(oldParamData);
	}

	// 成员的参数信息
	var tmpDs = compareDataset(newParamObj, oldParamObj, "ATTR_CODE",
			"ATTR_VALUE");

	// 将产品参数对象保存进商产品信息中(productGoodInfos)
	var productParam = new Wade.DataMap();
	for (var i = 0; i < tmpDs.length; i++) {
		var tmpData = tmpDs.get(i);
		productParam.add("pro" + i, tmpData);
	}
	var PRODUCT_PARAM = productGoodInfos.get("PRODUCT_PARAM");
	if (null == PRODUCT_PARAM) {
		PRODUCT_PARAM = new Wade.DataMap();
	}
	PRODUCT_PARAM.put($("#GRP_USER_ID").val(), productParam);
	productGoodInfos.put("PRODUCT_PARAM", PRODUCT_PARAM);

	// 商品信息
	var goodInfo = productGoodInfos.get("GOOD_INFO");
	if (null == goodInfo) {
		goodInfo = new Wade.DataMap();
	}
	goodInfo.put("LOCATION", "SEND");
	productGoodInfos.put("GOOD_INFO", goodInfo);

	// 产品信息
	var productInfo = new Wade.DataMap();
	productInfo.put("PRODUCT_ID", $("#PRODUCT_NUMBER").val());
	productInfo.put("MEB_OPER_CODE", $("#memberOperType").val());// 成员操作类型
	productInfo.put("MEB_TYPE", $("#memberType").val());// 成员类型1签约成员,2白名单,3黑名单
	productInfo.put("USER_ID", $("#GRP_USER_ID").val());// 子产品集团用户ID
	var productInfoList = new Wade.DatasetList(productGoodInfos
			.get("PRODUCT_INFO_LIST"));// 产品信息列表
	for (var i = 0; i < productInfoList.length; i++) {
		if (productInfoList.get(i, "USER_ID") == $("#GRP_USER_ID").val()) {
			productInfoList.removeAt(i);
		}
	}
	productInfoList.add(productInfo);
	productGoodInfos.put("PRODUCT_INFO_LIST", productInfoList);

	// 资费与服务信息包括缓存信息和提交信息，缓存信息供参数页面再次被访问使用，提交信息供后台入库用 将产品资费与服务的缓存信息保存到上产品信息中
	var productIndex = $("#PRODUCT_NUMBER").val();
	var tempProductElements = selectedElements.selectedEls;
	var TEMP_PRODUCTS_ELEMENT = new Wade.DataMap();
	if (productGoodInfos.get("TEMP_PRODUCTS_ELEMENT") != null) {
		TEMP_PRODUCTS_ELEMENT = productGoodInfos.get("TEMP_PRODUCTS_ELEMENT");
	}
	TEMP_PRODUCTS_ELEMENT.put(productIndex, tempProductElements);
	productGoodInfos.put("TEMP_PRODUCTS_ELEMENT", TEMP_PRODUCTS_ELEMENT);

	// 元素
	var productElements = selectedElements.getSubmitData();
	var PRODUCTS_ELEMENT = productGoodInfos.get("PRODUCTS_ELEMENT");
	if (null == PRODUCTS_ELEMENT) {
		PRODUCTS_ELEMENT = new Wade.DataMap();
	}
	PRODUCTS_ELEMENT.put($("#GRP_USER_ID").val(), productElements);
	productGoodInfos.put("PRODUCTS_ELEMENT", PRODUCTS_ELEMENT);

	// 流量叠加包校验
	var productID = $("#PRODUCT_NUMBER").val();
	var prodPesc =getProPesc(productID)//获取集团编码
	if ('99904' == prodPesc || '99905' == prodPesc) {
		var result = checkFlowPackage(productElements, tmpDs, productID);
		if (!result) {
			return;
		}
	}

	// IS_REOPEN作为标记用，true表示参数页面已经开启过，false表示参数页面初次加载
	var IS_REOPEN = new Wade.DataMap();
	if (productGoodInfos.get("IS_REOPEN") != null) {
		IS_REOPEN = productGoodInfos.get("IS_REOPEN");
	}
	IS_REOPEN.put($("#GRP_USER_ID").val(), true);
	productGoodInfos.put("IS_REOPEN", IS_REOPEN);

	// 将商产品信息保存至主页面(bboss.html)中
	$.setReturnValue({
		'productGoodInfos' : productGoodInfos
	}, true);
}

/**
 * chenyi
 * 2015-4-1
 * 根据本地产品编码获取集团产品编码
 */
function  getProPesc(productID){
	var prodPesc="";
	Wade.httphandler.submit("",
			"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
			"qrySpecNumber", "&PRODUCT_ID=" + productID, function(d) {
				var param = d.map.result;
				prodPesc = param;
			}, function(e, i) {
				$.showErrorMessage("操作失败");
				result = false;
			}, {
				async : false
			});
	return prodPesc;
}

/**
 * @description 流量叠加包校验 如果订购流量叠加包就不能对其他属性和资费进行操作
 * @author chenyi
 * @date 2014-7-10
 */
function checkFlowPackage(productElements, tmpDs, productID) {

	var flag = false;// 是否有流量叠加包标识
	var result = true;// 返回结果
	var size = productElements.length;
	var ret = "";// frontDataVerify校验返回值
	// 判断是否是流量叠包操作
	for (var i = 0; i < size; i++) {
		var elementId = productElements.get(i, "ELEMENT_ID");
		Wade.httphandler.submit("",
				"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
				"qryFluxElementInfo", "&ELEMENT_ID=" + elementId
						+ "&PRODUCT_ID=" + productID, function(d) {
					var param = d.map.result;
					ret = param;
					//如果订购了叠加包
					if(ret=="true"){
						flag = true;
						 if(size>1){
							  MessageBox.alert("","加油包资费一次只能订购一个！");
							  result = false;
							   return result;
						  }
					}
					
				},function(e, i) {
					$.showErrorMessage("操作失败");
					result = false;
				}, {
					async : false
				});

	}
	// 如果是 则判断是否对属性进行了操作
	if (flag) {
		for (var i = 0; i < tmpDs.length; i++) {
			var state = tmpDs.get(i, "STATE");
			if ("ADD" == state || "DEL" == state) {
				result = false;
				MessageBox.alert("","订购流量加油时不可对其他资费或属性进行修改变更！");
			}
		}
	}
	return result;
}
/**
 * @description 成员操作中同一次操作只能针对一个成员产品
 * @author xunyl
 * @date 2013-08-09
 */
function checkMemOpValid() {
	// 1- 获取BBOSS商产品对象
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());

	// 2- 校验是否选择成员操作类型
	var memOpType = $('#memberOperType').val();
	if (memOpType == '' || memOpType == null || !g_IsDigit(memOpType)) {
		alert("请选择对应的成员操作类型");
		return false;
	}

	// 3- 校验是否已经存在成员信息，存在的场合得确定是否要放弃之前的操作
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");
	if (productInfoList != null && productInfoList.length > 0) {
		var productId = productInfoList.get(0).get("PRODUCT_ID").toString();
		if (productId != $("#PRODUCT_NUMBER").val()) {
			MessageBox.confirm("提示信息:","成员新增和成员变更每次只能新增或者变更一个成员产品，确定要放弃之前的操作吗?", function(btn) {
						if (btn == 'ok') {
							// 删除产品信息
							productGoodInfos.put("PRODUCT_INFO_LIST",
									new Wade.DatasetList());
							// 删除产品参数信息
							productGoodInfos.put("PRODUCT_PARAM",
									new Wade.DataMap());
							// 删除产品资费信息
							productGoodInfos.put("PRODUCTS_ELEMENT",
									new Wade.DataMap());
							// 删除缓存资费信息
							productGoodInfos.put("TEMP_PRODUCTS_ELEMENT",
									new Wade.DataMap());
							// 删除再次打开标志
							productGoodInfos.put("IS_REOPEN",new Wade.DataMap());
							$("#productGoodInfos").val(productGoodInfos);
							setMemberParams();
						} else {
							return;
						}
					});
		} else {
			setMemberParams();
		}
	} else {
		setMemberParams();
	}

}

/**
 * 点下一步的时候，进行验证
 */
function validateParamForNext(method) {
	if (method == "CrtUs" || method == "ChgUs") 
	{
		if ($("#operType").val() == "") 
		{
			alert("请选择商品操作类型！");
			return false;
		}
		return setCheckedProducts(method);
	} 
	else if (method == "ChgMb" || "CrtMb") 
	{
		return checkedMemProduct(method)
	}

}

/**
 * 将没有被勾选的产品，从tradeData中移除
 */
function setCheckedProducts(method) {
	// 获取必选产品
	var compixproduct = new Wade.DatasetList($('#grpCompixProduct').val());

	// 获取商产品信息
	var productGoodInfos = new Wade.DataMap($('#productGoodInfos').val());

	// 获取商品编号
	var CURRENT_PRODUCT = productGoodInfos.get("GOOD_INFO").get("BASE_PRODUCT");

	// 获得指定区域的checkbox
	var chks = $("#powerDiv [type=checkbox]");

	// 获取已经订购的产品列表
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");

	// 被选中的产品
	var selectedProducts = new Wade.DatasetList();
	var countDis = 0;
	var countParam = 0;
	var countLocDis = 0;
	var opertype = $("#operType").val();

	var checkedNumber = 0;// 被勾选的checkbox数
	for (var j = 0; j < chks.length; j++) {
		if (chks[j].checked) {
			var isInTrade = "false";// productInfoList是在属性填写的时候往里面塞值的，如果为空或者被选中产品的productindex在里面找不着，都说明产品参数没有填写
			checkedNumber++;
			if (null != productInfoList && productInfoList.length != 0) {
				for (var i = 0; i < productInfoList.length; i++) {
					var productindex = 'ctag'
							+ productInfoList.get(i, "PRODUCT_ID") + '_'+ productInfoList.get(i, "PRODUCT_INDEX");
					if (productindex == chks[j].id) 
					{
						if (opertype == '5') {
							if (productInfoList.get(i, "PRODUCT_OPER_CODE") == '5')
								countDis++;
						}
						if (opertype == '55') {
							if (productInfoList.get(i, "PRODUCT_OPER_CODE") == '55')
								countLocDis++;
						}
						if (opertype == '9') {
							if (productInfoList.get(i, "PRODUCT_OPER_CODE") == '9')
								countParam++;
						}
						if (productInfoList.get(i, "PRODUCT_OPER_CODE"))// 有产品操作类型的，才向下传递
						{
							if (productInfoList.get(i, "PRODUCT_OPER_CODE") != 'EXIST') {
								if (!validateOpCrtUs($("#operType").val(),
										productInfoList.get(i,"PRODUCT_OPER_CODE"),
										productInfoList.get(i, "PRODUCT_ID"))
										|| !validateForce($("#operType").val(),
												productInfoList.get(i,"PRODUCT_OPER_CODE"),
												chks[j].force_tag,
												productInfoList.get(i,"PRODUCT_ID")))
								{
									return false;
								}
							}

							selectedProducts.add(productInfoList.get(i));
						}
						isInTrade = "true";
						continue;
					}
				}
			}
			if (isInTrade == "false") {
				var temp = j + 1;
				alert("请先填写第" + temp + "行,产品:" + chks[j].defaultValue + " 的参数！");
				return false;
			}
		}
	}
	if (checkedNumber == 0) {
		alert("请先选择产品！");
		return false;
	}

	var groupProductId = $('#GRP_PRODUCT_ID').val();
	
	
	// 查询商品资费
	var flag = "";
	Wade.httphandler.submit("", 
			"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
			"isExistDiscnt", "&PRODUCT_ID="+ groupProductId, function(d){
		flag = d.map.ret;
	}, function(e,i){
		$.showErrorMessage("操作失败");
		result = false;
	}, {
		async : false
	});

	// 本地编码转全网编码
	Wade.httphandler.submit("",
			"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
			"qrySpecNumber", "&PRODUCT_ID=" + groupProductId, function(d) {
				var param = d.map.result;
				groupProductId = param;
			}, function(e, i) {
				$.showErrorMessage("操作失败");
				result = false;
			}, {
				async : false
			});
	if (opertype == '5' && countDis == 0 && groupProductId != "010101008"&& groupProductId != "0102001") 
	{
		if("false" == flag)
		{
			alert("操作类型为修改商品资费，请至少修改一个产品的资费！");
			return false;
		}
		else
		{
			$("#MERCH_DIS").val("true");
		}
	}
	if (opertype == '55' && countLocDis == 0) {
		alert("操作类型为修改商品本地资费，请至少修改一个产品的资费！");
		return false;
	}
	if (opertype == '9' && countParam == 0) {
		alert("操作类型为修改商品属性，请至少修改一个产品的属性！");
		return false;
	}
	if (method == "CrtUs") {
		if (selectedProducts.length == 0) {
			if (checkedNumber > 0) {
				alert("请填写产品订购参数！");
			} else {
				alert("请先订购产品！");
			}
			return false;
		}
	}

	productGoodInfos.put("PRODUCT_INFO_LIST", selectedProducts);
	for (var j = 0; j < compixproduct.length; j++) {
		var item = compixproduct.get(j);
		var tag = "false";
		for (var z = 0; z < chks.length; z++) {
			if (chks[z].checked && chks[z].value == item.get("PRODUCT_ID_B")) {
				tag = "true";
			}
		}

		if (tag == "false") {
			alert("办理商品的必选产品[" + item.get("PRODUCT_ID_B") + "："
					+ item.get("PRODUCT_NAME") + "]未勾选!");
			return false;
		}
	}

	// 当前操作为变更中的取消产品订购，需要拼装产品信息
	var isCorrect = addDelProdInfo(productGoodInfos);
	if (!isCorrect) {
		return isCorrect;
	}

	// 当前操作类型为商品暂停、商品恢复，需要拼装产品信息
	isCorrect = addPsAndCntProdInfo(productGoodInfos);
	if (!isCorrect) {
		return isCorrect;
	}

	// 当前操作类型为商品预取消时，需要检查是否所有订购产品都处于正常状态
	isCorrect = checkProductState();
	if (!isCorrect) {
		return isCorrect;
	}

	// 设置商品信息
	var isCorrect = setMerchInfo(productGoodInfos, true);
	if (isCorrect == false) {
		return false;
	}

	// 检查产品依赖互斥关系
	isCorrect = checkBBossProInfoRule(productGoodInfos, method);
	return false;

}

/**
 * 检查产品依赖互斥关系规则
 * 
 * @author liuxx3
 * @date 2014-05-24
 */
function checkBBossProInfoRule(productGoodInfos, method) {

	// 获取商品编号
	var currentProduct = productGoodInfos.get("GOOD_INFO").get("BASE_PRODUCT");

	// 获取已经订购的产品列表
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");

	var groupId = $("#GROUP_ID").val();
	var operType = $("#operType").val();

	var productIdList = new Wade.DataMap();

	// method == "CrtUs" || method == "ChgUs"
	if (method == "CrtUs") {

		if (null != productInfoList && productInfoList.length != 0) {
			for (var i = 0; i < productInfoList.length; i++) {

				var productId = productInfoList.get(i, "PRODUCT_ID");
				var productOperType = productInfoList.get(i,
						"PRODUCT_OPER_CODE");
				var userId = productInfoList.get(i, "USER_ID");
				productIdList.put("PRODUCT_ID_" + (i + 1), productId);
				productIdList.put("PRO_OPER_TYPE_" + (i + 1), productOperType);
				productIdList.put("USER_ID_" + (i + 1), userId);

			}
		}

		$.beginPageLoading('业务验证中....');
		var result = pageFlowRuleCheck(
				'com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupUserRule',
				'checkBBossProInfoRule', '&GROUP_ID=' + groupId + '&OPER_TYPE='
						+ operType + '&PRODUCT_ID_LIST=' + productIdList
						+ '&CURRENT_PRODUCT=' + currentProduct);

		return false;

	} else if (method == "ChgUs") {

		if (null != productInfoList && productInfoList.length != 0) {
			for (var i = 0; i < productInfoList.length; i++) {

				var productId = productInfoList.get(i, "PRODUCT_ID");
				var productOperType = productInfoList.get(i,
						"PRODUCT_OPER_CODE");
				var userId = productInfoList.get(i, "USER_ID");
				productIdList.put("PRODUCT_ID_" + (i + 1), productId);
				productIdList.put("PRO_OPER_TYPE_" + (i + 1), productOperType);
				productIdList.put("USER_ID_" + (i + 1), userId);

			}
		}

		$.beginPageLoading('业务验证中....');
		var result = pageFlowRuleCheck(
				'com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ChangeUserElementRule',
				'checkBBossProInfoRule', '&GROUP_ID=' + groupId + '&OPER_TYPE='
						+ operType + '&PRODUCT_ID_LIST=' + productIdList
						+ '&CURRENT_PRODUCT=' + currentProduct);

		return false;

	}

}

/**
 * @description 成员操作校验(成员新增、删除、暂停、恢复)
 * @author xunyl
 * @date 2013-08-10
 */
function checkedMemProduct(method) {
	// 1- 产品列表为空，说明没有做任何操作，需要抛出异常
	var productGoodInfos = new Wade.DataMap($('#productGoodInfos').val());
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");
	if (null == productInfoList || productInfoList.length == 0) {
		if (method == "ChgMb") {
			alert("新增成员请填写成员参数，变更成员请选择具体的成员操作类型!");
		} else if (method == "CrtMb") {
			alert("新增成员请填写成员参数!");
		}
		return false;
	}

	// 2- 产品对应的状态为成员新增，但是该产品对应的复选框没有被勾选，说明没有做任何操作，需要抛出异常
	var userId = productInfoList.get(0, "USER_ID");
	if (productInfoList.get(0, "MEB_OPER_CODE") == '1') {
		if ($('#ctag' + userId).attr('checked') == false) {
			alert("新增成员，请注意勾选复选框选项!");
			return false;
		}
	}

	// 3- 成员产品对应的复选框已经勾上，但是产品列表中没有该产品，需要抛出异常
	var chks = $("#powerDiv [type=checkbox]");
	var selectedUserId = 'ctag' + userId;
	for (var i = 0; i < chks.length; i++) 
	{
		if ($(chks[i]).attr('disabled') == false
				&& $(chks[i]).attr('checked') == true
				&& $(chks[i]).attr('id') != selectedUserId) 
		{
			var temp = i + 1;
			alert("请先填写第" + temp + "行,产品:" + chks[i].productId + " 的参数!");
			return false;
		}
	}
	// 4- 设置商品信息
	return setMerchInfo(productGoodInfos, false);
}

/**
 * @descrpiton 设置商品信息
 * @author xunyl
 * @date 2013-08-10
 */
function setMerchInfo(productGoodInfos, isGrpOp) {
	// 1- 获取商品信息
	var goodsInfo = productGoodInfos.get("GOOD_INFO");

	// 2- 设置商品操作类型
	goodsInfo.put("MERCH_OPER_CODE", $('#operType').val());

	// 3- 设置业务保障等级
	goodsInfo.put("BUS_NEED_DEGREE", $('#BUS_NEED_DEGREE').val());

	// 4- 设置支付模式
	goodsInfo.put("BIZ_MODE", $("#BIZ_MODE").val());

	// 5- 设置发送方与落地方
	goodsInfo.put("LOCATION", "SEND");

	// 6- 设置套餐生效规则
	if ($("#PAY_MODE").val() == '') {
		alert("套餐生效方式不能为空!");
		return false;
	}
	goodsInfo.put("PAY_MODE", $("#PAY_MODE").val());// 套餐生效规则

	// 7- 保存商品信息
	productGoodInfos.put("GOOD_INFO", goodsInfo);
	$('#productGoodInfos').val(productGoodInfos);

	// 8- 商产品信息通过MC模式传递值，页面参数传递有大小限制
	if (isGrpOp) {
		$.ajax.submit('productGoodPart', 'transProductGoodInfos', '&GROUP_ID='+ $('#GROUP_ID').val(), '', function(data) {
		}, function(error_code, error_info) {
		}, {
			async : false
		});
	} else {
		var grpUserId = $("#GRP_USER_ID").val();
		var mebUserId = $("#MEB_USER_ID").val();
		$.ajax.submit('productGoodPart', 'transProductGoodInfos',
				'&MEB_USER_ID=' + mebUserId + '&PRODUCT_USER_ID=' + grpUserId,
				'', function(data) {
				}, function(error_code, error_info) {
				}, {
					async : false
				});
	}
	return true;
}

/**
 * @description 弹出产品参数页面
 * @author xunyl
 * @date 2013-08-22
 */
function popProductParamPage(e) {
	// 1- 获取商品操作类型
	var operType = $("#operType").val();
	if (operType == "" || operType == null) {
		alert("请选择商品操作类型！");
		return false;
	} else if (operType != "1") {// 商品变更页面用到
		if (e.parents("tr")[0].cells(7).disabled == true) {
			return;
		}
	}

	// 2- 根据商品操作类型获取对应的产品操作类型
	var productOperType = getProductOptype(operType);
	if (productOperType == "" || productOperType == null) {
		alert("请选择产品操作类型！");
		return false;
	}
	$("#" + e.attr("checkId")).attr("checked", "true");
	crtProductSelect($("#" + e.attr("checkId")));
	$("#PROD_OP_TYPE").val(productOperType);

	// 3- 商产品信息通过MC模式传递值，页面参数传递有大小限制
	var transParams = '&productGoodInfos=' + encodeURIComponent($('#productGoodInfos').val()) +	'&GROUP_ID='+ $('#GROUP_ID').val();
	
	$.ajax.submit('', 'transProductGoodInfos', transParams , '', function(data) {
	}, function(error_code, error_info) {
	}, {
		async : false
	});

	// 4- 分情况处理新装或者变更参数页面
	var rowIndex = e.parents("tr")[0].rowIndex;
	if (e.attr("method") == "CrtUs") {

		// 调用产品参数页面
		setTimeout(function() {
			var  params = '&IS_EXIST=false&PRODUCT_INDEX='+ e.attr("productindex") + '&PRODUCT_ID='+ e.attr("productId") 
			+ '&GROUP_ID='+ $('#GROUP_ID').val()+ '&refresh=true&producOperType=' + productOperType +'&rowIndex=' + rowIndex
			+ '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val()+ '&GRP_USER_EPARCHYCODE='+$('#GRP_USER_EPARCHYCODE').val();
			
	       $.popupPage('group.param.bboss.createuser.CreateUserParam2','initCrtUs',params, e.attr("productName")+ '产品信息','1100','640')}, 500);
	} 
	else if (e.attr("method") == "ChgUs") 
	{
		// 获取ESOP接口标志
		var ibSysId = "";
		var eos = $('#EOS').val();
		if (null != eos && eos != "") {
			var eosInfo = new Wade.DataMap(eos);
			ibSysId = eosInfo.get("IBSYSID").toString();
		}

		// 调用产品参数页面
		if (null == ibSysId || ibSysId == "") {
			setTimeout(
				function() {
					var params = '&IS_EXIST=true&PRODUCT_INDEX='+ e.attr("productindex")
						+ '&PRODUCT_STATUS='+ encodeURIComponent(e.attr("productstatus"))
						+ '&PRODUCT_ID='+ e.attr("productId")
						+ '&PRODUCT_ID_A='+ e.attr("productIdA")
						+ '&GROUP_ID='+ $('#GROUP_ID').val()
						+ '&GRP_USER_EPARCHYCODE='+ $('#GRP_USER_EPARCHYCODE').val()
						+ '&BBOSS_USER_ID='+ e.attr("userId")
						+ '&refresh=true&producOperType='+ productOperType
						+ '&FORCE_TAG='+ e.attr("force_tag")
						+ "&rowIndex=" + rowIndex ;
					
					$.popupPage('group.param.bboss.createuser.CreateUserParam2','initChgUs',params,e.attr("productName")+ '产品信息', '1100', '640')}, 500);
		} 
		else {
			var productIdA = e.attr("productIdA");
			// 将本地商品编码转为全网商品编码
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'qrySpecNumber', '&PRODUCT_ID=' + productIdA, function(d) {
						var param = d.map.result;
						productIdA = param;
					}, function(e, i) {
						$.showErrorMessage("操作失败");
						result = false;
					}, {
						async : false
					});
			if (productIdA == "01114001") {
				setTimeout(function() {
					var params = '&IS_EXIST=true&PRODUCT_INDEX='+ e.attr("productindex")
						+ '&PRODUCT_STATUS='+ encodeURIComponent(e.attr("productstatus")) 
						+ '&PRODUCT_ID='+ e.attr("productId") 
						+ '&PRODUCT_ID_A='+ e.attr("productIdA") 
						+ '&GROUP_ID='+ $('#GROUP_ID').val()
						+ '&GRP_USER_EPARCHYCODE='+ $('#GRP_USER_EPARCHYCODE').val()
						+ '&BBOSS_USER_ID='+ e.attr("userId")
						+ '&FORCE_TAG=' + e.attr("force_tag")
						+ '&refresh=true';
					
					$.popupPage(
							'group.param.bboss.createuser.CreateUserParam2',
							'initChgUs', params , e.attr("productName")	+ '产品信息', '1100', '640')}, 500);
			} else {
				alert("ESOP未变更该产品！");
			}
		}
	}

}

/*
 * 弹出参数组的页面
 */
function popParamGroup(e) {
	popupDialog('group.param.bboss.createuser.ParamGroup', 'init',
			'&PRODUCT_ID=' + e.productId + '&PARA_CODE=' + e.paraCode
					+ '&PARA_VALUE=' + e.value + '&BBOSS_USER_ID=' + e.userId
					+ '&refresh=true', '', '750', '650');

}

function changeValueForUpload(oper, domid) {
	$("#" + domid).parents("tr")[0].cells(3).innerText = $("#" + domid).val();
}

function changeValue(tag) {
	var class_type = tag.type;
	if ('text' != class_type) {
		tag.parentNode.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	} else {
		tag.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	}
}

function changeMemberValue(tag) {
	var class_type = tag.type;
	if ('text' != class_type) {
		tag.parentNode.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	} else {
		tag.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	}
}

// 工单开通--详细
function ShowDetail(PRODUCTORDERNUMBER) {
	ajaxSubmit(this, 'showDetail', 'cond_PRODUCTORDERNUMBER='
			+ $(PRODUCTORDERNUMBER).text(), 'OrderDetailPart');
}

// 工单开通--竣工
function OrderComplete() {
	var orders = new Wade.DatasetList();
	// orders.add($("input[name='ORDERROW']:checked"));
	var orderRows = $("input[name='ORDERROW']")
	var orderRowsVal = $("textarea[name='ORDERROW_VAL']")
	for (var i = 0; i < orderRows.length; i++) {
		if (orderRows[i].checked) {
			var order = new Wade.DataMap($(orderRowsVal[i]).text());
			orders.add(order);
		}
	}
	if (orders == null || orders.length == 0) {
		alert('请选择要处理的工单')
	} else if (null == $("input[name='result_tag']:checked").val()) {
		alert('请选择开通状态')
	} else {
		$("#SELECTEDORDERS").val(orders);
		ajaxSubmit(
				this,
				'BbossComplete',
				'&SELECTEDORDERS=' + orders,
				'QueryListPart,MemberDetailPart,OrderDetailPart,SelectdOrdersPart',
				function(data) {
					MessageBox.success("工单开通", "工单处理成功", "");
				});
	}

}

// 工单开通--页面控制
function showError(obj) {
	if (!$('#result_tag').attr("checked")) {
		$("#cond_RSPDESC").css("display", "");
	} else {
		$("#cond_RSPDESC").css("display", "none");
	}
}

// 工单开通--页面控制
function ShowMembNum(PRODUCTORDERNUMBER) {
	$('#ORDER_NUM').value = PRODUCTORDERNUMBER;
	ajaxSubmit(this, 'queryMemberInfos', '', 'MemberDetailPart');
}

// 工单开通--反馈信息区域
function showExtends(obj) {
	// 若为选中
	if ($(obj).attr('checked')) {
		// 检查是否有反馈属性
		ajaxSubmit(this, 'checkBbossOrderOpenExtendsExists', '&ORDERINFO='
				+ $(obj).val(), null, aferCheckExtends, null);
	}
}

// 检查反馈属性后，如果有反馈信息，弹出填写页面，没有则无操作
function aferCheckExtends(data) {
	var existsExtends = data.get("EXISTSEXTENDS");
	var orderInfo = data.get("ORDERINFO");
	var sqlParams = data.get("SQLPARAMS");
	if (existsExtends == "true") {
		popupPage('group.param.bboss.bbossOrderOpen.BbossOrderOpenExtends',
				'initParams', '&ORDERINFO=' + orderInfo + '&SQLPARAMS='
						+ sqlParams + '&refresh=true', 'BBOSS反馈信息', '750',
				'650');
	}
}

function validateOpCrtUs(merchOp, productOp, productName) {
	// 1-新增商品订购 2-取消商品订购 3-商品暂停 4-商品恢复 5-修改商品资费 6-变更成员（保留） 7-修改订购商品组成关系
	// 8-修改缴费关系(保留) 9-修改订购产品属性
	// 说明：其中1、2、3、4不能与其它操作类型组合。

	if (merchOp == "") {
		alert("商品操作类型不能为空！");
		return false;
	}

	// 1-新增产品订购
	// 2-取消产品订购3-产品暂停4-产品恢复5-修改产品资费6-变更成员（保留）8-修改缴费关系（保留）9-修改订购产品属性说明：其中1、2、3、4不能与其它操作类型组合。
	if (productOp == "") {
		alert("产品操作类型不能为空！");
		return false;
	}

	if (merchOp == "2") {
		if (productOp != "2") {
			alert("因为商品操作类型选择了“取消商品订购”，所以[" + productName
					+ "]的产品操作类型只能选“取消产品订购”！");
			return false;
		}
	} else if (merchOp == "3") {
		if (productOp != "3") {
			alert("因为商品操作类型选择了“商品暂停”，所以[" + productName + "]的产品操作类型只能选“产品暂停”！");
			return false;
		}
	} else if (merchOp == "4") {
		if (productOp != "4") {
			alert("因为商品操作类型选择了“商品恢复”，所以[" + productName + "]的产品操作类型只能选“产品恢复”！");
			return false;
		}
	} else if (merchOp == "10") {
		if (productOp != "11") {
			alert("因为商品操作类型选择了“预取消商品订购”，所以[" + productName
					+ "]的产品操作类型只能选“预取消产品订购”！");
			return false;
		}
	} else if (merchOp == "11") {
		if (productOp != "12") {
			alert("因为商品操作类型选择了“冷冻期恢复商品订购”，所以[" + productName
					+ "]的产品操作类型只能选“冷冻期恢复产品订购”！");
			return false;
		}
	}

	return true;
}

function validateForce(poOperType, productOperType, force_tag, product) {
	if (poOperType != '2' && force_tag == '1' && productOperType == '2') {
		alert('商品下必选产品[' + product + ']不能单独取消!');
		return false;
	}
	return true;
}

/*
 * chenyi 复制产品信息
 */
function copyElement(obj) {
	// 复制表格 变量
	var table = $('#productTable')[0];
	var productid = $(obj).attr('productid');
	var elementcnt = $(obj).attr('elementcnt');
	var productindex = $(obj).attr('productindex');
	var newIndex = parseInt(elementcnt) - 1;
	var rowIndex = event.srcElement.parentNode.parentNode.parentNode
			.getAttribute('elementIndex');
	var row = table.rows[parseInt(rowIndex) + 1];
	var newrow = row.insertAdjacentElement("afterEnd", document
			.createElement("TR"));
	var reg = new RegExp('(' + productid + '\\^)([0-9]{1,3})', 'gm');

	for (var i = 0; i < row.cells.length; i++) {
		var cellhtml = row.cells[i].innerHTML;
		var cell = document.createElement("TD");
		cell.innerHTML = repElementIndex(reg, cellhtml, elementcnt);
		newrow.appendChild(cell);
		if (i == 0) {
			cell.style.display = "none";
		}
		if (i == 1) {
			chk = cell.children[0];
			chk.id = 'ctag' + productid + '_' + newIndex;
			chk.checked = '';
			cell.className = "e_center";
		}
		if (i == 3) {
			var hiddenInput = document.createElement("INPUT");
			$(hiddenInput).css("display", "none");
			var newselect = cell.insertAdjacentElement("afterEnd", hiddenInput);
			newselect.id = 'isExist' + productid + '_' + newIndex;
			newselect.value = $('#isExist' + productid + '_' + productindex)
					.val();
			newselect.name = 'isExist' + productid + '_' + newIndex;
		}
		if (i == 5) {
			chk = cell.children[0];
			chk.setAttribute('productindex', newIndex);
			chk.setAttribute('checkId', 'ctag' + productid + '_' + newIndex);
		}
		if (i == 6) {
			chk = cell.children[0];
			chk.setAttribute('productindex', newIndex);
			chk.setAttribute('checkId', 'ctag' + productid + '_' + newIndex);
			chk.onclick = dropElement;
			tag = chk.children[0];
			tag.innerHTML = '取消产品';
		}
		obj.setAttribute('elementcnt', newIndex);
	}
	for (var j = 0; j < table.rows.length; j++) {
		if (j > 0)
			table.rows[j].setAttribute('elementIndex', j - 1);
	}

	// 新增产品赋值
	var mainProductIndex = productid + '_' + productindex;
	var newProductIndex = productid + '_' + newIndex;
	copyNewProductValue(mainProductIndex, newProductIndex, newIndex);
}

/**
 * @description 集团变更中复制产品信息，一般用于变更中可重复新增的产品
 * @author xunyl
 * @date 2016-08-05 
 */
function copyChangeUserElement(obj) {
	//1- 获取产品编号，产品下表，新增产品下标等信息
	var productid = $(obj).attr('productid');	
	var productindex = $(obj).attr('productindex');
	var elementcnt = $(obj).attr('elementcnt');
	var newIndex = parseInt(elementcnt) - 1;

	//2- 获取表行下表
	var rowIndex = event.srcElement.parentNode.parentNode.parentNode
			.getAttribute('elementIndex');
	
	//3- 生成新的行记录
	var table = $('#productTable')[0];
	var row = table.rows[parseInt(rowIndex) + 1];
	var newrow = row.insertAdjacentElement("afterEnd", document
			.createElement("TR"));
	
	//4- 生成字段值
	var reg = new RegExp('(' + productid + '\\^)([0-9]{1,3})', 'gm');
	for ( var i = 0; i < row.cells.length; i++) {
		var cellhtml = row.cells[i].innerHTML;
		var cell = document.createElement("TD");
		cell.innerHTML = repElementIndex(reg, cellhtml, elementcnt);
		newrow.appendChild(cell);
		//4-1 TAG列
		if (i == 0) {
			cell.style.display = "none";
		}
		//4-2  选择列
		if (i == 1) {
			chk = cell.children[0];
			chk.id = 'ctag' + productid + '_' + newIndex;
			chk.checked = '';
			chk.setAttribute('oldValue', 'false');
			cell.className = "e_center";
		}
		//4-2 用户ID去除
		if (i == 2) {
			cell.innerHTML="";
		}
		//4-3  判断产品是否已经受理
		if(i==3){
			var hiddenInput = document.createElement("INPUT");
			$(hiddenInput).css("display", "none");
			var newselect = cell.insertAdjacentElement("afterEnd", hiddenInput);
			newselect.id = 'isExist';
			newselect.value = 'false';//复制的产品，都是不存在用户信息的
			newselect.name = 'isExist';
		}

		//4-4产品状态
		if (i == 5) {
			chk = cell.children[0].children[0];
			chk.value = "新增";
		}
		//4-5 产品操作类型设置
		if (i == 6) {		
			cell.className = "e_center";
			chk = cell.children[0];
			chk.setAttribute('productindex', newIndex);
			chk.setAttribute('checkId', 'ctag' + productid + '_' + newIndex);
			chk.setAttribute('method', 'CrtUs');
			chk.setAttribute('innerText', '订购产品');
		}
		//4-6 操作
		if (i == 7) {
			cell.className = "e_center";
			chk = cell.children[0];
			chk.setAttribute('productindex', newIndex);
			chk.setAttribute('checkId', 'ctag' + productid + '_' + newIndex);
			chk.onclick = dropElement;
			tag = chk.children[0];
			tag.innerHTML = '取消产品';
		}
		//4-7 启停操作
		if(i==8){
			cell.style.display = "none";
			cell.className = "e_center";
			cell.children[0].id = 'pause' + productid + '_' + newIndex;
			
		}
		obj.setAttribute('elementcnt', newIndex);
	}
	for ( var j = 0; j < table.rows.length; j++) {
		if (j > 0)
			table.rows[j].setAttribute('elementIndex', j - 1);
	}

}

/**
 * @descripiton 新增产品赋值
 * @author xunyl
 * @date 2013-11-10
 */
function copyNewProductValue(mainProductIndex, newProductIndex, newIndex) {
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());
	if (null == productGoodInfos.get("PRODUCT_INFO_LIST")) {
		return;
	}

	// 添加产品信息
	productInfoList = new Wade.DatasetList(productGoodInfos.get(
			"PRODUCT_INFO_LIST").toString());
	for (var i = 0; i < productInfoList.length; i++) {
		var productInfo = productInfoList.get(i);
		var productIndex = productInfo.get("PRODUCT_ID") + "_"
				+ productInfo.get("PRODUCT_INDEX");
		if (productIndex == mainProductIndex) {
			var newProductInfo = new Wade.DataMap(productInfo.toString());
			newProductInfo.put("PRODUCT_INDEX", newIndex);
			productInfoList.add(newProductInfo);
			productGoodInfos.put("PRODUCT_INFO_LIST", productInfoList);
		}
	}

	// 添加产品参数信息
	var productParamInfoMap = productGoodInfos.get("PRODUCT_PARAM");
	if (productParamInfoMap != null) {
		var productParamInfoList = productParamInfoMap.get(mainProductIndex);
		if (productParamInfoList != null) {
			productParamInfoMap.put(newProductIndex, productParamInfoList);
			productGoodInfos.put("PRODUCT_PARAM", productParamInfoMap);
		}
	}

	// 添加资费与服务信息
	var productElementInfoMap = productGoodInfos.get("PRODUCTS_ELEMENT");
	if (productElementInfoMap != null) {
		var productElementInfoList = productElementInfoMap
				.get(mainProductIndex);
		if (productElementInfoList != null) {
			productElementInfoMap.put(newProductIndex, productElementInfoList);
			productGoodInfos.put("PRODUCTS_ELEMENT", productElementInfoMap);
		}
	}

	// 添加资费与服务的缓存信息
	var tempProductElementInfoMap = productGoodInfos
			.get("TEMP_PRODUCTS_ELEMENT");
	if (tempProductElementInfoMap != null) {
		var tempProductElementInfoList = tempProductElementInfoMap
				.get(mainProductIndex);
		if (tempProductElementInfoList != null) {
			tempProductElementInfoMap.put(newProductIndex,
					tempProductElementInfoList);
			productGoodInfos.put("TEMP_PRODUCTS_ELEMENT",
					tempProductElementInfoMap);
		}
	}

	// 添加是否重复开启标志
	var isReopenInfoMap = productGoodInfos.get("IS_REOPEN");
	if (isReopenInfoMap != null) {
		isReopenInfoMap.put(newProductIndex, true);
		productGoodInfos.put("IS_REOPEN", isReopenInfoMap);
	}

	$('#productGoodInfos').val(productGoodInfos);
}

/*
 * 删除产品信息
 */
function dropElement() {
	var table = $('#productTable')[0];
	var productid = $(this).attr('productid');
	var productindex = $(this).attr('productindex');

	table.deleteRow(parseInt(event.srcElement.parentNode.parentNode.parentNode
			.getAttribute('elementIndex')) + 1);

	for (var j = 0; j < table.rows.length; j++) {
		if (j > 0)
			table.rows[j].setAttribute('elementIndex', j - 1);
	}

	// 取消产品清除原有值
	var delProductIndex = productid + '_' + productindex;
	delProductValue(delProductIndex);
}

/**
 * @descripiton 取消产品清除原有值
 * @author xunyl
 * @date 2013-11-10
 */
function delProductValue(delProductIndex) {
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());
	if (null == productGoodInfos.get("PRODUCT_INFO_LIST")) {
		return;
	}

	// 删除产品信息
	productInfoList = new Wade.DatasetList(productGoodInfos.get(
			"PRODUCT_INFO_LIST").toString());
	for (var i = 0; i < productInfoList.length; i++) {
		var productInfo = productInfoList.get(i);
		var productIndex = productInfo.get("PRODUCT_ID") + "_"
				+ productInfo.get("PRODUCT_INDEX");
		if (productIndex == delProductIndex) {
			productInfoList.removeAt(i);
			productGoodInfos.put("PRODUCT_INFO_LIST", productInfoList);
			break;
		}
	}

	// 删除产品参数信息
	var productParamInfoMap = productGoodInfos.get("PRODUCT_PARAM");
	if (productParamInfoMap != null) {
		var productParamInfoList = productParamInfoMap.get(delProductIndex);
		if (productParamInfoList != null) {
			productParamInfoMap.removeKey(delProductIndex);
			productGoodInfos.put("PRODUCT_PARAM", productParamInfoMap);
		}
	}

	// 删除资费与服务信息
	var productElementInfoMap = productGoodInfos.get("PRODUCTS_ELEMENT");
	if (productElementInfoMap != null) {
		var productElementInfoList = productElementInfoMap.get(delProductIndex);
		if (productElementInfoList != null) {
			productElementInfoMap.removeKey(delProductIndex);
			productGoodInfos.put("PRODUCTS_ELEMENT", productElementInfoMap);
		}
	}

	// 删除资费与服务的缓存信息
	var tempProductElementInfoMap = productGoodInfos
			.get("TEMP_PRODUCTS_ELEMENT");
	if (tempProductElementInfoMap != null) {
		var tempProductElementInfoList = tempProductElementInfoMap
				.get(delProductIndex);
		if (tempProductElementInfoList != null) {
			tempProductElementInfoMap.removeKey(delProductIndex);
			productGoodInfos.put("TEMP_PRODUCTS_ELEMENT",
					tempProductElementInfoMap);
		}
	}

	// 删除是否重复开启标志
	var isReopenInfoMap = productGoodInfos.get("IS_REOPEN");
	if (isReopenInfoMap != null) {
		var isReopenInfoMap = productGoodInfos.get("IS_REOPEN");
		isReopenInfoMap.removeKey(delProductIndex);
		productGoodInfos.put("IS_REOPEN", isReopenInfoMap);
	}
	$('#productGoodInfos').val(productGoodInfos);
}

function repElementIndex(reg, html, index) {
	return (html.replace(reg, function($1, $2, $3) {
		return $2 + (parseInt(index) - 1);
	}));
}

var count = 1;
// 2- 定义产品对象
var countMap = new Wade.DataMap();

// 添加一组参数
function addGroupParams(Obj) {
	// 获取最大组数
	var maxGroup = $(Obj).attr('maxGroup');
	if (maxGroup == "undefined" || maxGroup == null) {
		count = 1;
	} else {
		count = maxGroup;
	}
	// 获取当前行的属性组编号
	var groupAttr = $(Obj).attr('desc');
	// 获取相关属性组的所有属性
	var groupAttrAll = $("button[desc=" + groupAttr + "]");// 无论新增多少组属性，新增按钮只有一个，确保了每次新增的属性个数都等于属性组原有属性个数
	// 相关属性组没有任何属性，事实上是不可能的
	if (groupAttrAll == "undefined" || groupAttrAll == null) {
		return;
	}
	if (countMap.get(groupAttr) == "undefined"
			|| countMap.get(groupAttr) == null) {
		countMap.put(groupAttr, count);
	} else {
		count = countMap.get(groupAttr);
	}
	count++;
	countMap.put(groupAttr, count);
	// 用于判别当前添加到第几组属性了
	// 循环属性组对应的每个属性
	for (var i = 0; i < groupAttrAll.length; i++) {
		addParam($(groupAttrAll[i]).attr('paramCode'), groupAttr);
	}
}

// 增加一行
function createRow(html) {
	var div = document.createElement("div");
	html = "<table><tbody>" + html + "</tbody></table>";
	div.innerHTML = html;
	return div.lastChild.lastChild;
}

// 删除一组参数
function deleteGroupParams(Obj) {
	var delBtnId = Obj.id;// 删除按钮的ID
	var groupAttr = $(Obj).attr('groupAttr');
	var delBtnIdArr = delBtnId.split("_");
	var count1 = delBtnIdArr[3];// 获取count值
	if (countMap.get(groupAttr) == "undefined"
			|| countMap.get(groupAttr) == null) {
		count = $('[desc =' + groupAttr + ']').length;
	} else {
		count = countMap.get(groupAttr);
	}
	if (count1 != count) {
		alert("请按序号大到小删除");
		return;
	}

	$('span[name=PRODUCT_PARAM_CODE]').each(
			function() {
				var buttonObj = $("#BUTTON_DEL_" + $.attr(this, "value") + "_"
						+ count);
				if (buttonObj != "undefined" && buttonObj != null
						&& buttonObj.length > 0) {// 说明该删除按钮时存在的，属于当前要删除的
					var obj = buttonObj[0];
					if ($(obj).attr('groupAttr') == groupAttr) {
						deleteParam(buttonObj[0]);// 删除表格行
					}
				}
			});

	count--;
	countMap.put(groupAttr, count);
}

// 删除一个参数行
function deleteParam(Obj) {
	var tr = Obj.parentNode.parentNode;
	var tbody = tr.parentNode;
	tbody.removeChild(tr);
}

// 集团产品成员新增 设置成员参数
function bbossSetUserParam(e) {
	// 1- 获取页面参数信息
	var grpUserId = $(e).attr('grpUserId');
	var mebUserId = $("#MEB_USER_ID").val();
	var productId = $(e).attr('productId');
	var mebEparchCode = $('#MEB_EPARCHY_CODE').val();
	var productName = $(e).attr('productName');
	var productGoodInfos = $('#productGoodInfos').val();

	// 2- 拼装弹出页面参数
	var param = '&IS_EXIST=false&GRP_USER_ID=' + grpUserId + '&MEM_USER_ID='+ mebUserId + '&PRODUCT_ID=' + productId + '&refresh=true'
			+ '&MEB_EPARCHY_CODE=' + mebEparchCode + '&PRODUCT_OPER_TYPE=1'+ '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val();
	
	var transParam = '&productGoodInfos='+ encodeURIComponent($('#productGoodInfos').val()) + '&MEB_USER_ID=' + mebUserId + '&PRODUCT_USER_ID=' + grpUserId;
	
	// 3- 商产品信息通过MC模式传递值，页面参数传递有大小限制
	$.ajax.submit('', 'transProductGoodInfos', transParam , '', function(data) 
	{
		if (data.map.result == "true") 
		{
			$.popupPage(
					'group.param.bboss.creategroupmember.CreateMemberParam2',
					'initCrtMb', param, productName + '产品信息', 800, 640, $(e).attr('id'));

			if ($('#3333') != null) {
				$('li[title^=3333]').css('display', 'none');
			}

		} else {
			alert("产品状态为非正常状态,不能添加成员");
		}
	}, function(error_code, error_info) {
	}, {
		async : false
	});

}

// 集团产品成员修改 修改成员参数
function modifyMemberParam(e) {
	var mebEparchy = $('#MEB_EPARCHY_CODE').val();
	var mebstate = $(e).attr('mebstate');
	var productId = $(e).attr('productId');
	var grpUserId = $(e).attr('grpUserId');
	var memUserId = $("#MEB_USER_ID").val();
	var productGoodInfos = $('#productGoodInfos').val();
	var productName = $(e).attr('productName');

	var param = '&IS_EXIST=true&MEB_EPARCHY_CODE=' + mebEparchy
			+ '&GRP_USER_ID=' + grpUserId + '&PRODUCT_STATUS_CODE=' + mebstate
			+ '&MEM_USER_ID=' + memUserId + '&PRODUCT_ID=' + productId
			+ '&refresh=true' + '&PRODUCT_OPER_TYPE=6';

	$.ajax.submit('', 'transProductGoodInfos', '&productGoodInfos='
			+ $('#productGoodInfos').val() + '&MEB_USER_ID=' + memUserId
			+ '&PRODUCT_USER_ID=' + grpUserId, '', function(data) {
		if (data.map.result == "true") {
			$.popupPage(
					'group.param.bboss.changememelement.ChangeMemberElement2',
					'initChgMb', param, productName + '产品信息', 800, 640, $(e).attr('id'));
		} else {
			alert("产品状态为非正常状态,不能添加成员");
		}
	}, function(error_code, error_info) {
	}, {
		async : false
	});
}

// 提交时校验
function validBaseInfo() {
	return $.validate.verifyAll('orderinfotabset');
}

/**
 * @description 商品操作类型变更,重置页面信息
 * @author xunyl
 * @date 2013-07-06
 */
function resetBbossInfo(e) {
	// 1- 获取商品操作类型
	var merchOperType = $(e).val();

	// 2- 刷新产品列表
	$.ajax.submit('', 'refreshProducts', '&GROUP_ID=' + $('#GROUP_ID').val(),
			'powerDiv,productGoodPart', function(data) {
				setProductOper(merchOperType)
			}, function(e, i) {
			});
}

/**
 * @description 通过商品操作类型控制产品操作类型
 * @author xunyl
 * @date 2013-07-06
 */
function setProductOper(merchOperType) {

	// 1- 商品操作类型为商品暂停，商品恢复，预取消商品订购，冷冻期恢复商品订购，取消商品订购
	if (merchOperType == '10' || merchOperType == '11' || merchOperType == '2') {
		var chks = $("#powerDiv [type=checkbox]");
		for (var j = 0; j < chks.length; j++) {
			chks[j].disabled = true;
			$(chks[j]).parents("tr")[0].cells(7).disabled = true;
		}
		return;
	}

	// 2- 商品操作类型为修改商品资费，修改产品属性
	if (merchOperType == '5' || merchOperType == '9' || merchOperType == '55') {
		var chks = $("#powerDiv [type=checkbox]");
		for (var j = 0; j < chks.length; j++) {
			chks[j].disabled = true;
			if (chks[j].checked == false || getProductStatus(j) == "暂停") {
				$(chks[j]).parents("tr")[0].cells(7).disabled = true;
			}
		}
		return;
	}

	// 3- 商品操作类型为修改订购商品组成关系，已订购商品只能取消订购
	if (merchOperType == '7') {
		var chks = $("#powerDiv [type=checkbox]");
		for (var j = 0; j < chks.length; j++) {
			if (chks[j].checked) {
				$(chks[j]).attr('disabled', false);
				$(chks[j]).parents("tr")[0].cells(6).disabled = true;
			}
		}
		return;
	}

	// 4- 商品操作类型为商品暂停，状态为正常的产品能够进行暂停
	if (merchOperType == '3') {
		$("#col_PAUSE_CONTINUE").css('display', 'block');
		var chks = $("#powerDiv [type=checkbox]");
		for (var j = 0; j < chks.length; j++) {
			$(chks[j]).parents("tr")[0].cells(8).style.display = "block";
			chks[j].disabled = true;
			$(chks[j]).parents("tr")[0].cells(7).disabled = true;
			if (getProductStatus(j) == "正常") {
				var checkId = $(chks[j]).attr("id");
				var pauseId = "pause" + checkId.substring(4);
				$("#" + pauseId).attr('innerText', '暂停');
			}
		}
	}

	// 5- 商品操作类型为商品恢复，状态为暂停的产品能够进行恢复
	if (merchOperType == '4') {
		$("#col_PAUSE_CONTINUE").css('display', 'block');
		var chks = $("#powerDiv [type=checkbox]");
		for (var j = 0; j < chks.length; j++) {
			$(chks[j]).parents("tr")[0].cells(8).style.display = "block";
			chks[j].disabled = true;
			$(chks[j]).parents("tr")[0].cells(7).disabled = true;
			if (chks[j].checked && getProductStatus(j) == "暂停") {
				var checkId = $(chks[j]).attr("id");
				var pauseId = "pause" + checkId.substring(4);
				$("#" + pauseId).css("display", "block");
				$("#" + pauseId).attr('innerText', '恢复');
			}
		}
	}
}

/**
 * @descripiton 获取产品状态
 * @author xunyl
 * @date 2013-08-21
 */
function getProductStatus(index) {
	var chks = $("#powerDiv [type=checkbox]");
	var str = chks[index].id;
	var productId = str.substring(str.indexOf("g") + 1, str.length);
	// var productId=$(chks[index]).val();
	var productStatus = $("#productstatus_" + productId).val();
	return productStatus;
}

/*
 * @description 设置产品状态，如果商品产品操作类型为暂停，则改为已暂停，为恢复则改为已恢复 @author xunyl @date
 * 2013-08-22
 */
function setProductStatus(e) {
	// 1- 获取商品操作类型
	var merchOpType = $('#operType').val();

	// 2- 获取当前产品操作类型的值(暂停、恢复、已暂停、已恢复)
	var currentState = $(e).attr('innerText');

	// 3- 修改原有的操作类型值，暂停/恢复修改为已暂停/已恢复，并且将字体颜色置灰;已暂停/已恢复修改为暂停/恢复，别且将字体颜色置亮
	if (currentState == "暂停") {
		MessageBox.confirm("提示信息", "暂停后产品将不能正常使用，确实需要暂停该产品吗?", function(btn) {
			if (btn == 'ok') {
				$(e).attr("innerText", "已暂停");
			} else {
				return;
			}
		});
	} else if (currentState == "恢复") {
		MessageBox.confirm("提示信息", "恢复后产品将按照正常标准收取费用，确实需要恢复该产品吗?",
				function(btn) {
					if (btn == 'ok') {
						$(e).attr("innerText", "已恢复");
					} else {
						return;
					}
				});
	} else if (currentState == "已暂停") {
		$(e).attr("innerText", "暂停");
	} else if (currentState == "已恢复") {
		$(e).attr("innerText", "恢复");
	} else {
		alert("程序错误，请及时通知相关人员修改!");
	}
}

/**
 * @description 根据商品操作类型获取对应的产品操作类型
 * @author xunyl
 * @date 2013-07-08
 */
function getProductOptype(operType) {
	// 1- 定义产品操作类型
	var productOperType = "";

	// 2- 商品操作类型为新增商品订购，对应产品操作类型为新增产品订购或者产品预受理
	if (operType == '1') {
		productOperType = $("#PROD_OP_TYPE").val();
	}

	// 3- 商品操作类型为修改商品资费，对应产品操作类型为修改产品资费
	if (operType == '5') {
		productOperType = "5";
	}

	// 3- 商品操作类型为修改商品本地资费，对应产品操作类型为修改产品本地资费
	if (operType == '55') {
		productOperType = "55";
	}

	// 4- 商品操作类型为修改订购商品组成关系，对应产品操作类型为新增产品订购
	if (operType == '7') {
		productOperType = "1";
	}

	// 5- 商品操作类型为修改订购产品属性，对应产品操作类型为修改订购产品属性
	if (operType == '9') {
		productOperType = "9";
	}

	// 6- 商品操作类型为商品暂停，对应产品操作类型为产品暂停
	if (operType == '3') {
		productOperType = "3";
	}

	// 7- 商品操作类型为商品恢复，对应产品操作类型为产品恢复
	if (operType == '4') {
		productOperType = "4";
	}

	// 8- 变更成员
	if (operType == '6') {
		productOperType = "6";
	}

	// 9- 业务开展省新增或删除
	if (operType == '13') {
		productOperType = "13";
	}

	// 10- 返回产品操作类型
	return productOperType;
}

/**
 * @description 新增属性组
 * @author xunyl
 * @date 2013-07-18
 */
function addParam(paraCode, groupAttr) {
	// 1- 拼装标记列(TAG)数据
	var html = '<td style="display:none">0</td>';
	// 2- 拼装属性编号列数据
	var paramCodeObj = null;
	$('span[name=PRODUCT_PARAM_CODE]').each(function() {
		if ($.attr(this, "value") == paraCode) {
			paramCodeObj = this;
			return true;
		}
	});

	html = html + paramCodeObj.parentNode.outerHTML;

	// 3- 拼装属性名称列数据
	var innerHtml = $("#PARAM_NAME_" + paraCode)[0].innerHTML + "" + count;
	html = html + '<td><span class="e_required">' + innerHtml + '</span></td>';

	// 4- 拼装属性值列
	html = html + '<td></td>';

	// 5- 拼装属性前缀
	var partValue = $("#FRONT_PART_" + paraCode).val();
	html = html + '<td style="display: none">' + partValue + '</td>';

	// 6- 拼装输入列(先清除原有输入列值，最后再将该值还原)
	var paramValueObj = $("#input_" + paraCode);
	var tmpValue = paramValueObj.val();
	paramValueObj.val("");
	html = html + $(paramValueObj[0]).parents("td")[0].outerHTML;
	paramValueObj.val(tmpValue);

	// 7- 拼装属性后缀
	var partValue = $("#AFTER_PART_" + paraCode).val();
	html = html + '<td style="display: none">' + partValue + '</td>';
	
	// 8- 下载
	if($("#PARAM_DOWNLOAD_" + paraCode).parents("td").size() > 0) {
		html = html + $("#PARAM_DOWNLOAD_" + paraCode).parents("td")[0].outerHTML;
	}else {
		html = html + '<td></td>';
	}
	
	//9- 导入
	if ($("#PARAM_DOWNLOAD_" + paraCode).parents("td").size() > 0) {
		html = html + $("#PARAM_IMPORT_" + paraCode).parents("td")[0].outerHTML;
	}else {
		html = html + '<td></td>';
	}
	
	// 10- 拼装备注列值
	if ($("#PARAM_DOWNLOAD_" + paraCode).parents("td").size() > 0) {
		html = html + $("#PARAM_DESC_" + paraCode).parents("td")[0].outerHTML;
	}else {
		html = html + '<td></td>';
	}

	// 11- 拼装按钮列值
	var temDisPlayable = $('#BUTTON_DEL_' + paraCode).css('display');
	var displayable = "";
	if (groupAttr.indexOf(paraCode) != 0) {// 判断是否为主属性
		var displayable = "none";
	}
	var btnHtml = '<td> <button style="display:' + displayable
			+ '" jwcid="@Any" id="BUTTON_DEL_' + paraCode + '_' + count;
	btnHtml += '" onclick="deleteGroupParams(this)" value="删除" type="button" class="e_button-right" groupAttr="'
			+ groupAttr + '">';
	btnHtml += '<i class="e_ico-delete"></i><span>删除</span></button></td>';
	html += btnHtml;

	// 12- 拼装属性组间的区分标志
	var groupattrHtml = '<td style="display: none">';
	groupattrHtml += '<input name="GROUPATTR_FLAG_' + paraCode;
	groupattrHtml += '" id="GROUPATTR_FLAG_' + paraCode;
	groupattrHtml += '" value=' + count + ' desc="属性组间的区分标志" /></td>';
	html += groupattrHtml;

	// 13- 拼装行数据
	var tablen = new Array();
	tablen[0] = "<tr>" + html + "</tr>";

	// 14- 获取表格对象，将行数据添加到表格对象上
	var productParamTable = $("#productParamTable");
	productParamTable[0].appendChild(createRow(tablen.join("")));
}

/**
 * @description 变更操作中选择商品组合关系变更，要么做产品删除，要么做产品新增
 * @author xunyl
 * @date 2013-08-05
 */
function crtProductSelect(e) {
	// 1- 获取商品操作类型
	var merchOpType = $('#operType').val();

	// 2- 商品操作类型非商品组合关系变更则直接退出
	if (merchOpType != '7') {
		return;
	}

	// 3- 如果现有的checkBox值与初始化值全部一致，则将所有checkBox都设置成可编辑状态
	var isSame = true;
	var chks = $("#powerDiv [type=checkbox]");
	for (var j = 0; j < chks.length; j++) {
		if ($(chks[j]).attr('checked').toString() != $(chks[j])
				.attr('oldValue')) {
			isSame = false;
			break;
		}
	}
	if (isSame == true) {
		chks.attr('disabled', false);
		for (var j = 0; j < chks.length; j++) {
			if ($(chks[j]).attr('checked') == false) {
				$(chks[j]).parents("tr")[0].cells(7).disabled = false;
			}
		}
		return;
	}

	// 4- 如果有产品的checkBox为不可编辑状态，说明之前已经有设置过编辑状态，直接推出
	var flag=true;
	for (var j = 0; j < chks.length; j++) {
		if ($(chks[j]).attr('disabled') == true) {
			return;
		}
		
		if ($(chks[j]).attr('oldValue') != 'true'){
			flag = false;
		}

	}
	if(flag){ //全部产品都订购了
		return;
	}

	// 5- 如果现有的值为true,表示新增产品订购，将已定购产品设置为不可编辑状态，否则表示删除产品，将为订购产品设置成不可编辑状态
	if ($(e).attr('checked') == true) {
		for (var j = 0; j < chks.length; j++) {
			if ($(chks[j]).attr('oldValue') == 'true'
					&& $(chks[j]).attr('id') != $(e).attr('id')) {
				$(chks[j]).attr('disabled', true);
			}
		}
	} else {
		for (var j = 0; j < chks.length; j++) {
			if ($(chks[j]).attr('oldValue') == 'false'
					&& $(chks[j]).attr('id') != $(e).attr('id')) {
				$(chks[j]).attr('disabled', true);
				$(chks[j]).parents("tr")[0].cells(7).disabled = true;
			}
		}
	}
}

/**
 * @description 变更操作中选择商品组合关系变更，拼装被删除产品信息至商产品对象
 * @author xunyl
 * @date 2013-08-05
 */
function addDelProdInfo(productGoodInfos) {
	// 1- 判断是否为商品组合关系变更，非组合关系变更直接退出
	var merchOpType = $('#operType').val();
	if (merchOpType != '7') {
		return true;
		;
	}

	// 2- 判断是否存在产品新增或者产品删除，不存在抛出警告信息
	var chks = $("#powerDiv [type=checkbox]");
	for (var i = 0; i < chks.length; i++) {
		if ($(chks[i]).attr('oldValue') != $(chks[i]).attr('checked')
				.toString()) {
			break;
		}
	}
	if (i == chks.length) {
		alert("请选择需要新增或者删除的产品!");
		return false;
	}

	// 3-如果是产品删除，则判断被删除的产品是否为基本产品(必选产品)，基本产品不能被删除
	for (var j = 0; j < chks.length; j++) {
		if ($(chks[j]).attr('oldValue') != $(chks[j]).attr('checked')
				.toString()
				&& $(chks[j]).attr('checked') == false) {
			// 3-1 获取对应的集团产品编号
			var productId = $(chks[j]).parents("tr")[0].cells(3).innerText;

			// 3-2 获取产品用户编号
			var userId = $(chks[j]).parents("tr")[0].cells(2).innerText;

			// 3-3 获取产品下标
			var productIndex = $(chks[j]).attr('id').split("_")[1];

			// 3-4 根据集团产品编号判断是否为基本产品
			if ($(chks[j]).attr('isMustSelect') == 'true') {
				alert("产品" + productId + "为必选产品，不能被删除");
				$(chks[j]).attr('checked', true)
				return false;
			} else {
				chgProductInfo(productGoodInfos, userId, productId,
						productIndex, "2");
			}
		}
	}

	return true;
}

/**
 * @description 拼装变更操作中产品删除、产品暂停、产品恢复的产品信息至商产品对象中
 * @author xunyl
 * @date 2013-07-02
 */
function chgProductInfo(productGoodInfos, userId, productId, productIndex,
		productStatu) {
	// 1- 获取老产品集
	var productInfoList = new Wade.DatasetList();
	if (null != productGoodInfos.get("PRODUCT_INFO_LIST")) {
		productInfoList = new Wade.DatasetList(productGoodInfos.get(
				"PRODUCT_INFO_LIST").toString());
	}

	// 2- 定义产品对象
	var productInfo = new Wade.DataMap();

	// 3- 拼装产品对象
	productInfo.put("PRODUCT_ID", productId);
	productInfo.put("USER_ID", userId);
	productInfo.put("PRODUCT_OPER_CODE", productStatu);
	productInfo.put("PRODUCT_INDEX", productIndex);

	// 4- 检查当前产品信息是否存在于老产品集中（如果存在则先删除老产品信息，再添加新产品信息）
	for (var i = 0; i < productInfoList.length; i++) {
		if (userId == productInfoList.get(i, "USER_ID")) {
			productInfoList.removeAt(i);
			break;
		}
	}
	productInfoList.add(productInfo);

	// 5- 将产品信息保存至商产品信息中
	productGoodInfos.put("PRODUCT_INFO_LIST", productInfoList);
	$('#productGoodInfos').val(productGoodInfos);
}

/**
 * @description 拼装变更操作中暂停或者恢复的产品信息至商产品对象中
 * @author xunyl
 * @date 2013-08-22
 */
function addPsAndCntProdInfo(productGoodInfos) {
	// 1- 判断当前的商品操作类型是否为商品暂停或者恢复，非商品暂停或者恢复直接退出
	var merchOpType = $('#operType').val();
	if (merchOpType != '3' && merchOpType != '4') {
		return true;
		;
	}

	// 2- 更新商产品对象中已暂停或者已恢复的产品状态信息
	var isSelectProdInfos = false;
	var chks = $("#powerDiv [type=checkbox]");
	for (var i = 0; i < chks.length; i++) {
		var checkId = $(chks[i]).attr("id");
		var pauseId = "pause" + checkId.substring(4);
		if (merchOpType == "3") {
			if ($("#" + pauseId).attr("innerText") == "已暂停") {
				var productId = $(chks[i]).val();
				var userId = $(chks[i]).parents("tr")[0].cells(2).innerText;
				var productIndex = checkId.split("_")[1];
				chgProductInfo(productGoodInfos, userId, productId,
						productIndex, "3");
				isSelectProdInfos = true;
			}
		} else if (merchOpType == "4") {
			if ($("#" + pauseId).attr("innerText") == "已恢复") {
				var productId = $(chks[i]).val();
				var userId = $(chks[i]).parents("tr")[0].cells(2).innerText;
				var productIndex = checkId.split("_")[1];
				chgProductInfo(productGoodInfos, userId, productId,
						productIndex, "4");
				isSelectProdInfos = true;
			}
		}
	}

	// 3- 没有暂停或者恢复，抛出警告信息
	if (isSelectProdInfos == false) {
		if (merchOpType == "3") {
			alert("商品暂停操作中，至少选择一个产品进行暂停，请选择需要被暂停的产品!");
		} else if (merchOpType == "4") {
			alert("商品恢复操作中，至少选择一个产品进行恢复，请选择需要恢复的产品!");
		}
		return false;
	}
	return true;
}

/**
 * @description 初始化ESOP信息
 * @author xunyl
 * @dae 2013-10-09
 */
function setEsopInit() {
	// 1- 判断是否为商品组合关系变更，非组合关系变更直接退出
	var merchOpType = $('#operType').val();
	if (merchOpType != '7') {
		return true;
		;
	}

	// 2- 根据产品操作类型(产品新增/删除)
	var esopProductInfo = new Wade.DataMap($("#ESOP_PRODUCT_INFO").val());
	var goodInfo = esopProductInfo.get("PRODUCT_GOOD_INFO");
	var productInfoList = goodInfo.get("PRODUCT_INFO");
	if (productInfoList == null || productInfoList.length <= 0) {
		return true;
	}
	for (var i = 0; i < productInfoList.length; i++) {
		var productInfo = productInfoList.get(i);
		var productOpType = productInfo.get("PRODUCT_OPER_CODE");
		var productUserId = productInfo.get("USER_ID");
		var productId = productInfo.get("PRODUCT_ID");
		// 控制产品信息界面显示(删除时比较产品用户编号，新增时比较产品编号)
		if ("1" == productOpType) {// 产品新增

		} else if ("2" == productOpType) {// 产品删除
			dealEsopDel(productUserId);
		} else {
			alert("商品组成关系变更中，产品操作类型[" + productOpType + "]不存在!");
		}
	}
}

/**
 * @description 集团变更，esop删除产品信息，初始化需要清除复选框的勾选状态
 * @author xunyl
 * @date 2013-10-10
 */
function dealEsopDel(esopProductUserId) {
	var chks = $("#powerDiv [type=checkbox]");
	for (var i = 0; i < chks.length; i++) {
		var productUserId = $(chks[i]).parents("tr")[0].cells(2).innerText;
		if (esopProductUserId == productUserId) {
			$(chks[i]).attr('checked', false);
			crtProductSelect($(chks[i]));
		}
	}
}

/**
 * @description 集团变更，esop新增产品信息，初始化需要复选框处于勾选状态
 * @author xunyl
 * @date 2013-10-10
 */
function dealEsopAdd(esopProductId) {
	var chks = $("#powerDiv [type=checkbox]");
	for (var i = 0; i < chks.length; i++) {
		var productId = $(chks[i]).val();
		if (esopProductId == productId) {
			$(chks[i]).attr('checked', true);
			crtProductSelect($(chks[i]));
		}
	}
}

/**
 * @description 成员添加时，限制一次只能添加一个成员产品
 * @author xunyl
 * @date 2013-08-09
 */
function setAddMemPage(e) {
	// 1- 获取商产品信息
	var productGoodInfos = new Wade.DataMap($('#productGoodInfos').val());

	// 2- 判断是否存在产品信息，如果不存在,则将页面所有checkBox设置为未选择状态
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");
	if (null == productGoodInfos.get("PRODUCT_INFO_LIST")) {
		var chks = $("#powerDiv [type=checkbox]");
		chks.attr('checked', false);
		return;
	}

	// 3- 如果存在产品信息，则将该产品对应的checkBox设置为选择状态，其它均设置为未选择状态
	var productId = productInfoList.get(0, "PRODUCT_ID");
	var chks = $("#powerDiv [type=checkbox]");
	for (var j = 0; j < chks.length; j++) {
		if (productId == $(chks[j]).attr('productId')) {
			$(chks[j]).attr('checked', true);
		} else {
			$(chks[j]).attr('checked', false);
		}
	}
}

/**
 * @description 成员变更时，限制一次只能操作一个成员产品
 * @author xunyl
 * @date 2013-08-09
 */
function setChgMemPage(e) {
	// 1- 获取商产品信息
	var productGoodInfos = new Wade.DataMap($('#productGoodInfos').val());

	// 2- 判断是否存在产品信息，如果不存在,则将未订购的成员产品checkBox设置为非选择状态
	var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");
	if (null == productGoodInfos.get("PRODUCT_INFO_LIST")) {
		var chks = $("#powerDiv [type=checkbox]");
		for (var i = 0; i < chks.length; i++) {
			if ($(chks[i]).attr('disabled') == false) {
				$(chks[i]).attr('checked', false);
			}
		}
		return;
	}

	// 3- 如果存在产品信息，则将该产品的checkBox设置为选择状态，其它未订购的产品设置为未选择状态
	var productId = productInfoList.get(0, "PRODUCT_ID");
	var chks = $("#powerDiv [type=checkbox]");
	for (var j = 0; j < chks.length; j++) {
		if (productId == $(chks[j]).attr('productId')
				&& $(chks[j]).attr('disabled') == false) {
			$(chks[j]).attr('checked', true);
		} else if ($(chks[j]).attr('disabled') == false) {
			$(chks[j]).attr('checked', false);
		}
	}
}

/**
 * 点击左侧包之后,执行的自定义方法
 */
function pkgListAfterSelectAction(pkg) {
	var selfParam = "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&PRODUCT_ID="
			+ $("#PRODUCT_NUMBER").val();
	var eparchyCode = $("#MEB_EPARCHY_CODE").val();
	pkgElementList.renderComponent(pkg, eparchyCode, selfParam);
}

/**
 * @description 预取消商品订购时，需要检测是否所有产品状态都为正常状态
 * @author xunyl
 * @date 2014-04-18
 */
function checkProductState() {
	// 1- 判断当前的商品操作类型是否为商品预取消订购，非商品预取消订购直接退出
	var merchOpType = $('#operType').val();
	if (merchOpType != '10') {
		return true;
		;
	}

	// 2- 循环检查所有已定购产品的状态，如果非正常状态提示客户经理
	var chks = $("#powerDiv [type=checkbox]");
	for (var i = 0; i < chks.length; i++) {
		if ($(chks[i]).attr('checked') == true) {
			// var productId = $(chks[i]).val();
			var str = chks[i].id;
			var productId = str.substring(str.indexOf("g") + 1, str.length);
			var proproductId = productId.substring(0, productId.indexOf("_"));
			if ($('#productstatus_' + productId).val() != '正常') {
				alert("产品编号[" + proproductId + "]对应的状态为非正常状态，不能进行预取消操作！");
				return false;
			}
		}
	}
	return true;
}
function initProductInfo() {
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == true) {
		selectedElements.isEffectNow = effectNow;
	}
}

function productTabSwitchAction(ptitle, title) {

	if ($('#elementPanel').length != 0
			&& $('#elementPanel').css('display') == 'block') {
		$('#elementPanel').css('display', 'none');
	}

	return true;
}

/**
 * @descripiton 一点支付业务中，成员附件等参数导入时需要生成批次号，导入完成时用来从缓存中读取文件名称
 * @author xunyl
 * @date 2015-10-28
 */
function setMqParam(domid){
	//1- 生成批次号并保存，导入完成后，根据批次号获取文件名
	$.ajax.submit('', 'getBatchId', '', '', function(data) {
		var batchId = data.map.result;
		$('#BATCH_TASK_ID').val(batchId);
	}, function(error_code, error_info) {
		alert("获取批次号失败，请重试！")
	}, {
		async : false
	});	
	
	//2- 根据属性编号获取模板编号
	var name = domid.toString().replace("_Comp","");	
	var laststr = name.split("_");
	var attrCode = laststr[2];
	var configStr = '';
	if(attrCode == '999033717'){
		configStr = 'import/bat/group/BATADDYDZFMEM.xml';
	}else if(attrCode == '999033734'){
		configStr = 'import/bat/group/BATCONFIRMYDZFMEM.xml';
	}else if(attrCode == '999033735'){
		configStr = 'import/bat/group/BATOPENYDZFMEM.xml';
	}	
	this.config=configStr;
	return true;
}

/**
 * @description 回写批量名称到输入框中
 * @author xunyl
 * @date 2015-10-28
 */
function setFileName(oper, domid){
	//1- 获取组件name
	var name = domid.toString().replace("_Comp","");
	
	//2- 将组件名称替换为输入框名称
	var laststr = name.split("_");
	var attrCode = laststr[2];
	compName = '#input_'+attrCode;
	
	//3- 获取缓存中的文件名称
	var fileName = '';
	var batchTaskId =$('#BATCH_TASK_ID').val();
	$.ajax.submit('', 'getFileName', '&BATCH_TASK_ID='+batchTaskId, '', function(data) {
		fileName = data.map.result;
	}, function(error_code, error_info) {
		alert("批量导入出错或者获取批量文件名称失败，请重试！")
	}, {
		async : false
	});	
	
	//3- 批量名称回写到文本框	
	$(compName).val(fileName);
	changeValue($(compName)[0]);
}
