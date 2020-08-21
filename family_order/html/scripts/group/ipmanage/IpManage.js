function getIpUserInfo()
{
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryIpUserInfosBySN", null, "custPart,userPart,acctPart,ipPart,OtherPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

/** 
 * 固定电话号码初步校验
 */	
function checkPhoneNumber()
{    
      var phoneType = $('#PHONETYPE').val();  
      var phoneLenth = "12";
      var serialNumberS = $('#SERIAL_NUMBER_S').val();
      var serialNumberE = $('#SERIAL_NUMBER_E').val();
      
      var serialNumber = serialNumberS+serialNumberE;       
      var patrn = /^([+-]?)(\d+)$/;    
      var isNumber =  patrn.test(serialNumber);
      
      if(isNumber == false)
	  {
	      alert("输入号码必须全为数字");
	      var serialNumberG3 = $('#SERIAL_NUMBER_E');
	          serialNumberG3.setAttribute('value',"");    
	      return false;
	  }
      if(phoneLenth != serialNumber.length)
      {
          alert("输入的号码位数错误，必须为"+phoneLenth+"位！");
          return false; 
      }
}

function tableRowClick()
{
	var rowData = $.table.get("OtherTable").getRowData();
	
	$("#DISCNT_CODE").val(rowData.get("DISCNT_CODE"));
	$("#PRODUCT_ID").val(rowData.get("PRODUCT_ID"));
	$("#BRAND_CODE").val(rowData.get("BRAND_CODE"));
	var serialNumber = rowData.get("SERIAL_NUMBER_G");
	var serialNumberS = serialNumber.substring(0,4);
    var serialNumberE = serialNumber.substring(4);
    var tempPwd = rowData.get("TEMP_PWD");
    $("#SERIAL_NUMBER_S").val(serialNumberS);
	$("#SERIAL_NUMBER_E").val(serialNumberE);
	$("#PARAM_CODE").val(0);
	$("#TEMP_PWD").val(tempPwd);
	$("#SERIAL_NUMBER_END_S").val(serialNumberS);
	$("#SERIAL_NUMBER_END_E").val(serialNumberE);
}

/** 
 * 点击新增按钮时新增行数据，对固定号码等进行校验
 */
function createDept() 
{
    var allData = getAllTableData("OtherTable", null);
    var rowcount = allData.length;
    var datacountx=0;
    for(var i=0;i<rowcount;i++)
    {
	   var dealTagx = allData.get("M_DEAL_TA");
	   if(dealTagx != '1')
	   {
	      datacountx++;
	   }
    }
      var serialNumberS = $('#SERIAL_NUMBER_S').val();
      var serialNumberE = $('#SERIAL_NUMBER_E').val();
      var serialNumber = serialNumberS+serialNumberE;
      var SERIAL_NUMBER_END_S = $('#SERIAL_NUMBER_END_S').val();
      var SERIAL_NUMBER_END_E = $('#SERIAL_NUMBER_END_E').val();
      var startnumber = parseInt(SERIAL_NUMBER_END_E);
      var endnumber =  parseInt(SERIAL_NUMBER_END_E);
      if(datacountx + endnumber - startnumber > 999){
        alert("集团已绑定了超过最大的电话数量999部！不能继续绑定!");
        return false;
      }
      var phoneLenth = 12;
       
      var patrn=/^([+-]?)(\d+)$/;    
      var isNumber =  patrn.test(serialNumberE);
      if(isNumber == false)
      {
          alert("输入号码必须全为数字！"); 	    
          return false;
      } 
    
      if(phoneLenth != serialNumber.length)
      {
        alert("输入的号码位数错误，必须为"+phoneLenth+"位！");
        return false; 
      } 
      if(serialNumberE > SERIAL_NUMBER_END_E){
      	alert("起始IP电话号码不能大于截止IP电话号码，请重新输入！");
      	return false;
      }
      
      var brandCode = $('#BRAND_CODE').val();          
      if(brandCode == "" || brandCode == null)
      {
       		alert("品牌不能为空！");
       		return false; 
      }
      var productid = $('#PRODUCT_ID').val();          
      if(productid == "" || productid == null)
      {
       		alert("产品不能为空！");
       		return false; 
      }
      var discntCode = $('#DISCNT_CODE').val();          
      if(discntCode == "" || discntCode == null)
      {
       		alert("优惠不能为空！");
       		return false; 
      }
      var productHidden = $('#PRODUCT_ID').val();
      var ipService = $('#IPServiceText').val();  
      var ippackage = $('#PACKAGESVC').val();    
      var pawd = $('#TEMP_PWD').val();
      var objPWD = $("#OLD_PWD").val();  
      var defaultPWD = "000000";                
      var packagesvc = $("#PACKAGESVC").val();
      if(pawd == "" || pawd == null)
      {
    	   var confirTag = confirm("没有填写新密码，是否使用默认密码："+defaultPWD+"？") ;
    	   if(confirTag == true)
    	   { 
    	   	  pawd = defaultPWD;
    	      objPWD = defaultPWD;  
    	   }else
    	   {
    	      return false;
    	   }
      }
      for(var j= serialNumberE; j<= SERIAL_NUMBER_END_E; j++)
      {
      	var serialNumberZ = SERIAL_NUMBER_END_S+j;
      	$('#SERIAL_NUMBER_G').val(serialNumberZ);
      	
      	var ds = getAllTableData("OtherTable", null);; //获得表格的数据	
	    for(var i=0; i<ds.length; i++){
		var sn = ds.get(i).get("SERIAL_NUMBER_G");  //表格中的服务号码
		if(serialNumberZ == sn ) {
			alert('关键字段['+'固定号码'+']已存在同样的值\r\n['+serialNumberZ+'],请不要重复添加！');			
			return false;
			continue;
		}
	  }
      	
	    $.beginPageLoading();
	    $.ajax.submit('OtherTable','checkPhone', '&PACKAGESVC='+ippackage+'&SERIAL_NUMBER_G='+serialNumberZ+'&IPSERVICE='+ipService+'&PRODUCT_ID_HIDDEN='+productHidden+'&PASSWORD='+pawd+"&DISCNT_CODE="+discntCode, null, 
	    function(data){
		$.endPageLoading(); 
		afterCreateDept(data);
	    },
	    function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);		
        });    
      } 
}

