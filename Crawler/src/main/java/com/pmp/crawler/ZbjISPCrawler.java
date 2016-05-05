package com.pmp.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by clouder on 4/19/16.
 */
public class ZbjISPCrawler extends BreadthCrawler {
    private static int urlCount;
    /**
     *
     */
    private static Proxys proxys = new Proxys();

    public ZbjISPCrawler(String crawlPath, boolean autoParse) throws Exception {
        super(crawlPath, autoParse);

    }

//    @Override
//    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
//        HttpRequest request = new HttpRequest(crawlDatum);
//        request.setProxy(proxys.nextRandom());
//        return request.getResponse();
//    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        if (page == null) {
            System.out.println("ISP fail URL>>>>>>>>>" + page.getUrl());
        }
        if (page.matchUrl("http://shop.zbj.com/[^/]+")) {
            crawlDatums.add(page.getUrl() + "/salerinfo.html");
            return;
        }
        if (page.matchUrl("http://home.zbj.com") || page.matchUrl("http://home.zbj.com/p-p.*")) {
            Links pageLinks = page.getLinks("h5 > a");
            System.out.println("ISP URL>>>>>>>>>" + pageLinks.size());
            System.out.println("ISP URL salerinfo>>>>>>>>>" + pageLinks.filterByRegex("http://shop.zbj.com/[^/]+").size());
            crawlDatums.add(pageLinks);
            return;
        }
        if (page.matchUrl("http://shop.zbj.com/[^/]+/salerinfo.html")) {
            urlCount++;
            Elements elements = page.select("#noMobileTip > div > div.wk-r > div > div");
            String urlid = page.getUrl().replace("https://", "").replace("http://", "").replaceAll("/+", "-");
            Path filepath = Paths.get("/home/lyz/temp/zbj-ISP" + urlid + ".html");
            FileUtilitys.writeToHtmlFile(elements, filepath);
        }
    }


    private static void readProxyfile() throws IOException {
        Path path = Paths.get("/home/clouder/workspace/crawler/Crawler/resources/proxy.config");
        List<String> contents = Files.readAllLines(path);
        contents.forEach(x ->
        {
            String[] values = x.split(" ");
            if (values.length >= 2) {
                System.out.println(">>>>>>IP:" + values[0].toString() + " ,port:" + Integer.valueOf(values[1].toString()));
                proxys.add(values[0].toString(), Integer.valueOf(values[1].toString()));
            }
        });
    }

    public static void main1(String[] args) throws IOException {
        readProxyfile();
    }

    public static void main(String[] args) throws Exception {
//        readProxyfile();
        ZbjISPCrawler crawler = new ZbjISPCrawler("crawler", true);
        crawler.addSeed("http://home.zbj.com");
//        crawler.setRetryInterval(5000);
        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);
        //http://home.zbj.com/p-p2.html
        for (int i = 1; i <= 100; i++) {
            crawler.addSeed(String.format("http://home.zbj.com/p-p%s.html", i == 1 ? "" : i));
        }
        crawler.setThreads(5);
        crawler.start(3);
        System.out.println("TOTAL ISP URL:  " + urlCount);

    }
}
