//设定一个变量记录是否已经填写过数据
var isSetData = 0;
var oldService = 0;

//详细情况看ADC的JS，MAS懒得写了
//不知道为什么取不到值啊，只好将shortnum=服务代码尾，再次点击的时候直接赋值
var shortnum = 0;

function initPageParam()
{
	//初始化服务参数界面
	var product_id = $("#cond_OFFER_CODE").val();
	var element_id = $("#pam_SERVICE_ID").val();
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
	
	//加这段逻辑是为了跳过submitOfferCha方法对该字段判断必须大于0
	var limitNum = $("#pam_DELIVER_NUM").val();
	if(limitNum==0)
		$("#pam_DELIVER_NUM").val("1");
	
	if(!submitOfferCha())
		return false; 
	
	if(limitNum==0)
		$("#pam_DELIVER_NUM").val("0");
	
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
}

function putpagedata(serviceId){

 dealOperStateoptions();

 //只有新增时，才从库取配置

 //生成服务代码尾数
 var modifytag = $("#pam_MODIFY_TAG").val();
 if (modifytag == '0')
 {
	 dealSpid_Adminnum();//设置集团管理员号码 和企业代码(取的集团客户信息里面维护的值)
	 dealClassId();      //客户等级
 }
  
 if (modifytag == '0'&& isSetData=='0')
	{
	   	putBizCodeForCrt();
	   	
	   	$("#pam_SERVICE_TYPE").closest("li").css("display","");
	 	$("#pam_SERVICE_TYPE_span").css("display","");
	 	$('#pam_SERVICE_TYPE').attr('nullable',"no");
	 	$("#pam_SERVICE_TYPE").closest("li").addClass("link required");
	 	$("#pam_WHITE_TOWCHECK").closest("li").css("display","");
	 	$("#pam_WHITE_TOWCHECK_span").css("display","");
	 	$('#pam_WHITE_TOWCHECK').attr('nullable',"no");
	 	$("#pam_WHITE_TOWCHECK").closest("li").addClass("link required");
	 	$("#pam_SMS_TEMPALTE").closest("li").css("display","");
	 	$("#pam_SMS_TEMPALTE_span").css("display","");
	 	$('#pam_SMS_TEMPALTE').attr('nullable',"no");
	 	$("#pam_SMS_TEMPALTE").closest("li").addClass("link required");
	 	$("#pam_PORT_TYPE").closest("li").css("display","");
	 	$("#pam_PORT_TYPE_span").css("display","");
	 	$('#pam_PORT_TYPE').attr('nullable',"no");
	 	$("#pam_PORT_TYPE").closest("li").addClass("link required");
	 	
	 	$("#pam_IS_TEXT_ECGN").attr("nullable","yes");
	 	$("#pam_SVR_CODE_END").attr("nullable","yes");
	 	
	 	$("#pam_BIZ_CODE").attr("disabled",false);
	 	$("#pam_BIZ_NAME").attr("disabled",false);
	 	
	 	$("#pam_DELIVER_NUM").closest("li").removeClass("link required");
	}	
 else if(isSetData == '1'){

		$("#pam_SVR_CODE_END").val(shortnum) ;
		$("#SVR_CODE_END_SECEND").val(shortnum) ;
		
		$("#pam_SERVICE_TYPE").closest("li").css("display","");
	 	$("#pam_SERVICE_TYPE_span").css("display","");
	 	$('#pam_SERVICE_TYPE').attr('nullable',"no");
	 	$("#pam_SERVICE_TYPE").closest("li").addClass("link required");
	 	$("#pam_WHITE_TOWCHECK").closest("li").css("display","");
	 	$("#pam_WHITE_TOWCHECK_span").css("display","");
	 	$('#pam_WHITE_TOWCHECK').attr('nullable',"no");
	 	$("#pam_WHITE_TOWCHECK").closest("li").addClass("link required");
	 	$("#pam_SMS_TEMPALTE").closest("li").css("display","");
	 	$("#pam_SMS_TEMPALTE_span").css("display","");
	 	$('#pam_SMS_TEMPALTE').attr('nullable',"no");
	 	$("#pam_SMS_TEMPALTE").closest("li").addClass("link required");
	 	$("#pam_PORT_TYPE").closest("li").css("display","");
	 	$("#pam_PORT_TYPE_span").css("display","");
	 	$('#pam_PORT_TYPE').attr('nullable',"no");
	 	$("#pam_PORT_TYPE").closest("li").addClass("link required");
	}
 else
  {
	  var vall=$("#pam_BIZ_IN_CODE").val();
	  var vpart=$("#pam_EC_BASE_IN_CODE").val();
	  var lall=vall.length;
	  var lpart=vpart.length;
	  var vtail=vall.substring(lpart,lall);
	  $("#pam_SVR_CODE_END").val(vtail) ;
	  $("#SVR_CODE_END_SECEND").val(vtail) ;
	  $("#pam_SVR_CODE_HEAD").val(vpart);
	  
	    $("#pam_SERVICE_TYPE").closest("li").css("display","");
	 	$("#pam_SERVICE_TYPE_span").css("display","");
	 	$('#pam_SERVICE_TYPE').attr('nullable',"no");
	 	$("#pam_SERVICE_TYPE").closest("li").addClass("link required");
	 	$("#pam_WHITE_TOWCHECK").closest("li").css("display","");
	 	$("#pam_WHITE_TOWCHECK_span").css("display","");
	 	$('#pam_WHITE_TOWCHECK').attr('nullable',"no");
	 	$("#pam_WHITE_TOWCHECK").closest("li").addClass("link required");
	 	$("#pam_SMS_TEMPALTE").closest("li").css("display","");
	 	$("#pam_SMS_TEMPALTE_span").css("display","");
	 	$('#pam_SMS_TEMPALTE').attr('nullable',"no");
	 	$("#pam_SMS_TEMPALTE").closest("li").addClass("link required");
	 	$("#pam_PORT_TYPE").closest("li").css("display","");
	 	$("#pam_PORT_TYPE_span").css("display","");
	 	$('#pam_PORT_TYPE').attr('nullable',"no");
	 	$("#pam_PORT_TYPE").closest("li").addClass("link required");
  }

 chargeBizAttr();
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
	  $("#pam_BILLING_TYPE").attr('disabled',false);
	  $("#pam_PRICE").attr('disabled',false);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_STATUS").attr('disabled',false);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#SVR_CODE_END_SECEND").attr('disabled',true);
	  
	  $("#pam_MO_ACCESS_NUM").attr('disabled',true);  //短信上行访问码，好像没用了。暂时不删除,设置为不能编辑
	  $("#pam_PRE_CHARGE").attr('disabled',false);
	  flipEnabled();
  }
  if (operState == "04")
  {
	  $("#pam_OPER_STATE").attr('disabled',false);
	  $("#pam_DELIVER_NUM").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_DAY").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',true);
	  flipDisabled();
  }
  if (operState == "05")
  {
      $("#pam_OPER_STATE").attr('disabled',false);
      $("#pam_DELIVER_NUM").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_DAY").attr('disabled',true);
	  $("#pam_MAX_ITEM_PRE_MON").attr('disabled',true);
	  flipDisabled();
  }
  if (operState == "01")
  {
	  if(!$("#pam_BILLING_TYPE").val())
	  {
	  	$("#pam_BILLING_TYPE").val("00");//没值的情况默认免费
	  }
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);

   }
  