/** 
 * 执行createDept()后的方法.
 */
function afterCreateDept(data)
{
     var phone = data.get(0);
     var userIdB = phone.get('USER_ID_B');
     var userIdBHidden = $('#USER_ID_B').val();
     var phoneTime = phone.get('phoneTime');		
     var serialNumber = phone.get('SERIAL_NUMBER');   
     var tempPWD = phone.get('PASSWORD');
     var serviceStr = phone.get('serviceStr');
     var packageSvc = phone.get('packageSvc');
	 var discntcode = phone.get('DISCNT_CODE');	
	 
     var iphoneinfoEdit = new Array();
     
     iphoneinfoEdit["IPServiceText"]= serviceStr;
     iphoneinfoEdit["USER_ID_B"]= userIdB;
     iphoneinfoEdit["PACKAGESVC"]= packageSvc;
     iphoneinfoEdit["SERIAL_NUMBER_G"]= serialNumber;
	 iphoneinfoEdit["CONDITIONM"]= "新增";
	 iphoneinfoEdit["OLCOM_TAG"]= "1";
	 iphoneinfoEdit["M_DEAL_TAG"]= "0";
     iphoneinfoEdit["OPEN_DATE"]= phoneTime;
     iphoneinfoEdit["PRODUCT_ID"]= "608001";
     iphoneinfoEdit["BRAND_CODE"]= "IP10";
     iphoneinfoEdit["TEMP_PWD"]= tempPWD;
     iphoneinfoEdit["DISCNT_CODE"]= discntcode;
     iphoneinfoEdit["PACKAGESVC"]= packageSvc;
     
     $.table.get("OtherTable").addRow(iphoneinfoEdit);
	 
	 //增加完后清空相关数据,为增加下组记录准备
	  $("#DISCNT_CODE").val("");
      $("#PRODUCT_ID").val("");
      $("#SERIAL_NUMBER_E").val("");
      $("#PARAM_CODE").val("");
      $("#TEMP_PWD").val("");
      $("#SERIAL_NUMBER_END_E").val(""); 
      $("#BRAND_CODE").val("");
      $('#IPServiceText').val(""); 
		
}
	/** 
 * 更新动态表格中的数据
 */
