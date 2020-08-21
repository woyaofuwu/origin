function changeQueryType() {
   var choose = $("#QUERYTYPE").val();
   if (choose=="2") { //按集团编码查询
   	  $("#selectGroupPart").css('display','block');
      $("#groupProductIdPart").css('display','none');
      $("#cond_SERIAL_NUMBER").attr('disabled',true);
      $("#OrderedPart").css('display','block');
      $('#GroupInfoPart').css("display","block");
      }
   else if (choose=="1") { //按成员号码查询
      $("#selectGroupPart").css('display','none');
      $("#groupProductIdPart").css('display','none');
      $("#cond_SERIAL_NUMBER").attr('disabled',false);
      $("#OrderedPart").css('display','none');
      $('#GroupInfoPart').css("display","none");
   }
   else if (choose == "3") { //按集团服务号码查询
      $("#selectGroupPart").css('display','none');
      $("#groupProductIdPart").css('display','block');
      $("#cond_SERIAL_NUMBER").attr('disabled',true);
      $("#OrderedPart").css('display','none');
      $('#GroupInfoPart').css("display","none");
   }
   
   $.ajax.submit(this, 'setPageNull','','BasePart,ServicPart,otherPart');
}

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	// 填充集团客户显示信息
	insertGroupCustInfo(data);
	getProductInfos();
}

//通过集团号码查询集团资料成功后调用的方法
function selectGroupErrorAfterAction(data){

	//清空填充的集团客户信息内容
    clearGroupCustInfo();
}

function getProductInfos()
{
	$.beginPageLoading();
	$.ajax.submit('GroupCustInfoPart,Ordered','getOrderProduct', '','BasePart,OrderedPart,otherPart', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function searchMemberBySerialNumber() 
{
   var serialValue = $("#cond_SERIAL_NUMBER").val();
   
   $.beginPageLoading();
   var choose=$("#QUERYTYPE").val();
   if (choose == "1") { //按成员服务号码查询
      
     $.ajax.submit(this, 'queryMemberInfo2', '&cond_SERIAL_NUMBER='+serialValue, null, 
		function(data)
		{
			$.endPageLoading();	
			afterCheckGroupInfo2(data);	
		},
		function(error_code,error_info,derror)
		{		
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    }); 
	}    
}

function afterCheckGroupInfo2(data) 
{
    if(data == null)
		return;
	var orderInfos = data.get("ORDERED_GROUPINFOS");	
		
	renderGrpList(orderInfos);
}

function searchByGroupSerialNumber() 
{  
   var grpsn = $("#cond_GRP_SN").val();
   $.beginPageLoading();
   $.ajax.submit('this', 'getGroupInfoByGrpSn','&cond_GRP_SN='+grpsn, 'BasePart,ServicPart,otherPart',
	function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function searchProduct()
{
    var userId = $("#USER_ID").val();
    $.beginPageLoading();
	$.ajax.submit('OrderedPart', 'queryBaseInfo',null, 'BasePart,ServicPart',
	function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


/*
 *判断是否是数字 
 */
function isNumber(obj){

	var reg= /^([1-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/; 

	if (!reg.test(obj) ){
	alert('服务号码必须为数字，请重新输入');
		return false;
	}
	if(obj.length <= 0){
	 alert('请输入成员服务号码');
	 return false;
	}
	
	return true;
}

/*
 *提交校验
 */
function onSubmitBaseTradeCheck()
{
     if(!$.validate.verifyField($("#NORMAL_TIME"))) return false;
     
     return true;
}



