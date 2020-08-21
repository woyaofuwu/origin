//xunyl 创建  此文件专门用于集团BBOSS产品的属性变更时校验
//modify by chenkh 修改为CRM 6.X 兼容版本
	 	
/** ****************************************统一接口************************************ */
/*
 * 集团BBOSS产品的属性变更统一接口
 */
function onValueChangeUnit(e) {
	// 由于新系统统一页面，所以针对特殊组件得特殊处理。
	if (e.el != null && e.el != undefined
			&& e.el.getAttribute("element_id") != null
			&& e.el.getAttribute("element_id") != undefined) {
		e = $("#" + e.el.getAttribute("name"));
		
		var methodName = $(e).attr("changeMethod");
		// 判断方法名是否存在，如果不存在则直接返回
		if (methodName == null || methodName == "undefined") {
			return true;
		}
		// 获取当前变更的属性编号
		var paramCode = $(e).attr("element_id");
		// 获取当前属性的属性值
		var attrValue = e.val();
		// 参数值为空的时候，不进行校验
		if ((attrValue == null || attrValue == "") && isNotInTheseCodes(paramCode)) {
			return true;
		}
		// 调用相应的js方法，将属性编号,变更后的值传入js方法中
		var changeFunc = eval(methodName);
		
		return new changeFunc(e, paramCode, attrValue);
	}

	// 常规属性————获取调用的js方法名
	var methodName = $(e).attr("changeMethod");
	// 判断方法名是否存在，如果不存在则直接返回
	if (methodName == null || methodName == "undefined") {
		return true;
	}
	// 获取当前变更的属性编号
	var paramCode = $(e).attr("element_id");
	// 获取当前属性的属性值
	var attrValue = e.value;
	// 参数值为空的时候，不进行校验
	if ((attrValue == null || attrValue == "") && isNotInTheseCodes(paramCode)) {
		return true;
	}
	// 调用相应的js方法，将属性编号,变更后的值传入js方法中
	var changeFunc = eval(methodName);

	return new changeFunc(e, paramCode, attrValue);
}
/* *****************************国际流量统付商品********************************** */
function intlFlow_onValueChange(e, attrCode, newValue) {
	
	if ("999104070" == attrCode ||"999104071" == attrCode) {	
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱格式");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
		
	}
	if ("999104016" == attrCode ) {
		if (newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "最多8个汉字或者字母");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("999104005" == attrCode ||"999104003" == attrCode ||"999104056" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/* *****************************物联网专网专号物联通商品********************************** */
function thingsOfweb_onValueChange(e, attrCode, newValue) {

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
			$('#input_B300074019').val('1');
		} else {
			$('#input_B300074019').val('2');
		}
	}

	if ("300074038" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "中文签名8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300074039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300074013" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300074052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], "对应省开卡数量，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

}

function thingsOfwebGPRS_onValueChange(e, attrCode, newValue) {

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
            $.validate.alerter.one($(e)[0], "中文签名8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300084039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300084007" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	if ("300084052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], "对应省开卡数量，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

/* *****************************物联网专网专号机器卡商品********************************** */
function machineCardOfweb_onValueChange(e, attrCode, newValue) {

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
			$('#input_B300054019').val('1');
		} else {
			$('#input_B300054019').val('2');
		}
	}

	if ("300054038" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "中文签名8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300054039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300054013" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300054052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], "对应省开卡数量，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

}

function machineCardGPRS_onValueChange(e, attrCode, newValue) {

	// 是否专用APN 联动下拉
	if ("300064024" == attrCode) {
		selectChange(e, attrCode, '300064014', newValue);
	}

	// 子产品名称 联动下拉
	if ("300064014" == attrCode) {
		selectChange(e, attrCode, '300064015', newValue);

//		Wade.httphandler.submit('',
//				'com.ailk.csview.group.verifyClass.frontDataVerify',
//				'getPbssBizProdInstId', '', function(d) {
//					var subs_id = d.map.result;
//					$('#input_B300064016').val(subs_id);
//
//				});
//
//		Wade.httphandler.submit('',
//				'com.ailk.csview.group.verifyClass.frontDataVerify',
//				'geneSubsId', '', function(d) {
//					var subs_id = d.map.result;
//					$('#input_B300064023').val(subs_id);
//
//				});

	}
	// 子产品类别和产品包类别 联动下拉
	if ("300064015" == attrCode) {
		selectChange(e, attrCode, '300064018', newValue);
	}
	// 获取产品订购关系 主办省保证唯一，并且保证与本省的一点出卡物联网专网专号业务订购关系ID不重复
	if ("300064016" == attrCode) {
		Wade.httphandler.submit('',
				'com.ailk.csview.group.verifyClass.frontDataVerify',
				'getPbssBizProdInstId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_B300064016').val(subs_id);

				});
	}
	// 用户标识 系统自动生成流水号
	if ("300064023" == attrCode) {

		Wade.httphandler.submit('',
				'com.ailk.csview.group.verifyClass.frontDataVerify',
				'geneSubsId', '', function(d) {
					var subs_id = d.map.result;
					$('#input_B300064023').val(subs_id);

				});
	}

	if ("300061008" == attrCode) {
		// 校验签名长度是否符合规范，客户签名4-8个汉字，或4-8个英文字母
		if (newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "中文签名8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("300064039" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 手机号码
	if ("300064007" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	if ("300064052" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], "对应省开卡数量，请确保所有字符都为有效数字");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

function heDuiJiang_onValuechange(e, attrCode, newValue)
{
	if("M91011013106" == attrCode){
		if("0" ==newValue){
			$('#BSELM91011013102_span').closest('.link ').css("display", "none");
			$('#BSELM91011013102').attr("nullable", "yes");
			$('#BSELM91011013101_span').closest('.link ').css("display", ""); 
			$('#BSELM91011013100_span').closest('.link ').css("display", ""); 
		}
		else{
			$('#BSELM91011013102_span').closest('.link ').css("display", "");
			$('#BSELM91011013102').attr("nullable", "no");
			$('#BSELM91011013100').attr("nullable", "yes");
			$('#BSELM91011013102_span').css("display", "");
			$('#BSELM91011013100_span').closest('.link ').css("display", "none"); 
			$('#BSELM91011013101_span').closest('.link ').css("display", "none"); 
			$('#BSELM91011013102_span').parent().parent().attr("class","link required");

		}
	}
	
	
}
/*************和对讲终端串号校验预占**************/
function checknum_onValuechange(e, attrCode, newValue){
	if("M91011013103"== attrCode || "M1103013103"== attrCode){
		if(!$.validate.verifyField(e)){
			return false;
		}
		var modemSn = newValue;
		 Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.enterprise.cs.bboss.common.AjaxDataVerify', 'getCustomRingingIMSh','&RES_NO='+modemSn, function(data) {
			 $.endPageLoading();
			 var resultCode = data.get("X_RESULTCODE");
			 var resultInfo = data.get("X_RESULTINFO");
			 if(resultCode =='0'){
				 MessageBox.success("终端串号预占成功！");
			 }
			 if(resultCode =='-1'){
                 $.validate.alerter.one($(e)[0], "提示",resultInfo);
			    	$(e).val("");
					$(e).select();
			       return false;
			    }else if(resultCode==undefined || resultCode==""){
                 $.validate.alerter.one($(e)[0], "提示","终端串号校验失败!");
			    	$(e).val("");
					$(e).select();
			    }	
	            }, function(e, i) {
	            	
	   			                   
	            },{async:false});	
		 
	}
}


/** *********************************流量统付业务属性校验************************************************ */
function flowOnePay_onValuechange(e, attrCode, newValue) {
	if ("999044003" == attrCode || "999044005" == attrCode || "999084003" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
            $.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联系人电话
	if ("999054005" == attrCode || "999054003" == attrCode || "999084003" == attrCode || "999084056" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 999044009仅业务模式为2时必填
	if ("999044008" == attrCode) {
		if ("2" == newValue) {
			$("#input_B999044009").parent().parent().attr("class","link required");
			$('#input_B999044009').attr("nullable", "no");
		} else {
			$("#input_B999044009").parent().parent().attr("class","link");
			$('#input_B999044009').attr("nullable", "yes");
		}

	}

	if ("999044009" == attrCode) {
		var limitFee = $("#input_B999044009").val();
		if (limitFee.length>2 &&(limitFee.substr(limitFee.length-2,2) == "MB" || limitFee.substr(limitFee.length-2,2) == "mb" )){
			return true;
		}
		else {
            $.validate.alerter.one($(e)[0], "每用户封顶流量单位（MB）必须填写！例如：100MB");
			$(e).val("");
			$(e).focus();
			return false;
		}
	}

	// 999044052业务地域范围为本省业务时，请填主办省（海南）
	if ("999044046" == attrCode) {
		var value = $('#input_B999044052').val();
		if ("2" == value && "898" != newValue) {
            $.validate.alerter.one($(e)[0], "业务地域范围为本省业务时，请填主办省(海南)！");
			$(e).val("");
			$(e).focus();
			return false;
		}
	}

	// 数字
	if ("999054011" == attrCode || "999054012" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 长度
	if ("999054016" == attrCode) {
		if (newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "长度最多为中文8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 月流量规模(GB)
	if ("999044013" == attrCode || "999084013" == attrCode) {
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 客户产品简称
	/*if ("999044014" == attrCode) {
		if (newValue.length > 8 || newValue.length < 2) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式正确,应为2~8个汉字或者字母");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}*/
	
	if ("999044014" == attrCode) {
		var len = 0;
        for (var i = 0; i < newValue.length; i++) {
           var length = newValue.charCodeAt(i);
           if(length>=0&&length<=128)
            { 
        	   len += 1;
            }
            else
            {
                len += 2;
            }
        } 
        if(len>8){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文，字母或者数字，但不能超过8位字符");
			$(e).val("");
			$(e).select();
			return false;
        } 
		return true;
	}
	
	

	if ("999054052" == attrCode) {
		var value = $('#input_B999054008').val();
		if ("5" == value && "2" != newValue) {
            $.validate.alerter.one($(e)[0], "受理模式为5时，业务范围只能选2-本省业务");
			$(e).val("");
			$(e).focus();
			return false;
		}
	}

	if ("999044052" == attrCode) {
		var value = $('#input_B999044008').val();
		if ("5" == value && "2" != newValue) {
            $.validate.alerter.one($(e)[0], "受理模式为5时，业务范围只能选2-本省业务");
			$(e).val("");
			$(e).focus();
			return false;
		}
	}

	// IP或Url条数
	if ("999044057" == attrCode) {
		var filter = /^[0-9]{1,3}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为正整数, 最长3位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("999044052" == attrCode) {
		if ("2" == newValue) {
			$('#input_B999044046').attr("disabled", "true");
			$('#input_B999044046').val("290");
			$('#input_B999044046').change();
		} else {
			$('#input_B999044046').removeAttr("disabled");
			$('#input_B999044046').val("");
			$('#input_B999044046').change();
		}
	}

	// if ("999041104" == attrCode || "999051104" == attrCode) {
	// if ("0" == newValue) {
	// $(e).val("");
	// $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为0,无限期的时候可以填写为00");
	// return false;
	// }
	// if ("00" == oldValue || oldValue == newValue || oldValue == "") {
	// return true;
	// } else {
	// $(e).val(oldValue);
	// $.validate.alerter.one($(e)[0], $(e).attr('desc') + "只支持从无限期改为有限期，不支持从有限期进行修改");
	// return false;
	// }
	// }
	if("M999051104"==attrCode){
		if(oldValue!="00"){
			$('#input_BM999041104').css("readonly","true");
		}
	}
	if("M999041103" == attrCode ){
		
		$('#BSELM999041102').attr("nullable", "yes");
	}
	if("M999051103" == attrCode){
		
		$('#BSELM999051102').attr("nullable", "yes");

	}
	if("M999041101" == attrCode||"M999051101" == attrCode||"M999081101" == attrCode||"M999091101" == attrCode){
		if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字,且流量单位默认为MB");
			$(e).val("");
			$(e).select();
			return false;
		}
	

	}
	
	if("M999041106" == attrCode){
		if("0" ==newValue){
			$('#BSELM999041102_span').closest('.link ').css("display", "none");
			$('#BSELM999041102').attr("nullable", "yes");
			$('#BSELM999041100_span').closest('.link ').css("display", ""); 
			$('#input_BM999041101').closest('.link ').css("display", ""); 
			$('#BSELM999041103_span').closest('.link ').css("display", ""); 
			$('#BSELM999041103').attr("nullable", "no");
			$('#input_BM999041104').closest('.link ').css("display", ""); 
			$('#input_BM999041104').attr("nullable", "no");
		}
		else{
			$('#BSELM999041102_span').closest('.link ').css("display", "");
			$('#BSELM999041102').attr("nullable", "no");
			$('#BSELM999041102_span').css("display", "");

			$('#BSELM999041100_span').closest('.link ').css("display", "none"); 
			$('#input_BM999041101').closest('.link ').css("display", "none"); 
			$('#BSELM999041103_span').closest('.link ').css("display", "none"); 
			$('#BSELM999041103').attr("nullable", "yes");
			$('#input_BM999041104').closest('.link ').css("display", "none");
			$('#input_BM999041104').attr("nullable", "yes");
		}
	}
	if("M999051106" == attrCode){
		if("0" ==newValue){
			$('#BSELM999051102_span').closest('.link ').css("display", "none");
			$('#BSELM999051102').attr("nullable", "yes");
			$('#BSELM999051100_span').closest('.link ').css("display", ""); 
			$('#input_BM999051101').closest('.link ').css("display", ""); 
			$('#BSELM999051103_span').closest('.link ').css("display", ""); 
			$('#BSELM999051103').attr("nullable", "no");
			$('#input_BM999051104').closest('.link ').css("display", ""); 
			$('#input_BM999051104').attr("nullable", "no");
		}
		else{
			$('#BSELM999051102_span').closest('.link ').css("display", "");
			$('#BSELM999051102').attr("nullable", "no");
			$('#BSELM999051102_span').css("display", "");

			$('#BSELM999051100_span').closest('.link ').css("display", "none"); 
			$('#input_BM999051101').closest('.link ').css("display", "none"); 
			$('#BSELM999051103_span').closest('.link ').css("display", "none"); 
			$('#BSELM999051103').attr("nullable", "yes");
			$('#input_BM999051104').closest('.link ').css("display", "none");
			$('#input_BM999051104').attr("nullable", "yes");
		}
	}
	
	return true;
}

/** *************************************网信业务******************************************* */

/*
 * 全网网信商品--属性变更校验
 */
function netInfo_onValueChange(e, attrCode, newValue) {
	if ("229017401" == attrCode) {
		// 校验签名长度是否符合规范，客户签名2-8个汉字，或2-8个英文字母
		if (newValue.length < 2 || newValue.length > 8) {
            $.validate.alerter.one($(e)[0], "客户签名2-8个汉字，或2-8个英文字母");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	if ("229017402" == attrCode) {
		if ("02" == newValue) {
			$("#input_B229017417").attr("nullable", "no");
		} else {
			$("#input_B229017417").attr("nullable", "yes");
		}
	}

	// 集团客户联系人邮箱
	if ("229017412" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], "管理员邮箱格式不正确");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], "套餐折扣的不正,折扣应为0-100的整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 集团客户长服务代码随客户使用号码类型的变更而变更
	if ("229017405" == attrCode) {
		if (newValue == "01") {
			$('#input_B229012009').val('106501623');
		} else if (newValue == "02") {
			$('#input_B229012009').val('1065016');
		}
		changeValue($('#input_B229012009')[0]);
		$('#input_B229012009').focus();
		return true;
	}

	// 手机号码
	if ("229017413" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.validate.alerter.one($(e)[0], "手机号码的格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 集团客户长服务代码
	if ("229012009" == attrCode) {
		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "集团客户服务代码不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		// 获取集团客户使用号码类型
		var longServType = $('#input_B229017405').val();
		if (longServType == "01") {// 统一号码
			// 检查前缀是否正确
			if (newValue.indexOf("106501623") != 0) {
				$.validate.alerter.one($(e)[0], "集团客户服务代码不正确，需要以106501623开头");
				$(e).val("");
				$(e).select();
				return false;
			}
			// 检查序列号位数是否正确
			if (newValue.toString().substring(9).length < 4
					|| newValue.toString().substring(9).length > 8) {
				$.validate.alerter.one($(e)[0], "集团客户服务代码不正确，106501623后面请接4-8位序列号");
				$(e).val("");
				$(e).select();
				return false;
			}
		} else {// 扩展号码
			if (newValue.indexOf("1065016") != 0) {
				$.validate.alerter.one($(e)[0], "集团客户服务代码不正确，需要以1065016开头");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (newValue.toString().substring(6).length > 2) {
				if (newValue.toString().substring(7, 9) == '23') {
					$
							.showErrorMessage("集团客户服务代码不正确，号码类型为扩展号码时，1065016+23的形式不能用");
					$(e).val("");
					$(e).select();
					return false;
				}
			}
			if (newValue.toString().substring(6).length < 5
					|| newValue.toString().substring(6).length > 11) {
				$.validate.alerter.one($(e)[0], "集团客户服务代码不正确，1065016后面请接4-10位序列号");
				$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 英文短信正文签名
	if ("229017419" == attrCode) {
		var filter = /^[a-zA-Z]{2,8}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度为2-8位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	return true;
}


/** *************************************跨省互联网******************************************* */
function specialInternet_onValueChange(e, attrCode, newValue) {
	// 带宽(请填写单位:M或G)
	if ("1112083307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).val("");
			$(e).select();
			return false;
		}

	}

	// 数字
	if ("1112083340" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 处理意见
	if ("1112083483" == attrCode) {
		if ("0" == newValue) {
			// 未处理原因
			$("#input_B1112083475").attr("nullable", "yes");
		}

		if ("1" == newValue) {
			// 未处理原因
			$("#input_B1112083475").attr("nullable", "no");
		}
	}

	// 联系人电话
	if ("1112083313" == attrCode || "1112083406" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 客户机房所在城市 1112083311 1112083305
	if ("1112083311" == attrCode) {
		selectChange(e, attrCode, '1112083305', newValue);
	}

	// 1112083473 电路开通状态
	if ("1112083473" == attrCode) {
		if ("开通" == newValue) {
			$("#BSEL1112083430").parent().parent().parent().attr("class", "link required");
			$("#input_B1112083430").attr("nullable", "no");
			$("#input_B1112083344").parent().parent().attr("class", "link required");
			$("#input_B1112083344").attr("nullable", "no");
		} else {
			$("#BSEL1112083430").parent().parent().parent().attr("class", "link ");
			$("#input_B1112083430").attr("nullable", "yes");
			$("#input_B1112083344").parent().parent().attr("class", "link ");
			$("#input_B1112083344").attr("nullable", "yes");
		}

		if ("未开通" == newValue) {
			$("#input_B1112083475").parent().parent().attr("class", "link required");
			$("#input_B1112083475").attr("nullable", "no");
		} else {
			$("#input_B1112083475").parent().parent().attr("class", "link ");
			$("#input_B1112083475").attr("nullable", "yes");
		}
	}

}
/** *************************************跨省行业应用******************************************* */

function ovProvinceTrade_onValueChange(e, attrCode, newValue) {
	// 数字判断
	if ("9116014507" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 配合省、配合地市联动性
	if ("9116014509" == attrCode) {
		selectChange(e, attrCode, '9116014510', newValue);
	}
	if ("9116014537" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** *************************************局数据制作******************************************* */
function bureauDataMake_onValueChange(e, attrCode, newValue) {
	// 判断是否为数字
	if ("1113035005" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** *************************************车务通业务******************************************* */

/*
 * 全网车务通商品--属性变更校验
 */
function soCarThings_onValueChange(e, attrCode, newValue) {
	// 管理员手机号码
	if ("1109016601" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 管理员密码 64个字节以内
	if ("1109010022" == attrCode) {
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
		if (valLength >= 64) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 管理员密码 64个字节以内
	if ("1109010021" == attrCode) {
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
		if (valLength >= 32) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 本地车务通商品--属性变更校验
 */
function localCarThings_onValueChange(e, attrCode, newValue) {
	// 管理员手机号码
	if ("1109037703" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 备注
	if ("1109031036" == attrCode && byteLength(newValue) > 100) {
		$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度超出范围，请控制在100字节内");
		$(e).val("");
		$(e).select();
		return false;
	}

	// 管理员账户
	if ("1109037701" == attrCode && byteLength(newValue) > 32) {
		$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度超出范围，请控制在32字节内");
		$(e).val("");
		$(e).select();
		return false;
	}

	// 管理员密码
	if ("1109037702" == attrCode && byteLength(newValue) > 64) {
		$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度超出范围，请控制在64字节内");
		$(e).val("");
		$(e).select();
		return false;
	}

	// 终端型号、终端ID、终端车牌号
	if (("1109031003" == attrCode || "1109031004" == attrCode || "1109031005" == attrCode)
			&& byteLength(newValue) > 32) {
		$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度超出范围，请控制在32字节内");
		$(e).val("");
		$(e).select();
		return false;
	}

	if ("1109035100" == attrCode) {
		selectChange(e, attrCode, '1109035101', newValue);
	}
}

/*
 * 全网POC商品--属性变更校验
 */
function netPOC_onValueChange(e, attrCode, newValue) {
	// 手机号码
	if ("1103027714" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

/** *************************************本地POC******************************************* */

/*
 * 本地POC商品--属性变更校验
 */
function localPOC_onValueChange(e, attrCode, newValue) {

	// 手机号码
	if ("1103017714" == attrCode) {
		var isMod = g_IsMobileNumber(newValue);
		if (isMod == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 备注
	if ("1103011036" == attrCode) {
		// 正则表达式验证邮箱格式
		if (newValue.length > 50) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度不能超过50");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

}

/*
 * 农信通商品--属性变更校验
 */
function nongXingTong_onValueChange(e, attrCode, newValue) {
	// 日期格式
	if ("1102221035" == attrCode || "331011035" == attrCode
			|| "331031035" == attrCode || "331041035" == attrCode
			|| "331051035" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1102221034" == attrCode || "331011034" == attrCode
			|| "331031034" == attrCode || "331041034" == attrCode
			|| "331051034" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("331030002" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "为6位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 每日下发的最大条数
	if ("1102221032" == attrCode || "331011032" == attrCode
			|| "331031032" == attrCode || "331031032" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 每月下发的最大条数
	if ("1102221033" == attrCode || "331011033" == attrCode
			|| "331031033" == attrCode || "331041033" == attrCode
			|| "331051033" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	// EC客户长服务
	if ("331012009" == attrCode || "331032019" == attrCode
			|| "331042009" == attrCode || "331052009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("125829999") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以125829999开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length < 16) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("331011009" == attrCode || "331031009" == attrCode
			|| "331041009" == attrCode || "331051009" == attrCode
			|| "331031019" == attrCode) {

		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.indexOf("125829999") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以125829999开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length < 16) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("1102221009" == attrCode || "1102222009" == attrCode) {

		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 16) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "为16位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** *************************************本地POC******************************************* */

/*
 * 财信通商品--属性变更校验
 */
function financInfo_onValueChange(e, attrCode, newValue) {
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以1065089开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length < 14) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于14位");
			$(e).val("");
			$(e).select();
			return false;
		}

		$('#input_B228012009').val(newValue);
		$('#input_B228012009').attr("disabled", true);

		$('#input_B228022009').val(newValue);
		$('#input_B228022009').attr("disabled", true);

		$('#input_B228032009').val(newValue);
		$('#input_B228032009').attr("disabled", true);

		$('#input_B228042009').val(newValue);
		$('#input_B228042009').attr("disabled", true);

		$('#input_B228052009').val(newValue);
		$('#input_B228052009').attr("disabled", true);

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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以1065089开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length < 14) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于14位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 彩信的短信上行点播码
	if ("228171025" == attrCode || "228141025" == attrCode
			|| "228181025" == attrCode || "228151025" == attrCode
			|| "228161025" == attrCode || "228191025" == attrCode) {

		if (newValue.indexOf("1065089") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "格式不正确，需要以1065089开头。EC短信基本接入号＋01，即1065022XYABCDE01");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.length < 16) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于16位");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 本地MAS（商品）--属性变更校验
 */
function localMas_onValueChange(e, attrCode, newValue) {

	// IP地址
	if ("1101050003" == attrCode || "1101050100" == attrCode
			|| "1101050104" == attrCode) {
		if ($('#input_B1101050002').val() == "浮动IP") {
			var zz = '127.0.0.1';
			if (zz != newValue) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确，接入类型为浮动IP时,IP只能为127.0.0.1");
				$(e).val("");
				$(e).select();
				return false;
			}
		} else {
			var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
			if (!zz.test(newValue)) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确，需要000.000.000.000格式");
				$(e).val("");
				$(e).select();
				return false;
			}
			var laststr = newValue.split(".");
			if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
					|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
				$(e).val("");
				$(e).select();
				return false;
			}
		}

	}

	// 联系人邮件
	if ("1101050007" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请填写正确的邮箱地址");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联系人电话
	if ("1101050006" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1101059900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为空！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 集团彩信直联(商品)--属性变更校验
 */
function groupMMS_onValueChange(e, attrCode, newValue) {
	// EC/SI上行URL
	if ("1106014222" == attrCode || "1107014222" == attrCode) {
		if ($.format.lowercase(newValue).indexOf("http://") != 0) {
			var newAddres="http://"+newValue;
			$(e).val(newAddres);
			return true;
		}
		return true;
	}

	// EC/SI主机IP地址
	if ("1106014208" == attrCode || "1107014208" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).val("");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("1106011009" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 判断是否为数字
	if ("1106014402" == attrCode || "1106014209" == attrCode
			|| "1107011009" == attrCode || "1107014402" == attrCode
			|| "1107014209" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1104019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为空！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/*
 * 集团短信直联(商品)--属性变更校验
 */
function groupSMS_onValueChange(e, attrCode, newValue) {

	// EC/SI主机IP地址
	if ("1105014208" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！需要000.000.000.000格式");
			$(e).val("");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须是1-255之间");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue == '127.0.0.1') {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！不能是127.0.0.1");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 备用IP地址
	if ("1105014418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		var strarr = newValue.split(",");
		for (var i = 0; i < strarr.length; i++) {
			var laststr = strarr[i].split(".");
			if (!zz.test(strarr[i])) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确，需要000.000.000.000格式");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
					|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (strarr[i] == '127.0.0.1') {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能是127.0.0.1");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}
	// 集客部联系电话
	if ("1105014221" == attrCode || "1105014219" == attrCode
			|| "1105014216" == attrCode || "1105014232" == attrCode
			|| "1105014232" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("1105011009" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须是数字!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 企业代码
	if ("1105012202" == attrCode) {
		// 检查 企业代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，为6位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * PushEmail（商品）--属性变更校验
 */
function pushEmail_onValueChange(e, attrCode, newValue) {
	if ("1101010007" == attrCode || "1112045005" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要000.000.000.000格式");
			$(e).val("");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1101018008" == attrCode || "1101018007" == attrCode
			|| "1101018002" == attrCode || "1101018006" == attrCode
			|| "1112019008" == attrCode || "1112019007" == attrCode
			|| "1112019002" == attrCode || "1112019006" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 跨省VPMN（商品）--属性变更校验
 */
function groupVPMN_onValueChange(e, attrCode, newValue) {

	// 合同附件
	if ("1111019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为空！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/*
 * 跨省集团wlan（商品）--属性变更校验
 */
function groupWlan_onValueChange(e, attrCode, newValue) {

	// 处理意见
	if ("301013483" == attrCode) {
		if ("0" == newValue) {
			// 未处理原因
			$("#input_B301013475").attr("nullable", "yes");
		}

		if ("1" == newValue) {
			// 未处理原因
			$("#input_B301013475").attr("nullable", "no");
		}
	}

	// A端对应省公司
	if ("301013311" == attrCode) {
		selectChange(e, attrCode, '301013305', newValue);
	}

	// 账号数量
	if ("301014327" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 联系电话
	if ("301013313" == attrCode || "301013406" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 带宽
	if ("301013307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入数字加单位!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 企业一卡通（商品）--属性变更校验
 */
function businessCard_onValueChange(e, attrCode, newValue) {

	// 每日/月下发的最大条数
	if ("247011032" == attrCode || "247011033" == attrCode
			|| "247021032" == attrCode || "247021033" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的下发条数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.substring(0, 1) == "-") {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，下发条数不能为负数");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 时间格式
	if ("247021036" == attrCode || "247011036" == attrCode
			|| "247011034" == attrCode || "247011035" == attrCode
			|| "247021034" == attrCode || "247021035" == attrCode
			|| "331051035" == attrCode) {
		var zz = /^([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$/;
		if (!zz.test(newValue) && newValue != "") {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC客户长服务和基本接入号
	if ("247011009" == attrCode || "247021009" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要填入5位数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 24701
		$('#input_B247012009').val(newValue);
		$('#input_B247012009').attr("disabled", true);

		// 24702
		$('#input_B247021019').val(newValue);
		$('#input_B247021019').attr("disabled", true);
		$('#input_B247021025').val(newValue);
		$('#input_B247021025').attr("disabled", true);
		$('#input_B247022019').val(newValue);
		$('#input_B247022019').attr("disabled", true);

	}
	// 英文签名
	if ("247020050" == attrCode || "247010050" == attrCode) {
		var filter = /^[a-zA-Z]{1,16}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过16位");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 手机号码
	if ("247020003" == attrCode || "247010003" == attrCode
			|| "247030023" == attrCode || "248030023" == attrCode
			|| "249030023" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	return true;
}
/*
 * 集团会议电话(商品) --属性变更校验
 */
function groupMeetPhone_onValueChange(e, attrCode, newValue) {

	if ("9905019105010002" == attrCode || "9905010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("9905019105010001" == attrCode || "9905010001" == attrCode) {
		if (!g_ismail(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	return true;
}

/*
 * 移动会议电话（商品）--属性变更校验
 */
function conferencePhone_onValueChange(e, attrCode, newValue) {

	if ("9105019105010002" == attrCode || "9105010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("9105019105010001" == attrCode || "9105010001" == attrCode) {
		if (!g_ismail(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	return true;
}

/*
 * 呼叫中心直联--属性变更校验
 */
function callCenterJoint_onValueChange(e, attrCode, newValue) {
	// 每日、月下发的最大条数
	if ("1113025005" == attrCode || "1113025004" == attrCode
			|| "1113026003" == attrCode) {
		if (newValue.length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 联系人电话
	if ("1113026010" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 合同附件
	if ("1113019900" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为空！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	return true;
}

/*
 * 400国际业务--属性变更校验
 */
function bnsIter400_onValueChange(e, attrCode, newValue) {

	// 400号码验证
	if ("4115077011" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("4001") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以4001开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length != 10) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度为10位");
			$(e).val("");
			$(e).select();
			return false;
		}

		$('#input_B4115071009').val('10657' + newValue);
		$('#input_B4115071009').attr("disabled", true);

		$('#input_B4115072009').val('10657' + newValue);
		$('#input_B4115072009').attr("disabled", true);

	}

	// 每日、月下发的最大条数
	if ("4115071033" == attrCode || "4115071032" == attrCode) {
		if (newValue.length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC长服务代码
	if ("4115072009" == attrCode) {
		if (newValue.indexOf("10657") != 0) {
			MessageBox
					.alert($(e).attr('desc') + "格式不正确，需要以10657开头+400语音的400号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC短信基本接入号
	if ("4115071009" == attrCode) {
		if (newValue.indexOf("10657") != 0) {
			MessageBox
					.alert($(e).attr('desc') + "格式不正确，需要以10657开头+400语音的400号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 集团客户短信接收手机号
	if ("4115070003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 中文短信/彩信正文签名
	if ("4115071031" == attrCode) {
		if (newValue.toString().length > 8) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于8位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC企业码
	if ("4115071028" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 不允许下发开始时间（HHMMSS） 不允许下发结束时间（HHMMSS）
	if ("4115071034" == attrCode || "4115071035" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		// 时间格式：hhmmss
		if (!g_IsTime(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

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
function business400_onValueChange(e, attrCode, newValue) {

	// 阻截省下拉联动
	if ("4115027012" == attrCode) {
		selectChange(e, attrCode, '4115027016', newValue);
	}
	// 时间格式：hhmmss
	if ("4115061035" == attrCode || "4115091034" == attrCode
			|| "4115061034" == attrCode || "4115091035" == attrCode
			|| "4115081034" == attrCode || "4115081035" == attrCode) {
		if (!g_IsTime(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 400预占流水号
	if ("4115017007" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (newValue.length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度为6位");
			$(e).val("");
			$(e).select();
			return false;
		}
		var num400 = $("#input_B4115017001").val();
		if (num400.length != 10) {
			$.validate.alerter.one($(e)[0], "请先填写400号码数据");
			$(e).val("");
		}
		var bookingNumber = newValue;
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
				+ "校验不通过:第5位为400号码的校验位（400号码的10位数字之和的个位数）");
			$(e).val("");
			$(e).select();
			return false;
		}

		if (pos6 != String(sum1_5 % 10)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
				+ "校验不通过:第6位为前5位数字的校验位（前5位数字之和的个位数）");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 400号码
	if ("4115017001" == attrCode || "4115027011" == attrCode
			|| "4115037011" == attrCode || "4115047011" == attrCode
			|| "4115097011" == attrCode || "4115087011" == attrCode
			|| "4115067011" == attrCode || "4115111001" == attrCode ) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("4001") != 0 && newValue.indexOf("4007") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，需要以4001或4007开头");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length != 10) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度为10位");
			$(e).val("");
			$(e).select();
			return false;
		}

		$('#input_B4115061009').val(newValue);
		$('#input_B4115061009').attr("disabled", true);
		$('#input_B4115062009').val(newValue);
		$('#input_B4115062009').attr("disabled", true);

		$('#input_B4115081009').val(newValue);
		$('#input_B4115081009').attr("disabled", true);
		$('#input_B4115082009').val(newValue);
		$('#input_B4115082009').attr("disabled", true);

		$('#input_B4115091009').val(newValue);
		$('#input_B4115091009').attr("disabled", true);
		$('#input_B4115092009').val(newValue);
		$('#input_B4115092009').attr("disabled", true);

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
		var str = /^[a-zA-Z0-9]{1,16}$/;
		if (!str.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号
	if ("4115061009" == attrCode || "4115091009" == attrCode
			|| "4115081009" == attrCode) {

		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length < 15) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于15位");
			$(e).val("");
			$(e).select();
			return false;
		}

	}

	// EC长服务代码
	if ("4115062009" == attrCode || "4115092009" == attrCode
			|| "4115082009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length < 15) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能小于15位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// EC企业码
	if ("4115061028" == attrCode || "4115091028" == attrCode
			|| "4115081028" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于6位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 中文短信/彩信正文签名
	if ("4115061031" == attrCode || "4115091031" == attrCode
			|| "4115071031" == attrCode || "4115081031" == attrCode) {
		if (newValue.toString().length > 8) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于8位");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 邮箱
	if ("4115017008" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 0－400平台网站不可以上传白名单,1-可以上传白名单 || 0－主叫号码按发话位置1－主叫号码按归属位置
	if ("4115017031" == attrCode || "4115017030" == attrCode) {
		if ("0" != newValue && "1" != newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入0或者1");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 400语音密码接入
	if ("4115037013" == attrCode) {
		if (newValue.length > 4) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于4位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 目的地号码数量
	if ("4115017032" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (parseInt(newValue) < 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "、不能小于1");
			$(e).val("");
			$(e).select();
			return false;
		}
		var operType = $("#productOperType").val();
		if (operType == '9') {
			var user_id = $("#GRP_USER_ID").val();// 返回用户的属性

			// 校验目的地
			Wade.httphandler.submit('',
					'com.ailk.csview.group.verifyClass.frontDataVerify',
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
						$.validate.alerter.one($(e)[0], "操作失败");
						result = false;
					}, {
						async : false
					});
		}
	}
	// 限制呼叫国内400业务次数
	if ("4115047015" == attrCode || "4115047014" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 9) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，长度不能大于9位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 集团客户一点支付--属性变更校验
 */
function GroupCustLine_onValueChange(e, attrCode, newValue) {
	// 管理员邮箱
	if ("999033712" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 代付比例
	if ("999031008" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	//成员套餐费销售折扣
	if ("91011010007" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 销售折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应为0-100的整数,低于7折时客户经理将提交总部审批");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 代付金额
	if ("999031007" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应大于0");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 是否统一资费
	if ("999033703" == attrCode) {
		if ("1" == newValue) {
			$("#BSEL999033704").parent().parent().parent().attr("class", "link required");
			$('#input_BSEL999033704').attr('nullable', 'no');
		} else if ("0" == newValue) {
			$("#BSEL999033704").parent().parent().parent().attr("class", "link ");
			$('#input_BSEL999033704').attr('nullable', 'yes');
		}
	}
}

/*
 * 个人帐单代付--属性变更校验
 */
function PersonBillPay_onValueChange(e, attrCode, newValue) {
	// 代付比例
	if ("999021008" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 代付金额
	if ("999021007" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应大于0");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

/*
 * 跨省行业应用卡--属性变更校验
 */
function specialApp_onValueChange(e, attrCode, newValue) {
	// 配合省联系人手机号
	if ("9116014552" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 跨国数据专线--属性变更校验
 */
function gloabSpecialLine_onValueChange(e, attrCode, newValue) {

	// 联系人电话
	if ("1112073322" == attrCode || "1112073313" == attrCode
			|| "1112073407" == attrCode || "1112073444" == attrCode
			|| "1112073411" == attrCode || "1112063407" == attrCode
			|| "1112063411" == attrCode || "1112063322" == attrCode
			|| "1112063313" == attrCode || "1112063444" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 判断是否为数字
	if ("1112063352" == attrCode || "1112063349" == attrCode
			|| "1112073349" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("1112064314" == attrCode || "1112064311" == attrCode
			|| "1112064315" == attrCode || "1112064312" == attrCode
			|| "1112074314" == attrCode || "1112074311" == attrCode
			|| "1112074315" == attrCode || "1112074312" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "不正确,应为0-100的整数,且所有月使用费用（关口局，落地省）的比例和为100");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1112074314" == attrCode) {

		var i = 100 - $('#input_B1112074314').val();
		$('#input_B1112074315').val(i);
		changeValue($('#input_B1112074315')[0]);
		$('#input_B1112074315').attr('disabled', false);

	}
	if ("1112074315" == attrCode) {

		var i = 100 - $('#input_B1112074315').val();
		$('#input_B1112074314').val(i);
		changeValue($('#input_B1112074314')[0]);
		$('#input_B1112074314').attr('disabled', false);
	}
	if ("1112074311" == attrCode) {

		var i = 100 - $('#input_B1112074311').val();
		$('#input_B1112074312').val(i);
		changeValue($('#input_B1112074312')[0]);
		$('#input_B1112074312').attr('disabled', false);
	}
	if ("1112074312" == attrCode) {

		var i = 100 - $('#input_B1112074312').val();
		$('#input_B1112074311').val(i);
		changeValue($('#input_B1112074311')[0]);
		$('#input_B1112074311').attr('disabled', false);
	}

	if ("1112064314" == attrCode) {

		var i = 100 - $('#input_B1112064314').val();
		$('#input_B1112064315').val(i);
		changeValue($('#input_B1112064315')[0]);
		$('#input_B1112064315').attr('disabled', false);

	}
	if ("1112064315" == attrCode) {

		var i = 100 - $('#input_B1112064315').val();
		$('#input_B1112064314').val(i);
		changeValue($('#input_B1112064314')[0]);
		$('#input_B1112064314').attr('disabled', false);
	}
	if ("1112064311" == attrCode) {

		var i = 100 - $('#input_B1112064311').val();
		$('#input_B1112064312').val(i);
		changeValue($('#input_B1112064312')[0]);
		$('#input_B1112064312').attr('disabled', false);
	}
	if ("1112064312" == attrCode) {

		var i = 100 - $('#input_B1112064312').val();
		$('#input_B1112064311').val(i);
		changeValue($('#input_B1112064311')[0]);
		$('#input_B1112064311').attr('disabled', false);
	}

	// 带宽(请填写单位:M或G)
	if ("1112063307" == attrCode || "1112073307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).val("");
			$(e).select();
			return false;
		}

	}
	// A 端省公司 1112053311 1112053305
	if ("1112073311" == attrCode) {
		selectChange(e, attrCode, '1112073305', newValue);
	}
	// Z端省公司 1112053306
	if ("1112073320" == attrCode) {
		selectChange(e, attrCode, '1112073306', newValue);
	}
	// 所在城市、机房名称、对应省公司、详细地址联动
	if ("1112063364" == attrCode) {
		selectChange(e, attrCode, '1112063321', newValue);
	}
	if ("1112063321" == attrCode) {
		selectChange(e, attrCode, '1112063306', newValue);
		selectChange(e, attrCode, '1112063319', newValue);
	}
	// 服务类型变化，接口类型 随之变化
	if ("1112063327" == attrCode) {
		selectChange(e, attrCode, '1112063316', newValue);
		selectChange(e, attrCode, '1112063325', newValue);
		selectChange(e, attrCode, '1112063440', newValue);
	}
}
function localIDC_onValueChange(e, attrCode, newValue) {
	if ("1112093336" == attrCode || "1112093337" == attrCode
			|| "1112093339" == attrCode || "1112093340" == attrCode
			|| "1112093342" == attrCode || "1112093343" == attrCode
			|| "1112133336" == attrCode || "1112123337" == attrCode
			|| "1112113337" == attrCode || "1112173336" == attrCode
			|| "1112133342" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("1112153336" == attrCode || "1112143336" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}
/*
 * 本地企业飞信--属性变更校验
 */
function locGroupFetion_onValueChange(e, attrCode, newValue) {
	// 管理员邮箱
	if ("910601006" == attrCode || "9106011019" == attrCode
			|| "910602006" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	// 集团客户联系人手机号码
	if ("910601007" == attrCode || "910602007" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 企业能力阀值
	if ("910601008" == attrCode || "910602008" == attrCode) {
		if (newValue.toString().length < 3 || newValue.indexOf("1:") != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正，正确的形式为[1:XXXXX]");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 注册号码
	if ("9106011017" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入手机号码或固定电话");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 代付比例
	if ("9106011025" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	if ("9106011024" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应大于等于0");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

}

/** **********************************企业手机报(商品)************************************* */
function enterpriseMobiPaperChange(e, attrCode, newValue) {
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
//			$
//					.showErrorMessage($(e).attr('desc')
//							+ "格式不正确,必须为中文，字母或者数字，但不能超过8位");
//			$(e).val("");
//			$(e).select();
//			return false;
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文，字母或者数字，但不能超过8位");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).val("");
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
			|| "40104290004" == attrCode || "91011010006" == attrCode	) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
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
			|| "40104290005" == attrCode || "91011010005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为6位数字");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/** ******************************企业阅读商品***************************************** */
function enterpriseRead_onValuechange(e, attrCode, newValue) {
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
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文，字母或者数字，但不能超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** *********************************************************************** */
/*
 * IMS多媒体彩铃--属性变更校验
 */
function customRingingIMS_onValueChange(e, attrCode, newValue) {

	// 管理员手机号码
	if ("9104010002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号码");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 管理员邮箱
	if ("9104010001" == attrCode || "9104011011" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

/*
 * 本地M2M（商品）--属性变更校验
 */
function localM2M_onValueChange(e, attrCode, newValue) {

	if ("1101070004" == attrCode || "1101070002" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 合同附件
	if ("1101070011" == attrCode) {
		if (null == newValue || '' == newValue) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能为空！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/** ***************************************跨省专线*********************************************** */

function LineBusiness_onValueChange(e, attrCode, newValue) {

	if ("1112053407" == attrCode || "1112053313" == attrCode
			|| "1112053322" == attrCode || "1112053411" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1112053436" == attrCode) {
		if(newValue!=null||newValue!=""||newValue!=undefined){
			var bizMode=PageData.getData($(".e_SelectOfferPart")).get("MERCHINFO").get("BIZ_MODE");
			if("3"==bizMode){
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "业务开展模式为主办省一点受理，一点支付时,该属性不可填写！");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}
	// A 端省公司 1112053311 1112053305
	if ("1112053311" == attrCode) {
		debugger;
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "不正确,应为0-100的整数,且所有月使用费用（A端，Z端）的比例和为100");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1112054314" == attrCode) {

		var i = 100 - $('#input_B1112054314').val();
		$('#input_B1112054315').val(i);
		changeValue($('#input_B1112054315')[0]);
		$('#input_B1112054315').attr('disabled', false);

	}
	if ("1112054315" == attrCode) {

		var i = 100 - $('#input_B1112054315').val();
		$('#input_B1112054314').val(i);
		changeValue($('#input_B1112054314')[0]);
		$('#input_B1112054314').attr('disabled', false);
	}
	if ("1112054311" == attrCode) {

		var i = 100 - $('#input_B1112054311').val();
		$('#input_B1112054312').val(i);
		changeValue($('#input_B1112054312')[0]);
		$('#input_B1112054312').attr('disabled', false);
	}
	if ("1112054312" == attrCode) {

		var i = 100 - $('#input_B1112054312').val();
		$('#input_B1112054311').val(i);
		changeValue($('#input_B1112054311')[0]);
		$('#input_B1112054311').attr('disabled', false);
	}

	// 带宽(请填写单位:M或G)
	if ("1112053307" == attrCode) {
		var zz = /^[1-9]\d*(\.\d+)?(M|G)$|^0(\.\d+)?(M|G)$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "的格式不正确，请输入数字加单位!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/*
 * 主办省全网MAS彩信（商品）--属性变更校验
 */
function hostNetMasMMS_onValueChange(e, attrCode, newValue) {

	// 联系电话
	if ("199024216" == attrCode || "199024221" == attrCode
			|| "199014232" == attrCode || "199024232" == attrCode) {

		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！为6位数字");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// 彩信基本接入号
	if ("199021019" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 主IP地址(浮动IP请填写“127.0.0.1”)
	if ("199024417" == attrCode || "199024418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		if (!zz.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确，需要000.000.000.000格式");
			$(e).val("");
			$(e).select();
			return false;
		}
		var laststr = newValue.split(".");
		if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
				|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确，必须是1-255之间");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 数字
	if ("199024202" == attrCode || "199024203" == attrCode
			|| "199024209" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 主办省全网MAS短信（商品）--属性变更校验
 */
function hostNetMas_onValueChange(e, attrCode, newValue) {
	// 企业代码
	if ("199010004" == attrCode) {
		// 检查 企业代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 检查 企业代码位数是否正确
		if (newValue.toString().length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，为6位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 英文签名
	if ("199010013" == attrCode) {
		var filter = /^[a-zA-Z]{1,8}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过8位");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("199011009" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 数字
	if ("199014202" == attrCode || "199014203" == attrCode
			|| "199014211" == attrCode || "199014209" == attrCode) {

		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.substring(0, 1) == "0" && newValue.length > 1) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能以0开头");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	// IP地址
	if ("199014418" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		var strarr = newValue.split(",");
		for (var i = 0; i < strarr.length; i++) {
			var laststr = strarr[i].split(".");
			if (!zz.test(strarr[i])) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确，需要000.000.000.000格式");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
					|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (strarr[i] == '127.0.0.1') {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能是127.0.0.1");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}

	if ("199014417" == attrCode) {
		var zz = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
		var strarr = newValue.split(",");
		for (var i = 0; i < strarr.length; i++) {
			var laststr = strarr[i].split(".");
			if (!zz.test(strarr[i])) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确，需要000.000.000.000格式");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (parseInt(laststr[0]) > 255 || parseInt(laststr[1]) > 255
					|| parseInt(laststr[2]) > 255 || parseInt(laststr[3]) > 255) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须是1-255之间");
				$(e).val("");
				$(e).select();
				return false;
			}
			if (strarr[i] == '127.0.0.1') {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，不能是127.0.0.1");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}
	//端口速率
	if ("199010011" == attrCode) {
		//var custId = $.getSrcWindow().$("#CUST_ID").val();
		var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
    	var custId = groupInfo.get("CUST_ID");
		var serv_level = "";
		// 校验目的地
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
		'qryGrpClassId', '&CUST_ID=' + custId, function(d) {
			var param = d.map.result;
			serv_level = param.get(0, 'SERV_LEVEL');
			if (serv_level==null||serv_level==undefined) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
				$(e).val("");
				$(e).select();
			}

		}, function(e, i) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
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
		        $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
	    	}
	    }
		if (serv_level=="3"||serv_level=="2") //铜牌、银牌级：<=100
		{
			if(parseInt(newValue)>100){
			    errinfo="铜牌、银牌级：最大短信端口速率不能超过100!";
			    $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
		if (serv_level=="4"||serv_level=="") //标准级：<=20
		{
			if(parseInt(newValue)>20){
			    errinfo="标准级：最大短信端口速率不能超过20!";
			    $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
	}
}

/*
 * 公众服务云业务--属性变更校验
 */
function pubCloudSvc_onValueChange(e, attrCode, newValue) {

	// 集客部联系电话
	if ("1116011005" == attrCode || "1116011004" == attrCode) {
		if (!g_IsTelephoneNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 手机号码
	if ("1116011002" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 资源产品优惠类型选择免费测试时，测试计划开始日期、测试计划结束日期、测试申请单附件字段必填，并且需要将费用折扣置为0且不可编辑
	if ("1116013001" == attrCode) {
		if ("1" == newValue) {// 1-免费测试 2-折扣优惠 3-标准资费
			// 计划开始日期
			$("#input_B1116013002").parent().parent().attr("class", "link required");
			$('#input_B1116013002').attr("nullable", "no");
			// 计划结束日期
			$("#input_B1116013003").parent().parent().attr("class", "link required");
			$('#input_B1116013003').attr("nullable", "no");
			// 测试申请单
			$("#BSEL1116013006").parent().parent().parent().attr("class", "link required");
			$('#input_B1116013006').attr("nullable", "no");
			// 费用折扣
			$('#input_B1116013005').val('0');
			$('#input_B1116013005').attr("disabled", true);
		} else {
			// 计划开始日期
			$("#input_B1116013002").parent().parent().attr("class", "link ");
			$('#input_B1116013002').attr("nullable", "yes");
			// 计划结束日期
			$("#input_B1116013003").parent().parent().attr("class", "link ");
			$('#input_B1116013003').attr("nullable", "yes");
			// 测试申请单
			$("#BSEL1116013006").parent().parent().parent().attr("class", "link ");
			$('#input_B1116013006').attr("nullable", "yes");
			// 费用折扣
			$('#input_B1116013005').val('');
			$('#input_B1116013005').attr("disabled", false);
			if ("2" == newValue) {
				$('#input_B1116013005').val('100');
			}
		}
	}

	// 变更类型为1时，资源产品优惠类型可以发生变化
	if ("1116011007" == attrCode) {
		if ("1" == newValue) {// 1-免费测试 2-折扣优惠 3-标准资费
			// 优惠类型
			$('#input_B1116013001').attr("disabled", false);
		}
	}

	// 省公司的费用折扣不能低于5折
	if ("1116013005" == attrCode) {
		if (parseInt(newValue) < 50) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "对于省公司不能低于5折");
			$(e).select();
			return false;
		}
	}
}

/** *************************************统一Centrex业务(商品)******************************************* */

/*
 * 校验多媒体桌面电话产品--属性校验
 * 
 */

function checkUnifyC_onValueChange(e, attrCode, newValue) {
	// 固话数量必须为数字
	if ("2350013107" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入错误!输入必须为数字！请重新输入！");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	// 校验电话号码
	if ("2350013106" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}
/*
 * 校验融合V网产品--属性校验
 * 
 */
function checkFuseVNet_onValueChange(e, attrCode, newValue) {
	// 校验电话号码
	if ("2350023107" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 校验融合总机产品--属性校验
 * 
 */
function checkSwitchBoard_onValueChange(e, attrCode, newValue) {
	// 校验电话号码
	if ("2350033107" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/*
 * 校验融合一号通产品--属性校验
 * 
 */
function checkFuseOneAll_onValueChange(e, attrCode, newValue) {
	// 校验电话号码
	if ("2350043106" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** *************************************APN漫游******************************************* */

/*
 * APN漫游属性APN后缀名校验
 */

function checkExtensions_onValueChange(e, attrCode, newValue) {
	if ("1115010003" == attrCode) {
		var apn = /^\.[a-zA-Z]{1,3}$/;
		var extensions = newValue.substring(newValue.indexOf("."),
				newValue.length);
		if (!apn.test(extensions)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "输入错误!请输入带省份简称的后缀，例如：北京为“.bj” 请重新输入!");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
}

/** *************************************政务易******************************************* */

/*
 * 政务易(商品) --短信白黑名单业务产品 --属性校验
 */

function checkSMSWhiteList(e, attrCode, newValue) {

	// 集团客户短信接收手机号校验
	if ("331070003" == attrCode || "331080003" == attrCode
			|| "331090003" == attrCode || "331060003" == attrCode
			|| "331100003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须为11位手机号码！");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号
	if ("331071009" == attrCode || "331101009" == attrCode
			|| "331091009" == attrCode || "331081009" == attrCode
			|| "331062009" == attrCode) {
		// 数字验证
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要为数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		$('#input_B331072009').val(newValue);
		$('#input_B331072009').attr("disabled", true);
		$('#input_B331082009').val(newValue);
		$('#input_B331082009').attr("disabled", true);
		$('#input_B331092009').val(newValue);
		$('#input_B331092009').attr("disabled", true);
		$('#input_B331102009').val(newValue);
		$('#input_B331102009').attr("disabled", true);

	}

	// 每日下发的最大条数
	if ("331071032" == attrCode || "331071033" == attrCode
			|| "331081032" == attrCode || "331081033" == attrCode
			|| "331091032" == attrCode || "331091033" == attrCode
			|| "331101032" == attrCode || "331101033" == attrCode) {
		var number = /^[0-9]{1,9}$/;
		if (!number.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if("331071034" == attrCode || "331071035" == attrCode) {
		var result = compareDate($('#input_B331071034').val(),$('#input_B331091035').val());
		if (result>=0){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能大于等于结束时间！");
            $(e).val("");
            $(e).select();
            return false;
		}
	}

    if("331081035" == attrCode || "331081034" == attrCode) {
        var result = compareDate($('#input_B331081034').val(),$('#input_B331081035').val());
        if (result>=0){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能小于等于开始时间！");
            $(e).val("");
            $(e).select();
            return false;
        }
    }
    if("331091034" == attrCode || "331091035" == attrCode) {
        var result = compareDate($('#input_B331091034').val(),$('#input_B331091035').val());
        if (result>=0){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能大于等于结束时间！");
            $(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	//
	if ("331101028" == attrCode) {
		var number = /^[0-9]{1,12}$/;
		if (!number.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须12位以内纯数字");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	return true;
}

function compareDate(strDate1,strDate2)
{
    var date1 = new Date(strDate1.replace(/\-/g, "\/"));
    var date2 = new Date(strDate2.replace(/\-/g, "\/"));
    return date1-date2;
}
/*
 * 统一充值
 */
function queryelements_onValueChange(e, attrCode, newValue){
	//业务拓展配合省公司
	if("50010004"==attrCode){
		var filter = /^[0-9]{3}$/;
		if(!filter.test(newValue)){
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为3位纯数字!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	//电话号码
	if("50010006"==attrCode){
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须为11位手机号码！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	//政企分公司服务费结算比例
	if("50010008"==attrCode){
		var filter = /^[0-9]{1,3}$/;
		if (!filter.test(newValue) || newValue>100 || newValue<0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须为0-100的纯数字！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
}
/** ***********************************跨省专线2.0******************************************** */
function business2_onValueChange(e, attrCode, newValue) {
	if("1112413307"==attrCode || "1112413352"==attrCode || "1112413349"==attrCode){
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为纯数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if("1112414311" == attrCode || "1112414312" == attrCode
			|| "1112414314" == attrCode || "1112414315" == attrCode){
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "不正确,应为0-100的整数,且所有月使用费用（A端，Z端）的比例和为100");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	if ("1112414311" == attrCode) {

		var i = 100 - $('#input_B1112414311').val();
		$('#input_B1112414312').val(i);
		changeValue($('#input_B1112414312')[0]);
		$('#input_B1112414312').attr('disabled', false);

	}
	if ("1112414312" == attrCode) {

		var i = 100 - $('#input_B1112414312').val();
		$('#input_B1112414311').val(i);
		changeValue($('#input_B1112414311')[0]);
		$('#input_B1112414311').attr('disabled', false);
	}
	if ("1112414314" == attrCode) {

		var i = 100 - $('#input_B1112414314').val();
		$('#input_B1112414315').val(i);
		changeValue($('#input_B1112414315')[0]);
		$('#input_B1112414315').attr('disabled', false);
	}
	if ("1112414315" == attrCode) {

		var i = 100 - $('#input_B1112414315').val();
		$('#input_B1112414314').val(i);
		changeValue($('#input_B1112414314')[0]);
		$('#input_B1112414314').attr('disabled', false);
	}
	//A端对应省公司和A端所在城市
	if ("1112413311" == attrCode) {
		selectChange(e, attrCode, '1112413305', newValue);
	}
	//Z端对应省公司和Z端所在城市
	if ("1112413320" == attrCode) {
		selectChange(e, attrCode, '1112415022', newValue);
	}
	//A端所在城市和A端区县
	if ("1112413305" == attrCode) {
		selectChange(e, attrCode, '1112415022', newValue);
	}
	//Z端所在城市和Z端区县
	if ("1112413306" == attrCode) {
		selectChange(e, attrCode, '1112415023', newValue);
	}
	
		
}
/** ***********************************行业网关云MAS商品******************************************** */
function IAWGcloudMAS_onValueChange(e, attrCode, newValue) {
	//短信端口速率（条/秒）
	// 短信端口速率（条/秒）
	if ("1101630011" == attrCode || "1101631019" == attrCode
			|| "1101632011" == attrCode || "1101634009" == attrCode
			|| "1101564019" == attrCode || "1101574005" == attrCode
			|| "1101584005" == attrCode || "1101594005" == attrCode
			|| "1101604005" == attrCode || "1101614005" == attrCode
			|| "1101624005" == attrCode || "1101624019" == attrCode
			|| "1101594019" == attrCode || "1101644009" == attrCode 
			|| "1101644010" == attrCode){ 
		if (!g_IsDigit(newValue)){ 
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为纯数字正整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if("1101630011"==attrCode || "1101632011" == attrCode || "1101644009" == attrCode || "1101644010" == attrCode|| "1101584005" == attrCode || "1101614005" == attrCode){
			if (newValue>10000||(newValue)%5!=0){
                $.validate.alerter.one($(e)[0], $(e).attr('desc') +"短/彩信端口速率必须是5的倍数且不能超过10000");
				$(e).val("");    
				$(e).select();					
				return false;
			}
		}
	}

	
	// 企业代码
	if ("1101630004" == attrCode || "1101544001" == attrCode||'1101644002' == attrCode
			|| "1101554001" == attrCode || "1101564001" == attrCode) {
		var filter = /^[0-9]{6,6}$/;
		if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为6位有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
	if ("1101634025" == attrCode) {
		if ("是" == newValue) {
			$("#BSEL1101634414").parent().parent().parent().attr("class", "link required");
			$("#BSEL1101634415").parent().parent().parent().attr("class", "link required");
			$("#input_B1101634004").parent().parent().attr("class", "link required");
			$("#BSEL1101634005").parent().parent().parent().attr("class", "link required");
			$("#input_B1101631019").parent().parent().attr("class", "link required");
			$("#input_B1101632011").parent().parent().attr("class", "link required");
			$('#input_B1101634414').attr("nullable", "no");
			$('#input_B1101634415').attr("nullable", "no");
			$('#input_B1101634004').attr("nullable", "no");
			$('#BSEL1101634005').attr("nullable", "no");
			$('#input_B1101631019').attr("nullable", "no");
			$('#input_B1101632011').attr("nullable", "no");
			$('#BSEL1101634005').attr("disabled", false);
			$('#input_B1101634004').attr("disabled", false);
			$('#input_B1101631019').attr("disabled", false);
			$('#input_B1101632011').attr("disabled", false);
			$('#input_B1101634414').attr("disabled", false);
			$('#input_B1101634415').attr("disabled", false);
		} else {
			$("#BSEL1101634414").parent().parent().parent().attr("class", "link ");
			$('#input_B1101634414').attr("nullable", "yes");
			$('#input_B1101634414').val("");
			$('#input_B1101634414').change();
			//changeValue($('#input_B1101634414')[0]);
			$("#BSEL1101634415").parent().parent().parent().attr("class", "link ");
			$('#input_B1101634415').attr("nullable", "yes");
			$('#input_B1101634415').val("");
			$('#input_B1101634415').change();
			//changeValue($('#input_B1101634415')[0]);
			$("#input_B1101634004").parent().parent().attr("class", "link ");
			$('#input_B1101634004').attr("nullable", "yes");
			$('#input_B1101634004').val("");
			$('#input_B1101634004').change();
			//changeValue($('#input_B1101634004')[0]);
			$("#BSEL1101634005").parent().parent().parent().attr("class", "link ");
			$('#BSEL1101634005').attr("nullable", "yes");
			$('#BSEL1101634005').val("");
			$('#BSEL1101634005').change();
			//changeValue($('#input_B1101634005')[0]);
			$("#input_B1101631019").parent().parent().attr("class", "link ");
			$('#input_B1101631019').attr("nullable", "yes");
			$('#input_B1101631019').val("");
			$('#input_B1101631019').change();
			//changeValue($('#input_B1101631019')[0]);
			$("#input_B1101632011").parent().parent().attr("class", "link ");
			$('#input_B1101632011').attr("nullable", "yes");
			$('#input_B1101632011').val("");
			$('#input_B1101632011').change();
			//changeValue($('#input_B1101632011')[0]);
			$('#BSEL1101634005').attr("disabled", true);
			$('#input_B1101634004').attr("disabled", true);
			$('#input_B1101631019').attr("disabled", true);
			$('#input_B1101632011').attr("disabled", true);
			$('#input_B1101634414').attr("disabled", true);
			$('#input_B1101634415').attr("disabled", true);
		}
	}
	
	if ("1101644006" == attrCode) {
	    if ("是" == newValue) {
	        //获取彩信基本接入号	        
	    	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	    	var groupId = groupInfo.get("GROUP_ID");
	    	var incodea = "02";
	        var bizTypeCode = "002";
			 Wade.httphandler.submit('',
			            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMasEcCodeListByA',
			            '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + groupId + '&BIZ_TYPE_CODE=' + bizTypeCode, function(d) {
				 			dealbaseinCodeputAfter(d,"110164");			            }, function(e, i) {
			                MessageBox.alert("操作失败");
			                return false;
			            },{async:false});
	        
	        $('#input_B1101644007').parent().parent().attr("class", "link required");
	        $('#input_B1101644008').parent().parent().attr("class", "link required");
	        $('#input_B1101644010').parent().parent().attr("class", "link required");
	        $('#input_B1101644007').attr("nullable", "no");
	        $('#input_B1101644008').attr("nullable", "no");
	        $('#input_B1101644010').attr("nullable", "no");
	        $('#input_B1101644007').attr("disabled", false);
	        $('#input_B1101644008').attr("disabled", false);
	        $('#input_B1101644010').attr("disabled", false);
	    } else {
	        $('#input_B1101644007').parent().parent().attr("class", "link ");
	        $('#input_1101644007').attr("nullable", "yes");
	        $('#input_1101644007').val("");
	        $('#input_1101644007').change();
	        //changeValue($('#input_B1101644007')[0]);
	        $('#input_B1101644008').parent().parent().attr("class", "link ");
	        $('#input_B1101644008').attr("nullable", "yes");
	        $('#input_B1101644008').val("");
	        $('#input_B1101644008').change();
	        //changeValue($('#input_B1101644008')[0]);
	        $('#input_B1101644010').parent().parent().attr("class", "link ");
	        $('#input_B1101644010').attr("nullable", "yes");
	        $('#input_B1101644010').val("");
	        $('#input_B1101644010').change();
	        //changeValue($('#input_B1101644010')[0]);
	        $('#input_B1101644007').attr("disabled", true);
	        $('#input_B1101644008').attr("disabled", true);
	        $('#input_B1101644010').attr("disabled", true);
	    }
	}
	
	if ("1101574011" == attrCode) {
		setServCode();//设置服务代码
	    if ("0" == newValue) {//0-黑名单
	    	var servCode = $("#input_B1101574002").val();
	    	var servCodeTemp="";
	    	if(servCode!=undefined && servCode!=""){
	    		servCodeTemp = servCode.substring(0,9);
	    	}
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
			$('#input_B1101574010').val(bizCode);
			//changeValue($('#input_B1101574010')[0]);
			
	        //获取彩信基本接入号
	    	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	    	var groupId = groupInfo.get("GROUP_ID");
	    	var incodea = "02";
	        var bizTypeCode = "002";
			 Wade.httphandler.submit('',
			            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMasEcCodeListByA',
			            '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + groupId + '&BIZ_TYPE_CODE=' + bizTypeCode, function(d) {
				 			dealbaseinCodeputAfter(d,"110164");			            }, function(e, i) {
			                MessageBox.alert("操作失败");
			                return false;
			            });
	    } else {//1-白名单
	    	var servCode = $("#input_B1101574002").val();
	    	var servCodeTemp="";
	    	if(servCode!=undefined && servCode!=""){
	    		servCodeTemp = servCode.substring(0,9);
	    	}	    	//var bizCode = "MHI4444441";
	    	var bizCode = "MHI4444442";
	    	/*if(servCodeTemp == "106509652"){
				bizCode = "MHI4444441";
			}else*/ if(servCodeTemp == "106509022"){
				bizCode = "MHI4444442";
			}else if(servCodeTemp == "106509622"){
				bizCode = "MHI4444446";
			}else{
				bizCode = "MHI4444448";
			}
			$('#input_B1101574010').val(bizCode);
			//changeValue($('#input_B1101574010')[0]);
	    }
	}

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
				$('#BSEL1101574011').val("2");
				$('#BSEL1101574011').change();
				$('#BSEL1101574011').attr("disabled", true);
			}else if("1101584002"==attrCode){
				$('#BSEL1101584011').val("2");
				$('#BSEL1101584011').change();
				$('#BSEL1101584011').attr("disabled", true);
			}
		} else if((newValue.length > 7 && newValue.substring(0, 7)=="1065097")||(newValue.length > 7 && newValue.substring(0, 7)=="1065091")
				) {
			if("1101574002"==attrCode){
				$('#BSEL1101574011').val("0");
				$('#BSEL1101574011').change();
				$('#BSEL1101574011').attr("disabled", true);
			}else if("1101584002"==attrCode){
				$('#BSEL1101584011').val("0");
				$('#BSEL1101584011').change();
				$('#BSEL1101584011').attr("disabled", true);
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
	
	// 企业代码
	if ("1101630004" == attrCode || "1101544001" == attrCode || "1101554001" == attrCode || "1101564001" == attrCode|| "1101644002" == attrCode
			||"1101604037" == attrCode||"1101614037" == attrCode||"1101624037" == attrCode) {
	    var filter = /^[0-9]{6,6}$/;
	    if (!filter.test(newValue)) {
	    	$.validate.alerter.one($(e)[0],$(e).attr('desc') + "格式不正确，应为6位有效数字");
	        $(e).select();
	        return false;
	    }
	}
	// 黑名单选择联动
	if ("1101544008" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101544009").parent().parent().parent().attr("class", "link required");
			$('#BSEL1101544009').attr("nullable", "no");
		} else {
			$("#BSEL1101544009").parent().parent().parent().attr("class", "link ");
			$('#BSEL1101544009').attr("nullable", "yes");
		}
	}

	if ("1101554008" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101554009").parent().parent().parent().attr("class", "link required");
			$('#BSEL1101554009').attr("nullable", "no");
		} else {
			$("#BSEL1101554009").parent().parent().parent().attr("class", "link ");
			$('#BSEL1101554009').attr("nullable", "yes");
		}
	}


	if ("1101604011" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101604012").parent().parent().parent().attr("class", "link required");
			$('#BSEL1101604012').attr("nullable", "no");
		} else {
			$("#BSEL1101604012").parent().parent().parent().attr("class", "link ");
			$('#BSEL1101604012').attr("nullable", "yes");
		}
	}
	
	if ("1101634026" == attrCode || "1101644013" == attrCode || "1101574031" == attrCode || "1101584031" == attrCode || "1101594031" == attrCode) {
	    if (newValue.length <= 10) {
	        //return true;
	    } else {
	        //$.showErrorMessage("网关登陆密码 最长为10位");
	        $.validate.alerter.one($(e)[0], "网关登陆密码 最长为10位");
			$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	
	
	if ("1101544007" == attrCode || "1101554007" == attrCode
			|| "1101564007" == attrCode || "1101574010" == attrCode
			|| "1101584010" == attrCode || "1101594010" == attrCode
			|| "1101604010" == attrCode || "1101614010" == attrCode
			|| "1101624010" == attrCode) {
		if (newValue.length <= 10) {
			//return true;
		} else {
            $.validate.alerter.one($(e)[0], "业务代码必须不长于10位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("1101564019" == attrCode || "1101624002" == attrCode
			|| "1101614002" == attrCode || "1101604002" == attrCode
			|| "1101594002" == attrCode || "1101584002" == attrCode
			|| "1101574002" == attrCode || "1101564006" == attrCode
			|| "1101554006" == attrCode || "1101544006" == attrCode
			|| "1101631009" == attrCode ||"1101644005"==attrCode) {
		//省公司全网长流程局数据、省行业网关省内局数据中“短信基本接入号”最大长度由21位变更为20位；
		if(("1101631009" == attrCode||"1101644005"==attrCode)&&g_IsDigit(newValue) && newValue.length <= 20) {
			//return true;
		}else{
			$.validate.alerter.one($(e)[0], "短信基本接入号最长为20位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		 if (g_IsDigit(newValue) && newValue.length <= 21) {
			//return true;
		} else {
            $.validate.alerter.one($(e)[0], "服务代码、上行点播码、最长为21位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
        if ("1101574002" == attrCode) {
            if (newValue.indexOf("1065096") != 0
                && newValue.indexOf("1065097") != 0) {
                $.validate.alerter.one($(e)[0], "服务代码不正确，需要以1065096或1065097开头");
                $(e).val("");
                $(e).select();
                return false;
            }
        }
	}
	
	if ("1101574005" == attrCode || "1101584005" == attrCode || "1101594005" == attrCode || "1101604005" == attrCode || "1101614005" == attrCode || "1101624005" == attrCode || "1101630011" == attrCode || "1101632011" == attrCode || "1101644009" == attrCode || "1101644010" == attrCode 
			|| "1101544025" == attrCode || "1101544026" == attrCode || "1101554025" == attrCode || "1101554026" == attrCode || "1101564025" == attrCode || "1101564026" == attrCode || "1101574025" == attrCode || "1101574026" == attrCode || "1101584025" == attrCode || "1101584026" == attrCode
			|| "1101594025" == attrCode || "1101594026" == attrCode || "1101604025" == attrCode || "1101604026" == attrCode || "1101614025" == attrCode || "1101614026" == attrCode || "1101624025" == attrCode || "1101624026" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"端口速率、每日最大下发条数、每月下发条数为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544032" == attrCode || "1101554032" == attrCode || "1101564032" == attrCode || "1101574035" == attrCode || "1101584035" == attrCode || "1101594035" == attrCode || "1101604032" == attrCode || "1101614032" == attrCode || "1101624032" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"本网套餐包含条数为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544031" == attrCode || "1101554031" == attrCode || "1101564031" == attrCode || "1101584034" == attrCode || "1101594034" == attrCode || "1101604031" == attrCode || "1101614031" == attrCode || "1101624031" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"本网套餐费（元/月）为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544033" == attrCode || "1101554033" == attrCode || "1101564033" == attrCode || "1101584036" == attrCode || "1101594036" == attrCode || "1101604033" == attrCode || "1101614033" == attrCode || "1101624033" == attrCode || "1101574036" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"本网套餐外资费标准（元/条）为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544035" == attrCode || "1101554035" == attrCode || "1101564035" == attrCode || "1101574038" == attrCode || "1101584038" == attrCode || "1101594038" == attrCode || "1101604035" == attrCode || "1101614035" == attrCode || "1101624035" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"异网套餐包含条数为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544034" == attrCode || "1101554034" == attrCode || "1101564034" == attrCode || "1101584037" == attrCode || "1101594037" == attrCode || "1101604034" == attrCode || "1101614034" == attrCode || "1101624034" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"异网套餐费（元/月）为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}
	if ("1101544036" == attrCode || "1101554036" == attrCode || "1101564036" == attrCode || "1101584039" == attrCode || "1101594039" == attrCode || "1101604036" == attrCode || "1101614036" == attrCode || "1101624036" == attrCode ) {
	    if (g_IsDigit(newValue)) {
	        //return true;
	    } else {
	    	$.validate.alerter.one($(e)[0],"异网套餐外资费标准（元/条）为整数");
	    	$(e).val("");
	        $(e).select();
	        return false;
	    }
	}

	// 集客部联系电话
	if ("1101634003" == attrCode || "1101634012" == attrCode || "1101634020" == attrCode || "1101574004" == attrCode || "1101574016" == attrCode || "1101574030" == attrCode || "1101584004" == attrCode || "1101584016" == attrCode || "1101584030" == attrCode || "1101594004" == attrCode || "1101594016" == attrCode || "1101594030" == attrCode || "1101604004" == attrCode || "1101604016" == attrCode || "1101614004" == attrCode || "1101614016" == attrCode || "1101624004" == attrCode || "1101624016" == attrCode || "1101644012" == attrCode) {
	    if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
	    	$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!请输入手机号码格式或电话号码格式");
	    	$(e).val("");
	    	$(e).select();	    	
	        return false;
	    }
	}
	
	// 英文短信/彩信正文签名
	if ("1101544005" == attrCode || "1101554005" == attrCode || "1101564005" == attrCode || "1101574009" == attrCode || "1101584009" == attrCode || "1101594009" == attrCode || "1101604009" == attrCode || "1101614009" == attrCode || "1101624009" == attrCode) {
	    var filter = /^[a-zA-Z]{1,40}$/;
	    if (!filter.test(newValue)) {
	    	$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文!长度不能超过40个字");
	    	$(e).val("");
	    	$(e).select();
	        return false;
	    }
	}

	// 中文短信/彩信正文签名
	if ("1101544004" == attrCode || "1101554004" == attrCode || "1101564004" == attrCode || "1101574008" == attrCode || "1101584008" == attrCode || "1101594008" == attrCode || "1101604008" == attrCode || "1101614008" == attrCode || "1101624008" == attrCode) {
	    var str = /^[a-zA-Z\u4E00-\uFA29]{1,20}$/;
	    if (!str.test(newValue)) {
	    	$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过20个字");
	    	$(e).val("");
	    	$(e).select();
	        return false;
	    }
	}
	
	if ("1101544027" == attrCode || "1101544028" == attrCode || "1101554027" == attrCode || "1101554028" == attrCode || "1101564027" == attrCode || "1101564028" == attrCode || "1101574027" == attrCode || "1101574028" == attrCode
			||"1101584027" == attrCode || "1101584028" == attrCode || "1101594027" == attrCode || "1101594028" == attrCode || "1101604027" == attrCode || "1101604028" == attrCode || "1101614027" == attrCode || "1101614028" == attrCode
			|| "1101624027" == attrCode || "1101624028" == attrCode ) {
		if (!g_IsTime(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
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
        	$(e).val("");
        	$(e).select();
            return false;
        }

    }
	//端口速率
	if ("1101574005" == attrCode) {
		if(parseInt(newValue)<1){
	        errinfo="短信端口速率不能最小为1!";
	        $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
			$(e).val("");
			$(e).select();
    	}
		//var custId = $.getSrcWindow().$("#CUST_ID").val();
		var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
    	var custId = groupInfo.get("CUST_ID");
		var serv_level = "";
		// 校验目的地
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify',
		'qryGrpClassId', '&CUST_ID=' + custId, function(d) {
			var param = d.map.result;
			serv_level = param.get(0, 'SERV_LEVEL');
			if (serv_level==null||serv_level==undefined) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
				$(e).val("");
				$(e).select();
			}

		}, function(e, i) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "查询不到集团服务等级信息,请先完善集团信息!");
			$(e).val("");
			$(e).select();
		}, {
			async : false
		});
	    if (serv_level=="1") //金牌级：<=2000
	    {
	    	if(parseInt(newValue)>2000){
		        errinfo="金牌级：2000!";
		        $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
	    	}
	    }
		if (serv_level=="3"||serv_level=="2") //铜牌、银牌级：<=100
		{
			if(parseInt(newValue)>100){
			    errinfo="铜牌、银牌级：最大短信端口速率不能超过100!";
			    $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
		if (serv_level=="4"||serv_level=="") //标准级：<=20
		{
			if(parseInt(newValue)>20){
			    errinfo="标准级：最大短信端口速率不能超过20!";
			    $.validate.alerter.one($(e)[0], $(e).attr('desc') + errinfo);
				$(e).val("");
				$(e).select();
			}
		}
	}

	checkBizCode(e,attrCode, newValue);


}

/** ***********************************托管式会议助理商品******************************************** */
function managedMeeting_onValueChange(e, attrCode, newValue) {

	// 校验签名长度是否符合规范，客户签名2-8个汉字
	if ("40401014001" == attrCode) {
		if (newValue.length > 8 || newValue.length < 2) {
			$.validate.alerter.one($(e)[0], "中文签名2~8个汉字");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	// EC短信基本接入号
	if ("40401014002" == attrCode) {
		// 数字验证
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要为8位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.length != 8) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要为8位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 每日下发的最大条数和每月下发的最大条数
	if ("40401014015" == attrCode || "40401014016" == attrCode) {
		var number = /^[0-9]{1,9}$/;
		if (!number.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("40401014016" == attrCode) {
			var dayNum=$('#input_B40401014015').val();
			if(newValue<dayNum){
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "月最大条数小于日最大条数,请重新填写");
				return false;
			}
	}
	if ("40401014015" == attrCode) {
			var dayNum=$('#input_B40401014016').val();
			if(newValue<dayNum){
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "月最大条数小于日最大条数,请重新填写");
				return false;
			}
	}
	
	
	// 时间格式：hhmmss
	if ("40401014017" == attrCode || "40401014018" == attrCode) {
		if (!g_IsTime(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("40401014017" == attrCode) {
        var result = compareDate($('#input_B40401014017').val(),$('#input_B40401014018').val());
        if (result>=0){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能大于等于结束时间！");
            $(e).val("");
            $(e).select();
            return false;
        }
	}
	if ("40401014018" == attrCode) {
        var result = compareDate($('#input_B40401014017').val(),$('#input_B40401014018').val());
        if (result>=0){
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "不能大于等于不允许下发结束时间！");
            $(e).val("");
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
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 英文签名不超过12个英文字符
	if ("40401014028" == attrCode) {
		var str = /^[a-zA-Z]{1,12}$/;
		if (!str.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过12位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 手机号码
	if ("40401014005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	if ("40401014002" == attrCode) {
		var filter = /^[0-9]{8,8}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为数字，并且必须为8位");
			$(e).val("");
			$(e).select();
			return false;
		}
		$('#input_B40401011019').val(newValue);
		$('#input_B40401011019').attr("disabled", true);
		$('#input_B40401014031').val(newValue);
		$('#input_B40401014031').attr("disabled", true);
	}
}

/** ***********************************手机报统付版商品************************************************* */
function mobiPaperAllPay_onValueChange(e, attrCode, newValue) {
	// 2~8个英文
	if ("40103010002" == attrCode || "40103020002" == attrCode
			|| "40103030002" == attrCode || "40103040002" == attrCode
			|| "40103050002" == attrCode || "40103060002" == attrCode
			|| "40103070002" == attrCode || "40103080002" == attrCode
			|| "40103090002" == attrCode || "40103100002" == attrCode
			|| "40103110002" == attrCode) {
		var str = /^[a-zA-Z]{2,8}$/;
		if (!str.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且长度为2~8位");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文!长度为2~8个汉字");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** ***************************************中央ADC商品******************************************** */

/*
 * 中央ADC业务（商品）--属性校验
 */
function checkCenterADC(e, attrCode, newValue) {

	// 手机号码
	if ("1102010003" == attrCode || "1102240003" == attrCode
			|| "208010003" == attrCode || "208020003" == attrCode
			|| "208030003" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!必须为11位手机号码!\n");
			//$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为英文签名!并且不能超过16位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC短信基本接入号---EC彩信基本接入号
	if ("1102011009" == attrCode || "1102241019" == attrCode
			|| "208011009" == attrCode || "208021009" == attrCode
			|| "208031009" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要填入5位数字");
			$(e).val("");
			$(e).select();
			return false;
		}

		// 110201
		$('#input_B1102012009').val(newValue);
		$('#input_B1102012009').attr("disabled", true);

		// 110224
		$('#input_B1102241025').val(newValue + '01');
		$('#input_B1102241025').attr("disabled", true);

		// 20801
		$('#input_B208011019').val(newValue);
		$('#input_B208011019').attr("disabled", true);

		$('#input_B208011025').val(newValue + '01');
		$('#input_B208011025').attr("disabled", true);

		$('#input_B208012019').val(newValue);
		$('#input_B208012019').attr("disabled", true);

		// 20802
		$('#input_B208021019').val(newValue);
		$('#input_B208021019').attr("disabled", true);

		$('#input_B208021025').val(newValue + '01');
		$('#input_B208021025').attr("disabled", true);

		$('#input_B208022019').val(newValue);
		$('#input_B208022019').attr("disabled", true);

		// 20803
		$('#input_B208031019').val(newValue);
		$('#input_B208031019').attr("disabled", true);

		$('#input_B208031025').val(newValue + '01');
		$('#input_B208031025').attr("disabled", true);

		$('#input_B208032019').val(newValue);
		$('#input_B208032019').attr("disabled", true);

	}

	// 数字验证
	if ("1102011028" == attrCode || "1102241028" == attrCode
			|| "208011028" == attrCode || "208021028" == attrCode
			|| "208031028" == attrCode) {
		if (!g_IsDigit(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须要为数字");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过8个字");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须9位以内纯数字");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		// 时间格式：hhmmss
		if (!g_IsTime(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的时间格式(HHMMSS)");
			$(e).val("");
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
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入过长 不能超过50个字或者100个字母");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC长服务代码
	if ("1102012009" == attrCode || "208012019" == attrCode
			|| "208022019" == attrCode || "208032019" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须为5位数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// EC彩信基本接入号
	if ("208011019" == attrCode || "208031019" == attrCode
			|| "208021019" == attrCode) {
		var number = /^[0-9]{5}$/;
		if (!number.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，同EC短信基本接入号相同!");
			$(e).val("");
			$(e).select();
			return false;
		}

	}

	// 彩信的短信上行点播码
	if ("1102241025" == attrCode || "208011025" == attrCode
			|| "208031025" == attrCode || "208021025" == attrCode) {
		var number = /^[0-9]{7}$/;
		if (!number.test(newValue) || newValue.substring(5, 7) != '01') {
            $.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "格式不正确，EC短信基本接入号＋01，即1065022XYABCDE01");
			$(e).val("");
			$(e).select();
			return false;
		}

	}

}

/** ***************************************双跨融合商品******************************************** */
/*
 * 双跨融合商品--属性变更校验
 */
function dbCroFus_onValueChange(e, attrCode, newValue) {
	// 集团管理员手机号码
	if ("200011801" == attrCode || "200021801" == attrCode
			|| "200031801" == attrCode || "200041801" == attrCode
			|| "200051801" == attrCode || "200061801" == attrCode
			|| "200071801" == attrCode || "200081801" == attrCode
			||"200091801"==attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 集团管理员邮箱
	if ("200011802" == attrCode || "200021802" == attrCode
			|| "200031802" == attrCode || "200041802" == attrCode
			|| "200051802" == attrCode || "200061802" == attrCode
			|| "200071802" == attrCode || "200011118" == attrCode
			|| "200071118" == attrCode || "200081802" == attrCode) {
		if (!g_ismail(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 最大成员数量
	if ("200011804" == attrCode || "200021804" == attrCode
			|| "200031804" == attrCode || "200041804" == attrCode
			|| "200051804" == attrCode || "200061804" == attrCode
			|| "200071804" == attrCode || "200081804" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.toString().length > 20) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "长度超出最大长度[20位]设置");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 固话功能费、手机功能费
	if ("200021871" == attrCode || "200021872" == attrCode
			|| "200051910" == attrCode || "200051911" == attrCode
			|| "200011130" == attrCode || "200011131" == attrCode
			|| "200011132" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).val("");
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
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 配合省
	if ("200041891" == attrCode) {
		selectChange(e, attrCode, '200041892', newValue);
	}

	if ("200021113" == attrCode) {
		if ("2" == newValue) {
			$('#input_B200021114').attr("disabled", true);
		}
	}

	// 成员号码校验
	var number = $('#input_B200021113').val();
	if ("200021114" == attrCode) {
		if ("1" == number) {
			if (!g_IsTelephoneNumber(newValue)) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc')
						+ "格式不正确！请输入正确电话号码格式!0+区号+本地固话形式");
				$(e).val("");
				$(e).select();
				return false;
			}
		} else if ("3" == number) {
			if (!g_IsMobileNumber(newValue)) {
				$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}

	if ("200011109" == attrCode) {
		if ("99" == newValue) {
			$('#input_B200011130').val("");
			$('#input_B200011130').attr("disabled", true);
			$('#input_B200011131').val("");
			$('#input_B200011131').attr("disabled", true);
		} else {
			$('#input_B200011130').val("");
			$('#input_B200011130').attr("disabled", false);
			$('#input_B200011131').val("");
			$('#input_B200011131').attr("disabled", false);
		}

		if ("11" == newValue || "01" == newValue || "12" == newValue) {
			$('#input_B200011132').val("");
			$('#input_B200011132').attr("disabled", false);
		} else {
			$('#input_B200011132').val("");
			$('#input_B200011132').attr("disabled", true);
		}
	}

	if ("200041133" == attrCode || "200041134" == attrCode) {
		if (g_IsDigit(newValue) == false || newValue * 1 > 100
				|| newValue * 1 < 0) {
			// 套餐折扣不正确
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "不正确,应为0-100的整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	return true;
}
/** *************************************全网IDC商品**************************************** */
/*
 * 全网IDC（产品）--属性变更校验
 */
function netIDC_onValueChange(e, attrCode, newValue) {
	if ("1117010502" == attrCode || "1117010202" == attrCode) {
		if ("0" == newValue) {
			$("#input_B1117010100").parent().parent().attr("class", "link required");
		} else {
			$("#input_B1117010100").parent().parent().attr("class", "link ");
		}
	}
}

/** *************************************短流程云MAS商品**************************************** */
function shortFlowCloudMAS(e, attrCode, newValue) {
	// 企业代码
	if ("1101490004" == attrCode || "1101503000" == attrCode
			|| "1101533000" == attrCode || "1101513000" == attrCode
			|| "1101523000" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}

    }

    if("1101470044" == attrCode){
        var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,20}$/;
        if (!filter.test(newValue)) {
            $.showErrorMessage($(e).attr('desc')
                    + "格式不正确,必须为中文，字母或者数字，但不能超过20位");
            $(e).val("");
            $(e).select();
            return false;
        }
	}
    if ("1101470043" == attrCode) {
        var filter = /^[a-zA-Z]{1,40}$/;
        if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过40位");
            $(e).val("");
            $(e).select();
            return false;
        }
    }
	// 业务代码
	if ("1101506201" == attrCode || "1101536201" == attrCode
			|| "1101516201" == attrCode || "1101526201" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 每月下发的最大条数(0代表不限制) --每日下发的最大条数(0代表不限制) --彩信端口速率(条/秒)--短信端口速率(条/秒)
	if ("1101492011" == attrCode || "1101490011" == attrCode
			|| "1101504302" == attrCode || "1101504303" == attrCode
			|| "1101534302" == attrCode || "1101534303" == attrCode
			|| "1101514302" == attrCode || "1101514303" == attrCode
			|| "1101524302" == attrCode || "1101524303" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		
		//REQ201910110008【集团总部需求】关于更新总部行业网关云MAS支撑实施方案的通知
		if ("1101492011" == attrCode || "1101490011" == attrCode) {
			if(Number(newValue)>10000){
				$.validate.alerter.one($(e)[0], "端口速率最大值10000.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}

	// EC管理员手机号 --调测期间联系电话
	if ("1101494306" == attrCode || "1101494216" == attrCode) {
		if (!g_IsTelephoneNumber(newValue) && !g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！请输入手机号格式或电话号码格式");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 短信基本接入号
	if ("1101491009" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
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
				$.validate.alerter.one($(e)[0], "短信基本接入号不正确，省公司订购校验：1065085/10650862/10693362开头，最短10位.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}else if (newValue.indexOf("1069") == 0 || newValue.indexOf("10650861") == 0) {
			if(newValue.length < 11){
				$.validate.alerter.one($(e)[0], "短信基本接入号不正确，省公司订购校验：1069/10650861开头，最短11位.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}else {
			$.validate.alerter.one($(e)[0], "短信基本接入号不正确，1069/10650861开头，最短11位,1065085/10650862/10693362，最短10位.");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
 // 短信基本接入号 短流程云MAS白名单号段
    if ("1101501001" == attrCode) {
        if (g_IsDigit(newValue) == false) {
            $.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
            $(e).val("");
            $(e).select();
            return false;
        }
        if (newValue.indexOf("1065093") != 0) {
            $.validate.alerter.one($(e)[0], "服务代码不正确，需要以1065093开头");
            $(e).val("");
            $(e).select();
            return false;
        }
    }
	// MMS基本接入号
	if ("1101491019" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
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
				$.validate.alerter.one($(e)[0], "MMS基本接入号不正确,省公司订购校验：10693362开头，最短10位.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}else if (newValue.indexOf("1069") == 0) {
			if(newValue.length < 11){
				$.validate.alerter.one($(e)[0], "MMS基本接入号不正确,省公司订购校验：1069开头，最短11位.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}else {
			$.validate.alerter.one($(e)[0], "MMS基本接入号不正确，1069开头，最短11位,10693362，最短10位.");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 产品110153服务代码校验
	if ("1101531001" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if (newValue.indexOf("1065099") != 0&&newValue.indexOf("1065094") != 0) {//短信基本接入号短流程云MAS白名单号段
			$.validate.alerter.one($(e)[0], "服务代码不正确，需要以1065099或1065094开头");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 产品110151 或 110152 产品服务代码校验
	if ("1101521011" == attrCode || "1101511001" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val();
			$(e).select();
			return false;
		}
		if (newValue.length > 20) {
			if ("1101511001" == attrCode) {
				$.validate.alerter.one($(e)[0], "短信基本接入号开头输入的数字最长不得超过20位");
			} else if ("1101521011" == attrCode) {
				$.validate.alerter.one($(e)[0], "MMS基本接入号开头输入的数字最长不得超过20位");
			}
			$(e).val();
			$(e).select();
			return false;
		}
	 if (newValue.indexOf("10692178") != 0||newValue.indexOf("10693169") != 0) {//验证1、新增短流程云MAS短彩信号段：10692178、10693169
            $.validate.alerter.one($(e)[0], "短信基本接入号不正确，需要以10692178或10693169开头");
            $(e).val("");
            $(e).select();
            return false;
        }
	}

	// 英文短信正文签名
	if ("1101500043" == attrCode || "1101530043" == attrCode
			|| "1101510043" == attrCode || "1101520043" == attrCode) {
		var filter = /^[a-zA-Z]{1,40}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，必须以英文字母且长度不超过40位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 中文短信正文签名
	if ("1101520044" == attrCode || "1101500044" == attrCode
			|| "1101510044" == attrCode || "1101530044" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,20}$/;
		if (!filter.test(newValue)) {
			$
					.showErrorMessage($(e).attr('desc')
							+ "格式不正确,必须为中文，字母或者数字，但不能超过20位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 中文短信正文签名
	if ("1101504200" == attrCode || "1101534200" == attrCode
			|| "1101514200" == attrCode || "1101524200" == attrCode) {
		var filter = /^[a-zA-Z0-9\u4E00-\uFA29]{1,20}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文，字母或者数字，但不能超过20位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

}

/** *************************************长流程云MAS（商品）**************************************** */
function longCloudMAS_onValueChange(e, attrCode, newValue) {
	// 企业代码
	if ("1101450004" == attrCode) {
		var filter = /^[0-9]{6,6}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为6位有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
	//REQ201910110008【集团总部需求】关于更新总部行业网关云MAS支撑实施方案的通知
	if ("1101452011" == attrCode || "1101450011" == attrCode) {
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], "请确保所有字符都为有效数字");
			$(e).val("");
			$(e).select();
			return false;
		}
		if ("1101452011" == attrCode || "1101450011" == attrCode) {
			if(Number(newValue)>10000){
				$.validate.alerter.one($(e)[0], "端口速率最大值10000.");
				$(e).val("");
				$(e).select();
				return false;
			}
		}
	}
}

/** *************************************139企业邮箱（商品）**************************************** */
/*
 * 139邮箱（产品）--属性变更校验
 */
function mail139_onValueChange(e, attrCode, newValue) {

	// 购买后成员总数
	if ("30102010001" == attrCode) {
		if (newValue < 5 || newValue == 5 || (newValue % 5) != 0) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "输入不正确！请输入不能为5且是5的倍数 ");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}

	// 联系人邮箱
	if ("30102010003" == attrCode) {
		// 正则表达式验证邮箱格式
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！");
			$(e).val("");
			$(e).select();
			return false;
		}
		return true;
	}
	// 手机号码
	if ("30102010004" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的手机号！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
}

/** ****************************************企业彩漫商品******************************************* */
function checkCompanyCartoonMMS(e, attrCode, newValue) {
	// 手机号码
	if ("40106010005" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须为11位的手机号码！");
			$(e).select();
			return false;
		}
	}

	// 邮箱
	if ("40106010004" == attrCode) {
		if (!g_ismail(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	// 服务代码
	if ("40106010006" == attrCode) {
		// 检查长服务代码是否为数字
		if (g_IsDigit(newValue) == false) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，请确保所有字符都为有效数字");
			$(e).select();
			return false;
		}

		// 检查序列号位数是否正确
		if (newValue.toString().length != 6) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "为6位数字");
			$(e).select();
			return false;
		}
	}
	// 中文签名
	if ("40106010001" == attrCode) {
		var str = /^[a-zA-Z0-9]{1,8}$/;
		if (!str.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文或英文!长度不能超过8个字");
			$(e).select();
			return false;
		}
	}

	// 英文签名
	if ("40106010002" == attrCode) {
		var str = /^[a-zA-Z0-9\u4E00-\uFA29]{1,8}$/;
		if (!str.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc')
					+ "格式不正确,必须为英文或者英文与数字混合!长度不能超过8位");
			$(e).select();
			return false;
		}
	}

}
/** ****************************************本地IDC******************************************* */
function checkLocalhostIDCParam(e, attrCode, newValue) {
    //带宽条数
	if ("50005010002" == attrCode) {
		if (!g_IsDigit(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为纯数字正整数");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
	//单价（元/月/带宽）  数字，单位：元
	if ("50005010003" == attrCode) {
		  var exp = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
		  if (!exp.test(newValue)) { 
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为正确的金额");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
	//上浮比例（%）  大于20的正整数
	if ("50005010004" == attrCode) { 
		if (!g_IsDigit(newValue)) { 
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为大于20的正整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		if(parseInt(newValue)<=20){
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为大于20的正整数");
			$(e).val("");
			$(e).select();
			return false;
		}
	}
	
}
/** ****************************************本地IDC******************************************* */


/** ****************************************省网关全网长流程云MAS商品******************************************* */
function checkGatewayCloudMASParam(e, attrCode, newValue) {
	// 短信端口速率（条/秒）
	if ("1101630011" == attrCode || "1101631019" == attrCode
			|| "1101632011" == attrCode || "1101634009" == attrCode
			|| "1101564019" == attrCode || "1101574005" == attrCode
			|| "1101584005" == attrCode || "1101594005" == attrCode
			|| "1101604005" == attrCode || "1101614005" == attrCode
			|| "1101624005" == attrCode || "1101624019" == attrCode
			|| "1101594019" == attrCode || "1101644009" == attrCode 
			|| "1101644010" == attrCode){ 
		if (!g_IsDigit(newValue)){ 
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确，应为纯数字正整数");
			$(e).val("");
			$(e).select();
			return false;
		}
		//省公司全网长流程局数据、省行业网关全网短流程视频短信、省行业网关省内网信视频短信、省行业网关省内局数据4个产品的短信端口速率、彩信端口速率最大值由“2000”调整为“10000”；
		if("1101630011"==attrCode || "1101632011" == attrCode || "1101644009" == attrCode || "1101644010" == attrCode|| "1101584005" == attrCode || "1101614005" == attrCode){
			if (newValue>10000||(newValue)%5!=0){
				$.validate.alerter.one($(e)[0],$(e).attr('desc')+"短/彩信端口速率必须是5的倍数且不能超过10000");
				$(e).val("");    
				$(e).select();					
				return false;
			}
		}
	}

	// 中文短信正文签名
	if ("1101544004" == attrCode || "1101554004" == attrCode
			|| "1101564004" == attrCode || "1101574008" == attrCode
			|| "1101584008" == attrCode || "1101594008" == attrCode
			|| "1101604008" == attrCode || "1101614008" == attrCode
			|| "1101624008" == attrCode) {
		var filter = /^[\u4E00-\uFA29]{1,20}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须为中文,但不能超过20位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 英文短信正文签名
	if ("1101544005" == attrCode || "1101554005" == attrCode
			|| "1101564005" == attrCode || "1101574009" == attrCode
			|| "1101584009" == attrCode || "1101594009" == attrCode
			|| "1101604009" == attrCode || "1101614009" == attrCode
			|| "1101624009" == attrCode) {
		var filter = /^[a-zA-Z0-9]{1,40}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须字母或者数字，但不能超过40位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 服务代码
	if ("1101544006" == attrCode || "1101554006" == attrCode
			|| "1101564006" == attrCode || "1101574002" == attrCode
			|| "1101584002" == attrCode || "1101594002" == attrCode
			|| "1101604002" == attrCode || "1101614002" == attrCode
			|| "1101624002" == attrCode) {
		var filter = /^[0-9]{1,21}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,必须数字，但不能超过21位");
			$(e).val("");
			$(e).select();
			return false;
		}
		if("1101574002"==attrCode){
			if(newValue.indexOf("1065096")!=0&& newValue.indexOf("1065097")!=0&& newValue.indexOf("1065090")!=0
					&& newValue.indexOf("1065091")!=0 && newValue.indexOf("1065080")!=0 && newValue.indexOf("1065081")!=0){
				$.validate.alerter.one($(e)[0],"服务代码不正确，需要以1065096或1065097或1065090或1065091或1065080或1065081开头");
				$(e).val("");    
				$(e).select();
				return false;
			}
			//如果以服务代码为1065096，1065090，1065080开头，系统默认“白名单”；为1065097，1065091，1065081开头，系统默认“黑名单”。不允许修改
			if((newValue.length > 7 && newValue.substring(0, 7)=="1065096")||(newValue.length > 7 && newValue.substring(0, 7)=="1065090")
					||(newValue.length > 7 && newValue.substring(0, 7)=="1065080")) {
				$('#BSEL1101574011').val("2");
				$('#BSEL1101574011').change();
				$('#BSEL1101574011').attr("disabled", true);
			} else if((newValue.length > 7 && newValue.substring(0, 7)=="1065097")||(newValue.length > 7 && newValue.substring(0, 7)=="1065091")
					||(newValue.length > 7 && newValue.substring(0, 7)=="1065081")) {
				$('#BSEL1101574011').val("0");
				$('#BSEL1101574011').change();
				$('#BSEL1101574011').attr("disabled", true);
			} 
		}
		
		if("1101584002"==attrCode ){//2.2 修改

			if(newValue.indexOf("1065096")!=0&& newValue.indexOf("1065097")!=0&& newValue.indexOf("1065090")!=0
					&& newValue.indexOf("1065091")!=0 && newValue.indexOf("1065080")!=0 && newValue.indexOf("1065081")!=0&&newValue.indexOf("10650871")!=0&& newValue.indexOf("10650872")!=0&& newValue.indexOf("10650873")!=0){
				$.validate.alerter.one($(e)[0], "服务代码不正确，需要以1065096或1065097或1065090或1065091或1065080或1065081或10650871或10650872或10650873开头");
				$(e).val("");
				$(e).select();
				return false;
			}
			//如果以服务代码为1065096，1065090，1065080开头，系统默认“白名单”；为1065097，1065091，1065081开头，系统默认“黑名单”。不允许修改
			if((newValue.length > 7 && newValue.substring(0, 7)=="1065096")||(newValue.length > 7 && newValue.substring(0, 7)=="1065090")
					||(newValue.length > 7 && newValue.substring(0, 7)=="1065080")) {
				$('#BSEL1101584011').val("2");
				$('#BSEL1101584011').change();
				$('#BSEL1101584011').attr("disabled", true);
			} else if((newValue.length > 7 && newValue.substring(0, 7)=="1065097")||(newValue.length > 7 && newValue.substring(0, 7)=="1065091")
					||(newValue.length > 7 && newValue.substring(0, 7)=="1065081")) {
				$('#BSEL1101584011').val("0");
				$('#BSEL1101584011').change();
				$('#BSEL1101584011').attr("disabled", true);
			}
		}
		if("1101594002"==attrCode){
			if(newValue.indexOf("1065096")!=0&&newValue.indexOf("1065090")!=0&&newValue.indexOf("1065080")!=0){
				$.validate.alerter.one($(e)[0],"服务代码不正确，需要以1065096或1065090或1065080开头");
				$(e).val("");    
				$(e).select();
				return false;
			}
		}
	}

	// 业务代码
	if ("1101544007" == attrCode || "1101554007" == attrCode
			|| "1101564007" == attrCode || "1101574010" == attrCode
			|| "1101584010" == attrCode || "1101604010" == attrCode
			|| "1101614010" == attrCode || "1101624010" == attrCode) {
		var filter = /^[a-zA-Z0-9]{1,10}$/;
		if (!filter.test(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为字母或数字，但不能超过10位");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 集团客户管理员手机号
	if ("1101574004" == attrCode || "1101584004" == attrCode
	        || "1101594004" == attrCode|| "1101614004" == attrCode
	        || "1101624004" == attrCode) {
		if (!g_IsMobileNumber(newValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确！必须为11位的手机号码！");
			$(e).val("");
			$(e).select();
			return false;
		}
	}

	// 黑名单选择联动
	if ("1101544008" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101544009").parent().parent().parent().attr("class", "link required");
			$('#input_B1101544009').attr("nullable", "no");
		} else {
			$("#BSEL1101544009").parent().parent().parent().attr("class", "link ");
			$('#input_B1101544009').attr("nullable", "yes");
		}
	}

	if ("1101554008" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101554009").parent().parent().parent().attr("class", "link required");
			$('#input_B1101554009').attr("nullable", "no");
		} else {
			$("#BSEL1101554009").parent().parent().parent().attr("class", "link ");
			$('#input_B1101554009').attr("nullable", "yes");
		}
	}


	if ("1101604011" == attrCode) {
		if ("2" == newValue) {
			$("#BSEL1101604012").parent().parent().parent().attr("class", "link required");
			$('#input_B1101604012').attr("nullable", "no");
		} else {
			$("#BSEL1101604012").parent().parent().parent().attr("class", "link ");
			$('#input_B1101604012').attr("nullable", "yes");
		}
	}

}
/*********************企业互联网电视 start********************/
/**
 * 参数校验
 */
function checkEnterpriseInternetTVAttr(e, attrCode, newValue) {

	//SP企业代码 SP业务代码
    if("40109010001"==attrCode||"40109010002"==attrCode
		||"40109020001"==attrCode||"40109020002"==attrCode
        ||"40109030001"==attrCode||"40109030002"==attrCode
        ||"40109040001"==attrCode||"40109040002"==attrCode){
        var filter = /^[a-zA-Z0-9]{1,12}$/;
        if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为字母或数字，但不能超过12位");
            $(e).val("");
            $(e).select();
            return false;
        }
    }

    //SP业务名称
    if("40109010003"==attrCode||"40109020003"==attrCode
	||"40109030003"==attrCode||"40109040003"==attrCode){
        var filter = /^[a-zA-Z0-9]{1,40}$/;
        if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为字母或数字，但不能超过40位");
            $(e).val("");
            $(e).select();
            return false;
        }
    }

    //订购归属区域邮政编码
    if("40109010009"==attrCode||"40109020009"==attrCode){
        var filter = /^[0-9]{1,6}$/;
        if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为数字，但不能超过6位");
            $(e).val("");
            $(e).select();
            return false;
        }
    }

    //折扣比例
    if("40109010010"==attrCode||"40109020010"==attrCode
	||"40109040010"==attrCode){
        var filter = /^[0-9]{2,3}$/;
        if (!filter.test(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为2-3位数字");
            $(e).val("");
            $(e).select();
            return false;
        }else{
            if(newValue>100||newValue<50){
                $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须在50-100内");
                $(e).val("");
                $(e).select();
                return false;
            }
        }
    }

    //集团客户联系人电话
    if("40109010013"==attrCode||"40109020013"==attrCode
	||"40109030013"==attrCode||"40109040013"==attrCode){
        if (!g_IsMobileNumber(newValue)) {
            $.validate.alerter.one($(e)[0], $(e).attr('desc') + "格式不正确,须为11位移动手机号码");
            $(e).val("");
            $(e).select();
            return false;
        }
    }
}
/*********************企业互联网电视 end********************/
/** ***************************************基础校验方法******************************************** */
/**
 * 获取bboss序列
 */
function getBbossSeqId(seqName) {
	var bbossSeq="";
	//取后台序列
	  Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getBbossSeqId',
	            '&SEQ_NAME='+seqName, function(d) {
		  		bbossSeq= d.map.result;
	            }, function(e, i) {
	                $.validate.alerter.one($(e)[0], "操作失败");
	                return false;
	            },{async:false});
	 return  bbossSeq ;
}

// 校验是否为数字类型
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

// 校验需要带小数点的数字
function g_IsDigitWithPoint(s, pointLength) {
	if (s == null) {
		return false;
	}
	if (s == '') {
		return true;
	}
	s = '' + s;
	var attrValues = s.split(".");
	if (attrValues.length == 1) {
		return g_IsDigit(s);
	} else if (attrValues.length == 2) {
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

// 校验是否为手机号码
function g_IsMobileNumber(s) {
	if (s == null || s == '') {
		return true;
	}
	if (s.length != 11
			|| (s.substring(0, 2) != '13' && s.substring(0, 2) != '15'
					&& s.substring(0, 3) != '147' && s.substring(0, 3) != '186'
					&& s.substring(0, 3) != '189' && s.substring(0, 3) != '188'
					&& s.substring(0, 3) != '187' && s.substring(0, 3) != '182'
					&& s.substring(0, 3) != '183' && s.substring(0, 3) != '180')) {
		return false;
	}
	if (!g_IsDigit(s)) {
		return false;
	}
	return true
}

// 校验是否为时间格式：hhmmss
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

function selectChange(e, attrCode, subAttrCode, newValue) {
	//debugger;
	// 如果父节点是请选择的情况下，则子节点对应的组件设置成不可编辑的状态，否则可编辑
	if (newValue == '' || newValue == null) {
		$('#BSEL' + subAttrCode).attr('value', '');
		//changeValue($('#BSEL' + subAttrCode)[0]);
		$('#BSEL' + subAttrCode).attr('disabled', true);
		return true;
	} else {
		$('#BSEL' + subAttrCode).attr('disabled', false);
	}

	
	// 根据选择的省公司获取该省公司对应的城市
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify', 'chooseCitys',
			'&PROVINCE_ATTR_CODE=' + attrCode + '&PROVINCE_ATTR_VALUE=' + newValue
					+ '&CITY_ATTR_CODE=' + subAttrCode, function(d) {
			var boundData = d.map.result;
			var _index = -1;

			// 拼下拉框对象
			var ulObj = $("#BSEL"+subAttrCode+"_float ul");
			var innerObj = '<li class="link on" idx="0" title="--请选择--" val=""><div class="main">--请选择--</div></li>';
			for (var i = 1; i <= boundData.length; i++) {
				innerObj = innerObj + '<li class="link" idx="' + i + '" title="'
					+ boundData.get(i-1, 'OPTION_NAME')
					+ '" val="' + boundData.get(i-1, 'OPTION_VALUE') + '"><div class="main">'
					+ boundData.get(i-1, 'OPTION_NAME') + '</div></li>';
			}

			// 新option对象替换老对象
			$('#BSEL' + subAttrCode).attr('value', '');
			$('#BSEL' + subAttrCode).val('');
			ulObj.html(innerObj);
			//清空text
			$('#BSEL' + subAttrCode+'_text').html('--请选择--');

		}, function(e, i) {
				$.validate.alerter.one($(e)[0], "操作失败");
				$(e).select();
				return false;
			}, {
			async : false
		}
	);
}

function changeValue(tag) {
	var class_type = tag.type;
	if ('text' != class_type) {
		tag.parentNode.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	} else {
		tag.parentNode.parentNode.parentNode.parentNode.cells[3].innerText = tag.value;
	}
}

function byteLength(str) {
	var byteLen = 0, len = str.length;
	if (!str)
		return 0;
	for (var i = 0; i < len; i++)
		byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
	return byteLen;
}

function submitBbossInfo(el) {
	if(submitOfferChaSpec(el.getAttribute('PROD_SPEC_ID'))) {
		backPopup(el);
	}
}
/**
 * 根据serv_type去修改bizCode,业务类型 占据业务编码的第七八位
 */
function chgBizCode(e,paramCode, attrValue)
{
	if(''==paramCode){
		return;
	}
	var bizeCodeAttrCode=getBizCodeAttrCode(paramCode.trim().substring(0,6));
	if(''==bizeCodeAttrCode){
		return;
	}
	var bizCode=$('#input_B'+bizeCodeAttrCode).val().trim();
	if(bizCode.length==0){
		alert("请先填写相应的业务代码");
		$("#BSEL"+paramCode).val("");
	}
	if(bizCode.length<8){
		return;
	}
	var servTypeCode=getServTypeCode(attrValue);
	if(""==servTypeCode){
		return;
	}
	var newBizCode=replaceStrAt(bizCode,6,8,getServTypeCode(attrValue.trim()));
	$('#input_B'+bizeCodeAttrCode).val(newBizCode);
}
function replaceStrAt(sourceStr,start,end,newStr){
	var frontStr=sourceStr.substring(0,start);
	var afterStr=sourceStr.substring(end,sourceStr.length);
	return frontStr+newStr+afterStr;
}
function getServTypeCode(servType){
	if("内部管理类"==servType){
		return "51";
	}else if("外部服务类"==servType){
		return "52";
	}else if("营销推广类"==servType){
		return "53";
	}else if ("公益类"==servType){
		return "54";
	}else{
		return "";
	}
}
function getBizCodeAttrCode(offerId){
	if('110154'==offerId){
		return '1101544007';
	}else if('110155'==offerId){
		return '1101554007';
	}else if('110156'==offerId){
		return '1101564007';
	}else if ('110157'==offerId){
		return '1101574010';
	}else if ('110158'==offerId){
		return '1101584010';
	}else if ('110159'==offerId){
		return '1101594010';
	}else if ('110160'==offerId){
		return '1101604010';
	}else if ('110161'==offerId){
		return '1101614010';
	}else if ('110162'==offerId){
		return '1101624010';
	}else {
		return '';
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
			$.validate.alerter.one($(e)[0],$(e).attr('desc') + "格式不正确!必须为11位手机号码!");
			$(e).select();
			return false;
		}
	}

	
	//邮箱验证
	if ("50008010003" == attrCode) {
		if (!g_ismail(newValue)) {
			$.validate.alerter.one($(e)[0],$(e).attr('desc') + "格式不正确，请输入正确的邮箱！");
			$(e).select();
			return false;
		}
	}
	
	//使用云存储路数验证 云存储套餐为“0”时，路数必需为“0”；其它值时系统校验，云存储路数不能超过总路数且不为0；手工录入，计费时使用
	if ("50008010007" == attrCode) {
		var val50008010006 = $("#input_B50008010006").val();
		var val50008010005 = $("#input_B50008010005").val();
		if(val50008010006 == "0"){
			if(newValue != 0){
				$.validate.alerter.one($(e)[0],$(e).attr('desc') + "云存储套餐为“0”时，路数必需为“0”！");
				$(e).select();
				return false;
			}
		}else{
			if (parseInt(val50008010005) < newValue) {
				$.validate.alerter.one($(e)[0],$(e).attr('desc') + "云存储路数不能超过总路数且不为0！");
				$(e).select();
				return false;
			}
		}
	}

}
/*
 * 千里眼小微版业务
 */
function clairvoyance_onValueChange(e,attrCode,oldValue,attrValue){
	//集团客户联系人邮箱
	if ("50008020003" == attrCode) {
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(attrValue)) {
			$.validate.alerter.one($(e)[0],$(e).attr('desc')+"格式不正确！");
			$(e).val("");    
			$(e).select();
			return false;
		}
	}
	//集团客户联系人电话
	if ("50008020002" == attrCode) {
		if (!g_IsMobileNumber(attrValue)) {
			$.validate.alerter.one($(e)[0], $(e).attr('desc')+"格式不正确！请输入11位手机号");
			$(e).val("");    
			$(e).select();
			return false;
		}
	}
	
	//总路数正整数校验
	if ("50008020005" == attrCode) {
		var filter = /^[1-9]\d*$/;
		if (!filter.test(attrValue)) {
			$.validate.alerter.one($(e)[0],$(e).attr('desc')+"格式不正确！请输入正整数");
			$(e).val("");    
			$(e).select();
			return false;
		}
		//使用云存储路数
 		var val50008020007 = $('#input_50008020007').val();
 		var val50008020007Num = Number(val50008020007);
 		//云存储套餐
 		var val50008020006 = $('#input_50008020006').val();
 		var val50008020006Num = Number(val50008020006);
 		if(!isEmpty(val50008020006)&&!isEmpty(val50008020007)){
 			if(val50008020006Num == 0){
 	 			if(val50008020007Num == 0){
 	 				return true;
 	 			}
 	 			$.validate.alerter.one($(e)[0],"云存储套餐为0时，使用云存储路数必需为0");
 	 			$(e).val("");    
 	 			$(e).select();
 	 			return false;
 	 		}
 	 		
 	 		if(val50008020007Num != 0 && val50008020007Num <= Number(attrValue)){
 	 			return true;
 	 		}
 	 		$.validate.alerter.one($(e)[0],"使用云存储路数不能超过总路数且不为0");
 	 		$(e).val("");    
 			$(e).select();
 	 		return false;
 		}
	}
	//云存储套餐
	if ('50008020006' == attrCode){
		//总路数
		var val50008020005 = $('#input_B50008020005').val();
		var val50008020005Num = Number(val50008020005);
		//使用云存储路数
		var val50008020007 = $('#input_B50008020007').val();
		var val50008020007Num = Number(val50008020007);
		
		if(!isEmpty(val50008020005)&&!isEmpty(val50008020007)){
			if(attrValue == '0'){
				if(val50008020007Num == 0){
					return true;
				}
				$.validate.alerter.one($(e)[0],"云存储套餐为0时，使用云存储路数必需为0");
				$(e).val("");    
				$(e).select();
				return false;
			}
			
			if(val50008020007Num != 0 && val50008020007Num <= val50008020005Num){
				return true;
			}
			$.validate.alerter.one($(e)[0],"使用云存储路数不能超过总路数且不为0");
			$(e).val("");    
			$(e).select();
			return false;
		}
	}
	//使用云存储路数
	if ('50008020007' == attrCode){
		var filter = /^(0|[1-9][0-9]*)$/;
		if (!filter.test(attrValue)) {
			$.validate.alerter.one($(e)[0],$(e).attr('desc')+"格式不正确！请输入整数");
			$(e).val("");    
			$(e).select();
			return false;
		}
		//总路数
		var val50008020005 = $('#input_B50008020005').val();
		var val50008020005Num = Number(val50008020005);
		//云存储套餐
		var val50008020006 = $('#input_B50008020006').val();
		var val50008020006Num = Number(val50008020006);
		
		if(!isEmpty(val50008020005)&&!isEmpty(val50008020006)){
			if(val50008020006Num == 0){
				if(attrValue == '0'){
					return true;
				}
				$.validate.alerter.one($(e)[0],"云存储套餐为0时，使用云存储路数必需为0");
				$(e).val("");    
				$(e).select();
				return false;
			}
			
			if(Number(attrValue) != 0 && Number(attrValue) <= val50008020005Num){
				return true;
			}
			$.validate.alerter.one($(e)[0],"使用云存储路数不能超过总路数且不为0");
			$(e).val("");    
			$(e).select();
			return false;
		}
	}
	return true;
}

/**
 * @param null
 * @returns void 设置省行业网关短流程云MAS服务代码
 */
function setServCode(){
    var nowProdOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
    var offerId = nowProdOfferData.get("OFFER_CODE");
    var productOpType = nowProdOfferData.get("MERCHP_OPER_TYPE");
  // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接退出
  if(productOpType != "1" && productOpType != "10"){
	  return false;
  }
/*	  // 1- 获取当前的产品编号(全网产品编号)
    var allNetProductId = "";
	 Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMerchPId',
	            '&ALLNET_PRODUCT_ID='+offerId, function(d) {
		 allNetProductId = d.map.result;
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            },{async:false});*/
  var typecode = $("#BSEL1101574011").val();
  var servCode = getRandom(typecode);
  $("#input_B1101574002").val(servCode);
  //changeValue($('#input_B1101574002')[0]);
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
	 Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMasServCodeByParam',
	            '&SERV_CODE=' + servCode, function(d) {
				    if(d.result == "1"){
				    	getRandom(typecode);
				    }
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            });

  return servCode;
}

//校验业务代码
function checkBizCode(e,attrCode, newValue) {

    if ("1101544007" == attrCode || "1101554007" == attrCode || "1101564007" == attrCode || "1101574010" == attrCode || "1101584010" == attrCode || "1101594010" == attrCode || "1101604010" == attrCode || "1101614010" == attrCode || "1101624010" == attrCode) {
        if (newValue.length != 10) {
        	$.validate.alerter.one($(e)[0],"业务代码长度必须为10位");
        	$(e).val("");
        	$(e).select();
            return false;
        }

    }

    if ("1101544007" == attrCode || "1101554007" == attrCode || "1101574010" == attrCode || "1101584010" == attrCode || "1101604010" == attrCode || "1101614010" == attrCode) {
        //校验业务代码 必须为字母数字的组合
        var masreg = /[\W]/g;
        var masrst = newValue.match(masreg); // 在字符串 s 中查找匹配。
        if (masrst != null) {
        	$.validate.alerter.one($(e)[0],"业务代码只能为字母或者数字,或者字母和数字的组合!");
        	$(e).val("");
        	$(e).select();
            return false;
        }

    } else if ("1101564007" == attrCode || "1101594010" == attrCode || "1101624010" == attrCode) {
        var numberre = /[^0-9]/g;
        var bizcodecheck = newValue.match(numberre); // 在字符串 s 中查找匹配。
        if (bizcodecheck != null) {
        	$.validate.alerter.one($(e)[0],"彩信业务的业务代码必须为全数字!");
        	$(e).val("");
        	$(e).select();
            return false;
        }

    }
    return true;
}

function dealbaseinCodeputAfter(data, allNetProductId) {
    var ecBaseInCode = data.get("EC_BASE_IN_CODE", "");
    var ecBaseInCodea = data.get("EC_BASE_IN_CODE_A", "");
    if (ecBaseInCode == '' || ecBaseInCode == '[]') {
    	MessageBox.alert("提示","该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!");
    }
    if ("110164" == allNetProductId) {
        if (ecBaseInCodea == '01') {
            $("#input_B1101644005").val(ecBaseInCode);
            //changeValue($('#input_1101644005')[0]);
            $('#input_B1101644005').attr('disabled', true);

        } else {
            $("#input_B1101644007").val(ecBaseInCode);
            //changeValue($('#input_1101644007')[0]);
            $('#input_B1101644007').attr('disabled', true);

        }

        $("#input_B1101644002").val(data.get("SP_CODE", ""));
        //changeValue($('#input_B1101644002')[0]);
        $('#input_B1101644002').attr('disabled', true);
    } else if ("110163" == allNetProductId) {
        if (ecBaseInCodea == '01') {
            $("#input_B1101631009").val(ecBaseInCode);
            //changeValue($('#input_B1101631009')[0]);
            $('#input_B1101631009').attr('disabled', true);

        } else {
        	if (ecBaseInCode.length>20){//省公司全网长流程局数据、省行业网关省内局数据中“短信基本接入号”最大长度由21位变更为20位
                MessageBox.alert("提示","省公司全网长流程局数据中“短信基本接入号”最大长度为20位");
            }
            $("#input_B1101631019").val(ecBaseInCode);
            //changeValue($('#input_B1101631019')[0]);
            $('#input_B1101631019').attr('disabled', true);

        }

        $("#input_B1101630004").val(data.get("SP_CODE", ""));
        //changeValue($('#input_B1101630004')[0]);
        $('#input_B1101630004').attr('disabled', true);

    } else if (allNetProductId == "110157" || allNetProductId == "110158" || allNetProductId == "110159") {
        $("#input_B" + allNetProductId + "4002").val(ecBaseInCode);
        //changeValue($("#input_" + allNetProductId + "4002")[0]);
        $("#input_B" + allNetProductId + "4002").attr('disabled', true);
        // 服务代码为1065096开头，系统默认“白名单”；为1065097开头，系统默认“黑名单”。不允许修改
        if ("110157" == allNetProductId) {
            if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509622")||ecBaseInCode.substring(0, 9) == "106509652"
            	||ecBaseInCode.substring(0, 9) == "106509022"||ecBaseInCode.substring(0, 9) == "106509052") {
                $('#input_B1101574011').val("2");
                //$('#input_B1101574011').change();
                $('#input_B1101574011').attr("disabled", true);
                //$('#PARAM_NAME_1101574012').removeClass("e_required");
                $('#input_B1101574012').attr("nullable", "yes");

            } else if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509722"||ecBaseInCode.substring(0, 9) == "106509752"||ecBaseInCode.substring(0, 9) == "106509122"||ecBaseInCode.substring(0, 9) == "106509152")) {
                $('#input_B1101574011').val("0");
                //$('#input_B1101574011').change();
                $('#input_B1101574011').attr("disabled", true);
                //$('#PARAM_NAME_1101574012').attr("class", "e_required");
                $('#input_B1101574012').attr("nullable", "no");
            } else {
    	    	MessageBox.alert("提示","省行业网关短流程服务代码必须以白名单1065096-XY-ABC，1065090-XY-ABC或者黑名单1065097-XY-ABC，1065091-XY-ABC(xy的校验规则请参照枚举对应关系)开头!");

            }
        }

    }
    return;
}


//基本接入号、服务代码字段加强校验
function checkFwdmCode(e,attrCode, newValue) {
    if ("1101574002" == attrCode || "199011029" == attrCode || "199011009" == attrCode || "1105011009" == attrCode
    		|| "1105012009" == attrCode || "1101631009" == attrCode || "1101631019" == attrCode || "1101544006" == attrCode
    		|| "1101564006" == attrCode || "1101554006" == attrCode || "1101594002" == attrCode
    		|| "1101584002" == attrCode || "1101644005" == attrCode || "1101644007" == attrCode || "1101604002" == attrCode
    		|| "1101624002" == attrCode ) {
        var numberre = /[^0-9]/g;
        var bizcodecheck = newValue.match(numberre); // 在字符串 s 中查找匹配。
        if (bizcodecheck != null) {
        	$.validate.alerter.one($(e)[0],"基本接入号、服务代码字段仅支持填写数字。");
        	$(e).val("");
        	$(e).select();
            return false;
        }

    }
    return true;
}