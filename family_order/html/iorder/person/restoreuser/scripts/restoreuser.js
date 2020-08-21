var PAGE_FEE_LIST = $.DataMap();

function refreshPartAtferAuth(data) {
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var userId = data.get("USER_INFO").get("USER_ID");
	var productId = data.get("USER_INFO").get("PRODUCT_ID");
	var brandCode = data.get("USER_INFO").get("BRAND_CODE");
	var netTypeCoce = data.get("USER_INFO").get("NET_TYPE_CODE");
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var cityCode = data.get("USER_INFO").get("CITY_CODE");
	var param = "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId + "&PRODUCT_ID=" + productId
			+ "&BRAND_CODE=" + brandCode + "&NET_TYPE_CODE=" + netTypeCoce 
			+ "&EPARCHY_CODE=" + eparchyCode + "&CITY_CODE=" + cityCode;
	//uca客户名称
	var custName = data.get("CUST_INFO").get("CUST_NAME");
	//效验方式
	var checkMode=data.get("CHECK_MODE");

    var psptTypeCode = data.get("CUST_INFO").get("PSPT_TYPE_CODE");
    
    $("#OLD_PRODUCT_NAME").html(data.get("USER_INFO").get("PRODUCT_NAME"));
    $("#OLD_PRODUCT_ID").val(data.get("USER_INFO").get("PRODUCT_ID"));

	$.ajax.submit('AuthPart', 'loadChildInfo', param,'ProductTypePart,userResInfosPart,hiddenPart,resDiscntInfosPart,resSvcInfosPart,resPlatSvcInfosPart',
			function(data) {
				if (data && data.length > 0) {
					//提示：IMS固话不复机
					var widenetTag = data.get('WIDENET_TAG');
					var IMSTag = data.get('IMS_TAG');
					if(widenetTag == "1" && IMSTag == "1" || IMSTag == "2")
					{
						MessageBox.alert("提示", "宽带和IMS固话不复机!");
					}
					var familyTag = data.get('FAMILY_TAG');
					if(familyTag == "1"){
						$("#IS_RESTORE_FAMILY").attr("disabled", false);
					}else{
						$("#IS_RESTORE_FAMILY").attr("disabled", true);
					}
					/**
					 * REQ201705270006_关于人像比对业务优化需求
					 * @author zhuoyingzhi
					 * @date 20170703
					 */
					$("#UCA_CUST_NAME").val(custName);
					$("#AUTH_CHECK_MODE").val(checkMode);
                    $("#custInfo_PSPT_TYPE_CODE").val(psptTypeCode);
					/********************************/
					/**
					 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
					 * @author zhuoyingzhi
					 * @date 20170805
					 */
					var isAgentRight=data.get("isAgentRight");
					
					if(isAgentRight == "0"){
						//有权限
						$("#agent_right_span").attr('disabled',false);
					}else if(isAgentRight == "1"){
						//无权限
						$("#mySegment").attr('disabled','true');
//						$("#agent_right_span").attr('disabled',true);
					}
					/*************REQ201707060009关于补卡、密码重置、复机业务优化的需求******end******************/					
					
					var disableRestoreFlag = data.get("DISABLE_RESTORE");
					var resReason = data.get("RESET_REASON");
					if(disableRestoreFlag=="1"){
						$.endPageLoading();
						if(resReason!=null && resReason!="")
						{
							MessageBox.alert("",resReason, $.auth.reflushPage);
						}
					}else{
						MessageBox.alert("提示",resReason);
						var msg = data.get("MESSAGE_CONTENT");
						if(msg!=null && msg!="")
						{
							MessageBox.alert("提示",msg);
						}
					}
					
//					packageList.renderComponent("", eparchyCode);
//					pkgElementList.initElementList(null);
					selectedElements.renderComponent("&PRODUCT_ID=", eparchyCode);
					//清空费用
					$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
					PAGE_FEE_LIST.clear();
					//针对原号复机的情况进行样式变化
					delResTable();
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				MessageBox.alert("提示", error_info);
			});
	
}

//资源校验 按钮事件
function checkRes(e) 
{
	var params = new $.DataMap();
	var resTypeCode = e;
	var resNo = null;
	$("#TABLE_TAG").val("0");	//设置表格操作标记（资源校验后执行的方法需要）
	if(resTypeCode == "0"){	//手机号码
		resNo =  $('#RES_SERIAL_CODE').val();//输入的资源编码
	}else if(resTypeCode == "1"){	//sim卡
		resNo = $('#RES_SIM_CODE').val();//输入的资源编码
	}
	$("#INPUT_RES_CODE").val(resNo);
	$("#INPUT_RES_TYPE_CODE").val(resTypeCode);
	if(resNo==null || resNo=="")
	{
		if(resTypeCode == "0"){
			$.TipBox.show(document.getElementById('RES_SERIAL_CODE_VALUE'), "请输入新资源号码再进行校验！", "red");
			return false;
		}
		if(resTypeCode == "1"){
			$.TipBox.show(document.getElementById('RES_SIM_CODE_VALUE'), "请输入新资源号码再进行校验！", "red");
		}
	}
	
	var newSimCard = $('#NEW_SIM_CARD_NO').val();//用户上一次新换的sim卡
    var newPhone = $('#NEW_PHONE_NO').val();//用户上一次新换的号码
    var oldPhone = $('#OLD_PHONE_NO').val();//用户老号码
    var oldSimCard = $('#OLD_SIM_CARD_NO').val();//用户老sim卡
    var needRePosses = $('#NEED_REPOSSESS').val();  //用户原来的手机号是否需要重新占用
    var oldPhoneCanUse = $('#OLD_PHONE_CANUSE').val(); //用户原来的手机号校验通过后，只能用原号码了
    var needChangePhone = $('#NEED_CHANGE_PHONE').val(); //用户是否需要更换新号码
    var needChangeSim = $('#NEED_CHANGE_SIM').val(); //用户是否需要更换新sim卡
    var simCheckTag = $('#SIM_CHECK_TAG').val(); //sim卡校验结果标记
    var phoneCheckTag = $('#PHONE_CHECK_TAG').val(); //服务号码校验结果标记
    
	var SerialNumber = ""; //传到服务层需要校验的服务号码
	var SimCardNo = "";//传到服务层需要校验的sim卡
	if(resTypeCode=="0")
	{
		if(!isTel(resNo))
		{
			$.TipBox.show(document.getElementById('RES_SERIAL_CODE_VALUE'), "服务号码格式不正确，请重新输入！", "red");
//			alert("服务号码格式不正确，请重新输入！");
			return false;
		}
		if(needChangePhone=="MUST_MAITAIN")
		{
			MessageBox.alert("提示", "原号码能用来复机，不能改号！");
			return false;
		}
		if(needRePosses=="1" && oldPhoneCanUse=="1" && oldPhone!=resNo)
		{
			MessageBox.alert("提示", "原号码校验通过，请使用原号码！");
			return false;
		}
		//上一个已经校验通过的号码，不需要重复进行资源校验，防止资源重复选占
		if(resNo==newPhone && phoneCheckTag=="1")
		{
			MessageBox.alert("提示", "此号码已经校验通过！");
			return false;
		}

		SerialNumber = resNo;
	}
	else if(resTypeCode=="1")
	{
		if(needChangePhone!="MUST_MAITAIN" && phoneCheckTag!="1")
		{
			$.TipBox.show(document.getElementById('RES_SERIAL_CODE'), "请先校验服务号码！", "red");
//			alert("请先校验服务号码！");
			return false;
		}
		if(resNo.length!=20)
		{
			$.TipBox.show(document.getElementById('RES_SIM_CODE_VALUE'), "输入的sim卡位数不对，请重新输入！", "red");
//			alert("输入的sim卡位数不对，请重新输入！");
			return false;
		}
		if(oldSimCard == resNo)
		{
			$.TipBox.show(document.getElementById('RES_SIM_CODE_VALUE'), "输入的sim卡号和原sim卡号相同，请重新输入！", "red");
//			alert("输入的sim卡号和原sim卡号相同，请重新输入！");
			return false;
		}
		//上一个已经校验通过的sim卡，不需要重复进行资源校验，防止资源重复选占
		if(resNo==newSimCard && simCheckTag=="1")
		{
			
			MessageBox.alert("提示", "此sim卡号已经校验通过！");
			return false;
		}

		if (newPhone!=null && newPhone!=""){
			SerialNumber = newPhone;
			SimCardNo = resNo;
		} else { 
			SerialNumber = oldPhone;
			SimCardNo = resNo;
		}
	}
	
	var param = '&RES_TYPE_CODE=' + resTypeCode + '&ORG_SIM_CARD_NO='+ oldSimCard
				+'&OLD_SIM_CARD_NO='+newSimCard +'&OLD_PHONE_NO='+ newPhone
				+'&NEW_SIM_CARD_NO='+SimCardNo +'&NEW_PHONE_NO='+ SerialNumber
				+'&WRITE_TAG=' + $("#WRITE_TAG").val()
				+'&EPARCHY_CODE='+ $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
				+'&USER_INFO='+ $.auth.getAuthData().get("USER_INFO");
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit(null, 'checkNewResource',param, null, function(data){
		data.put("RES_CODE", $("#INPUT_RES_CODE").val());
		data.put("RES_TYPE_CODE", $("#INPUT_RES_TYPE_CODE").val());
		afterCheckNewRes(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
  });
}

/**
 * 表格中校验按钮
 * @param e
 * @returns {Boolean}
 */
function checkTableRes(obj) 
{
	var resNo = $(obj).attr("thisResCode");
	var resTypeCode = $(obj).attr("thisResTypeCode");
	var tableTag = $(obj).attr("thisTableTag");
	$("#THIS_RES_CODE").val(resNo);	
	$("#THIS_RES_TYPE_CODE").val(resTypeCode);
	$("#TABLE_TAG").val(tableTag);	//设置表格操作标记（资源校验后执行的方法需要）
	if(resNo==null || resNo=="")
	{
		$.TipBox.show(document.getElementById('RES_CODE_VALUE'), "请输入新资源号码再进行校验！", "red");
//		alert('请输入新资源号码再进行校验！');
		return false;
	}
	
	var newSimCard = $('#NEW_SIM_CARD_NO').val();//用户上一次新换的sim卡
    var newPhone = $('#NEW_PHONE_NO').val();//用户上一次新换的号码
    var oldPhone = $('#OLD_PHONE_NO').val();//用户老号码
    var oldSimCard = $('#OLD_SIM_CARD_NO').val();//用户老sim卡
    var needRePosses = $('#NEED_REPOSSESS').val();  //用户原来的手机号是否需要重新占用
    var oldPhoneCanUse = $('#OLD_PHONE_CANUSE').val(); //用户原来的手机号校验通过后，只能用原号码了
    var needChangePhone = $('#NEED_CHANGE_PHONE').val(); //用户是否需要更换新号码
    var needChangeSim = $('#NEED_CHANGE_SIM').val(); //用户是否需要更换新sim卡
    var simCheckTag = $('#SIM_CHECK_TAG').val(); //sim卡校验结果标记
    var phoneCheckTag = $('#PHONE_CHECK_TAG').val(); //服务号码校验结果标记
    
	var SerialNumber = ""; //传到服务层需要校验的服务号码
	var SimCardNo = "";//传到服务层需要校验的sim卡
	if(resTypeCode=="0")
	{
		if(!isTel(resNo))
		{
			$.TipBox.show(document.getElementById('RES_SERIAL_CODE_VALUE'), "服务号码格式不正确，请重新输入！", "red");
//			alert("服务号码格式不正确，请重新输入！");
			return false;
		}
		if(needChangePhone=="MUST_MAITAIN")
		{
			MessageBox.alert("提示", "原号码能用来复机，不能改号！");
	    	$("#buttonSet_0").css("display", "none");
	    	$("#checkOKbutton_0").css("display", "");
			return false;
		}
		if(needRePosses=="1" && oldPhoneCanUse=="1" && oldPhone!=resNo)
		{
			MessageBox.alert("提示", "原号码校验通过，请使用原号码！");
			return false;
		}
		//上一个已经校验通过的号码，不需要重复进行资源校验，防止资源重复选占
		if(resNo==newPhone && phoneCheckTag=="1")
		{
			MessageBox.alert("提示", "此号码已经校验通过！");
			return false;
		}

		SerialNumber = resNo;
	}
	else if(resTypeCode=="1")
	{
		if(needChangePhone!="MUST_MAITAIN" && phoneCheckTag!="1")
		{
			$.TipBox.show(document.getElementById('RES_SERIAL_CODE'), "请先校验服务号码！", "red");
//			alert("请先校验服务号码！");
			return false;
		}
		if(resNo.length!=20)
		{
			$.TipBox.show(document.getElementById('RES_SIM_CODE_VALUE'), "输入的sim卡位数不对，请重新输入！", "red");
//			alert("输入的sim卡位数不对，请重新输入！");
			return false;
		}
		if(oldSimCard == resNo)
		{
			$.TipBox.show(document.getElementById('RES_SIM_CODE_VALUE'), "输入的sim卡号和原sim卡号相同，请重新输入！", "red");
			return false;
		}
		//上一个已经校验通过的sim卡，不需要重复进行资源校验，防止资源重复选占
		if(resNo==newSimCard && simCheckTag=="1")
		{
			
			MessageBox.alert("提示", "此sim卡号已经校验通过！");
			return false;
		}

		if (newPhone!=null && newPhone!=""){
			SerialNumber = newPhone;
			SimCardNo = resNo;
		} else { 
			SerialNumber = oldPhone;
			SimCardNo = resNo;
		}
	}
	
	var param = '&RES_TYPE_CODE=' + resTypeCode + '&ORG_SIM_CARD_NO='+ oldSimCard
				+'&OLD_SIM_CARD_NO='+newSimCard +'&OLD_PHONE_NO='+ newPhone
				+'&NEW_SIM_CARD_NO='+SimCardNo +'&NEW_PHONE_NO='+ SerialNumber
				+'&WRITE_TAG=' + $("#WRITE_TAG").val()
				+'&EPARCHY_CODE='+ $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
				+'&USER_INFO='+ $.auth.getAuthData().get("USER_INFO");
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit(null, 'checkNewResource',param, null, function(data){
		afterCheckNewRes(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
  });
}

//资源校验后
function afterCheckNewRes(data)
{
	var resType = null;
	var newResCode = null;
	var tableTag = $("#TABLE_TAG").val();
	if(tableTag == "1"){
		 resType = $('#THIS_RES_TYPE_CODE').val();//当前操作的资源类型
		 newResCode = $('#THIS_RES_CODE').val();
	}else{
		 resType = data.get("RES_TYPE_CODE");//当前操作的资源类型
		 newResCode = data.get("RES_CODE");
	}
	var imsi = data.get("IMSI");
	var ki = data.get("KI");
	var startDate = data.get("START_DATE");
	var endDate =  data.get("END_DATE");
	var simCardSaleMoney = data.get("SIM_CARD_SALE_MONEY");

	var newSimCard = $('#NEW_SIM_CARD_NO').val();//用户上一次新换的sim卡
    var newPhone = $('#NEW_PHONE_NO').val();//用户上一次新换的号码
    var oldPhone = $('#OLD_PHONE_NO').val();//用户老号码
    var oldSimCard = $('#OLD_SIM_CARD_NO').val();//用户老sim卡
    var needRePosses = $('#NEED_REPOSSESS').val(); //用户原来的手机号是否能复机

    //add by xingkj 给白卡标记和白卡买断用费赋值
    $('#SIM_FEE_TAG').val(data.get("SIM_FEE_TAG"));
    $('#SIM_CARD_SALE_MONEY').val(data.get("SIM_CARD_SALE_MONEY")==''?'0':data.get("SIM_CARD_SALE_MONEY"));
    
    if(resType == "0")
	{
    	if(data.get("PHONE_CHECK_TAG")!=null && data.get("PHONE_CHECK_TAG")!="")
    	{
    		$('#PHONE_CHECK_TAG').val(data.get("PHONE_CHECK_TAG"));
    	}
    	
    	if(data.get("PHONE_CHECK_TAG")!="1")
    	{
    		var msg = "号码资源校验失败！";
    		if(data.get("CHECK_MSG")!=null && data.get("CHECK_MSG")!=""){
    			msg = msg + data.get("CHECK_MSG");
    		}
    		/**
             * REQ201608230012 关于2016年下半年吉祥号码优化需求（三）
             * chenxy3 20161009
             * 提示其更换号码
             * */
    		if(data.get("NEED_CHANGE_PHONE")!=null && data.get("NEED_CHANGE_PHONE")!=""){
    			$('#NEED_CHANGE_PHONE').val(data.get("NEED_CHANGE_PHONE"));
    		}else{
    			$('#NEED_CHANGE_PHONE').val("false");
    		}
    		MessageBox.alert("提示", msg);
    		return false;
    	}else{
        	MessageBox.alert("提示", "号码校验通过！");
		}
    	

		if(needRePosses=="1" && oldPhone == newResCode){
			$("#OLD_PHONE_CANUSE").val("1");
		}

		$('#START_DATE').val(startDate);
		$('#END_DATE').val(endDate);
		$('#NEW_PHONE_NO').val(newResCode);

		//清空费用
		$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
		PAGE_FEE_LIST.clear();
		//号码需要交纳的预存费用，如果是老号码回收后重新选占返回的费用，不收取
		if(data.get("FEE_CODE_FEE") && newResCode!=oldPhone){
			var feeData = $.DataMap();
			feeData.put("MODE", "2");
			feeData.put("CODE", "62");//选号预存收入
			feeData.put("FEE",  data.get("FEE_CODE_FEE"));
			feeData.put("PAY",  data.get("FEE_CODE_FEE"));	
			feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());		
			PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
			$.feeMgr.insertFee(feeData);	
		}
		initProduct();
	}else if(resType == "1")
	{	
		if(data.get("SIM_CHECK_TAG")!=null && data.get("SIM_CHECK_TAG")!="")
		{
			$('#SIM_CHECK_TAG').val(data.get("SIM_CHECK_TAG"));
		}
		
		if(data.get("SIM_CHECK_TAG")!="1")
		{
			var msg = "sim卡资源校验失败！";
			if (data.get("TIPS_INFO")!=null && data.get("TIPS_INFO")!=""){
				msg = msg + data.get("TIPS_INFO");
			}
			MessageBox.alert("提示", msg);
			return false;
		}else{
			var msg = "sim卡资源校验通过！";
			if (data.get("FEE_DATA") && data.get("FEE_DATA").get("FEE_INFO")){
				msg = msg + data.get("FEE_DATA").get("FEE_INFO");
			}
			MessageBox.alert("提示", msg);
		}
		
		$('#START_DATE').val(startDate);
		$('#END_DATE').val(endDate);
		$('#NEW_SIM_CARD_NO').val(newResCode);
		$('#RES_INFO').val(imsi);
		$('#SIM_CARD_SALE_MONEY').val(simCardSaleMoney);

		//清空费用
		$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
		if(data.get("FEE_DATA") && data.get("FEE_DATA").get("FEE_TAG")=='1'
			&& data.get("FEE_DATA").get("FEE")){
				var feeData = $.DataMap();
				feeData.put("MODE", "0");
				feeData.put("CODE", "10");
				feeData.put("FEE",  data.get("FEE_DATA").get("FEE"));
				feeData.put("PAY",  data.get("FEE_DATA").get("FEE"));		
				feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());				
				$.feeMgr.insertFee(feeData);
				PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
	    }
	}
	
    if(tableTag == "1"){	//表格操作校验通过不修改表格数据，只修改通过图标
    	var rowIndex = userResTable.selected;
    	$("#buttonSet_" +rowIndex).css("display", "none");
    	$("#checkOKbutton_" +rowIndex).css("display", "");
    }else{
    	//修改选择行的数据	并修改校验通过图标
    	
    	var rowEdit = $.ajax.buildJsonData("userResInfosPart");
    	rowEdit["col_RES_CODE"]=$('#INPUT_RES_CODE').val();	//号码（提交用）
    	//展示在前台用
    	rowEdit["col_CHECK_RES_CODE"]=$('#INPUT_RES_CODE').val()
    		+ '<span class="e_space"></span>'
    		+ '<button jwcid="@Any" class="e_button-blue e_button-r e_button-s"> '
    		+ '<i class="e_ico-ok"></i><span >校验通过</span></button>';	
    	rowEdit["col_START_DATE"]=startDate;
    	rowEdit["col_END_DATE"]=endDate;
    	rowEdit["col_IMSI"]=imsi;
    	rowEdit["col_KI"]=ki;
    	if(resType == "0"){	//手机号码
    		userResTable.updateRow(rowEdit,0);
    	}else{	//sim卡
    		userResTable.updateRow(rowEdit,1);
    	}
    	$("#buttonSet_" +userResTable.selected).css("display", "none");
    	$("#checkOKbutton_" +userResTable.selected).css("display", "");
    }
	
	var thesubmit = $('#CSSUBMIT_BUTTON');
	if(thesubmit != null && thesubmit != "")
	{
		thesubmit.attr("disabled",false);
	}
}

function isResChecked()
{
    var needChangePhone = $('#NEED_CHANGE_PHONE').val(); //用户是否需要更换新号码
    var needChangeSim = $('#NEED_CHANGE_SIM').val(); //用户是否需要更换新sim卡
    var simCheckTag = $('#SIM_CHECK_TAG').val(); //sim卡校验结果标记
    var phoneCheckTag = $('#PHONE_CHECK_TAG').val(); //服务号码校验结果标记

    if(needChangePhone=="true" && phoneCheckTag!="1")
	{
    	MessageBox.alert("提示", "需要更换服务号码！");
		return false;
	}
	if(needChangePhone!="MUST_MAITAIN" && phoneCheckTag!="1")
	{
		$.TipBox.show(document.getElementById('AUTH_SERIAL_NUMBER'), "请先校验服务号码！", "red");
//		alert("请先校验服务号码！");
		return false;
	}

	if(needChangeSim=="true" && simCheckTag!="1")
	{
		MessageBox.alert("提示", "需要更换sim卡！");
		return false;
	}
	
	return true;
}


// 省内无线固话一证五号检查  REQ202001210005_关于优化TD无线固话业务一证五号验证的需求
function checkProvinceMorePsptId (){
    // 业务类型为：无线固话复机业务 进行省内固话一证五号检查。
    var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	var flag = true;
    if(tradeTypeCode && tradeTypeCode== "3813"){// 无线固话复机业务，才进行一证五号校验
		flag = false;
        // 获取三户信息
        var obj = $.auth.getAuthData();
        var custId = "";
        if (obj){
            custId =obj.get("CUST_INFO").get("CUST_ID");
        }
        // 准备参数
        var param = "&TRADE_TYPE_CODE=" + tradeTypeCode + "&CUST_ID=" + custId ;

        $.beginPageLoading("省内证件信息数量校验。。。");
        $.ajax.submit(null, "checkProvinceMorePsptId", param,'',
            function (data) {
                $.endPageLoading();
                if (data && data.get("CODE") != "0") {

                    MessageBox.alert(data.get("MSG"));
                    flag = false;
                }else {
                	flag = true;
				}
            }, function(error_code,error_info,derror){
                $.endPageLoading();
                MessageBox.error("错误提示", "省内证件信息数量校验获取后台数据错误！", null, null, error_info, derror);
            },{async: false});

    }else {// 非无线固话业务，不校验
        flag = true;
    }
    return flag;
}

//提交 台账
function submitBeforeAction()
{
	// 无线固话复机业务一证五号校验
    if(!checkProvinceMorePsptId()){
        return false;// 无线固话复机 ：一证五号校验失败，返回。
	}
	if(!isResChecked())
	{
		return false;
	}
	
	var data = selectedElements.getSubmitData();
	if(data==null || data=="[]" || data==""){
		MessageBox.alert("提示", "您没有选择产品信息，请选择产品！");
		return false;
	}

	var canSubmit = selectedElements.checkForcePackage(); 
	if(!canSubmit){
		return false;
	}	
		/**
		 * REQ201705270006_关于人像比对业务优化需求
		 * @author zhuoyingzhi
		 * @date 20170630
		 */
		var authCheckPsptTypeCode=$("#AUTH_CHECK_PSPT_TYPE_CODE").val();
		
		var authCheckPsptId=$("#AUTH_CHECK_PSPT_ID").val();
		
		//获取验证方式
		var authCheckMode=$("#AUTH_CHECK_MODE").val();
		
		
		//携入标识   1是携入      非1不是
		var npTag=$("#NPTag").val();
		
		//固话标识   1是固话  非1 不是固话
		var wxTag=$("#WXTag").val();
		
		if(npTag == 1 || wxTag == 1){
			//携入号码或者固话号码
			//提交的时候不处理
		}else{
            var picid = $("#custInfo_PIC_ID").val();
            var agentpicid = $("#custInfo_AGENT_PIC_ID").val();
            var angeTypecode= $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
            // 客户证件类型为 身份证/军人身份证
            if ((authCheckPsptTypeCode == "0" || authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "3") && authCheckMode=="0"){
                if (picid){// 客户摄像过
                    if (picid == "ERROR"){// 新户主 人像比对失败
                        MessageBox.error("告警提示","客户"+$("#custInfo_PIC_STREAM").val(),null, null, null, null);
                        return false;
                    }else {// 客户人像比对成功，继续往下办理业务
                        ;
                    }
                }else {// 客户未摄像过
                    if (agentpicid){// 经办人摄像过
                        if (agentpicid == "ERROR"){// 摄像失败
                            MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
                            return false;
                        }else {// 摄像成功   经办人或客户比对成功即可继续办理
                            ;
                        }
                    }else {// 经办人未摄像
                        MessageBox.error("告警提示","请客户或经办人摄像",null, null, null, null);
                        return false;
                    }
                }
            }else if ((authCheckPsptTypeCode == "D" || authCheckPsptTypeCode == "E" || authCheckPsptTypeCode == "G" || authCheckPsptTypeCode == "L" || authCheckPsptTypeCode == "M")  && authCheckMode=="0"){
                // 客户证件类型为单位证件类型
                if (angeTypecode == "0" || angeTypecode == "1" || angeTypecode == "3" ){
                    // 经办人证件类型为 身份证/军人身份证
                    if (agentpicid){// 经办人摄像过
                        if (agentpicid == "ERROR"){// 摄像失败
                            MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
                            return false;
                        }else {// 摄像成功   经办人或客户比对成功即可继续办理
                            ;
                        }
                    }else {// 经办人未摄像
                        MessageBox.error("告警提示","请经办人摄像",null, null, null, null);
                        return false;
                    }
                }
            }

		}
    /***************************************************/	
	var param = "&SELECTED_ELEMENTS=" + data.toString();
	
	/**REQ202002030009复机时增加继续保留的产品、优惠、平台业务的提醒 start*/
	if($("#IS_RESTORE_FAMILY").attr("checked")){
		$("#FAMILY_FALG").val('1');
	}else{
		$("#FAMILY_FALG").val('0');
	}
	
	var discntInputs = $("#resDiscntInfosPart input:checked");
	if(discntInputs.length>0){
		var discnts=new Wade.DatasetList
		for(var i=0;i<discntInputs.length;i++){
			var discnt=new Wade.DataMap();
			discnt.put("ELEMENT_ID",discntInputs[i].id);
			discnt.put("ELEMENT_NAME",discntInputs[i].title);
			discnt.put("ELEMENT_TYPE_CODE","D");
			discnt.put("PRODUCT_ID",$("#PRODUCT_ID").val());
			discnts.add(discnt)
		}
		param = param +"&RES_DISCNTS=" + discnts.toString();
	}
	var svcInputs = $("#resSvcInfosPart input:checked");
	if(svcInputs.length>0){
		var svcs=new Wade.DatasetList
		for(var i=0;i<svcInputs.length;i++){
			var svc=new Wade.DataMap();
			svc.put("ELEMENT_ID",svcInputs[i].id);
			svc.put("ELEMENT_NAME",svcInputs[i].title);
			svc.put("ELEMENT_TYPE_CODE","S");
			svc.put("PRODUCT_ID",$("#PRODUCT_ID").val());
			svcs.add(svc)
		}
		param = param +"&RES_SVCS=" + svcs.toString();
	}
	var platSvcInputs = $("#resPlatSvcInfosPart input:checked");
	if(platSvcInputs.length>0){
		var platSvcs=new Wade.DatasetList
		for(var i=0;i<platSvcInputs.length;i++){
			var platSvc=new Wade.DataMap();
			platSvc.put("ELEMENT_ID",platSvcInputs[i].id);
			platSvc.put("ELEMENT_NAME",platSvcInputs[i].title);
			platSvc.put("ELEMENT_TYPE_CODE","Z");
			platSvc.put("PRODUCT_ID",$("#PRODUCT_ID").val());
			platSvcs.add(platSvc)
		}
		param = param +"&RES_PLATSVCS=" + platSvcs.toString();
	}
	/**REQ202002030009复机时增加继续保留的产品、优惠、平台业务的提醒 end*/

	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
	var realName = $.auth.getAuthData().get("CUST_INFO").get("IS_REAL_NAME");//用于判断实名制用户办理的产品
	var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	var invoiceNo = $("#_INVOICE_CODE").val()
	param = param + "&SERIAL_NUMBER=" + serialNumber + "&USER_ID=" + userId
			+ "&INVOICE_NO=" + invoiceNo + "&EPARCHY_CODE=" + eparchyCode 
			+ "&REAL_NAME=" + realName; 
//	var data = $.table.get("userResTable").getTableData(null,true);//获取整个表格数据
	var data = userResTable.getData(true);//获取整个表格数据
	$("#X_CODING_STR").val(data);		
	$.cssubmit.addParam(param);
	return true;
}


/******************************工具方法*****************************************/
//设置某个原始的状态
function checkDisabledOper(obj,tag){
	if(tag == true){
		obj.attr("disabled",true);
		obj.attr("className","e_button-right e_dis");
	}
	if(tag == false){
		obj.attr("disabled",false);
		obj.attr("className","e_button-right");
	}
}

function isTel(str){
    var reg=/^([0-9]|[\-])+$/g ;
    if(str.length!=11&&str.length!=13){//增加物联网服务号码长度 13位
     return false;
    }
    else{
      return reg.exec(str);
    }
}


//===================产品组件脚本开始================================================
/**
 * 选中select
 * 
 * @param select名称
 * @param val
 *            值
 */
function selectedOption(name, val) {

	$("#" + name + " option[value=" + val + "]").attr("selected", true);
}

//产品和元素搜索
function changeSearchType(eventObj){
	var searchType = eventObj.value;
	var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
	param += "&SEARCH_TYPE="+searchType;
	if(searchType == "2"){
		param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
	}
	$.Search.get("productSearch").setParams(param);
}

function checkBeforeProduct() {
	if(!isResChecked())
	{
		return false;
	}

	//产品组件传值，如果有则只取传递产品
	var assignProductIds ="";
	var inparam ="";
	//147号码判断
    var authPhoneNo = $('#NEW_PHONE_NO').val(); //新服务号码
	if(authPhoneNo==null || authPhoneNo=="" || authPhoneNo!="undefined")
	{
		authPhoneNo = $('#OLD_PHONE_NO').val(); //原服务号码
	}
	if(authPhoneNo.indexOf('147')==0){
	  $.ajax.submit(null, 'getProductForSpc', inparam, null, function(data) {
		    $.cssubmit.disabledSubmitBtn(false);
		    for(var i = 0; i < data.getCount(); i++) {
		  	     var data0 = data.get(i);
		  	     if(data0){  
			              if(i=="0")
			                  assignProductIds= data0.get("PARA_CODE1");
			              else
			                  assignProductIds= assignProductIds+","+data0.get("PARA_CODE1");	
				 }
		  	}
		    ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),
		    		$('#EPARCHY_CODE').val(),"",assignProductIds);
	   },
	   function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
	else {
		ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
	}
}

/**
 * 选完产品后的动作
 * 
 * @param productId
 * @param productName
 * @param brandCode
 * @param brandName
 */
function afterChangeProduct(productId, productName, brandCode, brandName, productDesc) {
    $.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
    var feeData = $.DataMap();
    	feeData.clear();
		feeData.put("MODE", "2");
		feeData.put("CODE", "0");
		feeData.put("FEE",  "0");
		feeData.put("PAY",  "0");		
		feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
		$.feeMgr.insertFee(feeData);	
		
	    $("#PRODUCT_ID").val(productId);
//		$("#PRODUCT_NAME").val(productName);
		$("#PRODUCT_NAME").html(productName);
	    $("#PRODUCT_DESC").html(productDesc);
	    $("#PRODUCT_DESC").attr("tip",productDesc);
	    $("#productSelectBtn").attr("disabled", true).hide();  // 隐藏"产品目录"按钮
	    $("#CHANGE_PRODUCT_BTN").attr("disabled", false).show(); // 展示"变更"按钮
	    $("#PRODUCT_DISPLAY").show();                            // 展示已选产品
	    $("#PRODUCT_COMPONENT_DISPLAY").css("display", "");      // 展示"待选区"和"已选区"组件
		
		var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
		offerList.renderComponent(productId, eparchyCode);
//		pkgElementList.initElementList(null);
		var param = "&NEW_PRODUCT_ID="+productId;
		selectedElements.renderComponent(param, eparchyCode);
		//变换产品后，重置搜索组件 
		var setData = "&EPARCHY_CODE="+eparchyCode;
		setData += "&PRODUCT_ID="+productId;
		setData += "&SEARCH_TYPE=2";
		//$.Search.get("productSearch").setParams(setData);
		
		//老系统就没有查询产品费用
//		var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode 
//			+ "&EPARCHY_CODE="+ eparchyCode;
//		$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
//		    $.cssubmit.disabledSubmitBtn(false);
//		  	for(var i = 0,count=data.getCount(); i < count; i++) {
//		  	     var data = data.get(i);
//		  	     if(data){
//							feeData.clear();
//							feeData.put("MODE", data.get("FEE_MODE"));
//							feeData.put("CODE", data.get("FEE_TYPE_CODE"));
//							feeData.put("FEE",  data.get("FEE"));
//							feeData.put("PAY",  data.get("FEE"));		
//							feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());							
//							$.feeMgr.insertFee(feeData);			
//							PAGE_FEE_LIST.put("PRODUCT_FEE", $.feeMgr.cloneData(feeData));			
//					}
//		  	}
//         },
//         function(error_code,error_info,derror){
//			$.endPageLoading();
//			showDetailErrorInfo(error_code,error_info,derror);
//			});
}

/**
 * 产品搜索
 * 
 * @returns {Boolean}
 */
function searchOptionEnter() {
	var searchType = $("#productSearchType").val();
	var searchLi = $("#Ul_Search_productSearch li[class=focus]");
	if (searchType == "1") {
		// 产品搜索
		var productId = searchLi.attr("PRODUCT_ID");
		var productName = searchLi.attr("PRODUCT_NAME");
		var brandCode = searchLi.attr("BRAND_CODE");
		var brandName = searchLi.attr("BRAND");
		var productDesc = searchLi.attr("PRODUCT_DESC");
		afterChangeProduct(productId, productName, brandCode, brandName, productDesc);
	} else if (searchType == "2") {
		// 元素搜索
		var reOrder = searchLi.attr("REORDER");
		var elementId = searchLi.attr("ELEMENT_ID");
		var elementName = searchLi.attr("ELEMENT_NAME");
		var productId = searchLi.attr("PRODUCT_ID");
		var packageId = searchLi.attr("PACKAGE_ID");
		var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
		var forceTag = searchLi.attr("FORCE_TAG");

		if (reOrder != "R"
				&& selectedElements.checkIsExist(elementId, elementTypeCode)) {
			MessageBox.alert("提示", "您所选择的元素" + elementName + "已经存在于已选区，不能重复添加");
			return false;
		}
		var elementIds = $.DatasetList();
		var selected = $.DataMap();
		selected.put("PRODUCT_ID", productId);
		selected.put("PACKAGE_ID", packageId);
		selected.put("ELEMENT_ID", elementId);
		selected.put("ELEMENT_TYPE_CODE", elementTypeCode);
		selected.put("MODIFY_TAG", "0");
		selected.put("ELEMENT_NAME", elementName);
		selected.put("FORCE_TAG", forceTag);
		selected.put("REORDER", reOrder);
		elementIds.add(selected);
		if (selectedElements.addElements) {
			selectedElements.addElements(elementIds);
		}
	}
	$("#Div_Search_productSearch").css("visibility", "hidden");
}

/**
 * 设置品牌
 */
function setBrandCode() {
	if ($("#PRODUCT_TYPE_CODE").val() != "") {
		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	} else {
		$("#BRAND").val('');
	}
}

/**
 * 初始化产品产
 */
function initProduct() {
	var eparchy_code = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	offerList.renderComponent("",eparchy_code);
    selectedElements.renderComponent("",eparchy_code);
//    $("#productSelectBtn").attr("disabled", false);
//	$("#PRODUCT_NAME").val('');
	$("#productSelectBtn").attr("disabled", false).show();
    $("#PRODUCT_ID").val("");
    $("#PRODUCT_NAME").html("");
    $("#PRODUCT_DESC").html("");
    $("#PRODUCT_DISPLAY").hide();
    $("#PRODUCT_COMPONENT_DISPLAY").hide();
}

function disableElements(data) {
	if ($("#B_REOPEN_TAG").val() == '1') {
		selectedElements.disableAll();
	} else {
		if (data) {
			var temp = data.get(0);
			if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
				$("#NEW_PRODUCT_START_DATE").val(
						temp.get("NEW_PRODUCT_START_DATE"));
			}
		}
	}
}