//任何操作类型都不能变更的
  $("#pam_EC_BASE_IN_CODE").attr('disabled',true);
  $("#pam_EC_BASE_IN_CODE_A").attr('disabled',true);
  $("#pam_SVR_CODE_HEAD").attr('disabled',true);
  $("#pam_RSRV_TAG2").attr('disabled',true);
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
	  $('#pam_OPER_STATE').val('01');
	  $('#pam_OPER_STATE').attr('disabled',true);
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
 	  }else if(platsyncState=="1")//正常在用
 	  {
 		    pam_OPER_STATE.remove("01");
		    pam_OPER_STATE.remove("02");
		    pam_OPER_STATE.remove("05");
			$("#pam_OPER_STATE").val("08"); //默认选为变更
 	  }
 	  
 	  //变更时不能改服务代码
 	  $('#pam_SVR_CODE_END').attr('disabled',true);
 	  $('#pam_BIZ_CODE').attr('disabled',true)
}
}

//点确定按钮返回数据到父页面隐藏字段
function setData(obj)
{

	if ($('#pam_RSRV_STR4').val()=="")
	{
		MessageBox.alert("",'企业代码不能为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
		isSetData = 2;
		return false;		
	}

    var ecBaseInCode=$("#pam_EC_BASE_IN_CODE").val();
    if(ecBaseInCode==''|| ecBaseInCode == '[]')
    {
    	MessageBox.alert("",'该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
    	isSetData = 2;
   	    return ;
    }
    
    //拼一下服务代码
    var codeHead = $("#pam_SVR_CODE_HEAD").val();
    var codeSecend = $("#SVR_CODE_END_SECEND").val();
    $("#pam_BIZ_IN_CODE").val(codeHead+codeSecend);
    $("#pam_SVR_CODE_END").val(codeSecend);
    
    //调整成和ADC一样,都在最后提交的时候进行校验中英文签名
    var ret = ischinese();//中文签名特殊字符检验，邮箱域名
	if (ret == '1')
	{
	   isSetData = 2;
	   return;
	}
    
//	if(!$.validate.verifyAll("platsvcparamtable"))
//	{
//		disableEle();
//		return;
//	}
    var smsCheck = smsLimitCounCheck();
    if(!smsCheck){
    	isSetData = 2;
  	  return;
    }
	//不允许下发时间字间段的校验
	var forbidflag =checkforbidtime();
	if(!forbidflag)
	{
		isSetData = 2;
	  return;
	}
	//中文签名长度校验
	var ecgnzhflag = checkecgnzh();
	if(!ecgnzhflag)
	{
		isSetData = 2;
	  return;
	}
	
	var ckBizCode = checkBizCode(); //校验业务代码
	if(!ckBizCode)
	{
		isSetData = 2;
		return;
	}
	
	var dealSCFlag = checkBizInCode(); //校验服务代码
	if(!dealSCFlag)
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
    
	var adminFlag = checkAdminExist();/*校验管理员手机号*/
    if(!adminFlag)
	{
    	isSetData = 2;
		return;
	}
    
    //获取提交数据
	//commSubmit();
}



/*
   1－永久白名单
   2－黑名单
   3－限制次数白名单
   4－点播业务
  如果业务类型是3或是4时，需要填写pam_BIZ_ATTR。
*/
//function chargeBizAttr(){
//   setDeliverNumDis();//根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
//   setPreDay();
//}

/**
*服务代码处理
**/
//function dealServCode()
//{
// //today对EC基本接入号，业务接入号（服务代码）进行处理
//  var vHead = $("#pam_SVR_CODE_HEAD").val(); 
//  var vTail = $("#SVR_CODE_END_SECEND").val();
//  var cLen = $("#pam_C_LENGTH").val();
//
//  if(cLen!=""&&vTail.length!=cLen)
//  {
//  	alert("服务代码尾号必须为"+cLen+"位！");
//	return false;
//  }
//
//  	var numberre = /[^0-9]/g;
//  	var numrst = vTail.match(numberre);   // 在字符串 s 中查找匹配。
//	if(numrst!=null)
//	{
//   		alert("服务代码必须为数字!");
//   		return false;
//   	}
//    var userId = "";//暂时写为空，做变更的时候再调整
//	if(userId==undefined)
//	{
//		userId="";
//	}
//    var vServCode = vHead + vTail ;
//
//  //业务接入号被赋予 wade 控件，以便传到后台
//  $('#pam_BIZ_IN_CODE').val(vServCode);
//
//  $("#pam_SVR_CODE_END").val(vTail);
//  
//  //业务接入号填回到EC基本接入号
//  $('#pam_EC_BASE_IN_CODE').val(vServCode);
//
//  /**
//  var oldBizInCode=$.getSrcWindow().$('#OLD_BIZ_IN_CODE').val();
//  if(oldBizInCode!=""){
//				if(vTail!=oldBizInCode){
//				alert('同一集团产品下的服务代码要一致!');
//				return;
//			}
//
//  }
//  **/
//	var groupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
//	var bizFlag = true;
//
//	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "getDumpIdByajax",'&GROUP_ID='+groupId+'&BIZ_IN_CODE='+vServCode, function(data){
//		$.endPageLoading();
//		bizFlag = checkBizInCodeByajax(data);//生成的服务代码不能使用false
//		},    
//		function(error_code,error_info,derror){
//		$.endPageLoading();
//		showDetailErrorInfo(error_code,error_info,derror);
//    });
//	
//	return bizFlag;
//}
function chgBizCode()
{
	var bizCode=$('#pam_BIZ_CODE').val();
	var servType=$('#pam_SERVICE_TYPE').val();
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "createNewBizCode",'&BIZ_CODE='+bizCode+'&SERV_TYPE='+servType, function(data){
		$.endPageLoading();
		$('#pam_BIZ_CODE').val(data.get(0,"BIZ_CODE"));
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
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
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "getDumpSnByajax",'&SERIAL_NUMBER='+adminSerNum, function(data){
		$.endPageLoading();
		adminFlag = afterCheckAdminExist(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
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
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "grpUserOweFeeByajax",'&CUST_ID='+custId+'&PRODUCT_ID='+productId, function(data){
			$.endPageLoading();
			canFlag =  aftercanresume(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
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

//
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
function chargeTypeChanged(){
	   var billType = $("#pam_BILLING_TYPE").val();
	   var bizattr=$('#pam_BIZ_ATTR').val();
	   
		if(bizattr=="1"||bizattr=="2")//黑白名单时,计费类型必须免费 单价必须为0
		{
			if(billType!="00")
			{
				MessageBox.alert("","业务为 黑白名单属性时, 计费类型必须为 免费!");
				$("#pam_BILLING_TYPE").val("00");
			}
		}
	   
	   var price = $("#pam_PRICE").val();
	   billType = $("#pam_BILLING_TYPE").val();  //前面可能改变了，重新取一次
	   if (billType== "02" || billType== "01"){ 
	       if(price < 1) {MessageBox.alert("","单价请输入大于0的值");}
	       $("#pam_PRICE").val("");
	   }else{
	     $("#pam_PRICE").val("0");
	   }
	}

function chargePrice(){
	   var billType = $("#pam_BILLING_TYPE").val();
	   var price = $("#pam_PRICE").val();
	   if ( billType == "00" ){
	       $("#pam_PRICE").val("0");
	   }else{
	       if(price <1){ 
		       MessageBox.alert("","请输入大于0的值");
		       $("#pam_PRICE").val("");
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


 function ischinese()  
 {  
	    var zh = $("#pam_TEXT_ECGN_ZH").val();
		var en = $("#pam_TEXT_ECGN_EN").val();

		var re = /[&\*\+\.\[\]%\$\^\?\{}\|\\#@!~]/g;
		var rn = /[^A-Za-z0-9_\s]/g;
		//var rm = /\w+\.\w+$/g;

		var r = zh.match(re);
		var e = en.match(rn);
		//var m = em.match(rm);
		if(r!=null)
		{
			MessageBox.alert("","中文签名,不允许包含除中文 字母 数字外的特殊字符,请检查输入!");
			return '1';
		}
		if(e!=null)
		{
			MessageBox.alert("","英文签名,不允许包含除字母 数字外的特殊字符,请检查输入!");
			return '1';
		}

 	var s = $("#pam_TEXT_ECGN_ZH").val();
 	var l = s.length;

	 if(l>18||l<2)
 	{
 		MessageBox.alert("","中文签名，最多必须输入2-18个汉字之间!\n");
 	    disableEle();
 	    $("#pam_TEXT_ECGN_ZH").select();
 	   	return '1'; 
 	}

	var len = en.length;
	 if(len>36||len<4) {
		 MessageBox.alert("","英文签名，最多必须输入4-36个字符之间!\n");
		 disableEle();
		 $("#pam_TEXT_ECGN_EN").select();
		 return '1';
	 }
     var re = /[&\*\+\.\[\]%\$\^\?\{}\|\\#@!~]/g;
 	var textecgnzh = $("#pam_TEXT_ECGN_ZH").val();
 	var r = textecgnzh.match(re);
 	if(r!=null)
 	{
 		MessageBox.alert("","中文签名,不允许包含除中文 字母 数字外的特殊资费,请检查输入!");
 		return '1';
 	}

 }


function disableEle()
{
	$("#pam_PLAT_SYNC_STATE").attr('display',true);
	if ($("#pam_BIZ_CODE").val()==''){
		$("#pam_BIZ_CODE").attr('display',false);
 	}else{
 	      $("#pam_BIZ_CODE").attr('display',true);
 	}
 	$("#pam_BIZ_NAME").attr('display',true);
 	$("#pam_BIZ_STATUS").attr('display',true);
 	$("#pam_PRE_CHARGE").attr('display',true);
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist(){
    var signFlag = true;
    var productid=$("#cond_OFFER_CODE").val();
	var param ='&TEXT_ECGN_ZH='+$('#pam_TEXT_ECGN_ZH').val()+'&TEXT_ECGN_EN='+$('#pam_TEXT_ECGN_EN').val()+'&PRODUCT_ID='+productid;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "getSensitiveTextByajax",param, function(data){
		$.endPageLoading();
		signFlag = afterCheckSignExist(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
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

//
function smsLimitCounCheck()
{
	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue))
	{
	  MessageBox.alert("","您输入的日下发流量大于月下发流量，请重新输入！");
	  $("#pam_MAX_ITEM_PRE_DAY").val("1")
	  return false;
	}

	var max="100000";  
	var rsrvTag2=$('#pam_RSRV_TAG2').val();
	var errinfo="";
   
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
	if (rsrvTag2=="4") //VIP级：不能超过1000万改成不能超过10亿
	{
		 max="1000000000"; 
		 errinfo="VIP级：最大短信数不能超过10亿!";
	}
   
	if (parseInt(monValue)>parseInt(max))
	{
		MessageBox.alert("",errinfo);
		element.focus;
		return false;
	}
	
	return true;

}


function flipDisabled(){
	  
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
		
		$("#pam_BILLING_TYPE").attr('disabled',true);
		$("#pam_PRICE").attr('disabled',true);
		$("#pam_BIZ_STATUS").attr('disabled',true);
		$("#pam_IS_MAS_SERV").attr('disabled',true);
}

function flipEnabled(){
	$("#pam_DELIVER_NUM").attr('disabled',false);
	$("#pam_MAX_ITEM_PRE_DAY").attr('disabled',false);
	$("#pam_MAX_ITEM_PRE_MON").attr('disabled',false);
	
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
	
	$("#pam_IS_MAS_SERV").attr('disabled',false);
}




























//
function dealSpid_Adminnum()
{	
	var bizTypeCode = $("#pam_BIZ_TYPE_CODE").val();
	
	var spid;
	if(bizTypeCode == '002'){
		spid = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("RSRV_STR8");
	}
	else{
		spid = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("RSRV_STR3");
	}
	
	$('#pam_ADMIN_NUM').val($("#GROUP_MGR_SN").val());//管理员手机号
	$('#pam_RSRV_STR4').val(spid);  //写到GrpPlatsvc表的RSRV_STR4字段，发指令的 EC客户编码
}

function dealClassId()
{
 	  //新增时,客户等级默认取集团的信息
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

function putBizCodeForCrt() 
{
	$("#pam_EC_BASE_IN_CODE_A").val("01");

	if ($("#pam_BIZ_TYPE_CODE").val() == "002") {
		$("#pam_EC_BASE_IN_CODE_A").val("02");
	}else if ($("#pam_BIZ_TYPE_CODE").val() == "003") {
		$("#pam_EC_BASE_IN_CODE_A").val("03");
	}
  	var custid=$.enterpriseLogin.getInfo().get("GROUP_INFO").get("CUST_ID");   //从父页面获取CUST_ID
	var incodea=$('#pam_EC_BASE_IN_CODE_A').val();	
	var bizTypeCode = $('#pam_BIZ_TYPE_CODE').val();								
  
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "getMasEcCodeListByA",'&EC_BASE_IN_CODE_A='+incodea+'&CUST_ID='+custid+'&BIZ_TYPE_CODE='+bizTypeCode , function(data){
		$.endPageLoading();
		dealbaseinCodeputAfter(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function dealbaseinCodeputAfter(data)
{
	for(var i=0;i<data.length;i++)
	{
         var ecbaseincode=data.get(i);
         var baseIncodevalue=ecbaseincode.get("RSRV_STR1","");
         
         //jsAddItemToSelect($('#pam_EC_BASE_IN_CODE'), baseIncodevalue, baseIncodevalue);
         $("#pam_EC_BASE_IN_CODE").val(baseIncodevalue);
	}

    var ecBaseInCode=$("#pam_EC_BASE_IN_CODE").val();
    if(ecBaseInCode==''|| ecBaseInCode == '[]')
    {
    	MessageBox.alert("",'该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
   	    return;
    }
  
    ecBaseInCodeChange();
}

function ecBaseInCodeChange()
{
	$('#pam_SVR_CODE_HEAD').val($('#pam_EC_BASE_IN_CODE').val());
	$('#SVR_CODE_END_SECEND').attr("nullable","yes");
	$("#SVR_CODE_END_SECEND").attr("disabled",false);

    //begin: add by lijie9 for esop,2011-07-28, 如果已经有参照的服务代码，则取其尾
    if ($("#pam_SERVICE_CODE").val() && $("#pam_SVR_CODE_HEAD").val())
    {
    	var vSrvCode = $("#pam_SERVICE_CODE").val();
    	var vpart=$("#pam_SVR_CODE_HEAD").val();
    	var lall=vSrvCode.length;
		var lpart=vpart.length;
		
    	if (vSrvCode.substring(0,lpart) == vpart)
    	{
    		$("#SVR_CODE_END_SECEND").val(vSrvCode.substring(lpart,lall)) ;
    		$("#pam_SVR_CODE_END").val(vSrvCode.substring(lpart,lall)) ;
    	}
    }
}

function chargeBizAttr()//业务属性变更时的校验
{
	var bizattr=$("#pam_BIZ_ATTR").val();
	if(bizattr=="1"||bizattr=="2")//黑白名单时,计费类型必须免费 单价必须为0
	{
		$("#pam_BILLING_TYPE").val('00');   
		$("#pam_PRICE").val('0');
		$("#pam_BILLING_TYPE").attr('disabled',true);
		$("#pam_PRICE").attr('disabled',true);
	}
	else
	{
		$("#pam_BILLING_TYPE").attr('disabled',false);
	    $("#pam_PRICE").attr('disabled',false);
	    $("#pam_BILLING_TYPE").val('');
	    $("#pam_PRICE").val('');	
	}
	
}

/**
*作用：中文签名长度校验
*/
function checkecgnzh(){
     ecgnzh = $("#pam_TEXT_ECGN_ZH").val();
     var lenReg =  ecgnzh.replace(/[^\x00-\xFF]/g,'**').length;
     var textEcgnClass = $("#pam_TEXT_ECGN_ZH").attr("textEcgnClass");
	 if (textEcgnClass!=5){
	     if(lenReg<4 || lenReg>36)
	     {
	     	MessageBox.alert("","中文签名长度只能为4～36个字符!");
	     	return false;
	     }
     }
	return true;
}

//校验业务代码
function checkBizCode(){
	
	//校验业务代码长度必须为10位
	var bizcode=$('#pam_BIZ_CODE').val();
	
	//校验业务代码 必须为字母数字的组合
	var masreg = /[\W]/g;
 	var masrst = bizcode.match(masreg);   // 在字符串 s 中查找匹配。
	if(masrst!=null)
	{
		MessageBox.alert("","业务代码只能为字母或者数字,或者字母和数字的组合!");
 		$('#pam_BIZ_CODE').select();
 		return false;
  	}
	
	if(bizcode.length!=10)
	{
		MessageBox.alert("",'业务代码长度必须为10位!');
		$('#pam_BIZ_CODE').select();
		return false;
	}
	
  	var bizTypeCode = $("#pam_BIZ_TYPE_CODE").val();
	if(bizTypeCode=="002")  //MMS业务的业务代码为全数字
	{
		var numberre = /[^0-9]/g;
		var bizcodecheck = bizcode.match(numberre);   // 在字符串 s 中查找匹配。
		if(bizcodecheck!=null)
		{
			MessageBox.alert("","彩信业务的业务代码必须为全数字!");
 			return false;
  		}
	}
	
	return true;
}

function checkBizInCode(){
	   var vHead = $("#pam_SVR_CODE_HEAD").val();  
	   var vTail = $("#SVR_CODE_END_SECEND").val();
	   //var cLen = $("#pam_C_LENGTH").val();
	   var vServCode = vHead + vTail ;
	   
	   shortnum=vTail;
//	   if(cLen!=""&&vTail.length!=cLen)
//	   {
//	  		MessageBox.alert("","服务代码尾号必须为"+cLen+"位！");
//	  		return false;
//	    }
	   
		 //校验服务代码必须总长不超过20位
		if(vServCode.length>20)
		{
			MessageBox.alert("","服务代码 长度不能超过20位!");
			return false;
	 	}
	   
	    var numberre = /[^0-9]/g;
	  	var numrst = vTail.match(numberre);   // 在字符串 s 中查找匹配。
	  	if(numrst!=null)
		{
	   		MessageBox.alert("","服务代码必须为数字!");
	   		return false;
	   	}
	  	
	   //服务代码回填pam_BIZ_IN_CODE
	   $('#pam_BIZ_IN_CODE').val(vServCode); 
		
	   var groupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
		var bizFlag = true;

		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.mas.MasParamHandler", "getDumpIdByajax",'&GROUP_ID='+groupId+'&BIZ_IN_CODE='+vServCode, function(data){
			$.endPageLoading();
			bizFlag = checkBizInCodeByajax(data);//生成的服务代码不能使用false
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
		return bizFlag;
	}

//先放著，還需要修改具體的值 具体值是14
function lengthLimit(obj)
{
	var str=obj.value;
	var strlength=str.length;
   
	if(strlength>14)
	{
	   obj.value=str.substr(0,len);
	}
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