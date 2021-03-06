(function(){
    $.extend(OfferList.prototype,{
        renderComponent : function(productId, routeEparchyCode, param){
            this.init();
            if(param==null||param=="undefined"){
                param="";
            }
            if(productId){
                this.mainProductId = productId;
                $("#OFFERLIST_PRODUCT_ID").val(productId);
                productEnv.eparchyCode = routeEparchyCode;
                param+="&PRODUCT_ID="+productId+"&EPARCHY_CODE="+routeEparchyCode;
                if(typeof(this.getOtherParam)=="function"){
                    param += this.getOtherParam();
                }
                var obj = this;
                $.ajax.submit(null,null,param,$("#OFFERLIST_COMPONENT_ID").val(), function(data){obj.afterRender(productId, routeEparchyCode);});
            }
            else{
                $.ajax.submit(null, null, param, $("#OFFERLIST_COMPONENT_ID").val());
            }
        },

        afterRender : function(productId, eparchyCode){
            offerSuggest.setParam("PRODUCT_ID", productId);
            offerSuggest.setParam("EPARCHY_CODE", eparchyCode);
            offerSuggest.setParam("SEARCH_TYPE", "2");
            $("#offerSuggest").change(function (e, text, val) {
                var jsonObj = $.parseJSON(val.replace(/\'/g, '"'));
                offerList.searchElementOptionEnter(jsonObj);
            });
        },

        searchElementOptionEnter: function (jsonObj) {
            // 元素搜索
            var productId = jsonObj.PRODUCT_ID;
            var packageId = jsonObj.PACKAGE_ID;
            var elementId = jsonObj.ELEMENT_ID;
            var elementTypeCode = jsonObj.ELEMENT_TYPE_CODE;
            var elementName = jsonObj.ELEMENT_NAME;
            var forceTag = jsonObj.FORCE_TAG;
            var reOrder = jsonObj.REORDER;

            if (reOrder !== "R" && reOrder !== "C" && selectedElements.checkIsExist(elementId, elementTypeCode)) {
                MessageBox.alert("您所选择的元素" + elementName + "已经存在于已选区，不能重复添加！");
                return false;
            }
            var elementIds = $.DatasetList();
            var selected = $.DataMap();
            selected.put("PRODUCT_ID", productId);
            selected.put("PACKAGE_ID", packageId);
            selected.put("ELEMENT_ID", elementId);
            selected.put("ELEMENT_TYPE_CODE", elementTypeCode);
            selected.put("MODIFY_TAG", "0");
            selected.put("ELEMENT_NAME", elementName);
            selected.put("FORCE_TAG", forceTag);
            selected.put("REORDER", reOrder);
            elementIds.add(selected);
            if (selectedElements.addElements) {
                selectedElements.addElements(elementIds);
            }
        },

        initOfferList : function(data){
            this.init();
            if(!data){
                return;
            }
            var dataset = null;
            if(typeof(data)=="string"){
                dataset = new $.DatasetList(data);
            }
            else if(typeof(data)=="object"){
                dataset = data;
            }
            else{
                MessageBox.alert("数据格式错误");
                $.endPageLoading();
                return;
            }
            var obj = this;
            this.afterRender(dataset, obj);
        },

        refreshCategory : function(data, obj){
            var categorys = [];
            data.each(function(item,index,totalCount){
                var category = new Category();
                category.categoryId = item.get("CATEGORY_ID");
                category.categoryName = item.get("CATEGORY_NAME");
                var offerList = item.get("OFFER_LIST");
                for(var i=0;i<offerList.length;i++){
                    var offer = new Offer();
                    var temp = offerList.get(i);
                    offer.elementId = temp.get("OFFER_CODE");
                    offer.elementName = temp.get("OFFER_NAME");
                    offer.elementType = temp.get("OFFER_TYPE");
                    offer.selectFlag = temp.get("SELECT_FLAG");
                    offer.reOrder = temp.get("ORDER_MODE");
                    offer.explain = temp.get("DESCRIPTION");
                    offer.groupId = temp.get("GROUP_ID");
                    category.offerList.push(offer);
                }
                categorys.push(category);
            });
            obj.drawCategory(categorys);
            return categorys;
        },

        drawCategory: function(categorys){
            var drawArea = $("#OFFERLIST_DRAWAREA");
            drawArea.empty();
            if(categorys.length <= 0){
                return;
            }

            for(var i=0;i<categorys.length;i++){
                var category = categorys[i];
                category.draw();
            }
        },

        getElement : function(groupId, elementId, elementType){
            var categorys = this.categoryMap.get(groupId);
            if(categorys && categorys.length >0){
                for(var i=0;i<categorys.length;i++){
                    var category = categorys[i];
                    var offers = category.offerList;
                    if(offers && offers.length > 0){
                        for(var j=0;j<offers.length;j++){
                            var offer = offers[j];
                            if(offer.elementId == elementId && offer.elementType == elementType){
                                return offer;
                            }
                        }
                    }
                }
            }

            return this.getLableElement(groupId, elementId, elementType);
        },

        getLableElement : function(groupId, elementId, elementType){
            var offerEle = null;
            if(this.categoryLabelMap && this.categoryLabelMap.length >0)
            {
                var lebelMap = this.categoryLabelMap;
                lebelMap.eachKey(
                    function(key)
                    {
                        var categorys = lebelMap.get(key);
                        if(categorys && categorys.length >0){
                            for(var i=0;i<categorys.length;i++){
                                var category = categorys[i];
                                var offers = category.offerList;
                                if(offers && offers.length > 0){
                                    for(var j=0;j<offers.length;j++){
                                        var offer = offers[j];
                                        if(offer.groupId == groupId && offer.elementId == elementId && offer.elementType == elementType){
                                            offerEle = offer;
                                            return ;
                                        }
                                    }
                                }
                            }
                        }
                    }
                );
            }

            return offerEle;
        },

        addElement : function(elementId, elementType,groupId){
        	if (elementId == "99012000") {// 物联网语音服务 99012000
			   var serialNum = $("#SERIAL_NUMBER").val();
			   if (serialNum != null && serialNum.length >= 3&& serialNum.substr(0, 3) == "147") {
				  var pspttypecode = $("#PSPT_TYPE_CODE").val();// 客户证件类型
			      if (pspttypecode != "0" && pspttypecode != "1"&& pspttypecode != "A") {
					alert("使用物联网语音服务，开户证件类型必须为个人证件：\n本地身份证或外地身份证或护照！");
					return false;
				  }
			   }
			} 
        	
            var elementIds = $.DatasetList();
            var selected = $.DataMap();
            selected.put("PRODUCT_ID",$("#OFFERLIST_PRODUCT_ID").val());
            
            var currPackageId = groupId;
            if(!groupId || groupId==null || groupId=="undefined")
                currPackageId = this.currentGroup;
            
            selected.put("PACKAGE_ID",currPackageId);
            selected.put("ELEMENT_ID",elementId);
            selected.put("ELEMENT_TYPE_CODE",elementType)
            selected.put("MODIFY_TAG","0");
            elementIds.add(selected);
            if(selectedElements.addElements){
                selectedElements.addElements(elementIds);
            }
        },

        addElements: function(){
            var els = $("input[name=elementCheckBox]");
            this.selectedList = new $.DatasetList();
            var unSelectedName = "";
            var pspttypecode  = $("#PSPT_TYPE_CODE").val();//客户证件类型 
            for(var i=0;i<els.length;i++){
                var el = els[i];
                if(el.checked){
                    var tempEl = this.getElement(this.currentGroup,el.value, el.getAttribute("elementType"));
                    
                    if(tempEl.elementId=="99012000"){//物联网语音服务 99012000
                           var serialNum  = $("#SERIAL_NUMBER").val();  
                           if(serialNum!=null&&serialNum.length>=3&&serialNum.substr(0,3)=="147"){
                               if(pspttypecode!="0"&&pspttypecode!="1"&&pspttypecode!="A"){
                                      alert("使用物联网语音服务，开户证件类型必须为个人证件：\n本地身份证或外地身份证或护照！");
                                      return false;
                               }
                           }
                     }
                    
                    
                    if(tempEl.reOrder!="R"&&tempEl.reOrder!="C"&&selectedElements.checkIsExist(tempEl.elementId,tempEl.elementType)){
                        unSelectedName+=tempEl.elementName+",";
                        el.checked = false;
                        continue;
                    }
                    this.selectedList.add(tempEl);
                }
            }
            if(unSelectedName!=""){
                MessageBox.alert("您所选择的元素"+unSelectedName+"已经存在于已选区，不能重复添加");
            }
            if(this.selectedList.length<=0){
                MessageBox.alert('您没有选择任何元素');
                return;
            }
            if(this.checkSelf){
                if(!this.checkSelf(this.selectedList)){
                    return;
                }
            }
            var elementIds = $.DatasetList();
            for(var i=0;i<this.selectedList.length;i++){
                var selected = $.DataMap();
                selected.put("PRODUCT_ID",$("#OFFERLIST_PRODUCT_ID").val());
                var currPackageId = this.selectedList.get(i).groupId;
                if(!currPackageId || currPackageId==null || currPackageId=="undefined")
                    currPackageId = this.currentGroup;
                selected.put("PACKAGE_ID",currPackageId);
                selected.put("ELEMENT_ID",this.selectedList.get(i).elementId);
                selected.put("ELEMENT_TYPE_CODE",this.selectedList.get(i).elementType)
                selected.put("MODIFY_TAG","0");
                elementIds.add(selected);
            }
            if(selectedElements.addElements){
                selectedElements.addElements(elementIds);
            }
        },

        switchLabel : function(obj){
            this.switchLabelClass(obj);
            var labelKeyId = this.getSwitchLabelKeyIds(obj);
            if(labelKeyId == this.currentlabelKeyId){
                return;
            }

            this.currentlabelKeyId = labelKeyId;
            this.currentGroup = '';
            this.cleanGroupSelected();
            var param = "&LABEL_KEY_ID="+labelKeyId;
            // param = param+"&CATEGORY_ID="+$("#CATEGORY_ID").val();
            if(typeof(getOtherParam)=="function")
            {
                param += getOtherParam();
            }
            if(param.indexOf('&PRODUCT_ID=') < 0){
                param += "&PRODUCT_ID="+$("#OFFERLIST_PRODUCT_ID").val();
            }
            var object = this;
            hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.offer.OfferListHandler","switchLable", param, function(data){object.afterSwitchLable(data, object, labelKeyId);});

        },
        selectedGroupAction : function(groupId)
        {
            var packages = $("#packages").children();
            var size = packages.length;
            for(var i=0;i<size;i++){
                var thePackage = $(packages[i]);
                if(thePackage.attr("groupId")==groupId){
                    offerList.switchGroup(thePackage);
                    break;
                }
            }
        },
        getSwitchLabelKeyIds : function(obj){
            var labelKeyIds = '';
            var labelParentRoot = obj.parent().parent().parent().parent();
            var labelRoot = labelParentRoot.children();

            //第一个子节点是group的信息，从第二个节点开始循环
            for(var j=1;j<labelRoot.length;j++){

                var childrens = $($($(labelRoot[j]).children()[1]).children()[0]).children();
                for(var i=0;i<childrens.length;i++){
                    var child = $(childrens[i]);
                    var tempLabelKeyId = child.attr("labelKeyId");
                    var childClass = child.attr("class");
                    if(childClass == "on" && tempLabelKeyId != '-1'){
                        if(labelKeyIds == '')
                            labelKeyIds = tempLabelKeyId;
                        else
                            labelKeyIds = labelKeyIds+","+tempLabelKeyId;
                    }
                }
            }
            return labelKeyIds;
        },

        cleanLabelSelected : function(obj){

            var labelParentRoot = $('#labelPart');
            if(!labelParentRoot && labelParentRoot.length!=0)
                return;
            var labelRoot = labelParentRoot.children();

            for(var j=1;j<labelRoot.length;j++){

                var childrens = $($($(labelRoot[j]).children()[1]).children()[0]).children();
                for(var i=0;i<childrens.length;i++){
                    var child = $(childrens[i]);
                    var childClass = child.attr("class","");

                }
            }
        },

        afterSwitchLable : function(data, obj, labelKeyId){
            if(!data){
                return;
            }
            var categorys = obj.refreshCategory(data, obj);

            obj.categoryLabelMap.put(labelKeyId, categorys);
        },

        switchLabelClass : function(obj){
            var parent = obj.parent();
            var children = parent.children();
            var labelKeyId = obj.attr("labelKeyId");
            for(var i=0;i<children.length;i++){
                var child = $(children[i]);
                var tempLabelKeyId = child.attr("labelKeyId");
                if(tempLabelKeyId == labelKeyId){
                    child.attr("class","on");
                }
                else{
                    child.attr("class","");
                }
            }
        },

        switchGroup : function(obj){
            var groupId = obj.attr("groupId");
            if(groupId == this.currentGroup){
                return;
            }

            this.switchGroupClass(obj);
            this.currentGroup = groupId;
            this.currentlabelKeyId = '';
            this.cleanLabelSelected();
            var categorys = this.categoryMap.get(groupId);
            if(categorys){
                this.drawCategory(categorys);
            }
            else{
                var param = "&GROUP_ID="+groupId+"&GROUP_NAME="+obj.attr("groupName")+"&JOIN_SERVICE_NAME="+$("#SWITCH_JOIN_SERVICE").val()+"&SERVICE_NAME="+$("#SWITCH_GROUP_SERVICE").val()+"&CATEGORY_ID="+$("#CATEGORY_ID").val();
                if(typeof(getOtherParam)=="function")
                {
                    param += getOtherParam();
                }
                if(param.indexOf('&PRODUCT_ID=') < 0){
                    param += "&PRODUCT_ID="+$("#OFFERLIST_PRODUCT_ID").val();
                }
                var object = this;
                hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.offer.OfferListHandler","switchGroup", param, function(data){object.afterSwitchGroup(data, object, groupId);});
            }
        },

        cleanGroupSelected : function(obj){

            var groupRoot = $('#packages');
            if(!groupRoot && groupRoot.length!=0 )
                return;
            var childrens = groupRoot.children();
            for(var i=0;i<childrens.length;i++){
                var child = $(childrens[i]);
                var childClass = child.attr("class","");

            }

        },

        afterSwitchGroup : function(data, obj, groupId){
            if(!data){
                return;
            }
            var categorys = obj.refreshCategory(data, obj);
            obj.categoryMap.put(groupId, categorys);
        },

        switchGroupClass : function(obj){
            var parent = obj.parent();
            var children = parent.children();
            var groupId = obj.attr("groupId");
            for(var i=0;i<children.length;i++){
                var child = $(children[i]);
                var tempGroupId = child.attr("groupId");
                if(tempGroupId == groupId){
                    child.attr("class","on");
                }
                else{
                    child.attr("class","");
                }
            }
        },

        init : function(){
            this.categoryMap = new $.DataMap();
            this.categoryLabelMap = new $.DataMap();
            this.currentGroup = -1;
        }
    });
})();

(function(){
    $.extend(Category.prototype,{
        draw: function(){
            if(this.offerList.length <= 0){
                return;
            }
            var html=[];
            var drawArea = $("#OFFERLIST_DRAWAREA");
            //var colNum = $("#COL_NUM").val();
            var colNum = $.os.phone ? 1 : $("#COL_NUM").val();
            html.push("<div class='c_list c_list-narrow c_list-hideSide c_list-s c_list-col-"+colNum+"'><ul>")

            for(var i=0;i<this.offerList.length;i++){
                var className=null;
                var checked = false;
                var disabled = false;
                var offer = this.offerList[i];
                if(offer.selectFlag=='0'){
                    checked=true;
                    disabled= true;
                }
                else if(offer.selectFlag=='1'){
                    checked=true;
                }

                if(offer.reOrder!="R"&&offer.reOrder!="C"&&selectedElements.checkIsExist(offer.elementId,offer.elementType)){
                    className="e_dis";
                    checked=false;
                    disabled=true;
                }
                var addButton = '';
                // if(className != "e_dis" || checked == true){
                //     var addButton = 'onmouseover="$(this).prev().css(\'display\',\'\');" onmouseout="$(this).prev().css(\'display\',\'none\');"';
                // }
                var title = "";
                if(offer.elementType=="S" || offer.elementType == "Z"){
                    title = offer.elementName;
                }
                else{
                    title = offer.elementName+"&#13;&#13;"+offer.explain;
                }
                html.push('<li title="'+title+'"'+(className!=null?(' class="'+className+'"'):'')+' id="PELI_'+offer.elementType+"_"+offer.elementId+'">');



                html.push('<label style="width:100%;" id="LABEL_'+offer.elementType+"_"+offer.elementId+'" class="group link" '+ ((className!="e_dis" || checked == true)?addButton:'')+'>');
                html.push('<div class="content">');
                html.push('<div class="fn">');
                html.push('<input name="elementCheckBox" type="checkbox"'+(disabled==true?' disabled="true"':'')+(checked==true?' checked':'')+' id="PE_'+offer.elementType+"_"+offer.elementId+'" value='+offer.elementId+' elementType="'+offer.elementType+'" class="e_checkbox"/>');
                html.push('</div>');
                html.push('<div class="main">');
                html.push('['+offer.elementId+']'+offer.elementName);
                html.push('</div>');
                html.push('</div>');
                html.push('</label>');
                if(className!="e_dis" || checked == true ){
                    html.push('<div class="side side-float e_hide-phone">');
                    html.push('<button type="button" class="e_button-r"  onmouseover="$(this).find(\'button\').removeClass(\'e_hide\');"  onmouseout="$(this).find(\'button\').style.display=\'none\';" onclick="offerList.addElement(\''+offer.elementId+'\',\''+offer.elementType+'\',\''+offer.groupId+'\');"><span class="e_ico-next"></span></button>')
                    html.push('</div>');
                }
                html.push('</li>');
            }
            html.push("</ul></div>");
            $.insertHtml('beforeend',drawArea,html.join(""));
        }
    })
})();
