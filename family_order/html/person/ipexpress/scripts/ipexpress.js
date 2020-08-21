function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString(),
	'IPExpressEdit,hiddenPart,IPExpressInfos', function(){
		$("#col_cond_CAUSE").attr("disabled",false);
		$("#col_SERIAL_NUMBER_G").attr("disabled",false);
		$("#col_BRAND_CODE").attr("disabled",false);
		$("#col_PRODUCT_ID").attr("disabled",false);
		var isPwdOpen=$("#ISDEFALTIPPASSWD").val();
		if(isPwdOpen == "true")
		{
			$("#col_CHANGE_PASSWD").css("display","none");
		}
	},
	function(error_code,error_info, detail){
		//alert(error_info);
		MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, error_info, detail);
    });
}

$(document).ready(function(){
	var isPwdOpen=$("#ISDEFALTIPPASSWD").val();
	if(isPwdOpen == "true")
	{
		$("#col_CHANGE_PASSWD").css("display","none");
	}
	});


/** 
 * 点击动态表格触发行事件，设置隐藏域值
 */
function tableRowClick(e){
	
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	
	$('#col_cond_CAUSE').val(0);
	onCauseChange($('#col_cond_CAUSE'));
    $('#col_PRODUCT_ID_HIDDEN').val(rowData.get("col_PRODUCT_ID"));
    $('#col_PRODUCT_ID').val(rowData.get("col_PRODUCT_ID"));
    $('#col_OLD_PWD').val(rowData.get("col_OLD_PWD"));
    
    var serialNumber2 = rowData.get("col_SERIAL_NUMBER_G");	
    $('#col_SERIAL_NUMBER_F').val(serialNumber2.substring(0,4));
	$('#col_SERIAL_NUMBER_G').val(serialNumber2.substring(4));
	$('#col_TEMP_PWD').val(rowData.get("col_TEMP_PWD"));
	$('#col_USER_ID_B').val(rowData.get("col_USER_ID_B"));
	$('#col_PACKAGESVC').val(rowData.get("col_PACKAGESVC"));
	$('#col_BRAND_CODE').val(rowData.get("col_BRAND_CODE"));
	$('#col_OPEN_DATE').val(rowData.get("col_OPEN_DATE"));
	$('#col_IPServiceText').val(rowData.get("col_IPServiceText"));
	
}


/**
 * 当IP电话号码类型改变时，设置固定号码的地州号预显示
 */
function onCauseChange()
{
	var iserialHidden = $('#SERIAL_NUMBER_HIDDEN').val();
	$("#col_SERIAL_NUMBER_F").text(iserialHidden);//用来显示
	$("#col_SERIAL_NUMBER_F").val(iserialHidden);
	$("#col_SERIAL_NUMBER_G").val("");
}

/** 
 * 固定电话号码初步校验
 */	
function checkPhoneNumberM()
{      
	
        var phoneType = $("#PHONETYPE").val();             
        var phoneLenth = $("#PHONELENTH").val();  
        var serialNumberF = $("#col_SERIAL_NUMBER_F").val();
        var serialNumberG = $("#col_SERIAL_NUMBER_G").val();      
        var serialNumber = serialNumberF+serialNumberG;
        var patrn = /^([+-]?)(\d+)$/;   
        var isNumber =  patrn.test(serialNumber);      
		if(isNumber == false){
		  alert("电话号码规则验证未通过！");
          return false; 
		}
	    if(isNumber == false)
	     {
	      alert("输入号码必须全为数字！");
	      var serialNumberG3 = $("#col_SERIAL_NUMBER_G").val("");
	      return false;
	     }
        if(phoneLenth != serialNumber.length)
        {
          alert("输入的号码位数错误，必须为"+phoneLenth+"位！");
          return false; 
        }
}

/** 
 * 当IP直通车产品发生改变时，设置IP直通车服务所需要的入参值
 */		
function onProductChange(obj)
{
	
	$("#IPService").attr("disabled",false);
	var product = $("#col_PRODUCT_ID").val();
	$("#col_PRODUCT_ID_HIDDEN").val(product);
    
}


/** 
 * 当输入新密码时的一次校验
 */
