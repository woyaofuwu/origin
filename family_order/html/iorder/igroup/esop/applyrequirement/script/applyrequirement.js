var ACTION_EXITS = "3";
var offerChaList;
var DataLineList;
var BbossAttrList;

var runTag = true;				  
$(function(){
		  
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
				if(containSpecial(fileNameArr[i])){
					MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
					return false; 
				}
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "P");
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#ATTACH_FILE_NAME").val(obj.NAME);
		$("#ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#ATTACH_FILE_NAME").val("");
		$("#ATTACH_FILE_ID").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
	
	$("#upload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
				if(containSpecial(fileNameArr[i])){
					MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
					return false; 
				}
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "D");
				fileList.add(data);
			}
		}
		$("#DISCNT_ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#DISCNT_ATTACH_FILE_NAME").val(obj.NAME);
		$("#DISCNT_ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup07");
	});
	
	$("#upload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#DISCNT_ATTACH_FILE_NAME").val("");
		$("#DISCNT_ATTACH_FILE_ID").val("");
		
		$("#DISCNT_ATTACH_FILE_LIST").text("");
	});
	
	$("#marketingupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
				if(containSpecial(fileNameArr[i])){
					MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
					return false; 
				}
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "M");
				fileList.add(data);
			}
   
					   
					  
   
				 
				  
   
				
		}
		$("#C_MARKETING_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#C_MARKETING_FILE_NAME").val(obj.NAME);
		$("#C_MARKETING_FILENAME").val(obj.NAME);
		$("#C_MARKETING_FILE_ID").val(obj.ID);
		
		hidePopup("popup09");
	});
	$("#marketingupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#C_MARKETING_FILE_NAME").val("");
		$("#C_MARKETING_FILE_ID").val("");
		$("#C_MARKETING_FILENAME").val("");
		$("#C_MARKETING_FILE_LIST").text("");
	});
	
	/*$("#speauditfile").afterAction(function(e, file){
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "T");
		$("#speauditfileDate").text(data.toString());
		//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
	});*/
	
	if($("#BPM_TEMLET_ID").val()!="GROUPATTRCHANGE"&&$("#IS_READONLY").val()!="true"){
		initPageOfferCha();
	}
	
	//初始化页面只运行一次
	if(runTag){
		var ibsysId = $("#IBSYSID").val();
		if(ibsysId)
		{
			setGroupInfo();
			//repAuditOfferAfterAction(ibsysId);
			//设置产品不可编辑部分
			setOfferDisable();
			
			
			datalineOperTypeOnChange();
			ifNewProjectName();
			changeBusinessType();
			ifNewContract();
			confCrm();
			
			reflashTips();
			//加载专线属性
			if(typeof loadDataLineList=='function'){
				initPageOfferCha();
				loadDataLineList();

				if($("#cond_TEMPLET_ID").val()=="DIRECTLINECHANGESIMPLE"){
					addExprotImport();
				}
			}
			if(typeof loadMarketingLineList=='function'){
				loadMarketingLineList(setOfferDisable);
				if($("#C_MARKETING_FILE_ID").val()){
					var fileListM = $("#C_MARKETING_FILE_ID").val();
					marketingupload.loadFile(fileListM);
				}
			}
			
			//仅开通
			if($("#cond_TEMPLET_ID").val()=="EDIRECTLINEOPEN"||$("#cond_TEMPLET_ID").val()=="EDIRECTLINEOPENIDC"){
				//处理账户
				initAcctData();
			}
			
			//加载附件
			if($("#UPLOAD_FILELIST").val()){
				var fileListD = $("#UPLOAD_FILELIST").val();
				upload.loadFile(fileListD);
			}	
			if($("#FILEUPLOAD_FILELIST").val()){
				var fileListP = $("#FILEUPLOAD_FILELIST").val();
				fileupload.loadFile(fileListP);
			}
			
			runTag=false;
		}
	}
	
	
});

//刷新简单变更的提示块
function reflashTips(){
	var templetId = $("#cond_TEMPLET_ID").val();
	var changeMode = $("#pattr_CHANGEMODE").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	
	if(templetId=="DIRECTLINECHANGESIMPLE"){
		if(""!=changeMode||null!=changeMode||undefined==changeMode){
				$.ajax.submit("", "hintInfo", "&PRODUCT_ID="+offerCode+"&CHANGEMODE="+changeMode, "TipInfoPart", function(data){
					$.endPageLoading();
				}, 
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				},{async:true});
		}
	}
}


function defaultValueForBuildSetion(){
	debugger;
	var bizrange = $("#pattr_BIZRANGE").val();
	var condTempletId = $("#cond_TEMPLET_ID").val();
	var changeMode = $("#pattr_CHANGEMODE").val();
	
	$("#pattr_BUILDINGSECTION").val("");
	$("#pattr_BUILDINGSECTION_span").removeAttr("tip");
	if(bizrange=='省内跨地市'){
		$("#pattr_BUILDINGSECTION_span").attr("tip","勘察、变更勘察、开通、变更开通、减容请选择客户响应中心受理!");
		if(condTempletId=="EDIRECTLINEOPEN"||condTempletId=="DIRECTLINECHANGESIMPLE"||condTempletId == "ERESOURCECONFIRMZHZG"||condTempletId == "ECHANGERESOURCECONFIRM"||condTempletId == "EDIRECTLINECHANGE"){
			$("#pattr_BUILDINGSECTION").val("客户响应中心");
		}
		if(changeMode =="同楼搬迁" ){
			$("#pattr_BUILDINGSECTION").val("");
			$("#pattr_BUILDINGSECTION_span").attr("tip","同楼搬迁流程请根据搬迁点位所处地市选择AZ端建设单位受理!");	
		}
		if(changeMode == "减容"){
			$("#pattr_BUILDINGSECTION_span").attr("tip","减容请选择客户响应中心受理!");
			$("#pattr_BUILDINGSECTION").val("客户响应中心");
		}
		if(condTempletId=="MANUALSTOP"||condTempletId=="MANUALBACK"){
			$("#pattr_BUILDINGSECTION_span").attr("tip","停/复机流程请选择Z端（分支节点）建设单位受理!");
		}		
	}	
	if(condTempletId=="EDIRECTLINECANCEL"){
		$("#pattr_BUILDINGSECTION_span").attr("tip","注销流程请选择Z端（分支节点）建设单位受理!");
	}
}


function initPageOfferCha()
{
	debugger;
	var subOfferCode = "";
	var userId = $("#apply_USER_ID").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var ibsysId = $("#IBSYSID").val();
	var subIbsysId = $("#SUB_IBSYSID").val();
	var reecordNum = $("#RECORD_NUM").val();
	
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	
	if(showParamPart == "2")
	{
		var datalinOperType = $("#DATALINE_OPER_TYPE").val();
		if(datalinOperType == "1"){
			serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
		}
		var serialSEQ = $("#DATALINE_MAX_INDEX").val();
		var serialNO = $("#serialNO").val();
		/*if(operType == "21" || operType == "22"){
			userId = $("#"+offerChaHiddenId.substr(10)).attr("userId");
		}*/
	}
	
	//是否同步执行
	var isAsync = true;
	if($("#IS_READONLY").val()=="true"||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		isAsync = false;
	}
	var param="";
	if(ibsysId)
	{
		param = "&IBSYSID="+ibsysId+"&RECORD_NUM="+reecordNum+"&SUB_IBSYSID="+subIbsysId+"&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex+"&NOT_QRY=true";
	}
	else
	{
		param = "&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex+"&NOT_QRY=true";
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    },
    {
		async: isAsync  
	});
}

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

function setGroupInfo(){
	changeAreaByCity();
	$.enterpriseLogin.refreshGroupInfo($("#cond_GROUP_ID_INPUT").val());
	$("#cond_COUNTYNAME").val($("#hid_COUNTYNAME").val());
}

function setOfferDisable(){
	$("#cond_OPER_TYPE").attr("disabled","true");
	$("#cond_OFFER_CODE").attr("disabled","true");
	$("#cond_TEMPLET_ID").attr("disabled","true");
	$("#DATALINE_OPER_TYPE").attr("disabled","true");
	$("#DATALINE_SERIAL_NUMBER").attr("disabled","true");
	$("#cond_OFFER_NAME").attr("disabled","true");
	$("#cond_OFFER_NAME").parent().parent().parent().removeAttr("ontap");
	var templetId = $("#cond_TEMPLET_ID").val();
	if(templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		$("#pattr_BIZRANGE").attr("disabled","true");
		$("#pattr_CHANGEMODE").attr("disabled","true");
		
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode=="停机"||changeMode=="扩容"){
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "no");
		}else{
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "none");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "");
		}
		
	}
	
}

											   
											   
																	  
 

function changeOperType()
{
	$("#cond_OFFER_CODE").val("");
	$("#cond_OFFER_NAME").val("");
	$("#OFFER_CODE_LI").css("display", "none");
	$("#ParamPart").css("display", "none");
}

function changeTempletId()
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var inesccobusi400 = $("#CREATE_INESSCOBUSI400").val();
	var groupId = $("#cond_GROUP_ID").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one($("cond_OFFER_NAME")[0], "请选择产品！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	if(groupId == null || groupId == ""){
		$.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息，请输入集团编码后回车键查询！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	var userId = $("#apply_USER_ID").val();
	var opertype = $("#cond_OPER_TYPE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	if(!templetId){
		return false;
	}
	var param = "&TEMPLET_ID="+templetId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryInfo", param, "OfferPart,ParamPart,UploadPart,LineSpecialParam", function(data){
		$.endPageLoading();
		debugger;
		
		//默认选客户中心和设置提示
		defaultValueForBuildSetion();
		
//		if(templetId =='EDIRECTLINECHANGE'){
//			if(offerCode=="7011"||offerCode=="7010" ){
//				var changeModeArr = new Array(new Array(), new Array());
//				changeModeArr[0] = new Array('扩容','异楼搬迁');
//				pattr_CHANGEMODE.empty();
//				pattr_CHANGEMODE.append(changeModeArr[0][0],changeModeArr[0][0]);
//				pattr_CHANGEMODE.append(changeModeArr[0][1],changeModeArr[0][1]);
//			}
//		}
		if(templetId =='DIRECTLINECHANGESIMPLE'){
			if(offerCode=="7012"||offerCode=="7010"||offerCode=="70121"||offerCode=="70122"){
				var changeModeArr = new Array(new Array(), new Array());
				changeModeArr[0] = new Array('减容','同楼搬迁');
				pattr_CHANGEMODE.empty();
				pattr_CHANGEMODE.append(changeModeArr[0][0],changeModeArr[0][0]);
				pattr_CHANGEMODE.append(changeModeArr[0][1],changeModeArr[0][1]);
			}
			
			addExprotImport();
		}
		//载入历史数据清空
		$("#hisConfCrmTable tbody").html("");
		
		$("#apply_USER_ID").val(userId);

		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
		if(templetId=='EDIRECTLINECONTRACTCHANGE'){
			$("#newContractPart").css("display", "");
			$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
				var data1 = new Wade.DataMap();
				data1.put("FILE_ID", file.fileId);
				data1.put("FILE_NAME", file.name);
				data1.put("ATTACH_TYPE", "C");
				$("#C_FILE_LIST").text(data1.toString());
				$("#C_FILE_LIST").val(file.fileId+':'+file.name);
				
				$("#C_FILE_LIST_NAME").val(file.name);
			});
			if(data.get("FILE_ID")!=null&&data.get("FILE_ID")!=""){
				var data2 = new Wade.DataMap();
				data2.put("FILE_ID", data.get("FILE_ID"));
				data2.put("FILE_NAME", data.get("FILE_NAME"));
				data2.put("ATTACH_TYPE", "C");
				$("#C_FILE_LIST").text(data2.toString());
				$("#C_FILE_LIST").val(data.get("FILE_ID")+':'+data.get("FILE_NAME"));
				$("#C_FILE_LIST_NAME").val(data.get("FILE_NAME"));
			}
		}
		if(templetId=="ECHANGERESOURCECONFIRM"){
			changeBusinessType();
			changeChangeMode();
		}
		
		$("#cond_TEMPLET_ID").val(templetId);
		$("#cond_OPER_TYPE").val(opertype);
		
		if(templetId=='DIRECTLINEMARKETINGADD' || templetId=='DIRECTLINEMARKETINGUPD'){
			$("#batchAdd").css("display", "none");
			$("#add").css("display", "block");
			$("#delete").css("display", "block");
		}
		
		if($("#IS_READONLY").val()=="true"&& templetId !='DIRECTLINECHANGESIMPLE'){
			$("#batchAdd").css("display", "none");
			$("#add").css("display", "none");
			$("#delete").css("display", "none");
			$("#batchOut").css("display","none");
		}
		
		//显示勘察选择，只支持互联网专线（taosx）
		var chooseConfcrm =$("#pattr_IF_CHOOSE_CONFCRM").val();
		if("0"==chooseConfcrm&&templetId =='EDIRECTLINEOPENPBOSS'){
			$("#batchAdd").css("display", "");
		}else{
			$("#batchAdd").css("display", "none");
		}
		if(!("7011"==offerCode||"70111"==offerCode||"70112"==offerCode)){
			$("#pattr_IF_CHOOSE_CONFCRM").attr("disabled","disabled");
		}
		
		
		$("#offerchalist").val("");
		$("#offerchalist").val(data.get("DATALINEINFO"));
		
		if("1"==inesccobusi400){
			if("FOURMANAGE"==templetId){
				$("#cond_OFFER_NAME").val("99832");
			}
			if("VOICEMANAGE"==templetId){
				$("#cond_OFFER_NAME").val("99833");	
			}
			if("TIMERREVIEWFOURMANAGE"==templetId){
				$("#cond_OFFER_NAME").val("99835");
			}
			if("TIMERREVIEWVOICEMANAGE"==templetId){
				$("#cond_OFFER_NAME").val("99834");
			}
		}
		
		//加载出专线派单信息，方便后面取ATTR_NAME
		initPageOfferCha();
		
		if(templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
			changeChangeMode();
			$("#batchAdd").css("display", "none");
			$("#batchOut").css("display","none");
		}
		
		$("#batchAddImportZHZG").beforeAction(function(e){
			return confirm('是否导入?');
		});
		
		$("#batchAddImportZHZG").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','batchImportFileZHZG',null,null,function(datas){
					
					var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
					
					var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					for (var i = 0; i < offerchaList.length; i++) {
						var offercha = offerchaList[i];
						temp.add(offercha);
					}
					for(var j=0;j<datas.length;j++){
						var batchImports = new Wade.DatasetList();
						var data = datas.get(j);
						loadBatchDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(m);
							var batchImport = new Wade.DataMap();
							batchImport.put("ATTR_CODE", key[m]);
							batchImport.put("ATTR_VALUE", value);
							batchImport.put("ATTR_NAME", $("input[name='"+key[m]+"']").attr("desc"));
							batchImports.add(batchImport);
						}

						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(batchImports);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(batchImports);
					}
					
					$("#offerchalist").val(temp);
					
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
					});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
		
		$("#batchAddImport").beforeAction(function(e){
			return confirm('是否导入?');
		});
		
		$("#batchAddImport").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','batchImportFile',null,null,function(datas){
					
					var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
					
					var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					for (var i = 0; i < offerchaList.length; i++) {
						var offercha = offerchaList[i];
						temp.add(offercha);
					}
					for(var j=0;j<datas.length;j++){
						var batchImports = new Wade.DatasetList();
						var data = datas.get(j);
						loadBatchDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(m);
							var batchImport = new Wade.DataMap();
							if (key[m] == "NOTIN_RSRV_STR1") {
								var bandWidth = new Wade.DataMap();
								bandWidth.put("ATTR_CODE", "pattr_BANDWIDTH");
								bandWidth.put("ATTR_VALUE", value);
								batchImports.add(bandWidth);
							}
							batchImport.put("ATTR_CODE", key[m]);
							batchImport.put("ATTR_VALUE", value);
							batchImports.add(batchImport);
						}

						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(batchImports);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(batchImports);
					}
					
					$("#offerchalist").val(temp);
					
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
					});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
		
		$("#batchChangeImportZHZG").beforeAction(function(e){
			var bizRange = $("#pattr_BIZRANGE").val();
			if(bizRange == ""){
				MessageBox.alert("提示", "请先选择业务范围！");
	            return false;
			}
			return confirm('是否导入?');
		});
		
		$("#batchChangeImportZHZG").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','batchChangeImportZHZG',null,null,function(datas){
					//将主页面上已存在的数据删除
					$("#DATALINE_PARAM_UL").children().remove();
					 
					var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
					
					var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					for (var i = 0; i < offerchaList.length; i++) {
						var offercha = offerchaList[i];
						temp.add(offercha);
					}
					for(var j=0;j<datas.length;j++){
						var batchImports = new Wade.DatasetList();
						var data = datas.get(j);
						loadBatchDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(m);
							var batchImport = new Wade.DataMap();
							batchImport.put("ATTR_CODE", key[m]);
							batchImport.put("ATTR_VALUE", value);
							batchImport.put("ATTR_NAME", $("input[name='"+key[m]+"']").attr("desc"));
							batchImports.add(batchImport);
						}

						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(batchImports);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(batchImports);
					}
					
					$("#offerchalist").val(temp);
					
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
					});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
		
		$("#batchChangeImport").beforeAction(function(e){
		    var offerListNum = $("#DATALINE_PARAM_UL").length;
		    if (offerListNum != 0) {
		        if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
		            MessageBox.alert("提示", "请先选择关联勘察单号！");
		            return false;
		        }
		    }
			var changeNode = $("#pattr_CHANGEMODE").val();
			if(changeNode==""){
				MessageBox.alert("提示", "请先选择业务调整场景！");
				return false;
			}
			return confirm('是否导入?');
		});
		
		$("#batchChangeImport").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','batchChangeImportZHZG',null,null,function(datas){
					//将主页面上已存在的数据删除
					$("#DATALINE_PARAM_UL").children().remove();
					 
					var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
					
					var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					for (var i = 0; i < offerchaList.length; i++) {
						var offercha = offerchaList[i];
						temp.add(offercha);
					}
					for(var j=0;j<datas.length;j++){
						var batchImports = new Wade.DatasetList();
						var data = datas.get(j);
						loadBatchDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(m);
							var batchImport = new Wade.DataMap();
							batchImport.put("ATTR_CODE", key[m]);
							batchImport.put("ATTR_VALUE", value);
							var tempName = $("input[name='"+key[m]+"']").attr("desc");
							var pattrTempName = "pattr_"+key[m];
							var tempName2 = $("input[name='"+pattrTempName+"']").attr("desc");
							if(tempName!=undefined){
								batchImport.put("ATTR_NAME", tempName);
							}else{
								batchImport.put("ATTR_NAME", tempName2);
							}
							batchImports.add(batchImport);
						}

						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(batchImports);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(batchImports);
					}
					
					$("#offerchalist").val(temp);
					
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
					});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function addExprotImport(){
	$("#ChangeSimpleExportInternet").beforeAction(function(e){
		setExportLineNos();
		var lineNos = $("#LINE_NOS").val();
		if(!lineNos){
			 MessageBox.alert("提示","请先勾选需要导出的专线！");
			 return false;
		}
	});
	$("#ChangeSimpleExportInternet").afterAction(function(e, status){
		if(status=="ok"){
			$("#OUT_FLAG").val("true");
		}
	});
	
	$("#ChangeSimpleImportInternet").beforeAction(function(e){
		var changeMode =  $("#pattr_CHANGEMODE").val();
		if(!changeMode){
			MessageBox.alert("提示","请先选择变更场景！");
			return false;
		}
		var lineNos = $("#LINE_NOS").val();
		var outFlag = $("#OUT_FLAG").val();
		if(!lineNos||"true"!=outFlag){
			 MessageBox.alert("提示","请先导出专线并修改后再导入！");
			 return false;
		}
	});
	$("#ChangeSimpleImportInternet").afterAction(function(e, status){
		if(status=="ok"){
			$.beginPageLoading("正在处理...");
			   ajaxSubmit('','ChangeSimpleImportInternet',null,null,function(datas){
				 //将主页面上已存在的数据删除
				   $("#DATALINE_PARAM_UL").children().remove();
				   	var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = 0; //从0开始取(后面会刷新隐藏域offerchalist)
					$("#DATALINE_MAX_INDEX").val(maxIndex);
					
					//var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					/*for (var j = 0; j < offerchaList.length; j++) {
						var offercha = offerchaList[j];
						temp.add(offercha);
					}*/
					
					for(var i=0;i<datas.length;i++){
						var dataLineInfos = new Wade.DatasetList();
						var data = datas.get(i);
						loadChangeDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(key[m]);
							var dataLineInfo = new Wade.DataMap();
							var dataLinediscnt = new Wade.DataMap();
							dataLinediscnt.put("ATTR_CODE", key[m]);
							dataLinediscnt.put("ATTR_VALUE", value);
							var name = "";
							if(key[m].indexOf("NOTIN_")!=0){
								name = "pattr_"+key[m];
							}else{
								name = key[m];
							}
							dataLinediscnt.put("ATTR_NAME",$("input[name="+name+"]").attr("desc"));
							dataLineInfos.add(dataLinediscnt);
						
						}
						
						if(changeMode=="减容"){
							var dataBandWidthInfo = new Wade.DataMap();
							dataBandWidthInfo.put("ATTR_CODE", "HIDDEN_BANDWIDTH");
							dataBandWidthInfo.put("ATTR_VALUE", data.get("BANDWIDTH"));
							dataBandWidthInfo.put("ATTR_NAME","原来带宽");
							dataLineInfos.add(dataBandWidthInfo);
						}
						
						
						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(dataLineInfos);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(dataLineInfos);
						
					}
					$("#offerchalist").val(temp);
			    
			    $.endPageLoading();
			    /*MessageBox.success("导入成功！", "页面展示导入数据!",function(){
			     //flag="true";
			    });*/
			   },function(error_code,error_info,derror){
			    $.endPageLoading();
			    showDetailErrorInfo(error_code,error_info,derror);
			   });
		}
	});
}

