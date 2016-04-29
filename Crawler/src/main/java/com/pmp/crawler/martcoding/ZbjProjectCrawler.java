package com.pmp.crawler.martcoding;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by lyz on 4/26/16.
 */
public class ZbjProjectCrawler extends BreadthCrawler {
    private String path;
    private String cookies = "_uq=a4986e302848551ba7d415a9b80d6424; defaultShowUser=1; defaultShowService=1; appId=pgyu6atqyxixu; 422509d9fea01e3a14885453=1461600000000; userkey=2pqgub61tZWo%2BR8TfHx6gsbYshaFcKNsNhQPOsDrKR9ov4GiddHauUwdHvz6Vb58QdAZ4Gupp7GVk33ElPjKVOcXJTyMojOlhj6k5yStM942OPyuVT%2FnHgiO4LmRBTO%2B1LlSmv7uKKNaER0DKoOwgdVVmfyYEO406D6Oo630l4vOOLrE6PHWSLTKislWOj305izPbaFsOvUARq52d%2Fr%2FPHIvg%2FgvcWz0xcUX%2FoNoiKFYY6wmh7mYdMJkdeyx6qMHe0EkSErrQhHM; userid=14889666; nickname=e_lyxco1sz8g; brandname=e_lyxco1sz8g; viewed_task=14885453%3A7333229%2C7329251%2C7324852%2C7325092%2C7336152%3B14889666%3A4234060%2C7348056%2C7321281%2C7137467%2C7173133; PHPSESSID=i30diftd0ndl5c7g4cstafict1; _analysis=1eeai7gbAfudAselhBEjeC6%2BHJZSLxX6vIpi5rlIBBqRq%2BzUYJde%2BBVFRF0xvJbeBYFfEetAtaxxPfoxFKGu3g; fvtime=cd119RRV1XouGUM0Ilm0zR0MGVaujqg7bgV%2Fw2WbsP%2BT70NGSpmY; uniqid=d574d823fa1913ed210d92917b5db5b2; _ga=GA1.2.1269999331.1459934861; _gat=1; __utmt=1; __utma=168466538.1269999331.1459934861.1461891827.1461893906.27; __utmb=168466538.1.10.1461893906; __utmc=168466538; __utmz=168466538.1461833690.25.6.utmcsr=task.zbj.com|utmccn=(referral)|utmcmd=referral|utmcct=/t-wzkf/; searchArrayRandUsed=1; navi21d95d78=120.92.13.73:8112,d04ef2282a9bfa9014889666; d04ef2282a9bfa9014889666=1461859200000";

    public ZbjProjectCrawler(String crawlPath, boolean autoParse) {
        super( crawlPath, autoParse );
        path = "/home/lyz/temp/zbj-project/" + crawlPath + "/";
        Optional.ofNullable( Paths.get( path ) ).filter( Files::notExists ).ifPresent( x -> {
            try {
                Files.createDirectories( x );
            } catch (IOException e) {
                throw new UncheckedIOException( e );
            }
        } );
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
            if (page.select( "div > div > div > div.msg" ).size() > 0) return;
            //#ed-tit
            Elements elements = page.select( "#ed-tit" );
            page.select( "#j-content > div > div.taskmode-block.clearfix" ).stream().findFirst().ifPresent( elements::add );
            page.select( "#work-more" ).stream().findFirst().ifPresent( elements::add );
            page.select( "#j-content > div > div.user-add.task-extend-item" ).stream().findFirst().ifPresent( elements::add );
            page.select( "#j-receiptcon" ).stream().findFirst().ifPresent( elements::add );
            //elements.append( page.select( "#j-content > div > div.taskmode-block.clearfix" ).html() );
            //elements.append( page.select( "#work-more" ).html() );

//            if (page.select( "#j-content > div > div.user-add.task-extend-item" ).size() > 0) {
//                elements.append( page.select( "#j-content > div > div.user-add.task-extend-item" ).html() );
//            }
            //elements.append( page.select( "#j-receiptcon" ).html() );
            if(elements.size() ==0 || elements.html().isEmpty())
            {
                return;
            }
            String urlid = page.getUrl().replace( "https://", "" ).replace( "http://", "" ).replaceAll( "/+", "_" );
            Path filepath = Paths.get( path + urlid + ".html" );


//            elements.stream().forEach( x -> {
            try {
                BufferedWriter writer = Files.newBufferedWriter( filepath );
                writer.write( "<head>\n" +
                        "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                        "</head>" );
                writer.newLine();
                writer.append( elements.html() );
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
        crawler = new ZbjProjectCrawler( "wzkf", true );
        //http://task.zbj.com/t-wzkf/s5p4.html
        crawler.addSeed( "http://task.zbj.com/7044194/" );
        crawler.setThreads( 5 );
        crawler.start( 2 );
    }

    public static void main(String[] args) throws Exception {
        ZbjProjectCrawler crawler = null;
        String urlrjkf = "http://task.zbj.com/t-rjkf/";
        String urlui = "http://task.zbj.com/t-uisheji/";
        String urlydkf = "http://task.zbj.com/t-ydyykf/";
        String urlwzkf = "http://task.zbj.com/t-wzkf/";
//        crawler = new ZbjProjectCrawler( "rjkf", true );
        //http://task.zbj.com/t-wzkf/s5p4.html
//        fetchSeeds( urlrjkf, crawler );
//        crawler.setThreads( 5 );
//        crawler.start( 2 );
//        crawler = new ZbjProjectCrawler( "ui", true );
//        fetchSeeds( urlui, crawler );
//        crawler.setThreads( 10 );
//        crawler.start( 2 );
//        crawler = new ZbjProjectCrawler( "ydkf", true );
//        fetchSeeds( urlydkf, crawler );
//        crawler.setThreads( 10 );
//        crawler.start( 2 );
        crawler = new ZbjProjectCrawler( "wzkf", true );
        fetchSeeds( urlwzkf, crawler );
        crawler.setThreads( 5 );
        crawler.start( 2 );

    }
}