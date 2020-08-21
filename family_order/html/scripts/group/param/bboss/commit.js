//xunyl 创建  此文件专门用于集团BBOSS产品参数页面提交时校验

/** ****************************************统一接口************************************ */
/*
 * 集团BBOSS产品参数页面提交校验统一接口
 */
function paramCommit() {
	// 基本校验，例如非空校验
	var result = $.validate.verifyAll('orderinfotabset');
	if (result == false) {
		return false;
	}

	// ICB参数必填校验，适用于场景，勾选了ICB参数，但没点开填值的情况
	var verifyICB = checkICBIsNotEmpty();
	if (verifyICB == false) {
		return false;
	}

	// 集团定制
	if (typeof (commparaGrpPackageElements) == "function") {
		mytab.switchTo("定制信息");
		// 判断是否必选包已经定制上
		if (!checkUserGrpPkgForce())
			return false;
		// 生成定制信息
		commparaGrpPackageElements();
	}

	// 获取所有对象
	var attrParams = $('[id =PRODUCT_PARAM_CODE]');

	// 返回值定义
	var verifyResult = true;
	attrParams.each(function() {
		// 获取输入框对象
		var paramCode = $.attr(this, "value");
		var inputObj = $("#input_" + paramCode);

		var flag = $("#GROUPATTR_FLAG_" + paramCode);

		// 某些属性需要进一步验证
		verifyResult = checkParamValue(paramCode);
		if (verifyResult == false) {
			return false;
		}

		// 获取调用的js方法名
		var methodName = inputObj.attr('commitMethodName');
		// 判断方法名是否存在，如果不存在则直接返回
		if (methodName != null && methodName != "undefined") {
			// 获取产品的既有值
			var oldValue = $('#OLDVALUE_' + paramCode).val();
			// 获取当前属性的属性值
			var attrValue = inputObj.val();
			verifyResult = window[methodName](inputObj, paramCode, oldValue,
					attrValue);
			if (verifyResult == false) {
				return false;
			}
		}
	});
	return verifyResult;
}

/*
 * ICB参数必填校验
 * 
 */
function checkICBIsNotEmpty() {

	// ICB参数值不为空
	var isNotEmpty = true;

	// 获取产品包元素以及元素参数值（PRODUCT_ID、PACKAGE_ID、ELEMENT_ID、ATTR_PARAM）
	var productElements = selectedElements.selectedEls;
    if (productElements == ''||productElements == null || productElements == "undefined")
	{
	   return isNotEmpty;
	}
	// 循环资费元素
	productElements.each(function(item, index, totalcount) {
		// 过滤勾选的值，剔除无关的数据
		if (item.get("MODIFY_TAG") == "0" || item.get("MODIFY_TAG") == "1" || item.get("MODIFY_TAG") == "2") {
			// 获取元素名称
			var proId = item.get("PRODUCT_ID");
			var pkgId = item.get("PACKAGE_ID");
			var eleId = item.get("ELEMENT_ID");
			var eleName = item.get("ELEMENT_NAME");
			
			// 获取ICB资费参数
			var icbParam = item.get("ATTR_PARAM");
			// ATTR_PARAM不为NULL，则为带ICB的资费；
			if (icbParam) {
				var icbParamValue = "";
				for (var i = 0; i < icbParam.length; i++) {
					// ICB资费如果没有填任何资费参数，则提示
					var attrCode = icbParam.get(i, "ATTR_CODE");
					var attrValue = icbParam.get(i, "ATTR_VALUE");
					icbParamValue += attrValue;
					
					//针对资费属性的特殊校验(关于新增IDC流量计费模式的需求：峰值计费_资费/95法则计费_资费 的 带宽种类_资费属性的特殊校验)
					if(proId=='80001618' && pkgId=='40000401' && (eleId=='819001' || eleId=='819002')  && attrCode=='100001'){
						var reg = /^[0-9]*[M][b]$/;
						if(!reg.test(attrValue)){
							MessageBox.alert('校验提示',eleName+'['+eleId+']下填写的带宽种类['+attrValue+']不符合格式要求，正确的填写示例应该是10Mb!');
							isNotEmpty = false;
						}
					}
					//针对资费属性的特殊校验(关于新增IDC流量计费模式的需求：峰值计费_资费/95法则计费_资费 的 带宽种类_资费属性的特殊校验)
					if(proId=='80001618' && pkgId=='40000401' && (eleId=='819001' || eleId=='819002')  && attrCode=='100004'){
						var reg = /^[0-9]*$/;
						if(!reg.test(attrValue) ||(attrValue<20)){
							MessageBox.alert('校验提示',eleName+'['+eleId+']下填写的上浮比例['+attrValue+']不符合格式要求，正确的填写应该是大于20的正整数!');
							isNotEmpty = false;
						}
					}
				}

				if (icbParamValue == "") {
					alert(eleName + "的资费参数不能全为空！");
					isNotEmpty = false;
				}
			}
		}
	});

	return isNotEmpty;
}

