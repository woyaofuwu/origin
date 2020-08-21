if(typeof(PlatOrder)=="undefined"){window["PlatOrder"]=function(){};var platOrder = new PlatOrder();}
(function(){
	$.extend(PlatOrder.prototype,{
		afterSubmitSerialNumber: function(data){
			//先清理
			userPlatSvcsList.clearCache();
			platOrderAdd.clearCache();
			try{
			   if(mytab)
			   {
			   		mytab.switchTo("订购区");
			   }
			}catch(ex){
			}
		
			var userId = data.get("USER_INFO").get("USER_ID");
			var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			var psptId = data.get("CUST_INFO").get("ORIGIN_PSPT_ID");
			$("#USER_ID").val(userId);
			$("#USER_EPARCHY_CODE").val(eparchyCode);
			$("#SERIAL_NUMBER").val(serialNumber);
			$("#PSPT_ID").val(psptId);
			$("#IS_REAL_NAME").val(data.get("CUST_INFO").get("IS_REAL_NAME"));
			$("#CUST_NAME").val(data.get("CUST_INFO").get("ORIGIN_CUST_NAME"));
			$("#PSPT_TYPE_CODE").val(data.get("CUST_INFO").get("PSPT_TYPE_CODE"));
			
			userPlatSvcsList.renderComponent(userId,eparchyCode);
			if($("#showSwitch").val()=="true"){
				platOrderAdd.switchTabset("DSMP开关区","DSMP开关区");
			}
			if($("#showKeyBusiness").val()=="true"){
				platOrderAdd.switchTabset("重点业务办理区","重点业务办理区");
			}
		},
		
		submitDatas: function(){
			var data = userPlatSvcsList.getOperElements();
			var param = '';
			if(data&&data.length>0){
			    //platOrder.specialDeal(data); //提交前进行的特殊处理
				param += "&SELECTED_ELEMENTS="+data.toString();
			}
			if(userPlatSvcsList.allCancels&&userPlatSvcsList.allCancels.length>0){
				param += "&ALL_CANCEL="+userPlatSvcsList.allCancels.toString();
			}
			if(userPlatSvcsList.allSwitch&&userPlatSvcsList.allSwitch.length>0){
				var switches = new $.DatasetList();
				userPlatSvcsList.allSwitch.eachKey(function(key){
					var temp = userPlatSvcsList.allSwitch.get(key);
					switches.add(temp);
				});
				param+="&ALL_SWITCH="+switches.toString();
			}
			
			var elements = userPlatSvcsList.selectedElements;
			for(var i=0;i<elements.length;i++)
			{
				if(elements.get(i).get("IS_WRITE_ATTR") == false)
				{
					MessageBox.alert("提示","您还没有确定填写的属性值，请点击确认，再提交");
				    return false;
				}
			}
			
			 
			
			if(param.length<=0){
				MessageBox.alert("提示","您没有进行任何操作，不能提交");
				return false;
			}
			else{
				param+="&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
				$.cssubmit.addParam(param);
				return true;
			}
		},
		
		afterSubmit: function(data){
			$.showSucMessage("业务受理成功","工单流水号"+data.get("ORDER_ID"));
		},
		
		changeWlanPackType:function(eventObj){
		 
		  if(userPlatSvcsList.operCode !="" && userPlatSvcsList.operCode != "07"){
		      if(eventObj.value=='1'){
				$("#401_2").attr("disabled",true);
				$("#401").attr("disabled",null);
				
				$("#401_2").val("");
	
				}
				else if(eventObj.value=='2'){
					$("#401_2").attr("disabled",null);
					$("#401").attr("disabled",true);
					
					$("#401").val("");
				}
				else{
					$("#401_2").attr("disabled",true);
					$("#401").attr("disabled",true);
				}
		  }
			
		},
		
		
		specialDeal:function(data){
		  //针对WLAN的特殊处理
		   for(var i=0;i<data.length;i++){
		      var element = data.get(i);
		        //WLAN
				if(element.get("SERVICE_ID")=="98000201" ){
				    var attrs = element.get("ATTR_PARAM");
				    var temAttrs = new $.DatasetList();
				    var attrLength = attrs.length;
				    for(var j=0;j<attrs.length;j++){
				           //旧密码不提交
				           if(attrs.get(j).get("ATTR_CODE")!= "OLD_PASSWORD"){
				               temAttrs.add(attrs.get(j));
				           }
				    }
				    
				    element.put("ATTR_PARAM",temAttrs);
				}
		   }
		}
	});
})();
