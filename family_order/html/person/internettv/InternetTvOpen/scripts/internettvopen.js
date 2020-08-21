function displaySwitch(btn, o){
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}


/** 用戶认证结束之后执行的js方法 */
function refreshPartAtferAuth(data){
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,internetTvInfoPart',
			function(data) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true,"submitButton");
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

/** 魔百和产品选中 */
function queryPackages(){
	var productId = $("#PRODUCT_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	var param = "&PRODUCT_ID="+productId+"&SERIAL_NUMBER="+serialNumber+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId;

	$.beginPageLoading("基础优惠包和可选优惠包查询中......");
	$.ajax.submit(null, 'queryDiscntPackagesByPID', param, 'bPackagePart,oPackagePart,topSetBoxSaleActivePart',
			function(data) {
				//传出押金值
				$("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100);
				
				//如果临时押金值为空，则将临时押金值置为押金值
				if (null == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val() || '' == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val())
				{
					$("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100)
				}
				
				//将魔百和营销活动信息传出
				$("#MO_SALEACTIVE_LIST").val(data.get('TOP_SET_BOX_SALE_ACTIVE_LIST'));
				
				$("#MO_PRODUCT_ID").val('');
				$("#MO_PACKAGE_ID").val('');
				$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');//描述置为空
				
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
				$("#TOP_SET_BOX_PRODUCT_ID").val('');
			});
			
	//每次重新选择魔百和产品，都将营销活动的预存置为空
	$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
}

/** 魔百和营销活动校验 */
function changeTopSetBoxSaleActive(){

	var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
	var moSaleActiveList = $("#MO_SALEACTIVE_LIST").val();
	
	//如果选中的营销活动有值
	if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
	{
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
				if (topSetBoxSaleActiveId == moSaleActiveDataset.items[i].get('PARA_CODE2'))
				{
					topSetBoxSaleActiveProductId = moSaleActiveDataset.items[i].get("PARA_CODE4");
					topSetBoxSaleActivePackageId = moSaleActiveDataset.items[i].get("PARA_CODE5");
					topSetBoxSaleActiveExplain = moSaleActiveDataset.items[i].get("PARA_CODE24");
					ruleTag=moSaleActiveDataset.items[i].get("PARA_CODE22");
					depProdIds=moSaleActiveDataset.items[i].get("PARA_CODE23");
				}
			}
		}

		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
		var inparam = "&ROUTE_EPARCHY_CODE=" + eparchyCode 
					+ "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
					+ "&PRODUCT_ID=" + topSetBoxSaleActiveProductId
					+ "&PACKAGE_ID=" + topSetBoxSaleActivePackageId
					+ "&RULE_TAG=" + ruleTag
					+ "&DEP_PRODUCT_ID=" + depProdIds;
					
		$.beginPageLoading("魔百和营销活动校验中。。。");

		$.ajax.submit(null, 'checkSaleActive', inparam, null,
						function(data) 
						{
							$.endPageLoading();
							$("#MO_PRODUCT_ID").val(topSetBoxSaleActiveProductId);
							$("#MO_PACKAGE_ID").val(topSetBoxSaleActivePackageId);
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val(topSetBoxSaleActiveExplain);
							$("#TOP_SET_BOX_DEPOSIT").val("0");
							
							//营销活动费用需要重新取产品模型配置
					 		checkTopBoxSetSaleActiveFee(topSetBoxSaleActiveProductId,topSetBoxSaleActivePackageId);
							
						}, 
						function(error_code, error_info) 
						{
							$("#MO_PRODUCT_ID").val('');
							$("#MO_PACKAGE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
							$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
								
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						}
					);
	}
	else
	{
		$("#MO_PRODUCT_ID").val('');
		$("#MO_PACKAGE_ID").val('');
		$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');//描述置为空
		$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');//如果不选营销活动，则将活动预存值置为空
		$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
	}
}

//查询魔百和营销活动预存费用
function checkTopBoxSetSaleActiveFee(productId,packageId)
{
	$.beginPageLoading("魔百和宽带营销活动费用校验中。。。");
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var param = "&SERIAL_NUMBER="+ serialNumber + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=2";
				
	$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(fee);//很重要，将活动下的元素费用传出
					}, function(error_code, error_info) 
					{
						$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
								
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
}


/** 业务提交校验 */
function submitBeforeCheck(){
	var productId=$("#PRODUCT_ID").val();
	if(!productId || productId == ""){
		alert("魔百和产品不能为空！");
		return false;
	}
	
	var basePackages=$("#BASE_PACKAGES").val();
	if(!basePackages || basePackages == ""){
		alert("必选基础包不能为空！");
		return false;
	}

	if(!$.validate.verifyAll("widenetInfoPart") || !$.validate.verifyAll("internetTvInfoPart")) {
		return false;
	}
	
	//开始费用校验
	var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();//押金 单位：元
	var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();//营销活动预存 单位：分
	
	if (null == topSetBoxDeposit || '' == topSetBoxDeposit)
	{
		topSetBoxDeposit = '0';
	}
	if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
	{
		topSetBoxSaleActiveFee = '0';
	}
	
	var feeFlag = true;
	var totalFee = parseFloat(topSetBoxDeposit) + parseFloat(topSetBoxSaleActiveFee)/100;
				
	if (totalFee > 0)
	{
		feeFlag = false ;
		var tips = "您总共需要转出："+totalFee
			 	+ "元，其中魔百和押金："+topSetBoxDeposit
			 	+ "元，魔百和营销活动预存："+parseFloat(topSetBoxSaleActiveFee)/100
			 	+ "元，请确认您的余额是否足够？";

		if(window.confirm(tips))
		{
			//后台余额校验
			feeFlag = checkFeeBeforeSubmit();
		}
		else
		{
			return false;
		}
	}
				
	if(!feeFlag)
	{
		return false ;
	}

	return true;
}

/** 提交前费用校验 */
function checkFeeBeforeSubmit()
{
	var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();//单位：元
	var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();//单位：分
	var result = false;
				
	if (null == topSetBoxDeposit || '' == topSetBoxDeposit)
	{
		topSetBoxDeposit = '0';
	}
				
	if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
	{
		topSetBoxSaleActiveFee = '0';
	}
				
	var param = "&TOPSETBOX_DEPOSIT=" + topSetBoxDeposit 
			  + "&TOPSETBOX_SALE_ACTIVE_FEE=" + topSetBoxSaleActiveFee
			  + "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				
	$.beginPageLoading("提交前费用校验中。。。");			
	$.ajax.submit(null, 'checkFeeBeforeSubmit', param, null,
					function(data) 
					{
						$.endPageLoading();
						var resultCode = data.get("X_RESULTCODE");
						if(resultCode == '0')
							result = true;
					}, function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
						result = false ;
					},
					{async:false}
				);
				
	return result;
}
