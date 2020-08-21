
var moList = new Wade.DatasetList();

/**$Id: UserParamInfo.js,v 1.10 2013/05/02 07:44:11 liming2 Exp $*/
/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
 */

/**
 * 统一init方法
 */

function initPageParam(){
	
	//绑定表格
	bindDom();
	
    var svcotherlists = $("#svcotherlists").text();
    
	//将svcotherlists转成list类型
	var datalist = $.DatasetList(svcotherlists);
	//将数据放入表格中
	datalist.each(function(item){
		tjOtherTable.addRow($.parseJSON(item.toString()));
	});
	
	//当时变更的时候加载第三张表格的所填数据
	if($("#MODIFY_TAG").val()==2 && moList=='[]'){
		//变更产生的数据
		var molists = new Wade.DatasetList($("#molists").text());
		$.each(molists,function(index,data) { 
			//先放入输入框中，再取值放入表格中
			$("#MO_MATH").val(molists.get(index,'MO_MATH'));
			$("#MO_CODE").val(molists.get(index,'MO_CODE'));
			$("#MO_MATH_NAME").val(molists.get(index,'MO_MATH_NAME'));
			$("#MO_TYPE").val(molists.get(index,'MO_TYPE'));
			$("#DEST_SERV_CODE").val(molists.get(index,'DEST_SERV_CODE'));
			$("#DEST_SERV_CODE_MATH").val(molists.get(index,'DEST_SERV_CODE_MATH'));
			
			//新增表格行数据,并且保存参数
			var superTelEdit = $.ajax.buildJsonData("MoListDetail");
			
			//没有重复的数据，往表格中添加一条数据
			var data = $.DataMap(superTelEdit);
			//放入表格中的下拉框值
			data.put("MO_MATH_NAME",$("#MO_MATH").text());
			data.put("MO_TYPE_NAME",$("#MO_TYPE").text());
			data.put("DEST_SERV_CODE_MATH_NAME",$("#DEST_SERV_CODE_MATH").text());
			//往表格中增加一条数据
			MoListTable.addRow($.parseJSON(data.toString()));
			//删除输入框中所填的值
			delAll(superTelEdit);
		});
	}else{
		//将保存的第三张表格数据放入表格中
		if(moList!=null){
			$.each(moList,function(index,data) { 
				MoListTable.addRow($.parseJSON(data.toString()));
			});
		}
	}
	
	
	
	
	//和adc区别
	$("#pam_AUTH_CODE").attr('disabled',true);
	$("#pam_MAX_ITEM_PRE_MON").attr('disabled',$("#CHANGE_MAX_ITEM_PRE_MON").val());
	$("#pam_IS_TEXT_ECGN").attr('disabled',true);
	$("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	
	
	var serviceId = $("#SERVICE_ID").val();
	dealOperStateoptions(serviceId); //操作类型、服务状态设置，置恢部分输入框，管理员手机号码,个性化参数
	
	
	setStatetype();	//根据不同的操作类型，页面输入框的可见性.
	setDeliverNumDis();// 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
	dealWithBlackWhiteAttr();// 省级权限GROUP999的工号才可以订购黑名单，和变更业务属性。
}
/**
 * 为表格绑上事件
 * @returns
 */
function bindDom(){
	
	//给表格加个事件
	window["MoListTable"] = new Wade.Table("MoListTable", {
		fixedMode:true,
		editMode:true,
	});
	//给表格绑事件
	$("#MoListTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(MoListTable.getSelectedRowData());
	});
	
}
/**
 * 最后提交是的判断
 * @param obj
 * @returns
 */
function checkSub(obj){
	
	$("#MO_CODE").attr("nullable","yes");
	$("#MO_MATH").attr("nullable","yes");
	$("#MO_TYPE").attr("nullable","yes");
	$("#DEST_SERV_CODE").attr("nullable","yes");
	$("#DEST_SERV_CODE_MATH").attr("nullable","yes");

	//对时间填写进行校验
	var forbidflag =checkforbidtime();
	if(!forbidflag)
	{
	  return;
	}
	
	var productId = $("#SERVICE_ID").val();
	//对中文签名进行校验
	if(productId!='9051'){
		var ecgnzhflag = checkecgnzh();
		if(!ecgnzhflag)
		{
		  return;
		}
	}
	moList = MoListTable.getData();
	
	//保存molist的值，随公共走到后台
	$("#pam_MOLIST_Hidden").val(moList.toString());
	//保存svclist表格的值，随公共走到后台
	$("#pam_paramArea").val($("#svcotherlists").text());
	
	if(!$.validate.verifyAll(datePart))
    {
	   return false;
    }
	try {
		submitOfferCha();
		backPopup(obj);
	} catch (msg) {
		$.error(msg.message);
	}
}
/**
 * 作用：操作类型、服务状态设置，置恢部分输入框，管理员手机号码,AJAX刷新下拉列表。
 * 		基本接入号下拉列表，个性化参数
 */