function checkPassword1(obj)
{
   var pass1 = document.getElementById('newpassword').value;   
   var pass1Lenth = pass1.length;
   var patrn=/^([+-]?)(\d+)$/;
   if (null == pass1 ||  ""== pass1.trim()) {
      alert("输入密码不能为空！"); 
      document.getElementById('newpassword').value='';	
      document.getElementById('newpassword').focus();
      return false;	
   }
   if(pass1Lenth != 6)
   {
      alert("输入密码长度不等于6,请重新输入！");
	  document.getElementById('newpassword').value='';	
	  document.getElementById('newpassword').focus();
	  return false;	  
   }
  var isNumber =  patrn.test(pass1);
  if(isNumber == false)
  {
      alert("输入密码必须全为数字！"); 
      document.getElementById('newpassword').value='';	
      document.getElementById('newpassword').focus();
      return false;	
  }
    
    var count=0;
	var count1=0;
	var count2=0;
	
	for(var i=0;i<pass1.length-1;i++){
		if(pass1.charAt(i)*1==pass1.charAt(i+1)){
				count++;
		}else if(parseInt(pass1.charAt(i))+1==parseInt(pass1.charAt(i+1))){
				count1++;	
		}else if(parseInt(pass1.charAt(i))-1==parseInt(pass1.charAt(i+1))){
				count2++;
		}
	}

	if(count==pass1.length-1||count1==pass1.length-1||count2==pass1.length-1){
			alert("新服务密码过于简单，请重新输入");
			document.getElementById('newpassword').value='';
			document.getElementById('newpassword').focus();						
			return false;
	}
}

/**
 * IP直通车服务 服务查询
 */
