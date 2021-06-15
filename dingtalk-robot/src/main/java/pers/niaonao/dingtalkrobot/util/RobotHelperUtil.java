package pers.niaonao.dingtalkrobot.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest ;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import pers.niaonao.dingtalkrobot.enums.MsgTypeEnum;

import java.net.URLEncoder;
/**
 * @className: RobotHelperUtil
 * @description: 机器人工具类
 *      每个机器人每分钟最多发送20条
 *      限制6 个机器人/群
 *      https://developers.dingtalk.com/document/app/custom-robot-access/title-nfv-794-g71
 * @author: niaonao
 * @date: 2019/7/6
 **/
@Slf4j
public class RobotHelperUtil {

    /**
     * 钉钉群设置 webhook, 支持重置
     */
    private static final String ACCESS_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=40d2e1ef8c83b0ade5c7d2ae43553988c68373c1fa0901dcd701b0c2f5f90c59";
    /**
     * 加签密钥，支持重置
     */
    private static final String SECRET = "SECc90f8dac81401632962362d280f79a86e2875d8fa2282c7ee80249385be76b38";
    /**
     * 安全设置：是否加签
     */
    private static boolean isSign = true;

    /**
     * 客户端实例
     */
    public static DingTalkClient client = new DefaultDingTalkClient(getServerUrl(ACCESS_TOKEN, SECRET));

    /**
     * @description: 官方演示示例
     *      title 是消息列表下透出的标题
     *      text 是进入群后看到的消息内容
     *
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static void sdkDemoJava() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("测试文本消息");
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Arrays.asList("13261303345"));
        request.setAt(at);

        request.setMsgtype("link");
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setMessageUrl("https://www.dingtalk.com/");
        link.setPicUrl("");
        link.setTitle("时代的火车向前开");
        link.setText("这个即将发布的新版本，创始人陈航（花名“无招”）称它为“红树林”。\n" +
                "而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是“红树林");
        request.setLink(link);

        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("杭州天气");
        markdown.setText("#### 杭州天气 @156xxxx8827\n" +
                "> 9度，西北风1级，空气良89，相对温度73%\n\n" +
                "> ![screenshot](https://gw.alipayobjects.com/zos/skylark-tools/public/files/84111bbeba74743d2771ed4f062d1f25.png)\n"  +
                "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n");
        request.setMarkdown(markdown);
        requestExecute(request);
    }

    /**
     * @description: 发送普通文本消息
     * @param content   文本消息
     * @param mobileList    指定@ 联系人
     * @param isAtAll       是否@ 全部联系人
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByText(String content, List<String> mobileList, boolean isAtAll) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(isAtAll);
        //atMobiles	被@人的手机号
        if (!CollectionUtils.isEmpty(mobileList)) {
            at.setAtMobiles(mobileList);
            if (!isAtAll) {
                content = content + "\n";
                for (String mobile : mobileList) {
                    content = content + "@" + mobile;
                }
            }
        }
        //参数	参数类型	必须	说明
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setAt(at);
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_TEXT.getValue());
        request.setText(text);

        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }

    /**
     * @description: 发送link 类型消息，点击标题实现在钉钉外部打开链接
     * @param title 消息标题
     * @param text  消息内容
     * @param messageUrl     点击消息后跳转的url
     * @param picUrl    插入图片的url
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByLink(String title, String text, String messageUrl, String picUrl) {
        if (!DataValidUtil.checkNotEmpty(title, text, messageUrl)) {
            return null;
        }
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setTitle(title);
        link.setText(text);
        link.setMessageUrl(messageUrl);
        link.setPicUrl(picUrl);

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_LINK.getValue());
        request.setLink(link);

        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }


    /**
     * @description: 发送Markdown 编辑格式的消息
     * @param title 标题
     * @param markdownText  支持markdown 编辑格式的文本信息
     * @param mobileList    消息@ 联系人
     * @param isAtAll   是否@ 全部
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByMarkdown(String title, String markdownText, List<String> mobileList, boolean isAtAll) {
        if (!DataValidUtil.checkNotEmpty(title, markdownText)) {
            return null;
        }
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(isAtAll);
        if (!CollectionUtils.isEmpty(mobileList)) {
            at.setAtMobiles(mobileList);
            if (!isAtAll) {
                markdownText = markdownText + "\n";
                for (String mobile : mobileList) {
                    markdownText = markdownText + "@" + mobile;
                }
            }
        }
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(markdownText);

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_MARKDOWN.getValue());
        request.setMarkdown(markdown);
        request.setAt(at);

        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }

    /**
     * @description: 整体跳转ActionCard类型的消息发送
     * @param title 消息标题, 会话消息会展示标题
     * @param markdownText  markdown格式的消息
     * @param singleTitle   单个按钮的标题
     * @param singleURL 单个按钮的跳转链接
     * @param btnOrientation    是否横向排列(true 横向排列, false 纵向排列)
     *      参数	类型	必选	说明
     *      msgtype	string	true	此消息类型为固定actionCard
     *      title	string	true	首屏会话透出的展示内容
     *      text	string	true	markdown格式的消息
     *      singleTitle	string	true	单个按钮的方案。(设置此项和singleURL后btns无效)
     *      singleURL	string	true	点击singleTitle按钮触发的URL
     *      btnOrientation	string	false	0-按钮竖直排列，1-按钮横向排列
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByActionCardSingle(String title, String markdownText, String singleTitle, String singleURL, boolean btnOrientation) {
        if (!DataValidUtil.checkNotEmpty(title, markdownText)) {
            return null;
        }
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        actionCard.setText(markdownText);
        actionCard.setSingleTitle(singleTitle);
        actionCard.setSingleURL(singleURL);
        // 此处默认为0
        actionCard.setBtnOrientation(btnOrientation ? "1" : "0");

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_ACTION_CARD.getValue());
        request.setActionCard(actionCard);
        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }

    /**
     * @description: 独立跳转ActionCard类型 消息发送
     * @param title 标题
     * @param markdownText  文本
     * @param btns  按钮列表
     * @param btnOrientation    是否横向排列(true 横向排列, false 纵向排列)
     *      参数	类型	必选	说明
     *      msgtype	string	true	此消息类型为固定actionCard
     *      title	string	true	首屏会话透出的展示内容
     *      text	string	true	markdown格式的消息
     *      btns	array	true	按钮的信息：title-按钮方案，actionURL-点击按钮触发的URL
     *      btnOrientation	string	false	0-按钮竖直排列，1-按钮横向排列
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByActionCardMulti(String title, String markdownText, List<OapiRobotSendRequest.Btns> btns, boolean btnOrientation) {
        if (!DataValidUtil.checkNotEmpty(title, markdownText) || CollectionUtils.isEmpty(btns)) {
            return null;
        }
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        actionCard.setText(markdownText);
        // 此处默认为0
        actionCard.setBtnOrientation(btnOrientation ? "1" : "0");

        actionCard.setBtns(btns);

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_ACTION_CARD.getValue());
        request.setActionCard(actionCard);
        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }

    /**
     * @description: 发送FeedCard类型消息
     *      msgtype	string	true	此消息类型为固定feedCard
     *      title	string	true	单条信息文本
     *      messageURL	string	true	点击单条信息到跳转链接(在钉钉内部打开，不能跳转到外部)
     *      picURL	string	true	单条信息后面图片的URL
     * @param links
     * @return: com.dingtalk.api.response.OapiRobotSendResponse
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static OapiRobotSendResponse sendMessageByFeedCard(List<OapiRobotSendRequest.Links> links) {
        if (CollectionUtils.isEmpty(links)) {
            return null;
        }

        OapiRobotSendRequest.Feedcard feedcard = new  OapiRobotSendRequest.Feedcard();
        feedcard.setLinks(links);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(MsgTypeEnum.MSG_TYPE_FEED_CARD.getValue());
        request.setFeedCard(feedcard);
        OapiRobotSendResponse response = (OapiRobotSendResponse) requestExecute(request);
        return response;
    }

    /**
     * SDK 请求
     */
    public static TaobaoResponse requestExecute(TaobaoRequest request){
        TaobaoResponse response = null;
        try {
            response = RobotHelperUtil.client.execute(request);
        } catch (Exception e) {
            log.error("钉钉监控机器人发送消息失败, 异常捕获{}", e.getMessage());
        }
        return response;
    }

