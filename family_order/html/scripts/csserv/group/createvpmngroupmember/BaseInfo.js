/* $Id  */

function validate() {
    var selectProduct=$('#grpProductTreeSelected').val();
	if (selectProduct=='[]') {
	   alert('请先输入集团客户编码，查询并选择需要办理的集团产品后，再进行此操作！');
	   return false;
	}
	
    var custName=$('#CUST_NAME').val();	   
	if (custName=="") {
	   alert('尚未查询客户资料，请输入集团编码按回车查询!');
	   return false;
	}
	
	var serialNumber=$('#MEM_SERIAL_NUMBER').val();
	serialNumber = serialNumber.replace(/(^\s*)|(\s*$)/g, ""); 
	if (serialNumber == "") {
	   alert('请先输入成员服务号码查询资料后，再进行此操作！');
	   return false;
	}
	var munSerialNumenber = $('#cond_SERIAL_NUMBER').val();
	if (trim(munSerialNumenber) !=serialNumber || trim(munSerialNumenber) == "") {
		alert('成员资料有误，请重新输入成员服务号码并按回车查询成员资料后，再进行此操作');
	   	return false;
	}
    return true;    
}

function queryCompProduct(node) {
    $('#GROUP_AUTH_FLAG').val("false");
    var obj=$('#CompProductInfoPart');
    obj.css('display','block');
    
    var nodeids = node.id.split('_');
    //分散账期修改 参数添加USER_ID_A
    ajaxSubmit(this, 'queryProductInfo','&PRODUCT_ID=' + nodeids[2] + '&USER_ID_A=' + nodeids[3],'CompProductInfoPart,authForGroupPart,bookingPart');
}

