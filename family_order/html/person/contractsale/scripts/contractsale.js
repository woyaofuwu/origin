var elements = $.DatasetList();
var product = $.DataMap();
var consumeLimitElement = $.DataMap();

function refreshPartAtferAuth(data)
{
	clearContractSaleInfo();
	$('#RES_CHECK_FLAG').val("false");
	$('#CAN_SUBMIT_FLAG').val("false");
	
	$('#goods_RES_CODE').attr('disabled', false);
	
	$("#checkResButton").unbind("click");
	$("#checkResButton").bind("click", checkResInfo);
	$('#checkResIco').attr('class','e_ico-check');
	$('#checkResButtonName').html('校验');
	
	//清空界面的值
	$('#goods_RES_CODE').val('');
	$('#CONTRACT_ID').val('');
	$('#ContractDetailPart').css('display', 'none');
	$('#DEVICE_MODEL').text('');
	
	$('#CONTRACT_PRICE_DESC_PART').css('display', 'none');
	$('#CONTRACT_PRICE_DESC').text('');
	
}

function displaySwitch(btn,o) {
	var button = $(btn);
	var div = $('#'+o);

	if (div.css('display') != "none")
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold'); 
		button.children("span:first").text("展示客户信息");
	}
	else {
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold'); 
		button.children("span:first").text("不展示客户信息");
	}
}

function checkResInfo() {
	$('#DEVICE_MODEL').text('');
	var resCodeObj = $("#goods_RES_CODE");
	var authData = $.auth.getAuthData();
	if($("#SALE_STAFF_ID").val() == "")
	{
		alert("请输入促销员工");
		return false;
	}
	if(resCodeObj.val() == "")
	{
		alert("请输入串码");
		return false;
	}
	$('#RES_CHECK_FLAG').val("false");
	$('#CAN_SUBMIT_FLAG').val("false");

	var iResId = resCodeObj.attr("resId");
	var param = '&RES_CODE='+resCodeObj.val();
	param += "&STAFF_ID="+$("#SALE_STAFF_ID").val();
	param += "&SERIAL_NUMBER=" + authData.get('USER_INFO').get('SERIAL_NUMBER');
	$.beginPageLoading("终端校验..");
	$.ajax.submit(null, 'checkResInfo', param, '', refreshContract, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			$.showErrorMessage('终端校验失败',errorinfo);
		});
	
}

function refreshContract(ajaxData)
{
	$.endPageLoading();
	var xResultCode	= ajaxData.get('X_RESULTCODE', '-1');
	var xResultInfo	= ajaxData.get('X_RESULTINFO', '校验失败!');
	if (xResultCode != '0') {
		alert(xResultInfo);
		return false;
	}
	var param = "";
	var authData = $.auth.getAuthData();
	param += "&RES_TYPE=" + ajaxData.get('DEVICE_MODEL_CODE');
	param += "&EPARCHY_CODE=" + authData.get('USER_INFO').get('EPARCHY_CODE');
	$.ajax.submit(null, 'queryContractBycode', param, 'ContractPart');
	
	$('#DEVICE_MODEL_CODE').val(ajaxData.get('DEVICE_MODEL_CODE'));
	
	$('#goods_RES_CODE').attr('disabled', true);
	$("#checkResButton").unbind("click");
	$("#checkResButton").bind("click", resetResCheck);
	$('#checkResButtonName').html('修改');
	$('#checkResIco').attr('class','e_ico-edit');
	$('#RES_CHECK_FLAG').val("true");
	
	$('#DEVICE_MODEL').text(ajaxData.get('DEVICE_MODEL'));
	
	alert('终端校验成功');
}

function resetResCheck()
{
	$('#DEVICE_MODEL').text('');
	$('#goods_RES_CODE').attr('disabled', false);
	
	$("#checkResButton").unbind("click");
	$("#checkResButton").bind("click", checkResInfo);
	$('#checkResIco').attr('class','e_ico-check');
	$('#checkResButtonName').html('校验');
	
	$('#RES_CHECK_FLAG').val("false");
	$('#CAN_SUBMIT_FLAG').val("false");
}

