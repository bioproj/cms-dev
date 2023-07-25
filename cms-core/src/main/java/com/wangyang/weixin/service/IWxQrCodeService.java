package com.wangyang.weixin.service;

import com.wangyang.weixin.entity.WxQrCode;
import com.wangyang.weixin.pojo.WxQrCodeParam;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public interface IWxQrCodeService extends ICrudService<WxQrCode, WxQrCode, BaseVo,Integer> {
    WxMpQrCodeTicket createQrCode(WxQrCodeParam form);
}
