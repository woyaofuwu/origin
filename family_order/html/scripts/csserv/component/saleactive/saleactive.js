if(typeof(SaleActive)=="undefined"){
	window["SaleActive"]=function(){
		
	};
	var saleactive = new SaleActive();
}

(function(){
	$.extend(SaleActive.prototype,{
		queryProductByType: function(obj){	
			obj = $(obj);
			debugger;
			if(obj.val() != ""){
				var param = "&CAMPN_TYPE="+obj.val() + "&SPEC_TAG=refreshProduct";
				param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
				param += "&USER_ID=" + $('#SALEACTIVE_USER_ID').val();
				$.beginPageLoading("营销产品列表查询中。。。");
				ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
				    $("#SALE_PRODUCT_ID").empty();
				    $("#SALE_PRODUCT_ID").css('width', '100%');
					$("#SALE_PRODUCT_ID").css('width', '');
				    $("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
				    saleactiveList.clearSaleActiveList();
				    
				    var tipflag = "";
				    var tipinfo = "";
				    
				    d.each(function(item){
				       tipflag = item.get("TIP_FLAG");
				       tipinfo = item.get("TIP_INFO");
				    	
					   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
					   item.put('PRODUCT_NAME', productDesc);
					   $("#SALE_PRODUCT_ID").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
				    });
				    //$.Search.get('saleproductsearch').setSearchData(d);
				    $.Search.get("saleproductsearch").setParams("&LABEL_ID="+obj.val());
				    $('#saleproductsearch').attr('disabled', false);
					$.endPageLoading();
					if(saleactive.isTeminalCampnType(obj.val())){
					    $('#imeiQuery').css('display', '');
					    $('#saleStaffId').css('display', '');
					    //IPHONE6 相关处理
					    $('#isIphone6').css('display', 'none');
					    $('#iphone6Imei').css('display', 'none');
					    $("#IS_IPHONE6").val("");
					    $("#IPHONE6_IMEI").val("");
					}else{
					    $('#imeiQuery').css('display', 'none');
					    $('#saleStaffId').css('display', 'none');
					    $("#NEW_IMEI").val("");
					    $('#SALE_STAFF_ID').val("");
					     //IPHONE6 相关处理
					    $('#isIphone6').css('display', 'none');
					    $('#iphone6Imei').css('display', 'none');
					    $("#IS_IPHONE6").val("");
					    $("#IPHONE6_IMEI").val("");
					    if(saleactive.isIphone6CampnType(obj.val())){
					    	$('#isIphone6').css('display', '');
					    	$('#iphone6Imei').css('display', '');
						}
					}
					if("1" == tipflag)
					{
						alert(tipinfo);
					}
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销产品列表查询失败',errorinfo);
					//$.Search.get('saleproductsearch').setSearchData(new $.DatasetList());
					$.Search.get("saleproductsearch").setParams("");
				});				
			}
			else
			{
				$("#SALE_PRODUCT_ID").empty();
				$("#SALE_PRODUCT_ID").css('width', '100%');
				$("#SALE_PRODUCT_ID").css('width', '');
				$("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
				$.Search.get('saleproductsearch').setSearchData(new $.DatasetList());
				$('#saleproductsearch').attr('disabled', true);
			}
		},
		 //IPHONE6 活动大类校验
		isIphone6CampnType:function(campnType){
		   debugger;
		   var terminalCampnTypes = "YX11";
		   if(campnType!=""&&terminalCampnTypes.indexOf(campnType)>-1){
		      return true;
		   }
		   return false;
		},
		isTeminalCampnType:function(campnType){
		   debugger;
		   var terminalCampnTypes = "YX03|YX08|YX09|YX07|YX06";
		   if(campnType!=""&&terminalCampnTypes.indexOf(campnType)>-1){
		      return true;
		   }
		   return false;
		},
		checkByProduct: function(){
			var productId = $("#SALE_PRODUCT_ID").val();
			var userId = $('#SALEACTIVE_USER_ID').val();
			var custId = $('#SALEACTIVE_CUST_ID').val();			
			if(productId != ""){
				var param = "&PRODUCT_ID=" + productId;
				param += "&USER_ID=" + userId;
				param += "&CUST_ID=" + custId;
				param += "&SPEC_TAG=" + "checkByProduct";
				$.beginPageLoading("营销产品校验中。。。");
				ajaxSubmit(null,null,param,'productHiddenPart', function(d){
					$.endPageLoading();	
					//saleactive.queryPackages();
					saleactive.giftSaleactive();//REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销产品校验失败',errorinfo);
				});				
			}
		},
		giftSaleactive: function(){
			var productId = $("#SALE_PRODUCT_ID").val();
			var param = "&PRODUCT_ID=" + productId;
			param += "&SPEC_TAG=" + "giftSaleactive";
			$.beginPageLoading("特殊活动校验中。。。");
				ajaxSubmit(null,null,param,'productHiddenPart', function(d){
					$.endPageLoading();
					if(d.get("productAttr")==1)//是礼品码的活动
					{
						$('#giftCode').css('display', '');
						$('#nullId').css('display', '');
						$('#IS_GIFT_ACTIVE').val('1');
					}
					else
					{
						$('#giftCode').css('display', 'none');
						$('#nullId').css('display', 'none');
						$('#IS_GIFT_ACTIVE').val('0');
					}
					saleactive.queryPackages();
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('特殊活动校验失败',errorinfo);
					return;
				});	
		},
		queryPackages: function(){
		    $('#CommAndHotPart').css('display', 'none');
			$('#saleactiveListPart').css('display', '');
			var imei = $("#NEW_IMEI").val();
			debugger;
			if(imei!="" && imei!="undefined")
			{
				var saleStaffId = $('#SALE_STAFF_ID').val();
			    if(saleStaffId==''){
			       alert("请输入促销员工");return;
			    }
			}
			var productId = $("#SALE_PRODUCT_ID").val();
			var searchContent = $('#SEARCH_CONTENT').val();
			if(productId == '' && imei == '' && searchContent == '')
			{
				alert('未输入查询的条件！');return;
			}
			var campnType = $("#SALE_CAMPN_TYPE").val();
			if(saleactive.isTeminalCampnType(campnType)){
			   $('#imeiQuery').css('display', '');
			   $('#saleStaffId').css('display', '');
			   if(imei==''){
			      alert("请输入终端串码查询可办理的营销包");return;
			   }
			   var saleStaffId = $('#SALE_STAFF_ID').val();
			   if(saleStaffId==''){
			      alert("请输入促销员工");return;
			   }
			}
			// IPHONE6处理
			var is_iphone6 ='';
			var iphone6_imei = $('#IPHONE6_IMEI').val();
			if(saleactive.isIphone6CampnType(campnType)){
				is_iphone6 ='0';
			  	if(iphone6_imei!="" && iphone6_imei!="undefined"){
			   		is_iphone6 ='1';
			   }
			}
			var netOrderId = $('#NET_ORDER_ID').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
			var saleActiveProductId = $("#SALE_PRODUCT_ID").val();
			var saleActiveUserId = $('#SALEACTIVE_USER_ID').val();
			var saleActiveCustId = $('#SALEACTIVE_CUST_ID').val();	
			
			var isGiftActive = $('#IS_GIFT_ACTIVE').val();
			var giftCode = $('#GIFT_CODE').val();
			
            var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent +"&IS_IPHONE6="+is_iphone6+"&IPHONE6_IMEI="+iphone6_imei;
			param += "&SALEACTIVE_USER_ID="+saleActiveUserId+"&SALEACTIVE_CUST_ID="+saleActiveCustId;
			param += "&IS_GIFT_ACTIVE="+isGiftActive+"&GIFT_CODE="+giftCode;  
			saleactiveList.readerComponent(param);
		},
		
		queryPackagesByImei:function(){
		    var imei = $('#NEW_IMEI').val();
		    if(imei==''){
			   alert("请输入终端串码查询可办理的营销包");
			   return;
			}
			var saleStaffId = $('#SALE_STAFF_ID').val();
			if(saleStaffId==''){
			   alert("请输入促销员工");
			   return;
			}
			var netOrderId = $('#NET_ORDER_ID').val();
			var campnType = $('#SALE_CAMPN_TYPE').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
			var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent;
			saleactiveList.readerComponent(param);
		},
		
		readerComponent: function(userId, acctDay, firstDate, nextAcctDay, nextFirstDay, labelId, custId){
			var param = "";
			if(typeof(custId) != 'undefined') param += "&CUST_ID="+custId;
			if(typeof(userId) != 'undefined') param += "&USER_ID="+userId;
			if(typeof(acctDay) != 'undefined') param += "&ACCT_DAY="+acctDay;
			if(typeof(firstDate) != 'undefined') param += "&FIRST_DATE="+firstDate;
			if(typeof(nextAcctDay) != 'undefined') param += "&NEXT_ACCT_DAY="+nextAcctDay;
			if(typeof(nextFirstDay) != 'undefined') param += "&NEXT_FIRST_DAY="+nextFirstDay;
			param += '&NEED_USER_ID='+$('#NEED_USER_ID').val();
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			param += "&LABEL_ID=" + labelId;
			ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val());
		},
		queryPackage: function(obj){
			obj = $(obj);
			var value = obj.attr('package');
			if(value == '') return;
			var productId = value.split("|")[0];
			var packageId = value.split("|")[1];
			var campnType = value.split("|")[2];
			var imei = $("#NEW_IMEI").val();
			if(saleactive.isTeminalCampnType(campnType)){
			   if(imei==''){
			      alert("请输入终端串码查询可办理的营销包");return;
			   }
			   var saleStaffId = $('#SALE_STAFF_ID').val();
			   if(saleStaffId==''){
			      alert("请输入促销员工");return;
			   }
			   var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
			   var param = '&SERIAL_NUMBER='+serialNumber;
			   param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			   param += "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&CAMPN_TYPE=" + campnType;
			   param += "&NEW_IMEI=" + imei + "&SPEC_TAG=checkShotCutActive";
			   ajaxSubmit(null,null,param);
			   saleactiveModule.readerComponent(packageId, productId, campnType, imei);
			}
			else
			{
			   saleactiveModule.readerComponent(packageId, productId, campnType);
			}
		},
		search: function(){
			var productId = $("#Ul_Search_saleproductsearch li[class=focus]").attr("PRODUCT_ID");
			var productName = $("#Ul_Search_saleproductsearch li[class=focus]").attr("PRODUCT_NAME");
			$('#saleproductsearch').val(productName);
			$('#SALE_PRODUCT_ID').val(productId);
			$("#Div_Search_saleproductsearch").css("visibility","hidden");
			saleactive.queryPackages();
		},
		setAcctDayInfo: function(acctDay, firstDate, nextAcctDay, nextFirstDate) {
			$('#SALEACTIVE_ACCT_DAY').val(acctDay);
			$('#SALEACTIVE_FIRST_DATE').val(firstDate);
			$('#SALEACTIVE_NEXT_ACCT_DAY').val(nextAcctDay);
			$('#SALEACTIVE_NEXT_FIRST_DATE').val(nextFirstDate);
		},
		openChoicePage: function(){
			$("#detailTitle").attr("class","");
			$("#detailContent").css("display","none");
			$("#listContent").css("display","");
			$("#listTitle").attr("class","on");
		},
		openDetailPage: function(){
			$("#detailTitle").css("display","");
			$("#detailTitle").attr("class","on");
			$("#detailContent").css("display","");
			$("#listContent").css("display","none");
			$("#listTitle").attr("class","");
		},
		showHotSale: function(){
			$('#CommAndHotPart').css('display', '');
			$('#saleactiveListPart').css('display', 'none');
		},
		showGoodsInfo: function(goodsInfo){
		    debugger;
	        var showInfo = "品牌：" + goodsInfo.get("DEVICE_BRAND")+"  "
					     + "电池：" + goodsInfo.get("BATTERY")+"  "
					     + "颜色：" + goodsInfo.get("COLOR")+"  "
					     + "型号：" + goodsInfo.get("DEVICE_MODEL");
	        $('#GOODS_INFO').html(showInfo);
	        $('#GoodsInfoPart').css('display', '');
	        var allMoneyName = goodsInfo.get("DEVICE_BRAND") + goodsInfo.get("DEVICE_MODEL") + "手机款";
	        $('#ALL_MONEY_NAME').val(allMoneyName);
		},
		hiddenGoodsInfo: function(){
		    $('#GOODS_INFO').val("");
		    $('#GoodsInfoPart').css('display', 'none');
		}
	});
}
)();