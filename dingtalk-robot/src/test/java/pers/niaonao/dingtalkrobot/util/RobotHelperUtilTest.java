package pers.niaonao.dingtalkrobot.util;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @className: RobotHelperUtilTest
 * @description: 钉钉机器人消息通知测试
 * @author: niaonao
 * @date: 2019/7/6
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RobotHelperUtilTest {

    /**
     * 钉钉机器人消息发送的响应结果
     */
    private OapiRobotSendResponse response;

    @Test
    public void sendMessageByText() {
        String content = "系统运行异常, 服务器224.2.2.102通信失败!";
        response = RobotHelperUtil.sendMessageByText(content, Arrays.asList("17788559966"), false);
    }

    @Test
    public void sendMessageByLink() {
        String title = "自定义机器人";
        String text = "听说你在测试钉钉机器人, 悟空推荐看下钉钉开发文档自定义机器人";
        String messageUrl = "https://blog.csdn.net/niaonao";
        String picUrl = "http://img01.taobaocdn.com/top/i1/LB1lIUlPFXXXXbGXFXXXXXXXXXX#align=left&display=inline&height=294&originHeight=1372&originWidth=2088&status=done&width=447";
        response = RobotHelperUtil.sendMessageByLink(title, text, messageUrl, picUrl);
    }

    @Test
    public void sendMessageByMarkdown() {
        String title = "悟空建议你看下Markdown 语法";
        String markdownText =
                "## 列表\n" +
                        "无序列表\n" +
                        "- item1\n" +
                        "- item2\n" +
                        "## 代码片\n" +
                        "```java\n" +
                        "public void sendMessageByMarkdown() {\n" +
                        "        String title = \"title\";\n" +
                        "        String markdownText = \"text\";\n" +
                        "        response = RobotHelperUtil.sendMessageByMarkdown(title, markdownText,null, false);\n" +
                        "    }\n" +
                        "```";
        response = RobotHelperUtil.sendMessageByMarkdown(title, markdownText, Arrays.asList("17788559966"), false);
    }

    @Test
    public void sendMessageByActionCardSingle() {
        String title = "ActionCard 整体跳转Card";
        String markdownText = "### 消息内容";
        String singleTitle = "查看原文";
        String singleURL = "https://open-doc.dingtalk.com/microapp/serverapi3/iydd5h";
        response = RobotHelperUtil.sendMessageByActionCardSingle(title, markdownText, singleTitle, singleURL, true);

    }

    @Test
    public void sendMessageByActionCardMulti() {
        String title = "悟空建议查看下ActionCard 单独跳转类型消息";
        String markdownText = "### 备注\n此处省略了一些字^_^";
        List<OapiRobotSendRequest.Btns> btns = new ArrayList<>();
        btns.add(new OapiRobotSendRequest.Btns());
        btns.add(new OapiRobotSendRequest.Btns());
        btns.add(new OapiRobotSendRequest.Btns());
        btns.forEach((btn) -> {
            btn.setTitle("Button 标题" + btns.indexOf(btn));
            btn.setActionURL("https://open-doc.dingtalk.com/microapp/serverapi3/iydd5h");
        });
        response = RobotHelperUtil.sendMessageByActionCardMulti(title, markdownText, btns, true);
    }

    @Test
    public void sendMessageByFeedCard() {
        List<OapiRobotSendRequest.Links> links = new ArrayList<>();
        links.add(new OapiRobotSendRequest.Links());
        links.add(new OapiRobotSendRequest.Links());
        links.add(new OapiRobotSendRequest.Links());
        links.forEach((link) -> {
            link.setTitle("Link 标题" + links.indexOf(link));
            link.setMessageURL("https://open-doc.dingtalk.com/microapp/serverapi3/iydd5h");
            link.setPicURL("http://img01.taobaocdn.com/top/i1/LB1R2evQVXXXXXDapXXXXXXXXXX#align=left&display=inline&height=341&originHeight=804&originWidth=712&status=done&width=302");
        });
        response = RobotHelperUtil.sendMessageByFeedCard(links);
    }

    @Before
    public void before() {
    }

    @After
    public void after(){
        if (null == response) {
            log.info("[单元测试]: 请检查请求必传参数!");
            return;
        }

        StringBuilder responseString = new StringBuilder();
        responseString.append("class OapiRobotSendResponse {\n");
        responseString.append("    errCode: ").append(response.getErrcode()).append("\n");
        responseString.append("    errMsg: ").append(response.getErrmsg()).append("\n");
        responseString.append("}");
        log.info("[单元测试]发送消息出参: {}", responseString.toString());
    }
}
