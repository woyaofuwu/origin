
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
                            <div class="label">固话号码：</div>
                                <div class="value">
                                    <span class="e_mix">
                                        <input type="text" name="FIX_NUMBER" id="FIX_NUMBER_{{uniqueId}}" value="{{imsSn}}" datatype="numeric" desc="固话号码" maxLength="8"/>
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
                       <li class="link" name="EXIST_IMS_OFFER" id="EXIST_IMS_OFFER_{{uniqueId}}" style="display:none">
                            <div class="label">现有商品：</div>
                            <div class="value">
                              <span class="e_mix">
                                 <input type="text" name="EXIST_IMS_OFFER_NAME" id="EXIST_IMS_OFFER_NAME_{{uniqueId}}" value="" desc="用户已有商品名称"/>
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

        </div>

</div>