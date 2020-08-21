//设定一个变量记录是否已经填写过数据
var isSetData = 0;
var oldService = 0;

//加这个变量是为了解决服务参数填写完后，点击确定，再打开，服务代码为空的问题，这种逻辑也只能解决填完A，再打开A的情况
//不能解决填完A再填B，填完B再打开A的情况，根本原因是因为不懂为什么隐藏域的值可以完好的传到后台却不能在 再打开前台的时候展示出来，只要能找到导致这种情况的原因，问题就迎刃而解，然而自己太菜了
//结合下面第18行的注解，会发现第6行的注解情况其实问题不大，因为它会生成一个新的服务代码，不会展示为空，虽然达不到最佳效果，但也能接受咯
//问题是ADC没问题，MAS该怎么办呀，头疼，测试环境很多MAS读取服务代码配置都是空，不知道测试环境情况如何
var shortnum = 0;

function initPageParam()
{
	//初始化服务参数界面
	var product_id = $("#cond_OFFER_CODE").val();
	var element_id = $("#pam_SERVICE_ID").val();
	
	//这一步只能解决同时订购多种不同的服务,但解决不了的情况有:先填完一个服务A,再去填服务B,再打开服务A,这时候服务代码会读配置里的,而不是自己手动填写的,总之头很疼
	//可以考虑用一个数组存储所有点击过的服务,点击开的界面服务再与之前的进行对比,这样重复的服务界面就不会再走配置
	if(element_id!=oldService){
		isSetData=0;
		oldService=element_id;
		shortnum=0;
	}
	
	loadUserParamInfo(product_id,element_id);
}

function checkSub(obj)
{
	setData(obj);
	
	if(isSetData == 2){
		isSetData = 0;
		return false;
	}
	
	if(!submitOfferCha())
		return false; 
	
	isSetData = 1;
	
	backPopup(obj);
}

function loadUserParamInfo(product_id,element_id){

	var info = $.enterpriseLogin.getInfo();
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	
	
	var prodcutId =product_id;
	var serviceId = element_id;
	var svcparamvalue ="";
	var userId ="";
	var groupId = groupInfo.get("GROUP_ID");
	var custId = groupInfo.get("CUST_ID");
	var grpUserEparchyCode = groupInfo.get("EPARCHY_CODE");

	//老逻辑用user_id是否为空进行判断是新增还是变更，新逻辑不清楚是否可行，或者用modify_tag进行判断，看是否能成功
	//新的互联网界面暂时先不考虑ESOP系统，所以这些判断先去掉

	 putpagedata(serviceId);
     setStatetype();	//根据不同的操作类型，页面输入框的可见性.
     checkmodifypriv();
     setDeliverNumDis();// 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
}