/* 分省出卡物联通短信产品属性校验 */
function checkthingsOfwebParamValue() {

	var verifyResult = true;
	// 获取所有的子产品订购关系ID
	var prOffId = $("input[name=input_300074016]")

	for (var i = 0; i < prOffId.length; i++) {
		var e = prOffId[i];
		var value = $(e).val();
		if ('1' == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'getPbssBizProdInstId', '', function(d) {
						var subs_id = d.map.result;
						$(e).val(subs_id);
					}, null, {
						async : false
					});

		}
	}
	;
	// 获取所有的 子产品名称 如果为物联通集团主体产品（集团） 则用户id和订购关系必须一致
	var prName = $("select[name=input_300074014]")

	for (var i = 0; i < prName.length; i++) {
		var e = prName[i];
		var value = $(e).val();

		if ("物联通集团主体产品（集团）" == value) {
			var proId = $(prOffId[i]).val();
			$("#input_300074023").val(proId);
			$($("input[name=input_300074023]")[0]).val(proId)
			return verifyResult;
		}
	}
	;

	// 如果用户id为空，则重新生成序列
	var userID = $("input[name=input_300074023]");
	if (userID.length == 0) {
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'geneSubsId', '', function(d) {
					var subs_id = d.map.result;
					$($("input[name=input_300074023]")[0]).val(subs_id)
				}, null, {
					async : false
				});
	}
	return verifyResult;

}

function checkParamValue(paramCode) {

	var value = $("#input_" + paramCode).val();

	if (value == null || value == "") {
		return true;
	}

	// 专线业务、跨省互联网专线、跨省集团wlan、呼叫中心直联、跨国专线开通验收报告验证
	if ("1112054333" == paramCode || "1112084333" == paramCode
			|| "301014332" == paramCode || "1113025021" == paramCode
			|| "1112064333" == paramCode || "1112074333" == paramCode) {
		var fileAllName = $("#SIMPLEUPLOAD_SPAN_input_" + paramCode).text();
		var existFileAllName = $("#input_" + paramCode).val();
		var fileArry = fileAllName.split(".");
		var fileName = fileArry[fileArry.length - 1];
		var existFileArry = existFileAllName.split(".");
		var existFileName = existFileArry[existFileArry.length - 1];
		if (!(fileName.toUpperCase() == "JPG".toUpperCase()
				|| fileName.toUpperCase() == "BMP".toUpperCase()
				|| fileName.toUpperCase() == "JPEG".toUpperCase()
				|| fileName.toUpperCase() == "PNG".toUpperCase()
				|| fileName.toUpperCase() == "GIF".toUpperCase() || fileName
				.toUpperCase() == "PDF".toUpperCase())
				&& !(existFileName.toUpperCase() == "JPG".toUpperCase()
						|| existFileName.toUpperCase() == "BMP".toUpperCase()
						|| existFileName.toUpperCase() == "JPEG".toUpperCase()
						|| existFileName.toUpperCase() == "PNG".toUpperCase()
						|| existFileName.toUpperCase() == "GIF".toUpperCase() || existFileName
						.toUpperCase() == "PDF".toUpperCase())) {
			alert("开通验收附件上传文件类型错误（仅支持类型：JPG、BMP、JPEG、PNG、GIF、PDF）");

			return false;

		}
	}
	return true;
}