function chooseOfferAfterAction(offerCode, offerName, userId)
{
	var opertype = $("#cond_OPER_TYPE").val();
	var param = "&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryOffer", param, "OfferPart,ParamPart,UploadPart,LineSpecialParam", function(data){
		$.endPageLoading();
//		$("#cond_OFFER_CODE").val(offerCode);
//		$("#cond_OFFER_NAME").val(offerName);
		$("#apply_USER_ID").val(userId);

		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
		$("#cond_OPER_TYPE").val(opertype);
		
//		if($("#SHOW_PARAM_PART").val() == "2")
//		{
//			$("#LineSpecialParam").show();
//		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function repAuditOfferAfterAction(ibsysId)
{
	var userId = $("#apply_USER_ID").val();
	var offerCode = $("#BUSI_CODE").val();
	var nodeId = $("#NODE_ID").val();
	var bpmTempId = $("#BPM_TEMPLET_ID").val();
	var operType = $("#BUSIFORM_OPER_TYPE").val();
	var custId = $("#cond_CUST_ID").val();
	var param = "&BPM_TEMPLET_ID="+bpmTempId+"&NODE_ID="+nodeId+"&IBSYSID="+ibsysId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+operType+"&CUST_ID="+custId;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryOffer", param, "OfferPart,OfferAttrPart,ParamPart,UploadPart,attrPart,auditPart", function(data){
		$.endPageLoading();
		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function datalineOperTypeOnChange()
{
	var datalineOperType = $("#DATALINE_OPER_TYPE").val();
	var ifContract = $("#C_IF_NEW_CONTRACT").val();
	if(datalineOperType == 0)
	{//新增-新增集团产品用户
		$("#DATALINE_SERIAL_NUMBER_LI").css("display", "none");
		$("#DATALINE_SERIAL_NUMBER").attr("nullable", "yes");
		$("#DATALINE_SERIAL_NUMBER").val("");
		if(ifContract=='1'){
			debugger;
			ifNewContract();
		}
		$("#acctDealInfo").css("display", "");
	}
	else
	{//已有-在已有集团产品用户下新增专线
		$("#DATALINE_SERIAL_NUMBER_LI").css("display", "");
		$("#DATALINE_SERIAL_NUMBER").attr("nullable", "no");
		$("#acctDealInfo").css("display", "none");
		if(ifContract=='0'){
			MessageBox.alert("提示信息", "产品服务为已有,合同只可选择已有！");
			$("#C_IF_NEW_CONTRACT").val("");
			return false;
		}
	}
}

function getUserId(){
	var serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
	$.ajax.submit(this, "getUserIdByserialNum", "&SERIAL_NUMBER="+serialNumber, "", function(data){
		var userId = data.get("USER_ID");
		$("#DATALINE_USER_ID").val(userId);
		$("#C_IF_NEW_CONTRACT").val("");
		$("#contractSelectPart").css("display", "none");//已有 合同选择元素
		$("#contractContentPart").css("display", "none");//已有 合同信息元素
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

/**************************商品特征-开始*****************************/
function initOfferCha(offerChaHiddenId)
{
	debugger;
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); //将属性要保存到对应隐藏域的id存到隐藏域中
	var subOfferCode = "";
	var userId = $("#apply_USER_ID").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var ibsysId = $("#IBSYSID").val();
	var subIbsysId = $("#SUB_IBSYSID").val();
	var reecordNum = $("#RECORD_NUM").val();
	
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	if("MASG" == brandCode && $('#cond_OFFER_CODE').val() !='' )
	{
		var esopStr = $("#ESOP_OFFER_DATA").text();
		if(esopStr != ""){  
			$("#"+offerChaHiddenId).val(esopStr); 
		}
	}
	
	if(showParamPart == "2")
	{
		var datalinOperType = $("#DATALINE_OPER_TYPE").val();
		if(datalinOperType == "1"){
			serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
		}
		var serialSEQ = $("#DATALINE_MAX_INDEX").val();
		var serialNO = $("#serialNO").val();
		if(operType == "21" || operType == "22"){
			userId = $("#"+offerChaHiddenId.substr(10)).attr("userId");
		}
	}
	if(brandCode == "BOSG")
	{
		var offerDataHiddenId = "OFFER_DATA_" + offerChaHiddenId.substring(10);
		var offerData = new Wade.DataMap($("#"+offerDataHiddenId).text());
		userId = offerData.get("USER_ID");//产品USER_ID
		subOfferCode = offerData.get("OFFER_CODE");
		offerId = offerData.get("OFFER_ID");
		
	}
	var param="";
	if(ibsysId)
	{
		param = "&IBSYSID="+ibsysId+"&RECORD_NUM="+reecordNum+"&SUB_IBSYSID="+subIbsysId+"&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex;
	}
	else
	{
		param = "&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex;
	}
	param += "&NOT_QRY=true";
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		if(templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
			chooseMode();
		}
		showPopup("popup02", "offerChaPopupItem", true);
		
		var grpItems = data.map.result;
		if ("BOSG" == brandCode && grpItems)
		{
			for (var i=0;i<grpItems.length;i++)
			{
				var prodChaSpecInfo = grpItems.get(i);
				var elementId = prodChaSpecInfo.get("CHA_SPEC_ID");
				var regx = /[0-9]+/m;
				var code = elementId.match(regx);
				if (code != null)
				{
					elementId = code[code.length-1];
				}
				var elementValue = prodChaSpecInfo.get("CHA_VALUE");
				var grpItemId = "AREA_ID_" + prodChaSpecInfo.get("GROUP_ATTR");
				if (grpItemId)
				{
					dealGrpItemInit(grpItemId,elementId,elementValue);
				}
			}
		}
		
		var offerChaSpecStr = $("#"+offerChaHiddenId).val();
		if(offerChaSpecStr != "")
		{
			var offerChaSpecs = new Wade.DatasetList(offerChaSpecStr);
			for(var i = 0; i < offerChaSpecs.length; i++)
			{
				var offerChaSpec = offerChaSpecs.get(i);
				var attrCode = offerChaSpec.get("ATTR_CODE");
				var attrValue = offerChaSpec.get("ATTR_VALUE","");
				var grpItemId = offerChaSpec.get("GROUP_ATTR");
				if (grpItemId)
				{
//					dealGrpItemInit(grpItemId,elementId,elementValue);
				}
				else
				{
					var inputType = $("#offerChaPopupItem input[name="+attrCode+"]").attr("type");
//					if("BOSG" == brandCode)
//					{
//						inputType= $("#offerChaPopupItem input[element_id="+attrCode+"]").attr("type");
//					}
					if(inputType == "checkbox" || inputType == "radio")
					{
						if(attrValue == "1")
						{
							$("#offerChaPopupItem input[name="+attrCode+"]").attr("checked", "true");
						}
						else
						{
							$("#offerChaPopupItem input[name="+attrCode+"]").removeAttr("checked");
						}
					}
					else
					{	
//						if("BOSG" == brandCode)
//						{
//							$("#offerChaPopupItem input[element_id="+attrCode+"]").val(attrValue);
//						}
//						else
//						{
						var isReadonly = $("#IS_READONLY").val();
						//$("#offerChaPopupItem input[id="+attrCode+"]").val(attrValue);
						/*if(isReadonly=='true'||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="EDIRECTLINECHANGE"){
							if(attrCode.indexOf("NOTIN_")==0){
								$("#offerChaPopupItem input[id="+attrCode+"]").val(attrValue);
							}else{
								$("#offerChaPopupItem input[id=pattr_"+attrCode+"]").val(attrValue);
							}
						}else{
							$("#offerChaPopupItem input[id="+attrCode+"]").val(attrValue);
						}*/
						if(attrCode.indexOf("NOTIN_")==0||attrCode.indexOf("pattr_")==0){
							$("#offerChaPopupItem input[id="+attrCode+"]").val(attrValue);
						}else{
							$("#offerChaPopupItem input[id=pattr_"+attrCode+"]").val(attrValue);
						}
							
			
																		   
	   
//						}
						
					}
					
					// 如果是simpleupload组件还需要初始化几个值
//					if (elementValue.length>0 && "simpleupload" == $("#offerChaPopupItem input[element_id=" + elementId + "]").attr("x-wade-uicomponent")) {
//						var uploadId = $("#offerChaPopupItem input[element_id=" + elementId + "]").attr("id");
//						$("#" + uploadId + "_name").val(elementValue);
//						$("#" + uploadId + "_btn_close").attr("style","display:");
//						$("#" + uploadId + "_btn_download").attr("style","display:");
//					}
				}
			}
			
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeChangeMode(){
	debugger;
	//默认选客户中心和设置提示
	defaultValueForBuildSetion();
	
	var bizRange = $("#pattr_BIZRANGE").val();
	var condTempletId = $("#cond_TEMPLET_ID").val();
	if(!bizRange && condTempletId!="ECHANGERESOURCECONFIRM" && condTempletId!="EDIRECTLINECANCEL"){
		MessageBox.alert("提示","请选择业务范围！");
		return false;
	}
	if(condTempletId=="EDIRECTLINECANCEL"){
		var serviceType = $("#pattr_SERVICETYPE").val();
		if(serviceType=='0'){
			bizRange = "本地市";
			$("#pattr_DIRECTLINE_SCOPE").val(1);//发报文的隐藏字段
		}else if(serviceType=='1'){
			bizRange = "省内跨地市";
			$("#pattr_DIRECTLINE_SCOPE").val(2);//发报文的隐藏字段
		}
	}
	if(condTempletId=="EDIRECTLINECHANGE"){
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode=="扩容"){
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "no");
		}else{
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "none");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "");
		}
		return;
	}
	if(condTempletId == "EVIOPDIRECTLINECHANGESIMPLE" || condTempletId=="DIRECTLINECHANGESIMPLE"){
		$("#changImport").css("display", "");
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode=="减容" || changeMode=="同楼搬迁"){
			$("#CHANGEMODEIMPORT").val(changeMode);
		}
	}
	if(condTempletId=="DIRECTLINECHANGESIMPLE"||condTempletId=="MANUALSTOP"||condTempletId=="MANUALBACK"){
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode=="停机"){
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "no");
		}else{
			$("#CHANGHE_ACCEPTTANCE_PERIOD").css("display", "none");
			$("#pattr_ACCEPTTANCE_PERIOD").attr("nullable", "");
		}
		if(!$("#pattr_CHANGEMODE").val()){
			MessageBox.alert("提示","请选择业务调整场景！");
			return false;
		}
	}
	//清除已修改专线
	$("#offerchalist").val("");
	$("#DATALINE_MAX_INDEX").val("0");
	$("#DATALINE_PARAM_UL").html("");
	
	var offerCode = $("#cond_OFFER_CODE").val();
	var userId = $("#GRP_USER_ID").val();
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initDirectLineList", "&OFFER_CODE="+offerCode+"&EC_USER_ID="+userId+"&BIZRANGE="+bizRange+"&TEMPLET_ID="+condTempletId, "", function(datas){
		$("#CHANGEDATALINENOS").val("");
		$("#CHANGEDATALINENOS").val(datas);
		//var offerCode = $("#cond_OFFER_CODE").val();
		var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
		
//		var offerchaList = new Wade.DatasetList($("#offerchalist").val());
		var temp = new Wade.DatasetList();
//		for (var j = 0; j < offerchaList.length; j++) {
//			var offercha = offerchaList[j];
//			temp.add(offercha);
//		}
		
		for(var i=0;i<datas.length;i++){
			var dataLineInfos = new Wade.DatasetList();
			var data = datas.get(i);
			loadChangeDataLineParamList(data);
			var key = data.keys;
			for(var m=0;m<key.length;m++){
				var value = data.get(key[m]);
				var dataLineInfo = new Wade.DataMap();
				dataLineInfo.put("ATTR_CODE", key[m]);
				dataLineInfo.put("ATTR_VALUE", value);
				var name = "";
				if(key[m].indexOf("NOTIN_")!=0){
					name = "pattr_"+key[m];
				}else{
					name = key[m];
				}
				dataLineInfo.put("ATTR_NAME",$("input[name="+name+"]").attr("desc"));
				dataLineInfos.add(dataLineInfo);
			}
			
			if(changeMode=="减容"){
				var dataBandWidthInfo = new Wade.DataMap();
				dataBandWidthInfo.put("ATTR_CODE", "HIDDEN_BANDWIDTH");
				dataBandWidthInfo.put("ATTR_VALUE", data.get("BANDWIDTH"));
				dataBandWidthInfo.put("ATTR_NAME","原来带宽");
				dataLineInfos.add(dataBandWidthInfo);
			}
			if(condTempletId!="ECHANGERESOURCECONFIRM"){
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
				$("#"+offerChaHiddenId).val(dataLineInfos);
			}
			maxIndex++;
			$("#DATALINE_MAX_INDEX").val(maxIndex);
			temp.add(dataLineInfos);
			
		}
		$("#offerchalist").val(temp);
		
		$.endPageLoading();
		
		//加载导出条件域
		setExportLineNos();
		
		$("#ChangeSimpleImport").afterAction(function(e, status){
			  debugger;
			  if('ok' == status){
			   $.beginPageLoading("正在处理...");
			   ajaxSubmit('','ChangeSimpleImport',null,null,function(datas){
				 //将主页面上已存在的数据删除
				   $("#DATALINE_PARAM_UL").children().remove();
				 var offerCode = $("#cond_OFFER_CODE").val();
					var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
					
					var offerchaList = new Wade.DatasetList($("#offerchalist").val());
					var temp = new Wade.DatasetList();
					for (var j = 0; j < offerchaList.length; j++) {
						var offercha = offerchaList[j];
						temp.add(offercha);
					}
					
					for(var i=0;i<datas.length;i++){
						var dataLineInfos = new Wade.DatasetList();
						var data = datas.get(i);
						loadChangeDataLineParamList(data);
						var key = data.keys;
						for(var m=0;m<key.length;m++){
							var value = data.get(key[m]);
							var dataLineInfo = new Wade.DataMap();
							var dataLinediscnt = new Wade.DataMap();
							dataLinediscnt.put("ATTR_CODE", key[m]);
							dataLinediscnt.put("ATTR_VALUE", value);
							var name = "";
							if(key[m].indexOf("NOTIN_")!=0){
								name = "pattr_"+key[m];
							}else{
								name = key[m];
							}
							dataLinediscnt.put("ATTR_NAME",$("input[name="+name+"]").attr("desc"));
							dataLineInfos.add(dataLinediscnt);
						
						}
						
						if(changeMode=="减容"){
							var dataBandWidthInfo = new Wade.DataMap();
							dataBandWidthInfo.put("ATTR_CODE", "HIDDEN_BANDWIDTH");
							dataBandWidthInfo.put("ATTR_VALUE", data.get("BANDWIDTH"));
							dataBandWidthInfo.put("ATTR_NAME","原来带宽");
							dataLineInfos.add(dataBandWidthInfo);
						}
						
						var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
						$("#"+offerChaHiddenId).val(dataLineInfos);
						maxIndex++;
						$("#DATALINE_MAX_INDEX").val(maxIndex);
						temp.add(dataLineInfos);
						
					}
					$("#offerchalist").val(temp);
			    
			    $.endPageLoading();
			    MessageBox.success("导入成功！", "页面展示导入数据!",function(){
			     flag="true";
			    });
			   },function(error_code,error_info,derror){
			    $.endPageLoading();
			    showDetailErrorInfo(error_code,error_info,derror);
			   });
			  }  
		});
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function submitOfferCha()
{	
	var operType = $("#cond_OPER_TYPE").val();
	if(operType == "22")
	{
		return true;
	}
	
	if(!$.validate.verifyAll("offerChaPopupItem")){
		return false;
	}
	
	//校验专线名称.只开通单校验
	var tradeName = $("#pattr_TRADENAME").val();
	var condTempletId = $("#cond_TEMPLET_ID").val();
	var ifConfCrm = $("#pattr_IF_CHOOSE_CONFCRM").val();
	
	var changeMode = $("#pattr_CHANGEMODE").val();
	
	/*if(changeMode=="减容"){
		var newValue = $("#pattr_BANDWIDTH").val();
		//var oldValue = $("#pattr_BANDWIDTH").attr("old_value");
		
		var productNo = $("#DATALINE_PARAM_UL input:checked").attr("productno");
		
		$.beginPageLoading("正在校验...");
		$.ajax.submit("", "checkLineBandWidth", "&BANDWIDTH="+newValue+"&PRODUCTNO="+productNo, "", function(data){
			$.endPageLoading();
			if(data.get("FLAG")!="true"){
				$.validate.alerter.one($("#pattr_BANDWIDTH")[0], "减容场景带宽必须小于原来带宽！");
				$("#pattr_BANDWIDTH").val(data.get("BAND_WIDTH"));
				return false;
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
	}*/
	
	if("EDIRECTLINEOPEN"==condTempletId && ifConfCrm=="0" || "ERESOURCECONFIRMZHZG"==condTempletId && tradeName!=""){
		if(tradeName.length > 100){
			$.validate.alerter.one($("#pattr_TRADENAME")[0], "专线名称不能超过100个字符！");
			$("#pattr_TRADENAME").val("");
			return false;
		}
		//将此专线设置为已修改
		$("#IS_MODIFY_TAG").val(1);//1为已修改
		
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.esop.ckeckDatalineName.CkeckDatalineName", "ckeckDatalineName","&TRADENAME="+tradeName, function(data){
			$.endPageLoading();
			dataLineName(data);
		},    
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
		
	}
	else{
		var chaSpecObjs = $("#offerChaPopupItem input");
		var brand = $("#cond_BRAND_CODE").val();
		var offerChaSpecDataset = new Wade.DatasetList();
		//将此专线设置为已修改
		$("#IS_MODIFY_TAG").val(1);//1为已修改
		
		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			var chaValue = "";
			if("BOSG"==brand&&isBbossNull(chaSpecCode))
			{
				
				if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
				{
					continue;
				}
			}
			else if (!chaSpecCode)
			{
				if(chaSpecObjs[i].type != "checkbox" && chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
				{
					continue;
				}
			}
			if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
			{
				chaValue = chaSpecObjs[i].checked ? 1 : 0;
				chaSpecCode = chaSpecObjs[i].name;
			}
			else
			{
				chaValue = $("#"+chaSpecCode).val();
			}
			
			var offerChaSpecData = new Wade.DataMap();
			offerChaSpecData.put("ATTR_VALUE", chaValue);
			offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
			offerChaSpecData.put("ATTR_CODE", chaSpecCode);
			
//			if("BOSG"==brand)
//			{
//				var elementId = chaSpecObjs[i].getAttribute("element_id");
//				if(!elementId)
//				{
//					continue;
//				}
//				offerChaSpecData.put("ATTR_CODE", elementId);
//			}
			offerChaSpecDataset.add(offerChaSpecData);
		}
		var offerChaHiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
		$("#"+offerChaHiddenId).val(offerChaSpecDataset);
		
		$("#"+offerChaHiddenId).closest("li").find(".side").html("<span class='e_tag e_tag-green'>已设置</span>");
	    //设置默认勾选
		
		hidePopup(document.getElementById("offerChaPopupItem"));
		submitOfferChaAfterAction(offerChaSpecDataset);
		
		var offerchalist = new Wade.DatasetList($("#offerchalist").val());
		if("ETAPMARKETINGENTERING"==condTempletId||"ETAPMARKETINGENTERINGUPD"==condTempletId){
			return true;
		}
		offerchalist.add(offerChaSpecDataset);
		$("#offerchalist").val(offerchalist);
//		DataLineList = offerchalist;
		return true;
	}
}

function initPageParamCommon()
{
	var brandCode = $("#cond_BRAND_CODE").val();
	if("BOSG"==brandCode)
	{
		return;
	}
	var curOfferId = $("#prodDiv_OFFER_ID").val();
	var checkfunctionNameOfferId = "initPageParam_" + curOfferId;
	var checkfunctionName = "initPageParam";
	if($.isFunction(window[checkfunctionNameOfferId]))
	{
		window[checkfunctionNameOfferId]();
	}
	else if($.isFunction(window[checkfunctionName])){
		window[checkfunctionName]();
	}
}

function submitOfferChaAfterAction(offerChaSpecs)
{
	var submitFuncNameOfferId = "afterSubmitOfferCha_" + $("#cond_OFFER_ID").val();
	var submitFuncName = "afterSubmitOfferCha";
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	if(bpmTempletId=="ETAPMARKETINGENTERINGUPD" || bpmTempletId=="ETAPMARKETINGENTERING" || bpmTempletId=="ETAPMARKETINGEXCITATION" ){
		submitFuncName="afterSubmitOfferChaForMarkting";
	}
	if($.isFunction(window[submitFuncNameOfferId]))
	{
		window[submitFuncNameOfferId](offerChaSpecs);
	}
	else if($.isFunction(window[submitFuncName])){
		window[submitFuncName](offerChaSpecs);
	}
}

/**
 * bboss把前台没显示属性过滤
 * @param arg
 * @returns {Boolean}
 */
function isBbossNull(arg)
{
    if(!arg && arg!==0 && typeof arg!=="boolean")
    {
        return true ;
    }
    //不显示的属性不提交到后台
    if($("#"+arg).attr("style")=="display:none;")
    {
        return true ;
    }

    //去掉select为none的,兼容ie8的情况
    if($("#"+arg+"_span").children.length>0&&($("#"+arg+"_span").attr("style")=="display:none;" || $("#"+arg+"_span").attr("style")=="DISPLAY: none")){
        return true ;
    }
    return false;
}

function checkParamData() {
    var offerListNum = $("#orderDetailPart").length;
    if (offerListNum === 0) {
    	return false;
    }
    var eoms_customLevel = $("#eoms_customLevel").val();
    var eoms_customContact = $("#eoms_customContact").val();
    var eoms_customContactPhone = $("#eoms_customContactPhone").val();
    var eoms_accountManagerName = $("#eoms_accountManagerName").val();
    var eoms_accountManagerPhone = $("#eoms_accountManagerPhone").val();
    var eoms_accountManagerMail = $("#eoms_accountManagerMail").val();
    var eoms_provinceName = $("#eoms_provinceName").val();
    var eoms_cityName = $("#eoms_cityName").val();
    var eoms_countyName = $("#eoms_countyName").val();
    var eoms_bRequirementDesc = $("#eoms_bRequirementDesc").val();
    return !(eoms_customLevel && eoms_customContact && eoms_customContactPhone &&
        eoms_accountManagerName && eoms_accountManagerPhone && eoms_accountManagerMail && eoms_provinceName &&
        eoms_cityName && eoms_countyName && eoms_bRequirementDesc);

}

/**************************商品特征-结束*****************************/
//计算字符长度
function computevaluelen(value){
	var length = value.length;
	for(var i = 0; i < value.length; i++){
		if(value.charCodeAt(i) > 19967){
			length++;
		}
	}
	return length;
}