function afterRenderSelectedElements(data){
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
// ===================产品组件脚本结束================================================

//===================读写卡组件脚本================================================

function beforeReadCard(){
    var needChangePhone = $('#NEED_CHANGE_PHONE').val(); //用户是否需要更换新号码
    var simCheckTag = $('#SIM_CHECK_TAG').val(); //sim卡校验结果标记
    var phoneCheckTag = $('#PHONE_CHECK_TAG').val(); //服务号码校验结果标记

    if(needChangePhone=="true" && phoneCheckTag!="1")
	{
    	MessageBox.alert("提示", "需要更换服务号码！");
		return false;
	}
	if(needChangePhone!="MUST_MAITAIN" && phoneCheckTag!="1")
	{
		$.TipBox.show(document.getElementById('AUTH_SERIAL_NUMBER'), "请先校验服务号码！", "red");
//		alert("请先校验服务号码！");
		return false;
	}

	var sn = $("#NEW_PHONE_NO").val();
	if(sn==null || sn=='undefined' || sn == ""){
		sn = $('#AUTH_SERIAL_NUMBER').val();
	}
	$.simcard.setSerialNumber(sn);
	return true;
}


function beforeCheckSimCardNo(data) {
	//alert(data);
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		var simno = data.get("SIM_CARD_NO");
		 $("#RES_SIM_CODE").val(simno);
//		 checkRes();
		 checkRes("1");
	}
	$("#WRITE_TAG").val(data.get("WRITE_TAG"));
}

