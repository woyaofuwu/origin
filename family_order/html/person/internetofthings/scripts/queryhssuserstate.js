   
   function queryHssuserstate(data)
   { 
       ajaxSubmit('QueryRecordPart','queryInfos','','SvcStateList',function(data){
    	   if(data.get("ERROR_DESC")!=undefined && data.get("ERROR_DESC")!="")
    		   alert(data.get("ERROR_DESC"));
        
		},function(e,i){
			alert(e+":"+i);
		});

   }
   
