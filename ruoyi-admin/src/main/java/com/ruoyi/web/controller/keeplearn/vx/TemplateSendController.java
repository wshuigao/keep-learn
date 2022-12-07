package com.ruoyi.web.controller.keeplearn.vx;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * vx 测试
 * @author wh
 */
@RestController
@RequestMapping("/vx")
public class TemplateSendController {

  private static final Logger log = LoggerFactory.getLogger(TemplateSendController.class);

  // 公众号：appId
  @Value("${vx.appId}")
  private  String appId;
  // 公众号：appsecret
  @Value("${vx.appsecret}")
  private  String appsecret;

  // token 存放到redis中的key
  private final String ACCESS_TOKEN = "weiXinApp:access_token";

  @Autowired
  private RedisCache redisCache;

  /**
   * 获取 vx 的 ACCESS_TOKEN
   */
  @GetMapping("/getToken")
  public String getToken(){
    // 先从缓存中获取
    String token = (String) redisCache.getCacheObject(ACCESS_TOKEN);
    // 获取不到,再进行去调用微信的接口
    if (StringUtils.isBlank(token)) {
      //请求url路径
      String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appsecret;

      HashMap tokenMap = null;
      try {
        // 具体发送操作
        String str = HttpUtil.createGet(url).execute().body();

        tokenMap = JSON.parseObject(str, HashMap.class);
      } catch (Exception e) {
        log.error(e.toString());
        return null;
      }
      if (tokenMap == null || tokenMap.size() == 0) {
        return null;
      }
      token = (String) tokenMap.get("access_token");
      Integer expiresIn = (Integer) tokenMap.get("expires_in");
      if (StringUtils.isBlank(token) || expiresIn == null || expiresIn <= 0) {
        return null;
      }
      // 把token 放到缓存中
      redisCache.setCacheObject(ACCESS_TOKEN, token, expiresIn - 2500, TimeUnit.SECONDS);
      return token;
    }
    return token;
  }

  @GetMapping("/testPushMessage")
  public String testPushMessage() {
    // 组装要发送的数据
    JSONObject body = new JSONObject();
    // 要推给谁
    body.put("touser", "oIQ1z6MD8bcYAsnCCtiU6H54pLcg");
    // 模板ID
    body.put("template_id", "OT_KJJKhDdvGVReyz1HLhZVA8NwF43wtNu_jz_xqU68");

    // 具体发送内容
    JSONObject jsonParam = new JSONObject();
    JSONObject dateVal = new JSONObject();
    dateVal.put("value","2022-12-01");
    dateVal.put("color","#173177");
    jsonParam.put("date", dateVal);
    body.put("data", jsonParam);
    //发送
    String accessToken = getToken();
    log.info("token--->{}",accessToken);
    log.info("body---->{}",body.toString());

    String post = HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken, body.toString());
    log.info("通知到用户--->{}",post);
    return "ok";

  }

}
