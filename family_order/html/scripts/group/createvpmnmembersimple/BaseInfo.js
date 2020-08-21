function onSubmitBaseTradeCheck(){
	//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
	}
	
	var tempElements = selectedElements.selectedEls;
	if (!tempElements) {
		return false;
	}
	for (var i = 0; i < tempElements.length; i++) {
		if (tempElements.get(i, "MODIFY_TAG") == "0" && tempElements.get(i, "ATTR_PARAM_TYPE") == "9" && tempElements.get(i, "ATTR_PARAM").get(0, "PARAM_VERIFY_SUCC") == "false") {
			alert(tempElements.get(i, "ELEMENT_NAME") + ",\u7f3a\u5c11\u670d\u52a1\u53c2\u6570\uff0c\u8bf7\u8865\u5168\u76f8\u5173\u670d\u52a1\u53c2\u6570\u4fe1\u606f\uff01");
			return false;
		}
	}
	if (typeof (validateParamPage) == "function") {
		if (!validateParamPage("CrtMb")) {
			return false;
		}
	}
	
	if(!$.validate.verifyAll("productInfoPart")){
		return false;
	}
	var submitData = selectedElements.getSubmitData();
	if (!submitData) {
		return false;
	} 
	$("#SELECTED_ELEMENTS").val(submitData);
//	$.beginPageLoading('业务验证中....');
//	var checkParam = "&SERIAL_NUMBER=" + $("#MEB_SERIAL_NUMBER").val() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&USER_ID_B=" + $("#MEB_USER_ID").val()+ "&USER_ID=" + $("#GRP_USER_ID").val() + "&BRAND_CODE_B=" + $("#MEB_BRAND_CODE").val() + "&EPARCHY_CODE_B=" + $("#MEB_EPARCHY_CODE").val() + "&ALL_SELECTED_ELEMENTS=" + tempElements;
//	pageFlowRuleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule", "checkProductInfoRule", checkParam);
	
	return true;
} 