/*选择合约查询合约下元素*/
function queryProductInfos(obj){
	clearContractSaleInfo();
	obj = $(obj);
	var contractId = obj.val();
	if(contractId == '')
	{
		return;
	}
	var authData = $.auth.getAuthData();
	var param = "&PRODUCT_ID="+contractId+"&EPARCHY_CODE="+authData.get('USER_INFO').get('EPARCHY_CODE');
	param += '&USER_ID='+authData.get('USER_INFO').get('USER_ID');
	param += '&SERIAL_NUMBER='+authData.get('USER_INFO').get('SERIAL_NUMBER');
	param += '&NEW_IMEI='+$("#goods_RES_CODE").val();
	param += '&DEVICE_MODEL_CODE='+$('#DEVICE_MODEL_CODE').val();
	$.beginPageLoading("正在刷新合约信息..");
	$.ajax.submit(null, 'getContractInfoById', param, 'ContractDetailPart', function(data){
		$.endPageLoading();
		var confirmSet = data.get("TIPS_TYPE_CHOICE");
		var warnSet = data.get("TIPS_TYPE_TIP");
		//确认提示
		if(confirmSet && confirmSet.length>0)
		{
			var flag = true;
			confirmSet.each(function(item, index, totalCount)
			{
				if(!window.confirm(item.get("TIPS_INFO")))
				{
					flag = false;
					return false;
				}
			});
			if(!flag)
			{
				$('#ContractDetailPart').css('display', 'none');
				$('#CAN_SUBMIT_FLAG').val("false");
				return false;
			} 
		}
		//告警提示
		if(warnSet && warnSet.length>0)
		{
			warnSet.each(function(item, index, totalCount)
			{
				window.alert(item.get("TIPS_INFO"));
			});
		}
		autoAddChckedElement();
		$('#ContractDetailPart').css('display', '');
		$('#CAN_SUBMIT_FLAG').val("true");
		$('#CONTRACT_PRICE_DESC_PART').css('display', '');
		var contractPrice = parseInt($('#CONTRACT_PRICE').val());
		$('#CONTRACT_PRICE_DESC').text(contractPrice);
		
		//如果有折扣，这计算折扣后的
		var contractDiscount = $('#CONTRACT_DISCOUNT').val();
		if(contractDiscount != '')
		{
//			$('#CONSUME').val(calByDiscount($('#ORIGINAL_CONSUME').val()));
			$('#CONTRACT_PRICE').val(calByDiscount($('#ORIGINAL_CONTRACT_PRICE').val()));
			$('#NET_PRICE').val(calByDiscount($('#ORIGINAL_NET_PRICE').val()));
			$('#CONTRACT_PRICE_DESC').text(parseInt(calByDiscount($('#ORIGINAL_CONTRACT_PRICE').val())/100));
			calFee();
		}
	}, function(errorcode, errorinfo){
		$.endPageLoading();
		$.showErrorMessage('查询合约失败',errorinfo);
		$('#ContractDetailPart').css('display', 'none');
		$('#CAN_SUBMIT_FLAG').val("false");
	});
	
}

function autoAddChckedElement()
{
	$("#ElementPart input[type=checkbox]").each(function()
	{
		if($.attr(this, "checked"))
		{
			elementOnClick(this);
		}
	});
}

function consumeElementOnClick(obj)
{
	obj = $(obj);
	if(obj.attr('checked'))
	{
		var consume = calConsumeSum();  //勾选的资费套餐消费总额
		var curprice = getElementPrice(obj);
		if(curprice < consume)
		{
			var checkFlag = false;
			$("#ConsumeElementPart input[type=checkbox]").each(function()
			{
				var tmprice = getElementPrice(this);
				if(curprice < tmprice)
				{
					checkFlag = true;
					return false;
				}
			});
			if(checkFlag)
			{
				obj.attr('checked',false);
				alert("对不起，根据您选择的资费套餐你不能选择此档最低消费,请重新选择更大档的最低消费或者其他低资费套餐");
				return false;
			}
		}
		$("#ConsumeElementPart input[type=checkbox]").each(function()
		{
			if($(this).attr("checked") && $(this).attr("id") != obj.attr("id"))
			{
				$(this).attr("checked",false);
			}
		});
		$('#CONSUME').val(curprice);
	}
	else
	{
		$('#CONSUME').val('');
	}
	calFee();
}

function getElementPrice(obj)
{
	var price = $(obj).attr('price');
	return parseInt(price);	
}

