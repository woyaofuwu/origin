/*$Id:$*/
var PAGE_FEE_LIST = $.DataMap();

/*备注信息特殊字符校验*/
function checkRemark() {
	var remark = $("#REMARK").val();
	if(remark=="") {
		return true;
	}
	
	var textvalue = remark;
	var a = new Array('\\','\"','\/','{','}' );
	for(var i = 0; i < a.length; i++) {
		if(textvalue.indexOf(a[i]) >= 0) {	
			alert('请不要输入特殊字符！[' + a[i] + ']');
			$("#REMARK").focus();
			return false;
		}
	}
	
	return true;	
}


//根据界面上的勾选值 来拼接
function getPostValue()
{  
     var typeContents = $("*[name=POST_TYPESET]");
     var postTypeContents = $("*[name=POSTTYPE_CONTENT]");
     var typeContent="";
     var postTypeContent="";
       typeContents.each(function(){
    		 	if(this.checked){
    		 	 typeContent+=this.value;
    		 	}
    		});
    	postTypeContents.each(function(){
    		 	if(this.checked){
    		 	 postTypeContent+=this.value;
    		 	}
    		});
   $("#postInfo_POST_TYPESET").val(typeContent);
   $("#postInfo_POSTTYPE_CONTENT").val(postTypeContent);
}

/**
 * 检查是否没有一个被选中
 */
var isCheckBox= function(objId){
	var obj = $("*[name=" + objId +"]");
	if(obj&&obj.length){
		for(var i=0;i<obj.length;i++){
			if(obj[i].checked)
				return true;
		}
	}
	return false;
};


//点击确定按钮时的检查方法
function checkPost()
{       
    if($("#POST_TAG")==null || $("#POST_TAG").val() =="" )
    {
          alert("请选择邮寄标志！");
		  return false;
    }else if ($("#POST_TAG").val()=="0")
    { 
          return true;
    }else
    {   
	    var postTypes = $("*[name='POST_TYPESET']");
		if(!isCheckBox("POST_TYPESET")){
			alert("请选择邮寄方式！");
			return false;
		}
		
		//邮递周期不能为空		
		if($("#POST_CYC").val() =="" || $("#POST_CYC").val() ==null){
			alert("邮递周期不能为空，请选择邮递周期！");	
			return false;
		}
		
		if(postTypes[0] && postTypes[0].checked){ //检查邮政投递的方式是否已选
			if (!isCheckBox("POSTTYPE_CONTENT"))
			{
				alert("请选择邮政投递的内容！");
				return false;
			}
		}
		return true;	 
    }    
}

/*********************************邮寄资料函数 结束*************************/
/*隐藏或显示基本信息区*/
function checkBaseInfo(btn,o) {
	var button = btn;
	var div = document.getElementById(o);
	if (div.className != "")
	{
		div.className = "";
		button.children[0].className = "e_ico-hide";
		button.children[1].innerHTML = "隐藏非必填项";
	}
	else {
		div.className = "e_hide-x";
		button.children[0].className = "e_ico-show";
		button.children[1].innerHTML = "显示非必填项";
	}
	
}

/*************************************公用方法 开始************************************/


  
/*去掉空格*/
function trim(str) {
	return str.replace(/^\s+|\s+$/,'');
}

/*检查非空*/
function isNull(str) {
	if(str==undefined || str==null || str=="") {
		return true;
	}
	return false;
}
/*显示一块区域*/
function showLayer(optionID) {
	$("#" + optionID).css("display","");
}

/*隐藏一块区域*/
function hideLayer(optionID) {
	$("#" + optionID).css("display","none");
}

/*************************************公用方法 结束************************************/

