package com.example.sbcamelkafka;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SchedulerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .enableCORS(true)
                .component("jetty")
                .host("0.0.0.0")
                .port(8084);

        rest("/api")
                .get("/rerunFlightReportGeneration")
                .to("direct:rerunFlightReportGeneration");

        from("direct:rerunFlightReportGeneration")
                .routeId("rerunFlightReportGeneration")
                .log("Rerunning an Adhoc request for report Generation.")
                .to("direct:startReportGeneration");

        from("quartz://timerName?cron={{expression}}")
                .routeId("freshFlightReportGeneration")
                .log("Starting a frest report generation .")
                .to("direct:startReportGeneration");

        from("direct:startReportGeneration")
                .routeId("startReportGeneration")
                .log("Fetch Information from  Navitaire .")
                .setBody(simple("{hurray}"))
                .log("Sending data to Kafka Topic")
                .to("kafka:{{topic}}?brokers={{broker}}");

    }
}
