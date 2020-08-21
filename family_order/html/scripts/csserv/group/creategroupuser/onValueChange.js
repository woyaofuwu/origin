//xunyl 创建  此文件专门用于集团BBOSS产品的属性变更时校验

/** ****************************************统一接口************************************ */
/*
 * 集团BBOSS产品的属性变更统一接口
 */
function onValueChangeUnit(e) {
	// 获取调用的js方法名
	var methodName = $(e).attr("changeMethodName");
	// 判断方法名是否存在，如果不存在则直接返回
	if (methodName == null || methodName == "undefined") {
		return true;
	}
	// 获取当前变更的属性编号
	var paramCode = $(e).attr("paraCode");
	// 获取产品的既有值
	var oldValue = $('#OLDVALUE_' + paramCode).val();
	var poProductPlus = $(e).attr("pOProductPlus");
	// 获取当前属性的属性值
	var attrValue = e.value;
	// 参数值为空的时候，不进行校验
	if ((attrValue == null || attrValue == "") && isNotInTheseCodes(paramCode)) {
		return true;
	}
	// 调用相应的js方法，将属性编号，参数既有值和变更后的值传入js方法中
	return window[methodName](e, paramCode, oldValue, attrValue);
}

/*
 * 集团BBOSS产品的管理节点属性变更统一接口
 */
function onBbossManageValueChangeUnit(e) {
	// 获取调用的js方法名
	var methodName = $(e).attr("initMethodName");
	// 判断方法名是否存在，如果不存在则直接返回
	if (methodName == null || methodName == "undefined") {
		return true;
	}
	// 获取当前变更的属性编号
	var paramCode = $(e).attr("paraCode");

	// 获取当前属性的属性值
	var attrValue = e.value;

	// 参数值为空的时候，不进行校验
	if ((attrValue == null || attrValue == "") && isNotInTheseCodes(paramCode)) {
		return true;
	}

	// 调用相应的js方法，将属性编号，参数既有值和变更后的值传入js方法中
	return window[methodName](e, paramCode, attrValue);
}

/* *****************************物联网专网专号物联通商品********************************** */
function thingsOfweb_onValueChange(e, attrCode, oldValue, newValue) {

	// 短信级别下拉联动
	if ("300074002" == attrCode) {
		selectChange(e, attrCode, '300074014', newValue);
	}
	// // 子产品名称 联动下拉
	if ("300074014" == attrCode) {
		// 联动
		selectChange(e, attrCode, '300074015', newValue);
		selectChange(e, attrCode, '300074018', newValue);

	}
	// 子产品类别和产品包类别 联动下拉
	if ("300074014" == attrCode) {
		selectChange(e, attrCode, '300074018', newValue);
	}

	if ("300074015" == attrCode) {
		if ("I00010400001" == newValue) {
			$('#input_300074019').val('1');
		} else {
			$('#input_300074019').val('2');
		}
	}

	if ("300074038" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
			$.showErrorMessage("中文签名8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300074039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300074013" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300074052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("对应省开卡数量，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		return true;
	}

}

function thingsOfwebGPRS_onValueChange(e, attrCode, oldValue, newValue) {

	// 是否专用APN 联动下拉
	if ("300084024" == attrCode) {
		selectChange(e, attrCode, '300084014', newValue);
	}

	// 子产品名称 联动下拉
	if ("300084014" == attrCode) {
		selectChange(e, attrCode, '300084015', newValue);

	}
	// 子产品类别和产品包类别 联动下拉
	if ("300084015" == attrCode) {
		selectChange(e, attrCode, '300084018', newValue);
	}

	if ("300081008" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
			$.showErrorMessage("中文签名8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300084039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300084007" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	if ("300084052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("对应省开卡数量，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* *****************************物联网专网专号机器卡商品********************************** */
function machineCardOfweb_onValueChange(e, attrCode, oldValue, newValue) {

	// 短信级别下拉联动
	if ("300054002" == attrCode) {
		selectChange(e, attrCode, '300054014', newValue);
	}
	// // 子产品名称 联动下拉
	if ("300054014" == attrCode) {
		// 联动
		selectChange(e, attrCode, '300054015', newValue);
		selectChange(e, attrCode, '300054018', newValue);

	}
	// 子产品类别和产品包类别 联动下拉
	if ("300054014" == attrCode) {
		selectChange(e, attrCode, '300054018', newValue);
	}

	if ("300054015" == attrCode) {
		if ("I00010300001" == newValue) {
			$('#input_300054019').val('1');
		} else {
			$('#input_300054019').val('2');
		}
	}

	if ("300054038" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
			$.showErrorMessage("中文签名8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300054039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300054013" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300054052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("对应省开卡数量，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		return true;
	}

}

function machineCardGPRS_onValueChange(e, attrCode, oldValue, newValue) {

	// 是否专用APN 联动下拉
	if ("300064024" == attrCode) {
		selectChange(e, attrCode, '300064014', newValue);
	}

	// 子产品名称 联动下拉
	if ("300064014" == attrCode) {
		selectChange(e, attrCode, '300064015', newValue);

		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'getPbssBizProdInstId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_300064016').val(subs_id);

				});

		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'geneSubsId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_300064023').val(subs_id);

				});

	}
	// 子产品类别和产品包类别 联动下拉
	if ("300064015" == attrCode) {
		selectChange(e, attrCode, '300064018', newValue);
	}
	// 获取产品订购关系 主办省保证唯一，并且保证与本省的一点出卡物联网专网专号业务订购关系ID不重复
	if ("300064016" == attrCode) {
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'getPbssBizProdInstId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_300064016').val(subs_id);

				});
	}
	// 用户标识 系统自动生成流水号
	if ("300064023" == attrCode) {

		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
				'geneSubsId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_300064023').val(subs_id);

				});
	}

	if ("300061008" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
			$.showErrorMessage("中文签名8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300064039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300064007" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	if ("300064052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("对应省开卡数量，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* *****************************管理节点属性校验********************************** */
function bbossmanage_onValueChange(e, attrCode, newValue) {
	if ("10002" == attrCode.substring(4)) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* **********************************流量统付业务属性校验************************************************ */
function flowOnePay_onValuechange(e, attrCode, oldValue, newValue) {
	
	//实体流量统付卡有效期验证
	if("90011014029" == attrCode){
		var oDate = new Date(); //实例一个时间对象；
		var oDate1=oDate.getFullYear()+(oDate.getMonth()+4)+oDate.getDate();
		var oDate2=oDate.getFullYear()+2+(oDate.getMonth()+1)+oDate.getDate();
		 var d = new Date(newValue.replace(/\-/g, ''));  
		  if(oDate1>d&&d>oDate2)  
		 {  
			  $.showErrorMessage("日期不迟于提单日之后2年，不早于提单日之后3个月（3个月-2年之间）"); 
		  return false;
		 }
		 return true;
	}

	if ("999044003" == attrCode || "999044005" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联系人电话
	if ("999054005" == attrCode || "999054003" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 数字
	if ("999054011" == attrCode || "999054012" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}
	// 长度
	if ("999054016" == attrCode) {
		if (newValue.length > 8) {
			$.showErrorMessage("长度最多为中文8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}

	// 月流量规模(GB)
	if ("999044013" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}

	// 客户产品简称
	if ("999044014" == attrCode) {
		if (newValue.length > 8 || newValue.length < 2) {
			$.showErrorMessage($(e).attr('desc') + "格式正确,应为2~8个汉字或者字母");
			$(e).select();
			return false;
		}
		return true;
	}
	// 是否使用标准模板下发提醒短信
	if ("999044061" == attrCode || "999054061" == attrCode
			|| "999084061" == attrCode || "999094061" == attrCode) {
		if ("0" == newValue) {
			$('#PARAM_NAME_' + attrCode.substring(0, 7) + '62').attr("class",
					"e_required");
			$('#input_' + attrCode.substring(0, 7) + '62').attr("nullable", "no");
			$('#input_' + attrCode.substring(0, 7) + '62').attr("disabled", false);

		} else {
			$('#PARAM_NAME_' + attrCode.substring(0, 7) + '62').removeClass(
					"e_required");
			$('#input_' + attrCode.substring(0, 7) + '62').attr("nullable", "yes");
			$('#input_' + attrCode.substring(0, 7) + '62').attr("disabled", true);

		}

	}
	if ("999044062" == attrCode || "999054062" == attrCode
			|| "999084062" == attrCode || "999094062" == attrCode) {
		if (newValue.length > 200) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,应为200个中文以内");
			$(e).select();
			return false;
		}
	}

	return true;
}

/* **************************************网信业务******************************************* */

/*
 * 全网网信商品--属性变更校验
 */
function netInfo_onValueChange(e, attrCode, oldValue, newValue) {
	if ("229017401" == attrCode) {
		// 校验签名长度是否符合规范，客户签名2-8个汉字，或2-8个英文字母
		if (newValue.length < 2 || newValue.length > 8) {
			$.showErrorMessage("客户签名2-8个汉字，或2-8个英文字母");
			$(e).select();
			return false;
		}
		return true;
	}

	// 集团客户联系人邮箱
	if ("229017412" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage("管理员邮箱格式不正确");
			$(e).select();
			return false;
		}
		return true;
	}

	// 套餐折扣
	if ("229017404" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage("套餐折扣的不正,折扣应为0-100的整数");
			$(e).select();
			return false;
		}
		return true;
	}

	// 集团客户长服务代码随客户使用号码类型的变更而变更
	if ("229017405" == attrCode) {
		if (newValue == "01") {
			$('#input_229012009').val('106501623');
		} else if (newValue == "02") {
			$('#input_229012009').val('1065016');
		}
		changeValue($('#input_229012009')[0]);
		$('#input_229012009').focus();
		return true;
	}

	// 手机号码
	if ("229017413" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage("手机号码的格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	// 集团客户长服务代码
	if ("229012009" == attrCode) {
		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("集团客户服务代码不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		// 获取集团客户使用号码类型
		var longServType = $('#input_229017405').val();
		if (longServType == "01") {// 统一号码
			// 检查前缀是否正确
			if (newValue.indexOf("106501623") != 0) {
				$.showErrorMessage("集团客户服务代码不正确，需要以106501623开头");
				$(e).select();
				return false;
			}
			// 检查序列号位数是否正确
			if (newValue.toString().substring(9).length < 4
					|| newValue.toString().substring(9).length > 8) {
				$.showErrorMessage("集团客户服务代码不正确，106501623后面请接4-8位序列号");
				$(e).select();
				return false;
			}
		} else {// 扩展号码
			if (newValue.indexOf("1065016") != 0) {
				$.showErrorMessage("集团客户服务代码不正确，需要以1065016开头");
				$(e).select();
				return false;
			}
			if (newValue.toString().substring(6).length > 2) {
				if (newValue.toString().substring(7, 9) == '23') {
					$
							.showErrorMessage("集团客户服务代码不正确，号码类型为扩展号码时，1065016+23的形式不能用");
					$(e).select();
					return false;
				}
			}
			if (newValue.toString().substring(6).length < 5
					|| newValue.toString().substring(6).length > 11) {
				$.showErrorMessage("集团客户服务代码不正确，1065016后面请接4-10位序列号");
				$(e).select();
				return false;
			}
		}
		return true;
	}

	// 半托管接口服务器IP地址
	if ("229017417" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
	}

	// 英文短信正文签名
	if ("229017419" == attrCode) {
		var filter = /^[a-zA-Z]{2,8}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度为2-8位");
			$(e).select();
			return false;
		}
	}

	return true;
}

/* ****************************************跨省专线*********************************************** */
function specialLine_onValueChange(e, attrCode, oldValue, newValue) {
	// A端对应省公司
	if ("1112053311" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112053305').attr('value', '');
			changeValue($('#input_1112053305')[0]);
			$('#input_1112053305').attr('disabled', true);
			return true;
		} else {
			$('#input_1112053305').attr('disabled', false);
		}

		// 根据选择的省公司获取该省公司对应的城市
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112053305',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112053305')[0].innerHTML = "";
							$('#input_1112053305').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}

	// Z端对应省公司
	if ("1112053320" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112053306').attr('value', '');
			changeValue($('#input_1112053306')[0]);
			$('#input_1112053306').attr('disabled', true);
			return true;
		} else {
			$('#input_1112053306').attr('disabled', false);
		}

		// 根据选择的省公司获取该省公司对应的城市
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112053306',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112053306')[0].innerHTML = "";
							$('#input_1112053306').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}
}

/** *************************************跨省互联网******************************************* */
function specialInternet_onValueChange(e, attrCode, oldValue, newValue) {
	// 带宽(请填写单位:M或G)
	if ("1112083307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).select();
			return false;
		}

	}

	// 数字
	if ("1112083340" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}

	// 处理意见
	if ("1112083483" == attrCode) {
		if ("0" == newValue) {
			// 未处理原因
			$("#input_1112083475").attr("nullable", "yes");
		}

		if ("1" == newValue) {
			// 未处理原因
			$("#input_1112083475").attr("nullable", "no");
		}
	}

	// 联系人电话
	if ("1112083313" == attrCode || "1112083406" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 客户机房所在城市 1112083311 1112083305
	if ("1112083311" == attrCode) {
		selectChange(e, attrCode, '1112083305', newValue);
	}

	// 客户机房所在城市
	if ("1112083311" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112083305').attr('value', '');
			changeValue($('#input_1112083305')[0]);
			$('#input_1112083305').attr('disabled', true);
			return true;
		} else {
			$('#input_1112083305').attr('disabled', false);
		}

		// 根据选择的省公司获取该省公司对应的城市
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112083305',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112083305')[0].innerHTML = "";
							$('#input_1112083305').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}
}
/* **************************************跨省行业应用******************************************* */

function ovProvinceTrade_onValueChange(e, attrCode, oldValue, newValue) {
	// 数字判断
	if ("9116014507" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}
}

/* **************************************局数据制作******************************************* */
function bureauDataMake_onValueChange(e, attrCode, oldValue, newValue) {
	// 判断是否为数字
	if ("1113035005" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}
}

/* **************************************车务通业务******************************************* */

/*
 * 全网车务通商品--属性变更校验
 */
function soCarThings_onValueChange(e, attrCode, oldValue, newValue) {
	// 管理员手机号码
	if ("1109016601" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
}

/*
 * 本地车务通商品--属性变更校验
 */
function localCarThings_onValueChange(e, attrCode, oldValue, newValue) {
	// 管理员手机号码
	if ("1109037703" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	// 备注
	if ("1109031036" == attrCode && byteLength(newValue) > 100) {
		$.showErrorMessage($(e).attr('desc') + "长度超出范围，请控制在100字节内");
		$(e).select();
		return false;
	}

	// 管理员账户
	if ("1109037701" == attrCode && byteLength(newValue) > 32) {
		$.showErrorMessage($(e).attr('desc') + "长度超出范围，请控制在32字节内");
		$(e).select();
		return false;
	}

	// 管理员密码
	if ("1109037702" == attrCode && byteLength(newValue) > 64) {
		$.showErrorMessage($(e).attr('desc') + "长度超出范围，请控制在64字节内");
		$(e).select();
		return false;
	}

	// 终端型号、终端ID、终端车牌号
	if (("1109031003" == attrCode || "1109031004" == attrCode || "1109031005" == attrCode)
			&& byteLength(newValue) > 32) {
		$.showErrorMessage($(e).attr('desc') + "长度超出范围，请控制在32字节内");
		$(e).select();
		return false;
	}
}

/* **************************************全网POC商品******************************************* */

/*
 * 全网POC商品--属性变更校验
 */
function netPOC_onValueChange(e, attrCode, oldValue, newValue) {
	// 手机号码
	if ("1103027714" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
	// 管理员邮箱
	if ("1103027715" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* **************************************本地POC******************************************* */

/*
 * 本地POC商品--属性变更校验
 */
function localPOC_onValueChange(e, attrCode, oldValue, newValue) {

	// 手机号码
	if ("1103017714" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}

	// 管理员邮箱
	if ("1103017715" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确");
			$(e).select();
			return false;
		}
		return true;
	}

	// 备注
	if ("1103011036" == attrCode) {
		// 正则表达式验证邮箱格式
		if (newValue.length > 50) {
			$.showErrorMessage($(e).attr('desc') + "长度不能超过50");
			$(e).select();
			return false;
		}
		return true;
	}

}

/* **************************************农信通商品******************************************* */

/*
 * 农信通商品--属性变更校验
 */
function nongXingTong_onValueChange(e, attrCode, oldValue, newValue) {
	// 日期格式
	if ("1102221035" == attrCode || "331011035" == attrCode
			|| "331031035" == attrCode || "331041035" == attrCode
			|| "331051035" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}
	if ("1102221034" == attrCode || "331011034" == attrCode
			|| "331031034" == attrCode || "331041034" == attrCode
			|| "331051034" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}
	if ("331030002" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}

	// EC企业码
	if ("1102221028" == attrCode || "331011028" == attrCode
			|| "331031028" == attrCode || "331041028" == attrCode
			|| "331051028" == attrCode) {
		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "为6位数字");
			$(e).select();
			return false;
		}
	}

	// 每日下发的最大条数
	if ("1102221032" == attrCode || "331011032" == attrCode
			|| "331031032" == attrCode || "331031032" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
	}

	// 每月下发的最大条数
	if ("1102221033" == attrCode || "331011033" == attrCode
			|| "331031033" == attrCode || "331041033" == attrCode
			|| "331051033" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
	}

	// 集团客户短信接收手机号
	if ("1102220003" == attrCode || "331030003" == attrCode
			|| "331010003" == attrCode || "331050003" == attrCode
			|| "331040003" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
		return true;
	}
	// EC客户长服务
	if ("331012009" == attrCode || "331032019" == attrCode
			|| "331042009" == attrCode || "331052009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("125829999") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以125829999开头");
			$(e).select();
			return false;
		}
		if (newValue.toString().length < 16) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("331011009" == attrCode || "331031009" == attrCode
			|| "331041009" == attrCode || "331051009" == attrCode
			|| "331031019" == attrCode) {

		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		if (newValue.indexOf("125829999") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以125829999开头");
			$(e).select();
			return false;
		}
		if (newValue.toString().length < 16) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("1102221009" == attrCode || "1102222009" == attrCode) {

		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "为6位数字");
			$(e).select();
			return false;
		}
	}
}

/* **************************************财信通******************************************* */

/*
 * 财信通商品--属性变更校验
 */
function financInfo_onValueChange(e, attrCode, oldValue, newValue) {
	// 时间格式：hhmmss
	if ("228151034" == attrCode || "228151035" == attrCode
			|| "228161034" == attrCode || "228011034" == attrCode
			|| "228031035" == attrCode || "228011035" == attrCode
			|| "228161035" == attrCode || "228181035" == attrCode
			|| "228141034" == attrCode || "228141035" == attrCode
			|| "228171034" == attrCode || "228171035" == attrCode
			|| "228181034" == attrCode || "228121034" == attrCode
			|| "228111035" == attrCode || "228111034" == attrCode
			|| "228121035" == attrCode || "228041034" == attrCode
			|| "228131035" == attrCode || "228131034" == attrCode
			|| "228101035" == attrCode || "228081034" == attrCode
			|| "228051035" == attrCode || "228051034" == attrCode
			|| "228081035" == attrCode || "228101034" == attrCode
			|| "228091035" == attrCode || "228091034" == attrCode
			|| "228041035" == attrCode || "228021035" == attrCode
			|| "228031034" == attrCode || "228021034" == attrCode
			|| "228191034" == attrCode || "228191035" == attrCode) {
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}
	// 每日、月下发的最大条数
	if ("228021032" == attrCode || "228021033" == attrCode
			|| "228031032" == attrCode || "228031033" == attrCode
			|| "228011033" == attrCode || "228011032" == attrCode
			|| "228151032" == attrCode || "228151033" == attrCode
			|| "228161032" == attrCode || "228161033" == attrCode
			|| "228171032" == attrCode || "228171033" == attrCode
			|| "228181032" == attrCode || "228181033" == attrCode
			|| "228041032" == attrCode || "228041033" == attrCode
			|| "228191032" == attrCode || "228191033" == attrCode
			|| "228051032" == attrCode || "228051033" == attrCode
			|| "228081032" == attrCode || "228081033" == attrCode
			|| "228091032" == attrCode || "228091033" == attrCode
			|| "228101032" == attrCode || "228101033" == attrCode
			|| "228111032" == attrCode || "228111033" == attrCode
			|| "228121032" == attrCode || "228121033" == attrCode
			|| "228131032" == attrCode || "228131033" == attrCode
			|| "228141032" == attrCode || "228141033" == attrCode) {
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}

	// EC企业码
	if ("228021028" == attrCode || "228031028" == attrCode
			|| "228011028" == attrCode || "228151028" == attrCode
			|| "228161028" == attrCode || "228171028" == attrCode
			|| "228181028" == attrCode || "228041028" == attrCode
			|| "228191028" == attrCode || "228051028" == attrCode
			|| "228081028" == attrCode || "228091028" == attrCode
			|| "228101028" == attrCode || "228111028" == attrCode
			|| "228121028" == attrCode || "228131028" == attrCode
			|| "228141028" == attrCode) {
		if (newValue.length > 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号
	if ("228021009" == attrCode || "228031009" == attrCode
			|| "228011009" == attrCode || "228151009" == attrCode
			|| "228161009" == attrCode || "228171009" == attrCode
			|| "228181009" == attrCode || "228041009" == attrCode
			|| "228191009" == attrCode || "228051009" == attrCode
			|| "228081009" == attrCode || "228091009" == attrCode
			|| "228101009" == attrCode || "228111009" == attrCode
			|| "228121009" == attrCode || "228131009" == attrCode
			|| "228141009" == attrCode) {
		if (newValue.indexOf("1065089") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以1065089开头");
			$(e).select();
			return false;
		}
		if (newValue.length < 14) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于14位");
			$(e).select();
			return false;
		}
	}

	// EC长服务代码
	if ("228012009" == attrCode || "228022009" == attrCode
			|| "228032009" == attrCode || "228042009" == attrCode
			|| "228052009" == attrCode || "228082009" == attrCode
			|| "228092009" == attrCode || "228102009" == attrCode
			|| "228112009" == attrCode || "228122009" == attrCode
			|| "228132009" == attrCode || "228142019" == attrCode
			|| "228152019" == attrCode || "228162019" == attrCode
			|| "228172019" == attrCode || "228182019" == attrCode
			|| "228192019" == attrCode) {
		if (newValue.indexOf("1065089") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以1065089开头");
			$(e).select();
			return false;
		}
		if (newValue.length < 14) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于14位");
			$(e).select();
			return false;
		}
	}

	// 彩信的短信上行点播码
	if ("228171025" == attrCode || "228141025" == attrCode
			|| "228181025" == attrCode || "228151025" == attrCode
			|| "228161025" == attrCode || "228191025" == attrCode) {

		if (newValue.indexOf("1065089") != 0) {
			$.showErrorMessage($(e).attr('desc')
					+ "格式不正确，需要以1065089开头。EC短信基本接入号＋01，即1065022XYABCDE01");
			$(e).select();
			return false;
		}

		if (newValue.length < 16) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).select();
			return false;
		}
	}
	// 手机号码
	if ("228150003" == attrCode || "228140003" == attrCode
			|| "228180003" == attrCode || "228170003" == attrCode
			|| "228160003" == attrCode || "228190003" == attrCode
			|| "228090003" == attrCode || "228100003" == attrCode
			|| "228050003" == attrCode || "228080003" == attrCode
			|| "228130003" == attrCode || "228040003" == attrCode
			|| "228110003" == attrCode || "228120003" == attrCode
			|| "228020003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).select();
			return false;
		}
	}
}

