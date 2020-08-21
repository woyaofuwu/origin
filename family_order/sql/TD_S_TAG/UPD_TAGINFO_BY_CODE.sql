update td_s_tag set update_time = sysdate,tag_info = :TAG_INFO ,tag_char = :TAG_CHAR
where tag_code = :TAG_CODE and use_tag =:USE_TAG and subsys_code = :SUBSYS_CODE