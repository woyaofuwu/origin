$(function(){
	$("#FLAG").val("0");
})

function afterSubmitSerialNumber(data)
{
	$("#FLAG").val("0");
	var reqStr = "&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER")+"&FLAG="+$("#FLAG").val();
    ajaxSubmit(null,'getEffectTime',reqStr,'ChgEffectTimePart,HiddenPart',function(data){
     	if(data.get("ERROR_DESC")!=undefined && data.get("ERROR_DESC")!="")
     	{
     	   MessageBox.alert("提示",data.get("ERROR_DESC"));
     	   $.cssubmit.disabledSubmitBtn(true);//禁用掉认证组件
     	}
		},function(e,i){
			MessageBox.alert("提示",e+":"+i);
		});
}
   
function changeValue()
{
	var reqStr = "&USER_ID="+$("#USER_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&FLAG="+$("#FLAG").val();
	   ajaxSubmit(null,'getEffectTime',reqStr,'ChgEffectTimePart,HiddenPart',function(data){
		   if(data.get("ERROR_DESC")!=undefined && data.get("ERROR_DESC")!="")
    	   {
    	   MessageBox.alert("提示",data.get("ERROR_DESC"));
    	   $.cssubmit.disabledSubmitBtn(true);//禁用掉认证组件
    	   }else{
       		 $.cssubmit.disabledSubmitBtn(false);
        	}
		},function(e,i){
			MessageBox.alert("提示",e+":"+i);
		});
}

function changeEnd()
{
	var reqStr = "&USER_ID="+$("#USER_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&START_DATE="+$("#START_DATE").val()+"&test_ELEMENT_ID="+$("#test_ELEMENT_ID").val();
	   ajaxSubmit(null,'getENDTime',reqStr,'endPart',function(data){
		 
		},function(e,i){
			MessageBox.alert("提示",e+":"+i);
		});
}
   
function checkBeforeSubmit()
{
	var sysDate = getCurrDate();
	
	if($("#FLAG").val()==undefined  || $("#FLAG").val()=="")
    {
		 MessageBox.alert("提示","请选择套餐类型！");
	     return false;	 
	}
	if($("#START_DATE").val() < sysDate)
    {
       MessageBox.alert("提示","变更后的生效时间不能小于当前时间！");
       return false;
    }
    if($("#OLD_START_DATE").val() == $("#START_DATE").val())
    {
       MessageBox.alert("提示","变更后的生效时间不能与原生效时间相同！");
       return false;
    }
    return true;
}
   
   /***获取系统时间***/
function getCurrDate()
{
   	var now=new Date();
   	ye=now.getFullYear();
   	mo=now.getMonth()+1;
   	da=now.getDate();
   	ho=now.getHours();
   	mi=now.getMinutes();
   	se=now.getSeconds();

   	mo=mo<10?"0"+mo:mo;
   	da=da<10?"0"+da:da;

   	var startData =ye+"-"+mo+"-"+da;
   	return startData;
}