function updateDept()
{	
	var rowData = $.table.get("OtherTable").getRowData();
	
	var rowSerilNumber = rowData.get("SERIAL_NUMBER_G");
	var serialNumberS = $('#SERIAL_NUMBER_S').val();
    var serialNumberE= $('#SERIAL_NUMBER_E').val();
    var serialNumberx = serialNumberS+serialNumberE;
    $("#SERIAL_NUMBER_G").val(serialNumberx);
    
    if(rowSerilNumber != serialNumberx)
    {
     alert("不能修改电话号码，若要增加新号码请点击新增键!");
     return false;
    }
   
    var passWord = $('#TEMP_PWD').val();
    var defaltPWD = "000000";
    if(passWord == "" || passWord == null)
    {
       var conTag = confirm("没有填写新密码，是否使用默认密码:"+defaltPWD+"?");
        if(conTag == true)
        {
           passWord = defaltPWD;
        }
        else
        {
           return false;
        }	     
    }
   
    var dealTag= rowData.get("M_DEAL_TAG");	
	var iphoneinfoEdit = new Array();   
    if(dealTag == '4')
    {
       iphoneinfoEdit["CONDITIONM"]= "已修改";
	   iphoneinfoEdit["M_DEAL_TAG"]= "2";
    }
	iphoneinfoEdit["OLCOM_TAG"]= "1";
	iphoneinfoEdit["TEMP_PWD"]= passWord;
        
    var datalineData = $.ajax.buildJsonData("ipPart");
    var packagesvc = rowData.get("PACKAGESVC");
    var IPServiceText = rowData.get("IPServiceText");
    datalineData["PACKAGESVC"]=packagesvc;
    datalineData["IPServiceText"]=IPServiceText;
    $.table.get("OtherTable").updateRow(datalineData);
    $.table.get("OtherTable").updateRow(iphoneinfoEdit);   
   
    $("#DISCNT_CODE").val("");
    $("#PRODUCT_ID").val("");
	$("#SERIAL_NUMBER_E").val("");
	$("#PARAM_CODE").val("");
	$("#TEMP_PWD").val("");
	$("#SERIAL_NUMBER_END_E").val(""); 
	$("#BRAND_CODE").val("");
	$('#IPServiceText').val(""); 

}        
	
/** 
 * 删除动态表格的行数据
 */
function deleteDept() 
{
	var rowData = $.table.get("OtherTable").getRowData();
	var dealTag2 = rowData.get("M_DEAL_TAG");
	var serialNumberS = $('#SERIAL_NUMBER_S').val();
    var serialNumberE = $('#SERIAL_NUMBER_E').val();
    var serialNumbery = serialNumberS+serialNumberE;
    
    var iphoneinfoEdit = new Array();
    if(dealTag2 !=0)
	{
		iphoneinfoEdit["SERIAL_NUMBER_G"]= serialNumbery;
	    iphoneinfoEdit["M_DEAL_TAG"]= "1";
	    iphoneinfoEdit["CONDITIONM"]= "已删除";
	    iphoneinfoEdit["OLCOM_TAG"]= "1";
    }
		
	$.table.get("OtherTable").updateRow(iphoneinfoEdit);
	
	$("#DISCNT_CODE").val("");
    $("#PRODUCT_ID").val("");
	$("#SERIAL_NUMBER_E").val("");
	$("#PARAM_CODE").val("");
	$("#TEMP_PWD").val("");
	$("#SERIAL_NUMBER_END_E").val(""); 
	$("#BRAND_CODE").val("");
	$('#IPServiceText').val("");
}
	