    /**
     * @param: accessToken  token
     * @param: secret       加签密钥
     * @description: 获取 serverUrl
     * @return: java.lang.String
     * @author: niaonao
     */
    private static String getServerUrl(String accessToken, String secret) {
        StringBuilder serverUrl = new StringBuilder(accessToken);
        // isSign 是否加签
        if (!isSign) {
            return serverUrl.toString();
        }
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

            serverUrl.append("&timestamp=").append(timestamp)
                    .append("&sign=").append(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverUrl.toString();
    }

//    public static void main(String args[]) {
//        // sdkDemoJava();
//
//        // @指定联系人
//        /*String text = "这是文本消息，指定通知所有人";
//        sendMessageByText(text, null, true);*/
//
//        // @所有人
//        /*String title = "标题";
//        String markdownText = "这是文本消息，指定通知到部分负责人";
//        sendMessageByMarkdown(title, markdownText, Arrays.asList("17788559966"), false);*/
//
//        /*// 顶顶外部打开指定链接
//        String title = "标题";
//        String text = "内容";
//        String messageUrl = "https://blog.csdn.net/niaonao";
//        String picUrl = "https://img-blog.csdnimg.cn/img_convert/4f1819e8d505bfccef524ba36f18f527.png";
//        sendMessageByLink(title, text, messageUrl, picUrl);*/
//
//        /*String title = "标题";
//        String markdownText = "内容";
//        String singleTitle = "标题";
//        String singleUrl = "https://developers.blog.csdn.net/article/details/94960092";
//        sendMessageByActionCardSingle(title, markdownText, singleTitle, singleUrl, false);*/
//
//        /*String title = "标题";
//        String markdownText = "内容";
//        List<OapiRobotSendRequest.Btns> btnList = new ArrayList<>();
//        OapiRobotSendRequest.Btns btnDevelopers = new OapiRobotSendRequest.Btns();
//        btnDevelopers.setActionURL("https://developers.blog.csdn.net/article/details/94960092");
//        btnDevelopers.setTitle("developers 的博客");
//        OapiRobotSendRequest.Btns btnNiaonao = new OapiRobotSendRequest.Btns();
//        btnNiaonao.setActionURL("https://blog.csdn.net/niaonao");
//        btnNiaonao.setTitle("niaonao 的博客");
//        btnList.add(btnDevelopers);
//        btnList.add(btnNiaonao);
//        sendMessageByActionCardMulti(title, markdownText, btnList,true);*/
//
//        /*List<OapiRobotSendRequest.Links> links = new ArrayList<>();
//        OapiRobotSendRequest.Links linkBlog = new OapiRobotSendRequest.Links();
//        linkBlog.setTitle("博客地址");
//        linkBlog.setMessageURL("https://developers.blog.csdn.net");
//        linkBlog.setPicURL("https://img-blog.csdnimg.cn/img_convert/4f1819e8d505bfccef524ba36f18f527.png");
//        links.add(linkBlog);
//        sendMessageByFeedCard(links);*/
//    }
}
