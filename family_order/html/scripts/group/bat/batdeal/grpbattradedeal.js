var has4AOK = false;
var need4A = false;

// 新增批量
function addBatDeal() {
 	
	displayLayer();   
}

// 显示Layer
function displayLayer() {
	
	$.ajax.submit(this, "initBatCreatePoup", null, "BatCreatePart,BatCondPart,BatImportPart", null, null);
	
	$("#popup").css("display", "");
}

// 隐藏Layer
function hiddenLayer() {
	
	$("#popup").css("display", "none");
}

//导入方式选择处理
function importGrpChangeAction() {
	var import_mode = $('#GRP_BAT').val();
    if(import_mode == '0') {                                  //通过文件导入
		$('#cancel_text').css("display","none");
		$('#cancel_file').css("display","");
		$('#cancel_templet').css("display","");
		$("#submitPart").css('display','none');
	} else if(import_mode == '1'){                            //通过后台自动导入
		$('#cancel_text').css("display","");
		$('#cancel_file').css("display","none");
		$('#cancel_templet').css("display","none");
		$("#submitPart").css('display','');
	}
}
//批量弹出条件框初始化
function selectBatchOperType() {
	
	//重置金库开关
	has4AOK = false;
	need4A = false;
	
	var operType= $('#BATCH_OPER_CODE').val();
	var operName = $("#BATCH_OPER_CODE :selected").text();
	
	$('#BATCH_OPER_NAME').val(operName);
	
	if(operType == 'VPMNCHANGEDSHORTCODE' || operType == 'SPECVPMNCHGSHORTCODE' || operType == 'BATADDVPMNMEM')
	{
		$("#SMSGO").css('display','');
		$("#SMS_FLAG").attr("disabled",false);
	}
	else
	{
		$("#SMSGO").css('display','none');
		$("#SMS_FLAG").attr("disabled",true);
		$("#SMS_FLAG").val("0");
	}
	 
	$.beginPageLoading('系统加载中......');
	
	$.ajax.submit(this, 'queryComps', '&BATCH_OPER_TYPE=' + operType, 'BatCondPart,MqParamPart', 
		function(){
			$.endPageLoading();
			operTypeAfter(operType);
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}
//批量弹出组件的回调函数
function operTypeAfter(obj){
  		var operType= $('#BATCH_OPER_CODE').val();
  		var operTypeName = $('#BATCH_OPER_NAME').val();
		
		$('#CODING_STR').val("");
		$('#POP_CODING_STR').val("");

		if (obj == "BATGRPOPENMEB"){
			$("#bsubmit").attr("disabled","true");
			$('#cancel_file').css("display","none");
	  	} else{
	  		$('#cancel_file').css("display","");
	  		$('#cancel_templet').css("display","");
	  	}
	  	 
		if (obj == "BATGRPOPENMEB"){
			$('#MyInfoId').css("display","none");
			
		}
 	   if (obj == "HEEDUFORSCHOOLS"){
        $('#HeEduForSchoolsID').css("display","");

     }
		else if (obj=="BATADDYDZFMEM" || obj=="BATADDHYYYKMEM" || obj=="MEMBERADCORDER" || obj=="MEMBERMASORDER"
			|| obj=="NEWXXTUSERCHANGE" || obj=="NEWXXTUSERREG" || obj=="NEWXXTUSERREG_SPE"
				|| obj=="BATADDXHKNMEM" || obj=="XHKREGCHECK" || obj=="INDIGRPUSERREG" || obj=="OUTPROVVPNMEMCHANGE"
					|| obj=="BATOPENYDZFMEM" || obj=="BATOPENHYYYKMEM" || obj=="BATCONFIRMYDZFMEM" || obj=="BATADDBBOSSMEMBER"
						|| obj=="BATDELBBOSSMEMBER" || obj=="BATMODBBOSSMEMBER" || obj=="BATCONBBOSSMEMBER"
							|| obj=="BATPASBBOSSMEMBER"|| obj=="HPERSONUSERREG_SPE"|| obj=="HEEDUFORSCHOOLS"){
			
			$('#MyInfoId').css("display","");
		}
		
	   var info =$.DataMap();
	   info.put("BATCH_OPER_TYPE",obj);
	  
	   /*if (obj == "DESTROYGROUPUSER") {
	  	  $("#POP_CODING_STR").val(operTypeName);
	  	   $("#CODING_STR").val(info.toString());
	  	}else{
	  	   $("#CODING_STR").val("");
	  	}	*/  
	  	
	  	if(obj == "GRPDESTROYONEKEY") {
	  		$("#BatImportPart").css('display','none');
	  		$("#submitPart").css('display','');
	  	} else {
	  		$("#submitPart").css('display','none');
	  	}
	  	
	  	cleanPopupPageGrp();
}
//导入文件前的校验
function chooseCompSTR(obj) {
	var importId = "importDiv_" + obj;
	
	var operType= $('#BATCH_OPER_CODE').val();
	//已经4A校验通过
	if(has4AOK) {
    	
    	return true;
    }
	
	var taskName = $('#BATCH_TASK_NAME').val();
	var CODING_STR = $('#CODING_STR').val();
	var MEB_VOUCHER_FILE_LIST=$('#MEB_VOUCHER_FILE_LIST').val();
	var AUDIT_STAFF_ID=$('#AUDIT_STAFF_ID').val();
	if(taskName == "") {
	  $.showWarnMessage('请先填写批量任务名称','',null);
	  $('#BATCH_TASK_NAME').focus();
      return false;
	}
	if( taskName.length > 50 ){
		$.showWarnMessage('任务名称长度不能超过50字符，请重新输入！','',null);
		$('#BATCH_TASK_NAME').focus();
		return false;
	}
    if (CODING_STR == ""){
       $.showWarnMessage('请先选择批量条件','',null);
      return false;
    }
    //验证凭证信息文件是否上传
    if(MEB_VOUCHER_FILE_LIST==""){
    	if(operType=="BATADDYDZFMEM" || operType=="BATADDHYYYKMEM" || operType=="MEMBERADCORDER" || operType=="MEMBERMASORDER"
			|| operType=="NEWXXTUSERCHANGE" || operType=="NEWXXTUSERREG" || operType=="NEWXXTUSERREG_SPE"
				|| operType=="BATADDXHKNMEM" || operType=="XHKREGCHECK" || operType=="INDIGRPUSERREG" || operType=="OUTPROVVPNMEMCHANGE"
					|| operType=="BATOPENYDZFMEM" || operType=="BATOPENHYYYKMEM" || operType=="BATCONFIRMYDZFMEM" || operType=="BATADDBBOSSMEMBER"
						|| operType=="BATDELBBOSSMEMBER" || operType=="BATMODBBOSSMEMBER" || operType=="BATCONBBOSSMEMBER"
							|| operType=="BATPASBBOSSMEMBER"|| operType=="HPERSONUSERREG_SPE"){
    		
    		 $.showWarnMessage('请上传凭证文件','',null);
    		 return false;
    	}
    	
    }
    //校验稽核人员信息是否已经填写
    if(AUDIT_STAFF_ID==""){
    	if(operType=="BATADDYDZFMEM" || operType=="BATADDHYYYKMEM" || operType=="MEMBERADCORDER" || operType=="MEMBERMASORDER"
			|| operType=="NEWXXTUSERCHANGE" || operType=="NEWXXTUSERREG" || operType=="NEWXXTUSERREG_SPE"
				|| operType=="BATADDXHKNMEM" || operType=="XHKREGCHECK" || operType=="INDIGRPUSERREG" || operType=="OUTPROVVPNMEMCHANGE"
					|| operType=="BATOPENYDZFMEM" || operType=="BATOPENHYYYKMEM" || operType=="BATCONFIRMYDZFMEM" || operType=="BATADDBBOSSMEMBER"
						|| operType=="BATDELBBOSSMEMBER" || operType=="BATMODBBOSSMEMBER" || operType=="BATCONBBOSSMEMBER"
							|| operType=="BATPASBBOSSMEMBER"|| operType=="HPERSONUSERREG_SPE"){
    		 $.showWarnMessage('请选择稽核人员信息！','',null);
    		 return false;
    	}
    }
    
    //是否金库验证开关
	var grpbat4Aflag = $('#GRPBAT4AFLAG').val();
	
  	if(grpbat4Aflag == "false" || grpbat4Aflag == false)
  	{
		return true;
	}
    
	//进行金库校验
    var batch_oper_code = $('#BATCH_OPER_CODE').val();
    $("#TD_S_4APar").children("option").each( function(){
    	if(this.value == batch_oper_code)
    	{
    		need4A = true;
    		return false;//跳出循环
    	}
    });
    
    if(need4A){
    	
	    //导入前进行金库校验
	    doAuth(importId);
    }else{
    	
    	return true;
    }
    
    return false;
}
//导入文件创建成功后，返回页面信息显示
function finishImportData () {
	var batchTaskId = $('#BATCH_TASK_ID').val();
	//alert("-------batchTaskId---:"+batchTaskId);
	/**
	 * REQ201801150022_新增IMS号码开户人像比对功能
	 * @author zhuoyingzhi
	 * @date 20180326
	 */
	var  batchOperType=$("#BATCH_OPER_TYPE").val();
	//alert("-------batchOperType---:"+batchOperType);
	
	//"产品ID:"+productId+" 集团ID:"+group_id
	var popCodingStr = $('#POP_CODING_STR').val();
	var tag = popCodingStr.indexOf("产品ID:801110");
	
	if('BATOPENGROUPMEM' == batchOperType && "0" == tag){
		//REQ201807240010++新增批量开户界面人像比对、受理单打印优化需求 by mqx 20190123
		//记录信息到tf_b_trade_cnote_info表
		$.ajax.submit(null,'insertIntoTradeCnoteInfoBat','&BATCH_TASK_ID='+batchTaskId+'&BATCH_OPER_TYPE='+batchOperType,null,function(data){
			console.log("======mqx==insertIntoTradeCnoteInfoBat执行==========");
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		},{
			"async" : false
		});	 	
		
		
		//集团成员批量开户
		var hint_message='批量业务流水号：' + batchTaskId;
		//REQ201807240010++新增批量开户界面人像比对、受理单打印优化需求 by mqx 20190117
		//需要先有TF_B_TRADE_CNOTE_INFO数据，下面打印了才能回填rsrv_tag2字段
		//按行业应用卡批量开户的TF_B_TRADE_CNOTE_INFO数据，其实只需要batchId做trade_id就行,根据batchTaskId找batchId
		
		
		
//	   		MessageBox.show({
//				"title":"成功提示",
//				"msg":"数据导入成功!" +
//						"<br/>" +
//						"<div style='font-size: 13px;color: black;'>"+hint_message+"</div>",
//				"success":true,
//				"fn":function(btn){
//					    //回调
//						if(btn == "ok" || btn=="cancel"){	
//							//setPopupReturnValue('','');
//						}else if(btn == "ext0"){
//							return false;
//						}
//					},
//	             "beforeHide":function(btn){
//						if(btn == "ok"){	
//							return true;													
//						}else if(btn == "ext0"){
//							//打印电子工单
//							buildBatchBill(batchTaskId);
//							return false;
//						}
//					},
//				"buttons":$.extend({ok:"取消"},{ext0:"打印电子工单,print"})
//	   		});
		/**
	     * REQ201904260020新增物联网批量开户界面权限控制需求
	     * 免人像比对权限判断、免受理单打印
	     * @author mengqx
	     * @date 20190515
	     * @param clcle
	     * @throws Exception
	     */
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
			showMessageBatchBill(hint_message);
		}else{
			$.showSucMessage("批量业务创建成功",'批量业务流水号：' + batchTaskId);
		}
	}else{
		$.showSucMessage("批量业务创建成功",'批量业务流水号：' + batchTaskId);
	}
	hiddenLayer('popup');
}
//提交按钮操作
function importSubmit(){
	
	//是否金库验证开关
	var grpbat4Aflag = $('#GRPBAT4AFLAG').val();
	
  	if(grpbat4Aflag == "false" || grpbat4Aflag == false)
  	{
		return true;
	}
	
	need4A = false;
	//是否需要进行金库校验
    var batch_oper_code = $('#BATCH_OPER_CODE').val();
    $("#TD_S_4APar").children("option").each( function(){
    	if(this.value == batch_oper_code)
    	{
    		need4A = true;
    		return false;//跳出循环
    	}
    });
    
	//需要4A验证
    if(need4A){
    	var operCode = "GRPBAT_4A_" + batch_oper_code;
    	
		beginPageLoading("正进行金库认证..");
		
		$.treasury.auth(operCode, function(ret){
			
			endPageLoading();
			
			if(true === ret){
				
				alert("认证成功");
				importSubmit2();
				
				return true;
			}else{
				
				alert("认证失败");
				
				return false;
			}
		});
    }else{
    	importSubmit2();
    }
}
function importSubmit2(){
	var CODING_STR = $('#CODING_STR').val();
    if (CODING_STR == ""){
      $.showWarnMessage('请先选择批量条件','',null);
      return false;
    }
    
    var params = '';
    if($.validate.verifyAll("popup")) {
    	$.beginPageLoading("任务创建中...");
    	$.ajax.submit('PopupPart','createBatDealInfo',params,'PopupPart',function (data) {
    		$.endPageLoading();
    		var error_message = data.get('error_message');
    		var hint_message = data.get('hint_message');
    		if(!$.isEmptyObject(error_message))
    		{
    			$.showSucMessage("批量业务创建失败",error_message);
    		}
      		else if(!$.isEmptyObject(hint_message)) 
      		{
      			 $.showSucMessage("批量业务创建成功",hint_message);
      		}else
      		{
      			finishImportData();
      		}
      		
    	},
    	function(error_code,error_info,derror){
    		$.endPageLoading();
    		showDetailErrorInfo(error_code,error_info,derror);
        })
    } 
}
//M2M开户导入选择方式
function importGrpOpenAction(obj) {
    var conStr = $('#CODING_STR').val();
    if (conStr == ""){
      $.showWarnMessage('请先选择批量条件','',null);
      return false;
    }
    ajaxSubmit(this, '', null, 'OpenArea',null,false,function(){});
}
//查询批量信息
function getSelectValue(){ 
 	
	getSelectValue2();      
}

function getSelectValue2(){
	  var value=$('#OPER_TYPE').val();
	  var type=$('#cond_BATCH_OPER_TYPE').val();
	  var cond_BATCH_ID=$('#cond_BATCH_ID').val();
	  if(value==""){
	  	alert("请选择操作类型");
	  	return false;
	  }
	  if('0'==value) {//0:未启动
	  	$('#startButton').css('display','');
	  	$('#delButton').css('display','');
	  }else {//1:启动 删除
	  	$('#startButton').css('display','none');
	  	$('#delButton').css('display','none');
	  }
	  
	  if(!cond_BATCH_ID=="" && !cond_BATCH_ID==null &&!$.isNumber(cond_BATCH_ID))
	  {
	  	   alert("批次号只能为数字");
	       return false;
	  }
	  
	  $('#operType').val(value);
	  $.beginPageLoading();
	  $.ajax.submit('QueryForm','queryStartTaskInfo','','GantPart,hintBar',
		function (data){ 
			$.endPageLoading();
		},null);
	  return true;
}

//启动批量
function runBatDeal() {
	
	runBatDeal2();
}

function runBatDeal2(){
	var batchid = getCheckedValues("trades");
   if(null == batchid || "" == batchid) {
   		alert("请选择需要启动的批量任务！");
   		return false;
   }
   
   /**
	 * 判断批量是否已经打印
	 * @author  mengqx
	 * @date 20190124
	 */
   var printTag=1;
   var msgInfo;
   
	$.ajax.submit(null,'chexkBatTask','&BATCH_IDS='+batchid,'',function(data){ 
	    printTag=data.get("printTag");
		if(printTag=="0"){ 
			//存在 未打印或打印信息不存在的
			msgInfo=data.get("msgInfo");
		}
		$.endPageLoading();
	},function(error_code,error_info){
		$.MessageBox.error(error_code,error_info);
		$.endPageLoading();
	},{
		"async" : false
	});		
	
	/**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断、免受理单打印
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
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
	
	if('0' == cmpTag && '0' == printTag && msgInfo){
		MessageBox.error("存在未打印的批次号",msgInfo,null, null, null, null);
		return false;
	}
   
   
   
   $.ajax.submit('QueryForm','startBatDeals','&BATCH_IDS='+batchid,'GantPart',function(data){
  		
		$.endPageLoading();
		$.showSucMessage(data.map.result,"");
	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	});
}
//删除批量
function deleteBatDeal() {

	deleteBatDeal2();
}

function deleteBatDeal2(){
	var batchid = getCheckedValues("trades");
   if(null == batchid || "" == batchid) {
   		alert("请选择需要删除的批量任务！");
   }else {
   		MessageBox.confirm("提示信息","确定删除批量任务吗?",function(btn){
		if(btn=='ok'){
		   $.ajax.submit('QueryForm','delBatTrades','&BATCH_IDS='+batchid,'GantPart',function(data){
				$.endPageLoading();
				$.showSucMessage("批量数据删除成功!","");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
   }
}

// 显示批量任务详情
function showBatTaskInfo(obj) {
	var batchTaskId = $(obj).attr('batchtaskid');
	var params = '&BATCH_TASK_ID='+batchTaskId;
	$.popupPage("group.bat.batdeal.BatTaskInfo","queryTaskInfo",params,"批量任务信息","700","350");
}

//oper: 取消：cancel；终止：terminate；状态修改中的 确定：loading；导出完成后的确定：ok；导出失败时的确定：fail；
//导入组件监控
function exportAction(oper, domid) {
		
	if (oper == "cancel") {
	
	} else if (oper == "terminate") {
	
	} else if (oper == "loading") {
	
	} else if (oper == "ok") {
		finishImportData();
	} else if (oper == "fail") {
		alert("批量导入失败，请联系系统管理员！");
	} 
	return true;
}

/**
 * 金库校验
 */
function doAuth(objId) {
	
	//是否金库验证开关
	var grpbat4Aflag = $('#GRPBAT4AFLAG').val();
	
  	if(grpbat4Aflag == "false" || grpbat4Aflag == false)
  	{
		return true;
	}
	
	var batch_oper_code = $('#BATCH_OPER_CODE').val();
	
    var operCode = "GRPBAT_4A_" + batch_oper_code;
    	
	beginPageLoading("正进行金库认证..");
	
	$.treasury.auth(operCode, function(ret){
		
		endPageLoading();
		
		if(true === ret){
			
			has4AOK = true;
			alert("认证成功");
			$("#" +  objId).find("button[class='e_button-form']")[1].click();//再次触发开始导入按钮
			
			return true;
		}else{
			
			alert("认证失败");
			
			return false;
		}
	});
    
    return true;
}

/**
 *  REQ201801150022_新增IMS号码开户人像比对功能
 *  <br/>
 *  打印电子工单
 *  @author zhuoyingzhi
 *  @date 20180326
 */
 function buildBatchBill(batchTaskId)
 {
	   beginPageLoading("加载电子工单信息...");
	   $.ajax.submit(null,'queryBuildBatchBillInfo','&BATCH_TASK_ID='+batchTaskId,null,function(data){
			$.endPageLoading();
			
		     var detail="";     
		     
		     detail +="##"+"受理工号:"+data.get("TRADE_STAFF_ID", "");
		     detail +="##"+"受理业务时间:"+data.get("ACCEPT_DATE", "");
		     detail +="##"+"受理业务类型:"+data.get("TRADE_TYPE_NAME", "");
				
				var edocXml=[];
				edocXml.push('<?xml version="1.0" encoding="utf-8" ?>');
				edocXml.push('<IN>');
				edocXml.push('	<op_code>'+"10"+'</op_code>');
				edocXml.push('	<billid>'+data.get("BATCH_ID","")+'</billid>');//批次号
				
				edocXml.push('	<work_name>'+data.get("TRADE_STAFF_NAME", "")+'</work_name>');//工号
				edocXml.push('	<work_no>'+data.get("TRADE_STAFF_ID", "")+'</work_no>');
				edocXml.push('	<org_info>'+data.get("ORG_INFO", "")+'</org_info>');//部门编码(组织机构编码)
				edocXml.push('	<org_name>'+data.get("ORG_NAME", "")+'</org_name>');//部门名称
					
				edocXml.push('	<phone>'+data.get("SERIAL_NUMBER", "")+'</phone>');
				edocXml.push('	<serv_id>'+data.get("USER_ID", "")+'</serv_id>');
				edocXml.push('	<op_time>'+data.get("ACCEPT_DATE", "")+'</op_time>');//受理时间
				
				/*获取照片标志信息*/
				//摄像标识
				edocXml.push('	<pic_tag>'+data.get("PIC_ID", "")+'</pic_tag>');

				
				edocXml.push('	<busi_list>');
				edocXml.push('		<busi_info>');
				edocXml.push('			<op_code>'+"3008"+'</op_code>');//业务类型
				edocXml.push('			<sys_accept>'+data.get("BATCH_ID","")+'</sys_accept>');//批次号
				edocXml.push('			<busi_detail>'+detail+'</busi_detail>');
				edocXml.push('		</busi_info>');
				edocXml.push('	</busi_list>');

				edocXml.push('	<verify_mode>'+data.get("VERIFY_MODE", "")+'</verify_mode>');
				edocXml.push('	<id_card>'+data.get("ID_CARD","")+'</id_card>');
				edocXml.push('	<cust_name>'+data.get("CUST_NAME","")+'</cust_name>');
				edocXml.push('	<brand_name>'+data.get("PRODUCT_NAME","")+'</brand_name>');
				edocXml.push('	<copy_flag></copy_flag>');
				edocXml.push('	<agm_flag></agm_flag>');

				edocXml.push('</IN>');
				
				var edocStr = edocXml.join("");
		     
		    MakeBillActiveX.MainBuildBill(edocStr);
		 	edocStr=null;
			detail=null;
			edocXml=null;
			$.printMgr.edocPrintLock=false;		//关闭打印锁
			$.endPageLoading();			
			
			//点击了打印电子工单 
	        $("#PRINT_TAG").val("1");
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});	 	
 }


 /**
  *  REQ201807240010新增批量开户界面人像比对、受理单打印优化需求
  *  <br/>
  *  关闭弹出框
  *  @author mengqx
  *  @date 20190114
  */
 function  buildBatchBillRemove(){
 	var printTag=$("#PRINT_TAG").val();
 	if(printTag == 0){
 		alert("请先打印电子工单");
 		return false;
 	}
     //关闭界面,
 	if($("#SUBMIT_MSG_PANEL").length){
 		$("#SUBMIT_MSG_PANEL").remove();
 	}
 	$("#PRINT_TAG").val("0");
 }
 
 function showMessageBatchBill(hint_message) {
	 	var batchTaskId = $('#BATCH_TASK_ID').val();
		var msgArr = [];
		msgArr.push('<div id="SUBMIT_MSG_PANEL" class="c_popup">	');
			msgArr.push('<div class="c_popupWrapper">	');
			msgArr.push('<div class="c_popupHeight"></div>	');
			msgArr.push('<div class="c_popupBox">	');
			msgArr.push('<div class="c_popupTitle">	');
			msgArr.push('<div class="text">'	+ ("业务受理成功")	+ 	'</div>	');
			msgArr.push('</div>	');
			msgArr.push('<div class="c_popupContent"><div class="c_popupContentWrapper">	');
			msgArr.push('<div class="c_msg c_msg-popup c_msg-success ">	');
			msgArr.push('	<div id="SUBMIT_MSG_TITLE" class="title">'+hint_message+'</div>	');
			msgArr.push('	<div id="SUBMIT_MSG_CONTENT" class="content"></div>	');
			msgArr.push('</div>	');
			msgArr.push('<div id="SUBMIT_MSG_BTN" class="c_submit">	');

				//打印电子工单
				msgArr.push('<button class="print" onclick="javascript:buildBatchBill('+batchTaskId+');">	');
				msgArr.push('<i></i><span>'	+("打印电子工单")+	'</span></button>	 ');
				
				//关闭
				msgArr.push('<button class="cancel" onclick="javascript:buildBatchBillRemove();">	');
				msgArr.push('<i></i><span>'	+("关闭")+	'</span></button>	 ');

			msgArr.push('</div>	');
			msgArr.push('</div></div>	');
			msgArr.push('<div class="c_popupBottom"><div></div></div>	');
			msgArr.push('<div class="c_popupShadow"></div>	');
			msgArr.push('</div>	');
			msgArr.push('</div>	');
			msgArr.push('<iframe class="c_popupFrame"></iframe>	');
			msgArr.push('<div class="c_popupCover"></div>	');
		msgArr.push('</div>	');
			
		$(document.body).append(msgArr.join(""));
	}

 
