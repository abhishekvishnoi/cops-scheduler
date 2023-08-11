package com.example.sbcamelkafka;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CopsSchedulerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        /**
         * Rest configuration for the Rest API .
         */
        restConfiguration()
                .enableCORS(true)
                .component("jetty")
                .host("0.0.0.0")
                .port(8084);

        /**
         * A Rest Endpoint Route to Trigger the COPS .
         * The rest endpoint is for a fallback mechanism
         * which can be used to trigger report for previously
         * failed scenarios . The Rest endpoint will be
         * triggered form a web Application.
         */
        rest("/api")
                .get("/rerunFlightReportGeneration")
                .to("direct:rerunFlightReportGeneration");

        from("direct:rerunFlightReportGeneration")
                .routeId("rerunFlightReportGeneration")
                .log("Rerunning an Adhoc request for report Generation.")
                .to("direct:startReportGeneration");

        /**
         * A Scheduler Route to Trigger the COPS .
         * The scheduler will eventually be removed after Indigo Starts
         * using the Kubernetes pod Scheduler .
         */
        from("quartz://timerName?cron={{expression}}")
                .routeId("freshFlightReportGeneration")
                .log("Starting a fresh report generation .")
                .to("direct:startReportGeneration");

        /**
         * A route for report generation , both Rest Request
         * and the Scheduler converges to this route
         */
        from("direct:startReportGeneration")
                .routeId("startReportGeneration")
                .log("Fetch Information from  Navitaire .")
                .to("http://{{flight.data.api.host}}:{{flight.data.api.port}}/{{flight.data.api.path}}?bridgeEndpoint=true")
                .log("Sending data to Kafka Topic")
                .to("kafka:{{topic}}?brokers={{broker}}");

    }
}
