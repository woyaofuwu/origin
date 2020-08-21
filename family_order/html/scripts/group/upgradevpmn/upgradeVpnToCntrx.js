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
	//查询集团VPN信息
	qryVpnInfo();
} 

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();   
}

function qryVpnInfo(){
	$.beginPageLoading();
	$.ajax.submit("GrpUserInfoPart", "queryVpnInfos", null, "VpmnPart,userGrpPackage,codePart,offerListPart", 
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
//  $.ajax.submit(null,null,svcParam,"useTagInfoPart");
//  data.put('grpProductId',);
//  alert('userGrpPackage data:'+data);
//  $.ajax.submit(null,null,data,'userGrpPackage');
}

//刷新产品元素信息
function renderProductElements(data){ 
//  var dynParam = data.get("DYN_PARAM");   
  var productId = data.get("PRODUCT_ID");
  var grpEparchyCode = $('#GRP_USER_EPARCHYCODE').val();
  var cond = data.get("COND");
  var pkgParam = data.get("PKG_PARAM");  
   
  selectedElements.setAcctDayInfo(cond.get("ACCT_DAY"),cond.get("FIRST_DATE"),cond.get("NEXT_ACCT_DAY"),cond.get("NEXT_FIRST_DATE"));
 
  if(productId != '' && grpEparchyCode != ''){ 
	  var data="&PRODUCT_ID="+productId;
//	  packageList.renderComponent(productId,grpEparchyCode,data);
//	  pkgElementList.initElementList(null);  
	  selectedElements.renderComponent(data,grpEparchyCode);
  }
}

/**
 * 设置USER_EPARCHYCODE
 * 
 */ 
 function getOtherParam(){  
    var eparchyCode=$("#GRP_USER_EPARCHYCODE").val();
    var param="&USER_EPARCHY_CODE="+eparchyCode;
    return param;
    
    
} 
 
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
		 
		
		if(!$.validate.verifyAll("productInfoPart")){
			return false;
		}
		var submitData = selectedElements.getSubmitData();
		if (!submitData) {
			return false;
		} 
		$("#SELECTED_ELEMENTS").val(submitData);
		
		if(typeof(commparaGrpPackageElements)=="function"){
			//判断是否必选包已经定制上
			if(!checkUserGrpPkgForce()) return false;
			//生成定制信息
			commparaGrpPackageElements();
			
		}
		$.beginPageLoading('业务验证中....');
//	j2ee	var checkParam = "&SERIAL_NUMBER=" + $("#MEB_SERIAL_NUMBER").val() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&USER_ID_B=" + $("#MEB_USER_ID").val()+ "&USER_ID=" + $("#GRP_USER_ID").val() + "&BRAND_CODE_B=" + $("#MEB_BRAND_CODE").val() + "&EPARCHY_CODE_B=" + $("#MEB_EPARCHY_CODE").val() + "&ALL_SELECTED_ELEMENTS=" + tempElements;
//	j2ee	pageFlowRuleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule", "checkProductInfoRule", checkParam);
		
		return true;
	} 
 
//业务规则验证成功后，需要把选择的产品元素保存到缓存中
function pageFlowCheckAfterAction(){

 	var submitData = selectedElements.getSubmitData();
 	if(!submitData)
 		return false;
     $('#SELECTED_ELEMENTS').val(submitData);
 	//将页面中的元素数据保存到缓存中
 	var selectparam  = '&SELECTED_ELEMENTS='+submitData+'&ID='+$("#CUST_ID").val()+'&PRODUCT_ID='+$("#PRODUCT_ID").val()+'&TRADE_TYPE_CODE='+$("#TRADE_TYPE_CODE").val();
 	var grppackagelist  =  $("#SELECTED_GRPPACKAGE_LIST").val();
 	if(typeof(grppackagelist) != 'undefined'){
 		selectparam = selectparam +'&SELECTED_GRPPACKAGE_LIST='+grppackagelist;
 	}
 	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupCacheUtilHttpHandler','saveSelectElementsCache',selectparam,'','',{async:false});
    
 	return true;

 }
//////////////////////////*********** j2ee 下老上新 ************
function qryClick(){
	var serNum = $('#cond_SERIAL_NUMBER').val();;
	
	if(serNum.trim() != "") {
		ajaxDirect4CS(this, 'queryUserInfos', '&cond_SERIAL_NUMBER='+serNum, 'refreshPart', false, '');
	}else {
		alert('请输入集团服务号！');
		return false;
	
	}
	
	
}

function checkDiscnt() {
	var Discnt = $('#DISCNT_CODE').val();;
	if(Discnt.trim() == "") {
		alert('请选择优惠！');
		return false;
	}else{
		return true;
	}
}

function checkStaffRight() {
	var serNum = $('#cond_SERIAL_NUMBER').val();;
	var serialNumber = $('#SERIAL_NUMBER').val();; 
	if(serNum != serialNumber) {
		alert("\u8D44\u6599\u5F02\u5E38\uFF0C\u8BF7\u91CD\u65B0\u8F93\u5165\u96C6\u56E2\u4EA7\u54C1\u7F16\u7801\u67E5\u8BE2\u7528\u6237\u8D44\u6599\uFF01");
		return false;
	}
	else {
		return true;
	}
}


function clickcheck(){
	var checkflag = $("#effectNow")[0].checked;
	if(checkflag == true){
		setEffectNow("main");
	}
	else{
		setUnEffectNow("main");
	}
}

