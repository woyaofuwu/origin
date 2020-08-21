/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
 */
 
function loadUserParamInfo(){

	var serviceId="";	
	var prodcutId ="";
	var packageId ="";
	var param = "";
	var svcparamvalue ="";
	var userId ="";
	var urlParts = document.URL.split("?"); 
	var parameterParts = urlParts[1].split("&"); 
	var grpUserEparchyCode = $("#GRP_USER_EPARCHYCODE").val();
	
	try
	  {
	  	  userId=$.getSrcWindow().$("#USER_ID").val();//从父页面获取userId
	  	  if(userId==undefined)
	  	  {
	  	  	userId="";
	  	  }
	  }
	  catch(e)
	  {
		  userId="";
	  }
	  
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
	
		if(pairName=="PRODUCT_ID")
		{
		      prodcutId=pairValue;
		}
		if(pairName=="PACKAGE_ID")
		{
		      packageId=pairValue;
		      
		}
		if(pairName=="ELEMENT_ID")
		{
		      serviceId=pairValue;
		}

	}
	 //add by lijie9, 2011-5-16 for esop 接口调用参数准备
	var eos = $.DatasetList($.getSrcWindow().$("#EOS").val());//从父页面获取EOS数据
	
	var ibsysid = "";
	var subIbsysid = "";
	var nodeId = "";
	if(eos && eos != "" && eos != "[]")
	{ 
	    var eosData = eos.get(0); 
		ibsysid = eosData.get("IBSYSID");
		subIbsysid = eosData.get("SUB_IBSYSID");
		nodeId = eosData.get("NODE_ID");
		if(nodeId!="bossChange"){
			$("#servpart").css("display","block");
		}	
	}
   //esop end
   
	if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
	{
	     //ESOP接入
	   	if(eos && eos != "" && eos != "[]")
	   	{ 
            param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
			$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    },{async:false});
	    }
	   //非ESOP接入
	   else{
	   	   if(userId != undefined && userId != "")//userId不为空 集团产品变更
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode;
			}
			else//userId为空 集团产品受理
			{
				param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
			}
			$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    },{async:false});
		}
	   
	}
	
	else //找到已存在的参数值
	{
		var datasetsize=svcparamvalue.getCount();
		if(datasetsize<=1||(eos!="" && eos != "[]")) //表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验,第二条记录才是具体的参数信息)
		{
		     //ESOP接入
		   	if(eos && eos != "" && eos != "[]")
			{
		    	if(userId != undefined && userId != "")
				{
					param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
				}
				else
				{
					param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode+'&IBSYSID='+ibsysid+'&SUB_IBSYSID='+subIbsysid+'&NODE_ID='+nodeId;
				}
				
				$.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){	putpagedata(data,serviceId);$.endPageLoading();	},
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    },{async:false});
			}
			   //非ESOP接入
		   else{
				if(userId != undefined && userId != "")
				{
					param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+"&USER_ID="+userId+'&EPARCHY_CODE='+grpUserEparchyCode;
				}
				else
				{
					param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
				}
				$.ajax.submit('this', 'getServiceParamsByajax', param, null, function(data){
					putpagedata(data,serviceId);
					$.endPageLoading();
				},
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    },{async:false});
			}
		}
		else
		{
			putpagedata(svcparamvalue,serviceId);
		}
	}

	//do9627(serviceId);    //没有9627服务了
	
	setStatetype();	//根据不同的操作类型，页面输入框的可见性.
	checkmodifypriv();
};


/**
 * 作用：从url取关键值,并根据关键值从父页面取值   (如果URL中没有带参数，AJAX刷新后的处理)
 */
