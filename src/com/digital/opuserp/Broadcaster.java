package com.digital.opuserp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster {
	
	public enum Type_Func {
        RECEIVED_BROADCAST("received_broadcast"), ALERT_MAINTENANCE("alert_maintenance");

        private String style;

        Type_Func(String style) {
            this.style = style;
        }

        /**
         * @since 7.2
         *
         * @return the style name for this notification type.
         */
        public String getStyle() {
            return style;
        }
    }
	
	private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<BroadcastListener>();

    public static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static void broadcast(final String message,BroadcastListener listener_send, Type_Func type_action) {
        for (BroadcastListener listener : listeners) {
        	if(type_action.equals(Type_Func.RECEIVED_BROADCAST)){
        		listener.receiveBroadcast(message, listener_send);
        	}
        	
        	if(type_action.equals(Type_Func.ALERT_MAINTENANCE)){
        		listener.avisoManutencao(listener_send);
        	}
        }
    }

    public interface BroadcastListener {
        public void receiveBroadcast(String message, BroadcastListener listener);
        public void avisoManutencao(BroadcastListener listener);
    }
}
