SELECT COUNT(1) FROM dual WHERE
    (SELECT score FROM tf_f_user_newscore WHERE user_id=:USER_ID AND year_id='1000' AND score_type_code='10')>=
    (SELECT tag_number FROM td_s_tag WHERE tag_code=decode(:BRAND_NO,'3','CS_MZONEXCHANGENEW_MIN_LIMIT','CS_SCOREEXCHANGENEW_MIN_LIMIT')
    AND eparchy_code=:EPARCHY_CODE)