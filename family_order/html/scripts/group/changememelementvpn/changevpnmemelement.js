
function searchMemberBySerialNumber() {
   var serialValue = $("#cond_SERIAL_NUMBER").val();

  var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/; 
	
	if (!reg.test(serialValue) ){
		alert('服务号码必须为数字，请重新输入！');
		return false;
	}
	if(serialValue.length <= 0){
	 	alert('请输入成员服务号码！');
	 	return false;
	}
  	var flag = $('#GROUP_AUTH_FLAG').val();
   
    $('#TRADE_TYPE_CODE').val("3035");
    
    ajaxDirect4CS("group.changememelementvpn.changevpnmemelement","checkMemberInfo","&cond_SERIAL_NUMBER="+serialValue, null,false,aftersearchMemberBySerialNumber);
    
    return false;
    
   	
}

function aftersearchMemberBySerialNumber() {
    $('#TRADE_TYPE_CODE').val("3035");
	$('#AUTH_SERIAL_NUMBER').value=$('#cond_SERIAL_NUMBER').val();
    $('#authButton').click();
}

function authAfterFunction() {
	var flag = $('#GROUP_AUTH_FLAG').val();
	if (flag == "true") {
	var nvf=Wade.nav.getActiveNavFrame();
	nvf=nvf?nvf:parent;
		if(nvf){
			//var serialValue = $("#cond_SERIAL_NUMBER").val();
        	//Wade.page.beginPageLoading();
        	 $('#otherMemSerial').click();
			//redirectTo(this,'queryMemberInfo','&cond_SERIAL_NUMBER='+serialValue,'currentframe');
       	}
   	}
   	else {
   		return false;
   	}
      
}

function clickcheck(){
	var checkflag = $("#effectNow").checked;
	if(checkflag == true){
		setEffectNow("member");
	}
	else{
		setUnEffectNow("member");
	}
}

function initProductInfo() {
	var lefttabset = new TabSet("lefttabset", "left");
	lefttabset.addTab("产品信息", $("#product"));
	if ($("#pramaPage").value!="X")
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
}

function opertionResList(resType, resValue){
	var old = $("#GRP_OLDRES_RECORD");
	var ne = $("#GRP_NEWRES_LIST");
	var oldRecord = new Wade.DatasetList(old.val());
	var newList = new Wade.DatasetList(ne.val());
	var obj = new Wade.DataMap();
	obj.put("RES_TYPE_CODE", resType);
	obj.put("RES_CODE", resValue);
	obj.put("STATE","ADD");
	oldRecord.each(function(item,index,totalcount){
			if (item.get("RES_TYPE_CODE")==obj.get("RES_TYPE_CODE")){
				obj.put("STATE","MODI");
			}
		});
	newList.each(function(item,index,totalcount){
			if (item.get("RES_TYPE_CODE")==obj.get("RES_TYPE_CODE")){
				newList.removeAt(index);
			}
		});
	newList.add(obj);
	ne.value=newList.toString();
}
//huminghua 20111201 for 4A金库模式做的修改 start
//4A认证
function check4A(){
   var fun = check4Asuccess ; 
   var Checkflag4a=$('#CheckFlag4a').val();
   if(Checkflag4a=="false")
   {
	   if(Wade.nav&&Wade.nav.getFrame("sidebarframe"))
	   {
		  Wade.nav.getFrame("sidebarframe").check4AAudit('GRP_4A_ChangeVpnMember',"check4Asuccess",true,"check4Afail");
	   }
		return false;
	}else{
    	return true;
    }
}

//通过4A认证时调用下面方法, 模拟点击事件考虑IE与火狐浏览器
function check4Asuccess(){
	$('#CheckFlag4a').val("true");
	if(document.all){  
      	document.getElementById('bsubmit').click();         
    }  
    else{
	    var evt = document.createEvent("MouseEvents");  
	    evt.initEvent("click", true, true);  
	    document.getElementById('bsubmit').dispatchEvent(evt);  
    }  
}

//没通过4A认证时调用下面方法
function check4Afail(){
	alert("对不起，4A认证失败！");
}
//huminghua 20111201 for 4A金库模式做的修改 end
// j2ee 以上老代码暂保留
//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data){ 
	if(data == null)
		return;
	
	insertGroupUserInfo(data.get("GRP_USER_INFO"));
	