function afterSelectProductAction() {
    $('#SELECT_USER_ID').val('');
    $('#SELECT_PRODUCT_ID').val('');
    $('#SELECT_OPER_TYPE').val('');
    
    $('#cond_SERIAL_NUMBER').attr('disabled','false');
    focus($('#cond_SERIAL_NUMBER'));
    
	var compixProduct = this.ajaxDataset.get(0, "COMPIX_PRODUCT");
	$('#COMPIX_PRODUCT').val(compixProduct);
	Wade.page.endFlowOverlay();
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

function clearMemInfo(){
	$('#MEM_CUST_NAME').innerText="";
	$('#MEM_SERIAL_NUMBER').innerText="";
	$('#MEM_PRODUCT_ID').innerText="";
	$('#MEM_EPARCHY_CODE').innerText="";
}

function authAfterFunction() {
   var flag = $('#GROUP_AUTH_FLAG').value;
   if (flag == "true") {
       var nvf=Wade.nav.getActiveNavFrame();
       nvf=nvf?nvf:parent;
	   if(nvf){
           getFrame(['flowtab', nvf]).fireMouseEvent('bnext','click');
       }
   }
   else
      return false;
}

function checkInfo(){
    if (!validate()) 
        return false; 
	
	//start add by wangyf for VPMN成员新增时取值有误
	var choose=$('#QueryType').val();
	//集团产品编码
	var tempGPValue = $('#temp_GROUP_PRODUCT').val();
	var productSerialValue = $('#cond_GROUP_PRODUCT').val();
	//集团客户编码
	var tempGroupId = $('#temp_GROUP_ID').val();
	var groupIdValue = $('#cond_GROUP_ID1').val();
	if (choose=="0" || choose == "2") { //按集团编码或集团产品编码
		//start add by wangyf for VPMN成员新增时取值有误
    	 if(choose=="2"){
     	 	if(tempGPValue != "OK" && tempGPValue != ""){
     			//alert("tempGPValue=" + tempGPValue + " productSerialValue=" + productSerialValue);  
     			if(tempGPValue != productSerialValue){
     				alert("集团产品编码已改变,请重新回车后查询集团产品！");
     				//alert("tempGPValue2=" + tempGPValue + " productSerialValue2=" + productSerialValue); 
     				return false;     				
     			}
     		}
    	}
    	if(choose=="0"){
    		if(tempGroupId != "OK" && tempGroupId != ""){
     			//alert("tempGroupId=" + tempGroupId + " groupIdValue=" + groupIdValue);  
     			if(tempGroupId != groupIdValue){
     				alert("集团客户编码已改变,请重新回车后查询集团产品！");
     				//alert("tempGroupId2=" + tempGroupId + " groupIdValue2=" + groupIdValue); 
     				return false;     				
     			}
     		}
    	}
   	}
	//end add by wangyf for VPMN成员新增时取值有误
    var flag = $('#GROUP_AUTH_FLAG').value;
    var tradeTypeCode = $('#TRADE_TYPE_CODE').value; 
    var tradeTypeCode = $('#TRADE_TYPE_CODE').value;     
    var serial_number = $('#cond_SERIAL_NUMBER').value;
    var number = serial_number.substring(0,4);
    if(number == "0898") { //网外号码不验证密码
    	flag = "true";
    }
    if (flag == "true" || tradeTypeCode == "") {
        return true;
    }
    else {
        $('#AUTH_SERIAL_NUMBER').value=$('#cond_SERIAL_NUMBER').value;
        $('#authButton').click();
        return false;  
    }
}

function afterCheckGroupInfo() {
	var x_tag = this.ajaxDataset.get(0, "X_TAG");
	var serial_number = $('#cond_SERIAL_NUMBER').val();
	if (x_tag == 0) {
		var confirTag = confirm("服务号码["+serial_number+"]为非移动号码，是否需要新增客户核心资料") ;
    	if(confirTag == true){ 
    		ajaxSubmit(this, 'createUserInfo','','PersonInfoPart,EparchyPart');
   		}
    	else{
    		return false;
    	}
    	$('#GROUP_AUTH_FLAG').value = "true";
    }
    else {
    	$('#GROUP_AUTH_FLAG').value = "false";
    	return false;
    }
}

function clickcheck() {
    var effectNow=$('#effectNow');
    
    //分散账期修改 新增分散情况下的预约组件
    var divEffectNow = $('#divEffectNow');
    
	if(effectNow){
    	if(effectNow.checked){
    		$('#ifBooking').value = 'true';
    	}else{
    		$('#ifBooking').value = 'false';	 
    	}
    }else if(divEffectNow){
    	if(divEffectNow.checked){
    		$('#ifBooking').value = 'true';
    	}else{
    		$('#ifBooking').value = 'false';	 
    	}
    }
}

function changeQueryType() {
   var choose=$('#QueryType').val();
   if (choose=="0") { //按集团编码
      $('#selectGroupPart').css('display','block');
      $('#groupProductIdPart').style.display = "none";
      $('#cond_SERIAL_NUMBER').disabled = true;
   }
   else if (choose=="1") { //按成员手机号码
      $('#selectGroupPart').style.display = "none";
      $('#groupProductIdPart').style.display = "none";
      $('#cond_SERIAL_NUMBER').attr('disabled','false');
   }
   else if (choose == "2") { //按集团产品编码查询
      $('#selectGroupPart').style.display = "none";
      $('#groupProductIdPart').css('display','block');
      $('#cond_SERIAL_NUMBER').disabled = true;
   }
}

function searchMemberBySerialNumber() {
   var serialValue = $('#cond_SERIAL_NUMBER').val();
   var tmpSet = new Wade.DatasetList($('#grpProductTreeSelected').value);
   var productId = "";
   var userId = "";
   if (tmpSet.length > 0) {
       var tmpMap = tmpSet.get(0);
       productId=tmpMap.get("PRODUCT_ID");
       userId=tmpMap.get("USER_ID_A");
   }
   if(isNumber(serialValue)) {
      var choose=$('#QueryType').val();
      //start add by wangyf for VPMN成员新增时取值有误
      //集团产品编码
   	  var tempGPValue = $('#temp_GROUP_PRODUCT').val();
   	  var productSerialValue = $('#cond_GROUP_PRODUCT').val();
   	  //集团客户编码
   	  var tempGroupId = $('#temp_GROUP_ID').val();
   	  var groupIdValue = $('#cond_GROUP_ID1').val();
      //end add by wangyf for VPMN成员新增时取值有误
      if (choose=="0" || choose == "2") { //按集团编码或集团产品编码
      	 //start add by wangyf for VPMN成员新增时取值有误
      	 	if(choose=="2"){
	      	 	if(tempGPValue != "OK" && tempGPValue != ""){
	      			//alert("tempGPValue=" + tempGPValue + " productSerialValue=" + productSerialValue);  
	      			if(tempGPValue != productSerialValue){
	      				alert("集团产品编码已改变,请重新回车后查询集团产品！");
	      				//alert("tempGPValue2=" + tempGPValue + " productSerialValue2=" + productSerialValue); 
	      				return false;     				
	      			}
	      		}
      	 	}
      		if(choose=="0"){
      			if(tempGroupId != "OK" && tempGroupId != ""){
	      			//alert("tempGroupId=" + tempGroupId + " groupIdValue=" + groupIdValue);  
	      			if(tempGroupId != groupIdValue){
	      				alert("集团客户编码已改变,请重新回车后查询集团产品！");
	      				//alert("tempGroupId2=" + tempGroupId + " groupIdValue2=" + groupIdValue); 
	      				return false;     				
	      			}
	      		}
      		}
      		//end add by wangyf for VPMN成员新增时取值有误
         //分散账期修改 添加刷新区域bookingPart
         ajaxDirect4CS(this, 'queryMemberInfo','&cond_SERIAL_NUMBER='+serialValue+'&PRODUCT_ID='+productId+"&USER_ID="+userId,'PersonInfoPart,EparchyPart,bookingPart',true,afterCheckGroupInfo);
      } else if (choose == "1") { //按成员手机号码
         ajaxDirect4CS(this, 'queryMemberInfo2', '&cond_SERIAL_NUMBER='+serialValue, 'PersonInfoPart',true,afterCheckGroupInfo2);
      }
   }
}

function searchByGroupProductCode() {
   var serialValue = $('#cond_GROUP_PRODUCT').val();
   //start add by wangyf for VPMN成员新增时取值有误
   $('#temp_GROUP_PRODUCT').value = serialValue;
   //end add by wangyf for VPMN成员新增时取值有误
   $('#GROUP_AUTH_FLAG').value = "false";
   ajaxDirect4CS(this, 'getGroupInfoByGPId', '&cond_GROUP_PRODUCT='+serialValue, 'CustInfoPart,GroupProductPart',true,afterCheckGroupProductCode);
}

function afterCheckGroupProductCode() {
   var shortNum = this.ajaxDataset.get(0, "GROUPINFO_NUM");
   if(!setVPMNGroupInfo()){return;};
   if(shortNum >'0') {
      var userId = this.ajaxDataset.get(0, "USER_ID");
      var productId = this.ajaxDataset.get(0, "PRODUCT_ID");
      var operType = this.ajaxDataset.get(0, "PRODUCT_TYPE");
      
      $('#cond_SERIAL_NUMBER').attr('disabled','false');
      focus($('#cond_SERIAL_NUMBER'));
    
      if (userId != "" && productId != "" && operType != "") {
         var tt = new Array();
         var o = new Object();
         o.operType=operType;
         o.userId=userId;
         o.productId=productId;
         tt[0]=o;
         setSelectedElement(tt);
         Wade.page.beginFlowOverlay();
         $('#GROUP_AUTH_FLAG').value = "false";
         var obj=$('#CompProductInfoPart');
         obj.css('display','block');
         ajaxDirect4CS(this, 'queryProductInfo','&PRODUCT_ID='+productId,'CompProductInfoPart,authForGroupPart,bookingPart',true,endFlowOverlay);

         Wade.page.beginFlowOverlay();
         ajaxDirect4CS(this, 'getProductInfoByAjax', '&PRODUCT_ID='+productId , null, false,afterSelectProductAction);
      }  
   }
}

function endFlowOverlay() {
   Wade.page.endFlowOverlay();
}

function afterCheckGroupInfo2() {
   var shortNum = this.ajaxDataset.get(0, "GROUPINFO_NUM");
   if(shortNum >'0') {
      popupDialog('group.destroyvpmngroupmember.QueryDestroyGroup', 'queryGroupInfo', '&PARENT_BUTTON=groupChooseButton&cond_SERIAL_NUMBER='+$('#cond_SERIAL_NUMBER').value+'&refresh='+true, '集团信息', '600', '240');
   }
}

function chooseGroupButton() {
	var group = $('#cond_GROUP_ID1').value;
	if (group == ""){
		return false;
	}else{
		//start add by wangyf for VPMN成员新增时取值有误
   		$('#temp_GROUP_ID').value = group;
   		//end add by wangyf for VPMN成员新增时取值有误
		Wade.page.beginPageLoading();
		//redirectTo(this,'getGroupBaseInfo','&cond_GROUP_ID='+group+'&SELECT_USER_ID='+$('#SELECT_USER_ID').value+'&SELECT_PRODUCT_ID='+$('#SELECT_PRODUCT_ID').value+'&SELECT_OPER_TYPE='+$('#SELECT_OPER_TYPE').value,'currentframe');
	    //分散账期修改 增加刷新区域bookingPart
	    ajaxDirect4CS(this, 'getGroupBaseInfo', '&cond_GROUP_ID='+group+'&SELECT_USER_ID='+$('#SELECT_USER_ID').value+'&SELECT_PRODUCT_ID='+$('#SELECT_PRODUCT_ID').value+'&SELECT_OPER_TYPE='+$('#SELECT_OPER_TYPE').value, 'CustInfoPart,PersonInfoPart,GroupProductPart,selectGroupPart,bookingPart',true,afterBaseInfo);
	}
}

function afterBaseInfo() {
    $('#QueryType').value = "0";
    $('#selectGroupPart').css('display','block');
    $('#cond_SERIAL_NUMBER').val('');

    var userId = $('#SELECT_USER_ID').value;
    var productId = $('#SELECT_PRODUCT_ID').value;
    var operType = $('#SELECT_OPER_TYPE').value;
    
    $('#cond_SERIAL_NUMBER').attr('disabled','false');
    focus($('#cond_SERIAL_NUMBER'));
   	if(!setVPMNGroupInfo()){return;};
    
    if (userId != "" && productId != "" && operType != "") {
        var tt = new Array();
        var o = new Object();
        o.operType=operType;
        o.userId=userId;
        o.productId=productId;
        tt[0]=o;
        setSelectedElement(tt);
        Wade.page.beginFlowOverlay();
        $('#GROUP_AUTH_FLAG').value = "false";
        var obj=$('#CompProductInfoPart');
        obj.css('display','block');
        ajaxSubmit(this, 'queryProductInfo','&PRODUCT_ID='+productId,'CompProductInfoPart,authForGroupPart,bookingPart');
    
        Wade.page.beginFlowOverlay();
        ajaxDirect4CS(this, 'getProductInfoByAjax', '&PRODUCT_ID='+productId , null, false,afterSelectProductAction);
   }
}

function initBaseInfo() {
    getPageVisit().randomCode = new Date();
}

function setVPMNGroupInfo(){
	var radioes = getChildsByRecursion('GroupProductPart', 'input', 'type', 'radio');
	var isFinded = false;
	for(var i = 0;i!=radioes.length;++i){
		if(radioes[i].value=="8000"){
			isFinded = true;
			//radioes[i].checked = true;
			//Wade.page.beginPageLoading();
			//radioes[i].click();
			break;
		}
	}
	if(!isFinded){
		alert("对不起，该集团没有订购VPMN产品，无法办理此业务！");
		redirectTo(this,null,null,'currentframe');
		return false;
	}
	return true;
}
function getGroupInfo(){
//	Wade.ajax.ajaxDirect(this, "checkGroupId", "&cond_GROUP_ID="+$('#POP_cond_GROUP_ID').value);
//	Wade.ajax.ajaxDirect(this, "checkGroupId", "&cond_GROUP_ID=8721420844");
//	self.afterAction = "checkResult()";
	
	var group = $('#cond_GROUP_ID1').value;
	if (group == ""){
		alert ('请输入正确的集团编码！');
		return false;
	}else{
	  chooseGroupButton();
	}
	
}

/**
 * 分散账期修改
 * 分散账期新增方法：点击【下一步】根据用户账期判断
 * 
 * @param {} acctday
 * @return {Boolean}
 */
function judeAcctDays(acctday) {
	var acctdaydistrbute = getElementValue('ACCTDAY_DISTRIBUTION');
	//TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"),FALSE("4")
	if(acctdaydistrbute == "" || acctdaydistrbute == "0"){
	   //自然日出账的用户不做处理，保持和现有的逻辑一样
	   return true;    
	}
	
	var tradeData = new Wade.DataMap($('#tradeData').value);
	var memberuserid = tradeData.get("MEM_USER_INFO").get("USER_ID","");
	var acctdays = tradeData.get("USER_ACCT_DAY");
	if(acctdays == null)
	    return true;
	
	var useracctday = acctdays.get(memberuserid);
	var ifgoon = true;

	var validacct_day = useracctday.get("ACCT_DAY","");
	var validacct_startdate = useracctday.get("START_DATE","");
	var validacct_enddate = useracctday.get("END_DATE","");
	var novalidacct_day = useracctday.get("NEXT_ACCT_DAY","");
	var novalidacct_startdate = useracctday.get("NEXT_START_DATE","");
	var novalidacct_enddate = useracctday.get("NEXT_END_DATE","");
	var firstdaynextacctday = useracctday.get("FIRST_DAY_NEXTACCT","");
	
	var payType = $('#PLAN_TYPE').val();
	if(payType == null || payType == ""){
		alert("请选择付费方式!");
		return false;
	}

	var ifNaturedayProduct = getElementValue('IF_NATUREDAY_PRODUCT');//是否必须要求成员账期为自然月的产品 true：是 ; false：否
    var ifImmediProduct = getElementValue('IF_IMMEDI_PRODUCT');//是否为能立即生效的产品 true：是 ; false：否
	if(acctday == null || acctday == ""){
		acctday = "1";
	}
	
	var active_tag = "false";
	var ifbookingobj = $('#divEffectNow');
    if(ifbookingobj != null  && ifbookingobj.checked == true){
	     active_tag = "true";
	}

    
	//TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"),FALSE("4")
	if(acctdaydistrbute == "1"){
	   //存在未生效的账期。下账期为自然日出账的用户
	   if(active_tag != "true" && ((ifNaturedayProduct == "true" && ifImmediProduct == "false") || payType == "G")){
       	   alert("该用户账期变更还未生效，办理的集团业务为预约业务，请选择下账期生效!");
       	   return false;
       }
		
       /*if(active_tag != "true" && ifNaturedayProduct == "true" && ifImmediProduct == "false"){
       	   alert("该用户账期变更还未生效，办理的集团业务为预约业务，请选择下账期生效!");
       	   return false;
       }
       
       if(active_tag != "true" && ifNaturedayProduct == "true" && ifImmediProduct == "true" && payType != "P"){
       	   alert("该用户账期变更还未生效，办理的集团业务为预约业务，请选择下账期生效!");
       	   return false;
       }
       
       if(active_tag != "true" && ifNaturedayProduct == "false" && payType != "P"){
       	   alert("该用户账期变更还未生效，办理的集团业务为预约业务，请选择下账期生效!");
       	   return false;
       }*/
	}

    if(acctdaydistrbute == "2" || acctdaydistrbute == "3" ){
	   //存在未生效的账期。下账期为非自然日出账的用户
       if(ifNaturedayProduct == "true" || (ifNaturedayProduct == "false" && payType == "G")){
       	   alert("当前账期内不允许办理该集团业务，且用户处于未生效的账期变更[" + novalidacct_startdate + "]后生效，需在[" + novalidacct_startdate + "]以后再次变更账期!");
           return false;
       }
       
	}
	
	if(acctdaydistrbute == "4" ){
	   //为非自然日出账的用户
	    if(ifNaturedayProduct == "true" || (ifNaturedayProduct == "false" && payType == "G")){
           //特殊产品必须自然月生效
           var result = "用户当前的出账日是[" + validacct_day + "]号,需把出账日改为[" + acctday + "]号才可办理该集团业务！";
			if(confirm(result + "是否确定变更?")){
				//调用隐藏按钮,触发事件
				$('#changeAcctDay').click();
				//redirectToNav('person.changeacctday.ChangeAcctDay', 'onInitTrade', '&SERIAL_NUMBER='+$('#cond_SERIAL_NUMBER').value+'&AUTH_AUTO=TRUE&ISGRP=TRUE&REMARK=办理集团产品需要变更账期为自然月','contentframe');
			}
           return false;
       }
	}
	
    if(active_tag == "true"){
        alert("请注意：此业务为预约业务，生效时间是["+firstdaynextacctday+"]!");
    }
    
    //验证付费方式
    if(ifgoon){
    	ifgoon = validateCrtMb();
    }
    
 	return ifgoon;
}

/*
 * 分散账期修改
 * 选择付费计划,覆盖组件中的方法
 * 新增成员加入集团的时候特殊产品处理和集团付费处理
 */
function selectPayPlanBaseInfo(){
	var payPlanType = $('#PLAN_TYPE').value;
	
	try {
	    var afterFunction = $('#AFTER_ACTION').val();
	    if (afterFunction != null && afterFunction != "") eval("" + afterFunction);
	} catch(e) {
	}
    
    //新增处理逻辑 add start
    var bookingDiv = $('#displayBookingDiv');//预约区域
    var acctdaydistrbute = getElementValue('ACCTDAY_DISTRIBUTION');//账期分布
    var ifNaturedayProduct = getElementValue('IF_NATUREDAY_PRODUCT');//是否必须要求成员账期为自然月的产品 true：是 ; false：否
    var ifImmediProduct = getElementValue('IF_IMMEDI_PRODUCT');//是否为能立即生效的产品 true：是 ; false：否
    
    var descObj = $('#cond_Desc');//提示域
    descObj.innerText = "";//初始化
    var descInfo = "";//提示信息
    
    //优先提示特殊产品信息
    if(ifNaturedayProduct == "true"){
    	descInfo = "提示：此产品不支持分散账期的用户";
    }else if(ifNaturedayProduct == "false" && payPlanType == "G"){
    	descInfo = "提示：集团付费不支持分散账期的用户";
    }
        
    //根据成员不同账期分布,处理提示信息
    //TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"),FALSE("4")
    if(ifNaturedayProduct == "true" || (ifNaturedayProduct == "false" && payPlanType == "G")){
    	if(acctdaydistrbute == "1"){
    		if(ifImmediProduct == "true" && payPlanType == "G"){
    			descInfo += ",当前用户可办理预约业务!";
    		}else if(ifImmediProduct == "true" && payPlanType == "P"){
    			descInfo += "当前用户选择个人付费,可以立即生效!";
    		}else if(ifImmediProduct == "false"){
    			descInfo += ",当前用户可办理预约业务!";
    		}
    	}else if("2" == acctdaydistrbute || "3" == acctdaydistrbute){
    		descInfo += ",当前用户不容许办理业务!";
    	}else if("4" == acctdaydistrbute){
    		descInfo += ",当前用户必须将出账日改为1号才可办理业务!";
    	}
    	descObj.innerText = descInfo;
    }
    
    //界面元素的初始化
    if(bookingDiv){
    	var effectNowObj = $('#divEffectNow');
    	//第一刷新页面的时候,还未选择付费方法的时候
    	bookingDiv.style.display = "none";
    	effectNowObj.attr('disabled','false');//此处用false,不要用"false"
    	effectNowObj.checked = false;
    	$('#ifBooking').value = "false";
    }
    
    //根据付费方法显示[是否下账期]生效信息
    //个人付费: bookingDiv预约组件是否存在
    if(payPlanType == "P" && bookingDiv){
    	//必须要求为自然月账期
    	if(ifNaturedayProduct == "true"){
    		if(acctdaydistrbute == "1"){
    			bookingDiv.css('display','block');
    			if(ifImmediProduct == "false"){
    				var effectNowObj = $('#divEffectNow');
    				effectNowObj.disabled = true;//此处用true,不要用"true"
    				effectNowObj.checked = true;
    				$('#ifBooking').value = "true";
    			}
    		}
    	}else if(ifNaturedayProduct == "false"){
    		bookingDiv.css('display','block');
    	}
    }else if(payPlanType == "G" && bookingDiv){//集团付费: bookingDiv预约组件是否存在
    	if(acctdaydistrbute == "1") {
    		bookingDiv.css('display','block');
    		var effectNowObj = $('#divEffectNow');
    		effectNowObj.disabled = true;//此处用true,不要用"true"
    		effectNowObj.checked = true;
    		$('#ifBooking').value = "true";
    	}
    }
    
    if (payPlanType == "G") {
	    hidden($('#intItemPart'),false);
	} else {
	    hidden($('#intItemPart'),true);
	}
	//初始化
	payPlanSelInit();
}