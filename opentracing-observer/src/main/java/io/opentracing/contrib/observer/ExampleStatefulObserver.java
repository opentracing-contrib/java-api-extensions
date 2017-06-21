package io.opentracing.contrib.observer;

import java.util.HashMap;
import java.util.Map;

public class ExampleStatefulObserver implements TracerObserver {

    @Override
    public SpanObserver onStart(SpanData spanData, long startMicros, String operationName) {
        return new StatefulSpanObserver(spanData, startMicros, operationName);
    }

    public static class SpanState {
        private final long startMicros;
        private String operationName;

        public SpanState(SpanData spanData, long startMicros, String operationName) {
            this.startMicros = startMicros;
            this.operationName = operationName;
        }
    }

    public static class StatefulSpanObserver implements SpanObserver {
        
        private Map<Object,SpanState> spanState = new HashMap<Object,SpanState>();

        public StatefulSpanObserver(SpanData spanData, long startMicros, String operationName) {
            SpanState state = new SpanState(spanData, startMicros, operationName);
            spanState.put(spanData.getSpanId(), state);
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
            SpanState state = spanState.remove(spanData.getSpanId());
        }
    }
}