//	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
//	var orderInfos = data.get("ORDERED_GROUPINFOS");
//	insertMemberCustInfo(memcustInfo);
	insertMemberUserInfo(memuserInfo); 
	//刷新产品信息 
	qryProInfo();
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
//刷新产品相关信息
function renderProductInfos(data){
  $.ajax.setup({async:false});
  renderProductElements(data);
  $.ajax.setup({async:true});
  renderProductParams(data);
  
}
//刷新产品元素信息
function renderProductElements(data){ 
  var dynParam = data.get("DYN_PARAM");   
  var productId = dynParam.get("PRODUCT_ID");//$('#PRODUCT_ID').val();
  var mebEparchyCode = $('#MEB_EPARCHY_CODE').val();
  var mebUserId = $('#MEB_USER_ID').val();
  var cond = data.get("COND");
  var pkgParam = data.get("PKG_PARAM");  
  var grpUserId = pkgParam.get('GRP_USER_ID');
  selectedElements.setAcctDayInfo(cond.get("ACCT_DAY"),cond.get("FIRST_DATE"),cond.get("NEXT_ACCT_DAY"),cond.get("NEXT_FIRST_DATE"));
 
  if(productId != '' && mebEparchyCode != '' && mebUserId!= '' ){ 
	    var pkgparam = "&GRP_USER_ID="+grpUserId+"&USER_EPARCHY_CODE="+pkgParam.get('USER_EPARCHY_CODE');
  		packageList.renderComponent(productId,mebEparchyCode,pkgparam);
  		pkgElementList.initElementList(null); 
  		var data= "&PRODUCT_ID="+productId+"&MEB_USER_ID="+mebUserId+"&USER_ID="+mebUserId+"&GRP_USER_ID="+grpUserId; 
		selectedElements.renderComponent(data,mebEparchyCode);
  }
  
}
function renderProductParams(data){ 
  var dynParam = data.get("DYN_PARAM");  
  var productId = dynParam.get("PRODUCT_ID");//$('#PRODUCT_ID').val();
  var grpUserId = dynParam.get("GRP_USER_ID");
  var memUserId = dynParam.get("MEB_USER_ID");
  var memEparchyCode = dynParam.get("MEB_EPARCHY_CODE");
  $.beginPageLoading();
  $.ajax.submit(null,null, '&BUSI_TYPE=ChgMb&IS_RENDER=true&PRODUCT_ID='+productId+
		  '&GRP_USER_ID='+grpUserId+'&MEB_USER_ID='+memUserId+'&MEB_EPARCHY_CODE='+memEparchyCode,'productParamPart',function(data){
	  $.endPageLoading();
	   $.ajax.setup({async:false});
	   initMemParamInfo();
  	   $.ajax.setup({async:true});
	});
  
  
}

//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction(){
  
//  clearMemberCustInfo();
  clearMemberUserInfo();
  clearGroupUserInfo();
//  clearGroupCustInfo();
  
}
 
function onSubmitBaseTradeCheck(){

	var flag = $("#SKIP_FORCE_PACKAGE_FOR_PRODUCT").val();
	if(flag!="1"){
		//bixuanbao check
		if(!selectedElements.checkForcePackage())
			return false;
	}
	
	var tempElements = selectedElements.selectedEls;
    if(!tempElements)
   		return false;
    for (var i=0;i<tempElements.length ;i++ )
    {
     if(tempElements.get(i,"MODIFY_TAG")=='0'&&tempElements.get(i,"ATTR_PARAM_TYPE")=='9'&&tempElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
	 {
		alert(tempElements.get(i,"ELEMENT_NAME")+",缺少服务参数，请补全相关服务参数信息！"); 
		return false ; 
      }
		    
    }
    
    if(!$.validate.verifyAll('productInfoPart')) return false; 
    
    if(typeof(validateParamPage)=="function"){
		if (!validateParamPage('ChgMb')) return false
	}
    
    var submitData = selectedElements.getSubmitData();
	if (!submitData) {
		return false;
	} 
	$("#SELECTED_ELEMENTS").val(submitData);

//	var authTag = $("#GROUP_AUTH_FLAG").val();
//	if(authTag!= 'true'){
//		alert('号码未验证，请验证！');
//		groupAuthStart();
//		return false;
//	}
	
//    $.beginPageLoading('业务验证中....');
//    var checkParam = '&SERIAL_NUMBER='+$('#MEB_SERIAL_NUMBER').val()+'&PRODUCT_ID='+$('#PRODUCT_ID').val()+'&USER_ID_B='+$('#MEB_USER_ID').val()+'&USER_ID='+$('#GRP_USER_ID').val() +'&EPARCHY_CODE_B='+$("#MEB_EPARCHY_CODE").val()+'&ALL_SELECTED_ELEMENTS='+ tempElements+'&SKIP_FORCE_PACKAGE_FOR_PRODUCT='+flag ;
//	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ChangeMemElementRule','checkProductInfoRule',checkParam);
	
	return true;
} 

//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoErrAfterAction();
	
	if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		queryMemberInfo();
		return;
	}
}