function elementOnClick(obj)
{
	//$('#inputfee').val('');  //每次勾选置空最低消费额
	obj = $(obj);
	if(obj.attr('checked'))
	{
		var packageId = obj.attr("packageId");
		
		var maxNumberObj = $('#'+packageId+'_MAX_LIMIT');
		var maxNumber = maxNumberObj.val();
		if(maxNumber == '1')//当组上限制最大数为1时，勾上当前选中的元素，则需把同组的其他元素删除
		{
			var selectedSaleElements = getElementByPackageId(packageId);
			for(var i = 0; i < selectedSaleElements.length; i++)
			{
				var selectedSaleElement = selectedSaleElements.get(i);
				var elemKey = selectedSaleElement.get('ELEM_KEY');
				var checkBoxObj = $('#'+elemKey);
				checkBoxObj.attr('checked', false);
				delElement(elemKey);
			}
		}
		addElement(obj.attr('id'));
	}
	else
	{
		delElement(obj.attr('id'));
	}
	calFee();
}

function getElementByPackageId(packageId)
{
	var outSaleElements = $.DatasetList();
	elements.each(function(item, index, totalcount) {
				if (item.get("PACKAGE_ID") == packageId) {
					outSaleElements.add(item);
				}
			});
	return outSaleElements;
}

function addElement(elemKey)
{
	obj = $("#"+elemKey);
	var selectingElement = $.DataMap();
	selectingElement.put("IS_EXIST", obj.attr("isExist"));
	selectingElement.put('ELEM_KEY', obj.attr('id'));
	selectingElement.put('PRODUCT_ID', obj.attr('productId'));
	selectingElement.put('PACKAGE_ID', obj.attr('packageId'));
	selectingElement.put('ELEMENT_ID', obj.attr('elementId'));
	selectingElement.put('ELEMENT_TYPE_CODE', obj.attr('elementTypeCode'));
	selectingElement.put('ELEMENT_NAME', obj.attr('elementName'));
	selectingElement.put('PRICE', obj.attr('price'));
	selectingElement.put('PACKAGE_KIND_CODE', $('#'+obj.attr('packageId')+'_PACKAGE_KIND_CODE').val());
	selectingElement.put('MODIFY_TAG', '0');
	elements.add(selectingElement);
}

function delElement(elemKey)
{
	elements.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == elemKey) {
					elements.removeAt(index);
				}
			});
}

function clearContractSaleInfo()
{
	elements.clear();
	product.clear();
	consumeLimitElement.clear();
	
	$.feeMgr.clearFeeList("240");
	$('#ContractDetailPart').css('display', 'none');
	$('#CAN_SUBMIT_FLAG').val("false");
}