/** 
 * 通过此方法动态获取IP直通车服务的查询条件
 */
	function getIpServParam()
	{
		var param = '&IPService='+$('#IPServiceText').val();
		var productHidden = $('#PRODUCT_ID').val();		
		var serial_number = $("#cond_SERIAL_NUMBER").val();
		param +='&PRODUCT_ID='+productHidden+'&SERIAL_NUMBER='+serial_number+'&refresh='+true;
		return param; 
	}
	
/** 
 * 当输入新密码时的一次校验
 */
function checkPassword1()
{	
	var pass1 = $("#newpassword").val();
   	var pass1Lenth = pass1.length;
   	var patrn = /^([+-]?)(\d+)$/;
   	
   	if(!$.validate.verifyField($("#newpassword"))) 
   	{
   		$("#newpassword").val('');
   		$("#newpassword").focus();
   		return false;
   	}
   	
   	if(pass1Lenth != 6)
   	{
    	alert("输入密码长度不等于6,请重新输入！");
    	$("#newpassword").val('');
   		$("#newpassword").focus();
	  	return false;	  
   	}
  	var isNumber =  patrn.test(pass1);
  	if(isNumber == false)
  	{
    	alert("输入密码必须全为数字！"); 
    	$("#newpassword").val('');
   		$("#newpassword").focus();
      	return false;	
  	}
    
    var count=0;
	var count1=0;
	var count2=0;
	
	for(var i=0; i<pass1.length-1; i++)
	{
		if(pass1.charAt(i)*1 == pass1.charAt(i+1))
		{
			count++;
		}
		else if(parseInt(pass1.charAt(i))+1==parseInt(pass1.charAt(i+1)))
		{
			count1++;	
		}
		else if(parseInt(pass1.charAt(i))-1==parseInt(pass1.charAt(i+1)))
		{
			count2++;
		}
	}

	if(count == pass1.length-1 || count1 == pass1.length-1 || count2 == pass1.length-1)
	{
		alert("新服务密码过于简单，请重新输入");
		$("#newpassword").val('');
   		$("#newpassword").focus();					
		return false;
	}	
}

/** 
 * 设置完密码点击确定后，进行密码校验且将值返回父页面
 */
function checkPassword()
{
    if(!$.validate.verifyAll('PasswdPart')) return false;
    
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
        setReturnValue(['TEMP_PWD',pass2],true);
    }
}


// 业务受理前的校验
function onSubmitBaseTradeCheck()
{
	var datasetTable = $.table.get("OtherTable").getTableData()
	
	if(datasetTable == null || datasetTable == "" || datasetTable == "[]"){
		alert("已开通信息不能为空");
		return false;
	}
	
	if (!window.confirm("确定提交吗？")) return false;
	
	$.cssubmit.setParam("DATA_LIST", datasetTable);
	
	return true;
}

 //获取所有表格数据
 function getAllTableData(tbName,cols) {
	var dataset = new Wade.DatasetList();
	var table = $.table.get(tbName);
	//var d = $.table.get(tbName);
	var c = Wade("tbody", table.getTable()[0]);
	var e = table.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each (
			function(h, i) {
		    	var json = table.getRowData(cols, h + e);
	     		if (json) {
				   dataset.add(json);
				}
				json = null;
			});
	}
	c = null;
	table = null;
	return dataset;
}		
	
	
	
	
	
	
	