function qryProInfo(){
	$.beginPageLoading();
	$.ajax.submit("QueryCondPart", "initProductInfo", null, "rolePart", 
		function(data){
			$.endPageLoading();
			renderProductInfos(data);
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function initBaseInfo() {
	var lefttabset = new TabSet("lefttabset", "left");
	lefttabset.addTab("产品信息", $("#product")); 
	lefttabset.addTab("产品参数信息", $("#prama"));
	lefttabset.addTab("资源信息", $("#source"));
	lefttabset.draw();
	
	lefttabset.switchTo("产品信息");
	lefttabset.onTabSelect(function(tabset){
		var tab=tabset.getActiveTab();
		if(tab.caption=="产品参数信息") {
		    try {
		    	initCreateMemberParamTable();
		    	initCreateMemberParam();
		    	initMemberParamTable();
		    } catch (error) {
		   	}
		}
	})
	
	if ($('#cond_GROUP_PRODUCT').va() != "") {
	    $("#QueryType").val("2");
        $("#groupProductIdPart").css('display','block');
        $("#cond_SERIAL_NUMBER").attr('disabled',false);	    
	} else {
	    $("#QueryType").value = "1";
        $("#groupProductIdPart").css('display','none');
        $("#cond_SERIAL_NUMBER").attr('disabled',false);
	}
}

function getGroupInfo(){
	var group = $('#POP_cond_GROUP_ID').val();
	if (group == ""){
		alert ('请输入正确的集团编码！');
		return false;
	}else{
		$.beginPageLoading();
		ajaxDirect4CS(this, 'getGroupBaseInfo', '&cond_GROUP_ID='+group, '',true,afterCheckGroupInfo);
	}
}

function afterCheckGroupInfo() {
	var shortNum = this.ajaxDataset.get(0, "GROUPINFO_NUM");
	var productid = this.ajaxDataset.get(0,"PRODUCT_ID");
	var custid = this.ajaxDataset.get(0,"CUST_ID");
	if(shortNum >'0') {
		popupDialog('group.bat.batdeal.QueryGroupProduct', 'queryGroupInfo', '&cond_CUST_ID='+custid+'&cond_PRODUCT_ID='+productid, '集团查询', '600', '240');
	}
}

function returnValue() {
}

function refreshSelectElement(){
    selectedElementsInit('member');
}

function changeQueryType() {
   var choose=$("#cond_QueryType").val();
   if (choose=="1") { //按成员手机号码
      $("#groupProductIdPart").css('display','none');
      $("#memSnPart").css('display','block');
   }
   else if (choose == "2") { //按集团产品编码查询 
      $("#groupProductIdPart").css('display','block');
      $("#memSnPart").css('display','none');
   }        
}
//根据成员手机号查集团
function searchMemberBySerialNumber() {
   var serialValue = $("#cond_MEM_SERIAL_NUMBER").val();
   $.beginPageLoading();
	$.ajax.submit("QueryCondPart", "qryGrpInfoByMebSn", null, "QueryCondPart", 
		function(data){
			$.endPageLoading();
			var result = data.get("RESULT");
			var grpSn = data.get("GRP_SN"); 
			if(result=="true"){
				$.ajax.setup({async:false});
				$("#cond_GROUP_SERIAL_NUMBER").val(grpSn); 
				$("#cond_QueryType").val("2");
				$("#groupProductIdPart").css('display','block');
				$("#memSnPart").css('display','none');
				getGrpInfoBySn();
				$.ajax.setup({async:true});
			}
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
  	}
  );
}


function afterserchMemberBySerialNumber() {
         $('#AUTH_SERIAL_NUMBER').value=$('#cond_SERIAL_NUMBER').val();
         $('#authButton').click();
}

/*
 *判断是否是数字 
 */
function isNumber(obj){
	var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/; 
	if (!reg.test(obj) ){
	alert('服务号码必须为数字，请重新输入！');
		return false;
	}
	if(obj.length <= 0){
	 alert('请输入成员服务号码！');
	 return false;
	}
	return true;
} 
 
function validateSimple(){
   var innerGroupSerial=$("#inner_GROUP_SERIAL").innerText;
   var innerSerial=$("#inner_SERIAL").innerText;
   if (innerGroupSerial == "" || innerSerial == "") {
       alert('请输入集团产品编码和电话号码后，再进行此操作！');
       return false;
   }
   //start add by wangyf for VPMN成员新增时取值有误
   var productSerialValue = $("#cond_GROUP_PRODUCT").val();
   var tempGPValue = $("#temp_GROUP_PRODUCT").val();
	if(tempGPValue != "OK" && tempGPValue != ""){
		if(tempGPValue != productSerialValue){
			alert("集团产品编码已改变,请重新回车后查询集团产品！");
			return false;     				
		}
	}
	//end add by wangyf for VPMN成员新增时取值有误
   if (!validateParamPage('CrtMb')) 
       return false;
       
   return true;
}


function authAfterFunction() {
   var flag = $('#GROUP_AUTH_FLAG').val();
   if (flag == "true") {
       
       //分散改动
       if(!changeAcctDays()) return false;
       var userId = $('#USER_ID').val();
       $('#otherMemSerial').click();
   }
   else
      return false;
}

function clickcheck() {
    var effectNow=$("#effectNow");
	if(effectNow.checked) {
	    $("#ifBooking").value = 'true';
	}
	else {
	   	$("#ifBooking").value = 'false';	    	
	}
}

function changeAcctDays(){
	
	var tradeData=new Wade.DataMap($("#tradeData").value);
	var memberuserid = tradeData.get("MEM_USER_INFO").get("USER_ID","");
	var acctdays = tradeData.get("USER_ACCT_DAY");
	if(acctdays == null)
	    return true;
	var useracctday = acctdays.get(memberuserid);
	
	var acctdaydistrbute = useracctday.get('ACCTDAY_DISTRIBUTION');
	//TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"),FALSE("4")
	if(acctdaydistrbute == "" || acctdaydistrbute == "0"){
	   //自然日出账的用户不做处理，保持和现有的逻辑一样
	   return true;    
	}
	
	var specialProduct = tradeData.get("SPECIAL_PRODUCT","");
	if(specialProduct != "true"){
		return true;
	}
	
	var validacct_day = useracctday.get("ACCT_DAY","");
	var validacct_startdate = useracctday.get("START_DATE","");
	var validacct_enddate = useracctday.get("END_DATE","");
	var novalidacct_day = useracctday.get("NEXT_ACCT_DAY","");
	var novalidacct_startdate = useracctday.get("NEXT_START_DATE","");
	var novalidacct_enddate = useracctday.get("NEXT_END_DATE","");
	var firstdaynextacctday = useracctday.get("FIRST_DAY_NEXTACCT","");
	
	if(acctdaydistrbute =="4" ){
	
       //特殊产品必须自然月生效
          var result = "用户当前的出账日是"+validacct_day+"号,需把出账日改为1号才可办理该集团业务！";
		if(confirm(result+'是否确定变更？')){
			//调用隐藏按钮,触发事件
			$('#changeAcctDay').click();
		    //popupDialog('person.changeacctday.ChangeAcctDay', 'onInitTrade', '&SERIAL_NUMBER='+$('#cond_SERIAL_NUMBER').value+'&AUTH_AUTO=TRUE&ISGRP=TRUE&REMARK=办理集团产品需要变更账期为自然月', '账期变更', '800', '740');
		    
		}
          return false;
    }else if(acctdaydistrbute =="2" || acctdaydistrbute =="3" ){
       alert("用户存在未生效的账期变更，不能办理该业务！");
       return false;
    
    }
     return true;
}

function searchByGroupProductCode() { 
   var serialValue = $("#cond_GROUP_PRODUCT").val();
   //start add by wangyf for VPMN成员新增时取值有误
   $("#temp_GROUP_PRODUCT").val(serialValue);
   //end add by wangyf for VPMN成员新增时取值有误
   $('#GROUP_AUTH_FLAG').val('false');
//   $('#bGPId').click();
   $.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "getGroupInfoByGPId", null, "GroupInfoPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
   	}
   );
}
//通过集团号码查询集团资料成功后调用的方法
function selectGroupBySnAfterAction(data)
{ 
	$("#cond_SERIAL_NUMBER").attr('disabled',false);
	//获取三户资料
    var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
    var productCtrlInfo = data.get("PRODUCT_CTRL_INFO");
    var userInfo =  data.get("GRP_USER_INFO");
    var groupInfo =  data.get("GRP_CUST_INFO");  
    //初始集团用户资料
    if(userInfo != undefined && userInfo != null){
		insertGrpUserList(userInfo);
		insertSelGroupUserInfo(userInfo);
	}
	//初始集团资料信息
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
	//初始产品描述信息
	if(productExplainInfo != undefined && productExplainInfo != null){
//		insertProductExplainInfo(productExplainInfo);
//		insertProductCtrlInfo(productCtrlInfo);
	}
	//初始树
//	initTreeByProductInfo($('#GRP_PRODUCT_NAME').val(),$('#GRP_PRODUCT_ID').val());
//	//初始付费信息
//	renderPayPlanSel($('#GRP_PRODUCT_ID').val(),$('#GRP_USER_ID').val());
 
	//显示右侧区域
//	seeCompProductRefreshPart();
} 

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();   
}