function dealOperStateoptions(serviceId)
{
 	var modifytag=$('#MODIFY_TAG').val();
 	if("0"==modifytag)//新增时
 	{
 		var operStateList = new Wade.DatasetList();
 		var operState = new Wade.DataMap();
 		operState.put("TEXT", "新增");
 		operState.put("VALUE", "0");
 		operStateList.add(operState);
 		$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
 		$("#pam_OPER_STATE").val("0"); //新增
 		
 	}else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  $('#pam_OPER_STATE').html("");
 	  $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }
 	  if(platsyncState=="P")//暂停
 	  {
 	  	var operStateList = new Wade.DatasetList();
 		var operState = new Wade.DataMap();
 		operState.put("TEXT", "恢复");
 		operState.put("VALUE", "05");
 		operStateList.add(operState);
 		$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
 		$("#pam_OPER_STATE").val("05");//默认选为恢复
 	  	
 	  }else if(platsyncState=="1")//正常在用
 	  {
 	   $('#pam_OPER_STATE').html("");
	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
	    var operStateList = new Wade.DatasetList();
		var operState = new Wade.DataMap();
		operState.put("TEXT", "暂停");
		operState.put("VALUE", "04");
		var operState2 = new Wade.DataMap();
		operState2.put("TEXT", "变更");
		operState2.put("VALUE", "08");
		operStateList.add(operState);
		operStateList.add(operState2);
		$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
		$("#pam_OPER_STATE").val("08"); //默认选为变更
 	  }
 	  //getParamLists(serviceId);      //个性化参数
 	}
}

/**
 * 作用：基本接入号下拉列表
 */