function putpagedata(serviceId){

 dealOperStateoptions();

 //只有新增时，才从库取配置

 //生成服务代码尾数
 var vModiState = $("#pam_MODIFY_TAG").val();
 if (vModiState == '0'&& isSetData == '0')
 {
	 //alert($("#pam_SVR_CODE_END").val());
	 $("#SVR_CODE_END_SECEND").val($("#pam_SVR_CODE_END").val()) ;
	 $("#SVR_CODE_END_SECEND").attr("disabled",false);
 }
else if(isSetData == '1'){
	var vall=shortnum;
	var vpart=$("#pam_SVR_CODE_HEAD").val();
	var lall=vall.length;
	var lpart=vpart.length;
	var vtail=vall.substring(lpart,lall);
	$("#pam_SVR_CODE_END").val(vtail) ;
	$("#SVR_CODE_END_SECEND").val(vtail) ;
}
else
{
	var vall=$("#pam_BIZ_IN_CODE").val();
	var vpart=$("#pam_SVR_CODE_HEAD").val();
	var lall=vall.length;
	var lpart=vpart.length;
	var vtail=vall.substring(lpart,lall);
	$("#pam_SVR_CODE_END").val(vtail) ;
	$("#SVR_CODE_END_SECEND").val(vtail) ;
}


//如果企业邮箱产品　则显示邮箱域名要素
 var productid = $("#cond_OFFER_CODE").val();
 if(productid=="10005801")
 {
   $("#pam_RSRV_STR2").closest("li").css("display","");
   $("#pam_RSRV_STR2").css("display","");
 }
 else
 {
 	//$("#pam_RSRV_STR2").closest("li").css("display","none");
 }
 if(productid=="10009150")
 {
	
 	$("#pam_SP_CODE").closest("li").css("display","");
 	$("#pam_SP_CODE_span").css("display","");
 	$('#pam_SP_CODE').attr('nullable',"no");
 	$("#pam_SP_CODE").closest("li").attr("class", "link required");

 }
 else
 {
	//$("#pam_SP_CODE").closest("li").css("display","none");
 }
 //如果是集团通讯录
 if(productid=="10009805")
 {
 	$("#pam_CITY_CODE").closest("li").css("display","");
 	$("#pam_CITY_CODE").css("display","");
 	
 	$("#pam_CUST_MANAGER_ID").closest("li").css("display","");
 	$("#pam_CUST_MANAGER_ID").css("display","");
 	
 	$("#pam_STAFF_NAME").closest("li").css("display","");
 	$("#pam_STAFF_NAME").css("display","");
 	
 	$("#pam_SERVICE_ID").closest("li").css("display","");
 	$("#pam_SERVICE_ID").css("display","");

 }
 else
 {
//	 $("pam_CITY_CODE").closest("li").css("display","none");
//	 	$("pam_CUST_MANAGER_ID").closest("li").css("display","none");
//	 	$("pam_STAFF_NAME").closest("li").css("display","none");
//	 	$("pam_SERVICE_ID").closest("li").css("display","none");
 }

 // 全网彩信页面的提示语BUG修改
 var bizTypeCode=$("#pam_BIZ_TYPE_CODE").val();
 if (bizTypeCode == "001") {
   $("#pam_MAX_ITEM_PRE_DAY").text("每天最大短信数：");
   $("#pam_MAX_ITEM_PRE_MON").text("每月最大短信数：");
   $("#pam_IS_TEXT_ECGN").text("短信正文签名：");
 } else if (bizTypeCode == "002") {
   $("#pam_MAX_ITEM_PRE_DAY").text("每天最大彩信数：");
   $("#pam_MAX_ITEM_PRE_MON").text("每月最大彩信数：");
   $("#pam_IS_TEXT_ECGN").text("彩信正文签名：");
 }

//关于BOSS/ESOP集团短彩信端口新增端口分类的需求
 var isDisplay = $("#pam_IS_SPEC_AREA_DISPLAY").val();
 if (isDisplay == '1') {
	 
	 $("#pam_SERVICE_TYPE").closest("li").css("display","");
	 $("#pam_SERVICE_TYPE").closest("li").attr("class", "link required");
	 $("#pam_SERVICE_TYPE_span").css("display","");
	 //$("#pam_SERVICE_TYPE").css("display","");

	 $("#pam_WHITE_TOWCHECK").closest("li").css("display","");
	 $("#pam_WHITE_TOWCHECK").closest("li").attr("class", "link required");
	 $("#pam_WHITE_TOWCHECK_span").css("display","");
	 
	 $("#pam_SMS_TEMPALTE").closest("li").css("display","");
	 $("#pam_SMS_TEMPALTE").closest("li").attr("class", "link required");
	 $("#pam_SMS_TEMPALTE_span").css("display","");
	 
	 $("#pam_PORT_TYPE").closest("li").css("display","");
	 $("#pam_PORT_TYPE").closest("li").attr("class", "link required");
	 $("#pam_PORT_TYPE_span").css("display","");

	 $('#pam_SERVICE_TYPE').attr('nullable',"no");
	 $('#pam_WHITE_TOWCHECK').attr('nullable',"no");
	 $('#pam_SMS_TEMPALTE').attr('nullable',"no");
	 $('#pam_PORT_TYPE').attr('nullable',"no");
 } else {
//	 $("pam_SERVICE_TYPE").closest("li").css("display","none");
//	 $("pam_WHITE_TOWCHECK").closest("li").css("display","none");
//	 $("pam_SMS_TEMPALTE").closest("li").css("display","none");
//	 $("pam_PORT_TYPE").closest("li").css("display","none");
 }
}


/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	08-变更 04- 暂停 05-恢复
 */
function setStatetype(){
 var operState=$("#pam_OPER_STATE").val();
  if (operState == "08")
  {
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_BIZ_CODE").attr('disabled',true);
	  $("#pam_BIZ_NAME").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_STATUS").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_RSRV_TAG2").attr('disabled',true);
	  enabledparam();
	  showBizInCode();
  }
  if (operState == "04")
  {
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
      $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_OPER_STATE").attr('disabled',false);
	  $("#pam_DELIVER_NUM").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_DAY").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',true);

	  disabledparam();
	  showBizInCode();
  }
  if (operState == "05")
  {
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
      $("#pam_OPER_STATE").attr('disabled',false);
      $("#pam_DELIVER_NUM").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_DAY").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',true);
	  disabledparam();
	  showBizInCode();
  }
  if (operState == "01")
  {
	  $("#pam_PRE_CHARGE").attr('disabled',true);//预付费标记
	  $("#pam_BIZ_CODE").attr('disabled',true);
	  $("#pam_BIZ_NAME").attr('disabled',true);
	  $("#pam_SVR_CODE_HEAD").attr('disabled',true);
	  //$("#pam_BIZ_ATTR").attr('disabled',true);
	  $("#pam_BILLING_TYPE").attr('disabled',true);
	  $("#pam_PRICE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_STATUS").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#pam_RSRV_TAG2").attr('disabled',true);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  if(!$("#pam_BILLING_TYPE").val())
	  {
	  	$("#pam_BILLING_TYPE").val("00");//没值的情况默认免费
	  }
   }
}

