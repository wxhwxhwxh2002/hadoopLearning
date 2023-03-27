package org.example;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.BasicConfigurator;
/**
 * 功能描述
 *
 * @author: WenXiaohan_32005231_ZUCC
 * @date: 2023年03月27日 22:59
 */

// https://blog.csdn.net/a18307096730/article/details/121343243
// https://blog.csdn.net/qq_35170267/article/details/82860991
// https://blog.csdn.net/qq_44491709/article/details/107881124
// https://blog.csdn.net/qq_41983842/article/details/96478342
public class WordCount {
    public static class Map extends Mapper<Object,Text,Text,IntWritable>{
        private static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        public void map(Object key,Text value,Context context) throws
                IOException,InterruptedException{
            StringTokenizer st = new StringTokenizer(value.toString());
            while(st.hasMoreTokens()){
                word.set(st.nextToken());
                context.write(word, one);
            }
        }
    }
    public static class Reduce extends Reducer<Text,IntWritable,Text,IntWritable>{
        private static IntWritable result = new IntWritable();
        public void reduce(Text key,Iterable<IntWritable> values,Context context)
                throws IOException,InterruptedException{
            int sum = 0;
            for(IntWritable val:values){
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception{
        BasicConfigurator.configure(); //自动快速地使用缺省Log4j环境。
        // 填入自己的环境变量中配置的value值
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        Configuration conf = new Configuration();
        String[] otherArgs = new
                GenericOptionsParser(conf,args).getRemainingArgs();
        if(otherArgs.length != 2){
            System.err.println("Usage WordCount <int> <out>");
            System.exit(2);
        }
        Job job = new Job(conf,"word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}