/* **************************************本地MAS******************************************* */
/*
 * 本地MAS（商品）--属性变更校验
 */
function localMas_onValueChange(e, attrCode, oldValue, newValue) {

	// IP地址
	if ("1101050003" == attrCode || "1101050100" == attrCode
			|| "1101050104" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).select();
			return false;
		}
	}

	// 联系人邮件
	if ("1101050007" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请填写正确的邮箱地址");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联系人电话
	if ("1101050006" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1101059900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.showErrorMessage($(e).attr('desc') + "不能为空！");
			$(e).select();
			return false;
		}
	}
}

/* **************************************集团彩信直联******************************************* */

/*
 * 集团彩信直联(商品)--属性变更校验
 */
function groupMMS_onValueChange(e, attrCode, oldValue, newValue) {
	// EC/SI上行URL
	if ("1106014222" == attrCode || "1107014222" == attrCode) {
		if ($.format.lowercase(newValue).indexOf("http://") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以http://开头");
			$(e).select();
			return false;
		}
		return true;
	}

	// EC/SI主机IP地址
	if ("1106014208" == attrCode || "1107014208" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).select();
			return false;
		}
		return true;
	}
	// 集客部联系电话
	if ("1106014221" == attrCode || "1106014219" == attrCode
			|| "1106014216" == attrCode || "1106014232" == attrCode
			|| "1107014216" == attrCode || "1107014221" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("1106011009" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).select();
			return false;
		}
	}
	// 判断是否为数字
	if ("1106014402" == attrCode || "1106014209" == attrCode
			|| "1107011009" == attrCode || "1107014402" == attrCode
			|| "1107014209" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1104019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.showErrorMessage($(e).attr('desc') + "不能为空！");
			$(e).select();
			return false;
		}
	}

}

/* **************************************集团短信直联******************************************* */

/*
 * 集团短信直联(商品)--属性变更校验
 */
function groupSMS_onValueChange(e, attrCode, oldValue, newValue) {

	// EC/SI主机IP地址
	if ("1105014208" == attrCode || "1105014418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须是1-255之间");
			$(e).select();
			return false;
		}
		if (newValue == '127.0.0.1') {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！不能是127.0.0.1");
			$(e).select();
			return false;
		}
		return true;
	}
	// 集客部联系电话
	if ("1105014221" == attrCode || "1105014219" == attrCode
			|| "1105014216" == attrCode || "1105014232" == attrCode
			|| "1105014232" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("1105011009" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).select();
			return false;
		}
	}

	// 企业代码
	if ("1105012202" == attrCode) {
		// 检查 企业代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，为6位数字");
			$(e).select();
			return false;
		}
	}
}

/* **************************************PushEmail******************************************* */

/*
 * PushEmail（商品）--属性变更校验
 */
function pushEmail_onValueChange(e, attrCode, oldValue, newValue) {
	if ("1101010007" == attrCode || "1112045005" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).select();
			return false;
		}
	}
	if ("1101018008" == attrCode || "1101018007" == attrCode
			|| "1101018002" == attrCode || "1101018006" == attrCode
			|| "1112019008" == attrCode || "1112019007" == attrCode
			|| "1112019002" == attrCode || "1112019006" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).select();
			return false;
		}
	}
}

/* **************************************跨省VPMN******************************************* */

/*
 * 跨省VPMN（商品）--属性变更校验
 */
function groupVPMN_onValueChange(e, attrCode, oldValue, newValue) {

	// 合同附件
	if ("1111019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.showErrorMessage($(e).attr('desc') + "不能为空！");
			$(e).select();
			return false;
		}
	}

}

/* **************************************跨省集团wlan******************************************* */

/*
 * 跨省集团wlan（商品）--属性变更校验
 */
function groupWlan_onValueChange(e, attrCode, oldValue, newValue) {

	// 处理意见
	if ("301013483" == attrCode) {
		if ("0" == newValue) {
			// 未处理原因
			$("#input_301013475").attr("nullable", "yes");
		}

		if ("1" == newValue) {
			// 未处理原因
			$("#input_301013475").attr("nullable", "no");
		}
	}

	// A端对应省公司
	if ("301013311" == attrCode) {
		selectChange(e, attrCode, '301013305', newValue);
	}

	// 账号数量
	if ("301014327" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).select();
			return false;
		}
	}
	// 联系电话
	if ("301013313" == attrCode || "301013406" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
	// 带宽
	if ("301013307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入数字加单位!");
			$(e).select();
			return false;
		}
	}
}

/* **************************************企业一卡通******************************************* */

/*
 * 企业一卡通（商品）--属性变更校验
 */
function businessCard_onValueChange(e, attrCode, oldValue, newValue) {

	// 每日/月下发的最大条数
	if ("247011032" == attrCode || "247011033" == attrCode
			|| "247021032" == attrCode || "247021033" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).select();
			return false;
		}
	}

	// 时间格式
	if ("247021036" == attrCode || "247011034" == attrCode
			|| "247011035" == attrCode || "247021034" == attrCode
			|| "247021035" == attrCode || "331051035" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}

	// EC客户长服务和基本接入号
	if ("247011009" == attrCode || "247021009" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要填入5位数字");
			$(e).select();
			return false;
		}

		// 24701
		$('#input_247012009').val(newValue);
		$('#input_247012009').attr("disabled", true);

		// 24702
		$('#input_247021019').val(newValue);
		$('#input_247021019').attr("disabled", true);
		$('#input_247021025').val(newValue);
		$('#input_247021025').attr("disabled", true);
		$('#input_247022019').val(newValue);
		$('#input_247022019').attr("disabled", true);

	}
	// 英文签名
	if ("247020050" == attrCode || "247010050" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).select();
			return false;
		}
	}
	// 中文签名
	if ("247021031" == attrCode || "247011031" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}
	// 手机号码
	if ("247020003" == attrCode || "247010003" == attrCode
			|| "247030023" == attrCode || "248030023" == attrCode
			|| "249030023" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).select();
			return false;
		}
	}
	return true;
}

/* **************************************集团会议电话******************************************* */

/*
 * 集团会议电话(商品) --属性变更校验
 */
function groupMeetPhone_onValueChange(e, attrCode, oldValue, newValue) {

	if ("9905019105010002" == attrCode || "9905010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码！");
			$(e).select();
			return false;
		}
	}

	if ("9905019105010001" == attrCode || "9905010001" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	return true;
}

/* **************************************移动会议电话******************************************* */

/*
 * 移动会议电话（商品）--属性变更校验
 */
function conferencePhone_onValueChange(e, attrCode, oldValue, newValue) {

	if ("9105019105010002" == attrCode || "9105010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码！");
			$(e).select();
			return false;
		}
	}

	if ("9105019105010001" == attrCode || "9105010001" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	return true;
}

/* **************************************呼叫中心直联******************************************* */

/*
 * 呼叫中心直联--属性变更校验
 */
function callCenterJoint_onValueChange(e, attrCode, oldValue, newValue) {
	// 每日、月下发的最大条数
	if ("1113025005" == attrCode || "1113025004" == attrCode
			|| "1113026003" == attrCode) {
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}
	// 联系人电话
	if ("1113026010" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1113019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.showErrorMessage($(e).attr('desc') + "不能为空！");
			$(e).select();
			return false;
		}
	}
	return true;
}

/* **************************************400国际业务******************************************* */

/*
 * 400国际业务--属性变更校验
 */
function bnsIter400_onValueChange(e, attrCode, oldValue, newValue) {
	// 每日、月下发的最大条数
	if ("4115071033" == attrCode || "4115071032" == attrCode) {
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}
	// EC长服务代码
	if ("4115072009" == attrCode) {
		if (newValue.indexOf("10657") != 0) {
			$.showErrorMessage($(e).attr('desc')
					+ "格式不正确，需要以10657开头+400语音的400号码");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("4115071009" == attrCode) {
		if (newValue.indexOf("10657") != 0) {
			$.showErrorMessage($(e).attr('desc')
					+ "格式不正确，需要以10657开头+400语音的400号码");
			$(e).select();
			return false;
		}
	}
	// 集团客户短信接收手机号
	if ("4115070003" == attrCode) {
		if (!g_IsMobileNumber) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}
	// 中文短信/彩信正文签名
	if ("4115071031" == attrCode) {
		if (newValue.toString().length > 8) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于8位");
			$(e).select();
			return false;
		}
	}
	// EC企业码
	if ("4115071028" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).select();
			return false;
		}
	}

	// 不允许下发开始时间（HHMMSS） 不允许下发结束时间（HHMMSS）
	if ("4115071034" == attrCode || "4115071035" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		// 时间格式：hhmmss
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}
}

/* **************************************400业务******************************************* */
//400号码重复校验
function  check400Num(newValue){
	var result=false 
	 Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'check400Num',
	            '&ACCESSNUM=' + newValue, function(d) {
		 		result = d.map.result;
	            }, function(e, i) {
	                $.validate.alerter.one($(e)[0], "操作失败");
	                return false;
	            },{async:false});
	
	return result;
}
/*
 * 400业务--属性变更校验
 */