/* 分省出卡物联通GPRS产品物联通短信产品属性校验 */
function checkthingsOfwebGPRSParamValue() {

	var verifyResult = true;
	// 获取所有的子产品订购关系ID
	var prOffId = $("input[name=input_300084016]")

	for (var i = 0; i < prOffId.length; i++) {
		var e = prOffId[i];
		var value = $(e).val();
		if ('1' == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'getPbssBizProdInstId', '', function(d) {
						var subs_id = d.map.result;
						$(e).val(subs_id);
					}, null, {
						async : false
					});

		}
	}
	;

	// 如果用户id为空，则重新生成序列
	var userID = $("input[name=input_300084023]");
	for (var i = 0; i < userID.length; i++) {
		var userNumber = userID[i];
		var value = $(userNumber).val();
		if ("1" == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'geneSubsId', '', function(d) {
						var subs_id = d.map.result;
						$($("input[name=input_300084023]")[0]).val(subs_id)
					}, null, {
						async : false
					});
		}
	}

	return verifyResult;

}

/* 分省出卡机器卡短信产品属性校验 */
function checkmachineCardOfwebParamValue() {

	var verifyResult = true;
	// 获取所有的子产品订购关系ID
	var prOffId = $("input[name=input_300054016]")

	for (var i = 0; i < prOffId.length; i++) {
		var e = prOffId[i];
		var value = $(e).val();
		if ('1' == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'getPbssBizProdInstId', '', function(d) {
						var subs_id = d.map.result;
						$(e).val(subs_id);
					}, null, {
						async : false
					});

		}
	}
	;
	// 获取所有的 子产品名称 如果为机器卡集团主体产品（集团） 则用户id和订购关系必须一致
	var prName = $("select[name=input_300054014]")

	for (var i = 0; i < prName.length; i++) {
		var e = prName[i];
		var value = $(e).val();

		if ("机器卡集团主体产品（集团）" == value) {
			var proId = $(prOffId[i]).val();
			$("#input_300054023").val(proId);
			$($("input[name=input_300054023]")[0]).val(proId)
			return verifyResult;
		}
	}
	;

	// 如果用户id为空，则重新生成序列
	var userID = $("input[name=input_300054023]");
	if (userID.length == 0) {
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'geneSubsId', '', function(d) {
					var subs_id = d.map.result;
					$($("input[name=input_300054023]")[0]).val(subs_id)
				}, null, {
					async : false
				});
	}
	return verifyResult;

}

/* 分省出卡机器卡GPRS产品属性校验 */
function checkmachineCardGPRSParamValue() {

	var verifyResult = true;
	// 获取所有的子产品订购关系ID
	var prOffId = $("input[name=input_300064016]")

	for (var i = 0; i < prOffId.length; i++) {
		var e = prOffId[i];
		var value = $(e).val();
		if ('1' == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'getPbssBizProdInstId', '', function(d) {
						var subs_id = d.map.result;
						$(e).val(subs_id);
					}, null, {
						async : false
					});

		}
	}
	;

	// 如果用户id为空，则重新生成序列
	var userID = $("input[name=input_300064023]");
	for (var i = 0; i < userID.length; i++) {
		var userNumber = userID[i];
		var value = $(userNumber).val();
		if ("1" == value) {
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'geneSubsId', '', function(d) {
						var subs_id = d.map.result;
						$($("input[name=input_300064023]")[0]).val(subs_id)
					}, null, {
						async : false
					});
		}
	}
	return verifyResult;

}