function getComboBoxList(serviceId){
		 custId=$.getSrcWindow().$("#CUST_ID").val();//取父页面的CUST_ID
		$.ajax.submit('this', 'getComboBoxValue','&CUST_ID='+custId+'&SERVICE_ID='+serviceId,'bizCodeArea', function(){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function chgBizCode()
{
	var bizCode=$('#pam_BIZ_CODE').val();
	var servType=$('#pam_SERV_TYPE').val();
	$.ajax.submit(this, 'createNewBizCode', '&BIZ_CODE='+bizCode+'&SERV_TYPE='+servType,null, 
			function(data){
				$('#pam_BIZ_CODE').val(data.get(0,"BIZ_CODE"));
			},
			function(error_code,error_info,derror){
				showDetailErrorInfo(error_code,error_info,derror);
			},
			{async:false});
}


/**
*作用：字间段的校验
*/
function checkforbidtime()
{
	forbidstarttimea=$("#pam_FORBID_START_TIME_A").val(); 
	forbidendtimea=$("#pam_FORBID_END_TIME_A").val(); 
	forbidstarttimeb=$("#pam_FORBID_START_TIME_B").val(); 
	forbidendtimeb=$("#pam_FORBID_END_TIME_B").val(); 
	forbidstarttimec=$("#pam_FORBID_START_TIME_C").val(); 
	forbidendtimec=$("#pam_FORBID_END_TIME_C").val(); 
	forbidstarttimed=$("#pam_FORBID_START_TIME_D").val(); 
	forbidendtimed=$("#pam_FORBID_END_TIME_D").val(); 
	
	if((forbidstarttimea==""&&forbidendtimea!="")||(forbidstarttimea!=""&&forbidendtimea==""))
	{
		$.validate.alerter.one($("#pam_FORBID_END_TIME_A")[0], "不允许下发开始时间1和不允许下发结束时间1 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimeb==""&&forbidendtimeb!="")||(forbidstarttimeb!=""&&forbidendtimeb==""))
	{
		$.validate.alerter.one($("#pam_FORBID_END_TIME_B")[0], "不允许下发开始时间2和不允许下发结束时间2 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimec==""&&forbidendtimec!="")||(forbidstarttimec!=""&&forbidendtimec==""))
	{
		$.validate.alerter.one($("#pam_FORBID_END_TIME_C")[0], "不允许下发开始时间3和不允许下发结束时间3 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimed==""&&forbidendtimed!="")||(forbidstarttimed!=""&&forbidendtimed==""))
	{
		$.validate.alerter.one($("#pam_FORBID_END_TIME_D")[0], "不允许下发开始时间4和不允许下发结束时间4 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	return true;
}

/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	08-变更 04- 暂停 05-恢复
 */
function setStatetype(){
	
  var operState=$("#pam_OPER_STATE").val();
//  var monState = document.getElementById("pam_MAX_ITEM_PRE_MON").disabled;
  var monState = $("#CHANGE_MAX_ITEM_PRE_MON").val();
  if (operState == "08"){
	  $("#pam_TEXT_ECGN_EN").attr('disabled',false);
	  $("#pam_TEXT_ECGN_ZH").attr('disabled',false);
	  $("#pam_OPER_STATE").attr('disabled',false);
	  $("#SELECT_NO").attr('disabled',true);//置灰选号按钮
	  $("#pam_BILLING_TYPE").attr('disabled',false);
	  $("#pam_PRICE").attr('disabled',false);
	  $("#pam_DELIVER_NUM").attr('disabled',false);
	  $("#pam_USAGE_DESC").attr('disabled',false);
	  $("#pam_INTRO_URL").attr('disabled',false);
	  $("#pam_CS_URL").attr('disabled',false);
	  $("#pam_BILLING_MODE").attr('disabled',false);
	  $("#pam_MAX_ITEM_PRE_DAY").attr('disabled',false);
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',false);
	  $("#pam_IS_TEXT_ECGN").attr('disabled',false);
	  $("#pam_AUTH_CODE").attr('disabled',false);
	  
	  $("#pam_PRICE").attr('disabled',false);
	  $("#pam_BIZ_PRI").attr('disabled',false);
	  dealWithEcgnZhEn();
	  $("#pam_DEFAULT_ECGN_LANG").attr('disabled',false);	
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',monState);
	  //增加控制2018/2/3
	  $("#pam_EC_BASE_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
  }
  if (operState == "04"){
	  $("#pam_EC_BASE_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_BIZ_PRI").attr('disabled',true);
	  $("#pam_BILLING_MODE").attr('disabled',true);
	  $("#pam_DEFAULT_ECGN_LANG").attr('disabled',true);
	  $("#pam_TEXT_ECGN_ZH").attr('disabled',true);
	  $("#pam_TEXT_ECGN_EN").attr('disabled',true);
	  
//	  tableDisabled("platsvcparamtable",true);
//	  tableDisabled("MoListDetail",true);
  } 
  if (operState == "05"){
//	  tableDisabled("platsvcparamtable",true);
//	  tableDisabled("MoListDetail",true);
	  $("#pam_EC_BASE_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_BIZ_PRI").attr('disabled',true);
	  $("#pam_BILLING_MODE").attr('disabled',true);
	  $("#pam_DEFAULT_ECGN_LANG").attr('disabled',true);
	  $("#pam_TEXT_ECGN_ZH").attr('disabled',true);
	  $("#pam_TEXT_ECGN_EN").attr('disabled',true);
  }
  if (operState == "01"){
	  var bizCode = $("#pam_BIZ_CODE").val();
  	  var bizName = $("#pam_BIZ_NAME").val();
  	  var bizTypeCode = $("#pam_BIZ_TYPE_CODE").val();
  	  var accessMode = $("#pam_ACCESS_MODE").val();
  	  var bizStatus = $("#pam_BIZ_STATUS").val();
  	  var preCharge = $("#pam_PRE_CHARGE").val();
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);

	  	  if(bizCode == ""){
	  	$("#pam_BIZ_CODE").attr('disabled',false);
	  }
	  else{
	  	$("#pam_BIZ_CODE").attr('disabled',true);
	  }
	  if(bizName == ""){
	  	$("#pam_BIZ_NAME").attr('disabled',false);
	  }
	  else{
	  	$("#pam_BIZ_NAME").attr('disabled',true);
	  }
	  if(bizTypeCode == ""){
	  	$("#pam_BIZ_TYPE_CODE").attr('disabled',false);
	  }
	  else{
	  	$("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  }
	  if(accessMode == ""){
	  	$("#pam_ACCESS_MODE").attr('disabled',false);
	  }
	  else{
	  	$("#pam_ACCESS_MODE").attr('disabled',true);
	  }
	  $("#pam_BILLING_TYPE").attr('disabled',false);
	  $("#pam_PRICE").attr('disabled',false);
	  if(bizStatus == ""){
	  	$("#pam_BIZ_STATUS").attr('disabled',false);
	  }
	  else{
	  	$("#pam_BIZ_STATUS").attr('disabled',true);
	  }
	  if(preCharge == ""){
	  	$("#pam_PRE_CHARGE").attr('disabled',false);
	  }
	  else{
	  	$("#pam_PRE_CHARGE").attr('disabled',true);
	  }
  }
 }


/**
*作用：控制服务代码的显示，及校验标示的去必填
*/
function showBizInCode(){
	$("#SI_BASE_IN_CODE").css("display","none");
	//$("#BIZ_IN_CODE").attr("display","none");
	$("#SI_BASE_IN_CODE").attr("nullable","yes");
	
}

/**
 * 作用：控制TABLE里的值是否可填
 * @param tableName 
 * @param flag
 */
function tableDisabled(tableName, flag){

$("#" + tableName + " input").each(function(){
    this.disabled=flag;
});
	  
$("#" + tableName + " SELECT").each(function(){
     this.disabled=flag;
});
}
/**
 * 作用：点取消按钮返回原值数据到父页面隐藏字段
 */
function setCancleData(obj)
{
	var serviceId="";	
	var itemIndex= "";	
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var svcparamvalue="";
	try
	  {
	  	  svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
	  	  if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
	  	  {
	  	  	svcparamvalue="";
	  	  }
	  }
	  catch(e)
	  {
		  svcparamvalue="";
	  }
	for (i = 0; i < parameterParts.length; i++) 
	{ 
		var pairParts = parameterParts[i].split("="); 
		var pairName = pairParts[0]; 
		var pairValue = pairParts[1]; 
		if(pairName=="ELEMENT_ID")
		{
		  serviceId=pairValue;
		}
		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		} 
	}
	if(svcparamvalue==""||svcparamvalue=="[]")   //没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
		var dataset = $.DatasetList();
		var paramVerifySuccMap=$.DataMap();
		paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
		dataset.add(paramVerifySuccMap);
		svcparamvalue=dataset.toString();
	}
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,svcparamvalue.toString());
	$.setReturnValue();
}

//下拉列表选择
function getSvrCodeHead(){
	$("#pam_BIZ_IN_CODE").val($("#pam_EC_BASE_IN_CODE").val());
	checkAccessNumber();
}

/**校验服务代码是否可用*/
function checkAccessNumber(){
    var bizInCode = $("#pam_BIZ_IN_CODE").val();
    var ecBaseCode = $("#pam_EC_BASE_IN_CODE").val();
    var bizcode = $("#pam_BIZ_CODE").val();
    var extendcode=$("#pam_BIZ_EXTEND_CODE").val();
    bizcode=bizcode.replace(/(^\s*)|(\s*$)/g, "");
    
    if(bizcode=="")
    {
    	$.validate.alerter.one($("#pam_BIZ_CODE")[0], "请先填写业务代码！\n");
    	$("#pam_BIZ_IN_CODE").val("");
    	$("#pam_BIZ_CODE").focus();
    	return;
    }
    var lengthEc = ecBaseCode.length;
    if (bizInCode.length < lengthEc || ecBaseCode != bizInCode.substring(0,lengthEc)){
    	$.validate.alerter.one($("#pam_BIZ_IN_CODE")[0], "服务代码必须以基本接入号开始！\n");
    	$("#pam_BIZ_IN_CODE").val("");
    	return;
    }
    var authCode = $("#pam_AUTH_CODE");
    if(bizInCode.length <=3){
    	authCode.selectedIndex = 1;
    }else{
    	authCode.selectedIndex = 2;
    }
    
   var groupId="";
   	try
	  {
	  	  groupId=$.getSrcWindow().$("#GROUP_ID").val();//取父页面GROUP_ID值
	  	  if(groupId==undefined)
	  	  {
	  	  	groupId="";
	  	  }
	  }
	  catch(e)
	  {
		  groupId="";
	  }

	$.beginPageLoading();
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "checkAccessNumber",'&GROUP_ID='+groupId+'&ACCESSNUMBER='+bizInCode+'&BIZ_CODE='+bizcode+'&BIZ_EXTEND_CODE='+extendcode,
	        function(data){
	        afterCheckAccessNumber(data);
	        $.endPageLoading();
	        },function(error_code,error_info,derror){
	        $.endPageLoading();
	        //showDetailErrorInfo(error_code,error_info,derror);
	        });
    
}

/**校验扩展码是否可用*/
function checkExtendCode(){
    var bizInCode = $("#pam_BIZ_IN_CODE").val();
    var ecBaseCode = $("#pam_EC_BASE_IN_CODE").val();
    var bizcode = $("#pam_BIZ_CODE").val();
    var extendcode=$("#pam_BIZ_EXTEND_CODE").val();
    bizcode=bizcode.replace(/(^\s*)|(\s*$)/g, "");
    if(extendcode != ""){
    	var lengthExCode = extendcode.length;
	    if (lengthExCode < 1 || lengthExCode > 7){
	       	parent.MessageBox.alert("","扩展码长度必须是1到7位！");
	       	$("#pam_BIZ_EXTEND_CODE").val("");
	       	return;
	    }
    }
    
	//add by jinlt  20160829 start HXYD-YZ-REQ-20160808-003-关于提交MAS和云MAS多级子账户优化需求
    if(extendcode != ""){ //先判断扩展码是否为空
    	if(bizcode != ""){ //判断服务代码是否为空
		 //var bizInCode1 = ecBaseCode.concat(extendcode); //拼串后的业务接入号
		  var bizInCode1 = ecBaseCode + extendcode; //拼串后的业务接入号
    	}
    }else{
    	var bizInCode1 = bizInCode;
    }
	//add by jinlt  20160829 end HXYD-YZ-REQ-20160808-003-关于提交MAS和云MAS多级子账户优化需求
   var groupId="";
   	try
	  {
	  	  groupId=$.getSrcWindow().$("#GROUP_ID").val();//取父页面GROUP_ID值
	  	  if(groupId==undefined)
	  	  {
	  	  	groupId="";
	  	  }
	  }
	  catch(e)
	  {
		  groupId="";
	  }

	$.beginPageLoading();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "checkAccessNumber",'&GROUP_ID='+groupId+'&ACCESSNUMBER='+bizInCode+'&BIZ_CODE='+bizcode+'&BIZ_EXTEND_CODE='+extendcode,
	        function(data){
		    afterCheckExtendCode(data);
	        $.endPageLoading();
	        },function(error_code,error_info,derror){
	        $.endPageLoading();
	        //showDetailErrorInfo(error_code,error_info,derror);
	        });
    
}

/**校验服务代码AJAX刷新后的处理*/
function afterCheckAccessNumber(data){
//	var flag = data.get(0,"ISCHECKAACCESSNUMBER");
    var flag = data.get("result").get(0,"ISCHECKAACCESSNUMBER");

	var accessNumber = $("#pam_BIZ_IN_CODE").val();
	if(flag == "true"){
		$.validate.alerter.one($("#pam_BIZ_IN_CODE")[0], "服务代码可以使用！\n");
		$('#pam_BIZ_IN_CODE').val(accessNumber);
	}else{
		$.validate.alerter.one($("#pam_BIZ_IN_CODE")[0], "服务代码["+accessNumber+"]不能使用(业务代码 加 服务代码和本集团其他业务互相冲突)，请重新输入！");
		$('#pam_BIZ_IN_CODE').val("");
	}
}

/**校验扩展码AJAX刷新后的处理*/
function afterCheckExtendCode(data){
//	var flag = data.get(0,"ISCHECKAACCESSNUMBER");
    var flag = data.get("result").get(0,"ISCHECKAACCESSNUMBER");
	
	var extendNumber = $("#pam_BIZ_EXTEND_CODE").val();
	if(extendNumber != ""){
		if(flag == "true"){
			$.validate.alerter.one($("#pam_BIZ_EXTEND_CODE")[0], "扩展码可以使用！\n");
			$('#pam_BIZ_EXTEND_CODE').val(extendNumber);
		}else{
			$.validate.alerter.one($("#pam_BIZ_EXTEND_CODE")[0], "扩展码["+extendNumber+"]不能使用(业务代码 加扩展码和本集团其他业务互相冲突)，请重新输入！\n");
			$('#pam_BIZ_EXTEND_CODE').val("");
		}
	}
}

/**luojh 2009-11-07 15:41 end*/

/**
*table动态表：选中后将数据放入下方的输入框中
*/
function tableRowClick(data) {
	$("#MO_CODE").val(data.get("MO_CODE"));
	$("#MO_MATH").val(data.get("MO_MATH"));
	$("#MO_TYPE").val(data.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(data.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(data.get("DEST_SERV_CODE_MATH"));
}

function tableRowDBClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#MO_CODE").val(rowData.get("MO_CODE"));
	$("#MO_MATH").val(rowData.get("MO_MATH"));
	$("#MO_TYPE").val(rowData.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(rowData.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(rowData.get("DEST_SERV_CODE_MATH"));
}
/**
*动态表格新增一条记录
*/
function createMoinfo() 
{
	//定义用于判断是否唯一的标识
	var flag = 0;
	var editDate = $.ajax.buildJsonData("MoListDetail");
	
  	if(!$.validate.verifyAll('MoListDetail'))return false;
  	
  //新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("MoListDetail");
	
	var list =  MoListTable.getData(true,'MO_CODE,DEST_SERV_CODE');
	list.each(function(item,index,totalcount){
		if($("#MO_CODE").val()==list.get(index,'MO_CODE')||$("#DEST_SERV_CODE").val()==list.get(index,'DEST_SERV_CODE')){
			flag = 1;
		}
	});
	
	//判断是否有重复的数据
	if(flag==0){
		//没有重复的数据，往表格中添加一条数据
		var data = $.DataMap(superTelEdit);
		//放入表格中的下拉框值
		data.put("MO_MATH_NAME",$("#MO_MATH").text());
		data.put("MO_TYPE_NAME",$("#MO_TYPE").text());
		data.put("DEST_SERV_CODE_MATH_NAME",$("#DEST_SERV_CODE_MATH").text());
		//往表格中增加一条数据
		MoListTable.addRow($.parseJSON(data.toString()));
		//删除输入框中所填的值
		delAll(superTelEdit);
	}else{
		//有重复的数据
		$.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN_1")[0], "添加失败！“指令内容、目的号码”已经存在相同的值！");
		//删除输入框中所填的值
		delAll(superTelEdit);
	}
}
/**
*动态表格更新一条记录
*/
function updateMoinfo() 
{
var editDate = $.ajax.buildJsonData("MoListDetail");
	
  	if(!$.validate.verifyAll('MoListDetail'))return false;
  	
   //新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("MoListDetail");
	
	
	var data = $.DataMap(superTelEdit);
		
	//放入表格中的下拉框值
	data.put("MO_MATH_NAME",$("#MO_MATH").text());
	data.put("MO_TYPE_NAME",$("#MO_TYPE").text());
	data.put("DEST_SERV_CODE_MATH_NAME",$("#DEST_SERV_CODE_MATH").text());
		
	//更新表格中选中的一条数据
	MoListTable.updateRow($.parseJSON(data.toString()),MoListTable.selected);		
	//删除输入框中所填的值
	delAll(superTelEdit);
}
  
/**
*动态表格删除一条记录
*/
function deleteMoinfo() 
{
	//删除选中的一条数据的值
	MoListTable.deleteRow(MoListTable.selected);
	MoListTable.remove(MoListTable.getData());
}

/**
 * 添加到表格之后，删除输入框中的值
 */
function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}	




function getTableData() {
	//var data = $.table.get("MoListTable").getTableData(null,true);
	//$("#MoListTable tbody tr")[1].setAttribute("status", "2");
		var data1 = $.table.get("MoListTable").getTableData("MO_CODE", true);
	parent.MessageBox.alert("",data1);
}

//替换动态表格操作标识字段 并且设置其显示属性
function replaceTablecol(moCode,destServCode,xtag)
{
	var table=$.table.get("MoListTable").getTableData();//取动态表格数据集
	table.each(function(item,idx){
		var cellmodcode = item.get("MO_CODE");
		var celldestServCode = item.get("DEST_SERV_CODE");
		if(moCode==cellmodcode&&destServCode==celldestServCode)
		{
			$("#MoListTable tbody tr")[idx].setAttribute("status", xtag);
		   if(xtag=='1')//代表删除 那么将要删除的这行隐藏掉
		   {
		   		$("#MoListTable tbody tr")[idx].style.display='none';
		   }
		}
		
	});
}


/****************************************************动态表格结束*******************************************************/


/*
   1－永久白名单
   2－黑名单 
   3－限制次数白名单
   4－点播业务
  如果业务类型是3或是4时，需要填写pam_BIZ_ATTR。
*/
function chargeBizAttr(){
   setDeliverNumDis();//根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
   setPreDay();//根据选择的业务属性不同，改变对应的每月最大短信数和每天最大短信数的默认值
}

/**
*@author:liaolc
 @function: 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
*/
function setDeliverNumDis(){
   var bizAttrValue=$("#pam_BIZ_ATTR").val(); 
   if(bizAttrValue==1||bizAttrValue==2||bizAttrValue==""||bizAttrValue==null){
       $("#pam_DELIVER_NUM").val("0");
       $("#pam_DELIVER_NUM").attr('disabled',true);
//       $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
   }else{
      $("#pam_DELIVER_NUM").attr('disabled',false);
//      $.Flip.get("pam_DELIVER_NUM").setDisabled(false);
   }   
}

/**
*@author:ganquan
 @function: 根据选择的业务属性不同，改变对应的每月最大短信数和每天最大短信数的默认值
*/
function setPreDay(){
	var pam_BIZ_ATTR = $("#pam_BIZ_ATTR").val();
	if(pam_BIZ_ATTR == "1"){
		$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("defineValue"));
		$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("defineValue"));
	}
	else if(pam_BIZ_ATTR == "2"){
		$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("defineValue1"));
		$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("defineValue1"));
	}
}

/**
*作用：判断输入框的值
*/
function checkMessageAmount(obj){
	var rightClass = obj.getAttribute("rightClass");
	
	//xuxf*******************
	//根据业务属性，为amount选择不同的最大值
	var pam_BIZ_ATTR = $("#pam_BIZ_ATTR").val();
	var amount = "0";
	if(pam_BIZ_ATTR == "1"){
		amount = obj.getAttribute("maxValue");
	}
	else if(pam_BIZ_ATTR == "2"){
		amount = obj.getAttribute("maxValue1");
	}
	//***********************xuxf
	obj.value = obj.value.replace(/[^\d]/g,"");
	var inputValue = Number(obj.value);
	obj.value = Number(inputValue);
	if (rightClass <= 4){
	   if(Number(amount) < Number(inputValue)){
	     parent.MessageBox.alert("","您的权限级别为["+rightClass+"]，设置的最大下发量为["+amount+"]，请别越权！");
         obj.value = amount;
	   }

	   if(Number(inputValue) < 1){
	     parent.MessageBox.alert("","您的权限级别为["+rightClass+"]，设置的参数值不能为0，请别越权！");
	     obj.value = amount;
	   }

	}
	
	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue)){
		parent.MessageBox.alert("","您输入的日下发流量大于月下发流量，请重新输入！");
		//xuxf:*************************
		if(pam_BIZ_ATTR == "1"){
			$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("maxValue"));
			$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("maxValue"));
		}
		else if(pam_BIZ_ATTR == "2"){
			$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("maxValue1"));
			$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("maxValue1"));
		}
		//******************************xuxf
	}
}