function business400_onValueChange(e, attrCode, oldValue, newValue) {

	// 阻截省下拉联动
	if ("4115027012" == attrCode) {
		selectChange(e, attrCode, '4115027016', newValue);
	}
	// 时间格式：hhmmss
	if ("4115061035" == attrCode || "4115091034" == attrCode
			|| "4115061034" == attrCode || "4115091035" == attrCode
			|| "4115081034" == attrCode || "4115081035" == attrCode) {
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}
	// 400预占流水号
	if ("4115017007" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		if (newValue.length != 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度为6位");
			$(e).select();
			return false;
		}
	}

	// 400号码
	if ("4115017001" == attrCode || "4115027011" == attrCode
			|| "4115037011" == attrCode || "4115047011" == attrCode
			|| "4115097011" == attrCode || "4115087011" == attrCode
			|| "4115067011" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("4001") != 0 && newValue.indexOf("4007") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要以4001或4007开头");
			$(e).select();
			return false;
		}
		if (newValue.length != 10) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度为10位");
			$(e).select();
			return false;
		}

		$('#input_4115061009').val(newValue);
		$('#input_4115061009').attr("disabled", true);
		$('#input_4115062009').val(newValue);
		$('#input_4115062009').attr("disabled", true);

		$('#input_4115081009').val(newValue);
		$('#input_4115081009').attr("disabled", true);
		$('#input_4115082009').val(newValue);
		$('#input_4115082009').attr("disabled", true);

		$('#input_4115091009').val(newValue);
		$('#input_4115091009').attr("disabled", true);
		$('#input_4115092009').val(newValue);
		$('#input_4115092009').attr("disabled", true);

	}
	
	if ("4115017001" == attrCode){
		//400重复号码校验
		var result= check400Num(newValue);
		if("true"==result){
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "该400号码在系统已存在有效信息，请联系集团公司重新预占！");
			$(e).val("");
			$(e).select();
			return false;
		}
		
	}

	// 英文短信/彩信正文签名
	if ("4115060050" == attrCode || "4115080050" == attrCode
			|| "4115090050" == attrCode || "4115070050" == attrCode) {
		var str = /^[a-zA-Z]{1,16}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号
	if ("4115061009" == attrCode || "4115091009" == attrCode
			|| "4115081009" == attrCode) {

		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.length < 15) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于15位");
			$(e).select();
			return false;
		}

	}

	// EC长服务代码
	if ("4115062009" == attrCode || "4115092009" == attrCode
			|| "4115082009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.length < 15) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能小于15位");
			$(e).select();
			return false;
		}
	}
	// EC企业码
	if ("4115061028" == attrCode || "4115091028" == attrCode
			|| "4115081028" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).select();
			return false;
		}
	}
	// 中文短信/彩信正文签名
	if ("4115061031" == attrCode || "4115091031" == attrCode
			|| "4115071031" == attrCode || "4115081031" == attrCode) {
		if (newValue.toString().length > 8) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于8位");
			$(e).select();
			return false;
		}
	}
	// 每日、月下发的最大条数
	if ("4115061033" == attrCode || "4115061032" == attrCode
			|| "4115091032" == attrCode || "4115091033" == attrCode
			|| "4115071032" == attrCode || "4115071033" == attrCode
			|| "4115081032" == attrCode || "4115081033" == attrCode) {
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}
	// 邮箱
	if ("4115017008" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱格式");
			$(e).select();
			return false;
		}
	}
	// 0－400平台网站不可以上传白名单,1-可以上传白名单 || 0－主叫号码按发话位置1－主叫号码按归属位置
	if ("4115017031" == attrCode || "4115017030" == attrCode) {
		if ("0" != newValue && "1" != newValue) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入0或者1");
			$(e).select();
			return false;
		}
	}
	// 400语音密码接入
	if ("4115037013" == attrCode) {
		if (newValue.length > 4) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，长度不能大于4位");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
	}
	// 目的地号码数量
	if ("4115017032" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为数字");
			$(e).select();
			return false;
		}
		if (parseInt(newValue) < 1) {
			$.showErrorMessage($(e).attr('desc') + "、不能小于1");
			$(e).select();
			return false;
		}
		var operType = $("#productOperType").val();
		if (operType == '9') {
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
						if (parseInt(newValue) < parseInt(attr_value)) {
							$.showSucMessage("目的地号码由多到少时,请先在平台删除号码");
						}

					}, function(e, i) {
						$.showErrorMessage("操作失败");
						result = false;
					}, {
						async : false
					});
		}
	}

	//整数校验
	if("4115047014" == attrCode || "4115047015" == attrCode){
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr("desc") + "格式不正确，请确保所有字符都为整数");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要小于等于9位整数");
			$(e).select();
			return false;
		}
		var pattern = /^0.*/;
		if(pattern.test(newValue)){
			$.showErrorMessage($(e).attr("desc") + "格式不正确，请不要输入以0开头的数字");
			$(e).select();
			return false;
		}
	}

}

/* **************************************集团客户一点支付******************************************* */

/*
 * 集团客户一点支付--属性变更校验
 */
function GroupCustLine_onValueChange(e, attrCode, oldValue, newValue) {
	// 管理员邮箱
	if ("999033712" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联动下拉框
	// 阻截省下拉联动
	if ("999033720" == attrCode) {
		selectChange(e, attrCode, '999033721', newValue);
	}

	// 联系人电话
	if ("999033725" == attrCode || "999033723" == attrCode
			|| "999033711" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

	// 代付比例
	if ("999031008" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).select();
			return false;
		}
		return true;
	}

	// 代付金额
	if ("999031007" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应大于0");
			$(e).select();
			return false;
		}
		return true;
	}

	// 是否统一资费
	if ("999033703" == attrCode) {
		if ("1" == newValue) {
			$('#PARAM_NAME_999033704').addClass('e_required');
			$('#input_999033704').attr('nullable', 'no');
		} else if ("0" == newValue) {
			$('#PARAM_NAME_999033704').removeClass('e_required');
			$('#input_999033704').attr('nullable', 'yes');
		}
	}
}

/* **************************************个人帐单代付(老一点支付)******************************************* */

/*
 * 个人帐单代付--属性变更校验
 */
function PersonBillPay_onValueChange(e, attrCode, oldValue, newValue) {
	// 代付比例
	if ("999021008" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).select();
			return false;
		}
		return true;
	}

	// 代付金额
	if ("999021007" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应大于0");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* **************************************跨省行业应用卡******************************************* */

/*
 * 跨省行业应用卡--属性变更校验
 */
function specialApp_onValueChange(e, attrCode, oldValue, newValue) {
	// 配合省联系人手机号
	if ("9116014552" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}
}

/* **************************************跨国数据专线******************************************* */

/*
 * 跨国数据专线--属性变更校验
 */
function gloabSpecialLine_onValueChange(e, attrCode, oldValue, newValue) {

	// 联系人电话
	if ("1112073322" == attrCode || "1112073313" == attrCode
			|| "1112073407" == attrCode || "1112073444" == attrCode
			|| "1112073411" == attrCode || "1112063407" == attrCode
			|| "1112063411" == attrCode || "1112063322" == attrCode
			|| "1112063313" == attrCode || "1112063444" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
	// 判断是否为数字
	if ("1112063352" == attrCode || "1112063349" == attrCode
			|| "1112073349" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}
	// 带宽(请填写单位:M或G)
	if ("1112063307" == attrCode || "1112073307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).select();
			return false;
		}

	}
	//B类服务类型联动
	if("1112073327" == attrCode){
		selectChange(e, attrCode, '1112073316', newValue);
		selectChange(e, attrCode, '1112073440', newValue);
		selectChange(e, attrCode, '1112073325', newValue);
	}

	//A类服务类型联动
	if("1112063327" == attrCode){
		selectChange(e, attrCode, '1112063316', newValue);
		selectChange(e, attrCode, '1112063325', newValue);
		selectChange(e, attrCode, '1112063440', newValue);
	}

	//接口类型验证
	if("1112073325" == attrCode || "1112073316" == attrCode || "1112073340" == attrCode ||
			"1112063316" == attrCode || "1112063325" == attrCode || "1112063440" == attrCode){
		if($("#productOperType").val() != "10" && newValue == "待定"){
			$.showErrorMessage($(e).attr('desc') + "中待定选项只能在预受理时选择！");
			$(e).select();
			return false;
		}
	}
}

/* **************************************本地企业飞信******************************************* */

/*
 * 本地企业飞信--属性变更校验
 */
function locGroupFetion_onValueChange(e, attrCode, oldValue, newValue) {
	// 管理员邮箱
	if ("910601006" == attrCode || "9106011019" == attrCode
			|| "910602006" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}
	// 集团客户联系人手机号码
	if ("910601007" == attrCode || "910602007" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}
	// 企业能力阀值
	if ("910601008" == attrCode || "910602008" == attrCode) {
		if (newValue.toString().length < 3 || newValue.indexOf("1:") != 0) {
			$.showErrorMessage($(e).attr('desc') + "格式不正，正确的形式为[1:XXXXX]");
			$(e).select();
			return false;
		}
	}

	// 注册号码
	if ("9106011017" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入手机号码或固定电话");
			$(e).select();
			return false;
		}
	}
	// 代付比例
	if ("9106011025" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("9106011024" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc') + "不正确,应大于等于0");
			$(e).select();
			return false;
		}
		return true;
	}

}

/* ***********************************企业手机报(商品)************************************* */

/*
 * 企业手机报--属性变更校验
 */
function enterpriseMobiPaperChange(e, attrCode, oldValue, newValue) {

	// 中文短信/彩信正文签名
	if ("40104010001" == attrCode || "40104020001" == attrCode
			|| "40104030001" == attrCode || "40104040001" == attrCode
			|| "40104050001" == attrCode || "40104060001" == attrCode
			|| "40104070001" == attrCode || "40104080001" == attrCode
			|| "40104090001" == attrCode || "40104100001" == attrCode
			|| "40104110001" == attrCode || "40104120001" == attrCode
			|| "40104130001" == attrCode || "40104140001" == attrCode
			|| "40104150001" == attrCode || "40104160001" == attrCode
			|| "40104170001" == attrCode || "40104180001" == attrCode
			|| "40104190001" == attrCode || "40104200001" == attrCode
			|| "40104210001" == attrCode || "40104220001" == attrCode
			|| "40104230001" == attrCode || "40104240001" == attrCode
			|| "40104250001" == attrCode || "40104260001" == attrCode
			|| "40104270001" == attrCode || "40104280001" == attrCode
			|| "40104290001" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}

	// 英文短信/彩信正文签名
	if ("40104010002" == attrCode || "40104020002" == attrCode
			|| "40104030002" == attrCode || "40104040002" == attrCode
			|| "40104050002" == attrCode || "40104060002" == attrCode
			|| "40104070002" == attrCode || "40104080002" == attrCode
			|| "40104090002" == attrCode || "40104100002" == attrCode
			|| "40104110002" == attrCode || "40104120002" == attrCode
			|| "40104130002" == attrCode || "40104140002" == attrCode
			|| "40104150002" == attrCode || "40104160002" == attrCode
			|| "40104170002" == attrCode || "40104180002" == attrCode
			|| "40104190002" == attrCode || "40104200002" == attrCode
			|| "40104210002" == attrCode || "40104220002" == attrCode
			|| "40104230002" == attrCode || "40104240002" == attrCode
			|| "40104250002" == attrCode || "40104260002" == attrCode
			|| "40104270002" == attrCode || "40104280002" == attrCode
			|| "40104290002" == attrCode) {
		var str = /^[a-zA-Z]{1,16}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).select();
			return false;
		}
	}

	// 验证邮箱
	if ("40104010004" == attrCode || "40104020004" == attrCode
			|| "40104030004" == attrCode || "40104040004" == attrCode
			|| "40104050004" == attrCode || "40104060004" == attrCode
			|| "40104070004" == attrCode || "40104080004" == attrCode
			|| "40104090004" == attrCode || "40104100004" == attrCode
			|| "40104110004" == attrCode || "40104120004" == attrCode
			|| "40104130004" == attrCode || "40104140004" == attrCode
			|| "40104150004" == attrCode || "40104160004" == attrCode
			|| "40104170004" == attrCode || "40104180004" == attrCode
			|| "40104190004" == attrCode || "40104200004" == attrCode
			|| "40104210004" == attrCode || "40104220004" == attrCode
			|| "40104230004" == attrCode || "40104240004" == attrCode
			|| "40104250004" == attrCode || "40104260004" == attrCode
			|| "40104270004" == attrCode || "40104280004" == attrCode
			|| "40104290004" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}
	// 手机验证
	if ("40104010005" == attrCode || "40104020005" == attrCode
			|| "40104030005" == attrCode || "40104040005" == attrCode
			|| "40104050005" == attrCode || "40104060005" == attrCode
			|| "40104070005" == attrCode || "40104080005" == attrCode
			|| "40104090005" == attrCode || "40104100005" == attrCode
			|| "40104110005" == attrCode || "40104120005" == attrCode
			|| "40104130005" == attrCode || "40104140005" == attrCode
			|| "40104150005" == attrCode || "40104160005" == attrCode
			|| "40104170005" == attrCode || "40104180005" == attrCode
			|| "40104190005" == attrCode || "40104200005" == attrCode
			|| "40104210005" == attrCode || "40104220005" == attrCode
			|| "40104230005" == attrCode || "40104240005" == attrCode
			|| "40104250005" == attrCode || "40104260005" == attrCode
			|| "40104270005" == attrCode || "40104280005" == attrCode
			|| "40104290005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}

	// 服务代码验证
	if ("40104010006" == attrCode || "40104020006" == attrCode
			|| "40104030006" == attrCode || "40104040006" == attrCode
			|| "40104050006" == attrCode || "40104060006" == attrCode
			|| "40104070006" == attrCode || "40104080006" == attrCode
			|| "40104090006" == attrCode || "40104100006" == attrCode
			|| "40104110006" == attrCode || "40104120006" == attrCode
			|| "40104130006" == attrCode || "40104140006" == attrCode
			|| "40104150006" == attrCode || "40104160006" == attrCode
			|| "40104170006" == attrCode || "40104180006" == attrCode
			|| "40104190006" == attrCode || "40104200006" == attrCode
			|| "40104210006" == attrCode || "40104220006" == attrCode
			|| "40104230006" == attrCode || "40104240006" == attrCode
			|| "40104250006" == attrCode || "40104260006" == attrCode
			|| "40104270006" == attrCode || "40104280006" == attrCode
			|| "40104290006" == attrCode) {
		var filter = /^[0-9]{6}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为6位数字");
			$(e).select();
			return false;
		}
	}

	// 渠道编码
	if ("40104010010" == attrCode || "40104020010" == attrCode
			|| "40104030010" == attrCode || "40104040010" == attrCode
			|| "40104050010" == attrCode || "40104060010" == attrCode
			|| "40104070010" == attrCode || "40104080010" == attrCode
			|| "40104090010" == attrCode || "40104100010" == attrCode
			|| "40104110010" == attrCode || "40104120010" == attrCode
			|| "40104130010" == attrCode || "40104140010" == attrCode
			|| "40104150010" == attrCode || "40104160010" == attrCode
			|| "40104170010" == attrCode || "40104180010" == attrCode
			|| "40104190010" == attrCode || "40104200010" == attrCode
			|| "40104210010" == attrCode || "40104220010" == attrCode
			|| "40104230010" == attrCode || "40104240010" == attrCode
			|| "40104250010" == attrCode || "40104260010" == attrCode
			|| "40104270010" == attrCode || "40104280010" == attrCode
			|| "40104290010" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}

}

/* *******************************企业阅读商品***************************************** */

/*
 * 企业阅读商品--属性变更校验
 */
