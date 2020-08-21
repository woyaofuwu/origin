CKEDITOR.dialog.add("scaytDialog",function(s){var g=s.scayt,t='<p><img src="'+g.getLogo()+'" /></p><p>'+g.getLocal("version")+g.getVersion()+"</p><p>"+g.getLocal("text_copyrights")+"</p>",e=CKEDITOR.document,l={isChanged:function(){return null!==this.newLang&&this.currentLang!==this.newLang},currentLang:g.getLang(),newLang:null,reset:function(){this.currentLang=g.getLang(),this.newLang=null},id:"lang"};t=[{id:"options",label:g.getLocal("tab_options"),onShow:function(){},elements:[{type:"vbox",id:"scaytOptions",children:function(){var t,e=g.getApplicationConfig(),i=[],n={"ignore-all-caps-words":"label_allCaps","ignore-domain-names":"label_ignoreDomainNames","ignore-words-with-mixed-cases":"label_mixedCase","ignore-words-with-numbers":"label_mixedWithDigits"};for(t in e)(e={type:"checkbox"}).id=t,e.label=g.getLocal(n[t]),i.push(e);return i}(),onShow:function(){this.getChild();for(var t=s.scayt,e=0;e<this.getChild().length;e++)this.getChild()[e].setValue(t.getApplicationConfig()[this.getChild()[e].id])}}]},{id:"langs",label:g.getLocal("tab_languages"),elements:[{id:"leftLangColumn",type:"vbox",align:"left",widths:["100"],children:[{type:"html",id:"langBox",style:"overflow: hidden; white-space: normal;margin-bottom:15px;",html:'<div><div style="float:left;width:45%;margin-left:5px;" id="left-col-'+s.name+'"></div><div style="float:left;width:45%;margin-left:15px;" id="right-col-'+s.name+'"></div></div>',onShow:function(){var t=s.scayt.getLang();e.getById("scaytLang_"+s.name+"_"+t).$.checked=!0}},{type:"html",id:"graytLanguagesHint",html:'<div style="margin:5px auto; width:95%;white-space:normal;" id="'+s.name+'graytLanguagesHint"><span style="width:10px;height:10px;display: inline-block; background:#02b620;vertical-align:top;margin-top:2px;"></span> - This languages are supported by Grammar As You Type(GRAYT).</div>',onShow:function(){var t=e.getById(s.name+"graytLanguagesHint");s.config.grayt_autoStartup||(t.$.style.display="none")}}]}]},{id:"dictionaries",label:g.getLocal("tab_dictionaries"),elements:[{type:"vbox",id:"rightCol_col__left",children:[{type:"html",id:"dictionaryNote",html:""},{type:"text",id:"dictionaryName",label:g.getLocal("label_fieldNameDic")||"Dictionary name",onShow:function(t){var e=t.sender,i=s.scayt;setTimeout(function(){e.getContentElement("dictionaries","dictionaryNote").getElement().setText(""),null!=i.getUserDictionaryName()&&""!=i.getUserDictionaryName()&&e.getContentElement("dictionaries","dictionaryName").setValue(i.getUserDictionaryName())},0)}},{type:"hbox",id:"notExistDic",align:"left",style:"width:auto;",widths:["50%","50%"],children:[{type:"button",id:"createDic",label:g.getLocal("btn_createDic"),title:g.getLocal("btn_createDic"),onClick:function(){var e=this.getDialog(),i=o,t=s.scayt,n=e.getContentElement("dictionaries","dictionaryName").getValue();t.createUserDictionary(n,function(t){t.error||i.toggleDictionaryButtons.call(e,!0),t.dialog=e,t.command="create",t.name=n,s.fire("scaytUserDictionaryAction",t)},function(t){t.dialog=e,t.command="create",t.name=n,s.fire("scaytUserDictionaryActionError",t)})}},{type:"button",id:"restoreDic",label:g.getLocal("btn_restoreDic"),title:g.getLocal("btn_restoreDic"),onClick:function(){var e=this.getDialog(),t=s.scayt,i=o,n=e.getContentElement("dictionaries","dictionaryName").getValue();t.restoreUserDictionary(n,function(t){t.dialog=e,t.error||i.toggleDictionaryButtons.call(e,!0),t.command="restore",t.name=n,s.fire("scaytUserDictionaryAction",t)},function(t){t.dialog=e,t.command="restore",t.name=n,s.fire("scaytUserDictionaryActionError",t)})}}]},{type:"hbox",id:"existDic",align:"left",style:"width:auto;",widths:["50%","50%"],children:[{type:"button",id:"removeDic",label:g.getLocal("btn_deleteDic"),title:g.getLocal("btn_deleteDic"),onClick:function(){var e=this.getDialog(),t=s.scayt,i=o,n=e.getContentElement("dictionaries","dictionaryName"),a=n.getValue();t.removeUserDictionary(a,function(t){n.setValue(""),t.error||i.toggleDictionaryButtons.call(e,!1),t.dialog=e,t.command="remove",t.name=a,s.fire("scaytUserDictionaryAction",t)},function(t){t.dialog=e,t.command="remove",t.name=a,s.fire("scaytUserDictionaryActionError",t)})}},{type:"button",id:"renameDic",label:g.getLocal("btn_renameDic"),title:g.getLocal("btn_renameDic"),onClick:function(){var e=this.getDialog(),t=s.scayt,i=e.getContentElement("dictionaries","dictionaryName").getValue();t.renameUserDictionary(i,function(t){t.dialog=e,t.command="rename",t.name=i,s.fire("scaytUserDictionaryAction",t)},function(t){t.dialog=e,t.command="rename",t.name=i,s.fire("scaytUserDictionaryActionError",t)})}}]},{type:"html",id:"dicInfo",html:'<div id="dic_info_editor1" style="margin:5px auto; width:95%;white-space:normal;">'+g.getLocal("text_descriptionDic")+"</div>"}]}]},{id:"about",label:g.getLocal("tab_about"),elements:[{type:"html",id:"about",style:"margin: 5px 5px;",html:'<div><div id="scayt_about_">'+t+"</div></div>"}]}];s.on("scaytUserDictionaryAction",function(t){var e,i=SCAYT.prototype.UILib,n=t.data.dialog,a=n.getContentElement("dictionaries","dictionaryNote").getElement(),o=t.editor.scayt;void 0===t.data.error?(e=(e=o.getLocal("message_success_"+t.data.command+"Dic")).replace("%s",t.data.name),a.setText(e),i.css(a.$,{color:"blue"})):(""===t.data.name?a.setText(o.getLocal("message_info_emptyDic")):(e=(e=o.getLocal("message_error_"+t.data.command+"Dic")).replace("%s",t.data.name),a.setText(e)),i.css(a.$,{color:"red"}),null!=o.getUserDictionaryName()&&""!=o.getUserDictionaryName()?n.getContentElement("dictionaries","dictionaryName").setValue(o.getUserDictionaryName()):n.getContentElement("dictionaries","dictionaryName").setValue(""))}),s.on("scaytUserDictionaryActionError",function(t){var e,i=SCAYT.prototype.UILib,n=t.data.dialog,a=n.getContentElement("dictionaries","dictionaryNote").getElement(),o=t.editor.scayt;""===t.data.name?a.setText(o.getLocal("message_info_emptyDic")):(e=(e=o.getLocal("message_error_"+t.data.command+"Dic")).replace("%s",t.data.name),a.setText(e)),i.css(a.$,{color:"red"}),null!=o.getUserDictionaryName()&&""!=o.getUserDictionaryName()?n.getContentElement("dictionaries","dictionaryName").setValue(o.getUserDictionaryName()):n.getContentElement("dictionaries","dictionaryName").setValue("")});var o={title:g.getLocal("text_title"),resizable:CKEDITOR.DIALOG_RESIZE_BOTH,minWidth:340,minHeight:260,onLoad:function(){if(0!=s.config.scayt_uiTabs[1]){var t=o,e=t.getLangBoxes.call(this);e.getParent().setStyle("white-space","normal"),t.renderLangList(e),this.definition.minWidth=this.getSize().width,this.resize(this.definition.minWidth,this.definition.minHeight)}},onCancel:function(){l.reset()},onHide:function(){s.unlockSelection()},onShow:function(){if(s.fire("scaytDialogShown",this),0!=s.config.scayt_uiTabs[2]){var t=s.scayt,e=this.getContentElement("dictionaries","dictionaryName"),i=this.getContentElement("dictionaries","existDic").getElement().getParent(),n=this.getContentElement("dictionaries","notExistDic").getElement().getParent();i.hide(),n.hide(),null!=t.getUserDictionaryName()&&""!=t.getUserDictionaryName()?(this.getContentElement("dictionaries","dictionaryName").setValue(t.getUserDictionaryName()),i.show()):(e.setValue(""),n.show())}},onOk:function(){var t=o,e=s.scayt;this.getContentElement("options","scaytOptions"),t=t.getChangedOption.call(this),e.commitOption({changedOptions:t})},toggleDictionaryButtons:function(t){var e=this.getContentElement("dictionaries","existDic").getElement().getParent(),i=this.getContentElement("dictionaries","notExistDic").getElement().getParent();t?(e.show(),i.hide()):(e.hide(),i.show())},getChangedOption:function(){var t={};if(1==s.config.scayt_uiTabs[0])for(var e=this.getContentElement("options","scaytOptions").getChild(),i=0;i<e.length;i++)e[i].isChanged()&&(t[e[i].id]=e[i].getValue());return l.isChanged()&&(t[l.id]=s.config.scayt_sLang=l.currentLang=l.newLang),t},buildRadioInputs:function(t,e,i){var n=new CKEDITOR.dom.element("div"),a="scaytLang_"+s.name+"_"+e,o=CKEDITOR.dom.element.createFromHtml('<input id="'+a+'" type="radio"  value="'+e+'" name="scayt_lang" />'),r=new CKEDITOR.dom.element("label"),c=s.scayt;return n.setStyles({"white-space":"normal",position:"relative","padding-bottom":"2px"}),o.on("click",function(t){l.newLang=t.sender.getValue()}),r.appendText(t),r.setAttribute("for",a),i&&s.config.grayt_autoStartup&&r.setStyles({color:"#02b620"}),n.append(o),n.append(r),e===c.getLang()&&(o.setAttribute("checked",!0),o.setAttribute("defaultChecked","defaultChecked")),n},renderLangList:function(t){var e=t.find("#left-col-"+s.name).getItem(0);t=t.find("#right-col-"+s.name).getItem(0);var i,n=g.getScaytLangList(),a=g.getGraytLangList(),o={},r=[],c=0,l=!1;for(i in n.ltr)o[i]=n.ltr[i];for(i in n.rtl)o[i]=n.rtl[i];for(i in o)r.push([i,o[i]]);for(r.sort(function(t,e){var i=0;return t[1]>e[1]?i=1:t[1]<e[1]&&(i=-1),i}),o={},l=0;l<r.length;l++)o[r[l][0]]=r[l][1];for(i in r=Math.round(r.length/2),o)c++,l=i in a.ltr||i in a.rtl,this.buildRadioInputs(o[i],i,l).appendTo(c<=r?e:t)},getLangBoxes:function(){return this.getContentElement("langs","langBox").getElement()},contents:function(t,e){var i=[],n=s.config.scayt_uiTabs;if(!n)return t;for(var a in n)1==n[a]&&i.push(t[a]);return i.push(t[t.length-1]),i}(t)};return o});