function enabledparam(){
	//$("#pam_PLAT_SYNC_STATE").attr('disabled',false);
	$("#pam_SERVICE_TYPE").attr('disabled',false);
    $("#pam_WHITE_TOWCHECK").attr('disabled',false);
    $("#pam_SMS_TEMPALTE").attr('disabled',false);
    $("#pam_PORT_TYPE").attr('disabled',false);
    $("#pam_AUTH_CODE").attr('disabled',false);
    $("#pam_BIZ_PRI").attr('disabled',false);
    $("#pam_USAGE_DESC").attr('disabled',false);
    $("#pam_INTRO_URL").attr('disabled',false);
    $("#pam_CS_URL").attr('disabled',false);
    $("#pam_IS_TEXT_ECGN").attr('disabled',false);
    $("#pam_DEFAULT_ECGN_LANG").attr('disabled',false);
    $("#pam_TEXT_ECGN_ZH").attr('disabled',false);
    $("#pam_TEXT_ECGN_EN").attr('disabled',false);
    $("#pam_BILLING_MODE").attr('disabled',false);
    $("#pam_ADMIN_NUM").attr('disabled',false);
    $("#pam_RSRV_TAG2").attr('disabled',false);
    $("#pam_FORBID_START_TIME_A").attr('disabled',false);
    $("#pam_FORBID_END_TIME_A").attr('disabled',false);
    $("#pam_FORBID_START_TIME_B").attr('disabled',false);
    $("#pam_FORBID_END_TIME_B").attr('disabled',false);
    $("#pam_FORBID_START_TIME_C").attr('disabled',false);
    $("#pam_FORBID_END_TIME_C").attr('disabled',false);
    $("#pam_FORBID_START_TIME_D").attr('disabled',false);
	$("#pam_FORBID_END_TIME_D").attr('disabled',false);
}

function disabledparam(){
	
	$("#pam_SERVICE_TYPE").attr('disabled',true);
    $("#pam_WHITE_TOWCHECK").attr('disabled',true);
    $("#pam_SMS_TEMPALTE").attr('disabled',true);
    $("#pam_PORT_TYPE").attr('disabled',true);
    $("#pam_AUTH_CODE").attr('disabled',true);
    $("#pam_BIZ_PRI").attr('disabled',true);
    $("#pam_USAGE_DESC").attr('disabled',true);
    $("#pam_INTRO_URL").attr('disabled',true);
    $("#pam_CS_URL").attr('disabled',true);
    $("#pam_IS_TEXT_ECGN").attr('disabled',true);
    $("#pam_DEFAULT_ECGN_LANG").attr('disabled',true);
    $("#pam_TEXT_ECGN_ZH").attr('disabled',true);
    $("#pam_TEXT_ECGN_EN").attr('disabled',true);
    $("#pam_BILLING_MODE").attr('disabled',true);
    $("#pam_ADMIN_NUM").attr('disabled',true);
    $("#pam_RSRV_TAG2").attr('disabled',true);
    $("#pam_FORBID_START_TIME_A").attr('disabled',true);
    $("#pam_FORBID_END_TIME_A").attr('disabled',true);
    $("#pam_FORBID_START_TIME_B").attr('disabled',true);
    $("#pam_FORBID_END_TIME_B").attr('disabled',true);
    $("#pam_FORBID_START_TIME_C").attr('disabled',true);
    $("#pam_FORBID_END_TIME_C").attr('disabled',true);
    $("#pam_FORBID_START_TIME_D").attr('disabled',true);
	$("#pam_FORBID_END_TIME_D").attr('disabled',true);
}

/*校验是否有权限修改*/
function checkmodifypriv(){
	var hasmodifyprv=$("#pam_HASMODIFYPRV").val();
	if(hasmodifyprv=="true")
		{
		  $('#pam_RSRV_TAG2').attr('disabled',false);//客户等级
		
		}
	return;
}


function dealOperStateoptions()
{
	var modifytag=$('#pam_MODIFY_TAG').val();
	if("0"==modifytag)//新增时
	{
 	    $("#pam_OPER_STATE").val("01"); //新增
	    $('#pam_OPER_STATE').attr('disabled',true);

        var rsrvTag2="1";
        var classId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("CLASS_ID");

        if (classId=="5"|| classId=="6" || classId=="A1" || classId=="A2")
        {
            rsrvTag2="3";
        }
        if (classId=="7"|| classId=="8" || classId=="B1" || classId=="B2")
        {
            rsrvTag2="2";
        }
        $('#pam_RSRV_TAG2').val(rsrvTag2);//客户等级
	}
	else if("2"==modifytag)//对原有记录进行修改时
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
		    pam_OPER_STATE.remove("01");
		    pam_OPER_STATE.remove("02");
		    pam_OPER_STATE.remove("04");
		    pam_OPER_STATE.remove("08");
	 		$("#pam_OPER_STATE").val("05");//默认选为恢复
	  }
	  if(platsyncState=="1")//正常在用
	  {
		    pam_OPER_STATE.remove("01");
		    pam_OPER_STATE.remove("02");
		    pam_OPER_STATE.remove("05");
			$("#pam_OPER_STATE").val("08"); //默认选为变更
	  }

	  //变更时不能改服务代码
	   $('#pam_SVR_CODE_END').attr('disabled',true); //变更时不能改服务代码
	}

}

