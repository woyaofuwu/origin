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
			if(obj.val() != ""  && $("#CONTRACT_STEP").val() == "1"){
				$("#TERMINAL_SEARCH").empty();
	   		    $("#TERMINAL_SEARCH1").empty();
	   		    $('#TerminalPart').css('display', 'none');
	   		    $('#TerminalPart1').css('display', 'none');
	     		$('#StepByPhonePart').css('display', 'none');
				var param = "&CAMPN_TYPE="+obj.val() + "&SPEC_TAG=refreshProduct";
				param += "&CONTRACT_STEP="+ $('#CONTRACT_STEP').val()+"&USER_ID="+$('#SALEACTIVE_USER_ID').val()
				param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
				$.beginPageLoading("营销产品列表查询中。。。");
				ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
				    $("#SALE_PRODUCT_ID").empty();
				    $("#SALE_PRODUCT_ID").css('width', '100%');
					$("#SALE_PRODUCT_ID").css('width', '');
				    $("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
				    saleactiveList.clearSaleActiveList();
				    d.each(function(item){
					   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
					   item.put('PRODUCT_NAME', productDesc);
					   $("#SALE_PRODUCT_ID").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
				    });
					$.endPageLoading();
//					if(saleactive.isTeminalCampnType(obj.val())){
//					    $('#imeiQuery').css('display', '');
//					    $('#saleStaffId').css('display', '');
//					}else{
//					    $('#imeiQuery').css('display', 'none');
//					    $('#saleStaffId').css('display', 'none');
//					    $("#NEW_IMEI").val("");
//					    $('#SALE_STAFF_ID').val("");
//					}
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销产品列表查询失败',errorinfo);
				});	
			}
			else
			{
				$("#TERMINAL_SEARCH").empty();
				$("#SALE_PRODUCT_ID").empty();
				$("#SALE_PRODUCT_ID").css('width', '100%');
				$("#SALE_PRODUCT_ID").css('width', '');
				$("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
			}
			
		},
		isTeminalCampnType:function(campnType){
		   debugger;
		   var terminalCampnTypes = "YX03|YX08|YX09|YX07|YX06";
		   if(campnType!=""&&terminalCampnTypes.indexOf(campnType)>-1){
		      return true;
		   }
		   return false;
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
			var searchContent = '';//$('#SEARCH_CONTENT').val();
			if(productId == '' && imei == '')
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
			var netOrderId = $('#NET_ORDER_ID').val();
			var userInfo = $.auth.getAuthData().get("USER_INFO");
			var serialNumber = userInfo.get("SERIAL_NUMBER");
            var param = "&NET_ORDER_ID="+ netOrderId +"&SERIAL_NUMBER="+ serialNumber + "&NEW_IMEI="+imei;
			param += '&SALE_STAFF_ID=' + saleStaffId +'&CAMPN_TYPE='+ campnType +"&SPEC_TAG=renderByActiveQry";
			param += '&PRODUCT_ID='+ productId +"&SEARCH_CONTENT="+searchContent;
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
		
		readerComponent: function(userId, acctDay, firstDate, nextAcctDay, nextFirstDay, labelType){
			var param = "";
			if(typeof(userId) != 'undefined') param += "&USER_ID="+userId;
			if(typeof(acctDay) != 'undefined') param += "&ACCT_DAY="+acctDay;
			if(typeof(firstDate) != 'undefined') param += "&FIRST_DATE="+firstDate;
			if(typeof(nextAcctDay) != 'undefined') param += "&NEXT_ACCT_DAY="+nextAcctDay;
			if(typeof(nextFirstDay) != 'undefined') param += "&NEXT_FIRST_DAY="+nextFirstDay;
			param += '&NEED_USER_ID='+$('#NEED_USER_ID').val();
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
			param += "&LABEL_TYPE=" + labelType;
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
		},
		hiddenGoodsInfo: function(){
		    $('#GOODS_INFO').val("");
		    $('#GoodsInfoPart').css('display', 'none');
		},
		checkContractType:function(obj){
			obj=$(obj);
			
			$("#TERMINAL_SEARCH").val("");
   		    $("#TERMINAL_SEARCH1").val("");
		    $("#NEW_IMEI").val("");
		    $('#SALE_STAFF_ID').val("");
		    
        	if($("#CONTRACT_STEP").val() == "0"){//先选手机
        	    $('#TerminalPart').css('display', '');
       		    $('#TerminalPart1').css('display', 'none');
        		$('#StepByPhonePart').css('display', '');
     		    $("#SALE_PRODUCT_ID").empty();
     		    
     		   $('#imeiQuery').css('display', 'none');
     		   $('#saleStaffId').css('display', 'none');
     	   	}
        	else if($("#CONTRACT_STEP").val() == "1"){//先选合约
        	    $('#TerminalPart').css('display', 'none');
       		    $('#TerminalPart1').css('display', '');
     		   $('#StepByPhonePart').css('display', 'none');
     		   
     		   $('#imeiQuery').css('display', '');
     		   $('#saleStaffId').css('display', '');
     		   
     		   var param = '&CONTRACT_STEP=1&CAMPN_TYPE='+$('#SALE_CAMPN_TYPE').val()+'&USER_ID='+$('#SALEACTIVE_USER_ID').val();
     		   param += "&SPEC_TAG=refreshProduct&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
     		   $.beginPageLoading("营销产品列表查询中。。。");
     		   ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
				    $("#SALE_PRODUCT_ID").empty();
				    $("#SALE_PRODUCT_ID").css('width', '100%');
					$("#SALE_PRODUCT_ID").css('width', '');
				    $("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
				    saleactiveList.clearSaleActiveList();
				    d.each(function(item){
					   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
					   item.put('PRODUCT_NAME', productDesc);
					   $("#SALE_PRODUCT_ID").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
				    });
					$.endPageLoading();
//					if(saleactive.isTeminalCampnType(obj.val())){
//					    $('#imeiQuery').css('display', '');
//					    $('#saleStaffId').css('display', '');
//					}else{
//					    $('#imeiQuery').css('display', 'none');
//					    $('#saleStaffId').css('display', 'none');
//					    $("#NEW_IMEI").val("");
//					    $('#SALE_STAFF_ID').val("");
//					}
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销产品列表查询失败',errorinfo);
				});	
     	   }
        },
        checkStepByPhoneInfo:function(obj) {
        	
        	var price_start = $("#CONTRACT_PRICE_RANGE_START").val();
        	var price_end = $("#CONTRACT_PRICE_RANGE_END").val();
        	var terminal = $("#TERMINAL_SRC_CODE").val();
        	if(price_start == "" && price_end=="" && terminal =="")
        	{
        		alert("终端厂商和价格区间必选一项！");
        		return false;
        	}
        	
        	if((price_start == "" && price_end !="") || (price_start != "" && price_end==""))
        	{
        		alert("价格区间开始和结束都必选！");
        		return false;
        	}
        	
        	$('#NEW_IMEI').val("");
            var params = "&STARTPRICE="+ price_start + "&SPEC_TAG=getTerminalByHW&ENDPRICE="+price_end;
            params += "&FACTOR_CODE="+terminal+"&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
            
        	$.beginPageLoading("终端型号列表查询中。。。");
			ajaxSubmit(null,null,params,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
			    $("#TERMINAL_SEARCH").empty();
			    $("#TERMINAL_SEARCH1").empty();
			    if($("#CONTRACT_STEP").val() =="0"){
	        		$('#TerminalPart').css('display', '');
	        		$('#TerminalPart1').css('display', 'none');
	        		$("#TerminalPart").css('width', '100%');
					$("#TerminalPart1").css('width', '');
				    $("#TERMINAL_SEARCH").append("<option value=\"\">--请选择--</option>");	
				    saleactiveList.clearSaleActiveList();
				    d.each(function(item){ 
				    	var terminalValue = item.get("DEVICE_TYPE_CODE") + ',' +item.get("DEVICE_MODEL_CODE") + ',' + item.get("DEVICE_COST")+ ',' + item.get("SALE_PRICE");
					    $("#TERMINAL_SEARCH").append("<option title=\"" + item.get("GOODS_EXPLAIN") + "\"" + "value=\"" + terminalValue + "\">" +  item.get("GOODS_EXPLAIN") + "</option>");
				    });
	        	}
				$.endPageLoading();
			},
			function(errorcode, errorinfo){
				$.endPageLoading();
				$.showErrorMessage('终端型号列表查询失败',errorinfo);
			});
        },
        checkTerminalSearch:function(obj){
        	obj = $(obj);
        	if($("#CONTRACT_STEP").val() =="0"){
        		
        		var campnType=$("#SALE_CAMPN_TYPE").val();
        		if(!campnType){
        			alert("请选择活动类型！");
        			return false;
        		}
        		
        		var param = '&CONTRACT_STEP=0&DEVICE_MODEL_CODE='+obj.val()+'&CAMPN_TYPE='+campnType+'&USER_ID='+$('#SALEACTIVE_USER_ID').val();
      		   	param += "&SPEC_TAG=refreshProduct&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
      		   	$.beginPageLoading("营销产品列表查询中。。。");
      		   	ajaxSubmit(null,null,param,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
 				    $("#SALE_PRODUCT_ID").empty();
 				    $("#SALE_PRODUCT_ID").css('width', '100%');
 					$("#SALE_PRODUCT_ID").css('width', '');
 				    $("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
 				    saleactiveList.clearSaleActiveList();
 				    d.each(function(item){
 					   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
 					   item.put('PRODUCT_NAME', productDesc);
 					   $("#SALE_PRODUCT_ID").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
 				    });
 					$.endPageLoading();
 					if(saleactive.isTeminalCampnType(obj.val())){
 					    $('#imeiQuery').css('display', '');
 					    $('#saleStaffId').css('display', '');
 					}else{
 					    $('#imeiQuery').css('display', 'none');
 					    $('#saleStaffId').css('display', 'none');
 					    $("#NEW_IMEI").val("");
 					    $('#SALE_STAFF_ID').val("");
 					}
 				},
 				function(errorcode, errorinfo){
 					$.endPageLoading();
 					$.showErrorMessage('营销产品列表查询失败',errorinfo);
 				});	
        	}
        },
        getTerminalByID: function(){
        	if($("#CONTRACT_STEP").val() == "0"){//先选手机
        		var saleAct=$('#SALE_PRODUCT_ID').val();
        		if(!saleAct){
        			$('#imeiQuery').css('display', 'none');
            		$('#saleStaffId').css('display', 'none');
        		}else{
        			$('#imeiQuery').css('display', '');
            		$('#saleStaffId').css('display', '');
        		}
        		
        		
        	}else if($("#CONTRACT_STEP").val() == "1"){//先选合约
				var params = "&SPEC_TAG=getTerminalByID&PRODUCT_ID="+$('#SALE_PRODUCT_ID').val();
				params += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVE_EPARCHY_CODE_COMPID').val()).val();
	            
	        	$.beginPageLoading("终端型号列表查询中。。。");
				ajaxSubmit(null,null,params,$('#SALEACTIVE_COMPONENT_ID').val(), function(d){
				    $("#TERMINAL_SEARCH1").empty();
	        		$("#TerminalPart1").css('width', '100%');
				    $("#TERMINAL_SEARCH1").append("<option value=\"\">--请选择--</option>");	
				    saleactiveList.clearSaleActiveList();
				    d.each(function(item){ 
				    	var terminalDesc = item.get("DEVICE_BRAND") + '|' +item.get("DEVICE_MODEL") + '|' + item.get("SALE_PRICE_NAME");
					    $("#TERMINAL_SEARCH1").append("<option title=\"" + item.get("DEVICE_MODEL") + "\"" + "value=\"" + item.get("DEVICE_MODEL_CODE") + "\">" +  terminalDesc + "</option>");
				    });
					$.endPageLoading();
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('终端型号列表查询失败',errorinfo);
				});	
			}
			
		}
	});
}
)();