function putpagedata(dataset,serviceId)
{

	  var paramVerifySucc=dataset.get(0); //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
	  $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面
	  var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
	  var platsvcdata=paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
	  platsvcdata.eachKey(                    //将platsvcdata参数 填充到adcServicParamsForm
	 
	 	 function(key)
	 	 { 
	     	try
	     	{
	     		var tempElement = $("#"+key);
	     		if(tempElement != "undefined" && tempElement != ""){
	     			tempElement.val(platsvcdata.get(key,""));
	     		}
	     	}
	     	catch(e)
	     	{
	     	}
	     }
	    );

	  var molistdataset=paramMap.get('MOLIST');//得到业务上行指令数据 IDataset结构
	  if(molistdataset)
	  {
	  	var molistcount=molistdataset.getCount();
	  	for(j=0;j<molistcount;j++)
	 	 { 
	  		 var modata=molistdataset.get(j);
	   		 modata.eachKey(
	 			 function(mokey)
	 	 		 {
	     			try
	     			{

	     				$("#"+mokey).val(modata.get(mokey,""));
	     			}
	     			catch(ee)
	     			{
	     			}
	     		}
	    	);
	   	    if(modata.get("tag","")==""|| modata.get("tag","")=="2")//直接从数据库查得的记录
	   	 	{
	  			createMoinfo();
	  			replaceTablecol(modata.get("MO_CODE"),modata.get("DEST_SERV_CODE"),'2');
	  	 	}
	  	 	else if( modata.get("tag","")=="0")//新增的记录
	  	 	{
	  	 		createMoinfo();//往表新增这条记录
	  	 	}
	  	 	else if( modata.get("tag","")=="1")//删除的记录
	  		{
	  	 		createMoinfo();//往表新增这条记录
	  			replaceTablecol(modata.get("MO_CODE"),modata.get("DEST_SERV_CODE"),'1'); //修改这条记录的操作标识为删除,同时隐藏这条记录
	  	 	} 
	  	}
	  }	

	dealOperStateoptions(); //设置操作状态
	
	//只有新增时，才从库取配置
	var modifytag=$("#pam_MODIFY_TAG").val();

 	if("0"==modifytag)//新增时
 	{
	    dealSpid_Adminnum();//设置集团管理员号码 和企业代码(取的集团客户信息里面维护的值)
		dealClassId();      //客户等级
	}
	
	if (modifytag == '0'&&$("#PARAM_VERIFY_SUCC").val()=="false")
	{
	   	putBizCodeForCrt();
	    //begin: add by lijie9 for esop,2011-07-08, 如果已经有参照的服务代码，则取其尾
	    if ($("pam_SERVICE_CODE").val() && $("#svrCodeHead").val())
	    {
	    	var vSrvCode = $("#pam_SERVICE_CODE").val();
	    	var vpart=$("#svrCodeHead").val();
	    	var lall=vSrvCode.length;
			var lpart=vpart.length;
			
	    	if (vSrvCode.substring(0,lpart) == vpart)
	    	{
	    		$("#svrCodeTail").val(vSrvCode.substring(lpart,lall)) ;
	    	}
	    }
	    //end: add by lijie9 for esop,2011-07-08, 如果已经有参照的服务代码，则取其尾
	}		
	else
	{
		var vall=$("#pam_BIZ_IN_CODE").val();
		var vpart=$("#pam_EC_BASE_IN_CODE").val();
		var lall=vall.length;
		var lpart=vpart.length;
		var vtail=vall.substring(lpart,lall);
		$("#svrCodeTail").val(vtail);
		$("#svrCodeHead").val(vpart);
		
		//setMoAccessNum();
	}
	
	//设置短信上行访问码样式,彩信　ｗａｐ时显示为必填
	//setMoAccessNumClass();	
	
	bizattrChanged();
}


function putBizCodeForCrt() 
{
	$("#pam_EC_BASE_IN_CODE_A").val("01");
	
	if ($("#pam_BIZ_TYPE_CODE").val() == "002") {
		$("#pam_EC_BASE_IN_CODE_A").val("02");
	}else if ($("#pam_BIZ_TYPE_CODE").val() == "003") {
		$("#pam_EC_BASE_IN_CODE_A").val("03");
	}
  	
  	var custid=$.getSrcWindow().$("#CUST_ID").val();    //从父页面获取CUST_ID 
	var incodea=$('#pam_EC_BASE_IN_CODE_A').val();	
	var bizTypeCode = $('#pam_BIZ_TYPE_CODE').val();								
	$.ajax.submit(this,'getMasEcCodeListByA','&EC_BASE_IN_CODE_A='+incodea+'&CUST_ID='+custid+'&BIZ_TYPE_CODE='+bizTypeCode ,null,function(data){dealbaseinCodeputAfter(data)});
  
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
   	    alert('该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
   	    return;
    }
  
    ecBaseInCodeChange();
}

/**
 * 设置操作状态
 */