function setDeliverNumDis(){
	   var bizAttrValue=$("#pam_BIZ_ATTR").val();
	    var operState=$("#pam_OPER_STATE").val();
	   if(bizAttrValue==1||bizAttrValue==2||bizAttrValue==""||bizAttrValue==null||operState=="04"||operState=="05")
	   {
	       $("#pam_DELIVER_NUM").val("0");
	       $("#pam_DELIVER_NUM").attr('disabled',true);
	   }
	   else
	   {
	      $("#pam_DELIVER_NUM").attr('disabled',false);
	   }
	}





















//点确定按钮返回数据到父页面隐藏字段
function setData(obj)
{

	var ret = ischinese();//中文签名特殊字符检验，邮箱域名
	if (ret == '1')
	{
	   isSetData = 2;
	   return;
	}

	var itemPre = checkMessageAmount();//判断每月短信量和每日短信量
	if(!itemPre)
	{
	  isSetData = 2;
	  return;
	}

	var forbidflag =checkforbidtime();//校验允许下发时间
	if(!forbidflag)
	{
	  isSetData = 2;	
	  return;
	}
	var dealSCFlag= dealServCode();//校验服务代码
	if(!dealSCFlag)
	{
		isSetData = 2;
		return;
	}
	var adminFlag = checkAdminExist();/*校验管理员手机号*/
    if(!adminFlag)
	{
    	isSetData = 2;
		return;
	}
    var canFlag = canresume();//判断用户欠费状态下,不能做恢复操作
   	if(!canFlag)
	{
   		isSetData = 2;
		return;
	}
    var sigFlag = checkSignExist();//判断中英文签名是否存在敏感资费
    if(!sigFlag)
	{
    	isSetData = 2;
		return;
	}

    var productId= $("#cond_OFFER_CODE").val();

	if (productId == "9230") {
		// 企业短彩信 根据行业类型校验服务代码
	    var servCodeFlag = checkValidServCode();
	    if (!servCodeFlag) {
	    	isSetData = 2;
	    	return;
	    }

	    // 企业短彩信 校验吉祥号码
	    var beautifualFlag = checkIsBeautifual();
	    if (!beautifualFlag) {
	    	isSetData = 2;
	    	return;
	    }
	}

   //获取提交数据
   //commSubmit();
}

/**
 * 企业短彩信 根据行业类型校验服务代码
 */
function checkValidServCode() {
	var serCodeEnd = $('#pam_SVR_CODE_END').val();
	var custId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("CUST_ID");
	if(serCodeEnd == null  && serCodeEnd == "")
	{
		MessageBox.alert("",'服务代码尾号为空');
		 $("#pam_SVR_CODE_END").select();
		 return false;
	}
	var servCodeFlag=true;
	var param ='&SVR_CODE_END='+serCodeEnd+'&CUST_ID='+custId;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "checkValidServCode",param, function(data){
		$.endPageLoading();
		servCodeFlag = aftercheckValidServCode(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });

	return servCodeFlag;
}

function aftercheckValidServCode(data)
{
	var flag=data.get(0,"IS_FLAG");
	if(flag=="false")
	{
		MessageBox.alert("","服务代码尾号代码不属于集团客户的行业类型，请重新输入！");
		$("#pam_SVR_CODE_END").select();//focus();
		return false;
	}
	return true;
}

/**
 * 企业短彩信 校验吉祥号码
 */
function checkIsBeautifual() {
	var serCodeEnd = $('#pam_SVR_CODE_END').val();
	var serCodeHead = $("#pam_SVR_CODE_HEAD").val();
	if(serCodeEnd == null  && serCodeEnd == "")
	{
		 MessageBox.alert("",'服务代码尾号为空');
		 $("#pam_SVR_CODE_END").select();
		 return false;
	}

	var regu = "(000|111|222|333|444|555|66|777|88|99)$";
	var reg = new RegExp(regu);
	if (!reg.test(serCodeEnd)) {
		return true;
	}

	var beautifualFlag=true;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "checkIsBeautifual",'&SVR_CODE='+serCodeHead+serCodeEnd, function(data){
		$.endPageLoading();
		beautifualFlag = aftercheckIsBeautifual(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
	});

	return beautifualFlag;
}

