           function  checkVal(){
               var sysType=$("#cond_SYSTEM_TYPE").val();
               if(sysType==""){
                   alert("查询条件[平台]不能为空!");
                   return false;
               }
		        var number =$("#cond_USR_IDENTIFIER").val();
				var flowID = $("#cond_BASS_FLOW_ID").val();
				if(flowID==""&&number==""){
					alert("查询条件 [服务号码] 和  [经分流水号] 不能同时为空!");
					return false;
				}
				var indate=$("#cond_IN_DATE").val();
				if(indate==""){
				    alert("查询条件[导入时间]不能为空!");
					return false;
				}
				return true;
		    }
		    function queryTask(){
				 if(!checkVal())
		         {
		         	return  false;
		         }
				    $.beginLoading("查询中...");
				 $.ajax.submit('QueryPart', 'qryPCCBatTaskStatus', null, 'QueryListPart',function(data){
				     $.endLoading();
					    var dat=data.get('RESULT');
				        if(dat==0){
				    	   alert("批量操作任务执行状态查询成功!");
				    	}
				    	else if(dat==2){
				    	   alert("批量操作任务执行状态查询数据不存在!");
				    	} 
				    	else{
				    	   alert("批量操作任务执行状态查询调用iboss接口处理失败!");
				    	}
					},
					function(error_code,error_info){
						$.endLoading();
						alert(error_info);
				    });
			}  
		    
		    function myTabSwitchAction(ptitle, title) {

		    	return true;
		    }