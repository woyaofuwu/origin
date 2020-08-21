(function($){
	$.extend({feeMgr:{
		testTag:false,						//测试开关
		activeFlag : true,					//当前窗口激活标识
		cacheFee : "",						//临时缓冲的费用信息，主要是窗口切换之间暂存
		cacheFeeList : $.DatasetList(),		//临时缓冲的费用列表,主要窗口之间操作使用

		//查询业务费用列表
		loadTradeFee:function(tradeTypeCode, productId, eparchyCode, vipClassId){
			var params = "&TRADE_TYPE_CODE="+tradeTypeCode;
			params += "&PRODUCT_ID="+productId;
			if(eparchyCode){
				params += "&EPARCHY_CODE="+eparchyCode;				
			}
			if(vipClassId){
				params += "&VIP_CLASS_ID="+vipClassId;				
			}
			
			$.ajax.submit("", "getTradeFee", params,"",
				//提交成功回调
				function(dataset){
					if(!dataset || (dataset && dataset.length == 0)){
						return ;
					}
					//缓存好费用列表
					$.feeMgr.cacheFeeList = dataset;
					$.feeMgr.showFeeList($.feeMgr.cacheFeeList);
				},function(code, info, detail){
					$.endPageLoading();
					MessageBox.error("错误提示","加载营业费用错误！", null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示","加载营业费用超时!");
			});
		},
		
		/**
		 * 是否开启测试模式
		 * 在无外框情况下，开启测试模式
		 */
		setTestTag:function(flag){
			$.feeMgr.testTag = flag;
		},
		
		//从框架获取费用数据
		getViewFee:function() {
			//开启测试模式
			if($.feeMgr.testTag){
				return $.feeMgr.getTmpViewFee();
			}
			var data = null;
			//如果非激活状态，则从页面缓存获取费用数据，否则直接从外框获取
			if(!$.feeMgr.activeFlag){
				data = $.DataMap($.feeMgr.cacheFee);
			}else{
				if($.os.phone) {
					if($.ifee)
					{
						data = $.DataMap($.ifee.getFee());
					}
				}else if(top.getFee){
					data = $.DataMap(top.getFee());
				}
			}
			return data;
		},
		
		/**
		 * 临时接口，主要给调试使用
		 */
		getTmpViewFee:function() {
			var data = $.DataMap();
			data.put("FEE_LIST", $.feeMgr.cacheFeeList);
			
			var total = 0;
			($.feeMgr.cacheFeeList).each(function(item, index, totalCount){
				total += parseInt(item.get("PAY"));
			});

			data.put("FEE_PAY", $.DataMap("{\"0\":"+total+"}"));
			return data;
		},

		//显示费用列表
		showFeeList : function(feeList){
			//开启调试模式
			if($.feeMgr.testTag){
				return;
			}
			if(!feeList) {
				feeList = $.feeMgr.cacheFeeList;
			}
			//如果页面钝化了，则存入缓存中，不显示费用
			if(!$.feeMgr.activeFlag){
				var data = $.DataMap($.feeMgr.cacheFee);
				data.put("FEE_LIST", feeList);
				$.feeMgr.cacheFee = data.toString();
				return;
			}
			
			if($.os.phone) {
//				$.feeMgr.buildFeeList(feeList);
				if($.ifee) {
					$.ifee.showFeeList(feeList.toString());
				}
			}
			else if(top.showFeeList){
				top.showFeeList(feeList.toString());
			}
		},
		/**
		 * 新增费用,[ELEMENT_ID,PAY]非必传参数
		 * 多业务类型适合必传
		 * feeData[TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID]
		 */
		insertFee : function(feeData) {
			if(!feeData) return;
			
			if(!feeData.containsKey("PAY")){
				feeData.put("PAY", feeData.get("FEE"));		
			}
			
			if(!$.feeMgr.isNumber(feeData)) return;
			
			//更新缓冲区费用列表
			var flag = $.feeMgr.updateCacheFeeList(feeData, "ADD");

			//重新展示费用列表
			if(flag){
				$.feeMgr.showFeeList($.feeMgr.cacheFeeList);
			}	
		},
				
		/**
		 * 删除费用[FEE,PAY,ELEMENT_ID]非必传参数
		 * feeData[TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID]
		 */
		deleteFee : function(feeData) {
			if(!feeData) return;

			if(!feeData.containsKey("PAY")){
				feeData.put("PAY", feeData.get("FEE"));		
			}
			
			if(!$.feeMgr.isNumber(feeData)) return;
						
			//删除缓冲区费用列表
			var flag = $.feeMgr.updateCacheFeeList(feeData, "DEL");
			
			//重新展示费用列表
			if(flag){
				$.feeMgr.showFeeList($.feeMgr.cacheFeeList);
			}	
		},
		
		/**
		 * 删除费用[TRADE_TYPE_CODE,MODE,CODE,ELEMENT_ID]
		 */
		removeFee : function(tradeTypeCode, feeMode, feeTypeCode, elementId) {
			if(!feeMode || !feeTypeCode) return;
			var feeData = $.DataMap();
			feeData.put("TRADE_TYPE_CODE", 	tradeTypeCode);
			feeData.put("MODE", 			feeMode);
			feeData.put("CODE", 			feeTypeCode);
			if(elementId){
				feeData.put("ELEMENT_ID", elementId);
			}

			//删除缓冲区费用列表
			var flag = $.feeMgr.updateCacheFeeList(feeData, "REMOVE");
			
			//重新展示费用列表
			if(flag){
				$.feeMgr.showFeeList($.feeMgr.cacheFeeList);
			}	
		},
		
		/**
		 * 修改费用[TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID]
		 */
		modFee : function(feeData) {
			if(!feeData) return;

			//修改缓冲区费用列表
			var cacheFeeInfo = null;
			if(feeData.get("ELEMENT_ID")){
				cacheFeeInfo = $.feeMgr.findFeeInfo(feeData.get("TRADE_TYPE_CODE"), feeData.get("MODE"), feeData.get("CODE"), feeData.get("ELEMENT_ID"));	
			}else{
				cacheFeeInfo = $.feeMgr.findFeeInfo(feeData.get("TRADE_TYPE_CODE"), feeData.get("MODE"), feeData.get("CODE"));	
			}
			if(cacheFeeInfo){
				$.feeMgr.deleteFee(cacheFeeInfo);
				$.feeMgr.insertFee(feeData);
			}
		},
		
		//更新缓冲区费用列表
		updateCacheFeeList:function(inFeeData, state){
			var flag = true;
			var feeInfo = null;
			var feeData = $.feeMgr.cloneData(inFeeData);
			if(feeData.containsKey("ELEMENT_ID")){
				feeInfo = $.feeMgr.findFeeInfo(feeData.get("TRADE_TYPE_CODE"), feeData.get("MODE"), feeData.get("CODE"), feeData.get("ELEMENT_ID"));	
			}else{
				feeInfo = $.feeMgr.findFeeInfo(feeData.get("TRADE_TYPE_CODE"), feeData.get("MODE"), feeData.get("CODE"));	
			}
			
			if(state == "ADD"){
				if(feeInfo == null){
					$.feeMgr.cacheFeeList.add(feeData);
				}else{
					feeData.put("FEE", parseInt(feeData.get("FEE"))+parseInt(feeInfo.get("FEE")) );
					feeData.put("PAY", parseInt(feeData.get("PAY"))+parseInt(feeInfo.get("PAY")) );
					$.feeMgr.cacheFeeList.remove(feeInfo);
					$.feeMgr.cacheFeeList.add(feeData);
				}
			}else if(state == "DEL"){
				if(feeInfo == null){
					flag = false;
					//alert("删除的费用项不存在！");
				}else{
					//减费用时候，考虑到实缴费用有可能进行过折扣处理，这里以应缴纳费用作为扣减标准，处理完成以后，需要针对这个再次进行折扣
					var fee = parseInt(feeInfo.get("FEE"))-parseInt(feeData.get("FEE"));
					if(fee == 0){
						$.feeMgr.cacheFeeList.remove(feeInfo);
					}else{
						feeData.put("FEE", fee);
						feeData.put("PAY", fee);
						
						$.feeMgr.cacheFeeList.remove(feeInfo);
						$.feeMgr.cacheFeeList.add(feeData);
					}
				}
			}else if(state == "REMOVE"){
				if(feeInfo == null){
					flag = false;
					//alert("删除的费用项不存在！");
				}else{
					$.feeMgr.cacheFeeList.remove(feeInfo);
				}
			}
			feeInfo = null;
			feeData = null;
			return flag;
		},

		//扫描缓冲区，获取费用
		findFeeInfo : function(tradeTypeCode, feeMode, feeTypeCode, elementId) {
			if(!tradeTypeCode || !feeMode || !feeTypeCode) return null;
			var feeInfo = null;
			var data = $.feeMgr.getViewFee();
			if(!data || !data.get("FEE_LIST")) return null;
			$.feeMgr.cacheFeeList = data.get("FEE_LIST");
			
			for(var i=0; i<$.feeMgr.cacheFeeList.length; i++){
				var item = $.feeMgr.cacheFeeList.get(i);
				if(elementId && item.containsKey("ELEMENT_ID")){
					if(item.get("TRADE_TYPE_CODE") == tradeTypeCode 
						&& item.get("MODE") == feeMode
							&& item.get("CODE") == feeTypeCode 
								&& item.get("ELEMENT_ID") == elementId){
						feeInfo = item;
						break;
					}
				}else if(!elementId && !item.containsKey("ELEMENT_ID")){
					if(item.get("TRADE_TYPE_CODE") == tradeTypeCode 
						&& item.get("MODE") == feeMode
							&& item.get("CODE") == feeTypeCode ){
						feeInfo = item;
						break;
					}	
				}	
			}
			return feeInfo;
		},
		
		//清除所有费用
		clearFeeList : function(tradeTypeCode, feeMode){
			//如果传入了tradeTypeCode
			if(tradeTypeCode){
				//过滤指定的费用列表
				var tmpFeeList = $.DatasetList();
				var data = $.feeMgr.getViewFee();
				if(!data || !data.get("FEE_LIST")) return null;
				$.feeMgr.cacheFeeList = data.get("FEE_LIST");	
				($.feeMgr.cacheFeeList).each(function(item, index, totalCount){
					if(item.get("TRADE_TYPE_CODE") == tradeTypeCode){
						if(!feeMode){
							return true;
						}else if(feeMode && feeMode==item.get("MODE")){
							return true;
						}
					}
					tmpFeeList.add(item);
				});
				
				$.feeMgr.cacheFeeList = tmpFeeList;
				$.feeMgr.showFeeList($.feeMgr.cacheFeeList);
				
			}else{
				$.feeMgr.cacheFeeList = $.DatasetList();
				if($.os.phone) {
					if($.ifee)
					{
						return $.ifee.clearFeeList();
					}
				}else if(top.clearFeeList){
					// 清空费用组件
					return top.clearFeeList();
				}
				return true;				
			}
			
		},
		
		isNumber:function(feeData){
			var fee=feeData.get("FEE");
			var pay=feeData.get("PAY");
			if(!$.isNumeric(fee)){
				MessageBox.alert("告警提示","应缴金额格式不正确！");
				return false;
			}
			if(!$.isNumeric(pay)){
				MessageBox.alert("告警提示","实缴金额格式不正确！");
				return false;
			}
			//将带小数点费用直接四舍五入为整形数字，单位为分
			feeData.put("FEE", Math.round(parseFloat(fee)));
			feeData.put("PAY", Math.round(parseFloat(pay)));
			return true;
		},
		
		//深度克隆DataMap数据
		cloneData : function(feeData){
			var feeInfo = $.DataMap();
			if(!feeData) return feeInfo;
			feeData.eachKey(function(key, index, totalCount){
				feeInfo.put(key , feeData.get(key));
			});
			return feeInfo;
		},
		
		//深度克隆DatasetList数据
		cloneDataset : function(feeDataset){
			var feeInfos = $.DatasetList();
			if(!feeDataset) return feeInfos;
			
			var feeInfo = $.DataMap();
			for(var i=0; i<feeInfos.length; i++){
				feeInfo = $.feeMgr.cloneData(feeInfos.get(i));	
				feeInfos.add(feeInfo);
			}
			return feeInfos;
		},
		
		//获取费用台账数据
		getFeeTrade:function(){
			var tradeData = $.DataMap();
			var data = $.feeMgr.getViewFee();
			if(!data || !data.get("FEE_LIST")) return tradeData;
			
			tradeData.put("X_TRADE_FEESUB", $.feeMgr.getFeeList(data.get("FEE_LIST")));			//费用列表
			tradeData.put("X_TRADE_PAYMONEY", $.feeMgr.getPayModeList(data.get("FEE_PAY")));		//支付费用
			
			if(data.get("POS_INFO") && data.get("POS_INFO").length){
				tradeData.put("TRADE_POS_ID", (data.get("POS_INFO")).get("TRADE_POS_ID"));		//pos刷卡流水
			}
			return tradeData;
		},
		//获取费用列表数据
		getFeeList:function(feeSubList){
			var tradeFeeSubList = $.DatasetList(); 
			//如果没有传费用列表信息，则直接获取
			if(!feeSubList){
				var data = $.feeMgr.getViewFee();
				if(!data || !data.get("FEE_LIST")) return tradeFeeSubList;
				feeSubList = data.get("FEE_LIST");
			}
			if(!feeSubList || (feeSubList && feeSubList.length ==0)){
				 return tradeFeeSubList;
			}	
			for (var i = 0; i < feeSubList.length; i++) {
				var item = feeSubList.get(i);
				var tradeTypeCode = item.get("TRADE_TYPE_CODE");	//业务类型编码编码
				var feeMode = item.get("MODE");						//费用类型大类编码
				var feeTypeCode = item.get("CODE");					//费用类型编码
				var oldFee = item.get("FEE");						//应缴金额			
				var fee = item.get("PAY");							//实际缴纳
			 	tradeFeeSubList.add($.feeMgr.getFeeData(tradeTypeCode,feeMode,feeTypeCode,oldFee,fee, item.get("ELEMENT_ID")));
			}
			return tradeFeeSubList;
		},
		//获取支付方式数据
		getPayModeList:function(payMode){
			var tradePayModeList = $.DatasetList(); 
			/**
			 * 如果没有传支付信息，则直接获取
			 */
			if(!payMode){
				var data = $.feeMgr.getViewFee();
				if(!data || !data.get("FEE_LIST")) return tradePayModeList;
				
				payMode = data.get("FEE_PAY");
			}
			
			if(!payMode || (payMode && payMode.length==0)){
				return tradePayModeList;
			}
			var total= payMode.get("TOTAL");
			
			var money = 0;
			payMode.eachKey(function(key, idx, totalCount){
				if(key == "TOTAL" || key == "CHECK_INFO"){
					return true;
				}
				//排除等于0的支付方式
				if(parseInt(payMode.get(key))!= 0){
					money = $.format.number(payMode.get(key), "0");
					var payMoney = null;
					if(key == "-1"){
						payMoney = $.feeMgr.getPayModeData(key, money, payMode.get("CHECK_INFO"));
					}else{
						payMoney = $.feeMgr.getPayModeData(key, money);
					}
					tradePayModeList.add(payMoney);
				}
			});

			return tradePayModeList;
		},
		/**
		* 获取总费用,默认返回实缴费用
		* 如果flag=0，返回应缴费用
		* flag=1或者不填写返回实缴费用
		*/
		getTotalFee:function(flag){
			var totalFee = 0, totalPay = 0; 
			var data = $.feeMgr.getViewFee();
			if(!data || !data.get("FEE_LIST")){
				return (flag==0)?totalFee:totalPay;
			}
			var tradeFeeList = data.get("FEE_LIST");
			tradeFeeList.each(function(item, idx, totalCount){
				totalFee += parseInt(item.get("FEE"));
				totalPay += parseInt(item.get("PAY"));
			});	
			return (flag==0)?totalFee:totalPay;
		},
		//获取POS刷卡流水号
		getTradePosId:function(){
			var tradePosId = ""; 
			var data = $.feeMgr.getViewFee();
			if(!data || !data.get("FEE_LIST")) return tradePosId;
			
			if(data.get("POS_INFO") && data.get("POS_INFO").length){
				tradePosId = (data.get("POS_INFO")).get("TRADE_POS_ID");
			}
			return 	tradePosId;
		},
		//返回费用列表项
		getFeeData:function(tradeTypeCode, feeMode, feeTypeCode, oldFee, fee, elementId){
			var data = $.DataMap();
			data.put("TRADE_TYPE_CODE",		tradeTypeCode);
			data.put("FEE_TYPE_CODE",		feeTypeCode);
			data.put("FEE",					fee);	
			data.put("OLDFEE",				oldFee);
			data.put("FEE_MODE",			feeMode);
			data.put("ELEMENT_ID",			elementId);
			if(elementId){
				data.put("DISCNT_GIFT_ID",	elementId);
			}
			return data;
		},
		//返回费用支付项
		getPayModeData:function(payModeCode, money, checkData){		
			var data = $.DataMap();
			data.put("PAY_MONEY_CODE", payModeCode);
			data.put("MONEY", money);
			
			//如果是支票，则复制数据拼接支票台账
			if(payModeCode == "-1" && (checkData && checkData.length>0)){
				checkData.eachKey(function(key, index, totalCount){
					data.put(key, checkData.get(key));
				});
			}
			return data;	
		},
		/**
		 * 设置POS参数
		 * tradeTypeCode 业务类型编码
		 * serialNumber  服务号码
		 * eparchCode    地州编码，POS刷卡记录登记地州
		 * userId  		 用户ID，非必传参数,对于开户情况，此字段设置为null或者直接用服务号码替换
		 */
		setPosParam:function(tradeTypeCode, serialNumber, eparchCode, userId){
			if(!tradeTypeCode || !serialNumber){
				MessageBox.alert("信息提示", "设置POS参数：业务类型编码,服务号码为必传参数!");
				return ;
			}
			var posInfo = $.DataMap();
			posInfo.put("TRADE_TYPE_CODE", 	tradeTypeCode);
			posInfo.put("SERIAL_NUMBER", 	serialNumber);
			if(eparchCode){
				posInfo.put("FEE_EPARCHY_CODE", eparchCode);
			}
			if(userId && userId != ""){
				posInfo.put("USER_ID", 	userId);
			}			
			if(top.setPosParam){
				top.setPosParam(posInfo.toString());
			}
		},
		/**
		 * 情况费用
		 * 该方法主要提供给组件使用
		 */
		clearFee:function(){
			if(top.clearFee){
				top.clearFee();
			}
		},
		/**
		 * 校验费用
		 */
		checkFee:function(okFn, cancelFn){
			if($.os.phone) {
				if($.ifee) {
					var msg = $.ifee.checkFee();
					if(msg == "1"){
						return false;		//校验不通过	
					}else if(msg == "0"){
						return true;
					}
					MessageBox.confirm("费用提示", "请确认费用明细"+msg, function(btn){
						if(btn == "ok"){
							//确认提交，组装费用数据
							$.feeMgr.setFeeSubmitParam();
							if(okFn) okFn();
						}else{
							if(cancelFn) cancelFn();
						}
					});
					return false;
				}else {
					return true;
				}
				
			}else {
				//如果没有确认接口，则忽略，则返回
				if(!top.checkFee){
					//没有费用外框，走调试模式
					$.feeMgr.setFeeSubmitParam();
					return true;
				}
				var msg = top.checkFee();
				if(msg == "1"){
					return false;		//校验不通过	
				}else if(msg == "0"){
					return true;
				}
				MessageBox.confirm("费用提示", "请确认费用明细"+msg, function(btn){
					if(btn == "ok"){
						//确认提交，组装费用数据
						$.feeMgr.setFeeSubmitParam();
						if(okFn) okFn();
					}else{
						if(cancelFn) cancelFn();
					}
				});
				return false;
			}
		},
		
		//拼接费用台账数据
		setFeeSubmitParam:function(){
			var feeInfo = $.feeMgr.getFeeTrade();
			if(feeInfo.containsKey("X_TRADE_FEESUB") 
					&& feeInfo.get("X_TRADE_FEESUB").length){
				var payMoneys = feeInfo.get("X_TRADE_PAYMONEY");
				var posFee=0;
				payMoneys.each(function(item, index, totalCount){
					if(item.get("PAY_MONEY_CODE") == "m"){
						posFee = parseInt(item.get("MONEY"));
					}
				});
				$.cssubmit.setParam("X_TRADE_FEESUB",(feeInfo.get("X_TRADE_FEESUB")).toString());
				$.cssubmit.setParam("X_TRADE_PAYMONEY",payMoneys.toString());
				if(posFee>0 && feeInfo.containsKey("TRADE_POS_ID")){
					$.cssubmit.setParam("TRADE_POS_ID",feeInfo.get("TRADE_POS_ID"));
				}
			}
		},
		/**
		 * 情况费用
		 * 该方法主要提供给组件使用
		 */
		switchWindow:function(flag){
			$.feeMgr.tabFalg = flag;
		},
		
		buildFeeList:function(feeList){
			if(!$.ifee) {
				return;
			}
			if(!$.ifee.FEE_CONFIG){
				MessageBox.alert("告警提示", "费用配置数据加载异常！");
				return;
			}
			
			if(!feeList) {
				feeList = $.feeMgr.cacheFeeList;
			}
			
			//设置新的Fee_List数据
			$.ifee.FEE_LIST=feeList;
			
			//如果为空，
			if(!feeList || !feeList.length){
				$.feeMgr.hideFeeList();
				return;
			}
			
			var tradeTypeCode,mode,code,fee,pay;
			var modeName,modeCfg;
			var hasPriv=false;
			var feeArray=[];
			for(var i=0; i<feeList.length; i++){
				var feeItem = feeList.get(i);
				tradeTypeCode = feeItem.get("TRADE_TYPE_CODE");
				mode=feeItem.get("MODE");
				code=feeItem.get("CODE");
				fee= parseInt(feeItem.get("FEE"))/100;		//前台业务送入的费用数据已经校验且全部转换成以分为单位的整数
				//加载时如果没有实缴数据，则put实缴数据
				if(!feeItem.containsKey("PAY")){
					feeItem.put("PAY", feeItem.get("FEE"));
				}
				pay = parseInt(feeItem.get("PAY"))/100;

				modeName=$.ifee.FEE_CONFIG.FEE_MODES[mode];
				modeCfg=$.ifee.FEE_CONFIG[mode];
				if(!modeCfg || !modeCfg[code]){
					MessageBox.alert("告警提示", modeName+"费用类型编码[ "+ code +" ]对应数据不存在！");
					return;
				}
				
				feeArray.push('<li>');
				feeArray.push('<div class="main">'+(modeCfg?modeCfg[code].NAME:'')+'</div>');
				feeArray.push('<div class="side" idx="'+i+'">'+fee+'</div>');
				feeArray.push('<div class="side"><span class="e_tag e_tag-green">'+modeName+'</span></div>');
				feeArray.push('</li>');
				feeArray.push(' ');
			}
			$("#FEE_ITEM_LIST").html(feeArray.join(""));
        	$("#FEE_TOTAL").html($.feeMgr.getTotalPayFee(feeList)/100);
			$("#FEE_LIST_DIV").css("display","");
			
			$.ifee.initPay();
		},
		
		//统计总支付费用
		getTotalPayFee:function(feeList){
			var totalFee = 0;
			if(feeList && feeList.length){
				var pay;
				for(var i=0; i<feeList.length; i++){
					var feeItem = feeList.get(i);
					pay = feeItem.get("PAY");
					if(!$.isNumeric(pay)) pay = "0";
					totalFee+= parseInt(pay);
				}
			}
			return totalFee;
		},
		
		hideFeeList:function(){
			$("#FEE_LIST_DIV").css("display","none");
			$("#FEE_PLACEHOLDER").height("0");
		}
	}});
	
})(Wade);

//tab窗口激活时触发
function onActive(){ 
	if(top.renewFee){
		// 恢复费用数据
		top.renewFee($.feeMgr.cacheFee);
		//费用恢复完以后，改变激活状态
		$.feeMgr.activeFlag = true;
	}
}

//tab窗口变为非当前窗口时触发
function onUnActive(){
	if(top.clearFee && top.getFee){
		// 取当前费用信息
		$.feeMgr.cacheFee = top.getFee();
		//费用缓冲好以后，修改为非激活状态
		$.feeMgr.activeFlag = false;
		// 清空费用组件
		top.clearFee();
	}
}

//关闭tab窗口时触发
function onClose(current){ 
	if(top.clearFee && current==true){
		// 清空费用组件
		top.clearFee();
	}
	$.feeMgr.cacheFee = null;
	$.feeMgr.cacheFeeList = null;
}