function aftercheckIsBeautifual(data)
{
	var flag=data.get(0,"IS_FLAG");
	if(flag=="false")
	{
		MessageBox.alert("","服务代码为吉祥号码，请先进行预占！");
		$("#pam_SVR_CODE_END").select();//focus();
		return false;
	}
	return true;
}

/*
   1－永久白名单
   2－黑名单
   3－限制次数白名单
   4－点播业务
  如果业务类型是3或是4时，需要填写pam_BIZ_ATTR。
*/
function chargeBizAttr(){
   setDeliverNumDis();//根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
}

/**
*服务代码处理
**/
function dealServCode()
{
 //today对EC基本接入号，业务接入号（服务代码）进行处理
  var vHead = $("#pam_SVR_CODE_HEAD").val(); 
  var vTail = $("#SVR_CODE_END_SECEND").val();
  var cLen = $("#pam_C_LENGTH").val();

  if(cLen!=""&&vTail.length!=cLen)
  {
	MessageBox.alert("服务代码尾号必须为"+cLen+"位！");
	return false;
  }

  	var numberre = /[^0-9]/g;
  	var numrst = vTail.match(numberre);   // 在字符串 s 中查找匹配。
	if(numrst!=null)
	{
		MessageBox.alert("服务代码必须为数字!");
   		return false;
   	}
    var userId = "";//暂时写为空，做变更的时候再调整
	if(userId==undefined)
	{
		userId="";
	}
    var vServCode = vHead + vTail ;

    shortnum = vServCode;
    
  //业务接入号被赋予 wade 控件，以便传到后台
  $("#pam_BIZ_IN_CODE").val(vServCode);

  $("#pam_SVR_CODE_END").val(vTail);
  
  //业务接入号填回到EC基本接入号
  $('#pam_EC_BASE_IN_CODE').val(vServCode);

  /**
  var oldBizInCode=$.getSrcWindow().$('#OLD_BIZ_IN_CODE').val();
  if(oldBizInCode!=""){
				if(vTail!=oldBizInCode){
				alert('同一集团产品下的服务代码要一致!');
				return;
			}

  }
  **/
	var groupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
	var bizFlag = true;

	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "getDumpIdByajax",'&GROUP_ID='+groupId+'&BIZ_IN_CODE='+vServCode, function(data){
		$.endPageLoading();
		bizFlag = checkBizInCodeByajax(data);//生成的服务代码不能使用false
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });
	
	return bizFlag;
}
function chgBizCode()
{
	var bizCode=$('#pam_BIZ_CODE').val();
	var servType=$('#pam_SERVICE_TYPE').val();
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "createNewBizCode",'&BIZ_CODE='+bizCode+'&SERV_TYPE='+servType, function(data){
		$.endPageLoading();
		$('#pam_BIZ_CODE').val(data.get(0,"BIZ_CODE"));
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });
}
//获取提交数据
function commSubmit()
{
	var itemIndex= "";
	var urlParts = document.URL.split("?");
	var parameterParts = urlParts[1].split("&");
	for (i = 0; i < parameterParts.length; i++)
	{
		var pairParts = parameterParts[i].split("=");
		var pairName = pairParts[0];
		var pairValue = pairParts[1];

		if(pairName=="ITEM_INDEX")
		{
		      itemIndex=pairValue;
		}
	}

	var inputElements=document.getElementsByTagName('INPUT');
	for(i=0;i<inputElements.length;i++)
	{
	  inputElements[i].disabled=false;
	}


	var selectElements=document.getElementsByTagName('SELECT');
	for(i=0;i<selectElements.length;i++)
	{
		selectElements[i].disabled=false;
	}


	$("#CANCLE_FLAG").val("false");
	var paramset = $.DatasetList();
	var svcparamdata=$.DataMap();

	var platsvcparam=$.ajax.buildJsonData("adcServicParamsForm_pam","pam");//获取表单以pam开头的数据
	var platsvcdata=$.DataMap(platsvcparam);
	svcparamdata.put("PLATSVC",platsvcdata);//PLATSVC存放页面以pam开头的数据
	svcparamdata.put("CANCLE_FLAG",$("#CANCLE_FLAG").val());
	var molist="";
	try
	{
		molist = $.table.get("MoListTable").getTableData("SEQ_ID,MO_CODE,MO_MATH,MO_TYPE,DEST_SERV_CODE,DEST_SERV_CODE_MATH",false);
	}
	catch(e)
	{
	  molist="";
	}
	if(molist != ""){
	  svcparamdata.put("MOLIST",molist);
	}
	var serviceId=$("#SERVICE_ID").val();
	svcparamdata.put("ID",serviceId);
	var paramVerifySucc=new Wade.DataMap(); //加上一个表示点过确认按钮的 DataMap
	paramVerifySucc.put("PARAM_VERIFY_SUCC","true");

	paramset.add(paramVerifySucc);
	paramset.add(svcparamdata);
    $("#adcServicParamsForm_pam").attr('disabled',false);//设置整个form元素都可见
	$.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
	//$.getSrcWindow().$("#OLD_BIZ_IN_CODE").val($('#svrCodeTail').val());//设置服务代码到父页面 保证同一个产品受理时不同服务的服务代码相同
	$.setReturnValue();
	setStatetype();
}


