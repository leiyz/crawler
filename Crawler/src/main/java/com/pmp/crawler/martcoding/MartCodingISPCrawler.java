package com.pmp.crawler.martcoding;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by clouder on 4/19/16.
 */
public class MartCodingISPCrawler extends BreadthCrawler {
    public MartCodingISPCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        if (page.matchUrl("http://shop.zbj.com/[^/]+")) {
            crawlDatums.add(page.getUrl() + "/salerinfo.html");
            return;
        }
        if (page.matchUrl("http://home.zbj.com")) {
            Links pageLinks = page.getLinks("h5 > a");
            System.out.println("ISP URL>>>>>>>>>" + pageLinks);
            crawlDatums.add(pageLinks);
            return;
        }
        if (page.matchUrl("http://shop.zbj.com/[^/]+/salerinfo.html")) {
            Elements elements = page.select("#noMobileTip > div > div.wk-r > div > div");
            elements.stream().forEach(x -> {
                try {
                    String urlid = page.getUrl().replace("https://", "").replace("http://", "").replaceAll("/+", "-");
                    Files.write(Paths.get("/home/clouder/lyz/temp/ISP/" + urlid + ".html"), x.html().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        MartCodingISPCrawler crawler = new MartCodingISPCrawler("crawler", true);
        crawler.addSeed("http://home.zbj.com");
        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);

        crawler.setThreads(10);
        crawler.start(3);
    }
}