/*
 * 集团BBOSS某些产品的特殊校验
 */
function paramSpelValidate() {

	var result = true;
	var operType = $("#productOperType").val();
	var productId = $("#PRODUCT_NUMBER").val();
	// 将本地商品编码转为全网商品编码
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
			'qrySpecNumber', '&PRODUCT_ID=' + productId, function(d) {
				var param = d.map.result;
				productId = param;
			}, function(e, i) {
				$.showErrorMessage("操作失败");
				result = false;
			}, {
				async : false
			});
	// 商品资费变更
	if (operType == '5') {
		// 未变更任何资费，不准提交
		var productElements = selectedElements.getSubmitData();
		if (productElements == null || productElements == ''
				|| productElements.length == 0) {
			$.showErrorMessage("未变更任何资费!");
			return false;
		}
		var merDisCount = 0;
		var locDisCount = 0;
		for (var i = 0; i < productElements.length; i++) {
			var elementData = productElements.get(i);
			var elementTypeCode = elementData.get("ELEMENT_TYPE_CODE");
			var elementId = elementData.get("ELEMENT_ID");
			if (elementTypeCode == 'D') {
				// 查询是否是商品资费变更

				Wade.httphandler.submit('',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'qryProductToMerch', '&ELEMENT_ID=' + elementId,
						function(d) {
							var param = d.map.result;
							if (param == "true") {
								merDisCount++;
							} else {
								locDisCount++;
							}

						}, function(e, i) {
							$.showErrorMessage("操作失败");
							result = false;
						}, {
							async : false
						});
			}
		}
		if (merDisCount == 0) {
			$.showErrorMessage("商品资费变更未变更商品资费!");
		}
		if (locDisCount > 0) {
			$.showErrorMessage("商品资费变更不能变更商品本地资费!");
			return false;
		}

	}
	// 本地资费变更
	if (operType == '55') {
		var productElements = selectedElements.getSubmitData();
		if (productElements == null || productElements == ''
				|| productElements.length == 0) {
			productElements = new Wade.DatasetList();
		}
		var merDisCount = 0;
		for (var i = 0; i < productElements.length; i++) {
			var elementData = productElements.get(i);
			var elementTypeCode = elementData.get("ELEMENT_TYPE_CODE");
			var elementId = elementData.get("ELEMENT_ID");
			if (elementTypeCode == 'D') {
				// 查询是否是商品资费变更
				Wade.httphandler.submit('',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'qryProductToMerch', '&ELEMENT_ID=' + elementId,
						function(d) {
							var param = d.map.result;
							if (param == "true") {
								merDisCount++;
							}
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							result = false;
						}, {
							async : false
						});
			}
		}
		if (merDisCount > 0) {
			$.showErrorMessage("商品本地资费变更不能变更商品资费!");
			return false;
		}

		// 400 语音特殊校验
		if ('411501' == productId) {
			var user_id = $("#GRP_USER_ID").val();// 返回用户的属性

			// 校验目的地
			Wade.httphandler.submit('',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'qryOldValue', '&USER_ID=' + user_id
							+ '&ATTR_CODE=4115017032', function(d) {
						var param = d.map.result;
						// 目的地号码个数
						var attr_value = param.get(0, 'ATTR_VALUE');
						var attr_Code = param.get(0, 'ATTR_CODE');
						// var result = true;
						// 目的地号码个数校验
						result = is400_DestNum(attr_Code, attr_value);

					}, function(e, i) {
						$.showErrorMessage("操作失败");
						result = false;
					}, {
						async : false
					});

			if (result) // 前面校验通过才校验
			{

				// 校验400号码资费
				Wade.httphandler.submit('',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'qryOldValue', '&USER_ID=' + user_id
								+ '&ATTR_CODE=4115017001', function(d) {
							var param = d.map.result;

							// 400号码
							var attr_value = param.get(0, 'ATTR_VALUE');
							var attr_Code = param.get(0, 'ATTR_CODE');
							result = is400_BaseDiscnt();
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							result = false;
						}, {
							async : false
						});
			}
		}

	}
	// 物联网属性校验 以及特殊属性设值

	// 分省出卡物联通短信产品
	if ("30007" == productId) {
		result = checkthingsOfwebParamValue();
	} else if ("30008" == productId) {
		// 分省出卡物联通GPRS产品物联通短信产品属性校验
		result = checkthingsOfwebGPRSParamValue()
	}

	// 物联网专网专号机器卡商品
	if ("30005" == productId) {
		// 分省出卡机器卡短信产品
		result = checkmachineCardOfwebParamValue();

	} else if ("30006" == productId) {
		// 分省出卡机器卡GPRS产品
		reslut = checkmachineCardGPRSParamValue();
	}

	return result;
}