function enterpriseRead_onValuechange(e, attrCode, oldValue, newValue) {

	// 中文短信/彩信正文签名
	if ("40105010001" == attrCode || "40105020001" == attrCode
			|| "40105030001" == attrCode || "40105040001" == attrCode
			|| "40105050001" == attrCode || "40105060001" == attrCode
			|| "40105070001" == attrCode || "40105080001" == attrCode
			|| "40105090001" == attrCode || "40105100001" == attrCode
			|| "40105110001" == attrCode || "40105120001" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}

	// 英文短信/彩信正文签名
	if ("40105010002" == attrCode || "40105020002" == attrCode
			|| "40105030002" == attrCode || "40105040002" == attrCode
			|| "40105050002" == attrCode || "40105060002" == attrCode
			|| "40105070002" == attrCode || "40105080002" == attrCode
			|| "40105090002" == attrCode || "40105100002" == attrCode
			|| "40105110002" == attrCode || "40105120002" == attrCode) {
		var str = /^[a-zA-Z]{1,16}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).select();
			return false;
		}
	}

	// 验证邮箱
	if ("40105010004" == attrCode || "40105020004" == attrCode
			|| "40105030004" == attrCode || "40105040004" == attrCode
			|| "40105050004" == attrCode || "40105060004" == attrCode
			|| "40105070004" == attrCode || "40105080004" == attrCode
			|| "40105090004" == attrCode || "40105100004" == attrCode
			|| "40105110004" == attrCode || "40105120004" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机验证
	if ("40105010005" == attrCode || "40105020005" == attrCode
			|| "40105030005" == attrCode || "40105040005" == attrCode
			|| "40105050005" == attrCode || "40105060005" == attrCode
			|| "40105070005" == attrCode || "40105080005" == attrCode
			|| "40105090005" == attrCode || "40105100005" == attrCode
			|| "40105110005" == attrCode || "40105120005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}

	// 渠道编码
	if ("40105010010" == attrCode || "40105020010" == attrCode
			|| "40105030010" == attrCode || "40105040010" == attrCode
			|| "40105050010" == attrCode || "40105060010" == attrCode
			|| "40105070010" == attrCode || "40105080010" == attrCode
			|| "40105090010" == attrCode || "40105100010" == attrCode
			|| "40105110010" == attrCode || "40105120010" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,16}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc')
					+ "格式不正确,必须为中文，字母或者数字，但不能超过16位");
			$(e).select();
			return false;
		}
	}
}

/* *********************************IMS多媒体彩铃*************************************** */
/*
 * IMS多媒体彩铃--属性变更校验
 */
function customRingingIMS_onValueChange(e, attrCode, oldValue, newValue) {

	// 管理员手机号码
	if ("9104010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).select();
			return false;
		}
	}
	// 管理员邮箱
	if ("9104010001" == attrCode || "9104011011" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* *********************************本地M2M*************************************** */

/*
 * 本地M2M（商品）--属性变更校验
 */
function localM2M_onValueChange(e, attrCode, oldValue, newValue) {

	if ("1101070004" == attrCode || "1101070002" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
	// 合同附件
	if ("1101070011" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.showErrorMessage($(e).attr('desc') + "不能为空！");
			$(e).select();
			return false;
		}
	}

}

/* *********************************专线业务*************************************** */

/*
 * 跨省数据专线 --hzl
 */
function DataLineBusiness_onValueChange(e, attrCode, oldValue, newValue) {

	// A 端省公司 1112053311 
	if ("1112413311" == attrCode) {
		selectChange(e, attrCode, '1112413305', newValue);
	}
	// Z端省公司 1112053306
	if ("1112413320" == attrCode) {
		selectChange(e, attrCode, '1112413306', newValue);
	}
	// A 端省公司 1112053311 1112053305
	if ("1112413311" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112413305').attr('value', '');
			changeValue($('#input_1112413305')[0]);
			$('#input_1112413305').attr('disabled', true);
			return true;
		} else {
			$('#input_1112413305').attr('disabled', false);
		}

		// 根据选择的省公司获取该省公司对应的城市
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112413305',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112413305')[0].innerHTML = "";
							$('#input_1112413305').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}
	// Z 端省公司 1112053311 1112053305
	if ("1112413320" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112413306').attr('value', '');
			changeValue($('#input_1112413306')[0]);
			$('#input_1112413306').attr('disabled', true);
			return true;
		} else {
			$('#input_1112413306').attr('disabled', false);
		}

		// 根据选择的省公司获取该省公司对应的城市
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112413306',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112413306')[0].innerHTML = "";
							$('#input_1112413306').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}
	//新增区县下拉框值，根据选择省市
	//A端所在城市  1112413305 
	if ("1112413305" == attrCode) {
		selectChange(e, attrCode, '1112415022', newValue);
	}
	// A端所在城市  1112413305    A端区县 1112415022 
	if ("1112413305" == attrCode) {
		// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112415022').attr('value', '');
			changeValue($('#input_1112415022')[0]);
			$('#input_1112415022').attr('disabled', true);
			return true;
		} else {
			$('#input_1112415022').attr('disabled', false);
		}

		// 根据选择的A端所在城市，返回相应区县
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112415022',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112415022')[0].innerHTML = "";
							$('#input_1112415022').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}
	//Z端所在城市  1112413306  
	if ("1112413306" == attrCode) {
		selectChange(e, attrCode, '1112415023', newValue);
	}
	//Z端所在城市  1112413306  Z端区县 1112415023 
	if ("1112413306" == attrCode) {
		// 如果省份是请选择的情况下，则区县对应的组件设置成不可编辑的状态，否则可编辑
		if (newValue == '' || newValue == null) {
			$('#input_1112415023').attr('value', '');
			changeValue($('#input_1112415023')[0]);
			$('#input_1112415023').attr('disabled', true);
			return true;
		} else {
			$('#input_1112415023').attr('disabled', false);
		}

		// 根据选择的Z端所在城市，返回相应区县
		Wade.httphandler
				.submit(
						'',
						'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
						'chooseCitys',
						'&PROVINCE_ATTR_CODE=' + attrCode
								+ '&PROVINCE_ATTR_VALUE=' + newValue
								+ '&CITY_ATTR_CODE=1112415023',
						function(d) {
							// 拼option对象
							var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";
							var citys = d.map.result;
							for (var i = 0; i < citys.length; i++) {
								innerObj = innerObj + "<OPTION title="
										+ citys.get(i, 'OPTION_NAME')
										+ " value="
										+ citys.get(i, 'OPTION_VALUE') + ">"
										+ citys.get(i, 'OPTION_NAME')
										+ "</OPTION>";
							}

							// 新option对象替换老对象
							$('#input_1112415023')[0].innerHTML = "";
							$('#input_1112415023').html(innerObj);
						}, function(e, i) {
							$.showErrorMessage("操作失败");
							$(e).select();
							return false;
						});
	}

}

/*
 * 专线业务--属性变更校验
 */
function LineBusiness_onValueChange(e, attrCode, oldValue, newValue) {

	if ("1112053407" == attrCode || "1112053313" == attrCode
			|| "1112053322" == attrCode || "1112053411" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
	// A 端省公司 1112053311 1112053305
	if ("1112053311" == attrCode) {
		selectChange(e, attrCode, '1112053305', newValue);
	}
	// Z端省公司 1112053306
	if ("1112053320" == attrCode) {
		selectChange(e, attrCode, '1112053306', newValue);
	}

	if ("1112054314" == attrCode || "1112054311" == attrCode
			|| "1112054315" == attrCode || "1112054312" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.showErrorMessage($(e).attr('desc')
					+ "不正确,应为0-100的整数,且所有月使用费用（A端，Z端）的比例和为100");
			$(e).select();
			return false;
		}
	}
	if ("1112054314" == attrCode) {

		var i = 100 - $('#input_1112054314').val();
		$('#input_1112054315').val(i);
		changeValue($('#input_1112054315')[0]);
		$('#input_1112054315').attr('disabled', false);

	}
	if ("1112054315" == attrCode) {

		var i = 100 - $('#input_1112054315').val();
		$('#input_1112054314').val(i);
		changeValue($('#input_1112054314')[0]);
		$('#input_1112054314').attr('disabled', false);
	}
	if ("1112054311" == attrCode) {

		var i = 100 - $('#input_1112054311').val();
		$('#input_1112054312').val(i);
		changeValue($('#input_1112054312')[0]);
		$('#input_1112054312').attr('disabled', false);
	}
	if ("1112054312" == attrCode) {

		var i = 100 - $('#input_1112054312').val();
		$('#input_1112054311').val(i);
		changeValue($('#input_1112054311')[0]);
		$('#input_1112054311').attr('disabled', false);
	}

	// 带宽(请填写单位:M或G)
	if ("1112053307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).select();
			return false;
		}
	}

}

/* *********************************主办省全网MAS彩信*************************************** */

/*
 * 主办省全网MAS彩信（商品）--属性变更校验
 */
function hostNetMasMMS_onValueChange(e, attrCode, oldValue, newValue) {

	// 联系电话
	if ("199024216" == attrCode || "199024221" == attrCode
			|| "199014232" == attrCode || "199024232" == attrCode) {

		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).select();
			return false;
		}

	}

	// 企业代码
	if ("199020004" == attrCode || "101023000" == attrCode
			|| "102023000" == attrCode || "104023000" == attrCode
			|| "105023000" == attrCode || "106023000" == attrCode
			|| "107023000" == attrCode || "108023000" == attrCode) {
		// 检查 企业代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！为6位数字");
			$(e).select();
			return false;
		}
	}

	// 中、英文短信/彩信正文签名
	if ("101020043" == attrCode || "101020044" == attrCode
			|| "102020043" == attrCode || "102020044" == attrCode
			|| "104020043" == attrCode || "104020044" == attrCode
			|| "105020043" == attrCode || "105020044" == attrCode
			|| "106020043" == attrCode || "106020044" == attrCode
			|| "107020043" == attrCode || "107020044" == attrCode
			|| "108020043" == attrCode || "108020044" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}
	// 彩信基本接入号
	if ("199021019" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为数字");
			$(e).select();
			return false;
		}
	}

	// 主IP地址(浮动IP请填写“127.0.0.1”)
	if ("199024417" == attrCode || "199024418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "不正确，必须是1-255之间");
			$(e).select();
			return false;
		}
	}

	// 数字
	if ("199024202" == attrCode || "199024203" == attrCode
			|| "199024209" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为数字");
			$(e).select();
			return false;
		}
		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).select();
			return false;
		}
	}
}

/* *********************************主办省全网MAS短信*************************************** */

/*
 * 主办省全网MAS短信（商品）--属性变更校验
 */
function hostNetMas_onValueChange(e, attrCode, oldValue, newValue) {
	// 企业代码
	if ("199010004" == attrCode) {
		// 检查 企业代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，为6位数字");
			$(e).select();
			return false;
		}
	}

	// 英文签名
	if ("199010013" == attrCode) {
		var filter = /^[a-zA-Z]{1,8}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过8位");
			$(e).select();
			return false;
		}
	}
	// 中文签名
	if ("199010012" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("199011009" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为数字");
			$(e).select();
			return false;
		}
	}

	// 数字
	if ("199014202" == attrCode || "199014203" == attrCode
			|| "199014211" == attrCode || "199014209" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为数字");
			$(e).select();
			return false;
		}
		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).select();
			return false;
		}
	}
	// 联系电话
	// 集客部联系电话
	if ("199014216" == attrCode || "199014219" == attrCode
			|| "199014221" == attrCode || "1101020031" == attrCode
			|| "1101020027" == attrCode || "199014232" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
	// IP地址
	if ("199014418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).select();
			return false;
		}
		if (newValue == '127.0.0.1') {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，不能是127.0.0.1");
			$(e).select();
			return false;
		}
	}

	if ("199014417" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		var strarr = newValue.split(",");
		for (var i = 0; i < strarr.length; i++) {
			var laststr = strarr[i].split(".");
			if (!zz.test(strarr[i])) {
				$.showErrorMessage($(e).attr('desc')
						+ "格式不正确，需要000.000.000.000格式");
				$(e).select();
				return false;
			}
			if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
					|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
				$.showErrorMessage($(e).attr('desc') + "格式不正确，必须是1-255之间");
				$(e).select();
				return false;
			}
			if (strarr[i] == '127.0.0.1') {
				$.showErrorMessage($(e).attr('desc') + "格式不正确，不能是127.0.0.1");
				$(e).select();
				return false;
			}
		}
	}
	//端口速率
	if ("199010011" == attrCode) {
		// var custId = $.getSrcWindow().$("#CUST_ID").val();
		var group_id = $.getSrcWindow().$("#GROUP_ID").val();

		var serv_level = "";
		// 校验目的地
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
		'qryGrpByGroupId', '&GROUP_ID=' + group_id, function(d) {
			var param = d.map.result;
			serv_level = param.get(0, 'SERV_LEVEL');
			if (serv_level==null||serv_level==undefined) {
				$.showSucMessage("查询不到集团服务等级信息,请先完善集团信息");
				$(e).val("");
				$(e).select();
			}

		}, function(e, i) {
			$.showErrorMessage("操作失败");
			$(e).val("");
			$(e).select();
			result = false;
		}, {
			async : false
		});
	    if (serv_level=="1") //金牌级：<=2000
	    {
	    	if(parseInt(newValue)>2000){
		        errinfo="金牌级：最大短信端口速率不能超过2000!";
		        $.showErrorMessage($(e).attr('desc') + errinfo);
		        $(e).val("");
		        $(e).select();
			    return false;
	    	}
	    }
		if (serv_level=="3"||serv_level=="2") //铜牌、银牌级：<=100
		{
			if(parseInt(newValue)>100){
			    errinfo="铜牌、银牌级：最大短信端口速率不能超过100!";
			    $.showErrorMessage($(e).attr('desc') + errinfo);
			    $(e).val("");
			    $(e).select();
			    return false;
			}
		}
		if (serv_level=="4"||serv_level=="") //标准级：<=20
		{
			if(parseInt(newValue)>20){
			    errinfo="标准级：最大短信端口速率不能超过20!";
			    $.showErrorMessage($(e).attr('desc') + errinfo);
			    $(e).val("");
			    $(e).select();
			    return false;
			}
		}
	}
}

/* *********************************公众服务云业务*************************************** */

/*
 * 公众服务云业务--属性变更校验
 */
function pubCloudSvc_onValueChange(e, attrCode, oldValue, newValue) {

	// 集客部联系电话
	if ("1116011005" == attrCode || "1116011004" == attrCode) {
		if (!g_IsTelephoneNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
	}

	// 手机号码
	if ("1116011002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
	}

	// 邮件
	if ("1116011003" == attrCode || "1116011006" == attrCode
			|| "1116011013" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}

	  //资源产品优惠类型选择免费测试时，测试计划开始日期、测试计划结束日期、测试申请单附件字段必填，并且需要将费用折扣置为0且不可编辑
   if("1116013001" == attrCode){
   		if("1" == newValue){//1-免费测试   2-折扣优惠 3-标准资费
   			var eosInfo = $('#ISESOPTAG').val();
 		    /*if(eosInfo != "1")
 		    {
 		    	$.showErrorMessage($(e).attr('desc')+"选择免费测试 时，请走esop审核流程！");
 		    	$('#input_1116013001').val('');
 		    	return false;
 		    }*/
   			//计划开始日期
			$('#PARAM_NAME_1116013002').attr("class", "e_required");
			$('#input_1116013002').attr("nullable", "no");
			//计划结束日期
			$('#PARAM_NAME_1116013003').attr("class", "e_required");
			$('#input_1116013003').attr("nullable", "no");
			//测试申请单
			$('#PARAM_NAME_1116013006').attr("class", "e_required");
			$('#input_1116013006').attr("nullable", "no");
			//费用折扣
			$('#input_1116013005').val('0');
			$('#input_1116013005').attr("disabled",true);
	   	}else{
		    //计划开始日期
			$('#PARAM_NAME_1116013002').removeClass("e_required");
			$('#input_1116013002').attr("nullable", "yes");
			//计划结束日期
			$('#PARAM_NAME_1116013003').removeClass("e_required");
			$('#input_1116013003').attr("nullable", "yes");
			//测试申请单
			$('#PARAM_NAME_1116013006').removeClass("e_required");
			$('#input_1116013006').attr("nullable", "yes");
			//费用折扣
			$('#input_1116013005').val('');
			$('#input_1116013005').attr("disabled",false);
		   	if("2" == newValue){
		   		$('#input_1116013005').val('100');
		   	}
	   }
   	}

   	//变更类型为1时，资源产品优惠类型可以发生变化
    if("1116011007" == attrCode){
    	if("1" == newValue){//1-免费测试   2-折扣优惠 3-标准资费
   			//优惠类型
			$('#input_1116013001').attr("disabled",falseh);
   		}
   	}

    //省公司的费用折扣不能低于5折
   	if("1116013005" == attrCode){
   		if(parseInt(newValue)<50){
   			$.showErrorMessage($(e).attr('desc')+"对于省公司不能低于5折");
			$(e).select();
			return false;
   		}
   	}

}

/* **************************************统一Centrex业务(商品)******************************************* */

/*
 * 校验多媒体桌面电话产品--属性校验
 *
 */

function checkUnifyC_onValueChange(e, attrCode, oldValue, newValue) {
	// 固话数量必须为数字
	if ("2350013107" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "输入错误!输入必须为数字！请重新输入！");
			$(e).select();
			return false;
		}
		return true;
	}
	// 校验电话号码
	if ("2350013106" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}

}
/*
 * 校验融合V网产品--属性校验
 *
 */
