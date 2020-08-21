$(document).ready(function(){
	initValue();
});

function initValue()
{
	$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
	$('#VALID_MEMBER_NUMBER').val("");
	
	$("#InforMationPart").css("display","none");
}

function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	$.ajax.submit(null, 'loadInfo', param, 'FamilyAddPart,viceInfopart', function(data){
		 $.endPageLoading();	
		loadInfoSuccess(data);
		},function(error_code,error_info,derror){
			$("#CSSUBMIT_BUTTON").hide();
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
		});
}

function loadInfoSuccess(data){
	
	
	var flag = data.get("flag");
	if(flag == "0"){
		$("#HideOutPart").css('display','none');
		//$("#FamilyInfoPart").css('display','none');
		$("#FamilyAddPart").css('display','');
		$("#ButtonPart").css('display','none');	

		
	//	$("#NUMBER_TAG").val("0");
//		$("#Create").css("display","");
//		$("#Destroy").hide();
//		$("#bAdd").hide();
//		$("#bDel").hide();
//		$("#Create").attr('disabled',false);
//		$("#Destroy").attr('disbaled','disabled');
//		$("#bAdd").attr('disbaled','disabled');
//		$("#bDel").attr('disbaled','disabled');
	}else {
		$("#FamilyAddPart").css('display','');
		$("#ButtonPart").css('display','none');	
		$("#HideOutPart").css('display','');
		//$("#FamilyInfoPart").css('display','');
	}
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

//点击新增操作
function onPopMeb()
{
	//alert("点击新增=========");
	$("#Flag").val("CREATE");
	//新增办理业务之前必须选着择家庭网类型
	var serialNumberA=$("#HID_SERIAL_NUMBER_A").val();
	var productOfferingId=$("#HID_PRODUCT_OFFERING_ID").val();
	var snB=$("#HID_SERIAL_NUMBER_B").val();

	var param = "&SERIAL_NUMBER_A="+serialNumberA;
	var proType=$("#HID_PRODUCT_TYPE").val();		
	var poidCode=$("#HID_POID_CODE").val();		
	var poidLable=$("#HID_POID_LABLE").val();
	var validMemCount = $("#HID_MEM_COUNT").val();
	if(validMemCount >=19){
		MessageBox.alert("信息提示","家庭网成员不能超过19个！");
		return;
	}




	param += '&PRODUCT_OFFERING_ID='+productOfferingId;
	param += '&HID_SERIAL_NUMBER_B='+snB;
	param += '&HID_PRODUCT_TYPE='+proType;
	param += '&HID_POID_CODE='+poidCode;
	param += '&HID_POID_LABLE='+poidLable;
	param += '&HID_MEM_COUNT='+validMemCount;
	
	popupDialog('成员新增', 'familytradeoptimal.MfcMebInfoPop', 'init', param, null, '60', '35', true, null, null);
//	popupPage('familytradeoptimal.MfcMebInfoPop','','&SERIAL_NUMBER_A=' + serialNumberA+'&PRODUCT_OFFERING_ID='+productOfferingId, 
		//	'成员新增',650, null,null);

}
//点击新增操作
function addMeb()
{
//	alert("点击新增=========");
//	$("#Flag").val("CREATE");
//	//新增办理业务之前必须选着择家庭网类型
//	var serialNumberA=$("#HID_SERIAL_NUMBER_A").val();
//	var productOfferingId=$("#HID_PRODUCT_OFFERING_ID").val();
//	popupPage('未覆盖小区地址查询','person.tietbusy.QryUncoverOrder','','',null,'c_popup c_popup-full','uncoverNetBook.clearDisabled()',null);
//	popupPage('familytradeoptimal.MfcMebInfoPop','','&SERIAL_NUMBER_A=' + serialNumberA+'&PRODUCT_OFFERING_ID='+productOfferingId, 
//			'成员新增',650, null,null);
	
//	var mebcount =addInfoTable.getData(true,"SERIAL_NUMBER_B");
//	var count =mebcount.length;
//	if('18'==count){
//		MessageBox.alert("信息提示","当前成员数已经到达上限，无法继续添加！");
//		return false;
//	}
	//按钮的操作校验
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var memLable = $('#MEM_LABLE').val();
	var mainSn = $("#HID_SERIAL_NUMBER_B").val();//主号码
	var productType = $("#HID_PRODUCT_TYPE").val();//主号码
	var productOfferingId = $("#HID_PRODUCT_OFFERING_ID").val();//群组编码
	var validMemCount = $("#HID_MEM_COUNT").val();
	if(validMemCount >=19){
		MessageBox.alert("信息提示","家庭网成员不能超过19个！");
		return;
	}
	if(''==viceSerialNumber||null==viceSerialNumber){
		return false;
	}
		if(viceSerialNumber.length<11){
		MessageBox.alert("信息提示","手机号码格式不正确！");
		return ;
	}
	if(mainSn == viceSerialNumber){
		MessageBox.alert("信息提示","副号码不能和主号码一样，请重新输入！");
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
  var list=	addInfoTable.getData(true,"SERIAL_NUMBER_B");
  for(var i =0; i<list.length; i++){
	  var sn = addInfoTable.getRowData(i,"SERIAL_NUMBER_B").get(0);
	
	  if(viceSerialNumber == sn){
		  MessageBox.alert("信息提示","号码"+viceSerialNumber+"已经在新增成员之列，无法继续添加");
		  return false;
	  }
  }
	//密码验证	
	mebCheck(mainSn,viceSerialNumber,productType,productOfferingId,memLable);
	$("#SubmitCond").css('display','');	
	$('#TRADE_OPTION').val('CREATE');
	
	//lisw3新增
//	var tradeOption = $("#TRADE_OPTION").val();
//	self.parentNode.parentNode.click();
//	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
//	var table = viceInfoTable.getData(true);
//	var num =rowIndex-1;
//	var rowData = viceInfoTable.getRowData(num);
//	//界面副号码框
//	var serialB= $("#VICE_SERIAL_NUMBER").val();
//	
//	var meb = viceInfoTable.getRowData(num, "SERIAL_NUMBER_B");
//	var mebnumber =meb.get('SERIAL_NUMBER_B');
//	//取到表格的prouductofferid 传到后台
//	var pofferId= meb.get('PRODUCT_OFFERING_ID');
//	var mainnumber =$("#AUTH_SERIAL_NUMBER").val();
//	var prouducttype = $("#PRODUCT_TYPE").val();
	//$("#SubmitPart").css('display','');
//	MessageBox.alert("信息提示","您选择了[需要二次确认]，等待对方确认同意后，本次操作才最终成功！");
//	MessageBox.confirm("提示信息","是否新增号码"+serialB+"?",function(btn){
//	    if(btn == "ok"){
//	    	$('#TRADE_OPTION').val('CREATE');
//	    	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
//	    	param += '&REMARK='+$('#REMARK').val();
//	    	//var data = viceInfoTable.getRowData(num);
//	    	//var data = viceInfoTable.getData(true);
//			var mebData = mebInfopartTable.getData(true);
//
//			param += '&MEB_LIST='+mebData;
//
//	    	//param += '&MEB_LIST='+data;
//	    	param += '&DEL_TAG='+tag;
//	    	param += '&PRODUCT_OFFERING_ID='+pofferId;
//	    	param += '&PRODUCT_CODE='+prouducttype;
//
//	    	$.beginPageLoading("业务受理中......");
//
//			$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(mebdata) {
//				
//				var retCode = mebdata.get("RSP_CODE");
//			
//				if(retCode=="00"||retCode=="0000"||retCode=="0"){
//					$.endPageLoading();
//					MessageBox.success("提示信息","业务申请成功！");
//				}else if(retCode=="2999"){
//					$.endPageLoading();
//					MessageBox.alert("提示信息","号码已经停机或者销户，无法正常办理业务！");
//				}
//				else{
//					$.endPageLoading();
//					MessageBox.alert("提示信息","业务申请失败!");
//
//				}
//	},
//	
//	function(error_code,error_info,derror){			
//		$.endPageLoading();
//		MessageBox.error(error_code,error_info,derror);
//    });	
//	    	
//	    }else{
////	    	$("#NUMBER").val("");
////			$("#BRAND").val("");
//	    	$('#TRADE_OPTION').val('');
//	    	return;
//	    }
//   },{ok:"确定",cancel:"取消"});
}

function mebCheck(mainSn,viceSerialNumber,productType,productOfferingId,memLable){
	var param = "&SERIAL_NUMBER="+mainSn;
	param += '&SERIAL_NUMBER_B='+viceSerialNumber;
	param += '&PRODUCT_CODE='+productType;
	param += '&PRODUCT_OFFERING_ID='+productOfferingId;

	$.beginPageLoading("副号码校验...");
	$.ajax.submit(null, 'checkAddMeb', param, '', function(data) {
	
		$.endPageLoading();
		checkAddMebSucc();
	    
	},	
	function(error_code,error_info,derror){
		$.endPageLoading();
		$('#VICE_SERIAL_NUMBER').val('');
		MessageBox.error(error_code,error_info,derror);
    });	
	
	
}


//成功在table上新增一个亲亲网成员
function checkAddMebSucc(ajaxData){
	$.endPageLoading();
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var memLable = $('#MEM_LABLE').val();
	var proofferingId = $('#HID_PRODUCT_OFFERING_ID').val();
	var poCode = $('#HID_SERIAL_NUMBER_B').val();

//	var INST_ID_B ='9999';

	var familyEdit = {
	//		"INST_ID_B": INST_ID_B,
			"SERIAL_NUMBER_B": viceSerialNumber,
			"MEMBER_DELETE_B":"",
			"HID_PRODUCT_OFFERING_ID": proofferingId,
			"HID_SERIAL_NUMBER_A": poCode,	
			"MEM_LABLE":memLable		
		};
	addInfoTable.addRow(familyEdit);
	$('#VICE_SERIAL_NUMBER').val('');
	$('#MEM_LABLE').val('');

	var validMemCount = $("#HID_MEM_COUNT").val();

	$("#HID_MEM_COUNT").val(parseInt(validMemCount)+1);
}

//删除本次新增的成员
function delMeb() {
	var tabledata = addInfoTable.getData(true);

	for ( var i = tabledata.length - 1; i >= 0; i--) {
				addInfoTable.deleteRow(i, true);	
	}
	$("#HID_MEM_COUNT").val($("#HID_MEM_COUNT_BAK").val());
}
//删除已存在成员
function deleteMeb(self)
{
	$("#CSSUBMIT_BUTTON").hide();
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	self.parentNode.parentNode.click();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var table = mebInfopartTable.getData(true);
	var num =rowIndex-1;
	var rowData = mebInfopartTable.getRowData(num);
	
	 var meb = mebInfopartTable.getRowData(num, "SERIAL_NUMBER_B");
	 var mainnum = mebInfopartTable.getRowData(num, "MAIN_NUMBER");

	 
	 var mebnumber =meb.get('SERIAL_NUMBER_B');
	 var mainnumber =mainnum.get("MAIN_NUMBER");
	 var pofferId = $("#HID_PRODUCT_OFFERING_ID").val();
	 var productType = $("#HID_PRODUCT_TYPE").val();

	MessageBox.confirm("提示信息","是否删除号码"+mebnumber+"?",function(btn){
	    if(btn == "ok"){
	    	$('#TRADE_OPTION').val('DELETE');
	    	var param = '&SERIAL_NUMBER='+mainnumber;
	    	param += '&PRODUCT_OFFERING_ID='+pofferId;
	    	param += '&PRODUCT_CODE='+productType;

	    	var data = mebInfopartTable.getRowData(num);
	    	param += '&MEB_LIST='+data;
	    
	    	$.beginPageLoading("业务受理中......");
	
	    	$.ajax.submit('optionInfoPart,hiddentableInfoPart,mebInfopart', 'onTradeSubmit', param, 'HideOutPart', function(data) {
	    		var retCode = data.get("RSP_CODE");
	    	
	    			if(retCode=="00"||retCode =="0000"||retCode=="0"){
	    				$.endPageLoading();
	    				MessageBox.success("提示信息","业务申请成功！");
	    			
	    			}else{
	    				$.endPageLoading();
	    				MessageBox.alert("提示信息","业务申请失败!");
	    			}
	    	  
	    	},
	    	
	    	function(error_code,error_info,derror){
	    		
	    		$.endPageLoading();
	    		MessageBox.error(error_code,error_info,derror);
	        });	
	    	
	    }else{
//	    	$("#NUMBER").val("");
//			$("#BRAND").val("");
	    	$('#TRADE_OPTION').val('');
	    	return;
	    }
   },{ok:"确定",cancel:"取消"});
	
	
}

//群组信息变更
function updateMainGroup(self)
{
	self.parentNode.parentNode.click();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var table = mebInfopartTable.getData(true);
	var num =rowIndex-1;
	var rowData = viceInfoTable.getRowData(num);
	var meb = viceInfoTable.getRowData(num, "SERIAL_NUMBER_B");//操作号码
//	var mainNum = viceInfoTable.getRowData(num, "MAIN_NUMBER");
//	var pType = viceInfoTable.getRowData(num, "PRODUCT_TYPE");
//	var offeringId = viceInfoTable.getRowData(num, "PRODUCT_OFFERING_ID");


	var mainNumber =rowData.get('MAIN_NUMBER');//主号码
	var oprNumber = rowData.get('SERIAL_NUMBER_B');//操作号码
	var productType =rowData.get('PRODUCT_TYPE');//产品类型
	if("全国亲情网"==productType){
		 var proType="MFC000001";
	}else if("5G家庭会员群组"==productType){
        var proType="MFC000003";
	}else if("5G家庭套餐群组"==productType){
        var proType="MFC000004";
    }else if("5G融合套餐群组"==productType){
        var proType="MFC000005";
    }else if("全国亲情网(支付宝版月包)"==productType){
        var proType="MFC000006";
    }else if("全国亲情网(支付宝版季包)"==productType){
        var proType="MFC000007";
    }else if("全国亲情网(支付宝版年包)"==productType){
        var proType="MFC000008";
    }else if("全国亲情网(异网版月包)"==productType){
        var proType="MFC000009";
    }else if("全国亲情网(异网版季包)"==productType){
        var proType="MFC000010";
    }else if("全国亲情网(异网版年包)"==productType){
        var proType="MFC000011";
    }else{
		 var proType="MFC000002";
	}
	var productOffering =rowData.get('PRODUCT_OFFERING_ID');//群组编码
	var tag ='1';//变更标识

	if(mainNumber!=oprNumber){
		MessageBox.alert("信息提示","当前号码不支持主号码群组信息变更！");
		return false;
	}
	//SERIAL_NUMBER_B
//	var mainnumber =$("#AUTH_SERIAL_NUMBER").val();//操作号码
//	var pofferId = $("#HID_PRODUCT_OFFERING_ID").val();//群组编码

	
	
	
	var param = '&PRODUCT_OFFERING_ID='+productOffering;
	param += '&PRODUCT_CODE='+proType;
	param += '&CUSTOMER_PHONE='+mainNumber;
	param += '&MAIN_NUMBER='+oprNumber;//操作号码
	param += '&TAG='+tag;//标识
	popupDialog('群组信息变更', 'familytradeoptimal.UpdateGroupInfo', 'init', param, null, '60', '35', true, null, null);

	
	
}


//群组信息变更
function updateGroup(self)
{
	self.parentNode.parentNode.click();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var table = mebInfopartTable.getData(true);
	var num =rowIndex-1;
	var rowData = mebInfopartTable.getRowData(num);
	var meb = mebInfopartTable.getRowData(num, "SERIAL_NUMBER_B");
	var mainNum = mebInfopartTable.getRowData(num, "MAIN_NUMBER");

	var mainNumber =mainNum.get('MAIN_NUMBER');//主号码
	var mebnumber =meb.get('SERIAL_NUMBER_B');//成员号码
	var mainnumber =$("#AUTH_SERIAL_NUMBER").val();//操作号码
	var pofferId = $("#HID_PRODUCT_OFFERING_ID").val();//群组编码
	var productType = $("#HID_PRODUCT_TYPE").val();//产品编码
	var tag ='2';//变更标识

	
	var param = '&PRODUCT_OFFERING_ID='+pofferId;
	param += '&PRODUCT_CODE='+productType;
	param += '&CUSTOMER_PHONE='+mainNumber;
	param += '&MEM_NUMBER='+mebnumber;
	param += '&MAIN_NUMBER='+mainnumber;//操作号码
	param += '&TAG='+tag;//操作号码


	popupDialog('群组信息变更', 'familytradeoptimal.UpdateGroupInfo', 'init', param, null, '60', '35', true, null, null);

	
	
}
function isNotEmpty(value){
	if(value != null && value != "")
		return true;
	else 
		return false;
}

//业务提交
function onTradeSubmit()
{
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption == 'NULL'){
		MessageBox.alert("信息提示","您没有进行任何操作，不能进行提交！");
		return false;
	}else if(tradeOption == 'CREATE'){
		var param = '&SERIAL_NUMBER='+$("#HID_SERIAL_NUMBER_B").val();
		var data = addInfoTable.getData(true);
		var productOfferId = $("#HID_PRODUCT_OFFERING_ID").val();
		var poidCode = $("#HID_POID_CODE").val();
		var poidLable = $("#HID_POID_LABLE").val();
		var productType = $("#HID_PRODUCT_TYPE").val();
		var validMemCount = $("#HID_MEM_COUNT_BAK").val();
		param += '&MEB_LIST='+data;
		param += '&PRODUCT_OFFERING_ID='+productOfferId;
		param += '&PRODUCT_CODE='+productType;
        	param += '&POID_CODE='+poidCode;
       	        param += '&POID_LABLE='+poidLable;
		param += '&VALID_MEM_COUNT='+validMemCount;
		$.beginPageLoading("业务受理中......");
		$.ajax.submit('addoptionInfoPart,addInfopart', 'onTradeSubmit', param, 'addInfopart', function(mebdata) {
				
					var retCode = mebdata.get("RSP_CODE");
					var retDesc = mebdata.get("RSP_DESC");
				
					if(retCode=="00"||retCode=="0000"||retCode=="0"){
						$.endPageLoading();
						setPopupReturnValue(this, null, true);

					//	 $.setReturnValue({});
						MessageBox.success("提示信息","业务申请成功！");
					}else if(retCode=="2999"){
						$.endPageLoading();
						setPopupReturnValue(this, null, true);

						MessageBox.alert("提示信息","系统错误，请联系管理！");
						return false ;
					}
					else{
						$.endPageLoading();
						// $.setReturnValue({});

						MessageBox.alert("提示信息","业务申请失败!");

					}
		},
		
		function(error_code,error_info,derror){			
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
	    });	
		
	}else if(tradeOption == 'DELETE'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		//param += '&REMARK='+$('#REMARK').val();
		var data = viceInfoTable.getData(true);
		param += '&MEB_LIST='+data;
		var productOfferId = $("#HID_PRODUCT_OFFERING_ID").val();
		var productType = $("#HID_PRODUCT_TYPE").val();
		param += '&PRODUCT_OFFERING_ID='+productOfferId;
		param += '&PRODUCT_CODE='+productType;

	

		
		$.beginPageLoading("业务受理中......");
		$.ajax.submit('AuthPart,UCAViewPart,mebInfopart,optionInfoPart,hiddentableInfoPart', 'onTradeSubmit', param, 'HideOutPart,mebInfopart', function(mebdata) {
		    $.endPageLoading();
			MessageBox.alert("提示信息","业务申请成功！");		
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
	    });	
	}else if(tradeOption == 'DESTROY'){
		//按钮的操作校验
//		self.parentNode.parentNode.click();
//		var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
//		var table = viceInfoTable.getData(true);
//		var num =rowIndex-1;
//		var rowData = viceInfoTable.getRowData(num);
//		var meb = viceInfoTable.getRowData(num, "SERIAL_NUMBER_B");
//		var mebnumber =meb.get('SERIAL_NUMBER_B');
//		var pofferId= meb.get('PROUDUCT_OFFERING_ID');
//		var mainnumber =$("#AUTH_SERIAL_NUMBER").val();
		var productOfferId= $("#HID_PRODUCT_OFFERING_ID").val();
		MessageBox.confirm("提示信息","是否注销家庭网?",function(btn){
		    if(btn == "ok"){
				var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
				
				var prouducttype = $("#HID_PRODUCT_TYPE").val();

		    param += '&PRODUCT_OFFERING_ID='+productOfferId;
		    	param += '&PRODUCT_CODE='+prouducttype;

				$.beginPageLoading("业务受理中......");
				$.ajax.submit('AuthPart,UCAViewPart,viceInfopart,mebInfopart,optionInfoPart,hiddentableInfoPart', 'onTradeSubmit', param, 'HideOutPart', function(data) {
					$.endPageLoading();
					var retCode = data.get("RSP_CODE");			
					var retDesc = data.get("RSP_DESC");			
					if(retCode=="00"){
						$.endPageLoading();
						MessageBox.success("提示信息","业务申请成功！");
					}else if(retCode =="01"){
						$.endPageLoading();
						MessageBox.alert("提示信息","订单来源错误!");
					}else if(retCode == "02"){
						$.endPageLoading();
						MessageBox.alert("提示信息","订单来源省编码错误!");
					}else if(retCode == "03"){
						$.endPageLoading();
						MessageBox.alert("提示信息","订单类型错误!");
					}else if(retCode == "04"){
						MessageBox.alert("提示信息","业务订单号错误!");
					}else if(retCode == "05"){
						$.endPageLoading();
						MessageBox.alert("提示信息","产品编码错误!");
					}else if(retCode == "06"){
						$.endPageLoading();
						MessageBox.alert("提示信息","业务订购实例ID错误!");
					}else if(retCode == "07"){
						$.endPageLoading();
						MessageBox.alert("提示信息","操作类型错误!");
					}else if(retCode == "08"){
						$.endPageLoading();
						MessageBox.alert("提示信息","主号码类型错误!");
					}else if(retCode == "09"){
						$.endPageLoading();
						MessageBox.alert("提示信息","主号码不存在!");
					}else if(retCode == "10"){
						$.endPageLoading();
						MessageBox.alert("提示信息","订单重复!");
					}else if(retCode == "99"){
						$.endPageLoading();
						MessageBox.alert("提示信息",retDesc);
					}else{
						$.endPageLoading();
						MessageBox.alert("提示信息","系统错误,请联系管理员!");
					}
					$.endPageLoading();
					
				},
				
				function(error_code,error_info,derror){
					
					$.endPageLoading();
					MessageBox.error(error_code,error_info,derror);
			    });	
		    	
		    }else{
//		    	$("#NUMBER").val("");
//				$("#BRAND").val("");
//		    	$('#TRADE_OPTION').val('');
		    	return;
		    }
	   },{ok:"确定",cancel:"取消"});	
	}else if(tradeOption == 'CREATEFAMILY'){
		var prouducttype = $("#PRODUCT_TYPE").val();
		MessageBox.confirm("提示信息","是否创建家庭网?",function(btn){
		    if(btn == "ok"){
		    	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		    	//	param += '&REMARK='+$('#REMARK').val();
		    	param += '&PRODUCT_CODE='+prouducttype;
				param += '&POID_CODE='+$("#POID_CODE").val();
				param += '&POID_LABLE='+$("#POID_LABLE").val();
		    	
		    	$.beginPageLoading("业务受理中......");
		    	$.ajax.submit('AuthPart,UCAViewPart,viceInfopart,mebInfopart,optionInfoPart,hiddentableInfoPart', 'onTradeSubmit', param, 'HideOutPart', function(data) {
		    		var retCode = data.get("RSP_CODE");
		    		var retDesc = data.get("RSP_DESC");
		    		
		    		$.endPageLoading();
		    		if(retCode=="00"){
		    			MessageBox.success("提示信息","业务申请成功！");
		    			
		    		}
//		    		else if(retCode =="01"){
//		    			MessageBox.alert("提示信息","订单来源错误!");
//		    		}else if(retCode == "02"){
//		    			MessageBox.alert("提示信息","订单来源省编码错误!");
//		    		}else if(retCode == "03"){
//		    			MessageBox.alert("提示信息","订单类型错误!");
//		    		}else if(retCode == "04"){
//		    			MessageBox.alert("提示信息","业务订单号错误!");
//		    		}else if(retCode == "05"){
//		    			MessageBox.alert("提示信息","产品编码错误!");
//		    		}else if(retCode == "06"){
//		    			MessageBox.alert("提示信息","业务订购实例ID错误!");
//		    		}else if(retCode == "07"){
//		    			MessageBox.alert("提示信息","操作类型错误!");
//		    		}else if(retCode == "08"){
//		    			MessageBox.alert("提示信息","主号码类型错误!");
//		    		}else if(retCode == "09"){
//		    			MessageBox.alert("提示信息","主号码不存在!");
//		    		}else if(retCode == "10"){
//		    			MessageBox.alert("提示信息","订单重复!");
//		    		}else if(retCode == "99"){
//		    			MessageBox.alert("提示信息","该用户群组已达上限或当前选择的类型群组已达上限!");
//		    		}else if(retCode=="2999"){
//		    			MessageBox.alert("提示信息",retDesc);
//		    		}
//		    		else if(retCode=="98"){
//		    			MessageBox.alert("提示信息","该号码家庭网群已达上限，无法继续创家!");
//		    			
//		    		}
		    		else{
		    			MessageBox.alert("提示信息","原因是"+retDesc);
		    		}
		    	},
		    	
		    	function(error_code,error_info,derror){
		    		
		    		$.endPageLoading();
		    		MessageBox.error(error_code,error_info,derror);
		    	});	
		    }else{
		    	return;
		    }
	   },{ok:"确定",cancel:"取消"});
	}
	return true;
}

//办理一键注销业务
function onDesTradeSubmit()
{
	//获取服务号码
	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	//获取注销按钮的样式
	var className = $("#Destroy").attr("class");
	if(serialNum == ''){
		MessageBox.alert("信息提示","请先进行服务号码的查询！");
		if(className == 'e_button-page-ok'){
			$("#Destroy").removeClass().addClass('e_button-page-cancel');
		}
		return false;
	}
	//判断该服务号码是否能进行注销业务的办理
	$("#Destroy").removeClass().addClass("e_button-page-ok");
	//点击注销后，将业务操作改为注销状态
	$("#TRADE_OPTION").val('DESTROY');
	onTradeSubmit();
}

//办理家庭网创建业务
function onCreTradeSubmit()
{
	var inputPoidCode = $("#POID_CODE").val();
	var tableData = viceInfoTable.getData(true,"POID_CODE");
	for(var i =0; i<tableData.length; i++) {
		var tablePoidCode = tableData.get(i, "POID_CODE");

		if (inputPoidCode == tablePoidCode) {
			MessageBox.alert("信息提示", "群组编码已经存在，不能创建！请更改后再行创建。");
			return false;
		}
	}



	var poidLable = $("#POID_LABLE").val();
	if(poidLable && poidLable.length>32){
		MessageBox.alert("信息提示", "群组标签长度不能大于32");
		return false;
	}

	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	//新增办理业务之前必须选着择家庭网类型
	var familyType = $("#PRODUCT_TYPE").val();
	var poidCode =$("#POID_CODE").val();
	if(poidCode!=''){
		if (  poidCode<1 || poidCode>99 ){
			MessageBox.alert("信息提示","群组编码必须是1-99之间的数字！");
			return false;
		}
	}
	
	if(familyType == ''){
		MessageBox.alert("信息提示","请先进行家庭网类型的选择！");
		return false;
	}
	if(serialNum == ''){
		MessageBox.alert("信息提示","请先进行服务号码的查询！");
		return false;
	}
	$("#TRADE_OPTION").val('CREATEFAMILY');
	onTradeSubmit();

}



function loadMeb(mebData)
{	
	var serialNumberA = mebData.getAttribute("SERIAL_NUMBER_A");
	var roleCodeB = mebData.getAttribute("ROLE_CODE_B");
	var userIdA = mebData.getAttribute("USER_ID_A");
	var serialNumberB =mebData.getAttribute("SERIAL_NUMBER_B");
	var proType = mebData.getAttribute("PRODUCT_CODE");
	var poidCode = mebData.getAttribute("POID_CODE");
	var poidLable = mebData.getAttribute("POID_LABLE");
	var validMemCount = mebData.getAttribute("VALID_MEM_COUNT");
	//alert("============loadMeb"+proType);
	if("全国亲情网"==proType){
		$("#HID_PRODUCT_TYPE").val("MFC000001");
	}else if("5G家庭会员群组"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000003");
	}else if("5G家庭套餐群组"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000004");
    }else if("5G融合套餐群组"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000005");
    }else if("全国亲情网(支付宝版月包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000006");
    }else if("全国亲情网(支付宝版季包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000007");
    }else if("全国亲情网(支付宝版年包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000008");
    }else if("全国亲情网(异网版月包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000009");
    }else if("全国亲情网(异网版季包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000010");
    }else if("全国亲情网(异网版年包)"==proType){
        $("#HID_PRODUCT_TYPE").val("MFC000011");
    }else{
		$("#HID_PRODUCT_TYPE").val("MFC000002");

	}

	$("#mebInfopart").css('display','');
	if("主号"==roleCodeB){
		$("#ButtonPart").css('display','');	
	}else{
		$("#ButtonPart").css('display','none');	

	}
	//获取群组信息到隐藏框
