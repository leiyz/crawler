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
    private String seedsPrefix = "http://www.csto.com";

    public CSTOProjectCrawler(String crawlPath, boolean autoParse) {
        super( crawlPath, autoParse );
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest( crawlDatum );
//        InetSocketAddress addr = new InetSocketAddress( "117.135.251.136",
//                84 );
//        Proxy proxy = new Proxy( Proxy.Type.HTTP, addr ); // http 代理
        //如果我们知道代理server的名字, 可以直接使用
//        request.setProxy( proxy );
        request.setMAX_REDIRECT( 5 );
//        request.setTimeoutForConnect( 150000 );
//        request.setTimeoutForRead( 200000 );
        request.setCookie( "visite=%2Fproject%2Flist; CSTOID=s8v0dn01oip0gcrokkteq5ss96; login_checked=1; __utmt=1; dc_tos=o6weln; dc_session_id=1462777835937; __utma=174166704.147535321.1462777831.1462777831.1462777831.1; __utmb=174166704.2.10.1462777831; __utmc=174166704; __utmz=174166704.1462777831.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_67fed6c225de3f90d9f513aed5a91532=1462777830; Hm_lpvt_67fed6c225de3f90d9f513aed5a91532=1462777836; UserName=OOOO00; UserInfo=yuXA6jbFM8Aipvonl8ozIW7ySRd0mRK8Ylchc%252B7jUuRRSKn%252BAmxHeI80XqlLO2SsA2k%252BpvFhf2NpI%252FzLtMJbUDjFoiJx9JzHdwBrCf6Vt3bFTv4XmJOyv69ezvSU%252Bb6D" );
        return request.getResponse();
    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        if (page.matchUrl( "http://www.csto.com/project/list" )) {
            System.out.println( "----------->>>>>step1:" + page.getUrl() );
            Elements elements = page.select( "#list_pageshow > a" );
            elements.forEach( x ->
            {
                if (x.text().contains( "最后一页" )) {
                    String[] urlparams = x.attributes().get( "href" ).trim().split( "=" );
                    if (urlparams == null || urlparams.length < 2) return;
                    int totalPage = Integer.parseInt( urlparams[1] );
                    while (totalPage >= 2) {
                        totalPage--;
                        crawlDatums.add( seedsPrefix + urlparams[0] + "=" + totalPage );
                    }
                    return;
                }
            } );
            return;
        } else if (page.matchUrl( ".*?page=.*" )) {
            System.out.println( "----------->>>>>step2:" + page.getUrl() );
            Links pageLinks = page.getLinks( "#list_shwores > dl > dd.intro > div.title > a" );
            System.out.println( "------->CSTO project for each page size:" + pageLinks.size() );
            crawlDatums.add( pageLinks );
            return;
        } else {
            System.out.println( "----------->>>>>step3:" + page.getUrl() );
            String urlid = page.getUrl().replace( "https://", "" ).replace( "http://", "" ).replaceAll( "/+", "-" );
            String filePath = "/home/lyz/temp/csto-project";
            Elements elements = page.select( "body > div.wrap.ItemView > div.IVRight > dl > dt > h5 > span" );
            //project title body > div> div> h2
            //project status body > div> div> div.status_bar
            //project info body > div > div > div.item_board
            //project log body > div> div > div.item_log
            Elements prj = page.select( "body > div> div> h2" );
            prj.append( page.select( "body > div> div> div.status_bar" ).html() );
            prj.append( page.select( "body > div > div > div.item_board" ).html() );
            prj.append( page.select( "body > div> div > div.item_log" ).html() );
            if (elements == null && elements.size() <= 0) return;
            if ("完工".equals( elements.get( 0 ).text().trim() )) {
                Path filepath = Paths.get( filePath + "/finished/" + urlid + ".html" );
                FileUtilitys.writeToHtmlFile( prj, filepath );
                return;

            }
            if ("计划中".equals( elements.get( 0 ).text().trim() )) {
                Path filepath = Paths.get( filePath + "/working/" + urlid + ".html" );
                FileUtilitys.writeToHtmlFile( prj, filepath );
                return;
            }
            if ("关闭".equals( elements.get( 0 ).text().trim() )) {
                Path filepath = Paths.get( filePath + "/closed/" + urlid + ".html" );
                FileUtilitys.writeToHtmlFile( prj, filepath );
                return;
            }
            if ("竞标".equals( elements.get( 0 ).text().trim() )) {
                Path filepath = Paths.get( filePath + "/bidding/" + urlid + ".html" );
                FileUtilitys.writeToHtmlFile( prj, filepath );
                return;
            }
        }
    }

    public static void main1(String[] args) {
        int a = 10;
        a--;
        System.out.println( a );
    }

    public static void main(String[] args) throws Exception {
        CSTOProjectCrawler crawler = null;
        crawler = new CSTOProjectCrawler( "cstoCrawler", true );
        //http://www.csto.com/project/list?page=16
        //http://www.csto.com/project/list
        crawler.addSeed( "http://www.csto.com/project/list" );
        crawler.setThreads( 3 );
        crawler.start( 3 );
    }
}