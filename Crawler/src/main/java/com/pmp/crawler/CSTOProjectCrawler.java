package com.pmp.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by lyz on 4/26/16.
 */
public class CSTOProjectCrawler extends BreadthCrawler {

    public CSTOProjectCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

//    @Override
//    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
//        HttpRequest request = new HttpRequest(crawlDatum);
////        request.setCookie("bdshare_firstime=1462420516150; CSTOID=ns8bie188nkm58cs0hmvqvj854; visite=%2F; login_checked=1; UserName=OOOO00; UserInfo=yuXA6jbFM8Aipvonl8ozIW7ySRd0mRK8Ylchc%252B7jUuRRSKn%252BAmxHeI80XqlLO2SsA2k%252BpvFhf2NpI%252FzLtMJbUDjFoiJx9JzHdwBrCf6Vt3bFTv4XmJOyv69ezvSU%252Bb6D; dc_tos=o6p6cz; dc_session_id=1462440563176; Hm_lvt_67fed6c225de3f90d9f513aed5a91532=1462420028,1462430691; Hm_lpvt_67fed6c225de3f90d9f513aed5a91532=1462440563; __utmt=1; __utma=174166704.1927859358.1462420028.1462436899.1462440563.4; __utmb=174166704.1.10.1462440563; __utmc=174166704; __utmz=174166704.1462420028.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
//        return request.getResponse();
//    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        System.out.println(page.getUrl());
        if (page.matchUrl("http://www.csto.com/project/list")) {
            Links pageLinks = page.getLinks("#list_pageshow > a:nth - child(5)");
            if (pageLinks != null && pageLinks.size() == 1) {
                String[] urlparams = pageLinks.get(0).split("=");
                if (urlparams == null || urlparams.length < 2) return;
                int totalPage = Integer.parseInt(urlparams[1]);
                int pageNums = (int) (1 + Math.random() * (totalPage));
                while (pageNums >= 2) {
                    pageNums--;
                    crawlDatums.add(urlparams[0] + "=" + pageNums);
                }
                return;
            }
            return;
        }
        if (page.matchUrl("http://www.csto.com/project/list?page=[^/]/")) {
            //#list_shwores > dl > dd.intro > div.title > a
            Links pageLinks = page.getLinks("#list_shwores > dl > dd.intro > div.title > a");
            System.out.println("------->CSTO project for each page size:" + pageLinks.size());
            crawlDatums.add(pageLinks);
        }
        if (page.matchUrl("http://www.csto.com/p/[^/]/")) {
            String urlid = page.getUrl().replace("https://", "").replace("http://", "").replaceAll("/+", "-");
            String filePath = "/home/lyz/temp/csto-project";

            //IVRight content 计划中  工作中
            Elements elements = page.select("body > div.wrap.ItemView > div.IVRight > dl > dt > h5 > span");
            // finish project
            Elements elementsFinish = page.select("div.wrap finish project");
            if (elementsFinish != null && elementsFinish.size() > 0) {
                Path filepath = Paths.get(filePath + "/finished" + urlid + ".html");
                FileUtilitys.writeToHtmlFile(elementsFinish, filepath);
                return;

            } else {
                if (elements == null || elements.size() != 1) return;
                Elements elementsWorking = page.select("body > div> div.IVLeft");
                if ("计划中".equals(elements.get(0).text().trim())) {
                    Path filepath = Paths.get(filePath + "/working" + urlid + ".html");
                    FileUtilitys.writeToHtmlFile(elementsWorking, filepath);
                    return;
                }
                if ("关闭".equals(elements.get(0).text().trim())) {
                    Path filepath = Paths.get(filePath + "/closed" + urlid + ".html");
                    FileUtilitys.writeToHtmlFile(elementsWorking, filepath);
                    return;
                }
                //竞标
                if ("竞标".equals(elements.get(0).text().trim())) {
                    Path filepath = Paths.get(filePath + "/bidding" + urlid + ".html");
                    FileUtilitys.writeToHtmlFile(elementsWorking, filepath);
                    return;
                }
            }
        }
    }

    public static void main1(String[] args) {
        int a = 10;
        a--;
        System.out.println(a);
    }

    public static void main(String[] args) throws Exception {
        CSTOProjectCrawler crawler = null;
        crawler = new CSTOProjectCrawler("cstoCrawler", true);
        crawler.addSeed("http://www.csto.com/project/list");
        crawler.setThreads(100);
        crawler.start(3);
    }
}