//	$("#HID_PRODUCT_TYPE").val(mebData.getAttribute("PRODUCT_CODE"));
	$("#HID_SERIAL_NUMBER_A").val(serialNumberA);
	$("#HID_PRODUCT_OFFERING_ID").val(mebData.getAttribute("PRODUCT_OFFERING_ID"));
	$("#HID_SERIAL_NUMBER_B").val(mebData.getAttribute("SERIAL_NUMBER_B"));
	$("#HID_FAMILY_TAG").val(roleCodeB);
	$("#HID_USER_ID_A").val(userIdA);
	$("#HID_POID_CODE").val(poidCode);
	$("#HID_POID_LABLE").val(poidLable);
	$("#HID_MEM_COUNT").val(validMemCount);
	$("#HID_MEM_COUNT_BAK").val(validMemCount);
	var param = '&SERIAL_NUMBER_A='+serialNumberA;
	param += '&ROLE_CODE_B='+roleCodeB;
	param += '&USER_ID_A='+userIdA;
//	alert("=================param==="+param);
	$.ajax.submit('hiddentableInfoPart', 'loadMebInfo', param, 'mebInfopart', function(data){
		 $.endPageLoading();	
	},function(error_code,error_info,derror){
		 $.endPageLoading();
		 MessageBox.error(error_code,error_info,derror);
	});	
}