function onSubmit(){
   if(!verifyAll('BaseInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('OtherInfoPart'))
   {
	   return false;
   } 
   if(!verifyAll('PostInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('CustInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('AcctInfoPart'))
   {
	   return false;
   }
    if(!verifyAll('PasswdPart'))
   {
	   return false;
   } 
    var data = selectedElements.getSubmitData();
    var param = "&SELECTED_ELEMENTS="+data.toString();

    $.cssubmit.addParam(param);
    getPostValue();
    if($("#B_REOPEN_TAG").val()=='1'){
	    $.tradeCheck.setParam("ID",$("#USER_ID").val());
	    $.tradeCheck.setParam("USER_ID",$("#USER_ID").val());
	    $.tradeCheck.setParam("CUST_ID",$("#CUST_ID").val());
	    $.tradeCheck.setParam("PRODUCT_ID",$("#PRODUCT_ID").val());
	    $.tradeCheck.setParam("BRAND_CODE",$("#BRAND_CODE").val());
	    $.tradeCheck.setParam("X_CHOICE_TAG","1");
	    $.tradeCheck.setParam("TRADE_TYPE_CODE","10");  
	    $("#CSSUBMIT_BUTTON").attr("cancelRule","false");
     }   
     return  confirmParamAll();
}

/*封装confirmAll，处理特殊情况
*1、不同证件类型，不同的检查
*2、当需要后台校验的先执行ajax且其值不为空，再做客户端校验	
*/
function confirmParamAll(){
  if($("#B_REOPEN_TAG").val()!='1'){
	   	if($("#IMSI").val()==''){
		 alert('IMSI为空！');
		 return false;
		}
		
		if($("#KI").val()==''){
		 alert('KI为空！');
		 return false;
		}
	}
	var agentId = $("#AGENT_DEPART_ID");//开户代理商编码
	if(agentId.length){//不为undefined，表示代理商开户
	  if(agentId.val()==''){
	    alert('请选择开户代理商！');
	    return false;
	  }
		
	}
	if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))return false;
	if(!$.validate.verifyField($("#SIM_CARD_NO")[0]))return false;
	

	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	if(checkResultCode=="-1"){
		alert("新开户号码校验未通过！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	if(checkResultCode=="0"){
		alert("SIM卡号校验未通过！");
		$("#SIM_CARD_NO").focus();
		return false;
	}
	/*if(checkPsptCode != "1"){
	   alert("证件号码校验未通过！");
	   $("#PSPT_ID").focus();
	   return false;
    }*/
	if($("#B_REOPEN_TAG").val()=='1'){
	  if($("#PSPT_TYPE_CODE").val()==$("#OLD_PSPT_TYPE_CODE").val()){
	     alert("二次开户，证件类型必须修改！");
	     return false;
	  }
	  
	  if($("#PSPT_ID").val()==$("#OLD_PSPT_ID").val()){
	     alert("二次开户，证件号码必须修改！");
	     return false;
	  }
	
	}
	if($("#REAL_NAME").val()=='1')
	{
		if($("#PSPT_TYPE_CODE").val()=="Z")
		{
			alert("实名制开户，证件类型不能为其他，请重新选择！");
			return false;
		}
		var psptId = $("#PSPT_ID").val();
		if(psptId=="0"||psptId=="00"||psptId=="000"||psptId=="0000"||psptId=="00000"||psptId=="1"||psptId=="11"||psptId=="111"||psptId=="1111"||psptId=="11111"||psptId.indexOf("11111111")>-1)
		{
			alert("实名制开户，证件号码过于简单，请重新输入！");
			return false;
		}
		/*if($("#CUST_NAME").val().indexOf("海南通")>-1)
		{
			alert("实名制开户，客户名称不能为海南通，请重新输入！");
			$("#CUST_NAME").val("");
			return false;
		}*/
		if(!confirm('您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！')){
			return  false;
		}
	}
    if($("#REAL_NAME").val()=="1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() != "true"){
        alert("实名制开户数目校验未通过！");
        return false;
    }
	return checkPost();
}
/*初始化产品*/
function initProduct(){
             packageList.renderComponent("",$("#EPARCHY_CODE").val());
            pkgElementList.initElementList(null);
			selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
			$("#PRODUCT_NAME").val('');
}
/*设置用户产品品牌*/
function setBrandCode(){
	if($("#PRODUCT_TYPE_CODE").val()!=""){
	    $("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	}else{
		$("#BRAND").val('');
	}
	
}
function checkBeforeProduct(){
	//CHECK_RESULT_CODE:服务号码与SIM校验结果:0:服务校验通过，1:SIM卡校验通过，初始值为-1
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	var checkPsptCode  =    $("#CHECK_PSPT_CODE").val();
	if(checkResultCode=="-1"){
		alert("新开户号码校验未通过！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	if(checkResultCode=="0"){
		alert("SIM卡号校验未通过！");
		$("#SIM_CARD_NO").focus();
		return false;
	}
 if(!verifyAll('BaseInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('CustInfoPart'))
   {
	   return false;
   }
   if(!verifyAll('AcctInfoPart'))
   {
	   return false;
   }
  if(!verifyAll('OtherInfoPart'))
   {
	   return false;
   } 
   if(!verifyAll('PostInfoPart'))
   {
	   return false;
   } 
   if(!verifyAll('PasswdPart'))
   {
	   return false;
   } 
   //产品组件传值，如果有则只取传递产品 sunxin
   var assignProductIds ="";
   var inparam ="";
   //147号码判断
   if($("#SERIAL_NUMBER").val().indexOf('147')==0){
      $.ajax.submit(null, 'getProductForSpc', inparam, null, function(data) {
			    $.cssubmit.disabledSubmitBtn(false);
			    for(var i = 0; i < data.getCount(); i++) {
			  	     var data0 = data.get(i);
			  	     if(data0){  
				              if(i=="0")
				                  assignProductIds= data0.get("PARA_CODE1");
				              else
				                  assignProductIds= assignProductIds+","+data0.get("PARA_CODE1");	
						}
			  	}
			ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),null,assignProductIds);
	           },
	           function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
				});
   }
  else if($("#INFO_TAG").val()=="1"){
   $.ajax.submit(null, 'getProductForNet', inparam, null, function(data) {
			    $.cssubmit.disabledSubmitBtn(false);
			    for(var i = 0; i < data.getCount(); i++) {
			  	     var data0 = data.get(i);
			  	     if(data0){  
				              if(i=="0")
				                  assignProductIds= data0.get("PARA_CODE1");
				              else
				                  assignProductIds= assignProductIds+","+data0.get("PARA_CODE1");	
						}
			  	}
			ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),null,assignProductIds);
	           },
	           function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
				});
		}		
   else
  	 ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
}
/*设置产品目录是否可用与产品类型编码*/
function setProductType(obj){
	if(obj.value!=''){
		if(getElement("B_PRODUCT_MODE").value=="true"){
			disabled(getElement('productselectbutton'),true);
		}else{
			disabled(getElement('productselectbutton'),false);
		}
	}else{
		disabled(getElement('productselectbutton'),true);
	}
	getElement('productselectbutton').productTypeCode=obj.value;
}
/* 处理产品选择后的js校验 需要添加如果是网上选号，则一些产品不能办理！ sunxin   //获取只有网上选号才能办理的产品id
*		IDataset tmpSet = ParamQry.getCommpara(pd, "CSM", "7639", "netchoose_phone_product", "0898");
*    还需要处理如果选择了国际长途 在页面显示发票输入框,并且添加押金到费用列表
*/
function afterChangeProduct(productId,productName,brandCode,brandName){
          $.feeMgr.clearFeeList("10");
          //处理如果选择了国际长途 在页面显示发票输入框,并且添加押金到费用列表
          
		  $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
          $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
         var feeData = $.DataMap();
          	 /*feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "0");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","10");
			
			$.feeMgr.insertFee(feeData);	
			
			feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "2");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","10");
			$.feeMgr.insertFee(feeData);	*/

           $("#PRODUCT_ID").val(productId);
           $("#PRODUCT_NAME").val(productName);
            var param = "&NEW_PRODUCT_ID="+productId;
            packageList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
            pkgElementList.initElementList(null);
			selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
			var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
			//处理是否选择国际漫游 sunxin 还需要在选择元素的时候进行判断
			var length = selectedElements.selectedEls.length;
			for(var i=0;i<length;i++){
				var temp = selectedElements.selectedEls.get(i);
				if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="0" && temp.get("ELEMENT_TYPE_CODE")=="S")
				{
				  inparam+="&ELEMENT_ID=15";
				}
			}
			$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
			    $.cssubmit.disabledSubmitBtn(false);
			  	for(var i = 0; i < data.getCount(); i++) {
			  	     var data0 = data.get(i);
				     if(data0){
								feeData.clear();
								feeData.put("MODE", data0.get("FEE_MODE"));
								feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
								feeData.put("FEE",  data0.get("FEE"));
								feeData.put("PAY",  data0.get("FEE"));		
								feeData.put("TRADE_TYPE_CODE","10");							
								$.feeMgr.insertFee(feeData);			
						}
			  	}
		
	           },
	           function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
				});
			

}


