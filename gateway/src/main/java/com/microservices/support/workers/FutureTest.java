//package com.microservices.support.workers;
//
//import akka.actor.ActorSystem;
//import akka.dispatch.Futures;
//import akka.dispatch.OnFailure;
//import akka.dispatch.OnSuccess;
//import scala.concurrent.Future;
//import scala.util.Random;
//
//public class FutureTest {
//    public static void main(String[] args) throws Exception {
//        final ActorSystem system = ActorSystem.create("helloakka");
//
//        Future<String> f = Futures.future(() -> {
//            Thread.sleep(1000);
//            if (new Random(System.currentTimeMillis()).nextBoolean()){
//                return "Hello"+"World!";
//            }else {
//                throw new IllegalArgumentException("参数错误");
//            }
//        },system.dispatcher());
//        f.onSuccess(new PrintResult<String>(),system.dispatcher());
//        f.onFailure(new FailureResult(),system.dispatcher());
//        System.out.println("这个地方是外面");
//        system.terminate();
//    }
//    public final static class PrintResult<T> extends OnSuccess<T> {
//        @Override public final void onSuccess(T t) {
//            System.out.println(t);
//        }
//    }
//
//    public final static class FailureResult extends OnFailure {
//        @Override
//        public void onFailure(Throwable failure) throws Throwable {
//            System.out.println("进入错误的处理");
//            failure.printStackTrace();
//        }
//    }
//}
//