function calFee()
{
	var consumeLimitValue = '0';
	var depositFee = '0';
	var operFee = '0';
	var giftFee = '0';
	var contractMonth = $('#CONTRACT_MONTH').val();
	if(contractMonth == '')
	{
		return;
	}

	var consume = calConsumeSum();  //勾选的资费套餐消费总额
	consumeLimitValue = parseFloat($('#CONSUME').val());
	
	if(consumeLimitValue == '' || consumeLimitValue < consume)
	{
		var maxpriceConsume;
		var maxprice = 0;
		var checkFlag = true;
		//自动勾选最低消费额
		$("#ConsumeElementPart input[type=checkbox]").each(function()
		{
			var tmprice = getElementPrice(this);
			if(tmprice > maxprice)
			{
				maxprice = tmprice;
				maxpriceConsume = this;
			}
			if(tmprice >= consume)
			{
				if(!$(this).attr("checked"))
				{
					$(this).attr("checked",true);
				}
				$('#CONSUME').val(tmprice);
				checkFlag = false;
				return false;
			}
			else
			{
				if($(this).attr("checked"))
				{
					$(this).attr("checked",false);
				}
			}
		});
		if(checkFlag) //如果没有匹配的合适的，则勾选最大的
		{
			if(!$(maxpriceConsume).attr("checked"))
			{
				$(maxpriceConsume).attr("checked",true);
			}
			$('#CONSUME').val(maxprice);
		}
		consumeLimitValue = parseFloat($('#CONSUME').val());
	}	
		
	var customReturnRateList = new $.DatasetList($('#CUSTOM_RETURNRATE_CONFIG').val());
	if(customReturnRateList.length > 0)
	{
		var customReturnRateData = new $.DataMap();
		var minValue = 0;
		var maxValue = 0;
		var returnRate = '0';
		//根据用户的保底金额计算返回比率
		for(var i = 0; i < customReturnRateList.length; i++)
		{
			customReturnRateData = customReturnRateList.get(i);
			minValue = parseInt(customReturnRateData.get("ATTR_FIELD_MIN"));
			maxValue = parseInt(customReturnRateData.get("ATTR_FIELD_MAX"));
			if((minValue == -1 || minValue <= consumeLimitValue)
				&& (maxValue == -1 || maxValue >= consumeLimitValue))
			{
				returnRate = customReturnRateData.get('ATTR_FIELD_CODE');
				$('#RETURN_RATE').val(returnRate);
				break; 
			}
		}
	}
	
	var disctRuleCode = $('#DISCT_RULE_CODE').val();//折价规则
	if(disctRuleCode == 'DS8801')
	{
		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
		var param = '&PARA_CODE1='+consumeLimitValue+'&PARA_CODE2='+contractMonth+'&EPARCHY_CODE='+eparchyCode;	
		$.beginPageLoading("loadint...");
		$.ajax.submit(null, 'queryCommparaDetail', param, '', function(data){
			$.endPageLoading();
			if(data.length <= 0){
				alert("没有获取到该合约的返款配置信息!");
				//$("#CSSUBMIT_BUTTON").attr("disabled",true);
			}else{
				afterQuryCommpara(data);
			}
		}, function(errorcode, errorinfo){
			$.endPageLoading();
			$.showErrorMessage(errorcode,errorinfo);
		});
	}
	else
	{
		var returnRate = $('#RETURN_RATE').val(); //返回规则
		var subsidyRate = $("#SUBSIDY_RATE").val();//补贴率
		var netPrice = $('#NET_PRICE').val();//裸机销售价
		var contractPrice = $('#CONTRACT_PRICE').val();//合约零售价
		if(disctRuleCode == 'DS0001') //购机赠款
		{
			//预存款=最低消费*月份*分返比例  ,最高不超过合约款
			//购机款=合约价 - （ 最低消费*月份）*分返比例
			depositFee = (parseFloat(consumeLimitValue)*contractMonth*returnRate) / 100;
			if(depositFee > contractPrice)
			{
				depositFee = contractPrice;
			}
			operFee = contractPrice - depositFee;
		}
		else if(disctRuleCode == 'DS0002')  //预存优惠
		{
			//购机款=裸机零售价- 最低消费*月份*分返比例
			//预存款=合约价- 购机款
			operFee = parseFloat(netPrice) - (parseFloat(consumeLimitValue)*contractMonth*returnRate) / 100;
			if(operFee < 0)
			{
				operFee = 0;
			}
			depositFee = contractPrice - operFee;
		}
		else if(disctRuleCode == 'DS0005')
		{
			var creditClass = $("#CREDIT_CLASS").val();
			depositFee = parseFloat(consumeLimitValue)*(parseFloat(contractMonth))*(parseFloat(subsidyRate/100)); //实际购机价格
			operFee = parseFloat(contractPrice) -parseFloat(depositFee); //购机价格
			if("5"==creditClass){
				depositFee = 0;
			}else if("4"==creditClass){
				depositFee = parseFloat(depositFee) * 0.2
			}else if("3"==creditClass){
				depositFee = parseFloat(depositFee) * 0.4
			}else if("2"==creditClass){
				depositFee = parseFloat(depositFee) * 0.6
			}else if("1"==creditClass){
				depositFee = parseFloat(depositFee) * 0.8
			}
	        if(parseFloat(operFee) <= 0){
	        	operFee=0;
	            depositFee =contractPrice;
	        }
		}
		$('#DEPOSIT_FEE').val(depositFee);
		$('#OPER_FEE').val(operFee);
		$('#GIFT_FEE').val(giftFee);
		
		buildContractPreView();
		
		//展示费用
		$.feeMgr.clearFeeList("240");
		if(operFee > 0)
		{
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", "240");
			feeData.put("MODE", '0');
			feeData.put("CODE", '60');
			feeData.put("FEE", operFee);
			$.feeMgr.insertFee(feeData);
		}
		if(depositFee > 0)
		{
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", "240");
			feeData.put("MODE", '2');
			feeData.put("CODE", '43');
			feeData.put("FEE", depositFee);
			feeData.put("ELEMENT_ID", $('#DISCNT_GIFT_ID').val());
			$.feeMgr.insertFee(feeData);
		}
	}
}