//提交
function submitApply()
{
	debugger;
	var datalinelinum = $("#DATALINE_PARAM_UL li[id*=DATALINE]").length;
	var ifNew =  $("#C_IF_NEW_CONTRACT").val();
    var pattrtitle = $("#pattr_TITLE").val();
    var contractRemark = $("#C_REMARK").val();
    var pattrtitlelen = computevaluelen(pattrtitle);
    var cRemarklen = 0;
    var datalineflag = 0; 
    
     if(datalinelinum != 0){
 			$("#DATALINE_PARAM_UL li[id*=DATALINE]").each(function(){
 				var liLength = $(this).children(".main").find("li").length;
 				console.log("liLength++::"+liLength);
 				if(liLength != 0){
 					for(var i=0;i < liLength;i++){
 					 var label = $(this).children(".main").find("li").eq(i).children(".label").text();
 					if(label!=null&&label!=""){	
 		 				if(label=="专线宽带(兆)："){
 		 					var dateLineValue = $(this).children(".main").find("li").eq(i).children(".value").text();
 		 					console.log("dateLineValue++::"+dateLineValue);
 		 					if(dateLineValue!=null&&dateLineValue!=""){
 		 						if(isNaN(dateLineValue)){
 		 							datalineflag = 1;
 		 							return false;
 		 						}
 		 					}
 		 				}else if(label=="A端用户名称："){
 		 					var dateLineValue = $(this).children(".main").find("li").eq(i).children(".value").text();
 		 					console.log("dateLineValue++::"+dateLineValue);
 		 					if(dateLineValue!=null&&dateLineValue!=""){
 		 						if(computevaluelen(dateLineValue) > 50){
 		 							datalineflag = 2;
 		 							return false;
 		 						}
 		 					}
 		 				}else if(label=="Z端用户名称："){
 		 					var dateLineValue = $(this).children(".main").find("li").eq(i).children(".value").text();
 		 					console.log("dateLineValue++::"+dateLineValue);
 		 					if(dateLineValue!=null&&dateLineValue!=""){
 		 						if(computevaluelen(dateLineValue) > 50){
 		 							datalineflag = 3;
 		 							return false;
 		 						}
 		 					}
 		 				}else if(label=="A端街道/乡镇："){
 		 					var dateLineValue = $(this).children(".main").find("li").eq(i).children(".value").text();
 		 					console.log("dateLineValue++::"+dateLineValue);
 		 					if(dateLineValue!=null&&dateLineValue!=""){
 		 						if(computevaluelen(dateLineValue) > 100){
 		 							datalineflag = 4;
 		 							return false;
 		 						}
 		 					}
 		 				}else if(label=="Z端街道/乡镇："){
 		 					var dateLineValue = $(this).children(".main").find("li").eq(i).children(".value").text();
 		 					console.log("dateLineValue++::"+dateLineValue);
 		 					if(dateLineValue!=null&&dateLineValue!=""){
 		 						if(computevaluelen(dateLineValue) > 100){
 		 							datalineflag = 5;
 		 							return false;
 		 						}
 		 					}
 		 				}
 		 			}
 					 
 					}
 				}
 
 		});
     }
     
     if(datalineflag == 1){
    	 MessageBox.alert("提示", "专线宽带(单位：M)必须是数字！");
		 return false;
     }else if(datalineflag == 2){
    	 MessageBox.alert("提示", "[A端用户名称]不能超过50个字符长度,一个汉字算2个字符长度!");
		 return false;
     }else if(datalineflag == 3){
    	 MessageBox.alert("提示", "[Z端用户名称]不能超过50个字符长度,一个汉字算2个字符长度!");
		 return false;
     }else if(datalineflag == 4){
    	 MessageBox.alert("提示", "[A端街道/乡镇]不能超过100个字符长度,一个汉字算2个字符长度!");
		 return false;
     }else if(datalineflag == 5){
    	 MessageBox.alert("提示", "[Z端街道/乡镇]不能超过100个字符长度,一个汉字算2个字符长度!");
		 return false;
     }
     
     if(null != contractRemark&& ""!=contractRemark){
    	 cRemarklen = computevaluelen(contractRemark);
     }
     //判断主题字符长度
	if( pattrtitlelen > 70){
		MessageBox.alert("提示", "主题不能超过70个字符长度,一个汉字算2个字符长度");
		return false;
	}
	 //合同说明字符长度
	if( cRemarklen > 100){
		MessageBox.alert("提示", "合同说明不能超过100个字符长度,一个汉字算2个字符长度");
		return false;
	}
	if(ifNew=='0'){//新增
		if(!endDate()){
			return false;
		}
	}
	if(!$.validate.verifyAll("ParamPart")){
		return false;
	}
	var nodeAudit = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var templetIDStaff = nodeAudit.get("BPM_TEMPLET_ID");
	//var templetIDStaff = $("#cond_TEMPLET_ID").val();
	if("EDIRECTLINEOPENPBOSS" ==templetIDStaff ||  "DIRECTLINECHANGESIMPLE" ==templetIDStaff ||  "EDIRECTLINECHANGEPBOSS" ==templetIDStaff){
		var auditStaff = $("#pattr_AUDITSTAFF").val();
		if(auditStaff == null || auditStaff == "")
		{
			$.validate.alerter.one($("pattr_AUDITSTAFF")[0], "请选择稽核人员！");
			return false;
		}
	}
	if(!$.validate.verifyAll("groupBasePart")){
		return false;
	}
	if(templetIDStaff=='EDIRECTLINECONTRACTCHANGE'){
		var contractfilelist = $("#CONTRACT_FILE_LIST").val();
		if(""==contractfilelist||undefined==contractfilelist||null==contractfilelist){
			$.validate.alerter.one($("#CONTRACT_FILE_LIST")[0], "合同附件未上传，请上传附件！");
	        return false;
		}
	}
//    if (checkParamData()) {
//        MessageBox.alert("提示", "请确认工单详细信息数据是否完整！");
//        return false;
//    }
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == "")
	{
		$.validate.alerter.one(document.getElementById("cond_GROUP_ID"), "请输入集团编码，然后回车，查询集团信息！");
		return false;
	}
	var offerCode = $("#cond_OFFER_CODE").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one($("cond_OFFER_NAME")[0], "请选择产品！");
		return false;
	}
	
	var templetID400 = $("#cond_TEMPLET_ID").val();
	if(""==templetID400||undefined==templetID400||null==templetID400){
		$.validate.alerter.one($("#cond_TEMPLET_ID")[0], "请选择需要办理的业务流程！");
        return false;
	}
	if("FOURMANAGE"==templetID400||"VOICEMANAGE"==templetID400||"TIMERREVIEWFOURMANAGE"==templetID400||"TIMERREVIEWVOICEMANAGE"==templetID400||"MOBILE400OPEN"==templetID400){
		var offerListNum = $("#DATALINE_PARAM_UL").length;
		if (offerListNum != 0) {
			if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
				MessageBox.alert("错误", "您未勾选'数据列表'里的任何一条，请选择后提交！");
				return false;
			}
		}
	}else{
		
		//校验专线是否选择了子商品
		debugger;
		var offerListNum = $("#DATALINE_PARAM_UL").length;
		if (offerListNum != 0) {
			if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
				MessageBox.alert("错误", "您未勾选'数据列表'里的任何一条专线，请选择后提交！");
				return false;
			}
		}
	}
	
	var templetID = $("#cond_TEMPLET_ID").val();
	if("DIRECTLINEMARKETINGADD"==templetID){
		var fileName= $("#ATTACH_FILE_NAME").val();
		if(""==fileName||undefined==fileName||null==fileName){
			MessageBox.alert("错误",  "专线试用营销活动需要上传附件，请上传附件！");
	        return false;
		}
	}
	

	var operType = $("#cond_OPER_TYPE").val();//20:新增,23:资源变更,21:资费变更,225:删除,
	var operCode = transOperCode(operType);//0:新增,1:删除,2:变更
	var custData = new Wade.DataMap();
	custData.put("GROUP_ID", groupId);
	custData.put("CUST_NAME", $("#cond_CUST_NAME").val());
	
	var offerData = new Wade.DataMap();
	offerData.put("OFFER_CODE", offerCode);
	offerData.put("OFFER_TYPE", "P");
	offerData.put("OFFER_ID", $("#cond_OFFER_ID").val());
	offerData.put("OFFER_NAME", $("#cond_OFFER_NAME").val());
	offerData.put("OPER_CODE", operCode);
	if(operType != "20")//非开通
	{
		offerData.put("USER_ID",$("#apply_USER_ID").val());
	}
	var commData = new Wade.DataMap();
	if($("#EOS_COMMON_DATA").text()!=''){
		commData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	}
	var otherList = new Wade.DatasetList();
	//var spcOtherList = new Wade.DatasetList();
	var directlineTypeData = new Wade.DatasetList();
	var delParamData = new Wade.DatasetList();
	var eomsAttrData = new Wade.DatasetList();
	var eomsAttrList;
	var contractList;
	var idcList;
	var tapMarketingList;
	var tapMarketingLineList;
	var orderData = new Wade.DataMap();
	var confCrmData = new Wade.DataMap();
	
	var showParamPart = $("#SHOW_PARAM_PART").val();
	if(showParamPart == "1")
	{//标准产品参数
		var standardOfferCha = saveStandardOfferCha();
		offerData.put("OFFER_CHA", standardOfferCha);
	}
	else if(showParamPart == "2")
	{//普通专线产品
//		if(!verifyDataLine()){
//			return false;
//		}
		
		var datalineOfferCha = saveDataLineOfferCha(operCode,operType,offerCode);
		var checkFlag = datalineOfferCha.get("CHECK_FLAG");
//		if(checkFlag == false)
//		{
//			MessageBox.alert("提示信息", "业务查勘序号【"+datalineOfferCha.get("ERROR_TRADE_ID")+"】的A、Z端接入地址不能相同，请核对A、Z端安装所属地市、A、Z端安装地址所属区县、A、Z端所在街道/乡镇/路/巷/弄/行政村以及A端安装地址所在门牌/小区/单位/自然村/组属性不能全部相同！");
//			return false;
//		}
		offerData.put("SUBOFFERS", datalineOfferCha.get("SUBOFFER"));
		commData.put("DATALINE_OPER_TYPE", $("#DATALINE_OPER_TYPE").val());
		commData.put("DATALINE_SERIAL_NUMBER", $("#DATALINE_SERIAL_NUMBER").val());
		custData.put("APPLY_ID",$("#APPLY_ID").val())
		
		orderData.put("TITLE",$("#pattr_TITLE").val());
		orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
		
		confCrmData.put("CONF_IBSYSID",$("#CONFIBSYSID").val());
//		if($.validate.verifyAll("orderDetailPart")){
			eomsAttrData = builderEomsAttrData();
//		}else{
//			return false;
//		}
		//eomsAttrData = saveAcctInfo(eomsAttrData);//账户信息
		eomsAttrData = savePublicInfo(eomsAttrData);//公共信息
		eomsAttrData = saveBusinessInfo(eomsAttrData);//业务信息
		eomsAttrData = saveConfCrmInfo(eomsAttrData);//勘察信息
		eomsAttrData = saveOrderInfo(eomsAttrData);//订单级信息
		contractList = saveContractInfo();//合同信息
		eomsAttrData = saveGroupData(eomsAttrData);//客户信息
		eomsAttrData = saveMarkeDate(eomsAttrData);//营销活动信息
		var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
		var bpmTempletId = node.get("BPM_TEMPLET_ID");
		if(bpmTempletId == "EDIRECTLINEOPENPBOSS" || bpmTempletId == "EVIOPDIRECTLINEOPENPBOSS"){
			var datalinOperType = $("#DATALINE_OPER_TYPE").val();
			if(datalinOperType != "1"){
				var acctName = "";
				var acctDeal = $("#cond_ACCT_DEAL").val();
				if(acctDeal == 1)
				{//账户合户
					acctName = $("#ACCT_NAME_span").text()
				}else
				{
					var acctInfo = pageData.getAccountInfo();
					acctName = acctInfo.get("ACCT_NAME");
				}
				if(acctName == null || acctName == ""){
					MessageBox.alert("错误","您未新增账户或选择已有账户，请重新操作后再提交！");
					return false;
				}
				eomsAttrData = saveAcctInfo(eomsAttrData);//账户信息
			}
			if(bpmTempletId == "EDIRECTLINEOPENPBOSS" ){
				otherList = saveOtherInfo(otherList);
			}
		}

		tapMarketingList = saveTapMarketing(bpmTempletId);
		tapMarketingLineList=saveTapMarketingLine(operCode,operType,offerCode,bpmTempletId);
		if(tapMarketingLineList!=null&&tapMarketingLineList.length>0){
			offerData.put("SUBOFFERS", tapMarketingLineList);

		}
		DataLineList = new Wade.DatasetList($("#offerchalist").val());
		directlineTypeData = DataLineList;
		
//		if(operType != "23"){
			var crmData = new Wade.DataMap();
			crmData.put("ATTR_VALUE", $("#CROMNO").val());
			crmData.put("ATTR_NAME", "BSS工单号");
			crmData.put("ATTR_CODE", "CRMNO");
			eomsAttrData.add(crmData);
			
			var ifChooseConfcrm = $("#pattr_IF_CHOOSE_CONFCRM").val(); 
			var crmData2 = new Wade.DataMap();
			crmData2.put("ATTR_NAME", "BSS资源勘查工单号");
			crmData2.put("ATTR_CODE", "CONFCRMTICKETNO");
			if(ifChooseConfcrm=='0'){//否
				crmData2.put("ATTR_VALUE", "");
			}else if(ifChooseConfcrm=='1'){//是
				crmData2.put("ATTR_VALUE", $("#CONFCRMTICKETNO").val());
			}

			eomsAttrData.add(crmData2);
//		}
			
		if(operType != "23"){
			var crmData = new Wade.DataMap();
			crmData.put("ATTR_VALUE", $("#CONFIBSYSID").val());
			crmData.put("ATTR_NAME", "关联的勘察单号");
			crmData.put("ATTR_CODE", "CONFIBSYSID");
			eomsAttrData.add(crmData);
		}
		
		if(operType == "20"){
			var datalinOperType = $("#DATALINE_OPER_TYPE").val();
			if(datalinOperType == "1"){
				serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
				offerData.put("SERIAL_NUMBER",serialNumber);
				userId = $("#DATALINE_USER_ID").val();
				offerData.put("USER_ID",userId);
			}
		}
		
		if(operType != "20"){
			var userId = $("#GRP_USER_ID").val();
			var serialNumber = $("#GRP_SERIAL_NUMBER").val();
			offerData.put("USER_ID",userId);
			offerData.put("SERIAL_NUMBER",serialNumber);
		}
		
		if(operType == "25"){
			if($.validate.verifyAll("delParamPart")){
				delParamData = saveDelParamData();
			}else{
				return false;
			}
		}
		if(operType == "21" && (bpmTempletId == "EDIRECTLINECHANGEFEE"||bpmTempletId == "EDIRECTLINEDATACHANGE")){
			var stopdatalineflag = 0;
			var feeserialnumber = "";
			if ($("#DATALINE_PARAM input[type=checkbox]:checked").length == 0 ||$("#DATALINE_PARAM input[type=checkbox]:checked").length == "0") {
				MessageBox.alert("错误", "您未勾选需要变更的专线！");
				return false;
			}else{
				$("#DATALINE_PARAM input[type=checkbox]:checked").each(function(){
					var productno = $(this).attr("productno");
					if(productno == "0" || productno == null){
						feeserialnumber = $(this).attr("serialnumber");
						stopdatalineflag = 1;
						 return false;
					}
				});
				}
			if(stopdatalineflag == 1){
				MessageBox.alert("提示", "成员号码为【"+feeserialnumber+"】的专线已停机，办理专线复机之后才能进行资费变更！");
				return false;
			}
			datalineOfferCha = saveChangeDataLineOfferCha(operCode);
			offerData.put("SUBOFFERS", datalineOfferCha.get("SUBOFFER"));
			
			directlineTypeData = builderchangeEomsAttrData(directlineTypeData);
		}
		
		if(bpmTempletId == "EDIRECTLINEOPENPBOSS" || bpmTempletId == "EDIRECTLINECHANGEPBOSS" || bpmTempletId == "ERESOURCECONFIRMZHZG" || bpmTempletId == "EVIOPDIRECTLINECHANGEPBOSS" ||bpmTempletId == "EVIOPDIRECTLINEOPENPBOSS"){
			directlineTypeData = builderDirectLineData(directlineTypeData); 
			if(directlineTypeData==false){
				return false;
			}
			
		}
		
		if("DIRECTLINEMARKETINGADD"==templetID||"DIRECTLINEMARKETINGUPD"==templetID){
			directlineTypeData = builderDirectLineData(directlineTypeData); 
			if(directlineTypeData==false){
				return false;
			}
		}
		
		if(bpmTempletId == "DIRECTLINECHANGESIMPLE"||bpmTempletId == "EVIOPDIRECTLINECHANGESIMPLE"||bpmTempletId == "ECHANGERESOURCECONFIRM"||bpmTempletId == "MANUALSTOP"||bpmTempletId == "MANUALBACK"){
			$.beginPageLoading("数据校验中，请稍后...");
			var result = checkChangeDataLine();
			var flag = result.get("FLAG");
			if(!flag){
				$.endPageLoading();
				if(bpmTempletId=="ECHANGERESOURCECONFIRM"){
					MessageBox.alert("提示信息", "您进行的是变更勘察流程，但未进行任何专线信息的变更，请确认！");
					return false;
				}else{
					MessageBox.alert("专线["+result.get("PRODUCTNO")+"]信息未提交，请提交专线后再提交流程！");
					return false;
				}
			}
			$.endPageLoading();
			directlineTypeData = builderSimpleChangeDirectLineData();
		}
		
		if(bpmTempletId == "EDIRECTLINECANCELPBOSS"||bpmTempletId == "EVIOPDIRECTLINECANCELPBOSS"){
			var datalineNum = $("#pattr_CHANGE_DATALINE_NUM").val();
			if(datalineNum == '1' ||datalineNum == '0'){
				directlineTypeData = builderDirectLineData(directlineTypeData); 
				if(directlineTypeData==false){
					return false;
				}
			}else{
				directlineTypeData = builderSimpleChangeDirectLineData();
			}
		}
		
		//校验专线名称
		$.beginPageLoading("专线名称校验中...");
		var flags = checkInDataName(directlineTypeData,bpmTempletId);
		$.endPageLoading();
		if(""!=flags){
			MessageBox.alert("提示",flags);
			return false;
		}
		
		
		var isCheckTag = $("#isCheckTag").val();
		if(""!=isCheckTag&&isCheckTag!=null){
			var otherData = new Wade.DataMap();
			otherData.put("ATTR_CODE","isCheckTag");
			otherData.put("ATTR_NAME","是否免勘查");
			otherData.put("ATTR_VALUE",isCheckTag);
			otherData.put("RECODR_NUM","-1");
			otherList.add(otherData);
		}
		
		//扩容与停机计费方式
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode =="扩容"||changeMode =="停机"){
			var accPeData = new Wade.DataMap();
			accPeData.put("ATTR_CODE","ACCEPTTANCE_PERIOD");
			accPeData.put("ATTR_VALUE",$("#pattr_ACCEPTTANCE_PERIOD").val());
			accPeData.put("ATTR_NAME",$("#pattr_ACCEPTTANCE_PERIOD").attr("desc"));
			accPeData.put("RECORD_NUM","0");
			otherList.add(accPeData);
		}
		
		//获取自动复机时间
		if(bpmTempletId == "MANUALSTOP"){
			var isAutoBack = "";
			var backTime = $("#pattr_BACK_TIME").val();
			if(backTime){
				isAutoBack="1";
			}else{
				MessageBox.alert("提示","请选择复机时间！");
				return false;
			}
			var otherData = new Wade.DataMap();
			otherData.put("ATTR_CODE","IS_AUTOBACK");
			otherData.put("ATTR_NAME","是否自动复机");
			otherData.put("ATTR_VALUE",isAutoBack);
			otherData.put("RECORD_NUM","0");
			otherList.add(otherData);
			var timeData = new Wade.DataMap();
			timeData.put("ATTR_CODE","BACK_TIME");
			timeData.put("ATTR_NAME","自动复机时间");
			timeData.put("ATTR_VALUE",backTime);
			timeData.put("RECORD_NUM","0");
			otherList.add(timeData);
			
			var isControlData = new Wade.DataMap();
			isControlData.put("ATTR_CODE","ISCONTROL");
			isControlData.put("ATTR_NAME","是否自动信控");
			isControlData.put("ATTR_VALUE","否");
			eomsAttrData.add(isControlData);
			
		}
		if(!$.validate.verifyAll("eomsPart"))
		{
			return false;
		}
		eomsAttrList = builderEomsAttrData();
		
	}
	else if(showParamPart == "3")
	{//Bboss产品
		orderData.put("TITLE",$("#pattr_TITLE").val());
		orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
		
		if(operType == "20"){
			BbossAttrList = new Wade.DatasetList($("#offerchalist").val());
		}else if(operType == "21"){
			var offerChaSpecDataset400Change = new Wade.DatasetList();
			
			var offerChaChangeType = new Wade.DataMap();
			var changeType = $("#pattr_CHANGE_TYPE").val();
			offerChaChangeType.put("ATTR_VALUE", changeType);
			offerChaChangeType.put("ATTR_NAME", "变更类型");
			offerChaChangeType.put("ATTR_CODE", "pattr_CHANGE_TYPE");
			offerChaSpecDataset400Change.add(offerChaChangeType);
			
			var offerChapointAudit = new Wade.DataMap();
			var pointAudit = $("#pattr_ROUTE_POINTCUSTCENTERAUDIT").val();
			offerChapointAudit.put("ATTR_VALUE", pointAudit);
			offerChapointAudit.put("ATTR_NAME", "选择下一步");
			offerChapointAudit.put("ATTR_CODE", "pattr_ROUTE_POINTCUSTCENTERAUDIT");
			offerChaSpecDataset400Change.add(offerChapointAudit);
			
			var offerChadecription = new Wade.DataMap();
			var decription = $("#pattr_REQURIE_DECRIPTION").val();
			offerChadecription.put("ATTR_VALUE", decription);
			offerChadecription.put("ATTR_NAME", "业务需求描述");
			offerChadecription.put("ATTR_CODE", "pattr_REQURIE_DECRIPTION");
			offerChaSpecDataset400Change.add(offerChadecription);
			
			var offerchalist = new Wade.DatasetList($("#offerchalist").val());
			offerchalist.add(offerChaSpecDataset400Change);
			$("#offerchalist").val(offerchalist);
			
			BbossAttrList = new Wade.DatasetList($("#offerchalist").val());
			
		}else if(operType == "23"){
			
			var offerChaSpecDataset400Cancel = new Wade.DatasetList();
			
			var offerChaFeeOver = new Wade.DataMap();
			var feeOver = $("#pattr_ID_FEE_OVER").val();
			offerChaFeeOver.put("ATTR_VALUE", feeOver);
			offerChaFeeOver.put("ATTR_NAME", "是否欠费");
			offerChaFeeOver.put("ATTR_CODE", "pattr_ID_FEE_OVER");
			offerChaSpecDataset400Cancel.add(offerChaFeeOver);
			
			var offerChapointAudit = new Wade.DataMap();
			var pointAudit = $("#pattr_ROUTE_POINTCUSTCENTERAUDIT_23").val();
			offerChapointAudit.put("ATTR_VALUE", pointAudit);
			offerChapointAudit.put("ATTR_NAME", "选择下一步");
			offerChapointAudit.put("ATTR_CODE", "pattr_ROUTE_POINTCUSTCENTERAUDIT");
			offerChaSpecDataset400Cancel.add(offerChapointAudit);
			
			var offerChadecription = new Wade.DataMap();
			var decription = $("#pattr_REQURIE_DECRIPTION_23").val();
			offerChadecription.put("ATTR_VALUE", decription);
			offerChadecription.put("ATTR_NAME", "业务需求描述");
			offerChadecription.put("ATTR_CODE", "pattr_REQURIE_DECRIPTION");
			offerChaSpecDataset400Cancel.add(offerChadecription);
			
			var offerchalist = new Wade.DatasetList($("#offerchalist").val());
			offerchalist.add(offerChaSpecDataset400Cancel);
			$("#offerchalist").val(offerchalist);
			
			BbossAttrList = new Wade.DatasetList($("#offerchalist").val());
			
		}
		eomsAttrData = builderEomsAttrData();
		eomsAttrData = saveOrderInfo(eomsAttrData);//订单级信息
		eomsAttrData = saveCustInfo(eomsAttrData);//客户信息
//		if("1"==bbossOfferCha)
//		{
//			MessageBox.alert("提示信息", "请完成产品参数设置再提交！");
//			return false;
//		}
//		offerData.put("SUBOFFERS", bbossOfferCha);
	}else if(showParamPart == "4"){//流量自由充
		
		//订单级信息
		orderData.put("TITLE",$("#pattr_TITLE").val());
		orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
		
		eomsAttrData = builderEomsAttrData();
		eomsAttrData = saveOrderInfo(eomsAttrData);//订单级信息
		eomsAttrData = saveCustInfo(eomsAttrData);//客户信息
		
	}else if(showParamPart == "5"){
		orderData.put("TITLE",$("#pattr_TITLE").val());
		orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
		eomsAttrData = saveOrderInfo(eomsAttrData);//订单级信息
		var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
		var bpmTempletId = node.get("BPM_TEMPLET_ID");
		var productId = $("#cond_OFFER_CODE").val();
		if(bpmTempletId == "EDIRECTLINEOPENIDC"){
			var datalinOperType = $("#DATALINE_OPER_TYPE").val();
			if(datalinOperType != "1"){
				var acctName = "";
				var acctDeal = $("#cond_ACCT_DEAL").val();
				if(acctDeal == 1)
				{//账户合户
					acctName = $("#ACCT_NAME_span").text()
				}else
				{
					var acctInfo = pageData.getAccountInfo();
					acctName = acctInfo.get("ACCT_NAME");
				}
				if(acctName == null || acctName == ""){
					MessageBox.alert("错误","您未新增账户或选择已有账户，请重新操作后再提交！");
					return false;
				}
				eomsAttrData = saveAcctInfo(eomsAttrData);//账户信息
			}
			if(bpmTempletId == "EDIRECTLINEOPENPBOSS" ){
				otherList = saveOtherInfo(otherList);
			}
			idcList=saveDirectLineOpenIDC(productId,bpmTempletId);
		}else if (bpmTempletId == "EDIRECTLINECHANGEIDC"){
			idcList=saveDirectLineOpenIDC(productId,bpmTempletId);
			saveDirectLineOpenIDCList(idcList,productId,bpmTempletId);
		}else if(bpmTempletId == "EDIRECTLINECANCELIDC"||bpmTempletId == "EDIRECTLINECHECKIDC"
			||bpmTempletId == "EDIRECTLINEPREEMPTIONIDC"||bpmTempletId == "EDIRECTLINECHANGECHECKIDC"){
			idcList=saveDirectLineOpenIDC(productId,bpmTempletId);
		}

		
		
		var idcLineList = new Wade.DatasetList();
		var idcLineListOfferCha  = new Wade.DataMap();
		idcLineListOfferCha.put("OFFER_CODE",offerCode);
		idcLineListOfferCha.put("OFFER_CHA",idcList);
		idcLineListOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
		idcLineListOfferCha.put("OFFER_TYPE","P");
		idcLineListOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
		idcLineListOfferCha.put("OPER_CODE", operCode);
		idcLineList.add(idcLineListOfferCha);
		offerData.put("SUBOFFERS", idcLineList);


	}
	debugger;
	commData.put("OFFER_ID", $("#cond_OFFER_ID").val());
	commData.put("IBSYSID", $("#IBSYSID").val());
	commData.put("BUSIFORM_NODE_ID", $("#BUSIFORM_NODE_ID").val());
	var attachList = saveAttach();
	var accepttancePeriod = $("#pattr_ACCEPTTANCE_PERIOD").val();
	//添加计费方式审核附件
	if(accepttancePeriod =="2"){
		if(!$("#speauditfileDate").text()){
			$.validate.alerter.one($("#speauditfile")[0], "请上传‘下下账期’计费方式的OA审批附件！");
			return false;
		}else{
			var accepFile = new Wade.DataMap($("#speauditfileDate").text());
			attachList.add(accepFile);
		}
	}
	
	var submitParam = new Wade.DataMap();
	submitParam.put("CUST_DATA", custData);//上传
	submitParam.put("OFFER_DATA", offerData);//BBOSS
	submitParam.put("BUSI_SPEC_RELE", new Wade.DataMap($("#BUSI_SPEC_RELE").text()));//流程信息
	submitParam.put("NODE_TEMPLETE", new Wade.DataMap($("#NODE_TEMPLETE").text()));//流程节点信息
	submitParam.put("COMMON_DATA", commData);
	submitParam.put("OTHER_LIST",otherList);
	//不影响流程走向的other表信息（解决无指派问题，辉哥留坑）
	//submitParam.put("SPC_OTHER_LIST",spcOtherList);
	submitParam.put("ATTACH_LIST", attachList);
	submitParam.put("DISCNT_ATTACH_LIST", saveDiscntAttach());
	if("9983"==offerCode){
		submitParam.put("DIRECTLINE_DATA", BbossAttrList);
	}else{
		submitParam.put("DIRECTLINE_DATA", directlineTypeData);
	}
	submitParam.put("CONTRACT_DATA", contractList);
	submitParam.put("MARKETING_DATA", tapMarketingList);
	submitParam.put("MARKETING_LINEDATA", tapMarketingLineList);
	submitParam.put("IDC_DATA", idcList);
	
	submitParam.put("EOMS_ATTR_LIST", eomsAttrData);
	submitParam.put("ORDER_DATA", orderData);//订单级信息
	submitParam.put("CONFCRM_DATA", confCrmData);//关联勘察单信息
	
	if(eomsAttrList)
	{
		submitParam.put("EOMS_ATTR_LIST", eomsAttrList);//工单详细信息
	}

	submitParam.put("DEL_PARAM_LIST", delParamData);
	submitParam.put("EOMS_ATTR_LIST",eomsAttrData);
	
	var isReadonly = $("#IS_READONLY").val();
	submitParam.put("IS_READONLY",isReadonly);
	
	var message = "";
	if(isReadonly=="true"){
		message="提交成功";
	}else{
		message="流程创建成功";
	}

	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?"); 
					var pageName = getNavTitle(); 
					openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					$.MessageBox.confirm("提示:","您还未指派审核人，请在待办工单列表中指派",function(re){
						if(re=="ok"){
							closeNav();
						}
					});
				}
			}, {"ext1" : "指派"});
		}else if(data.get("ALERT_FLAG")== "true"){
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ALERT_URL").split("?");
					var ALERT_NAME = data.get("ALERT_NAME");
					var pageName = getNavTitle();
					openNav(ALERT_NAME, urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "下一步"});
		}
		else
		{
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function saveOtherInfo(otherList){
	var accPeData = new Wade.DataMap();
	accPeData.put("ATTR_CODE","ACCEPTTANCE_PERIOD");
	accPeData.put("ATTR_VALUE",$("#pattr_ACCEPTTANCE_PERIOD").val());
	accPeData.put("ATTR_NAME",$("#pattr_ACCEPTTANCE_PERIOD").attr("desc"));
	accPeData.put("RECORD_NUM","0");
	otherList.add(accPeData);
	var speStfIdData = new Wade.DataMap();
	speStfIdData.put("ATTR_CODE","SPECIAL_STAFF_ID");
	speStfIdData.put("ATTR_VALUE",$("#pattr_SPECIAL_STAFF_ID").val());
	speStfIdData.put("ATTR_NAME",$("#pattr_SPECIAL_STAFF_ID").attr("desc"));
	speStfIdData.put("RECORD_NUM","0");
	otherList.add(speStfIdData);
	var speStfSnData = new Wade.DataMap();
	speStfSnData.put("ATTR_CODE","SPECIAL_STAFF_PHONE");
	speStfSnData.put("ATTR_VALUE",$("#pattr_SPECIAL_STAFF_PHONE").val());
	speStfSnData.put("ATTR_NAME",$("#pattr_SPECIAL_STAFF_PHONE").attr("desc"));
	speStfSnData.put("RECORD_NUM","0");
	otherList.add(speStfSnData);
	return otherList;
}

function checkChangeDataLine(){
	debugger;
	var returnData = new Wade.DataMap();
	var offerCode = $("#cond_OFFER_CODE").val();
	var chks = $("#DATALINE_PARAM_UL [type=checkbox]");
	{
		//var tempDataLine = new Wade.DatasetList();
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var offerChaSpecDataset = new Wade.DatasetList();
				var idNum = chks[j].id.substring(9);
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + idNum;
				//$("#"+offerChaHiddenId).val(dataLineInfos);
				var lineData = $("#"+offerChaHiddenId).val();
				//没有数据，则表示专线未提交！
				if(!lineData){
					returnData.put("PRODUCTNO",$(chks[j]).attr("productno"));
					returnData.put("FLAG",false);
					return returnData;
				}
				
			}
		}
		returnData.put("FLAG",true);
	}
	return returnData;

}

//保存标准产品参数
function saveStandardOfferCha()
{
	var offerChaList = new Wade.DatasetList();
	var offerChaStr = $("#OFFER_CHA_"+$("#cond_OFFER_ID").val()).val();
	var offerChas = new Wade.DatasetList(offerChaStr);
	offerChaList.add(offerChas);
	return offerChaList;
}