/*校验管理员手机号*/
function checkAdminExist()
{

	var adminSerNum = $('#pam_ADMIN_NUM').val();
	if(adminSerNum == null  || adminSerNum == "")
	{
		MessageBox.alert("",'管理员号码为空');
		 $("#pam_ADMIN_NUM").select();
		 return false;
	}
	var adminFlag=true;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "getDumpSnByajax",'&SERIAL_NUMBER='+adminSerNum, function(data){
		$.endPageLoading();
		adminFlag = afterCheckAdminExist(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });

	return adminFlag;

}

function afterCheckAdminExist(data)
{
	var SnFlag=data.get(0,"IS_FLAGSN");
	if(SnFlag=="true")
	{
		MessageBox.alert("","管理员号码不是有效的在网用户，请重新输入！");
		//disableEle();
		$("#pam_ADMIN_NUM").select();//focus();
		return false;
	}
	return true;
	//commSubmit();
}

//校验业务接入码
function checkBizInCodeByajax(data)
{
	//判断当前是新增还是修改页面 today
	if ($("#pam_MODIFY_TAG").val() == '0')
	{
	   var flag = data.get(0,"ISCHECKAACCESSNUMBER");
		if(flag == "false")
		{
			MessageBox.alert("","生成的服务代码不能使用，请手动输入！");
			$("#pam_SVR_CODE_END").focus();
			return false;
		}
	}
	return true;

}



function dealBizCodetailDisable() //判断是用户开户 还是用户变更界面 如果是变更界面 限制服务代码不能修改
{
     var modifypageflag=false;
	 var selectElements=window.parent.document.getElementById('SELECTED_ELEMENTS').value;
	 var selectElementset = new Wade.DatasetList(selectElements);

	 for(var i=0;i<selectElementset.getCount();i++)//分理出产品变更哪些服务做了暂停恢复操作,存在stopsvcstr里面 和哪些产品元素做了修改存储在svcDataset里面
  	 {
  	    if(modifypageflag)
   	    {
   	    	break;
   	    }
  	 	var elementData=selectElementset.get(i);
  	 	var elementsDataset = elementData.get("ELEMENTS"); // 取元素
  	 	var productMode=elementData.get("PRODUCT_MODE");
  	 	for(var j=0;j<elementsDataset.getCount();j++)
   	 	{

   	 		var packageData = elementsDataset.get(j); // 取每个元素

   	 		var elementType = packageData.get("ELEMENT_TYPE_CODE", "");
   	    	var elementstate=packageData.get("STATE");
   	    	if(elementstate!="ADD")
   	    	{
   	    	  modifypageflag=true;
   	    	  $("#pam_SVR_CODE_END").attr('disabled',true);
   	    	  break;
   	    	}
   	    }
   	  }
}






