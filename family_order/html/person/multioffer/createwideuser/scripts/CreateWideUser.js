
/* 是否绑定号码的样式变更 */
function changeDisabled()
{
	if($("#IS_CHECKED").attr("checked"))
	{
		$("#IS_CHECKED").val("0");
		$("#AUTH_SERIAL_NUMBER").attr("disabled", false);
		$("#AUTH_SERIAL_NUMBER").focus();
	}
	else
	{
		$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
		$("#AUTH_SERIAL_NUMBER").val("");
		$("#IS_CHECKED").val("1");
	}
}
/* auth查询后自定义查询 */
function refreshPartAtferAuth(data)
{
	var param = "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString()
	+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();

	$.ajax.submit(this, 'loadChildInfo', param, 'UserInfoArea,productType', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}
/*隐藏或显示邮寄信息区*/
function writeBankInfo(btn)
{
	if("none" != $("#BankPart").css("display"))
	{
		$("#BankPart").css("display", "none");
		btn.children[0].className = "e_ico-unfold";
		btn.children[1].innerHTML = "填写银行信息";
	}
	else
	{
		$("#BankPart").css("display", "");
		btn.children[0].className = "e_ico-fold";
		btn.children[1].innerHTML = "隐藏银行信息";
	}
}
/*帐户类型变化时，样式处理*/
function checkPayModeCode()
{
	//如果帐户类型为现金
	if($("#PAY_MODE_CODE").val()=="0")
	{
		$("#SUPER_BANK_CODE").val("");
		$("#BANK_CODE").val("");
		$("#BANK_ACCT_NO").val("");
		
		$("#SUPER_BANK_CODE").attr("disabled", true);
		$("#BANK_CODE").attr("disabled", true);
		$("#BANK_ACCT_NO").attr("disabled", true);

		$("#span_SUPER_BANK_CODE").removeClass("e_required");
		$("#span_BANK_CODE").removeClass("e_required");
		$("#span_BANK_ACCT_NO").removeClass("e_required");

		$("#SUPER_BANK_CODE").attr("nullable", "yes");
		$("#BANK_CODE").attr("nullable", "yes");
		$("#BANK_ACCT_NO").attr("nullable", "yes");
	}
	else
	{
		$("#SUPER_BANK_CODE").attr("disabled", false);
		$("#BANK_CODE").attr("disabled", false);
		$("#BANK_ACCT_NO").attr("disabled", false);
		
		$("#span_SUPER_BANK_CODE").addClass("e_required");
		$("#span_BANK_CODE").addClass("e_required");
		$("#span_BANK_ACCT_NO").addClass("e_required");

		$("#SUPER_BANK_CODE").attr("nullable", "no");
		$("#BANK_CODE").attr("nullable", "no");
		$("#BANK_ACCT_NO").attr("nullable", "no");
	}
}
/*上级银行变化时，刷新下级银行*/
function checkSuperBankCode()
{
	var param = "&SUPER_BANK_CODE=" + $("#SUPER_BANK_CODE").val();
	$.beginPageLoading("银行数据查询中。。。");
	$.ajax.submit(this, 'getBankBySuperBank', param, 'BankCodePart', 
	function()
	{
		//刷新下级银行区域时，会将样式刷没了，这里认为，可以选择上级银行，则表示下级银行是必填的
		$("#span_BANK_CODE").addClass("e_required");
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}
/* 选择标准地址返回后查询地址的资源覆盖能力信息 */
function setReturnFunc()
{
	var accessType = $("#ACCESS_TYPE").val();
	var addrId = $("#ADDR_ID").val();
	
	$.beginPageLoading("综资资源覆盖能力查询。。。");
	$.ajax.submit(this, 'qryOltInfo', "&COVER_TYPE="+accessType+"&HOUSE_ID="+addrId, '', 
		function(dataset)
		{
			var portNum = dataset.get(0).get("FREE_PORT_NUM");
			$("#VENDOR_NAME").val(dataset.get(0).get("VENDOR_NAME"));//网络建设厂商名称
			$("#MAX_WIDTH").val(dataset.get(0).get("MAX_WIDTH"));//最大带宽
			$("#FREE_PORT_NUM").val(portNum);//空闲端口数，为0时不能开户
			$("#RESULT_CODE").val("0");//返回结果，用于提交时确认是否通过了查询
			/*if("" == portNum || 0 >= parseInt(portNum))
			{
				$("#RESULT_CODE").val("-1");
				$.showWarnMessage("资源覆盖能力查询", "该地址空闲端口数为0，不能进行宽带开户！");
			}*/
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$("#MAX_WIDTH").val("");
			$("#FREE_PORT_NUM").val("");
			$("#RESULT_CODE").val("-1");
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		}
	);
	
	/*$("#VENDOR_NAME").val("华为");//网络建设厂商名称
	$("#MAX_WIDTH").val("4");//最大带宽
	$("#FREE_PORT_NUM").val("1");//空闲端口数，为0时不能开户
	$("#RESULT_CODE").val("0");//返回结果，用于提交时确认是否通过了查询
	if("" == $("#FREE_PORT_NUM").val() || 0 >= $("#FREE_PORT_NUM").val())
	{
		$("#RESULT_CODE").val("-1");
		$.showWarnMessage("资源覆盖能力查询", "该地址空闲端口数为0，不能进行宽带开户！");
	}*/
	
	if("FTTH" == accessType)
	{
		$("#MODEM_SALE_TYPE").val("0");
		$("#MODEM_SALE_TYPE").attr("disabled", false);
		$("#modemVerify").css("display", "");
		$("#RES_CODE").attr("nullable", "no");
		$("#resVeirfy").addClass("e_required");
	}
	else
	{
		selFttbStyle();
	}
	
	changeModemSaleType();
}
function selFttbStyle()
{
	$("#VENDOR_NAME").val("");
	$("#MODEM_SALE_TYPE").val("1");
	$("#MODEM_SALE_TYPE").attr("disabled", true);
	$("#MODEM_FEE").val("0.00");
	$("#modemVerify").css("display", "none");//隐藏MODEM串号校验区域
	$("#RES_CODE").attr("nullable", "yes");//可以为空
	$("#resVeirfy").removeClass("e_required");
}
/* 修改MODEM购买方式时改变相关页面信息 */
function changeModemSaleType()
{
	var modemFeeTypeCode = "400";//光猫费用类型
	var feeMode = "0";
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	var modemSaleType = $("#MODEM_SALE_TYPE").val();
	
	if("0" == modemSaleType)//光猫：0-购买
	{
		$("#modemVerify").css("display", "");
		$("#RES_CODE").attr("nullable", "no");
		$("#resVeirfy").addClass("e_required");
		
		var productId = $("#PRODUCT_ID").val();
		if("" != productId && null != productId)
		{
			var modemFee = $("#MODEM_FEE_BAK").val();
			if("" != modemFee && null != modemFee)//第一次查询时不进行插费用
			{
				insertFee(tradeTypeCode, feeMode, modemFeeTypeCode, modemFee);
			}
		}
	}
	else
	{
		if("3" == modemSaleType)//1-无需设备；3-缺货
		{
			$("#RES_NO").val("-1");
		}
		else if("1" == modemSaleType)
		{
			$("#RES_NO").val("");
		}
		$("#MODEM_FEE").val("0.00");
		$("#modemVerify").css("display", "none");//隐藏MODEM串号校验区域
		$("#RES_CODE").attr("nullable", "yes");//可以为空
		$("#resVeirfy").removeClass("e_required");
		$.feeMgr.removeFee(tradeTypeCode, feeMode, modemFeeTypeCode);//清除光猫费用
	}
	
}
/* MODEM校验 */
function verifyModemSN()
{
	var resCode = $("#RES_CODE").val();
	if(null == resCode || "" == resCode)
	{
		alert("请先填写串号再进行校验！");
		return false;
	}
	$.beginPageLoading("MODEM数据校验中。。。");
	var param = "&HOUSE_ID="+$("#ADDR_ID").val()+"&RES_NO="+resCode+"&VENDOR_NAME="+$("#VENDOR_NAME").val()
				+"&COVER_TYPE="+$("#ACCESS_TYPE").val()+"&CUST_NAME="+$("#CUST_NAME").val()+"&CONTACT_PHONE="+$("#CONTACT_PHONE").val();
	$.ajax.submit(this, 'verifyModemSN', param, '', 
		function(dataset)
		{
			$("#ONU_NAME").val(dataset.get(0).get("BRAND_CODE"));//ONU-光猫厂商编码
			$("#ONU_TEXT").val(dataset.get(0).get("BRAND_NAME"));//ONU-光猫厂商名称
			$("#ONU_TYPE").val(dataset.get(0).get("EMODEL_MODEL_CODE"));//ONU-光猫型号编码
			$("#ONU_TYPE_TEXT").val(dataset.get(0).get("EMODEL_MODEL_NAME"));//ONU-光猫型号名称
			$("#RES_NO").val(resCode);
			alert("光猫预占成功！");
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$("#RES_NO").val("");
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		}
	);
}
/* MODEM占用 */
function useModemSN()
{
	$.beginPageLoading("MODEM数据校验中。。。");
	$.ajax.submit(this, 'useModemSN', "&RES_CODE="+$("#RES_CODE").val(), '', 
		function(dataset)
		{
			$.showWarnMessage("返回数据", dataset);
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
		}
	);
}
/* MODEM释放 */
function releaseModemSN()
{
	$.beginPageLoading("MODEM数据校验中。。。");
	$.ajax.submit(this, 'releaseModemSN', "&RES_CODE="+$("#RES_CODE").val(), '', 
		function(dataset)
		{
			$.showWarnMessage("返回数据", dataset);
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
		}
	);
}
/* MODEM更换 */
function changeModem()
{
	$.beginPageLoading("MODEM数据校验中。。。");
	$.ajax.submit(this, 'changeModem', "&RES_NO_SALE="+$("#RES_NO_SALE").val()+"&RES_NO_RELEASE="+$("#RES_NO_RELEASE").val(), '', 
		function(dataset)
		{
			$.showWarnMessage("返回数据", dataset);
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
		}
	);
}
/* 选择产品时刷新产品包 */
function getPackagesByProduct()
{
	var productId = $("#PRODUCT_ID").val();
	var modemSaleType = $("#MODEM_SALE_TYPE").val();
	
	if("" == productId || null == productId) 
	{
		$("#SelectedElementsTable").empty();
		$("#packagesArea").empty();
		$("#DiscntTable").empty();
		$("#MODEM_FEE").val("0.00");
		$("#INSTALL_FEE").val("0.00");
		$.feeMgr.clearFeeList();//未选择产品时，清空费用列表
		return false;
	}
	
	$.beginPageLoading("产品包数据查询中。。。");
	setTimeout(function(){//同步时先展示Loading，必须延迟一会儿
		$.ajax.setup({async:false});
		$.ajax.submit(this, 'getPackagesByProduct', "&PRODUCT_ID="+productId+"&MODEM_SALE_TYPE="+modemSaleType, 'packageListPart', 
			function(dataset)
			{
				$("#DiscntTable").empty();
				$("#SelectedElementsTable").empty();
			//	selOtherFee();
				setPackageArea(dataset);
			//	changeModemSaleType();
				$.endPageLoading();
			}, 
			function(error_code, error_info)
			{
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			}
		);
		$.ajax.setup({async:true});
	}, 200);
	
}
//设置费用组件，有组合方式，需要IF判断一下
function insertBroadBandFee(dataset)
{
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	dataset.each
	(
		function(info)
		{
			var feeMode = info.get("FEE_MODE");
			var installFeeMode = info.get("INSTALL_FEE_MODE");
			var modemFeeMode = info.get("MODEM_FEE_MODE");
			var feeTypeCode = "";
			var fee = "";
			
			if(feeMode && "" != feeMode && null != feeMode)
			{
				feeMode = feeMode;
				feeTypeCode = info.get("FEE_TYPE_CODE");
				fee = info.get("FEE");
				insertFee(tradeTypeCode, feeMode, feeTypeCode, fee);
			}
			if(installFeeMode && "" != installFeeMode && null != installFeeMode)
			{
				feeMode = installFeeMode;
				feeTypeCode = info.get("INSTALL_FEE_TYPE_CODE");
				fee = info.get("INSTALL_FEE");
				insertFee(tradeTypeCode, feeMode, feeTypeCode, fee);
			}
			if(modemFeeMode && "" != modemFeeMode && null != modemFeeMode)
			{
				feeMode = modemFeeMode;
				var feeTypeCode = info.get("MODEM_FEE_TYPE_CODE");
				var fee = info.get("MODEM_FEE");
				insertFee(tradeTypeCode, feeMode, feeTypeCode, fee);
			}
		}
	);
}
//设置预存费用组件
function insertFee(tradeTypeCode, feeMode, feeTypeCode, fee)
{
	$.feeMgr.removeFee(tradeTypeCode, feeMode, feeTypeCode);//清空费用列表
	
	var feeData = new $.DataMap();
	feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
	feeData.put("MODE", feeMode);
	feeData.put("CODE", feeTypeCode);
	feeData.put("FEE",  fee);
	$.feeMgr.insertFee(feeData);
	
	if("400" == feeTypeCode)//光猫费用
	{
		$("#MODEM_FEE").val(fee);
	}
	
	if("410" == feeTypeCode)//安装费用
	{
		$("#INSTALL_FEE").val(fee);
	}
}
/* 将返回的产品包资料放入页面展示 */
function setPackageArea(dataset)
{
	dataset.each
	(
		function(info)
		{
			//定义系统时间
			if("" != info.get("SYS_DATE") && null != info.get("SYS_DATE") && undefined != info.get("SYS_DATE"))
			{
				$("#SYS_DATE").val(info.get("SYS_DATE"));
			}
			
			//将必选包内的必选、默认元素放入
			var forceTag = info.get("FORCE_TAG");
			var defaultTag = info.get("DEFAULT_TAG");
			if ("1" == forceTag || "1" == defaultTag)
			{
				var packageId = info.get("PACKAGE_ID");
				var packageName = info.get("PACKAGE_NAME");
				var productId = info.get("PRODUCT_ID");
				var packageForceTag = info.get("FORCE_TAG");
				
				//判断是否已经存在于已选区域
				var selElements = getCheckedEleData();//获取已选区域信息
				var flag = true;//默认未选
				for(var i = 0; i < selElements.getCount(); i++)
				{
					var elementData = selElements.get(i);
					if(elementData.get("PACKAGE_ID") == packageId)
					{
						flag = false;
						break;
					}
				}
				
				//已选区域没有必选包，则将查询到的必选包必选元素放入已选区域
				if(flag)
				{
					$.beginPageLoading("必选包数据查询中。。。");
					var inparam = "&PACKAGE_ID="+packageId+"&SYS_DATE="+$("#SYS_DATE").val()+"&PRODUCT_ID="+productId;
					$.ajax.submit(this, 'getPackageElements', inparam, 'elementPart',
					function(dataset)
					{
						setForceElementsArea(dataset, productId, packageId, packageName, packageForceTag);
						$.endPageLoading();
					}, 
					function(error_code, error_info)
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});
				}
			}
			
			packagePushHtml(info);
		}
	);
}
/* 查询该产品下的光猫、安装费用 */
function selOtherFee()
{
	var productId = $("#PRODUCT_ID").val();
	if("" != productId && null != productId)
	{
		$.beginPageLoading("费用查询中。。。");
		$.ajax.submit(this, 'qryOtherFee', "&PRODUCT_ID="+productId, '',
		function(dataset)
		{
			if(dataset.length > 0)
			{
				insertBroadBandFee(dataset);
			}
			
			$("#MODEM_FEE_BAK").val($("#MODEM_FEE").val());
			$("#INSTALL_FEE_BAK").val($("#INSTALL_FEE").val());
			$.endPageLoading();
		}, 
		function(error_code, error_info)
		{
			$("#MODEM_FEE").val("0.00");
			$("#INSTALL_FEE").val("0.00");
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		});
	}
	else
	{
		$("#MODEM_FEE").val("0.00");
	}
}
/* 查询产品必选包时，将必选、默认元素先插入已选区域 */
function setForceElementsArea(dataset, productId, packageId, packageName, packageForceTag)
{
	var html=[];
	dataset.each
	(
		function(data)
		{
			setElement(data, productId, packageId, packageName, packageForceTag, html);
		}
	);
	$.insertHtml('beforeend', $("#SelectedElementsTable"), html.join(""));
}
/* 设置元素 */
function setElement(data, productId, packageId, packageName, packageForceTag, html)
{
	if("1" == data.get("FORCE_TAG") || "1" == data.get("DEFAULT_TAG"))//将必选、默认元素填入
	{
		var checked = true;
		var disabled = false;
		var forceTag = data.get("FORCE_TAG")=="1"?"必选":"可选";
		var defaultTag = data.get("DEFAULT_TAG")=="1"?"默认":"非默认";
		var elementType = data.get("ELEMENT_TYPE_CODE")=="S"?"服务":"优惠";
		var forceColor = data.get("FORCE_TAG")=="1"?"e_orange":"e_green";
		var defaultColor = data.get("DEFAULT_TAG")=="1"?"e_blue":"e_green";
		
		html.push('<tr title="'+data.get("EXPLAIN")+'">');
		html.push('<td style="display:none"><input name="existEleInput" id="PE_'+data.get("ELEMENT_TYPE_CODE")+"_"+data.get("ELEMENT_ID")+'" value='+data.get("ELEMENT_ID")+' ');
		html.push(' PACKAGE_FORCE_TAG="'+packageForceTag+'" ');
		html.push(' PRODUCT_ID="'+productId+'" ');
		html.push(' MODIFY_TAG="0" ');
		html.push(' START_DATE="'+data.get("START_DATE")+'" ');
		html.push(' END_DATE="'+data.get("END_DATE")+'" ');
		html.push(' ELEMENT_TYPE_CODE="'+data.get("ELEMENT_TYPE_CODE")+'" ');
		html.push(' FORCE_TAG="'+data.get("FORCE_TAG")+'" ');
		html.push(' DEFAULT_TAG="'+data.get("DEFAULT_TAG")+'" ');
		html.push(' PACKAGE_ID="'+packageId+'" ');
		html.push(' PACKAGE_NAME="'+packageName+'" ');
		html.push(' ELEMENT_ID="'+data.get("ELEMENT_ID")+'" ');
		html.push(' ELEMENT_NAME="'+data.get("ELEMENT_NAME")+'" ');
		html.push(' MAIN_TAG="'+data.get("MAIN_TAG")+'" ');
		html.push(' RSRV_STR1="'+data.get("RSRV_STR1")+'" ');
		html.push(' /></td>');
		
		html.push('<td title="'+productId+'" class="e_center" style="display:none">'+productId+'</td>');
		html.push('<td title="'+packageId+'" class="e_center" style="display:none">'+packageId+'</td>');
		html.push('<td title="'+packageName+'" class="e_center" width="20%">'+packageName+'</td>');
		html.push('<td title="'+data.get("ELEMENT_ID")+'" class="e_center" width="10%">'+data.get("ELEMENT_ID")+'</td>');
		html.push('<td title="'+data.get("ELEMENT_NAME")+'" class="e_center" width="20%">'+data.get("ELEMENT_NAME")+'</td>');
		html.push('<td title="'+forceTag+'" class="e_center '+ forceColor +'" width="5%">'+forceTag+'</td>');
		html.push('<td class="e_center" style="display:none">'+data.get("FORCE_TAG")+'</td>');
		html.push('<td title="'+defaultTag+'" class="e_center '+ defaultColor +'" class="e_center" width="5%">'+defaultTag+'</td>');
		html.push('<td class="e_center" style="display:none">'+data.get("DEFAULT_TAG")+'</td>');
		html.push('<td title="'+elementType+'" class="e_center" width="10%">'+elementType+'</td>');
		html.push('<td class="e_center" style="display:none">'+data.get("ELEMENT_TYPE_CODE")+'</td>');
		html.push('<td title="'+data.get("NEW_START_DATE")+'" width="15%">'+data.get("NEW_START_DATE")+'</td>');
		html.push('<td title="'+data.get("NEW_END_DATE")+'" width="15%">'+data.get("NEW_END_DATE")+'</td>');
		html.push('<td style="display:none">'+data.get("MAIN_TAG")+'</td>');
		html.push('<td style="display:none">'+data.get("RSRV_STR1")+'</td>');
		html.push('<td style="display:none">'+packageForceTag+'</td>');
		html.push('</tr>');
	}
}
/* 将包元素组装成HTML格式 */
function packagePushHtml(info)
{
	var html = [];
	html.push(' <li element="li" title="'+info.get("PACKAGE_ID")+':'+info.get("PACKAGE_NAME")+'" ');
	if(info.get("PACKAGE_TYPE_CODE")=='1' && info.get("FORCE_TAG")=='1')
	{
		html.push(' style="display:none;" ');//必选服务包隐藏，在营业员选择优惠时自动加上
	}
	html.push(' > <a href="#nogo" id="'+info.get("PACKAGE_ID")+'" class="text" onclick="getElementsByPackage('+info.get("PRODUCT_ID")
	+','+info.get("PACKAGE_ID")+',\''+info.get("PACKAGE_NAME")+'\')"');
	html.push(' productId="'+info.get("PRODUCT_ID")+'" ');
	html.push(' packageId="'+info.get("PACKAGE_ID")+'" ');
	html.push(' packageName="'+info.get("PACKAGE_NAME")+'" ');
	html.push(' limitType="'+info.get("LIMIT_TYPE")+'" ');
	html.push(' forceTag="'+info.get("FORCE_TAG")+'" ');
	html.push(' defaultTag="'+info.get("DEFAULT_TAG")+'" ');
	html.push(' minNumber="'+info.get("MIN_NUMBER")+'" ');
	html.push(' maxNumber="'+info.get("MAX_NUMBER")+'" >');
	html.push(' '+info.get("PACKAGE_NAME")+'</a> </li>');
	
	$.insertHtml('beforeend', $("#packagesArea"), html.join(""));
}
/* 通过包ID获取包元素 */
function getElementsByPackage(productId, packageId, packageName)
{
	$.beginPageLoading("产品元素查询中。。。");
	var param = "&PRODUCT_ID="+productId+"&PACKAGE_ID="+packageId+"&SYS_DATE="+$("#SYS_DATE").val();
	$.ajax.submit(this, 'getPackageElements', param, 'discntListPart',
	function(dataset)
	{
		setElementArea(dataset, productId, packageId, packageName);
		$.endPageLoading();
	}, 
	function(error_code, error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code, error_info);
	});
}
/* 选择包时，刷新可选区，同时对已经选择的元素进行屏蔽 */
function setElementArea(dataset, productId, packageId, packageName)
{
	var selElements = getCheckedEleData();//获取已选区域信息
	var html=[];
	
	//先判断是否是同一个主体优惠包，如是则继续，否则清除原有MAIN_TAG=1-主体优惠；2-标准优惠 的优惠数据
	for(var i = 0; i < selElements.getCount(); i++)
	{
		var elementData = selElements.get(i);
		var selElementTypeCode = elementData.get("ELEMENT_TYPE_CODE");
		var selElementId = elementData.get("ELEMENT_ID");
		var selMainTag = elementData.get("MAIN_TAG");
		
		if(elementData.get("PACKAGE_ID") != packageId && selElementTypeCode == "D" && (selMainTag == "1" || selMainTag == "2"))
		{
			$("#PE_"+selElementTypeCode+"_"+selElementId).parent().parent().remove();//清除展示区已选的这一列元素信息
		}
	}
	
	var operTag = true;//可以继续操作标识
	dataset.each
	(
		function(data)
		{
			var forceTag = data.get("FORCE_TAG");
			var defaultTag = data.get("DEFAULT_TAG");
			
			//如有相同优惠，则先勾选上
			var flag = false;
			for(var i = 0; i < selElements.getCount(); i++)
			{
				var elementData = selElements.get(i);
				
				if(elementData.get("PACKAGE_ID") == packageId && elementData.get("ELEMENT_ID") == data.get("ELEMENT_ID") && elementData.get("ELEMENT_TYPE_CODE") == data.get("ELEMENT_TYPE_CODE"))
				{
					flag = true;
					break;
				}
			}
			
			//如果有未进入已选区域的必选、默认元素，则在已选区域插入一条记录
			if(("1" == forceTag || "1" == defaultTag) && !flag)
			{
				if("D" == data.get("ELEMENT_TYPE_CODE") && "2" == data.get("MAIN_TAG"))
				{
					var rate = data.get("RSRV_STR1");
					var maxWidth = $("#MAX_WIDTH").val();
					if("" == maxWidth || null == maxWidth)
					{
						$.showWarnMessage("资源覆盖能力提示", "该地址没有查询到带宽信息，请重新选择地址进行资源覆盖能力查询！");
						operTag = false;
						return false;
					}
					if(parseInt(rate) > parseInt(maxWidth))
					{
						$.showWarnMessage("带宽提示", "您选择的宽带产品速率超出了该地址能配置的最大速率，请重新选择！");
						operTag = false;
						return false;
					}
					
					//modemSaleTypeResource(data.get("ELEMENT_ID"));
				}
				
				var defHtml = [];
				setElement(data, productId, packageId, packageName, "0", defHtml);
				$.insertHtml('beforeend', $("#SelectedElementsTable"), defHtml.join(""));
			}
			
			//将包元素放入待选区
			html.push('<tr title="'+data.get("EXPLAIN")+'"><td><label>');
			html.push('<input name="elementCheckBox" type="checkbox" id="DIS_'+data.get("ELEMENT_TYPE_CODE")+"_"+data.get("ELEMENT_ID")+'" value='+data.get("ELEMENT_ID")+' ');
			html.push(' onclick="checkAddDiscnts(this)" ');
			if("1" == forceTag || "1" == defaultTag || flag) html.push(' checked="true" ');
			if("1" == forceTag) html.push(' disabled="true" ');
			html.push(' ELEMENT_TYPE_CODE="'+data.get("ELEMENT_TYPE_CODE")+'" ');
			html.push(' DEFAULT_TAG="'+data.get("DEFAULT_TAG")+'" ');
			html.push(' FORCE_TAG="'+data.get("FORCE_TAG")+'" ');
			html.push(' ELEMENT_NAME="'+data.get("ELEMENT_NAME")+'" ');
			html.push(' ELEMENT_ID="'+data.get("ELEMENT_ID")+'" ');
			html.push(' REORDER="'+data.get("REORDER")+'" ');
			html.push(' EXPLAIN="'+data.get("EXPLAIN")+'" ');
			html.push(' PACKAGE_ID="'+packageId+'" ');
			html.push(' PACKAGE_NAME="'+packageName+'" ');
			html.push(' PRODUCT_ID="'+productId+'" ');
			html.push(' MAIN_TAG="'+data.get("MAIN_TAG")+'" ');
			html.push(' RSRV_STR1="'+data.get("RSRV_STR1")+'" ');
			html.push(' PACKAGE_FORCE_TAG="'+"0"+'" ');
			html.push(' class="e_checkbox"/> ');
			
			html.push('<span>['+data.get("ELEMENT_ID")+']'+data.get("ELEMENT_NAME")+'</span>');
			html.push('</lable></td>');
			html.push('<td>'+data.get("EXPLAIN")+'</td>');
			html.push('</tr>');
		}
	);
	$.insertHtml('beforeend', $("#DiscntTable"), html.join(""));
	
	if(operTag)
	{
		insertBroadBandFee(dataset);//放入优惠预存费用
	}
	else
	{
		var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
		$.feeMgr.removeFee(tradeTypeCode, "2", "0");//清空费用列表
		$.feeMgr.removeFee(tradeTypeCode, "2", "111");
	}
}
/* 选择新资费时进行元素互斥校验，获取开始结束时间等信息，并添加到已选区 */
function checkAddDiscnts(eleChecked)
{
	var selectedElementsDataset = getCheckedEleData();//获取已选展示区域元素记录
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	
	if(eleChecked.checked)
	{
		//校验数据准备
		var elements = new $.DatasetList();
		var discntInfo = new $.DataMap();
		discntInfo.put("PRODUCT_ID", $(eleChecked).attr("PRODUCT_ID"));
		discntInfo.put("PACKAGE_ID", $(eleChecked).attr("PACKAGE_ID"));
		discntInfo.put("PACKAGE_NAME", $(eleChecked).attr("PACKAGE_NAME"));
		discntInfo.put("ELEMENT_NAME", $(eleChecked).attr("ELEMENT_NAME"));
		discntInfo.put("ELEMENT_ID", $(eleChecked).attr("ELEMENT_ID"));
		discntInfo.put("ELEMENT_TYPE_CODE", $(eleChecked).attr("ELEMENT_TYPE_CODE"));
		discntInfo.put("FORCE_TAG", $(eleChecked).attr("FORCE_TAG"));
		discntInfo.put("DEFAULT_TAG", $(eleChecked).attr("DEFAULT_TAG"));
		discntInfo.put("MAIN_TAG", $(eleChecked).attr("MAIN_TAG"));
		elements.add(discntInfo);
		
		var params = "&SELECTED_ELEMENTS="+selectedElementsDataset.toString()+"&ELEMENTS="+elements.toString()+
					"&SYS_DATE="+$("#SYS_DATE").val();
		
		$.beginPageLoading("元素互斥校验中。。。");
		$.ajax.submit(this, 'checkElements', params, '',
		function(dataset)
		{
			var length = dataset.length;
			if(length > 0)
			{
				dataset.each(function(data)
				{
					if("" == data || null == data)//没有互斥元素，直接添加到已选区
					{
					}
					else
					{
						var limitTag = data.get("LIMIT_TAG");
						if("0" == limitTag)
						{
							var limitElementId = data.get("LIMIT_ELEMENT_ID");
							var limitElementName = data.get("LIMIT_ELEMENT_NAME");
							var limitElementTypecode = data.get("LIMIT_ELEMENT_TYPE_CODE");
							var limitElementTypeName = data.get("LIMIT_ELEMENT_TYPE_CODE") == "S" ? "服务" : "优惠";
							var elementTypeName = $(eleChecked).attr("ELEMENT_TYPE_CODE") == "S" ? "服务" : "优惠";
							
							var errorStr = limitElementTypeName+":"+limitElementName+"["+limitElementId+"]"+"与本次操作选择的"+elementTypeName+":"+eleChecked.ELEMENT_NAME+"["+eleChecked.ELEMENT_ID+"]存在互斥关系！";
							var result = window.confirm(errorStr+"\n点击“确定”按钮将添加本次操作的优惠，删除互斥元素\n点击“取消”按钮取消本次操作");
							if(!result)
							{
								$(eleChecked).attr("checked", false);
								return false;
							}
							else//清除互斥元素
							{
								$("#PE_"+limitElementTypecode+"_"+limitElementId).parent().parent().remove();//清除展示区已选的这一列元素信息
								if($("#DIS_"+limitElementTypecode+"_"+limitElementId))
								{
									$("#DIS_"+limitElementTypecode+"_"+limitElementId).attr("checked", false);
								}
							}
						}
					}
				});
				$.endPageLoading();
				if($(eleChecked).attr("checked"))
				{
					//setBaseElement(selectedElementsDataset);//处理基础优惠，待选区有互斥元素，则清除互斥元素，加入该包内的基础元素
					strElementData(eleChecked, dataset);//添加勾选元素
					//modemSaleTypeResource(eleChecked.ELEMENT_ID);
					insertBroadBandFee(dataset);//预存费用添加
				}
			}
		}, 
		function(error_code, error_info)
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		});
	}
	else//删除存在已选区域的优惠
	{
		var delTag = false;//删除了主优惠
		var discntCode = "";//标准优惠
		for(var i = 0; i < selectedElementsDataset.getCount(); i++)
		{
			var elementData = selectedElementsDataset.get(i);
			var chkPackageId =  $(eleChecked).attr("PACKAGE_ID");
			var chkElementId = $(eleChecked).attr("ELEMENT_ID");
			var chkElementTypeCode = $(eleChecked).attr("ELEMENT_TYPE_CODE");
			/*
			if("2" == elementData.get("MAIN_TAG") && "D" == elementData.get("ELEMENT_TYPE_CODE"))
			{
				discntCode = elementData.get("ELEMENT_ID");
			}
			*/
			
			if(elementData.get("PACKAGE_ID") == chkPackageId && elementData.get("ELEMENT_ID") == chkElementId && elementData.get("ELEMENT_TYPE_CODE") == chkElementTypeCode)
			{
				$("#PE_"+chkElementTypeCode+"_"+chkElementId).parent().parent().remove();//清除展示区已选的这一列优惠信息
				
				if("1" == elementData.get("MAIN_TAG") && "D" == chkElementTypeCode)//删除套餐主优惠时重新给MODEM、安装费用列表，避免优惠取消时有免费赠送的情况
				{
					/*
					delTag = true;
					var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
					var modemFee = $("#MODEM_FEE_BAK").val();
					var installFee = $("#INSTALL_FEE_BAK").val();
					var feeMode = "0";
					var modemTypeCode = "400";
					var installTypeCode = "410";
					
					$.feeMgr.removeFee(tradeTypeCode, feeMode, modemTypeCode);
					var feeData = new $.DataMap();
					feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
					feeData.put("MODE", feeMode);
					feeData.put("CODE", modemTypeCode);
					feeData.put("FEE",  modemFee);
					$.feeMgr.insertFee(feeData);
					
					$.feeMgr.removeFee(tradeTypeCode, feeMode, installTypeCode);
					var feeData1 = new $.DataMap();
					feeData1.put("TRADE_TYPE_CODE", tradeTypeCode);
					feeData1.put("MODE", feeMode);
					feeData1.put("CODE", installTypeCode);
					feeData1.put("FEE",  installFee);
					$.feeMgr.insertFee(feeData1);
					*/
					$.feeMgr.removeFee(tradeTypeCode, "2", "111");//清除预存
				}
			}
		}
		/*
		if(delTag)//当主优惠被删除时，需要重新给MODEM方式下拉框赋值
		{
			modemSaleTypeResource(discntCode);
		}
		*/
	}
}
/* 根据优惠重新给MODEM方式下拉框选项赋值 */
function modemSaleTypeResource(discntCode)
{
	$.beginPageLoading("MODEM方式查询中。。。");
	$.ajax.submit(this, 'getModemSaleType', '&DISCNT_CODE='+discntCode, 'modemArea',
	function(dataset)
	{
		$.endPageLoading();
		changeModemSaleType();
	}, 
	function(error_code, error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code, error_info);
	});
}
/* 基础优惠处理 */
function setBaseElement(selElements)
{
	var eleDatas = getReadyEleData();
	eleDatas.each
	(
		function(info)
		{
			var forceTag = info.get("FORCE_TAG");
			var defaultTag = info.get("DEFAULT_TAG");
			var elementId = info.get("ELEMENT_ID");
			var elementName = info.get("ELEMENT_NAME");
			var elementTypeCode = info.get("ELEMENT_TYPE_CODE");
			var productId = info.get("PRODUCT_ID");
			var packageId = info.get("PACKAGE_ID");
			var packageName = info.get("PACKAGE_NAME");
			var mainTag = info.get("MAIN_TAG");
			var rsrvStr1 = info.get("RSRV_STR1");
			
			if("1" == forceTag || "1" == defaultTag)
			{
				//校验数据准备
				var elements = new $.DatasetList();
				var eleInfo = new $.DataMap();
				eleInfo.put("PRODUCT_ID", productId);
				eleInfo.put("PACKAGE_ID", packageId);
				eleInfo.put("ELEMENT_ID", elementId);
				eleInfo.put("ELEMENT_TYPE_CODE", elementTypeCode);
				eleInfo.put("FORCE_TAG", forceTag);
				eleInfo.put("DEFAULT_TAG", defaultTag);
				elements.add(eleInfo);
				
				var params = "&SELECTED_ELEMENTS="+selElements.toString()+"&ELEMENTS="+elements.toString()+"&SYS_DATE="+$("#SYS_DATE").val();
				
				$.beginPageLoading("基础元素校验。。。");
				$.ajax.submit(this, 'checkElements', params, '',
					function(dataset)
					{
						var length = dataset.length;
						if(length > 0)
						{
							dataset.each(function(data)
							{
								if("" == data || null == data)//没有互斥元素，直接添加到已选区
								{
								}
								else
								{
									var limitTag = data.get("LIMIT_TAG");
									if("0" == limitTag)
									{
										var limitElementId = data.get("LIMIT_ELEMENT_ID");
										var limitElementTypecode = data.get("LIMIT_ELEMENT_TYPE_CODE");
										
										$("#PE_"+limitElementTypecode+"_"+limitElementId).parent().parent().remove();//清除展示区已选的这一列元素信息
									}
								}
							});
							
							eleInfo.put("ELEMENT_NAME", elementName);
							eleInfo.put("PACKAGE_NAME", packageName);
							eleInfo.put("START_DATE", dataset.get(0, "NEW_START_DATE"));
							eleInfo.put("END_DATE", dataset.get(0, "NEW_END_DATE"));
							eleInfo.put("MAIN_TAG", mainTag);
							eleInfo.put("RSRV_STR1", rsrvStr1);
							setCheckedElement(eleInfo);//添加基础元素
						}
						$.endPageLoading();
					},
					function(error_code, error_info)
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			}
		}
	);
}
/* 将待选区勾选的元素拼串 */
function strElementData(eleChecked, dataset)
{
	var eleData = new $.DataMap();
	eleData.put("PACKAGE_FORCE_TAG", "0");
	eleData.put("PRODUCT_ID", $(eleChecked).attr("PRODUCT_ID"));
	eleData.put("ELEMENT_NAME", $(eleChecked).attr("ELEMENT_NAME"));
	eleData.put("START_DATE", dataset.get(0, "NEW_START_DATE"));
	eleData.put("END_DATE", dataset.get(0, "NEW_END_DATE"));
	eleData.put("ELEMENT_TYPE_CODE", $(eleChecked).attr("ELEMENT_TYPE_CODE"));
	eleData.put("FORCE_TAG", $(eleChecked).attr("FORCE_TAG"));
	eleData.put("DEFAULT_TAG", $(eleChecked).attr("DEFAULT_TAG"));
	eleData.put("PACKAGE_ID", $(eleChecked).attr("PACKAGE_ID"));
	eleData.put("PACKAGE_NAME", $(eleChecked).attr("PACKAGE_NAME"));
	eleData.put("ELEMENT_ID", $(eleChecked).attr("ELEMENT_ID"));
	eleData.put("MAIN_TAG", $(eleChecked).attr("MAIN_TAG"));
	eleData.put("RSRV_STR1", $(eleChecked).attr("RSRV_STR1"));
	eleData.put("MODIFY_TAG", "0");
	setCheckedElement(eleData)
}
/* 将勾选的元素加到已选取元素展示区域 */
function setCheckedElement(data)
{
	var html=[];
	var checked = true;
	var disabled = false;
	var forceTag = data.get("FORCE_TAG")=="1"?"必选":"可选";
	var defaultTag = data.get("DEFAULT_TAG")=="1"?"默认":"非默认";
	var elementType = data.get("ELEMENT_TYPE_CODE")=="S"?"服务":"优惠";
	var forceColor = data.get("FORCE_TAG")=="1"?"e_orange":"e_green";
	var defaultColor = data.get("DEFAULT_TAG")=="1"?"e_blue":"e_green";
	
	html.push('<tr title="'+data.get("EXPLAIN")+'">');
	
	html.push('<td style="display:none"><input name="existEleInput" id="PE_'+data.get("ELEMENT_TYPE_CODE")+"_"+data.get("ELEMENT_ID")+'" value='+data.get("ELEMENT_ID")+' ');
	html.push(' PACKAGE_FORCE_TAG="'+data.get("FORCE_TAG")+'" ');
	html.push(' PRODUCT_ID="'+data.get("PRODUCT_ID")+'" ');
	html.push(' MODIFY_TAG="0" ');
	html.push(' ELEMENT_NAME="'+data.get("ELEMENT_NAME")+'" ');
	html.push(' START_DATE="'+data.get("START_DATE")+'" ');
	html.push(' END_DATE="'+data.get("END_DATE")+'" ');
	html.push(' ELEMENT_TYPE_CODE="'+data.get("ELEMENT_TYPE_CODE")+'" ');
	html.push(' FORCE_TAG="'+data.get("FORCE_TAG")+'" ');
	html.push(' DEFAULT_TAG="'+data.get("DEFAULT_TAG")+'" ');
	html.push(' PACKAGE_ID="'+data.get("PACKAGE_ID")+'" ');
	html.push(' ELEMENT_ID="'+data.get("ELEMENT_ID")+'" ');
	html.push(' MAIN_TAG="'+data.get("MAIN_TAG")+'" ');
	html.push(' RSRV_STR1="'+data.get("RSRV_STR1")+'" ');
	html.push(' PACKAGE_FORCE_TAG="'+"0"+'" ');
	html.push(' /></td>');
	
	html.push('<td title="'+data.get("PRODUCT_ID")+'" class="e_center" style="display:none">'+data.get("PRODUCT_ID")+'</td>');
	html.push('<td title="'+data.get("PACKAGE_ID")+'" class="e_center" style="display:none">'+data.get("PACKAGE_ID")+'</td>');
	html.push('<td title="'+data.get("PACKAGE_NAME")+'" class="e_center" width="20%">'+data.get("PACKAGE_NAME")+'</td>');
	html.push('<td title="'+data.get("ELEMENT_ID")+'" class="e_center" width="10%">'+data.get("ELEMENT_ID")+'</td>');
	html.push('<td title="'+data.get("ELEMENT_NAME")+'" class="e_center" width="20%">'+data.get("ELEMENT_NAME")+'</td>');
	html.push('<td title="'+forceTag+'" class="e_center '+ forceColor +'" class="e_center" width="5%">'+forceTag+'</td>');
	html.push('<td style="display:none">'+data.get("FORCE_TAG")+'</td>');
	html.push('<td title="'+defaultTag+'" class="e_center '+ defaultColor +'" class="e_center" width="5%">'+defaultTag+'</td>');
	html.push('<td style="display:none">'+data.get("DEFAULT_TAG")+'</td>');
	html.push('<td title="'+data.get("PRODUCT_ID")+'" class="e_center" width="10%">'+elementType+'</td>');
	html.push('<td style="display:none">'+data.get("ELEMENT_TYPE_CODE")+'</td>');
	html.push('<td title="'+data.get("START_DATE")+'" width="15%">'+data.get("START_DATE")+'</td>');
	html.push('<td title="'+data.get("END_DATE")+'" width="15%">'+data.get("END_DATE")+'</td>');
	html.push('<td style="display:none">'+data.get("MAIN_TAG")+'</td>');
	html.push('<td style="display:none">'+data.get("RSRV_STR1")+'</td>');
	html.push('<td style="display:none">'+"0"+'</td>');
	html.push('</tr>');
	
	$.insertHtml('beforeend', $("#SelectedElementsTable"), html.join(""));
}
/* 已选区选择的数据返回 */
function getCheckedEleData()
{
	var checkedDatas = $.DatasetList();
	$.each
	(
		$("input[name='existEleInput']"), function getElements(obj)
		{
			var data = new $.DataMap();
			data.put("PRODUCT_ID", this.PRODUCT_ID);
			data.put("PACKAGE_ID", this.PACKAGE_ID);
			data.put("ELEMENT_TYPE_CODE", this.ELEMENT_TYPE_CODE);
			data.put("ELEMENT_ID", this.ELEMENT_ID);
			data.put("ELEMENT_NAME", this.ELEMENT_NAME);
			data.put("FORCE_TAG", this.FORCE_TAG);
			data.put("DEFAULT_TAG", this.DEFAULT_TAG);
			data.put("MAIN_TAG", this.MAIN_TAG);
			data.put("RSRV_STR1", this.RSRV_STR1);
			data.put("MODIFY_TAG", "0");
			
			checkedDatas.add(data);
		}
	);
	return checkedDatas;
}
/* 待选区选择的数据返回 */
function getReadyEleData()
{
	var eleDatas = $.DatasetList();
	$.each
	(
		$("input[name='elementCheckBox']"), function getElements()
		{
			var data = new $.DataMap();
			data.put("PRODUCT_ID", this.PRODUCT_ID);
			data.put("PACKAGE_ID", this.PACKAGE_ID);
			data.put("PACKAGE_NAME", this.PACKAGE_NAME);
			data.put("ELEMENT_TYPE_CODE", this.ELEMENT_TYPE_CODE);
			data.put("ELEMENT_ID", this.ELEMENT_ID);
			data.put("ELEMENT_NAME", this.ELEMENT_NAME);
			data.put("FORCE_TAG", this.FORCE_TAG);
			data.put("DEFAULT_TAG", this.DEFAULT_TAG);
			data.put("MAIN_TAG", this.MAIN_TAG);
			data.put("RSRV_STR1", this.RSRV_STR1);
			
			eleDatas.add(data);
		}
	);
	return eleDatas;
}
//开户提交校验
function onTradeSubmit()
{
	if (!$.validate.verifyAll())
    {
		return false;
	}
	
	
	var mainSvcTag = false;//是否存在主体服务
	var speedTag = false;//是否存在速率优惠
	var rateTag = false;//速率是否超出地址安装最大值
	$.each
	(
		$("input[name='existEleInput']"), function getElements()
		{
			if("2" == this.MAIN_TAG && "D" == this.ELEMENT_TYPE_CODE)//必须存在2-基础标准套餐包优惠才能提交
			{
				speedTag = true;
				if(parseInt(this.RSRV_STR1) <= parseInt($("#MAX_WIDTH").val()))
				{
					rateTag = true;
				}
			}
			if("2019" == this.ELEMENT_ID && "S" == this.ELEMENT_TYPE_CODE)//必须存在618-主体服务才能提交，避免没有权限的问题
			{
				mainSvcTag = true;
			}
		}
	);
	
	if(!mainSvcTag)
	{
		$.showWarnMessage("服务提示", "产品包未配置宽带主体服务或该工号没有服务权限！");
    	return false;
	}

    
    
	var param = "&SELECTED_ELEMENTS=" + $.table.get("ElementsTable").getTableData(null, true);
	//param =param+"&JOIN_GROUP_TAG="+join_group_tag;//是否加入集团标识
	//param =param+"&GROUP_ACCT_ID="+group_acct_id;//集团账户
	$.cssubmit.addParam(param);
	
	var isChecked = $("#IS_CHECKED").val();
	if("1" == isChecked)//确认不绑定手机号码
	{
		var userInfo = $.auth.cacheData["AUTH_DATA"].get("USER_INFO");
		if(null != userInfo && "" != userInfo)
		{
			$.auth.cacheData["AUTH_DATA"].get("USER_INFO").put("SERIAL_NUMBER", "");//清空认证信息
		}
	}
	
	//$.cssubmit.bindCallBackEvent(setAlertBack);//设置提交业务后回调事件
	return true;
}
//页面加载时改变auth组件的label
/*
$(function()
{
	$("#SUPER_BANK_CODE").attr("disabled", true);
	$("#BANK_CODE").attr("disabled", true);
	$("#BANK_ACCT_NO").attr("disabled", true);
	
	$("#AUTH_SERIAL_NUMBER").attr("disabled", true);
	var htmlStr = '<label><input name="IS_CHECKED" id="IS_CHECKED" type="checkbox" class="e_checkbox" id="sceneid" disabled="true" onclick="changeDisabled()"/>绑定手机号：</label>';
	$("#TRADE_EPARCHY_NAME").parent().parent().prev().html(htmlStr);
	$("#AUTH_SERIAL_NUMBER").val("");
	$("#authArea").css("display", "");//为了让改变不是那么明显，先隐藏，改变之后再展示
});
*/
//执行成功回调
function setAlertBack(data)
{
	if(data && data.length>0)
	{
		var content = "点【确定】继续业务受理。";	
		if(data && data instanceof $.DatasetList && data.length>0)
		{
			var orderId = data.get(0).get("ORDER_ID");
			var accessAcct = data.get(0).get("ACCESS_ACCT");
			content = "宽带账号：" + accessAcct + "<br/>客户订单标识：" + orderId + "<br/>点【确定】继续业务受理。";
		}
		$.cssubmit.showMessage("success", "业务受理成功", content, true);
	}
}
//集团后续
/*
隐藏或显示集团信息区*/
function writeGroupInfo(btn)
{
	if("none" != $("#GroupPart").css("display"))
	{
		$("#GroupPart").css("display", "none");
		btn.children[0].className = "e_ico-unfold";
		btn.children[1].innerHTML = "填写集团信息";
	}
	else
	{
		$("#GroupPart").css("display", "");
		btn.children[0].className = "e_ico-fold";
		btn.children[1].innerHTML = "隐藏集团信息";
	}
}

function qryGroupAcctAll(){
	$.beginPageLoading("正在查询集团账户信息...");

	if($("#GROUP_SN").val()==""){
		alert("请输入集团号码");
		$.endPageLoading();
		return false;
	}else{
		$.ajax.submit('', 'getGroupAccts', "&SERIAL_NUMBER="+$("#GROUP_SN").val(), 'groupAcctsPart',
			function(datas) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			});
	}
}

function setAcctInfo(obj){
	$.beginPageLoading("正在查询账户余额信息...");
	$.ajax.submit('', 'getGroupAcctBalance', "&ACCT_ID="+obj.ACCT_ID, 'acctInfoPart',
			function(data) {
					$("#GROUP_ACCT_ID").val(obj.ACCT_ID);
					$("#GROUP_USER_ID").val(obj.USER_ID);
					$("#ALL_NEW_BALANCE").val(data.get("ALL_NEW_BALANCE"));
					$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			});
}
