
    <div class="c_box c_box-border">
    	<div class="c_title">
    		<div class="text">{{title}}</div>
    		<div class="fn" name="titleFn">
    					<%- template.render(btnsArt, {btnStrs:btnStrs,isLtIE9:isLtIE9}) %>
    		</div>
    	</div>

    	<div class="l_padding-2 l_padding-u">
        		<div class="c_form c_form-col-2 c_form-label-9">
        			<ul class="ul">
        				<li class="required">
                            <div class="label">实名认证： </div>
                                <div class="value">
                                <span class="e_mix">
                                    <input type="text" name="AUTH_FLOW" id="AUTH_FLOW_{{uniqueId}}" value="未认证" disabled="true" desc="实名认证"/>
                                    <button type="button" class="e_button-blue" name="AUTH_CHECK"><span class="e_ico-select"></span><span>证件信息采集</span></button>
                                </span>
                            </div>
        				</li>
        				<li class="required">
                            <div class="label">证件类型：</div>
                                <div class="value">
                                    <input type="text" name="pspt_type" id="pspt_type_{{uniqueId}}" value="" disabled="true" desc="证件类型"/>
                            </div>
        				</li>
        				<li class="required">
                            <div class="label">证件号码：</div>
                                <div class="value">
                                    <input type="text" name="ID_CARD" id="ID_CARD_{{uniqueId}}" value="" disabled="true" desc="证件号码"/>
                            </div>
                        </li>
                         <li class="required">
                            <div class="label">客户姓名：</div>
                                <div class="value">
                                    <input type="text" name="cust_name" id="cust_name_{{uniqueId}}" value="" disabled="true" desc="客户姓名"/>
                            </div>
                        </li>
                         <li class="required">
                            <div class="label">固话号码：</div>
                                <div class="value">
                                    <span class="e_mix">
                                        <input type="text" name="FIX_NUMBER" id="FIX_NUMBER_{{uniqueId}}" value="{{imsSn}}" datatype="numeric" desc="固话号码" maxLength="8"/>
                                        <button type="button" class="e_button-orange" name="CHECK_BTN"><span class="e_ico-check"></span><span>校验</span></button>
                                    </span>
                            </div>
                        </li>

                       <li class="required">
                            <div class="label">商品：</div>
                            <div class="value">
                                <span class="e_group">
                                    <span class="e_groupRight">
                                        <span class="e_ico-browse e_blue" tip="选择" name="selectOffer"></span>
                                    </span> <span class="e_groupMain">
                                    <input type="text" name="IMS_OFFER_NAME" id="IMS_OFFER_NAME_{{uniqueId}}" nullable="no"  disabled="true" desc="商品名称"/>
                                    </span>
                                </span>
                            </div>
                       </li>
                       <li class="link" name="RES_ID_CHECK" id="RES_ID_CHECK_{{uniqueId}}" style="display:none;">
                            <div class="label">输入音箱串号：</div>
                            <div class="value">
                                <span class="e_mix">
                                    <input type="text" name="RES_ID" id="RES_ID_{{uniqueId}}" value="" datatype="numeric" maxLength="11"
                                            desc="音箱串号" />
                                </span>
                            </div>
                        </li>
        			</ul>
    	        </div>
    	</div>
    	<div partId="SelectedOffers" name="SelectedOffers"></div>
        <div partId="hiddenPartIms">
            <input type="hidden" name="ROLE_TYPE" value="{{type}}"/>
            <input type="hidden" name="OLD_FIX_NUMBER" id="OLD_FIX_NUMBER_{{uniqueId}}" value="" desc="校验通过后的固话号码"/>
            <input type="hidden" name="TT_TRANSFER" id="TT_TRANSFER_{{uniqueId}}" value="0" desc="是否铁通固话迁移"/>
            <input type="hidden" name="OLD_RES_ID" id="OLD_RES_ID_{{uniqueId}}" value="" desc="校验通过后的音箱串号"/>
            <input type="hidden" name="IMS_PRODUCT_ID" id="IMS_PRODUCT_ID_{{uniqueId}}" value=""/>
            <input type="hidden" name="IMS_SELECTED_ELEMENTS" id="IMS_SELECTED_ELEMENTS_{{uniqueId}}"  value=""/>
            <input type="hidden" name="IMS_INDEX" id="IMS_INDEX_{{uniqueId}}"  value=""/>
            <input type="hidden" name="NEW_OR_OLD"  id="NEW_OR_OLD_{{uniqueId}}" value="{{type}}"/>
            <input type="hidden" name="MOBILE_PRODUCT_ID"  id="MOBILE_PRODUCT_ID_{{uniqueId}}" value="" desc="手机主产品ID"/>


            <input type="hidden" name="READ_CARD_PZ_VALUE" id="READ_CARD_PZ_VALUE_{{uniqueId}}" value="0" maxsize="10" desc="拍照标记值"/>
            <input type="hidden" name="PIC_ID" id="PIC_ID_{{uniqueId}}" value="" desc="客户照片ID"/>
            <input type="hidden" name="PIC_STREAM" id="PIC_STREAM_{{uniqueId}}" value="" desc="客户人像照片流"/>
            <input type="hidden" name="AGENT_PIC_ID" id="AGENT_PIC_ID_{{uniqueId}}"  value=""   desc="人像比对" />
            <input type="hidden" name="AGENT_PIC_STREAM" id="AGENT_PIC_STREAM_{{uniqueId}}" value=""   desc="拍摄经办人人像照片流" />
            <input type="hidden" name="ReadCardFlag1" id="ReadCardFlag1_{{uniqueId}}" value="0" desc="二代证读取标志"/>
            <input type="hidden" name="READ_CARD_PZ1"  id="READ_CARD_PZ1_{{uniqueId}}" value=""  maxsize="10" desc="经办人拍照标记" />
            <input type="hidden" name="READ_CARD_PZ_VALUE1" id="READ_CARD_PZ_VALUE1_{{uniqueId}}" value="0" maxsize="10" desc="拍照标记值"/>



            <!--提交时证件信息组装,字段值须与客户组件提交的值保持一致-->
            <input type="hidden" name="PSPT_ID" id="PSPT_ID_{{uniqueId}}" value="" desc="证件号码"/>
            <input type="hidden" name="CUST_NAME" id="CUST_NAME_{{uniqueId}}" value="" desc="客户姓名"/>
            <input type="hidden" name="BIRTHDAY" id="BIRTHDAY_{{uniqueId}}" value="" desc="出生日期"/>
            <input type="hidden" name="PSPT_END_DATE" id="PSPT_END_DATE_{{uniqueId}}" value="" desc="证件有效期"/>
            <input type="hidden" name="PSPT_ADDR"  id="PSPT_ADDR_{{uniqueId}}" value="" desc="证件地址"/>
            <input type="hidden" name="SEX" id="SEXPSPT_ADDR_{{uniqueId}}" value="" desc="性别"/>
            <input type="hidden" name="FOLK_CODE" id="FOLK_CODE_{{uniqueId}}" value="" desc="民族"/>
            <input type="hidden" name="AGENT_CUST_NAME" id="AGENT_CUST_NAME_{{uniqueId}}" value=""  desc="经办人名称" />
            <input type="hidden" name="AGENT_PSPT_TYPE_CODE" id="AGENT_PSPT_TYPE_CODE_{{uniqueId}}" value=""   desc="经办人证件类型"/>
            <input type="hidden" name="AGENT_PSPT_ID" id="AGENT_PSPT_ID_{{uniqueId}}" value=""  desc="经办人证件号码" />
            <input type="hidden" name="AGENT_PSPT_ADDR" id="AGENT_PSPT_ADDR_{{uniqueId}}" value=""   desc="经办人证件地址" />
            <input type="hidden" name="USE" id="USE_{{uniqueId}}" value=""   desc="使用人姓名" />
            <input type="hidden" name="USE_PSPT_TYPE_CODE" id="USE_PSPT_TYPE_CODE_{{uniqueId}}" value=""  desc="使用人证件类型" />
            <input type="hidden" name="USE_PSPT_ID" id="USE_PSPT_ID_{{uniqueId}}" value=""  desc="使用人证件号码" />
            <input type="hidden" name="USE_PSPT_ADDR" id="USE_PSPT_ADDR_{{uniqueId}}" value=""  desc="使用人证件地址" />
            <input type="hidden" name="PSPT_TYPE_CODE" id="PSPT_TYPE_CODE_{{uniqueId}}" value="" desc="证件类型编码"/>

        </div>

</div>