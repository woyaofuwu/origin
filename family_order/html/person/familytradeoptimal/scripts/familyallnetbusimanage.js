$(document).ready(function(){
	initValue();
});

function initValue()
{
	$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
	$('#VALID_MEMBER_NUMBER').val("");
}

function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	
	$.ajax.submit(null, 'loadInfo', param, 'FamilyInfoPart,viceInfopart', function(data){
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
	var valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
	if(flag == "0"){
		$("#HideOutPart").css('display','none');
		$("#FamilyInfoPart").css('display','none');
	
	
		$("#Destroy").css("display","none");
		$("#bAdd").css("display","none");
		$("#bDel").css("display","none");
	
		$("#Create").attr('disabled',false);
//		$("#Destroy").attr('disbaled','disabled');
//		$("#bAdd").attr('disbaled','disabled');
//		$("#bDel").attr('disbaled','disabled');
	}else if(flag == "1"){
		
	
		$("#HideOutPart").css('display','');
		$("#Create").css("display","none");
		$("#Destroy").css("display","none");
		$("#bAdd").css("display","none");
		$("#bDel").css("display","none");
		$("#Create").attr('disabled',true);
		$("#Destroy").attr('disbaled',true);
		$("#bAdd").attr('disbaled',true);
		$("#bDel").attr('disbaled',true);
	}else if(flag == "2"){
		$("#HideOutPart").css('display','');
		$("#FamilyInfoPart").css('display','');
		
		$("#Create").css("display","none");
		$("#Create").attr('disabled',true);
		$("#Destroy").attr('disbaled',false);
		$("#bAdd").attr('disbaled',false);
		$("#bDel").attr('disbaled',false);
	}
	
	if(valideMemberNumber&&valideMemberNumber!="-1"){
		$("#VALID_MEMBER_NUMBER_AREA").css("display","");
		$("#VALID_MEMBER_NUMBER").val(valideMemberNumber);
	}else{
		$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
		$("#VALID_MEMBER_NUMBER").val("0");
	}
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

//点击新增操作
function addMeb()
{
	var count =$("#VALID_MEMBER_NUMBER").val();
	if('18'==count){
		MessageBox.alert("信息提示","当前成员数已经到达上限，无法继续添加！");
	}
	//按钮的操作校验
	var viceSerialNumber = $('#VICE_SERIAL_NUMBER').val();
	var mainSn = $("#AUTH_SERIAL_NUMBER").val();//主号码
	if(''==viceSerialNumber||null==viceSerialNumber){
		return false;
	}
	if(mainSn == viceSerialNumber){
		MessageBox.alert("信息提示","副号码不能和主号码一样，请重新输入！");
		$('#VICE_SERIAL_NUMBER').val('');
		return;
	}
	
	
	//副号、副号短号已经在成员列表里的校验
	var list = $.table.get("viceInfoTable").getTableData(null,true);
	for(var i = 0, size = list.length; i < size; i++){
		var tmp = list.get(i);
		var sn = tmp.get('SERIAL_NUMBER_B');
		if(viceSerialNumber == sn)
		{
			MessageBox.alert("信息提示","号码"+viceSerialNumber+"已经在成员列表");
			return false;
		}
	}
	if(!$.validate.verifyAll("MAIN_NUM_INFO")){
		return false;
	}
	
	if(!$.validate.verifyAll("MEB_NUM_INFO")){
		return false;
	}
	//密码验证
	mebCheck(mainSn,viceSerialNumber);

	
	$('#TRADE_OPTION').val('CREATE');
}

function mebCheck(mainSn,viceSerialNumber){
	var param = "&SERIAL_NUMBER="+mainSn;
	param += '&SERIAL_NUMBER_B='+viceSerialNumber;
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
	
	
	var familyEdit = new Array();
	debugger;
    familyEdit["SERIAL_NUMBER_B"] = viceSerialNumber;
    
	familyEdit["MEMBER_DELETE_B"] = "";
	familyEdit["INST_ID_B"] = "9999";
	
	$.table.get("viceInfoTable").addRow(familyEdit);
	
	$('#VICE_SERIAL_NUMBER').val('');
	
	
	var addMemberNumber=$("#ADD_MEMBER_NUMBER").val();
	if(!addMemberNumber||addMemberNumber==""){
		addMemberNumber="0";
	}
	
	var addMemberNumberInt=parseInt(addMemberNumber);
	addMemberNumberInt++;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	
}

//删除本次新增的成员
function delMeb()
{
	var table = $.table.get("viceInfoTable");
	var json = table.getRowData();
	if(json.get('INST_ID_B') != '9999')
	{
		MessageBox.alert("信息提示","只能删除本次新增的成员");
		return false;
	}
	table.deleteRow();
	
	var addMemberNumberInt=parseInt($("#ADD_MEMBER_NUMBER").val());
	addMemberNumberInt--;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	if(addMemberNumberInt == 0){
		$("#TRADE_OPTION").val("NULL");
	}
	
}

//删除已存在成员
function deleteMeb(self)
{
	$("#CSSUBMIT_BUTTON").css("display","none");
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	self.parentNode.parentNode.click();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var table = $.table.get("viceInfoTable");
	var row = table.getRowByIndex(rowIndex+"");
	var rowData = table.getRowData();
    var data = eval('('+rowData+')');
    var endDate = data.LAST_DAY_THIS_ACCT;
    var rdendDate = data.END_DATE;
    var strsnB = data.SERIAL_NUMBER_B;
    var editEndDate = new Array();
    var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');//
    editEndDate["END_DATE"]=sysDate;
    editEndDate["EFFECT_NOW"]="YES";
 	self.disabled = true;
	table.updateRow(editEndDate);
	table.setRowCss(row,"delete");
	var num =rowIndex-1;
	 //var meb = viceInfoTable.getRowData(num, "SERIAL_NUMBER_B");
	 //var mebnumber =meb.get('SERIAL_NUMBER_B');
	
	 var mebnumber =strsnB;
	 var mainnumber =$("#AUTH_SERIAL_NUMBER").val();
	 if(mebnumber==mainnumber){
		 var tag ='BD'
	 }else{
		 var tag ='ZD'
	 }
	var addMemberNumberInt=parseInt($("#ADD_MEMBER_NUMBER").val());
	addMemberNumberInt--;
	$("#ADD_MEMBER_NUMBER").val(addMemberNumberInt);
	MessageBox.confirm("提示信息","是否删除号码",function(btn){
	    if(btn == "ok"){
	    	$('#TRADE_OPTION').val('DELETE');
	    	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	    	param += '&REMARK='+$('#REMARK').val();
	    	//var data = viceInfoTable.getRowData(num);
	    	//var rowData = viceInfoTable.getRowData(num);
	    	param += '&MEB_LIST='+rowData;
	    	param += '&DEL_TAG='+tag;
	    	$.beginPageLoading("业务受理中......");
	
	    	$.ajax.submit('OperInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(data) {
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
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
		$.beginPageLoading("业务受理中......");
		$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(mebdata) {
				
					var retCode = mebdata.get("RSP_CODE");
				
					if(retCode=="00"||retCode=="0000"||retCode=="0"){
						$.endPageLoading();
						MessageBox.success("提示信息","业务申请成功！");
					}else if(retCode=="2999"){
						$.endPageLoading();
						MessageBox.alert("提示信息","系统错误，请联系管理员！");
					}
					else{
						$.endPageLoading();
						MessageBox.alert("提示信息","业务申请失败!");

					}
		},
		
		function(error_code,error_info,derror){
			
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
	    });	
		
	}else if(tradeOption == 'DELETE'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		var data = $.table.get("viceInfoTable").getTableData();
		param += '&MEB_LIST='+data;
		
		$.beginPageLoading("业务受理中......");
		$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(mebdata) {
		    $.endPageLoading();
			MessageBox.alert("提示信息","业务申请成功！");		
		},
		
		function(error_code,error_info,derror){
			
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
	    });	
	}else if(tradeOption == 'DESTROY'){
		
		MessageBox.confirm("提示信息","是否注销家庭网?",function(btn){
		    if(btn == "ok"){
		    	$('#TRADE_OPTION').val('DESTROY');		    	
				var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
				param += '&REMARK='+$('#REMARK').val();
				$.beginPageLoading("业务受理中......");
				$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(data) {
					$.endPageLoading();
					var retCode = data.get("RSP_CODE");			
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
						MessageBox.alert("提示信息","未知错误!");
					}else{
						$.endPageLoading();
						MessageBox.alert("提示信息","系统错误,请联系管理员!");
					}
					$.endPageLoading();
					
					$('#TRADE_OPTION').val('NULL');		    
				},
				
				function(error_code,error_info,derror){
					
					$.endPageLoading();
					MessageBox.error(error_code,error_info,derror);
			    });	
		    	
		    }else{
//		    	$("#NUMBER").val("");
//				$("#BRAND").val("");
		    	$('#TRADE_OPTION').val('');
		    	return;
		    }
	   },{ok:"确定",cancel:"取消"});	
//		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
//		param += '&REMARK='+$('#REMARK').val();
//		$.beginPageLoading("业务受理中......");
//		$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(data) {
//			$.endPageLoading();
//			var retCode = data.get("RSP_CODE");
//	
//			if(retCode=="00"){
//				$.endPageLoading();
//				MessageBox.success("提示信息","业务申请成功！");
//			}else if(retCode =="01"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","订单来源错误!");
//			}else if(retCode == "02"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","订单来源省编码错误!");
//			}else if(retCode == "03"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","订单类型错误!");
//			}else if(retCode == "04"){
//				MessageBox.alert("提示信息","业务订单号错误!");
//			}else if(retCode == "05"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","产品编码错误!");
//			}else if(retCode == "06"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","业务订购实例ID错误!");
//			}else if(retCode == "07"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","操作类型错误!");
//			}else if(retCode == "08"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","主号码类型错误!");
//			}else if(retCode == "09"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","主号码不存在!");
//			}else if(retCode == "10"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","订单重复!");
//			}else if(retCode == "99"){
//				$.endPageLoading();
//				MessageBox.alert("提示信息","未知错误!");
//			}else{
//				$.endPageLoading();
//				MessageBox.alert("提示信息","系统错误,请联系管理员!");
//			}
//			$.endPageLoading();
//			
//			$('#TRADE_OPTION').val('NULL');		    
//		},
//		
//		function(error_code,error_info,derror){
//			
//			$.endPageLoading();
//			MessageBox.error(error_code,error_info,derror);
//	    });	
	}else if(tradeOption == 'CREATEFAMILY'){
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
		param += '&REMARK='+$('#REMARK').val();
		$.beginPageLoading("业务受理中......");
		$.ajax.submit('AuthPart,UCAViewPart,FamilyInfoPart,OperInfoPart,viceInfopart,OtherInfoPart', 'onTradeSubmit', param, 'HideOutPart,OperInfoPart,FamilyInfoPart', function(data) {
			var retCode = data.get("RSP_CODE");
			
		    $.endPageLoading();
			if(retCode=="00"){
				MessageBox.success("提示信息","业务申请成功！");
				//MessageBox.alert("提示信息","业务申请成功!");
			}else if(retCode =="01"){
				MessageBox.alert("提示信息","订单来源错误!");
			}else if(retCode == "02"){
				MessageBox.alert("提示信息","订单来源省编码错误!");
			}else if(retCode == "03"){
				MessageBox.alert("提示信息","订单类型错误!");
			}else if(retCode == "04"){
				MessageBox.alert("提示信息","业务订单号错误!");
			}else if(retCode == "05"){
				MessageBox.alert("提示信息","产品编码错误!");
			}else if(retCode == "06"){
				MessageBox.alert("提示信息","业务订购实例ID错误!");
			}else if(retCode == "07"){
				MessageBox.alert("提示信息","操作类型错误!");
			}else if(retCode == "08"){
				MessageBox.alert("提示信息","主号码类型错误!");
			}else if(retCode == "09"){
				MessageBox.alert("提示信息","主号码不存在!");
			}else if(retCode == "10"){
				MessageBox.alert("提示信息","订单重复!");
			}else if(retCode == "99"){
				MessageBox.alert("提示信息","未知错误!");
			}else if(retCode=="2999"){
				MessageBox.alert("提示信息","该号码状态异常，无法办理业务！!");
			}
			else{
				MessageBox.alert("提示信息","系统错误,请联系管理员!");
			}
			$('#TRADE_OPTION').val('NULL');
		},
		
		function(error_code,error_info,derror){
			
			$.endPageLoading();
			MessageBox.error(error_code,error_info,derror);
	    });	
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
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption == 'DESTROY'){
		MessageBox.alert("信息提示","您已点击【注销】按钮，请点击【提交】按钮提交注销业务！");
		return false;
	}else if(tradeOption != 'NULL'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行注销业务的操作！");
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
	var serialNum = $("#AUTH_SERIAL_NUMBER").val();
	var className = $("#Create").attr("class");
	if(serialNum == ''){
		MessageBox.alert("信息提示","请先进行服务号码的查询！");
		
		return false;
	}
	//按钮的操作校验
	var tradeOption = $("#TRADE_OPTION").val();
	if(tradeOption == 'CREATEFAMILY'){
		MessageBox.alert("信息提示","您已点击【创建家庭网】按钮，请点击【提交】按钮提交 创建家庭网业务！");
		return false;
	}else if(tradeOption != 'NULL'){
		MessageBox.alert("信息提示","请先完成其他业务未完成的操作，再进行创建家庭网业务的操作！");
		return false;
	}
	$("#TRADE_OPTION").val('CREATEFAMILY');
	onTradeSubmit();

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

	var param = '&BUSINESS_TYPE='+$("#BUSINESS_TYPE").val()+'&PRODUCT_OFFERING_ID='+$("#PRODUCT_OFFERING_ID").val()+'&CUSTOMER_PHONE='+$("#CUSTOMER_PHONE").val()+'&MEM_TYPE='+$("#MEM_TYPE").val()
	+'&MEM_AREA_CODE='+$("#MEM_AREA_CODE").val()+'&MEM_NUMBER='+$("#MEM_NUMBER").val()+'&BIZ_VERSION='+$("#BIZ_VERSION").val();	
	$.beginPageLoading("查询中......");
	$.ajax.submit('', 'bossInfo', param, 'BossQueryInfoPart,ShowResultpart', function(data) {
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
//点击table中的内容获取成员信息
function clickRow()
{	
	var rowData = $.table.get("viceInfoTable").getRowData();
	$('#MEB_SERIAL_NUMBER').val(rowData.get('SERIAL_NUMBER_B'));
	
}