//保存普通专线产品参数
function saveDataLineOfferCha(operCode,operType,offerCode)
{
	var offerChaList = new Wade.DatasetList();
	var mebOfferCode = $("#MEB_OFFER_CODE").val();
	var checkFlag = true;
	var errorTradeId = "";
	$("#DATALINE_PARAM_UL input[type=checkbox]:checked").each(function(){
		var value = $(this).closest("li").find("input[id*=OFFER_CHA_"+offerCode+"]").val();
		var offerChaStr = new Wade.DatasetList(value);
		if(offerCode == "5080" || offerCode == "5081")
		{
			checkFlag = checkCountryAZ(offerChaStr);
			if(checkFlag == false)
			{
				errorTradeId = $(this).closest("li").find(".title").text();
				return false;
			}
		}
		if(operType == "25")
		{
			var productNoInfo  = new Wade.DataMap();
			productNoInfo.put("ATTR_CODE","ProductNO");
			productNoInfo.put("ATTR_NAME","BSS工单号");
			productNoInfo.put("ATTR_VALUE",$(this).attr("productno"));
			offerChaStr.add(productNoInfo);
		}
		var MebOfferCha  = new Wade.DataMap();
		MebOfferCha.put("OFFER_CODE",mebOfferCode);
		MebOfferCha.put("OFFER_CHA",offerChaStr);
		MebOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
		MebOfferCha.put("OFFER_TYPE","P");
		MebOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
		MebOfferCha.put("OPER_CODE", operCode);
		
		if(operType != "20"){
			var userId = $(this).attr("userId");
			var serialNumber = $(this).attr("serialNumber");
			MebOfferCha.put("USER_ID",userId);
			MebOfferCha.put("SERIAL_NUMBER",serialNumber);
		}
		
		offerChaList.add(MebOfferCha);
	});
	
	var result = new Wade.DataMap();
	result.put("SUBOFFER", offerChaList);
	result.put("CHECK_FLAG", checkFlag);
	if(checkFlag == false)
	{
		result.put("ERROR_TRADE_ID", errorTradeId);
	}
	return result;
}
function saveChangeDataLineOfferCha(operCode){
	var mebOfferCode = $("#MEB_OFFER_CODE").val();
	var chk = document.getElementsByName("DATALINE_TAG");
	var tables = new Wade.DatasetList();
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				var table = new Wade.DataMap();
				table.put("OFFER_CODE",mebOfferCode);

			//	table.put("OFFER_CHA",offerChaStr);
				table.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
				table.put("OFFER_TYPE","P");
				table.put("OFFER_ID",$("#MEB_OFFER_ID").val());
				table.put("OPER_CODE", operCode);
				table.put("USER_ID",chk[i].getAttribute("userid"));
				table.put("SERIAL_NUMBER",chk[i].getAttribute("serialNumber"));
				tables.add(table);
            }
       	}
		
	}
	var result = new Wade.DataMap();
	result.put("SUBOFFER", tables);
	return result;
	
}

function checkCountryAZ(offerChaList)
{
	if(offerChaList)
	{
		var countyA = "";
		var cityA = "";
		var areaA = "";
        var countyZ = "";
        var cityZ = "";
        var areaZ = "";
        var villageA = "";
        var villageZ = "";

		for(var i = 0, size = offerChaList.length; i < size; i++)
		{
			var offerCha = offerChaList.get(i);
			var attrCode = offerCha.get("ATTR_CODE");
			if(attrCode === "countyA")
			{
				countyA = offerCha.get("ATTR_VALUE");
			}
			if(attrCode === "cityA")
			{
                cityA = offerCha.get("ATTR_VALUE");
			}
			if(attrCode === "areaA")
			{
                areaA = offerCha.get("ATTR_VALUE");
			}
			if(attrCode === "villageA")
			{
                villageA = offerCha.get("ATTR_VALUE");
			}
			if(attrCode === "countyZ")
			{
                countyZ = offerCha.get("ATTR_VALUE");
            }
            if(attrCode === "cityZ")
			{
                cityZ = offerCha.get("ATTR_VALUE");
            }
            if(attrCode === "areaZ")
			{
                areaZ = offerCha.get("ATTR_VALUE");
            }
            if(attrCode === "villageZ")
            {
                villageZ = offerCha.get("ATTR_VALUE");
            }
        }
        if(countyA !== "" && countyZ !== "" && cityA !== "" && areaA !== "" && cityZ !== "" && areaZ !== "" && villageA !== "" && villageZ !== "")
        {
        	var addressA = cityA + areaA + countyA + villageA;
        	var addressZ = cityZ + areaZ + countyZ + villageZ;

            if (addressA === addressZ) {
                return false;
            }
        }
    }
	return true;
}

//保存Bboss产品参数	SUBOFFER:[{OFFER_CODE:	OFFER_NAME:	OFFER_TYPE:	OFFER_ID: OPER_CODE: OFFER_CHA:[{},{}]}]
function saveBbossOfferCha()
{
	var offerChaList = new Wade.DatasetList();
	// 获得指定区域的checkbox
	var chks = $("#BBOSS_PRODUCT_UL [type=checkbox]");
	{
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)//获取选中的列表
			{
				var offerCodeId = $(chks[j]).closest("li").attr("id");
				var chkRed = $("#"+offerCodeId).find(".e_tag-red");
				if(chkRed.length>0)
				{
					return "1";
				}
				
				var offerDataOfferCodeId = "OFFER_DATA_"+offerCodeId;
				var offerChaOfferCodeId = "OFFER_CHA_"+offerCodeId;
				
				var offerDataStr = $("#"+offerDataOfferCodeId).text();
				var offerChaStr = $("#"+offerChaOfferCodeId).val();
				
				var offerDataset = new Wade.DataMap(offerDataStr);
				var offerChaset = new Wade.DatasetList(offerChaStr);
				
				//1.产品基本信息
				var offerData = new Wade.DataMap();
				offerData.put("OFFER_CODE", offerDataset.get("OFFER_CODE"));
				offerData.put("OFFER_TYPE", offerDataset.get("OFFER_TYPE"));
				offerData.put("OFFER_ID",   offerDataset.get("OFFER_ID"));
				offerData.put("OFFER_NAME", offerDataset.get("OFFER_NAME"));
				offerData.put("USER_ID", offerDataset.get("USER_ID",""));
				
				//2.产品参数信息
				var offerChaSpecList = new Wade.DatasetList();
				var offerCha = new Wade.DataMap();
				for(var i = 0; i < offerChaset.length; i++)
				{
					var temp = new Wade.DataMap(); 
					var offerChaSpec = offerChaset.get(i);
					var attrCode = offerChaSpec.get("ATTR_CODE");
					var attrValue = offerChaSpec.get("ATTR_VALUE");
					var attrName = offerChaSpec.get("ATTR_NAME");
			  		 if(""==attrValue||attrValue==null)
			  		 {
			  			continue;
			  		 }
			  		 if(""==attrCode||attrCode==null)
			  		 {
			  			continue;
			  		 }
			  		temp.put("ATTR_CODE",attrCode);
			  		temp.put("ATTR_VALUE",attrValue);
			  		temp.put("ATTR_NAME",attrName);
			  		offerChaSpecList.add(offerChaSpec);
			  	}
				offerData.put("OFFER_CHA",offerChaSpecList);
				
			  	offerChaList.add(offerData);
			}
		}
	}
	return offerChaList;
}

function saveAttach()
{
	//其他附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	//挖抢附件
	var marktingFile =new Wade.DatasetList($("#C_MARKETING_FILE_LIST").text());
	for(var i = 0, size = marktingFile.length; i < size; i++)
	{
		attachList.add(marktingFile.get(i));

	}
	//IDC附件
	if($("#IDC_C_FILE_LIST").text()!=''){
		var contractFile = new Wade.DataMap($("#IDC_C_FILE_LIST").text());
		if(contractFile.items!=""){
			attachList.add(contractFile);
		}
	}
	return attachList;
}
function saveDiscntAttach()
{
	var discntattachList = new Wade.DatasetList($("#DISCNT_ATTACH_FILE_LIST").text());
	for(var i = 0, size = discntattachList.length; i < size; i++)
	{
		discntattachList.get(i).put("REMARK", $("#DISCNT_ATTACH_REMARK").val());
	}
	//attachList.add(discntattachList);
	return discntattachList;
}

function transOperCode(operType)
{
	var operCode = "2";//变更
	if("20" == operType)
	{
		operCode = "0";//新增
	}
	else if("25" == operType)
	{
		operCode = "1";//删除
	}
	else
	{
		operCode = "2";//资源变更
	}
	return operCode;
}

function builderEomsAttrData(){
	var chaSpecObjs = $("#orderDetailPart input");
	var offerChaSpecDataset = new Wade.DatasetList();

	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
		{
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
		}
		
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("ATTR_CODE", chaSpecCode.substring(5));
		
		offerChaSpecDataset.add(offerChaSpecData);
	}
	return offerChaSpecDataset;
}
function builderchangeEomsAttrData(offerChaSpecDataset){
	var chaSpecObjs = $("#DATALINE_PARAM input");
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var offerChaSpecDatas = new Wade.DatasetList();
		var chaSpecCode = chaSpecObjs[i].id;
		var line = chaSpecCode.split("_").pop();
		var chaValue = "";
		if(chaSpecObjs[i].type == "checkbox" && chaSpecObjs[i].checked)// ||  chaSpecObjs[i].type == "radio"
		{
			var values =  $("#"+line+"new .value");
			var flag = false; 
			var productNo = "";
			for(var j =0 ;j<values.length;j++){
				var offerChaSpecData = new Wade.DataMap();
				var atts = values[j].firstChild;
				if(atts!=null){
					if(values[j].innerText != atts.getAttribute("oldvalue")){
						flag = true;
					}
					if("pam_NOTIN_LINE_INSTANCENUMBER" == atts.getAttribute("name")){
						productNo = values[j].innerText;
					}
					offerChaSpecData.put("ATTR_VALUE", values[j].innerText);
					offerChaSpecData.put("ATTR_NAME", atts.getAttribute("desc"));
					offerChaSpecData.put("ATTR_CODE", atts.getAttribute("name"));
					offerChaSpecDatas.add(offerChaSpecData);
				}
			}
			//alert(chaSpecObjs[i].flag)
			if(!flag){
				var error =  new Wade.DataMap();
				offerChaSpecDatas = new Wade.DatasetList();
				error.put("ERROR",productNo);
				offerChaSpecDatas.add(error);
				offerChaSpecDataset.add(offerChaSpecDatas);
				return offerChaSpecDataset;
			}	
			offerChaSpecDataset.add(offerChaSpecDatas);
		}
	}
	return offerChaSpecDataset;
	
}
function saveAcctInfo(eomsAttrData){
	var acctDeal = $("#cond_ACCT_DEAL").val();
	var acctDealData = new Wade.DataMap();
	acctDealData.put("ATTR_CODE","ACCT_DEAL");
	acctDealData.put("ATTR_NAME","账户操作");
	acctDealData.put("ATTR_VALUE",acctDeal);
	eomsAttrData.add(acctDealData);						
	var acctInfoDataset = new Wade.DataMap();
	if(acctDeal == 1)
	{//账户合户
		var acctId = $("#ACCT_COMBINE_ID").html();
		acctInfoDataset.put("ATTR_VALUE", acctId);
		acctInfoDataset.put("ATTR_NAME", "账户");
		acctInfoDataset.put("ATTR_CODE", "ACCT_ID");
		eomsAttrData.add(acctInfoDataset)
		var acctInfoDataset1 = new Wade.DataMap();
		acctInfoDataset1.put("ATTR_VALUE", ACTION_EXITS);
		acctInfoDataset1.put("ATTR_NAME", "账户操作编码");
		acctInfoDataset1.put("ATTR_CODE", "OPER_CODE");
		eomsAttrData.add(acctInfoDataset1);
		var acctNameData = new Wade.DataMap();
		acctNameData.put("ATTR_VALUE", $("#ACCT_NAME_span").text());
		acctNameData.put("ATTR_NAME", "账户名称");
		acctNameData.put("ATTR_CODE", "ACCT_NAME");
		eomsAttrData.add(acctNameData);					 
	}
	else
	{//账户新增
		//var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
		var acctInfo = pageData.getAccountInfo();
//		alert(acctInfo);
		if (acctInfo && acctInfo.length > 0) {
			var contractId = acctInfo.get("CONTRACT_ID");
			acctInfo.put("ACCT_CONTRACT_ID",contractId);
			acctInfo.removeKey("CONTRACT_ID");					 
		} else {
			var data = new $.DataMap();
			acctInfo = new $.DataMap();
			acctInfo.put("ACCT_NAME", getAcctName());
			acctInfo.put("ACCT_TYPE", "0");
		}
		
		acctInfo.eachKey(function(key, index, totalCount){
			var acctInfoData = new Wade.DataMap();
			acctInfoData.put("ATTR_CODE", key);
			acctInfoData.put("ATTR_VALUE", acctInfo.get(key));
			eomsAttrData.add(acctInfoData)
		});		
	}
	return eomsAttrData;
}

function verifyDataLine(){
	var operType = $("#cond_OPER_TYPE").val();
	var cheboxObj = $('#DATALINE_PARAM_UL :checkbox:checked[type="checkbox"]');
	if(cheboxObj.length == 0){
		if(operType == "21" || operType == "22"){
			MessageBox.alert("提示信息", "请选择需要变更的专线！");
			return false;
		}else if(operType == "23"){
			MessageBox.alert("提示信息", "请选择需要注销的专线！");
			return false;
		}
	}else{
		if(operType == "21"){
			var cheboxId = cheboxObj.attr("id");
			if($("#OFFER_CHA_"+cheboxId).val() == ""){
				MessageBox.alert("提示信息", "未对专线做任何修改！");
				return false;
			}
		}
	}
	return true;
}

   
			   
			 
   
									
 
													 
																							  
						 
		  
																						
					   
											   
																								
	   
	
					  
									   
					  
								   
	
 


function saveDirectlineTypeData(){
	var chaSpecObjs = $("#directlineTypePart input");
	var offerChaSpecDataset = new Wade.DatasetList();

	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
		{
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
		}
		
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("ATTR_CODE", chaSpecCode);
		
		offerChaSpecDataset.add(offerChaSpecData);
	}
	return offerChaSpecDataset;
}

function saveDelParamData(){
	var chaSpecObjs = $("#delParamPart input");
	var offerChaSpecDataset = new Wade.DatasetList();

	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio"  || chaSpecObjs[i].type == "switch" )
		{
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		}
		else
		{
			chaValue = $("#"+chaSpecCode).val();
		}
		
		var attrName = chaSpecObjs[i].getAttribute("desc");
		if(!attrName)
		{
			if(chaSpecObjs[i].getAttribute("x-wade-uicomponent") == "switch")
			{
				attrName = $(chaSpecObjs[i]).parent().attr("desc");
			}
		}
		
		
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", attrName);
		offerChaSpecData.put("ATTR_CODE", chaSpecCode);
		
		offerChaSpecDataset.add(offerChaSpecData);
	}
	return offerChaSpecDataset;
}
 

function openStaffPopup(fieldName)
{
	$("#staffSelFrame").contents().find("#field_name").val(fieldName);
	showPopup('staffPicker','staffPickerHome');
}

function ifNewContract(){
	debugger;
	var operType =  $("#DATALINE_OPER_TYPE").val();
	var ifNew =  $("#C_IF_NEW_CONTRACT").val();
	var serialNumber =  $("#DATALINE_SERIAL_NUMBER").val();
	if(operType== '0' && ifNew=='1'){
		MessageBox.alert("提示信息", "产品服务为新增,合同只可新增！");
		$("#C_IF_NEW_CONTRACT").val("");
		return false;
	}
	if(operType== '1' && ifNew=='0'){
		MessageBox.alert("提示信息", "产品服务为已有,合同只可选择已有！");
		$("#C_IF_NEW_CONTRACT").val("");
		return false;
	}
	var param = '';
	
	//审核不通过，重回APPLY节点记号
	var isReadonly = '';
	isReadonly = $("#IS_READONLY").val();
	if(isReadonly){
		param += "&IS_READONLY="+isReadonly;
		param += "&IBSYSID="+$("#IBSYSID").val();
		param += "&NODE_ID="+$("#NODE_ID").val();
	}
	var custId = $("#cond_CUST_ID").val();
	param += "&CUST_ID="+custId;
	param += "&IF_NEW="+ifNew;
	param += "&SERIAL_NUMBER="+serialNumber;
	var custId = $("#cond_CUST_ID").val();
							 						   
	if(ifNew=='0'){//新增
		$("#newContractPart").css("display", "block");//新增 合同信息元素
		$("#contractSelectPart").css("display", "none");//已有 合同选择元素
		$("#contractContentPart").css("display", "none");//已有 合同信息元素
		$("#C_CONTRACT").attr("nullable", "yes");//已有合同
		var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
		var bpmTempletId = node.get("BPM_TEMPLET_ID");
//		$("#C_CONTRACT_NAME_OLD").attr("disabled", "false");//合同名称
//		$("#C_CONTRACT_NAME_OLD").attr("nullable", "yes");//合同名称
//		$("#C_PRODUCT_NAME").attr("nullable", "yes");//产品名称
//		$("#C_PRODUCT_START_DATE_OLD").attr("nullable", "yes");//开始时间
//		$("#C_PRODUCT_END_DATE_OLD").attr("nullable", "yes");//结束时间
		
		$.beginPageLoading("正在处理...");
		ajaxSubmit(this,'initContract',param,"newContractPart",function(data){
			$.endPageLoading();
			if(isReadonly=="true"){
				$("#C_FILE_LIST").text(data.toString());
			}
			//获取合同附件name和id
			$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
				var data = new Wade.DataMap();
				data.put("FILE_ID", file.fileId);
				data.put("FILE_NAME", file.name);
				data.put("ATTACH_TYPE", "C");
				
				if(containSpecial(file.name)){
					MessageBox.alert("错误", "【"+file.name+"】文件名称包含特殊字符，请修改后再上传！");
					$("#CONTRACT_FILE_LIST").val("");
					$("#CONTRACT_FILE_LIST_name").val("");
					$("#C_FILE_LIST").val("");
					$("#C_FILE_LIST_NAME").val("");
					return false; 
				}
				
				$("#C_FILE_LIST").text(data.toString());
				$("#C_FILE_LIST").val(file.fileId+':'+file.name);
				$("#C_FILE_LIST_NAME").val(file.name);
			});
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}else if(ifNew=='1'){//已有
		$("#newContractPart").css("display", "none");//新增 合同信息元素
		$("#contractSelectPart").css("display", "block");//已有 合同选择元素
		$("#contractContentPart").css("display", "block");//已有 合同信息元素
		
		$.beginPageLoading("正在处理...");
		ajaxSubmit(this,'initContract',param,"contractSelectPart",function(data){
			$.endPageLoading();
			/*if(isReadonly=="true"){
				chooseContract();
			}*/
		},null);
	}else if(new Wade.DataMap($("#NODE_TEMPLETE").text()).get("BPM_TEMPLET_ID")=='EDIRECTLINECONTRACTCHANGE'){
		$("#newContractPart").css("display", "");
		$("#contractSelectPart").css("display", "none");
	}
	else{
		$("#newContractPart").css("display", "none");
		$("#contractSelectPart").css("display", "none");
	}
	var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var bpmTempletId = node.get("BPM_TEMPLET_ID");
	if(bpmTempletId == "EDIRECTLINECONTRACTCHANGE" ){
		if(isReadonly=="true")
		{
			var fileList=$("#C_FILE_LIST").val();
			if(fileList!=''){
				var arr =fileList.split(":");
				if(arr.length==2){
					var data2 = new Wade.DataMap();
					data2.put("FILE_ID", arr[0]);
					data2.put("FILE_NAME", arr[1]);
					data2.put("ATTACH_TYPE", "C");
					$("#C_FILE_LIST").text(data2.toString());
				}
			}
			$("#C_FILE_LIST").text(data.toString());
		}
		$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
			var data1 = new Wade.DataMap();
			data1.put("FILE_ID", file.fileId);
			data1.put("FILE_NAME", file.name);
			data1.put("ATTACH_TYPE", "C");
			$("#C_FILE_LIST").text(data1.toString());
			$("#C_FILE_LIST").val(file.fileId+':'+file.name);
			
			$("#C_FILE_LIST_NAME").val(file.name);
		});
	}
}

function chooseApplyContract(){
	debugger;
	var productId = $("#cond_OFFER_CODE").val();
	var productName = $("#cond_OFFER_NAME").val();
	var custId = $("#cond_CUST_ID").val();
	var contract =  $("#C_CONTRACT").val();
	var arr = contract.split("|");
	var contractId = arr[0];
	
	$.beginPageLoading("正在处理...");
	ajaxSubmit(this,'getContractById','&CONTRACT_ID='+contractId+'&PRODUCT_ID='+productId+'&CUST_ID='+custId+'&PRODUCT_NAME='+productName,"contractContentPart,newContractPart",
		function(data){
		$.endPageLoading();
		$("#CONTRACT_ID").val(contractId);	
		
		$("#C_PROTOCOL_CODE").attr("nullable", "yes");//合同协议编码
		$("#C_CONTRACT_NAME").attr("disabled", "true");//合同名称
		$("#C_CONTRACT_NAME_NEW").attr("nullable", "yes");//
		$("#C_POATT_TYPE").attr("nullable", "yes");//附件类型
		$("#C_CONTRACT_IS_AUTO_RENEW").attr("nullable", "yes");//是否自动续约
		$("#C_CONT_FEE").attr("nullable", "yes");//签约资费
		$("#C_CONTRACT_TYPE_CODE").attr("nullable", "yes");//合同类型
		$("#C_CONTRACT_WRITE_TYPE").attr("nullable", "yes");//合同签订类型
		$("#C_CONTRACT_WRITER").attr("nullable", "yes");//合同项目经理
		$("#C_CONTRACT_WRITE_DATE").attr("nullable", "yes");//合同签订日期
		$("#C_CONTRACT_START_DATE").attr("nullable", "yes");//合同有效起始日期
		$("#C_CONTRACT_END_DATE").attr("nullable", "yes");//合同有效终止日期
		$("#C_CONTRACT_IN_DATE").attr("nullable", "yes");//合同录入日期
		$("#C_FILE_LIST").attr("nullable", "yes");//合同附件
		$("#C_CONTRACT_STATE_CODE").attr("nullable", "yes");//合同状态
		$("#C_PRODUCT_START_DATE").attr("nullable", "yes");//产品有效起始日期
		$("#C_PRODUCT_END_DATE").attr("nullable", "yes");//产品有效终止日期
		$("#C_FILE_LIST_NAME").attr("nullable", "yes");//附件名称
		
	},null);
}

function confCrm(){
	debugger;
	var confCrm =  $("#pattr_IF_CHOOSE_CONFCRM").val();
	if(confCrm=='0'){//否
		//将主页面上已存在的数据删除
		$("#DATALINE_PARAM_UL").children().remove();
		
		$("#batchAdd").css("display", "block");
		$("#add").css("display", "block");
		$("#delete").css("display", "block");
		$("#chooseConfCrm").css("display", "none");
		//不选择关联勘察单，隐藏字段设为空
		$("#CONFIBSYSID").val(""); 
	}else if(confCrm=='1'){//是
		//将主页面上已存在的数据删除
		$("#DATALINE_PARAM_UL").children().remove();
		
		$("#batchAdd").css("display", "none");
		$("#add").css("display", "none");
		$("#delete").css("display", "block");
		$("#chooseConfCrm").css("display", "");
	}
}

function chooseConfCrm(){
	debugger;
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_OFFER_CODE").val();
	var templetId = $("#TEMPLET_ID").val();
	var bizrange = $("#pattr_BIZRANGE").val();
	var userId = $("#apply_USER_ID").val();
	
	if(templetId=="EDIRECTLINEOPEN"){
		var bpmTempletId = "ERESOURCECONFIRMZHZG";
	}
	if(templetId=="EDIRECTLINECHANGE"){
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(changeMode == ""){
			$.validate.alerter.one($("#pattr_CHANGEMODE")[0],"请先选择业务调整场景!");
			return false;
		}
		var bpmTempletId = "ECHANGERESOURCECONFIRM";
	}
	var param = "&GROUP_ID="+groupId+"&PRODUCT_ID="+productId+"&BPM_TEMPLET_ID="+bpmTempletId+"&BIZRANGE="+bizrange+"&EC_USER_ID="+userId;
	
	$.beginPageLoading("正在处理...");
	$.ajax.submit("", "initConfCrm", param, "confCrmPopupItem", function(data){
		$.endPageLoading();
		$("#confCrmExport").beforeAction(function(e){
			var rowDatas = myTable.getCheckedRowsData("LINENO");
			if(rowDatas==null||rowDatas==""){
				$.validate.alerter.one(rowDatas,"请先选择需要导出的专线!");
				return false;
			}
		});
		
		$("#importFile").beforeAction(function(e){
			MessageBox.alert("提示信息", "请先选择关联勘察单号！");
			return false;
		});
		
		showPopup('popup03', 'confCrmPopupItem', true);
		
	},null);
}