/**
*作用：判断中文签名输入框的值
*/
function checkEcgnZH(obj){
	var ecgn_zh_class = obj.getAttribute("ecgn_zh_class");
	if (ecgn_zh_class < 4){
	     //parent.MessageBox.alert("","您无权修改中文签名为中英文混搭方式！");
         obj.value = obj.value.replace(/[^\u4E00-\u9FA5]/g,"");
	}
	else{
		obj.value = obj.value.replace(/[^\u4E00-\u9FA50-9]/g,"");
	}
}

/**
*作用：中文签名长度校验
*/
function checkecgnzh(){
     ecgnzh = $("#pam_TEXT_ECGN_ZH").val();
     var lenReg =  ecgnzh.replace(/[^\x00-\xFF]/g,'**').length;
//     var textEcgnClass = $("#pam_TEXT_ECGN_ZH").attr("textEcgnClass");
     var textEcgnClass = $("#GRP_ECGN_SIZE_CLASS").val();
	 if (textEcgnClass!=5){
	     if(lenReg<6 || lenReg>16)
	     {
	    	 $.validate.alerter.one($("#pam_TEXT_ECGN_ZH")[0], "中文签名长度只能为6～16个字符!\n");
	     	return false;
	     }
	 }
	return true;
}


/** */
function chargeTypeChanged(){
   var billType = $("#pam_BILLING_TYPE").val();
   var price = $("#pam_PRICE").val();
   if (billType == "02"){
       if(price < 1) {
    	   $.validate.alerter.one($("#pam_PRICE")[0], "单价请输入大于0的值!\n");
       }
       $("#pam_PRICE").val("");
   }else{
     $("#pam_PRICE").val("0");
   }
}