function afterWriteCard(data){
	//alert(data.toString());
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.readSimCard();
	}
}


/**
 * 人像比对(按钮)
 * @param picid
 * @param picstream
 * @returns {Boolean}
 * @author zhuoyingzhi
 * @date 20170626
 */
function identification(picid,picstream){
	
	var custName,psptId,psptType,fornt,ucaCustName;
	
	if(picid == "custInfo_PIC_ID"){
	    //效验  客户名称(身份证中的客户名称)
		custName = $("#AUTH_CHECK_CUSTINFO_CUST_NAME").val();
		
		//uca中的客户名称
		ucaCustName = $("#UCA_CUST_NAME").val();
		
		//效验 身份证号码
		psptId = $("#AUTH_CHECK_PSPT_ID").val();
		//效验  身份证类型
		psptType = $("#AUTH_CHECK_PSPT_TYPE_CODE").val();
	
		//身份证正面
		fornt = $("#FRONTBASE64").val();
		
		//如果是手动输入证件号码,无法获取证件上的客户姓名
		if(custName == ""){
			custName=ucaCustName;
		}
	}else{
		//经办人
		custName = $("#custInfo_AGENT_CUST_NAME").val();
		psptId = $("#custInfo_AGENT_PSPT_ID").val();
		psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		fornt = $("#custInfo_AGENT_FRONTBASE64").val();
	}
	
	//携入标识
	var npTag=$("#NPTag").val();
	
	//固话标识   1是固话  非1 不是固话
	var wxTag=$("#WXTag").val();
	
	if(npTag == 1||wxTag == 1){
		MessageBox.success("提示","携入客户及固话暂不提供人像比对服务", null, null, null);
		return false;
	}

	if(psptId == "" || psptType=="" || custName == ""){
		if(picid == "custInfo_PIC_ID"){
			MessageBox.alert("提示", "请查询客户资料或不需要摄像");
			return false;
		}else{
			MessageBox.alert("提示", "请扫描或选择经办人证件类型");
			return false;
		}
	}
	
	
	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>'+'</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+$("#AUTH_SERIAL_NUMBER").val()+'</phone>');
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>'+'</op_time>');
	
	bossOriginalXml.push('	<busi_list>');
	bossOriginalXml.push('		<busi_info>');
	bossOriginalXml.push('			<op_code>'+'</op_code>');
	bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
	bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
	bossOriginalXml.push('		</busi_info>');
	bossOriginalXml.push('	</busi_list>');

	bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
	bossOriginalXml.push('	<id_card>'+'</id_card>');
	bossOriginalXml.push('	<cust_name>'+'</cust_name>');
	bossOriginalXml.push('	<copy_flag></copy_flag>');
	bossOriginalXml.push('	<agm_flag></agm_flag>');
	bossOriginalXml.push('</req>');
	var bossOriginaStr = bossOriginalXml.join("");
	//调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	//获取保存结果
	var result = makeActiveX.IdentificationInfo.result;			
	//获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	
	if(picID != ''){
		MessageBox.alert("提示", "人像摄像成功", function (btn) {
            if ("ok" === btn) {
                //获取照片流
                var picStream = makeActiveX.IdentificationInfo.pic_stream;
                picStream = escape (encodeURIComponent(picStream));
                if("0" == result){
                    $("#"+picid).val(picID);
                    $("#"+picstream).val(picStream);
                    var param = "&BLACK_TRADE_TYPE=310";
                    param += "&CERT_ID="+psptId;
                    param += "&CERT_NAME="+custName;
                    param += "&CERT_TYPE="+psptType;
                    param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));
                    param += "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                    $.beginPageLoading("正在进行人像比对。。。");
                    $.ajax.submit(null, 'cmpPicInfo', param, '', function(data){
                            $.endPageLoading();
                            if(data && data.get("X_RESULTCODE")== "0"){
                                MessageBox.success("成功提示","人像比对成功", null, null, null);
                                return true;
                            }else if(data && data.get("X_RESULTCODE")== "1"){
                                $("#"+picid).val("ERROR");
                                $("#"+picstream).val(data.get("X_RESULTINFO"));
                                MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
                                return false;
                            }
                        },
                        function(error_code,error_info,derror){
                            $("#"+picid).val("ERROR");
                            $("#"+picstream).val("人像比对失败,请重新拍摄");
                            $.endPageLoading();
                            //showDetailErrorInfo(error_code,error_info,derror);
                            MessageBox.error("告警提示","人像比对失败,请重新拍摄",null, null, null, null);
                        });
                }else{
                    MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
                }
            }
        });
	}else{
		MessageBox.alert("提示", "人像摄像失败");
		return false;
	}	
}



