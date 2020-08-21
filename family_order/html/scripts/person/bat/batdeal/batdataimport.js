
function querytaskinfo() {
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('TaskInfoPart', 'queryBatTaskByPK', null, 'TaskDataPart,DataImportPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function changePreMode(){
	var import_mode = $("#IMPORT_MODE").val();
	$("#BEGIN_NUM").val('');
	$("#END_NUM").val('');
	//$("#cond_STICK_LIST").val('');
	if(import_mode == '0'){
		$("#PRENUM").css("display","none");
		$("#PREFILE").css("display","block");
	}else if(import_mode == '1'){
		$("#PRENUM").css("display","block");
		$("#PREFILE").css("display","none");
	}else if(import_mode == '2'){
		$("#PRENUM").css("display","block");
		$("#PREFILE").css("display","none");	
	}else{
		$("#PRENUM").css("display","none");
		$("#PREFILE").css("display","none");
	}
}

function changePwlwMode(){
	var import_mode = $("#PWLW_IMPORT_MODE").val();
	$("#PWLW_BEGIN_NUM").val('');
	$("#PWLW_END_NUM").val('');
	//$("#cond_STICK_LIST").val('');
	if(import_mode == '0'){
		$("#PWLWNUM").css("display","none");
		$("#PWLWFILE").css("display","block");
	}else if(import_mode == '1'){
		$("#PWLWNUM").css("display","block");
		$("#PWLWFILE").css("display","none");
	}else if(import_mode == '2'){
		$("#PWLWNUM").css("display","block");
		$("#PWLWFILE").css("display","none");	
	}else{
		$("#PWLWNUM").css("display","none");
		$("#PWLWFILE").css("display","none");
	}
}

function changeTdMode(){
	var import_mode = $("#TD_IMPORT_MODE").val();
	$("#TD_BEGIN_NUM").val('');
	$("#TD_END_NUM").val('');
	//$("#cond_STICK_LIST").val('');
	if(import_mode == '0'){
		$("#TDNUM").css("display","none");
		$("#TDFILE").css("display","block");
	}else if(import_mode == '1'){
		$("#TDNUM").css("display","block");
		$("#TDFILE").css("display","none");
	}else if(import_mode == '2'){
		$("#TDNUM").css("display","block");
		$("#TDFILE").css("display","none");	
	}else{
		$("#TDNUM").css("display","none");
		$("#TDFILE").css("display","none");	
	}
}

function changeTrunkMode(){
	var import_mode = $("#TRUNK_IMPORT_MODE").val();
	$("#TRUNK_BEGIN_NUM").val('');
	$("#TRUNK_END_NUM").val('');
	//$("#cond_STICK_LIST").val('');
	if(import_mode == '0'){
		$("#TRUNKNUM").css("display","none");
		$("#TRUNKFILE").css("display","block");
	}else if(import_mode == '1'){
		$("#TRUNKNUM").css("display","block");
		$("#TRUNKFILE").css("display","none");
	}else if(import_mode == '2'){
		$("#TRUNKNUM").css("display","block");
		$("#TRUNKFILE").css("display","none");	
	}else{
		$("#TRUNKNUM").css("display","none");
		$("#TRUNKFILE").css("display","none");	
	}
}

function changeActMode(){
	var import_mode = $("#MODIFYACYCINFO_IMPORT_MODE").val();
	//$("#cond_STICK_LIST").val('');
	if(import_mode == '1'){
		//$("#ACTNUM").css("display","block");
		$("#ACTFILE").css("display","none");
	}else if(import_mode == '2'){
		//$("#ACTNUM").css("display","none");
		$("#ACTFILE").css("display","block");
	}else{
		//$("#ACTNUM").css("display","none");
		$("#ACTFILE").css("display","none");
	}
}

//导入数据，返回集合
var importLoadData;

function importData(){
	if(!$.validate.verifyAll("DataImportPart")){
		return false;
	}
	var BATCH_OPER_CODE = $("#BATCH_OPER_CODE").val();
	var IMPORT_MODE = '0';
	
	if(BATCH_OPER_CODE == 'CONSTRUCTIONADDR')
	{
		var returnFlag = false;
		var tag = $("#4APARIS_TAG").val();
		if(tag == "0"){
			beginPageLoading("正进行金库认证...");
			$.treasury.auth("BAT_4A_CONSTRUCTIONADDR",function(ret){
				endPageLoading();
				if(true == ret){
					alert("认证成功");
					$("#4APARIS_TAG").val("1");
				}else{
					alert("认证失败");
					$("#4APARIS_TAG").val("0");
				}
			});
			return returnFlag;
		}
	}
	
	if (BATCH_OPER_CODE == 'MODIFYTDPSPTINFO') {
		/*
		if (BATCH_OPER_CODE == 'BATREALNAME' || BATCH_OPER_CODE == 'BATUPDATEPSW' || BATCH_OPER_CODE == 'MODIFYTDPSPTINFO') {				
		var bat4acheckstring = "";
		if (BATCH_OPER_CODE == 'BATREALNAME') {
			bat4acheckstring = "BAT_4A_BATREALNAME";
		} else if (BATCH_OPER_CODE == 'BATUPDATEPSW') {
			bat4acheckstring = "BAT_4A_BATUPDATEPSW";
		} else if (BATCH_OPER_CODE == 'MODIFYTDPSPTINFO') {
			bat4acheckstring = "BAT_4A_MODIFYTDPSPTINFO";
		}
		*/
		var returnFlag = false;
		var tag = $("#4APARIS_TAG").val();
		if (tag == "0") {
			beginPageLoading("正进行金库认证...");
			$.treasury.auth("BAT_4A_CONSTRUCTIONADDR", function(ret) {
				endPageLoading();
				if (true == ret) {
					alert("认证成功");
					$("#4APARIS_TAG").val("1");
				} else {
					alert("认证失败");
					$("#4APARIS_TAG").val("0");
				}
			});
			return returnFlag;
		}
	}	
	
	if(BATCH_OPER_CODE == 'CREATEPREUSER' || BATCH_OPER_CODE == 'BATACTIVECREATEUSER'){
		IMPORT_MODE = $("#IMPORT_MODE").val();
		if(IMPORT_MODE == '0'){
			STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
		}else if(IMPORT_MODE == '1' || IMPORT_MODE == '2'){
			var BEGIN_NUM = $("#BEGIN_NUM").val();
			var END_NUM = $("#END_NUM").val();
			if(BEGIN_NUM == ''){
				alert('起始号不能为空');
				return false;
			}else if(END_NUM == ''){
				alert('终止号不能为空');
				return false;
			}
		}
	}else if(BATCH_OPER_CODE == 'BATCREATETRUNKUSER' || BATCH_OPER_CODE == 'BATAPPENDTRUNKUSER' ){
		IMPORT_MODE = $("#TRUNK_IMPORT_MODE").val();
		if(IMPORT_MODE == '0'){
			STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
		}else if(IMPORT_MODE == '1' || IMPORT_MODE == '2'){
			var BEGIN_NUM = $("#TRUNK_BEGIN_NUM").val();
			var END_NUM = $("#TRUNK_BEGIN_NUM").val();
			if(BEGIN_NUM == ''){
				alert('起始号不能为空');
				return false;
			}else if(END_NUM == ''){
				alert('终止号不能为空');
				return false;
			}
		}
	}else if(BATCH_OPER_CODE == 'CREATEPRETDUSER'){
		IMPORT_MODE = $("#TD_IMPORT_MODE").val();
		if(IMPORT_MODE == '0'){
			STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
		}else if(IMPORT_MODE == '1' || IMPORT_MODE == '2'){
			var BEGIN_NUM = $("#TD_BEGIN_NUM").val();
			var END_NUM = $("#TD_BEGIN_NUM").val();
			if(BEGIN_NUM == ''){
				alert('起始号不能为空');
				return false;
			}else if(END_NUM == ''){
				alert('终止号不能为空');
				return false;
			}
		}
	}else if(BATCH_OPER_CODE == 'MODIFYACYCINFO'){
		IMPORT_MODE = $("#MODIFYACYCINFO_IMPORT_MODE").val();
		if(IMPORT_MODE == '2'){
			STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
		}else if(IMPORT_MODE == '1'){
			var BANK_CODE = $("#BANK_CODE").val();
			var BANK_ACCT_NO = $("#BANK_ACCT_NO").val();
			if(BANK_CODE == ''){
				alert('银行名称不能为空');
				return false;
			}else if(BANK_ACCT_NO == ''){
				alert('银行账号不能为空');
				return false;
			}
		}
	}else if(BATCH_OPER_CODE == 'CREATEPREUSER_PWLW'){
		IMPORT_MODE = $("#PWLW_IMPORT_MODE").val();
		if(IMPORT_MODE == '0'){
			STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
		}else if(IMPORT_MODE == '1' || IMPORT_MODE == '2'){
			var BEGIN_NUM = $("#PWLW_BEGIN_NUM").val();
			var END_NUM = $("#PWLW_BEGIN_NUM").val();
			if(BEGIN_NUM == ''){
				alert('起始号不能为空');
				return false;
			}else if(END_NUM == ''){
				alert('终止号不能为空');
				return false;
			}
		}
	}else {
		STICK_LIST = $("#cond_STICK_LIST").val();
		if(STICK_LIST == ''){
		 	alert('请先上传文件');
		 	return false;
		}
	}
	
	//add by guonj 增加稽核流程
	if(BATCH_OPER_CODE=="GRPDISCNTCHGSPEC"){
		if($("#MEB_VOUCHER_FILE_LIST")){
			 var voucherFileList = $("#MEB_VOUCHER_FILE_LIST").val();
			 if(voucherFileList == ""){
				 alert("请上传凭证信息!");
				 return false;
			 } 
		 }
		 if($("#AUDIT_STAFF_ID")){
			 var auditStaffId = $("#AUDIT_STAFF_ID").val();
			 if(auditStaffId == ""){
				 alert("请选择稽核员!");
				 return false;
			 } 
		 }
	}
	
	
	var truthBeTold = confirm("确定要导入数据吗?");
	if (!truthBeTold) {
		return false;
	} 
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('TaskDataPart,TaskInfoPart,DataImportPart','importData','','',function(data){
		importLoadData=data;
		var title= "批量数据导入结果!";
		var dealType=data.get("DEAL_TYPE");
		var hint_message = data.get("HINT_MESSAGE");
		var batch_id = data.get("BATCH_ID");
		var batchTaskId = data.get("cond_BATCH_TASK_ID");
		var FAILED_TYPE = data.get("FAILED_TYPE");
		if(dealType == '1'){
			hint_message += "批量导入成功，并且已启动，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！批次号：" +batch_id + "<br/>";
		}else if(dealType == '2'){
			if(BATCH_OPER_CODE == 'CREATEPREUSER_PWLW'||BATCH_OPER_CODE == 'CREATEPREUSER_M2M' || BATCH_OPER_CODE == 'TD_FIXED_PHONE_STOP' || BATCH_OPER_CODE == 'TT_FIXED_PHONE_STOP' || BATCH_OPER_CODE == 'BATREALNAME' || BATCH_OPER_CODE == 'BATUPDATEPSW' || BATCH_OPER_CODE == 'MODIFYTDPSPTINFO'){
				//物联网开户    行业应用卡
				hint_message += "批量导入成功，请点击‘打印电子工单’按钮后再按[<a href=\"#nogo\" onclick=\"javascript:openNav('批量任务启动','bat.battaskstart.BatTaskStart', 'queryBatTaskStart','&FROM_PAGE=1&cond_BATCH_TASK_ID="+batchTaskId+"');\">批量任务启动</a>]进入启动页面!批次号："+batch_id + "<br/>";
			}else{
				hint_message += "批量导入成功，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！按[<a href=\"#nogo\" onclick=\"javascript:openNav('批量任务启动','bat.battaskstart.BatTaskStart', 'queryBatTaskStart','&FROM_PAGE=1&cond_BATCH_TASK_ID="+batchTaskId+"');\">批量任务启动</a>]进入启动页面!批次号："+batch_id + "<br/>";
			}
		}
		if(FAILED_TYPE == '1'){
			var DATASET_SIZE = data.get("DATASET_SIZE");
			var SUCC_SIZE = data.get("SUCC_SIZE");
			var FAILD_SIZE = data.get("FAILD_SIZE");
			var FILE_ID = data.get("FILE_ID");
			var ERROR_URL = data.get("ERROR_URL");
			hint_message += "批量导入情况：共导入" + DATASET_SIZE + "条<br/>成功" + SUCC_SIZE + "条<br/>失败" + FAILD_SIZE + "条<br/>请击<a href="+ERROR_URL+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
		}
		
		 /**
		  * REQ201707170020_新增物联卡开户人像采集功能
		  * @author zhuoyingzhi
		  * @date 20170824
		  */
		  if(BATCH_OPER_CODE == 'CREATEPREUSER_PWLW'|| BATCH_OPER_CODE == 'CREATEPREUSER_M2M'){
			  //物联网开户
			  
			  var DATASET_SIZE_PWLW;
			  if(FAILED_TYPE == '1'){
				  //文件导入
				  //成功条数
				  DATASET_SIZE_PWLW = data.get("DATASET_SIZE");
			  }
			  if(dealType == '2' ||(FAILED_TYPE == '1'&&DATASET_SIZE_PWLW >= 1)){
//				  if(BATCH_OPER_CODE == 'CREATEPREUSER_PWLW'){
//			       		MessageBox.show({
//			    			"title":"成功提示",
//			    			"msg":"业务受理成功!" +
//			    					"<br/>" +
//			    					"<div style='font-size: 13px;color: black;'>"+hint_message+"</div>",
//			    			"success":true,
//			    			"fn":function(btn){
//			    				    //回调
//			    					if(btn == "ok" || btn=="cancel"){	
//			    						//setPopupReturnValue('','');
//			    					}else if(btn == "ext0"){
//			    						return false;
//			    					}
//			    				},
//			                 "beforeHide":function(btn){
//			    					if(btn == "ok"){	
//			    						return true;													
//			    					}else if(btn == "ext0"){
//			    						//打印电子工单
//			    						buildBatchBill();
//			    						return false;
//			    					}
//			    				},
//			    			"buttons":$.extend({ok:"取消"},{ext0:"打印电子工单,print"})
//			       		}); 					  
//				  }else if(BATCH_OPER_CODE == 'CREATEPREUSER_M2M') {
				  
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
						$.showSucMessage(title,hint_message);
					}
//				  }
			  }else{
				  $.showSucMessage(title,hint_message);
			  }
		  }else{
		  		// TD二代无线固话批量停机 和 铁通固话批量停机 需要受理单打印
			  //增加
              if(BATCH_OPER_CODE == 'TT_FIXED_PHONE_STOP' || BATCH_OPER_CODE == 'TD_FIXED_PHONE_STOP' || BATCH_OPER_CODE == 'BATREALNAME' || BATCH_OPER_CODE == 'BATUPDATEPSW' || BATCH_OPER_CODE == 'MODIFYTDPSPTINFO'){
                  showMessageBatchBill(hint_message);
			  }else {
                  $.showSucMessage(title,hint_message);
			  }
		  }
		/*****************************************/
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 *  批量物联网开户打印电子工单
 *  @author zhuoyingzhi
 *  @date 20170908
 */ 
 function buildBatchBill()
 {
	var data=importLoadData;
    var detail="";
    var trade_type_code = data.get("TRADE_TYPE_CODE","");

     detail +="##"+"受理工号:"+data.get("TRADE_STAFF_ID", "");
     detail +="##"+"受理业务时间:"+data.get("ACCEPT_DATE", "");
     detail +="##"+"受理业务类型:"+data.get("TRADE_TYPE_NAME", "");
		
		var edocXml=[];
		edocXml.push('<?xml version="1.0" encoding="utf-8" ?>');
		edocXml.push('<IN>');
		if (trade_type_code != ""){
            edocXml.push('	<op_code>'+trade_type_code+'</op_code>');
        }else {
            edocXml.push('	<op_code>'+"10"+'</op_code>');
        }
		edocXml.push('	<billid>'+data.get("BATCH_ID","")+'</billid>');//批次号
		
		edocXml.push('	<work_name>'+data.get("TRADE_STAFF_NAME", "")+'</work_name>');//工号
		edocXml.push('	<work_no>'+data.get("TRADE_STAFF_ID", "")+'</work_no>');
		edocXml.push('	<org_info>'+data.get("ORG_INFO", "")+'</org_info>');//部门编码(组织机构编码)
		edocXml.push('	<org_name>'+data.get("ORG_NAME", "")+'</org_name>');//部门名称
			
		/**
		 * 局方要求  把联系人的手机号码修改为批次号 
		 * @author zhuoyingzhi
		 * @date 20180621
		 */
		edocXml.push('	<phone>'+data.get("BATCH_ID","")+'</phone>');
		edocXml.push('	<serv_id>'+data.get("USER_ID", "")+'</serv_id>');
		edocXml.push('	<op_time>'+data.get("ACCEPT_DATE", "")+'</op_time>');//受理时间
		
		/*获取照片标志信息*/
		//摄像标识
		edocXml.push('	<pic_tag>'+data.get("PIC_ID", "")+'</pic_tag>');

		
		edocXml.push('	<busi_list>');
		edocXml.push('		<busi_info>');
		 if (trade_type_code != ""){
			 edocXml.push('	<op_code>'+trade_type_code+'</op_code>');//业务类型
		 }else {
			 edocXml.push('	<op_code>'+"10"+'</op_code>');
		 }
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
	
	//点击了电子
	$("#PRINT_TAG").val("1");
	
 }
//关闭弹出框
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
}
 function showMessageBatchBill(hint_message) {

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
				msgArr.push('<button class="print" onclick="javascript:buildBatchBill();">	');
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
/**
 *
 * @author liujia
 * @returns
 */
function changeNumberType(){
	var number_type =  $("#NUMBER_TYPE")[0].options[$("#NUMBER_TYPE")[0].selectedIndex].value;
	// js中  "" == 0
	if(number_type == 0+"" ){
		$("#span_number_type").addClass("e_required");
		$("#MSISDN_TYPE").attr("nullable","no");
	}else {
		$("#span_number_type").removeClass("e_required");
		$("#MSISDN_TYPE").removeAttr("nullable");
		$("#MSISDN_TYPE").val("");
	}
}