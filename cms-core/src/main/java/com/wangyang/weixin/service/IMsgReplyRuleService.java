package com.wangyang.weixin.service;

import com.wangyang.weixin.entity.MsgReplyRule;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;


public interface IMsgReplyRuleService extends ICrudService<MsgReplyRule, MsgReplyRule, BaseVo,Integer> {


    List<MsgReplyRule> getMatchedRules(boolean exactMatch, String keywords);
}