function initAcctData(){
	var acctDeal = $("#cond_ACCT_DEAL").val();
	$.each($("#cond_ACCT_DEAL_span span"),function(){
		var idx = this.getAttribute("idx");
		if(idx==acctDeal){
			this.setAttribute("class","e_segmentOn");
			return false;
		}
	});
	
	var acctInfo = new Wade.DataMap($("#APPLY_ACCT_INFO").text());
	
	if(acctDeal == "1"){
		var custId = $("#cond_CUST_ID").val();
		var acctId = acctInfo.get("ACCT_ID");
		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "queryEcAccountList", "CUST_ID="+custId, "ecAccountListPart", function(data){
			$.endPageLoading();
			
			if(acctId)
			{
				$("#"+acctId).addClass("checked");
			}
			
			//showPopup('popup', 'chooseEcAccount', true);
			
			var acctName = acctInfo.get("ACCT_NAME");
			var html = "<span class='text' id='ACCT_NAME_span'>"+acctName+"</span>";
			html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
			$("#i_acctCombPart .value").html(html);
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
	}else if(acctDeal == "0"){
		var acctName = acctInfo.get("ACCT_NAME")
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_NAME_span">'+acctName+'</span>');
		
		$("#accountPopupItem_ACCT_NAME").val(acctName);
		var acctType = acctInfo.get("ACCT_TYPE");
		$("#accountPopupItem_ACCT_TYPE").val(acctType);
		if(acctType==1||acctType==2||acctType==3){
			$("#accountPopupItem_CONTRACT_ID").val(acctInfo.get("ACCT_CONTRACT_ID"));
			$("#accountPopupItem_SUPER_BANK_CODE").val(acctInfo.get("SUPER_BANK_CODE"));
			
			changeSuperBankCode(acctInfo.get("SUPER_BANK_CODE"));
			
			//$("#accountPopupItem_BANK_CODE_span span").text("工商银行河南分理处");
			$("#accountPopupItem_BANK_CODE").attr("value",acctInfo.get("BANK_CODE"));
			//$("#accountPopupItem_BANK_CODE").text(acctInfo.get("BANK_NAME"));
			$("#accountPopupItem_BANK_ACCT_NO").val(acctInfo.get("BANK_ACCT_NO"));
		}
		pageData.setAccountInfo(acctInfo);
	}
	
	acctDealChange(document.getElementById("cond_ACCT_DEAL"));
	
}
	
function changeSuperBankCode(value){
	var param = "&SUPER_BANK_CODE=" + value;
	
	$.ajax.submit('', "queryBanksBySuperBank", param, "accountPopupItemBankPart", function (data) {
	    // success
		var result = JSON.parse(json);
		//alert(1);
		
	}, function (errCode, errDesc) {
	    // faild
	},{
		async: false 
	});
}


function acctDealChange(el)
{
	var acctDeal = el.value;
	if(acctDeal == "1")
	{
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}
	else
	{
		$("#i_acctCombPart").css("display", "none");
		$("#i_acctSelPart").css("display", "");
	}
}

function showAcctAddPopup(el) {
	var offerCode=$("#cond_OFFER_CODE").val();
	/*var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId= custInfo.get("CUST_ID");*/
	/*if("9898"==offerCode||"7345"==offerCode){
		openNav("集团账户管理", "igroup.creategroupacct", "queryAcctInfoList", "&PRODUCT_ID="+offerCode+"&CUST_ID="+custId, "/order/iorder");
		$("#cond_ACCT_DEAL").val("1");
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}else{*/
		showPopup('popup', 'accountPopupItem', true);
		accountPopupItem.showAddPopup();
		var data = {};
		data["ACCT_NAME"] = createAcctName();
		accountPopupItem.fillAcctPopup(data);
	/*}*/
}

//合户操作：显示账户列表
function showAcctCombPopup(el)
{
	
	/*alert($.enterpriseLogin.getInfo().get("CUST_INFO"));
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");*/
	var custId = $("#cond_CUST_ID").val();
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryEcAccountList", "CUST_ID="+custId, "ecAccountListPart", function(data){
		$.endPageLoading();
		var acctId = $("#ACCT_COMBINE_ID").html();
		if(acctId)
		{
			$("#"+acctId).addClass("checked");
		}
		
		showPopup('popup', 'chooseEcAccount', true);
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

//合户操作：选择付费账户
function selectAccount(el)
{
	$("#ecAccountUL li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	
	var acctId = $(el).attr("id");
	var acctName = $(el).children().find("div[class=title]").html();
	var html = "<span class='text' id='ACCT_NAME_span'>"+acctName+"</span>";
	html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
	$("#i_acctCombPart .value").html(html);
	
	hidePopup('popup');
}

//生成账户名称：集团名称_商品名称
function createAcctName()
{	
	var acctName = $("#cond_CUST_NAME").val();
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}

	return acctName;
}

//生成账户名称：集团名称_商品名称
function getAcctName()
{	
	var acctName = $("#ACCT_COMBINE_ID").html();
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}

	return acctName;
}

/**
 * 账户新增回调
 */
function accountPopupItemCallback(data) {
	var acctInfo = data.get("ACCT_INFO");
	pageData.setAccountInfo(acctInfo);
	var acctName = $("#accountPopupItem_ACCT_NAME").val(); 
	// 改变选择框样式
	if (data) {
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_NAME_span">'+acctName+'</span>');
	}
}

function sureConfCrm(){
	debugger;
	var temp = new Wade.DatasetList();
	var rowDatas = myTable.getCheckedRowsData("LINENO");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选勘察信息!");
		return false;
	}
	
	//将主页面上已存在的数据删除
	$("#DATALINE_PARAM_UL").children().remove();
	
	var offerchaList = new Wade.DatasetList($("#offerchalist").val());
	for (var i = 0; i < offerchaList.length; i++) {
		var offercha = offerchaList[i];
		temp.add(offercha);
	}
	
	for(var i=0;i<rowDatas.length;i++){
		if(!tableDataLineInfos(rowDatas,i)){
			return false;
		}else{
			var rowData = tableDataLineInfos(rowDatas,i);
		}
		
		var param;
		param = "&DATA="+rowData;
		sureDataLineParamList(rowData);
		
		var batchImports = new Wade.DatasetList();
		var key = rowData.keys;
		for (var m = 0; m < key.length; m++) {
			var value = rowData.get(m);
			var batchImport = new Wade.DataMap();
			if(key[m]=="ROUTEMODE"){
				if(value=="单节点单路由"){
					value = "0";
				}else if(value=="单节点双路由"){
					value = "1";
				}else if(value=="双节点双路由"){
					value = "2";
				}
			}
			batchImport.put("ATTR_CODE", key[m]);
			batchImport.put("ATTR_VALUE", value);
			var tempName = $("input[name='"+key[m]+"']").attr("desc");
			var pattrTempName = "pattr_"+key[m];
			var tempName2 = $("input[name='"+pattrTempName+"']").attr("desc");
			if(tempName!=undefined){
				batchImport.put("ATTR_NAME", tempName);
			}else{
				batchImport.put("ATTR_NAME", tempName2);
			}
			batchImports.add(batchImport);
		}
		temp.add(batchImports);
		$("#offerchalist").val(temp);
	}
	$("#CHANGELINENOS").val("");
	$("#CHANGELINENOS").val(rowDatas);
	
	hidePopup("popup03");
}