//成员号码资料查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
  if(data == null)
	return;
	
	//异地号码判断
  if(!afterCheckInfo(data)) 
  	return false;
  var memcustInfo = data.get("MEB_CUST_INFO");
  var memuserInfo = data.get("MEB_USER_INFO"); 
  var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO");
  insertMemberCustInfo(memcustInfo);
  insertMemberUserInfo(memuserInfo); 
  insertUserAcctDayInfo(memuseracctdayInfo);
  //刷新产品信息 
  qryProInfo();
  
}
//刷新产品元素信息
function renderProductElements(data){ 
  var dynParam = data.get("DYN_PARAM");   
  var productId = dynParam.get("PRODUCT_ID");//$('#PRODUCT_ID').val();
  var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();
  var mebUserId = $('#MEB_USER_ID').val();
  var cond = data.get("COND");
  var pkgParam = data.get("PKG_PARAM");   
  selectedElements.setAcctDayInfo(cond.get("ACCT_DAY"),cond.get("FIRST_DATE"),cond.get("NEXT_ACCT_DAY"),cond.get("NEXT_FIRST_DATE"));
 
  if(productId != '' && mebEparchyCode != '' && mebUserId!= '' ){ 
	    var pkgparam = "&GRP_USER_ID="+pkgParam.get('GRP_USER_ID')+"&USER_EPARCHY_CODE="+pkgParam.get('USER_EPARCHY_CODE');
  		packageList.renderComponent(productId,mebEparchyCode,pkgparam);
  		pkgElementList.initElementList(null); 
  		var data="&USER_ID="+mebUserId+"&PRODUCT_ID="+productId;
		selectedElements.renderComponent(data,mebEparchyCode);
  }
  
}
/**
 * 点击左侧包之后,执行的自定义方法
 */
function pkgListAfterSelectAction(pkg) {
	var selfParam = "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&PRODUCT_ID=8000";// + $("#PRODUCT_ID").val();
	var eparchyCode = $("#MEB_EPARCHY_CODE").val(); 
	pkgElementList.renderComponent(pkg, eparchyCode, selfParam);
}
function renderProductParams(data){  
  var dynParam = data.get("DYN_PARAM");  
  var productId = dynParam.get("PRODUCT_ID");//$('#PRODUCT_ID').val();
  var grpUserId = dynParam.get("GRP_USER_ID");
  $.beginPageLoading();
  $.ajax.submit(null,null, '&BUSI_TYPE=CrtMb&IS_RENDER=true&PRODUCT_ID='+productId+'&GRP_USER_ID='+grpUserId,'productParamPart,resPart',function(data){
	  $.endPageLoading(); 
	},
	function(error_code,error_info, derror){
		selectMemberInfoAfterErrorAction();
		$.endPageLoading();  
		showDetailErrorInfo(error_code,error_info,derror);
	});
  
  
}

//刷新产品相关信息
function renderProductInfos(data){
  $.ajax.setup({async:false});
  renderProductElements(data);
  $.ajax.setup({async:true});
  renderProductParams(data);
  
}
//成员资料查询失败后调用的方法
function selectMemberInfoAfterErrorAction() {

	clearMemberCustInfo();
	clearMemberUserInfo(); 
	clearUserAcctDayInfo();
}
//判断 异地号码的后续处理
function afterCheckInfo(data){
  var result = data.get("OUT_PHONE","false");;
  if(result == "true"){
  	if(!(confirm("请注意：该号码是异地号码，是否继续成员新增?"))){
  	    //选择取消则退出办理
	    $('#cond_SERIAL_NUMBER').val();
	    selectMemberInfoAfterErrorAction();
	    return false;
	}
  }
  return true;
}
function clear()
{	  
	var comp=$('#CompProductRefreshPart');
	comp.css("display","none");
}

function selectPayPlanSelAfterAction(){
	setDiverTipCtrMb();
}