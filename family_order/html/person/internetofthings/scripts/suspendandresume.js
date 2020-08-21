function check(checkobj,compName)
{
   var  services = $('#'+compName).val();
   var temp = "";
   if(checkobj.checked)
   {
   		if(services == "")
	   {
	   		$('#'+compName).val(checkobj.value);
		}else
		{
		    temp =  $('#'+compName).val()+','+checkobj.value
		    $('#'+compName).val(temp);
		}
   }
   else
   {
       		var servicesArray = services.split(",");
   			for(var i =0;i<servicesArray.length;i++)
   			{
   			
	   			 //如果不是当前取消掉的，重新拼串
	   			   if(servicesArray[i] != checkobj.value)
	   			   {
	   			   
		                if( "" == temp )
		                {
		                  temp = servicesArray[i];
		                }else
		                {
		                	temp += ","+servicesArray[i];
		                }   			   
	   			     
	   			   }
   			}
   			$('#'+compName).val(temp);
   }
 }
   
   
   function afterSubmitSerialNumber(data)
   {
        $('#SERIAL_NUMBER').val(data.get("USER_INFO").get("SERIAL_NUMBER"));
       var reqStr = "&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
       ajaxSubmit(null,'getUserServices',reqStr,'SvcStateList',function(data){
        
		},function(e,i){
			MessageBox.alert("提示",e+":"+i);
		});

   }
   
   
   
   function checkIsChange(objName)
   {
   		if($('#'+objName).val()=="")
   		{
   		   MessageBox.alert("提示","您没有进行任何操作，不能提交");
   		   return false;
   		}
   		
   		return true;
   }
   