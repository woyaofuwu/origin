select decode(a.bpm_templet_id,'ERESOURCECONFIRMZHZG','开通勘察单','EDIRECTLINEOPENPBOSS','开通单','ECHANGERESOURCECONFIRM','变更勘察单','EDIRECTLINECHANGEPBOSS','变更开通单','       DIRECTLINECHANGESIMPLE','简单场景变更','EDIRECTLINECHANGEFEE','资费变更单','DIRECTLINECANCEL','拆机单','其他') as SUB_TYPE,
             t.IBSYSID,t.RSRV_STR4,t.BUSI_CODE PRODUCT_ID,t.GROUP_ID,to_char(t.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,'已归档' as STATE
             from tf_bh_eop_subscribe t
             where 1=1
             and t.BPM_TEMPLET_ID = :ROWS
             and to_char(t.accept_time,'yyyymmdd') >= to_char(to_date(:START_DATE,'yyyy-mm-dd'),'yyyymmdd')
              and to_char(t.accept_time,'yyyymmdd') <= to_char(to_date(:END_DATE,'yyyy-mm-dd'),'yyyymmdd')
             and t.deal_state = '2'
             order by t.accept_time desc