function dealOperStateoptions()
{
 	var modifytag=$('#pam_MODIFY_TAG').val();
 	if("0"==modifytag)//新增时
 	{
	  $('#pam_OPER_STATE').html("");
 	  $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项
 	  jsAddItemToSelect($("#pam_OPER_STATE"), '新增', '01');
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
 	   	$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	  	jsAddItemToSelect($("#pam_OPER_STATE"), '恢复', '05');
 	  }else if(platsyncState=="1")//正常在用
 	  {
 	   	$('#pam_OPER_STATE').html("");
 	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	    jsAddItemToSelect($('#pam_OPER_STATE'), '暂停', '04');
 	    jsAddItemToSelect($('#pam_OPER_STATE'), '变更', '08');
 	  }
 	  
 	  //变更时不能改服务代码
 	  $('#svrCodeTail').attr('disabled',true);
 	  $('#pam_BIZ_CODE').attr('disabled',true);
 	}
}

//---------------------------------上面是初始化页面时做的操作，下面是页面中做的一些JS-----------------------------------

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

//确定按钮
function setData(obj){
	
	if ($('#pam_RSRV_STR4').val()=="")
	{
		alert('企业代码不能为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
		return false;		
	}

    var ecBaseInCode=$("#pam_EC_BASE_IN_CODE").val();
    if(ecBaseInCode==''|| ecBaseInCode == '[]')
    {
   	    alert('该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!');
   	    return ;
    }

	if(!$.validate.verifyAll("platsvcparamtable"))
	{
		disableEle();
		return;
	}
	smsLimitCounCheck();
	//不允许下发时间字间段的校验
	var forbidflag =checkforbidtime();
	if(!forbidflag)
	{
	  return;
	}
	//中文签名长度校验
	var ecgnzhflag = checkecgnzh();
	if(!ecgnzhflag)
	{
	  return;
	}
	
	var ckBizCode = checkBizCode(); //校验业务代码
	if(!ckBizCode)
	{
		return;
	}
	
	var dealSCFlag = checkBizInCode(); //校验服务代码
	if(!dealSCFlag)
	{
		return;
	}
	
    var canFlag = canresume();//判断用户欠费状态下,不能做恢复操作
   	if(!canFlag)
	{
		return;
	}
    
    var sigFlag = checkSignExist();//判断中英文签名是否存在敏感资费
    if(!sigFlag)
	{
		return;
	}
    
	var adminFlag = checkAdminExist();/*校验管理员手机号*/
    if(!adminFlag)
	{
		return;
	}
    
    //获取提交数据
	commSubmit();  
}
function chgBizCode()
{
	var bizCode=$('#pam_BIZ_CODE').val();
	var servType=$('#pam_SERVICE_TYPE').val();
	$.ajax.submit(this, 'createNewBizCode', '&BIZ_CODE='+bizCode+'&SERV_TYPE='+servType,null, 
			function(data){
				$('#pam_BIZ_CODE').val(data.get(0,"BIZ_CODE"));
			},
			function(error_code,error_info,derror){
				showDetailErrorInfo(error_code,error_info,derror);
			},
			{async:false});
}
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
	$.setReturnValue();
	setStatetype();
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


/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	08-变更 04- 暂停 05-恢复
 */
function setStatetype(){
	
  var operState=$("#pam_OPER_STATE").val();
  if (operState == "08"){
	  tableDisabled("platsvcparamtable",false);
	  tableDisabled("MoListDetail",false);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_BIZ_CODE").attr('disabled',true);
	  $("#pam_BIZ_NAME").attr('disabled',true);
	  $("#pam_BIZ_IN_CODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR").attr('disabled',true);
	  //$("#pam_BILLING_TYPE").attr('disabled',true);
	  //$("#pam_PRICE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  //$("#pam_BIZ_STATUS").attr('disabled',true);
	  $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
	  $("#svrCodeTail").attr('disabled',true);
	  
	   $("#pam_MO_ACCESS_NUM").attr('disabled',true);  //短信上行访问码，好像没用了。暂时不删除,设置为不能编辑
	  
	  flipEnabled();
  }
  if (operState == "04"){
	  tableDisabled("adcServicParamsForm_pam",true);
	  tableDisabled("MoListDetail",true);
	  
	  flipDisabled();  
  } 
  if (operState == "05"){
	  tableDisabled("adcServicParamsForm_pam",true);
	  tableDisabled("MoListDetail",true);
	  
	  flipDisabled(); 
  }
  if (operState == "01"){  
      //新增时，保持HTML初始时的样式
  	  if(!$("#pam_BILLING_TYPE").val())
	  {
	  	$("#pam_BILLING_TYPE").val("00");//没值的情况默认免费
	  }
  } 
  
   //任何操作类型都不能变更的
   $("#pam_EC_BASE_IN_CODE").attr('disabled',true);
   $("#pam_EC_BASE_IN_CODE_A").attr('disabled',true);
   $("#svrCodeHead").attr('disabled',true);
   $("#pam_RSRV_TAG2").attr('disabled',true);
}

function flipDisabled(){
	  $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_DAY").setDisabled(true);
	  $.Flip.get("pam_MAX_ITEM_PRE_MON").setDisabled(true);
}

function flipEnabled(){
	  $.Flip.get("pam_DELIVER_NUM").setDisabled(false);
	  $.Flip.get("pam_MAX_ITEM_PRE_DAY").setDisabled(false);
	  $.Flip.get("pam_MAX_ITEM_PRE_MON").setDisabled(false);
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

/**liaolc
*作用：调资源接口生成服务代码
function getResBizInCode(){
      var siBaseInCode = $("#pam_SI_BASE_IN_CODE").val();//基本接入
	  var productId = $.getSrcWindow().$("#PRODUCT_ID").val();//从父页面获取productId
  	  if(productId==undefined)
  	  {
  	  	productId = "";
  	  }
  	  var params = "&PRODUCT_ID="+productId+"&X_RES_NO="+siBaseInCode;
  	  //要求基本接入号 和 产品ID 到资源页面选择 业务接入号  //subsys_cfg.resserv
  	  $.popupPage('popup.PortOccupySelect','queryPortOccupySel',params,'调资源选号页面','750','650',null,null,subsys_cfg.resserv,false);
}
*/

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

/**校验服务代码*/
function checkBizInCode(){
   var vHead = $("#svrCodeHead").val();  
   var vTail = $("#svrCodeTail").val();
   var cLen = $("#pam_C_LENGTH").val();
   var vServCode = vHead + vTail ;
   if(cLen!=""&&vTail.length!=cLen)
   {
  		MessageBox.alert("","服务代码尾号必须为"+cLen+"位！");
  		return false;
    }
   
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
	
	//add by lijie9, 2011-5-16 验证端到端传过来的服务代码和填写的是否一致
	var serviceCode = $("#pam_SERVICE_CODE").val();
	var modiState = $("#pam_MODIFY_TAG").val();
	var eos = $.DatasetList($.getSrcWindow().$("#EOS").val());//从父页面获取EOS数据
	                   //服务变更（注意：非产品变更）的时候不做校验，因为服务代码不能变更
	if(serviceCode!=vServCode && (eos && eos != "" && eos != "[]") && modiState=="0") 
	{
		alert("填写的服务代码与已审核的服务代码不一致，请核对!");
		return false;
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
	  
	var bizFlag = true;  
	$.ajax.submit('this', 'getDumpIdByajax', '&GROUP_ID='+groupId+'&BIZ_IN_CODE='+vServCode,null, 
			function(data) { bizFlag = checkBizInCodeByajax(data); },
	        function(error_code,error_info,derror) { showDetailErrorInfo(error_code,error_info,derror); },
	        {async:false}
			);
	
	return bizFlag;
}

/**校验服务代码AJAX刷新后的处理*/
function checkBizInCodeByajax(data){
	
	if ($("#pam_MODIFY_TAG").val() == '0')
	{
		var flag = data.get(0,"ISCHECKAACCESSNUMBER");
		if(flag == "false")
		{
			MessageBox.alert("","服务代码["+$('#pam_BIZ_IN_CODE').val()+"]与已有的重复，请修正！");
			$("#svrCodeTail").focus();
			return false;
		}
	}
	return true;
}

function canresume()
{
	var canFlag = true;
	var userId=$.getSrcWindow().$("#USER_ID").val();
	var operstate=$('#pam_OPER_STATE').val();
	if(operstate=="05")
	{
		$.ajax.submit('this', 'grpUserOweFeeByajax', '&USER_ID='+userId,null, 
		function(data){
			canFlag =  aftercanresume(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});
		
	}
	return canFlag;
}

function aftercanresume(owefeeset)
{
	var owefee=parseInt(owefeeset.get(0,"OWE_FEE"));//实时欠费信息
	var createvalue=parseInt(owefeeset.get(0,"CREDIT_VALUE"));//用户信用度

	if( owefee +createvalue*100<0)
	{
		alert('集团产品已经欠费,不能执行[恢复]操作！请先缴清费用后再执行[恢复]操作!');	
		return false;
	}
	return true;
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist(){
    var signFlag = true;
    var productid=$.getSrcWindow().$("#PRODUCT_ID").val();
	var param ='&TEXT_ECGN_ZH='+$('#pam_TEXT_ECGN_ZH').val()+'&TEXT_ECGN_EN='+$('#pam_TEXT_ECGN_EN').val()+'&PRODUCT_ID='+productid;
	$.ajax.submit('this', 'getSensitiveTextByajax', param,null, 
	function(data){
		 signFlag = afterCheckSignExist(data);
	},
	function(error_code,error_info,derror){
		showDetailErrorInfo(error_code,error_info,derror);
	},
	{async:false});
	
	return signFlag;
}

function afterCheckSignExist(data){
	var inProduct=data.get(0,'IN_PRODUCT');
	if(inProduct=="true"){
		var hasZh = data.get(0,"HAS_ZH");
		if(hasZh=="true"){
			alert("对不起，中文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_ZH").select();
			return false;
		}
		
		var hasEn = data.get(0,"HAS_EN");
		if(hasEn=="true"){
			alert("对不起，英文签名含有敏感字符["+data.get(0,"PARAM_NAME")+"]，请重新输入");
			$("#pam_TEXT_ECGN_EN").select();
			return false;
		}
	}
	return true;
}

/*校验管理员手机号*/
function checkAdminExist()
{
    
	var adminSerNum = $('#pam_ADMIN_NUM').val();
	if(adminSerNum == null  && adminSerNum == "")
	{
		 alert('管理员号码为空');
		 $("#pam_ADMIN_NUM").select();
		 return false;
	}
	var adminFlag=true;
	var param ='&SERIAL_NUMBER='+adminSerNum;
	$.ajax.submit('this', 'getDumpSnByajax', param,null, 
		function(data){
			adminFlag = afterCheckAdminExist(data);
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
		},
		{async:false});
	
	return adminFlag;
}

function afterCheckAdminExist(data)
{
	var SnFlag=data.get(0,"IS_FLAGSN");
	if(SnFlag=="true")
	{
		MessageBox.alert("","管理员号码不是有效的在网用户，请重新输入！");	
		$("#pam_ADMIN_NUM").select();//focus();
		return false;
	}
	return true;
}

/**
*table动态表
*/

function tableRowClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#MO_CODE").val(rowData.get("MO_CODE"));
	$("#MO_MATH").val(rowData.get("MO_MATH"));
	$("#MO_TYPE").val(rowData.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(rowData.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(rowData.get("DEST_SERV_CODE_MATH"));
}

function tableRowDBClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#MO_CODE").val(rowData.get("MO_CODE"));
	$("#MO_MATH").val(rowData.get("MO_MATH"));
	$("#MO_TYPE").val(rowData.get("MO_TYPE"));
	$("#DEST_SERV_CODE").val(rowData.get("DEST_SERV_CODE"));
	$("#DEST_SERV_CODE_MATH").val(rowData.get("DEST_SERV_CODE_MATH"));
}

function tableColumnClick() {alert(3);}
function tableAddRow(e) {$.table.get("MoListTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("MoListTable").deleteRow();};
function tableCleanRow(e) {$.table.get("MoListTable").cleanRow(e);};
/**
*动态表格新增一条记录
*/
function createMoinfo() 
{

	var editDate = $.ajax.buildJsonData("MoListDetail");
	
  	if(!$.validate.verifyAll('MoListDetail'))return false;
  	if (!$.table.get("MoListTable").isPrimary('MO_CODE,DEST_SERV_CODE', editDate)){
		editDate["MO_MATH_NAME"]=$("#MO_MATH").find("option:selected").text();
		editDate["MO_TYPE_NAME"]=$("#MO_TYPE").find("option:selected").text();
		editDate["DEST_SERV_CODE_MATH_NAME"]=$("#DEST_SERV_CODE_MATH").find("option:selected").text();
		//往表格里添加一行并将编辑区数据绑定上
		$.table.get("MoListTable").addRow(editDate,null,null,true);
		//往表格里添加一行数据后清空编辑框
		resetArea('MoListDetail',true);
	} else {
		MessageBox.alert("","添加失败！“指令内容、目的号码”已经存在相同的值！");
		resetArea('MoListDetail',true);
	}
}
/**
*动态表格更新一条记录
*/
function updateMoinfo() 
{
	var editDate = $.ajax.buildJsonData("MoListDetail");
	if(!$.validate.verifyAll('MoListDetail')) return false; 
	editDate["MO_MATH_NAME"]=$("#MO_MATH").find("option:selected").text();
	editDate["MO_TYPE_NAME"]=$("#MO_TYPE").find("option:selected").text();
	editDate["DEST_SERV_CODE_MATH_NAME"]=$("#DEST_SERV_CODE_MATH").find("option:selected").text();
	
	$.table.get("MoListTable").updateRow(editDate);
	//往表格里添加一行数据后清空编辑框
	resetArea('MoListDetail',true);
}
  
/**
*动态表格删除一条记录
*/
function deleteMoinfo() 
{
	var tab = $.table.get("MoListTable");
	tab.deleteRow(tab.getTable().attr("selected"));
	//往表格里添加一行数据后清空编辑框
	resetArea('MoListDetail',true);
}

function getTableData() {
	//var data = $.table.get("MoListTable").getTableData(null,true);
	//$("#MoListTable tbody tr")[1].setAttribute("status", "2");
		var data1 = $.table.get("tjOtherTable").getTableData(null, true);
	MessageBox.alert("",data1);
	
}
//替换动态表格操作标识字段 并且设置其显示属性
function replaceTablecol(moCode,destServCode,xtag)
{
	var table=$.table.get("MoListTable").getTableData();
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
/**************************************************动态表格结束*********************************************************/

/**************************************************其它方法开始*******************************************************************/
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
       $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
   }else{
      $("#pam_DELIVER_NUM").attr('disabled',false);
      $.Flip.get("pam_DELIVER_NUM").setDisabled(false);
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
	
function checkDeliverNum(){
   var result=false;
   var bizAttrValue=$('#pam_BIZ_ATTR').val();  
   /* 限制次数白名单和点播业务 必须填限制次数 */
   if(bizAttrValue==3||bizAttrValue==4){       
       var deliverNum=$('#pam_DELIVER_NUM').val();
       if($.trim(deliverNum)== ""||deliverNum==null){
          result=true;
       }
   }
   return result;    
}

/**
*作用：判断输入框的值
*/
function checkMessageAmount(obj){
	var rightClass = obj.getAttribute("rightClass");
	//ganquan*******************
	//根据业务属性，为amount选择不同的最大值
	var pam_BIZ_ATTR = $("#pam_BIZ_ATTR").val();
	var amount = "0";
	if(pam_BIZ_ATTR == "1"){
		amount = obj.getAttribute("maxValue");
	}
	else if(pam_BIZ_ATTR == "2"){
		amount = obj.getAttribute("maxValue1");
	}
	//***********************ganquan
	obj.value = obj.value.replace(/[^\d]/g,"");
	var inputValue = Number(obj.value);
	obj.value = Number(inputValue);
	if (rightClass <= 4){
	   if(Number(amount) < Number(inputValue)){
	     MessageBox.alert("","您的权限级别为["+rightClass+"]，设置的最大下发量为["+amount+"]，请别越权！");
         obj.value = amount;
	   }
	   
	   if(Number(inputValue) < 1){
	     MessageBox.alert("","您的权限级别为["+rightClass+"]，设置的参数值不能为0，请别越权！");
	     obj.value = amount;
	   }
	   	
	}
	
	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue)){
		MessageBox.alert("","您输入的日下发流量大于月下发流量，请重新输入！");
		//ganquan:*************************
		if(pam_BIZ_ATTR == "1"){
		
			$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("maxValue"));
			$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("maxValue"));
		}
		else if(pam_BIZ_ATTR == "2"){
			$("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("maxValue1"));
			$("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("maxValue1"));
		}
		//******************************ganquan
	}
	
}

/** */
function chargeTypeChanged(){
   var billType = $("#pam_BILLING_TYPE").val();
   var bizattr=$('#pam_BIZ_ATTR').val();
   
	if(bizattr=="1"||bizattr=="2")//黑白名单时,计费类型必须免费 单价必须为0
	{
		if(billType!="00")
		{
			alert("业务为 黑白名单属性时, 计费类型必须为 免费!");
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

function dealSpid_Adminnum()
{	
	var bizTypeCode = $("#pam_BIZ_TYPE_CODE").val();
	
	var spid;
	if(bizTypeCode == '002'){
		spid = $("#CUST_INFO_RSRV_STR8").val(); 
	}
	else{
		spid = $("#CUST_INFO_RSRV_STR3").val();
	}
	
	$('#pam_ADMIN_NUM').val($("#GROUP_MGR_SN").val());//管理员手机号
	$('#pam_RSRV_STR4').val(spid);  //写到GrpPlatsvc表的RSRV_STR4字段，发指令的 EC客户编码
}

function dealClassId()
{
 	  //新增时,客户等级默认取集团的信息
	  var rsrvTag2="1";
	  var classId = $("#CLASS_ID").val();
	  
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

function smsLimitCounCheck()
{
	var dayValue = $("#pam_MAX_ITEM_PRE_DAY").val();
	var monValue = $("#pam_MAX_ITEM_PRE_MON").val();
	if (Number(dayValue) > Number(monValue))
	{
	  MessageBox.alert("","您输入的日下发流量大于月下发流量，请重新输入！");
	  $("#pam_MAX_ITEM_PRE_DAY").val("1")
	  return;
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
	if (rsrvTag2=="4") //VIP级：不能超过10亿
	{
		 max="1000000000"; 
		 errinfo="VIP级：最大短信数不能超过10亿!";
	}
   
	if (parseInt(monValue)>parseInt(max))
	{
		alert(errinfo);
		element.focus;
		return '1';
	}

}

function ischinese()  
{  

	var s = $("#pam_TEXT_ECGN_ZH").val();
	var l = s.length;
	if(l>18)
	{
	    alert("中文签名，最多必须输入2-18个汉字之间!\n");
	    disableEle();
	    $("#pam_TEXT_ECGN_ZH").select();
	   	return '1'; 
	}
	if(l<2)
	{
	    alert("中文签名，最多必须输入2-18个汉字之间!\n");
	    disableEle();
	    $("#pam_TEXT_ECGN_ZH").select();
	   	return '1'; 
	}

	var en = $("#pam_TEXT_ECGN_EN").val();
	var len = en.length;
	if(len>36||len<4) {
		alert("英文签名，最多必须输入4-36个字符之间!\n");
		disableEle();
		$("#pam_TEXT_ECGN_EN").select();
		return '1';
	}
	/*var errorChar;  
	var badChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789><,<>{}?/+=|\\′\":;~!#$%()`";  
	errorChar = isCharsInBag( s, badChar)  
	if (errorChar != "" )  
	{  
		alert("中文签名，请重新输入中文\n");  
		disableEle();
		$("#pam_TEXT_ECGN_ZH").select();
		return '1';  
	}
	*/
    var re = /[&\*\+\.\[\]%\$\^\?\{}\|\\#@!~]/g;
	var textecgnzh = $("#pam_TEXT_ECGN_ZH").val();
	var r = textecgnzh.match(re);
	if(r!=null)
	{
		alert("中文签名,不允许包含除中文 字母 数字外的特殊资费,请检查输入!");
		return '1';
	}

}

function bizattrChanged()//业务属性变更时的校验
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

function lengthLimit(obj,len)
{
	var str=obj.value;
	var strlength=str.length;
   
	if(strlength>len)
	{
	   obj.value=str.substr(0,len);
	}
}
/*校验是否有权限修改*/
function checkmodifypriv(){
	var hasmodifyprv=$("#HASMODIFYPRV").val();
	if(hasmodifyprv=="true")
		{
		  $('#pam_RSRV_TAG2').attr('disabled',false);//客户等级
		
		}
	return;
}

function ecBaseInCodeChange()
{
	$('#svrCodeHead').val($('#pam_EC_BASE_IN_CODE').val());
	$('#svrCodeTail').val("");

    //begin: add by lijie9 for esop,2011-07-28, 如果已经有参照的服务代码，则取其尾
    if ($("#pam_SERVICE_CODE").val() && $("#svrCodeHead").val())
    {
    	var vSrvCode = $("#pam_SERVICE_CODE").val();
    	var vpart=$("#svrCodeHead").val();
    	var lall=vSrvCode.length;
		var lpart=vpart.length;
		
    	if (vSrvCode.substring(0,lpart) == vpart)
    	{
    		$("#svrCodeTail").val(vSrvCode.substring(lpart,lall)) ;
    	}
    }
}

