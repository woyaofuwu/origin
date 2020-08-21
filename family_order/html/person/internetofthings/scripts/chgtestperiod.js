function afterSubmitSerialNumber(data)
   {
       var reqStr = "&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
       ajaxSubmit(null,'init',reqStr,'ChgTestPeriodPart,HiddenPart',function(data){
        	if(data.get("ERROR_DESC")!=undefined && data.get("ERROR_DESC")!="")
        	{
        	   MessageBox.alert("提示",data.get("ERROR_DESC"));
        	   $.cssubmit.disabledSubmitBtn(true);//禁用掉认证组件
        	}
		},function(e,i){
			MessageBox.alert("提示",e+":"+i);
		});

   }
   
   
   function checkBeforeSubmit()
   {
      if($("#OLD_END_DATE").val() == $("#test_END_DATE").val())
      {
         MessageBox.alert("提示","变更后的测试期不能与原测试期相同！");
         return false;
      }
      
      return true;
   }