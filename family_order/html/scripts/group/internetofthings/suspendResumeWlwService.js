	function check(checkobj,compName)
	{
		
	   var id = $(checkobj).val();
	   var instId = $(checkobj).attr('instId');
	   var startDate = $(checkobj).attr('startDate');
	   var userIDA = $(checkobj).attr('userIDA');
	  
	   var checkedRow = id+","+instId+","+startDate+","+userIDA;
	   var  services = $('#'+compName).val();
	   
	   var temp = "";
	   if(checkobj.checked)
	   {
	   		if(services == "")
		    {
		   		$('#'+compName).val(checkedRow);
			}else
			{
			    temp =  $('#'+compName).val()+';'+checkedRow;
			    $('#'+compName).val(temp);
			}
	   }
	   else
	   {
	       		var servicesArray = services.split(";");
	   			for(var i =0;i<servicesArray.length;i++)
	   			{
	   			
		   			   //重新拼串
		   			   if(servicesArray[i] != checkedRow )
		   			   {
		   			   
			                if( "" == temp )
			                {
			                  temp = servicesArray[i];
			                }else
			                {
			                	temp += ";"+servicesArray[i];
			                }   			   
		   			     
		   			   }
	   			}
	   			$('#'+compName).val(temp);
	   }
	 }
   
    function checkIsChange(objName)
    {
   		if($('#'+objName).val()=="")
   		{
   		   alert("您没有进行任何操作，不能提交");
   		   return false;
   		}
   		return true;
    }

  //成员号码资料查询成功后调用的方法
  function selectMemberInfoAfterAction(data){
	  
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	    
    if(memuserInfo.get("BRAND_CODE")!= "PWLW"){
    	alert("非物联网用户不能办理该业务!");
      	return false;
    }
  	
    if(memuserInfo.get("USER_STATE_CODESET")!= "0"){
    	alert("用户不是正常状态，不能办理该业务!");
      	return false;
    }
  	
  	//异地号码判断
    if(!afterCheckInfo(data)) {
    	return false;
    }
    
    insertMemberCustInfo(memcustInfo);
    insertMemberUserInfo(memuserInfo);
    
    var meb_userid=$("#MEB_USER_ID").val();
    var meb_serialNumber=$("#MEB_SERIAL_NUMBER").val();
    
    $('#SERIAL_NUMBER').val(meb_serialNumber);
    $('#USER_ID').val(meb_userid);
    
    var reqStr = "&USER_ID="+meb_userid+"&SERIAL_NUMBER="+meb_serialNumber;
    ajaxSubmit(null,'getUserServices',reqStr,'SvcStateList',function(data){},function(e,i){alert(e+":"+i);});
  }

  //成员资料查询失败后调用的方法
  function selectMemberInfoAfterErrorAction() {
  	clearMemberCustInfo();
  	clearMemberUserInfo();
  }

  //判断 异地号码的后续处理
  function afterCheckInfo(data){
    var result = data.get("OUT_PHONE","false");;
    if(result == "true"){
    	if(!(confirm("请注意：该号码是异地号码，是否继续?"))){
    	    //选择取消则退出办理
  	    $('#cond_SERIAL_NUMBER').val();
  	    selectMemberInfoAfterErrorAction();
  	    return false;
  	}
    }
    return true;
  }
   