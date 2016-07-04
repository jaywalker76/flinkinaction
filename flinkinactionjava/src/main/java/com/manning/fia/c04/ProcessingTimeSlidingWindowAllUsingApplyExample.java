package com.manning.fia.c04;

import com.manning.fia.model.media.NewsFeed;
import com.manning.fia.transformations.media.NewsFeedMapper3;
import com.manning.fia.transformations.media.NewsFeedMapper4;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.shaded.com.google.common.base.Throwables;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * Created by hari on 6/26/16.
 */
public class ProcessingTimeSlidingWindowAllUsingApplyExample {
    public void executeJob() throws Exception {

        StreamExecutionEnvironment execEnv = StreamExecutionEnvironment
                .createLocalEnvironment(1);
        execEnv.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        DataStream<String> socketStream = execEnv.socketTextStream("localhost",
                9000);
        DataStream<Tuple5<Long, String, String, String, String>> selectDS = socketStream
                .map(new NewsFeedMapper3());
        AllWindowedStream<Tuple5<Long, String, String, String, String>, TimeWindow> ws1=
                selectDS.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(2)));
        DataStream<Tuple4<Long, Long, List<Long>,  Long>> result1 = ws1.apply(new AllWindowApplyFunction());

        result1.print();
        execEnv.execute("All Time Window Apply");
    }

    public static void main(String[] args) throws Exception {
        new NewsFeedSocket("/media/pipe/newsfeed", 1000,9000).start();
        ProcessingTimeSlidingWindowAllUsingApplyExample window = new ProcessingTimeSlidingWindowAllUsingApplyExample();
        window.executeJob();

    }
}