function checkFuseVNet_onValueChange(e, attrCode, oldValue, newValue) {
	// 校验电话号码
	if ("2350023107" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
}

/*
 * 校验融合总机产品--属性校验
 *
 */
function checkSwitchBoard_onValueChange(e, attrCode, oldValue, newValue) {
	// 校验电话号码
	if ("2350033107" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
}

/*
 * 校验融合一号通产品--属性校验
 *
 */
function checkFuseOneAll_onValueChange(e, attrCode, oldValue, newValue) {
	// 校验电话号码
	if ("2350043106" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).select();
			return false;
		}
	}
}

/* **************************************APN漫游******************************************* */

/*
 * APN漫游属性APN后缀名校验
 */

function checkExtensions_onValueChange(e, attrCode, oldValue, newValue) {
	if ("1115010003" == attrCode) {
		var apn = /^\.[a-zA-Z]{1,3}$/;
		var extensions = newValue.substring(newValue.indexOf("."),
				newValue.length);
		if (!apn.test(extensions)) {
			$.showErrorMessage($(e).attr('desc')
					+ "输入错误!请输入带省份简称的后缀，例如：北京为“.bj” 请重新输入!");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* **************************************政务易******************************************* */

/*
 * 政务易(商品) --短信白黑名单业务产品 --属性校验
 */

function checkSMSWhiteList(e, attrCode, oldValue, newValue) {

	// 集团客户短信接收手机号校验
	if ("331070003" == attrCode || "331080003" == attrCode
			|| "331090003" == attrCode || "331060003" == attrCode
			|| "331100003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须为11位手机号码！");
			$(e).select();
			return false;
		}
	}

	// 中文短信/彩信正文签名
	if ("331070031" == attrCode || "331080031" == attrCode
			|| "331090031" == attrCode || "331100031" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号
	if ("331071009" == attrCode || "331101009" == attrCode
			|| "331091009" == attrCode || "331081009" == attrCode
			|| "331062009" == attrCode) {
		// 数字验证
		if (!g_IsDigit(newValue) || newValue.length != 7) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为7位的数字,1065000+2位省份代码自动生成无需填写");
			$(e).select();
			return false;
		}
		$('#input_331072009').val(newValue);
		$('#input_331072009').attr("disabled", true);
		$('#input_331082009').val(newValue);
		$('#input_331082009').attr("disabled", true);
		$('#input_331092009').val(newValue);
		$('#input_331092009').attr("disabled", true);
		$('#input_331102009').val(newValue);
		$('#input_331102009').attr("disabled", true);

	}

	// 每日下发的最大条数
	if ("331071032" == attrCode || "331071033" == attrCode
			|| "331081032" == attrCode || "331081033" == attrCode
			|| "331091032" == attrCode || "331091033" == attrCode
			|| "331101032" == attrCode || "331101033" == attrCode) {
		var number = /^[0-9]{1,9}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).select();
			return false;
		}
	}

	// 备注 100个字节以内
	if ("331071036" == attrCode || "331081036" == attrCode
			|| "331091036" == attrCode || "331101036" == attrCode) {
		var str = /[^\x00-\xff]/g;
		var valLength = 0;
		for (var ii = 0; ii < newValue.length; ii++) {
			var word = newValue.substring(ii, 1);
			if (str.test(word)) {
				valLength += 2;
			} else {
				valLength++;
			}
		}
		if (valLength > 100) {
			$.showErrorMessage($(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).select();
			return false;
		}
	}

	// 日期格式
	if ("331071034" == attrCode || "331071035" == attrCode
			|| "331081034" == attrCode || "331081035" == attrCode
			|| "331091035" == attrCode || "331091034" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}

	//
	if ("331101028" == attrCode) {
		var number = /^[0-9]{1,12}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须12位以内纯数字");
			$(e).select();
			return false;
		}
	}

	// EC长服务代码
	if ("331072009" == attrCode || "331082009" == attrCode
			|| "331092009" == attrCode || "331102009" == attrCode) {
		if (newValue.substring(0, 7) != "1065000") {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为1065000开头，如:1065000WWXXZZZZZ,WW代表省,XX代表子业务,ZZZZ代表流水号");
			$(e).select();
			return false;
		}
	}

	return true;
}

/* ************************************托管式会议助理商品******************************************** */

/*
 * 托管式会议助理(商品) --属性校验
 */
function managedMeeting_onValueChange(e, attrCode, oldValue, newValue) {

	// 校验签名长度是否符合规范，客户签名2-8个汉字
	if ("40401014001" == attrCode) {
		if (newValue.length > 8 || newValue.length < 2) {
			$.showErrorMessage("中文签名2~8个汉字");
			$(e).select();
			return false;
		}
		return true;
	}
	// EC短信基本接入号
	if ("40401014002" == attrCode) {
		// 数字验证
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为8位数字");
			$(e).select();
			return false;
		}
		if (newValue.length != 8) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为8位数字");
			$(e).select();
			return false;
		}
	}

	// 每日下发的最大条数和每月下发的最大条数
	if ("40401014015" == attrCode || "40401014016" == attrCode) {
		var number = /^[0-9]{1,9}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).select();
			return false;
		}
	}

	// 时间格式：hhmmss
	if ("40401014017" == attrCode || "40401014018" == attrCode) {
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}

	// 备注 100个字节以内
	if ("40401014019" == attrCode) {
		var str = /[^\x00-\xff]/g;
		var valLength = 0;
		for (var ii = 0; ii < newValue.length; ii++) {
			var word = newValue.substring(ii, 1);
			if (str.test(word)) {
				valLength += 2;
			} else {
				valLength++;
			}
		}
		if (valLength > 100) {
			$.showErrorMessage($(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).select();
			return false;
		}
	}

	// 英文签名不超过12个英文字符
	if ("40401014028" == attrCode) {
		var str = /^[a-zA-Z]{1,12}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过12位");
			$(e).select();
			return false;
		}
	}

	// 手机号码
	if ("40401014005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}
}

/* ************************************手机报统付版商品************************************************* */

/*
 * 手机报统付版(商品) --属性校验
 */
function mobiPaperAllPay_onValueChange(e, attrCode, oldValue, newValue) {
	// 2~8个英文
	if ("40103010002" == attrCode || "40103020002" == attrCode
			|| "40103030002" == attrCode || "40103040002" == attrCode
			|| "40103050002" == attrCode || "40103060002" == attrCode
			|| "40103070002" == attrCode || "40103080002" == attrCode
			|| "40103090002" == attrCode || "40103100002" == attrCode
			|| "40103110002" == attrCode) {
		var str = /^[a-zA-Z]{2,8}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且长度为2~8位");
			$(e).select();
			return false;
		}
	}

	// 集团客户联系人邮箱
	if ("40103010004" == attrCode || "40103020005" == attrCode
			|| "40103030005" == attrCode || "40103040005" == attrCode
			|| "40103050005" == attrCode || "40103060005" == attrCode
			|| "40103070005" == attrCode || "40103080005" == attrCode
			|| "40103090005" == attrCode || "40103100005" == attrCode
			|| "40103110005" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}

	// 中文短信/彩信正文签名
	if ("40103010001" == attrCode || "40103020001" == attrCode
			|| "40103030001" == attrCode || "40103040001" == attrCode
			|| "40103050001" == attrCode || "40103060001" == attrCode
			|| "40103070001" == attrCode || "40103080001" == attrCode
			|| "40103090001" == attrCode || "40103100001" == attrCode
			|| "40103110001" == attrCode) {
		var str = /^[\u4E00-\uFA29]{2,8}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为中文!长度为2~8个汉字");
			$(e).select();
			return false;
		}
	}

	// 手机号码
	if ("40103010005" == attrCode || "40103020006" == attrCode
			|| "40103030006" == attrCode || "40103040006" == attrCode
			|| "40103050006" == attrCode || "40103060006" == attrCode
			|| "40103070006" == attrCode || "40103080006" == attrCode
			|| "40103090006" == attrCode || "40103100006" == attrCode
			|| "40103110006" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}
}
/* *****************************************企业彩漫商品******************************************* */
function checkCompanyCartoonMMS(e,attrCode, oldValue, newValue){
	// 手机号码
	if("40106010005" == attrCode){
		if(!g_IsMobileNumber(newValue)){
			$.showErrorMessage($(e).attr('desc') + "格式不正确！必须为11位的手机号码！");
			$(e).select();
			return false;
		}
	}

	//邮箱
	if("40106010004" == attrCode){
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	//服务代码
	if("40106010006" == attrCode){
		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 6) {
			$.showErrorMessage($(e).attr('desc') + "为6位数字");
			$(e).select();
			return false;
		}
	}
	//中英文签名
	if("40106010001" == attrCode){
		var str = /^[a-zA-Z\u4E00-\uFA29]{1,8}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过8个字");
			$(e).select();
			return false;
		}
	}
}

/* ****************************************中央ADC商品******************************************** */

/*
 * 中央ADC业务（商品）--属性校验
 */
function checkCenterADC(e, attrCode, oldValue, newValue) {

	// 手机号码
	if ("1102010003" == attrCode || "1102240003" == attrCode
			|| "208010003" == attrCode || "208020003" == attrCode
			|| "208030003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}

	// 英文短信/彩信正文签名
	if ("1102010050" == attrCode || "1102240050" == attrCode
			|| "208010050" == attrCode || "208020050" == attrCode
			|| "208030050" == attrCode) {
		var str = /^[a-zA-Z]{1,16}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号---EC彩信基本接入号
	if ("1102011009" == attrCode || "1102241019" == attrCode
			|| "208011009" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要填入5位数字");
			$(e).select();
			return false;
		}

		// 110201
		$('#input_1102012009').val(newValue);
		$('#input_1102012009').attr("disabled", true);

		// 110224
		$('#input_1102241025').val(newValue + '01');
		$('#input_1102241025').attr("disabled", true);

		// 20801
		$('#input_208011019').val(newValue);
		$('#input_208011019').attr("disabled", true);

		$('#input_208011025').val(newValue + '01');
		$('#input_208011025').attr("disabled", true);

		$('#input_208012019').val(newValue);
		$('#input_208012019').attr("disabled", true);

		// 20802
		$('#input_208021019').val(newValue);
		$('#input_208021019').attr("disabled", true);

		$('#input_208021025').val(newValue + '01');
		$('#input_208021025').attr("disabled", true);

		$('#input_208022019').val(newValue);
		$('#input_208022019').attr("disabled", true);

		// 20803
		$('#input_208031019').val(newValue);
		$('#input_208031019').attr("disabled", true);

		$('#input_208031025').val(newValue + '01');
		$('#input_208031025').attr("disabled", true);

		$('#input_208032019').val(newValue);
		$('#input_208032019').attr("disabled", true);

	}

	// 数字验证
	if ("1102011028" == attrCode || "1102241028" == attrCode
			|| "208011028" == attrCode || "208021028" == attrCode
			|| "208031028" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为数字");
			$(e).select();
			return false;
		}
	}

	// 中文短信/彩信正文签名
	if ("1102011031" == attrCode || "1102241031" == attrCode
			|| "208011031" == attrCode || "208021031" == attrCode
			|| "208031031" == attrCode) {
		var str = /^[a-zA-Z\u4E00-\uFA29]{1,8}$/;
		if (!str.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过8个字");
			$(e).select();
			return false;
		}
	}

	// 每日下发的最大条数和每月下发的最大条数
	if ("1102011032" == attrCode || "1102011033" == attrCode
			|| "1102241032" == attrCode || "1102241033" == attrCode
			|| "208011032" == attrCode || "208011033" == attrCode
			|| "208021032" == attrCode || "208021033" == attrCode
			|| "208031032" == attrCode || "208031033" == attrCode) {
		var number = /^[0-9]{1,9}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).select();
			return false;
		}
	}

	// 不允许下发开始时间 --不允许下发结束时间
	if ("1102011034" == attrCode || "1102011035" == attrCode
			|| "1102241034" == attrCode || "1102241035" == attrCode
			|| "208011034" == attrCode || "208011035" == attrCode
			|| "208021034" == attrCode || "208021035" == attrCode
			|| "208031034" == attrCode || "208031035" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		// 时间格式：hhmmss
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
	}

	// 备注 100个字节以内
	if ("1102011036" == attrCode || "1102241036" == attrCode
			|| "208011036" == attrCode || "208021036" == attrCode
			|| "208031036" == attrCode) {
		var str = /[^\x00-\xff]/g;
		var valLength = 0;
		for (var ii = 0; ii < newValue.length; ii++) {
			var word = newValue.substring(ii, 1);
			if (str.test(word)) {
				valLength += 2;
			} else {
				valLength++;
			}
		}
		if (valLength > 100) {
			$.showErrorMessage($(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).select();
			return false;
		}
	}

	// EC长服务代码
	if ("1102012009" == attrCode || "208012019" == attrCode
			|| "208022019" == attrCode || "208032019" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须为5位数字");
			$(e).select();
			return false;
		}
	}

	// EC彩信基本接入号
	if ("208011019" == attrCode || "208031019" == attrCode
			|| "208021019" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，同EC短信基本接入号相同!");
			$(e).select();
			return false;
		}

	}

	// 彩信的短信上行点播码
	if ("1102241025" == attrCode || "208011025" == attrCode
			|| "208031025" == attrCode || "208021025" == attrCode) {
		var number = /^[0-9]{7}$/;
		if (!number.test(newValue) || newValue.substring(5, 7) != '01') {
			$.showErrorMessage($(e).attr('desc')
					+ "格式不正确，EC短信基本接入号＋01，即1065022XYABCDE01");
			$(e).select();
			return false;
		}

	}

}

