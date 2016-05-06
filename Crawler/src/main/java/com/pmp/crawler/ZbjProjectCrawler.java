package com.pmp.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lyz on 4/26/16.
 */
public class ZbjProjectCrawler extends BreadthCrawler {

    public ZbjProjectCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setCookie(ZjbLoginCN.getZbjCookie("lyz88119@126.com", "engine"));
        return request.getResponse();
    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        if (page.matchUrl("http://task.zbj.com/t-.*")) {
            Links pageLinks = page.getLinks("tr>td>p>a");
            System.out.println("Zbj URL>>>>>>>>>" + page.getUrl());
            System.out.println("Zbj task URL>>>>>>>>>" + pageLinks.size());
            crawlDatums.add(pageLinks);
            return;
        }
        if (page.matchUrl("http://task.zbj.com/[^/]/")) {
            HttpResponse response = page.getResponse();
            Elements elements = page.select("#ed-tit");
            String urlid = page.getUrl().replace("https://", "").replace("http://", "").replaceAll("/+", "-");
            Path filepath = Paths.get("/home/lyz/temp/zbj-project" + urlid + ".html");
            elements.append(page.select("#j-content > div > div.taskmode-block.clearfix").html());
//            elements.append(page.select("#work-more").html());模拟登录
            //#j-content > div > div.nts.mt15.clearfix.task-extend-item
            elements.append(page.select("#j-content > div > div.nts.mt15.clearfix.task-extend-item").html());
            FileUtilitys.writeToHtmlFile(elements, filepath);
        }
    }

    public static void main(String[] args) throws Exception {
        ZbjProjectCrawler crawler = null;
        crawler = new ZbjProjectCrawler("zbjCrawler", true);
        String urlrjkf = "http://task.zbj.com/t-rjkf/";
        String urlui = "http://task.zbj.com/t-uisheji/";
        String urlydkf = "/http://task.zbj.com/t-ydyykf/";
        String urlwzkf = "http://task.zbj.com/t-wzkf/";
        //http://task.zbj.com/t-wzkf/s5p4.html
        for (int i = 1; i <= 100; i++) {
            if (i == 1) {
                crawler.addSeed(urlrjkf);
                crawler.addSeed(urlui);
                crawler.addSeed(urlydkf);
                crawler.addSeed(urlwzkf);
                continue;
            }
            String urlAppend = String.format("s5p%s.html", i);
            crawler.addSeed(urlrjkf + urlAppend);
            crawler.addSeed(urlui + urlAppend);
            crawler.addSeed(urlydkf + urlAppend);
            crawler.addSeed(urlwzkf + urlAppend);
        }
        crawler.setThreads(100);
        crawler.start(2);
    }
}