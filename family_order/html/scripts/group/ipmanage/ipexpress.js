/** 
 * 选择服务后提交触发此方法，用来拼服务和服务包串，和服务变化标记返回至父页面
 */
function changeQueryType()
{  
    var data = "", serviceId = "", serviceTemp = "", packageTemp = "", packageId ="" ;
	var s = $("input[name='ipservices']");	
	
	for (var i=0;i<s.length;i++)
	{
		if (s[i].checked)
	    {
		    data = $(s[i]).val();
		    var tabledata = data.split(";");
		    serviceTemp = tabledata[0];
	     	packageTemp = tabledata[1];
	     	
			serviceId += serviceTemp + "@";	
			packageId += packageTemp +"@"+serviceTemp+"~";
	    }
	}
	
      var serviceOld = new Array();
      var serviceNew = new Array();
      var serviceHidden = $('#SERVICE_ID_HIDDEN').val();	
  
  if(serviceHidden.length != tabledata[1].length)
  { 
     setReturnValue({'ACTION_TAG':"0",'PACKAGESVC':packageId,'IPServiceText':serviceId},true);
     return;
  }else
  {
   for(var m=0;m<tabledata[1].length;m++)
   { 
    for(var n=0;n<serviceHidden.length;n++)
    { 
      if(tabledata[1][m] == serviceHidden[n])
       {
         delete tabledata[1][m];
         delete serviceHidden[n]; 
       }
     } 
   } 
 
   for(var m=0;m<tabledata[1].length;m++)
   { 
    for(var n=0;n<serviceHidden.length;n++)
    { 
      if(tabledata[1][m] != serviceHidden[n])
       {
         setReturnValue({'ACTION_TAG':"0",'PACKAGESVC':packageId,'IPServiceText':serviceId},true);
         return;
       }
     } 
   } 
  }	
	setReturnValue({'ACTION_TAG':"1",'PACKAGESVC':packageId,'IPServiceText':serviceId},true);

}



	