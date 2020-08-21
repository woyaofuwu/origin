
/**
 * 导入
 * @returns {Boolean}
 */
function importQueryData(){

	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	
	$.ajax.submit('SubmitQueryInfo','importQueryDataInfo','','SvcStateList',function(data){
		$.endPageLoading();
		var msg=data.get(0).get("msg");
		   $.showSucMessage(msg);
		   resetPage('SubmitQueryInfo');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


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
   