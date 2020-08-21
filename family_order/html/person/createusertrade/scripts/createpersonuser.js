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


/*************************************公用方法 开始************************************/


  
/*去掉空格*/
function trim(str) {
    if (isNull(str)){
        return "";
    }
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
   /**
	 * 人像比对
	 */
   var AGENT_PIC_ID=$("#AGENT_PIC_ID").val();
   if(AGENT_PIC_ID ==''){
	   $("#AGENT_PIC_ID").val("AGENT_PIC_ID_value");
   }
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
   if($("#DEFAULT_PWD_FLAG").val()!="1"){
	    if(!verifyAll('PasswdPart'))
	   {
		   return false;
	   } 
   }
   if($("#BIRTHDAY").val()==""){
	  $("#BIRTHDAY").val("1900-01-01");
   }
 //2015-04-13 密码加密
   if($("#USER_PASSWD").val()!=null&&$("#USER_PASSWD").val()!=""&&$("#USER_PASSWD").val().length==6){
	   var newPwd=$("#USER_PASSWD").val(); 
	   $.getScript("scripts/csserv/common/des/des.js",function(){  
			var data=newPwd;
			var firstKey="c";
			var secondKey="x"
			var thirdKey="y"
			$("#USER_PASSWD").val(strEnc(data,firstKey,secondKey,thirdKey)+"xxyy");
		});
   }
   /**
    * 人像比对
    */
	//AGENT_PIC_ID_value这是界面默认的值,
	//当没有对经办人人像摄像是，就把AGENT_PIC_ID指控
	var AGENT_PIC_ID_111=$("#AGENT_PIC_ID").val();
	if(AGENT_PIC_ID_111 =='AGENT_PIC_ID_value'){
		$("#AGENT_PIC_ID").val("");
	}
    /**
     * 添加判断物联网开户，不需要进行人像比对
     * @author zhuoyingzhi
     * @date 20170706 
     */
	var opentype = $("#OPEN_TYPE").val();
	
	if(opentype == "IOT_OPEN"){
		//物联网开户
		
		/**
		 * REQ201707170020_新增物联卡开户人像采集功能
		 * <br/>
		 * 物联网开户，放开人像比对
		 * @author zhuoyingzhi
		 * @date 20170824
		 */
		   var cmpTag = "1";
			$.ajax.submit(null,'isCmpPic',param,'',
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
				var picid = $("#PIC_ID").val();
				if(null != picid && picid == "ERROR"){
					//客户摄像失败
					MessageBox.error("告警提示","客户"+$("#PIC_STREAM").val(),null, null, null, null);
					return false;
				}
				var psptTypeCode=$("#PSPT_TYPE_CODE").val();
				
				//经办人信息
				var agentpicid = $("#AGENT_PIC_ID").val();
				var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
				
				if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
					
					/**
					 * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。 
					 */
					//经办人名称
				    var  custName = $("#AGENT_CUST_NAME").val();
				    //经办人证件号码
					var  psptId = $("#AGENT_PSPT_ID").val();
					//经办人证件地址
					var  agentPsptAddr= $("#AGENT_PSPT_ADDR").val();
					
					if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
						MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
						return false;
					}
					
					 if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
							MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
							return false;
					 }
				}		
				
				if(null != agentpicid && agentpicid == "ERROR"){
					//经办人摄像失败
					MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
					return false;
				}
				
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					//经办人未摄像
					MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
					return false;
				}
				var param = "&PIC_ID="+picid+"&AGENT_PIC_ID="+agentpicid;
				$.cssubmit.addParam(param);
			}		
		/**********************************************/
	}else{
		//非物联网开户
		   var cmpTag = "1";
			$.ajax.submit(null,'isCmpPic',param,'',
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
				var picid = $("#PIC_ID").val();
				if(null != picid && picid == "ERROR"){
					MessageBox.error("告警提示","客户"+$("#PIC_STREAM").val(),null, null, null, null);
					return false;
				}
				var psptTypeCode=$("#PSPT_TYPE_CODE").val();
				
				//经办人信息
				var agentpicid = $("#AGENT_PIC_ID").val();
				var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
				
				if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
					
					/**
					 * REQ201705270006_关于人像比对业务优化需求
					 * <br/>
					 * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。 
					 * @author zhuoyingzhi
					 * @date 20170620
					 */
					//经办人名称
				    var  custName = $("#AGENT_CUST_NAME").val();
				    //经办人证件号码
					var  psptId = $("#AGENT_PSPT_ID").val();
					//经办人证件地址
					var  agentPsptAddr= $("#AGENT_PSPT_ADDR").val();
					
					if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
						MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
						return false;
					}
					
					 if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
							MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
							return false;
					 }
				}		
				
				if(null != agentpicid && agentpicid == "ERROR"){
					MessageBox.error("告警提示","经办人"+$("#AGENT_PIC_STREAM").val(),null, null, null, null);
					return false;
				}
				
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
					return false;
				}
				
				/**
				 * REQ201707060020_关于年龄外经办人限制变更的优化
				 * <br/>
				 * 如果客户的证件类型为  户口本且客户未进行摄像  则提示客户摄像
				 * <br/>
				 * 不使用if((psptTypeCode == "0" || psptTypeCode == "1") && picid == "")这个
				 * 业务逻辑说明：证件类型为户口本 不对 经办人进行处理,为了不影响原来的判断逻辑，则单独写。
				 * @author zhuoyingzhi
				 * @date 20170803
				 */
				if(psptTypeCode == "2" && picid == ""){
					//未进行客户摄像
					//客户证件号码
					var custPsptId = $("#PSPT_ID").val();	
					
					if(custPsptId != "" && checkCustAge(custPsptId,psptTypeCode) ){
						//11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）； 
						MessageBox.error("告警提示","请进行客户摄像!",null, null, null, null);
						return false;
					}
				}				
				/***********************************/
				
				var param = "&PIC_ID="+picid+"&AGENT_PIC_ID="+agentpicid;
				$.cssubmit.addParam(param);
			}
	}
	/****************************************/
    var data = selectedElements.getSubmitData();
    var param = "&SELECTED_ELEMENTS="+data.toString();
     if(data=="[]"){
     alert('您没有选择产品信息，请选择产品！');
	 return false;
    }
     
    $.cssubmit.addParam(param);
    getPostValue();
     return  confirmParamAll();
}

