package com.vodafone.vfcloudcontactsspeedtester;

import java.net.MalformedURLException;
import java.util.Vector;

import android.util.Log;
import android.view.View;

import com.vodafone.lib.sec.Event;
import com.vodafone.lib.sec.SecLib;

public class SmapiEvent {

	public final int logClientInternalEvents(String typeOfevent,
			String useCase, boolean NewUseCase, String smapi_event,
			String smapi_context, String smapi_tag, Vector<String> payload)
			throws MalformedURLException {
		int i;
		String tmpPayload = "";
		if (typeOfevent == "client") {
			final Event clientEvent = Event.clientEvent(smapi_event,
					smapi_context, smapi_tag);

			for (i = 0; i < payload.size(); i++) {
				// Log.v("ForSMAPI", Integer.toString(payload.size()) + " - " +
				// Integer.toString(i) +" - " + payload.elementAt(i) );
				if (payload.elementAt(i) != null) {
					tmpPayload = tmpPayload + "\n" + payload.elementAt(i);
					// clientEvent.addPayload("Payload key" +
					// Integer.toString(i), payload.elementAt(i));
					// Log.v("ForSMAPI",payload.elementAt(i));
				}

			}
			clientEvent.addPayload("event-tags", tmpPayload);
			// Log.v("ForSMAPI",clientEvent.getTransactionId() + " " );

			clientEvent.setUseCase(useCase, NewUseCase);

			SecLib.logEvent(clientEvent);

		}

		else if (typeOfevent == "internal") {
			final Event internalEvent = Event.internalEvent(
					"Test internal event",
					// Exception to send as a stack trace (optional).
					null);
			internalEvent.addPayload("Custom Payload 1", "Custom Payload 1");
			internalEvent.addPayload("Custom Payload 2", "Custom Payload 2");
			internalEvent.setUseCase("demo", false);
			SecLib.logEvent(internalEvent);
		}
		return 0;
	}

	public final String logRequestEvents(String typeOfevent,
			String description, String traceDestination, String protocol,
			String url, String method, String body)
			throws MalformedURLException {
		
		body="";
		
		if (typeOfevent == "request") {
			final Event requestEvent = Event.requestEvent(description,traceDestination, protocol, url, method, body);
			requestEvent.addRequestHeader("Custom Header", "Custom Value");
			requestEvent.addResponseHeader("Custom Header", "Custom Value");
			requestEvent.setResponse(200, 123L);
			//String transactionIDforHeader= requestEvent.getTransactionId();
			requestEvent.addPayload("Custom Payload 1", "Custom Payload 1");
			requestEvent.addPayload("Custom Payload 2", "Custom Payload 2");
			requestEvent.setUseCase("demo", false);
			SecLib.logEvent(requestEvent);
			return requestEvent.getTransactionId();
		}
		return null;
	}

	/**
	 * Upload all cached logs to the back end API immediately.
	 * 
	 * Note: This is optional, as SecLib has its own service for managing the
	 * uploading of events in batches.
	 * 
	 * @param view
	 *            Selected Button.
	 */
	public final void flushLogs(final View view) {
		SecLib.flush();
	}
}