/*************************************行业网关云MAS商品*********************************************/
function IAWGcloudMAS_onValueChange(e, attrCode, oldValue, newValue) {
	/*
	 * 省公司全网长流程局数据1101630011  1101632011省行业网关全网短流程视频短信1101584005省行业网关省内网信视频短信1101614005
	省行业网关省内局数据1101644009 1101644010 端口速率最大值由“2000”调整为“10000”；
	 */
	//短信端口速率（条/秒）
	if("1101630011"==attrCode || "1101632011" == attrCode ||  "1101584005"==attrCode ||"1101614005"==attrCode ||
			"1101644009" == attrCode || "1101644010" == attrCode){
			if (newValue>10000||newValue%5!=0){
				$.showErrorMessage($(e).attr('desc')+"短/彩信端口速率必须是5的倍数且不能超过10000");
				$(e).val("");    
				$(e).select();					
				return false;
			}
		}
	if( "1101574005"==attrCode||"1101594005"==attrCode ||"1101604005"==attrCode ||"1101624005"==attrCode ){
			if (newValue>2000||newValue%5!=0){
				$.showErrorMessage($(e).attr('desc')+"短/彩信端口速率必须是5的倍数且不能超过2000");
				$(e).val("");    
				$(e).select();					
				return false;
			}
		}
	//彩信端口速率（条/秒）
	if("1101632011" == attrCode|| "1101644010" == attrCode)
	{
		if (newValue>500||(newValue)%5!=0)
		{
			$.showErrorMessage($(e).attr('desc')+"彩信端口速率必须是5的倍数且不能超过500");
			$(e).val("");    
			$(e).select();					
			return false;
		}
	}
	if ("1101634025" == attrCode) {
	    if ("是" == newValue) {
	    	 //获取彩信基本接入号
	        var group_id = $.getSrcWindow().$("#GROUP_ID").val(); //从父页面获取CUST_ID 
	        var incodea = "02";
	        var bizTypeCode = "002";
	        $.ajax.submit(this, 'getMasEcCodeListByA', '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + group_id + '&BIZ_TYPE_CODE=' + bizTypeCode, null,
	        function(data) {
	            dealbaseinCodeputAfter(data,"110163");
	        });
	        $('#PARAM_NAME_1101634414').attr("class", "e_required");
	        $('#PARAM_NAME_1101634415').attr("class", "e_required");
	        $('#PARAM_NAME_1101634004').attr("class", "e_required");
	        $('#PARAM_NAME_1101634005').attr("class", "e_required");
	        $('#PARAM_NAME_1101631019').attr("class", "e_required");
	        $('#PARAM_NAME_1101632011').attr("class", "e_required");
	        $('#input_1101634414').attr("nullable", "no");
	        $('#input_1101634415').attr("nullable", "no");
	        $('#input_1101634004').attr("nullable", "no");
	        $('#input_1101634005').attr("nullable", "no");
	        $('#input_1101631019').attr("nullable", "no");
	        $('#input_1101632011').attr("nullable", "no");
	        $('#input_1101634005').attr("disabled", false);
	        $('#input_1101634004').attr("disabled", false);
	        $('#input_1101631019').attr("disabled", false);
	        $('#input_1101632011').attr("disabled", false);
	        $('#input_1101634414').attr("disabled", false);
	        $('#input_1101634415').attr("disabled", false);
	    } else {
	        $('#PARAM_NAME_1101634414').removeClass("e_required");
	        $('#input_1101634414').attr("nullable", "yes");
	        $('#input_1101634414').val("");
	        $('#input_1101634414').change();
	        changeValue($('#input_1101634414')[0]);
	        $('#PARAM_NAME_1101634415').removeClass("e_required");
	        $('#input_1101634415').attr("nullable", "yes");
	        $('#input_1101634415').val("");
	        $('#input_1101634415').change();
	        changeValue($('#input_1101634415')[0]);
	        $('#PARAM_NAME_1101634004').removeClass("e_required");
	        $('#input_1101634004').attr("nullable", "yes");
	        $('#input_1101634004').val("");
	        $('#input_1101634004').change();
	        changeValue($('#input_1101634004')[0]);
	        $('#PARAM_NAME_1101634005').removeClass("e_required");
	        $('#input_1101634005').attr("nullable", "yes");
	        $('#input_1101634005').val("");
	        $('#input_1101634005').change();
	        changeValue($('#input_1101634005')[0]);
	        $('#PARAM_NAME_1101631019').removeClass("e_required");
	        $('#input_1101631019').attr("nullable", "yes");
	        $('#input_1101631019').val("");
	        $('#input_1101631019').change();
	        changeValue($('#input_1101631019')[0]);
	        $('#PARAM_NAME_1101632011').removeClass("e_required");
	        $('#input_1101632011').attr("nullable", "yes");
	        $('#input_1101632011').val("");
	        $('#input_1101632011').change();
	        changeValue($('#input_1101632011')[0]);
	        $('#input_1101634005').attr("disabled", true);
	        $('#input_1101634004').attr("disabled", true);
	        $('#input_1101631019').attr("disabled", true);
	        $('#input_1101632011').attr("disabled", true);
	        $('#input_1101634414').attr("disabled", true);
	        $('#input_1101634415').attr("disabled", true);
	    }
	}
	
	//是否开通视频短信与短信默认ip地址联动1101634027
		//不开通	0    开通	1
		if ("1101634027" == attrCode) {
			if ("1" == newValue||newValue=="是") {
				//主ip地址
				$('#input_1101634417').val("112.33.250.209");
				//备用ip地址
				$('#input_1101634418').val("112.33.254.241");
			} else {
				//主ip地址
				$('#input_1101634417').val("112.35.7.199,112.35.1.159");
				//备用ip地址
				$('#input_1101634418').val("112.35.2.132,112.35.1.160");
			}
			$("#input_1101634417").focus();
			$("#input_1101634417").blur();
			$("#input_1101634418").focus();
			$("#input_1101634418").blur();
			$("#input_1101634027").focus();
		}

	if ("1101644006" == attrCode) {
	    if ("是" == newValue) {
	        //获取彩信基本接入号
	        var group_id = $.getSrcWindow().$("#GROUP_ID").val(); //从父页面获取CUST_ID 
	        var incodea = "02";
	        var bizTypeCode = "002";
	        $.ajax.submit(this, 'getMasEcCodeListByA', '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + group_id + '&BIZ_TYPE_CODE=' + bizTypeCode, null,
	        function(data) {
	            dealbaseinCodeputAfter(data,"110164");
	        });
	        $('#PARAM_NAME_1101644007').attr("class", "e_required");
	        $('#PARAM_NAME_1101644008').attr("class", "e_required");
	        $('#PARAM_NAME_1101644010').attr("class", "e_required");
	        $('#input_1101644007').attr("nullable", "no");
	        $('#input_1101644008').attr("nullable", "no");
	        $('#input_1101644010').attr("nullable", "no");
	        $('#input_1101644007').attr("disabled", false);
	        $('#input_1101644008').attr("disabled", false);
	        $('#input_1101644010').attr("disabled", false);
	    } else {
	        $('#PARAM_NAME_1101644007').removeClass("e_required");
	        $('#input_1101644007').attr("nullable", "yes");
	        $('#input_1101644007').val("");
	        $('#input_1101644007').change();
	        changeValue($('#input_1101644007')[0]);
	        $('#PARAM_NAME_1101644008').removeClass("e_required");
	        $('#input_1101644008').attr("nullable", "yes");
	        $('#input_1101644008').val("");
	        $('#input_1101644008').change();
	        changeValue($('#input_1101644008')[0]);
	        $('#PARAM_NAME_1101644010').removeClass("e_required");
	        $('#input_1101644010').attr("nullable", "yes");
	        $('#input_1101644010').val("");
	        $('#input_1101644010').change();
	        changeValue($('#input_1101644010')[0]);
	        $('#input_1101644007').attr("disabled", true);
	        $('#input_1101644008').attr("disabled", true);
	        $('#input_1101644010').attr("disabled", true);
	    }
	}
	//add by songxw start 省行业网关短流程云MAS
	if ("1101574011" == attrCode) {
		setServCode();//设置服务代码
	    if ("0" == newValue) {//0-黑名单
	    	var servCode = $("#input_1101574002").val();
	    	var servCodeTemp = servCode.substring(0,9);
	    	var bizCode = "MHI4444443";
			if(servCodeTemp == "106509752"){
				bizCode = "MHI4444443";
			}else if(servCodeTemp == "106509122"){
				bizCode = "MHI4444445";
			}else if(servCodeTemp == "106509722"){
				bizCode = "MHI4444447";
			}else{
				bizCode = "MHI4444449";
			}
			$('#input_1101574010').val(bizCode);
			changeValue($('#input_1101574010')[0]);
			
	        //获取彩信基本接入号
	        var group_id = $.getSrcWindow().$("#GROUP_ID").val(); //从父页面获取CUST_ID 
	        var incodea = "02";
	        var bizTypeCode = "002";
	        $.ajax.submit(this, 'getMasEcCodeListByA', '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + group_id + '&BIZ_TYPE_CODE=' + bizTypeCode, null,
	        function(data) {
	            dealbaseinCodeputAfter(data,"110164");
	        });
	    } else {//1-白名单
	    	var servCode = $("#input_1101574002").val();
	    	var servCodeTemp = servCode.substring(0,9);
	    	//var bizCode = "MHI4444441";
	    	var bizCode = "MHI4444442";
	    	/*if(servCodeTemp == "106509652"){
				bizCode = "MHI4444441";
			}else */if(servCodeTemp == "106509022"){
				bizCode = "MHI4444442";
			}else if(servCodeTemp == "106509622"){
				bizCode = "MHI4444446";
			}else{
				bizCode = "MHI4444448";
			}
			$('#input_1101574010').val(bizCode);
			changeValue($('#input_1101574010')[0]);
	    }
	}
	//add by songxw end 省行业网关短流程云MAS
	//add by wangzc7 start 省行业网关短流程云MAS
	if("1101574002"==attrCode || "1101584002"==attrCode){
		if(newValue.indexOf("1065096")!=0&& newValue.indexOf("1065097")!=0&& newValue.indexOf("1065090")!=0
				&& newValue.indexOf("1065091")!=0 ){
			$.showErrorMessage("服务代码不正确，需要以1065096或1065097或1065090或1065091开头");
			$(e).val("");    
			$(e).select();
			return false;
		}
		if(newValue.substring(7, 9)!="22"&& newValue.substring(7, 9)!="52"&& newValue.substring(7, 9)!="92"){
			$.showErrorMessage("服务代码不正确，第8位到第9位应该是22或52或92");
			$(e).val("");    
			$(e).select();
			return false;
		}
		//如果以服务代码为1065096，1065090，1065080开头，系统默认“白名单”；为1065097，1065091，1065081开头，系统默认“黑名单”。不允许修改
		if((newValue.length > 7 && newValue.substring(0, 7)=="1065096")||(newValue.length > 7 && newValue.substring(0, 7)=="1065090")
				) {
			if("1101574002"==attrCode){
				$('#input_1101574011').val("2");
				$('#input_1101574011').change();
				$('#input_1101574011').attr("disabled", true);
			}else if("1101584002"==attrCode){
				$('#input_1101584011').val("2");
				$('#input_1101584011').change();
				$('#input_1101584011').attr("disabled", true);
			}
		} else if((newValue.length > 7 && newValue.substring(0, 7)=="1065097")||(newValue.length > 7 && newValue.substring(0, 7)=="1065091")
				) {
			if("1101574002"==attrCode){
				$('#input_1101574011').val("0");
				$('#input_1101574011').change();
				$('#input_1101574011').attr("disabled", true);
			}else if("1101584002"==attrCode){
				$('#input_1101584011').val("0");
				$('#input_1101584011').change();
				$('#input_1101584011').attr("disabled", true);
			}
		} 
	}

	if("1101594002"==attrCode){
		if(newValue.indexOf("1065096")!=0&&newValue.indexOf("1065090")!=0){
			$.showErrorMessage("彩信基本接入号不正确，需要以1065096或1065090开头");
			$(e).val("");    
			$(e).select();
			return false;
		}
	}
	//add by wangzc7 end 省行业网关短流程云MAS
	
	// 企业代码
	if ("1101630004" == attrCode || "1101544001" == attrCode || "1101554001" == attrCode || "1101564001" == attrCode|| "1101644002" == attrCode
			||"1101604037" == attrCode||"1101614037" == attrCode||"1101624037" == attrCode) {
	    var filter = /^[0-9]{6,6}$/;
	    if (!filter.test(newValue)) {
	        $.showErrorMessage($(e).attr('desc') + "格式不正确，应为6位有效数字");
	        $(e).select();
	        return false;
	    }
	}
	//黑名单选择联动
	if ("1101544008" == attrCode) {
	    if ("0" == newValue) {
	        $('#PARAM_NAME_1101544009').attr("class", "e_required");
	        $('#input_1101544009').attr("nullable", "no");
	    } else {
	        $('#PARAM_NAME_1101544009').removeClass("e_required");
	        $('#input_1101544009').attr("nullable", "yes");
	    }
	}

	if ("1101554008" == attrCode) {
	    if ("0" == newValue) {
	        $('#PARAM_NAME_1101554009').attr("class", "e_required");
	        $('#input_1101554009').attr("nullable", "no");
	    } else {
	        $('#PARAM_NAME_1101554009').removeClass("e_required");
	        $('#input_1101554009').attr("nullable", "yes");
	    }
	}
	if ("1101604011" == attrCode) {
	    if ("0" == newValue) {
	        $('#PARAM_NAME_1101604012').attr("class", "e_required");
	        $('#input_1101604012').attr("nullable", "no");
	    } else {
	        $('#PARAM_NAME_1101604012').removeClass("e_required");
	        $('#input_1101604012').attr("nullable", "yes");
	    }
	}

	if ("1101634026" == attrCode || "1101644013" == attrCode || "1101574031" == attrCode || "1101584031" == attrCode || "1101594031" == attrCode) {
	    if (newValue.length <= 10) {
	        //return true;
	    } else {
	        $.showErrorMessage("网关登陆密码 最长为10位");
	        $(e).select();
	        return false;
	    }
	}

	if ("1101564019" == attrCode || "1101624002" == attrCode || "1101614002" == attrCode || "1101604002" == attrCode || "1101594002" == attrCode || "1101584002" == attrCode || "1101574002" == attrCode || "1101564006" == attrCode || "1101554006" == attrCode || "1101544006" == attrCode || "1101631009" == attrCode|| "1101594019" == attrCode|| "1101624019" == attrCode||"1101644005"==attrCode) {
		//省公司全网长流程局数据、省行业网关省内局数据中“短信基本接入号”最大长度由21位变更为20位；
		if(("1101631009" == attrCode||"1101644005"==attrCode)&&g_IsDigit(newValue) && newValue.length <= 20){
			 //return true;
	    }else{
	    	 $.showErrorMessage("短信基本接入号最长为20位数字");
		        $(e).select();
		        return false;
	    }
		if (g_IsDigit(newValue) && newValue.length <= 21) {
	        //return true;
	    } else {
	        $.showErrorMessage("服务代码、上行点播码、最长为21位数字");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101574005" == attrCode || "1101584005" == attrCode || "1101594005" == attrCode || "1101604005" == attrCode || "1101614005" == attrCode || "1101624005" == attrCode || "1101630011" == attrCode || "1101632011" == attrCode || "1101644009" == attrCode || "1101644010" == attrCode 
			|| "1101544025" == attrCode || "1101544026" == attrCode || "1101554025" == attrCode || "1101554026" == attrCode || "1101564025" == attrCode || "1101564026" == attrCode || "1101574025" == attrCode || "1101574026" == attrCode || "1101584025" == attrCode || "1101584026" == attrCode
			|| "1101594025" == attrCode || "1101594026" == attrCode || "1101604025" == attrCode || "1101604026" == attrCode || "1101614025" == attrCode || "1101614026" == attrCode || "1101624025" == attrCode || "1101624026" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("端口速率、每日最大下发条数、每月下发条数为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544032" == attrCode || "1101554032" == attrCode || "1101564032" == attrCode || "1101574035" == attrCode || "1101584035" == attrCode || "1101594035" == attrCode || "1101604032" == attrCode || "1101614032" == attrCode || "1101624032" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("本网套餐包含条数为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544031" == attrCode || "1101554031" == attrCode || "1101564031" == attrCode || "1101584034" == attrCode || "1101594034" == attrCode || "1101604031" == attrCode || "1101614031" == attrCode || "1101624031" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("本网套餐费（元/月）为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544033" == attrCode || "1101554033" == attrCode || "1101564033" == attrCode || "1101584036" == attrCode || "1101594036" == attrCode || "1101604033" == attrCode || "1101614033" == attrCode || "1101624033" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("本网套餐外资费标准（元/条）为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544035" == attrCode || "1101554035" == attrCode || "1101564035" == attrCode || "1101574038" == attrCode || "1101584038" == attrCode || "1101594038" == attrCode || "1101604035" == attrCode || "1101614035" == attrCode || "1101624035" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("异网套餐包含条数为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544034" == attrCode || "1101554034" == attrCode || "1101564034" == attrCode || "1101584037" == attrCode || "1101594037" == attrCode || "1101604034" == attrCode || "1101614034" == attrCode || "1101624034" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("异网套餐费（元/月）为整数");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544036" == attrCode || "1101554036" == attrCode || "1101564036" == attrCode || "1101584039" == attrCode || "1101594039" == attrCode || "1101604036" == attrCode || "1101614036" == attrCode || "1101624036" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	        $.showErrorMessage("异网套餐外资费标准（元/条）为整数");
	        $(e).select();
	        return false;
	    }
	}

	// 集客部联系电话
	if ("1101634003" == attrCode || "1101634012" == attrCode || "1101634020" == attrCode || "1101574004" == attrCode || "1101574016" == attrCode || "1101574030" == attrCode || "1101584004" == attrCode || "1101584016" == attrCode || "1101584030" == attrCode || "1101594004" == attrCode || "1101594016" == attrCode || "1101594030" == attrCode || "1101604004" == attrCode || "1101604016" == attrCode || "1101614004" == attrCode || "1101614016" == attrCode || "1101624004" == attrCode || "1101624016" == attrCode || "1101644012" == attrCode) {
	    if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
	        $.showErrorMessage($(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
	        $(e).select();
	        return false;
	    }
	}
	
	// 英文短信/彩信正文签名
	if ("1101544005" == attrCode || "1101554005" == attrCode || "1101564005" == attrCode || "1101574009" == attrCode || "1101584009" == attrCode || "1101594009" == attrCode || "1101604009" == attrCode || "1101614009" == attrCode || "1101624009" == attrCode) {
	    var filter = /^[a-zA-Z]{1,16}$/;
	    if (!filter.test(newValue)) {
	        $.showErrorMessage($(e).attr('desc') + "格式不正确,必须为英文!长度不能超过16个字");
	        $(e).select();
	        return false;
	    }
	}

	// 中文短信/彩信正文签名
	if ("1101544004" == attrCode || "1101554004" == attrCode || "1101564004" == attrCode || "1101574008" == attrCode || "1101584008" == attrCode || "1101594008" == attrCode || "1101604008" == attrCode || "1101614008" == attrCode || "1101624008" == attrCode) {
	    var str = /^[a-zA-Z\u4E00-\uFA29]{1,8}$/;
	    if (!str.test(newValue)) {
	        $.showErrorMessage($(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过8个字");
	        $(e).select();
	        return false;
	    }
	}
	
	if ("1101544027" == attrCode || "1101544028" == attrCode || "1101554027" == attrCode || "1101554028" == attrCode || "1101564027" == attrCode || "1101564028" == attrCode || "1101574027" == attrCode || "1101574028" == attrCode
			||"1101584027" == attrCode || "1101584028" == attrCode || "1101594027" == attrCode || "1101594028" == attrCode || "1101604027" == attrCode || "1101604028" == attrCode || "1101614027" == attrCode || "1101614028" == attrCode
			|| "1101624027" == attrCode || "1101624028" == attrCode ) {
		if (!g_IsTime(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).select();
			return false;
		}
		
	}
	//业务代码
	if ("1101544007" == attrCode || "1101554007" == attrCode 
    		|| "1101564007" == attrCode || "1101574010" == attrCode 
    		|| "1101584010" == attrCode || "1101594010" == attrCode 
    		|| "1101604010" == attrCode || "1101614010" == attrCode 
    		|| "1101624010" == attrCode) 
	{
        if (newValue.length > 10) 
        {
        	$.showErrorMessage("业务代码长度不能超过10位");
 	        $(e).select();
            return false;
        }

    }
	
	//端口速率
	if ("1101574005" == attrCode) {
		if(parseInt(newValue)<1){
	        errinfo="短信端口速率不能小于1!";
	        $.showErrorMessage($(e).attr('desc') + errinfo);
			$(e).val("");
			$(e).select();
    	}
    	// var custId = $.getSrcWindow().$("#CUST_ID").val();
		var group_id = $.getSrcWindow().$("#GROUP_ID").val();
		var serv_level = "";
		// 校验目的地
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
		'qryGrpByGroupId', '&GROUP_ID=' + group_id, function(d) {
			var param = d.map.result;
			serv_level = param.get(0, 'SERV_LEVEL');
			if (serv_level==null||serv_level==undefined) {
				$.showErrorMessage($(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
				$(e).val("");
				$(e).select();
			}

		}, function(e, i) {
			$.showErrorMessage($(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
			$(e).val("");
			$(e).select();
		}, {
			async : false
		});
	    if (serv_level=="1") //金牌级：<=2000
	    {
	    	if(parseInt(newValue)>2000){
		        errinfo="金牌级：最大短信端口速率不能超过2000!";
		        $.showErrorMessage($(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
	    	}
	    }
		if (serv_level=="3"||serv_level=="2") //铜牌、银牌级：<=100
		{
			if(parseInt(newValue)>100){
			    errinfo="铜牌、银牌级：最大短信端口速率不能超过100!";
			    $.showErrorMessage($(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
		if (serv_level=="4"||serv_level=="") //标准级：<=20
		{
			if(parseInt(newValue)>20){
			    errinfo="标准级：最大短信端口速率不能超过20!";
			    $.showErrorMessage($(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
	}
	checkBizCode(e,attrCode, newValue);
}


//业务类型变更同步变更业务编码
function biz_type_onValueChange(e,attrCode,oldValue,newValue){
	//110154	1101548801	业务类型  1101544007	业务代码
	if ("1101548801" == attrCode) {
		var biz_code = $('#input_1101544007').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101544007').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101544007').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101544007').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101544007').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101544007')[0]);
	}
	//110155	1101558801	业务类型  1101554007	业务代码
	if ("1101558801" == attrCode) {
		var biz_code = $('#input_1101554007').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101554007').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101554007').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101554007').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101554007').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101554007')[0]);
	}
	//110156	1101568801	业务类型  1101564007	业务代码
	if ("1101568801" == attrCode) {
		var biz_code = $('#input_1101564007').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101564007').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101564007').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101564007').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101564007').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101564007')[0]);
	}
	//110157	1101578801	业务类型  1101574010	业务代码
	if ("1101578801" == attrCode) {
		var biz_code = $('#input_1101574010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101574010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101574010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101574010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101574010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101574010')[0]);
	}
	//110158	1101588801	业务类型  1101584010	业务代码
	if ("1101588801" == attrCode) {
		var biz_code = $('#input_1101584010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101584010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101584010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101584010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101584010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101584010')[0]);
	}
	//110159	1101598801	业务类型  1101594010	业务代码 
	if ("1101598801" == attrCode) {
		var biz_code = $('#input_1101594010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101594010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101594010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101594010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101594010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101594010')[0]);
	} 
	//110160	1101608801	业务类型  1101604010	业务代码 
	if ("1101608801" == attrCode) {
		var biz_code = $('#input_1101604010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101604010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101604010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101604010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101604010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101604010')[0]);
	} 
	//110161	1101618801	业务类型  1101614010	业务代码
	if ("1101618801" == attrCode) {
		var biz_code = $('#input_1101614010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101614010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101614010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101614010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101614010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101614010')[0]);
	} 
	//110162	1101628801	业务类型  1101624010	业务代码
	if ("1101628801" == attrCode) {
		var biz_code = $('#input_1101624010').val();
		if ("内部管理类" == newValue) {//QHH2412301
			$('#input_1101624010').val(biz_code.substr(0,6)+"51"+biz_code.substr(8));
		} else if ("外部服务类" == newValue){
			$('#input_1101624010').val(biz_code.substr(0,6)+"52"+biz_code.substr(8));
		} else if ("营销推广类" == newValue){
			$('#input_1101624010').val(biz_code.substr(0,6)+"53"+biz_code.substr(8));
		}else if ("公益类" == newValue){
			$('#input_1101624010').val(biz_code.substr(0,6)+"54"+biz_code.substr(8));
		}
		changeValue($('#input_1101624010')[0]);
	} 

}

//校验业务代码
function checkBizCode(e,attrCode, newValue) {

    if ("1101544007" == attrCode || "1101554007" == attrCode || "1101564007" == attrCode || "1101574010" == attrCode || "1101584010" == attrCode || "1101594010" == attrCode || "1101604010" == attrCode || "1101614010" == attrCode || "1101624010" == attrCode) {
        if (newValue.length != 10) {
        	$.showErrorMessage("业务代码长度必须为10位");
 	        $(e).select();
            return false;
        }

    }

    if ("1101544007" == attrCode || "1101554007" == attrCode || "1101574010" == attrCode || "1101584010" == attrCode || "1101604010" == attrCode || "1101614010" == attrCode) {
        //校验业务代码 必须为字母数字的组合
        var masreg = /[\W]/g;
        var masrst = newValue.match(masreg); // 在字符串 s 中查找匹配。
        if (masrst != null) {
        	$.showErrorMessage("业务代码只能为字母或者数字,或者字母和数字的组合!");
 	        $(e).select();
            return false;
        }

    } else if ("1101564007" == attrCode || "1101594010" == attrCode || "1101624010" == attrCode) {
        var numberre = /[^0-9]/g;
        var bizcodecheck = newValue.match(numberre); // 在字符串 s 中查找匹配。
        if (bizcodecheck != null) {
        	$.showErrorMessage("彩信业务的业务代码必须为全数字!");
 	        $(e).select();
            return false;
        }

    }
    return true;
}


/* ****************************************双跨融合商品******************************************** */
/*
 * 双跨融合商品--属性变更校验
 */
function dbCroFus_onValueChange(e, attrCode, oldValue, newValue) {
	// 集团管理员手机号码
	if ("200011801" == attrCode || "200021801" == attrCode
			|| "200031801" == attrCode || "200041801" == attrCode
			|| "200051801" == attrCode || "200061801" == attrCode
			|| "200071801" == attrCode||"200091801"==attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}

	// 集团管理员邮箱
	if ("200011802" == attrCode || "200021802" == attrCode
			|| "200031802" == attrCode || "200041802" == attrCode
			|| "200051802" == attrCode || "200061802" == attrCode
			|| "200071802" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}

	// 最大成员数量
	if ("200011804" == attrCode || "200021804" == attrCode
			|| "200031804" == attrCode || "200041804" == attrCode
			|| "200051804" == attrCode || "200061804" == attrCode
			|| "200071804" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 20) {
			$.showErrorMessage($(e).attr('desc') + "长度超出最大长度[20位]设置");
			$(e).select();
			return false;
		}
	}

	// 固话功能费、手机功能费
	if ("200021871" == attrCode || "200021872" == attrCode
			|| "200051910" == attrCode || "200051911" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
	}

	// 固话套餐折扣率
	if ("200011851" == attrCode || "200041897" == attrCode
			|| "200061934" == attrCode || "200071934" == attrCode) {
		if (!g_IsDigitWithPoint(newValue, 2)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确或者小数点后的位数不正，详情请参考备注信息");
			$(e).select();
			return false;
		}
	}

	// 配合省
	if ("200041891" == attrCode) {
		selectChange(e, attrCode, '200041892', newValue);
	}
	return true;
}

/* **************************************139企业邮箱（商品）**************************************** */
/*
 * 139邮箱（产品）--属性变更校验
 */
function mail139_onValueChange(e, attrCode, oldValue, newValue) {
	// 联系人邮箱
	if ("30102010003" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}
	// 手机号码
	if ("30102010004" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).select();
			return false;
		}
	}
}

/* **************************************爱流量统付（商品）**************************************** */
/*
 * 爱流量统付（产品）--属性变更校验
 */
function loveflow_onValueChange(e, attrCode, oldValue, newValue) {
	// 联系人邮箱
	if ("90012014004" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确！");
			$(e).select();
			return false;
		}
		return true;
	}
	// 手机号码
	if ("90012014002" == attrCode || "90012014006" == attrCode ||  "90012014010" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).select();
			return false;
		}
	}
}

/* ****************************************千里眼商品属性较验******************************************** */

/* author: songxw
 * 千里眼商品--属性校验
 */
function checkKeye(e, attrCode, oldValue, newValue) {

	// 手机号码
	if ("50008010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}

	// 数字验证
	if ("50008010005" == attrCode || "50008010007" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为数字");
			$(e).select();
			return false;
		}
	}
	
	//邮箱验证
	if ("50008010003" == attrCode) {
		if (!g_ismail(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	
	//使用云存储路数验证 云存储套餐为“0”时，路数必需为“0”；其它值时系统校验，云存储路数不能超过总路数且不为0；手工录入，计费时使用
	if ("50008010007" == attrCode) {
		var val50008010006 = $("#50008010006").val();
		var val50008010005 = $("#50008010005").val();
		if(val50008010006 == "0"){
			if(newValue != 0){
				$.showErrorMessage($(e).attr('desc') + "云存储套餐为“0”时，路数必需为“0”！");
				$(e).select();
				return false;
			}
		}else{
			if (parseInt(val50008010005) < newValue) {
				$.showErrorMessage($(e).attr('desc') + "云存储路数不能超过总路数且不为0！");
				$(e).select();
				return false;
			}
		}
	}

}

/* ****************************************和对讲商品属性较验******************************************** */
/* author: songxw
 * 和对讲商品--属性校验
 */
function hdj_onValueChange(e, attrCode, oldValue, newValue) {
	// 成员套餐费销售折扣（%）
	if ("91011010007" == attrCode) {
		if (parseInt(newValue)<70) {
			$.showErrorMessage($(e).attr('desc') + "成员套餐销售折扣最低7折!");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，必须要为数字");
			$(e).select();
			return false;
		}
	}
}

/* ****************************************基础校验方法******************************************** */
/*
 * 校验是否为数字类型
 */
function g_IsDigit(s) {
	if (s == null) {
		return false;
	}
	if (s == '') {
		return true;
	}
	s = '' + s;
	if (s.substring(0, 1) == '-' && s.length > 1) {
		s = s.substring(1, s.length);
	}
	var patrn = /^[0-9]*$/;
	if (!patrn.exec(s)) {
		return false;
	}
	return true
}

/*
 * 校验需要带小数点的数字
 */
function g_IsDigitWithPoint(s, pointLength) {
	if (s == null) {
		return false;
	}
	if (s == '') {
		return true;
	}
	s = '' + s;
	var attrValues = s.split(".");
	if (attrValues.size() == 1) {
		return g_IsDigit(s);
	} else if (attrValues.size() == 2) {
		var integerResult = g_IsDigit(attrValues[0]);
		if (integerResult == false) {
			return false;
		} else if (attrValues[1].toString().length > pointLength) {
			alert("小数点后的位数超出设置的长度" + pointLength);
			return false;
		} else {
			return g_IsDigit(attrValues[1]);
		}
	} else {
		return false;
	}
}

/*
 * 校验是否为手机号码
 */
function g_IsMobileNumber(s) {
	if (s == null || s == '') {
		return true;
	}
	var result = false;
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
			'verifyIsMobileNumber', '&MOBILENUMBER=' + s, function(d) {
		    if(d.get("ISMOBILENUMBER")=="true")
		{
				result=true;
			}
			else
		{
				result=false;
			}


			}, function(e, i) {
				$.showErrorMessage("操作失败");
				result = false;
			}, {
				async : false
			});
	if (!g_IsDigit(s)) {
		return false;
	}
	return result
}

/*
 * 校验是否为时间格式：hhmmss
 */
function g_IsTime(s) {
	var zz = /^(0[0-9]|1[0-9]|2[0-3]){1}(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9]){2}$/;
	if (!zz.exec(s)) {
		return false;
	}
	return true
}

// 校验电话号码
// 兼容格式：区号（3-4）-电话号码(7到8位)
function g_IsTelephoneNumber(s) {
	var zz = /^(0\d{2,3}-)?(\d{7,8})$/;
	if (!zz.exec(s)) {
		return false;
	}
	return true;
}

// 一般情况下，当属性值为空时不进行校验，但某些情况比较特殊，即使为空也需要校验
function isNotInTheseCodes(attrCode) {
	if (attrCode == "1112053311" || attrCode == "1112053320"
			|| attrCode == "301013311" || attrCode == "4115027012") {
		return false;
	}
	return true;
}

// 邮箱格式
function g_ismail(s) {
	// 正则表达式验证邮箱格式
	var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (!filter.test(s)) {

		return false;
	}
	return true;
}

// 下拉联动公用方法
function selectChange(e, attrCode, cityCode, newValue) {
	// 如果省份是请选择的情况下，则城市对应的组件设置成不可编辑的状态，否则可编辑
	if (newValue == '' || newValue == null) {
		$('#input_' + cityCode).attr('value', '');
		changeValue($('#input_' + cityCode)[0]);
		$('#input_' + cityCode).attr('disabled', true);
		return true;
	} else {
		$('#input_' + cityCode).attr('disabled', false);
	}

	// 根据选择的省公司获取该省公司对应的城市
	Wade.httphandler
			.submit(
					'',
					'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
					'chooseCitys',
					'&PROVINCE_ATTR_CODE=' + attrCode + '&PROVINCE_ATTR_VALUE='
							+ newValue + '&CITY_ATTR_CODE=' + cityCode,
					function(d) {
						var rowData = $.table.get("productParamTable")
								.getRowData("FLAG");
						var _index = rowData.get("FLAG") - 1;
						var city = $("select[id='input_" + cityCode + "']");
						// 拼option对象
						var innerObj = "<OPTION title=--请选择-- selected value=''>--请选择--</OPTION>";

						var citys = d.map.result;
						for (var i = 0; i < citys.length; i++) {
							innerObj = innerObj + "<OPTION title="
									+ citys.get(i, 'OPTION_NAME') + " value="
									+ citys.get(i, 'OPTION_VALUE') + ">"
									+ citys.get(i, 'OPTION_NAME') + "</OPTION>";
						}

						// 新option对象替换老对象
						if (-1 != _index) {
							$(city[_index]).html(innerObj)
							changeValue(city[_index])
						} else {
							$('#input_' + cityCode).attr('value', '');
							changeValue($('#input_' + cityCode)[0]);
							$('#input_' + cityCode)[0].innerHTML = "";
							$('#input_' + cityCode).html(innerObj);
						}
					}, function(e, i) {
						$.showErrorMessage("操作失败");
						$(e).select();
						return false;
					});
}

function byteLength(str) {
	var byteLen = 0, len = str.length;
	if (!str)
		return 0;
	for (var i = 0; i < len; i++)
		byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
	return byteLen;
}


//------集团短彩信XYZ资费套餐需求验证以下部分暂时写在这里 下个省优化会搬动以下JS(liuxx3 2014 10-11)------------

/**
 * 作用:选择XYZ资费短信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthParam(){
	var xyzCost = $("#30011118").val();
	var xyzItem = $("#30011116").val();
	var xyzExcessCost = $("#30011117").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 200)
	{
		if(Number(xyzItem) < 3000)
		{
			var xyzWithin = Number(xyzCost)/Number(xyzItem);
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需少于3000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>200||Number(xyzCost)==200)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>3000||Number(xyzItem)==3000)&& Number(xyzItem)<16000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.07){
				MessageBox.alert("","您输入的超出费用不得低于0.07元/条 输入有误 请重新输入！");
				return false;
			}
			return true;

		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于3000条小于16000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>16000||Number(xyzItem)==16000)&& Number(xyzItem)<50000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于16000条小于50000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>50000||Number(xyzItem)==50000)&& Number(xyzItem)<100000){
			if(xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于50000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>5500||Number(xyzCost)==5500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)){
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条 输入有误 请重新输入！");
			return false;
		}
	}
}


/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZMessageYearParam(){
	var xyzCost = $("#30011108").val();
	var xyzItem = $("#30011106").val();
	var xyzExcessCost = $("#30011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageMonthSpecialParam(){

	var xyzCost = $("#30011338").val();
	var xyzItem = $("#30011336").val();
	var xyzExcessCost = $("#30011337").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZMessageYearSpecialParam(){

	var xyzCost = $("#30011328").val();
	var xyzItem = $("#30011326").val();
	var xyzExcessCost = $("#30011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}


/**
 * 作用:选择XYZ资费彩信包月套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthParam(){
	var xyzCost = $("#30011138").val();
	var xyzItem = $("#30011136").val();
	var xyzExcessCost = $("#30011137").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<50))
	{
		MessageBox.alert("","您输入的功能费金额不得低于50元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>50||Number(xyzCost)==50)&& Number(xyzCost) < 400)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem) < 2000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.3){
				MessageBox.alert("","您输入的超出费用不得低于0.3元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
				MessageBox.alert("","您输入的赠送条数需小于2000条 输入有误 请重新输入！");
				return false;
		}

	}

	if((Number(xyzCost)>400||Number(xyzCost)==400)&& Number(xyzCost) < 1000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>2000||Number(xyzItem)==2000)&& Number(xyzItem)<5000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于2000条小于5000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 3000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<15000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于15000条 输入有误 请重新输入！");
			return false;
		}
	}

	if((Number(xyzCost)>3000||Number(xyzCost)==3000)&& Number(xyzCost) < 5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>15000||Number(xyzItem)==15000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于15000条小于25000条 输入有误 请重新输入！");
			return false;
		}
	}

	if(Number(xyzCost)>5000||Number(xyzCost)==5000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>25000||Number(xyzItem)==25000)
		{
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条 输入有误 请重新输入！");
			return false;
		}

	}
}


/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkXYZ_MMSYearParam(){
	var xyzCost = $("#30011128").val();
	var xyzItem = $("#30011126").val();
	var xyzExcessCost = $("#30011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包月特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSMonthSpecialParam(){

	var xyzCost = $("#30011238").val();
	var xyzItem = $("#30011236").val();
	var xyzExcessCost = $("#30011237").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkXYZ_MMSYearSpecialParam(){

	var xyzCost = $("#30011228").val();
	var xyzItem = $("#30011226").val();
	var xyzExcessCost = $("#30011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费短信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZMessageYearParam(){
	var xyzCost = $("#40011108").val();
	var xyzItem = $("#40011106").val();
	var xyzExcessCost = $("#40011107").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<600))
	{
		MessageBox.alert("","您输入的功能费金额不得低于600元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>600||Number(xyzCost)==600)&& Number(xyzCost) < 1500)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>10000||Number(xyzItem)==10000)&& Number(xyzItem)<25000){
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.08){
				MessageBox.alert("","您输入的超出费用不得低于0.08元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于10000条小于25000条 输入有误 请重新输入！");
			return false;
		}


	}

	if((Number(xyzCost)>1500||Number(xyzCost)==1500)&& Number(xyzCost) < 6000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>25000||Number(xyzItem)==25000)&& Number(xyzItem)<100000)
		{
			if(xyzWithin < 0.06)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.065){
				MessageBox.alert("","您输入的超出费用不得低于0.065元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于25000条小于100000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>6000||Number(xyzCost)==6000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>100000||Number(xyzItem)==100000)&& Number(xyzItem)<200000)
		{
			if( xyzWithin < 0.055)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.06){
				MessageBox.alert("","您输入的超出费用不得低于0.06元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于100000条小于200000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>200000||Number(xyzItem)==200000)
		{
			if(xyzWithin < 0.05)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.055){
				MessageBox.alert("","您输入的超出费用不得低于0.055元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于200000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费短信包年特殊套餐ICB参数验证
 * liuxx3--2016 --01 --09
 */
function checkNewXYZMessageYearSpecialParam(){

	var xyzCost = $("#40011328").val();
	var xyzItem = $("#40011326").val();
	var xyzExcessCost = $("#40011327").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.05)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.05元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.05)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.05元 输入有误 请重新输入！");
		return false;
	}
	return true;
}

/**
 * 作用:选择XYZ资费彩信包年套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */

function checkNewXYZ_MMSYearParam(){
	var xyzCost = $("#40011128").val();
	var xyzItem = $("#40011126").val();
	var xyzExcessCost = $("#40011127").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	if((Number(xyzCost)<1000))
	{
		MessageBox.alert("","您输入的功能费金额不得低于1000元 输入有误 请重新输入！");
		return false;
	}

	if((Number(xyzCost)>1000||Number(xyzCost)==1000)&& Number(xyzCost) < 4000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>5000||Number(xyzItem)==5000)&& Number(xyzItem)<20000){
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.28){
				MessageBox.alert("","您输入的超出费用不得低于0.28元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于5000条小于20000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>4000||Number(xyzCost)==4000)&& Number(xyzCost) < 8000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>20000||Number(xyzItem)==20000)&& Number(xyzItem)<40000)
		{
			if(xyzWithin < 0.2)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.25){
				MessageBox.alert("","您输入的超出费用不得低于0.25元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else
		{
			MessageBox.alert("","您输入的赠送条数需大于等于20000条小于40000条 输入有误 请重新输入！");
			return false;
		}

	}

	if((Number(xyzCost)>8000||Number(xyzCost)==8000)&& Number(xyzCost) < 11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if((Number(xyzItem)>40000||Number(xyzItem)==40000)&& Number(xyzItem)<60000){
			if(xyzWithin < 0.18)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.18元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.22){
				MessageBox.alert("","您输入的超出费用不得低于0.22元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于40000条小于60000条 输入有误 请重新输入！");
			return false;
		}

	}

	if(Number(xyzCost)>11000||Number(xyzCost)==11000)
	{
		var xyzWithin = Number(xyzCost)/Number(xyzItem);
		if(Number(xyzItem)>60000||Number(xyzItem)==60000){
			if(xyzWithin < 0.15)
			{
				MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
				return false;
			}
			if(Number(xyzExcessCost)<0.2){
				MessageBox.alert("","您输入的超出费用不得低于0.2元/条 输入有误 请重新输入！");
				return false;
			}
			return true;
		}
		else{
			MessageBox.alert("","您输入的赠送条数需大于等于60000条 输入有误 请重新输入！");
			return false;
		}
	}
}

/**
 * 作用:选择XYZ资费彩信包年特殊套餐ICB参数验证
 * liuxx3--2014 --10 --09
 */
function checkNewXYZ_MMSYearSpecialParam(){

	var xyzCost = $("#40011228").val();
	var xyzItem = $("#40011226").val();
	var xyzExcessCost = $("#40011227").val();

	var patrn = /(\d)+/;
	if (!patrn.test(xyzCost)){
		MessageBox.alert("","您输入的功能费金额含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzItem)){
		MessageBox.alert("","您输入的赠送条数含有非数字字符 请重新输入！");
		return false;
	}
	if (!patrn.test(xyzExcessCost)){
		MessageBox.alert("","您输入的超出费用含有非数字字符 请重新输入！");
		return false;
	}

	var xyzWithin = Number(xyzCost)/Number(xyzItem);

	if(xyzWithin < 0.15)
	{
		MessageBox.alert("","您输入的功能费用与赠送条数价格需高于0.15元/条 输入有误 请重新输入！");
		return false;
	}
	if(Number(xyzExcessCost)<0.15)
	{
		MessageBox.alert("","您输入的超出费用不得低于0.15元 输入有误 请重新输入！");
		return false;
	}
	return true;
}
//-------------------------集团短彩信XYZ资费套餐需求END(liuxx3 2014-10-11)------------------------------------

/**
 * @param null
 * @returns void 设置省行业网关短流程云MAS服务代码
 */
function setServCode(){
  // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接退出
  var productOpType = $("#productOperType").val();
  if(productOpType != "1" && productOpType != "10"){
	  return false;
  }
  // 1- 获取当前的产品编号(全网产品编号)
  var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
  var allNetProductId = productParamInfoList.get(0,"PRODUCT_ID");
  var typecode = $("#input_1101574011").val();
  var servCode = getRandom(typecode);
  $("#input_1101574002").val(servCode);
  changeValue($('#input_1101574002')[0]);
}

/**
 * @param null
 * @returns {String} 省行业网关短流程云MAS服务代码固定5位+3位随机数
 */
function getRandom(typecode){
  var rand = "";
  if(typecode == "0"){//黑名单
	  for(var i = 0; i < 3; i++){
	      var r = Math.floor(Math.random() * 10);
	      rand += r;
	  }
	  var arr = ["106509752","106509122","106509722","106509152"];
  }else{//白名单
	  //var arr = ["106509652","106509022","106509622","106509052"];
	  for(var i = 0; i < 6; i++){
	      var r = Math.floor(Math.random() * 10);
	      rand += r;
	  }
	  var arr = ["106509022","106509622","106509052"];
  }
  var index = Math.floor((Math.random()*arr.length));
  
  var servCode = arr[index]+rand;
  $.ajax.submit(this, 'getMasServCodeByParam', '&SERV_CODE=' + servCode, null,
  function(data){
	    if(data.result == "1"){
	    	getRandom(typecode);
	    }
  });
  return servCode;
}

/*************************CDN业务 产品属性-域名校验******************************************/
function domainNameCheck_onValueChange(e,attrCode,oldValue,newValue){
	//CDN业务产品属性 “试用业务对应域名 ” 规则校验 
	if("50004010022"==attrCode){
		if(newValue.isEmpty()){
			$.showErrorMessage("域名不能为空!");
			$(e).select();
			return false;
		}
		//1. 域名最小长度为3，最大长度为255
		if(newValue.length<3||newValue.length>255){
			$.showErrorMessage("域名最小长度为3，最大长度为255!");
			$(e).select();
			return false;
		}
		
		// 正则表达式
		var filter = /^[0-9]*$/;
		var filter2 = /^[A-Za-z0-9\-]+$/;
		
		//2. 每段以点号(".")分隔组成完整域名
		var domainList = newValue.split(";");
		for(var i=0;i<domainList.length;i++){
			var part = domainList[i].split(".");
			for(var j=0;j<part.length;j++){
				//3. 每段由字母、数字、"-"(连接符)组成，开头及结尾不能为"-",每段最大长度为63
				if(!filter2.test(part[j])){
					$.showErrorMessage("域名的每段由字母、数字、'-'(连接符)组成，不能含有其他字符!");
					$(e).select();
					return false;
				}
				var index = part[j].indexOf("-");
				if(0==index || part[j].toString().length-1 == index){
					$.showErrorMessage("格式不正确，域名的每段的开头及结尾不能是  '-'(连接符)！");
					$(e).select();
					return false;
				}
				if(part[j].toString().length>63){
					$.showErrorMessage("格式不正确，域名的每段长度不能大于 63！");
					$(e).select();
					return false;
				}
				
				//4. 最后一段不能为纯数字 
				if(filter.test(part[part.length-1])){
					$.showErrorMessage("格式不正确，域名的最后一段不能为纯数字(或空)!");
					$(e).select();
					return false;
				}
			}
		}
	}
}


/** *************************************短流程云MAS商品**************************************** */
function shortFlowCloudMAS(e, attrCode, oldValue, newValue) {
	// 短信基本接入号
	if ("1101491009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		//if (newValue.indexOf("1065098") != 0
		//		&& newValue.indexOf("1065099") != 0) {
		//	$.validate.alerter.one($(e)[0], "短信基本接入号不正确，需要以1065098或1065099开头");
		//	$(e).val("");
		//	$(e).select();
		//	return false;
		//}
		//REQ201910110008【集团总部需求】关于更新总部行业网关云MAS支撑实施方案的通知（没写完）
		if(newValue.indexOf("1065085") == 0 || newValue.indexOf("10650862") == 0 || newValue.indexOf("10693362") == 0){
			if(newValue.length < 10){
				$.showErrorMessage("短信基本接入号不正确，省公司订购校验：1065085/10650862/10693362开头，最短10位.");
				$(e).select();
				return false;
			}
		}else if (newValue.indexOf("1069") == 0 || newValue.indexOf("10650861") == 0) {
			if(newValue.length < 11){
				$.showErrorMessage("短信基本接入号不正确，省公司订购校验：1069/10650861开头，最短11位.");
				$(e).select();
				return false;
			}
		}else {
			$.showErrorMessage("短信基本接入号不正确，1069/10650861开头，最短11位,1065085/10650862/10693362，最短10位.");
			$(e).select();
			return false;
		}
	}

	// MMS基本接入号
	if ("1101491019" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		//if (newValue.indexOf("1065098") != 0) {
		//	$.validate.alerter.one($(e)[0], "MMS基本接入号不正确，需要以1065098开头");
		//	$(e).val("");
		//	$(e).select();
		//	return false;
		//}
		if(newValue.indexOf("10693362") == 0){
			if(newValue.length < 10){
				$.showErrorMessage("MMS基本接入号不正确,省公司订购校验：10693362开头，最短10位.");
				$(e).select();
				return false;
			}
		}else if (newValue.indexOf("1069") == 0) {
			if(newValue.length < 11){
				$.showErrorMessage("MMS基本接入号不正确,省公司订购校验：1069开头，最短11位.");
				$(e).select();
				return false;
			}
		}else {
			$.showErrorMessage("MMS基本接入号不正确，1069开头，最短11位,10693362，最短10位.");
			$(e).select();
			return false;
		}
	}
	
	// 每月下发的最大条数(0代表不限制) --每日下发的最大条数(0代表不限制) --彩信端口速率(条/秒)--短信端口速率(条/秒)
	if ("1101492011" == attrCode || "1101490011" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("请确保所有字符都为有效数字.");
			$(e).select();
			return false;
		}
		
		//REQ201910110008【集团总部需求】关于更新总部行业网关云MAS支撑实施方案的通知
		if ("1101492011" == attrCode || "1101490011" == attrCode) {
			if(Number(newValue)>10000){
				$.showErrorMessage("端口速率最大值10000.");
				$(e).select();
				return false;
			}
		}
		
	}
}

/** *************************************长流程云MAS（商品）**************************************** */
function longCloudMAS_onValueChange(e, attrCode, oldValue, newValue) {
	// 企业代码
	if ("1101450004" == attrCode) {
		var filter = /^[0-9]{6,6}$/;
		if (!filter.test(newValue)) {
			$.showErrorMessage($(e).attr('desc') + "格式不正确，应为6位有效数字");
			$(e).select();
			return false;
		}
	}
	
	//REQ201910110008【集团总部需求】关于更新总部行业网关云MAS支撑实施方案的通知
	if ("1101452011" == attrCode || "1101450011" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.showErrorMessage("请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}
		if ("1101452011" == attrCode || "1101450011" == attrCode) {
			if(Number(newValue)>10000){
				$.showErrorMessage("端口速率最大值10000.");
				$(e).select();
				return false;
			}
		}
	}
	
}