function tableDataLineInfos (rowDatas,i){
	debugger;
	var productId = $("#cond_OFFER_CODE").val();
	var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var bpmTempletId = node.get("BPM_TEMPLET_ID");
	var changeMode = $("#CHANGE_MODE").val();
	
	var rowData = rowDatas.get(i);//new Wade.DataMap();
	var tabLine = rowData.get("LINENO_ID");
	
	if(bpmTempletId == 'EDIRECTLINEOPENPBOSS'){
		var hiddenBandWidth = $("#"+tabLine+"HIDDEN_BANDWIDTH").val();
		rowData.put("HIDDEN_BANDWIDTH",hiddenBandWidth);
		
		var bandWidth = $("#"+tabLine+"BANDWIDTH").val();
		rowData.put("NOTIN_RSRV_STR1",bandWidth);
		
		if(eval(hiddenBandWidth)<eval(bandWidth)){
			MessageBox.alert("提示信息", "带宽不能大于勘察单带宽！");
			return false;
		}
		
		var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
		rowData.put("BIZSECURITYLV",bizSecurityLv);
		
		var portACustom = $("#"+tabLine+"PORTACUSTOM").val();
		rowData.put("PORTACUSTOM",portACustom);
		
		var portAInterFaceType = $("#"+tabLine+"PORTAINTERFACETYPE").val();
		rowData.put("PORTAINTERFACETYPE",portAInterFaceType);
		
		var portAContact = $("#"+tabLine+"PORTACONTACT").val();
		rowData.put("PORTACONTACT",portAContact);
		
		var portAContactPhone = $("#"+tabLine+"PORTACONTACTPHONE").val();
		rowData.put("PORTACONTACTPHONE",portAContactPhone);
		
		var tradeName = $("#"+tabLine+"TRADENAME").val();
		rowData.put("TRADENAME",tradeName);
		
		var notinRsrvStr2 = $("#"+tabLine+"NOTIN_RSRV_STR2").val();
		rowData.put("NOTIN_RSRV_STR2",notinRsrvStr2);
		
		var notinRsrvStr3 = $("#"+tabLine+"NOTIN_RSRV_STR3").val();
		rowData.put("NOTIN_RSRV_STR3",notinRsrvStr3);
		
		if(productId=='7012'||productId=='70121'||productId=='70122'){
			var routeMode = $("#"+tabLine+"ROUTEMODE").val();
			if (routeMode == '0') {
				routeMode = "单节点单路由";
			} else if (routeMode == '1') {
				routeMode = "单节点双路由";
			} else if (routeMode == '2') {
				routeMode = "双节点双路由";
			}
			rowData.put("ROUTEMODE",routeMode);
			
			var portZCustom = $("#"+tabLine+"PORTZCUSTOM").val();
			rowData.put("PORTZCUSTOM",portZCustom);
			
			var portZInterFaceType = $("#"+tabLine+"PORTZINTERFACETYPE").val();
			rowData.put("PORTZINTERFACETYPE",portZInterFaceType);
			
			var portZContact = $("#"+tabLine+"PORTZCONTACT").val();
			rowData.put("PORTZCONTACT",portZContact);
			
			var portZContactPhone = $("#"+tabLine+"PORTZCONTACTPHONE").val();
			rowData.put("PORTZCONTACTPHONE",portZContactPhone);
			
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			var notinRsrvStr16 = $("#"+tabLine+"NOTIN_RSRV_STR16").val();
			rowData.put("NOTIN_RSRV_STR16",notinRsrvStr16);
			//如果必填项为空，则算这条专线未修改
			if (bandWidth == "" || bizSecurityLv == "" || portACustom == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || tradeName == ""
					|| notinRsrvStr2 == "" || notinRsrvStr3 == ""
					|| portZCustom == "" || portZInterFaceType == ""
					|| portZContact == "" || portZContactPhone == ""
					|| notinRsrvStr11 == "" || notinRsrvStr12 == ""
					|| notinRsrvStr16 == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}else if(productId=='7011'||productId=='70111'||productId=='70112'){
			var transferMode = $("#"+tabLine+"TRANSFERMODE").val();
			rowData.put("TRANSFERMODE",transferMode);
			
			var ipType = $("#"+tabLine+"IPTYPE").val();
			rowData.put("IPTYPE",ipType);
			
			var cusappServIpAddNum = $("#"+tabLine+"CUSAPPSERVIPADDNUM").val();
			rowData.put("CUSAPPSERVIPADDNUM",cusappServIpAddNum);
			
			var cusappServIpv4AddNum = $("#"+tabLine+"CUSAPPSERVIPV4ADDNUM").val();
			rowData.put("CUSAPPSERVIPV4ADDNUM",cusappServIpv4AddNum);
			
			var cusappServIpv6AddNum = $("#"+tabLine+"CUSAPPSERVIPV6ADDNUM").val();
			rowData.put("CUSAPPSERVIPV6ADDNUM",cusappServIpv6AddNum);
			
			var domainName = $("#"+tabLine+"DOMAINNAME").val();
			rowData.put("DOMAINNAME",domainName);
			
			var maindoMainAdd = $("#"+tabLine+"MAINDOMAINADD").val();
			rowData.put("MAINDOMAINADD",maindoMainAdd);
			
			var notinRsrvStr10 = $("#"+tabLine+"NOTIN_RSRV_STR10").val();
			rowData.put("NOTIN_RSRV_STR10",notinRsrvStr10);
			
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			//如果必填项为空，则算这条专线未修改
			if (bandWidth == "" || bizSecurityLv == "" || portACustom == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || tradeName == ""
					|| notinRsrvStr2 == "" || notinRsrvStr3 == ""
					|| ipType == "" || cusappServIpAddNum == ""
					|| cusappServIpv4AddNum == "" || cusappServIpv6AddNum == ""
					|| domainName == "" || maindoMainAdd == ""	
					|| notinRsrvStr10 == "" || notinRsrvStr11 == "" 
					|| notinRsrvStr12 == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}else if(productId == '7010'){
			var transferMode = $("#"+tabLine+"TRANSFERMODE").val();
			rowData.put("TRANSFERMODE",transferMode);
			
			var supportMode = $("#"+tabLine+"SUPPORTMODE").val();
			rowData.put("SUPPORTMODE",supportMode);
			
			var repeaterNum = $("#"+tabLine+"REPEATERNUM").val();
			rowData.put("REPEATERNUM",repeaterNum);
			
			var amount = $("#"+tabLine+"AMOUNT").val();
			rowData.put("AMOUNT",amount);
			
			var isCustomerPe = $("#"+tabLine+"ISCUSTOMERPE").val();
			rowData.put("ISCUSTOMERPE",isCustomerPe);
			
			var customerDeviceMode = $("#"+tabLine+"CUSTOMERDEVICEMODE").val();
			rowData.put("CUSTOMERDEVICEMODE",customerDeviceMode);
			
			var customerDeviceType = $("#"+tabLine+"CUSTOMERDEVICETYPE").val();
			rowData.put("CUSTOMERDEVICETYPE",customerDeviceType);
			
			var customerDeviceVendor = $("#"+tabLine+"CUSTOMERDEVICEVENDOR").val();
			rowData.put("CUSTOMERDEVICEVENDOR",customerDeviceVendor);
			
			var phonePerMission = $("#"+tabLine+"PHONEPERMISSION").val();
			rowData.put("PHONEPERMISSION",phonePerMission);
			
			var phoneList = $("#"+tabLine+"PHONELIST").val();
			rowData.put("PHONELIST",phoneList);
			
			var conproductNo = $("#"+tabLine+"CONPRODUCTNO").val();
			rowData.put("CONPRODUCTNO",conproductNo);
			
			var notinRsrvStr15 = $("#"+tabLine+"NOTIN_RSRV_STR15").val();
			rowData.put("NOTIN_RSRV_STR15",notinRsrvStr15);
			
			var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
			rowData.put("IS_MODIFY_TAG","1");
		}else if(productId == '7016'){
			rowData.put("BANDWIDTH",bandWidth);
			var transferMode = $("#"+tabLine+"TRANSFERMODE").val();
			rowData.put("TRANSFERMODE",transferMode);
			var supportMode = $("#"+tabLine+"SUPPORTMODE").val();
			rowData.put("SUPPORTMODE",supportMode);
			var REPEATERNUM = $("#"+tabLine+"REPEATERNUM").val();
			rowData.put("REPEATERNUM",REPEATERNUM);
			var AMOUNT = $("#"+tabLine+"AMOUNT").val();
			rowData.put("AMOUNT",AMOUNT);
			var PORTARATE = $("#"+tabLine+"PORTARATE").val();
			rowData.put("PORTARATE",PORTARATE);
			var cusappServIpAddNum = $("#"+tabLine+"CUSAPPSERVIPADDNUM").val();
			rowData.put("CUSAPPSERVIPADDNUM",cusappServIpAddNum);
			var isCustomerPe = $("#"+tabLine+"ISCUSTOMERPE").val();
			rowData.put("ISCUSTOMERPE",isCustomerPe);
			var customerDeviceMode = $("#"+tabLine+"CUSTOMERDEVICEMODE").val();
			rowData.put("CUSTOMERDEVICEMODE",customerDeviceMode);
			
			var customerDeviceType = $("#"+tabLine+"CUSTOMERDEVICETYPE").val();
			rowData.put("CUSTOMERDEVICETYPE",customerDeviceType);
			
			var phoneList = $("#"+tabLine+"PHONELIST").val();
			rowData.put("PHONELIST",phoneList);
			
			var customerDeviceList = $("#"+tabLine+"CUSTOMERDEVICELIST").val();
			rowData.put("CUSTOMERDEVICELIST",customerDeviceList);
			
			var customerDeviceVendor = $("#"+tabLine+"CUSTOMERDEVICEVENDOR").val();
			rowData.put("CUSTOMERDEVICEVENDOR",customerDeviceVendor);
			var domainName = $("#"+tabLine+"DOMAINNAME").val();
			rowData.put("DOMAINNAME",domainName);
			var mainDoMainAdd = $("#"+tabLine+"MAINDOMAINADD").val();
			rowData.put("MAINDOMAINADD",mainDoMainAdd);
			
			var notinRsrvStr10 = $("#"+tabLine+"NOTIN_RSRV_STR10").val();
			rowData.put("NOTIN_RSRV_STR10",notinRsrvStr10);
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			var amount = $("#"+tabLine+"AMOUNT").val();
			rowData.put("AMOUNT",amount);
			var portaRate = $("#"+tabLine+"PORTARATE").val();
			rowData.put("PORTARATE",portaRate);
			//如果必填项为空，则算这条专线未修改
			if (bandWidth == "" || notinRsrvStr2 == "" || notinRsrvStr10 == ""
					|| notinRsrvStr11 == "" || notinRsrvStr12 == ""
					|| bizSecurityLv == "" || supportMode == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || cusappServIpAddNum == ""
					|| isCustomerPe == "" || phoneList == ""
					|| tradeName == ""||amount==""||portaRate==""
					) {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}
	}
	
	if(changeMode == '扩容'){
		var hiddenBandWidth = $("#"+tabLine+"HIDDEN_BANDWIDTH").val();
		rowData.put("HIDDEN_BANDWIDTH",hiddenBandWidth);
		
		var bandWidth = $("#"+tabLine+"BANDWIDTH").val();
		rowData.put("NOTIN_RSRV_STR1",bandWidth);
		
		if(eval(hiddenBandWidth)<eval(bandWidth)){
			MessageBox.alert("提示信息", "带宽不能大于勘察单带宽！");
			return false;
		}
		rowData.put("BANDWIDTH",bandWidth);

		var notinRsrvStr2 = $("#"+tabLine+"NOTIN_RSRV_STR2").val();
		rowData.put("NOTIN_RSRV_STR2",notinRsrvStr2);
		
		var notinRsrvStr3 = $("#"+tabLine+"NOTIN_RSRV_STR3").val();
		rowData.put("NOTIN_RSRV_STR3",notinRsrvStr3);
		
		//如果必填项为空，则算这条专线未修改
		if (bandWidth == "" || notinRsrvStr2 == "" || notinRsrvStr3 == "") {
			var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
			rowData.put("IS_MODIFY_TAG","0");
		}else{
			var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
			rowData.put("IS_MODIFY_TAG","1");
		}
	}else if(changeMode == '业务保障级别调整'){
		if(productId=='7012'||productId=='70121'||productId=='70122'){
			var notinRsrvStr16 = $("#"+tabLine+"NOTIN_RSRV_STR16").val();
			rowData.put("NOTIN_RSRV_STR16",notinRsrvStr16);
			
			var routeMode = $("#"+tabLine+"ROUTEMODE").val();
			if (routeMode == '0') {
				routeMode = "单节点单路由";
			} else if (routeMode == '1') {
				routeMode = "单节点双路由";
			} else if (routeMode == '2') {
				routeMode = "双节点双路由";
			}
			rowData.put("ROUTEMODE",routeMode);
			
			var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
			rowData.put("BIZSECURITYLV",bizSecurityLv);
			
			//如果必填项为空，则算这条专线未修改
			if (notinRsrvStr16 == "" || bizSecurityLv == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}else{
			var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
			rowData.put("BIZSECURITYLV",bizSecurityLv);
			
			//如果必填项为空，则算这条专线未修改
			if (bizSecurityLv == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}
		
	}else if(changeMode == '异楼搬迁'){
		if(productId=='7012'||productId=='70121'||productId=='70122'){
			var notinRsrvStr3 = $("#"+tabLine+"NOTIN_RSRV_STR3").val();
			rowData.put("NOTIN_RSRV_STR3",notinRsrvStr3);
			
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			var routeMode = $("#"+tabLine+"ROUTEMODE").val();
			if (routeMode == '0') {
				routeMode = "单节点单路由";
			} else if (routeMode == '1') {
				routeMode = "单节点双路由";
			} else if (routeMode == '2') {
				routeMode = "双节点双路由";
			}
			rowData.put("ROUTEMODE",routeMode);
			
			var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
			rowData.put("BIZSECURITYLV",bizSecurityLv);
			
			var portACustom = $("#"+tabLine+"PORTACUSTOM").val();
			rowData.put("PORTACUSTOM",portACustom);
			
			var cityA = $("#"+tabLine+"CITYA").val();
			rowData.put("CITYA",cityA);
			
			var areaA = $("#"+tabLine+"AREAA").val();
			rowData.put("AREAA",areaA);
			
			var countyA = $("#"+tabLine+"COUNTYA").val();
			rowData.put("COUNTYA",countyA);
			
			var villageA = $("#"+tabLine+"VILLAGEA").val();
			rowData.put("VILLAGEA",villageA);
			
			var portAInterFaceType = $("#"+tabLine+"PORTAINTERFACETYPE").val();
			rowData.put("PORTAINTERFACETYPE",portAInterFaceType);
			
			var portAContact = $("#"+tabLine+"PORTACONTACT").val();
			rowData.put("PORTACONTACT",portAContact);
			
			var portAContactPhone = $("#"+tabLine+"PORTACONTACTPHONE").val();
			rowData.put("PORTACONTACTPHONE",portAContactPhone);
			
			var tradeName = $("#"+tabLine+"TRADENAME").val();
			rowData.put("TRADENAME",tradeName);
			
			var portZCustom = $("#"+tabLine+"PORTZCUSTOM").val();
			rowData.put("PORTZCUSTOM",portZCustom);
				
			var cityZ = $("#"+tabLine+"CITYZ").val();
			rowData.put("CITYZ",cityZ);
			
			var areaZ = $("#"+tabLine+"AREAZ").val();
			rowData.put("AREAZ",areaZ);
			
			var countyZ = $("#"+tabLine+"COUNTYZ").val();
			rowData.put("COUNTYZ",countyZ);
			
			var villageZ = $("#"+tabLine+"VILLAGEZ").val();
			rowData.put("VILLAGEZ",villageZ);
			
			var portZInterFaceType = $("#"+tabLine+"PORTZINTERFACETYPE").val();
			rowData.put("PORTZINTERFACETYPE",portZInterFaceType);
				
			var portZContact = $("#"+tabLine+"PORTZCONTACT").val();
			rowData.put("PORTZCONTACT",portZContact);
				
			var portZContactPhone = $("#"+tabLine+"PORTZCONTACTPHONE").val();
			rowData.put("PORTZCONTACTPHONE",portZContactPhone);
			
			//如果必填项为空，则算这条专线未修改
			if (bizSecurityLv == "" || portACustom == "" || cityA == ""
					|| areaA == "" || countyA == "" || villageA == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || tradeName == ""
					|| notinRsrvStr3 == "" || portZCustom == ""
					|| areaZ == "" || countyZ == "" || villageZ == ""
					|| portZInterFaceType == "" || portZContact == ""
					|| portZContactPhone == "" || notinRsrvStr11 == ""
					|| notinRsrvStr12 == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}else if(productId=='7011'||productId=='70111'||productId=='70112'){
			var notinRsrvStr3 = $("#"+tabLine+"NOTIN_RSRV_STR3").val();
			rowData.put("NOTIN_RSRV_STR3",notinRsrvStr3);
			
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
			rowData.put("BIZSECURITYLV",bizSecurityLv);
			
			var portACustom = $("#"+tabLine+"PORTACUSTOM").val();
			rowData.put("PORTACUSTOM",portACustom);
			
			var cityA = $("#"+tabLine+"CITYA").val();
			rowData.put("CITYA",cityA);
			
			var areaA = $("#"+tabLine+"AREAA").val();
			rowData.put("AREAA",areaA);
			
			var countyA = $("#"+tabLine+"COUNTYA").val();
			rowData.put("COUNTYA",countyA);
			
			var villageA = $("#"+tabLine+"VILLAGEA").val();
			rowData.put("VILLAGEA",villageA);
			
			var portAInterFaceType = $("#"+tabLine+"PORTAINTERFACETYPE").val();
			rowData.put("PORTAINTERFACETYPE",portAInterFaceType);
			
			var portAContact = $("#"+tabLine+"PORTACONTACT").val();
			rowData.put("PORTACONTACT",portAContact);
			
			var portAContactPhone = $("#"+tabLine+"PORTACONTACTPHONE").val();
			rowData.put("PORTACONTACTPHONE",portAContactPhone);
			
			var domainName = $("#"+tabLine+"DOMAINNAME").val();
			rowData.put("DOMAINNAME",domainName);
			
			var mainDoMainAdd = $("#"+tabLine+"MAINDOMAINADD").val();
			rowData.put("MAINDOMAINADD",mainDoMainAdd);
			
			var tradeName = $("#"+tabLine+"TRADENAME").val();
			rowData.put("TRADENAME",tradeName);
			
			//如果必填项为空，则算这条专线未修改
			if (bizSecurityLv == "" || portACustom == "" || areaA == ""
					|| countyA == "" || villageA == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || tradeName == ""
					|| notinRsrvStr3 == "" || domainName == ""
					|| maindoMainAdd == "" || notinRsrvStr11 == ""
					|| notinRsrvStr12 == "") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}else if(productId=='7016'){
			var notinRsrvStr3 = $("#"+tabLine+"NOTIN_RSRV_STR3").val();
			rowData.put("NOTIN_RSRV_STR3",notinRsrvStr3);
			
			var notinRsrvStr11 = $("#"+tabLine+"NOTIN_RSRV_STR11").val();
			rowData.put("NOTIN_RSRV_STR11",notinRsrvStr11);
			
			var notinRsrvStr12 = $("#"+tabLine+"NOTIN_RSRV_STR12").val();
			rowData.put("NOTIN_RSRV_STR12",notinRsrvStr12);
			
			var bizSecurityLv = $("#"+tabLine+"BIZSECURITYLV").val();
			rowData.put("BIZSECURITYLV",bizSecurityLv);
			
			var portACustom = $("#"+tabLine+"PORTACUSTOM").val();
			rowData.put("PORTACUSTOM",portACustom);
			
			var cityA = $("#"+tabLine+"CITYA").val();
			rowData.put("CITYA",cityA);
			
			var areaA = $("#"+tabLine+"AREAA").val();
			rowData.put("AREAA",areaA);
			
			var countyA = $("#"+tabLine+"COUNTYA").val();
			rowData.put("COUNTYA",countyA);
			
			var villageA = $("#"+tabLine+"VILLAGEA").val();
			rowData.put("VILLAGEA",villageA);
			
			var portAInterFaceType = $("#"+tabLine+"PORTAINTERFACETYPE").val();
			rowData.put("PORTAINTERFACETYPE",portAInterFaceType);
			
			var portAContact = $("#"+tabLine+"PORTACONTACT").val();
			rowData.put("PORTACONTACT",portAContact);
			
			var portAContactPhone = $("#"+tabLine+"PORTACONTACTPHONE").val();
			rowData.put("PORTACONTACTPHONE",portAContactPhone);
			
			var domainName = $("#"+tabLine+"DOMAINNAME").val();
			rowData.put("DOMAINNAME",domainName);
			
			var mainDoMainAdd = $("#"+tabLine+"MAINDOMAINADD").val();
			rowData.put("MAINDOMAINADD",mainDoMainAdd);
			
			var tradeName = $("#"+tabLine+"TRADENAME").val();
			rowData.put("TRADENAME",tradeName);
			
			var portaRate = $("#"+tabLine+"PORTARATE").val();
			rowData.put("PORTARATE",portaRate);
			
			var isCustomerPe = $("#"+tabLine+"ISCUSTOMERPE").val();
			rowData.put("ISCUSTOMERPE",isCustomerPe);
			var customerDeviceMode = $("#"+tabLine+"CUSTOMERDEVICEMODE").val();
			rowData.put("CUSTOMERDEVICEMODE",customerDeviceMode);
			
			var customerDeviceType = $("#"+tabLine+"CUSTOMERDEVICETYPE").val();
			rowData.put("CUSTOMERDEVICETYPE",customerDeviceType);
			
			var phoneList = $("#"+tabLine+"PHONELIST").val();
			rowData.put("PHONELIST",phoneList);
			
			var customerDeviceList = $("#"+tabLine+"CUSTOMERDEVICELIST").val();
			rowData.put("CUSTOMERDEVICELIST",customerDeviceList);
			
			var customerDeviceVendor = $("#"+tabLine+"CUSTOMERDEVICEVENDOR").val();
			rowData.put("CUSTOMERDEVICEVENDOR",customerDeviceVendor);
			
			var supportMode = $("#"+tabLine+"SUPPORTMODE").val();
			rowData.put("SUPPORTMODE",supportMode);
			
			var AMOUNT = $("#"+tabLine+"AMOUNT").val();
			rowData.put("AMOUNT",AMOUNT);
			
			//如果必填项为空，则算这条专线未修改
			if (bizSecurityLv == "" || portACustom == "" || areaA == ""
					|| countyA == "" || villageA == ""
					|| portAInterFaceType == "" || portAContact == ""
					|| portAContactPhone == "" || tradeName == ""
					|| notinRsrvStr3 == "" || domainName == ""
					|| maindoMainAdd == "" || notinRsrvStr11 == ""
					|| notinRsrvStr12 == "" || isCustomerPe==""||phoneList==""||supportMode=="") {
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","0");
			}else{
				var isModifyTag = $("#"+tabLine+"IS_MODIFY_TAG").val();
				rowData.put("IS_MODIFY_TAG","1");
			}
		}
	}

	return rowData;
}

function doReturnVal() {
	debugger;
	var rowData = lineAddrTable.getCheckedRowsData("ckline");
	var addrStandard = rowData.get(0).get("addrStandard");
	ajaxSubmit(this,'getCoverInfoAddr',"&STANDARD_ADDR="+addrStandard,"LineAddrResultPart",function(data){
		var tag = data.get("IS_COVER_TAG");
		if(tag == "0"){
			if(!sureLineAddrParamList(rowData.get(0))){
				return false;
			}
			hidePopup("popup04");
		}else{
			$.validate.alerter.one(rowData,"该地址不为厚覆盖，不可选为快速开通地址!");
		}
	},null);
}

function doReturnValue() {
	debugger;
	var rowData = lineAddrTable.getCheckedRowsData("ckline");
	var addInfo = rowData.get(0);
	var addrStandard = addInfo.get("addrStandard");
	var provinceA = addInfo.get("PROVINCEA");
	var posId = addInfo.get("DEVICEID");
	var capacityUnused = addInfo.get("CAPACITY_UNUSED");
	if("0"== capacityUnused){
		$.validate.alerter.one(rowData,"该地址的端口数为'0',不可选为快速开通地址，请重新选择!");
		return false;
	}
	var cityA = addInfo.get("CITYA");
	var areaA = addInfo.get("AREAA");
	if("海南省"==provinceA){
		provinceA="海南";
	}
	var cityAa="";
	if("美兰区"==areaA||"龙华区"==areaA||"琼山区"==areaA||"秀英区"==areaA){
		cityAa="海口";
	}
	if("天涯区"==areaA||"吉阳区"==areaA||"海棠区"==areaA||"崖州区"==areaA){
		cityAa="三亚";
	}
	
	if("白沙县"==cityA||"昌江县"==cityA||"乐东县"==cityA||"陵水县"==cityA){
		cityA=areaA+"黎族自治县";
	}
	if("保亭县"==cityA||"琼中县"==cityA){
		cityA=areaA+"黎族苗族自治县";
	}
//	if("儋州"==cityA||"东方"==cityA||"琼海"==cityA||"万宁"==cityA||"文昌"==cityA||"五指山"==cityA||"三沙"==cityA){
//		areaAa=cityA+"市";
//	}
//	if("澄迈"==cityA||"临高"==cityA||"屯昌"==cityA||"定安"==cityA){
//		areaAa=cityA+"县";
//	}
	
	var countyA = addInfo.get("COUNTYA");
	var villageA = addInfo.get("VILLAGEA");

	$("#pattr_QUICKADDRA").val(addrStandard); 
	$("#pattr_PROVINCEA").val(provinceA); 
	
	if(""!=cityAa){
		$("#pattr_CITYA").val(cityAa); 
		$("#pattr_AREAA").val(areaA);
	}else{
		
		$("#pattr_CITYA").val(areaA); 
		$("#pattr_AREAA").val(cityA); 
	}
	$("#pattr_COUNTYA").val(countyA); 
	$("#pattr_VILLAGEA").val(villageA);
	$("#pattr_posID").val(posId);
	hidePopup("popup04");
}


function changeBusinessType(){
	var type = $("#pattr_BUSINESSTYPE").val();
	var isReadonly = $("#IS_READONLY").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	var productId = $("#cond_OFFER_CODE").val();
	if(type=='0'){//项目
		$("#IF_NEW_PROJECTNAME").css("display", "");
		$("#CONTRACTREQUIREDATE").css("display", "");
		$("#pattr_PROJECTNAME").attr("nullable", "no");
		$("#pattr_IF_NEW_PROJECTNAME").attr("nullable", "no");
		$("#pattr_CONTRACTREQUIREDATE").attr("nullable", "no");
		
		if(isReadonly != "true"){
			$("#pattr_IF_NEW_PROJECTNAME").val("");
		}else if($("#pattr_IF_NEW_PROJECTNAME").val()=="1" && isReadonly == "true"){
			$("#pattr_PROJECTNAME").attr("nullable", "yes");
		}
		
	}else if(type=='1'){//零星
		$("#PROJECTNAME").css("display", "none");
		$("#CONTRACTREQUIREDATE").css("display", "none");
		$("#IF_NEW_PROJECTNAME").css("display", "none");
		$("#PROJECTNAME").attr("nullable", "yes");
		$("#CONTRACTREQUIREDATE").attr("nullable", "yes");
		$("#pattr_PROJECTNAME").attr("nullable", "yes");
		$("#pattr_PROJECTNAME").val("");
		$("#pattr_CONTRACTREQUIREDATE").attr("nullable", "yes");
		$("#pattr_CONTRACTREQUIREDATE").val("");
		$("#pattr_IF_NEW_PROJECTNAME").attr("nullable", "yes");
		$("#pattr_IF_NEW_PROJECTNAME").val("");
		$("#PROJECTNAME_OLD").css("display", "none");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "yes");
		
		if(isReadonly != "true"){
			$("#pattr_IF_NEW_PROJECTNAME").val("");
		}
		
	}
	if(templetId=="EDIRECTLINEOPEN" && productId !="7010"){
		var ibsysid = $("#IBSYSID").val();
		var param = "&ajaxListener="+"queryAcceptPerByBusinessType"+"&BUSINESSTYPE="+type+"&IS_READONLY="+isReadonly+"&IBSYSID="+ibsysid;
		$.beginPageLoading("数据加载中......");
		$.ajax.submit(null, "queryAcceptPerByBusinessType", param, "", function(datas){
			$.endPageLoading();
			
			$("#pattr_ACCEPTTANCE_PERIOD").val("");
			
			var html = "";
			var select = $("#pattr_ACCEPTTANCE_PERIOD_float ul:first");
			for(var i =0;i<datas.length;i++){
				var data = datas.get(i);
				var text = data.get("DATA_NAME");
				var value = data.get("DATA_ID");
				var tip = "";
				if(value=="0"){
					tip = "立即计费";
				}else if(value=="1"){
					tip = "下个月1号";
				}else if(value=="2"){
					tip = "下下个月1号";
				}
				html += '<li class="link" idx='+i+' title='+text+' val='+value+'><div class="main" tip='+tip+'>'+text+'</div></li>';
			}
			select.html(html);
			
			window["pattr_ACCEPTTANCE_PERIOD"] = new Wade.Select(
					// 对应元素，el 可以为元素 id，或者原生 dom 对象
					"pattr_ACCEPTTANCE_PERIOD",
					// 参数设置
					{
						value:"",
						inputable:false,
						disabled:false,
						addDefault:true,
						defaultText:"--请选择--",
						selectedIndex:-1,
						optionAlign:"right"
							
					}
			);
			
			if(isReadonly=="true"){
				$("#pattr_ACCEPTTANCE_PERIOD").val($("#hidden_ACCEPTTANCE_PERIOD").val());
				changeAccepttancePeriod();
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
	
}

function saveContractInfo(){
	var ifNewContract = $("#C_IF_NEW_CONTRACT").val();
	var contractDataset = new Wade.DatasetList();
	
	var ifNew = $("#ContractPart input");
	for(var i = 0, size = ifNew.length; i < size; i++)
	{
		var ifNewCode = ifNew[i].id;
		var ifNewValue = $("#"+ifNewCode).val();
		
		var ifNewData = new Wade.DataMap();
		ifNewData.put("ATTR_VALUE", ifNewValue);
		ifNewData.put("ATTR_NAME", ifNew[i].getAttribute("desc"));
		ifNewData.put("ATTR_CODE", ifNewCode);
		
		contractDataset.add(ifNewData);
	}
	
	if(ifNewContract=='0'){
		var newContract = $("#newContractPart input");
		for(var i = 0, size = newContract.length; i < size; i++)
		{
			var newContractCode = newContract[i].id;
			if(newContractCode==''||newContractCode=='CONTRACT_FILE_LIST_name'||newContractCode=='CONTRACT_FILE_LIST'){
				continue;
			}
			var newContractValue = $("#"+newContractCode).val();
			
			if(newContractCode=='C_CONTRACT_NAME_NEW'){
				newContractCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", newContractValue);
			contractData.put("ATTR_NAME", newContract[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", newContractCode);
			
			contractDataset.add(contractData);
		}
	}else if(ifNewContract=='1'){
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#contractContentPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			var contractsValue = $("#"+contractsCode).val();
			
			if(contractsCode=='C_CONTRACT_NAME_OLD'){
				contractsCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", contractsValue);
			contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", contractsCode);
			
			contractDataset.add(contractData);
		}
		var newContract = $("#newContractPart input");
		for(var i = 0, size = newContract.length; i < size; i++)
		{
			var newContractCode = newContract[i].id;
			if(newContractCode==''||newContractCode=='CONTRACT_FILE_LIST_name'||newContractCode=='CONTRACT_FILE_LIST'){
				continue;
			}
			var newContractValue = $("#"+newContractCode).val();
			
			if(newContractCode=='C_CONTRACT_NAME_NEW'){
				newContractCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", newContractValue);
			contractData.put("ATTR_NAME", newContract[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", newContractCode);
			
			contractDataset.add(contractData);
		}
	}else{
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#newContractPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				var contractsValue = $("#"+contractsCode).val();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
				contractData.put("ATTR_CODE", contractsCode);
				
				contractDataset.add(contractData);
			}
			
		}
		var contractLineInfo = $("#contractLineInfoPart span[class=value]");
		var recordNum=0;
		for(var i = 0, size = contractLineInfo.length; i < size; i++)
		{
			var contractsCode = contractLineInfo[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				if(contractsCode=='NOTIN_LINE_NO'){
					recordNum++;
				}
				var contractsValue = $("#"+contractsCode).text();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", "");
				contractData.put("ATTR_CODE", contractsCode);
				contractData.put("RECORD_NUM", recordNum);
				contractDataset.add(contractData);
			}
			
		}
		
	}
	return contractDataset;
}
function saveDirectLineOpenIDCList(newIDCDataset,productId,bpmTempletId){
	$("#IDCDATALIST_PARAM input[type=checkbox]").each(function(){
//		if((this).attr("checked")=='true'){
		
		
			var madifyFlag=false;
			var newList = $(this).closest("li").find("div[id*=new] ul li span[class=value] span");
			for(var i=0;i<newList.length;i++){
				var id=newList[i].id;
				if($("#"+id).text()!=$("#"+id).attr("oldvalue")||$(this).attr("checked")==false){
					var newIdcCustomerData = new Wade.DataMap();
					newIdcCustomerData.put("ATTR_VALUE", $("#"+id).text());
					newIdcCustomerData.put("ATTR_NAME", $("#"+id).attr("desc"));
					newIdcCustomerData.put("ATTR_CODE", $("#"+id).attr("name"));
					newIdcCustomerData.put("RECORD_NUM", $(this).val());
					newIDCDataset.add(newIdcCustomerData);
					madifyFlag=true;
				}
			}
			var newIdcCustomerData = new Wade.DataMap();
			if($(this).attr("checked")==true&&madifyFlag){
				newIdcCustomerData.put("ATTR_VALUE", "2");
				newIdcCustomerData.put("ATTR_NAME", "修改标识");
				newIdcCustomerData.put("ATTR_CODE", "7041_MODIFY_TAG");
				newIdcCustomerData.put("RECORD_NUM", $(this).val());
				newIDCDataset.add(newIdcCustomerData);
			}
			else if($(this).attr("checked")==false){
				newIdcCustomerData.put("ATTR_VALUE", "1");
				newIdcCustomerData.put("ATTR_NAME", "修改标识");
				newIdcCustomerData.put("ATTR_CODE", "7041_MODIFY_TAG");
				newIdcCustomerData.put("RECORD_NUM", $(this).val());
				newIDCDataset.add(newIdcCustomerData);
			}
//		}
		
	});
}
function saveDirectLineOpenIDCtoInfo(newIDCDataset,newIdcCustomerInfo){
	for(var i = 0, size = newIdcCustomerInfo.length; i < size; i++)
	{
		var newIdcCustomerCode = newIdcCustomerInfo[i].id;
		if(newIdcCustomerCode!=null&&newIdcCustomerCode!=''){
			var newIdcCustomerValue = $("#"+newIdcCustomerCode).val();
			var newIdcCustomerData = new Wade.DataMap();
			newIdcCustomerData.put("ATTR_VALUE", newIdcCustomerValue);
			newIdcCustomerData.put("ATTR_NAME", newIdcCustomerInfo[i].getAttribute("desc"));
			newIdcCustomerData.put("ATTR_CODE", newIdcCustomerCode);
			newIdcCustomerData.put("RECORD_NUM", "0");
			newIDCDataset.add(newIdcCustomerData);
		}
	}
	return newIDCDataset;
}
function saveDirectLineOpenIDCtoCustomerInfo(newIDCDataset,newIdcCustomerInfo){
	var flag=false;
	for(var i = 0, size = newIdcCustomerInfo.length; i < size; i++)
	{
		var newIdcCustomerCode = newIdcCustomerInfo[i].id;
		if(newIdcCustomerCode!=null&&newIdcCustomerCode!=''){
			var newIdcCustomerValue = $("#"+newIdcCustomerCode).val();
			var newIdcCustomerOldValue = $("#"+newIdcCustomerCode).attr("oldvalue");
			var newIdcCustomerData = new Wade.DataMap();
			newIdcCustomerData.put("ATTR_VALUE", newIdcCustomerValue);
			newIdcCustomerData.put("ATTR_NAME", newIdcCustomerInfo[i].getAttribute("desc"));
			newIdcCustomerData.put("ATTR_CODE", newIdcCustomerCode);
			newIdcCustomerData.put("RECORD_NUM", "0");
			newIDCDataset.add(newIdcCustomerData);
			if(!flag&&newIdcCustomerValue!=newIdcCustomerOldValue){
				newIdcCustomerData = new Wade.DataMap();
				newIdcCustomerData.put("ATTR_VALUE", "2");
				newIdcCustomerData.put("ATTR_NAME", "客户其他信息修改标识");
				newIdcCustomerData.put("ATTR_CODE","CUST_MODIFYTAG");
				newIdcCustomerData.put("RECORD_NUM", "0");
				newIDCDataset.add(newIdcCustomerData);
				flag=true;
			}
		}
	}
	return newIDCDataset;
}
function saveDirectLineOpenIDC(productId,bpmTempletId){
	var newIDCDataset = new Wade.DatasetList();
	var newIdcCustomerInfo = $("#IdcCustomerInfoPart input");
	newIDCDataset=saveDirectLineOpenIDCtoCustomerInfo(newIDCDataset,newIdcCustomerInfo);
	var newIdcLineInfo = $("#IdcLineInfo input");
	if(newIdcLineInfo!=null){
		newIDCDataset=saveDirectLineOpenIDCtoInfo(newIDCDataset,newIdcLineInfo);
	}
	var newIdcLineInfo = $("#IdcOrderNumInfo input");
	if(newIdcLineInfo!=null){
		newIDCDataset=saveDirectLineOpenIDCtoInfo(newIDCDataset,newIdcLineInfo);
	}
	if($("#IDC_ContractId").val()!=null){
		var newIdcData = new Wade.DataMap();
		newIdcData.put("ATTR_VALUE", $("#IDC_ContractId").val());
		newIdcData.put("ATTR_NAME", "合同编码");
		newIdcData.put("ATTR_CODE", "IDC_ContractId");
		newIdcData.put("RECORD_NUM", "0");
		newIDCDataset.add(newIdcData);
		
	}
//	if(bpmTempletId!='EDIRECTLINECANCELIDC'){
		var newIdcProductInfo7041 = $("#IdcProductInfo"+productId+"Part input");
		if(newIdcProductInfo7041!=null){
			for(var i = 0, size = newIdcProductInfo7041.length; i < size; i++)
			{
				var newIdcCode = newIdcProductInfo7041[i].id;
				if(newIdcCode!=null&&newIdcCode!=''){
					var newIdcValue = $("#"+newIdcCode).val();
					var newIdcData = new Wade.DataMap();
					newIdcData.put("ATTR_VALUE", newIdcValue);
					newIdcData.put("ATTR_NAME", newIdcProductInfo7041[i].getAttribute("desc"));
					newIdcData.put("ATTR_CODE", newIdcCode);
					newIdcData.put("RECORD_NUM", "0");
					newIDCDataset.add(newIdcData);
				}
			}
//		}
		
		
	}
	if(bpmTempletId=='EDIRECTLINEOPENIDC' || bpmTempletId=='EDIRECTLINECHANGEIDC'|| bpmTempletId=='EDIRECTLINECANCELIDC'){
		var newIdcPriceInfo = $("#IdcProductInfoPricePart input");
		for(var i = 0, size = newIdcPriceInfo.length; i < size; i++)
		{
			var newIdcPriceCode = newIdcPriceInfo[i].id;
			var oldVal=$("#"+newIdcPriceCode).attr("oldvalue");
			if(newIdcPriceCode!=null&&newIdcPriceCode!=''){
				var newIdcPriceValue = $("#"+newIdcPriceCode).val();
				var newIdcPriceData = new Wade.DataMap();
				if(bpmTempletId=='EDIRECTLINEOPENIDC'){ //新增一套
					newIdcPriceData.put("ATTR_VALUE", newIdcPriceValue);
					newIdcPriceData.put("ATTR_NAME", newIdcPriceInfo[i].getAttribute("desc"));
					newIdcPriceData.put("ATTR_CODE", newIdcPriceCode);
					newIdcPriceData.put("RECORD_NUM", "0");
					newIDCDataset.add(newIdcPriceData);
				}
				if(bpmTempletId=='EDIRECTLINECHANGEIDC'){ //新增一套，删除一套
					if(oldVal!=null&&oldVal!=""&&oldVal!=newIdcPriceValue){//有资费参数变更
						for(var j = 0, sizej = newIdcPriceInfo.length;j < sizej; j++){
							var newIdcPriceDataj = new Wade.DataMap();
							var newIdcPriceCodej = newIdcPriceInfo[j].id;
							var newIdcPriceValuej = $("#"+newIdcPriceCodej).val();
							var oldValj=$("#"+newIdcPriceCodej).attr("oldvalue");
							newIdcPriceDataj.put("ATTR_VALUE", newIdcPriceValuej);
							newIdcPriceDataj.put("ATTR_NAME", newIdcPriceInfo[j].getAttribute("desc"));
							newIdcPriceDataj.put("ATTR_CODE", newIdcPriceCodej);
							newIdcPriceDataj.put("RECORD_NUM", "0");
							newIDCDataset.add(newIdcPriceDataj);
							newIdcPriceDataj = new Wade.DataMap();
							newIdcPriceDataj.put("ATTR_VALUE", oldValj);
							newIdcPriceDataj.put("ATTR_NAME", newIdcPriceInfo[j].getAttribute("desc"));
							newIdcPriceDataj.put("ATTR_CODE", newIdcPriceCodej+"_OLD");
							newIdcPriceDataj.put("RECORD_NUM", "0");
							newIDCDataset.add(newIdcPriceDataj);
						}
						break;
					}
				}
				if(bpmTempletId=='EDIRECTLINECANCELIDC'){ //删除一套
					if(oldVal!=null&&oldVal!=""){
						newIdcPriceData.put("ATTR_VALUE", oldVal);
						newIdcPriceData.put("ATTR_NAME", newIdcPriceInfo[i].getAttribute("desc"));
						newIdcPriceData.put("ATTR_CODE", newIdcPriceCode+"_OLD");
						newIdcPriceData.put("RECORD_NUM", "0");
						newIDCDataset.add(newIdcPriceData);
					}
				}
				
				
			}
		}
		
	}
	
	
	return newIDCDataset;

}
function saveTapMarketing(bpmTempletId){
	var newTapMarketingDataset = new Wade.DatasetList();
	var newTapMarketing = $("#TapMarketingWorkformPart input");
	for(var i = 0, size = newTapMarketing.length; i < size; i++)
	{
		var newTapMarketingCode = newTapMarketing[i].id;
		if(newTapMarketingCode!=null&&newTapMarketingCode!=''){
			var newTapMarketingValue = $("#"+newTapMarketingCode).val();
			var tapMarketingData = new Wade.DataMap();
			tapMarketingData.put("ATTR_VALUE", newTapMarketingValue);
			tapMarketingData.put("ATTR_NAME", newTapMarketing[i].getAttribute("desc"));
			tapMarketingData.put("ATTR_CODE", newTapMarketingCode);
			newTapMarketingDataset.add(tapMarketingData);
			if(newTapMarketingCode=='C_TAPMARKETING_OPERATION'&&newTapMarketingValue!='0'){
				tapMarketingData = new Wade.DataMap();
				var tapSelect = $("#C_TAPMARKETING_SELECT");
				tapMarketingData.put("ATTR_VALUE", tapSelect.val());
				tapMarketingData.put("ATTR_NAME", tapSelect[0].getAttribute("desc"));
				tapMarketingData.put("ATTR_CODE",  tapSelect[0].id);
				newTapMarketingDataset.add(tapMarketingData);
			}
		}
	}
	if(bpmTempletId=='ETAPMARKETINGENTERINGUPD'||bpmTempletId=='ETAPMARKETINGEXCITATION'){
		var tapMarketingData = new Wade.DataMap();
		var tapSelect = $("#C_TAPMARKETING_SELECT");
		tapMarketingData.put("ATTR_VALUE", tapSelect.val());
		tapMarketingData.put("ATTR_NAME", tapSelect[0].getAttribute("desc"));
		tapMarketingData.put("ATTR_CODE",  tapSelect[0].id);
		newTapMarketingDataset.add(tapMarketingData);
	}
	
	if($("#cond_OFFER_CODE").val()!=''){
		var tapMarketingDataNew = new Wade.DataMap();
		tapMarketingDataNew.put("ATTR_VALUE", $("#cond_OFFER_CODE").val());
		tapMarketingDataNew.put("ATTR_NAME", "专线类型");
		tapMarketingDataNew.put("ATTR_CODE", "C_LINETYPE");
		newTapMarketingDataset.add(tapMarketingDataNew);
	}
	

	return newTapMarketingDataset;
}
//保存专线产品数据
function saveTapMarketingLine(operCode,operType,offerCode,bpmTempletId){
	var tapMarketingLineList = new Wade.DatasetList();
	var mebOfferCode = $("#MEB_OFFER_CODE").val();
	var checkFlag = true;
	var errorTradeId = "";
	if(bpmTempletId!='ETAPMARKETINGEXCITATION'){
		$("#MARKETING_PARAM_UL input[type=radio]").each(function(){
			var value = $(this).closest("li").find("input[id*=OFFER_CHA_"+offerCode+"]").val();
			var offerChaStr = new Wade.DatasetList(value);


			var MebOfferCha  = new Wade.DataMap();
			MebOfferCha.put("OFFER_CODE",mebOfferCode);
			MebOfferCha.put("OFFER_CHA",offerChaStr);
			MebOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
			MebOfferCha.put("OFFER_TYPE","P");
			MebOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
			MebOfferCha.put("OPER_CODE", operCode);
			

			
			tapMarketingLineList.add(MebOfferCha);
		});
	}else{
		$("#MARKETING_PARAM_UL div[id*=new]").each(function(){
			var newTapMarketingDataset = new Wade.DatasetList();
			$("#MARKETING_PARAM_UL div[id*=new] input").each(function(){
				var newTapMarketingValue = $("#"+newTapMarketingCode).val();
				var newTapMarketingCode = $(this)[0].name;

				var newTapMarketingValue = $(this).val();
				var tapMarketingData = new Wade.DataMap();
				tapMarketingData.put("ATTR_VALUE", newTapMarketingValue);
				tapMarketingData.put("ATTR_NAME",  $(this)[0].getAttribute("desc"));
				tapMarketingData.put("ATTR_CODE", newTapMarketingCode);
				newTapMarketingDataset.add(tapMarketingData);
			});
			
			var MebOfferCha  = new Wade.DataMap();
			MebOfferCha.put("OFFER_CODE",mebOfferCode);
			MebOfferCha.put("OFFER_CHA",newTapMarketingDataset);
			MebOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
			MebOfferCha.put("OFFER_TYPE","P");
			MebOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
			MebOfferCha.put("OPER_CODE", operCode);
			

			
			tapMarketingLineList.add(MebOfferCha);
		});

	}
	
	
	return tapMarketingLineList;
}
function savePublicInfo(eomsAttrData){
	var publicInfos = $("#CommonPart input");
	for(var i = 0, size = publicInfos.length; i < size; i++)
	{
		var publicCode = publicInfos[i].id;
		if(!publicCode){
			continue;
		}
		//去掉隐藏的计费方式值
		if(publicCode=="hidden_ACCEPTTANCE_PERIOD"){
			continue;
		}
		
		var publicValue = $("#"+publicCode).val();
		
		var ifNewProjectName = $("#IF_NEW_PROJECTNAME").val();
		if(ifNewProjectName=='1'){
			if(publicCode=='pattr_PROJECTNAME_OLD'){
				publicCode = 'pattr_PROJECTNAME';
			}
		}
		
		var publicInfoData = new Wade.DataMap();
		publicInfoData.put("ATTR_VALUE", publicValue);
		publicInfoData.put("ATTR_NAME", publicInfos[i].getAttribute("desc"));
		publicInfoData.put("ATTR_CODE", publicCode.substring(6));
		
		eomsAttrData.add(publicInfoData);
	}
	return eomsAttrData;
	
}

function saveBusinessInfo(eomsAttrData){
	var businessInfos = $("#businessPart input");
	for(var i = 0, size = businessInfos.length; i < size; i++)
	{
		var businessCode = businessInfos[i].id;
		var businessValue = $("#"+businessCode).val();
		
		var businessInfoData = new Wade.DataMap();
		businessInfoData.put("ATTR_VALUE", businessValue);
		businessInfoData.put("ATTR_NAME", businessInfos[i].getAttribute("desc"));
		businessInfoData.put("ATTR_CODE", businessCode.substring(6));
		
		eomsAttrData.add(businessInfoData);
	}
	return eomsAttrData;
	
}

function saveConfCrmInfo(eomsAttrData){
	var confCrmInfoData = new Wade.DataMap();
	var ifChooseConfCrm = $("#pattr_IF_CHOOSE_CONFCRM").val();
	if(ifChooseConfCrm!=""){
		confCrmInfoData.put("ATTR_VALUE", ifChooseConfCrm);
		confCrmInfoData.put("ATTR_NAME", "是否选择勘察单号");
		confCrmInfoData.put("ATTR_CODE", "IF_CHOOSE_CONFCRM");
		eomsAttrData.add(confCrmInfoData)
	}
	return eomsAttrData;
}

function saveOrderInfo(eomsAttrData){
	var orderInfos = $("#OrderPart input");
	for(var i = 0, size = orderInfos.length; i < size; i++)
	{
		var orderCode = orderInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		
		var orderInfoData = new Wade.DataMap();
		orderInfoData.put("ATTR_VALUE", orderValue);
		orderInfoData.put("ATTR_NAME", orderInfos[i].getAttribute("desc"));
		orderInfoData.put("ATTR_CODE", orderCode.substring(6));
		
		eomsAttrData.add(orderInfoData);
	}
	return eomsAttrData;
}

function saveCustInfo(eomsAttrData){
	var custInfoData1 = new Wade.DataMap();
	custInfoData1.put("ATTR_VALUE", $("#cond_CUST_NAME").val());
	custInfoData1.put("ATTR_NAME", "客户名称");
	custInfoData1.put("ATTR_CODE", "CUSTOMNAME");
	eomsAttrData.add(custInfoData1);
	
	var custInfoData2 = new Wade.DataMap();
	custInfoData2.put("ATTR_VALUE", $("#cond_GROUP_ID").val());
	custInfoData2.put("ATTR_NAME", "集团编码");
	custInfoData2.put("ATTR_CODE", "CUSTOMNO");
	eomsAttrData.add(custInfoData2);
	
	return eomsAttrData;
}

function endDate(){
	debugger;
	var date = new Date();
	var sign1 = "-";
	var sign2 = ":";
	var year = date.getFullYear();//年
	var month = date.getMonth()+1;//月
	var day = date.getDate();//日
	var hour = date.getHours();//时
	var minutes = date.getMinutes();//分
	var second = date.getSeconds();//秒
	if(month>=1 && month<=9){
		month = "0"+month;
	}
	if(day>=0 && day<=9){
		day = "0"+day;
	}
	if(hour>=0 && hour<=9){
		hour = "0"+hour;
	}
	if(minutes>=0 && minutes<=9){
		minutes = "0"+minutes;
	}
	if(second>=0 && second<=9){
		second = "0"+second;
	}
	var currentdate = year + sign1 + month + sign1 + day + " " + hour + sign2 + minutes + sign2 + second;
	
	var contractStartDate = $("#C_CONTRACT_START_DATE").val();//合同起始日期
	var contractEndDate = $("#C_CONTRACT_END_DATE").val();//合同有效终止日期
	var productStartDate = $("#C_PRODUCT_START_DATE").val();//产品有效起始日期
	var productEndDate = $("#C_PRODUCT_END_DATE").val();//产品有效终止日期
	var writreDate = $("#C_CONTRACT_WRITE_DATE").val();//合同签订日期
	var contractfilelist = $("#CONTRACT_FILE_LIST").val();//合同附件
	
	
	if(contractEndDate < currentdate){
        $.validate.alerter.one($("#C_CONTRACT_END_DATE")[0], "合同有效终止日期必须大于当前时间！");
        return false;
	}
	if(contractEndDate < contractStartDate){
        $.validate.alerter.one($("#C_CONTRACT_END_DATE")[0], "合同有效终止日期必须大于合同起始日期！");
        return false;
	}
	if(productEndDate < currentdate){
        $.validate.alerter.one($("#C_PRODUCT_END_DATE_NEW")[0], "产品有效终止日期必须大于当前时间！");
        return false;
	}
	if(productEndDate < productStartDate){
        $.validate.alerter.one($("#C_PRODUCT_END_DATE_NEW")[0], "产品有效终止日期必须大于产品起始日期！");
        return false;
	}
	if(writreDate > contractStartDate){
        $.validate.alerter.one($("#C_CONTRACT_WRITE_DATE")[0], "合同签订日期不能大于合同有效起始日期！");
        return false;
	}
	if(productEndDate > contractEndDate){
        $.validate.alerter.one($("#C_PRODUCT_END_DATE")[0], "产品有效终止日期不能大于合同有效终止日期！");
        return false;
	}
	if(productStartDate < contractStartDate){
        $.validate.alerter.one($("#C_PRODUCT_START_DATE")[0], "产品有效起始日期不能小于合同有效起始日期！");
        return false;
	}
	if(""==contractfilelist||undefined==contractfilelist||null==contractfilelist){
		$.validate.alerter.one($("#CONTRACT_FILE_LIST")[0], "合同附件未上传，请上传附件！");
        return false;
	}
	
	 return true;
}

function historyData(){
	showPopup("popup05", "queryHisDataPopupItem", true);
}

function dataLineName(data){
	debugger;
	$.endPageLoading();
	//var nameExists =  data.get("NAME_EXISTS");
	var nameExists="TRUE";
	if(nameExists=='TRUE'){
		//$.validate.alerter.one($("#pattr_TRADENAME")[0], "验证成功！");
		var chaSpecObjs = $("#offerChaPopupItem input");
		var brand = $("#cond_BRAND_CODE").val();
		var offerChaSpecDataset = new Wade.DatasetList();

		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			var chaValue = "";
			if("BOSG"==brand&&isBbossNull(chaSpecCode))
			{
				
				if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
				{
					continue;
				}
			}
			else if (!chaSpecCode)
			{
				if(chaSpecObjs[i].type != "checkbox" && chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
				{
					continue;
				}
			}
			if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
			{
				chaValue = chaSpecObjs[i].checked ? 1 : 0;
				chaSpecCode = chaSpecObjs[i].name;
			}
			else
			{
				chaValue = $("#"+chaSpecCode).val();
			}
			
			var offerChaSpecData = new Wade.DataMap();
			offerChaSpecData.put("ATTR_VALUE", chaValue);
			offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
			offerChaSpecData.put("ATTR_CODE", chaSpecCode);
			
//			if("BOSG"==brand)
//			{
//				var elementId = chaSpecObjs[i].getAttribute("element_id");
//				if(!elementId)
//				{
//					continue;
//				}
//				offerChaSpecData.put("ATTR_CODE", elementId);
//			}
			offerChaSpecDataset.add(offerChaSpecData);
		}
		var offerChaHiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
		$("#"+offerChaHiddenId).val(offerChaSpecDataset);
		
		$("#"+offerChaHiddenId).closest("li").find(".side").html("<span class='e_tag e_tag-green'>已设置</span>");
	    //设置默认勾选
		
		hidePopup(document.getElementById("offerChaPopupItem"));
		submitOfferChaAfterAction(offerChaSpecDataset);
		
		var offerchalist = new Wade.DatasetList($("#offerchalist").val());
		offerchalist.add(offerChaSpecDataset);
		$("#offerchalist").val(offerchalist);
//		DataLineList = offerchalist;
		return true;
	}else{   
		 $.validate.alerter.one($("#pattr_TRADENAME")[0], "专线名称已存在！");
		 $("#pattr_TRADENAME").val("");
	     return false;
	}
}

function builderDirectLineData(chaSpecObjs){
	debugger;
	var templetId = $("#TEMPLET_ID").val();
	
	var chks = $("#DATALINE_PARAM_UL [type=checkbox]");
	{
		var tempDataLine = new Wade.DatasetList();
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var offerChaSpecDataset = new Wade.DatasetList();
				var idNum = chks[j].id.substring(9);
				var temp = chaSpecObjs[idNum];
				var productNo;
				var isStopFlag=false;
				for (var i = 0, size = temp.length; i < size; i++) {
					if(temp[i].get("ATTR_CODE")=='NOTIN_LINE_NO'){
						productNo = temp[i].get("ATTR_VALUE");
					}
					if(temp[i].get("ATTR_CODE")=="PRODUCTNO"){
						productNo = temp[i].get("ATTR_VALUE");
					}
					//审核不通过，回到申请界面时不不校验
					var isReadOnly = $("#IS_READONLY").val();
					//开通选择勘察单、变更选择勘察单、开通单批量导入提交时判断专线信息是否进行修改
					if(temp[i].get("ATTR_CODE")=='IS_MODIFY_TAG'&&isReadOnly!="true"){
						var attrValue = temp[i].get("ATTR_VALUE");
						if(attrValue=='0'){//IS_MODIFY_TAG为0是未修改
							MessageBox.alert("提示信息", "【"+productNo+"】专线信息不全，请先将专线信息填写完全！");
							return false;
						}
					}
					var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
					var bpmTempletId = node.get("BPM_TEMPLET_ID");
					if(temp[i].get("ATTR_CODE")=='ISSTOP'&&temp[i].get("ATTR_VALUE")=="1"&&bpmTempletId == "EDIRECTLINECANCELPBOSS"){
						isStopFlag=true;//注销停机状态拦截
					}
					
					var offerChaSpecData = new Wade.DataMap();
					offerChaSpecData.put("ATTR_VALUE", temp[i].get("ATTR_VALUE"));
					offerChaSpecData.put("ATTR_NAME", temp[i].get("ATTR_NAME",""));
					offerChaSpecData.put("ATTR_CODE", temp[i].get("ATTR_CODE"));
					offerChaSpecDataset.add(offerChaSpecData);
				}
				if(isStopFlag){
					MessageBox.alert("提示信息", "【"+productNo+"】该专线为已停机状态，请先复机后再执行拆机操作！");
					return false;	
				}
				
				tempDataLine.add(offerChaSpecDataset);
			}
		}
	}
	return tempDataLine;
}

function builderSimpleChangeDirectLineData(){
	var offerCode = $("#cond_OFFER_CODE").val();
	var chks = $("#DATALINE_PARAM_UL [type=checkbox]");
	{
		var tempDataLine = new Wade.DatasetList();
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var offerChaSpecDataset = new Wade.DatasetList();
				var idNum = chks[j].id.substring(9);
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + idNum;
				var temp = new Wade.DatasetList($("#"+offerChaHiddenId).val());
				for (var i = 0, size = temp.length; i < size; i++) {
					var offerChaSpecData = new Wade.DataMap();
					var attrCode = temp[i].get("ATTR_CODE");
					var value = temp[i].get("ATTR_VALUE");
					if (attrCode == "NOTIN_RSRV_STR6"||attrCode == "NOTIN_RSRV_STR7"||attrCode == "NOTIN_RSRV_STR8") {
						if(value.indexOf("%")>=0){
							value = value.substring(0,value.length-1);
						}
					}
					
					offerChaSpecData.put("ATTR_VALUE", value);
					offerChaSpecData.put("ATTR_NAME", temp[i].get("ATTR_NAME",""));
					if(attrCode.indexOf("pattr_")==0){
						attrCode = attrCode.substring(6);
					}
					offerChaSpecData.put("ATTR_CODE",attrCode);
					offerChaSpecDataset.add(offerChaSpecData);
				}
				tempDataLine.add(offerChaSpecDataset);
			}
		}
	}
	return tempDataLine;
}

function speAuditFormQuery(){
	var staffName = $("#cond_SpeStaffName").val();
	if(!staffName){
		MessageBox.alert("提示","请输入员工姓名！");
		return false;
	}
	ajaxSubmit("",'qryStaffinfoByName','&STAFF_NAME='+staffName,'auditParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function showAuditStaff(){
	ajaxSubmit("",'qryStaffinfo',null,'auditParts',function(data){
		showPopup("popup08", "auditPopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function showSpeAuditStaff(){
	ajaxSubmit("",'qrySpeStaffinfo',null,'speAuditParts',function(data){
		showPopup("popup10", "speAuditPopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function setSpeAuditReturnValue(el){
	var staffId = $(el).attr("staff_id");
    var staffPhone =  $(el).attr("staff_phone");
	$("#pattr_SPECIAL_STAFF_ID").val(staffId);
	$("#pattr_SPECIAL_STAFF_PHONE").val(staffPhone);
	backPopup("popup10", "speAuditPopupItem", true);
}

function setReturnValue(el){
	var staffId = $(el).attr("staff_id");
    var staffPhone =  $(el).attr("staff_phone");
	$("#pattr_AUDITSTAFF").val(staffId);
	$("#pattr_AUDITPHONE").val(staffPhone);
	backPopup("popup08", "auditPopupItem", true);
}

function isAutoRenew(){
	debugger;
	var isAutiRenew = $('#C_CONTRACT_IS_AUTO_RENEW').val();
	if(isAutiRenew=='1'){
		$('#C_RENEW_END_DATE').attr('nullable', 'no');
		$('#li_C_RENEW_END_DATE').addClass('required');
	}else if(isAutiRenew == '0'){
		$('#C_RENEW_END_DATE').attr('nullable', 'yes');
		$('#li_C_RENEW_END_DATE').removeClass('required');
	}
}

function saveGroupData(eomsAttrData) {
	debugger;
	var chaSpecObjs = $("#groupBasePart input");

	for (var i = 0, size = chaSpecObjs.length; i < size; i++) {
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if (chaSpecObjs[i].type == "checkbox" || chaSpecObjs[i].type == "radio") {
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		} else {
			chaValue = $("#" + chaSpecCode).val();
		}

		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("ATTR_CODE", chaSpecCode.substring(5));

		eomsAttrData.add(offerChaSpecData);
	}
	var input = new Wade.DataMap();
	var productName = $("#cond_OFFER_NAME").val();
	input.put("ATTR_VALUE",productName);
	input.put("ATTR_NAME","业务类型（原产品名称）");
	input.put("ATTR_CODE","PRODUCTNAME");
	eomsAttrData.add(input);
	
	return eomsAttrData;
}

function getConfCrmInfo(){
	debugger;
	var confCrmInfo = $("#pattr_CONFCRMTICKETNO").val(); 
	var temp = confCrmInfo.split("【");
	var ibsysid = parseInt(temp[1]);
	$("#CONFIBSYSID").val(ibsysid); 
	var productId = $("#cond_OFFER_CODE").val();
	var groupId = $("#cond_GROUP_ID").val();
	var templetId = $("#TEMPLET_ID").val();
	var bizrange = $("#pattr_BIZRANGE").val();
	var userId = $("#apply_USER_ID").val();
	var changeNode = $("#pattr_CHANGEMODE").val();
	
	if(productId=="7011"||productId=="7012"||productId=="7016"
		||productId=="70111"||productId=="70112"||productId=="70121"||productId=="70122"){
		if(templetId=="EDIRECTLINEOPEN"){
			var bpmTempletId = "ERESOURCECONFIRMZHZG";
		}
		if(templetId=="EDIRECTLINECHANGE"){
			var bpmTempletId = "ECHANGERESOURCECONFIRM";
		}
	}else if (productId=="7010"){
		if(templetId=="EDIRECTLINEOPEN"){
			var bpmTempletId = "ERESOURCECONFIRMZHZG";
		}
		if(templetId=="EDIRECTLINECHANGE"){
			var bpmTempletId = "EVIOPDIRECTLINECHANGEPBOSS";
		}
	}
	var param = "&IBSYSID="+ibsysid+"&PRODUCT_ID="+productId+"&GROUP_ID="+groupId+"&BPM_TEMPLET_ID="+bpmTempletId+"&BIZRANGE="+bizrange+"&EC_USER_ID="+userId+"&CHANGE_MODE="+changeNode;
	
	$.beginPageLoading("正在处理...");
	ajaxSubmit(this,'getConfCrmInfo',param ,"confCrmPopupItem",function(data){
		$.endPageLoading();
		$("#CONFCRMTICKETNO").val(data.get(0).get("SERIALNO"));
		$("#pattr_CONFCRMTICKETNO").val(confCrmInfo);
		$("#pattr_BUILDINGSECTION").val(data.get(0).get("BUILDINGSECTION"));
//		$("#pattr_TITLE").val(data.get(0).get("TITLE"));
		$("#pattr_URGENCY_LEVEL").val(data.get(0).get("URGENCY_LEVEL"));
//		$("#cond_CITYNAME").val(data.get(0).get("CITYNAME"));
//		$("#cond_COUNTYNAME").val(data.get(0).get("COUNTYNAME"));
		$("#pattr_BIZRANGE").val(data.get(0).get("BIZRANGE"));
		$("#pattr_SERVICETYPE").val(data.get(0).get("SERVICETYPE"));
		changeServicetype();
		
		$("#confCrmExport").beforeAction(function(e){
			var rowDatas = myTable.getCheckedRowsData("LINENO");
			if(rowDatas==null||rowDatas==""){
				$.validate.alerter.one(rowDatas,"请先选择需要导出的专线!");
				return false;
			}
			var rowDatas2 = new Wade.DatasetList();
			for (var i = 0; i < rowDatas.length; i++) {
				var rowData = tableDataLineInfos(rowDatas,i);
				rowDatas2.add(rowData);
			}
			$("#DATALINENOS").val(rowDatas2);
		});
		
		$("#importFile").beforeAction(function(e){
			if(changeNode==""){
				MessageBox.alert("提示", "请先选择业务调整场景！");
				return false;
			}
			return confirm('是否导入?');
		});
		
		$("#importFile").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','importFile','&PRODUCT_ID='+productId+'&BPM_TEMPLET_ID='+bpmTempletId,'QryResultPart',function(data){
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
						//将此专线设置为已修改
						$("#IS_MODIFY_TAG").val(1);//1为已修改
		    		});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});

}

function showContractLines(el){
	
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_OFFER_CODE").val();
	var templetId = $("#TEMPLET_ID").val();
	
	var param = "&GROUP_ID="+groupId+"&PRODUCT_ID="+productId+"&TEMPLET_ID="+templetId;
	
	ajaxSubmit("",'initContractLines',param,'datalinePopupItem',function(data){
		showPopup("popupDataline", "datalinePopupItem", true);
		
		$("#importFile").beforeAction(function(e){
			return confirm('是否导入?');
		});
		
		$("#importFile").afterAction(function(e, status){
			debugger;
			if('ok' == status){
				$.beginPageLoading("正在处理...");
				ajaxSubmit('','importFile','&PRODUCT_ID='+productId,'queryLines',function(data){
					$.endPageLoading();
					MessageBox.success("导入成功！", "页面展示导入数据!",function(){
						flag="true";
		    		});
				},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}		
		});
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}



function queryProductLines() {
	debugger;
	if(!$.validate.verifyAll("CondPart")){
		return false;
	}
	var productNO = $("#cond_PRODUCT_NO").val();
	var productID = $("#cond_OFFER_CODE").val();
	var groupId = $("#cond_GROUP_ID").val();
        $.beginPageLoading("数据提交中，请稍后...");
    	$.ajax.submit("", "queryProductLines", "&PRODUCTNO="+productNO+"&GROUP_ID="+groupId+"&PRODUCT_ID="+productID, "queryLines", function(data){
    		$.endPageLoading();

    	}, 
    	function(error_code,error_info,derror){
    		$.endPageLoading();
    		showDetailErrorInfo(error_code,error_info,derror);
    	});    
}

function saveConfCrm(){
	debugger;
	var temp = new Wade.DatasetList();
	var groupId = $("#cond_GROUP_ID").val();
	var rowDatas = LineTable.getCheckedRowsData("LINENOS");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选专线信息!");
		return false;
	}
	
//	$("#batchAdd").css("display", "none");
//	$("#add").css("display", "none");
//	$("#delete").css("display", "block");
//	
	var param = "&LINES="+rowDatas+"&GROUP_ID="+groupId+"&PRODUCT_ID="+$("#cond_OFFER_CODE").val();
	
	
	//将主页面上已存在的数据删除
	$("#DATALINE_PARAM_UL").children().remove();
	
	var offerchaList = new Wade.DatasetList($("#offerchalist").val());
	for (var i = 0; i < offerchaList.length; i++) {
		var offercha = offerchaList[i];
		temp.add(offercha);
	}
	
   ajaxSubmit("",'getAddLineInfoMarketing',param,'',function(data){
	   for(var i=0;i<rowDatas.length;i++){
			var rowData = rowDatas.get(i);
			var lineNo = rowData.get("NOTIN_LINE_NO");
			
			var param;
			param = "&DATA="+rowData;
			sureContracLineParamList(rowData);
			
			var batchImports = new Wade.DatasetList();
			var key = rowData.keys;
			for (var m = 0; m < key.length; m++) {
				var value = rowData.get(m);
				var batchImport = new Wade.DataMap();
				batchImport.put("ATTR_CODE", key[m]);
				batchImport.put("ATTR_VALUE", value);
				batchImports.add(batchImport);
			}
			temp.add(batchImports);
			$("#offerchalist").val(temp);
		}
	  
//		$("#batchAdd").css("display", "none");
//		$("#add").css("display", "none");
//		$("#delete").css("display", "block");
		
		hidePopup("popupDataline");
	   
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function saveMarkeDate(eomsAttrData){
	debugger;
	var chaSpecObjs = $("#MarketPart input");

	for (var i = 0, size = chaSpecObjs.length; i < size; i++) {
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if (chaSpecObjs[i].type == "checkbox" || chaSpecObjs[i].type == "radio") {
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		} else {
			chaValue = $("#" + chaSpecCode).val();
		}

		var MarkeData = new Wade.DataMap();
		MarkeData.put("ATTR_VALUE", chaValue);
		MarkeData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		MarkeData.put("ATTR_CODE", chaSpecCode.substring(6));

		eomsAttrData.add(MarkeData);
	}
	
	var marketList = $("#MarketPart1 input");

	for (var i = 0, size = marketList.length; i < size; i++) {
		var chaSpecCode = marketList[i].id;
		var chaValue = "";
		if (marketList[i].type == "checkbox" || marketList[i].type == "radio") {
			chaValue = marketList[i].checked ? 1 : 0;
			chaSpecCode = marketList[i].name;
		} else {
			chaValue = $("#" + chaSpecCode).val();
		}

		var MarkeData1 = new Wade.DataMap();
		MarkeData1.put("ATTR_VALUE", chaValue);
		MarkeData1.put("ATTR_NAME", marketList[i].getAttribute("desc"));
		MarkeData1.put("ATTR_CODE", chaSpecCode.substring(6));

		eomsAttrData.add(MarkeData1);
	}
	
	return eomsAttrData;
	
}
//回写参数列表
function sureContracLineParamList(rowData)
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var check_tag = $("#isCheckTag").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var lineNo = rowData.get("NOTIN_LINE_NO","");
	var bandWidth = rowData.get("NOTIN_RSRV_STR1","");
	var notinPaybackPreiod = rowData.get("NOTIN_PAYBACK_PERIOD","");
	var notinCost = rowData.get("NOTIN_COST","");
	var notinTerm = rowData.get("NOTIN_TERM","");
	var notinHunderd = rowData.get("NOTIN_HUNDRED","");
	
	//$("#DATALINE_PARAM_UL").children().remove();
	if(templetId == "DIRECTLINEMARKETINGADD"){
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'/></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线实例号：</span><span class='value'>"+lineNo+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>协议期限(年)：</span><span class='value'></span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>预计资费(元/月)：</span><span class='value'></span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>回收期(年)：</span><span class='value'></span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>百元值：</span><span class='value'></span>";
		liHtml += "</li>";
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyContracLineParam("+rowData+");'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
		maxIndex++;
	}
	
	if(templetId == "DIRECTLINEMARKETINGUPD"){
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'/></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线实例号：</span><span class='value'>"+lineNo+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>协议期限(年)：</span><span class='value'>"+notinTerm+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>预计资费(元/月)：</span><span class='value'>"+notinCost+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>回收期(年)：</span><span class='value'>"+notinPaybackPreiod+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>百元值：</span><span class='value'>"+notinHunderd+"</span>";
		liHtml += "</li>";
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyContracLineParam("+rowData+");'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
		maxIndex++;
	}
	
	$("#DATALINE_PARAM_UL").append(liHtml);
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	
	$("#DATALINE_MAX_INDEX").val(maxIndex);
	$("#check_tag").val(check_tag);
	
}

function modifyContracLineParam(obj){
	debugger;
	var offerListNum = $("#DATALINE_PARAM_UL").length;
    if (offerListNum != 0) {
        if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
            MessageBox.alert("提示", "请勾选需要变更的信息进行修改！");
            return false;
        }else if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length > 1) {
            MessageBox.alert("提示", "每次修改只能勾选一条信息！");
            return false;
        }
    }
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex + "_confCrm";
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); 
	var rowData = new Wade.DataMap(obj);
	var productNo = rowData.get("NOTIN_LINE_NO","");
	var brandcode = rowData.get("NOTIN_RSRV_STR1","");
	var notinPaybackPreiod = rowData.get("NOTIN_PAYBACK_PERIOD","");
	var notinCost = rowData.get("NOTIN_COST","");
	var notinTerm = rowData.get("NOTIN_TERM","");
	var notinHunderd = rowData.get("NOTIN_HUNDRED","");
	
	param = "&NOTIN_HUNDRED="+notinHunderd+"&NOTIN_TERM="+notinTerm+"&NOTIN_COST="+notinCost+"&NOTIN_PAYBACK_PERIOD="+notinPaybackPreiod+"&NOTIN_RSRV_STR1="+brandcode+"&PRODUCT_NO="+productNo+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&NODELIST="+nodeTemplete+"&OFFER_CHA_HIDDEN_ID="+offerChaHiddenId;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		showPopup("popup02", "offerChaPopupItem", true);
		$("#NOTIN_LINE_NO").val(rowData.get("NOTIN_LINE_NO"));
		$("#NOTIN_RSRV_STR1").val(rowData.get("NOTIN_RSRV_STR1"));
		$("#NOTIN_RSRV_STR2").val(rowData.get("NOTIN_RSRV_STR2"));
		$("#NOTIN_RSRV_STR3").val(rowData.get("NOTIN_RSRV_STR3"));
//		$("#pattr_NOTIN_RSRV_STR4").val(rowData.get("NOTIN_RSRV_STR4"));
		$("#NOTIN_RSRV_STR9").val(rowData.get("NOTIN_LINE_NO"));
		if(offerCode=='7011'||offerCode=='70111'||offerCode=='70112'){
			$("#NOTIN_RSRV_STR10").val(rowData.get("NOTIN_RSRV_STR10"));
			$("#NOTIN_RSRV_STR11").val(rowData.get("NOTIN_RSRV_STR11"));
			$("#NOTIN_RSRV_STR12").val(rowData.get("NOTIN_RSRV_STR12"));
			$("#pattr_TRANSFERMODE").val(rowData.get("TRANSFERMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_CITYA").val(rowData.get("CITYA"));
			$("#pattr_AREAA").val(rowData.get("AREAA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_IPTYPE").val(rowData.get("IPTYPE"));
			$("#pattr_CUSAPPSERVIPADDNUM").val(rowData.get("CUSAPPSERVIPADDNUM"));
			$("#pattr_CUSAPPSERVIPV4ADDNUM").val(rowData.get("CUSAPPSERVIPV4ADDNUM"));
			$("#pattr_CUSAPPSERVIPV6ADDNUM").val(rowData.get("CUSAPPSERVIPV6ADDNUM"));
			$("#pattr_DOMAINNAME").val(rowData.get("DOMAINNAME"));
			$("#pattr_MAINDOMAINADD").val(rowData.get("MAINDOMAINADD"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
		if(offerCode=='7012'||offerCode=='70121'||offerCode=='70122'){
//			$("#NOTIN_RSRV_STR6").val(rowData.get("NOTIN_RSRV_STR6"));
			$("#NOTIN_RSRV_STR7").val(rowData.get("NOTIN_RSRV_STR7"));
			$("#NOTIN_RSRV_STR8").val(rowData.get("NOTIN_RSRV_STR8"));
			$("#NOTIN_RSRV_STR11").val(rowData.get("NOTIN_RSRV_STR11"));
			$("#NOTIN_RSRV_STR12").val(rowData.get("NOTIN_RSRV_STR12"));
			$("#NOTIN_RSRV_STR16").val(rowData.get("NOTIN_RSRV_STR16"));
			$("#pattr_ROUTEMODE").val(rowData.get("ROUTEMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_PORTZCUSTOM").val(rowData.get("PORTZCUSTOM"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_PORTZCUSTOM").val(rowData.get("PORTZCUSTOM"));
			$("#pattr_PROVINCEZ").val(rowData.get("PROVINCEZ"));
			$("#pattr_COUNTYZ").val(rowData.get("COUNTYZ"));
			$("#pattr_VILLAGEZ").val(rowData.get("VILLAGEZ"));
			$("#pattr_PORTZINTERFACETYPE").val(rowData.get("PORTZINTERFACETYPE"));
			$("#pattr_PORTZCONTACT").val(rowData.get("PORTZCONTACT"));
			$("#pattr_PORTZCONTACTPHONE").val(rowData.get("PORTZCONTACTPHONE"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

function changeOperType400(){
	var offerName = $("#cond_OFFER_NAME").val();
	$("#cond_OFFER_CODE").val("9983");
	
}


function auditFormQuery(){
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("auditForm",'qryStaffinfo',null,'auditParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function changeAreaByCity2()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("cond_CITY");
	if (cityField.value == '海口')	{
		cond_AREA.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			cond_AREA.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		cond_AREA.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			cond_AREA.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		cond_AREA.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				cond_AREA.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};

function changeServicetype(){
	var bizrange = $("#pattr_BIZRANGE").val();
	$("#pattr_CHANGE_DATALINE_NUM").val("0");
	if(bizrange=='本地市'){
		$("#pattr_SERVICETYPE").val('0');
		$("#pattr_DIRECTLINE_SCOPE").val(1);//发报文的隐藏字段
	}else if(bizrange=='省内跨地市'||bizrange=='跨省'){
		$("#pattr_SERVICETYPE").val('1');
		$("#pattr_DIRECTLINE_SCOPE").val(2);//发报文的隐藏字段
	}
	var templetId = $("#cond_TEMPLET_ID").val();
	if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		changeChangeMode();
	}
	defaultValueForBuildSetion();
}

function cancelChangeServicetype(){
	debugger;
	$("#pattr_CHANGE_DATALINE_NUM").val("0");
	var serviceType = $("#pattr_SERVICETYPE").val();
	if(serviceType=='0'){
		$("#pattr_DIRECTLINE_SCOPE").val(1);//发报文的隐藏字段
	}else if(serviceType=='1'){
		$("#pattr_DIRECTLINE_SCOPE").val(2);//发报文的隐藏字段
	}
	changeChangeMode();
}

function initMarkInfo(){
	var IBSYSID_MARKETING = $("#pattr_C_MARKETING_SELECT").val();
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_OFFER_CODE").val();
	var templetId = $("#TEMPLET_ID").val();
//	var offerchalist = new Wade.DatasetList($("#offerchalist").val());
	
	$("#DATALINE_PARAM_UL").children().remove();
	
	var param = "&IBSYSID_MARKETING="+IBSYSID_MARKETING+"&GROUP_ID="+groupId+"&PRODUCT_ID="+productId+"&TEMPLET_ID="+templetId;
	
	ajaxSubmit("",'initMarketing',param,'',function(data){
//		$("#offerchalist").val(offerchalist);
		refreshForAutoData(data.get("COMINFO"),"MarketPart");
		var rowDatas = data.get("DATALINEINFO");
		var temp = new Wade.DatasetList();
		
		var offerchaList = new Wade.DatasetList($("#offerchalist").val());
		var temp = new Wade.DatasetList();
		for (var j = 0; j < offerchaList.length; j++) {
			var offercha = offerchaList[j];
			temp.add(offercha);
		}
		
		for(var i=0;i<rowDatas.length;i++){
			var rowData = rowDatas.get(i);
			
			sureContracLineParamList(rowData);
			var batchImports = new Wade.DatasetList();
			var key = rowData.keys;
			for (var m = 0; m < key.length; m++) {
				var value = rowData.get(m);
				var batchImport = new Wade.DataMap();
				batchImport.put("ATTR_CODE", key[m]);
				batchImport.put("ATTR_VALUE", value);
				batchImports.add(batchImport);
			}
			temp.add(batchImports);
		}
		$("#offerchalist").val(temp);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function changemonth(){
	var issucc = $("#pattr_C_MARKETING_IS_SUCC").val();
	var month = $("#pattr_C_MARKETING_MONTH").val();
	
	if( issucc == '0' && month >3 ){
		MessageBox.error("错误提示","不能促成签约时,试用月份不可超过3个月！");
		$("#pattr_C_MARKETING_IS_SUCC").val("");
		$("#pattr_C_MARKETING_MONTH").val("");
		return false;
	}
	
}

function EDirectLineChangeMode(){
	//将主页面上已存在的数据删除
//	$("#DATALINE_PARAM_UL").children().remove();
	$("#IS_MODIFY_TAG").val(0);//0为未修改
}

function changeAccepttancePeriod(){
	var accepttancePeriod = $("#pattr_ACCEPTTANCE_PERIOD").val();
	if(accepttancePeriod=='2'){
		$("#SPEAUDITFILE").css("display", "");
		$("#SPEAUDITFILE").attr("nullable", "no");
		$("#speauditfile").afterAction(function(e, file){
			var data = new Wade.DataMap();
			
			if(containSpecial(file.name)){
				MessageBox.alert("错误", "【"+file.name+"】文件名称包含特殊字符，请修改后再上传！");
				$("#SPEAUDITFILE").val("");
				return false; 
			}
			
			data.put("FILE_ID", file.fileId);
			data.put("FILE_NAME", file.name);
			data.put("ATTACH_TYPE", "T");
			$("#speauditfileDate").text(data.toString());
			//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
		});
	}else{
		$("#SPEAUDITFILE").css("display", "none");
		$("#SPEAUDITFILE").attr("nullable", "yes");
	}
	
}

function ifNewProjectName(){
	debugger;
	var ifNew =  $("#pattr_IF_NEW_PROJECTNAME").val();
	
	var groupId = $("#cond_GROUP_ID").val();
	var custId = $("#cond_CUST_ID").val();

	if(ifNew=='0'){//新增
		$("#PROJECTNAME").css("display", "");
		$("#pattr_PROJECTNAME").attr("nullable", "no");
		$("#PROJECTNAME_OLD").css("display", "none");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "yes");
		$("#pattr_PROJECTNAME_OLD").val("");
	}else if(ifNew=='1'){//已有
		$("#PROJECTNAME").css("display", "none");
		$("#pattr_PROJECTNAME").attr("nullable", "yes");
		$("#pattr_PROJECTNAME").val("");
		$("#PROJECTNAME_OLD").css("display", "");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "no");
	}
}

function queryHisConfCrm(){
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_OFFER_CODE").val();
	var ibsysid = $("#cond_IBSYSID").val();
	var title = $("#cond_TITLE").val();
	var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var bpmTempletId = node.get("BPM_TEMPLET_ID");
	
	var param = "&GROUP_ID="+groupId+"&PRODUCT_ID="+productId+"&IBSYSID="+ibsysid+"&TITLE="+title+"&BPM_TEMPLET_ID="+bpmTempletId;
	$.beginPageLoading("正在查询...");
	ajaxSubmit('queryHisDataForm','queryHisConfCrm',param,"HisConfCrmResultPart",function(data){
		$.endPageLoading();
	},null);
}

function sureHisConfCrm(obj){
	var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var bpmTempletId = node.get("BPM_TEMPLET_ID");
	var ibsysid = $(obj).attr("IBSYSID");
	var state = $(obj).attr("STATE");
	var offerCode = $("#cond_OFFER_CODE").val();
	
	var param = "&IBSYSID="+ibsysid+"&STATE="+state+"&BPM_TEMPLET_ID="+bpmTempletId;
	$.beginPageLoading("正在查询...");
	ajaxSubmit(this,'sureHisConfCrm',param,"",function(datas){
		$.endPageLoading();
		$("#CHANGEDATALINENOS").val("");
		$("#CHANGEDATALINENOS").val(datas);
		
		$("#pattr_BREQUIREMENTDESC").val(datas.get(0).get("BREQUIREMENTDESC"));
		$("#pattr_BUILDINGSECTION").val(datas.get(0).get("BUILDINGSECTION"));
		$("#pattr_BIZRANGE").val(datas.get(0).get("BIZRANGE"));
		$("#pattr_BUSINESSTYPE").val(datas.get(0).get("BUSINESSTYPE"));
//		$("#pattr_DIRECTLINE_SCOPE").val(datas.get(0).get("DIRECTLINE_SCOPE"));
		$("#pattr_SERVICETYPE").val(datas.get(0).get("SERVICETYPE"));
//		$("#pattr_TITLE").val(datas.get(0).get("TITLE"));
		$("#pattr_URGENCY_LEVEL").val(datas.get(0).get("URGENCY_LEVEL"));
//		$("#cond_CITYNAME").val(datas.get(0).get("CITYNAME"));
//		$("#cond_COUNTYNAME").val(datas.get(0).get("COUNTYNAME"));
		changeServicetype();
		var temp = new Wade.DatasetList();
		//将主页面上已存在的数据删除
		$("#DATALINE_PARAM_UL").children().remove();
		
		var offerchaList = new Wade.DatasetList($("#offerchalist").val());
		for (var i = 0; i < offerchaList.length; i++) {
			var offercha = offerchaList[i];
			temp.add(offercha);
		}
		for (var i = 0; i < datas.length; i++) {
			var data = datas.get(i);
			sureHisDataLineParamList(data);
			
			var hisBatchImports = new Wade.DatasetList();
			var key = data.keys;
			for (var m = 0; m < key.length; m++) {
				var value = data.get(m);
				var hisBatchImport = new Wade.DataMap();
				var tempKey = key[m];
				if(tempKey=='BREQUIREMENTDESC'||tempKey=='BUILDINGSECTION'||tempKey=='BIZRANGE'||tempKey=='BUSINESSTYPE'||tempKey=='DIRECTLINE_SCOPE'||tempKey=='TITLE'||tempKey=='URGENCY_LEVEL'||tempKey=='COUNTYNAME'||tempKey=='CITYNAME'){
					continue;
				}
				if(tempKey=='ISPREOCCUPY'){
					if(value=='否'){
						value = "0";
					}else if(value=='是'){
						value = "1";
					}
				}
				if(tempKey=='ROUTEMODE'){
					if(value=='单节点单路由'){
						value = "0";
					}else if(value=='单节点双路由'){
						value = "1";
					}else if(value=='双节点双路由'){
						value = "2";
					}
				}
				hisBatchImport.put("ATTR_CODE", tempKey);
				hisBatchImport.put("ATTR_VALUE", value);
				var name = "pattr_"+key[m];
				hisBatchImport.put("ATTR_NAME", $("input[name='"+name+"']").attr("desc"));
				hisBatchImports.add(hisBatchImport);
			}
			var maxIndex = $("#DATALINE_MAX_INDEX").val();
			var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
			$("#"+offerChaHiddenId).val(hisBatchImports);
			maxIndex++;
			$("#DATALINE_MAX_INDEX").val(maxIndex);
			
			temp.add(hisBatchImports);
			$("#offerchalist").val(temp);
		}
		hidePopup("popup05");
	},null);
}

function ChangeLines(){
	var bizRange = $("#pattr_BIZRANGE").val();
	var condTempletId = $("#cond_TEMPLET_ID").val();
	if (!bizRange && condTempletId !='EDIRECTLINECANCEL') {
		MessageBox.alert("提示","请选择业务范围！");
		return false;
	}
	
	if(condTempletId=="EDIRECTLINECANCEL"){
		var serviceType = $("#pattr_SERVICETYPE").val();
		if(serviceType == ""){
			MessageBox.alert("提示","请选择专线类型！");
			return false;
		}else if(serviceType=='0'){
			bizRange = "本地市";
			$("#pattr_DIRECTLINE_SCOPE").val(1);//发报文的隐藏字段
		}else if(serviceType=='1'){
			bizRange = "省内跨地市";
			$("#pattr_DIRECTLINE_SCOPE").val(2);//发报文的隐藏字段
		}
	}
	
	var offerCode = $("#cond_OFFER_CODE").val();
	var userId = $("#GRP_USER_ID").val();

	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initDirectLineList", "&OFFER_CODE=" + offerCode + "&EC_USER_ID=" + userId + "&BIZRANGE=" + bizRange+"&TEMPLET_ID="+condTempletId, "ChangeLinesResultPart", function(datas) {
		$.endPageLoading();
		$("#offerchalist").val("");
		$("#offerchalist").val(datas);
//		$("#CHANGEDATALINENOS").val("");
//		$("#CHANGEDATALINENOS").val(datas);
	}, function(error_code, error_info, derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code, error_info, derror);
	});
	showPopup("popup11", "querChangeLinesPopupItem", true);
}

function queryChangeLines() {
	var offerCode = $("#cond_OFFER_CODE").val();
	var userId = $("#GRP_USER_ID").val();
	var bizRange = $("#pattr_BIZRANGE").val();
	var datalineNum = $("#pattr_CHANGE_DATALINE_NUM").val();

	var condTempletId = $("#cond_TEMPLET_ID").val();
	if(condTempletId=="EDIRECTLINECANCEL"){
		var serviceType = $("#pattr_SERVICETYPE").val();
		if(serviceType=='0'){
			bizRange = "本地市";
			$("#pattr_DIRECTLINE_SCOPE").val(1);//发报文的隐藏字段
		}else if(serviceType=='1'){
			bizRange = "省内跨地市";
			$("#pattr_DIRECTLINE_SCOPE").val(2);//发报文的隐藏字段
		}
	}
	
	var param = "&OFFER_CODE=" + offerCode + "&EC_USER_ID=" + userId + "&BIZRANGE=" + bizRange ;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("queryChangeLinesForm", "queryChangeLines", param , "ChangeLinesResultPart", function(datas) {
		$.endPageLoading();
			if(datalineNum != '0'){
				$("#offerchalist").add(datas);
			}else{
				$("#offerchalist").val(datas);
			}
			datalineNum++;
			$("#pattr_CHANGE_DATALINE_NUM").val(datalineNum);
		
	}, function(error_code, error_info, derror) {
		$.endPageLoading();
		showDetailErrorInfo(error_code, error_info, derror);
	});
}

function sureChangeLines(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	
	var temp = new Wade.DatasetList();
	var rowDatas = changeLinesTable.getCheckedRowsData("cklines");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选专线信息!");
		return false;
	}
	
	var templetId = $("#cond_TEMPLET_ID").val();
	//展示专线列表
	if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		var changeMode = $("#pattr_CHANGEMODE").val();
		if(!changeMode){
			MessageBox.alert("提示","请先选择变更场景！");
			hidePopup("popup11");
			return false;
		}
		$("#changImport").css("display","");
	}
	
	//第一次查询将主页面上已存在的数据删除
	var datalineNum = $("#pattr_CHANGE_DATALINE_NUM").val();
	if(datalineNum == '0'){
		datalineNum++;
		$("#pattr_CHANGE_DATALINE_NUM").val(datalineNum);
	}
	if(datalineNum == '1'){
		datalineNum++;
		$("#pattr_CHANGE_DATALINE_NUM").val(datalineNum);
		$("#DATALINE_PARAM_UL").children().remove();
	}
	var offerchaList = new Wade.DatasetList($("#offerchalist").val());
	
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var lineNo2 = rowData.get("PRODUCTNO");
		
		for (var j = 0; j < offerchaList.length; j++) {
			var offercha = offerchaList.get(j);
			var lineNo = offercha.get("PRODUCTNO");
			if(lineNo!=lineNo2){
				continue;
			}
			var maxIndex = $("#DATALINE_MAX_INDEX").val();
			
			sureHisDataLineParamList(offercha);
			
			var batchImports = new Wade.DatasetList();
			var key = offercha.keys;
			for (var m = 0; m < key.length; m++) {
				var value = offercha.get(m);
				var batchImport = new Wade.DataMap();
				batchImport.put("ATTR_CODE", key[m]);
				batchImport.put("ATTR_VALUE", value);
				/*var tempName = $("input[name='"+key[m]+"']").attr("desc");
				if(tempName!=undefined){
					batchImport.put("ATTR_NAME", tempName);
				}else{
					batchImport.put("ATTR_NAME", "");
				}*/
				var name = "";
				if(key[m].indexOf("NOTIN_")!=0){
					name = "pattr_"+key[m];
				}else{
					name = key[m];
				}
				batchImport.put("ATTR_NAME",$("input[name="+name+"]").attr("desc"));
				
				batchImports.add(batchImport);
				
			}
			
			if(changeMode=="减容"){
				var dataBandWidthInfo = new Wade.DataMap();
				dataBandWidthInfo.put("ATTR_CODE", "HIDDEN_BANDWIDTH");
				dataBandWidthInfo.put("ATTR_VALUE", offercha.get("BANDWIDTH"));
				dataBandWidthInfo.put("ATTR_NAME","原来带宽");
				batchImports.add(dataBandWidthInfo);
			}
			
			if(templetId!="ECHANGERESOURCECONFIRM"){
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
				$("#"+offerChaHiddenId).val(batchImports);
			}
			
			maxIndex++;
			$("#DATALINE_MAX_INDEX").val(maxIndex);
			
			if(templetId =="EDIRECTLINECANCEL"){
				temp.add(batchImports);
			}else{
				temp.add(offercha);
				
			}
			$("#offerchalist").val(temp);
			
			if(templetId =="ECHANGERESOURCECONFIRM"){
				if(datalineNum == '1'){
					$("#CHANGEDATALINENOS").val(offerchaList);
				}else{
					var temp2 = new Wade.DatasetList();
					var offerchaList2 = new Wade.DatasetList($("#offerchalist").val());
					for (var k = 0; k < offerchaList2.length; k++) {
						var offercha2 = offerchaList2[k];
						temp2.add(offercha2);
					}
					var changeDataLineNos = new Wade.DatasetList($("#CHANGEDATALINENOS").val());
					for (var p = 0; p < changeDataLineNos.length; p++) {
						var offercha3 = changeDataLineNos[p];
						temp2.add(offercha3);
					}
					$("#CHANGEDATALINENOS").val(temp2);
				}
			}
			
			break;
		}
	}
	
	//加载导出条件域
	setExportLineNos();
	
	hidePopup("popup11");
	
}

function setExportLineNos(){
	var checkboxs = $("#DATALINE_PARAM_UL input[type=checkbox]");
	var lineNos = "";
	for(var i=0;i<checkboxs.length;i++){
		if(checkboxs[i].checked){
			lineNos += $(checkboxs[i]).attr("productno")+",";
		}
	}
	$("#LINE_NOS").val(lineNos);
	//alert($("#LINE_NOS").val());
}
function checkInDataName(directlineTypeData,bpmTempletId){
	debugger;
	var flags="";
	
	var tradeNamesInfos = new  Wade.DatasetList();
	
	if("ERESOURCECONFIRMZHZG"==bpmTempletId||"ECHANGERESOURCECONFIRM"==bpmTempletId){
		flags="";
	}else{
		for ( var j = 0; j < directlineTypeData.length; j++) 
		{
			var tradename= "";
			var productNo= "";
			var bis = directlineTypeData.get(j);
			var tradeNamesInfo =new  Wade.DataMap();
			for ( var e = 0; e < bis.length; e++) 
			{
				var biss=bis.get(e);
				var attrValue= biss.get("ATTR_CODE");
				if("pattr_TRADENAME"==attrValue||"TRADENAME"==attrValue){
					tradename= biss.get("ATTR_VALUE"); 
					tradeNamesInfo.put("TRADENAME",tradename);
					
				}
				if("pattr_PRODUCTNO"==attrValue||"PRODUCTNO"==attrValue){
					productNo=biss.get("ATTR_VALUE"); 
					tradeNamesInfo.put("PRODUCTNO",productNo);
				}
				if(""!=flags){
					break;
				}
				
			}
			if(""!=tradename&&""!=productNo){
				var param ="&TRADE_NAME="+encodeURIComponent(tradename)+"&BPM_TEMPLET_ID="+bpmTempletId+"&PRODUCTNO="+productNo;
				$.beginPageLoading("专线名称校验中...");
				$.ajax.submit("", "queryTradename", param , "", function(data) {
					$.endPageLoading();
					if("true" != data.get("RESULT")){
						flags = data.get("ERROR_MESSAGE");
					}
					
				}, function(error_code, error_info, derror) {
					$.endPageLoading();
					showDetailErrorInfo(error_code, error_info, derror);
				},{async:false});
			}
			
			if(""!=tradename&&""!=productNo){
				tradeNamesInfos.add(tradeNamesInfo);
			}
			
		}
		if(tradeNamesInfos.length >1 ){
			$.beginPageLoading("专线名称校验中...");
			$.ajax.submit("", "queryTradenameInfo", "&TRADENAME_INFOS="+tradeNamesInfos , "", function(data) {
				$.endPageLoading();
				if("true" != data.get("RESULT")){
					flags = data.get("ERROR_MESSAGE");
				}
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			},{async:false});
		}
	}
	return flags;
	
}