/***
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 客户摄像
 * @author zhuoyigzhi
 * @date 20170801
 */
function changeMethod1(){
	$("#span_CUST").css("display","");
	//经办人隐藏
	$("#span_AGENT").css("display","none");
	
	//经办人名称
	$("#custInfo_AGENT_CUST_NAME").val("");
	//经办人证件号码
	$("#custInfo_AGENT_PSPT_ID").val("");
	//经办人证件类型
	$("#custInfo_AGENT_PSPT_TYPE_CODE").val("");
	
	//经办人照片id
	$("#custInfo_AGENT_PIC_ID").val("");
	
	//拍摄经办人人像照片流
	$("#custInfo_AGENT_PIC_STREAM").val("");
	//经办人身份证反面照
	$("#custInfo_AGENT_BACKBASE64").val("");
	//经办人身份证正面照
	$("#custInfo_AGENT_FRONTBASE64").val("");
}
/***
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 经办人摄像
 * @author zhuoyigzhi
 * @date 20170802
 */
function changeMethod2(){	
	//隐藏客户摄像
	$("#span_CUST").css("display","none");
	//经办人 显示
	
	$("#span_AGENT").css("display","");
	
	//客户摄像id
	$("#custInfo_PIC_ID").val("");
	//拍摄人像照片流
	$("#custInfo_PIC_STREAM").val("");
}
/**
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * @author zhuoyingzhi
 * @date 20170802
 * 扫描读取经办人身份证信息
 */