function afterQuryCommpara(data)
{
	var operFee = data.get("PARA_CODE3");//购机款
	var depositFee = data.get("PARA_CODE4");//预存款
	var compenfeeTypeCode = data.get("PARA_CODE9");//购机款小类编码
	var depositfeeTypeCode = data.get("PARA_CODE8");//预存款小类编码
	var discntGiftId = data.get("PARA_CODE7");//预存编码
	$('#DEPOSIT_FEE').val(depositFee);
	$('#OPER_FEE').val(operFee);
	var months = $('#CONTRACT_MONTH').val();//捆绑月份
	var consumeLimitValue = parseFloat($('#CONSUME').val());
	var contractPreView = "合约承诺最低消费为" + (consumeLimitValue/100) + "元,客户承诺合约期为" + months + "个月</br>";
	var averagereturn	= parseFloat(data.get("PARA_CODE5"))/100;
	var latmonthreturn	= parseFloat(data.get("PARA_CODE6"))/100;
	var returnDesc="话费月均返还额(末月除外)"+averagereturn+"元,末月返还额"+latmonthreturn+"元";
    contractPreView += returnDesc;
	var discount = $('#CONTRACT_DISCOUNT').val();
	if(discount != null && discount != '')
	{
		contractPreView += "</br>合约价打" + discount*10 + "折";
	}
	$('#contractPreView').html(contractPreView);
	
	$.feeMgr.clearFeeList("240");//展示费用
	if(operFee > 0)
	{
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "240");
		feeData.put("MODE", '0');
		feeData.put("CODE", compenfeeTypeCode);
		feeData.put("FEE", operFee);
		$.feeMgr.insertFee(feeData);
	}
	if(depositFee > 0)
	{
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "240");
		feeData.put("MODE", '2');
		feeData.put("CODE", depositfeeTypeCode);
		feeData.put("FEE", depositFee);
		feeData.put("ELEMENT_ID", discntGiftId);
		$.feeMgr.insertFee(feeData);
	}
}

function buildContractPreView()
{
	var discntRuleCode = $('#DISCT_RULE_CODE').val();//折价规则
	
	var consumeLevel = $('#CONSUME').val();//最低消费
	
	var months = $('#CONTRACT_MONTH').val();//捆绑月份
	var depositFee = $('#DEPOSIT_FEE').val();//预存款
	var operFee = $('#OPER_FEE').val();//购机款
	var giftFee = $('#GIFT_FEE').val();//赠款
	
	if(months == '' || consumeLevel == '')
	{
		return;
	}
	
	var contractPreView = "合约承诺最低消费为" + (consumeLevel/100) + "元,客户承诺合约期为" + months + "个月</br>";
	
	//月返还/赠送额度
	var tmpFee = "";
	var returnDesc = "";
	
	if(discntRuleCode == 'DS0004')
	{
		tmpFee = parseFloat(giftFee)/100;
		returnDesc = "话费月均赠送额";
	}
	else
	{
		tmpFee = parseFloat(depositFee)/100;
		returnDesc = "话费月均返还额";
	}
	var mod = parseFloat(tmpFee)%parseFloat(months);
	if(mod != 0)
	{
		//向上取整
	    var averagereturn =parseInt(tmpFee/parseFloat(months))+1;
	    var lastmonthreturn = parseFloat(tmpFee) -parseFloat(averagereturn)*(parseFloat(months)-1);
		if(parseFloat(lastmonthreturn)<0){
			averagereturn=parseFloat(averagereturn)-1;	
			lastmonthreturn = parseFloat(tmpFee) -parseFloat(averagereturn)*(parseFloat(months)-1);
		}
		returnDesc+="(末月除外)"+averagereturn+"元，末月"+$.format.number(lastmonthreturn,"0.00")+"元";
	}
	else
	{
		var showaverage=parseFloat(tmpFee)/parseFloat(months);
		returnDesc+= showaverage +"元";
	}
	contractPreView += returnDesc;
	
	var discount = $('#CONTRACT_DISCOUNT').val();
	if(discount != null && discount != '')
	{
		contractPreView += "</br>合约价打" + discount*10 + "折";
	}
	
	$('#contractPreView').html(contractPreView);
}	

