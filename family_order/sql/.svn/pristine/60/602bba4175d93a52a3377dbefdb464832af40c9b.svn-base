SELECT partition_id,
       to_char(user_id) user_id,
       to_char(acct_id) acct_id,
       payitem_code,
       acct_priority,
       user_priority,
       bind_type,
       start_cycle_id,
       end_cycle_id,
       default_tag,
       act_tag,
       limit_type,
       to_char(limit) limit,
       complement_tag,
       update_staff_id,
       update_depart_id,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time
  FROM tf_a_payrelation a
 where acct_id = TO_NUMBER(:ACCT_ID)
   and act_tag = '1'
   and start_cycle_id <= end_cycle_id
   and exists (select *
          from TF_F_USER b
         where a.user_id = b.user_id
           and remove_tag = '0')