/****************************************行业网关云MAS业务******************************************* */
function IAGWcloudMas_commmit(e, attrCode, oldValue, newValue) {
	if("1101630004" == attrCode) {
		// 校验是否重复
		if (isNeedValDup(attrCode, newValue)) {
			return isDuplicate(e, attrCode, newValue);
		}
	}
}

/** *************************************网信业务******************************************* */

/*
 * 全网网信商品--属性变更校验
 */
function netInfo_commit(e, attrCode, oldValue, newValue) {
	// 集团客户签名
	if ("229017401" == attrCode) {
		// 校验是否重复
		if (isNeedValDup(attrCode, newValue)) {
			return isDuplicate(e, attrCode, newValue);
		}
	}

	// 集团客户长服务代码
	if ("229012009" == attrCode) {
		// 获取集团客户使用号码类型
		var longServType = $('#input_229017405').val();
		if (longServType == "01") {// 统一号码
			// 省内排重
			if (isNeedValDup(attrCode, newValue)) {
				return isDuplicate(e, attrCode, newValue);
			}
		}
	}

	// 半托管接口服务器IP地址
	if ("229017417" == attrCode) {
		var depsType = $('#input_229017402').val();
		if (depsType == "02" && (newValue == null || newValue == "")) {// 半托管
			$.showErrorMessage("集团客户托管方式为半托管" + $(e).attr('desc') + "的值不能为空!");
			$(e).select();
			return false;
		}
	}

	return true;
}

/*
 * 400商品--属性变更校验
 */
function business400_commit(e, attrCode, oldValue, newValue) {

	// 400号码
	if ("4115017001" == attrCode) {
		// 根据选择的省公司获取该省公司对应的城市
		if (is400_BaseDiscnt()) {
			if (isNeedValDup(attrCode, newValue)) {
				return isDuplicate(e, attrCode, newValue);
			}
		} else {
			return false;
		}
	}
	// 预占流水号
	if ("4115017007" == attrCode) {

		// 第5位为4001号码的校验位（4001号码的10位数字之和的个位数），第6位为前5位数字的校验位（前5位数字之和的个位数）
		var num400 = $('#input_4115017001').val();
		bookingNumber = newValue;
		var bookingNumber2 = bookingNumber.split("");
		var num4002 = num400.split("");
		var sum400 = 0;
		for (var w = 0; w < num4002.length; w++) {
			sum400 = sum400 + parseInt(num4002[w]);
		}
		var sum1_5 = 0;
		var pos5;
		var pos6;
		for (var w = 0; w < bookingNumber2.length - 1; w++) {
			sum1_5 = sum1_5 + parseInt(bookingNumber2[w]);
		}
		pos5 = bookingNumber2[4];
		pos6 = bookingNumber2[5];
		if (pos5 != String(sum400 % 10)) {
			$.showErrorMessage($(e).attr('desc')
					+ "校验不通过:第5位为400号码的校验位（400号码的10位数字之和的个位数）");
			$(e).select();
			return false;
		}

		if (pos6 != String(sum1_5 % 10)) {
			$.showErrorMessage($(e).attr('desc')
					+ "校验不通过:第6位为前5位数字的校验位（前5位数字之和的个位数）");
			$(e).select();
			return false;
		}

	}
	// 号码个数
	if ("4115017032" == attrCode) {
		return is400_DestNum(attrCode, newValue);
	}
	//呼叫国内400业务次数限制
	if ("4115047015" == attrCode) {
		//按日限制呼叫要小于按月限制呼叫
		var limitMonth = $('#input_4115047014').val();
		var limitDay=newValue;
		if(limitDay>limitMonth){
			$.showErrorMessage($(e).attr('desc')
					+ "校验不通过:按月限制呼叫国内400业务次数小于按日限制呼叫国内400业务次数");
			$(e).select();
			return false;
		}
	}
	
	return true;
}

