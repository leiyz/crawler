package com.pmp.crawler.martcoding;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by lyz on 4/26/16.
 */
public class ZbjProjectCrawler extends BreadthCrawler {
    private String path;
    private String cookies = "_uq=a4986e302848551ba7d415a9b80d6424; defaultShowUser=1; defaultShowService=1; appId=pgyu6atqyxixu; 422509d9fea01e3a14885453=1461600000000; uniqid=900aceb1e30f61ae166ef8141ea2535c; PHPSESSID=fd7brd0p48h585pdkjclbnllg5; _ga=GA1.2.1269999331.1459934861; _uv=14; viewed_task=14885453%3A7333229%2C7329251%2C7324852%2C7325092%2C7336152%3B14889666%3A7137467%2C7173133%2C7321281%2C6316767%2C5967968; navie9e2474f=120.92.13.73:8112,d04ef2282a9bfa9014889666; d04ef2282a9bfa9014889666=1461772800000; _gat=1; __utmt=1; rongIMMainPage=rluz2bl8vwgeqaor; webimMainPage=98jnc3n8zuns714i; _ga=GA1.3.1269999331.1459934861; __utma=168466538.1269999331.1459934861.1461814187.1461824468.24; __utmb=168466538.18.10.1461824468; __utmc=168466538; __utmz=168466538.1461570677.16.5.utmcsr=search.zbj.com|utmccn=(referral)|utmcmd=referral|utmcct=/p/; userkey=2pqgub61tZWo%2BR8TfHx6gsbYshaFcKNsNhQPOsDrKR9ov4GiddHauUwdHvz6Vb58QdAZ4Gupp7GVk33ElPjKVOcXJTyMojOlhj6k5yStM942OPyuVT%2FnHgiO4LmRBTO%2B1LlSmv7uKKNaER0DKoOwgdVVmfyYEO406D6Oo630l4vOOLrE6PHWSLTKislWOj305izPbaFsOvUARq52d%2Fr%2FPHIvg%2FgvcWz0xcUX%2FoNoiKFYY6wmh7mYdMJkdeyx6qMHe0EkSErrQhHM; userid=14889666; nickname=e_lyxco1sz8g; brandname=e_lyxco1sz8g";

    public ZbjProjectCrawler(String crawlPath, boolean autoParse) {
        super( crawlPath, autoParse );
        path = "/home/lyz/temp/zbj-project/" + crawlPath + "/";
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest( crawlDatum );
//        request.setCookie( com.pmp.crawler.martcoding.ZjbLoginCN.getZbjCookie( "lyz88119@126.com", "engine" ) );
        request.setCookie( cookies );
        return request.getResponse();
    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        if (page.matchUrl( "http://task.zbj.com/t-.*" )) {
            Links pageLinks = page.getLinks( "tr>td>p>a" ).filterByRegex( "http://task.zbj.com/[^/]+/" );
            System.out.println( "Zbj URL>>>>>>>>>" + page.getUrl() );
            System.out.println( "Zbj task URL>>>>>>>>>" + pageLinks.size() );
            crawlDatums.add( pageLinks );
            return;
        }
        if (page.matchUrl( "http://task.zbj.com/[^/]+/" )) {
//            HttpResponse response = page.getResponse();
//            Elements elements = page.select( "#j-content > div > div.taskmode-block.clearfix" );
//            elements.append( page.select( "#j-content > div > div.taskmode-block.clearfix" ).html() );
//            elements.append( page.select( "#work-more" ).html() );
//            elements.append( page.select( "#j-content > div > div.nts.mt15.clearfix.task-extend-item" ).html() );
            if (page.select( "div > div > div > div.msg" ).size() > 0) return;
            String urlid = page.getUrl().replace( "https://", "" ).replace( "http://", "" ).replaceAll( "/+", "_" );
            Path filepath = Paths.get( path + urlid + ".html" );
//            elements.stream().forEach( x -> {
            try {
                BufferedWriter writer = Files.newBufferedWriter( filepath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW );
                writer.write( "<head>\n" +
                        "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                        "</head>" );
                writer.newLine();
                writer.append( page.getHtml() );
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            } );
        }
    }

    private static void fetchSeeds(String url, BreadthCrawler crawler) {
        for (int i = 1; i <= 100; i++) {
            if (i == 1) {
                crawler.addSeed( url );
                continue;
            }
            String urlAppend = String.format( "s5p%s.html", i );
            crawler.addSeed( url + urlAppend );
        }
    }

    public static void main1(String[] args) throws Exception {
        ZbjProjectCrawler crawler = null;
        crawler = new ZbjProjectCrawler( "rjkfCrawler", true );
        //http://task.zbj.com/t-wzkf/s5p4.html
        crawler.addSeed( "http://task.zbj.com/7137467/" );
        crawler.setThreads( 5 );
        crawler.start( 2 );
    }

    public static void main(String[] args) throws Exception {
        ZbjProjectCrawler crawler = null;
        String urlrjkf = "http://task.zbj.com/t-rjkf/";
        String urlui = "http://task.zbj.com/t-uisheji/";
        String urlydkf = "http://task.zbj.com/t-ydyykf/";
        String urlwzkf = "http://task.zbj.com/t-wzkf/";
        crawler = new ZbjProjectCrawler( "rjkf", true );
        //http://task.zbj.com/t-wzkf/s5p4.html
        fetchSeeds( urlrjkf, crawler );
        crawler.setThreads( 10 );
        crawler.start( 2 );
        crawler = new ZbjProjectCrawler( "ui", true );
        fetchSeeds( urlui, crawler );
        crawler.setThreads( 10 );
        crawler.start( 2 );
        crawler = new ZbjProjectCrawler( "ydkf", true );
        fetchSeeds( urlydkf, crawler );
        crawler.setThreads( 10 );
        crawler.start( 2 );
        crawler = new ZbjProjectCrawler( "wzkf", true );
        fetchSeeds( urlwzkf, crawler );
        crawler.setThreads( 10 );
        crawler.start( 2 );

    }
}