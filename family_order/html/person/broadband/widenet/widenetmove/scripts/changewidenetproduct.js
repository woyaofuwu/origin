function setAddress()
{
	$("#STAND_ADDRESS").val("海南省海口市琼山区板桥路智德网通讯店周边52号旁电杆1层102");
	$("#DEVICE_ID").val("829858");
	$("#OPEN_TYPE").val("FTTH");
	changeWidenetProduct.selectProductModeA();
}

if(typeof(ChangeWidenetProduct)=="undefined"){window["ChangeWidenetProduct"]=function(){};var changeWidenetProduct = new ChangeWidenetProduct();}
(function(){
	$.extend(ChangeWidenetProduct.prototype,{
		afterSubmitSerialNumber: function(data){

			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			/*
			var openDate = data.get("USER_INFO").get("OPEN_DATE");
			var custName = data.get("CUST_INFO").get("CUST_NAME");
			
			$("#CUST_NAME").val(custName);
			$("#OPEN_DATE").val(openDate);
			*/
	
			$("#USER_PRODUCT_ID").val(userProductId);
			$("#USER_ID").val(userId);
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#NEW_PRODUCT_ID").val("");
			$("#NEXT_PRODUCT_ID").val("");
			$("#NEW_PRODUCT_START_DATE").val("");
			$("#OLD_PRODUCT_END_DATE").val("");
			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			$.ajax.submit(null,"loadChildInfo","&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val(),'productTypePart',changeWidenetProduct.afterLoadChildInfo);
			
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
			offerList.renderComponent(null,eparchyCode);
		},
		
		displaySwitch : function(btn, o) {
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
		},
		
		afterLoadChildInfo: function(data){
			var userProductArea = $("#USER_PRODUCT_NAME");
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			var userBrandArea = $("#USER_BRAND_NAME");
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			userProductArea.empty();
			nextProductArea.empty();
			userBrandArea.empty();
			nextBrandArea.empty();
			var userProductId = data.get(0).get("USER_PRODUCT_ID");
			var nextProductId = data.get(0).get("NEXT_PRODUCT_ID");
			var nextProductStartDate = data.get(0).get("NEXT_PRODUCT_START_DATE");
			selectedElements.setEnvProductId(userProductId,nextProductId,nextProductStartDate);
			$("#NEXT_PRODUCT_ID").val(nextProductId);
			$("#PRODUCT_ID").val(userProductId);
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				$('#productSelectBtn').attr("disabled",true);
			}
			var eparchyCode = data.get(0).get("EPARCHY_CODE");
			
			$.insertHtml('beforeend',userProductArea,data.get(0).get("USER_PRODUCT_NAME"));
			$.insertHtml('beforeend',userBrandArea,data.get(0).get("USER_BRAND_NAME"));
			$.insertHtml('beforeend',nextProductArea,data.get(0).get("NEXT_PRODUCT_NAME"));
			$.insertHtml('beforeend',nextBrandArea,data.get(0).get("NEXT_BRAND_NAME"));
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				offerList.renderComponent(nextProductId,eparchyCode);
			}
			else{
				offerList.renderComponent(userProductId,eparchyCode);
			}
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			$("#SALE_ACTIVE_LIST").val("");
			$("#SALE_ACTIVE_DESC").val("");
			$("#SALEACTIVE_PACKAGE_ID").val("");
			$("#SALEACTIVE_PRODUCT_ID").val("");
			$("#SALE_ACTIVE_FEE").val("");
			$("#SALE_ACTIVE_FEE_SHOW").val("");
		   	var productId = $("#CHG_PRODUCT_ID").val();
			var checkIndex=$("#CHG_PRODUCT_ID").get(0).selectedIndex;  //获取Select选择的索引值
			var modelCode = $("#MODEL_MODE").val();
			var isDependActive = $("#IS_DEPEND_ACTIVE").val();
			var oldPackageNameB = $("#SALE_PACKAGE_NAME").val();
			var oldProductId = $("#OLD_PROD_ID").val();
			var oldProdmode = $("#OLD_PROD_MODE").val();
			var productMode = $("#NEW_PRODUCT_MODE").val();
			var saleProductId = $("#SALE_PRODUCT_ID").val();
			
			if(productId==""||productId==null){
				offerList.renderComponent("",$("#USER_EPARCHY_CODE").val());
				var paraNull="&USER_ID="+$("#USER_ID").val();
				selectedElements.renderComponent(paraNull,$("#USER_EPARCHY_CODE").val());
				
				if(modelCode=="3"&&isDependActive=="1"){
					$("#MODEM_DEPOSIT").val(parseInt($("#FIRST_RENT").val())/100);
					var isNeedChgProdB = $("#IS_NEED_CHG_PROD").val();
					if(oldPackageNameB!=null&&oldPackageNameB!=""&&isNeedChgProdB!="1")
						$("#MODEM_DEPOSIT").val(parseInt($("#SECOND_RENT").val())/100);
				}
			}else{
				if(modelCode=="3"&&isDependActive=="1"){
					$("#MODEM_DEPOSIT").val(parseInt($("#FIRST_RENT").val())/100);
					var isNeedChgProdB = $("#IS_NEED_CHG_PROD").val();
					//if(oldPackageNameB!=null&&oldPackageNameB!=""&&isNeedChgProdB!="1")
					//	$("#MODEM_DEPOSIT").val($("#SECOND_RENT").val());
				}
			}
			$("#PRODUCT_ID_RATE").get(0).selectedIndex=checkIndex;
			var new_rate = $("#PRODUCT_ID_RATE").find("option:selected").text();
			var old_rate = $("#USER_WIDENET_RATE").val();
			//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
			if(saleProductId != "" && saleProductId != null && saleProductId == "66002202" && parseInt(new_rate) < 51200)
			{
				alert("您已经办理过度假宽带月、季、半年套餐（海南）营销活动，不能选择速率低于50M套餐，请重新选择更高档的套餐！");
				$("#CHG_PRODUCT_ID").val("");
				return false;
			}
			//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
			var hasEffActive = $("#HAS_EFF_ACTIVE").val();
			var hasEffYear = $("#HAS_EFF_YEAR").val();
			
			var hasYearActive = $("#HAS_YEAR_ACTIVE").val();
			var hasEndYearActive = $("#HAS_End_YEAR_ACTIVE").val();
			if((hasEffActive=="1"||hasEffYear=="1"||(hasYearActive=="1"&&hasEndYearActive=="1"))&&(old_rate!=null&&old_rate!="")&&(new_rate!=null&&new_rate!="")&&(parseInt(old_rate)>parseInt(new_rate))){
				alert("您已经办理过包年套餐或者未到期的营销活动，不能选择低档套餐，请重新选择同档或更高档的套餐！");
				$("#CHG_PRODUCT_ID").val("");
				
				if(modelCode=="3"&&isDependActive=="1"){
					$("#MODEM_DEPOSIT").val(parseInt($("#FIRST_RENT").val())/100);
					var isNeedChgProdB = $("#IS_NEED_CHG_PROD").val();
					if(oldPackageNameB!=null&&oldPackageNameB!=""&&isNeedChgProdB!="1")
						$("#MODEM_DEPOSIT").val(parseInt($("#SECOND_RENT").val())/100);
				}
				return false;
			}
			
			var newWideType = $("#NEW_WIDE_TYPE").val();
			var strRsrvStr2 = $("#RSRV_STR2").val();
			
			if(newWideType == strRsrvStr2 && parseInt(old_rate)==parseInt(new_rate))
			{
				alert("同制式、同速率的宽带产品不允许移机！");
				$("#CHG_PRODUCT_ID").val("");
				return false;
			}
			
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			nextProductArea.empty();
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			nextBrandArea.empty();
			$.insertHtml('beforeend',nextProductArea,productName);
			$.insertHtml('beforeend',nextBrandArea,brandName);
			
			var param = "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			if("07" == oldProdmode && "11" == productMode)
			{
				if(old_rate < new_rate)
				{
					param += "&REMOVE_GROUP_ID=41005405" ;
				}else if (old_rate == new_rate && "51200" == new_rate) {
					param += "&REMOVE_GROUP_ID=41005605" ;
				}
			}
			
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val(),param);
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val());
			$("#NEW_PRODUCT_ID").val(productId);
			if(productId==""){$("#IS_CHG_PROD").val("FALSE");}
			else{$("#IS_CHG_PROD").val("TRUE");}
			
			var isBusiness = $("#IS_BUSINESS_WIDE").val();
			if(isBusiness!="1"){
				var activeParam = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId
					+"&ROUTE_EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()+"&USER_ID="+$("#USER_ID").val()+"&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()
					+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&HAS_YEAR_ACTIVE="+$("#HAS_YEAR_ACTIVE").val()+"&HAS_End_YEAR_ACTIVE="+$("#HAS_End_YEAR_ACTIVE").val()
					+"&IS_HAS_YEAR_DISCNT="+$("#IS_HAS_YEAR_DISCNT").val();
				$.ajax.submit(null,"getSaleActiveList",activeParam,'saleActiveList,saleActiveDesc',
					function(data){
						var isHas = data.get(0).get("IS_HAS");
						if(isHas=="1"){
							//alert("您的产品已经发生变化，请选择新的营销活动或者包年套餐！");
						}
					}
				);
			}
		},
		
		afterRenderSelectedElements: function(data){
			if(data){
				var temp = data.get(0);
				if(temp.get("OLD_PRODUCT_END_DATE")){
					$("#OLD_PRODUCT_END_DATE").val(temp.get("OLD_PRODUCT_END_DATE"));
				}
				if(data.get(0).get("NEW_PRODUCT_START_DATE")){
					$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
				}
				if(temp.get("EFFECT_NOW_DISABLED")=="false"){
					$("#EFFECT_NOW").attr("disabled","");
				}
				else{
					$("#EFFECT_NOW").attr("disabled",true);
				}
				if(temp.get("EFFECT_NOW_CHECKED")=="true"){
					$("#EFFECT_NOW").attr("checked",true);
					$("#EFFECT_NOW").trigger("click");
				}
				else{
					$("#EFFECT_NOW").attr("checked","");
				}
			}
		},
		
		submit: function(){
			var canSubmit = selectedElements.checkForcePackage();
			if(!canSubmit){
				return false;
			}
			
			var data = selectedElements.getSubmitData();
			if(data&&data.length>0){
				var param = "&SELECTED_ELEMENTS="+data.toString()+"&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				if($("#EFFECT_NOW").attr("checked")){
					param+="&EFFECT_NOW=1";
				}
				$.cssubmit.addParam(param);
				return true;
			}
			else{
				alert("您没有进行任何操作，不能提交");
			}
		},
		
		selectProductModeA:function(){
			$("#SALEACTIVE_PACKAGE_ID").val("");
			$("#SALEACTIVE_PRODUCT_ID").val("");
			$("#SALE_ACTIVE_FEE").val("");
			$("#SALE_ACTIVE_FEE_SHOW").val("");
			var userProductId = $("#USER_PRODUCT_ID").val();
			var userId = $("#USER_ID").val();
			var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#EPARCHY_CODE").val();
			var productMode = $("#NEW_PRODUCT_MODE").val();
			var productMode = $("#NEW_PRODUCT_MODE").val();
			$("#DETAIL_ADDRESS_1").val($("#STAND_ADDRESS").val());
			$("#IS_NEED_CHG_PROD").val("0");
			var para = "&IS_BUSINESS_WIDE="+$("#IS_BUSINESS_WIDE").val()+"&DEVICE_ID="+$("#DEVICE_ID").val()+"&AREA_CODE="+$("#AREA_CODE").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&OPEN_TYPE="+$("#OPEN_TYPE").val()+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			selectedElements.setAcctDayInfo($("#ACCT_USER_DAY").val(),$("#FIRST_USER_DATE").val(),$("#NEXT_ACCT_DAY").val(),$("#NEXT_FIRST_DATE").val());
			$.beginPageLoading("宽带产品查询。。。");
			$.ajax.submit('','initProductChg',para,'productTypePart,productModePart,ModelModePart,saleActiveList,saleActiveDesc',
				function(data){
					$.endPageLoading();
					var freeRight=data.get(0).get("MODEM_FREE_RIGHT");
					var selfRight=data.get(0).get("MODEM_SELF_RIGHT");
					$("#MODEL_MODE option:[value='0']").remove();
					$("#MODEL_MODE option:[value='2']").remove();
					$("#MODEL_MODE option:[value='3']").remove();
					$("#MODEL_MODE").append("<option value='0'>租赁</option>");
					$("#MODEL_MODE").append("<option value='2'>赠送</option>");
					$("#MODEL_MODE").append("<option value='3'>自备</option>");
					if(freeRight==null || freeRight=="0"){
						$("#MODEL_MODE option:[value='2']").remove();
					}
					
					if(selfRight ==null || selfRight=="0"){
						$("#MODEL_MODE option:[value='3']").remove();
					}
					
					
					var showInfos = data.get(0).get("SHOW_INFOS");
					var showInfos1 = data.get(0).get("SHOW_INFOS1");
					var isSameDeviceId = data.get(0).get("IS_SAME_DEVICEID");
					var hasMonthDiscnt = data.get(0).get("HAS_MONTH_DISCNT");
					
					if(isSameDeviceId=="1"){
						alert("业务限制：您选择的标准地址和现在使用的一致，请重新选择标准地址！");
						$("#STAND_ADDRESS").val("");
						return false;
					}
					
					var isAdslLimit = data.get(0).get("IS_ADSL_LIMIT");
					if(isAdslLimit=="1"){
						alert("业务限制：不能从非ADSL宽带转为ADSL宽带，请重新选择标准地址！");
						$("#STAND_ADDRESS").val("");
						return false;
					}
				
					var isChgOther = data.get(0).get("IS_CHG_OTHER");
					var isNeedChgProd = data.get(0).get("IS_NEED_CHG_PROD");
					var isHasYearDiscnt = data.get(0).get("IS_HAS_YEAR_DISCNT");
					$("#IS_HAS_YEAR_DISCNT").val(isHasYearDiscnt);
					$("#HAS_YEAR_ACTIVE").val(data.get(0).get("HAS_YEAR_ACTIVE"));
					$("#HAS_End_YEAR_ACTIVE").val(data.get(0).get("HAS_End_YEAR_ACTIVE"));
					var isFtth = data.get(0).get("IS_FTTH");
					if(isFtth=="1"){
						//alert("您的宽带产品需要选择光猫，请在页面进行选择！");
						$("#IS_NEED_MODEL").val(isFtth);
					}else {
						if(data.get(0).get("OTHER_AREA_FLAG")=='FALSE'){
							alert("未跨区，无须再次申领光猫。");
						}else if(data.get(0).get("OTHER_AREA_FLAG")=='TRUE'){
							alert("跨区，但光猫厂家一样，无须再次申领光猫。");
						}
					}
					if(isChgOther=="1"){
						if(isNeedChgProd=="1"){
							alert("您当前有预约的主产品变更，不能办理跨宽带类型移机，请重新在标准地址中选择您的当前产品对应的宽带类型!");
							$("#STAND_ADDRESS").val("");
						}else alert("您当前有预约的主产品变更，只能办理移机业务，不能变更产品!");
						return false;
					}
					//if(isHasYearDiscnt=="1"){
					//	if(isNeedChgProd=="1"){
					//		alert("您当前有生效的包年产品，不能跨宽带类型移机，请重新在标准地址中选择您的当前产品对应的宽带类型!");
					//		$("#STAND_ADDRESS").val("");
					//	}else{
					//		alert("您有生效的包年产品，当前只能办理移机业务，不能办理产品变更！");
					//	}
					//	return false;
					//}
					$("#IS_NEED_CHG_PROD").val(isNeedChgProd);
					
					//changeWidenetProduct.afterLoadChildInfo(data);
					if(isNeedChgProd=="1")
					{
						if(hasMonthDiscnt=="1")
						{
							alert("您订购的宽带包月优惠不能进行产品变更，只能办理同类型宽带移机！");
							$("#STAND_ADDRESS").val("");
							$("#ProdShowArea").css('display', 'none');
							return false;
						}
						else
						{
							alert("您的宽带产品已经发生变化，请重新选择您需要的宽带产品");
						}
					}
					else
					{
						if(hasMonthDiscnt=="1")
						{
							alert("您订购的宽带包月优惠不能进行产品变更操作！");
							
							$("#ProdShowArea").css('display', 'none');
						}
					}
					
					var isNeedEx = data.get(0).get("IS_EXCHANGE_MODEL");
					//if(isNeedEx=="1"){alert("您现有的光猫可以继续使用，如果需要更换，请在页面选择，同时请将您原申领的光猫在90天内到移动营业厅办理退还，如逾期未办理，光猫冻结预存款将自动沉淀");}
					//if(isNeedEx=="5"){alert("您现有的光猫可以继续使用，如果需要更换，请在页面选择");}
					if(isNeedEx=="0"){alert("您现有的光猫在新地址下无法使用，请在页面选择新的光猫，同时请将您原申领的光猫在90天内到移动营业厅办理退还，如逾期未办理，光猫冻结预存款将自动沉淀");}
					if(isNeedEx=="4"){alert("您现有的光猫在新地址下无法使用，请在页面选择新的光猫");}
					if(isNeedEx=="2"){alert("您新的产品已经不需要光猫，请在90天内到移动营业厅办理退还，如逾期未办理，光猫冻结预存款将自动沉淀");}
					$("#IS_EXCHANGE_MODEL").val(isNeedEx);
					
					$("#PRODUCT_ID_RATE").val(userProductId);
					$("#USER_WIDENET_RATE").val($("#PRODUCT_ID_RATE").find("option:selected").text());
					
					var hasEffActive = data.get(0).get("HAS_EFF_ACTIVE");
					$("#HAS_EFF_ACTIVE").val(hasEffActive);
					var hasEffYear = data.get(0).get("HAS_EFF_YEAR");
					$("#HAS_EFF_YEAR").val(hasEffYear);
					var oldRate = data.get(0).get("OLD_PRODUCT_RATE");
					$("#USER_WIDENET_RATE").val(oldRate);
					
					var newWideType = data.get(0).get("NEW_WIDE_TYPE");
					$("#NEW_WIDE_TYPE").val(newWideType);
					
					var strPriv = $("#WIDENETMOVE_FIRST").val();
					
					if(strPriv == '1')
					{
						$("#CHG_PRODUCT_ID").attr("disabled",true);
						$("#NEW_PRODUCT_MODE").attr("disabled",true);
						$("#SALE_ACTIVE_LIST").attr("disabled",true);
						var strRsrvStr2 = $("#RSRV_STR2").val();
						if(strRsrvStr2 != newWideType)
						{
							alert("开户首月优惠期间，仅提供同制式、同速率宽带移机!");
							$("#STAND_ADDRESS").val("");
							$("#ADDRESS_BUILDING_NUM").val("");
							$("#DETAIL_ADDRESS_1").val("");
							$("#AREA_CODE").get(0).selectedIndex=0;
							$("#NEW_PRODUCT_MODE").get(0).selectedIndex=0;
							$("#CHG_PRODUCT_ID").get(0).selectedIndex=0;
							$("#SALE_ACTIVE_LIST").get(0).selectedIndex=0;
							return false;
						}
					}
					
					var iUMN = data.get(0).get("IS_USE_MODEL_NOW");
					$("#IS_USE_MODEL_NOW").val(iUMN);
					var isDMN = data.get(0).get("IS_DISCNT_MODEL_NOW");
					$("#IS_DISCNT_MODEL_NOW").val(isDMN);
					var idDmB = data.get(0).get("IS_DISCNT_MODEL_BEFORE");
					$("#IS_DISCNT_MODEL_BEFORE").val(idDmB);
					var dMnm = data.get(0).get("DISCNT_MODEL_NOW_MONEY");
					$("#DISCNT_MODEL_NOW_MONEY").val(dMnm);
					var ndmb = data.get(0).get("NUM_DISCNT_MODEL_BEFORE");
					$("#NUM_DISCNT_MODEL_BEFORE").val(ndmb);
					
					var param="&USER_ID="+userId;
					selectedElements.renderComponent(param,eparchyCode);
					offerList.renderComponent(null,eparchyCode);
				},function(error_code, error_info,detail) { 
				    $.endPageLoading();
    			});
			  /**
			   * 9级地址改造涉及
			   * @author zhuoyingzhi
			   * 20161017
			   */
			  afterAddressBuildingNum();
		},
		
		displaySwitch:function(btn,o) {
			var button = $(btn);
			var div = $('#'+o);
			if (div.css('display') != "none")
			{
				div.css('display', 'none');
				button.children("i").attr('className', 'e_ico-unfold'); 
				button.children("span:first").text("显示客户信息");
			}
			else {
				div.css('display', '');
				button.children("i").attr('className', 'e_ico-fold'); 
				button.children("span:first").text("隐藏客户信息");
			}
		}
	});
})();
/**
 * 9级地址改造涉及
 * @author zhuoyingzhi
 * 20161014
 */
function afterAddressBuildingNum(){
	 //获取楼层和房号
	if($("#ADDRESS_BUILDING_NUM")){
		var address_building_num=$("#ADDRESS_BUILDING_NUM").val();
		if(address_building_num !=''){
			 //有值
			$("#ADDRESS_BUILDING_NUM").attr("disabled", true);
			
			$("#FLOOR_AND_ROOM_NUM_FLAG").val('1');
		}else{
			//没有值
			$("#ADDRESS_BUILDING_NUM").val(''); 
			$("#ADDRESS_BUILDING_NUM").attr("disabled", false);
			
			$("#FLOOR_AND_ROOM_NUM_FLAG").val('0');
		}
	}
}