function canresume()
{
	var canFlag = true;
	var userId="";//$.getSrcWindow().$("#USER_ID").val();  先注释为空 等做变更的时候再调整
	var operstate=$('#pam_OPER_STATE').val();
	if(operstate=="05")
	{
		var custId = $("#cond_CUST_ID").val();
		var productId    = $("#cond_OFFER_CODE").val();
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "grpUserOweFeeByajax",'&CUST_ID='+custId+'&PRODUCT_ID='+productId, function(data){
			$.endPageLoading();
			canFlag =  aftercanresume(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
			},
	        {
			  async: false
	    });
	}
	return canFlag;
}
function aftercanresume(owefeeset)
{
	var owefee=parseInt(owefeeset.get(0,"OWE_FEE"));//实时欠费信息
	var createvalue=parseInt(owefeeset.get(0,"CREDIT_VALUE"));//用户信用度
	//var rst=owefee +createvalue*100;
	//alert('实时欠费的费用为:'+owefee+"用户信用度为:"+createvalue+" 最后值:"+rst);
	if( owefee +createvalue*100<0)
	{
		MessageBox.alert("",'集团产品已经欠费,不能执行[恢复]操作！请先缴清费用后再执行[恢复]操作!');
		return false;
	}
	return true;
	//checkSignExist();//判断中英文签名是否存在敏感资费
}


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
		MessageBox.alert("","不允许下发开始时间1和不允许下发结束时间1 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimeb==""&&forbidendtimeb!="")||(forbidstarttimeb!=""&&forbidendtimeb==""))
	{
		MessageBox.alert("","不允许下发开始时间2和不允许下发结束时间2 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimec==""&&forbidendtimec!="")||(forbidstarttimec!=""&&forbidendtimec==""))
	{
		MessageBox.alert("","不允许下发开始时间3和不允许下发结束时间3 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	if((forbidstarttimed==""&&forbidendtimed!="")||(forbidstarttimed!=""&&forbidendtimed==""))
	{
		MessageBox.alert("","不允许下发开始时间4和不允许下发结束时间4 必须配对 \n可以都为空,或都不为空!");
		return false;
	}
	return true;
}


//当计费类型变更时，做的判断
function chargeTypeChanged()
{
	var bizAttr=$('#pam_BIZ_ATTR').val();
	var billType = $('#pam_BILLING_TYPE').val();
	if(bizAttr != '0')
	{
		if (billType!='00')
		{
			MessageBox.alert("",'业务属性为黑白名单时，计费类型必须为免费，且单价须为0！');
			$('#pam_BILLING_TYPE').val('00');
			$('#pam_PRICE').val('0');
		}
	}
	if(bizAttr == '0')
	{
		if (billType=='00')
		{
			MessageBox.alert("",'业务属性为订购关系时，计费类型不能是免费！');
			$('#pam_BILLING_TYPE').val('01');
		}
	}
}




/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) {
    //判断是否存在
    if (jsSelectIsExitItem(objSelect, objItemValue)) {
        MessageBox.alert("","该Item的Value值已经存在");
    } else {
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
    }
}

 function jsSelectIsExitItem(objSelect, objItemValue) {
    var isExit=false;
    objSelect.children("option").each(function(){
         if(this.value==objItemValue)
            return !isExit;
    });
   return isExit;
}

function isCharsInBag (s, bag)
{
	var i,c;
	for (var i = 0; i < s.length; i++)
	{
		c = s.charAt(i);//字符串s中的字符
		if (bag.indexOf(c) > -1)
		return c;
	}
	return "";
}

function ischinese()
{

	var zh = $("#pam_TEXT_ECGN_ZH").val();
	var en = $("#pam_TEXT_ECGN_EN").val();
	var em = $("#pam_RSRV_STR2").val();//邮箱域名

	var re = /[&\*\+\.\[\]%\$\^\?\{}\|\\#@!~]/g;
	var rn = /[^A-Za-z0-9_\s]/g;
	//var rm = /\w+\.\w+$/g;

	var r = zh.match(re);
	var e = en.match(rn);
	//var m = em.match(rm);
	if(r!=null)
	{
		$.validate.alerter.one($("#pam_TEXT_ECGN_ZH")[0],"中文签名,不允许包含除中文 字母 数字外的特殊字符,请检查输入!\n");
		//MessageBox.alert("","中文签名,不允许包含除中文 字母 数字外的特殊字符,请检查输入!");
		return '1';
	}
	if(e!=null)
	{
		$.validate.alerter.one($("#pam_TEXT_ECGN_EN")[0],"英文签名,不允许包含除字母 数字外的特殊字符,请检查输入!\n");
		//MessageBox.alert("","英文签名,不允许包含除字母 数字外的特殊字符,请检查输入!");
		return '1';
	}

	var l = zh.length;
	if(l>18||l<2)
	{
		$.validate.alerter.one($("#pam_TEXT_ECGN_ZH")[0],"中文签名，最多输入2-18个汉字之间!\n");
		//MessageBox.alert("","中文签名，最多输入3-8个汉字之间!\n");
	    $("#pam_TEXT_ECGN_ZH").select();
	   	return '1';
	}

	var len = en.length;
	if(len>36||len<4) {
		$.validate.alerter.one($("#pam_TEXT_ECGN_EN")[0],"最多必须输入4-36个字符之间!\n");
		disableEle();
		$("#pam_TEXT_ECGN_EN").select();
		return '1';
	}
	/**
	if(m!=null&&(em=!null||em!=""))
	{
		alert("邮箱域名格式不正确,请检查输入!");
		return '1';
	}
	**/

}

function disableEle()
{
  //20090527重设为默认的 disabled
	$("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	$("#pam_SIBASE_INCODE_A").attr('disabled',true);
	$("#pam_BIZ_CODE").attr('disabled',true);
	$("#pam_BIZ_NAME").attr('disabled',true);
	$("#pam_SVR_CODE_HEAD").attr('disabled',true);
	$("#pam_BILLING_TYPE").attr('disabled',true);
	$("#pam_PRICE").attr('disabled',true);
	$("#pam_BIZ_STATUS").attr('disabled',true);
	$("#pam_BIZ_ATTR").attr('disabled',true);
	$("#pam_PRE_CHARGE").attr('disabled',true);
	//$("#pam_IS_TEXT_ECGN").attr('disabled',true);
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist(){
    var signFlag = true;
    var productid=$("#cond_OFFER_CODE").val();
	var param ='&TEXT_ECGN_ZH='+$('#pam_TEXT_ECGN_ZH').val()+'&TEXT_ECGN_EN='+$('#pam_TEXT_ECGN_EN').val()+'&PRODUCT_ID='+productid;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.adc.AdcParamHandler", "getSensitiveTextByajax",param, function(data){
		$.endPageLoading();
		signFlag = afterCheckSignExist(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });

	return signFlag;
}


function afterCheckSignExist(data){
	var inProduct=data.get(0,'IN_PRODUCT');
	if(inProduct=="true"){
		var hasZh = data.get(0,"HAS_ZH");
		if(hasZh=="true"){
			MessageBox.alert("","对不起，中文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_ZH").select();
			return false;
		}

		var hasEn = data.get(0,"HAS_EN");
		if(hasEn=="true"){
			MessageBox.alert("","对不起，英文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_EN").select();
			return false;
		}
	}
	return true;
	//checkAdminExist();
}


function smsLimitCounCheck(element)
{
    var str=$(element).val();
	if(null==str||str==''||str=='0')
	{
	  $(element).val('1');
	}
	else
	{
	  $(element).val(str);
	}

	var max="100000";
	var rsrvTag2=$('#pam_RSRV_TAG2').val();
	var errinfo="";
	if (parseInt(str)<=parseInt('0'))
	{
		MessageBox.alert("","最大短信数不能小于或等于0!");
		element.focus;
		return '1';

	}

	if(rsrvTag2=="1")//普通级：不能超过10万
	{
	   max="100000";
	   errinfo="普通级：最大短信数不能超过10万!";
	}
    if (rsrvTag2=="2") //银牌级：不能超过100万
    {
	   max="1000000";
	   errinfo="银牌级：最大短信数不能超过100万!";
    }
	if (rsrvTag2=="3") //金牌级：不能超过500万
	{
		 max="5000000";
		 errinfo="金牌级：最大短信数不能超过500万!";
	}
	if (rsrvTag2=="4") //VIP级：不能超过1000万改成不超过10亿
	{
		 max="1000000000";
		 errinfo="VIP级：最大短信数不能超过10亿!";
	}

	if (parseInt(str)>parseInt(max))
	{
		MessageBox.alert("",errinfo);
		element.focus;
		return '1';

	}
}


/**
*作用：控制服务代码的显示，及校验标示的去必填
*/
function showBizInCode(){
	$("#pam_BIZ_IN_CODE").closest("li").css("display","");
	$("#pam_BIZ_IN_CODE_span").css("display","");
	$("#pam_BIZ_IN_CODE").closest("li").attr("class", "link required");
	$("#pam_BIZ_IN_CODE").css("display","");

	$("#pam_SVR_CODE_HEAD").closest("li").css("display","none");
	$("#pam_SVR_CODE_HEAD_span").css("display","none");
	$("#SVR_CODE_END_SECEND").css("display","none");
	$("#pam_SVR_CODE_HEAD").attr('nullable','yes');
	$("#pam_SVR_CODE_END").attr('nullable','yes');

}

/**
*作用：校验日下发短信量和月下发短信量
*/
function checkMessageAmount(){

	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue)){
		MessageBox.alert("","您输入的每月短信数不能小于每日短信数，请重新输入！");
		return false;
	}
	var max="100000";
	var rsrvTag2=$('#pam_RSRV_TAG2').val();
	var errinfo="";
	if (monValue<="0")
	{
		MessageBox.alert("","最大短信数不能小于或等于0!");
		$("#pam_MAX_ITEM_PRE_MON").focus;
		return false;

	}

	if(rsrvTag2=="1")//普通级：不能超过10万
	{
	   max="100000";
	   errinfo="普通级：最大短信数不能超过10万!";
	}
    if (rsrvTag2=="2") //银牌级：不能超过100万
    {
	   max="1000000";
	   errinfo="银牌级：最大短信数不能超过100万!";
    }
	if (rsrvTag2=="3") //金牌级：不能超过500万
	{
		 max="5000000";
		 errinfo="金牌级：最大短信数不能超过500万!";
	}
	if (rsrvTag2=="4") //VIP级：不能超过1000万
	{
		 max="10000000";
		 errinfo="VIP级：最大短信数不能超过1000万!";
	}

	if (parseInt(monValue)>parseInt(max))
	{
		MessageBox.alert("",errinfo);
		$("#pam_MAX_ITEM_PRE_MON").focus;
		return false;

	}
	return true;

}

function toggleshow(){
	$("#UI_show").css("display","none"); 
	$("#UI_hide").css("display","block"); 
	$("#accountInfo").css("display","block");
}

function togglehide(){
	$("#UI_hide").css("display","none"); 
	$("#UI_show").css("display","block"); 
	$("#accountInfo").css("display","none");
}