function chargePrice(){
   var billType = $("#pam_BILLING_TYPE").val();
   var price = $("#pam_PRICE").val();
   if ( billType != "02"){
       $("#pam_PRICE").val("0");
   }else{
       if(price <= 0){ 
	       $.validate.alerter.one($("#pam_PRICE")[0], "请输入大于0的值!\n");
	       $("#pam_PRICE").val("");
       }
   }
}

/*
 * HXYD-YZ-REQ-20100706-002关于进一步完善ADC/MAS黑白名管理功能的需求
 * 省级权限GROUP999的工号才可以订购黑名单，和变更业务属性。
 *  2010-11-03新增了权限中英文签名的控制，只有GROUP999的工号才可以变更。
 */
function dealWithBlackWhiteAttr(){
	var blackWhiteClass = $("#BLACK_WHITE_CLASS_ATTR").val();
    
	if (blackWhiteClass==5){
	    $("#pam_BIZ_ATTR").attr('disabled',false);  	    
	}else{
	    $("#pam_BIZ_ATTR").attr('disabled',true); 
	    var operState=$("#pam_OPER_STATE").val();
	    //只是在变更时控制，暂停和恢复已经在本JS的其他部分控制了
	    if(operState == "08"){
	    $("#pam_TEXT_ECGN_ZH").attr('disabled',true); 
	    $("#pam_TEXT_ECGN_EN").attr('disabled',true); 	
	    }	 
	}	
}