/*封装confirmAll，处理特殊情况
*1、不同证件类型，不同的检查
*2、当需要后台校验的先执行ajax且其值不为空，再做客户端校验	
*/
function confirmParamAll(){
  
	   	if($("#IMSI").val()==''){
		 alert('IMSI为空！');
		 return false;
		}
		
		/*if($("#KI").val()==''){
		 alert('KI为空！');
		 return false;
		}*/
	var agentId = "";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	if(isAgent=='1')
	agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	else
	agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
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
             offerList.renderComponent("",$("#EPARCHY_CODE").val());
//             pkgElementList.initElementList(null);
           selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
           $("#productSelectBtn").attr("disabled", false);
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
    if($("#DEFAULT_PWD_FLAG").val()!="1"){
	   if(!verifyAll('PasswdPart'))
	   {
		   return false;
	   } 
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
			ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),"",assignProductIds);
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
			ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),"",assignProductIds);
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
/* 处理产品选择后的js校验 
*/
function afterChangeProduct(productId,productName,brandCode,brandName){
        $.feeMgr.clearFeeList("10");
        $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
        $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));
        var feeData = $.DataMap();
        feeData.clear();
		feeData.put("MODE", "2");
		feeData.put("CODE", "0");
		feeData.put("FEE",  "0");
		feeData.put("PAY",  "0");		
		feeData.put("TRADE_TYPE_CODE","10");
			
		$.feeMgr.insertFee(feeData);	
			
		/*feeData.clear();
		feeData.put("MODE", "2");
		feeData.put("CODE", "2");
		feeData.put("FEE",  "0");
		feeData.put("PAY",  "0");		
		feeData.put("TRADE_TYPE_CODE","10");
		$.feeMgr.insertFee(feeData);*/
        $("#PRODUCT_ID").val(productId);
        $("#PRODUCT_NAME").val(productName);
        var param = "&NEW_PRODUCT_ID="+productId;
            
            
        offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