/*
 * 集团客户一点支付 --附件校验
 */
function customerAllPayAttach_commit(e, attrCode, oldValue, newValue) {
	var result = true;
	var flowpoint = $("#FLOWPOINT").val();
	var manageNode="";
	if(undefined != flowpoint){
		manageNode=flowpoint.substring(6);
	}
	if ("999033717" == attrCode && ""!=manageNode) {
		if ((manageNode == "1107" || manageNode == "1118") && oldValue != ""
				&& oldValue == newValue) {
			var paramTitle = $("#PARAM_NAME_" + attrCode).text();
			$.showErrorMessage("请重新上传" + paramTitle);
			result = false;
		}
	}

	if ("999033734" == attrCode && ""!=manageNode) {
		if ((manageNode == "1101" || manageNode == "1106"
				|| manageNode == "1107") && oldValue != ""
				&& oldValue == newValue) {
			var paramTitle = $("#PARAM_NAME_" + attrCode).text();
			$.showErrorMessage("请重新上传" + paramTitle);
			result = false;
		}
	}
	if ("999033735" == attrCode &&""!=manageNode) {
		if ((manageNode == "1103" || manageNode == "1109"
				|| manageNode == "1111" || manageNode == "1113"
				|| manageNode == "1114" || manageNode == "1115"
				|| manageNode == "1120") && oldValue != ""
				&& oldValue == newValue) {
			var paramTitle = $("#PARAM_NAME_" + attrCode).text();
			$.showErrorMessage("请重新上传" + paramTitle);
			result = false;
		}
	}
	return result;
}

/** ***************************************基础校验方法******************************************** */
// 省内排重
function isDuplicate(e, attrCode, newValue) {
	var result = true;
	Wade.httphandler
			.submit('', 'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'isDuplicate', '&ATTR_CODE=' + attrCode + '&ATTR_VALUE='
							+ newValue, function(d) {
						if (d.map.result == "false") {
							$.showErrorMessage($(e).attr('desc')
									+ "的值在系统中已经重复，请重新设置!");
							$(e).select();
							result = false;

						}
					}, function(e, i) {
						$.showErrorMessage("操作失败");
						$(e).select();
						result = false;
					}, {
						async : false
					});
	return result;
}

// 400保底资费
function is400_BaseDiscnt() {
	// 被选中的产品元素
	var productElements = selectedElements.getSubmitData();
	var productElements = "";
	var result = true;

	for (var j = 0; j < productElements.length; j++) {
		var elementData = productElements.get(j);
		var elementId = elementData.get("ELEMENT_ID")
		var attrParam = elementData.get("ATTR_PARAM");
		if ("62300175" == elementId) {
			attrValue = attrParam.get(0).get("ATTR_VALUE")

			if (attrValue == "") {
				$.showErrorMessage("保底资费金额不能为零");
				result = false;
			} else if (!(attrValue >= 0 && attrValue <= 200000)) {
				$.showErrorMessage("保底资费金额须为0-200000之间的整数");
				result = false;
			} else if (!/(^[1-9]\d*$)/.test(attrValue)) {
				$.showErrorMessage("保底资费金额请输入整数");
				result = false;
			}
		}

	}

	return result;
}

