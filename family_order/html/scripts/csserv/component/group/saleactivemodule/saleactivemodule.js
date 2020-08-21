
var gElems = $.DatasetList();

if(typeof(SaleActiveModule)=="undefined"){
	window["SaleActiveModule"]=function(){
		
	};
	var saleactiveModule = new SaleActiveModule();
}

(function(){
	$.extend(SaleActiveModule.prototype,{
		readerComponent: function(param,userId){	
			saleactiveModule.clearSaleActive();
			var checkParam = param;
			checkParam += '&SPEC_TAG=checkByPackage';
			
			$.beginPageLoading("规则校验中。。。");	
			ajaxSubmit(null, null, checkParam, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), 
				function(data) {
					$.endPageLoading();
					var flag = data.get("FLAG");
					if(flag=="false"){
						var message = data.get("ERROR_MESSAGE");
						window.alert(message);
						return false;
					}
					saleactiveModule.drawSaleActive(param);
					$('#SALEACTIVE_USER_ID').val(userId);
				}, 
				saleactiveModule.activeCheckFail);
		},
		drawSaleActive : function(param){
			$.beginPageLoading("营销活动展现中。。。");		
			ajaxSubmit(null, null, param, $('#SALEACTIVEMODULE_COMPONENT_ID').val(), saleactiveModule.afterDrawSaleActive,
			function(errorcode, errorinfo){
				$.endPageLoading();
				$.showErrorMessage('活动查询失败',errorinfo);
			});
		},
		activeCheckFail: function(rscode, rsinfo){
			$.endPageLoading();
			$.showErrorMessage('活动校验不通过',rsinfo);
		},
		afterDrawSaleActive: function(ajaxDataset){
			$.endPageLoading();
		},
		clearSaleActive: function() {
			var componentId = $("#SALEACTIVEMODULE_COMPONENT_ID").val();
			$('#'+componentId+'_PART').html('');
			$.feeMgr.clearFeeList("3606");
			var gElems = $.DatasetList();
			
			saleactiveModule.cancelElementAttr('');
		},
		
		/**
		 * 提交时获取营销活动相关的数据
		 */
		getSaleActiveSubmitData: function(){
			var saleactiveData = new Wade.DataMap();
			saleactiveData.put("PRODUCT_ID", $("#SALEACTIVE_PRODUCT_ID").val());
			saleactiveData.put("PACKAGE_ID", $("#SALEACTIVE_PACKAGE_ID").val());
			saleactiveData.put("SALEGOODS_IMEI", $("#SALEGOODS_NEW_IMEI").val());
			saleactiveData.put("SALE_STAFF_ID", $("#SALE_STAFF_ID").val());
			saleactiveData.put("CAMPN_TYPE", $("#SALEACTIVE_CAMPN_TYPE").val());
			saleactiveData.put("START_DATE", $("#SALEACTIVE_START_DATE").val());
			saleactiveData.put("END_DATE", $("#SALEACTIVE_END_DATE").val());
			saleactiveData.put("BOOK_DATE", $("#SALEACTIVE_BOOK_DATE").val());
			saleactiveData.put("ONNET_START_DATE", $("#SALEACTIVE_ONNET_START_DATE").val());
			saleactiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").val());
			saleactiveData.put("ONNET_END_DATE", $("#SALEACTIVE_ONNET_END_DATE").val());
			saleactiveData.put("NEED_ACCT", $("#SALEACTIVE_NEED_ACCT").val());
			saleactiveData.put("ACCT_ID", $("#SALEACTIVE_ACCT_ID").val());
			if(gElems.length > 0){
				saleactiveData.put("SELECTED_ELEMENTS", gElems);
			}
			
			return saleactiveData;
		},
		/**
		 * 提交时的营销活动JS规则校验
		 */
		saleactiveSubmitJSCheck: function(){
			if($("#SALEACTIVE_PACKAGE_ID").length == 0 || $("#SALEACTIVE_PACKAGE_ID").val() == ''){
				alert('请先选择一个活动');
				return false;
			}
			
			if($("#SALEACTIVE_NEED_ACCT").val() == '1' && ($("#SALEACTIVE_ACCT_ID").length == 0 || $("#SALEACTIVE_ACCT_ID").val() == '')){
				alert('该营销活动包必须选择账户信息!');
				return false;
			}
			var minElem = parseInt($("#SALEACTIVE_PGK_ELEMENT_MIN").val());
			var maxElem = parseInt($("#SALEACTIVE_PGK_ELEMENT_MAX").val());
			if (gElems.length < minElem || gElems > maxElem) {
				alert("元素个数限制! 最小选择数："+minElem+" 最大选择数："+maxElem);
				return false;
			}
		    
			var goods = saleactiveModule.spGetGoods();
			if (goods.length>0) {
				for(var i=0;i<goods.length;i++){
					var good = goods.get(i);
					if (good.get("HAS_CHECKED") != "TRUE") {
						alert("请先校验资源!");
						return false;
					}else if (good.get("RES_CODE") == "-1") {
						alert("资源数据丢失,请重新输入检验!");
						good.put("HAS_CHECKED","FALSE") // 设置实物为未校验状态
						var elemKey = good.get("ELEM_KEY");
						var elemKeys = elemKey.split("_");
						var ResTextId=$(elemKeys[0]+"_"+elemKeys[1]+"_RES_CODE");
						ResTextId.attr('disabled', false);
						return false;
					}
				}
			}
		    return true;
		},
		showAttr: function(eventObj){
			var obj = $(eventObj);
			var element = saleactiveModule.spGetElem(obj.attr("checkboxId"));
			var id = obj.attr("checkboxId");
			if(element == null) 
			{
				element = saleactiveModule.spGetElem(obj.attr("id"));
				if(element == null) 
				{
					return;
				}
				id = obj.attr("id");
			}
			var date = $('#'+id+'_DATES').val();
			var params = "&ELEMENT_ID="+element.get("ELEMENT_ID")+"&ELEMENT_TYPE_CODE="+element.get("ELEMENT_TYPE_CODE")
				+"&ITEM_INDEX="+id+"&DATES="+date+"&END_ENABLE_TAG="+obj.attr("end_enable_tag");
			
			//add at 2015-07-21
			var attrs = element.get("ATTR_PARAM");
			if(typeof(attrs) != 'undefined' && attrs.length > 0)
			{
				var length = attrs.length;
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					if("7347" == attrCode){
						params = params + "&DISPLAY_CONDITION=D" + attr.get("RSRV_STR2");
						break;
					}
				}
			}
			//add at 2015-07-21
			$.ajax.submit(null,null,params,$("#ELEMENTATTR_COMPONENT_ID").val(),function(){saleactiveModule.afterShowAttr(eventObj,element)});
		},
		afterShowAttr: function(eventObj,tempElement){
			//设置回填值
			var obj = $(eventObj);
			var attrs = tempElement.get("ATTR_PARAM");
			if(typeof(attrs) != 'undefined' && attrs.length > 0)
			{
				var length = attrs.length;
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					$("#"+attrCode).val(attrValue);
					if("81408" == attrCode || "81409" == attrCode){
						var attrObj = $("#"+attrCode);
						var attr81408 = $("#81408");	//单价X(元)
						var attr81409 = $("#81409");	//数量Y
						var attr81410 = $("#81410");	//约定消费金额(元)
						/*绑定onchange事件，用于输入分钟数/流量时，自动计算功能费*/
						attrObj.change(function(){
							var chgAttrVal = $(this).val();
							var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/;
						    if (!reg.test(chgAttrVal))
						    {
						    	MessageBox.alert("提示信息", "限定值必须为数字,请重新输入！");
						    	$(this).focus();
						    	$(this).val(0);
						        return false;
						    }
							if (attr81408.val() != null && attr81408.val() != 'undefined' 
								&& attr81409.val() != null && attr81409.val() != 'undefined'){
								attr81410.val( Math.round(attr81408.val() * 100) * parseInt(attr81409.val()) );					//显示为页面上约定消费金
							}
						});
					}
					if("81410" == attrCode){
						var attrObj = $("#"+attrCode);
						attrObj.attr("disabled", "true");	//功能费不可编辑
					}
				}
				
			}
			var o = $(eventObj).offset();	
			var topAdd = 0;
			var scroll =  $("div[class=m_wrapper]:first");
			if(scroll.length>0){
				topAdd = scroll.attr("scrollTop");
			}
			var leftAdd = 0;
			if(null==obj.attr("checkboxId")){
				leftAdd=270;
			}
			var optionTop = o.top+obj.height()+topAdd;
			$("#elementPanel").css("top", optionTop + "px");
			$("#elementPanel").css("left", (o.left+obj.width()+leftAdd-$("#elementPanel").width()) + "px");
			$("#elementPanel").css("display","");
			
			//处理参数页面的长度高于top到窗口底部的高度。
			var panelHight = $("#elementPanel").height();					 
			var winClientHeight = document.body.clientHeight;
			var panelClientHeight = winClientHeight - optionTop;
			
			if(panelHight > (panelClientHeight)){
				var panelHeight = optionTop-panelHight ;
				if(panelHeight >=0 ){
					$("#elementPanel").css("top", optionTop-panelHight + "px");
				}else{
						
					$('#elementPanelDiv').attr('class','c_scroll');
					$('#elementPanelDiv').height(panelClientHeight-40+'px');
				}
			}
		},
		hideAttr: function(){
			$("#elementPanel").css("display","none");
		},
		confirmAttr: function(itemIndex) {
			itemIndex = $('#ATTR_ITEM_INDEX').val();
			var element = saleactiveModule.spGetElem(itemIndex);
			var attrs = $.DatasetList();
			$("#elementPanel select").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attrs.add(attr);
			});
			$("#elementPanel input").each(function(){
				var attr = $.DataMap();
				attr.put('ATTR_CODE', $.attr(this, 'id'));
				attr.put('ATTR_VALUE', $.attr(this, 'value'));
				attrs.add(attr);
			});
			element.put("ATTR_PARAM", attrs);
			$("#elementPanel").css("display","none");
			var startDate= $('#START_DATE').val();
			var endDate= $('#END_DATE').val();
			element.put("START_DATE",startDate);
			element.put("END_DATE",endDate);
			$('#'+itemIndex+'_START_DATE').html(startDate);
			$('#'+itemIndex+'_END_DATE').html(endDate);
		},
		cancelElementAttr: function(itemIndex){
			$("#elementPanel").css("display","none");
			$("#elementPanelUL").html();
		},
		disabledDepositPlusItem: function(cbid, flag) {
			var checkboxObj = $("#"+cbid);
			var giftUseTag = checkboxObj.attr('gift_use_tag');
			var depositType = checkboxObj.attr('deposit_type');
			if(depositType == '2')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_FEE";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
			if(giftUseTag == '1')
			{
				var index = checkboxObj.attr('index');
				var plusId = "DEPOSIT_" + index + "_DEPOSIT_USER_ID";
				if(plusId != null && plusId != '') {
					if($("#"+plusId).length > 0) {
						$("#"+plusId).attr('disabled', flag);
					}
				}
			}
		},
		/**
		 * 包展现时字段拼装已选上的元素
		 */
		spAutoAddCheckedElems: function(){
			gElems = $.DatasetList();
			$('#detailTitleName').text($('#SALEACTIVE_PACKAGE_NAME').val());
		
			// 优惠
			$("#SaleDiscntTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 服务
			$("#SaleServiceTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//预存赠送
			$("#SaleDepositTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//积分
			$("#SaleScoreTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//信用度
			$("#SaleCreditTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			//平台业务
			$("#SalePlatSvcTable input[type=checkbox]").each(function()
			{
				if($.attr(this, "checked"))
				{
					saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
				}
			});
			// 实物
			$("#SaleGoodsTable input[type=checkbox]").each(function()
			{
				saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});
			
			$("#SaleCombineTable input[type=checkbox]").each(function()
			{
				saleactiveModule.spCheckBoxOnclickAction($.attr(this, "id"));
			});
		},
		spCheckBoxOnclickAction: function(cbId){
			var cb = $("#"+cbId);
			if (cb.attr("checked")) {
				saleactiveModule.spDecodeElem(cbId);
				
				if(cb.attr("has_attr")){
					saleactiveModule.showAttr(cb);
				}
			} else { 
				saleactiveModule.hideAttr();
				saleactiveModule.spDelElemById(cbId);
			}
		},
		/*拼装元素串*/
		spDecodeElem: function(cbId) {
			var param = new Wade.DataMap();
			var cb = $("#"+cbId);
			saleactiveModule.spDecodePubParam(param, cb);
			
			var elemType = cb.attr("element_type_code");
		
			if (elemType == 'S') {
				saleactiveModule.spDecodeServiceParam(param, cb);
			} else if (elemType == 'D') {
				saleactiveModule.spDecodeDiscntParam(param, cb);
			} else if (elemType == 'R') {
				//暂不支持
			} else if (elemType == 'Z') {
				saleactiveModule.spDecodePlatsvcParam(param, cb);
			} else if (elemType == 'A') {
				saleactiveModule.spDecodeDepositParam(param, cb);
			} else if (elemType == 'G') {
				saleactiveModule.spDecodeGoodsParam(param,cb);
			} else if (elemType == 'J') {
				saleactiveModule.spDecodeScoreParam(param, cb);
			} else if (elemType == 'C') {
				saleactiveModule.spDecodeCreditParam(param, cb);
			} else if (elemType == "K") {
			    saleactiveModule.spDecodeCombineParam(param, cb);
			}
		},
		
		/** 拼各类元素共有的参数 */
 		spDecodePubParam: function(param, elem) {
			param.put("ELEM_KEY", elem.attr("id"));
			
			param.put("ELEMENT_ID", elem.attr("element_id"));
			param.put("ELEMENT_TYPE_CODE", elem.attr("element_type_code"));
			param.put("MODIFY_TAG", "0");
			if(typeof(elem.attr('fee')) != 'undefined')
			{
				param.put("FEE", elem.attr("fee"));
				param.put("FEE_MODE", elem.attr("fee_mode"));
				param.put("FEE_TYPE_CODE", elem.attr("fee_type_code"));
				param.put("PAY_MODE", elem.attr("pay_mode"));
				param.put("IN_DEPOSIT_CODE", elem.attr('in_deposit_code'));
				param.put("OUT_DEPOSIT_CODE", elem.attr('out_deposit_code'));
				param.put('PAYMENT_ID', elem.attr('fee_type_code'));
			}
		},
		
		/*通过checkId删除一个元素*/
		spDelElemById: function(cbId) {
			var cb = $('#'+cbId);
			gElems.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					gElems.removeAt(index);
					saleactiveModule.spDelFeeItem(item);
					if(item.get('ELEMENT_TYPE_CODE') == 'A')
					{
						saleactiveModule.disabledDepositPlusItem(item.get("ELEM_KEY"), true);
					}
				}
			});
		},
		/*服务拼串*/
		spDecodeServiceParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.decodeAttrParam(param, elem)
			saleactiveModule.spAddElem(param);
		},
		/*优惠拼串*/
		spDecodeDiscntParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.decodeAttrParam(param, elem)
			saleactiveModule.spAddElem(param);
		},
		/*A元素拼串*/
		spDecodeDepositParam: function(param, elem) {
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			param.put("SERIAL_NUMBER_B", elem.attr('serial_number_b'));
			param.put("GIFT_USER_ID", elem.attr('user_id_a'));
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		
			saleactiveModule.disabledDepositPlusItem(param.get('ELEM_KEY'), false);
		},
		/*平台业务拼串*/
		spDecodePlatsvcParam: function(param, elem) {
			param.put("ELEMENT_ID", elem.attr('element_id'));
			saleactiveModule.spAddElem(param);
		},
		
		/*实物拼串*/
		spDecodeGoodsParam: function(param,elem) {
			var id = elem.attr("id");
			param.put("HAS_CHECKED",elem.attr("has_check"));
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		
		/**构建积分数据 */
 		spDecodeScoreParam: function(param, elem) {
			param.put("SCORE_VALUE", $("#"+elem.attr('input_id')).val());//从积分输入框中取值
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		/**构建信用度数据 */
		spDecodeCreditParam: function(param, elem) {
			//spDecodeCampnParam(param, elem);
			param.put("CREDIT_VALUE", elem.attr('credit_value'));
			param.put("CREDIT_GIFT_MONTHS", elem.attr('credit_gift_months'));
			param.put("START_DATE", elem.attr("start_date"));
			param.put("END_DATE", elem.attr("end_date"));
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		
		spDecodeCombineParam: function(param, elem){
			saleactiveModule.spAddElem(param);
			saleactiveModule.spAddFeeItem(param);
		},
		/*添加元素费用*/
		spAddFeeItem: function(param) {
			var fee = param.get("FEE", ""); 
			var feeMode = param.get("FEE_MODE", ""); 
			var feeTypeCode = param.get("FEE_TYPE_CODE", ""); 
			var payMode = param.get("PAY_MODE", "");
			var elemType = param.get("ELEMENT_TYPE_CODE", "");
			if (fee == "" || feeMode == "" || feeTypeCode == "") {
				return false;
			}
			if (payMode == "1") {
				return true; 
			}
			//A元素的ELEMENT_ID需要记录到表里，后续要根据ELEMENT_ID找到ACTION_CODE(A_DISCNT_CODE)传到账务
			if(elemType == 'A')
			{
				saleactiveModule.insertFee("3606", param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('FEE'), param.get('ELEMENT_ID'));
			}
			else
			{
				saleactiveModule.insertFee("3606", param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('FEE'));
			}
		},
		/*构建元素的属性值*/
		decodeAttrParam: function(param, elem) {
			var attrInitParam = $("#"+$(elem).attr("id")+'_ATTR').val();
			if(typeof(attrInitParam) != 'undefined' && attrInitParam != '')
			{
				var attrs = $.DatasetList(attrInitParam);
				param.put("ATTR_PARAM", attrs);
			}
		},
		/*删除元素的费用*/
		spDelFeeItem: function(param) {
			if(param.get('ELEMENT_TYPE_CODE') == 'A')
			{
				$.feeMgr.removeFee("3606", param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'), param.get('ELEMENT_ID'));
			}
			else
			{
				$.feeMgr.removeFee("3606", param.get('FEE_MODE'), param.get('FEE_TYPE_CODE'));
			}
		},
		
		spAddElem: function(elem) {
			gElems.each(function(item, index, totalcount) {
						if (item.get("ELEM_KEY") == elem.get("ELEM_KEY")) {
							gElems.removeAt(index);
						}
					});
			gElems.add(elem);
		},
		spDelElem: function(elem) {
			gElems.each(function(item, index, totalcount) {
				if (item.get("ELEM_KEY") == elem.get("ELEM_KEY")) {
					gElems.removeAt(index);
					saleactiveModule.spDelFeeItem(item);
				}
			});
		},
		insertFee: function(tradeTypeCode, feeMode, feeTypeCode, fee, elementId) {
			var userId = $("#SALEACTIVE_USER_ID").val();
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
			feeData.put("MODE", feeMode);
			feeData.put("CODE", feeTypeCode);
			feeData.put("FEE", fee);
			feeData.put("USER_ID", userId);
			if(typeof(elementId) != "undefined" && elementId != "")
			{
				feeData.put('ELEMENT_ID', elementId);
			}
			$.feeMgr.insertFee(feeData);
			// 设置pos参数
			$.feeMgr.setPosParam(tradeTypeCode, $("#SALEACTIVE_SERIAL_NUMBER").val(), $("#SALEACTIVE_EPARCHY_CODE").val());
		},
		spCheckResInfo: function(obj){
			var button = $(obj);
			var inpt = $("#GOODS_"+button.attr("index")+"_RES_CODE");
			if(inpt.val() == ''){
				alert('不能为空');return;
			}
			if(inpt.attr("enterstafftag")=="1"){
				var saleStaff = $("#GOODS_"+button.attr("index")+"_STAFF_ID");
				if(saleStaff.val()==''){
					alert('请输入促销员工');return;
				}
			}
			
			// 设置实物为未校验
			saleactiveModule.spSetResCheckState("FALSE",inpt.attr("goodsId"));
			
			if($(inpt).val().length > 20){
				alert('串码最多20位,您输入的终端串码过长,请确认后输入!');return;
			}

			var checkResParam = '&RES_TYPE_CODE='+inpt.attr('resTypeCode')+"&RES_ID="+inpt.attr('resId')+"&RES_NO="+inpt.val();
			checkResParam += '&PRODUCT_ID='+$("#SALEACTIVE_PRODUCT_ID").val();
			checkResParam += '&PACKAGE_ID='+$("#SALEACTIVE_PACKAGE_ID").val();
			if($("#SALEACTIVE_OTHER_NUMBER").length > 0)
			{
				checkResParam += '&OTHERNET_TAG='+$("#SALEACTIVE_OTHERNET_TAG").val();
				checkResParam += '&OTHER_NUMBER='+$("#SALEACTIVE_OTHER_NUMBER").val();
			}
			checkResParam += '&SPEC_TAG=checkResInfo';
			checkResParam += "&EPARCHY_CODE=" + $('#SALEGOODS_EPARCHY_CODE').val();
			checkResParam += "&RES_CHECK="+inpt.attr('resCheck');
			checkResParam += "&SALE_STAFF_ID="+$("#GOODS_"+button.attr("index")+"_STAFF_ID").val();
			
			$.beginPageLoading("终端预占中。。。");	
				
			ajaxSubmit(null, null, checkResParam, $('#SALEGOODS_COMPONENT_ID').val(), 
				   function(d){
				      saleactiveModule.spAfterCheckResInfo(d, inpt);
				   },
			       function(errorcode, errorinfo){
				      $.endPageLoading();
				      $.showErrorMessage('终端预占失败',errorinfo);
				   }
			);
		},
		spSetResCheckState: function(state,goodsId){
			var goods = saleactiveModule.spGetGoodByGoodId(goodsId);
			if (goods) {
				goods.put("HAS_CHECKED", state);
				saleactiveModule.spAddElem(goods);
			}
		},
		spGetGoodByGoodId: function(goodsId){
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY").indexOf("GOODS")>=0&&item.get("ELEMENT_ID")==goodsId) {
					return item;
				}
			}
			return null;
		},
		
		spGetGoods: function(goodsId){
			var result = new $.DatasetList();
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY").indexOf("GOODS")>=0) {
					result.add(item);
				}
			}
			return result;
		},
		spAfterCheckResInfo: function(ajaxDataset, obj){
			$.endPageLoading();
			if (ajaxDataset.length <= 0) {
				alert("资源校验失败!");
				return false;
			}
			var tgoods = saleactiveModule.spGetGoodByGoodId(obj.attr("goodsId"));
			tgoods.put("HAS_CHECKED", "TRUE"); // 设置实物为已校验状态
			tgoods.put("RES_CODE", $(obj).val()); // 资源号码
			tgoods.put("SALE_STAFF_ID", $("#GOODS_"+obj.attr("index")+"_STAFF_ID"));
			saleactiveModule.spAddElem(tgoods);
			obj.attr("disabled","true");
			alert("资源校验成功");
		},
		/*根据checkId获取一个元素*/
		spGetElem: function(cbId) {
			var cb = $('#'+cbId);
			for (var i = 0; i < gElems.length; i++) {
				var item = gElems.get(i);
				if (item.get("ELEM_KEY") == cb.attr("id")) {
					return item;
				}
			}
			return null;
		},
		changeStartDate: function(obj) {
			var endOffSet = $("#END_OFFSET")[0];
			var endOffSetIndex = endOffSet.selectedIndex;
			var startIndex = obj.selectedIndex;
			
			var endSize = endOffSetIndex*2+startIndex;
			$("#END_DATE")[0].options[endSize].selected = true; // 结束时间联动
		},
		changeEndOffSet: function(obj) {
			var startDate = $("#START_DATE")[0];
			var startIndex = startDate.selectedIndex;
			var endOffSetIndex = obj.selectedIndex;
			
			var endSize = endOffSetIndex*2+startIndex;
			$("#END_DATE")[0].options[endSize].selected = true; // 结束时间联动
			
		}
		
	});
})();
	