//      pkgElementList.initElementList(null);
	    selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
		//变换产品后，重置搜索组件 sunxin
		var setData = "&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
		setData += "&PRODUCT_ID="+productId;
		setData += "&SEARCH_TYPE=2";
		$.Search.get("elementSearch").setParams(setData);
			
		var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
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
					PAGE_FEE_LIST.put("PRODUCT_FEE", $.feeMgr.cloneData(feeData));			
				}
			}
		
	    },
	    function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
		
		//海南添加
		if(typeof(createPersonUserExtend) != "undefined" && createPersonUserExtend.afterChangeProduct)
		{
			createPersonUserExtend.afterChangeProduct(inparam);
		}

}


function disableElements(data){

   if(data){
     var temp = data.get(0);
     if(data.get(0).get("NEW_PRODUCT_START_DATE")){
			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
	}
   }
  
}
function SetAdvanceFeeForAgent()
{
	//先清空产品相关缓冲区预存款费用
	// 传参：tradeTypeCode feeMode feeTypeCode
    if($("#AGENT_ADVANCE_FEE :selected").val() !=  "")
    {
            $.feeMgr.removeFee("10","2","0");
    }else
    {
            return;
    }
	//重新插入用户选择的预存款费用
	var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "0");
			feeData.put("FEE",  $("#AGENT_ADVANCE_FEE :selected").val() * 100);	
			feeData.put("TRADE_TYPE_CODE","10");
	$.feeMgr.insertFee(feeData);
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
			var searchLi = $("#Ul_Search_productSearch li[class=focus]");
				//产品搜索
				var productId = searchLi.attr("PRODUCT_ID");
				var productName = searchLi.attr("PRODUCT_NAME");
				var brandCode = searchLi.attr("BRAND_CODE");
				var brandName = searchLi.attr("BRAND");
				afterChangeProduct(productId,productName,brandCode,brandName);
			
			
			$("#Div_Search_productSearch").css("visibility","hidden");
}
function searchElementOptionEnter(){
			var searchLi = $("#Ul_Search_elementSearch li[class=focus]");
			
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
			
			$("#Div_Search_elementSearch").css("visibility","hidden");
}

