

if(typeof(ChangeWidenetProduct)=="undefined"){window["ChangeWidenetProduct"]=function(){};var changeWidenetProduct = new ChangeWidenetProduct();}
(function(){
	$.extend(ChangeWidenetProduct.prototype,{
		SALE_ACTIVE_LIST: $.DatasetList(),  //营销活动列表
		PRODUCT_LIST: $.DatasetList(),      //产品列表
		afterSubmitSerialNumber: function(data){

			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	
			$("#USER_PRODUCT_ID").val(userProductId);
			$("#USER_ID").val(userId);
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#NEW_PRODUCT_ID").val("");
			$("#NEXT_PRODUCT_ID").val("");
			$("#NEW_PRODUCT_START_DATE").val("");
			$("#OLD_PRODUCT_END_DATE").val("");
			$("#CHANGE_UP_DOWN_TAG").val('0');
			$("#NEW_PACKAGE_ID").val('');
			selectedElements.clearCache();
			var acctDayInfo = data.get("ACCTDAY_INFO");
			selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
			var para_str = "&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			para_str = para_str+"&SERIAL_NUMBER_A="+$("#AUTH_SERIAL_NUMBER").val();
			$.beginPageLoading("查询校验中。。。。。");
			$.ajax.submit(null,"loadChildInfo",para_str,'productTypePart,changetimePart,curactivePart,productHiddenPart',
				function(data)
				{
					$.endPageLoading();
					changeWidenetProduct.afterLoadChildInfo(data);
					
					//预先选择传过来希望变更的宽带产品
					var preProductId = $("#PRE_PRODUCT_ID").val();
					if(preProductId != null && preProductId !=""){
						var hasProductId = false;
						var productList = changeWidenetProduct.PRODUCT_LIST;
						var obj = document.getElementById("PRODUCT_ID");
						if(productList != null && productList != '')
						{
							for (var i = 0; i < productList.length; i++)
							{
								if (preProductId == productList.get(i).get('PRODUCT_ID')){
									obj.selectedText = productList.get(i).get('PRODUCT_NAME');
									hasProductId = true;
									break;
								}
							}
						}
						if(hasProductId){
							$("#PRODUCT_ID").val(preProductId);
							changeWidenetProduct.afterChangeProductNew(obj);
						}else{
							MessageBox.alert("提示","抱歉，该用户不能变更为ID是"+preProductId+"的宽带产品产品，请您重新选择宽带产品！")
						}
					}
				},
				function(error_code, error_info) 
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				}
			);
			
			var param="&USER_ID="+userId;
			selectedElements.renderComponent(param,eparchyCode);
			offerList.renderComponent(null,eparchyCode);
		},
		
		afterLoadChildInfo: function(data){
			
			var userProductArea = $("#USER_PRODUCT_NAME");
			var userProductDateArea = $("#USER_PRODUCT_DATE");
			
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			var nextProductDateArea = $("#NEXT_PRODUCT_DATE");
			
			userProductArea.empty();
			userProductDateArea.empty();
			nextProductArea.empty();
			nextProductDateArea.empty();
			
			var userProductId = data.get(0).get("USER_PRODUCT_ID");
			var userProductName = data.get(0).get("USER_PRODUCT_NAME");
			var userProductStartDate = data.get(0).get("USER_PRODUCT_START_DATE");
			var userProductEndDate = data.get(0).get("USER_PRODUCT_END_DATE");
			
			var nextProductId = data.get(0).get("NEXT_PRODUCT_ID");
			var nextProductName = data.get(0).get("NEXT_PRODUCT_NAME");
			var nextProductStartDate = data.get(0).get("NEXT_PRODUCT_START_DATE");
			var nextProductEndDate = data.get(0).get("NEXT_PRODUCT_END_DATE");
			
			selectedElements.setEnvProductId(userProductId,nextProductId,nextProductStartDate);
			
			$("#NEXT_PRODUCT_ID").val(nextProductId);
			$("#PRODUCT_ID").val(userProductId);
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				$('#productSelectBtn').attr("disabled",true);
			}
			var eparchyCode = data.get(0).get("EPARCHY_CODE");
			
			if(typeof(userProductName)!="undefined"&&userProductName!=null&&userProductName!=''){
				$("#OLD_PRODUCT").css("display","")
				$.insertHtml('beforeend',userProductArea,userProductName);
				$.insertHtml('beforeend',userProductDateArea,userProductStartDate+" ~ "+userProductEndDate);
			}else{
				$("#OLD_PRODUCT").css("display","none")
			}
			
			if(typeof(nextProductName)!="undefined"&&nextProductName!=null&&nextProductName!=''){
				$("#NEW_PRODUCT").css("display","")
				$.insertHtml('beforeend',nextProductArea,nextProductName);
				$.insertHtml('beforeend',nextProductDateArea,nextProductStartDate+" ~ "+nextProductEndDate);
			}else{
				$("#NEW_PRODUCT_ITEM").css("display","none")
			}
			
			if(typeof(nextProductId)!="undefined"&&nextProductId!=null&&nextProductId!=''){
				offerList.renderComponent(nextProductId,eparchyCode);
			}
			else{
				offerList.renderComponent(userProductId,eparchyCode);
			}
			
			var productList = data.get(0).get("PRODUCT_LIST");
			changeWidenetProduct.PRODUCT_LIST.clear();
			changeWidenetProduct.PRODUCT_LIST = productList;
			
			$("#USER_WIDENET_RATE").val(data.get(0).get("USER_PRODUCT_RATE"));
			
			$("#KDTS_PRODUCT").val(data.get(0).get("KDTS_PRODUCT"));
			
			
			//设置预约时间
			$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());
			//初始化产品列表后，在根据当前的产品初始化对应的营销活动列表
			changeWidenetProduct.initActivePackage(userProductId);
			$("#curactivePart").css("display","");
		},
		
		afterChangeProduct: function(productId,productName,brandCode,brandName){
		   var productId = $("#PRODUCT_ID").val();
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			nextProductArea.empty();
			var nextBrandArea = $("#NEXT_BRAND_NAME");
			nextBrandArea.empty();
			$.insertHtml('beforeend',nextProductArea,productName);
			$.insertHtml('beforeend',nextBrandArea,brandName);
			offerList.renderComponent(productId,$("#USER_EPARCHY_CODE").val());
			
			var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId;
			selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val(),false);
			$("#NEW_PRODUCT_ID").val(productId);
			
			//选择产品后要判断是升档还是降档
			var checkIndex=$("#PRODUCT_ID").get(0).selectedIndex;  //获取Select选择的索引值
			$("#PRODUCT_ID1").get(0).selectedIndex=checkIndex;
			var new_rate = $("#PRODUCT_ID1").find("option:selected").text();
			var old_rate = $("#USER_WIDENET_RATE").val();
			//alert("USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val());
			//alert("NEW_PRODUCT_ID="+productId);
			//alert(new_rate);
			//alert(old_rate);

			$("#CHANGE_UP_DOWN_TAG").val("0");//先给个初始值
			if ($("#USER_PRODUCT_ID").val() != productId && old_rate==new_rate)
			{
				//速率不变，但是产品变了，如：候鸟20M---普通20M
				$("#CHANGE_UP_DOWN_TAG").val("3");
			}
			var booktag = $("#V_BOOK_TAG").val();
			if (parseInt(new_rate)>parseInt(old_rate))
			{
				//升档
				$("#CHANGE_UP_DOWN_TAG").val("1");//升档标志
				var eparchyCode = $("#USER_EPARCHY_CODE").val();
				var para_str = "&USER_ID="+$("#USER_ID").val()+"&V_END_DATE="+$("#V_END_DATE").val()+"&ROUTE_EPARCHY_CODE="+eparchyCode;
				$.beginPageLoading("校验中。。。。。");
				$.ajax.submit(null,"resetBookTime_UP",para_str,'changetimePart',function(data){
						//预约时间处理
						$.endPageLoading();
						changeWidenetProduct.selectFirstDate($("#PACKAGE_VALID"));
					},
					function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			}else if (parseInt(new_rate)<parseInt(old_rate))
			{
				//降档
				if (booktag=="0")//活动到期时间还在3个月后
				{
					if ($("#PRODUCT_YEAR").val()=="1")
					{
						MessageBox.alert("提示","您的包年套餐还未到期，不能办理降档宽带产品及活动！");
						$("#PRODUCT_ID").get(0).selectedIndex=checkIndex;
						return ;
					}
					if ($("#PRODUCT_ACTIVE").val()=="1")
					{
						MessageBox.alert("提示","您的营销活动还未到期，不能办理降档宽带产品及活动！");
						$("#PRODUCT_ID").get(0).selectedIndex=checkIndex;
						return ;
					}
				}else
				{
					$("#CHANGE_UP_DOWN_TAG").val("2");//降档标志
					var eparchyCode = $("#USER_EPARCHY_CODE").val();
					var para_str = "&USER_ID="+$("#USER_ID").val()+"&V_END_DATE="+$("#V_END_DATE").val()+"&ROUTE_EPARCHY_CODE="+eparchyCode;
					//alert("BookTime_Down"+para_str);
					$.beginPageLoading("校验中。。。。。");
					$.ajax.submit(null,"resetBookTime_Down",para_str,'changetimePart',
						function(data){
							$.endPageLoading();
							//预约时间处理
							changeWidenetProduct.selectFirstDate($("PACKAGE_VALID"));
						},
						function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						}
					);
				}
				
			}
			//初始化营销活动包列表
			changeWidenetProduct.initActivePackage(productId);
		},
		//根据产品ID获取产品速率
		getProductRateById:function(newProductId){
			var new_rate = "0";
			var productList = changeWidenetProduct.PRODUCT_LIST;
			if(productList != null && productList != '')
			{
				for (var i = 0; i < productList.length; i++)
				{
					if (newProductId == productList.get(i).get('PRODUCT_ID'))
					{
						new_rate = productList.get(i).get('WIDE_RATE');
					}
				}
			}
			return new_rate;
		},
		
		//根据产品ID获取产品速率
		getProductRateSpcById:function(newProductId){
			var new_rate_spc = "0";
			var productList = changeWidenetProduct.PRODUCT_LIST;
			if(productList != null && productList != '')
			{
				for (var i = 0; i < productList.length; i++)
				{
					if (newProductId == productList.get(i).get('PRODUCT_ID'))
					{
						new_rate_spc = productList.get(i).get('WIDE_RATE_SPECIAL');
					}
				}
			}
			return new_rate_spc;
		},
		//选择产品后
		afterChangeProductNew:function(obj){
			var newProductId = $("#PRODUCT_ID").val();
		   	var userCurProductId = $("#USER_PRODUCT_ID").val();
		   	
		   	//设置预约产品的名称及品牌
			var nextProductArea = $("#NEXT_PRODUCT_NAME");
			nextProductArea.empty();
		   	if(newProductId == null || newProductId == '')
		   	{
		   		$("#NEW_PRODUCT").css("display","none");
		   		MessageBox.alert("提示","宽带产品不能为空,请选择!");
		   		return ;
		   	}
		   	if(newProductId != '' && newProductId == userCurProductId )
		   	{
		   		$("#CHANGE_UP_DOWN_TAG").val('0');
		   	}
		   	
		   	//需要先进行产品的升级档判断，判断是否允许受理
		   	//选择产品后要判断是升档还是降档

			//获取新产品速率
			var new_rate = changeWidenetProduct.getProductRateById(newProductId);
			
			//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程 start
/*			var bandwidth = $("#BANDWIDTH").val();
			var checkBandWidthTag = $("#CHECK_BANDWIDTH_TAG").val();
			if("1" == checkBandWidthTag)
			{
		        if((parseInt(new_rate) >= 1000*1024 && "200" == bandwidth)
		        		|| (parseInt(new_rate) >= 200*1024 && "" == bandwidth))
		        {
		        	MessageBox.alert("提示","您所选择的设备暂不支持"+parseInt(new_rate)/1024+"M产品");
					$("#PRODUCT_ID").val('');
		        	return ;
		        }
			}*/
			//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程 end
			
			var old_rate = $("#USER_WIDENET_RATE").val();
			
			var bookTag = $("#V_BOOK_TAG").val();          //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
			var isYearProduct = $("#PRODUCT_YEAR").val();  //是否包年套餐，1：是，0：否
			var haveSaleActive = $("#PRODUCT_ACTIVE").val(); //是否有营销活动，1：是,0:否
			var endFlag = $("#END_FLAG").val();  //是否最后一个月
			var rateDownUpFlag = "0";  //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
			var effectiveDate = $("#PACKAGE_VALID").val();  //用户选择的生效时间
			var agreementEndDate = $("#V_END_DATE").val();  //用户包年套餐或营销活动的结束时间
			var mobileTVEndDate = $("#MOBILE_TV_END_DATE").val();//尝鲜活动结束日期
			
			var hasMobileTV = $("#HAS_MOBILE_TV").val();
			if(new_rate< 50*1024 && hasMobileTV=='1' && changeWidenetProduct.dateCompare(mobileTVEndDate,effectiveDate)){
				$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
				MessageBox.alert("提示","您当前有移动电视尝鲜营销活动未到期,不能进行降档操作!");
				return ;
			}
			
			if(Number(new_rate) <  Number(old_rate))
			{
				//降档操作
				//判断原套餐是否包年或有营销活动
				
				//包年套餐或营销活动未到期的情况
				if("0" == bookTag)
				{
					if("1" == isYearProduct)
					{
						$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
						MessageBox.alert("提示","您当前有包年套餐或营销活动还未到期,不能进行降档操作!");
						return ;
					}

					if("1" == haveSaleActive)
					{
						$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
						MessageBox.alert("提示","您当前有营销活动未到期,不能进行降档操作!");
						return ;
					}
				}
				else
				{
					//包年套餐或营销活动3个月内到期的情况或无包年及营销活动的情况
					//包年或营销活动3个月内到期，预约日期，只能选择协议到期后的次月1日,调用Ajax 计算日期
					//无包年或营销活动，只能选择次月1日生效
					if("1" == isYearProduct)
					{
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
							MessageBox.alert("提示","您的原产品中有包年套餐,还未到期,不能进行降档操作!");
							return ;
						}
					}
					
					if("1" == haveSaleActive)
					{
						//判断是否最后一个月
						
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
							$("#PRODUCT_ID").val(userCurProductId);	
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
					}
				}
				rateDownUpFlag = "2";
			}
			else if(Number(new_rate) >  Number(old_rate))
			{
				//升档操作
				//升档不限制生效时间，可选择从下月开始的最近3个月内生效
				
				rateDownUpFlag = "1";
			}
		   	else if(Number(new_rate) ==  Number(old_rate))
		   	{
		   		//速率不变
		   		//需判断产品是否该变的情况，产品未变，预约日期为次月1日
		   		//需要产品已变，速率不变，需要判断是否有包年或营销活动
		   		if(newProductId != userCurProductId)
		   		{
		   			//判断原套餐是否包年或有营销活动
					//包年套餐或营销活动未到期的情况
					if("0" == bookTag)
					{
						if("1" == isYearProduct)
						{
							var new_rate_spc = changeWidenetProduct.getProductRateSpcById(newProductId);
							if("1" != new_rate_spc){
								$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
								MessageBox.alert("提示","您的原产品速率与新选择的产品速率相同,您的原产品中包年套餐未到期,不能进行次操作!");
								return ;
							}	
						}
						
						if("1" == haveSaleActive)
						{
							var new_rate_spc = changeWidenetProduct.getProductRateSpcById(newProductId);
							if("1" != new_rate_spc){
								$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
								MessageBox.alert("提示","您的原产品速率与新选择的产品速率相同,您当前有营销活动未到期,不能进行降档操作!");
								return ;
							}
						}
					}
					else
					{
						//包年套餐或营销活动3个月内到期的情况或无包年及营销活动
						//预约日期，如果有包年及营销活动，只能选择协议到期后的次月1日
						//如果无包年及营销活动，只能选择次月1日，调用Ajax 计算日期
						if("1" == isYearProduct && endFlag == "1" )
						{
							$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
							MessageBox.alert("提示","您的原产品中有包年套餐,还未到期,不能进行降档操作!");
							return ;
						}
						
						if("1" == haveSaleActive)
						{
							//判断是否最后一个月
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								var tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
								$("#PRODUCT_ID").val(userCurProductId); //清空所选择的产品
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
						}
						rateDownUpFlag = "3";
					}
		   		}
		   	}
			
			//REQ201910240002 融合套餐与宽带速率适配系统提醒功能—BOSS侧 start
            var params = "&NEW_PRODUCT_ID=" + newProductId
            + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
		    $.ajax.submit(null, "getNewProductTips", params, null,
		        function (rtnData) {
		            if (rtnData != null && rtnData.length > 0) {
		            	if("0000"==rtnData.get("X_RESULTCODE")){
		            		MessageBox.alert(rtnData.get("X_RESULTINFO"));
		            	}
		            }
		        },
		        function (error_code, error_info, detail) {
		            MessageBox.error("错误提示", error_info, null, null, null, detail);
		        });
            //REQ201910240002 融合套餐与宽带速率适配系统提醒功能—BOSS侧 end
		   	
		   	$("#CHANGE_UP_DOWN_TAG").val(rateDownUpFlag);
		   	
		   	if(rateDownUpFlag != "")
		   	{
		   		//设置参数
			   /*
			    var bookTimeParam = "&USER_ID=" + $("#USER_ID").val();
			   	bookTimeParam += "&USER_PRODUCT_ID=" + $("#USER_PRODUCT_ID").val() 
			   	bookTimeParam += "&NEW_PRODUCT_ID=" + newProductId;
			   	bookTimeParam += "&USER_WIDENET_RATE=" + $("#USER_WIDENET_RATE").val();
			   	bookTimeParam += "&NEW_WIDENET_RATE=" + new_rate;
			   	bookTimeParam += "&IS_YEAR_PRODUCT=" + isYearProduct;
			   	bookTimeParam += "&HAVE_SALE_ACTIVE=" + haveSaleActive;
			   	bookTimeParam += "&CHANGE_UP_DOWN_TAG=" + rateDownUpFlag;
			   	bookTimeParam += "&ROUTE_EPARCHY_CODE=" + $("#USER_EPARCHY_CODE").val();
			   	bookTimeParam += "&V_END_DATE=" + $("#V_END_DATE").val();
			   	bookTimeParam += "&BOOK_TAG=" + bookTag;

			   	//同步调用，后续的处理必须等待该ajax处理完成
			   	$.ajax.submit(null,"resetBookTime",bookTimeParam,'changetimePart',
			   		function(data){
						$.endPageLoading();
					},
					function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					},
					{async:false}
				);
				*/
				$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());  //设置预约时间，产品组件需要
				
				
				
				//刷新产品包选择区域
				offerList.renderComponent(newProductId,$("#USER_EPARCHY_CODE").val());
				
				var data="&USER_ID="+$("#USER_ID").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+newProductId;
				selectedElements.renderComponent(data,$("#USER_EPARCHY_CODE").val(),false);
				$("#NEW_PRODUCT_ID").val(newProductId);
			
			   	//重新选择生效时间
				changeWidenetProduct.selectFirstDate($("#PACKAGE_VALID"));
				
				//初始化营销活动包列表
				changeWidenetProduct.initActivePackage(newProductId);
				
				$("#NEW_PRODUCT").css("display","");
				var selectedText = obj.selectedText;
				$.insertHtml('beforeend',nextProductArea,selectedText);
		   	}
		},
		
		
		//获取选择产品后的服务id，用户办理营销活动时的规则校验
		getActiveRuleSvcCheck:function(){
			var sel_svcid="";
			var uls = $("#SelectSvcUl").children("li");
			uls.each(function(i,li){
				var li_class = $(li).attr("class");
				if (li_class.indexOf("new")>=0)
				{
					var svc_val = $(li).children("label").children("div").children(".fn").children("input").val();
					if (sel_svcid=="")
					{
						sel_svcid = svc_val;
					}else
					{
						sel_svcid = sel_svcid+"|"+svc_val;
					}
					
				}
				
			});
			return sel_svcid;
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
		
		getChangeType:function(){
			var changeType = "1";
			
			var bookTag = $("#V_BOOK_TAG").val();          //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
			var isYearProduct = $("#PRODUCT_YEAR").val();  //是否包年套餐，1：是，0：否
			var haveSaleActive = $("#PRODUCT_ACTIVE").val(); //是否有营销活动，1：是,0:否
			var changeUpDownTag = $("#CHANGE_UP_DOWN_TAG").val(); //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
			var endFlag = $("#END_FLAG").val();  //是否最后一个月
			var isYearActive = $("#USER_YEAR_ACTIVE").val(); //是否是包年营销活动，1：是,0:否
			var upgradeSaleActiveProductIds= $("#UPGRADE_SALE_ACTIVE_PRODUCT_IDS").val(); //当前营销活动在产品变更升档时可以选择的营销产品ID
			
			var effectiveDate = $("#PACKAGE_VALID").val();  //用户选择的生效时间
			var agreementEndDate = $("#V_END_DATE").val();  //用户包年套餐或营销活动的结束时间
			
			var oldSalePackageId = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动id
			var newSalePackageId = $("#WIDENET_ACTIVE_PACKAGE").val();//用户要变更的新活动id
			
			var oldProductId = $("#USER_PRODUCT_ID").val();//用户当前的宽带产品id
			var newProductId = $("#NEW_PRODUCT_ID").val();//用户要办理变更的宽带产品id
			
			//新选的营销活动类型
			var newSelectActiveType = "";
			var newSaleProductId = "";
			var salePackageIdFlag = "";
			var tradetypecode = $("#TRADE_TYPE_CODE").val();
			var oldSaleProductId = $("#USER_SALE_PRODUCT_ID").val();//用户当前的活动对应的产品id
			
			var saleActiveList = changeWidenetProduct.SALE_ACTIVE_LIST;
			if(saleActiveList != null && saleActiveList != '')
			{
				for (var i = 0; i < saleActiveList.length; i++)
				{
					if (newSalePackageId == saleActiveList.get(i).get('PARA_CODE5'))
					{
						newSelectActiveType = saleActiveList.get(i).get('PARA_CODE7');
						newSaleProductId = saleActiveList.get(i).get('PARA_CODE4');
						salePackageIdFlag = saleActiveList.get(i).get('PARA_CODE8'); //20170511不受活动结束时间限制，可以变活动。
						salePackageIdFlagSpc = saleActiveList.get(i).get('PARA_CODE9');
					}
				}
			}
			
			if("3"==$("#PACKAGE_VALID")[0].selectedIndex && salePackageIdFlag!= "1"){
				MessageBox.alert("提示","特定的活动才能选这个预约时间");
				$("#WIDENET_ACTIVE_PACKAGE").val('');
				return false ;
			}
			
			
			var changeSubmitData = selectedElements.getSubmitData();
			
			var changeFlag = false ;  //标记产品优惠或服务是否已改变
			
			var changeQZdis = false; //琼中户户通套餐
			
			if (changeSubmitData != null && changeSubmitData.length > 0)
			{
				changeFlag = true ;
				
				for (var i = 0; i < changeSubmitData.length; i++)
				{
					if ("84020042"== changeSubmitData.get(i).get('ELEMENT_ID'))
					{
						changeQZdis = true;
						break;
					}
				}
			}
				
			
			if(changeUpDownTag == '0')
			{
				//产品未变的情况
				//未做任何改变，不能提交
				if(changeFlag == false && (newSalePackageId == null || newSalePackageId == ''))
				{
					//未做任何改变，不允许提交
					$.MessageBox.alert("提示","您没有进行任何操作，不能提交!");
					return false ;
				}
				//原无营销活动
				if((oldSalePackageId == null || oldSalePackageId == '')) 
				{
					//原套餐有包年的情况
					if(isYearProduct == "1")
					{
						if(changeFlag == true && (newSalePackageId == null || newSalePackageId == ''))
						{
							changeType = "1";    //原产品没有营销活动，新营销活动也无，新产品未变，优惠变更了
							return changeType;
						}
						
						//优惠未变，营销活动已变
						
						if(newSalePackageId != '' && endFlag == '1')
						{
							if(salePackageIdFlag == "1" && "601" == tradetypecode){ //办理了特殊的活动可以预约到活动结束
								//如果用户选择的时间小于协议结束时间，则不允许提交						
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
									MessageBox.alert("提示",tipMsg);
									return false ;
								}else{
									changeType = "2";    //原产品没有营销活动，新营销活动，新产品未变，优惠未变，新增营销活动
									return changeType;
								}
							}else{
								MessageBox.alert("提示","您的当前产品有包年套餐，且您未进行产品变更,不能办理营销活动!");
								return false ;
							}
							
						}
						else if(newSalePackageId != '' && endFlag == '0')
						{
							if(changeFlag == true)
							{
								changeType = "3";    
								return changeType;
							}
							else
							{
								changeType = "2";    
								return changeType;
							}
						}
					}
					else 
					{
						//原产品无包年套餐，也无新选营销活动
						if(changeFlag == true && (newSalePackageId == null || newSalePackageId == ''))
						{
							changeType = "1";    //原产品没有营销活动，新营销活动也无，新产品未变，优惠变更了
							return changeType;
						}
						
						if(changeFlag == false && newSalePackageId != '')
						{
							changeType = "2";    //原产品没有营销活动，新营销活动也无，新产品未变，优惠未变，新增营销活动
							return changeType;
						}
						
						if(changeFlag == true && newSalePackageId != '')
						{
							var isSelectYearDiscnt = false ;
							//既有优惠变更，也有营销活动变更，如果有包年优惠，不能同时生效，需提示互斥
							//判断是否有包年优惠，有包年，且活动未到期，不能办理，如果活动3个月内到期，也不能办理
							isSelectYearDiscnt = changeWidenetProduct.checkIsSelectYearDiscnts();
							
							if(isSelectYearDiscnt)
							{
								//包年套餐，不能选择营销活动,产品不变暂不考虑活动到期的情况
								MessageBox.alert("提示","营销活动与包年套餐不能同时生效,请取消营销活动或包年套餐!");
								return false ;
							}
							
							changeType = "3";    //原产品没有营销活动，新营销活动有，新产品未变，优惠已变，新增营销活动，需同时调用产品变更接口及营销活动接口
							return changeType;
						}
					}
				}
				else if(oldSalePackageId != '')
				{
					//原有营销活动的情况，不变产品的情况下，不管原活动有没有结束都不允许再办理营销活动，因为原营销活动可能是2050年结束
                    if(newSalePackageId != '' && oldSalePackageId == newSalePackageId && newSaleProductId !="66002202" && newSaleProductId !="66004809")
					{
						//原营销活动非本月底结束
						MessageBox.alert("提示","您当前已经有营销活动，未变更产品，不能再选择营销活动!");
						return false ;
					}
					else if(newSalePackageId != '' && oldSalePackageId != newSalePackageId)
					{
						//一个产品对于多条营销活动的情况
						//判断原活动是否到期，不到期不能办理
						if(bookTag == '0')
						{
							if(salePackageIdFlag == "1" && "601" == tradetypecode){ //办理了特殊的活动可以预约到活动结束
								//如果用户选择的时间小于协议结束时间，则不允许提交						
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									if("1" != salePackageIdFlagSpc)
									{
										var tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
										$("#PACKAGE_DESC").val('');
										MessageBox.alert("提示",tipMsg);
										return false ;
									}
									
									
									if("1" == salePackageIdFlagSpc && oldSaleProductId != '' && (oldProductId == '84010438' || oldProductId == '84010439' || oldProductId == '84010440' || oldProductId == '84011238'))
									{
										if((oldSaleProductId == '69908001' ||  oldSaleProductId == '67220428') && oldSaleProductId != newSaleProductId)
										{
											var tipMsg = "您当前有营销活动，您本次选择的生效时间为[" + effectiveDate
											+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
										
											$(id).val('');
											$("#PACKAGE_DESC").val('');
											MessageBox.alert("提示",tipMsg);
											return false ;
										}
										
									}	
									
									
									if("1" == salePackageIdFlagSpc)
									{
										var tipMsg = "您之前有营销活动未到期，变更到新产品将会终止您的原营销活动，为您重新添加新的营销活动，是否继续?"
											if(window.confirm(tipMsg))
											{
												$("#SPECIAL_SALE_FLAG").val("1");//特殊标识
												if( newProductId == "" && oldSalePackageId != newSalePackageId){
													changeType = "8";
													return changeType ;
												}
												
												changeType = "5";     //升档，活动转活动的情况，需要同时调用产品变更接口及营销活动终止接口和营销活动受理接口
												return changeType ;
											}
									}
								}
							}
							//REQ202001030018增加宽带1+多人约消档次的开发需求
							else if(changeWidenetProduct.checkIsChangeProduct())
							{
								changeType = "8";     //活动转活动的情况，需要同时调用营销活动终止接口和营销活动受理接口
								return changeType ;
							}
							//REQ202001030018增加宽带1+多人约消档次的开发需求
							else{  ////办理了特殊的活动可以预约到活动结束
								MessageBox.alert("提示","您当前有营销活动未到期，不能再办理营销活动!");
								return false ;
							}
						}
						
						if(bookTag == '1')
						{
							//如果用户选择的时间小于协议结束时间，则不允许提交						
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								var tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
                            //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
                            else if(newSaleProductId == '66002202' || newSaleProductId == '66004809')
                            {
                                changeType = "2";
                                return changeType;
                            }
                            //add by zhangxing3 for 候鸟月、季、半年套餐（海南）

						}
						
						//原营销活动本月底结束
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","营销活动与包年套餐不能同时生效,请取消包年套餐或营销活动!");
							return false ;
						}
						
						if(changeFlag == true)
						{
							changeType = "6";    //终止原活动，新增新活动，产品变更
							return changeType;
						}
						else
						{
							changeType = "8";     //终止原活动，新增新活动
							return changeType;
						}
					}
                    //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
                    else if(newSalePackageId != '' && oldSalePackageId == newSalePackageId)
                    {
                        if(bookTag == '1' && (newSaleProductId == '66002202' || newSaleProductId == '66004809'))
                        {
                            //如果用户选择的时间小于协议结束时间，则不允许提交
                            if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
                            {
                                var tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + effectiveDate
                                    + "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";

                                MessageBox.alert("提示",tipMsg);
                                return false ;
                            }
                            else
                            {
                                changeType = "2";
                                return changeType;
                            }
                        }
                    }
                    //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
					else if(newSalePackageId == '') //未选择营销活动
					{
						//判断是否结束，未结束，不能选包年套餐
						var isSelectYearDiscnt = false ;
						isSelectYearDiscnt = changeWidenetProduct.checkIsSelectYearDiscnts();
						
						if(isSelectYearDiscnt && endFlag == '1')
						{
							if(changeQZdis)
							{
								//需要提示，原活动将终止
								var tipMsg = "您之前有营销活动未到期，办理琼中户户通将会终止您的营销活动，是否继续?";
								MessageBox.confirm("告警提示",tipMsg,function(re){
									if(re=="ok"){
										changeType = "4";     //需要同时调用产品变更接口及营销活动终止接口
										if(!changeType)
										{
											return false ;
										}
										
										if(!changeWidenetProduct.checkFeeBeforeSubmit())
										{
											return false ;
										}
										
										$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());
										
										if(changeWidenetProduct.submit(changeType)){
											$.cssubmit.submitTrade();
										}
									}
								});
								return false;
							}else
							{								
								//原有营销活动,产品不变不能选择包年套餐，暂不考虑活动到期的情况
								MessageBox.alert("提示","营销活动与包年套餐不能同时生效,请取消包年套餐!");
								return false ;
							}
						}
						
						changeType = "1";    //原产品有营销活动，新营销活动无，新产品未变，优惠已变，只调用产品变更接口
						return changeType;
					}
				}
				
			} // 产品不变结束
			else if(changeUpDownTag == '1')	//产品升档开始
			{
				//原包年套餐
				if(isYearProduct == '1')
				{
					//只有包年升级到包年能随意升，其他需要等包年到期
					if ('WIDE_YEAR_ACTIVE' != newSelectActiveType)
					{
						//判断原活动是否到期，不到期不能办理
						//包年未到期不允许变更
						if(bookTag == '0')
						{
							if(salePackageIdFlag == "1" && "601" == tradetypecode){ //办理了特殊的活动可以预约到活动结束
								//如果用户选择的时间小于协议结束时间，则不允许提交						
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									$("#PACKAGE_DESC").val('');
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
							
							}else{  ////办理了特殊的活动可以预约到活动结束
								//包年未到期不允许变更
								MessageBox.alert("提示","您的原产品中有包年优惠未到期，不能进行产品变更!");
								return false ;
							}
						}
						
						if(bookTag == '1')
						{
							//如果用户选择的时间小于协议结束时间，则不允许提交						
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								var tipMsg = "您当前产品有包年套餐，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
						}
					}
					
					if(newSalePackageId == '')
					{
						changeType = "1";     //升档，包年转包年的情况
						return changeType ;
					}
					else if(newSalePackageId != '')
					{
						//包年转营销活动的情况
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","营销活动与包年套餐不能同时生效，请取消包年套餐或营销活动!");
							return false ;
						}
						else
						{
							changeType = "3";     //升档，包年转活动的情况，需要同时调用产品变更接口及营销活动受理接口
							return changeType ;
						}
					}
				}
				else if(haveSaleActive == '1')  //原有营销活动的情况
				{
					//新选没有营销活动
					if(null == newSalePackageId || '' == newSalePackageId )
					{
						//活动转包年的情况，新产品必需有包年套餐
						//add by zhangxing3 for REQ201905060008优化包年宽带客户到期当月可申请办理产品变更
						//if( !changeWidenetProduct.checkIsSelectYearDiscnts())
						if( endFlag == '1' && !changeWidenetProduct.checkIsSelectYearDiscnts())
						//add by zhangxing3 for REQ201905060008优化包年宽带客户到期当月可申请办理产品变更
						{
							MessageBox.alert("提示","您之前有营销活动未到期，要变更产品需要选包年套餐或营销活动!且您的原营销活动将会终止!");
							return false ;
						}
						else
						{
							//需要提示，原活动将终止
							var tipMsg = "您之前有营销活动未到期，变更到新产品将会终止您的营销活动，是否继续?";
							MessageBox.confirm("告警提示",tipMsg,function(re){
								if(re=="ok"){
									changeType = "4";     //升档，活动转包年的情况，需要同时调用产品变更接口及营销活动终止接口
									if(!changeType)
									{
										return false ;
									}
									
									if(!changeWidenetProduct.checkFeeBeforeSubmit())
									{
										return false ;
									}
									
									$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());
									
									if(changeWidenetProduct.submit(changeType)){
										$.cssubmit.submitTrade();
									}
								}
							});
							return false;
						}
					}
					else
					{
						//如果没有则表示协原营销活动协议期内不能升级到新的营销活动
						if (null == upgradeSaleActiveProductIds
								|| '' == upgradeSaleActiveProductIds
								|| upgradeSaleActiveProductIds.indexOf(newSaleProductId) < 0)
						{
							//包年未到期不允许变更
							if(bookTag == '0' && (newSaleProductId != "69908001" || oldSaleProductId != "67220428"))
							{
								if(salePackageIdFlag == "1" && "601" == tradetypecode){ //办理了特殊的活动可以预约到活动结束
									//如果用户选择的时间小于协议结束时间，则不允许提交						
									if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
									{
										var tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
										$("#PACKAGE_DESC").val('');
										MessageBox.alert("提示",tipMsg);
										return false ;
									}
								}else{  ////办理了特殊的活动可以预约到活动结束
								
									MessageBox.alert("提示","您的原营销活动未到期，不能选择新的营销活动!");
									return false ;
								}
							}
							
							if(bookTag == '1' && (newSaleProductId != "69908001" || oldSaleProductId != "67220428"))
							{
								//如果用户选择的时间小于协议结束时间，则不允许提交	
								//alert("-------------zx-------------newSaleProductId:"+newSaleProductId+",oldSaleProductId:"+oldSaleProductId);
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前产品有营销活动套餐，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
										
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
							}
						}
						
						
						//活动转活动的情况，新产品优惠中不能有包年优惠
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","包年优惠和营销活动不能同时生效，请取消包年优惠或营销活动!");
							return false ;
						}
						
						if(bookTag == '1' && changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							changeType = "5";     //升档，活动转活动的情况，需要同时调用产品变更接口及营销活动终止接口和营销活动受理接口
							return changeType ;
						}
						else
						{
							//需要提示原活动终止，新活动受理
							var tipMsg = "您之前有营销活动未到期，变更到新产品将会终止您的原营销活动，为您重新添加新的营销活动，是否继续?"
							MessageBox.confirm("告警提示",tipMsg,function(re){
								if(re=="ok"){
									changeType = "5";     //升档，活动转活动的情况，需要同时调用产品变更接口及营销活动终止接口和营销活动受理接口
									if(!changeType)
									{
										return false ;
									}
									
									if(!changeWidenetProduct.checkFeeBeforeSubmit())
									{
										return false ;
									}
									
									$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());
									
									if(changeWidenetProduct.submit(changeType)){
										$.cssubmit.submitTrade();
									}
								}
							});
							return false;
						}
					}
				}
				else 
				{
					//原产品无包年套餐和营销活动
					if(newSalePackageId == '')
					{
						changeType = "1";  
						return changeType ;
					}
					else if(newSalePackageId != '')
					{
						//营销活动和包年套餐不能同时生效
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","包年优惠和营销活动不能同时生效，请取消包年优惠或营销活动!");
							return false ;
						}
						else 
						{
							changeType = "3";      //调用调用产品变更接口和营销活动受理接口
							return changeType ;
						}
					}
				}
			}//产品升档结束
			else if(changeUpDownTag == '2')	//产品降档开始
			{
				if(isYearProduct == '1')
				{
					//原产品有包年套餐
					if(bookTag == '0')
					{
						MessageBox.alert("提示","您当前产品有包年套餐未到期，不能办理产品降档变更!");
						return false ;
					}
					
					
					//包年套餐 也不允许进行营销活动受理
					if(newSalePackageId != '' && endFlag == '1')
					{
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前产品有包年套餐，产品降档变更不允许受理营销活动，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请取消营销活动或进行产品升档变更或重新选择生效时间!";
								
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
						else
						{
							if(changeWidenetProduct.checkIsSelectYearDiscnts())
							{
								MessageBox.alert("提示","营销活动与包年套餐不能同时生效，请取消包年套餐或营销活动!");
								return false ;
							}
							
							changeType = "3";  
							return changeType ;
						}
					}
					else if(newSalePackageId != '' && endFlag == '0')
					{
						//包年套餐的最后一个月，也是允许营销活动受理的
						//包年变包年，降档不允许
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","营销活动与包年套餐不能同时生效，请取消包年套餐或营销活动!");
							return false ;
						}
						
						changeType = "3";  
						return changeType ;
					}
					else if(newSalePackageId == '' && endFlag == '0')
					{
						changeType = "1";  
						return changeType ;
					}
					else if(newSalePackageId == '' && endFlag == '1')
					{
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前产品有包年套餐未到期，不能办理产品降档变更，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
								
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
						else
						{
							changeType = "1";  
							return changeType ;
						}
					}
				}
				else if(haveSaleActive == '1')
				{
					//原产品有营销活动
					if(bookTag == '0')
					{
						MessageBox.alert("提示","您当前有营销活动未到期，不能办理产品降档变更!");
						return false ;
					}
					
					if(newSalePackageId != '' && endFlag == '1')
					{
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前有营销活动未到期，不能办理产品降档变更，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
								
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
						else
						{
							//之前有营销活动且即将到期，活动变活动，不能选包年
							if(changeWidenetProduct.checkIsSelectYearDiscnts())
							{
								MessageBox.alert("提示","包年套餐与营销活动互斥，请取消包年套餐或营销活动!");
								return false ;
							}
							
							changeType = "6";   //降档，原有营销活动，又新选了营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
							return changeType ;
						}
					}
					else if(newSalePackageId != '' && endFlag == '0')
					{
						//之前有营销活动且月底到期，活动变活动，不能选包年
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","包年套餐与营销活动互斥，请取消包年套餐或营销活动!");
							return false ;
						}
						
						changeType = "6";      //降档，原有营销活动，又新选了营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
						return changeType ;
					}
					else if(newSalePackageId == '' && endFlag == '0')
					{
						changeType = "7";     //降档，原有营销活动，无新选营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
						return changeType ;
					}
					else if(newSalePackageId == '' && endFlag == '1')
					{
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前有营销活动未到期，不能办理产品降档变更，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
								
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
						else
						{
							changeType = "7";  //降档，原有营销活动，无新选营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
							return changeType ;
						}
					}
				}
				else
				{
					//无包年和营销活动，允许自由降档
					if(newSalePackageId != '')
					{
						//有营销活动，不能有包年套餐
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","包年套餐与营销活动不能同时生效，请取消包年套餐或营销活动!");
							return false ;
						}
						
						changeType = "3";  
						return changeType ;
					}
					else
					{
						changeType = "1";  
						return changeType ;
					}
				}
			}//产品降档结束
			else if(changeUpDownTag == '3')	//产品同档变更开始（产品变，速率不变）
			{
				if(isYearProduct == '1')
				{
					if(bookTag == '0')
					{
						MessageBox.alert("您当前产品有包年套餐未到期，不能办理产品变更!");
						return false ;
					}
					else if(bookTag == '1')
					{
						if(newSalePackageId != '')
						{
							if(endFlag == "0")
							{
								//有营销活动，不能有包年套餐
								if(changeWidenetProduct.checkIsSelectYearDiscnts())
								{
									MessageBox.alert("提示","包年套餐与营销活动不能同时生效，请取消包年套餐或营销活动!");
									return false ;
								}
								else
								{
									changeType = "3";   //同时调用产品变更接口和营销活动受理接口
									return changeType ;
								}
							}
							else if(endFlag == "1")
							{
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前包年套餐未到期，不能办理产品变更，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
										
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
								
								changeType = "3";   //同时调用产品变更接口和营销活动受理接口
								return changeType ;
							}
						}
						else
						{
							if(endFlag == '1')
							{
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前包年套餐未到期，不能办理产品变更，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
										
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
							}
							
							changeType = "1"; 
							return changeType ;
						}
					}
				}
				else if(haveSaleActive == '1')
				{
					if(bookTag == '0')
					{
						MessageBox.alert("提示","您当前有营销活动未到期，不能办理产品变更!");
						return false ;
					}
					else if(bookTag == '1')
					{
						if(endFlag == "1")
						{
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								var tipMsg = "您当前有营销活动未到期，不能办理产品变更，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
									
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
						}
							
						if(newSalePackageId != '')
						{
							//有营销活动，不能有包年套餐
							if(changeWidenetProduct.checkIsSelectYearDiscnts())
							{
								MessageBox.alert("提示","包年套餐与营销活动不能同时失效，请取消包年套餐或营销活动!");
								return false ;
							}
							else
							{
								changeType = "6";   //同时调用产品变更接口和营销活动受理接口,生效日期必须为协议到期的次月
								return changeType ;
							}
						}
						else
						{
							changeType = "7";   //调用产品变更接口生效日期必须为协议到期的次月
							return changeType ;
						}
					}
				}
				else 
				{
					//无包年和营销活动，允许同档变更
					if(newSalePackageId != '')
					{
						//有营销活动，不能有包年套餐
						if(changeWidenetProduct.checkIsSelectYearDiscnts())
						{
							MessageBox.alert("提示","包年套餐与营销活动不能同时失效，请取消包年套餐或营销活动!");
							return false ;
						}
						changeType = "3";  
						return changeType ;
					}
					else
					{
						changeType = "1";  
						return changeType ;
					}
				}
			} //产品同档变更结束
			else
			{
				MessageBox.alert("警告","未知操作类型，不能提交!");
				MessageBox.alert("提示","UP_DOWN_TAG=" + changeUpDownTag + ";BOOK_TAG=" + bookTag + ";IS_YEAR_PRODUCT=" + isYearProduct + ";END_FALG=" + endFlag);
				return false ;
			}
		},
		//校验用户选择的优惠中是否有包年优惠
		checkIsSelectYearDiscnts:function(){
			//判断优惠是否有包年，包年不能提交
			var isSelectYearDiscnt = false ;
			var changeSubmitData = selectedElements.getSubmitData();
			if(changeSubmitData != null && changeSubmitData.length > 0)
			{
				var param = "&SELECTED_ELEMENTS=" + changeSubmitData.toString();
				param += "&ROUTE_EPARCHY_CODE=" + $("#USER_EPARCHY_CODE").val();
								
				$.beginPageLoading("校验是否包年套餐。。。。。");
				$.ajax.submit(null,"checkIsYearDiscnts", param ,'',
					function(data)
					{
						$.endPageLoading();
						var isYearDiscnt = data.get('IS_YEAR_DISCNT');
						if(isYearDiscnt == true || isYearDiscnt == "true" || isYearDiscnt == "TRUE")
							isSelectYearDiscnt = true;
					},
					function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					},
					{async:false}
				);
			}
			return isSelectYearDiscnt ;
		},
		checkIsChangeProduct:function(){
			//判断宽带1+活动是否可平移
			var isChangeProduct = false ;
			var oldSalePackageId = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动id
			var newSalePackageId = $("#WIDENET_ACTIVE_PACKAGE").val();//用户要变更的新活动id

			var param = "&OLD_SALE_PACKAGE_ID=" + oldSalePackageId;
			param += "&NEW_SALE_PACKAGE_ID=" + newSalePackageId;

			$.beginPageLoading("校验是否可以平移活动。。。。。");
			$.ajax.submit(null,"checkIsChangeProduct", param ,'',
				function(data)
				{
					$.endPageLoading();
					var isChangeProductResult = data.get('IS_CHANGE_PRODUCT');
					if(isChangeProductResult == true || isChangeProductResult == "true" || isChangeProductResult == "TRUE")
						isChangeProduct = true;
				},
				function(error_code, error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				},
				{async:false}
			);
			return isChangeProduct ;
		},
		//比较日期大小,date1 > date2 返回true ,否则返回 false
		dateCompare:function(date1,date2){
			var strDate1 = date1;
			var strDate2 = date2;
			if(strDate1.length > 10)
			{
				strDate1 = strDate1.substring(0,10);
			}
			if(strDate2.length > 10)
			{
				strDate2 = strDate2.substring(0,10);
			}
			
			var arrayDate1 = strDate1.split("-");
			var arrayDate2 = strDate2.split("-");
			
			var newDate1 = new Date(arrayDate1[0],arrayDate1[1] - 1,arrayDate1[2]);
			var times1 = newDate1.getTime();
			var newDate2 = new Date(arrayDate2[0],arrayDate2[1] - 1,arrayDate2[2]);
			var times2 = newDate2.getTime();
			
			if(times1 > times2)
			{
				return true 
			}
			else 
			{
				return false;
			}
		},
		
		//提交前判断用户办理的业务有几种，产品变更，营销活动是否都有办理
		checkbeforsubmit:function(){
			var old_pack = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动id
			var new_pack = $("#WIDENET_ACTIVE_PACKAGE").val();//用户要变更的活动id
			//
			var old_prod_year = $("#PRODUCT_YEAR").val();//用户当前的产品是否是包年产品套餐，=1：是
			var old_pack_year = $("#PRODUCT_ACTIVE").val();//用户当前是否有营销活动，=1：有
			//
			var change_old_prod = $("#USER_PRODUCT_ID").val();//用户当前的宽带产品id
			var change_new_prod = $("#NEW_PRODUCT_ID").val();//用户要办理变更的宽带产品id
			/**
	    	 * 1，活动--包年：变更前有营销活动，变更后没有的，办理产品变更要终止该活动
	    	 * 2，活动--活动：变更前有营销活动，要先终止，同时又有新办理营销活动
	    	 * 3，无--活动，包年--活动：以前没有营销活动，现在要新办理营销活动
	    	 * */
			if(old_pack=="" && new_pack=="" )
			{
				//之前没有办理过活动，现在也不办理，只是做单纯的产品变更
				return changeWidenetProduct.submit("0");
			}
			if(old_pack!="" && new_pack=="" )
			{
				return changeWidenetProduct.submit("1");
			}
			if(old_pack!="" && new_pack!="" )
			{
				if (change_new_prod==change_old_prod || change_new_prod=="" || change_new_prod ==null)
				{
					//说明产品没有变，直接走营销活动终止流程和营销活动受理流程
					return changeWidenetProduct.submit("4");
				}
				return changeWidenetProduct.submit("2");
			}
			if(old_pack=="" && new_pack!="" )
			{
				//
				if (change_new_prod==change_old_prod || change_new_prod=="" || change_new_prod ==null)
				{
					//说明产品没有变，直接走营销活动受理流程
					return changeWidenetProduct.submit("5");
				}
				return changeWidenetProduct.submit("3");
			}
			
			
			return true;
		},
		//提交前费用校验
		checkFeeBeforeSubmit:function()
		{
			var flag = true;
			
			var newSalePackageId = $("#WIDENET_ACTIVE_PACKAGE").val();//用户要变更的活动id
			
			//没选营销活动则不作此校验
			if (null == newSalePackageId || '' == newSalePackageId)
			{
				return flag;
			}
			
			var newSaleProductId = changeWidenetProduct.getSaleProductIdByPackageId(newSalePackageId);
			var oldSalePackageId = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动id
			var oldSaleProductId = $("#USER_SALE_PRODUCT_ID").val();//用户当前的活动对应的产品id
			var isYearProduct = $("#PRODUCT_YEAR").val();
			
			var changeSubmitData = selectedElements.getSubmitData();
			
			var param = "&NEW_SALE_ACTIVE_PACKAGE_ID=" + newSalePackageId
			+ "&NEW_SALE_ACTIVE_PRODUCT_ID=" + newSaleProductId
			+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()
			+"&BOOKING_DATE=" + $("#BOOKING_DATE").val()
			+"&OLD_SALE_ACTIVE_PACKAGE_ID=" + oldSalePackageId
			+"&OLD_SALE_ACTIVE_PRODUCT_ID=" + oldSaleProductId
			+"&IS_YEAR_PRODUCT="+isYearProduct
			+"&SELECTED_ELEMENTS=" + changeSubmitData.toString();
			
			$.beginPageLoading("提交校验中。。。");	
			
			$.ajax.submit(null, 'checkFeeBeforeSubmit', param, null,
				function(data) 
				{
					$.endPageLoading();
				
					$("#WIDE_ACTIVE_PAY_FEE").val(data.get("WIDE_ACTIVE_PAY_FEE"));
					$("#YEAR_DISCNT_REMAIN_FEE").val(data.get("YEAR_DISCNT_REMAIN_FEE"));
					$("#RETURN_YEAR_DISCNT_REMAIN_FEE").val(data.get("RETURN_YEAR_DISCNT_REMAIN_FEE"));
					$("#REMAIN_FEE").val(data.get("REMAIN_FEE"));
					$("#ACCT_REMAIN_FEE").val(data.get("ACCT_REMAIN_FEE"));
					
					if ('0' != data.get("WIDE_ACTIVE_PAY_FEE"))
					{
						var tips = "您本次业务需要从现金类存折中转出："+parseFloat(data.get("WIDE_ACTIVE_PAY_FEE"))/100+"元!";
						
						alert(tips);
					}
					
					flag = true;
					
				}, function(error_code, error_info) 
				{
					flag = false;
					
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				},
				{async:false}
			);
			
			return flag;
		},
		//提交前优惠校验
		checkDisnctBeforeSubmit:function()
		{
			var flag = true;
			var changeSubmitData = selectedElements.getSubmitData();
			if(changeSubmitData != null && changeSubmitData.length > 0)
			{
				var param = "&SELECTED_ELEMENTS=" + changeSubmitData.toString()+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				$.beginPageLoading("提交校验中。。。");

				$.ajax.submit(null, 'checkDisnctBeforeSubmit', param, null,
					function(data) 
					{
						$.endPageLoading();
					
						if ('true' != data.get("RESULT"))
						{
							var tips = "暂时无法办理提速礼包["+data.get("ELEMENT_ID")+"]，当前提速包剩余数为0！";
							
							alert(tips);
							flag = false;
						}
						
						
						
					}, function(error_code, error_info) 
					{
						flag = false;
						
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					},
					{async:false}
				);
				
			}
			
			
			
			return flag;
		},
		beforeSubmit:function()
		{
			var changeType = changeWidenetProduct.getChangeType();
			//alert("测试用，正式上线会去掉,变更类型:[" + changeType + "]");
			if(!changeType)
			{
				return false ;
			}
			
			if(!changeWidenetProduct.checkFeeBeforeSubmit())
			{
				return false ;
			}
			if(!changeWidenetProduct.checkDisnctBeforeSubmit())
			{
				return false ;
			}
			$("#BOOKING_DATE").val($("#PACKAGE_VALID").val());
			
			return changeWidenetProduct.submit(changeType);
		},
		
		submit: function(num)
		{
			var canSubmit = selectedElements.checkForcePackage();
			
			if(!canSubmit){
				return false;
			}
			
			var old_pack = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动id
			var new_pack = $("#WIDENET_ACTIVE_PACKAGE").val();//用户要变更的活动id
			var old_prod = $("#USER_SALE_PRODUCT_ID").val();//用户当前的活动对应的产品id

			var newSalePackageId = new_pack;
			var newSaleProductId = changeWidenetProduct.getSaleProductIdByPackageId(new_pack);

			var change_old_prod = $("#USER_PRODUCT_ID").val();//用户当前的宽带产品id
			var change_new_prod = $("#NEW_PRODUCT_ID").val();//用户要办理变更的宽带产品id
			var change_checkactive="0";//是否要变宽带产品
			var change_checksvc_str24="";//保存选择产品后的服务id，用于后面的活动规则校验
			if (change_old_prod!=change_new_prod)
			{
				change_checkactive="1";
				change_checksvc_str24 = changeWidenetProduct.getActiveRuleSvcCheck();
			}
			
			var data = selectedElements.getSubmitData();
			if(data&&data.length>0){
				var param = "&SELECTED_ELEMENTS="+data.toString()+"&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				if($("#EFFECT_NOW").attr("checked")){
					param+="&EFFECT_NOW=1";
				}
				param+="&BOOKDATE="+$("#PACKAGE_VALID").val();
				param+="&END_BOOKDATE="+$("#PACKAGE_VALID1").find("option:selected").text();
				param+="&CHANGE_TYPE="+num;
				param+="&WIDE_TYPE="+$("#WIDE_TYPE").val();
				param+="&CHANGE_UP_DOWN_TAG="+$("#CHANGE_UP_DOWN_TAG").val();
				param+="&SPECIAL_SALE_FLAG="+$("#SPECIAL_SALE_FLAG").val();
				param+="&V_USER_PACKAGE_ID="+old_pack;
				param+="&V_NEW_PACKAGE_ID="+new_pack;
				param+="&V_USER_PRODUCT_ID="+old_prod;
				param+="&WIDE_USER_CREATE_SALE_ACTIVE="+change_checkactive;
				param+="&WIDE_USER_SELECTED_SERVICEIDS="+change_checksvc_str24;
				param+="&NEW_SALE_PRODUCT_ID=" + newSaleProductId + "&NEW_SALE_PACKAGE_ID=" + newSalePackageId;
				param+="&BOOKING_DATE=" + $("#BOOKING_DATE").val();
				param+="&V_BOOK_TAG=" + $("#V_BOOK_TAG").val();
				param+="&WIDE_ACTIVE_PAY_FEE=" + $("#WIDE_ACTIVE_PAY_FEE").val();
				param+="&YEAR_DISCNT_REMAIN_FEE=" + $("#YEAR_DISCNT_REMAIN_FEE").val();
				param+="&RETURN_YEAR_DISCNT_REMAIN_FEE=" + $("#RETURN_YEAR_DISCNT_REMAIN_FEE").val();
				param+="&REMAIN_FEE=" + $("#REMAIN_FEE").val();
				param+="&ACCT_REMAIN_FEE=" + $("#ACCT_REMAIN_FEE").val();
				param+="&KDTS_PRODUCT=" + $("#KDTS_PRODUCT").val();
				//alert("param==="+param);
				$.cssubmit.addParam(param);
			}
			else{
				
				if (new_pack=="" || new_pack==null)
				{
					//新的活动为空，说明没有任何操作
					MessageBox.alert("提示","您没有进行任何操作，不能提交");
					return false;
				}
				//走营销活动受理流程
				var param = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				param+="&BOOKDATE="+$("#PACKAGE_VALID").val();
				param+="&END_BOOKDATE="+$("#PACKAGE_VALID1").find("option:selected").text();
				param+="&CHANGE_TYPE="+num;
				param+="&WIDE_TYPE="+$("#WIDE_TYPE").val();
				param+="&CHANGE_UP_DOWN_TAG="+$("#CHANGE_UP_DOWN_TAG").val();
				param+="&V_USER_PACKAGE_ID="+old_pack;
				param+="&V_NEW_PACKAGE_ID="+new_pack;
				param+="&V_USER_PRODUCT_ID="+old_prod;
				param+="&NEW_SALE_PRODUCT_ID=" + newSaleProductId + "&NEW_SALE_PACKAGE_ID=" + newSalePackageId;
				param+="&BOOKING_DATE=" + $("#BOOKING_DATE").val();
				param+="&V_BOOK_TAG=" + $("#V_BOOK_TAG").val();
				param+="&KDTS_PRODUCT=" + $("#KDTS_PRODUCT").val();
				//alert("param==="+param);
				$.cssubmit.addParam(param);
			}
			//吴甘丽要求增加变更提示，新产品生效前，不允许移机
			var changeUpDownTag = $("#CHANGE_UP_DOWN_TAG").val(); //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
			var KDTS_PRODUCT = $("#KDTS_PRODUCT").val();
			if("0" != changeUpDownTag) 
			{
				var tipMsgForMove = "新产品生效前，将不允许做产品变更和跨产品的移机业务。请确认是否继续?";
				
				//BUS201907300031新增度假宽带季度半年套餐开发需求
				if ("66004809" == old_prod && (newSaleProductId != null && newSaleProductId != ""))
				{
					tipMsgForMove = "您之前有度假宽带2019未到期，变更到新产品将会终止您的原营销活动。新产品生效前，将不允许做产品变更和跨产品的移机业务。请确认是否继续?";
				}
				
				//
				if (KDTS_PRODUCT != null && KDTS_PRODUCT != "")
				{
					tipMsgForMove = "您已经参加“移动20周年--家庭宽带畅享提速活动”，如办理宽带产品变更，提速活动将取消，是否要办理产品变更？"+tipMsgForMove;
				}
				
				//BUS201907300031新增度假宽带季度半年套餐开发需求
				MessageBox.confirm("告警提示",tipMsgForMove,function(re){
					if(re=="ok"){
						$.cssubmit.submitTrade();
					}
				});
			}
			else{
				if(KDTS_PRODUCT != null && KDTS_PRODUCT != ""){
					tipMsgForMove = "您已经参加“移动20周年--家庭宽带畅享提速活动”，如办理宽带产品变更，提速活动将取消，是否要办理产品变更？新产品生效前，将不允许做产品变更和跨产品的移机业务。请确认是否继续?";
					MessageBox.confirm("告警提示",tipMsgForMove,function(re){
						if(re=="ok"){
							$.cssubmit.submitTrade();
						}
					});
				}else{
					return true;
				}
			}
		},
		
		getSaleProductIdByPackageId: function(packageId){
			var saleProductId = "";
			var saleActiveList = changeWidenetProduct.SALE_ACTIVE_LIST;
			if(saleActiveList != null && saleActiveList != '')
			{
				for (var i = 0; i < saleActiveList.length; i++)
				{
					if (packageId == saleActiveList.get(i).get('PARA_CODE5'))
					{
						saleProductId = saleActiveList.get(i).get('PARA_CODE4');
					}
				}
			}
			
			return saleProductId;
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
		},
		initActivePackage: function(productId){
			//初始化界面上的营销活动包列表
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var para_str = "&USER_ID="+$("#USER_ID").val()+"&NEW_PRODUCT_ID="+productId+"&ROUTE_EPARCHY_CODE="+eparchyCode;
			$.beginPageLoading("初始化活动。。。。。");
			$.ajax.submit(null,"initActivePackage",para_str,'activepackagePart',
				function(data){
					//预约时间处理
					$.endPageLoading();
					/*
					if(flag)
					{
						//重新计算元素开始结束时间
						changeWidenetProduct.selectFirstDate($("#PACKAGE_VALID"));
					}*/
					
					//清空营销活动描述
					$("#PACKAGE_DESC").val('');
					
					changeWidenetProduct.SALE_ACTIVE_LIST.clear();
					changeWidenetProduct.SALE_ACTIVE_LIST = data;
				},
				function(error_code, error_info) 
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				}
			);
			
		},
		
		onChangeSelectSaleActive:function(id)
		{
			var newSalePackageId = $(id).val();
			var newSelectSalePackageId = $("#NEW_PACKAGE_ID").val();
			var saleActiveList = changeWidenetProduct.SALE_ACTIVE_LIST;
			
			if(newSalePackageId == null || newSalePackageId == '')
			{
				//不选择营销包
				if(newSelectSalePackageId == null || newSelectSalePackageId == '')
				{
					return ;
				}
				//取消之前选择的操作
				$("#NEW_PACKAGE_ID").val("");
				$("#PACKAGE_DESC").val("");
				
				//是否处理恢复产品选择?
				
				return ;
			}
			var changeUpDownTag = $("#CHANGE_UP_DOWN_TAG").val(); //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
			
			var bookTag = $("#V_BOOK_TAG").val();          //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
			var isYearProduct = $("#PRODUCT_YEAR").val();  //是否包年套餐，1：是，0：否
			var haveSaleActive = $("#PRODUCT_ACTIVE").val(); //是否有营销活动，1：是,0:否
			var isYearActive = $("#USER_YEAR_ACTIVE").val(); //是否是包年营销活动，1：是,0:否
			var upgradeSaleActiveProductIds= $("#UPGRADE_SALE_ACTIVE_PRODUCT_IDS").val(); //当前营销活动在产品变更升档时可以选择的营销产品ID
			var endFlag = $("#END_FLAG").val();             //本月已到期，本月未到期
			var effectiveDate = $("#PACKAGE_VALID").val();  //用户选择的生效时间
			var agreementEndDate = $("#V_END_DATE").val();  //用户包年套餐或营A销活动的结束时间
			
			//新选的营销活动类型
			var newSelectActiveType = "";
			var newSaleProductId = "";
			var salePackageIdFlag = "";
			var tradetypecode = $("#TRADE_TYPE_CODE").val();
			var userSaleProductId = $("#USER_SALE_PRODUCT_ID").val();//用户当前的活动对应的产品id
			
			var saleActiveList = changeWidenetProduct.SALE_ACTIVE_LIST;
			if(saleActiveList != null && saleActiveList != '')
			{
				for (var i = 0; i < saleActiveList.length; i++)
				{
					if (newSalePackageId == saleActiveList.get(i).get('PARA_CODE5'))
					{
						newSelectActiveType = saleActiveList.get(i).get('PARA_CODE7');
						newSaleProductId = saleActiveList.get(i).get('PARA_CODE4');
						salePackageIdFlag = saleActiveList.get(i).get('PARA_CODE8'); //20170511不受活动结束时间限制，可以变活动。
						salePackageIdFlagSpc = saleActiveList.get(i).get('PARA_CODE9');
					}
				}
			}
			
			if("3"==$("#PACKAGE_VALID")[0].selectedIndex && salePackageIdFlag!= "1"){
				MessageBox.alert("提示","特定的活动才能选这个预约时间");
				$(id).val('');
				return false ;
			}
			
			if(changeUpDownTag == "0")
			{
				//产品不变，如之前有包年活动营销活动不允许变更营销活动
				if(isYearProduct == "1" && bookTag == "0")
				{
					if(salePackageIdFlag == "1" && "601" == tradetypecode && userSaleProductId != "67220429" && userSaleProductId != "69908016"){ //办理了特殊的活动可以预约到活动结束
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							var tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
							$(id).val('');
							$("#PACKAGE_DESC").val('');
							MessageBox.alert("提示",tipMsg);
							return false ;
						}
					}else{  ////办理了特殊的活动可以预约到活动结束
						
						$(id).val('');
						$("#PACKAGE_DESC").val('');
						MessageBox.alert("提示","您当前的产品有包年套餐,未变更产品不能再选择营销活动!");
						return ;
					}
				}
				
				if(isYearProduct == "1" && bookTag == "1" ) //包年套餐即将到期
				{
					//判断选择的时间是否大于当前结束时间
					//如果用户选择的时间小于协议结束时间，则不允许提交						
					if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
					{
						var tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + effectiveDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
						
						$(id).val('');
						$("#PACKAGE_DESC").val('');
						MessageBox.alert("提示",tipMsg);
						return false ;
					}
				}
                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）活动 start
                if(newSaleProductId == "66002202" && bookTag == "1" )
                {
                    //判断选择的时间是否大于当前结束时间
                    //如果用户选择的时间小于协议结束时间，则不允许提交
                    if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
                    {
                        var tipMsg = "您当前有候鸟月、季、半年套餐（海南）活动未到期，您本次选择的生效时间为[" + effectiveDate
                            + "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";

                        $(id).val('');
                        $("#PACKAGE_DESC").val('');
                        MessageBox.alert("提示",tipMsg);
                        return false ;
                    }
                }
                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）活动 end
                //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求 start
                if(newSaleProductId == "66004809" && bookTag == "1" )
                {
                    //判断选择的时间是否大于当前结束时间
                    //如果用户选择的时间小于协议结束时间，则不允许提交
                    if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
                    {
                        var tipMsg = "您当前有度假宽带2019活动未到期，您本次选择的生效时间为[" + effectiveDate
                            + "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";

                        $(id).val('');
                        $("#PACKAGE_DESC").val('');
                        MessageBox.alert("提示",tipMsg);
                        return false ;
                    }
                }
                //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求 end
				if(haveSaleActive == "1" && bookTag == "0")
				{
					//REQ202001030018增加宽带1+多人约消档次的开发需求
					var userSalePackageId = $("#USER_SALE_PACKAGE_ID").val();//用户当前的活动对应的包id
					//REQ202001030018增加宽带1+多人约消档次的开发需求
					if(salePackageIdFlag == "1" && "601" == tradetypecode && userSaleProductId != "67220429" && userSaleProductId != "69908016")
					{ //办理了特殊的活动可以预约到活动结束
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
						{
							if("1" != salePackageIdFlagSpc)
							{
								var tipMsg = "您当前有营销活动，您本次选择的生效时间为[" + effectiveDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
								$(id).val('');
								$("#PACKAGE_DESC").val('');
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
							
							var oldProductId = $("#USER_PRODUCT_ID").val();//用户当前的宽带产品id
							if("1" == salePackageIdFlagSpc && userSaleProductId != '' && (oldProductId == '84010438' || oldProductId == '84010439' || oldProductId == '84010440' || oldProductId == '84011238'))
							{
								if((userSaleProductId == '69908001' ||  userSaleProductId == '67220428') && userSaleProductId != newSaleProductId)
								{
									var tipMsg = "您当前有营销活动，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
									$(id).val('');
									$("#PACKAGE_DESC").val('');
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
								
							}			
						}
					}
					//REQ202001030018增加宽带1+多人约消档次的开发需求										
					else if( changeWidenetProduct.checkIsChangeProduct())
					{
						
					}
					//REQ202001030018增加宽带1+多人约消档次的开发需求
					else{  ////办理了特殊的活动可以预约到活动结束
						$(id).val('');
						$("#PACKAGE_DESC").val('');
						MessageBox.alert("提示","您当前有营销活动,未变更产品不能再选择营销活动!");
						return ;
					}
				}
				
				if(haveSaleActive == "1" && bookTag == "1" ) //营销活动即将到期
				{
					//判断选择的时间是否大于当前结束时间
					if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
					{
						var tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + effectiveDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
						
						$(id).val('');
						$("#PACKAGE_DESC").val('');	
						MessageBox.alert("提示",tipMsg);
						return false ;
					}
				}
				
				//当前没有包年套餐或营销活动，或者有但即将到期
				
				//判断当前用户选择的优惠，不能有包年套餐
				var isSelectYearDiscnt = false ;
				isSelectYearDiscnt = changeWidenetProduct.checkIsSelectYearDiscnts();
				
				if(isSelectYearDiscnt && ("1" != salePackageIdFlagSpc))
				{
					$(id).val('');
					$("#PACKAGE_DESC").val('');
					MessageBox.alert("提示","营销活动与包年套餐互斥，您当前已经选择了包年套餐，不能办理此业务!");
					return ;
				}
			}
			else if(changeUpDownTag == "3" || changeUpDownTag == "2")
			{
				//有包年或营销活动的情况，如果未到期不能选择营销活动
				if(bookTag == "0")
				{
					if(isYearProduct == "1")
					{
						$(id).val('');
						$("#PACKAGE_DESC").val('');
						MessageBox.alert("提示","您当前的产品有包年套餐未到期不能办理此业务!");
						return ;
					}
					
					if(haveSaleActive == "1")
					{
						$(id).val('');
						$("#PACKAGE_DESC").val('');
						MessageBox.alert("提示","您当前的营销活动未到期不能办理此业务!");
						return ;
					}
				}
			}
			else if(changeUpDownTag == "1")
			{
				//升档
				//判断当前是否有包年套餐或者包年营销活动
				if(isYearProduct == "1")
				{
					//只有包年升级到包年能随意升，其他需要等包年到期
					if ('WIDE_YEAR_ACTIVE' != newSelectActiveType)
					{
						//有包年或营销活动的情况，如果未到期不能选择营销活动
						if ('0' == bookTag )
						{
							if(salePackageIdFlag == "1" && "601" == tradetypecode && userSaleProductId != "67220429" && userSaleProductId != "69908016")
							{
								//判断选择的时间是否大于当前结束时间
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
									$(id).val('');
									$("#PACKAGE_DESC").val('');	
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
							}
							else{
								$(id).val('');
								$("#PACKAGE_DESC").val('');
								MessageBox.alert("提示","您当前的产品有包年套餐未到期不能办理此业务!");
								return false ;
							}
						}
						else if ('1' == bookTag )
						{
							//判断选择的时间是否大于当前结束时间
							//如果用户选择的时间小于协议结束时间，则不允许提交						
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								var tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
								$(id).val('');
								$("#PACKAGE_DESC").val('');
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
						}
					}
				}
				
				//如果原来有营销活动
				if ('1' == haveSaleActive)
				{
					//如果没有则表示协原营销活动协议期内不能升级到新的营销活动
					if (null == upgradeSaleActiveProductIds
							|| '' == upgradeSaleActiveProductIds
							||upgradeSaleActiveProductIds.indexOf(newSaleProductId) < 0)
					{
						//包年未到期不允许变更
						if(bookTag == '0' && ( newSaleProductId != "69908001" || userSaleProductId != "67220428"))
						{
							if(salePackageIdFlag == "1" && "601" == tradetypecode && userSaleProductId != "67220429" && userSaleProductId != "69908016")
							{
								//判断选择的时间是否大于当前结束时间
								if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
								{
									var tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + effectiveDate
										+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
									$(id).val('');
									$("#PACKAGE_DESC").val('');	
									MessageBox.alert("提示",tipMsg);
									return false ;
								}
							}
							else{
								$(id).val('');
								$("#PACKAGE_DESC").val('');
								
								MessageBox.alert("提示","您的原营销活动未到期，不能选择新的营销活动!");
								return false ;
							}
						}
						
						if(bookTag == '1' && ( newSaleProductId != "69908001" || userSaleProductId != "67220428"))
						{
							//如果用户选择的时间小于协议结束时间，则不允许提交	
							if(!changeWidenetProduct.dateCompare(effectiveDate,agreementEndDate))
							{
								$(id).val('');
								$("#PACKAGE_DESC").val('');
								
								var tipMsg = "您当前产品有营销活动套餐，您本次选择的生效时间为[" + effectiveDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
								MessageBox.alert("提示",tipMsg);
								return false ;
							}
						}
					}
				}
				
				
				//判断新产品是否选择了包年套餐
				var isSelectYearDiscnt = false ;
				
				isSelectYearDiscnt = changeWidenetProduct.checkIsSelectYearDiscnts();
				
				if(isSelectYearDiscnt)
				{
					$(id).val('');
					$("#PACKAGE_DESC").val('');
					MessageBox.alert("提示","营销活动与包年套餐互斥，您当前新产品已经选择了包年套餐，不能办理此业务!");
					return ;
				}
			}
			$("#NEW_PACKAGE_ID").val(newSalePackageId);
			changeWidenetProduct.queryActiveInfo();
		},
		
		queryActivePackageInfo: function(id){
			//初始化界面上的营销活动描述信息，同时
			var PRODUCT_YEAR = $("#PRODUCT_YEAR").val();
			var pack_id = $(id).val();
			if (pack_id=="")
			{
				$("#PACKAGE_DESC").text("");
				if (PRODUCT_YEAR=="1")
				{
					//是包年套餐，当不选择营销活动时，要把之前选择营销活动时删除的包年套餐优惠恢复回来
					var trs = $("#SelectDiscntTable").children("tr");
					trs.each(function(i,tr){
						var inp = $(tr).children("td").eq(0).children("input");
						var yearflag=$(inp).attr("yearflag");
						if (yearflag=="1")
						{
							changeWidenetProduct.selectActiveDelProduct(inp);
						}
					});
				}
				return ;
			}
			$("#NEW_PACKAGE_ID").val(pack_id);
			//判断是否含有包年套餐
			if (PRODUCT_YEAR=="1")
			{
				//提示，要办理营销活动，就要删除包年套餐
				MessageBox.confirm("确认提示", "您已有包年的宽带产品，如要继续选择营销活动，则会自动取消现有的包年宽带产品！是否继续？", function(btn){
					if(btn == "ok"){
						//继续，则在产品的优惠已选栏总找到包年的套餐，设置成删除状态
						var trs = $("#SelectDiscntTable").children("tr");
						trs.each(function(i,tr){
							var inp = $(tr).children("td").eq(0).children("input");//.find("input [name='SELECTED_DISCNT_CHECKBOX']").val();
							var cla = $(inp).attr("class");
							if (cla=="e_del")
							{
								
							}else
							{
								//去后台校验是否包年套餐
								var discntid = $(inp).val();
								var eparchyCode = $("#USER_EPARCHY_CODE").val();
								var p_str="&NEW_DISCNT_ID="+discntid+"&ROUTE_EPARCHY_CODE="+eparchyCode;
								$.beginPageLoading("校验中。。。。。");
								$.ajax.submit(null,"checkPackageYear",p_str,'',
									function(data){
										$.endPageLoading();
										if(data&&data.length>0)
										{
											changeWidenetProduct.selectActiveDelProduct(inp);
										}
									},
									function(error_code, error_info) 
									{
										$.endPageLoading();
										$.MessageBox.error(error_code, error_info);
									}
								);
							}

						});
						changeWidenetProduct.queryActiveInfo();
					}
				});	
			}else
			{
				changeWidenetProduct.queryActiveInfo();
			}
			
		},
		queryActiveInfo:function(){
			//初始化营销活动包描述信息
			var eparchyCode = $("#USER_EPARCHY_CODE").val();
			var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
			var para_str = "&SERIAL_NUMBER="+serialNumber+"&NEW_PACKAGE_ID="+$("#NEW_PACKAGE_ID").val()+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&EPARCHY_CODE="+eparchyCode;
			//
			var change_old_prod = $("#USER_PRODUCT_ID").val();//用户当前的宽带产品id
			var change_new_prod = $("#NEW_PRODUCT_ID").val();//用户要办理变更的宽带产品id
			var change_checkactive="0";//是否要变宽带产品
			var change_checksvc_str24="";//保存选择产品后的服务id，用于后面的活动规则校验
			if (change_old_prod!=change_new_prod)
			{
				change_checkactive="1";
				change_checksvc_str24 = changeWidenetProduct.getActiveRuleSvcCheck();
			}
			para_str +="&WIDE_USER_CREATE_SALE_ACTIVE="+change_checkactive;
			para_str +="&WIDE_USER_SELECTED_SERVICEIDS="+change_checksvc_str24;
			para_str +="&NEW_SALE_PRODUCT_ID=" + changeWidenetProduct.getSaleProductIdByPackageId($("#NEW_PACKAGE_ID").val());
			para_str +="&CHANGE_UP_DOWN_TAG=" + $("#CHANGE_UP_DOWN_TAG").val(); //标记升级档，0 不变，1：升档,2:降档,3:产品变，速率不变
			para_str +="&BOOKING_DATE=" + $("#BOOKING_DATE").val(); //预约时间
			para_str +="&USER_SALE_PRODUCT_ID=" + $("#USER_SALE_PRODUCT_ID").val(); //预约时间 add 20170511

//			alert("para_str="+para_str);
			$.beginPageLoading("营销活动规则校验中。。。。。");
			$.ajax.submit(null,"initPackageInfo",para_str,'',
				function(data)
				{
					$.endPageLoading();
					changeWidenetProduct.afterQueryPkgInfo(data);
				},
				function(error_code, error_info) 
				{
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
					//清空营销活动选项
					$("#WIDENET_ACTIVE_PACKAGE").val('');
				}
			);
			
		},
		afterQueryPkgInfo:function(data){
			if(data){
				var temp = data.get(0);
				var ext_desc = temp.get("DESCRIPTION");
				//$("#PACKAGE_DESC").text(ext_desc);
				$("#PACKAGE_DESC").val(ext_desc);
			}
			$.endPageLoading();
		},
		//选择生效日期后重新计算元素开始结束时间
		selectFirstDate:function(id)
		{//选择预约日期之后的处理
			var v_date = $("#PACKAGE_VALID").val();
			if (v_date=="")
			{
				return ;
			}
			
			$("#BOOKING_DATE").val(v_date);
			var nextProductDate = $("#NEXT_PRODUCT_DATE");
			nextProductDate.empty();
			
			$.insertHtml('beforeend',nextProductDate,$("#PACKAGE_VALID").val());
			
			changeProductExtend.afterBookingDate();
		},
		selectActiveDelProduct: function(elCheckBox){
			//选择营销活动之后，要删除产品变更组件中已选优惠中的包年套餐优惠，因为，包年套餐和营销活动只能二选一
			var itemIndex = $(elCheckBox).attr("itemIndex");
			
			var el = selectedElements.selectedEls.get(itemIndex);
			
			var check_ed = $(elCheckBox).attr("checked");
			if (check_ed)
			{
				$(elCheckBox).attr("checked",false);
			}else
			{
				$(elCheckBox).attr("checked",true);
			}
			//alert($(elCheckBox).attr("yearflag"));
			
			check_ed = $(elCheckBox).attr("checked")
			if(check_ed){
				var yearflag=$(elCheckBox).attr("yearflag");
				if (yearflag!="1")
				{
					//不是包年套餐那一条，不管
					return;
				}
			    var obj = $("#"+itemIndex+"_ATTRPARAM")
				if(obj){
					obj.attr("disabled","");
				}
				if(el.get("MODIFY_TAG")=="1"){
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
//						elCheckBox.parentNode.parentNode.className="";
						$(elCheckBox).parent().parent().removeClass("e_del");
					}
					else if(el.get("ELEMENT_TYPE_CODE")=="S"){
//						elCheckBox.parentNode.parentNode.parentNode.className="";
						$(elCheckBox).parent().parent().parent().removeClass("e_del");
					}
					if(el.get("MODIFY_ATTR")=="true"){
						el.put("MODIFY_TAG","2");
					}
					else{
						el.put("MODIFY_TAG","exist");
					}

					el.put("START_DATE",el.get("OLD_START_DATE"));
					el.put("END_DATE",el.get("OLD_END_DATE"));
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						selectedElements.resetDate(el);
					}
					return;
				}
				else if(el.get("MODIFY_TAG")=="0_1"){
					el.put("MODIFY_TAG","0");
					var feeData = el.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);
	    					$.feeMgr.insertFee(fee);
	    				}
	    			}
	    		
	    		if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
					eval($("#ELEMENT_EXTEND_ACTION").val());
				}
	    			
					return;
				}
			}
			else{
				var obj = $("#"+itemIndex+"_ATTRPARAM")
				if(obj){
					obj.attr("disabled",true);
				}	
				if(el.get("MODIFY_TAG")=="exist"||el.get("MODIFY_TAG")=="2"){
					//表示是用户原有的元素
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						$(elCheckBox).parent().parent().addClass("e_del");
//						alert($(elCheckBox).parent().parent().attr("class"));
						//
						$(elCheckBox).attr("yearflag","1");
					}
					else if(el.get("ELEMENT_TYPE_CODE")=="S"){
//						elCheckBox.parent().parent().parent().className="e_del";
						$(elCheckBox).parent().parent().parent().addClass("e_del");
					}
					el.put("MODIFY_TAG","1");//删除
					
					el.put("OLD_START_DATE",el.get("START_DATE"));
					el.put("OLD_END_DATE",el.get("END_DATE"));
				}
				else if(el.get("MODIFY_TAG")=="0"){
					el.put("MODIFY_TAG","0_1");
					var feeData = el.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);
	    					$.feeMgr.deleteFee(fee);
	    				}
	    			}
					if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
						eval($("#ELEMENT_EXTEND_ACTION").val());
					}
					return;
				}
			}
			$("#elementPanel").css("display","none");
			if(el.get("MODIFY_TAG")=="1"){
				var tempEls = new $.DatasetList();
				tempEls.add(el);
				var params = "&IS_ELEMENT=true&ELEMENTS="+tempEls.toString()+"&EPARCHY_CODE="+productEnv.eparchyCode;
				if($("#basicStartDateControlId").val()!=""){
					params+="&BASIC_START_DATE="+$("#"+$("#basicStartDateControlId").val()).val();
				}
				if($("#basicCancelDateControlId").val()!=""){
					params+="&BASIC_CANCEL_DATE="+$("#"+$("#basicCancelDateControlId").val()).val();
				}
				if(this.isEffectNow){
					params+="&EFFECT_NOW=true";
				}
				if(typeof(selectedElements.getOtherParam)=="function"){
					params += selectedElements.getOtherParam();
				}
				if(selectedElements.userProductId!=null){
					params+="&USER_PRODUCT_ID="+selectedElements.userProductId;
				}
				if(selectedElements.nextProductId!=null){
					params+="&NEXT_PRODUCT_ID="+selectedElements.nextProductId;
				}
				if(selectedElements.nextProductStartDate!=null){
					params+="&NEXT_PRODUCT_START_DATE="+selectedElements.nextProductStartDate;
				}
				params+="&SELECTED_ELEMENTS="+selectedElements.selectedEls.toString()+"&USER_ID="+productEnv.userId+"&CALL_SVC="+$("#callAddElementSvc").val()+"&TRADE_TYPE_CODE="+$("#SELECTED_TRADE_TYPE_CODE").val();
				params+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
//				alert("params=="+params);
				
				$.beginPageLoading("已选区加载中。。。。。");
				hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist.SelectedElementsHandler","dealElement", params, function(data){changeWidenetProduct.afterSelectActiveDel(data,elCheckBox)},function(errorCode,errorInfo){selectedElements.errProcessReverse(errorCode,errorInfo,elCheckBox)});
			}
		},
		afterSelectActiveDel: function(data,elCheckBox){
			var element = data.get(0);
//			alert("element=="+element);
			if(element.get("ERROR_INFO")){
				MessageBox.confirm("告警提示",element.get("ERROR_INFO").replace(/<br>/ig,"\n")+"\n点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素\n点击“取消”按钮取消本次操作",function(re){
					if(re=="ok"){
						//选择预约日期后，重新计算的优惠结束日期
						var l_date = $("#PACKAGE_VALID1").find("option:selected").text();
						
						var temp = selectedElements.selectedEls.get(element.get("ITEM_INDEX"));
//						temp.put("END_DATE",element.get("END_DATE"));
						temp.put("END_DATE",l_date);
						temp.put("EFFECT_NOW_START_DATE",element.get("EFFECT_NOW_START_DATE"));
//						temp.put("EFFECT_NOW_END_DATE",element.get("EFFECT_NOW_END_DATE"));
						temp.put("EFFECT_NOW_END_DATE",l_date);
						temp.put("OLD_EFFECT_NOW_START_DATE",element.get("OLD_EFFECT_NOW_START_DATE"));
						temp.put("OLD_EFFECT_NOW_END_DATE",element.get("OLD_EFFECT_NOW_END_DATE"));
						
						if(element.get("ELEMENT_TYPE_CODE")=="D"){
							var html=[];
							html.push('<option value="'+temp.get("END_DATE").substring(0,10)+'">'+temp.get("END_DATE").substring(0,10)+'</option>');
							$.insertHtml('afterbegin',$("#"+element.get("ITEM_INDEX")+"_END_DATE"),html.join(""));
							$("#"+element.get("ITEM_INDEX")+"_END_DATE").val(temp.get("END_DATE").substring(0,10));
						}
						$.endPageLoading();
					}else{
						changeWidenetProduct.selectActiveDelProduct(elCheckBox);//elCheckBox.click();
						$.endPageLoading();
					}
				});
			}else{
				//选择预约日期后，重新计算的优惠结束日期
				var l_date = $("#PACKAGE_VALID1").find("option:selected").text();
				
				var temp = selectedElements.selectedEls.get(element.get("ITEM_INDEX"));
//				temp.put("END_DATE",element.get("END_DATE"));
				temp.put("END_DATE",l_date);
				temp.put("EFFECT_NOW_START_DATE",element.get("EFFECT_NOW_START_DATE"));
//				temp.put("EFFECT_NOW_END_DATE",element.get("EFFECT_NOW_END_DATE"));
				temp.put("EFFECT_NOW_END_DATE",l_date);
				temp.put("OLD_EFFECT_NOW_START_DATE",element.get("OLD_EFFECT_NOW_START_DATE"));
				temp.put("OLD_EFFECT_NOW_END_DATE",element.get("OLD_EFFECT_NOW_END_DATE"));
				
				if(element.get("ELEMENT_TYPE_CODE")=="D"){
					var html=[];
					html.push('<option value="'+temp.get("END_DATE").substring(0,10)+'">'+temp.get("END_DATE").substring(0,10)+'</option>');
					$.insertHtml('afterbegin',$("#"+element.get("ITEM_INDEX")+"_END_DATE"),html.join(""));
					$("#"+element.get("ITEM_INDEX")+"_END_DATE").val(temp.get("END_DATE").substring(0,10));
				}
				$.endPageLoading();
			}
		}
	});
})();

$(function() {
	var showCheckbox=getCookie("showCheckbox");  
	if (showCheckbox!="") {    
		if(showCheckbox == "false"){
			$("#showCheckbox").attr('checked',false)
			displayOfferList('chooseoffers')
		}else{
			$("#showCheckbox").attr('checked',true)
			displayOfferList('chooseoffers')
		}
	}  
})
function displayOfferList(obj){
	var div = $('#'+obj);
	if($("#showCheckbox").attr('checked')==true){
		div.css('display', '');
		setCookie("showCheckbox","true",365);
	}
	else{
		div.css('display', 'none');
		setCookie("showCheckbox","false",365);
	}
}

function setCookie(cname,cvalue,exdays) {
	var d = new Date();
	d.setTime(d.getTime()+(exdays*24*60*60*1000));
	var expires = "expires="+d.toGMTString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname){
	var name = cname + "=";  
	var ca = document.cookie.split(';');  
	for(var i=0; i<ca.length; i++){
		var c = ca[i].trim();    
		if (c.indexOf(name)==0) 
			return c.substring(name.length,c.length);  
	}  
	return "";
}













