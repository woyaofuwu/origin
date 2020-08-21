function setAddress()
{
	$("#STAND_ADDRESS").val("测试宽带移机地址");
	$("#DEVICE_ID").val("9527");
	$("#OPEN_TYPE").val("GPON");
	changeWidenetProduct.selectProductModeA();
}

if(typeof(ChangeWidenetProduct)=="undefined"){window["ChangeWidenetProduct"]=function(){};var changeWidenetProduct = new ChangeWidenetProduct();}
(function(){
	$.extend(ChangeWidenetProduct.prototype,{
		//展示与隐藏客户基本信息
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
		
		//标准地址选择
		selectProductModeA:function(){
			var userId = $("#USER_ID").val();
			var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#EPARCHY_CODE").val();
			var userProductId = $("#USER_PRODUCT_ID").val();
			
			$("#DETAIL_ADDRESS_1").val($("#STAND_ADDRESS").val());//将详细地址置为与标准地址相同的值
			$("#IS_NEED_CHG_PROD").val("0");//初始主产品是否存在变更
						
			var para = "&USER_ID="+userId
					 + "&SERIAL_NUMBER="+serialNumber
					 + "&ROUTE_EPARCHY_CODE="+eparchyCode
					 + "&DEVICE_ID="+$("#DEVICE_ID").val()
					 + "&AREA_CODE="+$("#AREA_CODE").val()
					 + "&OPEN_TYPE="+$("#OPEN_TYPE").val()
					 + "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();

			$.beginPageLoading("宽带产品查询。。。");
			$.ajax.submit('','initProductChg',para,'productTypePart,productModePart,ModelModePart',
				function(data){
					$.endPageLoading();
					var freeRight=data.get(0).get("MODEM_FREE_RIGHT");		 
                    var selfRight=data.get(0).get("MODEM_SELF_RIGHT");	
                    
                    if(typeof(MODEL_MODE) != "undefined" ){
						MODEL_MODE.remove("0");
						MODEL_MODE.remove("2");
						MODEL_MODE.remove("3");
						MODEL_MODE.append("租赁","0");
						MODEL_MODE.append("赠送","2");
						MODEL_MODE.append("自备","3");
					}
                    
                    if(freeRight==null || freeRight=="0"){	 	 
                        if(typeof(MODEL_MODE) != "undefined" ){
							MODEL_MODE.remove("2");
						}
                    }	 	 
                   	 	 
                    if(selfRight ==null || selfRight=="0"){	 	 
                        if(typeof(MODEL_MODE) != "undefined" ){
							MODEL_MODE.remove("3");
						}
                    }
					//拦截规则1：没有匹配到能更换的新宽带产品
					var noNewProduct = data.get(0).get("NO_NEW_PRODUCT");
					if(noNewProduct=="1"){
						MessageBox.alert("提示","该地址宽带类型中没有同速率的新宽带产品可以转换！");
						$("#STAND_ADDRESS").val("");
						return false;
					}
					
					//拦截规则2：不能从非ADSL宽带转为ADSL宽带
					var isAdslLimit = data.get(0).get("IS_ADSL_LIMIT");
					if(isAdslLimit=="1"){
						MessageBox.alert("提示","业务限制：不能从非ADSL宽带转为ADSL宽带，请重新选择标准地址！");
						$("#STAND_ADDRESS").val("");
						return false;
					}

					//拦截规则3：是否已经预约了主产品变更
					var isChgOther = data.get(0).get("IS_CHG_OTHER");//IS_CHG_OTHER 是否已存在主产品变更，判断依据为主产品的START_DATE是否大于sysdate，大于或等于值为1即存在未生效主产品
					var isNeedChgProd = data.get(0).get("IS_NEED_CHG_PROD");//IS_NEED_CHG_PROD 是否存在主产品类型变化 0-没变  1-变了  判断依据为新老产品的productMode是否相同
					if(isChgOther=="1"){
						if(isNeedChgProd=="1"){
							MessageBox.alert("提示","您当前有预约的主产品变更，不能办理跨宽带类型移机，请重新在标准地址中选择您的当前产品对应的宽带类型!");
							$("#STAND_ADDRESS").val("");
						}else{
							MessageBox.alert("提示","您当前有预约的主产品变更，只能办理移机业务，不能变更产品!");
						}
						return false;
					}
					$("#IS_NEED_CHG_PROD").val(isNeedChgProd);//选择了新地址后，PRODUCT_MODE宽带类型是否发生变化 1-变了 0-没变
					
					var newWideType = data.get(0).get("NEW_WIDE_TYPE");//移机后新宽带类型 1-移动FTTB 2-铁通ADSL 3-移动FTTH 5-铁通FTTH 6-铁通FTTB
					$("#NEW_WIDE_TYPE").val(newWideType);
					$("#OLD_WIDE_TYPE").val(data.get(0).get("OLD_WIDE_TYPE"));
					
					//拦截规则4：是否是开户首月优惠期间
					var strPriv = $("#WIDENETMOVE_FIRST").val();//1-在首月免费期内
					if(strPriv == '1')
					{
						$("#CHG_PRODUCT_ID").attr("disabled",true);//不允许修改宽带产品
						$("#NEW_PRODUCT_MODE").attr("disabled",true);//不允许修改宽带类型
						var strRsrvStr2 = $("#RSRV_STR2").val();//老宽带的productMode
						
						//如果新老宽带类型不同
						if(strRsrvStr2 != newWideType)
						{
							MessageBox.alert("提示","开户首月优惠期间，仅提供同制式、同速率宽带移机!");
							$("#STAND_ADDRESS").val("");
							$("#ADDRESS_BUILDING_NUM").val("");
							$("#DETAIL_ADDRESS_1").val("");
							$("#AREA_CODE").get(0).selectedIndex=0;
							$("#NEW_PRODUCT_MODE").get(0).selectedIndex=0;
							$("#CHG_PRODUCT_ID").get(0).selectedIndex=0;
							return false;
						}
					}
					
					//REQ201903080003一机多宽优惠活动开发需求调整
					var changeProductTag = data.get(0).get("CHANGE_PRODUCT_TAG");
					if("TRUE" == changeProductTag)
					{
						$("#CHG_PRODUCT_ID").attr("disabled",false);//允许修改宽带产品
						$("#IS_ONESN_MANYWIDE").val("1");	
					}
					//REQ201903080003一机多宽优惠活动开发需求调整
					
					//光猫逻辑1：是否需要申请光猫
					var isFtth = data.get(0).get("IS_FTTH");
					if(isFtth=="1"){
						$("#IS_NEED_MODEL").val(isFtth);//IS_NEED_MODEL 1--需要选光猫
					}/*
					  *REQ201804170012 宽带移机功能优化 modify by xuzh5 2018-6-20 11:23:04 
						else{
						if(data.get(0).get("OTHER_AREA_FLAG")=='FALSE'){//OTHER_AREA_FLAG 是否跨区
							MessageBox.alert("提示","未跨区，无须再次申领光猫。");
						}else if(data.get(0).get("OTHER_AREA_FLAG")=='TRUE'){
							MessageBox.alert("提示","跨区，但光猫厂家一样，无须再次申领光猫。");
						}
					}*/
					
					//光猫逻辑2：是否需要更换光猫
					var isNeedEx = data.get(0).get("IS_EXCHANGE_MODEL");//是否需要更换光猫
					if(isNeedEx=="0"){MessageBox.alert("提示","您现有的光猫在新地址下无法使用，请在页面选择新的光猫，同时请将您原申领的光猫在90天内到移动营业厅办理退还，如逾期未办理，光猫冻结预存款将自动沉淀");}
					if(isNeedEx=="4"){MessageBox.alert("提示","您现有的光猫在新地址下无法使用，请在页面选择新的光猫");}
					if(isNeedEx=="2"){MessageBox.alert("提示","您新的产品已经不需要光猫，请在90天内到移动营业厅办理退还，如逾期未办理，光猫冻结预存款将自动沉淀");}
					$("#IS_EXCHANGE_MODEL").val(isNeedEx);//是否需要更换光猫
					
					var param = "&USER_ID="+userId;
					//如果主产品类型变了即productMode，则需要进行宽带产品变更
					if(isNeedChgProd=="1"){
						var newProductIdValue = data.get(0).get("NEW_PRODUCT_ID_VALUE");
						var firstDayNextMonth = data.get(0).get("FIRST_DAY_NEXT_MONTH");
						var param="&USER_ID="+userId+"&USER_PRODUCT_ID="+userProductId+"&NEW_PRODUCT_ID="+newProductIdValue+"&BOOKING_DATE="+firstDayNextMonth;
						$("#IS_CHG_PROD").val("TRUE");
						$("#NEW_PRODUCT_ID").val(newProductIdValue);
						$("#FIRST_DAY_NEXT_MONTH").val(firstDayNextMonth);
					}
					
					selectedElements.renderComponent(param,eparchyCode);//初始化已选区元素
					offerList.renderComponent(null,eparchyCode);//初始化待选区为空，不提供元素选择
					
				},function(error_code, error_info,detail) {
				    $.endPageLoading();
				    MessageBox.alert("提示","处理失败！");
					$("#STAND_ADDRESS").val("");
    			});
			  /**
			   * 9级地址改造涉及
			   * @author zhuoyingzhi
			   * 20161017
			   */
			  afterAddressBuildingNum();
		},
		
		//宽带产品选择 貌似没用可以删除了
		afterChangeProduct: function(productId,productName,brandCode,brandName){
			var productId = $("#CHG_PRODUCT_ID").val();
			var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
			var data1="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			offerList.renderComponent(productId, eparchyCode);
			selectedElements.renderComponent(data1,eparchyCode,false);
			
			
			/*//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程
		   	var productId = $("#CHG_PRODUCT_ID").val();
		   	var param = "&NEW_PRODUCT_ID=" + productId;
			$.beginPageLoading("宽带产品速率查询。。。");
			$.ajax.submit(this, 'getProductRateByProductId',param, '',
					function(data) 
					{
						var new_rate = data.get("NEW_RATE");
						$.endPageLoading();
						var bandwidth = $("#BANDWIDTH").val();
						//alert('----------new_rate:'+new_rate+',bandwidth:'+bandwidth);
				        if((parseInt(new_rate) >= 1000*1024 && "200" == bandwidth)
				        		|| (parseInt(new_rate) >= 200*1024 && "" == bandwidth))
				        {
				        	MessageBox.alert("提示","您所选择的设备暂不支持"+parseInt(new_rate)/1024+"M产品");
				        	$("#CHG_PRODUCT_ID").val("");
			                return false;
				        }
				        
						var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
						var data1="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
						offerList.renderComponent(productId, eparchyCode);
						selectedElements.renderComponent(data1,eparchyCode,false);

					}, function(error_code, error_info) 
					{
			        	$("#WIDE_PRODUCT_ID").val("");
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});*/
			
			//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程
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

