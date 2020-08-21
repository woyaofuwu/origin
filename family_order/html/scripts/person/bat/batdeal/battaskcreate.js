
/***********************************common functions*****************************************/

function changeBatchOperType(obj) {
	selectedElements.selectedEls = new $.DatasetList();
	obj = $(obj);
	var batchOperName = $("#"+obj.attr("id")+" :selected").text();
	$('#BATCH_TASK_NAME').val(batchOperName);
	var param = "&BATCH_OPER_TYPE="+obj.val();
	if('COUNTRYNETACTIVE'==obj.val()||'SALEACTIVE'==obj.val()){
		param=param+"&PRODUCT_ID=69900490";
	}else if('SALEACTIVEEND'==obj.val()){
		param=param+"&PRE_FLAG=1";
	}else if('CREATEPREUSER'==obj.val()){
		param=param+"&PRE_FLAG=1";
	}else if('BATACTIVECREATEUSER'==obj.val()){
		param=param+"&PRE_FLAG=6";
	}else if('CREATEPREUSER_PWLW'==obj.val()){
		param=param+"&PRE_FLAG=2";
	}else if('CREATEPREUSER_M2M'==obj.val()){
		param=param+"&PRE_FLAG=1";
	}else if('BATCREATEFIXEDUSER'==obj.val()){
		param=param+"&PRE_FLAG=3";
	}else if('BATCREATETRUNKUSER'==obj.val()){
		param=param+"&PRE_FLAG=5";
	}else if('CREATEPRETDUSER'==obj.val()){
		param=param+"&PRE_FLAG=4";
	}else if('CREATEPREUSER_SCHOOL'==obj.val()){
		param=param+"&PRE_FLAG=9";
	}else if('SERVICECHG'==obj.val()){
		param=param+"&SVC_FLAG=1";
	}else if('SUSPENDSERVICE'==obj.val()){
		param=param+"&SVC_FLAG=2";
	}else if('RESUMESERVICE'==obj.val()){
		param=param+"&SVC_FLAG=3";
	}else if('SERVICECHGSPEC'==obj.val()){
		param=param+"&SVC_FLAG=4";
	}else if('BATPCRFCHG'==obj.val()){
		param=param+"&SVC_FLAG=1";
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit(null,null,param,'BAT_COND',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function createbattask() {
	
	if($("#cond_BATCH_OPER_TYPE").val()=="GRPDISCNTCHGSPEC"){
		$('#START_DATE1').attr("maxName","");
	}
	
	//加入对前台新建任务和子页面必填参数的校验 
	if(!$.validate.verifyAll("QueryCondPart")){
		return false;
	}
	
	
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	var checkResult = eval(batch_oper_type+"()");
	
	if(checkResult == false){
		return false;
	}
	
	if(!$.validate.verifyAll("BatCondPart")){
		return false;
	}
	//REQ201612020008 关于生日数据优化的需求
	if($("#cond_BATCH_OPER_TYPE").val()=="CREATEPREUSER_PWLW"||$("#cond_BATCH_OPER_TYPE").val()=="CREATEPREUSER_M2M"){
		var birthday = $("#BIRTHDAY").val();
		if(birthday == "") { 
		   var strdata = new Wade.DataMap($("#CODING_STR").val());			
		   strdata.put('BIRTHDAY', "1900-01-01");
		   $("#CODING_STR").val(strdata);
		}		 
	}
	//REQ201612020008 关于生日数据优化的需求
	
	/**
	 * 物联网策略变更校验
	 */
	if(batch_oper_type=="BATPCRFCHG"){
		
		var service_id = $("#SERVICE").val();
		if(service_id == undefined || service_id == ""){
			alert("请选择策略服务！");
			return false ;
		}
		
		var selected = $(".checkbox:checked");
		for(var x = 0;x<selected.length-1;x++){
			var indexValue = selected.eq(x);
			var father = indexValue.parent();
			var bros = father.next();
			var value = bros.children().children().val();
			for(var y = x+1;y<selected.length;y++){
				var index_other = selected.eq(y);
				var father_other = index_other.parent();
				var bro = father_other.next();
				var value_other = bro.children().children().val();
				if(value == value_other){
					alert("策略编码唯一，不可重复选择！");
			return false ;
				}
				
			}
			
		}
		if(selected.length<1){
			alert("请选择策略！");
			return false;
		}else if(selected.length >= 1){
			for(x = 0;x<selected.length;x++){
				var indexValue = selected.eq(x);
				var father = indexValue.parent();
				var bros = father.nextAll();
				for(var y = 0;y<bros.length;y++){
					var realValue = bros.eq(y).children().children().val();
					var realName = bros.eq(y).children().children().attr("id");
					if(realValue == undefined || realValue == ""){
						if(realName=="SERVICE_CODE"){
							alert("策略编码不能为空!");
						}
						if(realName=="OPER_TYPE"){
							alert("操作类型不能为空!");
						}
						if(realName=="USAGE_STATE"){
							alert("配额状态不能为空!");
						}
						if(realName=="BILLING_TYPE"){
							alert("计费类型不能为空!");
						}

						return false  ;
					}
						
				}
			}
		}
		
		
	}

	
	
	/**
	 * REQ201707170020_新增物联卡开户人像采集功能
	 * <br/>
	 * @author zhuoyingzhi
	 * @date 20170814
	 */
	if(batch_oper_type == "CREATEPREUSER_PWLW")
	{
		var idCodingStr = new Wade.DataMap($("#CODING_STR").val());
		var strChenmoqi = "0";
		var bChenmoqi = $("#CHEN_MO_QI")[0].checked;
		if(bChenmoqi == true)
		{
			strChenmoqi = "1";
			idCodingStr.put('ACCT_TAG', "2");
		}
		//alert(strChenmoqi);
		idCodingStr.put('CHEN_MO_QI', strChenmoqi);
		$("#CODING_STR").val(idCodingStr);
	    
		var cmpTag = "1";
		$.ajax.submit(null,'isBatCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
		
		if(cmpTag == "0"){		
			var picid = $("#custInfo_PIC_ID").val();
			
			if(null != picid && picid == "ERROR"){
				//客户摄像失败
				MessageBox.error("告警提示","客户"+$("#custInfo_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			//客户证件类型
			var psptTypeCode=$("#PSPT_TYPE_CODE").val();
			
			//经办人摄像id
			var agentpicid = $("#custInfo_AGENT_PIC_ID").val();
			//经办人证件类型
			var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
			
			if((psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "3" ) && picid == ""){
				/*****客户未进行摄像******/
				//经办人名称
			    var  custName = $("#AGENT_CUST_NAME").val();
			    //经办人证件号码
				var  psptId = $("#AGENT_PSPT_ID").val();
				//经办人证件地址
				var  agentPsptAddr= $("#AGENT_PSPT_ADDR").val();

                if (agentpicid){// 经办人已摄像
                    if (agentpicid == "ERROR"){// 摄像失败
                        MessageBox.error("告警提示", "经办人" + $("#custInfo_AGENT_PIC_STREAM").val());
                        return false;
                    }
                }else {// 经办人未摄像
                    MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                    return false;
                }
			}

			if (psptTypeCode == "D" || psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "L" || psptTypeCode == "M"){
                if (agentTypeCode == ""){// 经办人证件类型为空
                    MessageBox.error("告警提示", "请选择经办人的证件类型");
                    return false;
                }

                // 客户证件类型是单位类型，经办人证件选择 身份证/军人身份证需要进行人像比对
                if(agentTypeCode == "0" || agentTypeCode == "1" || agentTypeCode == "3"){
                    if (agentpicid){// 经办人已摄像
                        if (agentpicid == "ERROR"){// 摄像失败
                            MessageBox.error("告警提示", "经办人" + $("#custInfo_AGENT_PIC_STREAM").val());
                            return false;
                        }
                    }else {// 经办人未摄像
                        MessageBox.error("告警提示", "请经办人摄像！");
                        return false;
                    }
                }
			}
		}
	}
	/************************************************/
	
	/**
	 * REQ201806190020_新增行业应用卡批量开户人像比对功能
	 * @author zhuoyingzhi
	 * @date 20180718
	 */
	 if(!createpreuserM2midentification(batch_oper_type)){
		 return false;
	 }
	
	
	var truthBeTold = confirm("确定要提交批量信息吗?");
	
	if (!truthBeTold) {
		return false;
	}  
	
	$.beginPageLoading("业务受理中...");
	
	var codestr = $("#CODING_STR").val();
	var batch_task_name =  $("#TASK_NAME").val();
	//var batch_oper_name =  $("#cond_BATCH_OPER_TYPE")[0].options($("#cond_BATCH_OPER_TYPE")[0].selectedIndex).text;
	var batch_oper_name = $('#cond_BATCH_OPER_TYPE option:selected').text();
	var start_date=$("#START_DATE1").val();
	var end_date=$("#END_DATE1").val();
	var remark = $('#REMARK').val();
	var auditNo = $('#AUDIT_NO').val();
	
	var productId = $("#NEW_PRODUCT_ID").val();
	if(typeof(productId) == 'undefined'){
		productId = '';
	}
	
	
	codestr = encodeURIComponent(codestr);
	var condition1 = "";
	var param = "&CODEINGSTR=" + codestr;
	    param =  param + "&BATCH_TASK_NAME="+batch_task_name;
	    param =  param + "&BATCH_OPER_CODE="+batch_oper_type;
	    param =  param + "&BATCH_OPER_NAME="+batch_oper_name;
	    param =  param + "&START_DATE="+start_date;
	    param =  param + "&END_DATE="+end_date;
	    param =  param + "&REMARK="+remark;
	    param =  param + "&AUDIT_NO="+auditNo;
	    param =  param + "&PRODUCT_ID="+productId;
	    if("MODIFYACYCINFO" == batch_oper_type){
			CONDITION2 = $("#IMPORT_COND").val();
			param =  param + "&CONDITION2="+CONDITION2;
		}
	    


	   

	 



	    var attrs = "";
		if(batch_oper_type=="BATPCRFCHG"){
			var selected = $(".checkbox:checked");
			for(var x = 0;x<selected.length;x++){
				var indexValue = selected.eq(x);
				var father = indexValue.parent();
				var bros = father.nextAll();
				var text = "{";
				for(var y = 0;y<bros.length;y++){
					var realValue = bros.eq(y).children().children().val();
					var realName = bros.eq(y).children().children().attr("id");
					if('START_DATE'==realName.substring(0,10)){
						text = text + '"START_DATE":"'+realValue+'",';
					}else if('END_DATE'==realName.substring(0,8)){
						text = text + '"END_DATE":"'+realValue+'",';
					}else{
						text = text + '"'+realName+'":"'+realValue+'",';
					}
					
				}
				var leng = text.length;
				text = text.substring(0,leng-1);
				text = text+"},";
				attrs = attrs + text;
			}
			var long = attrs.length;
			attrs = attrs.substring(0,long-1);
			param = param + "&attrs=["+attrs+"]"+"&SERVICE="+$("#SERVICE").val();
			
			var tradeTypeTag = $("#TRADE_TYPE_TAG").val();
			if(tradeTypeTag != null && tradeTypeTag != ""){
				param = param + "&TRADE_TYPE_TAG=" + tradeTypeTag;
			}
		}
	
	$.ajax.submit(null,"submitBatTask",param,'',function(data){
		$.endPageLoading();
		var title= "批量任务创建结果!";
		var batchTaskId=data.get("BATCH_TASK_ID");
		var content=  "批量信息创建成功,<br/> 点击[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回本页面，点击[<a href=\"#nogo\" onclick=\"javascript:openNav('批量数据导入','bat.batdataimport.BatDataImport', 'pageInit','&cond_BATCH_TASK_ID="+batchTaskId+"');\">批量数据导入</a>]到数据导入页面<br/>批量任务流水号(BATCH_TASK_ID)："+batchTaskId ;
		//var content=  "批量信息创建成功,<br/> 点击[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回本页面，点击[<a href=\"#nogo\" onclick=\"javascript:openNav('批量数据导入','bat.batdataimport.BatDataImport', '','');window.closeNavByTitle('批量任务创建')\">批量数据导入</a>]到数据导入页面<br/>批量任务流水号(BATCH_TASK_ID)："+batchTaskId ;
		
		$.showSucMessage(title,content);
		resetParams();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function TESTPHONERETURN(){
	return true;
}


function getOtherParam(){
	return "&BATCH_OPER_TYPE="+$("#cond_BATCH_OPER_TYPE").val();
}

function resetParams(){
	$.beginPageLoading("数据重置中...");
	$.ajax.submit('','initBatDealInput','','QueryCondPart,BAT_COND',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function createPreUserCommon(tradeTypeCode){

	if(!selectedElements.checkForcePackage())
		return false;
	var data = selectedElements.getSubmitData();
	
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'500');
	if(!prodCheck){
		return false;
	}
	
	var bankcode = $("#BANK_CODE").val();
	var paymodecode = $("#PAY_MODE_CODE").val();
	if(paymodecode != '0') 
	{
		if(bankcode == ''){
			alert('银行名称不能为空！');
			return false;
		}
	}
	var payName =  $("#PAY_MODE_CODE")[0].options($("#PAY_MODE_CODE")[0].selectedIndex).text;
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	var productName = $("#PRODUCT_NAME").val();
	var custname = $("#CUST_NAME").val();
	var psptId = $("#PSPT_ID").val();
	var address = $("#ADDRESS").val();
	var postcode = $("#POST_CODE").val();
	var linkphone = $("#LINK_PHONE").val();
	var usertypecode = $("#USER_TYPE_CODE").val();
	var accttag = $("#ACCT_TAG").val();
	var bankacctno = $("#BANK_ACCT_NO").val();
	var bankname = $("#BANK_CODE").text();
	var superBankCode =  $("#SUPER_BANK_CODE").val();

	var otherinfo = new Wade.DataMap();
	
	if(brand!=""){otherinfo.put('BRAND', brand);}
	if(brandCode!=""){otherinfo.put('BRAND_CODE', brandCode);}
	if(productName!=""){otherinfo.put('PRODUCT_NAME', productName);}
	if(payName!=""){otherinfo.put('PAY_NAME', payName);}
	if(custname!=""){otherinfo.put('CUST_NAME', custname);}
	if(bankname!=""){custData.put('BANK_NAME', bankname);}
	if(psptId!=""){otherinfo.put('PSPT_ID', psptId);}
	if(address!=""){otherinfo.put('ADDRESS', address);}
	if(postcode!=""){otherinfo.put('POST_CODE', postcode);}
	if(linkphone!=""){otherinfo.put('LINK_PHONE', linkphone);}
	if(usertypecode!=""){otherinfo.put('USER_TYPE_CODE', usertypecode);}
	if(paymodecode!=""){otherinfo.put('PAY_MODE_CODE', paymodecode);}
	if(accttag!=""){otherinfo.put('ACCT_TAG', accttag);}
	if(bankcode!=""){otherinfo.put('BANK_CODE', bankcode);}
	if(bankacctno!=""){otherinfo.put('BANK_ACCT_NO', bankacctno);}
	if(superBankCode!=""){otherinfo.put('SUPER_BANK_CODE', superBankCode);}
	
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	if(batch_oper_type == 'CREATEPREUSER_PWLW'){
		var callingTypeCode = $("#CALLING_TYPE_CODE").val();
		if(callingTypeCode!=""){otherinfo.put('CALLING_TYPE_CODE', callingTypeCode);}
	}

	//REQ201502050013 放号政策调整需求 by songlm
	if(batch_oper_type == 'CREATEPREUSER'){
		var agentPresentFee = $("#AGENT_PRESENT_FEE").val();
		otherinfo.put('AGENT_PRESENT_FEE', agentPresentFee);
	}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
	/**
	 * REQ201609060001_2016年下半年测试卡功能优化（二）
	 * @author zhuoyingzhi
	 * 20160930
	 * 批量预开户 添加 测试卡类型
	 */
	if(batch_oper_type == "CREATEPREUSER"){
		var test_card_type=$("#TEST_CARD_TYPE").val();
		if(test_card_type != ""){
			otherinfo.put('TEST_CARD_TYPE', test_card_type);
		}
		

		/**
		 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求 chenxy3 2016-12-14
		 * 增加测试用户--使用人信息
		 * */
		if($("#USER_TYPE_CODE").val()=='A'){

			otherinfo.put('CUST_NAME', '中国移动通信集团海南有限公司'); 
			otherinfo.put('PSPT_ID', '91460000710920952X');
			otherinfo.put('PSPT_TYPE_CODE', 'E'); 
			otherinfo.put('PSPT_ADDR', '海南省海口市金龙路88号');
		}

		var usePsptId = $("#USE_PSPT_ID").val();
		var useCustName = $("#USE_CUSTNAME").val();
		var usePsptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
		var usePsptAddr =  $("#USE_PSPT_ADDR").val(); 
		if(usePsptId!=""){otherinfo.put('USE_PSPT_ID', usePsptId);}
		if(useCustName!=""){otherinfo.put('USE', useCustName);}
		if(usePsptTypeCode!=""){otherinfo.put('USE_PSPT_TYPE_CODE', usePsptTypeCode);}
		if(usePsptAddr!=""){otherinfo.put('USE_PSPT_ADDR', usePsptAddr); }
	}
  	$("#CODING_STR").val(otherinfo);	
  	
}

//行业应用卡批量开户
function createPreUser_M2M(tradeTypeCode){
	var data = selectedElements.getSubmitData();
	
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'10');
	if(!prodCheck){
		return false;
	}
	
	var otherinfo = new Wade.DataMap();
	
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	if(brand != "") { otherinfo.put('BRAND', brand); }
	//alert(brand);
	 
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	if(brandCode != "")	{ otherinfo.put('BRAND_CODE', brandCode); }
	
	var productName = $("#PRODUCT_NAME").val();
	if(productName != "") { otherinfo.put('PRODUCT_NAME', productName); }
	
	var custname = $("#CUST_NAME").val();
	if(custname != "") { otherinfo.put('CUST_NAME', custname); }
	
	var phone = $("#PHONE").val();
	if(phone != "") { otherinfo.put('PHONE', phone); }
	
	var usertypecode = $("#USER_TYPE_CODE").val();
	if(usertypecode != ""){ otherinfo.put('USER_TYPE_CODE', usertypecode); }
	
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	if(psptTypeCode != "") { otherinfo.put('PSPT_TYPE_CODE', psptTypeCode); }
	
	var psptId = $("#PSPT_ID").val();
	if( psptId!="" ) { otherinfo.put('PSPT_ID', psptId); }
	
	var birthday = $("#BIRTHDAY").val();
	if(birthday != "") { otherinfo.put('BIRTHDAY', birthday); }
	
	var psptAddr = $("#PSPT_ADDR").val();
	if(psptAddr != "") { otherinfo.put('PSPT_ADDR', psptAddr); }
	
	var psptEndDate = $("#PSPT_END_DATE").val();
	if(psptEndDate != "") { otherinfo.put('PSPT_END_DATE', psptEndDate); }
	
	var postAddress = $("#POST_ADDRESS").val();
	if(postAddress != "") { otherinfo.put('POST_ADDRESS', postAddress); }
	
	var postcode = $("#POST_CODE").val();
	if(postcode != "") { otherinfo.put('POST_CODE', postcode); }
	
	var agentCustName = $("#AGENT_CUST_NAME").val();
	if(agentCustName != "") { otherinfo.put('AGENT_CUST_NAME', agentCustName); }
	
	var agentPsptTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
	if(agentPsptTypeCode != "") { otherinfo.put('AGENT_PSPT_TYPE_CODE', agentPsptTypeCode); }
	
	var agentPsptId = $("#AGENT_PSPT_ID").val();
	if(agentPsptId != "") { otherinfo.put('AGENT_PSPT_ID', agentPsptId); }
	
	var agentPsptAddr = $("#AGENT_PSPT_ADDR").val();
	if(agentPsptAddr != "") { otherinfo.put('AGENT_PSPT_ADDR', agentPsptAddr); }
	
	var user = $("#USE").val();
	if(user != "") { otherinfo.put('USE', user); }
	
	var usePsptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
	if(usePsptTypeCode != "") { otherinfo.put('USE_PSPT_TYPE_CODE', usePsptTypeCode); }
	
	var usePsptId = $("#USE_PSPT_ID").val();
	if(usePsptId != "") { otherinfo.put('USE_PSPT_ID', usePsptId); }
	
	var usePsptAddr = $("#USE_PSPT_ADDR").val();
	if(usePsptAddr != "") { otherinfo.put('USE_PSPT_ADDR', usePsptAddr); }
	
	var rsrvstr2 = $("#RSRV_STR2").val();
	if(rsrvstr2 != "") { otherinfo.put('RSRV_STR2', rsrvstr2); }
	
	var rsrvstr3 = $("#RSRV_STR3").val();
	if(rsrvstr3 != "") { otherinfo.put('RSRV_STR3', rsrvstr3); }
	
	var rsrvstr4 = $("#RSRV_STR4").val();
	if(rsrvstr4 != "") { otherinfo.put('RSRV_STR4', rsrvstr4); }
	
	var rsrvstr5 = $("#RSRV_STR5").val();
	if(rsrvstr5 != "") { otherinfo.put('RSRV_STR5', rsrvstr5); }
	
	var payName = $("#PAY_NAME").val();
	if(payName != "") { otherinfo.put('PAY_NAME', payName); }
	
	var payModeCode = $("#PAY_MODE_CODE").val();
	if(payModeCode != "") { otherinfo.put('PAY_MODE_CODE', payModeCode); }
	
	var acctDay = $("#ACCT_DAY_YW").val();
	if(acctDay != "") { otherinfo.put('ACCT_DAY', acctDay); }
	
	var superBankCode = $("#SUPER_BANK_CODE").val();
	if(superBankCode != "") { otherinfo.put('SUPER_BANK_CODE', superBankCode); }
	
	var bank = $("#BANK").val();
	if(bank != "") { otherinfo.put('BANK', bank); }
	
	var bankCode = $("#BANK_CODE").val();
	if(bankCode != "") { otherinfo.put('BANK_CODE', bankCode); }
	
	var bankAcctNo = $("#BANK_ACCT_NO").val();
	if(bankAcctNo != "") { otherinfo.put('BANK_ACCT_NO', bankAcctNo); }
	
	var userPassword = $("#USER_PASSWD").val();
	if(userPassword != "") { otherinfo.put('USER_PASSWD', userPassword); }
	
	//liquan
	var legalperson = $("#legalperson").val();
	if(legalperson != "") { otherinfo.put('legalperson', legalperson); }
	
	var startdate = $("#startdate").val();
	if(startdate != "") { otherinfo.put('startdate', startdate); }
	
	var termstartdate = $("#termstartdate").val();
	if(termstartdate != "") { otherinfo.put('termstartdate', termstartdate); }
	
	var termenddate = $("#termenddate").val();
	if(termenddate != "") { otherinfo.put('termenddate', termenddate); }
	
	var orgtype = $("orgtype").val();
	if(orgtype != "") { otherinfo.put('orgtype', orgtype); }
	
	var effectiveDate = $("#effectiveDate").val();
	if(effectiveDate != "") { otherinfo.put('effectiveDate', effectiveDate); }
	
	var expirationDate = $("#expirationDate").val();
	if(expirationDate != "") { otherinfo.put('expirationDate', expirationDate); }
	//liquan
	//otherinfo.put('TRADE_TYPE_CODE', "10");
	otherinfo.put('ACCT_TAG', "2");
	otherinfo.put('OPEN_MODE', "0");
	otherinfo.put('IS_REAL_NAME', "1");
	otherinfo.put('REAL_NAME', "1");
	otherinfo.put('ACTIVE_TAG', "0");
	otherinfo.put('CHECK_DEPART_ID', $("#CHECK_DEPART_ID").val());
	otherinfo.put('OPEN_TYPE', ''); //开户类型
	otherinfo.put('M2M_FLAG', '0'); //是否物联网类型，0不是，1是
	otherinfo.put('M2M_TAG', '1'); //是否M2M类型，0不是，1是
	
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	if(batch_oper_type == 'CREATEPREUSER_M2M'){
		var callingTypeCode = $("#CALLING_TYPE_CODE").val();
		if(callingTypeCode != "") { otherinfo.put('CALLING_TYPE_CODE', callingTypeCode); }
		
		/**
		 * REQ201806190020_新增行业应用卡批量开户人像比对功能
		 * <br/>
		 * @author zhuoyingzhi
		 * @date 20180718
		 */		
		//经办人摄像标识
		var custInfo_AGENT_PIC_ID=$("#custInfo_AGENT_PIC_ID").val();
		if(custInfo_AGENT_PIC_ID!=""){otherinfo.put('custInfo_AGENT_PIC_ID', custInfo_AGENT_PIC_ID);}
		
		//责任人摄像标识
		var custInfo_RSRV_STR4_PIC_ID=$("#custInfo_RSRV_STR4_PIC_ID").val();
		if(custInfo_RSRV_STR4_PIC_ID!=""){otherinfo.put('custInfo_RSRV_STR4_PIC_ID', custInfo_RSRV_STR4_PIC_ID);}
		/********************************/
	}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
  	$("#CODING_STR").val(otherinfo);
}

//物联网开户
function createPreUser_PWLW(tradeTypeCode){
	
	var data = selectedElements.getSubmitData();
	
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'10');
	if(!prodCheck){
		return false;
	}
	
	var otherinfo = new Wade.DataMap();
	
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	if(brand != "") { otherinfo.put('BRAND', brand); }
	//alert(brand);
	 
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	if(brandCode != "")	{ otherinfo.put('BRAND_CODE', brandCode); }
	
	var productName = $("#PRODUCT_NAME").val();
	if(productName != "") { otherinfo.put('PRODUCT_NAME', productName); }
	
	var custname = $("#CUST_NAME").val();
	if(custname != "") { otherinfo.put('CUST_NAME', custname); }
	
	var phone = $("#PHONE").val();
	if(phone != "") { otherinfo.put('PHONE', phone); }
	
	var usertypecode = $("#USER_TYPE_CODE").val();
	if(usertypecode != ""){ otherinfo.put('USER_TYPE_CODE', usertypecode); }
	
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	if(psptTypeCode != "") { otherinfo.put('PSPT_TYPE_CODE', psptTypeCode); }
	
	var psptId = $("#PSPT_ID").val();
	if( psptId!="" ) { otherinfo.put('PSPT_ID', psptId); }
	
	var birthday = $("#BIRTHDAY").val();
	if(birthday != "") { otherinfo.put('BIRTHDAY', birthday); }
	
	var psptAddr = $("#PSPT_ADDR").val();
	if(psptAddr != "") { otherinfo.put('PSPT_ADDR', psptAddr); }
	
	var psptEndDate = $("#PSPT_END_DATE").val();
	if(psptEndDate != "") { otherinfo.put('PSPT_END_DATE', psptEndDate); }
	
	var postAddress = $("#POST_ADDRESS").val();
	if(postAddress != "") { otherinfo.put('POST_ADDRESS', postAddress); }
	
	var postcode = $("#POST_CODE").val();
	if(postcode != "") { otherinfo.put('POST_CODE', postcode); }
	
	var agentCustName = $("#AGENT_CUST_NAME").val();
	if(agentCustName != "") { otherinfo.put('AGENT_CUST_NAME', agentCustName); }
	
	var agentPsptTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
	if(agentPsptTypeCode != "") { otherinfo.put('AGENT_PSPT_TYPE_CODE', agentPsptTypeCode); }
	
	var agentPsptId = $("#AGENT_PSPT_ID").val();
	if(agentPsptId != "") { otherinfo.put('AGENT_PSPT_ID', agentPsptId); }
	
	var agentPsptAddr = $("#AGENT_PSPT_ADDR").val();
	if(agentPsptAddr != "") { otherinfo.put('AGENT_PSPT_ADDR', agentPsptAddr); }
	
	var rsrvstr2 = $("#RSRV_STR2").val();
	if(rsrvstr2 != "") { otherinfo.put('RSRV_STR2', rsrvstr2); }
	
	var rsrvstr3 = $("#RSRV_STR3").val();
	if(rsrvstr3 != "") { otherinfo.put('RSRV_STR3', rsrvstr3); }
	
	var rsrvstr4 = $("#RSRV_STR4").val();
	if(rsrvstr4 != "") { otherinfo.put('RSRV_STR4', rsrvstr4); }
	
	var rsrvstr5 = $("#RSRV_STR5").val();
	if(rsrvstr5 != "") { otherinfo.put('RSRV_STR5', rsrvstr5); }
	
	//liquan REQ201611070004关于物联网业务相关开户界面的优化需求	
	var user = $("#USE").val();
	if(user != "") { otherinfo.put('USE', user); }
	
	var usePsptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
	if(usePsptTypeCode != "") { otherinfo.put('USE_PSPT_TYPE_CODE', usePsptTypeCode); }
	
	var usePsptId = $("#USE_PSPT_ID").val();
	if(usePsptId != "") { otherinfo.put('USE_PSPT_ID', usePsptId); }
	
	var usePsptAddr = $("#USE_PSPT_ADDR").val();
	if(usePsptAddr != "") { otherinfo.put('USE_PSPT_ADDR', usePsptAddr); }
	//liquan REQ201611070004关于物联网业务相关开户界面的优化需求
	
	
	//liquan
	var legalperson = $("#legalperson").val();
	if(legalperson != "") { otherinfo.put('legalperson', legalperson); }
	
	var startdate = $("#startdate").val();
	if(startdate != "") { otherinfo.put('startdate', startdate); }
	
	var termstartdate = $("#termstartdate").val();
	if(termstartdate != "") { otherinfo.put('termstartdate', termstartdate); }
	
	var termenddate = $("#termenddate").val();
	if(termenddate != "") { otherinfo.put('termenddate', termenddate); }
	
	var orgtype = $("#orgtype").val();
	if(orgtype != "") { otherinfo.put('orgtype', orgtype); }
	
	var effectiveDate = $("#effectiveDate").val();
	if(effectiveDate != "") { otherinfo.put('effectiveDate', effectiveDate); }
	
	var expirationDate = $("#expirationDate").val();
	if(expirationDate != "") { otherinfo.put('expirationDate', expirationDate); }
	//liquan
	var payName = $("#PAY_NAME").val();
	if(payName != "") { otherinfo.put('PAY_NAME', payName); }
	
	var payModeCode = $("#PAY_MODE_CODE").val();
	if(payModeCode != "") { otherinfo.put('PAY_MODE_CODE', payModeCode); }
	
	var acctDay = $("#ACCT_DAY_YW").val();
	if(acctDay != "") { otherinfo.put('ACCT_DAY', acctDay); }
	
	var superBankCode = $("#SUPER_BANK_CODE").val();
	if(superBankCode != "") { otherinfo.put('SUPER_BANK_CODE', superBankCode); }
	
	var bank = $("#BANK").val();
	if(bank != "") { otherinfo.put('BANK', bank); }
	
	var bankCode = $("#BANK_CODE").val();
	if(bankCode != "") { otherinfo.put('BANK_CODE', bankCode); }
	
	var bankAcctNo = $("#BANK_ACCT_NO").val();
	if(bankAcctNo != "") { otherinfo.put('BANK_ACCT_NO', bankAcctNo); }
	
	var userPassword = $("#USER_PASSWD").val();
	if(userPassword != "") { otherinfo.put('USER_PASSWD', userPassword); }
	
	otherinfo.put('ACCT_TAG', "0");
	otherinfo.put('OPEN_MODE', "0");
	otherinfo.put('IS_REAL_NAME', "1");
	otherinfo.put('REAL_NAME', "1");
	otherinfo.put('ACTIVE_TAG', "0");
	otherinfo.put('CHECK_DEPART_ID', $("#CHECK_DEPART_ID").val());
	otherinfo.put('M2M_FLAG', '1'); //是否物联网类型，0不是，1是
	otherinfo.put('M2M_TAG', '0'); //是否M2M类型，0不是，1是
	
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	if(batch_oper_type == 'CREATEPREUSER_PWLW'){
		var callingTypeCode = $("#CALLING_TYPE_CODE").val();
		if(callingTypeCode != "") { otherinfo.put('CALLING_TYPE_CODE', callingTypeCode); }
		
		/**
		 * REQ201707170020_新增物联卡开户人像采集功能
		 * <br/>
		 * @author zhuoyingzhi
		 * @date 20170825
		 */
		//客户摄像标识
		var custInfo_PIC_ID=$("#custInfo_PIC_ID").val();
		if(custInfo_PIC_ID!=""){otherinfo.put('custInfo_PIC_ID', custInfo_PIC_ID); }
		
		//经办人摄像标识
		var custInfo_AGENT_PIC_ID=$("#custInfo_AGENT_PIC_ID").val();
		if(custInfo_AGENT_PIC_ID!=""){otherinfo.put('custInfo_AGENT_PIC_ID', custInfo_AGENT_PIC_ID);}
		/********************************/
	}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
  	$("#CODING_STR").val(otherinfo);
}

//批量商务宽带开户
function CREATEPREUSER_BNBD() 
{
	
	if(!$.validate.verifyAll("MustInfoPart"))
	{
		return false;
	}
	
	var otherinfo = new Wade.DataMap();
	var strSn = $("#SN").val();
	if(strSn != "") { otherinfo.put('SERIAL_NUMBER', strSn); }
	
	var strCphone = $("#CONTACT_PHONE").val();
	if(strCphone != "") { otherinfo.put('CONTACT_PHONE', strCphone); }
	
	var strCn = $("#CONTACT").val();
	if(strCn != "") { otherinfo.put('CONTACT', strCn); }
	
	var strPn = $("#PHONE").val();
	if(strPn != "") { otherinfo.put('PHONE', strPn); }
	
	$("#CODING_STR").val(otherinfo);
}
/***********************************business use functions*****************************************/

//批量预开户
function CREATEPREUSER() {
	return createPreUserCommon('500');	
}

//批量预开户
function CREATEPREUSER_SCHOOL() {
	return createPreUserCommon('500');	
}

//批量买断开户
function BATACTIVECREATEUSER() {
	
	var data = selectedElements.getSubmitData();
	
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'700');
	if(!prodCheck){
		return false;
	}
	
	var bankcode = $("#BANK_CODE").val();
	var paymodecode = $("#PAY_MODE_CODE").val();
	if(paymodecode != '0') 
	{
		if(bankcode == ''){
			alert('银行名称不能为空！');
			return false;
		}
	}
	
	var payName =  $("#PAY_MODE_CODE")[0].options($("#PAY_MODE_CODE")[0].selectedIndex).text;
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	var productName = $("#PRODUCT_NAME").val();
	var custname = $("#CUST_NAME").val();
	var psptId = $("#PSPT_ID").val();
	var address = $("#ADDRESS").val();
	var postcode = $("#POST_CODE").val();
	var linkphone = $("#LINK_PHONE").val();
	var usertypecode = $("#USER_TYPE_CODE").val();
	var accttag = $("#ACCT_TAG").val();
	var bankacctno = $("#BANK_ACCT_NO").val();
	var bankname = $("#BANK_CODE").text();
	var advanceFee = $("#ADVANCE_FEE").val();
	var superBankCode =  $("#SUPER_BANK_CODE").val();

	var otherinfo = new Wade.DataMap();
	
	if(brand!=""){otherinfo.put('BRAND', brand);}
	if(brandCode!=""){otherinfo.put('BRAND_CODE', brandCode);}
	if(productName!=""){otherinfo.put('PRODUCT_NAME', productName);}
	if(payName!=""){otherinfo.put('PAY_NAME', payName);}
	if(custname!=""){otherinfo.put('CUST_NAME', custname);}
	if(bankname!=""){custData.put('BANK_NAME', bankname);}
	if(psptId!=""){otherinfo.put('PSPT_ID', psptId);}
	if(address!=""){otherinfo.put('ADDRESS', address);}
	if(postcode!=""){otherinfo.put('POST_CODE', postcode);}
	if(linkphone!=""){otherinfo.put('LINK_PHONE', linkphone);}
	if(usertypecode!=""){otherinfo.put('USER_TYPE_CODE', usertypecode);}
	if(paymodecode!=""){otherinfo.put('PAY_MODE_CODE', paymodecode);}
	if(accttag!=""){otherinfo.put('ACCT_TAG', accttag);}
	if(bankcode!=""){otherinfo.put('BANK_CODE', bankcode);}
	if(bankacctno!=""){otherinfo.put('BANK_ACCT_NO', bankacctno);}
	if(advanceFee!=""){otherinfo.put('ADVANCE_FEE', advanceFee);}
	if(superBankCode!=""){otherinfo.put('SUPER_BANK_CODE', superBankCode);}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
  	$("#CODING_STR").val(otherinfo);	
}

//物联网批量开户
function CREATEPREUSER_PWLW() {
	return createPreUser_PWLW('10');	
}

//行业应用卡批量开户
function CREATEPREUSER_M2M() {
	return createPreUser_M2M('10');	
}

//无线固话批量预开户
function CREATEPRETDUSER() {
	return createPreUserCommon('3820');	
}

//固话批量装机
function BATCREATEFIXEDUSER(){
	var data = selectedElements.getSubmitData();
	
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'9750');
	if(!prodCheck){
		return false;
	}
	
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	var productName = $("#PRODUCT_NAME").val();
	
	var standAddressCode = $("#STAND_ADDRESS_CODE").val();
	var standAddress = $("#STAND_ADDRESS").val();
	var SIGN_PATH = $("#SIGN_PATH").val();
	
	var otherinfo = new Wade.DataMap();
	if(brand!=""){otherinfo.put('BRAND', brand);}
	if(brandCode!=""){otherinfo.put('BRAND_CODE', brandCode);}
	if(productName!=""){otherinfo.put('PRODUCT_NAME', productName);}
	
	if(standAddressCode!=""){otherinfo.put('STAND_ADDRESS_CODE',standAddressCode);}
	if(standAddress!=""){otherinfo.put('STAND_ADDRESS',standAddress);}
	if(SIGN_PATH!=""){otherinfo.put('SIGN_PATH',SIGN_PATH);}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
	$("#CODING_STR").val(otherinfo);	
}

//千群百号装机
function BATCREATETRUNKUSER(){
	var data = selectedElements.getSubmitData();
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}	
	if(data == '[]'){
		alert("产品不能为空");
		return false;
	}
	
	var prodCheck = checkProducts(data,'9751');
	if(!prodCheck){
		return false;
	}
	
	var brand = $("#PRODUCT_TYPE_CODE")[0].options($("#PRODUCT_TYPE_CODE")[0].selectedIndex).text;
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	var productName = $("#PRODUCT_NAME").val();
	
	var mainUserS = $("#MAIN_SERIAL_NUMBER").val();
	var standAddressCode = $("#STAND_ADDRESS_CODE").val();
	var standAddress = $("#STAND_ADDRESS").val();
	var SIGN_PATH = $("#SIGN_PATH").val();
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	var pspt = $("#PSPT_ID").val();
	var custName = $("#CUST_NAME").val();
	var contact = $("#CONTACT").val();
	var phone = $("#PHONE").val();
	var areaType = $("#AREA_TYPE").val();
	var clearModeType = $("#CLEAR_MODE_TYPE").val();
	var acctType = $("#ACCT_TYPE").val();
	
	var otherinfo = new Wade.DataMap();
	if(brand!=""){otherinfo.put('BRAND', brand);}
	if(brandCode!=""){otherinfo.put('BRAND_CODE', brandCode);}
	if(productName!=""){otherinfo.put('PRODUCT_NAME', productName);}
	
	if(standAddressCode!=""){otherinfo.put('STAND_ADDRESS_CODE',standAddressCode);}
	if(standAddress!=""){otherinfo.put('STAND_ADDRESS',standAddress);}
	if(SIGN_PATH!=""){otherinfo.put('SIGN_PATH',SIGN_PATH);}
	if(mainUserS!==""){otherinfo.put('MAIN_SERIAL_NUMBER',mainUserS);}
	if(psptTypeCode!==""){otherinfo.put('PSPT_TYPE_CODE',psptTypeCode);}
	if(pspt!==""){otherinfo.put('PSPT_ID',pspt);}
	if(custName!==""){otherinfo.put('CUST_NAME',custName);}
	if(contact!==""){otherinfo.put('CONTACT',contact);}
	if(phone!==""){otherinfo.put('PHONE',phone);}
	if(areaType!==""){otherinfo.put('AREA_TYPE',areaType);}
	if(clearModeType!==""){otherinfo.put('CLEAR_MODE_TYPE',clearModeType);}
	if(acctType!==""){otherinfo.put('ACCT_TYPE',acctType);}
	
	otherinfo.put("SELECTED_ELEMENTS",data);
	$("#CODING_STR").val(otherinfo);	
}

//千群百号追加
function BATAPPENDTRUNKUSER(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var data=new Wade.DataMap();
	data.put('MAIN_SERIAL_NUMBER',$("#MAIN_SERIAL_NUMBER").val());
	$("#CODING_STR").val(data);	
}

//批量办理套餐
function DISCNTCHG(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var DISCNT_CODE = $("#DISCNT_CODE").val();
	var MODIFY_TAG = $("#MODIFY_TAG").val();
	var data=new Wade.DataMap();
	data.put('DISCNT_CODE',DISCNT_CODE);
	data.put('MODIFY_TAG',MODIFY_TAG);
	data.put('START_DATE',$("#START_DATE").val());
	data.put('END_DATE',$("#END_DATE").val());
	$("#CODING_STR").val(data);	
}	

//批量办理特殊套餐
function DISCNTCHGSPEC(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var DISCNT_CODE = $("#DISCNT_CODE").val();
	var MODIFY_TAG = $("#MODIFY_TAG").val();
	var data=new Wade.DataMap();
	data.put('DISCNT_CODE',DISCNT_CODE);
	data.put('MODIFY_TAG',MODIFY_TAG);
	data.put('START_DATE',$("#START_DATE").val());
	data.put('END_DATE',$("#END_DATE").val());
	$("#CODING_STR").val(data);	
}

//批量办理特殊套餐
function GRPDISCNTCHGSPEC(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var END_DATE= $("#END_DATE").val();
	var data=new Wade.DataMap();
	data.put('END_DATE1',END_DATE);
	$("#CODING_STR").val(data);	
}
//批量办理家庭网套餐
function FAMILYNETDISCNT(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var data=new Wade.DataMap();
	data.put('MODIFY_TAG',$("#MODIFY_TAG").val());
	$("#CODING_STR").val(data);
}

//目标用户批量导入
function INFOMANAGE(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var data=new Wade.DataMap();
	data.put('MODIFY_TAG',$("#MODIFY_TAG").val());
	data.put('RSRV_VALUE',$("#RSRV_VALUE").val());
	data.put('START_DATE',$("#START_DATE").val());
	data.put('END_DATE',$("#END_DATE").val());
	data.put('ELEMENT_ID',$("#ELEMENT_ID").val());
	data.put('ELEMENT_TYPE',$("#ELEMENT_TYPE").val());
	data.put('RSRV_STR8',$("#RSRV_STR8").val());
	$("#CODING_STR").val(data);
}

//批量办理营销活动
function SALEACTIVE(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var PRODUCT_PACKAGE_ID = $("#PRODUCT_PACKAGE_ID").val();
	var PRODUCT_PACKAGE_NAME =  $("#PRODUCT_PACKAGE_ID")[0].options($("#PRODUCT_PACKAGE_ID")[0].selectedIndex).text;
	var CAMPN_TYPE = PRODUCT_PACKAGE_ID.split('-')[0];
	var PRODUCT_ID = PRODUCT_PACKAGE_ID.split('-')[1];
	var PACKAGE_ID = PRODUCT_PACKAGE_ID.split('-')[2];
	var data=new Wade.DataMap();
	data.put('CAMPN_TYPE',CAMPN_TYPE);
	data.put('PRODUCT_ID',PRODUCT_ID);
	data.put('PACKAGE_ID',PACKAGE_ID);
	/** micy@REQ201712080007 - 关于批量办理营销活动付费方式优化*/
    	var payMode = $("#PAY_MODE").val();
    	if (payMode != undefined && payMode != null && payMode != "")
    	{
        	data.put('PAY_MONEY_CODE',payMode);
    	}
    	/** micy@REQ201712080007 - 关于批量办理营销活动付费方式优化*/
	$("#CODING_STR").val(data);
}

function BATMOBILESTOP(){
	$("#CODING_STR").val('');
}

function SALEACTIVEEND(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var PRODUCT_PACKAGE_ID = $("#PRODUCT_PACKAGE_ID").val();
	var PRODUCT_PACKAGE_NAME =  $("#PRODUCT_PACKAGE_ID")[0].options($("#PRODUCT_PACKAGE_ID")[0].selectedIndex).text;
	var CAMPN_TYPE = PRODUCT_PACKAGE_ID.split('-')[0];
	var PRODUCT_ID = PRODUCT_PACKAGE_ID.split('-')[1];
	var PACKAGE_ID = PRODUCT_PACKAGE_ID.split('-')[2];
	var data=new Wade.DataMap();
	data.put('CAMPN_TYPE',CAMPN_TYPE);
	data.put('PRODUCT_ID',PRODUCT_ID);
	data.put('PACKAGE_ID',PACKAGE_ID);
	$("#CODING_STR").val(data);
}

//批量服务变更
function SERVICECHG(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var SERVICE_ID = $("#SERVICE_ID").val();
	var MODIFY_TAG = $("#MODIFY_TAG").val();
	var data=new Wade.DataMap();
	data.put('SERVICE_ID',SERVICE_ID);
	data.put('MODIFY_TAG',MODIFY_TAG);
	data.put('START_DATE',$("#START_DATE").val());
	data.put('END_DATE',$("#END_DATE").val());
	$("#CODING_STR").val(data);
}


//无条件特殊批量服务变更
function SERVICECHGSPEC(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var SERVICE_ID = $("#SERVICE_ID").val();
	var MODIFY_TAG = $("#MODIFY_TAG").val();
	var data=new Wade.DataMap();
	data.put('SERVICE_ID',SERVICE_ID);
	data.put('MODIFY_TAG',MODIFY_TAG);
	data.put('START_DATE',$("#START_DATE").val());
	data.put('END_DATE',$("#END_DATE").val());
	$("#CODING_STR").val(data);
}


//批量服务暂停
function SUSPENDSERVICE(){
	SERVICECHG();
}

//批量服务恢复
function RESUMESERVICE(){
	SERVICECHG();
}

//营销活动（入乡情网送农信通）批量办理
function COUNTRYNETACTIVE(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var PRODUCT_PACKAGE_ID = $("#PRODUCT_PACKAGE_ID").val();
	var PRODUCT_PACKAGE_NAME =  $("#PRODUCT_PACKAGE_ID")[0].options($("#PRODUCT_PACKAGE_ID")[0].selectedIndex).text;
	var PRODUCT_ID = PRODUCT_PACKAGE_ID.split('-')[0];
	var PACKAGE_ID = PRODUCT_PACKAGE_ID.split('-')[1];
	var data=new Wade.DataMap();
	data.put('PRODUCT_ID',PRODUCT_ID);
	data.put('PACKAGE_ID',PACKAGE_ID);
	$("#CODING_STR").val(data);
}

//批量业务平台业务
function BATPLATFORM(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var BIZ_TYPE_CODE = $("#BIZ_TYPE_CODE").val();
	var SP_CODE = $("#SP_CODE").val();
	var BIZ_CODE = $("#BIZ_CODE").val();
	var OPER_CODE = $("#OPER_CODE").val();
	var START_DATE = $("#START_DATE").val();
	var REMARK = $("#REMARK").val();
	var INFO_VALUE = $("#INFO_VALUE").val();
	var data=new Wade.DataMap();
	data.put('BIZ_TYPE_CODE',BIZ_TYPE_CODE);
	data.put('SP_CODE',SP_CODE);
	data.put('BIZ_CODE',BIZ_CODE);
	data.put('OPER_CODE',OPER_CODE);
	data.put('START_DATE',START_DATE);
	data.put('REMARK',REMARK);
	data.put('INFO_VALUE',INFO_VALUE);
	if(BIZ_TYPE_CODE == '19')
	{
		if('' == INFO_VALUE){
			alert('无线音乐会员会员级别不能为空！');
			return false;
		}
		data.put('INFO_VALUE',INFO_VALUE);
		data.put('INFO_CODE',"302");
	}
	$("#CODING_STR").val(data);
}

//批量手机支付业务
function BATHBPAY(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var BIZ_TYPE_CODE = $("#BIZ_TYPE_CODE").val();
	var SP_CODE = $("#SP_CODE").val();
	var BIZ_CODE = $("#BIZ_CODE").val();
	var OPER_CODE = $("#OPER_CODE").val();
	var START_DATE = $("#START_DATE").val();
	var REMARK = $("#REMARK").val();
	//var INFO_VALUE = $("#INFO_VALUE").val();
	var data=new Wade.DataMap();
	data.put('BIZ_TYPE_CODE',BIZ_TYPE_CODE);
	data.put('SP_CODE',SP_CODE);
	data.put('BIZ_CODE',BIZ_CODE);
	data.put('OPER_CODE',OPER_CODE);
	data.put('START_DATE',START_DATE);
	data.put('REMARK',REMARK);
	//data.put('INFO_VALUE',INFO_VALUE);
	$("#CODING_STR").val(data);
}

//短信白名单批量处理
function REDMEMBER(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var OPER_TYPE = $("#OPER_TYPE").val();
	var END_DATE = $("#END_DATE").val();
	var data=new Wade.DataMap();
	data.put('OPER_TYPE',OPER_TYPE);
	data.put('END_TIME',END_DATE);
	$("#CODING_STR").val(data);
}

//批量星火计划
function SPARKPLAN(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var PRODUCT_PACKAGE_ID = $("#DEFAULE_VALUE").val();
	var PRODUCT_PACKAGE_NAME =  $("#DEFAULE_VALUE")[0].options($("#DEFAULE_VALUE")[0].selectedIndex).text;
	var PRODUCT_ID = PRODUCT_PACKAGE_ID.split('-')[0];
	var PACKAGE_ID = PRODUCT_PACKAGE_ID.split('-')[1];
	var data=new Wade.DataMap();
	data.put('PRODUCT_ID',PRODUCT_ID);
	data.put('PACKAGE_ID',PACKAGE_ID);
  	$("#CODING_STR").val(data);
}

//批量银行账户资料变更
function MODIFYACYCINFO(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var bankcode = $("#BANK_CODE").val();
	var paymodecode = $("#PAY_MODE_CODE").val();
	
	if(paymodecode != '0') 
	{
		if(bankcode == ''){
			alert('银行名称不能为空！');
			return false;
		}
	}
	if($("#CONTRACT_NO").val().length > 20) 
	{
		alert('合同号不能超过20位');
		return false;
	}
	var acctinfo_PAY_MODE_CODE = $("#PAY_MODE_CODE");
   	if(acctinfo_PAY_MODE_CODE != null && (acctinfo_PAY_MODE_CODE.val() == '0' ||acctinfo_PAY_MODE_CODE.val() == '')) {}
   	else{
   		if($("#RSRV_STR5").val()=="0")
 	   {
 	   		alert('欠费周期必须大于0');
 	   		return false;
 	   }
   		
	   if($("#RSRV_STR5").val().length > 4)
	   {
	   		alert('欠费周期位数不能大于4位');
	   		return false;
	   }
	   var r = /^\+?[0-9][0-9]*$/;//正整数 //isNaN($("#acctinfo_RSRV_STR5").val())
	   var r4 = /^(0|[1-9][0-9]*)$/; //验证零和非零开头的数字
	   if(r.test($("#RSRV_STR5").val()) == false && $("#PAY_MODE_CODE").val() != '0')
	   {
	   		alert('欠费周期必须是非负整数');
	   		return false;
	   }else{
		   var strR4 = $("#RSRV_STR5").val();//parseInt()
		   if( r4.test(strR4) == false ){
			   	alert('欠费周期必须是非负整数!');
	   			return false;
		   }
	   }
   	}
	
	var data = new Wade.DataMap();
	
	var payname = $("#PAY_NAME").val();
	var bankname = $("#BANK_CODE").text();
	var paymode = $("#PAY_MODE_CODE")[0].options($("#PAY_MODE_CODE")[0].selectedIndex).text;
	var bankacctno = $("#BANK_ACCT_NO").val();
	var contractno = $("#CONTRACT_NO").val();
	var postcode = $("#POST_CODE").val();
	var postaddress = $("#POST_ADDRESS").val();
	var importcond = $("#IMPORT_COND").val();
	var rsrvstr5 = $("#RSRV_STR5").val();
	
	if(payname!=""){data.put('PAY_NAME', payname);}
	if(bankname!=""){data.put('BANK_NAME', bankname);}
	if(paymodecode!=""){data.put('PAY_MODE_CODE', paymodecode);}
	if(paymode!=""){data.put('PAY_MODE', paymode);}
	if(bankcode!=""){data.put('BANK_CODE', bankcode);}
	if(bankacctno!=""){data.put('BANK_ACCT_NO', bankacctno);}
	if(contractno!=""){data.put('CONTRACT_NO', contractno);}
	if(postcode!=""){data.put('POST_CODE', postcode);}
	if(postaddress!=""){data.put('POST_ADDRESS', postaddress);}
	//if(importcond!=""){data.put('IMPORT_COND', importcond);}
	if(rsrvstr5!=""){data.put('RSRV_STR5', rsrvstr5);}

	if(importcond == ''){
		$("#IMPORT_COND").val(data);
	}
	
	$("#CODING_STR").val(data);
}

/***********************************business use functions*****************************************/
//批量办理亲亲网业务 @yanwu
function BATQQNET(){
	$("#CODING_STR").val('');
}

//物联网沉默用户批量激活 @yanwu
function SILENCECALLDEAL_PWLW(){
	$("#CODING_STR").val('');
}

//行业应用卡类用户资料变更 @yanwu
function MODIFYCUSTINFO_M2M(){
	$("#CODING_STR").val('');
}

//批量主套餐办理 @zhangxing3
function BATWIDEPRODUCTCHANGE(){
	$("#CODING_STR").val('');
}

//批量主套餐办理 @yanwu
function MODIFYPRODUCT_MAIN(){
	$("#CODING_STR").val('');
}

//批量企业宽带套餐变更 @guonj
function BATMODWIDEPACKAGE(){
	$("#CODING_STR").val('');
}

//批量主套餐办理 @yanwu
function MODIFYPRODUCT_NAME(){
	$("#CODING_STR").val('');
}

//以单位证件开户的开户证件变更
function MODIFYGROUPPSPTINFO(){
	$("#CODING_STR").val('');
}
//批量宽带施工 @xuyt
function CONSTRUCTIONADDR(){
	$("#CODING_STR").val('');
}

//不良信息号码批量处理
function BLACKSMSCHG(){
	$("#CODING_STR").val('');
}

//测试卡批量操作
function BATTESTCARD(){
	$("#CODING_STR").val('');
}

//测试卡批量停机
function STOPTESTCARD(){
	$("#CODING_STR").val('');
}

//集团下发终端激活
function TERMINALACTIVATION(){
	$("#CODING_STR").val('');
}

//集团下发终端退货
function TERMINALRETURN(){

}

//集团下发终端赠费
function TERMINALPRESENTFEE(){
	$("#CODING_STR").val('');
}

//批量USIM卡预开
function CREATEUSIMPREUSER(){
	$("#CODING_STR").val('');
}

//批量办理校园宽带套餐
function CampusBroadband(){
	$("#CODING_STR").val('');
}

//批量局方开机
function OFFICESTOPOPEN(){
	$("#CODING_STR").val('');
}

//批量买断开户手工激活
function BATACTIVEUSER(){
	$("#CODING_STR").val('');
}

//骚扰电话停机人工审核
function PHONEMONITORCHECK(){
	$("#CODING_STR").val('');
}

//物联网批量开机
function OPENUSER(){
	$("#CODING_STR").val('');
}

//物联网批量立即销号
function DESTROYUSER(){
	$("#CODING_STR").val('');
}

//物联网批量停机
function STOPUSER(){
	$("#CODING_STR").val('');
}

//批量账期变更
function ACCTDAYCHG(){
	$("#CODING_STR").val('');
}

//批量TD二代无线固话批量停机 @chencn
function TD_FIXED_PHONE_STOP(){
    $("#CODING_STR").val('');
}

//批量铁通固话批量停机 @chencn
function TT_FIXED_PHONE_STOP(){
    $("#CODING_STR").val('');
}

//产品规则校验
function checkProducts(data,tradeTypeCode){
	var eparchycode = $("#EPARCHY_CODE").val();
	var newProductId = $("#NEW_PRODUCT_ID").val();
	var newProductStartDate = $("#NEW_PRODUCT_START_DATE").val();
	var brandCode = $("#PRODUCT_TYPE_CODE").val();
	var prodId = $("#NEW_PRODUCT_ID").val();
	var resultCode = "1";
	var resultInfo="";
	var content = "";
	var param = "&SELECTED_ELEMENTS=" + data+"&EPARCHY_CODE="+eparchycode+"&NEW_PRODUCT_START_DATE="+newProductStartDate+"&TRADE_TYPE_CODE="+tradeTypeCode+"&PRODUCT_TYPE_CODE="+brandCode+"&NEW_PRODUCT_ID="+prodId;
	$.ajax.submit('','checkProducts',param,'',function(data){
		resultCode = data.get("RESULT_CODE");
		if("2" == resultCode){
			resultInfo = data.get("ERROR_INFO");
			if(null != resultInfo && resultInfo.length>500){
				content = resultInfo.substring(0,500);
			}else{
				content = resultInfo ;
			}
		}
	},
	function(error_code,error_info){
		alert(error_info);
	}, {async:false});
	
	if("2" == resultCode){
		MessageBox.error("错误提示","业务受理失败！",'','取消',content,resultInfo,'');
		return false;
	}else{
		return true;
	}
	
}
function BATREALNAME(){
	$("#CODING_STR").val('');
}
function BATUPDATEPSW(){
	$("#CODING_STR").val('');
}
function BATDESTROYUSER(){
	$("#CODING_STR").val('');
}
function BATCREATEM2MTAG(){
	$("#CODING_STR").val('');
}
function BATOPENSTOP(){
	$("#CODING_STR").val('');
}

/**
 * 批量返销营销活动
 * chenxy3
 * 20160126
 * */
function BATACTIVECANCEL(){
	var data = new Wade.DataMap(); 
	var saleCampnType=$("#SALE_CAMPN_TYPE").val();
	var prodId=$("#SALE_PRODUCT_ID").val();
	var packId=$("#SALE_PACKAGE_ID").val();
	if(saleCampnType!=""){
		data.put('SALE_CAMPN_TYPE', saleCampnType);
	}else{
		alert("活动类型不能为空，请选择。");	
		return false;
	}
	if(prodId!=""){
		data.put('SALE_PRODUCT_ID', prodId); 
	}else{
		alert("营销方案不能为空，请选择。");	
		return false;
	}
	if(packId!=""){
		data.put('SALE_PACKAGE_ID', packId);
	}else{
		alert("产品包不能为空，请选择。");
		return false;
	}
	$("#CODING_STR").val(data);
}
/**
 * REQ201608150016_新增“以单位证件开户集团成员实名资料维护界面”需求
 * @returns
 */
function MODIFY_GROUPMEMBER(){
	$("#CODING_STR").val('');
}
/**
 * 
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160930
 * @returns
 */
function TESTCARDUSER(){
	$("#CODING_STR").val('');
}
/**
 * REQ201610200010_关于测试卡管理的三点优化
 * @author zhuoyingzhi
 * 20161110
 * 批量修改测试卡客户名称
 * @returns
 */
function TESTCARDCUSTNAME(){
	$("#CODING_STR").val('');
}

/**
 * REQ201610200010_关于测试卡管理的三点优化
 * @author zhuoyingzhi
 * 20161110
 * 批量修改测试卡流量封顶值
 * @returns
 */
function TESTCARDMAXFLOWVALUE(){
	$("#CODING_STR").val('');
}

/**
 * REQ201612010002_关于2016年下半年测试卡规则优化需求（一）
 * @author zhuoyingzhi
 * 20170110
 * <br/>
 * 测试号码批量局开
 * @returns
 */
function TESTCARDOFFICEOPEN(){
	$("#CODING_STR").val('');
}
/**
 * REQ201806190026 关于和校园业务异网号码批量开户的需求
 * @author tanzheng
 * 20180829
 * <br/>
 * 异网号码批量开户
 * @returns
 */
function BATOTHERCREATEUSER(){
	$("#CODING_STR").val('');
}
function BATBNBDWIDENETDELETE(){
	$("#CODING_STR").val('');
}
function MODIFYTDPSPTINFO(){
	$("#CODING_STR").val('');
}
function SVCQSUPERVISOR(){
	$("#CODING_STR").val('');
}

function BATBENEFITADDUSENUM(){
    $("#CODING_STR").val('');
}
/**
 * REQ201806190020_新增行业应用卡批量开户人像比对功能
 * <br/>
 * 判断 行业应用卡批量开户  人像比对情况  
 * @param batch_oper_type
 * @returns {Boolean}
 * @author zhuoyingzhi
 * @date 20180718
 */
function createpreuserM2midentification(batch_oper_type){
	//行业应用卡批量开户
	if(batch_oper_type == "CREATEPREUSER_M2M"){
		var cmpTag = "1";
		$.ajax.submit(null,'isBatCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
		
		if(cmpTag == "0"){	
			//责任人摄像标识
			var picid = $("#custInfo_RSRV_STR4_PIC_ID").val();
			
			if(null != picid && picid == "ERROR"){
				//责任人摄像失败
				MessageBox.error("告警提示","责任人"+$("#custInfo_RSRV_STR4_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			//责任人证件类型
			var psptTypeCode=$("#RSRV_STR3").val();
			
			//经办人摄像id
			var agentpicid = $("#custInfo_AGENT_PIC_ID").val();
			//经办人证件类型
			var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
			
			//责任人是否摄像通过标志
			var rsrv_str4_cust_pic=false;
			
			if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
				/*****责任人未进行摄像******/
				/**
				 * 执行到这里，说明 责任人或经办人必须有一个摄像
				 */
				//经办人名称
			    var  custName = $("#AGENT_CUST_NAME").val();
			    //经办人证件号码
				var  psptId = $("#AGENT_PSPT_ID").val();
				//经办人证件地址
				var  agentPsptAddr= $("#AGENT_PSPT_ADDR").val();
				
				if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
					MessageBox.error("告警提示","请进行责任人或经办人摄像!",null, null, null, null);
					return false;
				}
				
	             
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					//责任人和经办人证件类型都满足人像比对,但都未进行人像比对,则拦截提示
					MessageBox.error("告警提示","请进行责任人或经办人摄像!",null, null, null, null);
					return false;
				}
				
				/**
				 * 经办人的证件类型不满足,人像比对要求.则责任人必须摄像
				 */
				if(picid == "" && agentpicid == ""){
					MessageBox.error("告警提示","请进行责任人摄像!",null, null, null, null);
					return false;
				}
			}
			
			if(null != agentpicid && agentpicid == "ERROR"){
				//经办人摄像失败
				MessageBox.error("告警提示","经办人"+$("#custInfo_AGENT_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			
			
			if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid != "" && picid != "ERROR"){
				//说明责任人需要摄像并人像比对通过,则不需要判断经办人是否需要摄像
				rsrv_str4_cust_pic=true;
			}
			
			if(!rsrv_str4_cust_pic){
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
					return false;
				}
			}
	
		}
	}
	return true;
}



