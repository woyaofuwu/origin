<div class="c_box c_box-border">
    <div class="c_title">
        <div class="text">{{title}}手机成员</div>
        <div class="fn" name="titleFn">
            <%- template.render(btnsArt, {btnStrs:btnStrs,isLtIE9:isLtIE9}) %>
        </div>
    </div>
    <div class="l_padding-2 l_padding-u">
        <div class="c_form c_form-col-3">
            <ul>
                <li class="link required">
                    <div class="label">服务号码：</div>
                    <div class="value">
						<span class="e_group">
							<span class="e_groupRight">
								<span class="e_ico-search e_blue" tip="查询" handle="query"></span>
							</span>
							<span class="e_groupMain">
								<input type="text" name="PHONE_NUM" value="{{sn}}" maxlength="11"/>
							</span>
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
								<input type="text" name="ORDER_DISCNT_CODE" nullable="no" disabled="true"/>
							</span>
						</span>
                    </div>
                </li>
            </ul>
        </div>
        <div class="c_list c_list-s">
            <ul name="phoneMem">
            </ul>
        </div>
    </div>
    <div partId="SelectedOffers"></div>
    <div class="c_space"></div>
    <div name="BuildWideNetRolePart"></div>
</div>
<div class="c_space"></div>