function popupService(){
	$.beginPageLoading("查询中...");
	$.ajax.submit(null, 'initService', '&IPService='+$('#col_IPServiceText').val()+'&PRODUCT_ID='+$('#col_PRODUCT_ID_HIDDEN').val(), 'popupServicePart', function(){
			$.endPageLoading();
			$('#popup-service').css('display','');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}


/** 
 * 选择服务后提交触发此方法，用来拼服务和服务包串，和服务变化标记返回至父页面
 */
function changeQueryType()
{  
	debugger;
	var serviceId = "", serviceTemp = "", packageTemp = "", packageId ="" ;
	var s = $("*[name='ipservices']");
	
	for (var i=0;i<s.length;i++)
	{
		if (s[i].checked)
		 {
	     	serviceTemp = s[i].getAttribute("value");
	     	packageTemp = s[i].getAttribute("package");
			serviceId += serviceTemp + "@";	
			packageId += packageTemp +"@"+serviceTemp+"~";		   	   	   		
		}
	}
	
  	
  	$("#col_PACKAGESVC").val(packageId);
  	$("#col_IPServiceText").val(serviceId);
  	$('#popup-service').css('display','none');
   //setReturnValue(null, null ,['ACTION_TAG','PACKAGESVC','IPServiceText'],["0",packageId,serviceId]);
}

/** 
 * 设置完密码点击确定后，进行密码校验且将值返回父页面
 */
function checkPassword(obj)
{
	var pass1 = $("#newpassword").val();
	var pass2 = $("#newpasswordagain").val();
	var passwd;	   
	if (pass1 != pass2)
	{
		alert("前后两次输入密码不一致!");
		return false;
    }
    else
    {
    	$("#col_TEMP_PWD").val(pass2);
    	$('#popup-pwd').css('display','none');
    }
   
}




 
 /** 
 * 点击新增按钮时新增行数据，对固定号码等进行校验
 */
function createDept() {
	
	if(!$.validate.verifyAll("IPExpressEdit")){
	  
	  	return false;
	}
    var limit = $('#LIMITNUM').val();
    var serialNumberF = $('#col_SERIAL_NUMBER_F').val();
    var serialNumberG = $('#col_SERIAL_NUMBER_G').val();
    
    var serialNumber = serialNumberF+serialNumberG;
    var phoneLenth = $('#PHONELENTH').val(); 
     
    var patrn=/^([+-]?)(\d+)$/;    
    var isNumber =  patrn.test(serialNumberG);
    if(isNumber == false)
     {
      alert("输入号码必须全为数字！"); 	    
      return false;
     } 
     
     //获取编辑区的数据
    var custEdit = $.ajax.buildJsonData("EditPart");
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
     
	var datacountx=0;
	var addcountx=0;
	for(var i=0;i<deptTable.length;i++)
	{
		var dealTagx = deptTable.get([i],"col_M_DEAL_TAG");
		var serialNumTagx = deptTable.get([i],"col_SERIAL_NUMBER_G");
		
		if(serialNumTagx == serialNumber)
		{
			alert("该号码已经存在于列表中！");
			return false;
		}
		if(dealTagx != '1')
		{
		   datacountx++;
		}
		if(dealTagx == '0')
		{
			addcountx=addcountx+1;
		}
	}
	if(limit !=0)
	{
      if(datacountx >= limit){alert("您只能绑定:"+limit+"部电话，不能再继续绑定！");return;}
    }
   
     
    if(phoneLenth != serialNumber.length)
    {
      alert("输入的号码位数错误，必须为"+phoneLenth+"位！");
      return false; 
    } 
     
    var productHidden = $('#col_PRODUCT_ID_HIDDEN').val();
    var ipService = $('#col_IPServiceText').val();      
    var pawd = $('#col_TEMP_PWD').val();
    var objPWD = $("#col_TEMP_PWD");  
    var defaultPWD = $('#DEFALTIPPASSWD').val();                
    if(pawd == "" || pawd == null){
  	   var confirTag = confirm("没有填写新密码，是否使用默认密码："+defaultPWD+"？") ;
  	   if(confirTag == true){ 
  	   	
  	    objPWD.val(defaultPWD);  
  	   }else{
  	   	
  	   	return false;
  	   }
    }
	for(var i=1;i<=deptTable.length;i++)
	{
		var serialTable =  deptTable.get([i],"col_SERIAL_NUMBER_G");
		var dealTag3 = deptTable.get([i],"col_M_DEAL_TAG");
		if(dealTag3 == "1")
		{
		   var oldService =  deptTable.get([i],"col_OLD_IPServiceText");			  
		  if(serialNumber == serialTable)
		   {
			   alert("已把该号码恢复到操作前的状态,如只需要修改信息请使用修改功能!");
			   custEdit["col_M_DEAL_TAG"] = "4";
			   custEdit["col_OLCOM_TAG"] = "0";
			   custEdit["col_IPServiceText"] = oldService;
			   custEdit["col_CONDITIONM"] = "未修改";
			   //更新表格数据
    		   $.table.get("DeptTable").updateRow(custEdit);
			  return ;
		   }
		}
	}
	var ipBrand = $("#col_BRAND_CODE").val(); 
    if(ipBrand == null || ipBrand == "")
    {
    	alert("请选择IP直通车品牌！");
    	return false;
    }
	$.beginPageLoading("校验中...");
    
    $.ajax.submit(null, 'checkPhone',  '&SERIAL_NUMBER_G='+serialNumber+'&IPSERVICE='+ipService+'&PRODUCT_ID_HIDDEN='+productHidden, 
	null, afterCreateDept,
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
   // ajaxDirect(this,'checkPhone','&SERIAL_NUMBER_G='+serialNumber+'&IPSERVICE='+ipService+'&PRODUCT_ID_HIDDEN='+productHidden, 'brandpart', null, afterCreateDept);
     
}

/** 
 * 执行createDept()后的方法.
 */
function afterCreateDept(data)
{
	 $.endPageLoading();
     var userIdB = data.get([0],'USER_ID_B');
     $('#col_USER_ID_B').val(userIdB);
     var phoneTime = data.get([0],'phoneTime');		    
     var ipServiceTemp = $('#col_IPServiceText').val();
     if(ipServiceTemp == "" || ipServiceTemp == null) 
     {
	     $("#col_IPServiceText").val(data.get([0],'serviceStr'));
	     $("#col_PACKAGESVC").val(data.get([0],'packageSvc'));
     }     
     var serialNumberF = $('#col_SERIAL_NUMBER_F').val();
     var serialNumberG = $('#col_SERIAL_NUMBER_G').val();
     var serialNumber = serialNumberF+serialNumberG; 
     
     /*custEdit["SERIAL_NUMBER_G"] = serialNumber;
     custEdit["CONDITIONM"]= "新增";
     custEdit["OLCOM_TAG"]= "1";
     custEdit["M_DEAL_TAG"]="0";
     custEdit["OPEN_DATE"]= phoneTime;*/
     
     
     
     $("#col_SERIAL_NUMBER_G").val(serialNumber);
	 $("#col_CONDITIONM").val("新增");
	 $("#col_OLCOM_TAG").val("1");
	 $("#col_M_DEAL_TAG").val("0");
     $("#col_OPEN_DATE").val(phoneTime);
     
     var custEdit = $.ajax.buildJsonData("EditPart");
     $.table.get("DeptTable").addRow(custEdit);
	 //增加完后清空相关数据,为增加下组记录准备
	   $('#col_TEMP_PWD').val("");
       $('#col_cond_CAUSE').val("");
       $('#col_IPServiceText').val("");
	   $('#col_PRODUCT_ID').val("");
	   $('#col_BRAND_CODE').val("");
	   $('#col_SERIAL_NUMBER_G').val("");
	  
}
	



/** 
 * 更新动态表格中的数据
 */
function updateDept(){	    
	 	
		var rowData = $.table.get("DeptTable").getRowData();
		if(rowData.length == 0){
			alert("请您选择记录后再进行操作！");
			return false;
		}
		
	   var rowSerilNumber = rowData.get("col_SERIAL_NUMBER_G");
	   var serialNumberF = $('#col_SERIAL_NUMBER_F').val();
       var serialNumberG = $('#col_SERIAL_NUMBER_G').val();
       var serialNumberx = serialNumberF + serialNumberG;
	   if(rowSerilNumber != serialNumberx)
	   {
	     alert("不能修改电话号码，若要增加新号码请点击新增键!");
	     return false;
	   }
	   var passWord = $('#col_TEMP_PWD').val();
	   var defaltPWD = $('#DEFALTIPPASSWD').val();
	   if(passWord == "" || passWord == null)
	   {
	     var conTag = confirm("没有填写新密码，是否使用默认密码:"+defaltPWD+"?");
	     if(conTag == true)
	     {
	       $("#col_TEMP_PWD").val(defaltPWD);
	     }
	     else
	     {
	      return false;
	     }	     
	   }
	    var dealTag2 = rowData.get("col_M_DEAL_TAG");
	    if(dealTag2 == '4')
	    {
			$("#col_CONDITIONM").val("已修改");			
			$("#col_M_DEAL_TAG").val("2");					
		}
		$("#col_OLCOM_TAG").val("1");
		$("#col_SERIAL_NUMBER_G").val(serialNumberx);
		var custEdit = $.ajax.buildJsonData("EditPart");
		$("#col_SERIAL_NUMBER_G").val(serialNumberG);
		//更新表格数据
	    $.table.get("DeptTable").updateRow(custEdit);
}




/** 
 * 删除动态表格的行数据
 */
function deleteDept() 
{
	var rowData = $.table.get("DeptTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	
   var dealTag4 =rowData.get("col_M_DEAL_TAG"); 	 
   var serialNumberF = $('#col_SERIAL_NUMBER_F').val();
   var serialNumberG = $('#col_SERIAL_NUMBER_G').val();
   var serialNumbery = serialNumberF+serialNumberG;	  
   if(dealTag4 !=0)
   {
	   var snG=$('#col_SERIAL_NUMBER_G').val();
	   var snF=$('#col_SERIAL_NUMBER_F').val();
	   $('#col_SERIAL_NUMBER_G').val(snF+snG);
	   $("#col_OLCOM_TAG").val("1");
	   $("#col_M_DEAL_TAG").val("1");	
	   $("#col_CONDITIONM").val("已删除");
	   var custEdit = $.ajax.buildJsonData("EditPart");
	   $('#col_SERIAL_NUMBER_G').val(snG);
	//删除固话条数限制
	$.table.get("DeptTable").updateRow(custEdit);
	}
	else
	{
	   $.table.get("DeptTable").deleteRow();
	}
}

	
	/** 
 * 业务提交前触发的方法
 */
function submitDepts() {			  
    
	 /*if(!$.validate.verifyAll("IPExpressEdit")){
	  
	  	return false;
	 }*/
	
	 //获取编辑区的数据
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	var countAll = 0;
	 
	for(var i=0;i<deptTable.length;i++)
	{
		var userIdB = deptTable.get([i],"col_USER_ID_B");
		var userIdBOld = deptTable.get([i],"col_OLD_USER_ID_B");			
		if(userIdB == userIdBOld)
		{
		  var productId =  deptTable.get([i],"col_PRODUCT_ID");
		  var productIdOld = deptTable.get([i],"col_OLD_PRODUCT_ID");
		  var brandCode = deptTable.get([i],"col_BRAND_CODE");
		  var brandCodeOld = deptTable.get([i],"col_OLD_BRAND_CODE");
		  var passWord = deptTable.get([i],"col_TEMP_PWD");
		  var passWordOld =  deptTable.get([i],"col_OLD_PWD");
		  var ipService1 = deptTable.get([i],"col_IPServiceText");
		  var ipService2 = deptTable.get([i],"col_OLD_IPServiceText");
		  
		  if(productId == productIdOld && brandCode == brandCodeOld && passWord == passWordOld && ipService1 == ipService2)
		  {
               $("#M_DEAL_TAG").val("4");
               $("#OLCOM_TAG").val("0");
		  }
		}	
		
		var ipDealTag = deptTable.get([i],"M_DEAL_TAG");
		if(ipDealTag != "4"){
			countAll++;	
		}	
	}
	
    if(countAll == 0)
    {
       alert("您没有做任何操作！");
       return false;
    }
    $("#X_CODING_STR").val(deptTable);
	return true;	
}


	