function onTradeSubmit()
{
	if(!jsSubmitCheck())
	{
		return false;
	}
	
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&PACKAGE_ID=' + $('#SALEACTIVE_PACKAGE_ID').val();
	param += '&PRODUCT_ID=' + $('#CONTRACT_ID').val();
	param += '&CAMPN_TYPE=' + $('#CAMPN_TYPE').val();
	param += '&DEPOSIT_FEE=' + $('#DEPOSIT_FEE').val();
	param += '&OPER_FEE=' + $('#OPER_FEE').val();
	if($('#GIFT_FEE').val() && $('#GIFT_FEE').val() != ""){
		param += '&GIFT_FEE=' + $('#GIFT_FEE').val();
	}
	param += '&MONTHS=' + $('#CONTRACT_MONTH').val();
	param += '&RES_CODE=' + $('#goods_RES_CODE').val(); //终端IMEI
	param += '&SALE_STAFF_ID=' + $('#SALE_STAFF_ID').val();//销售员工
	param += '&NEED_BUILD_FEE=0';
	param += '&NEED_CHECK_RES=0';
	param += '&DISCOUNT=' + $('#CONTRACT_DISCOUNT').val();
	param += '&CONSUME_LIMIT=' + $('#CONSUME').val();
	param += '&REMARK='+$('#REMARK').val();
	
	var addElements = new $.DatasetList();
	elements.each(function(item, index, totalcount) 
	{
		addElements.add(item);
		if(typeof(item.get('PRODUCT_ID')) != 'undefined' && item.get('PRODUCT_ID') != '')
		{ 
			var mainProductId = item.get('PRODUCT_ID');
			var element = new $.DataMap();
			element.put("ELEMENT_TYPE_CODE","P");
			element.put("ELEMENT_ID",mainProductId);
			element.put("MODIFY_TAG","0");
			addElements.add(element);  //针对语音优惠组，添加产品元素
		}
	});
	param += '&ELEMENTS='+addElements.toString(); //产品变更要素
	
	$.cssubmit.addParam(param);
	var checkParam = "";
	checkParam += '&PACKAGE_ID_A='+$('#SALEACTIVE_PACKAGE_ID').val();
	checkParam += '&PRODUCT_ID_A='+$('#CONTRACT_ID').val();
	
	$.tradeCheck.addParam(checkParam);
	
	return true;
}

function jsSubmitCheck()
{
	var resCheckFlag = $('#RES_CHECK_FLAG').val();
	if(resCheckFlag == 'false')
	{
		alert('串号未校验');
		return false;
	}
	
	var canSubmitFlag = $('#CAN_SUBMIT_FLAG').val();
	if(canSubmitFlag == 'false')
	{
		alert('未选择合约');
		return false;
	}
	
	var contractMonth = $('#CONTRACT_MONTH').val();
	if(contractMonth == null || contractMonth == '')
	{
		alert('未选择合约期');
		return false;
	}
	
	consumeLevel = $('#CONSUME').val();
	if(consumeLevel == '' || consumeLevel == '0')
	{
		alert('没有选择营销资源或最低消费');
		return false;
	}
	
	
	
	return true;
}

function checkRemark()
{
	var flag = $("#remarkBox").attr("checked");
	if(flag == true)
	{
		$("#remarkArea").css("display", "");
		$("#REMARK").focus();
	}
	else
	{
		$("#remarkArea").css("display", "none");
	}
}

/*
function checkDiscountQualifyAndCalFee()
{
	var contractDiscount = $('#CONTRACT_DISCOUNT').val();
	$('#CONSUME').val(calByDiscount($('#ORIGINAL_CONSUME').val()));
	$('#CONTRACT_PRICE').val(calByDiscount($('#ORIGINAL_CONTRACT_PRICE').val()));
	$('#NET_PRICE').val(calByDiscount($('#ORIGINAL_NET_PRICE').val()));
	$('#CONTRACT_PRICE_DESC').text(parseInt(calByDiscount($('#ORIGINAL_CONTRACT_PRICE').val())/100));
	calFee();
}
*/	

function calByDiscount(value)
{
	value = value/100;
	var contractDiscount = $('#CONTRACT_DISCOUNT').val();
	if(contractDiscount != '')
	{
		value = value * contractDiscount;
	}	
	
	return Math.round($.format.number(value,"0.0"))*100;//先保留一位小数，再做四舍五入
}	

function calConsumeSum()
{
	var totalPrice = 0;
	if(elements.length > 0)
	{
		elements.each(function(item, index, totalcount) {
					var price = item.get('PRICE');
					totalPrice += parseInt(price);
				});
	}
	
	return totalPrice;
}	