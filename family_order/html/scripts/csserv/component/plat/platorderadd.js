if(typeof(PlatOrderAdd)=="undefined"){window["PlatOrderAdd"]=function(){};var platOrderAdd = new PlatOrderAdd();}
(function(){
	$.extend(PlatOrderAdd.prototype,{
		isShowAllCancel:false,
		isShowSwitch:false,
		isShowkeybusiness:false,
		queryPlatSvc: function(){
			var disableSearch = $("#searchBtn").attr("isdisable");
			if("true" == disableSearch)
			{
				MessageBox.alert("提示","搜索功能已经被禁用");
				return;
			}
			
			var cond = $("#platSearch").val();
			if(cond==""){
				MessageBox.alert("提示","请输入查询条件");
				return;
			}
			else{
				var data = '&COND='+cond+'&TABSET=ORDER_AREA';
				$.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),null);
			}
		},
		
		clearCache: function(data){
		   platOrderAdd.isShowAllCancel = false;
		   platOrderAdd.isShowSwitch = false;
		   platOrderAdd.isShowkeybusiness = false;
		},
		
		//变更搜索方式 ，局方要求快捷搜索和按业务平台搜索同时存在
		switchSearchWay: function(eventObj){
			var elements = $(eventObj).siblings();
			for(var i=0;i<elements.length;i++)
			{
				$(elements[i]).attr("class","");
			}
			
			$(eventObj).attr("class","on");
			
			var title = $(eventObj).attr("title");
			if(title == "快捷搜索")
			{
				$("#queryCond").attr("style","display:block");
				$("#selectCond").attr("style","display:none");
			}else
			{
				$("#selectCond").attr("style","display:block");
				$("#queryCond").attr("style","display:none");
			}
			
		},
		
		checkBoxAction: function(eventObj, iskey){
			if(userPlatSvcsList){
				if(eventObj.checked){
					var serviceId = $(eventObj).attr("serviceId");
					if (serviceId == '98001901' || serviceId == '80002009' || serviceId == '55687442'
						|| serviceId == '55627768'){
						MessageBox.alert("提示","此用户可办理相应福包业务，可通过产品变更界面进行办理。");
					}
					if(userPlatSvcsList.findElement(serviceId)){
						if (serviceId != '55588338' && serviceId != '55588339' && serviceId != '55588340'){
							alert("用户已经存在该服务，不能重复订购");
							eventObj.checked = false;
							return;
						}
					}
					if(iskey){
						userPlatSvcsList.addKeyElement(serviceId);
					}else{
						userPlatSvcsList.addElement(serviceId);
					}
				}
			}
		},
		
		optionEnterAction:function(data){
			
			var serviceId = $("#Ul_Search_platSearch li[class=focus]").attr("SERVICE_ID");
			var serviceName = $("#Ul_Search_platSearch li[class=focus]").attr("SERVICE_NAME");
			$("#platSearch").val(serviceName);
			platOrderAdd.queryPlatSvc();
			
			//禁用搜索按钮
			var disableSearch = $("#searchBtn").attr("isdisable");
			if("true" == disableSearch)
			{
				return;
			}
			if (serviceId == '98001901' || serviceId == '80002009' || serviceId == '55687442'
				|| serviceId == '55627768'){
				MessageBox.alert("提示","此用户可办理相应福包业务，可通过产品变更界面进行办理。");
			}
			if(userPlatSvcsList.findElement(serviceId)){
				if (serviceId != '55588338' && serviceId != '55588339' && serviceId != '55588340'){
					MessageBox.alert("提示","用户已经存在该服务，不能重复订购");
					return;
				}
			}
			userPlatSvcsList.addElement(serviceId);
			$("#Div_Search_platSearch").css("visibility","hidden");
		},
		
		switchTabset:function(ptitle, title){
			if(title=="订购区"){
				return true;
			}
			else if(title=="全退订区"){
				var data="&TABSET=ALL_CANCEL_AREA";
				if(!this.isShowAllCancel){
					$.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),platOrderAdd.afterSwitchAllCancel);
				}
				return true;
			}
			else if(title=="DSMP开关区"){
			    if($("#USER_ID").val()=='')
			    {
			       return true;
			    }
			
				var data="&TABSET=SWITCH_AREA&USER_ID="+$("#USER_ID").val()+"&ROUTE_EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
				if(!this.isShowSwitch){
					$.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),platOrderAdd.afterSwitchSwitch);
				}
				return true;
			}
			else if(title=="重点业务办理区"){	
				var data = '&TABSET=KEYBUSINESS_AREA';	
				$.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),platOrderAdd.afterSwitchKeyBusiness);
				return true;
			}
		},
		
		afterSwitchSwitch:function(data){
			var area = $("#switchArea");
			area.empty();
			var html=[];
			if(data){
				var length = data.length;
				for(var i=0;i<length;i++){
					var map = data.get(i);
					html.push('<tr><td class="e_center"><input type="checkbox" isClose="'+map.get("IS_CLOSE")+'" serviceId="'+map.get("DATA_ID")+'" value="'+map.get("DATA_ID")+'" class="e_checkbox" onclick="userPlatSvcsList.addAllSwitch(this);"/></td>');
					html.push('<td>'+map.get("DATA_NAME")+'</td>');
					html.push('<td>'+(map.get("IS_CLOSE")=="false"?"开":"关")+'</td></tr>');
				}
				$.insertHtml('beforeend',area,html.join(""));
			}
			platOrderAdd.isShowSwitch = true;
		},
		
		afterSwitchAllCancel:function(data){
			var area = $("#allCancelArea");
			area.empty();
			var html=[];
			if(data){
				var length = data.length;
				for(var i=0;i<length;i++){
					var flag = false;
					var map = data.get(i);
					var spSelect = [];
					if(map.get("DATA_ID")=="SP"){
						var spMap = userPlatSvcsList.getAllCancelSpCode();
						if(spMap.length>0){
							spSelect.push('<span class="e_select"><span><span><select id="ALL_CANCEL_SP" disabled="true" onchange="userPlatSvcsList.changeSpAllCancel(this)">')
							spMap.eachKey(function(key){
								spSelect.push('<option value="'+key+'">'+spMap.get(key)+'</option>');
							});
							spSelect.push('</select></span></span></span>');
							flag = true;
						}
					}
					else if(map.get("DATA_ID")=="DSMP"||map.get("DATA_ID")=="MUSC"){
						if(userPlatSvcsList.isExist(map.get("DATA_ID"),"2")){
							flag = true;
						}
					}
					else if(userPlatSvcsList.isExist(map.get("DATA_ID"),"1")){
						flag = true;
					}
					if(flag){
						html.push('<tr><td class="e_center"><input type="checkbox" bizTypeCode="'+map.get("DATA_ID")+'" value="'+map.get("DATA_ID")+'" class="e_checkbox" onclick="userPlatSvcsList.addAllCancel(this);"/></td>');
						html.push('<td>'+map.get("DATA_NAME")+(spSelect.length>0?spSelect.join(""):"")+'</td></tr>');
					}
					
				}
				$.insertHtml('beforeend',area,html.join(""));
				platOrderAdd.isShowAllCancel = true;
			}
		},
		afterSwitchKeyBusiness: function(data){
			var area = $("#keyBusinessArea");
			area.empty();
			var html=[];
			if(data){
				var length = data.length;
				for(var i=0;i<length;i++){
					var map = data.get(i);
					html.push('<tr><td class="e_center"><input type="checkbox" serviceId="'+map.get("SERVICE_ID")+'" value="'+map.get("SERVICE_ID")+'" class="e_checkbox" onclick="platOrderAdd.checkBoxAction(this,true);"/></td>');
					html.push('<td>'+map.get("SERVICE_NAME")+'</td>');
					html.push('<td>'+map.get("BIZ_CODE")+'</td>');
					html.push('<td>'+map.get("SP_NAME")+'</td>');
					html.push('<td>'+map.get("SP_CODE")+'</td>');
					html.push('<td>'+map.get("BIZ_TYPE")+'</td>');
					html.push('<td>'+map.get("BILL_TYPE")+'</td>');
					html.push('<td>'+map.get("PRICE")/1000+'</td></tr>');
				}
				$.insertHtml('beforeend',area,html.join(""));
			}
			platOrderAdd.isShowkeybusiness = true;
		}
		
	});
})();