function is400_DestNum(attrCode, newValue) {
	var result = true;
	// 目的地号码个数
	// 被选中的产品元素
	var productElements = selectedElements.getSubmitData();
	var operType = $("#productOperType").val();
	if (operType == '9') {
		selectedElements1 = selectProudctElements();
	} else {
		selectedElements1 = selectedElements.selectedEls;
	}
	var elementData = new Wade.DataMap();
	var elementTypeCode;
	var error = '';
	var errornum = 1;
	for (var i = 0; i < selectedElements1.length; i++) {

		elementData = selectedElements1.get(i);
		elementTypeCode = elementData.get("ELEMENT_TYPE_CODE");
		if ("D" == elementTypeCode) {
			// 400目的号码个数资费
			if ('40000005' == elementData.get("ELEMENT_ID")) {

				var discnt = elementData.get("ATTR_PARAM");
				if (discnt.get(0, "ATTR_VALUE") == '') {
					error = error + '\n' + errornum + ':请填写资费包:下挂号码扩展功能费的资费参数!';
					errornum++;
				} else if (parseInt(discnt.get(0, "ATTR_VALUE")) != parseInt(newValue)) {
					if (operType == '1') {
						error = error + '\n' + errornum + ':您填写资费包参数['
								+ discnt.get(0, "ATTR_VALUE") + "]跟实际号码个数["
								+ newValue + "]不一致!";
						errornum++;
					} else if (operType == '9') {

						alert("产品参数【目的地号码数量】修改后，资费包参数【语音路由下挂号码个数】也会做相应修改");

					} else if (operType == '55') {
						error = '请在【修改商品属性】操作中修改产品参数【目的地号码数量】，资费包参数【语音路由下挂号码个数】也会做相应修改';
					}

				}
			}
		}
	}
	if (error != '') {
		$.showErrorMessage('操作错误:\n' + error);
		// $(e).select();
		result = false;
	}
	return result;

}

// 是否需要重复校验
function isNeedValDup(attrCode, newValue) {
	var flag = false; // 默认不需要校验
	var oldValue = '';

	// 因为升级后集团受理与变更的参数页面合并了，而OLD_PRODUCT_PARAMS元素仅供变更使用，因此受理时赋空值
	var user_id = $("#GRP_USER_ID").val();
	if (null == $("#GRP_USER_ID").val() || "" == $("#GRP_USER_ID").val()) {
		flag = true;
	}

	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify', 'qryOldValue',
			'&ATTR_CODE=' + attrCode + '&USER_ID=' + user_id, function(d) {
				var attrs = d.map.result;
				if (attrs.length > 0) {
					oldValue = attrs.get(0, 'ATTR_VALUE');
				}
				if (newValue != '' && newValue != oldValue) {
					flag = true; // 需要校验
				}

			}, function(e, i) {
				$.showErrorMessage("操作失败");
				$(e).select();
				result = false;
			}, {
				async : false
			});
	return flag;
}

function selectProudctElements() {
	var attrs;
	var productId = $("#PRODUCT_NUMBER").val();
	var user_id = $("#GRP_USER_ID").val();
	var grp_user_eparchycode = $("#GRP_USER_EPARCHYCODE").val();
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
			'qrySelectParms',
			'&PRODUCT_ID=' + productId + '&USER_ID=' + user_id
					+ '&GRP_USER_EPARCHYCODE=' + grp_user_eparchycode,
			function(d) {
				var selects = d.map.result;
				if (selects.length > 0)
					attrs = selects.get(0, 'SELECTED_ELEMENTS');

			}, function(e, i) {
				$.showErrorMessage("操作失败");
				$(e).select();
				result = false;
			}, {
				async : false
			});
	return attrs;
}