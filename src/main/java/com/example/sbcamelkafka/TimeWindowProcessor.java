package com.example.sbcamelkafka;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.quartz.QuartzMessage;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class TimeWindowProcessor implements Processor {


        @Override
        public void process(Exchange exchange) throws Exception {

            QuartzMessage message = exchange.getIn(QuartzMessage.class);
            JobExecutionContext jec = message.getJobExecutionContext();

            Date sft = jec.getScheduledFireTime();
            Date after6hours = timeAfter(6 , sft);
            Date after10hours = timeAfter(10 , sft);
            Date after14hours = timeAfter(14 , sft);

            exchange.getIn().setHeader("windowStart" , after6hours);
            exchange.getIn().setHeader("windowEnd" , after10hours);
            exchange.getIn().setHeader("backupWindowStart" , after10hours);
            exchange.getIn().setHeader("backupWindowEnd" , after14hours);

        }

        Date timeAfter(int hours , Date currentTime){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            return  calendar.getTime();
        }

}