function dealWithEcgnZhEn(){
	var blackWhiteClass = $("#BLACK_WHITE_CLASS_ATTR").val();
    
    if (blackWhiteClass!=5){
	    $("#pam_TEXT_ECGN_ZH").attr('disabled',true); 
	    $("#pam_TEXT_ECGN_EN").attr('disabled',true); 	

    }
}

/* 服务代码必须填限制 */
function checkBizInCode(){
   var result=false;
   var bizInCode=$('#pam_BIZ_IN_CODE').val(); 
   if(bizInCode == ""||bizInCode==null){
      $('#pam_BIZ_IN_CODE').val('');
   		result=true;
     }
 }
//add by jinlt 20160902  HXYD-YZ-REQ-20160808-003-关于提交MAS和云MAS多级子账户优化需求 start
/* 扩展码必须填限制 */
function checkBizExtendCode(){
   var result=false;
   var bizExtendCode=$('#pam_BIZ_EXTEND_CODE').val(); 
   var lengthExCode = bizExtendCode.length;
   if(bizExtendCode == ""||bizExtendCode==null){
      $('#pam_BIZ_EXTEND_CODE').val('');
   		result=true;
     }
 }
//add by jinlt 20160902  HXYD-YZ-REQ-20160808-003-关于提交MAS和云MAS多级子账户优化需求 end

 /**
 * 作用：动态表格列元素值的修改，
 */
