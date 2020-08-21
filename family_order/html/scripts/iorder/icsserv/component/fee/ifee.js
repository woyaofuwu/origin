(function($){
	$.extend({ifee:{
		clazz:"com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.fee.IFeeHandler",
		POS_URL:"/order/order?service=page/components.fee.PosTrade&listener=onInitTrade",
		FEE_CONFIG:null,  		//费用配置数据
		FEE_LIST:null,   		//费用列表数据
		FEE_PAY: null,   		//支付数据
		POS_CONFIG: null,   	//POS配置数据
		POS_INFO: null,   		//POS缴费信息
		PAY_LIMIT: false,   	//是否只支持一种支付方式

		CASH_CODE:"0",	//现金	
		CHECK_CODE:"-1",	//支票
		POS_CODE:"m",	//银联电子商务POS

		init:function(){
			//如果能取到NGBOSS外框，则不需要启用新费用组件，影响效率
			if(!$.os.phone) 
			{
				return;
			}
			
			//为避免影响打开主界面速度，费用相关数据异步加载
			//加载费用配置数据
			$.httphandler.get($.ifee.clazz, "loadFeeConfig", null, 
				function(jsonData){
					if(jsonData.data && !$.isEmptyObject(jsonData.data)){
						$.ifee.FEE_CONFIG=jsonData.data;
						//创建费用支付展示 
//						$.ifee.buildPayMode($.ifee.FEE_CONFIG.PAY_MODES);
					}
				},
				function(code, info, detail){
					MessageBox.error("错误提示","费用配置加载失败！",null, null, info, detail);
				},{
					dataType:"json",
					simple:true
			});
			
			//获取组件支付限制开关
			if($("#onlyPayMode").val()=="true") $.ifee.PAY_LIMIT = true;
		},
		/**
		 * 获取费用修改权限
		 * 同步方式获取费用权限
		 */
		getModFeePriv:function(privCode){
			if(!privCode) return false;
			if($.ifee.FEE_CONFIG[privCode]){
				return $.ifee.FEE_CONFIG[privCode];
			}
			var priv = false;
			$.httphandler.get($.ifee.clazz, "getModFeePriv", "&PRIV_CODE="+privCode, 
				function(jsonData){
					priv = jsonData.data["FEE_SPECPRIV"];
					$.ifee.FEE_CONFIG[privCode] = priv;
				},
				function(code, info, detail){
					priv = false;
				},{
					async:false,
					dataType:"json",
					simple:true
			});
			return priv;
		},
		//判断是否有修改费用权限
		hasModFeePriv:function(tradeTypeCode, mode, code){
			var hasPriv = false;
			
			if (mode=="2" && code=="521") { //光猫不能减免。
				return false;
			}
			
			//营销活动费用修改权限 
			if(tradeTypeCode=="240" && $.ifee.FEE_CONFIG.SYSCHANGEACTIVEFEE){
				return true;
			}
			
			if ($.ifee.FEE_CONFIG.SUPER && !(mode=="2" && code=="62")) {
				return true;
			}
			/**
			 * 宽带开户要单独获取权限
			 * 613        FTTH宽带开户 
			 * 630        校园宽带开户   
			 * 600        GPON宽带开户
			 * 612        ADSL宽带开户
			 * 611        GPON宽带子账号开户
			 */
			if ($.ifee.FEE_CONFIG.KD001 && (tradeTypeCode=="600" || tradeTypeCode=="611"
					|| tradeTypeCode=="612" || tradeTypeCode=="613" || tradeTypeCode=="630")) {
				return true;
			}
			var modeCfg=$.ifee.FEE_CONFIG[mode];
			if(mode == "0" || mode == "1"){
				hasPriv= modeCfg.PRIV || (modeCfg[code] && modeCfg[code].PRIV);
			}else if(mode == "2"){
				hasPriv= modeCfg.PRIV && code!="62" || (modeCfg[code] && modeCfg[code].PRIV);
			}
			return hasPriv;
		},
		
		show:function(){
			$.sidebar.showFeeInfo();
			$("#fee_container,#fee_content").css("display","");
			$("#fee_title").removeClass("title-unfold").addClass("title-fold");
		},
		hide:function(){
			$("#fee_title").removeClass("title-fold").addClass("title-unfold");
			$("#fee_content,#pay_content").css("display","none");
		},
		disabledPos:function(flag){
			var className="ok";
			var state="0";
			if(flag){
				className="cancel";
				state="1";
			}
			$("#POS_PAY_BTN").attr("className", className);
			$("#POS_PAY_BTN").attr("state", state);
			//$("#fee_pay_list input[name=PAYMONEY][pay_mode="+$.ifee.POS_CODE+"]").attr("disabled", flag);
		},
		/**
		 * ***************费用列表及支付处理***********************
		 */
		buildFeeList:function(feeList){
			if(!$.ifee.FEE_CONFIG){
				MessageBox.alert("告警提示", "费用配置数据未加载");
				return;
			}
			//设置新的Fee_List数据
			$.ifee.FEE_LIST=feeList;
			
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
		//创建支付列表
		buildPayMode:function(payModes){
			if(!payModes || !payModes.length){
				return ;
			}
			var payModeStr = [],advancePayStr = [];
			//默认显示现金和POS付款方式
			for(var i=0; i<payModes.length; i++){
				if(payModes[i].DATA_ID==$.ifee.CASH_CODE || payModes[i].DATA_ID==$.ifee.POS_CODE || payModes[i].DATA_ID=="1"){
					advancePayStr.push($.ifee.getPayModeHtml("", payModes[i].DATA_NAME, payModes[i].DATA_ID));						
				}else{
					payModeStr.push($.ifee.getPayModeHtml("hide", payModes[i].DATA_NAME, payModes[i].DATA_ID));
				}
			}
			$("#fee_pay_list").empty();
			$("#fee_pay_list").append(advancePayStr.join(""));
			$("#fee_pay_list").append(payModeStr.join(""));
			//绑定事件
			var inputs=$("#fee_pay_list input[name=PAYMONEY]");
			inputs.bind("focus",$.ifee.events.onPayInputFocus);
			inputs.bind("blur",$.ifee.events.onPayInputBlur);
			inputs.bind("click",$.ifee.events.onPayInputClick);		
			
			$("#fee_pay_list a[name=PAYMONY_LI]").bind("click",$.ifee.events.onPayModeLiClick);
			$("#POS_PAY_BTN").bind("click",$.ifee.events.onPosPayClick);
			
			payModeStr = null;
			advancePayStr=null;
		},
		getPayModeHtml:function(cssName, payName, payCode){
			var str = [];
			str.push('<li class="'+cssName+'">');
			str.push('<span class="label">'+payName+'：</span>');
			str.push('<a class="switch" href="#nogo" name="PAYMONY_LI"></a>');
			if(payCode == $.ifee.POS_CODE){
				str.push('<span class="elements">');
				str.push('<button id="POS_PAY_BTN" state="0" class="ok"></button>');
			}
			str.push('<span class="input"><span><input name="PAYMONEY" pay_mode="'+payCode+'" type="text" maxlength="10" value="0"/></span></span>');
			if(payCode == $.ifee.POS_CODE){
				str.push('</span>');
			}
			str.push('</li>');
			return str.join("");
		},
		//统计总支付费用
		getTotalPayFee:function(){
			var totalFee = 0;
			if($.ifee.FEE_LIST && $.ifee.FEE_LIST.length){
				var pay;
				for(var i=0; i<$.ifee.FEE_LIST.length; i++){
					var feeItem = $.ifee.FEE_LIST.get(i);
					pay = feeItem.get("PAY");
					if(!$.isNumeric(pay)) pay = "0";
					totalFee+= parseInt(pay);
				}
			}
			return totalFee;
		},
		/**
		 * 初始化支付信息
		 * 第一次送入支付方式金额费用时候
		 */
		initPay:function(){
			var total=$.ifee.getTotalPayFee();
			//创建支付列表
			$.ifee.FEE_PAY= $.DataMap();
			
			/**
			 * 以下为保存支付方式金额数据
			 */
			$.ifee.FEE_PAY.put("TOTAL",total);	//总金额
			
			//初始化新的支付数据
			var payModes = $.ifee.FEE_CONFIG.PAY_MODES;
			var tmpFee;
			for(var i=0; i<payModes.length; i++){
				tmpFee=0;
				if(payModes[i].DATA_ID == $.ifee.CASH_CODE){
					tmpFee=total;
				}
				$.ifee.FEE_PAY.put(payModes[i].DATA_ID, tmpFee);		//初始化支付方式金额
			}
		},
		//更新支付方式金额
		updatePay:function(){
			//如果没有费用列表数据，则直接返回
			if(!$.ifee.FEE_LIST || !$.ifee.FEE_LIST.length){
				return;
			}	
			var totalFee=$.ifee.getTotalPayFee();
			var oldTotalFee= parseInt($.ifee.FEE_PAY.get("TOTAL"));		
			
			//更新支付数据,如果新总金额等于原来总金额，则直接返回
			if(totalFee == oldTotalFee){
				return;
			}
			var balance=totalFee-oldTotalFee, fee=0;
			$.ifee.FEE_PAY.put("TOTAL",totalFee);		//更新总支付费用
			$("#span_fee_total").text(totalFee/100);	//更新展示的总支付费用
			
			var upFlag = false;
			//当且限制一种支付方式，且没有POS刷卡时候，将所有差额加到有支付的那条记录上
			if($.ifee.PAY_LIMIT && $("#POS_PAY_BTN").attr("state")=="0"){
				$.ifee.FEE_PAY.eachKey(function(key, index, totalCount){
					if(key == "TOTAL" || key=="CHECK_INFO") return true;
					fee = parseInt($.ifee.FEE_PAY.get(key));
					if(fee != 0){
						$.ifee.FEE_PAY.put(key, fee+balance);
						$("#fee_pay_list input[name=PAYMONEY][pay_mode="+key+"]").val((fee+balance)/100);
						upFlag = true;
						return false;
					}
				});
				//如果没有涉及到费用更新，则需要重新计算设置支付费用，默认加到现金支付方式
				if(!upFlag){
					$.ifee.FEE_PAY.put($.ifee.CASH_CODE, totalFee);
					$("#fee_pay_list input[name=PAYMONEY][pay_mode="+$.ifee.CASH_CODE+"]").val(totalFee/100);
				}
				return;
			}

			//如果支持多种支付方式，则按照多重支付方式进行
			var curFee, isBreak=false;
			$("#fee_pay_list [name=PAYMONEY]").each(function(){
				isBreak=false;
				var key = $(this).attr("pay_mode");
				//如果是POS机缴费，则不能修改，直接跳过
				if(key==$.ifee.POS_CODE && $.ifee.POS_INFO
						&& $("#POS_PAY_BTN").attr("state")!="0") {
					return true;
				}
				curFee= parseInt($.ifee.FEE_PAY.get(key));
				if(balance>0){ //新合计金额大于原合计金额
					curFee=curFee+balance;
					balance = 0;
					isBreak = true;
				}else if(balance<0){  //新合计金额小于原合计金额
					curFee=curFee-Math.abs(balance); //依次递减
					if(curFee<0){
						balance=curFee;			//重新计算剩余差额
						curFee=0;				//设置当前费用为0
					}else{
						balance=0;
						isBreak = true;
					}
				}
				//更新计算的新数据
				$.ifee.FEE_PAY.put(key,curFee);
				$(this).val(curFee/100);
				return !isBreak;
			});
			//如果最后还不够扣减，说明已经进行了POS缴费或清退押金情况，则设置现金额度为负数
			if(balance<0){
				curFee= parseInt($.ifee.FEE_PAY.get($.ifee.CASH_CODE));
				curFee = curFee-Math.abs(balance);		//计算负数
				$.ifee.FEE_PAY.put($.ifee.CASH_CODE, curFee);
				$("#fee_pay_list input[name=PAYMONEY][pay_mode="+$.ifee.CASH_CODE+"]").val(curFee/100);
			}
		},
		//费用列表修改费用事件
		setFeeInputVal:function(feeFieldObj){
			//获取费用索引值
			var idx=feeFieldObj.attr("idx");
			if(!/^\d+$/.test(idx)) return;
			
			var oldFee = parseInt($.ifee.FEE_LIST.get(idx,"PAY"))/100;
			var modFeeStr=$.trim(feeFieldObj.val());
			if(!modFeeStr || modFeeStr=="")	modFeeStr="0";
			
			//校验数据格式,返回数值类型数据
			var modFee=$.ifee.valid.verifyMoneyVal(modFeeStr);
			if(false===modFee){
				//校验不通过，恢复数据
				feeFieldObj.val(oldFee);
				return;
			}
			//校验业务逻辑
			modFee=$.ifee.valid.verifyFeeVal(idx,modFee);
			if(false===modFee){
				//校验不通过，恢复数据
				feeFieldObj.val(oldFee);
				return;
			}
			
			//更新数据，并展示费用列表项金额
			$.ifee.FEE_LIST.get(idx).put("PAY", parseFloat(modFee)*100);
			feeFieldObj.val(modFee);
			//更新支付方式
			$.ifee.updatePay();
		},
		//支付方式修改事件
		setPayInputVal:function(payFieldObj){
			//如果费用信息不存在则终止
			if(!$.ifee.FEE_LIST || !$.ifee.FEE_LIST.length 
					|| !$.ifee.FEE_PAY || !$.ifee.FEE_PAY.length) return;	
					
			var payMode=payFieldObj.attr("pay_mode");
			var oldFee = parseInt($.ifee.FEE_PAY.get(payMode))/100;
			var modFeeStr=$.trim(payFieldObj.val());
			if(!modFeeStr || modFeeStr=="")	modFeeStr="0";
	
			//校验数据格式,返回数据已经转换成数值类型
			var modFee=$.ifee.valid.verifyMoneyVal(modFeeStr);
			if(false===modFee){
				//校验不通过，恢复数据
				payFieldObj.val(oldFee);
				return;
			}
			//校验业务逻辑
			modFee=$.ifee.valid.verifyPayVal(payMode,modFee);
			if(false===modFee){
				//校验不通过，恢复数据
				payFieldObj.val(oldFee);
				return;
			}
							
			var curFee, isBreak=false;
			//初始化遍历标识
			var balance=modFee*100-oldFee*100;
			$("#fee_pay_list input[name=PAYMONEY]").each(function(){
				isBreak=false;
				var tmpPayMode = $(this).attr("pay_mode");
				if(tmpPayMode != payMode ){
					//如果是POS机缴费，则不能修改，直接跳过
					if(tmpPayMode==$.ifee.POS_CODE && $.ifee.POS_INFO 
						&& $("#POS_PAY_BTN").attr("state")!="0") {
						 return true;	
					}
					curFee= parseInt($.ifee.FEE_PAY.get(tmpPayMode));
					if(balance>0){ //新值大于原值，其它项相应减少
						curFee=curFee-balance;
						if(curFee<0){ //如果不够减少的额度，则设置为0
							balance=Math.abs(curFee);
							curFee=0;
						}else{
							balance=0;
							isBreak=true; //否则跳出
						}
					}else{ //新值小于原值,其它项相应增加
						curFee=curFee+Math.abs(balance);
						balance=0;
						isBreak=true; //跳出循环
					}
					
					//更新数据
					$.ifee.FEE_PAY.put(tmpPayMode, curFee);
					$(this).val(curFee/100);
				}
				return !isBreak;
			});
			//更新当前项数据
			$.ifee.FEE_PAY.put(payMode,modFee*100);
			payFieldObj.val(modFee);

			//如果最后还不够扣减，说明已经进行了POS缴费，则设置现金额度为负数
			if(balance>0){
				curFee= parseInt($.ifee.FEE_PAY.get($.ifee.CASH_CODE));
				curFee = curFee-balance;
				$.ifee.FEE_PAY.put($.ifee.CASH_CODE, curFee);
				$("#fee_pay_list input[name=PAYMONEY][pay_mode="+$.ifee.CASH_CODE+"]").val(curFee/100);
			}
			if(payMode == $.ifee.CHECK_CODE){
				$.ifee.showCheckEditTip();
			}
		},
		//设置是否仅一种支付触发事件
		setOnlyPayInputVal:function(payFieldObj){
			if(!$.ifee.FEE_LIST || !$.ifee.FEE_LIST.length 
					|| !$.ifee.FEE_PAY || !$.ifee.FEE_PAY.length) return;
			//如果支持多种支付方式，或者已经刷卡，则不触发动作
			if(!$.ifee.PAY_LIMIT || $("#POS_PAY_BTN").attr("state")!="0"){
				return;
			}
			var payMode=payFieldObj.attr("pay_mode");
			var total=parseInt($.ifee.FEE_PAY.get("TOTAL"));
			var payMoney=0;
			$.ifee.FEE_PAY.eachKey(function(key){
				if(key == "TOTAL" || key=="CHECK_INFO") return true;
				if(payMode == key){	//如果支付方式跟当前选定的相同，则将总支付金额全部转入该支付方式
					payMoney=total;
				}else{
					payMoney=0;
				}
				$.ifee.FEE_PAY.put(key, payMoney);
			});
			
			$("#fee_pay_list input[name=PAYMONEY]").val("0");
			$("#fee_pay_list input[name=PAYMONEY][pay_mode="+payMode+"]").val(total/100);
		},
		/**
		 * ***************支票***********************
		 */
		showCheckEditTip:function(){
			if($.ifee.FEE_PAY){
				var check=$.ifee.FEE_PAY.get($.ifee.CHECK_CODE);
				if(check>0){
					$.ifee.showCheckEdit();
				}
			}
		},
		showCheckEdit:function(){
			$("#fee_check_edit").css("display","");
		},
		hideCheckEdit:function(){
			$("#fee_check_edit").css("display","none");
		},
		updateCheckData:function(){
			var flag = $.validate.queryAll("fee_check_edit_area");
			if(!flag){
				return ;
			}
			var data= $.DataMap();
			data.put("CHECK_CARD_NO", $("#fee_check_edit_area input[name=CHECK_CARD_NO]").val());
			data.put("CHECK_CARD_NAME", $("#fee_check_edit_area input[name=CHECK_CARD_NAME]").val());
			data.put("CHECK_BANK_CODE", $("#fee_check_edit_area select[name=BANK]").val());
			data.put("CHECK_LIMIT", $("#fee_check_edit_area input[name=CHECK_LIMIT]").val());
			
			if($.ifee.FEE_PAY.containsKey("CHECK_INFO")){
				$.ifee.FEE_PAY.get("CHECK_INFO").clear();
				$.ifee.FEE_PAY.removeKey("CHECK_INFO");
			}
			$.ifee.FEE_PAY.put("CHECK_INFO", data);
			$.ifee.hideCheckEdit();
		},
		//支票弹出框-创建银行选择框数据
		buildBankSelect:function(selector,data){
			if(selector){
				var sel=$(selector);
				sel.empty();
				sel.append("<option value=''>----请选择----</option>");
					
				if(data && data.length){
					for(var i=0;i<data.length;i++){
						sel.append("<option value='" + data[i].BANK_CODE + "'>" + data[i].BANK + "</option>");
					}
				}
			}
		},
		/**
		 * ***************费用名字、数据校验、绑定事件***********************
		 */
					//获取费用大类名称
		getFeeModeDesc:function(mode){
			var result = "未知费用类型";
			if($.ifee.FEE_CONFIG.FEE_MODES && $.ifee.FEE_CONFIG.FEE_MODES[mode]){
				result = $.ifee.FEE_CONFIG.FEE_MODES[mode];
			}
			return result;
		},
		//获取费用小类名称
		getFeeTypeCodeDesc:function(mode, code){
			var result = "未知费用类型";
			if($.ifee.FEE_CONFIG[mode] && $.ifee.FEE_CONFIG[mode][code]){
				result = $.ifee.FEE_CONFIG[mode][code].NAME;
			}
			return result;
		},
		valid:{
			//校验费用数据
			verifyMoneyVal:function(val){
				/**
				if(!/^[0-9]+(.[0-9]+)?$/.test(val)){
					alert("金额输入不合法！");
					return false;
				}*/
				if(!$.isNumeric(val)){
					MessageBox.alert("告警提示","输入金额不合法！");
					return false;
				}
				//进行四舍五入处理
				var fee = parseFloat(val)*100;
				return Math.round(fee)/100;
			},
			//校验费用项业务逻辑
			verifyFeeVal:function(idx,val){
				var feeItem=$.ifee.FEE_LIST.get(idx);
				var payFee= parseInt(feeItem.get(idx,"PAY"));
				//如果没有发生变化，则返回
				if(payFee==val*100){
					return false;
				}
				//如果费用不存在，或数据异常则返回
				if(!feeItem || !feeItem.length) return false; 
				
				//转换成元操作
				var fee= parseInt(feeItem.get("FEE"))/100;		
				
				//只有FEE_MODE为0(营业费)时校验业务规则
				if(val>fee && feeItem.get("MODE")=="0"){
					MessageBox.alert("告警提示","营业费用不能超过应缴金额!");
					return false;
				}
				//获取费用配置数据
				var feeCfg=$.ifee.FEE_CONFIG["0"][feeItem.get("CODE")];
				if(feeCfg){
					var preMoney=parseInt(feeCfg.PREMONEY);
					if("0"==feeCfg.PRE_TAG && (fee-val)>(preMoney/100)){
						MessageBox.alert("告警提示","优惠金额过大！最多只能优惠金额[ " + (preMoney/100) + " ]元");
						return false;
					}else if("1"==feeCfg.PRE_TAG && ((fee-val)/fee)*100>preMoney){
						MessageBox.alert("告警提示","优惠比例过大！最大优惠比例为[ " + preMoney + "% ]");
						return false;
					}
				}
				return val;
			},
			//校验支付方式业务逻辑
			verifyPayVal:function(payMode,val){
				if(!$.ifee.FEE_PAY || !$.ifee.FEE_PAY.length) return false;
				//如果等于输入值，则直接返回，不做处理
				var payFee=parseInt($.ifee.FEE_PAY.get(payMode));
				if(payFee==val*100){
					return false;
				}
				//校验POS已经刷卡，则必须先取消前一笔交易后才能修改金额
				if(payMode == $.ifee.POS_CODE && $("#POS_PAY_BTN").attr("state")!="0" ){
					MessageBox.alert("告警提示", "POS机已经刷卡，必须撤销以后才能修改支付金额！");
					return false;
				}
				
				//如果只有一种支付方式，则必须等于之前金额
				if($.ifee.PAY_LIMIT && payFee!=val*100){
					MessageBox.alert("告警提示", "仅支持一种支付方式，支付金额必须等于总金额！");
					return false;
				}
				
				var total=parseInt($.ifee.FEE_PAY.get("TOTAL"));
				if(total<val*100){
					MessageBox.alert("告警提示","输入的金额不能大于总金额!");
					return false;
				}
				return val;
			}
		},
		events:{
			//费用区域展示隐藏
			onFeePartClick:function(){
				if($(this).hasClass("title-unfold")){
					$.ifee.show();
				}else{
					$.ifee.hide();
				}
			},
			//支付方式展开开关
			onPayModeSwitchClick:function(){
				var payEl = $(this).parent().parent().parent().parent();
				if ($(this).attr("className") == 'show') {
					$(this).attr("className", "hide");
					payEl.attr("className", "pay pay-showAll");
				}
				else if ($(this).attr("className") == 'hide') {
					$(this).attr("className", "show");
					payEl.attr("className", "pay");
				}
			},
			onFeeInputFocus:function(){
				$(this).attr("className", "focus");
				$(this).select();
			},
			onFeeInputBlur:function(){
				$(this).attr("className", "");
				$.ifee.setFeeInputVal($(this));
			},
			onPayInputFocus:function(){
				$(this).parent().parent().attr("className", "input input-focus");
				$(this).select();
			},
			onPayInputClick:function(){
				$.ifee.setOnlyPayInputVal($(this));
			},
			onPayInputBlur:function(){
				$(this).parent().parent().attr("className", "input");
				
				$.ifee.setPayInputVal($(this));
			},
			onPayModeLiClick:function(){
				var payModeLi = $(this).parent();
				if (payModeLi.attr("className") == 'hide') {
					payModeLi.attr("className", "");
				}
				else {
					payModeLi.attr("className", "hide");
				}
			},
			onPosPayClick:function(){
				//如果没有费用信息，则终止
				if(!$.ifee.FEE_LIST || !$.ifee.FEE_LIST.length 
				|| !$.ifee.FEE_PAY || !$.ifee.FEE_PAY.length) return;
				/**var state = $(this).attr("state");
				if(state=="0"){
					$.ifee.posPayFee();
				}else{
					$.ifee.posCancelFee();
				}*/
				$.ifee.posPayFee();
			},
			onShowCheckEditBtnClick:function(){
				$.ifee.showCheckEdit();
			},
			onSuperBankChange:function(){
				var bankCode=$(this).val();
				if(bankCode){
					//$.beginLoading("正在加载银行列表");
					var sel=$("#fee_check_edit [name=BANK]");
					sel.empty();
					sel.append("<option>加载银行列表...</option>");
					//加载银行数据
					$.httphandler.get($.ifee.clazz, "loadFeeBankList", "&SUPER_BANK_CODE=" + bankCode, function(jsonData){
						//$.endLoading();
						$.ifee.buildBankSelect("#fee_check_edit [name=BANK]",jsonData.data);
					},
					function(code, info, detail){
						$.ifee.buildBankSelect("#fee_check_edit [name=BANK]");
						MessageBox.error("错误提示","银行列表加载失败！",null, null, info, detail);
					},
					{
						dataType:"json",
						simple:true
					});
				}
			}
		},
		/**
		 * ********************费用外框与业务费用交互****************************
		 * 
		 * 展示费用信息
		 */
		showFeeList:function(dataStr){
			if(!dataStr || !$.isString(dataStr)){
				MessageBox.alert("告警提示","费用项数据不正确");
				return false;
			}
			var feeList = $.DatasetList(dataStr);
			if(!feeList || !feeList.length){
				$.ifee.clearFeeList();
				return true;
			}
			//生成费用列表
			$.ifee.buildFeeList(feeList);
			//处理费用支付
//			if($.ifee.FEE_PAY && $.ifee.FEE_PAY.length){
//				$.ifee.updatePay();
//			}else{
//				$.ifee.initPay();
//			}
//			
//			if($.ifee.FEE_LIST && $.ifee.FEE_LIST.length){
//				$.ifee.show();					
//			}else{
//				$.ifee.hide();					
//			}
			
			return true;
		},
		//恢复费用信息
		renewFee:function(dataStr){
			if(dataStr && $.isString(dataStr)){
				var data=$.DataMap(dataStr);
				var feelistData=data.get("FEE_LIST");
				//如果没有费用项，直接返回
				if(!feelistData || !feelistData.length){
					$.ifee.hide();
					return;
				}
				//生成列表				
				$.ifee.buildFeeList(feelistData);
				
				//恢复各类数据				
				$.ifee.FEE_PAY = data.get("FEE_PAY");
				if($.ifee.FEE_PAY && $.ifee.FEE_PAY.length){
					var total = $.ifee.FEE_PAY.get("TOTAL");
					if(!total || !$.isNumeric(total)) total = "0";
					$("#span_fee_total").text(parseInt(total)/100);
					
					var fee;
					$.ifee.FEE_PAY.eachKey(function(key, index, totalCount){
						if(key == "TOTAL" || key=="CHECK_INFO") return true;
						fee = $.ifee.FEE_PAY.get(key);
						if(!$.isNumeric(fee)) fee = "0";
						$("#fee_pay_list input[pay_mode="+key+"]").val(parseInt(fee)/100);
					});
					
					//重新计算费用，更新支付列表
					$.ifee.updatePay();
				}else{
					$.ifee.initPay();
				}

				if($.ifee.FEE_LIST && $.ifee.FEE_LIST.length){
					$.ifee.show();					
				}else{
					$.ifee.hide();					
				}
									
				//恢复支票数据
				var checkInfo = $.ifee.FEE_PAY.get("CHECK_INFO");
				if(checkInfo && checkInfo.length){
					checkInfo.eachKey(function(key){
						$("#fee_check_edit_area *[name=" + key + "]").val(checkInfo.get(key));
					});
				}
				
				//恢复POS机信息
				$.ifee.POS_INFO = data.get("POS_INFO");
				$.ifee.POS_CONFIG = data.get("POS_CONFIG");
				
				//如果有刷卡金额，且刷卡金额跟缓存的POS支付金额一致，才显示关闭icon
				var chargeFee=0;
				if($.ifee.POS_INFO && $.ifee.POS_INFO.length 
						&& $.ifee.POS_INFO.containsKey("CUR_AMOUNT")){
					chargeFee=$.ifee.POS_INFO.get("CUR_AMOUNT");
				}
				var posFee = $.ifee.FEE_PAY.get($.ifee.POS_CODE);
				if(chargeFee && parseInt(posFee)>0 
						&& chargeFee == posFee ){
					$.ifee.disabledPos(true);
				}else{
					$.ifee.disabledPos(false);
				}
			}
		},
		//清除所有费用
		clearFeeList:function(isClosed){
			if(!isClosed && $.ifee.POS_INFO && $("#POS_PAY_BTN").attr("state")=="1"){
				MessageBox.alert("告警提示", "已经进行了POS刷卡缴费，必须撤销以后才能操作费用!", $.ifee.posPayFee);
				return false;
			}
			if($.ifee.FEE_LIST && $.ifee.FEE_LIST.length){
				$.ifee.FEE_LIST.each(function(item){
					item.clear();
				});
			}
			$.ifee.FEE_LIST=null;
			if($.ifee.FEE_PAY && $.ifee.FEE_PAY.length){
				if($.ifee.FEE_PAY.containsKey("CHECK_INFO")){
					$.ifee.FEE_PAY.get("CHECK_INFO").clear();
					$.ifee.FEE_PAY.removeKey("CHECK_INFO");
				}
				$.ifee.FEE_PAY.clear();
			}
			$.ifee.FEE_PAY=null;
			
			return true;
		},
		//清空费用组件所有数据
		clearFee:function(){
			$.ifee.clearFeeList(true);
			
			if($.ifee.POS_CONFIG){
				$.ifee.POS_CONFIG.clear();
			}
			$.ifee.POS_CONFIG=null;
			
			if($.ifee.POS_INFO){
				$.ifee.POS_INFO.clear();
				$.ifee.disabledPos(false);
			}
			$.ifee.POS_INFO=null;
			$.sidebar.hideFeeInfo();
		},
		//获取费用信息
		getFee:function(){
			var feeList = $.DatasetList();
			var feePay = $.DataMap();
			var posConf = $.DataMap();
			var posInfo = $.DataMap();
			if($.ifee.FEE_LIST && $.ifee.FEE_LIST.length){
				feeList = $.ifee.FEE_LIST;
			}
			if($.ifee.FEE_PAY && $.ifee.FEE_PAY.length){
				feePay = $.ifee.FEE_PAY;
			}
			if($.ifee.POS_CONFIG && $.ifee.POS_CONFIG.length){
				posConf = $.ifee.POS_CONFIG;
			}
			if($.ifee.POS_INFO && $.ifee.POS_INFO.length){
				posInfo = $.ifee.POS_INFO;
			}
			return "{\"FEE_LIST\":" + feeList.toString() + ",\"FEE_PAY\":" + feePay.toString() + ",\"POS_CONFIG\":" + posConf.toString() + ",\"POS_INFO\":" + posInfo.toString() + "}";
		},
		/**
		 * 确认费用
		 */
		checkFee:function(){
			//如果没有费用信息，则跳过校验自动提交
			if(!$.ifee.FEE_LIST || !$.ifee.FEE_LIST.length){
				return "0";
			}
					
			//判断是否支付方式
			if($.ifee.PAY_LIMIT){
				var payCount = 0;
				$.ifee.FEE_PAY.eachKey(function(key, idx, totalCount){
					if(key == "TOTAL" || key == "CHECK_INFO"){
						return true;
					}
					if(parseInt($.ifee.FEE_PAY.get(key)) != 0){
						payCount++;
					}
				});
				if(payCount>1){
					MessageBox.alert("告警提示", "仅支持一种支付方式！");
					return "1";
				}				
			}
			//如果没有进行POS机刷卡
			if($.ifee.FEE_PAY.containsKey($.ifee.POS_CODE)
				 && $.ifee.FEE_PAY.get($.ifee.POS_CODE)>0
					&& $("#POS_PAY_BTN").attr("state")=="0" ){
				MessageBox.alert("告警提示", "请进行POS机刷卡！", $.ifee.posPayFee);
				return "1";
			}
			//如果没有录入支票信息
			if($.ifee.FEE_PAY.containsKey($.ifee.CHECK_CODE)
				 && $.ifee.FEE_PAY.get($.ifee.CHECK_CODE)>0
					&& ( !$.ifee.FEE_PAY.containsKey("CHECK_INFO") 
						|| !($.ifee.FEE_PAY.get("CHECK_INFO")).length) ){
				MessageBox.alert("告警提示", "请先录入支票信息！", function(){
					$.ifee.showCheckEdit();
				});
				return "1";
			}
			
			//如果没有费用信息，返回true，执行其他动作
			var modeKey = null,tmpFee;
			var feeMode = $.DataMap();
			$.ifee.FEE_LIST.each(function(item,index,totalcount){
				modeKey = item.get("MODE");
				tmpFee=0;
				if(feeMode.containsKey(modeKey)){
					tmpFee=parseInt(feeMode.get(modeKey));
				}
				feeMode.put(modeKey, tmpFee + parseInt(item.get("PAY")));
			});
			if(feeMode.length < 1){
				return "0";
			}
			
			var msg = "";
			feeMode.eachKey(function(key){
   				var factPayFee = parseInt(feeMode.get(key));
   				if(factPayFee>=0){
   					msg += "收取" + $.ifee.getFeeModeDesc(key) + ( Math.abs(factPayFee)/100 ) + "元<br\/>";
   				}else if(factPayFee<0){
   					msg += "清退" + $.ifee.getFeeModeDesc(key) + ( Math.abs(factPayFee)/100 ) + "元<br\/>";
   				}
			});
			msg += "--------------------<br\/>";
			msg += "合计" + (parseInt($.ifee.FEE_PAY.get("TOTAL"))/100) + "元<br\/>";
			msg += "是否继续?";
			return msg;
		},
		/**
		 * *******************POS机***********************
		 * 设置POS机公用参数
		 */
		setPosParam:function(posStr){
			if(posStr && $.isString(posStr)){
				$.ifee.POS_CONFIG=$.DataMap(posStr);
			}	
		},
		//POS缴费
		posPayFee:function(){
			if(!$.ifee.POS_CONFIG || !$.ifee.POS_CONFIG.length){
				MessageBox.alert("告警提示", "POS配置数据未加载!");
				return;
			}
			var posFee = $.ifee.FEE_PAY.get($.ifee.POS_CODE);
			if (parseInt(posFee) <= 0) {
				MessageBox.alert("告警提示", "POS刷卡金额必须大于零!");
				return;
			}
			if(!$.ifee.POS_INFO || !$.ifee.POS_INFO.length){
				$.ifee.POS_INFO = $.DataMap();
			}
			var userId,chargeFee="0";
			if($.ifee.POS_CONFIG.containsKey("USER_ID")){
				userId = $.ifee.POS_CONFIG.get("USER_ID");
			}else{
				userId = "9999999999999999";
			}
			if($.ifee.POS_INFO.containsKey("CUR_AMOUNT")){
				chargeFee=$.ifee.POS_INFO.get("CUR_AMOUNT");
			}
			if(!chargeFee) chargeFee="0";
			var param = "&TRADE_TYPE_CODE="+$.ifee.POS_CONFIG.get("TRADE_TYPE_CODE");
			param += "&SERIAL_NUMBER="+$.ifee.POS_CONFIG.get("SERIAL_NUMBER");
			param += "&EPARCHY_CODE="+$.ifee.POS_CONFIG.get("FEE_EPARCHY_CODE");		//路由地州
			param += "&POS_COUNT_LIMIT="+$.ifee.FEE_CONFIG.POS_COUNT_LIMIT;				//刷卡限制次数
			param += "&USER_ID="+userId;
			param += "&CUR_AMOUNT="+chargeFee;
			param += "&ALL_AMOUNT="+$.ifee.FEE_PAY.get($.ifee.POS_CODE);
			
			//预刷卡TRADE_ID，后续需要替换成真正TRADE_ID
			if($.ifee.POS_INFO.containsKey("TRADE_POS_ID")){
				param += "&PRE_TRADE_ID="+$.ifee.POS_INFO.get("TRADE_POS_ID");
				$.ifee.showPosTrade($.ifee.POS_URL+param);
				return;
			}
			//每笔业务只会加载一次，
			$.httphandler.submit(null, $.ifee.clazz, "getPosTradeId", "&EPARCHY_CODE="+$.ifee.POS_CONFIG.get("FEE_EPARCHY_CODE"), function(data){
				if(data && data.length>0){
					//保存好虚拟POS刷卡ID，后续登记时候需要替换为正式的台账ID
					$.ifee.POS_INFO.put("TRADE_POS_ID", data.get("TRADE_POS_ID"));
					param += "&PRE_TRADE_ID="+$.ifee.POS_INFO.get("TRADE_POS_ID");
					$.ifee.showPosTrade($.ifee.POS_URL+param);
				}else{
					MessageBox.alert("告警提示", "获取pos业务流水号出错！");
				}
			},function(code, info, detail){
				MessageBox.error("错误提示","加载pos业务流水号错误！",null, null, info, detail);
			});
		},
		afterPosTrade:function(fee){
			$.ifee.POS_INFO.put("CUR_AMOUNT", fee);
			//如果费用相等，则禁用POS机刷卡操作
			if($.ifee.FEE_PAY.get($.ifee.POS_CODE) == fee){
				$.ifee.disabledPos(true);
			}else{
				$.ifee.disabledPos(false);
				/*触发费用支付重新计算
				var posInput = $("#fee_pay_list input[name=PAYMONEY][pay_mode="+$.ifee.POS_CODE+"]");
				posInput.val("0");
				posInput.attr("disabled", false);
				posInput.trigger("blur");*/
			}
		},
		
		//关闭POS刷卡窗口
		closePosTrade:function(fee){
			$.ifee.afterPosTrade(fee);
			$("#PosTradePanel").css("display", "none");
		},

		//POS刷卡窗口
		showPosTrade:function(posUrl){
			var posTrade=$("#PosTradePanel");
			if(!posTrade || !posTrade.length){
				var posHtml = [];
				posHtml.push('<div id="PosTradePanel" class="c_popup">	');
				posHtml.push('<div class="c_popupWrapper">	');
				posHtml.push('<div class="c_popupHeight"></div>	');
				posHtml.push('<div class="c_popupBox" style="width:800px">	');
				posHtml.push('<div class="c_popupTitle">	');
				posHtml.push('<div class="text">POS机刷卡</div>	');
				posHtml.push('</div>	');
				posHtml.push('<div class="c_popupContent"><div class="c_popupContentWrapper">	');
				posHtml.push('<div class="c_msg c_msg-popup">	');
				
				posHtml.push('	<iframe id="posTradeTarget" style="height:388px" scrolling="no" frameborder="0" src=""></iframe>	');
				
				posHtml.push('</div>	');
				posHtml.push('</div></div>	');
				posHtml.push('<div class="c_popupBottom"><div></div></div>	');
				posHtml.push('<div class="c_popupShadow"></div>	');
				posHtml.push('</div>	');
				posHtml.push('</div>	');
				posHtml.push('<iframe class="c_popupFrame"></iframe>	');
				posHtml.push('<div class="c_popupCover"></div>	');
				posHtml.push('</div>	');
				
				$(document.body).append(posHtml.join(""));
				posHtml=null;
				posTrade=$("#PosTradePanel");
			}
			$("#posTradeTarget").attr("src", posUrl);
			posTrade.css("display", "");
		}
	}});

//	//以下方法主要是业务界面交互使用
//	window.showFeeList=$.ifee.showFeeList;					//展示费用列表
//	window.clearFeeList=$.ifee.clearFeeList;					//清除费用列表，不包括费用POS信息
//	window.checkFee=$.ifee.checkFee;							//校验费用
//	window.setPosParam=$.ifee.setPosParam;					//设置POS配置数据
//	
//	//以下方法主要是在页面打开关闭切换过程中使用
//	window.getFee=$.ifee.getFee;								//获取费用
//	window.clearFee=$.ifee.clearFee;							//清除费用：包括费用列表，支付列表，POS信息等
//	window.renewFee=$.ifee.renewFee;							//恢复费用
	
	$($.ifee.init);

})(Wade);