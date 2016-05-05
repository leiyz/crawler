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
public class CSTOProjectCrawler extends BreadthCrawler {

    public CSTOProjectCrawler(String crawlPath, boolean autoParse) {
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
        if (page.getUrl() == "http://www.csto.com/project/list") {
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

    public static void main(String[] args) {
        int a = 10;
        a--;
        System.out.println(a);
    }

    public static void main1(String[] args) throws Exception {
        CSTOProjectCrawler crawler = null;
        crawler = new CSTOProjectCrawler("cstoCrawler", true);
        crawler.addSeed("http://www.csto.com/project/list");
        crawler.setThreads(100);
        crawler.start(2);
    }
}