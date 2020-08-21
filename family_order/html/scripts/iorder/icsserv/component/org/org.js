(function($){
	if($.org == undefined){
		var grantAreaTreeInited=false,currentAreaTree="areaTree";
		var grantDeptTreeInited=false,currentDeptTree="deptTree";
		$.org={
			areaTreeSwitchAction:function(ptitle,title){
				if("归属区域" == title){
					currentAreaTree="areaTree";
				}else if("授权区域" == title){
					currentAreaTree="grantAreaTree";
					if(window.grantAreaTree && !grantAreaTreeInited){
						$.each($.ajax.buildJsonData("treeParamArea"),function(key,val){
							grantAreaTree.setParam(key,val);
						});
						grantAreaTree.setParam("loadflag","grant");
						grantAreaTree.init();
						grantAreaTreeInited=true;
					}
				} 
				return true;
			},			
			loadAreaTree:function(){
				$.each($.ajax.buildJsonData("treeParamArea"),function(key,val){
					areaTree.setParam(key,val);
				});
				$.each($.ajax.buildJsonData("areaTreeParamArea"),function(key,val){
					areaTree.setParam(key,val);
				});
				areaTree.init();
			},
			setAreaPopupReturnValue:function(){
				var field_name=$("#field_name").val();
				var popFieldName="POP_" + field_name;

				var selAreaIds=[],selAreaNames=[];
	 			var checkedNodes = window[currentAreaTree].getCheckedNodeIds(true);
				if(checkedNodes && checkedNodes.length){
					$.each(checkedNodes,function(idx,nodeid){
						var data = window[currentAreaTree]._getNodeDataByNodeId(nodeid);
						selAreaIds.push(data.id);
						selAreaNames.push(data.text);
					});
				}
				var retObj={};
				retObj[field_name]=selAreaIds.join(",");
				retObj[popFieldName]=selAreaNames.join(",");
				backPopup(this,retObj);
			},
			deptTreeSwitchAction:function(ptitle,title){
				if("归属部门" == title){
					currentDeptTree="deptTree";
				}else if("授权部门" == title){
					currentDeptTree="grantDeptTree";
					if(window.grantDeptTree && !grantDeptTreeInited){
						$.each($.ajax.buildJsonData("treeParamArea"),function(key,val){
							grantDeptTree.setParam(key,val);
						});
						grantDeptTree.setParam("loadflag","grant");
						grantDeptTree.init();
						grantDeptTreeInited=true;
					}
				} 
				return true;
			},
			loadDeptTree:function(){
				$.each($.ajax.buildJsonData("treeParamArea"),function(key,val){
					deptTree.setParam(key,val);
				});
				$.each($.ajax.buildJsonData("deptTreeParamArea"),function(key,val){
					deptTree.setParam(key,val);
				});
				deptTree.init();		
			},
			setDeptPopupReturnValue:function(){
				var field_name=$("#field_name").val();
				var popFieldName="POP_" + field_name;

				var selDeptIds=[],selDeptNames=[];
	 			var checkedNodes = window[currentDeptTree].getCheckedNodeIds(true);
				if(checkedNodes && checkedNodes.length){
					$.each(checkedNodes,function(idx,nodeid){
						var data = window[currentDeptTree]._getNodeDataByNodeId(nodeid);
						selDeptIds.push(data.id);
						selDeptNames.push(data.text);
					});
				}
				var retObj={};
				retObj[field_name]=selDeptIds.join(",");
				retObj[popFieldName]=selDeptNames.join(",");
				backPopup(this,retObj);
			},
			queryStaffs:function(){
				if($.validate.verifyAll("QueryCond")){
					$.beginPageLoading("正在加载员工数据..");
					$.ajax.submit("QueryCond","queryStaffs","","staffsPart",function(){
						$.endPageLoading();
					},function(code,info){
						$.endPageLoading();
						alert("加载员工数据出错\r\n" + info);
					});
				}
			},
			queryStaffsSimp:function(){				
				var value = $.trim($('#STAFF_SEARCH_TEXT').val());
				if(value=="") return;
				var param = "&cond_STAFF_ID_NAME="+value;
				$.beginPageLoading("正在加载员工数据..");
				$.ajax.submit("","queryStaffsSimp",param,"staffsPart",function(){
					$.endPageLoading();
				},function(code,info){
					$.endPageLoading();
					alert("加载员工数据出错\r\n" + info);
				});
			},
			fillStaffsParam:function(data){
				$("#cond_AREA_CODE").val(data.get("cond_AREA_CODE"));
				$("#cond_DEPART_ID").val(data.get("cond_DEPART_ID"));
				$("#cond_STAFF_NAME").val(data.get("cond_STAFF_NAME"));
				$("#cond_SEX").val(data.get("cond_SEX"));
				$("#cond_SERIAL_NUMBER").val(data.get("cond_SERIAL_NUMBER"));
				$("#cond_STAFF_ID").val(data.get("cond_STAFF_ID"));
				$.org.queryStaffs();
			},
			setStaffPopupReturnValue:function(staffId,staffName,serialNumber){
				
				var field_name=$("#field_name").val();
				var popFieldName="POP_" + field_name;
				var retObj={};
				//单个
				if(staffId && staffName){
					retObj[field_name]=staffId;
					retObj[popFieldName]=staffName;
					if(serialNumber){
						retObj[field_name+"_SN"]=serialNumber;
					}else{
						retObj[field_name+"_SN"]="";
					}
				}else if("true"==$("#multi").val()){//多个
					var selStaffIds=[];
					var selStaffNames=[];
					$("#staffsPart input[name=staffs]").each(function(){
						if(this.checked){
							var data=this.value.split(",");
							selStaffIds.push(data[0]);
							selStaffNames.push(data[1]);
						}
					});
					retObj[field_name]=selStaffIds.join(",");
					retObj[popFieldName]=selStaffNames.join(",");
				}
				setPopupReturnValue(this,retObj);
				if(typeof(parent.$.developStaff) != "undefined")
				{
					parent.$.developStaff.setDevelopStaff();
				}
			}
		};
	}
})(Wade);