/**
 * 显示数据表格
 */
function displayTypeChange(){
	var display_type=$("#DISPLAY_TYPE").val();
	$(".top").css("display","none");
	if(display_type=="1"){//文件显示
		$("#InforMationRealTimePart").css("display","none");
		$("#InforMationRealTimeTranPart").css("display","none");
		$("#InforMationPart").css("display","");
	}else{//实时显示
		$("#InforMationPart").css("display","none");
		$("#InforMationRealTimePart").css("display","");
		$("#InforMationRealTimeTranPart").css("display","");
	}
}
/**
 * boss 查询接口
 */
function bossquery()
{
	var business = $("#BUSINESS_TYPE").val();
	var bizverson =$("#BIZ_VERSION").val();
	if(business == ''){
		MessageBox.alert("信息提示","业务类型为空！");
		return false;
	}
	if(bizverson==''){
		MessageBox.alert("信息提示","业务版本为空！");
		return false;
	}
	var memType=$("#MEM_TYPE").val();
	if(memType==''){
		MessageBox.alert("信息提示","成员类型为空！");
		return false;
	}
	var display_type=$("#DISPLAY_TYPE").val();
	var param = '&BUSINESS_TYPE='+$("#BUSINESS_TYPE").val()+'&PRODUCT_OFFERING_ID='+$("#PRODUCT_OFFERING_ID").val()+'&PRODUCT_CODE='+$("#PRODUCT_CODE").val()+'&CUSTOMER_PHONE='+$("#CUSTOMER_PHONE").val()+'&MEM_TYPE='+$("#MEM_TYPE").val()
	+'&MEM_AREA_CODE='+$("#MEM_AREA_CODE").val()+'&MEM_NUMBER='+$("#MEM_NUMBER").val()+'&BIZ_VERSION='+$("#BIZ_VERSION").val();	
	if(display_type=="2"){//实时显示
		$.beginPageLoading("查询中......");
		$.ajax.submit('', 'bossRealTimeInfo', param, 'BossQueryInfoPart,InforMationRealTimePart,InforMationRealTimeTranPart', function(data) {
			var retCode = data.get("RSP_CODE");
			var retDesc = data.get("RSP_DESC");
			$.endPageLoading();
			if(retCode!="00"){
				$.endPageLoading();
				MessageBox.alert("提示信息",retDesc);
			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
		});
	}else{//文件显示
		$.beginPageLoading("查询中......");
		$.ajax.submit('', 'bossInfo', param, 'BossQueryInfoPart,InforMationPart', function(data) {
			var retCode = data.get(0).get("RSP_CODE");
			$.endPageLoading();
			if(retCode=="00"){
				MessageBox.success("提示信息","查询成功！");
				//MessageBox.alert("提示信息","业务申请成功!");
			}else{
				 $.endPageLoading();
				MessageBox.alert("提示信息","查询失败！")
			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
		});
	}
}
//点击table中的内容获取成员信息
function clickRow()
{	
	var rowData = $.table.get("viceInfoTable").getRowData();
	$('#MEB_SERIAL_NUMBER').val(rowData.get('SERIAL_NUMBER_B'));
	
}
/**
 * boss 订单状态查询接口
 */
function familyorderstatusbossquery()
{
	var business = $ ("#BUSINESS_TYPE").val();
	var customerphone = $("#CUSTOMER_PHONE").val();
	var bizverson =$("#BIZ_VERSION").val();
	var poordernumber = $("#PO_ORDER_NUMBER").val();
	if(business == ''){
		MessageBox.alert("信息提示","业务类型为空！");
		return false;
	}	
	if(customerphone == '') {
		MessageBox.alert("信息提示","客户标识不能为空! ");
		return false;
	}	
	if(poordernumber == '') {
		MessageBox.alert("信息提示","订单号不能为空! ");
		return false;
	}	
	if(bizverson==''){
		MessageBox.alert("信息提示","业务版本为空! ");
		return false;
	}
   var param = '&BUSINESS_TYPE='+$("#BUSINESS_TYPE").val()+'&CUSTOMER_PHONE='+$("#CUSTOMER_PHONE").val()+'&PO_ORDER_NUMBER='+$("#PO_ORDER_NUMBER").val()+'&MEM_ORDER_NUMBER='+$("#MEM_ORDER_NUMBER").val()
	+'&BIZ_VERSION='+$("#BIZ_VERSION").val();	
	$.beginPageLoading("查询中......");
	$.ajax.submit('', 'familyOrderStatusQuery', param, 'BossQueryInfoPart,ShowResultpart', function(data) {
		var retCode = data.get(0).get("RSP_CODE");
       $.endPageLoading();
       if("00"==retCode) {
    	   MessageBox.success("提示信息","查询成功！");
       }
       else {				
    	   MessageBox.alert("提示信息","查询失败，原因是"+data.get(0).get("RSP_DESC"));	
       }
	   },function(error_code,error_info,derror){
		   $.endPageLoading();
		   MessageBox.error(error_code,error_info,derror);
	   });

	}