function clickScanPspt2(){
	var psptTypeCode=$("#custInfo_PSPT_TYPE_CODE").val();
	var needpicinfo = null;
	var tag = (psptTypeCode=="E" || psptTypeCode=="G" 
		|| psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L")? true : false;
	if(tag){
		//客户证件类型为单位证件
		needpicinfo = "PIC_INFO";
	}
	getMsgByEForm("custInfo_AGENT_PSPT_ID","custInfo_AGENT_CUST_NAME",needpicinfo,null,null,null,null,null);
	
}

function delResTable(){
	
	var needChangePhone = $('#NEED_CHANGE_PHONE').val(); //用户是否需要更换新号码
	
	if(needChangePhone=="MUST_MAITAIN")	//	原号复机
	{
		//修改表格号码校验状态
		var rowEdit = $.ajax.buildJsonData("userResInfosPart");
    	rowEdit["col_CHECK_RES_CODE"]=$('#AUTH_SERIAL_NUMBER').val()
    		+ '<span class="e_space"></span>'
    		+ '<button jwcid="@Any" class="e_button-blue e_button-r e_button-s"> '
    		+ '<i class="e_ico-ok"></i><span >无需校验</span></button>';	
		userResTable.updateRow(rowEdit,0);
		
		//屏蔽新号码校验区域
		$("#_number").css('display','none');
	}
}


