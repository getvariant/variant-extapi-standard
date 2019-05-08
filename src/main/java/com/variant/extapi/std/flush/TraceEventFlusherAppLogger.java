package com.variant.extapi.std.flush;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.variant.core.schema.Variation.Experience;
import com.variant.server.api.FlushableTraceEvent;
import com.variant.server.api.TraceEventFlusher;

/**
 * An implementation of {@link TraceEventFlusher}, which appends trace events
 * to the application logger. This is the default, out of the box event flusher,
 * which is completely independent of the operational environment. Probably not for production use, 
 * but is good enough for the demo application.
 * <p>
 * Configuration. You may use the {@code variant.event.flusher.class.init} configuration property
 * to pass configuration details to this object.
 * <ul>
 * <li><code>level</code> - specifies the logging level to be used. Defaults to 'INFO'.<br/>
 * </ul>
 * Example:<br/>
 * <code>variant.event.flusher.class.init = {init="INFO"}</code>
 * 
 * 
 * @since 0.5
 */
public class TraceEventFlusherAppLogger implements TraceEventFlusher {
	
	private static final Logger LOG = LoggerFactory.getLogger(TraceEventFlusherAppLogger.class);

	private String level = null;

	public TraceEventFlusherAppLogger(Config config) {
		level = config.getString("level");
		
		if (level == null) {
			level = "INFO";
			LOG.info("No level specified. Will use INFO.");
		}

	}

	@Override
	public void flush(Collection<FlushableTraceEvent> events)
			throws Exception {

		for (FlushableTraceEvent event: events) {
			StringBuilder msg = new StringBuilder();
			msg.append("{")
    			.append("event_id:'").append(event.getId()).append("', ")
				.append("event_name:'").append(event.getName()).append("', ")
				.append("created_on:'").append(DateTimeFormatter.ISO_INSTANT.format(event.getTimestamp())).append("', ")
				.append("session_id:'").append(event.getSessionId()).append("'");

			if (!event.getLiveExperiences().isEmpty()) {
			   msg.append(", event_experiences:[");
			   boolean first = true;
   			for (Experience e: event.getLiveExperiences()) {
   			   if (first) first = false;
   			   else msg.append(", ");
   			   msg.append("{")
               .append("test_name:'").append(e.getVariation().getName()).append("', ")
               .append("experience_name:'").append(e.getName()).append("', ")
               .append("is_control:").append(e.isControl())
               .append("}");
            }
   			msg.append("]");
			}			
			
	      if (!event.getAttributes().isEmpty()) {
            msg.append(", event_attributes:[");
            boolean first = true;
	         for (Map.Entry<String, String> param: event.getAttributes().entrySet()) {
	            if (first) first = false;
	            else msg.append(", ");
	            msg.append("{")
	            .append("key:'").append(param.getKey()).append("', ")
	            .append("value:'").append(param.getValue()).append("'")
	            .append("}");
	         }
	         msg.append("]");
	      }     

	      msg.append("}");

	      switch (level.toUpperCase()) {
	      case "TRACE": LOG.trace(msg.toString()); break;
	      case "DEBUG": LOG.debug(msg.toString()); break;
	      case "INFO": LOG.info(msg.toString()); break;
	      case "ERROR": LOG.error(msg.toString()); break;
	      }
		}
								
	}

}