function disableElements(data){
 if($("#B_REOPEN_TAG").val()=='1'){
         selectedElements.disableAll();
  }else{
   if(data){
     var temp = data.get(0);
     if(data.get(0).get("NEW_PRODUCT_START_DATE")){
			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
	}
   }
  }
}

/*************************************产品搜索引擎函数 开始************************************/

function changeSearchType(eventObj){
			var searchType = eventObj.value;
			var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
			param += "&SEARCH_TYPE="+searchType;
			if(searchType == "2"){
				param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
			}
			$.Search.get("productSearch").setParams(param);
}
function searchOptionEnter(){
			var searchType = $("#productSearchType").val();
			var searchLi = $("#Ul_Search_productSearch li[class=focus]");
			if(searchType == "1"){
				//产品搜索
				var productId = searchLi.attr("PRODUCT_ID");
				var productName = searchLi.attr("PRODUCT_NAME");
				var brandCode = searchLi.attr("BRAND_CODE");
				var brandName = searchLi.attr("BRAND");
				afterChangeProduct(productId,productName,brandCode,brandName);
			}
			else if(searchType == "2"){
				//元素搜索
				var reOrder = searchLi.attr("REORDER");
				var elementId = searchLi.attr("ELEMENT_ID");
				var elementName = searchLi.attr("ELEMENT_NAME");
				var productId = searchLi.attr("PRODUCT_ID");
				var packageId = searchLi.attr("PACKAGE_ID");
				var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
				var forceTag = searchLi.attr("FORCE_TAG");
				
				if(reOrder!="R"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
					alert("您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
					return false;
				}
				var elementIds = $.DatasetList();
				var selected = $.DataMap();
				selected.put("PRODUCT_ID",productId);
				selected.put("PACKAGE_ID",packageId);
				selected.put("ELEMENT_ID",elementId);
				selected.put("ELEMENT_TYPE_CODE",elementTypeCode);
				selected.put("MODIFY_TAG","0");
				selected.put("ELEMENT_NAME",elementName);
				selected.put("FORCE_TAG",forceTag);
				selected.put("REORDER",reOrder);
				elementIds.add(selected);
				if(selectedElements.addElements){
					selectedElements.addElements(elementIds);
				}
			}
			$("#Div_Search_productSearch").css("visibility","hidden");
}
/*海南开户js 挪到此文件中，初版 sunxin*/

//加载页面js
$(document).ready(function(){
$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
$.custInfo.pushWidget(-1,"REMARK_PART");

var obj =  $("#other_REAL_NAME");
obj.attr("checked",true);
obj.attr("disabled",true);
 $("#REAL_NAME").val("1");

var tag =  $("#other_ACTIVE_TAG");
tag.attr("disabled",true);
 $("#ACTIVE_TAG").val("0");
		$("#TIETONG_NUMBER").bind("keydown",function(e){
	    if (e.keyCode == 13 || e.keyCode == 108) {
			// 回车事件
	    	getMobilePhoneByTieTongNumber();
			return false;
		}
		
	});
			$.developStaff.init("10"); 
});

/*************************************校验开户号码及SIM资源 开始************************************/
function checkOldSNPwd(){
//初始化部分界面数据
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");	    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	$("#PSPT_INPUT").removeClass("e_elements-success");
	$("#PSPT_INPUT").removeClass("e_elements-error");
	intiAllElements();
	//清空费用
	$.feeMgr.clearFeeList("10");
	PAGE_FEE_LIST.clear();
	//重置按钮
	$.cssubmit.disabledSubmitBtn(true);
	//清空产品资料
	if($("#EPARCHY_CODE").val() !=''){
	    initProduct();
	}
	
	if($("#SERIAL_NUMBER").val()==''||$("#SERIAL_NUMBER").val().length<11) {
	     $("#SERIAL_NUMBER").val('');
	     return false;
	}
	if(!isTel($("#SERIAL_NUMBER").val()))
	{ 
		//通过回车
		if(flag==1)
			alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	//如果开户号码为空，或格式不正确，则返回
	if(!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
		return false;
	}

	checkMphone(0);
	
	/*$.ajax.submit(null,'querySaleActive','&SERIAL_NUMBER=' + $("#SERIAL_NUMBER").val(),null,function(data){
	    var data0 = data.get(0);
	   if(data0.get("SYS009") == 'TRUE' )
		{
			//getElement("AUTH_FOR_PERSON_SERIAL_NUMBER").value  = data0.get("SERIAL_NUMBER_A");
			//getElement("PERSON_AUTH_FLAG").value  = "true" ; 测试屏蔽，不知道现在组件参数 sunxin
			checkMphone(0);
		}
		else{
			if(data0.get("SERIAL_NUMBER_A") != '' )
			{
				getElement("AUTH_FOR_PERSON_SERIAL_NUMBER").value = data0.get("SERIAL_NUMBER_A");
				return authForPersonStart();
			}
			else {
				checkMphone(0);
			}
		}
	});*/
// 测试环境下没有数据 MM表没有记录 
}
function isTel(str){
       var reg=/^([0-9]|[\-])+$/g ;
       if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
        return false;
       }
       else{
         return reg.exec(str);
       }
}

/*校验号码onblur and 回车*/
function checkMphone(flag){
	//intiAllElements();
	var serialNumber = $("#SERIAL_NUMBER").val();
	//针对实名制，需要传递手机给组件 sunxin
	$.custInfo.setSerialNumber(serialNumber);
		var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();	//考虑预配情况
		$("#CHECK_RESULT_CODE").val("-1");		//设置号码未校验
		
		var oldSerialNumber = "";
		if(!isNull($("#OLD_SERIAL_NUMBER").val())) {
			oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
		}
		
	if(!isTel(serialNumber))
	{ 
		//通过回车
		if(flag==1)
			alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	
	//先将SIM卡号清空
	$("#SIM_CARD_NO").val("898600");
	$("#SIM_CARD_NO").attr("disabled", false);
	if(serialNumber==''||serialNumber.length<11)return false;
	if(!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
		return false;
	}
	var agentId = $("#AGENT_DEPART_ID");		//开户代理商编码
	var agentDepartId="";
	//不为undefined，表示代理商开户
	if(agentId.length) {
		if(agentId.val()=="") {
			alert("请选择开户代理商！");
			$("#AGENT_DEPART_ID").focus();
			return false;
		}
		agentDepartId = agentId.val().split("#")[0];	//截取代理商部门id
		$("#AGENT_DEPART_ID").val(agentDepartId);
	}

    var psptIDselected = "";
    var infoTag = "0";
   if(flag==1){
        psptIDselected = $("#OLD_NET_CHOOSE_ID").val();
        infoTag = $("#INFO_TAG").val();
    }
    else{
    	$("#INFO_TAG").val("0");
    }
	var authFlag = $("#PERSON_AUTH_FLAG").val();
	var authSerialNumber = $("#AUTH_FOR_PERSON_SERIAL_NUMBER").val();
	$.beginPageLoading("新开户号码校验中......");
	$.ajax.submit(null,'checkSerialNumber','&AUTH_FOR_PERSON_SERIAL_NUMBER=' + authSerialNumber+'&PERSON_AUTH_FLAG=' + authFlag+'&PSPTID_SELECTED=' + psptIDselected+'&INFO_TAG='+infoTag+'&SERIAL_NUMBER=' + serialNumber+'&OLD_SERIAL_NUMBER='+oldSerialNumber+'&OLD_SIM_CARD_NO='+oldSimCardNo+'&AGENT_ID_FOR_CHECK='+agentDepartId+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val(),'CheckSimCardNoHidePart,CheckSerialNumberHidePart,ProductTypePart',function(data){
	//TODO 提示信息待处理
		//增加开户可提示相关信息(规则已终止，省略)
		$("#OLD_SERIAL_NUMBER").val(serialNumber);
		if($("#B_REOPEN_TAG").val() != "1") {
			$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
			//校验完后界面部分数据处理
			setAjaxAtferCheckMphone(data);
			$.endPageLoading();
			//处理手机号码预配产品 sunxin
			var singleProduct=data.get(0).get("EXISTS_SINGLE_PRODUCT");
			if(singleProduct){
				var param = "&NEW_PRODUCT_ID="+singleProduct;
				packageList.renderComponent(singleProduct,$("#EPARCHY_CODE").val());//展示待选区域数据
				selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());//展示已选区域数据
			}
		}
		else {
			$("#SIM_CARD_NO").attr("disabled", true);
			//二次开户基本区域数据展现
			var data0 = data.get(0);
			var simCardNo =data0.get("SIM_CARD_NO");
			var psptTypeCode =data0.get("PSPT_TYPE_CODE");
			var psptId =data0.get("PSPT_ID");
			var psptEndDate= data0.get("PSPT_END_DATE");
	        var psptAddr= data0.get("PSPT_ADDR");
	        var userTypeCode= data0.get("USER_TYPE_CODE");
	        var custName= data0.get("CUST_NAME");
	        var birthday= data0.get("BIRTHDAY");
	        var phone= data0.get("PHONE");
	        var postCode= data0.get("POST_CODE");
	        var noteType= data0.get("NOTE_TYPE");
	        var postAddress= data0.get("POST_ADDRESS");
	        var payName= data0.get("PAY_NAME");
	        var payModeCode= data0.get("PAY_MODE_CODE");
	        var superBankCode= data0.get("SUPER_BANK_CODE");
	        var bankCode= data0.get("BANK_CODE");
	        var bankAcctNo= data0.get("BANK_ACCT_NO");
	        var remark= data0.get("REMARK");
	        $("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));//设置sim卡校验通过
			$("#SIM_CARD_NO").val(simCardNo);
			$("#PSPT_TYPE_CODE").val(psptTypeCode);
			$("#PSPT_ID").val(psptId);
			$("#OLD_PSPT_TYPE_CODE").val(psptTypeCode);
			$("#OLD_PSPT_ID").val(psptId);
			$("#PSPT_END_DATE").val(psptEndDate);			
			$("#PSPT_ADDR").val(psptAddr);
			$("#USER_TYPE_CODE").val(userTypeCode);
			$("#CUST_NAME").val(custName);
			$("#BIRTHDAY").val(birthday);
			$("#PHONE").val(phone);
			$("#POST_CODE").val(postCode);
			$("#NOTE_TYPE").val(noteType);
			$("#PAY_NAME").val(payName);
			$("#PAY_MODE_CODE").val(payModeCode);
			$("#SUPER_BANK_CODE").val(superBankCode);
			$("#BANK_CODE").val(bankCode);
			$("#BANK_ACCT_NO").val(bankAcctNo);
			$("#REMARK").val(remark);
			
			var param = "&USER_ID="+data0.get("USER_ID")+"&USER_PRODUCT_ID="+data0.get("PRODUCT_ID")+"&B_REOPEN_TAG="+$("#B_REOPEN_TAG").val();
			selectedElements.renderComponent(param,data0.get("EPARCHY_CODE"));
			$("#productSelectBtn").attr("disabled", true);
			$("#PRODUCT_NAME").attr("disabled", true);
			$("#PRODUCT_TYPE_CODE").attr("disabled", true);
			$("#preChoosePart").css("display","none");
			$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
			
			$("#USER_ID").val(data0.get("USER_ID"));
			$("#CUST_ID").val(data0.get("CUST_ID"));
			$("#PRODUCT_ID").val(data0.get("PRODUCT_ID"));
			$("#BRAND_CODE").val(data0.get("BRAND_CODE"));
			
			var feeData = $.DataMap();	
			feeData.put("MODE", "2");
			feeData.put("CODE", "0");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE","10");			
			$.feeMgr.insertFee(feeData);	
			
			$("#ACCT_DAY").val('1');
			$("#ACCT_DAY").attr("disabled", true);
			$("#HINT").css("display", "");
		    $("#HINT").text("该号码已经开户，下面进行二次开户！");
		   
		    $("#productSearchType").attr("disabled", true);
		    $("#productSearch").attr("disabled", true);
		    $.cssubmit.disabledSubmitBtn(false);
		
		}
		if(	$("#PAY_MODE_CODE").val()=="0"){
		   $("#SUPER_BANK_CODE").attr("disabled", true);
		   $("#BANK_CODE").attr("disabled", true);
		   $("#BANK_ACCT_NO").attr("disabled", true);
		}
		   $.endPageLoading();
		   //网上选号绑定了套餐包默认勾选
	var xcoding = data.get(0).get("A_X_CODING_STR");
	if(xcoding){
	    $("#SalePackage").css("display","");
    	var saleProductId = xcoding.value.split("|")[0];	
    	var salePackageId = xcoding.value.split("|")[1];	
    	$("#SALE_PRODUCT_ID").val(saleProductId) ;
    	$("#SALE_PACKAGE_ID").val(salePackageId) ;
		$("#BIND_SALE_TAG").val("1") ;
    	$("#packageBody input[name=packageCheckBox]").each(function(){
	       var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("packageTable");
            var json = table.getRowData(null, rowIndex);
            var packageId = json.get("PACKAGE_ID");
            if(packageId == salePackageId){
               this.click();
            }
            
        });    
    	//无更改权限时隐藏其他可选包并设置营销包必选  wenhj
    	if($("#SYSCHANGPACKAGE").val()!="1"){
    	$("#packageBody input[name=packageCheckBox]").each(function(){
	       var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("packageTable");
            var json = table.getRowData(null, rowIndex);
            var packageId = json.get("PACKAGE_ID");
            if(packageId != salePackageId)
               $(this).parent().parent().css("display", "none");
           else
              $(this).parent().parent().attr("disabled", true);
            
         }); 
        	
    	}
    	
		$("#SalePackage").css("display", "");
		$("#PackageInfo").css("display", "");	
	}else{
		$("#SALE_PRODUCT_ID").val("") ;
    	$("#SALE_PACKAGE_ID").val("") ;
		$("#BIND_SALE_TAG").val("0") ;
		$("#SalePackage").css("display", "none");
		$("#PackageInfo").css("display", "none");
		
	} 
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
	showDetailErrorInfo(error_code,error_info,derror);
    });
}
/*新开户号码校验完后返回值的处理*/
function setAjaxAtferCheckMphone(data) {
	var data0 = data.get(0);
	var simCardNo =data0.get("SIM_CARD_NO");
    var checkResultCode = data0.get("CHECK_RESULT_CODE");
    var existsPlunder_188= data0.get("EXISTS_188_PLUNDER"); 
	//预配预开时，sim卡自动带出来，且为不可修改
	if(!isNull(simCardNo)) {
		$("#SIM_CARD_NO").val(simCardNo);
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#SIM_CARD_NO").attr("disabled", true);
		$("#CHECK_RESULT_CODE").val(checkResultCode);//设置sim卡校验通过
		$("#HINT").css("display", "");
		$("#HINT").text("号码和sim卡校验成功！");
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").attr("disabled", true);
	}
	else {
		$("#SIM_CARD_NO").val("898600");
		$("#SIM_CARD_NO").attr("disabled", false);
		$("#CHECK_RESULT_CODE").val(checkResultCode);		//设置号码校验通过
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
	}
	//188抢号处理
	if(!isNull(existsPlunder_188)&&existsPlunder_188=="1"){
		$("#TD_PSPT_ID").val(data0.get("RANDOM_NO"));
		$("#TD_NAME").val(data0.get("TD_NAME"));
		$("#TD_ADDRESS").val(data0.get("TD_ADDRESS"));
		$("#TD_PHONE").val(data0.get("TD_PHONE"));
		$("#TD_ADDRESS2").val(data0.get("TD_ADDRESS2"));
		$("#TD_ZIP").val(data0.get("TD_ZIP"));
		$("#TD_MAIL").val(data0.get("TD_MAIL"));		
		//188抢号时，证件号码置为不可选，默认为身份证
		$("#PSPT_TYPE_CODE").val("0");
		$("#PSPT_TYPE_CODE").attr("disabled", true);
	}
	if(data0.get("FEE")){
		var feeData = $.DataMap();
		feeData.put("MODE", data0.get("FEE_MODE"));
		feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
		feeData.put("FEE",  data0.get("FEE"));
		feeData.put("PAY",  data0.get("FEE"));		
		feeData.put("TRADE_TYPE_CODE","10");		
		PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
		$.feeMgr.insertFee(feeData);	
		
	}
	//物联网开户
	if($("#OPEN_TYPE").val()=='IOT_OPEN'){
		$("#productSearchType").attr("disabled", true);
		$("#productSearch").attr("disabled", true);
	}
}

/*新开户号码校验，初始化界面部分数据  后续可能不需要 sunxin*/
function intiAllElements() {
	$("#PSPT_END_DATE").val("");
	$("#PSPT_ID").val("");
	$("#CUST_NAME").val("");
	$("#POST_ADDRESS").val("");
	$("#POST_CODE").val("");
	$("#PHONE").val("");
	$("#PSPT_ADDR").val("");
	$("#CONTACT").val("");
	$("#CONTACT_PHONE").val("");
	$("#WORK_NAME").val("");
	$("#WORK_DEPART").val("");
	$("#EMAIL").val("");
	$("#FAX_NBR").val("");
	$("#HOME_ADDRESS").val("");
	
	if($("#USER_PASSWD")) {
		$("#USER_PASSWD").val("");
	}
	$("#REMARK").val("");
	$("#BIRTHDAY").val("");
	
	$("#PAY_NAME").val("");
	$("#PAY_MODE_CODE").val("");
	$("#BANK_CODE").val("");
	$("#BANK_ACCT_NO").val("");
}
/*SIM卡号校验*/
function checkSimCardNo() {
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	$("#PSPT_INPUT").removeClass("e_elements-success");
	$("#PSPT_INPUT").removeClass("e_elements-error");
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	var personAuthFlag = $("#PERSON_AUTH_FLAG").val();	//签约赠188号码活动新增
	
	$.feeMgr.clearFeeList("10");
	$.cssubmit.disabledSubmitBtn(true);
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo=="" || simCardNo.length<20) {
		alert("SIM卡号输入不正确");
		$("#SIM_CARD_NO").focus();
		return false;
	}
	
	var oldSimCardNo = "";
	if(!isNull($("#OLD_SIM_CARD_NO").val())) {
		oldSimCardNo=$("#OLD_SIM_CARD_NO").val();
	}

	//先检查服务号码是否校验通过	
	if($("#SERIAL_NUMBER").val()=="") {
		alert("请先校验新开户号码！");
		return false ;
	}
    if((checkResultCode.value==undefined || checkResultCode.value=="" || checkResultCode.value== null) && personAuthFlag == "false")
	{	//签约赠188号码活动新增
		checkResultCode.value="-1";
	}
	if(checkResultCode=="-1") {
		alert("新开户号码未校验通过！");
		return false;
	}
	var param = "&CHECK_RESULT_CODE=" + checkResultCode + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()+ "&M2M_FLAG=" + $("#M2M_FLAG").val()+ "&AUTH_FOR_SALE_ACTIVE_TAG=" + $("#AUTH_FOR_SALE_ACTIVE_TAG").val()+ "&SPEC_SN_SECTNO_SIM_FEE=" + $("#SPEC_SN_SECTNO_SIM_FEE").val()+ "&NO_CARD_FEE_BRAND=" + $("#NO_CARD_FEE_BRAND").val()
	+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val()+ "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()+ "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()+ "&CHECK_DEPART_ID=" + $("#CHECK_DEPART_ID").val();
	$.beginPageLoading("SIM卡号校验中......");
	$.ajax.submit(null, 'checkSimCardNo', param, 'CheckSimCardNoHidePart', function(data){
		var data0 = data.get(0);
		initProduct();
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		if(data0.get("FEE")){
				var feeData = $.DataMap();
				feeData.put("MODE", data0.get("FEE_MODE"));
				feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","10");				
				$.feeMgr.insertFee(feeData);
				PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
				

	    }
	    //省内异地开户费用处理 先屏蔽，测试后处理
	   /*if($("#OPEN_TYPE").val()=="PROV_REMOTE_OPEN"){
		    var advancePayFlag=$("#PROV_OPEN_ADVANCE_PAY_FLAG").val();
		    var advancePay=$("#PROV_OPEN_ADVANCE_PAY").val();
		    var operFeeFlag=$("#PROV_OPEN_OPERFEE_FLAG").val();
		    var operFee=$("#PROV_OPEN_OPERFEE").val();
		    if(advancePayFlag=="true"){
		        $.feeMgr.clearFeeList("10","2");
		      	var feeData = $.DataMap();
				feeData.put("MODE", "2");
				feeData.put("CODE", "29");//修改 sunxin
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","10");				
				$.feeMgr.insertFee(feeData);
		    }
		     if(operFeeFlag=="true"){
		        $.feeMgr.clearFeeList("10","0");
		      	var feeData = $.DataMap();
				feeData.put("MODE", "0");
				feeData.put("CODE", "10");
				feeData.put("FEE",  operFee);
				feeData.put("PAY",  operFee);		
				feeData.put("TRADE_TYPE_CODE","10");				
				$.feeMgr.insertFee(feeData);
		    }
		    
		 }*/
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		//产品搜索引擎参数设置
		$.Search.get("productSearch").setParams("&EPARCHY_CODE="+$("#EPARCHY_CODE").val()+"&SEARCH_TYPE=1");
		//产品结账日设置
		var acct_day = $("#ACCT_DAY");
		selectedElements.setAcctDayInfo(acct_day.val(),"","","");
		//pos机刷卡参数加载
		$.feeMgr.setPosParam("10", $("#SERIAL_NUMBER").val(), $("#EPARCHY_CODE").val());
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#SIM_CARD_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });
    

}

/*************************************校验开户号码及SIM资源 结束************************************/
//实名制
/*
function dealRealNameCheckBoxChange(objC){
     if(objC.checked && checkPsptIdForReal()){
      $("#REAL_NAME").val("1");
        checkRealNameLimitByPspt();
    }
    else if (!objC.checked){
        $("#REAL_NAME").val("0");
    }
}

激活标记处理

function dealActiveTagCheckBoxChange(objC){
     if(objC.checked){
      $("#ACTIVE_TAG").val("1");
       
    }
    else if (!objC.checked){
        $("#ACTIVE_TAG").val("0");
    }
}

function checkPsptIdForReal(){
    if($("#PSPT_TYPE_CODE").val()=="Z")
    {
        alert("实名制开户，证件类型不能为其他，请重新选择！");
        return false;
    }
    var psptId = $("#PSPT_ID").val();
    if(psptId=="0"||psptId=="00"||psptId=="000"||psptId=="0000"||psptId=="00000"||psptId=="1"||psptId=="11"||psptId=="111"||psptId=="1111"||psptId=="11111"||psptId.indexOf("11111111")>-1)
    {
        alert("实名制开户，证件号码过于简单，请重新输入！");
        return false;
    }
    if(!$.validate.verifyField($("#CUST_NAME"))){
	   var obj =  $("#other_REAL_NAME");
	     obj.attr("checked",false);
	        return false;
    }
    if($("#CUST_NAME").val().indexOf("海南通")>-1)
    {
        alert("实名制开户，客户名称不能为海南通，请重新输入！");
        $("#CUST_NAME").val("");	//校验通过标识
        return false;
    }
    if(!$.validate.verifyField($("#PSPT_ID"))){
	   var obj =  $("#other_REAL_NAME");
	     obj.attr("checked",false);
	        return false;
    }
    return true;
}

检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByPspt(){
    var custName = $("#CUST_NAME").val();
    var psptId = $("#PSPT_ID").val();
    if(custName == "" || psptId == ""){
        return false;
    }
   $.beginPageLoading("新开户实名制校验中......");
   $.ajax.submit(null,'checkRealNameLimitByPspt','&custName='+custName+'&psptId='+psptId,'',function(data){
  	
	    if(data.get(0).get("CODE") == '0')
	    {
	        $("#REALNAME_LIMIT_CHECK_RESULT").val("true");
	    }else{
	        $("#REALNAME_LIMIT_CHECK_RESULT").val("false");
	        alert(data.get(0).get("MSG"));
	    }
	     $.endPageLoading();
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
    });
    
}*/

/*密码组件前校验*/
function PasswdbeforeAction() {
	 if($("#PSPT_TYPE_CODE").val()==""){
      alert("证件类型不能为空！");
      return false;
    }
     if($("#PSPT_ID").val()==""){
      alert("证件号码不能为空！");
      return false;
    }
    
     if($("#BIRTHDAY").val()==""){
      alert("出生日期不能为空！");
      return false;
    }
    //将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) {

$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}