//加载页面js
$(document).ready(function(){
	$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
	$.custInfo.pushWidget(9,"CALLING_TYPE_CODE_PART");
	$.custInfo.pushWidget(-1,"REMARK_PART");
	$.acctInfo.delWidget(7);

	/*var obj =  $("#other_REAL_NAME");
	obj.attr("checked",true);
	obj.attr("disabled",true);*/
 	$("#REAL_NAME").val("1");

	var tag =  $("#other_ACTIVE_TAG");
	tag.attr("disabled",true);
 	$("#ACTIVE_TAG").val("0");
	$("#TIETONG_NUMBER").bind("keydown",function(e){
	    if (e.keyCode == 13 || e.keyCode == 108) 
	    {
			// 回车事件
	    	getMobilePhoneByTieTongNumber();
			return false;
		}
		
	});
	
	$.developStaff.init("10"); 
	
    $("#AGENT_PSPT_TYPE_CODE").empty();
    $("#AGENT_PSPT_TYPE_CODE").append("<option value=''>请选择</option>");
    $("#AGENT_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
    $("#AGENT_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
    $("#AGENT_PSPT_TYPE_CODE").append("<option value='2'>户口本</option>");
	$("#AGENT_PSPT_TYPE_CODE").append("<option value='A'>护照</option>");
	$("#AGENT_PSPT_TYPE_CODE").append("<option value='N'>台湾居民来往大陆通行证</option>");
	$("#AGENT_PSPT_TYPE_CODE").append("<option value='O'>港澳居民来往内地通行证</option>");
	$("#AGENT_PSPT_TYPE_CODE").append("<option value='P'>外国人永久居留身份证</option>");
	
	
    $("#USE_PSPT_TYPE_CODE").empty();
    $("#USE_PSPT_TYPE_CODE").append("<option value=''>请选择</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='2'>户口本</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='A'>护照</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='N'>台湾居民来往大陆通行证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='O'>港澳居民来往内地通行证</option>");
	$("#USE_PSPT_TYPE_CODE").append("<option value='P'>外国人永久居留身份证</option>");
	
	
	var opentype = $("#OPEN_TYPE").val();
	if(opentype == "IOT_OPEN")
	{
		$("#RsrvFieldPart").css('display','block');
		
		$("#USER_TYPE_CODE").empty();
		$("#USER_TYPE_CODE").append("<option value='0'>个人</option>");
		$("#USER_TYPE_CODE").append("<option value='8'>集团用户</option>");
		$("#USER_TYPE_CODE").append("<option value='G'>测试机用户</option>");
		
		$("#PSPT_TYPE_CODE").empty();
		$("#PSPT_TYPE_CODE").append("<option value='0'>本地身份证</option>");
		$("#PSPT_TYPE_CODE").append("<option value='1'>外地身份证</option>");
		$("#PSPT_TYPE_CODE").append("<option value='A'>护照</option>");
		$("#PSPT_TYPE_CODE").append("<option value='D'>单位证明</option>");
		$("#PSPT_TYPE_CODE").append("<option value='E'>营业执照</option>");
		$("#PSPT_TYPE_CODE").append("<option value='M'>组织机构代码证</option>");
		$("#PSPT_TYPE_CODE").append("<option value='G'>事业单位法人证书</option>");
		$("#USE_PSPT_TYPE_CODE").append("<option value='3'>军人身份证</option>");
		$("#AGENT_PSPT_TYPE_CODE").append("<option value='3'>军人身份证</option>");
		$("#RSRV_STR3").append("<option value='3'>军人身份证</option>");
		
	}else
	{
		$("#RsrvFieldPart").css('display','none'); 
	}
	
});

/*************************************校验开户号码及SIM资源 开始************************************/
function checkOldSNPwd(){

var serialNumber = $("#SERIAL_NUMBER").val();
var oldSerialNumber = "";
		if(!isNull($("#OLD_SERIAL_NUMBER").val())) {
			oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
		}
	//同一个号码时，不在校验 sunxin
	if(oldSerialNumber==serialNumber){
	   return;
	}
//初始化部分界面数据
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");	    
    $("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
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
	
	var agentId = "";
	var agentCode ="";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	if(isAgent=='1')
	agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	else
	agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
	if(agentId.length){//不为undefined，表示代理商开户
	  if(agentId.val()==''){
	    alert('请选择开户代理商！');
	    return false;
	  }
		if(agentId.val()!=''){
	 	 agentCode=agentId.val().substring(0,5);
		}
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
	$.ajax.submit(null,'checkSerialNumber','&AUTH_FOR_PERSON_SERIAL_NUMBER=' + authSerialNumber+'&PERSON_AUTH_FLAG=' + authFlag+'&PSPTID_SELECTED=' + psptIDselected+'&INFO_TAG='+infoTag+'&SERIAL_NUMBER=' + serialNumber+'&OLD_SERIAL_NUMBER='+oldSerialNumber+'&OLD_SIM_CARD_NO='+oldSimCardNo+'&AGENT_DEPART_ID='+agentCode+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val(),'CheckSimCardNoHidePart,CheckSerialNumberHidePart,ProductTypePart,SalePackage',function(data){
	//TODO 提示信息待处理
		//增加开户可提示相关信息(规则已终止，省略)
		$("#OLD_SERIAL_NUMBER").val(serialNumber);
		
			$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
			//校验完后界面部分数据处理
			setAjaxAtferCheckMphone(data);
			$.endPageLoading();
			//处理手机号码预配产品 sunxin
			var singleProduct=data.get(0).get("EXISTS_SINGLE_PRODUCT");
			if(singleProduct){
			    afterChangeProduct(singleProduct);
				$("#productSelectBtn").attr("disabled", true);
				$("#PRODUCT_NAME").attr("disabled", true);
				$("#PRODUCT_TYPE_CODE").attr("disabled", true);
				//$("#preChoosePart").css("display","none");
				 $.cssubmit.disabledSubmitBtn(false);
			}
		/*else {
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
		   
		    //$("#productSearchType").attr("disabled", true);
		    $("#productSearch").attr("disabled", true);
		    $("#elementSearch").attr("disabled", true);
		    $.cssubmit.disabledSubmitBtn(false);
		
		}*/
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
	    	var saleProductId = xcoding.split("|")[0];	
	    	var salePackageId = xcoding.split("|")[1];	
	    	$("#SALE_PRODUCT_ID").val(saleProductId) ;
	    	$("#SALE_PACKAGE_ID").val(salePackageId) ;
			$("#BIND_SALE_TAG").val("1");
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
	              if(data.get(0).get("SYSCHANGPACKAGE4")!="1"){
	            	  $(this).parent().parent().attr("disabled", true);
	              }
            
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
		/**
		 * REQ201602290007 关于入网业务人证一致性核验提醒的需求
		 * chenxy3 2016-03-08
		 * */
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit(null,'checkNeedBeforeCheck','','',
				function(data){ 
					var flag=data.get("PARA_CODE1");
					if(flag=="1"){ 
						var param ="&TRADE_ID=10"+"&EPARCHY_CODE=0898"+"&TRADE_TYPE_CODE=10";;
						popupPage('beforecheck.BeforeCheck','init',param,'业务检查','680','400',null,null,null,null,false);
					}
				  	$.endPageLoading();
				},function(error_code,error_info){
					$.MessageBox.error(error_code,error_info);
					$.endPageLoading();
				});
	},
	function(error_code,error_info,derror){
	$.endPageLoading();
	$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
	showDetailErrorInfo(error_code,error_info,derror);
    });
}
//变更营销包权限判断 add by wenhj 2013.01.26
function choosePackageCheckBox(obj)
{

	if($("#SYSCHANGPACKAGE").val()=="1")
	{
		 $("#packageBody input[name=packageCheckBox]").each(function()
		 {
	      	 if(this.checked)
	      	 {
		     	 this.click();
		         var rowIndex = this.parentNode.parentNode.rowIndex;
		   	     var table = $.table.get("packageTable");
	             var json = table.getRowData(null, rowIndex);
	             var packageid = json.get("PACKAGE_ID");
	             $("#SALE_PACKAGE_ID").val(packageid);
	             $("#BIND_SALE_TAG").val("1");
	          }
	      });
	}
     
	if($("input[name=packageCheckBox]:checked").length==0)
	{
        $("#SALE_PACKAGE_ID").val("") ;
        $("#BIND_SALE_TAG").val("0") ;
    }
     
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
		//$("#HINT").css("display", "");
		//$("#HINT").text("号码和sim卡校验成功！");
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
		$("#SIM_CARD_INPUT").addClass("e_elements-success");
		//$("#SIM_CARD_INPUT").attr("disabled", true);
		//预配卡费用 sunxin
		if(data0.get("FEE")){
				var feeData = $.DataMap();
				feeData.put("MODE", data0.get("FEE_MODE"));
				feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data0.get("FEE"));
				feeData.put("PAY",  data0.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","10");	
				feeData.put("SYSCHANGPACKAGE4",data0.get("SYSCHANGPACKAGE4"));//REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
				$.feeMgr.insertFee(feeData);
				PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
				

	    }
	     //处理密码卡 sunxin
		var cardPasswd = $("#CARD_PASSWD").val();	//密码
		var passCode = $("#PASSCODE").val();	//密码因子
		if(cardPasswd!=""&&passCode!=""){
			if(confirm('该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？')){
				$("#DEFAULT_PWD_FLAG").val("1");//使用初始密码 sunxin
				hideLayer("PasswdPart");//将密码组件设置不能选择
			}
			else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			   }
		}
		else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			 }
		
	}
	else {
		$("#SIM_CARD_NO").val("898600");
		$("#SIM_CARD_NO").attr("disabled",false);
		$("#CHECK_RESULT_CODE").val(checkResultCode);		//设置号码校验通过
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
	}
	//号码需要交纳的预存费用 sunxin
	if(data0.get("FEE_CODE_FEE")){
		var feeData = $.DataMap();
		feeData.put("MODE", "2");
		feeData.put("CODE", "62");//选号预存收入
		feeData.put("FEE",  data0.get("FEE_CODE_FEE"));
		feeData.put("PAY",  data0.get("FEE_CODE_FEE"));	
		feeData.put("TRADE_TYPE_CODE","10");
		feeData.put("SYSCHANGPACKAGE4",data0.get("SYSCHANGPACKAGE4"));//REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
		PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
		$.feeMgr.insertFee(feeData);	
		
	}
	//物联网开户
	if($("#OPEN_TYPE").val()=='IOT_OPEN'){
		//$("#productSearchType").attr("disabled", true);
		$("#productSearch").attr("disabled", true);
		$("#elementSearch").attr("disabled", true);
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

function beforeReadCard(){
var flag =$("#M2M_FLAG").val();
if(flag=="1"){
   $.simcard.setNetTypeCode("07");
}
	var sn = $("#SERIAL_NUMBER").val();
	$.simcard.setSerialNumber(sn);
	return true;
}
function beforeCheckSimCardNo(data) {
var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
if(isWrited == "1"){
	var simno =data.get("SIM_CARD_NO");
	 $("#SIM_CARD_NO").val(simno);
	 checkSimCardNo(1);
	}
}
function afterWriteCard(data){
//	alert(data.toString());
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.readSimCard();
	}
}
/*SIM卡号校验*/
function checkSimCardNo(flag) {
   
	var checkResultCode = $("#CHECK_RESULT_CODE").val();	//校验通过标识
	var personAuthFlag = $("#PERSON_AUTH_FLAG").val();	//签约赠188号码活动新增
	
	//$.cssubmit.disabledSubmitBtn(true);
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo=="" || simCardNo.length<20) {
	if(flag==1)
		alert("SIM卡号输入不正确");
		//$("#SIM_CARD_NO").focus();
		return false;
	}
	
	var oldSimCardNo = "";
	if(!isNull($("#OLD_SIM_CARD_NO").val())) {
		oldSimCardNo=$("#OLD_SIM_CARD_NO").val();
	}
	if(oldSimCardNo==simCardNo){
	   return;
	}
	$.cssubmit.disabledSubmitBtn(true);
	$.feeMgr.clearFeeList("10");//防止多次点击校验产生多条费用
	$("#SIM_CARD_INPUT").removeClass("e_elements-success");
	$("#SIM_CARD_INPUT").removeClass("e_elements-error");
	
	var agentId = "";
	var agentCode ="";
	var isAgent = $("#IS_AGENT").val();//开户代理商编码
	if(isAgent=='1')
	agentId= $("#AGENT_DEPART_ID");//开户代理商编码
	else
	agentId=$("#AGENT_DEPART_ID1");//开户代理商编码
	if(agentId.length){//不为undefined，表示代理商开户
	  if(agentId.val()==''){
	    alert('请选择开户代理商！');
	    return false;
	  }
		if(agentId.val()!=''){
	 	 agentCode=agentId.val().substring(0,5);
		}
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
	+ "&OPEN_TYPE=" + $("#OPEN_TYPE").val()+ "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()+ "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()+ "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()+'&AGENT_DEPART_ID='+agentCode;
	$.beginPageLoading("SIM卡号校验中......");
	$.ajax.submit(null, 'checkSimCardNo', param, 'CheckSimCardNoHidePart', function(data){
		var data0 = data.get(0);
		//initProduct();
		
		$("#OLD_SIM_CARD_NO").val(simCardNo);
		$("#CHECK_RESULT_CODE").val(data0.get("CHECK_RESULT_CODE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		$.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
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
		$.cssubmit.disabledSubmitBtn(false);
		//pos机刷卡参数加载
		$.feeMgr.setPosParam("10", $("#SERIAL_NUMBER").val(), $("#EPARCHY_CODE").val());
		$.endPageLoading();
		//处理密码卡 sunxin
		var cardPasswd = $("#CARD_PASSWD").val();	//密码
		var passCode = $("#PASSCODE").val();	//密码因子
		if(cardPasswd!=""&&passCode!=""){
			if(confirm('该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？')){
				$("#DEFAULT_PWD_FLAG").val("1");//使用初始密码 sunxin
				hideLayer("PasswdPart");//将密码组件设置不能选择
			}
			else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			   }
		}
		else{
			   $("#DEFAULT_PWD_FLAG").val("0");//不使用初始密码 sunxin
				showLayer("PasswdPart");//将密码组件设置不能选择
			 }
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#SIM_CARD_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
    });
    

}

/*************************************校验开户号码及SIM资源 结束************************************/

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
    
    /* if($("#BIRTHDAY").val()==""){
      alert("出生日期不能为空！");
      return false;
    }*/
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

function verifyenterprise(){
	$.custInfo.verifyEnterpriseCard(); 
}

function verifyorg(){
	$.custInfo.verifyOrgCard(); 
}
/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * <br/>
 * 判断是否在
 * 11岁（含）至120岁（不含）之间的
 * @param idCard
 * @param psptTypeCode
 */
function checkCustAge(idCard,psptTypeCode){

	//根据身份证  获取周岁
	var cust_age = this.jsGetAge(idCard);
	
	if(11 <=cust_age && cust_age < 120 ){
		//11岁（含）至120岁（不含）
		return true;
	}else{
		return false;
	}
}
/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * <br/>
 * 使用客户资料变更里面的方法(在生产上已经存在的方法)
 * @author  zhuoyinghi
 * @date 20170803
 * @param idCard
 * @returns
 */
function jsGetAge(idCard){				 
    var returnAge;
	var bstr = idCard.substring(6,14);			 
    var birthYear = bstr.substring(0,4);
    var birthMonth = bstr.substring(4,6);
    var birthDay = bstr.substring(6,8);
    
    var d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();
    
    if(nowYear == birthYear)
    {
        returnAge = 0;//同年 则为0岁
    }
    else
    {
        var ageDiff = nowYear - birthYear ; //年之差
        if(ageDiff > 0)
        {
            var monthDiff = nowMonth - birthMonth;//月之差
            if(monthDiff <= 0)
            {
                returnAge = ageDiff - 1;
            }
            else
            {
                returnAge = ageDiff ;
            }
        }
        else
        {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;//返回周岁年龄		    
}

/**
 * 操作员个人用户开户-用户类型开户权限校验
 * @author zhaohj3
 * @date 2018-1-4 16:50:20
 */
function checkUserTypePriv()
{
	var userTypeCode = $("#USER_TYPE_CODE").val();
	var privId = "";
	if (userTypeCode == "A") // 测试机用户
	{
		privId = "SYS_CRM_CREATE_USERTYPE_A";
	}
	
	if (privId != "")
	{
		$.beginPageLoading("个人用户开户-用户类型开户权限校验中...");
		var param = "&PRIV_ID=" + privId;
		var hasPrivFlag = "true";
		$.ajax.submit(null, 'hasPriv', param, null, function (data) {
			hasPrivFlag = data.get("HAS_PRIV");
			var staffId = data.get("STAFF_ID");
			if (hasPrivFlag == "false") {
				alert("操作员[" + staffId +"]没有[个人用户开户-用户类型（测试机用户）开户权限]，权限编码为：[" + privId + "]");
				$("#USER_TYPE_CODE").val("0");
			}
			$.endPageLoading();
		},function (error_code,error_info) {
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{async:false});

		if (hasPrivFlag == "false") {
			return false;
		}
	}
}