//控制产品树保持在可视区域内
function controlProductTree()
{
	var productList=System.get("productList");
	var xy=[0,0];
	if(productList){
		productList.position("absolute");
		productList.setWidth("200");
		xy=productList.getXY();
	}
	
	var e_wrapper=System.get("e_wrapper");
	e_wrapper.on("scroll",function(){
		var sz=this.getScroll();
		 if(sz.top>xy[1]){
		 	productList.setTop(sz.top +10);
		 }
		 if(sz.top<=10){
		 	productList.setTop(xy[1]);
		 }
		
	});
};

function setPasswd() {

}

function initProductInfo() {
	//控制产品树保持在可视区域内
	controlProductTree();
	
	var lefttabset = new TabSet("lefttabset", "left");

	lefttabset.addTab("产品信息", $("#product"));	
	if ($("#pramaPage").val()!="X")
	    lefttabset.addTab("产品参数信息", $("#prama"));
	lefttabset.addTab("资源信息", $("#source"));
	lefttabset.draw();
	
	lefttabset.switchTo("产品信息");
	lefttabset.onTabSelect(function(tabset){
		var tab=tabset.getActiveTab();
		if(tab.caption=="产品参数信息") {
		    try {
		        createUserParamTabset();
		    } catch (error) {
		    }
		}
	})
}

function checkProductElements() 
{
   var checkflagset=$("#PRODUCT_ELEMENTS_CHECK_FLAG").val();
	var flaglist = checkflagset.split(","); 
	for(i=0;i<flaglist.length;i++)
	{
	
		var flag=flaglist[i];
		if(flag=="checkPlatServers")
		{
		  if(!checkPlatServers())
		  {
		    return false;
		  }
		}
	}
 	return true;
}

function checkPlatServers() 
{
  var selectElements=$("#SELECTED_ELEMENTS").val();
  var needcheckservices=$("#NEED_CHECK_SERVICES").val();
  var needchecklist= needcheckservices.split(","); 
  var selectElementset = new Wade.DatasetList(selectElements);
  var svcDataset = new Wade.DatasetList(); // 用户本身服务信息
  var grpserverPackageset =  new Wade.DatasetList(); // 成员定制的服务信息

  for(i=0;i<selectElementset.getCount();i++)
  {
 
   	  var elementData=selectElementset.get(i);
   	 var productMode=elementData.get("PRODUCT_MODE");
   	 var elementsDataset = elementData.get("ELEMENTS"); // 取元素
   	 
   	 for(j=0;j<elementsDataset.getCount();j++)
   	 {
   	 	var packageData = elementsDataset.get(j); // 取每个元素
   	 	var elementType = packageData.get("ELEMENT_TYPE_CODE", "");
   	    if((productMode=="10"||productMode=="11")&&elementType=="S")//处理用户级的服务元素
   	 	{
   	 		svcDataset.add(packageData);
   	 	}
   	 	else if((productMode=="12"||productMode=="13")&&elementType=="S")//处理成员级的服务元素
   	 	{
   	 		grpserverPackageset.add(packageData);
   	 	}
   	 }
   	
  }

  for(i=0;i<grpserverPackageset.getCount();i++)
  {
  	var packageData=grpserverPackageset.get(i);
  	var packserverId=packageData.get("ELEMENT_ID");
  
  	for(k=0;k<needchecklist.length;k++)
  	{
  	   if(packserverId==needchecklist[k])//过滤为只有需要校验的服务 才执行校验
  	   {
  	   		var compareflag="";
  	   		for(j=0;j<svcDataset.getCount();j++)
  			{
  				var svcdata=svcDataset.get(j);
  				var svcServerId=svcdata.get("ELEMENT_ID");
  				if(packserverId==svcServerId)
  				{
  					compareflag="true";
  					break;
  				}	
  			}
  			
  			if(compareflag!="true")
  			{
  				var errormsg="定制成员产品的 ["+packageData.get("ELEMENT_NAME")+"] 服务 \n必选订购集团产品的 ["+packageData.get("ELEMENT_NAME")+"] 服务";
  				alert(errormsg);
  				return false;
  		 	}
  		}
  		
  		
  	 }
   }

  return true;
}

function opertionResList(resType, resValue){
	var old = $("#GRP_OLDRES_RECORD");
	var ne = $("#GRP_NEWRES_LIST");
	var oldRecord = new Wade.DatasetList(old.value);
	var newList = new Wade.DatasetList(ne.value);
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

function checkRes(obj){

    var temValue = obj.val();
    var code = $('#HIDDENRESULT');
    if(code.value !=''){         
       var deleobj = new Wade.DataMap();
	     deleobj.put("RES_TYPE_CODE", '0');
	     deleobj.put("RES_CODE", code.value);
	     deleobj.put("CHECKED","true");
	     deleobj.put("DISABLED","true");
	     deleteRes(deleobj);
     }
    code.value = "";
    if (temValue == code.val() || temValue.length<11){
	  alert("请输入正确的手机号码！");
	  return false;
	}
 
    ajaxSubmit(this, 'checkResourceInfo', '&res_RES_TYPE=0&res_RES_VALUE='+temValue, 'ResPart','',null,function(){
    	afterCheckRes(code,temValue,this.ajaxDataset);
    });
}