function inputBlur(ipt){
  var field = ipt.getAttribute("field");
  var filltype = ipt.getAttribute("filltype");
  var value = $(ipt).val();
  if(field != "" && field != null && field != value){
  	 if(filltype == "0"){
     	ipt.parentNode.parentNode.parentNode.parentNode.cells[0].innerText="2";
     }else if(filltype == "1"){
        ipt.parentNode.parentNode.parentNode.parentNode.parentNode.cells[0].innerText="2";
     }
  }else{
     if(filltype == "0"){
     	ipt.parentNode.parentNode.parentNode.parentNode.cells[0].innerText="0";
     }else if(filltype == "1"){
     	ipt.parentNode.parentNode.parentNode.parentNode.parentNode.cells[0].innerText="0";
     }
  }
  
  if(filltype == "0"){
     	ipt.parentNode.parentNode.parentNode.parentNode.cells[4].innerText=$(ipt).val();
     }else if(filltype == "1"){
     	ipt.parentNode.parentNode.parentNode.parentNode.parentNode.cells[4].innerText=$(ipt).val();
     }  
}

/**
 * 作用：如果用户有个性化参数，则取
 */
function getParamLists(serviceId){
	var grpUserEparchyCode = $("#GRP_USER_EPARCHYCODE").val();
	var userId=$.getSrcWindow().$("#USER_ID").val();
	if(userId==undefined)	userId="";
	var param = '&USER_ID='+userId+'&SERVICE_ID='+serviceId+'&GRP_USER_EPARCHYCODE='+grpUserEparchyCode;
	$.ajax.submit('this', 'getParamLists',param,'paramArea', function(data){
		var paramVerifySucc=data.get(0);
		var platsvcdata=paramVerifySucc.get('BIZ_EXTEND_CODE');
		$('#pam_BIZ_EXTEND_CODE').val(platsvcdata);
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

