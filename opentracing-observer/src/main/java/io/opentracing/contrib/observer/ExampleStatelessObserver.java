package io.opentracing.contrib.observer;

import java.util.Map;

public class ExampleStatelessObserver implements TracerObserver, SpanObserver {

    @Override
    public SpanObserver onStart(SpanData spanData, long startMicros, String operationName) {
        return this;
    }

    @Override
    public void onSetOperationName(SpanData spanData, String operationName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSetTag(SpanData spanData, String name, Object value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSetBaggageItem(SpanData spanData, String key, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLog(SpanData spanData, long timestampMicroseconds, Map<String, ?> fields) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLog(SpanData spanData, long timestampMicroseconds, String event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFinish(SpanData spanData, long finishMicros) {
        // TODO Auto-generated method stub
        
    }

}
